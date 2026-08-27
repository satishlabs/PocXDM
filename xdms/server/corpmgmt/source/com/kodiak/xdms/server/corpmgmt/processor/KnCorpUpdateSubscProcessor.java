/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.processor;

import com.kodiak.common.commdto.request.KnXDMCorpSubscInfoRequestDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.dto.KnPayloadIP;
import com.kodiak.common.dto.KncontactNotifyInfoDTO;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpInfoResDTO;
import com.kodiak.common.resources.KnPayloadIdentifier.KnPLIden;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;

import java.util.*;
import static com.kodiak.xdms.server.common.dao.persister.db.tables.xdm.KnXDMCBUserProfileMgmtDAO.knLogger;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE.POC_WIFIONLY;

public class KnCorpUpdateSubscProcessor {

    private KnCorpCommonInfoUtil commonInfoUtil;
    private boolean initiateCorpDocCleanup = false;
    private KnXDMCorpSubscInfoRequestDTO corpSubsRequestDTO;
    private KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    private KnCorpContactInfoUtil contactInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnCorpActivationInfoUtil activationInfoUtil;
    private boolean switchConvergedClient = false;
    private boolean deleteSubscribersCorpData = false;
    private boolean deleteExtContactDataFrmOtherCorp = false;
    private KnCorpGroupProfileUtil groupProfilUtil;
    private KnCorpUserProfileUtil userProfileUtil;
    private boolean cleanUpCorporateProfile = false;
    private boolean updateSusbcribersCorpIdInImpactedTables = false;

    public KnCorpUpdateSubscProcessor() {
        final String methodName = "KnCorpUpdateSubscProcessor Constructor";
        knLogger.info(methodName, "in Corp Update Subscriber Processor.");
        commonInfoUtil = new KnCorpCommonInfoUtil();
        corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        contactInfoUtil = new KnCorpContactInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        activationInfoUtil = new KnCorpActivationInfoUtil();
        groupProfilUtil = new KnCorpGroupProfileUtil();
        userProfileUtil = new KnCorpUserProfileUtil();
    }

