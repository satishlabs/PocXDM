/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMBulkMediator.java
 * Subsystem:  XDMS-BulkFrameWork
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     08/07/2015    8.0
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

import com.kodiak.common.commdto.request.*;
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.common.resources.KnCustomConfigDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.utilities.processinvoker.KnProcessInvokerException;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.bulkfw.processing.pam.KnPAMDispGrpMemChecker;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.*;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDispatchDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnTGSModeChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnFailedCBTxnLogDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubscriberDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpEXDMSNotifyDto;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpAutoPairingResponse;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGrpBasicInfoRespDto;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpInfoResDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpFailedData;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGrpBasicInfoDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;
import com.kodiak.xdms.server.pubmgmt.clientintf.IPubClientIntf;
import com.kodiak.xdms.server.pubmgmt.clientintf.impl.KnPubClientIntf;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubSubsDTO;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnPAMSubsProfInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

import java.util.*;


import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubscrEXDMSNotifyBulkDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubscrEXDMSNotifyDto;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.DISPATCH_TYPE_WEB;
import static com.kodiak.xdms.server.common.resources.KnConstants.WEBDISPATCHER;

import com.kodiak.frameworks.messaging.common.dto.KnMqServiceConfig;
import com.kodiak.utilities.syncgateway.KnManageSyncUserProfileUtil;
import com.kodiak.utilities.syncgateway.dto.KnSubscriberInfoDTO;
import com.kodiak.utilities.syncgateway.dto.KnSyncResponseDTO;
import com.kodiak.xdms.bulkfw.processing.pam.KnPAMMicroServiceCommon;


/**
 * Class which implements all bulk  API's that are supported by the XDM server
 * This class will invoke the server libraries for processing of the requests.
 */
