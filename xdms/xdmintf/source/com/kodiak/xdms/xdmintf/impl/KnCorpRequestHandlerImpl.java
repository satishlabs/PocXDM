/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpRequestHandlerImpl.java
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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.common.commdto.common.KnCorpDetailsDTO;
import com.kodiak.common.commdto.common.KnIdDetails;
import com.kodiak.common.commdto.common.KnIdValue;
import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.request.KnXDMCorpClientActRequestDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnXDMCorpClientActResponseDTO;
import com.kodiak.common.commdto.response.KnXDMCorpRespDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.xdms.xdmintf.IRequestHandlerIntf;
import com.kodiak.xdms.xdmintf.IResponseHandler;
import com.kodiak.xdms.xdmintf.IXDMFacadeIntf;

import com.kodiak.logger.KnLogger;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kodiak.xdms.mediator.KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR;
import static com.kodiak.xdms.server.common.resources.KnConstants.FALSE;

public class KnCorpRequestHandlerImpl implements IRequestHandlerIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpRequestHandlerImpl.class);

    private static final String SUCCESS_MSG = "Operation is successfull";
    private static final String FAILURE_MSG = "Operation failed with error";
    private static final String PAM_SUBS_AUDIT = "4008";
    private static final String CORP_AUDIT = "4002";

    private IXDMFacadeIntf facadeIntf;
    private IResponseHandler respHandler;
    private KnAuditHelper audit;
    private KnAuditHelper pamSubsAudit;

    public KnCorpRequestHandlerImpl() {
        final String methodName = "KnCorpRequestHandlerImpl()";
        facadeIntf = new KnXDMFacadeImpl();
        respHandler = new KnResponseHandlerImpl();
        knLogger.info(methodName, "Initializing Audit");
        audit = KnAuditHelper.getAuditLogger(CORP_AUDIT);
        knLogger.info(methodName, "corp Audit initialized - ", audit);
        pamSubsAudit = KnAuditHelper.getAuditLogger(PAM_SUBS_AUDIT);
        knLogger.info(methodName, "pam Audit initialized - ", pamSubsAudit);
    }

    public boolean processRequest(KnMessage msgObj) {
        /**
         * 1. Get the payload from the msgObj
         * 2. Check if the payload is instance of IXDMRequestDTO
         * 3. Get the operation type
         * 4. Switch to the respective operation (ex: createGroup, modfiyGroup etc)
         */
        final String methodName = "processRequest(KnMessage)";
        knLogger.entry(methodName, msgObj);
        int operationId = -1;
        KnXDMCorpClientActRequestDTO clientActReqDTO = new KnXDMCorpClientActRequestDTO();

        Object payLoad = msgObj.getPayLoad();
        IXDMRequestDTO requestDTO = null;
        long latency = 0;
        long requestTime = System.currentTimeMillis();
        if (payLoad instanceof IXDMRequestDTO) {
            requestDTO = (IXDMRequestDTO) payLoad;
            String operationType = requestDTO.getOperationType();
            operationId = Integer.parseInt(operationType);
        } else if (payLoad instanceof String) {
            knLogger.info(methodName, "The String request recived from the Rest service is ", payLoad);
            knLogger.info(methodName, "Convert the json string to object");
            try {
                knLogger.info(methodName, "Inside the try block to convert the object");
                ObjectMapper mapper = new ObjectMapper();
                knLogger.info(methodName, "ObjectMapper - ", mapper);
                // Convert JSON string to Object
                clientActReqDTO = mapper.readValue((String) payLoad, KnXDMCorpClientActRequestDTO.class);
                knLogger.info(methodName, "clientActReqDTO", clientActReqDTO);
                String operationType = clientActReqDTO.getOperationType();
                knLogger.info(methodName, "clientActReqDTO", clientActReqDTO);
                operationId = Integer.parseInt(operationType);
                knLogger.info(methodName, "operationId", operationId);
            } catch (JsonGenerationException e) {
                e.printStackTrace();
                knLogger.error(methodName, "JsonGenerationException", e);
            } catch (JsonMappingException e) {
                e.printStackTrace();
                knLogger.error(methodName, "JsonMappingException", e);
            } catch (IOException e) {
                e.printStackTrace();
                knLogger.error(methodName, "IOException", e);
            } catch (Exception e) {
                e.printStackTrace();
                knLogger.error(methodName, "Exception", e);
            }
        } else {
            //todo process failure response..need to return error response to Web layer.
            knLogger.error(methodName, "Unexpected Class type - ", payLoad.getClass());
            knLogger.error(methodName, "Can't proceed further. Dropping the request and returning false");
            return FALSE;   //todo need to return error response to Web layer
        }
        knLogger.debug(methodName, "RequestDTO - ", requestDTO);
        String operation = null;
        IXDMResponseDTO respDto = null;
        try {
            switch (operationId) {
                //Authentication
                case KnConstants.OPERATION_ID_AUTHENTICATE:
                    operation = "authenticate";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.authenticate(requestDTO);
                    break;

                //Contact Management
                case KnConstants.OPERATION_ID_GET_CORP_MASTERLIST:
                    operation = "getCorpMasterList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpMasterList(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_SUBS_CONTACTLIST:
                    operation = "getCorpSubscContactList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpSubscContactList(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_MODIFYSUBSC_CONTACTS:
                    operation = "modifyCorpSubscContacts";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.modifyCorpSubscContacts(msgObj);
                    break;

                case KnConstants.OPERATION_ID_PUSH_SUBLIST:
                    operation = "pushSublists";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.pushSublists(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_REMOVE_SUBLIST:
                    operation = "removeSublist";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.removeSublist(msgObj);
                    break;

                case KnConstants.OPERATION_ID_ADD_CORPCONTACT:
                    operation = "addExternalContacts";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.addCorpContacts(msgObj);
                    break;

                case KnConstants.OPERATION_ID_MODIFY_CORPCONTACT:
                    operation = "modifyExternalContacts";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.modifyCorpContacts(msgObj);
                    break;

                case KnConstants.OPERATION_ID_REMOVE_CORPCONTACT:
                    operation = "removeExternalContacts";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.removeCorpContacts(msgObj);
                    break;

                case KnConstants.OPERATION_ID_GET_CORPCONTACTDETAILS:
                    operation = "getCorpContactDetails";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpContactDetails(requestDTO);
                    break;

                //sublist Management
                case KnConstants.OPERATION_ID_CREATE_SUBLIST:
                    operation = "createSublist";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.createSublist(msgObj);
                    break;

                case KnConstants.OPERATION_ID_MODIFY_SUBLIST:
                    operation = "modifySublist";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.modifySublist(msgObj);
                    break;

                case KnConstants.OPERATION_ID_DELETE_SUBLIST:
                    operation = "deleteSublist";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.deleteSublist(msgObj);
                    break;

                case KnConstants.OPERATION_ID_GET_SUBLIST_DETAILS:
                    operation = "getSublistDetails";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getSublistDetails(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_ALL_SUBLIST:
                    operation = "getAllSublist";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getAllSublist(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_DISTRIBUTION_LIST:
                    operation = "getDistributionList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getDistributionList(requestDTO);
                    break;

                //Group Management

                case KnConstants.OPERATION_ID_CREATE_CORP_GROUP:
                    operation = "createCorpGroup";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.createCorpGroup(msgObj);
                    break;

                case KnConstants.OPERATION_ID_MODIFY_CORPGROUP:
                    operation = "modifyCorpGroup";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.modifyCorpGroup(msgObj);
                    break;

                case KnConstants.OPERATION_ID_DELETE_CORPGROUP:
                    operation = "deleteCorpGroup";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.deleteCorpGroup(msgObj);
                    break;

                case KnConstants.OPERATION_ID_GROUP_REHOME:
                    operation = "groupRehome";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.groupRehome(msgObj);
                    break;

                case KnConstants.OPERATION_ID_GET_GROUPDETAILS:
                    operation = "getCorpGroupDetails";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpGroupDetails(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_GROUPLIST:
                    operation = "getCorpGroupList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpGroupList(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_GROUP_DETAILS_LIST:
                    operation = "getCorpGroupDetailsList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpGroupDetailsList(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_SUBSCRIBERS_GRPLIST:
                    operation = "getCorpSubscriberGroupList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpSubscriberGroupList(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_SUBS_EMAILID:
                    operation = "getSubscriberEmailId";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getSubscriberEmailId(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_SAVE_ACTIVATION_CODE:
                    operation = "saveActivationCode";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.saveActivationCode(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_CORP_CUSTOM:
                    operation = "CORP_CUSTOM";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.customCorpOperation(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GENERATE_ACTIVATION_CODES:
                    operation = "generateActivationCodes";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.generateActivationCodes(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_MAIL_INFO:
                    operation = "getMailInfo";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getMailInfo(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_SEND_ACTIVATION_MAIL:
                    operation = "sendActivationMail";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.sendActivationMail(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_HEALTH_PING:
                    facadeIntf.isAlive(msgObj);
                    return true;
                case KnConstants.OPERATION_ID_MODIFY_SUBSC_FEATURE_BIT:
                    operation = "modifySubscCorpFeature";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.modifySubscCorpFeature(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_MODIFY_SUBSCRIBER_SCAN_LIST:
                    operation = "modifySubscriberScanList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.modifySubscriberScanList(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_SUBSCRIBER_SCAN_LIST:
                    operation = "getSubscriberScanList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getSubscriberScanList(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_BILLING_NUMBERS:
                    operation = "getAllBillingMdns";
                    pamSubsAudit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getAllBillingMdns(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_LICENSE_SUBS:
                    operation = "getLicenseSubs";
                    pamSubsAudit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getLicenseSubs(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_MARK_SUBS_FOR_DELETION:
                    operation = "markSubsForDeletion";
                    pamSubsAudit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.markSubsForDeletion(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UPDATE_BILLING_NAME:
                    operation = "updateBillingName";
                    pamSubsAudit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.updateBillingName(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_LICENSE_AUTHENTICATE:
                    operation = "licenseAuthenticate";
                    pamSubsAudit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.licenseAuthenticate(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_PAM_SUBS_CORP_CUSTOM:
                    operation = "getAllBillingMdns_custom";
                    pamSubsAudit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.customCorpOperation(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_SUBSCR_REVERSE_CONTACTS:
                    operation = "getSubscrReverseContacts";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getSubscrReverseContacts(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_SUBSCR_SUBLISTS:
                    operation = "getSubscrSublists";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getSubscrSublists(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_POC_LINKED_GROUP_LIST:
                    operation = "getPocLinkedGroupList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getPocLinkedGroupList(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_REMOVE_SUBSCONTACTS:
                    operation = "removeSubscribersContacts";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.removeSubscribersContacts(msgObj);
                    break;
                case KnConstants.OPERATION_ID_REMOVE_SUBSFROM_ALLSUBLIST:
                    operation = "removeSubscribersAllSublist";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.removeSubscribersAllSublist(msgObj);
                    break;
                case KnConstants.OPERATION_ID_REMOVE_SUB_FROM_ALLGROUPS:
                    operation = "removeSubscribersAllGroups";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.removeSubscribersAllGroups(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_SUBSCRIBER_FEATURE_SETS:
                    operation = "getAllCorpSubscrFeatureSets";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getAllCorpSubscrFeatureSets(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UPDATE_SUBSCRIBER_CORPADMINFS:
                    operation = "updateSubscriberCorpAdminFs";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.updateCorpAdminFS(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_ACTIVATION_CODE:
                    operation = "getActivationCode";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getActivationCode(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_CORP_SUBSCRIBER_DETAILS:
                    operation = "getSubscriberDetails";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpSubscriberDetails(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_SWITCH_SUBSCRIBER_CLIENT_PROFILE:
                    operation = "switchConvergedClient";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.switchConvergedClient(msgObj);
                    break;
                case KnConstants.OPERATION_ID_REST_GET_SUBSCR_ACTIVATION_CODE:
                    operation = "getSubscriberActivationCode";
                    knLogger.info(methodName, "Inside getSubscriberActivationCode");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getSubscrActivationCode((IXDMRequestDTO) clientActReqDTO);
                    break;
                case KnConstants.OPERATION_ID_REST_GENERATE_OTP:
                    operation = "generateOTP";
                    knLogger.info(methodName, "Inside generateOTP");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.generateOTP((IXDMRequestDTO) clientActReqDTO);
                    break;
                case KnConstants.OPERATION_ID_REST_VALIDATE_OTP:
                    operation = "validateOTP";
                    knLogger.info(methodName, "Inside validateOTP");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.validateOTP((IXDMRequestDTO) clientActReqDTO);
                    break;
                case KnConstants.OPERATION_ID_REST_GET_CORP_HIERARCHY:
                    operation = "getCorpHierachy";
                    knLogger.info(methodName, "Inside getCorpHierachy");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpBanFanList((IXDMRequestDTO) clientActReqDTO);
                    break;
                case KnConstants.OPERATION_ID_REST_GET_USER_DETAILS:
                    operation = "getUserProfile";
                    knLogger.info(methodName, "Inside getUserDetails");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getUserProfile((IXDMRequestDTO) clientActReqDTO);
                    break;
                case KnConstants.OPERATION_ID_UPDATE_CORP_SUBSCRIBER:
                    operation = "updateCorpSubscriber";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.updateCorpSubscriber(msgObj);
                    break;
                case KnConstants.OPERATION_ID_REST_GENERATE_SUBSCR_ACTIVATION_CODE:
                    operation = "generateActivationCodeIDMIntf";
                    knLogger.info(methodName, "Inside generateActivationCodeIDMIntf");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.generateActivationCodeIDMIntf((IXDMRequestDTO) clientActReqDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_CORP_SUBS_USER_PROFILE:
                    operation = "getCorpSubsUserProfile";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpSubsUserProfile(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_RESET_CORP_SUBS_USER_PASSOWRD:
                    operation = "resetCorpSubsUserPassword";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.resetCorpSubsUserPassword(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_RESEND_CORP_SUBS_VERIFICATION_EMAIL:
                    operation = "resendCorpSubsVerificationEmail";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.resendCorpSubsVerificationEmail(requestDTO);
                    break;
                case KnConstants.OP_ID_FORCE_SYNC:
                    operation = "forceSync";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.forceSync(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_SET_TARGET_USER_PERMISSIONS:
                    operation = "setTargetPermission";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.setTargetPermissions(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_TARGET_USER_PERMISSIONS:
                    operation = "getTargetPermission";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getTargetPermissions(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_AUTHORIZED_MDN_LIST:
                    operation = "getAuthorizedMdnList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getAuthorizedMdnList(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_SET_EMERGENCY_ATTRIBUTES:
                    operation = "setSubsEmergencyAttributes";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.setSubsEmergencyAttributes(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_EMERGENCY_ATTRIBUTES:
                    operation = "getSubsEmergencyAttributes";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getSubsEmergencyAttributes(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UPDATE_SUBS_ALIAS_ENTITIES:
                    operation = "updateSubsAliasEntities";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.updateSubsAliasEntities(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GENERATE_TEMP_PASSWORD:
                    operation = "generateTempPassword";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.generateTempPassword(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_USER_EMERGENCY:
                    operation = "getUserEmergDest";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getUserEmergDest(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_MODIFY_SUBSCRIBER_TG_LIST:
                    operation = "modifySubscriberTGList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.modifySubscriberTGList(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_SUBSCRIBER_TG_LIST:
                    operation = "getSubscriberTGList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getSubscriberTGList(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_DELETE_SUBSCRIBER_TG_LIST:
                    operation = "deleteTGList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.deleteTGList(msgObj);
                    break;
                case KnConstants.OPERATION_ID_REST_SEND_SMS:
                    operation = "sendSMS";
                    knLogger.info(methodName, "Inside sendSMS");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.sendSMS((IXDMRequestDTO) clientActReqDTO);
                    break;
                case KnConstants.OPERATION_ID_SEND_TEMP_PASSWORD:
                    operation = "sendTempPassword";
                    knLogger.info(methodName, "Inside sendTempPassword");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.sendTempPassword((IXDMRequestDTO) requestDTO);
                    break;
                case KnConstants.OPERATION_ID_ADD_BULK_GROUPS:
                    operation = "addBulkGroupsToSubscriber";
                    knLogger.info(methodName, "Inside addBulkGroupsToSubscriber");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.addBulkGroupsToSubscriber(msgObj);
                    break;
                case KnConstants.OPERATION_ID_CREATE_TALK_SCAN_LIST:
                    operation = "createSubsATGScanList";
                    knLogger.info(methodName, "Inside createSubsATGScanList");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.createSubsATGScanList(msgObj);
                    break;
                case KnConstants.OPERATION_ID_CREATE_OSIM_LIST:
                    operation = "createOSMList";
                    knLogger.info(methodName, "Inside createOSMList");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.createOSMList((IXDMRequestDTO) requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UPDATE_OSM_LIST:
                    operation = "updateOSMList";
                    knLogger.info(methodName, "Inside updateOSMList");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.updateOSMList((IXDMRequestDTO) requestDTO);
                    break;
                case KnConstants.OPERATION_ID_DELETE_OSM_LIST:
                    operation = "deleteOSMList";
                    knLogger.info(methodName, "Inside deleteOSMList");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.deleteOSMList(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_OSM_LIST:
                    operation = "getOSMList";
                    knLogger.info(methodName, "Inside getOSMList");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getOSMList((IXDMRequestDTO) requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_OSM_LIST_DETAILS:
                    operation = "getOSMListDetails";
                    knLogger.info(methodName, "Inside getOSMListDetails");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getOSMListDetails((IXDMRequestDTO) requestDTO);
                    break;
                case KnConstants.OPERATION_ASSIGN_OSMLISTID_TO_GROUP:
                    operation = "assignOSMIdToGroup";
                    knLogger.info(methodName, "Inside assignOSMIdToGroup");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.assignOSMIdToGroup(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_OSM_GROUP_LIST:
                    operation = "getOSMGroupList";
                    knLogger.info(methodName, "Inside getOSMGroupList");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getOSMGroupList((IXDMRequestDTO) requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UPDATE_CORP_SUBSCRIBER_MCS_IDS:
                    operation = "updateCorpSubscriberMCSIds";
                    knLogger.info(methodName, "Inside updateCorpSubscriberMCSIds");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.updateCorpSubscriberMCSIds(msgObj);
                    break;
                case KnConstants.OPERATION_ID_CREATE_USER_PROFILE:
                    operation = "createUserProfile";
                    knLogger.info(methodName, "Inside createUserProfile");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.createUserProfile((IXDMRequestDTO) requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UPDATE_USER_PROFILE:
                    operation = "updateUserProfile";
                    knLogger.info(methodName, "Inside updateUserProfile");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.updateUserProfile((IXDMRequestDTO) requestDTO);
                    break;
                case KnConstants.OPERATION_ID_DELETE_USER_PROFILE:
                    operation = "deleteUserProfile";
                    knLogger.info(methodName, "Inside deleteUserProfile");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.deleteUserProfile((IXDMRequestDTO) requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_USER_PROFILE_DETAILS:
                    operation = "getUserProfileDetails";
                    knLogger.info(methodName, "Inside getUserProfileDetails");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    knLogger.debug(methodName, "--->requestDTO - ", requestDTO);
                    respDto = facadeIntf.getUserProfileDetails((IXDMRequestDTO) requestDTO);
                    knLogger.debug(methodName, "--->respDto - ", respDto);
                    break;
                case KnConstants.OPERATION_ID_GET_USER_PROFILE_LIST:
                    operation = "getUserProfileList";
                    knLogger.info(methodName, "Inside getUserProfileList");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    knLogger.debug(methodName, "--->requestDTO - ", requestDTO);
                    respDto = facadeIntf.getUserProfileList((IXDMRequestDTO) requestDTO);
                    knLogger.debug(methodName, "--->respDto - ", respDto);
                    break;
                case KnConstants.OPERATION_ID_GET_USER_PROFILE_LIST_BY_NAME:
                    operation = "getUserProfileListByName";
                    knLogger.info(methodName, "Inside getUserProfileListByName");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    knLogger.debug(methodName, "--->requestDTO - ", requestDTO);
                    respDto = facadeIntf.getUserProfileListByName((IXDMRequestDTO) requestDTO);
                    knLogger.debug(methodName, "--->respDto - ", respDto);
                    break;
                case KnConstants.OPERATION_ID_GET_SUBSCRIBER_USER_PROFILE_LIST:
                    operation = "getSubscriberUserProfileList";
                    knLogger.info(methodName, "Inside getSubscriberUserProfileList");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    knLogger.debug(methodName, "--->requestDTO - ", requestDTO);
                    respDto = facadeIntf.getSubscriberUserProfileList((IXDMRequestDTO) requestDTO);
                    knLogger.debug(methodName, "--->respDto - ", respDto);
                    break;
                case KnConstants.OPERATION_ID_ASSIGN_USER_PROFILE:
                    operation = "assignUserProfile";
                    knLogger.info(methodName, "Inside assignUserProfile");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.assignUserProfile((IXDMRequestDTO) requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UNASSIGN_USER_PROFILE:
                    operation = "unassignUserProfile";
                    knLogger.info(methodName, "Inside unassignUserProfile");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.unassignUserProfile(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_USER_PROFILE_SUBSCRIBERLIST:
                    operation = "getUserProfileSubscriberList";
                    knLogger.info(methodName, "Inside getUserProfileSubscriberList");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getUserProfileSubscriberList((IXDMRequestDTO) requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UPDATE_DEFAULT_USERPROFILE:
                    operation = "updateDefaultProfile";
                    knLogger.info(methodName, "Inside updateDefaultprofile");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.updateDefaultprofile((IXDMRequestDTO) requestDTO);
                    break;
                case KnConstants.OPERATION_ID_CREATE_GROUP_PROFILE:
                    operation = "createGroupProfile";
                    knLogger.info(methodName, "Received create bulk group request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.createGroupProfile(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_CREATE_BULK_GROUP_WITH_PROFILE:
                    operation = "createBulkCorpGroup";
                    knLogger.info(methodName, "Received create bulk group request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.createBulkCorpGroup(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_GROUP_PROFILE_LIST:
                    operation = "getGroupProfileList";
                    knLogger.info(methodName, "Received create bulk group request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getGroupProfileList(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_GROUP_PROFILE_DETAILS:
                    operation = "getGroupProfileDetails";
                    knLogger.info(methodName, "Received create bulk group request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getGroupProfileDetails(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_GROUP_LIST_FOR_GROUP_PROFILE:
                    operation = "getProfileGroupList";
                    knLogger.info(methodName, "Received create bulk group request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getProfileGroupList(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_SEARCH_GROUP_PROFILE:
                    operation = "searchGroupProfile";
                    knLogger.info(methodName, "Received search group profile request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.searchGroupProfile(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_CREATE_CORP_GROUP_WITH_PROFILE:
                    operation = "createCorpGroupWithProfile";
                    knLogger.info(methodName, "Received create corp group with profile request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.createCorpGroupWithProfile(msgObj);
                    break;
                case KnConstants.OPERATION_ID_MODIFY_GROUP_PROFILE:
                    operation = "modifyGroupProfile";
                    knLogger.info(methodName, "Received modify group profile request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.modifyGroupProfile(msgObj);
                    break;
                case KnConstants.OPERATION_ID_DELETE_BULK_GROUP_WITH_PROFILE:
                    operation = "deleteBulkCorpGroup";
                    knLogger.info(methodName, "Received delete bulk group request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.deleteBulkCorpGroup(msgObj);
                    break;
                case KnConstants.OPERATION_ID_DELETE_GROUP_PROFILE:
                    operation = "deleteGroupProfile";
                    knLogger.info(methodName, "Received delete group profile request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.deleteGroupProfile(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_CORP_SHARED_TRUST_MATRIX:
                    knLogger.debug("Identified the case");
                    operation = "getSharedCorpTrustMatrix";
                    knLogger.info(methodName, "Received getSharedCorpTrustMatrix request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getSharedCorpTrustMatrix(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UPDATE_CORP_TRUST_MATRIX:
                    operation = "updateCorpTrustMatrix";
                    knLogger.info(methodName, "Received updateCorpTrustMatrix request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.updateCorpTrustMatrix(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_DELETE_CORP_TRUST_MATRIX:
                    operation = "deleteCorpTrustMatrix";
                    knLogger.info(methodName, "Received deleteCorpTrustMatrix request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.deleteCorpTrustMatrix(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_SEND_TRK_MATERIAL:
                    operation = "sendTrkMaterial";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.sendTrkMaterial(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_ASYNC_OP_STATUS:
                    operation = "getAsyncOpStatus";
                    knLogger.info(methodName, "Received getAsyncOpStatus request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getAsyncOpStatus(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_SUBSCR_CLIENT_SETTINGS:
                    operation = "getSubClientSettings";
                    knLogger.info(methodName, "Received getSubClientSettings request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getSubscrClientSettings(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_SET_SUBSCR_CLIENT_SETTINGS:
                    operation = "setSubscrClientSettings";
                    knLogger.info(methodName, "Received setSubscrClientSettings request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.setSubscrClientSettings(msgObj);
                    break;
                case KnConstants.OPERATION_ID_MODIFY_GROUP_UGW_CONFIG:
                    operation = "modifyGroupsUGWConfig";
                    knLogger.info(methodName, "Received modifyGroupsUGWConfig request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.modifyGroupsUGWConfig(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_GROUPS_UGW_CONFIG:
                    operation = "getGroupsUGWConfig";
                    knLogger.info(methodName, "Received getGroupsUGWConfig request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getGroupsUGWConfig(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_ASSIGN_COMMON_CONTACT_LIST_TO_SUBSCRIBERS:
                    operation = "assignCommonContactList";
                    knLogger.info(methodName, "Received assignCommonContactList request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.assignCommonContactList(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_DEVICEINFO:
                    operation = "getDeviceDetails";
                    knLogger.info(methodName, "Received getDeviceDetails request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getDeviceDetails(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UNASSIGN_COMMON_CONTACT_LIST_TO_SUBSCRIBERS:
                    operation = "unAssignCommonContactList";
                    knLogger.info(methodName, "Received unAssignCommonContactList request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.unAssignCommonContactList(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_DEVICELIST:
                    operation = "getDeviceList";
                    knLogger.info(methodName, "Received getDeviceList request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getDeviceList(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_GROUPSTATS:
                    operation = "getGroupStats";
                    knLogger.info(methodName, "Received getDeviceStats request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getGroupStats(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_SUBSCRIBERS_STATS_OF_CORP:
                    operation = "getSubscriberStats";
                    knLogger.info(methodName, "Received getSubscriberStats request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getSubscriberStats(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_DEVICESTATS:
                    operation = "getDeviceStats";
                    knLogger.info(methodName, "Received getDeviceStats request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getDeviceStats(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_SEND_MAIL:
                    operation = "sendMail";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.sendMail(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_CORPORATE_FS:
                    operation = "getCorporateFS";
                    knLogger.info(methodName, "Received getCorporateFS request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorporateFS(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UPDATE_CORP_ADMIN_FS:
                    operation = "updateCorporateFS";
                    knLogger.info(methodName, "Received updateCorporateFS request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.updateCorporateFS(requestDTO);
                    break;
                case KnConstants.OP_ID_UPDATE_CAT_PER_SET:
                    operation = "setCATAccessPermission";
                    knLogger.info(methodName, "Received setCATAccessPermission request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.setCATAccessPermission(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_MODIFY_CORPGROUP_PROPERTIES:
                    operation = "modifyBulkGroupProperties";
                    knLogger.info(methodName, "Received modifyBulkGroupProperties request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.modifyBulkGroupProperties(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_CLONE_CONTACT_GROUPS_AND_FEATURES:
                    operation = "cloneContactsGroupsAndFeatures";
                    knLogger.info(methodName, "Inside cloneContactsGroupsAndFeatures");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.cloneContactsGroupsAndFeatures(msgObj);
                    break;

                case KnConstants.OPERATION_ID_DELETE_HIERARCHY:
                    operation = "deleteHierarchy";
                    knLogger.info(methodName, "Inside deleteHierarchy");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.deleteHierarchy(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_CREATE_HIRARCHY:
                    operation = "createHierarchy";
                    knLogger.info(methodName, "Received create Hierarchy request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.createHierarchy(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_MODIFY_HIRARCHY:
                    operation = "modifyHierarchy";
                    knLogger.info(methodName, "Received modify Hierarchy request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.modifyHierarchy(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_REGIONS:
                    operation = "getRegions";
                    knLogger.info(methodName, "Received getRegions request");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getRegions(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_ALLOCATE_SUBSCRIBER:
                    operation = "allocateSubs";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.allocateSubs(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UNALLOCATE_SUBSCRIBER:
                    operation = "unAllocateSubs";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.unAllocateSubs(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_PTT_SETTING_TEMPLATE_LIST:
                    operation = "getAllPTTSettingTemplateList";
                    knLogger.info(methodName, "Inside getAllPTTSettingTemplateList");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getPTTSettingDocList(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_PTT_SETTING_DOC:
                    operation = "getPTTSettingDoc";
                    knLogger.info(methodName, "Inside getPTTSettingDoc");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getPTTSettingDoc(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_SET_DEFAULT_PTT_SETTING_DOC:
                    operation = "setDefaultPttSettingDoc";
                    knLogger.info(methodName, "Inside setDefaultPttSettingDoc");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.setDefaultPttSettingDoc(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_ASSIGN_PTT_SETTING_TO_HIERARCHY:
                    operation = "assignPttSettingDocToHierarchy";
                    knLogger.info(methodName, "Inside assignPttSettingDocToHierarchy");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.assignPttSettingToHierarchy(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UNASSIGN_PTT_SETTING_TO_HIERARCHY:
                    operation = "unassignPttSettingDocToHierarchy";
                    knLogger.info(methodName, "Inside unassignPttSettingDocToHierarchy");
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.unassignPttSettingToHierarchy(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_ASSIGN_PTT_SETTING_TO_MDN_LIST:
                    operation = "assignPttSettingDocToMdns";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.assignPttSettingDocToMdns(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UNASSIGN_PTT_SETTING_TO_MDN_LIST:
                    operation = "unassignPttSettingDocToMdns";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.unassignPttSettingDocToMdns(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_MDN_LIST_FOR_PTT_SETTINGID:
                    operation = "getPttSettingDocMdnList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getPttSettingDocMdnList(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_MDN_COUNT_FOR_PTT_SETTING_DOC:
                    operation = "getPttSettingDocMdnList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getMDNCountForPttSettingDocID(requestDTO);
                    break;

                default:
                    knLogger.error(methodName, "Wrong operation Id passed in request - ", operationId);
                    respDto = new KnXDMCorpRespDTO();
                    respDto.setResponseStatus(1); //todo need to add proper internal server error code
                    respDto.setResponseCode("111");
                    respDto.setResponseMessage("Failure...");
                    //facadeIntf.unKnownOperation(msgObj); //todo need to return error response to Web layer
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception in Req handler", e);
            respDto = new KnXDMCorpRespDTO();
            respDto.setResponseStatus(1);
            respDto.setResponseCode(ERROR_CODE_INTERNAL_ERROR);
            respDto.setResponseMessage("..Internal Server Error..");
        }

        boolean isSent = Boolean.FALSE;
        if (operationId == KnConstants.OPERATION_ID_REST_GET_SUBSCR_ACTIVATION_CODE || operationId == KnConstants.OPERATION_ID_REST_GENERATE_OTP
                || operationId == KnConstants.OPERATION_ID_REST_VALIDATE_OTP || operationId == KnConstants.OPERATION_ID_REST_GET_CORP_HIERARCHY
                || operationId == KnConstants.OPERATION_ID_REST_GENERATE_SUBSCR_ACTIVATION_CODE || operationId == KnConstants.OPERATION_ID_REST_GET_USER_DETAILS
                || operationId == KnConstants.OPERATION_ID_REST_SEND_SMS) {
            knLogger.info(methodName, "post Operation operationId-", operationId);
            if (operationId == KnConstants.OPERATION_ID_REST_GET_CORP_HIERARCHY) {
                KnXDMCorpClientActResponseDTO resp = (KnXDMCorpClientActResponseDTO) respDto;
                KnCorpDetailsDTO corpDetails = new KnCorpDetailsDTO();
                Map<String, Object> responseMap = resp.getResponseMap();
                corpDetails.setCorpId((String) responseMap.get(KnConstants.CORP_ID));
                corpDetails.setExtCorpId((String) responseMap.get(KnConstants.EXT_CORP_ID));
                corpDetails.setCorpName((String) responseMap.get(KnConstants.CORPNAME));
                List<KnIdDetails> idDetails = new ArrayList<>();
                KnIdDetails bandetails = new KnIdDetails();
                bandetails.setIdType(KnConstants.BAN_TYPE);
                bandetails.setIdList((List<KnIdValue>) responseMap.get(KnConstants.BAN_DETAILS));
                idDetails.add(bandetails);
                KnIdDetails fandetails = new KnIdDetails();
                fandetails.setIdType(KnConstants.FAN_TYPE);
                fandetails.setIdList((List<KnIdValue>) responseMap.get(KnConstants.FAN_DETAILS));
                idDetails.add(fandetails);
                corpDetails.setIdDetails(idDetails);
                corpDetails.setStatus((String) responseMap.get(KnConstants.STATUS));
                corpDetails.setStatusCode((String) responseMap.get(KnConstants.STATUS_CODE));
                corpDetails.setMessage((String) responseMap.get(KnConstants.MESSAGE));
                resp.setCorpDetailsDTO(corpDetails);
                isSent = respHandler.processCorpBanFanJsonResponse(respDto, msgObj);
            } else {
                isSent = respHandler.processJsonResponse(respDto, msgObj);
            }
        } else {
            isSent = respHandler.processResponse(respDto, msgObj);
        }
        KnAuditHelper auditHelper = audit;
        if (KnConstants.OPERATION_ID_GET_BILLING_NUMBERS == operationId || KnConstants.OPERATION_ID_GET_LICENSE_SUBS == operationId
                || KnConstants.OPERATION_ID_LICENSE_AUTHENTICATE == operationId || KnConstants.OPERATION_ID_MARK_SUBS_FOR_DELETION == operationId
                || KnConstants.OPERATION_ID_PAM_SUBS_CORP_CUSTOM == operationId) {
            auditHelper = pamSubsAudit;
        }
        auditResponse(respDto, auditHelper, new StringBuilder("CID:").append(msgObj.getCorrelationId()).toString(), operation);
        knLogger.info(methodName, "Message sent - ", isSent);
        long responseTime = System.currentTimeMillis();
        latency = responseTime - requestTime;
        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.CORP_PROV_AVG_LATENCY_PEG, (int) latency);
        return isSent;
    }

    private void auditResponse(IXDMResponseDTO respDto, KnAuditHelper auditHelper, String corRelationId, String operation) {
        if (null != respDto && respDto.getResponseStatus() == 0) {
            auditHelper.writeAuditMessage(corRelationId, operation, KnAuditHelper.STATUS.SUCCESS, SUCCESS_MSG);
        } else {
            auditHelper.writeAuditMessage(corRelationId, operation, KnAuditHelper.STATUS.FAILURE, FAILURE_MSG, respDto.getResponseCode());
        }
    }

}
