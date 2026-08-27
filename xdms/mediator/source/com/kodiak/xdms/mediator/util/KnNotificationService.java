/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.frameworks.messaging.common.KnMessageException;
import com.kodiak.frameworks.messaging.producer.KnRmqMessagePublisher;
import com.kodiak.frameworks.messaging.resources.KnRmqConfig;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnXDMPAMRespDTO;
import com.kodiak.common.commdto.response.KnXDMRespDTO;
import com.kodiak.common.commdto.response.KnXDMSubsProfileRespDTO;
import com.kodiak.utilities.jaxb.beans.KnJAXBParser;
import com.kodiak.utilities.jaxb.beans.prov.ids.KnAccountInfoBean;
import com.kodiak.utilities.jaxb.beans.prov.ids.KnHeaderInfoBean;
import com.kodiak.utilities.jaxb.beans.prov.pamnotification.*;
import com.kodiak.xdms.mediator.KnMediatorConstants;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.resources.KnConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;




public class KnNotificationService {
    private static final KnLogger knLogger = KnLogger.getLogger(KnNotificationService.class);

    private static KnServiceError errorMapper;
    private static KnAuditHelper audit = null;
    private static String NOTIFICATION = "NOTIFICATION";
    private static KnRmqMessagePublisher msgFw;



    public KnNotificationService(KnAuditHelper audit) {
        //try {
            String methodName = "KnNotificationService(KnServiceError,KnAuditHelper)";
            errorMapper = KnServiceError.getInstance(KnMediatorConstants.ERROR_CODE_MAPPINGS_FILE, KnMediatorConstants.PAM_ERROR_CODE_MAPPINGS_FILE ,KnMediatorConstants.SOAP_PAM_ERROR_CODE_MAPPINGS_FILE);
            knLogger.info(methodName, "Entry Constructor");
            KnNotificationService.audit = audit;
            msgFw = KnRmqMessagePublisher.getInstance();
        /*}catch (KnMessageException e) {
            knLogger.fatal("KnNotificationService", "Msg Fwk Initialization failed ", e);
        }*/
    }


