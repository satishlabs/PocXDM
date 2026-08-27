/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpXdmGroupDao.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 7, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dao.persister;


import com.kodiak.common.commdto.request.KnXDMGroupPropertyInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.*;

import java.util.*;

public interface ICorpXdmGroupDAO {

    public Collection<KnCorpGroupInfoPersistDTO> selectGroupList(int corpId, int maxMemPerGroup, String pttServerId, boolean readOnly,
                                                                 KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException;
    public Map<Integer, KnCorpGroupDTO> getGroupInfoList(int corpId, int groupId, String pttServerId, boolean readOnly,
                                                                 KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpGroupInfoPersistDTO> selectGroupListPaginated(int corpId, int maxMemPerGroup, int nextToken, int fetchSize, String pttServerId,
                                                                          boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException;

    public Set<Integer> selectGroupListCount(int corpId,String hierarchyId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpGroupInfoPersistDTO> selectSubsGroupList(String subsMdn, int corpId, int
            maxMemPerGroup, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpGroupInfoPersistDTO> getSubsGroupListForXcapOnMdn(String subsMdn, int
            maxMemPerGroup, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpGroupInfoPersistDTO> selectSubsLocGroupList(String subsMdn, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpGroupInfoPersistDTO selectGroupNameInfo(int corpId, String groupName, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<String, Integer> selectSubscriberGroupCounts(Collection<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public void createGroupInfo(KnCorpGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public KnCorpGroupInfoPersistDTO selectGroupInfo(int groupId, int corpId, int clintIntf, KnPersisterTxn persisterTxn,Boolean hiearchyCall)
            throws KnDAOException , KnXDMServerException;

    public KnCorpGroupInfoPersistDTO selectGroupInfoByGroupId(int groupId, int clintIntf, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public void insertGroupListRefInfo(int groupId, Collection<Integer> sublistIds, KnPersisterTxn persisterTxn)
            throws KnDAOException;

   /* public Collection<KnCorpGroupMemPersistDTO> selectGroupPrivateListMembers(int groupId, int corpId, int maxContactLimit,
                                                                              KnPersisterTxn persisterTxn)
            throws KnDAOException;*/

    public int getGroupCountByName(String groupName, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteGroup(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteGroupSublistRef(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteGroupPrivateList(int corpListId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteGroupDistribution(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateGroupMemberListId(int groupId, int corpSublistId, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public int getCorpGroupCount(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void addToCorpGroupDistInfo(Collection<KnCorpSubscriberDTO> actualMdnToBeAddedToGroup, int groupId,
                                       KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<Integer> getCorpGroupIdByOsmListId(int OsmListId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> updateGroupListEtag(Collection<Integer> groupIdLst, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int updateIsOSMAuthorize(Set<Integer> groupIds, String mdn,String isOSMAuthorize, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> updateGroupListEtag(Map<Integer, Integer> groupIdLst, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getGroupHavingMember(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> getGroupsAllMemberList(int groupId, int maxGroupMemberLimit, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public KnCorpGroupDTO getGroupBasicInfo(int groupId,boolean readonly, KnPersisterTxn persisterTxn)
            throws KnDAOException;
    public Integer getMemberCountFromMemberList(int groupId,boolean readonly, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<Integer, HashMap<String, Collection<String>>> getAllSubscribersGroupList(Collection<String> mdnList, int corpId, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public void removeSublistListsMappingFromGroup(Collection<Integer> removedSublistIds, int
            groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getGroupPrivateListId(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCorpGroupMemberCountEntry(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public LinkedList<String> getGroupMemberList(int groupId, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public void modifyGroupName(String groupName, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyGroupVideoPermission(Integer videoPermission, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyGroupAvatar(Integer avatar, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyGroupOSMListId(int corpId,int groupId,String OSMListId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteAllGroupsSublistRef(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteAllGroupsDistribution(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteAllGroupsCorpGroupMemberCountEntry(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteAllGroups(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteAllGrpHierarchy(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertIntoCorpGroupMemberList(Map<Integer, Collection<KnCorpContactDTO>> groupMemberList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertBulkIntoCorpGroupMemberList(Map<Integer, KnCorpContactDTO> groupMemberList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public LinkedHashMap<Integer, LinkedList<Integer>> deleteCorpGroupMemberList(LinkedHashMap<Integer, LinkedList<String>> groupMemberList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<String>> selectGroupMemberListForGroupIds(Collection<Integer> groupList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getGroupMemSize(Collection<Integer> groupList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getMCXGroupMemSize(Collection<Integer> groupList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpGroupInfoPersistDTO selectGroupBasicInfo(int groupId, int corpId, int clientIntf, KnPersisterTxn persisterTxn,Boolean hiearchyCall) throws KnDAOException;
    public KnCorpGroupInfoPersistDTO getGroupBasicInfoDetailsWithoutCorpId(int groupId, int clientIntf, KnPersisterTxn persisterTxn,Boolean hiearchyCall) throws KnDAOException;

    public Collection<Integer> getGroupsSublistListFromDB(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnCorpGroupMemberDTO> getGroupSupervisorMembers(int groupId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getExternalSubscriberGroupCount(Collection<String> externalMdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateGroupMemberListSupervisorList(Collection<KnCorpGroupMemberDTO> supervisorMemberList, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteAllCorpGroupMemberList(Collection<Integer> groupIdList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getAllGroupMdns(Collection<Integer> groupIdList,int corpId,KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, HashMap<Integer, String>> getGroupListStatus(Collection<Integer> groupIdLst, KnPersisterTxn persisterTxn) throws
            KnDAOException;

    public ArrayList<Integer> getAllGroupsPrivateList(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public LinkedHashMap<Integer, LinkedList<String>> getGroupDispatcherSubscriber(Collection<Integer> groupIdsList, int supervisor, KnPersisterTxn persisterTxn) throws KnDAOException;

    public LinkedHashMap<Integer, LinkedList<String>> getGroupMdnSubscriber(Collection<Integer> groupIdsList, int memberType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<String>> getGroupLocWatcherSubscriber(Collection<Integer> groupIdList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateGroupType(int groupType, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getAllSubscribersGroupListForAllCorporate(Collection<String> mdnList, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public void updateCorpGroupMemberList(Collection<Integer> groupList, String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<String>> getAllSubscribersGroupListAsExtContact(Collection<String> mdnList, int corpId, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<String>> getGroupSubscriberDistList(Collection<Integer> grpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertIntoCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>> adddedmembers, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertIntoCorpGrpDistInfo(Map<Integer, String> adddedmembers, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteFrmCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>> deletedmembers, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getSubsNotInDispatchGroupFromMDNList(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getSubsNotInNormalDispatchGroupFromMDNList(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;


    public Collection<String> getLocWatcherMdn(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getCorpGroupCount(int corpId, List<Integer> groupTypeList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean isSubscriberPartOfGroup(int groupId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnCorpGroupMemberDTO> getGroupMemberDetailsListInfo(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<String>> getAllSubscribersGroupDistForAllCorporate(Collection<String> mdnList, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<String>> getGroupSpecificSubsc(Collection<Integer> groupIds, int supervisorType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getCoporateGrpSpecificSubsc(Collection<Integer> supervisorTypes, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getGrpSpecificSubsc(Collection<Integer> supervisorTypes, Collection<Integer> groupIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getGroupListForGroupMDN(Collection<String> groupMdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public LinkedList<String> getExternalGrpMembers(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Collection<Integer>> getGroupListForSubs(Collection<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getSubsGroupIdList(String subsMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, KnCorpGrpBasicInfoDTO> getGrpDetForGetDir(List<Integer> subsGroupIdList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnCorpGroupMemberDTO> getGroupMembersList(int groupId,boolean readonly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpGroupMemberDTO> getCorpGroupMembersList(Collection<Integer> groupIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpGroupMemberDTO> getCorpGroupMemberForLocWatcher(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getCorpGroupMemberIsLocWatcher(Collection<Integer> groupIds, int isLocwatcher, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, List<String>> getCorpGroupMemberForLocSupervisor(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getTruncatedGroupMems(int groupId, int maxGroupMemberLimit, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<String, Integer> getGroupPrivtMemLst(int groupId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> getValidGrpInCorp(List<Integer> grpIdList, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> getValidGrp(List<Integer> grpIdList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getSubsGroupIdList(List<Integer> grpList, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, List<KnCorpGrpMemListDTO>> getSubsGroupIdListMap(List<String> mdnList, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public void deleteGrpMemList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteGrpDistList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean cleanUpSubsCampedGrps(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean cleanUpSubsCampedGrps(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCampedGrps(String mdn, List<Integer> grpIdList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createSubsCampedGrps(String mdn, List<KnCorpTalkGrpInfoDTO> grpList, int campedBy, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createSubsChannelGrps(String mdn, List<KnCorpTalkGrpInfoDTO> grpList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createSubsTalkGrpScanMode(String mdn, Integer mode,  KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubsTalkGrpScanMode(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpTGSPersistDTO> getSubsCampedGrp(KnIPTalkGroupDTO ipTalkGroupDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteAllCampedGroups(KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubsTalkGrpScanMode(String mdn, Integer mode, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnTalkGrpScanMode getSubsTalkGrpScanMode(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpInOutParamDTO cleanUpCampedGrp(List<Integer> deletedGrpIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpInOutParamDTO cleanUpCampedGrp(Map<Integer, List<String>> grpMembersMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubsTalkGrpScanEtag(String mdn, Integer etag, KnPersisterTxn persisterTxn) throws KnDAOException;

    void deleteSubsCampedGrps(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    void deleteSubsChannelGrps(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public ArrayList<KnCorpGroupDTO> getGrpsNameEtagInfo(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void addToCorpGroupDistInfo(List<String> distMdnList, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> selectGroupMemberList(int grpId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> deleteGroupDistInfo(List<String> mdnList, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteGroupDistInfo(Map<Integer, List<String>> groupMemMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getBroadcstGroupList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateGrpBroadcasters(Map<Integer, List<String>> groupMemMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> getGroupMemCount(Collection<Integer> groupIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getSupervisorGroups(String mdn, int supervisor, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getSupervisorGroups(List<String> mdnList, int supervisor, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<Integer> getGrpDispMemList(List<Integer> dispatcherGrpList, String MDN, String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<Integer> getGrpIdsHavingAtleastOneMember(List<Integer> dispatcherGrpList, String MDN, String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateGrpMemListProperties(Collection<KnCorpGroupMemberDTO> modifiedMembers, int grpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyGroupOverrdeDND(int overrideDnd, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public ArrayList<KnCorpGroupDTO> getGroupBasicInfoList(Collection<Integer> groupIds,boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<Integer, KnCorpGroupInfoPersistDTO> getGroupDisplayNameCorpIdMapInfo(List<Integer> groupIdLst, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, List<String>> getSubscriberGroupIds(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> selectSubscrGrpCounts(List<String> finalGroupMembersinDB, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<Integer> selectGroupIdList(int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getCampedGroupCount(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getSubscTgscDocEtag(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<Integer> groupsPushedToSublists(Collection<Integer> removeSublistIds, String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<Integer> getGroupSupervisorList(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpGroupDTO getGroupDetailsByName(String grpPrefix, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpTGSPersistDTO> getSubsChannelGrp(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyGroupLmrInteropCapable(int lmrInteropCapable, Collection<Integer> groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyGroupUGWParameter(int ugwInterop, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyGroupRecordingFsParameter(int recordingFs, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void modifyAuthorizedLargeTGParameter(int authorizedLargeTG, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;
    public Map<Integer, Integer> getSGCorpGroupMembersCount(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getValidGrpTypeInCorp(Collection<String> grpIdList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, String> selectGroupMemberForGroupIds(Collection<Integer> groupIds, String memberMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyGroupEmergAttributes(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpGroupInfoPersistDTO> selectTpGroupList(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpGroupDTO getGroupIdByName(String grpPrefix, int clientIntf, String ownerMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getGroupMembersClientTypeMap(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean updateGroupOwner(String oldGroupOwner, String newGroupOwner, int corpID, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean updateGroupOwner(List<String> oldGroupOwner, String newGroupOwner, int corpID, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getSubscribersGroupList(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, List<String>> getCorpGroupIds(Collection<Integer> corpGroupIdList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getGroupMembersClientType(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<Integer> getCorpGroupCountIntf(int corpId, int intf, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getCorpGroupCountIntfPerOwner(int corpId, int intf, String grpOwner, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> selectSubscriberAbdgGroupCounts(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getExternalSubscriberAbdgGroupCount(Collection<String> externalMdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<Integer> getOwnerGroupIds(String mdn, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<Integer> getOwnerGroupIds(List<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpAddlTGInfoDTO> getSubsAddlTGList(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpAddlTGInfoDTO> getSubsAddlTGList(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubsAddlTGList(Collection<KnCorpAddlTGInfoDTO> corpAddlTGInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertSubsAddlTGList(Collection<KnCorpAddlTGInfoDTO> corpAddlTGInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubsAddlTalkGroup(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDocChgDTO> insertOrUpdateAddlTGInfo(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<Integer>> getZoneChannelMap(KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubsAddlTalkGroupDoc(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpAddlTGInfoDTO> getSubsAddlDetails(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDocChgDTO> deleteFromSubsAddInfoInfo(Collection<String> mdnList, Map<String, KnOPDocChgDTO> addlTGMap,
                                                                KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Long> getSubsAddlEtagMap(Collection<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertSubsAddlTGDoc(Map<String, Long> subsEtagMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpGroupDTO> getAllLargeGroups(KnPersisterTxn persisterTxn) throws KnDAOException;

    void updateGrpMemListLocWatchers(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    void updateIsLargeGrpFlag(Map<Integer, Integer> groupLrgGrpFlagMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    int getLrgAbdgGroupCount(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteAllTGListGrpIds(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getLocWatcherAndDispatcher(Collection<Integer> groupIdLst, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpInOutParamDTO cleanUpTGSSGrp(List<Integer> deletedGrpIds, KnPersisterTxn persisterTxn) throws KnDAOException ;

    public Map<String, Boolean> getTgssFeatureBit(Set<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpInOutParamDTO cleanUpTGSSGrpMdn(Integer deletedGrpId,List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException ;
	
	public Map<Integer, Collection<KnCorpGroupMemberDTO>> getGrpSubsc(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnCorpGroupMemberDTO> getGroupMembersListForWcsrOrCatUi(int groupId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getRealMdns(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    List<String> getExistingGroupName(List<String> grpNameList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    void createBulkGroupInfoDetails(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, KnPersisterTxn persisterTxn) throws KnDAOException;

    void addBulkSublistsToGroup(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, KnPersisterTxn persisterTxn) throws KnDAOException;

    List<KnCorpGroupInfoPersistDTO> selectProfileGroupList(KnIPCorpGroupProfileDTO groupProfileDTO, int maxMemPerCorpGroup, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Integer> selectGroupCountByGroupProfileId(List<String> groupProfileIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Integer> getSubsScrGroupCount(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Integer> getSubsScrGroupCount(List<String> mdns, KnPersisterTxn persisterTxn, int corpId) throws KnDAOException;

    public Map<String,Integer> getSubsScrGroupCountExceptABDG(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Integer> getSubsScrGroupCountExceptABDG(List<String> mdns, KnPersisterTxn persisterTxn, int corpId) throws KnDAOException;

    Map<String, List<Integer>> getSubscriberDistGroupList(Set<Integer> groupList, KnPersisterTxn persisterTxn) throws KnDAOException;

    void updateGroupInfoProperties(Map<Integer, KnCorpGroupDTO> groupEtagList, KnCorpGroupProfilePersistDTO groupProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    void createGroupSharedCorpInfo(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpSharedCorpInfo> selectGroupSharedCorpInfo(int ownedCorpId, int groupId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfo(int ownedCorpId, Collection<Integer> groupId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer,List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfoBySharedCorpId(int sharedCorpId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<Integer, List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfoByGroupId(Collection<Integer> groupIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * Method to delete list of group ids from shared group info.
     * @param groupIds
     * @param persisterTxn
     * @throws KnDAOException
     */
    void deleteSharedGroups(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    void deleteSharedGroupsByOwnedCorp(Integer ownedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    void modifyGroup_GrpSharedFlag(Map<Integer, Integer> groupIdSharedFlagMap, String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpSharedCorpInfo> selectGroupSharedCorpInfoByOwnedCorpId(int ownedCorpId, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public List<Integer> getGroupIdsByMemberAndGroupType(String mdn,Integer groupType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpGroupInfoPersistDTO> selectSubsGroupList(String subsMdn, int maxMemPerGroup, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpGroupInfoPersistDTO> selectSubsGroupListPaginated(String subsMdn, int maxMemPerGroup, int fetchSize, int nextToken, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertGroupHiearchyMap(Integer groupId, List<String> ownerFanIds, String idType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertGroupHiearchyMap(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, List<String> ownerFanIds, String idType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void removeGroupHiearchyMapByOwnerIds(Integer groupId, List<String> ownerFanIds, String idType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void removeGroupHiearchyMapByGroupId(Integer groupId, KnPersisterTxn persisterTxn)  throws KnDAOException;

    public Map<Integer, List<String>> getGroupOwnerList(List<Integer> groupIds,String idType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, List<String>> getGroupSharedIdList(List<Integer> groupIds, String idType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer,Integer> isValidGroupForIdList(List<Integer> groupIds, List<Integer> idList, String idType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getSystemPreConfigGroupCont(KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getSharedGroupMemberBySharedAndOwnCorpids(int ownedCorpId,List<Integer> sharedCorpids, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer,KnCorpGroupInfoPersistDTO> getAllPreconfigGroupInfo(int corpId,KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public List<Integer> getIsPreConfiguredParamList(List<Integer> groupIdList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getSharedCorpIds(int GroupId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyBulkGroupProperties(List<KnXDMGroupPropertyInfoDTO> bulkGroupProperties, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpGroupInfoPersistDTO getGroupBasicDetails(int groupId, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Collection<KnCorpGroupInfoPersistDTO> getSubsAbdgGroupList(String subsMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteGroupHierarchy(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getFirstNetFanIdsByFanIds(List<String> fanIds, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException;

    public Collection<Integer> getFanList(int corpId, Collection<Integer> idListExistInReq, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> getIdValueGroupIdMap(List<Integer> groupIds, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Integer> getOwnerAndSharedContextIdByGroupIds(List<Integer> groupIds,String idType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateAllGroupsCorpGroupMemberCountEntry(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException;

    Map<Integer, Integer> getGroupHierarchyMap(List<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public List<KnCorpGroupInfoDTO> getGroupsDetailsWithoutMembers(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Integer> getGroupCountByNameForGroupsName(Collection<String> groupNames, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<Integer> getCorpGroupByOSMListIdMap(Map<Integer, Integer> groupOSMListIdMap,KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyBulkGroupName(Map<Integer, String> grpIdvsDisplayName, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyBulkGroupAvatar(Map<Integer, Integer> tempGrpIdvsAvatar, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyBulkGroupOSMListId(Map<Integer, KnIPCorpGroupInfoDTO> grpIdvsOSMListIdMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> getMaxLocWatchersCount(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> getGroupMemsCounts(Collection<Integer> groupIds , KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> getCorpIdfromGroupID(List<String> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer,Collection<Integer>> getBulkGroupsSublistListFromDB(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void updateBulkGroupType(Map<Integer, Integer> grpIdvsIsGroupTypeChanged, KnPersisterTxn persisterTxn) throws KnDAOException;
    public Map<Integer, Map<String, KnCorpGroupMemberDTO>> getBulkGroupMembersList(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, KnCorpGroupMemberDTO> getBulkGroupMembersListMap(List<Integer> groupIds, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyBulkGroupOverrdeDND(Map<Integer, Integer> groupIdVsOverrideDnd, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void modifyBulkGroupLmrInteropCapable(Map<Integer, Integer> grpIdvsIsLmrInteropFeatureChangedMap, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void modifyBulkGroupUGWParameter(Map<Integer, Integer> groupIdVsUgwParameterMap, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void modifyBulkGroupRecordingFsParameter(Map<Integer, Integer> groupIdVsRecordingFsParameterMap, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void modifyBulkGroupEmergAttributes(Map<Integer, KnIPCorpGroupInfoDTO> groupInfoDTOMap, KnPersisterTxn persisterTxn) throws KnDAOException;
    Map<Integer, KnCorpGroupInfoPersistDTO> selectBulkGroupBasicInfo(Map<Integer, Integer> groupCorpMap, int clientIntf, KnPersisterTxn persisterTxn,Boolean hiearchyCall) throws KnDAOException;
    Map<Integer, KnCorpGroupInfoPersistDTO> getBulkGroupBasicInfoDetailsWithoutCorpId(List<Integer> groupId, int clientIntf, KnPersisterTxn persisterTxn,Boolean hiearchyCall) throws KnDAOException;
    void updateBulkGroupMemberListSupervisorList(Map<Integer, Collection<KnCorpGroupMemberDTO>> supervisorMemberListMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Integer> getSubscriberBroadcastGroupCount(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<String> getGroupMemsList(Collection<Integer> groupIds , KnPersisterTxn persisterTxn) throws KnDAOException;


    public Integer getTotalMemberCountFromGroupList(List<Integer> groupIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getPaginatedSubscriberData(Integer groupId , Integer firstIndex, Integer lastIndex, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Integer getGroupIdBasedonRowNumber(List<String> mdnList,int rowNumber, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,KnCorpGroupMemberDTO> getGroupSubsBasicInfo(List<Integer> groupIdList, List<String> mdnList,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getUpmSharedCorpIds(String userProfileId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubsOsmAuthorizeInAllGroups(String mdn,String isOSMAuthorize, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getMcxGroups(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String fetchGroupPocHome(int corpGroupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getGroupsWithNullPocHome(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updatePocHome(String pocHome, int clusterId, int corpGroupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateGroupMemberCountByGroupId(Map<Integer, Integer> nonZeroMemberGroupIds, KnPersisterTxn persisterTxn);

    // Phase 6: hierarchy-scoped group sharing DAO methods
    public void insertSharedGroupHierarchyMap(Integer groupId, List<String> hierarchyIds, List<Integer> corpIds, KnPersisterTxn persisterTxn) throws KnDAOException;
    public List<int[]> getSharedGroupHierarchyMappings(Integer groupId, KnPersisterTxn persisterTxn) throws KnDAOException;
    public int countActiveGroupsByCorpPair(Integer ownedCorpId, Integer sharedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException;
}
