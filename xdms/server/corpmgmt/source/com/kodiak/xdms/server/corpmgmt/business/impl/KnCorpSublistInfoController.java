/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpSublistInfoController.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 11, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.business.impl;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.dbfw.KnDbSyncFwConstants;
import com.kodiak.frameworks.dbfw.collectors.KnSqlJobCollector;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.idgenerator.KnIdGeneratorImpl;
import com.kodiak.utilities.idgenerator.dao.KnTableInfoBean;
import com.kodiak.utilities.lieventhandler.dto.KnLIEventDTO;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnExtProfileDetails;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpSublistInfoController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistSubscDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpAutoPairingResponse;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistDistributionRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSublistRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookIPDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMdnListPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnMdnDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnReverseContactPersistDto;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnActions;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.AUTO_ASSIGN_ZONE_POSITION;
import static com.kodiak.xdms.server.common.resources.KnConstants.LIBRARY_NAME_CORP_MGMT;
import static com.kodiak.xdms.server.common.resources.KnConstants.STATUS_FAILURE;
import static com.kodiak.xdms.server.common.resources.KnErrorCodes.Validator.ADDED_CONTACT_ALRREADY_EXIST;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populateXdmResponseFroomHook;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isNullOrEmpty;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isObjectNull;
import static com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes.CORP_SUBLIST_MANAGER;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.INVALID_SUBLISTS_SUBSCRIBER_CONTACT;
import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.DELETE_SUBLIST;
import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.MODIFY_SUBLIST;
import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.REMOVE_SUBSCRIBERS_ALL_SUBLIST;


