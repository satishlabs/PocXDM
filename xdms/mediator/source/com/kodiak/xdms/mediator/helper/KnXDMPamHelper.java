/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * File name:   KnXDMPamHelper.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Sanjeev H V          oct 05, 2014     7.10
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
 */

package com.kodiak.xdms.mediator.helper;

import com.kodiak.common.commdto.common.KnBulkOpInfoDTO;
import com.kodiak.common.commdto.request.KnCorpPAMSubsReqDTO;
import com.kodiak.common.commdto.request.KnXDMPAMAccInfoDTO;
import com.kodiak.common.commdto.request.KnXDMPAMSubsProfInfoDTO;
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.generatealarmutil.KnAlarmConstants;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;
import com.kodiak.utilities.processinvoker.KnProcessInvokerException;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.utilities.kuidgenerator.KnKUIDConstants;
import com.kodiak.utilities.kuidgenerator.KnKUIDGenerator;
import com.kodiak.xdms.bulkfw.KnXDMBulkMediator;
import com.kodiak.xdms.bulkfw.resources.KnBulkDocDiffConstants;
import com.kodiak.xdms.bulkfw.dto.KnBulkOrderDTO;
import com.kodiak.xdms.bulkfw.dto.KnBulkOrderRespDTO;
import com.kodiak.xdms.bulkfw.factory.subsprov.KnSubsProvBulkOrderFactory;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants.MEDIATOR_RESP_STATUS;
import com.kodiak.xdms.mediator.KnMediatorConstants;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnClientTypeConfigDTO;
import com.kodiak.xdms.server.common.dto.common.KnXDMSServiceConfigDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpGroupInfoUtil;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPBulkSubsProvInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPPAMAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCorpProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCreatePAMAccountDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCreateSubsInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPPAMAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPProvDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPUpdatePAMAccountDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPUpdateSubsInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnPAMSubsProfInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvUtil;

import java.util.*;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.common.resources.KnConstants.ENABLED;
import static com.kodiak.xdms.server.common.resources.KnConstants.AUTO_DEVICESHARE_WIFI_CC;
import static com.kodiak.xdms.server.common.resources.KnConstants.DEVICE_SHARING_FEATURE_FLAG;

