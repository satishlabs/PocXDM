/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.xdmintf.impl;

import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.request.KnXDMSubsInfoDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnXDMRespDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.xdmintf.IRequestHandlerIntf;
import com.kodiak.xdms.xdmintf.IResponseHandler;
import com.kodiak.xdms.xdmintf.IXDMFacadeIntf;

import static com.kodiak.xdms.server.common.resources.KnConstants.FALSE;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMdataIntfRequestHandlerImpl.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Sept 17, 2016                8.1.2
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnXDMdataIntfRequestHandlerImpl implements IRequestHandlerIntf {


    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMdataIntfRequestHandlerImpl.class);

    private static final String SUCCESS_MSG = "Operation is successful";
    private static final String FAILURE_MSG = "Operation failed with error";
    private static final String FAILURE_MSG_INVALID_OPERATION = "Unsupported Operation";
    private static final String XDMDataIntfAudit = "4019";
    private static final int FAILURE = 1;

    private IXDMFacadeIntf facadeIntf;
    private IResponseHandler respHandler;
    private KnAuditHelper audit;


    public KnXDMdataIntfRequestHandlerImpl() {

        final String methodName = "KnXDMdataIntfRequestHandlerImpl()";
        facadeIntf = new KnXDMFacadeImpl();
        respHandler = new KnResponseHandlerImpl();
        knLogger.info(methodName, "Initializing XDMDataIntf Audit");
        audit = KnAuditHelper.getAuditLogger(XDMDataIntfAudit);
        knLogger.info(methodName, "XDMDataIntf Audit initialized - ", audit);
    }

    @Override
    public boolean processRequest(KnMessage msgObj) {

        /**
         * 1. Get the payload from the msgObj
         * 2. Check if the payload is instance of IXDMRequestDTO
         * 3. Get the operation type
         * 4. Switch to the respective operation (ex: getGroupDetails, getSubscruberGroupList, getSubscriberDetails, getPubGroupDetails etc)
         */
        final String methodName = "processRequest(KnMessage)";
        knLogger.entry(methodName, msgObj);
        int operationId = -1;

        Object payLoad = msgObj.getPayLoad();
        IXDMRequestDTO requestDTO;
        if (payLoad instanceof IXDMRequestDTO) {
            requestDTO = (IXDMRequestDTO) payLoad;
            String operationType = requestDTO.getOperationType();
            operationId = Integer.parseInt(operationType);
        } else {
            knLogger.error(methodName, "Unexpected Class type - ", payLoad.getClass());
            knLogger.error(methodName, "Can't proceed further. Dropping the request and returning false");
            return FALSE;
        }
        knLogger.debug(methodName, "RequestDTO - ", requestDTO);
        String operation = null;
        IXDMResponseDTO respDto = null;
        KnXDMSubsInfoDTO getSubscriberDTO = null;   //getSubscriberDetails
        knLogger.debug(methodName, "operationId -",operationId);
        try {
            switch (operationId) {

                case KnConstants.OPERATION_ID_GET_GROUPDETAILS:
                    operation = "getCorpGroupDetails";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpGroupDetails(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_SUBSCRIBERS_GRPLIST:
                    operation = "getSubscriberGroupList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpSubscriberGroupList(requestDTO);
                    break;

                case KnConstants.OP_ID_GET_SUBS_PROFILE:
                    operation = "getSubscriberDetails";
                    if (requestDTO instanceof KnXDMSubsInfoDTO) {
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    }
                    respDto = facadeIntf.getSubscriberDetails(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_GROUP_DETAILS:
                    operation = "getPubGroupDetails";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getPubGroupDetails(msgObj);
                    break;

                case KnConstants.OPERATION_ID_GET_CORP_EXTERNAL_CONTACT_DETAILS:
                    operation = "getCorpExtContactDetails";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpExtContactDetails(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_SUBS_CONTACTLIST:
                    operation = "getSubsContactList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpSubscContactList(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_SOAP_GET_GROUP_LIST:
                    operation = "getGroupList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getPubGroupList(msgObj);
                    break;

                case KnConstants.OPERATION_ID_GET_CORPORATE_PROFILE:
                    operation = "getCorporateProfile";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorporateProfile(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_LI_TARGET_INFO:
                    operation = "getLITargetInfo";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getLITargetInfo(requestDTO);
                    break;

                case KnConstants.OPERATION_ID_GET_CONTACTS_LIST_DETAILS:
                    operation = "getPubContactList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getPubContactList(msgObj);
                    break;

                case KnConstants.OPERATION_ID_GET_TARGET_USER_PERMISSIONS:
                    operation = "getMcpttMappingDetails";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getTargetPermissions(msgObj);
                    break;
                case KnConstants.OPERATION_ID_GET_CORPPROFILE_BY_ENTITIES:
                    operation = "getCorporateProfileByEntities";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getCorpProfileByEntities(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_POC_CONFIG:
                    operation = "getPoCConfig";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getPoCConfig(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_MOBILE_SYNC_LOC_SUPERVISOR:
                    operation = "getMobileSyncLocSupervisors";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getMobileSyncLocSupervisors(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_PROFILEID_MDN_MAP_FOR_MCPTTID:
                    operation = "getMdnProfileIdsForMcPttIds";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getMdnProfileIdsForMcPttIds(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_SUBS_GROUP_MEMBERSHIP_DETAILS:
                    operation = "getSubsGroupMembershipDetails";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getSubsGroupMembershipDetails(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_USERPROFILEIDS_BY_PROFILEMDNS:
                    operation = "getUserprofileidsByProfileMdns";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getUserprofileidsByProfileMdns(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_SUBSCR_CLIENT_SETTINGS:
                    operation = "getSubClientSettings";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getSubscrClientSettings(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_MDN_AUTHORIZATION_FOR_GROUP:
                    operation = "getMdnAuthorizationForGroupId";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getMdnAuthorizationForGroupId(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_GROUPS_DETAILS_WITHOUT_MEMBERS:
                    operation = "getGroupsDetailsWithoutMembers";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getGroupsDetailsWithoutMembers(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_CREATE_PTT_SETTING_TEMPLATE:
                    operation = "createPTTSettingTemplateDoc";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.createPTTSettingDoc(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_PTT_SETTING_TEMPLATE_LIST:
                    operation = "getPTTSettingDocList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getPTTSettingDocList(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_PTT_SETTING_DOC:
                    operation = "getPTTSettingDoc";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getPTTSettingDoc(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_SET_DEFAULT_PTT_SETTING_DOC:
                    operation = "setDefaultPttSettingDoc";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.setDefaultPttSettingDoc(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_DELETE_PTT_SETTING_TEMPLATE:
                    operation = "deletePTTSettingDoc";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.deletePTTSettingDoc(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_MDN_LIST_FOR_PTT_SETTINGID:
                    operation = "getMDNCountForPttSettingDocID";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.getMDNCountForPttSettingDocID(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_ASSIGN_PTT_SETTING_TO_CORP:
                    operation = "assignPttSettingToCorp";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.assignPttSettingToCorp(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_UNASSIGN_PTT_SETTING_TO_CORP:
                    operation = "unassignPttSettingToCorp";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.unassignPttSettingToCorp(requestDTO);
                    break;
                case KnConstants.OPERATION_ID_MODIFY_PTT_SETTING_TEMPLATE:
                    operation = "modifyPTTSettingTemplate";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDto = facadeIntf.modifyPTTSettingTemplate(requestDTO);
                    respDto = facadeIntf.modifyPTTSettingTemplate(requestDTO);
                    break;
                default:
                    knLogger.error(methodName, "Wrong operation Id passed in request - ", operationId);
                    respDto = new KnXDMRespDTO();
                    respDto.setResponseStatus(FAILURE);
                    respDto.setResponseCode("11111");
                    respDto.setResponseMessage(FAILURE_MSG_INVALID_OPERATION + "-[" + operationId + "]");
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception in Req handler", e);
            respDto = new KnXDMRespDTO();
            respDto.setResponseStatus(FAILURE);
            respDto.setResponseCode("11111");
            respDto.setResponseMessage(FAILURE_MSG_INVALID_OPERATION + "-[" + operationId + "]");
        }
        boolean isSent = respHandler.processResponse(respDto, msgObj);
        auditResponse(respDto, audit, new StringBuilder("CID:").append(msgObj.getCorrelationId()).toString(), operation);
        knLogger.info(methodName, "Message sent - ", isSent);
        return isSent;
    }

    private void auditResponse(IXDMResponseDTO respDto, KnAuditHelper auditHelper, String corRelationId, String operation) {
        if (respDto.getResponseStatus() == 0) {
            auditHelper.writeAuditMessage(corRelationId, operation, KnAuditHelper.STATUS.SUCCESS, SUCCESS_MSG);
        } else {
            auditHelper.writeAuditMessage(corRelationId, operation, KnAuditHelper.STATUS.FAILURE, FAILURE_MSG, respDto.getResponseCode());
        }
    }
}