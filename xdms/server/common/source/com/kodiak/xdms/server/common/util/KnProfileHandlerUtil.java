/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.util;

import com.kodiak.common.dao.KnConnectionException;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.frameworks.confignotifier.watcher.KnConfigWatcherUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManager;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.common.resources.KnCacheKeys;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.common.resources.KnGeneralUtil.getFeatureBitValue;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnProfileHandlerUtil.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 12, 2011           7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

/**
 * This class is responsible for implementing all the profile cache related
 * activities.
 */
public class KnProfileHandlerUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnProfileHandlerUtil.class);
    public KnConfigWatcherUtil configWatcherUtil = KnConfigWatcherUtil.getInstance();

    //stores the singleton instance of KnProfileCache
    private KnProfileCache profileCache;

    private static KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
    private static KnGeneralUtil generalUtil = new KnGeneralUtil();

    //stores the default refresh refresh interval for profile objects.
    //this is used when the refresh interval configured is less than 5 mins
    private static final int DEFAULT_REFRSH_INTERVAL = 600;

    /**
     * This method initializes the profile cache with the cacheSize if the cacheSize > 0
     * otherwise initializes with the default cacheSize. The refreshInterval should be
     * greater than or equal to 5 mins, other wise will be set to default value of 5 mins.
     *
     * @param cacheSize       the maximum size of the profile cache
     * @param refreshInterval the time interval after which the cached profile obj will expire
     */
    public KnProfileHandlerUtil(int cacheSize, int refreshInterval) {
        String methodName = "KnProfileHandlerUtil(cacheSize, refreshInterval)";
        //refresh interval in mins
        if (refreshInterval < 300) refreshInterval = DEFAULT_REFRSH_INTERVAL;
        if (cacheSize <= 0) {
            knLogger.info(methodName, "Creating profile cache");
            profileCache = new KnProfileCache(refreshInterval);
        } else {
            knLogger.info(methodName, "Creating profile cache");
            profileCache = new KnProfileCache(cacheSize, refreshInterval);
        }
        //register KnGenInfoUtil calss object to config notifier
        List<Observer> observers = new ArrayList<>();
        observers.add(genInfoUtil);
        configWatcherUtil.addObservers(observers);

    }

    /**
     * This method returns the singleton instance of the KnProfileManager class
     * and also initialize the KnProfileCache object reference.
     *
     * @param cacheSize       the maximum size of the profile cache
     * @param refreshInterval the time interval after which the cached profile obj will expire
     * @return the singleton instance of KnProfileManager
     */
