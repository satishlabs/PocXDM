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
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.lieventhandler.dto.KnLIEventDTO;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpInfoResDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


import static com.kodiak.xdms.server.common.resources.KnConstants.LIBRARY_NAME_CORP_MGMT;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;

public class KnCorpDocCleanUpProcessor {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpDocCleanUpProcessor.class);
    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnCorpContactInfoUtil contactInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    private KnAASFramework authorizationFwk;
    KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
    private KnValidatorFramework validatorFW;
    private KnGeneralUtil generalUtil;
    private KnGenInfoUtil genInfoUtil;
    private KnCorpActivationInfoUtil activationInfoUtil;
    private KnCorpUserProfileUtil corpUserProfileUtil;
    private KnCorpGroupProfileUtil groupProfilUtil;
    private KnCorpUserProfileUtil userProfileUtil;
    private KnCorpOSMInfoUtil corpOSMInfoUtil;

    public KnCorpDocCleanUpProcessor() {
        commonInfoUtil = new KnCorpCommonInfoUtil();
        contactInfoUtil = new KnCorpContactInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        generalUtil = new KnGeneralUtil();
        authorizationFwk = KnAASFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
        validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        genInfoUtil = KnGenInfoUtil.getInstance();
        activationInfoUtil = new KnCorpActivationInfoUtil();
        corpUserProfileUtil = new KnCorpUserProfileUtil();
        groupProfilUtil = new KnCorpGroupProfileUtil();
        userProfileUtil = new KnCorpUserProfileUtil();
        corpOSMInfoUtil = new KnCorpOSMInfoUtil();
    }

    public KnCorpInfoResDTO deleteSubscleanUpCorporateProfile(KnIPCorpContactDTO contactDTO, KnSubsProfileDTO subsProfile,String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        {
            String methodName = "deleteSubscleanUpCorporateProfile(KnIPCorpContactDTO, KnPersisterTxn)";
            KnCorpInOutParamDTO corpInOutParamDTO = new KnCorpInOutParamDTO();
            KnCorpInfoResDTO respDTO = new KnCorpInfoResDTO();
            Set<String> groupFleetMembers = new HashSet<String>();
            try {
                String mdn = contactDTO.getMdn().trim();
                knLogger.debug(methodName, " Entry :", contactDTO);
                knLogger.debug(" xdmsHomePttId - ", xdmsHomePttId);
                if (contactDTO.getMcpttCompliance() == 1) {
                    KnCorpProfileDTO corpProfileDetails = commonInfoUtil
                            .getProfileDetails(String.valueOf(subsProfile.getCorpId()), CORP_PROFILE, false, persisterTxn);
                    Integer maxGroupsPerCRIClient = corpProfileDetails.getMaxGroupsPerSubsc();
                    knLogger.debug(methodName, "value of max group per subscriber ", maxGroupsPerCRIClient);
                    Integer gourpCount = groupInfoUtil.getSubsScrGroupCount(Arrays.asList(mdn), xdmsHomePttId, persisterTxn)
                            .get(mdn); // mm
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
                int corpId = subsProfile.getCorpId();
                KnCorpProfileDTO corpProfile = new KnCorpProfileDTO();
                respDTO.setCorpId(corpId);
                //Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
                HashMap<Integer, String> groupNameMap = new HashMap<Integer, String>();

                boolean upmCall = contactDTO.isUpmCall();
                knLogger.debug(methodName, " upmCall :", upmCall);


                LinkedList<String> mdnList = new LinkedList<String>();
                mdnList.add(mdn);
                //need this for LI
                /*Map<String, Collection<String>> mdnContactMap = contactInfoUtil.getSubscribersContactList(mdnList, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, " mdnContactMap :", mdnContactMap);//using for LI event so keeping it*/

                groupInfoUtil.deleteSubsAddlTalkGroupDoc(mdnList, xdmsHomePttId, persisterTxn);// Etag removal
                // Addl Talk Group Doc
                //etagMap = commonInfoUtil.formMdnAddlTGListDeleteNotification(mdn, etagMap);// Removing since it's a self notification
                Collection<KnCorpAddlTGInfoDTO> subsMdnAddlTGList = groupInfoUtil.getSubsAddlTGList(mdn, xdmsHomePttId, persisterTxn);//mdn-groupId-ZoneId-ChannelId
                knLogger.debug(methodName, "subsAddlTGList - ", subsMdnAddlTGList);
                if (subsMdnAddlTGList != null && !subsMdnAddlTGList.isEmpty()) {
                    groupInfoUtil.deleteSubsAddlTalkGroup(mdnList, xdmsHomePttId, persisterTxn);
                    //knLogger.debug(methodName, "etagMap AddlTG - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                    /*Map<Integer, Integer> groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsMdnAddlTGList, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
                    if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                        etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, null,
                                subsMdnAddlTGList, groupCorpIdMap);
                        knLogger.debug(methodName, "etagMap AddlTG -diff - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                    }*/ //removing this since it's a self notification
                }
                /////EXTERNAL SUBSCRIEBR USE CASE FOR PUBLIC////
                if (corpId <= 0) {
                    //Get corpId where MDN exist as ext contact
                    Map<Integer, String> corpIdExtContactNameMap = contactInfoUtil.getCorpIdListWhereIsExternalContact(mdn, xdmsHomePttId, persisterTxn);//got from other corp
                    knLogger.debug(methodName, "corpIdExtContactNameMap - ", corpIdExtContactNameMap);
                    if (corpIdExtContactNameMap != null && !corpIdExtContactNameMap.isEmpty()) {
                        knLogger.debug(methodName, "Subscriber passed ish of public subscription type MDN - ", KnGDPRTemplate.mdn(mdn));
                        deleteExtContactDataFrmOtherCorp(contactDTO, persisterTxn, corpIdExtContactNameMap, corpInOutParamDTO,xdmsHomePttId);
                        //knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                        //respDTO.setChangeLogMap(etagMap);
                    }
                    return respDTO;
                }
                contactDTO.setCorpId(corpId);
                //use the xdms home pttServerId from Corp profile

                int privateContactListId = subsProfile.getContactListId();
                knLogger.info(methodName, "xdmsHomePttId - ", xdmsHomePttId);
                //determine the no of members for the corporate
                /*int count = contactInfoUtil.getCorpSubscriberCount(corpId, xdmsHomePttId, persisterTxn);
                corpProfile = commonInfoUtil.getProfileDetails("" + corpId, "CorpAdmin", true, persisterTxn);
                int corpProfileCleanUp = corpProfile.getMaxPredefinedTmpltCnt();
                int deviceCount = genInfoUtil.getDeviceCountForCorpId(corpId, persisterTxn);
                int deviceCountMdn = genInfoUtil.getDeviceCountByMdnAndCorpId(mdn, corpId, persisterTxn);
                knLogger.info(methodName, "corpProfileCleanUp flag- ", corpProfileCleanUp, " deviceCount - ", deviceCount, " deviceCountMdn - ", deviceCountMdn, " subscriber count - ", count);
                *//*if (count <= 1 && corpProfileCleanUp != 1 &&
                        (deviceCount == 0 || (deviceCount == 1 && deviceCountMdn == 1))) {
                    // since cleanUpCorporateProfile() methos is cleaning its corp data.
                    // Before cleaning corp data, delete external data from other corp.
                    deleteExtContactDataFrmOtherCorp(contactDTO, persisterTxn, null, corpInOutParamDTO,xdmsHomePttId);
                    deleteMcpttDoc(mdn, xdmsHomePttId, persisterTxn,true);
                    respDTO = cleanUpCorporateProfile(contactDTO, corpProfile, corpInOutParamDTO,xdmsHomePttId, persisterTxn);
                    //respDTO.setTgsModeChgMap(corpInOutParamDTO.getTgsModeChgMap());
                    //return respDTO;
                    return respDTO;
                }*/
                //1. Delete this subscriber from contact list of all the subscribers to whom he belongs to
                //2. Delete this subscriber from all groups he belongs to
                //3. Delete association from all the Sublists pushed to him
                //4. Delete this subscriber from all the Sublists where he is a member
                //5. Delete his Private ConatctList Sublist

                // get list of MDNs where he is present as contact.
                // Collection<String> contactMDNs = contactInfoUtil.getContactMDNs(mdn, xdmsHomePttId, persisterTxn);
                //Get list of sublist ids distributed to this subscriber.
                // sending corpListId = 0 , for retrieving the private list id also.
                Collection<KnCorpSublistDTO> listOfDistSublistIds = new ArrayList();
                if (upmCall) {
                    String userProfileId = subsProfile.getUserProfileId();
                    KnIPCorpContactDTO contactObj = new KnIPCorpContactDTO();
                    ArrayList<String> ids = new ArrayList<String>();
                    ids.add(subsProfile.getUserProfileId());
                    Map<String, Integer> onwerInfo = corpUserProfileUtil.getUserProfileOwnerinfo(ids, xdmsHomePttId, persisterTxn);
                    if (onwerInfo != null && !onwerInfo.isEmpty() && onwerInfo.get(userProfileId) != subsProfile.getCorpId()) {
                        //upm sharing enabled and is shared
                        contactObj.setMdn(mdn);
                        contactObj.setCorpId(onwerInfo.get(userProfileId));
                        listOfDistSublistIds = contactInfoUtil.getSubscMappedSublistList(contactObj,
                                0, xdmsHomePttId, persisterTxn);
                    } else {
                        //upm is not shared or upm is shared and is the owner
                        listOfDistSublistIds = contactInfoUtil.getSubscMappedSublistList(contactDTO,
                                0, xdmsHomePttId, persisterTxn);
                    }

                } else {
                    listOfDistSublistIds = contactInfoUtil.getSubscMappedSublistList(contactDTO,
                            0, xdmsHomePttId, persisterTxn);
                }
                if (listOfDistSublistIds != null && !listOfDistSublistIds.isEmpty()) { //This is fine
                    Collection<Integer> removeListIds = new ArrayList<Integer>();
                    for (KnCorpSublistDTO corpSubDto : listOfDistSublistIds) {
                        removeListIds.add(corpSubDto.getSublistId());
                    }
                    KnCorpSubscriberDTO corpSubsDto = new KnCorpSubscriberDTO();
                    corpSubsDto.setMdn(mdn);
                    contactInfoUtil.removeSublistMappingForSubscriber(removeListIds, corpSubsDto,
                            xdmsHomePttId, persisterTxn);
                }
                /*Map<String, Collection<String>> dispContactList = null;//dispatch use case
                if (subsProfile.getClientType() == DISPATCH_CLIENT.value()
                        || subsProfile.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()) {
                    dispContactList = contactInfoUtil.getSubscribersContactList(mdnList, xdmsHomePttId, persisterTxn); //where mdn=?
                }*/
                contactInfoUtil.deleteSubscribersContactList(mdn, xdmsHomePttId, persisterTxn);//where mdn=?
                contactInfoUtil.updateSubscribersContactCount(mdnList, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
                // get all sublist ids where this MDN is member in all corporation.
                //Map<Integer, Collection<String>> sublistMemberMap = sublistInfoUtil.
                       // getSubcriberSublistMemberShipListForAllCorporate(mdnList, xdmsHomePttId, persisterTxn); //from listmem where mdn=?

                /*Collection<Integer> sublistList = sublistMemberMap.keySet();
                //Collection<String> contactMDNs = new ArrayList<String>();
                Map<Integer, Integer> subListForProfileUpdate = new HashMap<>();
                if (sublistMemberMap != null && !sublistMemberMap.isEmpty()) {
                    Collection<Integer> sublistIdList = sublistMemberMap.keySet();
                    sublistInfoUtil.deleteMembersFromAllSublist(sublistMemberMap, xdmsHomePttId, persisterTxn);
                    sublistInfoUtil.fetchAndUpdateSublistEtag(sublistIdList,
                            xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "Calling sublist List Distribution list - ", sublistMemberMap);
                    Map<Integer, Collection<KnCorpSubscriberDTO>> contactMemberMap = contactInfoUtil.getSublistListDistributionList(
                            sublistList, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "contactMemberMap - ", contactMemberMap);
                    for (int sublistId : sublistList) {
                        Collection<KnCorpSubscriberDTO> contactList = contactMemberMap.get(sublistId);
                        if (contactList != null && !contactList.isEmpty()) {
                            for (KnCorpSubscriberDTO contact : contactList) {
                                if (!mdnList.contains(contact.getMdn())) {
                                    //contactMDNs.add(contact.getMdn());
                                }
                            }
                        }
                    }
                    //knLogger.info(methodName, "ContactList - ", KnGDPRTemplate.mdnList(contactMDNs)); //we dont need this data
                }*/

                // delete the privatecontactlist id
                if (privateContactListId > 0) {
                    sublistInfoUtil.deleteSublist(privateContactListId, corpId, xdmsHomePttId, persisterTxn);
                }

                LinkedHashMap<String, LinkedList<String>> removeMdnListMap = new LinkedHashMap<String, LinkedList<String>>();
                LinkedHashMap<String, LinkedList<Integer>> delContactStatus = null;
                /*if (!contactMDNs.isEmpty()) {
                    //deleting members from the contact list table
                    for (String subscMdn : contactMDNs) {
                        removeMdnListMap.put(subscMdn, mdnList);
                    }
                    delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(removeMdnListMap, xdmsHomePttId, persisterTxn);
                    knLogger.info(methodName, "Updating ResourceList Etags");
                    etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMDNs, xdmsHomePttId, etagMap, persisterTxn);
                }*/
                knLogger.debug(methodName, "subsProfile.getClientType() - ", subsProfile.getClientType());
                LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus = new LinkedHashMap<Integer, LinkedList<Integer>>();
                LinkedHashMap<Integer, LinkedList<String>> removeGrpMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();
                Map<Integer, Collection<String>> groupDistributionList = new HashMap<Integer, Collection<String>>();

                Map<Integer, String> corpIdExtContactNameMap = contactInfoUtil.getCorpIdListWhereIsExternalContact(mdn, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "corpIdExtContactNameMap - ", corpIdExtContactNameMap);
                if (corpIdExtContactNameMap != null && !corpIdExtContactNameMap.isEmpty()) {
                    Collection<Integer> corpIdList = corpIdExtContactNameMap.keySet();
                    if (corpIdList != null && !corpIdList.isEmpty()) {
                        // update the corporate etag.
                        knLogger.debug(methodName, "corpIdList - ", corpIdList);
                        contactInfoUtil.updateCorporateEtagForIdList(corpIdList, xdmsHomePttId, persisterTxn);
                    }//updating other corp etag is allowing in the sync path.. nn
                }

                //get all group ids where this mdn is member in all corporation.
                Map<Integer, Collection<KnCorpGroupMemberDTO>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupListForAllCorporate(mdnList,
                        xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "groupMemberMap :", groupMemberMap);
                Map<Integer, String> dispGrpNameMap = groupInfoUtil.getSupervisorGroupsAndName(mdn, KnConstants.GROUP_DISPATCHER, xdmsHomePttId, persisterTxn);
                //jusbin - here we are slecting the supervisor groups and updating etag then sharing the names here.
                //List<Integer> dispatcherGrpList = new ArrayList<>(dispGrpNameMap.keySet());

          /*  //check if any other dispatcher exists for the group.
            //get the list of dispatchers leaving this mdn if no more exists then add to the deleted group list
            Set<Integer> nonDelDispGrp =  groupInfoUtil.getGrpDispMemList(dispatcherGrpList, mdn, xdmsHomePttId, persisterTxn);
            // nonDelDispGrp should contain the group id against the list of otherDispatchers
            dispatcherGrpList.removeAll(nonDelDispGrp);*/

                Collection<String> disabledOdlMember = new HashSet<>();
                Map<Integer, Integer> groupMapListForProfileUpdate = new HashMap<>();
                if (!groupMemberMap.keySet().isEmpty()) {
                    Collection<Integer> allGroupIdList = groupMemberMap.keySet();
                    //Variable to hold all group ids other than dispatch group.
                    List<Integer> otherGrpIdList = new ArrayList<>(allGroupIdList);
                    // otherGrpIdList.removeAll(dispatcherGrpList);
                    //delete members from the corpgroupmemberlist table

                    Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlDetails(allGroupIdList, xdmsHomePttId, persisterTxn);
                    Map<Integer, Collection<KnCorpAddlTGInfoDTO>> existingAddlTGList = new HashMap<>();
                    Collection<KnCorpAddlTGInfoDTO> delAddlTGList = new ArrayList<>();
                    // Additional Talk Group:
                    for (KnCorpAddlTGInfoDTO addlTg : subsAddlTGList) {
                        Collection<KnCorpAddlTGInfoDTO> existAddl = null;
                        if (existingAddlTGList.get(addlTg.getGroupId()) != null) {
                            existAddl = existingAddlTGList.get(addlTg.getGroupId());
                            existAddl.add(addlTg);
                        } else {
                            existAddl = new ArrayList<>();
                            existAddl.add(addlTg);
                            existingAddlTGList.put(addlTg.getGroupId(), existAddl);
                        }
                    }
                /*
                    Get the group member count and group type for the groupIds and set these values into etag Map
                 */
                    Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(allGroupIdList, xdmsHomePttId, persisterTxn);

                    Collection<KnCorpGroupMemberDTO> grpMemberDetails = groupInfoUtil.getCorpGroupMembersList(allGroupIdList, xdmsHomePttId, persisterTxn);
                    for (KnCorpGroupMemberDTO grpMem : grpMemberDetails) {
                        disabledOdlMember.add(grpMem.getMdn());
                    }

                    for (int grpId : otherGrpIdList) {
                        removeGrpMdnListMap.put(grpId, mdnList);
                        groupMapListForProfileUpdate.put(grpId, KnConstants.CB_MAPPING.ETAG_CHANGE.value());
                    }

                    Collection<Integer> groupInfoUpdate = new ArrayList<>();
                    if (subsProfile.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value() || subsProfile.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
                        Map<Integer, Integer> sgMDNCountDetailsMap = groupInfoUtil.getSGCorpGroupMembersCount(allGroupIdList, xdmsHomePttId, persisterTxn);
                        if (sgMDNCountDetailsMap != null) {
                            sgMDNCountDetailsMap.forEach((corpGroupId, SGMemberCount) -> {
                                if (SGMemberCount == 1) {
                                    groupInfoUpdate.add(corpGroupId);
                                }
                            });
                        }
                        groupInfoUtil.modifyGroupLmrInteropCapable(LMR_INTEROP_NON_CAPABLE, groupInfoUpdate, xdmsHomePttId, persisterTxn);
                    }

                    //delGroupMemStatus = groupInfoUtil.deleteCorpGroupMemberList(removeGrpMdnListMap, xdmsHomePttId, persisterTxn);
                    //Update the group member count
                    //sublistInfoUtil.updateSublistsSubscribersContactCount(sublistMemberMap.keySet(), MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                            //MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);

                    //Update IS_LARGE group flag from 1 to 0 in case of Delete Subscriber procedure
                    // when group member count becomes less than or equal to maximum normal talk group count.
                    Map<Integer, Integer> currentGrpMemCount = groupInfoUtil.getGroupMemCount(allGroupIdList, xdmsHomePttId, persisterTxn);
                    Map<Integer, Integer> isLargeGrpDisable = new HashMap<>();
                    for (Map.Entry<Integer, Integer> entry : currentGrpMemCount.entrySet()) {
                        int memCount = entry.getValue();
                        if ((groupDetailsMap.get(entry.getKey()).getGroupType()) == KnConstants.STANDARD_GROUP &&
                                memCount <= corpProfile.getMaxMemPerCorpGroup() && groupDetailsMap.get(entry.getKey()).isLargeGroup()) {
                            isLargeGrpDisable.put(entry.getKey(), 0);
                        } else if ((groupDetailsMap.get(entry.getKey()).getGroupType()) == KnConstants.DISPATCH_GROUP &&
                                memCount <= corpProfile.getMaxMembersPerDispatchGroup() && groupDetailsMap.get(entry.getKey()).isLargeGroup()) {
                            isLargeGrpDisable.put(entry.getKey(), 0);
                        }
                    }
                    if (!isLargeGrpDisable.isEmpty()) {
                        groupInfoUtil.updateIsLargeGrpFlag(isLargeGrpDisable, xdmsHomePttId, persisterTxn);
                    }

                    //Filter out the actually added members and deleted members as same member can be already present in the group via sublist etc
                    //DAO calls to delete from the dg.corpgroupdistinfo table
                    Map<Integer, Map<String, Collection<String>>> groupMemDelMap = new HashMap<Integer, Map<String, Collection<String>>>();
                    for (int grpId : otherGrpIdList) {
                        Map<String, Collection<String>> deletedMemberMap = new HashMap<String, Collection<String>>();
                        deletedMemberMap.put(KnConstants.DELTED_MEMBERS, removeGrpMdnListMap.get(grpId));
                        groupMemDelMap.put(grpId, deletedMemberMap);
                    }
                    //DAO calls to delete from the dg.corpgroupdistinfo table
                    groupInfoUtil.deleteFrmCorpGroupDistInfo(groupMemDelMap, xdmsHomePttId, persisterTxn);
                    //Retrieving the current group members i.e internal members from dg.corpgroupdistinfo
                    //updating groupId etag
                    //knLogger.info(methodName, "Updating Group Etags");
                    //Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(otherGrpIdList, xdmsHomePttId, persisterTxn);
                    //Determine the deleted and the modified groups
                    Map<String, HashMap<Integer, String>> groupListStatus =
                            groupInfoUtil.getGroupListStatus(otherGrpIdList, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "groupListStatus :", groupListStatus);
                    //retrieval of members
                    groupDistributionList = groupInfoUtil.getGroupSubscriberDistList(allGroupIdList, xdmsHomePttId, persisterTxn);
                    Collection<Integer> ownerGroupIds = groupInfoUtil.getOwnerGroupIds(mdn, corpId, xdmsHomePttId, persisterTxn);
                    knLogger.info(methodName, "ownerGroupIds: ", ownerGroupIds);
                    // Delete corp groups with < 2 members
                    Set<Integer> delGroupIdList = new HashSet<>();
                    if (groupListStatus.get(KnConstants.DELETED) != null || !ownerGroupIds.isEmpty()) {
                        delGroupIdList = new HashSet<>();
                        if (groupListStatus.get(KnConstants.DELETED) != null) {
                            delGroupIdList.addAll(groupListStatus.get(KnConstants.DELETED).keySet());
                        }
                        delGroupIdList.addAll(ownerGroupIds);
                        //delGroupIdList.addAll(dispatcherGrpList);
                        if (!delGroupIdList.isEmpty()) {
                            //Addint the dispatch group fleet members
                            /*for (int dispGrpId : dispatcherGrpList) {//jusbin - cant find the usages of this groupFleetMembers
                                if (null != groupDistributionList.get(dispGrpId)) {
                                    groupFleetMembers.addAll(groupDistributionList.get(dispGrpId));
                                }
                            }*/
                            //Map<Integer, Collection<String>> deletedMembersMap = new HashMap<Integer, Collection<String>>();
                            for (Integer grpId : delGroupIdList) {
                                Collection<String> delMembersList = groupDistributionList.get(grpId);
                               // deletedMembersMap.put(grpId, delMembersList);
                                //for the contact added to update the directory
                                //contactMDNs.addAll(delMembersList);
                                removeGrpMdnListMap.remove(grpId);
                                delGroupMemStatus.remove(grpId);
                                if (existingAddlTGList.get(grpId) != null)
                                    delAddlTGList.addAll(existingAddlTGList.get(grpId));
                                groupMapListForProfileUpdate.put(grpId, KnConstants.CB_MAPPING.NEED_TO_REMOVE.value());
                            }
                            if (groupListStatus.get(KnConstants.DELETED) != null) {
                                groupNameMap.putAll(groupListStatus.get(KnConstants.DELETED));
                            }
                            groupNameMap.putAll(dispGrpNameMap);
                           // etagMap = commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                                    //com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                        }
                    }

                    // Additional Talk Group:
                    if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {//Removing not and just keeping the deletion
                        Collection<String> mdnForAddlTg = subsAddlTGList.stream().map(KnCorpAddlTGInfoDTO::getMdn).distinct().collect(Collectors.toList());
                        groupInfoUtil.deleteSubsAddlTGList(delAddlTGList, xdmsHomePttId, persisterTxn);
                        subsAddlTGList.removeAll(delAddlTGList);
                        subsAddlTGList.forEach(subsAddl -> {
                            if (groupDetailsMap.get(subsAddl.getGroupId()) != null)
                                subsAddl.setGroupMemCount(groupDetailsMap.get(subsAddl.getGroupId()).getGroupMemCount());
                        });
                        /*etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHomePttId, mdnForAddlTg, persisterTxn, etagMap, upmCall, true);
                        knLogger.debug(methodName, "etagMap AddlTG - ", KnGDPRTemplate.mapKeyMdn(etagMap));*/
                        /*Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
                        if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                            groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsAddlTGList, xdmsHomePttId, persisterTxn);
                        }
                        knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
                        if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                            etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, subsAddlTGList, delAddlTGList, groupCorpIdMap);
                            knLogger.debug(methodName, "etagMap AddlTG -diff - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                        }*/
                    }
                    //empty group support:if last subscriber is deleted then group will not be delted
                    //groupInfoUtil.deleteAllGroups(delGroupIdList, xdmsHomePttId, corpInOutParamDTO, persisterTxn);
                    groupInfoUtil.emptyGroup(delGroupIdList, xdmsHomePttId, corpInOutParamDTO, persisterTxn);
                    //jusbin - fine we are deleting based on the member count

                    if (contactDTO.getHierarchyType() == com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE.HIERARCHY && groupListStatus.get(KnConstants.DELETED) != null) { //yy
                        List<Integer> groupIdsToBeDeleted = new CopyOnWriteArrayList<>();
                        groupIdsToBeDeleted.addAll(groupListStatus.get(KnConstants.DELETED).keySet());
                        knLogger.debug(methodName, "Empty groups ", groupIdsToBeDeleted);
                        for (Integer groupId : groupIdsToBeDeleted) {
                            KnCorpGroupDTO groupDetailsMaps = groupDetailsMap.get(groupId);
                            if (groupDetailsMaps.getMcxGrpInd() == 1) {
                                groupIdsToBeDeleted.remove(groupId);
                            }
                        }
                        if (!groupIdsToBeDeleted.isEmpty()) {
                            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
                            corpXdmDao.deleteAllGrpHierarchy(groupIdsToBeDeleted, persisterTxn);
                            groupInfoUtil.deleteAllGroups(groupIdsToBeDeleted, xdmsHomePttId, new KnCorpInOutParamDTO(), persisterTxn);
                        }
                    }


                    //etagMap = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                    //etagMap = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);

                    //jusbin - If the memberCount is>0 then we are considering the group as modified
                    /*if (groupListStatus.get(KnConstants.MODIFIED) != null) {
                        Set<Integer> modGroupIdList = groupListStatus.get(KnConstants.MODIFIED).keySet();
                        if (modGroupIdList != null && !modGroupIdList.isEmpty()) {
                            Map<Integer, Collection<String>> modifiedMembersMap = new HashMap<Integer, Collection<String>>();

                            for (Integer grpId : modGroupIdList) {
                                Collection<String> modMembersList = groupDistributionList.get(grpId);
                                modifiedMembersMap.put(grpId, modMembersList);
                                //for the contact added to update the directory
                                //contactMDNs.addAll(modMembersList);
                            }
                            etagMap = commonInfoUtil.formSubscriberNotification(modifiedMembersMap, groupEtagMap,
                                    com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
                            groupNameMap.putAll(groupListStatus.get(KnConstants.MODIFIED));
                        }
                    }*/
                }
                /*Set<Integer> sharedSublistSet = sublistMemberMap.keySet();
                if (sharedSublistSet != null && !sharedSublistSet.isEmpty()) {
                    ArrayList<Integer> sharedSublists = sublistInfoUtil.getSharedSublistFromList(sharedSublistSet, xdmsHomePttId, persisterTxn);//where policy=3.
                    ArrayList<Integer> nonSharedSublists = sublistInfoUtil.getNonSharedSublistFromList(sharedSublistSet, xdmsHomePttId, persisterTxn);//where policy=5
                    ArrayList<Integer> subList = new ArrayList<Integer>();
                    subList.addAll(sharedSublists);
                    subList.addAll(nonSharedSublists);
                    ArrayList<Integer> emptySublist = sublistInfoUtil.getEmptySublistFrmList(subList, xdmsHomePttId, persisterTxn);//count = 0
                    if (emptySublist != null && !emptySublist.isEmpty()) {
                        sublistInfoUtil.deleteAllSublist(emptySublist, xdmsHomePttId, persisterTxn);
                        if (emptySublist.contains(corpProfile.getPairedContactListId())) {// use case is unknown not commeting DG.POCCORPINFO : PAIREDCONTACTLISTID nn
                            //set the corporate profile paried linked id to  null
                            sublistInfoUtil.updateCorpPairedContListId(corpId, 0, corpProfile.getXdmsHome(), persisterTxn);
                        }
                    }
                    *//*for (Integer sId : sharedSublistSet) {
                        int val = KnConstants.CB_MAPPING.ETAG_CHANGE.value();
                        if (emptySublist != null && !emptySublist.isEmpty() && emptySublist.contains(sId)) {
                            val = KnConstants.CB_MAPPING.NEED_TO_REMOVE.value();
                        }
                        subListForProfileUpdate.put(sId, val);//jusbin - cant find the usage of this guy
                    }*//*
                }*/
                //profile notification
                //updating etag and time here not commenting nn
                List<String> profileMdnList = new ArrayList<>();
                profileMdnList.add(mdn);
                Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = corpUserProfileUtil.profileMdnEtagUpdate(
                        null
                        , String.valueOf(corpId), profileMdnList
                        , "0", xdmsHomePttId, persisterTxn);
                respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
                if (profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()) {//nn cehck
                    respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
                }

                /*if (!contactMDNs.isEmpty()) {
                    knLogger.debug(methodName, "Updating Directory Etags");
                    etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMDNs, null, etagMap, xdmsHomePttId, upmCall, persisterTxn);
                    contactInfoUtil.updateSubscribersContactCount(mdnList, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
                }*/
                //Delete the MDN from External contact table.
                if (!corpIdExtContactNameMap.isEmpty()) { //jusbin - deleting from extContact table but keeping this here
                    contactInfoUtil.deleteSusbcribersFromExtContactTables(mdn, xdmsHomePttId, persisterTxn);
                }


                /*if (dispContactList != null && dispContactList.get(mdn) != null) {//jusbin - this is only for dispatch subscriber
                    Collection<String> overAllList = new ArrayList<String>();
                    Collection<String> updateDispMemList = respDTO.getDisabledDispatchMemList();
                    if (updateDispMemList != null) {
                        overAllList.addAll(updateDispMemList);
                    }
                    overAllList.addAll(dispContactList.get(mdn));
                    respDTO.setDisabledDispatchMemList(overAllList);
                }

                if (respDTO.getDisabledDispatchMemList() != null) {
                    disabledOdlMember.addAll(respDTO.getDisabledDispatchMemList());
                }
                respDTO.setDisabledDispatchMemList(disabledOdlMember);*/ //jusbin - lets see the impact of this guy

                //To Disabled 27bit when there is no loc wathcer  is present in the UPM , below changes are applicable only for MCX group type
                /*String inputMdn = contactDTO.getMdn().trim();
                //jusbin - get userprofileid of mdn then correspoding groups of userprofile
                Set<Integer> groupIds = commonInfoUtil.getGroupIdBasedOnMdn(inputMdn, persisterTxn, xdmsHomePttId);
                LinkedHashMap<Integer, LinkedList<String>> groupDiaptacherMap = groupInfoUtil.getGroupDispatcherSubscriber(new ArrayList<>(groupIds), DISPATCHER, xdmsHomePttId, persisterTxn);
                int mdnCount = 0;
                Set<String> profileMdnSet = new HashSet<>();
                if (null != groupIds && !groupIds.isEmpty() && groupIds.size() != 0) {
                    mdnCount = commonInfoUtil.getMdnCountBasedOnUPMID(new ArrayList<>(groupIds), persisterTxn, xdmsHomePttId);
                    profileMdnSet = commonInfoUtil.getProfileMdns(groupIds, persisterTxn, xdmsHomePttId);
                }
                if (mdnCount == 1 && null != profileMdnSet && !profileMdnSet.isEmpty() && profileMdnSet.size() != 0
                        && (null == groupDiaptacherMap || groupDiaptacherMap.isEmpty() || groupDiaptacherMap.size() == 0)) {
                    respDTO.setDisabledDispatchMemList(profileMdnSet);
                }*/

                //Delete MCPTT related document.
                deleteMcpttDoc(mdn, xdmsHomePttId, persisterTxn,false);
                // Delete Subscriber case no need to send the SEH notification,
                // hence return type of the cleanUpSubsCampedGrps is irrelevant
                groupInfoUtil.cleanUpSubsCampedGrps(mdn, xdmsHomePttId, persisterTxn);
                // update GROUP_OWNER column of DG.CORPGROUPINFO to null whereever this mdn is as group owner
                //.. introduced in dynamic contacts and group feature
                //jusbin - nn if we update the value here there is a possibility of lock since same we are doing from the async also
                this.groupInfoUtil.updateGroupOwner(mdn, null, corpId, xdmsHomePttId, persisterTxn);
                activationInfoUtil.deleteActivationCodeForMDN(mdn, xdmsHomePttId, persisterTxn);
                //etagMap = commonInfoUtil.formMdnTGSCDeleteNotification(mdn, etagMap, xdmsHomePttId, persisterTxn);
                //etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, removeMdnListMap,
                        //null, null, removeGrpMdnListMap, null, null,
                        //groupDistributionList, delContactStatus, delGroupMemStatus, null);
                //knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                //etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
                //knLogger.debug(methodName, "etagMap after auth mapping modification- ", KnGDPRTemplate.mapKeyMdn(etagMap));
                //HashMap<String, KnOPDirChgDTO> liEtagMap = new HashMap<>(etagMap);
                /*knLogger.debug(methodName, "liEtagMap - ", liEtagMap);
                //etagMap.remove(mdn); // jusbin - after all the logic we are removing the mdn here - nn
                LinkedList<KnLIEventDTO> liEventList = new LinkedList<>();
                if (!upmCall) {
                    liEventList = KnCorpCommonInfoUtil.populateLIData(liEtagMap, xdmsHomePttId, groupNameMap,
                            null, null, persisterTxn);
                } else {
                    //contact
                    if (!mdnContactMap.isEmpty()) {
                        LinkedList<KnLIEventDTO> contactLiEvent = KnCorpCommonInfoUtil.upmContactPopulateLIData(mdn, mdnContactMap.get(mdn), xdmsHomePttId, persisterTxn);
                        if (!contactLiEvent.isEmpty()) {
                            liEventList.addAll(contactLiEvent);
                        }
                    }
                    //group
                    LinkedList<KnLIEventDTO> groupLiEvent = KnCorpCommonInfoUtil.upmUnassignUserProfilePopulateLIData(liEtagMap
                            , xdmsHomePttId, groupNameMap, mdn, persisterTxn);
                    if (!groupLiEvent.isEmpty()) {
                        liEventList.addAll(groupLiEvent);
                    }
                }

                knLogger.debug(methodName, " liEventList :", liEventList);
                respDTO.setLiEventList(liEventList);
                //Set<String> mdns = etagMap.keySet();
                //List<String> grpMdns = contactInfoUtil.getGroupMdns(new ArrayList<String>(mdns), corpId, xdmsHomePttId, persisterTxn);
                //for (String mdnStr : grpMdns) {
                //    etagMap.remove(mdnStr);
                //}
                //knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                //respDTO.setChangeLogMap(etagMap);
                respDTO.setTgsModeChgMap(corpInOutParamDTO.getTgsModeChgMap());
                respDTO.setMdnCorpId(corpId);*/

            } catch (KnCorpBOException e) {
                knLogger.error(methodName, "KnCorpBOException occured while deleting subscriber (corp association) - ", e);
                populate(respDTO, e);

            } catch (Exception e) {
                knLogger.error(methodName, "Exception occured while deleting subscriber (corp association) - ",
                        new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
                populate(respDTO, e);

            }
            knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
            return respDTO;
        }
    }

    public void deleteExtContactDataFrmOtherCorp(KnIPCorpContactDTO contactDTO, KnPersisterTxn
            persisterTxn, Map<Integer, String> corpIdExtContactNameMap, KnCorpInOutParamDTO inOutParamDTO, String xdmsHomePttId) throws KnException {
        String methodName = "deleteExtContactDataFrmOtherCorp(KnIPCorpContactDTO, KnSubsProfileDTO, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();

        String mdn = contactDTO.getMdn();


        int corpId = contactDTO.getCorpId();

        knLogger.debug(methodName, "xdmsHomePttId - ", xdmsHomePttId);

        //1. Delete this subscriber from contact list of all the subscribers to whom he belongs to
        //2. Delete this subscriber from all groups he belongs to
        //3. Delete association from all the Sublists pushed to him
        //4. Delete this subscriber from all the Sublists where he is a member
        //5. Delete his Private ConatctList Sublist

        LinkedList<String> mdnList = new LinkedList<String>();
        mdnList.add(mdn);
        //determine the corporates where the mdn is an external member.
        //Collection<Integer> corpList = contactInfoUtil.getExtCorpForSub(mdn, xdmsHomePttId, persisterTxn);
        if (corpIdExtContactNameMap == null) {
            corpIdExtContactNameMap = contactInfoUtil.getCorpIdListWhereIsExternalContact(mdn, xdmsHomePttId, persisterTxn);//passed as param
        }

        //if (corpList == null || corpList.size() <= 0) {
        //}
        // deleting the ext contact from ext contact table
        knLogger.debug(methodName, "corpIdExtContactNameMap - ", corpIdExtContactNameMap);
        Collection<Integer> corpIdList = corpIdExtContactNameMap.keySet();

        knLogger.debug(methodName, "corpIdList - ", corpIdList);

        // get all sublist ids where this MDN is member in other corporation as ext contact.
        Map<Integer, Collection<String>> sublistMemberMap = sublistInfoUtil.
                getSubcriberSublistMemberShipListAsExtContact(mdnList, corpId, xdmsHomePttId, persisterTxn); //externalSUslistId,mdn
        //get all group ids where this mdn is member in other corporation as ext contact.
        Map<Integer, Collection<String>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupListAsExtContact(mdnList, corpId,
                xdmsHomePttId, persisterTxn); //externalGroupid,mdn
        Collection<Integer> groupIds = groupMemberMap.keySet();
        Collection<Integer> sublistList = sublistMemberMap.keySet();
        Collection<String> contactMDNs = new ArrayList<String>();
        if (sublistMemberMap != null && !sublistMemberMap.isEmpty()) {
            Collection<Integer> sublistIdList = sublistMemberMap.keySet();
            sublistInfoUtil.deleteMembersFromAllSublist(sublistMemberMap, xdmsHomePttId, persisterTxn);
            sublistInfoUtil.fetchAndUpdateSublistEtag(sublistIdList,
                    xdmsHomePttId, persisterTxn);
                /*knLogger.debug(methodName, "Calling sublist List Distribution list - ", sublistMemberMap);
                Map<Integer, Collection<KnCorpSubscriberDTO>> contactMemberMap = contactInfoUtil.getSublistListDistributionList(
                        sublistList, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "contactMemberMap - ", contactMemberMap);
                for (int sublistId : sublistList) {
                    Collection<KnCorpSubscriberDTO> contactList = contactMemberMap.get(sublistId);
                    if (contactList != null && !contactList.isEmpty()) {
                        for (KnCorpSubscriberDTO corpSubscriberDTO : contactList) {
                            String contact = corpSubscriberDTO.getMdn();
                            if (!mdnList.contains(contact)) {
                                contactMDNs.add(contact);
                            }
                        }
                    }
                }*/
            //knLogger.info(methodName, "ContactList - ", KnGDPRTemplate.mdnList(contactMDNs));
        }
        LinkedHashMap<String, LinkedList<Integer>> delContactStatus = null;
        LinkedHashMap<Integer, LinkedList<Integer>> delGrpMemStatus = null;
        LinkedHashMap<String, LinkedList<String>> removeMdnListMap = new LinkedHashMap<String, LinkedList<String>>();
        // Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
            /*if (contactMDNs != null && !contactMDNs.isEmpty()) {
                //deleting members from the contact list table
                for (String subscMdn : contactMDNs) {
                    removeMdnListMap.put(subscMdn, mdnList);
                }
                //delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(removeMdnListMap, xdmsHomePttId, persisterTxn);
                knLogger.info(methodName, "Updating ResourceList Etags");
                etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMDNs, xdmsHomePttId, etagMap, persisterTxn);
            }*/
        //
        LinkedHashMap<Integer, LinkedList<String>> removeGrpMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();
        Map<Integer, Collection<String>> groupDistList = new HashMap<Integer, Collection<String>>();
        if (groupMemberMap != null && !groupMemberMap.isEmpty()) {
            //Updating the groups member count.
            Collection<Integer> sublistIdList = sublistMemberMap.keySet();
            // this method will update the contact count and group member count.
                /*sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdList, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                        MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);*/
            //Get the group member count and group type for the groupIds and set these values into etag Map
            //Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupMemberMap.keySet(), xdmsHomePttId, persisterTxn);

            //delete members from the corpgroupmemberlist table
                /*for (int grpId : groupIds) {
                    removeGrpMdnListMap.put(grpId, mdnList);
                }*/
            //delGrpMemStatus = groupInfoUtil.deleteCorpGroupMemberList(removeGrpMdnListMap, xdmsHomePttId, persisterTxn);
            //knLogger.info(methodName, "Updating Group Etags");
            //Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIds, xdmsHomePttId, persisterTxn);
            //knLogger.info(methodName, "Updating Subs Group Etags");
            //Determine the deleted and the modified groups
            Map<String, HashMap<Integer, String>> groupListStatus =
                    groupInfoUtil.getGroupListStatus(groupIds, xdmsHomePttId, persisterTxn);
            groupDistList = groupInfoUtil.getGroupSubscriberDistList(groupIds, xdmsHomePttId, persisterTxn);
            // Delete corp groups with < 2 members
            if (groupListStatus.get(KnConstants.DELETED) != null) {
                Set<Integer> delGroupIdList = groupListStatus.get(KnConstants.DELETED).keySet();
                if (delGroupIdList != null && !delGroupIdList.isEmpty()) {
                    //Map<Integer, Collection<String>> deletedMembersMap = new HashMap<Integer, Collection<String>>();

                    for (Integer grpId : delGroupIdList) {
                        Collection<String> delMembersList = groupDistList.get(grpId);
                        //deletedMembersMap.put(grpId, delMembersList);
                        //for the contact added to update the directory
                        //contactMDNs.addAll(delMembersList);
                        removeGrpMdnListMap.remove(grpId);
                        delGrpMemStatus.remove(grpId);
                    }
                    groupInfoUtil.deleteAllGroups(delGroupIdList, xdmsHomePttId, inOutParamDTO, persisterTxn);
                        /*etagMap = commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                                com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                        groupNameMap.putAll(groupListStatus.get(KnConstants.DELETED));
                        etagMap = commonInfoUtil.formTGSCDocumentNotification(inOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                        etagMap = commonInfoUtil.formTGSSDocumentNotification(inOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);*/
                }
            }
                /*if (groupListStatus.get(KnConstants.MODIFIED) != null) {
                    Set<Integer> modGroupIdList = groupListStatus.get(KnConstants.MODIFIED).keySet();
                    if (modGroupIdList != null && !modGroupIdList.isEmpty()) {
                        Map<Integer, Collection<String>> modifiedMembersMap = new HashMap<Integer, Collection<String>>();

                        for (Integer grpId : modGroupIdList) {
                            Collection<String> modMembersList = groupDistList.get(grpId);
                            modifiedMembersMap.put(grpId, modMembersList);
                            //for the contact added to update the directory
                            //contactMDNs.addAll(modMembersList);
                        }
                        etagMap = commonInfoUtil.formSubscriberNotification(modifiedMembersMap, groupEtagMap,
                                com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
                        groupNameMap.putAll(groupListStatus.get(KnConstants.MODIFIED));
                    }
                }*/
        }

        Set<Integer> sharedSublistSet = sublistMemberMap.keySet();
        if (sharedSublistSet != null && !sharedSublistSet.isEmpty()) {
            ArrayList<Integer> sharedSublists = sublistInfoUtil.getSharedSublistFromList(sharedSublistSet, xdmsHomePttId, persisterTxn);
            ArrayList<Integer> emptySublist = sublistInfoUtil.getEmptySublistFrmList(sharedSublists, xdmsHomePttId, persisterTxn);
            if (emptySublist != null && !emptySublist.isEmpty()) {
                sublistInfoUtil.deleteAllSublist(emptySublist, xdmsHomePttId, persisterTxn);
            }
        }

            /*if (!contactMDNs.isEmpty()) {
                knLogger.info(methodName, "Updating Directory Etags");
                contactInfoUtil.updateSubscribersContactCount(contactMDNs, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
                etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMDNs, null, etagMap, xdmsHomePttId, persisterTxn);
            }*/
        // deleting the ext contact from ext contact table
        if (!corpIdExtContactNameMap.isEmpty()) {
            contactInfoUtil.deleteSusbcribersFromExtContactTables(mdn, xdmsHomePttId, persisterTxn);
            contactInfoUtil.updateCorporateEtagForIdList(corpIdList, xdmsHomePttId, persisterTxn);
        }
            /*etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, removeMdnListMap, null, null, removeGrpMdnListMap, null, null, groupDistList, delContactStatus, delGrpMemStatus, null);
            knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
            etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
            knLogger.debug(methodName, "etagMap after auth mapping modification- ", KnGDPRTemplate.mapKeyMdn(etagMap));
            //respDTO.setChangeLogMap(etagMap);
            // populate(respDTO);
*//*
            knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);*/
    }

    public void deleteMcpttDoc(String mdn, String xdmsHomePttId,
                                KnPersisterTxn persisterTxn, boolean lastSubscriber) throws KnCorpBOException {
        String methodName = "deleteMcpttDoc(String, String, Map<String, KnOPDirChgDTO>, KnPersisterTxn, boolean)";
        LinkedList<String> mdnList = new LinkedList<String>();
        mdnList.add(mdn);
        /*Map<String, KnMcpttPermissionDTO> mcpttTargPermissionMap = corpSubsProvInfoUtil.getTargUserPermissions(mdn, xdmsHomePttId, persisterTxn);//where targetMdn
        Collection<KnSubsDestEmergencyAttributes> mdnEmergAttributesForDestination =
                corpSubsProvInfoUtil.getEmergDestAttributesForDestination(mdn, xdmsHomePttId, false, persisterTxn);//where emergencydest = ?*/

        corpSubsProvInfoUtil.deleteFromMcpttPermInfoAuthMdn(mdn, xdmsHomePttId, persisterTxn);//yy where auth mdn=?
        corpSubsProvInfoUtil.deleteFromAuthDoc(mdnList, xdmsHomePttId, persisterTxn);//yy
        corpSubsProvInfoUtil.deleteFromEmergSubsDestInfo(mdn, xdmsHomePttId, persisterTxn);//yy
        corpSubsProvInfoUtil.deleteFromEmergDoc(mdnList, xdmsHomePttId, persisterTxn);//yy
        if (lastSubscriber) {//keep this data if its not the last susbcriber
            corpSubsProvInfoUtil.deleteFromEmergInfoForDest(mdn, xdmsHomePttId, persisterTxn);//nn
            corpSubsProvInfoUtil.deleteFromMcpttPermInfoTargetMdn(mdn, xdmsHomePttId, persisterTxn);//nn where targ mdn=?
        }
        /*if(mcpttTargPermissionMap != null && !mcpttTargPermissionMap.isEmpty()){//nn in the async
            for(String authMdn : mcpttTargPermissionMap.keySet()){
                etagMap = commonInfoUtil.formMdnAuthDeleteNotification(authMdn, etagMap);
            }
        }
        if(mdnEmergAttributesForDestination != null && !mdnEmergAttributesForDestination.isEmpty()){//nn in the async
            for(KnSubsDestEmergencyAttributes emergencyDestination : mdnEmergAttributesForDestination){
                etagMap = commonInfoUtil.formMdnEmergDeleteNotification(emergencyDestination.getMdn(), etagMap);
            }
        }
        etagMap = commonInfoUtil.formMdnAuthDeleteNotification(mdn, etagMap);
        etagMap = commonInfoUtil.formMdnEmergDeleteNotification(mdn, etagMap);
        knLogger.info(methodName, "EXIT : Returning Response - ", KnGDPRTemplate.mapKeyMdn(etagMap));*/
        //return etagMap;
    }

    public KnCorpInfoResDTO cleanUpCorporateProfile(KnIPCorpContactDTO corpContactDTO, KnCorpProfileDTO corpProfile,
                                                    KnCorpInOutParamDTO corpInOutParamDTO, String xdmsHome, KnPersisterTxn persisterTxn) {
        String methodName = "cleanUpCorporateProfile(corpContactDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", corpContactDTO);


        KnCorpInfoResDTO respDTO = new KnCorpInfoResDTO();
        try {
            //get MDN
            int corpId = corpContactDTO.getCorpId();
            String mdn = corpContactDTO.getMdn();

            if(corpProfile == null){
                knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
                corpProfile = commonInfoUtil.getProfileDetails("" + corpContactDTO.getCorpId(), "CorpAdmin", false, persisterTxn);
            }

            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            //KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + corpProfileDetails.getCorpId(), CORP_PROFILE, false, persisterTxn);


            KnIPCorpInfoDTO corpInfoDTO = new KnIPCorpInfoDTO();
            corpInfoDTO.setCorpId(corpProfile.getCorpId());

            //delete Subscriber Contact List
            contactInfoUtil.deleteSubscribersContactList(mdn, xdmsHome, persisterTxn);//yy where contact mdn=mdn

            //Determine all the sublist for the corportae irrespective of the distribution policy or list type

            //Determine all the groups for the corporate
            Collection<KnCorpGroupInfoPersistDTO> groupList =
                    groupInfoUtil.getAllGroupList(corpInfoDTO, corpProfile.getMaxMemPerCorpGroup(), xdmsHome, persisterTxn);//where corpid=? query id="186"

            //Deleting the subscriber from shared groups.
            //String xdmsHomePttId = corpProfile.getXdmsHome();
            Set<Integer> groupIds = groupInfoUtil.getAllSubscribersGroupListForAllCorporate(Collections.singletonList(mdn),
                    xdmsHome, persisterTxn).keySet(); //query id="192" - WHERE CGML.MEMBERMDN IN (MDNLIST)
            groupIds.removeAll(groupList.stream().map(KnCorpGroupInfoPersistDTO::getGroupId).collect(Collectors.toSet()));
            knLogger.debug(methodName, " Shared groups after filteration  ", groupIds);//only shared
            if (!groupIds.isEmpty()) {//yy not deleting from memberlist for other corp here will send the notification
                List<KnCorpGroupDTO> groupInfoList = groupInfoUtil.getGroupBasicInfoList(groupIds, xdmsHome, persisterTxn);
                Map<Integer, Collection<String>> memberMdnSublistMap = new HashMap<>();
                groupInfoList.forEach(groupDto -> memberMdnSublistMap.put(groupDto.getGrpMemListId(), Collections.singletonList(mdn)));
                knLogger.debug(methodName, " memberMdnSublistMap - ", memberMdnSublistMap);
                //delete the member from sublist
                sublistInfoUtil.deleteMembersFromAllSublist(memberMdnSublistMap, xdmsHome, persisterTxn);
                LinkedHashMap<Integer, LinkedList<String>> removeGrpMdnListMap = new LinkedHashMap<>();
                LinkedList<String> mdnList = new LinkedList<>();
                mdnList.add(mdn);
                groupIds.forEach(groupId -> removeGrpMdnListMap.put(groupId, mdnList));
                //delete from memberList table
                //groupInfoUtil.deleteCorpGroupMemberList(removeGrpMdnListMap, xdmsHomePttId, persisterTxn);
                // Update the group member count
                //sublistInfoUtil.updateSublistsSubscribersContactCount(groupIds, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                //        MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
                Map<Integer, Map<String, Collection<String>>> groupMemDelMap = new HashMap<Integer, Map<String, Collection<String>>>();
                for (int grpId : groupIds) {
                    Map<String, Collection<String>> deletedMemberMap = new HashMap<String, Collection<String>>();
                    deletedMemberMap.put(KnConstants.DELTED_MEMBERS, removeGrpMdnListMap.get(grpId));
                    groupMemDelMap.put(grpId, deletedMemberMap);
                }
                //deleting from DistInfo table
                groupInfoUtil.deleteFrmCorpGroupDistInfo(groupMemDelMap, xdmsHome, persisterTxn);
            }

            knLogger.debug(methodName, "Retrieved CorpGroup List - ", groupList);
            if (groupList != null && !groupList.isEmpty()) {
                Collection<Integer> groupIdsList = new ArrayList<Integer>();
                Collection<String> mdnList = new ArrayList<String>();
                mdnList.add(mdn);
                Map<Integer, Collection<String>> removeGrpMdnListMap = new HashMap<Integer, Collection<String>>();
                for (KnCorpGroupInfoPersistDTO groupPersistTO : groupList) {
                    int grpId = groupPersistTO.getGroupId();
                    groupIdsList.add(groupPersistTO.getGroupId());
                    removeGrpMdnListMap.put(grpId, mdnList);
                }
                //getting the record from DG.RECORDING_TARGET_INFO.
                List<String> target = new ArrayList<>();
                for (int groupId : groupIdsList) {
                    target.add(corpId + ("_") + groupId);
                }
                IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmsHome);
                //deleting the record from DG.RECORDING_TARGET_INFO.
                commonXDMServerDAO.deleteRecordingInfoTargetByTarget(target, persisterTxn);
                //Remove all the groups list ref entries
                groupInfoUtil.deleteAllGroups(groupIdsList, xdmsHome, corpInOutParamDTO, persisterTxn); // keeping delete all groups here
                //etagMap = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, etagMap, xdmsHome, persisterTxn);
                //etagMap = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, etagMap, xdmsHome, persisterTxn);
                //respDTO.setChangeLogMap(etagMap);
            }
            //getting shared groups of owned corp
            List<KnCorpSharedCorpInfo> ownedCorpGroupInfo = groupInfoUtil.selectGroupSharedCorpInfoByOwnedCorpId(corpId, xdmsHome, persisterTxn);//where ownercorpid=?
            respDTO.setGroupIds(ownedCorpGroupInfo.stream().map(KnCorpSharedCorpInfo::getCorpGroupId).collect(Collectors.toList()));
            respDTO.setMdnCorpId(corpId);
            //deleting group sharing data
            groupProfilUtil.deleteGroupProfileSharedInfoByOwnedCorpId(corpId, xdmsHome, persisterTxn);
            groupProfilUtil.deleteGroupProfileByCorpId(corpId, xdmsHome, persisterTxn);
            Collection<String> upmIds = userProfileUtil.getUserProfileIdsByCorpId(corpId, xdmsHome, persisterTxn);
            groupInfoUtil.deleteProfileGroupInfoByProfileIds(new ArrayList<>(upmIds), xdmsHome, persisterTxn);
            groupInfoUtil.deleteSharedGroupsByOwnedCorp(corpId, xdmsHome, persisterTxn);
            KnGeneralCacheUtil.getInstance().deleteCorpMatrixByOwnedCorpId(corpProfile.getExtCorpId().trim());

            //Determine all the sublist for the corportae irrespective of the distribution policy or list type
            Collection<Integer> sublistIdsList = sublistInfoUtil.getCorpSublistIdList(corpInfoDTO, xdmsHome, persisterTxn);
            if (sublistIdsList != null && !sublistIdsList.isEmpty()) {
                sublistInfoUtil.deleteAllSublist(sublistIdsList, xdmsHome, persisterTxn);
            }
            //Deleting the external guys for the corporate
            contactInfoUtil.deleteCorporateExternalMembers(corpInfoDTO, xdmsHome, persisterTxn);

            //Nullify the corpid for the corp members in the external table
//            contactInfoUtil.nullifyContactCorpIdInExtTable(corpId, xdmsHome, persisterTxn);

            //deleting all user profiles for the corporate
            userProfileUtil.deleteUserProfilesByCorpId(String.valueOf(corpId),xdmsHome,persisterTxn);
            //Step:
            //prepare Group List respone
            populate(respDTO);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured during forceSyunc ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured during forceSyunc ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);

        }
        knLogger.info(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }


}

