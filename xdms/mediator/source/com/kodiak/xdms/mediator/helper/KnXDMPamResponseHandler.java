/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.helper;

import com.kodiak.common.commdto.common.KnBulkOpInfoDTO;
import com.kodiak.common.commdto.request.KnXDMCorpInfoRequestDTO;
import com.kodiak.common.commdto.request.KnXDMPAMAccInfoDTO;
import com.kodiak.common.commdto.request.KnXDMPAMSubsProfInfoDTO;
import com.kodiak.common.commdto.response.KnXDMCorpRespDTO;
import com.kodiak.common.commdto.response.KnXDMPAMRespDTO;
import com.kodiak.common.commdto.response.KnXDMRespDTO;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants.RESPONSE_STATUS;
import com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.common.resources.KnThreadExecutors;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.pocxlaclient.KnPoCXLAEventDTO;
import com.kodiak.utilities.pocxlaclient.KnPoCXLATableDetailsDTO;
import com.kodiak.utilities.pocxlaclient.event.KnTableUpdate;
import com.kodiak.utilities.processinvoker.KnProcessInvokerException;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.utilities.springcontainer.KnSpringContextProvider;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.mediator.KnMediatorConstants;
import com.kodiak.xdms.mediator.resources.jobs.KnBulkResponse;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.dto.common.KnBulkOrderInfoDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPPAMAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCorpProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPDeletePAMAccountDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPPAMAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPUpdatePAMAccountDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