public class KnXDMPamHelper {

	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMPamHelper.class);

	private static KnXDMPamHelper xdmPamHelper = new KnXDMPamHelper();
	private KnXDMProvMediator provMediator = null;
	private IProvClientIntf provClientIntf;
	private KnSubsProvBulkOrderFactory bulkFactory;
	private KnXDMCommonMediator commonMediator = null;
	private KnXDMCorpMediator corpMediator = null;
	private KnXDMBulkMediator bulkMediator;
	private KnKUIDGenerator kuidGenerator;
    private KnGenInfoUtil genInfoUtil;

	private static final String EMPTY_STRING = "";

	private KnXDMPamHelper() {
		this.provMediator = KnXDMProvMediator.getInstance();
		this.provClientIntf = KnProvClientImpl.getInstance();
		this.bulkFactory = new KnSubsProvBulkOrderFactory();
		this.commonMediator = KnXDMCommonMediator.getInstance();
		this.corpMediator = KnXDMCorpMediator.getInstance();
		this.bulkMediator = KnXDMBulkMediator.getInstance();
		this.kuidGenerator = KnKUIDGenerator.getInstance();
        genInfoUtil = KnGenInfoUtil.getInstance();
	}

	public static KnXDMPamHelper getInstance() {
		return xdmPamHelper;
	}

	public KnXDMPAMRespDTO changePAMServiceAuthStatus(KnMessage message, String operation, KnXDMPAMRespDTO responseDTO,
                                                      boolean isBulkProcessing, KnPersisterTxn persisterTxn) throws KnException {
		 final String methodName = "changePAMServiceAuthStatus(KnMessage, Srting, KnPersisterTxn)";
		// knLogger.entry(methodName);
		 String billingNumber = null;
		 KnXDMPAMAccInfoDTO pamSubStatusInfoDTO = null;

		 pamSubStatusInfoDTO = (KnXDMPAMAccInfoDTO) message.getPayLoad();
		 if(pamSubStatusInfoDTO.getVersion() == null) message.setUpgrade(true);
         knLogger.debug(methodName, "Received DTO for change Service Auth Status - ", pamSubStatusInfoDTO, ", isBulkProcessing-", isBulkProcessing);

         billingNumber = pamSubStatusInfoDTO.getBillingNumber();
         //populating the prov libraryDTO
         KnIPPAMAccInfoDTO pamAccountInfoDTO = this.provMediator.populatePAMAccountDTO(pamSubStatusInfoDTO);

         KnOPPAMAccInfoDTO pamAccountInfo = this.provClientIntf.getPAMAccountInfo(pamAccountInfoDTO.getBillingMdn(), persisterTxn);

         if (KnMediatorConstants.PAM_ACCOUNT_STATE.DELETE_IN_PROGRESS.value() == pamAccountInfo.getPamAccState()) {
             knLogger.error(methodName, "pam account is in delete-in-progress !!! ");
             throw new KnXDMServerException(KnMediatorConstants.PAMACCOUNT_DELETE_IN_PROGRESS, "pam account is in delete-in-progress ");
         }

         KnOPPAMAccInfoDTO pamSubsProfInfoDTO = this.provClientIntf.getPAMSubsProfile(pamAccountInfoDTO, persisterTxn);

         Integer subsClientType = pamSubsProfInfoDTO.getProfileDetails().getClient_Type();
         if (pamSubStatusInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                Map<String, Object> customMap = null;
                customMap = pamSubStatusInfoDTO.getProfileDetails().getCustomParamMap();
                String action = String.valueOf(customMap.get(KnMediatorConstants.ACTION));

                if (!action.equals("Cancel")) {
                    //retriving from db/cache client type config check is enable or disable
                	//LMR client type changes
                    if (subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) || subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value())
                    		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) || subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value())
                    		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value())
                    		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value())) {

                        knLogger.info(methodName, "Checking client type config table for client :", subsClientType, " ACTION :", action);
                        KnClientTypeConfigDTO clientTypeConfigDTO = this.genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
                        if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                            knLogger.error(methodName, "Client type is disabled");
                            throw new KnXDMServerException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
                        }
                    }
                }
            }else{
                //retriving from db/cache client type config check is enable or disable
            	//LMR client type changes
            	if (subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) || subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value())
                		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) || subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value())
                		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value())
            			|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value())) {

            		KnClientTypeConfigDTO clientTypeConfigDTO = this.genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
                    if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                        knLogger.error(methodName, "Client type is disabled");
                        throw new KnXDMServerException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
                    }
                }
            }

         //populate the Subscriber Prov library DTO
         pamAccountInfoDTO.setProfileDetails(pamSubsProfInfoDTO.getProfileDetails());
         pamSubStatusInfoDTO.getProfileDetails().setPamAccId(pamSubsProfInfoDTO.getPamAccId());
         pamSubStatusInfoDTO.setPamAccId(pamSubsProfInfoDTO.getPamAccId());
         pamSubStatusInfoDTO.getProfileDetails().setHierarchyType(pamSubStatusInfoDTO.getHierarchyType());

         if (isBulkProcessing) {
        	 knLogger.info(methodName, "its a bulk operation..inserting into bulk table");
        	 // call bulk FW
        	 KnBulkOrderDTO bulkOrderDTO = new KnBulkOrderDTO();
        	 knLogger.debug(methodName, "bulk calling pamAccInfoDTO :", pamSubStatusInfoDTO);
        	 message.setPayLoad(pamSubStatusInfoDTO);
        	 bulkOrderDTO.setBulkOrderReqObj(message);
        	 bulkOrderDTO.setBulkOrderObj(message);
        	 bulkOrderDTO.setCorpId(pamSubsProfInfoDTO.getPamAccId());
        	 bulkOrderDTO.setOperationType(KnBulkFwConstants.getBulkOperationType(pamSubStatusInfoDTO.getProfileDetails().getServiceAuthStatus()));

        	 //calling bulk processor
        	 KnBulkOrderRespDTO bulkRespDTO = this.bulkFactory.performBulkOp(bulkOrderDTO);
        	 knLogger.info(methodName, "submitted bulk order for change auth status :  ", bulkRespDTO);

        	 if (KnBulkDocDiffConstants.REQUEST_STATUS.FAILURE.equals(bulkRespDTO.getReqStatus())) {
        		 knLogger.error(methodName, "failed to submit bulk order  ");
        		 throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, bulkRespDTO.getResponseMsg());
        	 }

         } else {
        	knLogger.info(methodName, "its a non bulk operation..processing synchronously");

        	List<String> pamMdns = provClientIntf.retrievePAMAccountMDNs(pamSubsProfInfoDTO.getPamAccId(), persisterTxn);
        	pamSubStatusInfoDTO.getProfileDetails().setMdns(pamMdns);
        	responseDTO = (KnXDMPAMRespDTO) this.bulkMediator.changeServiceAuthStatus(pamSubStatusInfoDTO, persisterTxn);

        	if (responseDTO != null && responseDTO.getResponseStatus() == MEDIATOR_RESP_STATUS.SUCCESS.value()) {
        		// update pseudo numbers as used
        		KnXDMPamResponseHandler responseHandler = KnXDMPamResponseHandler.getInstance();
        		responseHandler.changePAMServiceAuthStatusResponse(responseDTO, pamSubStatusInfoDTO, message.getCorrelationId(), persisterTxn);
        	 } else {
        		 this.setNotifyParams(pamSubStatusInfoDTO.getTransactionId(), billingNumber, pamSubStatusInfoDTO.getOpMap(), responseDTO);
        		 throw new KnXDMServerException(responseDTO.getResponseCode(), responseDTO.getResponseMessage());
        	 }
         }

         this.setNotifyParams(pamSubStatusInfoDTO.getTransactionId(), billingNumber, pamSubStatusInfoDTO.getOpMap(), responseDTO);

      //   knLogger.exit(methodName);
		 return responseDTO;
	 }


	public KnXDMPAMRespDTO createLicensePack(KnMessage message, String operation, KnXDMPAMRespDTO responseDTO,
                                                                                boolean isBulkProcessing, KnPersisterTxn persisterTxn) throws KnException {
		final String methodName = "createLicensePack(KnMessage, Srting, KnPersisterTxn)";
	//	knLogger.entry(methodName);
		KnXDMPAMAccInfoDTO pamAccInfoDTO;
        KnXDMPAMAccInfoDTO pamAccInfoDTO2 = new KnXDMPAMAccInfoDTO();
        KnBulkOrderRespDTO respDTO;
        KnXDMPAMSubsProfInfoDTO knXDMPAMSubsProfInfoDTO= new KnXDMPAMSubsProfInfoDTO();
        String billingNumber;

        pamAccInfoDTO = (KnXDMPAMAccInfoDTO) message.getPayLoad();
        String version =pamAccInfoDTO.getVersion();
	    knLogger.warn( methodName,"web card version ", version);
        knLogger.debug(methodName, "received DTO for create PAM Account - ", pamAccInfoDTO, ", isBulkProcessing-", isBulkProcessing);
        if(version == null) message.setUpgrade(true);
        // check only add pkg code map for create Licence Pack
        Map<String, Integer> addPkgIds=new HashMap<String, Integer>();
        if(pamAccInfoDTO.getProfileDetails().getPkgIdMap()!=null && pamAccInfoDTO.getProfileDetails().getPkgIdMap().get(KnConstants.ADD_ACTION)!=null)
        {
        	addPkgIds=pamAccInfoDTO.getProfileDetails().getPkgIdMap().get(KnConstants.ADD_ACTION);
        }


        //retriving from db client type config check is enable or disable
        int subsClientType = pamAccInfoDTO.getProfileDetails().getClient_Type();
        //LMR client type changes
        if (subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value())
        		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value())
        		|| subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value())
        		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value())) {

        	 knLogger.debug(methodName, "Checking client type config table ",subsClientType );
             KnClientTypeConfigDTO clientTypeConfigDTO = this.genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
            if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                knLogger.error(methodName, "Client type is disabled");
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
            }
        }

        billingNumber = pamAccInfoDTO.getBillingNumber();
        if(pamAccInfoDTO.getProfileDetails().getSubsDefPttRadio() != com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED){
        	String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
			KnXDMSServiceConfigDTO knXDMSServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttId, persisterTxn);
			if(knXDMSServiceConfigDTO.getSubsDefPttRadio() == com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED){
				pamAccInfoDTO.getProfileDetails().setSubsDefPttRadio(com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED);
			}
			else{
				KnOPCorpProfileInfoDTO knOPCorpProfileInfoDTO = retrieveCorporateProfile(pamAccInfoDTO.getProfileDetails().getExtCorpId(), persisterTxn);
				if(knOPCorpProfileInfoDTO.getSubsDefPttRadio() == com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED){
					pamAccInfoDTO.getProfileDetails().setSubsDefPttRadio(com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED);
				}
			}
        }
        if(pamAccInfoDTO.getProfileDetails().getSubsDefPttRadio() == com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED ){
        	switch(pamAccInfoDTO.getProfileDetails().getClient_Type()){

        	case 5:
				pamAccInfoDTO.getProfileDetails().setClient_Type(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value());
				subsClientType= KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value();
				break;
			case 10:
	  		    pamAccInfoDTO.getProfileDetails().setClient_Type(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value());
			    subsClientType= KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value();
				break;
			default:
				break;
			}
        }
        //populate the Subscriber Prov library DTO
        KnIPPAMAccInfoDTO pamAccountInfoDTO = this.provMediator.populatePAMAccountDTO(pamAccInfoDTO);
        knLogger.debug(methodName, "creating PAM account", pamAccountInfoDTO);
        //calling the PAM Account creation Lib
        KnOPCreatePAMAccountDTO respPAMCreateDTO = this.provClientIntf.createPAMAccount(pamAccountInfoDTO, persisterTxn);

        int pamAccId = respPAMCreateDTO.getPamAccId();
        pamAccInfoDTO.setPamAccId(pamAccId);
        knLogger.debug(methodName, "pamAccId :", pamAccId);
        pamAccInfoDTO2.setPamAccId(pamAccId);

        if (pamAccInfoDTO.getProfileDetails().getClient_Type() == 0) {
            pamAccInfoDTO.getProfileDetails().setClient_Type(KnConstants.SUBSCRIBERS_CLIENT_TYPE.WIFIONLY.value());
        }

        if ((pamAccInfoDTO.getProfileDetails().getPubSubsType() == -1 && pamAccInfoDTO.getProfileDetails().getCorpSubsType() == -1) ||
                pamAccInfoDTO.getProfileDetails().getPubSubsType() == 0 && pamAccInfoDTO.getProfileDetails().getCorpSubsType() == 0) {
            pamAccInfoDTO.getProfileDetails().setPubSubsType(KnConstants.PUBLIC_SUBSCRIPTION_TYPE.PUBLIC.value());
            pamAccInfoDTO.getProfileDetails().setCorpSubsType(KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value());
        }

        //Invoking the Custom Invoker for Custom data:
        Map<String, Object> customReqMap = null;
        if (pamAccInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
            knLogger.debug(methodName, "CUSTOM_ flag is ON ");
            if (pamAccountInfoDTO.getProfileDetails() != null && pamAccountInfoDTO.getProfileDetails().getCustomParamMap() != null) {
                customReqMap = pamAccountInfoDTO.getProfileDetails().getCustomParamMap();
            } else {
                customReqMap = new HashMap<String, Object>();
                customReqMap.put(KnMediatorConstants.ACCOUNT_TYPE_INDICATOR, KnConstants.ACCOUNT_TYPE.INDIVIDUAL.value());
                customReqMap.put(KnMediatorConstants.RATE_PLAN, String.valueOf(KnConstants.SUBSCRIBERS_CLIENT_TYPE.WIFIONLY.value()));

            }
        }

        //if number of subscribers creation is 0 , then don't create subscribers
        if (pamAccountInfoDTO.getSubscriberCount() > 0) {
            KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO = this.provMediator.populateBulkSubsProvInfoDTO(pamAccInfoDTO);
            KnOPCreateSubsInfoDTO respValidateDTO = this.provClientIntf.validateCreateSubscriber(bulkSubsProvInfoDTO, pamAccountInfoDTO.getSubscriberCount(), persisterTxn);
            knLogger.debug(methodName, "validate profile:  ", respValidateDTO);

            pamAccInfoDTO.getProfileDetails().setPamAccId(pamAccId);
            pamAccInfoDTO.getProfileDetails().setAutoPair(respValidateDTO.getCorpAutoPairing());
            if (customReqMap != null) {
                customReqMap.remove(com.kodiak.common.resources.KnConstants.PERSISTER_TXN);
                pamAccInfoDTO.getProfileDetails().setCustomParamMap(customReqMap);
                knXDMPAMSubsProfInfoDTO.setCustomParamMap(customReqMap);
            }
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String deviceSharingFlag = microServicesParamNameValueMap.get(DEVICE_SHARING_FEATURE_FLAG);
            knLogger.info(methodName, "deviceSharingFlag",  deviceSharingFlag);
            int deviceSharewifiFlag=Integer.parseInt(microServicesParamNameValueMap.get(AUTO_DEVICESHARE_WIFI_CC));
            knLogger.info(methodName, "deviceSharewifiFlag-->",  deviceSharewifiFlag);
            if ((subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
					|| subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()
					|| subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
					|| subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value() ) && Integer.parseInt(deviceSharingFlag) == ENABLED
                    && deviceSharewifiFlag == ENABLED){
            	knLogger.debug(methodName, "license type for wifi , crosscarrier and its ptt ");
            	pamAccInfoDTO.getProfileDetails().setLicenseType(KnConstants.USER_LICENSE_TYPE);
			}
          //required in Bulk FW. Bulk FW always refer PAM Subs details
            pamAccInfoDTO.getProfileDetails().setHierarchyType(pamAccInfoDTO.getHierarchyType());

            //checking bulk and non bulk operations
            if (isBulkProcessing) {
            	knLogger.info(methodName, "its a bulk operation..inserting into bulk table");
            	KnBulkOrderDTO bulkOrderDTO = new KnBulkOrderDTO();
            	knLogger.debug(methodName, "bulk calling pamAccInfoDTO :", pamAccInfoDTO);
            	message.setPayLoad(pamAccInfoDTO);
            	bulkOrderDTO.setBulkOrderReqObj(message);
            	bulkOrderDTO.setBulkOrderObj(message);
            	bulkOrderDTO.setCorpId(pamAccId);
            	bulkOrderDTO.setOperationType(KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_CREATE.value());
            	//calling bulk processor
            	knLogger.debug(methodName, "inserting into bulk order table");
            	respDTO = this.bulkFactory.performBulkOp(bulkOrderDTO);
            	knLogger.info(methodName, "submitted bulk order for create Subscribers :  ", respDTO);

            	if (KnBulkDocDiffConstants.REQUEST_STATUS.FAILURE.equals(respDTO.getReqStatus())) {
            		knLogger.error(methodName, "failed to submit bulk order  ");
            		throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, respDTO.getResponseMsg());
            	}

            } else {
            	knLogger.info(methodName, "its a non bulk operation..processing synchronously");

            	 //allocate pseudo mdns for creation
            	 List<String> mdnList = kuidGenerator.getKodiakUserIDs(KnKUIDConstants.COUNTRYCODE,pamAccInfoDTO.getTotalNoOfLines());
            	 pamAccInfoDTO.getProfileDetails().setMdns(mdnList);
                knXDMPAMSubsProfInfoDTO.setMdns(mdnList);
                pamAccInfoDTO2.setProfileDetails(knXDMPAMSubsProfInfoDTO);
            	 responseDTO =  (KnXDMPAMRespDTO) bulkMediator.createSubscribers(pamAccInfoDTO, Boolean.TRUE, persisterTxn);
                 knLogger.debug(methodName, "KnXDMPAMRespDTO responseDTO :", responseDTO);
                responseDTO.setKnXDMPAMAccInfoDTO(pamAccInfoDTO2);
            	 if (responseDTO != null && responseDTO.getResponseStatus() == MEDIATOR_RESP_STATUS.SUCCESS.value()) {
            		 // update pseudo numbers as used
            		 KnXDMPamResponseHandler responseHandler = KnXDMPamResponseHandler.getInstance();
            		 responseHandler.createLicensePackResponse(responseDTO, pamAccInfoDTO, message.getCorrelationId(), persisterTxn);

            	 } else {
            		 knLogger.error(methodName, "PAM subscriber creation failed");
            		 //de allocate pseudo mdns incase of creation failure

            		 if (responseDTO != null && responseDTO.getResponseCode().contains("SP12051")) {
                         knLogger.info(methodName, "Mismatch observed in Pseudo Pool and PocSubsInfo for mdns  ", KnGDPRTemplate.mdnList(mdnList));
                         KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.ALARM_PSEUDO_NUM_POOL_DB_INCONSISTENCY_OBSERVED, KnAlarmConstants.SEVERITY_MAJOR,
                        		 KnKUIDConstants.ALARM_MOCLASSTYPE, "KnXDMPamHelper");
                     }
            		 this.setNotifyParams(pamAccInfoDTO.getTransactionId(), billingNumber, pamAccInfoDTO.getOpMap(), responseDTO);
            		 throw new KnXDMServerException(responseDTO.getResponseCode(), responseDTO.getResponseMessage());
            	 }
            }

        } else {
            knLogger.debug(methodName, "subscriber count is 0 hence skipping subscribers creation ");

        }
        responseDTO.setKnXDMPAMAccInfoDTO(pamAccInfoDTO2);
        if (pamAccountInfoDTO.getSubscriberCount() <= 0) {
            responseDTO.setResponseCode(KnMediatorConstants.SUCCESS_CODE);
            responseDTO.setResponseMessage("SUCCESSFULLY executed the operation");
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            responseDTO.setPamStatus(KnMediatorConstants.PAM_ACCOUNT_STATE.ACTIVE.value());
            responseDTO.setKnXDMPAMAccInfoDTO(pamAccInfoDTO2);
            this.commonMediator.sendResponse(responseDTO, message, null,version);

        }
        this.setNotifyParams(pamAccInfoDTO.getTransactionId(), billingNumber, pamAccInfoDTO.getOpMap(), responseDTO);

        knLogger.exit(methodName, responseDTO);
		return responseDTO;
	}

	public KnXDMPAMRespDTO deleteLicensePack(KnMessage message, String operation, KnXDMPAMRespDTO responseDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
		final String methodName = "deleteLicensePack(KnMessage, String, KnPersisterTxn)";
		//knLogger.entry(methodName);
		KnXDMPAMAccInfoDTO pamAccInfoDTO;
		String billingNumber;

		pamAccInfoDTO = (KnXDMPAMAccInfoDTO) message.getPayLoad();
        if(pamAccInfoDTO.getVersion() == null) message.setUpgrade(true);
	    knLogger.debug(methodName, "Received DTO for Delete PAM account - ", pamAccInfoDTO);

	    billingNumber = pamAccInfoDTO.getBillingNumber();
        this.setNotifyParams(pamAccInfoDTO.getTransactionId(), billingNumber, pamAccInfoDTO.getOpMap(), responseDTO);

        KnIPPAMAccInfoDTO pamAccountInfoDTO = this.provMediator.populatePAMAccountDTO(pamAccInfoDTO);

        KnOPPAMAccInfoDTO pamAccountInfo = this.provClientIntf.getPAMAccountInfo(pamAccountInfoDTO.getExtPamAccId(), persisterTxn);
        if (pamAccountInfo.getPamAccState() == KnMediatorConstants.PAM_ACCOUNT_STATE.DELETE_IN_PROGRESS.value()) {
            knLogger.error(methodName, "pam account is in delete-in-progress !!! ");
            throw new KnXDMServerException(KnMediatorConstants.PAMACCOUNT_DELETE_IN_PROGRESS, "pam account is in delete-in-progress ");
        }

        KnOPPAMAccInfoDTO pamSubsProfInfoDTO = this.provClientIntf.getPAMSubsProfile(pamAccountInfoDTO, persisterTxn);
        knLogger.debug(methodName, "got subscriber proifile - ", pamSubsProfInfoDTO);
        //populate the Subscriber Prov library DTO
        pamAccountInfoDTO.setProfileDetails(pamSubsProfInfoDTO.getProfileDetails());

        pamAccInfoDTO.setPamAccId(pamSubsProfInfoDTO.getProfileDetails().getPamAccId());
        pamAccInfoDTO.getProfileDetails().setPamAccId(pamSubsProfInfoDTO.getProfileDetails().getPamAccId());
        pamAccInfoDTO.getProfileDetails().setProfileId(pamSubsProfInfoDTO.getProfileDetails().getProfileId());
        pamAccInfoDTO.getProfileDetails().setExtCorpId(pamSubsProfInfoDTO.getProfileDetails().getExtCorpId());

        //updating pam account state to delete in progress
        pamAccountInfoDTO.setPamAccState(KnMediatorConstants.PAM_ACCOUNT_STATE.DELETE_IN_PROGRESS.value());
        KnOPUpdatePAMAccountDTO respPAMUpdateDTO = this.provClientIntf.updatePAMAccState(pamAccountInfoDTO, persisterTxn);
        knLogger.debug(methodName, " - updated PAM account state :", respPAMUpdateDTO);

        //required in Bulk FW. Bulk FW always refer PAM Subs details
        pamAccInfoDTO.getProfileDetails().setHierarchyType(pamAccInfoDTO.getHierarchyType());

        //call bulk FW
        KnBulkOrderDTO bulkOrderDTO = new KnBulkOrderDTO();
        knLogger.debug(methodName, "bulk calling pamAccInfoDTO :", pamAccInfoDTO);
        message.setPayLoad(pamAccInfoDTO);
        bulkOrderDTO.setBulkOrderReqObj(message);
        bulkOrderDTO.setBulkOrderObj(message);
        bulkOrderDTO.setCorpId(pamSubsProfInfoDTO.getPamAccId());
        bulkOrderDTO.setOperationType(KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DELETE.value());
        knLogger.debug(methodName, "inserting into bulk order table");
        //calling bulk processor
        KnBulkOrderRespDTO bulkRespDTO = this.bulkFactory.performBulkOp(bulkOrderDTO);
        knLogger.info(methodName, "submitted bulk order for delete Subscribers :  ", bulkRespDTO);

        if (KnBulkDocDiffConstants.REQUEST_STATUS.FAILURE.equals(bulkRespDTO.getReqStatus())) {
            knLogger.error(methodName, "failed to submit bulk order  ");
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, bulkRespDTO.getResponseMsg());
        }

		knLogger.exit(methodName,responseDTO);
		return responseDTO;
	}

	public KnXDMPAMRespDTO downgradeLicensePack(KnMessage message, String operation, KnXDMPAMRespDTO responseDTO, KnPersisterTxn persisterTxn) throws Exception {
		final String methodName = "downgradeLicensePack(KnMessage, Srting, KnPersisterTxn)";
		//knLogger.entry(methodName);

		KnXDMPAMAccInfoDTO pamAccInfoDTO;
		String billingNumber = null;

    	pamAccInfoDTO = (KnXDMPAMAccInfoDTO) message.getPayLoad();
        knLogger.debug(methodName, "Received DTO for change Service Auth Status - ", pamAccInfoDTO);
        if(pamAccInfoDTO.getVersion() == null) message.setUpgrade(true);

        billingNumber = pamAccInfoDTO.getBillingNumber();
        this.setNotifyParams(pamAccInfoDTO.getTransactionId(), billingNumber, pamAccInfoDTO.getOpMap(), responseDTO);

        String billingMdn = pamAccInfoDTO.getBillingNumber();
        int maxSub = pamAccInfoDTO.getTotalNoOfLines();
        knLogger.debug(methodName, "pamAccInfoDTO.getProfileDetails():", pamAccInfoDTO.getProfileDetails());

        KnOPPAMAccInfoDTO serverDTO = this.provClientIntf.getPAMAccountInfo(billingMdn, persisterTxn);
        KnIPPAMAccInfoDTO pamAccountInfoDTO = this.provMediator.populatePAMAccountDTO(pamAccInfoDTO);
        KnOPPAMAccInfoDTO pamSubsProfInfoDTO = this.provClientIntf.getPAMSubsProfile(pamAccountInfoDTO, persisterTxn);
        knLogger.debug(methodName, "got subscriber proifile - ", pamSubsProfInfoDTO);

        //retriving from db client type config check is enable or disable
        Integer subsClientType = pamSubsProfInfoDTO.getProfileDetails().getClient_Type();
        //LMR client type changes
        if (subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value())
        		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value())
        		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value())
        		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value())) {

        	knLogger.debug(methodName, "Checking client type config table ", subsClientType);
        	KnClientTypeConfigDTO clientTypeConfigDTO = this.genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
        	if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
        		knLogger.error(methodName, "Client type is disabled");
        		throw new KnXDMServerException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
        	}
        }

        serverDTO.setProfileDetails(pamSubsProfInfoDTO.getProfileDetails());
        int dbMaxSub = serverDTO.getTotalNoOfLines();
        int subCount = dbMaxSub - maxSub;
        knLogger.debug(methodName, "dbMaxSub:", dbMaxSub, "maxSub:", maxSub, "subCount:", subCount);

        if (subCount <= 0 || maxSub <=0 ) {
            knLogger.error(methodName, "Invalid Rate plan Request is recieved, subCount:", subCount, " or MaxSub:", maxSub);
            throw new KnXDMServerException(KnMediatorConstants.INVALID_LICENSE_LIMIT, "Invalid License limit Request is recieved");
        }

        serverDTO.setTotalNoOfLines(maxSub);
        if (serverDTO.getPamAccState() == KnMediatorConstants.PAM_ACCOUNT_STATE.DELETE_IN_PROGRESS.value()) {
            knLogger.error(methodName, "pam account is in delete-in-progress !!! ");
            throw new KnXDMServerException(KnMediatorConstants.PAMACCOUNT_DELETE_IN_PROGRESS, "pam account is in delete-in-progress ");
        }

        // this is required to get and set the old PAM subscriber profile in case of SOAP call..NOT required in Custom call..
        if (pamAccInfoDTO.getHierarchyType() == HIERARCHY_TYPE.NON_HIERARCHY) {
        	knLogger.debug(methodName, "CUSTOM_ flag is OFF ");
        	pamAccInfoDTO = this.provMediator.populatePAMAccountDTO(pamAccInfoDTO, serverDTO);
        }
        // end custom logic

      //populate the Subscriber Prov library DTO
        pamAccInfoDTO.setPamAccId(pamSubsProfInfoDTO.getPamAccId());
        pamAccInfoDTO.getProfileDetails().setPamAccId(pamSubsProfInfoDTO.getPamAccId());
        pamAccInfoDTO.getProfileDetails().setProfileId(pamSubsProfInfoDTO.getProfileDetails().getProfileId());
        pamAccInfoDTO.setSubsCount(subCount);

      //required in Bulk FW. Bulk FW always refer PAM Subs details
        pamAccInfoDTO.getProfileDetails().setHierarchyType(pamAccInfoDTO.getHierarchyType());

        List<String> mdnList = retrieveUnusedSubsList(pamAccInfoDTO, persisterTxn);

        knLogger.debug(methodName, "free mdnlist recieved are ", mdnList.size());
        if (mdnList.size() > 0) {
            KnBulkOrderDTO bulkOrderDTO = new KnBulkOrderDTO();
            pamAccInfoDTO.setBillingNumber(billingMdn);
            knLogger.debug(methodName, "bulk calling pamAccInfoDTO :", pamAccInfoDTO);
            message.setPayLoad(pamAccInfoDTO);
            bulkOrderDTO.setBulkOrderReqObj(message);
            bulkOrderDTO.setBulkOrderObj(message);
            bulkOrderDTO.setCorpId(pamAccInfoDTO.getPamAccId());
            bulkOrderDTO.setOperationType(KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DOWNGRADE.value());
            knLogger.debug(methodName, "inserting into bulk order table");
            KnBulkOrderRespDTO respDTO = this.bulkFactory.performBulkOp(bulkOrderDTO);
            knLogger.info(methodName, "submitted bulk order for downgrade Subscribers in PAM :  ", respDTO);
            if (KnBulkDocDiffConstants.REQUEST_STATUS.FAILURE.equals(respDTO.getReqStatus())) {
                knLogger.error(methodName, "failed to submit bulk order  ");
                throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, respDTO.getResponseMsg());
            }
        }
        knLogger.exit(methodName, responseDTO);
		return responseDTO;
	}

	public KnXDMPAMRespDTO updateLicensePack(KnMessage message, String operation, KnXDMPAMRespDTO responseDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnDAOException, KnProcessInvokerException {
		final String methodName = "updateLicensePack(KnMessage, Srting, KnPersisterTxn)";
		//knLogger.entry(methodName);
		String billingNumber = null;
        KnXDMPAMSubsProfInfoDTO knXDMPAMSubsProfInfoDTO= new KnXDMPAMSubsProfInfoDTO();
		KnXDMPAMAccInfoDTO pamAccInfoDTO=null;
        KnXDMPAMAccInfoDTO pamAccInfoDTO2 = new KnXDMPAMAccInfoDTO();

    	pamAccInfoDTO = (KnXDMPAMAccInfoDTO) message.getPayLoad();
        knLogger.debug(methodName, "Received DTO for update PAM account - ", pamAccInfoDTO);
        if(pamAccInfoDTO.getVersion() == null) message.setUpgrade(true);

        billingNumber = pamAccInfoDTO.getBillingNumber();
        this.setNotifyParams(pamAccInfoDTO.getTransactionId(), billingNumber, pamAccInfoDTO.getOpMap(), responseDTO);

        //populate the Subscriber Prov library DTO
        KnIPPAMAccInfoDTO pamAccountInfoDTO = this.provMediator.populatePAMAccountDTO(pamAccInfoDTO);

        IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
        //Retrieving internal Pam Account Id
        KnOPPAMAccInfoDTO pamAccountInfo = this.provClientIntf.getPAMAccountInfo(pamAccInfoDTO.getBillingNumber(), persisterTxn);

        //retriving pam subscriber profile
        KnOPPAMAccInfoDTO pamSubsProfInfoDTO = this.provClientIntf.getPAMSubsProfile(pamAccountInfoDTO, persisterTxn);
        knLogger.debug(methodName, "got subscriber proifile - ", pamSubsProfInfoDTO);
        knXDMPAMSubsProfInfoDTO.setPamAccId(pamSubsProfInfoDTO.getProfileDetails().getPamAccId());
        knXDMPAMSubsProfInfoDTO.setSubscriberFS2(pamSubsProfInfoDTO.getProfileDetails().getSubscriberFS2());
        knXDMPAMSubsProfInfoDTO.setCorpID(pamSubsProfInfoDTO.getProfileDetails().getCorpID());
        knXDMPAMSubsProfInfoDTO.setCorpSubsType(pamSubsProfInfoDTO.getProfileDetails().getCorpSubsType());
        pamAccInfoDTO2.setProfileDetails(knXDMPAMSubsProfInfoDTO);
        responseDTO.setKnXDMPAMAccInfoDTO(pamAccInfoDTO2);
        knLogger.debug(methodName, "UpdateLicensepack pamAccInfoDTO2: "+pamAccInfoDTO2);

        if (KnMediatorConstants.PAM_ACCOUNT_STATE.DELETE_IN_PROGRESS.value() == pamAccountInfo.getPamAccState()) {
			knLogger.error(methodName, "pam account is in delete-in-progress !!! ");
			throw new KnXDMServerException(KnMediatorConstants.PAMACCOUNT_DELETE_IN_PROGRESS, "pam account is in delete-in-progress ");
		}

        //retriving from db client type config check is enable or disable
        //LMR client type changes
        Integer subsClientType = pamSubsProfInfoDTO.getProfileDetails().getClient_Type();
        if (subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value())
        		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value())
        		|| subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value())
        		|| subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value())) {

            knLogger.debug(methodName, "Checking client type config table ", subsClientType);
            KnClientTypeConfigDTO clientTypeConfigDTO = this.genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
            if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                knLogger.error(methodName, "Client type is disabled");
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
            }
        }

        int pamAccId = pamAccountInfo.getPamAccId();
        knLogger.debug(methodName, "after retrieving pamAccId:", pamAccId);

        //Setting the retrieved pamAccid to DTO
        pamAccountInfoDTO.setPamAccId(pamAccId);
        pamAccountInfoDTO.getProfileDetails().setPamAccId(pamAccId);
        pamAccInfoDTO.setPamAccId(pamAccId);
        pamAccInfoDTO.getProfileDetails().setPamAccId(pamAccId);

        KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO = this.provMediator.populateBulkSubsProvInfoDTO(pamAccInfoDTO);
        KnOPUpdateSubsInfoDTO respValidateDTO = this.provClientIntf.validateUpdateSubscriber(bulkSubsProvInfoDTO, persisterTxn);
        knLogger.debug(methodName, "validate profile response:  ", respValidateDTO);

        knLogger.debug(methodName, "validating update pamaccount", pamAccountInfoDTO);
        KnOPUpdatePAMAccountDTO updatePAMAccountDTO =  this.provClientIntf.validateUpdatePamAccount(pamAccountInfoDTO, persisterTxn);
        knLogger.debug(methodName, " KnOPUpdatePAMAccountDTO ", updatePAMAccountDTO);

        int corpId = provXDMServerDAO.retrieveCorporationId(pamAccInfoDTO.getProfileDetails().getExtCorpId(), persisterTxn);
        provXDMServerDAO.updateCorpName(pamAccInfoDTO.getProfileDetails().getExtCorpId(), pamAccInfoDTO.getProfileDetails().getCorpName(), persisterTxn);

        if(updatePAMAccountDTO.isSubsFSUpdated()){
            List<String> mdnList = provClientIntf.retrievePAMAccountMDNs(pamSubsProfInfoDTO.getPamAccId(), persisterTxn);
            knLogger.debug(methodName, " mdnList ", KnGDPRTemplate.mdnList(mdnList));

            if(mdnList != null && !mdnList.isEmpty() ){
                pamAccInfoDTO.getProfileDetails().setMdns(mdnList);

                knLogger.debug(methodName,"pamAccInfoDTO :",pamAccInfoDTO);
                IXDMResponseDTO ixdmResponseDTO=this.bulkMediator.updateBulkSubsFSAndPkgIds(pamAccInfoDTO,persisterTxn);
                knLogger.debug(methodName,"IXDMResponseDTO ixdmResponseDTO :",ixdmResponseDTO);
               // pamAccountInfoDTO.getProfileDetails().setProvFSMap(subsFS.);
                if(ixdmResponseDTO.getResponseStatus()== KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                    knLogger.debug(methodName, "failed to update Feature bits ");
                    throw new KnProvBOException(ixdmResponseDTO.getResponseCode(), ixdmResponseDTO.getResponseMessage());
                } else {
                    knLogger.debug(methodName, "successfully update PAM DEtails");

                }
            }
        }
        knLogger.info(methodName, "updating PAM profile..");
        this.provClientIntf.updatePAMSubsProfile(pamAccountInfoDTO, persisterTxn);

        knLogger.debug(methodName, "updating PAM Acc name..");
        this.provClientIntf.updatePAMAccName(pamAccountInfoDTO, persisterTxn);

      //Invoking the Custom Invoker for Custom data:
        Map<String, Object> customMap = pamAccInfoDTO.getProfileDetails().getCustomParamMap();
        if (pamAccInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
            knLogger.debug(methodName, "customMap ::", customMap);
            customMap.put(KnProvConstants.CORP_ID, String.valueOf(corpId));
            customMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_PROV_UPDATE_PAM_DETAILS_OP);
            customMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
            //Should not support RatePlanChanges
            customMap.put(KnProvConstants.RATE_PLAN, null);
            pamAccInfoDTO.getProfileDetails().setCustomParamMap(customMap);

            knLogger.debug(methodName, "CUSTOM_PROV_UPDATE_PAM_DETAILS_OP ");

            Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, pamAccInfoDTO);
            knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);
            if (customResp instanceof KnXDMRespDTO) {
                KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customResp;

                if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                    knLogger.debug(methodName, "failed to update PAM DEtails ");
                    throw new KnProvBOException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());

                } else {
                    knLogger.debug(methodName, "successfully update PAM DEtails");

                }

            }
        }
        // end custom logic
        this.provClientIntf.updatePAMEtag(pamAccId, persisterTxn);
        knLogger.exit(methodName,"KnXDMPAMRespDTO responseDTO ", responseDTO);
		return responseDTO;
	}


	public KnXDMPAMRespDTO changeBillingNumber(KnMessage message, String operation, KnXDMPAMRespDTO responseDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnDAOException {
		final String methodName = "changeBillingNumber(KnMessage, Srting, KnPersisterTxn)";
		//knLogger.entry(methodName);
		KnXDMPAMAccInfoDTO pamAccInfoDTO;
        String oldBillingNumber;

        pamAccInfoDTO = (KnXDMPAMAccInfoDTO) message.getPayLoad();
        knLogger.debug(methodName, "Received DTO for change Billing MDN - ", pamAccInfoDTO);
        if(pamAccInfoDTO.getVersion() == null) message.setUpgrade(true);

        oldBillingNumber = pamAccInfoDTO.getOldBillingNumber();
        this.setNotifyParams(pamAccInfoDTO.getTransactionId(), oldBillingNumber, pamAccInfoDTO.getOpMap(), responseDTO);

        //populate the Subscriber Prov library DTO
        KnIPPAMAccInfoDTO pamAccountInfoDTO = this.provMediator.populatePAMAccountDTO(pamAccInfoDTO);
        //Retrieving internal Pam Account Id
        KnOPPAMAccInfoDTO existingPAMAccInfo = this.provClientIntf.getPAMAccountInfo(oldBillingNumber, persisterTxn);
        knLogger.debug(methodName, " existingPAMAccInfo - ", existingPAMAccInfo);

        //retriving pam subscriber profile fro old billing MDN
        pamAccountInfoDTO.setBillingMdn(oldBillingNumber);
        KnOPPAMAccInfoDTO pamSubsProfInfoDTO = this.provClientIntf.getPAMSubsProfile(pamAccountInfoDTO, persisterTxn);
        knLogger.debug(methodName, "got subscriber proifile - ", pamSubsProfInfoDTO);

        if (KnMediatorConstants.PAM_ACCOUNT_STATE.DELETE_IN_PROGRESS.value() == existingPAMAccInfo.getPamAccState()) {
			knLogger.error(methodName, "pam account is in delete-in-progress !!! ");
			throw new KnXDMServerException(KnMediatorConstants.PAMACCOUNT_DELETE_IN_PROGRESS, "pam account is in delete-in-progress ");
		}

        //retriving from db client type config check is enable or disable
        Integer subsClientType = pamSubsProfInfoDTO.getProfileDetails().getClient_Type();
        //LMR client type changes
        if (subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value())
        		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value())
        		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value())
        		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value())) {

        	knLogger.debug(methodName, "Checking client type config table ", subsClientType);
            KnClientTypeConfigDTO clientTypeConfigDTO = this.genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
            if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                knLogger.error(methodName, "Client type is disabled");
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
            }
        }

        KnIPPAMAccInfoDTO ipPamAccInfoDTO = new KnIPPAMAccInfoDTO();
        ipPamAccInfoDTO.setOldBillingNumber(pamAccInfoDTO.getOldBillingNumber());
        ipPamAccInfoDTO.setBillingMdn(pamAccInfoDTO.getBillingNumber());
        //setting existing pamaccid
        ipPamAccInfoDTO.setPamAccId(existingPAMAccInfo.getPamAccId());

        //calling the Prov Library
        KnOPUpdatePAMAccountDTO pamUpdateRespDTO = this.provClientIntf.migratePAMAccount(ipPamAccInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Successfully updated PAM account with resp DTO - ", pamUpdateRespDTO);

        knLogger.debug(methodName, "updating PAM etags");
        this.provClientIntf.updatePAMEtag(pamUpdateRespDTO.getPamAccId(), persisterTxn);

        knLogger.exit(methodName, responseDTO);
		return responseDTO;
	}

	public KnXDMPAMRespDTO upgradeLicensePack(KnMessage message, String operation, KnXDMPAMRespDTO responseDTO,
                                                                                 boolean isBulkProcessing, KnPersisterTxn persisterTxn) throws KnException {
		final String methodName = "upgradeLicensePack(KnMessage, Srting, KnPersisterTxn)";
		//knLogger.entry(methodName);
		String billingNumber;
		KnXDMPAMAccInfoDTO pamAccInfoDTO;
		KnBulkOrderRespDTO respDTO;

		pamAccInfoDTO = (KnXDMPAMAccInfoDTO) message.getPayLoad();
        knLogger.debug(methodName, "Received DTO for upgrade PAM Account - ", pamAccInfoDTO, ", isBulkProcessing-", isBulkProcessing);
        if(pamAccInfoDTO.getVersion() == null) message.setUpgrade(true);

        billingNumber = pamAccInfoDTO.getBillingNumber();

        //retriving pam account info
		KnOPPAMAccInfoDTO existingPAMAcc = this.provClientIntf.getPAMAccountInfo(billingNumber, persisterTxn);
		int pamAccID = existingPAMAcc.getPamAccId();
		if (existingPAMAcc.getPamAccState() == KnMediatorConstants.PAM_ACCOUNT_STATE.DELETE_IN_PROGRESS.value()) {
			knLogger.error(methodName, "pam account is in delete-in-progress !!! ");
			throw new KnXDMServerException(KnMediatorConstants.PAMACCOUNT_DELETE_IN_PROGRESS, "pam account is in delete-in-progress ");
		}

		int dbMaxSub = existingPAMAcc.getTotalNoOfLines();
		int reqMaxSub = pamAccInfoDTO.getTotalNoOfLines();
		int subCount = reqMaxSub - dbMaxSub;
		knLogger.debug(methodName, "dbMaxSub:", dbMaxSub, "reqMaxSub:", reqMaxSub, "subCount:", subCount);

		if (subCount <= 0 || reqMaxSub <= 0) {
			 knLogger.error(methodName, "Invalid Rate plan Request is recieved, subCount:", subCount, " or reqMaxSub:", reqMaxSub);
	         throw new KnXDMServerException(KnMediatorConstants.INVALID_LICENSE_LIMIT, "Invalid License Limit recieved");
		}

		IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

		KnIPPAMAccInfoDTO pamAccountInfoDTO = this.provMediator.populatePAMAccountDTO(pamAccInfoDTO);
		//retriving existing pam account prof
		KnOPPAMAccInfoDTO pamSubsProfInfoDTO = this.provClientIntf.getPAMSubsProfile(pamAccountInfoDTO, persisterTxn);
		knLogger.debug(methodName, "got subscriber profile - ", pamSubsProfInfoDTO);

		 //retriving from db client type config check is enable or disable
        Integer subsClientType = pamSubsProfInfoDTO.getProfileDetails().getClient_Type();
        //LMR client type changes
        if (subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value())
        		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value())
        		|| subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value())
        		|| subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value())) {

            knLogger.debug(methodName, "Checking client type config table ", subsClientType);
            KnClientTypeConfigDTO clientTypeConfigDTO = this.genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
            if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                knLogger.error(methodName, "Client type is disabled");
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
            }
        }

		this.provClientIntf.validateUpgradePAMAccount(reqMaxSub, persisterTxn);
		existingPAMAcc.setTotalNoOfLines(reqMaxSub);
		existingPAMAcc.setProfileDetails(pamSubsProfInfoDTO.getProfileDetails());

		// this is required to get and set the old PAM subscriber profile in case of SOAP call..NOT required in Custom call..
		if (pamAccInfoDTO.getHierarchyType() == HIERARCHY_TYPE.NON_HIERARCHY) {
			knLogger.debug(methodName, "CUSTOM_ flag is OFF ");
			pamAccInfoDTO = this.provMediator.populatePAMAccountDTO(pamAccInfoDTO, existingPAMAcc);
		}
        pamAccInfoDTO.getProfileDetails().setPkgIdMap(pamSubsProfInfoDTO.getProfileDetails().getPkgIdMap());
        pamAccInfoDTO.getProfileDetails().setClient_Type(pamSubsProfInfoDTO.getProfileDetails().getClient_Type());
        pamAccInfoDTO.getProfileDetails().setLicenseType(pamSubsProfInfoDTO.getProfileDetails().getLicenseType());
        //Converged client changes starts
		if(pamAccInfoDTO.getProfileDetails()!=null && pamAccInfoDTO.getProfileDetails().getSubsDefPttRadio() != com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED ){

			String xdmPttId = genInfoUtil.retrieveLocalXDMPttServerId();
			KnXDMSServiceConfigDTO knXDMSServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttId, persisterTxn);
			if(knXDMSServiceConfigDTO.getSubsDefPttRadio() == com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED){
				pamAccInfoDTO.getProfileDetails().setSubsDefPttRadio(com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED);
			}
			else{
				KnOPCorpProfileInfoDTO knOPCorpProfileInfoDTO = retrieveCorporateProfile(pamSubsProfInfoDTO.getProfileDetails().getExtCorpId(), persisterTxn);
				if(knOPCorpProfileInfoDTO.getSubsDefPttRadio() == com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED){
					pamAccInfoDTO.getProfileDetails().setSubsDefPttRadio(com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED);
				}
			}
		}
		if(pamAccInfoDTO.getProfileDetails().getSubsDefPttRadio() == com.kodiak.common.resources.KnConstants.SUBSCR_DEF_PTTRADIO_ENABLED ){
			switch(subsClientType){
			case 5:
				pamAccInfoDTO.getProfileDetails().setClient_Type(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value());
				break;
			case 10:
				pamAccInfoDTO.getProfileDetails().setClient_Type(KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value());
				break;
			default:
				break;
			}
		}
