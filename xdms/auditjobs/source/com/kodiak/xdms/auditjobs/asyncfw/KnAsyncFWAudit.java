/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.auditjobs.asyncfw;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.resources.KnConstants;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.ASYNCFW_MAX_JOB_RUNTIME;

public class KnAsyncFWAudit extends KnAbstractJob {

    private static final long serialVersionUID = 1L;
    private static final KnLogger knLogger = KnLogger.getLogger(KnAsyncFWAudit.class);
    private KnGeneralCacheUtil cacheUtil = KnGeneralCacheUtil.getInstance();
    private static final long DEFAULT_RUNNING_TIME = 23 * 60 * 60 * 1000;

    public boolean executeTask() throws KnJobSchedulerException {
        String methodName = "executeTask()";
        long currentTime = Instant.now().toEpochMilli();
        String jobMaxRunTImeStr = System.getenv(ASYNCFW_MAX_JOB_RUNTIME);
        knLogger.info(methodName,"jobMaxRunTImeStr - ",jobMaxRunTImeStr);

        long jobMaxRunTimeInMs = DEFAULT_RUNNING_TIME;
        if(jobMaxRunTImeStr!=null){
            try {
                long longVal = Long.parseLong(jobMaxRunTImeStr);
                if(longVal>jobMaxRunTimeInMs) {
                    jobMaxRunTimeInMs = longVal;
                }
            }catch (Exception e){
                knLogger.error(methodName,"Exception - ",e);
            }
        }
        long runTime = currentTime - jobMaxRunTimeInMs;
        knLogger.info(methodName, "ENTRY: Start of Async FW Audit job exp Time " + currentTime," runTime -",runTime,"jobMaxRunTimeInMs -",jobMaxRunTimeInMs);
        try {
            List<KnAsyncJobDTO> longRunningJobs = cacheUtil.getAllLongRunningJobs(runTime);
            knLogger.info(methodName, " longRunningJobs :", longRunningJobs);
            Set<String> txnIds = longRunningJobs.stream().map(KnAsyncJobDTO::getTxnId).collect(Collectors.toSet());
            knLogger.info(methodName, " longRunningJobs txnIds :",txnIds);
            int count = -1;
            if (!longRunningJobs.isEmpty()) {
                count = cacheUtil.updateAllAsyncJobStatus(txnIds, KnConstants.UPM_JOB_STATUS.CRASHED.Value());
                knLogger.info(methodName, "updating CAT_ASYNC_TXN_INFO table - ");
                cacheUtil.updateCATTXNStatus(txnIds, KnConstants.UPM_JOB_STATUS.CRASHED.Value(), "CRASHED", 1);
                knLogger.info(methodName, "number of  jobs updated to crashed status - ", count);
            }
            //Stale record in CAT_ASYNC_TXN_INFO , will be handle by cat.
            /**
            List<String> catStaleTransactionIds = cacheUtil.getCATStaleTransactionIds();
            knLogger.info(methodName, " catStaleTransactionIds :", catStaleTransactionIds);
            if(!catStaleTransactionIds.isEmpty()){
                cacheUtil.updateCATTXNStatus(catStaleTransactionIds,
                        KnConstants.UPM_JOB_STATUS.COMPLETE.Value(),
                        "SUCCESS", KnConstants.MSGREADSTATUS.UNREAD.Value());
            }
             */
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred");
            knLogger.error(methodName, e);
        }
        return false;

    }

    public String getCronExpression() {
        String methodName = "getCronExpression()";
        knLogger.info(methodName, "ENTRY: Get Cron Expression - ");
        // timer is set to every 3 minute
        String timer = "3";
        String cronExpression = "0 */" + timer + " * ? * *";
        knLogger.info(methodName, "EXIT: Cron Expression - ", cronExpression);
        return cronExpression;

    }
}