//    public static KnProfileHandlerUtil getInstance(int cacheSize, int refreshInterval) {
//        if (instance == null) {
//            instance = new KnProfileHandlerUtil(cacheSize, refreshInterval);
//        }
//        return instance;
//    }

    /**
     * This method returns the instance of KnSubsProfileDTO object against the provided userName.
     * It first tries to get the profile object from the profileCache only if override flag is false.
     * If override flag is true it will always fetch the profile details from DB even if it's
     * available in cache and will override the cache.
     *
     * @param userName      the userName of the user
     * @param profileType   the profile type of the user
     * @param overrideCache flag indicating whether to check cache for profile information
     * @return userProfile the user profile object
     * @throws KnDAOException exception
     */
    public <P extends KnProfileDTO> P getProfileDetails(String userName, String profileType, boolean
            overrideCache, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {

        String methodName = "getProfileDetails()";
        knLogger.debug(methodName, "userName ->", KnGDPRTemplate.mdn(userName), ", profileType ->", profileType, ", overrideCache ->", overrideCache);
        KnProfileDTO profileDTO;
        boolean ownedTxn = false;
        if (userName == null) {
            knLogger.error(methodName, "Invalid user name passed.");
            return null;
        }
        //checking whether to check the profile in cache or directly fetch from DB
        //if the overrideCache flag is false and profile in cache is not expired
        //then use the cache otherwise go for DB fetch
        if (!overrideCache) {
            knLogger.debug(methodName, "Checking profile in cache.");
            profileDTO = getProfileFromCache(userName);
            knLogger.debug(methodName, "Profile Expiry time ->", KnProfileCache.getExpiryTime());
            if (profileDTO != null) {
                if (profileDTO.isActive()) {
                    knLogger.debug(methodName, "Returning profile from profile cache.");
                    profileDTO.setDTOStatus(KnConstants.STATUS_SUCCESS);
                    //noinspection unchecked
                    return (P) profileDTO;
                } else {
                    knLogger.debug(methodName, "Profile is not upto date with DB. Will be fetched from DB.");
                }
            } else {
                knLogger.debug(methodName, "User Profile not found in profile cache. Deeping into DB.");
            }
        } else {
            knLogger.debug(methodName, "Ignoring profile from profile cache and deeping into DB.");
        }
        //Profile does not exists in profileCache, so do a DB fetch for the profileDTO.
        try {
            KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
            String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                    createXDMServerDAO(xdmPttId);
            //2. Checking the profile type
            if (KnProfileTypes.PUBLIC_PROFILE.equalsIgnoreCase(profileType)) {
                //public profile retrieval and population
                KnSubsProfileDTO subsProfileDTO = xdmDAO.getSubscriberProfile(userName, readOnly, persisterTxn);
                knLogger.debug(methodName, "Subscr Info DTO fetched from DB ->", subsProfileDTO);
                //adding to profile cache
                //addProfileToCache(subsProfileDTO);
                //noinspection unchecked
                return (P) subsProfileDTO;

            } else if (KnProfileTypes.CORP_PROFILE.equalsIgnoreCase(profileType)) {
                //corporate profile retrieval and population
                KnCorpProfileDTO corpProfileDTO = xdmDAO.getCorporateProfile(userName, persisterTxn);
                corpProfileDTO.setMdn(userName); //key to the cache element
                KnXDMSServiceConfigDTO xdmsConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttId, persisterTxn);
                if(corpProfileDTO.getPrivacyAmbDiscListenFlag() == null){
                    corpProfileDTO.setPrivacyAmbDiscListenFlag(xdmsConfigDTO.getPrivacyAmbDiscListen());
                }
                else
                {
                    corpProfileDTO.setPrivacyAmbDiscListenFlag(corpProfileDTO.getPrivacyAmbDiscListenFlag());
                }
                corpProfileDTO.setMaxContactsPerSubsc(xdmsConfigDTO.getMaxCorpContactsPerSubs());
                corpProfileDTO.setMaxGroupsPerSubsc(xdmsConfigDTO.getMaxCorpGrpsPerSubs());
                corpProfileDTO.setMaxGroupsPerLargeDispatch(xdmsConfigDTO.getMaxCorpGrpsPerLargeDispatch());
                corpProfileDTO.setMaxExtContactsPerCorp(xdmsConfigDTO.getMaxExtContactsPerCorp());
                corpProfileDTO.setMaxContactsPerRequest(xdmsConfigDTO.getMaxContactPerRequest());
                corpProfileDTO.setDispatchEnabled(xdmsConfigDTO.getDispatchEnabled());
                corpProfileDTO.setSupervisoryOverrideEnabled(xdmsConfigDTO.getSupervisoryEnabled());
                int maxXdmDispatchGroup = xdmsConfigDTO.getMaxDispatchGroup();
                int maxCorpProfDispatchGrp = corpProfileDTO.getMaxDispatchGroup();
                if (maxCorpProfDispatchGrp == 0 || maxCorpProfDispatchGrp > maxXdmDispatchGroup) {
                    corpProfileDTO.setMaxDispatchGroup(maxXdmDispatchGroup);
                }
                int maxMembersPerDispatchGrp = xdmsConfigDTO.getMaxMembersPerDispatchGroup();
                int maxCorpProfMemPerDispatchGrp = corpProfileDTO.getMaxMembersPerDispatchGroup();
                if (maxCorpProfMemPerDispatchGrp == 0 || maxCorpProfMemPerDispatchGrp > maxMembersPerDispatchGrp) {
                    corpProfileDTO.setMaxMembersPerDispatchGroup(maxMembersPerDispatchGrp);
                }
                corpProfileDTO.setMaxDispatchMembersPerDispatchGroup(xdmsConfigDTO.getMaxDispatchMembersPerDispatchGroup());
                int maxCorpList = xdmsConfigDTO.getMaxSublistsPerCorp();
                int maxCorpProfCorpList = corpProfileDTO.getMaxCorpLists();
                if (maxCorpProfCorpList == 0 || maxCorpProfCorpList > maxCorpList) {
                    corpProfileDTO.setMaxCorpLists(maxCorpList);
                }
                int maxMembersPerCorpSublist = xdmsConfigDTO.getMaxMembersPerCorpSublist();
                int maxCorpProfMemPerCorpList = corpProfileDTO.getMaxMemPerCorpList();
                if (maxCorpProfMemPerCorpList == 0 || maxCorpProfMemPerCorpList > maxMembersPerCorpSublist) {
                    corpProfileDTO.setMaxMemPerCorpList(maxMembersPerCorpSublist);
                }
                int maxCorpGrp = xdmsConfigDTO.getMaxPOCGrpsPerCorp();
                int maxCorpProfCorpGrp = corpProfileDTO.getMaxCorpGroups();
                if (maxCorpProfCorpGrp == 0 || maxCorpProfCorpGrp > maxCorpGrp) {
                    corpProfileDTO.setMaxCorpGroups(maxCorpGrp);
                }
                int maxMembersPerCorpGroup = xdmsConfigDTO.getMaxMembersPerCorpPOCGrp();
                int maxCorpProfMemPerCorpGrp = corpProfileDTO.getMaxMemPerCorpGroup();
                if (maxCorpProfMemPerCorpGrp == 0 || maxCorpProfMemPerCorpGrp > maxMembersPerCorpGroup) {
                    corpProfileDTO.setMaxMemPerCorpGroup(maxMembersPerCorpGroup);
                }
                int maxExtSubsPerCorp = corpProfileDTO.getMaxExtSubsPerCorp();
                if(maxExtSubsPerCorp == 0 || maxExtSubsPerCorp > xdmsConfigDTO.getMaxExtSubsPerCorp()){
                    corpProfileDTO.setMaxExtSubsPerCorp(xdmsConfigDTO.getMaxExtSubsPerCorp());
                }
                corpProfileDTO.setEnablePocDonorRadioSupport(xdmsConfigDTO.getEnablePocDonorRadioSupport());
                int maxThresoldLimit = generalUtil.getGenActCodMaxSubsLmt();
                corpProfileDTO.setMaxSubsAllowedGenActvReq(maxThresoldLimit);
                corpProfileDTO.setSystemTGSBit(xdmsConfigDTO.getEnableTGS());
                corpProfileDTO.setMaxCampedGrp(xdmsConfigDTO.getMaxCampedGroups());
                corpProfileDTO.setMaxPriority(xdmsConfigDTO.getMaxPriority());
                corpProfileDTO.setTgscanningClient(xdmsConfigDTO.getTgscanningClient());
                corpProfileDTO.setMaxPttRadioChannelSize(xdmsConfigDTO.getPttRadioChannelListSize());
                corpProfileDTO.setMaxPttRadioScanSize(xdmsConfigDTO.getPttRadioScanListSize());
                corpProfileDTO.setPttRadioDefScanMode(xdmsConfigDTO.getPttRadioDefScanMode());
                corpProfileDTO.setPttRadioChannelListSize(xdmsConfigDTO.getPttRadioChannelListSize());
                corpProfileDTO.setPttRadioScanListSize(xdmsConfigDTO.getPttRadioScanListSize());
                corpProfileDTO.setMaxSGPerGrp(xdmsConfigDTO.getMaxSGMdnsPerGroup());

                int maxMemPerBCGrp = corpProfileDTO.getMaxMemPerBCGrp();
                if(maxMemPerBCGrp == 0 || maxMemPerBCGrp > xdmsConfigDTO.getMaxMemPerBCGrp()){
                    corpProfileDTO.setMaxMemPerBCGrp(xdmsConfigDTO.getMaxMemPerBCGrp());
                }
                corpProfileDTO.setEnableBCGrpFeature(xdmsConfigDTO.getEnableBCGrpFeature());

                int webCorpDispatcherEnabled = corpProfileDTO.getWebDispatchEnabled();
                int webXDMSvcDispatcherEnabled = xdmsConfigDTO.getWebDispatchEnabled();
                if (webCorpDispatcherEnabled == 1 || webXDMSvcDispatcherEnabled == 1) {
                    corpProfileDTO.setWebDispatchEnabled(KnConstants.BIT_TRUE);
                }

                corpProfileDTO.setInteropLicenceType(xdmsConfigDTO.getInteropLicenceType());
                corpProfileDTO.setMaxSGPatchPerGrp(xdmsConfigDTO.getMaxSGPatchPerGrp());
                corpProfileDTO.setEmergFeature(xdmsConfigDTO.getEmergFeature());
                corpProfileDTO.setAmbientListening(xdmsConfigDTO.getAmbientListening());
                corpProfileDTO.setDiscreteListening(xdmsConfigDTO.getDiscreteListening());
                corpProfileDTO.setUserCheck(xdmsConfigDTO.getUserCheck());
                corpProfileDTO.setUserSvcCtrl(xdmsConfigDTO.getUserSvcCtrl());

                int maxChannelsPerZone = xdmsConfigDTO.getMaxChannelsPerZone();
                int maxCorpChannelsPerZone = corpProfileDTO.getMaxChannelsPerZone();
                if (maxCorpChannelsPerZone == 0) {
                    corpProfileDTO.setMaxChannelsPerZone(maxChannelsPerZone);
                }
                int maxRadioChannels = xdmsConfigDTO.getMaxRadioChannels();
                int maxCorpRadioChannels = corpProfileDTO.getMaxRadioChannels();
                if (maxCorpRadioChannels == 0) {
                    corpProfileDTO.setMaxRadioChannels(maxRadioChannels);
                }
                int maxZones = xdmsConfigDTO.getMaxZones();
                int maxCorpZones = corpProfileDTO.getMaxZones();
                if (maxCorpZones == 0 ) {
                    corpProfileDTO.setMaxZones(maxZones);
                }
                if(corpProfileDTO.getLargeGrpSupported() == 1 && xdmsConfigDTO.getLargeGrpSupport() == 1){
                    corpProfileDTO.setLargeGrpSupported(1);
                }else {
                    corpProfileDTO.setLargeGrpSupported(0);
                }
                corpProfileDTO.setMaxLrgGrpPerCorp(xdmsConfigDTO.getMaxLrgGrpPerCorp());
                corpProfileDTO.setMaxMemPerLrgGrp(xdmsConfigDTO.getMaxMemPerLrgGrp());
                corpProfileDTO.setMaxLrgBGrpPerCorp(xdmsConfigDTO.getMaxLrgBGrpPerCorp());
                corpProfileDTO.setMaxMemPerLrgBGrp(xdmsConfigDTO.getMaxMemPerLrgBGrp());
                corpProfileDTO.setMcVideoEnabled(xdmsConfigDTO.getMcVideoEnabled());
                corpProfileDTO.setMcVideoUnCfrmPullEnabled(xdmsConfigDTO.getMcVideoUnCfrmPullEnabled());
                //PTX Changes:
                populateBitInfo(corpProfileDTO, persisterTxn);
                knLogger.debug(methodName, "Setting Corp user profile into cache. Corp Profile ->", corpProfileDTO);
                //addProfileToCache(corpProfileDTO);
                //noinspection unchecked
                return (P) corpProfileDTO;

            } else {
                knLogger.debug(methodName, "Invalid Profile type found, Could not process profile request.");
                throw new KnBOException(KnErrorCodes.BOEntity.INVALID_PROFILE, "Invalid profile - " + profileType);
            }
        } catch (KnDAOException dex) {
            // rollback txn if already opened.
            knLogger.error(methodName, "DAO Exception occured during profile retrieval.", dex);
            throw dex;
        } catch (KnBOException bex) {
            // rollback txn if already opened.
            knLogger.error(methodName, "BO Exception occured during profile retrieval.", bex);
            throw bex;
        }

    }

    public Map<String, KnSubsProfilePersistDTO> getSubscriberProfileDetails(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {

        String methodName = "getSubscriberProfileDetails()";
        knLogger.debug(methodName, "mdnList ->",KnGDPRTemplate.mdnList(mdnList));
        KnProfileDTO profileDTO;
        boolean ownedTxn = false;
        KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
        String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                createXDMServerDAO(xdmPttId);
        Map<String, KnSubsProfilePersistDTO> subsProfileDTO = xdmDAO.getSubscriberProfileDetails(mdnList, readOnly, persisterTxn);
        return subsProfileDTO;
    }

    /**
     * This method clears the profileCache entry for the specified userName if any exists
     * and returns the profile or null if it does not exist in cache.
     *
     * @param userName the userName of the user
     * @return userProfile the deleted user profile object
     */
    public <P extends KnProfileDTO> P removeProfileCache(String userName) {
        //this method should be called when a particular userProfile expires
        //e.g. this method can be invoked when a particular user logs out or
        //session timeout happens
        //noinspection unchecked
        return (P) deleteProfileFromCache(userName);
    }

    /**
     * This method refreshes the user profile entry in the cache.
     *
     * @param userName    the userName of the user
     * @param profileType the profile type of the user
     * @return userProfile the refreshed user profile object
     * @throws KnDAOException
     *          exception
     */
    public <P extends KnProfileDTO> P refreshProfileCache(String userName, String profileType) throws KnDAOException, KnBOException {
        deleteProfileFromCache(userName);
        //return (P) getProfileDetails(userName, KnProfileTypes.PUBLIC_PROFILE, true);
        //noinspection unchecked
        return (P) getProfileDetails(userName, profileType, true, KnConstants.FALSE, null);
    }

    /**
     * Private method for adding profileDTO object to profileDTO cache
     *
     * @param profileDTO the profileDTO object
     */
    private void addProfileToCache(KnProfileDTO profileDTO) {
        String methodName = "addProfileToCache(profileDTO)";
        knLogger.debug(methodName, "Profilecache - ", profileCache);
        //profileCache.addProfileToCache(profileDTO);
    }

    /**
     * Private method for retrieving the profile object from profile cache
     *
     * @param userName the userName of the user
     * @return userProfile the profile object of the user
     */
    private <P extends KnProfileDTO> P getProfileFromCache(String userName) {
        String methodName = "getProfileFromCache(userName)";
        knLogger.debug(methodName, "ProfileCache obj - ", profileCache);
        //noinspection unchecked
        return (P) profileCache.getProfileFromCache(userName);
    }

    /**
     * Private method for deleting the profile object from profile cache
     *
     * @param userName the userName of the user
     * @return userProfile the deleted profile object
     */
    private <P extends KnProfileDTO> P deleteProfileFromCache(String userName) {
        //noinspection unchecked
        return (P) profileCache.deleteProfileFromCache(userName);
    }

    /**
     * This method is for retrieving the external profile details for all profileIds from cache.
     * If not found in cache retrieve from DG and put the same in cache back.
     * @param isOverride
     * @param persisterTxn
     * @return
     */
    public Map<Integer, KnExtProfileDetails> getExtProfileDetails(boolean isOverride, String xdmPttServerId, KnPersisterTxn
            persisterTxn) throws KnBOException{
        String methodName = "getExtProfileDetails(int, boolean, String, KnPersisterTxn)";
        Map<Integer, KnExtProfileDetails> extPrifileDetailsMap = null;
        knLogger.debug(methodName, "ENTRY: Retrieve Service Config");
        try {
            knLogger.debug(methodName, "retrieving the XDMS Service Configuration");
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = configManager.getCacheManager();
            knLogger.debug(methodName, "Reading from Cache");
            extPrifileDetailsMap = (Map<Integer, KnExtProfileDetails>) cacheManager.get(KnCacheKeys.EXTERNAL_PROFILE_LIST);
            if (extPrifileDetailsMap == null || extPrifileDetailsMap.isEmpty() || isOverride) {
                knLogger.debug(methodName, "Not found in cache or is override is true, hence reading from DB ");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                extPrifileDetailsMap = xdmServerDAO.getExtProfileDetailsMap(persisterTxn);
                cacheManager.put(KnCacheKeys.EXTERNAL_PROFILE_LIST, extPrifileDetailsMap);
            }
            knLogger.debug(methodName, "Retrieved external profile map : " + extPrifileDetailsMap);
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
        }
        return extPrifileDetailsMap;
    }

    private void populateBitInfo(KnCorpProfileDTO corpProfileDTO, KnPersisterTxn persisterTxn) throws KnBOException {
        final String methodName = "populateBitInfo(KnCorpProfileDTO, KnPersisterTxn)";
        int clusterId = parseIntSafe(System.getenv(CLUSTERID_ENV_NAME), -1);
        knLogger.debug(methodName, "OpsCorpFs2() - ", corpProfileDTO.getOpsCorpFs2(), "clusterId - ", clusterId);
        if (clusterId < 0) {
            knLogger.error(methodName, "Env var ", CLUSTERID_ENV_NAME,
                    " is not set or not a valid integer. Skipping MICROSVCS SERVICE CONFIG lookup.");
            return;
        }
        try {
            String opsCorpFs2 = corpProfileDTO.getOpsCorpFs2();
            Map<String, String> msSvcConfigDocMap = genInfoUtil.retrieveMSSvcsServiceConfig(clusterId, persisterTxn);
            if (msSvcConfigDocMap == null || msSvcConfigDocMap.isEmpty()) {
                knLogger.warn(methodName, "MICROSVCS SERVICE CONFIG map is null or empty for clusterId - ", clusterId);
                return;
            }
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            if (microServicesParamNameValueMap == null || microServicesParamNameValueMap.isEmpty()) {
                knLogger.warn(methodName, "MICROSVCOMM Config map is null or empty for clusterId - ", clusterId);
                return;
            }
            if (getFeatureBitValue(opsCorpFs2, TEXTMSGFLAGBIT) && (parseIntSafe(msSvcConfigDocMap.get(TEXTMSGFLAG), 0) == 1
                    || parseIntSafe(microServicesParamNameValueMap.get(SDSFEATUREFLAG), 0) == 1)) {
                corpProfileDTO.setTextMsgFlag(1);
            }
            if (getFeatureBitValue(opsCorpFs2, MULTIMEDIAMSGFLAGBIT) && (parseIntSafe(msSvcConfigDocMap.get(MULTIMEDIAMSGFLAG),
                    0) == 1 || parseIntSafe(microServicesParamNameValueMap.get(FDFEATUREFLAG), 0) == 1)) {
                corpProfileDTO.setMultiMediaMsgFlag(1);
            }
            if (getFeatureBitValue(opsCorpFs2, LOCATIONMSGFLAGBIT) && (parseIntSafe(msSvcConfigDocMap.get(LOCATIONMSGFLAG),
                    0) == 1 || parseIntSafe(microServicesParamNameValueMap.get(SDSFEATUREFLAG), 0) == 1)) {
                corpProfileDTO.setLocationMsgFlag(1);
            }
            if (getFeatureBitValue(opsCorpFs2, URGENTMSGFLAGBIT) && parseIntSafe(msSvcConfigDocMap.get(URGENTMSGFLAG), 0) == 1) {
                corpProfileDTO.setUrgentMsgFlag(1);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-expected Exception occurred");
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to get MICROSVCS SERVICE CONFIG info", e);
        }
    }

    private static int parseIntSafe(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public KnPocSubsAddlInfoDTO getSubsAddlDetails(String mdn , KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        String methodName = "getSubsAddlDetails()";
        knLogger.debug(methodName, "mdn ->", KnGDPRTemplate.mdn(mdn));
        KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
        String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        return xdmDAO.getSubsAddlDetails(mdn,persisterTxn );
    }

    public KnEmergencyInfoDTO getEmergencySubsDestInfo(String mdn , KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        String methodName = "getEmergencySubsDestInfo()";
        knLogger.debug(methodName, "mdn ->", KnGDPRTemplate.mdn(mdn));
        KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
        String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        KnEmergencyInfoDTO emergencySubsDestInfo= xdmDAO.getEmergencySubsDestInfo(mdn,persisterTxn );
        return emergencySubsDestInfo;
    }

    public KnSubsProfilePersistDTO  getProfileDetailsByMcpttID(String mcpttId ,KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        String methodName = "getProfileDetailsByMcpttID()";
        knLogger.debug(methodName, "mcpttId ->",KnGDPRTemplate.mcpttId(mcpttId));
        KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
        String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        KnSubsProfilePersistDTO subsProfileDTO= xdmDAO.getProfileDetailsByMcpttID(mcpttId,persisterTxn );
        return subsProfileDTO;
    }

    public KnSubsProfilePersistDTO      getProfileDetailsByMcIDAndUpmIndex(String mcId,String upmIndex ,KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        String methodName = "getProfileDetailsByMcIDAndUpmIndex()";
        knLogger.debug(methodName, "mcId ->", mcId," upmIndex ->",upmIndex);
        KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
        String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        KnSubsProfilePersistDTO subsProfileDTO= xdmDAO.getProfileDetailsByMcIDAndUpmIndex(mcId,upmIndex,persisterTxn );
        return subsProfileDTO;
    }

    public List<KnSubsProfileDTO> getProfileDetailsListByMcID(String mcId ,KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        String methodName = "getProfileDetailsListByMcID()";
        knLogger.debug(methodName, "mcId ->", KnGDPRTemplate.mcId(mcId));
        KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
        String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        return xdmDAO.getProfileDetailsListByMcID(mcId,persisterTxn );
    }

    public KnCorpUserProfileDTO getUserProfileNameByindex(int corpId, int upmIndex, KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        String methodName = "getUserProfileNameByindex()";
        knLogger.debug(methodName, "corpId ", corpId," upmIndex ",upmIndex);
        KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
        String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        return xdmDAO.getUserProfileNameByindex(corpId,upmIndex,persisterTxn );
    }
    
    public KnCorpUserProfileDTO getUserProfileById(String userProfileId) throws KnDAOException, KnBOException {
        String methodName = "getUserProfileById( String)";
        knLogger.debug(methodName, " userProfileId ",userProfileId);
        KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
        String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        return xdmDAO.getUserProfileById(userProfileId);
    }

    public List<String> getRealMdns(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        String methodName = "getRealMdns()";
        knLogger.debug(methodName, "mdns ->", KnGDPRTemplate.mdnList(mdns));
        KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
        String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        return xdmDAO.getRealMdns(mdns, readOnly, persisterTxn);
    }

    public Map<String, List<String>> getProfileMdnListByBaseMdnsList(List<String> baseMdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
        String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        return xdmDAO.getProfileMdnListByBaseMdnsList(baseMdnList, readOnly, persisterTxn);
    }
    
    public Map<String, Collection<KnDocChangeListDTO>> updateSubsTS(Set<String> mdnList,String exists, KnPersisterTxn persisterTxn) throws KnDAOException ,KnBOException{
    	String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
    	IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        return xdmDAO.updateSubsTS(mdnList,exists, persisterTxn);
    }
    
    public Map<String, Integer> updateAndGetDirecEtag(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException,KnBOException {
    	String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
    	IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        return xdmDAO.getAndUpdateDirectoryEtag(mdnList, persisterTxn);
    }

	public Set<KnCorpGroupListInfoDTO> retriveGroupProfileInfoByProfileId(String userProfileId,KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
		String methodName = "getUserProfileById(int, String)";
        knLogger.debug(methodName, " userProfileId ",userProfileId);
        KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
        String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        return xdmDAO.retriveGroupProfileInfoByProfileId(userProfileId,persisterTxn);
	}

	public Map<Integer, Integer> retrivePreConfigGroupProfileInfoByProfileId(String userProfileId,KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
		String methodName = "retrivePreConfigGroupProfileInfoByProfileId(int, String)";
        knLogger.debug(methodName, " userProfileId ",userProfileId);
        KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
        String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        return xdmDAO.retrivePreConfigGroupProfileInfoByProfileId(userProfileId,persisterTxn);
	}

    public Map<String, List<Integer>> retriveUpmIdGroupMapByUserProfileId(List<String> userProfileIdList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        KnFactorySelector.getDAOFactory(KnFactorySelector.DB);
        String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        return xdmDAO.retriveUpmIdGroupMapByUserProfileId(userProfileIdList, readOnly, persisterTxn);
    }

}
