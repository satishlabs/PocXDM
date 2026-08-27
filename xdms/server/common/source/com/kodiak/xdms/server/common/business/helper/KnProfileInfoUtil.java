/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.business.helper;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.util.KnProfileHandlerUtil;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnProfileInfoUtil.java
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
public class KnProfileInfoUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnProfileInfoUtil.class);

    private static String CLASS = KnProfileInfoUtil.class.getName();

    //stores the singleton instance of KnProfileInfoUtil
    //private static KnProfileInfoUtil instance;
    //stores the refernce of KnProfileHandlerUtil
    private KnProfileHandlerUtil profileHandler;
    //stores the boolean flag indicating whether KnProfileInfoUtil
    //have already been initialized
    private static boolean isInitialized;

    private static volatile Map<String, KnProfileInfoUtil> instanceMap;


    /**
     * Constructor with cachSize as parameter. The cacheSize parameter determines
     * the maximum number of profile objects that can be cached at any instant of time.
     * Ideally the module first instantiating the first instance should pass the cacheSize.
     * If not the cache will be initialized with a default cacheSize.
     *
     * @param cacheSize the maximum size of the profile cache
     */
    private KnProfileInfoUtil(int cacheSize, int refreshTime) {
        profileHandler = new KnProfileHandlerUtil(cacheSize, refreshTime);
    }

    public static synchronized void initialize(String instanceKey, int cacheSize, int refreshTime) {
        KnProfileInfoUtil instance = null;
        if (instanceMap == null) {
            instanceMap = new ConcurrentHashMap<String, KnProfileInfoUtil>();
        } else {
            instance = instanceMap.get(instanceKey);
        }
        if (instance == null) {
            instance = new KnProfileInfoUtil(cacheSize, refreshTime);
        }
        instanceMap.put(instanceKey, instance);
    }

    /**
     * This method initializes the profile cache with the default cache size.
     *
     * @return the singleton instance of the KnProfileInfoUtil class
     */
//    public static KnProfileInfoUtil getInstance() {
//        return getInstance(0, 0);
//    }
    public static KnProfileInfoUtil getInstance(String key) {
        String methodName = "getInstance(key)";
        knLogger.debug( methodName, "Key - " + key);
        KnProfileInfoUtil instance = null;
        if (instanceMap != null) {
            instance = instanceMap.get(key);
        }
        knLogger.debug( methodName, "InstanceMap - " + instanceMap);
        return instance;
    }

    /**
     * This method initializes the profile cache with the cacheSize parameter value.
     *
     * @param cacheSize the maximum size of the profile cache
     * @return the singleton instance of the KnProfileInfoUtil class
     */
