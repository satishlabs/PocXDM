/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.helper;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;

import static com.kodiak.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND;
import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import static com.kodiak.common.resources.KnConstants.FEATURE_SET.*;
import static com.kodiak.common.resources.KnGeneralUtil.convertBitSetToHexString;
import static com.kodiak.common.resources.KnGeneralUtil.convertHexStringToBitSet;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.NON_FIRSTNET_FAN;
public class KnCorpUserProfileUtil {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpUserProfileUtil.class);

    public void createUserProfile(String profileId,KnCorpUserProfileDTO userProfile,
                                  String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="createUserProfile()";
        knLogger.debug(methodName, "ENTRY userProfile:",userProfile);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.createUserProfile(profileId,userProfile, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateUserProfile(String corpId, String userProfileId, KnCorpModifyUserProfileDTO userProfile,
                                  String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException, DocumentNotFoundException {
        final String methodName="updateUserProfile()";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateUserProfile(corpId,userProfileId,userProfile, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteUserProfile(String corpId,String userProfileId,
                                  String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="deleteUserProfile()";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteUserProfile(corpId, userProfileId,persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public String getProfileName(int corpId, String userProfileName, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getProfileName()";
        knLogger.debug(methodName, "ENTRY :");
        String result = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getProfileName(corpId, userProfileName);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public KnCorpUserProfileDTO getUserProfile(String corpId, String userProfileId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUserProfile()";
        knLogger.debug(methodName, "ENTRY :");
        KnCorpUserProfileDTO result=null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfile(corpId, userProfileId);
            knLogger.debug(methodName, "--->result from couchbase- ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }
    public KnCorpUserProfileDTO getUserProfile( String userProfileId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUserProfile()";
        knLogger.debug(methodName, "ENTRY :");
        KnCorpUserProfileDTO result=null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfile(userProfileId);
            knLogger.debug(methodName, "--->result from couchbase- ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }
    public List<KnCorpUserProfileDTO> getUserProfile( List<String> userProfileIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUserProfile(List<String> )";
        knLogger.debug(methodName, "ENTRY :");
        List<KnCorpUserProfileDTO> result=null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfile(userProfileIds);
            knLogger.debug(methodName, "--->result from couchbase- ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public List<KnCorpUserProfileDTO> getUserProfile( List<String> userProfileIds, String fetchSize, String startIndex,
                                                   String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUserProfile(List<String> )";
        List<KnCorpUserProfileDTO> result=null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfile(userProfileIds, fetchSize, startIndex);
            } catch (KnDAOException e) {
                throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
            }
        return result;
    }

    public Set<KnCorpGroupListInfoDTO> retriveGroupProfileInfoByProfileId(String userProfileId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return retriveGroupProfileInfoByProfileId(userProfileId, xdmsHome, false, persisterTxn);
    }

    /**
     * Method to retrieve group profile info for requested user profile Id. This is read only method.
     */
    public Set<KnCorpGroupListInfoDTO> retriveGroupProfileInfoByProfileId(String userProfileId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "retriveGroupProfileInfoByProfileId(String, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        Set<KnCorpGroupListInfoDTO> result = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);

            result = xdmDAO.retriveGroupProfileInfoByProfileId(userProfileId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);

        }
        return result;
    }

    public List<Integer> getUserProfileGroupIdBySharedCorpId(int sharedCorpId, IXDMServerDAO xdmDAO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserProfileGroupIdBySharedCorpId(int, IXDMServerDAO, KnPersisterTxn)";
        boolean returnValue =  false;
        List<Integer> grpIds = xdmDAO.getUserProfileGroupIdBySharedCorpId(sharedCorpId,persisterTxn);
        knLogger.debug(methodName, " grpIds" + grpIds);
        return  grpIds;
    }

    public Map<String,KnCorpGroupListInfoDTO> retriveGroupProfileInfoByGroupId(Integer groupId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "retriveGroupProfileInfoByGroupId()";
        knLogger.debug(methodName, "ENTRY :");
        Map<String,KnCorpGroupListInfoDTO> result=null;
        try {
        	ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);

			 result= xdmDAO.retriveGroupProfileInfoByGroupId(groupId, persisterTxn);
		} catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);

		}
        return result;
    }

    public Collection<KnCorpGroupMemberDTO> groupProfileInfoByGroupId(Integer groupId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "groupProfileInfoByGroupId()";
        knLogger.debug(methodName, "ENTRY :");
        Collection<KnCorpGroupMemberDTO> result = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);

            result= xdmDAO.groupProfileInfoByGroupId(groupId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);

        }
        return result;
    }

    public Collection<KnCorpUserProfileDTO> getUserProfileList(String corpId,String fetchSize,String startIndex,String xdmsHome,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUserProfileList()";
        knLogger.debug(methodName, "ENTRY :");
        Collection<KnCorpUserProfileDTO> result=new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileListByCorpId(corpId,startIndex,fetchSize);
            knLogger.debug(methodName, "--->result from couchbase- ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public Integer getMaxTotalCountByCorpId(String corpId,String xdmsHome,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getMaxTotalCountByCorpId()";
        knLogger.debug(methodName, "ENTRY :");
        Integer maxTotalCount=null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            maxTotalCount = xdmDAO.getMaxTotalCountByCorpId(corpId);
            knLogger.debug(methodName, "--->result from couchbase- ", maxTotalCount);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return maxTotalCount;
    }

    public Collection<KnCorpUserProfileDTO> getUserProfileListByCorpAndHierarchyId(String corpId, String hierarchyId, String fetchSize, String startIndex, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUserProfileListByCorpAndHierarchyId()";
        knLogger.debug(methodName, "ENTRY corpId:", corpId, ", hierarchyId:", hierarchyId);
        Collection<KnCorpUserProfileDTO> result = new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileListByCorpAndHierarchyId(corpId, hierarchyId, startIndex, fetchSize);
            knLogger.debug(methodName, "--->result from couchbase- ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public Integer getMaxTotalCountByCorpAndHierarchyId(String corpId, String hierarchyId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getMaxTotalCountByCorpAndHierarchyId()";
        knLogger.debug(methodName, "ENTRY corpId:", corpId, ", hierarchyId:", hierarchyId);
        Integer maxTotalCount = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            maxTotalCount = xdmDAO.getMaxTotalCountByCorpAndHierarchyId(corpId, hierarchyId);
            knLogger.debug(methodName, "--->result from couchbase- ", maxTotalCount);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return maxTotalCount;
    }

    public String getUserProfileIndexSeqence(int corpId,String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUserProfile()";
        knLogger.debug(methodName, "ENTRY :");
        String result=null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileIndexSeqence(corpId);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Error msg :",e.getErrorMessage());
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public Collection<KnCorpUserProfileDTO> getUserProfileListByUserProfileNamePattern(String corpId, String userProfileNamePattern, String fetchSize, String startIndex, String isCaseSensitiveSearch, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUserProfileListByUserProfileNamePattern()";
        knLogger.debug(methodName, "ENTRY :");
        Collection<KnCorpUserProfileDTO> result=new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileListByUserProfileNamePattern(corpId, userProfileNamePattern, startIndex, fetchSize, isCaseSensitiveSearch);
            knLogger.debug(methodName, "--->result from couchbase- ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public Collection<KnCorpUserProfileDTO> getUserProfileListForNamePattern(String corpId, String userProfileNamePattern, String fetchSize, String startIndex, String isCaseSensitiveSearch, List<String> userProfileIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        Collection<KnCorpUserProfileDTO> result = new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileListForNamePattern(corpId, userProfileNamePattern, startIndex, fetchSize, isCaseSensitiveSearch, userProfileIds);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public Collection<KnCorpUserProfileDTO> getUserProfileListForNamePattern(String corpId, String userProfileNamePattern, String isCaseSensitiveSearch, List<String> userProfileIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        Collection<KnCorpUserProfileDTO> result = new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileListForNamePattern(corpId, userProfileNamePattern, isCaseSensitiveSearch, userProfileIds);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public Integer getUserProfileCountByUserProfileNamePatternAndUserprofileIds(String corpId, String userProfileNamePattern, String isCaseSensitiveSearch, List<String> userProfileIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        Integer result = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileCountByUserProfileNamePatternAndUserprofileIds(corpId, userProfileNamePattern, isCaseSensitiveSearch, userProfileIds);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public List<KnCorpUserProfileDTO> getUserProfileListByUserProfileNamePatternSharedCorp(List<String> userProfileIds, String userProfileNamePattern, String fetchSize, String startIndex, String isCaseSensitiveSearch, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUserProfileListByUserProfileNamePatternSharedCorp()";
        knLogger.debug(methodName, "ENTRY :");
        List<KnCorpUserProfileDTO> result=new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileListByUserProfileNamePatternSharedCorp(userProfileIds, userProfileNamePattern, startIndex, fetchSize, isCaseSensitiveSearch);
            knLogger.debug(methodName, "--->result from couchbase- ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public Integer getMaxTotalCountByCorpIdAndProfileNamePattern(String corpId,String profileNamePattern,String xdmsHome,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getMaxTotalCountByCorpIdAndProfileNamePattern()";
        knLogger.debug(methodName, "ENTRY :");
        Integer maxTotalCount=null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            maxTotalCount = xdmDAO.getMaxTotalCountByCorpIdAndProfileNamePattern(corpId,profileNamePattern);
            knLogger.debug(methodName, "--->result from couchbase- ", maxTotalCount);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return maxTotalCount;
    }
    public  Map<Integer, Integer> getSubscriberUserProfileList(String mcId,int corpId,String xdmsHome,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscriberUserProfileList()";
        knLogger.debug(methodName, "ENTRY :");
        Map<Integer, Integer> result=new HashMap<Integer, Integer>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getSubscriberUserProfileList(mcId, corpId, persisterTxn);
            knLogger.debug(methodName, "--->result from timesten- ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }
    public  Map<String,Integer> getSubscriberUserProfileList(String mcId,String xdmsHome, boolean raedOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscriberUserProfileList(String, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        Map<String,Integer> result=new HashMap<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getSubscriberUserProfileList(mcId, raedOnly, persisterTxn);
            knLogger.debug(methodName, "--->result from timesten- ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }
    public Collection<KnSubscriberUserProfileDTO> getUserProfileListByUserProfileIndex(String corpId,List<Integer> userProfileIndex,String xdmsHome,KnPersisterTxn persisterTxn) throws KnCorpBOException {
		final String methodName = "getUserProfileListByUserProfileIndex()";
		knLogger.debug(methodName, "ENTRY :");
		Collection<KnSubscriberUserProfileDTO> result = new ArrayList<>();
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			result = xdmDAO.getUserProfileListByUserProfileIndex(corpId, userProfileIndex);
			knLogger.debug(methodName, "--->result from couchbase- ", result);
		} catch (KnDAOException e) {
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		return result;
    }

    public Collection<KnMDNInfoDto> getUserProfileSubscriberList(int corpId, String userProfileId, int startIndex, int fetchSize, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getUserProfileSubscriberList()";
        knLogger.debug(methodName, "ENTRY :");
        Collection<KnMDNInfoDto> result = new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileSubscriberList(corpId, userProfileId, startIndex, fetchSize, readOnly, persisterTxn);
            knLogger.debug(methodName, "--->result - ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public void updateDefaultProfile(String corpId, String profileId, List<String> mdns, int defaultProfile, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "updateDefaultProfile()";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateDefaultProfile(corpId, profileId,mdns,defaultProfile,persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    public List<String> getProfileMdnByUPId(String corpId,String userProfileId,String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return getProfileMdnByUPId(corpId, userProfileId, xdmsHome, false, persisterTxn);
    }

    public List<String> getProfileMdnByUPId(String corpId,String userProfileId,String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getProfileMdnByUPId(String, String, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        List<String> mdnList = new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            mdnList = xdmDAO.getProfileMdnByUPId(corpId, userProfileId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return mdnList;
    }

    public String getDefaultProfileMdnByMdn(String mdn,int isDefaultProfile,String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getDefaultProfileMdnByMdn()";
        knLogger.debug(methodName, "ENTRY :");
        String defaultProfileMdn = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            defaultProfileMdn = xdmDAO.getDefaultProfileMdnByMdn(mdn,isDefaultProfile,persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return defaultProfileMdn;
    }

    public String getProfileMdnByMdnUPID(String profileId, String mdn, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException{
        final String methodName = "getProfileMdnByMdnUPID()";
        knLogger.debug(methodName, "ENTRY :");
        String defaultProfileMdn = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            defaultProfileMdn = xdmDAO.getProfileMdnByMdnUPID(profileId,mdn,persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return defaultProfileMdn;
    }


    /*
    * 1. Get all the profileIds from couchBase where requested groups/sublists are pushed also get all sharedCorpInfo for the groups
    * 2. Update the lastProfileUpdateTime in CB respective to each mapped profileIds.
    * 3. Modify the mapping of groups/sublist with userProfile.
    * 4. Pass the profileIds to another utility method for notify and etag Update.
    * 5. exists should be passed 0 in case of delete subscriber.
    * 6. sharedCorpList will be null by default
    */

    public Map<String, Collection<KnDocChangeListDTO>> updateImpactedCBDocuments(String corpId, Map<Integer, Integer> sublistInfo,
                                                                                 Map<Integer, Integer> groupInfo, String exists,
                                                                                 String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {

        return updateImpactedCBDocuments(corpId, sublistInfo,groupInfo, exists,null,xdmsHome,null,null,null,persisterTxn);

    }

    public Map<String, Collection<KnDocChangeListDTO>> updateImpactedCBDocuments(String corpId, Map<Integer, Integer> sublistInfo,
                                                                                 Map<Integer, Integer> groupInfo, String exists,
                                                                                 List<String> sublistContact,String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {

        return updateImpactedCBDocuments(corpId, sublistInfo,groupInfo, exists,null,xdmsHome,null,sublistContact,null,persisterTxn);

    }

    public Map<String, Collection<KnDocChangeListDTO>> updateImpactedCBDocuments(String corpId, Map<Integer, Integer> sublistInfo,
                                                                                 Map<Integer, Integer> groupInfo, String exists,
                                                                                 String xdmsHome, List<Integer> sharedCorpList,
                                                                                 List<String> removedHierarchyIds,
                                                                                 KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return updateImpactedCBDocuments(corpId, sublistInfo, groupInfo, exists, null, xdmsHome, sharedCorpList, null, removedHierarchyIds, persisterTxn);
    }


    /*
     * 1. Get all the profileIds from couchBase where requested groups/sublists are pushed also get all sharedCorpInfo for the groups
     * 2. Update the lastProfileUpdateTime in CB respective to each mapped profileIds.
     * 3. Modify the mapping of groups/sublist with userProfile.
     * 4 .if input userProfileIds are not empty then add them to profilemdns map for cleanup
     * 5. Pass the profileIds to another utility method for notify and etag Update.
     * 6. exists should be passed 0 in case of delete subscriber.
     */

    /**
     * overloaded method for supporting sharedCorpList
     * @param corpId
     * @param sublistInfo
     * @param groupInfo
     * @param exists
     * @param xdmsHome
     * @param sharedCorpList
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     * wrapper method to support old paths
     */



    public Map<String, Collection<KnDocChangeListDTO>> updateImpactedCBDocuments(String corpId, Map<Integer, Integer> sublistInfo,
                                                                                 Map<Integer, Integer> groupInfo, String exists,
                                                                                 Set<String> userProfileIds,
                                                                                 String xdmsHome, List<Integer> sharedCorpList,
                                                                                 List<String> sublistContact,
                                                                                 List<String> removedHierarchyIds,
                                                                                 KnPersisterTxn persisterTxn)
            throws KnCorpBOException {


        final String methodName = "updateImpactedCB(String, Map<Integer, Integer>, Map<Integer, Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "corpId: ", corpId, "sublistInfo: ", sublistInfo, "groupInfo: ",
                groupInfo,"sharedCorpList : ",sharedCorpList,"userProfileIds - " ,userProfileIds);
        knLogger.debug("sublistContact :",sublistContact == null ? sublistContact : KnGDPRTemplate.mdnList(sublistContact));
        Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = new HashMap<>();
        try {
            Collection<String> profileIds = new HashSet<>();
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Map<String, Integer> userProfileIdsFromSublist = null;
            Set<KnCorpUserProfileMCPTTConfig> mcpttPermissionsConfig=new HashSet<>();
            if(sublistContact!=null&&!sublistContact.isEmpty()){
                for(String subListMdn:sublistContact){
                    mcpttPermissionsConfig.add(new KnCorpUserProfileMCPTTConfig(subListMdn,0));
                }
            }
            if (sublistInfo != null) {
                Set<Integer> subListIds = sublistInfo.keySet();
                userProfileIdsFromSublist = xdmDAO.getUserProfileIdFromSublistIds(corpId, subListIds);
                profileIds.addAll(userProfileIdsFromSublist.keySet());
            }
            Map<String, Collection<String>> userProfileIdsFromGroup = null;
            if (groupInfo != null) {
                Set<Integer> groupIds = groupInfo.keySet();
                Set<Integer> corpIds = new HashSet<>();
                corpIds.add(Integer.parseInt(corpId));
                if(sharedCorpList!=null && !sharedCorpList.isEmpty()){
                    corpIds.addAll(sharedCorpList);
                }else {
                    //Get all shared Corp ids for the input groupIds
                    // to fetch groups information across all corporations
                    Map<Integer, List<KnCorpSharedCorpInfo>> shareCorpGroupMap = xdmDAO.selectGroupSharedCorpInfoByGroupId(groupIds,false, persisterTxn);
                    if (shareCorpGroupMap != null && !shareCorpGroupMap.isEmpty()) {
                        Set<Integer> sharedCorpIds = shareCorpGroupMap.values().stream().
                                flatMap(Collection::stream).
                                map(KnCorpSharedCorpInfo::getCorpId).
                                collect(Collectors.toSet());
                        corpIds.addAll(sharedCorpIds);
                    }
                }
                userProfileIdsFromGroup = xdmDAO.getUserProfileIdFromGroupIds(corpIds, groupIds);
                profileIds.addAll(userProfileIdsFromGroup.keySet());
                if (removedHierarchyIds != null && !removedHierarchyIds.isEmpty()) {
                    String groupIdStr = String.valueOf(groupIds.iterator().next());
                    knLogger.debug(methodName, " Processing removedHierarchyIds, count=", removedHierarchyIds.size(), " corpIds=", corpIds, " groupIdStr=", groupIdStr);
                    for (String hierarchyId : removedHierarchyIds) {
                        if (hierarchyId != null) {
                            Set<String> hierProfileIds = xdmDAO.getUserProfileIdsByHierarchyId(corpIds, hierarchyId);
                            knLogger.debug(methodName, "hierarchyId=", hierarchyId, " extra UPM profileIds found=", hierProfileIds.size());
                            for (String hierProfileId : hierProfileIds) {
                                userProfileIdsFromGroup.remove(hierProfileId);
                                KnCorpModifyUserProfileDTO hierUserProfile = new KnCorpModifyUserProfileDTO();
                                hierUserProfile.setCorporateID(Integer.valueOf(corpId));
                                hierUserProfile.setRemovedGroupIdsList(Collections.singleton(groupIdStr));
                                knLogger.info(methodName, " Removing groupId=", groupIdStr, " from profileId=", hierProfileId);
                                xdmDAO.updateUserProfile(corpId, hierProfileId, hierUserProfile, persisterTxn);
                                profileIds.add(hierProfileId);
                            }
                        }
                    }
                    knLogger.debug(methodName, " After hierarchy UPM lookup, total profileIds count=", profileIds.size(), " userProfileIdsFromGroup size=", userProfileIdsFromGroup.size());
                }
            }

            if(userProfileIds!=null && !userProfileIds.isEmpty()){
                profileIds.addAll(userProfileIds);
            }


            knLogger.debug(methodName, "profileIds :", profileIds);
            KnCorpModifyUserProfileDTO userProfile = null;
            if (userProfileIdsFromGroup != null && !userProfileIdsFromGroup.isEmpty()) {
                for (Map.Entry<String, Collection<String>> entry : userProfileIdsFromGroup.entrySet()) {
                    userProfile = new KnCorpModifyUserProfileDTO();
                    String pId = entry.getKey();
                    userProfile.setCorporateID(Integer.valueOf(corpId));
                    if (userProfileIdsFromSublist != null && userProfileIdsFromSublist.containsKey(pId)) {
                        Integer sId = userProfileIdsFromSublist.get(pId);
                        if (sId != null && sublistInfo.get(sId) == 0) {
                            userProfile.setContactListID(-1);
                            userProfile.setRemovedMcpttPermissionsConfig(mcpttPermissionsConfig);
                        }
                        userProfileIdsFromSublist.remove(pId);
                    }
                    Collection<String> pushedGroups = entry.getValue();
                    if(pushedGroups != null){
                        userProfile.setRemovedGroupIdsList(pushedGroups.stream().filter(g -> groupInfo.get(Integer.parseInt(g)) != null
                                && groupInfo.get(Integer.parseInt(g)) == 0).collect(Collectors.toSet()));
                    }
                    xdmDAO.updateUserProfile(corpId, pId, userProfile, persisterTxn);
                }
            }
            knLogger.debug(methodName, "profileUpdated:1 :");
            if (userProfileIdsFromSublist != null && !userProfileIdsFromSublist.isEmpty()) {
                for (Map.Entry<String, Integer> entry : userProfileIdsFromSublist.entrySet()) {
                    userProfile = new KnCorpModifyUserProfileDTO();
                    String pId = entry.getKey();
                    userProfile.setCorporateID(Integer.valueOf(corpId));
                    Integer sId = userProfileIdsFromSublist.get(pId);
                    if (sId != null && sublistInfo.get(sId) == 0) {
                        userProfile.setContactListID(-1);
                        userProfile.setRemovedMcpttPermissionsConfig(mcpttPermissionsConfig);
                    }
                    xdmDAO.updateUserProfile(corpId, pId, userProfile, persisterTxn);
                }
            }
            knLogger.debug(methodName, "profileUpdated:2 :");
            //Updating SubsEtag and Directory Etag:
            profileMdnEtagMap = profileMdnEtagUpdate(new ArrayList<>(profileIds), corpId,null, exists, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "profileMdnEtagMap:: ", KnGDPRTemplate.mapKeyMdn(profileMdnEtagMap));
        } catch (DocumentNotFoundException ex) {
            throw new KnCorpBOException(ROW_NOT_FOUND, ex.getMessage(), ex);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return profileMdnEtagMap;
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
            ,List<String> profileMdnList,String exists, String xdmsHome,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "profileMdnEtagUpdate(List<String>, String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "userProfileIds: ", userProfileIds
                , "corpId: ", corpId,"profileMdnList :",profileMdnList == null ? profileMdnList : KnGDPRTemplate.mdnList(profileMdnList));
        Map<String, Collection<KnDocChangeListDTO>> docUriMap = new HashMap<>();
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            Set<String> fetchedProfileMdnList = new HashSet<>();
            //if profileMdn is known
            if(profileMdnList!=null&&!profileMdnList.isEmpty()){
                fetchedProfileMdnList.addAll(profileMdnList);
            }
            //if sublist,group are not part of UPM check
            if(userProfileIds!=null&&!userProfileIds.isEmpty()) {
                for (String upmId : userProfileIds) {
                    fetchedProfileMdnList.addAll(getProfileMdnByUPId(corpId, upmId, xdmsHome, persisterTxn));
                }
            }
            //UPM is not assigned to mdn check.
            if (!fetchedProfileMdnList.isEmpty()) {
                var mdnListArray = new ArrayList<>(fetchedProfileMdnList);
                var mdnSplitList = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
                for (var mdnBatchList : mdnSplitList) {
                    //updating LASTPROFILEUPDATETIME in POCSUBSCRINFO
                    var docUriMapRes = corpXdmDao.updateSubsTS(new HashSet<>(mdnBatchList), exists, persisterTxn);
                    //updating ETAG in XDM_DIRECTORY
                    corpXdmDao.updateAndGetDirecEtag(new HashSet<>(mdnBatchList), persisterTxn);
                    if (null != docUriMapRes && !docUriMapRes.isEmpty()) {
                        docUriMap.putAll(docUriMapRes);
                    }
                }
            }
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return docUriMap;
    }

    public Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagUpdateForCreateGroup(List<String> userProfileIds, String corpId
            ,List<String> profileMdnList,String exists, String xdmsHome,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "profileMdnEtagUpdateForCreateGroup(List<String>, String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "userProfileIds: ", userProfileIds
                , "corpId: ", corpId,"profileMdnList :",profileMdnList == null ? profileMdnList : KnGDPRTemplate.mdnList(profileMdnList));
        Map<String, Collection<KnDocChangeListDTO>> docUriMap = new HashMap<>();
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            Set<String> fetchedProfileMdnList = new HashSet<>();
            //if profileMdn is known
            if(profileMdnList!=null&&!profileMdnList.isEmpty()){
                fetchedProfileMdnList.addAll(profileMdnList);
            }
            //if sublist,group are not part of UPM check
            if(userProfileIds!=null&&!userProfileIds.isEmpty()) {
                for (String upmId : userProfileIds) {
                    fetchedProfileMdnList.addAll(getProfileMdnByUPId(corpId, upmId, xdmsHome, persisterTxn));
                }
            }
            //UPM is not assigned to mdn check.
            if (!fetchedProfileMdnList.isEmpty()) {
                var mdnListArray = new ArrayList<>(fetchedProfileMdnList);
                var mdnSplitList = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
                for (var mdnBatchList : mdnSplitList) {
                    //updating LASTPROFILEUPDATETIME in POCSUBSCRINFO and ETAG in XDM_DIRECTORY
                    var docUriMapRes = corpXdmDao.updateSubsTSandDirecEtag(new HashSet<>(mdnBatchList), exists, persisterTxn);
                    if (null != docUriMapRes && !docUriMapRes.isEmpty()) {
                        docUriMap.putAll(docUriMapRes);
                    }
                }
            }
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return docUriMap;
    }

    public Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagUpdateForUpm(List<String> userProfileIds, String corpId
            ,List<String> profileMdnList,String exists, String xdmsHome,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "profileMdnEtagUpdate(List<String>, String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "userProfileIds: ", userProfileIds
                , "corpId: ", corpId,"profileMdnList :",profileMdnList == null ? profileMdnList : KnGDPRTemplate.mdnList(profileMdnList));
        Map<String, Collection<KnDocChangeListDTO>> docUriMap = new HashMap<>();
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            Set<String> fetchedProfileMdnList = new HashSet<>();
            //if profileMdn is known
            if(profileMdnList!=null&&!profileMdnList.isEmpty()){
                fetchedProfileMdnList.addAll(profileMdnList);
            }
            //if sublist,group are not part of UPM check
            if(userProfileIds!=null&&!userProfileIds.isEmpty()) {
                for (String upmId : userProfileIds) {
                    fetchedProfileMdnList.addAll(getProfileMdnByUPId(corpId, upmId, xdmsHome, persisterTxn));
                }
            }
            //UPM is not assigned to mdn check.
            if (!fetchedProfileMdnList.isEmpty()) {
                var mdnListArray = new ArrayList<>(fetchedProfileMdnList);
                var mdnSplitList = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
                for (var mdnBatchList : mdnSplitList) {
                    //updating LASTPROFILEUPDATETIME in POCSUBSCRINFO
                    var docUriMapRes = corpXdmDao.updateSubsTS(new HashSet<>(mdnBatchList), exists, persisterTxn);
                    //updating ETAG in XDM_DIRECTORY
                   // corpXdmDao.updateAndGetDirecEtag(new HashSet<>(mdnBatchList), persisterTxn);
                    if (null != docUriMapRes && !docUriMapRes.isEmpty()) {
                        docUriMap.putAll(docUriMapRes);
                    }
                }
            }
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return docUriMap;
    }

    public String getUpmFsBasedOnDefUpmFs(String defaultUpmFs, KnUserProfileFSDTO ipUpmFs){
        final String methodName="getUpmFsBasedOnDefUpmFs()";
        knLogger.debug(methodName,"Entry defaultUpmFs :",defaultUpmFs," ipUpmFs :",ipUpmFs);
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
        upmFS.put(PUSHTOLOCATION.value(),ipUpmFs.getPtloc()!=null?Integer.valueOf(ipUpmFs.getPtloc()):ptloc);
        upmFS.put(TLKGRPSCANCLIENT.value(),ipUpmFs.getTgsclnt()!=null?Integer.valueOf(ipUpmFs.getTgsclnt()):tgsclnt);
        upmFS.put(BREADCUMB.value(),ipUpmFs.getBrdcrmb()!=null?Integer.valueOf(ipUpmFs.getBrdcrmb()):brdcrmb);
        upmFS.put(GEOFENCEFEATURE.value(),ipUpmFs.getGeofnc()!=null?Integer.valueOf(ipUpmFs.getGeofnc()):geofnc);
        upmFS.put(AMBIENTLISTENING.value(),ipUpmFs.getAmbientListening()!=null?Integer.valueOf(ipUpmFs.getAmbientListening()):ambientListening);
        upmFS.put(DISCRETELISTENING.value(),ipUpmFs.getDiscreteListening()!=null?Integer.valueOf(ipUpmFs.getDiscreteListening()):discreteListening);
        upmFS.put(USERCHECK.value(),ipUpmFs.getUserCheck()!=null?Integer.valueOf(ipUpmFs.getUserCheck()):userCheck);
        upmFS.put(USERENABLEDISABLE.value(),ipUpmFs.getUserEnable()!=null?Integer.valueOf(ipUpmFs.getUserEnable()):userEnable);
        upmFS.put(ONDEMLOCATION.value(),ipUpmFs.getLocPublish()!=null?Integer.valueOf(ipUpmFs.getLocPublish()):locPublish);
        upmFS.put(MCVIDEOTX.value(),ipUpmFs.getMcVideoTx()!=null?Integer.valueOf(ipUpmFs.getMcVideoTx()):mcVideoTx);
        upmFS.put(MCVIDEORX.value(),ipUpmFs.getMcVideoRx()!=null?Integer.valueOf(ipUpmFs.getMcVideoRx()):mcVideoRx);
        upmFS.put(MCVIDEOGROUPRX.value(),ipUpmFs.getMcVideoGroupRx()!=null?Integer.valueOf(ipUpmFs.getMcVideoGroupRx()):mcVideoGroupRx);
        upmFS.put(MCVIDEOCONFIRMEDPULL.value(),ipUpmFs.getMcVideoConfirmedPull()!=null?Integer.valueOf(ipUpmFs.getMcVideoConfirmedPull()):mcVideoConfirmedPull);
        upmFS.put(OPERATIONALSTATUSMESSAGING.value(),ipUpmFs.getOsm()!=null?Integer.valueOf(ipUpmFs.getOsm()):osm);
        upmFS.put(EMERGENCY.value(),ipUpmFs.getEmergency()!=null?Integer.valueOf(ipUpmFs.getEmergency()):emergency);
        upmFS.put(SELF_DND_PRIVILEGE.value(), ipUpmFs.getSelfDnDPrivilege() != null ? Integer.valueOf(ipUpmFs.getSelfDnDPrivilege()) : selfDnDPrivilege);

        knLogger.debug("upmFS.entrySet() ",upmFS.entrySet());

        BitSet upmDefFs = convertHexStringToBitSet(defaultUpmFs);
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

    public Map<String, Integer> getUserProfileIdFromSublistIds(String corpId, Collection<Integer> subListId,
			String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {

		final String methodName = "getUserProfileIdFromSublistIds(String, Collection<Integer>, String, KnPersisterTxn)";
	    knLogger.debug(methodName, "ENTRY :", "corpId: ", corpId, "subListId: ", subListId);
		Map<String, Integer> userProfileIdsFromSublist = new HashMap<String, Integer>();
		try {
			ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
			if (corpId != null && subListId != null && !subListId.isEmpty()) {
				userProfileIdsFromSublist = xdmDAO.getUserProfileIdFromSublistIds(corpId, subListId);
			}
		} catch (KnDAOException e) {
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		 knLogger.debug(methodName, "EXIT :",userProfileIdsFromSublist);
		return userProfileIdsFromSublist;
	}

	 public List<String> getProfileMdnsByUserProfileIds(String corpId,Collection<String> userProfileIds,String xdmsHome, KnPersisterTxn persisterTxn)
	            throws KnCorpBOException {
	        final String methodName = "getProfileMdnsByUserProfileIds()";
	        knLogger.debug(methodName, "ENTRY :"," corpId ",corpId," userProfileIds ",userProfileIds);
	        List<String> mdnList = new ArrayList<>();
	        try {
	            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
	            mdnList=xdmDAO.getProfileMdnsByCorpId(corpId,persisterTxn).entrySet().stream().filter(t-> userProfileIds.contains(t.getValue())).map(t-> t.getKey()).collect(Collectors.toList());

	        } catch (KnDAOException e) {
	            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
	        }
	        knLogger.debug(methodName, "EXIT :", KnGDPRTemplate.mdnList(mdnList));
	        return mdnList;
	    }

    public List<KnCorpSubscriberDTO> getProfileMdnInfoByUPId(String corpId,String userProfileId,String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getProfileMdnInfoByUPId()";
        knLogger.debug(methodName, "ENTRY :");
        List<KnCorpSubscriberDTO> subscriberDTOList = new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            subscriberDTOList = xdmDAO.getProfileMdnInfoByUPId(corpId,userProfileId,persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return subscriberDTOList;
    }

    public void updateImpactedTuPerms(int corpId,Collection<String> addTuMdns
            ,Collection<String> removeTuMdns, Collection<String> upmIds,String xdmsHome) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateImpactedTuPerms(corpId, addTuMdns, removeTuMdns, upmIds);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
        public void insertProfileGroupInfo(String userProfileId, Set<KnCorpGroupListInfoDTO> groupList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
            final String methodName = "insertProfileGroupInfo(String, Set<KnCorpGroupListInfoDTO> , String, KnPersisterTxn)";
            knLogger.debug(methodName, "ENTRY :");
            try {
                ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
                xdmDAO.insertProfileGroupInfo(userProfileId,groupList, persisterTxn);
            } catch (KnDAOException e) {
                throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
            }
        }


        public void deleteProfileGroupInfo(String userProfileId, List<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
            final String methodName = "deleteProfileGroupInfo(String, List<String>, String, KnPersisterTxn)";
            knLogger.debug(methodName, "ENTRY :");
            try {
                ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
                xdmDAO.deleteProfileGroupInfo(userProfileId,groupIds, persisterTxn);
            } catch (KnDAOException e) {
                throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
            }
        }

        public void deleteProfileGroupInfoByGroupId(int groupId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
            final String methodName = "deleteProfileGroupInfoByGroupId(int, String, KnPersisterTxn)";
            knLogger.debug(methodName, "ENTRY :");
            try {
                ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
                xdmDAO.deleteProfileGroupInfoByGroupId(groupId, persisterTxn);
            } catch (KnDAOException e) {
                throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
            }
        }

        public void deleteProfileGroupInfoByProfileId(String profileId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
            final String methodName = "deleteProfileGroupInfoByProfileId(String, String, KnPersisterTxn)";
            knLogger.debug(methodName, "ENTRY :");
            try {
                ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
                xdmDAO.deleteProfileGroupInfoByProfileId(profileId, persisterTxn);
            } catch (KnDAOException e) {
                throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
            }
        }

        public void deleteProfileSharedList(String profileId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
            final String methodName = "deleteProfileSharedList(String, String, KnPersisterTxn)";
            knLogger.debug(methodName, "ENTRY :");
            try {
                ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
                xdmDAO.deleteProfileSharedList(profileId, persisterTxn);
            } catch (KnDAOException e) {
                throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
            }
    }

    public Map<Integer, KnCorpTrustMatrixDTO> getTrustMatrixMap(String extCorpId, KnGeneralCacheUtil
            generalCacheUtil, KnCorpCommonInfoUtil commonInfoUtil, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "getTrustMatrixMap()";
        Map<Integer, KnCorpTrustMatrixDTO> matrixMap = new HashMap<>();
        try {
            List<KnCorpTrustMatrixDTO> matrixList = generalCacheUtil.getSharedCorpMatrixBySharedCorpId(extCorpId);
            if(null != matrixList && !matrixList.isEmpty()){
                Map<String, KnCorpTrustMatrixDTO> extMatrixMap = new HashMap<>();
                matrixList.forEach(item->{
                    extMatrixMap.put(item.getExtCorpId(), item);
                });
                Map<String,Integer> extIntCorpIDMap = commonInfoUtil.getCorpIdMap(extMatrixMap.keySet(),null ,persisterTxn);
                knLogger.debug(methodName, "extIntCorpIDMap ", extIntCorpIDMap);
                extMatrixMap.forEach((k,v)->{
                    matrixMap.put(extIntCorpIDMap.get(k), v);
                });
            }
            knLogger.debug(methodName, "matrixMap ", matrixMap);

        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return matrixMap;
    }

    public void deleteUserProfilesByCorpId(String corpId,
                                  String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteUserProfilesByCorpId(corpId,persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<String> getUserProfileIdsByCorpId(Integer corpId,String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        Collection<String> upmIds=new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            upmIds=xdmDAO.getUserProfileIdsByCorpId(corpId,persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return upmIds;
    }

    public Map<String, Integer> getAndDeleteGroupIdsFromUserProfile(Collection<Integer> groupIds,String xdmsHome) throws KnCorpBOException{
        Map<String, Integer> upmIdCorpIdMap=new HashMap<>();
        try{
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            upmIdCorpIdMap=xdmDAO.getAndDeleteGroupIdsFromUserProfile(groupIds);

        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return upmIdCorpIdMap;
    }

    public void deleteMcpttPermConfig(String mdn,String corpId,String xdmsHome)throws KnCorpBOException{
        try{
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteMcpttPermConfig(mdn,corpId);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteMcpttPermConfig(List<String> mdnList,String corpId,String xdmsHome)throws KnCorpBOException{
        try{
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteMcpttPermConfig(mdnList,corpId);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, String> getUserProfileIdNameBySublistId(Integer corpId, Integer subListId,
                                                               String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getUserProfileIdFromSublistIds(String, Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "corpId: ", corpId, "subListId: ", subListId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getUserProfileIdNameBySublistId(corpId, subListId);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertUserProfileHiearchyMap(String userProfileId, List<String> ownerFanIds,String idType, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertUserProfileHiearchyMap(userProfileId,ownerFanIds,idType, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteUserProfileHiearchyMap(String userProfileId,String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteUserProfileHiearchyMap(userProfileId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> userProfileIdsHiearchyMap(KnPersisterTxn persisterTxn, String xdmsHome, List<String> iD_VALUE, boolean readOnly) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.userProfileIdsHiearchyMap(persisterTxn, iD_VALUE, readOnly);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> userProfileIdsHiearchyMapFetchSize(KnPersisterTxn persisterTxn, String fetchSize, String startIndex, String xdmsHome, List<String> iD_VALUE) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.userProfileIdsHiearchyMapFetchSize(persisterTxn, startIndex, fetchSize, iD_VALUE);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> getOwnerIDByUserProfileId(String userProfileId,String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getOwnerIDByUserProfileId(userProfileId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String,List<String>> getUserProfileOwnerList(Collection<String> userProfileIds, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getUserProfileOwnerList(userProfileIds, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void removeUserProfileHiearchyMapByOwnerIds(String userProfileId
            , List<String> ownerFanIds,String idType, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.removeUserProfileHiearchyMapByOwnerIds(userProfileId,ownerFanIds,idType, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String,Integer> getSublistMemberCorpDetails(int sublistId,String corpId,String xdmsHome,KnPersisterTxn persisterTxn)throws KnCorpBOException{
        Map<String,Integer> memCorpInfo=new HashMap<>();
        try{
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            memCorpInfo=xdmDAO.getSublistMemberCorpDetails(sublistId,corpId,persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return memCorpInfo;
    }

    public void insertUserProfileSharedList(List<KnUserprofileSharedlistDTO> userprofileSharedlist,
                                  String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="insertUserProfileSharedList()";
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.insertUserProfileSharedList(userprofileSharedlist, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName," KnDAOException ",e.getMessage());
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getUserProfileOwnerinfo(ArrayList<String> profileIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getUserProfileOwnerinfo(profileIds, xdmsHome, false, persisterTxn);
    }

    /**
     * Method to retrieve User profile owner info for requested profile Ids. This is read only method.
     */
    public Map<String, Integer> getUserProfileOwnerinfo(ArrayList<String> profileIds, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUserProfileOwnerinfo()";
        knLogger.debug(methodName, "ENTRY -readOnly :", readOnly);
        Map<String, Integer> result = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileOwnerinfo(profileIds, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public List<Integer> getSharedCorpIdFromUserProfielId(String userProfileId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getSharedCorpIdFromUserProfielId(userProfileId, xdmsHome, false, persisterTxn);
    }

    /**
     * API will provide the sharedCorpIdFromUserProfielId
     * @param userProfileId
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<Integer> getSharedCorpIdFromUserProfielId(String userProfileId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSharedCorpIdFromUserProfielId(String, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        List<Integer> result=null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result= xdmDAO.getSharedCorpIdFromUserProfielId(userProfileId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public List<String> getXdmUserProfileIdsBySharedCorpId(String sharedCorpId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getXdmUserProfileIdsBySharedCorpId(String, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        List<String> result = new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getXdmUserProfileIdsBySharedCorpId(sharedCorpId, readOnly, persisterTxn);
            knLogger.debug(methodName, "--->result from XDM - ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public List<String> getMdnBySharedUserProfileCorpIds(int ownCorpId,List<Integer> sharedCorpids, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException  {
        List<String> result=new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getMdnBySharedUserProfileCorpIds(ownCorpId,sharedCorpids,persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public List<KnUserprofileSharedlistDTO> getListOfProfilesBySharedCorpIds(Integer sharedCorpId,Integer ownerCorpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException  {
        final String methodName = "getListOfProfilesBySharedCorpIds(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        List<KnUserprofileSharedlistDTO> result = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getListOfProfilesBySharedCorpIds(sharedCorpId, ownerCorpId,persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public Collection<KnSubscriberUserProfileDTO> getUserProfileListByProfileIds(List<String> userProfileIds,String xdmsHome,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUserProfileListByProfileIds()";
        knLogger.debug(methodName, "ENTRY :");
        Collection<KnSubscriberUserProfileDTO> result = new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileListByProfileIds(userProfileIds);
            knLogger.debug(methodName, "--->result from couchbase- ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public List<KnUserprofileSharedlistDTO> getUserProfileSharedListByUpmId(String userProfileId,
                                                                            String xdmsHome,
                                                                            KnPersisterTxn persisterTxn) throws KnCorpBOException  {
        List<KnUserprofileSharedlistDTO> result;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileSharedListByUpmId(userProfileId,persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public Collection<KnMDNInfoDto> getUserProfileAllSubscriberList(String userProfileId, int startIndex, int fetchSize, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getUserProfileAllSubscriberList()";
        knLogger.debug(methodName, "ENTRY :");
        Collection<KnMDNInfoDto> result = new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileAllSubscriberList(userProfileId, startIndex, fetchSize, readOnly, persisterTxn);
            knLogger.debug(methodName, "--->result - ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public void deleteCorpInfoFromUserProfileSharedList(String userProfileId,List<String> corpIds,
                                            String xdmsHome, KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="insertUserProfileSharedList()";
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.deleteCorpInfoFromUserProfileSharedList(userProfileId,corpIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName," KnDAOException ",e.getMessage());
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<KnCorpContactDTO> getUserProfileAllSubscriberListDetails(String userProfileId,String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getUserProfileAllSubscriberListDetails()";
        knLogger.debug(methodName, "ENTRY :");
        List<KnCorpContactDTO> result = new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getUserProfileAllSubscriberListDetails(userProfileId,persisterTxn);
            knLogger.debug(methodName, "--->result - ", result);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    /**
     *
     * @param userProfileIds
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<String, List<String>> getProfileMdnListByBaseMdnsList(List<String> baseMdns, String xdmPttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileMdnListByBaseMdnsList(List<String>, IXDMServerDAO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttId);
        Map<String, List<String>> mdnListMapping = xdmDAO.getProfileMdnListByBaseMdnsList(baseMdns, readOnly, persisterTxn);
        return mdnListMapping;
    }


    /**
     *
     * @param userProfileIds
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<KnUserProfileAssignedDTO> getUserProfileSubsCount(Collection<String> userProfileIds, String xdmsHome , boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUserProfileSubsCount(Collection<String>, String, boolean, persisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        List<KnUserProfileAssignedDTO> result=null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result= xdmDAO.getUserProfileSubsCount(userProfileIds, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public void validateUPMRequest(String profileId, Collection<Integer> idListInt, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException, KnCorpBOValidationException {
        final String methodName = "validateUPMRequest(String, Collection<Integer>, String, KnPersisterTxn)";
        knLogger.info(methodName,"Entry::","idListInt::", idListInt);
        List<String> ownerIdsForUPMId = getOwnerIDByUserProfileId(profileId, xdmsHome,true, persisterTxn);
        List<String> idListStr = idListInt.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        boolean flag = false;
        for (String id : idListStr) {
            if (!ownerIdsForUPMId.contains(id)) {
                flag = true;
            } else {
                flag = false;
                break;
            }
        }
        if (flag) {
            throw new KnCorpBOValidationException(NON_FIRSTNET_FAN,
                    "Invalid owner context ids", "", "", "", "DataType", "");
        }
    }

    /**
     * Compares two hexadecimal strings bit by bit.
     *
     * @param hex1 the first hexadecimal string to compare
     * @param hex2 the second hexadecimal string to compare
     * @return true if the hexadecimal strings are equal bit by bit, false otherwise
     */
    public boolean compareHexBits(String hex1, String hex2) {
        String methodName = "compareHexBits()";
        // Convert hex strings to BigInteger and compare bitwise
        BigInteger bigInt1 = new BigInteger(hex1, 16);
        BigInteger bigInt2 = new BigInteger(hex2, 16);
        boolean result = bigInt1.equals(bigInt2);
        knLogger.info(methodName, "EXIT---> result: ", result);
        return result;
    }
}