    public static boolean notifyResponse(IXDMResponseDTO msgObj,String version) {
        String methodName = "notifyResponse(KnMessage)";
        KnXDMRespDTO respObj = null;
        KnXDMPAMRespDTO pamRespObj = null;
        String mdn = null;
        String txnId = null;
        int clientType = -1;
        boolean imeiChanged = false;
        boolean notifyResp = false;
        ArrayList<com.kodiak.utilities.jaxb.beans.prov.ids.KnProductInfoBean> prodsList = null;
       String modifiedAction = null;

        try {
            //retrieving the message Payload

            if (msgObj instanceof IXDMResponseDTO) {
                notifyResp = true;
                if (msgObj instanceof KnXDMRespDTO) {
                    respObj = (KnXDMRespDTO) msgObj;
                    knLogger.debug(methodName, "IDS Notification Recieved");
                    knLogger.debug(methodName, "Pay load Object : KnXDMRespDTO :", respObj);
                    if (respObj.getCustomParamMap() != null) {
                        Map<String, Object> customRespMap = respObj.getCustomParamMap();
                        if (customRespMap.containsKey(KnMediatorConstants.IMEI_CHANGED)) {
                            imeiChanged = (Boolean) customRespMap.get(KnMediatorConstants.IMEI_CHANGED);
                            knLogger.debug(methodName, "IMEI Changed :", imeiChanged);
                        }
                        if (customRespMap.containsKey(KnMediatorConstants.CLIENT_TYPE)) {
                            clientType = (Integer) customRespMap.get(KnMediatorConstants.CLIENT_TYPE);
                            knLogger.debug(methodName, "ClientType :", clientType);
                        }
                        if (customRespMap.containsKey(KnMediatorConstants.ACTION)) {
                            modifiedAction = (String) customRespMap.get(KnMediatorConstants.ACTION);
                            knLogger.debug(methodName, "modifiedAction :", modifiedAction);
                        }
                    }
                    txnId = respObj.getTransactionId();
                    if (txnId == null || txnId.isEmpty()) {
                        //PEGS FOR Total number of  'failure' response notifications received from backend XDMS.
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.WPT_POC_3PP_NUM_FAIL_RESPNTFY_FROM_XDMS);
                        throw new KnXDMServerException("INTERNAL_SERVER_ERROR", "trans Id not fetched from server");


                    }
                    Map<String, Object> customMap = KnTxnManagerImpl.getInstance().getCustomMap(txnId);

                    mdn = customMap.get(KnMediatorConstants.MSISDN).toString();
                    Map<String, List<String>> actionRatePlans=(Map<String, List<String>>)
                            customMap.get(KnMediatorConstants.ACTIONRATEPLANS);


                    audit.writeAuditMessage(txnId, NOTIFICATION, KnAuditHelper.STATUS.REQUEST,
                            actionRatePlans.toString()+" operation requested for the subscriber " + mdn);

                    //Total number of notification transactions initiated
                    KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.WPT_POC_3PP_NUM_NTFY_TXN_INITIATED);
                    String url = (String) customMap.get(KnMediatorConstants.NOTIFICATION_URL);

                    com.kodiak.utilities.jaxb.beans.prov.ids.KnProvisioningResponseBean responseBean = new com.kodiak.utilities.jaxb.beans.prov.ids.KnProvisioningResponseBean();
                    responseBean.setSchemaLocation(KnMediatorConstants.NONAME_SPACE_SCH_LOC);
                    responseBean.setSystem(KnMediatorConstants.SYSTEM);
                    KnHeaderInfoBean header = new KnHeaderInfoBean();
                    header.setTransactionId(customMap.get(KnMediatorConstants.IDS_TRANSACTION_ID).toString());
                    header.setProvCarrier(customMap.get(KnMediatorConstants.PROVISIONING_CARRIER).toString());
                    //get current date and time
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ssZ");
                    Date date = new Date();
                    String format = dateFormat.format(date);
                    String newFormat = format.substring(0, format.length() - 2) + ":" + format.substring(format.length() - 2);
                    header.setTimeStamp(newFormat);
                    com.kodiak.utilities.jaxb.beans.prov.ids.KnSenderInfoBean sender = new com.kodiak.utilities.jaxb.beans.prov.ids.KnSenderInfoBean();
                    sender.setLogin(customMap.get(KnMediatorConstants.SENDER_LOGIN).toString());
                    sender.setPassword(customMap.get(KnMediatorConstants.SENDER_PASSWORD).toString());
                    header.setSender(sender);
                    com.kodiak.utilities.jaxb.beans.prov.ids.KnTransactionCodeBean transCode = new com.kodiak.utilities.jaxb.beans.prov.ids.KnTransactionCodeBean();
                    //  mapping error code to major code
                    KnErrorBean errorBean;
                    String code = errorMapper.getServiceErrorCode(respObj.getResponseCode());
                    errorBean = errorMapper.getMappedError(code);
                    if (errorBean == null) {
                        errorBean = errorMapper.getMappedError(KnMediatorConstants.ERROR_CODE_INTERNAL_SERVER_ERROR);
                    }

                    transCode.setMajorCode(errorBean.getMajorCode());
                    transCode.setDescription(errorBean.getMajorDescription());

                    header.setTransactionCode(transCode);
                    responseBean.setHeader(header);

                    com.kodiak.utilities.jaxb.beans.prov.ids.KnOrderInfoBean order = new com.kodiak.utilities.jaxb.beans.prov.ids.KnOrderInfoBean();
                    KnAccountInfoBean account = new KnAccountInfoBean();
                    account.setMdn(mdn);
                    order.setAccount(account);
                    com.kodiak.utilities.jaxb.beans.prov.ids.KnProductsInfoBean products = new com.kodiak.utilities.jaxb.beans.prov.ids.KnProductsInfoBean();

                    //String modifiedAction = (String) customMap.get(KnIDSProvisionConstants.ACTION);

                    //====  imei enhancement ..
                    if(modifiedAction!=null && !modifiedAction.equals("null")) {
                        if (modifiedAction.equals(KnMediatorConstants.NOTIFICATIONACTIONS.ADD.value()) && (clientType == KnMediatorConstants.SUBS_CLIENT_TYPE.HANDSET.value()
                                || clientType == KnMediatorConstants.SUBS_CLIENT_TYPE.POC_DONOR_RADIO.value())) {
                            knLogger.debug(methodName, "Enter into ..create.. imei  enhancement.>.");
                            String MsgTxt = KnIDSConfigLoader.retreiveIDSConfig().get(KnMediatorConstants.IDS_CREATE_HS_SUBSCR_NTFY_SMS);
                            knLogger.debug(methodName, "Create SMS  MsgTxt ", MsgTxt);
                            account.setSms(MsgTxt);
                            transCode.setMajorCode(KnMediatorConstants.IDS_IMEI_MAJOR_CODE);
                            transCode.setDescription(KnMediatorConstants.IDS_IMEI_RESPONSE);

                        } else if (modifiedAction.equals(KnMediatorConstants.NOTIFICATIONACTIONS.MODIFY.value()) && imeiChanged && (clientType == KnMediatorConstants.SUBS_CLIENT_TYPE.HANDSET.value()
                                || clientType == KnMediatorConstants.SUBS_CLIENT_TYPE.POC_DONOR_RADIO.value())) {
                            knLogger.debug(methodName, "Enter into update..imei enhancement..");
                            String MsgTxt = KnIDSConfigLoader.retreiveIDSConfig().get(KnMediatorConstants.IDS_IMEI_CHANGE_NTFY_SMS);
                            knLogger.debug(methodName, "Update/Modify  SMS MSG Txt ", MsgTxt);
                            account.setSms(MsgTxt);
                            transCode.setMajorCode(KnMediatorConstants.IDS_IMEI_MAJOR_CODE);
                            transCode.setDescription(KnMediatorConstants.IDS_IMEI_RESPONSE);

                        } else if (modifiedAction.equals(KnMediatorConstants.NOTIFICATIONACTIONS.ADD.value()) && clientType == KnMediatorConstants.SUBS_CLIENT_TYPE.PTT_RADIO_HANDSET_CLIENT.value()) {
                            knLogger.debug(methodName, "Enter into create redio handset client sms enhancement");
                            String MsgTxt = KnIDSConfigLoader.retreiveIDSConfig().get(KnMediatorConstants.IDS_CREATE_HS_SUBSCR_NTFY_SMS_PTTRADIO);
                            knLogger.debug(methodName, "Create radio handset client SMS MsgTxt ", MsgTxt);
                            account.setSms(MsgTxt);
                            transCode.setMajorCode(KnMediatorConstants.IDS_IMEI_MAJOR_CODE);
                            transCode.setDescription(KnMediatorConstants.IDS_IMEI_RESPONSE);

                        } else if (modifiedAction.equals(KnMediatorConstants.NOTIFICATIONACTIONS.MODIFY.value()) && imeiChanged && clientType == KnMediatorConstants.SUBS_CLIENT_TYPE.PTT_RADIO_HANDSET_CLIENT.value()) {
                            knLogger.debug(methodName, "Enter into  redio handset client imei sms enhancement");
                            String MsgTxt = KnIDSConfigLoader.retreiveIDSConfig().get(KnMediatorConstants.IDS_IMEI_CHANGE_NTFY_SMS_PTTRADIO);
                            knLogger.debug(methodName, "Modify  radio handset client SMS MSG Txt ", MsgTxt);
                            account.setSms(MsgTxt);
                            transCode.setMajorCode(KnMediatorConstants.IDS_IMEI_MAJOR_CODE);
                            transCode.setDescription(KnMediatorConstants.IDS_IMEI_RESPONSE);
                        }
                    }

                    ArrayList<com.kodiak.utilities.jaxb.beans.prov.ids.KnProductInfoBean> productBeanList = (ArrayList<com.kodiak.utilities.jaxb.beans.prov.ids.KnProductInfoBean>)customMap.get(KnMediatorConstants.PRODUCTTAG);
                    ArrayList<com.kodiak.utilities.jaxb.beans.prov.ids.KnProductInfoBean> prods = new ArrayList<com.kodiak.utilities.jaxb.beans.prov.ids.KnProductInfoBean>();
                    for(com.kodiak.utilities.jaxb.beans.prov.ids.KnProductInfoBean product : productBeanList) {
                        String action = product.getAction();
                        // if (action.equals(KnMediatorConstants.CHANGEMDN)) {
                        //   action = KnMediatorConstants.MODIFY;
                        //  }
                        //If error code is different for action and featureBit,
                        //TODO: for now duplicate error code is there but in future this condition required.
                        if (modifiedAction != null && action.equalsIgnoreCase(modifiedAction)) {
                            product.setMajorCode(errorBean.getMajorCode());
                            product.setDescription(errorBean.getMajorDescription());
                        } else {
                            //this is part for featureBit
                            product.setMajorCode(errorBean.getMajorCode());
                            product.setDescription(errorBean.getMajorDescription());
                        }

                        com.kodiak.utilities.jaxb.beans.prov.ids.KnTransactionCodeBean prodtransCode = new com.kodiak.utilities.jaxb.beans.prov.ids.KnTransactionCodeBean();
                        com.kodiak.utilities.jaxb.beans.prov.ids.KnTransactionCodeListBean transCodeList = new com.kodiak.utilities.jaxb.beans.prov.ids.KnTransactionCodeListBean();

                        prodtransCode.setMajorCode(errorBean.getMajorCode());
                        prodtransCode.setDescription(errorBean.getMajorDescription());
                        transCodeList.setErrorCode(errorBean.getServiceErrorCode());
                        transCodeList.setErrorMessageText(errorBean.getErrMsg());
                        prodtransCode.setTransactionCodeList(transCodeList);
                        product.setTransactionCode(prodtransCode);

                        prods.add(product);

                        if ((transCodeList.getErrorCode() != null && transCodeList.getErrorCode().equals("70030"))) {
                            //PEGS FOR Total number of  'failure' response notifications received from backend XDMS.
                            //  KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.WPT_POC_3PP_NUM_FAIL_RESPNTFY_FROM_XDMS);
                            knLogger.debug(methodName, "skipping notification....");
                            return true;
                        }

                        products.setProduct(prods);
                        order.setProducts(products);
                        responseBean.setOrder(order);

                        if (transCode.getDescription().equalsIgnoreCase(KnMediatorConstants.IDS_IMEI_RESPONSE)) {
                            //Total number of  'success' response notifications received from backend XDMS.
                            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.WPT_POC_3PP_NUM_SUCC_RESPNTFY_FROM_XDMS);
                        }
                    }
                        if (KnMediatorConstants.UPDATEBAN.equals(customMap.get(KnMediatorConstants.ACTION))) {
                            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.WPT_POC_3PP_NUM_UPDATE_BAN_SUCC_RESP_SENT);
                        }

