/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnJobStatusObserver.java
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
import com.kodiak.common.commdto.request.KnXDMPAMAccInfoDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.springcontainer.KnSpringContextProvider;
import com.kodiak.xdms.bulkfw.controller.KnBulkFwController;
import com.kodiak.xdms.bulkfw.dao.KnXDMBulkOrderInfoDAO;
import com.kodiak.xdms.bulkfw.dao.KnXDMProvBatchInfoDAO;
import com.kodiak.xdms.bulkfw.dto.KnBatchDTO;
import com.kodiak.xdms.bulkfw.dto.KnBulkDTO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants.STATUS;

import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

public class KnJobStatusObserver extends Observable {
    private static final KnLogger knLogger = KnLogger.getLogger(KnJobStatusObserver.class);
    private static ConcurrentHashMap<Integer, KnBulkDTO> runningJobs = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Integer, KnBulkDTO> getRunningJobs() {
        return runningJobs;
    }

    private KnXDMProvBatchInfoDAO batchInfoDao;
    private KnXDMBulkOrderInfoDAO boInfoDao;
    private static KnJobStatusObserver instance;
    private static KnBulkFwController knBulkFwController;


    public KnJobStatusObserver() {
        initialize();
    }

    public static synchronized KnJobStatusObserver getInstance() {
        if (instance == null) {
            instance = new KnJobStatusObserver();
            KnJobStatusObserver.setRunningJobs(runningJobs);
            knBulkFwController = (KnBulkFwController) KnSpringContextProvider.getApplicationContext().getBean("knBulkFwController");
            instance.addObserver(knBulkFwController);
        }
        return instance;
    }

    private void initialize() {
        String pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        boInfoDao = new KnXDMBulkOrderInfoDAO(pttServerId);
        batchInfoDao = new KnXDMProvBatchInfoDAO(pttServerId);
    }

    /**
     * Method adds the job to running job list
     * @param job
     */
    public synchronized void addJob(KnBulkDTO job) {
        final String methodName = "addJob(KnProvBatchJobDTO, KnPersisterTxn)";
            runningJobs.put(job.getCorpId(),job);
        knLogger.info(methodName ,"Running Jobs" , runningJobs.size());
    }

    //Update the status of the JobexecId
    public void updateJobStatus(int status, int jobExecId) {
        final String methodName = "updateJobStatus(status, jobExecId, persisterTxn)";
        knLogger.info(methodName, "status - ", status, "jobExecId - ", jobExecId);
            if (status == STATUS.COMPLETED.value()) {
                knLogger.info(methodName, "Job Completed for Job exec ID - ", jobExecId, "Notifying Observers");
            }
    }

    /**
     * Notify the Observer after completion of job to start processing of Jobs in JobStore
     * @param corpId
     */
    public void deleteJob(int corpId) {
        final String methodName = "deleteJob(KnProvBatchJobDTO)";
        setChanged();
        knLogger.info(methodName, "Job Completed for Job exec ID - ", corpId, "Running jobs", runningJobs);
        runningJobs.remove(corpId);
        knLogger.debug(methodName, "After removing completed jos from running jobs - ", runningJobs);
        knLogger.info(methodName, "Notifying Observers", corpId);
        notifyObservers();
    }

    /**
     * Method to Add RollBack Bulk Job to JobStore Directly to persist the BulkOrderId
     * @param job
     * @param responseDTO
     * @param pamAccountInfo
     * @param corpid
     * @param persisterTxn
     * @throws Exception
     */

    public void addRollbackJob(KnBatchDTO job, IXDMResponseDTO responseDTO,KnXDMPAMAccInfoDTO pamAccountInfo, int corpid,  KnPersisterTxn persisterTxn) throws Exception{
        String methodname = "addRollbackJob(KnProvBatchJobDTO,IXDMResponseDTO,KnPersisterTxn)";
        knLogger.info( methodname, "ENTRY ");
        job.setStatus(STATUS.FAILED.value());
        batchInfoDao.update(job, persisterTxn);
        KnBulkDTO  bulkOrderInfo = new KnBulkDTO();
        long cycleMdn = job.getCycleMdn();
        knLogger.info(methodname, "cycleMdn ", cycleMdn);
        if(cycleMdn == 0)	{
            //Not even a single cycle has been executed successfully. The job can be straight away marked as failed
            batchInfoDao.delete(job.getBulkOrderId(),persisterTxn);
            deleteJob(corpid);
            bulkOrderInfo.setStatus(STATUS.FAILED.value());
        }
        else	{
            //Insert rollback record to batch table.
            batchInfoDao.delete(job.getBulkOrderId(), persisterTxn);
            bulkOrderInfo.setBulkOrderId(job.getBulkOrderId());
            if(job.getOpertaionType()==KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_CREATE.value()){
                bulkOrderInfo.setBulkOrderType(KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_ROLLBACK.value());
            }
            if(job.getOpertaionType()==KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_REACTIVATE.value()){
                bulkOrderInfo.setBulkOrderType(KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_SUSPEND_ROLLBACK.value());
            }
            if(job.getOpertaionType()==KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_SUSPEND.value()){
                bulkOrderInfo.setBulkOrderType(KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_REACTIVATE_ROLLBACK.value());
            }

            bulkOrderInfo.setRollback(true);
            bulkOrderInfo.setStatus(KnBulkFwConstants.STATUS.NOT_STARTED.value());
            KnMessage msg = new KnMessage();
            msg.setPayLoad(pamAccountInfo);
            bulkOrderInfo.setPriority(1);
            bulkOrderInfo.setCorpId(corpid);
            bulkOrderInfo.setChangeLevel(0);
            bulkOrderInfo.setInsertionTime(System.currentTimeMillis());
            bulkOrderInfo.setStatus(STATUS.NOT_STARTED.value());
            knLogger.info(methodname, "Bulk Object for Rollback ", bulkOrderInfo);
            bulkOrderInfo.setBulkOrderReqObj(msg);
            deleteJob(corpid);
            knBulkFwController.newJobAlert(null, bulkOrderInfo);
            knLogger.info(methodname, "Rollback set for CREATE_SUBSCRIBER job with bulkorderid ", bulkOrderInfo.getBulkOrderId(), " range from ", job.getStartMdn(), " to ", cycleMdn, " jobExceutionId");

        }
        //Update bulkorder table
        bulkOrderInfo.setBulkOrderId(job.getBulkOrderId());
        bulkOrderInfo.setCompletionTime(System.currentTimeMillis());
        bulkOrderInfo.setBulkOrderObj(responseDTO);
        bulkOrderInfo.setBulkOrderRespObj(responseDTO);
        boInfoDao.updateJobStatus(bulkOrderInfo, persisterTxn);
        knLogger.info( methodname, "EXIT ");
    }

    public static void setRunningJobs(ConcurrentHashMap<Integer, KnBulkDTO> runningJobs) {
        KnJobStatusObserver.runningJobs = runningJobs;
    }
}