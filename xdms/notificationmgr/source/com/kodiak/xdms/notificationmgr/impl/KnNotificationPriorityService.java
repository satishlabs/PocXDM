/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.notificationmgr.impl;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;

import com.kodiak.common.commdto.common.KnNotificationParamDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.beans.KnNotifPriorityConfigDTO;
import com.kodiak.xdms.notificationmgr.resources.KnXcapNotifyConstants;
import com.kodiak.xdms.server.common.configuration.KnConfigurationException;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManager;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.resources.KnCacheKeys;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton service that resolves the notification suppression priority tier for a given
 * operation code (OPS_CODE).
 *
 * Resolution order:
 *  1. GG distributed cache (key: KnCacheKeys.XCAP_NOTIFY_PRIORITY_CONFIG)
 *  2. TimesTen DB table DG.NOTIFICATION_PRIORITY_CONFIG (on cache miss — loaded once,
 *     then stored back into GG for subsequent lookups)
 *
 * Priority tiers (NOTIFICATION_PRIORITY_CONFIG.PRIORITY_LEVEL):
 *   10 = CRITICAL — always dispatched immediately (XCAP_PENDING_NOTIFYQ.PRIORITY = 0)
 *    5 = HIGH     — standard BAU (XCAP_PENDING_NOTIFYQ.PRIORITY = 5)
 *    0 = LOW      — suppressed; no DB write, changes surface via client 30-min refresh
 */
public class KnNotificationPriorityService {

    private static final KnLogger knLogger = KnLogger.getLogger(KnNotificationPriorityService.class);

    private static final String SELECT_NOTIFICATION_PRIORITY_CONFIG =
            "SELECT OPS_ID, OPS_CODE, PRIORITY_LEVEL, PRIORITY_VALUE, NOTIFY_DESC, LAST_MODIFIED, OPS_NAME " +
                    "FROM DG.XCAP_NOTIFICATION_PRIORITY";

    private static volatile KnNotificationPriorityService instance;

    private KnNotificationPriorityService() {
    }

    public static KnNotificationPriorityService getInstance() {
        if (instance == null) {
            synchronized (KnNotificationPriorityService.class) {
                if (instance == null) {
                    instance = new KnNotificationPriorityService();
                }
            }
        }
        return instance;
    }

    /**
     * Returns the full priority configuration DTO for the given operation code.
     * Returns null if no configuration exists for the code (callers should apply default BAU logic).
     *
     * @param opsCode operation code to look up
     * @return KnNotifPriorityConfigDTO or null if not found
     */
    public KnNotifPriorityConfigDTO getPriorityConfig(int opsCode) {
        final String methodName = "getPriorityConfig()";
        knLogger.info(methodName, "ENTRY: looking up priority config for opsCode=", opsCode);

        try {
            Map<Integer, KnNotifPriorityConfigDTO> configMap = getCachedConfigMap();

            if (configMap == null || configMap.isEmpty()) {
                knLogger.info(methodName, "Cache empty — loading priority config from TimesTen DB");
                configMap = loadFromDB();
                if (configMap != null && !configMap.isEmpty()) {
                    storeCachedConfigMap(configMap);
                    knLogger.info(methodName, "Priority config loaded from DB and stored in GG cache, size=", configMap.size());
                } else {
                    knLogger.error(methodName, "DB returned empty priority config — returning null for opsCode=", opsCode);
                    return null;
                }
            }

            KnNotifPriorityConfigDTO dto = configMap.get(opsCode);
            if (dto != null) {
                knLogger.info(methodName, "EXIT: found config for opsCode=", opsCode,
                        " priorityLevel=", dto.getPriorityLevel(), " priorityValue=", dto.getPriorityValue());
            } else {
                knLogger.info(methodName, "EXIT: no config found for opsCode=", opsCode, " — caller will apply BAU logic");
            }
            return dto;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception resolving priority config for opsCode=", opsCode, " : ", e);
            return null;
        }
    }