                        String respXML = KnJAXBParser.convertBeanToXml(responseBean, com.kodiak.utilities.jaxb.beans.prov.ids.KnProvisioningResponseBean.class);


                        //adding task
                        addTask(actionRatePlans.toString(), url, respXML, txnId, mdn,KnRmqConfig.getInstance().getIdsNotifyServiceBindingKey());
                        knLogger.debug(methodName, "removing txnId ::", txnId);


                    KnTxnManagerImpl.getInstance().removeTxId(txnId);

                    knLogger.debug(methodName, "Exit...notifyResponse(KnMessage) ");
                    return notifyResp;

                } else if (msgObj instanceof KnXDMPAMRespDTO) {
                    pamRespObj = (KnXDMPAMRespDTO) msgObj;
                    boolean isURLSame = false;
                    String opsCliPAMNotiURL = KnIDSConfigLoader.retreiveIDSConfig().get(KnMediatorConstants.OPSCLI_PAM_NTFY_URL);
                    txnId = pamRespObj.getTransactionId();
                    if (txnId == null || txnId.isEmpty()) {
                        //PEGS FOR Total number of  'failure' response notifications received from backend XDMS.
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.WPT_POC_3PP_NUM_FAIL_RESPNTFY_FROM_XDMS);
                        throw new KnXDMServerException(KnMediatorConstants.INTERNAL_SERVER_ERROR, "trans Id not fetched from server");

                    }
                    Map<String, Object> customMap = KnTxnManagerImpl.getInstance().getCustomMap(txnId);

                    if(customMap == null || customMap.isEmpty()){
                        Integer action = null;
                        String extPamAccId = null;
                        List<KnXDMSubsProfileRespDTO> subsProfileList = null;
                        if (pamRespObj.getSubProfileList() != null) {
                            subsProfileList = pamRespObj.getSubProfileList();

                        }
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.WPT_POC_3PP_TOTAL_NTFNS_INITIATED);

                        String url = (String) pamRespObj.getCustomParamMap().get(KnMediatorConstants.PAM_NOTIFICATION_URL);
                        knLogger.debug(methodName, "Notification URL from Req: ", url);
                        action = (Integer) pamRespObj.getCustomParamMap().get(KnMediatorConstants.ACTION);
                        extPamAccId = pamRespObj.getBillingNumber();
                        int pamState = pamRespObj.getPamStatus();
                        
                        //get current date and time
                        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ssZ");
                        Date date = new Date();
                        String format = dateFormat.format(date);
                        String newFormat = format.substring(0, format.length() - 2) + ":" + format.substring(format.length() - 2);
                        //  mapping error code to major code
                        KnErrorBean errorBean;
                        String code  = errorMapper.getSoapPamServiceErrorCode(pamRespObj.getResponseCode());
                        	 knLogger.debug(methodName, "error from soap pam: ", code);
                        	 errorBean = errorMapper.getSoapPamMappedError(code);
                        	 knLogger.debug(methodName, "error from soap pam: ", errorBean);
                        	 
                        if (errorBean == null) {
                            errorBean = errorMapper.getMappedError(KnMediatorConstants.ERROR_CODE_INTERNAL_SERVER_ERROR);
                        }
                        
                        String respString=null;
                       
                        String requestInterface =(String) pamRespObj.getCustomParamMap().get(KnMediatorConstants.INTERFACE);
        				if (!KnMediatorConstants.INTERFACETYPE.REST.getType().equalsIgnoreCase(requestInterface)) {
        					com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnProvResponseBean responsePamBean = new com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnProvResponseBean();
                            responsePamBean.setSchemaLocation(KnMediatorConstants.NONAME_SPACE_SCH_LOC);
                            com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnHeaderInfoBean header = new com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnHeaderInfoBean();
                            header.setTransId(txnId);
                            header.setAction(String.valueOf(action));
                            header.setTimeStamp(newFormat);
                            
                            com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnTransCodeBean transCode = new com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnTransCodeBean();
                            transCode.setStatusCode(errorBean.getMajorCode());
                            transCode.setDescription(errorBean.getMajorDescription());

                            com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnTransCodeListBean transCodeList = new com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnTransCodeListBean();
                            transCodeList.setErrorCode(errorBean.getServiceErrorCode());
                            transCodeList.setErrorMessageText(errorBean.getErrMsg());
                            transCode.setTransCodeList(transCodeList);


                            header.setTransCode(transCode);
                            responsePamBean.setHeader(header);

                            com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnAccountInfoBean account = new com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnAccountInfoBean();
                            account.setPamAccId(extPamAccId);
                            if (pamState != 0) {
                                account.setStatus(String.valueOf(pamState));
                            }
                            if (subsProfileList != null) {
                                com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnDetailsInfoBean details = new com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnDetailsInfoBean();
                                ArrayList<com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnSubscrInfoBean> subscrList = new ArrayList<com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnSubscrInfoBean>();

                                for (KnXDMSubsProfileRespDTO subsProfile : subsProfileList) {
                                    com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnSubscrInfoBean subscr = new com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnSubscrInfoBean();
                                    subscr.setMdn(subsProfile.getMdn());
                                    subscr.setAuthStatus(String.valueOf(subsProfile.getServiceAuthStatus()));
                                    subscr.setClientType(String.valueOf(subsProfile.getSubscriberClientType()));
                                    subscrList.add(subscr);
                                }
                                details.setSubscrInfo(subscrList);
                                account.setDetails(details);
                            }
                            responsePamBean.setAccountInfo(account);
                            knLogger.debug( methodName, "RTX opsCliPAMNotiURL :", opsCliPAMNotiURL);
                            respString = KnJAXBParser.convertBeanToXml(responsePamBean, com.kodiak.utilities.jaxb.beans.prov.pamnotification.KnProvResponseBean.class);
        				}
        				else{
        					KnJsonNotificationResponse response = new KnJsonNotificationResponse();
        					KnJsonHeader header = new KnJsonHeader();
        					header.setTxnId(txnId);
        					header.setTimeStamp(newFormat);
        					List<KnJsonTxnCode> txnCodes= new ArrayList<>();
        					KnJsonTxnCode txnCode = new KnJsonTxnCode();
        					txnCode.setStatusCode(errorBean.getMajorCode());
        					txnCode.setDescription(errorBean.getMajorDescription());
        					List<KnJsonTxnCodeList> txnCodeLists= new ArrayList<>();
        					KnJsonTxnCodeList txnCodeList = new KnJsonTxnCodeList();
        					txnCodeList.setErrorCode(errorBean.getServiceErrorCode());
        					txnCodeList.setErrorMsgText(errorBean.getErrMsg());
        					txnCodeLists.add(txnCodeList);
        					txnCode.setTxnCodeList(txnCodeLists);
        					txnCodes.add(txnCode);
        					header.setTxnCode(txnCodes);
        					KnJsonAccountInfo accountInfo = new KnJsonAccountInfo();
        					accountInfo.setBillingNumber(extPamAccId);
        					response.setHeader(header);
        					response.setAccountInfo(accountInfo);
        					
        					ObjectMapper mapper = new ObjectMapper();
        					respString = mapper.writeValueAsString(response);
        				}
        					


                        audit.writeAuditMessage(txnId, NOTIFICATION, KnAuditHelper.STATUS.REQUEST, getAction(action) + " Operation requested for the pam account id  " + extPamAccId);
                        knLogger.debug( methodName, "Exit...notifyResponse(KnMessage) ");

                        if(opsCliPAMNotiURL!= null && !opsCliPAMNotiURL.trim().equalsIgnoreCase(url.trim()))
                        {
                            knLogger.debug( methodName, "Both URLS does not matching, hence  sending notification. ");
                            String routingKey=KnRmqConfig.getInstance().getIdsNotifyServiceBindingKey();
                            if(version!=null && version.matches(KnConstants.VERSION_2X)) {
                            	routingKey=KnRmqConfig.getInstance().getProvNotifyServiceBindingKey();
                            }
                            addTask(action.toString(), url, respString, txnId, extPamAccId,routingKey);
                        }else{
                            knLogger.debug( methodName,  "Both URLS Match.");
                            audit.writeAuditMessage(txnId, NOTIFICATION, KnAuditHelper.STATUS.SUCCESS,getAction(action) + "OPS Operation success for the pam mdn " + extPamAccId);

                        }
                        return true;
                    }
                    else {

                        mdn = customMap.get(KnMediatorConstants.MSISDN).toString();
                        Map<String, List<String>> actionRatePlans = (Map<String, List<String>>)
                                customMap.get(KnMediatorConstants.ACTIONRATEPLANS);
                        com.kodiak.utilities.jaxb.beans.prov.ids.KnProvisioningResponseBean responseBean = new com.kodiak.utilities.jaxb.beans.prov.ids.KnProvisioningResponseBean();

                        audit.writeAuditMessage(txnId, NOTIFICATION, KnAuditHelper.STATUS.REQUEST,
                                actionRatePlans.toString() + " operation requested for the subscriber " + mdn);
                        //Total number of notification transactions initiated
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.WPT_POC_3PP_NUM_NTFY_TXN_INITIATED);
                        String url = (String) customMap.get(KnMediatorConstants.NOTIFICATION_URL);

                        responseBean.setSchemaLocation(KnMediatorConstants.NONAME_SPACE_SCH_LOC);
                        responseBean.setSystem(KnMediatorConstants.SYSTEM);
                        KnHeaderInfoBean header = new KnHeaderInfoBean();
                        header.setTransactionId(customMap.get(KnMediatorConstants.IDS_TRANSACTION_ID).toString());
                        header.setProvCarrier(customMap.get(KnMediatorConstants.PROVISIONING_CARRIER).toString());
                        //get current date and time
                        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ssZ");
                        Date date = new Date();
                        String format = dateFormat.format(date);
                        String newFormat = format.substring(0, format.length() - 2) + ":" + format.substring(format.length() - 2);
                        header.setTimeStamp(newFormat);
                        com.kodiak.utilities.jaxb.beans.prov.ids.KnSenderInfoBean sender = new com.kodiak.utilities.jaxb.beans.prov.ids.KnSenderInfoBean();
                        sender.setLogin(customMap.get(KnMediatorConstants.SENDER_LOGIN).toString());
                        sender.setPassword(customMap.get(KnMediatorConstants.SENDER_PASSWORD).toString());
                        header.setSender(sender);
                        com.kodiak.utilities.jaxb.beans.prov.ids.KnTransactionCodeBean transCode = new com.kodiak.utilities.jaxb.beans.prov.ids.KnTransactionCodeBean();
                        //  mapping error code to major code
                        KnErrorBean errorBean;
                        knLogger.debug(methodName, "pamRespObj.getResponseCode() - " + pamRespObj.getResponseCode());
                        String code = errorMapper.getPamServiceErrorCode(pamRespObj.getResponseCode());
                        knLogger.debug(methodName, "code - " + code);
                        errorBean = errorMapper.getPamMappedError(code);
                        knLogger.debug(methodName, "errorBean - " + errorBean);
                        if (errorBean == null) {
                            errorBean = errorMapper.getPamMappedError(KnMediatorConstants.PAM_ERROR_CODE_INTERNAL_SERVER_ERROR);
                        }

                        transCode.setMajorCode(errorBean.getMajorCode());
                        transCode.setDescription(errorBean.getMajorDescription());

                        header.setTransactionCode(transCode);
                        responseBean.setHeader(header);

                        com.kodiak.utilities.jaxb.beans.prov.ids.KnOrderInfoBean order = new com.kodiak.utilities.jaxb.beans.prov.ids.KnOrderInfoBean();
                        KnAccountInfoBean account = new KnAccountInfoBean();
                        account.setMdn(mdn);
                        order.setAccount(account);
                        com.kodiak.utilities.jaxb.beans.prov.ids.KnProductsInfoBean products = new com.kodiak.utilities.jaxb.beans.prov.ids.KnProductsInfoBean();


                        ArrayList<com.kodiak.utilities.jaxb.beans.prov.ids.KnProductInfoBean> productBeanList = (ArrayList<com.kodiak.utilities.jaxb.beans.prov.ids.KnProductInfoBean>) customMap.get(KnMediatorConstants.PRODUCTTAG);
                        ArrayList<com.kodiak.utilities.jaxb.beans.prov.ids.KnProductInfoBean> prods = new ArrayList<com.kodiak.utilities.jaxb.beans.prov.ids.KnProductInfoBean>();
                        for (com.kodiak.utilities.jaxb.beans.prov.ids.KnProductInfoBean product : productBeanList) {
                            String action = product.getAction();

                            //  if (action.equals(KnMediatorConstants.CHANGEMDN)) {
                            //    action = KnMediatorConstants.MODIFY;
                            //}
                            //If error code is different for action and featureBit,
                            //TODO: for now duplicate error code is there but in future this condition required.
                            //     modifiedAction = (String)customMap.get(KnIDSProvisionConstants.ACTION);
                            if (modifiedAction != null && action.equalsIgnoreCase(modifiedAction)) {
                                product.setMajorCode(errorBean.getMajorCode());
                                product.setDescription(errorBean.getMajorDescription());
                            } else {
                                //this part for featureBit
                                product.setMajorCode(errorBean.getMajorCode());
                                product.setDescription(errorBean.getMajorDescription());
                            }


                            com.kodiak.utilities.jaxb.beans.prov.ids.KnTransactionCodeBean prodtransCode = new com.kodiak.utilities.jaxb.beans.prov.ids.KnTransactionCodeBean();
                            com.kodiak.utilities.jaxb.beans.prov.ids.KnTransactionCodeListBean transCodeList = new com.kodiak.utilities.jaxb.beans.prov.ids.KnTransactionCodeListBean();
                            //====  imei enhancement ..
                            knLogger.debug(methodName, "Enter into other than imei  ");
                            prodtransCode.setMajorCode(errorBean.getMajorCode());
                            prodtransCode.setDescription(errorBean.getMajorDescription());
                            transCodeList.setErrorCode(errorBean.getServiceErrorCode());
                            transCodeList.setErrorMessageText(errorBean.getErrMsg());
                            prodtransCode.setTransactionCodeList(transCodeList);
                            prodtransCode.setMajorCode(errorBean.getMajorCode());
                            prodtransCode.setDescription(errorBean.getMajorDescription());
                            product.setTransactionCode(prodtransCode);

                            prods.add(product);

                            products.setProduct(prods);
                            order.setProducts(products);
                            responseBean.setOrder(order);

                        }

                    String respXML = KnJAXBParser.convertBeanToXml(responseBean, com.kodiak.utilities.jaxb.beans.prov.ids.KnProvisioningResponseBean.class);


                    if (opsCliPAMNotiURL != null && !opsCliPAMNotiURL.trim().equalsIgnoreCase(url.trim())) {
                        knLogger.debug(methodName, "Both URLS Does Not Match.");
                        addTask(actionRatePlans.toString(), url, respXML, txnId, mdn,KnRmqConfig.getInstance().getIdsNotifyServiceBindingKey());
                    }else {
                        knLogger.debug(methodName, "Both URLS Match.");
                        audit.writeAuditMessage(txnId, NOTIFICATION, KnAuditHelper.STATUS.SUCCESS, actionRatePlans.toString() + " OPSCLI Operation success for the pam mdn " + mdn);
                    }

                    if (transCode.getDescription().equalsIgnoreCase(KnMediatorConstants.IDS_IMEI_RESPONSE)) {
                        //Total number of  'success' response notifications received from backend XDMS.
                        KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.WPT_POC_3PP_NUM_SUCC_RESPNTFY_FROM_XDMS);
                    }

                    knLogger.debug(methodName, "remove txnId ::", txnId);
                    KnTxnManagerImpl.getInstance().removeTxId(txnId);
                    }
                    knLogger.debug(methodName, "Exit...notifyResponse(KnMessage) ");
                    return notifyResp;
                }

            }
            return notifyResp;
        } catch (KnXDMServerException ke) {
            knLogger.error(methodName, "KnIDSException  Occured ", ke);
            knLogger.error(methodName, ke);
            //PEGS FOR Total number of  'failure' response notifications received from backend XDMS.
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.WPT_POC_3PP_NUM_FAIL_RESPNTFY_FROM_XDMS);
            audit.writeAuditMessage(txnId, NOTIFICATION, KnAuditHelper.STATUS.FAILURE,
                    " operation failed for the subscriber " + mdn);
            //  getFailureResponse(respObj, ke);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception Occured ", e);
            knLogger.error(methodName, e);
            //PEGS FOR Total number of  'failure' response notifications received from backend XDMS.
            KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.WPT_POC_3PP_NUM_FAIL_RESPNTFY_FROM_XDMS);
            //   getFailureResponse(respObj, e);
        }
        return false;
    }

    private static void addTask(String action, String url, String payLoad, String txnId, String mdn,String notifyRoutingKey) throws KnXDMServerException {
        String methodName = "addTask(String action, String url, String payLoad, String txnId, String mdn)";

        ObjectMapper objectMapper = new ObjectMapper();
        String routingKey = KnRmqConfig.getInstance().getDistSchedulerBindingKey();
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("ACTION", action);
        objectMap.put("URL", url);
        objectMap.put("PAYLOAD", payLoad);
        objectMap.put("TXNID",txnId);
        objectMap.put("MDN",mdn);

        String newTxnId=txnId+System.nanoTime();
        knLogger.debug(methodName, "newTxnId - ", newTxnId);
        KnJobMetaData knJobMetaData = new KnJobMetaData(1,newTxnId,1,objectMap, notifyRoutingKey, KnMediatorConstants.listOfTriggerTimings);
        try {
        String json = objectMapper.writeValueAsString(knJobMetaData);
        KnMessage request = new KnMessage();
        request.setDestRoutingKey(routingKey);
        request.setSync(Boolean.FALSE);
        request.setPayLoad(json);
        request.setDestQueueName(NOTIFICATION);
        msgFw.sendMessage(request);
        } catch (KnMessageException e) {
            knLogger.error(methodName, "KnMessageException occurred while sending message - ", e);

        }catch (Exception e){
            knLogger.error(methodName, "Exception occurred while sending message - ", e);
        }


    }

    public static IXDMResponseDTO getFailureResponse(KnXDMRespDTO respDTO, Exception e) {
        String methodName = "getFailureResponse(KnXDMRespDTO, Exception)";
        if (e instanceof KnXDMServerException) {
            respDTO.setResponseCode(((KnXDMServerException) e).getErrorCode());
            respDTO.setResponseMessage(e.getMessage());

        } else {
            respDTO.setResponseCode(KnMediatorConstants.INTERNAL_SERVER_ERROR);
            respDTO.setResponseMessage(e.getMessage());
        }
        knLogger.debug(methodName, "Response DTO  - ", respDTO);
        return respDTO;
    }

    public static String getAction(Integer action) {
        switch (action) {
            case 1:
                return "CREATE";
            case 2:
                return "MODIFY";
            case 3:
                return "SUSPEND";
            case 4:
                return "RESUME";
            case 5:
                return "CANCEL";
            case 6:
                return "VIEW";
            case 7:
                return "CHANGEMDN";
            case 8:
                return "UPGRADE_RATE_PLAN";
            case 9:
                return "DOWNGRADE_RATE_PLAN";
            default:
                return " INVALID ACTION ";
        }
    }
    
}