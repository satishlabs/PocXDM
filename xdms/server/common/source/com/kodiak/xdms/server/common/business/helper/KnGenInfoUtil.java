/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnGenInfoUtil.java
 * Subsystem:   Provisioning Library
 * <p>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       1/2/11   7.0
 * <p>
 * <p>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * *************************************************************************
 */
package com.kodiak.xdms.server.common.business.helper;


import com.kodiak.common.cache.ICache;
import com.kodiak.common.cache.KnCache;
import com.kodiak.common.commdto.common.KnPTTSettingDocInfoDTO;
import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.dao.KnConnectionException;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.ggcache.KnGGCache;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.frameworks.messaging.common.dto.KnMqServiceConfig;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.common.commdto.response.KnXDMSubsProfileRespDTO;
import com.kodiak.common.exception.KnException;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.idgenerator.KnIdGeneratorImpl;
import com.kodiak.utilities.idgenerator.dao.KnTableInfoBean;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.configuration.KnConfigurationException;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManager;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpProfilePersistDTO;
import com.kodiak.xdms.server.common.resources.KnCacheKeys;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.dto.common.KnCorpUserProfileMCPTTConfig;



import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.common.resources.KnConstants.XCAP_MAX_PENDING_NOTIFYQ_SIZE;
import static com.kodiak.common.resources.KnConstants.XDM_MAX_NOTIFICATION_COUNT;
import static com.kodiak.common.resources.KnConstants.BACKWARD_COMPATIBILTY_REQ;
import static com.kodiak.common.resources.KnConstants.INVALID_POC_HOME;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;


public class KnGenInfoUtil implements Observer {
    private static final KnLogger knLogger = KnLogger.getLogger(KnGenInfoUtil.class);

    private static KnGenInfoUtil instance;
    private static boolean isInitialized = false;

    private String moduleName = "GenInfoUtil";
    private Map<String, List<String>> tableInfo = new HashMap<String, List<String>>();
    private Map<String, String> tableMap;

    private boolean isSuppressWaterMark = false;