// This is to register for pocXLA client..  no need to create object for this.. object will be created by spring FW.
@Component
public class KnXDMPamResponseHandler implements ApplicationListener<KnTableUpdate> {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMPamResponseHandler.class);

    //private static final KnXDMPamResponseHandler instance =  new KnXDMPamResponseHandler();
    private KnAuditHelper auditPam;
    private KnAuditHelper auditProv;
    IProvClientIntf provClientIntf;
    private KnXDMProvMediator provMediator = null;
    private KnXDMPamHelper xdmPamHelper = null;
    private KnXDMCommonMediator commonMediator = null;
    private ExecutorService service;
    private final String TABLE_XDM_BULK_ORDER_INFO = "XDM_BULK_ORDER_INFO";
    private KnXDMCorpMediator corpMediator = null;
    private final String ACTIVE_RELEASE_DIR = System.getProperty("activeRelDir");
    private final String fileName = ACTIVE_RELEASE_DIR + File.separator + "appconf.properties";
    private final String PAM_RESPONDER_BULK_EXP_TIME = "PAM_RESPONDER_BULK_EXP_TIME";
    private Long pamRespBulExp = 60L;

    private KnXDMPamResponseHandler() {
    	final String methodName = "Constructor";
        knLogger.entry(methodName);
        auditPam = KnAuditHelper.getAuditLogger(KnMediatorConstants.AUDIT_LOGGER_CONSTANT);
        auditProv = KnAuditHelper.getAuditLogger(KnMediatorConstants.AUDIT_PROVLOGGER_CONSTANT);
        provClientIntf = KnProvClientImpl.getInstance();
        provMediator = KnXDMProvMediator.getInstance();
        xdmPamHelper = KnXDMPamHelper.getInstance();
        commonMediator = KnXDMCommonMediator.getInstance();
        service = KnThreadExecutors.newDynamicThreadPool(3, 3, 0, "PAMResponder");
        this.corpMediator = KnXDMCorpMediator.getInstance();
        Object value = KnGeneralUtil.getPropertyValue(fileName, PAM_RESPONDER_BULK_EXP_TIME);
        if (value != null) {
        	pamRespBulExp = Long.valueOf(value.toString());
    }
    }

    public static KnXDMPamResponseHandler getInstance() {
        // Since we declared this class has a component, this class oblect will be created by spring FW by initializing time. below is the way to access this object.
        KnXDMPamResponseHandler pamResponseHandler = (KnXDMPamResponseHandler) KnSpringContextProvider.getApplicationContext().getBean("knXDMPamResponseHandler");
        knLogger.debug("getInstance()", "returning pam responder - ", pamResponseHandler);
        return pamResponseHandler;
    }

    public void sendPAMNotifications() {
        final String methodName = "sendPAMNotifications()";
        knLogger.entry(methodName, "pamRespBulExp in mins-", pamRespBulExp);

        long currentTimer = Calendar.getInstance().getTimeInMillis();
        Long pamDelCanTimer = pamRespBulExp * 60 * 1000;
        long expiryTimer = currentTimer - pamDelCanTimer;

        List<KnBulkOrderInfoDTO> bulkOrderInfoDTOs = this.commonMediator.getCompletedBulkOrdersInfo(expiryTimer);
            knLogger.debug(methodName, "bulkOrderInfoDTOs : ", bulkOrderInfoDTOs);
            for (KnBulkOrderInfoDTO bulkOrderInfoDTO : bulkOrderInfoDTOs) {
                knLogger.debug(methodName, "Submitting to Exceuter service");
                this.service.submit(new KnBulkResponse(bulkOrderInfoDTO));
        	knLogger.info(methodName, "Submitted for bulkOrderId-", bulkOrderInfoDTO.getBulkOrderId());
            }

        knLogger.exit(methodName);
    }

    public void onApplicationEvent(KnTableUpdate updateEvent) {
        final String methodName = "onApplicationEvent(KnTableUpdate)";
        knLogger.entry(methodName, updateEvent);

        if (KnStatusManagerClient.getCurrentState() == KnStatusMgrConstants.CARD_STATES.ACTIVE) {
            knLogger.info(methodName, "Card status is ACTIVE.. proceessing PAM notify response");

            KnPoCXLATableDetailsDTO poCXLATableDetailsDTO = updateEvent.getKnPoCXLATableDetailsDTO();
            knLogger.debug(methodName, poCXLATableDetailsDTO);

            if (TABLE_XDM_BULK_ORDER_INFO.equalsIgnoreCase(poCXLATableDetailsDTO.getTableName())) {
                boolean isFailed = poCXLATableDetailsDTO.isEncodingFailed();
                knLogger.debug(methodName, "isFailed" + isFailed);
                KnBulkOrderInfoDTO bulkOrderInfoDTO;
                if (isFailed) {
                    KnPoCXLAEventDTO poCXLAEventDTO = poCXLATableDetailsDTO.getColumnDetailsMap().get(KnMediatorConstants.XDM_BULK_ORDER_INFO.BULKORDER_ID.value());
                    int bulkOrderID = (int) poCXLAEventDTO.getColumnValue();
                    knLogger.debug(methodName, "getting value from DB for bulkOrder ID : ", bulkOrderID);
                    bulkOrderInfoDTO = commonMediator.getCompletedBulkOrderInfo(bulkOrderID);
                    knLogger.debug(methodName, "Submitting to Exceuter service");
                    this.service.submit(new KnBulkResponse(bulkOrderInfoDTO));
                    knLogger.info(methodName, "Submitted for bulkOrderId-", bulkOrderInfoDTO.getBulkOrderId());

                } else {
                    bulkOrderInfoDTO = this.populateBulkOrderInfoDTO(poCXLATableDetailsDTO);
                    int status = bulkOrderInfoDTO.getStatus();
                    knLogger.debug(methodName, "Bulk order statsu - " + status);
                    if (status == 2 || status == 3) {
                        knLogger.debug(methodName, "Submitting to Exceuter service");
                        this.service.submit(new KnBulkResponse(bulkOrderInfoDTO));
                        knLogger.info(methodName, "Submitted!!!!!!! ");
                    }
                }
            }

        }
        knLogger.exit(methodName);
    }

    public void sendXDMPAMResponse(KnBulkOrderInfoDTO bulkOrderInfoDTO) {
        final String methodName = "sendXDMPAMResponse(KnBulkOrderInfoDTO)";
        knLogger.entry(methodName, bulkOrderInfoDTO);

        ObjectInputStream objectIn;
        KnMessage message = null;
        KnXDMPAMAccInfoDTO pamAccInfoDTO = null;
        KnXDMPAMRespDTO responseDTO = null;
        KnXDMRespDTO respDTO = null;
        int action = -1;
        KnPersisterTxn persisterTxn = null;
        List<Integer> bulkOrders = new ArrayList<Integer>();

        try {

            byte[] requestBuff = (byte[]) bulkOrderInfoDTO.getBulkOrderReqObj();
            objectIn = new ObjectInputStream(new ByteArrayInputStream(requestBuff));
            message = (KnMessage) objectIn.readObject();
            if (message.getPayLoad() instanceof KnXDMPAMAccInfoDTO) {
                pamAccInfoDTO = (KnXDMPAMAccInfoDTO) message.getPayLoad();
                knLogger.debug(methodName, "received DTO for PAM Account - ", pamAccInfoDTO);
                String version =pamAccInfoDTO.getVersion();
        	    knLogger.error( methodName,"web card version ", version);
        	    if(version == null) message.setUpgrade(true);
                Object respObj = bulkOrderInfoDTO.getBulkOrderRespObj();

                byte[] respBuff = (byte[]) respObj;
                ObjectInputStream objectOut = new ObjectInputStream(new ByteArrayInputStream(respBuff));
                Object respObj1 = objectOut.readObject();

                knLogger.debug(methodName, "respObj- ", respObj);
                knLogger.debug(methodName, "respObj1- ", respObj1);
                if (respObj == null) {
                    throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, "Bulk response is null");
                }

                action = Integer.parseInt(pamAccInfoDTO.getOperationType());
                if (respObj1 instanceof KnXDMPAMAccInfoDTO) {
                    knLogger.debug(methodName, "respObj1 instanceof  KnXDMPAMAccInfoDTO - ");
                    responseDTO = getResponseObject(respObj, pamAccInfoDTO);
                } else if (respObj1 instanceof KnXDMRespDTO) {
                    knLogger.debug(methodName, "respObj1 instanceof KnXDMRespDTO - ");
                    respDTO = getXdmResponseObject(respObj, pamAccInfoDTO);
                } else {
                    knLogger.debug(methodName, "respObj1 other instance - ");
                    responseDTO = getResponseObject(respObj, pamAccInfoDTO);
                }

                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();

                switch (action) {
                    case com.kodiak.common.resources.KnConstants.OP_ID_CREATE_PAM_ACCOUNT:
                        knLogger.debug(methodName, " - create action");
                        this.createLicensePackResponse(responseDTO, pamAccInfoDTO, message.getCorrelationId(), persisterTxn);
                        break;

                    case com.kodiak.common.resources.KnConstants.OP_ID_UPGRADE_PAM_ACCOUNT:
                        knLogger.debug(methodName, " - upgrade action");
                        this.upgradeLicensePackResponse(responseDTO, pamAccInfoDTO, message.getCorrelationId(), persisterTxn);
                        break;

                    case com.kodiak.common.resources.KnConstants.OP_ID_DELETE_PAM_ACCOUNT:
                        knLogger.debug(methodName, " - delete action");
                        this.deleteLicensePackResponse(responseDTO, pamAccInfoDTO, message.getCorrelationId(), persisterTxn);
                        break;

                    case com.kodiak.common.resources.KnConstants.OP_ID_CHANGE_AUTH_STATUS_PAM_ACC:
                        knLogger.debug(methodName, " - change auth status action");
                        this.changePAMServiceAuthStatusResponse(responseDTO, pamAccInfoDTO, message.getCorrelationId(), persisterTxn);
                        break;

                    case com.kodiak.common.resources.KnConstants.OP_ID_DOWNGRADE_RATE_PLAN_PAM_ACCOUNT:
                        knLogger.debug(methodName, " - Downgrade rate plan action");
                        this.downgradeLicensePackResponse(responseDTO, pamAccInfoDTO, message.getCorrelationId(), persisterTxn);
                        break;


                    case com.kodiak.common.resources.KnConstants.OP_ID_CUSTOM_UPDATE_BAN:
                        knLogger.debug(methodName, " - Update Ban action");
                        this.updateHierarchyResponse(respDTO, pamAccInfoDTO, message.getCorrelationId(), persisterTxn);
                        break;

                    default:
                        knLogger.debug(methodName, " - Unknown action");
                }

                persisterTxn.save();
                knLogger.debug(methodName, "transaction saved");

                if (respObj1 instanceof KnXDMRespDTO) {
                    knLogger.debug(methodName, "Notification for update Ban");
                    this.commonMediator.sendResponse(respDTO, message, null,version);
                } else {
                    knLogger.debug(methodName, "Notification for pam");
                    boolean isFinal = this.xdmPamHelper.isFinalNotification(responseDTO, message, action, pamAccInfoDTO.isIsprofileChange());
                    if (isFinal) {
                        this.commonMediator.sendResponse(responseDTO, message, null,version);
                    }
                }
                bulkOrders.add(bulkOrderInfoDTO.getBulkOrderId());
                this.commonMediator.deleteBulkOrders(bulkOrders);
                knLogger.info(methodName, "delete  bulk orders to done  ", bulkOrders.size());

            }
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            KnDbUtil.rollback(persisterTxn);
            if (message != null) {
                this.commonMediator.genericFailureAsynResponse(responseDTO, message, e);
                // clean up table entries
                bulkOrders.add(bulkOrderInfoDTO.getBulkOrderId());
                this.cleanUpOnFailure(bulkOrders, responseDTO);
            }
        }
        knLogger.exit(methodName);

    }

    private void cleanUpOnFailure(List<Integer> bulkOrders, KnXDMPAMRespDTO xdmpamRespDTO) {
        final String methodName = "cleanUp(List<Integer>, KnPersisterTxn)";
        knLogger.entry(methodName);
        // deleting bulk order entry

        KnPersisterTxn persisterTxn = null;
        try {
            this.commonMediator.deleteBulkOrders(bulkOrders);
            knLogger.info(methodName, "delete  bulk orders to done  ", bulkOrders.size());

            if (xdmpamRespDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();

                knLogger.debug(methodName, "Custom is ON");
                Map<String, Object> customMap = new HashMap<>();
                customMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_DELETE_BULKOPINFO_OP);
                customMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);

                KnBulkOpInfoDTO bulkOpInfoDTO = new KnBulkOpInfoDTO();
                bulkOpInfoDTO.setTransID(xdmpamRespDTO.getTransactionId());
                bulkOpInfoDTO.setCustomParamMap(customMap);

                knLogger.debug(methodName, "Calling Custom lib with - ", bulkOpInfoDTO);
                Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, bulkOpInfoDTO);
                knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);

                persisterTxn.save();
            }

        } catch (Exception e) {
            KnDbUtil.rollback(persisterTxn);
            knLogger.error(methodName, "Exception occured while cleaning records ", e);
        }
        knLogger.exit(methodName);
    }

    private KnBulkOrderInfoDTO populateBulkOrderInfoDTO(KnPoCXLATableDetailsDTO poCXLATableDetailsDTO) {
        final String methodName = "populateBulkOrderInfoDTO(KnPoCXLATableDetailsDTO)";
        knLogger.entry(methodName, poCXLATableDetailsDTO);
        KnPoCXLAEventDTO poCXLAEventDTO;
        Map<String, KnPoCXLAEventDTO> colDetailsMap = poCXLATableDetailsDTO.getColumnDetailsMap();
        knLogger.debug(methodName, "Recived column details - ", colDetailsMap);
        KnBulkOrderInfoDTO bulkOrderInfoDTO = new KnBulkOrderInfoDTO();

        poCXLAEventDTO = colDetailsMap.get(KnMediatorConstants.XDM_BULK_ORDER_INFO.BULKORDER_ID.value());
        bulkOrderInfoDTO.setBulkOrderId((int) poCXLAEventDTO.getColumnValue());

        poCXLAEventDTO = colDetailsMap.get(KnMediatorConstants.XDM_BULK_ORDER_INFO.BULKORDER_TYPE.value());
        bulkOrderInfoDTO.setBulkOrderType((int) poCXLAEventDTO.getColumnValue());

        poCXLAEventDTO = colDetailsMap.get(KnMediatorConstants.XDM_BULK_ORDER_INFO.STATUS.value());
        bulkOrderInfoDTO.setStatus((Integer) poCXLAEventDTO.getColumnValue());

        poCXLAEventDTO = colDetailsMap.get(KnMediatorConstants.XDM_BULK_ORDER_INFO.BO_REQ_OBJECT.value());
        bulkOrderInfoDTO.setBulkOrderReqObj(poCXLAEventDTO.getColumnValue());

        poCXLAEventDTO = colDetailsMap.get(KnMediatorConstants.XDM_BULK_ORDER_INFO.BO_RESP_OBJECT.value());
        bulkOrderInfoDTO.setBulkOrderRespObj(poCXLAEventDTO.getColumnValue());

        knLogger.exit(methodName, bulkOrderInfoDTO);
        return bulkOrderInfoDTO;
    }

    public void createLicensePackResponse(KnXDMPAMRespDTO responseDTO, KnXDMPAMAccInfoDTO pamAccInfoDTO, String correlationID, KnPersisterTxn persisterTxn)
            throws KnException {
        final String methodName = "createLicensePackRespone(KnXDMPAMRespDTO, KnXDMPAMAccInfoDTO, String, persisterTxn)";
        knLogger.entry(methodName);

        String extCorpID = null;
        KnXDMPAMSubsProfInfoDTO pamSubsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
        if (pamSubsProfInfoDTO != null) {
            extCorpID = pamSubsProfInfoDTO.getExtCorpId();
        }

        if (responseDTO.getResponseStatus() == RESPONSE_STATUS.SUCCESS.value()) {
            //disable autopairing
            if (pamAccInfoDTO.getProfileDetails().isAutoPair() != null && !pamAccInfoDTO.getProfileDetails().isAutoPair()) {
                KnXDMCorpRespDTO corpRespDTO = null;
                knLogger.debug(methodName, "Corp auto pair is enabled. call corp api for disabling");
                KnOPCorpProfileInfoDTO corpProfileInfoDTO = provClientIntf.retrieveCorporateProfile(pamAccInfoDTO.getProfileDetails().getExtCorpId(), persisterTxn);
                // disable auto pairing
                KnXDMCorpInfoRequestDTO corpInfoRequestDTO = new KnXDMCorpInfoRequestDTO();
                corpInfoRequestDTO.setEnableAutoPair(Boolean.FALSE);
                corpInfoRequestDTO.setCorpId(String.valueOf(corpProfileInfoDTO.getCorpId()));
                corpInfoRequestDTO.setHierarchyType(pamAccInfoDTO.getHierarchyType());
                corpRespDTO = corpMediator.updateCorpAutoPairing(corpInfoRequestDTO, persisterTxn);

                knLogger.debug(methodName, "Corp Autopairing response - ", corpRespDTO);
                if (corpRespDTO.getResponseStatus() != KnMediatorConstants.SUCCESS) {
                    throw new KnXDMServerException(corpRespDTO.getResponseCode(), corpRespDTO.getResponseMessage());
                }
            }

            responseDTO.setPamStatus(KnMediatorConstants.PAM_ACCOUNT_STATE.ACTIVE.value());
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_CREATED, pamAccInfoDTO.getSubsCount());
            this.auditSuccess(correlationID, KnMediatorConstants.PAM_AUDIT_CREATE_LICENSE_PACK, pamAccInfoDTO.getBillingNumber(), extCorpID);
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_CREATE_PAM_ACCOUNT_REQ_SUCCESS);

            // NNI subscriber etag update and peg increment
            KnXDMPAMSubsProfInfoDTO subsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
            this.provClientIntf.updateEtagForNNISubscr(subsProfInfoDTO.getExtCorpId(), subsProfInfoDTO.getClient_Type(), persisterTxn);

            //NNI pegs for alias MDN and Group MDN
            int clientType = subsProfInfoDTO.getClient_Type();
            SUBS_CLIENT_TYPE subsClientType = SUBS_CLIENT_TYPE.getValueOf(clientType);
            knLogger.debug(methodName, "incrementing nni pegs.. subs count -", pamAccInfoDTO.getSubsCount());
            switch (subsClientType) {
                case ALIASMDN:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_NNI_ALIAS_SUBSCRIBERS_CREATED, pamAccInfoDTO.getSubsCount());
                    break;
                case GROUPMDN:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_NNI_GROUP_SUBSCRIBERS_CREATED, pamAccInfoDTO.getSubsCount());
                    break;
                case CROSSCARRIER:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_CROSS_CARRIER_PTT_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                    break;
                case MOBILEAPI:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_MOBILE_API_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                    break;
                case THIRDPARTYDISPATCHERCLIENT:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_3RDPARTY_DISPATCHER_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                    break;
                case PTTRADIOHANDSETCLIENT:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_PTTRADIO_HANDSET_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                    break;
                case PTTRADIOCROSSCARRIERCLIENT:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_PTTRADIO_CROSSCARRIER_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                    break;
                case PTTRADIOWIFIONLYCLIENT:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_PTTRADIO_WIFIONLY_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                    break;
                case SGMDNPATCH:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                    break;
                default:
                    break;
            }

        } else {
            KnIPPAMAccInfoDTO pamAccountInfoDTO = this.provMediator.populatePAMAccountDTO(pamAccInfoDTO);
            knLogger.debug(methodName, "Deleting PAM Account", pamAccountInfoDTO);
            this.provClientIntf.deletePAMAccount(pamAccountInfoDTO, persisterTxn);
            knLogger.debug(methodName, "Deleted PAM Account");
            this.auditFailure(correlationID, KnMediatorConstants.PAM_AUDIT_CREATE_LICENSE_PACK, pamAccInfoDTO.getBillingNumber(), extCorpID, responseDTO.getResponseCode());
        }

        knLogger.exit(methodName);
    }

    public void upgradeLicensePackResponse(KnXDMPAMRespDTO responseDTO, KnXDMPAMAccInfoDTO pamAccInfoDTO, String correlationID, KnPersisterTxn persisterTxn)
            throws KnException {
        final String methodName = "upgradeLicensePackRespone(KnXDMPAMRespDTO, KnXDMPAMAccInfoDTO, String, persisterTxn)";
        knLogger.entry(methodName);

        try {
            String extCorpID = null;
            KnXDMPAMSubsProfInfoDTO pamSubsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
            if (pamSubsProfInfoDTO != null) {
                extCorpID = pamSubsProfInfoDTO.getExtCorpId();
            }

            if (responseDTO.getResponseStatus() == RESPONSE_STATUS.SUCCESS.value()) {
                KnOPPAMAccInfoDTO respPAMAccInfoDTO = this.provClientIntf.getPAMAccountInfo(pamAccInfoDTO.getBillingNumber(), persisterTxn);
                knLogger.debug(methodName, " - got PAM account state :", respPAMAccInfoDTO.getPamAccState());
                responseDTO.setPamStatus(respPAMAccInfoDTO.getPamAccState());
                // update tmaxsubscribers
                KnIPPAMAccInfoDTO ippamAccInfoDTO = new KnIPPAMAccInfoDTO();
                ippamAccInfoDTO.setBillingMdn(pamAccInfoDTO.getBillingNumber());
                ippamAccInfoDTO.setTotalNoOfLines(pamAccInfoDTO.getTotalNoOfLines());

                knLogger.debug(methodName, "updating max subscriber - ", KnGDPRTemplate.mdn(pamAccInfoDTO.getBillingNumber()), " ", pamAccInfoDTO.getTotalNoOfLines());
                this.provClientIntf.updatePAMAccMaxSub(ippamAccInfoDTO, persisterTxn);

                //Invoking the Custom Invoker for Custom data:
                if (pamAccInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                    knLogger.debug(methodName, "Custom Flag is ON. update Rateplan");
                    this.xdmPamHelper.updateRatePlan(pamAccInfoDTO, persisterTxn);
                }

                //disable autopairing
                if (pamAccInfoDTO.getProfileDetails().isAutoPair() != null && !pamAccInfoDTO.getProfileDetails().isAutoPair()) {
                    KnXDMCorpRespDTO corpRespDTO = null;
                    knLogger.debug(methodName, "Corp auto pair is enabled. call corp api for disabling");
                    KnOPCorpProfileInfoDTO corpProfileInfoDTO = provClientIntf.retrieveCorporateProfile(pamAccInfoDTO.getProfileDetails().getExtCorpId(), persisterTxn);
                    // disable auto pairing
                    KnXDMCorpInfoRequestDTO corpInfoRequestDTO = new KnXDMCorpInfoRequestDTO();
                    corpInfoRequestDTO.setEnableAutoPair(Boolean.FALSE);
                    corpInfoRequestDTO.setCorpId(String.valueOf(corpProfileInfoDTO.getCorpId()));
                    corpInfoRequestDTO.setHierarchyType(pamAccInfoDTO.getHierarchyType());
                    corpRespDTO = corpMediator.updateCorpAutoPairing(corpInfoRequestDTO, persisterTxn);

                    knLogger.debug(methodName, "Corp Autopairing response - ", corpRespDTO);
                    if (corpRespDTO.getResponseStatus() != KnMediatorConstants.SUCCESS) {
                        throw new KnXDMServerException(corpRespDTO.getResponseCode(), corpRespDTO.getResponseMessage());
                    }
                }

                // updatePam etags
                this.provClientIntf.updatePAMEtag(pamAccInfoDTO.getPamAccId(), persisterTxn);

                // audit
                this.auditSuccess(correlationID, KnMediatorConstants.PAM_AUDIT_UPGRADE_LICENSE_PACK, pamAccInfoDTO.getBillingNumber(), extCorpID);
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_CREATED, pamAccInfoDTO.getSubsCount());
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_UPDATE_RATE_PLAN_REQ_SUCCESS);

                // NNI subscriber etag update and peg increment
                KnXDMPAMSubsProfInfoDTO subsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
                this.provClientIntf.updateEtagForNNISubscr(subsProfInfoDTO.getExtCorpId(), subsProfInfoDTO.getClient_Type(), persisterTxn);

                //NNI pegs for alias MDN and Group MDN
                int clientType = subsProfInfoDTO.getClient_Type();
                SUBS_CLIENT_TYPE subsClientType = SUBS_CLIENT_TYPE.getValueOf(clientType);
                knLogger.debug(methodName, "incrementing nni pegs.. subs count -", pamAccInfoDTO.getSubsCount());
                switch (subsClientType) {
                    case ALIASMDN:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_NNI_ALIAS_SUBSCRIBERS_CREATED, pamAccInfoDTO.getSubsCount());
                        break;
                    case GROUPMDN:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_NNI_GROUP_SUBSCRIBERS_CREATED, pamAccInfoDTO.getSubsCount());
                        break;
                    case CROSSCARRIER:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_CROSS_CARRIER_PTT_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                        break;
                    case MOBILEAPI:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_MOBILE_API_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                        break;
                    case THIRDPARTYDISPATCHERCLIENT:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_3RDPARTY_DISPATCHER_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                        break;
                    case PTTRADIOHANDSETCLIENT:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_PTTRADIO_HANDSET_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                        break;
                    case PTTRADIOCROSSCARRIERCLIENT:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_PTTRADIO_CROSSCARRIER_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                        break;
                    case PTTRADIOWIFIONLYCLIENT:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_PTTRADIO_WIFIONLY_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                        break;
                    case SGMDNPATCH:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_CREATED, pamAccInfoDTO.getSubsCount());
                        break;
                    default:
                        break;
                }

            } else {
                this.auditFailure(correlationID, KnMediatorConstants.PAM_AUDIT_UPGRADE_LICENSE_PACK, pamAccInfoDTO.getBillingNumber(), extCorpID, responseDTO.getResponseCode());
            }

        } catch (KnProcessInvokerException e) {
            knLogger.error(methodName, "KnProcessInvokerException occured", e);
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage());
        }

        knLogger.exit(methodName);
    }

    private void deleteLicensePackResponse(KnXDMPAMRespDTO responseDTO, KnXDMPAMAccInfoDTO pamAccInfoDTO, String correlationID, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        final String methodName = "deleteLicensePackResponse(KnXDMPAMRespDTO, KnXDMPAMAccInfoDTO, String, KnPersisterTxn)";
        knLogger.entry(methodName);

        String extCorpID = null;
        KnXDMPAMSubsProfInfoDTO pamSubsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
        if (pamSubsProfInfoDTO != null) {
            extCorpID = pamSubsProfInfoDTO.getExtCorpId();
        }

        if (responseDTO.getResponseStatus() == RESPONSE_STATUS.SUCCESS.value()) {
            // deleta PAM Account details
            KnIPPAMAccInfoDTO pamAccountInfoDTO = this.provMediator.populatePAMAccountDTO(pamAccInfoDTO);
            knLogger.debug(methodName, "Deleting pamaccount - ", pamAccountInfoDTO);
            KnOPDeletePAMAccountDTO respDeleteDTO = this.provClientIntf.deletePAMAccount(pamAccountInfoDTO, persisterTxn);
            knLogger.debug(methodName, "deleted PAM Account", respDeleteDTO);

            // pam audit
            this.auditSuccess(correlationID, KnMediatorConstants.PAM_AUDIT_DELETE_LICENSE_PACK, pamAccInfoDTO.getBillingNumber(), extCorpID);
            // pegging
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_DELETED, pamAccInfoDTO.getSubsCount());
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_DELETE_PAM_ACCOUNT_REQ_SUCCESS);

            // NNI subscriber etag update and peg increment
            KnXDMPAMSubsProfInfoDTO subsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
            this.provClientIntf.updateEtagForNNISubscr(subsProfInfoDTO.getExtCorpId(), subsProfInfoDTO.getClient_Type(), persisterTxn);

            //NNI pegs for alias MDN and Group MDN
            int clientType = subsProfInfoDTO.getClient_Type();
            SUBS_CLIENT_TYPE subsClientType = SUBS_CLIENT_TYPE.getValueOf(clientType);
            knLogger.debug(methodName, "incrementing nni pegs.. subs count -", pamAccInfoDTO.getSubsCount());
            switch (subsClientType) {
                case ALIASMDN:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_NNI_ALIAS_SUBSCRIBERS_DELETED, pamAccInfoDTO.getSubsCount());
                    break;
                case GROUPMDN:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_NNI_GROUP_SUBSCRIBERS_DELETED, pamAccInfoDTO.getSubsCount());
                    break;
                case CROSSCARRIER:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_CROSS_CARRIER_PTT_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                    break;
                case MOBILEAPI:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_MOBILE_API_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                    break;
                case THIRDPARTYDISPATCHERCLIENT:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_3RDPARTY_DISPATCHER_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                    break;
                case PTTRADIOHANDSETCLIENT:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_PTTRADIO_HANDSET_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                    break;
                case PTTRADIOCROSSCARRIERCLIENT:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_PTTRADIO_CROSSCARRIER_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                    break;
                case PTTRADIOWIFIONLYCLIENT:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_PTTRADIO_WIFIONLY_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                    break;
                case SGMDNPATCH:
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                    break;
                default:
                    break;
            }

        } else {
            this.auditFailure(correlationID, KnMediatorConstants.PAM_AUDIT_DELETE_LICENSE_PACK, pamAccInfoDTO.getBillingNumber(), extCorpID, responseDTO.getResponseCode());
        }

        knLogger.exit(methodName);
    }

    public void changePAMServiceAuthStatusResponse(KnXDMPAMRespDTO responseDTO, KnXDMPAMAccInfoDTO pamAccInfoDTO, String correlationID, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        final String methodName = "changePAMServiceAuthStatusResponse(KnXDMPAMRespDTO, KnXDMPAMAccInfoDTO, String, KnPersisterTxn)";
        knLogger.entry(methodName);

        String extCorpID = null;
        KnXDMPAMSubsProfInfoDTO pamSubsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
        if (pamSubsProfInfoDTO != null) {
            extCorpID = pamSubsProfInfoDTO.getExtCorpId();
        }

        if (responseDTO.getResponseStatus() == RESPONSE_STATUS.SUCCESS.value()) {
            KnIPPAMAccInfoDTO pamAccountInfoDTO = new KnIPPAMAccInfoDTO();
            pamAccountInfoDTO.setBillingMdn(pamAccInfoDTO.getBillingNumber());
            int serviceAuthStatus = pamAccInfoDTO.getProfileDetails().getServiceAuthStatus();

            if (serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value()) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_ACTIVATED, pamAccInfoDTO.getSubsCount());
                responseDTO.setPamStatus(KnMediatorConstants.PAM_ACCOUNT_STATE.ACTIVE.value());
                pamAccountInfoDTO.setPamAccState(KnMediatorConstants.PAM_ACCOUNT_STATE.ACTIVE.value());
                // audit
                this.auditSuccess(correlationID, KnMediatorConstants.PAM_AUDIT_CHANGE_SERVICE_AUTH_STATUS, pamAccInfoDTO.getBillingNumber(), extCorpID);

            } else if (serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()) {
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_DEACTIVATED, pamAccInfoDTO.getSubsCount());
                responseDTO.setPamStatus(KnMediatorConstants.PAM_ACCOUNT_STATE.SUSPEND.value());
                pamAccountInfoDTO.setPamAccState(KnMediatorConstants.PAM_ACCOUNT_STATE.SUSPEND.value());
                // audit
                this.auditSuccess(correlationID, KnMediatorConstants.PAM_AUDIT_CHANGE_SERVICE_AUTH_STATUS, pamAccInfoDTO.getBillingNumber(), extCorpID);
            }

            KnOPUpdatePAMAccountDTO respPAMUpdateDTO = this.provClientIntf.updatePAMAccState(pamAccountInfoDTO, persisterTxn);
            // updatePam etags
            this.provClientIntf.updatePAMEtag(pamAccInfoDTO.getPamAccId(), persisterTxn);

            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_UPDATED, pamAccInfoDTO.getSubsCount());
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_CHANGE_PAMSERVICEAUTH_REQ_SUCCESS);
            knLogger.debug(methodName, " - updated PAM account state :", respPAMUpdateDTO);

        } else {
            this.auditFailure(correlationID, KnMediatorConstants.PAM_AUDIT_CHANGE_SERVICE_AUTH_STATUS, pamAccInfoDTO.getBillingNumber(), extCorpID, responseDTO.getResponseCode());
        }

        knLogger.exit(methodName);
    }

    private void downgradeLicensePackResponse(KnXDMPAMRespDTO responseDTO, KnXDMPAMAccInfoDTO pamAccInfoDTO, String correlationID, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        final String methodName = "downgradeLicensePackResponse(KnXDMPAMRespDTO, KnXDMPAMAccInfoDTO, String, KnPersisterTxn)";
        knLogger.entry(methodName);
        try {

            String extCorpID = null;
            KnXDMPAMSubsProfInfoDTO pamSubsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
            if (pamSubsProfInfoDTO != null) {
                extCorpID = pamSubsProfInfoDTO.getExtCorpId();
            }

            if (responseDTO.getResponseStatus() == RESPONSE_STATUS.SUCCESS.value()) {
                KnIPPAMAccInfoDTO ippamAccInfoDTO = this.provMediator.populatePAMAccountDTO(pamAccInfoDTO);
                ippamAccInfoDTO.setPamAccId(pamAccInfoDTO.getPamAccId());
                this.provClientIntf.updatePAMAccMaxSub(ippamAccInfoDTO, persisterTxn);

                //Invoking the Custom Invoker for Custom data:
                if (pamAccInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                    knLogger.debug(methodName, "Custom Flag is ON. update Rateplan");
                    this.xdmPamHelper.updateRatePlan(pamAccInfoDTO, persisterTxn);
                }

                // updatePam etags
                this.provClientIntf.updatePAMEtag(pamAccInfoDTO.getPamAccId(), persisterTxn);

                // PAM Audits
                this.auditSuccess(correlationID, KnMediatorConstants.PAM_AUDIT_DOWNGRADE_LICENSE_PACK, pamAccInfoDTO.getBillingNumber(), extCorpID);
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_UPDATE_RATE_PLAN_REQ_SUCCESS);
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_DELETED, pamAccInfoDTO.getSubsCount());

                // NNI subscriber etag update and peg increment
                KnXDMPAMSubsProfInfoDTO subsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
                this.provClientIntf.updateEtagForNNISubscr(subsProfInfoDTO.getExtCorpId(), subsProfInfoDTO.getClient_Type(), persisterTxn);

                responseDTO.setPamStatus(pamAccInfoDTO.getPamAccState());

                //NNI pegs for alias MDN and Group MDN
                int clientType = subsProfInfoDTO.getClient_Type();
                SUBS_CLIENT_TYPE subsClientType = SUBS_CLIENT_TYPE.getValueOf(clientType);
                knLogger.debug(methodName, "incrementing nni pegs.. subs count -", pamAccInfoDTO.getSubsCount());
                switch (subsClientType) {
                    case ALIASMDN:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_NNI_ALIAS_SUBSCRIBERS_DELETED, pamAccInfoDTO.getSubsCount());
                        break;
                    case GROUPMDN:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_NNI_GROUP_SUBSCRIBERS_DELETED, pamAccInfoDTO.getSubsCount());
                        break;
                    case CROSSCARRIER:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_CROSS_CARRIER_PTT_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                        break;
                    case MOBILEAPI:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_MOBILE_API_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                        break;
                    case THIRDPARTYDISPATCHERCLIENT:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_3RDPARTY_DISPATCHER_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                        break;
                    case PTTRADIOHANDSETCLIENT:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_PTTRADIO_HANDSET_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                        break;
                    case PTTRADIOCROSSCARRIERCLIENT:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_PTTRADIO_CROSSCARRIER_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                        break;
                    case PTTRADIOWIFIONLYCLIENT:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_PTTRADIO_WIFIONLY_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                        break;
                    case SGMDNPATCH:
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_DELETED, pamAccInfoDTO.getSubsCount());
                        break;
                    default:
                        break;
                }

            } else {
                this.auditFailure(correlationID, KnMediatorConstants.PAM_AUDIT_DOWNGRADE_LICENSE_PACK, pamAccInfoDTO.getBillingNumber(), extCorpID, responseDTO.getResponseCode());
            }


        } catch (KnProcessInvokerException e) {
            knLogger.error(methodName, "KnProcessInvokerException occured", e);
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage());
        }

        knLogger.exit(methodName);
    }


    private void updateHierarchyResponse(KnXDMRespDTO responseDTO, KnXDMPAMAccInfoDTO pamAccInfoDTO, String correlationID, KnPersisterTxn persisterTxn)
            throws KnException {
        final String methodName = "updateHierarchyResponse(KnXDMRespDTO, KnXDMPAMAccInfoDTO, String, KnPersisterTxn)";
        knLogger.entry(methodName);
        try {

            if (responseDTO.getResponseStatus() == RESPONSE_STATUS.SUCCESS.value()) {

                // disable autopairing
                if (pamAccInfoDTO.getProfileDetails().isAutoPair() != null && !pamAccInfoDTO.getProfileDetails().isAutoPair()) {
                    KnOPCorpProfileInfoDTO corpProfileInfoDTO = provClientIntf.retrieveCorporateProfile(pamAccInfoDTO.getProfileDetails().getExtCorpId(), persisterTxn);
                    if (corpProfileInfoDTO.getPairedContactListId() > 0) {
                        KnXDMCorpRespDTO corpRespDTO = null;
                        knLogger.debug(methodName, "Corp auto pair is enabled. call corp api for disabling");
                        // disable auto pairing
                        KnXDMCorpInfoRequestDTO corpInfoRequestDTO = new KnXDMCorpInfoRequestDTO();
                        corpInfoRequestDTO.setEnableAutoPair(Boolean.FALSE);
                        corpInfoRequestDTO.setCorpId(String.valueOf(corpProfileInfoDTO.getCorpId()));
                        corpInfoRequestDTO.setHierarchyType(pamAccInfoDTO.getHierarchyType());
                        corpRespDTO = corpMediator.updateCorpAutoPairing(corpInfoRequestDTO, persisterTxn);

                        knLogger.debug(methodName, "Corp Autopairing response - ", corpRespDTO);
                        if (corpRespDTO.getResponseStatus() != KnMediatorConstants.SUCCESS) {
                            throw new KnXDMServerException(corpRespDTO.getResponseCode(), corpRespDTO.getResponseMessage());
                        }
                    }
                }

                // int corpid=0;//TODO
                //this.provClientIntf.retrievePAMAccIdForCorpId(pamAccInfoDTO)
                Map<String, Object> customMap = pamAccInfoDTO.getProfileDetails().getCustomParamMap();
                knLogger.debug(methodName, "customParamMap", customMap);
                int corpId = (Integer) customMap.get("OLD_CORP_ID");
                knLogger.debug(methodName, "corpId", corpId);
                List<Integer> pamAccIdList = this.provClientIntf.retrievePAMAccIdForCorpId(corpId, persisterTxn);
                // updatePam etags
                for (int pamAccId : pamAccIdList) {
                    this.provClientIntf.updatePAMEtag(pamAccId, persisterTxn);
                }
                auditProv.writeAuditMessage("CID:" + correlationID, KnMediatorConstants.UPDATE_BAN_AUDIT, KnAuditHelper.STATUS.SUCCESS, "Operation Successful");
                KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.NUM_UPDATE_BAN_API_SUCC_RESP);
                //responseDTO.set(pamAccInfoDTO.getPamAccState());
            } else {
                auditProv.writeAuditMessage("CID:" + correlationID, KnMediatorConstants.UPDATE_BAN_AUDIT, KnAuditHelper.STATUS.FAILURE, "Operation failed ", responseDTO.getResponseCode());

            }


        } catch (KnProvException e) {
            knLogger.error(methodName, "DAO Exception occured", e);
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage());

        }
        knLogger.exit(methodName);
    }

    private KnXDMPAMRespDTO getResponseObject(Object respObj, KnXDMPAMAccInfoDTO pamAccInfoDTO) throws Exception {
        KnXDMPAMRespDTO responseDTO = new KnXDMPAMRespDTO();
        byte[] respBuff = (byte[]) respObj;
        ObjectInputStream objectOut = null;
        try {
            objectOut = new ObjectInputStream(new ByteArrayInputStream(respBuff));
            responseDTO = (KnXDMPAMRespDTO) objectOut.readObject();
            responseDTO.setTransactionId(pamAccInfoDTO.getTransactionId());
            responseDTO.setBillingNumber(pamAccInfoDTO.getBillingNumber());
            responseDTO.setCustomParamMap(pamAccInfoDTO.getOpMap());
            responseDTO.setHierarchyType(pamAccInfoDTO.getHierarchyType());
            return responseDTO;
        } finally {
            if (objectOut != null) {
                objectOut.close();
            }
        }
    }

    private KnXDMRespDTO getXdmResponseObject(Object respObj1, KnXDMPAMAccInfoDTO pamAccInfoDTO) throws Exception {
        KnXDMRespDTO responseDTO = new KnXDMRespDTO();
        byte[] respBuff = (byte[]) respObj1;
        ObjectInputStream objectOut = null;
        try {
            objectOut = new ObjectInputStream(new ByteArrayInputStream(respBuff));
            responseDTO = (KnXDMRespDTO) objectOut.readObject();
            responseDTO.setCustomParamMap(pamAccInfoDTO.getOpMap());
            responseDTO.setTransactionId(pamAccInfoDTO.getTransactionId());
            //	responseDTO.setResponseCode();
            //responseDTO.setResponseDetails();
            //responseDTO.setTransactionId(pamAccInfoDTO.getTransactionId());
            //responseDTO.setBillingNumber(pamAccInfoDTO.getBillingNumber());
            //responseDTO.setCustomParamMap(pamAccInfoDTO.getOpMap());
            return responseDTO;
        } finally {
            if (objectOut != null) {
                objectOut.close();
            }
        }
    }


    private void auditSuccess(String correlationID, String operation, String billingNumber, String extCorpID) {

    	StringBuffer auditString=new StringBuffer().append("Operation Successful for Billing MDN:").append(billingNumber);
        if(extCorpID != null){
        	auditString.append(",ExtCorpID:").append(extCorpID);
        }
    	this.auditPam.writeAuditMessage("CID:" + correlationID, operation, KnAuditHelper.STATUS.SUCCESS,auditString.toString());
    }

    private void auditFailure(String correlationID, String operation, String billingNumber, String extCorpID, String errorCode) {

    	StringBuffer auditString=new StringBuffer().append("Operation failed for Billing MDN:").append(billingNumber);
    	if(extCorpID != null){
                auditString.append(",ExtCorpID:").append(extCorpID);
    	}
    	this.auditPam.writeAuditMessage(new StringBuffer().append("CID:").append(correlationID).toString(), operation, KnAuditHelper.STATUS.FAILURE,auditString.toString(), errorCode);
    }

}
