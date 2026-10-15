/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.common.commdto.request.KnXDMCorpSubscInfoRequestDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.utilities.syncgateway.KnManageSyncUserProfileUtil;
import com.kodiak.utilities.syncgateway.dto.KnSubscriberInfoDTO;
import com.kodiak.utilities.syncgateway.dto.KnSyncResponseDTO;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.mediator.helper.KnXDMPubDocMediator;
import com.kodiak.xdms.mediator.resources.KnJobConstants;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnDeRegisterNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnFailedCBTxnLogDTO;
import com.kodiak.xdms.server.common.dto.common.KnMDNDetailsDTO;
import com.kodiak.xdms.server.common.dto.common.KnUnAssignEXDMSNotifyDto;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPDeleteSubsRespDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.UNASSIGN_USER_PROFILE;
import static com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS;
import static com.kodiak.common.resources.KnConstants.MICROSERVICE_NOTIFY_DOC_VER;

public class KnUPMMCXGroupCleanUpOnMCPTTDisabledBitTask implements Runnable {

    private static final KnLogger knLogger = KnLogger.getLogger(KnUPMMCXGroupCleanUpOnMCPTTDisabledBitTask.class);

    private Integer corpId;
    private String mdn;
    private String txnId;
    private KnGenInfoUtil genInfoUtil;
    private KnGeneralCacheUtil generalCacheUtil;
    private IProvClientIntf provClientIntf;
    private KnXDMCorpMediator corpMediator;
    private KnXDMCommonMediator commonMediator;
    private IXcapDiffNotifierIntf notifier;
    private KnXDMPubDocMediator pubDocMediator;
    private KnJobSchedulerImpl scheduler;
    private int maxNotificationSize;

    KnUPMMCXGroupCleanUpOnMCPTTDisabledBitTask(Integer corpId,String mdn,String txnId){
        this.corpId=corpId;
        this.mdn=mdn;
        this.txnId=txnId;
        genInfoUtil = KnGenInfoUtil.getInstance();
        generalCacheUtil = KnGeneralCacheUtil.getInstance();
        provClientIntf = KnProvClientImpl.getInstance();
        corpMediator = KnXDMCorpMediator.getInstance();
        commonMediator = KnXDMCommonMediator.getInstance();
        notifier = new KnXcapDiffNotifierImpl();
        pubDocMediator = KnXDMPubDocMediator.getInstance();
        scheduler = KnJobSchedulerImpl.getInstance();
        maxNotificationSize = 800;
    }

    @Override
    public void run() {
        runTask();
    }

    private void runTask(){
        final String methodName="runTask()";
        KnPersisterTxn persisterTxn = null;
        try {
            knLogger.info(methodName, "Entry :", " txnId :", txnId, " mdn :", mdn);
            generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());

            // opening the transaction
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
            subscriberInfoDTO.setMdn(mdn);
            KnOPSubsProfileInfoDTO subsProfileInfoDTO = provClientIntf.getSubscriberDetails(subscriberInfoDTO, persisterTxn);
            //check if the mdn is a pseudo mdn then restrict the operation
            if (subsProfileInfoDTO.getPamAccId() != null && subsProfileInfoDTO.getPamAccId() != 0) {
                knLogger.error(methodName, "Mdn is present as a pseudo number,operation not allowed");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_AS_PSEUDOMDN_IN_POCSUBSCRINFO,
                        "Mdn is present as a pseudo mdn in POCSUBSCRINFO");
            }