    private KnGenInfoUtil() {
        tableMap = new HashMap<String, String>();
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.POC_SUPPORTED_DEVICES.value(), KnCacheKeys.POC_SUPPORTED_DEVICES);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.XDMS_SVC_CONFIG.value(), KnCacheKeys.XDMS_SVC_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.XDM_WEB_SUBSYSTEM_CONFIG.value(), KnCacheKeys.XDMS_SUBS_SYS_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.DIALPLANINFO.value(), KnCacheKeys.DIAL_PLAN_INFO);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.APNINFO.value(), KnCacheKeys.APN_INFO);
        //tableMap.put(KnConstants.CACHE_TABLE_LIST.APNPROFILEINFO.value(), KnCacheKeys.APN_XCAP_ROOT_URIS);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.APNPROFILEINFO.value(), KnCacheKeys.APN_CONFIG_INFO);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.POC_BLACKLIST_DEVICES.value(), KnCacheKeys.POC_BLACKLIST_DEVICES);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.ACTIVATIONCODECONFIG.value(), KnCacheKeys.ACTIVATIONCODECONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.GENERIC_POC_NNI_CONFIG.value(), KnCacheKeys.NNI_FEATURE_STATUS);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SIGNALINGCARDINFO.value(), KnCacheKeys.WEBCARD_IP_LIST);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SIGNALINGCARDADDLINFO.value(), KnCacheKeys.WEBCARD_IP_LIST);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.RTXENVVARIABLEINFO.value(), KnCacheKeys.RTX_VALUE_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.CLIENT_TYPE_CONFIG.value(), KnCacheKeys.CLIENT_TYPE_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SUPP_VOCODER_PROFILES.value(), KnCacheKeys.SUPP_VOCODER_PROFILE);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.MICROSVCS_COMMONCONFIG.value(), KnCacheKeys.MS_COMMON_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.MICROSVCS_CLUSTERINFO.value(), KnCacheKeys.MS_CLUSTER_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.MICROSVCS_SERVICECONFIG.value(), KnCacheKeys.MS_SVC_CONFIG_DOC);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.MQ_QUEUE_INFO.value(), KnCacheKeys.MQ_QUEUE_ROUTING_KEYS);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SERVICE_FQDN_INFO.value(), KnCacheKeys.PTX_URLS_CAUCH_BASE);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.ADDL_PROFILE_INFO.value(), KnCacheKeys.ADDL_PROFILE_INFO_BY_PKGTYPE);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.DATA_PACKAGE_INFO.value(), KnCacheKeys.DATA_PACKAGE_INFO);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.QPP_PCRF_PROFILE.value(), KnCacheKeys.QPP_PCRF_PROFILE);

        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.POC_SUPPORTED_DEVICES.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.POC_BLACKLIST_DEVICES.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.XDMS_SVC_CONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.XDM_WEB_SUBSYSTEM_CONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.DIALPLANINFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.APNINFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.APNPROFILEINFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.ACTIVATIONCODECONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.GENERIC_POC_NNI_CONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SIGNALINGCARDINFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SIGNALINGCARDADDLINFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.RTXENVVARIABLEINFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.CLIENT_TYPE_CONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SUPP_VOCODER_PROFILES.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.MICROSVCS_COMMONCONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.MICROSVCS_CLUSTERINFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.MICROSVCS_SERVICECONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.MQ_QUEUE_INFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SERVICE_FQDN_INFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.ADDL_PROFILE_INFO.value(),null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.DATA_PACKAGE_INFO.value(),null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.QPP_PCRF_PROFILE.value(),null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.ADDL_PROFILE_INFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.DATA_PACKAGE_INFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.QPP_PCRF_PROFILE.value(), null);
        knLogger.info("KnGenInfoUtil", "Maps initialized ");
    }

    public static synchronized KnGenInfoUtil getInstance() {
        if (!isInitialized) {
            instance = new KnGenInfoUtil();
            isInitialized = true;
        }
        return instance;
    }

    /**
     * method to retrieve the Local XDM PttServer Id.
     *
     * @return String
     * @throws KnBOException exception
     */
    public String retrieveLocalXDMPttServerId() throws KnBOException {
        String methodName = "retrieveLocalXDMPttServerId()";
        String xdmPttServerId = null;
        knLogger.debug(methodName, "ENTRY: retrieve Local XDM PTT ID ");
        Properties dbMgrProps = null;
        String DG_ = "DG_";
        try {

            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            xdmPttServerId = (String) cacheManager.get(KnCacheKeys.XDMS_PTTSERVER_ID);

            if (xdmPttServerId == null || "".equals(xdmPttServerId)) {
                dbMgrProps = (Properties) cacheManager.get(KnCacheKeys.DBMGR_PROPS);
                if (dbMgrProps == null || dbMgrProps.isEmpty()) {
                    dbMgrProps = new Properties();
                    String dbMgrFilePath = System.getProperty(ACTIVE_RELEASE_DIR);
                    String dbMgrFileWithPath = dbMgrFilePath + "/" + DB_MGR_FILE_NAME;
                    knLogger.debug(methodName, "DBMgr props file with path " + dbMgrFileWithPath);
                    try {
                        dbMgrProps.load(new FileInputStream(dbMgrFileWithPath));
                    } catch (IOException ioe) {
                        knLogger.error(methodName, "Unable to load DB MGR file properties.");
                        knLogger.error(methodName, ioe);
                        throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unable to load the DB Mgr file Props", ioe);
                    }

                    String localDBDSN = (String) dbMgrProps.get(DB_MGR_DBDSN);
                    if (localDBDSN != null && !"".equals(localDBDSN.trim())) {
                        String localPttId = localDBDSN.substring(3);
                        if (localPttId != null && !localPttId.trim().equals("")) {
                            dbMgrProps.put(DB_MGR_LOCAL_PTTID, localPttId);
                        }
                    }

                    cacheManager.put(KnCacheKeys.DBMGR_PROPS, dbMgrProps);

                }
                xdmPttServerId = dbMgrProps.getProperty(DB_MGR_LOCAL_PTTID);
                cacheManager.put(KnCacheKeys.XDMS_PTTSERVER_ID, xdmPttServerId);
            }
            return xdmPttServerId;

        } catch (Exception e) {
            knLogger.error(methodName, "Failed to get local XDM PttServer Id");
            knLogger.error(methodName, e);
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed Loading DB Mgr Props File", e);
        } finally {
            knLogger.debug(methodName, "EXIT: retrieve local XDM PttServer Id : " + xdmPttServerId);
        }
    }

    public int retrieveIdForTable(String tableName, String pttServerId, String columnName, boolean retry) {
        return retrieveIdForTable(tableName, pttServerId, columnName, retry, null);
    }

    public int retrieveIdForTable(String tableName, String pttServerId, String columnName, boolean retry, KnDBConst.DataStores dataStoreId) {
        String methodName = "retrieveIdForTable(String)";
        KnIdGeneratorImpl idGenerator = KnIdGeneratorImpl.getInstance();
        tableName = tableName.toUpperCase();
        //caching should not be happen at the utility level. so moved to KnIdGeneratorImpl
        //     KnTableInfoBean tableInfoBean = registeredTableList.get(tableName);
        //   if (tableInfoBean == null) {
        knLogger.debug(methodName, "registering table " + tableName + " to ID Gen");
        ArrayList<KnTableInfoBean> registerTable = new ArrayList<KnTableInfoBean>();
        KnTableInfoBean tableInfoBean = new KnTableInfoBean();
        tableInfoBean.setTableName(tableName);
        tableInfoBean.setColumnName(columnName);
        tableInfoBean.setPttServerId(pttServerId);
        tableInfoBean.setDataStoreId(dataStoreId);
        registerTable.add(tableInfoBean);
        idGenerator.registerTables(registerTable);
        //    registeredTableList.put(tableName, tableInfoBean);
//    }

        if (retry) {
            ArrayList<KnTableInfoBean> registerTableList = new ArrayList<KnTableInfoBean>();
//            KnTableInfoBean tableInfoBean = new KnTableInfoBean();
//            tableInfoBean.setTableName(tableName);
//            tableInfoBean.setColumnName(columnName);
//            tableInfoBean.setPttServerId(pttServerId);
            registerTableList.add(tableInfoBean);
//            idGenerator.registerTables(registerTable);
            idGenerator.reInitialiseIds(registerTableList);
        }
        return idGenerator.getNextId(tableName);
    }

    public List<Integer> retrieveIdForTable(String tableName, String pttServerId, String columnName, boolean retry, int size) {
        return retrieveIdForTable(tableName, pttServerId, columnName, retry, null, size);
    }

    public List<Integer> retrieveIdForTable(String tableName, String pttServerId, String columnName, boolean retry, KnDBConst.DataStores dataStoreId, int size) {
        String methodName = "retrieveIdForTable(String,String,String,boolean,size)";
        KnIdGeneratorImpl idGenerator = KnIdGeneratorImpl.getInstance();
        tableName = tableName.toUpperCase();
        knLogger.debug(methodName, "registering table " + tableName + " to ID Gen");
        ArrayList<KnTableInfoBean> registerTable = new ArrayList<KnTableInfoBean>();
        KnTableInfoBean tableInfoBean = new KnTableInfoBean();
        tableInfoBean.setTableName(tableName);
        tableInfoBean.setColumnName(columnName);
        tableInfoBean.setPttServerId(pttServerId);
        tableInfoBean.setDataStoreId(dataStoreId);
        registerTable.add(tableInfoBean);
        idGenerator.registerTables(registerTable);
        if (retry) {
            ArrayList<KnTableInfoBean> registerTableList = new ArrayList<KnTableInfoBean>();
            registerTableList.add(tableInfoBean);
            idGenerator.reInitialiseIds(registerTableList);
        }
        return idGenerator.getNextId(tableName, false, size);
    }

    public Map<Integer, KnPOCBlackListDevicesDTO> retrievePOCBlackListDevices(String xdmPttServerId, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrievePOCBlackListDevices(String,KnPersisterTxn)";
        Map<Integer, KnPOCBlackListDevicesDTO> blacklistDevices = null;
        //  KnPOCSuppDevicesDTO pocSuppDevicesDTO = null;
        knLogger.debug(methodName, "ENTRY: Retrieve POC Blacklist Devices");
        try {
            knLogger.debug(methodName, "retrieving the POC Blacklist Devices");
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            knLogger.debug(methodName, "Reading from Cache");
            //  pocSuppDevicesDTO = (KnPOCSuppDevicesDTO) cacheManager.get(KnCacheKeys.POC_SUPPORTED_DEVICES);
            blacklistDevices = (HashMap<Integer, KnPOCBlackListDevicesDTO>) cacheManager.get(KnCacheKeys.POC_BLACKLIST_DEVICES);
            if (blacklistDevices == null) {
                blacklistDevices = new HashMap<Integer, KnPOCBlackListDevicesDTO>();
                knLogger.debug(methodName, " Not found in cache, hence reading from DB.. ");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                blacklistDevices = xdmServerDAO.retrievePOCBlackListDevices(persisterTxn);
                cacheManager.put(KnCacheKeys.POC_BLACKLIST_DEVICES, blacklistDevices);
            }

            knLogger.debug(methodName, "Retrieved POC Blacklisted Devices " + blacklistDevices);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.POC_BLACKLISTED_DEVICES_NOT_FOUND,
                            "POC Blacklisted Devices info not found", e);
                }
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected Exception occurred");
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get POC Blacklisted Devices", e);
        } finally {
            knLogger.info(methodName, "EXIT: POC Blacklisted Devices " + blacklistDevices);
        }

        return blacklistDevices;
    }

    public KnXDMSServiceConfigDTO retrieveXDMSServiceConfig(String xdmPttServerId, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrieveServiceConfig(KnPersisterTxn)";
        KnXDMSServiceConfigDTO xdmsServiceConfigDTO = null;
        knLogger.debug(methodName, "ENTRY: Retrieve Service Config");
        try {
            knLogger.debug(methodName, "retrieving the XDMS Service Configuration");
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            knLogger.debug(methodName, "Reading from Cache");
            xdmsServiceConfigDTO = (KnXDMSServiceConfigDTO) cacheManager.get(KnCacheKeys.XDMS_SVC_CONFIG);
            if (xdmsServiceConfigDTO == null) {
                knLogger.debug(methodName, "Not found in cache, hence reading from DB ");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                xdmsServiceConfigDTO = xdmServerDAO.retrieveXDMSServiceConfig(persisterTxn);
                //xcap root uri format :: <retrieved/configured xcap root + xcap root context (xcap war file name)>
                xdmsServiceConfigDTO.setXcapRootUri(xdmsServiceConfigDTO.getXcapRootUri() + com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT);
                xdmsServiceConfigDTO.setXcapRootUri_Wifi(xdmsServiceConfigDTO.getXcapRootUri_Wifi() + com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT);
                cacheManager.put(KnCacheKeys.XDMS_SVC_CONFIG, xdmsServiceConfigDTO);
            }

            knLogger.debug(methodName, "Retrieved XDMS Service Config " + xdmsServiceConfigDTO);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SERVICE_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found", e);
                }
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected Exception occurred");
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get XDMS service config info", e);
        } finally {
            knLogger.debug(methodName, "EXIT: Retrive Service Config " + xdmsServiceConfigDTO);
        }

        return xdmsServiceConfigDTO;
    }

    public Map<String, String> retrieveXDMSSystemConfigValues() throws KnBOException {
        String methodName = "retrieveXDMMessageTTLValues";
        long messageTTLValue = -1;
        knLogger.debug(methodName, "ENTRY: retrieve XDM Message TTL Value");
        Map<String, String> xdmSubsystemConfig = new HashMap<String, String>();
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            xdmSubsystemConfig = (Map<String, String>) cacheManager.get(KnCacheKeys.XDMS_SUBS_SYS_CONFIG);

            if (xdmSubsystemConfig == null || xdmSubsystemConfig.isEmpty()) {
                knLogger.debug(methodName, "Not found in cache, hence reading from DB");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                xdmSubsystemConfig = xdmServerDAO.retrieveXDMSSubsSysConfig(null);
                cacheManager.put(KnCacheKeys.XDMS_SUBS_SYS_CONFIG, xdmSubsystemConfig);
            }

        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found", e);
                }
            }
        }

        return xdmSubsystemConfig;
    }

    public Map<String, KnPOCSuppDevicesDTO> retrievePOCSuppDevices(String xdmPttServerId, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrievePOCSuppDevices(KnPersisterTxn)";
        Map<String, KnPOCSuppDevicesDTO> suppDevices = null;
        //  KnPOCSuppDevicesDTO pocSuppDevicesDTO = null;
        knLogger.debug(methodName, "ENTRY: Retrieve POC Supported Devices");
        try {
            knLogger.debug(methodName, "retrieving the POC Supported Devices");
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            knLogger.debug(methodName, "Reading from Cache");
            //  pocSuppDevicesDTO = (KnPOCSuppDevicesDTO) cacheManager.get(KnCacheKeys.POC_SUPPORTED_DEVICES);
            suppDevices = (HashMap<String, KnPOCSuppDevicesDTO>) cacheManager.get(KnCacheKeys.POC_SUPPORTED_DEVICES);
            if (suppDevices == null) {
                suppDevices = new HashMap<String, KnPOCSuppDevicesDTO>();
                knLogger.debug(methodName, " Not found in cache, hence reading from DB.. ");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                suppDevices = xdmServerDAO.retrievePOCSuppDevices(persisterTxn);
                cacheManager.put(KnCacheKeys.POC_SUPPORTED_DEVICES, suppDevices);
            }

            knLogger.debug(methodName, "Retrieved POC Supported Devices " + suppDevices);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.POC_SUPPORTED_DEVICES_NOT_FOUND,
                            "POC Supported Devices info not found", e);
                }
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected Exception occurred");
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get POC Supported Devices", e);
        } finally {
            knLogger.info(methodName, "EXIT: POC Supported Devices " + suppDevices);
        }

        return suppDevices;
    }


    public String generateDirDocUri(String mdn) {
        String methodName = "generateDirDocUri(String)";
        String documentURI;

        String appUId = DIR_DOC_AUID;
        String documentType = DIR_DOC_TYPE;
        String documentName = DIR_DOC_NAME;
        String xui = generateXUI(mdn);

        StringBuilder strBuffer = new StringBuilder(100);
        strBuffer.append(appUId).append("/");
        strBuffer.append(documentType).append("/");
        strBuffer.append(xui).append("/");
        strBuffer.append(documentName);

        documentURI = strBuffer.toString();

        knLogger.debug(methodName, "sel uri generated - " + KnGDPRTemplate.mdnUriTemplate(documentURI));
        return documentURI;
    }


    /**
     * method to generate the XUI
     *
     * @param mdn String
     * @return String XUI
     */
    public String generateXUI(String mdn) {
        String methodName = "generateXUI";
        knLogger.debug(methodName, "generating XUI for mdn - " + KnGDPRTemplate.mdn(mdn));
        String xui = TEL_URI_TEMPLATE + mdn;
        knLogger.debug(methodName, "XUI for the MDN - " + KnGDPRTemplate.mdnPart(xui));
        return xui;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Map<String, List<String>> getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(Map<String, List<String>> tableInfo) {
        this.tableInfo = tableInfo;
    }

    public void update(Observable observable, Object o) {
        String methodName = "update()";
        knLogger.info(methodName, "cleaning cache");
        List<String> modifiedTables = (List<String>) o;
        KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
        try {
            ICacheManager cacheManager = configManager.getCacheManager();

            for (String str : modifiedTables) {
                if (tableInfo.containsKey(str)) {
                    cacheManager.put(tableMap.get(str), null);
                    knLogger.debug(methodName, "cleaning cache for ", str);
                }
                if (str.equals(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.MICROSVCS_COMMONCONFIG.value())) {
                    cacheManager.put(tableMap.get(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SERVICE_FQDN_INFO.value()), null);
                }
            }
        } catch (KnConfigurationException knConfigurationException) {
            knLogger.error(methodName, "Exception", "caught in update");
        }
    }


    /**
     * @param apnName
     * @param persisterTxn
     * @return ;
     * @throws KnBOException
     */
    public Integer getAPNId(String apnName, KnPersisterTxn persisterTxn) throws KnBOException {
        return getAPNId(apnName, false, persisterTxn);
    }

    public Integer getAPNId(String apnName, boolean readOnly, KnPersisterTxn persisterTxn) throws KnBOException {
        final String methodName = "getAPNId(apnName, KnPersisterTxn)";
        knLogger.debug(methodName, apnName, persisterTxn);
        Integer apnId = null;
        Map<String, Integer> apnInfo = null;
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            apnInfo = ((Map<String, Integer>) cacheManager.get(KnCacheKeys.APN_INFO));
            if (apnInfo == null) {
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(retrieveLocalXDMPttServerId());
                apnInfo = xdmServerDAO.retrieveAPNInfo(readOnly, persisterTxn);
                cacheManager.put(KnCacheKeys.APN_INFO, apnInfo);
            }
            apnId = apnInfo.get(apnName);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        " APN ID  not found", e);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  apn profile info  ", e);
        }
        knLogger.exit(methodName, apnId);
        return apnId;
    }

    public String getDefaultAPNName(KnPersisterTxn persisterTxn) throws KnBOException {
        return getDefaultAPNName(false, persisterTxn);
    }
    public String getDefaultAPNName(boolean readOnly, KnPersisterTxn persisterTxn) throws KnBOException {
        final String methodName = "getDefaultAPNName(boolean, KnPersisterTxn)";
        knLogger.debug(methodName, persisterTxn);
        String defaultApnName = null;

        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            defaultApnName = (String) cacheManager.get(KnCacheKeys.DEFAULT_APN_NAME);
            knLogger.debug(methodName, "retrieved default apn name  from cache - ", defaultApnName);
            if (defaultApnName == null) {
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(retrieveLocalXDMPttServerId());
                defaultApnName = xdmServerDAO.selectDefaultAPNName(readOnly, persisterTxn);
                cacheManager.put(KnCacheKeys.DEFAULT_APN_NAME, defaultApnName);
            }
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        " default apn name  not found", e);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred", e);
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  default apn name   ", e);
        }
        knLogger.debug(methodName, defaultApnName);
        return defaultApnName;
    }


    public String getXCAPRootURI(Integer apnId, KnPersisterTxn persisterTxn, boolean isMcxNotify) throws KnBOException {
        final String methodName = "getXCAPRootURI(Integer, KnPersisterTxn)";
        knLogger.debug(methodName, apnId, persisterTxn);
        String xcapRootURI = null;
        Map<Integer, KnAPNConfigDTO> knAPNConfDTOMap = new HashMap<Integer, KnAPNConfigDTO>();
        KnAPNConfigDTO apnConfigDTO = new KnAPNConfigDTO();

        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            knAPNConfDTOMap = (Map<Integer, KnAPNConfigDTO>) cacheManager.get(KnCacheKeys.APN_CONFIG_INFO);

            if (knAPNConfDTOMap == null || knAPNConfDTOMap.isEmpty()) {
                knLogger.debug(methodName, "Not found in cache hence reading from db");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                knAPNConfDTOMap = xdmServerDAO.retrieveAPNInfoConfig(persisterTxn);
                cacheManager.put(KnCacheKeys.APN_CONFIG_INFO, knAPNConfDTOMap);
            }
            apnConfigDTO = (KnAPNConfigDTO) knAPNConfDTOMap.get(apnId);
            knLogger.debug(methodName, "APNconfigDTO  value -", apnConfigDTO);
            if (apnConfigDTO == null) {
                throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                        "XDMS Service Config info not found");
            }
            if (!isMcxNotify) {
                xcapRootURI = apnConfigDTO.getApnXCAPUri();
            } else {
                xcapRootURI = apnConfigDTO.getMcsXCAPUri();
            }
            knLogger.debug(methodName, "retrieved xcapRootUriMap  - ", KnGDPRTemplate.mdnUriTemplate(xcapRootURI));
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        " xcap root uri  found", e);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred", e);
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  apn profile info  ", e);
        }
        knLogger.debug(methodName, KnGDPRTemplate.mdnUriTemplate(xcapRootURI));
        return xcapRootURI;
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnBOException
     */
    public String getXCAPRootURI(String mdn, KnPersisterTxn persisterTxn) throws KnBOException {
        final String methodName = "getXCAPRootURI(mdn, KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdn(mdn), persisterTxn);
        String xcapRootURI = null;
        Integer apnid;
        try {
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(retrieveLocalXDMPttServerId());
            apnid = xdmServerDAO.getSubsApnId(mdn, persisterTxn);
            int pv = xdmServerDAO.getSubscriberPV(mdn, persisterTxn);
            xcapRootURI = getXCAPRootURI(apnid, persisterTxn, false);
            if(pv >= PROTOCOL_VERSION_13_X){
                xcapRootURI = xcapRootURI + com.kodiak.common.resources.KnConstants.OIDC_XCAP_ROOT_CONTEXT;
            }else{
                xcapRootURI = xcapRootURI + com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT;

            }
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        " xcap root uri  found", e);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  apn profile info  ", e);
        }
        knLogger.debug(methodName, KnGDPRTemplate.mdn(xcapRootURI));
        return xcapRootURI;

    }


    /**
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnBOException
     */
    public Map<String, String> getXCAPRootURI(List<String> mdns, KnPersisterTxn persisterTxn, boolean isMcxNotify) throws KnBOException {
        return getXCAPRootURI(mdns, false, persisterTxn, isMcxNotify);
    }

    public Map<String, String> getXCAPRootURI(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn, boolean isMcxNotify) throws KnBOException {
        final String methodName = "getXCAPRootURI(List<String>, boolean, KnPersisterTxn, boolean)";
        Map<String, String> xcapRootUris = new HashMap<>();
        knLogger.entry(methodName, "ENTRY : mdns size ", mdns.size());
        if(mdns == null || mdns.isEmpty())
        {
            knLogger.exit(methodName, "EXIT : xcapRootUris size " , xcapRootUris);
            return xcapRootUris;
        }
        var mdnListArray = new ArrayList<>(mdns);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var subXCAPRootURI = getSubXCAPRootURI(subsList, readOnly, persisterTxn, isMcxNotify);
            if (subXCAPRootURI != null && !subXCAPRootURI.isEmpty())
            {
                xcapRootUris.putAll(subXCAPRootURI);
            }
        }
        knLogger.exit(methodName, "EXIT : xcapRootUris size " , xcapRootUris == null ? 0 : xcapRootUris.size());
        return xcapRootUris;

    }

    private Map<String, String> getSubXCAPRootURI(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn, boolean isMcxNotify) throws KnBOException {
        final String methodName = "getSubXCAPRootURI(List<String>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdnList(mdns));
        Map<String, String> xcapRootUris = null;
        try {
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(retrieveLocalXDMPttServerId());
            if (!(mdns == null || mdns.isEmpty())) {
                Map<String, Integer> subsApnInfo = xdmServerDAO.getSubsApnId(mdns, readOnly, persisterTxn);
                Map<String, Integer> pvs = xdmServerDAO.getSubscribersPV(mdns, readOnly, persisterTxn);
                xcapRootUris = new HashMap<String, String>();
                for (Map.Entry<String, Integer> mapSet : subsApnInfo.entrySet()) {
                    String xcapRootURI = getXCAPRootURI(mapSet.getValue(), persisterTxn, isMcxNotify);
                    if(pvs.get(mapSet.getKey()) >= PROTOCOL_VERSION_13_X && !isMcxNotify){
                        xcapRootURI = xcapRootURI + com.kodiak.common.resources.KnConstants.OIDC_XCAP_ROOT_CONTEXT;
                    } else if(pvs.get(mapSet.getKey()) > PROTOCOL_VERSION_18 && isMcxNotify){
                        xcapRootURI = xcapRootURI + com.kodiak.common.resources.KnConstants.MCSXCAP_XCAP_ROOT_CONTEXT;
                    } else {
                        xcapRootURI = xcapRootURI + com.kodiak.common.resources.KnConstants.XCAP_ROOT_CONTEXT;
                    }
                    xcapRootUris.put(mapSet.getKey(), xcapRootURI);
                }
            }

        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        " xcap root uri  found", e);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  apn profile info  ", e);
        }
        knLogger.debug(methodName,KnGDPRTemplate.mdnMap(xcapRootUris));
        return xcapRootUris;
    }


    public KnActivationCodeConfigDTO getActivationCodeConfig(int clientType, int intf, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "getActivationCodeConfig(int,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:Retrieve activation config for client type - ", clientType, "interface - ", intf);
        KnActivationCodeConfigDTO activationCodeConfigDTO = null;
        Map<String, KnActivationCodeConfigDTO> activationCodeConfigDTOMap = null;
        try {
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(retrieveLocalXDMPttServerId());
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            knLogger.debug(methodName, "Reading from Cache");
            activationCodeConfigDTOMap = (Map<String, KnActivationCodeConfigDTO>) cacheManager.get(KnCacheKeys.ACTIVATIONCODECONFIG);
            String key = clientType + DELIM + intf;
            if (activationCodeConfigDTOMap == null || activationCodeConfigDTOMap.get(key) == null) {
                activationCodeConfigDTOMap = xdmServerDAO.retrieveActivationCodeConfig(persisterTxn);
                knLogger.debug(methodName, " Not found in cache, hence reading from DB.. ");
                cacheManager.put(KnCacheKeys.ACTIVATIONCODECONFIG, activationCodeConfigDTOMap);
            }
            if (activationCodeConfigDTOMap.containsKey(key)) {
                knLogger.debug(methodName, "client type present in the activation code config map ");
                activationCodeConfigDTO = activationCodeConfigDTOMap.get(key);
            } else {
                knLogger.error(methodName, "client type not configured ");
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to retrieve activation code config  ", e);
        }
        knLogger.debug(methodName, "EXIT:Retrieved activation config for client type - ", clientType, "and intf - ", intf, "Config - ", activationCodeConfigDTO);
        return activationCodeConfigDTO;
    }


    public Map<String, String> retrieveRTXConfigValues(KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrieveRTXConfigValues";
        knLogger.debug(methodName, "ENTRY: retrieve RTX variable Value");
        Map<String, String> rtxConfigValueMap = new HashMap<String, String>();
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            rtxConfigValueMap = (Map<String, String>) cacheManager.get(KnCacheKeys.RTX_VALUE_CONFIG);

            if (rtxConfigValueMap == null || rtxConfigValueMap.isEmpty()) {
                List<String> keyList = new ArrayList<String>();
                keyList.add(XDMS_ALLOW_SUMDN_AS_CONTACT);
                keyList.add(ENABLE_CORP_AUTO_PAIRING);
                keyList.add(AUTO_PAIR_SUBLIST_NAME);
                keyList.add(AUTO_PAIR_GROUP_NAME);
                keyList.add(CORP_AUTO_PAIRING_SIZE);
                keyList.add(ROAM_NA_CLIENT_TYPES);
                keyList.add(XDM_NXT_GEN_CAT_ENABLED);
                keyList.add(IP_VER_CELLULAR);
                keyList.add(IP_PREF_ON_CELL_INTF);
                keyList.add(IP_PREF_ON_WIFI_INTF);
                keyList.add(IP_VER_MULTICAST);
                keyList.add(IP_PREF_MULTICAST);
                keyList.add(DEFAULT_WEBRTC_VOCODER_PROFILEID);
                keyList.add(XDMS_LMR_SUB_DEFAULT_NAME);
                keyList.add(MAX_LOC_WATCHERS_PER_GRP);
                keyList.add(SERVER_PREFERRED_VOCODER_PROFILEID);
                keyList.add(CONV_CLIENT_ENABLED);
                keyList.add(IDM_INTF_INT_FQDN);
                keyList.add(SPECIFIC_CODEC_SUPPPORT_OLDER_BREW_CLIENTS);
                keyList.add(OLDER_CLIENTS_CODEC_SUPPORTED_BREW_UA);
                keyList.add(BASE_PKGCODE);
                keyList.add(NUM_OF_LG_SUPPORTED);
                keyList.add(FIRSTFLAG_CONFIG);
                keyList.add(MCPTT_CELLULAR_SIGNALING_TRANSPORT);
                keyList.add(POC_VOCODER_PRIORITY_LIST);
                keyList.add(BULK_PROV_BATCH_SIZE);
                keyList.add(ENABLE_OIDC);
                keyList.add(IDS_BLOCKED_CAT_CORP_LIST);
                keyList.add(ALLOW_MALFORMED_UA_ACTIVATION);
                keyList.add(ALLOWED_SIPMSG_SIZE_ON_TCP);
                keyList.add(ALLOWED_SIPMSG_SIZE_ON_UDP);
                keyList.add(ALLOWED_SIPMSG_HEADERS_SIZE);
                keyList.add(BACKWARD_COMPATIBILTY_REQ);
                knLogger.debug(methodName, "Not found in cache, hence reading from DB");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                rtxConfigValueMap = xdmServerDAO.retrieveRTXConfig(keyList, persisterTxn);
                cacheManager.put(KnCacheKeys.RTX_VALUE_CONFIG, rtxConfigValueMap);
            }

        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found", e);
                }
            }
        }

        return rtxConfigValueMap;
    }

    public boolean isUnUpgradedPOCSERVER(String pttServerId,KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "isUnUpgradedPOCSERVER(String)";
        knLogger.info(methodName, "ENTRY :");
        Map<String, String> envValue = retrieveRTXConfigValues(persisterTxn);
        if (envValue != null && envValue.get(BACKWARD_COMPATIBILTY_REQ) != null) {
            if (Integer.parseInt(envValue.get(BACKWARD_COMPATIBILTY_REQ)) == 0) {
                return false;
            }
        }
        boolean status = true;
        Integer count;
        try {
            KnCache cache = KnCache.getInstance();
            ICache cacheManager = cache.getCacheManager();
            count = (Integer) cacheManager.get(com.kodiak.common.cache.KnCacheKeys.UNUPGRADED_POCSERVER_LIST);
            if (count == null) {
                knLogger.info(methodName, " reading from GG ");
                count = KnGGCache.unUpgradedPoCServerListDAO.isUnUpgradedPOCSERVER(pttServerId);
                //cacheManager.add(KnCacheKeys.UNUPGRADED_POCSERVER_LIST, count, KnConstants.CACHE_EXPIRY_TIME);
                knLogger.info(methodName, " fetched values from GG : " + count);
            }
            if(count <= 0) status = false;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occurred - ", e);
        }
        return status;
    }

    public KnClientTypeConfigDTO getClientTypeConfig(int clientType, KnPersisterTxn persisterTxn) throws KnBOException {

        String methodName = "getClientTypeConfig(clientType)";
        Map<Integer, KnClientTypeConfigDTO> clientTypeConfigDTOMap = new HashMap<>();
        KnClientTypeConfigDTO configDTO = new KnClientTypeConfigDTO();

        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            clientTypeConfigDTOMap = (Map<Integer, KnClientTypeConfigDTO>) cacheManager.get(KnCacheKeys.CLIENT_TYPE_CONFIG);

            if (clientTypeConfigDTOMap == null || clientTypeConfigDTOMap.isEmpty()) {
                knLogger.debug(methodName, "Not found in cache hence reading from db");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                clientTypeConfigDTOMap = xdmServerDAO.retrieveClientTypeConfig(persisterTxn);
                cacheManager.put(KnCacheKeys.CLIENT_TYPE_CONFIG, clientTypeConfigDTOMap);
            }
            configDTO = (KnClientTypeConfigDTO) clientTypeConfigDTOMap.get(clientType);
            knLogger.debug(methodName, "client type config value -", configDTO);
            if (configDTO == null) {
                throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                        "XDMS Service Config info not found");
            }

        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found", e);
                }
            }
        }
        return configDTO;
    }


    public KnAPNConfigDTO retrieveAPNInfoConfig( KnPersisterTxn persisterTxn,int apnId) throws KnBOException {

        String methodName = "retrieveAPNDynamicQoS(clientType)";
        Map<Integer, KnAPNConfigDTO> knAPNConfDTOMap = new HashMap<>();
        KnAPNConfigDTO apnConfigDTO = new KnAPNConfigDTO();

        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            knAPNConfDTOMap = (Map<Integer, KnAPNConfigDTO>) cacheManager.get(KnCacheKeys.APN_CONFIG_INFO);

            if (knAPNConfDTOMap == null || knAPNConfDTOMap.isEmpty()) {
                knLogger.debug(methodName, "Not found in cache hence reading from db");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                knAPNConfDTOMap = xdmServerDAO.retrieveAPNInfoConfig(persisterTxn);
                cacheManager.put(KnCacheKeys.APN_CONFIG_INFO, knAPNConfDTOMap);
            }
            apnConfigDTO = (KnAPNConfigDTO) knAPNConfDTOMap.get(apnId);
            knLogger.debug(methodName, "APNconfigDTO  value -", apnConfigDTO);
            if (apnConfigDTO == null) {
                throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                        "XDMS Service Config info not found");
            }

        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found", e);
                }
            }
        }
        return apnConfigDTO;
    }

    public Map<Integer, KnSuppVocoderProfileDTO> retrieveSuppVocoderProfile(KnPersisterTxn persisterTxn)throws KnBOException{

        String methodName = "retrieveSuppVocoderProfile(KnPersisterTxn)";
        Map<Integer, KnSuppVocoderProfileDTO> suppVocoderProfileDTOMap = new TreeMap<>();
      //  KnSuppVocoderProfileDTO suppVocoderProfileDTO = new KnSuppVocoderProfileDTO();

        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            suppVocoderProfileDTOMap = (Map<Integer, KnSuppVocoderProfileDTO>) cacheManager.get(KnCacheKeys.SUPP_VOCODER_PROFILE);

            if (suppVocoderProfileDTOMap == null || suppVocoderProfileDTOMap.isEmpty()) {
                knLogger.debug(methodName, "Not found in cache hence reading from db");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                suppVocoderProfileDTOMap = xdmServerDAO.retrieveSuppVocoderProfile(persisterTxn);
                cacheManager.put(KnCacheKeys.SUPP_VOCODER_PROFILE, suppVocoderProfileDTOMap);
            }
            knLogger.debug(methodName, "suppVocoderProfileDTOMap  value -", suppVocoderProfileDTOMap);
            if (suppVocoderProfileDTOMap == null) {
                throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                        "XDMS Service Config info not found");
            }

        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "SUPPORTEDCLIENTVOCODERS info not found", e);
                }
            }
        }
        return suppVocoderProfileDTOMap;
    }

    public Map<String, String> retrieveMSSvcsCommonConfig(int clusterId,KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrieveMSSvcsCommonConfig";
        knLogger.debug(methodName, "ENTRY: Retrieve Microservice  Config");
        List<KnMicroSvcsCommonConfig> msSvcConfigList = new ArrayList<>();
        Map<String, String> msSvcConfigDocMap = new HashMap<>();
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            msSvcConfigList = (List<KnMicroSvcsCommonConfig>) cacheManager.get(KnCacheKeys.MS_COMMON_CONFIG);

            if (msSvcConfigList == null || msSvcConfigList.isEmpty()) {
                knLogger.info(methodName, "Not found in cache, hence reading from DB");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                msSvcConfigList = xdmServerDAO.retrieveMSSvcsCommonConfig(persisterTxn);
                cacheManager.put(KnCacheKeys.MS_COMMON_CONFIG, msSvcConfigList);
            }
            //knLogger.debug("msSvcConfigList :",msSvcConfigList);
            for(KnMicroSvcsCommonConfig commonConfigDto : msSvcConfigList){
                if(clusterId == commonConfigDto.getClusterId()) {
                    msSvcConfigDocMap.put(commonConfigDto.getParamName(), commonConfigDto.getParamValue());
                }
            }
        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found", e);
                }
            }
        }
        knLogger.debug("Exit - : ",methodName);
        return msSvcConfigDocMap;
    }

    public boolean retrieveCriLocFlag(int clusterId, KnPersisterTxn persisterTxn) throws KnBOException {
    	boolean flag=false;
    	
    	try {
			Map<String, String> commanFlags=retrieveMSSvcsCommonConfig(clusterId,persisterTxn);
			if(commanFlags.get(EPC_MCX_CRILOC_FLAG)!=null && Integer.parseInt(commanFlags.get(KnConstants.EPC_MCX_CRILOC_FLAG))==1) {
				flag=true;
			}

		} catch (KnBOException e) {
			knLogger.error("Failed to retrieve EPC_MCX_CRILOC_FLAG",e);		
			}
    	return flag;
    }
    public  Map<Integer, List<KnMicroSvcsCommonConfig>> retrieveMSSvcsCommonConfigMap(KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrieveMSSvcsCommonConfig---For CouchBase Connection";
        knLogger.debug(methodName, "ENTRY: Retrieve Microservice  Config ");
        List<KnMicroSvcsCommonConfig> msSvcConfigList = new ArrayList<>();
        Map<Integer, List<KnMicroSvcsCommonConfig>> microSvcsCommonConfigMap=new HashMap<>();
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            msSvcConfigList = (List<KnMicroSvcsCommonConfig>) cacheManager.get(KnCacheKeys.MS_COMMON_CONFIG);

            if (msSvcConfigList == null || msSvcConfigList.isEmpty()) {
                knLogger.debug(methodName, "Not found in cache, hence reading from DB");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                msSvcConfigList = xdmServerDAO.retrieveMSSvcsCommonConfig(persisterTxn);
                cacheManager.put(KnCacheKeys.MS_COMMON_CONFIG, msSvcConfigList);
            }
            for(KnMicroSvcsCommonConfig dto:msSvcConfigList) {
                if (microSvcsCommonConfigMap.get(dto.getClusterId()) != null)
                    microSvcsCommonConfigMap.get(dto.getClusterId()).add(dto);
                else {
                    ArrayList<KnMicroSvcsCommonConfig> list=new ArrayList<KnMicroSvcsCommonConfig>();
                    list.add(dto);
                    microSvcsCommonConfigMap.put(dto.getClusterId(),list);
                }
            }
        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found", e);
                }
            }
        }
       // knLogger.debug("Exit - : ",methodName ,microSvcsCommonConfigMap);
        return microSvcsCommonConfigMap;
    }


    public Map<String, String> retrieveMSSvcsCommonConfig(int clusterId) throws KnBOException {
    	final String methodName = "retrieveMSSvcsCommonConfig";
    	Map<String, String> msSvcConfig = new HashMap<>();
    	//KnPersisterTxn persisterTxn  = null;
    	 try {
    		/*persisterTxn =  KnPersisterTxn.getPersisterTxn();
			persisterTxn.open();*/
			msSvcConfig = this.retrieveMSSvcsCommonConfig(clusterId, null);
			//persisterTxn.save();

		} /*catch (KnPersistenceException e) {
		    knLogger.error(methodName, "Failed to get the Transaction", e);
		    rollback(persisterTxn);

		}*/ catch (Exception e) {
			 knLogger.error(methodName, "unexpected exception", e);
			  //rollback(persisterTxn);
		}
    	 return msSvcConfig;
    }

    private void rollback(KnPersisterTxn txn) {
        try {
            knLogger.error("rollback()", "Rolling back transaction");
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)",
                    "Failed to rollback the transaction.");
        }
    }

    public Map<String, String> retrieveMSSvcsCommonConfigWithParmScope(int clusterId, int paramScope, boolean readOnly, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrieveMSSvcsCommonConfigWithParmScope";
        knLogger.debug(methodName, "ENTRY: Retrieve Microservice  Config");
        List<KnMicroSvcsCommonConfig> msSvcConfigList = new ArrayList<>();
        Map<String, String> msSvcConfigDocMap = new HashMap<>();
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            msSvcConfigList = (List<KnMicroSvcsCommonConfig>) cacheManager.get(KnCacheKeys.MS_COMMON_CONFIG);

            if (msSvcConfigList == null || msSvcConfigList.isEmpty()) {
                knLogger.debug(methodName, "Not found in cache, hence reading from DB");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                msSvcConfigList = xdmServerDAO.retrieveMSSvcsCommonConfig(persisterTxn);
                cacheManager.put(KnCacheKeys.MS_COMMON_CONFIG, msSvcConfigList);
            }
            //knLogger.debug("msSvcConfigList :",msSvcConfigList);
            for(KnMicroSvcsCommonConfig commonConfigDto : msSvcConfigList){
                if(clusterId == commonConfigDto.getClusterId() && paramScope == commonConfigDto.getParamScope()) {
                    msSvcConfigDocMap.put(commonConfigDto.getParamName(), commonConfigDto.getParamValue());
                }
            }

        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found", e);
                }
            }
        }
        knLogger.debug("Exit - : ",methodName ,msSvcConfigDocMap.keySet());
        return msSvcConfigDocMap;
    }

    public KnMicroSvcsClusterInfo retrieveMSSvcsClusterConfig(int clusterId, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrieveMSSvcsClusterConfig";
        knLogger.debug(methodName, "ENTRY: Retrieving Microservices Cluster Config info");
        KnMicroSvcsClusterInfo msClusterConfigMap = new KnMicroSvcsClusterInfo();
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            msClusterConfigMap = (KnMicroSvcsClusterInfo) cacheManager.get(KnCacheKeys.MS_CLUSTER_CONFIG);

            if (msClusterConfigMap == null) {
                knLogger.debug(methodName, "Not found in cache, hence reading from DB");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                msClusterConfigMap = xdmServerDAO.retrieveMSSvcsClusterConfig(persisterTxn);
                cacheManager.put(KnCacheKeys.MS_CLUSTER_CONFIG, msClusterConfigMap);
            }

        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found", e);
                }
            }
        }
        knLogger.debug("Exit - : ",methodName ,msClusterConfigMap);
        return msClusterConfigMap;
    }

    public Map<String, String> retrieveMSSvcsServiceConfig(int clusterId, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrieveMSSvcsClusterConfig";
        knLogger.debug(methodName, "ENTRY: Retrieving Microservices Cluster Config info");
        List<KnMicroSvcsServiceConfig> msServiceConfigList = new ArrayList<>();
        Map<String, String> msSvcConfigDocMap = new HashMap<>();
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            msServiceConfigList = (List<KnMicroSvcsServiceConfig>) cacheManager.get(KnCacheKeys.MS_SVC_CONFIG_DOC);

            if (msServiceConfigList == null || msServiceConfigList.isEmpty()) {
                knLogger.debug(methodName, "Not found in cache, hence reading from DB");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                msServiceConfigList = xdmServerDAO.retrieveMSSvcsServiceConfig(persisterTxn);
                cacheManager.put(KnCacheKeys.MS_SVC_CONFIG_DOC, msServiceConfigList);
            }

            for(KnMicroSvcsServiceConfig serviceConfigDto : msServiceConfigList){
                if(clusterId == serviceConfigDto.getClusterId()) {
                    msSvcConfigDocMap.put(serviceConfigDto.getParamName(), serviceConfigDto.getParamvalue());
                }
            }
        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found", e);
                }
            }
        }

        return msSvcConfigDocMap;
    }

    public Map<Integer, String> retrievePTXbucketUrls(KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrievePTXbucketUrls";
        knLogger.debug(methodName, "ENTRY: Retrieving PTX bucket url info ");
        Map<Integer, String> ptxUrls = new HashMap<>();
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            ptxUrls = (HashMap<Integer, String>) cacheManager.get(KnCacheKeys.PTX_URLS_CAUCH_BASE);

            if (ptxUrls == null || ptxUrls.isEmpty()) {
                ptxUrls = new HashMap<>();
                knLogger.debug(methodName, "Not found in cache, hence reading from DB");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                Map<Integer, String> sericeFqdnMap = xdmServerDAO.retrieveSericeFqdnInfo(persisterTxn);

                if(sericeFqdnMap!= null && !sericeFqdnMap.isEmpty()){
                    for(Map.Entry<Integer, String> entry : sericeFqdnMap.entrySet()){
                        Map<String, String> msSvcConfigDocMap = retrieveMSSvcsCommonConfig(entry.getKey(), persisterTxn);
                        if(Integer.parseInt(msSvcConfigDocMap.get(CONSUL_SERVICE_FLAG)) == 1){
                        StringBuilder strBuilder = new StringBuilder(100);
                        knLogger.debug(methodName, "IS_SG_SSL_ENABLED--->"+IS_SG_SSL_ENABLED);
                        if(SSL_ENABLED.equals(msSvcConfigDocMap.get(IS_SG_SSL_ENABLED))) {
                            strBuilder.append(HTTP_SCHEME_TLS);
                        }else{
                            knLogger.info(methodName, "Making non ssl connection to syncGW");
                            strBuilder.append(HTTP_SCHEME);
                        }
                        strBuilder.append(entry.getValue());
                        strBuilder.append(COLON);
                        strBuilder.append(msSvcConfigDocMap.get(PTX_SYNC_GW_ADMIN_PORT));
                        strBuilder.append(FORWARD_SLASH);
                        strBuilder.append(msSvcConfigDocMap.get(PTX_BUCKET_NAME));
                        strBuilder.append(FORWARD_SLASH);
                        knLogger.debug(methodName, "PTX url for cluster Id ", entry.getKey(), " is ", strBuilder.toString() );
                        ptxUrls.put(entry.getKey(), strBuilder.toString());
                        }
                    }
                }
                int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
                if(!sericeFqdnMap.keySet().contains(clusterId) || ptxUrls.isEmpty()){
                    knLogger.error(methodName,"!sericeFqdnMap.keySet().contains(clusterId)- ", !sericeFqdnMap.keySet().contains(clusterId)," ptxUrls.isEmpty()",  ptxUrls.isEmpty());
                    throw new KnBOException(KnErrorCodes.BOEntity.SERVER_CONFIGURATION_FAILURE, "Server configuration not found");
                }
                knLogger.debug(methodName, "PTX urls cached  " ,ptxUrls);
                cacheManager.put(KnCacheKeys.PTX_URLS_CAUCH_BASE, ptxUrls);
            }

        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found", e);
                }
            }
        }

        return ptxUrls;
    }

    /**
     * This method retrieves to get the service fqdn and other ServiceConfig configuration. *
     *
     * @return the map of configuration
     * @throws KnBOException
     *             exception
     */
    public Map<Integer, KnMqServiceConfig> retrieveServerConfDetails(KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrieveServerConfDetails";
        knLogger.debug(methodName, "ENTRY: retrieve PTTID Display Pref variable Value",persisterTxn);
        Map<Integer, KnMqServiceConfig> knMqServiceConfigMap = new HashMap<Integer, KnMqServiceConfig>();
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            knMqServiceConfigMap = ( Map<Integer, KnMqServiceConfig>) cacheManager.get(KnCacheKeys.MQ_SERVICE_CONFIG_INFO);

            if (knMqServiceConfigMap == null || knMqServiceConfigMap.isEmpty()) {
                knLogger.debug(methodName, "Not found in cache, hence reading from DB");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                knMqServiceConfigMap = xdmServerDAO.retrieveMqConfigDetails(persisterTxn);
                cacheManager.put(KnCacheKeys.MQ_SERVICE_CONFIG_INFO, knMqServiceConfigMap);
            }
            knLogger.debug(methodName, "knMqServiceConfigMap: ", knMqServiceConfigMap);
        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found", e);
                }
            }
        }
        return knMqServiceConfigMap;
    }

    /**
     * Method to retrieve the routing keys for user and group event queues.
     * @param xdmPttServerId
     * @param persisterTxn
     * @return
     * @throws KnBOException
     */

    public Map<String, String> retrieveNotifyRoutingKeys(String xdmPttServerId, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retrieveNotifyRoutingKeys(String,KnPersisterTxn)";
        Map<String, String> notifyQueueRoutingKeys = null;
        //  KnPOCSuppDevicesDTO pocSuppDevicesDTO = null;
        knLogger.debug(methodName, "ENTRY: Retrieve Routing keys");
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            knLogger.debug(methodName, "Reading from Cache");
            notifyQueueRoutingKeys = (HashMap<String, String>) cacheManager.get(KnCacheKeys.MQ_QUEUE_ROUTING_KEYS);
            if (notifyQueueRoutingKeys == null) {
                knLogger.debug(methodName, " Not found in cache, hence reading from DB.. ");
                int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
                int sigCordType = KnGeneralUtil.getSoftwareInstalled().value();
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                notifyQueueRoutingKeys = xdmServerDAO.retrieveNotifyRoutingKeys(clusterId,sigCordType,persisterTxn);
                cacheManager.put(KnCacheKeys.MQ_QUEUE_ROUTING_KEYS, notifyQueueRoutingKeys);
            }
            knLogger.debug(methodName, "Retrieved POC Blacklisted Devices " + notifyQueueRoutingKeys);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected Exception occurred", e);
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get POC Blacklisted Devices", e);
        } finally {
            knLogger.info(methodName, "EXIT: Routing Keys " + notifyQueueRoutingKeys);
        }

        return notifyQueueRoutingKeys;
    }

    public Map<Integer,KnClientTypeConfigDTO> getClientTypeConfig(String countryCode, KnPersisterTxn persisterTxn) throws KnBOException {

        String methodName = "getClientTypeConfig(countryCode)";
//        Map<String,Map<Integer, KnClientTypeConfigDTO>> clientTypeConfigDTOMap = new HashMap<>();
        Map<Integer, KnClientTypeConfigDTO> clientTypeConfigDTOMap = new HashMap<>();
        Map<Integer, KnClientTypeConfigDTO> clientConfigDTO = new HashMap<>();

        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            clientTypeConfigDTOMap = (Map<Integer, KnClientTypeConfigDTO>) cacheManager.get(KnCacheKeys.CLIENT_TYPE_CONFIG);

            if (clientTypeConfigDTOMap == null || clientTypeConfigDTOMap.isEmpty()) {
                knLogger.debug(methodName, "Not found in cache hence reading from db");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                clientTypeConfigDTOMap = xdmServerDAO.retrieveClientTypeConfig(persisterTxn);
                cacheManager.put(KnCacheKeys.CLIENT_TYPE_CONFIG, clientTypeConfigDTOMap);
            }

//            clientConfigDTO = clientTypeConfigDTOMap.get(countryCode);
            knLogger.debug(methodName, "client type config value -", clientTypeConfigDTOMap);
            if (clientTypeConfigDTOMap == null) {
                throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                        "XDMS Service Config info not found");
            }

        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            if (e instanceof KnConnectionException) {
                throw new KnBOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Operation failed due to connection error : ", e);
            } else if (e instanceof KnPersistenceException) {
                String errorCode = e.getErrorCode();
                if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found", e);
                }
            }
        }
        return clientTypeConfigDTOMap;
    }
    public void insertCBTxnFailLog(KnFailedCBTxnLogDTO cbTxnLogDTO, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "insertCBTxnFailLog(KnFailedCBTxnLogDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: insertCBTxnFailLog ", cbTxnLogDTO);
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            xdmServerDAO.insertCBTxnFailLog(cbTxnLogDTO, persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to insert Failed txn info", e);
        } finally {
            knLogger.info(methodName, "EXIT: insertCBTxnFailLog() ");
        }
    }

    public String getIDMInternalFqdn(KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "getIDMInternalFqdn(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY");
        StringBuilder idmUrl;
        try {
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            knLogger.debug(methodName, "clusterId - ", clusterId);
            Map<String, String> rtxConfigValues = retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            idmUrl = new StringBuilder();
            String idmFqdn = rtxConfigValues.get(IDM_INTF_INT_FQDN);
            knLogger.debug(methodName, "idmFqdn - ", idmFqdn);
            String port = rtxConfigValues.get(IDM_WS_FQDN_INT_PORT);
            knLogger.debug(methodName, "port - ", port);
            idmUrl.append(idmFqdn).append(COLON).append(port);
        } catch (KnBOException ex) {
            throw ex;
        }
        knLogger.debug(methodName, "idmUrl.toString() - ", idmUrl.toString());
        return idmUrl.toString();
    }

    public void deleteCBTxnFailLog(KnFailedCBTxnLogDTO cbTxnLogDTO, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "deleteCBTxnFailLog(KnFailedCBTxnLogDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: KnFailedCBTxnLogDTO ", cbTxnLogDTO);
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            xdmServerDAO.deleteCBTxnFailLog(cbTxnLogDTO, persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to delete Failed txn info", e);
        } finally {
            knLogger.info(methodName, "EXIT: deleteCBTxnFailLog() ");
        }
    }


    /**
     * This method is to calculate final serviceAuthStatus as per 9.0 feature User Check, ambient and discret listening
     * SDD_R9_0_P8_F_1
     * @param serviceStatusOp
     * @param serviceStatusAuthUser
     * @return serviceAuthStatus
     *
     * */
    public static Integer calculateServiceAuthStatus(int serviceStatusOp, int serviceStatusAuthUser){
        String methodName = "calculateServiceAuthStatus(serviceStatusOp, serviceStatusAuthUser)";
        knLogger.debug(methodName," serviceStatusOp -", serviceStatusOp, " serviceStatusAuthUser -",serviceStatusAuthUser);
        int serviceAuthStatus =0;

        if(serviceStatusAuthUser == 3 || serviceStatusOp == 3){
            serviceAuthStatus = 3;
        }else if (serviceStatusAuthUser ==2 && serviceStatusOp == 2){
            serviceAuthStatus = 2;
        }else {
            serviceAuthStatus = 0;
        }

        knLogger.debug(methodName," Returning service auth status : ",serviceAuthStatus);
        return serviceAuthStatus;
    }

	public Map<Integer, KnDataPkgInfoDTO> retrieveDataPkgInfo(KnPersisterTxn persisterTxn)throws KnBOException
    {
    	 String methodName = "retrieveDataPkgInfo(KnPersisterTxn persisterTxn)";
         knLogger.info(methodName, "ENTRY: retrieveDataPkgInfo ", persisterTxn);
         Map<Integer, KnDataPkgInfoDTO> dataPkgInfo=null;
         try {
        	 KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
             ICacheManager cacheManager = configManager.getCacheManager();
             String xdmPttServerId = retrieveLocalXDMPttServerId();
             knLogger.debug(methodName, "reading from Cache");
             dataPkgInfo = (Map<Integer, KnDataPkgInfoDTO>) cacheManager.get(KnCacheKeys.DATA_PACKAGE_INFO);
             if (dataPkgInfo == null || dataPkgInfo.isEmpty()) {
                 knLogger.debug(methodName, "Not found in cache hence reading from db");
                 IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                 dataPkgInfo=xdmServerDAO.retrieveDataPkgInfo(persisterTxn);
                 cacheManager.put(KnCacheKeys.DATA_PACKAGE_INFO, dataPkgInfo);
             }
             knLogger.debug(methodName, "data pkg info -", dataPkgInfo);
         } catch (KnConfigurationException e) {
             knLogger.error(methodName, "Failed in laoding configuration manager");
             throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
         }  catch (KnDAOException e) {
        	 knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
		} finally {
             knLogger.info(methodName, "EXIT: retrieveDataPkgInfo() ");
         }
		return dataPkgInfo;

    }
    public Map<Integer, Integer> getDataPkgId(Integer pkgType, KnPersisterTxn persisterTxn) throws KnBOException
    {
    	 String methodName = "getDataPkgId(Integer profileId, KnPersisterTxn persisterTxn)";
         knLogger.info(methodName, "ENTRY: getDataPkgId ", "pkg type -",pkgType);
         Map<Integer, Map<Integer, Integer>> dataPkdMap=null;
         try {
        	 KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
             ICacheManager cacheManager = configManager.getCacheManager();
             String xdmPttServerId = retrieveLocalXDMPttServerId();
             knLogger.debug(methodName, "reading from Cache");
             dataPkdMap = (Map<Integer, Map<Integer, Integer>>) cacheManager.get(KnCacheKeys.ADDL_PROFILE_INFO_BY_PKGTYPE);
             if (dataPkdMap == null ||dataPkdMap.isEmpty()) {
                 knLogger.debug(methodName, "Not found in cache hence reading from db");
                 IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                 dataPkdMap= xdmServerDAO.retrieveAddlProfileInfoByPkgType(persisterTxn);
                 cacheManager.put(KnCacheKeys.ADDL_PROFILE_INFO_BY_PKGTYPE, dataPkdMap);
             }
             knLogger.debug(methodName, "data pkg info -", dataPkdMap);
         } catch (KnConfigurationException e) {
             knLogger.error(methodName, "Failed in laoding configuration manager");
             throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
         } catch (KnDAOException e) {
        	 knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
		} finally {
             knLogger.info(methodName, "EXIT: getDataPkgId() ");
         }

		return dataPkdMap.get(pkgType);


    }

    public String getOIDCInternalFqdn(KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "getOIDCInternalFqdn(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY");
        StringBuilder idmUrl;
        try {
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            knLogger.debug(methodName, "clusterId - ", clusterId);
            Map<String, String> rtxConfigValues = retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            idmUrl = new StringBuilder();
            String idmFqdn = rtxConfigValues.get(OIDC_INTF_INT_FQDN);
            knLogger.debug(methodName, "oidcFqdn - ", idmFqdn);
            String port = rtxConfigValues.get(OIDC_WS_FQDN_INT_PORT);
            knLogger.debug(methodName, "port - ", port);
            idmUrl.append(idmFqdn).append(COLON).append(port);
        } catch (KnBOException ex) {
            knLogger.error(methodName, "Exception Occured - ", ex);
            throw ex;
        }
        knLogger.debug(methodName, "oidcFqdn.toString() - ", idmUrl.toString());
        return idmUrl.toString();
    }

    public String getEmergencyResourcePriority(int QPPPCRFProfileId, int apnId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "getEmergencyResourcePriority(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY");
        String emergencyResourcePriority = null;
        List<KnQPPpcrfProfileDTO> qpPpcrfProfileDTOS = new ArrayList<>();
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            qpPpcrfProfileDTOS = (List<KnQPPpcrfProfileDTO>) cacheManager.get(KnCacheKeys.QPP_PCRF_PROFILE);
            if (qpPpcrfProfileDTOS == null || qpPpcrfProfileDTOS.isEmpty()) {
                knLogger.debug(methodName, "Not found in cache, hence reading from DB");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                qpPpcrfProfileDTOS = xdmServerDAO.retriveQPPpcrfProfile(readOnly, persisterTxn);
                cacheManager.put(KnCacheKeys.QPP_PCRF_PROFILE, qpPpcrfProfileDTOS);
            }

            knLogger.debug("qpPpcrfProfileDTOS :",qpPpcrfProfileDTOS);
            for(KnQPPpcrfProfileDTO qpPpcrfProfileDTO : qpPpcrfProfileDTOS){
                if(qpPpcrfProfileDTO.getQppPcrfProfileId() == QPPPCRFProfileId && qpPpcrfProfileDTO.getApnId() == apnId) {
                    emergencyResourcePriority = qpPpcrfProfileDTO.getMcpttIdentifier()  +"."+ qpPpcrfProfileDTO.getReservationPriority();
                }
            }
        } catch (KnConfigurationException e) {
            knLogger.error(methodName, "Failed in laoding configuration manager");
            throw new KnBOException(KnErrorCodes.ConfigManager.INTERNAL_ERROR, "failed loading config manager", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
        }
        knLogger.debug(methodName, "emergencyResourcePriority - ", emergencyResourcePriority);
        return emergencyResourcePriority;
    }

    public void updateSipDigestPwd(String deviceId,String pwd,KnPersisterTxn persisterTxn){

        String methodName = "updateSipDigestPwd(String,String,KnPersisterTxn)";
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            xdmServerDAO.updateDigestPwd(deviceId, pwd, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Expected Exception occurred - ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
        }

    }

    public int getDeviceCountForCorpId(int corpId, KnPersisterTxn persisterTxn) {

        String methodName = "getDeviceCountForCorpId(String,KnPersisterTxn)";
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            return xdmServerDAO.getDeviceCountForCorpId(corpId, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Expected Exception occurred - ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
        }
        return -1;
    }

    public int getDeviceCountByMdnAndCorpId(String mdn, int corpId, KnPersisterTxn persisterTxn) {

        String methodName = "getDeviceCountForCorpId(String,KnPersisterTxn)";
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            return xdmServerDAO.getDeviceCountByMdnAndCorpId(mdn, corpId, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Expected Exception occurred - ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
        }
        return -1;
    }

    public String getDeviceImpl(String deviceId,KnPersisterTxn persisterTxn){

        String methodName = "getDeviceImpl(String,KnPersisterTxn)";
        String deviceImpu = null;
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            deviceImpu = xdmServerDAO.getDeviceImpuInfo(deviceId,persisterTxn );
        } catch (KnBOException e) {
            knLogger.error(methodName, "Expected Exception occurred - ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
        }

        return deviceImpu;
    }

    public boolean checkDeviceExsists(String deviceId,KnPersisterTxn persisterTxn){

        String methodName = "checkDeviceExsists(String,String,KnPersisterTxn)";
        boolean result = false;
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            result = xdmServerDAO.checkDeviceExsists(deviceId,persisterTxn );
        } catch (KnBOException e) {
            knLogger.error(methodName, "Expected Exception occurred - ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
        }

        return result;
    }

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnBOException
     */
    public String getMCSXCAPRootURI(String mdn, KnPersisterTxn persisterTxn) throws KnBOException {
        final String methodName = "getMCSXCAPRootURI(mdn, KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdn(mdn), persisterTxn);
        String mcsXcapRootURI = null;
        Integer apnid;
        try {
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(retrieveLocalXDMPttServerId());
            apnid = xdmServerDAO.getSubsApnId(mdn, persisterTxn);
            List<String> mcsxcapRootURIS = getMCSXCAPRootURIS(apnid, persisterTxn);
            knLogger.debug(methodName,"mcsxcapRootURIS - ",mcsxcapRootURIS);
            if(mcsxcapRootURIS!=null && !mcsxcapRootURIS.isEmpty()) {
                mcsXcapRootURI = mcsxcapRootURIS.get(0) + com.kodiak.common.resources.KnConstants.MCSXCAP_XCAP_ROOT_CONTEXT;
            }
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        " mcs xcap root uri  found", e);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred");
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  apn profile info  ", e);
        }
        knLogger.debug(methodName, mcsXcapRootURI);
        return mcsXcapRootURI;

    }


    /**
     * @param persisterTxn
     * @return
     * @throws KnBOException
     */
    public Set<String> getMCSXCAPRootURIs(KnPersisterTxn persisterTxn) throws KnBOException {
        final String methodName = "getMCSXCAPRootURIs(KnPersisterTxn)";
        knLogger.debug(methodName, persisterTxn);
        Set<String> mcsXcapRootURIs = new HashSet<>();

        List<String> mcsxcapRootURIS = getMCSXCAPRootURIS(null, persisterTxn);
        knLogger.debug(methodName, "mcsxcapRootURIS - ", mcsxcapRootURIS);
        if (mcsxcapRootURIS != null && !mcsxcapRootURIS.isEmpty()) {
            mcsxcapRootURIS.forEach(rooturi -> {
                if (rooturi != null && rooturi.trim().length() > 0) {
                    String mcsXcapRootURI = rooturi + com.kodiak.common.resources.KnConstants.MCSXCAP_XCAP_ROOT_CONTEXT;
                    mcsXcapRootURIs.add(mcsXcapRootURI);
                }
            });
        }
        knLogger.debug(methodName, "mcsXcapRootURIs - ", mcsXcapRootURIs);
        return mcsXcapRootURIs;

    }


    /**
     *
     * @param apnId
     * @param persisterTxn
     * @return
     * @throws KnBOException
     */
    private List<String> getMCSXCAPRootURIS(Integer apnId, KnPersisterTxn persisterTxn) throws KnBOException {
        final String methodName = "getMCSXCAPRootURIS(Integer, KnPersisterTxn)";
        knLogger.debug(methodName, apnId, persisterTxn);
        String mcsXcapRootURI = null;
        Map<Integer, KnAPNConfigDTO> knAPNConfDTOMap = new HashMap<Integer, KnAPNConfigDTO>();
        KnAPNConfigDTO apnConfigDTO = new KnAPNConfigDTO();
        List<String> mcsXCAPRootUris = new ArrayList<>();
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName, "reading from Cache");
            knAPNConfDTOMap = (Map<Integer, KnAPNConfigDTO>) cacheManager.get(KnCacheKeys.APN_CONFIG_INFO);

            if (knAPNConfDTOMap == null || knAPNConfDTOMap.isEmpty()) {
                knLogger.debug(methodName, "Not found in cache hence reading from db");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                knAPNConfDTOMap = xdmServerDAO.retrieveAPNInfoConfig(persisterTxn);
                cacheManager.put(KnCacheKeys.APN_CONFIG_INFO, knAPNConfDTOMap);
            }
            if (apnId == null) {
                knAPNConfDTOMap.forEach((k, apnConfig) -> {
                    mcsXCAPRootUris.add(apnConfig.getMcsXCAPUri());
                });
                knLogger.debug(methodName, "mcs xcap uri's list - ", mcsXCAPRootUris);
            } else {
                apnConfigDTO = (KnAPNConfigDTO) knAPNConfDTOMap.get(apnId);
                knLogger.debug(methodName, "APNconfigDTO  value -", apnConfigDTO);
                if (apnConfigDTO == null) {
                    throw new KnBOException(KnErrorCodes.BOEntity.XDMS_SUBSYSTEM_CONFIG_NOT_FOUND,
                            "XDMS Service Config info not found");
                }
                mcsXCAPRootUris.add(apnConfigDTO.getMcsXCAPUri());
            }

            knLogger.debug(methodName, "retrieved mcsXcapRootUriMap  - ", mcsXcapRootURI);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        " mc xcap root uri not found", e);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected exception occurred", e);
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  apn profile info  ", e);
        }
        knLogger.debug(methodName, mcsXcapRootURI);
        return mcsXCAPRootUris;
    }
    
    
    public KnXDMSubsProfileRespDTO selectSubsProfileInfo(List<String> mdnList, KnPersisterTxn persistTxn) throws KnBOException {
        String methodName = "selectSubsProfileInfo(List<String>, KnPersisterTxn)";
        KnXDMSubsProfileRespDTO respDTO = null;
        knLogger.debug(methodName, "ENTRY: retrieve subscriber profile info - ", KnGDPRTemplate.mdnList(mdnList));
        try {
        	String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            respDTO = xdmServerDAO.selectSubsProfileInfo(mdnList, persistTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        "Subscriber Profile info not found", e);
            }
        }catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get subscriber Info", e);
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", respDTO);
        return respDTO;
    }

    public LinkedHashSet<String> selectSubDetails(List<String> mdnList, KnPersisterTxn persistTxn) throws KnBOException {
        String methodName = "selectSubDetails(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: retrieve subscriber profile info - ", KnGDPRTemplate.mdnList(mdnList));
        LinkedHashSet<String> dispMdnList = new LinkedHashSet<String>();
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            dispMdnList = xdmServerDAO.selectSubDetails(mdnList, persistTxn);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get subscriber Info", e);
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", dispMdnList.size());
        return dispMdnList;
    }

    public List<String> getMcsIdNullMdnList(KnPersisterTxn persistTxn) throws KnBOException {
        String methodName = "getMcsIdNullMdnList(KnPersisterTxn)";
        List<String> mdnList = null;
        knLogger.debug(methodName, "ENTRY: getMcsIdNullMdnList - ",persistTxn);
        try {
        	String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            mdnList = xdmServerDAO.getMcsIdNullMdnList(persistTxn);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to getMcsIdNullMdnList", e);
        }
        knLogger.debug(methodName, "EXIT : getMcsIdNullMdnList -> ",KnGDPRTemplate.mdnList(mdnList));
        return mdnList;
    }
    
    public void updateMcsIdForMdnList(List<String> mdnList,KnPersisterTxn persistTxn) throws KnBOException {
        String methodName = "updateMcsIdForMdnList(List<String>,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: updateMcsIdForMdnList - ",KnGDPRTemplate.mdnList(mdnList));
        try {
        	String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            xdmServerDAO.updateMcsIdForMdnList(mdnList,persistTxn);
        }catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to updateMcsIdForMdnList", e);
        }
        knLogger.debug(methodName, "EXIT : updateMcsIdForMdnList -> ",KnGDPRTemplate.mdnList(mdnList));
    }

    public KnTalkGrpScanModeDTO getSubsTalkGrpScanMode(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "getSubsTalkGrpScanMode(String, boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName,"xdmPttServerId ======== ",xdmPttServerId);
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            return xdmServerDAO.getSubsTalkGrpScanMode(mdn, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to getSubsTalkGrpScanMode", e);
        }
    }
    
    
    public List<String> getBaseMdn(List<String> mdnList, KnPersisterTxn persistTxn) throws KnBOException {
        String methodName = "getBaseMdn)";
        List<String> mdns = new ArrayList<String>();
        knLogger.debug(methodName, "getBaseMdn from profile MDN's - ",KnGDPRTemplate.mdnList(mdnList));
        try {
        	String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            mdnList = xdmServerDAO.getRealMdns(mdnList, true, persistTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        "Subscriber Profile info not found", e);
            }
        }catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get subscriber Info", e);
        }
        knLogger.debug(methodName, "EXIT : retrive BaseMdn -> ",KnGDPRTemplate.mdnList(mdnList));
        return mdnList;
    }

    public Map<String, Set<String>> getBaseMdnsMap(List<String> mdnList, KnPersisterTxn persistTxn) throws KnBOException {
        String methodName = "getBaseMdnsMap(List<String>, KnPersisterTxn)";
        Map<String, Set<String>> mdns = new HashMap<String, Set<String>>();
        knLogger.debug(methodName, "getBaseMdn from profile MDN's - ", KnGDPRTemplate.mdnList(mdnList));
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            mdns = xdmServerDAO.getBaseMdnsMap(mdnList, true, persistTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        "Subscriber Profile info not found", e);
            }
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get subscriber Info", e);
        }
        knLogger.info(methodName, "EXIT : retrive BaseMdn -> ", mdnList.size());
        return mdns;
    }

    public void insertSubsTalkGrpScanMode(KnTalkGrpScanModeDTO talkGrpScanModeDTO,KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "insertSubsTalkGrpScanMode(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName,"xdmPttServerId ======== ",xdmPttServerId);
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            xdmServerDAO.insertSubsTalkGrpScanMode(talkGrpScanModeDTO, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to insertSubsTalkGrpScanMode", e);
        }
    }

    public void updateSubsTalkGrpScanMode(List<String> mdnList,int tgscMode,KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "updateSubsTalkGrpScanMode(List<String>,int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            knLogger.debug(methodName,"xdmPttServerId ======== ",xdmPttServerId);
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            xdmServerDAO.updateSubsTalkGrpScanMode(mdnList,tgscMode, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to insertSubsTalkGrpScanMode", e);
        }
    }

    public static String decryptPayloadForLIEvent(String key, String payload, String iv) throws Exception {
        final String methodName = "decryptPayloadForLIEvent(String,String,IV)";
        knLogger.debug(methodName, "Entry Encrpted MDN",payload);
        byte[] decryptedBytes =null;
        try {
            byte[] decodedKey = Base64.getDecoder().decode(key);
            byte[] decodedIV = Base64.getDecoder().decode(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(decodedIV);
            SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(payload));
        }
        catch(Exception e){
            knLogger.error(methodName, "Exception occurred : ",e);
            throw e;
        }
        return new String(decryptedBytes);
    }

    public static <TSource, TRes> void transformList(List<TSource> sourceList, List<TRes> resultList) {
        if (sourceList.size() > 0) {
            for (TSource obj : sourceList) {
                TRes result = (TRes) obj;
                if (result == null) {
                    System.out.println("error in conversion");
                }
                resultList.add(result);
            }
        }
    }
    public void updateSegmentIndicator(KnPersisterTxn persistTxn, List<String> mdns, Map<String, String> mdnSegmentIndicator) throws KnBOException {
        // TODO Auto-generated method stub
        String methodName = "updateSegmentIndicator(KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: updateSegmentIndicator - ");
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            xdmServerDAO.updateSegmentIndicator(persistTxn,mdns,mdnSegmentIndicator);
        }catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to updateSegmentIndicator", e);
        }
    }
    public Map<String, String> getSegIndEnableSubs(KnPersisterTxn persistTxn) throws KnBOException {
        // TODO Auto-generated method stub
        String methodName = "getSegIndEnableSubs(KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: updateSegmentIndicator - ");
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            return xdmServerDAO.getSegIndEnableSubs(persistTxn);
        }catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to updateSegmentIndicator", e);
        }
    }

    public static Collection<Integer> convertStrToIntColl(Collection<String> strColl) {
        Collection<Integer> intColl = new ArrayList<Integer>(strColl.size());
        for (String idStr : strColl) {
            if (idStr != null && !idStr.isEmpty()) {
                Integer id = Integer.valueOf(idStr);
                intColl.add(id);
            }
        }
        return intColl;
    }

    public static Collection<Integer> convertStrToIntInRange(Collection<String> strColl) {
        Collection<Integer> intColl = new ArrayList<Integer>(strColl.size());
        for (String idStr : strColl) {
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    Integer id = Integer.valueOf(idStr);
                    intColl.add(id);
                }catch (Exception e) {
                    knLogger.error(" convertStrToIntInRange idStr:",idStr);
                }
            }
        }
        return intColl;
    }

    public static String formIntegerCommaSeperatedIdList(Collection<Integer> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        if (collectionStr == null || collectionStr.isEmpty()) {
            return "";
        }
        for (Integer str : collectionStr) {
            buffer = buffer.append(str).append(",");
        }
        int indx = buffer.lastIndexOf(",");
        if (indx > 0) {
            buffer.deleteCharAt(indx);
        }
        return buffer.toString();
    }
    public static String replaceContactWithValue(String str, String constant, String value) {
        String finalStr = "";
        if (str == null || str.trim().equals("")) {
            return finalStr;
        }
        finalStr = str.replaceAll(constant, value);
        return finalStr;
    }

    public String selectSubscriberMdnByMCSId(List<String> mcsIds, KnPersisterTxn persistTxn) throws KnBOException {
        String methodName = "selectSubscriberMdnByMCSIds";
        String mdn= null;
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            mdn= xdmServerDAO.selectSubscriberMdnByMCSId(mcsIds, persistTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        "Subscriber Profile info not found", e);
            }
        }catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get subscriber base mdn", e);
        }
        return mdn;
    }

    public KnXDMDeviceProvDTO getDeviceImuiInfo(String deviceId, KnPersisterTxn persistTxn) throws KnBOException {
        String methodName = "getDeviceImuiInfo)";
        knLogger.debug(methodName, "Get DeviceImui Info by deviceId - ",deviceId);
        KnXDMDeviceProvDTO deviceProvDTO = new KnXDMDeviceProvDTO();
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            deviceProvDTO = xdmServerDAO.getDeviceImuiInfo(deviceId, persistTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                        "device info not found", e);
            }
        }catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get device Info", e);
        }
        knLogger.debug(methodName, "EXIT : retrive DeviceImuiInfo -> ",deviceProvDTO);
        return deviceProvDTO;
    }

    public static String mcpttGroupUri(int groupId, int corpId, String sipProxyUri){
        StringBuilder builder = new StringBuilder();
        builder.append(SIP).append(corpId)
                .append(DOT).append(groupId)
                .append(AT).append(sipProxyUri);
        return builder.toString();
    }

    public int getXdmMaxNotificationCount(){
        int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
        Map<String, String> microServicesParamNameValueMap = null;
        try {
            microServicesParamNameValueMap = retrieveMSSvcsCommonConfig(clusterId);
        } catch (KnBOException e) {
            throw new RuntimeException(e);
        }
        return Integer.parseInt(microServicesParamNameValueMap.get(XDM_MAX_NOTIFICATION_COUNT) != null ? microServicesParamNameValueMap.get(XDM_MAX_NOTIFICATION_COUNT) : "1000");
    }

    public int getMaxPendingNotifySize(){
        int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
        Map<String, String> microServicesParamNameValueMap = null;
        try {
            microServicesParamNameValueMap = KnGenInfoUtil.getInstance().retrieveMSSvcsCommonConfig(clusterId);
        } catch (KnBOException e) {
            throw new RuntimeException(e);
        }
        return Integer.parseInt(microServicesParamNameValueMap.get(XCAP_MAX_PENDING_NOTIFYQ_SIZE) != null ? microServicesParamNameValueMap.get(XCAP_MAX_PENDING_NOTIFYQ_SIZE) : "10000");
    }

    public boolean isSuppressWaterMark(int notificationCount, int recordCount) {
        String methodName = "isSuppressWaterMark";
        int upperWaterMark = getMaxPendingNotifySize();
        int lowerWaterMark = 0;
        if (upperWaterMark >= 5000) {
            lowerWaterMark = upperWaterMark - 5000;
        }
        knLogger.info(methodName, "isSuppressWaterMark value :", isSuppressWaterMark, "recordCount :", recordCount, "notificationCount :", notificationCount);
        if (!isSuppressWaterMark && (notificationCount + recordCount > upperWaterMark)) {
            isSuppressWaterMark = true;
            knLogger.debug(methodName, "Upper Water Mark reached , notification suppressing start, isSuppressWaterMark value set to:", isSuppressWaterMark);
        } else if (isSuppressWaterMark && (recordCount <= lowerWaterMark) && (notificationCount + recordCount <= upperWaterMark)) {
            isSuppressWaterMark = false;
            knLogger.debug(methodName, "lower Water Mark reached, notification send resumed, isSuppressWaterMark value set to:", isSuppressWaterMark);
        }
        knLogger.debug(methodName, "isSuppressWaterMark value:", isSuppressWaterMark);
        return isSuppressWaterMark;
    }

    public int getAllSubscribersCount(int corpId) throws KnBOException, KnDAOException {
        String methodName = "getAllSubscribersCount()";
        knLogger.debug(methodName, "ENTRY : getAllSubscribersCount - ");
        int allsubscribersCount = 0;
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        allsubscribersCount = xdmServerDAO.getAllSubscribersCount(corpId);
        return allsubscribersCount;
    }

    public Map<String, String> profileMdnswithMCID(int corpId, int start, int end) throws KnBOException, KnDAOException {
        String methodName = "profileMdnswithMCID(KnPersisterTxn)";
        Map<String, String> profileMdnswithMCID = new HashMap<>();
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        profileMdnswithMCID = xdmServerDAO.getProfileMDNswithMCID(corpId, start, end);
        return profileMdnswithMCID;
    }

    public List<String> profileMdnswithNoBaseMDN(Map<String, String> profileMDNsWithMCID) throws KnBOException, KnDAOException {
        String methodName = "profileMdnswithNoBaseMDN(KnPersisterTxn)";
        List<String> profileMdnswithNoBaseMDN = new ArrayList<>();
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        profileMdnswithNoBaseMDN = xdmServerDAO.getProfileMDNswithNoBaseMDN(profileMDNsWithMCID);
        return profileMdnswithNoBaseMDN;
    }

    public List<String> getAllProfileMDNs(KnPersisterTxn persisterTxn) throws KnBOException, KnDAOException {
        String methodName = "getAllProfileMDNs(KnPersisterTxn)";
        knLogger.debug(methodName,"Entry::");
        List<String> profileMDNs = new ArrayList<>();
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        profileMDNs = xdmServerDAO.getAllProfileMDNs(persisterTxn);
        return profileMDNs;
    }

    public Map<String, List<String>> getInconsistenGroupId(String profileMdn, List<String> groupIds) throws KnBOException, KnDAOException {
        String methodName = "getInconsistenGroupId(String, List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry::");
        Map<String, List<String>> mapOfProfileMdnWithGroupIds = new HashMap<>();
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        mapOfProfileMdnWithGroupIds = xdmServerDAO.getInconsistenGroupId(profileMdn, groupIds);
        return mapOfProfileMdnWithGroupIds;
    }

    public Map<String, List<String>> getSublistIdContactMdnMap(String sublistId) throws KnBOException, KnDAOException {
        String methodName = "getSublistIdContactMdnMap(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry::");
        Map<String, List<String>> mapOfSublistIdWithContactMdn = new HashMap<>();
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        mapOfSublistIdWithContactMdn = xdmServerDAO.getSublistIdContactMdnMap(sublistId);
        return mapOfSublistIdWithContactMdn;
    }

    public List<String> ifSublistExistsinTT(List<String> sublistIds, KnPersisterTxn persisterTxn) throws KnBOException, KnDAOException {
        String methodName = "ifSublistExistsinTT(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry::");
        List<String> sublistIdList = new ArrayList<>();
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        sublistIdList = xdmServerDAO.ifSublistExistsinTT(sublistIds, persisterTxn);
        return sublistIdList;
    }

    public Map<String, List<String>> getProfileMdnsForUPMId(String upmId) throws KnBOException, KnDAOException {
        String methodName = "getProfileMdnsForUPMId(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry::");
        Map<String, List<String>> mapOfUPMIdWithProfileMdns = new HashMap<>();
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        mapOfUPMIdWithProfileMdns = xdmServerDAO.getProfileMdnsForUPMId(upmId);
        return mapOfUPMIdWithProfileMdns;
    }

    public Map<String, List<String>> getContactMdnByProfileMdn(String profileMdn) throws KnBOException, KnDAOException {
        String methodName = "getContactMdnByProfileMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry::");
        Map<String, List<String>> mapOfProfileMdnWithContactMdn = new HashMap<>();
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        mapOfProfileMdnWithContactMdn = xdmServerDAO.getContactMdnByProfileMdn(profileMdn);
        return mapOfProfileMdnWithContactMdn;
    }

    public String getFeatureBSForProfileMdnFromTT(String profileMdn) throws KnBOException, KnDAOException {
        String methodName = "getFeatureBSForProfileMdnFromTT(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry::");
        String featureBSForProfileMDN = null;
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        featureBSForProfileMDN = xdmServerDAO.getFeatureBSForProfileMdnFromTT(profileMdn);
        return featureBSForProfileMDN;
    }

    public Map<String, KnCorpUserProfileMCPTTConfig> getProfileMdnPerm(String profileMdn) throws KnBOException, KnDAOException {
        String methodName = "getProfileMdnPerm(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry::");
        Map<String, KnCorpUserProfileMCPTTConfig> profileMdnPermMap = new HashMap<>();
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            profileMdnPermMap = xdmServerDAO.getProfileMdnPerm(profileMdn);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
        }
        return profileMdnPermMap;
    }

    public static String getSecDBPttServerId(String pttServerId) throws IllegalArgumentException {
        String methodName = "getSecDBPttServerId(String)";
        if (pttServerId == null || pttServerId.isEmpty()) {
            knLogger.error(methodName, "Invalid argument - pttServerId is null or empty");
            throw new IllegalArgumentException("pttServerId cannot be null or empty");
        }
        String secDBPttServerId = pttServerId + "_" + KnDBConst.DataStores.XDM_SHARED_DATA.getValue();
        knLogger.debug(methodName, "Exit::" , secDBPttServerId);
        return secDBPttServerId;
    }

    public List<String> getAllCorpIds() {
        String methodName = "getAllCorpIds()";
        knLogger.debug(methodName, "Entry::");
        List<String> corpIds = new ArrayList<>();
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            corpIds = xdmServerDAO.getAllCorpIds();
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
        }
        return corpIds;
    }

    public List<String> getGroupIdsForProfileMdn(String profileMdnItr) {
        String methodName = "getGroupIdsForProfileMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry::");
        List<String> groupIds = new ArrayList<>();
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            groupIds = xdmServerDAO.getGroupIdsForProfileMdn(profileMdnItr);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
        }
        return groupIds;
    }

    public KnEmergencyInfoDTO getEmergencyInfoForProfileMdn(String profileMdnItr) {
        String methodName = "getEmergencyInfoForProfileMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry::");
        KnEmergencyInfoDTO emergencyInfoDTO = new KnEmergencyInfoDTO();
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            emergencyInfoDTO = xdmServerDAO.getEmergencyInfoForProfileMdn(profileMdnItr);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
        }
        return emergencyInfoDTO;
    }
    public Map<String, Map<Integer,Integer>> getSublistDetailByProfileMdns(List<String> profileMdn, KnPersisterTxn persisterTxn) throws KnBOException, KnDAOException {
        String methodName = "getContactMdnByProfileMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry::");
        Map<String, Map<Integer,Integer>> mapOfProfileMdnWithContactMdn = new HashMap<>();
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        mapOfProfileMdnWithContactMdn = xdmServerDAO.getSublistDetailByProfileMdns(profileMdn, persisterTxn);
        return mapOfProfileMdnWithContactMdn;
    }

    public Map<Integer, List<String>> getSublistMemberBySublistId(List<Integer> subLists, KnPersisterTxn persisterTxn) throws KnBOException, KnDAOException {
        String methodName = "getContactMdnByProfileMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry::");
        Map<Integer, List<String>> mapOfProfileMdnWithContactMdn = new HashMap<>();
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        mapOfProfileMdnWithContactMdn = xdmServerDAO.getSublistMemberBySublistId(subLists, persisterTxn);
        return mapOfProfileMdnWithContactMdn;
    }

    public int getUpmIndexLimit(String corpID,KnPersisterTxn persisterTxn) throws KnBOException, KnDAOException {
        String methodName = "getUpmIndexLimit(String, KnPersisterTxn)";
        int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
        Map<String, String> microServicesParamNameValueMap = null;
        int systemLevelLimit = 0;
        int corpLevelLimit = 0;
        KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        try {
            microServicesParamNameValueMap = retrieveMSSvcsCommonConfig(clusterId);
        } catch (KnBOException e) {
            throw new RuntimeException(e);
        }
        systemLevelLimit = microServicesParamNameValueMap.get("MAX_USERPROFILES_PERSUB") != null
                ? Integer.parseInt(microServicesParamNameValueMap.get("MAX_USERPROFILES_PERSUB"))
                : 0;
        corpProfilePersistDTO = xdmServerDAO.getCorporateProfile(corpID,persisterTxn);
        if(null != corpProfilePersistDTO.getMaxAssignProfiles()) {
            corpLevelLimit = corpProfilePersistDTO.getMaxAssignProfiles();
        }
        knLogger.debug(methodName, "systemLevelLimit::", systemLevelLimit, "corpLevelLimit::", corpLevelLimit);
        return Math.max(systemLevelLimit, corpLevelLimit);
    }

    public boolean isAnyProfileMdnCrossingIndexLimit(String mcId, int upmIndexLimit, KnPersisterTxn persisterTxn) throws KnBOException, KnDAOException {
        String methodName = "isAnyProfileMdnCrossingIndexLimit()";
        knLogger.debug(methodName, "Entry::");
        boolean isLimitCrossed = false;
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        isLimitCrossed = xdmServerDAO.isAnyProfileMdnCrossingIndexLimit(mcId, upmIndexLimit, persisterTxn);
        return isLimitCrossed;
    }

    public void recoverImpactedMdns(String mcId, KnPersisterTxn persisterTxn) {
        String methodName = "recoverImpactedMdns(String)";
        knLogger.debug(methodName, "Entry::");
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            xdmDAO.recoverImpactedMdns(mcId, persisterTxn);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
        }
    }

    public String selectXDMCorpFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnBOException, KnDAOException {
        final String methodName = "selectCorpFS(corpId, readOnly, persisterTxn)";
        knLogger.debug(methodName, "Entry::");
        String xdmCorpFS = null;
        String xdmPttServerId = retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
        xdmCorpFS = xdmServerDAO.selectXDMCorpFS(corpId, readOnly, persisterTxn);
        return xdmCorpFS;
    }

    public KnLocationEnabledCorporateSubscriberDetails getLocationEnabledCorporateSubscriberDetails(String mdn, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "getLocationEnabledCorporateSubscriberDetails(String, String)";
        Connection conn = null;
        PreparedStatement pStmt = null;
        String localPttId = retrieveLocalXDMPttServerId();
        KnLocationEnabledCorporateSubscriberDetails locationEnabledSubsDetails = new KnLocationEnabledCorporateSubscriberDetails();
        ResultSet rs = null;
        try {
            conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(localPttId), true);
            String query = "SELECT MDN,XDMSFS2,CLIENTFS2,OPSFS2,CORPADMINFS2,USERPROFILEFS2,CLIENTPV_MAJORVERSION,ACTIVEFS2,CORPID,LASTPROFILEUPDATETIME,SUBSCRIBERFS2,POCHOME,PRESENCEHOME,XDMSHOME FROM DG.POCSUBSCRINFO WHERE MDN= ?;";
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);
            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                locationEnabledSubsDetails.setMdn(rs.getString("MDN"));
                locationEnabledSubsDetails.setXdmFS2(rs.getString("XDMSFS2"));
                locationEnabledSubsDetails.setClientFS2(rs.getString("CLIENTFS2"));
                locationEnabledSubsDetails.setOpsFS2(rs.getString("OPSFS2"));
                locationEnabledSubsDetails.setCorpAdminFS2(rs.getString("CORPADMINFS2"));
                locationEnabledSubsDetails.setUserProfileFS2(rs.getString("USERPROFILEFS2"));
                locationEnabledSubsDetails.setProtocolVersion(rs.getInt("CLIENTPV_MAJORVERSION"));
                locationEnabledSubsDetails.setActiveFS2(rs.getString("ACTIVEFS2"));
                locationEnabledSubsDetails.setCorpId(rs.getInt("CORPID"));
                locationEnabledSubsDetails.setLastUpdateTime(rs.getLong("LASTPROFILEUPDATETIME"));
                locationEnabledSubsDetails.setSubscriberFS2(rs.getString("SUBSCRIBERFS2"));
                locationEnabledSubsDetails.setPocHome(rs.getString("POCHOME"));
                locationEnabledSubsDetails.setPresenceHome(rs.getString("PRESENCEHOME"));
                locationEnabledSubsDetails.setXdmsHome(rs.getString("XDMSHOME"));
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred - ", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return locationEnabledSubsDetails;
    }

    public List<String> getBaseAndProfileMdnsInBatch(int corpId, int startIndex, int endIndex) throws KnBOException {
        String methodName = "getBaseAndProfileMdnsInBatch(int,int,int)";
        knLogger.entry(methodName, "Entry::", corpId, startIndex, endIndex);
        List<String> mdnList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String localPttId = retrieveLocalXDMPttServerId();
        try {
            conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(localPttId), true);
            String query = "SELECT ROWS ? TO ? MDN FROM DG.POCSUBSCRINFO WHERE CORPID = ?";
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, startIndex);
            pStmt.setInt(2, endIndex);
            pStmt.setInt(3, corpId);
            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.info(methodName, "QUERY: Executed - ");
            while (rs.next()) {
                mdnList.add(rs.getString("MDN"));
            }
            KnPersisterTxn.getPersisterTxn().save();
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred - ", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        knLogger.debug("MDNLIST::", mdnList.size());
        return mdnList;
    }

    public String getCorpFS2ForCorpId(int corpId) throws KnBOException {
        String methodName = "getCorpFS2ForCorpId(int)";
        String corpFS2 = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String localPttId = retrieveLocalXDMPttServerId();
        try {
            conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(localPttId), true);
            String query = "SELECT CORPFS2 FROM DG.POCCORPINFO WHERE CORPID = ?";
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);
            knLogger.info(methodName, "QUERY: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.info(methodName, "QUERY: Executed - ");
            if (rs.next()) {
                corpFS2 = rs.getString("CORPFS2");
            }
            KnPersisterTxn.getPersisterTxn().save();
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred - ", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return corpFS2;
    }

    public void updateActiveFS2andXDMSFS2InDB(String mdn, String activeFS2, String xdmsFS2) throws KnBOException {
        String methodName = "updateActiveFS2InDB(String, String)";
        Connection conn = null;
        PreparedStatement pStmt = null;
        String localPttId = retrieveLocalXDMPttServerId();
        try {
            conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(localPttId), false);
            String query = "UPDATE DG.POCSUBSCRINFO SET ACTIVEFS2 = ? , XDMSFS2 = ? WHERE MDN = ?;";
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, activeFS2);
            pStmt.setString(2, xdmsFS2);
            pStmt.setString(3, mdn);
            knLogger.info(methodName, "QUERY: Executing - ", query);
            pStmt.executeUpdate();
            knLogger.info(methodName, "QUERY: Executed - ");
            KnPersisterTxn.getPersisterTxn().save();
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred - ", e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    /**
     * This method retrieves all MDN's mapped to requested MCPTTID and checks if any of this subscriber is member of group.
     *
     * @param existingMemList List of existing group member's.
     * @param mcpttId         The MCPTTID to retrieve all subscribers mapped with it.
     * @param persisterTxn    The transaction object for persistence operations.
     * @return true if the MDN is authorized, null otherwise.
     */
    public Boolean isMdnAuthorized(List<String> existingMemList, String mcpttId, KnPersisterTxn persisterTxn) {
        String methodName = "isMdnAuthorized(List<List>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mcpttId -", mcpttId);
        Boolean isMdnAuthorized = null;
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            Map<String, Map<Integer, String>> mcpttIdMDNMap = xdmDAO.selectProfileIdMDNsByMcpttIds(Collections.singletonList(mcpttId), persisterTxn);
            knLogger.info(methodName, "mcpttId -", mcpttId, " mcpttIdMDNMap obtained from the DB is-->", mcpttIdMDNMap);
            if (null != mcpttIdMDNMap && !mcpttIdMDNMap.isEmpty()) {
                List<String> mdnList = new ArrayList<>(mcpttIdMDNMap.get(mcpttId).values());
                if (!Collections.disjoint(mdnList, existingMemList)) {
                    isMdnAuthorized = Boolean.TRUE;
                }
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Expected Exception occurred - ", e);
        }
        knLogger.info(methodName, "Is mdn authorized :", isMdnAuthorized);
        return isMdnAuthorized;
    }

    public Map<String, Integer> getDirectoryEtag(Set<String> mdnList) /*throws KnDAOException*/ {
        String methodName = "getAndUpdateDirectoryEtag(persisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdnList -", mdnList.size());
        Map<String, Integer>  directoryEtagMap = new HashMap<>();
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            directoryEtagMap = xdmServerDAO.getDirectoryEtag(mdnList);

        } catch (Exception e) {
            //throw KnDbUtil.processException(e, "Failed while fetching and updating the Subscriber directory etag " + e,
            //pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, selectQuery );
        }
        return directoryEtagMap;
    }
    public String getDefaultPTTSettingDocValue(int corpId, String hierarchyId,String subsPttSettingDocId,KnPersisterTxn persisterTxn ){
        String methodName="getDefaultPTTSettingDocValue()";
        String pttSettingDocId = null;
        // Determine PTT Setting Doc ID with proper fallback logic
        int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        try {
            String xdmPttServerId = retrieveLocalXDMPttServerId();
            String systemDefaultPttSettingDoc = retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(com.kodiak.xdms.server.common.resources.KnConstants.SYSTEM_DEFAULT_PTTSETTINGDOCID);
            knLogger.debug(methodName, "Subs addl - ptt setting docId:", subsPttSettingDocId, ", system default -", systemDefaultPttSettingDoc);
            // Priority: Subscriber's assigned doc > Corp default doc > System default doc

            if (subsPttSettingDocId != null && !subsPttSettingDocId.isEmpty()) {
                // Use subscriber's assigned PTT setting doc
                pttSettingDocId = subsPttSettingDocId;
                knLogger.debug(methodName, "Using subscriber's assigned PTT setting doc:", pttSettingDocId);
            } else {

                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                Set<KnPTTSettingDocInfoDTO> pttSettingDocIds = xdmServerDAO.getAllPTTSettingDocList(corpId, hierarchyId, persisterTxn);
                // Fallback to corp-level default
                //Set<KnPTTSettingDocInfoDTO> pttSettingDocIds = pttSettingsUtil.getAllPTTSettingDocList(corpId, null, xdmPttServerId, persisterTxn);

                if (pttSettingDocIds != null && !pttSettingDocIds.isEmpty()) {
                    pttSettingDocId = pttSettingDocIds.stream()
                            .filter(doc -> doc.getIsDefault() == 1)
                            .map(KnPTTSettingDocInfoDTO::getDocId)
                            .filter(id -> id != null && !id.isBlank())
                            .findFirst()
                            .orElse(systemDefaultPttSettingDoc);
                    knLogger.debug(methodName, "Using corp-level default PTT setting doc:", pttSettingDocId);
                } else {
                    // Fallback to system default
                    pttSettingDocId = systemDefaultPttSettingDoc;
                    knLogger.debug(methodName, "No corp-level default found, using system default PTT setting doc:", pttSettingDocId);
                }
            }
        }catch (Exception e){
            knLogger.error(methodName, "Error in getDefaultPTTSettingDocValue",e);
        }
        return pttSettingDocId;
    }

    public String getEffectivePocHome(String corpPocHome, String groupPocHome) {
        if (corpPocHome == null || corpPocHome.trim().isEmpty() || INVALID_POC_HOME.equals(corpPocHome.trim())) {
            knLogger.debug("getEffectivePocHome", "Corp POC Home is null/empty/0, using group-level POCHOME: ", groupPocHome);
            return groupPocHome;
        }
        return corpPocHome;
    }
}


