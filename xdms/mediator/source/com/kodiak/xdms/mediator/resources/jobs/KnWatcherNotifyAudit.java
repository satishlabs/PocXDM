/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.mediator.resources.jobs.asyncframework.KnAsyncService;
import com.kodiak.xdms.mediator.resources.jobs.upm.KnWatcherNotify;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.resources.KnConstants;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;


import static com.kodiak.xdms.mediator.KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR;
import static com.kodiak.xdms.mediator.resources.jobs.asyncframework.KnAsyncFwConstants.WATCHER_JOB_THREAD_POOL_SIZE;

public class KnWatcherNotifyAudit implements Runnable, IStatusMgrNotifyIntf {

    private static final KnLogger knLogger = KnLogger.getLogger(KnWatcherNotifyAudit.class);
    private KnStatusMgrConstants.CARD_STATES currentRedState = KnStatusMgrConstants.CARD_STATES.UNKNOWN;
    private static KnWatcherNotifyAudit instance;
    private static int state = 10;
    private static final int ASSIGN_UPM = 10;
    private static final int ADD_GROUP = 13;
    private static final int MODIFY_GROUP = 14;
    private static final int REMOVE_GROUP = 15;
    private static final int WATCHER_NOTIFIACTION_INPROGRESS = KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value();
    private static KnGeneralCacheUtil generalCacheUtil;
    private static ExecutorService executor = null;

    @Override
    public void run() {
        String methodName = "WatcherRun";
        knLogger.info(methodName, "xdm current state - ", getCurrentRedundancyStatus());
        if (getCurrentRedundancyStatus() == KnStatusMgrConstants.CARD_STATES.ACTIVE) {
            try {
                triggerJob();
                knLogger.info(methodName, "Done execution");
            } catch (Throwable t) {
                knLogger.error(methodName, "Exception occurred while triggering job: ", t.getMessage(), t);
            }
        }
    }

