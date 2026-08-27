/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnProvRequestHandlerImpl.java
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

import com.kodiak.common.commdto.common.KnXDMExtSubscriberDTO;
import com.kodiak.common.commdto.request.*;
import com.kodiak.common.commdto.response.*;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.xdms.xdmintf.IRequestHandlerIntf;
import com.kodiak.xdms.xdmintf.IResponseHandler;
import com.kodiak.xdms.xdmintf.IXDMFacadeIntf;
import com.kodiak.xdms.xdmintf.resources.KnXdmIntfUtil;

import java.util.List;
import java.util.Map;

/**
 * Class which receives the message from the messaging Queue
 * Retrieve the Pay load from the message
 * Based the operation ID process the request Operation
 */
public class KnProvRequestHandlerImpl implements IRequestHandlerIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnProvRequestHandlerImpl.class);

    private IResponseHandler respHandler;
    private KnAuditHelper audit;
    private KnAuditHelper auditTPuser;

    public KnProvRequestHandlerImpl() {
        respHandler = new KnResponseHandlerImpl();
        knLogger.info("KnProvRequestHandlerImpl()", "Initializing Audits");
        audit = KnAuditHelper.getAuditLogger(KnConstants.AUDIT.PROV.value());
        auditTPuser = KnAuditHelper.getAuditLogger(KnConstants.AUDIT.TPUSER.value());
        knLogger.info("KnProvRequestHandlerImpl()", "Audits initialized - ", audit, auditTPuser);
    }

    public boolean processRequest(KnMessage msgObj) {
        /**
         * 1. Get the payload from the msgObj
         * 2. Check if instance of IInputDTO
         * 3. Get the operation type
         * 4. switch to the respective operation (ex: createSubscriber, updateSubscriber etc)
         */
        String methodName = "processRequest(KnMessage)";
        knLogger.info(methodName, "ENTRY: Received Message Object - ", msgObj);
        int operationId = -1;
        boolean synch = true;
        boolean isAuditReq = true;
        //retrieving the message Payload
        Object payLoad = msgObj.getPayLoad();
        IXDMRequestDTO inputDTO = null;
        boolean synch_response = true;


        KnXDMSubsProvInfoDTO createOrUpdateSubscriberDTO = null;    //createSubscriber,updateSubscriber ,updateSubscriberName
        KnXDMActivateInfoDTO activateSubscriberDTO = null;    //activateSubscriber
        KnXDMSubsInfoDTO deleteOrGetSubscriberDTO = null;   //deleteSubscriber ,getSubscriberDetails,getSubscriberConfigDocument,getDefaultSubscriberProfile
        KnXDMSubsStatusInfoDTO changeServiceAuthStatusDTO = null;   //changeServiceAuthStatus
        KnXDMChangeMDNInfoDTO changeMDNDTO = null;    //changeMDN
        KnXDMExtSubsRequestDTO deleteExternalSubDTO = null;  //deleteExternalSubscriber


        if (payLoad instanceof IXDMRequestDTO) {
            knLogger.debug(methodName, "Pay load Object is instance of IXDMRequestDTO");
            inputDTO = (IXDMRequestDTO) payLoad;
            String operationType = inputDTO.getOperationType();
            operationId = Integer.parseInt(operationType);
        } else {
            operationId = 0;
        }
        String operation = null;
        IXDMResponseDTO respDTO = null;
        knLogger.debug(methodName, "operation id - ", operationId);
        IXDMFacadeIntf facadeIntf = KnXdmIntfUtil.getFacadeObj(operationId, inputDTO);
        String activationMDN = null;
        long latency = 0;
        long requestTime = System.currentTimeMillis();
        try {
            switch (operationId) {
                case KnConstants.OP_ID_CREATE_SUBS:
                    operation = "createSubscriber";
                    if (inputDTO instanceof KnXDMSubsProvInfoDTO) {
                        createOrUpdateSubscriberDTO = (KnXDMSubsProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + createOrUpdateSubscriberDTO.getMdn());
                    }

                    respDTO = facadeIntf.createSubscriber(inputDTO);
                    break;

                case KnConstants.OP_ID_UPDATE_SUBS:
                    operation = "updateSubscriber";
                    if (inputDTO instanceof KnXDMSubsProvInfoDTO) {
                        createOrUpdateSubscriberDTO = (KnXDMSubsProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + createOrUpdateSubscriberDTO.getMdn());
                    }

                    respDTO = facadeIntf.updateSubscriber(msgObj);
                    break;

                case KnConstants.OP_ID_DELETE_SUBS:
                    operation = "deleteSubscriber";
                    if (inputDTO instanceof KnXDMSubsInfoDTO) {
                        deleteOrGetSubscriberDTO = (KnXDMSubsInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + deleteOrGetSubscriberDTO.getMdn());
                    }

                    respDTO = facadeIntf.deleteSubscriber(msgObj);
                    break;

                case KnConstants.OP_ID_GET_SUBS_PROFILE:
                    operation = "getSubscriberDetails";
                    if (inputDTO instanceof KnXDMSubsInfoDTO) {
                        deleteOrGetSubscriberDTO = (KnXDMSubsInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + deleteOrGetSubscriberDTO.getMdn());
                    }

                    respDTO = facadeIntf.getSubscriberDetails(inputDTO);
                    break;

                case KnConstants.OP_ID_CHANGE_AUTH_STATUS:
                    operation = "changeServiceAuthStatus";
                    if (inputDTO instanceof KnXDMSubsStatusInfoDTO) {
                        changeServiceAuthStatusDTO = (KnXDMSubsStatusInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + changeServiceAuthStatusDTO.getMdn());
                    }

                    respDTO = facadeIntf.changeServiceAuthStatus(msgObj);
                    break;

                case KnConstants.OP_ID_CHANGE_MDN:
                    operation = "changeMdn";
                    if (inputDTO instanceof KnXDMChangeMDNInfoDTO) {
                        changeMDNDTO = (KnXDMChangeMDNInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - old mdn - " + changeMDNDTO.getOldMDN() + ",new mdn - " + changeMDNDTO.getNewMdn());
                    }

                    respDTO = facadeIntf.changeMdn(msgObj);
                    break;

                case KnConstants.OP_ID_FORCE_SYNC:
                    operation = "forceSync";
                    if (inputDTO instanceof KnXDMSubsInfoDTO) {
                        deleteOrGetSubscriberDTO = (KnXDMSubsInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + deleteOrGetSubscriberDTO.getMdn());
                    }
                    respDTO = facadeIntf.forceSync(inputDTO);
                    break;

                case KnConstants.OP_ID_ACTIVATE_SUBS:
                    operation = "activateSubscriber";
                    if (inputDTO instanceof KnXDMActivateInfoDTO) {
                        activateSubscriberDTO = (KnXDMActivateInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received with activation key - " + activateSubscriberDTO.getActivationKey());
                    }

                    respDTO = facadeIntf.activateSubscriber(msgObj);
                    KnXDMActivateRespDTO actRespObj = (KnXDMActivateRespDTO) respDTO;
                    activationMDN = actRespObj.getMdn();
                    break;

                case KnConstants.OP_ID_GET_SUBS_CONFIG:
                    operation = "getSubscriberConfigDocument";
                    if (inputDTO instanceof KnXDMSubsInfoDTO) {
                        deleteOrGetSubscriberDTO = (KnXDMSubsInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + deleteOrGetSubscriberDTO.getMdn());
                    }

                    respDTO = facadeIntf.getSubscriberConfigDocument(msgObj);
                    break;

                case KnConstants.OP_ID_UPDATE_SUBS_NAME:
                    operation = "updateSubscriberName";
                    if (inputDTO instanceof KnXDMSubsProvInfoDTO) {
                        createOrUpdateSubscriberDTO = (KnXDMSubsProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + createOrUpdateSubscriberDTO.getMdn());
                    }

                    respDTO = facadeIntf.updateSubscriberName(msgObj);
                    break;

                case KnConstants.OP_ID_GET_DEFAULT_SUBS_PROFILE:
                    operation = "getDefaultSubscriberProfile";
                    if (inputDTO instanceof KnXDMSubsInfoDTO) {
                        deleteOrGetSubscriberDTO = (KnXDMSubsInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + deleteOrGetSubscriberDTO.getMdn());
                    }

                    respDTO = facadeIntf.getDefaultSubscriberProfile(inputDTO);
                    break;


                case KnConstants.OP_ID_DEVICE_ACTIVATION:
                    operation = "deviceActivation";
                    if (inputDTO instanceof KnXDMActivateInfoDTO) {
                        activateSubscriberDTO = (KnXDMActivateInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + activateSubscriberDTO.getActivationKey());
                    }
                    respDTO = facadeIntf.deviceActivation(inputDTO);
                    break;

                case KnConstants.OP_ID_USER_LOGIN:
                    operation = "userLogin";
                    if (inputDTO instanceof KnXDMActivateInfoDTO) {
                        activateSubscriberDTO = (KnXDMActivateInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + activateSubscriberDTO.getMdn());
                    }
                    respDTO = facadeIntf.userLogin(msgObj);
                    break;


                case KnConstants.OP_ID_GET_EMERGENCY_DETAILS:
                    operation = "subsEmergencyDetails";
                    if (inputDTO instanceof KnXDMActivateInfoDTO) {
                        activateSubscriberDTO = (KnXDMActivateInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + activateSubscriberDTO.getMdn());
                    }
                    respDTO = facadeIntf.getSubsEmergencyDetails(inputDTO);
                    break;

                case KnConstants.OPERATION_ID_HEALTH_PING:
                    facadeIntf.isAlive(msgObj);
                    return true;

                case KnConstants.OP_ID_CUSTOM_PROV:
                    operation = "CUSTOM_PROV";
                    String version = null;
                    if (inputDTO instanceof KnXDMSubsProvInfoDTO) {
                        createOrUpdateSubscriberDTO = (KnXDMSubsProvInfoDTO) inputDTO;
                        version = createOrUpdateSubscriberDTO.getVersion();
                        createOrUpdateSubscriberDTO.setCorrelationId(msgObj.getCorrelationId());
                        createOrUpdateSubscriberDTO.setSrcRoutingKey(msgObj.getSrcRoutingKey());
                        createOrUpdateSubscriberDTO.setSrcQueueName(msgObj.getSrcQueueName());
                    }
                    knLogger.debug(methodName, "version -- ", version);
                    respDTO = facadeIntf.customProvOp(inputDTO);
                    if (version != null && version.matches(com.kodiak.xdms.server.common.resources.KnConstants.VERSION)) {
                        synch = false;
                    } else {
                        synch = true;
                    }
                    // This check is for PAM
                    if (respDTO instanceof KnXDMPAMRespDTO) {
                        isAuditReq = false;
                        synch_response = false;
                        synch = false;
                    }
                    if (respDTO instanceof KnXDMRespDTO) {
                        synch_response = false;
                        Map<String, Object> getCustomParamMap = ((KnXDMRespDTO) respDTO).getCustomParamMap();
                        if (getCustomParamMap == null) {
                            knLogger.debug(methodName, "getCustomParamMap == null");
                        } else {
                            knLogger.debug(methodName, "getCustomParamMap != null");
                            if (((KnXDMRespDTO) respDTO).getCustomParamMap().containsKey(KnConstants.MDN)) {
                                if (KnConstants.UPDATEBAN_MDN.equals((((KnXDMRespDTO) respDTO).getCustomParamMap()).get(KnConstants.MDN))) {
                                    isAuditReq = false;
                                }
                            }
                        }
                    }
                    break;

                case KnConstants.OP_ID_UPDATE_CLIENT_FS1:
                    respDTO = facadeIntf.updateClientFS1(msgObj);
                    break;

                case KnConstants.OP_ID_CREATE_PAM_ACCOUNT:
                    synch = false;
                    respDTO = facadeIntf.createLicensePack(msgObj);
                    break;

                case KnConstants.OP_ID_GET_LICENSE_PK_PROFILE:
                    synch = true;
                    respDTO = facadeIntf.getLicensePackProfile(msgObj);
                    break;

                case KnConstants.OP_ID_UPGRADE_PAM_ACCOUNT:
                    synch = false;
                    respDTO = facadeIntf.upgradeLicensePack(msgObj);
                    break;
                case KnConstants.OP_ID_UPGRADE_OR_DOWNGRADE_LICENSE_PK:
                    synch = false;
                    respDTO = facadeIntf.upgradeOrDowngradLicensePack(msgObj);
                    break;
                case KnConstants.OP_ID_DELETE_PAM_ACCOUNT:
                    synch = false;
                    respDTO = facadeIntf.deleteLicensePack(msgObj);
                    break;

                case KnConstants.OP_ID_CHANGE_BILLING_NUM_PAM_ACCOUNT:
                    synch = false;
                    respDTO = facadeIntf.changeBillingNumber(msgObj);
                    break;

                case KnConstants.OP_ID_CHANGE_AUTH_STATUS_PAM_ACC:
                    synch = false;
                    respDTO = facadeIntf.changePAMServiceAuthStatus(msgObj);
                    break;

                case KnConstants.OP_ID_GET_PAM_ACCOUNT_DETAILS:
                    operation = "GET_PAM_ACCOUNT_DETAILS";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - ");
                    respDTO = facadeIntf.getPAMAccountMDNsDetails(msgObj);
                    break;

                case KnConstants.OP_ID_UPDATE_PAM_ACCOUNT_PROFILE:
                    synch = false;
                    respDTO = facadeIntf.updateLicensePack(msgObj);
                    break;

                case KnConstants.OP_ID_DOWNGRADE_RATE_PLAN_PAM_ACCOUNT:
                    synch = false;
                    respDTO = facadeIntf.downgradeLicensePack(msgObj);
                    break;
                case KnConstants.OP_ID_DELETE_EXT_SUBS:
                    operation = "deleteExternalSubscriber";
                    if (inputDTO instanceof KnXDMExtSubsRequestDTO) {
                        String mdn = null;
                        deleteExternalSubDTO = (KnXDMExtSubsRequestDTO) inputDTO;
                        List<KnXDMExtSubscriberDTO> xdmExtSubscriberDTOList = deleteExternalSubDTO.getExtSubs();
                        for (KnXDMExtSubscriberDTO xdmExtSubscriberDTO : xdmExtSubscriberDTOList) {
                            mdn = xdmExtSubscriberDTO.getMdn();
                        }
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + mdn);
                    }

                    respDTO = facadeIntf.deleteExternalSubscriber(msgObj);
                    break;
                case KnConstants.OP_ID_UPDATE_AUTO_PAIRING:
                    operation = "updateAutoPairing";
                    if (inputDTO instanceof KnXDMSubsProvInfoDTO) {
                        createOrUpdateSubscriberDTO = (KnXDMSubsProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for Corp- " + createOrUpdateSubscriberDTO.getExtCorpId()
                                + ", Auto pair flag-" + createOrUpdateSubscriberDTO.getPairingInd());
                    }

                    respDTO = facadeIntf.updateAutoPairing(inputDTO);
                    break;
                case KnConstants.OP_ID_CREATE_TP_USER:
                    operation = "createTPUser";
                    this.auditTPuser.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDTO = facadeIntf.createTPUser(inputDTO);
                    this.auditTPResponse(respDTO, msgObj, operation);
                    isAuditReq = false;
                    break;
                case KnConstants.OP_ID_UPDATE_TP_USER:
                    operation = "updateTPUser";
                    this.auditTPuser.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDTO = facadeIntf.updateTPUser(inputDTO);
                    this.auditTPResponse(respDTO, msgObj, operation);
                    isAuditReq = false;
                    break;
                case KnConstants.OP_ID_DELETE_TP_USER:
                    operation = "deleteTPUser";
                    this.auditTPuser.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDTO = facadeIntf.deleteTPUser(msgObj);
                    this.auditTPResponse(respDTO, msgObj, operation);
                    isAuditReq = false;
                    break;
                case KnConstants.OP_ID_GEN_ACTIVATION_CODE:
                    operation = "generateTPActivationCode";
                    this.auditTPuser.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDTO = facadeIntf.generateTPActivationCode(inputDTO);
                    this.auditTPResponse(respDTO, msgObj, operation);
                    isAuditReq = false;
                    break;
                case KnConstants.OP_ID_GETSYSCONFIG:
                    operation = "getSysConfig";
                    if (inputDTO instanceof KnXDMSubsProvInfoDTO) {
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for get sys config ");
                        createOrUpdateSubscriberDTO = (KnXDMSubsProvInfoDTO) inputDTO;
                    }
                    respDTO = facadeIntf.getSysConfig(createOrUpdateSubscriberDTO);
                    break;
                case KnConstants.OPERATION_ID_GET_SUBSCRIBERS_GRPLIST:
                    operation = "getSubscriberCorpGroupList";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDTO = facadeIntf.getSubscriberCorpGroupList(inputDTO);
                    break;
                case KnConstants.OPERATION_ID_SEARCH_CORP_ADD_BOOK:
                    operation = "searchCorpAddressBook";
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    respDTO = facadeIntf.searchCorpAddressBook(inputDTO);
                    break;
                case KnConstants.OP_ID_UPDATE_MCSIDS:
                    operation = "updateMCSIDS";
                    if (inputDTO instanceof KnXDMSubsProvInfoDTO) {
                        createOrUpdateSubscriberDTO = (KnXDMSubsProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + createOrUpdateSubscriberDTO.getMdn());
                    }
                    respDTO = facadeIntf.updateMCSIds(inputDTO);
                    break;
                case KnConstants.OP_ID_UPDATE_USERID:
                    operation = "updateUserId";
                    if (inputDTO instanceof KnXDMSubsProvInfoDTO) {
                        createOrUpdateSubscriberDTO = (KnXDMSubsProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + createOrUpdateSubscriberDTO.getMdn());
                    }
                    respDTO = facadeIntf.updateUserId(inputDTO);
                    break;
                case KnConstants.OP_ID_UPDATE_LICENSE_PK_MCSIDS:
                    operation = "updateLicensePackMCSIDS";
                    if (inputDTO instanceof KnXDMSubsProvInfoDTO) {
                        createOrUpdateSubscriberDTO = (KnXDMSubsProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + createOrUpdateSubscriberDTO.getMdn());
                    }
                    respDTO = facadeIntf.updateLicensePackSubsMCSIds(inputDTO);
                    break;
                case KnConstants.OP_ID_UPDATE_LICENSE_PK_USERID:
                    operation = "updateLicensePackUserId";
                    if (inputDTO instanceof KnXDMSubsProvInfoDTO) {
                        createOrUpdateSubscriberDTO = (KnXDMSubsProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + createOrUpdateSubscriberDTO.getMdn());
                    }
                    respDTO = facadeIntf.updateLicensePackSubsUserId(inputDTO);
                    break;

                case KnConstants.OP_ID_CREATE_DEVICE:
                    operation = "createDevice";
                    if (inputDTO instanceof KnXDMDeviceProvInfoDTO) {
                        KnXDMDeviceProvInfoDTO deviceProvInfoDTO = (KnXDMDeviceProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + deviceProvInfoDTO.getDeviceId());
                    }
                    respDTO = facadeIntf.createDevice(inputDTO);
                    break;

                case KnConstants.OP_ID_GETDEVICE:
                    operation = "getDeviceInfo";
                    if (inputDTO instanceof KnXDMDeviceProvInfoDTO) {
                        KnXDMDeviceProvInfoDTO deviceProvInfoDTO = (KnXDMDeviceProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + deviceProvInfoDTO.getDeviceId());
                    }
                    respDTO = facadeIntf.getDeviceInfo(inputDTO);
                    break;
                case KnConstants.OP_ID_DELETEDEVICE:
                    operation = "deleteDeviceInfo";
                    if (inputDTO instanceof KnXDMDeviceProvInfoDTO) {
                        KnXDMDeviceProvInfoDTO deviceProvInfoDTO = (KnXDMDeviceProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + deviceProvInfoDTO.getDeviceId());
                    }
                    respDTO = facadeIntf.deleteDeviceInfo(inputDTO);
                    break;
                case KnConstants.OP_ID_MODIFY_DEVICE:
                    operation = "modifyDevice";
                    if (inputDTO instanceof KnXDMDeviceProvInfoDTO) {
                        KnXDMDeviceProvInfoDTO deviceProvInfoDTO = (KnXDMDeviceProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + deviceProvInfoDTO.getDeviceId());
                    }
                    respDTO = facadeIntf.modifyDevice(inputDTO);
                    break;
                case KnConstants.OP_ID_CREATE_CORP_ACCOUNT:
                    operation = "createCorpAccount";
                    if (inputDTO instanceof KnXDMCorpProfileInfoDTO) {
                        KnXDMCorpProfileInfoDTO corpAccountDTO = (KnXDMCorpProfileInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + corpAccountDTO.getAccountId());
                    }
                    respDTO = facadeIntf.createCorpAccount(inputDTO);
                    break;
                case KnConstants.OP_ID_DELETE_CORP_ACCOUNT:
                    operation = "deleteCorpAccount";
                    if (inputDTO instanceof KnXDMCorpInfoDTO) {
                        KnXDMCorpInfoDTO corpAccountDTO = (KnXDMCorpInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + corpAccountDTO.getAccountId());
                    }
                    respDTO = facadeIntf.deleteCorpAccount(inputDTO);
                    break;
                case KnConstants.OP_ID_GET_CORPORATE_ACC_DETAILS:
                    operation = "getCorporateAccountDetails";
                    if (inputDTO instanceof KnXDMDeviceProvInfoDTO) {
                        KnXDMDeviceProvInfoDTO deviceProvInfoDTO = (KnXDMDeviceProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received for - " + deviceProvInfoDTO.getAccountId());
                    }
                    respDTO = facadeIntf.getCorporateAccountDetails(inputDTO);
                    break;
                case KnConstants.OP_ID_GET_CORPORATE_ACCOUNT_LIST:
                    operation = "getCorporateAccountsList";
                    if (inputDTO instanceof KnXDMCorpAccountsListDTO) {
                        KnXDMDeviceProvInfoDTO corpAccListDTO = (KnXDMDeviceProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received");
                    }
                    respDTO = facadeIntf.getCorporateAccountsList(inputDTO);
                    break;
                case KnConstants.OP_ID_UPDATE_CORP_ACCOUNT:
                    operation = "updateCorporateAccount";
                    if (inputDTO instanceof KnXDMCorpAccountsListDTO) {
                        KnXDMCorpAccountsListDTO corpAccListDTO = (KnXDMCorpAccountsListDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received" + corpAccListDTO.getXdmCorpAccList());
                    }
                    respDTO = facadeIntf.updateCorpAccount(inputDTO);
                    break;
                case KnConstants.OP_ID_GET_EXTGW_PROFILE_LIST:
                    operation = "getExtGWProfileList";
                    if (inputDTO instanceof KnXDMDeviceProvInfoDTO) {
                        KnXDMDeviceProvInfoDTO extGWProfileDTO = (KnXDMDeviceProvInfoDTO) inputDTO;
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.REQUEST, "Request received" + extGWProfileDTO.getTxnId());
                    }
                    respDTO = facadeIntf.getExtGWProfileList(inputDTO);
                    break;
                default:
                    respDTO = facadeIntf.unKnownOperation(inputDTO);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Exception in Req handler", e);
            respDTO = facadeIntf.unKnownOperation(inputDTO);
        }
        boolean status = true;
        if (synch) {
            status = respHandler.processResponse(respDTO, msgObj, synch_response);
            knLogger.info(methodName, "Message Sent Status - ", status);
        } else {
            status = respHandler.deleteAppIntfMsg(msgObj);
            knLogger.info(methodName, "Message deleted from App interface table  status - ", status);
        }

        if (respDTO != null) {
            if (isAuditReq) {
                if (respDTO.getResponseStatus() == 0) {
                    if (null != operation && operation.equals("activateSubscriber")) {
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.SUCCESS, "Operation Successful for - ", activationMDN);
                    } else {
                        audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.SUCCESS, "Operation Successful");
                    }
                } else {
                    audit.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.FAILURE, "Operation failed ", respDTO.getResponseCode());
                }
            }
        }
        long responseTime = System.currentTimeMillis();
        latency = responseTime - requestTime;
        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.SOAP_PROV_AVG_LATENCY_PEG, (int) latency);
        return status;
    }

    private void auditTPResponse(IXDMResponseDTO respDTO, KnMessage msgObj, String operation) {
        if (respDTO.getResponseStatus() == 0) {
            auditTPuser.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.SUCCESS, "Operation Successful");
        } else {
            auditTPuser.writeAuditMessage("CID:" + msgObj.getCorrelationId(), operation, KnAuditHelper.STATUS.FAILURE, "Operation failed ", respDTO.getResponseCode());
        }
    }
}