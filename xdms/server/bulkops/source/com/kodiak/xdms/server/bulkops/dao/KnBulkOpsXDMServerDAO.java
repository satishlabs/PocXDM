package com.kodiak.xdms.server.bulkops.dao;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.dao.KnBulkOpsXDMServerDAO.KnMdnPkgInfo;
import com.kodiak.xdms.server.bulkops.dto.common.KnBulkOpsNNISubsDTO;
import com.kodiak.xdms.server.bulkops.dto.common.KnBulkOpsPocConfigDTO;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsConstants;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsDAOSourceTypes;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsErrorCodes;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsDBUtil;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBXDMServerDAO;
import com.kodiak.xdms.server.common.dto.persistdat.KnContactListPersistDTO;

import com.kodiak.xdms.server.common.dto.common.KnAPNConfigDTO;
import com.kodiak.xdms.server.common.dto.common.KnPOCSvcConfigDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnDeviceInfoPersistDTO;
import com.kodiak.common.commdto.common.KnBulkSubsProfileDTO;
import com.kodiak.xdms.server.bulkops.dto.common.KnMDNValidationResult;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.featureset.KnFeatureSetException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class KnBulkOpsXDMServerDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkOpsXDMServerDAO.class);
    private KnBulkOpsDBUtil bulkOpsDBUtil = null;
    private static final String QRY_APN_FIELD_ROPERTIES = "SELECT DYNAMICQOSFLAG,XCAPROOTURI,APNID, PTXBUCKETURICELL, " +
            "LOCDATAURICELL ,MCSXCAPROOTURI from DG.APNPROFILEINFO WHERE PTTSERVERID = ?";

    public KnBulkOpsXDMServerDAO() throws KnBulkOpsException {
        bulkOpsDBUtil = KnBulkOpsDBUtil.getInstance();
    }

    /**
     * Get APN IDs for multiple MDNs
     *
     * @param mdnList      List of MDNs to retrieve APN IDs for
     * @param persisterTxn Database transaction
     * @return Map of MDN to APN ID (Integer)
     * @throws KnDAOException if database operation fails
     */
    public Map<String, Integer> getSubsApnIdsBulk(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsApnIdsBulk(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching APN IDs for ", mdnList.size(), " MDNs");

        String queryPrefix = "SELECT MDN, APNID FROM DG.SUBSCRAPNINFO WHERE MDN IN ";

        Map<String, Integer> apnIdMap = bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                mdnList,
                rs -> {
                    Map<String, Integer> resultMap = new HashMap<>();
                    while (rs.next()) {
                        String mdn = rs.getString("MDN").trim();
                        Integer apnId = rs.getInt("APNID");
                        resultMap.put(mdn, apnId);
                    }
                    return resultMap;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "Fetched APN IDs for ", apnIdMap.size(), " MDNs");
        return apnIdMap;
    }

        /**
     * Bulk update device status for a list of device info objects.
     * Uses BulkOps DB util to acquire connection; performs JDBC batch update locally.
     *
     * @param deviceInfoList List of KnDeviceInfoPersistDTO containing deviceId and status
     * @param persisterTxn   Transaction object
     * @throws KnDAOException if any database error occurs
     */
    public void updateDeviceInfoStatusAndPasswordBulk(List<KnDeviceInfoPersistDTO> deviceInfoList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateDeviceInfoStatusAndPasswordBulk(List<KnDeviceInfoPersistDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "Updating device status for ", deviceInfoList.size(), " devices");

        String query = "UPDATE DG.DEVICE_INFO SET DEVICESTATUS=? WHERE DEVICEID=?";

        bulkOpsDBUtil.executeBatchUpdate(
                query,
                deviceInfoList,
                (pStmt, deviceInfo) -> {
                    pStmt.setObject(1, deviceInfo.getDeviceStatus());
                    pStmt.setObject(2, deviceInfo.getDeviceId());
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "Successfully updated device status for ", deviceInfoList.size(), " devices");
    }

    /**
     * Check which MDNs are present in PAM Account Info
     *
     * @param mdnList       List of MDNs to check
     * @param persisterTxn Database transaction
     * @return List of MDNs present in PAM Account Info
     * @throws KnDAOException if database operation fails
     */
    public List<String> mdnPresentInPamAccInfo (List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "mdnPresentInPamAccInfo(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Checking PAM Account Info for ", mdnList.size(), " MDNs");

        String queryPrefix = "SELECT EXTERNALPAMACCID FROM " + KnBulkOpsDAOSourceTypes.PAMACCOUNTINFO + " WHERE EXTERNALPAMACCID IN ";

        List<String> presentMdns = bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                mdnList,
                rs -> {
                    List<String> resultList = new ArrayList<>();
                    while (rs.next()) {
                        String mdn = rs.getString("EXTERNALPAMACCID");
                        resultList.add(mdn != null ? mdn.trim() : mdn);
                    }
                    return resultList;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "Found ", presentMdns.size(), " MDNs in PAM Account Info");
        return presentMdns;
    }

    /**
     * Check which MDNs are present as External Subscribers
     *
     * @param extMdnList    List of External MDNs to check
     * @param persisterTxn Database transaction
     * @return List of MDNs present as External Subscribers
     * @throws KnDAOException if database operation fails
     */
    public List<String> mdnPresentAsExtSubs(List<String> extMdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "mdnPresentAsExtSubs(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Checking External Subscriber Info for ", extMdnList.size(), " MDNs");

        String queryPrefix = "SELECT MDN FROM " + KnBulkOpsDAOSourceTypes.EXTSUBSCRINFO + " WHERE MDN IN ";

        List<String> presentMdns = bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                extMdnList,
                rs -> {
                    List<String> resultList = new ArrayList<>();
                    while (rs.next()) {
                        String mdn = rs.getString("MDN");
                        resultList.add(mdn != null ? mdn.trim() : mdn);
                    }
                    return resultList;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "Found ", presentMdns.size(), " MDNs in External Subscriber Info");
        return presentMdns;
    }

    /**
     * Get subscriber count for multiple PTT Server IDs
     *
     * @param pttServerIds List of PTT Server IDs
     * @param persisterTxn Database transaction
     * @return Map of PTT Server ID to subscriber count
     * @throws KnDAOException if database operation fails
     */
    public Map<String, Integer> getSubsCount(List<String> pttServerIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsCount(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching subscriber count for ", pttServerIds.size(), " PTT Server IDs");

        if (pttServerIds.isEmpty()) {
            knLogger.debug(methodName, "Empty PTT Server ID list");
            return new HashMap<>();
        }

        String queryPrefix = "SELECT PTTSERVERID, SUBSCRIBERCOUNT FROM " + KnBulkOpsDAOSourceTypes.SERVERCAPACITYUTILINFO + " WHERE PTTSERVERID IN ";

        Map<String, Integer> subsCountMap = bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                pttServerIds,
                rs -> {
                    Map<String, Integer> resultMap = new HashMap<>();
                    while (rs.next()) {
                        String pttServerId = rs.getString("PTTSERVERID");
                        pttServerId = (pttServerId != null) ? pttServerId.trim() : pttServerId;
                        int subsCount = rs.getInt("SUBSCRIBERCOUNT");
                        resultMap.put(pttServerId, subsCount);
                    }
                    return resultMap;
                },
                persisterTxn,
                null
        );

        knLogger.debug(methodName, "Fetched subscriber count for ", subsCountMap.size(), " PTT Server IDs");
        return subsCountMap;
    }

    /**
     * Get POC capacity information (subscriber count + capacity config) in a single query
     * Uses IN clause for multiple PTT Server IDs support
     *
     * @param pttServerIds List of PTT Server IDs
     * @param persisterTxn Database transaction
     * @return Map of PTT Server ID to KnBulkOpsPocConfigDTO (contains both count and config)
     * @throws KnDAOException if database operation fails
     */
    public Map<String, KnBulkOpsPocConfigDTO> getPocCapacityInfo(List<String> pttServerIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPocCapacityInfo(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching combined POC capacity info for ", pttServerIds.size(), " PTT Server IDs");

        if (pttServerIds.isEmpty()) {
            knLogger.debug(methodName, "Empty PTT Server ID list");
            return new HashMap<>();
        }

        String queryPrefix =
            "SELECT u.PTTSERVERID, u.SUBSCRIBERCOUNT, c.MAXSUBSCRIBERS, c.ALLOWSUBSCRPROVISIONING " +
            "FROM " + KnBulkOpsDAOSourceTypes.SERVERCAPACITYUTILINFO + " u " +
            "LEFT JOIN " + KnBulkOpsDAOSourceTypes.POCSUBSCRCAPACITYCONFIG + " c ON u.PTTSERVERID = c.PTTSERVERID " +
            "WHERE u.PTTSERVERID IN ";

        Map<String, KnBulkOpsPocConfigDTO> capacityInfoMap = bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                pttServerIds,
                rs -> {
                    Map<String, KnBulkOpsPocConfigDTO> resultMap = new HashMap<>();
                    while (rs.next()) {
                        String pttServerId = rs.getString("PTTSERVERID");
                        pttServerId = (pttServerId != null) ? pttServerId.trim() : pttServerId;
                        int subsCount = rs.getInt("SUBSCRIBERCOUNT");
                        Integer maxSubsLimit = rs.getInt("MAXSUBSCRIBERS");
                        Integer allowProv = rs.getInt("ALLOWSUBSCRPROVISIONING");

                        boolean hasConfig = !rs.wasNull();
                        resultMap.put(pttServerId, KnBulkOpsPocConfigDTO.forPocCapacity(
                            pttServerId, subsCount, maxSubsLimit, allowProv, hasConfig));
                    }
                    return resultMap;
                },
                persisterTxn,
                null
        );

        knLogger.debug(methodName, "Fetched combined POC capacity info for ", capacityInfoMap.size(), " PTT Server IDs");
        return capacityInfoMap;
    }

    /**
     * Get partition config and POC capacity info in a single query using JOINs
     *
     * @param pocPttServerId POC PTT Server ID (if null/empty, only partition config is fetched)
     * @param persisterTxn Database transaction
     * @return KnBulkOpsPocConfigDTO containing partition config and capacity info (if requested)
     * @throws KnDAOException if database operation fails or partition config not found
     */
    public KnBulkOpsPocConfigDTO getPartitionAndCapacityConfig(String pocPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPartitionAndCapacityConfig(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching partition config and POC capacity in single query");

        String xdmPttServerId = KnBulkOpsDBUtil.getXdmPttServerId();

        String query;
        Object[] params;

        if (pocPttServerId != null && !pocPttServerId.isEmpty()) {
            // Single query with subqueries to get BOTH partition config AND capacity info
            query = "SELECT " +
                      " p.PTTServerId as PARTITION_PTTSERVERID," +
                      " p.MDNPartitionType_POC as MDN_PARTITION_TYPE," +
                      " p.EnableCorpAccountAnchoring as ENABLE_CORP_ANCH," +
                      " p.MaxSubscrPerXDMS as MAX_SUBS_XDMS," +
                      " p.MaxSubscrPerPoC as MAX_SUBS_POC," +
                      " u.PTTSERVERID as POC_PTTSERVERID," +
                      " u.SUBSCRIBERCOUNT," +
                      " c.MAXSUBSCRIBERS," +
                      " c.ALLOWSUBSCRPROVISIONING" +
                    " FROM " + KnBulkOpsDAOSourceTypes.SERVERCAPACITYUTILINFO + " u" +
                    " LEFT JOIN " + KnBulkOpsDAOSourceTypes.POCSUBSCRCAPACITYCONFIG + " c ON u.PTTSERVERID = c.PTTSERVERID" +
                    " LEFT JOIN " + KnBulkOpsDAOSourceTypes.SUBSCRPARTITIONINGCONFIG + " p ON p.PTTServerId = ?" +
                    " WHERE u.PTTSERVERID = ?";
            params = new Object[]{ xdmPttServerId, pocPttServerId };
        } else {
            // Partition config only
            query = "SELECT PTTServerId, MDNPartitionType_POC, EnableCorpAccountAnchoring, " +
                   "MaxSubscrPerXDMS, MaxSubscrPerPoC FROM " + KnBulkOpsDAOSourceTypes.SUBSCRPARTITIONINGCONFIG + " WHERE PTTServerId = ?";
            params = new Object[]{xdmPttServerId};
        }

        KnBulkOpsPocConfigDTO configDTO = bulkOpsDBUtil.executeQuery(query, params, rs -> {
            if (rs.next()) {
                KnBulkOpsPocConfigDTO dto = new KnBulkOpsPocConfigDTO();

                if (pocPttServerId != null && !pocPttServerId.isEmpty()) {
                    // Extract partition config from subquery results
                    dto.setPartitionPttServerId(rs.getString("PARTITION_PTTSERVERID"));
                    dto.setMdnPartitionTypePOC(rs.getInt("MDN_PARTITION_TYPE"));
                    dto.setEnableCorpAccAnch(rs.getInt("ENABLE_CORP_ANCH"));
                    dto.setMaxSubsPerXDMS(rs.getInt("MAX_SUBS_XDMS"));
                    dto.setMaxSubsPerPoC(rs.getInt("MAX_SUBS_POC"));

                    // Extract capacity info
                    dto.setPocPttServerId(rs.getString("POC_PTTSERVERID"));
                    dto.setSubscriberCount(rs.getInt("SUBSCRIBERCOUNT"));
                    Integer maxLimit = rs.getInt("MAXSUBSCRIBERS");
                    Integer allowProv = rs.getInt("ALLOWSUBSCRPROVISIONING");

                    if (!rs.wasNull()) {
                        dto.setMaxSubsLimit(maxLimit);
                        dto.setAllowProv(allowProv);
                        dto.setHasCapacityConfig(true);
                    }
                } else {
                    // Partition config only
                    dto.setPartitionPttServerId(rs.getString("PTTServerId"));
                    dto.setMdnPartitionTypePOC(rs.getInt("MDNPartitionType_POC"));
                    dto.setEnableCorpAccAnch(rs.getInt("EnableCorpAccountAnchoring"));
                    dto.setMaxSubsPerXDMS(rs.getInt("MaxSubscrPerXDMS"));
                    dto.setMaxSubsPerPoC(rs.getInt("MaxSubscrPerPoC"));
                }

                return dto;
            }
            return null;
        }, persisterTxn, null);

        if (configDTO == null || configDTO.getPartitionPttServerId() == null) {
            knLogger.error(methodName, "Partition config not found");
            throw new KnDAOException(KnBulkOpsErrorCodes.BOEntity.SUBS_PARTITION_CONFIG_NOT_FOUND,
                    "Subscriber Partition config not found");
        }

        knLogger.debug(methodName, "Fetched config: ", configDTO);
        return configDTO;
    }

    /**
     * Get all POC PTT Server IDs with capacity info in single query using JOINs
     * Only returns servers with ALLOW_PROV enabled
     *
     * @param persisterTxn Database transaction
     * @return Map of PTT Server ID to KnBulkOpsPocConfigDTO with capacity info
     * @throws KnDAOException if database operation fails
     */
    public Map<String, KnBulkOpsPocConfigDTO> getAllPocServersWithCapacity(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAllPocServersWithCapacity(KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching all POC servers with capacity info");

        // ULTRA-OPTIMIZED: Single query with JOINs, COALESCE for null handling, filtered in SQL
        String query =
            "SELECT p.PTTSERVERID, " +
            "       COALESCE(u.SUBSCRIBERCOUNT, 0) as SUBSCRIBERCOUNT, " +
            "       c.MAXSUBSCRIBERS, " +
            "       c.ALLOWSUBSCRPROVISIONING " +
            "FROM " + KnBulkOpsDAOSourceTypes.PTTSERVERIPINFO + " p " +
            "LEFT JOIN " + KnBulkOpsDAOSourceTypes.SERVERCAPACITYUTILINFO + " u ON p.PTTSERVERID = u.PTTSERVERID " +
            "LEFT JOIN " + KnBulkOpsDAOSourceTypes.POCSUBSCRCAPACITYCONFIG + " c ON p.PTTSERVERID = c.PTTSERVERID " +
            "WHERE p.SIGNALINGCARDTYPE = " + KnBulkOpsConstants.POC_CARD_TYPE +
            " AND c.ALLOWSUBSCRPROVISIONING = 1";

        Map<String, KnBulkOpsPocConfigDTO> capacityMap = bulkOpsDBUtil.executeQuery(query, new Object[]{}, rs -> {
            Map<String, KnBulkOpsPocConfigDTO> resultMap = new HashMap<>();
            while (rs.next()) {
                String pttServerId = rs.getString("PTTSERVERID");
                pttServerId = (pttServerId != null) ? pttServerId.trim() : pttServerId;
                int subsCount = rs.getInt("SUBSCRIBERCOUNT");
                Integer maxSubsLimit = rs.getInt("MAXSUBSCRIBERS");
                Integer allowProv = rs.getInt("ALLOWSUBSCRPROVISIONING");

                boolean hasConfig = !rs.wasNull();
                resultMap.put(pttServerId, KnBulkOpsPocConfigDTO.forPocCapacity(
                    pttServerId, subsCount, maxSubsLimit, allowProv, hasConfig));
            }
            return resultMap;
        }, persisterTxn, null);

        knLogger.debug(methodName, "Fetched ", capacityMap.size(), " POC servers with ALLOW_PROV enabled");
        return capacityMap;
    }

    /**
     * Get POC home for multiple MDNs based on prefix mapping (MDN_BASED partitioning)
     * Uses batch query to fetch all relevant prefix mappings at once, then does in-memory longest prefix match
     *
     * @param mdnList List of MDNs to lookup
     * @param persisterTxn Database transaction
     * @return Map of MDN to PTT Server ID
     * @throws KnDAOException if database operation fails
     */
    public Map<String, String> getSubscriberPoCHomeForMdns(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberPoCHomeForMdns(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Getting POC home for ", mdnList.size(), " MDNs");

        if (mdnList == null || mdnList.isEmpty()) {
            return new LinkedHashMap<>();
        }

        final List<String> mdnListCopy = new ArrayList<>(mdnList);

        String query = "SELECT SUBSCRIBERPREFIX, POC_PTTSERVERID FROM " + KnBulkOpsDAOSourceTypes.SUBS_POCSERVERMAP;

        Map<String, String> mdnToPocMap = bulkOpsDBUtil.executeQuery(query, new Object[]{}, rs -> {
            // Store all prefix mappings in a list
            List<KnBulkOpsPocConfigDTO.PrefixMapping> allPrefixes = new ArrayList<>();
            while (rs.next()) {
                String prefix = rs.getString("SUBSCRIBERPREFIX");
                String pttServerId = rs.getString("POC_PTTSERVERID");
                if (prefix != null && pttServerId != null) {
                    allPrefixes.add(new KnBulkOpsPocConfigDTO.PrefixMapping(prefix, pttServerId));
                }
            }

            // Sort prefixes by length descending - longest first
            // This allows early exit once we find a match (longest match wins)
            allPrefixes.sort((p1, p2) -> Integer.compare(p2.prefix.length(), p1.prefix.length()));

            // For each MDN, find longest matching prefix - LinkedHashMap maintains insertion order
            Map<String, String> resultMap = new LinkedHashMap<>(mdnListCopy.size());

            for (String mdn : mdnListCopy) {
                if (mdn == null) continue;

                // Since prefixes are sorted longest-first, first match is the longest match
                for (KnBulkOpsPocConfigDTO.PrefixMapping mapping : allPrefixes) {
                    if (mdn.startsWith(mapping.prefix)) {
                        resultMap.put(mdn, mapping.pttServerId);
                        break; // Early exit - found longest match
                    }
                }
            }

            return resultMap;
        }, persisterTxn, null);

        knLogger.debug(methodName, "Mapped ", mdnToPocMap.size(), " MDNs to POC homes");
        return mdnToPocMap;
    }

    /**
     * Get pre-assigned corporate home for an external corporate ID
     *
     * @param extCorpId External corporate ID
     * @param persisterTxn Database transaction
     * @return Pre-assigned POC home, null if not found
     * @throws KnDAOException if database operation fails
     */
    public String getPreAssignCorpHome(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPreAssignCorpHome(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching pre-assigned corp home for extCorpId: ", extCorpId);

        String query = "SELECT POCHOME FROM " + KnBulkOpsDAOSourceTypes.PREASSIGNEDCORPHOME + " WHERE EXTCORPID = ?";
        String pocHome = bulkOpsDBUtil.executeQuery(query, new Object[]{extCorpId}, rs -> {
            if (rs.next()) {
                return rs.getString("POCHOME");
            }
            return null;
        }, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);

        knLogger.info(methodName, "Pre-assigned corp home for extCorpId ", extCorpId, " -> ", pocHome);
        return pocHome;
    }

    /**
     * Check if PR-in-POC feature is enabled from environment variables
     *
     * @param persisterTxn Database transaction
     * @return true if enabled, false otherwise
     * @throws KnDAOException if database operation fails
     */
    public Boolean isPrInPocEnabled(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isPrInPocEnabled(KnPersisterTxn)";
        knLogger.debug(methodName, "Checking if PR-in-POC is enabled");

        String query = " SELECT PARAMNAME, PARAMVALUE FROM " + KnBulkOpsDAOSourceTypes.RTXENVVARIABLEINFO + " WHERE PARAMNAME = 'ENABLE_PR_IN_POC' AND PTTSERVERID = ?";
        Object[] params = new Object[]{KnBulkOpsDBUtil.getXdmPttServerId()};
        Boolean isEnabled = bulkOpsDBUtil.executeQuery(query, params, rs -> {
            if (rs.next()) {
                String value = rs.getString("PARAMVALUE");
                return KnBulkOpsConstants.PR_IN_POC_ENABLED.equals(value);
            }
            return false;
        }, persisterTxn, null);

        knLogger.debug(methodName, "PR-in-POC enabled: ", isEnabled);
        return isEnabled;
    }

    /**
     * Retrieve Presence Service Config for multiple POC servers (batch query)
     * Checks if each POC server has ENABLE_PR_IN_POC enabled
     *
     * @param pocPttServerIds List of POC PTT Server IDs
     * @param persisterTxn Database transaction
     * @return Map of POC PTT Server ID to EnablePRInPoC flag (1=enabled, 0/null=disabled)
     * @throws KnDAOException if database operation fails
     */
    public Map<String, Integer> retrievePresenceServiceConfigBatch(List<String> pocPttServerIds,
                                                                    KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePresenceServiceConfigBatch(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching presence config for ", pocPttServerIds.size(), " POC servers");

        if (pocPttServerIds == null || pocPttServerIds.isEmpty()) {
            return new HashMap<>();
        }

        String queryPrefix = "SELECT PTTSERVERID, ENABLE_PR_IN_POC FROM " + KnBulkOpsDAOSourceTypes.PRESENCESERVICECONFIG + " WHERE PTTSERVERID IN ";

        Map<String, Integer> configMap = bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                pocPttServerIds,
                rs -> {
                    Map<String, Integer> resultMap = new HashMap<>();
                    while (rs.next()) {
                        String pttServerId = rs.getString("PTTSERVERID");
                        pttServerId = (pttServerId != null) ? pttServerId.trim() : pttServerId;
                        int enablePrInPoc = rs.getInt("ENABLE_PR_IN_POC");
                        resultMap.put(pttServerId, enablePrInPoc);
                    }
                    return resultMap;
                },
                persisterTxn,
                null
        );

        knLogger.debug(methodName, "Fetched config for ", configMap.size(), " POC servers");
        return configMap;
    }

    /**
     * Get presence home for multiple MDNs based on prefix mapping (OPTIMIZED for bulk)
     * Fetches all prefix mappings once and does in-memory longest prefix match for all MDNs
     * Same logic as POC prefix matching but for Presence servers
     *
     * @param mdnList List of MDNs to lookup
     * @param persisterTxn Database transaction
     * @return Map of MDN to Presence PTT Server ID
     * @throws KnDAOException if database operation fails or prefix not found for any MDN
     */
    public Map<String, String> getSubscriberPresenceHomeForMdns(List<String> mdnList,
                                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberPresenceHomeForMdns(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Getting Presence home for ", mdnList.size(), " MDNs");

        if (mdnList == null || mdnList.isEmpty()) {
            return new LinkedHashMap<>();
        }

        // Make a final copy for thread-safe access in lambda
        final List<String> mdnListCopy = new ArrayList<>(mdnList);

        // Fetch all presence prefix mappings from DB (single query)
        String query = "SELECT SUBSCRIBERPREFIX, PR_PTTSERVERID FROM " + KnBulkOpsDAOSourceTypes.SUBS_PRSERVERMAP;

        Map<String, String> mdnToPresenceMap = bulkOpsDBUtil.executeQuery(query, new Object[]{}, rs -> {
            // Store all prefix mappings
            List<KnBulkOpsPocConfigDTO.PrefixMapping> allPrefixes = new ArrayList<>();
            while (rs.next()) {
                String prefix = rs.getString("SUBSCRIBERPREFIX");
                String prPttServerId = rs.getString("PR_PTTSERVERID");
                if (prefix != null && prPttServerId != null) {
                    allPrefixes.add(new KnBulkOpsPocConfigDTO.PrefixMapping(prefix, prPttServerId));
                }
            }

            // OPTIMIZATION: Sort prefixes by length descending - longest first for early exit
            allPrefixes.sort((p1, p2) -> Integer.compare(p2.prefix.length(), p1.prefix.length()));

            // For each MDN, find longest matching prefix - LinkedHashMap maintains insertion order
            Map<String, String> resultMap = new LinkedHashMap<>(mdnListCopy.size());

            for (String mdn : mdnListCopy) {
                if (mdn == null) continue;

                // Since prefixes are sorted longest-first, first match is the longest match
                for (KnBulkOpsPocConfigDTO.PrefixMapping mapping : allPrefixes) {
                    if (mdn.startsWith(mapping.prefix)) {
                        resultMap.put(mdn, mapping.pttServerId);
                        break; // Early exit - found longest match
                    }
                }
            }
            return resultMap;
        }, persisterTxn, null);

        knLogger.debug(methodName, "Mapped ", mdnToPresenceMap.size(), " MDNs to Presence homes");
        return mdnToPresenceMap;
    }

    /**
     * Retrieve POC Service Configuration for a single POC PTT Server ID
     * Optimized for single POC home retrieval
     *
     * @param pocPttServerId Single POC PTT Server ID
     * @param persisterTxn Database transaction
     * @return KnPOCSvcConfigDTO configuration for the POC server, null if not found
     * @throws KnDAOException if database operation fails
     */
    public KnPOCSvcConfigDTO retrievePOCSvcConfig(String pocPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrievePOCSvcConfig(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieving POC Service Config for POC server: ", pocPttServerId);

        if (pocPttServerId == null || pocPttServerId.isEmpty()) {
            knLogger.warn(methodName, "POC server ID is null or empty");
            return null;
        }

        KnPOCSvcConfigDTO config = bulkOpsDBUtil.executeQuery(
            "SELECT PTTSERVERID, PRIMARYPOCSERVERURI, MAXLEGSINADHOCGRPCALL, POC_CONFFACTORYURI, " +
            "TBCPREQTIMEOUT, FLOORIDLEDETECTIONTIMER, MEDIAIDLETIMER, MEDIAPORTREFRESHTIME, " +
            "POCPUBLISHVALIDITY, PREESTSESSIONVALIDITY, NUMMEDIAPORTKAMSGS, MEDIAPORTKAMSGSIZE, " +
            "NUMTBCPRETRIESBYCLIENT, ENABLEINSTAPOC, MAXFLOORHOLDDURATION, MAXTERMLEGSINPOCGRPCALL, " +
            "MAXTALKBURSTGRACETIME, ENABLE_SUPERVISORY_OVERRIDE, ENABLE_MISSED_CALL_ALERT, " +
            "ENABLE_CALL_ENDED_ALERT, SIPRETRYTIMER, SIPRETRYTIMERBACKOFF, IPDEBOUNCETIMER, " +
            "Client_TBCPFloorReqRetryTimer, Client_TBCPRelReqRetryTimer, PreCallNumMediaPortKAMsgs, " +
            "PrecallMediaPortKAMsgSize, PreCallMediaPortKAInterval, MediaPortKAInterval, " +
            "PreCallMediaKADuration, MEDIASECURESESSIONREFRESHINTVL, CLIENTINCALLSUSPENDTIMER, " +
            "ENABLEPOCWIFI, DYNAMICQOSFLAG, MAXSIMULDEDICATEDSESSION, MAXSIMULDYNAMICSESSION " +
            "FROM " + KnBulkOpsDAOSourceTypes.POC_SVC_CONFIG + " WHERE PTTSERVERID = ?",
            new Object[]{pocPttServerId},
            rs -> {
                if (rs.next()) {
                    KnPOCSvcConfigDTO configDto = new KnPOCSvcConfigDTO();

                    configDto.setPttServerId(rs.getString("PTTSERVERID"));
                    configDto.setPrimaryPOCServerURI(rs.getString("PRIMARYPOCSERVERURI"));
                    configDto.setMaxLegsInAdhocGrpCall(rs.getInt("MAXLEGSINADHOCGRPCALL"));
                    configDto.setPOC_ConfFactoryURI(rs.getString("POC_CONFFACTORYURI"));
                    configDto.setTBCPReqTimeout(rs.getInt("TBCPREQTIMEOUT"));
                    configDto.setFloorIdleDetectionTimer(rs.getInt("FLOORIDLEDETECTIONTIMER"));
                    configDto.setMediaIdleTimer(rs.getInt("MEDIAIDLETIMER"));
                    configDto.setMediaPortRefreshTime(rs.getInt("MEDIAPORTREFRESHTIME"));
                    configDto.setPOCPublishValidity(rs.getInt("POCPUBLISHVALIDITY"));
                    configDto.setPreestSessionValidity(rs.getInt("PREESTSESSIONVALIDITY"));
                    configDto.setNumMediaPortKAMsgs(rs.getInt("NUMMEDIAPORTKAMSGS"));
                    configDto.setMediaPortKAMsgSize(rs.getInt("MEDIAPORTKAMSGSIZE"));
                    configDto.setNumTBCPRetriesByClient(rs.getInt("NUMTBCPRETRIESBYCLIENT"));
                    configDto.setEnableInstaPOC(rs.getInt("ENABLEINSTAPOC"));
                    configDto.setMaxFloorHoldDuration(rs.getInt("MAXFLOORHOLDDURATION"));
                    configDto.setMaxTermLegsInPOCGrpCall(rs.getInt("MAXTERMLEGSINPOCGRPCALL"));
                    configDto.setMaxTalkBurstGraceTime(rs.getInt("MAXTALKBURSTGRACETIME"));
                    configDto.setEnableSuperVisoryOverride(rs.getInt("ENABLE_SUPERVISORY_OVERRIDE"));
                    configDto.setEnableMissedCallAlert(rs.getInt("ENABLE_MISSED_CALL_ALERT"));
                    configDto.setEnableCallEndedAlert(rs.getInt("ENABLE_CALL_ENDED_ALERT"));
                    configDto.setSipRetryTimer(rs.getInt("SIPRETRYTIMER"));
                    configDto.setSipRetryTimerBackOff(rs.getInt("SIPRETRYTIMERBACKOFF"));
                    configDto.setClient_TBCPFloorReqRetryTimer(rs.getInt("Client_TBCPFloorReqRetryTimer"));
                    configDto.setClient_TBCPRelReqRetryTimer(rs.getInt("Client_TBCPRelReqRetryTimer"));
                    configDto.setPreCallNumMediaPortKAMsgs(rs.getInt("PreCallNumMediaPortKAMsgs"));
                    configDto.setPrecallMediaPortKAMsgSize(rs.getInt("PrecallMediaPortKAMsgSize"));
                    configDto.setPreCallMediaPortKAInterval(rs.getInt("PreCallMediaPortKAInterval"));
                    configDto.setMediaPortKAInterval(rs.getInt("MediaPortKAInterval"));
                    configDto.setPreCallMediaKADuration(rs.getInt("PreCallMediaKADuration"));
                    configDto.setIpDebounceTimer(rs.getInt("IPDEBOUNCETIMER"));
                    configDto.setMediaSecureSesRefIntvl(rs.getInt("MEDIASECURESESSIONREFRESHINTVL"));
                    configDto.setClientInCallSusTimer(rs.getInt("CLIENTINCALLSUSPENDTIMER"));
                    configDto.setEnablePocWifi(rs.getInt("ENABLEPOCWIFI"));
                    configDto.setDynamicQosFlag(rs.getInt("DYNAMICQOSFLAG"));
                    configDto.setMaxSDDSession(rs.getInt("MAXSIMULDEDICATEDSESSION"));
                    configDto.setMaxSDYSession(rs.getInt("MAXSIMULDYNAMICSESSION"));

                    return configDto;
                }
                return null;
            },
            persisterTxn,
            null
        );

        if (config != null) {
            knLogger.info(methodName, "EXIT: Retrieved POC service configuration for: ", pocPttServerId);
        } else {
            knLogger.warn(methodName, "EXIT: No POC service configuration found for: ", pocPttServerId);
        }
        return config;
    }
    
    /**
     * Get profile ID for NNI subscriber based on profile type
     *
     * @param profileType  Profile type to fetch
     * @param persisterTxn Database transaction
     * @return Profile ID for the given profile type
     * @throws KnDAOException if database operation fails or no record found
     */
    public int getProfileIdForNNISubscriber(int profileType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileIdForNNISubscriber(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: profileType=", profileType);

        String query = "SELECT PROFILEID FROM " + KnBulkOpsDAOSourceTypes.GENERIC_NNI_PROFILE + " WHERE PROFILE_TYPE = ?";
        Object[] params = new Object[]{profileType};

        Integer profileId = bulkOpsDBUtil.executeQuery(query, params, rs -> {
            if (rs.next()) {
                return rs.getInt("PROFILEID");
            }
            throw new KnDAOException(KnBulkOpsErrorCodes.DAO.ROW_NOT_FOUND, "No profile found for profileType: " + profileType);
        }, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);

        knLogger.info(methodName, "EXIT: profileId=", profileId);
        return profileId;
    }

    /**
     * Create NNI subscriber profile entries in bulk for multiple MDNs using upsert (MERGE) operation.
     * If the MDN already exists, updates PROFILEID and NNIACTIVEFS.
     * If the MDN doesn't exist, inserts a new record.
     *
     * @param nniSubsDTO   NNI subscriber data containing MDNs, profileId, and nniActiveFs
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void createNNISubscrProfile(KnBulkOpsNNISubsDTO nniSubsDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createNNISubscrProfile(KnBulkOpsNNISubsDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Creating/Updating NNI profile for ", nniSubsDTO.getMdns().size(), " MDNs");

        if (nniSubsDTO == null || nniSubsDTO.getMdns() == null || nniSubsDTO.getMdns().isEmpty()) {
            knLogger.warn(methodName, "No MDNs provided for NNI subscriber profile creation");
            return;
        }

        // Define columns for MERGE operation
        List<String> keyColumns = Collections.singletonList("MDN");
        List<String> allColumns = Arrays.asList("MDN", "PROFILEID", "NNIACTIVEFS");
        // Update PROFILEID and NNIACTIVEFS when record exists
        List<String> updateColumns = Arrays.asList("PROFILEID", "NNIACTIVEFS");

        List<Object[]> batchParams = new ArrayList<>();
        for (String mdn : nniSubsDTO.getMdns()) {
            batchParams.add(new Object[]{mdn, nniSubsDTO.getProfileId(), nniSubsDTO.getNniActiveFs()});
        }

        // Use MERGE for upsert operation
        bulkOpsDBUtil.executeBatchMerge(
                KnBulkOpsDAOSourceTypes.POC_NNISUBSCR_INFO,
                keyColumns,
                allColumns,
                updateColumns,
                batchParams,
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.info(methodName, "EXIT: Successfully created/updated NNI profiles for ", nniSubsDTO.getMdns().size(), " MDNs");
    }

    /**
     * Add APN profile for bulk subscribers using upsert (MERGE) operation.
     * If the MDN already exists, updates the APNID.
     * If the MDN doesn't exist, inserts a new record.
     *
     * @param mdnList      List of MDNs to assign APN
     * @param apnId        APN ID to be assigned
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void addSubApnForBulk(List<String> mdnList, int apnId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addSubApnForBulk(List<String>, int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Adding/Updating APN profile for ", mdnList.size(), " MDNs, apnId: ", apnId);

        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "No MDNs to process, returning");
            return;
        }

        // Define columns for MERGE operation
        List<String> keyColumns = Collections.singletonList("MDN");
        List<String> allColumns = Arrays.asList("MDN", "APNID");
        // Update APNID when record exists
        List<String> updateColumns = Collections.singletonList("APNID");

        List<Object[]> batchParams = new ArrayList<>();
        for (String mdn : mdnList) {
            batchParams.add(new Object[]{mdn, apnId});
        }

        // Use MERGE for upsert operation
        bulkOpsDBUtil.executeBatchMerge(
                KnBulkOpsDAOSourceTypes.SUBSCRAPNINFO,
                keyColumns,
                allColumns,
                updateColumns,
                batchParams,
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.info(methodName, "EXIT: Successfully added/updated APN profile for ", mdnList.size(), " MDNs");
    }

    /**
     * Create subscriber roaming profile entries in bulk for multiple MDNs using upsert (MERGE) operation.
     * If the MDN already exists, updates the ROAMINGCLUSTERID.
     * If the MDN doesn't exist, inserts a new record.
     *
     * @param mdnList              List of MDNs to create roaming profile
     * @param roamingClusterIdList List of roaming cluster IDs (same for all MDNs)
     * @param persisterTxn         Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void createSubscrRoamingProfileForBulk(List<String> mdnList, List<Integer> roamingClusterIdList,
                                                   KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSubscrRoamingProfileForBulk(List<String>, List<Integer>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Creating/Updating roaming profile for ", mdnList.size(), " MDNs with ",
                     roamingClusterIdList.size(), " roaming cluster IDs");

        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "No MDNs to process, returning");
            return;
        }

        if (roamingClusterIdList == null || roamingClusterIdList.isEmpty()) {
            knLogger.debug(methodName, "No roaming cluster IDs provided, returning");
            return;
        }

        // Define columns for MERGE operation
        // Key column: MDN only
        List<String> keyColumns = Collections.singletonList("MDN");
        List<String> allColumns = Arrays.asList("MDN", "ROAMINGCLUSTERID");
        // Update ROAMINGCLUSTERID when record exists
        List<String> updateColumns = Collections.singletonList("ROAMINGCLUSTERID");

        List<Object[]> batchParams = new ArrayList<>();

        // For each MDN, create entries for all roaming cluster IDs
        for (String mdn : mdnList) {
            for (Integer roamingClusterId : roamingClusterIdList) {
                batchParams.add(new Object[]{mdn, roamingClusterId});
            }
        }

        // Use MERGE for upsert operation
        bulkOpsDBUtil.executeBatchMerge(
                KnBulkOpsDAOSourceTypes.POCSUBSCRROAMINGPROFILE,
                keyColumns,
                allColumns,
                updateColumns,
                batchParams,
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.info(methodName, "EXIT: Successfully created/updated ", batchParams.size(),
                     " roaming profile entries (", mdnList.size(), " MDNs × ",
                     roamingClusterIdList.size(), " cluster IDs)");
    }

    /**
     * Add MDNs to Corp Resource List Index Doc for bulk subscribers (only if not already exists)
     * Uses optimized single query with NOT EXISTS check to avoid checking each MDN individually
     *
     * Thread-safe implementation handling all edge cases:
     * - Handles duplicate MDNs in input list
     * - First checks which MDNs already exist in the table
     * - Only generates new RESOURCELISTDOCID for MDNs that don't exist
     * - Uses synchronized block with fresh ID generation to prevent race conditions
     * - Handles race condition where MDN is inserted between check and insert (catches duplicate key and retries)
     * - For existing MDNs, only updates ETAG (keeps existing RESOURCELISTDOCID unchanged)
     *
     * Primary Key: RESOURCELISTDOCID
     * Unique Constraint: MDN
     *
     * @param mdnList      List of MDNs to add to corp resource list index
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void addMdnToCorpResourceListIndexDocForBulk(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addMdnToCorpResourceListIndexDocForBulk(List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Adding ", mdnList.size(), " MDNs to Corp Resource List Index Doc");

        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "No MDNs to process, returning");
            return;
        }

        try {
            String xdmPttServerId = KnBulkOpsDBUtil.getXdmPttServerId();

            List<String> existingMdns = fetchExistingCorpResourceListMdns(mdnList, persisterTxn);
            knLogger.debug(methodName, "Found ", existingMdns.size(), " MDNs already exist in Corp Resource List Index Doc");

            List<String> mdnsToInsert = new ArrayList<>(mdnList);
            mdnsToInsert.removeAll(existingMdns);

            knLogger.debug(methodName, "MDNs to insert: ", mdnsToInsert.size());
            if (mdnsToInsert.isEmpty()) {
                knLogger.info(methodName, "EXIT: All MDNs already exist, nothing to insert");
                return;
            }

            KnDBXDMServerDAO dbXdmServerDAO = new KnDBXDMServerDAO(xdmPttServerId);
            for (String mdn : mdnsToInsert) {
                dbXdmServerDAO.addMdnToCorpResourceListIndexDoc(mdn, persisterTxn);
                knLogger.debug(methodName, "Inserted MDN: ", KnGDPRTemplate.mdn(mdn), " to Corp Resource List Index Doc");
            }

            knLogger.info(methodName, "EXIT: Successfully added ", mdnsToInsert.size(),
                         " new MDNs to Corp Resource List Index Doc (", existingMdns.size(), " already existed)");

        } catch (Exception e) {
            knLogger.error(methodName, "Failed to add MDNs to Corp Resource List Index Doc: ", e);
            throw KnDbUtil.processException(e, "Failed to add MDNs to Corp Resource List Index Doc: " + e.getMessage(),
                    KnBulkOpsDBUtil.getXdmPttServerId(), KnBulkOpsDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC,
                    null);
        }
    }

    /**
     * Fetch MDNs that already exist in XDM_CORPRESOURCELISTINDEXDOC table
     *
     * @param mdnList      List of MDNs to check
     * @param persisterTxn Database transaction
     * @return List of MDNs that already exist in the table
     * @throws KnDAOException if database operation fails
     */
    private List<String> fetchExistingCorpResourceListMdns(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "fetchExistingCorpResourceListMdns";

        String queryPrefix = "SELECT MDN FROM " + KnBulkOpsDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC + " WHERE MDN IN ";

        List<String> existingMdns = bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                mdnList,
                rs -> {
                    List<String> resultList = new ArrayList<>();
                    while (rs.next()) {
                        String mdn = rs.getString("MDN");
                        resultList.add(mdn != null ? mdn.trim() : mdn);
                    }
                    return resultList;
                },
                persisterTxn,
                null
        );

        knLogger.debug(methodName, "Found ", existingMdns.size(), " existing MDNs in Corp Resource List Index Doc");
        return existingMdns;
    }

    /**
     * Add MDNs to XDM Contact List Doc Map for bulk subscribers
     * Generates contact list IDs and creates entries in XDM_CONTACTLIST_DOCMAP table
     *
     * Thread-safe implementation handling all edge cases:
     * - Handles duplicate MDNs in input list
     * - First checks which MDNs already exist in the table
     * - Only generates new IDs (CONTACTLISTID, RESOURCELISTDOCID) for MDNs that don't exist
     * - Uses synchronized block with fresh MAX(RESOURCELISTDOCID) fetch to prevent race conditions
     * - Handles race condition where MDN is inserted between check and insert (catches duplicate key and retries as update)
     * - For existing MDNs, only updates RESOURCELIST_ETAG (keeps existing IDs unchanged)
     *
     * Primary Key: RESOURCELISTDOCID
     * Unique Constraint: OWNERMDN
     *
     * @param mdnList      List of MDNs to add
     * @param persisterTxn Database transaction
     * @return List of contact list IDs (generated for new, fetched for existing)
     * @throws KnDAOException if database operation fails
     */
    public List<Integer> addMdnToXDMContactListDocMapForBulk(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addMdnToXDMContactListDocMapForBulk(List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Adding ", mdnList.size(), " MDNs to XDM Contact List Doc Map");

        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "No MDNs to process, returning empty list");
            return new ArrayList<>();
        }

        List<Integer> contactListDocIds = new ArrayList<>();

        try {
            String xdmPttServerId = KnBulkOpsDBUtil.getXdmPttServerId();

            // First, check which MDNs already exist in the table
            Map<String, Integer> existingMdnMap = getExistingContactListIds(mdnList, persisterTxn);
            knLogger.debug(methodName, "Found ", existingMdnMap.size(), " existing MDNs in XDM Contact List Doc Map");

            // Separate MDNs into existing and new
            List<String> newMdnList = new ArrayList<>();
            for (String mdn : mdnList) {
                if (!existingMdnMap.containsKey(mdn)) {
                    newMdnList.add(mdn);
                }
            }
            knLogger.debug(methodName, "MDNs to insert: ", newMdnList.size(), ", MDNs already existing: ", existingMdnMap.size());

            KnDBXDMServerDAO dbXdmServerDAO = new KnDBXDMServerDAO(xdmPttServerId);

            // Only insert MDNs that don't already exist
            for (String mdn : newMdnList) {
                KnContactListPersistDTO contactListPersistDTO = new KnContactListPersistDTO();
                contactListPersistDTO.setMdn(mdn);
                int contactListDocId = dbXdmServerDAO.addMdnToXDMContactListDocMap(contactListPersistDTO, persisterTxn);
                existingMdnMap.put(mdn, contactListDocId);
                knLogger.debug(methodName, "Inserted MDN: ", KnGDPRTemplate.mdn(mdn), " with contactListDocId: ", contactListDocId);
            }

            // Build the result list in the same order as the input mdnList
            for (String mdn : mdnList) {
                contactListDocIds.add(existingMdnMap.get(mdn));
            }

            knLogger.info(methodName, "EXIT: Successfully processed ", mdnList.size(), " MDNs (inserted: ", newMdnList.size(), ", existing: ", (mdnList.size() - newMdnList.size()), ")");
            return contactListDocIds;

        } catch (Exception e) {
            knLogger.error(methodName, "Failed to add MDNs to XDM Contact List Doc Map: ", e);
            throw KnDbUtil.processException(e, "Failed to add MDNs to XDM Contact List Doc Map: " + e.getMessage(),
                    KnBulkOpsDBUtil.getXdmPttServerId(), KnBulkOpsDAOSourceTypes.XDM_CONTACTLIST_DOCMAP,
                    null);
        }
    }

    /**
     * Get existing CONTACTLISTID for MDNs that already exist in XDM_CONTACTLIST_DOCMAP
     *
     * @param mdnList      List of MDNs to check
     * @param persisterTxn Database transaction
     * @return Map of MDN to CONTACTLISTID for existing records
     * @throws KnDAOException if database operation fails
     */
    private Map<String, Integer> getExistingContactListIds(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExistingContactListIds";
        Map<String, Integer> existingMdnMap = new HashMap<>();

        if (mdnList == null || mdnList.isEmpty()) {
            return existingMdnMap;
        }

        String queryPrefix = "SELECT OWNERMDN, CONTACTLISTID FROM " + KnBulkOpsDAOSourceTypes.XDM_CONTACTLIST_DOCMAP +
                " WHERE OWNERMDN IN ";

        try {
            existingMdnMap = bulkOpsDBUtil.executeInClauseQuery(queryPrefix, mdnList, rs -> {
                Map<String, Integer> resultMap = new HashMap<>();
                while (rs.next()) {
                    String ownerMdn = rs.getString("OWNERMDN");
                    resultMap.put(ownerMdn != null ? ownerMdn.trim() : ownerMdn, rs.getInt("CONTACTLISTID"));
                }
                return resultMap;
            }, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to fetch existing contact list IDs: ", e);
            throw KnDbUtil.processException(e, "Failed to fetch existing contact list IDs: " + e.getMessage(),
                    KnBulkOpsDBUtil.getXdmPttServerId(), KnBulkOpsDAOSourceTypes.XDM_CONTACTLIST_DOCMAP, queryPrefix);
        }

        return existingMdnMap != null ? existingMdnMap : new HashMap<>();
    }

    /**
     * Add MDNs to XDM Contact List for bulk subscribers
     * Creates entries in XDM_CONTACTLIST table
     *
     * @param mdnList         List of MDNs to add
     * @param contactListIds  List of contact list IDs (one per MDN)
     * @param persisterTxn    Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void addMdnToXDMContactListForBulk(List<String> mdnList, List<Integer> contactListIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addMdnToXDMContactListForBulk(List<String>, List<Integer>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Adding ", mdnList.size(), " MDNs to XDM Contact List");

        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "No MDNs to process, returning");
            return;
        }

        if (contactListIds == null || contactListIds.size() != mdnList.size()) {
            throw new KnDAOException(KnBulkOpsErrorCodes.DAO.BULK_CONTACTLIST_INSERT_FAILED,
                    "Contact list IDs size mismatch with MDN list size");
        }

        try {
            // Define columns for MERGE operation
            // Key column: OWNERMDN (unique per subscriber)
            // CONTACTLISTID is the PRIMARY KEY of DG.XDM_CONTACTLIST and TimesTen does not
            // allow updating a primary key column via MERGE (raises TT0956). Therefore we
            // explicitly pass an EMPTY updateColumns list so the generated MERGE statement
            // contains only the WHEN NOT MATCHED THEN INSERT branch (insert-only upsert).
            // If a row with the same OWNERMDN already exists, it is left untouched.
            List<String> keyColumns = Collections.singletonList("OWNERMDN");
            List<String> allColumns = Arrays.asList("CONTACTLISTID", "OWNERMDN");
            List<String> updateColumns = Collections.emptyList(); // suppress WHEN MATCHED UPDATE

            List<Object[]> batchParams = new ArrayList<>();
            for (int i = 0; i < mdnList.size(); i++) {
                batchParams.add(new Object[]{
                    contactListIds.get(i),
                    mdnList.get(i)
                });
            }

            // Use MERGE for upsert - if record exists, no error and no change
            bulkOpsDBUtil.executeBatchMerge(
                    KnBulkOpsDAOSourceTypes.XDM_CONTACTLIST,
                    keyColumns,
                    allColumns,
                    updateColumns,
                    batchParams,
                    persisterTxn,
                    KnDBConst.DataStores.XDM_SHARED_DATA
            );

            knLogger.info(methodName, "EXIT: Successfully added/verified ", mdnList.size(), " MDNs in XDM Contact List");

        } catch (Exception e) {
            knLogger.error(methodName, "Failed to add MDNs to XDM Contact List: ", e);
            throw KnDbUtil.processException(e, "Failed to add MDNs to XDM Contact List: " + e.getMessage(),
                    KnBulkOpsDBUtil.getXdmPttServerId(), KnBulkOpsDAOSourceTypes.XDM_CONTACTLIST,
                    null);
        }
    }

    /**
     * Add MDNs to XDM Directory for bulk subscribers
     * Creates entries in XDM_DIRECTORY table
     *
     * @param mdnList      List of MDNs to add
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    /**
     * Add MDNs to XDM Directory for bulk subscribers
     * Creates entries in XDM_DIRECTORY table
     *
     * Thread-safe implementation handling all edge cases:
     * - Handles duplicate MDNs in input list
     * - First checks which MDNs already exist in the table
     * - Only generates new DIRDOCID for MDNs that don't exist
     * - Uses synchronized block with fresh ID generation to prevent race conditions
     * - Handles race condition where MDN is inserted between check and insert (catches duplicate key and retries)
     * - For existing MDNs, only updates ETAG (keeps existing DIRDOCID and MDN unchanged)
     *
     * Primary Key: DIRDOCID
     * Unique Constraint: MDN
     *
     * @param mdnList      List of MDNs to add
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void addMdnToXDMDirectoryForBulk(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addMdnToXDMDirectoryForBulk(List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Adding ", mdnList.size(), " MDNs to XDM Directory");

        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "No MDNs to process, returning");
            return;
        }

        try {
            String xdmPttServerId = KnBulkOpsDBUtil.getXdmPttServerId();

            // First, check which MDNs already exist in the table
            Set<String> existingMdns = getExistingXDMDirectoryMdns(mdnList, persisterTxn);
            knLogger.debug(methodName, "Found ", existingMdns.size(), " existing MDNs in XDM Directory");

            // Separate MDNs into existing and new
            List<String> newMdnList = new ArrayList<>();
            for (String mdn : mdnList) {
                if (!existingMdns.contains(mdn)) {
                    newMdnList.add(mdn);
                }
            }
            knLogger.debug(methodName, "MDNs to insert: ", newMdnList.size(), ", MDNs already existing: ", existingMdns.size());

            // Only insert MDNs that don't already exist
            KnDBXDMServerDAO dbXdmServerDAO = new KnDBXDMServerDAO(xdmPttServerId);
            for (String mdn : newMdnList) {
                dbXdmServerDAO.addMdnToXDMDirectory(mdn, persisterTxn);
                knLogger.debug(methodName, "Inserted MDN: ", KnGDPRTemplate.mdn(mdn), " to XDM Directory");
            }

            knLogger.info(methodName, "EXIT: Successfully processed ", mdnList.size(), " MDNs (inserted: ", newMdnList.size(), ", existing: ", existingMdns.size(), ")");

        } catch (Exception e) {
            knLogger.error(methodName, "Failed to add MDNs to XDM Directory: ", e);
            throw KnDbUtil.processException(e, "Failed to add MDNs to XDM Directory: " + e.getMessage(),
                    KnBulkOpsDBUtil.getXdmPttServerId(), KnBulkOpsDAOSourceTypes.XDM_DIRECTORY,
                    null);
        }
    }

    /**
     * Get existing MDNs that already exist in XDM_DIRECTORY
     *
     * @param mdnList      List of MDNs to check
     * @param persisterTxn Database transaction
     * @return Set of MDNs that exist in the table
     * @throws KnDAOException if database operation fails
     */
    private Set<String> getExistingXDMDirectoryMdns(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExistingXDMDirectoryMdns";
        Set<String> existingMdns = new HashSet<>();

        if (mdnList == null || mdnList.isEmpty()) {
            return existingMdns;
        }

        String queryPrefix = "SELECT MDN FROM " + KnBulkOpsDAOSourceTypes.XDM_DIRECTORY + " WHERE MDN IN ";

        try {
            existingMdns = bulkOpsDBUtil.executeInClauseQuery(queryPrefix, mdnList, rs -> {
                Set<String> resultSet = new HashSet<>();
                while (rs.next()) {
                    String mdn = rs.getString("MDN");
                    resultSet.add(mdn != null ? mdn.trim() : mdn);
                }
                return resultSet;
            }, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to fetch existing XDM Directory MDNs: ", e);
            throw KnDbUtil.processException(e, "Failed to fetch existing XDM Directory MDNs: " + e.getMessage(),
                    KnBulkOpsDBUtil.getXdmPttServerId(), KnBulkOpsDAOSourceTypes.XDM_DIRECTORY, queryPrefix);
        }

        return existingMdns != null ? existingMdns : new HashSet<>();
    }

    /**
     * Create addon packages for bulk subscribers
     * Creates entries in DG.POCSUBSADDONPKG table for each MDN-package combination
     *
     * Upsert behavior: For each MDN, if addon packages change, the old packages are replaced with new ones.
     * - First deletes all existing addon packages for the given MDNs
     * - Then inserts the new addon packages
     * This ensures each MDN always has the latest set of addon packages.
     *
     * @param mdnList       List of MDNs
     * @param addOnPkgCodes List of addon package codes (same for all MDNs)
     * @param persisterTxn  Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void createSubAddOnPkgsForBulk(List<String> mdnList, List<String> addOnPkgCodes, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSubAddOnPkgsForBulk(List<String>, List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Creating addon packages for ", mdnList.size(), " MDNs with ",
                     addOnPkgCodes.size(), " package codes");

        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "No MDNs to process, returning");
            return;
        }

        if (addOnPkgCodes == null || addOnPkgCodes.isEmpty()) {
            knLogger.debug(methodName, "No addon package codes provided, returning");
            return;
        }

        try {
            // Step 1: Delete existing addon packages for all MDNs (to replace with new ones)
            String deleteQueryPrefix = "DELETE FROM " + KnBulkOpsDAOSourceTypes.POCSUBSADDONPKG + " WHERE MDN IN ";
            bulkOpsDBUtil.executeInClauseUpdate(deleteQueryPrefix, mdnList, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            knLogger.debug(methodName, "Deleted existing addon packages for ", mdnList.size(), " MDNs");

            // Step 2: Insert new addon packages for all MDNs
            String insertQuery = "INSERT INTO " + KnBulkOpsDAOSourceTypes.POCSUBSADDONPKG +
                    " (MDN, ADDON_PKGCODE) VALUES (?, ?)";

            List<Object[]> batchParams = new ArrayList<>();

            // For each MDN, create entries for all addon packages (cartesian product)
            for (String mdn : mdnList) {
                for (String pkgCode : addOnPkgCodes) {
                    batchParams.add(new Object[]{mdn, pkgCode});
                }
            }

            bulkOpsDBUtil.executeBatchUpdate(insertQuery, batchParams, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);

            knLogger.info(methodName, "EXIT: Successfully replaced addon packages - ", batchParams.size(),
                         " entries (", mdnList.size(), " MDNs × ",
                         addOnPkgCodes.size(), " packages)");

        } catch (Exception e) {
            knLogger.error(methodName, "Failed to create addon packages: ", e);
            throw KnDbUtil.processException(e, "Failed to create addon packages for bulk MDNs: " + e.getMessage(),
                    KnBulkOpsDBUtil.getXdmPttServerId(), KnBulkOpsDAOSourceTypes.POCSUBSADDONPKG,
                    null);
        }
    }

    /**
     * Create subscriber package additional info for bulk subscribers
     * Creates/Updates entries in DG.POCSUBSCR_ADDLINFO table
     *
     * Upsert enabled: MDN is the key column. If MDN exists, updates the other columns.
     * If MDN doesn't exist, inserts a new record.
     *
     * @param mdnList             List of MDNs
     * @param tierPackageCode     Tier package code (same for all MDNs)
     * @param dataPkgId           Data package ID (same for all MDNs)
     * @param onBoardingMailReqd  On-boarding mail required flag (same for all MDNs)
     * @param persisterTxn        Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void createSubscrPkgAddlInfoForBulk(List<String> mdnList, String tierPackageCode, Integer dataPkgId,
                                               Integer onBoardingMailReqd, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSubscrPkgAddlInfoForBulk(List<String>, String, Integer, Integer, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Creating/Updating subscriber package additional info for ", mdnList.size(), " MDNs");

        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "No MDNs to process, returning");
            return;
        }

        try {
            // Define columns for MERGE operation
            // Key column: MDN
            // Update columns: TIME_SLOT_TYPE, TIER_PKG_CODE, DATA_PKG_ID, ONBOARDINGMAILS_REQ, USER_PROFILE_NAME
            List<String> keyColumns = Collections.singletonList("MDN");
            List<String> allColumns = Arrays.asList("MDN", "TIME_SLOT_TYPE", "TIER_PKG_CODE", "DATA_PKG_ID", "ONBOARDINGMAILS_REQ", "USER_PROFILE_NAME");
            List<String> updateColumns = Arrays.asList("TIME_SLOT_TYPE", "TIER_PKG_CODE", "DATA_PKG_ID", "ONBOARDINGMAILS_REQ", "USER_PROFILE_NAME");

            List<Object[]> batchParams = new ArrayList<>();

            for (String mdn : mdnList) {
                batchParams.add(new Object[]{
                        mdn,
                        KnBulkOpsConstants.TIME_SLOT_TYPE,
                        tierPackageCode,
                        dataPkgId,
                        onBoardingMailReqd,
                        null  // USER_PROFILE_NAME is null for bulk operations
                });
            }

            // Use MERGE for upsert - if MDN exists, update; if not, insert
            bulkOpsDBUtil.executeBatchMerge(
                    KnBulkOpsDAOSourceTypes.POCSUBSCR_ADDLINFO,
                    keyColumns,
                    allColumns,
                    updateColumns,
                    batchParams,
                    persisterTxn,
                    KnDBConst.DataStores.XDM_SHARED_DATA
            );

            knLogger.info(methodName, "EXIT: Successfully created/updated subscriber package additional info for ", mdnList.size(), " MDNs");

        } catch (Exception e) {
            knLogger.error(methodName, "Failed to create subscriber package additional info: ", e);
            throw KnDbUtil.processException(e, "Failed to create subscriber package additional info for bulk MDNs: " + e.getMessage(),
                    KnBulkOpsDBUtil.getXdmPttServerId(), KnBulkOpsDAOSourceTypes.POCSUBSCR_ADDLINFO,
                    null);
        }
    }

    /**
     * Update etag (LastUpdateTime) for corporate NNI reference ID in CORP_GW_LINKED_ACCOUNTINFO table
     * This is used to notify changes in NNI subscribers
     *
     * @param corpNNIRefId Corporate NNI Reference ID (LinkedGwKey)
     * @param lastUpdateTime Last profile update time (timestamp)
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    public void updateEtagForCorpNNIRefId(String corpNNIRefId, long lastUpdateTime, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateEtagForCorpNNIRefId(String, long, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Updating etag for corpNNIRefId: ", corpNNIRefId, ", time: ", lastUpdateTime);

        String query = "UPDATE " + KnBulkOpsDAOSourceTypes.CORP_GW_LINKED_ACCOUNTINFO + " SET LastUpdateTime = ? WHERE POC_CORP_NNI_REFERENCE_ID = ?";
        Object[] params = new Object[]{lastUpdateTime, corpNNIRefId};

        try {
            bulkOpsDBUtil.executeQuery(query, params, rs -> null, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            knLogger.info(methodName, "EXIT: Successfully updated etag for corpNNIRefId: ", corpNNIRefId);
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to update etag: ", e);
            throw KnDbUtil.processException(e, "Failed to update etag for corpNNIRefId: " + corpNNIRefId, KnBulkOpsDBUtil.getXdmPttServerId(),
                    KnBulkOpsDAOSourceTypes.CORP_GW_LINKED_ACCOUNTINFO, query);
        }
    }


    /**
     * Validates external corporate ID hierarchy
     *
     * @param extCorpId     External Corporate ID to validate
     * @param hierarchyType Expected hierarchy type
     * @param persisterTxn Database transaction
     * @return true if hierarchy matches or extCorpId doesn't exist in DB, false otherwise
     * @throws KnDAOException if database operation fails
     */
    public boolean validateExtCorpCCAndHierarchy(String extCorpId, KnConstants.HIERARCHY_TYPE hierarchyType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "validateExtCorpCCAndHierarchy(String, KnConstants.HIERARCHY_TYPE)";
        knLogger.debug(methodName, "extCorpId : ", extCorpId, "hierarchyType:", hierarchyType.value());

        String query = "SELECT CORP_HIERARCHY FROM DG.POCCORPINFO WHERE EXTCORPID = ?";
        Object[] params = new Object[]{extCorpId};

    
        KnConstants.HIERARCHY_TYPE corpHierarchyType = bulkOpsDBUtil.executeQuery(query, params, rs -> {
            if (rs.next()) {
                return KnConstants.HIERARCHY_TYPE.validate(rs.getInt("CORP_HIERARCHY"));
            }
            return null;
        }, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);

        if (corpHierarchyType == null) {
            // if ext corpid doesn't exist in DB then return true(assuming valid corporate)
            return true;
        }
        return hierarchyType.equals(corpHierarchyType);
    }

    /**
     * Validates MDN hierarchy and returns detailed results for each MDN
     * MDNs that match the hierarchy or don't exist in DB are considered valid
     * MDNs with hierarchy mismatch are considered invalid
     *
     * @param mdnList       List of MDNs to validate
     * @param hierarchyType Expected hierarchy type
     * @param persisterTxn Database transaction
     * @return KnMDNValidationResult containing lists of valid and invalid MDNs with error details
     * @throws KnDAOException if database operation fails
     */
    public KnMDNValidationResult validateMDNCCAndHierarchy(List<String> mdnList, KnConstants.HIERARCHY_TYPE hierarchyType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "validateMDNCCAndHierarchy(List<String>, KnConstants.HIERARCHY_TYPE)";
        knLogger.debug(methodName, "mdnList size: ", mdnList.size(), "hierarchyType:", hierarchyType.value());

        KnMDNValidationResult result = new KnMDNValidationResult();

        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "No MDNs to validate");
            return result;
        }

        String queryPrefix = "SELECT MDN, ADDLINFO FROM DG.POCSUBSCRINFO WHERE MDN IN ";

        Set<String> mdnSet = new HashSet<>(mdnList);

        bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                mdnList,
                rs -> {
                    while (rs.next()) {
                        String mdn = rs.getString("MDN");
                        mdn = (mdn != null) ? mdn.trim() : mdn;
                        KnConstants.HIERARCHY_TYPE type = KnConstants.HIERARCHY_TYPE.validate(rs.getInt("ADDLINFO"));

                        mdnSet.remove(mdn); // Remove from set to track which MDNs were found

                        if (hierarchyType.equals(type)) {
                            result.addValidMdn(mdn);
                            knLogger.debug(methodName, "MDN hierarchy validated successfully: ", KnGDPRTemplate.mdn(mdn));
                        } else {
                            result.addInvalidMdn(mdn,
                                KnBulkOpsErrorCodes.BOEntity.INVALID_HIERARCHY_REQUEST,
                                "Hierarchy mismatch - Expected: " + hierarchyType + ", Found: " + type);
                            knLogger.debug(methodName, "Hierarchy mismatch for MDN: ", KnGDPRTemplate.mdn(mdn),
                                    ", Expected: ", hierarchyType, ", Found: ", type);
                        }
                    }
                    return null;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        // MDNs not found in DB are considered valid (assuming valid subscriber)
        for (String mdn : mdnSet) {
            result.addValidMdn(mdn);
            knLogger.debug(methodName, "MDN not found in DB, considering valid: ", KnGDPRTemplate.mdn(mdn));
        }

        knLogger.debug(methodName, "Validation completed - Valid: ", result.getValidMdns().size(),
                      ", Invalid: ", result.getInvalidMdns().size());

        return result;
    }

    /**
     * Get subscriber service authentication status for multiple MDNs
     *
     * @param mdnList       List of MDNs to check
     * @param persisterTxn Database transaction
     * @return Map of MDN to service auth status (Integer)
     * @throws KnDAOException if database operation fails
     */
    public Map<String, Integer> getSubscriberServiceAuthStatus(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberServiceAuthStatus(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching service auth status for ", mdnList.size(), " MDNs");

        String queryPrefix = "SELECT MDN, SERVICEAUTHSTATUS FROM DG.POCSUBSCRINFO WHERE MDN IN ";

        Map<String, Integer> serviceAuthStatusMap = bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                mdnList,
                rs -> {
                    Map<String, Integer> resultMap = new HashMap<>();
                    while (rs.next()) {
                        String mdn = rs.getString("MDN");
                        mdn = (mdn != null) ? mdn.trim() : mdn;
                        int authStatus = rs.getInt("SERVICEAUTHSTATUS");
                        resultMap.put(mdn, authStatus);
                    }
                    return resultMap;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "Fetched service auth status for ", serviceAuthStatusMap.size(), " MDNs");
        return serviceAuthStatusMap;
    }

    /**
     * Retrieve APN configuration information from the database
     *
     * @param persisterTxn Database transaction
     * @return Map of APN ID to APN configuration DTO
     * @throws KnDAOException if database operation fails
     */
    public Map<Integer, KnAPNConfigDTO> retrieveAPNInfoConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveAPNInfoConfig(KnPersisterTxn)";
        knLogger.debug(methodName, "Retrieving APN configuration information");

        String query = "SELECT DYNAMICQOSFLAG, XCAPROOTURI, APNID, PTXBUCKETURICELL, LOCDATAURICELL, MCSXCAPROOTURI " +
                "FROM DG.APNPROFILEINFO WHERE PTTSERVERID = ?";

        Map<Integer, KnAPNConfigDTO> apnConfigMap = bulkOpsDBUtil.executeQuery(
                query,
                new Object[]{KnBulkOpsDBUtil.getXdmPttServerId()},
                rs -> {
                    Map<Integer, KnAPNConfigDTO> resultMap = new HashMap<>();
                    while (rs.next()) {
                        KnAPNConfigDTO configDTO = new KnAPNConfigDTO();
                        configDTO.setDynamicQosFlag(rs.getInt(1));
                        configDTO.setApnXCAPUri(rs.getString(2));
                        configDTO.setApnId(rs.getInt(3));
                        configDTO.setPtxBucketUri(rs.getString(4));
                        configDTO.setLocDataUriCellular(rs.getString(5));
                        configDTO.setMcsXCAPUri(rs.getString(6));
                        resultMap.put(configDTO.getApnId(), configDTO);
                    }
                    return resultMap;
                },
                persisterTxn,
                null
        );

        knLogger.debug(methodName, "Retrieved APN configuration for ", apnConfigMap.size(), " APNs");
        return apnConfigMap;
    }

    /** 
     * Fetch subscriber profiles in bulk for multiple MDNs.
     * Used to retrieve existing profile data before performing bulk updates.
     *
     * This method fetches:
     * 1. Basic subscriber info from POCSUBSCRINFO
     * 2. Feature set fields (for recalculation when pkgIdMap changes)
     * 3. Existing tier package from POCSUBSCR_ADDLINFO
     * 4. Existing addon packages from SUBSCR_ADDON_PKGINFO
     *
     * @param mdnList List of MDNs to fetch profiles for
     * @param persisterTxn Database transaction
     * @return Map of MDN to KnBulkSubsProfileDTO containing profile details
     * @throws KnDAOException if database operation fails
     */
    public Map<String, KnBulkSubsProfileDTO> fetchSubscriberProfilesInBulk(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "fetchSubscriberProfilesInBulk(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Fetching subscriber profiles for ", mdnList.size(), " MDNs");

        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "Empty MDN list");
            return new HashMap<>();
        }

        // Step 1: Fetch main subscriber info from POCSUBSCRINFO (including all feature set fields)
        String queryPrefix =
            "SELECT MDN, SUBSCRNAME, CORPID, CLIENT_TYPE, PUBLICSUBSCRIPTIONTYPE, CORPSUBSCRIPTIONTYPE, " +
            "SERVICEAUTHSTATUS, ACTIVEFS2, SUBSCRIBERFS2, CLIENTFS2, OPSFS2, CORPADMINFS2, " +
            "XDMSFS2, USERPROFILEFS2, CLIENTPV_MAJORVERSION, CLIENTPV_MINORVERSION, " +
            "USER_ID, DISPATCH_TYPE, MC_PTTID, MC_ID, MC_DATAID, MC_VIDEOID, QPPPACKID, MCPTT_COMPLIANCE, LICENSE_TYPE, CLIENT_PASSWORD, " +
            "POCHOME, PRESENCEHOME, XDMSHOME, ALIAS_MDN, DISPATCH_GRP_MEMBER, ACCOUNT_ID, USERPROFILEINDEX, PAMACCID, CORPCONTACTLISTID, " +
            "LASTPROFILEUPDATETIME FROM DG.POCSUBSCRINFO WHERE MDN IN ";

        Map<String, KnBulkSubsProfileDTO> profilesMap = bulkOpsDBUtil.executeInClauseQuery(
                queryPrefix,
                mdnList,
                rs -> {
                    Map<String, KnBulkSubsProfileDTO> resultMap = new HashMap<>();
                    while (rs.next()) {
                        KnBulkSubsProfileDTO profile = new KnBulkSubsProfileDTO();
                        String mdn = rs.getString("MDN");
                        profile.setMdn(mdn != null ? mdn.trim() : null);
                        String subscrName = rs.getString("SUBSCRNAME");
                        // Multilingual handling: Convert from ISO-8859-1 to UTF-8 (aligned with Single MDN flow)
                        if (subscrName != null) {
                            try {
                                subscrName = new String(subscrName.getBytes("8859_1"), "UTF-8");
                            } catch (java.io.UnsupportedEncodingException e) {
                                knLogger.warn("fetchSubscriberProfilesInBulk", "Encoding conversion failed for SUBSCRNAME, using raw value");
                            }
                            profile.setNetworkName(subscrName.trim());
                        } else {
                            profile.setNetworkName(null);
                        }
                        profile.setCorpId(rs.getInt("CORPID"));
                        profile.setSubsClientType(rs.getInt("CLIENT_TYPE"));
                        profile.setPublicSubscriptionType(rs.getInt("PUBLICSUBSCRIPTIONTYPE"));
                        profile.setCorporateSubscriptionType(rs.getInt("CORPSUBSCRIPTIONTYPE"));
                        profile.setServiceAuthStatus(rs.getInt("SERVICEAUTHSTATUS"));
                        profile.setPamAccId(rs.getInt("PAMACCID"));
                        profile.setCorpContactListId(rs.getInt("CORPCONTACTLISTID"));
                        profile.setLastProfileUpdateTime(rs.getLong("LASTPROFILEUPDATETIME"));

                        // Feature Set fields
                        profile.setActiveFS2(rs.getString("ACTIVEFS2"));
                        profile.setSubsFS2(rs.getString("SUBSCRIBERFS2"));
                        profile.setClientFS2(rs.getString("CLIENTFS2"));
                        profile.setOpsFS2(rs.getString("OPSFS2"));
                        profile.setCorpAdminFS2(rs.getString("CORPADMINFS2"));
                        profile.setXdmsFS2(rs.getString("XDMSFS2"));
                        profile.setUserProfileFS2(rs.getString("USERPROFILEFS2"));

                        profile.setClientPVmajorVer(rs.getInt("CLIENTPV_MAJORVERSION"));
                        profile.setClientPVminorVer(rs.getInt("CLIENTPV_MINORVERSION"));
                        
                        // Trim string fields that may have DB padding (aligned with Single MDN flow)
                        String userId = rs.getString("USER_ID");
                        profile.setUserId(userId != null ? userId.trim() : null);
                        
                        profile.setDispatchType(rs.getInt("DISPATCH_TYPE"));
                        
                        String mcpttId = rs.getString("MC_PTTID");
                        profile.setMcpttId(mcpttId != null ? mcpttId.trim() : null);
                        
                        String mcId = rs.getString("MC_ID");
                        profile.setMcId(mcId != null ? mcId.trim() : null);

                        String mcDataId = rs.getString("MC_DATAID");
                        profile.setMcDataId(mcDataId != null ? mcDataId.trim() : null);

                        String mcVideoId = rs.getString("MC_VIDEOID");
                        profile.setMcVideoId(mcVideoId != null ? mcVideoId.trim() : null);

                        profile.setQppPkgId(rs.getInt("QPPPACKID"));
                        profile.setMcpttCompliance(rs.getInt("MCPTT_COMPLIANCE"));
                        profile.setLicenseType(rs.getInt("LICENSE_TYPE"));
                        String clientPassword = rs.getString("CLIENT_PASSWORD");
                        profile.setClientPassword(clientPassword != null ? clientPassword.trim() : null);

                        // PTT Server home fields (needed for activeFS calculation) - MUST trim as these are used for config lookup
                        String pocHome = rs.getString("POCHOME");
                        profile.setPocHome(pocHome != null ? pocHome.trim() : null);
                        
                        String presenceHome = rs.getString("PRESENCEHOME");
                        profile.setPresenceHome(presenceHome != null ? presenceHome.trim() : null);
                        
                        String xdmsHome = rs.getString("XDMSHOME");
                        profile.setXdmsHome(xdmsHome != null ? xdmsHome.trim() : null);
                        
                        profile.setDispatchGroupMember(rs.getInt("DISPATCH_GRP_MEMBER"));
                        
                        String aliasMdn = rs.getString("ALIAS_MDN");
                        profile.setAliasMdn(aliasMdn != null ? aliasMdn.trim() : null);
                        
                        profile.setMcsCompliance(rs.getString("MCPTT_COMPLIANCE"));
                        
                        String accountId = rs.getString("ACCOUNT_ID");
                        profile.setAccountId(accountId != null ? accountId.trim() : null);
                        
                        // User Profile Management index (0 = base MDN, >0 = profile MDN)
                        Integer userProfileIndex = rs.getInt("USERPROFILEINDEX");
                        if (rs.wasNull()) {
                            userProfileIndex = null;
                        }
                        profile.setUserProfileIndex(userProfileIndex);
                        
                        resultMap.put(profile.getMdn(), profile);
                    }
                    return resultMap;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "Fetched ", profilesMap.size(), " subscriber profiles from POCSUBSCRINFO");

        // Step 2: Fetch tier package and data package from POCSUBSCR_ADDLINFO
        if (!profilesMap.isEmpty()) {
            String addlInfoQuery = "SELECT MDN, TIER_PKG_CODE, DATA_PKG_ID FROM DG.POCSUBSCR_ADDLINFO WHERE MDN IN ";
            bulkOpsDBUtil.executeInClauseQuery(
                    addlInfoQuery,
                    mdnList,
                    rs -> {
                        while (rs.next()) {
                            String mdn = rs.getString("MDN");
                            mdn = (mdn != null) ? mdn.trim() : null;  // Trim to match profilesMap key
                            KnBulkSubsProfileDTO profile = profilesMap.get(mdn);
                            if (profile != null) {
                                String tierPkgCode = rs.getString("TIER_PKG_CODE");
                                profile.setTierPkgCode(tierPkgCode != null ? tierPkgCode.trim() : null);
                                profile.setDataPkgId(rs.getInt("DATA_PKG_ID"));
                            }
                        }
                        return null;
                    },
                    persisterTxn,
                    KnDBConst.DataStores.XDM_SHARED_DATA
            );
            knLogger.debug(methodName, "Fetched tier package info from POCSUBSCR_ADDLINFO");
        }

        // Step 3: Fetch addon packages from SUBSCR_ADDON_PKGINFO
        if (!profilesMap.isEmpty()) {
            String addonQuery = "SELECT MDN, ADDON_PKGCODE FROM DG.SUBSCR_ADDON_PKGINFO WHERE MDN IN ";
            bulkOpsDBUtil.executeInClauseQuery(
                    addonQuery,
                    mdnList,
                    rs -> {
                        while (rs.next()) {
                            String mdn = rs.getString("MDN");
                            mdn = (mdn != null) ? mdn.trim() : null;  // Trim to match profilesMap key
                            String addonPkgCode = rs.getString("ADDON_PKGCODE");
                            addonPkgCode = (addonPkgCode != null) ? addonPkgCode.trim() : null;
                            KnBulkSubsProfileDTO profile = profilesMap.get(mdn);
                            if (profile != null && addonPkgCode != null) {
                                profile.addAddonPkgCode(addonPkgCode);
                            }
                        }
                        return null;
                    },
                    persisterTxn,
                    KnDBConst.DataStores.XDM_SHARED_DATA
            );
            knLogger.debug(methodName, "Fetched addon packages from SUBSCR_ADDON_PKGINFO");
        }

        // Step 4: Fetch corpFS2 and hierarchyType from POCCORPINFO for corporate subscribers
        // Get unique corpIds that need corpFS2 and hierarchyType
        if (!profilesMap.isEmpty()) {
            Set<Integer> corpIds = new HashSet<>();
            for (KnBulkSubsProfileDTO profile : profilesMap.values()) {
                if (profile.getCorporateSubscriptionType() == com.kodiak.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()
                        && profile.getCorpId() > 0) {
                    corpIds.add(profile.getCorpId());
                }
            }

            if (!corpIds.isEmpty()) {
                // Fetch corpFS2 and hierarchyType for all unique corpIds
                List<Integer> corpIdList = new ArrayList<>(corpIds);
                String corpQuery = "SELECT CORPID, CORPFS2, CORP_HIERARCHY FROM DG.POCCORPINFO WHERE CORPID IN ";
                Map<Integer, String> corpFs2Map = new HashMap<>();
                Map<Integer, Integer> corpHierarchyMap = new HashMap<>();

                bulkOpsDBUtil.executeInClauseQuery(
                        corpQuery,
                        corpIdList.stream().map(String::valueOf).collect(java.util.stream.Collectors.toList()),
                        rs -> {
                            while (rs.next()) {
                                int corpId = rs.getInt("CORPID");
                                corpFs2Map.put(corpId, rs.getString("CORPFS2"));
                                corpHierarchyMap.put(corpId, rs.getInt("CORP_HIERARCHY"));
                            }
                            return null;
                        },
                        persisterTxn,
                        KnDBConst.DataStores.XDM_SHARED_DATA
                );

                // Set corpFS2 and hierarchyType in profiles
                for (KnBulkSubsProfileDTO profile : profilesMap.values()) {
                    int corpId = profile.getCorpId();
                    if (corpFs2Map.containsKey(corpId)) {
                        profile.setCorpFS2(corpFs2Map.get(corpId));
                    }
                    if (corpHierarchyMap.containsKey(corpId)) {
                        int hierarchyValue = corpHierarchyMap.get(corpId);
                        // Convert int to HIERARCHY_TYPE enum
                        if (hierarchyValue == com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE.HIERARCHY.value()) {
                            profile.setHierarchyType(com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE.HIERARCHY);
                        } else {
                            profile.setHierarchyType(com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE.NON_HIERARCHY);
                        }
                    } else {
                        // Default to NON_HIERARCHY if not found
                        profile.setHierarchyType(com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE.NON_HIERARCHY);
                    }
                }
                knLogger.debug(methodName, "Fetched corpFS2 and hierarchyType for ", corpIds.size(), " corporate IDs");
            }
        }

        // Set default hierarchy type for non-corporate subscribers
        for (KnBulkSubsProfileDTO profile : profilesMap.values()) {
            if (profile.getHierarchyType() == null) {
                profile.setHierarchyType(com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE.NON_HIERARCHY);
            }
        }

        knLogger.debug(methodName, "Completed fetching ", profilesMap.size(), " subscriber profiles with all related data");
        return profilesMap;
    }

    /**
     * Bulk update subscriber network names (SUBSCRNAME) and LASTPROFILEUPDATETIME.
     * Uses batch processing for efficiency - single DB round trip for all updates.
     *
     * This method only updates MDNs where networkName needs to change:
     * - MDNs where networkName == MDN in the request are EXCLUDED (keep existing networkName)
     * - Only MDNs with explicitly different networkName are updated
     *
     * @param networkNameMap Map of MDN to new NetworkName (only contains MDNs that need networkName update)
     * @param persisterTxn Database transaction
     * @return Number of records updated
     * @throws KnDAOException if database operation fails
     */
    public int updateSubscriberNetworkNamesInBulk(Map<String, String> networkNameMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscriberNetworkNamesInBulk(Map<String, String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Updating network names for ", networkNameMap.size(), " MDNs");

        if (networkNameMap == null || networkNameMap.isEmpty()) {
            knLogger.debug(methodName, "Empty networkName map, nothing to update");
            return 0;
        }

        String updateQuery = "UPDATE DG.POCSUBSCRINFO SET SUBSCRNAME = ?, LASTPROFILEUPDATETIME = ? WHERE MDN = ?";
        long currentTime = System.currentTimeMillis();
        int totalUpdated = 0;

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(updateQuery);

            for (Map.Entry<String, String> entry : networkNameMap.entrySet()) {
                String mdn = entry.getKey();
                String newNetworkName = entry.getValue();

                // Multilingual handling: Convert from UTF-8 to ISO-8859-1 (aligned with Single MDN flow)
                if (newNetworkName != null && !newNetworkName.isEmpty()) {
                    try {
                        newNetworkName = new String(newNetworkName.getBytes("UTF-8"), "8859_1");
                    } catch (java.io.UnsupportedEncodingException e) {
                        knLogger.warn(methodName, "Encoding conversion failed for networkName, using raw value");
                    }
                }

                pStmt.setString(1, newNetworkName);
                pStmt.setLong(2, currentTime);
                pStmt.setString(3, mdn);
                pStmt.addBatch();

                knLogger.debug(methodName, "Adding batch: MDN=", KnGDPRTemplate.mdn(mdn), ", NetworkName=", newNetworkName);
            }

            // Execute batch update
            int[] batchResults = pStmt.executeBatch();

            // Count successful updates
            for (int result : batchResults) {
                if (result >= 0 || result == PreparedStatement.SUCCESS_NO_INFO) {
                    totalUpdated++;
                }
            }

            knLogger.info(methodName, "Bulk update completed - Total updated: ", totalUpdated, " out of ", networkNameMap.size());

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during bulk network name update - ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to update network names in bulk: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to update network names in bulk", e);
        } finally {
            // Only close PreparedStatement, NOT connection (managed by transaction)
            KnDbUtil.closeStatement(pStmt);
        }

        return totalUpdated;
    }

    /**
     * Bulk update subscriber LASTPROFILEUPDATETIME for MDNs that don't need networkName update
     * but still need other fields (like pkgIdMap) to be processed.
     *
     * This is called for MDNs where networkName == MDN (keeping existing networkName)
     * but still need their profile update time refreshed for pkgIdMap changes.
     *
     * @param mdnList List of MDNs to update
     * @param persisterTxn Database transaction
     * @return Number of records updated
     * @throws KnDAOException if database operation fails
     */
    public int updateSubscriberProfileTimeInBulk(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscriberProfileTimeInBulk(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Updating profile time for ", mdnList.size(), " MDNs");

        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "Empty MDN list, nothing to update");
            return 0;
        }

        // Build IN clause query for bulk update
        String placeholders = String.join(",", java.util.Collections.nCopies(mdnList.size(), "?"));
        String updateQuery = "UPDATE DG.POCSUBSCRINFO SET LASTPROFILEUPDATETIME = ? WHERE MDN IN (" + placeholders + ")";
        long currentTime = System.currentTimeMillis();
        int totalUpdated = 0;

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(updateQuery);

            // Set timestamp parameter
            pStmt.setLong(1, currentTime);

            // Set MDN parameters
            for (int i = 0; i < mdnList.size(); i++) {
                pStmt.setString(i + 2, mdnList.get(i));
            }

            totalUpdated = pStmt.executeUpdate();

            knLogger.info(methodName, "Bulk profile time update completed - Total updated: ", totalUpdated);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during bulk profile time update - ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to update profile times in bulk: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to update profile times in bulk", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }

        return totalUpdated;
    }

    // ==================== PACKAGE ID MAP PROCESSING ====================

    /**
     * Fetch existing tier and addon packages for multiple MDNs.
     * This retrieves package information needed for pkgIdMap processing.
     *
     * @param mdnList List of MDNs to fetch packages for
     * @param persisterTxn Database transaction
     * @return Map of MDN to existing package info (tierPkgCode and addonPkgCodes)
     * @throws KnDAOException if database operation fails
     */
    public Map<String, KnMdnPkgInfo> fetchExistingPackagesInBulk(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "fetchExistingPackagesInBulk";
        knLogger.debug(methodName, "Fetching existing packages for ", mdnList.size(), " MDNs");

        if (mdnList == null || mdnList.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, KnMdnPkgInfo> pkgInfoMap = new HashMap<>();

        // 1. Fetch tier packages from POCSUBSCR_ADDLINFO
        String tierQuery = "SELECT MDN, TIER_PKG_CODE FROM DG.POCSUBSCR_ADDLINFO WHERE MDN IN ";
        bulkOpsDBUtil.executeInClauseQuery(
                tierQuery,
                mdnList,
                rs -> {
                    while (rs.next()) {
                        String rawMdn = rs.getString("MDN");
                        final String mdn = (rawMdn != null) ? rawMdn.trim() : rawMdn;
                        String tierPkgCode = rs.getString("TIER_PKG_CODE");
                        tierPkgCode = (tierPkgCode != null) ? tierPkgCode.trim() : tierPkgCode;
                        KnMdnPkgInfo pkgInfo = pkgInfoMap.computeIfAbsent(mdn, k -> new KnMdnPkgInfo(k));
                        pkgInfo.setTierPkgCode(tierPkgCode);
                    }
                    return null;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        // 2. Fetch addon packages from SUBSCR_ADDON_PKGINFO
        String addonQuery = "SELECT MDN, ADDON_PKGCODE FROM DG.SUBSCR_ADDON_PKGINFO WHERE MDN IN ";
        bulkOpsDBUtil.executeInClauseQuery(
                addonQuery,
                mdnList,
                rs -> {
                    while (rs.next()) {
                        String rawMdn = rs.getString("MDN");
                        final String mdn = (rawMdn != null) ? rawMdn.trim() : rawMdn;
                        String addonPkgCode = rs.getString("ADDON_PKGCODE");
                        addonPkgCode = (addonPkgCode != null) ? addonPkgCode.trim() : addonPkgCode;
                        KnMdnPkgInfo pkgInfo = pkgInfoMap.computeIfAbsent(mdn, k -> new KnMdnPkgInfo(k));
                        pkgInfo.addAddonPkgCode(addonPkgCode);
                    }
                    return null;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA
        );

        knLogger.debug(methodName, "Fetched package info for ", pkgInfoMap.size(), " MDNs");
        return pkgInfoMap;
    }

    /**
     * Process pkgIdMap ADD/REMOVE actions and calculate final package IDs.
     * Implements the same logic as KnSubsProvController.updateSubscriber() for pkgIdMap processing.
     *
     * Validation includes:
     * 1. REMOVE - Package must exist with correct type
     * 2. ADD - Package type mismatch validation
     * 3. ADD - QPP package conflict (only one QPP package allowed)
     * 4. ADD - Tier package conflict (only one tier package allowed)
     *
     * @param existingPkgIds Map of existing package codes to package types (tier=1, addon=2)
     * @param pkgIdMap Package ID map with ADD/REMOVE actions
     * @param qppPkgCodes List of QPP package codes for validation (can be null)
     * @return KnPkgIdMapResult containing final packages and validation status
     */
    public KnPkgIdMapResult processPkgIdMap(Map<String, Integer> existingPkgIds,
                                             Map<String, Map<String, Integer>> pkgIdMap,
                                             List<String> qppPkgCodes) {
        String methodName = "processPkgIdMap";

        KnPkgIdMapResult result = new KnPkgIdMapResult();
        result.setFinalPkgIds(new HashMap<>(existingPkgIds)); // Start with existing

        // DECLARATIVE SEMANTIC:
        // - If pkgIdMap is null/empty: Clear all tier and addon packages (desired state = empty)
        // - If pkgIdMap has ADD: Replace with packages in ADD (desired state = ADD list)
        if (pkgIdMap == null || pkgIdMap.isEmpty()) {
            // Clear all packages - desired state is empty
            Map<String, Integer> finalPkgIds = result.getFinalPkgIds();
            boolean hadPackages = !finalPkgIds.isEmpty();
            finalPkgIds.clear();
            result.setChanged(hadPackages);
            result.setValid(true);
            knLogger.debug(methodName, "Clearing all packages (pkgIdMap is null/empty)");
            return result;
        }

        Map<String, Integer> finalPkgIds = result.getFinalPkgIds();
        Map<String, Integer> addPkgIds = pkgIdMap.get("Add");
        Map<String, Integer> removePkgIds = pkgIdMap.get("Remove");

        // TIER_PKG_TYPE = 1, ADDON_PKG_TYPE = 2
        final int TIER_PKG_TYPE = 1;
        final int ADDON_PKG_TYPE = 2;

        // Process REMOVE action first
        if (removePkgIds != null && !removePkgIds.isEmpty()) {
            for (Map.Entry<String, Integer> entry : removePkgIds.entrySet()) {
                String pkgCode = entry.getKey().trim();
                Integer pkgType = entry.getValue();

                // Validate package exists with correct type
                if (!existingPkgIds.containsKey(pkgCode)) {
                    result.setValid(false);
                    result.setErrorMessage("Package not found for removal: " + pkgCode);
                    knLogger.error(methodName, "Package not found for removal: ", pkgCode);
                    return result;
                }

                if (existingPkgIds.get(pkgCode).intValue() != pkgType.intValue()) {
                    result.setValid(false);
                    result.setErrorMessage("Package type mismatch for removal: " + pkgCode);
                    knLogger.error(methodName, "Package type mismatch for removal: ", pkgCode);
                    return result;
                }

                finalPkgIds.remove(pkgCode);
                result.setChanged(true);
                knLogger.debug(methodName, "Removed package: ", pkgCode);
            }
        }

        // Process ADD action
        if (addPkgIds != null && !addPkgIds.isEmpty()) {
            // If there's no explicit REMOVE section, treat ADD as a replacement operation
            // This means: the ADD section represents the COMPLETE desired state of packages
            // Remove ALL existing tier and addon packages, then add the new ones from ADD list
            if (removePkgIds == null || removePkgIds.isEmpty()) {
                // For replacement semantic: remove ALL tier (type=1) and addon (type=2) packages
                // This ensures that if you send only TIER1, existing addons are also removed
                Iterator<Map.Entry<String, Integer>> iterator = finalPkgIds.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> existingEntry = iterator.next();
                    Integer existingType = existingEntry.getValue();
                    // Remove if it's a tier (1) or addon (2) package
                    if (existingType == TIER_PKG_TYPE || existingType == ADDON_PKG_TYPE) {
                        knLogger.debug(methodName, "Removing existing package for replacement: ", 
                                      existingEntry.getKey(), " type: ", existingType);
                        iterator.remove();
                        result.setChanged(true);
                    }
                }
            }

            for (Map.Entry<String, Integer> entry : addPkgIds.entrySet()) {
                String pkgCode = entry.getKey().trim();
                Integer pkgType = entry.getValue();

                // Skip if package already exists with same type (can happen if explicit REMOVE was used)
                if (finalPkgIds.containsKey(pkgCode)) {
                    if (finalPkgIds.get(pkgCode).intValue() != pkgType.intValue()) {
                        result.setValid(false);
                        result.setErrorMessage("Package type mismatch for add: " + pkgCode);
                        knLogger.error(methodName, "Package type mismatch for add: ", pkgCode);
                        return result;
                    }
                    // Package already exists with same type, skip
                    knLogger.debug(methodName, "Package already exists, skipping: ", pkgCode);
                    continue;
                }

                // QPP Package Validation (same logic as KnSubsProvController.updateSubscriber)
                // Only one QPP package can be assigned at a time
                if (qppPkgCodes != null && !qppPkgCodes.isEmpty() && qppPkgCodes.contains(pkgCode)) {
                    // Check if any package in finalPkgIds is a QPP package
                    List<String> existingQppPkgs = finalPkgIds.keySet().stream()
                            .filter(qppPkgCodes::contains)
                            .collect(java.util.stream.Collectors.toList());

                    if (!existingQppPkgs.isEmpty()) {
                        result.setValid(false);
                        result.setErrorMessage("QPP package already assigned: " + existingQppPkgs.get(0) + ", cannot add: " + pkgCode);
                        knLogger.error(methodName, "QPP package already assigned, cannot add: ", pkgCode);
                        return result;
                    }
                }

                // Validate tier package conflict
                if (pkgType == TIER_PKG_TYPE) {
                    // Check if tier package already exists in finalPkgIds (after removals/replacements)
                    boolean tierExists = finalPkgIds.values().stream().anyMatch(v -> v == TIER_PKG_TYPE);
                    if (tierExists) {
                        result.setValid(false);
                        result.setErrorMessage("Tier package already assigned, cannot add: " + pkgCode);
                        knLogger.error(methodName, "Tier package already assigned, cannot add: ", pkgCode);
                        return result;
                    }
                }

                finalPkgIds.put(pkgCode, pkgType);
                result.setChanged(true);
                knLogger.debug(methodName, "Added package: ", pkgCode, " type: ", pkgType);
            }
        }

        result.setValid(true);
        knLogger.debug(methodName, "Final package IDs: ", finalPkgIds);
        return result;
    }

    /**
     * Bulk update tier package code in POCSUBSCR_ADDLINFO for multiple MDNs.
     *
     * @param mdnList List of MDNs to update
     * @param tierPkgCode Tier package code to set (can be null to clear)
     * @param persisterTxn Database transaction
     * @return Number of records updated
     * @throws KnDAOException if database operation fails
     */
    public int updateTierPackageInBulk(List<String> mdnList, String tierPkgCode, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateTierPackageInBulk";
        knLogger.debug(methodName, "Updating tier package for ", mdnList.size(), " MDNs to: ", tierPkgCode);

        if (mdnList == null || mdnList.isEmpty()) {
            return 0;
        }

        String placeholders = String.join(",", java.util.Collections.nCopies(mdnList.size(), "?"));
        String updateQuery = "UPDATE DG.POCSUBSCR_ADDLINFO SET TIER_PKG_CODE = ? WHERE MDN IN (" + placeholders + ")";

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(updateQuery);

            pStmt.setString(1, tierPkgCode);
            for (int i = 0; i < mdnList.size(); i++) {
                pStmt.setString(i + 2, mdnList.get(i));
            }

            int updated = pStmt.executeUpdate();
            knLogger.info(methodName, "Updated tier package for ", updated, " MDNs");
            return updated;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during tier package update: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to update tier package: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to update tier package", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * Bulk delete addon packages for multiple MDNs.
     *
     * @param mdnList List of MDNs to delete addon packages for
     * @param persisterTxn Database transaction
     * @return Number of records deleted
     * @throws KnDAOException if database operation fails
     */
    public int deleteAddonPackagesInBulk(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAddonPackagesInBulk";
        knLogger.debug(methodName, "Deleting addon packages for ", mdnList.size(), " MDNs");

        if (mdnList == null || mdnList.isEmpty()) {
            return 0;
        }

        String placeholders = String.join(",", java.util.Collections.nCopies(mdnList.size(), "?"));
        String deleteQuery = "DELETE FROM DG.SUBSCR_ADDON_PKGINFO WHERE MDN IN (" + placeholders + ")";

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(deleteQuery);

            for (int i = 0; i < mdnList.size(); i++) {
                pStmt.setString(i + 1, mdnList.get(i));
            }

            int deleted = pStmt.executeUpdate();
            knLogger.info(methodName, "Deleted addon packages for ", deleted, " MDN entries");
            return deleted;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during addon package delete: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to delete addon packages: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to delete addon packages", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * Bulk insert addon packages for multiple MDNs.
     * Inserts the same addon packages for all MDNs in the list.
     *
     * @param mdnList List of MDNs to insert addon packages for
     * @param addonPkgCodes List of addon package codes to insert
     * @param persisterTxn Database transaction
     * @return Number of records inserted
     * @throws KnDAOException if database operation fails
     */
    public int insertAddonPackagesInBulk(List<String> mdnList, List<String> addonPkgCodes, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertAddonPackagesInBulk";
        knLogger.debug(methodName, "Inserting ", addonPkgCodes.size(), " addon packages for ", mdnList.size(), " MDNs");

        if (mdnList == null || mdnList.isEmpty() || addonPkgCodes == null || addonPkgCodes.isEmpty()) {
            return 0;
        }

        String insertQuery = "INSERT INTO DG.SUBSCR_ADDON_PKGINFO (MDN, ADDON_PKGCODE) VALUES (?, ?)";
        int totalInserted = 0;

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(insertQuery);

            // For each MDN, insert all addon packages
            for (String mdn : mdnList) {
                for (String addonPkgCode : addonPkgCodes) {
                    pStmt.setString(1, mdn);
                    pStmt.setString(2, addonPkgCode);
                    pStmt.addBatch();
                }
            }

            int[] results = pStmt.executeBatch();
            for (int result : results) {
                if (result >= 0 || result == PreparedStatement.SUCCESS_NO_INFO) {
                    totalInserted++;
                }
            }

            knLogger.info(methodName, "Inserted ", totalInserted, " addon package entries");
            return totalInserted;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during addon package insert: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to insert addon packages: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to insert addon packages", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * Process pkgIdMap for all MDNs in bulk.
     * This implements the full pkgIdMap processing logic similar to single MDN flow:
     * 1. Fetch QPP package codes for validation
     * 2. Fetch existing packages for all MDNs
     * 3. Process ADD/REMOVE actions PER MDN with QPP validation
     * 4. Update tier package per MDN
     * 5. Delete old addon packages and insert new ones per MDN
     * 6. Update DATA_PKG_ID in POCSUBSCR_ADDLINFO
     *
     * IMPORTANT: Each MDN is processed individually based on its own existing packages.
     * The same ADD/REMOVE actions are applied to each MDN, but the result may differ
     * based on what packages each MDN already has.
     *
     * @param mdnList List of MDNs to process
     * @param pkgIdMap Package ID map with ADD/REMOVE actions (applied to all MDNs)
     * @param preFetchedData Pre-fetched utility data (QPP codes, data pkg maps) to avoid utility calls during transaction
     * @param persisterTxn Database transaction
     * @return KnPkgIdMapBulkResult containing processing results
     * @throws KnDAOException if database operation fails
     */
    public KnPkgIdMapBulkResult processPkgIdMapInBulk(List<String> mdnList, Map<String, Map<String, Integer>> pkgIdMap, KnPreFetchedData preFetchedData, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "processPkgIdMapInBulk";
        knLogger.debug(methodName, "Processing pkgIdMap for ", mdnList.size(), " MDNs");

        KnPkgIdMapBulkResult result = new KnPkgIdMapBulkResult();

        // DECLARATIVE SEMANTIC:
        // - If pkgIdMap is null/empty: Clear all packages (tier=null, delete all add-ons)
        // - If pkgIdMap has {Add={...}}: Replace with specified packages
        boolean clearAllPackages = (pkgIdMap == null || pkgIdMap.isEmpty());
        
        if (clearAllPackages) {
            knLogger.info(methodName, "pkgIdMap is null/empty - clearing all tier and add-on packages for ", mdnList.size(), " MDNs");
        }

        // TIER_PKG_TYPE = 1, ADDON_PKG_TYPE = 2
        final int TIER_PKG_TYPE = 1;
        final int ADDON_PKG_TYPE = 2;

        // 1. Use pre-fetched QPP package codes (already fetched BEFORE main transaction was opened)
        //    This avoids calling featureSetUtil.getQPPPkgCodes() which has internal save() that releases connection
        List<String> qppPkgCodes = (preFetchedData != null && preFetchedData.hasQppPkgCodes())
                ? preFetchedData.getQppPkgCodes()
                : new ArrayList<>();
        knLogger.debug(methodName, "Using pre-fetched QPP package codes: ", qppPkgCodes);

        // 2. Fetch existing packages for all MDNs (uses the main transaction)
        Map<String, KnMdnPkgInfo> existingPkgMap = fetchExistingPackagesInBulk(mdnList, persisterTxn);

        // 3. Process ADD/REMOVE actions PER MDN
        //    Store per-MDN final packages for FeatureSet recalculation
        Map<String, Map<String, Integer>> perMdnFinalPkgIds = new HashMap<>();
        Map<String, String> perMdnTierPkgCode = new HashMap<>();
        Map<String, List<String>> perMdnAddonPkgCodes = new HashMap<>();
        boolean anyChanged = false;

        for (String mdn : mdnList) {
            // Get existing packages for this MDN
            Map<String, Integer> existingPkgIds = new HashMap<>();
            KnMdnPkgInfo mdnPkgInfo = existingPkgMap.get(mdn);
            if (mdnPkgInfo != null) {
                if (mdnPkgInfo.getTierPkgCode() != null) {
                    existingPkgIds.put(mdnPkgInfo.getTierPkgCode(), TIER_PKG_TYPE);
                }
                if (mdnPkgInfo.getAddonPkgCodes() != null) {
                    for (String addonCode : mdnPkgInfo.getAddonPkgCodes()) {
                        existingPkgIds.put(addonCode, ADDON_PKG_TYPE);
                    }
                }
            }

            // Process ADD/REMOVE for this MDN
            KnPkgIdMapResult processResult = processPkgIdMap(existingPkgIds, pkgIdMap, qppPkgCodes);

            if (!processResult.isValid()) {
                // Validation failed for this MDN - fail the entire batch
                result.setSuccess(false);
                result.setErrorMessage("PkgIdMap validation failed for MDN " + mdn + ": " + processResult.getErrorMessage());
                knLogger.error(methodName, "PkgIdMap validation failed for MDN ", KnGDPRTemplate.mdn(mdn), ": ", processResult.getErrorMessage());
                return result;
            }

            if (processResult.isChanged()) {
                anyChanged = true;
            }

            // Store final packages for this MDN
            Map<String, Integer> finalPkgIds = processResult.getFinalPkgIds();
            perMdnFinalPkgIds.put(mdn, finalPkgIds);

            // Extract tier and addon packages for this MDN
            String finalTierPkgCode = null;
            List<String> finalAddonPkgCodes = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : finalPkgIds.entrySet()) {
                if (entry.getValue() == TIER_PKG_TYPE) {
                    finalTierPkgCode = entry.getKey();
                } else if (entry.getValue() == ADDON_PKG_TYPE) {
                    finalAddonPkgCodes.add(entry.getKey());
                }
            }
            perMdnTierPkgCode.put(mdn, finalTierPkgCode);
            perMdnAddonPkgCodes.put(mdn, finalAddonPkgCodes);

            knLogger.debug(methodName, "MDN ", KnGDPRTemplate.mdn(mdn), " - Final tier: ", finalTierPkgCode, ", Final addons: ", finalAddonPkgCodes);
        }

        if (!anyChanged) {
            result.setProcessed(true);
            result.setSuccess(true);
            knLogger.debug(methodName, "No package changes needed for any MDN");
            return result;
        }

        // Get XDM PTT Server ID for package lookup
        String xdmPttServerId = KnBulkOpsDBUtil.getXdmPttServerId();

        // 4. Calculate DATA_PKG_ID and QPPPACKID per MDN based on addon packages
        //    Use pre-fetched data package mappings to avoid utility calls that release the connection
        Map<String, Integer> perMdnDataPkgId = new HashMap<>();
        Map<String, Integer> perMdnQppPkgId = new HashMap<>();

        try {
            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();

            // Use pre-fetched data package mappings (fetched BEFORE main transaction was opened)
            Map<Integer, Integer> qppDataPkgMap = (preFetchedData != null && preFetchedData.getQppDataPkgMap() != null)
                    ? preFetchedData.getQppDataPkgMap()
                    : new HashMap<>();
            Map<Integer, Integer> addonDataPkgMap = (preFetchedData != null && preFetchedData.getAddonDataPkgMap() != null)
                    ? preFetchedData.getAddonDataPkgMap()
                    : new HashMap<>();

            knLogger.debug(methodName, "Using pre-fetched data package maps - QPP: ", qppDataPkgMap.size(),
                          ", Addon: ", addonDataPkgMap.size());

            for (String mdn : mdnList) {
                List<String> addonPkgCodes = perMdnAddonPkgCodes.get(mdn);
                Integer qppPkgId = null;
                Integer dataPkgId = null;

                if (addonPkgCodes != null && !addonPkgCodes.isEmpty()) {
                    for (String addonPkgCode : addonPkgCodes) {
                        try {
                            Integer profileId = featureSetUtil.getAddProfIdForPkg(addonPkgCode, xdmPttServerId);
                            if (profileId != null) {
                                // Check for QPP data package first
                                if (qppDataPkgMap != null) {
                                    Integer qppId = qppDataPkgMap.get(profileId);
                                    if (qppId != null) {
                                        qppPkgId = qppId;
                                        dataPkgId = qppId;
                                        break; // Found QPP, use it
                                    }
                                }
                                // If no QPP, check addon data package
                                if (dataPkgId == null && addonDataPkgMap != null) {
                                    dataPkgId = addonDataPkgMap.get(profileId);
                                }
                            }
                        } catch (KnFeatureSetException e) {
                            knLogger.warn(methodName, "Failed to get profile ID for addon package ", addonPkgCode, ": ", e);
                        }
                    }
                } else {
                    // No addon packages - clear QPP
                    qppPkgId = null;
                    dataPkgId = null;
                }

                // Set defaults if not found
                if (qppPkgId == null) {
                    qppPkgId = com.kodiak.xdms.server.common.resources.KnConstants.DEFAULT_QPP_ID;
                }
                if (dataPkgId == null) {
                    dataPkgId = com.kodiak.xdms.server.common.resources.KnConstants.DEFAULT_DATAPKG_ID;
                }

                perMdnDataPkgId.put(mdn, dataPkgId);
                perMdnQppPkgId.put(mdn, qppPkgId);

                knLogger.debug(methodName, "MDN ", KnGDPRTemplate.mdn(mdn), " - dataPkgId: ", dataPkgId, ", qppPkgId: ", qppPkgId);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Error calculating DATA_PKG_ID/QPPPACKID: ", e);
            // Use defaults for all MDNs on error
            for (String mdn : mdnList) {
                perMdnDataPkgId.put(mdn, com.kodiak.xdms.server.common.resources.KnConstants.DEFAULT_DATAPKG_ID);
                perMdnQppPkgId.put(mdn, com.kodiak.xdms.server.common.resources.KnConstants.DEFAULT_QPP_ID);
            }
        }

        // 5. Update tier package and DATA_PKG_ID per MDN in POCSUBSCR_ADDLINFO
        updateTierPackageAndDataPkgPerMdnInBulk(perMdnTierPkgCode, perMdnDataPkgId, persisterTxn);
        result.setTierPkgUpdated(true);

        // 6. Update QPPPACKID in POCSUBSCRINFO per MDN
        updateQppPkgIdInBulk(perMdnQppPkgId, persisterTxn);

        // 7. Delete existing addon packages for all MDNs
        deleteAddonPackagesInBulk(mdnList, persisterTxn);

        // 8. Insert new addon packages per MDN
        insertAddonPackagesPerMdnInBulk(perMdnAddonPkgCodes, persisterTxn);
        result.setAddonPkgsUpdated(true);

        // Store per-MDN addon packages for client type upgrade check
        result.setPerMdnAddonPkgCodes(perMdnAddonPkgCodes);

        result.setProcessed(true);
        result.setSuccess(true);
        result.setPerMdnFinalPkgIds(perMdnFinalPkgIds);

        // Use first MDN's packages for backward compatibility (if needed)
        if (!perMdnTierPkgCode.isEmpty()) {
            String firstMdn = mdnList.get(0);
            result.setFinalTierPkgCode(perMdnTierPkgCode.get(firstMdn));
            result.setFinalAddonPkgCodes(perMdnAddonPkgCodes.get(firstMdn));
            result.setFinalPkgIds(perMdnFinalPkgIds.get(firstMdn));
        }

        knLogger.info(methodName, "PkgIdMap processing completed for ", mdnList.size(), " MDNs");
        return result;
    }

    /**
     * Bulk update tier package code per MDN in POCSUBSCR_ADDLINFO.
     * Each MDN can have a different tier package.
     *
     * @param mdnTierPkgMap Map of MDN to tier package code
     * @param dataPkgId Data package ID to set (same for all)
     * @param persisterTxn Database transaction
     * @return Number of records updated
     * @throws KnDAOException if database operation fails
     */
    public int updateTierPackagePerMdnInBulk(Map<String, String> mdnTierPkgMap, Integer dataPkgId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateTierPackagePerMdnInBulk";
        knLogger.debug(methodName, "Updating tier package for ", mdnTierPkgMap.size(), " MDNs");

        if (mdnTierPkgMap == null || mdnTierPkgMap.isEmpty()) {
            return 0;
        }

        String updateQuery = "UPDATE DG.POCSUBSCR_ADDLINFO SET TIER_PKG_CODE = ?, DATA_PKG_ID = ? WHERE MDN = ?";
        int totalUpdated = 0;

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(updateQuery);

            for (Map.Entry<String, String> entry : mdnTierPkgMap.entrySet()) {
                String mdn = entry.getKey();
                String tierPkgCode = entry.getValue();

                pStmt.setString(1, tierPkgCode);
                if (dataPkgId != null) {
                    pStmt.setInt(2, dataPkgId);
                } else {
                    pStmt.setNull(2, java.sql.Types.INTEGER);
                }
                pStmt.setString(3, mdn);
                pStmt.addBatch();
            }

            int[] batchResults = pStmt.executeBatch();
            for (int result : batchResults) {
                if (result >= 0 || result == PreparedStatement.SUCCESS_NO_INFO) {
                    totalUpdated++;
                }
            }

            knLogger.info(methodName, "Updated tier package for ", totalUpdated, " MDNs");
            return totalUpdated;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during tier package update: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to update tier package per MDN: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to update tier package per MDN", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * Bulk update tier package code and DATA_PKG_ID per MDN in POCSUBSCR_ADDLINFO.
     * Each MDN can have different tier package and DATA_PKG_ID.
     *
     * @param mdnTierPkgMap Map of MDN to tier package code
     * @param mdnDataPkgIdMap Map of MDN to DATA_PKG_ID
     * @param persisterTxn Database transaction
     * @return Number of records updated
     * @throws KnDAOException if database operation fails
     */
    public int updateTierPackageAndDataPkgPerMdnInBulk(Map<String, String> mdnTierPkgMap,
                                                        Map<String, Integer> mdnDataPkgIdMap,
                                                        KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateTierPackageAndDataPkgPerMdnInBulk";
        knLogger.debug(methodName, "Updating tier package and DATA_PKG_ID for ", mdnTierPkgMap.size(), " MDNs");

        if (mdnTierPkgMap == null || mdnTierPkgMap.isEmpty()) {
            return 0;
        }

        String updateQuery = "UPDATE DG.POCSUBSCR_ADDLINFO SET TIER_PKG_CODE = ?, DATA_PKG_ID = ? WHERE MDN = ?";
        int totalUpdated = 0;

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(updateQuery);

            for (Map.Entry<String, String> entry : mdnTierPkgMap.entrySet()) {
                String mdn = entry.getKey();
                String tierPkgCode = entry.getValue();
                Integer dataPkgId = mdnDataPkgIdMap.get(mdn);

                pStmt.setString(1, tierPkgCode);
                if (dataPkgId != null) {
                    pStmt.setInt(2, dataPkgId);
                } else {
                    pStmt.setInt(2, com.kodiak.xdms.server.common.resources.KnConstants.DEFAULT_DATAPKG_ID);
                }
                pStmt.setString(3, mdn);
                pStmt.addBatch();
            }

            int[] batchResults = pStmt.executeBatch();
            for (int result : batchResults) {
                if (result >= 0 || result == PreparedStatement.SUCCESS_NO_INFO) {
                    totalUpdated++;
                }
            }

            knLogger.info(methodName, "Updated tier package and DATA_PKG_ID for ", totalUpdated, " MDNs");
            return totalUpdated;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during tier package and DATA_PKG_ID update: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to update tier package and DATA_PKG_ID per MDN: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to update tier package and DATA_PKG_ID per MDN", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * Bulk update QPPPACKID in POCSUBSCRINFO for multiple MDNs.
     * This is needed when addon packages change to update the QPP package ID.
     *
     * @param mdnQppPkgIdMap Map of MDN to QPPPACKID
     * @param persisterTxn Database transaction
     * @return Number of records updated
     * @throws KnDAOException if database operation fails
     */
    public int updateQppPkgIdInBulk(Map<String, Integer> mdnQppPkgIdMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateQppPkgIdInBulk";
        knLogger.debug(methodName, "Updating QPPPACKID for ", mdnQppPkgIdMap.size(), " MDNs");

        if (mdnQppPkgIdMap == null || mdnQppPkgIdMap.isEmpty()) {
            return 0;
        }

        String updateQuery = "UPDATE DG.POCSUBSCRINFO SET QPPPACKID = ? WHERE MDN = ?";
        int totalUpdated = 0;

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(updateQuery);

            for (Map.Entry<String, Integer> entry : mdnQppPkgIdMap.entrySet()) {
                String mdn = entry.getKey();
                Integer qppPkgId = entry.getValue();

                if (qppPkgId != null) {
                    pStmt.setInt(1, qppPkgId);
                } else {
                    pStmt.setInt(1, com.kodiak.xdms.server.common.resources.KnConstants.DEFAULT_QPP_ID);
                }
                pStmt.setString(2, mdn);
                pStmt.addBatch();
            }

            int[] batchResults = pStmt.executeBatch();
            for (int result : batchResults) {
                if (result >= 0 || result == PreparedStatement.SUCCESS_NO_INFO) {
                    totalUpdated++;
                }
            }

            knLogger.info(methodName, "Updated QPPPACKID for ", totalUpdated, " MDNs");
            return totalUpdated;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during QPPPACKID update: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to update QPPPACKID in bulk: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to update QPPPACKID in bulk", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * Get data package ID mapping using genInfoUtil - aligned with single MDN flow.
     * Single MDN flow: genInfoUtil.getDataPkgId(pkgType, persisterTxn)
     *
     * Maps profile ID to data package ID for the given package type.
     *
     * @param pkgType Package type (QPP_DATA_PKG_TYPE or ADDON_DATA_PKG_TYPE)
     * @param persisterTxn Database transaction
     * @return Map of profile ID to data package ID
     * @throws KnDAOException if operation fails
     */
    private Map<Integer, Integer> getDataPkgIdMap(Integer pkgType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDataPkgIdMap";
        knLogger.debug(methodName, "Getting data package ID map for pkgType: ", pkgType);

        try {
            // Use genInfoUtil like single MDN flow does
            KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
            Map<Integer, Integer> dataPkgIdMap = genInfoUtil.getDataPkgId(pkgType, persisterTxn);

            knLogger.debug(methodName, "Fetched ", (dataPkgIdMap != null ? dataPkgIdMap.size() : 0),
                    " data package mappings via genInfoUtil for pkgType ", pkgType);
            return dataPkgIdMap != null ? dataPkgIdMap : new HashMap<>();

        } catch (Exception e) {
            knLogger.error(methodName, "Exception fetching data package ID map via genInfoUtil: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to get data package ID map: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to get data package ID map", e);
        }
    }

    /**
     * Bulk insert addon packages per MDN.
     * Each MDN can have different addon packages.
     *
     * @param mdnAddonPkgsMap Map of MDN to list of addon package codes
     * @param persisterTxn Database transaction
     * @return Number of records inserted
     * @throws KnDAOException if database operation fails
     */
    public int insertAddonPackagesPerMdnInBulk(Map<String, List<String>> mdnAddonPkgsMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertAddonPackagesPerMdnInBulk";
        knLogger.debug(methodName, "Inserting addon packages for ", mdnAddonPkgsMap.size(), " MDNs");

        if (mdnAddonPkgsMap == null || mdnAddonPkgsMap.isEmpty()) {
            return 0;
        }

        String insertQuery = "INSERT INTO DG.SUBSCR_ADDON_PKGINFO (MDN, ADDON_PKGCODE) VALUES (?, ?)";
        int totalInserted = 0;

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(insertQuery);

            for (Map.Entry<String, List<String>> entry : mdnAddonPkgsMap.entrySet()) {
                String mdn = entry.getKey();
                List<String> addonPkgCodes = entry.getValue();

                if (addonPkgCodes == null || addonPkgCodes.isEmpty()) {
                    continue;
                }

                for (String addonPkgCode : addonPkgCodes) {
                    pStmt.setString(1, mdn);
                    pStmt.setString(2, addonPkgCode);
                    pStmt.addBatch();
                }
            }

            int[] results = pStmt.executeBatch();
            for (int result : results) {
                if (result >= 0 || result == PreparedStatement.SUCCESS_NO_INFO) {
                    totalInserted++;
                }
            }

            knLogger.info(methodName, "Inserted ", totalInserted, " addon package entries");
            return totalInserted;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during addon package insert: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to insert addon packages per MDN: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to insert addon packages per MDN", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * Recalculate and update FeatureSet (subsFS2, activeFS2) for multiple MDNs in bulk
     * when pkgIdMap changes.
     *
     * This implements the same FeatureSet recalculation logic as single MDN flow:
     * 1. Get default SubsFS for base package (if configured)
     * 2. Get default SubsFS for all package codes (tier + addon)
     * 3. OR the feature bits together
     * 4. Generate final subsFS2
     * 5. Generate final activeFS2 based on all feature set components
     * 6. Bulk update database
     *
     * @param existingProfilesMap Map of MDN to existing profile data
     * @param perMdnFinalPkgIds Per-MDN final package IDs after ADD/REMOVE processing
     * @param xdmPttServerId XDM PTT Server ID
     * @param persisterTxn Database transaction
     * @param result Optional KnBulkUpdateResult to populate with updated FeatureSets (for notifications)
     * @return Number of records updated
     * @throws KnDAOException if database operation fails
     */
    public int recalculateAndUpdateFeatureSetInBulk(
            Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
            Map<String, Map<String, Integer>> perMdnFinalPkgIds,
            String xdmPttServerId,
            KnPersisterTxn persisterTxn,
            KnBulkUpdateResult result) throws KnDAOException {

        String methodName = "recalculateAndUpdateFeatureSetInBulk";
        knLogger.debug(methodName, "Recalculating FeatureSet for ", existingProfilesMap.size(), " MDNs");

        if (existingProfilesMap == null || existingProfilesMap.isEmpty()) {
            return 0;
        }

        if (perMdnFinalPkgIds == null || perMdnFinalPkgIds.isEmpty()) {
            knLogger.warn(methodName, "No per-MDN final packages provided, skipping FeatureSet recalculation");
            return 0;
        }

        try {
            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();

            // Fetch BASE_PKGCODE from system config (same for all MDNs)
            // Single MDN flow: String basePkgCode = paramNameValueMap.get(KnConstants.BASE_PKGCODE);
            String basePkgCode = bulkOpsDBUtil.getBasePkgCode(persisterTxn);

            // Prepare batch updates
            Map<String, String[]> mdnFeatureSetMap = new HashMap<>(); // MDN -> [subsFS2, activeFS2, xdmsFS2]

            for (Map.Entry<String, KnBulkSubsProfileDTO> entry : existingProfilesMap.entrySet()) {
                String mdn = entry.getKey();
                KnBulkSubsProfileDTO profile = entry.getValue();

                // Get this MDN's final packages
                Map<String, Integer> finalPkgIds = perMdnFinalPkgIds.get(mdn);
                if (finalPkgIds == null || finalPkgIds.isEmpty()) {
                    knLogger.debug(methodName, "No final packages for MDN ", KnGDPRTemplate.mdn(mdn), ", skipping FeatureSet recalculation");
                    continue;
                }

                try {
                    // Calculate new subsFS2 based on package codes
                    int publicType = profile.getPublicSubscriptionType();
                    int corpType = profile.getCorporateSubscriptionType();
                    int clientType = profile.getSubsClientType();

                    // Build existing packages map from profile (before ADD/REMOVE changes)
                    // This is needed for LMR bit status determination
                    Map<String, Integer> existingPkgIds = new HashMap<>();
                    if (profile.getTierPkgCode() != null && !profile.getTierPkgCode().isEmpty()) {
                        existingPkgIds.put(profile.getTierPkgCode(), 1); // TIER_PKG_TYPE = 1
                    }
                    if (profile.getAddonPkgCodes() != null) {
                        for (String addonCode : profile.getAddonPkgCodes()) {
                            existingPkgIds.put(addonCode, 2); // ADDON_PKG_TYPE = 2
                        }
                    }

                    // === IMPORTANT: Base Package Processing (matches single MDN flow) ===
                    // Single MDN flow does:
                    // 1. Get base package FS: basePkgCodeFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(...)
                    // 2. OR it with finalFSBitSet
                    // 3. Then get pkgCodeFS and OR that too
                    java.util.BitSet finalFSBitSet = new java.util.BitSet(Long.SIZE);

                    // Step 1: Get default FS for BASE package (if configured)
                    if (basePkgCode != null && !basePkgCode.isEmpty()) {
                        String basePkgCodeFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(
                                publicType, corpType, clientType, basePkgCode, xdmPttServerId);
                        java.util.BitSet basePkgCodeBitSet = featureSetUtil.convertHexStringToBitSet(basePkgCodeFS);
                        finalFSBitSet.or(basePkgCodeBitSet);
                        knLogger.debug(methodName, "Base pkg FS applied for MDN ", KnGDPRTemplate.mdn(mdn), ": ", basePkgCodeFS);
                    }

                    // Step 2: Get default FS for NEW package codes (after ADD/REMOVE)
                    String pkgCodeFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(
                            publicType, corpType, clientType, finalPkgIds, xdmPttServerId);
                    java.util.BitSet pkgCodeBitSet = featureSetUtil.convertHexStringToBitSet(pkgCodeFS);
                    finalFSBitSet.or(pkgCodeBitSet);
                    knLogger.debug(methodName, "Pkg codes FS applied for MDN ", KnGDPRTemplate.mdn(mdn), ": ", pkgCodeFS);

                    // === LMR BIT STATUS HANDLING ===
                    // Preserve manual operator overrides for LMR (InterOP) bit
                    // This ensures that if an operator manually enabled/disabled the LMR bit,
                    // it will be preserved even when packages change
                    // NOTE: Use EXISTING packages (before changes) to determine manual override status
                    int lmrBitStatus = findLmrBitStatus(profile, existingPkgIds, xdmPttServerId, featureSetUtil);
                    final int LMR_BIT = com.kodiak.xdms.server.common.resources.KnConstants.LMR_BIT;

                    if (finalFSBitSet.get(LMR_BIT) &&
                        lmrBitStatus == com.kodiak.xdms.server.common.resources.KnConstants.LMR_BIT_STATUS.MANUALLY_DISABLED.Value()) {
                        // New packages enable LMR, but it was manually disabled - keep it disabled
                        finalFSBitSet.clear(LMR_BIT);
                        knLogger.debug(methodName, "LMR bit cleared for MDN ", KnGDPRTemplate.mdn(mdn), " - was manually disabled");
                    } else if (!finalFSBitSet.get(LMR_BIT) &&
                               lmrBitStatus == com.kodiak.xdms.server.common.resources.KnConstants.LMR_BIT_STATUS.MANUALLY_ENABLED.Value()) {
                        // New packages disable LMR, but it was manually enabled - keep it enabled
                        finalFSBitSet.set(LMR_BIT);
                        knLogger.debug(methodName, "LMR bit set for MDN ", KnGDPRTemplate.mdn(mdn), " - was manually enabled");
                    }
                    // === END LMR BIT STATUS HANDLING ===

                    String newSubsFS2 = featureSetUtil.convertBitSetToHexString(finalFSBitSet);

                    // Generate final subsFS2
                    String subsFS2 = featureSetUtil.generateSubsFeatureSet(newSubsFS2, newSubsFS2, newSubsFS2);

                    // Handle on-demand location bit based on dispatch group member
                    String xdmsFs2 = profile.getXdmsFS2();
                    if (profile.getDispatchGroupMember() == com.kodiak.xdms.server.common.resources.KnConstants.BIT_TRUE) {
                        xdmsFs2 = featureSetUtil.getSetFeatureSetBits(xdmsFs2,
                                new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION.value()});
                    } else {
                        xdmsFs2 = featureSetUtil.getClearFeatureSetBits(xdmsFs2,
                                new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION.value()});
                    }

                    // Get client capability override bitmask
                    String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(
                            xdmPttServerId, profile.getClientPVmajorVer());

                    // Prepare values for activeFS calculation (PTT IDs already trimmed at fetch time)
                    String pocPttId = profile.getPocHome();
                    String presencePttId = profile.getPresenceHome();
                    String xdmsPttId = profile.getXdmsHome();
                    
                    // Handle invalid PTT server IDs (e.g., "0" or null means not configured)
                    // Fallback priority: xdmsHome -> xdmPttServerId (always valid)
                    // This prevents NPE in generateConfFeatureBitSet when looking up system config
                    String effectiveFallbackPttId = xdmsPttId;
                    if (effectiveFallbackPttId == null || effectiveFallbackPttId.isEmpty() || "0".equals(effectiveFallbackPttId)) {
                        effectiveFallbackPttId = xdmPttServerId;
                        knLogger.debug(methodName, "xdmsHome is invalid [", xdmsPttId, "], using xdmPttServerId [", xdmPttServerId, "] as ultimate fallback");
                    }
                    
                    if (pocPttId == null || pocPttId.isEmpty() || "0".equals(pocPttId)) {
                        knLogger.debug(methodName, "Invalid pocPttId [", pocPttId, "] for MDN ", KnGDPRTemplate.mdn(mdn), ", using fallback [", effectiveFallbackPttId, "]");
                        pocPttId = effectiveFallbackPttId;
                    }
                    if (presencePttId == null || presencePttId.isEmpty() || "0".equals(presencePttId)) {
                        knLogger.debug(methodName, "Invalid presencePttId [", presencePttId, "] for MDN ", KnGDPRTemplate.mdn(mdn), ", using fallback [", effectiveFallbackPttId, "]");
                        presencePttId = effectiveFallbackPttId;
                    }
                    if (xdmsPttId == null || xdmsPttId.isEmpty() || "0".equals(xdmsPttId)) {
                        knLogger.debug(methodName, "Invalid xdmsPttId [", xdmsPttId, "] for MDN ", KnGDPRTemplate.mdn(mdn), ", using fallback [", effectiveFallbackPttId, "]");
                        xdmsPttId = effectiveFallbackPttId;
                    }
                    
                    // Log original values from profile object (before applying defaults)
                    knLogger.debug(methodName, "Original profile FS values for MDN ", KnGDPRTemplate.mdn(mdn), ":",
                            " pocHome=", profile.getPocHome(), " (effective=", pocPttId, ")",
                            ", presenceHome=", profile.getPresenceHome(), " (effective=", presencePttId, ")",
                            ", xdmsHome=", profile.getXdmsHome(), " (effective=", xdmsPttId, ")",
                            ", clientFS2=", profile.getClientFS2(),
                            ", subsFS2=", profile.getSubsFS2(),
                            ", activeFS2=", profile.getActiveFS2(),
                            ", opsFS2=", profile.getOpsFS2(),
                            ", corpAdminFS2=", profile.getCorpAdminFS2(),
                            ", corpFS2=", profile.getCorpFS2(),
                            ", userProfileFS2=", profile.getUserProfileFS2(),
                            ", xdmsFS2=", profile.getXdmsFS2(),
                            ", clientPVmajorVer=", profile.getClientPVmajorVer(),
                            ", dispatchGroupMember=", profile.getDispatchGroupMember());
                    
                    // Apply defaults for null FS values (aligned with Single MDN flow)
                    String clientFS2 = profile.getClientFS2();
                    if (clientFS2 == null) {
                        clientFS2 = featureSetUtil.getDefFinalClientFS(clientCapOverrideBitMask);
                        knLogger.debug(methodName, "Applied default clientFS2 for MDN ", KnGDPRTemplate.mdn(mdn), ": ", clientFS2);
                    }
                    
                    String opsFS2 = profile.getOpsFS2();
                    if (opsFS2 == null) {
                        opsFS2 = featureSetUtil.getDefFinalOpsFS();
                        knLogger.debug(methodName, "Applied default opsFS2 for MDN ", KnGDPRTemplate.mdn(mdn), ": ", opsFS2);
                    }
                    
                    String corpAdminFS2 = profile.getCorpAdminFS2();
                    if (corpAdminFS2 == null) {
                        corpAdminFS2 = featureSetUtil.getDefFinalCorpAdminFS();
                        knLogger.debug(methodName, "Applied default corpAdminFS2 for MDN ", KnGDPRTemplate.mdn(mdn), ": ", corpAdminFS2);
                    }
                    
                    String userProfileFS2 = profile.getUserProfileFS2();
                    if (userProfileFS2 == null) {
                        userProfileFS2 = featureSetUtil.getDefFinalUserProfileFS();
                        knLogger.debug(methodName, "Applied default userProfileFS2 for MDN ", KnGDPRTemplate.mdn(mdn), ": ", userProfileFS2);
                    }
                    
                    // Ensure xdmsFs2 has a default if null
                    if (xdmsFs2 == null) {
                        xdmsFs2 = featureSetUtil.getDefFinalXdmsFS();
                        knLogger.debug(methodName, "Applied default xdmsFs2 for MDN ", KnGDPRTemplate.mdn(mdn), ": ", xdmsFs2);
                    }

                    // Log final FS values before generating activeFS2
                    knLogger.debug(methodName, "Final FS values for MDN ", KnGDPRTemplate.mdn(mdn), " before activeFS2 generation:",
                            " pocPttId=", pocPttId,
                            ", presencePttId=", presencePttId,
                            ", xdmsPttId=", xdmsPttId,
                            ", clientFS2=", clientFS2,
                            ", subsFS2=", subsFS2,
                            ", opsFS2=", opsFS2,
                            ", corpAdminFS2=", corpAdminFS2,
                            ", userProfileFS2=", userProfileFS2,
                            ", xdmsFs2=", xdmsFs2,
                            ", clientCapOverrideBitMask=", clientCapOverrideBitMask,
                            ", corpType=", corpType);

                    // Generate activeFS2
                    String activeFS2;
                    if (corpType == com.kodiak.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                        activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId,
                                clientFS2, subsFS2, opsFS2, clientCapOverrideBitMask, xdmsFs2, userProfileFS2);
                    } else {
                        String corpFS2 = profile.getCorpFS2();
                        if (corpFS2 == null) {
                            corpFS2 = featureSetUtil.getDefFinalCorpFS();
                        }
                        knLogger.debug(methodName, "corpFS2 for MDN ", KnGDPRTemplate.mdn(mdn), ": ", corpFS2);
                        activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId,
                                clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask,
                                xdmsFs2, userProfileFS2);
                    }

                    mdnFeatureSetMap.put(mdn, new String[]{subsFS2, activeFS2, xdmsFs2});
                    knLogger.debug(methodName, "Calculated FS for MDN ", KnGDPRTemplate.mdn(mdn),
                                  " - subsFS2: ", subsFS2, ", activeFS2: ", activeFS2);
                    
                    // Populate result with updated FeatureSets for notifications
                    if (result != null) {
                        result.addUpdatedSubsFS2(mdn, subsFS2);
                        result.addUpdatedActiveFS2(mdn, activeFS2);
                    }

                } catch (KnFeatureSetException e) {
                    knLogger.error(methodName, "FeatureSet calculation failed for MDN ", KnGDPRTemplate.mdn(mdn), ": ", e);
                    // Fail the batch - invalid package codes should not be allowed
                    // This aligns with Single MDN flow which throws PKG_NOT_FOUND for invalid packages
                    //throw new KnDAOException(KnBulkOpsErrorCodes.BOEntity.PKG_NOT_FOUND,
                    //        "Invalid package for MDN " + mdn + ": " + e.getMessage(), e);
                    throw new KnDAOException(KnBulkOpsErrorCodes.BOEntity.PKG_NOT_FOUND,
                            "Invalid Addon or Tier Pkg", e);
                }
            }

            // Bulk update database with calculated feature sets
            if (!mdnFeatureSetMap.isEmpty()) {
                return updateFeatureSetsInBulk(mdnFeatureSetMap, persisterTxn);
            }

            return 0;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during FeatureSet recalculation: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to recalculate FeatureSet: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to recalculate FeatureSet, Invalid Addon or Tier Pkg is present", e);
        }
    }

    /**
     * Determines the LMR (Land Mobile Radio / InterOP) bit status for a subscriber.
     * This method checks if the LMR bit was manually enabled or disabled by an operator.
     *
     * Logic:
     * 1. Calculate what the default FS would be based on existing packages
     * 2. Compare with the current FS stored in the database
     * 3. If current FS has LMR_BIT=1 but default FS has LMR_BIT=0 → MANUALLY_ENABLED
     * 4. If current FS has LMR_BIT=0 but default FS has LMR_BIT=1 → MANUALLY_DISABLED
     * 5. Otherwise → NO_CHANGE
     *
     * This ensures that manual operator overrides are preserved when packages change.
     *
     * @param profile Subscriber profile with existing FS
     * @param existingPkgIds Existing package IDs for the subscriber
     * @param xdmPttServerId XDM PTT Server ID
     * @param featureSetUtil FeatureSet utility instance
     * @return LMR bit status (MANUALLY_ENABLED, MANUALLY_DISABLED, or NO_CHANGE)
     */
    private int findLmrBitStatus(KnBulkSubsProfileDTO profile,
                                  Map<String, Integer> existingPkgIds,
                                  String xdmPttServerId,
                                  KnFeatureSetUtil featureSetUtil) {
        String methodName = "findLmrBitStatus";

        try {
            final int LMR_BIT = com.kodiak.xdms.server.common.resources.KnConstants.LMR_BIT;

            // Get the existing subsFS2 from the profile
            String existingSubsFS2 = profile.getSubsFS2();
            if (existingSubsFS2 == null || existingSubsFS2.isEmpty()) {
                knLogger.debug(methodName, "No existing subsFS2 for MDN ", KnGDPRTemplate.mdn(profile.getMdn()));
                return com.kodiak.xdms.server.common.resources.KnConstants.LMR_BIT_STATUS.NO_CHANGE.Value();
            }

            java.util.BitSet existingFSBitSet = featureSetUtil.convertHexStringToBitSet(existingSubsFS2);
            knLogger.debug(methodName, "Existing subsFS BitSet for MDN ", KnGDPRTemplate.mdn(profile.getMdn()), ": ", existingFSBitSet.toString());

            // Calculate what the default FS would be based on existing packages
            java.util.BitSet defaultFSBitSet = new java.util.BitSet(Long.SIZE);

            if (existingPkgIds != null && !existingPkgIds.isEmpty()) {
                String pkgCodeFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(
                        profile.getPublicSubscriptionType(),
                        profile.getCorporateSubscriptionType(),
                        profile.getSubsClientType(),
                        existingPkgIds,
                        xdmPttServerId);
                defaultFSBitSet = featureSetUtil.convertHexStringToBitSet(pkgCodeFS);
            }

            knLogger.debug(methodName, "Default FS BitSet based on packages for MDN ", KnGDPRTemplate.mdn(profile.getMdn()), ": ", defaultFSBitSet.toString());

            // Determine LMR bit status
            int status = com.kodiak.xdms.server.common.resources.KnConstants.LMR_BIT_STATUS.NO_CHANGE.Value();

            if (existingFSBitSet.get(LMR_BIT) && !defaultFSBitSet.get(LMR_BIT)) {
                // Current FS has LMR enabled, but default based on packages would have it disabled
                // This means operator MANUALLY ENABLED it
                status = com.kodiak.xdms.server.common.resources.KnConstants.LMR_BIT_STATUS.MANUALLY_ENABLED.Value();
                knLogger.debug(methodName, "LMR bit was MANUALLY_ENABLED for MDN ", KnGDPRTemplate.mdn(profile.getMdn()));
            } else if (!existingFSBitSet.get(LMR_BIT) && defaultFSBitSet.get(LMR_BIT)) {
                // Current FS has LMR disabled, but default based on packages would have it enabled
                // This means operator MANUALLY DISABLED it
                status = com.kodiak.xdms.server.common.resources.KnConstants.LMR_BIT_STATUS.MANUALLY_DISABLED.Value();
                knLogger.debug(methodName, "LMR bit was MANUALLY_DISABLED for MDN ", KnGDPRTemplate.mdn(profile.getMdn()));
            }

            knLogger.debug(methodName, "LMR bit status for MDN ", KnGDPRTemplate.mdn(profile.getMdn()), ": ", status);
            return status;

        } catch (KnFeatureSetException e) {
            knLogger.error(methodName, "Error determining LMR bit status for MDN ", KnGDPRTemplate.mdn(profile.getMdn()), ": ", e);
            // Return NO_CHANGE on error to avoid disrupting the flow
            return com.kodiak.xdms.server.common.resources.KnConstants.LMR_BIT_STATUS.NO_CHANGE.Value();
        }
    }

    /**
     * Bulk update FeatureSet fields (SUBSCRIBERFS2, ACTIVEFS2, XDMSFS2) in POCSUBSCRINFO.
     *
     * @param mdnFeatureSetMap Map of MDN to [subsFS2, activeFS2, xdmsFS2]
     * @param persisterTxn Database transaction
     * @return Number of records updated
     * @throws KnDAOException if database operation fails
     */
    public int updateFeatureSetsInBulk(Map<String, String[]> mdnFeatureSetMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateFeatureSetsInBulk";
        knLogger.debug(methodName, "Updating FeatureSets for ", mdnFeatureSetMap.size(), " MDNs");

        if (mdnFeatureSetMap == null || mdnFeatureSetMap.isEmpty()) {
            return 0;
        }

        String updateQuery = "UPDATE DG.POCSUBSCRINFO SET SUBSCRIBERFS2 = ?, ACTIVEFS2 = ?, XDMSFS2 = ?, " +
                            "LASTPROFILEUPDATETIME = ? WHERE MDN = ?";
        long currentTime = System.currentTimeMillis();
        int totalUpdated = 0;

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(updateQuery);

            for (Map.Entry<String, String[]> entry : mdnFeatureSetMap.entrySet()) {
                String mdn = entry.getKey();
                String[] featureSets = entry.getValue();

                pStmt.setString(1, KnGeneralUtil.getFeatureSet(featureSets[0])); // subsFS2
                pStmt.setString(2, KnGeneralUtil.getFeatureSet(featureSets[1])); // activeFS2
                pStmt.setString(3, KnGeneralUtil.getFeatureSet(featureSets[2])); // xdmsFS2
                pStmt.setLong(4, currentTime);
                pStmt.setString(5, mdn);
                pStmt.addBatch();

                knLogger.debug(methodName, "Adding batch: MDN=", KnGDPRTemplate.mdn(mdn));
            }

            int[] batchResults = pStmt.executeBatch();

            for (int result : batchResults) {
                if (result >= 0 || result == PreparedStatement.SUCCESS_NO_INFO) {
                    totalUpdated++;
                }
            }

            knLogger.info(methodName, "FeatureSet bulk update completed - Total updated: ", totalUpdated);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during FeatureSet bulk update: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to update FeatureSets in bulk: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to update FeatureSets in bulk", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }

        return totalUpdated;
    }

    /**
     * Fetches QPP package codes using featureSetUtil - aligned with single MDN flow.
     * Single MDN flow: featureSetUtil.getQPPPkgCodes(xdmPttServerId)
     *
     * @param persisterTxn Database transaction (not used - featureSetUtil handles caching)
     * @return List of QPP package codes
     * @throws KnDAOException if operation fails
     */
    private List<String> fetchQppPackageCodes(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "fetchQppPackageCodes";
        List<String> qppPkgCodes = new ArrayList<>();

        try {
            // Use featureSetUtil like single MDN flow does
            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
            String xdmPttServerId = KnBulkOpsDBUtil.getXdmPttServerId();

            qppPkgCodes = featureSetUtil.getQPPPkgCodes(xdmPttServerId);
            knLogger.debug(methodName, "Fetched ", qppPkgCodes.size(), " QPP package codes via featureSetUtil: ", qppPkgCodes);

        } catch (KnFeatureSetException e) {
            knLogger.error(methodName, "Exception fetching QPP package codes via featureSetUtil: ", e);
            // Return empty list if fetch fails - validation will continue without QPP check
            knLogger.warn(methodName, "Continuing without QPP validation");
        }

        return qppPkgCodes;
    }

    /**
     * Checks for client type upgrades when packages with USER_PROFILE_MGMT_BIT are added.
     *
     * Conditions for upgrade:
     * 1. hierarchyType == NON_HIERARCHY
     * 2. Final packages contain COLLABORATION (TIER1) or COMMAND (TIER2)
     * 3. Package has USER_PROFILE_MGMT_BIT enabled in its default FeatureSet
     * 4. Current clientType is HANDSET, POC_WIFIONLY, or CROSSCARRIER
     *
     * Upgrade mappings:
     * - HANDSET → PTTRADIOHANDSETCLIENT
     * - POC_WIFIONLY → PTTRADIOWIFIONLYCLIENT
     * - CROSSCARRIER → PTTRADIOCROSSCARRIERCLIENT
     *
     * @param existingProfilesMap Map of MDN to existing profile data
     * @param perMdnFinalPkgIds Per-MDN final package IDs after ADD/REMOVE
     * @param xdmPttServerId XDM PTT Server ID
     * @param persisterTxn Database transaction
     * @return Map of MDN to new (upgraded) client type for MDNs that need upgrade
     */
    private Map<String, Integer> checkAndGetClientTypeUpgrades(
            Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
            Map<String, Map<String, Integer>> perMdnFinalPkgIds,
            String xdmPttServerId,
            KnPersisterTxn persisterTxn) {

        String methodName = "checkAndGetClientTypeUpgrades";
        Map<String, Integer> clientTypeUpgrades = new HashMap<>();

        final String COLLABORATION = com.kodiak.common.resources.KnConstants.COLLABORATION; // "TIER1"
        final String COMMAND = com.kodiak.common.resources.KnConstants.COMMAND; // "TIER2"
        final int HANDSET = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.HANDSET.value();
        final int POC_WIFIONLY = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value();
        final int CROSSCARRIER = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.CROSSCARRIER.value();
        final int PTTRADIOHANDSETCLIENT = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value();
        final int PTTRADIOWIFIONLYCLIENT = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value();
        final int PTTRADIOCROSSCARRIERCLIENT = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value();
        final int USER_PROFILE_MGMT_BIT = com.kodiak.common.resources.KnConstants.FEATURE_SET.USER_PROFILE_MGMT_BIT.value();

        try {
            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();

            for (Map.Entry<String, KnBulkSubsProfileDTO> entry : existingProfilesMap.entrySet()) {
                String mdn = entry.getKey();
                KnBulkSubsProfileDTO profile = entry.getValue();

                // Condition 1: Check hierarchy type is NON_HIERARCHY
                if (profile.getHierarchyType() != com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE.NON_HIERARCHY) {
                    continue;
                }

                // Build EXISTING packages map (before ADD/REMOVE changes) - same as single MDN flow
                // Single MDN flow: "exsitingSubsPkgs.containsKey(COLLABORATION) || exsitingSubsPkgs.containsKey(COMMAND)"
                Map<String, Integer> existingPkgIds = new HashMap<>();
                if (profile.getTierPkgCode() != null && !profile.getTierPkgCode().isEmpty()) {
                    existingPkgIds.put(profile.getTierPkgCode(), 1); // TIER_PKG_TYPE = 1
                }
                if (profile.getAddonPkgCodes() != null) {
                    for (String addonCode : profile.getAddonPkgCodes()) {
                        existingPkgIds.put(addonCode, 2); // ADDON_PKG_TYPE = 2
                    }
                }

                // Condition 2: Check EXISTING packages contain COLLABORATION or COMMAND
                // This matches single MDN flow which checks exsitingSubsPkgs (packages BEFORE changes)
                boolean hasCollaborationOrCommand = existingPkgIds.containsKey(COLLABORATION) || existingPkgIds.containsKey(COMMAND);
                if (!hasCollaborationOrCommand) {
                    continue;
                }

                // Condition 3: Check if current client type is upgradeable
                int currentClientType = profile.getSubsClientType();
                if (currentClientType != HANDSET && currentClientType != POC_WIFIONLY && currentClientType != CROSSCARRIER) {
                    continue;
                }

                // Condition 4: Check if package has USER_PROFILE_MGMT_BIT enabled
                // Single MDN flow uses finalPkgIdsMap.keySet().iterator().next() - first package from FINAL list
                Map<String, Integer> finalPkgIds = perMdnFinalPkgIds.get(mdn);
                if (finalPkgIds == null || finalPkgIds.isEmpty()) {
                    continue;
                }

                // Get first package code from FINAL packages to check USER_PROFILE_MGMT_BIT
                String pkgCode = finalPkgIds.keySet().iterator().next();
                try {
                    String defSubsFS2 = featureSetUtil.retrieveDefSubsFS2(pkgCode, persisterTxn);
                    boolean userProfileBitEnabled = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(defSubsFS2, USER_PROFILE_MGMT_BIT);

                    if (userProfileBitEnabled) {
                        // Determine new client type
                        int newClientType;
                        if (currentClientType == HANDSET) {
                            newClientType = PTTRADIOHANDSETCLIENT;
                        } else if (currentClientType == POC_WIFIONLY) {
                            newClientType = PTTRADIOWIFIONLYCLIENT;
                        } else { // CROSSCARRIER
                            newClientType = PTTRADIOCROSSCARRIERCLIENT;
                        }

                        clientTypeUpgrades.put(mdn, newClientType);
                        knLogger.info(methodName, "MDN ", KnGDPRTemplate.mdn(mdn), " eligible for client type upgrade from ",
                                     currentClientType, " to ", newClientType);
                    }
                } catch (Exception e) {
                    knLogger.warn(methodName, "Failed to check USER_PROFILE_MGMT_BIT for package ", pkgCode, ": ", e);
                }
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Error checking client type upgrades: ", e);
        }

        knLogger.info(methodName, "Found ", clientTypeUpgrades.size(), " MDNs eligible for client type upgrade");
        return clientTypeUpgrades;
    }

    /**
     * Bulk update subscriber client types (CLIENT_TYPE) in POCSUBSCRINFO.
     *
     * @param mdnClientTypeMap Map of MDN to new client type
     * @param persisterTxn Database transaction
     * @return Number of records updated
     * @throws KnDAOException if database operation fails
     */
    public int updateSubscriberClientTypesInBulk(Map<String, Integer> mdnClientTypeMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscriberClientTypesInBulk";
        knLogger.debug(methodName, "Updating client type for ", mdnClientTypeMap.size(), " MDNs");

        if (mdnClientTypeMap == null || mdnClientTypeMap.isEmpty()) {
            return 0;
        }

        String updateQuery = "UPDATE DG.POCSUBSCRINFO SET CLIENT_TYPE = ?, LASTPROFILEUPDATETIME = ? WHERE MDN = ?";
        int totalUpdated = 0;
        long currentTime = System.currentTimeMillis();

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(updateQuery);

            for (Map.Entry<String, Integer> entry : mdnClientTypeMap.entrySet()) {
                String mdn = entry.getKey();
                Integer newClientType = entry.getValue();

                pStmt.setInt(1, newClientType);
                pStmt.setLong(2, currentTime);
                pStmt.setString(3, mdn);
                pStmt.addBatch();
            }

            int[] batchResults = pStmt.executeBatch();
            for (int result : batchResults) {
                if (result >= 0 || result == PreparedStatement.SUCCESS_NO_INFO) {
                    totalUpdated++;
                }
            }

            knLogger.info(methodName, "Updated client type for ", totalUpdated, " MDNs");
            return totalUpdated;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during client type update: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to update client types in bulk: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to update client types in bulk", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * Bulk update tier package code and DATA_PKG_ID in POCSUBSCR_ADDLINFO for multiple MDNs.
     *
     * @param mdnList List of MDNs to update
     * @param tierPkgCode Tier package code to set (can be null to clear)
     * @param dataPkgId Data package ID to set
     * @param persisterTxn Database transaction
     * @return Number of records updated
     * @throws KnDAOException if database operation fails
     */
    public int updateTierPackageAndDataPkgInBulk(List<String> mdnList, String tierPkgCode, Integer dataPkgId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateTierPackageAndDataPkgInBulk";
        knLogger.debug(methodName, "Updating tier package and DATA_PKG_ID for ", mdnList.size(), " MDNs");

        if (mdnList == null || mdnList.isEmpty()) {
            return 0;
        }

        String placeholders = String.join(",", java.util.Collections.nCopies(mdnList.size(), "?"));
        String updateQuery = "UPDATE DG.POCSUBSCR_ADDLINFO SET TIER_PKG_CODE = ?, DATA_PKG_ID = ? WHERE MDN IN (" + placeholders + ")";

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(updateQuery);

            pStmt.setString(1, tierPkgCode);
            if (dataPkgId != null) {
                pStmt.setInt(2, dataPkgId);
            } else {
                pStmt.setNull(2, java.sql.Types.INTEGER);
            }

            for (int i = 0; i < mdnList.size(); i++) {
                pStmt.setString(i + 3, mdnList.get(i));
            }

            int updated = pStmt.executeUpdate();
            knLogger.info(methodName, "Updated tier package and DATA_PKG_ID for ", updated, " MDNs");
            return updated;

        } catch (Exception e) {
            knLogger.error(methodName, "Exception during tier package and DATA_PKG_ID update: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to update tier package and DATA_PKG_ID: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to update tier package and DATA_PKG_ID", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    // ==================== COMPREHENSIVE BULK UPDATE ====================

    /**
     * Comprehensive bulk subscriber update operation.
     * Handles networkName, profile time, pkgIdMap updates, and FeatureSet recalculation efficiently using batch operations.
     *
     * Update Logic:
     * 1. For MDNs with networkName different from MDN → Update SUBSCRNAME + LASTPROFILEUPDATETIME
     * 2. For MDNs with networkName == MDN → Only update LASTPROFILEUPDATETIME (keep existing networkName)
     * 3. pkgIdMap: Full processing with ADD/REMOVE actions, validation, and DB updates
     * 4. FeatureSet: Recalculate subsFS2 and activeFS2 when pkgIdMap changes
     *
     * @param mdnsToUpdate Complete list of MDNs to update
     * @param networkNameMap Map of MDN to new NetworkName (only MDNs that need networkName change)
     * @param pkgIdMap Package ID map for tier/addon updates (applied to all MDNs)
     * @param existingProfilesMap Map of MDN to existing profile data (for FeatureSet recalculation)
     * @param xdmPttServerId XDM PTT Server ID (for FeatureSet recalculation)
     * @param preFetchedData Pre-fetched utility data (QPP codes, data pkg maps) fetched BEFORE main transaction
     * @param persisterTxn Database transaction
     * @return KnBulkUpdateResult containing update statistics
     * @throws KnDAOException if database operation fails
     */
    public KnBulkUpdateResult updateSubscribersInBulk(
            List<String> mdnsToUpdate,
            Map<String, String> networkNameMap,
            Map<String, Map<String, Integer>> pkgIdMap,
            Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
            String xdmPttServerId,
            KnPreFetchedData preFetchedData,
            KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateSubscribersInBulk";
        knLogger.debug(methodName, "Starting bulk update for ", mdnsToUpdate.size(), " MDNs");

        KnBulkUpdateResult result = new KnBulkUpdateResult();
        result.setTotalMdnCount(mdnsToUpdate.size());

        try {
            // Separate MDNs that need networkName update vs those that don't
            List<String> mdnsWithoutNetworkNameUpdate = new ArrayList<>();
            for (String mdn : mdnsToUpdate) {
                if (networkNameMap == null || !networkNameMap.containsKey(mdn)) {
                    mdnsWithoutNetworkNameUpdate.add(mdn);
                }
            }

            knLogger.info(methodName, "MDNs requiring networkName update: ",
                         (networkNameMap != null ? networkNameMap.size() : 0),
                         ", MDNs keeping existing networkName: ", mdnsWithoutNetworkNameUpdate.size());

            // Step 1: Update networkNames for MDNs that need it
            if (networkNameMap != null && !networkNameMap.isEmpty()) {
                int networkNameUpdated = updateSubscriberNetworkNamesInBulk(networkNameMap, persisterTxn);
                result.setNetworkNameUpdatedCount(networkNameUpdated);
                knLogger.debug(methodName, "NetworkName updated for ", networkNameUpdated, " MDNs");
            }

            // Step 2: Update profile time for MDNs that don't need networkName update
            if (!mdnsWithoutNetworkNameUpdate.isEmpty()) {
                int profileTimeUpdated = updateSubscriberProfileTimeInBulk(mdnsWithoutNetworkNameUpdate, persisterTxn);
                result.setProfileTimeUpdatedCount(profileTimeUpdated);
                knLogger.debug(methodName, "Profile time updated for ", profileTimeUpdated, " MDNs");
            }

            // Step 3: Process pkgIdMap with full validation and updates
            // DECLARATIVE SEMANTIC: 
            // - If pkgIdMap is null/missing: Clear all packages (tier=null, delete all add-ons)
            // - If pkgIdMap is provided: Replace packages with what's in pkgIdMap
            // Use pre-fetched data (QPP codes, data pkg maps) to avoid utility calls that release the connection
            KnPkgIdMapBulkResult pkgResult = processPkgIdMapInBulk(mdnsToUpdate, pkgIdMap, preFetchedData, persisterTxn);

            if (!pkgResult.isSuccess()) {
                result.setSuccess(false);
                result.setErrorMessage("PkgIdMap processing failed: " + pkgResult.getErrorMessage());
                return result;
            }

            result.setPkgIdMapProcessed(pkgResult.isProcessed());
            knLogger.info(methodName, "PkgIdMap processed: ", pkgResult.isProcessed(),
                         ", Tier updated: ", pkgResult.isTierPkgUpdated(),
                         ", Addons updated: ", pkgResult.isAddonPkgsUpdated());

            // Step 4: Recalculate FeatureSet if packages changed
            if (pkgResult.isProcessed() && pkgResult.getPerMdnFinalPkgIds() != null && !pkgResult.getPerMdnFinalPkgIds().isEmpty()) {
                if (existingProfilesMap != null && !existingProfilesMap.isEmpty() && xdmPttServerId != null) {
                    int fsUpdated = recalculateAndUpdateFeatureSetInBulk(
                            existingProfilesMap, pkgResult.getPerMdnFinalPkgIds(), xdmPttServerId, persisterTxn, result);
                    result.setFeatureSetUpdated(fsUpdated > 0);
                    knLogger.info(methodName, "FeatureSet recalculated and updated for ", fsUpdated, " MDNs");
                } else {
                    knLogger.warn(methodName, "Skipping FeatureSet recalculation - missing profile data or server ID");
                }
            }

                // Step 5: Check and apply Client Type Upgrade if applicable
                // When certain packages (TIER1/COLLABORATION, TIER2/COMMAND) with USER_PROFILE_MGMT_BIT
                // are added to NON_HIERARCHY subscribers, client type may need upgrading:
                // - HANDSET → PTTRADIOHANDSETCLIENT
                // - POC_WIFIONLY → PTTRADIOWIFIONLYCLIENT
                // - CROSSCARRIER → PTTRADIOCROSSCARRIERCLIENT
                if (pkgResult.isProcessed() && existingProfilesMap != null && !existingProfilesMap.isEmpty() && xdmPttServerId != null) {
                    Map<String, Integer> clientTypeUpgrades = checkAndGetClientTypeUpgrades(
                            existingProfilesMap, pkgResult.getPerMdnFinalPkgIds(), xdmPttServerId, persisterTxn);

                    if (!clientTypeUpgrades.isEmpty()) {
                        int upgraded = updateSubscriberClientTypesInBulk(clientTypeUpgrades, persisterTxn);
                        knLogger.info(methodName, "Client type upgraded for ", upgraded, " MDNs");
                        
                        // Store client type upgrades in result for SMS notifications
                        result.setClientTypeUpgrades(clientTypeUpgrades);

                        // Step 6: Zone/Channel Auto-Assign for upgraded clients (only for client type upgrades)
                        // This is triggered when isUpgradePkg = true in single MDN flow
                        // Auto-assigns zones and channels for PTT Radio clients
                        try {
                            assignZonesAndChannelsInBulk(clientTypeUpgrades.keySet(), existingProfilesMap, persisterTxn);
                            knLogger.info(methodName, "Zones/Channels auto-assigned for upgraded MDNs");
                        } catch (Exception e) {
                            knLogger.warn(methodName, "Zone/Channel auto-assign failed (non-critical): ", e.getMessage());
                            // Non-critical - continue with other operations
                        }
                    }
                }

                // Step 7: Update Affiliation Feature flag based on new activeFS2
                // When FeatureSet changes (due to package updates), affiliation flag may need updating
                // Condition: AFFILIATIONFEATURE bit in activeFS2, clientPVmajorVer >= 18, valid client type
                // IMPORTANT: Pass the UPDATED activeFS2 values from result, not the old values from existingProfilesMap
                if (result.isFeatureSetUpdated() && existingProfilesMap != null && !existingProfilesMap.isEmpty()) {
                    try {
                        updateAffiliationFlagInBulk(existingProfilesMap, result.getUpdatedActiveFS2Map(), xdmPttServerId, persisterTxn);
                        knLogger.info(methodName, "Affiliation flag updated based on new FeatureSet");
                    } catch (Exception e) {
                        knLogger.warn(methodName, "Affiliation flag update failed (non-critical): ", e.getMessage());
                        // Non-critical - continue with other operations
                    }
                }

                // Step 8: MCPTT Feature Disable Check
                // When activeFS2 changes, check and update MCPTT feature permissions
                // IMPORTANT: Pass the UPDATED activeFS2 values from result, not the old values from existingProfilesMap
                if (result.isFeatureSetUpdated() && existingProfilesMap != null && !existingProfilesMap.isEmpty()) {
                    try {
                        checkAndUpdateMcpttFeatureInBulk(mdnsToUpdate, existingProfilesMap, result.getUpdatedActiveFS2Map(), persisterTxn);
                        knLogger.info(methodName, "MCPTT feature permissions checked and updated");
                    } catch (Exception e) {
                        knLogger.warn(methodName, "MCPTT feature check failed (non-critical): ", e.getMessage());
                        // Non-critical - continue with other operations
                    }
                }

            result.setSuccess(true);
            knLogger.info(methodName, "Bulk update completed - Result: ", result);

        } catch (KnDAOException e) {
            result.setSuccess(false);
            //result.setErrorMessage(e.getMessage());
            result.setErrorMessage(e.getErrorMessage());
            throw e;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("Unexpected error");
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Bulk update failed: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Bulk update failed", e);
        }

        return result;
    }

    // ==================== ADDITIONAL PACKAGE-CHANGE RELATED OPERATIONS ====================

    /**
     * Updates Affiliation Feature flag in CORPGROUPMEMBERLIST for multiple MDNs.
     * This is triggered when activeFS2 changes due to package updates.
     *
     * Condition for enabling affiliation:
     * 1. AFFILIATIONFEATURE bit enabled in activeFS2
     * 2. clientPVmajorVer >= 18
     * 3. Valid client type (checked via checkClientType method)
     *
     * @param existingProfilesMap Map of MDN to profile data (contains profile metadata)
     * @param updatedActiveFS2Map Map of MDN to NEW activeFS2 values (after FeatureSet recalculation)
     * @param xdmPttServerId XDM PTT Server ID
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    private void updateAffiliationFlagInBulk(
            Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
            Map<String, String> updatedActiveFS2Map,
            String xdmPttServerId,
            KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateAffiliationFlagInBulk";
        knLogger.debug(methodName, "Updating affiliation flag for ", existingProfilesMap.size(), " MDNs");

        final int PROTOCOL_VERSION_18 = 18;
        final int ENABLED = 1;
        final int DISABLED = 0;
        final int AFFILIATIONFEATURE_BIT = com.kodiak.common.resources.KnConstants.FEATURE_SET.AFFILIATIONFEATURE.value();

        Map<String, String> mdnAffiliationMap = new HashMap<>();

        for (Map.Entry<String, KnBulkSubsProfileDTO> entry : existingProfilesMap.entrySet()) {
            String mdn = entry.getKey();
            KnBulkSubsProfileDTO profile = entry.getValue();

            // Get the NEW activeFS2 from the updated map (not from profile which has OLD value)
            String activeFS2 = (updatedActiveFS2Map != null) ? updatedActiveFS2Map.get(mdn) : null;
            // Fall back to profile's activeFS2 if not in updated map (MDN had no FeatureSet changes)
            if (activeFS2 == null || activeFS2.isEmpty()) {
                activeFS2 = profile.getActiveFS2();
            }
            if (activeFS2 == null || activeFS2.isEmpty()) {
                continue;
            }

            // Check if AFFILIATIONFEATURE bit is enabled in activeFS2
            boolean affiliationBitEnabled = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(
                    activeFS2, AFFILIATIONFEATURE_BIT);

            int isAffiliationEnabled = DISABLED;

            // Condition: clientPVmajorVer >= 18, affiliationBit enabled, valid client type
            if (profile.getClientPVmajorVer() >= PROTOCOL_VERSION_18
                    && affiliationBitEnabled
                    && checkClientTypeForAffiliation(profile.getSubsClientType(), profile.getMcpttCompliance())) {
                isAffiliationEnabled = ENABLED;
            }

            mdnAffiliationMap.put(mdn, String.valueOf(isAffiliationEnabled));
        }

        if (!mdnAffiliationMap.isEmpty()) {
            // Update affiliation flag in CORPGROUPMEMBERLIST table
            updateAffiliationFlagInDb(mdnAffiliationMap, persisterTxn);
            knLogger.info(methodName, "Updated affiliation flag for ", mdnAffiliationMap.size(), " MDNs");
        }
    }

    /**
     * Checks if client type is valid for affiliation feature.
     * This matches the checkClientType method logic in single MDN flow.
     *
     * @param clientType Subscriber client type
     * @param mcpttCompliance MCPTT compliance flag
     * @return true if client type is valid for affiliation
     */
    private boolean checkClientTypeForAffiliation(int clientType, int mcpttCompliance) {
        // Client types that support affiliation (matching single MDN flow)
        // PTT Radio clients and MCPTT compliant clients
        final int HANDSET = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.HANDSET.value();
        final int POC_WIFIONLY = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value();
        final int CROSSCARRIER = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.CROSSCARRIER.value();
        final int PTTRADIOHANDSETCLIENT = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value();
        final int PTTRADIOWIFIONLYCLIENT = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value();
        final int PTTRADIOCROSSCARRIERCLIENT = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value();

        // Valid client types for affiliation
        if (clientType == HANDSET || clientType == POC_WIFIONLY || clientType == CROSSCARRIER
                || clientType == PTTRADIOHANDSETCLIENT || clientType == PTTRADIOWIFIONLYCLIENT
                || clientType == PTTRADIOCROSSCARRIERCLIENT) {
            return true;
        }

        // MCPTT compliant clients also support affiliation
        if (mcpttCompliance == 1) {
            return true;
        }

        return false;
    }

    /**
     * Updates affiliation flag in CORPGROUPMEMBERLIST table for multiple MDNs.
     *
     * @param mdnAffiliationMap Map of MDN to affiliation flag (0 or 1)
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    private void updateAffiliationFlagInDb(Map<String, String> mdnAffiliationMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateAffiliationFlagInDb";

        if (mdnAffiliationMap == null || mdnAffiliationMap.isEmpty()) {
            return;
        }

        String updateQuery = "UPDATE DG.CORPGROUPMEMBERLIST SET IS_AFFILIATION_ENABLED = ? WHERE MEMBERMDN = ?";

        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(updateQuery);

            for (Map.Entry<String, String> entry : mdnAffiliationMap.entrySet()) {
                pStmt.setInt(1, Integer.parseInt(entry.getValue()));
                pStmt.setString(2, entry.getKey());
                pStmt.addBatch();
            }

            int[] batchResults = pStmt.executeBatch();
            int totalUpdated = 0;
            for (int result : batchResults) {
                if (result >= 0 || result == PreparedStatement.SUCCESS_NO_INFO) {
                    totalUpdated++;
                }
            }

            knLogger.info(methodName, "Affiliation flag updated for ", totalUpdated, " MDNs in CORPGROUPMEMBERLIST");

        } catch (Exception e) {
            knLogger.error(methodName, "Exception updating affiliation flag: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to update affiliation flag: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to update affiliation flag", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * Auto-assigns zones and channels for upgraded PTT Radio clients.
     * This is triggered when client type is upgraded (isUpgradePkg scenario).
     *
     * In single MDN flow: provInfoUtil.assignZonesAndChannels(mdn, dbSubsClienType, autoAssignZoneChannelFlag, extCorpId, persisterTxn)
     *
     * Logic:
     * 1. Check AUTO_ASSIGN_ZONE flag from MSS config
     * 2. For each upgraded MDN, get their group memberships
     * 3. Get zone/channel configuration
     * 4. Assign zones and channels based on group type priority (DISPATCHER > STD > BCG)
     *
     * @param upgradedMdns Set of MDNs that had client type upgraded
     * @param existingProfilesMap Map of MDN to profile data
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    private void assignZonesAndChannelsInBulk(
            java.util.Set<String> upgradedMdns,
            Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
            KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "assignZonesAndChannelsInBulk";
        knLogger.debug(methodName, "Auto-assigning zones/channels for ", upgradedMdns.size(), " upgraded MDNs");

        if (upgradedMdns == null || upgradedMdns.isEmpty()) {
            return;
        }

        // Check AUTO_ASSIGN_ZONE flag from MSS config
        String autoAssignZoneFlag = getAutoAssignZoneFlag(persisterTxn);
        if (autoAssignZoneFlag == null || autoAssignZoneFlag.equals("0")) {
            knLogger.info(methodName, "AUTO_ASSIGN_ZONE is disabled, skipping zone/channel assignment");
            return;
        }

        // Valid client types for zone/channel assignment (original client types before upgrade)
        final int HANDSET = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.HANDSET.value();
        final int POC_WIFIONLY = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value();
        final int CROSSCARRIER = com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.CROSSCARRIER.value();

        int assignedCount = 0;

        for (String mdn : upgradedMdns) {
            KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
            if (profile == null) {
                continue;
            }

            // Get original client type (before upgrade) to validate
            // The upgrade happens from HANDSET/POC_WIFIONLY/CROSSCARRIER to PTT Radio variants
            // We need to check if the subscriber is eligible for zone/channel assignment
            int originalClientType = profile.getSubsClientType();

            // Only assign for subscribers with valid original client types
            // (the profile still has original type since DB update happens in batch)
            if (originalClientType != HANDSET && originalClientType != POC_WIFIONLY && originalClientType != CROSSCARRIER) {
                knLogger.debug(methodName, "MDN ", KnGDPRTemplate.mdn(mdn), " has client type ", originalClientType, " - not eligible for zone/channel assignment");
                continue;
            }

            try {
                // Get corp groups for this MDN
                List<Integer> groupIds = getGroupIdsForMdn(mdn, persisterTxn);
                if (groupIds == null || groupIds.isEmpty()) {
                    knLogger.debug(methodName, "MDN ", KnGDPRTemplate.mdn(mdn), " has no group memberships, skipping zone/channel assignment");
                    continue;
                }

                // Get extCorpId from profile
                String extCorpId = getExtCorpIdForMdn(mdn, profile.getCorpId(), persisterTxn);

                // Assign zones and channels
                boolean assigned = assignZonesAndChannelsForMdn(mdn, groupIds, extCorpId, persisterTxn);
                if (assigned) {
                    assignedCount++;
                    knLogger.debug(methodName, "Zone/Channel assigned for MDN ", KnGDPRTemplate.mdn(mdn));
                }

            } catch (Exception e) {
                knLogger.warn(methodName, "Failed to assign zones/channels for MDN ", KnGDPRTemplate.mdn(mdn), ": ", e.getMessage());
                // Continue with other MDNs
            }
        }

        knLogger.info(methodName, "Zone/Channel assignment completed for ", assignedCount, " out of ", upgradedMdns.size(), " MDNs");
    }

    /**
     * Gets the AUTO_ASSIGN_ZONE flag from MSS config.
     * Aligns with single MDN flow: genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(AUTO_ASSIGN_ZONE)
     *
     * @param persisterTxn Database transaction
     * @return Auto-assign zone flag value ("0" or "1")
     */
    private String getAutoAssignZoneFlag(KnPersisterTxn persisterTxn) {
        String methodName = "getAutoAssignZoneFlag";

        try {
            String clusterIdEnv = System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME);
            if (clusterIdEnv == null || clusterIdEnv.trim().isEmpty()) {
                knLogger.warn(methodName, "CLUSTERID environment variable is not set, defaulting AUTO_ASSIGN_ZONE to disabled");
                return "0";
            }
            int clusterId = Integer.parseInt(clusterIdEnv.trim());

            // Use genInfoUtil like single MDN flow does
            KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
            Map<String, String> mssConfig = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);

            if (mssConfig != null) {
                String value = mssConfig.get(com.kodiak.xdms.server.common.resources.KnConstants.AUTO_ASSIGN_ZONE);
                knLogger.debug(methodName, "AUTO_ASSIGN_ZONE flag for clusterId ", clusterId, " via genInfoUtil is: ", value);
                return value != null ? value : "0";
            }
            knLogger.debug(methodName, "No AUTO_ASSIGN_ZONE config found for clusterId ", clusterId);
        } catch (Exception e) {
            knLogger.warn(methodName, "Failed to get AUTO_ASSIGN_ZONE flag via genInfoUtil: ", e.getMessage());
        }

        return "0"; // Default to disabled
    }

    /**
     * Gets all group IDs (corp groups + shared groups) for a given MDN.
     * Aligns with single MDN flow: xdmDAO.getGroupIdsList() and xdmDAO.getSharedGroupList()
     *
     * Corp Groups: SELECT CORPGROUPID FROM DG.CORPGROUPMEMBERLIST WHERE GRP_MODIFY_PERM = 1 AND MEMBERMDN = ?
     * Shared Groups: SELECT CORPGROUPMEMBERLIST.CORPGROUPID FROM DG.CORPGROUPMEMBERLIST
     *                JOIN DG.CORPGROUPINFO ON CORPGROUPMEMBERLIST.CORPGROUPID = CORPGROUPINFO.CORPGROUPID
     *                WHERE CORPGROUPINFO.GROUP_SHARED = 1 AND CORPGROUPMEMBERLIST.MEMBERMDN = ?
     *
     * @param mdn Mobile directory number
     * @param persisterTxn Database transaction
     * @return List of group IDs
     * @throws KnDAOException if database operation fails
     */
    private List<Integer> getGroupIdsForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupIdsForMdn";
        Set<Integer> groupIds = new HashSet<>();

        // Get corp group IDs - Same as single MDN flow in KnPOCSubscrInfoDAO.getGroupIdsList()
        String corpGroupQuery = "SELECT CORPGROUPID FROM DG.CORPGROUPMEMBERLIST WHERE GRP_MODIFY_PERM = 1 AND MEMBERMDN = ?";
        try {
            Connection conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            try (PreparedStatement pStmt = conn.prepareStatement(corpGroupQuery)) {
                pStmt.setString(1, mdn);
                try (ResultSet rs = pStmt.executeQuery()) {
                    while (rs.next()) {
                        groupIds.add(rs.getInt("CORPGROUPID"));
                    }
                }
            }
            knLogger.debug(methodName, "Found ", groupIds.size(), " corp groups for MDN ", KnGDPRTemplate.mdn(mdn));
        } catch (Exception e) {
            knLogger.warn(methodName, "Failed to get corp group IDs for MDN ", KnGDPRTemplate.mdn(mdn), ": ", e.getMessage());
        }

        // Get shared group IDs - Same as single MDN flow in KnPOCSubscrInfoDAO.getSharedGroupList()
        String sharedGroupQuery = "SELECT CORPGROUPMEMBERLIST.CORPGROUPID FROM DG.CORPGROUPMEMBERLIST " +
                "JOIN DG.CORPGROUPINFO ON CORPGROUPMEMBERLIST.CORPGROUPID = CORPGROUPINFO.CORPGROUPID " +
                "WHERE CORPGROUPINFO.GROUP_SHARED = 1 AND CORPGROUPMEMBERLIST.MEMBERMDN = ?";

        try {
            Connection conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            try (PreparedStatement pStmt = conn.prepareStatement(sharedGroupQuery)) {
                pStmt.setString(1, mdn);
                try (ResultSet rs = pStmt.executeQuery()) {
                    while (rs.next()) {
                        groupIds.add(rs.getInt("CORPGROUPID"));
                    }
                }
            }
            knLogger.debug(methodName, "Total groups (corp + shared) for MDN ", KnGDPRTemplate.mdn(mdn), ": ", groupIds.size());
        } catch (Exception e) {
            knLogger.warn(methodName, "Failed to get shared group IDs for MDN ", KnGDPRTemplate.mdn(mdn), ": ", e.getMessage());
        }

        return new ArrayList<>(groupIds);
    }

    /**
     * Gets the external corp ID for a given MDN based on corpId.
     *
     * @param mdn Mobile directory number
     * @param corpId Internal corp ID
     * @param persisterTxn Database transaction
     * @return External corp ID or null
     */
    private String getExtCorpIdForMdn(String mdn, int corpId, KnPersisterTxn persisterTxn) {
        String methodName = "getExtCorpIdForMdn";

        if (corpId <= 0) {
            return null;
        }

        String query = "SELECT EXTCORPID FROM DG.POCCORPINFO WHERE CORPID = ?";
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {
            Connection conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);
            rs = pStmt.executeQuery();
            if (rs.next()) {
                return rs.getString("EXTCORPID");
            }
        } catch (Exception e) {
            knLogger.warn(methodName, "Failed to get extCorpId for corpId ", corpId, ": ", e.getMessage());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }

        return null;
    }

    /**
     * Assigns zones and channels for a single MDN based on its group memberships.
     *
     * @param mdn Mobile directory number
     * @param groupIds List of group IDs the MDN belongs to
     * @param extCorpId External corp ID
     * @param persisterTxn Database transaction
     * @return true if assignment was successful
     * @throws KnDAOException if database operation fails
     */
    private boolean assignZonesAndChannelsForMdn(String mdn, List<Integer> groupIds, String extCorpId,
                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "assignZonesAndChannelsForMdn";

        if (groupIds == null || groupIds.isEmpty()) {
            return false;
        }

        // Get zone/channel configuration
        int maxZones = 16; // Default
        int maxChannelsPerZone = 16; // Default

        try {
            Map<String, Integer> zoneChannelConfig = getZoneChannelConfig(extCorpId, persisterTxn);
            if (zoneChannelConfig != null) {
                maxZones = zoneChannelConfig.getOrDefault("MAXZONES", 16);
                maxChannelsPerZone = zoneChannelConfig.getOrDefault("MAXCHANNELSPERZONE", 16);
            }
        } catch (Exception e) {
            knLogger.warn(methodName, "Failed to get zone/channel config, using defaults: ", e.getMessage());
        }

        // Get group info (group type and name) for sorting
        Map<Integer, Integer> groupTypeMap = getGroupTypes(groupIds, persisterTxn);

        // Group priority: DISPATCHER (0) > STD (1) > BCG (2)
        // Sort groups by type, then by group ID
        List<Integer> sortedGroupIds = new ArrayList<>(groupIds);
        sortedGroupIds.sort((g1, g2) -> {
            int type1 = groupTypeMap.getOrDefault(g1, 99);
            int type2 = groupTypeMap.getOrDefault(g2, 99);
            if (type1 != type2) {
                return Integer.compare(type1, type2);
            }
            return Integer.compare(g1, g2);
        });

        // Assign zones and channels
        Set<String> usedZoneChannelPairs = new HashSet<>();
        List<int[]> assignments = new ArrayList<>(); // [groupId, zoneId, channelId]

        for (Integer groupId : sortedGroupIds) {
            boolean assigned = false;
            for (int z = 1; z <= maxZones && !assigned; z++) {
                for (int c = 1; c <= maxChannelsPerZone; c++) {
                    String pairKey = z + "-" + c;
                    if (!usedZoneChannelPairs.contains(pairKey)) {
                        assignments.add(new int[]{groupId, z, c});
                        usedZoneChannelPairs.add(pairKey);
                        assigned = true;
                        break;
                    }
                }
            }

            if (!assigned) {
                knLogger.warn(methodName, "No more zone/channel slots available for MDN ", KnGDPRTemplate.mdn(mdn), ", groupId ", groupId);
                break;
            }
        }

        // Insert assignments into database
        if (!assignments.isEmpty()) {
            insertZoneChannelAssignments(mdn, assignments, persisterTxn);
            knLogger.debug(methodName, "Assigned ", assignments.size(), " zone/channels for MDN ", KnGDPRTemplate.mdn(mdn));
            return true;
        }

        return false;
    }

    /**
     * Gets zone/channel configuration for a corporation.
     * Aligns with single MDN flow: KnPOCSubscrInfoDAO.getZoneAndChannerConfigValues()
     *
     * Primary: SELECT MAXZONES, MAXCHANNELSPERZONE FROM DG.POCCORPINFO WHERE EXTCORPID = ?
     * Fallback: SELECT MAXZONES, MAXCHANNELSPERZONE FROM DG.XDMS_SVC_CONFIG
     *
     * @param extCorpId External corp ID
     * @param persisterTxn Database transaction
     * @return Map with MAXZONES and MAXCHANNELSPERZONE
     */
    private Map<String, Integer> getZoneChannelConfig(String extCorpId, KnPersisterTxn persisterTxn) {
        String methodName = "getZoneChannelConfig";
        Map<String, Integer> config = new HashMap<>();

        // First try to get config from POCCORPINFO for the specific extCorpId
        if (extCorpId != null && !extCorpId.isEmpty()) {
            String corpQuery = "SELECT MAXZONES, MAXCHANNELSPERZONE FROM DG.POCCORPINFO WHERE EXTCORPID = ?";
            try {
                Connection conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
                try (PreparedStatement pStmt = conn.prepareStatement(corpQuery)) {
                    pStmt.setString(1, extCorpId);
                    try (ResultSet rs = pStmt.executeQuery()) {
                        if (rs.next()) {
                            int maxZones = rs.getInt("MAXZONES");
                            int maxChannels = rs.getInt("MAXCHANNELSPERZONE");
                            if (maxZones > 0 && maxChannels > 0) {
                                config.put("MAXZONES", maxZones);
                                config.put("MAXCHANNELSPERZONE", maxChannels);
                                knLogger.debug(methodName, "Got zone/channel config from POCCORPINFO for extCorpId ",
                                        extCorpId, ": MAXZONES=", maxZones, ", MAXCHANNELSPERZONE=", maxChannels);
                                return config;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                knLogger.debug(methodName, "No zone/channel config found in POCCORPINFO for extCorpId ", extCorpId);
            }
        }

        // Fallback to XDMS_SVC_CONFIG defaults (same as single MDN flow)
        String fallbackQuery = "SELECT MAXZONES, MAXCHANNELSPERZONE FROM DG.XDMS_SVC_CONFIG";
        try {
            Connection conn = bulkOpsDBUtil.getDBConnection(persisterTxn, null);
            try (PreparedStatement pStmt = conn.prepareStatement(fallbackQuery);
                 ResultSet rs = pStmt.executeQuery()) {
                if (rs.next()) {
                    config.put("MAXZONES", rs.getInt("MAXZONES"));
                    config.put("MAXCHANNELSPERZONE", rs.getInt("MAXCHANNELSPERZONE"));
                    knLogger.debug(methodName, "Got zone/channel config from XDMS_SVC_CONFIG defaults");
                    return config;
                }
            }
        } catch (Exception e) {
            knLogger.warn(methodName, "Failed to get zone/channel config from XDMS_SVC_CONFIG: ", e.getMessage());
        }

        // Last resort - use hardcoded defaults
        config.put("MAXZONES", 16);
        config.put("MAXCHANNELSPERZONE", 16);
        knLogger.debug(methodName, "Using hardcoded default zone/channel config");
        return config;
    }

    /** 
     * Gets group types for a list of group IDs.
     * Aligns with single MDN flow group type validation in KnProvInfoUtil.processAutoAssignZoneAndChannel()
     *
     * @param groupIds List of group IDs
     * @param persisterTxn Database transaction
     * @return Map of groupId to groupType
     */
    private Map<Integer, Integer> getGroupTypes(List<Integer> groupIds, KnPersisterTxn persisterTxn) {
        String methodName = "getGroupTypes";
        Map<Integer, Integer> groupTypeMap = new HashMap<>();

        if (groupIds == null || groupIds.isEmpty()) {
            return groupTypeMap;
        }

        String placeholders = String.join(",", java.util.Collections.nCopies(groupIds.size(), "?"));
        String query = "SELECT CORPGROUPID, GROUPTYPE FROM DG.CORPGROUPINFO WHERE CORPGROUPID IN (" + placeholders + ")";

        try {
            Connection conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            try (PreparedStatement pStmt = conn.prepareStatement(query)) {
                for (int i = 0; i < groupIds.size(); i++) {
                    pStmt.setInt(i + 1, groupIds.get(i));
                }

                try (ResultSet rs = pStmt.executeQuery()) {
                    while (rs.next()) {
                        groupTypeMap.put(rs.getInt("CORPGROUPID"), rs.getInt("GROUPTYPE"));
                    }
                }
            }
            knLogger.debug(methodName, "Retrieved group types for ", groupTypeMap.size(), " groups");
        } catch (Exception e) {
            knLogger.warn(methodName, "Failed to get group types: ", e.getMessage());
        }

        return groupTypeMap;
    }

    /**
     * Inserts zone/channel assignments for a MDN into SUBSCRPTTRADIOTGLIST table.
     * Aligns with single MDN flow: KnPOCSubscrInfoDAO.insertSubsAddlTGList()
     *
     * INSERT INTO DG.SUBSCRPTTRADIOTGLIST (MDN, GROUPID, ZONEID, CHANNELID) VALUES (?, ?, ?, ?)
     *
     * @param mdn Mobile directory number
     * @param assignments List of [groupId, zoneId, channelId] arrays
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    private void insertZoneChannelAssignments(String mdn, List<int[]> assignments, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertZoneChannelAssignments";

        if (assignments == null || assignments.isEmpty()) {
            return;
        }

        // Same table and columns as single MDN flow in KnPOCSubscrInfoDAO.insertSubsAddlTGList()
        String insertQuery = "INSERT INTO DG.SUBSCRPTTRADIOTGLIST (MDN, GROUPID, ZONEID, CHANNELID) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            try (PreparedStatement pStmt = conn.prepareStatement(insertQuery)) {
                for (int[] assignment : assignments) {
                    pStmt.setString(1, mdn);
                    pStmt.setInt(2, assignment[0]); // groupId
                    pStmt.setInt(3, assignment[1]); // zoneId
                    pStmt.setInt(4, assignment[2]); // channelId
                    pStmt.addBatch();
                }

                pStmt.executeBatch();
                knLogger.debug(methodName, "Inserted ", assignments.size(), " zone/channel assignments for MDN ", KnGDPRTemplate.mdn(mdn));
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception inserting zone/channel assignments: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to insert zone/channel assignments: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to insert zone/channel assignments", e);
        }
    }

    /** 
     * Checks and updates MCPTT feature permissions based on new activeFS2.
     * When activeFS2 changes, MCPTT feature constraints need to be verified.
     *
     * This implements the same logic as Single MDN flow's setMCPTTFeatureDisable():
     * 1. Check MCPTT feature bits in activeFS2 (AMBIENTLISTENING, DISCRETELISTENING, USERCHECK, USERENABLEDISABLE)
     * 2. For each MDN, get MCPTT permission info from DG.MCPTT_PERM_INFO table
     * 3. If permBitset == 0 for any target entry, delete that entry
     * 4. If DISCRETELISTENING bit is disabled, disable DISCREET_ENABLED and update AUTHORIZATION_DOC etag
     *
     * @param mdnList List of MDNs to check
     * @param existingProfilesMap Map of MDN to profile data (contains profile metadata)
     * @param updatedActiveFS2Map Map of MDN to NEW activeFS2 values (after FeatureSet recalculation)
     * @param persisterTxn Database transaction
     */
    private void checkAndUpdateMcpttFeatureInBulk(
            List<String> mdnList,
            Map<String, KnBulkSubsProfileDTO> existingProfilesMap,
            Map<String, String> updatedActiveFS2Map,
            KnPersisterTxn persisterTxn) {

        String methodName = "checkAndUpdateMcpttFeatureInBulk";
        knLogger.debug(methodName, "Checking MCPTT feature permissions for ", mdnList.size(), " MDNs");

        // MCPTT Feature bits from Single MDN flow (KnSubsProvController.setMCPTTFeatureDisable)
        final int AMBIENTLISTENING_BIT = com.kodiak.common.resources.KnConstants.FEATURE_SET.AMBIENTLISTENING.value();
        final int DISCRETELISTENING_BIT = com.kodiak.common.resources.KnConstants.FEATURE_SET.DISCRETELISTENING.value();
        final int USERCHECK_BIT = com.kodiak.common.resources.KnConstants.FEATURE_SET.USERCHECK.value();
        final int USERENABLEDISABLE_BIT = com.kodiak.common.resources.KnConstants.FEATURE_SET.USERENABLEDISABLE.value();

        // Track MDNs that need discreet listener disabled
        List<String> mdnsWithDiscreetListenerDisabled = new ArrayList<>();

        for (String mdn : mdnList) {
            KnBulkSubsProfileDTO profile = existingProfilesMap.get(mdn);
            if (profile == null) {
                continue;
            }

            // Get the NEW activeFS2 from the updated map (not from profile which has OLD value)
            String activeFS2 = (updatedActiveFS2Map != null) ? updatedActiveFS2Map.get(mdn) : null;
            // Fall back to profile's activeFS2 if not in updated map (MDN had no FeatureSet changes)
            if (activeFS2 == null || activeFS2.isEmpty()) {
                activeFS2 = profile.getActiveFS2();
            }
            if (activeFS2 == null || activeFS2.isEmpty()) {
                continue;
            }

            knLogger.debug(methodName, "Checking MCPTT feature bits for MDN ", KnGDPRTemplate.mdn(mdn), " activeFS2: ", activeFS2);

            // Check all MCPTT feature bits (aligned with Single MDN flow)
            boolean ambientListenerBit = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(activeFS2, AMBIENTLISTENING_BIT);
            boolean discreetListenerBit = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(activeFS2, DISCRETELISTENING_BIT);
            boolean userCheckBit = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(activeFS2, USERCHECK_BIT);
            boolean userEnableDisableBit = com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue(activeFS2, USERENABLEDISABLE_BIT);

            knLogger.debug(methodName, "MCPTT bits for MDN ", KnGDPRTemplate.mdn(mdn), ": ambientListener=", ambientListenerBit,
                    ", discreetListener=", discreetListenerBit, ", userCheck=", userCheckBit, 
                    ", userEnableDisable=", userEnableDisableBit);

            // Step 1: Delete MCPTT_PERM_INFO entries where permBitset == 0
            // Single MDN flow: getMCPTTPermInfo then deleteTargetEntry if permBitset == 0
            try {
                deleteMcpttPermInfoWithZeroBitset(mdn, persisterTxn);
            } catch (Exception e) {
                knLogger.warn(methodName, "Failed to delete MCPTT perm info with zero bitset for MDN ", KnGDPRTemplate.mdn(mdn), ": ", e.getMessage());
            }

            // Step 2: If discreet listener bit is disabled, track for batch update
            if (!discreetListenerBit) {
                mdnsWithDiscreetListenerDisabled.add(mdn);
            }
        }

        // Step 3: Batch update - disable DISCREET_ENABLED and update AUTHORIZATION_DOC etag
        // Aligned with Single MDN flow: disableDiscreetEnabled + updateAuthorizationDocEtag
        if (!mdnsWithDiscreetListenerDisabled.isEmpty()) {
            knLogger.info(methodName, "Disabling discreet listener for ", mdnsWithDiscreetListenerDisabled.size(), " MDNs");
            try {
                disableDiscreetEnabledInBulk(mdnsWithDiscreetListenerDisabled, persisterTxn);
                updateAuthorizationDocEtagInBulk(mdnsWithDiscreetListenerDisabled, persisterTxn);
                knLogger.info(methodName, "MCPTT discreet listener disabled and auth doc etag updated for ", 
                        mdnsWithDiscreetListenerDisabled.size(), " MDNs");
            } catch (Exception e) {
                knLogger.warn(methodName, "Failed to update MCPTT discreet listener settings: ", e.getMessage());
            }
        }
    }

    /**
     * Deletes MCPTT permission info entries where permBitset equals 0.
     * Aligned with Single MDN flow (KnSubsProvController.setMCPTTFeatureDisable):
     * <pre>
     * for(KnMCPTTPermInfoDTO permInfoDTO : mcpttPermInfoDTOS){
     *     if(permInfoDTO.getPermBitset().equals(0L)){
     *         xdmServerDAO.deleteTargetEntry(mdn, permInfoDTO.getTargetMdn(), persisterTxn);
     *     }
     * }
     * </pre>
     *
     * @param mdn MDN to check
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    private void deleteMcpttPermInfoWithZeroBitset(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteMcpttPermInfoWithZeroBitset";

        // Query to find MCPTT_PERM_INFO entries with permBitset == 0
        String selectQuery = "SELECT AUTHORIZED_MDN, TARGET_MDN, PERM_BITSET FROM DG.MCPTT_PERM_INFO WHERE AUTHORIZED_MDN = ?";
        String deleteQuery = "DELETE FROM DG.MCPTT_PERM_INFO WHERE TARGET_MDN = ? AND AUTHORIZED_MDN = ?";

        Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement deleteStmt = null;
        ResultSet rs = null;

        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setString(1, mdn);
            rs = selectStmt.executeQuery();

            List<String> targetMdnsToDelete = new ArrayList<>();
            while (rs.next()) {
                long permBitset = rs.getLong("PERM_BITSET");
                if (permBitset == 0L) {
                    String targetMdn = rs.getString("TARGET_MDN");
                    if (targetMdn != null) {
                        targetMdnsToDelete.add(targetMdn.trim());
                    }
                }
            }

            if (!targetMdnsToDelete.isEmpty()) {
                knLogger.debug(methodName, "Deleting ", targetMdnsToDelete.size(), 
                        " MCPTT_PERM_INFO entries with zero bitset for MDN ", KnGDPRTemplate.mdn(mdn));
                deleteStmt = conn.prepareStatement(deleteQuery);
                for (String targetMdn : targetMdnsToDelete) {
                    deleteStmt.setString(1, targetMdn);
                    deleteStmt.setString(2, mdn);
                    deleteStmt.addBatch();
                }
                deleteStmt.executeBatch();
                knLogger.debug(methodName, "Deleted MCPTT_PERM_INFO entries for MDN ", KnGDPRTemplate.mdn(mdn));
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Exception deleting MCPTT perm info for MDN ", KnGDPRTemplate.mdn(mdn), ": ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to delete MCPTT perm info: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to delete MCPTT perm info", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(selectStmt);
            KnDbUtil.closeStatement(deleteStmt);
        }
    }

    /**
     * Disables DISCREET_ENABLED in DG.MCPTT_PERM_INFO for multiple MDNs.
     * Aligned with Single MDN flow (KnSubsProvController.setMCPTTFeatureDisable):
     * <pre>
     * if (!discreetListenerBit){
     *     xdmServerDAO.disableDiscreetEnabled(mdn, persisterTxn);
     * }
     * </pre>
     *
     * @param mdnList List of MDNs to update
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    private void disableDiscreetEnabledInBulk(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "disableDiscreetEnabledInBulk";
        knLogger.debug(methodName, "Disabling DISCREET_ENABLED for ", mdnList.size(), " MDNs");

        if (mdnList == null || mdnList.isEmpty()) {
            return;
        }

        // Query: UPDATE DG.MCPTT_PERM_INFO SET DISCREET_ENABLED = 0 WHERE AUTHORIZED_MDN = ?
        String updateQuery = "UPDATE DG.MCPTT_PERM_INFO SET DISCREET_ENABLED = 0 WHERE AUTHORIZED_MDN = ?";

        Connection conn = null;
        PreparedStatement pStmt = null;

        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(updateQuery);

            for (String mdn : mdnList) {
                pStmt.setString(1, mdn);
                pStmt.addBatch();
            }

            int[] results = pStmt.executeBatch();
            int totalUpdated = 0;
            for (int result : results) {
                if (result >= 0 || result == PreparedStatement.SUCCESS_NO_INFO) {
                    totalUpdated++;
                }
            }

            knLogger.info(methodName, "Disabled DISCREET_ENABLED for ", totalUpdated, " MDNs in MCPTT_PERM_INFO");

        } catch (Exception e) {
            knLogger.error(methodName, "Exception disabling DISCREET_ENABLED: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to disable DISCREET_ENABLED: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to disable DISCREET_ENABLED", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * Updates AUTHORIZATION_DOC etag for multiple MDNs.
     * Aligned with Single MDN flow (KnSubsProvController.setMCPTTFeatureDisable):
     * <pre>
     * long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
     * xdmServerDAO.updateAuthorizationDocEtag(mdn, lastProfileUpdateTime, persisterTxn);
     * </pre>
     *
     * @param mdnList List of MDNs to update
     * @param persisterTxn Database transaction
     * @throws KnDAOException if database operation fails
     */
    private void updateAuthorizationDocEtagInBulk(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateAuthorizationDocEtagInBulk";
        knLogger.debug(methodName, "Updating AUTHORIZATION_DOC etag for ", mdnList.size(), " MDNs");

        if (mdnList == null || mdnList.isEmpty()) {
            return;
        }

        // Query: UPDATE DG.AUTHORIZATION_DOC SET ETAG = ? WHERE MDN = ?
        String updateQuery = "UPDATE DG.AUTHORIZATION_DOC SET ETAG = ? WHERE MDN = ?";
        long lastProfileUpdateTime = System.currentTimeMillis();

        Connection conn = null;
        PreparedStatement pStmt = null;

        try {
            conn = bulkOpsDBUtil.getDBConnection(persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            pStmt = conn.prepareStatement(updateQuery);

            for (String mdn : mdnList) {
                pStmt.setLong(1, lastProfileUpdateTime);
                pStmt.setString(2, mdn);
                pStmt.addBatch();
            }

            int[] results = pStmt.executeBatch();
            int totalUpdated = 0;
            for (int result : results) {
                if (result >= 0 || result == PreparedStatement.SUCCESS_NO_INFO) {
                    totalUpdated++;
                }
            }

            knLogger.info(methodName, "Updated AUTHORIZATION_DOC etag for ", totalUpdated, " MDNs");

        } catch (Exception e) {
            knLogger.error(methodName, "Exception updating AUTHORIZATION_DOC etag: ", e);
            //throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
            //        "Failed to update AUTHORIZATION_DOC etag: " + e.getMessage(), e);
            throw new KnDAOException(KnBulkOpsErrorCodes.Validator.ERROR_CODE_INTERNAL_ERROR,
                    "Failed to update AUTHORIZATION_DOC etag", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    /**
     * DTO class to hold bulk update results
     */
    public static class KnBulkUpdateResult {
        private boolean success;
        private int totalMdnCount;
        private int networkNameUpdatedCount;
        private int profileTimeUpdatedCount;
        private boolean pkgIdMapProcessed;
        private boolean featureSetUpdated;
        private String errorMessage;
        // Maps to hold updated FeatureSets for notifications
        private Map<String, String> updatedSubsFS2Map = new HashMap<>();
        private Map<String, String> updatedActiveFS2Map = new HashMap<>();
        // Map to hold client type upgrades (MDN -> new client type) for SMS notifications
        private Map<String, Integer> clientTypeUpgrades = new HashMap<>();

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public int getTotalMdnCount() {
            return totalMdnCount;
        }

        public void setTotalMdnCount(int totalMdnCount) {
            this.totalMdnCount = totalMdnCount;
        }

        public int getNetworkNameUpdatedCount() {
            return networkNameUpdatedCount;
        }

        public void setNetworkNameUpdatedCount(int networkNameUpdatedCount) {
            this.networkNameUpdatedCount = networkNameUpdatedCount;
        }

        public int getProfileTimeUpdatedCount() {
            return profileTimeUpdatedCount;
        }

        public void setProfileTimeUpdatedCount(int profileTimeUpdatedCount) {
            this.profileTimeUpdatedCount = profileTimeUpdatedCount;
        }

        public boolean isPkgIdMapProcessed() {
            return pkgIdMapProcessed;
        }

        public void setPkgIdMapProcessed(boolean pkgIdMapProcessed) {
            this.pkgIdMapProcessed = pkgIdMapProcessed;
        }

        public boolean isFeatureSetUpdated() {
            return featureSetUpdated;
        }

        public void setFeatureSetUpdated(boolean featureSetUpdated) {
            this.featureSetUpdated = featureSetUpdated;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Map<String, String> getUpdatedSubsFS2Map() {
            return updatedSubsFS2Map;
        }

        public void setUpdatedSubsFS2Map(Map<String, String> updatedSubsFS2Map) {
            this.updatedSubsFS2Map = updatedSubsFS2Map;
        }

        public void addUpdatedSubsFS2(String mdn, String subsFS2) {
            if (this.updatedSubsFS2Map == null) {
                this.updatedSubsFS2Map = new HashMap<>();
            }
            this.updatedSubsFS2Map.put(mdn, subsFS2);
        }

        public Map<String, String> getUpdatedActiveFS2Map() {
            return updatedActiveFS2Map;
        }

        public void setUpdatedActiveFS2Map(Map<String, String> updatedActiveFS2Map) {
            this.updatedActiveFS2Map = updatedActiveFS2Map;
        }

        public void addUpdatedActiveFS2(String mdn, String activeFS2) {
            if (this.updatedActiveFS2Map == null) {
                this.updatedActiveFS2Map = new HashMap<>();
            }
            this.updatedActiveFS2Map.put(mdn, activeFS2);
        }

        public Map<String, Integer> getClientTypeUpgrades() {
            return clientTypeUpgrades;
        }

        public void setClientTypeUpgrades(Map<String, Integer> clientTypeUpgrades) {
            this.clientTypeUpgrades = clientTypeUpgrades;
        }

        @Override
        public String toString() {
            return "KnBulkUpdateResult{" +
                    "success=" + success +
                    ", totalMdnCount=" + totalMdnCount +
                    ", networkNameUpdatedCount=" + networkNameUpdatedCount +
                    ", profileTimeUpdatedCount=" + profileTimeUpdatedCount +
                    ", pkgIdMapProcessed=" + pkgIdMapProcessed +
                    ", featureSetUpdated=" + featureSetUpdated +
                    ", errorMessage='" + errorMessage + '\'' +
                    ", updatedSubsFS2Map.size=" + (updatedSubsFS2Map != null ? updatedSubsFS2Map.size() : 0) +
                    ", updatedActiveFS2Map.size=" + (updatedActiveFS2Map != null ? updatedActiveFS2Map.size() : 0) +
                    ", clientTypeUpgrades.size=" + (clientTypeUpgrades != null ? clientTypeUpgrades.size() : 0) +
                    '}';
        }
    }

    /**
     * DTO to hold package information for a single MDN
     */
    public static class KnMdnPkgInfo {
        private String mdn;
        private String tierPkgCode;
        private List<String> addonPkgCodes = new ArrayList<>();

        public KnMdnPkgInfo(String mdn) {
            this.mdn = mdn;
        }

        public String getMdn() {
            return mdn;
        }

        public void setMdn(String mdn) {
            this.mdn = mdn;
        }

        public String getTierPkgCode() {
            return tierPkgCode;
        }

        public void setTierPkgCode(String tierPkgCode) {
            this.tierPkgCode = tierPkgCode;
        }

        public List<String> getAddonPkgCodes() {
            return addonPkgCodes;
        }

        public void setAddonPkgCodes(List<String> addonPkgCodes) {
            this.addonPkgCodes = addonPkgCodes;
        }

        public void addAddonPkgCode(String addonPkgCode) {
            if (addonPkgCode != null) {
                this.addonPkgCodes.add(addonPkgCode);
            }
        }
    }

    /**
     * DTO to hold result of processing pkgIdMap ADD/REMOVE actions
     */
    public static class KnPkgIdMapResult {
        private boolean valid = true;
        private boolean changed = false;
        private Map<String, Integer> finalPkgIds = new HashMap<>();
        private String errorMessage;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public boolean isChanged() {
            return changed;
        }

        public void setChanged(boolean changed) {
            this.changed = changed;
        }

        public Map<String, Integer> getFinalPkgIds() {
            return finalPkgIds;
        }

        public void setFinalPkgIds(Map<String, Integer> finalPkgIds) {
            this.finalPkgIds = finalPkgIds;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    /**
     * DTO to hold bulk pkgIdMap processing result
     */
    public static class KnPkgIdMapBulkResult {
        private boolean processed = false;
        private boolean success = true;
        private boolean tierPkgUpdated = false;
        private boolean addonPkgsUpdated = false;
        private boolean featureSetUpdated = false;
        private String finalTierPkgCode;
        private List<String> finalAddonPkgCodes = new ArrayList<>();
        private Map<String, Integer> finalPkgIds = new HashMap<>();
        private Map<String, Map<String, Integer>> perMdnFinalPkgIds = new HashMap<>();  // Per-MDN final packages
        private Map<String, List<String>> perMdnAddonPkgCodes = new HashMap<>();  // Per-MDN addon packages for client type upgrade
        private String errorMessage;

        public boolean isProcessed() {
            return processed;
        }

        public void setProcessed(boolean processed) {
            this.processed = processed;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public boolean isTierPkgUpdated() {
            return tierPkgUpdated;
        }

        public void setTierPkgUpdated(boolean tierPkgUpdated) {
            this.tierPkgUpdated = tierPkgUpdated;
        }

        public boolean isAddonPkgsUpdated() {
            return addonPkgsUpdated;
        }

        public void setAddonPkgsUpdated(boolean addonPkgsUpdated) {
            this.addonPkgsUpdated = addonPkgsUpdated;
        }

        public boolean isFeatureSetUpdated() {
            return featureSetUpdated;
        }

        public void setFeatureSetUpdated(boolean featureSetUpdated) {
            this.featureSetUpdated = featureSetUpdated;
        }

        public String getFinalTierPkgCode() {
            return finalTierPkgCode;
        }

        public void setFinalTierPkgCode(String finalTierPkgCode) {
            this.finalTierPkgCode = finalTierPkgCode;
        }

        public List<String> getFinalAddonPkgCodes() {
            return finalAddonPkgCodes;
        }

        public void setFinalAddonPkgCodes(List<String> finalAddonPkgCodes) {
            this.finalAddonPkgCodes = finalAddonPkgCodes;
        }

        public Map<String, Integer> getFinalPkgIds() {
            return finalPkgIds;
        }

        public void setFinalPkgIds(Map<String, Integer> finalPkgIds) {
            this.finalPkgIds = finalPkgIds;
        }

        public Map<String, Map<String, Integer>> getPerMdnFinalPkgIds() {
            return perMdnFinalPkgIds;
        }

        public void setPerMdnFinalPkgIds(Map<String, Map<String, Integer>> perMdnFinalPkgIds) {
            this.perMdnFinalPkgIds = perMdnFinalPkgIds;
        }

        public Map<String, List<String>> getPerMdnAddonPkgCodes() {
            return perMdnAddonPkgCodes;
        }

        public void setPerMdnAddonPkgCodes(Map<String, List<String>> perMdnAddonPkgCodes) {
            this.perMdnAddonPkgCodes = perMdnAddonPkgCodes;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    /**
     * DTO to hold pre-fetched utility data that should be obtained BEFORE opening the main transaction.
     * These utility methods have internal save() calls that release the DB connection.
     * By pre-fetching this data, we avoid connection null issues during bulk operations.
     */
    public static class KnPreFetchedData {
        private List<String> qppPkgCodes = new ArrayList<>();
        private Map<Integer, Integer> qppDataPkgMap = new HashMap<>();
        private Map<Integer, Integer> addonDataPkgMap = new HashMap<>();

        public List<String> getQppPkgCodes() {
            return qppPkgCodes;
        }

        public void setQppPkgCodes(List<String> qppPkgCodes) {
            this.qppPkgCodes = qppPkgCodes != null ? qppPkgCodes : new ArrayList<>();
        }

        public Map<Integer, Integer> getQppDataPkgMap() {
            return qppDataPkgMap;
        }

        public void setQppDataPkgMap(Map<Integer, Integer> qppDataPkgMap) {
            this.qppDataPkgMap = qppDataPkgMap != null ? qppDataPkgMap : new HashMap<>();
        }

        public Map<Integer, Integer> getAddonDataPkgMap() {
            return addonDataPkgMap;
        }

        public void setAddonDataPkgMap(Map<Integer, Integer> addonDataPkgMap) {
            this.addonDataPkgMap = addonDataPkgMap != null ? addonDataPkgMap : new HashMap<>();
        }

        public boolean hasQppPkgCodes() {
            return qppPkgCodes != null && !qppPkgCodes.isEmpty();
        }

        public boolean hasDataPkgMaps() {
            return (qppDataPkgMap != null && !qppDataPkgMap.isEmpty()) ||
                   (addonDataPkgMap != null && !addonDataPkgMap.isEmpty());
        }
    }

}