public class KnCorpSublistInfoController implements ICorpSublistInfoController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpSublistInfoController.class);

    private static final String CLASS = KnCorpSublistInfoController.class.getName();

    private KnValidatorFramework validatorFW;
    private KnCorpContactInfoUtil contactInfoUtil;
    KnCorpCommonInfoUtil commonInfoUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
    private static final int DISABLED = 0;
    private KnCorpUserProfileUtil corpUserProfileUtil;
    private KnGenInfoUtil genInfoUtil;

    public KnCorpSublistInfoController() {
        contactInfoUtil = new KnCorpContactInfoUtil();
        commonInfoUtil = new KnCorpCommonInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
        corpUserProfileUtil = new KnCorpUserProfileUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
    }

    public KnCorpResponseDTO addToParingList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {

        String methodName = "addToParingList(contactDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactDTO);


        //contactDTO -> mdn, corpId
        //1. check if corp already has pairingListId in CorpInfo from corpProfile
        //2. If list does not exists create it - createSublist()
        //3. distribute the sublist to the new mdn
        //4. update etags and send notification to all sublist memebers

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {

            int corpId = contactDTO.getCorpId();
            String pairingMdn = contactDTO.getMdn();
            //Step1.
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE,
                    true, persisterTxn);
            knLogger.info(methodName, "Corporate Profile :- ", corpProfile);
            String xdmsHomePttId = corpProfile.getXdmsHome();
            List<String> mdnToAdd = new ArrayList<String>();
            Collection<KnCorpContactDTO> collCorpContDto = new ArrayList<KnCorpContactDTO>();
            //Step2.
            int pairedContactListId = corpProfile.getPairedContactListId();
            knLogger.info(methodName, "Corporate PairedContactListId :- ", pairedContactListId);
            KnCorpSublistDistributionRespDTO sublistDistDto = null;
            if (pairedContactListId <= 0) {
                knLogger.debug(methodName, "Paired Contact List Id not created for the corporation. ");
                KnIPCorpSublistInfoDTO sublistInfoDTO = new KnIPCorpSublistInfoDTO();
                sublistInfoDTO.setEntityId(CORP_SUBLIST_MANAGER);
                sublistInfoDTO.setOperationType(KnOperationTypes.CREATE_SUBLIST);
                sublistInfoDTO.setCorpId(corpId);
                sublistInfoDTO.setDistributionPolicy(KnConstants.DEFAULT_GROUP_DIST_POLICY);
                sublistInfoDTO.setSublistName(KnConstants.DEFAULT_CORP_PAIRING_LIST_NAME + corpId);

                mdnToAdd.add(pairingMdn);
                sublistInfoDTO.setAddedMdnList(mdnToAdd);
                sublistInfoDTO.setSublistType(KnConstants.SUBLIST_TYPE_SHARED_LIST);
                sublistInfoDTO.setHierarchyType(contactDTO.getHierarchyType());

                knLogger.debug(methodName, "xdmsHomePttId :- ", xdmsHomePttId);
                //createSublist
                knLogger.debug(methodName, "Before calling to create sublist, sublistInfoDTO :- ", sublistInfoDTO);
                KnCorpSublistRespDTO sublistResp = createSublist(sublistInfoDTO, persisterTxn);
                knLogger.info(methodName, "Sublist creation resp - ", sublistResp);
                int status = sublistResp.getStatus();
                if (STATUS_FAILURE == status) {
                    knLogger.error(methodName, "Sublist creation failed - ", status);
                    throw new KnCorpBOException(sublistResp.getStatusCode(), sublistResp.getMessage());
                }
                knLogger.debug(methodName, "After create sublist is called");
                pairedContactListId = sublistResp.getSublistDTO().getSublistId();
                sublistInfoUtil.updateCorpPairedContListId(corpId, pairedContactListId, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "After update Corporate Paired contactlist ID is called.");

            } else {
                knLogger.debug(methodName, "Paired Contact List Id already exist for the corporation. ",
                        pairedContactListId);

                //add MDN to the sublist
                Collection<KnCorpSubscriberDTO> collSubscriberDto = new ArrayList<KnCorpSubscriberDTO>();
                KnCorpSubscriberDTO subscriberDto = new KnCorpSubscriberDTO();
                subscriberDto.setCorpId(corpId);
                subscriberDto.setMdn(pairingMdn);
                collSubscriberDto.add(subscriberDto);
                knLogger.debug(methodName, "Before calling add member to sublist SubscriberDto :- ", collSubscriberDto,
                        ", pairedContactListId :- " + pairedContactListId);
                sublistInfoUtil.addSublistMembers(collSubscriberDto, pairedContactListId, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "After adding member to the list..");
                //update etags in resourcelist table
                //get distribution list for the sublist
                sublistDistDto = sublistInfoUtil.getDistributionList(pairedContactListId,
                        KnConstants.FILTER_TYPE_PAIRING_CONTACTS, corpProfile.getMaxContactsPerSubsc(),
                        corpProfile.getMaxMemPerCorpGroup(), corpId, xdmsHomePttId, false, persisterTxn);
                knLogger.debug(methodName, "Distribution list of the pairedContactList :- ", sublistDistDto);
                collCorpContDto = sublistDistDto.getContactList();
                // Collection<String> mdnToAdd = new ArrayList<String>();
                //mdnToAdd.add(pairingMdn);
                for (KnCorpContactDTO corpContDto : collCorpContDto) {
                    mdnToAdd.add(corpContDto.getMdn());
                }
                mdnToAdd.add(pairingMdn);
                knLogger.debug(methodName, "Final Distribution list of the pairedContactList :- ", sublistDistDto);

            }
            //distribute sublist to him
            KnCorpSubscriberDTO corpSubscDTO = new KnCorpSubscriberDTO();
            corpSubscDTO.setMdn(pairingMdn);
            Collection<Integer> distListId = new ArrayList<Integer>();
            distListId.add(pairedContactListId);
            knLogger.debug(methodName, "Before calling to pushSublistToSubscriber:: distListId, corpSubscDTO :- ", distListId, corpSubscDTO);
            contactInfoUtil.pushSublistToSubscriber(distListId, corpSubscDTO, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "After pushSublistToSubscriber is called");

            //added string list to set to hold the mdns present in the corporate since if dispatcher [present we need to set the is_dispatch_grp_member to 1.
            ArrayList<String> mdnList = new ArrayList<String>();

            //Insert into corpcontactlist table
            Map<String, Collection<KnCorpSubscriberDTO>> mdnContactListMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
            if (sublistDistDto != null) {
                Collection<KnCorpContactDTO> existingMembers = new ArrayList<KnCorpContactDTO>(collCorpContDto);
                knLogger.debug(methodName, "existingMembers", existingMembers);
                existingMembers.remove(new KnCorpContactDTO(pairingMdn));
                KnCorpSubscriberDTO subscriberDto = new KnCorpSubscriberDTO();
                subscriberDto.setMdn(pairingMdn);
                subscriberDto.setCorpId(corpId);
                subscriberDto.setName(contactDTO.getName());
                subscriberDto.setContact_type(KnConstants.INTERNAL_SUBSC_CLIENT_TYPE);
                subscriberDto.setClientType(contactDTO.getClientType());
                Collection<KnCorpSubscriberDTO> contactPairingMdn = new ArrayList<KnCorpSubscriberDTO>();
                contactPairingMdn.add(subscriberDto);

                Collection<KnCorpSubscriberDTO> contactExistingMdns = new ArrayList<KnCorpSubscriberDTO>();

                for (KnCorpContactDTO corpContactDTO : existingMembers) {
                    String mdn = corpContactDTO.getMdn();
                    mdnContactListMap.put(mdn, contactPairingMdn);
                    KnCorpSubscriberDTO subscriberExistingDto = new KnCorpSubscriberDTO();
                    subscriberExistingDto.setMdn(mdn);
                    subscriberExistingDto.setCorpId(corpId);
                    subscriberExistingDto.setName(corpContactDTO.getName());
                    contactExistingMdns.add(subscriberExistingDto);
                    mdnList.add(mdn);
                }
                mdnList.add(contactDTO.getMdn());
                knLogger.debug(methodName, "contactPairingMdn", contactPairingMdn);
                knLogger.debug(methodName, "contactExistingMdns", contactExistingMdns);
                mdnContactListMap.put(pairingMdn, contactExistingMdns);
                knLogger.debug(methodName, "Before calling to insertMembersIntoCorpContactList:: mdnContactListMap :-", KnGDPRTemplate.mapKeyMdn(mdnContactListMap));
                contactInfoUtil.insertMembersIntoCorpContactList(mdnContactListMap, xdmsHomePttId, persisterTxn);

            }

            //update etags in resourcelist table
            knLogger.debug(methodName, "Before calling to update etags:: mdnToAdd :-", KnGDPRTemplate.mdnList(mdnToAdd));
            Map<String, KnOPDirChgDTO> mapDirCngDto = contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId,
                    mdnToAdd, persisterTxn, null);
            knLogger.debug(methodName, "After update etags is called :: mapDirCngDto :- ", KnGDPRTemplate.mapKeyMdn(mapDirCngDto));

            //Update subscriber contact count.
            knLogger.debug(methodName, "Before calling update subcriber contact count for  sublistId :- ",
                    pairedContactListId);
            Collection<Integer> sublistIds = new ArrayList<Integer>();
            sublistIds.add(pairedContactListId);
            sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIds, corpProfile.getMaxContactsPerSubsc(),
                    MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
            knLogger.debug(methodName, "After update subscriber contact count is called.");
            mapDirCngDto = KnCorpCommonInfoUtil.formXcapDiffNotification(mapDirCngDto, mdnContactListMap, null, null, null, null, null, null, null, null, null, null);
            //removingh the newlyadded member
            mapDirCngDto.remove(corpSubscDTO.getMdn());
            respDTO.setChangeLogMap(mapDirCngDto);
            LinkedList<KnLIEventDTO> liEventList = KnCorpCommonInfoUtil.populateLIData(mapDirCngDto
                    , xdmsHomePttId, null, null, null,persisterTxn);
            respDTO.setLiEventList(liEventList);
            //check if any of the subscriber is a dispacther
            boolean isDispatchPresent = contactInfoUtil.isDispatchMemberPresent(mdnToAdd, xdmsHomePttId, persisterTxn);
            if (isDispatchPresent) {
                respDTO.setEnabledDispatchMemList(mdnList);
            }
            populate(respDTO);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while Corporate Pairing of contact. ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while Corporate Pairing of contact- ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        return respDTO;
    }

    /**
     * API to create sublist.
     * INPUT: List of MDNS [Including internal and external], Sublist Name.
     * Validation:
     * 1) Sublist name must be unique per corporate
     * 2) All sublist memeber[includes internal and external member] must be valid poc subscriber
     * 3) Max sublist per corporate
     * 4) Min member in sublist
     * 5) Max member in sublist
     * 6) Client type validation [allowed client type 0,1,2,5]
     * 7) Max member in sublist per request
     */
    public KnCorpSublistRespDTO createSublist(KnIPCorpSublistInfoDTO sublistInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "createSublist(sublistInfoDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", sublistInfoDTO);
        KnCorpSublistRespDTO respDTO = new KnCorpSublistRespDTO();
        try {
            int corpId = sublistInfoDTO.getCorpId();
            KnSublistDetailsPersistDTO valPersistDto = new KnSublistDetailsPersistDTO();
            knLogger.debug(methodName, "Fetching CorpProfile");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);
            String xdmsHomePttId = corpProfile.getXdmsHome();
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            //Invoking custom hook
            Map<String, Object> customParams = sublistInfoDTO.getCustomParamMap();
            if (sublistInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
            	//in case of update auto pairing we are not passing custom map. hence this check is required.
            	if (customParams != null) {
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                sublistInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.CREATE_SUBLIST);
                hookIPDTO.setData(sublistInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
             }
            }
            final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);


            /**
             * sublistName Hold the name of the sublist to create
             */
            String sublistName = sublistInfoDTO.getSublistName();
            knLogger.info(methodName, "Retrieving Sublist count by Name - ", sublistName);

            /**
             * Sublist Name must be unique per corporate, if isSublistExistInCorporate is true means sublist already exist.
             */
            boolean isSublistExistInCorporate = sublistInfoUtil.isSublistExistInCorporate(corpId, sublistName, xdmsHomePttId, persisterTxn);

            knLogger.info(methodName, "isSublistExistInCorporate=", isSublistExistInCorporate);

            /**
             * sublistMdns Hold the list of MDN to create sublist.
             */
            Collection<String> sublistMdns = sublistInfoDTO.getAddedMdnList();

            /**
             * validSublistMdns Hold the Valid POC Subscriber, i.e sublistMdns which exist in DG.POCSUBSCRINFO table
             */
            Collection<String> validSublistMdns = null;

            /**
             * validSublistMdnsDTO Hold the Valid POC Subscriber details
             */
            Collection<KnCorpSubscriberDTO> validSublistMdnsDTO = null;

            /**
             * notInternalContact Hold the MDNs which doesn't belongs to the current administrator corporate id
             */
            Collection<KnCorpSubscriberDTO> notInternalContact = null;

            /**
             * validExtCnt Hold the Valid external contact, i.e. notInternalContact which exist in DG.EXTCORPCONTACT table
             */
            Collection<KnCorpSubscriberDTO> validExtCnt = null;

            /**
             * Holds the NNI Contacts, NNI contact we can get from input mdnlist and filtering internal and external contact, [i.e nniCnt = mdnList - (internal + external contact)]
             */
            List<String> nniCntList = new ArrayList<String>();

            knLogger.debug(CLASS, methodName, "Added mdnList - ", KnGDPRTemplate.mdnList(sublistMdns));
            if (sublistMdns != null && !sublistMdns.isEmpty()) {
                KnCorpMdnListPersistDTO corpMdnListPersistDto = new KnCorpMdnListPersistDTO();
                corpMdnListPersistDto.setCorpId(corpId);
                corpMdnListPersistDto.setAddedMdnList(sublistMdns);
                KnMdnDetailsPersistDTO mdnDetailsPersistDTO = contactInfoUtil.getPoCSubscriberInfo(corpMdnListPersistDto, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "After getPoCSubscriberInfo result obtained PocSubscriberList- ", mdnDetailsPersistDTO);
                validSublistMdns = mdnDetailsPersistDTO.getMdnList();
                validSublistMdnsDTO = mdnDetailsPersistDTO.getAddedMdnDTO();
                notInternalContact = mdnDetailsPersistDTO.getExternalMdnList();

                // Below is logic to get NNI List from input mdnList
                // [i.e nniCnt = mdnList - (internal + external contact)]
                nniCntList.addAll(sublistMdns);
                nniCntList.removeAll(validSublistMdns);

                // Now NNI Contact also can be part of Sublist, Hence populate it to validSublistMdnsDTO
                /*for (String mdn : nniCntList) {
                    KnCorpSubscriberDTO nniSubsDto = new KnCorpSubscriberDTO();
					nniSubsDto.setMdn(mdn);
					validSublistMdnsDTO.add(nniSubsDto);
				}*/
                knLogger.debug(methodName, "inputExternalContacts - ", notInternalContact);
                if (notInternalContact != null && !notInternalContact.isEmpty()) {
                    sublistInfoDTO.setExternalContacts(notInternalContact);
                    KnMdnDetailsPersistDTO mdnDetailsPersistDTOExt = contactInfoUtil.getExternalConatctsInfo(corpId, notInternalContact, xdmsHomePttId,
                            persisterTxn);
                    knLogger.debug(methodName, "Valid External contacts for the Corporate - ", mdnDetailsPersistDTOExt);
                    validExtCnt = mdnDetailsPersistDTOExt.getExternalMdnList();
                }
            }

            List<Integer> profileIdList = contactInfoUtil.getExtSubsrProfilelist(nniCntList, xdmsHomePttId, persisterTxn);
            //If NNI subscr count is less then the notInternalContact count, request MDN consist of kodiak external subscribers also.
            if ((notInternalContact != null) && (notInternalContact.size() > nniCntList.size())) {
                profileIdList.add(KnConstants.PROFILE_ID_KODIAK_EXTERNAL_CONTACT);
            }
            Map<Integer, KnExtProfileDetails> extProfileDetailsMap = commonInfoUtil.getExtProfileDetails(profileIdList, xdmsHomePttId, persisterTxn);
            valPersistDto.setConfiguredProfileMap(extProfileDetailsMap);
            valPersistDto.setInputProfileIdList(profileIdList);
            knLogger.debug(CLASS, methodName, "sublistMdns=", KnGDPRTemplate.mdnList(sublistMdns), "validSublistMdns=", KnGDPRTemplate.mdnList(validSublistMdns), "validSublistMdnsDTO=", validSublistMdnsDTO, "notInternalContact=", notInternalContact, "validExtCnt", validExtCnt);
            int corpSublistCount = sublistInfoUtil.getCorpSublistCount(corpId, 1, xdmsHomePttId, false, persisterTxn,sublistInfoDTO.getHierarchyId());
            if (sublistMdns != null) {
                int memberCount = sublistMdns.size();
                valPersistDto.setTotalSublistsMembersCount(memberCount);
                valPersistDto.setContactCountInRequest(memberCount);
            }

            int maxMemPerCorpList = corpProfile.getMaxMemPerCorpList();
            //For Sublist with ListDistributionType=5
            boolean isNonAutoPair = sublistInfoDTO.getUserProfileListType() != null && sublistInfoDTO.getUserProfileListType() == ENABLED;
            boolean isUpmSublist= (isNonAutoPair && sublistInfoDTO.getListDistribution() == null);
            if(isUpmSublist){
                final String sysSublistSize = microServicesParamNameValueMap.get(MAXMEM_NONAP_SUBLIST);
                maxMemPerCorpList = sysSublistSize == null ? 0 : Integer.parseInt(sysSublistSize);
            }

            valPersistDto.setInputDTO(sublistInfoDTO);
            valPersistDto.setAddedMdnDTOList(validSublistMdnsDTO);
            valPersistDto.setPocMdnList(validSublistMdns);
            valPersistDto.setCorpSublistCount(corpSublistCount);
            valPersistDto.setSublistExistInCorporate(isSublistExistInCorporate);
            valPersistDto.setCorpExtContacts(validExtCnt);
            valPersistDto.setMaxCorpList(corpProfile.getMaxCorpLists());
            valPersistDto.setMaxCorpListMemberCount(maxMemPerCorpList);
            valPersistDto.setMaxSubscribersContactLimit(corpProfile.getMaxContactsPerSubsc());
            valPersistDto.setMaxAllowedContactCountPerRequest(corpProfile.getMaxContactsPerRequest());
            valPersistDto.setSublistName(sublistName);
            //common sublist modification allowed if system and corp flag is enabled

            boolean listDistribution = sublistInfoDTO.getListDistribution() != null && sublistInfoDTO.getListDistribution() == ENABLED;
            boolean isCommonContactList = (isNonAutoPair && listDistribution);
            final String systemCmnContactListSupport = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SUPPORT )==null
                    ?"0":microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SUPPORT );
            final Integer corpCommonContactListSupport=corpProfile.getCommonContactListSupport();
            int commonContactListsupport = corpCommonContactListSupport == null ? Integer.parseInt(systemCmnContactListSupport) : corpCommonContactListSupport;
            valPersistDto.setNonAutoPair(isNonAutoPair);
            valPersistDto.setListDistribution(listDistribution);
            valPersistDto.setCommonContactList(isCommonContactList);
            valPersistDto.setCommContactListSupp(commonContactListsupport==ENABLED);

            //common sublist size allowed
            final String systemCmnContactListSize = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SIZE);
            final int cmnContactListSize = systemCmnContactListSize == null ? 0 : Integer.parseInt(systemCmnContactListSize);
            valPersistDto.setCmnContactListSize(cmnContactListSize);
            int totalSublistMemCount=sublistMdns.size();
            valPersistDto.setTotalCommonContactListMemCount(totalSublistMemCount);


            knLogger.debug(methodName, "Invoking Validation FW - ", valPersistDto);
            validatorFW.validate(valPersistDto);
            knLogger.debug(methodName, "Validation completed Successfully");
            KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
            int sublistId = sublistInfoDTO.getSublistId();
            if (sublistInfoDTO.getSublistId() <= 0) {
                sublistId = genInfoUtil.retrieveIdForTable(KnConstants.CORP_LIST_TABLE, xdmsHomePttId, KnConstants.CORP_LIST_TABLE_PK, false, KnConstants.DUAL_DATA_STORE);
            }
            KnCorpSublistDTO sublistDTO = new KnCorpSublistDTO();
            sublistDTO.setCorpId(corpId);
            sublistDTO.setSublistId(sublistId);
            sublistDTO.setSublistName(sublistInfoDTO.getSublistName());
            int distPolicy = sublistInfoDTO.getDistributionPolicy();
            if (distPolicy != -1) {
                sublistDTO.setDistributionPolicy(distPolicy);
            } else {
                sublistDTO.setDistributionPolicy(KnConstants.DIST_POLICY_SHARED_ANY_LIST);
            }
            knLogger.debug(methodName, "--->sublistInfoDTO.getUserProfileListType() -",sublistInfoDTO.getUserProfileListType());
            if(sublistInfoDTO.getUserProfileListType()!= null && sublistInfoDTO.getUserProfileListType() == ENABLED) {
                sublistDTO.setDistributionPolicy(DIST_POLICY_USER_PROFILE);
                sublistInfoDTO.setDistribution(false);
            }
            sublistDTO.setSublistType(KnConstants.SUBLIST_TYPE_SHARED_LIST);
            if(isCommonContactList)
            {
                sublistInfoDTO.setDistribution(false);
                sublistDTO.setSublistType(KnConstants.SUBLIST_TYPE_SHARED_LIST);
                sublistDTO.setDistributionPolicy(DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT);
            }
            sublistDTO.setETag(KnConstants.INITIAL_ETAG);
            sublistDTO.setHierarchyId(sublistInfoDTO.getHierarchyId());
            sublistInfoUtil.createSubListDetails(validSublistMdnsDTO, sublistDTO, xdmsHomePttId, persisterTxn);

            if (sublistInfoDTO.isDistribution()) {
                Map<String, Collection<KnCorpSubscriberDTO>> finalMissingContacts = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
                Map<String, Collection<KnCorpSubscriberDTO>> subscrNewAddedContMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
                Collection<String> internalMembers = new ArrayList<String>();
                Collection<KnCorpSubscriberDTO> internalMembersDTO = new ArrayList<KnCorpSubscriberDTO>();
                knLogger.debug(methodName, "validSublistMdnsDTO -",validSublistMdnsDTO);
                if (validSublistMdnsDTO != null && !validSublistMdnsDTO.isEmpty()) {
                    for (KnCorpSubscriberDTO subsc : validSublistMdnsDTO) {
                        subsc.setUa(KnGeneralUtil.getUA(subsc.getUserAgent(), subsc.getClientPVmajorVer()));
                        if (subsc.getCorpId() == corpId) {
                            internalMembers.add(subsc.getMdn());
                            internalMembersDTO.add(subsc);
                            subscrNewAddedContMap.put(subsc.getMdn(), validSublistMdnsDTO);
                        }
                    }
                }
                Map<String, Collection<String>> subsContactMap = contactInfoUtil.getSubscribersContactList(internalMembers, xdmsHomePttId, persisterTxn);
                if (subsContactMap != null && !subsContactMap.isEmpty()) {
                    finalMissingContacts = commonInfoUtil.filterMemberToBeAddedToSubscriber(subsContactMap, subscrNewAddedContMap);
                } else {
                    finalMissingContacts = subscrNewAddedContMap;
                }
                KnIPCorpSublistSubscDistDTO distDTO = new KnIPCorpSublistSubscDistDTO();
                distDTO.setMdnList(internalMembers);
                Collection<Integer> sublistIdList = new ArrayList<Integer>();
                sublistIdList.add(sublistDTO.getSublistId());
                distDTO.setSublistIds(sublistIdList);
                knLogger.info(methodName, "Push Sublist for the subscribers as the data is proper. ");
                contactInfoUtil.pushSublistListToSubscriberList(distDTO, xdmsHomePttId, persisterTxn);
                if (finalMissingContacts != null && !finalMissingContacts.isEmpty()) {
                    contactInfoUtil.insertMembersIntoCorpContactList(finalMissingContacts, xdmsHomePttId, persisterTxn);
                }
                Collection<Integer> sublistIds = new ArrayList<Integer>();
                sublistIds.add(sublistId);
                sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIds, corpProfile.getMaxContactsPerSubsc(), corpProfile.getMaxMemPerCorpGroup(),
                        corpProfile.getMaxMembersPerDispatchGroup(), xdmsHomePttId, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
                Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId, internalMembers, internalMembersDTO, persisterTxn, null);
                etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, finalMissingContacts, null, null, null, null, null, null, null, null, null, null);
                respDTO.setChangeLogMap(etagMap);
                LinkedList<KnLIEventDTO> liEventList = KnCorpCommonInfoUtil.populateLIData(etagMap
                        , xdmsHomePttId, null, null, null,persisterTxn);
                respDTO.setLiEventList(liEventList);
            }
            KnCorpSublistDTO sublistRespInfoDTO = new KnCorpSublistDTO();
            sublistRespInfoDTO.setSublistId(sublistId);
            sublistRespInfoDTO.setSublistName(sublistName);
            sublistRespInfoDTO.setSublistType(KnConstants.SHARED_LIST_TYPE);
            if (null != validSublistMdnsDTO) {
                sublistRespInfoDTO.setMemberCount(validSublistMdnsDTO.size());
            }
            respDTO.setSublistDTO(sublistRespInfoDTO);
            respDTO.setEtag(String.valueOf(KnConstants.INITIAL_ETAG));
            respDTO.setMdnCorpId(corpId);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while creating sub list - ", e);
            populate(respDTO, e);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while creating sub list - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while createSublist- ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }

    public KnCorpResponseDTO modifySublist(KnIPCorpSublistInfoDTO sublistInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "modifySublist(sublistInfoDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", sublistInfoDTO);

        long etag;
        //Step:
        //creating Response DTO object
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpInOutParamDTO corpInOutParamDTO = new KnCorpInOutParamDTO();
        String xdmsHomePttId = "";
        try {
            int corpId = sublistInfoDTO.getCorpId();

            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile =
                    commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            //use the xdms home pttServerId from Corp profile
            xdmsHomePttId = corpProfile.getXdmsHome();

            // Collection<Integer> addedSublistIds = sublistInfoDTO.getAddedSublistIds();
            Collection<String> addedMdnList = sublistInfoDTO.getAddedMdnList();
            LinkedList<String> removedMdnList = sublistInfoDTO.getRemovedMdnList();

            Collection<KnCorpSubscriberDTO> sublistMember = contactInfoUtil.getSubscPrivateMemberList(sublistInfoDTO.getSublistId(),
                    xdmsHomePttId, persisterTxn);
            KnMdnDetailsPersistDTO externalPocSubsPersistDto = new KnMdnDetailsPersistDTO();
            Map<String, KnCorpSubscriberDTO> extContactMap = sublistInfoUtil.getCorpExternalSubscriber(corpId, xdmsHomePttId, persisterTxn);
            Collection<String> corpExtContacts = extContactMap.keySet();
            Collection<KnCorpSubscriberDTO> subscribersInfo =
                    contactInfoUtil.getSubscribersInfo(addedMdnList, xdmsHomePttId, persisterTxn);

            int sublistId = sublistInfoDTO.getSublistId();
            knLogger.debug(methodName, "Before Getting the subList basic Details");
            KnCorpSublistDTO sublistDetails = sublistInfoUtil.getSublistInfo(sublistId,
                    sublistInfoDTO.getCorpId(), xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "After Getting the subList basic Details sublistDetails:",sublistDetails);

            int distPolicy = sublistDetails.getDistributionPolicy();
            boolean autoPairingOffSublist = (distPolicy == DIST_POLICY_USER_PROFILE);
            boolean isCommonContactList = (distPolicy == DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT);

            boolean modifyName = false;
            etag = sublistDetails.getETag();
            /* Collection<KnCorpSubscriberDTO> addedMdnDTOList = contactInfoUtil.getPoCSubscriberInfo(addedMdnList,
           xdmsHomePttId, persisterTxn);*/
            Collection<KnCorpSubscriberDTO> addedMdnDTOList = new ArrayList<KnCorpSubscriberDTO>();
            Collection<KnCorpSubscriberDTO> externalContacts = new ArrayList<KnCorpSubscriberDTO>();
            //to hold the current internal members which will be there after the modification
            Collection<KnCorpSubscriberDTO> intExitingMembers = new ArrayList<KnCorpSubscriberDTO>();
            //to hold the new internal members which will be there after the modification
            Collection<KnCorpSubscriberDTO> intNewMembers = new ArrayList<KnCorpSubscriberDTO>();
            //this is to hold the NNI MDNs from the added mdn list
            List<String> nniMdnList = new ArrayList<>();
            //This is to indicate the modifying sublist distributed to a dispatch group
            boolean isPushedDispatchGrp = false;
            //This is to indicate the modifying sublist is distributed to a group.
            boolean isSublistInGroup = false;
            Collection<String> validPocSubs = new ArrayList<String>(subscribersInfo.size());
            List<String> mdnsAdded = new ArrayList<>();
            List<String> totalMdn = new ArrayList<>();
            totalMdn.addAll(addedMdnList);
            for(KnCorpSubscriberDTO MDN : sublistMember){
                totalMdn.add(MDN.getMdn());
            }
            Map<String, KnCorpSubsEntitiesDTO> subsEntitiesDTOMap = contactInfoUtil.getSubsEntitiesDetails(totalMdn, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "subsEntitiesDTOMap: ", KnGDPRTemplate.mapKeyMdn(subsEntitiesDTOMap));
            List<String> criClientList = new ArrayList<>(); //Variable to hold the CRI clients from added member list.
            for (KnCorpSubscriberDTO subsc : subscribersInfo) {
                if(subsc.getMcpttCompliance() == KnConstants.MCPTT_COMPLIANCE){
                    criClientList.add(subsc.getMdn());
                }
                addedMdnDTOList.add(subsc);
                validPocSubs.add(subsc.getMdn());
                if (subsc.getCorpId() != corpId) {
                    if (extContactMap != null && !extContactMap.isEmpty() && extContactMap.get(subsc.getMdn()) != null) {
                        subsc.setName(extContactMap.get(subsc.getMdn()).getName());
                        if(subsc.getContact_type() == 0){
                            subsc.setContact_type(KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT);
                        }
                    }
                    externalContacts.add(subsc);
                    mdnsAdded.add(subsc.getMdn());
                    if (KnConstants.CONTACT_TYPE_EXTERNAL_SUBSCRIBER == subsc.getContact_type()) {
                        nniMdnList.add(subsc.getMdn());
                    }

                } else {
                    intNewMembers.add(subsc);
                }
                if(subsEntitiesDTOMap.containsKey(subsc.getMdn())){
                    subsc.setAliasMdn(subsEntitiesDTOMap.get(subsc.getMdn()).getAliasMdn());
                    subsc.setUserId(subsEntitiesDTOMap.get(subsc.getMdn()).getUserId());
                    subsc.setSubsActiveFS2(subsEntitiesDTOMap.get(subsc.getMdn()).getActiveFs2());
                }
                subsc.setUa(KnGeneralUtil.getUA(subsc.getUserAgent(), subsc.getClientPVmajorVer()));
                // }
            }
            //Get the profile ID for NNI subscribers.
            List<Integer> profileIdList = contactInfoUtil.getExtSubsrProfilelist(nniMdnList, xdmsHomePttId, persisterTxn);
            //If NNI subscr count is less then the notInternalContact count, request MDN consist of kodiak external subscribers also.
            if (nniMdnList.size() < externalContacts.size()) {
                profileIdList.add(KnConstants.PROFILE_ID_KODIAK_EXTERNAL_CONTACT);
            }
            Map<Integer, KnExtProfileDetails> extProfileDetailsMap = commonInfoUtil.getExtProfileDetails(profileIdList, xdmsHomePttId, persisterTxn);

            Collection<KnCorpSubscriberDTO> validaExtMdn = new ArrayList<KnCorpSubscriberDTO>();
            for (KnCorpSubscriberDTO subsc : externalContacts) {
                if (corpExtContacts.contains(subsc.getMdn())) {
                    validaExtMdn.add(subsc);
                }
            }
            externalPocSubsPersistDto.setExternalMdnList(validaExtMdn);

            Collection<String> subscPrsntForSublist = new ArrayList<String>();
            Collection<KnCorpSubscriberDTO> sublistMemAftrrRemvlMem = new ArrayList<KnCorpSubscriberDTO>();
            LinkedList<String> intMemInDelMembersStr = new LinkedList<String>();
            Collection<KnCorpSubscriberDTO> intMemInDelMembers = new ArrayList<KnCorpSubscriberDTO>();
            LinkedList<String> sublistMemberStrList = new LinkedList<String>();
            Map<String, KnCorpSubscriberDTO> subscNameMap = contactInfoUtil.getPocSubscribersDetails(sublistMember, corpProfile.getXdmsHome(), persisterTxn);
            knLogger.debug(methodName, "subscNameMap", KnGDPRTemplate.mapKeyMdn(subscNameMap));
            for (KnCorpSubscriberDTO subsc : sublistMember) {
                if (removedMdnList.contains(subsc.getMdn())) {
                    subscPrsntForSublist.add(subsc.getMdn());
                    if (subsc.getCorpId() == corpId) {
                        intMemInDelMembers.add(subsc);
                        intMemInDelMembersStr.add(subsc.getMdn());
                    }
                }
                if (!subscPrsntForSublist.contains(subsc.getMdn())) {
                    if (subsc.getCorpId() == corpId) {
                        subsc.setName(subscNameMap.get(subsc.getMdn()).getName());
                        subsc.setClientType(subscNameMap.get(subsc.getMdn()).getClientType());
                        subsc.setUa(KnGeneralUtil.getUA(subscNameMap.get(subsc.getMdn()).getUserAgent(), subscNameMap.get(subsc.getMdn()).getClientPVmajorVer()));
                        intExitingMembers.add(subsc);
                    } else {
                        if (extContactMap != null && !extContactMap.isEmpty() && extContactMap.get(subsc.getMdn()) != null) {
                            subsc.setName(extContactMap.get(subsc.getMdn()).getName());
                        }
                        if (subsc.getCorpId() > 0) {
                            subsc.setClientType(subscNameMap.get(subsc.getMdn()).getClientType());
                            subsc.setContact_type(KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT);
                        }
                        mdnsAdded.add(subsc.getMdn());
                    }
                    sublistMemAftrrRemvlMem.add(subsc);
                }
                sublistMemberStrList.add(subsc.getMdn());

                if(subsEntitiesDTOMap.containsKey(subsc.getMdn())){
                    subsc.setAliasMdn(subsEntitiesDTOMap.get(subsc.getMdn()).getAliasMdn());
                    subsc.setUserId(subsEntitiesDTOMap.get(subsc.getMdn()).getUserId());
                    subsc.setSubsActiveFS2(subsEntitiesDTOMap.get(subsc.getMdn()).getActiveFs2());
                }
            }
            int dbSubListMemberCount = sublistInfoUtil.getSubListMemberCount(sublistId
                    , xdmsHomePttId, persisterTxn);
            KnSublistDetailsPersistDTO sublistDetailsDTO = new KnSublistDetailsPersistDTO();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = sublistInfoDTO.getCustomParamMap();
            boolean hiearchyFlag = (sublistInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY);
            if (hiearchyFlag) {
            	//in case of update auto pairing we are not passing custom map. hence this check is required.
            	if (customParams != null) {
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                customParams.put(com.kodiak.common.resources.KnConstants.SUBLIST_MEMBERS, sublistMemberStrList);
                customParams.put(com.kodiak.common.resources.KnConstants.ETXERNAL_MEMBERS, corpExtContacts);
                customParams.put(com.kodiak.common.resources.KnConstants.SUBLIST_ID, String.valueOf(sublistInfoDTO.getSublistId()));
                    List<Integer> sublistIdList = new ArrayList<Integer>();
                    sublistIdList.add(sublistInfoDTO.getSublistId());
                    KnCorpSublistInfoUtil knCorpSublistInfoUtil = new KnCorpSublistInfoUtil();
                    knCorpSublistInfoUtil.validateSublistParams(sublistIdList,customParams, xdmsHomePttId, persisterTxn);
                sublistInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.MODIFY_SUBLIST);
                hookIPDTO.setData(sublistInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
              }

            	if(autoPairingOffSublist){
                    int minimumSubListMemberCount=0;

                    minimumSubListMemberCount=minimumSubListMemberCount+dbSubListMemberCount;

                    if(addedMdnList!=null&&!addedMdnList.isEmpty()){
                        minimumSubListMemberCount=minimumSubListMemberCount+addedMdnList.size();
                    }
            	    if(removedMdnList!=null&&!removedMdnList.isEmpty()){
                        minimumSubListMemberCount=minimumSubListMemberCount-removedMdnList.size();
                    }
                    if(minimumSubListMemberCount<1){
                        Map<String, String> upmIdAndName = corpUserProfileUtil.getUserProfileIdNameBySublistId(corpId, sublistId
                                , xdmsHomePttId, persisterTxn);
                        sublistDetailsDTO.setLastMemberSublistUPMMap(upmIdAndName);
                    }
                }
            }

            if (!isNullOrEmpty(sublistInfoDTO.getSublistName()) && (!isNullOrEmpty(sublistDetails.getSublistName()))
                    && !sublistDetails.getSublistName().trim().equals(sublistInfoDTO.getSublistName())) {
                knLogger.debug(methodName, "Modify Name indicator set to true");
                modifyName = true;
            }

            int sublistCount = 0;
            if (!isNullOrEmpty(sublistInfoDTO.getSublistName())) {
                sublistCount = sublistInfoUtil.getSublistCountByNameExcludingCurrentSublist(corpId,
                        sublistInfoDTO.getSublistName(), sublistId, xdmsHomePttId, persisterTxn);
            }
            /*knLogger.debug( methodName, "Before getPoCSublistIdInfo.");
            KnSublistDetailsPersistDTO pocSublistIds = sublistInfoUtil.getPoCSublistIdInfo(addedSublistIds,
                    corpId, xdmsHomePttId, persisterTxn);
            knLogger.debug( methodName, "After getPoCSublistIdInfo.");
            knLogger.debug( methodName, "Before getPoCSubscriberInfo.");*/


            knLogger.debug(methodName, "Before getExternalConatctsInfo for input externalContacts.");
            sublistInfoDTO.setExternalContacts(externalContacts);

            //todo comment
            //Step:
            //
            /* KnMdnDetailsPersistDTO externalPocSubsPersistDto = contactInfoUtil.getExternalConatctsInfo(corpId,
          externalContacts, xdmsHomePttId, persisterTxn);*/
            knLogger.debug(methodName, "After getExternalConatctsInfo");


            KnMdnDetailsPersistDTO removalSubsDTO = new KnMdnDetailsPersistDTO();
            removalSubsDTO.setMdnList(subscPrsntForSublist);
            Collection<KnCorpSubscriberDTO> membersToBeAddedToSublist = KnCorpCommonInfoUtil.filterOutTheNewlyAddedMebers(
                    sublistMemAftrrRemvlMem, subscribersInfo, externalPocSubsPersistDto.getExternalMdnList());
            /* Collection<String> finalSublistMembers = KnCorpCommonInfoUtil.filterFinalPrivateListMembers(
          sublistMember, membersToBeAddedToSublist, removalSubsDTO);*/

            // Variable to hold the internal members to be added into sublist.
            Collection<String> intMemToBeAdded = new ArrayList<String>();
            for (KnCorpSubscriberDTO corpSubscriberDTO : membersToBeAddedToSublist) {
                if (corpSubscriberDTO.getCorpId() == corpId) {
                    intMemToBeAdded.add(corpSubscriberDTO.getMdn());
                }
            }

            Map<Integer, String> groupNameMap = new HashMap<Integer, String>();
            // Get the group list where this sublist is distributed.
            Collection<KnCorpGroupInfoPersistDTO> groupDTOLst = sublistInfoUtil.getGroupsMappedToSublist(sublistId, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "groupDTOLst  - ", groupDTOLst);
            Collection<Integer> groupIdList = new ArrayList<Integer>();
            if (groupDTOLst != null && !groupDTOLst.isEmpty()) {
                // Get the list of group ids where mdns exist
                Map<String, Collection<Integer>> subsGroupListMap = groupInfoUtil.getGroupListForSubs(intMemToBeAdded, xdmsHomePttId,false, persisterTxn);
                knLogger.debug(methodName, "subsGroupListMap  - ", KnGDPRTemplate.mapKeyMdn(subsGroupListMap));
                // Get the group member list for group ids  in  groupDTOLst.
                for (KnCorpGroupInfoPersistDTO grpInfoDto : groupDTOLst) {
                    groupNameMap.put(grpInfoDto.getGroupId(), corpId + "_" + grpInfoDto.getGroupDisplayName());
                    groupIdList.add(grpInfoDto.getGroupId());
                    isSublistInGroup = true;
                    if (KnConstants.GROUP_DISPATCHER == grpInfoDto.getGroupType()) {
                        isPushedDispatchGrp = true;
                    }
                }

                // Variable to hold subscribers and group count.
                Map<String, Integer> subsGroupCountMap = new HashMap<String, Integer>();
                for (String mdn : intMemToBeAdded) {
                    // groupListForSubs variable holds all group ids where this mdn will be part of after modification.
                    Set<Integer> groupListForSubs = new HashSet<Integer>(groupIdList);
                    Collection<Integer> subsMappedToGrpList = subsGroupListMap.get(mdn);
                    if (subsMappedToGrpList != null) {
                        groupListForSubs.addAll(subsMappedToGrpList);
                    }
                    subsGroupCountMap.put(mdn, groupListForSubs.size());
                }
                knLogger.debug(methodName, "subsGroupCountMap ", KnGDPRTemplate.mapKeyMdn(subsGroupCountMap));
                //Setting these values if sublist is mapped to any group
                sublistDetailsDTO.setSubsGroupCountMap(subsGroupCountMap);
                sublistDetailsDTO.setMaxGroupsPerMemberCount(corpProfile.getMaxGroupsPerSubsc());
            }

            LinkedList<String> interMemList = new LinkedList<String>();
            if (sublistInfoDTO.isDistribution()) {
                //calculate if the contact count will exceed
                for (KnCorpSubscriberDTO subsc : subscribersInfo) {
                    if (subsc.getCorpId() == corpId) {
                        interMemList.add(subsc.getMdn());
                    }
                }
            }

            int count = membersToBeAddedToSublist.size() + sublistMemAftrrRemvlMem.size();

            knLogger.debug(methodName, "Total final contact count  - ", count);
            int deletedMembersCount = 0;
            if (removedMdnList != null && !removedMdnList.isEmpty()) {
                deletedMembersCount = removedMdnList.size();
            }

            sublistDetailsDTO.setTotalSublistsMembersCount(count);
            if ((addedMdnList == null || addedMdnList.isEmpty())) {//&&
                //(addedSublistIds == null || addedSublistIds.isEmpty())) {
                sublistDetailsDTO.setTotalSublistsMembersCount(corpProfile.getMaxMemPerCorpList());
            }
            //Get the distribution list for the Sublist.
            Collection<String> mappedMdnList = sublistInfoUtil.getSubscribersDistToSublist(sublistId, xdmsHomePttId, persisterTxn);
            knLogger.debug("mappedMdnList of the sublist from the DB are-,", KnGDPRTemplate.mdnList(mappedMdnList));
            if (mappedMdnList.size() > 0) {
                sublistDetailsDTO.setSublistDistributed(true);
            }
            List<String> ReceipentProfileMdns=new ArrayList<>();

            int maxMemPerCorpList = corpProfile.getMaxMemPerCorpList();
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> paramNameValueMapCommon = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);

            if(distPolicy == KnConstants.DIST_POLICY_USER_PROFILE){
                final String sysSublistSize = paramNameValueMapCommon.get(MAXMEM_NONAP_SUBLIST);
                maxMemPerCorpList = sysSublistSize == null ? 0 : Integer.parseInt(sysSublistSize);
            }

            //sublistDetailsDTO.setSublistIds(pocSublistIds.getSublistIds());
            sublistDetailsDTO.setPocMdnList(validPocSubs);
            sublistDetailsDTO.setInputDTO(sublistInfoDTO);
            sublistDetailsDTO.setCurrentEtag(sublistDetails.getETag());
            sublistDetailsDTO.setMaxCorpListMemberCount(maxMemPerCorpList);
            sublistDetailsDTO.setSublistCountByName(sublistCount);
            sublistDetailsDTO.setMaxCorpList(corpProfile.getMaxCorpLists());
            sublistDetailsDTO.setCorpExtContacts(externalPocSubsPersistDto.getExternalMdnList());
            sublistDetailsDTO.setDbSublistMembers(sublistMember);
            sublistDetailsDTO.setSublistType(sublistDetails.getSublistType());
            sublistDetailsDTO.setMaxSubscribersContactLimit(corpProfile.getMaxContactsPerSubsc());
            sublistDetailsDTO.setMaxAllowedContactCountPerRequest(corpProfile.getMaxContactsPerRequest());
            sublistDetailsDTO.setContactCountInRequest(subscribersInfo.size() + deletedMembersCount);
            sublistDetailsDTO.setAddedMdnDTOList(addedMdnDTOList);
            sublistDetailsDTO.setConfiguredProfileMap(extProfileDetailsMap);
            sublistDetailsDTO.setSublstInDispGrp(isPushedDispatchGrp);
            sublistDetailsDTO.setSublistInGroup(isSublistInGroup);
            sublistDetailsDTO.setSublistId(sublistId);
            sublistDetailsDTO.setSublistName(sublistInfoDTO.getSublistName());
            if(distPolicy==KnConstants.DIST_POLICY_USER_PROFILE){
                sublistDetailsDTO.setDistributionPolicy(distPolicy);
                //if distribution policy is 5, then forcefully making distibution flag as false
                sublistDetailsDTO.setDistribution(false);
                sublistInfoDTO.setDistribution(false);
            }
            String maxGroupsPerCRIClient = paramNameValueMapCommon.get(KnConstants.MAX_GROUPS_PER_CRI_CLIENT);
            if(maxGroupsPerCRIClient != null){
                sublistDetailsDTO.setMaxGroupsPerCRIClient(Integer.parseInt(maxGroupsPerCRIClient));
            }
            sublistDetailsDTO.setCriClientList(criClientList);

            //common sublist modification allowed if system and corp flag is enabled
            final String systemCmnContactListSupport = paramNameValueMapCommon.get(COMMON_CONTACTLIST_SUPPORT )==null
                    ?"0":paramNameValueMapCommon.get(COMMON_CONTACTLIST_SUPPORT );
            final Integer corpCommonContactListSupport=corpProfile.getCommonContactListSupport();
            int commonContactListsupport = corpCommonContactListSupport == null ? Integer.parseInt(systemCmnContactListSupport) : corpCommonContactListSupport;
            sublistDetailsDTO.setCommonContactList(isCommonContactList);
            sublistDetailsDTO.setCommContactListSupp(commonContactListsupport==ENABLED);

            //common sublist size allowed
            final String systemCmnContactListSize = paramNameValueMapCommon.get(COMMON_CONTACTLIST_SIZE);
            final int cmnContactListSize = systemCmnContactListSize == null ? 0 : Integer.parseInt(systemCmnContactListSize);
            sublistDetailsDTO.setCmnContactListSize(cmnContactListSize);
            int totalSublistMemCount=dbSubListMemberCount;
            if(addedMdnList!=null){
                totalSublistMemCount=totalSublistMemCount+addedMdnList.size();
            }
            if(removedMdnList!=null){
                totalSublistMemCount=totalSublistMemCount-removedMdnList.size();
            }
            sublistDetailsDTO.setTotalCommonContactListMemCount(totalSublistMemCount);

            knLogger.debug(methodName, "Invoking Validation FW - ", sublistDetailsDTO);
            validatorFW.validate(sublistDetailsDTO);
            knLogger.debug(methodName, "Validation completed Successfully");

            if (count == 0) {
                knLogger.debug(methodName, "Sublist will get deleted as the sublist member Count is becoming zero");
                KnIPCorpSublistDTO sublistDTO = new KnIPCorpSublistDTO();
                sublistDTO.setCorpId(corpId);
                sublistDTO.setSublistId(sublistId);
                respDTO = deleteSublistOperation(sublistDTO, xdmsHomePttId, corpInOutParamDTO, corpProfile.getLinkedGwKey(), persisterTxn, corpProfile);
                respDTO.setTgsModeChgMap(corpInOutParamDTO.getTgsModeChgMap());
                populate(respDTO);
                return respDTO;
            }

            Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
            Collection<String> disabledLocWatcher = new ArrayList<>();
            Collection<String> enabledLocWatcher = new ArrayList<>();
            boolean isGroupExists = false;
            Map<Integer, Collection<String>> removedLocWatcherMap = new HashMap<>();
            if (groupDTOLst != null && !groupDTOLst.isEmpty()) {
                isGroupExists = true;
                // Preparing the Collection<String> for contact assignement and ODL Bit.
                if (removedMdnList != null && !removedMdnList.isEmpty()) {
                    Collection<KnCorpGroupMemberDTO> grpMemberDetails = groupInfoUtil.getCorpGroupMembersList(groupIdList, xdmsHomePttId, persisterTxn);
                    Collection<String> totalMemMdn = new ArrayList<>();
                    if (!grpMemberDetails.isEmpty()) {
                        for (KnCorpGroupMemberDTO groupMemberDTO : grpMemberDetails) {
                            totalMemMdn.add(groupMemberDTO.getMdn());
                            if(groupMemberDTO.getLocWatcher() == ENABLED){
                                Collection<String> removedLocWatchers = null;
                                if(removedLocWatcherMap.get(groupMemberDTO.getGroupId()) != null){
                                    removedLocWatchers = removedLocWatcherMap.get(groupMemberDTO.getGroupId());
                                    removedLocWatchers.add(groupMemberDTO.getMdn());
                                } else {
                                    removedLocWatchers = new ArrayList<>();
                                    removedLocWatchers.add(groupMemberDTO.getMdn());
                                    removedLocWatcherMap.put(groupMemberDTO.getGroupId(), removedLocWatchers);
                                }
                            }
                        }
                    }
                    if(removedMdnList != null){
                        disabledLocWatcher.addAll(removedMdnList);
                    }
                    if(totalMemMdn != null){
                        disabledLocWatcher.addAll(totalMemMdn);
                    }
                }
                knLogger.debug(methodName, "disabledLocWatcher ", KnGDPRTemplate.mdnList(disabledLocWatcher));
            }

            knLogger.debug(methodName, "Modify Name indicator - ", modifyName);
            Collection<Integer> sublistIdList = new ArrayList<Integer>();
            sublistIdList.add(sublistId);

            if ((/*(isObjectNull(addedSublistIds) || (!isObjectNull(addedSublistIds) && addedSublistIds.isEmpty())) &&*/
                    (isObjectNull(addedMdnList) || (!isObjectNull(addedMdnList) && addedMdnList.isEmpty())) &&
                            (isObjectNull(removedMdnList) || (!isObjectNull(removedMdnList) && removedMdnList.isEmpty()))) && modifyName
                    ) {
                sublistInfoUtil.modifySublistName(sublistInfoDTO.getSublistName(), sublistId,
                        xdmsHomePttId, persisterTxn);
                sublistInfoUtil.updateSublistEtag(sublistId, ++etag, xdmsHomePttId, persisterTxn);
                respDTO.setEtag(String.valueOf(etag));
                populate(respDTO);
                return respDTO;
            }
            boolean isChanged = false;
            if (membersToBeAddedToSublist != null && !membersToBeAddedToSublist.isEmpty()) {
                isChanged = true;
                knLogger.debug(methodName, "isChanged  - " + isChanged);
            }


            if (!isChanged && (sublistMemAftrrRemvlMem.size() != sublistMember.size())) {
                isChanged = true;
            }
            LinkedHashMap<String, LinkedList<String>> mdnContactListMap = new LinkedHashMap<String, LinkedList<String>>();
            LinkedHashMap<String, LinkedList<Integer>> status = null;
            int maxContactsPerSubs = corpProfile.getMaxContactsPerSubsc();
            if (isChanged) {
                Map<Integer, Integer> subListForProfileUpdate = new HashMap<>();
                subListForProfileUpdate.put(sublistInfoDTO.getSublistId(), KnConstants.CB_MAPPING.ETAG_CHANGE.value());
                sublistInfoDTO.setFinalMemberList(membersToBeAddedToSublist);
                //Modifying Sublist details
                respDTO = sublistInfoUtil.modifySublistDetails(sublistInfoDTO, modifyName, xdmsHomePttId, persisterTxn);
                sublistInfoUtil.updateSublistEtag(sublistId, ++etag, xdmsHomePttId, persisterTxn);
//                Collection<String> mdnListToRemoveDist = new ArrayList<String>();
                Set<String> mdnListToUpdateResourceEtag = new HashSet<String>();
                //Collection<String> mappedMdnList = sublistInfoUtil.getSubscribersDistToSublist(sublistId, xdmsHomePttId, persisterTxn);
                //get the info about if the sublist is pushed to a dispatcher
                boolean isPushedToDisp = false;
                if (mappedMdnList != null) {
                    isPushedToDisp = contactInfoUtil.isDispatchMemberPresent(new ArrayList(mappedMdnList), xdmsHomePttId, persisterTxn);
                }
                if (sublistInfoDTO.isDistribution()) {
                    KnIPCorpSublistSubscDistDTO distDTO = new KnIPCorpSublistSubscDistDTO();
//                    Set<String> lstToUpdateCntactCount = new HashSet<String>();
                    //adding new members and removed members in order to add the distribution if not present or to remove from deleted members if present
                    Collection<Integer> sublistIdLst = new ArrayList<Integer>();
                    sublistIdLst.add(sublistInfoDTO.getSublistId());
                    distDTO.setSublistIds(sublistIdLst);
                    /*Map<Integer, Collection<String>> subListPushed = sublistInfoUtil.sublistPushedToSubscribersFrmList(distDTO, xdmsHomePttId,
                            persisterTxn);*/
                    Map<Integer, Collection<String>> subListPushed = new HashMap<Integer, Collection<String>>();
                    subListPushed.put(sublistId, mappedMdnList);
                    Collection<String> mdnLstAlreadyPushed = subListPushed.get(sublistInfoDTO.getSublistId());
                    if (mdnLstAlreadyPushed != null && !mdnLstAlreadyPushed.isEmpty()) {
                        interMemList.removeAll(mdnLstAlreadyPushed);
                    }
                    if(isPushedToDisp){
                        respDTO.setEnabledDispatchMemList(interMemList);
                    }
                    //final mapped members
                    mappedMdnList.addAll(interMemList);
                    knLogger.debug("mappedMdnList of the sublist since distribution is true the member becomes mapped now-,", KnGDPRTemplate.mdnList(mappedMdnList));
                    distDTO.setMdnList(interMemList);
                    knLogger.info(methodName, "Push SUblist for the subscribers as the data is proper. ");
                    contactInfoUtil.pushSublistListToSubscriberList(distDTO, xdmsHomePttId, persisterTxn);
                    //lstToUpdateCntactCount.addAll(interMemList);

                    //update the dg.corpContactList table for the subscribers the sublist is pushed.
                    /* if (finalMissingContacts != null && !finalMissingContacts.isEmpty()) {
                        contactInfoUtil.insertMembersIntoCorpContactList(finalMissingContacts, xdmsHomePttId, persisterTxn);
                    }*/                             //will be taken care below
                    /*  etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId,
                   interMemList, persisterTxn);*/
                    if (removedMdnList != null && !removedMdnList.isEmpty()) {
                        removedMdnList.removeAll(interMemList);
                        if (removedMdnList != null && !removedMdnList.isEmpty()) {
                            KnIPCorpSublistSubscDistDTO removeDist = new KnIPCorpSublistSubscDistDTO();
                            /* List<String> mdnList = new ArrayList<String>();
                            for (String mdn : removedMdnList) {
                                if(mappedMdnList.contains(mdn)){
                                    mdnList.add(mdn);
                                }
                            }*/
                            removeDist.setMdnList(intMemInDelMembersStr);
                            removeDist.setSublistIds(sublistIdLst);
                            sublistInfoUtil.removeSubscribersSublist(removeDist, xdmsHomePttId, persisterTxn);
                            //if (finalSublistMembers != null && !finalSublistMembers.isEmpty()) {

                            for (String mdn : intMemInDelMembersStr) {
                                mdnContactListMap.put(mdn, sublistMemberStrList);
                            }
                            status = contactInfoUtil.deleteMembersFromCorpContactList(mdnContactListMap, xdmsHomePttId, persisterTxn);
                            //}
                            mdnListToUpdateResourceEtag.addAll(intMemInDelMembersStr);
                            //etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(removedMdnList, xdmsHomePttId, persisterTxn);
//                            lstToUpdateCntactCount.addAll(mdnListToRemoveDist);
                        }
                    }
                    if (removedMdnList != null && !removedMdnList.isEmpty()) {
                        if (addedMdnList == null || addedMdnList.isEmpty())
                        {
                            knLogger.debug(methodName," Request came for remove the subscriber so skipping validation.");
                            maxContactsPerSubs = -1;
                        }
                        contactInfoUtil.updateSubscribersContactCount(removedMdnList, maxContactsPerSubs, xdmsHomePttId, persisterTxn);
                    }
                }

                Map<String, Collection<KnCorpSubscriberDTO>> finalMissingContsForPushedMembers = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
                if (mappedMdnList != null && !mappedMdnList.isEmpty()) {
                    knLogger.debug("mappedMdnList are-,", KnGDPRTemplate.mdnList(mappedMdnList));
                    //modifying the corpcontactlist table
                    if (subscribersInfo != null && !subscribersInfo.isEmpty()) {
                        Collection<KnCorpSubscriberDTO> externalContactList = externalPocSubsPersistDto.getExternalMdnList();
                        if (externalContactList != null && !externalContactList.isEmpty()) {
                            //setting the proper corpId for the external subscribers
                            Map<String, KnCorpSubscriberDTO> subscMap = new HashMap<String, KnCorpSubscriberDTO>();
                            for (KnCorpSubscriberDTO subscriber : subscribersInfo) {
                                subscMap.put(subscriber.getMdn(), subscriber);
                            }
                            for (KnCorpSubscriberDTO subsc : externalContactList) {
                                String memberMdn = subsc.getMdn();
                                if (subscMap.containsKey(memberMdn)) {
                                    KnCorpSubscriberDTO subscriber = subscMap.get(memberMdn);
                                    subscriber.setCorpId(subsc.getCorpId());
                                }
                            }
                            subscribersInfo = subscMap.values();
                        }
                        Map<String, Collection<KnCorpSubscriberDTO>> subscrNewAddedContMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
                        Set<KnCorpSubscriberDTO> memList = new HashSet<KnCorpSubscriberDTO>();
                        memList.addAll(sublistMemAftrrRemvlMem);
                        memList.addAll(subscribersInfo);
                        knLogger.debug(methodName, "memList --> ",memList);
                        if(sublistInfoDTO.isDistribution()) {
                            for (KnCorpSubscriberDTO subsc : intNewMembers) {
                                subscrNewAddedContMap.put(subsc.getMdn(), memList);
                            }
                        }

                        //iterating thru the mapped mdn list and making sure its not in deleted list and the subscribers new
                        // added contact map just the missing subscriber should be considered i.e the guys which are not part of sublist but having the sublist
                        for (String mdn : mappedMdnList) {
                            if ((!removedMdnList.contains(mdn)) && (!subscrNewAddedContMap.containsKey(mdn))) {
                                subscrNewAddedContMap.put(mdn, subscribersInfo);
                            }
                        }

                        //get the subscriber Current ContactCount
                        Map<String, Collection<String>> subsContactMap =
                                contactInfoUtil.getSubscribersContactList(mappedMdnList, xdmsHomePttId, persisterTxn);
                        /* Map<String, Collection<KnCorpSubscriberDTO>>*/
                        finalMissingContsForPushedMembers =
                                commonInfoUtil.filterMemberToBeAddedToSubscriber(subsContactMap, subscrNewAddedContMap);
                        contactInfoUtil.insertMembersIntoCorpContactList(finalMissingContsForPushedMembers, xdmsHomePttId, persisterTxn);
                    }
                    //getting profile mdn of base mdn, to add to notification for common sublist modification

                    if(mappedMdnList!=null&&isCommonContactList){
                        ReceipentProfileMdns= corpSubsProvInfoUtil
                                .getProfileMdnByBaseMdns(new ArrayList<>(mappedMdnList), xdmsHomePttId, persisterTxn);
                        mappedMdnList.addAll(ReceipentProfileMdns);
                    }
                    mdnListToUpdateResourceEtag.addAll(mappedMdnList);
                    //etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(mappedMdnList, xdmsHomePttId, persisterTxn);
                }
                //getting profile mdn of base mdn, to add to notification for common sublist modification
                knLogger.debug(methodName, "Before filter mdnListToUpdateResourceEtag ", mdnListToUpdateResourceEtag);
                if(isCommonContactList){
                    //from the list of mdn remove the mdn where 121 featurebit is disabled
                    Map<String, KnCorpSubscriberDTO> subsDetails = contactInfoUtil.getPoCSubscriberExistMap(mdnListToUpdateResourceEtag,
                            xdmsHomePttId, persisterTxn);
                   Set<String> commonSublistDis = new HashSet<>();
                    if(subsDetails != null && !subsDetails.isEmpty()) {
                        for (String mdn : mdnListToUpdateResourceEtag) {
                            KnCorpSubscriberDTO subsDtls = subsDetails.get(mdn);
                            boolean isCommonContact = subsDtls.getSubscriberFs2() != null && KnGeneralUtil.getFeatureBitValue(subsDtls.getSubscriberFs2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.COMMON_CONTACT_LIST.value());
                            if (!isCommonContact) {
                                commonSublistDis.add(mdn);
                            }
                        }
                    }
                    mdnListToUpdateResourceEtag.removeAll(commonSublistDis);
                }
                knLogger.debug(methodName, "After filter mdnListToUpdateResourceEtag ", mdnListToUpdateResourceEtag);
                if (!isObjectNull(mdnListToUpdateResourceEtag) && !mdnListToUpdateResourceEtag.isEmpty()) {
                    etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(mdnListToUpdateResourceEtag, xdmsHomePttId, null, persisterTxn);
                }

                Collection<Integer> groupIdLst = new ArrayList<Integer>();
                Collection<Integer> dispatchGroup = new ArrayList<Integer>();
                for (KnCorpGroupInfoPersistDTO grpDTO : groupDTOLst) {
                    groupIdLst.add(grpDTO.getGroupId());
                    int grpType = grpDTO.getGroupType();
                    if (grpType == KnConstants.DISPATCH_GROUP) {
                        dispatchGroup.add(grpDTO.getGroupId());
                    }
                }

                Set<String> contactMemSet = new HashSet<String>();
                Map<Integer, Collection<KnCorpContactDTO>> groupMembersInsertList = new HashMap<Integer, Collection<KnCorpContactDTO>>();
                LinkedHashMap<Integer, LinkedList<String>> groupRemoveMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();
                Map<Integer, Collection<String>> groupDistListAfterModify = new HashMap<Integer, Collection<String>>();
                LinkedHashMap<Integer, LinkedList<Integer>> deletedGroupMembersStatus = new LinkedHashMap<Integer, LinkedList<Integer>>();
                Map<Integer, Collection<String>> currentGroupMember = new HashMap<Integer, Collection<String>>();
                Map<String, Collection<String>> groupFailurePairingMap = new HashMap<>();
                Collection<KnCorpAddlTGInfoDTO> delAddlTGList = new ArrayList<>();
                Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlDetails(groupIdLst, xdmsHomePttId, persisterTxn);
                Map<Integer, Collection<KnCorpAddlTGInfoDTO>> existingAddlTGList = new HashMap<>();
                Map<String, Collection<String>> actualDeletedGroupMemberMap = new HashMap<>();
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
                Map<Integer, Integer> groupMapListForProfileUpdate = new HashMap<>();
                if (groupIdLst != null && !groupIdLst.isEmpty()) {
                    //modifying the corpgroupmemberlist table
                    if (subscribersInfo != null && !subscribersInfo.isEmpty()) {
                        LinkedList<String> memberListToBeAdded = new LinkedList<String>();
                        for (KnCorpSubscriberDTO corpSubsDto : subscribersInfo) {
                            memberListToBeAdded.add(corpSubsDto.getMdn());
                        }
                        Map<Integer, Collection<KnCorpSubscriberDTO>> groupMemberListMap = new HashMap<Integer, Collection<KnCorpSubscriberDTO>>();
                        for (int groupId : groupIdLst) {
                            groupMemberListMap.put(groupId, subscribersInfo);
                            groupMapListForProfileUpdate.put(groupId, KnConstants.CB_MAPPING.ETAG_CHANGE.value());
                        }
                        currentGroupMember = groupInfoUtil.selectGroupMemberListForGroupIds(groupIdLst, xdmsHomePttId, persisterTxn);
                        Map<Integer, Collection<KnCorpSubscriberDTO>> actualMdnToBeAddedToGroup =
                                commonInfoUtil.filterGroupMembersTobeAdded(currentGroupMember, groupMemberListMap);
                        for (Map.Entry<Integer, Collection<KnCorpSubscriberDTO>> entry : actualMdnToBeAddedToGroup.entrySet()) {
                            Collection<KnCorpContactDTO> memberList = new ArrayList<KnCorpContactDTO>();
                            Integer groupId = entry.getKey();
                            Collection<KnCorpSubscriberDTO> memberMdnList = entry.getValue();
                            for (KnCorpSubscriberDTO subscriberDTO : memberMdnList) {
                                KnCorpContactDTO contact = new KnCorpContactDTO();
                                contact.setMdn(subscriberDTO.getMdn());
                                contact.setSupervisory(0);
                                contact.setContact_type(subscriberDTO.getContact_type());
                                contact.setClientType(subscriberDTO.getClientType());
                                contact.setName(subscriberDTO.getName());
                               /* if(subscriberDTO.getClientPVmajorVer()>=com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18  && subscriberDTO.getClientType()>=KnConstants.SUBSCR_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() && subscriberDTO.getClientType()<=KnConstants.SUBSCR_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
                                	contact.setIsAffiliationEnabled(ENABLED);
                                }else {
                                	contact.setIsAffiliationEnabled(DISABLED);
                                }*/

                                boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(subscriberDTO.getSubsActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.AFFILIATIONFEATURE.value());
                                if (subscriberDTO.getClientPVmajorVer() >= com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18
                                        && bitEnabled && checkClientType(subscriberDTO.getClientType(), subscriberDTO.getMcpttCompliance())) {
                                    contact.setIsAffiliationEnabled(ENABLED);
                                } else {
                                    contact.setIsAffiliationEnabled(DISABLED);
                                }
                                memberList.add(contact);
                            }
                            groupMembersInsertList.put(groupId, memberList);
                        }
                        groupInfoUtil.insertIntoCorpGroupMemberList(groupMembersInsertList, xdmsHomePttId, persisterTxn);
                        //UCSPROVCONFIG-7673
                        //Changes for PDMBB-12990
                        //Assign Zones and Position
                        //If new flag is not null and is enabled then only do below
                        String autoAssignZoneChannelFlag = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn)
                                .get(AUTO_ASSIGN_ZONE_POSITION);
                        knLogger.debug(methodName, "autoAssignZoneChannelFlag:", autoAssignZoneChannelFlag);
                        // If auto-assign is disabled, return disabled status
                        if(autoAssignZoneChannelFlag != null && autoAssignZoneChannelFlag.equals("1") && groupMembersInsertList != null && !groupMembersInsertList.isEmpty()){
                            List<String> unassignedMdns = new ArrayList<>();
                            List<String> assignmentStatusList = new ArrayList<>();
                            for (Integer groupId : groupIdList) {
                                Collection<KnCorpContactDTO> memberList = groupMembersInsertList.get(groupId);
                                Map<String, Object> assignmentResult = performAutoAssignZonesAndChannels(
                                        Arrays.asList(groupId),
                                        memberList,
                                        corpProfile,
                                        contactInfoUtil,
                                        persisterTxn
                                );
                                String autoZonePositionStatus = (String) assignmentResult.get("status");
                                assignmentStatusList.add(autoZonePositionStatus);
                                List<String> unassignedMdnResults = (List<String>) assignmentResult.get("unassignedMdns");
                                if (unassignedMdnResults != null && !unassignedMdnResults.isEmpty()) {
                                    unassignedMdns.addAll(unassignedMdnResults);
                                }

                            }
                            if (unassignedMdns != null && !unassignedMdns.isEmpty()) {
                                respDTO.setUnassignedMdns(unassignedMdns);
                            }
                            if(assignmentStatusList != null && !assignmentStatusList.isEmpty()){
                                if (assignmentStatusList.stream().allMatch(entry -> ZONE_ASSIGNMENT_STATUS.ASSIGNED.value().equals(entry))){
                                    respDTO.setZonePositionAssignmentStatus(ZONE_ASSIGNMENT_STATUS.ASSIGNED.value());
                                } else if (assignmentStatusList.stream().allMatch(entry -> ZONE_ASSIGNMENT_STATUS.FAILED.value().equals(entry))) {
                                    respDTO.setZonePositionAssignmentStatus(ZONE_ASSIGNMENT_STATUS.FAILED.value());
                                } else if (assignmentStatusList.stream().allMatch(entry -> ZONE_ASSIGNMENT_STATUS.DISABLED.value().equals(entry))) {
                                    respDTO.setZonePositionAssignmentStatus(ZONE_ASSIGNMENT_STATUS.DISABLED.value());
                                } else {
                                    respDTO.setZonePositionAssignmentStatus(ZONE_ASSIGNMENT_STATUS.PARTIAL.value());
                                }
                            }
                            knLogger.debug(methodName, "respDto with zonePositionAssignmentStatus:", respDTO.getZonePositionAssignmentStatus());
                        }
                        else{
                            respDTO.setZonePositionAssignmentStatus(ZONE_ASSIGNMENT_STATUS.DISABLED.value());
                            knLogger.info(methodName, "Auto-assignment disabled. Status:", respDTO.getZonePositionAssignmentStatus());
                        }
                    }
                    //retrieval of members
                    Map<Integer, Collection<String>> groupDistList = groupInfoUtil.getGroupSubscriberDistList(groupIdLst, xdmsHomePttId, persisterTxn);
                    //Filter out the actually added members and deleted members as same member can be already present in the group via sublist etc
                    Map<Integer, Map<String, Collection<String>>> memberDetailsList = commonInfoUtil.filterAddedRemovedMembers(groupDistList,
                            subscribersInfo, intMemInDelMembers, corpId);
                    //DAO calls to insert and delete from the dg.corpgroupdistinfo table
                    groupInfoUtil.deleteFrmCorpGroupDistInfo(memberDetailsList, xdmsHomePttId, persisterTxn);
                    //For Broadcast group, added members should not be inserted into group distribution table.
                    //So removing the broadcast groups from memberDetailsList.
                    Map<Integer, Map<String, Collection<String>>> memberDetailsForDist = new HashMap<>(memberDetailsList);
                    for (KnCorpGroupInfoPersistDTO grpPersistDTO : groupDTOLst) {
                        if (KnConstants.BROADCAST_GROUP == grpPersistDTO.getGroupType()) {
                            memberDetailsForDist.remove(grpPersistDTO.getGroupId());
                        }
                    }
                    groupInfoUtil.insertIntoCorpGroupDistInfo(memberDetailsForDist, xdmsHomePttId, persisterTxn);
                    //update the subscribers DISPATCH_GROUP_MEMBER attribute updation for the DIPATCH GROUP
                    Set<String> mdnSet = new HashSet<String>();
                    if (memberDetailsList != null && !memberDetailsList.isEmpty()) {
                        for (int grpId : dispatchGroup) {
                            Map<String, Collection<String>> memberList = memberDetailsList.get(grpId);
                            if (memberList != null && !memberList.isEmpty()) {
                                Collection<String> addedMemberList = memberList.get(KnConstants.ADDED_MEMBERS);
                                if (addedMemberList != null && !addedMemberList.isEmpty()) {
                                    mdnSet.addAll(addedMemberList);
                                }
                            }
                        }
                    }
                    if (mdnSet != null && !mdnSet.isEmpty()) {
                        Collection<String> existingEnabledMemList = new ArrayList<>();
                        if (respDTO.getEnabledDispatchMemList() != null)
                            existingEnabledMemList.addAll(respDTO.getEnabledDispatchMemList());
                        existingEnabledMemList.addAll(mdnSet);
                        respDTO.setEnabledDispatchMemList(existingEnabledMemList);
                    }
                    Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIdLst, xdmsHomePttId, persisterTxn);
                    groupDistListAfterModify = groupInfoUtil.getGroupSubscriberDistList(groupIdLst, xdmsHomePttId, persisterTxn);
                    //This variable is to hold the actilly deleted members from group member list table for each group id.
                    Map<Integer, Collection<String>> actualDeletedMembers = new HashMap<>();
                    //This variable is to hold the deleted members in the deleted groups (less than 2 member group Ids)
                    Map<Integer, Collection<String>> deletedMembersMap = new HashMap<Integer, Collection<String>>();
                    Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupIdLst, xdmsHomePttId, persisterTxn);
                    if (!isObjectNull(removedMdnList) && !removedMdnList.isEmpty() && groupIdLst != null && !groupIdLst.isEmpty()) {
                        //Removing the members from the corpgroupmemberlist table

                        for (int groupId : groupIdLst) {
                            groupRemoveMdnListMap.put(groupId, removedMdnList);
                        }
                        deletedGroupMembersStatus = groupInfoUtil.deleteCorpGroupMemberList(groupRemoveMdnListMap, xdmsHomePttId, persisterTxn);
                        actualDeletedMembers = commonInfoUtil.filterDeletedGroupMembers(groupDistListAfterModify, groupDistList);
                        //De-assign camped group for deleted internal members from the groupIds.
                        Map<Integer, List<String>> deCampedGrpMemMap = commonInfoUtil.prepareGrpMemPam(actualDeletedMembers);
                        knLogger.debug("deCampedGrpMemMap-,", deCampedGrpMemMap);
                        groupInfoUtil.cleanUpCampedGrp(deCampedGrpMemMap, corpInOutParamDTO, xdmsHomePttId, persisterTxn);
                        etagMap = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);


                        //Determine the deleted and the modified groups
                        Map<String, HashMap<Integer, String>> groupListStatus = groupInfoUtil.getGroupListStatus(groupIdLst, xdmsHomePttId, persisterTxn);
                        knLogger.debug("groupListStatus-,", groupListStatus);
                        // Delete corp groups with < 2 members
                        Set<Integer> delGroupIdList = new HashSet<>();
                        Collection<String> groupIdAsDestination = new ArrayList<>();
                        if (groupListStatus.get(KnConstants.DELETED) != null && groupListStatus.get(KnConstants.DELETED).keySet() != null) {
                            delGroupIdList = groupListStatus.get(KnConstants.DELETED).keySet();
                            if (delGroupIdList != null && !delGroupIdList.isEmpty()) {
                                for (Integer grpId : delGroupIdList) {
                                    Collection<String> delMembersList = groupDistListAfterModify.get(grpId);
                                    deletedMembersMap.put(grpId, delMembersList);
                                    //for the contact added to update the directory
                                    contactMemSet.addAll(delMembersList);
                                    groupIdAsDestination.add(String.valueOf(grpId));
                                    if(existingAddlTGList.get(grpId) != null) delAddlTGList.addAll(existingAddlTGList.get(grpId));
                                    groupMapListForProfileUpdate.put(grpId, KnConstants.CB_MAPPING.NEED_TO_REMOVE.value());
                                }
                                Map<String, Collection<String>> emergDestUserMap = corpSubsProvInfoUtil.getEmergDestUserMap(groupIdAsDestination,
                                        xdmsHomePttId, persisterTxn);
                                groupFailurePairingMap.putAll(emergDestUserMap);
                                for (Integer groupId : delGroupIdList) {
                                    groupDistListAfterModify.remove(groupId);
                                }
                            }
                        }
                        actualDeletedGroupMemberMap = KnCorpCommonInfoUtil.getDeletedMembers(groupRemoveMdnListMap, deletedGroupMembersStatus);
                        //LocWatcher: Removed
                        Collection<String> finalLocWatcherRemoved = new ArrayList<>();
                        actualDeletedGroupMemberMap.forEach((grpId, deletedMem) -> {
                            Collection<String> actualLocWatcher = removedLocWatcherMap.get(Integer.parseInt(grpId));
                            if(actualLocWatcher != null){
                                finalLocWatcherRemoved.addAll(actualLocWatcher.stream().filter(deletedMem::contains).collect(Collectors.toList()));
                            }
                        });
                        respDTO.setRemovedLocWatcherList(finalLocWatcherRemoved);
                        // Additional Talk Group:
                        if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                            Map<String, Collection<String>> finalActualDeletedGroupMemberMap = actualDeletedGroupMemberMap;
                            delAddlTGList.addAll(subsAddlTGList.stream().filter(addlMem -> (finalActualDeletedGroupMemberMap
                                    .get(String.valueOf(addlMem.getGroupId())) != null) && finalActualDeletedGroupMemberMap
                                    .get(String.valueOf(addlMem.getGroupId())).contains(addlMem.getMdn())).collect(Collectors.toList()));
                            Collection<String> mdnForAddlTg = subsAddlTGList.stream().map(KnCorpAddlTGInfoDTO::getMdn).distinct().collect(Collectors.toList());
                            groupInfoUtil.deleteSubsAddlTGList(delAddlTGList, xdmsHomePttId, persisterTxn);
                            subsAddlTGList.removeAll(delAddlTGList);
                            subsAddlTGList.forEach(subsAddl -> {
                                if(groupDetailsMap.get(subsAddl.getGroupId()) != null) subsAddl.setGroupMemCount(groupDetailsMap.get(subsAddl.getGroupId()).getGroupMemCount());
                            });
                            etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHomePttId, mdnForAddlTg, persisterTxn, etagMap);
                            knLogger.debug(methodName, "etagMap AddlTG - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                            Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
                            if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                                groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsAddlTGList, xdmsHomePttId, persisterTxn);
                            }
                            knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
                            if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                                etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, subsAddlTGList, delAddlTGList, groupCorpIdMap);
                                knLogger.debug(methodName, "etagMap AddlTG -diff - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                            }
                        }
                        groupInfoUtil.deleteAllGroups(delGroupIdList, xdmsHomePttId, corpInOutParamDTO, persisterTxn);
                        etagMap = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                        etagMap = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                    }

                    if (addedMdnList != null && !addedMdnList.isEmpty()) {
                        Collection<KnCorpGroupMemberDTO> grpMemberDetails = groupInfoUtil.getCorpGroupMemberForLocWatcher(groupIdList, xdmsHomePttId, persisterTxn);
                        Collection<String> locWatcherMember = new ArrayList<>();
                        if (!grpMemberDetails.isEmpty()) {
                            for (KnCorpGroupMemberDTO groupMemberDTO : grpMemberDetails) {
                                if (groupMemberDTO.getLocWatcher() == KnConstants.LOCWATCHER) {
                                    locWatcherMember.add(groupMemberDTO.getMdn());
                                }
                            }
                        }
                        // Now we will call contact assignement method.
                        contactInfoUtil.contactForSupervisiorLocWatcher(locWatcherMember, addedMdnList, corpId, corpProfile.getMaxContactsPerSubsc(),
                                etagMap, corpProfile.getXdmsHome(), persisterTxn);

                        if(locWatcherMember.size() > 0 && addedMdnList != null) {
                            enabledLocWatcher.addAll(addedMdnList);
                        }
                        knLogger.debug(methodName, "enabledLocWatcher ", KnGDPRTemplate.mdnList(enabledLocWatcher));
                    }

                    //Updating the group member count
                    Collection<Integer> sublistIdsList = new ArrayList<Integer>();
                    sublistIdsList.add(sublistId);
                    if(corpProfile.getLargeGrpSupported() == 1){
                        knLogger.debug(methodName, "Updating count for large group supported");
                        sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdsList, maxContactsPerSubs,
                                corpProfile.getMaxMemPerLrgGrp(), corpProfile.getMaxMemPerLrgGrp(), xdmsHomePttId, persisterTxn, corpProfile.getMaxMemPerLrgBGrp());
                    }else{
                        sublistInfoUtil.updateSublistsSubscribersContactCount(sublistIdsList, maxContactsPerSubs,
                                corpProfile.getMaxMemPerCorpGroup(), corpProfile.getMaxMembersPerDispatchGroup(), xdmsHomePttId, persisterTxn, corpProfile.getMaxMemPerBCGrp());
                    }

                     /*
                        Get the group member count and group type for the groupIds and set these values into etag Map
                     */
                    if (!actualDeletedMembers.isEmpty()) {
                        etagMap = commonInfoUtil.formSubscriberNotification(actualDeletedMembers, groupEtagMap,
                                com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                    }
                    if (!deletedMembersMap.isEmpty()) {
                        etagMap = commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                                com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                    }
                    Map<Integer, Collection<String>> groupDistAfterModify_NonLrg = new HashMap<>();
                    Map<Integer, Integer> largeGrpInfoMap = new HashMap<>();
                    if (corpProfile.getLargeGrpSupported() == 1) {
                        Map<Integer, Integer> memberCountForAllGrps = groupInfoUtil.getGroupMemCount(groupIdLst, xdmsHomePttId, persisterTxn);
                        for(Integer grpId : groupIdLst){
                            Integer memberCount = memberCountForAllGrps.get(grpId);
                            if ((groupDetailsMap.get(grpId).getGroupType() == KnConstants.STANDARD_GROUP && memberCount != null) ||
                                    (groupDetailsMap.get(grpId).getGroupType() == KnConstants.DISPATCH_GROUP && memberCount != null)) {
                                int maxMembers = (groupDetailsMap.get(grpId).getGroupType() == KnConstants.STANDARD_GROUP)
                                        ? corpProfile.getMaxMemPerCorpGroup(): corpProfile.getMaxMembersPerDispatchGroup();
                                if(memberCount > maxMembers){
                                    largeGrpInfoMap.put(grpId, 1);
                                }else if(memberCount <= maxMembers && groupDetailsMap.get(grpId).isLargeGroup()){
                                    largeGrpInfoMap.put(grpId, 0);
                                }
                            }
                        }
                    }

                    if (!groupDistListAfterModify.isEmpty()) {
                        for(KnCorpGroupInfoPersistDTO grp : groupDTOLst){
                            KnCorpGroupDTO grpAftrModify = groupDetailsMap.get(grp.getGroupId());
                            if(grpAftrModify != null ){
                                if(!grpAftrModify.isLargeGroup() && !grp.isLargeGroup()){
                                    groupDistAfterModify_NonLrg.put(grp.getGroupId(), groupDistListAfterModify.get(grp.getGroupId()));
                                }
                            }
                        }
                        knLogger.debug(methodName, "groupDistListAfterModify - ", groupDistListAfterModify);
                        knLogger.debug(methodName, "groupDistAfterModify_NonLrg - ", groupDistAfterModify_NonLrg);
                        etagMap = commonInfoUtil.formSubscriberNotification(groupDistAfterModify_NonLrg, groupEtagMap,
                                com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
                    }
                    for (Collection<String> memberList : groupDistListAfterModify.values()) {
                        contactMemSet.addAll(memberList);
                    }
                    knLogger.debug(methodName, "largeGrpInfoMap - ", largeGrpInfoMap);
                    //Updating IS_LARGEGROUP flag
                    if(!largeGrpInfoMap.isEmpty()){
                        groupInfoUtil.updateIsLargeGrpFlag(largeGrpInfoMap, xdmsHomePttId, persisterTxn);
                        //Updating loc watchers for large groups.
                        List<Integer> largeGrpIdList = new ArrayList<>();
                        for(Integer id : largeGrpInfoMap.keySet()){
                            if(largeGrpInfoMap.get(id) == 1){
                                largeGrpIdList.add(id);
                            }
                        }
                        knLogger.debug(methodName, "largeGrpIdList - ", largeGrpIdList);
                        if(!largeGrpIdList.isEmpty()){
                            groupInfoUtil.updateGrpMemListLocWatchers(largeGrpIdList, persisterTxn, xdmsHomePttId);
                            //Validate the max large group counts if any group become large group after modify
                            groupInfoUtil.validateLargeGroupCounts(corpProfile, persisterTxn, xdmsHomePttId);
                        }
                    }
                }

                LinkedHashMap<String, LinkedList<Integer>> status2 = null;
                LinkedHashMap<String, LinkedList<String>> removeMdnListMap = new LinkedHashMap<String, LinkedList<String>>();
                if (!isObjectNull(removedMdnList) && !removedMdnList.isEmpty()) {
                    for (String mdn : mappedMdnList) {
                        if (!removedMdnList.contains(mdn)) {
                            removeMdnListMap.put(mdn, removedMdnList);
                        }
                    }
                    status2 = contactInfoUtil.deleteMembersFromCorpContactList(removeMdnListMap, xdmsHomePttId, persisterTxn);
                    respDTO.setDisabledDispatchMemList(intMemInDelMembersStr);
                }

                Set<String> updateDirMdnList = new HashSet<String>();
                if ((mappedMdnList != null && !mappedMdnList.isEmpty())) {
                    updateDirMdnList.addAll(mappedMdnList);
                }
                if (contactMemSet != null && !contactMemSet.isEmpty()) {
                    updateDirMdnList.addAll(contactMemSet);
                }
                /* if (sublistInfoDTO.isDistribution()) {
                    updateDirMdnList.addAll(mdnListToRemoveDist);
                }*/
                if (updateDirMdnList != null && !updateDirMdnList.isEmpty()) {
                    etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(updateDirMdnList, null, etagMap, xdmsHomePttId,
                            persisterTxn);
                    //removing all profile mdn from the
                    updateDirMdnList.removeAll(ReceipentProfileMdns);
                    contactInfoUtil.updateSubscribersContactCount(updateDirMdnList, maxContactsPerSubs, xdmsHomePttId, persisterTxn);
                }

                etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, finalMissingContsForPushedMembers, mdnContactListMap, null, groupMembersInsertList, groupRemoveMdnListMap, null, null, groupDistListAfterModify, status, deletedGroupMembersStatus, null);

                etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, removeMdnListMap, null, null, null, null, null, null, status2, null, null);
                knLogger.debug(methodName, "etagMap ", KnGDPRTemplate.mapKeyMdn(etagMap));
                if(removeMdnListMap != null){
                    removeMdnListMap.forEach((key, value) -> {
                        if(mdnContactListMap.get(key) != null){
                            LinkedList<String> value1 = mdnContactListMap.get(key);
                            value1.addAll(value);
                        } else {
                            mdnContactListMap.put(key, value);
                        }
                    });
                }

                LinkedHashMap<String, LinkedList<Integer>> finalStatus = status;
                if (status2 != null) {
                    status2.forEach((key, value) -> {
                        if (finalStatus != null) {
                            if (finalStatus.get(key) != null) {
                                LinkedList<Integer> value1 = finalStatus.get(key);
                                value1.addAll(value);
                            } else {
                                finalStatus.put(key, value);
                            }
                        }
                    });
                }

                if(sublistDetails.getDistributionPolicy() == 6){
                    //the sublist is common
                    Map<String, Collection<String>> authCompleteMapRemove = new ConcurrentHashMap<>();
                    Map<String,List<String>> infos = sublistInfoUtil.getAuAndCommonTuMapping(new ArrayList<>(mappedMdnList),removedMdnList,xdmsHomePttId,persisterTxn);
                    for(Map.Entry<String, List<String>> auMap : infos.entrySet()) {
                        String au = auMap.getKey();
                        ArrayList<String> tuList =(ArrayList<String>) auMap.getValue();
                        List<String> profileMdns = sublistInfoUtil.getProfileMdnByBaseMdns(tuList,xdmsHomePttId, persisterTxn);
                        tuList.addAll(profileMdns);
                        authCompleteMapRemove.put(au,tuList);
                    }
                    sublistInfoUtil.deleteFromMcpttPerm(authCompleteMapRemove,xdmsHomePttId, persisterTxn);
                }

                Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = corpUserProfileUtil.updateImpactedCBDocuments(String.valueOf(corpId), subListForProfileUpdate,
                        groupMapListForProfileUpdate,null, xdmsHomePttId, persisterTxn);
                respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
                if(profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()){
                    respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
                }
                
                // remove from McpttPermInfo
				if (removedMdnList != null && !removedMdnList.isEmpty()) {
					Map<String, Integer> UserProfileIds = corpUserProfileUtil.getUserProfileIdFromSublistIds(String.valueOf(corpId), subListForProfileUpdate.keySet(), xdmsHomePttId, persisterTxn);
					List<String> authMdns=corpUserProfileUtil.getProfileMdnsByUserProfileIds(String.valueOf(corpId), UserProfileIds.keySet(), xdmsHomePttId, persisterTxn);
					List<String> targetMdns= corpSubsProvInfoUtil.getProfileMdnByBaseMdns(removedMdnList, xdmsHomePttId, persisterTxn);
					targetMdns.addAll(removedMdnList);
					Map<String, Collection<String>> authTargetMap = new HashMap<String, Collection<String>>();
					for (String authMdn : authMdns) {
						authTargetMap.put(authMdn, targetMdns);
					}
					
					corpSubsProvInfoUtil.deleteFromMcpttPermInfoProfileMdns(authTargetMap, xdmsHomePttId, persisterTxn);

				}
				
                //Contact:
                Map<String, Collection<String>> actualDeletedContactMap = KnCorpCommonInfoUtil.getActualDeletedContacts(mdnContactListMap, finalStatus);
                if(actualDeletedContactMap != null && !actualDeletedContactMap.isEmpty()){
                    Map<String, Collection<String>> emergUserDestMap = corpSubsProvInfoUtil.getEmergUserDestMap(actualDeletedContactMap.keySet(),
                            xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "emergUserDestMap-- ", KnGDPRTemplate.mapKeyValueListMdn(emergUserDestMap));
                    Collection<String> finalFailedRemovedContacts = new ArrayList<>();
                    Map<String, Collection<String>> contactFailurePairingMap = new HashMap<>();
                    if (emergUserDestMap != null && !emergUserDestMap.isEmpty()) {
                        emergUserDestMap.forEach((emergUser, emergDestinations) -> {
                            Collection<String> removedContacts = actualDeletedContactMap.get(emergUser);
                            Collection<String> mappedDestination = emergDestinations.stream().filter(removedContacts::contains).collect(Collectors.toList());
                            finalFailedRemovedContacts.addAll(mappedDestination);
                            if(!mappedDestination.isEmpty()) contactFailurePairingMap.put(emergUser, mappedDestination);
                        });
                    }
                    knLogger.debug(methodName, "contactFailurePairingMap-- ", KnGDPRTemplate.mapKeyValueListMdn(contactFailurePairingMap));
                    if(!contactFailurePairingMap.isEmpty()){
                        knLogger.error(methodName, "Emergency Destination Mapping Exists - Contact");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGENCY_DESTINATION_MAPPING_EXISTS_FOR_CONTACT,
                                "Emergency Destination Mapping Exists for contacts--", CORP_SUBLIST_MANAGER,
                                MODIFY_SUBLIST, "", Arrays.asList(finalFailedRemovedContacts).toString(), "");
                    }
                }
                //Group:
                if(actualDeletedGroupMemberMap != null && !actualDeletedGroupMemberMap.isEmpty()){
                    Map<String, Collection<String>> emergDestUserMap = corpSubsProvInfoUtil.getEmergDestUserMap(actualDeletedGroupMemberMap.keySet(),
                            xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "emergDestUserMap-- ", KnGDPRTemplate.mapKeyValueListMdn(emergDestUserMap));
                    if (emergDestUserMap != null && !emergDestUserMap.isEmpty()) {
                        Map<String, Collection<String>> finalActualDeletedGroupMemberMap1 = actualDeletedGroupMemberMap;
                        emergDestUserMap.forEach((emergDest, emergDestinations) -> {
                            Collection<String> removedGroupMember = finalActualDeletedGroupMemberMap1.get(emergDest);
                            Collection<String> mappedDestination = emergDestinations.stream().filter(removedGroupMember::contains).collect(Collectors.toList());
                            if(mappedDestination != null && !mappedDestination.isEmpty()) groupFailurePairingMap.put(emergDest, mappedDestination);
                        });
                    }
                    knLogger.debug(methodName, "groupFailurePairingMap-- ", KnGDPRTemplate.mapKeyValueListMdn(groupFailurePairingMap));
                }
                if(!groupFailurePairingMap.isEmpty()){
                    knLogger.error(methodName, "Emergency Destination Mapping Exists - Group");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGENCY_DESTINATION_MAPPING_EXISTS_FOR_GROUP,
                            "Emergency Destination Mapping Exists for Group--", CORP_SUBLIST_MANAGER,
                            MODIFY_SUBLIST, "", groupFailurePairingMap.keySet().toString(), "");
                }
                etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
                knLogger.debug(methodName, "etagMap after auth mapping modification- ", KnGDPRTemplate.mapKeyMdn(etagMap));

                for (Map.Entry<Integer, Collection<String>> entry : groupDistListAfterModify.entrySet()) {
                    entry.getValue().addAll(mdnsAdded);
                }
                LinkedList<KnLIEventDTO> liEventList = KnCorpCommonInfoUtil.populateLIData(etagMap
                        , xdmsHomePttId, groupNameMap, groupDistListAfterModify, currentGroupMember,persisterTxn);
                respDTO.setLiEventList(liEventList);

                if(corpProfile.getLargeGrpSupported() == 1){
                    //If large group is supported, remove the entry from etagMap if the doc change dto list is null or empty
                    Set<String> mdnSet = new HashSet<>(etagMap.keySet());
                    for(String mdn : mdnSet){
                        KnOPDirChgDTO chgDTO = etagMap.get(mdn);
                        if(chgDTO.getDocChgDTO() == null || chgDTO.getDocChgDTO().isEmpty()){
                            etagMap.remove(mdn);
                        }
                    }
                }

                respDTO.setChangeLogMap(etagMap);
                //send the data in response if ther are any subscriber is a dispatch to whom the sublist is pushed.
                if (isGroupExists) {
                    Collection<String> existingDisableDispatchMemList = respDTO.getDisabledDispatchMemList();
                    Collection<String> existingEnableDispatchMemList = respDTO.getEnabledDispatchMemList();
                    if(existingDisableDispatchMemList != null && !existingDisableDispatchMemList.isEmpty()){
                        existingDisableDispatchMemList.addAll(disabledLocWatcher);
                        respDTO.setDisabledDispatchMemList(existingDisableDispatchMemList);
                    } else{
                        respDTO.setDisabledDispatchMemList(disabledLocWatcher);
                    }
                    if(existingEnableDispatchMemList != null && !existingEnableDispatchMemList.isEmpty()){
                        existingEnableDispatchMemList.addAll(enabledLocWatcher);
                        respDTO.setEnabledDispatchMemList(existingEnableDispatchMemList);
                    } else{
                        respDTO.setEnabledDispatchMemList(enabledLocWatcher);
                    }
                }
            } else if (modifyName) {
                sublistInfoUtil.modifySublistName(sublistInfoDTO.getSublistName(), sublistId,
                        xdmsHomePttId, persisterTxn);
                sublistInfoUtil.updateSublistEtag(sublistId, ++etag, xdmsHomePttId, persisterTxn);
            }
            respDTO.setEtag(String.valueOf(etag));
            respDTO.setTgsModeChgMap(corpInOutParamDTO.getTgsModeChgMap());
            respDTO.setMdnCorpId(corpId);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }

        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while modifying Sublist - ", e);
            populate(respDTO, e);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while modifying Sublist - ", e);
            knLogger.debug(methodName, "Error Code recieved is  - ", e.getErrorCode());
            if (e.getErrorCode().equalsIgnoreCase(KnErrorCodes.DAO.ROW_ALREADY_EXISTS_GROUP_DOC)) {
                knLogger.debug(methodName, "Initiate the refresh of data as unique constraint error occurred in Db - ", e.getErrorCode());
                KnIdGeneratorImpl idGenerator = KnIdGeneratorImpl.getInstance();
                String tableName = "DG.XDM_CORP_GROUPDOC";
                tableName = tableName.toUpperCase();
                KnTableInfoBean tableInfoBean = new KnTableInfoBean();
                tableInfoBean.setColumnName("CORPGROUPDOCID");
                tableInfoBean.setTableName(tableName);
                tableInfoBean.setPttServerId(xdmsHomePttId);
                List<KnTableInfoBean> tableList = new ArrayList<KnTableInfoBean>();
                tableList.add(tableInfoBean);
                knLogger.debug(methodName, "Before  call to the reInitialise of the ids");
                idGenerator.reInitialiseIds(tableList);
                knLogger.debug(methodName, "After  call to the reInitialise of the ids");
            }
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while modifying Sublist- ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }

    public KnCorpResponseDTO deleteSublist(KnIPCorpSublistDTO sublistDTO, KnPersisterTxn persisterTxn) {
        String methodName = "deleteSublist(sublistDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", sublistDTO);

        //Step:
        //creating Response DTO object
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            KnCorpInOutParamDTO corpInOutParamDTO = new KnCorpInOutParamDTO();
            int corpId = sublistDTO.getCorpId();
            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile =
                    commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();

            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = sublistDTO.getCustomParamMap();
            if (sublistDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                sublistDTO.setCustomParamMap(customParams);
                List<Integer> sublistIdList = new ArrayList<Integer>();
                sublistIdList.add(sublistDTO.getSublistId());
                KnCorpSublistInfoUtil knCorpSublistInfoUtil = new KnCorpSublistInfoUtil();
                knCorpSublistInfoUtil.validateSublistParams(sublistIdList,customParams, xdmsHomePttId, persisterTxn);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.DELET_SUBLIST);
                hookIPDTO.setData(sublistDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            int sublistId = sublistDTO.getSublistId();

            knLogger.debug(methodName, "Before Getting the subList basic Details");
            KnCorpSublistDTO sublistDetails = sublistInfoUtil.getSublistInfo(sublistId,
                    sublistDTO.getCorpId(), xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "After Getting the subList basic Details");


            KnSublistDetailsPersistDTO sublistDetailsDTO = new KnSublistDetailsPersistDTO();
            sublistDetailsDTO.setCurrentEtag(sublistDetails.getETag());
            sublistDetailsDTO.setSublistType(sublistDetails.getSublistType());
            sublistDetailsDTO.setInputDTO(sublistDTO);
            sublistDetailsDTO.setSublistId(sublistId);
            knLogger.debug(methodName, "Invoking Validation FW - ", sublistDetailsDTO);
            validatorFW.validate(sublistDetailsDTO);
            knLogger.debug(methodName, "Validation completed Successfully", sublistDetailsDTO);

            respDTO = deleteSublistOperation(sublistDTO, xdmsHomePttId, corpInOutParamDTO, corpProfile.getLinkedGwKey(), persisterTxn, corpProfile);
            respDTO.setTgsModeChgMap(corpInOutParamDTO.getTgsModeChgMap());
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while delete Sublist - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while delete Sublist - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT : Returning Response - ", respDTO);
        return respDTO;
    }

    public KnCorpSublistRespDTO getSublistDetails(KnIPCorpSublistDTO sublistReqDto, KnPersisterTxn persisterTxn) {
        String methodName = "getSublistDetails(KnIPCorpSublistDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed with corpId and sublistId- ", sublistReqDto.getCorpId(),
                " ", sublistReqDto.getSublistId());

        //Step:
        //creating Response DTO object
        KnCorpSublistRespDTO respDTO = new KnCorpSublistRespDTO();
        try {
            int corpId = sublistReqDto.getCorpId();

            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile =
                    commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();

            knLogger.debug(methodName, "Before Getting the subList basic Details");
            KnCorpSublistDTO sublistDetails = new KnIPCorpSublistDistDTO();
            Boolean isSharedCorpReq = Boolean.FALSE;
            Integer ownerCorpId = null;
            try {
                sublistDetails = sublistInfoUtil.getSublistInfo(sublistReqDto.getSublistId(),
                        sublistReqDto.getCorpId(), xdmsHomePttId, persisterTxn);
            } catch (KnCorpBOException e) {
                //sublist is not found for the mdn
                List<String> userProfilelist = corpUserProfileUtil.getXdmUserProfileIdsBySharedCorpId(String.valueOf(corpId), xdmsHomePttId, true, persisterTxn);
                if (!userProfilelist.isEmpty()) {
                    List<KnCorpUserProfileDTO> userProfileList = corpUserProfileUtil.getUserProfile(userProfilelist, xdmsHomePttId, persisterTxn);
                    for (KnCorpUserProfileDTO profile : userProfileList) {
                        if (profile.getContactListID() != null && profile.getContactListID() == sublistReqDto.getSublistId()) {
                            isSharedCorpReq = Boolean.TRUE;
                            ownerCorpId = profile.getCorporateID();
                            sublistDetails = sublistInfoUtil.getSublistInfo(sublistReqDto.getSublistId(),
                                    ownerCorpId, xdmsHomePttId, persisterTxn);
                        }
                    }
                }
                if (sublistDetails == null) {
                    throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBLIST_DOES_NOT_BELONG_TO_CORP, "Sublist Doesn't exist", e);
                }
            }

            //custom hook call
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = sublistReqDto.getCustomParamMap();
            if (sublistReqDto.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                sublistReqDto.setCustomParamMap(customParams);
                if (!isSharedCorpReq) {
                    List<Integer> sublistIdList = new ArrayList<Integer>();
                    sublistIdList.add(sublistReqDto.getSublistId());
                    KnCorpSublistInfoUtil knCorpSublistInfoUtil = new KnCorpSublistInfoUtil();
                    knCorpSublistInfoUtil.validateSublistParams(sublistIdList, customParams, xdmsHomePttId, persisterTxn);
                }
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.GET_SUBLIST_DETAILS);
                hookIPDTO.setData(sublistReqDto);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }

            knLogger.debug(methodName, "After Getting the subList basic Details - ", sublistDetails);

            long currentEtag = sublistDetails.getETag();
            knLogger.debug(methodName, "currentEtag - ", currentEtag);
            knLogger.debug(methodName, "sublistReqDto.getETag() - ", sublistReqDto.getETag());

            KnSublistDetailsPersistDTO sublistDetailsDTO = new KnSublistDetailsPersistDTO();
            sublistDetailsDTO.setCurrentEtag(sublistDetails.getETag());
            sublistDetailsDTO.setInputDTO(sublistReqDto);
            sublistDetailsDTO.setSublistType(sublistDetails.getSublistType());
            sublistDetailsDTO.setSublistId(sublistDetails.getSublistId());
            knLogger.debug(methodName, "Invoking Validation FW - ", sublistDetailsDTO);
            validatorFW.validate(sublistDetailsDTO);
            knLogger.debug(methodName, "Validation completed Successfully", sublistDetailsDTO);

            knLogger.debug(methodName, "Before getSublistDetails ");
            if (!isSharedCorpReq) {
                respDTO = sublistInfoUtil.getSublistDetails(sublistReqDto.getOperationType(), sublistReqDto.getSublistId(), sublistReqDto.getCorpId(),
                        corpProfile.getMaxContactsPerSubsc(), xdmsHomePttId, true, persisterTxn);
            } else {
                respDTO = sublistInfoUtil.getSublistDetails(sublistReqDto.getOperationType(), sublistReqDto.getSublistId(), ownerCorpId,
                        corpProfile.getMaxContactsPerSubsc(), xdmsHomePttId, true, persisterTxn);
            }
            //Get the distribution list for the Sublist.

           if(sublistDetails.getDistributionPolicy() == DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT) {
               Collection<String> mappedMdnList = sublistInfoUtil.getSubscribersDistToSublist(sublistReqDto.getSublistId(), xdmsHomePttId, true, persisterTxn);
               knLogger.debug(methodName, "mappedMdnList of the sublist from the DB are-,", KnGDPRTemplate.mdnList(mappedMdnList));
               if (mappedMdnList.size() > 0) {
                   respDTO.setAssignedSubsList(mappedMdnList.stream().collect(Collectors.toList()));
               }
           }
            knLogger.debug(methodName, "After getSublistDetails ");
            KnCorpSublistDTO sublistDto = new KnCorpSublistDTO();
            sublistDto.setSublistName(sublistDetails.getSublistName());
            sublistDto.setSublistId(sublistDetails.getSublistId());
            sublistDto.setSublistType(sublistDetails.getSublistType());
            sublistDto.setDistributionPolicy(sublistDetails.getDistributionPolicy());
            sublistDto.setETag(currentEtag);
            respDTO.setSublistDTO(sublistDto);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }

        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while getSublistDetails - ", e);
            populate(respDTO, e);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while getSublistDetails - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while getSublistDetails- ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }

    public KnCorpSublistListRespDTO getAllSublist(KnIPCorpInfoDTO corpInfoDto, KnPersisterTxn persisterTxn) {

        String methodName = "getAllSharedCorpSublists(corpInfoDto)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", corpInfoDto.getCorpId(), " NextToken - ", corpInfoDto.getNextToken(), " FetchSize - ", corpInfoDto.getFetchSize());

        //Step:
        //creating Response DTO object
        KnCorpSublistListRespDTO respDTO = new KnCorpSublistListRespDTO();
        try {

            int corpId = corpInfoDto.getCorpId();
            int nextToken = 0;
            if (null != corpInfoDto.getNextToken()) {
                nextToken = corpInfoDto.getNextToken();
            }
            int fetchSize = 0;
            if (null != corpInfoDto.getFetchSize()) {
                fetchSize = corpInfoDto.getFetchSize();
            }
            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile =
                    commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);
            if (isObjectNull(corpProfile)) {
                knLogger.error(methodName, "Corporate Profile not found - ", corpProfile);
                //todo throw error need to check
            }

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();

            Collection<KnCorpSublistDTO> sublistList = sublistInfoUtil.getAllSharedCorpSublists(corpId, nextToken, fetchSize, xdmsHomePttId, true, persisterTxn, corpInfoDto.getHierarchyId());
            int count = sublistInfoUtil.getCorpSublistCount(corpId, 1, xdmsHomePttId, true, persisterTxn,corpInfoDto.getHierarchyId());
            respDTO.setCount(String.valueOf(count));
            respDTO.setSublistList(sublistList);
            populate(respDTO);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while getAllSublist - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while getAllSublist- ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }

    public KnCorpSublistDistributionRespDTO getDistributionList(KnIPCorpSublistDistDTO distributionInfoDto, KnPersisterTxn persisterTxn) {
        String methodName = "getDistributionList(distributionInfoDto)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", distributionInfoDto);

        //Step:
        //creating Response DTO object
        KnCorpSublistDistributionRespDTO respDTO = new KnCorpSublistDistributionRespDTO();
        try {
            int corpId = distributionInfoDto.getCorpId();

            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();

            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = distributionInfoDto.getCustomParamMap();
            if (distributionInfoDto.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                List<Integer> sublistIdList = new ArrayList<Integer>();
                sublistIdList.add(distributionInfoDto.getSublistId());
                KnCorpSublistInfoUtil knCorpSublistInfoUtil = new KnCorpSublistInfoUtil();
                knCorpSublistInfoUtil.validateSublistParams(sublistIdList,customParams, xdmsHomePttId, persisterTxn);
                distributionInfoDto.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.GET_DISTRIBUTION_LIST);
                hookIPDTO.setData(distributionInfoDto);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            KnCorpSublistDTO sublistDTO = sublistInfoUtil.getSublistInfo(distributionInfoDto.getSublistId(),
                    corpId, xdmsHomePttId, true, persisterTxn);

            KnSublistDetailsPersistDTO sublistDetailsDTO = new KnSublistDetailsPersistDTO();
            sublistDetailsDTO.setSublistType(sublistDTO.getSublistType());
            sublistDetailsDTO.setInputDTO(distributionInfoDto);
            sublistDetailsDTO.setSublistId(sublistDTO.getSublistId());
            knLogger.debug(methodName, "Invoking Validation FW - ", sublistDetailsDTO);
            validatorFW.validate(sublistDetailsDTO);
            knLogger.debug(methodName, "Validation completed Successfully");
            knLogger.debug(methodName, " distributionInfoDto.getFilterType()-->", distributionInfoDto.getFilterType());

            respDTO = sublistInfoUtil.getDistributionList(sublistDTO.getSublistId(),
                    distributionInfoDto.getFilterType(),
                    corpProfile.getMaxContactsPerSubsc(), corpProfile.getMaxMemPerCorpGroup(), corpId,
                    xdmsHomePttId, true, persisterTxn);
            
            Collection<KnCorpContactDTO> contactList = respDTO.getContactList();
            List <String> profileMdns= new ArrayList<String>();
            if(contactList!=null&&!contactList.isEmpty())
			{
				for (KnCorpContactDTO knCorpContactDTO : contactList) {
					if (!KnConstants.USEPROFILEINDEX.equals(knCorpContactDTO.getUserProfileIndex())) {
						profileMdns.add(knCorpContactDTO.getMdn());
					}
				}
				if (!profileMdns.isEmpty()) {
                    Map<String, String> baseMdnProfileMdnMap = corpSubsProvInfoUtil.getProfileMdnBaseMdnMap(profileMdns,
                            xdmsHomePttId, true, persisterTxn);
					knLogger.debug(methodName, " baseMdnProfileMdnMap-->", KnGDPRTemplate.mdnMap(baseMdnProfileMdnMap));

					for (KnCorpContactDTO knCorpContactDTO : contactList) {
						if (baseMdnProfileMdnMap.containsKey(knCorpContactDTO.getMdn())) {
							knCorpContactDTO.setMdn(baseMdnProfileMdnMap.get(knCorpContactDTO.getMdn()));
						}
					}
					respDTO.setContactList(contactList);
				}
			}
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while getSublistDetails - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while getting the distribution list - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while getting the distribution list - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }

    public KnCorpResponseDTO deleteSublistOperation(KnIPCorpSublistDTO sublistDTO, String xdmsHomePttId,
                                                    KnCorpInOutParamDTO corpInOutParamDTO, String linkedGWKey,
                                                    KnPersisterTxn persisterTxn, KnCorpProfileDTO corpProfile) throws KnBOException,
            KnDAOException, KnCorpBOValidationException {
        String methodName = "deleteSublistOperation(KnIPCorpSublistDTO , String , KnPersisterTxn)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        int sublistId = sublistDTO.getSublistId();
        int corpId = sublistDTO.getCorpId();
        Map<Integer, Integer> subListForProfileUpdate = new HashMap<>();
        subListForProfileUpdate.put(sublistId, KnConstants.CB_MAPPING.NEED_TO_REMOVE.value());
        Collection<String> mdnList = sublistInfoUtil.getSubscribersDistToSublist(sublistId, xdmsHomePttId, persisterTxn);

        Collection<KnCorpGroupInfoPersistDTO> groupDTOLst = sublistInfoUtil.getGroupsMappedToSublist(sublistId, xdmsHomePttId, persisterTxn);

        Collection<Integer> groupIdLst = new ArrayList<Integer>();
        Collection<Integer> dispatchGroup = new ArrayList<Integer>();
        HashMap<Integer, String> groupNameMap = new HashMap<Integer, String>();
        //determining the private list members
        List<Integer> privateGrpListIds = new ArrayList<Integer>();
        Map<Integer, KnCorpGroupInfoPersistDTO> largeGroupMap = new HashMap<>();
        Collection<String> disabledLocWatcher = new ArrayList<>();
        Map<Integer, Collection<String>> removedLocWatcherMap = new HashMap<>();
        Map<Integer, Integer> groupMapListForProfileUpdate = new HashMap<>();
        for (KnCorpGroupInfoPersistDTO grpDTO : groupDTOLst) {
            if(grpDTO.isLargeGroup()){
                largeGroupMap.put(grpDTO.getGroupId(), grpDTO);
            }
            groupNameMap.put(grpDTO.getGroupId(), corpId + "_" + grpDTO.getGroupDisplayName());
            groupIdLst.add(grpDTO.getGroupId());
            groupMapListForProfileUpdate.put(grpDTO.getGroupId(), KnConstants.CB_MAPPING.ETAG_CHANGE.value());
            int grpType = grpDTO.getGroupType();
            if (grpType == KnConstants.DISPATCH_GROUP) {
                dispatchGroup.add(grpDTO.getGroupId());
            }
            privateGrpListIds.add(grpDTO.getGroupMemberListId());

            Collection<KnCorpGroupMemberDTO> grpMemberDetails = groupInfoUtil.getCorpGroupMembersList(groupIdLst, xdmsHomePttId, persisterTxn);
            Collection<String> totalMemMdn = new ArrayList<>();
            if (!grpMemberDetails.isEmpty()) {
                for (KnCorpGroupMemberDTO groupMemberDTO : grpMemberDetails) {
                    totalMemMdn.add(groupMemberDTO.getMdn());
                    if(groupMemberDTO.getLocWatcher() == ENABLED){
                        Collection<String> removedLocWatchers = null;
                        if(removedLocWatcherMap.get(groupMemberDTO.getGroupId()) != null){
                            removedLocWatchers = removedLocWatcherMap.get(groupMemberDTO.getGroupId());
                            removedLocWatchers.add(groupMemberDTO.getMdn());
                        } else {
                            removedLocWatchers = new ArrayList<>();
                            removedLocWatchers.add(groupMemberDTO.getMdn());
                            removedLocWatcherMap.put(groupMemberDTO.getGroupId(), removedLocWatchers);
                        }
                    }
                }
            }
            disabledLocWatcher.addAll(totalMemMdn);
        }
        //get the private list members as client 8 will  be present only in the private list
        List<String> privateLstMdns = sublistInfoUtil.getSublistMembers(privateGrpListIds, xdmsHomePttId, persisterTxn);
        List<String> grpMdns = contactInfoUtil.getGroupMdns(privateLstMdns, corpId, xdmsHomePttId, persisterTxn);
        List<Integer> sublistIds=new ArrayList<>();
        sublistIds.add(sublistId);
        List<String> sublistMemberMdns = sublistInfoUtil.getSublistMembers(sublistIds, xdmsHomePttId, persisterTxn);

        KnIPCorpSublistInfoDTO sublistInputDTO = new KnIPCorpSublistInfoDTO();
        sublistInputDTO.setSublistId(sublistId);
        // determine sublist members
        Collection<KnCorpSubscriberDTO> sublistMembers = sublistInfoUtil.getSublistContactList(sublistInputDTO, xdmsHomePttId, false,
                persisterTxn);

        // remove sublist members
        sublistInfoUtil.deleteAllSublistMembers(sublistId, xdmsHomePttId, persisterTxn);
        Map<String, KnOPDirChgDTO> eTags = new HashMap<String, KnOPDirChgDTO>();
        LinkedList<String> removedMebers = new LinkedList<String>();
        for (KnCorpSubscriberDTO subsc : sublistMembers) {
            removedMebers.add(subsc.getMdn());
        }
        LinkedHashMap<String, LinkedList<String>> subscDelContList = new LinkedHashMap<String, LinkedList<String>>();
        LinkedHashMap<String, LinkedList<Integer>> delContactStatus = null;
        if (mdnList != null && !mdnList.isEmpty()) {
            // Delete the members from the CorpcontactList table
            if (!removedMebers.isEmpty()) {
                for (String mdn : mdnList) {
                    subscDelContList.put(mdn, removedMebers);
                }
                delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(subscDelContList, xdmsHomePttId, persisterTxn);
            }
            eTags = contactInfoUtil.updateSubcribersResourceListIndexDoc(mdnList, xdmsHomePttId, null, persisterTxn);
        }

//        Map<Integer, Integer> groupEtagMap = new HashMap<Integer, Integer>();
//        Map<Integer, Collection<String>> currentMembers = new HashMap<Integer, Collection<String>>();
        LinkedHashMap<Integer, LinkedList<String>> memToBeDelFromGrp = new LinkedHashMap<Integer, LinkedList<String>>();
        LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus = new LinkedHashMap<Integer, LinkedList<Integer>>();
        //Variable to hold the group distribution list before modification.
        Map<Integer, Collection<String>> initialGrpDistributionMap;
        //Variable to hold the group distribution list after modification.
        Map<Integer, Collection<String>> finalGrpDistributionMap = new HashMap<>();
        //This is to hold the group id and member Map for the deleted groups because of member less than 2.
        Map<Integer, Collection<String>> deletedMembersMap = new HashMap<>();
        //This is to hold the group id and deleted member list for modifed group ids
        Map<Integer, Collection<String>> actualDeletedMembers = new HashMap<>();
        //This is to hold the group id and existing member list map
        Map<Integer, Collection<String>> modifiedMembersMap = new HashMap<>();
        //This is to hold the group id and EmergencyUser list map
        Map<String, Collection<String>> groupFailurePairingMap = new HashMap<>();
        Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlDetails(groupIdLst, xdmsHomePttId, persisterTxn);
        Map<Integer, Collection<KnCorpAddlTGInfoDTO>> existingAddlTGList = new HashMap<>();
        Map<String, Collection<String>> actualDeletedGroupMemberMap = new HashMap<>();
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
        knLogger.debug(methodName, "existingAddlTGList - Group", existingAddlTGList);
        Collection<KnCorpAddlTGInfoDTO> delAddlTGList = new ArrayList<>();
        if (!groupIdLst.isEmpty()) {
            //Get the initial group distribution before modification
            initialGrpDistributionMap = groupInfoUtil.getGroupSubscriberDistList(groupIdLst, xdmsHomePttId, persisterTxn);
            // Delete the members from the CorpGroupMemberList table
            if (!removedMebers.isEmpty()) {
                // need to modify the entry only if any members are present
                //groupInfoUtil.modifyGroupMemberEntry(groupIdLst, corpId, xdmsHomePttId, persisterTxn);
                for (int groupId : groupIdLst) {
                    memToBeDelFromGrp.put(groupId, removedMebers);
                }
                delGroupMemStatus = groupInfoUtil.deleteCorpGroupMemberList(memToBeDelFromGrp, xdmsHomePttId, persisterTxn);
                Map<Integer, Map<String, Collection<String>>> delGroupMemberMap = new HashMap<Integer, Map<String, Collection<String>>>();
                for (int groupId : groupIdLst) {
                    Map<String, Collection<String>> deletedMap = new HashMap<String, Collection<String>>();
                    deletedMap.put(KnConstants.DELTED_MEMBERS, memToBeDelFromGrp.get(groupId));
                    delGroupMemberMap.put(groupId, deletedMap);
                }
                //DAO calls to insert and delete from the dg.corpgroupdistinfo table
                groupInfoUtil.deleteFrmCorpGroupDistInfo(delGroupMemberMap, xdmsHomePttId, persisterTxn);
            }
            Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIdLst, xdmsHomePttId, persisterTxn);
            //Determine the deleted and the modified groups
            Map<String, HashMap<Integer, String>> groupListMap = groupInfoUtil.getGroupListStatus(groupIdLst, xdmsHomePttId, persisterTxn);
            finalGrpDistributionMap = groupInfoUtil.getGroupSubscriberDistList(groupIdLst, xdmsHomePttId, persisterTxn);
            // Delete corp groups with < 2 members
            Set<Integer> delGroupIdList = new HashSet<>();
            if (groupListMap.get(KnConstants.DELETED) != null) {
                delGroupIdList = groupListMap.get(KnConstants.DELETED).keySet();
                Collection<String> groupIdAsDestination = new ArrayList<>();
                if (!delGroupIdList.isEmpty()) {
                    for (Integer grpId : delGroupIdList) {
                        Collection<String> delMembersList = finalGrpDistributionMap.get(grpId);
                        deletedMembersMap.put(grpId, delMembersList);
                        //for the contact added to update the directory
                        mdnList.addAll(delMembersList);
                        //Removing the deleted groups from the deleted group members and status since we need not send that list to any subscr
                        memToBeDelFromGrp.remove(grpId);
                        delGroupMemStatus.remove(grpId);
                        groupIdAsDestination.add(String.valueOf(grpId));
                        if(existingAddlTGList.get(grpId) != null) delAddlTGList.addAll(existingAddlTGList.get(grpId));
                        groupMapListForProfileUpdate.put(grpId, KnConstants.CB_MAPPING.NEED_TO_REMOVE.value());
                    }
                    Map<String, Collection<String>> emergDestUserMap = corpSubsProvInfoUtil.getEmergDestUserMap(groupIdAsDestination,
                            xdmsHomePttId, persisterTxn);
                    groupFailurePairingMap.putAll(emergDestUserMap);
                }
            }
            Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupIdLst, xdmsHomePttId, persisterTxn);

            actualDeletedGroupMemberMap = KnCorpCommonInfoUtil.getDeletedMembers(memToBeDelFromGrp, delGroupMemStatus);
            Collection<String> finalLocWatcherRemoved = new ArrayList<>();
            actualDeletedGroupMemberMap.forEach((grpId, deletedMem) -> {
                Collection<String> actualLocWatcher = removedLocWatcherMap.get(Integer.parseInt(grpId));
                if(actualLocWatcher != null){
                    finalLocWatcherRemoved.addAll(actualLocWatcher.stream().filter(deletedMem::contains).collect(Collectors.toList()));
                }
            });
            respDTO.setRemovedLocWatcherList(finalLocWatcherRemoved);
            // Additional Talk Group:
            if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                Map<String, Collection<String>> finalActualDeletedGroupMemberMap = actualDeletedGroupMemberMap;
                delAddlTGList.addAll(subsAddlTGList.stream().filter(addlMem -> (finalActualDeletedGroupMemberMap
                        .get(String.valueOf(addlMem.getGroupId())) != null) && finalActualDeletedGroupMemberMap
                        .get(String.valueOf(addlMem.getGroupId())).contains(addlMem.getMdn())).collect(Collectors.toList()));
                Collection<String> mdnForAddlTg = subsAddlTGList.stream().map(KnCorpAddlTGInfoDTO::getMdn).distinct().collect(Collectors.toList());
                groupInfoUtil.deleteSubsAddlTGList(delAddlTGList, xdmsHomePttId, persisterTxn);
                subsAddlTGList.removeAll(delAddlTGList);
                subsAddlTGList.forEach(subsAddl -> {
                    if(groupDetailsMap.get(subsAddl.getGroupId()) != null) subsAddl.setGroupMemCount(groupDetailsMap.get(subsAddl.getGroupId()).getGroupMemCount());
                });
                eTags = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHomePttId, mdnForAddlTg, persisterTxn, eTags);
                knLogger.debug(methodName, "etagMap AddlTG - ", KnGDPRTemplate.mapKeyMdn(eTags));
                Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
                if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                    groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsAddlTGList, xdmsHomePttId, persisterTxn);
                }
                knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
                if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                    eTags = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(eTags, null, subsAddlTGList, delAddlTGList, groupCorpIdMap);
                    knLogger.debug(methodName, "etagMap AddlTG -diff - ", KnGDPRTemplate.mapKeyMdn(eTags));
                }
            }
            if (!delGroupIdList.isEmpty()) {
                if (HIERARCHY_TYPE.HIERARCHY == sublistDTO.getHierarchyType()) {
                    ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
                    corpXdmDao.deleteAllGrpHierarchy(delGroupIdList, persisterTxn);
                }
                groupInfoUtil.deleteAllGroups(delGroupIdList, xdmsHomePttId, corpInOutParamDTO, persisterTxn);
            }
            eTags = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, eTags, xdmsHomePttId, persisterTxn);
            eTags = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, eTags, xdmsHomePttId, persisterTxn);
            if (!isObjectNull(sublistMembers) && !sublistMembers.isEmpty()) {
                actualDeletedMembers = commonInfoUtil.filterDeletedGroupMembers(finalGrpDistributionMap, initialGrpDistributionMap);
                Collection<Collection<String>> actualDelMemList = actualDeletedMembers.values();
                Set<String> delMemList = new HashSet<String>();
                for (Collection<String> memberList : actualDelMemList) {
                    delMemList.addAll(memberList);
                }
                eTags = contactInfoUtil.updateDistinctSubcribersDirectory(delMemList, null, eTags, xdmsHomePttId, persisterTxn);

                /**
                 * actualDeletedMembers variable holds the actual deleted internal members from the groups.
                 * So de-assigning these members from groupids.
                 */
                Map<Integer, List<String>> deCampedGrpMemMap = commonInfoUtil.prepareGrpMemPam(actualDeletedMembers);
                Map<String, Integer> subscTgscEtag = corpInOutParamDTO.getMdnTgscEtag();
                groupInfoUtil.cleanUpCampedGrp(deCampedGrpMemMap, corpInOutParamDTO, xdmsHomePttId, persisterTxn);
                Map<String, Integer> subscTgscEtag1 = corpInOutParamDTO.getMdnTgscEtag();
                if (subscTgscEtag1 == null) {
                    subscTgscEtag1 = new HashMap<String, Integer>();
                }
                if (subscTgscEtag != null) {
                    subscTgscEtag1.putAll(subscTgscEtag);
                }
                corpInOutParamDTO.setMdnTgscEtag(subscTgscEtag1);
                eTags = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, eTags, xdmsHomePttId, persisterTxn);
            }
            if (groupListMap != null && groupListMap.get(KnConstants.MODIFIED) != null) {
                Set<Integer> modGroupIdList = groupListMap.get(KnConstants.MODIFIED).keySet();
                if (!modGroupIdList.isEmpty()) {
                    for (Integer grpId : modGroupIdList) {
                        Collection<String> modMembersList = finalGrpDistributionMap.get(grpId);
                        modifiedMembersMap.put(grpId, modMembersList);
                        //for the contact added to update the directory
                        mdnList.addAll(modMembersList);
                    }
                }
            }
            //Updating the group member count table
            Collection<Integer> sublistList = new ArrayList<>();
            sublistList.add(sublistId);
            sublistInfoUtil.updateSublistsSubscribersContactCount(sublistList, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                    MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn, MAX_LIMIT_VALIDATION_NOT_REQUIRED);
            //Get the group member count and group type for the groupIds and set these values into etag Map
            respDTO.setDisabledDispatchMemList(removedMebers);
            if (!deletedMembersMap.isEmpty()) {
                commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                        com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), eTags, groupDetailsMap, null);
            }
            if (!actualDeletedMembers.isEmpty()) {
                commonInfoUtil.formSubscriberNotification(actualDeletedMembers, groupEtagMap,
                        com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), eTags, groupDetailsMap, null);
            }
            if (!modifiedMembersMap.isEmpty()) {
                Map<Integer, Integer> nonLargeGrpEtagMap = new HashMap<>();
                for(Integer grpId : groupEtagMap.keySet()){
                    if(!largeGroupMap.containsKey(grpId)){
                        nonLargeGrpEtagMap.put(grpId, groupEtagMap.get(grpId));
                    }
                }
                commonInfoUtil.formSubscriberNotification(modifiedMembersMap,
                        nonLargeGrpEtagMap, com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), eTags, groupDetailsMap, null);
            }

            Map<Integer, Integer> memberCountMap = groupInfoUtil.getGroupMemCount(largeGroupMap.keySet(), xdmsHomePttId, persisterTxn);
            Map<Integer, Integer> isLargeGrpChangedMap = new HashMap<>();
            for (Map.Entry<Integer, KnCorpGroupInfoPersistDTO> entry : largeGroupMap.entrySet()) {
                KnCorpGroupInfoPersistDTO grpInfo = entry.getValue();
                Integer currentCount = memberCountMap.get(grpInfo.getGroupId());
                //UCSPROVCONFIG-8968
                if (currentCount != null) {
                    int maxMembers = switch (grpInfo.getGroupType()) {
                        case BROADCAST_GROUP -> corpProfile.getMaxMemPerBCGrp();
                        case STANDARD_GROUP -> corpProfile.getMaxMemPerCorpGroup();
                        case DISPATCH_GROUP -> corpProfile.getMaxMembersPerDispatchGroup();
                        default -> 0;
                    };
                    if (maxMembers >= currentCount) {
                        isLargeGrpChangedMap.put(grpInfo.getGroupId(), 0);
                    }
                }
            }

            if(!isLargeGrpChangedMap.isEmpty()){
                groupInfoUtil.updateIsLargeGrpFlag(isLargeGrpChangedMap, xdmsHomePttId, persisterTxn);
            }
        }
        sublistInfoUtil.deleteSublistRefAndSublist(sublistId, xdmsHomePttId, persisterTxn);
        if (mdnList != null && !mdnList.isEmpty()) {
            eTags = contactInfoUtil.updateDistinctSubcribersDirectory(mdnList, null, eTags, xdmsHomePttId, persisterTxn);
            contactInfoUtil.updateSubscribersContactCount(mdnList, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
            if(!largeGroupMap.isEmpty()){
                Set<String> mdnSet = new HashSet<>(eTags.keySet());
                for(String mdn : mdnSet){
                    KnOPDirChgDTO chgDTO = eTags.get(mdn);
                    if(chgDTO.getDocChgDTO() == null || chgDTO.getDocChgDTO().isEmpty()){
                        eTags.remove(mdn);
                    }
                }
            }
        }
        eTags = KnCorpCommonInfoUtil.formXcapDiffNotification(eTags, null, subscDelContList, null,
                null, memToBeDelFromGrp, null, null, finalGrpDistributionMap, delContactStatus, delGroupMemStatus, null);
        knLogger.debug(methodName, "etagMap ", KnGDPRTemplate.mapKeyMdn(eTags));
        //Contact:
        Map<String, Collection<String>> actualDeletedContactMap = KnCorpCommonInfoUtil.getActualDeletedContacts(subscDelContList, delContactStatus);
        if(actualDeletedContactMap != null && !actualDeletedContactMap.isEmpty()){
            Map<String, Collection<String>> emergUserDestMap = corpSubsProvInfoUtil.getEmergUserDestMap(actualDeletedContactMap.keySet(),
                    xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "emergUserDestMap-- ", KnGDPRTemplate.mapKeyValueListMdn(emergUserDestMap));
            Collection<String> finalFailedRemovedContacts = new ArrayList<>();
            Map<String, Collection<String>> contactFailurePairingMap = new HashMap<>();
            if (emergUserDestMap != null && !emergUserDestMap.isEmpty()) {
                emergUserDestMap.forEach((emergUser, emergDestinations) -> {
                    Collection<String> removedContacts = actualDeletedContactMap.get(emergUser);
                    Collection<String> mappedDestination = emergDestinations.stream().filter(removedContacts::contains).collect(Collectors.toList());
                    finalFailedRemovedContacts.addAll(mappedDestination);
                    if(!mappedDestination.isEmpty()) contactFailurePairingMap.put(emergUser, mappedDestination);
                });
            }
            knLogger.debug(methodName, "contactFailurePairingMap-- ", contactFailurePairingMap);
            if(!contactFailurePairingMap.isEmpty()){
                knLogger.error(methodName, "Emergency Destination Mapping Exists - Contact");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGENCY_DESTINATION_MAPPING_EXISTS_FOR_CONTACT,
                        "Emergency Destination Mapping Exists for contacts--", CORP_SUBLIST_MANAGER,
                        DELETE_SUBLIST, "", Arrays.asList(finalFailedRemovedContacts).toString(), "");
            }
        }
        //Group:
        if (actualDeletedGroupMemberMap != null && !actualDeletedGroupMemberMap.isEmpty()) {
            Map<String, Collection<String>> emergDestUserMap = corpSubsProvInfoUtil.getEmergDestUserMap(actualDeletedGroupMemberMap.keySet(),
                    xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "emergDestUserMap-- ", emergDestUserMap);
            if (emergDestUserMap != null && !emergDestUserMap.isEmpty()) {
                Map<String, Collection<String>> finalActualDeletedGroupMemberMap1 = actualDeletedGroupMemberMap;
                emergDestUserMap.forEach((emergDest, emergUser) -> {
                    Collection<String> removedGroupMember = finalActualDeletedGroupMemberMap1.get(emergDest);
                    Collection<String> mappedDestination = emergUser.stream().filter(removedGroupMember::contains).collect(Collectors.toList());
                    if (mappedDestination != null && !mappedDestination.isEmpty())
                        groupFailurePairingMap.put(emergDest, mappedDestination);
                });
            }
            knLogger.debug(methodName, "groupFailurePairingMap-- ", groupFailurePairingMap);
        }
        if(!groupFailurePairingMap.isEmpty()){
            knLogger.error(methodName, "Emergency Destination Mapping Exists - Group");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGENCY_DESTINATION_MAPPING_EXISTS_FOR_GROUP,
                    "Emergency Destination Mapping Exists for Group--", CORP_SUBLIST_MANAGER,
                    DELETE_SUBLIST, "", groupFailurePairingMap.keySet().toString(), "");
        }
        eTags = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, eTags);
        knLogger.debug(methodName, "etagMap after auth mapping modification- ", KnGDPRTemplate.mapKeyMdn(eTags));
        Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = corpUserProfileUtil.updateImpactedCBDocuments(String.valueOf(corpId), subListForProfileUpdate,
                groupMapListForProfileUpdate,null,sublistMemberMdns,xdmsHomePttId, persisterTxn);
        respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
        if(profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()){
            respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
        }
        LinkedList<KnLIEventDTO> liEventList = KnCorpCommonInfoUtil.populateLIData(eTags
                , xdmsHomePttId, groupNameMap, finalGrpDistributionMap, finalGrpDistributionMap,persisterTxn);
        respDTO.setLiEventList(liEventList);
        for (String grpMdn : grpMdns) {
            eTags.remove(grpMdn);
        }
        respDTO.setChangeLogMap(eTags);
        if (mdnList != null) {
            //check if any of the subscrber is a dispatcher
            boolean isDispmemExist = contactInfoUtil.isDispatchMemberPresent(new ArrayList<String>(mdnList), xdmsHomePttId, persisterTxn);
            //remove the group mdns
            removedMebers.removeAll(grpMdns);
            if (isDispmemExist) {
                respDTO.setDisabledDispatchMemList(removedMebers);
            }
        }
        Collection<String> disabledMember = new HashSet<>();
        if(respDTO.getDisabledDispatchMemList() != null){
            disabledMember.addAll(respDTO.getDisabledDispatchMemList());
        }
        disabledMember.addAll(disabledLocWatcher);
        respDTO.setDisabledDispatchMemList(disabledMember);
        /*if(!grpMdns.isEmpty()){
            if (linkedGWKey != null ) {
                //get the refernce id from the account info table.
                KnCorpGWLinkedAccountInfoDTO accountInfo = commonInfoUtil.getCorporateLinkedAccountInfo(linkedGWKey, xdmsHomePttId, persisterTxn);
                //update etag in the account info table
                //update etag
                Map<String, String> asyncInput = new HashMap<>();
                asyncInput.put(KnDbSyncFwConstants.PTTSERVER_ID, xdmsHomePttId);
                asyncInput.put(KnDbSyncFwConstants.NNI_REF_ID, accountInfo.getNniRefId());
                knLogger.debug(methodName, " Async inputs", asyncInput);
                KnSqlJobCollector collector = KnSqlJobCollector.getInstance();
                int serviceType = KnDbSyncFwConstants.EXECUTOR.ETAG_UPDATE_NNI.value();
                collector.collect(serviceType, asyncInput);

            }
        }*/
        return respDTO;
    }

    /**
     * This method is to retrieve the list of sublists where the request MDN exist as member.
     * 1. Validate if corporate exist.
     * 2. Validate if request MDN is part of corporation (internal or external)
     * If validation successfull
     * 3. Retrieve the list of sublist where MDN exist from DG.CORPLISTMEMBER table.
     * 4. Filter the sublist Ids based on corpid and list type from DG.CORPLISTINFO
     * Also retrieve the sublist details and return the sublists.
     *
     * @param contactDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnCorpSublistListRespDTO getSubscrSublists(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getSubscrSublists()";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactDTO.getMdn(), " for CorpId - ", contactDTO.getCorpId());

        //Step:
        //creating Response DTO object
        KnCorpSublistListRespDTO respDTO = new KnCorpSublistListRespDTO();
        try {

            int corpId = contactDTO.getCorpId();
            String subscrMdn = contactDTO.getMdn();

            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile =
                    commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);
            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();
            Map<String, Object> customParams = contactDTO.getCustomParamMap();
            if (contactDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(subscrMdn), customParams, xdmsHomePttId, persisterTxn);
            }
            //get the Subscriber profile details from cache
            int subscribersCount = contactInfoUtil.getSubscribersCount(subscrMdn, corpId, true, xdmsHomePttId, persisterTxn);
            boolean isSubscrExternalCont = false;
            if (subscribersCount < 1) {
                isSubscrExternalCont = contactInfoUtil.isExternalContExist(subscrMdn, corpId, xdmsHomePttId, persisterTxn);
            }
            KnReverseContactPersistDto valPersistDTO = new KnReverseContactPersistDto();
            valPersistDTO.setSubscriberCount(subscribersCount);
            valPersistDTO.setExternalContact(isSubscrExternalCont);
            valPersistDTO.setInputDTO(contactDTO);
            knLogger.debug(methodName, "Invoking Validation FW - ", valPersistDTO);
            validatorFW.validate(valPersistDTO);
            knLogger.debug(methodName, "Validation completed successfully");
            knLogger.debug(methodName, "Before DB call to get the subscribers contact List.");
            List<Integer> sublistIds = sublistInfoUtil.getSubscrAllSublists(subscrMdn, xdmsHomePttId, true, persisterTxn);
            Map<Integer, KnCorpSublistDTO> filteredSublistsMap = new HashMap<>();
            if (sublistIds.size() > 0) {
                filteredSublistsMap = sublistInfoUtil.filterSublists(sublistIds, corpId, KnConstants.SUBLIST_TYPE_SHARED_LIST, xdmsHomePttId, true, persisterTxn);
            }
            knLogger.debug(methodName, "After DB call filteredSublistsMap", filteredSublistsMap);

            respDTO.setSublistList(filteredSublistsMap.values());
            populate(respDTO);

        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured in getSubscrSublists - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while getSubscrSublists - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while getSubscrSublists- ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }

    /**
     * Method to remove the subscriber from all the shared sublist where he is a member.
     * The delete would happen 10 sublist at a time.If there are more sublist he is part of the
     * response message will indicate that there are more sublist to be deleted.
     * 1. Validate if corporate exist.
     * 2. Validate if request MDN is part of corporation (internal)
     * If validation successful:
     * 3. Determine 11 Sublist for the Subscriber, if 11 sublist are found that means the operation to be re-executed.
     * 4. Get the List of subscribers the sublist are distributed to AND Get the list of groups the sublist is distributed to.
     * 5. Remove the mdn from the corpListMember table for all the 10 sublist.
     * 6. Update the etag of all the 10 sublist.
     * 7. Delete the member from the corp contact list table for the subscribers obtained in point no 4 only if the
     * member is not coming from any other shared sublist.
     * 8. Update the CorpResourceListEtag appropriately for the subscriber.
     * 9. Determine the member where group is distributed.
     * 10. Delete the member from the group member list table for the groupIds.
     * 11. Delete the member from Distribution table, the empty groups have to deleted as well.
     * 12. Update the etag of group ids, call the SP ti update the count tables of group and groups.
     * 13. Update the directory etags.
     * 14. Send the XCAP notifications for the members on the resource list and the group change. Diff would be sent if
     * the member is actually deleted else the document notification will be sent.
     * 15. If there were 11 sublist then the operation has to be re-excuted so we will have to return an indicator to the
     * web isSublistExists.
     *
     * @param subscriberInfoDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnCorpResponseDTO removeSubscribersAllSublist(KnIPCorpContactDTO subscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "removeSubscribersAllSublist(subscriberInfoDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", subscriberInfoDTO.getMdn(), " for CorpId - ", subscriberInfoDTO.getCorpId());
        //Step: creating Response DTO object
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpInOutParamDTO corpInOutParamDTO = new KnCorpInOutParamDTO();
        Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
        try {
            int corpId = subscriberInfoDTO.getCorpId();
            //Step:get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);
            //Step: IDTYPEget the Subscriber profile details from cache
            knLogger.debug(methodName, "Fetch the subscProfile profile if cached or fetch from the DB the details");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(subscriberInfoDTO.getMdn(),
                    KnProfileTypes.PUBLIC_PROFILE, true, persisterTxn);
            knLogger.info(methodName, "subscProfile Profile - ", subscProfile);
            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();

            //Invoking custom hook
            knLogger.debug(methodName, "Custom Call");
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = subscriberInfoDTO.getCustomParamMap();
            if (subscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                knLogger.debug(methodName, "customParams after the change - ", customParams);
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(subscriberInfoDTO.getMdn()), customParams, xdmsHomePttId, persisterTxn);
                subscriberInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.REMOVE_SUBSCRIBERS_ALL_SUBLIST);
                hookIPDTO.setData(subscriberInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                knLogger.debug(methodName, "Call before Hook.Process Invoker - ", processInvoker);
                Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    knLogger.debug(methodName, "Call After Hook");
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            int privateListId = subscProfile.getContactListId();
            String mdn = subscriberInfoDTO.getMdn();
            LinkedList<String> mdnList = new LinkedList<String>();
            mdnList.add(subscriberInfoDTO.getMdn());
            KnContactDetailsPersistDTO valPersistDTO = new KnContactDetailsPersistDTO();
            valPersistDTO.setInputDTO(subscriberInfoDTO);
            KnCorpSubscriberDTO subsDTO = new KnCorpSubscriberDTO();
            subsDTO.setMdn(mdn);
            subsDTO.setCorpId(corpId);
            valPersistDTO.setSubscDto(subsDTO);
            valPersistDTO.setSubsCorpId(subscProfile.getCorpId());
            knLogger.debug(methodName, "After setting of the validation framework object ", "valPersistDTO - ", valPersistDTO);
            knLogger.info(methodName, "Invoking Validation FW - ", valPersistDTO);
            validatorFW.validate(valPersistDTO);
            knLogger.debug(methodName, "Validation completed successfully");
            Collection<Integer> modifiedSublistIdsinLoop = new ArrayList<>();
            LinkedList<Integer> modifiedSublistIds = new LinkedList<>();
            // Get the list of sublist ids are distributed to requestMDN.(Also total count of sublist pushed to requestMDN).
            knLogger.debug(methodName, "privateListId", privateListId);
            LinkedList<Integer> sublistPushedToSubscribers = sublistInfoUtil.sublistPushedToSubscribers(mdn, corpId, xdmsHomePttId,
                    persisterTxn);

            Map<Integer, Integer> sublistPushedToSubscribersAndCount = sublistInfoUtil.sublistPushedToSubscribersAndCount(sublistPushedToSubscribers, xdmsHomePttId,
                    persisterTxn);
            Map<Integer, Integer> subListForProfileUpdate = new HashMap<>();
            Map<Integer, Integer> groupMapListForProfileUpdate = new HashMap<>();
            knLogger.debug(methodName, "sublistPushedToSubscribersAndCount", sublistPushedToSubscribersAndCount);
            for (Map.Entry<Integer, Integer> entry : sublistPushedToSubscribersAndCount.entrySet()) {
                int sublistId = entry.getKey();
                int sublistMemberCount = entry.getValue();
                if (sublistMemberCount == 1) {
                    knLogger.debug(methodName, "Sublist will get deleted as the sublist member Count is becoming zero");
                    KnIPCorpSublistDTO sublistDTO = new KnIPCorpSublistDTO();
                    sublistDTO.setCorpId(corpId);
                    sublistDTO.setSublistId(sublistId);
                    respDTO = deleteSublistOperation(sublistDTO, xdmsHomePttId, corpInOutParamDTO, corpProfile.getLinkedGwKey(), persisterTxn, corpProfile);
                    Map<String, KnOPDirChgDTO> loopEtag = respDTO.getChangeLogMap();
                    for (String sMdn : loopEtag.keySet()) {
                        KnOPDirChgDTO directory = loopEtag.get(sMdn);
                        KnOPDirChgDTO initialDir = etagMap.get(sMdn);
                        if (initialDir == null) {
                            etagMap.put(sMdn, directory);
                        } else {
                            Collection<KnOPDocChgDTO> loopDocs = directory.getDocChgDTO();
                            if (loopDocs != null) {
                                Collection<KnOPDocChgDTO> exisitingDocs = initialDir.getDocChgDTO();
                                exisitingDocs.addAll(loopDocs);
                                directory.setDocChgDTO(exisitingDocs);
                            }
                           etagMap.put(sMdn, directory);
                        }
                    }
                }else{
                    modifiedSublistIds.add(sublistId);
                    subListForProfileUpdate.put(sublistId, KnConstants.CB_MAPPING.ETAG_CHANGE.value());
                }
            }

            // todo the null check of the subscriber
            knLogger.debug(methodName, "modifiedSublistIds", modifiedSublistIds);
            int totalSublistSize = modifiedSublistIds.size();
            if (totalSublistSize > MAX_SUBLIST_SIZE) {
                respDTO.setIsSublistExists(Boolean.TRUE);
                modifiedSublistIdsinLoop = modifiedSublistIds.subList(0, MAX_SUBLIST_SIZE);
            }else {
                modifiedSublistIdsinLoop = modifiedSublistIds.subList(0, modifiedSublistIds.size());
            }

            knLogger.debug(methodName, "modifiedSublistIds", modifiedSublistIds);
            // Fetching the list of Subscribers the sublist are distributed to.
            LinkedHashSet<String> distributionToMultipleSublist = sublistInfoUtil.selectSubsDistributionToMultipleSublist(modifiedSublistIdsinLoop, mdn, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "distributionToMultipleSublist", KnGDPRTemplate.mdnList(distributionToMultipleSublist));
               /* KnCorpSubscriberDTO corpSubsDto = new KnCorpSubscriberDTO();
                corpSubsDto.setMdn(subscriberInfoDTO.getMdn());*/
            // Get the list of groups the sublist is distributed to.
            Set<Integer> groupsPushedToSublists = groupInfoUtil.groupsPushedToSublists(modifiedSublistIdsinLoop, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "groupsPushedToSublists", groupsPushedToSublists);
            //Removing the mdn from the DG.CORPLISTMEMBER table for all the 10 sublist once at a time.
            sublistInfoUtil.deleteSublistMembersForRequestMDN(modifiedSublistIdsinLoop, mdn, xdmsHomePttId, persisterTxn);
            // Updating the etag of 10 sublist.
            sublistInfoUtil.fetchAndUpdateSublistEtag(modifiedSublistIdsinLoop, xdmsHomePttId, persisterTxn);
            LinkedHashMap<String, LinkedList<String>> mdnContactListMap = new LinkedHashMap<String, LinkedList<String>>();
            LinkedHashMap<String, LinkedList<Integer>> delContactStatus = null;

            LinkedList<String> removedContactMdnList = new LinkedList<String>();
            removedContactMdnList.addAll(distributionToMultipleSublist);
            knLogger.debug(methodName, "removedContactMdnList", KnGDPRTemplate.mdnList(removedContactMdnList));
            LinkedList<String> removedRequestMdn = new LinkedList<String>(Arrays.asList(mdn));
            for (String sublistMember : removedContactMdnList) {
                mdnContactListMap.put(sublistMember, removedRequestMdn);
            }
            /*Deleting the member from the corp contact list table for the subscribers obtained in point no 4 only if the
            member is not coming from any other shared sublist.*/
            knLogger.debug(methodName, "mdnContactListMap",  KnGDPRTemplate.mapMdnAsLinkedValue(mdnContactListMap));
            delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(mdnContactListMap, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "delContactStatus", delContactStatus);
            // determine the members where the group is distributed.
            Map<Integer, Collection<String>> groupDistList = groupInfoUtil.getGroupSubscriberDistList(groupsPushedToSublists, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "groupDistList", groupDistList);

            // Broadcaster Group
            Set<Integer> bgGroupList = groupInfoUtil.getGroupSupervisorList(groupsPushedToSublists, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "bgGroupList - ", bgGroupList);
            //Delete the requestMDN from the groupMemberList table for the groupIds and delete the member from the distribution table.
            //The empty group have to deleted as well.
            LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus = new LinkedHashMap<Integer, LinkedList<Integer>>();
            LinkedHashMap<Integer, LinkedList<String>> actualMdnToBeDeletedFromGroupMap = new LinkedHashMap<Integer, LinkedList<String>>();
            Map<Integer, Collection<String>> removedLocWatcherMap = new HashMap<>();
            Collection<Integer> groupIdLst = new ArrayList<>();
            HashSet<String> modifiedMDNList  = new HashSet<>();
            Set<Integer> grpIdsForEtagChange = new HashSet<>();
            Collection<String> disabledOdlMember = new HashSet<>();
            if (groupDistList != null && !groupDistList.isEmpty()) {
                for (Map.Entry<Integer, Collection<String>> entry : groupDistList.entrySet()) {
                    int groupId = entry.getKey();
                    groupMapListForProfileUpdate.put(groupId, KnConstants.CB_MAPPING.ETAG_CHANGE.value());
                    Collection<String> memberMdnList = entry.getValue();
                    for (String memberMDN : memberMdnList) {
                        LinkedList<String> mdnTobeDeleted = new LinkedList<>();
                        if (memberMDN.equals(mdn)) {
                            mdnTobeDeleted.add(memberMDN);
                            groupIdLst.add(groupId);
                            actualMdnToBeDeletedFromGroupMap.put(groupId, mdnTobeDeleted);
                        }
                        modifiedMDNList.add(memberMDN);

                    }
                }
                if(bgGroupList != null && !bgGroupList.isEmpty()){
                    for(int bgGroup : bgGroupList){
                        actualMdnToBeDeletedFromGroupMap.put(bgGroup, mdnList);
                        grpIdsForEtagChange.add(bgGroup);
                        groupIdLst.add(bgGroup);
                    }
                }
                knLogger.debug(methodName, "actualMdnToBeDeletedFromGroupMap", KnGDPRTemplate.mapMdnAsLinkedValue(actualMdnToBeDeletedFromGroupMap));
                Collection<KnCorpGroupMemberDTO> grpMemberDetails = groupInfoUtil.getCorpGroupMemberForLocWatcher(actualMdnToBeDeletedFromGroupMap.keySet(),
                        xdmsHomePttId, persisterTxn);
                for(KnCorpGroupMemberDTO grpMem : grpMemberDetails){
                    disabledOdlMember.add(grpMem.getMdn());
                    if(grpMem.getLocWatcher() == ENABLED){
                        Collection<String> removedLocWatchers = null;
                        if(removedLocWatcherMap.get(grpMem.getGroupId()) != null){
                            removedLocWatchers = removedLocWatcherMap.get(grpMem.getGroupId());
                            removedLocWatchers.add(grpMem.getMdn());
                        } else {
                            removedLocWatchers = new ArrayList<>();
                            removedLocWatchers.add(grpMem.getMdn());
                            removedLocWatcherMap.put(grpMem.getGroupId(), removedLocWatchers);
                        }
                    }
                }
                disabledOdlMember.add(subscriberInfoDTO.getMdn());
                delGroupMemStatus = groupInfoUtil.deleteCorpGroupMemberList(actualMdnToBeDeletedFromGroupMap, xdmsHomePttId, persisterTxn);
                //Deleting from group dist info
               // groupInfoUtil.deleteGrpDistList(mdnList, xdmsHomePttId, persisterTxn);
            }
            Collection<KnCorpAddlTGInfoDTO> subsAddlTGList = groupInfoUtil.getSubsAddlDetails(groupIdLst, xdmsHomePttId, persisterTxn);
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

            //Deleting from group dist info
            Collection<KnCorpSubscriberDTO> removedMembersList = contactInfoUtil.getSubscribersInfo(mdnList, xdmsHomePttId, persisterTxn);
            Collection<KnCorpSubscriberDTO> uniqueMdnToBeDeletedToGroup =
                    contactInfoUtil.getDistinctMembers(removedMembersList, groupIdLst, corpId, xdmsHomePttId, persisterTxn);
            //Filter out the actually added members and deleted members as same member can be already present in the group via sublist etc
            Map<Integer, Map<String, Collection<String>>> memberDetailsList = commonInfoUtil.filterAddedRemovedMembers(groupDistList,
                    null, uniqueMdnToBeDeletedToGroup, corpId);
            knLogger.debug(methodName, "memberDetailsList - ", KnGDPRTemplate.mapMdnOfMap(memberDetailsList));
            //DAO calls to delete from the dg.corpgroupdistinfo table
            for (int grpId : groupIdLst) {
                Collection<String> deletedMembers = (memberDetailsList.get(grpId)).get(KnPersisterConstants.DELTED_MEMBERS);
                if (deletedMembers != null && !deletedMembers.isEmpty()) {
                    groupInfoUtil.deleteFrmCorpGroupDistInfo(memberDetailsList, xdmsHomePttId, persisterTxn);
                }
            }

            //Updating the group member count
            sublistInfoUtil.updateSublistsSubscribersContactCount(modifiedSublistIdsinLoop, corpProfile.getMaxContactsPerSubsc(),
                    corpProfile.getMaxMemPerCorpGroup(), corpProfile.getMaxMembersPerDispatchGroup(), xdmsHomePttId, persisterTxn, corpProfile.getMaxMemPerBCGrp());