    /**
     * Invalidates the GG cache entry for the notification priority config.
     * Called from OPSCLI or KnGenInfoUtil when the config table is updated.
     */
    public void invalidateCache() {
        final String methodName = "invalidateCache()";
        try {
            ICacheManager cacheManager = KnConfigurationsManager.getInstance().getCacheManager();
            cacheManager.put(KnCacheKeys.XCAP_NOTIFY_PRIORITY_CONFIG, null);
            knLogger.info(methodName, "XCAP notification priority config cache invalidated successfully");
        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed to invalidate cache: ", e);
        }
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------
    private Map<Integer, KnNotifPriorityConfigDTO> getCachedConfigMap() {
        final String methodName = "getCachedConfigMap()";
        try {
            ICacheManager cacheManager = KnConfigurationsManager.getInstance().getCacheManager();
            Object cached = cacheManager.get(KnCacheKeys.XCAP_NOTIFY_PRIORITY_CONFIG);
            knLogger.debug(methodName, "Cache get result for key=", KnCacheKeys.XCAP_NOTIFY_PRIORITY_CONFIG,
                    " : ", (cached != null ? "HIT" : "MISS"));
            return (Map<Integer, KnNotifPriorityConfigDTO>) cached;
        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Cache read failed: ", e);
            return null;
        }
    }

    private void storeCachedConfigMap(Map<Integer, KnNotifPriorityConfigDTO> configMap) {
        final String methodName = "storeCachedConfigMap()";
        try {
            ICacheManager cacheManager = KnConfigurationsManager.getInstance().getCacheManager();
            cacheManager.put(KnCacheKeys.XCAP_NOTIFY_PRIORITY_CONFIG, configMap);
            knLogger.debug(methodName, "Config map stored in cache, size=", configMap.size());
        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Cache write failed: ", e);
        }
    }

    // -----------------------------------------------------------------------
    // DB loader — reads DG.NOTIFICATION_PRIORITY_CONFIG from TimesTen on cache miss
    // -----------------------------------------------------------------------

    /**
     * Loads notification priority configuration from TimesTen
     * (DG.NOTIFICATION_PRIORITY_CONFIG table) and returns it keyed by OPS_CODE.
     * Called once on cache miss; result is stored back into GG cache by the caller.
     */
    private Map<Integer, KnNotifPriorityConfigDTO> loadFromDB() {
        final String methodName = "loadFromDB()";
        knLogger.info(methodName, "ENTRY: loading notification priority config from DB");
        Map<Integer, KnNotifPriorityConfigDTO> configMap = new HashMap<>();
        KnPersisterTxn persisterTxn = null;
        Connection connection = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            connection = persisterTxn.getDBConnection(localPttId, true);
            pStmt = connection.prepareStatement(SELECT_NOTIFICATION_PRIORITY_CONFIG);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnNotifPriorityConfigDTO dto = new KnNotifPriorityConfigDTO();
                dto.setOpsId(rs.getInt("OPS_ID"));
                dto.setOpsCode(rs.getInt("OPS_CODE"));
                dto.setPriorityLevel(rs.getString("PRIORITY_LEVEL"));
                dto.setPriorityValue(rs.getInt("PRIORITY_VALUE"));
                dto.setNotifyDesc(rs.getString("NOTIFY_DESC"));
                dto.setLastModified(rs.getString("LAST_MODIFIED"));
                dto.setOpsName(rs.getString("OPS_NAME"));
                configMap.put(dto.getOpsCode(), dto);
            }
            persisterTxn.save();
            knLogger.info(methodName, "EXIT: loaded ", configMap.size(), " entries from DB");
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception loading priority config: ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception loading priority config: ", e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception loading priority config: ", e);
        } finally {
            closeQuietly(rs, pStmt);
        }
        return configMap;
    }

    private void closeQuietly(ResultSet rs, PreparedStatement pStmt) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
        if (pStmt != null) {
            try { pStmt.close(); } catch (SQLException ignored) {}
        }
    }
}
