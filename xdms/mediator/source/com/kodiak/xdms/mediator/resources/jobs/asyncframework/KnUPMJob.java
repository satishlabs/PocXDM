/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.asyncframework;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.kodiak.common.commdto.common.KnCorpGroupContactDTO;
import com.kodiak.common.commdto.request.KnXDMSubsInfoDTO;
import com.kodiak.common.commdto.response.KnXDMCorpUserProfileRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.ggcache.dto.KnAsyncJobTaskDTO;
import com.kodiak.common.resources.KnErrorCodes;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.mediator.helper.KnXDMCorpMediator;
import com.kodiak.xdms.mediator.impl.KnXDMMediatorV2;
import com.kodiak.xdms.mediator.resources.KnJobConstants;
import com.kodiak.xdms.mediator.resources.jobs.KnDispGrpMemChecker;
import com.kodiak.xdms.mediator.resources.jobs.upm.*;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnAssignEXDMSNotifyDto;
import com.kodiak.xdms.server.common.dto.common.KnMDNDetailsDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpContactManager;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpEXDMSNotifyDto;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpModifyUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.impl.KnProvClientImpl;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;

import static com.kodiak.common.resources.KnConstants.DEFAULT_EMERGENCY_TIMER;
import static com.kodiak.common.resources.KnConstants.MICROSERVICES_EVENT_TYPE.ASSIGN_USER_PROFILE;
import static com.kodiak.common.resources.KnConstants.MICROSERVICES_NOTIFY_EVENT_TYPE.USER_NOTIFY_EVENTS;
import static com.kodiak.common.resources.KnConstants.MICROSERVICE_NOTIFY_DOC_VER;
import static com.kodiak.xdms.server.common.resources.KnConstants.UPM_OPERATION_TYPE.RETRY_MODIFY_USER_PROFILE;

public class KnUPMJob extends KnAbstractJob implements Runnable {

    private static final KnLogger knLogger = KnLogger.getLogger(KnUPMJob.class);

    private String txnId;
    private KnAsyncJobDTO asyncJobDTO;
    private KnGeneralCacheUtil generalCacheUtil;
    private KnJobSchedulerImpl scheduler;
    private KnXDMCorpMediator corpMediator;
    private KnXDMCommonMediator commonMediator;
    private static final String DELIMIT_PIPE = "|";
    private final static String FAILED_MSG = "Failed to process.";
    //Below list is used to to get the upm count which has the lockwatcher
    List<Integer> upmCount = new ArrayList<>();

    //should be same as in KnTransaction
    private enum STATE {
        NEW, STARTED, COMMITED, ROLLBACK, COMMIT_FAIL, ROLLBACK_FAIL
    }

    KnUPMJob(String txnId, KnAsyncJobDTO asyncJobDTO) {
        this.txnId = txnId;
        this.asyncJobDTO = asyncJobDTO;
        generalCacheUtil = KnGeneralCacheUtil.getInstance();
        scheduler = KnJobSchedulerImpl.getInstance();
        corpMediator = KnXDMCorpMediator.getInstance();
        commonMediator = KnXDMCommonMediator.getInstance();
    }

    @Override
    public void run() {
        executeJob();
    }