public class KnXDMBulkMediator {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMBulkMediator.class);
    private static final KnXDMBulkMediator instance = new KnXDMBulkMediator();

    //System property name that needs to be set at start-up
    //This is the file-name (full path) that contains the information
    //required for initializing the logger
    IProvClientIntf provClientIntf;
    ICorpClientIntf corpClientIntf;
    IXcapDiffNotifierIntf notifier;
    IPubClientIntf pubClientIntf;
    private KnFeatureSetUtil featureSetUtil;
    private KnGeneralCacheUtil generalCacheUtil;
    private static final int DIFF_SIZE = 40;
    public static final String JOB_GROUP_NAME = "PAM_DISPATCHR_GROUP_JOB";


    private static final String INVALID_DTO_MSG = "Invalid DTO is passed";
    private static final String RECEIVED_DTO_MSG = "Recieved DTO is ";
    private KnJobSchedulerImpl scheduler;
    private KnAuditHelper audit;
    private static final String CORP_AUDIT="4002";
    private KnGenInfoUtil genInfoUtil;
    private KnPAMMicroServiceCommon knPAMMicroServiceCommon= null;
   // private KnUPMJobScheduler upmJobScheduler;
    //private KnXDMCommonMediator commonMediator;



    private KnXDMBulkMediator() {
        provClientIntf = KnProvClientImpl.getInstance();
        corpClientIntf = new KnCorpClientImpl();
        pubClientIntf = new KnPubClientIntf();
        notifier = new KnXcapDiffNotifierImpl();
        scheduler = KnJobSchedulerImpl.getInstance();
        featureSetUtil = KnFeatureSetUtil.getInstance();
        audit =  KnAuditHelper.getAuditLogger(CORP_AUDIT);
        genInfoUtil = KnGenInfoUtil.getInstance();
        knPAMMicroServiceCommon=KnPAMMicroServiceCommon.getInstance();
        generalCacheUtil = KnGeneralCacheUtil.getInstance();
      //  upmJobScheduler = KnUPMJobScheduler.getInstance();
        //commonMediator = KnXDMCommonMediator.getInstance();
    }


    public static KnXDMBulkMediator getInstance() {
        knLogger.debug("getInstance()", "Returning instance", instance);
        return instance;
    }


    /**
     * Method which creates the Subscriber Profile  and populates to the response
     * commdto (between wrapper and the Server) and sends the response back to the
     * client (SOAP client etc.) through messaging fw MQ.
     *
     * @param requestDTO   IXDMRequestDTO
     * @param persisterTxn KnPersisterTxn
     * @return IXDMResponseDTO Object
     */
    public IXDMResponseDTO createSubscribers(IXDMRequestDTO requestDTO, boolean isFirstBatch, KnPersisterTxn persisterTxn) {
        final String methodName = "createSubscribers(KnMessage,KnPersisterTxn)";
        knLogger.entry(methodName, requestDTO, isFirstBatch);
        KnXDMPAMAccInfoDTO subsProvInputDTO;
        IXDMResponseDTO responseDTO = new KnXDMPAMRespDTO();
        try {

            //conversion of InputDTO
            subsProvInputDTO = this.getRecievedDTO(methodName, requestDTO);
            //verifying InputDTO
            if (isNull(subsProvInputDTO)) {
                knLogger.error(methodName, INVALID_DTO_MSG);
                this.getInvalidDto();
            }

            //populate the Subscriber Prov library DTO
            KnIPBulkSubsProvInfoDTO subsProvInfoDTO = populateBulkSubsProvInfoDTO(subsProvInputDTO);
            KnOPPAMAccInfoDTO pamAccInfoDTO =  provClientIntf.getPAMAccInfoFromId(subsProvInputDTO.getProfileDetails().getPamAccId(), false, persisterTxn);
            subsProvInfoDTO.setExtPamAccId(pamAccInfoDTO.getExtPamAccId());
            //calling the Prov Library
            KnOPCreateSubsInfoDTO respDTO = provClientIntf.createSubscribers(subsProvInfoDTO, persisterTxn);
            knLogger.debug(methodName, "Successfully Created bulk Subscribers Profile with resp DTO - ", respDTO);
            responseDTO = getSuccessResponse(responseDTO);

            //if flag is true then we need to add pam subscriber profile.
            if (isFirstBatch) {
                knLogger.debug(methodName, "checking weather it is create or upgare operation- isUpgare-", subsProvInputDTO.getProfileDetails().isUpgrade());
                if (!subsProvInputDTO.getProfileDetails().isUpgrade()) {
                    knLogger.info(methodName, "creating PAM profile for the first time");
                    createPAMProfile(subsProvInputDTO, persisterTxn);
                }
            }

        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to create PAM account ", e);
            responseDTO = getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            responseDTO = getFailureResponse(responseDTO, e);
        }
        knLogger.exit(methodName, responseDTO);
        return responseDTO;
    }

    private void createPAMProfile(KnXDMPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnProcessInvokerException {
        final String methodName = "createPAMProfile(KnXDMPAMAccInfoDTO)";
        knLogger.entry(methodName, pamAccInfoDTO);

        KnIPPAMAccInfoDTO pamIPAccountInfoDTO = populatePAMAccountDTO(pamAccInfoDTO);
        knLogger.debug(methodName, "KnIPPAMAccInfoDTO pamIPAccountInfoDTO : ", pamIPAccountInfoDTO);

		int corpID = provClientIntf.retrieveCorporationId(pamIPAccountInfoDTO.getProfileDetails().getExtCorpId(), persisterTxn);
		knLogger.debug(methodName, "Internal corpID received-", corpID);
		int pamAccID = pamAccInfoDTO.getProfileDetails().getPamAccId();
		knLogger.debug(methodName, "Pam Acc ID - " + pamAccID);
		pamAccInfoDTO.setPamAccId(pamAccID);
		pamIPAccountInfoDTO.setPamAccId(pamAccID);
		pamIPAccountInfoDTO.getProfileDetails().setCorpID(corpID);
		KnOPProvDTO responseDTO = provClientIntf.createPAMSubsProfile(pamIPAccountInfoDTO, persisterTxn);
		knLogger.debug(methodName, "PAM  Profile KnOPProvDTO respDTO :", responseDTO);

		//Invoking the Custom Invoker for Custom data:
		if (pamAccInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
		    knLogger.debug(methodName, "CUSTOM_ flag is ON.. creating PAM profile");

		    Map<String, Object> customMap = pamAccInfoDTO.getProfileDetails().getCustomParamMap();
		    customMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_PROV_ADD_PAMACCOUNT_OP);
		    customMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
		    pamAccInfoDTO.getProfileDetails().setCustomParamMap(customMap);

		    knLogger.debug(methodName, "CUSTOM_PROV_ADD_PAMACCOUNT_OP ");
		    Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, pamAccInfoDTO);
		    knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);

		    if (customResp instanceof KnXDMRespDTO) {
		        KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customResp;
		        if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
		            knLogger.debug(methodName, "failed to create PAM subs profile ");
		            throw new KnXDMServerException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());
		        }
		        knLogger.debug(methodName, "successfully PAM  subs profile created ");
		    }
		} else {
		    knLogger.info(methodName, "Custom Not installed !!! skipping addition subs profile creation ");
		}

        knLogger.exit(methodName, "PAM profile is created successfully...");
    }

    private void deletePAMProfile(KnXDMPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnProcessInvokerException {
        final String methodName = "deletePAMProfile(KnXDMPAMAccInfoDTO)";
        knLogger.entry(methodName);

        KnIPPAMAccInfoDTO pamIPAccountInfoDTO = populatePAMAccountDTO(pamAccInfoDTO);
		knLogger.debug(methodName, "Deleting PAM profile info");
		KnOPProvDTO respDTO = provClientIntf.deletePAMSubsProfile(pamIPAccountInfoDTO, persisterTxn);
		knLogger.debug(methodName, "PAM  profile info is deleted..", respDTO);

		if (pamAccInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
		    knLogger.info(methodName, "CUSTOM is ON..  deleting PAM Add subs profile info");

		    Map<String, Object> customRequestMap = new HashMap<>();
		    customRequestMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_PROV_CANCEL_PAMACCOUNT_OP);
		    customRequestMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
		    pamAccInfoDTO.getProfileDetails().setCustomParamMap(customRequestMap);

		    Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, pamAccInfoDTO);
		    knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);
		    if (customResp instanceof KnXDMRespDTO) {
		        KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customResp;
		        if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
		            knLogger.debug(methodName, "failed to delete PAM  Add subs profile ");
		            throw new KnProvBOException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());
		        }
		        knLogger.debug(methodName, "successfully PAM  subs profile deleted ");
		    }
		} else {
		    knLogger.info(methodName, "Custom is Not installed !!! skipping addition subs profile deletion  ");
		}

        knLogger.exit(methodName, "Successfully deleted PAM profile");
    }


    /**
     * Method which creates the Subscriber Profile  and populates to the response
     * commdto (between wrapper and the Server) and sends the response back to the
     * client (SOAP client etc.) through messaging fw MQ.
     *
     * @param requestDTO   IXDMRequestDTO
     * @param persisterTxn KnPersisterTxn
     * @return IXDMResponseDTO Object
     */

    public IXDMResponseDTO deleteSubscribersForRollback(IXDMRequestDTO requestDTO, boolean isLastMDN, KnPersisterTxn persisterTxn) {
        final String methodName = "deleteSubscribers(IXDMRequestDTO,KnPersisterTxn)";
        knLogger.entry(methodName, requestDTO);
        KnXDMPAMAccInfoDTO subsProvInputDTO;
        IXDMResponseDTO responseDTO = new KnXDMPAMRespDTO();
        try {
            //conversion of InputDTO
            subsProvInputDTO = this.getRecievedDTO(methodName, requestDTO);
            //verifying InputDTO
            if (isNull(subsProvInputDTO)) {
                knLogger.error(methodName, INVALID_DTO_MSG);
                this.getInvalidDto();
            }
            //populate the Subscriber Prov library DTO
            KnIPBulkSubsProvInfoDTO subsProvInfoDTO = populateBulkSubsProvInfoDTO(subsProvInputDTO);
            //calling the Prov Library
            KnOPBulkDeleteSubsRespDTO respDTO = provClientIntf.deleteSubscribers(subsProvInfoDTO, isLastMDN, persisterTxn);
            knLogger.debug(methodName, "Successfully deleted  bulk Subscribers Profile with resp DTO - ", respDTO);
            // return responseDTO ;
            responseDTO = getSuccessResponse(responseDTO);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to delete PAM Account ", e);
            responseDTO = getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            responseDTO = getFailureResponse(responseDTO, e);
        }
        knLogger.exit(methodName, responseDTO);
        return responseDTO;
    }

    /**
     * Method which creates the Subscriber Profile  and populates to the response
     * commdto (between wrapper and the Server) and sends the response back to the
     * client (SOAP client etc.) through messaging fw MQ.
     *
     * @param requestDTO   IXDMRequestDTO
     * @param persisterTxn KnPersisterTxn
     * @return IXDMResponseDTO Object
     */

    public IXDMResponseDTO deleteSubscriber(IXDMRequestDTO requestDTO, boolean isLastMDN, KnPersisterTxn persisterTxn) {
        final String methodName = "deleteSubscriber(IXDMRequestDTO, isLastMDN, KnPersisterTxn)";
        knLogger.entry(methodName, requestDTO, isLastMDN);
        KnXDMPAMAccInfoDTO subsProvInputDTO;
        IXDMResponseDTO responseDTO = new KnXDMPAMRespDTO();
        Map<String,String>actFSMap= new HashMap<String,String>();
        Map<String ,Boolean> xcapCapabilityMap= new HashMap<String ,Boolean>();
        KnIPCorpContactDTO contactDTO =null;
        KnCorpInfoResDTO respDto =null;
        KnOPSubsProfileInfoDTO subsProfileInfoDTO= null;
        int corpId =0;
        List<String> mdns= null;
        try {
            //conversion of InputDTO
            subsProvInputDTO = this.getRecievedDTO(methodName, requestDTO);
            //verifying InputDTO
            if (isNull(subsProvInputDTO)) {
                knLogger.error(methodName, INVALID_DTO_MSG);
                this.getInvalidDto();
            }
            //populate the Subscriber Prov library DTO
            KnIPBulkSubsProvInfoDTO subsProvInfoDTO = populateBulkSubsProvInfoDTO(subsProvInputDTO);

            //calling the Prov Library
            mdns = subsProvInfoDTO.getMdns();

            if (mdns != null && mdns.size() > 0) {
                //retrieve the subscriber profile
                //this profile info will be useful in getting the type of subscriber
                // if subscriber is public then invoke public library for deleting of all grps/contacts
                // if subscriber is corp then invoke corp library for deleting of owned grps/contacts
                KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
                actFSMap = provClientIntf.fetchActiveFSForBulkMdns(mdns, persisterTxn);
                subscriberInfoDTO.setMdn(mdns.get(0));
                subsProfileInfoDTO = provClientIntf.getSubscriberDetails(subscriberInfoDTO, persisterTxn);

                // delete Additional subscriber in custom
                corpId = subsProfileInfoDTO.getCorpId();
                if (subsProvInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                    KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();

                    KnXDMPAMSubsProfInfoDTO subsProfInfoDTO = new KnXDMPAMSubsProfInfoDTO();
                    Map<String, Object> customReqMap = new HashMap<>();
                    customReqMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_PROV_CANCEL_PAMSUBS_OP);
                    customReqMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customReqMap.put(KnProvConstants.CORP_ID, String.valueOf(corpId));
                    subsProfInfoDTO.setMdns(mdns);
                    subsProfInfoDTO.setCustomParamMap(customReqMap);
                    pamAccInfoDTO.setProfileDetails(subsProfInfoDTO);

                    knLogger.debug(methodName, "CUSTOM_PROV_CANCEL_PAMACCOUNT_OP ");

                    Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, pamAccInfoDTO);
                    knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);
                    if (customResp instanceof KnXDMRespDTO) {
                        KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customResp;

                        if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                            knLogger.debug(methodName, "failed to  deleted custom subscribers ");
                            throw new KnProvBOException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());
                        } else {
                            knLogger.debug(methodName, "successfully deleted custom subscribers ");

                        }
                    }
                }
                //delete subscribers
                knLogger.info(methodName,"MDNs are ==== ", KnGDPRTemplate.mdnList(mdns));
                //delete subscribers
                for (String mdn : mdns) {
                    knLogger.info(methodName,"MDN is === ",KnGDPRTemplate.mdn(mdn));
                    subscriberInfoDTO.setMdn(mdn);
                    subsProfileInfoDTO = provClientIntf.getSubscriberDetails(subscriberInfoDTO, persisterTxn);
                    contactDTO = new KnIPCorpContactDTO();
                    contactDTO.setMdn(mdn);
                    contactDTO.setHierarchyType(subsProfileInfoDTO.getHierarchyType());

                    knLogger.debug(methodName, "invoking the corporate library ");
                    //populating the corporate Library DTO
                    respDto = corpClientIntf.deleteSubscriber(contactDTO, persisterTxn);
                    knLogger.debug("KnCorpInfoResDTO:", respDto);
                    if (respDto.getStatus() != 0) {
                        throw new KnXDMServerException(respDto.getStatusCode(), respDto.getMessage());
                    }
                    knLogger.debug(methodName, "deleted the corporate data for mdn ");

                    //Preparing job
                    Collection<String> deletedMemberList = respDto.getDisabledDispatchMemList();

                    Set<String> allMcsXcapUris =  genInfoUtil.getMCSXCAPRootURIs(persisterTxn);
                    knLogger.debug(methodName, "deletedMemberList - ", deletedMemberList);
                    // If deleted members list id not null and not empty start job to update DISPATCH_GROUP_MEMBER field
                    if (deletedMemberList != null && !deletedMemberList.isEmpty()) {
                        knLogger.debug(methodName, "Calling Dispatch group member job - ");
                        List<KnPAMDispGrpMemChecker> jobList = new ArrayList<>(1);
                        KnPAMDispGrpMemChecker dispGrpMemCheckerJob = new KnPAMDispGrpMemChecker();
                        dispGrpMemCheckerJob.setCorpId(subsProfileInfoDTO.getCorpId());
                        dispGrpMemCheckerJob.setDeletedMembers(deletedMemberList);
                        jobList.add(dispGrpMemCheckerJob);
                        try {
                            knLogger.debug(methodName, "Scheduling the Jobs - ", jobList);
                            scheduler.addRamJob(jobList, JOB_GROUP_NAME);
                        } catch (KnJobSchedulerException e) {
                            knLogger.error(methodName, "KnJobSchedulerException occurred while ",
                                    "submitting Notification Job to Scheduler - ", e);

                        }
                    }

                    // NO need to block notification for NNI PAM subscribers. this is corp notifications
                    //Step:
                    //prepare notification and send to notification mgr
                    Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = prepareNotification(respDto);
                    knLogger.debug(methodName, "Sending notifications - ", xcapDiffList);
                    knLogger.debug(methodName, "Notification status - ", notifier.sendXcapDiffNotifications(xcapDiffList, persisterTxn));

                    knLogger.info(methodName, "Call to LI ");

                    // NO need to block notification for NNI PAM subscribers. this is corp notifications
                    //calling TGSModeChangeNotification
                    sendTGSModeChangeNotification(respDto.getTgsModeChgMap());

                    int publicSubscriptionType = subsProfileInfoDTO.getPublicSubscriptionType();
                    if (publicSubscriptionType == KnConstants.PUBLIC_SUBSCRIPTION_TYPE.PUBLIC.value()) {
                        knLogger.debug(methodName, "invoking the public library");

                        KnIPPubSubsDTO inputDTO = new KnIPPubSubsDTO();
                        inputDTO.setMdn(mdn);
                        knLogger.debug(methodName, "Library Call : InputDTO:  ", inputDTO);
                        pubClientIntf.deleteAllContactsAndGroups(inputDTO, persisterTxn);

                        knLogger.debug(methodName, "deleted public data for mdn ", KnGDPRTemplate.mdn(mdn));
                    }

                    // Delete PAM profile(PAMADDSUBSCR_PROFILEINFO, PAMSUBSCRPROFILEINFO) if it is last MDN,
                    if (isLastMDN) {
                        knLogger.debug(methodName, "Deleting PAM subscriber profile before deleting last MDN");
                        deletePAMProfile(subsProvInputDTO, persisterTxn);
                        String extCorpId=subsProfileInfoDTO.getExtCorpId();
                        int clientType=subsProfileInfoDTO.getSubsClientType();
                        knLogger.debug(methodName, "update etag for NNI subscr before deleting last MDN with extCorpId = ",extCorpId);
                        this.provClientIntf.updateEtagForNNISubscr(extCorpId, clientType, persisterTxn);
                    }

                    //invoking the prov library for Deleting subscriber profile
                    subscriberInfoDTO.setMdn(mdn);
                    KnOPDeleteSubsRespDTO respDTO = provClientIntf.deleteSubscriber(subscriberInfoDTO, persisterTxn);

                    int clientType = subsProvInputDTO.getProfileDetails().getClient_Type();
                    knLogger.debug(methodName, "subscriber Client type - " + clientType);
                    if (clientType != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() && clientType != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                        deleteNotification(respDTO);
                    } else {
                        knLogger.debug(methodName, "Client type is 7/8 dropping notifications");
                    }

                 // IDM user profile delete
                    String idmFqdn = genInfoUtil.getIDMInternalFqdn(persisterTxn);
                    boolean isOIDCApplicable = generalCacheUtil.isOIDCApplicable(subsProfileInfoDTO.getCorpId(), WEBDISPATCHER);
                    String appId = subsProfileInfoDTO.getDispatchType() == DISPATCH_TYPE_WEB ? APP_ID.DISPATCHER.value() : APP_ID.HANDSET_STANDARD.value();
                    if((subsProfileInfoDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_13 || (subsProfileInfoDTO.getClientPVmajorVer() == 0 && isOIDCApplicable))){
                        KnManageSyncUserProfileUtil.getInstance().deleteIDMUserForOIDCMgmt(idmFqdn, appId, mdn);
                    } else if((subsProfileInfoDTO.getClientPVmajorVer() != 0 && subsProfileInfoDTO.getClientPVmajorVer() < PROTOCOL_VERSION_13)
                            || (subsProfileInfoDTO.getClientPVmajorVer() == 0 && !isOIDCApplicable)){
                        KnManageSyncUserProfileUtil.getInstance().deleteIDMUser(idmFqdn, subsProfileInfoDTO.getUserId());
                    }
                    // Added below to support UPM Deletion notify in At&t
                    if(!respDTO.getUserProfileMdns().isEmpty())
                    {
                        KnIPUserProfileDTO ipUserProfileDTO=new KnIPUserProfileDTO();
                        ipUserProfileDTO.setCorpId(String.valueOf(respDTO.getCorpId()));
                        ipUserProfileDTO.setUserProfileMdns(respDTO.getUserProfileMdns());

                        String deleteUpmJsonString = KnCorpCommonInfoUtil.ObjToJson(ipUserProfileDTO);
                        Long transactionId = System.currentTimeMillis();
                        knLogger.debug(methodName, "transactionId - ", transactionId);
                        KnAsyncJobDTO knAsyncJobDTO = knPAMMicroServiceCommon.createJobNotifyDTO(String.valueOf(respDTO.getCorpId()), transactionId.toString()
                                , null, KnConstants.UPM_OPERATION_TYPE.DELETE_USER_PROFILE_MDN.Value(), null,
                                KnConstants.UPM_RESOURCE_TYPE.MDN.Value(), deleteUpmJsonString);
                        knPAMMicroServiceCommon.addJob(knAsyncJobDTO);
                    }else {

                        //If it baseMDN and basedMDN is part of non-mcx group as a last member then we should do cleanup of group from all the user profiles
                        //And sending deleteGroup event for other subsystems
                        Map<Integer, String> delGrpPocHomeMap = respDto.getDelGrpPocHomeMap();
                        if(delGrpPocHomeMap!=null && !delGrpPocHomeMap.isEmpty()) {
                            for(Integer grpId:delGrpPocHomeMap.keySet()) {
                                Long transactionId = System.nanoTime();
                                knLogger.debug(methodName, "transactionId - ", transactionId);
                                KnAsyncJobDTO knAsyncJobDTO = knPAMMicroServiceCommon.createJobNotifyDTO(String.valueOf(respDTO.getCorpId()), transactionId.toString()
                                        , null, KnConstants.UPM_OPERATION_TYPE.DELETE_GROUP_FROM_PROFILE.Value(), String.valueOf(grpId),
                                        KnConstants.UPM_RESOURCE_TYPE.GROUP.Value(), null);
                                knPAMMicroServiceCommon.addJob(knAsyncJobDTO);
                            }

                            List<KnCorpEXDMSNotifyDto> delGrpNotifyList = knPAMMicroServiceCommon.getDeleteGrpMicroSrvNotifyDto(respDto);
                            knLogger.debug(methodName, "Publishing micro service notify - ");
                            knPAMMicroServiceCommon.startNotifyMicroServicesJob(delGrpNotifyList, null);
                        }
                    }
                    if (respDto != null) {
                        boolean notifyStatus = knPAMMicroServiceCommon.sendMCSGRPNotification(respDto, allMcsXcapUris, respDto.getCorpId());
                        knLogger.debug(methodName, "sending Group notification Status: ", notifyStatus);
                    }
                }
            }
            //here:
            //Get the xcap mobile sync flag
            boolean xcapMobileSync = knPAMMicroServiceCommon.getXcapMobileSyncFlag(persisterTxn);
            knLogger.debug(methodName, "xcapMobileSync - ", xcapMobileSync);

            //Get the RMQ configurations
            KnMqServiceConfig rmqInfoDto = knPAMMicroServiceCommon.retrieveServerConfDetails(persisterTxn);
            knLogger.debug(methodName, "rmqInfoDto- ", rmqInfoDto);
            // saving the transaction

            KnSubscriberInfoDTO knSubscriberInfoDTO = new KnSubscriberInfoDTO();
            knLogger.debug(methodName, "actFSMap :  ", actFSMap);

            xcapCapabilityMap=KnGeneralUtil.getFeatureBitForBulk(actFSMap, com.kodiak.common.resources.KnConstants.FEATURE_SET.XCAPCOUCHCLIENT.value());
            knLogger.debug(methodName, "xcapCapabilityMap :  ", xcapCapabilityMap);

            //Storing the list of bucket urls in a list
            Map<Integer, String> bucketUrlList = genInfoUtil.retrievePTXbucketUrls(persisterTxn);
            knLogger.debug(methodName, "bucketUrlList : ",bucketUrlList);
            if(xcapMobileSync) {
                for (Map.Entry<String, Boolean> xcapCapabilityMapEntry : xcapCapabilityMap.entrySet()) {
                    knSubscriberInfoDTO.setMdn(xcapCapabilityMapEntry.getKey());
                    knLogger.debug(methodName, "knSubscriberInfoDTO : mdn", KnGDPRTemplate.mdn(knSubscriberInfoDTO.getMdn()));
                    KnFailedCBTxnLogDTO failedCBTxnLogDTO = new KnFailedCBTxnLogDTO();
                    failedCBTxnLogDTO.setMdn(knSubscriberInfoDTO.getMdn());
                    genInfoUtil.deleteCBTxnFailLog(failedCBTxnLogDTO, persisterTxn);
                    for (Map.Entry<Integer, String> entry : bucketUrlList.entrySet()) {
                        List<String> bucketUrl = new ArrayList<>();
                        Integer curClusterId = entry.getKey();
                        knLogger.debug(methodName, "Updating/deleting profile in clusterId - ", curClusterId);
                        bucketUrl.add(entry.getValue());
                        //REST call for profile creation starts here.
                        KnSyncResponseDTO knSyncResponseDTO = KnManageSyncUserProfileUtil.getInstance().deleteSyncDocument(knSubscriberInfoDTO, bucketUrl);
                        knLogger.debug(methodName, "KnSyncResponseDTO status : ", knSyncResponseDTO.getStatus());
                        if (knSyncResponseDTO.getStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                            failedCBTxnLogDTO = new KnFailedCBTxnLogDTO();
                            failedCBTxnLogDTO.setMdn(knSubscriberInfoDTO.getMdn());
                            failedCBTxnLogDTO.setImpactedClusterId(entry.getKey());
                            failedCBTxnLogDTO.setTxnType(KnConstants.CB_PROFILE_TXN_TYPE.PROFILE_DELETE.value());
                            genInfoUtil.insertCBTxnFailLog(failedCBTxnLogDTO, persisterTxn);
                        }
                    }
                }
            }

            if(xcapMobileSync){
                knLogger.debug(methodName, "Publishing micro service notify group - ");
                if(respDto!=null)
                    knPAMMicroServiceCommon.startNotifyMicroServicesJob(respDto.getChangeLogMap(),corpId , rmqInfoDto);
            }
            if(xcapMobileSync) {
                for (Map.Entry<String, Boolean> xcapCapabilityMapEntry : xcapCapabilityMap.entrySet()) {
                    KnSubscrEXDMSNotifyDto knSubscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
                    knSubscrEXDMSNotifyDto.setCorpid(corpId);
                    knSubscrEXDMSNotifyDto.setMdn(xcapCapabilityMapEntry.getKey());
                    knSubscrEXDMSNotifyDto.setId(com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_SUBSCR.value() + com.kodiak.common.resources.KnConstants.LINE_SAPERATOR + xcapCapabilityMapEntry.getKey());
                    knSubscrEXDMSNotifyDto.setType(com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.DELETE_SUBSCR.value());
                    knSubscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                    knSubscrEXDMSNotifyDto.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());

                    List<KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<KnSubscrEXDMSNotifyDto>();
                    notifyDtoList.add(knSubscrEXDMSNotifyDto);
                    knLogger.debug(methodName, "notifyDtoList :", notifyDtoList);
                    knLogger.info(methodName, "Publishing micro service notify for user- ");
                    knPAMMicroServiceCommon.startNotifyMicroServicesJob(notifyDtoList, rmqInfoDto);

                }
            }
            
            
            // return responseDTO ;
            responseDTO = getSuccessResponse(responseDTO);
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            responseDTO = getFailureResponse(responseDTO, e);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to delete PAM Account ", e);
            responseDTO = getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            responseDTO = getFailureResponse(responseDTO, e);
        }
        knLogger.exit(methodName, responseDTO);

        return responseDTO;
    }


    /**
     * method to update the Service auth status of the Subscriber
     * Re-activate or De-activate the subscriber.
     *
     * @param requestDTO   IXDMRequestDTO
     * @param persisterTxn KnPersisterTxn
     * @return KnOPProvDTO
     */
    public IXDMResponseDTO changeServiceAuthStatus(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) {

        final String methodName = "changeServiceAuthStatus(IXDMRequestDTO,KnPersisterTxn)";
        knLogger.entry(methodName, requestDTO);
        KnXDMPAMAccInfoDTO subsProvInputDTO;
        IXDMResponseDTO responseDTO = new KnXDMPAMRespDTO();
        boolean syncdisabled=true;
        Map<String ,Boolean> xcapCapabilityMap= new HashMap<String ,Boolean>();
        try {
            //conversion of InputDTO
            subsProvInputDTO = this.getRecievedDTO(methodName, requestDTO);
            //verifying InputDTO
            if (isNull(subsProvInputDTO)) {
                knLogger.error(methodName, INVALID_DTO_MSG);
                this.getInvalidDto();
            }
            //populate the Subscriber Prov library DTO
            KnIPBulkSubsProvInfoDTO subsProvInfoDTO = populateBulkSubsProvInfoDTO(subsProvInputDTO);
            //calling the Prov Library
            KnOPBulkChgAuthStatusRespDTO respDTO = provClientIntf.changeServiceAuthStatuses(subsProvInfoDTO, persisterTxn);
            knLogger.debug(methodName, "Successfully changeServiceAuthStatus for bulk Subscribers Profile with resp DTO - ", respDTO);

            KnXcapDiffNotifyDTO xcapDiffNotifyDTO;
            List<KnOPDirChgDTO> dirChgDTOs = respDTO.getDirChgDTOs();

            int serviceAuthStatus = subsProvInfoDTO.getServiceAuthStatus();
            int i = 0;
            knLogger.debug(methodName, "mdn length :", subsProvInfoDTO.getMdns().size());
            knLogger.debug(methodName, "dirChgDTOs :", dirChgDTOs.size());
            List<KnXcapDiffNotifyDTO> xcapDiffList = new ArrayList<>();
            for (String mdn : subsProvInfoDTO.getMdns()) {
                // if it is zero we are not going to send notifications
                if (dirChgDTOs.size() != 0) {
                    xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();

                    if (serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value() || serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value()) {
                        xcapDiffNotifyDTO.setDeRegisterNotify(true);
                        KnDeRegisterNotifyDTO deRegisterNotifyDTO = new KnDeRegisterNotifyDTO();
                        deRegisterNotifyDTO.setMdn(mdn);
                        deRegisterNotifyDTO.setPocHome(dirChgDTOs.get(i).getPocHome());
                        deRegisterNotifyDTO.setPresenceHome(dirChgDTOs.get(i).getPresenceHome());
                        deRegisterNotifyDTO.setAction(KnConstants.MESSAGE_TYPE.USER_DEACTIVATE.value());
                        xcapDiffNotifyDTO.setReason(KnConstants.REASON.USER_DEACTIVATE.value());
                        xcapDiffNotifyDTO.setDeRegisterNotifyDTO(deRegisterNotifyDTO);
                    }
                    Collection<KnXcapDiffDocDTO> xcapDocList = new ArrayList<>();
                    ArrayList<KnOPDocChgDTO> chgDocList = (ArrayList<KnOPDocChgDTO>) dirChgDTOs.get(i).getDocChgDTO();
                    for (KnOPDocChgDTO chgDTO : chgDocList) {
                        KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
                        xcapDiffDocDTO.setDocChangeType(chgDTO.getDocumentChgType());
                        xcapDiffDocDTO.setDocEtag(chgDTO.getNewEtag());
                        xcapDiffDocDTO.setDocumentSelector(chgDTO.getDocUri());
                        xcapDiffDocDTO.setVideoPermission(chgDTO.getVideoPermission());
                        xcapDocList.add(xcapDiffDocDTO);
                    }

                    xcapDiffNotifyDTO.setDocDiffObj(xcapDocList);
                    xcapDiffNotifyDTO.setDirNewEtag(dirChgDTOs.get(i).getDirNewEtag());
                    xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTOs.get(i).getDirPrevEtag());
                    xcapDiffNotifyDTO.setDirURI(dirChgDTOs.get(i).getDirUri());
                    xcapDiffNotifyDTO.setXcapRootUri(dirChgDTOs.get(i).getXcapRootURI());
                    xcapDiffNotifyDTO.setProtocolVersion(dirChgDTOs.get(i).getProtoVersion());
                    xcapDiffNotifyDTO.setPocHome(respDTO.getPocServerHome());
                    xcapDiffNotifyDTO.setPresenceHome(respDTO.getPresenceServerHome());

                    xcapDiffList.add(xcapDiffNotifyDTO);
                    i++;
                }
            }

            notifier.setMaxNotfnsPerJob(KnProvConstants.DEFAULT_NOTIFY_SIZE);
            boolean status = notifier.sendXcapDiffNotifications(xcapDiffList);
            knLogger.info(methodName, "Notification status - ", status);

            //Get the xcap mobile sync flag
            boolean xcapMobileSync = knPAMMicroServiceCommon.getXcapMobileSyncFlag(persisterTxn);
            knLogger.debug(methodName, "xcapMobileSync - ", xcapMobileSync);

            //Get the RMQ configurations
            KnMqServiceConfig rmqInfoDto = knPAMMicroServiceCommon.retrieveServerConfDetails(persisterTxn);
            knLogger.debug(methodName, "rmqInfoDto- ", rmqInfoDto);

            syncdisabled=true;
            if(serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value())
                syncdisabled=false;

            KnSubscriberInfoDTO knSubscriberInfoDTO = new KnSubscriberInfoDTO();
            Map<String,String>actFSMap= respDTO.getActiveFSMap2();
            knLogger.debug(methodName, "[ actFSMap ]", actFSMap,"[ syncdisabled ]",syncdisabled);


            xcapCapabilityMap=KnGeneralUtil.getFeatureBitForBulk(actFSMap, com.kodiak.common.resources.KnConstants.FEATURE_SET.XCAPCOUCHCLIENT.value());
            knLogger.debug(methodName, "xcapCapabilityMap :  ", xcapCapabilityMap);

            //Storing the list of bucket urls in a list
            Map<Integer, String> bucketUrlList = genInfoUtil.retrievePTXbucketUrls(persisterTxn);
            knLogger.debug(methodName, "bucketUrlList : ",bucketUrlList);

            for (Map.Entry<String, Boolean> xcapCapabilityMapEntry : xcapCapabilityMap.entrySet())
            {
            if(xcapCapabilityMapEntry.getValue()){

                knSubscriberInfoDTO.setMdn(xcapCapabilityMapEntry.getKey());
                knSubscriberInfoDTO.setDisabled(syncdisabled);
                knLogger.debug(methodName, "knSubscriberInfoDTO :[ mdn]",KnGDPRTemplate.mdn(knSubscriberInfoDTO.getMdn()),
                                           "[ syncDisabled ]",knSubscriberInfoDTO.isDisabled());
                KnFailedCBTxnLogDTO failedCBTxnLogDTO = new KnFailedCBTxnLogDTO();
                failedCBTxnLogDTO.setMdn(knSubscriberInfoDTO.getMdn());
                genInfoUtil.deleteCBTxnFailLog(failedCBTxnLogDTO, persisterTxn);
                    for(Map.Entry<Integer, String> entry : bucketUrlList.entrySet()) {
                        List<String>  bucketUrl = new ArrayList<>();
                        Integer curClusterId = entry.getKey();
                            knLogger.debug(methodName, "Updating/deleting profile in clusterId - ", curClusterId);
                            bucketUrl.add(entry.getValue());
                            //REST call for profile creation starts here.
                            KnSyncResponseDTO knSyncResponseDTO = KnManageSyncUserProfileUtil.getInstance().updateSyncDocument(knSubscriberInfoDTO, bucketUrl);
                            knLogger.debug(methodName, "KnSyncResponseDTO status : ", knSyncResponseDTO.getStatus());
                            if (knSyncResponseDTO.getStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()){
                                failedCBTxnLogDTO = new KnFailedCBTxnLogDTO();
                                failedCBTxnLogDTO.setMdn(knSubscriberInfoDTO.getMdn());
                                failedCBTxnLogDTO.setImpactedClusterId(entry.getKey());
                                failedCBTxnLogDTO.setTxnType(KnConstants.CB_PROFILE_TXN_TYPE.PROFILE_ENABLE.value());
                                if(knSubscriberInfoDTO.isDisabled()) {
                                    failedCBTxnLogDTO.setTxnType(KnConstants.CB_PROFILE_TXN_TYPE.PROFILE_DISABLE.value());
                                }
                                genInfoUtil.insertCBTxnFailLog(failedCBTxnLogDTO, persisterTxn);
                            }
                        }
                    }
                }
           knLogger.info(methodName,"Exits :change auth status sync doc creation!");
            //notification services starts
           if(xcapMobileSync ){

                KnSubscrEXDMSNotifyBulkDTO subscrEXDMSNotifyBulkDTO=new KnSubscrEXDMSNotifyBulkDTO();
                List<KnSubscrEXDMSNotifyBulkDTO> knSubscrEXDMSNotifyBulkDTOList=new ArrayList<KnSubscrEXDMSNotifyBulkDTO>();

               subscrEXDMSNotifyBulkDTO.setId(com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.CHANGE_SERVICE_AUTH_STATUS.value() + KnConstants.LINE_SAPERATOR + respDTO.getMdn());
               subscrEXDMSNotifyBulkDTO.setType(com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.CHANGE_SERVICE_AUTH_STATUS.value());
               subscrEXDMSNotifyBulkDTO.setVer(MICROSERVICE_NOTIFY_DOC_VER);
               subscrEXDMSNotifyBulkDTO.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());

                List<KnSubscrEXDMSNotifyDto> notifyDtoList= new ArrayList<KnSubscrEXDMSNotifyDto>();
                knLogger.debug(methodName,"KnSubscrEXDMSNotifyBulkDTO Before Entring For Loop :",subscrEXDMSNotifyBulkDTO);
                for (Map.Entry<String, Boolean> xcapCapabilityMapEntry : xcapCapabilityMap.entrySet())
                {
                    if(xcapCapabilityMapEntry.getValue()){
                        KnSubscrEXDMSNotifyDto subscrEXDMSNotifyDTO=new KnSubscrEXDMSNotifyDto();
                        subscrEXDMSNotifyDTO.setMdn(xcapCapabilityMapEntry.getKey());
                        subscrEXDMSNotifyDTO.setCorpid(respDTO.getCorpId());
                        subscrEXDMSNotifyDTO.setAuthStatus(serviceAuthStatus);
                        notifyDtoList.add(subscrEXDMSNotifyDTO);
                    }
                }
                subscrEXDMSNotifyBulkDTO.setMdnList(notifyDtoList);
                knSubscrEXDMSNotifyBulkDTOList.add(subscrEXDMSNotifyBulkDTO);

                knLogger.debug(methodName,"knSubscrEXDMSNotifyBulkDTOList :",knSubscrEXDMSNotifyBulkDTOList);

               knLogger.debug(methodName, "Publishing micro service notify for Change Auth status - ");
               knPAMMicroServiceCommon.startNotifyMicroServicesJob(knSubscrEXDMSNotifyBulkDTOList, rmqInfoDto);

            }
            //populate the response DTO
            //checking if the request is to deactivate or Provisioned subscriber
            //if the request is deactivate subscriber then populate the deactivation notification dto
            responseDTO = getSuccessResponse(responseDTO);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to Change service auth status of Subscriber ", e);
            responseDTO = getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            responseDTO = getFailureResponse(responseDTO, e);
        }
        knLogger.exit(methodName, responseDTO);

        return responseDTO;
    }

    private KnIPBulkSubsProvInfoDTO populateBulkSubsProvInfoDTO(KnXDMPAMAccInfoDTO pamAccInfoDTO) {
        final String methodName = "populateBulkSubsProvInfoDTO(KnXDMPAMAccInfoDTO)";
        knLogger.entry(methodName, pamAccInfoDTO);

        KnIPBulkSubsProvInfoDTO subsProvInfoDTO = new KnIPBulkSubsProvInfoDTO();
        KnXDMPAMSubsProfInfoDTO pamSubsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
        //populate PAM subs profile details
        subsProvInfoDTO.setPamAccId(pamSubsProfInfoDTO.getPamAccId());
        subsProvInfoDTO.setProfileId(pamSubsProfInfoDTO.getProfileId());
        subsProvInfoDTO.setProfileName(pamSubsProfInfoDTO.getProfileName());
        subsProvInfoDTO.setSubscriberFS2(pamSubsProfInfoDTO.getSubscriberFS2());
        subsProvInfoDTO.setPubSubsType(pamSubsProfInfoDTO.getPubSubsType());
        subsProvInfoDTO.setCorpSubsType(pamSubsProfInfoDTO.getCorpSubsType());
        subsProvInfoDTO.setClient_Type(pamSubsProfInfoDTO.getClient_Type());
        subsProvInfoDTO.setExtCorpId(pamSubsProfInfoDTO.getExtCorpId());
        subsProvInfoDTO.setCorpName(pamSubsProfInfoDTO.getCorpName());
        subsProvInfoDTO.setServiceAuthStatus(pamSubsProfInfoDTO.getServiceAuthStatus());

        subsProvInfoDTO.setMdns(pamAccInfoDTO.getProfileDetails().getMdns());
        subsProvInfoDTO.setProvFSMap(pamAccInfoDTO.getFeatureBitInfoMap());
        subsProvInfoDTO.setPkgIdMap(pamSubsProfInfoDTO.getPkgIdMap());
        //get custom details
        subsProvInfoDTO.setCustomParamMap(pamSubsProfInfoDTO.getCustomParamMap());
        subsProvInfoDTO.setExtPamAccId(pamAccInfoDTO.getBillingNumber());
        subsProvInfoDTO.setHierarchyType(pamAccInfoDTO.getHierarchyType());
        subsProvInfoDTO.setLicenseType(pamAccInfoDTO.getProfileDetails().getLicenseType());
        subsProvInfoDTO.setFirstNetIndicator(pamAccInfoDTO.getProfileDetails().getFirstNetIndicator());
        knLogger.exit(methodName, subsProvInfoDTO);
        return subsProvInfoDTO;
    }

    /* method to populate the Library Input DTO
    *
    * @param pamAccInfoDTO KnXDMPAMAccInfoDTO
    * @return KnIPPAMAccInfoDTO Library input DTO
    */
    public KnIPPAMAccInfoDTO populatePAMAccountDTO(KnXDMPAMAccInfoDTO pamAccInfoDTO) {
        String methodName = "populatePAMAccountDTO(KnXDMPAMAccInfoDTO)";
        knLogger.debug(methodName, "XDM Subs PAM Account Info DTO - ", pamAccInfoDTO);

        KnIPPAMAccInfoDTO pamAccountInfoDTO = new KnIPPAMAccInfoDTO();
        pamAccountInfoDTO.setBillingName(pamAccInfoDTO.getBillingName());
        pamAccountInfoDTO.setBillingMdn(pamAccInfoDTO.getBillingNumber());
        pamAccountInfoDTO.setExtPamAccId(pamAccInfoDTO.getBillingNumber());
        pamAccInfoDTO.setOldBillingNumber(pamAccInfoDTO.getOldBillingNumber());
        pamAccountInfoDTO.setTotalNoOfLines(pamAccInfoDTO.getTotalNoOfLines());
        pamAccountInfoDTO.setSubscriberCount(pamAccInfoDTO.getSubsCount());
        pamAccountInfoDTO.setHierarchyType(pamAccInfoDTO.getHierarchyType());

        KnPAMSubsProfInfoDTO pamSubsProfInfoDTO = new KnPAMSubsProfInfoDTO();
        //populate PAM subs profile details
        if (pamAccInfoDTO.getProfileDetails() != null) {
            KnXDMPAMSubsProfInfoDTO xdmpamSubsProfInfoDTO = pamAccInfoDTO.getProfileDetails();
            knLogger.debug(methodName, "KnXDMPAMSubsProfInfoDTO pamAccInfoDTO:", pamAccInfoDTO);
            pamSubsProfInfoDTO.setProfileName(xdmpamSubsProfInfoDTO.getProfileName());
            pamSubsProfInfoDTO.setSubscriberFS2(xdmpamSubsProfInfoDTO.getSubscriberFS2());
            pamSubsProfInfoDTO.setPubSubsType(xdmpamSubsProfInfoDTO.getPubSubsType());
            pamSubsProfInfoDTO.setCorpSubsType(xdmpamSubsProfInfoDTO.getCorpSubsType());
            pamSubsProfInfoDTO.setClient_Type(xdmpamSubsProfInfoDTO.getClient_Type());
            pamSubsProfInfoDTO.setExtCorpId(xdmpamSubsProfInfoDTO.getExtCorpId());
            pamSubsProfInfoDTO.setCorpName(xdmpamSubsProfInfoDTO.getCorpName());
            pamSubsProfInfoDTO.setServiceAuthStatus(xdmpamSubsProfInfoDTO.getServiceAuthStatus());
            pamSubsProfInfoDTO.setPamAccId(xdmpamSubsProfInfoDTO.getPamAccId());
            pamSubsProfInfoDTO.setProfileId(xdmpamSubsProfInfoDTO.getProfileId());
            pamSubsProfInfoDTO.setEmail(xdmpamSubsProfInfoDTO.getEmailAddress());
            pamSubsProfInfoDTO.setImei(xdmpamSubsProfInfoDTO.getIMEI());
            pamSubsProfInfoDTO.setPkgIdMap(xdmpamSubsProfInfoDTO.getPkgIdMap());
            pamSubsProfInfoDTO.setProvFSMap(pamAccInfoDTO.getFeatureBitInfoMap());
            //get custom details
            pamSubsProfInfoDTO.setCustomParamMap(xdmpamSubsProfInfoDTO.getCustomParamMap());
            pamSubsProfInfoDTO.setLicenseType(xdmpamSubsProfInfoDTO.getLicenseType());
            pamSubsProfInfoDTO.setFirstNetIndicator(xdmpamSubsProfInfoDTO.getFirstNetIndicator());
            pamAccountInfoDTO.setProfileDetails(pamSubsProfInfoDTO);
        }
        knLogger.debug(methodName, "KnIPPAMAccInfoDTO  - ", pamAccountInfoDTO);
        return pamAccountInfoDTO;
    }

    public IXDMResponseDTO getFailureResponse(IXDMResponseDTO respDTO, Exception e) {
        final String methodName = "getFailureResponse(IXDMResponseDTO, Exception)";
        knLogger.entry(methodName, respDTO);
        if (e instanceof KnException) {
            respDTO.setResponseCode(((KnException) e).getErrorCode());
            respDTO.setResponseMessage(((KnException) e).getErrorMessage());
            respDTO.setResponseStatus(com.kodiak.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value());
        } else {
            respDTO.setResponseCode(KnProvConstants.ERROR_CODE_INTERNAL_ERROR);
            respDTO.setResponseMessage(e.getMessage());
            respDTO.setResponseStatus(com.kodiak.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value());
        }
        knLogger.exit(methodName, respDTO);
        return respDTO;
    }


    public IXDMResponseDTO getSuccessResponse(IXDMResponseDTO respDTO) {
        final String methodName = "getSuccessResponse(IXDMResponseDTO)";
        knLogger.entry(methodName);
        respDTO.setResponseCode(KnProvConstants.SUCCESS_CODE);
        respDTO.setResponseMessage("SUCCESSFULLY executed the operation");
        respDTO.setResponseStatus(com.kodiak.common.resources.KnConstants.RESPONSE_STATUS.SUCCESS.value());
        knLogger.exit(methodName, respDTO);
        return respDTO;
    }


    public void deleteNotification(KnOPDeleteSubsRespDTO provRespDTO) {
        final String methodName = "deleteNotification(KnOPDeleteSubsRespDTO)";
        knLogger.entry(methodName);
        //sending the notification
        //populate the KnXcapDiffNotifyDTO
        // Notification requires the following params
        // 1. Dir Uri, 2. Dir prev etag,
        KnXcapDiffNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();
        KnOPDirChgDTO dirChgDTO = provRespDTO.getDirChgDTO();
        xcapDiffNotifyDTO.setDeRegisterNotify(true);
        KnDeRegisterNotifyDTO deRegisterNotifyDTO = new KnDeRegisterNotifyDTO();
        deRegisterNotifyDTO.setMdn(provRespDTO.getMdn());
        deRegisterNotifyDTO.setPocHome(provRespDTO.getPocServerHome());
        deRegisterNotifyDTO.setPresenceHome(provRespDTO.getPresenceServerHome());
        deRegisterNotifyDTO.setAction(KnConstants.MESSAGE_TYPE.USER_DELETE.value());
        xcapDiffNotifyDTO.setDeRegisterNotifyDTO(deRegisterNotifyDTO);


        xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
        xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
        xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
        xcapDiffNotifyDTO.setReason(KnConstants.REASON.USER_DELETE.value());
        xcapDiffNotifyDTO.setProtocolVersion(dirChgDTO.getProtoVersion());
        xcapDiffNotifyDTO.setPocHome(provRespDTO.getPocServerHome());
        xcapDiffNotifyDTO.setPresenceHome(provRespDTO.getPresenceServerHome());
        knLogger.debug(methodName, "Notification DTO generated - ", xcapDiffNotifyDTO);

        KnXcapDiffNotifier xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
        knLogger.exit(methodName, "Successfully sent the notification", notifier.sendXcapDiffNotifications(xcapDiffNotifyDTO));

    }

    private Collection<KnXcapDiffDirChgNotifyDTO> prepareNotification(KnCorpResponseDTO respDto) {
        final String methodName = "prepareNotifications(KnCorpResponseDTO)";
        knLogger.entry(methodName, respDto);
        Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = new ArrayList<>();
        Map<String, KnOPDirChgDTO> changeLogMap = respDto.getChangeLogMap();
        if (changeLogMap != null) {
            for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                KnOPDirChgDTO dirChgDTO = entry.getValue();
                List<KnXcapDiffDocDTO> diffDocList = new ArrayList<>();
                Collection<KnOPDocChgDTO> doclist = dirChgDTO.getDocChgDTO();
                if (doclist != null) {
                    for (KnOPDocChgDTO docDto : doclist) {
                        KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
                        xcapDiffDocDTO.setDocumentSelector(docDto.getDocUri());
                        xcapDiffDocDTO.setDocUri(docDto.getEntryUri());
                        xcapDiffDocDTO.setDocChangeType(docDto.getDocumentChgType());
                        xcapDiffDocDTO.setDocEtag(docDto.getNewEtag());
                        xcapDiffDocDTO.setVideoPermission(docDto.getVideoPermission());
                        //Added the setting of the parameters needed for the xcap doc diff notifications
                        Collection<KnSubscriberDTO> addedContactList = docDto.getAddedContactList();
                        int addContLstSize = 0;
                        if (addedContactList != null) {
                            addContLstSize = addedContactList.size();
                        }
                        Collection<String> deletedContactList = docDto.getRemovedContactList();
                        int delContLstSize = 0;
                        if (deletedContactList != null) {
                            delContLstSize = deletedContactList.size();
                        }
                        if (addContLstSize + delContLstSize <= DIFF_SIZE) {
                            if (KnBulkFwConstants.CORP_BROADCAST_GROUP_TYPE != docDto.getGroupType()) {
                                xcapDiffDocDTO.setAddedContactList(addedContactList);
                                xcapDiffDocDTO.setRemovedContactList(deletedContactList);
                            }
                        }
                        xcapDiffDocDTO.setGroupName(docDto.getGroupName());
                        xcapDiffDocDTO.setGroupMemCount(docDto.getGroupMemCount());
                        Collection<KnSubscriberDTO> addedGrpMemList = docDto.getAddedGroupMembers();
                        int addGrpMemLstSize = 0;
                        if (addedGrpMemList != null) {
                            addGrpMemLstSize = addedGrpMemList.size();
                        }
                        Collection<String> deletedGrpMemList = docDto.getRemovedGroupMembers();
                        int delGrpMemLstSize = 0;
                        if (deletedGrpMemList != null) {
                            delGrpMemLstSize = deletedGrpMemList.size();
                        }
                        Collection<KnSubscriberDTO> modGrpMemList = docDto.getModifiedGrpMembers();
                        int modGrpMemLstSize = 0;
                        if (modGrpMemList != null) {
                            modGrpMemLstSize = modGrpMemList.size();
                        }
                        if (addGrpMemLstSize + delGrpMemLstSize + modGrpMemLstSize <= DIFF_SIZE) {
                            if (KnBulkFwConstants.CORP_BROADCAST_GROUP_TYPE != docDto.getGroupType()) {
                                xcapDiffDocDTO.setAddedGroupMembers(addedGrpMemList);
                                xcapDiffDocDTO.setRemovedGroupMembers(deletedGrpMemList);
                                xcapDiffDocDTO.setModifiedGrpMembers(modGrpMemList);
                            }
                        }
                        Collection<KnSubscriberDTO> modContactList = docDto.getModifiedContactMembers();
                        int modContLstSize = 0;
                        if (modContactList != null) {
                            modContLstSize = modContactList.size();
                        }
                        if (modContLstSize <= DIFF_SIZE) {
                            if (KnBulkFwConstants.CORP_BROADCAST_GROUP_TYPE != docDto.getGroupType()) {
                                xcapDiffDocDTO.setModifiedContactList(docDto.getModifiedContactMembers());
                            }
                        }
                        xcapDiffDocDTO.setPrevDocEtag(docDto.getPrevEtag());
                        diffDocList.add(xcapDiffDocDTO);
                    }
                }
                KnXcapDiffDirChgNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffDirChgNotifyDTO();
                xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
                Collections.sort(diffDocList);
                xcapDiffNotifyDTO.setDocDiffObj(diffDocList);
                xcapDiffNotifyDTO.setDirNewEtag(dirChgDTO.getDirNewEtag());
                xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
                xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
                xcapDiffNotifyDTO.setPocHome(dirChgDTO.getPocHome());
                xcapDiffNotifyDTO.setPresenceHome(dirChgDTO.getPresenceHome());
                xcapDiffNotifyDTO.setNotfnCapability(dirChgDTO.isNotfnCapability());
                xcapDiffNotifyDTO.setProtocolVersion(dirChgDTO.getProtoVersion());
                xcapDiffList.add(xcapDiffNotifyDTO);
            }
        }
        knLogger.exit(methodName, xcapDiffList);
        return xcapDiffList;
    }

    public List<String> retrievePAMAccountMDNs(int pamAccId, int fetchSize, String startMdn, KnPersisterTxn persisterTxn) throws KnProvException {
        return provClientIntf.retrievePAMAccountMDNs(pamAccId, fetchSize, startMdn, persisterTxn);
    }

    public List<String> retrievePAMAccountMDNs(int pamAccId, String startMdn, String endMdn, int fetchSize, KnPersisterTxn persisterTxn) throws KnProvException {
        return provClientIntf.retrievePAMAccountMDNs(pamAccId, startMdn, endMdn, fetchSize, persisterTxn);
    }

    public List<String> retrievePAMAccountMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvException {
        return provClientIntf.retrievePAMAccountMDNs(pamAccId, persisterTxn);
    }

    public List<String> retrievePAMAccountMDNsByInsertionTime(int pamAccId, long insertionTime, KnPersisterTxn persisterTxn) throws KnProvException {
        return provClientIntf.getPAMAccountMdnsByInsertionTime(pamAccId, insertionTime, persisterTxn);
    }


    /**
     * Method which delete the Subscriber Profiles(Licensepacks)  and populates to the response
     * commdto (between wrapper and the Server) and sends the response back to the
     * client (SOAP client etc.) through messaging fw MQ.
     *
     * @param requestDTO   IXDMRequestDTO
     * @param persisterTxn KnPersisterTxn
     * @return IXDMResponseDTO Object
     */

    public IXDMResponseDTO downgradeRatePlan(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) {
        final String methodName = "downgradeRatePlan(IXDMRequestDTO,KnPersisterTxn)";
        knLogger.entry(methodName, requestDTO);
        KnXDMPAMAccInfoDTO subsProvInputDTO;
        IXDMResponseDTO responseDTO = new KnXDMPAMRespDTO();
        try {
            if (requestDTO instanceof KnXDMPAMAccInfoDTO) {
                subsProvInputDTO = (KnXDMPAMAccInfoDTO) requestDTO;
                knLogger.debug(methodName, "received DTO  - ", subsProvInputDTO);
            } else {
                knLogger.error(methodName, "received Invalid DTO  - ");
                return getInvalidDto();
            }
            //populate the Subscriber Prov library DTO
            KnIPBulkSubsProvInfoDTO subsProvInfoDTO = populateBulkSubsProvInfoDTO(subsProvInputDTO);

            //calling corp clean up data
            //populate the Subscriber Prov library DTO
            KnIPCorpPAMSubsDTO ipCorpPAMSubsDTO = new KnIPCorpPAMSubsDTO();
            ipCorpPAMSubsDTO.setPamAccId(subsProvInfoDTO.getPamAccId());
            ipCorpPAMSubsDTO.setExtCorpId(subsProvInfoDTO.getExtCorpId());
            ipCorpPAMSubsDTO.setClientType(subsProvInfoDTO.getClientType());
            ipCorpPAMSubsDTO.setCleanUpMdnLst(subsProvInfoDTO.getMdns());
            ipCorpPAMSubsDTO.setUnUsedMdnCount(subsProvInputDTO.getSubsCount());

            //calling the corp data
            KnCorpResponseDTO respDto = corpClientIntf.cleanCorpData(ipCorpPAMSubsDTO, persisterTxn);
            knLogger.debug(methodName, "Successfully cleanup corp details - ", respDto);

            //populating for public cleanup
            KnIPPubSubsDTO pubSubsDTO = new KnIPPubSubsDTO();
            pubSubsDTO.setClientType(subsProvInfoDTO.getClientType());
            pubSubsDTO.setMdnList(subsProvInfoDTO.getMdns());

            //calling public cleanup
            pubClientIntf.deleteAllContactsAndGroups4ListOfMdns(pubSubsDTO, persisterTxn);
            knLogger.debug(methodName, "Successfully cleanup public details - ");

            //calling the Prov Library
            KnOPBulkDeleteSubsRespDTO subsRespDTO = provClientIntf.deleteSubscribers(subsProvInfoDTO, false, persisterTxn);
            knLogger.debug(methodName, "Successfully deleted  bulk Subscribers Profile with resp DTO - ", subsRespDTO);

            //prepare Notification and send to notification manager for prov data
            for (KnOPDeleteSubsRespDTO deleteSubsRespDTO : subsRespDTO.getDeleteSubsRespDTOs())
                deleteNotification(deleteSubsRespDTO);
            //prepare notification and send to notification mgr for corporate date
            Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = prepareNotification(respDto);
            knLogger.debug(methodName, "Sending notifications - ", xcapDiffList);
            knLogger.debug(methodName, "Notification status - ", notifier.sendXcapDiffNotifications(xcapDiffList, persisterTxn));
            sendTGSModeChangeNotification(respDto.getTgsModeChgMap());

            knLogger.info(methodName, "Call to LI ");
            //KnLIEventHandler.logLI(respDto.getLiEventList());

            // return responseDTO ;
            responseDTO = getSuccessResponse(responseDTO);
        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to delete PAM Account ", e);
            responseDTO = getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            responseDTO = getFailureResponse(responseDTO, e);
        }

        knLogger.exit(methodName, responseDTO);
        return responseDTO;
    }

    private IXDMResponseDTO getInvalidDto() {
        IXDMResponseDTO responseDTO = new KnXDMPAMRespDTO();
        responseDTO.setResponseMessage(INVALID_DTO_MSG);
        responseDTO.setResponseCode(KnProvConstants.ERROR_CODE_INVALID_DTO_PASSED);
        responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
        return responseDTO;
    }

    public boolean sendTGSModeChangeNotification(Map<String, KnTGSModeChgDTO> tgsModeChgDTOMap) {
        final String methodName = "sendTGSModeChangeNotification(Map<String, KnTGSModeChgDTO>)";
        boolean status = false;
        knLogger.entry(methodName, tgsModeChgDTOMap);
        if (tgsModeChgDTOMap != null && !tgsModeChgDTOMap.isEmpty()) {
            List<KnSEHNotifyDTO> xcapDiffList = prepareTSGModeCngNotification(tgsModeChgDTOMap);
            notifier.setMaxNotfnsPerJob(3);
            status = notifier.sendSEHNotifications(xcapDiffList);
        } else {
            knLogger.info(methodName, "Map is empty, Hence skipping SEHNotification");
        }
        knLogger.exit(methodName, status);
        return status;
    }

    private List<KnSEHNotifyDTO> prepareTSGModeCngNotification(Map<String, KnTGSModeChgDTO> tgsModeChgDTOMap) {
        final String methodName = "prepareTSGModeCngNotification(Map<String, KnTGSModeChgDTO>)";
        knLogger.entry(methodName, tgsModeChgDTOMap);
        List<KnSEHNotifyDTO> sehNotifyDTOList = new ArrayList<>();
        for (Map.Entry<String, KnTGSModeChgDTO> entry : tgsModeChgDTOMap.entrySet()) {
            KnTGSModeChgDTO modeChgDTO = entry.getValue();
            KnTGSModeNotifyDTO modeNotifyDTO = new KnTGSModeNotifyDTO();
            modeNotifyDTO.setPocHome(modeChgDTO.getPocHome());
            modeNotifyDTO.setMdn(entry.getKey());
            modeNotifyDTO.setPresenceHome(modeChgDTO.getPresenceHome());
            modeNotifyDTO.setTgsMode(modeChgDTO.getTgsMode());
            modeNotifyDTO.setAction(com.kodiak.xdms.server.common.resources.KnConstants.MESSAGE_TYPE.TGSC_MODE_CHANGE.value());
            KnSEHNotifyDTO sehNotifyDTO = new KnSEHNotifyDTO();
            sehNotifyDTO.setTgsModeNotify(Boolean.TRUE);
            sehNotifyDTO.setTgsModeNotifyDTO(modeNotifyDTO);
            sehNotifyDTOList.add(sehNotifyDTO);

        }
        knLogger.exit(methodName, sehNotifyDTOList);
        return sehNotifyDTOList;
    }

    private KnXDMPAMAccInfoDTO getRecievedDTO(String methodName, IXDMRequestDTO requestDTO) {
        KnXDMPAMAccInfoDTO subsProvInputDTO = null;
        if (requestDTO instanceof KnXDMPAMAccInfoDTO) {
            subsProvInputDTO = (KnXDMPAMAccInfoDTO) requestDTO;
            knLogger.debug(methodName, RECEIVED_DTO_MSG, subsProvInputDTO);
        }
        return subsProvInputDTO;
    }

    private boolean isNull(Object input) {
        return input == null ? Boolean.TRUE : Boolean.FALSE;
    }

    public IXDMResponseDTO updateHierarchy(IXDMRequestDTO requestDTO, boolean isFirstMDN, boolean isLastMDN, KnPersisterTxn persisterTxn) {
        final String methodName = "updateHierarchy(IXDMRequestDTO,boolean,boolean)";
        knLogger.debug(methodName, "ENTRY: requestDTO", requestDTO, " ,isFirstMDN", isFirstMDN, " ,isLastMDN", isLastMDN);
        KnXDMPAMAccInfoDTO subsProvInputDTO = (KnXDMPAMAccInfoDTO) requestDTO;
        String extCorpId = subsProvInputDTO.getProfileDetails().getExtCorpId();
        knLogger.debug(methodName, "extCorpId-", extCorpId);
        KnOPCorpProfileInfoDTO corpProfileInfoDTO;
        KnOPCorpProfileInfoDTO oldCorpProfileInfoDTO;
        KnOPSubsProfileInfoDTO subsProfileInfoDTO;
        KnCustomConfigDTO customConfigDTO;
        KnOPCorpProfileInfoDTO newCorpProfileInfoDTO;
        int newCorpId;
        List<KnOPDirChgDTO> dirChgDTOs;
        List<KnOPUpdateSubsInfoDTO> updateHierarchyRespDTOs;
        IXDMResponseDTO responseDTO = new KnXDMRespDTO();
        List<String> mdns = subsProvInputDTO.getProfileDetails().getMdns();
        knLogger.debug(methodName, "List of Mdns For Update Hierarchy-", KnGDPRTemplate.mdnList(mdns));
        String extBanId = (String) subsProvInputDTO.getProfileDetails().getCustomParamMap().get(KnProvConstants.EXT_BAN_ID);
        String extFanId = (String) subsProvInputDTO.getProfileDetails().getCustomParamMap().get(KnProvConstants.EXT_FAN_ID);
        String fanName = (String) subsProvInputDTO.getProfileDetails().getCustomParamMap().get(KnProvConstants.FAN_NAME);
        String banName = (String) subsProvInputDTO.getProfileDetails().getCustomParamMap().get(KnProvConstants.BAN_NAME);
        int oldCorpId = (Integer) subsProvInputDTO.getProfileDetails().getCustomParamMap().get(KnProvConstants.OLD_CORP_ID);
        String corpName = (String) subsProvInputDTO.getProfileDetails().getCustomParamMap().get(KnProvConstants.CORP_NAME);
        knLogger.debug(methodName, "ENTRY : extBanId", extBanId, " ,extFanId", extFanId, "fanName", fanName, " ,banName", banName, "corpName", corpName);
        int fanId = 0;
        try {
            for (String mdn : mdns) {
                knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn));
                if (isFirstMDN) {

                    knLogger.info(methodName, "FirstMdn check , isFirstMDN = true");
                    //retrieve corporate profile for request extCorpId
                    corpProfileInfoDTO = provClientIntf.retrieveCorporateProfile(extCorpId, persisterTxn);
                    knLogger.debug(methodName, "corpProfileInfoDTO ", corpProfileInfoDTO);

                    if (corpProfileInfoDTO.getCorpId() == 0) {

                        //retrieve subscriber details
                        knLogger.debug(methodName, "corpProfileInfoDTO.getCorpId() == 0 ");
                        KnIPSubscriberInfoDTO subscriberDTO = new KnIPSubscriberInfoDTO();
                        subscriberDTO.setMdn(mdn);
                        knLogger.debug(methodName, "get subscriber details", subscriberDTO);
                        subsProfileInfoDTO = provClientIntf.getSubscriberDetails(subscriberDTO, persisterTxn);
                        knLogger.debug(methodName, "subscriber details response", subsProfileInfoDTO);


                        //retrieve old corporate profile details
                        String oldExtCorpId = subsProfileInfoDTO.getExtCorpId();
                        oldCorpProfileInfoDTO = provClientIntf.retrieveCorporateProfile(oldExtCorpId, persisterTxn);
                        knLogger.debug(methodName, "Got old corporate profile", oldCorpProfileInfoDTO);
                        String pocHome = oldCorpProfileInfoDTO.getPocHome();
                        String xdmsHome = oldCorpProfileInfoDTO.getXDMSHome();
                        knLogger.debug(methodName, "extCorpId-", extCorpId, " ,pocHome-", pocHome, " ,xdmsHome-", xdmsHome);

                        String corpFS2 = null;
                        String opsCorpFS2 = null;
                        corpFS2 = featureSetUtil.getDefFinalCorpFS();
                        knLogger.debug(methodName, "final corpFS :", corpFS2);

                        //get final OpsCorpFeatureSet
                        opsCorpFS2 = featureSetUtil.getDefFinalOpsCorpFS();
                        knLogger.debug(methodName, "final opsCorpFS :", opsCorpFS2);

                        //create new corporate profile with old pocHome
                        KnIPCorpProfileInfoDTO corpProfInfoDTO = new KnIPCorpProfileInfoDTO();
                        corpProfInfoDTO.setExtCorpId(extCorpId);
                        corpProfInfoDTO.setPocHome(pocHome);
                        corpProfInfoDTO.setXDMSHome(xdmsHome);
                        corpProfInfoDTO.setOpsCorpFS2(opsCorpFS2);
                        corpProfInfoDTO.setCorpFS2(corpFS2);
                        corpProfInfoDTO.setCorporateName(corpName);
                        corpProfInfoDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
                        knLogger.debug(methodName, "creating corporate profile", corpProfInfoDTO);
                        newCorpProfileInfoDTO = provClientIntf.createCorpProfile(corpProfInfoDTO, persisterTxn);
                        newCorpId = newCorpProfileInfoDTO.getCorpId();
                        knLogger.debug(methodName, "newCorpId", newCorpId);

                    } else {
                        knLogger.debug(methodName, "Corporate already exists,no need to create");
                    }


                    //call to retrieve the new corpid ,required to set in creating fan
                    //call to get the newcorpid
                    KnOPCorpProfileInfoDTO newCorpProfile = provClientIntf.retrieveCorporateProfile(extCorpId, persisterTxn);
                    newCorpId = newCorpProfile.getCorpId();
                    knLogger.info(methodName, "1.new Corpid-", newCorpId);

                    if (subsProvInputDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                        knLogger.info(methodName, "Custom is On.");

                        //create Fan details
                        knLogger.debug(methodName, "Creating Fan Details");
                        Map<String, Object> customRequestMap = new HashMap<String, Object>();
                        customRequestMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_PROV_CREATE_FAN);
                        customRequestMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                        customRequestMap.put(KnProvConstants.EXT_FAN_ID, extFanId);
                        customRequestMap.put(KnProvConstants.FAN_NAME, fanName);
                        KnXDMSubsProvInfoDTO createFanDTO = new KnXDMSubsProvInfoDTO();
                        createFanDTO.setCorpId(String.valueOf(newCorpId));
                        createFanDTO.setCustomParamMap(customRequestMap);
                        Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, createFanDTO);
                        knLogger.debug(methodName, "Response from the Custom Create Fan - ", customResp);
                        if (customResp instanceof KnXDMRespDTO) {
                            KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customResp;
                            if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                                knLogger.debug(methodName, "failed to Create Fan ");
                                throw new KnProvBOException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());
                            }
                            knLogger.debug(methodName, "successfully created Fan ");
                            knLogger.debug(methodName, "customResponseDTO", customResponseDTO);
                            knLogger.debug(methodName, "customResponseDTO.getCustomParamMap()", customResponseDTO.getCustomParamMap());
                            if (customResponseDTO.getCustomParamMap().containsKey(KnProvConstants.INT_FAN_ID)) {
                                fanId = (Integer) customResponseDTO.getCustomParamMap().get(KnProvConstants.INT_FAN_ID);
                            }
                        }
                        knLogger.debug(methodName, "Retrieved fanId", fanId);


                        //create Ban details
                        knLogger.debug(methodName, "Creating Ban Details");
                        KnXDMSubsProvInfoDTO createBanDTO = new KnXDMSubsProvInfoDTO();
                        Map<String, Object> customRequestBanMap = new HashMap<String, Object>();
                        customRequestBanMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_PROV_CREATE_BAN);
                        customRequestBanMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                        customRequestBanMap.put(KnProvConstants.INT_FAN_ID, fanId);
                        customRequestBanMap.put(KnProvConstants.EXT_BAN_ID, extBanId);
                        customRequestBanMap.put(KnProvConstants.BAN_NAME, banName);
                        customRequestBanMap.put(KnProvConstants.BAN_STATUS, 1);
                        createBanDTO.setCustomParamMap(customRequestBanMap);
                        Object customBanResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, createBanDTO);
                        knLogger.debug(methodName, "Response from Custom Create Ban - ", customBanResp);
                        if (customBanResp instanceof KnXDMRespDTO) {
                            KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customBanResp;
                            if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                                knLogger.debug(methodName, "failed to create BAN_DETAILS ");
                                throw new KnProvBOException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());
                            }
                            knLogger.debug(methodName, "successfully created Ban ");
                        }

                    } else {
                        knLogger.info(methodName, "Custom is Not installed !!! skipping addition subs profile deletion  ");
                    }
                }//end of isFirstMdn

                //call to get the newcorpid
                KnOPCorpProfileInfoDTO newCorpProfile = provClientIntf.retrieveCorporateProfile(extCorpId, persisterTxn);
                newCorpId = newCorpProfile.getCorpId();
                knLogger.info(methodName, "2.new Corpid-", newCorpId);

                //TODO : added
                //retrieve the old profile of the subscriber to get old corpid before its update ,required to update in corp module
                KnIPSubscriberInfoDTO subscriberDTO = new KnIPSubscriberInfoDTO();
                subscriberDTO.setMdn(mdn);
                subsProfileInfoDTO = provClientIntf.getSubscriberDetails(subscriberDTO, persisterTxn);
                int oldCorpIdFromSubscr = subsProfileInfoDTO.getCorpId();
                knLogger.debug(methodName, "old corpid from subscr", oldCorpIdFromSubscr);

                //call the update Hierarchy for each mdn
                KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO = new KnIPBulkSubsProvInfoDTO();
                List<String> mdnList = new ArrayList<>();
                mdnList.add(mdn);
                bulkSubsProvInfoDTO.setMdns(mdnList);
                bulkSubsProvInfoDTO.setCorpID(newCorpId);
                bulkSubsProvInfoDTO.setExtCorpId(extCorpId);
                bulkSubsProvInfoDTO.setCustomParamMap(subsProvInputDTO.getProfileDetails().getCustomParamMap());
                bulkSubsProvInfoDTO.setCorpName(subsProvInputDTO.getProfileDetails().getCorpName());
                bulkSubsProvInfoDTO.setClient_Type(subsProfileInfoDTO.getSubsClientType());
                bulkSubsProvInfoDTO.setAutoPair(subsProvInputDTO.getProfileDetails().isAutoPair());
                bulkSubsProvInfoDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
                knLogger.info(methodName, "Calling updateHierarchy with DTO bulkSubsProvInfoDTO-", bulkSubsProvInfoDTO);
                knLogger.info(methodName, "Calling updateHierarchy with DTO bulkSubsProvInfoDTO-", bulkSubsProvInfoDTO.getMdns());
                updateHierarchyRespDTOs = provClientIntf.updateHierarchy(bulkSubsProvInfoDTO, persisterTxn);
                knLogger.info(methodName, "After update hierarchy in bulk-", updateHierarchyRespDTOs);
                //newCorpId=updateHierarchyRespDTOs.get(0).getCorpId();

                knLogger.info(methodName, "oldCorpId-", oldCorpId, " ,oldCorpIdFromSubscr-", oldCorpIdFromSubscr, " ,newCorpId-", newCorpId);

                //call update subscriber in corporate module
                KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
                //contactDTO.setCorpId(newCorpId);
                contactDTO.setMdn(mdn);
                contactDTO.setNewCorpId(newCorpId);
                contactDTO.setCorpId(oldCorpIdFromSubscr);//
                knLogger.info(methodName, "Before Calling updateSubscriber in corporate module-", contactDTO);
                KnCorpInfoResDTO updateCorpRespDTO = corpClientIntf.updateSubscriber(contactDTO, persisterTxn);
                knLogger.info(methodName, "After Calling updateSubscriber in corporate module-");
                if (KnConstants.RESPONSE_STATUS.FAILURE.value() == updateCorpRespDTO.getStatus()) {
                    knLogger.error(methodName, "Failed to update subscriber in corp ");
                    throw new KnXDMServerException(updateCorpRespDTO.getStatusCode(), updateCorpRespDTO.getMessage());

                }

                //Get the xcap mobile sync flag
                boolean xcapMobileSync = knPAMMicroServiceCommon.getXcapMobileSyncFlag(persisterTxn);
                knLogger.debug(methodName, "xcapMobileSync - ", xcapMobileSync);
                //Get the RMQ configurations
                KnMqServiceConfig rmqInfoDto = knPAMMicroServiceCommon.retrieveServerConfDetails(persisterTxn);
                knLogger.debug(methodName, "rmqInfoDto - ", rmqInfoDto);
                if(xcapMobileSync){
                    knLogger.debug(methodName, "Publishing micro service notify for Group event - ");
                    knPAMMicroServiceCommon.startNotifyMicroServicesJob(updateCorpRespDTO.getChangeLogMap(), newCorpId, rmqInfoDto);
                }

                // enable auto corp autopairing
                Boolean corpAutoPair = updateHierarchyRespDTOs.get(0).getCorpAutoPairing();
                Boolean corpExist = updateHierarchyRespDTOs.get(0).getIsOldCorp();

                if (corpAutoPair != null && corpExist != null) {
                    KnXDMCorpRespDTO corpRespDTO = null;
                    if (corpAutoPair) {
                        knLogger.debug(methodName, "Auot pair is on,  call corp module for autopair");
                        if (corpExist) {
                            knLogger.debug(methodName, "Corp  exist. hence add to  auto pairing");
                            KnXDMCorpSubscInfoRequestDTO contactRequestDTO = new KnXDMCorpSubscInfoRequestDTO();
                            contactRequestDTO.setCorpId(String.valueOf(newCorpId));
                            contactRequestDTO.setSubscriberMdn(subsProfileInfoDTO.getMdn());
                            contactRequestDTO.setName(subsProfileInfoDTO.getNetworkName());
                            contactRequestDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
                            corpRespDTO = this.addToPairingList(contactRequestDTO, persisterTxn);
                        } else {
                            knLogger.debug(methodName, "Corp does exist hence created new. call enable auto pairing");
                            KnXDMCorpInfoRequestDTO corpInfoRequestDTO = new KnXDMCorpInfoRequestDTO();
                            corpInfoRequestDTO.setEnableAutoPair(Boolean.TRUE);
                            corpInfoRequestDTO.setCorpId(String.valueOf(newCorpId));
                            corpInfoRequestDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
                            corpRespDTO = this.updateCorpAutoPairing(corpInfoRequestDTO, persisterTxn);
                        }
                    }
                    knLogger.debug(methodName, "Corp Autopairing response - ", corpRespDTO);
                    if (corpRespDTO.getResponseStatus() != KnProvConstants.SUCCESS) {
                        throw new KnXDMServerException(corpRespDTO.getResponseCode(), corpRespDTO.getResponseMessage());
                    }
                }

                //update corp profile last update time for old and new corp ,needed for cat\
                provClientIntf.updateCorpProfileLastUpdateTime(oldCorpIdFromSubscr, persisterTxn);
                provClientIntf.updateCorpProfileLastUpdateTime(newCorpId, persisterTxn);


                if (isLastMDN) {
                    knLogger.info(methodName, "LastMdnCheck , isLastMDN = true");
                    //update pam subs corpid fom old to new corpid
                    bulkSubsProvInfoDTO.setCorpID(newCorpId);
                    knLogger.debug(methodName, "Before calling update Pam subsprof corpid ,newCorpId-", newCorpId, " ,oldCorpId-", oldCorpIdFromSubscr);
                    provClientIntf.updatePAMSubsProfCorpId(bulkSubsProvInfoDTO, oldCorpIdFromSubscr, persisterTxn);
                    knLogger.debug(methodName, "After calling update Pam subsprof corpid");

                    //delete corp profile for old corpid
                    KnIPCorpProfileInfoDTO delCorpProfileInfoDTO = new KnIPCorpProfileInfoDTO();
                    delCorpProfileInfoDTO.setCorpId(oldCorpIdFromSubscr);
                    provClientIntf.deleteCorporateProfile(delCorpProfileInfoDTO, persisterTxn);
                    knLogger.debug(methodName, "After deleting corporate profile");

                    //update pam subs prof fanid
                    KnXDMSubsProvInfoDTO PamSubsFanUpdateDTO = new KnXDMSubsProvInfoDTO();
                    Map<String, Object> pamSubsFanUpdateMap = new HashMap<String, Object>();
                    pamSubsFanUpdateMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_UPDATE_PAMACCFAN);
                    pamSubsFanUpdateMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    pamSubsFanUpdateMap.put(KnProvConstants.EXT_FAN_ID, extFanId);
                    pamSubsFanUpdateMap.put(KnProvConstants.EXT_BAN_ID, extBanId);
                    PamSubsFanUpdateDTO.setCustomParamMap(pamSubsFanUpdateMap);
                    Object customPamAccFanResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, PamSubsFanUpdateDTO);
                    knLogger.debug(methodName, "Response from the Custom Update PamSubscr Fan ", customPamAccFanResp);


                    //update ban status to unblock
                    KnXDMSubsProvInfoDTO banStatusUnblockDTO = new KnXDMSubsProvInfoDTO();
                    Map<String, Object> banStatusUnblockMap = new HashMap<String, Object>();
                    banStatusUnblockMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_UPDATE_BAN_STATUS);
                    banStatusUnblockMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    banStatusUnblockMap.put(KnProvConstants.BAN_STATUS, 0);
                    banStatusUnblockMap.put(KnProvConstants.EXT_BAN_ID, extBanId);
                    banStatusUnblockDTO.setCustomParamMap(banStatusUnblockMap);
                    Object customBanResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, banStatusUnblockDTO);
                    knLogger.debug(methodName, "Response from Custom Unblock Ban Status - ", customBanResp);


                }
                knLogger.debug(methodName, "outside for loop");

                //send config doc notification
                dirChgDTOs = new ArrayList<>();
                for (KnOPUpdateSubsInfoDTO updateSubsInfoDTO : updateHierarchyRespDTOs) {
                    dirChgDTOs.add(updateSubsInfoDTO.getDirChgDTO());
                }
                List<KnXcapDiffNotifyDTO> xcapDiffList = prepareXcapDiffNotification(dirChgDTOs);
                notifier.setMaxNotfnsPerJob(KnProvConstants.DEFAULT_NOTIFY_SIZE);
                knLogger.debug(methodName, "Notification status - ", notifier.sendXcapDiffNotifications(xcapDiffList));

            }
            responseDTO = getSuccessResponse(responseDTO);
        } catch (KnProvException e) {
            knLogger.error(methodName, "Prov BO  Exception occurred ", e);
            updateBanStatus(extBanId, 0);
            responseDTO = getFailureResponse(responseDTO, e);
        } catch (KnProcessInvokerException e) {
            knLogger.error(methodName, "Process Invoker Exception occurred ", e);
            updateBanStatus(extBanId, 0);
            responseDTO = getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to update Ban hierarchy ", e);
            updateBanStatus(extBanId, 0);
            responseDTO = getFailureResponse(responseDTO, e);
        }
        knLogger.info(methodName, "EXIT : responseDTO ", responseDTO);
        return responseDTO;
    }

    public void updateBanStatus(String extBanId, int status) {
        final String methodName = "updateBanStatus(String)";
        KnPersisterTxn persisterTxn = null;
        try {

            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "Opening the Transaction");
            persisterTxn.open();

            //update ban status to unblock
            KnXDMSubsProvInfoDTO banStatusUnblockDTO = new KnXDMSubsProvInfoDTO();
            Map<String, Object> banStatusUnblockMap = new HashMap<String, Object>();
            banStatusUnblockMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_UPDATE_BAN_STATUS);
            banStatusUnblockMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
            banStatusUnblockMap.put(KnProvConstants.BAN_STATUS, status);
            banStatusUnblockMap.put(KnProvConstants.EXT_BAN_ID, extBanId);
            banStatusUnblockDTO.setCustomParamMap(banStatusUnblockMap);
            Object customBanResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, banStatusUnblockDTO);
            knLogger.debug(methodName, "Response from Custom Unblock Ban Status - ", customBanResp);
            persisterTxn.save();
            knLogger.debug(methodName, "Updated Ban Status to unblock");
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            rollback(persisterTxn);
        } catch (KnProcessInvokerException e) {
            knLogger.error(methodName, "Process invoker exception", e);
            rollback(persisterTxn);
        }
    }

    private void rollback(KnPersisterTxn txn) {
        try {
            knLogger.error("rollback()", "Rolling back transaction");
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)", "Failed to rollback the transaction.");
        }
    }

    private List<KnXcapDiffNotifyDTO> prepareXcapDiffNotification(List<KnOPDirChgDTO> dirChgDTOs) {
        String methodName = "sendNotification(KnOPProvDTO,String)";
        List<KnXcapDiffNotifyDTO> xcapDiffNotifyDTOs = new ArrayList<>();
        for (KnOPDirChgDTO dirChgDTO : dirChgDTOs) {
            KnXcapDiffNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();
            Collection<KnXcapDiffDocDTO> xcapDocList = new ArrayList<>();
            Collection<KnOPDocChgDTO> chgDocList = dirChgDTO.getDocChgDTO();
            if (chgDocList != null) {
                for (KnOPDocChgDTO chgDocDTO : chgDocList) {
                    KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
                    xcapDiffDocDTO.setDocChangeType(chgDocDTO.getDocumentChgType());
                    xcapDiffDocDTO.setDocEtag(chgDocDTO.getNewEtag());
                    xcapDiffDocDTO.setDocumentSelector(chgDocDTO.getDocUri());
                    xcapDiffDocDTO.setVideoPermission(chgDocDTO.getVideoPermission());
                    xcapDocList.add(xcapDiffDocDTO);
                }
            }
            xcapDiffNotifyDTO.setDocDiffObj(xcapDocList);
            xcapDiffNotifyDTO.setDirNewEtag(dirChgDTO.getDirNewEtag());
            xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
            xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
            xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
            xcapDiffNotifyDTO.setProtocolVersion(dirChgDTO.getProtoVersion());
            xcapDiffNotifyDTO.setPocHome(dirChgDTO.getPocHome());
            xcapDiffNotifyDTO.setPresenceHome(dirChgDTO.getPresenceHome());
            xcapDiffNotifyDTOs.add(xcapDiffNotifyDTO);
            knLogger.debug(methodName, "Notification DTO generated - ", xcapDiffNotifyDTO);
        }
        return xcapDiffNotifyDTOs;
    }


    /**
     * This method will fetch list of mdns for the given banid
     *
     * @param banId
     * @param persisterTxn
     * @return
     * @throws KnProvException
     */
    public List<String> retrieveBanMDNs(int banId, HIERARCHY_TYPE hierarchyType, KnPersisterTxn persisterTxn) throws KnProvException {
        final String methodName = "retrieveBanMDNs(String)";
        knLogger.debug(methodName, "ENTRY : extBanId ", banId);
        List<String> banMdnList;
        banMdnList = provClientIntf.retrieveBanMDNs(banId, hierarchyType, persisterTxn);
        if (banMdnList != null) {
            knLogger.debug(methodName, "EXIT : banMdnList size ", banMdnList.size());
        }
        knLogger.debug(methodName, "EXIT : banMdnList ", KnGDPRTemplate.mdnList(banMdnList));
        return banMdnList;

    }

    /**
     * This  method is called by the OP CLI and the audit to eanble the corporate auto pairing feature.
     *
     * @param corpInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMCorpRespDTO updateCorpAutoPairing(IXDMRequestDTO corpInfoDTO, KnPersisterTxn persisterTxn) throws KnException {
        String methodName = "updateCorpAutoPairing(int,KnPersisterTxn)";
        knLogger.debug(methodName, "Auto pairing corporate - ", corpInfoDTO);
        if (!(corpInfoDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    corpInfoDTO.getClass());
            throw new KnXDMServerException(KnProvConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDto = (KnXDMCorpInfoRequestDTO) corpInfoDTO;
        String auditStr = "Request recieved for Corporate-".concat(xdmRequestDto.getCorpId()).concat("with auto pair flag-")+ xdmRequestDto.getEnableAutoPair();
        audit.writeAuditMessage(KnConstants.UPDATE_AUTO_PAIR_BULK_CID, KnConstants.UPDATE_AUTO_PAIR_OPN, KnAuditHelper.STATUS.REQUEST, auditStr);
        KnIPCorpInfoDTO ipCorpDTO = new KnIPCorpInfoDTO();
        ipCorpDTO.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        ipCorpDTO.setEnableAutoPair(xdmRequestDto.getEnableAutoPair());
        ipCorpDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
        KnCorpAutoPairingResponse respDto = corpClientIntf.updateCorpAutoPairing(ipCorpDTO, persisterTxn);
        knLogger.debug(methodName, "updatedcorpautopairing sublist operation map- ", KnGDPRTemplate.mapKeyMdn(respDto.getChangeLogMap()));
        int pairedContactListId = respDto.getPairedContactListId();
        KnCorpResponseDTO grpResp = null;
        knLogger.debug(methodName, "respDto obtained- ", respDto);
        if (ipCorpDTO.getEnableAutoPair()) {
            if (respDto != null && respDto.getStatus() == KnConstants.STATUS_SUCCESS) {
                if (respDto.getGroupPairing() != null) {
                    knLogger.debug(methodName, "respDto.isCreateGroup()is not null- ", respDto.getGroupPairing());
                    if (respDto.getGroupPairing()) {
                        //call the create group
                        knLogger.debug(methodName, "Create Group Flow");
                        KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
                        groupInfoDTO.setGroupDisplayName(respDto.getGroupName());
                        groupInfoDTO.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
                        List<Integer> sublistIds = new ArrayList<Integer>();
                        sublistIds.add(pairedContactListId);
                        groupInfoDTO.setAddedSublistIds(sublistIds);
                        groupInfoDTO.setAddedMemberDTOMdns(new ArrayList<KnCorpContactDTO>());
                        groupInfoDTO.setGroupType(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP);
                        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
                        groupInfoDTO.setOperationType(KnOperationTypes.CREATE_GROUP);
                        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
                        groupInfoDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
                        knLogger.debug(methodName, "Invoke create Group from add to pairing list", groupInfoDTO);
                        grpResp = corpClientIntf.createGroup(groupInfoDTO, persisterTxn);
                        knLogger.debug(methodName, "updatedcorpautopairing corpGroupResp map- ", KnGDPRTemplate.mapKeyMdn(grpResp.getChangeLogMap()));
                        knLogger.debug(methodName, "updatedcorpautopairing corpGroupResp map- ", grpResp);
                    } else if (!respDto.getGroupPairing()) {
                        //call the modify group
                        knLogger.debug(methodName, "Modify Group Flow");
                        KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
                        groupInfoDTO.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
                        groupInfoDTO.setGroupId(respDto.getGroupId());
                        groupInfoDTO.setETag(Integer.valueOf(respDto.getEtag()));
                        List<Integer> sublistIds = new ArrayList<Integer>();
                        sublistIds.add(pairedContactListId);
                        groupInfoDTO.setAddedSublistIds(sublistIds);
                        groupInfoDTO.setRemovedMemberMdns(new LinkedList<String>());
                        groupInfoDTO.setRemovedSublistIds(new ArrayList<Integer>());
                        groupInfoDTO.setAddedMemberDTOMdns(new ArrayList<KnCorpContactDTO>());
                        groupInfoDTO.setGroupType(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP);
                        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
                        groupInfoDTO.setOperationType(KnOperationTypes.CREATE_GROUP);
                        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
                        groupInfoDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
                        knLogger.debug(methodName, "Invoke modify Group from add to pairing list", groupInfoDTO);
                        KnCorpGrpBasicInfoRespDto grpBasicInfoDto = corpClientIntf.getBasicGrpInfo(groupInfoDTO, persisterTxn);
                        KnCorpGrpBasicInfoDTO grpBasicInfo = new KnCorpGrpBasicInfoDTO();
                        grpBasicInfo.setGroupId(grpBasicInfoDto.getGroupId());
                        grpBasicInfo.setGrpDisplayName(grpBasicInfoDto.getGrpDisplayName());
                        grpBasicInfo.setGrpType(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP);
                        grpBasicInfo.setGroupListId(grpBasicInfoDto.getGroupListId());
                        grpBasicInfo.setGrpEtag(grpBasicInfoDto.getGrpEtag());
                        grpBasicInfo.setCorpId(grpBasicInfoDto.getCorpId());
                        knLogger.debug(methodName, "Invoke modify Group with grpBasicInfo", grpBasicInfo);
                        grpResp = corpClientIntf.modifyGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
                        knLogger.debug(methodName, "updatedcorpautopairing modyGrpResp map- ", KnGDPRTemplate.mapKeyMdn(grpResp.getChangeLogMap()));
                        knLogger.debug(methodName, "updatedcorpautopairing modyGrpResp map- ", grpResp);
                    }
                }

                if (grpResp != null && grpResp.getStatus() == KnConstants.STATUS_SUCCESS) {
                    knLogger.debug(methodName, "grpResp not null ", grpResp);
                    Map<String, KnOPDirChgDTO> changeLog = grpResp.getChangeLogMap();
                    if (respDto != null) {
                        Map<String, KnOPDirChgDTO> addpairChangeLog = respDto.getChangeLogMap();
                        if (changeLog != null) {
                            for (String mdn : changeLog.keySet()) {
                                KnOPDirChgDTO directory = changeLog.get(mdn);
                                if (directory != null) {
                                    Collection<KnOPDocChgDTO> documentsList = directory.getDocChgDTO();
                                    if (documentsList == null) {
                                        documentsList = new ArrayList<KnOPDocChgDTO>();
                                    }
                                    if (addpairChangeLog != null) {
                                        KnOPDirChgDTO dir = addpairChangeLog.get(mdn);
                                        if (dir != null) {
                                            Collection<KnOPDocChgDTO> docs = dir.getDocChgDTO();
                                            if (docs != null) {
                                                documentsList.addAll(docs);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                        respDto.setChangeLogMap(grpResp.getChangeLogMap());
                    } else {
                        knLogger.debug(methodName, "failure scenario", grpResp);
                        //respDto = grpResp;
                        populateResponse(respDto, grpResp);
                    }
                } else if (grpResp != null && grpResp.getStatus() == KnConstants.STATUS_FAILURE) {
                    //respDto = grpResp;
                    populateResponse(respDto, grpResp);
                }
            }
        }


        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        knLogger.debug(methodName, "xdmRespDto- ", xdmRespDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            audit.writeAuditMessage(KnConstants.UPDATE_AUTO_PAIR_BULK_CID, KnConstants.UPDATE_AUTO_PAIR_OPN, KnAuditHelper.STATUS.FAILURE, KnConstants.FAILURE_MSG);
            return xdmRespDto;
        }
        //Step:
        //prepare notification and send to notification mgr
        Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = prepareNotification(respDto);
        knLogger.debug(methodName, "Sending notifications - ", xcapDiffList);
        boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, persisterTxn);
        knLogger.debug(methodName, "Notification status - ", isNotified);
        xdmRespDto.setEnabledDispatchMemList(respDto.getEnabledDispatchMemList());
        knLogger.debug(methodName, "Returning Response - ", xdmRespDto);

        knLogger.debug(methodName, "Sending LI notifications - ");
        KnLIEventHandler.logLI(respDto.getLiEventList());
        knLogger.debug(methodName, "Li Notification Send status - ");
        //Get the xcap mobile sync flag
        boolean xcapMobileSync = knPAMMicroServiceCommon.getXcapMobileSyncFlag(persisterTxn);
        knLogger.debug(methodName, "xcapMobileSync - ", xcapMobileSync);
        //Get the RMQ configurations
        KnMqServiceConfig rmqInfoDto = knPAMMicroServiceCommon.retrieveServerConfDetails(persisterTxn);
        knLogger.debug(methodName, "rmqInfoDto - ", rmqInfoDto);
        if(xcapMobileSync){
            knLogger.debug(methodName, "Publishing micro service notify for Group event - ");
            knPAMMicroServiceCommon.startNotifyMicroServicesJob(respDto.getChangeLogMap(), Integer.valueOf(xdmRequestDto.getCorpId()), rmqInfoDto);
        }
        populateXdmResponse(xdmRespDto, respDto);
        audit.writeAuditMessage(KnConstants.UPDATE_AUTO_PAIR_BULK_CID, KnConstants.UPDATE_AUTO_PAIR_OPN, KnAuditHelper.STATUS.SUCCESS, KnConstants.SUCCESS_MSG);
        return xdmRespDto;
    }

    private void populateResponse(KnCorpAutoPairingResponse respDto, KnCorpResponseDTO grpResp) {
        respDto.setChangeLogMap(grpResp.getChangeLogMap());
        respDto.setDisabledDispatchMemList(grpResp.getDisabledDispatchMemList());
        respDto.setEnabledDispatchMemList(grpResp.getEnabledDispatchMemList());
        respDto.setStatus(grpResp.getStatus());
        respDto.setMessage(grpResp.getMessage());
        respDto.setFailedDataList(grpResp.getFailedDataList());
        respDto.setStatusCode(grpResp.getStatusCode());
        respDto.setLiEventList(grpResp.getLiEventList());
        respDto.setEtag(grpResp.getEtag());
        respDto.setTgsModeChgMap(grpResp.getTgsModeChgMap());
    }


    /**
     * This method is used to add the sent subscriber to the paird list of the corporate
     *
     * @param contactRequestDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMCorpRespDTO addToPairingList(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnException {

        String methodName = "addToPairingList(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnProvConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) contactRequestDTO;
        audit.writeAuditMessage(KnConstants.ADD_TO_PAIRING_BULK_CID, KnConstants.ADD_TO_PAIRING_OPN, KnAuditHelper.STATUS.REQUEST, "Request recieved");
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        contactDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        contactDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        contactDTO.setName(xdmRequestDTO.getName());
        contactDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Pairing Corp contact - ", contactDTO);
        KnCorpAutoPairingResponse respDto = corpClientIntf.addToPairingList(contactDTO, persisterTxn);
        knLogger.debug(methodName, "Sublist operation etag map - ", KnGDPRTemplate.mapKeyMdn(respDto.getChangeLogMap()));
        knLogger.debug(methodName, "Response - ", respDto.getStatus());
        int pairedContactListId = respDto.getPairedContactListId();
        KnCorpResponseDTO grpResp = new KnCorpResponseDTO();
        if (respDto.getStatus() == KnConstants.STATUS_SUCCESS) {
            if (respDto.getGroupPairing() != null) {
                if (respDto.getGroupPairing()) {
                    //call the create group
                    KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
                    groupInfoDTO.setGroupDisplayName(respDto.getGroupName());
                    List<Integer> sublistIds = new ArrayList<Integer>();
                    groupInfoDTO.setCorpId(contactDTO.getCorpId());
                    sublistIds.add(pairedContactListId);
                    groupInfoDTO.setAddedSublistIds(sublistIds);
                    groupInfoDTO.setAddedMemberDTOMdns(new ArrayList<KnCorpContactDTO>());
                    groupInfoDTO.setGroupType(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP);
                    groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
                    groupInfoDTO.setOperationType(KnOperationTypes.CREATE_GROUP);
                    groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
                    groupInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
                    grpResp = corpClientIntf.createGroup(groupInfoDTO, persisterTxn);
                    knLogger.debug(methodName, "Group operation etag map - ", KnGDPRTemplate.mapKeyMdn(grpResp.getChangeLogMap()));
                } else if (!respDto.getGroupPairing()) {
                    //call the modify group
                    knLogger.debug(methodName, "Modify Group Flow");
                    KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
                    groupInfoDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
                    groupInfoDTO.setGroupId(respDto.getGroupId());
                    groupInfoDTO.setETag(Integer.valueOf(respDto.getEtag()));
                    List<Integer> sublistIds = new ArrayList<Integer>();
                    sublistIds.add(pairedContactListId);
                    groupInfoDTO.setAddedSublistIds(sublistIds);
                    groupInfoDTO.setRemovedMemberMdns(new LinkedList<String>());
                    groupInfoDTO.setRemovedSublistIds(new ArrayList<Integer>());
                    groupInfoDTO.setAddedMemberDTOMdns(new ArrayList<KnCorpContactDTO>());
                    groupInfoDTO.setGroupType(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP);
                    groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
                    groupInfoDTO.setOperationType(KnOperationTypes.CREATE_GROUP);
                    groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
                    groupInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
                    knLogger.debug(methodName, "Invoke modify Group from add to pairing list", groupInfoDTO);
                    KnCorpGrpBasicInfoRespDto grpBasicInfoDto = corpClientIntf.getBasicGrpInfo(groupInfoDTO, persisterTxn);
                    KnCorpGrpBasicInfoDTO grpBasicInfo = new KnCorpGrpBasicInfoDTO();
                    grpBasicInfo.setGroupId(grpBasicInfoDto.getGroupId());
                    grpBasicInfo.setGrpDisplayName(grpBasicInfoDto.getGrpDisplayName());
                    grpBasicInfo.setGrpType(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP);
                    grpBasicInfo.setGroupListId(grpBasicInfoDto.getGroupListId());
                    grpBasicInfo.setGrpEtag(grpBasicInfoDto.getGrpEtag());
                    grpBasicInfo.setCorpId(grpBasicInfoDto.getCorpId());
                    knLogger.debug(methodName, "Invoke modify Group with grpBasicInfo", grpBasicInfo);
                    grpResp = corpClientIntf.modifyGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
                    knLogger.debug(methodName, "updatedcorpautopairing modyGrpResp map- ", KnGDPRTemplate.mapKeyMdn(grpResp.getChangeLogMap()));
                    knLogger.debug(methodName, "updatedcorpautopairing modyGrpResp map- ", grpResp);
                }
                if (grpResp != null && grpResp.getStatus() == KnConstants.STATUS_SUCCESS) {
                    knLogger.debug(methodName, "grpResp not null ", grpResp);
                    Map<String, KnOPDirChgDTO> changeLog = grpResp.getChangeLogMap();
                    if (respDto != null) {
                        Map<String, KnOPDirChgDTO> addpairChangeLog = respDto.getChangeLogMap();
                        if (changeLog != null) {
                            for (String mdn : changeLog.keySet()) {
                                KnOPDirChgDTO directory = changeLog.get(mdn);
                                if (directory != null) {
                                    Collection<KnOPDocChgDTO> documentsList = directory.getDocChgDTO();
                                    if (documentsList == null) {
                                        documentsList = new ArrayList<KnOPDocChgDTO>();
                                    }
                                    if (addpairChangeLog != null) {
                                        KnOPDirChgDTO dir = addpairChangeLog.get(mdn);
                                        if (dir != null) {
                                            Collection<KnOPDocChgDTO> docs = dir.getDocChgDTO();
                                            if (docs != null) {
                                                documentsList.addAll(docs);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                        respDto.setChangeLogMap(grpResp.getChangeLogMap());
                    } else {
                        knLogger.debug(methodName, "failure scenario", grpResp);
                        //respDto = grpResp;
                        populateResponse(respDto, grpResp);
                    }
                } else if (grpResp != null && grpResp.getStatus() == KnConstants.STATUS_FAILURE) {
                    //respDto = grpResp;
                    populateResponse(respDto, grpResp);
                }
            }
        }

        knLogger.debug(methodName, "final operation etag map - ", KnGDPRTemplate.mapKeyMdn(respDto.getChangeLogMap()));
        knLogger.debug(methodName, "final operation etag map - ", respDto);


        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            audit.writeAuditMessage(KnConstants.ADD_TO_PAIRING_BULK_CID, KnConstants.ADD_TO_PAIRING_OPN, KnAuditHelper.STATUS.FAILURE, KnConstants.FAILURE_MSG);
            return xdmRespDto;
        }
        //Step:
        //prepare notification and send to notification mgr
        Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = prepareNotification(respDto);
        knLogger.debug(methodName, "Sending notifications - ", xcapDiffList);
        boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, persisterTxn);
        knLogger.debug(methodName, "Notification status - ", isNotified);
        knLogger.debug(methodName, "Returning Response - ", xdmRespDto);

        knLogger.debug(methodName, "Sending LI notifications - ");
        KnLIEventHandler.logLI(respDto.getLiEventList());
        knLogger.debug(methodName, "Li Notification Send status - ");
        //Get the xcap mobile sync flag
        boolean xcapMobileSync = knPAMMicroServiceCommon.getXcapMobileSyncFlag(persisterTxn);
        knLogger.debug(methodName, "xcapMobileSync - ", xcapMobileSync);
        //Get the RMQ configurations
        KnMqServiceConfig rmqInfoDto = knPAMMicroServiceCommon.retrieveServerConfDetails(persisterTxn);
        knLogger.debug(methodName, "rmqInfoDto - ", rmqInfoDto);
        if(xcapMobileSync){
            knLogger.debug(methodName, "Publishing micro service notify for Group event - ");
            knPAMMicroServiceCommon.startNotifyMicroServicesJob(respDto.getChangeLogMap(), Integer.valueOf(xdmRequestDTO.getCorpId()), rmqInfoDto);
        }
        audit.writeAuditMessage(KnConstants.ADD_TO_PAIRING_BULK_CID, KnConstants.ADD_TO_PAIRING_OPN, KnAuditHelper.STATUS.SUCCESS, KnConstants.SUCCESS_MSG);
        return xdmRespDto;
    }

    //Populating XDM response
    private void populateXdmResponse(KnXDMCorpRespDTO xdmRespDto, KnCorpResponseDTO respDto) {
        int status = respDto.getStatus();
        xdmRespDto.setResponseStatus(status);
        xdmRespDto.setResponseCode(respDto.getStatusCode());
        xdmRespDto.setResponseMessage(respDto.getMessage());
        Collection<KnCorpFailedData> failedDataList = respDto.getFailedDataList();
        String etag = respDto.getEtag();
        if (status == KnConstants.RESPONSE_STATUS.SUCCESS.value()) {
            if (failedDataList != null && !failedDataList.isEmpty()) {
                xdmRespDto.setFailureDetails(populateFailureDetails(failedDataList));
            }
            if (etag != null) {
                xdmRespDto.setEtag(etag);
            }
        } else if (failedDataList != null && failedDataList.isEmpty()) {
            xdmRespDto.setFailureDetails(populateFailureDetails(failedDataList));
        }
    }

    private Collection<KnXDMFailureRespDTO> populateFailureDetails(Collection<KnCorpFailedData> failedDataList) {

        Collection<KnXDMFailureRespDTO> failedList = null;

        if (failedDataList == null || failedDataList.isEmpty()) {
            return failedList;
        }
        failedList = new ArrayList<KnXDMFailureRespDTO>(failedDataList.size());
        for (KnCorpFailedData failedData : failedDataList) {
            KnXDMFailureRespDTO failureDTO = new KnXDMFailureRespDTO(failedData.getAttribute(),
                    getString(failedData.getValues()), failedData.getCode(), failedData.getMsg());
            failedList.add(failureDTO);
        }
        return failedList;
    }

    /**
     * This  method is for UpdateSubsFeatureBitSet
     * @param requestDTO
     * @param persisterTxn
     * @return
     */
     public IXDMResponseDTO updateBulkSubsFSAndPkgIds (IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "updateBulkSubsFSAndPkgIds(Map<String,Integer>)";
        knLogger.info(methodName, "ENTRY: update feature bit for subscribers ",requestDTO);

        KnOPBulkRespDTO provRespDTO = null;
        Map<Integer, Integer> provFS = null;
        Map<String, KnOPDispatchDirChgDTO> mdnDispatchChgDTOMap = null;
        IXDMResponseDTO responseDTO = new KnXDMPAMRespDTO();
        final  int DEFAULT_NOTIFY_SIZE= 4;

        try {
            // send final notification from mediator only (get dir change DTO
            // from response of KnBulkSPC).
            KnXcapDiffNotifyDTO xcapDiffNotifyDTO = null;
            if (!(requestDTO instanceof KnXDMPAMAccInfoDTO)) {
                      knLogger.error(methodName, "requestDTO not of type KnXDMPAMAccInfoDTO ",requestDTO.getClass());
                      throw new KnXDMServerException(KnProvConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
            }

            KnXDMPAMAccInfoDTO xdmPAMSAccInfoDTO=new KnXDMPAMAccInfoDTO();
            xdmPAMSAccInfoDTO=(KnXDMPAMAccInfoDTO)requestDTO;
            knLogger.debug(methodName, "xdmPAMSAccInfoDTO :",xdmPAMSAccInfoDTO);

            KnIPBulkSubsProvInfoDTO subsProfInfoDTO= new KnIPBulkSubsProvInfoDTO();
            provFS=xdmPAMSAccInfoDTO.getFeatureBitInfoMap();
            knLogger.debug(methodName, "from KnXDMPAMAccInfoDTO provFS:",provFS);
           // provFS= xdmPAMSAccInfoDTO.getProfileDetails().getFeatureBitInfoMap();
           // knLogger.debug(methodName, "from KnXDMPAMSubsProfInfoDTO provFS :",provFS);
            subsProfInfoDTO.setProvFSMap(provFS);

            subsProfInfoDTO.setSubscriberFS2(xdmPAMSAccInfoDTO.getProfileDetails().getSubscriberFS2());
            subsProfInfoDTO.setPamAccId(xdmPAMSAccInfoDTO.getPamAccId());
            subsProfInfoDTO.setProfileId(xdmPAMSAccInfoDTO.getProfileDetails().getProfileId());
            subsProfInfoDTO.setProfileName(xdmPAMSAccInfoDTO.getProfileDetails().getProfileName());
            subsProfInfoDTO.setPubSubsType(xdmPAMSAccInfoDTO.getProfileDetails().getPubSubsType());
            subsProfInfoDTO.setCorpSubsType(xdmPAMSAccInfoDTO.getProfileDetails().getCorpSubsType());
            subsProfInfoDTO.setClient_Type(xdmPAMSAccInfoDTO.getProfileDetails().getClient_Type());
            subsProfInfoDTO.setExtCorpId(xdmPAMSAccInfoDTO.getProfileDetails().getExtCorpId());
            subsProfInfoDTO.setCorpName(xdmPAMSAccInfoDTO.getProfileDetails().getCorpName());
            subsProfInfoDTO.setCreationTime(xdmPAMSAccInfoDTO.getCreationTime());
            subsProfInfoDTO.setLastUpdateTime(xdmPAMSAccInfoDTO.getLastUpdateTime());
            subsProfInfoDTO.setMdns(xdmPAMSAccInfoDTO.getProfileDetails().getMdns());
            subsProfInfoDTO.setImei(xdmPAMSAccInfoDTO.getProfileDetails().getIMEI());
            subsProfInfoDTO.setEmail(xdmPAMSAccInfoDTO.getProfileDetails().getEmailAddress());
            subsProfInfoDTO.setPkgIdMap(xdmPAMSAccInfoDTO.getProfileDetails().getPkgIdMap());
            subsProfInfoDTO.setFirstNetIndicator(xdmPAMSAccInfoDTO.getProfileDetails().getFirstNetIndicator());
            knLogger.debug(methodName, " subsProfInfoDTO :",subsProfInfoDTO, "xdmPAMSAccInfoDTO",xdmPAMSAccInfoDTO);
            // subsProfInfoDTO.setCustomParamMap(xdmPAMSubsProfInfoDTO.getCustomParamMap());

            provRespDTO = provClientIntf.updateBulkSubsFSAndPkgIds(subsProfInfoDTO, persisterTxn);
            knLogger.debug(methodName, " Update feature bits  successfully :",subsProfInfoDTO, "provRespDTO",provRespDTO);
            // get all changed activeFs subs
            mdnDispatchChgDTOMap = provRespDTO.getMdnDispatcherChgDTOMap();
            knLogger.debug(methodName, "mdnDispatchChgDTOMap :" ,mdnDispatchChgDTOMap);

            int i = 0;
            List<KnXcapDiffNotifyDTO> xcapDiffList = new ArrayList<KnXcapDiffNotifyDTO>();
            //Get the xcap mobile sync flag
            boolean xcapMobileSync = knPAMMicroServiceCommon.getXcapMobileSyncFlag(persisterTxn);
            knLogger.debug(methodName, "xcapMobileSync: ",xcapMobileSync);
          //Get the RMQ configurations
            KnMqServiceConfig rmqInfoDto = knPAMMicroServiceCommon.retrieveServerConfDetails(persisterTxn);
            knLogger.debug(methodName, "[ rmqInfoDto ]: ",rmqInfoDto);
            List<KnSubscrEXDMSNotifyDto> notifyDtoList = new ArrayList<KnSubscrEXDMSNotifyDto>();
            for (String mdn : mdnDispatchChgDTOMap.keySet()) {
                xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();

                KnOPDispatchDirChgDTO opDispatchDirChgDTO = mdnDispatchChgDTOMap.get(mdn);

                if ((opDispatchDirChgDTO.getProtoVersion() .matches(com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_REGEX))
                        && (opDispatchDirChgDTO.isActiveFSChanged())) {

                    KnProfileNotifyDTO profileNotifyDTO = new KnProfileNotifyDTO();
                    profileNotifyDTO.setActiveFeatureSetChange(mdn);
                    xcapDiffNotifyDTO.setProfileNotify(true);
                    profileNotifyDTO.setMdn(mdn);
                    profileNotifyDTO.setPocHome(opDispatchDirChgDTO.getPocHome());
                    profileNotifyDTO.setPresenceHome(opDispatchDirChgDTO.getPresenceHome());
                    profileNotifyDTO.setAction(KnConstants.MESSAGE_TYPE.SUBSCR_PROFILE_CHANGE.value());
                    xcapDiffNotifyDTO.setProfileNotifyDTO(profileNotifyDTO);

                }
                Collection<KnXcapDiffDocDTO> xcapDocList = new ArrayList<KnXcapDiffDocDTO>();
                knLogger.debug(methodName, "xcapDiffNotifyDTO :" ,xcapDiffNotifyDTO);

                ArrayList<KnOPDocChgDTO> chgDocList = (ArrayList<KnOPDocChgDTO>) opDispatchDirChgDTO.getDocChgDTO();
                knLogger.debug(methodName, "chgDocList :" ,chgDocList);

                for (KnOPDocChgDTO chgDTO : chgDocList) {
                    KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
                    xcapDiffDocDTO .setDocChangeType(chgDTO.getDocumentChgType());
                    xcapDiffDocDTO.setDocEtag(chgDTO.getNewEtag());
                    xcapDiffDocDTO.setDocumentSelector(chgDTO.getDocUri());
                    xcapDiffDocDTO.setVideoPermission(chgDTO.getVideoPermission());
                    xcapDocList.add(xcapDiffDocDTO);
                }
                knLogger.debug(methodName, "xcapDocList :" ,xcapDocList);

                knLogger.debug(methodName, "After setting details :" ,chgDocList);
                xcapDiffNotifyDTO.setDocDiffObj(xcapDocList);
                xcapDiffNotifyDTO.setDirNewEtag(opDispatchDirChgDTO .getDirNewEtag());
                xcapDiffNotifyDTO.setDirPrevEtag(opDispatchDirChgDTO.getDirPrevEtag());
                xcapDiffNotifyDTO.setDirURI(opDispatchDirChgDTO.getDirUri());
                xcapDiffNotifyDTO.setXcapRootUri(opDispatchDirChgDTO.getXcapRootURI());
                xcapDiffNotifyDTO.setProtocolVersion(opDispatchDirChgDTO.getProtoVersion());
                xcapDiffNotifyDTO.setPocHome(opDispatchDirChgDTO.getPocHome());
                xcapDiffNotifyDTO.setPresenceHome(opDispatchDirChgDTO.getPresenceHome());
                xcapDiffList.add(xcapDiffNotifyDTO);
                i++;
                boolean xcapCouchClientBit = KnGeneralUtil.getFeatureBitValue(opDispatchDirChgDTO.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.XCAPCOUCHCLIENT.value());
                knLogger.debug(methodName, "xcapCouchClientBit ", xcapCouchClientBit);
                int pv = 1;
                if(opDispatchDirChgDTO.getProtoVersion()!=null ) {
                 if(opDispatchDirChgDTO.getProtoVersion().contains(".")) {
                     pv = Integer.parseInt(opDispatchDirChgDTO.getProtoVersion().substring(0, opDispatchDirChgDTO.getProtoVersion().indexOf(".")));
                 }else{
                     pv= Integer.parseInt(opDispatchDirChgDTO.getProtoVersion());
                 }
                }
                knLogger.debug(methodName, "xcapMobileSync:",xcapMobileSync," xcapCouchClientBit:", xcapCouchClientBit," PV:",pv);
                if (xcapMobileSync && (xcapCouchClientBit || pv >= PROTOCOL_VERSION_18 )) {

                    KnSubscrEXDMSNotifyDto subscrEXDMSNotifyDto = new KnSubscrEXDMSNotifyDto();
                    subscrEXDMSNotifyDto.setMdn(mdn);
                    subscrEXDMSNotifyDto.setCorpid(opDispatchDirChgDTO.getCorpId());
                    subscrEXDMSNotifyDto.setActiveFS(KnGeneralUtil.convertHexStringToLong(opDispatchDirChgDTO.getActiveFS2()));
                    subscrEXDMSNotifyDto.setActiveFS2(opDispatchDirChgDTO.getActiveFS2());
                    subscrEXDMSNotifyDto.setPv(opDispatchDirChgDTO.getProtoVersion());
                    subscrEXDMSNotifyDto.setOldActiveFS(opDispatchDirChgDTO.getOldActiveFS());
                    subscrEXDMSNotifyDto.setId(com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.FEATURE_BIT_CHANGE.value() + KnConstants.LINE_SAPERATOR + mdn);
                    subscrEXDMSNotifyDto.setType(com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.FEATURE_BIT_CHANGE.value());
                    subscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                    subscrEXDMSNotifyDto.setNotifyEventType(com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS.value());
                    subscrEXDMSNotifyDto.setLastProfileUpdateTime(opDispatchDirChgDTO.getLastProfileUpdateTime());
                    notifyDtoList.add(subscrEXDMSNotifyDto);
                }
                provClientIntf.actionOnTGSSDocCust(mdn,opDispatchDirChgDTO.getActiveFS2(),"0",persisterTxn);
            }
            notifier.setMaxNotfnsPerJob(DEFAULT_NOTIFY_SIZE);
            knLogger.debug(methodName, "notifier :" ,notifier);

            boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList);
            knLogger.debug(methodName, "Notification status - ", isNotified);
            knLogger.debug(methodName, "notifyDtoList :", notifyDtoList);
            knLogger.info(methodName, "Publishing micro service notify  for User event - ");
            knPAMMicroServiceCommon.startNotifyMicroServicesJob(notifyDtoList, rmqInfoDto);
            knLogger.debug(methodName, "micro service notify for activeFs change event - Sent");
            // How to send the notification.
            //KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_UPDATED, ((KnIPBulkSubsProvInfoDTO) requestDTO).getMdns().size());
            responseDTO = getSuccessResponse(responseDTO);
            knLogger.debug(methodName, "After setting success responseDTO :" ,responseDTO);

        } catch (KnXDMServerException e) {
            knLogger.error(methodName, "Failed to Change service auth status of Subscriber ", e);
            responseDTO = getFailureResponse(responseDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            responseDTO = getFailureResponse(responseDTO, e);
        }
        knLogger.debug(methodName, "Exiting : with response responseDTO", responseDTO);
        return responseDTO;
    }



    private String getString(Collection<String> collectionStr) {
        StringBuffer sb = new StringBuffer(100);
        for (String str : collectionStr) {
            sb.append(str).append(",");
        }
        int indx = sb.lastIndexOf(",");
        if (indx > 0) {
            sb.deleteCharAt(indx);
        }
        return sb.toString();
    }
}