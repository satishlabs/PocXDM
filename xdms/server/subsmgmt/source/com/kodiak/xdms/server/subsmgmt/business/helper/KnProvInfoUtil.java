/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnProvInfoUtil.java
 * Subsystem:   Provisioning Library
 * <p>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       12/31/10      7.0
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
package com.kodiak.xdms.server.subsmgmt.business.helper;

import com.kodiak.common.commdto.common.KnExtGatewayInfoDTO;
import com.kodiak.common.commdto.common.KnXDMCorpUserDetailsRespDTO;
import com.kodiak.common.commdto.request.KnXDMSubsAliasDetailsReqDTO;
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.dao.*;
import com.kodiak.common.ggcache.dto.KnOidcTmpPwdDTO;
import com.kodiak.common.resources.*;
import com.kodiak.utilities.featureset.KnFeatureSetException;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.syncgateway.KnManageSyncUserProfileUtil;
import com.kodiak.xdms.server.common.business.helper.KnProfileInfoUtil;
import com.kodiak.xdms.server.common.configuration.KnConfigurationException;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.common.exception.KnException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManager;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.subsmgmt.dto.common.*;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnBulkNNISubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnPAMAccPersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsAddlInfoPersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsAddlTGInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvCacheKeys;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.FEATURE_SET.*;
import static com.kodiak.common.resources.KnGeneralUtil.convertBitSetToHexString;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.WEBDISPATCHER;
import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.common.resources.KnConstants.DELIM;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.SecureRandom;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class KnProvInfoUtil implements Observer {
    private static final KnLogger knLogger = KnLogger.getLogger(KnProvInfoUtil.class);
    private String moduleName = "provInfoUtil";
    private Map<String, List<String>> tableInfo = new HashMap<>();
    private final KnGenInfoUtil genInfoUtil;
    private KnGeneralUtil generalUtil;
    private Map<String, String> tableMap;
    private KnFeatureSetUtil featureSetUtil;
    public static final String MCDATAUSERPROFILE_AUID = "org.3gpp.mcdata.user-profile";
    public static final String MCPTT_UE_PROFILE_AUID = "org.3gpp.mcptt.user-profile";
    public static final String MCVIDEOUSERPROFILE_AUID = "org.3gpp.mcvideo.user-profile";
    private static Map<String,String> auidMap=new HashMap<>();
    private KnProfileInfoUtil profileHandler;
    private static final String DISPATCHER_GROUP = "1";
    private static final String STD_GROUP = "0";
    private static final String BCG_GROUP = "2";
    private static final int SINGLE_ZONE = 1;

    public KnProvInfoUtil() {
        knLogger.info("Constructor", "in subs prov util1");
        genInfoUtil = KnGenInfoUtil.getInstance();
        generalUtil = new KnGeneralUtil();
        tableMap = new HashMap<String, String>();
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.PTTSERVERIPINFO.value(), KnProvCacheKeys.PTT_SERVER_INFO);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SUBSCRPARTITIONINGCONFIG.value(), KnProvCacheKeys.PARTITION_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.PRESENCESERVICECONFIG.value(), KnProvCacheKeys.PR_IN_POC_ENABLED);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SUBS_POCSERVERMAP.value(), KnProvCacheKeys.POC_SERVER_MAP);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SUBS_PRSERVERMAP.value(), KnProvCacheKeys.PRESENCE_SERVER_MAP);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.ROAMINGCLUSTERINFO.value(), KnProvCacheKeys.SUPPORTED_ROAMING_LIST);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.POC_SVC_CONFIG.value(), KnProvCacheKeys.POC_SVC_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.POCREGISTRARSRVCCONFIG.value(), KnProvCacheKeys.POC_REGISTRAR_SVC_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SIPPROXYSVCCONFIG.value(), KnProvCacheKeys.SIP_PROXY_SVC_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.FEATUREACCESSNUMBERINFO.value(), KnProvCacheKeys.FEATURE_ACCESS_NUMBER_INFO);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.FEATUREACCESSINFO.value(), KnProvCacheKeys.FEATURE_ACCESS_INFO);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SWPKGCONFIGPARAMVALUE.value(), KnProvCacheKeys.SW_PKG_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.DIALPLANINFO.value(), KnProvCacheKeys.DIAL_PLAN_INFO);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.XDMS_DOCSUBPRX_CONFIG.value(), KnProvCacheKeys.XDMS_DOC_SUB_PRX_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.INSTAPOC_SRVC_CFG.value(), KnProvCacheKeys.INSTA_POC_SVC_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.LOCATIONSERVICECONFIG.value(), KnProvCacheKeys.LOC_SVC_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SIGNALINGCARDADDLINFO.value(), KnProvCacheKeys.SIGNALING_CARD_INFO);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.PAM_SVC_CONFIG.value(), KnProvCacheKeys.PAM_SVC_CONFIG);
        tableMap.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.ROAMINGMCCMNCINFO.value(), KnProvCacheKeys.ROAMINGMCCMNCINFO);


        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.PTTSERVERIPINFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SUBSCRPARTITIONINGCONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.PRESENCESERVICECONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SUBS_POCSERVERMAP.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SUBS_PRSERVERMAP.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.ROAMINGCLUSTERINFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.POC_SVC_CONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.POCREGISTRARSRVCCONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SIPPROXYSVCCONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.FEATUREACCESSNUMBERINFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.FEATUREACCESSINFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SWPKGCONFIGPARAMVALUE.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.DIALPLANINFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.XDMS_DOCSUBPRX_CONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.INSTAPOC_SRVC_CFG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.LOCATIONSERVICECONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.SIGNALINGCARDADDLINFO.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.PAM_SVC_CONFIG.value(), null);
        tableInfo.put(com.kodiak.common.resources.KnConstants.CACHE_TABLE_LIST.ROAMINGMCCMNCINFO.value(), null);
        featureSetUtil = KnFeatureSetUtil.getInstance();


        knLogger.info("KnProvInfoUtil", "Maps initialized ");

        //auid list
        auidMap.put("mcvideo",MCVIDEOUSERPROFILE_AUID);
        auidMap.put("mcdata",MCDATAUSERPROFILE_AUID);
        auidMap.put("mcptt",MCPTT_UE_PROFILE_AUID);

        profileHandler = KnProfileInfoUtil.getInstance(KnProfileTypes.PUBLIC_PROFILE);
    }


    /**
     * method to retrieve the Subscribers PoC Home based on the Paritioning logic
     *
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    public String getSubsPoCHome(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getSubsPoCHome(String, boolean , KnPersisterTxn)";
        knLogger.debug(methodName, "get POC Home for mdn ", KnGDPRTemplate.mdn(mdn));
        String pocHome = null;
        //get the Type of partitioning.
        try {
            KnSubsPartitionConfigDTO partitionConfigDTO = getPartitionConfig(persisterTxn);
            // check if the load based partitioning is enabled or not.
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            if (partitionConfigDTO.getMdnPartitionTypePOC() == KnProvConstants.PARTITION_TYPE.LOAD_BASED.value()) {
                //get the list of POC Ptt Server Ids
                KnConfigurationsManager confManager = KnConfigurationsManager.getInstance();
                ICacheManager cacheManager = confManager.getCacheManager();
                Map<String, List<String>> pttServerInfo = (Map<String, List<String>>) cacheManager.get(KnProvCacheKeys.PTT_SERVER_INFO);
                knLogger.debug(methodName, "PTTSERVER INFO is", pttServerInfo);
                if (pttServerInfo == null) {
                    knLogger.info(methodName, "Retrieving Pttserver info from DB");
                    pttServerInfo = xdmServerDAO.retrievePttServerIds(persisterTxn);
                    cacheManager.put(KnProvCacheKeys.PTT_SERVER_INFO, pttServerInfo);
                }
                //check if the Prov is allowed for the POCs and add only those into the list whose POC is in ALLOW_PROV mode
                knLogger.debug(methodName, "PTTSERVER INFO ", pttServerInfo);
                List<String> pocPttIdList = pttServerInfo.get(KnProvConstants.POC_PTT_ID_KEY);
                List<String> pocPttIds = new ArrayList<String>();
                pocPttIds.addAll(pocPttIdList);
                knLogger.debug(methodName, "POC PTT IDS ", pocPttIds);
                Map<String, KnPocSubsCapConfigDTO> pocSubsCapConfigMap =
                        getPocSubsCapacityConfig(pocPttIds, persisterTxn);
                for (Map.Entry<String, KnPocSubsCapConfigDTO> entrySet : pocSubsCapConfigMap.entrySet()) {
                    String pttServerId = entrySet.getKey();
                    KnPocSubsCapConfigDTO pocSubsCapConfigDTO = entrySet.getValue();
                    if (pocSubsCapConfigDTO.getAllowProv() != KnProvConstants.ALLOW_POC_PROV.ALLOW_PROV.value()) {
                        knLogger.debug(methodName, "not ALLOW_PROV ", pttServerId);
                        pocPttIds.remove(pttServerId);
                    }
                }
                knLogger.debug(methodName, "POC PTT IDS after subs cap config ", pocPttIds);

                if (pocPttIds.isEmpty()) {
                    throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                            "MAX limit for all the POC Home has been reached");
                } else {
                    // get the subscriber count.
                    Map<String, Integer> subsCntPerServer = xdmServerDAO.getSubsCount(pocPttIds, persisterTxn);
                    knLogger.debug(methodName, "Get server home based on Loading factor");
                    pocHome = calcLoadingFactor(subsCntPerServer, pocSubsCapConfigMap);

                }
            } else {
                // mdn based partitioning
                pocHome = getSubscriberPoCHome(mdn, persisterTxn);
                List<String> pocHomeList = new ArrayList<String>();
                pocHomeList.add(pocHome);
                Map<String, Integer> subsCntPerServer = xdmServerDAO.getSubsCount(pocHomeList, persisterTxn);
                Map<String, KnPocSubsCapConfigDTO> pocSubsCapConfigMap =
                        getPocSubsCapacityConfig(pocHomeList, persisterTxn);
                if ((pocSubsCapConfigMap.get((pocHome)) == null) || pocSubsCapConfigMap.get((pocHome)).getMaxSubsLimit() <= (subsCntPerServer.get(pocHome)) ||
                        pocSubsCapConfigMap.get(pocHome).getAllowProv() != KnProvConstants.ALLOW_POC_PROV.ALLOW_PROV.value()) {
                    knLogger.error(methodName, "MAX limit for the POC Home has been reached");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                            "MAX limit for the POC Home has been reached");
                }
            }

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.POC_SERVER_MAP_NOT_FOUND, "POC Server info not found", e);
            }
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "Prov BO Exception ", e.getMessage());
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get ptt Server info", e);
        }
        knLogger.debug(methodName, "EXIT: Poc Home for the mdn is ", pocHome);
        return pocHome;
    }

    public Map<String, String> getSubsPoCHome(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getSubsPoCHome(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: get POC Home for ", mdnList != null ? mdnList.size() : 0, " MDNs");

        Map<String, String> mdnToPocHomeMap = new LinkedHashMap<>();
        if (mdnList == null || mdnList.isEmpty()) {
            return mdnToPocHomeMap;
        }

        try {
            KnSubsPartitionConfigDTO partitionConfigDTO = getPartitionConfig(persisterTxn);
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            if (partitionConfigDTO.getMdnPartitionTypePOC() == KnProvConstants.PARTITION_TYPE.LOAD_BASED.value()) {
                // LOAD_BASED: resolve pocHome once and assign the same value to all MDNs
                KnConfigurationsManager confManager = KnConfigurationsManager.getInstance();
                ICacheManager cacheManager = confManager.getCacheManager();
                Map<String, List<String>> pttServerInfo = (Map<String, List<String>>) cacheManager.get(KnProvCacheKeys.PTT_SERVER_INFO);
                if (pttServerInfo == null) {
                    knLogger.info(methodName, "Retrieving Pttserver info from DB");
                    pttServerInfo = xdmServerDAO.retrievePttServerIds(persisterTxn);
                    cacheManager.put(KnProvCacheKeys.PTT_SERVER_INFO, pttServerInfo);
                }
                List<String> pocPttIds = new ArrayList<>(pttServerInfo.get(KnProvConstants.POC_PTT_ID_KEY));
                Map<String, KnPocSubsCapConfigDTO> pocSubsCapConfigMap = getPocSubsCapacityConfig(pocPttIds, persisterTxn);
                Iterator<String> iterator = pocPttIds.iterator();
                while (iterator.hasNext()) {
                    String pttServerId = iterator.next();
                    KnPocSubsCapConfigDTO capConfig = pocSubsCapConfigMap.get(pttServerId);
                    if (capConfig == null || capConfig.getAllowProv() != KnProvConstants.ALLOW_POC_PROV.ALLOW_PROV.value()) {
                        knLogger.debug(methodName, "not ALLOW_PROV ", pttServerId);
                        iterator.remove();
                    }
                }
                if (pocPttIds.isEmpty()) {
                    throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                            "MAX limit for all the POC Home has been reached");
                }
                Map<String, Integer> subsCntPerServer = xdmServerDAO.getSubsCount(pocPttIds, persisterTxn);
                String pocHome = calcLoadingFactor(subsCntPerServer, pocSubsCapConfigMap);
                knLogger.info(methodName, "LOAD_BASED pocHome resolved: ", pocHome);
                for (String mdn : mdnList) {
                    mdnToPocHomeMap.put(mdn, pocHome);
                }
            } else {
                // MDN_BASED: use prefix trie from cache/DB to map each MDN to its pocHome
                KnConfigurationsManager confManager = KnConfigurationsManager.getInstance();
                ICacheManager cacheManager = confManager.getCacheManager();
                KnTriePrefixUtility<String> pocServerMap = (KnTriePrefixUtility<String>) cacheManager.get(KnProvCacheKeys.POC_SERVER_MAP);
                if (pocServerMap == null) {
                    pocServerMap = xdmServerDAO.retrieveSubsPoCServerMap(persisterTxn);
                    cacheManager.put(KnProvCacheKeys.POC_SERVER_MAP, pocServerMap);
                }

                // Resolve pocHome per MDN and collect unique pocHomes for capacity validation
                Set<String> uniquePocHomes = new LinkedHashSet<>();
                Map<String, String> tempMdnPocMap = new LinkedHashMap<>();
                for (String mdn : mdnList) {
                    if (pocServerMap.hasPrefix(mdn)) {
                        String pocHome = pocServerMap.get(mdn);
                        tempMdnPocMap.put(mdn, pocHome);
                        uniquePocHomes.add(pocHome);
                    } else {
                        knLogger.warn(methodName, "POC Server Map not found for mdn: ", KnGDPRTemplate.mdn(mdn));
                    }
                }

                // Validate capacity for all unique pocHomes in a single batch
                List<String> pocHomeList = new ArrayList<>(uniquePocHomes);
                Map<String, Integer> subsCntPerServer = xdmServerDAO.getSubsCount(pocHomeList, persisterTxn);
                Map<String, KnPocSubsCapConfigDTO> pocSubsCapConfigMap = getPocSubsCapacityConfig(pocHomeList, persisterTxn);

                for (Map.Entry<String, String> entry : tempMdnPocMap.entrySet()) {
                    String mdn = entry.getKey();
                    String pocHome = entry.getValue();
                    KnPocSubsCapConfigDTO capConfig = pocSubsCapConfigMap.get(pocHome);
                    Integer subsCount = subsCntPerServer.get(pocHome);
                    if (capConfig == null || subsCount == null
                            || capConfig.getMaxSubsLimit() <= subsCount
                            || capConfig.getAllowProv() != KnProvConstants.ALLOW_POC_PROV.ALLOW_PROV.value()) {
                        knLogger.warn(methodName, "Capacity limit reached or prov not allowed for pocHome: ", pocHome,
                                ", mdn: ", KnGDPRTemplate.mdn(mdn));
                    } else {
                        mdnToPocHomeMap.put(mdn, pocHome);
                    }
                }
            }

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.POC_SERVER_MAP_NOT_FOUND, "POC Server info not found", e);
            }
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "Prov BO Exception ", e.getMessage());
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get ptt Server info in bulk", e);
        }

        knLogger.debug(methodName, "EXIT: Resolved POC homes for ", mdnToPocHomeMap.size(), " out of ", mdnList.size(), " MDNs");
        return mdnToPocHomeMap;
    }


    /**
     * method to retrieve POC home server for subscribers based on cluster ID and subscriber count.
     *
     * @param clusterId the cluster identifier to retrieve POC servers from
     * @param subscriberCount the number of subscribers to be provisioned
     * @param persisterTxn the database transaction context for persistence operations
     * @return the POC home server ID that can accommodate the subscribers
     * @throws KnProvBOException
     */
    public String getSubsPoCHomeByClusterID(int clusterId, int subscriberCount, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getSubsPoCHomeByClusterID(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "get POC Home by cluster id ", clusterId);
        String pocHome = null;
        try {
            if (subscriberCount <= 0){
                throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_REQUEST_DATA,
                        "Subscriber count must be greater than zero");
            }
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            //get the list of POC Ptt Server Ids for the particular cluster ID
            Map<String, List<String>> pttServerInfo = xdmServerDAO.retrievePttServerIdsByClusterId(clusterId, persisterTxn);
            knLogger.debug(methodName, "PTTSERVER INFO is", pttServerInfo);
            List<String> pocPttIds = pttServerInfo.get(KnProvConstants.POC_PTT_ID_KEY);;
            if (pocPttIds.isEmpty()) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                        "MAX limit for all the POC Home has been reached");
            }

            Map<String, KnPocSubsCapConfigDTO> pocSubsCapConfigMap =
                    getPocSubsCapacityConfig(pocPttIds, persisterTxn);

            //check if the Prov is allowed for the POCs and add only those into the list whose POC is in ALLOW_PROV mode
            Iterator<String> iterator = pocPttIds.iterator();
            while (iterator.hasNext()) {
                String pttServerId = iterator.next();
                KnPocSubsCapConfigDTO pttServerConfig = pocSubsCapConfigMap.get(pttServerId);
                // Remove if (Config is Missing) OR (Provisioning is NOT Allowed)
                if (pttServerConfig == null ) {
                    knLogger.warn(methodName, "Removing server: Configuration missing for pttserver with ID ", pttServerId);
                    iterator.remove();
                } else if (pttServerConfig.getAllowProv() != KnProvConstants.ALLOW_POC_PROV.ALLOW_PROV.value()) {
                    knLogger.debug(methodName, "Removing server: Provisioning disabled for pttserver with ID ", pttServerId);
                    iterator.remove();
                }
            }
            knLogger.debug(methodName, "POC PTT IDS after subs cap config ", pocPttIds);

            if (pocPttIds.isEmpty()) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                        "MAX limit for all the POC Home has been reached");
            } else {
                // get the subscriber count.
                Map<String, Integer> subsCntPerServer = xdmServerDAO.getSubsCount(pocPttIds, persisterTxn);
                knLogger.debug(methodName, "Get server home based on Loading factor");
                pocHome = calcLoadingFactor(subsCntPerServer, pocSubsCapConfigMap);
                if (!hasCapacityForSubscribers(pocHome, subscriberCount, subsCntPerServer, pocSubsCapConfigMap)) {
                    throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                            "MAX limit for the POC Home has been reached");
                }
            }
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.POC_SERVER_MAP_NOT_FOUND, "POC Server info not found", e);
            }
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "Prov BO Exception ", e.getMessage());
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get ptt Server info", e);
        }
        knLogger.debug(methodName, "EXIT: Poc Home for the mdn is ", pocHome);
        return pocHome;
    }

    /**
     * method checks if the POC home server has sufficient capacity for the specified number of subscribers.
     *
     * @param pocHome the POC home server identifier
     * @param subscriberCount the number of subscribers to be added
     * @param subsCntPerServer map of current subscriber counts per server
     * @param pocSubsCapConfigMap map of POC subscriber capacity configurations
     * @return true if the server has capacity, false otherwise
     */
    private boolean hasCapacityForSubscribers(String pocHome, int subscriberCount, Map<String, Integer> subsCntPerServer, Map<String, KnPocSubsCapConfigDTO> pocSubsCapConfigMap) {
        Integer currentSubsCount = subsCntPerServer.get(pocHome);
        Integer maxServerCapacity = pocSubsCapConfigMap.get(pocHome).getMaxSubsLimit();
        if(currentSubsCount != null && maxServerCapacity != null && (currentSubsCount + subscriberCount) <= maxServerCapacity){
            return true;
        }
        return false;
    }

    /**
     * method to calculate the loading Factor
     *
     * @param subsCntPerServer
     * @param pocSubsCapConfigMap
     * @return
     */
    private String calcLoadingFactor(Map<String, Integer> subsCntPerServer, Map<String, KnPocSubsCapConfigDTO> pocSubsCapConfigMap) throws KnProvBOException {
        String methodName = "calcLoadingFactor(Map<String, Integer>, Map<String, KnPocSubsCapConfigDTO>)";
        // find the loading factor
        String maxCapacityServer = null;
        int maxAvailableCapacity = 0;
        for (Map.Entry<String, Integer> set : subsCntPerServer.entrySet()) {
            String serverId = set.getKey();
            int subsCount = set.getValue();
            if(pocSubsCapConfigMap.containsKey(serverId)){
                int currentAvailableCapacity = pocSubsCapConfigMap.get(serverId).getMaxSubsLimit() - subsCount;
                if(currentAvailableCapacity > maxAvailableCapacity){
                    maxAvailableCapacity = currentAvailableCapacity;
                    maxCapacityServer = serverId;
                }
            }
        }

        knLogger.debug(methodName, " Max available capacity ", maxAvailableCapacity);

        if (maxAvailableCapacity <= 0) {
            knLogger.error(methodName, "Max limit reached for all PoC servers");
            throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                    "MAX limit for all POC Homes has been reached");
        }

        knLogger.debug(methodName, " Server Home ", maxCapacityServer);
        knLogger.info(methodName, "EXIT: Server Home after calc loading factor ", maxCapacityServer);
        return maxCapacityServer;
    }

    public boolean isPocHomeValidForAllocation(String pttServerId, int subscriberCount, KnPersisterTxn persisterTxn) throws KnException {
        String methodName = "isPocHomeValidForAllocation(String, int, KnPersisterTxn)";

        IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
        List<String> targetServerList = Collections.singletonList(pttServerId);

        // 1. Fetch Capacity Config & Runtime Provisioning status
        Map<String, KnPocSubsCapConfigDTO> pocSubsCapConfigMap = xdmServerDAO.getPocSubsCapacityConfig(targetServerList, persisterTxn);
        KnPocSubsCapConfigDTO pttServerConfig = pocSubsCapConfigMap.get(pttServerId);

        if (pttServerConfig == null) {
            knLogger.warn(methodName, "Validation failed: Configuration missing for pttServer ID: ", pttServerId);
            return false;
        }

        if (pttServerConfig.getAllowProv() != KnProvConstants.ALLOW_POC_PROV.ALLOW_PROV.value()) {
            knLogger.info(methodName, "Validation failed: Runtime provisioning disabled for pttServer ID: ", pttServerId);
            return false;
        }

        // 2. Fetch Active Real-Time Subscriber Allocations
        Map<String, Integer> subsCntPerServer = xdmServerDAO.getSubsCount(targetServerList, persisterTxn);
        if(subsCntPerServer == null) {
            knLogger.warn(methodName, "Validation failed: Unable to retrieve subscriber count for pttServer ID: ", pttServerId);
            return false;
        }
        int currentSubsCount = subsCntPerServer.getOrDefault(pttServerId, 0);

        int remainingCapacity = pttServerConfig.getMaxSubsLimit() - currentSubsCount;
        if (remainingCapacity < subscriberCount) {
            knLogger.warn(methodName, "Validation failed: Insufficient headroom on {}. Required: {}, Available: {}",
                    pttServerId, subscriberCount, remainingCapacity);
            return false;
        }

        knLogger.info(methodName, "Validation passed: PoC Home "+pttServerId+" is safe for allocation for subs count ", subscriberCount);
        return true;
    }

    /**
     * Method to retrieve the Subscriber Configuration Partition Info
     *
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    public KnSubsPartitionConfigDTO getPartitionConfig(KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getPartitionConfig(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieve partition config ");
        KnSubsPartitionConfigDTO partitionConfigDTO = null;

        try {
            KnConfigurationsManager confManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = confManager.getCacheManager();
            partitionConfigDTO = (KnSubsPartitionConfigDTO) cacheManager.get(KnProvCacheKeys.PARTITION_CONFIG);

            if (partitionConfigDTO == null) {
                IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                partitionConfigDTO = xdmServerDAO.retrievePartitionConfig(persisterTxn);
                cacheManager.put(KnProvCacheKeys.PARTITION_CONFIG, partitionConfigDTO);
            }
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBS_PARTITION_CONFIG_NOT_FOUND, "Subs partition Config not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception ", e.getMessage());
            throw new KnProvBOException(e.getErrorCode(), "failed to retrive subs partitionn config");
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get partition config info", e);
        }

        knLogger.info(methodName, "EXIT : Partition Config  -> ", partitionConfigDTO);
        return partitionConfigDTO;
    }

    /**
     * method to retrieve the flag value from the ENV VARIABLE INFO table
     *
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    public boolean isPrInPocEnabled(KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "isPrInPoCEnabled(KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: check if PR in POC is enabled");
        Boolean isPrInPocEnabled = null;
        try {
            KnConfigurationsManager confManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = confManager.getCacheManager();
            isPrInPocEnabled = (Boolean) cacheManager.get(KnProvCacheKeys.PR_IN_POC_ENABLED);

            if (isPrInPocEnabled == null) {
                IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                isPrInPocEnabled = xdmServerDAO.isPrInPocEnabled(persisterTxn);
                cacheManager.put(KnProvCacheKeys.PR_IN_POC_ENABLED, isPrInPocEnabled);
            }
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception ", e.getMessage());
            throw new KnProvBOException(e.getErrorCode(), "failed to retrive Pr in poc enabled flag");
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get partition config info", e);
        }
        return isPrInPocEnabled;
    }


    /**
     * method to retrieve the flag value from the ENV VARIABLE INFO table
     *
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    public String getPreAssignCorpHome(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getPreAssignCorpHome(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: check if PR in POC is enabled");
        String preAssignCorpHome = null;
        try {
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            preAssignCorpHome = xdmServerDAO.getPreAssignCorpHome(extCorpId, persisterTxn);

        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception ", e.getMessage());
            throw new KnProvBOException(e.getErrorCode(), "failed to get pre assign corp home");
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to pre assign corp home", e);
        }
        return preAssignCorpHome;
    }

    /**
     * method to retrieve the POC Subscriber Capacity Configuration
     *
     * @param pttServerIds list of pttServerId
     * @param persisterTxn
     * @return
     */
    public Map<String, KnPocSubsCapConfigDTO> getPocSubsCapacityConfig(List<String> pttServerIds, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getPocSubsCapacityConfig(String, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: get Poc Subs Capacity Config for pttServer Id ", pttServerIds);
        Map<String, KnPocSubsCapConfigDTO> pocSubsCapConfigMap = null;
        try {
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            pocSubsCapConfigMap = xdmServerDAO.getPocSubsCapacityConfig(pttServerIds, persisterTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception ", e.getMessage());
            throw new KnProvBOException(e.getErrorCode(), "failed to get Poc Subs Capacity Config");
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get Poc Subs Capacity Config", e);
        }
        knLogger.debug(methodName, "Poc Subs Capacity Config for pttServer Id ", pttServerIds, " is ", pocSubsCapConfigMap);
        return pocSubsCapConfigMap;
    }


    /**
     * Retrieve the Subscriber PoC Server Home Id of the Subscriber/Mdn for MDN based Partitioning
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @return String
     * @throws KnProvBOException exception
     */
    public String getSubscriberPoCHome(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getSubscriberPoCHome(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: get subscriber poc home for Mdn - ", KnGDPRTemplate.mdn(mdn));
        String poCPttServerId = null;
        try {
            //need to check in the cache first then go for the DB call
            KnConfigurationsManager confManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = confManager.getCacheManager();
            KnTriePrefixUtility<String> pocServerMap = (KnTriePrefixUtility<String>) cacheManager.get(KnProvCacheKeys.POC_SERVER_MAP);
            if (pocServerMap == null) {
                IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                pocServerMap = xdmServerDAO.retrieveSubsPoCServerMap(persisterTxn);
                cacheManager.put(KnProvCacheKeys.POC_SERVER_MAP, pocServerMap);
            }

            if (pocServerMap.hasPrefix(mdn)) {
                poCPttServerId = pocServerMap.get(mdn);
                knLogger.debug(methodName, "POC Server for mdn - ", poCPttServerId);
            }

            if (poCPttServerId == null) {
                knLogger.debug(methodName, "POC Server Map not found for mdn - ", KnGDPRTemplate.mdn(mdn));
                throw new KnProvBOException(KnErrorCodes.BOEntity.POC_SERVER_MAP_NOT_FOUND,
                        "PoC Server Id not found");
            }


        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.POC_SERVER_MAP_NOT_FOUND, "POC Server info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get poc server info", e);
        }
        knLogger.info(methodName, "EXIT : POC Server Id -> ", poCPttServerId);

        return poCPttServerId;
    }

    /**
     * * Retrieve the Subscriber presence Server Home Id
     *
     * @param mdn
     * @param poCPttServerId
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    public String getSubsPresenceHome(String mdn, String poCPttServerId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getSubsPresenceHome(String,String,KnPersisterTxn)";

        String presencePttServerId = null;
        try {
            if (isPrInPocEnabled(persisterTxn)) {
                if (KnProvConstants.POC_HOME_NOT_ASSIGNED.equals(poCPttServerId)) {
                    knLogger.debug(methodName, "POC Server Id is 0 for mdn - ", KnGDPRTemplate.mdn(mdn));
                    presencePttServerId = KnProvConstants.POC_HOME_NOT_ASSIGNED;
                }else {
                    //check if the poc server contains the Presence.
                    //hence looking for presence config in Poc Server
                    IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                    KnPresenceServiceConfigDTO prSvcConfigDTO = provXDMServerDAO.retrievePresenceServiceConfig(poCPttServerId, persisterTxn);
                    if (prSvcConfigDTO != null && prSvcConfigDTO.getEnablePRInPoC() == KnProvConstants.ENABLE_PR_IN_POC) {
                        presencePttServerId = poCPttServerId;
                    } else {
                        presencePttServerId = getSubscriberPresenceHome(mdn, persisterTxn);
                    }
                }
            } else {
                presencePttServerId = getSubscriberPresenceHome(mdn, persisterTxn);
            }
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.PRESENCE_SERVER_MAP_NOT_FOUND,
                        "Presence Server info not found", e);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed while retrieving Presence server info", e);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        }
        knLogger.info(methodName, "EXIT : Presence Server Id -> ", presencePttServerId);
        return presencePttServerId;

    }

    /**
     * Retrieve the Subscriber Presence Server Home Id of the Subscriber/Mdn
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @return String
     * @throws KnProvBOException exception
     */
    private String getSubscriberPresenceHome(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getSubscriberPresenceHome(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: get Subscriber presence home for Mdn - ", KnGDPRTemplate.mdn(mdn));
        String presencePttServerId = null;
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            KnTriePrefixUtility<String> presenceServerMap = (KnTriePrefixUtility<String>) cacheManager.get(KnProvCacheKeys.PRESENCE_SERVER_MAP);

            if (presenceServerMap == null) {
                IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                presenceServerMap = xdmServerDAO.retrieveSubsPresenceServerMap(persisterTxn);
                cacheManager.put(KnProvCacheKeys.PRESENCE_SERVER_MAP, presenceServerMap);
            }

            if (presenceServerMap.hasPrefix(mdn)) {
                presencePttServerId = presenceServerMap.get(mdn);
                knLogger.debug(methodName, "Presence Server for mdn - ", presencePttServerId);
            }

            if (presencePttServerId == null) {
                knLogger.debug(methodName, "presence Server Map not found for mdn - ", KnGDPRTemplate.mdn(mdn));
                throw new KnProvBOException(KnErrorCodes.BOEntity.PRESENCE_SERVER_MAP_NOT_FOUND,
                        "Presence Server Id not found");
            }
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.PRESENCE_SERVER_MAP_NOT_FOUND,
                        "Presence Server info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get Presence Server Info", e);
        }
        knLogger.info(methodName, "EXIT : Presence Server Id -> ", presencePttServerId);

        return presencePttServerId;
    }


    /**
     * @param extCorpId
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    public KnOPCorpProfileInfoDTO retrieveCorpProfile(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrieveCorpProfile(String, extCorpId)";
        knLogger.debug(methodName, "ENTRY: retrieve Corp Profile for Ext Corp id - ", extCorpId);
        KnOPCorpProfileInfoDTO corpProfileRespDTO = null;
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            corpProfileRespDTO = provXDMServerDAO.retrieveCorporateProfile(extCorpId, persisterTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
            knLogger.error(methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed while retrieving corp Id", e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed while retrieving corp Id", e);
        }
        knLogger.info(methodName, "EXIT : Corporation Profile Info -> ", corpProfileRespDTO);
        return corpProfileRespDTO;
    }

    /**
     * Method to retrieve the Corporation Subscriber Count
     *
     * @param corpId       int
     * @param persisterTxn KnPersisterTxn
     * @return corp Subscriber Count of the corporation Id
     * @throws KnProvBOException Exception
     */
    public int retrieveCorpSubsCount(int corpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrieveCorpSubsCount(int)";
        int corpSubsCount = -1;
        knLogger.debug(methodName, "ENTRY : get Corporation Subs Count for corp id - ", corpId);
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            corpSubsCount = provXDMServerDAO.retrieveCorpSubscriberCount(corpId, persisterTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
            knLogger.error(methodName, e);

            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed while retrieving corp Subs Count", e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed while retrieving corp Subs Count", e);
        }
        knLogger.debug(methodName, "EXIT : Corporation Subs Count for corp id [", corpId, "]-> ", corpSubsCount);

        return corpSubsCount;
    }

    /**
     * method to retrieve the XDMS Service Config Info
     *
     * @return KnXDMSServiceConfigDTO
     * @throws KnProvBOException BO Entity Exception
     */
    public KnXDMSServiceConfigDTO retrieveXdmsSvcConfigInfo() throws KnProvBOException {
        String methodName = "retrieveXdmsSvcConfigInfo()";
        KnXDMSServiceConfigDTO xdmsServiceConfigDTO = null;
        knLogger.debug(methodName, "ENTRY: retrieve XDMS SVC Config - ");
        try {
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, null);

        } catch (KnBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw new KnProvBOException(e.getErrorCode(), "Failed while retrieving XDMS SVC Config");
        }

        return xdmsServiceConfigDTO;
    }

    /**
     * Method to retrieve the Supported/Configured Roaming Lists
     *
     * @return Map<Integer, String> Map of (ClusterId, ClusterNames)
     * @throws KnProvBOException Exception
     */
    public Map<Integer, String> retrieveSupportedRoamingList() throws KnProvBOException {
        String methodName = "retrieveSupportedRoamingList()";
        Map<Integer, String> roamingListMap = null;
        knLogger.debug(methodName, "ENTRY: Retrieve supported roaming list ");

        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            roamingListMap = (Map<Integer, String>) cacheManager.get(KnProvCacheKeys.SUPPORTED_ROAMING_LIST);
            knLogger.debug(methodName, "Roaming list in Cache - ", roamingListMap);

            if (roamingListMap == null || roamingListMap.isEmpty()) {
                knLogger.debug(methodName, "Roaming list not found in cache. Reading from DB");
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                roamingListMap = provXDMServerDAO.retrieveRoamingClusterId(null);
                cacheManager.put(KnProvCacheKeys.SUPPORTED_ROAMING_LIST, roamingListMap);
            }

        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.ROAMING_CLUSTER_NOT_FOUND, "Roaming List info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
            knLogger.error(methodName, e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get Roaming List Info", e);
        }
        knLogger.debug(methodName, "EXIT : Retrieve supported roaming List - ", roamingListMap);

        return roamingListMap;
    }

    /**
     * method to retrieve the Subscriber Info (data from the PoCSubscrInfo Table)
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnProvBOException BO entity exception
     */
    public KnOPSubsProfileInfoDTO retrieveSubscriberInfo(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrieveSubscriberProfile(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO respDTO = null;
        knLogger.debug(methodName, "ENTRY: retrieve subscriber profile info - ", KnGDPRTemplate.mdn(mdn));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            respDTO = provXDMServerDAO.retrieveSubscriberInfo(mdn, persisterTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Subscriber Profile info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get subscriber Info", e);
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", respDTO);


        return respDTO;
    }

    public Map<String, com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO> retrieveSubscriberInfo(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrieveSubscriberProfile(String, KnPersisterTxn)";
        Map<String, com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO> resMap = new HashMap<>();
        knLogger.debug(methodName, "ENTRY: retrieve subscriber profile info - ", KnGDPRTemplate.mdnList(mdnList));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            resMap = provXDMServerDAO.retrieveSubscriberInfo(mdnList, persisterTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Subscriber Profile info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get subscriber Info", e);
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", resMap);


        return resMap;
    }

    public KnOPSubsProfileInfoDTO retrieveBaseMdnByMcpttId(String mcpttId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrieveBaseMdnByMcpttId(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO respDTO = null;
        knLogger.debug(methodName, "ENTRY: retrieve subscriber profile info mcpttId - ", KnGDPRTemplate.mcpttId(mcpttId));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            respDTO = provXDMServerDAO.retrieveBaseMdnByMcpttId(mcpttId, persisterTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Subscriber Profile info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get subscriber Info", e);
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", respDTO);


        return respDTO;
    }

    /**
     * method to retrieve the Subscriber Info (data from the PoCSubscrInfo Table)
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnProvBOException BO entity exception
     */
    public KnOPSubsAddlInfoProfileDTO retrieveSubscrAddlInfo(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrieveSubscrAddlInfo(String, KnPersisterTxn)";
        KnOPSubsAddlInfoProfileDTO respDTO = null;
        knLogger.debug(methodName, "ENTRY: retrieve subscriber Addlinfo profile info - ", KnGDPRTemplate.mdn(mdn));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            respDTO = provXDMServerDAO.retrieveSubscrAddlInfo(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception  Occurred - ", e);
            throw new KnProvBOException(e.getErrorCode(), "Operation failed : ", e);
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber AddlInfo Profile Info -> ", respDTO);


        return respDTO;
    }

    /**
     * method to retrieve the Subscriber Roaming profile info
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @return Collection list of existing subs roaming profile
     * @throws KnProvBOException BO layer exception
     */
    public List<Integer> retrieveSubscrRoamingProfile(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrieveSubscriberProfile(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Retrieving Subscriber Roaming profile for MDN - ", KnGDPRTemplate.mdn(mdn));
        List<Integer> roamingTypes = null;
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            roamingTypes = provXDMServerDAO.retrieveSubscrRoamingProfile(mdn, persisterTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_ROAMING_INFO_NOT_FOUND,
                        "Subscriber Roaming info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
            knLogger.error(methodName, e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get Roaming Profile Info", e);
        }
        knLogger.info(methodName, "EXIT : Subscriber Roaming Profile Info for mdn [", KnGDPRTemplate.mdn(mdn), "]", roamingTypes);

        return roamingTypes;
    }

    public String getMd5HashfromString(String text)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] md5hash;
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }

    public String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (byte aData : data) {
            int halfbyte = (aData >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = aData & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * Method to generate the HA1 password
     *
     * @param userId   String mdn
     * @param realm    String realm name
     * @param password String password
     * @return String ha1password
     * @throws KnProvBOException Exception
     */
    public String generateHA1(String userId, String realm, String password) throws KnProvBOException {
        String methodName = "generateHA1(String, String, String)";
        knLogger.info(methodName, "ENTRY: generate HA1 of Digest Auth with data ",
                ", user_Id - ", KnGDPRTemplate.userId(userId), ", realm - ", realm, ", password - ", password);
        String ha1Password = null;
        try {
            String ha1str = userId + ":" + realm + ":" + password;
            ha1Password = getMd5HashfromString(ha1str);
        } catch (NoSuchAlgorithmException e) {
            knLogger.error(methodName, "Failed to generate HA1 Password");
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to generate HA1 Password", e);
        } catch (UnsupportedEncodingException e) {
            knLogger.error(methodName, "Failed to generate HA1 Password");
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to generate HA1 Password", e);
        }
        knLogger.debug(methodName, "HA1 generated - ", ha1Password);
        return ha1Password;
    }


    /**
     * method to generate the XUI
     *
     * @param mdn String
     * @return String XUI
     */
    public String generateXUI(String mdn) {
        String methodName = "generateXUI";
        knLogger.debug(methodName, "generating XUI for mdn - ", KnGDPRTemplate.mdn(mdn));
        String xui = KnProvConstants.TEL_URI_TEMPLATE + mdn;
        knLogger.debug(methodName, "XUI for the MDN - ",  KnGDPRTemplate.mdn(xui));
        return xui;
    }

    /**
     * method to generate the Subscriber config doc Sel Uri as per ICD
     *
     * @param mdn String
     * @return String sel uri
     */
    public String generateSubsConfigSelUri(String mdn) {
        String methodName = "generateSubsConfigSelUri";

        knLogger.debug(methodName, "generating sel uri for Notification for mdn - ",  KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(150);
        strBuffer.append("xcap-directory/folder%5B@auid=%22kn-subscriber-config%22%5D/entry%5B@uri=%22kn-subscriber-config/users/tel:+");
        strBuffer.append(mdn);
        strBuffer.append("/index%22%5D/@etag");

        knLogger.debug(methodName, "generated sel uri for Notification for mdn - ",  KnGDPRTemplate.mdn(mdn), " is - ", KnGDPRTemplate.mdnUriTemplate(strBuffer.toString())
        );
        return strBuffer.toString();
    }

    /**
     * method to retrieve the Presence Service Config data
     * Since the data is cached the persister Txn is used only first time so no persister Txn is required
     *
     * @param presencePttServerId
     * @return KnPresenceServiceConfigDTO
     * @throws KnProvBOException BO Layer Exception
     */
    public KnPresenceServiceConfigDTO retrievePresenceServiceConfig(String presencePttServerId) throws KnProvBOException {
        String methodName = "retrievePresenceServiceConfig(String)";
        knLogger.debug(methodName, "ENTRY: retrieve Presence Service Config for pttid - ", presencePttServerId
        );
        KnPresenceServiceConfigDTO presenceServiceConfigDTO = null;
        Map<String, KnPresenceServiceConfigDTO> presenceServiceConfigMap = null;
        try {

            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            presenceServiceConfigMap = (Map<String, KnPresenceServiceConfigDTO>) cacheManager.get(KnProvCacheKeys.PRESENCE_SVC_CONFIG);
            if (!(presenceServiceConfigMap == null || presenceServiceConfigMap.isEmpty()) && presenceServiceConfigMap.containsKey(presencePttServerId)) {
                presenceServiceConfigDTO = presenceServiceConfigMap.get(presencePttServerId);
            }
            knLogger.debug(methodName, "retrieved Service config DTO from cache - ", presenceServiceConfigDTO
            );

            if (presenceServiceConfigDTO == null) {
                knLogger.debug(methodName, "retrieving the presence svc config from DB");
                IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                presenceServiceConfigDTO = provXdmServerDAO.retrievePresenceServiceConfig(presencePttServerId, null);
                //cacheManager.put(KnProvCacheKeys.PRESENCE_SVC_CONFIG, presenceServiceConfigDTO);
                if (presenceServiceConfigMap == null) {
                    presenceServiceConfigMap = new HashMap<String, KnPresenceServiceConfigDTO>();
                    presenceServiceConfigMap.put(presencePttServerId, presenceServiceConfigDTO);
                } else {
                    presenceServiceConfigMap.put(presencePttServerId, presenceServiceConfigDTO);
                }
                cacheManager.put(KnProvCacheKeys.PRESENCE_SVC_CONFIG, presenceServiceConfigMap);
            }

            knLogger.debug(methodName, "Presence Service config DTO - ", presenceServiceConfigDTO);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.PR_SVC_CONFIG_NOT_FOUND,
                        "Presence Svc Config not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);

        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get Presence service config ", e);
        }
        knLogger.debug(methodName, "EXIT : Presence Service Config - ", presenceServiceConfigDTO);

        return presenceServiceConfigDTO;
    }

    /**
     * method to retrieve the POC Service Config Data
     * Since the data is cached the persister Txn is used only first time so no persister Txn is required
     *
     * @param pocPttServerId
     * @return KnPOCSvcConfigDTO
     * @throws KnProvBOException BO entity layer exception
     */
    public KnPOCSvcConfigDTO retrievePOCSvcConfig(String pocPttServerId) throws KnProvBOException {
        String methodName = "retrievePOCSvcConfig(String)";
        knLogger.debug(methodName, "ENTRY: retrieve POC Service Config for pttid - ", pocPttServerId
        );
        KnPOCSvcConfigDTO pocSvcConfigDTO = null;
        Map<String, KnPOCSvcConfigDTO> pocSvcConfigMap;
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            //pocSvcConfigDTO = (KnPOCSvcConfigDTO) cacheManager.get(KnProvCacheKeys.POC_SVC_CONFIG);
            pocSvcConfigMap = (Map<String, KnPOCSvcConfigDTO>) cacheManager.get(KnProvCacheKeys.POC_SVC_CONFIG);
            if (pocSvcConfigMap != null && pocSvcConfigMap.containsKey(pocPttServerId)) {
                pocSvcConfigDTO = pocSvcConfigMap.get(pocPttServerId);
            }
            knLogger.debug(methodName, "retrieved poc svc config DTO from cache - ", pocSvcConfigDTO
            );

            if (pocSvcConfigDTO == null) {
                knLogger.debug(methodName, "retrieving the poc svc config from DB");
                IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                pocSvcConfigDTO = provXdmServerDAO.retrievePOCSvcConfig(pocPttServerId, null);
                if (pocSvcConfigMap == null) {
                    pocSvcConfigMap = new HashMap<String, KnPOCSvcConfigDTO>();
                    pocSvcConfigMap.put(pocPttServerId, pocSvcConfigDTO);
                } else {
                    pocSvcConfigMap.put(pocPttServerId, pocSvcConfigDTO);
                }

                cacheManager.put(KnProvCacheKeys.POC_SVC_CONFIG, pocSvcConfigMap);
            }

            knLogger.debug(methodName, "PoC svc config DTO -  ", pocSvcConfigDTO);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.POC_SVC_CONFIG_NOT_FOUND,
                        "POC Svc Config not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);


        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get POC service config ", e);
        }
        knLogger.debug(methodName, "EXIT : POC Service Config - ", pocSvcConfigDTO);

        return pocSvcConfigDTO;
    }

    /**
     * method to retrieve the POC registrar Service Config Data
     * Since the data is cached the persister Txn is used only first time so no persister Txn is required
     *
     * @param pocPttServerId
     * @return KnPOCRegistrarSrvcConfigDTO
     * @throws KnProvBOException BO entity layer exception
     */
    public KnPOCRegistrarSrvcConfigDTO retrievePOCregistrarSrvcConfig(String pocPttServerId) throws KnProvBOException {
        String methodName = "retrievePOCregistrarSrvcConfig(String)";
        knLogger.debug(methodName, "ENTRY: retrieve POC registrarService Config for pttid - ", pocPttServerId
        );
        KnPOCRegistrarSrvcConfigDTO pocRegSvcConfigDTO = null;
        Map<String, KnPOCRegistrarSrvcConfigDTO> pocRegSvcConfigMap = null;
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            //pocRegSvcConfigDTO = (KnPOCRegistrarSrvcConfigDTO) cacheManager.get(KnProvCacheKeys.POC_REGISTRAR_SVC_CONFIG);
            pocRegSvcConfigMap = (Map<String, KnPOCRegistrarSrvcConfigDTO>) cacheManager.get(KnProvCacheKeys.POC_REGISTRAR_SVC_CONFIG);
            if (pocRegSvcConfigMap != null && pocRegSvcConfigMap.containsKey(pocPttServerId)) {
                pocRegSvcConfigDTO = pocRegSvcConfigMap.get(pocPttServerId);
            }
            knLogger.debug(methodName, "retrieved poc registrar svc config DTO from cache - ", pocRegSvcConfigDTO);

            if (pocRegSvcConfigDTO == null) {
                knLogger.debug(methodName, "retrieving the poc registrar svc config from DB");
                IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                pocRegSvcConfigDTO = provXdmServerDAO.retrievePOCregistrarSrvcConfig(pocPttServerId, null);
                //cacheManager.put(KnProvCacheKeys.POC_REGISTRAR_SVC_CONFIG, pocRegSvcConfigDTO);
                if (pocRegSvcConfigMap == null) {
                    pocRegSvcConfigMap = new HashMap<String, KnPOCRegistrarSrvcConfigDTO>();
                    pocRegSvcConfigMap.put(pocPttServerId, pocRegSvcConfigDTO);
                } else {
                    pocRegSvcConfigMap.put(pocPttServerId, pocRegSvcConfigDTO);
                }
                cacheManager.put(KnProvCacheKeys.POC_REGISTRAR_SVC_CONFIG, pocRegSvcConfigMap);
            }

            knLogger.debug(methodName, "PoC registrar svc config DTO -  ", pocRegSvcConfigDTO);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.POC_REGISTRAR_SVC_CONFIG_NOT_FOUND,
                        "POC Registrar Svc Config not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);


        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get POC registrar service config ", e);
        }
        knLogger.debug(methodName, "EXIT : POC registrar Service Config -> ", pocRegSvcConfigDTO);
        return pocRegSvcConfigDTO;
    }

    /**
     * DG.SIPProxySvcConfig
     * method to retrieve the SIP proxy Svc Config Data
     * Since the data is cached the persister Txn is used only first time so no persister Txn is required
     *
     * @param pocPttServerId
     * @return KnSipProxySvcConfigDTO
     * @throws KnProvBOException BO entity layer exception
     */
    public KnSIPProxySvcConfigDTO retrieveSIPProxySvcConfig(String pocPttServerId) throws KnProvBOException {
        String methodName = "retrieveSIPProxySvcConfig(String)";
        knLogger.debug(methodName, "ENTRY: retrieve SIP proxy Svc Config f - ", pocPttServerId);
        KnSIPProxySvcConfigDTO sipProxySvcConfigDTO = null;
        Map<String, KnSIPProxySvcConfigDTO> sipProxyvcConfigMap = null;
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();

            sipProxyvcConfigMap = (Map<String, KnSIPProxySvcConfigDTO>) cacheManager.get(KnProvCacheKeys.SIP_PROXY_SVC_CONFIG);
            if (sipProxyvcConfigMap != null && sipProxyvcConfigMap.containsKey(pocPttServerId)) {
                sipProxySvcConfigDTO = sipProxyvcConfigMap.get(pocPttServerId);
            }
            knLogger.debug(methodName, "retrieved  SIP proxy Svc Config DTO from cache - ", sipProxySvcConfigDTO);

            if (sipProxySvcConfigDTO == null) {
                knLogger.debug(methodName, "retrieving the SIP proxy Svc Config from DB");
                IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                sipProxySvcConfigDTO = provXdmServerDAO.retrieveSIPProxySvcConfig(pocPttServerId, null);
                if (sipProxyvcConfigMap == null) {
                    sipProxyvcConfigMap = new HashMap<String, KnSIPProxySvcConfigDTO>();
                    sipProxyvcConfigMap.put(pocPttServerId, sipProxySvcConfigDTO);
                } else {
                    sipProxyvcConfigMap.put(pocPttServerId, sipProxySvcConfigDTO);
                }
                cacheManager.put(KnProvCacheKeys.SIP_PROXY_SVC_CONFIG, sipProxyvcConfigMap);
            }


            knLogger.debug(methodName, "SIP proxy Svc Config DTO -  ", sipProxySvcConfigDTO);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SIP_PROXY_SVC_CONFIG_NOT_FOUND,
                        "SIP proxy Svc Config not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);


        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get POC registrar service config ", e);
        }
        knLogger.debug(methodName, "EXIT : POC registrar Service Config -> ", sipProxySvcConfigDTO);

        return sipProxySvcConfigDTO;
    }

    /**
     * method to retrieve the feature access info
     * Since the data is cached the persister Txn is used only first time so no persister Txn is required
     *
     * @param featureAccIndex
     * @return KnFeatureAccessInfoDTO
     * @throws KnProvBOException BO entity layer exception
     */


    public KnFeatureAccessNumberInfoDTO retrieveFeatureAccessNumberInfo(int featureAccIndex) throws KnProvBOException {
        String methodName = "retrieveFeatureAccessNumberInfo(int)";
        knLogger.debug(methodName, "ENTRY: retrieve feature access number info  - featureAccIndex: ", featureAccIndex);
        KnFeatureAccessNumberInfoDTO featureAccessNumberInfoDTO = null;
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            featureAccessNumberInfoDTO = (KnFeatureAccessNumberInfoDTO) cacheManager.get(KnProvCacheKeys.FEATURE_ACCESS_NUMBER_INFO);
            knLogger.debug(methodName, "retrieved feature access number info DTO from cache - ", featureAccessNumberInfoDTO);

            if (featureAccessNumberInfoDTO == null) {
                knLogger.debug(methodName, "retrieving the  feature access number info from DB");
                IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                featureAccessNumberInfoDTO = provXdmServerDAO.retrieveFeatureAccessNumberInfo(featureAccIndex, null);
                cacheManager.put(KnProvCacheKeys.FEATURE_ACCESS_NUMBER_INFO, featureAccessNumberInfoDTO);
            }

            knLogger.debug(methodName, " feature access info DTO -  ", featureAccessNumberInfoDTO);


        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.FEATURE_ACCESS_INFO_NOT_FOUND,
                        " feature access info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  feature access info ", e);
        }
        knLogger.debug(methodName, "EXIT : feature access info -> ", featureAccessNumberInfoDTO);
        return featureAccessNumberInfoDTO;
    }

    /**
     * method to retrieve the feature access info
     * Since the data is cached the persister Txn is used only first time so no persister Txn is required
     *
     * @param featureAccIndex
     * @return KnFeatureAccessInfoDTO
     * @throws KnProvBOException BO entity layer exception
     */


    public KnFeatureAccessInfoDTO retrieveFeatureAccessInfo(int featureAccIndex) throws KnProvBOException {
        String methodName = "retrieveFeatureAccessInfo(int)";
        knLogger.debug(methodName, "ENTRY: retrieve feature access info  - featureAccIndex: ", featureAccIndex);
        KnFeatureAccessInfoDTO featureAccessInfoDTO = null;
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            featureAccessInfoDTO = (KnFeatureAccessInfoDTO) cacheManager.get(KnProvCacheKeys.FEATURE_ACCESS_INFO);
            knLogger.debug(methodName, "retrieved feature access info DTO from cache - ", featureAccessInfoDTO);

            if (featureAccessInfoDTO == null) {
                knLogger.debug(methodName, "retrieving the  feature access info from DB");
                IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                featureAccessInfoDTO = provXdmServerDAO.retrieveFeatureAccessInfo(featureAccIndex, null);
                cacheManager.put(KnProvCacheKeys.FEATURE_ACCESS_INFO, featureAccessInfoDTO);
            }

            knLogger.debug(methodName, " feature access info DTO -  ", featureAccessInfoDTO);

        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.FEATURE_ACCESS_INFO_NOT_FOUND, " feature access info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.FEATURE_ACCESS_INFO_NOT_FOUND,
                    " feature access info not found", e);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  feature access info ", e);
        }
        knLogger.debug(methodName, "EXIT :  feature access info -> ", featureAccessInfoDTO);

        return featureAccessInfoDTO;
    }


    /**
     * method to retrieve the Software package config
     * Since the data is cached the persister Txn is used only first time so no persister Txn is required
     *
     * @return KnXDMSDocSubPrxConfigDTO
     * @throws KnProvBOException BO entity layer exception
     */
    public List<KnSWPkgConfigDTO> retrieveSWPkgConfig() throws KnProvBOException {
        String methodName = "retrieveSWPkgConfig()";
        knLogger.debug(methodName, "ENTRY: retrieve Software package config");
        List<KnSWPkgConfigDTO> swPkgConfigDTOs = null;
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            swPkgConfigDTOs = (List<KnSWPkgConfigDTO>) cacheManager.get(KnProvCacheKeys.SW_PKG_CONFIG);
            knLogger.debug(methodName, "retrieved Software package config DTO from cache - ", swPkgConfigDTOs
            );

            if (swPkgConfigDTOs == null) {
                knLogger.debug(methodName, "retrieving the Software package config from DB");
                IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                swPkgConfigDTOs = provXdmServerDAO.retrieveSWPkgConfig(null);
                cacheManager.put(KnProvCacheKeys.SW_PKG_CONFIG, swPkgConfigDTOs);
            }

            knLogger.debug(methodName, "Software package config DTO -  ", swPkgConfigDTOs);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SW_PKG_CONFIG_NOT_FOUND,
                        "Software package config not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);

        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get Software package config ", e);
        }
        knLogger.debug(methodName, "EXIT : Software package config ", swPkgConfigDTOs);

        return swPkgConfigDTOs;
    }

    /**
     * method to retrieve the Dial Plan Info Data
     * Since the data is cached the persister Txn is used only first time so no persister Txn is required
     *
     * @return KnPOCRegistrarSrvcConfigDTO
     * @throws KnProvBOException BO entity layer exception
     */
    public KnDialPlanInfoDTO retrieveDialPlanInfo() throws KnProvBOException {
        String methodName = "retrieveDialPlanInfo()";
        knLogger.debug(methodName, "ENTRY: retrieve Dial Plan Info Config");
        KnDialPlanInfoDTO dialPlanConfigDTO = null;
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            dialPlanConfigDTO = (KnDialPlanInfoDTO) cacheManager.get(KnProvCacheKeys.DIAL_PLAN_INFO);
            knLogger.debug(methodName, "retrieved Dial Plan Info DTO from cache - ", dialPlanConfigDTO
            );

            if (dialPlanConfigDTO == null) {
                knLogger.debug(methodName, "retrieving the Dial Plan Info from DB");
                IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                dialPlanConfigDTO = provXdmServerDAO.retrieveDialPlanInfo(null);
                cacheManager.put(KnProvCacheKeys.DIAL_PLAN_INFO, dialPlanConfigDTO);
            }

            knLogger.debug(methodName, "Dial Plan Info DTO -  ", dialPlanConfigDTO);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.DIAL_PLAN_INFO_NOT_FOUND,
                        "Dial Plan Info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);

        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get Dial Plan Info ", e);
        }
        knLogger.debug(methodName, "EXIT : Dial Plan Info  ", dialPlanConfigDTO);

        return dialPlanConfigDTO;
    }

    /**
     * method to retrieve the XDMS Doc Subs Prefix Data
     * Since the data is cached the persister Txn is used only first time so no persister Txn is required
     *
     * @return KnXDMSDocSubPrxConfigDTO
     * @throws KnProvBOException BO entity layer exception
     */
    public KnXDMSDocSubPrxConfigDTO retrieveXDMSDocSubPrxConfig(String presPttServerId) throws KnProvBOException {
        String methodName = "retrieveXDMSDocSubPrxConfig(String)";
        knLogger.debug(methodName, "ENTRY: retrieve XDMS Doc Sub Prx Config");
        KnXDMSDocSubPrxConfigDTO xdmsDocSubPrxConfigDTO = null;
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            xdmsDocSubPrxConfigDTO = (KnXDMSDocSubPrxConfigDTO) cacheManager.get(KnProvCacheKeys.XDMS_DOC_SUB_PRX_CONFIG);
            knLogger.debug(methodName, "retrieved XDMS Doc Sub Prx Config DTO from cache - ", xdmsDocSubPrxConfigDTO
            );

            if (xdmsDocSubPrxConfigDTO == null) {
                knLogger.debug(methodName, "retrieving the XDMS Doc Sub Prx Config from DB");
                IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                xdmsDocSubPrxConfigDTO = provXdmServerDAO.retrieveXDMSDocSubPrxConfig(presPttServerId, null);
                // cacheManager.put(KnProvCacheKeys.XDMS_DOC_SUB_PRX_CONFIG, xdmsDocSubPrxConfigDTO);
            }

            knLogger.debug(methodName, "XDMS Doc Sub Prx Config DTO -  ", xdmsDocSubPrxConfigDTO);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_DOC_SUB_PRX_CONFIG_NOT_FOUND,
                        "XDMS Doc Sub Prx Config not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);

        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get XDMS Doc Sub Prx Config ", e);
        }
        knLogger.debug(methodName, "EXIT : XDMS Doc Sub Prx Config ", xdmsDocSubPrxConfigDTO);

        return xdmsDocSubPrxConfigDTO;
    }

    /**
     * method to retrieve the Insta POC Svc Config Data
     * Since the data is cached the persister Txn is used only first time so no persister Txn is required
     *
     * @param pocPttServerId
     * @return KnInstaPOCSrvcCfgDTO
     * @throws KnProvBOException BO entity layer exception
     */
    public KnInstaPOCSrvcCfgDTO retrieveInstaPOCSrvcCfg(String pocPttServerId) throws KnProvBOException {
        String methodName = "retrieveInstaPOCSrvcCfg(String)";
        knLogger.debug(methodName, "ENTRY: retrieve Insta PoC Srvc Config for pttid - ", pocPttServerId
        );
        KnInstaPOCSrvcCfgDTO instaPOCSvcConfigDTO = null;
        Map<String, KnInstaPOCSrvcCfgDTO> instaPOCSvcConfigMap = null;

        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            //instaPOCSvcConfigDTO = (KnInstaPOCSrvcCfgDTO) cacheManager.get(KnProvCacheKeys.INSTA_POC_SVC_CONFIG);
            instaPOCSvcConfigMap = (Map<String, KnInstaPOCSrvcCfgDTO>) cacheManager.get(KnProvCacheKeys.INSTA_POC_SVC_CONFIG);
            if (!(instaPOCSvcConfigMap == null || instaPOCSvcConfigMap.isEmpty()) && instaPOCSvcConfigMap.containsKey(pocPttServerId)) {
                instaPOCSvcConfigDTO = instaPOCSvcConfigMap.get(pocPttServerId);
            }
            knLogger.debug(methodName, "retrieved Insta PoC Srvc Config DTO from cache - ", instaPOCSvcConfigDTO
            );

            if (instaPOCSvcConfigDTO == null) {
                knLogger.debug(methodName, "retrieving the Insta PoC Srvc Config from DB");
                IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                instaPOCSvcConfigDTO = provXdmServerDAO.retrieveInstaPOCSrvcCfg(pocPttServerId, null);
                //cacheManager.put(KnProvCacheKeys.INSTA_POC_SVC_CONFIG, instaPOCSvcConfigDTO);
                if (instaPOCSvcConfigMap == null) {
                    instaPOCSvcConfigMap = new HashMap<String, KnInstaPOCSrvcCfgDTO>();
                    instaPOCSvcConfigMap.put(pocPttServerId, instaPOCSvcConfigDTO);
                } else {
                    instaPOCSvcConfigMap.put(pocPttServerId, instaPOCSvcConfigDTO);
                }
                cacheManager.put(KnProvCacheKeys.INSTA_POC_SVC_CONFIG, instaPOCSvcConfigMap);

            }

            knLogger.debug(methodName, "Insta PoC Srvc Config DTO -  ", instaPOCSvcConfigDTO);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.INSTA_POC_SVC_CONFIG_NOT_FOUND,
                        "Insta PoC Srvc Config not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);

        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get Insta PoC Srvc Config ", e);
        }
        knLogger.debug(methodName, "EXIT : Insta PoC Srvc Config ", instaPOCSvcConfigDTO);

        return instaPOCSvcConfigDTO;
    }


    public KnLocationServiceConfigDTO retrieveLocationServiceConfig(String presencePttId) throws KnProvBOException {
        String methodName = "retrieveLocationServiceConfig";
        knLogger.debug(methodName, "ENTRY: Retrieve Location Service Config - ", presencePttId);
        KnLocationServiceConfigDTO locSvcConfigDTO = null;
        Map<String, KnLocationServiceConfigDTO> locationSvcConfigMap = null;

        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            locationSvcConfigMap = (Map<String, KnLocationServiceConfigDTO>) cacheManager.get(KnProvCacheKeys.LOC_SVC_CONFIG);
            if (!(locationSvcConfigMap == null || locationSvcConfigMap.isEmpty()) && locationSvcConfigMap.containsKey(presencePttId)) {
                locSvcConfigDTO = locationSvcConfigMap.get(presencePttId);
            }
            knLogger.debug(methodName, "retrieved Location Svc Config DTO from cache - ", locSvcConfigDTO
            );

            if (locSvcConfigDTO == null) {
                knLogger.debug(methodName, "retrieving the Location Srvc Config from DB");
                IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                locSvcConfigDTO = provXdmServerDAO.retrieveLocationSrvcConfig(presencePttId, null);
                if (locationSvcConfigMap == null) {
                    locationSvcConfigMap = new HashMap<String, KnLocationServiceConfigDTO>();
                    locationSvcConfigMap.put(presencePttId, locSvcConfigDTO);
                } else {
                    locationSvcConfigMap.put(presencePttId, locSvcConfigDTO);
                }
                cacheManager.put(KnProvCacheKeys.LOC_SVC_CONFIG, locationSvcConfigMap);

            }

            knLogger.debug(methodName, "Location Srvc Config DTO -  ", locSvcConfigDTO);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.LOCATION_SERVICE_CONFIG_NOT_FOUND,
                        "Location Srvc Config not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);

        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get Location Srvc Config ", e);
        }
        knLogger.debug(methodName, "EXIT : Location Srvc Config ", locSvcConfigDTO);

        return locSvcConfigDTO;


    }


    /**
     * method to update the Route Uri's as per ICD
     * Route URI's in the DB are configured with only Domain Name
     * but to be in compliance with the handset ICD the format of the URI
     * is updated through this method
     * <p/>
     * this method appends prefix as "sip:" and suffix as "lr"
     * <p/>
     * format of the Route as per ICD; sip:routeDomainName;lr
     *
     * @param routeUri String routeURI
     * @return routeUri after format
     */
    public String updateConfigRouteUri(String routeUri) {
        String methodName = "updateConfigRouteUri(String)";
        knLogger.debug(methodName, "updating route uri - ", routeUri);

        if (routeUri == null) {
            return null;
        } else {
            routeUri = routeUri.trim();
            if ("".equals(routeUri)) {
                return null;
            }
        }

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(KnProvConstants.SIP_URI_TEMPLATE);
        strBuffer.append(routeUri);
        strBuffer.append(KnProvConstants.SEMI_COLON).append(KnProvConstants.ROUTING_TYPE_TEMPLATE);

        knLogger.debug(methodName, "Formatted route Uri - ", strBuffer.toString());
        return strBuffer.toString();
    }

    /**
     * method to retrieve the Ext Corp Id
     *
     * @param corpId int
     * @return String Ext Corp Id
     * @throws KnProvBOException BO entity Exception
     */
    public String retrieveExtCorpId(int corpId) throws KnProvBOException {
        String methodName = "retrieveExtCorpId";
        knLogger.debug(methodName, "ENTRY: Retrieving Ext Corp Id for mdn - ", corpId);
        String extCorpId = null;
        try {
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            extCorpId = xdmServerDAO.retrieveExtCorporationId(corpId, null);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            knLogger.error(methodName, "Persistence Exception with error code - ", errorCode);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Ext Corp Id not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);


        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to retrieve Ext Corp Id", e);
        }
        knLogger.debug(methodName, "EXIT : retrieved Ext CorpId Id for corpId [", corpId, "], ",
                "is [", extCorpId, "] ");


        return extCorpId;
    }

    /**
     * method to retrieve the Signailng Card name
     *
     * @param pttServerId  String
     * @param persisterTxn String
     * @return String - Signaling Card Name
     * @throws KnProvBOException BO Entity Exception
     */
    public String getSignalingCardInfo(String pttServerId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getSignalingCardInfo(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: retrieving Signaling card info details for pttserver id ", pttServerId
        );
        String signalingCardName = null;

        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();

            //Map<pttserverid, pttServerName>
            Map<String, String> signalingCardInfo = (Map<String, String>) cacheManager.get(KnProvCacheKeys.SIGNALING_CARD_INFO);
            if (signalingCardInfo != null && !signalingCardInfo.isEmpty()) {
                signalingCardName = signalingCardInfo.get(pttServerId);
            }

            knLogger.debug(methodName, "Signaling card name from Cache - ", signalingCardName);

            if (signalingCardName == null || signalingCardName.equals("")) {
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                signalingCardName = provXDMServerDAO.retrieveSignalingCardName(pttServerId, persisterTxn);
                if (signalingCardInfo == null) {
                    signalingCardInfo = new HashMap<String, String>();
                }
                signalingCardInfo.put(pttServerId, signalingCardName);
                cacheManager.put(KnProvCacheKeys.SIGNALING_CARD_INFO, signalingCardInfo);
            }
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to retrieve sig card info", e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to retrieve sig card info", e);
        }
        knLogger.debug(methodName, "EXIT: Sig Card name for Ptt server [", pttServerId, "] is "
                , signalingCardName);


        return signalingCardName;
    }


    /**
     * method to retrieve the PAM Service Config Data
     * Since the data is cached the persister Txn is used only first time so no persister Txn is required
     *
     * @return KnPOCSvcConfigDTO
     * @throws KnProvBOException BO entity layer exception
     */
    public KnPAMSvcConfigDTO retrievePAMSvcConfig(KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrievePAMSvcConfig(String)";
        knLogger.debug(methodName, "ENTRY: retrieve PAM Service Config");
        KnPAMSvcConfigDTO pamSvcConfigDTO = null;
        try {
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            pamSvcConfigDTO = (KnPAMSvcConfigDTO) cacheManager.get(KnProvCacheKeys.PAM_SVC_CONFIG);
            knLogger.debug(methodName, "retrieved PAM svc config o DTO from cache - ", pamSvcConfigDTO);

            if (pamSvcConfigDTO == null) {
                knLogger.debug(methodName, "retrieving the PAM Svc Config info from DB");
                IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                pamSvcConfigDTO = provXdmServerDAO.retrievePAMSvcConfig(persisterTxn);
                cacheManager.put(KnProvCacheKeys.PAM_SVC_CONFIG, pamSvcConfigDTO);
            }

            knLogger.debug(methodName, " PAM Svc Config DTO -  ", pamSvcConfigDTO);


        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.PAM_SVC_CONFIG_NOT_FOUND,
                        " feature access info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  pam svc config ", e);
        }
        knLogger.debug(methodName, "EXIT : Pam Svc Config -> ", pamSvcConfigDTO);
        return pamSvcConfigDTO;
    }

    public KnPAMAccPersistDTO populateDefaultProfile(KnPAMAccPersistDTO pamAccPersistDTO,KnPersisterTxn persisterTxn) throws KnProvBOException{
        String methodName = "populateDefaultProfile(String)";
        KnPAMSubsProfInfoDTO pamSubsProfInfoDTO = pamAccPersistDTO.getProfileDetails();
        knLogger.debug(methodName, "KnPAMSubsProfInfoDTO pamSubsProfInfoDTO", pamSubsProfInfoDTO);

        String tierPackageId=null;
        Map<String,Integer> addonPackageIds=new HashMap<>();
        Map<String, Integer> addPkgIds=new HashMap<String, Integer>();
        if(pamSubsProfInfoDTO.getPkgIdMap()!=null && pamSubsProfInfoDTO.getPkgIdMap().get(KnConstants.ADD_ACTION)!=null)
        {
            addPkgIds=pamSubsProfInfoDTO.getPkgIdMap().get(KnConstants.ADD_ACTION);
            for (Entry<String, Integer> entry : addPkgIds.entrySet()) {
                if(entry.getValue().intValue()==KnConstants.TIER_PKG_TYPE.intValue())
                {
                    tierPackageId=entry.getKey();
                }
                else if(entry.getValue().intValue()==KnConstants.ADDON_PKG_TYPE.intValue())
                {
                    addonPackageIds.put(entry.getKey(), entry.getValue());
                }
            }
        }
        Map<Integer, Integer> provFSMap = pamSubsProfInfoDTO.getProvFSMap();
        knLogger.debug(methodName,"provFSMap fetched from subscriber profile  :",provFSMap);

        BitSet bitset =null;
        String xdmPttServerId;

        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();

            if (pamSubsProfInfoDTO.getProfileId() == 0) {
                pamSubsProfInfoDTO.setProfileId(KnProvConstants.DEFAULT_PROFILEID);
            }
            if (pamSubsProfInfoDTO.getClient_Type() == 0) {
                pamSubsProfInfoDTO.setClient_Type(KnProvConstants.DEFAULT_CLIENTTYPE);
            }
            int publicSubscriptionType = pamAccPersistDTO.getProfileDetails().getPubSubsType();
            int corporateSubscriptionType = pamAccPersistDTO.getProfileDetails().getCorpSubsType();
            Integer subsClientType = pamAccPersistDTO.getProfileDetails().getClient_Type();

            BitSet finalFSBitSet = new BitSet(Long.SIZE);
            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            String basePkgCode=paramNameValueMap.get(KnConstants.BASE_PKGCODE);
            String basePkgDefFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(publicSubscriptionType, corporateSubscriptionType, subsClientType, basePkgCode, xdmPttServerId);
            knLogger.debug(methodName, "base pkg is applied - ", basePkgDefFS);
            BitSet basePkgDefFSBitSet =featureSetUtil.convertHexStringToBitSet(basePkgDefFS);
            finalFSBitSet.or(basePkgDefFSBitSet);
            if(!addPkgIds.isEmpty())
            {
                String pkgCodesFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(publicSubscriptionType, corporateSubscriptionType, subsClientType,addPkgIds, xdmPttServerId);
                knLogger.debug(methodName, "addPkgIds is present with subscriberFS - ", pkgCodesFS);
                BitSet pkgCodes_BiSet =featureSetUtil.convertHexStringToBitSet(pkgCodesFS);
                finalFSBitSet.or(pkgCodes_BiSet);
            }
            String subsFeatureSet2 =featureSetUtil.convertBitSetToHexString(finalFSBitSet);
            knLogger.info(methodName, "subsFeatureSet2 :", subsFeatureSet2, "provFSMap :", provFSMap);
            //Here we need to calcualte the subscriberFS based on value
            bitset = new BitSet();
            if (provFSMap != null) {
                for (Map.Entry<Integer, Integer> provFSMapEntry : provFSMap.entrySet()) {
                    Integer featureId = provFSMapEntry.getKey();
                    Integer featureEnabled = provFSMapEntry.getValue();
                    if (featureEnabled == com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.ENABLED.value()) {
                        bitset.set(featureId);
                    } else {
                        bitset.clear(featureId);
                    }
                }
            }
            String provFS2 = featureSetUtil.convertBitSetToHexString(bitset);
            knLogger.debug(methodName, "  provFS2 ", provFS2, "bitset :", bitset);
            String provFS2BitMask = provFS2;

            String subsFS2 = featureSetUtil.generateSubsFeatureSet(subsFeatureSet2, provFS2, provFS2BitMask);
            pamSubsProfInfoDTO.setSubscriberFS2(subsFS2);
            //**Here calculation ends/
            if ((pamSubsProfInfoDTO.getPubSubsType() == -1 && pamSubsProfInfoDTO.getCorpSubsType() == -1) ||
                    pamSubsProfInfoDTO.getPubSubsType() == 0 && pamSubsProfInfoDTO.getCorpSubsType() == 0) {
                pamSubsProfInfoDTO.setPubSubsType(KnProvConstants.DEFAULT_PUBTYPE);
                pamSubsProfInfoDTO.setCorpSubsType(KnProvConstants.DEFAULT_CORPTYPE);
            }

            Integer qppPkgId = null;
            Integer profileId = null;
            Integer dataPkgId = null;
            if (!addonPackageIds.isEmpty()) {
                pamSubsProfInfoDTO.setAddOnPkgId(new ArrayList<String>(addonPackageIds.keySet()));
                for (String addonPkgCode : addonPackageIds.keySet()) {
                    profileId = featureSetUtil.getAddProfIdForPkg(addonPkgCode, xdmPttServerId);
                    if (profileId != null) {
                        qppPkgId = genInfoUtil.getDataPkgId(KnConstants.QPP_DATA_PKG_TYPE, persisterTxn).get(profileId);
                        if (qppPkgId != null) {
                            dataPkgId = qppPkgId;
                            break;
                        } else if (dataPkgId == null) {
                            dataPkgId = genInfoUtil.getDataPkgId(KnConstants.ADDON_DATA_PKG_TYPE, persisterTxn)
                                    .get(profileId);
                        }

                    }
                }

            }
            if (qppPkgId == null) {
                qppPkgId = KnConstants.DEFAULT_QPP_ID;
            }
            if (dataPkgId == null) {
                dataPkgId = KnConstants.DEFAULT_DATAPKG_ID;
            }
            if (profileId == null) {
                profileId = KnConstants.DEFAULT_PROFILE_ID;
            }

            pamSubsProfInfoDTO.setTierPkgCode(tierPackageId);

            pamSubsProfInfoDTO.setDataPkgId(dataPkgId);
            pamAccPersistDTO.setProfileDetails(pamSubsProfInfoDTO);

            knLogger.debug(methodName, "EXIT : populateDefaultProfile -> ", pamSubsProfInfoDTO);
        } catch (KnBOException | KnFeatureSetException e) {
            knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
            knLogger.error(methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get  pam svc config ", e);
        }

        return pamAccPersistDTO;
    }

    /**
     * Method to validate and get the poc server home for the subscriber
     *
     * @param mdn
     * @param corpProfileInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    public Map<String, String> validateAndgetCorpAnchorPocHome(String mdn, KnOPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "validateAndgetCorpAnchorPocHome(String,KnOPCorpProfileInfoDTO,KnPersisterTxn)";
        knLogger.info(methodName, "Mdn :", KnGDPRTemplate.mdn(mdn));

        return validateAndgetCorpAnchorPocHome(mdn, corpProfileInfoDTO, 0, persisterTxn);
    }

    /**
     * Method to validate and get the poc server home for the bulk subscribers
     *
     * @param mdn
     * @param corpProfileInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    public Map<String, String> validateAndgetCorpAnchorPocHome(String mdn, KnOPCorpProfileInfoDTO corpProfileInfoDTO, int subsCount, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "validateAndgetCorpAnchorPocHome(String,KnOPCorpProfileInfoDTO,int , KnPersisterTxn)";
        // Since this is 2nd subs of the corp,
        // check if the corp anchoring is enabled and  set corp poc home as subs poc home
        // else fall back to partitioning logic
        knLogger.debug(methodName, "MDN :",KnGDPRTemplate.mdn(mdn), " subsCount:", subsCount);
        String poCPttServerId = null;
        boolean updateCorpHome = false;
        Map<String, String> corpAnchorMap = new HashMap<String, String>();
        try {
            KnSubsPartitionConfigDTO partitionConfigDTO = getPartitionConfig(persisterTxn);
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            String corpPoCHome = corpProfileInfoDTO.getPocHome();
            String extCorpId = corpProfileInfoDTO.getExtCorpId();

            if (corpProfileInfoDTO.getCorpId() > 0) {
                if (partitionConfigDTO.getEnableCorpAccAnch() == KnProvConstants.CORP_ACC_ANCHORING.ENABLED.value() &&
                        partitionConfigDTO.getMdnPartitionTypePOC() == KnProvConstants.PARTITION_TYPE.LOAD_BASED.value()) {
                    List<String> pocHome = new ArrayList<String>();
                    pocHome.add(corpPoCHome);
                    Map<String, Integer> pocSubsCnt = provXDMServerDAO.getSubsCount(pocHome, persisterTxn);
                    //check
                    // 1. if Max Limit is reached OR
                    // 2. if POC is disabled for Prov
                    List<String> pttIds = new ArrayList<String>();
                    pttIds.add(corpPoCHome);
                    KnPocSubsCapConfigDTO subsCapConfigDTO = getPocSubsCapacityConfig(pttIds, persisterTxn).get(corpPoCHome);
                    if ((subsCapConfigDTO == null) || ((pocSubsCnt.get(corpPoCHome) + subsCount) >= (subsCapConfigDTO.getMaxSubsLimit())) ||
                            (subsCapConfigDTO.getAllowProv() == KnProvConstants.ALLOW_POC_PROV.DONT_ALLOW.value())) {
                        // since the max limit for POC has been reached, looking for a new PoC Home
                        poCPttServerId = getSubsPoCHome(mdn, persisterTxn);
                        updateCorpHome = true;
                    } else {
                        //setting the PoC Home of Corp to new Subscriber
                        knLogger.debug(methodName, "Resetting corp Home to subs");
                        poCPttServerId = corpPoCHome;
                    }
                } else if (partitionConfigDTO.getEnableCorpAccAnch() == KnProvConstants.CORP_ACC_ANCHORING.ENABLED.value() &&
                        partitionConfigDTO.getMdnPartitionTypePOC() == KnProvConstants.PARTITION_TYPE.MDN_BASED.value()) {
                    List<String> pocHome = new ArrayList<String>();
                    pocHome.add(corpPoCHome);
                    Map<String, Integer> pocSubsCnt = provXDMServerDAO.getSubsCount(pocHome, persisterTxn);
                    List<String> pttIds = new ArrayList<String>();
                    pttIds.add(corpPoCHome);
                    KnPocSubsCapConfigDTO subsCapConfigDTO = getPocSubsCapacityConfig(pttIds, persisterTxn).get(corpPoCHome);
                    if ((subsCapConfigDTO == null) || ((pocSubsCnt.get(corpPoCHome) + subsCount) >= (subsCapConfigDTO.getMaxSubsLimit())) ||
                            (subsCapConfigDTO.getAllowProv() == KnProvConstants.ALLOW_POC_PROV.DONT_ALLOW.value())) {
                        // since the max limit for POC has been reached, raise alarm (Alrm raised in exception catch block).
                        knLogger.error(methodName, "MAX limit for the POC Home has been reached");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                                "MAX limit for the POC Home has been reached");
                    } else {
                        //setting the PoC Home of Corp to new Subscriber
                        knLogger.debug(methodName, "Resetting corp Home to subs");
                        poCPttServerId = corpPoCHome;
                    }
                } else {
                    poCPttServerId = getSubsPoCHome(mdn, persisterTxn);
                }
            } else {
                // since this is the first subs for the corp
                // check if there is any pre assigned home
                String preAssignCorpHome = getPreAssignCorpHome(extCorpId, persisterTxn);
                if (preAssignCorpHome != null) {
                    poCPttServerId = preAssignCorpHome;
                    //check if the Pre Assign corp home limit has been reached
                    List<String> pocHome = new ArrayList<String>();
                    pocHome.add(poCPttServerId);
                    Map<String, Integer> pocSubsCnt = provXDMServerDAO.getSubsCount(pocHome, persisterTxn);
                    KnPocSubsCapConfigDTO subsCapConfigDTO = getPocSubsCapacityConfig(pocHome, persisterTxn).get(poCPttServerId);
                    if ((subsCapConfigDTO == null) || ((pocSubsCnt.get(poCPttServerId) + subsCount) >= (subsCapConfigDTO.getMaxSubsLimit())) ||
                            (subsCapConfigDTO.getAllowProv() != KnProvConstants.ALLOW_POC_PROV.ALLOW_PROV.value())) {
                        knLogger.error(methodName, "MAX limit for the POC Home has been reached");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                                "MAX limit for the POC Home has been reached");
                    }
                } else {
                    //first subs so ignore corp anchor flag 2
                    poCPttServerId = getSubsPoCHome(mdn, persisterTxn);
                }

            }
            corpAnchorMap.put(KnProvConstants.POC_PTT_ID_KEY, poCPttServerId);
            corpAnchorMap.put(KnProvConstants.UPDATE_CORP_HOME, String.valueOf(updateCorpHome));
            knLogger.info(methodName, "returning pocPttServerId :", poCPttServerId, " updateCorpHome:", updateCorpHome);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception ", e.getMessage());
            throw new KnProvBOException(e.getErrorCode(), "failed to retrieve subs partition/pocserver config");
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception - ", e);
            throw e;
        }
        return corpAnchorMap;
    }

    public boolean isBlackListDevice(KnUserAgentDTO userAgentDTO, String xdmPttServerId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "isBlackListDevice(KnUserAgentDTO,String,KnPersisterTxn)";
        boolean isBlacklistDevice = false;
        try {
            int blkBlackListDevices = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getBlockBlackListDevices();
            knLogger.info(methodName, " BlOCK BLACK LIST DEVICES :", blkBlackListDevices);
            knLogger.info(methodName, " userAgentDTO is", userAgentDTO);
            if (blkBlackListDevices == 0) {
                isBlacklistDevice = false;
            } else {
                Map<Integer, KnPOCBlackListDevicesDTO> detailsFromDB = genInfoUtil.retrievePOCBlackListDevices(xdmPttServerId, persisterTxn);
                for (Map.Entry<Integer, KnPOCBlackListDevicesDTO> entry : detailsFromDB.entrySet()) {
                    KnPOCBlackListDevicesDTO pocBlackListDevicesDTO = entry.getValue();
                    knLogger.debug(methodName, " pocBlackListDevicesDTO is", pocBlackListDevicesDTO);
                    if ((pocBlackListDevicesDTO.getDeviceVendor().equals(userAgentDTO.getManufactName()) || pocBlackListDevicesDTO.getDeviceVendor().equals("*")) &&
                            (pocBlackListDevicesDTO.getDeviceModel().equals(userAgentDTO.getDeviceName()) || pocBlackListDevicesDTO.getDeviceModel().equals("*")) &&
                            (pocBlackListDevicesDTO.getDeviceOS().equals(userAgentDTO.getOsName()) || pocBlackListDevicesDTO.getDeviceOS().equals("*")) &&
                            (pocBlackListDevicesDTO.getDeviceOSVersion().equals(userAgentDTO.getOsVersion()) || pocBlackListDevicesDTO.getDeviceOSVersion().equals("*")) &&
                            (pocBlackListDevicesDTO.getUiVersion().equals(userAgentDTO.getUiVersion()) || pocBlackListDevicesDTO.getUiVersion().equals("*")) &&
                            (pocBlackListDevicesDTO.getHsBaseband().equals(userAgentDTO.getHsBaseband()) || pocBlackListDevicesDTO.getHsBaseband().equals("*")) &&
                            (pocBlackListDevicesDTO.getProtocolVersion().equals(userAgentDTO.getProtocolVersion()) || pocBlackListDevicesDTO.getProtocolVersion().equals("*"))) {
                        isBlacklistDevice = true;
                        break;
                    }

                }

            }
            knLogger.debug(methodName, " isBlackListDevice :", isBlacklistDevice);
            return isBlacklistDevice;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while activate Subscriber");
            knLogger.error(methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while activate Subscriber", e);
        }
    }

    public boolean isSupportedDevice(KnUserAgentDTO userAgentDTO, String xdmPttServerId, KnPersisterTxn persisterTxn, KnOPSubsProfileInfoDTO existingSubsProfileDTO) throws KnProvBOException {
        String methodName = "isSupportedDevice()";
        int allowUaAct = 0;
        boolean ownedTxn = false;
        boolean supportedDevice = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            Integer subsClientType = existingSubsProfileDTO.getSubsClientType();
            //retriving from db/cache client type supp dev check check is enable or disable
            knLogger.debug(methodName,"Subscriber client type is ",subsClientType);
            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value() ||
                    subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value() ||
                    subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() ||
                    subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value() ||
                    subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
                KnClientTypeConfigDTO clientTypeConfigDTO = genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
                if (clientTypeConfigDTO.getEnableSuppDevChk() == KnProvConstants.CLIENT_TYPE_CONFIGURATION.DISABLED.value()) {
                    knLogger.error(methodName, "Client type enable supp device check is disabled ",clientTypeConfigDTO.getEnableSuppDevChk());
                    supportedDevice = true; //return client type not supported
                    return supportedDevice;
                }
            }
            Map<String, String> configMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            if (configMap != null) {
                allowUaAct = Integer.parseInt(configMap.get(KnConstants.ALLOW_MALFORMED_UA_ACTIVATION));
            }
            knLogger.debug(methodName, " is ALLOW_MALFORMED_UA_ACTIVATION :", allowUaAct);

            if (allowUaAct == 0 || (userAgentDTO != null)) {
                int blkUnsuppDevices = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getBlockUnsupportedDevices();

                if ((blkUnsuppDevices == KnProvConstants.BLK_UNSUPPORTED_DEVICES.ENABLED.value()) &&
                        ((existingSubsProfileDTO.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value())
                                || (existingSubsProfileDTO.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value())
                                || (existingSubsProfileDTO.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value())
                                || (existingSubsProfileDTO.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.MOBILE_CLIENT.value())
                                || (existingSubsProfileDTO.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value())
                                || (existingSubsProfileDTO.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.PDVCONNECT.value())
                                || (existingSubsProfileDTO.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value())
                                || (existingSubsProfileDTO.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value())
                                || (existingSubsProfileDTO.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()))) {
                    if (userAgentDTO == null) {
                        knLogger.error(methodName, "BLOCK_UNSUPPORTED_DEVICES is enabled and device not supported");
                        // throw new KnProvBOException(KnErrorCodes.BOEntity.UNSUPPORTED_DEVICE, "UNSUPPORTED DEVICE");
                        supportedDevice = false;
                    } else {
                        Map<String, KnPOCSuppDevicesDTO> detailsFromDB = genInfoUtil.retrievePOCSuppDevices(xdmPttServerId, persisterTxn);
                        for (Map.Entry<String, KnPOCSuppDevicesDTO> entry : detailsFromDB.entrySet()) {

                            if (entry.getKey().lastIndexOf(xdmPttServerId) != -1) {
                                KnPOCSuppDevicesDTO suppDevice = entry.getValue();

                                knLogger.debug(methodName, "suppDevice ::", suppDevice, " UserAgent Detais :", userAgentDTO);

                                if (!(userAgentDTO.getDeviceName().equals(suppDevice.getDeviceModel()) &&
                                        userAgentDTO.getManufactName().equals(suppDevice.getDeviceVendor())) ||
                                        ((userAgentDTO.getDeviceName().equals(suppDevice.getDeviceModel()) &&
                                                userAgentDTO.getManufactName().equals(suppDevice.getDeviceVendor())) &&
                                                !((suppDevice.getDeviceOS() == null || userAgentDTO.getOsName().equals(suppDevice.getDeviceOS())) &&
                                                        (suppDevice.getDeviceOSVersion() == null || userAgentDTO.getOsVersion().equals(suppDevice.getDeviceOSVersion()))))) {
                                    supportedDevice = false;
                                    continue;
                                }
                                supportedDevice = true;
                                break;
                            }
                        }
                    }
                } else {
                    supportedDevice = true;

                }
            }
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber already exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while activate Subscriber");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while activate Subscriber", e);
        }

        return supportedDevice;
    }


    private void rollback(KnPersisterTxn txn) {
        try {
            txn.rollback();
        } catch (Exception e) {
            knLogger.error("rollback(txn)", "Failed to rollback the transaction.");
        }
    }

    public Map<String, List<String>> getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(Map<String, List<String>> tableInfo) {
        this.tableInfo = tableInfo;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    //@Override
    public void update(Observable observable, Object o) {
        String methodName = "update()";
        knLogger.info(methodName, "cleaning cache");
        List<String> modifiedTables = (List<String>) o;

        KnConfigurationsManager confManager = KnConfigurationsManager.getInstance();
        try {
            ICacheManager cacheManager = confManager.getCacheManager();
            for (String str : modifiedTables) {
                if (tableInfo.containsKey(str)) {
                    cacheManager.put(tableMap.get(str), null);
                    knLogger.debug(methodName, "cleaning cache for ", str);
                }
            }
        } catch (KnConfigurationException knConfigurationException) {
            knLogger.error(methodName, "Exception", "caught in update");
        }
    }

    /**
     * method to retrieve the notification data for the list of mdns (data from the PoCSubscrInfo Table)
     *
     * @param mdns         List
     * @param persisterTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnProvBOException BO entity exception
     */
    public Map<String, KnOPSubsProfileInfoDTO> retrieveNotificationDetails4mdns(List mdns, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrieveNotificationDetails4mdns(List, KnPersisterTxn)";
        Map<String, KnOPSubsProfileInfoDTO> respDTO = null;
        knLogger.entry(methodName, KnGDPRTemplate.mdnList(mdns));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            respDTO = provXDMServerDAO.retrieveNotificationDetails4mdns(mdns, persisterTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Subscriber Profile info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);

        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get subscriber Info", e);
        }
        knLogger.info(methodName, "EXIT : retrieve Subscriber Profile Info -> ", respDTO);


        return respDTO;
    }

    public int getMappedSubscriptionType(int publicSubscType, int corpSubscType) {
        String methodName = "getMappedSubscriptionType(int,int)";
        int subscriptionType = -1;
        if (publicSubscType == 1 && corpSubscType == 1) {
            subscriptionType = 2;
        } else if (publicSubscType == 0 && corpSubscType == 1) {
            subscriptionType = 1;
        } else if (publicSubscType == 1 && corpSubscType == 0) {
            subscriptionType = 0;
        }
        knLogger.debug(methodName, subscriptionType);
        return subscriptionType;

    }


    public List<Integer> getRoamingTypeClusterIds(int roamingType, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getRoamingTypeClusterIds(int,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:Retrieve the clusterIds for roaming type - ", roamingType);
        Map<Integer, List<Integer>> roamingTypeClusterIdMap = null;
        List<Integer> clusterIdList = null;
        try {

            KnConfigurationsManager confManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = confManager.getCacheManager();
            roamingTypeClusterIdMap = (Map<Integer, List<Integer>>) cacheManager.get(KnProvCacheKeys.ROAMINGMCCMNCINFO);
            knLogger.debug(methodName, "before reading from db/cache roamingTypeClusterIdMap is", roamingTypeClusterIdMap);
            if (roamingTypeClusterIdMap == null) {
                knLogger.info(methodName, "Not found in cache,Retrieving roamingTypeClusterIdMap from DB");
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                roamingTypeClusterIdMap = provXDMServerDAO.retrieveRoamingTypeClusterIds(persisterTxn);
                cacheManager.put(KnProvCacheKeys.ROAMINGMCCMNCINFO, roamingTypeClusterIdMap);
            }
            knLogger.debug(methodName, "after reading from db/cache roamingTypeClusterIdMap is", roamingTypeClusterIdMap);
            if (roamingTypeClusterIdMap.containsKey(roamingType)) {
                clusterIdList = roamingTypeClusterIdMap.get(roamingType);
            } else {
                clusterIdList = new ArrayList<>();
            }
            knLogger.debug(methodName, "EXIT:ClusterIds list - ", clusterIdList, " for roaming type - ", roamingType);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO Exception Occurred", e);

        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to retrieve clusterId list", e);
        }
        return clusterIdList;
    }


    public Integer getRoamingType(List<Integer> clusterIds, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getRoamingType(List<Integer>,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:get Roaming type for clusterids -  ", clusterIds);
        Map<Integer, List<Integer>> roamingTypeClusterIdMap = null;
        int maxRoamingTYpe = 0;
        try {

            KnConfigurationsManager confManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = confManager.getCacheManager();
            roamingTypeClusterIdMap = (Map<Integer, List<Integer>>) cacheManager.get(KnProvCacheKeys.ROAMINGMCCMNCINFO);
            knLogger.debug(methodName, "before reading from db/cache roamingTypeClusterIdMap is", roamingTypeClusterIdMap);
            if (roamingTypeClusterIdMap == null) {
                knLogger.info(methodName, "Not found in cache,Retrieving roamingTypeClusterIdMap from DB");
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
                roamingTypeClusterIdMap = provXDMServerDAO.retrieveRoamingTypeClusterIds(persisterTxn);
                cacheManager.put(KnProvCacheKeys.ROAMINGMCCMNCINFO, roamingTypeClusterIdMap);
            }
            knLogger.debug(methodName, "after reading from db/cache roamingTypeClusterIdMap is", roamingTypeClusterIdMap);
            for (Map.Entry<Integer, List<Integer>> roamingTypeClusterId : roamingTypeClusterIdMap.entrySet()) {
                List<Integer> clusterIdList = roamingTypeClusterId.getValue();
                Integer roamingType = roamingTypeClusterId.getKey();
                if (!Collections.disjoint(clusterIds, clusterIdList)) {
                    if (maxRoamingTYpe < roamingType) {
                        maxRoamingTYpe = roamingType;
                    }
                }
            }

            knLogger.info(methodName, "EXIT:Returned max roaming type -  ", maxRoamingTYpe);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to retrieve clusterId list", e);
        }
        return maxRoamingTYpe;
    }

    public void createNNISubscriberProfile(List<String> mdns, int subsClientType,KnPersisterTxn persisterTxn) throws KnFeatureSetException, KnDAOException, KnProvBOException {
        final String methodName = "createNNISubscriberProfile(List, int)";
        knLogger.debug(methodName, "ENTRY : mdns ", KnGDPRTemplate.mdnList(mdns));
        int profileType = 2;
        long nniSubScrFs = 0l;
        nniSubScrFs = featureSetUtil.retrieveNNISubScrFs(profileType);
        knLogger.info(methodName, " Retrieved nniSubScrFs ", nniSubScrFs);
        long pocSystemFs = featureSetUtil.retrievePocSystemFs();
        knLogger.info(methodName, " Retrieved pocSystemFs ", pocSystemFs);
        long nniGwFs = featureSetUtil.retrieveNNIGwFs();
        knLogger.info(methodName, " Retrieved nniGwFs ", nniGwFs);
        long nniActiveFs = featureSetUtil.generateNNIActiveFeatureBitSet(nniSubScrFs, pocSystemFs, nniGwFs);
        IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

        int profileId=provXDMServerDAO.getProfileIdForNNISubscriber(profileType,persisterTxn);
        KnBulkNNISubsProfilePersistDTO bulkNNISubsProfilePersistDTO = new KnBulkNNISubsProfilePersistDTO();
        KnNNISubsProfileDTO nniSubsProfile = new KnNNISubsProfileDTO();
        nniSubsProfile.setNniActiveFs(nniActiveFs);
        nniSubsProfile.setProfileId(profileId);
        bulkNNISubsProfilePersistDTO.setMdns(mdns);
        bulkNNISubsProfilePersistDTO.setNniSubsProfile(nniSubsProfile);
        knLogger.debug(methodName, "Creating NNI Subscriber profile ");
        provXDMServerDAO.createNNISubscrProfile(bulkNNISubsProfilePersistDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT : Successfully create NNI Subscriber profile");
    }

    public KnOPSubsProfileInfoDTO selectSubsProfileInfo(List<String> mdnList, boolean readOnly, KnPersisterTxn persistTxn) throws KnProvBOException {
        String methodName = "selectSubsProfileInfo(List<String>,boolean, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO respDTO = null;
        knLogger.debug(methodName, "ENTRY: retrieve subscriber profile info - ", KnGDPRTemplate.mdnList(mdnList));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            respDTO = provXDMServerDAO.selectSubsProfileInfo(mdnList, readOnly, persistTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Subscriber Profile info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get subscriber Info", e);
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", respDTO);
        return respDTO;
    }

    /**
     * method to retrieve the Subscriber Info (data from the PoCSubscrInfo Table)
     *
     * @param mdnList          String
     * @param persisterTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnProvBOException BO entity exception
     */
    public Map<String,String>  fetchActiveFSForBulkMdns(List<String> mdnList, KnPersisterTxn persisterTxn)throws KnProvBOException {
        String methodName = "fetchActiveFsForMdns(List<String> , KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY: fetchActiveFsForMdns - ", KnGDPRTemplate.mdnList(mdnList));
        Map<String,String> activeFsMap = new HashMap<String,String>();
        try {
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            Collection<KnSubsProfileDTO> subsProfileDTOs = xdmServerDAO.fetchSubsSpecificDetailsForBulkMdns(mdnList, false, persisterTxn);
            if(subsProfileDTOs != null && !subsProfileDTOs.isEmpty()){
                subsProfileDTOs.forEach(subsDTO -> {
                    activeFsMap.put(subsDTO.getMdn(), subsDTO.getActiveFS2());
                });
            }
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            knLogger.error(methodName, "Persistence Exception  Occurred - " , e,"Error code",errorCode);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ",  e.getMessage());
            throw new KnProvBOException(e.getErrorCode(), "Failed to retrieve activeFS map for mdn list");

        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(e.getErrorCode(), "Failed retrieve the activeFS for mdn list", e);
        }
        knLogger.debug(methodName, "EXIT : fetchActiveFsForMdns :[ activeFsMap ]",KnGDPRTemplate.mapKeyMdn(activeFsMap));


        return activeFsMap;
    }

    public static byte[] getEncryptedPassword(String password, byte[] salt,  int iterations,  int derivedKeyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength * KnConstants.EIGHT);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return f.generateSecret(spec).getEncoded();
    }

    public KnOPSubsProfileInfoDTO retrieveSubscriberInfoForUFMI(String ufmi, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrieveSubscriberInfoForUFMI(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO respDTO = null;
        knLogger.debug(methodName, "ENTRY: retrieve subscriber profile info  ufmi - ", ufmi);
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            respDTO = provXDMServerDAO.retrieveSubscriberInfoForUFMI(ufmi, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", respDTO);


        return respDTO;
    }

    public KnOPSubsProfileInfoDTO retrieveSubscriberInfoByUserId(String userId, KnPersisterTxn persisterTxn)
            throws KnProvBOException {
        String methodName = "retrieveSubscriberInfoByUserId(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO respDTO = null;
        knLogger.debug(methodName, "ENTRY: retrieve subscriber profile info  userId - ", KnGDPRTemplate.userId(userId));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();
            respDTO = provXDMServerDAO.selectSubscriberProfileByUserId(userId, persisterTxn);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Subscriber Profile info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", respDTO);

        return respDTO;
    }

    public KnOPSubsProfileInfoDTO retrieveSubscriberInfoByAliasMdn(String aliasMdn, KnPersisterTxn persisterTxn)
            throws KnProvBOException {
        String methodName = "retrieveSubscriberInfoByAliasMdn(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO respDTO = null;
        knLogger.debug(methodName, "ENTRY: retrieve subscriber profile info  aliasMdn - ", KnGDPRTemplate.mdn(aliasMdn));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();
            respDTO = provXDMServerDAO.selectSubscriberProfileByAliasMdn(aliasMdn, persisterTxn);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Subscriber Profile info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", respDTO);

        return respDTO;
    }

    public KnOPSubsProfileInfoDTO retrieveSubscriberInfoByMcpttId(String mcpttId, KnPersisterTxn persisterTxn)
            throws KnProvBOException {
        String methodName = "retrieveSubscriberInfoByMcpttId(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO respDTO = null;
        knLogger.debug(methodName, "ENTRY: retrieve subscriber profile info  mcpttId - ", KnGDPRTemplate.mcpttId(mcpttId));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();
            respDTO = provXDMServerDAO.selectSubscriberProfileByMcpttId(mcpttId, persisterTxn);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Subscriber Profile info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", respDTO);

        return respDTO;
    }

    public int computeClientType(Integer subsProfileClientType, int reqClientType) {
        String methodName = "computeClientType()";
        int type = subsProfileClientType;
        List<Integer> pttRadioClients = new ArrayList<>(Arrays.asList(14, 15, 16));
        List<Integer> nonPttRadioClients = new ArrayList<>(Arrays.asList(1, 5, 10));
        knLogger.debug(methodName, "subsProfileClientType - ", subsProfileClientType, ", reqClientType - ", reqClientType);
        switch (reqClientType){
            case 1 :
                if(pttRadioClients.contains(subsProfileClientType)){
                    type = 14;
                }
                break;
            case 10 :
                if(pttRadioClients.contains(subsProfileClientType)){
                    type = 15;
                }
                break;
            case 5 :
                if(pttRadioClients.contains(subsProfileClientType)){
                    type = 16;
                }
                break;
            case 14 :
                if(nonPttRadioClients.contains(subsProfileClientType)){
                    type = 1;
                }
                break;
            case 15 :
                if(nonPttRadioClients.contains(subsProfileClientType)){
                    type = 5;
                }
                break;
            case 16 :
                if(nonPttRadioClients.contains(subsProfileClientType)){
                    type = 10;
                }
                break;
        }
        knLogger.debug(methodName, "returning type - ", type);
        return type;
    }

    /**
     *
     * @param mdn
     * @param eTag
     * @param persisterTxn
     * @throws KnProvBOException
     */
    public void createTGSSDoc(String mdn, long eTag, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "createTGSSDoc(String,long,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: createTGSSDoc mdn - ", KnGDPRTemplate.mdn(mdn));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();
            provXDMServerDAO.createTGSSDoc(mdn, eTag, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.debug(methodName, "EXIT : createTGSSDoc mdn - ", KnGDPRTemplate.mdn(mdn));
    }

    public long getTGSSDocEtag(String mdn,  KnPersisterTxn persisterTxn)throws KnProvBOException {

        String methodName = "getTGSSDocEtag(String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: getTGSSDocEtag mdn - ",KnGDPRTemplate.mdn(mdn));
        long etag = 0;
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();
            etag = provXDMServerDAO.getTGSSDocEtag(mdn,persisterTxn );
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.debug(methodName, "EXIT : getTGSSDocEtag mdn - ", KnGDPRTemplate.mdn(mdn)+" etag - "+etag);
        return etag;
    }

    /**
     *
     * @param mdn
     * @param persisterTxn
     * @throws KnProvBOException
     */
    public void deleteTGSSProfile(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "deleteTGSSProfile(String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: deleteTGSSProfile mdn - ", KnGDPRTemplate.mdn(mdn));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();
            provXDMServerDAO.deleteTGSSDoc(mdn, persisterTxn);
        }catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.debug(methodName, "EXIT : deleteTGSSProfile mdn - ", KnGDPRTemplate.mdn(mdn));
    }

    /**
     *
     * @param mdn
     * @return
     */
    public String generateTGSSListSelUri(String mdn,Boolean isAdd) {
        String methodName = "generateTGSSListSelUri";
        knLogger.debug(methodName, "generating TGSS List uri for Notification for mdn - ", KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(150);
        strBuffer.append("xcap-directory/folder%5B@auid=%22kn-tgss-list%22%5D/entry%5B@uri=%22kn-tgss-list/users/tel:+");
        strBuffer.append(mdn);
        strBuffer.append("/index%22%5D");
        //in remove doc case etag should not be part of notification
        if(isAdd == Boolean.TRUE) {
            strBuffer.append("/@etag");
        }
        knLogger.debug(methodName, "generated TGSS List uri for Notification for mdn - ", KnGDPRTemplate.mdn(mdn), " is - ", strBuffer.toString());

        return  strBuffer.toString();
    }


    public List<KnOPSubsProfileInfoDTO> selectSubscriberProfileByMCSIds(List<String> mcsId, KnPersisterTxn persisterTxn)
            throws KnProvBOException {
        String methodName = "selectSubscriberProfileByMCSIds(List<String>, KnPersisterTxn)";
        List<KnOPSubsProfileInfoDTO> respDTO = new ArrayList<KnOPSubsProfileInfoDTO>();
        knLogger.debug(methodName, "ENTRY: retrieve subscriber profile info  mcsId - ", mcsId);
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();
            respDTO = provXDMServerDAO.selectSubscriberProfileByMCSIds(mcsId, persisterTxn);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Subscriber Profile info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", respDTO);

        return respDTO;
    }

    public Map<String, Map<Integer, String>> retrieveProfileIdMDNsByMcpttIds(List<String> mcpttIds, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnProvBOException {
        String methodName = "retrieveProfileIdMDNsByMcpttIds(String,boolean, KnPersisterTxn)";
        Map<String,Map<Integer,String>> respDTO =null ;
        knLogger.debug(methodName, "ENTRY: retrieve subscriber profileId and Mdns for  mcpttId:", KnGDPRTemplate.mcPttIdList(mcpttIds), " readOnly :", readOnly);
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();
            respDTO = provXDMServerDAO.selectProfileIdMDNsByMcpttIds(mcpttIds, readOnly, persisterTxn);
        } catch (KnPersistenceException e) {
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                knLogger.info(methodName, "Subscriber Profile info not found" );
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Subscriber Profile info not found", e);
            }
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage());
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.debug(methodName, "EXIT : selectProfileIdMDNsByMcpttIds-> ",KnGDPRTemplate.mcpttIdAndProfileMdnMap(respDTO));

        return respDTO;
    }

    public KnOPSubsProfileInfoDTO retrieveSubscriberInfoForUFMIForAssignUserProfile(String ufmi, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrieveSubscriberInfoForUFMIForAssignUserProfile(String, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO respDTO = null;
        knLogger.debug(methodName, "ENTRY: retrieve subscriber profile info  ufmi - ", ufmi);
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            respDTO = provXDMServerDAO.retrieveSubscriberInfoForUFMIForAssignUserProfile(ufmi, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", respDTO);


        return respDTO;
    }

    public String getUserProfileName(int corpId, Integer userProfileIndex)
            throws KnProvBOException {
        String methodName = "getUserProfileName(int, Integer)";
        String userProfileName = null;
        knLogger.debug(methodName, "ENTRY: getUserProfileName - ", userProfileIndex);
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();
            userProfileName = provXDMServerDAO.getProfileName(corpId, userProfileIndex);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage());
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.debug(methodName, "EXIT : getUserProfileName -> ", userProfileName);

        return userProfileName;
    }

    public String getUpmFsBasedOnDefUpmFs(String defaultUpmFs, KnUserProfileFSProvDTO ipUpmFs){
        final String methodName="getUpmFsBasedOnDefUpmFs()";
        knLogger.debug(methodName,"defaultUpmFs :",defaultUpmFs," ipUpmFs :",ipUpmFs);
        KnFeatureSetUtil featureSetUtil=KnFeatureSetUtil.getInstance();
        Map<Integer, Integer> upmFS = new HashMap<>();
        int ptx = featureSetUtil.getFeatureBitValue(defaultUpmFs, PUSHTOTEXT.value()) ? 1 : 0;
        int ptmd = featureSetUtil.getFeatureBitValue(defaultUpmFs, PUSHTOMULTIMEDIA.value()) ? 1 : 0;
        int ptloc = featureSetUtil.getFeatureBitValue(defaultUpmFs, PUSHTOLOCATION.value()) ? 1 : 0;
        int tgsclnt = featureSetUtil.getFeatureBitValue(defaultUpmFs, TLKGRPSCANCLIENT.value()) ? 1 : 0;
        int brdcrmb = featureSetUtil.getFeatureBitValue(defaultUpmFs, BREADCUMB.value()) ? 1 : 0;
        int geofnc = featureSetUtil.getFeatureBitValue(defaultUpmFs, GEOFENCEFEATURE.value()) ? 1 : 0;
        int ambientListening = featureSetUtil.getFeatureBitValue(defaultUpmFs, AMBIENTLISTENING.value()) ? 1 : 0;
        int discreteListening = featureSetUtil.getFeatureBitValue(defaultUpmFs, DISCRETELISTENING.value()) ? 1 : 0;
        int userCheck = featureSetUtil.getFeatureBitValue(defaultUpmFs, USERCHECK.value()) ? 1 : 0;
        int userEnable = featureSetUtil.getFeatureBitValue(defaultUpmFs, USERENABLEDISABLE.value()) ? 1 : 0;
        int locPublish = featureSetUtil.getFeatureBitValue(defaultUpmFs, ONDEMLOCATION.value()) ? 1 : 0;
        int mcVideoTx = featureSetUtil.getFeatureBitValue(defaultUpmFs, MCVIDEOTX.value()) ? 1 : 0;
        int mcVideoRx = featureSetUtil.getFeatureBitValue(defaultUpmFs, MCVIDEORX.value()) ? 1 : 0;
        int mcVideoGroupRx = featureSetUtil.getFeatureBitValue(defaultUpmFs, MCVIDEOGROUPRX.value()) ? 1 : 0;
        int mcVideoConfirmedPull = featureSetUtil.getFeatureBitValue(defaultUpmFs, MCVIDEOCONFIRMEDPULL.value()) ? 1 : 0;
        int osm = featureSetUtil.getFeatureBitValue(defaultUpmFs, OPERATIONALSTATUSMESSAGING.value()) ? 1 : 0;
        int emergency = featureSetUtil.getFeatureBitValue(defaultUpmFs, EMERGENCY.value()) ? 1 : 0;
        int selfDnDPrivilege = featureSetUtil.getFeatureBitValue(defaultUpmFs, SELF_DND_PRIVILEGE.value()) ? 1 : 0;

        upmFS.put(PUSHTOTEXT.value(),ipUpmFs.getPtx()!=null?Integer.valueOf(ipUpmFs.getPtx()):ptx);
        upmFS.put(PUSHTOMULTIMEDIA.value(),ipUpmFs.getPtmd()!=null?Integer.valueOf(ipUpmFs.getPtmd()):ptmd);
        upmFS.put(MCDATA_SDS_FEATURE.value(), ipUpmFs.getPtx() != null ? Integer.valueOf(ipUpmFs.getPtx()) : ptx);
        upmFS.put(MCDATA_FD_FEATURE.value(), ipUpmFs.getPtmd() != null ? Integer.valueOf(ipUpmFs.getPtmd()) : ptmd);
        upmFS.put(PUSHTOLOCATION.value(),ipUpmFs.getPtloc()!=null?Integer.valueOf(ipUpmFs.getPtloc()):ptloc);
        upmFS.put(TLKGRPSCANCLIENT.value(),ipUpmFs.getTgsclnt()!=null?Integer.valueOf(ipUpmFs.getTgsclnt()):tgsclnt);
        upmFS.put(BREADCUMB.value(),ipUpmFs.getBrdcrmb()!=null?Integer.valueOf(ipUpmFs.getBrdcrmb()):brdcrmb);
        upmFS.put(GEOFENCEFEATURE.value(),ipUpmFs.getGeofnc()!=null?Integer.valueOf(ipUpmFs.getGeofnc()):geofnc);
        upmFS.put(AMBIENTLISTENING.value(),ipUpmFs.getAmbientListening()!=null?Integer.valueOf(ipUpmFs.getAmbientListening()):ambientListening);
        upmFS.put(DISCRETELISTENING.value(),ipUpmFs.getDiscreteListening()!=null?Integer.valueOf(ipUpmFs.getDiscreteListening()):discreteListening);
        upmFS.put(USERCHECK.value(),ipUpmFs.getUserCheck()!=null?Integer.valueOf(ipUpmFs.getUserCheck()):userCheck);
        upmFS.put(USERENABLEDISABLE.value(),userEnable);
        upmFS.put(ONDEMLOCATION.value(),ipUpmFs.getLocPublish()!=null?Integer.valueOf(ipUpmFs.getLocPublish()):locPublish);
        upmFS.put(MCVIDEOTX.value(),ipUpmFs.getMcVideoTx()!=null?Integer.valueOf(ipUpmFs.getMcVideoTx()):mcVideoTx);
        upmFS.put(MCVIDEORX.value(),ipUpmFs.getMcVideoRx()!=null?Integer.valueOf(ipUpmFs.getMcVideoRx()):mcVideoRx);
        upmFS.put(MCVIDEOGROUPRX.value(),ipUpmFs.getMcVideoGroupRx()!=null?Integer.valueOf(ipUpmFs.getMcVideoGroupRx()):mcVideoGroupRx);
        upmFS.put(MCVIDEOCONFIRMEDPULL.value(),ipUpmFs.getMcVideoConfirmedPull()!=null?Integer.valueOf(ipUpmFs.getMcVideoConfirmedPull()):mcVideoConfirmedPull);
        upmFS.put(OPERATIONALSTATUSMESSAGING.value(),ipUpmFs.getOsm()!=null?Integer.valueOf(ipUpmFs.getOsm()):osm);
        upmFS.put(EMERGENCY.value(),ipUpmFs.getEmergency()!=null?Integer.valueOf(ipUpmFs.getEmergency()):emergency);
        upmFS.put(SELF_DND_PRIVILEGE.value(), ipUpmFs.getSelfDnDPrivilege() != null ? Integer.valueOf(ipUpmFs.getSelfDnDPrivilege()) : selfDnDPrivilege);

        knLogger.debug("upmFS.entrySet() ",upmFS.entrySet());

        BitSet upmDefFs = featureSetUtil.convertHexStringToBitSet(defaultUpmFs);
        for(Map.Entry<Integer,Integer> provEntry: upmFS.entrySet()){
            if(provEntry.getValue()== 1){
                upmDefFs.set(provEntry.getKey());
            }else{
                upmDefFs.clear(provEntry.getKey());
            }
        }
        String upmHex = convertBitSetToHexString(upmDefFs);
        knLogger.debug(methodName,"Exit upmHex ",upmHex);
        return upmHex;
    }

    public static List<KnDocChangeListDTO> buildMCSDOC(String mcId
            , String mdn, String upmIndex, String newEtag, String previousEtag, int mcsCompliance, String exists) {
        List<KnDocChangeListDTO> docList = new ArrayList<>();
        if (mcsCompliance == 0) {
            //for kodiak clients
            for (Map.Entry auid : auidMap.entrySet()) {
                StringBuilder mcsDocUri = new StringBuilder();
                mcsDocUri.append(auid.getValue())
                        .append("/users/")
                        .append(mcId)
                        .append("/")
                        .append(auid.getKey())
                        .append("-user-profile-")
                        .append(upmIndex)
                        .append(".xml");
                if(exists != null && exists.equals("0")){
                    docList.add(new KnDocChangeListDTO(mdn, mcsDocUri.toString(), null, null,"0"));
                }else{
                    docList.add(new KnDocChangeListDTO(mdn, mcsDocUri.toString(), newEtag, previousEtag,null));
                }
            }
        } else {
            for (Map.Entry auid : auidMap.entrySet()) {
                StringBuilder mcsDocUri = new StringBuilder();
                mcsDocUri.append(auid.getValue())
                        .append("/users/")
                        .append(mcId)
                        .append("/user-profile-")
                        .append(upmIndex)
                        .append(".xml");
                if(exists != null && exists.equals("0")){
                    docList.add(new KnDocChangeListDTO(mdn, mcsDocUri.toString(), null, null,"0"));
                }else{
                    docList.add(new KnDocChangeListDTO(mdn, mcsDocUri.toString(), newEtag, previousEtag,null));
                }
            }
        }
        return docList;
    }

    public Map<String,String> calculateActiveFS2WithUPMFS(int subscriptionType,String pocPttId,
                                                          String presencePttId,String xdmsPttId,String clientFS2
            , String subsFS2, String corpFS2, String opsFS2, String corpAdminFS2, String clientCapOverrideBitMask,
                                                          Map<String, KnOPSubsProfileInfoDTO> mdnUpmFsMap) {
        String methodName = "calculateActiveFS2WithUPMFS()";
        String activeFS2 = null;
        Map<String, String> mdnActiveFsMap = new HashMap<>();
        int CORPORATE_TYPE_SUBS = 1;
        knLogger.debug(methodName, "mdnUpmFsMap :", KnGDPRTemplate.mapKeyMdn(mdnUpmFsMap), " subscriptionType ", subscriptionType, "pocPttId ",
                pocPttId);

        try {
            for (Entry mdnFsMap : mdnUpmFsMap.entrySet()) {
                KnOPSubsProfileInfoDTO value = (KnOPSubsProfileInfoDTO) mdnFsMap.getValue();
                if (subscriptionType == CORPORATE_TYPE_SUBS) {
                    activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2,
                            corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask, value.getXdmsFS2(),
                            value.getUserProfileFS2());
                } else {
                    activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2, opsFS2,
                            clientCapOverrideBitMask, value.getXdmsFS2(), value.getUserProfileFS2());
                }
                mdnActiveFsMap.put(mdnFsMap.getKey().toString(), activeFS2);
            }

        } catch (KnFeatureSetException e) {
            knLogger.error(methodName, "Exception occured :", e.getMessage());
        }
        knLogger.debug(methodName, " mdnActiveFsMap : ", KnGDPRTemplate.mapKeyMdn(mdnActiveFsMap));
        return mdnActiveFsMap;
    }

    public KnOPSubsProfileInfoDTO selectUserProfileIdsByProfileMdns(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "selectUserProfileIdsByProfileMdns(List<String>, KnPersisterTxn)";
        KnOPSubsProfileInfoDTO respDTO = null;
        knLogger.debug(methodName, "ENTRY: mdn- ", KnGDPRTemplate.mdnList(mdnList));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            respDTO = provXDMServerDAO.selectUserProfileIdsByProfileMdns(mdnList, persisterTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        }catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), "failed to retrieve user profileIds by profile mdns ");
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "failed to retrieve user profileIds by profile mdns", e);
        }
        knLogger.debug(methodName, "EXIT : retrieve user profileIds -> ", respDTO);
        return respDTO;
    }

    public KnXDMSubsProfileRespDTO getSubscrClientSettings(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnProvBOException{
        String methodName = "getSubscrClientSettings(mdn , boolean, KnPersisterTxn)";
        KnXDMSubsProfileRespDTO respDTO= new KnXDMSubsProfileRespDTO();
        knLogger.debug(methodName,"ENTRY : mdn " , KnGDPRTemplate.mdn(mdn) );
        KnOPSubsProfileInfoDTO subsProfileInfoDTO = new KnOPSubsProfileInfoDTO();
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            subsProfileInfoDTO = provXDMServerDAO.getSubscrClientSettings(mdn, readOnly, persisterTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        }catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(e.getErrorCode(), "failed to retrieve Recording Status ");
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "failed to retrieve Recording Status", e);
        }
        respDTO.setMdn(mdn);
        respDTO.setRecordingStatus(subsProfileInfoDTO.getRecordingStatus());
        knLogger.debug(methodName, "EXIT : retrieve Recording Status -> ", respDTO);
        return respDTO;
    }

    /**
     * method to retrieve the Subscriber Info (data from the PoCSubscrInfo Table)
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO
     * @throws KnProvBOException BO entity exception
     */
    public KnOPSubsAddlInfoProfileDTO setSubscrClientAddlInfo(KnSubsAddlInfoPersistDTO mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrieveSubscrAddlInfo(String, KnPersisterTxn)";
        KnOPSubsAddlInfoProfileDTO respDTO = null;
        knLogger.debug(methodName, "ENTRY: retrieve subscriber Addlinfo profile info - ", KnGDPRTemplate.mdn(mdn.getMdns().get(0)));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            respDTO = provXDMServerDAO.setSubscrClientAddlInfo(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception  Occurred - ", e);
            throw new KnProvBOException(e.getErrorCode(), "Operation failed : ", e);
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber AddlInfo Profile Info -> ", respDTO);


        return respDTO;
    }

    public boolean checkAliasInfoCombinationExist(List<KnSubsAliasInfoDTO> inputList,  Map<String, List<KnSubsAliasInfoDTO>> aliasIdInfoMap)
    {
        knLogger.debug("Enter checkAliasInfoCombinationExist(List<KnSubsAliasInfoDTO> inputList,  Map<String, List<KnSubsAliasInfoDTO>> aliasIdInfoMap)");
        Set<String> mdns=aliasIdInfoMap.keySet();
        for(String mdn:mdns)
        {
            List<KnSubsAliasInfoDTO> aliasInfolist=aliasIdInfoMap.get(mdn);
            for(KnSubsAliasInfoDTO aliasInfoexst:aliasInfolist)
            {
                for(KnSubsAliasInfoDTO newAlias:inputList)
                {
                    if(newAlias.equals(aliasInfoexst))
                        return true;

                }
            }

        }
        knLogger.debug("EXIT :: checkAliasInfoCombinationExist(List<KnSubsAliasInfoDTO> inputList,  Map<String, List<KnSubsAliasInfoDTO>> aliasIdInfoMap)");
        return false;


    }

    public boolean checkAliasInfoCombinationExist(String mdn ,List<KnSubsAliasInfoDTO> inputList,  Map<String, List<KnSubsAliasInfoDTO>> aliasIdInfoMap)
    {
        knLogger.debug("Enter checkAliasInfoCombinationExist(List<KnSubsAliasInfoDTO> inputList,  Map<String, List<KnSubsAliasInfoDTO>> aliasIdInfoMap)");
        Set<String> mdns=aliasIdInfoMap.keySet();
        for(String mdnmap:mdns)
        {
            List<KnSubsAliasInfoDTO> aliasInfolist=aliasIdInfoMap.get(mdn);
            for(KnSubsAliasInfoDTO aliasInfoexst:aliasInfolist)
            {
                for(KnSubsAliasInfoDTO newAlias:inputList)
                {
                    if(newAlias.equals(aliasInfoexst) && mdnmap.equals(mdn))
                        return true;

                }
            }

        }
        knLogger.debug("EXIT :: checkAliasInfoCombinationExist(List<KnSubsAliasInfoDTO> inputList,  Map<String, List<KnSubsAliasInfoDTO>> aliasIdInfoMap)");
        return false;


    }

    public Map<String, List<Integer>> retriveUpmIdGroupMapByUserProfileId(List<String> userProfileIdList, KnPersisterTxn persisterTxn)
            throws KnProvBOException {
        return retriveUpmIdGroupMapByUserProfileId(userProfileIdList, false, persisterTxn);
    }

    public Map<String, List<Integer>> retriveUpmIdGroupMapByUserProfileId(List<String> userProfileIdList, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnProvBOException {
        String methodName = "retriveUpmIdGroupMapByUserProfileId()";
        try {
            return profileHandler.retriveUpmIdGroupMapByUserProfileId(userProfileIdList, readOnly, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile!");
            throw new KnProvBOException(e.getErrorCode(), "Operation failed : ", e);
        }
    }

    public Map<String,String> getBaseMdnListForRequestingMdnList(List<String> mdnList, KnPersisterTxn persistTxn) throws KnProvBOException {
        String methodName = "getBaseMdnListForRequestingMdnList(List<String>, KnPersisterTxn)";
        Map<String,String> respDTO = null;
        knLogger.debug(methodName, "ENTRY: - ", KnGDPRTemplate.mdnList(mdnList));
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            respDTO = provXDMServerDAO.getBaseMdnListForRequestingMdnList(mdnList, persistTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND,
                        "Subscriber Profile info not found", e);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get BaseMdnListForRequestingMdnList", e);
        }
        knLogger.debug(methodName, "EXIT : retrieve BaseMdnListForRequestingMdnList Info -> ", respDTO);
        return respDTO;
    }

    public KnCorporateProfilepersistDTO1 selectCorpID(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "selectCorpID(String, KnPersisterTxn)";
        KnCorporateProfilepersistDTO1 profilepersistDTO = new KnCorporateProfilepersistDTO1();
        String errorCode;
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            profilepersistDTO = provXDMServerDAO.getCorpID(extCorpId, persisterTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnException e) {
            knLogger.error(methodName, "DAO Exception Occurred - " + e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CORP_PROFILE,
                    "Corp Profile info not found", e);
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", profilepersistDTO);
        return profilepersistDTO;
    }

    public KnCorporateProfilepersistDTO1 selectCorporateAccountDetails(int CorpId, KnPersisterTxn persisterTxn) throws
            KnProvBOException {
        String methodName = "selectCorporateAccountDetails(String, KnPersisterTxn)";
        KnCorporateProfilepersistDTO1 profilepersistDTO = new KnCorporateProfilepersistDTO1();
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            profilepersistDTO = provXDMServerDAO.getCorporateAccountDetails(CorpId, persisterTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - " + e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - " + e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CORP_PROFILE,
                        "Corp Profile info not found", e);
            }
        }  catch (KnException e) {
            knLogger.error(methodName, "Exception - " + e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get subscriber Info", e);
        }
        knLogger.debug(methodName, "EXIT : retrieve Subscriber Profile Info -> ", profilepersistDTO);
        return profilepersistDTO;
    }

    public KnXDMCorpAccountsListDTO retrieveCorporationAccountsList(KnPersisterTxn persisterTxn,String fetchSize, String nextToken) throws
            KnDAOException, KnProvException {
        String methodName = "retrieveCorporationAccountsList";
        knLogger.debug(methodName, "ENTRY: Retrieving Corporation Accounts List - ");
        KnXDMCorpAccountsListDTO resp = null;
        try {
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            resp = xdmServerDAO.retrieveCorporationAccountsList(null,fetchSize,nextToken);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            knLogger.error(methodName, "Persistence Exception with error code - ", errorCode);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to retrieve Corporation Accounts List", e);
        }
        knLogger.debug(methodName, "EXIT : retrieved Corporation Accounts List");

        return resp;
    }

    /**
     * This method returns the poc home for corporate or subscriber based on load balanced
     *
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    public String getSubsPoCHome(KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getSubsPoCHome(KnPersisterTxn)";
        knLogger.debug(methodName, "get POC Home for corporate ");
        String pocHome = null;
        //get the Type of partitioning.
        try {
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            //get the list of POC Ptt Server Ids
            KnConfigurationsManager confManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = confManager.getCacheManager();
            Map<String, List<String>> pttServerInfo = (Map<String, List<String>>) cacheManager.get(KnProvCacheKeys.PTT_SERVER_INFO);
            knLogger.debug(methodName, "PTTSERVER INFO is", pttServerInfo);
            if (null == pttServerInfo) {
                knLogger.info(methodName, "Retrieving Pttserver info from DB");
                pttServerInfo = xdmServerDAO.retrievePttServerIds(persisterTxn);
                cacheManager.put(KnProvCacheKeys.PTT_SERVER_INFO, pttServerInfo);
            }
            //check if the Prov is allowed for the POCs and add only those into the list whose POC is in ALLOW_PROV mode
            knLogger.debug(methodName, "PTTSERVER INFO ", pttServerInfo);
            List<String> pocPttIdList = pttServerInfo.get(KnProvConstants.POC_PTT_ID_KEY);
            List<String> pocPttIds = new ArrayList<>();
            pocPttIds.addAll(pocPttIdList);
            knLogger.debug(methodName, "POC PTT IDS ", pocPttIds);
            Map<String, KnPocSubsCapConfigDTO> pocSubsCapConfigMap =
                    getPocSubsCapacityConfig(pocPttIds, persisterTxn);
            for (Map.Entry<String, KnPocSubsCapConfigDTO> entrySet : pocSubsCapConfigMap.entrySet()) {
                String pttServerId = entrySet.getKey();
                KnPocSubsCapConfigDTO pocSubsCapConfigDTO = entrySet.getValue();
                if (null != pocSubsCapConfigDTO && KnProvConstants.ALLOW_POC_PROV.ALLOW_PROV.value() != pocSubsCapConfigDTO.getAllowProv()) {
                    knLogger.debug(methodName, "not ALLOW_PROV ", pttServerId);
                    pocPttIds.remove(pttServerId);
                }
            }
            knLogger.debug(methodName, "POC PTT IDS after subs cap config ", pocPttIds);
            if (pocPttIds.isEmpty()) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC,
                        "MAX limit for all the POC Home has been reached");
            } else {
                // get the subscriber count.
                Map<String, Integer> subsCntPerServer = xdmServerDAO.getSubsCount(pocPttIds, persisterTxn);
                knLogger.debug(methodName, "Get server home based on Loading factor");
                pocHome = calcLoadingFactor(subsCntPerServer, pocSubsCapConfigMap);
            }
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.POC_SERVER_MAP_NOT_FOUND, "POC Server info not found", e);
            }
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "Prov BO Exception ", e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get ptt Server info", e);
        }
        knLogger.debug(methodName, "EXIT: Poc Home for the mdn is ", pocHome);
        return pocHome;
    }

    public boolean isDeleteCorporateProfile(int corpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "isDeleteCorporateProfile(int , KnPersisterTxn)";
        boolean isDeleteCorporateProfile = false;
        try{
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            int corpSubsCount = provXDMServerDAO.retrieveCorpSubscriberCount(corpId, persisterTxn);
            int corpProfileCleanUp = provXDMServerDAO.retrieveCorpProfileCleanUp(corpId, persisterTxn);
            int deviceCount = genInfoUtil.getDeviceCountForCorpId(corpId, persisterTxn);
            knLogger.info(methodName, "corp Subscriber count - ", corpSubsCount, " corpProfileCleanUp :", corpProfileCleanUp, "deviceCount", deviceCount);
            if (corpSubsCount == 0 && corpProfileCleanUp != 1 && deviceCount == 0) {
                isDeleteCorporateProfile = true;
            }
        }catch (KnPersistenceException e) {
            knLogger.error(methodName, "Persistence Exception  Occurred - ", e);
            String errorCode = e.getErrorCode();
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(errorCode)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.POC_SERVER_MAP_NOT_FOUND, "POC Server info not found", e);
            }
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "Prov BO Exception ", e);
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get ptt Server info", e);
        }
        knLogger.exit(methodName, "isDeleteCorporateProfile", isDeleteCorporateProfile);
        return isDeleteCorporateProfile;
    }
    /**
     * Method to retrieve the Corporation Subscriber Count with profile mdn
     *
     * @param corpId       int
     * @param persisterTxn KnPersisterTxn
     * @return corp Subscriber Count of the corporation Id
     * @throws KnProvBOException Exception
     */
    public int retrieveCorpSubscriberWithProfileCnt(int corpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrieveCorpSubsCount(int)";
        int corpSubsCount = -1;
        knLogger.debug(methodName, "ENTRY : get Corporation Subs Count for corp id - ", corpId);
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            corpSubsCount = provXDMServerDAO.retrieveCorpSubscriberWithProfileCnt(corpId, persisterTxn);
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
            knLogger.error(methodName, e);

            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed while retrieving corp Subs Count", e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed while retrieving corp Subs Count", e);
        }
        knLogger.debug(methodName, "EXIT : Corporation Subs Count for corp id [", corpId, "]-> ", corpSubsCount);

        return corpSubsCount;
    }
    public static boolean validateExtGatewayId(String extGatewayId,KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "validateExtGatewayId(String)";
        boolean isValid = false;
        try {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            KnExtGatewayInfoDTO knExtGatewayInfoDTO = provXDMServerDAO.getExtGatewayDetails(extGatewayId, persisterTxn);
            if (null != knExtGatewayInfoDTO) {
                isValid = true;
            }
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed while retrieving external gateway info", e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed while retrieving external gateway info", e);
        }
        return isValid;
    }

    public void updateIdmAndOidcProfilesForChangeMdnAPI(String oldMdn, String newMdn, KnOPSubsProfileInfoDTO oldSubsProfileInfoDTO, KnSubsProfilePersistDTO subsProfilePersistDTO,
                                                        String sipRandomPwdPlain, KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "updateIdmAndOidcProfilesForChangeMdnAPI()";
        knLogger.debug(methodName, "ENTRY :");
        knLogger.debug(" oldMdn ", oldMdn, " newMdn ", newMdn, " oldSubsProfileInfoDTO ", oldSubsProfileInfoDTO, " subsProfilePersistDTO ", subsProfilePersistDTO);
        knLogger.debug(methodName, "ENTRY :");
        String idmFqdn = genInfoUtil.getIDMInternalFqdn(persisterTxn);
        int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
        Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
        String deviceSharingFlag = microServicesParamNameValueMap.get(DEVICE_SHARING_FEATURE_FLAG);
        String pwdExpiry = microServicesParamNameValueMap.get(TEMP_PASSWORD_EXPIRY);
        knLogger.info(methodName, "deviceSharingFlag", deviceSharingFlag);

        String appId = oldSubsProfileInfoDTO.getDispatchType() == DISPATCH_TYPE_WEB ? APP_ID.DISPATCHER.value() : APP_ID.HANDSET_STANDARD.value();
        if (KnConstants.MCSCOMPLIANCE == oldSubsProfileInfoDTO.getMcpttCompliance()) {
            appId = APP_ID.USERMCSCLIENTS.value();
            pwdExpiry = microServicesParamNameValueMap.get(com.kodiak.common.resources.KnConstants.MCS_TEMP_PASSWORD_EXPIRY);
        }
        KnGeneralCacheUtil generalCacheUtil = KnGeneralCacheUtil.getInstance();
        boolean isOIDCApplicable = generalCacheUtil.isOIDCApplicable(oldSubsProfileInfoDTO.getCorpId(), WEBDISPATCHER);
        boolean oidcProfileExists = true;
        boolean idmProfileExists = false;
        KnXDMSubsAliasDetailsRespDTO oidcRespDto = new KnXDMSubsAliasDetailsRespDTO();
        KnCorpSubsUserDetailsRespDTO idmRespDto = new KnCorpSubsUserDetailsRespDTO();
        String oldUserIdForOIDC = null;
        String newUserIdForOIDC = null;
        if (oldSubsProfileInfoDTO.getAliasMdn() != null) {
            oldUserIdForOIDC = oldSubsProfileInfoDTO.getAliasMdn();
        } else {
            oldUserIdForOIDC = oldMdn;
        }
        String aliasMdn = subsProfilePersistDTO!=null?subsProfilePersistDTO.getAliasMdn():oldSubsProfileInfoDTO.getAliasMdn();
        if (aliasMdn != null) {
            newUserIdForOIDC = aliasMdn;
        } else {
            newUserIdForOIDC = newMdn;
        }
        if ((oldSubsProfileInfoDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_13) || (oldSubsProfileInfoDTO.getClientPVmajorVer() == 0 && isOIDCApplicable)) {
            KnXDMSubsAliasDetailsReqDTO oidcSubsDto = new KnXDMSubsAliasDetailsReqDTO();
            oidcSubsDto.setUserid(oldUserIdForOIDC);
            oidcRespDto = KnManageSyncUserProfileUtil.getInstance()
                    .notifyIDMIntfForOIDCMgmt(oidcSubsDto, appId, idmFqdn, null, HttpMethod.GET);
            if ((oldSubsProfileInfoDTO.getClientPVmajorVer() == 0 && isOIDCApplicable) && HttpStatus.NOT_FOUND.equals(oidcRespDto.getStatusCode())) {
                KnXDMCorpUserDetailsRespDTO idmSubscriberDTO = new KnXDMCorpUserDetailsRespDTO();
                idmSubscriberDTO.setUserId(oldSubsProfileInfoDTO.getUserId());
                knLogger.debug(methodName, "idmSubscriberDTO - ", idmSubscriberDTO, "idmFqdn - ", idmFqdn);
                idmRespDto = KnManageSyncUserProfileUtil.getInstance().notifyIDMIntf(idmSubscriberDTO, idmFqdn, null, HttpMethod.GET);
                if (HttpStatus.OK.equals(idmRespDto.getStatusCode())) {
                    oidcProfileExists = false;
                    idmProfileExists = true;
                }
            }
        } else if ((oldSubsProfileInfoDTO.getClientPVmajorVer() != 0 && oldSubsProfileInfoDTO.getClientPVmajorVer() < PROTOCOL_VERSION_13)
                || (oldSubsProfileInfoDTO.getClientPVmajorVer() == 0 && !isOIDCApplicable)) {
            KnXDMCorpUserDetailsRespDTO idmSubscriberDTO = new KnXDMCorpUserDetailsRespDTO();
            idmSubscriberDTO.setUserId(oldSubsProfileInfoDTO.getUserId());
            knLogger.debug(methodName, "idmSubscriberDTO - ", idmSubscriberDTO, "idmFqdn - ", idmFqdn);
            idmRespDto = KnManageSyncUserProfileUtil.getInstance().notifyIDMIntf(idmSubscriberDTO, idmFqdn, null, HttpMethod.GET);
            if (HttpStatus.OK.equals(idmRespDto.getStatusCode())) {
                oidcProfileExists = false;
                idmProfileExists = true;
            }
        }

        if (oidcProfileExists) {
            KnXDMSubsAliasDetailsReqDTO oidcUserProfile = new KnXDMSubsAliasDetailsReqDTO();
            oidcUserProfile.setUserid(newUserIdForOIDC);
            oidcUserProfile.setEmail(oidcRespDto.getEmail());
            List<String> actions = new ArrayList<>();
            Map<String, Object> oidcAttributes = new HashMap<>();
            actions.add(TMP_PWD_MODE.PASSWORD_INFO_MAIL.value());
            actions.add(TMP_PWD_MODE.PASSWORD_INFO_SMS.value());
            oidcAttributes.put(ACTIONS, actions);
            String mdn=subsProfilePersistDTO!=null?subsProfilePersistDTO.getMdn():oldSubsProfileInfoDTO.getMdn();
            oidcAttributes.put(MCPTT_ID_OIDC, mdn);
            String mcVideoId = subsProfilePersistDTO!=null?subsProfilePersistDTO.getMcVideoId():oldSubsProfileInfoDTO.getMcVideoId();;
            oidcAttributes.put(MCVIDEO_ID_OIDC, mcVideoId);
            String mcDataId = subsProfilePersistDTO!=null?subsProfilePersistDTO.getMcDataId():oldSubsProfileInfoDTO.getMcDataId();
            oidcAttributes.put(MCDATA_ID_OIDC, mcDataId);
            oidcUserProfile.setAttributes(oidcAttributes);
            oidcUserProfile.setTemppwd(Boolean.FALSE);
            if(oldSubsProfileInfoDTO.getMcpttCompliance() == MCPTT_COMPLIANCE_ENABLED){
                String xdmPttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
                KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                String realm = xdmsServiceConfigDTO.getAuthRealm();
                knLogger.debug(methodName, "realm ", realm);
                String sipdigestpwd = KnGeneralUtil.generateMD5(newUserIdForOIDC.concat(DELIM).concat(realm).concat(DELIM).concat(sipRandomPwdPlain));
                knLogger.debug(methodName, "generated MD5 ", sipdigestpwd);
                String deviceImpl = KnConstants.TEL_URI_TEMPLATE+newUserIdForOIDC;
                String deviceImpu = genInfoUtil.getDeviceImpl(deviceImpl,persisterTxn);
                oidcAttributes.put(MCPTT_ID_OIDC, subsProfilePersistDTO.getMcpttId());
                oidcAttributes.put(MC_ID_OIDC,subsProfilePersistDTO.getMcId() );
                oidcAttributes.put(NETWORK_NAME,subsProfilePersistDTO.getNetworkName());
                oidcAttributes.put(DIGEST_PWD_OIDC,sipRandomPwdPlain);
                oidcAttributes.put(DEVICEIMPL_OIDC,deviceImpl);
                oidcAttributes.put(DEVICEIMPU_OIDC,deviceImpu);
                oidcAttributes.put(MCS_SCOPES_OIDC,KnGeneralUtil.populateOidcScopes(subsProfilePersistDTO.getActiveFS2()));
                knLogger.debug(methodName, "updating digest ", newUserIdForOIDC);
                genInfoUtil.updateSipDigestPwd(newUserIdForOIDC, sipdigestpwd, persisterTxn);
            }
            int passwordExpiry = 0;

            if (pwdExpiry != null) passwordExpiry = Integer.parseInt(pwdExpiry);
            long currentTimeInMilliSecond = System.currentTimeMillis();
            long pwdExpiryInMilli = 0l;
            KnGeneralPasswordUtil pwdUtil = KnGeneralPasswordUtil.getInstance();
            sipRandomPwdPlain = pwdUtil.generatePassword(sipRandomPwdPlain, appId);
            if (sipRandomPwdPlain != null && (oldSubsProfileInfoDTO.getClientPVmajorVer() > PROTOCOL_VERSION_13 || oldSubsProfileInfoDTO.getClientPVmajorVer() == 0)) {
                pwdExpiryInMilli = currentTimeInMilliSecond + (1000 * 60 * passwordExpiry);
                generalCacheUtil.deleteOldOidcTmpPwd(oidcUserProfile.getUserid());
                KnOidcTmpPwdDTO oidcTmpPwdDTO = new KnOidcTmpPwdDTO();
                KnEncryptionDecryptionUtil encryptionDecryptionUtil = KnEncryptionDecryptionUtil.getInstance();
                String encryptedPwd = encryptionDecryptionUtil.encrypt(oidcUserProfile.getUserid(), sipRandomPwdPlain);
                String derivedKey = KnGeneralUtil.convertToHex(encryptedPwd.getBytes());
                knLogger.debug(methodName, "Derived key after generation and convertion: ", derivedKey);
                oidcTmpPwdDTO.setTmpPwd(KnGeneralUtil.convertHexToAscii(derivedKey));
                oidcTmpPwdDTO.setMdn(oidcUserProfile.getUserid());
                oidcTmpPwdDTO.setTmpPwdCreationTS(String.valueOf(currentTimeInMilliSecond));
                oidcTmpPwdDTO.setTmpPwdExpiry(String.valueOf(pwdExpiryInMilli));
                generalCacheUtil.insertOidcTmpPwd(oidcTmpPwdDTO);
            }
            oidcUserProfile.setPwd(sipRandomPwdPlain);
            if(oldSubsProfileInfoDTO.getLicenseType() == ENABLED){
                oidcUserProfile.setTemppwd(Boolean.TRUE);
                oidcUserProfile.setGeneratepwd(sipRandomPwdPlain == null);
                oidcUserProfile.setPwdexpiry(pwdExpiryInMilli);
                knLogger.debug(methodName, "oidcUserProfile - ", oidcUserProfile, "idmFqdn - ", idmFqdn);
            }else{
                knLogger.debug(methodName, "not license type oidcUserProfile - ", oidcUserProfile, "idmFqdn - ", idmFqdn);
                oidcUserProfile.setUserid(newUserIdForOIDC);
                oidcUserProfile.setGeneratepwd(sipRandomPwdPlain == null);
                oidcUserProfile.setTemppwd(false);
            }


            KnXDMSubsAliasDetailsRespDTO syncResponseDTO = KnManageSyncUserProfileUtil.getInstance().createIDMUserForOIDCMgmt(oidcUserProfile, appId,
                    idmFqdn, true, oldUserIdForOIDC);
            if (com.kodiak.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == syncResponseDTO.getStatus()) {
                knLogger.error(methodName, "Error for updating profile in OidcIDM - ", syncResponseDTO.getStatus());
                throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while change MDN of Subscriber");
            }
        }
        if (idmProfileExists) {
            KnXDMCorpUserDetailsRespDTO idmSubscriberDTO = new KnXDMCorpUserDetailsRespDTO();
            String userId = subsProfilePersistDTO!=null?subsProfilePersistDTO.getUserId():oldSubsProfileInfoDTO.getUserId();
            idmSubscriberDTO.setUserId(userId);
            idmSubscriberDTO.setUserType(com.kodiak.common.resources.KnConstants.OTHER);
            idmSubscriberDTO.setCorpId(oldSubsProfileInfoDTO.getExtCorpId());
            idmSubscriberDTO.setMdn(newUserIdForOIDC);
            KnCorpSubsUserDetailsRespDTO syncResponseDTO = KnManageSyncUserProfileUtil.getInstance().createIDMUser(idmSubscriberDTO,
                    idmFqdn, true, oldSubsProfileInfoDTO.getUserId());
            if (syncResponseDTO.getStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                knLogger.error(methodName, "Error for updating profile in IDM - ", syncResponseDTO.getStatus());
                throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while change MDN of Subscriber");
            }
        }
    }
    public KnXDMExtGWProfileListDTO retrieveExtGWProfileList() throws KnDAOException, KnProvException {
        String methodName = "retrieveExtGWProfileList";
        knLogger.debug(methodName, "ENTRY: Retrieving External GW Profile List - ");
        KnXDMExtGWProfileListDTO resp = null;
        try {
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            resp = xdmServerDAO.retrieveExtGWProfileList();
        } catch (KnConnectionException e) {
            knLogger.error(methodName, "Connection Exception  Occurred - ", e);
            throw new KnProvException(KnErrorCodes.DAO.CONNECTION_FAILED, "Operation failed due to connection error : ", e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred - ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed while retrieving external gateway profile list", e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception - ", e.getMessage());
            throw new KnProvException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to retrieve External GW Profile List", e);
        }
        knLogger.debug(methodName, "EXIT : retrieved External GW Profile List");

        return resp;
    }

    /**
     * Assigns zones and channels for a subscriber based on client type, auto-assign settings, and group information.
     * Skips processing if auto-assign is disabled, no groups are found, or the client type is invalid.
     *
     * @param mdn               The subscriber's MDN.
     * @param reqSubsClientType The client type of the subscriber.
     * @param autoAssignZone    Indicates whether auto-assign is enabled.
     * @param extCorpId         The external corporate ID.
     * @param persisterTxn      The transaction object for persistence operations.
     * @throws KnDAOException If there is an error accessing the database.
     */
    public boolean assignZonesAndChannels(String mdn, Integer reqSubsClientType, String autoAssignZone, String extCorpId,
                                          KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException {
        final String methodName = "assignZonesAndChannels()";
        knLogger.debug(methodName, "Entry, autoAssignZone:", autoAssignZone);
        IProvXDMServerDAO xdmDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
        List<Integer> corpGroupIds = xdmDAO.getGroupIdsList(mdn, persisterTxn);
        List<Integer> sharedGroupIds = xdmDAO.getSharedGroupList(mdn, persisterTxn);
        Set<Integer> combinedGroupIds = new HashSet<>();
        if (corpGroupIds != null && !corpGroupIds.isEmpty()) {
            combinedGroupIds.addAll(corpGroupIds);
        }
        if (sharedGroupIds != null && !sharedGroupIds.isEmpty()) {
            combinedGroupIds.addAll(sharedGroupIds);
        }
        // Validate client type and auto-assign setting
        List<Integer> validClientTypes = Arrays.asList(SUBS_CLIENT_TYPE.HANDSET.value(), SUBS_CLIENT_TYPE.POC_WIFIONLY.value(), SUBS_CLIENT_TYPE.CROSSCARRIER.value());
        boolean isValidClient = validClientTypes.contains(reqSubsClientType);
        if (autoAssignZone == null || autoAssignZone.equals("0") || combinedGroupIds.isEmpty() || !isValidClient) {
            knLogger.info(methodName, "Skipping zone/channel assignment. Reason: Auto-assign disabled, no groups found, or invalid client type.");
            return true;
        }
        processAutoAssignZoneAndChannel(new ArrayList<>(combinedGroupIds), mdn, extCorpId, persisterTxn);
        return false;
    }

    /**
     * Processes the auto-assignment of zones and channels for groups based on the provided configuration and group information.
     *
     * @param groupInfoMap A map containing group information, where the key is the group type and the value is a list of maps with group details.
     * @param mdn          The mobile directory number (MDN) of the subscriber.
     * @param extCorpId    The external corporate ID associated with the subscriber.
     * @param persisterTxn The transaction object used for database operations.
     * @throws KnDAOException    If a data access error occurs during processing.
     * @throws KnProvBOException If a business logic error occurs during processing.
     */
    private void processAutoAssignZoneAndChannel(List<Integer> groupIds, String mdn, String extCorpId,
                                                 KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException {
        String methodName = "processAutoAssignZoneAndChannel()";
        knLogger.debug(methodName, "ENTRY Point : -");
        IProvXDMServerDAO xdmDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
        Map<String, List<Map<Integer, String>>> groupInfoMap = xdmDAO.getGroupInfo(groupIds, persisterTxn);
        var zoneAndChannelConfigValues = xdmDAO.getZoneAndChannerConfigValues(extCorpId, persisterTxn);
        String deviceInfo = xdmDAO.getAddDeviceInfo(mdn, persisterTxn);
        if (deviceInfo != null && deviceInfo.contains("{")) {
            int noOfChannelsPerZone = Integer.parseInt(extractValue(deviceInfo, "NoOfChannelsPerZone"));
            boolean isMultiZone = Boolean.parseBoolean(extractValue(deviceInfo, "IsMultiZone"));
            if (!isMultiZone) {
                zoneAndChannelConfigValues.put("MAXZONES", SINGLE_ZONE);
                zoneAndChannelConfigValues.put("MAXCHANNELSPERZONE", noOfChannelsPerZone);
            }
        }
        knLogger.debug(methodName,"zoneAndChannelConfigValues:", zoneAndChannelConfigValues);
        List<String> groupTypeOrder = Arrays.asList(DISPATCHER_GROUP, STD_GROUP, BCG_GROUP);
        int numZones = zoneAndChannelConfigValues.get("MAXZONES");
        int numChannels = zoneAndChannelConfigValues.get("MAXCHANNELSPERZONE");
        List<KnSubsAddlTGInfoDTO> subsAddlTGInfoDTOS = new ArrayList<>();
        Set<String> usedZoneChannelPairs = new HashSet<>();
        for (String groupType : groupTypeOrder) {
            if (!groupInfoMap.containsKey(groupType)) continue;
            List<Map<Integer, String>> groupDetails = groupInfoMap.get(groupType);
            Map<Integer, String> filteredGroupMap = new HashMap<>();
            for (Map<Integer, String> groupDetail : groupDetails) {
                filteredGroupMap.putAll(groupDetail);
            }
            if (groupType.equals(BCG_GROUP)) {
                List<Integer> allCorpGroupIds = filteredGroupMap.keySet().stream().toList();
                if (allCorpGroupIds.isEmpty()) {
                    knLogger.debug(methodName, "No Broadcaster Group IDs found for group type 2");
                    continue;
                }
                Set<Integer> validBroadcasterGroups = new HashSet<>(xdmDAO.getBroadcasterGroupIds(mdn, persisterTxn));
                knLogger.debug(methodName, "Valid Broadcaster Groups for MDN:", mdn, " -> ", validBroadcasterGroups);
                filteredGroupMap.entrySet().removeIf(entry -> !validBroadcasterGroups.contains(entry.getKey()));
            }
            List<String> sortedGroupNames = new ArrayList<>(filteredGroupMap.values());
            Collections.sort(sortedGroupNames);
            Set<String> processedGroupNames = new HashSet<>();
            for (String groupName : sortedGroupNames) {
                // Skip if the groupName has already been processed
                if (processedGroupNames.contains(groupName)) {
                    continue;
                }
                // Add the groupName to the processed set
                processedGroupNames.add(groupName);
                // Collect all corpGroupIds for the given groupName
                List<Integer> corpGroupIds = filteredGroupMap.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(groupName))
                        .map(Map.Entry::getKey)
                        .distinct() // Ensure unique corpGroupIds
                        .collect(Collectors.toList());
                // Skip if no corpGroupIds are found
                if (corpGroupIds.isEmpty()) continue;
                // Assign zones and channels for each corpGroupId
                for (Integer corpGroupId : corpGroupIds) {
                    boolean assigned = false;
                    for (int z = 1; z <= numZones && !assigned; z++) {
                        for (int c = 1; c <= numChannels; c++) {
                            String pairKey = z + "-" + c;
                            if (!usedZoneChannelPairs.contains(pairKey)) {
                                KnSubsAddlTGInfoDTO dto = new KnSubsAddlTGInfoDTO();
                                dto.setGroupId(corpGroupId);
                                dto.setZoneId(z);
                                dto.setChannelId(c);
                                dto.setMdn(mdn);
                                subsAddlTGInfoDTOS.add(dto);
                                usedZoneChannelPairs.add(pairKey);
                                assigned = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (!subsAddlTGInfoDTOS.isEmpty()) {
            xdmDAO.insertSubsAddlTGList(subsAddlTGInfoDTOS, persisterTxn);
        }
        knLogger.debug(methodName, "EXIT Point : processGroupTypes -");
    }

    /**
     * Extracts the value associated with a given key.
     */
    private static String extractValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\"");
        int colonIndex = json.indexOf(":", keyIndex);
        int commaIndex = json.indexOf(",", colonIndex);
        int endIndex = commaIndex == -1 ? json.indexOf("}", colonIndex) : commaIndex;
        return json.substring(colonIndex + 1, endIndex).trim().replace("\"", "");
    }
}
