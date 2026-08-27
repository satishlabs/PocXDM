/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubRequestHandlerImpl.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Harsha             Dec 29, 2010  7.0
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
package com.kodiak.xdms.xdmintf.impl;

import com.kodiak.common.commdto.common.KnXDMScanlistInfoDTO;
import com.kodiak.common.commdto.request.*;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnXDMActivateRespDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.xdms.xdmintf.IRequestHandlerIntf;
import com.kodiak.xdms.xdmintf.IResponseHandler;
import com.kodiak.xdms.xdmintf.IXDMFacadeIntf;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnAuditHelper;

public class KnPubRequestHandlerImpl implements IRequestHandlerIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPubRequestHandlerImpl.class);

    private IResponseHandler respHandler;
    private KnAuditHelper audit;

    public KnPubRequestHandlerImpl() {
        respHandler = new KnResponseHandlerImpl();
        knLogger.info("KnPubRequestHandlerImpl()", "Initializing Audit");
        audit = KnAuditHelper.getAuditLogger("4001");
        knLogger.info("KnPubRequestHandlerImpl()", "Audit initialized - ", audit);
    }

    public boolean processRequest(KnMessage msgObj) {
        /**
         * TODO
         * 1. Get the payload from the msgObj
         * 2. Check if instance of IInputDTO
         * 3. Get the operation type
         * 4. switch to the respective operation (ex: createGroup, modfiyGroup etc)
         */

        String methodName = "processRequest(KnMessage)";
        knLogger.info(methodName, "ENTRY: Public Mgmt Request - ", msgObj);
        int operationId = -1;
        boolean synch = false;
        String operation = null;
        Object payLoad = msgObj.getPayLoad();
        IXDMRequestDTO inputDTO = null;

        KnXDMSubsProvInfoDTO updateSubscrOrUserAgentDTO = null;  //updateSubscriberName,updateUserAgent
        KnXDMCorpGroupInfoRequestDTO updateCampedGroupDTO = null;  //updateCampedGroup
        KnXDMSubsProvInfoDTO updateClientFSDTO = null;  //updateClientFS1
        KnXDMSubsInfoDTO getSubscriberConfigDocDTO = null; //getSubscriberConfigDocument
        KnXDMAuthListRequestDTO authListRequestDTO = null;
        KnXDMTGSSListRequestDTO tgssListReqDTO = null;
        KnXDMActivateInfoDTO activateSubscriberDTO = null;    //activateSubscriber
        KnXDMAddlTalkGroupRequestDTO addlTalkGroupRequestDTO = null;
        KnXDMPrivacyOptInRequestDTO knXDMPrivacyOptInRequestDTO = null;
        KnXDMMcsReqDTO knXDMMcsReqDTO = null;
        KnXDMSelectProfileDTO selectProfileDTO = null;
        KnXDMGetAuthorizedUserListDTO getAuthorizedUserListDTO = null;
        KnXDMRadioDeviceActivateInfoDTO radioDeviceActivateInfoDTO = null;
        if (payLoad instanceof IXDMRequestDTO) {
            inputDTO = (IXDMRequestDTO) payLoad;
            String operationType = inputDTO.getOperationType();
            operationId = Integer.parseInt(operationType);
        } /*else {
            // Throw back exception
        }    */
        String requestMDN = null;
        String activationKey = null;
        knLogger.info(methodName, "Operation Id : ", operationId);
        IXDMResponseDTO respDTO = null;
        IXDMFacadeIntf facadeIntf = new KnXDMFacadeImpl();

        try {
            switch (operationId) {
                case KnConstants.OPERATION_ID_ADD_CONTACTS:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    facadeIntf.addContacts(msgObj);
                    break;

                case KnConstants.OPERATION_ID_MODIFY_CONTACTS:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    facadeIntf.modifyContacts(msgObj);
                    break;

                case KnConstants.OPERATION_ID_DELETE_CONTACTS:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_DELETE_REQ_RCVD);
                    facadeIntf.deleteContacts(msgObj);
                    break;

                case KnConstants.OPERATION_ID_GET_CONTACTS_LIST_DETAILS:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_GET_REQ_RCVD);
                    facadeIntf.getContactList(msgObj);
                    break;

                case KnConstants.OPERATION_ID_CREATE_GROUP:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    facadeIntf.createGroup(msgObj);
                    break;

//            case KnConstants.OPERATION_ID_MODIFY_GROUP:
//                facadeIntf.modifyGroup(msgObj);
//                break;

                case KnConstants.OPERATION_ID_MODIFY_GROUP_NAME:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    facadeIntf.modifyGroupName(msgObj);
                    break;

                case KnConstants.OPERATION_ID_MODIFY_GROUP_MEMBER:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    facadeIntf.modifyGroupMember(msgObj);
                    break;

                case KnConstants.OPERATION_ID_ADD_GROUP_MEMBERS:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    facadeIntf.addGroupMember(msgObj);
                    break;

                case KnConstants.OPERATION_ID_DELETE_GROUP_MEMBERS:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_DELETE_REQ_RCVD);
                    facadeIntf.deleteGroupMember(msgObj);
                    break;

                case KnConstants.OPERATION_ID_DELETE_GROUP:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_DELETE_REQ_RCVD);
                    facadeIntf.deleteGroup(msgObj);
                    break;

                case KnConstants.OPERATION_ID_GET_GROUP_DETAILS:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_GET_REQ_RCVD);
                    facadeIntf.getGroupDocDetails(msgObj);
                    break;

