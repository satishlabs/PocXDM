/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnThreadExecutors;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.server.common.resources.KnConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class KnMCXGroupCleanUpOnMCPTTDisabledBitAudit implements Runnable, IStatusMgrNotifyIntf {

    private static final KnLogger knLogger = KnLogger.getLogger(KnMCXGroupCleanUpOnMCPTTDisabledBitAudit.class);
    private ConcurrentHashMap<String, KnAsyncJobDTO> mdnMcxGroupCleanUpJobs = new ConcurrentHashMap<>();
    private KnStatusMgrConstants.CARD_STATES currentRedState = KnStatusMgrConstants.CARD_STATES.UNKNOWN;
    public static KnMCXGroupCleanUpOnMCPTTDisabledBitAudit instance;
    private static KnGeneralCacheUtil generalCacheUtil;
    private static ExecutorService executor =null;
    private String taskMdn;

    static{
        executor = KnThreadExecutors.newFixedThreadExecutor(4,"MCX_GROUP_CLEAN_TASK");
        knLogger.info("Intiaizing executor :",executor);
    }

    @Override
    public void run() {
        if (getCurrentRedundancyStatus() == KnStatusMgrConstants.CARD_STATES.ACTIVE) {
            executeAudit();
        }
    }

    private void executeAudit(){
        String methodName = "executeAudit()";
        String failedTxn=null;

        try {
            mdnMcxGroupCleanUpJobs.putAll(generalCacheUtil.getJobStatusByOperationIdAndStatus
                    (KnConstants.UPM_OPERATION_TYPE.VERY_LARGE_GROUP_BIT_DISABLED.Value(), KnConstants.UPM_JOB_STATUS.VERY_LARGE_GRP_CLEAN_NEW.Value()));

            if(!mdnMcxGroupCleanUpJobs.isEmpty()){
                knLogger.debug(methodName," mdnMcxGroupCleanUpJobs :",mdnMcxGroupCleanUpJobs);
                for(Map.Entry<String, KnAsyncJobDTO> mdnCorp:mdnMcxGroupCleanUpJobs.entrySet()){
                    KnAsyncJobDTO asyncJob = mdnCorp.getValue();
                    Integer corpId = asyncJob.getCorpId();
                    taskMdn = mdnCorp.getKey();
                    String txnId = asyncJob.getTxnId();
                    int resourceType = asyncJob.getResourceType();
                    failedTxn=txnId;
                    if(KnConstants.UPM_RESOURCE_TYPE.PROFILEMDN.Value()==resourceType){
                        //profile mdn
                        KnUPMMCXGroupCleanUpOnMCPTTDisabledBitTask deleteProfileMdnTask=new KnUPMMCXGroupCleanUpOnMCPTTDisabledBitTask(corpId,taskMdn,txnId);
                        executor.execute(deleteProfileMdnTask);
                    }else {
                        //base mdn
                        KnMCXGroupCleanUpOnMCPTTDisabledBitTask cleanUpTask=new KnMCXGroupCleanUpOnMCPTTDisabledBitTask(corpId,taskMdn,txnId);
                        executor.execute(cleanUpTask);
                    }
                    mdnMcxGroupCleanUpJobs.remove(taskMdn);
                    Thread.sleep(1000);
                }
            }
        } catch (KnDAOException e) {
            mdnMcxGroupCleanUpJobs.remove(taskMdn);
            updateFailedAsyncJobTaskStatus(failedTxn);
            knLogger.error(methodName," KnDAOException :",e.getMessage());

        }catch (Throwable e) {
            mdnMcxGroupCleanUpJobs.remove(taskMdn);
            updateFailedAsyncJobTaskStatus(failedTxn);
            knLogger.error(methodName," Throwable :",e.getMessage());
        }
    }

    private void updateFailedAsyncJobTaskStatus(String failedTxn){
        String methodName = "updateFailedAsyncJobTaskStatus()";
        try {
            generalCacheUtil.updateAsyncJobStatus(failedTxn, KnConstants.UPM_JOB_STATUS.FAILURE.Value());
        } catch (Exception e) {
            knLogger.error(methodName," Exception :",e.getMessage());
        }
    }

    private KnStatusMgrConstants.CARD_STATES getCurrentRedundancyStatus() {
        return this.currentRedState;
    }

    @Override
    public void notify(KnStatusMgrConstants.CARD_STATES previousState, KnStatusMgrConstants.CARD_STATES currentState) {
        String methodName = "notify(KnStatusMgrConstants.CARD_STATES, KnStatusMgrConstants.CARD_STATES)";
        knLogger.info(methodName, "notify from Status Mgr [Prev State - ", previousState,
                "; Current State - ", currentState + "]");
        this.currentRedState = currentState;
        knLogger.info(methodName, "Current State ", currentState);
    }

    public static KnMCXGroupCleanUpOnMCPTTDisabledBitAudit getInstance() {
        if (instance == null) {
            instance = new KnMCXGroupCleanUpOnMCPTTDisabledBitAudit();
            //registering for Redundancy Notify Status
            List<IStatusMgrNotifyIntf> list = new ArrayList<>();
            list.add(instance);
            KnStatusManagerClient.registerObjects(list);
            knLogger.info("getInstance()", "registered for Status..");
            generalCacheUtil = KnGeneralCacheUtil.getInstance();
        }
        return instance;
    }
}