    private void triggerJob() throws KnXDMServerException {
        String methodName = "triggerJob";
        boolean result = false;
        try {
            LinkedHashMap<String, KnAsyncJobDTO> newJobs = new LinkedHashMap<>();
            if (newJobs.isEmpty() || newJobs.size() < WATCHER_JOB_THREAD_POOL_SIZE) {
                knLogger.info(methodName, "no jobs in map for processing hence pulling from GG", "size", newJobs.size());
                int jobsToBePulled = WATCHER_JOB_THREAD_POOL_SIZE - newJobs.size();
                knLogger.info(methodName, "no jobs to be pulled from GG - ", jobsToBePulled);

                //MINT-23641
                List<Integer> jobStatus = new ArrayList<>();
                jobStatus.add(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_ASSIGN.Value());
                jobStatus.add(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_ADD_GROUP.Value());
                jobStatus.add(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_MODIFY_GROUP.Value());
                jobStatus.add(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_REMOVE_GROUP.Value());
                jobStatus.add(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value());
                LinkedHashMap<Integer, Map<Integer, String>> jobNotifyDTOS = generalCacheUtil.getStatusCorpIdMap(jobStatus);
                if (null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_ASSIGN.Value()) &&
                        null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value())) {
                    knLogger.info(methodName, "Holding jobs for corps ", jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value()).keySet(),
                            " as there is already one asynchronous operation is running for the given corpIds hence waiting for it to complete");
                    jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_ASSIGN.Value()).keySet().removeAll
                            (jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value()).keySet());
                }
                if (null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_ADD_GROUP.Value()) &&
                        null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value())) {
                    knLogger.info(methodName, "Holding jobs for corps ", jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value()).keySet(),
                            " as there is already one asynchronous operation is running for the given corpIds hence waiting for it to complete");
                    jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_ADD_GROUP.Value()).keySet().removeAll
                            (jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value()).keySet());
                }
                if (null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_MODIFY_GROUP.Value()) &&
                        null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value())) {
                    knLogger.info(methodName, "Holding jobs for corps ", jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value()).keySet(),
                            " as there is already one asynchronous operation is running for the given corpIds hence waiting for it to complete");
                    jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_MODIFY_GROUP.Value()).keySet().removeAll
                            (jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value()).keySet());
                }
                if (null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_REMOVE_GROUP.Value()) &&
                        null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value())) {
                    knLogger.info(methodName, "Holding jobs for corps ", jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value()).keySet(),
                            " as there is already one asynchronous operation is running for the given corpIds hence waiting for it to complete");
                    jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_REMOVE_GROUP.Value()).keySet().removeAll
                            (jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_INPROGRESS.Value()).keySet());
                }

                Map<Integer, String> newJobsMap = new HashMap<>();
                if (null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_ASSIGN.Value())) {
                    newJobsMap.putAll(jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_ASSIGN.Value()));
                }
                if (null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_ADD_GROUP.Value()) ) {
                    newJobsMap.putAll(jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_ADD_GROUP.Value()));
                }
                if (null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_MODIFY_GROUP.Value())) {
                    newJobsMap.putAll(jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_MODIFY_GROUP.Value()));
                }
                if (null != jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_REMOVE_GROUP.Value())) {
                    newJobsMap.putAll(jobNotifyDTOS.get(KnConstants.UPM_JOB_STATUS.WATCHER_NOTIFICATION_MODIFY_UPM_REMOVE_GROUP.Value()));
                }
                LinkedHashMap<String, KnAsyncJobDTO> dbJobs = generalCacheUtil.getWatchersDetails(jobsToBePulled, newJobsMap);
                knLogger.debug(methodName, "dbjobs.size", dbJobs.size());
                if (dbJobs != null && !dbJobs.isEmpty()) {
                    knLogger.debug(methodName, "dbjobs txnIds", dbJobs.keySet());
                    newJobs.putAll(dbJobs);
                }
                knLogger.debug(methodName, "newJobs", newJobs);
                knLogger.info(methodName, "Total threads -", WATCHER_JOB_THREAD_POOL_SIZE, "busy threads - ", newJobs.size(), "ideal threads - ", jobsToBePulled);
            }
            if (newJobs != null && !newJobs.isEmpty()) {
                Iterator<Map.Entry<String, KnAsyncJobDTO>> itr = newJobs.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<String, KnAsyncJobDTO> entry = itr.next();
                    String txnId = entry.getKey();
                    KnAsyncJobDTO watcherJobNotifyDTO = entry.getValue();
                    KnWatcherNotify watcherJob = new KnWatcherNotify(txnId, watcherJobNotifyDTO, newJobs);
                    ThreadPoolExecutor executor = (ThreadPoolExecutor) KnAsyncService.getJobwatcherExecutor();
                    int queueSize = executor.getQueue().size();
                    knLogger.info(methodName, "Triggering job", "txnId-", txnId, "upmJobNotifyDTO-", watcherJobNotifyDTO, " queueSize- " + queueSize);

                    Set<String> txnIdsSet = new HashSet<>();
                    txnIdsSet.add(txnId);
                    if (queueSize < 5 && isValidUpmJob(watcherJobNotifyDTO.getOpStatus())) {
                        generalCacheUtil.updateWatcherState(txnIdsSet, WATCHER_NOTIFIACTION_INPROGRESS);
                        executor.submit(watcherJob);
                    } else {
                        knLogger.debug(methodName, "Condition not met for updating watcher state or submitting job");
                    }

                    Thread.sleep(200);
                    itr.remove();
                    knLogger.info(methodName, "Removed job:", txnId, "size", newJobs.size());
                }
                result = true;
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Failed due to DAO exception: ", e.getMessage(), e);
        } catch (Exception e) {
            knLogger.error(methodName, "UnexpectedException occurred while ", "SendNotification - ", new KnException(
                    ERROR_CODE_INTERNAL_ERROR, e.getMessage(), e));
            throw new KnXDMServerException(ERROR_CODE_INTERNAL_ERROR, "Unexpected exception occurred in SendNotification", e);
        }
    }

    public static KnWatcherNotifyAudit getInstance() {
        if (instance == null) {
            instance = new KnWatcherNotifyAudit();
            // registering for Redundancy Notify Status
            List<IStatusMgrNotifyIntf> list = new ArrayList<>();
            list.add(instance);
            KnStatusManagerClient.registerObjects(list);
            generalCacheUtil = KnGeneralCacheUtil.getInstance();
            knLogger.debug("getInstance()", "registered for Status..");
        }
        return instance;
    }

    /**
     * method which gives redundancy state as TRUE if ACTIVE else false
     *
     * @return boolean
     */
    public KnStatusMgrConstants.CARD_STATES getCurrentRedundancyStatus() {
        return this.currentRedState;
    }

    @Override
    public void notify(KnStatusMgrConstants.CARD_STATES previousState, KnStatusMgrConstants.CARD_STATES currentState) {
        String methodName = "notify(KnStatusMgrConstants.CARD_STATES, KnStatusMgrConstants.CARD_STATES)";
        knLogger.info(methodName, "notify from Status Mgr [Prev State - ", previousState,
                "; Current State - ", currentState + "]");
        this.currentRedState = currentState;
        knLogger.debug(methodName, "Current State ", currentState);
    }

    private boolean isValidUpmJob(int opType) {
        return (opType == ASSIGN_UPM
                || opType == ADD_GROUP
                || opType == MODIFY_GROUP
                || opType == REMOVE_GROUP
        );
    }
}