//            case KnConstants.OPERATION_ID_GET_GROUP_LIST:
//                facadeIntf.getGroupList(msgObj);
//                break;

                case KnConstants.OPERATION_ID_GET_DIRECTORY:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_GET_REQ_RCVD);
                    facadeIntf.getDirectory(msgObj);
                    break;

                case KnConstants.OPERATION_ID_SOAP_GET_GROUP_LIST:
                    facadeIntf.getGroupList(msgObj);
                    break;

                case KnConstants.OPERATION_ID_SOAP_GET_GROUP_DETAILS:
                    facadeIntf.getGroupDetails(msgObj);
                    break;

                case KnConstants.OPERATION_ID_SOAP_GET_CONTACTLIST:
                    facadeIntf.getAllContactList(msgObj);
                    break;

                case KnConstants.OPERATION_ID_GET_RLS_DOC:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_GET_REQ_RCVD);
                    facadeIntf.getRLSDoc(msgObj);
                    break;

                //update subs name will be called in provisioning API
                case KnConstants.OPERATION_ID_UPDATE_SUBSCRIBER_NAME:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    operation = "updateSubscriberName";
                    if (inputDTO instanceof KnXDMSubsProvInfoDTO) {
                        updateSubscrOrUserAgentDTO = (KnXDMSubsProvInfoDTO) inputDTO;
                        requestMDN = updateSubscrOrUserAgentDTO.getMdn();
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for-" + updateSubscrOrUserAgentDTO.getMdn());

                    }
                    respDTO = facadeIntf.updateSubscriberName(msgObj);
                    synch = true;
                    break;
                //update Camped Group  will be called in Corp API
                case KnConstants.OPERATION_ID_UPDATE_CAMPED_GROUP:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    operation = "updateCampedGroup";
                    if (inputDTO instanceof KnXDMCorpGroupInfoRequestDTO) {
                        updateCampedGroupDTO = (KnXDMCorpGroupInfoRequestDTO) inputDTO;
                        requestMDN = updateCampedGroupDTO.getOwnerMdn();
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for-" + updateCampedGroupDTO.getOwnerMdn());

                    }
                    respDTO = facadeIntf.updateCampedGroup(msgObj);
                    synch = true;
                    break;

                //update subs client FS will be called in provisioning API
                case KnConstants.OPERATION_ID_UPDATE_CLIENT_FS1:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    operation = "updateClientFS1";
                    if (inputDTO instanceof KnXDMSubsProvInfoDTO) {
                        updateClientFSDTO = (KnXDMSubsProvInfoDTO) inputDTO;
                        requestMDN = updateClientFSDTO.getMdn();
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for-" + updateClientFSDTO.getMdn());

                    }
                    respDTO = facadeIntf.updateClientFS1(msgObj);
                    synch = true;
                    break;

                //update user agent
                case KnConstants.OP_ID_UPDATE_USERAGENT:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    operation = "updateUserAgent";
                    if (inputDTO instanceof KnXDMSubsProvInfoDTO) {
                        updateSubscrOrUserAgentDTO = (KnXDMSubsProvInfoDTO) inputDTO;
                        requestMDN = updateSubscrOrUserAgentDTO.getMdn();
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + updateSubscrOrUserAgentDTO.getMdn());
                    }
                    respDTO = facadeIntf.updateUserAgent(inputDTO);
                    synch = false;
                    break;

                //get subs config doc will be called in provisioning API
                case KnConstants.OPERATION_ID_GET_SUBS_CONFIG:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_GET_REQ_RCVD);
                    operation = "getSubscriberConfigDocument";
                    if (inputDTO instanceof KnXDMSubsInfoDTO) {
                        getSubscriberConfigDocDTO = (KnXDMSubsInfoDTO) inputDTO;
                        requestMDN = getSubscriberConfigDocDTO.getMdn();
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for-" + getSubscriberConfigDocDTO.getMdn());

                    }
                    respDTO = facadeIntf.getSubscriberConfigDocument(msgObj);
                    synch = true;
                    break;

                //get corp group details will be called in corporate API
                case KnConstants.OPERATION_ID_GET_CORP_GROUPDETAILS:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_GET_REQ_RCVD);
                    operation = "getCorpGroupDetails";
                    KnXDMCorpGroupInfoRequestDTO xdmRequestDto = (KnXDMCorpGroupInfoRequestDTO) inputDTO;
                    requestMDN = xdmRequestDto.getOwnerMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + requestMDN);
                    respDTO = facadeIntf.getCorpGroupDetails(inputDTO);
                    synch = true;
                    break;

                //get corp subs contact list will be called in corporate API
                case KnConstants.OPERATION_ID_GET_CORP_SUBS_CONTACTLIST:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_GET_REQ_RCVD);
                    operation = "getCorpSubscContactList";
                    KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) inputDTO;
                    requestMDN = xdmRequestDTO.getSubscriberMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + requestMDN);
                    respDTO = facadeIntf.getCorpSubscContactList(inputDTO);
                    synch = true;
                    break;

                case KnConstants.OPERATION_ID_UPDATE_SCANLIST:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    operation = "updateScanList";
                    KnXDMScanlistInfoDTO scanListInfoDTO = (KnXDMScanlistInfoDTO) inputDTO;
                    requestMDN = scanListInfoDTO.getMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + requestMDN);
                    respDTO = facadeIntf.updateScanList(inputDTO);
                    synch = true;
                    break;

                case KnConstants.OPERATION_ID_GET_SCANLIST:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_GET_REQ_RCVD);
                    operation = "getScanList";
                    if (inputDTO instanceof KnXDMScanlistInfoDTO) {
                        KnXDMScanlistInfoDTO getScanlistInfoDTO = (KnXDMScanlistInfoDTO) inputDTO;
                        requestMDN = getScanlistInfoDTO.getMdn();
                    }
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + requestMDN);
                    respDTO = facadeIntf.getScanList(inputDTO);
                    synch = true;
                    break;

                case KnConstants.OPERATION_ID_DELETE_SCANLIST:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_DELETE_REQ_RCVD);
                    operation = "deleteScanList";
                    KnXDMScanlistInfoDTO deleteScanListInfoDTO = (KnXDMScanlistInfoDTO) inputDTO;
                    requestMDN = deleteScanListInfoDTO.getMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + requestMDN);
                    respDTO = facadeIntf.deleteScanList(inputDTO);
                    synch = true;
                    break;

                case KnConstants.OPERATION_ID_MODIFY_DYNAMIC_CONTACT:
                    operation = "modifyDynamicContacts";
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    //todo audit
                    facadeIntf.modifyDynamicContacts(msgObj);
                    //synch = true;
                    break;

                case KnConstants.OPERATION_ID_DELETE_DYNAMIC_CONTACT:
                    operation = "deleteDynamicContacts";
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    //todo audit
                    facadeIntf.deleteDynamicContacts(msgObj);
                    //synch = true;
                    break;

                case KnConstants.OPERATION_ID_GET_DYNAMIC_CONTACT:
                    operation = "getDynamicContactList";
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    //todo audit
                    facadeIntf.getDynamicContacts(msgObj);
                    //synch = true;
                    break;

                case KnConstants.OPERATION_ID_GET_DYNAMIC_NON_SHARED_GROUP_DETAILS:
                    operation = "getDynamicNonSharedGrpDetails";
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    //todo audit
                    facadeIntf.getDynamicNonSharedGrpDetails(msgObj);
                    //synch = true;
                    break;

                case KnConstants.OPERATION_ID_GET_DYNAMIC_NON_SHARED_GROUP_LIST:
                    operation = "getDynamicNonSharedGrpDetails";
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    //todo audit
                    facadeIntf.getDynamicNonSharedGrpList(msgObj);
                    //synch = true;
                    break;

                case KnConstants.OPERATION_ID_DELETE_DYNAMIC_NON_SHARED_GROUP:
                    operation = "getDeleteDynamicNonSharedGrp";
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    //todo audit
                    facadeIntf.deleteDynamicNonSharedGrp(msgObj);
                    // synch = true;
                    break;

                case KnConstants.OPERATION_ID_CREATE_DYNAMIC_NON_SHARED_GROUP:
                    operation = "createNonSharedGroup";
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    //todo audit
                    facadeIntf.createNonSharedGroup(msgObj);
                    // synch = true;
                    break;

                case KnConstants.OPERATION_ID_MODIFY_DYNAMIC_NON_SHARED_GROUP:
                    operation = "modifyNonSharedGroup";
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    //todo audit
                    facadeIntf.modifyNonSharedGroup(msgObj);
                    // synch = true;
                    break;


                case KnConstants.OPERATION_ID_GET_AUTHORIZATIONLIST:
                    operation = "getAuthorizationList";
                    authListRequestDTO = (KnXDMAuthListRequestDTO) inputDTO;
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_GET_REQ_RCVD);
                    requestMDN = authListRequestDTO.getAuthMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + authListRequestDTO.getAuthMdn());
                    respDTO = facadeIntf.getAuthorizationList(inputDTO);
                    synch = true;
                    break;

                case KnConstants.OPERATION_ID_UPDATE_AUTHORIZATIONLIST:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    operation = "updateAuthorizationList";
                    authListRequestDTO = (KnXDMAuthListRequestDTO) inputDTO;
                    requestMDN = authListRequestDTO.getAuthMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + authListRequestDTO.getAuthMdn());
                    respDTO = facadeIntf.updateAuthorizationList(msgObj);
                    synch = true;
                    break;
                case KnConstants.OPERATION_ID_GET_EMERGENCY_CONFIGDOC:
                    operation = "getEmergencyConfigDoc";
                    authListRequestDTO = (KnXDMAuthListRequestDTO) inputDTO;
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_GET_REQ_RCVD);
                    requestMDN = authListRequestDTO.getAuthMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + authListRequestDTO.getAuthMdn());
                    respDTO = facadeIntf.getEmergencyConfigDoc(inputDTO);
                    synch = true;
                    break;
                case KnConstants.OPERATION_ID_GET_SUBSCRIBER_TG_LIST:
                    operation = "getGroupUsageListDoc";
                    addlTalkGroupRequestDTO = (KnXDMAddlTalkGroupRequestDTO) inputDTO;
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_GET_REQ_RCVD);
                    requestMDN = addlTalkGroupRequestDTO.getMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + addlTalkGroupRequestDTO.getMdn());
                    respDTO = facadeIntf.getSubscriberTGList(inputDTO);
                    synch = true;
                    break;
                case KnConstants.OPERATION_ID_GET_GROUP_USAGE_LIST_DOC:
                    operation = "getGroupUsageListDoc";
                    authListRequestDTO = (KnXDMAuthListRequestDTO) inputDTO;
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_GET_REQ_RCVD);
                    requestMDN = authListRequestDTO.getAuthMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + authListRequestDTO.getAuthMdn());
                    respDTO = facadeIntf.getGroupUsageListDoc(inputDTO);
                    synch = true;
                    break;
                case KnConstants.OPERATION_ID_GET_TGSSLIST:
                    operation = "getTGSSList";
                    tgssListReqDTO = (KnXDMTGSSListRequestDTO) inputDTO;
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_GET_REQ_RCVD);
                    requestMDN = tgssListReqDTO.getMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + tgssListReqDTO.getMdn());
                    respDTO = facadeIntf.getTGSSList(inputDTO);
                    synch = true;
                    break;

                case KnConstants.OPERATION_ID_UPDATE_TGSSLIST:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_PUT_REQ_RCVD);
                    operation = "updateTGSSList";
                    tgssListReqDTO = (KnXDMTGSSListRequestDTO) inputDTO;
                    requestMDN = tgssListReqDTO.getMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + tgssListReqDTO.getMdn());
                    respDTO = facadeIntf.updateTGSSList(inputDTO);
                    synch = true;
                    break;
                case KnConstants.OPERATION_ID_DELETE_TGSSLIST:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.TOTAL_NUM_XCAP_DELETE_REQ_RCVD);
                    operation = "deleteTGSSList";
                    tgssListReqDTO = (KnXDMTGSSListRequestDTO) inputDTO;
                    requestMDN = tgssListReqDTO.getMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + tgssListReqDTO.getMdn());
                    respDTO = facadeIntf.deleteTGSSList(inputDTO);
                    synch = true;
                    break;
                case KnConstants.OPERATION_ID_GET_SUBSCRIBERS_GRPLIST:
                    operation = "getSubscriberCorpGroupList";
                    KnXDMCorpSubscInfoRequestDTO subsGrplistDto = (KnXDMCorpSubscInfoRequestDTO) inputDTO;
                    requestMDN = subsGrplistDto.getSubscriberMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received -" + requestMDN);
                    respDTO = facadeIntf.getSubscriberCorpGroupList(inputDTO);
                    synch = true;
                    break;
                case KnConstants.OPERATION_ID_SEARCH_CORP_ADD_BOOK:
                    operation = "searchCorpAddressBook";
                    if (inputDTO instanceof KnXDMSearchCorpAddressBookRequestDTO) {
                        KnXDMSearchCorpAddressBookRequestDTO corpAddressBookDto = (KnXDMSearchCorpAddressBookRequestDTO) inputDTO;
                        requestMDN = corpAddressBookDto.getMdn();
                    }
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received -" + requestMDN);
                    respDTO = facadeIntf.searchCorpAddressBook(inputDTO);
                    synch = true;
                    break;
                case KnConstants.OP_ID_DEVICE_ACTIVATION:
                    operation = "deviceActivation";
                    if (inputDTO instanceof KnXDMActivateInfoDTO) {
                        activateSubscriberDTO = (KnXDMActivateInfoDTO) inputDTO;
                        activationKey = activateSubscriberDTO.getActivationKey();
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, activationKey);
                    }
                    respDTO = facadeIntf.deviceActivation(inputDTO);
                    synch = true;
                    break;

                case KnConstants.OP_ID_USER_LOGIN:
                    operation = "userLogin";
                    if (inputDTO instanceof KnXDMActivateInfoDTO) {
                        activateSubscriberDTO = (KnXDMActivateInfoDTO) inputDTO;
                        requestMDN = activateSubscriberDTO.getMdn();
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, activateSubscriberDTO.getMdn());
                    }
                    respDTO = facadeIntf.userLogin(msgObj);
                    synch = true;
                    break;

                case KnConstants.SELECT_PROFILE_MDN_OPID:
                    operation = "selectedProfileMdn";
                    if (inputDTO instanceof KnXDMSelectProfileDTO) {
                        selectProfileDTO = (KnXDMSelectProfileDTO) inputDTO;
                        requestMDN = selectProfileDTO.getMdn();
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + selectProfileDTO.getMdn());
                    }
                    respDTO = facadeIntf.selectProfileMdn(inputDTO);
                    synch = true;
                    break;
                case KnConstants.OP_ID_GET_EMERGENCY_DETAILS:
                    operation = "subsEmergencyDetails";
                    if (inputDTO instanceof KnXDMActivateInfoDTO) {
                        activateSubscriberDTO = (KnXDMActivateInfoDTO) inputDTO;
                        requestMDN = activateSubscriberDTO.getMdn();
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + activateSubscriberDTO.getMdn());
                    }
                    respDTO = facadeIntf.getSubsEmergencyDetails(inputDTO);
                    synch = true;
                    break;
                case KnConstants.OP_ID_ACTIVATE_SUBS:
                    operation = "activateSubscriber";
                    if (inputDTO instanceof KnXDMActivateInfoDTO) {
                        activateSubscriberDTO = (KnXDMActivateInfoDTO) inputDTO;
                        activationKey = activateSubscriberDTO.getActivationKey();
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received with activation key - " + activateSubscriberDTO.getActivationKey());
                    }

                    respDTO = facadeIntf.activateSubscriber(msgObj);
                    KnXDMActivateRespDTO actRespObj = (KnXDMActivateRespDTO) respDTO;
                    requestMDN = actRespObj.getMdn();
                    synch = true;
                    break;
                case KnConstants.OPERATION_ID_PRIVACYOPTIN:
                    operation = "privacyOptStatus";
                    knXDMPrivacyOptInRequestDTO = (KnXDMPrivacyOptInRequestDTO) inputDTO;
                    requestMDN = knXDMPrivacyOptInRequestDTO.getMdn();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + knXDMPrivacyOptInRequestDTO.getMdn());
                    respDTO = facadeIntf.updatePrivacyOptStatus(msgObj);
                    synch = true;
                    break;
                case KnConstants.OPERATION_ID_HEALTH_PING:
                    facadeIntf.isAlive(msgObj);
                    return true;

                case KnConstants.OPERATION_ID_GET_MCPTT_UE_CONFIG:
                    operation = "GetMCPTTUEConfig";
                    knXDMMcsReqDTO = (KnXDMMcsReqDTO) inputDTO;
                    requestMDN = knXDMMcsReqDTO.getMcId();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + knXDMMcsReqDTO.getMcId());
                    respDTO = facadeIntf.getMCPTTUEConfig(inputDTO);
                    synch = true;
                    break;
                case KnConstants.OPERATION_ID_GET_MCPTT_USER_PROFILE:
                    operation = "getMCPTTUserProfile";
                    knXDMMcsReqDTO = (KnXDMMcsReqDTO) inputDTO;
                    requestMDN = knXDMMcsReqDTO.getMcId();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + knXDMMcsReqDTO.getMcId());
                    respDTO = facadeIntf.getMCPTTUserProfile(msgObj);
                    synch = true;
                    break;
                case KnConstants.OPERATION_ID_GET_MCPTT_SERVICE_CONFIG:
                    operation = "getMCPTTServiceConfig";
                    knXDMMcsReqDTO = (KnXDMMcsReqDTO) inputDTO;
                    requestMDN = knXDMMcsReqDTO.getMcpttID();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + knXDMMcsReqDTO.getMcpttID());
                    respDTO = facadeIntf.getMCPTTServiceConfig(inputDTO);
                    synch = true;
                    break;

                case KnConstants.OPERATION_ID_GET_MCDATA_UE_CONFIG:
                    operation = "getMCDATAUEConfig";
                    knXDMMcsReqDTO = (KnXDMMcsReqDTO) inputDTO;
                    requestMDN = knXDMMcsReqDTO.getMcId();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + knXDMMcsReqDTO.getMcId());
                    respDTO = facadeIntf.getMCDATAUEConfig(inputDTO);
                    synch = true;
                    break;

                case KnConstants.OPERATION_ID_GET_MCDATA_USER_PROFILE:
                    operation = "getMCDATAUserProfile";
                    knXDMMcsReqDTO = (KnXDMMcsReqDTO) inputDTO;
                    requestMDN = knXDMMcsReqDTO.getMcId();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + knXDMMcsReqDTO.getMcId());
                    respDTO = facadeIntf.getMCDATAUserProfile(msgObj);
                    synch = true;
                    break;

                case KnConstants.OPERATION_ID_GET_MCDATA_SERVICE_CONFIG:
                    operation = "getMCDATAServiceConfig";
                    knXDMMcsReqDTO = (KnXDMMcsReqDTO) inputDTO;
                    requestMDN = knXDMMcsReqDTO.getMcpttID();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + knXDMMcsReqDTO.getMcpttID());
                    respDTO = facadeIntf.getMCDATAServiceConfig(inputDTO);
                    synch = true;
                    break;

                case KnConstants.OPERATION_ID_GET_MCVIDEO_UE_CONFIG:
                    operation = "getMCVIDEOUEConfig";
                    knXDMMcsReqDTO = (KnXDMMcsReqDTO) inputDTO;
                    requestMDN = knXDMMcsReqDTO.getMcId();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + knXDMMcsReqDTO.getMcId());
                    respDTO = facadeIntf.getMCVideoUEConfig(inputDTO);
                    synch = true;
                    break;

                case KnConstants.OPERATION_ID_GET_MCVIDEO_USER_PROFILE:
                    operation = "getMCVIDEOUserProfile";
                    knXDMMcsReqDTO = (KnXDMMcsReqDTO) inputDTO;
                    requestMDN = knXDMMcsReqDTO.getMcId();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + knXDMMcsReqDTO.getMcId());
                    respDTO = facadeIntf.getMCVideoUserProfile(msgObj);
                    synch = true;
                    break;

                case KnConstants.OPERATION_ID_GET_MCVIDEO_SERVICE_CONFIG:
                    operation = "getMCVIDEOServiceConfig";
                    knXDMMcsReqDTO = (KnXDMMcsReqDTO) inputDTO;
                    requestMDN = knXDMMcsReqDTO.getMcpttID();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + knXDMMcsReqDTO.getMcpttID());
                    respDTO = facadeIntf.getMCVideoServiceConfig(inputDTO);
                    synch = true;
                    break;

                case KnConstants.OPERATION_ID_GET_MCS_GROUP_DOC:
                    operation = "getMCSGroupDoc";
                    knXDMMcsReqDTO = (KnXDMMcsReqDTO) inputDTO;
                    requestMDN = knXDMMcsReqDTO.getMcpttID();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + knXDMMcsReqDTO.getMcpttID());
                    respDTO = facadeIntf.getMCSGroupDoc(inputDTO);
                    synch = true;
                    break;
                case KnConstants.OPERATION_ID_GET_MCS_USER_DIR:
                    operation = "getMCSUserDir";
                    knXDMMcsReqDTO = (KnXDMMcsReqDTO) inputDTO;
                    requestMDN = knXDMMcsReqDTO.getMcId();
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received - " + knXDMMcsReqDTO.getMcId());
                    respDTO = facadeIntf.getMCSUserDir(inputDTO);
                    synch = true;
                    break;
                case KnConstants.OP_ID_RADIO_DEVICE_ACTIVATION:
                    operation = "radioDeviceActivation";
                    if (inputDTO instanceof KnXDMRadioDeviceActivateInfoDTO) {
                        radioDeviceActivateInfoDTO = (KnXDMRadioDeviceActivateInfoDTO) inputDTO;
                        requestMDN = radioDeviceActivateInfoDTO.getDeviceId();
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for Radio Device: ", radioDeviceActivateInfoDTO.getDeviceId());
                    }
                    respDTO = facadeIntf.radioDeviceActivation(inputDTO);
                    synch = true;
                    break;
                case KnConstants.OPERATION_ID_GET_AUTHORIZED_USERLIST:
                    operation = "getAuthorizedUserList";
                    if (inputDTO instanceof KnXDMGetAuthorizedUserListDTO) {
                        getAuthorizedUserListDTO = (KnXDMGetAuthorizedUserListDTO) inputDTO;
                        requestMDN = getAuthorizedUserListDTO.getMdn();
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + getAuthorizedUserListDTO.getMdn());
                    }
                    respDTO = facadeIntf.getAuthorizedUserList(inputDTO);
                    synch = true;
                    break;
                default:
                    respDTO = facadeIntf.unKnownOperation(inputDTO);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception in Req handler", e);
            respDTO = facadeIntf.unKnownOperation(inputDTO);
        }

        if (synch) {
            boolean status = respHandler.processResponse(respDTO, msgObj);
            knLogger.info(methodName, "sync Message Sent Status-  ", status);
        } else {
            boolean status = respHandler.deleteAppIntfMsg(msgObj);
            knLogger.debug(methodName, "Message deleted from App interface table  status - ", status);
        }

        if (respDTO != null) {

            if (respDTO.getResponseStatus() == 0) {
                if (null != activationKey) {
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.SUCCESS, activationKey);
                } else {
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.SUCCESS, "Operation Successful for - " + requestMDN);
                }
            } else {
                if (null != requestMDN) {
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.FAILURE, "Operation failed - " + requestMDN, respDTO.getResponseCode());
                } else {
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.FAILURE, "Operation failed ", respDTO.getResponseCode());
                }
            }
        }
        knLogger.info(methodName, "Message Sent Status - true ");
        return true;
    }
}
