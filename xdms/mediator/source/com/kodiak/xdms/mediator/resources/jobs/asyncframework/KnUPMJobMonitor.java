/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.asyncframework;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.server.common.resources.KnConstants;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.kodiak.xdms.mediator.resources.jobs.asyncframework.KnAsyncFwConstants.JOB_THREAD_POOL_SIZE;

public class KnUPMJobMonitor implements Runnable, IStatusMgrNotifyIntf {

    private static final KnLogger knLogger = KnLogger.getLogger(KnUPMJobMonitor.class);
    public static KnUPMJobMonitor instance;
    private static KnUPMJobScheduler upmJobScheduler;
    private static KnGeneralCacheUtil generalCacheUtil;
    private KnStatusMgrConstants.CARD_STATES currentRedState = KnStatusMgrConstants.CARD_STATES.UNKNOWN;
    LinkedHashMap<String, KnAsyncJobDTO> newJobs = new LinkedHashMap<>();

    @Override
    public void run() {
        knLogger.info("upmJobMointor()", "xdm current state - ", getCurrentRedundancyStatus());
        if (getCurrentRedundancyStatus() == KnStatusMgrConstants.CARD_STATES.ACTIVE) {
            triggerJob();
        }
    }


    private boolean triggerJob() {
        String methodName = "triggerJob";

        boolean result = false;
        try {
            if(newJobs.isEmpty() || newJobs.size()< JOB_THREAD_POOL_SIZE) {
                knLogger.info(methodName, "no jobs in map for processing hence pulling from GG","size", newJobs.size());
                int jobsToBePulled = JOB_THREAD_POOL_SIZE - newJobs.size();
                knLogger.info(methodName, "no jobs to be pulled from GG - ",jobsToBePulled);
                //MINT-23641
                List<Integer> jobStatus = new ArrayList<>();
                jobStatus.add(KnConstants.UPM_JOB_STATUS.NEW.Value());
                jobStatus.add(KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
                LinkedHashMap<Integer, Map<Integer, String>> jobNotifyDTOS = generalCacheUtil.getStatusCorpIdMap(jobStatus);
                if (null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.NEW.Value()) &&
                        null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.INPROGRESS.Value())) {
                    knLogger.info(methodName, "Holding jobs for corps ", jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.INPROGRESS.Value()).keySet(), " as there is already one asynchronous operation is running for the given " +
                            "corpIds hence waiting for it to complete");
                    jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.NEW.Value()).keySet().removeAll(jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.INPROGRESS.Value()).keySet());
                }
                Map<Integer, String> newJobsMap = new HashMap<>();
                if (null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.NEW.Value())) {
                    newJobsMap = jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.NEW.Value());
                }
                LinkedHashMap<String, KnAsyncJobDTO> dbJobs = generalCacheUtil.getNewJobs(jobsToBePulled, newJobsMap);
                if (dbJobs != null && !dbJobs.isEmpty()) {
                    knLogger.debug(methodName, "dbjobs txnIds", dbJobs.keySet());
                    newJobs.putAll(dbJobs);
                }
                knLogger.debug(methodName, "newJobs", newJobs);
                knLogger.info(methodName, "Total threads -",JOB_THREAD_POOL_SIZE,"busy threads - ", newJobs.size(),"ideal threads - ",jobsToBePulled);
            }else {
                knLogger.info(methodName, "jobs already available in new jobs", newJobs.keySet());
            }
            if (newJobs != null && !newJobs.isEmpty()) {
                Iterator<Map.Entry<String, KnAsyncJobDTO>> itr = newJobs.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<String, KnAsyncJobDTO> entry = itr.next();
                    String txnId = entry.getKey();
                    KnAsyncJobDTO upmJobNotifyDTO = entry.getValue();
                    KnUPMJob upmJob = new KnUPMJob(txnId, upmJobNotifyDTO);
                    ThreadPoolExecutor executor = (ThreadPoolExecutor) KnAsyncService.getJobExecutor();
                    int queueSize = executor.getQueue().size();
                    knLogger.info(methodName, "Triggering job", "txnId-", txnId, "upmJobNotifyDTO-", upmJobNotifyDTO
                            , " queueSize- " + queueSize);
                    /**
                     * In-case of latency in UPM job process,Jobs were queued up in the blocking queue. The Job status was updated by UPM thread, so which ever
                     * jobs were not picked from queue, was again called by this monitor and was being posted in blocking queue.
                     * Changes 1 -> Moving update status before giving to executor, so that same jobs should be picked up by job monitor.
                     * Changes 2 -> queue size check, so that xdm crash time, more jobs should not marked as in-progress, otherwise those records will be never picked up.
                     */

                    if (queueSize < 5 && isValidUpmJob(upmJobNotifyDTO.getOpType())) {
                        generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());
                        executor.submit(upmJob);
                    }
                    Thread.sleep(1000);
                    itr.remove();
                    knLogger.info(methodName, "removed job:", txnId, "size", newJobs.size());
                }
                result = true;
            }
        } catch (Throwable e) {
            knLogger.error(methodName, "exception while fetching jobs", e);
        }

        return result;
    }

    public static KnUPMJobMonitor getInstance() throws KnDAOException {
        if (instance == null) {
            instance = new KnUPMJobMonitor();
            //registering for Redundancy Notify Status
            List<IStatusMgrNotifyIntf> list = new ArrayList<>();
            list.add(instance);
            KnStatusManagerClient.registerObjects(list);
            knLogger.debug("getInstance()", "registered for Status..");

            upmJobScheduler = KnUPMJobScheduler.getInstance();
            generalCacheUtil = KnGeneralCacheUtil.getInstance();

            Map<String, String> transactionIds = generalCacheUtil.getInProgressJobs(KnConstants.UPM_JOB_STATUS.INPROGRESS.Value());//Just in progress jobs
            for (Map.Entry<String, String> entry : transactionIds.entrySet()) {
                String txnId = entry.getKey();
                int opType = Integer.parseInt(entry.getValue());
                Integer recoveredStatus = getRecoveredStatus(opType);
                generalCacheUtil.updateAsyncJobStatus(txnId, recoveredStatus);
            }

            Map<String, String> notifyTransactionIds = generalCacheUtil.getInProgressJobs(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value());
            for (String txnId : notifyTransactionIds.keySet()) {
                if (notifyTransactionIds.get(txnId).equals(KnConstants.UPM_OPERATION_TYPE.WATCHER_NOTIFICATION_ASSIGN.Value() + "")) {
                    generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_ASSIGN.Value());
                } else if (notifyTransactionIds.get(txnId).equals(KnConstants.UPM_OPERATION_TYPE.WATCHER_NOTIFICATION_MODIFY_UPM_ADD_GROUP.Value() + "")) {
                    generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_ADD_GROUP.Value());
                } else if (notifyTransactionIds.get(txnId).equals(KnConstants.UPM_OPERATION_TYPE.WATCHER_NOTIFICATION_MODIFY_UPM_MODIFY_GROUP.Value() + "")) {
                    generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_MODIFY_GROUP.Value());
                } else if (notifyTransactionIds.get(txnId).equals(KnConstants.UPM_OPERATION_TYPE.WATCHER_NOTIFICATION_MODIFY_UPM_REMOVE_GROUP.Value() + "")) {
                    generalCacheUtil.updateAsyncJobStatus(txnId, KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_REMOVE_GROUP.Value());
                }
            }
        }

        return instance;
    }

    @Override
    public void notify(KnStatusMgrConstants.CARD_STATES previousState, KnStatusMgrConstants.CARD_STATES currentState) {
        String methodName = "notify(KnStatusMgrConstants.CARD_STATES, KnStatusMgrConstants.CARD_STATES)";
        knLogger.info(methodName, "notify from Status Mgr [Prev State - ", previousState,
                "; Current State - ", currentState + "]");

        this.currentRedState = currentState;

        knLogger.debug(methodName, "Current State ", currentState);
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

    private boolean isValidUpmJob(int opType) {
        return (opType == KnConstants.UPM_OPERATION_TYPE.ASSIGN_USER_PROFILE.Value()
                || opType == KnConstants.UPM_OPERATION_TYPE.MODIFY_USER_PROFILE.Value()
                || opType == KnConstants.UPM_OPERATION_TYPE.DELETE_USER_PROFILE_MDN.Value()
                || opType == KnConstants.UPM_OPERATION_TYPE.DELETE_GROUP_FROM_PROFILE.Value()
                || opType == KnConstants.UPM_OPERATION_TYPE.DELETE_GROUP_SHARED_USER_PROFILE_NOTIFICATION.Value()
        );
    }

    private static Integer getRecoveredStatus(int opType) {
        if (opType == KnConstants.UPM_OPERATION_TYPE.STALE_UPM_ELEMENTS.Value()) {
            return KnConstants.UPM_JOB_STATUS.STALE.Value();
        }
        if (opType == KnConstants.UPM_OPERATION_TYPE.VERY_LARGE_GROUP_BIT_DISABLED.Value()) {
            return KnConstants.UPM_JOB_STATUS.VERY_LARGE_GRP_CLEAN_NEW.Value();
        }
        if (opType == KnConstants.UPM_OPERATION_TYPE.RETRY_MODIFY_USER_PROFILE.Value()) {
            return KnConstants.UPM_JOB_STATUS.RETRY.Value();
        }
        return KnConstants.UPM_JOB_STATUS.NEW.Value();
    }

}
