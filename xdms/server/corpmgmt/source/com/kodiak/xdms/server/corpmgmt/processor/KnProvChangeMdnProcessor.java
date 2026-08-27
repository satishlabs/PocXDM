/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.processor;


import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.lieventhandler.dto.KnLIEventDTO;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistSubscDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnMdnDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnTalkGrpScanMode;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

import static com.kodiak.common.resources.KnConstants.DEFAULT_AU;
import static com.kodiak.common.resources.KnConstants.DISABLED;
import static com.kodiak.xdms.server.common.resources.KnConstants.LIBRARY_NAME_CORP_MGMT;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.EMERGENCY_INIT_NOT_MODIFIED;

public class KnProvChangeMdnProcessor {
    private static final KnLogger knLogger = KnLogger.getLogger(KnProvChangeMdnProcessor.class);

    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnCorpContactInfoUtil contactInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;


    public KnProvChangeMdnProcessor() {
        commonInfoUtil = new KnCorpCommonInfoUtil();
        contactInfoUtil = new KnCorpContactInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
    }
    public KnCorpResponseDTO changeMdn(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        String methodName = "changeMdn(contactDTO, persisterTxn)";
        knLogger.debug(methodName, "ENTRY : - ", contactDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
        try {
            String mdn = contactDTO.getMdn();
            String newMDN = contactDTO.getNewMdn();

            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            KnSubsProfileDTO subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);//getting the profile details nn

            int corpId = subsProfile.getCorpId();
            //Below check is not required as if corpid=0, in case of public subscribers
            // need to change the ext contact details.
            /* if (corpId <= 0) {
                knLogger.error( methodName, "Invalid Corporate Subscriber passed. MDN - " , mdn);
                throw new KnCorpBOException(KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
            }*/
            contactDTO.setCorpId(corpId);
            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = subsProfile.getXdmsHome();
            knLogger.info(methodName, "xdmsHomePttId - ", xdmsHomePttId);
            if (contactDTO.getMcpttCompliance() == 1) {
                KnCorpProfileDTO corpProfileDetails = commonInfoUtil
                        .getProfileDetails(String.valueOf(subsProfile.getCorpId()), CORP_PROFILE, false, persisterTxn);
                Integer maxGroupsPerCRIClient = corpProfileDetails.getMaxGroupsPerSubsc();
                knLogger.debug(methodName, "value of max group per subscriber ", maxGroupsPerCRIClient);
                Integer gourpCount = groupInfoUtil.getSubsScrGroupCount(Arrays.asList(mdn), xdmsHomePttId, persisterTxn)
                        .get(mdn);
                if (gourpCount == null) {
                    gourpCount = 0;
                }
                if (maxGroupsPerCRIClient != null) {
                    knLogger.debug(methodName, "gourpCount  - ", gourpCount, "maxGroupsPerCRIClient  - ",
                            maxGroupsPerCRIClient);
                    if (gourpCount.compareTo(Integer.valueOf(maxGroupsPerCRIClient)) > 0) {
                        throw new KnCorpBOException(KnErrorCodes.Validator.MAX_GROUPS_PER_CRI_CLIENT_EXCEED,
                                "max groups per CRI client exceed.");
                    }
                }
            }
            //Steps
            //1. Update this subscriber in contact list of all the subscribers where he belongs to
            //2. Update this subscriber in all the groups he belongs to
            //3. Update association for all the Sublists pushed to him
            //4. Update this subscriber in all the Sublists where he is a member
            //5. Update his mdn in Private ConatctList

            // get list of MDNs where he is present as contact.

            Collection<String> requestMdnList = new ArrayList<String>();
            requestMdnList.add(newMDN);
            knLogger.info(methodName, "requestMdnList - ", KnGDPRTemplate.mdnList(requestMdnList));
            Collection<String> mdnList = new ArrayList<String>();
            mdnList.add(mdn);
            //Additional Talk Group:
            Map<String, Long> subsEtagMap = groupInfoUtil.getSubsAddlEtagMap(mdnList, xdmsHomePttId, persisterTxn);
            //DG.SUBSCRPTTRADIOGROUPLISTDOC WHERE MDN IN
            Map<String, Long> newSubsEtagMap = new HashMap<>();
            newSubsEtagMap.put(newMDN, subsEtagMap.get(mdn));
            if(subsEtagMap.get(mdn) != null) {
                groupInfoUtil.deleteSubsAddlTalkGroupDoc(mdnList, xdmsHomePttId, persisterTxn);//DG.SUBSCRPTTRADIOGROUPLISTDOC
                groupInfoUtil.insertSubsAddlTGDoc(newSubsEtagMap, xdmsHomePttId, persisterTxn);
            }
            Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlTGList(mdn, xdmsHomePttId, persisterTxn);
            if(subsAddlTGList != null && !subsAddlTGList.isEmpty()){
                groupInfoUtil.deleteSubsAddlTalkGroup(mdnList, xdmsHomePttId, persisterTxn);
                subsAddlTGList.forEach(addlTGInfoDTO -> {
                    addlTGInfoDTO.setMdn(newMDN);
                });
                //etagMap = commonInfoUtil.formMdnAddlTGListDeleteNotification(mdn, etagMap);
                groupInfoUtil.insertSubsAddlTGList(subsAddlTGList, xdmsHomePttId, persisterTxn);
            }
            Collection<String> newMdnLists = new ArrayList<String>();
            newMdnLists.add(newMDN);
            //updating the etag select etag method can be called here.
            etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHomePttId, newMdnLists, persisterTxn, etagMap);
            //Update the private list name for the MDn as the Name has to be PrivateListNEWMDNNO
            int privateListId = subsProfile.getContactListId();
            if (privateListId > 0) {
                String privateListName = (KnConstants.PRIVATE_LIST_NAME).concat(newMDN);
                sublistInfoUtil.modifySublistName(privateListName, privateListId, xdmsHomePttId, persisterTxn);
            }
            //get all sublist mapped to him and remove the association and push to new mdn
            Collection<KnCorpSublistDTO> listOfDistSublistIds = contactInfoUtil.getSubscMappedSublistList(contactDTO,
                    0, xdmsHomePttId, persisterTxn);
            if (listOfDistSublistIds != null && !listOfDistSublistIds.isEmpty()) {
                Collection<Integer> updateListIds = new ArrayList<Integer>();
                for (KnCorpSublistDTO corpSubDto : listOfDistSublistIds) {
                    updateListIds.add(corpSubDto.getSublistId());
                }
                knLogger.info(methodName, "updateListIds - ", updateListIds);
                KnCorpSubscriberDTO corpSubsDto = new KnCorpSubscriberDTO();
                corpSubsDto.setMdn(mdn);
                contactInfoUtil.removeSublistMappingForSubscriber(updateListIds, corpSubsDto,
                        xdmsHomePttId, persisterTxn);
                KnIPCorpSublistSubscDistDTO distDTO = new KnIPCorpSublistSubscDistDTO();
                distDTO.setSublistIds(updateListIds);
                knLogger.info(methodName, "newMdnLists - ", KnGDPRTemplate.mdnList(newMdnLists));
                distDTO.setMdnList(newMdnLists);
                contactInfoUtil.pushSublistListToSubscriberList(distDTO, xdmsHomePttId, persisterTxn);
            }


            // get all sublist ids where this MDN is member across the corporate.
            Map<Integer, Collection<String>> sublistMemberMap = sublistInfoUtil.getSubcriberSublistMemberShipListForAllCorporate(mdnList,
                    xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "sublistMemberMap - ", sublistMemberMap);

            // Get all group ids where this MDN is member across the corporate.
            Map<Integer, Collection<KnCorpGroupMemberDTO>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupListForAllCorporate(mdnList,
                    xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "groupMemberMap - ", groupMemberMap);

            Collection<Integer> sublistList = sublistMemberMap.keySet();
            Collection<String> contactMemberList = new ArrayList<String>();
            Collection<KnCorpSubscriberDTO> contactMdnList = new ArrayList<KnCorpSubscriberDTO>();  // this variable holds all the MDNs having old MDN as contact
            if (sublistMemberMap != null && !sublistMemberMap.isEmpty()) {
                //Deleting old MDN from the sublist and adding new MDN to it.
                sublistInfoUtil.changeMDNImpactInSublist(sublistMemberMap, newMDN, corpId, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "Calling sublist List Distribution list - ", sublistMemberMap);
                //Get all the MDNs to whom these sublist are distributed
                Map<Integer, Collection<KnCorpSubscriberDTO>> contactMemberMap = contactInfoUtil.getSublistListDistributionList(
                        sublistList, xdmsHomePttId, persisterTxn);
                //notes contact mdn is coming here
                knLogger.debug(methodName, "contactMemberMap - ", contactMemberMap);
                for (int sublistId : sublistList) {
                    Collection<KnCorpSubscriberDTO> contactList = contactMemberMap.get(sublistId);
                    if (contactList != null && !contactList.isEmpty()) {
                        for (KnCorpSubscriberDTO contact : contactList) {
                            if (!mdnList.contains(contact.getMdn())) {
                                contactMemberList.add(contact.getMdn());
                                contactMdnList.add(contact);
                            }
                        }
                    }
                }
            }

            //update the owner mdn in the contact list table
            contactInfoUtil.updateOwnerMdnInContactList(contactDTO, xdmsHomePttId, persisterTxn);

            //remove the old mdn member as its directory and the resource list entries will not be found in DB
            /*  if (contactMemberList.contains(mdn)) {
                contactMemberList.remove(mdn);
                contactMemberList.add(newMDN);
            }*/

            //Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();

            LinkedList<String> removeMdnList = new LinkedList<String>();
            removeMdnList.add(mdn);

            //LinkedHashMap<String, LinkedList<Integer>> delContMemStatus = new LinkedHashMap<String, LinkedList<Integer>>();
            LinkedHashMap<Integer, LinkedList<Integer>> delGrpMemStatus = new LinkedHashMap<Integer, LinkedList<Integer>>();
            LinkedHashMap<String, LinkedList<String>> removeMdnListMap = new LinkedHashMap<String, LinkedList<String>>();
            LinkedHashMap<Integer, LinkedList<String>> removeGrpMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();

            if (contactMemberList != null && !contactMemberList.isEmpty()) {
                int extCorpId = 0;
                ///add and remove members from the corpcontactlist table
                //remove mdns
                for (String contactMdn : contactMemberList) {
                    removeMdnListMap.put(contactMdn, removeMdnList);
                }
                contactInfoUtil.deleteMembersFromCorpContactList(removeMdnListMap, xdmsHomePttId, persisterTxn);
                //add members
                Map<String, Collection<String>> subsContactMap =
                        contactInfoUtil.getSubscribersContactList(contactMemberList, xdmsHomePttId, persisterTxn);
                Map<String, Collection<KnCorpSubscriberDTO>> addedMdnListMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
                for (KnCorpSubscriberDTO subscriberDTO : contactMdnList) {
                    ArrayList<KnCorpSubscriberDTO> newMdnList = new ArrayList<KnCorpSubscriberDTO>();
                    KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                    subsc.setMdn(newMDN);
                    subsc.setName(subsProfile.getNetworkName());
                    subsc.setCorpId(corpId);
                    if (subscriberDTO.getCorpId() != corpId) {
                        Collection<KnCorpSubscriberDTO> externalContactList = new ArrayList<KnCorpSubscriberDTO>();
                        extCorpId = subscriberDTO.getCorpId();
                        externalContactList.add(new KnCorpSubscriberDTO(mdn));
                        KnMdnDetailsPersistDTO mdnDetailsPersistDTO = contactInfoUtil.getExternalConatctsInfo(extCorpId, externalContactList, xdmsHomePttId, persisterTxn);
                        Collection<KnCorpSubscriberDTO> externalLIst = mdnDetailsPersistDTO.getExternalMdnList();
                        if (externalLIst != null && !externalLIst.isEmpty()) {
                            for (KnCorpSubscriberDTO corpSubscriberDTO : externalLIst) {
                                subsc.setName(corpSubscriberDTO.getName());
                            }
                        }
                        subsc.setContact_type(KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT);
                        subsc.setClientType(subsProfile.getClientType());
                    }
                    newMdnList.add(subsc);
                    addedMdnListMap.put(subscriberDTO.getMdn(), newMdnList);
                }
                Map<String, Collection<KnCorpSubscriberDTO>> finalMissingContacts =
                        commonInfoUtil.filterMemberToBeAddedToSubscriber(subsContactMap, addedMdnListMap);
                contactInfoUtil.insertMembersIntoCorpContactList(finalMissingContacts, xdmsHomePttId, persisterTxn);
                knLogger.info(methodName, "Updating ResourceList Etags");
                //etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMemberList, xdmsHomePttId, etagMap, persisterTxn);
                //etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, finalMissingContacts, removeMdnListMap, null, null, null, null, null, null, delContMemStatus, null, null);
                knLogger.debug(methodName, "etagMap ", KnGDPRTemplate.mapKeyMdn(etagMap));

                //Commented below method call to avoid deletion of data from mcptt_perm_info table. Because we updating mcptt_perm_info with new MDN.
                //etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
                knLogger.debug(methodName, "etagMap after auth mapping modification- ", KnGDPRTemplate.mapKeyMdn(etagMap));
            }

            // get all groupIds where this MDN is member.
            Collection<Integer> groupIdLst = groupMemberMap.keySet();

            if (groupMemberMap != null && !groupMemberMap.isEmpty()) {
                // update old mdn to new mdn in  CorpGroupMemberList table.
                groupInfoUtil.updateCorpGroupMemberList(groupIdLst, mdn, newMDN, xdmsHomePttId, persisterTxn);
            }

            Map<Integer, Collection<String>> groupMemberDistMap = groupInfoUtil.getAllSubscribersGroupDistForAllCorporate(mdnList,
                    xdmsHomePttId, persisterTxn);
            LinkedList<Integer> status = new LinkedList<Integer>();
            Map<Integer, Collection<KnCorpContactDTO>> addedGroupMembers = new HashMap<Integer, Collection<KnCorpContactDTO>>();
            HashMap<Integer, String> groupNameMap = new HashMap<Integer, String>();
            Map<Integer, Collection<String>> groupDistList = new HashMap<Integer, Collection<String>>();
            if (groupIdLst != null) {
                //updating in dg.corpgroupdistinfo table
                Collection<Integer> groupDistIdLst = groupMemberDistMap.keySet();
                status.add(1);
                if (groupDistIdLst.size() > 0) {
                    Map<Integer, Map<String, Collection<String>>> memberInDistDetailsList = new HashMap<Integer, Map<String, Collection<String>>>();
                    for (int id : groupDistIdLst) {
                        Map<String, Collection<String>> memDelDistMap = new HashMap<String, Collection<String>>();
                        memDelDistMap.put(KnConstants.DELTED_MEMBERS, removeMdnList);
                        memDelDistMap.put(KnConstants.ADDED_MEMBERS, requestMdnList);
                        memberInDistDetailsList.put(id, memDelDistMap);
                    }
                    groupInfoUtil.deleteFrmCorpGroupDistInfo(memberInDistDetailsList, xdmsHomePttId, persisterTxn);
                    groupInfoUtil.insertIntoCorpGroupDistInfo(memberInDistDetailsList, xdmsHomePttId, persisterTxn);
                }
                groupDistList = groupInfoUtil.getGroupSubscriberDistList(groupIdLst, xdmsHomePttId, persisterTxn);
                ArrayList<KnCorpGroupDTO> groupNameEtagList = groupInfoUtil.getGrpsNameEtagInfo(groupIdLst, xdmsHomePttId, persisterTxn);
                //Map<Integer, Integer> groupEtagMap = new HashMap<Integer, Integer>();
                for (KnCorpGroupDTO groupData : groupNameEtagList) {
                    //  groupEtagMap.put(groupData.getGroupId(), groupData.getETag());
                    groupNameMap.put(groupData.getGroupId(), (String.valueOf(groupData.getCorpId()).concat("_").concat(groupData.getGroupDisplayName())));
                }

                /*
                    Get the group member count and group type for the groupIds and set these values into etag Map
                 */
                Map<Integer, Integer> groupMemCountMap = groupInfoUtil.getGroupMemCount(groupIdLst, xdmsHomePttId, persisterTxn);
                ArrayList<KnCorpGroupDTO> groupDetailList = groupInfoUtil.getGrpsNameEtagInfo(groupIdLst, xdmsHomePttId, persisterTxn);
                Map<Integer, KnCorpGroupDTO> groupDetailsMap = new HashMap<>(groupDetailList.size());
                for (KnCorpGroupDTO groupDTO : groupDetailList) {
                    if (null != groupMemCountMap.get(groupDTO.getGroupId())) {
                        groupDTO.setGroupMemCount(groupMemCountMap.get(groupDTO.getGroupId()));
                    }
                    groupDetailsMap.put(groupDTO.getGroupId(), groupDTO);
                }
                //etagMap = commonInfoUtil.formSubscriberNotification(groupDistList, groupEtagMap,
                //com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);

            }
            if (groupMemberMap != null) {
                int extCorpId = 0;
                for (Map.Entry<Integer, Collection<KnCorpGroupMemberDTO>> entry : groupMemberMap.entrySet()) {
                    int grpId = entry.getKey();
                    removeGrpMdnListMap.put(grpId, removeMdnList);
                    delGrpMemStatus.put(grpId, status);
                    ArrayList<KnCorpContactDTO> grpMemList = new ArrayList<KnCorpContactDTO>();
                    Collection<KnCorpGroupMemberDTO> members = entry.getValue();
                    KnCorpContactDTO grpMemDTO = new KnCorpContactDTO();
                    grpMemDTO.setMdn(newMDN);
                    grpMemDTO.setName(subsProfile.getNetworkName());
                    grpMemDTO.setCorpId(corpId);
                    for (KnCorpGroupMemberDTO groupMemberDTO : members) {
                        if (groupMemberDTO.getCorpId() != corpId) {
                            extCorpId = groupMemberDTO.getCorpId();
                            Collection<KnCorpSubscriberDTO> externalContactList = new ArrayList<KnCorpSubscriberDTO>();
                            externalContactList.add(new KnCorpSubscriberDTO(mdn));
                            KnMdnDetailsPersistDTO mdnDetailsPersistDTO = contactInfoUtil.getExternalConatctsInfo(extCorpId, externalContactList, xdmsHomePttId, persisterTxn);
                            Collection<KnCorpSubscriberDTO> externalLIst = mdnDetailsPersistDTO.getExternalMdnList();
                            if (externalLIst != null && !externalLIst.isEmpty()) {
                                for (KnCorpSubscriberDTO corpSubscriberDTO : externalLIst) {
                                    grpMemDTO.setName(corpSubscriberDTO.getName());
                                }
                            }
                        }
                        grpMemDTO.setSupervisory(groupMemberDTO.getSupervisory());
                        grpMemDTO.setLocWatcher(groupMemberDTO.getLocWatcher());
                    }
                    grpMemList.add(grpMemDTO);
                    addedGroupMembers.put(grpId, grpMemList);
                    //we need both added and removed
                    //etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, null, null, addedGroupMembers, removeGrpMdnListMap, null, null, groupDistList, null, delGrpMemStatus, null);
                }
            }
            knLogger.info(methodName, "Updating owner MDN");
            //update the owner mdn in the contact list table
            contactInfoUtil.updateOwnerMdnInContactList(contactDTO, xdmsHomePttId, persisterTxn);

            /*if ((contactMemberList != null && !contactMemberList.isEmpty()) || (groupIdLst != null && !groupIdLst.isEmpty())) {
                knLogger.info(methodName, "Updating Directory Etags");
                Set<String> memberSet = new HashSet<String>();
                memberSet.addAll(contactMemberList);
                etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(memberSet, groupIdLst, etagMap, xdmsHomePttId, persisterTxn);
            }*/
            knLogger.info(methodName, "Contact MDNs for subscriber - ", contactMemberList);
            contactMemberList.add(mdn);
            contactMemberList.add(newMDN);
            contactInfoUtil.updateSubscribersContactCount(contactMemberList, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
            //long currentCorpProfileEtag = contactInfoUtil.updateCorporateEtag(corpId, xdmsHomePttId, persisterTxn);
            //respDTO.setEtag(String.valueOf(currentCorpProfileEtag));
            //knLogger.debug(methodName, "etagMap - ", etagMap);
            //HashMap<String, KnOPDirChgDTO> liEtagMap = new HashMap<>(etagMap);
            //knLogger.debug(methodName, "liEtagMap - ", liEtagMap);
            etagMap.remove(newMDN);
            respDTO.setChangeLogMap(etagMap);
            //update the corp id for the other corporates where he is a external subscriber
            // contactInfoUtil.updateSusbcribersCorpIdInImpactedTables(String.valueOf(corpId), newMDN, xdmsHomePttId, persisterTxn);
            //contactInfoUtil.updateSusbcribersCorpIdInImpactedTables(null, mdn, xdmsHomePttId, persisterTxn);
            Map<Integer, KnCorpSubscriberDTO> corpIdExtContactNameMap = contactInfoUtil.getCorpIdContactTypeWhereIsExternalContact(mdn, xdmsHomePttId, persisterTxn);
            if (corpIdExtContactNameMap != null && !corpIdExtContactNameMap.isEmpty()) {
                Collection<Integer> corpIdList = corpIdExtContactNameMap.keySet();
                /*if (corpIdList != null && !corpIdList.isEmpty()) {
                    // update the corporate etag.
                    knLogger.debug(methodName, "corpIdList - ", corpIdList);
                    contactInfoUtil.updateCorporateEtagForIdList(corpIdList, xdmsHomePttId, persisterTxn);
                }*/
                //Delete oldMDN and insert new MDN in External contact table.
                contactInfoUtil.deleteSusbcribersFromExtContactTables(mdn, xdmsHomePttId, persisterTxn);
                contactInfoUtil.addExternalContactsInAllCorp(corpIdExtContactNameMap, newMDN, corpId, xdmsHomePttId, persisterTxn);
            }
            /**
             * Updating the CAMPEDGROUPINFO table.
             * To change the MDN here first delete the existing old mdn entries and insert with new mdn for each groupId.
             */

            KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
            ipTalkGroupDTO.setMdn(mdn);
            KnTalkGrpScanMode grpScanMode = groupInfoUtil.getSubsTalkGrpScanMode(mdn, xdmsHomePttId, persisterTxn);
            if (grpScanMode != null) {
                List<KnCorpTGSPersistDTO> corpTGSPersistDTOs = groupInfoUtil.getSubsCampedGrp(ipTalkGroupDTO, xdmsHomePttId, persisterTxn);
                List<KnCorpTalkGrpInfoDTO> newGrpList = new ArrayList<KnCorpTalkGrpInfoDTO>();

                for (KnCorpTGSPersistDTO corpTGSPersistDTO : corpTGSPersistDTOs) {
                    KnCorpTalkGrpInfoDTO corpTalkGrpInfoDTO = new KnCorpTalkGrpInfoDTO();
                    corpTalkGrpInfoDTO.setGroupId(corpTGSPersistDTO.getGroupId());
                    corpTalkGrpInfoDTO.setGroupName(corpTGSPersistDTO.getGroupName());
                    corpTalkGrpInfoDTO.setPriority(corpTGSPersistDTO.getPriority());
                    newGrpList.add(corpTalkGrpInfoDTO);
                }

                List<KnCorpTGSPersistDTO> corpChannelPersistDTOs = groupInfoUtil.getSubsChannelGrp(ipTalkGroupDTO, xdmsHomePttId, persisterTxn);
                List<KnCorpTalkGrpInfoDTO> newChannelGrpList = new ArrayList<KnCorpTalkGrpInfoDTO>();

                for (KnCorpTGSPersistDTO corpTGSPersistDTO : corpChannelPersistDTOs) {
                    KnCorpTalkGrpInfoDTO corpTalkGrpInfoDTO = new KnCorpTalkGrpInfoDTO();
                    corpTalkGrpInfoDTO.setGroupId(corpTGSPersistDTO.getGroupId());
                    corpTalkGrpInfoDTO.setGroupName(corpTGSPersistDTO.getGroupName());
                    corpTalkGrpInfoDTO.setChannel(corpTGSPersistDTO.getChannel());
                    newChannelGrpList.add(corpTalkGrpInfoDTO);
                }

                groupInfoUtil.createSubsChannelGrps(newMDN, newChannelGrpList, xdmsHomePttId, persisterTxn);
                groupInfoUtil.createSubsCampedGrps(newMDN, newGrpList, com.kodiak.common.resources.KnConstants.CAMPED_BY_CORPORATE_ADMIN,
                        xdmsHomePttId, persisterTxn);
                groupInfoUtil.createSubsTalkGrpScanMode(newMDN, grpScanMode.getMode(), xdmsHomePttId, persisterTxn);
                // Delete Subscriber case no need to send the SEH notification,
                // hence return type of the cleanUpSubsCampedGrps is irrelevant
                groupInfoUtil.cleanUpSubsCampedGrps(mdn, xdmsHomePttId, persisterTxn);
                etagMap = commonInfoUtil.formMdnTGSCDeleteNotification(mdn, etagMap, xdmsHomePttId, persisterTxn);
            }//This is self mdn so no issue.,
            // Authorization related Changes: Ambient & Discrete Listening
            Map<String, KnMcpttPermissionDTO> mcpttAuthPermissionMap = corpSubsProvInfoUtil.getAuthUserPermissions(mdn,
                    xdmsHomePttId, persisterTxn);
            Map<String, KnMcpttPermissionDTO> mcpttTargPermissionMap = corpSubsProvInfoUtil.getTargUserPermissions(mdn,
                    xdmsHomePttId, persisterTxn);
            Map<String, Long> authEtagMap = corpSubsProvInfoUtil.seleteFromAuthDoc(mdnList, xdmsHomePttId, persisterTxn);
            if(!authEtagMap.isEmpty()){
                Map<String, Long> newAuthEtagMap = new HashMap<>();
                newAuthEtagMap.put(newMDN, authEtagMap.get(mdn));
                corpSubsProvInfoUtil.deleteFromAuthDoc(mdnList, xdmsHomePttId, persisterTxn);
                corpSubsProvInfoUtil.insertIntoAuthDoc(null, newAuthEtagMap, xdmsHomePttId, persisterTxn);
                etagMap = commonInfoUtil.formMdnAuthDeleteNotification(mdn, etagMap);
            }
            boolean mcpttUpdate = false;
            if (mcpttAuthPermissionMap != null && !mcpttAuthPermissionMap.isEmpty()) {
                mcpttUpdate = true;
                Collection<KnMcpttPermissionDTO> mcpttPermissionDTOS = mcpttAuthPermissionMap.values();
                mcpttPermissionDTOS.forEach(mcpttDto -> {
                    mcpttDto.setAuthMdn(newMDN);
                });
                corpSubsProvInfoUtil.deleteFromMcpttPermInfoAuthMdn(mdn, xdmsHomePttId, persisterTxn);
                corpSubsProvInfoUtil.insertIntoMcpttPermInfo(mcpttPermissionDTOS, xdmsHomePttId, persisterTxn);
                etagMap = corpSubsProvInfoUtil.updateAuthorizationImpactedTables(xdmsHomePttId, newMDN, null,
                        corpId, persisterTxn, etagMap);
            }//fine self mdn only
            if (mcpttTargPermissionMap != null && !mcpttTargPermissionMap.isEmpty()) {
                mcpttUpdate = true;
                Collection<KnMcpttPermissionDTO> mcpttPermissionDTOS = mcpttTargPermissionMap.values();
                mcpttPermissionDTOS.forEach(mcpttDto -> {
                    mcpttDto.setTargetMdn(newMDN);
                });
                corpSubsProvInfoUtil.deleteFromMcpttPermInfoTargetMdn(mdn, xdmsHomePttId, persisterTxn);
                corpSubsProvInfoUtil.insertIntoMcpttPermInfo(mcpttPermissionDTOS, xdmsHomePttId, persisterTxn);
                //etagMap = corpSubsProvInfoUtil.updateAuthorizationImpactedTables(xdmsHomePttId, null, newMDN,
                //      corpId, persisterTxn, etagMap);
            }//no buddy
            if (mcpttUpdate) {
                KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
                ipSubscriberInfoDTO.setMdn(mdn);
                ipSubscriberInfoDTO.setServiceAuthStatus(DISABLED);
                ipSubscriberInfoDTO.setServiceAuthStatusAU(DEFAULT_AU);
                ipSubscriberInfoDTO.setCorpId(String.valueOf(corpId));
                corpSubsProvInfoUtil.updateSubscriberServiceAuthStatus(ipSubscriberInfoDTO, xdmsHomePttId, persisterTxn);
            }

            //Emergency related Changes:
            //Emergency to be set POCSUBSCR_ADDLINFO,POCSUBSCRINFO
            KnSubsEmergencyAttributes oldMdnsubsEmergencyAttributes = corpSubsProvInfoUtil
                    .getEmergSubsAttributes(mdn, corpId, xdmsHomePttId, false, persisterTxn);

            KnSubsEmergencyAttributes updateSubsEmergencyAttributes = null;
            if (null != oldMdnsubsEmergencyAttributes.getEmergInitPermission() &&
                    oldMdnsubsEmergencyAttributes.getEmergInitPermission() != com.kodiak.common.resources.KnConstants.DISABLED) {
                updateSubsEmergencyAttributes = new KnSubsEmergencyAttributes();
                updateSubsEmergencyAttributes.setMdn(newMDN);
                updateSubsEmergencyAttributes.setEmergDestTypeIntf(oldMdnsubsEmergencyAttributes.getEmergDestTypeIntf());
                updateSubsEmergencyAttributes.setEmergCallType(oldMdnsubsEmergencyAttributes.getEmergCallType());
                updateSubsEmergencyAttributes.setEmergCnclPermission(oldMdnsubsEmergencyAttributes.getEmergCnclPermission());
                if (null != oldMdnsubsEmergencyAttributes.getEmergLmrBehaviour()) {
                    updateSubsEmergencyAttributes.setEmergLmrBehaviour(oldMdnsubsEmergencyAttributes.getEmergLmrBehaviour());
                }
                if (null != oldMdnsubsEmergencyAttributes.getEmergInitPermission()
                        && oldMdnsubsEmergencyAttributes.getEmergInitPermission() != EMERGENCY_INIT_NOT_MODIFIED) {
                    updateSubsEmergencyAttributes.setEmergInitPermission(oldMdnsubsEmergencyAttributes.getEmergInitPermission());
                }
                if (null != oldMdnsubsEmergencyAttributes.getEmergOriginBitSet()) {
                    updateSubsEmergencyAttributes.setEmergOriginBitSet(oldMdnsubsEmergencyAttributes.getEmergOriginBitSet());
                }
                if (null != oldMdnsubsEmergencyAttributes.getEmergTermBitSet()) {
                    updateSubsEmergencyAttributes.setEmergTermBitSet(oldMdnsubsEmergencyAttributes.getEmergTermBitSet());
                }
            }

            if (null != updateSubsEmergencyAttributes) {
                corpSubsProvInfoUtil.updateToEmergSubsDestInfo(updateSubsEmergencyAttributes, xdmsHomePttId, persisterTxn);
            }

            Collection<KnSubsDestEmergencyAttributes> mdnEmergAttributes =
                    corpSubsProvInfoUtil.getEmergAttributes(mdn, xdmsHomePttId, persisterTxn);
            Collection<KnSubsDestEmergencyAttributes> mdnEmergAttributesForDestination =
                    corpSubsProvInfoUtil.getEmergDestAttributesForDestination(mdn, xdmsHomePttId, false, persisterTxn);
            Map<String, Long> emergEtagMap = corpSubsProvInfoUtil.seleteFromEmergDoc(mdnList, xdmsHomePttId, persisterTxn);
            if(!emergEtagMap.isEmpty()){
                Map<String, Long> newEmergEtagMap = new HashMap<>();
                newEmergEtagMap.put(newMDN, emergEtagMap.get(mdn));
                corpSubsProvInfoUtil.deleteFromEmergDoc(mdnList, xdmsHomePttId, persisterTxn);
                corpSubsProvInfoUtil.insertIntoEmergDoc(null, newEmergEtagMap, xdmsHomePttId, persisterTxn);
                etagMap = commonInfoUtil.formMdnEmergDeleteNotification(mdn, etagMap);
            }
            if(mdnEmergAttributes != null && !mdnEmergAttributes.isEmpty()){
                mdnEmergAttributes.forEach(emergDestDto -> {
                    emergDestDto.setMdn(newMDN);
                });
                corpSubsProvInfoUtil.deleteFromEmergSubsDestInfo(mdn, xdmsHomePttId, persisterTxn);
                corpSubsProvInfoUtil.insertIntoEmergSubsAttributes(mdnEmergAttributes, xdmsHomePttId, persisterTxn);
                etagMap = corpSubsProvInfoUtil.updateEmergencyImpactedTables(xdmsHomePttId, newMDN, corpId, persisterTxn, etagMap);
            }
            if (mdnEmergAttributesForDestination != null && !mdnEmergAttributesForDestination.isEmpty()) {
                mdnEmergAttributesForDestination.forEach(emergDestDto -> {
                    emergDestDto.setEmergDest(newMDN);
                });
                corpSubsProvInfoUtil.updateEmergSubsAttributes(mdnEmergAttributesForDestination, xdmsHomePttId, persisterTxn);
                /*for (KnSubsDestEmergencyAttributes emergencyDestination : mdnEmergAttributesForDestination) {
                    etagMap = corpSubsProvInfoUtil.updateEmergencyImpactedTables(xdmsHomePttId, emergencyDestination.getMdn(),
                            corpId, persisterTxn, etagMap);
                }*/
            }
            // update GROUP_OWNER column of DG.CORPGROUPINFO to null where ever this mdn is as group owner
            //.. introduced in dynamic contacts and group feature
            this.groupInfoUtil.updateGroupOwner(mdn, newMDN, corpId, xdmsHomePttId, persisterTxn);

            knLogger.debug(methodName, "LI Data  - ");
            // LinkedList<KnLIEventDTO> liEventList = KnCorpCommonInfoUtil.populateLIData(liEtagMap
            //, xdmsHomePttId, groupNameMap, null, null,persisterTxn);
            //respDTO.setLiEventList(liEventList);


        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while changeMdn (corp association) - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while changeMdn (corp association) - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }
}