/*        int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
        Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
        String deviceSharingFlag = microServicesParamNameValueMap.get(DEVICE_SHARING_FEATURE_FLAG);
        knLogger.info(methodName, "deviceSharingFlag",  deviceSharingFlag);
        int deviceSharewifiFlag=Integer.parseInt(microServicesParamNameValueMap.get(AUTO_DEVICESHARE_WIFI_CC));
        knLogger.info(methodName, "deviceSharewifiFlag-->",  deviceSharewifiFlag);
		 if ((subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
					|| subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()
					|| subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
					|| subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) && Integer.parseInt(deviceSharingFlag) == ENABLED
                    && deviceSharewifiFlag == ENABLED){
			knLogger.debug(methodName, "license type for wifi , crosscarrier and its ptt ");
         	pamAccInfoDTO.getProfileDetails().setLicenseType(KnConstants.USER_LICENSE_TYPE);
			}else{
             pamAccInfoDTO.getProfileDetails().setLicenseType(pamSubsProfInfoDTO.getProfileDetails().getLicenseType());
         }*/
		//Converged client  changes ends
		//for upgrade we need to add flag, so that bulk will identify weather it is a create or upgrade license pack.
		pamAccInfoDTO.getProfileDetails().setUpgrade(true);

		pamAccInfoDTO.setPamAccId(pamSubsProfInfoDTO.getPamAccId());
        pamAccInfoDTO.getProfileDetails().setPamAccId(pamSubsProfInfoDTO.getPamAccId());
        pamAccInfoDTO.setSubsCount(subCount);


        KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO = this.provMediator.populateBulkSubsProvInfoDTO(pamAccInfoDTO);
        KnOPCreateSubsInfoDTO respValidateDTO = this.provClientIntf.validateCreateSubscriber(bulkSubsProvInfoDTO, subCount, persisterTxn);
        knLogger.debug(methodName, "validate profile response:  ", respValidateDTO);
        pamAccInfoDTO.getProfileDetails().setAutoPair(respValidateDTO.getCorpAutoPairing());

        if (pamAccInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
        	//removing transaction from map for bulk order persistence
        	pamAccInfoDTO.getProfileDetails().getCustomParamMap().remove(com.kodiak.common.resources.KnConstants.PERSISTER_TXN);
        }

        int existingSubscCount = provXDMServerDAO.retrieveSubsCountforPAM(pamAccID, persisterTxn);
        pamAccInfoDTO.setExistingSubsCount(existingSubscCount);

        //required in Bulk FW. Bulk FW always refer PAM Subs details
        pamAccInfoDTO.getProfileDetails().setHierarchyType(pamAccInfoDTO.getHierarchyType());

        if (isBulkProcessing) {
        	knLogger.info(methodName, "its a bulk operation..inserting into bulk table");
        	KnBulkOrderDTO bulkOrderDTO = new KnBulkOrderDTO();
        	knLogger.debug(methodName, "bulk calling pamAccInfoDTO :", pamAccInfoDTO);
        	message.setPayLoad(pamAccInfoDTO);
        	bulkOrderDTO.setBulkOrderReqObj(message);
        	bulkOrderDTO.setBulkOrderObj(message);
        	bulkOrderDTO.setCorpId(pamAccID);
        	bulkOrderDTO.setOperationType(KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_CREATE.value());
        	knLogger.debug(methodName, "inserting into bulk order table");
        	respDTO = this.bulkFactory.performBulkOp(bulkOrderDTO);
        	knLogger.info(methodName, "submitted bulk order for create Subscribers in update PAM :  ", respDTO);
        	if (KnBulkDocDiffConstants.REQUEST_STATUS.FAILURE.equals(respDTO.getReqStatus())) {
        		knLogger.error(methodName, "failed to submit bulk order  ");
        		throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, respDTO.getResponseMsg());
        	}

        } else {
        	knLogger.info(methodName, "its a non bulk operation..processing synchronously");
        	 //allocate pseudo mdns for creation
       	 	List<String> mdnList = kuidGenerator.getKodiakUserIDs(KnKUIDConstants.COUNTRYCODE,subCount);
       	 	pamAccInfoDTO.getProfileDetails().setMdns(mdnList);
            pamAccInfoDTO.setFeatureBitInfoMap(existingPAMAcc.getProfileDetails().getProvFSMap());
            pamAccInfoDTO.getProfileDetails().setSubscriberFS2(existingPAMAcc.getProfileDetails().getSubscriberFS2());
       	 	responseDTO =  (KnXDMPAMRespDTO) bulkMediator.createSubscribers(pamAccInfoDTO, Boolean.FALSE, persisterTxn);

       	 	if (responseDTO != null && responseDTO.getResponseStatus() == MEDIATOR_RESP_STATUS.SUCCESS.value()) {
       	 		// update pseudo numbers as used
       	 		KnXDMPamResponseHandler responseHandler = KnXDMPamResponseHandler.getInstance();
       	 		responseHandler.upgradeLicensePackResponse(responseDTO, pamAccInfoDTO, message.getCorrelationId(), persisterTxn);

       	 	} else {
       	 		knLogger.error(methodName, "PAM upgrade subscriber creation failed");
       	 		//de allocate pseudo mdns incase of creation failure

       	 		if (responseDTO != null && responseDTO.getResponseCode().contains("SP12051")) {
       	 			knLogger.info(methodName, "Mismatch observed in Pseudo Pool and PocSubsInfo for mdns  ", KnGDPRTemplate.mdnList(mdnList));
       	 			KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.ALARM_PSEUDO_NUM_POOL_DB_INCONSISTENCY_OBSERVED, KnAlarmConstants.SEVERITY_MAJOR,
       	 				KnKUIDConstants.ALARM_MOCLASSTYPE, "KnXDMPamHelper");
       	 		}
       	 	 this.setNotifyParams(pamAccInfoDTO.getTransactionId(), billingNumber, pamAccInfoDTO.getOpMap(), responseDTO);
       		 throw new KnXDMServerException(responseDTO.getResponseCode(), responseDTO.getResponseMessage());
       	 }
       }

        this.setNotifyParams(pamAccInfoDTO.getTransactionId(), billingNumber, pamAccInfoDTO.getOpMap(), responseDTO);


        knLogger.exit(methodName, responseDTO);
		return responseDTO;
	}

	/**
	 *
	 * @param message Request object
	 * @param operation operation Name for auditing purpose
	 * @param responseDTO License pack response object
	 * @param persisterTxn Transaction object
	 * @return License pack info
	 * @throws KnDAOException throws KnDAOException if DB Exception occurred
	 * @throws KnXDMServerException throws KnXDMServerException if any Business related operation fails
	 * @throws KnProcessInvokerException    throws KnProcessInvokerException any internal error occurred
	 */
	public KnXDMLicensePackResponseDTO getLicensePackProfile(KnMessage message, String operation, KnXDMLicensePackResponseDTO responseDTO, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException, KnProcessInvokerException {
		final String methodName = "getLicensePackProfile(KnMessage, Srting, KnPersisterTxn)";
		//knLogger.entry(methodName, persisterTxn);
		String billingNumber;
		KnXDMPAMAccInfoDTO pamAccInfoDTO;

		pamAccInfoDTO = (KnXDMPAMAccInfoDTO) message.getPayLoad();
        knLogger.debug(methodName, "Received DTO for upgrade PAM Account - ", pamAccInfoDTO);
        if(pamAccInfoDTO.getVersion() == null) message.setUpgrade(true);

        billingNumber = pamAccInfoDTO.getBillingNumber();
        responseDTO.setTransactionId(pamAccInfoDTO.getTransactionId());
        responseDTO.setBillingNumber(billingNumber);

		KnOPPAMAccInfoDTO existingPAMAcc = this.provClientIntf.getPAMAccountInfo(billingNumber, persisterTxn);
		knLogger.debug(methodName, "got License Pack info - ", existingPAMAcc);
		int pamAccID = existingPAMAcc.getPamAccId();

		responseDTO.setBillingName(existingPAMAcc.getBillingName());
		responseDTO.setTotalNoOfLines(existingPAMAcc.getTotalNoOfLines());
		responseDTO.setServiceAuthStatus(existingPAMAcc.getPamAccState());
		responseDTO.setCreationTime(existingPAMAcc.getCreationTime());
		responseDTO.setUpdationTime(existingPAMAcc.getLastUpdateTime());

		KnIPPAMAccInfoDTO pamAccountInfoDTO = new KnIPPAMAccInfoDTO();
		KnPAMSubsProfInfoDTO pamSubsProfInfoDTO = new KnPAMSubsProfInfoDTO();
		pamSubsProfInfoDTO.setPamAccId(pamAccID);
		pamAccountInfoDTO.setProfileDetails(pamSubsProfInfoDTO);

		// getting PAM sunscriber Profile info
		KnOPPAMAccInfoDTO oppamAccInfoDTO = this.provClientIntf.retrievePAMSubsProfInfo(pamAccountInfoDTO, persisterTxn);
		KnPAMSubsProfInfoDTO subsProfInfoDTO = oppamAccInfoDTO.getProfileDetails();
		knLogger.debug(methodName, "got License Pack subscriber profile info - ", subsProfInfoDTO);
		if (subsProfInfoDTO != null) {
			responseDTO.setExtCorpID(subsProfInfoDTO.getExtCorpId());
			responseDTO.setCorpName(subsProfInfoDTO.getCorpName());
			int subscriptionType = KnProvUtil.getMappedSubscriptionType(subsProfInfoDTO.getPubSubsType(), subsProfInfoDTO.getCorpSubsType());
			responseDTO.setSubscriptionType(subscriptionType);
			responseDTO.setSubsClientType(subsProfInfoDTO.getClient_Type());
			responseDTO.setEmail(subsProfInfoDTO.getEmail());
			responseDTO.setImei(subsProfInfoDTO.getImei());
			Map<String, Integer> pkgIds= new HashMap<>();
			if(subsProfInfoDTO.getTierPkgCode() !=null)
			pkgIds.put(subsProfInfoDTO.getTierPkgCode(), KnConstants.TIER_PKG_TYPE);
			if(subsProfInfoDTO.getAddOnPkgId()!=null)
			{
				for (String pkgCode : subsProfInfoDTO.getAddOnPkgId()) {
					pkgIds.put(pkgCode, KnConstants.ADDON_PKG_TYPE);
				}
			}

			Map<String, Map<String, Integer>> pkgIdMap= new HashMap<>();
			pkgIdMap.put(KnConstants.ADD_ACTION, pkgIds);
			responseDTO.setPkgIdMap(pkgIdMap);
			responseDTO.setLicenseType(subsProfInfoDTO.getLicenseType());
			responseDTO.setFirstNetIndicator(subsProfInfoDTO.getFirstNetIndicator());
            // or set the fs till here and pass to xdm there we can convert it to map
			responseDTO.setSubscriberFs(KnGeneralUtil.convertHexStringToLong(subsProfInfoDTO.getSubscriberFS2()));
            responseDTO.setSubscriberFs2(subsProfInfoDTO.getSubscriberFS2());
            // we can do the convertion here and then we can set the map here only and pass it to XDM
            responseDTO.setProvFSMap(subsProfInfoDTO.getProvFSMap());

		}

       //Invoking the Custom Invoker for Custom data:
        Map<String, Object> customMap = null;

        if (pamAccInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
        	knLogger.info(methodName, "CUSTOM_ flag is ON ");
        	pamAccInfoDTO.setPamAccId(pamSubsProfInfoDTO.getPamAccId());
        	pamAccInfoDTO.setProfileDetails(new KnXDMPAMSubsProfInfoDTO());
        	pamAccInfoDTO.getProfileDetails().setPamAccId(pamSubsProfInfoDTO.getPamAccId());

            customMap = getCustomPAMAccount(pamAccInfoDTO, persisterTxn);
            knLogger.debug(methodName, "got License Pack Custom MAP - ", customMap);
            responseDTO.setCustomParamMap(customMap);
        }
        // end custom logic

        knLogger.exit(methodName, responseDTO);
		return responseDTO;
	}

	 private static boolean isNull(Object input) {
		 return input == null ? Boolean.TRUE : Boolean.FALSE;
	 }

	 public static boolean isNullOrEmpty(String value) {
		 boolean result = Boolean.FALSE;
		 if (value == null || EMPTY_STRING.equalsIgnoreCase(trim(value))) {
			 result = Boolean.TRUE;
		 }
		 return result;
	 }

	 private static String trim(String input) {
		 return input != null ? input.trim() : input;
	 }

	  private Map<String, Object> getCustomPAMAccount(KnXDMPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProcessInvokerException, KnXDMServerException {
	        final String methodName = "getCustomPAMAccount(KnXDMPAMAccInfoDTO , KnPersisterTxn)";
	        knLogger.entry(methodName, pamAccInfoDTO, persisterTxn);
	        KnXDMRespDTO customResponseDTO = null;
	        Map<String, Object> customMap = new HashMap<String, Object>();
	        customMap.put(KnMediatorConstants.CUSTOM_PROV_ACTION, KnMediatorConstants.CUSTOM_PROV_VIEW_PAMACCOUNT_OP);
	        customMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
	        pamAccInfoDTO.getProfileDetails().setCustomParamMap(customMap);
	        knLogger.debug(methodName, "CUSTOM_PROV_VIEW_PAMACCOUNT_OP ");
	        customResponseDTO = invokeHook(KnMediatorConstants.CUSTOM_PROV_INVOKER, pamAccInfoDTO);

	        if (!isNull(customResponseDTO))
	            customMap = customResponseDTO.getCustomParamMap();
	        knLogger.exit(methodName, customMap);
	        return customMap;
	    }

	  private KnXDMRespDTO invokeHook(String hookName, KnXDMPAMAccInfoDTO pamAccInfoDTO) throws KnProcessInvokerException, KnXDMServerException {
	        final String methodName = "invokeHook(String, KnXDMPAMAccInfoDTO)";
	        knLogger.entry(methodName, hookName, pamAccInfoDTO);
	        KnXDMRespDTO customResponseDTO = null;
	        Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnMediatorConstants.CUSTOM_PROV_INVOKER, pamAccInfoDTO);
	        knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);
	        if (customResp instanceof KnXDMRespDTO) {
	            customResponseDTO = (KnXDMRespDTO) customResp;

	            if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
	                knLogger.debug(methodName, "failed to view PAM subs profile ");
	                throw new KnXDMServerException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());
	            }
	        }
	        knLogger.exit(methodName, customResponseDTO);
	        return customResponseDTO;
	    }

	  public List<String> retrieveUnusedSubsList(KnXDMPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, Exception {
	        final String methodName = "retrieveUnusedSubsList(pamAccInfoDTO,KnPersisterTxn)";
	        List<String> freeMdnList = null;
	        LinkedHashSet<String> freeMdnsLHSet = new LinkedHashSet<String>();
	        int reqCount = 0, seqReqCount = 0, start = 0, end = 0;
	        knLogger.entry(methodName, pamAccInfoDTO, persisterTxn);
	        reqCount = pamAccInfoDTO.getSubsCount();

	        // get the pseudo numbers marked for downgrade by CAT-UI
	        List<String> catMdnList = kuidGenerator.getKUIDByStatus(reqCount, (pamAccInfoDTO.getPamAccId()), KnKUIDConstants.KUID_MDN_STATUS.DOWNGRADE_BY_CAT.valueOf());
	        freeMdnsLHSet.addAll(catMdnList);
	        knLogger.debug(methodName, "CAT marked MDN List - ", KnGDPRTemplate.mdnList(freeMdnsLHSet));

	        //check for priority Logic one
	        if (reqCount > freeMdnsLHSet.size()) {
	        	//priority logic
	        	//1.Pseudo Numbers which are provisioned but are not activated yet
	        	freeMdnsLHSet.addAll(this.provMediator.retrievePAMAccountProvMDNs(pamAccInfoDTO.getPamAccId(), persisterTxn));

	        	//check for priority Logic two
	        	if (reqCount > freeMdnsLHSet.size()) {
	        		//2.Pseudo Numbers that are activated but don't have any corporate contacts or groups
	        		freeMdnsLHSet.addAll(getNoCorpCordMdnsList(pamAccInfoDTO, persisterTxn));

	        		//check for priority Logic Three
	        		if (reqCount > freeMdnsLHSet.size()) {
	        			//3.The lines with the last sequential number.
	        			seqReqCount = reqCount - freeMdnsLHSet.size();
	        			while (reqCount > freeMdnsLHSet.size()) {
	        				start = end + 1;
	        				end = (end + (seqReqCount * 2));
	        				freeMdnsLHSet.addAll(this.provMediator.getPamAccLastSequenceMdns(pamAccInfoDTO.getPamAccId(), start, end, persisterTxn));

	        			}
	        		}
	        	}
	        }


	        freeMdnList = (new LinkedList<String>(freeMdnsLHSet)).subList(0, reqCount);
	        //updating licensce pack
	        List<String> insertMdnsList =new ArrayList<>(freeMdnList);
	        insertMdnsList.removeAll(catMdnList);
	        kuidGenerator.insertKUIDPool(insertMdnsList, pamAccInfoDTO.getPamAccId(),KnKUIDConstants.KUID_MDN_STATUS.DOWNGRADE.valueOf(), persisterTxn);
	        kuidGenerator.updateKUIDPool(catMdnList, pamAccInfoDTO.getPamAccId(),KnKUIDConstants.KUID_MDN_STATUS.DOWNGRADE.valueOf(), persisterTxn);

	        knLogger.exit(methodName, KnGDPRTemplate.mdnList(freeMdnList));
	        return freeMdnList;
	    }

	  private List<String> getNoCorpCordMdnsList(KnXDMPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
	        final String methodName = "getNoCorpStringMdnsList(KnXDMPAMAccInfoDTO, KnPersisterTxn)";

	        KnCorpPAMSubsRespDTO responseDTO = null;
	        knLogger.entry(methodName, pamAccInfoDTO, persisterTxn);
	        List<String> corpfreeMdns = new ArrayList<>();
	        //populate the Subscriber Prov library DTO
	        KnCorpPAMSubsReqDTO corpPAMSubsReqDTO = new KnCorpPAMSubsReqDTO();
	        corpPAMSubsReqDTO.setPamAccId(pamAccInfoDTO.getPamAccId());
	        corpPAMSubsReqDTO.setExtCorpId(pamAccInfoDTO.getProfileDetails().getExtCorpId());
	        corpPAMSubsReqDTO.setClientType(pamAccInfoDTO.getProfileDetails().getClient_Type());
	        corpPAMSubsReqDTO.setUnUsedMdnCount(pamAccInfoDTO.getSubsCount());

	        //calling the corpMediator Library
	        responseDTO = this.corpMediator.getUnusedSubsList(corpPAMSubsReqDTO, persisterTxn);
	        knLogger.debug(methodName, " corp Library response - ", responseDTO);

	        if (!isNull(responseDTO)) {
	        	corpfreeMdns = responseDTO.getFreePAMSubsList();
	        }
	        knLogger.exit(methodName, corpfreeMdns);
	        return corpfreeMdns;
	    }

	  public KnOPPAMAccInfoDTO getPAMAccountDetails(String extPAMAccId, KnPersisterTxn persisterTxn) throws KnXDMServerException  {
		  final String methodName = " getPAMAccountDetails(String, KnPersisterTxn)";
		  knLogger.entry(methodName, extPAMAccId, persisterTxn);

		  KnOPPAMAccInfoDTO pamAccInfo = null;
		  try {
			  pamAccInfo = this.provClientIntf.getPAMAccountInfo(extPAMAccId, persisterTxn);
			  knLogger.debug(methodName, "Retrived PAM Account info - ", pamAccInfo);

			  KnIPPAMAccInfoDTO ippamAccInfoDTO = new KnIPPAMAccInfoDTO();
			  ippamAccInfoDTO.setProfileDetails(new KnPAMSubsProfInfoDTO());
			  ippamAccInfoDTO.getProfileDetails().setPamAccId(pamAccInfo.getPamAccId());

			  KnOPPAMAccInfoDTO pamSubsProfOpDTO = this.provClientIntf.retrievePAMSubsProfInfo(ippamAccInfoDTO, persisterTxn);
			  KnPAMSubsProfInfoDTO pamSubsProfInfoDTO =  pamSubsProfOpDTO.getProfileDetails();
			  knLogger.debug(methodName, "Retrived PAM Subscriber profile info - ", pamSubsProfInfoDTO);

			  //Invoking the Custom Invoker for Custom data:
			  Map<String, Object> customMap = null;
			  if (pamAccInfo.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
				  knLogger.debug(methodName, "CUSTOM_ flag is ON ");

				  KnXDMPAMSubsProfInfoDTO xdmpamSubsProfInfoDTO = new KnXDMPAMSubsProfInfoDTO();
				  xdmpamSubsProfInfoDTO.setPamAccId(pamAccInfo.getPamAccId());
				  xdmpamSubsProfInfoDTO.setProfileId(pamSubsProfInfoDTO.getProfileId());

				  KnXDMPAMAccInfoDTO xdmpamAccInfoDTO = new KnXDMPAMAccInfoDTO();
				  xdmpamAccInfoDTO.setProfileDetails(xdmpamSubsProfInfoDTO);

				  customMap = this.getCustomPAMAccount(xdmpamAccInfoDTO, persisterTxn);
				  knLogger.debug(methodName, "Retrived PAM Add Subscriber profile info - ", customMap);
				  pamSubsProfInfoDTO.setCustomParamMap(customMap);
			  }

			  pamAccInfo.setProfileDetails(pamSubsProfInfoDTO);

		  } catch (KnProvException e) {
			  knLogger.error(methodName, "KnProvException occured", e);
			  // not throwing exception if PAM account not found.. sending response as null
			  if (!e.getErrorCode().equals(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND)) {
				  throw e;
			  }

		  } catch (KnProcessInvokerException e) {
			  knLogger.error(methodName, "ProcessInvokerException occured", e);
			  throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());
		  }
		  knLogger.exit(methodName, pamAccInfo);
		  return pamAccInfo;
	  }

	  public boolean isFinalNotification(KnXDMPAMRespDTO xdmpamRespDTO, KnMessage message, int bulkOperationID, boolean isProfileChanged) throws KnXDMServerException {
		  final String methodName = "isFinalNotification(KnXDMPAMRespDTO, KnMessage, int, boolean)";
		  knLogger.entry(methodName, xdmpamRespDTO, bulkOperationID, isProfileChanged);
		  boolean isFinalNotification = false;
		  KnPersisterTxn persisterTxn = null;

		  try {

			  persisterTxn = KnPersisterTxn.getPersisterTxn();
			  persisterTxn.open();

			  if (xdmpamRespDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
	        	knLogger.debug(methodName, "Custom is ON");
	        	Map<String, Object> customMap = new HashMap<>();
	        	Object customResp;
	        	customMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_UPDATE_BULKOPINFO_OP);
	        	customMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);

	        	KnBulkOpInfoDTO bulkOpInfoDTO = new KnBulkOpInfoDTO();
	        	bulkOpInfoDTO.setTransID(xdmpamRespDTO.getTransactionId());
	        	bulkOpInfoDTO.setOperationID(String.valueOf(bulkOperationID));
	        	bulkOpInfoDTO.setRespCode(xdmpamRespDTO.getResponseCode());
	        	bulkOpInfoDTO.setCustomParamMap(customMap);
	        	knLogger.debug(methodName, "Calling Custom lib with - ", bulkOpInfoDTO);
	        	customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, bulkOpInfoDTO);
	        	knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);

	        	if (customResp instanceof KnBulkOpInfoRespDTO) {
	        		KnBulkOpInfoRespDTO bukBulkOpInfoRespDTO = (KnBulkOpInfoRespDTO) customResp;

	        		if (bukBulkOpInfoRespDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
	                    knLogger.debug(methodName, "failed to update Bulk Op Details");
	                    throw new KnProvBOException(bukBulkOpInfoRespDTO.getResponseCode(), bukBulkOpInfoRespDTO.getResponseMessage());

	                }
	        		knLogger.debug(methodName, "Successfully updated PAM Bulk Op info details");
	        	}

				//Get custom Bulk
				customMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_GET_BULKOPINFO_OP);
	        	customMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
	        	bulkOpInfoDTO.setCustomParamMap(customMap);
	        	customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, bulkOpInfoDTO);
	        	knLogger.debug(methodName, "Response from the Custom Prov  for Get PAM Bulk op Info Details- ", customResp);

	        	if (customResp instanceof KnBulkOpInfoRespDTO) {
	        		KnBulkOpInfoRespDTO bukBulkOpInfoRespDTO = (KnBulkOpInfoRespDTO) customResp;
	        		List<KnBulkOpInfoDTO>  bulopInfoList =  bukBulkOpInfoRespDTO.getBulkOpInfoDTOList();
	        		knLogger.debug(methodName, "Bulk PAM Info List - ", bulopInfoList);
	        		int count = 0;
	        		Map<Integer, String> bulkRespMap = new TreeMap<>();
	        		if (bulopInfoList != null) {
	        			for (KnBulkOpInfoDTO knBulkOpInfoDTO : bulopInfoList) {
							String RespCode = trim(knBulkOpInfoDTO.getRespCode());
							if (RespCode != null && !RespCode.isEmpty()) {
								bulkRespMap.put(knBulkOpInfoDTO.getPriority(), RespCode);
								count ++;
							}
						}
	        			// if response code is not null in all the entries check weather all scuccess and failure
	        			knLogger.debug(methodName, "count - ", count, "bulopInfoList.size() - ", bulopInfoList.size());
	        			if (count == bulopInfoList.size()) {
	        				isFinalNotification = true;
	        				xdmpamRespDTO.setResponseCode(KnProvConstants.SUCCESS_CODE);
	        				// Check for any failure in responses,  if failure is there then set the failure response code in the final
	        				// response in Priority basis
	        				knLogger.debug(methodName, "bulkRespMap - ", bulkRespMap);
	        				for (Map.Entry<Integer, String> entry : bulkRespMap.entrySet()) {
								if (!entry.getValue().equals(KnProvConstants.SUCCESS_CODE)) {
									knLogger.debug(methodName, "Setting error code - ", entry.getValue(), " of priority - " , entry.getKey());
									xdmpamRespDTO.setResponseCode(entry.getValue());
									break;
								}
							}

	        			}

	        		}
	        	}

	        	if (isFinalNotification) {
	        		customMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_DELETE_BULKOPINFO_OP);
	        		customMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
	        		bulkOpInfoDTO.setCustomParamMap(customMap);

	        		knLogger.debug(methodName, "Calling Custom lib with - ", bulkOpInfoDTO);
	        		customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, bulkOpInfoDTO);
	        		knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);

	        		if (isProfileChanged) {
	    	        	// updating PAM Account
	        			try {
	        				// if failed here we have to drop it.. its partial succeess for us.
	        				updateLicensePack(message, KnMediatorConstants.PAM_AUDIT_UPDATE_LICENSE_PACK, xdmpamRespDTO, persisterTxn);

	        				// NNI subscriber etag update and peg increment
	        				KnXDMPAMAccInfoDTO pamAccInfoDTO = (KnXDMPAMAccInfoDTO) message.getPayLoad();
	        				KnXDMPAMSubsProfInfoDTO subsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
	        				this.provClientIntf.updateEtagForNNISubscr(subsProfInfoDTO.getExtCorpId(), subsProfInfoDTO.getClient_Type(), persisterTxn);
	        			} catch (Exception e) {
	        				knLogger.error(methodName, "UN Expected Excetion occured",  e);
	        			}
	        		}
	        	}

	        	if (bulkOperationID == com.kodiak.common.resources.KnConstants.OP_ID_DELETE_PAM_ACCOUNT) {
	        		knLogger.debug(methodName, "DeletelLicensePack Resonse.. dropping notification-", xdmpamRespDTO.getTransactionId());
	        		isFinalNotification = false;
	        	}

	        } else {
				isFinalNotification = true;
			}
	        persisterTxn.save();
		} catch (KnDAOException | KnProcessInvokerException e) {
			knLogger.error(methodName, "Exception occured", e);
			rollback(persisterTxn);
			throw new KnXDMServerException(e.getErrorCode(), e.getErrorMessage());

		} catch (Exception e) {
			knLogger.error(methodName, "Exception occured", e);
			rollback(persisterTxn);
			throw e;
		}
		  knLogger.exit(methodName, isFinalNotification);
		  return isFinalNotification;
	  }

	  public KnOPProvDTO updateEtagForNNISubscr(String ExtCorpId,int clientType,KnPersisterTxn persisterTxn) throws KnProvException {
		  return this.provClientIntf.updateEtagForNNISubscr(ExtCorpId, clientType, persisterTxn);
	  }

	  /**
		 * Rollback the transaction
		 *
		 * @param txn
		 *            transaction object
		 */
		private void rollback(KnPersisterTxn txn) {
			try {
				knLogger.error("rollback()", "Rolling back transaction");
				if (txn != null) {
					txn.rollback();
				}
			} catch (Exception e) {
				knLogger.error("rollback(txn)", "Failed to rollback the transaction.", e);
			}
		}

	public void updateRatePlan(KnXDMPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProcessInvokerException, KnProvBOException {
		final String methodName = "updateRatePlan()";
		knLogger.entry(methodName, pamAccInfoDTO,  persisterTxn);

		// Invoking the Custom Invoker for Custom data:
		Map<String, Object> customMap = pamAccInfoDTO.getProfileDetails().getCustomParamMap();
		knLogger.debug(methodName, "customMap ::", customMap);
		customMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_PROV_UPDATE_PAM_RATEPLAN_OP);
		customMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
		pamAccInfoDTO.getProfileDetails().setCustomParamMap(customMap);

		Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, pamAccInfoDTO);
		knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);
		if (customResp instanceof KnXDMRespDTO) {
			KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customResp;

			if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
				knLogger.debug(methodName, "failed to update PAM Rate plan DEtails ");
				throw new KnProvBOException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());

			} else {
				knLogger.debug(methodName, "successfully update PAM RatePlan");

			}
		}
		knLogger.exit(methodName);
	}

    /**
     * this method will accept the external corpid in the request.
     * so first it will get the internal corpid using the request extcorpid
     * then it will get the pamm acc id using that internal corpid
     * @param extCorpId
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public List<Integer> retrievePAMAccIdForCorpId(String extCorpId,KnPersisterTxn persisterTxn) throws KnXDMServerException {
        final String methodName = "retrievePAMAccIdForCorpId(String)";
        knLogger.debug(methodName, "ENTRY:extCorpId", extCorpId);
        List<Integer> listOfPamAccId=new ArrayList<>();
        try {

            KnOPCorpProfileInfoDTO corpProfileInfoDTO= provClientIntf.retrieveCorporateProfile(extCorpId, persisterTxn);
            knLogger.debug(methodName,"Corp profile received",corpProfileInfoDTO);
            int corpId=corpProfileInfoDTO.getCorpId();
            knLogger.debug(methodName,"corpId received",corpId);
            listOfPamAccId=this.provClientIntf.retrievePAMAccIdForCorpId(corpId, persisterTxn);
            knLogger.debug(methodName, "Retrived PAM Account info - ", listOfPamAccId);
        } catch (KnProvException e) {
            knLogger.error(methodName, "KnProvException occured", e);
                throw e;
        }
        knLogger.info(methodName, "EXIT", listOfPamAccId);
        return listOfPamAccId;
    }

    public int retrievePAMSubsCount(String extPamAccId, KnPersisterTxn persisterTxn) throws KnProvBOException, KnDAOException  {
    	final String methodName = "retrievePAMSubsCount(String, KnPersisterTxn)";
    	knLogger.entry(methodName, extPamAccId);
		IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

		int intPamAccID = provXDMServerDAO.retrievePAMAccId(extPamAccId, persisterTxn);
		int pamSubsCount = provXDMServerDAO.retrieveSubsCountforPAM(intPamAccID, persisterTxn);
		knLogger.exit(methodName, pamSubsCount);
		return pamSubsCount;
    }

    public void setNotifyParams(String transID, String billingNumber,  Map<String, Object> opMap, KnXDMPAMRespDTO responseDTO) {
    	responseDTO.setTransactionId(transID);
        responseDTO.setBillingNumber(billingNumber);
        responseDTO.setCustomParamMap(opMap);
    }

    public KnOPCorpProfileInfoDTO retrieveCorporateProfile(String extCorpId,KnPersisterTxn persisterTxn) throws KnXDMServerException {
    	  final String methodName = "retrieveCorporateProfile(String,KnPersisterTxn)";
          knLogger.debug(methodName, "ENTRY : extCorpId ", extCorpId);
          KnOPCorpProfileInfoDTO knOPCorpProfileInfoDTO = this.provClientIntf.retrieveCorporateProfile(extCorpId, persisterTxn);
          knLogger.debug(methodName,"Retrived corp profile ",knOPCorpProfileInfoDTO);
    	return knOPCorpProfileInfoDTO;
    }

    public KnXDMPAMRespDTO updateSubscribersFSAndPkgIds(KnMessage message, KnXDMPAMRespDTO responseDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnDAOException, KnProcessInvokerException{

        String methodName = "updateSubscribersFSAndPkgIds";
        KnXDMPAMAccInfoDTO xdmpamAccInfoDTO =(KnXDMPAMAccInfoDTO) message.getPayLoad();

        knLogger.debug(methodName, "Received DTO for update PAM account - ", xdmpamAccInfoDTO);

        String billingNumber = xdmpamAccInfoDTO.getBillingNumber();
        this.setNotifyParams(xdmpamAccInfoDTO.getTransactionId(), billingNumber, xdmpamAccInfoDTO.getOpMap(), responseDTO);

        //populate the Subscriber Prov library DTO
        KnIPPAMAccInfoDTO pamAccountInfoDTO = this.provMediator.populatePAMAccountDTO(xdmpamAccInfoDTO);

        //Retrieving internal Pam Account Id
        KnOPPAMAccInfoDTO pamAccountInfo = this.provClientIntf.getPAMAccountInfo(xdmpamAccInfoDTO.getBillingNumber(), persisterTxn);

        //retriving pam subscriber profile
        KnOPPAMAccInfoDTO pamSubsProfInfoDTO = this.provClientIntf.getPAMSubsProfile(pamAccountInfoDTO, persisterTxn);
        knLogger.debug(methodName, "got subscriber proifile - ", pamSubsProfInfoDTO);

        if (KnMediatorConstants.PAM_ACCOUNT_STATE.DELETE_IN_PROGRESS.value() == pamAccountInfo.getPamAccState()) {
            knLogger.error(methodName, "pam account is in delete-in-progress !!! ");
            throw new KnXDMServerException(KnMediatorConstants.PAMACCOUNT_DELETE_IN_PROGRESS, "pam account is in delete-in-progress ");
        }

        //retriving from db client type config check is enable or disable
        //LMR client type changes
        Integer subsClientType = pamSubsProfInfoDTO.getProfileDetails().getClient_Type();
        if (subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value())
                || subsClientType == (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value())
                || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value())
                || subsClientType ==  (KnConstants.SUBSCRIBERS_CLIENT_TYPE.SGMDNPATCH.value())) {

            knLogger.debug(methodName, "Checking client type config table ", subsClientType);
            KnClientTypeConfigDTO clientTypeConfigDTO = this.genInfoUtil.getClientTypeConfig(subsClientType, persisterTxn);
            if (clientTypeConfigDTO.getIsEnable() != KnProvConstants.CLIENT_TYPE_CONFIGURATION.ENABLED.value()) {
                knLogger.error(methodName, "Client type is disabled");
                throw new KnXDMServerException(KnErrorCodes.BOEntity.CLIENT_TYPE_DISABLED, "Subscriber Client Type is disabled");
            }
        }

        int pamAccId = pamAccountInfo.getPamAccId();
        knLogger.debug(methodName, "after retrieving pamAccId:", pamAccId);

        //Setting the retrieved pamAccid to DTO
        pamAccountInfoDTO.setPamAccId(pamAccId);
        pamAccountInfoDTO.getProfileDetails().setPamAccId(pamAccId);
        xdmpamAccInfoDTO.setPamAccId(pamAccId);
        knLogger.debug(methodName, "validating update pamaccount", pamAccountInfoDTO);
        KnOPUpdatePAMAccountDTO updatePAMAccountDTO =  this.provClientIntf.validateUpdatePamAccount(pamAccountInfoDTO, persisterTxn);
        knLogger.debug(methodName, " KnOPUpdatePAMAccountDTO ", updatePAMAccountDTO);

        if(updatePAMAccountDTO.isSubsFSUpdated()){
            List<String> mdnList = provClientIntf.retrievePAMAccountMDNs(pamSubsProfInfoDTO.getPamAccId(), persisterTxn);
            knLogger.debug(methodName, " mdnList ", KnGDPRTemplate.mdnList(mdnList));

            if(mdnList != null && !mdnList.isEmpty() ){
                xdmpamAccInfoDTO.getProfileDetails().setMdns(mdnList);

                knLogger.debug(methodName,"pamAccInfoDTO :",xdmpamAccInfoDTO);
                IXDMResponseDTO ixdmResponseDTO=this.bulkMediator.updateBulkSubsFSAndPkgIds(xdmpamAccInfoDTO,persisterTxn);
                knLogger.debug(methodName,"IXDMResponseDTO ixdmResponseDTO :",ixdmResponseDTO);
                // pamAccountInfoDTO.getProfileDetails().setProvFSMap(subsFS.);
                if(ixdmResponseDTO.getResponseStatus()== KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                    knLogger.debug(methodName, "failed to update Feature bits ");
                    throw new KnProvBOException(ixdmResponseDTO.getResponseCode(), ixdmResponseDTO.getResponseMessage());
                }
                knLogger.info(methodName, "updating PAM profile..");
                this.provClientIntf.updatePAMSubsProfile(pamAccountInfoDTO, persisterTxn);
            }
        }
        this.provClientIntf.updatePAMEtag(pamAccId, persisterTxn);
        knLogger.exit(methodName,"KnXDMPAMRespDTO responseDTO ", responseDTO);
        return responseDTO;
    }

    public void deleteAllGroups(Collection<Integer> groupIdsList, String xdmsHome, KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "deleteAllGroups(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIdsList :", groupIdsList);
        KnCorpGroupInfoUtil knCorpGroupInfoUtil = new KnCorpGroupInfoUtil();
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            //corpXdmDao.deleteAllGrpHierarchy(groupIdsList, persisterTxn);
            knCorpGroupInfoUtil.deleteAllGroups(groupIdsList, xdmsHome, null, persisterTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "KnProvException occured", e);
            throw e;
        }
        knLogger.info(methodName, " Groups deleted from DB ");
    }

}













