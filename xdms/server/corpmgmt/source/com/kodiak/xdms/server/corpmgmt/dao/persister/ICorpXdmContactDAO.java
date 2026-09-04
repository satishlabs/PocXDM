/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpXdmContactDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        07-02-2011      7.0
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

import com.kodiak.common.commdto.common.KnIdDetailsDTO;

import com.kodiak.common.commdto.common.KnXDMDeviceAddlInfoDTO;
import com.kodiak.common.commdto.request.KnXDMGroupPropertyInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.*;
import com.kodiak.common.commdto.response.KnDeviceDetailsDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupMemPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMdnListPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnMdnDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnTPVendorDetailsPersistDTO;

import java.util.*;

public interface ICorpXdmContactDAO {

    public Collection<KnCorpSubscriberDTO> getPoCSubscribersInfo(KnCorpMdnListPersistDTO
                                                                         contactListDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

//    public Collection<KnCorpSubscriberDTO> getPoCSubscribersInfo(Collection<String> addedMdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> getExternalPoCSubscriberInfo(Collection<KnCorpSubscriberDTO>
                                                                                contactListDTO, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> getSubscriberPrivateContactListInfo(KnCorpMdnListPersistDTO
                                                                                       contactListDTO, int privatelistId, KnPersisterTxn persister) throws KnDAOException;

    public void addPrivateContactList(Collection<KnCorpSubscriberDTO> privateContactList, int privateSublistId
            , KnPersisterTxn persisterTxn) throws KnDAOException;

    public void addPrivateContactList(Collection<KnCorpSubscriberDTO> privateContactList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> updateSubcribersImpactedTables(Collection<String> mdn, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> updateSubcribersImpactedTablesForUpm(Collection<String> mdn, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> updateSubcribersImpactedTables(Collection<String> mdn, Collection<KnCorpSubscriberDTO> mdnDTOList, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException;

    public void updateSubscCorpListId(String mdn, int privateCorpListId, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Collection<String> getCorporateSpecificPoCSubscribersFrmList(Collection<String> mdnList, int
            corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, String> getAllSubscriberContactCount(Collection<String> mdnList, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Map<String, Integer> getSubscriberAdditionalContactCnt(KnIPCorpSublistSubscDistDTO distDTO, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public int getFinalMemberContactCount(Collection<Integer> finalSublistIds, Collection<KnCorpSubscriberDTO>
            mdnsToBeAddedToPrivateList, KnMdnDetailsPersistDTO contactMdnPersistDto, KnPersisterTxn
                                                  persisterTxn) throws KnDAOException;

    public void removeSubscribersSublist(KnIPCorpSublistSubscDistDTO subsRequestDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<String, KnCorpSubscriberDTO> getCorpExternalSubscriber(int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<String, Map<String, KnCorpSubscriberDTO>> getCorpAllSubscribers(int corpId, int maxAllowedContactCount, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Set<Integer> getPamAccId(int corpId, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Collection<KnCorpSublistDTO> getSubsMappedSublistList(KnIPCorpContactDTO contactDTO, int
            corpListId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Collection<KnCorpSubscriberDTO>> getSubscPrivateInternalExternalContacts(KnIPCorpContactDTO
                                                                                                        contactDTO, int corpListId, int maxContactLimit, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpSubscContactListRespDTO addExternalContacts(KnIPCorpContactListDTO contactListDTO, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public KnCorpResponseDTO modifyExternalContacts(KnIPCorpContactDTO contactDTO, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Map<String, KnCorpSubscriberDTO> getSubsribersCorporateDetails(Collection<String> mdnList, int corpId, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Collection<String> getSubscMdnsHavingExtContact(KnIPCorpContactDTO contactDTO, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Collection<String> getPoCSubscPrsntForSublistFrmMdnList(Collection<String> memberList, int
            corpListId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> getSubscPrivateMemberListDetails(int privateListId, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public void deleteSubscPrivateContactList(Collection<String> removePrivateContactList, int
            privateListId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getSubscribersDistToSublist(int sublistId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public LinkedHashSet<String> selectSubsDistributionToMultipleSublist(Collection<Integer> sublistIds, String MDN, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Set<Integer> SelectCorpListIdOnlyPushedViaPrivateGroupList(Collection<Integer> sublistIds, String mdn, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public KnCorpContactListRespDTO getExternalContactDetails(Collection<KnIPCorpContactDTO> contactDTO, int
            corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> getExternalPoCSubscriberDetails(Collection<KnCorpSubscriberDTO>
                                                                                   contactsList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getSubscribersDocumentEtag(String mdn, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<String,Integer> getSubscribersDocumentEtag(Collection<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public int getSubscribersCount(String subscriberMdn, int corpId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public int selectCorpId(String extCorpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public long getCorporateEtag(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getCorporateFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getSubscriberResourceListEtag(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public long updateCorporateEtag(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getFinalMemberContactCount(Collection<Integer> finalSublistListInDB, Collection<String>
            finalMdnInPrivateList, String subscriberMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubscribersContactCount(Collection<String> mdnList, int maxSubscContactCount, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<String, KnOPDirChgDTO> updateSubcribersResourceListIndexDoc(Collection<String> mdnList, Map<String, KnOPDirChgDTO> etagMaps, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public void updateEtagForSubMdn(Map<String, Integer> mdnEtagMap, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> setPocHomeForProfileMdns(Collection<String> mdnList, Collection<Integer>
            groupIdLst, Map<String, KnOPDirChgDTO> etags, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersDirectory(Collection<String> mdnList, Collection<Integer>
    groupIdLst, Map<String, KnOPDirChgDTO> etags, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersDirectory(Collection<String> mdnList, Collection<Integer>
            groupIdLst, Map<String, KnOPDirChgDTO> etags,boolean upmDocNotify, KnPersisterTxn persisterTxn) throws KnDAOException;


    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersETags(Collection<String> mdnList, Collection<String> selfMdnList, Collection<Integer>
            groupIdLst, Map<String, KnOPDirChgDTO> etags, boolean updateResourceDaoETag, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersDirectoryClone(Collection<String> mdnList, Collection<Integer>
            groupIdLst, Map<String, KnOPDirChgDTO> etags,boolean upmDocNotify, KnPersisterTxn persisterTxn) throws KnDAOException;


    public List<String> getCorpResourceList(KnIPCorpContactDTO contactDTO, int
            maxContacts, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getCorpCommonContactList(String mdn, int
            maxContacts, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<String> getCorpNonCommonContactList(String mdn, int
            maxContacts, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getMappedSubscribersContactList(Collection<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public int getExternalContactCount(int corpId, int contactType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteExtMember(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getContactMDNs(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> updateSubscribersDirectroy(Collection<String> mdnList,
                                                                 Map<String, KnOPDirChgDTO> etagMap,
                                                                 KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Collection<KnCorpSubscriberDTO>> getSublistListDistributionList(Collection<Integer> sublistList,
                                                                                        KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCorporateExternalMembers(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getCorpSubscriberCount(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void nullifyContactCorpIdInExtTable(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getFinalMemberGroupContactCount(Collection<Integer> sublistMappedToGroup, Collection<KnCorpSubscriberDTO> privateMemberList,
                                                              Collection<KnCorpSubscriberDTO> mdnsToBeAddedToPrivateList,
                                                              KnMdnDetailsPersistDTO contactMdnPersistDto,
                                                              KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertMembersIntoCorpContactList(Map<String, Collection<KnCorpSubscriberDTO>> mdnContactListMap,
                                                 KnPersisterTxn persisterTxn) throws KnDAOException;

    public LinkedHashMap<String, LinkedList<Integer>> deleteMembersFromCorpContactList(LinkedHashMap<String, LinkedList<String>> mdnContactListMap,
                                                                                       KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Collection<String>> getSubscribersContactList(Collection<String> mdnList,
                                                                     KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateOwnerMdnInContactList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSubscribersContactList(String ownerMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnCorpSubscriberDTO> getPoCSubscriberExistMap(Collection<String> mdnList,
                                                                     KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSusbcribersCorpIdInImpactedTables(String corpId, String mdn, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<String, Integer> getSubscribersPrivateListId(Collection<String> completeMdnList,
                                                            KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Collection<KnCorpSubscriberDTO>> getSubscribersPrivateList(Map<String, Integer> subscPrivateListMap,
                                                                                  KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertCorpContactListMembers(Map<String, Collection<KnCorpSubscriberDTO>> memberOfPrivateList,
                                             KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSusbcribersFromExtContactTables(String mdn, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<Integer, String> getCorpIdListWhereIsExternalContact(String mdn, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<Integer, KnCorpSubscriberDTO> getCorpIdContactTypeWhereIsExternalContact(String mdn, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public void addExternalContactsInAllCorp(Map<Integer, KnCorpSubscriberDTO> corpIdExtContactNameMap, String newMdn, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public Map<String, KnIPCorpActivationDTO> getActivationCodeForCorpoateSubscriber(Collection<String> mdnList,
                                                                                     KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnIPCorpActivationDTO> getActivationCodeForCorpoateSubscriber(Collection<String> mdnList, boolean readOnly,
                                                                                     KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateCorporateEtagForIdList(Collection<Integer> corpIdList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Collection<KnCorpSubscriberDTO>> getSubscribersContactDeatilsList(Collection<String> completeMdnList,
                                                                                         KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, String> getSubscribersName(Set<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<Integer> getExtCorpForSub(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getDeActNonHandsetSubsc(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnCorpSubscriberDTO> getSubscIsMemOfDispGrpDetails(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getPocSubscribersDetails(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,Integer> getMdnCorpIdMapping(Collection<String> mdnList,  KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> getSubscriberProfileInfo(Collection<String> mdnList, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> getSubscriberProfileDetails(Collection<String> mdnList, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnSubscriberDTO> getSubscribersNameForCorp(List<String> mdnList, int corpId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> getExternalPoCSubscriberInfo(List<String> extContList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> getExternalPoCSubscriberInfo(List<String> extContList, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnSubscriberDTO> getExtContName(List<String> extContList, int corpId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getSubsContactCount(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpGroupMemPersistDTO> getGrpMemDetails(List<String> internalMemLst, Map<String, Integer> contCountmap, int
            corpId, int maxContact, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpGroupMemPersistDTO> getExtGrpMemDetails(List<String> extMemLst, int corpId, boolean read, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Map<String, KnCorpSubscriberDTO> getSubscriberDto(List<String> mdnList, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpContactDTO> getSubsDetails(String operationType, List<String> membersList, int corpId, int
            maxContactLimit, Map<String, Integer> contCountMap, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpSubscriberDTO selectPocSubscriberInfo(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getCorpSubsWithNoGrps(int pamAccId, int clientType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getCorpSubsWithNoConts(int pamAccId, int clientType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, List<String>> getSubsContactList(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCorpContactList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSusbcribersFromExtContactTables(List<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public List<Integer> getCorpIdList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifySubsCorpFeatureSet(KnCorpSubscriberDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpSubscriberDTO getSubscriberDetail(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean isExternalSubscriber(String mdn, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean isDispatchMemberPresent(List<String> mappedMdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public ArrayList<String> getInternalNonDispatchMember(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public ArrayList<String> getDispContacts(Collection<String> nonDispGrpMember, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Boolean> getLocationPubFeaturebit(ArrayList<String> pocHome, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getInternalSubscriberCount(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getExternalSubscriberCount(int corpId, int contactType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpContactBean getInternalSubscriberList(int corpId, Integer filterType, Integer fetchSize, Integer nextToken, Integer sortType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Map<String, Object>> getInternalSubscriberPackageMap(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpContactBean getInternalInteropSubscriberList(int corpId, Integer filterType, Integer fetchSize, Integer nextToken, Integer sortType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnExtSubsDetailsDTO> getExternalSubscriberList(int corpId, Integer filterType, Integer fetchSize, Integer nextToken, Integer sortType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnExtSubsDetailsDTO> getNniSubscriberList(int corpId, Integer filterType, Integer fetchSize, Integer nextToken, Integer sortType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getSubscriberContactCount(Set<String> keySet, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;


    public Map<String, KnCorpSubscriberDTO> getExtSubsMap(List<String> extSubsList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getExtSubsrProfilelist(List<String> extSubsList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnCorpSubscriberDTO> getSubsribersCorporateDetails(Collection<String> mdnList, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public Collection<KnCorpGroupMemberDTO> getMemDetsils(int groupId, String mdn , KnPersisterTxn
            persisterTxn , String pptServerId) throws KnDAOException;

    public KnMdnDetailsPersistDTO getPoCSubscriberDetails(Collection<String> mdnList, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    public List<KnCorpSubscriberDTO> getCorpSubscrDetails(List<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Boolean> getSubscrBCGrpBit(List<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    boolean isExternalContExist(String mdn, int corpid, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnMDNInfoDto> getSubscrReverseContacts(ArrayList<Integer> integers, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpGWLinkedAccountInfoDTO getCorporateLinkedAccountInfo(String refId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, String> getVendorID(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnTPVendorDetailsPersistDTO getVendorDetails(String vendorID, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getGroupMdnInList(Collection<String> mdnList, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpSubscriberDTO> getGroupMdnInListDTO(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Boolean> getTgscFeatureBit(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getMdnSpecificToClient(int corpId, int client_type, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getMdnsSpecificToClients(int corpId, Collection<Integer> client_type, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean getIsGWEnabledForCorp(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Boolean> getClientTypeConfigDetails(KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<Integer> getCorporatePamClientTypes(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getPoCSubsDetails(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> updateAndGetDirecEtag(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnCorpSubscriberDTO> getCorpExtContact(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getSubscribersContactList(List<String> completeMdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpSubscriberDTO selectSubsInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean checkValidHierarchySubs(List<String> mdnList,Map<String, Object> customParams, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getExtSubsrProfile(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnCorpSubsEntitiesDTO> getSubsEntitiesDetails(List<String> mdnList,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, String> getMdnByUsingAliasMdn(List<String> aliasMdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, String> getMdnByUsingUserId(List<String> userIdList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateSubsProfileLastUpdateTime(KnCorpSubscriberDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer,Integer> getUpIndexCountMap(Collection<Integer> userprofileIndexes, Integer corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    Map<String, Integer> getCorpIdMap(Set<Integer> corpIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    Map<String, Integer> getCorpIdMapByExtCorpIds(Set<String> extCorpIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersDirectoryBCG(Collection<String> mdnList, Collection<Integer>
            groupIdLst, Map<String, KnOPDirChgDTO> etags, KnPersisterTxn persisterTxn,String xdmsHomePttId) throws KnDAOException, KnCorpBOException;

    public void getAndUpdateBulkDirectoryEtag(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateBulkSubsTS(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getOwnCorpRegroupMembers(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> updateSetSubcribersDirectory(Collection<String> mdnList
            , Map<String, KnOPDirChgDTO> etags, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Set<Integer> getCorpIdFromMdnList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> updateRegroupSubscribersDirectory(Collection<String> mdnList,
                                                                 Map<String, KnOPDirChgDTO> etagMap,
                                                                 KnPersisterTxn persisterTxn) throws KnDAOException;
    public boolean ifMdnisSGMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnDeviceDetailsDTO> getDeviceList(int corpId, Integer filterType, Integer fetchSize, Integer nextToken, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnXDMDeviceAddlInfoDTO> getDeviceAddInfoMap(List<String> deviceList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpDeviceInfoRespDTO getDeviceDetails(KnIPDeviceInfoDTO deviceInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateCorpFs(String corpFs,String corpId,KnPersisterTxn persisterTxn) throws KnDAOException;

    public void modifyBulkGroupProperties(List<KnXDMGroupPropertyInfoDTO> bulkGroupProperties, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getCorpDeviceCount(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getGrpMemDetails(List<String> internalMemLst, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Map<String, Object>> getCorpAllInternalSubscribers(Collection<Integer> idListExistInDB, int idType, int corpId, int maxAllowedContactCount,
                                                                          int fetchSize, int nexttoken, int sortType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getCorpAllInternalSubscribersCount(Collection<Integer> idListExistInDB, int idType, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getMdnsLessThanThirteenPv(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    List<KnIdDetailsDTO> getBanDetailsByBanFanId(List<Integer> idList, int idType, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteBulkSubscPrivateContactList(Map<Integer, List<String>> groupIdVsRemovedMdnsListMap, Map<Integer, Integer>
            groupIdVsGroupPrivateListMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getExtSubsrProfilelistMap(List<String> extSubsList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnOPDirChgDTO> updateSubcribersEtagTables(Collection<String> mdn, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException;

    public long getCorporateEtagOnCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String updateCatAccessPermSet(String extCorpId, String catAccessPermSet, KnPersisterTxn persisterTxn) throws KnDAOException;


    public String selectCorpFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateDispMem(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteCorpContactMdnList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateLocwatcherMdn(String mdn, int locwatcher, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getProfileMdnsByMcids(Collection<String> mdnList, int dispGrpMem, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean isCorpHierarchyMapped(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getClusterId(Set<String> geoCodes, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, String> getPocHome(int corpId, String hierarchyId, List<Integer> clusterId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void insertAnchorPocInfo(String corpId, String hierarchyId, int clusterId, String fetchedPocHome, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateAnchorPocInfo(String corpId, String hierarchyId, int clusterId, String fetchedPocHome, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updatePocSubsInfoBatch(List<KnAllocatePocSubsUpdateDTO> updatePocSubs, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String selectOpsCorpFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, KnCorpSubscriberDTO> getSubscriberDetails(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    Map<String,Integer> fetchAllHierarchyWithDepth(String corpId,String descendantId,KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, String> getCorpIdListWhereIsExternalContact(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteBulkSubscribersContactList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSublistDTO> getSubsMappedSublistList(Map<String, KnIPCorpContactDTO> contactDTOMap, int corpListId, KnPersisterTxn persisterTxn,int corpId);

    public String selectAnsestorID(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException;

    Map<String,Set<String>> getHierarchyMappedGeocode(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getPocHomeByHierarchyIdFromAnchor(int corpid,String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException;
}