    public boolean executeJob() {
        String methodName = "run()";
        knLogger.info(methodName, "Entry - txnId", txnId, "opType", asyncJobDTO.getOpType());

        try {
            writeAsyncJobAudit(asyncJobDTO, "REQUEST");
            if (asyncJobDTO.getOpType() == KnConstants.UPM_OPERATION_TYPE.ASSIGN_USER_PROFILE.Value()) {
                // generalCacheUtil.updateAsyncJo````kbStatus(txnId, KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
                //generalCacheUtil.updateCATTXNStatus(txnId, KnConstants.UPM_JOB_STATUS.INPROGRESS.Value(), "RUNNING", KnConstants.MSGREADSTATUS.UNREAD.Value());
                knLogger.info(methodName, "AssignUser profile started for txnId=", txnId);

                ICorpClientIntf corpClientIntf = new KnCorpClientImpl();
                IProvClientIntf provClientIntf = KnProvClientImpl.getInstance();
                KnPersisterTxn assignSubsTxn = null;
                KnCorpResponseDTO eTagcorpResponseDTO = null;
                KnCorpResponseDTO userProfileDetails = new KnCorpResponseDTO();
                try {
                    assignSubsTxn = KnPersisterTxn.getPersisterTxn();
                    assignSubsTxn.open();

                    //getting subscriber details
                    KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
                    subscriberInfoDTO.setMdn(asyncJobDTO.getResourceEntity());
                    //String corpId = String.valueOf(asyncJobDTO.getCorpId());
                    Result result = assignUserProfile(provClientIntf, assignSubsTxn, corpClientIntf, subscriberInfoDTO);
                    if (result.taskResult() != null && result.taskResult().getTaskStatus() == KnConstants.STATUS_SUCCESS) {
                        assignSubsTxn.save();
                        knLogger.info(methodName, "Transaction saved for txnId=", txnId);
                        //profile Notification to subscriber with the user profiles;
                        //profileNotification(result.userProfileId(), result.corpId());

                        if (result.assignGroupTaskResult() != null) {
                            initiateDispatchGrpJob(result.assignGroupTaskResult(), asyncJobDTO.getCorpId());
                        }

                        //updating job status
                        generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
                        generalCacheUtil.updateCatTxnStatusGg(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value(), "SUCCESS", KnConstants.MSGREADSTATUS.UNREAD.Value());
                    } else {
                        knLogger.error(methodName, FAILED_MSG, " for txnId= ", txnId);
                        rollback(assignSubsTxn);//we can remove this but rollback will be delay
                        throw new KnDAOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Failed to assign");
                    }
                } catch (Exception e) {
                    knLogger.error(methodName, "Error while assigning the user profile for txnId= ", txnId);
                    rollback(assignSubsTxn);
                    throw new KnDAOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Failed to assign");
                }

            } else if (asyncJobDTO.getOpType() == KnConstants.UPM_OPERATION_TYPE.MODIFY_USER_PROFILE.Value()) {
                knLogger.info(methodName, " ModifyUser profile started for txnId=", txnId);
                //   generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
                //generalCacheUtil.updateCATTXNStatus(txnId, KnConstants.UPM_JOB_STATUS.INPROGRESS.Value(), "RUNNING", KnConstants.MSGREADSTATUS.UNREAD.Value());

                ICorpClientIntf corpClientIntf = new KnCorpClientImpl();
                KnPersisterTxn modifyUpmQueryTxn = null;
                try {
                    modifyUpmQueryTxn = KnPersisterTxn.getPersisterTxn();
                    modifyUpmQueryTxn.open();

                    KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
                    ipUserProfileDTO.setCorpId(String.valueOf(asyncJobDTO.getCorpId()));
                    String userProfileId = asyncJobDTO.getUserProfileId();
                    ipUserProfileDTO.setProfileId(userProfileId);
                    KnCorpResponseDTO profileMdnResp = corpClientIntf.getProfileMdnByUPId(ipUserProfileDTO, false, modifyUpmQueryTxn);
                    KnCorpResponseDTO userProfileDetails = corpClientIntf.getUserProfileDetails(ipUserProfileDTO, false, modifyUpmQueryTxn);
                    Integer dbSublistId = userProfileDetails.getUserProfile().getContactListID();
                    if (null == userProfileDetails.getUserProfile().getEmergencyConfig().getEmergConfigTimer()) {
                        KnIPUserProfileDTO ipUserProfilePermDTO = KnCorpCommonInfoUtil.jsonToObject(asyncJobDTO.getPayLoad(), KnIPUserProfileDTO.class);
                        if (null == ipUserProfilePermDTO.getModifiedUserProfileDTO().getEmergencyConfig().getEmergConfigTimer()) {
                            final KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
                            final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
                            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, modifyUpmQueryTxn);
                            ipUserProfilePermDTO.getModifiedUserProfileDTO().getEmergencyConfig().setEmergConfigTimer(microServicesParamNameValueMap.get(DEFAULT_EMERGENCY_TIMER));
                            String modifyUpmJsonString = KnCorpCommonInfoUtil.ObjToJson(ipUserProfilePermDTO);
                            asyncJobDTO.setPayLoad(modifyUpmJsonString);
                        }
                    }
                    modifyUpmQueryTxn.save();
                    knLogger.info(methodName, " Modify UPM userProfileDetails:", userProfileDetails);
                    knLogger.info(methodName, " profileMdnResp part of upm:", profileMdnResp);
                    AtomicInteger cbsDocUpdateCount = new AtomicInteger();
                    AtomicInteger failedTxnCount = new AtomicInteger();
                    List<String> profileMdns = new ArrayList();

                    if (profileMdnResp != null && !profileMdnResp.getMdnList().isEmpty()) {
                        KnTaskResult modifyGroupTaskResult = new KnTaskResult();
                        userProfileDetails.setUpmCount(upmCount);
                        for (String profileMdn : profileMdnResp.getMdnList()) {
                            modifyGroupTaskResult = modifyUpmEachProfile(userProfileDetails, dbSublistId, cbsDocUpdateCount, failedTxnCount, modifyGroupTaskResult, profileMdn);
                            profileMdns.add(userProfileDetails.getMdn());

                            modifyLiEventContactNotification(profileMdn, ipUserProfileDTO, dbSublistId, profileMdns);
                        }

                        //if all profile mdn updates fails then modifyUpm notify will be failed.
                        if (failedTxnCount.get() == profileMdnResp.getMdnList().size()) {
                            knLogger.error(methodName, " Failed modify upm for all profile mdn ", profileMdnResp.getMdnList(), " for upmId:", userProfileId);
                            knLogger.error(methodName, FAILED_MSG, " for txnId= ", txnId);
                            throw new KnDAOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Failed to update");
                        } else {
                            //profile Notification to subscriber with the user profiles;
                            profileNotification(asyncJobDTO.getUserProfileId(), String.valueOf(asyncJobDTO.getCorpId()));

                            generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
                            generalCacheUtil.updateCatTxnStatusGg(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value(), "SUCCESS", KnConstants.MSGREADSTATUS.UNREAD.Value());
                            knLogger.info(methodName, " Done modify upm for all profile mdns ");
                        }
                        //Above case will lead to the stale enteries if any temporary failure occurs. Hence below code is added which will handle the case if any profile mdns update fails then those failed mdns are updated with status as 9.
                        if (failedTxnCount.get() > 0 && failedTxnCount.get() != profileMdnResp.getMdnList().size()) {
                            String payload = asyncJobDTO.getPayLoad();
                            int corpId = asyncJobDTO.getCorpId();
                            knLogger.debug(methodName, "failedTxnCount: ", failedTxnCount.get(), " profileMdnResp.getMdnList().size(): ", profileMdnResp.getMdnList().size(), "failedMdnList::", KnGDPRTemplate.profileMdn(profileMdns));
                            profileMdns.removeIf(Objects::isNull);
                            UUID uuid = UUID.randomUUID();
                            generalCacheUtil.insertTempStaleRecords(uuid.toString(), KnConstants.UPM_JOB_STATUS.RETRY.Value(), profileMdns, userProfileId, corpId, payload, RETRY_MODIFY_USER_PROFILE.Value());
                        }
                    } else {
                        knLogger.info(methodName, "updating UPM CB doc ");
                        //Case where only upm template is modified and not assinged to any subscriber.
                        KnPersisterTxn modifyUpmCBTxn = null;
                        try {
                            modifyUpmCBTxn = KnPersisterTxn.getPersisterTxn();
                            modifyUpmCBTxn.open();
                            KnTaskResult taskResult = modifyUpm(modifyUpmCBTxn);
                            knLogger.debug(methodName, " taskResult- ", taskResult);
                            if (taskResult != null && taskResult.getTaskStatus() == KnConstants.STATUS_SUCCESS) {
                                modifyUpmCBTxn.save();
                                generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
                                generalCacheUtil.updateCatTxnStatusGg(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value(), "SUCCESS", KnConstants.MSGREADSTATUS.UNREAD.Value());
                                knLogger.info(methodName, " Done modify upm for upmId:", userProfileId);
                            } else {
                                rollback(modifyUpmCBTxn);
                                knLogger.error(methodName, " Failed modify upm for upmId:", userProfileId);
                                knLogger.error(methodName, FAILED_MSG, " for txnId= ", txnId);
                                throw new KnDAOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Failed to assign");
                            }
                        } catch (Exception e) {
                            rollback(modifyUpmCBTxn);
                            knLogger.error(methodName, " Failed modify upm for upmId:", userProfileId);
                            throw new KnDAOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Failed to modify");
                        }
                    }

                    generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
                    generalCacheUtil.updateCatTxnStatusGg(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value(), "SUCCESS", KnConstants.MSGREADSTATUS.UNREAD.Value());
                } catch (Exception e) {
                    rollback(modifyUpmQueryTxn);
                    knLogger.error(methodName, " Failed modify upm for txnId:", txnId);
                    throw new KnDAOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Failed to Modify");
                }

            } else if (asyncJobDTO.getOpType() == KnConstants.UPM_OPERATION_TYPE.DELETE_USER_PROFILE_MDN.Value()) {
                // generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
                //deleteUserProfileMdns
                KnIPUserProfileDTO ipUserProfilePermDTO = KnCorpCommonInfoUtil.jsonToObject(asyncJobDTO.getPayLoad(), KnIPUserProfileDTO.class);
                deleteUserProfileMdns(ipUserProfilePermDTO);
                generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value());

            } else if (asyncJobDTO.getOpType() == KnConstants.UPM_OPERATION_TYPE.DELETE_GROUP_FROM_PROFILE.Value()) {
                //  generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
                deleteGroupFromUserProfile();
                generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
            } else if (asyncJobDTO.getOpType() == KnConstants.UPM_OPERATION_TYPE.DELETE_GROUP_SHARED_USER_PROFILE_NOTIFICATION.Value()) {
                // generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
                deleteGroupSharedUserProfileNotification();
                generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
            } else if (asyncJobDTO.getOpType() == RETRY_MODIFY_USER_PROFILE.Value()) {
                knLogger.info(methodName, "Retry ModifyUser profile started for txnId=", txnId);
                IProvClientIntf provClientIntf = KnProvClientImpl.getInstance();
                ICorpClientIntf corpClientIntf = new KnCorpClientImpl();
                KnPersisterTxn modifyUpmQueryTxn = null;
                try {
                    modifyUpmQueryTxn = KnPersisterTxn.getPersisterTxn();
                    modifyUpmQueryTxn.open();

                    String userProfileMdn = asyncJobDTO.getResourceEntity();
                    KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
                    subscriberInfoDTO.setMdn(userProfileMdn);
                    KnOPSubsProfileInfoDTO subsDetails = provClientIntf.getSubscriberDetails(subscriberInfoDTO, modifyUpmQueryTxn);
                    if (subsDetails.getServiceAuthStatus() != 2) {
                        knLogger.debug(methodName, "Subscriber is not authorized for service", txnId);
                        KnIPUserProfileDTO ipUserProfilePermDTO = new KnIPUserProfileDTO();
                        List<String> userProfileMdns = new ArrayList<>();
                        userProfileMdns.add(userProfileMdn);
                        ipUserProfilePermDTO.setUserProfileMdns(userProfileMdns);
                        if (!subsDetails.getUserProfileId().equals("0")) {
                            String mdn = provClientIntf.getBaseMdnByProfileMdn(userProfileMdn, modifyUpmQueryTxn);
                            subscriberInfoDTO.setMdn(mdn);
                        }
                        deleteUserProfileMdns(ipUserProfilePermDTO);
                        asyncJobDTO.setResourceEntity(subscriberInfoDTO.getMdn());
                        knLogger.debug(methodName, "after deleteUserProfileMdns :");
                        assignUserProfile(provClientIntf, modifyUpmQueryTxn, corpClientIntf, subscriberInfoDTO);
                    } else {
                        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
                        ipUserProfileDTO.setCorpId(String.valueOf(asyncJobDTO.getCorpId()));
                        String userProfileId = asyncJobDTO.getUserProfileId();
                        ipUserProfileDTO.setProfileId(userProfileId);
                        KnCorpResponseDTO userProfileDetails = corpClientIntf.getUserProfileDetails(ipUserProfileDTO, false, modifyUpmQueryTxn);
                        Integer dbSublistId = userProfileDetails.getUserProfile().getContactListID();
                        modifyUpmQueryTxn.save();
                        knLogger.debug(methodName, " Modify UPM userProfileDetails:", userProfileDetails);
                        AtomicInteger cbsDocUpdateCount = new AtomicInteger();
                        AtomicInteger failedTxnCount = new AtomicInteger();
                        List<String> profileMdns = new ArrayList<>();
                        profileMdns.add(userProfileDetails.getMdn());
                        KnTaskResult modifyGroupTaskResult = new KnTaskResult();
                        userProfileDetails.setUpmCount(upmCount);
                        modifyGroupTaskResult = modifyUpmEachProfile(userProfileDetails, dbSublistId, cbsDocUpdateCount, failedTxnCount, modifyGroupTaskResult, userProfileMdn);
                        modifyLiEventContactNotification(userProfileMdn, ipUserProfileDTO, dbSublistId, profileMdns);
                        String dbMaxRetryValue = asyncJobDTO.getCallBackUri();
                        int dbMaxRetry = dbMaxRetryValue == null ? 0 : Integer.parseInt(dbMaxRetryValue);
                        boolean dbMaxreached = false;
                        if (dbMaxRetry >= 1) {
                            dbMaxreached = true;
                        }
                        if (failedTxnCount.get() == 0 && !dbMaxreached) {
                            KnXDMCommonMediator commonMediator = KnXDMCommonMediator.getInstance();
                            long transactionId = System.currentTimeMillis();
                            dbMaxRetry = dbMaxRetry + 1;
                            knLogger.error(methodName, " dbMaxRetry: ", dbMaxRetry);
                            KnAsyncJobDTO knAsyncJobDTO = commonMediator.createJobNotifyDTO(String.valueOf(asyncJobDTO.getCorpId()), String.valueOf(transactionId)
                                    , asyncJobDTO.getUserProfileId(), RETRY_MODIFY_USER_PROFILE.Value(), asyncJobDTO.getResourceEntity(),
                                    KnConstants.UPM_RESOURCE_TYPE.PROFILEMDN.Value(), asyncJobDTO.getPayLoad(), KnConstants.UPM_JOB_STATUS.RETRY.Value(), String.valueOf(dbMaxRetry));
                            generalCacheUtil.createAsyncJob(knAsyncJobDTO);
                        }
                    }
                    generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.FAILURE.Value());
                } catch (Exception e) {
                    rollback(modifyUpmQueryTxn);
                    knLogger.error(methodName, " Failed modify upm for txnId:", txnId);
                    throw new KnDAOException(KnErrorCodes.DAO.INTERNAL_ERROR, "Failed to Modify");
                }

            } else {
                knLogger.error(methodName, "Invalid operation type for txnId=", txnId);
                generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.FAILURE.Value());
                generalCacheUtil.updateCatTxnStatusGg(txnId, KnConstants.UPM_JOB_STATUS.FAILURE.Value(), "FAILURE", KnConstants.MSGREADSTATUS.UNREAD.Value());
                writeAsyncJobAudit(asyncJobDTO, "FAILURE");
            }
            writeAsyncJobAudit(asyncJobDTO, "SUCCESS");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured in job for txnId-", txnId, " error msg:", e.getMessage());
            try {
                generalCacheUtil.updateFailedJobStatusWithRetry(txnId, KnConstants.UPM_JOB_STATUS.FAILURE.Value());
                generalCacheUtil.updateFailedCatTxnStatusWithRetry(txnId, KnConstants.UPM_JOB_STATUS.FAILURE.Value(), "FAILURE", KnConstants.MSGREADSTATUS.UNREAD.Value());
                writeAsyncJobAudit(asyncJobDTO, "FAILURE");
            } catch (KnDAOException ex) {
                knLogger.error(methodName, "2nd KnDAOException occured in job for txnId-", txnId, " error msg:", e.getMessage());
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured in job for txnId-", txnId, " error msg:", e.getMessage());
            try {
                generalCacheUtil.updateFailedJobStatusWithRetry(txnId, KnConstants.UPM_JOB_STATUS.FAILURE.Value());
                generalCacheUtil.updateFailedCatTxnStatusWithRetry(txnId, KnConstants.UPM_JOB_STATUS.FAILURE.Value(), "FAILURE", KnConstants.MSGREADSTATUS.UNREAD.Value());
                writeAsyncJobAudit(asyncJobDTO, "FAILURE");
            } catch (KnDAOException ex) {
                knLogger.error(methodName, "2nd Exception occured in job for txnId-", txnId, " error msg:", e.getMessage());
            }

        } catch (Throwable t) {
            knLogger.error(methodName, "Unexpected Throwable occured in job for txnId-", txnId, " error msg:", t.getMessage());
            try {
                generalCacheUtil.updateFailedJobStatusWithRetry(txnId, KnConstants.UPM_JOB_STATUS.FAILURE.Value());
                generalCacheUtil.updateFailedCatTxnStatusWithRetry(txnId, KnConstants.UPM_JOB_STATUS.FAILURE.Value(), "FAILURE", KnConstants.MSGREADSTATUS.UNREAD.Value());
                writeAsyncJobAudit(asyncJobDTO, "FAILURE");
            } catch (KnDAOException ex) {
                knLogger.error(methodName, "Failed to update job status after Throwable for txnId-", txnId, " error msg:", ex.getMessage());
            }
        }
        return false;
    }

    private void modifyLiEventContactNotification(String profileMdn, KnIPUserProfileDTO ipUserProfileDTO, Integer dbSublistId, List<String> profileMdns) throws KnPersistenceException {
        String methodName = "modifyLiEventContactNotification(String,KnIPUserProfileDTO,Integer,List<String>)";
        Integer addedSublistId = null;
        Integer removedSublistId = null;
        KnIPCorpSubscContactListDTO contactListDTO = new KnIPCorpSubscContactListDTO();
        //profile mdn to be set from mediator
        contactListDTO.setSubscriberMdn(profileMdn);
        contactListDTO.setCorpId(Integer.parseInt(ipUserProfileDTO.getCorpId()));

        KnIPUserProfileDTO ipUserProfilePermDTO = KnCorpCommonInfoUtil.jsonToObject(asyncJobDTO.getPayLoad(), KnIPUserProfileDTO.class);
        KnCorpModifyUserProfileDTO modifyUpmReq = ipUserProfilePermDTO.getModifiedUserProfileDTO();
        knLogger.debug(methodName, "modifyUpmReq:", ipUserProfilePermDTO);
        //get the value from req
        Integer reqSubListId = modifyUpmReq.getContactListID();

        knLogger.debug(methodName, "reqSubListId:", reqSubListId, "dbSublistId:", dbSublistId);
        if (reqSubListId != null && !reqSubListId.equals(-1)) {
            addedSublistId = reqSubListId;
            if (!reqSubListId.equals(dbSublistId)) {
                removedSublistId = dbSublistId;
            }
        } else if (reqSubListId != null && reqSubListId.equals(-1)) {
            removedSublistId = dbSublistId;
        }
        Collection<Integer> addedSublistIds = new ArrayList<>();
        if (addedSublistId != null) {
            addedSublistIds.add(addedSublistId);
        }
        Collection<Integer> removedSublistIds = new ArrayList<>();
        if (removedSublistId != null) {
            removedSublistIds.add(removedSublistId);
        }
        contactListDTO.setAddedSublistIds(addedSublistIds);
        contactListDTO.setRemovedSublistIds(removedSublistIds);
        Collection<String> addedMdnList = new ArrayList<>();
        Collection<String> removedMdnList = new ArrayList<>();
        contactListDTO.setAddedMdnList(addedMdnList);
        contactListDTO.setRemovedMdnList(removedMdnList);

        ICorpContactManager corpContactManager = new KnCorpClientImpl();
        KnPersisterTxn modifyUpmTxn = KnPersisterTxn.getPersisterTxn();
        modifyUpmTxn.open();
        KnCorpResponseDTO resp = corpContactManager.getLiEvents(contactListDTO, profileMdns, dbSublistId, modifyUpmTxn);
        KnLIEventHandler.logLI(resp.getLiEventList());
        modifyUpmTxn.save();
    }

    private void sendAssignNotification(KnCorpResponseDTO eTagcorpResponseDTO,KnTaskResult assignGroupTaskResult , KnIPSubscriberInfoDTO subscriberInfoDTO, String corpId) throws KnException {
        String methodName = "sendAssignNotification(KnCorpResponseDTO,Result,KnIPSubscriberInfoDTO,String)";
        knLogger.info(methodName, "AssignNotification started");
        knLogger.debug(methodName, "eTagcorpResponseDTO: ", eTagcorpResponseDTO, "assignGroupTaskResult: ", assignGroupTaskResult.getAssignGroupResp(), "subscriberInfoDTO: ", subscriberInfoDTO, "corpId: ", corpId);
        ICorpClientIntf corpClientIntf = new KnCorpClientImpl();
        IProvClientIntf provClientIntf = KnProvClientImpl.getInstance();
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
        ipUserProfileDTO.setCorpId(String.valueOf(eTagcorpResponseDTO.getIntCorpId()));
        ipUserProfileDTO.setProfileId(eTagcorpResponseDTO.getUserProfileId());
        KnPersisterTxn userProfileNotificationTxn = KnPersisterTxn.getPersisterTxn();
        userProfileNotificationTxn.open();
        KnCorpResponseDTO profileNotifyResp = corpClientIntf.userProfileNotficationForUpm(ipUserProfileDTO, userProfileNotificationTxn);
        knLogger.debug(methodName, " profileNotifyResp:", profileNotifyResp);
        boolean status = commonMediator.prepareMcxNotifyForProfileMdns(profileNotifyResp);
        knLogger.info(methodName, "sending Profile notification Status: ", status);
        boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(userProfileNotificationTxn);
        List<KnAssignEXDMSNotifyDto> notifyDtoList = new ArrayList<>();
        KnOPSubsProfileInfoDTO subsProfileInfo = provClientIntf.getSubscriberDetails(subscriberInfoDTO, userProfileNotificationTxn);
        userProfileNotificationTxn.save();
        List<KnCorpEXDMSNotifyDto> microserviceNotify = new ArrayList<>();
        knLogger.info(methodName, "xcapMobileSync:: ", xcapMobileSync);
        if (xcapMobileSync) {
            KnAssignEXDMSNotifyDto subscrEXDMSNotifyDto = new KnAssignEXDMSNotifyDto();
            subscrEXDMSNotifyDto.setId(ASSIGN_USER_PROFILE.value() + KnConstants.LINE_SAPERATOR + subscriberInfoDTO.getMdn());
            subscrEXDMSNotifyDto.setType(ASSIGN_USER_PROFILE.value());
            subscrEXDMSNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
            subscrEXDMSNotifyDto.setCorpid(Integer.parseInt(corpId));
            subscrEXDMSNotifyDto.setClientType(subsProfileInfo.getSubsClientType());
            subscrEXDMSNotifyDto.setPv(subsProfileInfo.getClientPVmajorVer() + "." + subsProfileInfo.getClientPVminorVer());
            KnMDNDetailsDTO bMdn = new KnMDNDetailsDTO();
            bMdn.setMdn(subscriberInfoDTO.getMdn());
            bMdn.setActiveFS(subsProfileInfo.getActiveFS2());
            bMdn.setLastProfileUpdateTime(subsProfileInfo.getLastProfileUpdateTime());
            subscrEXDMSNotifyDto.setBaseMdn(bMdn);
            List<KnMDNDetailsDTO> pMdnsList = new ArrayList<>();
            KnMDNDetailsDTO pMdn = new KnMDNDetailsDTO();
            pMdn.setMdn(eTagcorpResponseDTO.getProfileMdn());
            pMdn.setActiveFS(eTagcorpResponseDTO.getActiveFS2());
            pMdn.setLastProfileUpdateTime(eTagcorpResponseDTO.getLastUpdateprofileTime());
            subscrEXDMSNotifyDto.setNotifyEventType(USER_NOTIFY_EVENTS.value());
            pMdnsList.add(pMdn);
            subscrEXDMSNotifyDto.setProfileMdns(pMdnsList);
            notifyDtoList.add(subscrEXDMSNotifyDto);
            if (!notifyDtoList.isEmpty()) {
                knLogger.info(methodName, "Publishing micro service notify for Assign Subscriber Task");
                    commonMediator.startNotifyMicroServicesJob(notifyDtoList);
            }

            KnPersisterTxn liEvevntUpmTxn = KnPersisterTxn.getPersisterTxn();
            liEvevntUpmTxn.open();

            KnIPCorpSubscContactListDTO contactListDTO = new KnIPCorpSubscContactListDTO();
            //profile mdn to be set from mediator
            contactListDTO.setSubscriberMdn(eTagcorpResponseDTO.getProfileMdn());
            contactListDTO.setCorpId(Integer.parseInt(ipUserProfileDTO.getCorpId()));

            KnCorpResponseDTO userProfileDetails = corpClientIntf.getUserProfileDetails(ipUserProfileDTO, false, liEvevntUpmTxn);
            Integer dbSublistId = userProfileDetails.getUserProfile().getContactListID();
            ICorpContactManager corpContactManager = new KnCorpClientImpl();
            List<String> profileMdns = new ArrayList<>();
            profileMdns.add(subscriberInfoDTO.getMdn());
            Collection<Integer> addedSublistIds = new ArrayList<>();
            if (dbSublistId != null) {
                addedSublistIds.add(dbSublistId);
                Collection<Integer> removedSublistIds = new ArrayList<>();
                //adding target mdn of the UMP to contact list of profile mdn
                Collection<String> addedMdnList = new ArrayList<>();
                Collection<String> removedMdnList = new ArrayList<>();

                contactListDTO.setAddedSublistIds(addedSublistIds);
                contactListDTO.setRemovedSublistIds(removedSublistIds);
                contactListDTO.setAddedMdnList(addedMdnList);
                contactListDTO.setRemovedMdnList(removedMdnList);
                KnCorpResponseDTO resp = corpContactManager.getLiEvents(contactListDTO, profileMdns, dbSublistId, liEvevntUpmTxn);
                KnLIEventHandler.logLI(resp.getLiEventList());
            }
            liEvevntUpmTxn.save();


            knLogger.debug("eTagcorpResponseDTO::", eTagcorpResponseDTO);
            KnCorpResponseDTO grpResponseDTO = assignGroupTaskResult.getAssignGroupResp();
            Set<KnCorpGroupListInfoDTO> upmGroups = assignGroupTaskResult.getUpmGroups();
            if (grpResponseDTO != null) {
                if (null != upmGroups && !upmGroups.isEmpty()) {
                    knLogger.info("EntryTime::", System.currentTimeMillis());
                    for (KnCorpGroupListInfoDTO groupInfo : upmGroups) {
                        if (assignGroupTaskResult.getMcxGrpInd() == 1) {
                            knLogger.debug("InsideMCX");
                            KnCorpGroupContactDTO memberProps = new KnCorpGroupContactDTO(groupInfo.getGrpMemProps().getIsSupervisor(),
                                    groupInfo.getGrpMemProps().getIsBroadcaster(), groupInfo.getGrpMemProps().getIsLocSupervisor(), groupInfo.getGrpMemProps().getIsOSMAuthorized(),
                                    groupInfo.getGrpMemProps().getCallInitiateAllowed(), groupInfo.getGrpMemProps().getCallTerminateAllowed(),groupInfo.getGrpMemProps().getIncallAllowed()
                            ,groupInfo.getGrpMemProps().getVideoCallInitiateAllowed(),
                                    groupInfo.getGrpMemProps().getVideoCallReceiveAllowed(),
                                    groupInfo.getGrpMemProps().getVideoInCallAllowed());
                            List<KnCorpEXDMSNotifyDto> grpNotifyDtoList = commonMediator.getModifyMcxGrpMicroSrvNotifyDto(grpResponseDTO, Integer.parseInt(corpId),
                                    groupInfo.getGroupID(), eTagcorpResponseDTO.getUserProfileId(), memberProps, grpResponseDTO.getOldLmrInteropFlag());
                            microserviceNotify.addAll(grpNotifyDtoList);
                            knLogger.info("grpNotifyDtoList::", grpNotifyDtoList.size());
                            if (!microserviceNotify.isEmpty()) {
                                commonMediator.startNotifyMicroServicesJob(microserviceNotify);
                            }
                        } else {
                            knLogger.debug("InsideNonMCX", eTagcorpResponseDTO.getChangeLogMap());
                            commonMediator.startNotifyMicroServicesJob(eTagcorpResponseDTO.getChangeLogMap(), Integer.parseInt(corpId), grpResponseDTO.getGroupCreatedBy(),
                                    grpResponseDTO.getLmrIntropCapable(), grpResponseDTO.getMcxGrpInd(), grpResponseDTO.getOldLmrInteropFlag());
                        }
                    }
                }
            }
        }
        knLogger.info(methodName, "Notification completed ");
    }

    private Result assignUserProfile(IProvClientIntf provClientIntf, KnPersisterTxn assignSubsTxn, ICorpClientIntf corpClientIntf, KnIPSubscriberInfoDTO subscriberInfoDTO) throws KnException {
        String methodName = "assignUserProfile()";
        knLogger.info(methodName, "Entry - ");
        KnOPSubsProfileInfoDTO subsProfileInfoDTO = provClientIntf.getSubscriberDetails(subscriberInfoDTO, assignSubsTxn);
        //getting userprofile details
        String userProfileId = asyncJobDTO.getUserProfileId();
        String corpId = String.valueOf(asyncJobDTO.getCorpId());
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
        KnCorpResponseDTO eTagcorpResponseDTO = null;
        ipUserProfileDTO.setCorpId(corpId);
        ipUserProfileDTO.setProfileId(userProfileId);
        KnCorpResponseDTO userProfileDetails = corpClientIntf.getUserProfileDetails(ipUserProfileDTO, false, assignSubsTxn);
        knLogger.info(methodName, " userProfileDetails in assignUPM task =", userProfileDetails);

        //create Subs
        KnTaskResult taskResult = null;
        KnTaskResult assignSubscriberTask = createSubsTask(subsProfileInfoDTO, userProfileDetails, asyncJobDTO.getPayLoad(), assignSubsTxn);
        KnIPUserProfileDTO ipUserProfilePermDTO = KnCorpCommonInfoUtil.jsonToObject(asyncJobDTO.getPayLoad(), KnIPUserProfileDTO.class);
        String hierarchyType = String.valueOf(ipUserProfilePermDTO.getHierarchyType().value());
        knLogger.debug(methodName, "hierarchyType: ", hierarchyType);
        //getting profile mdn
        //String profileMdn=generalCacheUtil.getProfileMdnByTaskId(createTaskId);
        taskResult = assignSubscriberTask;
        String profileMdn = taskResult.getProfileMdn();
        String activeFS2 = taskResult.getActiveFS2();
        Long lastProfileUpdate = taskResult.getLastUpdateprofileTime();

        //assign contact task
        knLogger.debug(methodName, "assign contact transaction status =", assignSubsTxn.getTransactionStatus(),
                " taskResult- ", taskResult);
        if (taskResult != null && taskResult.getTaskStatus() == KnConstants.STATUS_SUCCESS) {
            taskResult = assignContactTask(profileMdn, subsProfileInfoDTO, userProfileDetails, assignSubsTxn);
        }

        //assign group
        KnTaskResult assignGroupTaskResult = null;
        knLogger.debug(methodName, "assign group transaction status =", assignSubsTxn.getTransactionStatus(),
                " taskResult- ", taskResult);
        if (taskResult != null && taskResult.getTaskStatus() == KnConstants.STATUS_SUCCESS) {
            assignGroupTaskResult = assignGroupTask(profileMdn, userProfileDetails, assignSubsTxn);
            if (userProfileDetails.getUserProfile().getTgscMode() != null) {
                assignGroupTaskResult.setTgscMode(userProfileDetails.getUserProfile().getTgscMode());
            }
            taskResult = assignGroupTaskResult;
        }


        //set target perms
        knLogger.debug(methodName, "assign permission transaction status =", assignSubsTxn.getTransactionStatus(),
                " taskResult- ", taskResult);
        if (taskResult != null && taskResult.getTaskStatus() == KnConstants.STATUS_SUCCESS) {
            taskResult = setTargetPermTask(profileMdn, userProfileDetails, assignSubsTxn);
        }
        //set emergency attribute
        knLogger.debug(methodName, "assign contact emergency status =", assignSubsTxn.getTransactionStatus(),
                " taskResult- ", taskResult);
        if (taskResult != null && taskResult.getTaskStatus() == KnConstants.STATUS_SUCCESS) {
            taskResult = setEmergencyAttributesTask(profileMdn, userProfileDetails, hierarchyType, assignSubsTxn);
        }
        //profile mdn,set target perms for all AU,where this base mdn is TU
        knLogger.debug(methodName, "assign permission to all AU transaction status =", assignSubsTxn.getTransactionStatus(),
                " taskResult- ", taskResult);
        if (taskResult != null && taskResult.getTaskStatus() == KnConstants.STATUS_SUCCESS) {
            taskResult = setPermissionToAllAU(profileMdn, assignSubsTxn);
        }
        userProfileDetails.setMdn(subscriberInfoDTO.getMdn());
        userProfileDetails.setIntCorpId(Integer.parseInt(corpId));
        userProfileDetails.setProfileMdn(profileMdn);
        userProfileDetails.setTgscMode(assignGroupTaskResult.getTgscMode());
        if (null != assignGroupTaskResult.getGroupIds()) {
            userProfileDetails.setGroupIds(assignGroupTaskResult.getGroupIds());
        }
        knLogger.debug(methodName, "userProfileDetails for selfEtagUpdate: ", userProfileDetails);
        if (taskResult != null && taskResult.getTaskStatus() == KnConstants.STATUS_SUCCESS) {
            eTagcorpResponseDTO = corpMediator.updateSelfEtag(userProfileDetails, assignSubsTxn);
            userProfileDetails.setUserProfileId(userProfileId);
            userProfileDetails.setActiveFS2(activeFS2);
            userProfileDetails.setLastUpdateprofileTime(lastProfileUpdate);
            userProfileDetails.setChangeLogMap(eTagcorpResponseDTO.getChangeLogMap());
            sendAssignNotification(userProfileDetails, assignGroupTaskResult, subscriberInfoDTO, corpId);
        }
        Result result = new Result(userProfileId, corpId, taskResult, assignGroupTaskResult, assignSubscriberTask);
        knLogger.debug(methodName, "result: ", result);
        return result;
    }

    private record Result(String userProfileId, String corpId, KnTaskResult taskResult,
                          KnTaskResult assignGroupTaskResult, KnTaskResult assignSubscriberTask) {
    }

    private void deleteUserProfileMdns(KnIPUserProfileDTO ipUserProfilePermDTO) throws KnDAOException {
        String methodName = "deleteUserProfileMdns()";
        knLogger.info(methodName, "deleteUserProfileMdnReq:", ipUserProfilePermDTO);
        List<String> userProfileMdns = ipUserProfilePermDTO.getUserProfileMdns();
        List<String> resubmitDeleteUserProfileMdns = new ArrayList<>();
        for (String deleteProfileMdn : userProfileMdns) {
            try {
                /*Commenting this path and calling Delete subscriber path
                Below path should trigger all the userProfile related notifications
                KnTaskResult taskResult = deleteUserProfileMdn(deleteProfileMdn);
                if (taskResult.getErrorMsg() != null) {
                    resubmitDeleteUserProfileMdns.add(deleteProfileMdn);
                }*/
                KnXDMMediatorV2 knXDMMediatorV2 = KnXDMMediatorV2.getInstance();
                boolean result = knXDMMediatorV2.prepareAndSendDeleteSubscriberRequest(deleteProfileMdn);
                if (!result) {
                    resubmitDeleteUserProfileMdns.add(deleteProfileMdn);
                }
                knLogger.debug(methodName, "deleteUserProfileMdnReq:", deleteProfileMdn, " is successful", result);
            } catch (Exception ex) {
                knLogger.error(methodName, "Failure during deleteUserProfileMdn ,", ex.getStackTrace());
            }
        }
        String dbMaxRetryValue = asyncJobDTO.getCallBackUri();
        int dbMaxRetry = dbMaxRetryValue == null ? 0 : Integer.parseInt(dbMaxRetryValue);
        boolean dbMaxreached = false;
        if (dbMaxRetry >= 5) {
            dbMaxreached = true;
        }
        knLogger.debug(methodName, "resubmitDeleteUserProfileMdns :", resubmitDeleteUserProfileMdns
                , " dbMaxRetryValue :", dbMaxRetryValue, " dbMaxreached :", dbMaxreached);
        if (!resubmitDeleteUserProfileMdns.isEmpty() && !dbMaxreached) {
            KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
            int corpId = asyncJobDTO.getCorpId();
            KnXDMCommonMediator commonMediator = KnXDMCommonMediator.getInstance();
            ipUserProfileDTO.setCorpId(String.valueOf(asyncJobDTO.getCorpId()));
            ipUserProfileDTO.setUserProfileMdns(resubmitDeleteUserProfileMdns);
            String deleteUpmJsonString = KnCorpCommonInfoUtil.ObjToJson(ipUserProfileDTO);
            long transactionId = System.currentTimeMillis();
            KnAsyncJobDTO knAsyncJobDTO = commonMediator.createJobNotifyDTO(String.valueOf(corpId), String.valueOf(transactionId)
                    , null, KnConstants.UPM_OPERATION_TYPE.DELETE_USER_PROFILE_MDN.Value(), null,
                    KnConstants.UPM_RESOURCE_TYPE.MDN.Value(), deleteUpmJsonString, KnConstants.UPM_JOB_STATUS.NEW.Value(), String.valueOf(dbMaxRetry + 1));
            generalCacheUtil.createAsyncJob(knAsyncJobDTO);
        }
    }

    private KnTaskResult modifyUpmEachProfile(KnCorpResponseDTO userProfileDetails, Integer dbSublistId,
                                              AtomicInteger cbsDocUpdateCount, AtomicInteger failedTxnCount,
                                              KnTaskResult modifyGroupTaskResult, String profileMdn) {
        String methodName = "modifyUpmEachProfile(KnCorpResponseDTO, Integer , AtomicInteger, AtomicInteger, KnTaskResult, String)";
        KnPersisterTxn modifyUpmTxn = null;
        Map<Integer, Collection<KnCorpContactDTO>> getAddedGroupMembersMap = new HashMap<>();
        try {
            knLogger.entry(methodName, "Start: profileMdn- ", profileMdn, " failedTxnCount- ", failedTxnCount, " cbsDocUpdateCount-", cbsDocUpdateCount);
            //open transaction
            modifyUpmTxn = KnPersisterTxn.getPersisterTxn();
            modifyUpmTxn.open();
            //USERPROFILEFS2
            KnTaskResult taskResult = modifyUpmFS(profileMdn, modifyUpmTxn);

            //modifyGroupUpm
            knLogger.debug(methodName, " modify upm group transaction status =", modifyUpmTxn.getTransactionStatus(), " taskResult- ", taskResult);
            if (taskResult != null && taskResult.getTaskStatus() == KnConstants.STATUS_SUCCESS) {
                modifyGroupTaskResult = modifyUpmGroup(profileMdn, modifyUpmTxn, userProfileDetails);
                taskResult = modifyGroupTaskResult;
                upmCount.add(modifyGroupTaskResult.getUpmCount());
                userProfileDetails.setGroupIds(modifyGroupTaskResult.getGroupIds());
                if (modifyGroupTaskResult.getAddGroupResp() != null && modifyGroupTaskResult.getAddGroupResp().getAddedGroupMembersMap() != null) {
                    getAddedGroupMembersMap = modifyGroupTaskResult.getAddGroupResp().getAddedGroupMembersMap();
                }
                if (modifyGroupTaskResult.getRemoveGroupResp() != null && modifyGroupTaskResult.getRemoveGroupResp().getDeletedGrpMembers() != null) {
                    userProfileDetails.setDeletedGrpMembers(modifyGroupTaskResult.getRemoveGroupResp().getDeletedGrpMembers());
                }
                if (modifyGroupTaskResult.getModifyGroupMemPropResp() != null && modifyGroupTaskResult.getModifyGroupMemPropResp().getModifiedGrpMembers() != null) {
                    userProfileDetails.setModifiedGrpMembers(modifyGroupTaskResult.getModifyGroupMemPropResp().getModifiedGrpMembers());
                }
                userProfileDetails.setAddedGroupMembersMap(getAddedGroupMembersMap);
                userProfileDetails.setGroupEtagToBeUpdated(taskResult.isGroupEtagToBeUpdated());
            }

            //modifyUpmContact
            knLogger.debug(methodName, " modify upm contact transaction status =", modifyUpmTxn.getTransactionStatus(), " taskResult- ", taskResult);
            if (taskResult != null && taskResult.getTaskStatus() == KnConstants.STATUS_SUCCESS) {
                taskResult = modifyUpmContact(profileMdn, modifyUpmTxn, dbSublistId);
                userProfileDetails.setContactEtagToBeUpdated(taskResult.isContactEtagToBeUpdated());
                knLogger.debug(methodName, " taskResult- ", taskResult, "isContactEtagToBeUpdated-", taskResult.isContactEtagToBeUpdated());
            }

            //modifyUpmPermission
            knLogger.debug(methodName, " modify upm permission transaction status =", modifyUpmTxn.getTransactionStatus(), " taskResult- ", taskResult);
            if (taskResult != null && taskResult.getTaskStatus() == KnConstants.STATUS_SUCCESS) {
                taskResult = modifyUpmPermission(profileMdn, modifyUpmTxn);
                userProfileDetails.setPermissionEtagToBeUpdated(taskResult.isPermissionEtagToBeUpdated());
                knLogger.debug(methodName, " taskResult- ", taskResult, "isPermissionEtagToBeUpdated-", taskResult.isPermissionEtagToBeUpdated());
            }

            //modifyEmergencyAttributesTask
            knLogger.debug(methodName, " modify upm emergency transaction status =", modifyUpmTxn.getTransactionStatus(), " taskResult- ", taskResult);
            if (taskResult != null && taskResult.getTaskStatus() == KnConstants.STATUS_SUCCESS) {
                taskResult = modifyUpmEmergencyAttributesTask(profileMdn, modifyUpmTxn);
                userProfileDetails.setEmergencyEtagToBeUpdated(taskResult.isEmergencyEtagToBeUpdated());
                knLogger.debug(methodName, " taskResult- ", taskResult, "isEmergencyEtagToBeUpdated-", taskResult.isEmergencyEtagToBeUpdated());
            }

            //modifyUpmCB
            knLogger.debug(methodName, " modify upm CBS doc transaction status =", modifyUpmTxn.getTransactionStatus(), " taskResult- ", taskResult);
            if (taskResult != null && taskResult.getTaskStatus() == KnConstants.STATUS_SUCCESS && cbsDocUpdateCount.get() == 0) {
                taskResult = modifyUpm(modifyUpmTxn);
                cbsDocUpdateCount.incrementAndGet();
            }

            //saving transaction
            if (taskResult != null && taskResult.getTaskStatus() == KnConstants.STATUS_SUCCESS) {
                //ETAG Update and forming dirChgDTO
                KnOPDirChgDTO xcapDirChgDTO = new KnOPDirChgDTO();
                List<KnCorpEXDMSNotifyDto> microserviceNotify = new ArrayList<>();
                KnXDMCommonMediator commonMediator = KnXDMCommonMediator.getInstance();
                KnCorpResponseDTO etagResp = corpMediator.updateEtag(asyncJobDTO.getCorpId(), profileMdn, userProfileDetails, modifyUpmTxn);
                knLogger.debug(methodName, "etagResp : ", etagResp);
                if (etagResp.getDirChgDTOs() != null && !etagResp.getDirChgDTOs().isEmpty()) {
                    List<KnOPDirChgDTO> dirChgDTOList = etagResp.getDirChgDTOs();
                    for (KnOPDirChgDTO dirChgDTOs : dirChgDTOList) {
                        xcapDirChgDTO.setXcapRootURI(dirChgDTOs.getXcapRootURI());
                        xcapDirChgDTO.setDirUri(dirChgDTOs.getDirUri());
                        xcapDirChgDTO.setPocHome(dirChgDTOs.getPocHome());
                        xcapDirChgDTO.setPresenceHome(dirChgDTOs.getPresenceHome());
                        xcapDirChgDTO.setDirNewEtag(dirChgDTOs.getDirNewEtag());
                        //Sending Directory Notify
                        commonMediator.sendXcapNotification(xcapDirChgDTO);
                    }
                }
                //Sending MCS Events
                if (null != modifyGroupTaskResult.getAddGroupResp()) {
                    KnCorpResponseDTO addGroupResp = modifyGroupTaskResult.getAddGroupResp();
                    addGroupResp.setChangeLogMap(etagResp.getChangeLogMap());
                    if (null != modifyGroupTaskResult.getAddUpmGroup() && !modifyGroupTaskResult.getAddUpmGroup().isEmpty()) {
                        for (KnCorpGroupListInfoDTO groupInfo : modifyGroupTaskResult.getAddUpmGroup()) {
                            if (addGroupResp.getMcxGrpInd() == 1) {
                                com.kodiak.common.commdto.common.KnCorpGroupContactDTO memberProps = new com.kodiak.common.commdto.common.KnCorpGroupContactDTO(groupInfo.getGrpMemProps().getIsSupervisor(),
                                        groupInfo.getGrpMemProps().getIsBroadcaster(), groupInfo.getGrpMemProps().getIsLocSupervisor(), groupInfo.getGrpMemProps().getIsOSMAuthorized(),
                                        groupInfo.getGrpMemProps().getCallInitiateAllowed(), groupInfo.getGrpMemProps().getCallTerminateAllowed(), groupInfo.getGrpMemProps().getIncallAllowed(),groupInfo.getGrpMemProps().getVideoCallInitiateAllowed(),
                                        groupInfo.getGrpMemProps().getVideoCallReceiveAllowed(),
                                        groupInfo.getGrpMemProps().getVideoInCallAllowed());
                                List<KnCorpEXDMSNotifyDto> grpNotifyDtoList = commonMediator.getModifyMcxGrpMicroSrvNotifyDto(addGroupResp, Integer.parseInt(String.valueOf(asyncJobDTO.getCorpId())), groupInfo.getGroupID(),
                                        userProfileDetails.getUserProfileId(), memberProps, addGroupResp.getOldLmrInteropFlag());
                                microserviceNotify.addAll(grpNotifyDtoList);
                            } else {
                                commonMediator.startNotifyMicroServicesJob(addGroupResp.getChangeLogMap(), addGroupResp.getMdnCorpId(), addGroupResp.getGroupCreatedBy(),
                                        addGroupResp.getLmrIntropCapable(), addGroupResp.getMcxGrpInd(), addGroupResp.getOldLmrInteropFlag());
                            }
                        }
                        boolean status = commonMediator.sendMCSGRPNotification(addGroupResp, taskResult.getAllMcsXcapUris(), Integer.parseInt(String.valueOf(asyncJobDTO.getCorpId())));
                        knLogger.info(methodName, "sending Group notification Status0: ", status);
                    }
                }
                if (null != modifyGroupTaskResult.getModifyGroupMemPropResp()) {
                    KnCorpResponseDTO modifyGroupMemPropResp = modifyGroupTaskResult.getModifyGroupMemPropResp();
                    modifyGroupMemPropResp.setChangeLogMap(etagResp.getChangeLogMap());
                    if (null != modifyGroupTaskResult.getModifyUpmGroup() && !modifyGroupTaskResult.getModifyUpmGroup().isEmpty()) {
                        for (KnCorpGroupListInfoDTO groupInfo : modifyGroupTaskResult.getModifyUpmGroup()) {
                            if (modifyGroupMemPropResp.getMcxGrpInd() == 1) {
                                com.kodiak.common.commdto.common.KnCorpGroupContactDTO memberProps = new com.kodiak.common.commdto.common.KnCorpGroupContactDTO(groupInfo.getGrpMemProps().getIsSupervisor(),
                                        groupInfo.getGrpMemProps().getIsBroadcaster(), groupInfo.getGrpMemProps().getIsLocSupervisor(), groupInfo.getGrpMemProps().getIsOSMAuthorized(),
                                        groupInfo.getGrpMemProps().getCallInitiateAllowed(), groupInfo.getGrpMemProps().getCallTerminateAllowed(), groupInfo.getGrpMemProps().getIncallAllowed(),groupInfo.getGrpMemProps().getVideoCallInitiateAllowed(),
                                        groupInfo.getGrpMemProps().getVideoCallReceiveAllowed(),
                                        groupInfo.getGrpMemProps().getVideoInCallAllowed());
                                List<KnCorpEXDMSNotifyDto> grpNotifyDtoList = commonMediator.getModifyMcxGrpMicroSrvNotifyDto(modifyGroupMemPropResp, Integer.parseInt(String.valueOf(asyncJobDTO.getCorpId())),
                                        groupInfo.getGroupID(), userProfileDetails.getUserProfileId(), memberProps, modifyGroupMemPropResp.getOldLmrInteropFlag());
                                microserviceNotify.addAll(grpNotifyDtoList);
                            } else {
                                commonMediator.startNotifyMicroServicesJob(modifyGroupMemPropResp.getChangeLogMap(), modifyGroupMemPropResp.getMdnCorpId(),
                                        modifyGroupMemPropResp.getGroupCreatedBy(), modifyGroupMemPropResp.getLmrIntropCapable(), modifyGroupMemPropResp.getMcxGrpInd(),
                                        modifyGroupMemPropResp.getOldLmrInteropFlag());
                            }
                        }
                        boolean status = commonMediator.sendMCSGRPNotification(modifyGroupMemPropResp, taskResult.getAllMcsXcapUris(), Integer.parseInt(String.valueOf(asyncJobDTO.getCorpId())));
                        knLogger.info(methodName, "sending Group notification Status: ", status);
                    }
                }
                if (null != modifyGroupTaskResult.getRemoveGroupResp()) {
                    KnCorpResponseDTO removeGroupResp = modifyGroupTaskResult.getRemoveGroupResp();
                    removeGroupResp.setChangeLogMap(etagResp.getChangeLogMap());
                    commonMediator.startNotifyMicroServicesJob(removeGroupResp.getChangeLogMap(), removeGroupResp.getMdnCorpId(),
                            removeGroupResp.getGroupCreatedBy(), removeGroupResp.getLmrIntropCapable(),
                            removeGroupResp.getMcxGrpInd(), removeGroupResp.getOldLmrInteropFlag());

                    boolean status = commonMediator.sendMCSGRPNotification(removeGroupResp, taskResult.getAllMcsXcapUris(), Integer.parseInt(String.valueOf(asyncJobDTO.getCorpId())));
                    knLogger.info(methodName, "sending Group notification Status1: ", status);
                }
                if (null != taskResult.getMicroserviceNotify() && !taskResult.getMicroserviceNotify().isEmpty()) {
                    commonMediator.startNotifyMicroServicesJob(microserviceNotify);
                }
                if (null != taskResult)
                    modifyUpmTxn.save();

                if (modifyGroupTaskResult != null) {
                    initiateDispatchGrpJob(modifyGroupTaskResult, asyncJobDTO.getCorpId());
                }

            } else {
                knLogger.error(methodName, " Failed modify upm for profile mdn :", profileMdn);
                userProfileDetails.setMdn(profileMdn);
                failedTxnCount.incrementAndGet();
                rollback(modifyUpmTxn);
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Failed Modify UPM task ", profileMdn);
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            failedTxnCount.incrementAndGet();
            rollback(modifyUpmTxn);
        }
        knLogger.exit(methodName, "End: profileMdn- ", profileMdn, " failedTxnCount- ", failedTxnCount, " cbsDocUpdateCount-", cbsDocUpdateCount);
        return modifyGroupTaskResult;
    }

    private KnTaskResult createSubsTask(KnOPSubsProfileInfoDTO subsProfileInfoDTO
            , KnCorpResponseDTO userProfileDetails,
                                        String payLoad,
                                        KnPersisterTxn assignSubsTxn) throws KnDAOException {
        final String methodName = "createSubsTask()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering create subscriber taskId ", taskId);

        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.ASSIGN_USER_PROFILE.Value(), KnConstants.UPM_TASK_TYPE.CREATESUBSCRIBER.Value());
        knLogger.debug(methodName, "userProfileDetails -----TGSC ", userProfileDetails.getUserProfile().getTgscMode());
        KnAssignSubscriberTask createSubscriberTask = new KnAssignSubscriberTask(asyncJobDTO.getUserProfileId(),
                asyncJobDTO.getResourceEntity(),
                String.valueOf(asyncJobDTO.getCorpId()),
                "0"
                , subsProfileInfoDTO, userProfileDetails, payLoad, assignSubsTxn);
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        createSubscriberTask.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        createSubscriberTask.addPostprocessor(postProcssor);
        KnTaskResult taskResult = createSubscriberTask.execute();
        knLogger.debug(methodName, "Completed create subscriber task_Id", taskId, " taskResult ", taskResult);
        return taskResult;
    }

    private KnTaskResult assignContactTask(String profileMdn
            , KnOPSubsProfileInfoDTO subsProfileInfoDTO
            , KnCorpResponseDTO userProfileDetails
            , KnPersisterTxn assignSubsTxn) throws KnDAOException {
        final String methodName = "assignContactTask()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering assign contact taskId ", taskId);

        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.ASSIGN_USER_PROFILE.Value(), KnConstants.UPM_TASK_TYPE.ASSIGNCONTACTLIST.Value());
        KnAssignContactTask assignTask = new KnAssignContactTask(profileMdn,
                String.valueOf(asyncJobDTO.getCorpId()),
                subsProfileInfoDTO, userProfileDetails, assignSubsTxn);
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        assignTask.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        assignTask.addPostprocessor(postProcssor);
        KnTaskResult taskResult = assignTask.execute();
        knLogger.debug(methodName, "Completed assign contact taskId ", taskId);
        return taskResult;
    }

    private KnTaskResult assignGroupTask(String profileMdn
            , KnCorpResponseDTO userProfileDetails
            , KnPersisterTxn assignSubsTxn) throws KnDAOException {
        final String methodName = "assignGroupTask()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering assign group taskId ", taskId);
        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.ASSIGN_USER_PROFILE.Value(), KnConstants.UPM_TASK_TYPE.GROUPADDITION.Value());
        /*KnAssignGroupTask assignGroupTask = new KnAssignGroupTask(profileMdn
                ,String.valueOf(asyncJobDTO.getCorpId())
                , asyncJobDTO.getUserProfileId()
                ,userProfileDetails,assignSubsTxn
                );*/
        KnBulkAssignGroupTask assignGroupTask = new KnBulkAssignGroupTask(profileMdn
                , String.valueOf(asyncJobDTO.getCorpId())
                , asyncJobDTO.getUserProfileId()
                , userProfileDetails, assignSubsTxn
        );
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        assignGroupTask.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        assignGroupTask.addPostprocessor(postProcssor);
        KnTaskResult taskResult = assignGroupTask.execute();
        knLogger.info(methodName, "Completed assign group task_Id", taskId);
        return taskResult;
    }

    private KnTaskResult setTargetPermTask(String profileMdn
            , KnCorpResponseDTO userProfileDetails
            , KnPersisterTxn assignSubsTxn) throws KnDAOException {
        final String methodName = "setTargetPermTask()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering set target perm taskId ", taskId);
        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.ASSIGN_USER_PROFILE.Value(), KnConstants.UPM_TASK_TYPE.TARGETPERMISSSION.Value());
        KnAssignPermissionTask setTargetPermTask = new KnAssignPermissionTask(profileMdn
                , String.valueOf(asyncJobDTO.getCorpId())
                , asyncJobDTO.getUserProfileId()
                , userProfileDetails, assignSubsTxn
        );
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        setTargetPermTask.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        setTargetPermTask.addPostprocessor(postProcssor);
        KnTaskResult taskResult = setTargetPermTask.execute();
        knLogger.debug(methodName, "Completed set target perm taskId", taskId);
        return taskResult;
    }

    private KnTaskResult setEmergencyAttributesTask(String profileMdn
            , KnCorpResponseDTO userProfileDetails
            , String hierarchyType
            , KnPersisterTxn assignSubsTxn) throws KnDAOException {
        final String methodName = "setEmergencyAttributesTask()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering set emergency attributes  taskId ", taskId);
        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.ASSIGN_USER_PROFILE.Value(), KnConstants.UPM_TASK_TYPE.TARGETPERMISSSION.Value());
        KnAssignEmergencyAttributesTask setTargetPermTask = new KnAssignEmergencyAttributesTask(profileMdn
                , String.valueOf(asyncJobDTO.getCorpId())
                , userProfileDetails
                , hierarchyType
                , assignSubsTxn
        );
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        setTargetPermTask.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        setTargetPermTask.addPostprocessor(postProcssor);
        KnTaskResult taskResult = setTargetPermTask.execute();
        knLogger.debug(methodName, "Completed set emergency attributes taskId", taskId);
        return taskResult;
    }

    private KnTaskResult setPermissionToAllAU(String profileMdn, KnPersisterTxn assignSubsTxn) throws KnDAOException {
        final String methodName = "setPermissionToAllAU()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering set permission to all AU  taskId ", taskId);
        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.ASSIGN_USER_PROFILE.Value(), KnConstants.UPM_TASK_TYPE.TARGETPERMISSSION_TO_AU.Value());
        KnAssignTargetPermsToAllAUTask setTargetPermTask = new KnAssignTargetPermsToAllAUTask(profileMdn
                , String.valueOf(asyncJobDTO.getCorpId())
                , asyncJobDTO.getResourceEntity()
                , assignSubsTxn
        );
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        setTargetPermTask.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        setTargetPermTask.addPostprocessor(postProcssor);
        KnTaskResult taskResult = setTargetPermTask.execute();
        knLogger.debug(methodName, "Completed set permission to all AU", taskId);
        return taskResult;
    }


    private void profileNotification(String userProfileId, String corpId) throws KnDAOException {
        final String methodName = "profileNotification()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering profile notification taskId ", taskId);
        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.ASSIGN_USER_PROFILE.Value(), KnConstants.UPM_TASK_TYPE.USER_PROFILE_NOTIFICATION.Value());
        KnProfileNotificationTask profileNotificationTask = new KnProfileNotificationTask(userProfileId, corpId);
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        profileNotificationTask.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        profileNotificationTask.addPostprocessor(postProcssor);
        profileNotificationTask.execute();
        knLogger.debug(methodName, "Profile notification sent for taskId", taskId);
    }

    private KnTaskResult modifyUpm(KnPersisterTxn modifyUpmTxn) throws KnDAOException {
        final String methodName = "modifyUpm()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering modify upm cb ", taskId);
        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.MODIFY_USER_PROFILE.Value(), KnConstants.UPM_TASK_TYPE.MODIFYUPM.Value());
        KnModifyUpmTask modifyUpm = new KnModifyUpmTask(asyncJobDTO.getPayLoad()
                , String.valueOf(asyncJobDTO.getCorpId())
                , modifyUpmTxn
        );
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        modifyUpm.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        modifyUpm.addPostprocessor(postProcssor);
        KnTaskResult taskResult = modifyUpm.execute();
        knLogger.debug(methodName, "Completed modify upm cb taskId", taskId);
        return taskResult;
    }

    private KnTaskResult modifyUpmGroup(String profileMdn, KnPersisterTxn modifyUpmTxn, KnCorpResponseDTO userProfileDetails) throws KnDAOException {
        final String methodName = "modifyUpmGroup()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering modify Upm Group", taskId);

        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.MODIFY_USER_PROFILE.Value(), KnConstants.UPM_TASK_TYPE.MODIFYUPMGROUP.Value());
        KnModifyGroupTask modifyUpm = new KnModifyGroupTask(
                String.valueOf(asyncJobDTO.getCorpId())
                , asyncJobDTO.getUserProfileId()
                , asyncJobDTO.getPayLoad()
                , modifyUpmTxn
                , profileMdn
                , userProfileDetails
        );
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        modifyUpm.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        modifyUpm.addPostprocessor(postProcssor);
        KnTaskResult taskResult = modifyUpm.execute();
        knLogger.debug(methodName, "Completed modify Upm Group", taskId);
        return taskResult;
    }

    private KnTaskResult modifyUpmContact(String profileMdn, KnPersisterTxn modifyUpmTxn, Integer dbSublistId) throws KnDAOException {
        final String methodName = "modifyUpmContact()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering modify Upm Contact", taskId);
        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.MODIFY_USER_PROFILE.Value(), KnConstants.UPM_TASK_TYPE.MODIFYUPMSUBLIST.Value());
        KnModifyContactTask modifyUpm = new KnModifyContactTask(
                String.valueOf(asyncJobDTO.getCorpId())
                , asyncJobDTO.getUserProfileId()
                , asyncJobDTO.getPayLoad()
                , modifyUpmTxn
                , profileMdn
                , dbSublistId
        );
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        modifyUpm.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        modifyUpm.addPostprocessor(postProcssor);
        KnTaskResult taskResult = modifyUpm.execute();
        knLogger.debug(methodName, "Completed modify Upm Contact", taskId);
        return taskResult;
    }

    private KnTaskResult modifyUpmPermission(String profileMdn, KnPersisterTxn modifyUpmTxn) throws KnDAOException {
        final String methodName = "modifyUpmPermission()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering modify Upm Permission", taskId);
        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.MODIFY_USER_PROFILE.Value(), KnConstants.UPM_TASK_TYPE.MODIFYUPMPERMISSION.Value());
        KnModifyPermissionTask modifyUpm = new KnModifyPermissionTask(
                String.valueOf(asyncJobDTO.getCorpId())
                , asyncJobDTO.getPayLoad()
                , modifyUpmTxn
                , profileMdn
        );
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        modifyUpm.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        modifyUpm.addPostprocessor(postProcssor);
        KnTaskResult taskResult = modifyUpm.execute();
        knLogger.debug(methodName, "Completed modify Upm Permission", taskId);
        return taskResult;
    }

    private KnTaskResult modifyUpmEmergencyAttributesTask(String profileMdn, KnPersisterTxn modifyUpmTxn) throws KnDAOException {
        final String methodName = "modifyUpmEmergencyAttributesTask()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering set emergency attributes  taskId ", taskId);
        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.ASSIGN_USER_PROFILE.Value(), KnConstants.UPM_TASK_TYPE.TARGETPERMISSSION.Value());
        KnModifyEmergencyAttributesTask setTargetPermTask = new KnModifyEmergencyAttributesTask(
                String.valueOf(asyncJobDTO.getCorpId())
                , asyncJobDTO.getPayLoad()
                , modifyUpmTxn
                , profileMdn
        );
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        setTargetPermTask.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        setTargetPermTask.addPostprocessor(postProcssor);
        KnTaskResult taskResult = setTargetPermTask.execute();
        knLogger.debug(methodName, "Completed set emergency attributes taskId", taskId);
        return taskResult;
    }

    private KnTaskResult modifyUpmFS(String profileMdn, KnPersisterTxn modifyUpmTxn) throws KnDAOException {
        final String methodName = "modifyUpmFS()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering modify Upm FS", taskId);
        knLogger.info(methodName,"ENTRY::");
        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.MODIFY_USER_PROFILE.Value(), KnConstants.UPM_TASK_TYPE.MODIFYUPMPERMISSION.Value());
        KnModifyUserProfileFsTask modifyUpm = new KnModifyUserProfileFsTask(
                asyncJobDTO.getPayLoad()
                , modifyUpmTxn
                , profileMdn
        );
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        modifyUpm.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId), KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        modifyUpm.addPostprocessor(postProcssor);
        KnTaskResult taskResult = modifyUpm.execute();
        knLogger.debug(methodName, "Completed modify Upm FS", taskId);
        knLogger.info(methodName,"EXIT::");
        return taskResult;
    }

    private void updateAsyncJobTask(long task_Id, int operationType, String taskType) throws KnDAOException {
        knLogger.debug("asyncJobDTO: ", asyncJobDTO);
        KnAsyncJobTaskDTO jobTaskDTO = new KnAsyncJobTaskDTO();
        jobTaskDTO.setOperationType(operationType);
        jobTaskDTO.setCorpId(asyncJobDTO.getCorpId());
        jobTaskDTO.setTxnId(asyncJobDTO.getTxnId());
        jobTaskDTO.setTaskId(String.valueOf(task_Id));
        jobTaskDTO.setResourceEntity(asyncJobDTO.getResourceEntity());
        jobTaskDTO.setStartTimeStamp(String.valueOf(Instant.now().toEpochMilli()));
        jobTaskDTO.setSeq(1);
        jobTaskDTO.setStatus(KnConstants.UPM_JOB_STATUS.NEW.Value());
        jobTaskDTO.setTaskType(taskType);
        generalCacheUtil.createAsyncJobTask(jobTaskDTO);
    }

    private KnTaskResult deleteUserProfileMdn(String deleteProfileMdn) throws KnDAOException {
        final String methodName = "deleteUserProfileMdn()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering delete User Profile Mdn", taskId);

        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.DELETE_USER_PROFILE_MDN.Value(),
                KnConstants.UPM_TASK_TYPE.DELETESUBSCRIBER.Value());
        KnDeleteUserProfileMdnTask deleteUserProfileMdn = new KnDeleteUserProfileMdnTask(deleteProfileMdn,
                String.valueOf(asyncJobDTO.getCorpId()), String.valueOf(taskId));
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId),
                KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        deleteUserProfileMdn.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId),
                KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        deleteUserProfileMdn.addPostprocessor(postProcssor);
        KnTaskResult taskResult = deleteUserProfileMdn.execute();
        knLogger.debug(methodName, "Completed delete User Profile Mdn for userProfileMdn--"
                , deleteProfileMdn, " taskId: ", taskId, " taskResult :", taskResult);
        return taskResult;
    }

    private void deleteGroupFromUserProfile() throws KnDAOException {
        final String methodName = "deleteGroupFromUserProfile()";
        String removedGroupId = asyncJobDTO.getResourceEntity();
        String sharedCorpIdStr = asyncJobDTO.getUserProfileId();
        knLogger.debug(methodName, "deleteGroupFromUserProfile: ", removedGroupId, " sharedCorpIds - ", sharedCorpIdStr);

        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering delete User Profile Mdn", taskId);
        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.DELETE_GROUP_FROM_PROFILE.Value(),
                KnConstants.UPM_TASK_TYPE.DELETEGROUP.Value());
        KnRemoveUserProfileGroupTask removeUserProfileGroupTask = new KnRemoveUserProfileGroupTask(removedGroupId,
                String.valueOf(asyncJobDTO.getCorpId()), String.valueOf(taskId), sharedCorpIdStr);
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId),
                KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        removeUserProfileGroupTask.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId),
                KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        removeUserProfileGroupTask.addPostprocessor(postProcssor);
        removeUserProfileGroupTask.execute();
        knLogger.debug(methodName, "Completed delete Group from  User Profile for removedGroupId--", removedGroupId, taskId);

    }

    private void deleteGroupSharedUserProfileNotification() throws KnDAOException {
        final String methodName = "deleteGroupSharedUserProfileNotification()";
        long taskId = System.nanoTime();
        knLogger.debug(methodName, "triggering profile notification for deleted shared group ,taskId:", taskId);
        updateAsyncJobTask(taskId, KnConstants.UPM_OPERATION_TYPE.DELETE_GROUP_FROM_PROFILE.Value(),
                KnConstants.UPM_TASK_TYPE.DELETEGROUP.Value());
        KnDeletedSharedGroupUserProfileNotificationTask deletedGSUPNotify = new KnDeletedSharedGroupUserProfileNotificationTask(asyncJobDTO.getPayLoad());
        KnTaskStatusUpdatePreProcessor preProcessor = new KnTaskStatusUpdatePreProcessor(String.valueOf(taskId),
                KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
        deletedGSUPNotify.addPreprocessor(preProcessor);
        KnTaskStatusUpdatePostProcssor postProcssor = new KnTaskStatusUpdatePostProcssor(String.valueOf(taskId),
                KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
        deletedGSUPNotify.addPostprocessor(postProcssor);
        deletedGSUPNotify.execute();
        knLogger.debug(methodName, "Done sending profile notification for deleted shared group taskId:", taskId);
    }

    private void writeAsyncJobAudit(KnAsyncJobDTO asyncJobDTO, String status) {
        String methodName = "writeAsyncJobAudit";
        StringBuilder sb = new StringBuilder();
        sb.append(status);
        sb.append(DELIMIT_PIPE);
        sb.append(asyncJobDTO.getCorpId());
        sb.append(DELIMIT_PIPE);
        sb.append(asyncJobDTO.getTxnId());
        sb.append(DELIMIT_PIPE);
        sb.append(asyncJobDTO.getOpType());
        sb.append(DELIMIT_PIPE);
        if (asyncJobDTO.getUserProfileId() != null) {
            sb.append(asyncJobDTO.getUserProfileId());
            sb.append(DELIMIT_PIPE);
        }

        if (asyncJobDTO.getResourceEntity() != null) {
            sb.append(asyncJobDTO.getResourceEntity());
            sb.append(DELIMIT_PIPE);
        }
        knLogger.info(methodName, sb.toString());
    }

    private void writeAsyncJobTaskAudit(KnAsyncJobTaskDTO asyncJobTaskDTO, String status) {
        String methodName = "writeAsyncJobTaskAudit";
        StringBuilder sb = new StringBuilder();
        sb.append(status);
        sb.append(DELIMIT_PIPE);
        sb.append(asyncJobTaskDTO.getCorpId());
        sb.append(DELIMIT_PIPE);
        sb.append(asyncJobTaskDTO.getTxnId());
        sb.append(DELIMIT_PIPE);
        sb.append(asyncJobTaskDTO.getOperationType());
        sb.append(DELIMIT_PIPE);
        sb.append(asyncJobTaskDTO.getTaskType());
        if (asyncJobTaskDTO.getPayLoad() != null) {
            sb.append(DELIMIT_PIPE);
            sb.append(asyncJobTaskDTO.getPayLoad());
        }
        sb.append(DELIMIT_PIPE);
        if (asyncJobTaskDTO.getUserProfileId() != null) {
            sb.append(asyncJobDTO.getUserProfileId());
            sb.append(DELIMIT_PIPE);
        }

        if (asyncJobTaskDTO.getResourceEntity() != null) {
            sb.append(asyncJobDTO.getResourceEntity());
            sb.append(DELIMIT_PIPE);
        }

        knLogger.info(methodName, sb.toString());

    }


    private void initiateDispatchGrpJob(KnTaskResult taskResult, int corpId) {
        String methodName = "initiateDispatchGrpJob(KnTaskResult,corpId)";
        knLogger.info(methodName, " taskResult :", taskResult, " corpId :", corpId);
        int msgTruncationLimit = 800;
        Collection<String> addedMdnList = null;
        Collection<String> deletedMdnList = null;
        Collection<String> addedLocWatcher = null;
        Collection<String> removedLocWatcher = null;
        // If added members list id not null and not empty start job to update
        // DISPATCH_GROUP_MEMBER field
        KnDispGrpMemChecker dispGrpMemCheckerJob = new KnDispGrpMemChecker();
        if (taskResult.getEnabledDispatchMemList() != null && !taskResult.getEnabledDispatchMemList().isEmpty())
            addedMdnList = new ArrayList<>(new HashSet<>(taskResult.getEnabledDispatchMemList()));
        if (taskResult.getDisabledDispatchMemList() != null && !taskResult.getDisabledDispatchMemList().isEmpty())
            deletedMdnList = new ArrayList<>(new HashSet<>(taskResult.getDisabledDispatchMemList()));
        if (taskResult.getAddedLocWatcherList() != null && !taskResult.getAddedLocWatcherList().isEmpty())
            addedLocWatcher = new ArrayList<>(new HashSet<>(taskResult.getAddedLocWatcherList()));
        if (taskResult.getRemovedLocWatcherList() != null && !taskResult.getRemovedLocWatcherList().isEmpty())
            removedLocWatcher = new ArrayList<>(new HashSet<>(taskResult.getRemovedLocWatcherList()));

        if (addedMdnList != null && !addedMdnList.isEmpty()) {
            knLogger.debug(methodName, "Calling Dispatch group member job Added- ", addedMdnList);
            dispGrpMemCheckerJob.setAddedMembers(addedMdnList);
        }
        if (deletedMdnList != null && !deletedMdnList.isEmpty()) {
            knLogger.debug(methodName, "Calling Dispatch group member job removed- ", deletedMdnList);
            dispGrpMemCheckerJob.setDeletedMembers(deletedMdnList);
        }
        if (addedLocWatcher != null && !addedLocWatcher.isEmpty()) {
            knLogger.debug(methodName, "Calling Dispatch group member job addedLoc- ", addedLocWatcher);
            dispGrpMemCheckerJob.setAddedLocWatcher(addedLocWatcher);
        }
        if (removedLocWatcher != null && !removedLocWatcher.isEmpty()) {
            knLogger.debug(methodName, "Calling Dispatch group member job removedLoc- ", removedLocWatcher);
            dispGrpMemCheckerJob.setRemovedLocWatcher(removedLocWatcher);
        }
        if ((dispGrpMemCheckerJob.getAddedMembers() != null && !dispGrpMemCheckerJob.getAddedMembers().isEmpty()) || (dispGrpMemCheckerJob.getAddedLocWatcher() != null && !dispGrpMemCheckerJob.getAddedLocWatcher().isEmpty())
                || (dispGrpMemCheckerJob.getDeletedMembers() != null && !dispGrpMemCheckerJob.getDeletedMembers().isEmpty())
                || (dispGrpMemCheckerJob.getRemovedLocWatcher() != null && !dispGrpMemCheckerJob.getRemovedLocWatcher().isEmpty())) {
            List<KnDispGrpMemChecker> jobList = new ArrayList<>(1);
            dispGrpMemCheckerJob.setMaxNotificationSize(msgTruncationLimit);
            // dispGrpMemCheckerJob.setSyncNotfyThresold(syncNotifyThresold);
            dispGrpMemCheckerJob.setCorpId(corpId);
            dispGrpMemCheckerJob.setUpmCall(Boolean.TRUE);
            jobList.add(dispGrpMemCheckerJob);
            try {
                knLogger.debug(methodName, "Scheduling the Jobs - ", jobList);
                scheduler.addRamJob(jobList, KnJobConstants.JOB_GROUP_NAME);
            } catch (KnJobSchedulerException e) {
                knLogger.error(methodName, "KnJobSchedulerException occurred while ",
                        "submitting Notification Job to Scheduler - " + e);
            }
        }
    }

    private void rollback(KnPersisterTxn txn) {
        try {
            knLogger.error("rollback()", "Rolling back transaction");
            if (txn != null && txn.getTransactionStatus() != KnPersisterTxn.STATE.ROLLBACK.ordinal()) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)",
                    "Failed to rollback the transaction.");
        }
    }
}