//    public static KnProfileInfoUtil getInstance(int cacheSize, int refreshInterval) {
//        String methodName = "getInstance(int)";
//        if (!isInitialized) {
//            synchronized (KnProfileInfoUtil.class) {
//                if (instance == null) {
//                    instance = new KnProfileInfoUtil(cacheSize, refreshInterval);
//                    isInitialized = KnConstants.TRUE;
//                    knLogger.debug( methodName, "KnProfileInfoUtil initialized. " +
//                            "cacheSize passed ->" + cacheSize + ", Refresh interval passed ->" +
//                            refreshInterval);
//                }//end of null check//
//            }//end of lock//
//        }
//        return instance;
//    }

    /**
     * This method returns the user profile object for the specified user. This method first
     * tries to fetch the profile information from the profile cache and if not found in cache
     * deeps into DB.
     *
     * @param userName     the userName of the user
     * @param profileType  the profile type of the user
     * @param persisterTxn
     * @return an instance of KnSubsProfileDTO
     * @throws KnBOException exception
     */
    public <P extends KnProfileDTO> P getProfileDetails(String userName, String profileType, KnPersisterTxn persisterTxn) throws KnBOException {

        String methodName = "getProfileDetails()";
        try {
            KnProfileDTO profileDTO = profileHandler.getProfileDetails(userName, profileType, KnConstants.FALSE, KnConstants.FALSE, persisterTxn);
            if (profileDTO == null) {
                knLogger.error( methodName, "Profile Information not found - Invalid userId or Invalid profile type.");
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found - Invalid userId or Invalid profile type.");
            }
            //noinspection unchecked
            return (P) profileDTO;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occured - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            } else if (e instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :", e.getMessage());
                throw new KnBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, e.getMessage(), e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while retrieving Profile information", e);
        } catch (KnBOException boe) {
            knLogger.error( methodName, "BO Exception occured - ", boe.getMessage());
            throw boe;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to get Profile information", e);
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }

    /**
     * This method returns the user profile object for the specified user. This method provides the user
     * with an option whether to get the cached profile info if any exists or directly get it from DB.
     * Direct DB fetch is useful in situations where we need to get the upto date information from DB
     * like login operation.
     *
     * @param userName    the userName of the user
     * @param profileType the profile type of the user
     * @param isOverride  flag indicating whether to fetch profile information directly from DB
     * @return an instance of KnSubsProfileDTO
     * @throws KnBOException exception
     */
    public <P extends KnProfileDTO> P getProfileDetails(String userName, String profileType, boolean isOverride, boolean readOnly,
                                                        KnPersisterTxn persisterTxn)
            throws KnBOException {

        String methodName = "getProfileDetails()";
        try {
            //noinspection unchecked
            return (P) profileHandler.getProfileDetails(userName, profileType, isOverride, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occured - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while retrieving Profile information", e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to get Profile information", e);
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }

    public Map<String, KnSubsProfilePersistDTO> getSubscriberProfileDetails(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnBOException {

        String methodName = "getSubscriberProfileDetails()";
        try {
            return profileHandler.getSubscriberProfileDetails(mdnList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occured - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while retrieving Profile information", e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to get Profile information", e);
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }

    /**
     * This method deletes the profile details from cache
     *
     * @param userName the userName of the user
     * @return the instance of the removed profile
     */
    public <P extends KnProfileDTO> P removeProfileCache(String userName) {
        //noinspection unchecked
        return (P) profileHandler.removeProfileCache(userName);
    }

    /**
     * This method rfreshes the profile cache for the specified user
     *
     * @param userName    the userName of the user
     * @param profileType the profile type of the user
     * @return the instance of the refreshed profile
     * @throws KnBOException exception
     */
    public <P extends KnProfileDTO> P refreshProfileCache(String userName, String profileType) throws KnBOException {
        String methodName = "refreshProfileCache()";
        try {
            //noinspection unchecked
            return (P) profileHandler.refreshProfileCache(userName, profileType);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occured - " + e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while refreshing Profile information", e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - " + e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to refresh profile information", e);
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }

    /**
     * This method returns the external profile details for a profile ID.
     * @param overrideCache
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnBOException
     */
    public Map<Integer, KnExtProfileDetails> getExtProfileDetails(boolean overrideCache, String
            xdmsHome, KnPersisterTxn persisterTxn) throws KnBOException{
        return profileHandler.getExtProfileDetails(overrideCache, xdmsHome, persisterTxn);
    }

    public KnPocSubsAddlInfoDTO getSubsAddlDetails(String mdn, KnPersisterTxn persisterTxn)
            throws KnBOException {

        String methodName = "getSubsAddlDetails()";
        try {
            return profileHandler.getSubsAddlDetails(mdn,persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occurred - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while retrieving Profile information", e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to get Profile information", e);
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }

    public KnEmergencyInfoDTO getEmergencySubsDestInfo(String mdn, KnPersisterTxn persisterTxn)
            throws KnBOException {

        String methodName = "getEmergencySubsDestinfo()";
        try {
            return profileHandler.getEmergencySubsDestInfo(mdn,persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occurred - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while retrieving Profile information", e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to get Profile information", e);
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }

    public <P extends KnProfileDTO> P getProfileDetailsByMcpttID(String mcpttId,KnPersisterTxn persisterTxn)
            throws KnBOException {

        String methodName = "getProfileDetailsByMcpttID()";
        try {
            //noinspection unchecked
            return (P) profileHandler.getProfileDetailsByMcpttID(mcpttId,persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occurred - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while retrieving Profile information", e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to get Profile information", e);
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }

    public <P extends KnProfileDTO> P getProfileDetailsByMcIDAndUpmIndex(String mcId,String upmIndex,KnPersisterTxn persisterTxn)
            throws KnBOException {

        String methodName = "getProfileDetailsByMcIDAndUpmIndex()";
        try {
            return (P) profileHandler.getProfileDetailsByMcIDAndUpmIndex(mcId,upmIndex,persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occurred - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while retrieving Profile information", e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to get Profile information", e);
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }

    public List<KnSubsProfileDTO> getProfileDetailsListByMcID(String mcId,KnPersisterTxn persisterTxn)
            throws KnBOException {

        String methodName = "getProfileDetailsListByMcID()";
        try {
            return profileHandler.getProfileDetailsListByMcID(mcId,persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occurred - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while retrieving Profile information", e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to get Profile information", e);
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }

    public KnCorpUserProfileDTO getUserProfileNameByindex(int corpId, int upmIndex, KnPersisterTxn persisterTxn)
            throws KnBOException {

        String methodName = "getUserProfileNameByindex()";
        try {
            return profileHandler.getUserProfileNameByindex(corpId,upmIndex,persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occurred - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while retrieving Profile information", e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to get Profile information", e);
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }
    public KnCorpUserProfileDTO getUserProfileById( String userProfileId)
            throws KnBOException {

        String methodName = "getUserProfileById(String)";
        try {
            return profileHandler.getUserProfileById(userProfileId);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occurred - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while retrieving Profile information", e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to get Profile information", e);
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }

    public List<String> getRealMdns(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "getRealMdns(List<String>,KnPersisterTxn)";
        try {
            return profileHandler.getRealMdns(mdns, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occurred - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while retrieving Profile information", e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to get Profile information", e);
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }

    public Map<String, List<String>> getProfileMdnListByBaseMdnsList(List<String> baseMdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnBOException {

        String methodName = "getProfileMdnListByBaseMdnsList()";
        try {
            return profileHandler.getProfileMdnListByBaseMdnsList(baseMdnList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occurred - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while retrieving Profile information", e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to get information", e);
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }
    
    /**
    *
    * @param userProfileIds
    * @param corpId
    * @param profileMdnList
    * @param exists :exists should be passed 0 in case of delete subscriber.
    * @param xdmsHome
    * @param persisterTxn
    * @return Map<String, Collection<KnDocChangeListDTO>>
    * @throws KnCorpBOException
    */
   public Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagUpdate(List<String> userProfileIds, String corpId
           ,List<String> profileMdnList,String exists, String xdmsHome,KnPersisterTxn persisterTxn) throws KnBOException {
       final String methodName = "profileMdnEtagUpdate(List<String>, String, String, KnPersisterTxn)";
       knLogger.debug(methodName, "ENTRY :", "userProfileIds: ", userProfileIds
               , "corpId: ", corpId,"profileMdnList :", KnGDPRTemplate.mdnList(profileMdnList));
       Map<String, Collection<KnDocChangeListDTO>> docUriMap = new HashMap<>();
       try {
           Set<String> fetchedProfileMdnList = new HashSet<>();
           //if profileMdn is known
           if(profileMdnList!=null&&!profileMdnList.isEmpty()){
               fetchedProfileMdnList.addAll(profileMdnList);
           }
         
           //UPM is not assigned to mdn check.
			if (fetchedProfileMdnList != null && !fetchedProfileMdnList.isEmpty()) {
				// updating LASTPROFILEUPDATETIME in POCSUBSCRINFO
				docUriMap = profileHandler.updateSubsTS(fetchedProfileMdnList, exists, persisterTxn);

				// updating ETAG in XDM_DIRECTORY
				profileHandler.updateAndGetDirecEtag(fetchedProfileMdnList, persisterTxn);
			}
       } catch (KnDAOException e) {
           throw new KnBOException(e.getErrorCode(), e.getErrorMessage(), e);
       }
       return docUriMap;
   }

	public Set<KnCorpGroupListInfoDTO> retriveGroupProfileInfoByProfileId(String userProfileId,KnPersisterTxn persisterTxn) throws KnBOException {
		 String methodName = "getMcxUserProfileById(String)";
	        try {
	            return profileHandler.retriveGroupProfileInfoByProfileId(userProfileId,persisterTxn);
	        } catch (KnDAOException e) {
	            knLogger.error( methodName, "DAO Exception occurred - ", e.getMessage());
	            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
	                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
	                        "Profile Information not found", e);
	            }
	            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
	                    "Error while retrieving Profile information", e);
	        } catch (Exception e) {
	            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
	            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
	                    "Failed to get Profile information", e);
	        } finally {
	            knLogger.debug( methodName, "EXIT");
	        }
	}


	public Map<Integer, Integer> retrivePreConfigGroupProfileInfoByProfileId(String userProfileId,KnPersisterTxn persisterTxn) throws KnBOException {
		 String methodName = "retrivePreConfigGroupProfileInfoByProfileId(String)";
	        try {
	            return profileHandler.retrivePreConfigGroupProfileInfoByProfileId(userProfileId,persisterTxn);
	        } catch (KnDAOException e) {
	            knLogger.error( methodName, "DAO Exception occurred - ", e.getMessage());
	            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
	                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
	                        "Profile Information not found", e);
	            }
	            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
	                    "Error while retrieving Profile information", e);
	        } catch (Exception e) {
	            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
	            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
	                    "Failed to get Profile information", e);
	        } finally {
	            knLogger.debug( methodName, "EXIT");
	        }
	}

    public Map<String,List<Integer>> retriveUpmIdGroupMapByUserProfileId( List<String> userProfileIdList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "retriveUpmIdGroupMapByUserProfileId()";
        try {
            return profileHandler.retriveUpmIdGroupMapByUserProfileId(userProfileIdList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception occurred - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnBOException(KnErrorCodes.BOEntity.SUBSC_NOT_REGISTERED,
                        "Profile Information not found", e);
            }
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Error while retrieving Profile information", e);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured - ", e.getMessage());
            throw new KnBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Failed to get Profile information", e);
        }
    }
   
   
}