            // invoking the corporate library
            KnXDMCorpSubscInfoRequestDTO contactRequestDTO = new KnXDMCorpSubscInfoRequestDTO();
            contactRequestDTO.setSubscriberMdn(mdn);
            contactRequestDTO.setHierarchyType(subsProfileInfoDTO.getHierarchyType());
            contactRequestDTO.setUpmCall(true);
            knLogger.debug(methodName," corp delete subscriber req :",contactRequestDTO);
            KnCorpResponseDTO corpResp = corpMediator.deleteSubscriber(contactRequestDTO, persisterTxn);
            int status = corpResp.getStatus();
            knLogger.debug(methodName, " corp Library response - ", corpResp);
            if (status != 0) {
                throw new KnXDMServerException(corpResp.getStatusCode(), corpResp.getMessage());
            }
            //prepare notification and send to notification mgr
            Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = commonMediator.prepareNotification(corpResp);
            knLogger.debug(methodName, "Sending notifications - ", xcapDiffList);
            boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, persisterTxn);
            knLogger.debug(methodName, "Notification status - ", isNotified);
            Collection<String> deletedMemberList = corpResp.getDisabledDispatchMemList();
            knLogger.debug(methodName, "deletedMemberList - ", KnGDPRTemplate.mdnList(deletedMemberList));
            commonMediator.sendTGSModeChangeNotification(corpResp.getTgsModeChgMap());
            KnLIEventHandler.logLI(corpResp.getLiEventList());
            int publicSubscriptionType = subsProfileInfoDTO.getPublicSubscriptionType();
            if (publicSubscriptionType == KnConstants.PUBLIC_SUBSCRIPTION_TYPE.PUBLIC.value()) {
                knLogger.debug(methodName, "invoking the public library");
                pubDocMediator.deleteAllContactsAndGroups(mdn, persisterTxn);
                knLogger.debug(methodName, "deleted public data for mdn ");
            }
            // invoking the prov library for Deleting subscriber profile
            KnOPDeleteSubsRespDTO provRespDTO = provClientIntf.deleteSubscriber(subscriberInfoDTO, persisterTxn);
            knLogger.debug(methodName,"Delete Subscriber operation response - ", provRespDTO);
            // Initializing the KnSubscriberInfoDTO for base RestAPI
            KnSubscriberInfoDTO knSubscriberInfoDTO= new KnSubscriberInfoDTO();
            // Fetching the boolean value for 45th bit, If it is true then we will call the new REST API.
            boolean xcapCouchClient = KnGeneralUtil.getFeatureBitValue(provRespDTO.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.XCAPCOUCHCLIENT.value());
            knLogger.debug(methodName, "xcapCouchClient ", xcapCouchClient);
            // checking if 45th bit is 0/false or 1/true. If not 0/false then we make the rest call
            if(xcapCouchClient){
                knSubscriberInfoDTO.setMdn(provRespDTO.getMdn());
                knLogger.debug(methodName, "knSubscriberInfoDTO : mdn",knSubscriberInfoDTO);
                // Fetching the bucket URLS
                Map<Integer, String> bucketUrlList = genInfoUtil.retrievePTXbucketUrls(persisterTxn);
                knLogger.debug(methodName, "bucketUrlList : ", bucketUrlList);
                KnFailedCBTxnLogDTO failedCBTxnLogDTO = new KnFailedCBTxnLogDTO();
                failedCBTxnLogDTO.setMdn(knSubscriberInfoDTO.getMdn());
                genInfoUtil.deleteCBTxnFailLog(failedCBTxnLogDTO, persisterTxn);
                for(Map.Entry<Integer, String> entry : bucketUrlList.entrySet()) {
                    List<String> bucketUrl = new ArrayList<>();
                    Integer curClusterId = entry.getKey();
                    knLogger.debug(methodName, "Updating/deleting profile in clusterId - ", curClusterId);
                    bucketUrl.add(entry.getValue());
                    //REST call for profile creation starts here.
                    KnSyncResponseDTO knSyncResponseDTO = KnManageSyncUserProfileUtil.getInstance().deleteSyncDocument(knSubscriberInfoDTO, bucketUrl);
                    knLogger.debug(methodName, "KnSyncResponseDTO status : ", knSyncResponseDTO.getStatus());
                    if (knSyncResponseDTO.getStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()){
                        failedCBTxnLogDTO = new KnFailedCBTxnLogDTO();
                        failedCBTxnLogDTO.setMdn(knSubscriberInfoDTO.getMdn());
                        failedCBTxnLogDTO.setImpactedClusterId(entry.getKey());
                        failedCBTxnLogDTO.setTxnType(KnConstants.CB_PROFILE_TXN_TYPE.PROFILE_DELETE.value());
                        genInfoUtil.insertCBTxnFailLog(failedCBTxnLogDTO, persisterTxn);
                    }
                }
            }
            //Get the xcap mobile sync flag
            boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(persisterTxn);
            knLogger.debug(methodName, "Saving the transaction");
            persisterTxn.save();
            // sending the notification
            // populate the KnXcapDiffNotifyDTO
            // Notification requires the following params
            // 1. Dir Uri, 2. Dir prev etag,
            if (subsProfileInfoDTO.getSubsClientType() != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value()
                    && subsProfileInfoDTO.getSubsClientType() != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
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
                boolean notificationStatus = notifier.sendXcapDiffNotifications(xcapDiffNotifyDTO);
                if (notificationStatus) {
                    knLogger.debug(methodName, "Successfully sent the notification");
                }
            }
            if(xcapMobileSync){
                knLogger.debug(methodName, "Publishing micro service notify - ");
                commonMediator.startNotifyMicroServicesJob(corpResp.getChangeLogMap(), subsProfileInfoDTO.getCorpId());
            }

            /*
             * Spawning a new thread for update of the DISPATCH_GRP_MEMBER FLAG
             * of Members of the dispatch groups that has been deleted
             */
            knLogger.debug(methodName, "deletedMemberList - ", KnGDPRTemplate.mdnList(deletedMemberList));
            // If deleted members list id not null and not empty start job to
            // update DISPATCH_GROUP_MEMBER field
            if (deletedMemberList != null && !deletedMemberList.isEmpty()) {
                knLogger.debug(methodName,"Calling Dispatch group member job - ");
                List<KnDispGrpMemChecker> jobList = new ArrayList<>(1);
                KnDispGrpMemChecker dispGrpMemCheckerJob = new KnDispGrpMemChecker();
                dispGrpMemCheckerJob.setMaxNotificationSize(maxNotificationSize);
                dispGrpMemCheckerJob.setCorpId(subsProfileInfoDTO.getCorpId());
                dispGrpMemCheckerJob.setDeletedMembers(corpResp.getDisabledDispatchMemList());
                jobList.add(dispGrpMemCheckerJob);
                try {
                    knLogger.debug(methodName, "Scheduling the Jobs - ", jobList);
                    scheduler.addRamJob(jobList, KnJobConstants.JOB_GROUP_NAME);
                } catch (KnJobSchedulerException e) {
                    knLogger.error(methodName,"KnJobSchedulerException occurred while ", "submitting Notification Job to Scheduler - " + e);
                    knLogger.error(methodName, e);
                }
            }
            knLogger.debug(methodName,"User Profile Mdn deleted Successfully :", KnGDPRTemplate.mdn(mdn));

            if(corpResp != null){
                boolean profileNotifyStatus = commonMediator.prepareMcxNotifyForProfileMdns(corpResp);
                knLogger.debug(methodName, "sending Profile notification Status: ", profileNotifyStatus);
            }

            List<KnUnAssignEXDMSNotifyDto> notifyDtoList = new ArrayList<>();

            if (xcapMobileSync) {
                KnUnAssignEXDMSNotifyDto unAssignEXDMSNotifyDto = new KnUnAssignEXDMSNotifyDto();
                unAssignEXDMSNotifyDto.setId(UNASSIGN_USER_PROFILE.value() + KnConstants.LINE_SAPERATOR + mdn);
                unAssignEXDMSNotifyDto.setType(UNASSIGN_USER_PROFILE.value());
                unAssignEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
                unAssignEXDMSNotifyDto.setCorpid(subsProfileInfoDTO.getCorpId());
                unAssignEXDMSNotifyDto.setClientType(subsProfileInfoDTO.getSubsClientType());
                unAssignEXDMSNotifyDto.setPv(subsProfileInfoDTO.getClientPVmajorVer() + "." + subsProfileInfoDTO.getClientPVminorVer());
                KnMDNDetailsDTO bMdn = new KnMDNDetailsDTO();
                bMdn.setMdn(mdn);
                bMdn.setActiveFS(subsProfileInfoDTO.getActiveFS2());
                bMdn.setLastProfileUpdateTime(subsProfileInfoDTO.getLastProfileUpdateTime());
                unAssignEXDMSNotifyDto.setBaseMdn(bMdn);
                unAssignEXDMSNotifyDto.setNotifyEventType(USER_NOTIFY_EVENTS.value());
                unAssignEXDMSNotifyDto.setProfileMdns(corpResp.getProfileMdnEtagMap() != null ? new ArrayList<>(corpResp.getProfileMdnEtagMap().keySet()) : null);
                notifyDtoList.add(unAssignEXDMSNotifyDto);
            }
            if(!notifyDtoList.isEmpty()){
                knLogger.info(methodName, "Publishing micro service notify  for User event delete- ");
                commonMediator.startNotifyMicroServicesJob(notifyDtoList);
            }

            generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
            knLogger.info(methodName, "Exit :", " txnId :", txnId, " mdn :", mdn);
        }catch (Throwable e) {
            updateFailedAsyncJobTaskStatus(txnId);
            rollback(persisterTxn);
            knLogger.error(methodName," Throwable :",e.getMessage());
        }
    }

    private void updateFailedAsyncJobTaskStatus(String failedTxn){
        String methodName = "updateFailedAsyncJobTaskStatus()";
        try {
            knLogger.error(methodName," failedTxn :", failedTxn);
            generalCacheUtil.updateAsyncJobStatus(failedTxn, KnConstants.UPM_JOB_STATUS.FAILURE.Value());
        } catch (Exception e) {
            knLogger.error(methodName," Exception :",e.getMessage());
        }
    }

    private void rollback(KnPersisterTxn txn) {
        try {
            knLogger.error("rollback()", "Rolling back transaction");
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)",
                    "Failed to rollback the transaction.");
        }
    }
}
