/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;

import java.util.*;

public interface ICorpUserProfileDAO {

    public void createUserProfile(String userProfileId, KnCorpUserProfileDTO userProfile, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteUserProfile(String corpId, String profileId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateUserProfile(String corpId, String userProfileId, KnCorpModifyUserProfileDTO userProfile, KnPersisterTxn persisterTxn) throws KnDAOException, DocumentNotFoundException;

    public String getProfileName(int corpId, String userProfileName) throws KnDAOException;

    public KnCorpUserProfileDTO getUserProfile(String corpId, String userProfileId) throws KnDAOException;

    public KnCorpUserProfileDTO getUserProfile(String userProfileId) throws KnDAOException;

    public List<KnCorpUserProfileDTO> getUserProfile(List<String> userProfileIds) throws KnDAOException;

    public List<KnCorpUserProfileDTO> getUserProfile(List<String> userProfileIds, String fetchSize, String startIndex) throws KnDAOException;

    public Collection<KnCorpUserProfileDTO> getUserProfileListByCorpId(String corpId, String startIndex, String limit) throws KnDAOException;

    public String getUserProfileIndexSeqence(int corpId) throws KnDAOException;

    public Integer getMaxTotalCountByCorpId(String corpId) throws KnDAOException;

    public Collection<KnCorpUserProfileDTO> getUserProfileListByUserProfileNamePattern(String corpId, String userProfileNamePattern, String startIndex, String limit, String isCaseSensitiveSearch) throws KnDAOException;

    public Collection<KnCorpUserProfileDTO> getUserProfileListForNamePattern(String corpId, String userProfileNamePattern, String startIndex, String limit, String isCaseSensitiveSearch, List<String> userProfileIds) throws KnDAOException;

    public Collection<KnCorpUserProfileDTO> getUserProfileListForNamePattern(String corpId, String userProfileNamePattern, String isCaseSensitiveSearch, List<String> userProfileIds) throws KnDAOException;

    public Integer getUserProfileCountByUserProfileNamePatternAndUserprofileIds(String corpId, String userProfileNamePattern, String isCaseSensitiveSearch, List<String> userProfileIds) throws KnDAOException;

    public List<KnCorpUserProfileDTO> getUserProfileListByUserProfileNamePatternSharedCorp(List<String> userProfileIds, String userProfileNamePattern, String startIndex, String limit, String isCaseSensitiveSearch) throws KnDAOException;

    public Integer getMaxTotalCountByCorpIdAndProfileNamePattern(String corpId, String userProfileNamePattern) throws KnDAOException;

    public Collection<KnSubscriberUserProfileDTO> getUserProfileListByUserProfileIndex(String corpId, List<Integer> userProfileIndex) throws KnDAOException;

    public Collection<KnMDNInfoDto> getUserProfileSubscriberList(int corpId, String userProfileId, int startIndex, int fetchSize, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateDefaultProfile(String corpId, String profileId, List<String> mdn, int defaultProfile, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getProfileMdnByUPId(String corpId, String userProfileId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getDefaultProfileMdnByMdn(String mdn, int isDefaultProfile, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getProfileMdnByMdnUPID(String profileId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getUserProfileIdFromSublistIds(String corpId, Collection<Integer> subListId) throws KnDAOException;

    public Map<String, Collection<String>> getUserProfileIdFromGroupIds(Collection<Integer> corpIds, Collection<Integer> groupIds) throws KnDAOException;

    public List<KnCorpSubscriberDTO> getProfileMdnInfoByUPId(String corpId, String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateImpactedTuPerms(int corpId,Collection<String> addTuMdns,Collection<String> removeTuMdns, Collection<String> upmIds) throws KnDAOException;

    public void insertProfileGroupInfo(String userProfileId, Set<KnCorpGroupListInfoDTO> groupList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteProfileGroupInfoByGroupId(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteProfileGroupInfo(String userProfileId, List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteProfileGroupInfoByProfileId(String profileId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteProfileSharedList(String profileId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<KnCorpGroupListInfoDTO> retriveGroupProfileInfoByProfileId( String userProfileId ,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDBPersistenceException, KnDBConnectionException, KnDAOException;

    void deleteBulkProfileGroupInfo(List<Integer> groupIdList, KnPersisterTxn persisterTxn) throws KnDAOException;

    void deleteProfileGroupInfoByProfileIds(List<String> profileIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteUserProfilesByCorpId(String corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getUserProfileIdsByCorpId(Integer corpId,KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public Map<String,KnCorpGroupListInfoDTO> retriveGroupProfileInfoByGroupId( Integer groupId ,KnPersisterTxn persisterTxn) throws KnDBPersistenceException, KnDBConnectionException, KnDAOException;

    public  Collection<KnCorpGroupMemberDTO>  groupProfileInfoByGroupId( Integer groupId ,KnPersisterTxn persisterTxn) throws KnDBPersistenceException, KnDBConnectionException, KnDAOException;

    public Map<String, Integer> getAndDeleteGroupIdsFromUserProfile(Collection<Integer> groupIds) throws KnDAOException;

    public void deleteMcpttPermConfig(String mdn,String corpId) throws KnDAOException;

    public void deleteMcpttPermConfig(List<String> mdnList,String corpId) throws KnDAOException;

    public Map<String,Integer> getSublistMemberCorpDetails(int sublistId, String corpId,KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertUserProfileHiearchyMap(String userProfileId, List<String> ownerFanIds,String idType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteUserProfileHiearchyMap(String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> userProfileIdsHiearchyMap(KnPersisterTxn persisterTxn, List<String> iD_VALUE, boolean readOnly) throws KnDAOException;

    public List<String> userProfileIdsHiearchyMapFetchSize(KnPersisterTxn persisterTxn,String startIndex, String fetchSize, List<String> iD_VALUE) throws KnDAOException;

    public List<String> getOwnerIDByUserProfileId(String userProfileId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,List<String>> getUserProfileOwnerList(Collection<String> userProfileIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void removeUserProfileHiearchyMapByOwnerIds(String userProfileId, List<String> ownerFanIds,String idType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, String> getUserProfileIdNameBySublistId(Integer corpId, Integer subListId) throws KnDAOException;

    public Map<String,Integer> getUserProfileOwnerinfo(ArrayList<String> profileIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getSharedCorpIdFromUserProfielId(String userProfileId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertUserProfileSharedList(List<KnUserprofileSharedlistDTO> userprofileSharedlist, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getXdmUserProfileIdsBySharedCorpId(String sharedCorpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getMdnBySharedUserProfileCorpIds(int ownCorpId,List<Integer> sharedCorpids, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnUserprofileSharedlistDTO> getListOfProfilesBySharedCorpIds(Integer sharedCorpId,Integer ownerCorpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnSubscriberUserProfileDTO> getUserProfileListByProfileIds(List<String> userProfileIds) throws KnDAOException;

    public Collection<KnMDNInfoDto> getUserProfileAllSubscriberList(String userProfileId, int startIndex, int fetchSize, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnUserprofileSharedlistDTO> getUserProfileSharedListByUpmId(String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCorpInfoFromUserProfileSharedList(String userProfileId,List<String> corpIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpContactDTO> getUserProfileAllSubscriberListDetails(String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnUserProfileAssignedDTO> getUserProfileSubsCount(Collection<String> userprofileIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<String> getProfileMdnList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getGroupMemCountAndList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> getGroupMemberCount(Set<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> getGroupAllMemCount(Set<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getSubsDetails(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getUserPfofileIdCount(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> getProfileIdCount(Set<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getMemCountBasedOnLocWatcher(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<Integer> getGroupIdBasedOnMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getMdnCountBasedOnUPMID(List<Integer> groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<String> getProfileMdns(Set<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpUserProfileDTO> getAllDistinctUPMByCorp(String corpId) throws KnDAOException;

    public String selectCorpFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateDispMem(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpUserProfileDTO> getUserProfileListByCorpAndHierarchyId(
            String corpId, String hierarchyId, String startIndex, String limit) throws KnDAOException;

    public Integer getMaxTotalCountByCorpAndHierarchyId(String corpId, String hierarchyId) throws KnDAOException;

    public Set<String> getUserProfileIdsByHierarchyId(Collection<Integer> corpIds, String hierarchyId) throws KnDAOException;
}
