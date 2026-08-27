/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/

package com.kodiak.xdms.mediator.resources.jobs.asyncframework;

import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.server.common.resources.KnConstants;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import static com.kodiak.xdms.mediator.resources.jobs.asyncframework.KnAsyncFwConstants.JOB_THREAD_POOL_SIZE;

public class KnRetryUpmJobsThread implements Runnable, IStatusMgrNotifyIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnRetryUpmJobsThread.class);
    private KnStatusMgrConstants.CARD_STATES currentRedState = KnStatusMgrConstants.CARD_STATES.UNKNOWN;
    private static KnGeneralCacheUtil generalCacheUtil;
    LinkedHashMap<String, KnAsyncJobDTO> newJobs = new LinkedHashMap<>();
    public static KnRetryUpmJobsThread instance;
    private static KnUPMJobScheduler upmJobScheduler;

    public boolean triggerJob() {
        String methodName = "triggerJob()";
        boolean result = false;
        try {
            if (newJobs.isEmpty() || newJobs.size() < JOB_THREAD_POOL_SIZE) {
                int jobsToBePulled = JOB_THREAD_POOL_SIZE - newJobs.size();
                LinkedHashMap<String, KnAsyncJobDTO> dbJobs = generalCacheUtil.getRetryJobs(jobsToBePulled);
                knLogger.debug(methodName, "dbjobs.size", dbJobs.size());
                if (dbJobs != null && !dbJobs.isEmpty()) {
                    knLogger.debug(methodName, "dbjobs txnIds", dbJobs.keySet());
                    newJobs.putAll(dbJobs);
                }
                knLogger.debug(methodName, "newJobs", newJobs);
                knLogger.info(methodName, "Total threads -", JOB_THREAD_POOL_SIZE, "busy threads - ", newJobs.size(), "ideal threads - ", jobsToBePulled);
            } else {
                knLogger.info(methodName, "jobs already available in new retry jobs", newJobs.keySet());
            }

            if (newJobs != null && !newJobs.isEmpty()) {
                Iterator<Map.Entry<String, KnAsyncJobDTO>> itr = newJobs.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<String, KnAsyncJobDTO> entry = itr.next();
                    String txnId = entry.getKey();
                    KnAsyncJobDTO upmJobNotifyDTO = entry.getValue();
                    List<Integer> corpJobStatus = generalCacheUtil.getJobStatusByCorpId(upmJobNotifyDTO.getCorpId());
                    if (corpJobStatus.isEmpty()) {
                        KnUPMJob upmJob = new KnUPMJob(txnId, upmJobNotifyDTO);
                        ThreadPoolExecutor executor = (ThreadPoolExecutor) KnAsyncService.getJobExecutor();
                        int queueSize = executor.getQueue().size();
                        knLogger.info(methodName, "Triggering job", "txnId-", txnId, "upmJobNotifyDTO-", upmJobNotifyDTO
                                , " queueSize- " + queueSize);
                        if (queueSize < 5 && isValidUpmJob(upmJobNotifyDTO.getOpType())) {
                            knLogger.info(methodName, "Valid UPM job");
                            generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
                            executor.submit(upmJob);
                        }
                        Thread.sleep(1000);
                        itr.remove();
                        knLogger.info(methodName, "removed job:", txnId, "size", newJobs.size());
                    } else {
                        knLogger.info(methodName, "Holding txnId -" + txnId + "as there is already one asynchronous operation is running for the given " +
                                "corp hence waiting for it to complete", upmJobNotifyDTO.getCorpId());
                    }
                }
                result = true;
            }
        } catch (Throwable e) {
            knLogger.error(methodName, "exception while fetching jobs", e);
        }
        return result;
    }

    public static KnRetryUpmJobsThread getInstance() {
        if (instance == null) {
            instance = new KnRetryUpmJobsThread();
            //registering for Redundancy Notify Status
            List<IStatusMgrNotifyIntf> list = new ArrayList<>();
            list.add(instance);
            KnStatusManagerClient.registerObjects(list);
            knLogger.debug("getInstance()", "registered for Status..");

            upmJobScheduler = KnUPMJobScheduler.getInstance();
            generalCacheUtil = KnGeneralCacheUtil.getInstance();
        }

        return instance;
    }

    @Override
    public void run() {
        knLogger.info("upmJobMointor()", "xdm current state - ", getCurrentRedundancyStatus());
//        if (getCurrentRedundancyStatus() == KnStatusMgrConstants.CARD_STATES.ACTIVE) {
        triggerJob();
//        }
    }

    /**
     * method which gives redundancy state as TRUE if ACTIVE else false
     *
     * @return boolean
     */
    public KnStatusMgrConstants.CARD_STATES getCurrentRedundancyStatus() {
        String methodName = "getCurrentRedundancyStatus";
        knLogger.debug(methodName, "Current Card Redundancy Status ", this.currentRedState);
        return this.currentRedState;
    }

    @Override
    public void notify(KnStatusMgrConstants.CARD_STATES previousState, KnStatusMgrConstants.CARD_STATES
            currentState) {
        String methodName = "notify(KnStatusMgrConstants.CARD_STATES, KnStatusMgrConstants.CARD_STATES)";
        knLogger.info(methodName, "notify from Status Mgr [Prev State - ", previousState,
                "; Current State - ", currentState + "]");

        this.currentRedState = currentState;

        knLogger.debug(methodName, "Current State ", currentState);
    }

    private boolean isValidUpmJob(int opType) {
        return (opType == KnConstants.UPM_OPERATION_TYPE.ASSIGN_USER_PROFILE.Value()
                || opType == KnConstants.UPM_OPERATION_TYPE.MODIFY_USER_PROFILE.Value()
                || opType == KnConstants.UPM_OPERATION_TYPE.DELETE_USER_PROFILE_MDN.Value()
                || opType == KnConstants.UPM_OPERATION_TYPE.DELETE_GROUP_FROM_PROFILE.Value()
                || opType == KnConstants.UPM_OPERATION_TYPE.DELETE_GROUP_SHARED_USER_PROFILE_NOTIFICATION.Value()
                || opType == KnConstants.UPM_OPERATION_TYPE.RETRY_MODIFY_USER_PROFILE.Value()
//                || opType == KnConstants.UPM_OPERATION_TYPE.RETRY_DELETE_USER_PROFILE_MDN.Value()
        );
    }

}
