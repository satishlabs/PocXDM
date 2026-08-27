/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpXdmSublistDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        07-02-2011      7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'N
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

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistSubscDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSublistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistDistributionRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMdnListPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpSublistListPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnMdnDetailsPersistDTO;

import java.util.*;

public interface ICorpXdmSublistDAO {

    public void updateCorpPairedContListId(int corpId, int pairedListId, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Collection<Integer> getSubsMappedSublistId(KnCorpSublistListPersistDTO sublistListDTO,
                                                      KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<Integer> getPoCSublistIdInfo(Collection<Integer> sublistid, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Collection<Integer> getCommonContactListRejectForGrp(Collection<Integer> sublistid, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException;
    //public Collection<KnCorpSubscriberDTO> getSublistContactDetails(int sublistId, KnPersisterTxn persisterTxn);


    public void deleteSublistMembers(Collection<String> contactList, int sublistId
            , KnPersisterTxn persisterTxn) throws KnDAOException;

    public void pushSublistToSubscriber(Collection<Integer> addedSubListId, KnCorpSubscriberDTO subsDTO
            , KnPersisterTxn persisterTxn) throws KnDAOException;

    public void removeSublistMappingForSubscriber(Collection<Integer> addedSubListId, KnCorpSubscriberDTO subsDTO
            , KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<String>> sublistPushedToSubscribersFrmList(KnIPCorpSublistSubscDistDTO distDTO
            , KnPersisterTxn persisterTxn) throws KnDAOException;

    public LinkedList<Integer> sublistPushedToSubscribers(String MDN, int corpId
            , KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> sublistPushedToSubscribersAndCount(LinkedList<Integer> sublistIds
            , KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, String> getSublistPushedSublistCntForSubsc(KnIPCorpSublistSubscDistDTO subsRequestDTO
            , KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> getSublistContactList(int sublistId
            , boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void addSublistMembers(Collection<KnCorpSubscriberDTO> finalMemberList, int sublistId, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public void modifySublistName(String sublistName, int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void createSublist(KnCorpSublistDTO sublistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpGroupInfoPersistDTO> getGroupsMappedToSublist(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException;

    /*public Map<String, KnOPDirChgDTO> updateSubcribersGroupEtags(Map<Integer, Collection<KnCorpGroupMemPersistDTO>>
                                                                         groupMembers, Map<String, KnOPDirChgDTO> eTags, int documntChngType, KnPersisterTxn persisterTxn) throws KnDAOException;
*/
    public KnCorpSublistDTO getSublistInfo(int sublistId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpSublistDTO getSublistInfoWithHierarchyId(int sublistId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException;


    public int getCommonContactListReject(int sublistId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean isSublistExistInCorporate(int corpId, String sublistName, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> selectDistinctMembers(Collection<KnCorpSubscriberDTO> addedMemList, Collection<Integer>
            addedSublistIds, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> getDistinctMembersForSublist(Collection<String>
                                                                                addedMdnList, Collection<Integer> addedSublistIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getCorpSublistCount(int corpId, int listType, boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException;

    public Collection<KnCorpSublistDTO> getAllSublist(int corpId, int listType, int nextToken, int fetchSize, boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId)
            throws KnDAOException;

    public KnCorpSublistRespDTO getSublistDetails(String operationType, int sublistId, int corpId, int maxContactLimit, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public void deleteSublist(int sublistId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSublist(List<Integer> sublistId, int corpId, List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCorpListDistReference(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCorpListDistGroupReference(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpSublistDistributionRespDTO getDistributionList(int sublistId, int filterType, int
            maxContactLimit, int maxGroupMemberCount, int corpId, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpSublistDTO> getGroupsSublistList(int groupId, String pttServerId, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Collection<Integer> getGroupsSublistIdInRemovedList(int groupId, Collection<Integer>
            removedSublistIds, String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnMdnDetailsPersistDTO getGroupPrivateMemberListInfoFromMdnList(KnCorpMdnListPersistDTO
                                                                                   corpMdnListPersistDto, int groupMemberlistId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void pushSublistListToSubscriberList(KnIPCorpSublistSubscDistDTO distDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<Integer, Integer> fetchAndUpdateSublistEtag(Collection<Integer> sublistIdList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSublistsSubscribersContactCount(Collection<Integer> sublistIds, int maxContactLimit, int
            maxGrpContactLimit, int maxDispGrpContactLimit, KnPersisterTxn persisterTxn, int maxBGMemCount) throws KnDAOException;

    public int getSublistCountByNameExcludingCurrentSublist(int corpId, String sublistName, int sublistId,
                                                            KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<String>> getSubcriberSublistMemberShipList(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public void deleteMembersFromAllSublist(Map<Integer, Collection<String>> sublistMemberMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteAllSublistMembers(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertMemberInAllSublists(Map<Integer, Collection<String>> sublistMemberMap, int corpId, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Collection<Integer> getCorpSublistIdList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteAllSublist(Collection<Integer> sublistIdsList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSublistRefAndSublist(int subListId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSublistDTO> getSubscMappedSublistListWithMemberCount(KnIPCorpContactDTO contactDTO, int corpListId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public ArrayList<Integer> getSharedSublistFromList(Collection<Integer> sublistLists, KnPersisterTxn persisterTxn) throws KnDAOException;

    public ArrayList<Integer> getEmptySublistFrmList(ArrayList<Integer> sharedSublists, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteAllSublistInfo(ArrayList<Integer> privateGroupSublistList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteAllCorpSublistMembers(Collection<Integer> privateGroupSublistList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertCorplistMembers(Map<Integer, Collection<KnCorpSubscriberDTO>> listMemberMap,KnPersisterTxn persisterTxn) throws KnDAOException;

     public Map<Integer, Collection<String>> getSubcriberSublistMemberShipListForAllCorporate(Collection<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<Integer, Collection<String>> getSubcriberSublistMemberShipListAsExtContact(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public void updateSublistEtag(int sublistId, long etag, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> getSubscribersInfo(Collection<String> addedMdnList, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public List<KnCorpContactDTO> getExtSubListMemDetails(List<String> extMemLst, int corpId, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Map<String, List<String>> getSublistMemMap(int sublistId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, List<String>> getSubsSublistList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCorpListDistGroupReference(List<Integer> sublistId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCorpSublistMemList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getEmptySublistIds(List<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCorpListDistribution(List<Integer> sublistIds, List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCorpListDistForMdn(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSublistInfoList(List<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException;
    public void deleteAllSubListsOfCorporate(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpSubscriberDTO> getSubscPrivateMemberList(int privateContactListId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpSubscriberDTO> getSublistDistinctMembers(Collection<Integer> addedSublistIds, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getSubscrAllSublists(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, KnCorpSublistDTO> filterSublists(List<Integer> sublistIds, int corpId, int sublistType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getSublistMembers(List<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSublistMembersForRequestMDN(Collection<Integer> sublistIds, String MDN, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpSublistDTO getSublistDetailsByName(int corpId, String subPrefix, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer,Integer> getAllTypePoCSublistIdInfo(Collection<Integer> sublistid, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException;
    
    public ArrayList<Integer> getNonSharedSublistFromList(Collection<Integer> sublistLists, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<Integer> getAllSublistForGroupIds(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn)  throws KnDAOException;

    void createSublist(List<KnCorpSublistDTO> sublistDTOList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getSubListMemberCount(int subListId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> commContctListCntForSubsc(KnIPCorpSublistSubscDistDTO subsRequestDTO
            , KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getCorpIdFromCorpListInfo(int subListId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getCommonContactListForMdns(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getAllSublistContactMdns(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getNonExisitingGroupMemberInPrivSublist(Integer groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    Map<Integer, List<String>> getNonExisitingGroupMemberInPrivSublistForGroups(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean isSublistValidRequest(List<Integer> sublistIdList, Map<String, Object> customParams, KnPersisterTxn persisterTxn) throws KnDAOException;
    public Map<String,Integer> getPaginatedContatInfo(List<String> contactMdns, int startIndex, int endIndex, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Integer getUniqueContactListCount(String contactMdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

}