//            Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
            HashMap<Integer, String> groupNameMap = new HashMap<Integer, String>();
            Map<String, Collection<String>> groupFailurePairingMap = new HashMap<>();
            Map<String, Collection<String>> actualDeletedGroupMemberMap = new HashMap<>();
            if (!groupIdLst.isEmpty()) {
                Map<Integer, Collection<String>> groupDistListAfterModify = new HashMap<Integer, Collection<String>>();
                //This variable is to hold the deleted members in the deleted groups (less than 1 member group Ids)
                Map<Integer, Collection<String>> deletedMembersMap = new HashMap<Integer, Collection<String>>();
                //This variable is to hold the actilly deleted members from group member list table for each group id.
                Map<Integer, Collection<String>> actualDeletedMembers = new HashMap<>();

                groupDistListAfterModify = groupInfoUtil.getGroupSubscriberDistList(groupIdLst, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "groupDistListAfterModify", groupDistListAfterModify);
                actualDeletedMembers = commonInfoUtil.filterDeletedGroupMembers(groupDistListAfterModify, groupDistList);
                knLogger.debug(methodName, "actualDeletedMembers", actualDeletedMembers);

                for (Map.Entry<Integer, Collection<String>> grpStatusEntry : actualDeletedMembers.entrySet()) {
                    Integer groupId = grpStatusEntry.getKey();
                    Collection<String> memberMdnList = grpStatusEntry.getValue();
                    knLogger.debug(methodName, "memberMdnListTTT - ", KnGDPRTemplate.mdnList(memberMdnList));
                    if (!grpStatusEntry.getValue().isEmpty()) {
                        grpIdsForEtagChange.add(groupId);
                    }
                    knLogger.debug(methodName, "grpIdsForEtagChange - ", grpIdsForEtagChange);

                }

                Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(grpIdsForEtagChange, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "groupEtagMap", groupEtagMap);

                //Determine the deleted and the modified groups
                Map<String, HashMap<Integer, String>> groupListStatus = groupInfoUtil.getGroupListStatus(groupIdLst, xdmsHomePttId, persisterTxn);
                knLogger.debug("groupListStatus-,", KnGDPRTemplate.mapKeyMdn(groupListStatus));
                // Delete corp groups with < 2 members
                actualDeletedGroupMemberMap = KnCorpCommonInfoUtil.getDeletedMembers(actualMdnToBeDeletedFromGroupMap, delGroupMemStatus);
                Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupIdLst, xdmsHomePttId, persisterTxn);
                Set<Integer> delGroupIdList = new HashSet<>();
                if (groupListStatus.get(KnConstants.DELETED) != null && groupListStatus.get(KnConstants.DELETED).keySet() != null) {
                    delGroupIdList = groupListStatus.get(KnConstants.DELETED).keySet();
                    Collection<String> groupIdAsDestination = new ArrayList<>();
                    if (delGroupIdList != null && !delGroupIdList.isEmpty()) {
                        for (Integer grpId : delGroupIdList) {
                            Collection<String> delMembersList = groupDistListAfterModify.get(grpId);
                            deletedMembersMap.put(grpId, delMembersList);
                            groupIdAsDestination.add(String.valueOf(grpId));
                            if(existingAddlTGList.get(grpId) != null) delAddlTGList.addAll(existingAddlTGList.get(grpId));
                            groupMapListForProfileUpdate.put(grpId, KnConstants.CB_MAPPING.NEED_TO_REMOVE.value());
                        }
                        Map<String, Collection<String>> emergDestUserMap = corpSubsProvInfoUtil.getEmergDestUserMap(groupIdAsDestination,
                                xdmsHomePttId, persisterTxn);
                        groupFailurePairingMap.putAll(emergDestUserMap);
                        for (Integer groupId : delGroupIdList) {
                            groupDistListAfterModify.remove(groupId);
                        }
                    }
                }
                //LocWatcher: Removed
                Collection<String> finalLocWatcherRemoved = new ArrayList<>();
                actualDeletedGroupMemberMap.forEach((grpId, deletedMem) -> {
                    Collection<String> actualLocWatcher = removedLocWatcherMap.get(Integer.parseInt(grpId));
                    finalLocWatcherRemoved.addAll(actualLocWatcher.stream().filter(deletedMem::contains).collect(Collectors.toList()));
                });
                respDTO.setRemovedLocWatcherList(finalLocWatcherRemoved);
                // Additional Talk Group:
                if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                    Map<String, Collection<String>> finalActualDeletedGroupMemberMap = actualDeletedGroupMemberMap;
                    delAddlTGList.addAll(subsAddlTGList.stream().filter(addlMem -> (finalActualDeletedGroupMemberMap
                            .get(String.valueOf(addlMem.getGroupId())) != null) && finalActualDeletedGroupMemberMap
                            .get(String.valueOf(addlMem.getGroupId())).contains(addlMem.getMdn())).collect(Collectors.toList()));
                    Collection<String> mdnForAddlTg = subsAddlTGList.stream().map(KnCorpAddlTGInfoDTO::getMdn).distinct().collect(Collectors.toList());
                    groupInfoUtil.deleteSubsAddlTGList(delAddlTGList, xdmsHomePttId, persisterTxn);
                    subsAddlTGList.removeAll(delAddlTGList);
                    subsAddlTGList.forEach(subsAddl -> {
                        if(groupDetailsMap.get(subsAddl.getGroupId()) != null) subsAddl.setGroupMemCount(groupDetailsMap.get(subsAddl.getGroupId()).getGroupMemCount());
                    });
                    etagMap = groupInfoUtil.updateAddlTalkGroupImpactedTables(xdmsHomePttId, mdnForAddlTg, persisterTxn, etagMap);
                    knLogger.debug(methodName, "etagMap AddlTG - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                    Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
                    if (subsAddlTGList != null && !subsAddlTGList.isEmpty()) {
                        groupCorpIdMap = groupInfoUtil.getGroupCorpIdMap((List<KnCorpAddlTGInfoDTO>) subsAddlTGList, xdmsHomePttId, persisterTxn);
                    }
                    knLogger.debug(methodName, "groupCorpId - ", groupCorpIdMap);
                    if (null != groupCorpIdMap && !groupCorpIdMap.isEmpty()) {
                        etagMap = KnCorpCommonInfoUtil.formXcapAddlTGDiffNotification(etagMap, null, subsAddlTGList, delAddlTGList, groupCorpIdMap);
                        knLogger.debug(methodName, "etagMap AddlTG -diff - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                    }
                }
                etagMap = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                groupInfoUtil.deleteAllGroups(delGroupIdList, xdmsHomePttId, corpInOutParamDTO, persisterTxn);
                if (groupListStatus.get(KnConstants.DELETED) != null) {
                    groupNameMap.putAll(groupListStatus.get(KnConstants.DELETED));
                } else {
                    groupNameMap.putAll(groupListStatus.get(KnConstants.MODIFIED));
                }
                //Updating the group member count
                sublistInfoUtil.updateSublistsSubscribersContactCount(modifiedSublistIdsinLoop, corpProfile.getMaxContactsPerSubsc(),
                        corpProfile.getMaxMemPerCorpGroup(), corpProfile.getMaxMembersPerDispatchGroup(), xdmsHomePttId, persisterTxn, corpProfile.getMaxMemPerBCGrp());
                     /*
                        Get the group member count and group type for the groupIds and set these values into etag Map
                     */
                if (!actualDeletedMembers.isEmpty()) {
                    etagMap = commonInfoUtil.formSubscriberNotification(actualDeletedMembers, groupEtagMap,
                            com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                }
                if (!deletedMembersMap.isEmpty()) {
                    etagMap = commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                            com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                }
                if (!groupDistListAfterModify.isEmpty()) {
                    etagMap = commonInfoUtil.formSubscriberNotification(groupDistListAfterModify, groupEtagMap,
                            com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
                }
            }
            etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(modifiedMDNList, groupsPushedToSublists, etagMap, xdmsHomePttId, persisterTxn);
            // Updating the Impacted tables for input MDN.
            Map<String, KnOPDirChgDTO> etagMap1 = contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId, removedContactMdnList, persisterTxn, null);
            etagMap.putAll(etagMap1);
            // Preparing the Notification related with this API.
            etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, mdnContactListMap,
                    null, null, actualMdnToBeDeletedFromGroupMap, null,
                    null, null, delContactStatus, delGroupMemStatus, null);
            //Contact:
            Map<String, Collection<String>> actualDeletedContactMap = KnCorpCommonInfoUtil.getActualDeletedContacts(mdnContactListMap, delContactStatus);
            if(actualDeletedContactMap != null && !actualDeletedContactMap.isEmpty()){
                Map<String, Collection<String>> emergUserDestMap = corpSubsProvInfoUtil.getEmergUserDestMap(actualDeletedContactMap.keySet(),
                        xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "emergUserDestMap-- ", emergUserDestMap);
                Collection<String> finalFailedRemovedContacts = new ArrayList<>();
                Map<String, Collection<String>> contactFailurePairingMap = new HashMap<>();
                if (emergUserDestMap != null && !emergUserDestMap.isEmpty()) {
                    emergUserDestMap.forEach((emergUser, emergDestinations) -> {
                        Collection<String> removedContacts = actualDeletedContactMap.get(emergUser);
                        Collection<String> mappedDestination = emergDestinations.stream().filter(removedContacts::contains).collect(Collectors.toList());
                        finalFailedRemovedContacts.addAll(mappedDestination);
                        if(!mappedDestination.isEmpty()) contactFailurePairingMap.put(emergUser, mappedDestination);
                    });
                }
                knLogger.debug(methodName, "contactFailurePairingMap-- ", contactFailurePairingMap);
                if(!contactFailurePairingMap.isEmpty()){
                    knLogger.error(methodName, "Emergency Destination Mapping Exists - Contact");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGENCY_DESTINATION_MAPPING_EXISTS_FOR_CONTACT,
                            "Emergency Destination Mapping Exists for contacts--", CORP_SUBLIST_MANAGER,
                            MODIFY_SUBLIST, "", Arrays.asList(finalFailedRemovedContacts).toString(), "");
                }
            }
            //Group:
            if(actualDeletedGroupMemberMap != null && !actualDeletedGroupMemberMap.isEmpty()){
                Map<String, Collection<String>> emergDestUserMap = corpSubsProvInfoUtil.getEmergDestUserMap(actualDeletedGroupMemberMap.keySet(),
                        xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "emergDestUserMap-- ", emergDestUserMap);
                if (emergDestUserMap != null && !emergDestUserMap.isEmpty()) {
                    Map<String, Collection<String>> finalActualDeletedGroupMemberMap1 = actualDeletedGroupMemberMap;
                    emergDestUserMap.forEach((emergDest, emergUser) -> {
                        Collection<String> removedGroupMember = finalActualDeletedGroupMemberMap1.get(emergDest);
                        Collection<String> mappedDestination = emergUser.stream().filter(removedGroupMember::contains).collect(Collectors.toList());
                        if(mappedDestination != null && !mappedDestination.isEmpty()) groupFailurePairingMap.put(emergDest, mappedDestination);
                    });
                }
                knLogger.debug(methodName, "groupFailurePairingMap-- ", groupFailurePairingMap);
            }
            if(!groupFailurePairingMap.isEmpty()){
                knLogger.error(methodName, "Emergency Destination Mapping Exists - Group");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGENCY_DESTINATION_MAPPING_EXISTS_FOR_GROUP,
                        "Emergency Destination Mapping Exists for Group--", CORP_SUBLIST_MANAGER,
                        REMOVE_SUBSCRIBERS_ALL_SUBLIST, "", groupFailurePairingMap.keySet().toString(), "");
            }
            knLogger.debug(methodName, "etagMap ", KnGDPRTemplate.mapKeyMdn(etagMap));
            etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
            knLogger.debug(methodName, "etagMap after auth mapping modification- ", KnGDPRTemplate.mapKeyMdn(etagMap));

            Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = corpUserProfileUtil.updateImpactedCBDocuments(String.valueOf(corpId), subListForProfileUpdate,
                    groupMapListForProfileUpdate,null, xdmsHomePttId, persisterTxn);
            Map<String, Collection<KnDocChangeListDTO>> existingMap = null;
            if(respDTO.getProfileMdnEtagMap() != null && !respDTO.getProfileMdnEtagMap().isEmpty()){
                existingMap = respDTO.getProfileMdnEtagMap();
                existingMap.putAll(profileMdnEtagMap);
            } else {
                existingMap = profileMdnEtagMap;
            }
            respDTO.setProfileMdnEtagMap(existingMap);
            if(existingMap != null){
                respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
            }
            //populate Success response
            respDTO.setChangeLogMap(etagMap);

            LinkedList<KnLIEventDTO> liEventList = KnCorpCommonInfoUtil.populateLIData(etagMap
                    , xdmsHomePttId, groupNameMap, null, null,persisterTxn);
            respDTO.setDisabledDispatchMemList(disabledOdlMember);
            respDTO.setLiEventList(liEventList);
            knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
            respDTO.setEtag(String.valueOf(subscriberInfoDTO.getEtag()));
            respDTO.setMdnCorpId(subscProfile.getCorpId());
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }

        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while removing subscribers All Sublists - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while removing subscribers All Sublists - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while removing subscribers All Sublists - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }

    public Object getLastElement(final Collection c) {
        final Iterator itr = c.iterator();
        Object lastElement = itr.next();
        while(itr.hasNext()) {
            lastElement=itr.next();
        }
        return lastElement;
    }

    /**
     * This method will update the auto pairing indicator for the corporate based on the indicationi.e to enable or disable the
     * autopairing.This method will be called by the create subscriber as well i.e the first handset subscriber of the corporation.
     *
     * @param contactDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnCorpAutoPairingResponse updateCorpAutoPairing(KnIPCorpInfoDTO contactDTO, KnPersisterTxn persisterTxn) {
        String methodName = "updateCorpAutoPairing(KnIPCorpInfoDTO, KnPersisterTxn)";
        knLogger.entry(methodName);
        knLogger.info(methodName, "The input provided is :-", contactDTO);
        KnCorpAutoPairingResponse respDTO = new KnCorpAutoPairingResponse();
        //determine the list of hanset subscribers
        //call the internal method which accepts the list of subscribers and the corpId as the parameter.
        try {
            int corpId = contactDTO.getCorpId();
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile =
                    commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);
            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();
            Integer pairedListid = 0;
            if (contactDTO.getEnableAutoPair()) {
                knLogger.info(methodName, "Enable Auto Pairing - ");
                Collection<String> mdnList = contactInfoUtil.getMdnSpecificToClient(corpId, KnConstants.CLIENT_TYPE_HANDSET, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "Handeset subcribers list size - ", mdnList.size());
                KnCorpMdnListPersistDTO persistDTO = new KnCorpMdnListPersistDTO();
                persistDTO.setAddedMdnList(mdnList);
                persistDTO.setInputDTO(contactDTO);
                validatorFW.validate(persistDTO);
//                respDTO = addTopairingList(mdnList, corpProfile, xdmsHomePttId, persisterTxn);
                //check if the sublist exists with the default name
                //if exist modify the members else create the sublist
                //check if the group existsand member list is >2
                KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
                String subPrefix = "CORP_AUTO_PAIR_SUBLIST";
                Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
                knLogger.debug(methodName, "param value Map", paramNameValueMap);
                String paramValue = paramNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.AUTO_PAIR_SUBLIST_NAME);
                if (paramValue != null) {
                    subPrefix = paramValue;
                } else {
                    throw new KnCorpBOException("CP24008", "The mandatory setting are missing");
                }
                KnCorpSublistDTO subInfo = sublistInfoUtil.getSublistDetailsByName(corpId, subPrefix, xdmsHomePttId, persisterTxn);
                if (subInfo != null) {
                    pairedListid = subInfo.getSublistId();
                }
                knLogger.debug(methodName, "pairedListid obtained from DB ", pairedListid);
                if (pairedListid > 0) {
                    knLogger.debug(methodName, "The auto paired named sublist exists in DB", pairedListid);
                    //The sublist already exsists add members to the sublist
                    KnIPCorpSublistInfoDTO sublistInfoDTO = new KnIPCorpSublistInfoDTO();
                    sublistInfoDTO.setAddedMdnList(mdnList);
                    sublistInfoDTO.setRemovedMdnList(new LinkedList<String>());
                    sublistInfoDTO.setSublistId(pairedListid);
                    sublistInfoDTO.setETag(subInfo.getETag());
                    sublistInfoDTO.setCorpId(corpId);
                    sublistInfoDTO.setEntityId(CORP_SUBLIST_MANAGER);
                    sublistInfoDTO.setOperationType(MODIFY_SUBLIST);
                    sublistInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
                    sublistInfoDTO.setDistribution(Boolean.TRUE);
                    sublistInfoDTO.setHierarchyType(contactDTO.getHierarchyType());
                    knLogger.debug(methodName, "modify sublist request DTO sent ", sublistInfoDTO);
                    KnCorpResponseDTO modSubDTO = modifySublist(sublistInfoDTO, persisterTxn);
                    respDTO.setStatus(modSubDTO.getStatus());
                    respDTO.setStatusCode(modSubDTO.getStatusCode());
                    respDTO.setChangeLogMap(modSubDTO.getChangeLogMap());
                    respDTO.setDisabledDispatchMemList(modSubDTO.getDisabledDispatchMemList());
                    respDTO.setEnabledDispatchMemList(modSubDTO.getEnabledDispatchMemList());
                    respDTO.setEtag(modSubDTO.getEtag());
                    respDTO.setFailedDataList(modSubDTO.getFailedDataList());
                    respDTO.setTgsModeChgMap(modSubDTO.getTgsModeChgMap());
                    respDTO.setLiEventList(modSubDTO.getLiEventList());
                    respDTO.setMessage(modSubDTO.getMessage());
                    respDTO.setPairedContactListId(modSubDTO.getPairedContactListId());
                    respDTO.setPeg(modSubDTO.getPeg());
                    knLogger.debug(methodName, "response from the modify sublist ", respDTO);
                } else {
                    knLogger.debug(methodName, "Paired Contact List Id not created for the corporation. ");
                    KnIPCorpSublistInfoDTO sublistInfoDTO = new KnIPCorpSublistInfoDTO();
                    sublistInfoDTO.setEntityId(CORP_SUBLIST_MANAGER);
                    sublistInfoDTO.setOperationType(KnOperationTypes.CREATE_SUBLIST);
                    sublistInfoDTO.setCorpId(corpId);
                    sublistInfoDTO.setDistributionPolicy(KnConstants.DIST_POLICY_SHARED_ANY_LIST);
                    sublistInfoDTO.setAddedMdnList(mdnList);
                    sublistInfoDTO.setSublistType(KnConstants.SUBLIST_TYPE_SHARED_LIST);
                    sublistInfoDTO.setDistribution(Boolean.TRUE);
                    knLogger.debug(methodName, "subPrefix :- ", subPrefix);
                    sublistInfoDTO.setSublistName(subPrefix);
                    sublistInfoDTO.setHierarchyType(contactDTO.getHierarchyType());
                    //createSublist
                    knLogger.debug(methodName, "Before calling to create sublist, sublistInfoDTO :- ", sublistInfoDTO);
                    KnCorpSublistRespDTO createSubRes = createSublist(sublistInfoDTO, persisterTxn);
                    knLogger.info(methodName, "Sublist creation resp - ", createSubRes);
                    int status = createSubRes.getStatus();
                    if (STATUS_FAILURE == status) {
                        knLogger.error(methodName, "Sublist creation failed - ", status);
                        throw new KnCorpBOException(createSubRes.getStatusCode(), createSubRes.getMessage());
                    }
                    //if the group found return group Input  DTO and send the indicator to group indicator
                    knLogger.debug(methodName, "After create sublist is called update the paird contactlist id.Response obtained is -", createSubRes);
                    pairedListid = createSubRes.getSublistDTO().getSublistId();
                    respDTO.setChangeLogMap(createSubRes.getChangeLogMap());
                }
                knLogger.debug(methodName, " update Corporate Paired contactlist ID in corporate table.");
                sublistInfoUtil.updateCorpPairedContListId(corpId, pairedListid, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "After update Corporate Paired contactlist ID is called.");
                knLogger.debug(methodName, "mdn list size of the corporate - ", mdnList.size());
                if (mdnList.size() >= 2) {
                    //get the group existing with the name
                    //if found send the modify group request with the group id
                    //else create group
                    //send an indicator to create the group.
                    //or
                    //send an indicator to modify the group.

                    String grpPrefix = "CORP_AUTO_PAIR_GRP";
                    knLogger.debug(methodName, "param value Map", paramNameValueMap);
                    String grpParamValue = paramNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.AUTO_PAIR_GROUP_NAME);
                    if (grpParamValue != null && !grpParamValue.isEmpty()) {
                        grpPrefix = grpParamValue;
                    } else {
                        throw new KnCorpBOException("CP24008", "The mandatory setting are missing");
                    }
                    KnCorpGroupDTO grpDTO = this.processGroupPairing(corpId, pairedListid, grpPrefix, xdmsHomePttId, persisterTxn);
                    if (grpDTO != null) {
                        if (grpDTO.isGroupPairing() != null) {
                            respDTO.setGroupPairing(Boolean.FALSE);
                            respDTO.setGroupId(grpDTO.getGroupId());
                            respDTO.setEtag(String.valueOf(grpDTO.getETag()));
                        } else {
                            respDTO.setGroupPairing(null);
                        }

                    } else {
                        respDTO.setGroupPairing(Boolean.TRUE);
                        respDTO.setGroupName(grpPrefix);
                    }

                }
            } else {
                knLogger.info(methodName, "Disable Auto pairing");
                sublistInfoUtil.updateCorpPairedContListId(corpId, 0, xdmsHomePttId, persisterTxn);
            }
            respDTO.setPairedContactListId(pairedListid);
            updateCorporateEtag(corpId);
            populate(respDTO);
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured in updateCorpAutoPairing - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while updateCorpAutoPairing - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while updateCorpAutoPairing- ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.info(methodName, "respDTO :", respDTO);
        return respDTO;
    }

    /**
     * This method is called to add the subscriber to add to the auto pairing listelse create one if its the first subscriber.
     *
     * @param contactDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnCorpAutoPairingResponse addToPairingList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        //call the private method that accepts the List of mdn and the corpId for the auto pairing
        String methodName = "addToPairingList(KnIPCorpInfoDTO, KnPersisterTxn)";
        knLogger.entry(methodName);
        knLogger.debug(methodName, "input dto passed is : ", contactDTO);
        KnCorpAutoPairingResponse respDTO = new KnCorpAutoPairingResponse();
        //determine the list of hanset subscribers
        //call the internal method which accepts the list of subscribers and the corpId as the parameter.
        try {
            int corpId = contactDTO.getCorpId();
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile =
                    commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);
            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();
            Collection<String> mdnList = new ArrayList<String>();
            mdnList.add(contactDTO.getMdn());
            knLogger.debug(methodName, "Before the addTopairing list method call");
            //check if the corporate is already linked
            int pairedContactListId = corpProfile.getPairedContactListId();
            if (pairedContactListId > 0) {
                knLogger.info(methodName, "The corporate is already auto linked");
                KnCorpSublistDTO sublistDTO = sublistInfoUtil.getSublistInfo(pairedContactListId, corpId, xdmsHomePttId, persisterTxn);
                KnIPCorpSublistInfoDTO sublistInfoDTO = new KnIPCorpSublistInfoDTO();
                sublistInfoDTO.setAddedMdnList(mdnList);
                sublistInfoDTO.setRemovedMdnList(new LinkedList<String>());
                sublistInfoDTO.setSublistId(pairedContactListId);
                sublistInfoDTO.setETag(sublistDTO.getETag());
                sublistInfoDTO.setCorpId(corpId);
                sublistInfoDTO.setEntityId(CORP_SUBLIST_MANAGER);
                sublistInfoDTO.setOperationType(MODIFY_SUBLIST);
                sublistInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
                sublistInfoDTO.setDistribution(Boolean.TRUE);
                sublistInfoDTO.setHierarchyType(contactDTO.getHierarchyType());
                knLogger.debug(methodName, "modify sublist request DTO sent ", sublistInfoDTO);
                KnCorpResponseDTO modSubDTO = modifySublist(sublistInfoDTO, persisterTxn);
                respDTO.setStatus(modSubDTO.getStatus());
                respDTO.setStatusCode(modSubDTO.getStatusCode());
                respDTO.setChangeLogMap(modSubDTO.getChangeLogMap());
                respDTO.setDisabledDispatchMemList(modSubDTO.getDisabledDispatchMemList());
                respDTO.setEnabledDispatchMemList(modSubDTO.getEnabledDispatchMemList());
                respDTO.setEtag(modSubDTO.getEtag());
                respDTO.setFailedDataList(modSubDTO.getFailedDataList());
                respDTO.setTgsModeChgMap(modSubDTO.getTgsModeChgMap());
                respDTO.setLiEventList(modSubDTO.getLiEventList());
                respDTO.setMessage(modSubDTO.getMessage());
                respDTO.setPairedContactListId(pairedContactListId);
                respDTO.setPeg(modSubDTO.getPeg());
                if (respDTO.getStatus() == KnConstants.SUCCESS) {
                    List<Integer> sublistIds = new ArrayList<Integer>();
                    sublistIds.add(pairedContactListId);
                    knLogger.debug(methodName, "Get the subscribers member from the sublist");
                    List<String> sublistMembers = sublistInfoUtil.getSublistMembers(sublistIds, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "subscriber size", sublistMembers.size());
                    if (sublistMembers.size() == 2) {
                        KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
                        String grpPrefix = "CORP_AUTO_PAIR_GRP";
                        Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
                        knLogger.debug(methodName, "param value Map", paramNameValueMap);
                        String grpParamValue = paramNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.AUTO_PAIR_GROUP_NAME);
                        if (grpParamValue != null && !grpParamValue.isEmpty()) {
                            grpPrefix = grpParamValue;
                        } else {
                            throw new KnCorpBOException("CP24008", "The mandatory setting are missing");
                        }
                        KnCorpGroupDTO grpDTO = this.processGroupPairing(corpId, pairedContactListId, grpPrefix, xdmsHomePttId, persisterTxn);
                        if (grpDTO != null) {
                            if (!grpDTO.isContactPairing()) {
                                respDTO.setGroupPairing(Boolean.FALSE);
                                respDTO.setGroupId(grpDTO.getGroupId());
                                respDTO.setEtag(String.valueOf(grpDTO.getETag()));
                            } else {
                                respDTO.setGroupPairing(null);
                            }

                        } else {
                            respDTO.setGroupPairing(Boolean.TRUE);
                            respDTO.setGroupName(grpPrefix);
                        }
                    }
                    knLogger.debug(methodName, "After create sublist is called update the paird contactlist id");
                    sublistInfoUtil.updateCorpPairedContListId(corpId, pairedContactListId, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "After update Corporate Paired contactlist ID is called.");
                    populate(respDTO);
                }
            } else {
                knLogger.debug(methodName, "The corporate is not auto paired so this is a invalid call to the API");
            }
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while getSubscrSublists - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while getSubscrSublists- ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.info(methodName, "respDTO :", respDTO);
        return respDTO;
    }


    /**
     * Method to update the corporate etag via the jib
     *
     * @param corpId
     */
    public void updateCorporateEtag(int corpId) throws KnBOException {
        String methodName = "updateCorporateEtag()";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId);
        //Go for async updation.
        knLogger.debug(methodName, "To start async update");
        String pttServerId = KnGenInfoUtil.getInstance().retrieveLocalXDMPttServerId();
        Map<String, String> asyncInput = new HashMap<>();
        asyncInput.put(KnDbSyncFwConstants.PTTSERVER_ID, pttServerId);
        asyncInput.put(KnDbSyncFwConstants.CORP_ID, String.valueOf(corpId));
        knLogger.debug(methodName, " Async inputs", asyncInput);
        KnSqlJobCollector collector = KnSqlJobCollector.getInstance();
        int serviceType = KnDbSyncFwConstants.EXECUTOR.ETAG_UPDATE.value();
        collector.collect(serviceType, asyncInput);
        knLogger.debug(methodName, "Updated to collector", serviceType);
    }

    /*
     * This method will validate wheather default groupd is exist r needs to be newly created or Modified or No action to be taken.
     * */
    private KnCorpGroupDTO processGroupPairing(int corpId, int pairedContactListId, String grpPrefix, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnBOException {
        String methodName = "processGroupPairing";
        //get the group existing with the name
        //if found send the modify group request with the groupid
        //else create group
        //send an indicator to create the group.

        KnCorpGroupDTO groupDTO = groupInfoUtil.getGroupDetailsByName(grpPrefix, corpId, xdmsHomePttId, persisterTxn);
        knLogger.debug(methodName, "groupDTO obtained from db", groupDTO);
        int grpId = groupDTO.getGroupId();
        if (grpId > 0) {
            knLogger.debug(methodName, "get sublist mapped to the group", groupDTO);
            List<Integer> sublistids = groupInfoUtil.getGroupSublistIds(grpId, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "sublistids mapped are ", sublistids);
            if (sublistids.contains(pairedContactListId)) {
                knLogger.debug(methodName, "The sublist is mapped to the group");
                groupDTO.setGroupPairing(null);

            } else {
                knLogger.debug(methodName, "The sublist is not mapped to the group so modify the group and add this sublist");
                groupDTO.setGroupPairing(Boolean.FALSE);

            }
        } else {
            knLogger.debug(methodName, "The group does not exists so create a group with the prefix");
            groupDTO = null;
        }
        return groupDTO;
    }
    private boolean checkClientType(Integer clientType,int mcsCompliance)
    {
        return clientType == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
                || clientType == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
                || clientType == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
                || clientType == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE.DISPATCH_CLIENT.value()
                || clientType == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                || mcsCompliance == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.MCPTT_COMPLIANCE;
    }
    @Override
    public KnCorpResponseDTO assignCommonContactList(KnIPCorpSublistSubscDistDTO subscDistDTO, KnPersisterTxn persisterTxn) {
        String methodName = "assignCommonContactList(sublistInfoDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", subscDistDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            int corpId = subscDistDTO.getCorpId();
            Collection<Integer> reqAddSublistIds = subscDistDTO.getSublistIds();
            String commonContactListId = subscDistDTO.getCommonSublistId();
            KnSublistDetailsPersistDTO valPersistDto = new KnSublistDetailsPersistDTO();
            knLogger.debug(methodName, "Fetching CorpProfile");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            boolean isCommonContactEnabled = Boolean.FALSE;
            for (String mdn : subscDistDTO.getMdnList()) {
                KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn,
                        KnProfileTypes.PUBLIC_PROFILE, true, persisterTxn);
                isCommonContactEnabled = subscProfile.getSubscriberFS2() != null && KnGeneralUtil.getFeatureBitValue(subscProfile.getSubscriberFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.COMMON_CONTACT_LIST.value());
                if (!isCommonContactEnabled) {
                    break;
                }
            }

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();

            final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);

            Map<String, Object> customParams = subscDistDTO.getCustomParamMap();
            boolean hiearchyFlag = (subscDistDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY);
            if (hiearchyFlag) {
                //in case of update auto pairing we are not passing custom map. hence this check is required.
                if (customParams != null) {
                    List<Integer> sublistIdList = new ArrayList<Integer>();
                    sublistIdList.addAll(reqAddSublistIds);
                    KnCorpSublistInfoUtil knCorpSublistInfoUtil = new KnCorpSublistInfoUtil();
                    knCorpSublistInfoUtil.validateSublistParams(sublistIdList, customParams, xdmsHomePttId, persisterTxn);
                    subscDistDTO.setCustomParamMap(customParams);
                }
            }
            /**
             * sublistMdns Hold the list of MDN to create sublist.
             */
            Collection<String> sublistMdns = subscDistDTO.getMdnList();

            if (sublistMdns != null) {
                int memberCount = sublistMdns.size();
                valPersistDto.setContactCountInRequest(memberCount);
            }
            valPersistDto.setInputDTO(subscDistDTO);
            boolean isCommonContactListSupport = Boolean.FALSE;
            if (isCommonContactEnabled) {
                //common sublist modification allowed if system and corp flag is enabled
                final String systemCmnContactListSupport = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SUPPORT) == null
                        ? "0" : microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SUPPORT);
                final Integer commonContactListSupportFlagOfCorpProfile = corpProfile.getCommonContactListSupport();
                if (systemCmnContactListSupport.equals(BIT_ENABLED) || commonContactListSupportFlagOfCorpProfile == Integer.valueOf(ENABLED)) {
                    isCommonContactListSupport = Boolean.TRUE;
                }
                valPersistDto.setCommContactListSupp(isCommonContactListSupport);
            }

            int subListCorpId = sublistInfoUtil.getCorpIdFromCorpListInfo(subscDistDTO.getSublistIds().stream().findAny().get(),
                    xdmsHomePttId, persisterTxn);

            //Set<Integer> pocSubcCorpIds = contactInfoUtil.getCorpIdFromMdnList(sublistMdns.stream().collect(Collectors.toList()),xdmsHomePttId, persisterTxn);
            Collection<KnCorpSubscriberDTO> mdnDetails = contactInfoUtil.getSubscriberProfileDetails(sublistMdns, corpId, xdmsHomePttId, false, persisterTxn);
            Set<Integer> pocSubcCorpIds = mdnDetails.stream().map(KnCorpSubscriberDTO::getCorpId).collect(Collectors.toSet());
            valPersistDto.setSubListCorpId(subListCorpId);
            valPersistDto.setSubscrsCorpIds(pocSubcCorpIds);
            valPersistDto.setCorpId(corpId);

            //common sublist size per sub  allowed
            final String sysCmnCntctListSizePerSubString = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_PERSUB);
            final int sysCmnCntctListSizePerSub = sysCmnCntctListSizePerSubString == null ? 0 : Integer.parseInt(sysCmnCntctListSizePerSubString);
            valPersistDto.setSysMaxAllowedCommCnctListPerSub(sysCmnCntctListSizePerSub);
            Map<String, Integer> commContctListCntForSubsc = sublistInfoUtil.commContctListCntForSubsc(subscDistDTO, xdmsHomePttId, persisterTxn);
            valPersistDto.setCommCnctListCountPerSub(commContctListCntForSubsc);

            knLogger.debug(methodName, "Invoking Validation FW - ", valPersistDto);
            validatorFW.validate(valPersistDto);
            knLogger.debug(methodName, "Validation completed Successfully");

            //remove from the map the already inserted data.

            KnIPCorpSublistSubscDistDTO distDTO = new KnIPCorpSublistSubscDistDTO();
            distDTO.setMdnList(sublistMdns);
            distDTO.setSublistIds(subscDistDTO.getSublistIds());


            Map<Integer, Collection<String>> alreadyInsertedSubList = sublistInfoUtil.sublistPushedToSubscribersFrmList(distDTO, xdmsHomePttId, persisterTxn);
            if (alreadyInsertedSubList != null && !alreadyInsertedSubList.isEmpty()) {
                knLogger.debug(methodName, "Already pushed subscribers. ", alreadyInsertedSubList);
                var insertedMdnList = alreadyInsertedSubList.get(distDTO.getSublistIds().stream().findAny().get());
                var mdnList =  distDTO.getMdnList().stream()
                        .filter(mdn -> !insertedMdnList.contains(mdn)).collect(Collectors.toList());
                distDTO.setMdnList(mdnList);
                knLogger.debug(methodName, "After removing the updated. ", mdnList, " distDTo mdnlist ", distDTO.getMdnList());
            }
            if(distDTO.getMdnList() == null || distDTO.getMdnList().isEmpty()){
                knLogger.info(methodName, "Common Contact List is already assigned to all  teh request subscribers", distDTO.getMdnList());
                populate(respDTO);
                return respDTO;
            }

            knLogger.debug(methodName, "Push Sublist for the subscribers distDTO- ", distDTO);
            contactInfoUtil.pushSublistListToSubscriberList(distDTO, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "updateSubscribersContactCount. corpProfile.getMaxContactsPerSubsc()- ", corpProfile.getMaxContactsPerSubsc());
            contactInfoUtil.updateSubscribersContactCount(distDTO.getMdnList(), corpProfile.getMaxContactsPerSubsc(), xdmsHomePttId, persisterTxn);
            //DG.CORPLISTMEMBER
            List<KnCorpSubscriberDTO> distAddedSublistMemLst = sublistInfoUtil.getSublistDistinctMembers(reqAddSublistIds, corpId, xdmsHomePttId, persisterTxn);
            //Get the distinct added members among added sublis id list and added member list
            Collection<KnCorpSubscriberDTO> subscriberList =
                    contactInfoUtil.getDistinctMembersList(null, distAddedSublistMemLst, corpId, xdmsHomePttId, persisterTxn);
            Map<String, Collection<String>> subsContactMap =
                    contactInfoUtil.getSubscribersContactList(sublistMdns, xdmsHomePttId, persisterTxn);
            Map<String, Collection<KnCorpSubscriberDTO>> subscrNewAddedContMap = new HashMap<>();

            for(String reqMdn:sublistMdns){
                subscrNewAddedContMap.put(reqMdn,subscriberList);
            }
            Map<String, Collection<KnCorpSubscriberDTO>> insertContacts = commonInfoUtil.filterMemberToBeAddedToSubscriber(subsContactMap
                    , subscrNewAddedContMap);
            contactInfoUtil.insertMembersIntoCorpContactList(insertContacts, xdmsHomePttId, persisterTxn);
            //xcap/mcsXcap notification
            //get all the online mdns (base+profile)
            List<String> baseAndProfileUnassignedMdns=new ArrayList<>(sublistMdns);

            knLogger.debug(methodName, "Before filter baseAndProfileUnassignedMdns ", baseAndProfileUnassignedMdns);
               if(isCommonContactListSupport) {
                   //from the list of mdn remove the mdn where 121 featurebit is disabled
                   Map<String, KnCorpSubscriberDTO> subsDetails = contactInfoUtil.getPoCSubscriberExistMap(baseAndProfileUnassignedMdns,
                           xdmsHomePttId, persisterTxn);
                   Set<String> commonSublistDis = new HashSet<>();
                   if (subsDetails != null && !subsDetails.isEmpty()) {
                       for (String mdn : baseAndProfileUnassignedMdns) {
                           KnCorpSubscriberDTO subsDtls = subsDetails.get(mdn);
                           boolean isCommonContact = subsDtls.getSubscriberFs2() != null && KnGeneralUtil.getFeatureBitValue(subsDtls.getSubscriberFs2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.COMMON_CONTACT_LIST.value());
                           if (!isCommonContact) {
                               commonSublistDis.add(mdn);
                           }
                       }
                   }
                   baseAndProfileUnassignedMdns.removeAll(commonSublistDis);
                   if (sublistMdns != null) {
                       sublistMdns.removeAll(commonSublistDis);
                   }
               }
            knLogger.debug(methodName, "After filter baseAndProfileUnassignedMdns ", baseAndProfileUnassignedMdns, "sublistMdns", sublistMdns);

            List<String> profileMdns= corpSubsProvInfoUtil.getProfileMdnByBaseMdns(new ArrayList<>(sublistMdns), xdmsHomePttId, persisterTxn);
            if(!profileMdns.isEmpty()){
                baseAndProfileUnassignedMdns.addAll(profileMdns);
                Collection<KnCorpSubscriberDTO> firstEntryValue = insertContacts.entrySet().stream().findFirst().get().getValue();
                //adding deleted contact to profilemdn for forming the notification
                for(String profileMdn:profileMdns){
                    insertContacts.put(profileMdn,firstEntryValue);
                }
            }
            Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = corpUserProfileUtil.profileMdnEtagUpdate(
                    null, String.valueOf(corpId), profileMdns, null, xdmsHomePttId, persisterTxn);
            respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
            if (null != profileMdnEtagMap && !profileMdnEtagMap.isEmpty()) {
                respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
            }
            Map<String,String> onlineMdns = KnGeneralCacheUtil.getInstance().getOnlineSubcribersListByMdn(baseAndProfileUnassignedMdns);
            List<String> onlineMdnList = new LinkedList<>(onlineMdns.keySet());
            Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId, baseAndProfileUnassignedMdns,
                    persisterTxn, null);
            Map<String, KnOPDirChgDTO> formedEtagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, insertContacts, null,
                    null, null, null, null, null,
                    null, null, null, null);
            knLogger.debug(methodName, "etagMap after the form xcpa difff- ", KnGDPRTemplate.mapKeyMdn(formedEtagMap));
            respDTO.setChangeLogMap(formedEtagMap);
            respDTO.setOnlineMdnList(onlineMdnList);
            respDTO.setMdnCorpId(corpId);
            populate(respDTO);
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while assigning sub list - ", e);
            populate(respDTO, e);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while assigning sub list - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while  assigning Sublist- ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO unAssignCommonContactList(KnIPCorpSublistSubscDistDTO subscDistDTO, KnPersisterTxn persisterTxn) {
        String methodName = "unAssignCommonContactList(sublistInfoDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", subscDistDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            int corpId = subscDistDTO.getCorpId();
            Collection<String> reqSublistMdns = subscDistDTO.getMdnList();
            Integer commonSublistId = Integer.parseInt(subscDistDTO.getCommonSublistId());
            Collection<Integer> subListIds=new ArrayList<>();
            subListIds.add(commonSublistId);

            KnSublistDetailsPersistDTO valPersistDto = new KnSublistDetailsPersistDTO();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Corporate Profile - ", corpProfile);
            String xdmsHomePttId = corpProfile.getXdmsHome();

            Map<String, Object> customParams = subscDistDTO.getCustomParamMap();
            boolean hiearchyFlag = (subscDistDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY);
            if (hiearchyFlag) {
                //in case of update auto pairing we are not passing custom map. hence this check is required.
                if (customParams != null) {
                    List<Integer> sublistIdList = new ArrayList<Integer>();
                    sublistIdList.addAll(subListIds);
                    KnCorpSublistInfoUtil knCorpSublistInfoUtil = new KnCorpSublistInfoUtil();
                    knCorpSublistInfoUtil.validateSublistParams(sublistIdList, customParams, xdmsHomePttId, persisterTxn);
                    subscDistDTO.setCustomParamMap(customParams);
                }
            }
            valPersistDto.setInputDTO(subscDistDTO);
            int subListCorpId = sublistInfoUtil.getCorpIdFromCorpListInfo(commonSublistId,xdmsHomePttId, persisterTxn);
            Collection<KnCorpSubscriberDTO> mdnDetails = contactInfoUtil.getSubscriberProfileDetails(reqSublistMdns, corpId, xdmsHomePttId, false, persisterTxn);
            Set<Integer> pocSubcCorpIds = mdnDetails.stream().map(KnCorpSubscriberDTO::getCorpId).collect(Collectors.toSet());
            valPersistDto.setSubListCorpId(subListCorpId);
            valPersistDto.setSubscrsCorpIds(pocSubcCorpIds);
            valPersistDto.setCorpId(corpId);
            //mapping exists for the sublist and mdn
            KnIPCorpSublistSubscDistDTO distDTO = new KnIPCorpSublistSubscDistDTO();
            distDTO.setMdnList(reqSublistMdns);
            distDTO.setSublistIds(subListIds);
            //sublistId, mdnList
            Collection<String> reqSublistMdnsValid =new ArrayList<>(reqSublistMdns);
            Map<Integer, Collection<String>> alreadyInsertedSubList = sublistInfoUtil.sublistPushedToSubscribersFrmList(distDTO, xdmsHomePttId, persisterTxn);
            if(!alreadyInsertedSubList.isEmpty()){
                Collection<String> dbMdnList = alreadyInsertedSubList.get(commonSublistId);
                reqSublistMdnsValid.removeAll(dbMdnList);
            }
            valPersistDto.setUnAssignedSublistMdns(reqSublistMdnsValid);

            knLogger.debug(methodName, "Invoking Validation FW - ", valPersistDto);
            validatorFW.validate(valPersistDto);
            knLogger.debug(methodName, "Validation completed Successfully");

            //remove all RECIPIENTMDN  from DG.CORPLISTDISTINFO of CORPLISTID
            sublistInfoUtil.removeSubscribersSublist(distDTO, xdmsHomePttId, persisterTxn);
            //Kdk_POCDataRetrieval.updateCorpContactCountForSubsc
            contactInfoUtil.updateSubscribersContactCount(reqSublistMdns, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
            //remove DG.CORPCONTACTLIST for assigned mdn for the sublist
            List<KnCorpSubscriberDTO> distAddedSublistMemLst = sublistInfoUtil.getSublistDistinctMembers(subListIds, corpId, xdmsHomePttId, persisterTxn);
            LinkedList<String> sublistContactMdn = distAddedSublistMemLst.stream()
                    .map(KnCorpSubscriberDTO::getMdn).collect(Collectors.toCollection(LinkedList::new));
            LinkedHashMap<String, LinkedList<String>> removedMdnContactListMap = new LinkedHashMap<>();
            for(String reqMdn:reqSublistMdns){
                removedMdnContactListMap.put(reqMdn,sublistContactMdn);
            }
            LinkedHashMap<String, LinkedList<Integer>> delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(removedMdnContactListMap
                    , xdmsHomePttId, persisterTxn);
            //xcap/mcsXcap notification
            //get all the online mdns (base+profile)
            List<String> baseAndProfileUnassignedMdns=new ArrayList<>(reqSublistMdns);

            knLogger.debug(methodName, "Before filter baseAndProfileUnassignedMdns ", baseAndProfileUnassignedMdns);
            //from the list of mdn remove the mdn where 121 featurebit is disabled
            Map<String, KnCorpSubscriberDTO> subsDetails = contactInfoUtil.getPoCSubscriberExistMap(baseAndProfileUnassignedMdns,
                    xdmsHomePttId, persisterTxn);
            Set<String> commonSublistDis = new HashSet<>();
            if(subsDetails != null && !subsDetails.isEmpty()) {
                for (String mdn : baseAndProfileUnassignedMdns) {
                    KnCorpSubscriberDTO subsDtls = subsDetails.get(mdn);
                    boolean isCommonContact = subsDtls.getSubscriberFs2() != null && KnGeneralUtil.getFeatureBitValue(subsDtls.getSubscriberFs2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.COMMON_CONTACT_LIST.value());
                    if (!isCommonContact) {
                        commonSublistDis.add(mdn);
                    }
                }
            }
            baseAndProfileUnassignedMdns.removeAll(commonSublistDis);
            if(reqSublistMdns != null){
                reqSublistMdns.removeAll(commonSublistDis);
            }
            knLogger.debug(methodName, "After filter baseAndProfileUnassignedMdns ", baseAndProfileUnassignedMdns);


            List<String> profileMdns= corpSubsProvInfoUtil.getProfileMdnByBaseMdns(new ArrayList<>(reqSublistMdns), xdmsHomePttId, persisterTxn);
            if(!profileMdns.isEmpty()){
                baseAndProfileUnassignedMdns.addAll(profileMdns);
                LinkedList<Integer> firstEntryValue = delContactStatus.entrySet().stream().findFirst().get().getValue();
                //adding deleted contact to profilemdn for forming the notification
                for(String profileMdn:profileMdns){
                    removedMdnContactListMap.put(profileMdn,sublistContactMdn);
                    delContactStatus.put(profileMdn,firstEntryValue);
                }
            }
            Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = corpUserProfileUtil.profileMdnEtagUpdate(
                    null, String.valueOf(corpId), profileMdns, null, xdmsHomePttId, persisterTxn);
            respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
            if (null != profileMdnEtagMap && !profileMdnEtagMap.isEmpty()) {
                respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
            }
            Map<String,String> onlineMdns = KnGeneralCacheUtil.getInstance().getOnlineSubcribersListByMdn(baseAndProfileUnassignedMdns);
            List<String> onlineMdnList = new LinkedList<>(onlineMdns.keySet());
            Map<String, Collection<KnCorpSubscriberDTO>> finalContacts=new HashMap<>();
            Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId, baseAndProfileUnassignedMdns,
                    persisterTxn, null);
            Map<String, KnOPDirChgDTO> formedEtagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, finalContacts
                    , removedMdnContactListMap,
                    null, null, null, null, null,
                    null, delContactStatus, null, null);
            knLogger.debug(methodName, "etagMap after the form xcpa difff- ", KnGDPRTemplate.mapKeyMdn(formedEtagMap));
            respDTO.setOnlineMdnList(onlineMdnList);
            respDTO.setChangeLogMap(formedEtagMap);
            respDTO.setMdnCorpId(corpId);
            populate(respDTO);
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while assigning sub list - ", e);
            populate(respDTO, e);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while assigning sub list - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while  assigning Sublist- ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO unAssignCommonContactListForCloningContact(KnIPCorpSublistSubscDistDTO subscDistDTO, KnPersisterTxn persisterTxn) {
        String methodName = "unAssignCommonContactListForCloningContact(sublistInfoDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", subscDistDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            int corpId = subscDistDTO.getCorpId();
            Collection<String> reqSublistMdns = subscDistDTO.getMdnList();
            Integer commonSublistId = Integer.parseInt(subscDistDTO.getCommonSublistId());
            Collection<Integer> subListIds=new ArrayList<>();
            subListIds.add(commonSublistId);

            KnSublistDetailsPersistDTO valPersistDto = new KnSublistDetailsPersistDTO();
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Corporate Profile - ", corpProfile);
            String xdmsHomePttId = corpProfile.getXdmsHome();

            valPersistDto.setInputDTO(subscDistDTO);
            int subListCorpId = sublistInfoUtil.getCorpIdFromCorpListInfo(commonSublistId,xdmsHomePttId, persisterTxn);
            Collection<KnCorpSubscriberDTO> mdnDetails = contactInfoUtil.getSubscriberProfileDetails(reqSublistMdns, corpId, xdmsHomePttId, false, persisterTxn);
            Set<Integer> pocSubcCorpIds = mdnDetails.stream().map(KnCorpSubscriberDTO::getCorpId).collect(Collectors.toSet());
            valPersistDto.setSubListCorpId(subListCorpId);
            valPersistDto.setSubscrsCorpIds(pocSubcCorpIds);
            valPersistDto.setCorpId(corpId);
            //mapping exists for the sublist and mdn
            KnIPCorpSublistSubscDistDTO distDTO = new KnIPCorpSublistSubscDistDTO();
            distDTO.setMdnList(reqSublistMdns);
            distDTO.setSublistIds(subListIds);
            //sublistId, mdnList
            Collection<String> reqSublistMdnsValid =new ArrayList<>(reqSublistMdns);
            Map<Integer, Collection<String>> alreadyInsertedSubList = sublistInfoUtil.sublistPushedToSubscribersFrmList(distDTO, xdmsHomePttId, persisterTxn);
            if(!alreadyInsertedSubList.isEmpty()){
                Collection<String> dbMdnList = alreadyInsertedSubList.get(commonSublistId);
                reqSublistMdnsValid.removeAll(dbMdnList);
            }
            valPersistDto.setUnAssignedSublistMdns(reqSublistMdnsValid);

            /*knLogger.debug(methodName, "Invoking Validation FW - ", valPersistDto);
            validatorFW.validate(valPersistDto);
            knLogger.debug(methodName, "Validation completed Successfully");*/

            //remove all RECIPIENTMDN  from DG.CORPLISTDISTINFO of CORPLISTID
            sublistInfoUtil.removeSubscribersSublist(distDTO, xdmsHomePttId, persisterTxn);
            //Kdk_POCDataRetrieval.updateCorpContactCountForSubsc
            contactInfoUtil.updateSubscribersContactCount(reqSublistMdns, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
            //remove DG.CORPCONTACTLIST for assigned mdn for the sublist
            List<KnCorpSubscriberDTO> distAddedSublistMemLst = sublistInfoUtil.getSublistDistinctMembers(subListIds, corpId, xdmsHomePttId, persisterTxn);
            LinkedList<String> sublistContactMdn = distAddedSublistMemLst.stream()
                    .map(KnCorpSubscriberDTO::getMdn).collect(Collectors.toCollection(LinkedList::new));
            LinkedHashMap<String, LinkedList<String>> removedMdnContactListMap = new LinkedHashMap<>();
            for(String reqMdn:reqSublistMdns){
                removedMdnContactListMap.put(reqMdn,sublistContactMdn);
            }
            LinkedHashMap<String, LinkedList<Integer>> delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(removedMdnContactListMap
                    , xdmsHomePttId, persisterTxn);
            //xcap/mcsXcap notification
            //get all the online mdns (base+profile)
            List<String> baseAndProfileUnassignedMdns=new ArrayList<>(reqSublistMdns);

            knLogger.debug(methodName, "Before filter baseAndProfileUnassignedMdns ", baseAndProfileUnassignedMdns);
            //from the list of mdn remove the mdn where 121 featurebit is disabled
            Map<String, KnCorpSubscriberDTO> subsDetails = contactInfoUtil.getPoCSubscriberExistMap(baseAndProfileUnassignedMdns,
                    xdmsHomePttId, persisterTxn);
            Set<String> commonSublistDis = new HashSet<>();
            if(subsDetails != null && !subsDetails.isEmpty()) {
                for (String mdn : baseAndProfileUnassignedMdns) {
                    KnCorpSubscriberDTO subsDtls = subsDetails.get(mdn);
                    boolean isCommonContact = subsDtls.getSubscriberFs2() != null && KnGeneralUtil.getFeatureBitValue(subsDtls.getSubscriberFs2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.COMMON_CONTACT_LIST.value());
                    if (!isCommonContact) {
                        commonSublistDis.add(mdn);
                    }
                }
            }
            baseAndProfileUnassignedMdns.removeAll(commonSublistDis);
            if(reqSublistMdns != null){
                reqSublistMdns.removeAll(commonSublistDis);
            }
            knLogger.debug(methodName, "After filter baseAndProfileUnassignedMdns ", baseAndProfileUnassignedMdns);


            List<String> profileMdns= corpSubsProvInfoUtil.getProfileMdnByBaseMdns(new ArrayList<>(reqSublistMdns), xdmsHomePttId, persisterTxn);
            if(!profileMdns.isEmpty()){
                baseAndProfileUnassignedMdns.addAll(profileMdns);
                LinkedList<Integer> firstEntryValue = delContactStatus.entrySet().stream().findFirst().get().getValue();
                //adding deleted contact to profilemdn for forming the notification
                for(String profileMdn:profileMdns){
                    removedMdnContactListMap.put(profileMdn,sublistContactMdn);
                    delContactStatus.put(profileMdn,firstEntryValue);
                }
            }
            Map<String,String> onlineMdns = KnGeneralCacheUtil.getInstance().getOnlineSubcribersListByMdn(baseAndProfileUnassignedMdns);
            List<String> onlineMdnList = new LinkedList<>(onlineMdns.keySet());
            Map<String, Collection<KnCorpSubscriberDTO>> finalContacts=new HashMap<>();
            Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId, baseAndProfileUnassignedMdns,
                    persisterTxn, null);
            Map<String, KnOPDirChgDTO> formedEtagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, finalContacts
                    , removedMdnContactListMap,
                    null, null, null, null, null,
                    null, delContactStatus, null, null);
            knLogger.debug(methodName, "etagMap after the form xcpa difff- ", KnGDPRTemplate.mapKeyMdn(formedEtagMap));
            respDTO.setOnlineMdnList(onlineMdnList);
            respDTO.setChangeLogMap(formedEtagMap);
            respDTO.setMdnCorpId(corpId);
            populate(respDTO);
        } /*catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while assigning sub list - ", e);
            populate(respDTO, e);

        }*/ catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while assigning sub list - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occured while  assigning Sublist- ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }
    private Map<String, Object> performAutoAssignZonesAndChannels(
            List<Integer> groupIds,
            Collection<? extends KnCorpSubscriberDTO> memberDTOList,
            KnCorpProfileDTO corpProfile,
            KnCorpContactInfoUtil contactInfoUtil,
            KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {

        final String methodName = "performAutoAssignZonesAndChannels()";
        knLogger.debug(methodName, "ENTRY - groupIds:", groupIds, " memberCount:",
                memberDTOList != null ? memberDTOList.size() : 0);

        Map<String, Object> result = new HashMap<>();
        List<String> unassignedMdns = new ArrayList<>();

        // Validate input
        if (groupIds == null || groupIds.isEmpty()) {
            result.put("status", ZONE_ASSIGNMENT_STATUS.DISABLED.value());
            result.put("unassignedMdns", unassignedMdns);
            knLogger.warn(methodName, "No group IDs provided for auto-assignment");
            return result;
        }

        if (memberDTOList == null || memberDTOList.isEmpty()) {
            result.put("status", ZONE_ASSIGNMENT_STATUS.DISABLED.value());
            result.put("unassignedMdns", unassignedMdns);
            knLogger.warn(methodName, "No members provided for auto-assignment");
            return result;
        }

        try {
            // Prepare data for assignment
            String pttServerID = genInfoUtil.retrieveLocalXDMPttServerId();

            // Collect all MDNs
            List<String> mdnIds = memberDTOList.stream()
                    .map(KnCorpSubscriberDTO::getMdn)
                    .collect(Collectors.toList());

            knLogger.debug(methodName, "Processing", mdnIds.size(), "MDNs for groups:", groupIds);


            var DBSubMap = contactInfoUtil.getPoCSubscriberCorporateDetails(mdnIds, pttServerID, persisterTxn);
            Map<String, Integer> mdnClientTypeMap = new HashMap<>();

            for (var member : memberDTOList) {
                String mdn = member.getMdn();
                int clientType = member.getClientType();
                if (DBSubMap.get(member.getMdn()) != null) {
                    clientType = DBSubMap.get(member.getMdn()).getClientType();
                    mdn = DBSubMap.get(member.getMdn()).getMdn();
                }


                mdnClientTypeMap.put(mdn, clientType);

                knLogger.debug(methodName, "Prepared assignment for mdn:", mdn, " clientType:", clientType);
            }

            // Get all external MDNs for the corp
            String xdmsHome = corpProfile.getXdmsHome();
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            Map<String, KnCorpSubscriberDTO> extSubscribersMap = corpXdmDao.getCorpExternalSubscriber(corpProfile.getCorpId(), persisterTxn);
            Set<String> extMdnSet = null;
            if(extSubscribersMap != null && !extSubscribersMap.isEmpty()){
                extMdnSet = extSubscribersMap.keySet();
            }

            // Call assignment method once for all members
            Map<String, Boolean> autoAssignResults = groupInfoUtil.assignZonesAndChannels_V2(
                    groupIds,
                    mdnClientTypeMap,
                    corpProfile.getExtCorpId(),
                    pttServerID,
                    persisterTxn,
                    extMdnSet);
            if (autoAssignResults == null || autoAssignResults.isEmpty()) {
                result.put("status", ZONE_ASSIGNMENT_STATUS.FAILED.value());
                result.put("unassignedMdns", unassignedMdns);
                knLogger.warn(methodName,"Auto-assignment returned no results");
                return result;
            }
            // Analyze results
            long successCount = autoAssignResults.values().stream()
                    .filter(Boolean::booleanValue)
                    .count();
            long failedCount = autoAssignResults.values().stream()
                    .filter(assigned -> !assigned)
                    .count();
            long totalCount = autoAssignResults.size();

            knLogger.info(methodName, "Assignment completed - Total:", totalCount,
                    " Success:", successCount, " Failed:", failedCount);

            // Determine status based on results
            if (successCount == totalCount) {
                // All assigned successfully
                result.put("status", ZONE_ASSIGNMENT_STATUS.ASSIGNED.value());
                knLogger.info(methodName, "All MDNs successfully assigned zones/channels");

            } else if (failedCount == totalCount) {
                // All failed
                result.put("status", ZONE_ASSIGNMENT_STATUS.FAILED.value());
                knLogger.error(methodName, "All MDNs failed zone/channel assignment");

            } else {
                // Partial success - some assigned, some failed
                result.put("status", ZONE_ASSIGNMENT_STATUS.PARTIAL.value());

                // Collect unassigned MDNs
                for (Map.Entry<String, Boolean> entry : autoAssignResults.entrySet()) {
                    if (!entry.getValue()) {
                        unassignedMdns.add(entry.getKey());
                    }
                }

                knLogger.warn(methodName, "Partial assignment - Unassigned MDN count:",
                        unassignedMdns.size(), " Unassigned MDNs:", unassignedMdns);
            }

            result.put("unassignedMdns", unassignedMdns);

        } catch (Exception e) {
            knLogger.error(methodName, "Auto-assignment failed with exception:", e);
            result.put("status", ZONE_ASSIGNMENT_STATUS.FAILED.value());
            result.put("unassignedMdns", unassignedMdns);
        }

        knLogger.debug(methodName, "EXIT - Status:", result.get("status"));
        return result;
    }
}
