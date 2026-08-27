/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.kodiak.common.commdto.request.KnXDMCorpSubscInfoRequestDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.commdto.response.KnXDMRespDTO;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants.FEATURE_SET;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.frameworks.messaging.common.dto.KnMqServiceConfig;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.frameworks.statisticalmgr.KnStatisticsManagerImpl;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.utilities.syncgateway.KnManageSyncUserProfileUtil;
import com.kodiak.utilities.syncgateway.dto.KnSubscriberInfoDTO;
import com.kodiak.utilities.syncgateway.dto.KnSyncResponseDTO;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.mediator.helper.KnXDMPubDocMediator;
import com.kodiak.xdms.mediator.resources.KnJobConstants;
import com.kodiak.xdms.mediator.resources.jobs.KnDispGrpMemChecker;
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

import static com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.UNASSIGN_USER_PROFILE;
import static com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS;
import static com.kodiak.common.resources.KnConstants.MICROSERVICE_NOTIFY_DOC_VER;
import static com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.CBS_NOT_REACHABLE;
import static com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR;
import static com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE;

public class KnDeleteUserProfileMdnTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnDeleteUserProfileMdnTask.class);
    private KnGenInfoUtil genInfoUtil;
    private KnGeneralCacheUtil generalCacheUtil;
    private String userProfileMdn;
    private String taskId;
    IProvClientIntf provClientIntf;
    private KnXDMCorpMediator corpMediator;
    private KnXDMCommonMediator commonMediator;
    IXcapDiffNotifierIntf notifier;
    private KnXDMPubDocMediator pubDocMediator;
    private KnJobSchedulerImpl scheduler;
    private int maxNotificationSize;


    public KnDeleteUserProfileMdnTask(){}

    public KnDeleteUserProfileMdnTask( String userProfileMdn, String corpId,String taskId) {
        this.userProfileMdn=userProfileMdn;
        this.taskId=taskId;
        genInfoUtil = KnGenInfoUtil.getInstance();
        generalCacheUtil = KnGeneralCacheUtil.getInstance();
        provClientIntf = KnProvClientImpl.getInstance();
        corpMediator = KnXDMCorpMediator.getInstance();
        commonMediator = KnXDMCommonMediator.getInstance();
        notifier = new KnXcapDiffNotifierImpl();
        pubDocMediator = KnXDMPubDocMediator.getInstance();
        scheduler = KnJobSchedulerImpl.getInstance();
        maxNotificationSize = 800; // TODO read from SVC config

    }


    @Override
    public KnTaskResult executeTask() {
    	 String methodName = "executeTask()-->DeleteUserProfileMdn";
         knLogger.debug(methodName,"calling delete subscriber for user profile");
          KnPersisterTxn persisterTxn = null;
          IXDMResponseDTO responseDTO = new KnXDMRespDTO();
        KnAuditHelper audit= KnAuditHelper.getAuditLogger("4002");
          KnTaskResult taskResult=new KnTaskResult();
          try {
              // opening the transaction
              persisterTxn = KnPersisterTxn.getPersisterTxn();
              knLogger.debug(methodName, "opening the transaction ");
              persisterTxn.open();
              String mdn = userProfileMdn;
              // retrieve the subscriber profile
              // this profile info will be useful in getting the type of
              // subscriber
              // if subscriber is public then invoke public library for deleting
              // of all grps/contacts
              // if subscriber is corp then invoke corp library for deleting of
              // owned grps/contacts
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
              // int corpSubscriptionType =
              // subsProfileInfoDTO.getCorporateSubscriptionType();
              // if (corpSubscriptionType ==
              // KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
              knLogger.debug(methodName, "invoking the corporate library ");
              // populating the corporate Library DTO
              KnXDMCorpSubscInfoRequestDTO contactRequestDTO = new KnXDMCorpSubscInfoRequestDTO();
              contactRequestDTO.setSubscriberMdn(userProfileMdn);
              contactRequestDTO.setHierarchyType(subsProfileInfoDTO.getHierarchyType());
              contactRequestDTO.setUpmCall(true);
              knLogger.debug(methodName," corp delete subscriber req :",contactRequestDTO);
              KnCorpResponseDTO corpResp = corpMediator.deleteSubscriber(contactRequestDTO, persisterTxn);
              int status = corpResp.getStatus();
              knLogger.debug(methodName, " corp Library response - ", corpResp);
              if (status != 0) {
                  throw new KnXDMServerException(corpResp.getStatusCode(), corpResp.getMessage());
              }
              knLogger.debug(methodName, "deleted the corporate data for mdn ");
              // }
              //prepare notification and send to notification mgr
              Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = commonMediator.prepareNotification(corpResp);
              knLogger.debug(methodName, "Sending notifications - ", xcapDiffList);
              boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, persisterTxn);
              knLogger.debug(methodName, "Notification status - ", isNotified);
              Collection<String> deletedMemberList = corpResp.getDisabledDispatchMemList();
              knLogger.debug(methodName, "deletedMemberList - ", KnGDPRTemplate.mdnList(deletedMemberList));
              commonMediator.sendTGSModeChangeNotification(corpResp.getTgsModeChgMap());
              knLogger.debug(methodName, "Sending LI notifications - ");
              KnLIEventHandler.logLI(corpResp.getLiEventList());
              knLogger.debug(methodName, "Li Notification Send status - ");
              int publicSubscriptionType = subsProfileInfoDTO.getPublicSubscriptionType();
              if (publicSubscriptionType == KnConstants.PUBLIC_SUBSCRIPTION_TYPE.PUBLIC.value()) {
                  knLogger.debug(methodName, "invoking the public library");
                  pubDocMediator.deleteAllContactsAndGroups(mdn, persisterTxn);
                  knLogger.debug(methodName, "deleted public data for mdn ");
              }
              // invoking the prov library for Deleting subscriber profile
              subscriberInfoDTO.setUpmCall(KnConstants.TRUE);
              subscriberInfoDTO.setHierarchyType(subsProfileInfoDTO.getHierarchyType());
              KnOPDeleteSubsRespDTO provRespDTO = provClientIntf.deleteSubscriber(subscriberInfoDTO, persisterTxn);
              knLogger.debug(methodName,"Delete Subscriber operation response - ", provRespDTO);
              // Initializing the KnSubscriberInfoDTO for base RestAPI
              KnSubscriberInfoDTO knSubscriberInfoDTO= new KnSubscriberInfoDTO();
              // Fetching the boolean value for 45th bit, If it is true then we will call the new REST API.
              boolean xcapCouchClient = KnGeneralUtil.getFeatureBitValue(provRespDTO.getActiveFS2(), FEATURE_SET.XCAPCOUCHCLIENT.value());
              knLogger.debug(methodName, "xcapCouchClient ", xcapCouchClient);
              // checking if 45th bit is 0/false or 1/true. If not 0/false then we make the rest call
              if(xcapCouchClient){
                  // Populating the data KnSubscriberInfoDTO knSubscriberInfoDTO.
                  knSubscriberInfoDTO.setMdn(provRespDTO.getMdn());
                  knLogger.debug(methodName, "knSubscriberInfoDTO : mdn",knSubscriberInfoDTO);
                  // Fetching the bucket URLS
                  Map<Integer, String> bucketUrlList = genInfoUtil.retrievePTXbucketUrls(persisterTxn);
                  knLogger.debug(methodName, "bucketUrlList : ", bucketUrlList);
                  KnFailedCBTxnLogDTO  failedCBTxnLogDTO = new KnFailedCBTxnLogDTO();
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
            
              // sending the response to the Client
              responseDTO = commonMediator.getSuccessResponse(responseDTO);
  			/*
  			 * Spawning a new thread for update of the DISPATCH_GRP_MEMBER FLAG
  			 * of Members of the dispatch groups that has been deleted
  			 */
              knLogger.debug(methodName, "deletedMemberList - ", KnGDPRTemplate.mdnList(deletedMemberList));
              // If deleted members list id not null and not empty start job to
              // update DISPATCH_GROUP_MEMBER field
              if (deletedMemberList != null && !deletedMemberList.isEmpty()) {
                  knLogger.debug(methodName,"Calling Dispatch group member job - ");
                  List<KnDispGrpMemChecker> jobList = new ArrayList<KnDispGrpMemChecker>(1);
                  KnDispGrpMemChecker dispGrpMemCheckerJob = new KnDispGrpMemChecker();
                  dispGrpMemCheckerJob.setMaxNotificationSize(maxNotificationSize);
                  // dispGrpMemCheckerJob.setSyncNotfyThresold(500);
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
        	  knLogger.debug(methodName,"User Profile Mdn deleted Successfully :",KnGDPRTemplate.mdn(userProfileMdn));
              generalCacheUtil.updateProfileMdn(userProfileMdn,taskId);

              if(corpResp != null){
                  boolean profileNotifyStatus = commonMediator.prepareMcxNotifyForProfileMdns(corpResp);
                  knLogger.debug(methodName, "sending Profile notification Status: ", profileNotifyStatus);
              }

              List<KnUnAssignEXDMSNotifyDto> notifyDtoList = new ArrayList<KnUnAssignEXDMSNotifyDto>();

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

          } catch (KnPersistenceException e) {
              knLogger.error(methodName, "Failed to get the Transaction", e);
              rollback(persisterTxn);
              audit.writeAuditMessage("Failed ProfileMdn:" + userProfileMdn, "DeleteUserProfileMdnTask", KnAuditHelper.STATUS.FAILURE, "DeleteUPMMDNTask request failed " + e.getErrorCode());
              KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_DELETION_FAILURE);
          } catch (KnXDMServerException e) {
              knLogger.error(methodName,"Failed to Delete User Profile Mdn Operation ", e);
              rollback(persisterTxn);
              audit.writeAuditMessage("Failed ProfileMdn:" + userProfileMdn, "DeleteUserProfileMdnTask", KnAuditHelper.STATUS.FAILURE, "DeleteUPMMDNTask request failed " + e.getErrorCode());
              KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_DELETION_FAILURE);
              if(e.getErrorCode().equals(CBS_NOT_REACHABLE)){
                  taskResult.setErrorMsg("restart-task");
              } else if (e.getErrorCode().equals(INTERNAL_ERROR) || e.getErrorCode().equals(PTT_SERVER_NOT_REACHABLE) || e.getErrorCode().equals(KnErrorCodes.DAO.CONNECTION_FAILED)){
                  taskResult.setTtTempError("TT-error");
              }
          } catch (Exception e) {
              knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
              rollback(persisterTxn);
              audit.writeAuditMessage("Failed ProfileMdn:" + userProfileMdn, "DeleteUserProfileMdnTask", KnAuditHelper.STATUS.FAILURE, "DeleteUPMMDNTask request failed " + e.getMessage());
              KnStatisticsManagerImpl.getInstance().increment(KnOMConstants.XDM_NUM_SUBSCR_DELETION_FAILURE);
          }
          knLogger.info(methodName, "EXIT: Delete User Profile Mdn taskResult - ",taskResult);
          return taskResult;
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
