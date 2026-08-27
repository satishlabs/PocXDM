/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnPOCSvcConfigDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.*;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubGroupDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupPersistDTO;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ************************************************************************
 * <p/>
 * File name:  IPubXdmDAO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 10, 2011           7.0
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
public interface IPubXdmDAO {


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateEtagForDirDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getCurrentEtagForDirDoc(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param contactListId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void addContacts(int contactListId, Collection<KnMemberDTO> members,
                            KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param contactListId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void modifyContacts(int contactListId, Collection<KnMemberDTO> members,
                               KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param contactListId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteContacts(int contactListId, Collection<String> members,
                               KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param contactListId
     * @param memberMdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public boolean checkContactDetails(int contactListId, String memberMdn, boolean readonly,
                                       KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param pocGroupId
     * @param memberMdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public boolean checkGroupMemberExists(int pocGroupId, String memberMdn, boolean readonly,
                                          KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param pocGroupId
     * @param readonly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getGroupMemberCount(int pocGroupId, boolean readonly,
                                   KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * //
     *
     * @param persisterTxn
     * @throws KnDAOException
     */

    public void updateContactListDocMapEtagsForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void syncAllContactDocs(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void syncAllGroupDocs(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllContacts(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllGroups(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateAllContactDocs(String mdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param newMdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateAllGroupDocs(String mdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param resourceListIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Integer> getContactListIdsForResourceDocIds(Collection<Integer> resourceListIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param contactListIds
     * @param readOnly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Collection<KnMemberDTO>> getContactListMemberInfos(Collection<Integer>
                                                                                   contactListIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void createOMAGroupDoc(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateOMAGroupDocXmlDoc(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void replaceOMAGroupDoc(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateGroupDisplayName(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * Gets the group details XML for the owner MDN
     *
     * @param mdn
     * @param xcapDocUri
     * @param etag
     * @param readOnly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public InputStream getCurrentOMAGroupDocXmlDoc(String mdn, String xcapDocUri, int etag, boolean readOnly,
                                                   KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param grpInfoPersistDTO
     * @param xcapDocUri
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void getCurrentOMAGroupDoc(KnPubGroupInfoPersistDTO grpInfoPersistDTO, String xcapDocUri,
                                      KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param groupDispName
     * @param persisterTxn
     * @throws KnDAOException
     */
    public KnPubGroupInfoPersistDTO getOMAGroupDocForGroupName(String mdn, String listServiceUri, String groupDispName,
                                                               KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnPubGroupDTO> getAllGroupDocsForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnPubGroupDTO> getAllGroupsForMdn(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param groupDocs
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateAllOMAGroupDocs(Collection<KnPubGroupDTO> groupDocs, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param xcapDocUri
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getGroupDocId(String xcapDocUri, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param pocGroupId
     * @param ownerMdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnPubGroupInfoDTO getGroupDetails(int pocGroupId, String ownerMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param groupDocId
     * @param ownerMdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnPubGroupInfoDTO getPubGroupDetails(int groupDocId, String ownerMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getCurrentOMAGroupDocEtag(KnPubGroupInfoPersistDTO groupInfoPersistDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getCurrentOMAGroupDocEtag(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param groupDocId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteOMAGroupDoc(int groupDocId, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void createPocGroupDocMap(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param pocGroupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deletePocGroupDocMap(int pocGroupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param groupDocId
     * @param etag
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateEtagForPOCGroupDocMap(int groupDocId, int etag, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     *
     * @param groupDocId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
//    public int getPocGroupIdForGroupDocId(int groupDocId, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param groupDocIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Integer> getPocGroupIdsForGroupDocIds(Collection<Integer> groupDocIds, boolean
            readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void createPOCGroup(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn
            persisterTxn) throws KnDAOException;

    /**
     * @param pocGroupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deletePocGroup(int pocGroupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param mdn
     * @param listServiceURI
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deletePocGroupForMdn(String mdn, String listServiceURI,
                                     KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param pocGroupId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void addPocGroupMembers(int pocGroupId, Collection<KnGroupMemberDTO> members,
                                   KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param pocGroupId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void modifyPocGroupMembers(int pocGroupId, Collection<KnGroupMemberDTO> members,
                                      KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param pocGroupId
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deletePocGroupMembers(int pocGroupId, Collection<String> members,
                                      KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param pocGroupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllPocGroupMembers(int pocGroupId, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param groupName
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int countNumberOfGroupsOwnedByMDN(String mdn, String groupName,
                                             KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int countNumberOfContactMembers(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param mdn
     * @param members
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void addContactsToPOCContactList(String mdn, Collection<KnMemberDTO> members,
                                            KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param contactMDNs
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteContactsFromPOCContactList(String mdn, Collection<String> contactMDNs,
                                                 KnPersisterTxn persisterTxn) throws KnDAOException;

    //========= req ===    rqHistoryBasedPresence_23 : Populating the DG.publicContactCount table

    /**
     * @param MDN
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getPublicContactCount(String MDN, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param MDN
     * @param contactCount
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateContactCount(String MDN, int contactCount, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param MDN
     * @param contactCount
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void addContactCount(String MDN, int contactCount, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param MDN
     * @param persisterTxn
     */
    public void deleteContactCount(String MDN, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param oldMdn
     * @param newMDN
     * @param contactCount
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateContactMDN(String oldMdn, String newMDN, int contactCount, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdns
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllContacts(List mdns, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdns
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllGroups(List mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param MDN
     * @param persisterTxn
     */
    public void deleteContactCount(List MDN, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<String> getAllListServiceUrisForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param mdn
     * @param readonly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getContactListIdForMDN(String mdn, boolean readonly, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getResourceListEtagForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getResourceListEtagForMdn(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<Integer> getContactListIdForMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;


    public Integer getCorpListIdForMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> getMemberMdnsForCorpListId(Collection<Integer> corpListId,Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException;


    /**
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnMemberDTO> getMembersClientType(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * @param pocGroupId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnGroupMemberDTO> getPocGroupMembers(int pocGroupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnAuthDocDTO getAuthorizationDocDetails(String mdn,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnEmergencyDocDTO getEmergencyDocDetails(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnGroupUsageListDocDTO getGroupUsageListDocDetails(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnMCPTTPermInfoDTO> getMCPTTPermInfo(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnMCPTTPermInfoDTO> getMCPTTPermInfoOnTargetMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getUserServiceStatus(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, Integer> getTargetMdnsCorpid(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnMCPTTPermInfoDTO getMCPTTPermInfoOnTarget(String mdn, String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnMCPTTPermInfoDTO getMCPTTPermInfoByMdns(String mdn, String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateAuthorizationDocDetails(String mdn, long etag, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateMCPTTDiscreetEnabled(String authMdn, List<String> targetMdns, int discreetListenerStatus,KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateDiscreetEnabledForTarget(List<String> targetMdns, int discreetListenerStatus, long lastProfileUpdateTime, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateServiceAuthStatusForTarget(List<String> targetMdns, int userServStatus, int finalEerviceAuthStatus, long lastProfileUpdateTime, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getAllAuthMdsForTarget(String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateMdnsEtagForAuthDoc(List<String> authMdnsList, long etag, KnPersisterTxn persisterTxn)throws KnDAOException;

    public KnSubsAddEmgrConfigDTO getSubAddEmergencyConfig(String mdn, boolean readOnly, KnPersisterTxn persisterTxn)throws KnDAOException;

    public List<KnEmgrDestinationInfoDTO> getEmergencyDestinationInfo(String mdn, boolean readOnly, KnPersisterTxn persisterTxn)throws KnDAOException;

    /**
     * Method to get the corporate detaisl from dg.poccorpinfo tables by external corporate ID.
     * @param extCorpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    KnCorpProfileDTO getCorpProfileDetails(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * Method to retrieve the subscriber details.
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    List<KnMemberDTO> getMembersDetails(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * Method to get the third party ID.
     * @param vendorId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    int getTPId(String vendorId, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * Method to return a Map of MDN and respective third party ID.
     * @param tpMdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    Map<String,Integer> getMdnTPidMap(List<String> tpMdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * Method to get the external contacts in the corporation
     * @param extContactList
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    List<String> getExtContactList(List<String> extContactList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * Interface to update the OMA Group, listserviceuri, groupdocuri and displayname for a groupdocid
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    void updateOMAGroupDetail(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     * Interface to update the listserviceuri for llist of groups
     * @param pocGroups
     * @param persisterTxn
     * @throws KnDAOException
     */
    void updatePocGroupsListSrvUri(List<KnPubGroupPersistDTO> pocGroups, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String, String> getUfmiForContactMDNs(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateContactUfmi(String mdn, String contactMDN, String ufmi, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnTGSSDocDTO getTGSSDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnTGSSDocDTO getTGSSDoc(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateEtagTGSSDoc(String mdn, long etag, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getSSGroupIds(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void addSSChannelGrpInfo(String mdn, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteSSChannelGrpInfo(String mdn, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getCorpMaxSSDDCount(int corpid,KnPersisterTxn persisterTxn) throws KnDAOException;

    public int getPOCMaxSSDDCount(KnPersisterTxn persisterTxn,String pocPttServerId) throws KnDAOException;

   // public int getPOCMaxSimDynSession(KnPersisterTxn persisterTxn,String pocPttServerId) throws KnDAOException;

   // public int getMaxSimDynSession(String ownerMdn,KnPersisterTxn persisterTxn) throws KnDAOException;

   // public int getCorpMaxSimDynSession(int corpId,KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnPOCSvcConfigDTO retrievePOCSvcConfig(String pocPttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public Collection<String> getPrivateContactsForMDN(String mdn,Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public String getRealMdnForProfileMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<String,KnMemberDTO> getExtContactListByContactMDN(List<String> extContactList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    /**
     *
     * @param mdn
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public boolean isExternalContExist(String mdn, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getCommonCorpListIdForMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getCommonContactListForMdns(String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<String> getNonCommonContactListForMdns(String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> getSsGroupIdsCorpInfo(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, Integer> getCorpGrpMemCount(Collection<Integer> grpIdList,String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException;

}