    public KnCorpInfoResDTO processUpdateSubscriber(KnXDMCorpSubscInfoRequestDTO corpSubsRequestDTO, KnPayloadIP knPayloadIP,
                                                    BitSet taskBitSet, KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "processUpdateSubscriber(corpSubsRequestDTO, persisterTxn)";
        this.setCorpSubsRequestDTO(corpSubsRequestDTO);
        KnCorpInfoResDTO respDTO = new KnCorpInfoResDTO();
        boolean ownedTxn = false;
        Map<String, Object> knPayloadCarrier = knPayloadIP.getKnPayloadCarrier();

        try {
            knLogger.info(methodName, "ENTRY: process UpdateSubscriber with DTO - ", corpSubsRequestDTO);
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
            KnCorpInOutParamDTO corpInOutParamDTO = new KnCorpInOutParamDTO();

            String mdn = corpSubsRequestDTO.getSubscriberMdn();
            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            KnSubsProfileDTO subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    true, persisterTxn);
            int corpId = Integer.parseInt(corpSubsRequestDTO.getCorpId());
            respDTO.setCorpId(corpId);

            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails("" + subsProfile.getCorpId(), CORP_PROFILE,
                    false, persisterTxn);

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = subsProfile.getXdmsHome();
            knLogger.info(methodName, "xdmsHomePttId - ", xdmsHomePttId);

            if (corpSubsRequestDTO.getName() != null && !corpSubsRequestDTO.getName().equals(corpSubsRequestDTO.getOldName())) {
                // Call this in ASYNC
                // passing old and new name to Async controller to update it in the backend.
                // TODO - Logic to be implemented in ASYNC from: KnCorpGenericInfoController.updateSubscriberName
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.updateContactName.get(), true);
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isContactNotifyRequired.get(), true);
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.notifyAllGroupMembers.get(), true);
                List<String> modifiedMdns = new ArrayList<>();
                modifiedMdns.add(mdn);
                KncontactNotifyInfoDTO kncontactNotifyInfoDTO = new KncontactNotifyInfoDTO();
                kncontactNotifyInfoDTO.setModifiedContacts(modifiedMdns);
                knPayloadIP.setKncontactNotifyInfoDTO(kncontactNotifyInfoDTO);
                knPayloadCarrier.put(KnPLIden.NEW_CONTACT_NAME.name(), corpSubsRequestDTO.getName());
                knPayloadCarrier.put(KnPLIden.OLD_CONTACT_NAME.name(), corpSubsRequestDTO.getOldName());
            } else {
                corpSubsRequestDTO.setName(corpSubsRequestDTO.getOldName());
            }

            if (corpSubsRequestDTO.getCorpSubscriptionType() != corpSubsRequestDTO.getNewCorpSubscriptionType()) {
                // Call this in ASYNC
                // passing old and new name to Async controller to update it in the backend.
                // TODO - Logic to be implemented in ASYNC from: KnCorpGenericInfoController.updateSubscSubscriptionType
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.updateSubscriptionType.get());
                knPayloadCarrier.put(KnPLIden.NEW_SUBSCRIPTION_TYPE.name(), corpSubsRequestDTO.getNewCorpSubscriptionType());
                knPayloadCarrier.put(KnPLIden.OLD_SUBSCRIPTION_TYPE.name(), corpSubsRequestDTO.getCorpSubscriptionType());

                if (checkSubscriptionTypeChangeType()) {
                    initiateCorpDocCleanup = true;
                }
            }

            if (corpSubsRequestDTO.getClientType() != corpSubsRequestDTO.getNewClientType()) {
                // Call this in ASYNC
                // passing old and new name to Async controller to update it in the backend.
                // TODO - Logic to be implemented in ASYNC from: KnCorpGenericInfoController.updateSubscribersClientType
                taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.updateClientType.get());
                knPayloadCarrier.put(KnPLIden.NEW_CLIENT_TYPE.name(), corpSubsRequestDTO.getNewClientType());
                knPayloadCarrier.put(KnPLIden.OLD_CLIENT_TYPE.name(), corpSubsRequestDTO.getClientType());

                if (checkClientTypeUpdateType()) {
                    initiateCorpDocCleanup = true;
                }
            }

            if (taskBitSet.get(KnGeneralUtil.ASYNC_TASK_ID.updateCorpId.get())) {
                // Call this in ASYNC
                // passing old and new name to Async controller to update it in the backend.
                // TODO - Logic to be implemented in ASYNC from: KnCorpGenericInfoController.updateSubscribersCorpId
                knPayloadCarrier.put(KnPLIden.NEW_CORP_ID.name(), corpSubsRequestDTO.getNewCorpId());
                knPayloadCarrier.put(KnPLIden.OLD_CORP_ID.name(), corpSubsRequestDTO.getNewCorpId());

                initiateCorpDocCleanup = true;
            }

            // Performing operations in Sync path here.

            if (initiateCorpDocCleanup) {
                // Cleanup the corporate data of the customer here.
                KnCorpDocCleanUpProcessor corpDocCleanUpProcessor = new KnCorpDocCleanUpProcessor();
                KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
                contactDTO.setCorpId(corpId);
                contactDTO.setMdn(mdn);
                KnCorpDocCleanUpProcessor cleanup = new KnCorpDocCleanUpProcessor();

                HashMap<Integer, String> groupNameMap = new HashMap<Integer, String>();
                if (deleteSubscribersCorpData) {
                    cleanup.deleteSubscleanUpCorporateProfile(contactDTO, subsProfile, xdmsHomePttId, persisterTxn);
                    respDTO.setDeleteSubscribersCorpData(true);
                }

                if (deleteExtContactDataFrmOtherCorp) {
                    Map<Integer, String> corpIdExtContactNameMap = new HashMap<>();
                    cleanup.deleteExtContactDataFrmOtherCorp(contactDTO, persisterTxn, corpIdExtContactNameMap, corpInOutParamDTO,xdmsHomePttId);
                    respDTO.setDeleteSubscribersCorpData(true);
                }

                if (switchConvergedClient) {
                    switchConvergedClient(contactDTO, subsProfile, persisterTxn, etagMap);
                    taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.notifyAllGroupMembers.get(),true);
                    taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.isContactNotifyRequired.get(),true);
                    List<String> modifiedMdns = new ArrayList<>();
                    modifiedMdns.add(mdn);
                    KncontactNotifyInfoDTO kncontactNotifyInfoDTO = new KncontactNotifyInfoDTO();
                    kncontactNotifyInfoDTO.setModifiedContacts(modifiedMdns);
                    knPayloadIP.setKncontactNotifyInfoDTO(kncontactNotifyInfoDTO);
                }

                if (cleanUpCorporateProfile) {
                    int count = contactInfoUtil.getCorpSubscriberCount(Integer.parseInt(corpSubsRequestDTO.getCorpId()), xdmsHomePttId, persisterTxn);
                    // cleanup the corporate profile if he is the last subs in corp
                    if (count == 0) {
                        cleanup.cleanUpCorporateProfile(contactDTO, corpProfile, corpInOutParamDTO, xdmsHomePttId, persisterTxn);
                    }
                    // Clean the CORP profile after the last contact from the CORP is deleted.
                }

                if(updateSusbcribersCorpIdInImpactedTables)
                { //self mdn changes
                    contactInfoUtil.updateSusbcribersCorpIdInImpactedTables(String.valueOf(corpId), mdn, xdmsHomePttId, persisterTxn);
                    // Cleaning impacted tables.
                }
            }

            respDTO.setStatus(0); // set status after cleanup operations.
            knLogger.info(methodName, "EXIT: process UpdateSubscriber operation ");
            return respDTO;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while update Subscriber");
            knLogger.error(methodName, e);
            if(ownedTxn)
            {
                persisterTxn.rollback();
            }
            throw new Exception(e);
        }
    }

    private boolean checkClientTypeUpdateType() {

        int clientType = corpSubsRequestDTO.getClientType();
        int newClientType = corpSubsRequestDTO.getNewClientType();
        String methodName = "checkClientTypeUpdateType(KnPersisterTxn persisterTxn)";
        switch (clientType) {
            case KnConstants.CLIENT_TYPE_UNKNOWN:
                knLogger.debug(methodName, "Changing from unknown ");

                switch (newClientType) {
                    case KnConstants.CLIENT_TYPE_DISPATCH:
                        // Client Type is changing from unknown to dispatch
                        // need to delete all corporate data of the corporation and
                        // ext contact data of other corporation
                        knLogger.debug(methodName, "Changing from unknown to Distatch.");
                        deleteSubscribersCorpData = true;
                        deleteExtContactDataFrmOtherCorp = true;
                        return true;

                    case KnConstants.INTER_OP_CLIENT_TYPE:
                        // Changing from unknown to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from unknown to interOP subscriber ");
                        deleteSubscribersCorpData = true;
                        deleteExtContactDataFrmOtherCorp = true;
                        return true;
                }// inner switch end
                break;

            case KnConstants.CLIENT_TYPE_HANDSET:
                knLogger.debug(methodName, "Changing from Handset ");
                switch (newClientType) {
                    case KnConstants.CLIENT_TYPE_DISPATCH:
                        // Changing from Handset to Dispatch.
                        // Need to delete ext contact data in other corporation
                        // and delete corp data in his corporation
                        knLogger.debug(methodName, "Changing from Handset to Dispatch");
                        deleteExtContactDataFrmOtherCorp = true;
                        deleteSubscribersCorpData = true;
                        return true;
                    case KnConstants.INTER_OP_CLIENT_TYPE:
                        // Changing from handset to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from handset to interOP subscriber ");
                        deleteExtContactDataFrmOtherCorp = true;
                        deleteSubscribersCorpData = true;
                        return true;
                    case KnConstants.PTTRADIOHANDSETCLIENT:
                        // Changing from handset to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from handset to PttHandset subscriber ");
                        switchConvergedClient = true;
                        return true;
                } // inner switch end
                break;

            case KnConstants.CLIENT_TYPE_DESKTOP:
                knLogger.debug(methodName, "Changing from Desktop ");
                switch (newClientType) {
                    case KnConstants.CLIENT_TYPE_DISPATCH:
                        // Changing from Desktopt to Dispatch.
                        // Need to delete ext contact data in other corporation
                        // and delete corp data in his corporation
                        knLogger.debug(methodName, "Changing from Desktopt to Dispatch");
                        deleteSubscribersCorpData = true;
                        deleteExtContactDataFrmOtherCorp = true;
                        return true;
                    case KnConstants.INTER_OP_CLIENT_TYPE:
                        // Changing from desktop to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from desktop to interOP subscriber ");
                        deleteSubscribersCorpData = true;
                        deleteExtContactDataFrmOtherCorp = true;
                        return true;
                } // inner switch end
                break;

            case KnConstants.CLIENT_TYPE_DISPATCH:
                knLogger.debug(methodName, "Changing from Dispatch to Any ");
                deleteSubscribersCorpData = true;
                return true;

            case KnConstants.INTER_OP_CLIENT_TYPE:
                knLogger.debug(methodName, "Changing from InterOP to any other client ");
                // Deleteing the subscriber from group private sublist and group where he is.
                deleteSubscribersCorpData = true;
                return true;

            case KnConstants.WIFI_CLIENT_TYPE:
                knLogger.debug(methodName, "Changing from WIFI Client ");
                switch (newClientType) {
                    case KnConstants.CLIENT_TYPE_DISPATCH:
                        // Changing from Desktopt to Dispatch.
                        // Need to delete ext contact data in other corporation
                        // and delete corp data in his corporation
                        knLogger.debug(methodName, "Changing from Wifi to Dispatch");
                        deleteSubscribersCorpData = true;
                        deleteExtContactDataFrmOtherCorp = true;
                        return true;
                    case KnConstants.INTER_OP_CLIENT_TYPE:
                        // Changing from desktop to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from Wifi to interOP subscriber ");
                        deleteSubscribersCorpData = true;
                        deleteExtContactDataFrmOtherCorp = true;
                        return true;
                    case KnConstants.PTTRADIOWIFIONLYCLIENT:
                        // Changing from desktop to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from Wifi to PttWifi subscriber ");
                        switchConvergedClient = true;
                        return true;
                }

            case KnConstants.THIRD_PARTYPOC_CLIENT_TYPE:
                knLogger.debug(methodName, "Changing from 3rd party PoC Client ");
                switch (newClientType) {
                    case KnConstants.CLIENT_TYPE_DISPATCH:
                        // Changing from Desktopt to Dispatch.
                        // Need to delete ext contact data in other corporation
                        // and delete corp data in his corporation
                        knLogger.debug(methodName, "Changing from 3rdpoc to Dispatch");
                        deleteSubscribersCorpData = true;
                        deleteExtContactDataFrmOtherCorp = true;
                        return true;
                    case KnConstants.INTER_OP_CLIENT_TYPE:
                        // Changing from desktop to interOP.
                        // Delete corporate and ext contact datas.
                        knLogger.debug(methodName, "Changing from 3rdpoc to interOP subscriber ");
                        deleteSubscribersCorpData = true;
                        deleteExtContactDataFrmOtherCorp = true;
                        return true;
                }

            case KnConstants.CLIENT_TYPE_CROSSCARRIER:
                knLogger.debug(methodName, "Changing from CrossCarrier ");
                switch (newClientType) {
                    case KnConstants.PTTRADIOCROSSCARRIERCLIENT:
                           /*  Changing from crossCarrier to pttCrossCarrier:
                            1. Need to Remove the Channel List/Scan list associated with the Subscriber.
                            2. Send Group/contact doc change notify clients where Subscr is a member of.
                            3. Send tgsc doc change notify to Client if applicable.
                            4. Send Group doc change event notifies to Micro Services. */
                        knLogger.debug(methodName, "Changing from CrossCarrier to PttCrossCarrier");
                        switchConvergedClient = true;
                        return true;
                }

            case KnConstants.PTTRADIOHANDSETCLIENT:
            case KnConstants.PTTRADIOCROSSCARRIERCLIENT:
            case KnConstants.PTTRADIOWIFIONLYCLIENT:
                knLogger.debug(methodName, "Changing from pttHandset, pttCrossCarrier, pttWifi");
                switch (newClientType) {
                    case KnConstants.CLIENT_TYPE_HANDSET:
                    case KnConstants.CLIENT_TYPE_CROSSCARRIER:
                    case KnConstants.WIFI_CLIENT_TYPE:
                        /*  Changing from PttRadio to Non-PttRadio:
                            1. Need to Remove the Channel List/Scan list associated with the Subscriber.
                            2. Send Group/contact doc change notify clients where Subscr is a member of.
                            3. Send tgsc doc change notify to Client if applicable.
                            4. Send Group doc change event notifies to Micro Services. */
                        knLogger.debug(methodName, "Changing from PttRadio to Non-PttRadio");
                        switchConvergedClient = true;
                        return true;
                }
                knLogger.debug(methodName, "Changing from 3rdpoc to interOP subscriber ");
                break;
        }
        return false;
    }

    private boolean checkSubscriptionTypeChangeType() throws KnCorpBOException {

        int subscriptionType = corpSubsRequestDTO.getCorpSubscriptionType();
        int newSubscriptionType = corpSubsRequestDTO.getNewCorpSubscriptionType(); // Use this to check TO type; if required.
        String methodName = "checkSubscriptionTypeChangeType()";
        switch (subscriptionType) {
            case KnConstants.SUBSCRIPTION_TYPE_PUBLIC: {
                knLogger.debug(methodName, " Changing from Public - ");
                switch (newSubscriptionType) {
                    case KnConstants.SUBSCRIPTION_TYPE_CORPORATE: {
                        // changing from public to corporate
                        // need to delete external data of all corporation
                        // because corporate subsc can not exist as external contact.
                        knLogger.debug(methodName, " Changing from Public to Corporate ");
                        deleteSubscribersCorpData = true;
                        return true;
                    }
                    case KnConstants.SUBSCRIPTION_TYPE_PUBLIC_CORP: {
                        // changing from public to public & corporate
                        // update the corpid in ext contact table and list member table.
                        knLogger.debug(methodName, " Changing from Public to Corporate&Public ");
                        updateSusbcribersCorpIdInImpactedTables = true;
                        break;
                    }
                } // inner switch close
            }
            break;

            case KnConstants.SUBSCRIPTION_TYPE_CORPORATE: {
                knLogger.debug(methodName, " Changing from Corporate - ");
                switch (newSubscriptionType) {
                    case KnConstants.SUBSCRIPTION_TYPE_PUBLIC: {
                        // Changing from corporate to public
                        // Need to clean corporate data.
                        knLogger.debug(methodName, " Changing from Corporate to Public - ");
                        deleteSubscribersCorpData = true;
                        //  set null for corpid in ext contact and list member table.
                        updateSusbcribersCorpIdInImpactedTables = true;
                        //determine the no of members for the corporate
                        cleanUpCorporateProfile = true;
                    }
                }   //inner switch end
            }
            break;

            case KnConstants.SUBSCRIPTION_TYPE_PUBLIC_CORP: {
                knLogger.debug(methodName, " Changing from Corporate&Public - ");
                switch (newSubscriptionType) {
                    case KnConstants.SUBSCRIPTION_TYPE_CORPORATE: {
                        // Changing from Public & corporate to Corporate
                        // need to delete ext contact table
                        knLogger.debug(methodName, " Changing from Corporate&Public to Corporate ");
                        deleteExtContactDataFrmOtherCorp = true;
                        return true;
                    }
                    case KnConstants.SUBSCRIPTION_TYPE_PUBLIC: {
                        // Changing from Public&Corporate to Public
                        // Need to delete corporate data and set corpid to null in ext and list
                        // member table.
                        knLogger.debug(methodName, " Changing from Corporate&Public to Public ");
                        deleteSubscribersCorpData = true;
                        updateSusbcribersCorpIdInImpactedTables = true;
                        //determine the no of members for the corporate
                        cleanUpCorporateProfile = true;
                    }

                } // inner switch end
            }
        }
        return false;
    }

    private void setCorpSubsRequestDTO(KnXDMCorpSubscInfoRequestDTO corpSubsRequestDTO) {
        this.corpSubsRequestDTO = corpSubsRequestDTO;
    }

    private KnCorpResponseDTO switchConvergedClient(KnIPCorpContactDTO contactDTO, KnSubsProfileDTO subsProfile, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnException {
        String methodName = "switchConvergedClient(KnIPCorpContactDTO, KnSubsProfileDTO, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        String mdn = contactDTO.getMdn();
        if (subsProfile == null) {
            knLogger.debug(methodName, "Fetch the SubsProfile profile - ", KnGDPRTemplate.mdn(mdn));
            subsProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE, true, persisterTxn);
        }
        String xdmsHomePttId = subsProfile.getXdmsHome();
        knLogger.debug(methodName, "xdmsHomePttId - ", xdmsHomePttId);
        //Subscriber mdns where mdn belongs as a contact
        /*Collection<String> subscriberList = contactInfoUtil.getContactMDNs(mdn, xdmsHomePttId, persisterTxn);
        Collection<String> contactMDNs = contactInfoUtil.getPocSubscribersDetails(subscriberList, contactDTO.getCorpId(), xdmsHomePttId, persisterTxn);
        *//**
         * INT-95992 - Optimization for switch convergent client
         *           1.Etag update-> The Etag update (XDM_DIRECTORY,CORPGROUPINFO,XDM_CORPRESOURCELISTINDEXDOC) is already handled by Etag-management thread.
         *           So avoiding for duplicate update.
         * 		    2.Notification -> Self notification is not disturbed. The belonging contact and supervisory group notification is cut down. Now on client refresh, the update clienttype data will be populated
         *//*
      *//* if (subscriberList != null && !subscriberList.isEmpty()) {
           etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(subscriberList, xdmsHomePttId, etagMap, persisterTxn);
            knLogger.debug(methodName, "etagMap after updateSubcribersResourceListIndexDoc - ", KnGDPRTemplate.mapKeyMdn(etagMap));
        } *//*
        //Retrieving groups where mdn is part of
        Collection<KnCorpGroupInfoPersistDTO> groupList = groupInfoUtil.getSubsGroupListForXcap(contactDTO, 0,
                xdmsHomePttId, persisterTxn);
        Collection<Integer> groupIds = new ArrayList<Integer>();
        for (KnCorpGroupInfoPersistDTO groupInfoPersistDTO : groupList) {
            groupIds.add(groupInfoPersistDTO.getGroupId());
        }
        Map<Integer, Collection<String>> groupDistInfo;
        Map<Integer, Collection<KnCorpGroupMemberDTO>> groupMemberMap = new HashMap<Integer, Collection<KnCorpGroupMemberDTO>>();*/
        /**
         * INT-95992 - Optimization for switch convergent client
         *           1.Etag update-> The Etag update (XDM_DIRECTORY,CORPGROUPINFO,XDM_CORPRESOURCELISTINDEXDOC) is already handled by Etag-management thread.
         *           So avoiding for duplicate update.
         * 		  2.Notification -> Self notification is not disturbed. The belonging contact and supervisory group notification is cut down. Now on client refresh, the update clienttype data will be populated
         */
      /* if (!groupIds.isEmpty()) {
            Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIds, xdmsHomePttId, persisterTxn);
            Collection<String> mdnList = new ArrayList<String>();
            mdnList.add(mdn);
            groupMemberMap = groupInfoUtil.getAllSubscribersGroupListForAllCorporate(mdnList, xdmsHomePttId, persisterTxn);
            *//*
             Get the group member count and group type for the groupIds and set these values into etag Map
             *//*
            Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupIds, xdmsHomePttId, persisterTxn);
            groupDistInfo = groupInfoUtil.getGroupSubscriberDistList(groupIds, xdmsHomePttId, persisterTxn);
            etagMap = commonInfoUtil.formSubscriberNotification(groupDistInfo, groupEtagMap,
                    DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
        }*/
        /*subscriberList.add(mdn);
        if ((!subscriberList.isEmpty()) || (!groupIds.isEmpty())) {
            knLogger.info(methodName, "Updating Directory Etags");
           etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(subscriberList, groupIds, etagMap, xdmsHomePttId, persisterTxn);
        }
        subscriberList.remove(mdn);*/
        /*KnCorpSubscriberDTO subscInternal = new KnCorpSubscriberDTO();
        KnCorpSubscriberDTO subscExternal = new KnCorpSubscriberDTO();
        subscExternal.setMdn(contactDTO.getMdn());
        subscExternal.setName(subsProfile.getNetworkName());
        subscExternal.setClientType(contactDTO.getNewClientType());
        subscExternal.setContact_type(XCAP_DIFF_CONTACT_TYPE_EXTERNAL_CONTACT);
        subscInternal.setMdn(contactDTO.getMdn());
        subscInternal.setName(subsProfile.getNetworkName());
        subscInternal.setClientType(contactDTO.getNewClientType());
        subscInternal.setUa(KnGeneralUtil.getUA(subsProfile.getUserAgent(), subsProfile.getClientMajorVersion()));
        Map<String, Collection<KnCorpSubscriberDTO>> modifiedContactMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
        Collection<KnCorpSubscriberDTO> corpSubscriberListInternal = new ArrayList<KnCorpSubscriberDTO>();
        Collection<KnCorpSubscriberDTO> corpSubscriberListExternal = new ArrayList<KnCorpSubscriberDTO>();
        corpSubscriberListInternal.add(subscInternal);
        corpSubscriberListExternal.add(subscExternal);
        for (String contact : subscriberList) {
            if (contactMDNs.contains(contact)) {
                modifiedContactMap.put(contact, corpSubscriberListInternal);
            } else {
                modifiedContactMap.put(contact, corpSubscriberListExternal);
            }
        }

        for (Map.Entry<Integer, Collection<KnCorpGroupMemberDTO>> entry : groupMemberMap.entrySet()) {
            Collection<KnCorpGroupMemberDTO> groupMember = entry.getValue();
            for (KnCorpGroupMemberDTO subscriber : groupMember) {
                subscriber.setName(subsProfile.getNetworkName());
                subscriber.setClientType(contactDTO.getNewClientType());
                subscriber.setUa(KnGeneralUtil.getUA(subsProfile.getUserAgent(), subsProfile.getClientMajorVersion()));
            }
        }*/
        Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlTGList(mdn, xdmsHomePttId, persisterTxn);
        knLogger.debug(methodName, "subsAddlTGList - ", subsAddlTGList);
        if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
            if ((contactDTO.getClientType() == SUBSCR_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() && contactDTO.getNewClientType() == HANDSET.value()) ||
                    (contactDTO.getClientType() == SUBSCR_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value() && contactDTO.getNewClientType() == CROSSCARRIER.value()) ||
                    (contactDTO.getClientType() == SUBSCR_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value() && contactDTO.getNewClientType() == POC_WIFIONLY.value())) {
                Collection<String> mdnList = new ArrayList<>();
                mdnList.add(mdn);
                groupInfoUtil.deleteSubsAddlTalkGroup(mdnList, xdmsHomePttId, persisterTxn);
                //groupInfoUtil.deleteSubsAddlTalkGroupDoc(mdnList, xdmsHomePttId, persisterTxn);
                //etagMap = commonInfoUtil.formMdnAddlTGListDeleteNotification(mdn, etagMap);
                etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHomePttId, mdnList, persisterTxn, etagMap);
                knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
                if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                    groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsAddlTGList, xdmsHomePttId, persisterTxn);
                }
                knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
                if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                    etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, null,
                            subsAddlTGList, groupCorpIdMap);
                    knLogger.debug(methodName, "etagMap AddlTG -diff - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                }
            }
        }
        etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, null);
        activationInfoUtil.deleteActivationCodeForMDN(mdn, xdmsHomePttId, persisterTxn);
        boolean tgscCleanUp = groupInfoUtil.cleanUpSubsCampedGrps(mdn, xdmsHomePttId, persisterTxn);
        if(tgscCleanUp){
            etagMap = commonInfoUtil.formMdnTGSCDeleteNotification(mdn, etagMap, xdmsHomePttId, persisterTxn);
        }
        respDTO.setChangeLogMap(etagMap);
        knLogger.debug(methodName, "etagMap - ", etagMap);
        return respDTO;
    }
}