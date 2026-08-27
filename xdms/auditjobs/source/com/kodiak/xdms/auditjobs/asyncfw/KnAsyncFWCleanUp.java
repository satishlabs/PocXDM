/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.auditjobs.asyncfw;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.resources.KnConstants;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class KnAsyncFWCleanUp extends KnAbstractJob {

    private static final long serialVersionUID = 1L;
    private static final KnLogger knLogger = KnLogger.getLogger(KnAsyncFWCleanUp.class);
    private KnGeneralCacheUtil cacheUtil = KnGeneralCacheUtil.getInstance();
    private KnGeneralUtil generalUtil = new KnGeneralUtil();
    //2hrs
    private static final long DEFAULT_TIME_TO_KEEP_DATA = (2*60*60*1000);

    public boolean executeTask() throws KnJobSchedulerException {
        String methodName = "executeTask()";
        long currentTime = Instant.now().toEpochMilli();
        //Default value
        long jobKeepTimeInMs = currentTime - DEFAULT_TIME_TO_KEEP_DATA;
        knLogger.info(methodName, "ENTRY: Start of Async FW Audit job exp Time " + currentTime," jobKeepTimeInMs - ",jobKeepTimeInMs);
        try {
            List<Integer> jobStatus = new ArrayList<>();
            jobStatus.add(KnConstants.UPM_JOB_STATUS.CRASHED.Value());
            jobStatus.add(KnConstants.UPM_JOB_STATUS.COMPLETE.Value());
            jobStatus.add(KnConstants.UPM_JOB_STATUS.FAILURE.Value());
            knLogger.info(methodName, "get All completed,Failed & crashed jobs " + currentTime);

            Map<String, String> asyncOpRetTimeMap = generalUtil.retrieveMSCommonConfig(new ArrayList<String>(Collections.singleton(KnConstants.ASYNC_OP_RETENTION_TIME_IN_MIN)));
            knLogger.debug(methodName,"asyncOpRetTimeMap - ",asyncOpRetTimeMap);
            if(asyncOpRetTimeMap!=null && !asyncOpRetTimeMap.isEmpty()){
                knLogger.info(methodName,"configured retenetion time found hence overriding default value ");
                jobKeepTimeInMs = currentTime - Long.parseLong(asyncOpRetTimeMap.get(KnConstants.ASYNC_OP_RETENTION_TIME_IN_MIN));
            }

            List<KnAsyncJobDTO> jobs = cacheUtil.getJobsByStatusAndUpdateTime(jobStatus,jobKeepTimeInMs);

            Set<String> crashedJobTxnIds = jobs.stream().filter(asyncJobDTO -> asyncJobDTO.getOpStatus() == KnConstants.UPM_JOB_STATUS.CRASHED.Value()).
                    map(KnAsyncJobDTO::getTxnId).collect(Collectors.toSet());
            Set<String> completedJobsTxnIds = jobs.stream().filter(asyncJobDTO -> asyncJobDTO.getOpStatus() == KnConstants.UPM_JOB_STATUS.COMPLETE.Value()).
                    map(KnAsyncJobDTO::getTxnId).collect(Collectors.toSet());

            Set<String> failedJobsTxnIds = jobs.stream().filter(asyncJobDTO -> asyncJobDTO.getOpStatus() == KnConstants.UPM_JOB_STATUS.FAILURE.Value()).
                    map(KnAsyncJobDTO::getTxnId).collect(Collectors.toSet());

            knLogger.info(methodName, "crashedJobsCount - " , crashedJobTxnIds.size()," completedJobsCount -",completedJobsTxnIds.size(),
                    " failedJobsTxnIds -",failedJobsTxnIds.size());

            knLogger.debug(methodName, "crashedJobs - " , crashedJobTxnIds," completedJobs -",completedJobsTxnIds," failedJobsTxnIds -",failedJobsTxnIds);

            Set<String> allTxnIds = new HashSet<>();
            allTxnIds.addAll(completedJobsTxnIds);
            allTxnIds.addAll(crashedJobTxnIds);
            allTxnIds.addAll(failedJobsTxnIds);

            knLogger.debug(methodName, "txnIds " + allTxnIds);
            int jobCount = 0;
            int taskCount = 0;
            if (!jobs.isEmpty()) {
                jobCount = cacheUtil.deleteAsyncJob(allTxnIds);
                taskCount = cacheUtil.deleteAsyncJObTasksByTxnIdID(allTxnIds);

            }
            knLogger.info(methodName, "number of  jobs deleted " + jobCount," number of  tasks deleted " + taskCount);

            Collection<String> staleAsyncTaskTransactionIds = cacheUtil.getStaleTransactionInAsyncTask();
            if (!staleAsyncTaskTransactionIds.isEmpty()) {
                final AtomicInteger counter = new AtomicInteger(0);
                final int batchSize = 900;
                final Collection<List<String>> staleAsyncTaskTransactionIdsChunk = staleAsyncTaskTransactionIds.stream()
                        .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / batchSize))
                        .values();
                for (List<String> taskIdChunks : staleAsyncTaskTransactionIdsChunk) {
                    int staleTaskCount = cacheUtil.deleteAsyncJObTasksByTxnIdID(taskIdChunks);
                    knLogger.info(methodName, " staleTaskCount:", staleTaskCount);
                }
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            knLogger.error(methodName, e);
        }
        return false;

    }

    public String getCronExpression() {
        String methodName = "getCronExpression()";
        knLogger.info(methodName, "ENTRY: Get Cron Expression - ");
        // timer is set to every 45 minutes
        String timer = "45";
        String cronExpression = "0 */" + timer + " * ? * *";
        knLogger.info(methodName, "EXIT: Cron Expression - ", cronExpression);
        return cronExpression;

    }
}
