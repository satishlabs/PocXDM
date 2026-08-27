/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.controller;

import com.kodiak.common.dao.KnDbUtil;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkFwController.java
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

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.common.resources.KnThreadExecutors;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;
import com.kodiak.utilities.pocxlaclient.KnPoCXLAClient;
import com.kodiak.utilities.pocxlaclient.KnPoCXLAEventDTO;
import com.kodiak.utilities.pocxlaclient.KnPoCXLATableDetailsDTO;
import com.kodiak.utilities.pocxlaclient.KnRegisterTblDTO;
import com.kodiak.utilities.pocxlaclient.event.KnTableInsert;
import com.kodiak.utilities.springcontainer.KnSpringContextProvider;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.bulkfw.dao.KnXDMBulkOrderInfoDAO;
import com.kodiak.xdms.bulkfw.dto.KnBulkDTO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;
import com.kodiak.xdms.bulkfw.validator.KnBulkValidator;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.kodiak.utilities.generatealarmutil.KnAlarmConstants.*;

@Component
public class KnBulkFwController implements Observer, ApplicationListener<KnTableInsert> {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkFwController.class);

    private static KnStatusMgrConstants.CARD_STATES currentState;
    private KnBulkValidator knBulkValidator;
    private KnXDMBulkOrderInfoDAO boinfoDao;
    private ClassPathXmlApplicationContext configAppContext;
    private JobLauncher jobLauncher;
    private static final int datastoreId = 0;
    private static final int subsystemid = 110;
    private ExitCodeMapper exitCodeMapper = new SimpleJvmExitCodeMapper();
    KnPersisterTxn persisterTxn;
    private final String ACTIVE_RELEASE_DIR = System.getProperty("activeRelDir");
    private final String fileName = ACTIVE_RELEASE_DIR + File.separator + "appconf.properties";
    private final String BULk_REQUEST_EXP_TIME = "BULk_REQUEST_EXP_TIME";
    private Long bulkJobExp = 60L;
    private AtomicBoolean isCriticalAlarmRaised = new AtomicBoolean(true);

    public ConcurrentLinkedDeque<KnBulkDTO> getJobStore() {
        return jobStore;
    }

    public static ConcurrentLinkedDeque<KnBulkDTO> jobStore;
    private static ExecutorService service;
    private KnBulkThread bulkExtThread;


    private KnBulkFwController() throws KnException {
        knLogger.info("KnBulkFwController", "Loading context from constructor ");
        jobStore = new ConcurrentLinkedDeque<>();
        service = KnThreadExecutors.newDynamicThreadPool(1, 1, 0L, "KnBulkFw");
        knBulkValidator = KnBulkValidator.getInstance();
        String pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        boinfoDao = new KnXDMBulkOrderInfoDAO(pttServerId);
        Object value = KnGeneralUtil.getPropertyValue(fileName, BULk_REQUEST_EXP_TIME);
        if (value != null) {
        	bulkJobExp = Long.valueOf(value.toString());
        }
        knLogger.info("KnBulkFwController", "EXIT: Loading context from constructor ");
    }

    /**
     * Method Registers the XDM_BULK_ORDER_INFO with PoCXLA server
     */
    public void registerDynamicClient() {
        String methodName = "registerDynamicClient()";
        try {
            KnPoCXLAClient xlaClient = KnPoCXLAClient.getInstance();
            List<KnRegisterTblDTO> tblesToRegister = new ArrayList<>();
            KnRegisterTblDTO regTable = new KnRegisterTblDTO();
            regTable.setTableName(KnBulkFwConstants.BO_INFO_TABLE_NAME_DG);
            regTable.setDataStoreId(datastoreId);
            tblesToRegister.add(regTable);
            knLogger.info(methodName, "Bulk Table for registration", tblesToRegister);
            xlaClient.registerTables(subsystemid, tblesToRegister);
            knLogger.info(methodName, "Bulk Table for registration Successfull");
        } catch (Exception e) {
            knLogger.fatal(methodName, "Exception occured :  Failed to register table", e);
        }
    }


    /**
     * Method to construct BulkOrder DTO and submit to Batch processing on New Job alert
     * @param tableData
     * @param bulkDTO
     * @throws Exception
     */
    public void newJobAlert(KnPoCXLATableDetailsDTO tableData, KnBulkDTO bulkDTO) throws Exception {
        String methodName = "newJobAlert";
        knLogger.info(methodName, "Job Alert From  WithIn bulk, ", bulkDTO);
        if (tableData!=null){
        Map<String, KnPoCXLAEventDTO> columnDetailsMap = tableData.getColumnDetailsMap();
        KnBulkDTO bulkDto = new KnBulkDTO();
        KnPoCXLAEventDTO dto;
        if (!tableData.isEncodingFailed()) {
            dto = columnDetailsMap.get("BULKORDER_ID");
            if (dto != null) {
                bulkDto.setBulkOrderId((int) dto.getColumnValue());
            }

            dto = columnDetailsMap.get("BULKORDER_TYPE");
            if (dto != null) {
                bulkDto.setBulkOrderType((int) dto.getColumnValue());
            }

            dto = columnDetailsMap.get("CHANGE_LEVEL");
            if (dto != null) {
                bulkDto.setChangeLevel((int) dto.getColumnValue());
            }

            dto = columnDetailsMap.get("CORP_ID");
            if (dto != null) {
                bulkDto.setCorpId((int) dto.getColumnValue());
            }

            dto = columnDetailsMap.get("BO_REQ_OBJECT");
            if (dto != null) {
                bulkDto.setBulkOrderReqObj(getObjectFromStream((byte[]) dto.getColumnValue()));
            }

            dto = columnDetailsMap.get("INSERTION_TIME");
            if (dto != null) {
                String longvalue = dto.getColumnValue().toString();
                bulkDto.setInsertionTime(new Long(longvalue));
            }

            dto = columnDetailsMap.get("STATUS");
            if (dto != null) {
                bulkDto.setStatus((int) dto.getColumnValue());
            }

            dto = columnDetailsMap.get("BO_REQ_OBJECT_VERSION");
            if (dto != null) {
                bulkDto.setBulkOrderObjVersion(String.valueOf(dto.getColumnValue()));
            }

            dto = columnDetailsMap.get("COMPLETION_TIME");
            if (dto != null) {
                String longvalue = dto.getColumnValue().toString();
                bulkDto.setCompletionTime(new Long(longvalue));
            }

            dto = columnDetailsMap.get("PRIORITY");
            if (dto != null) {
                bulkDto.setPriority((int) dto.getColumnValue());
            }
        } else {
            dto = columnDetailsMap.get("BULKORDER_ID");
            if (dto != null) {
                bulkDto.setBulkOrderId((int) dto.getColumnValue());
            }
            try {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                bulkDto = boinfoDao.getBulkOrderDetails(bulkDto.getBulkOrderId(), persisterTxn);
                persisterTxn.save();
            } catch (Exception e) {
                knLogger.error(methodName, "Failed to get bulkOrder details for bulkorder ID", e);
                persisterTxn.rollback();
            }
            }
            jobStore.addLast(bulkDto);
            knLogger.info(methodName, "Bulk order table details from PocXlaClient", bulkDto);
            knLogger.debug(methodName, "Job store contents", jobStore, " after adding Bulk Job  ");
            processNewJobs(jobStore);
        }
        if(bulkDTO != null) {
            jobStore.addLast(bulkDTO);
            knLogger.debug(methodName, "ENTRY: start Bulk FW thread - ");
            if (bulkExtThread == null)
                bulkExtThread = new KnBulkThread();
            try {
                //thread required
                service.submit(bulkExtThread);
                knLogger.debug(methodName, "Started !!!!!!! ");
            } catch (Exception e) {
                knLogger.error(methodName, "Failed to start Bulk FW thread", e);
            }
        }
        knLogger.debug(methodName, "Job store contents", jobStore, " after adding Bulk Job  ");



    }

    /**
     * Method Will act on the Jobs alert present in the JobStore
     * Performs Validation of Jobs and Updating Job Store
     * @param jobs
     */
    public void processNewJobs(ConcurrentLinkedDeque<KnBulkDTO> jobs) {
        String methodName = "processNewJobs()";
        knLogger.info(methodName, "New Job for processing");
        try {
            ConcurrentLinkedDeque<KnBulkDTO> bulkOders = new ConcurrentLinkedDeque<>();
            bulkOders.addAll(jobs);
            List<KnBulkDTO> validJobs = validateJob(bulkOders);
            knLogger.debug(methodName, "Valid Jobs", validJobs);
            updateJobStore(validJobs);
            knLogger.debug(methodName, "Update job store by removing jobs which are submitted to batch processing");
            startBatchJob(validJobs);
            knLogger.info(methodName, "Starting Batch job for Bulk orders in Job store");

        } catch (Exception e) {
            knLogger.error(methodName, "Exception while executing New job", e);
        }
    }

    /**
     * Method validates the Jobs in Jobstore
     * @param jobs
     * @return
     * @throws KnException
     */
    private List<KnBulkDTO> validateJob(ConcurrentLinkedDeque<KnBulkDTO> jobs) throws KnException {
        String methodName = "validateJob()";
        knLogger.debug(methodName, "Jobs for Validation ", jobs);
        List<KnBulkDTO> jobsForExcecution = knBulkValidator.validate(jobs);
        knLogger.info(methodName, "Validated jobs for Execution ", jobsForExcecution.size());
        return jobsForExcecution;
    }

    private void startBatchJob(List<KnBulkDTO> jobsForExcecution) throws KnException {
        String methodName = "startBatchJob(int)";
        KnBatchManager knBatchManager = KnBatchManager.getInstance();
        knLogger.debug(methodName, "Validated jobs for Execution ", jobsForExcecution.size());
        for (KnBulkDTO knBulkOrderInfoDTO : jobsForExcecution) {
            knLogger.info(methodName, "Preparing Batch for  ", knBulkOrderInfoDTO.getBulkOrderId());
            knBatchManager.prepareBatch(knBulkOrderInfoDTO);
        }
        knLogger.info(methodName, "EXIT ");

    }

    /**
     * Entry Point to Spring Batch processing
     * @param jobPaths
     * @param jobName
     * @param jobParams
     * @return
     */
    public synchronized int startJob(String[] jobPaths, String jobName, Map<String, String> jobParams) {
        String methodName = "startJob(String, String, JobParameters)";
        knLogger.info(methodName, "ENTRY: Start Job ");
        int jobExitStatus = 0;

        try {
            ApplicationContext jobContext = KnSpringContextProvider.getApplicationContext();
            if (jobContext == null) {
                throw new Exception("Invalid Data used for Initialization of Spring Batch");
            }
            // Generating the Job Parameters for the received job Params
            JobParameters jobParameters = null;
            if (jobParams != null && !jobParams.isEmpty()) {
                jobParameters = marshallJobParameters(jobParams);
            }
            knLogger.info(methodName, " Job Parameters are ", jobParameters);
            Job job;
            job = (Job) jobContext.getBean(jobName);

            knLogger.debug(methodName, "Launching Job ", job);
            jobLauncher = (JobLauncher) jobContext.getBean("jobLauncher");
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            knLogger.debug(methodName, "Launched Job ", job, " with ", jobExecution);
            jobExitStatus = exitCodeMapper.intValue(jobExecution.getExitStatus().getExitCode());

            knLogger.debug(methodName, "Job Status", jobExitStatus);
            clearCriticalAlarmIfRaised();
        } catch (NoSuchJobException e) {
            knLogger.error(methodName, "NoSuchJobException occurred", e);
            knLogger.error(methodName, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred", e);
            knLogger.error(methodName, e);
            if (e instanceof CannotGetJdbcConnectionException) {
                // Raise the critical alarm only once
                isCriticalAlarmRaised.set(false);
                raiseCriticalAlarmIfNotRaised();
            }

        }
        knLogger.info(methodName, "EXIT: Start Job ", jobExitStatus);
        return jobExitStatus;
    }

    /**
     * Method to Update the JobStore
     * @param jobsForExcecution
     */
    private void updateJobStore(List<KnBulkDTO> jobsForExcecution) {
        String methodName = "updateJobStore";
        knLogger.debug(methodName, "Jobs before Updating Job store", jobStore);
        for (KnBulkDTO bulkDTO : jobStore) {
            for (KnBulkDTO validatedJobs : jobsForExcecution) {
                if (bulkDTO.getBulkOrderId() == validatedJobs.getBulkOrderId()) {
                    knLogger.debug(methodName, "BulkOrder validated for Execution ", bulkDTO.getBulkOrderId());
                    jobStore.remove(bulkDTO);
                }
            }
        }
        knLogger.info(methodName, "Jobs After Updating Job store", jobStore);
    }

    private JobParameters marshallJobParameters(Map<String, String> jobParams) {
        String methodName = "generateJobParameters(HashMap<String, String>)";
        knLogger.info(methodName, "ENTRY: generate Jobparameters");
        JobParameters genJobParams = null;

        JobParametersBuilder paramBuilder = new JobParametersBuilder();
        for (Map.Entry<String, String> jobParamsMap : jobParams.entrySet()) {
            paramBuilder.addString(jobParamsMap.getKey(), jobParamsMap.getValue());
        }
        genJobParams = paramBuilder.toJobParameters();
        knLogger.info(methodName, "EXIT: Job Parameters generated ");
        return genJobParams;
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        final String methodName = "update(Observable arg0, Object arg1)";
        knLogger.info(methodName, "Running jobs Completed");
        if (bulkExtThread == null)
            bulkExtThread = new KnBulkThread();
        try {
            //thread required
            service.submit(bulkExtThread);
            knLogger.info(methodName, "Started !!!!!!! ");
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to start Bulk FW thread", e);
        }

    }

    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    /**
     * Method to receive PocXLA event
     * @param event
     */
    public void onApplicationEvent(KnTableInsert event) {
        String methodName = "onApplicationEvent()";

        if (KnStatusMgrConstants.CARD_STATES.ACTIVE.equals(KnStatusManagerClient.getCurrentState())) {
        	knLogger.info(methodName, "Card is in ACTIVE mode-", KnStatusManagerClient.getCurrentState(),  ", processing bulk jobList");
            KnPoCXLATableDetailsDTO tableData;
            tableData = event.getKnPoCXLATableDetailsDTO();
            if (tableData != null) {
                knLogger.info(methodName, "ENTRY: tableData, Insert event received for table ", tableData.getTableName());
                knLogger.info(methodName, "tableData", tableData);
                if (tableData.getTableName().equals(KnBulkFwConstants.BO_INFO_TABLE_NAME_DG)) {
                    try {
                        newJobAlert(tableData, null);
                    } catch (Exception e) {
                        knLogger.error(methodName, "Failed to add new job to job store", e);
                    }
                }
            }
        }else{
            knLogger.info(methodName, "Card is in STAND-BY mode-", KnStatusManagerClient.getCurrentState(),  ", Jobs not added to bulk jobList");
        }
    }


    /**
     * Generate KnMessage object from byte array
     *
     * @param requestBuff
     * @return
     */
    private KnMessage getObjectFromStream(byte[] requestBuff) {
        String methodName = "getObjectFromStream(byte[])";
        knLogger.debug(methodName, "ENTRY ", requestBuff.length);
        KnMessage msg = null;
        try {
            ObjectInputStream objectIn;
            objectIn = new ObjectInputStream(new ByteArrayInputStream(requestBuff));
            msg = (KnMessage) objectIn.readObject();
            objectIn.close();
        } catch (IOException e) {
            knLogger.error(methodName, "IO Exception occurred", e);
        } catch (ClassNotFoundException e) {
            knLogger.error(methodName, "ClassNotFoundException occurred", e);
        }
        knLogger.info(methodName, "EXIT ");
        return msg;
    }
    
    public List<KnBulkDTO> getPendingBulkOrdersDetails() {
    	final String methodName = "getPendingBulkOrdersDetails";
    	KnPersisterTxn persisterTxn = null;
    	List<KnBulkDTO> bulkOrderList = null;
    	try {
			persisterTxn = KnPersisterTxn.getPersisterTxn();
			knLogger.debug(methodName, "Opening the Transaction");
			persisterTxn.open();
			
			knLogger.info(methodName, "bulkJobExp in mins-", bulkJobExp);
			long currentTimer = Calendar.getInstance().getTimeInMillis();
    		Long bulkExpTimer = bulkJobExp * 60 * 1000;
    		long expiryTimer = currentTimer - bulkExpTimer;
			
    		bulkOrderList = this.boinfoDao.getPendingBulkOrderDetails(expiryTimer, persisterTxn);
			persisterTxn.save();
			
		} catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred - ", e);
            KnDbUtil.rollback(persisterTxn);
        }
    	return bulkOrderList;
    }

    /**
     * Raises a critical alarm if it has not already been raised.
     */
    private void raiseCriticalAlarmIfNotRaised() {
        if (isCriticalAlarmRaised.compareAndSet(false, true)) {
            KnAlarmGeneratorUtil.generateAlarmRest(ALARM_SPRING_BATCH_TT_CONNECTION, SEVERITY_CRITICAL, XDMMANAGEDOBJECT_CLASSTYPE, KnDbUtil.getDBConfigInfo().getLocalPttId());
        }
    }

    /**
     * Clears the critical alarm if it has been raised.
     */
    private void clearCriticalAlarmIfRaised() {
        if (isCriticalAlarmRaised.get()) {
            KnAlarmGeneratorUtil.generateAlarmRest(ALARM_SPRING_BATCH_TT_CONNECTION, SEVERITY_CLEAR, XDMMANAGEDOBJECT_CLASSTYPE, KnDbUtil.getDBConfigInfo().getLocalPttId());
            isCriticalAlarmRaised.set(false); // Reset the flag
        }
    }
}



