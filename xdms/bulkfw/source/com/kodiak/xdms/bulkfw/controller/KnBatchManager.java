/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.controller;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBatchManager.java
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
import com.kodiak.common.commdto.response.KnXDMPAMRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.kuidgenerator.KnKUIDConstants;
import com.kodiak.utilities.kuidgenerator.KnKUIDGenerator;
import com.kodiak.utilities.springcontainer.KnSpringContextProvider;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.bulkfw.KnBulkFwInitializer;
import com.kodiak.xdms.bulkfw.KnJobStatusObserver;
import com.kodiak.xdms.bulkfw.KnXDMBulkMediator;
import com.kodiak.xdms.bulkfw.dao.KnXDMBulkOrderInfoDAO;
import com.kodiak.xdms.bulkfw.dao.KnXDMProvBatchInfoDAO;
import com.kodiak.xdms.bulkfw.dto.KnBatchDTO;
import com.kodiak.xdms.bulkfw.dto.KnBulkDTO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants.STATUS;
import com.kodiak.xdms.bulkfw.resources.KnBulkProvConstants;
import com.kodiak.xdms.bulkfw.util.KnPAMBatchUtils;
import com.kodiak.xdms.bulkfw.util.KnPseudoMdnHandler;
import com.kodiak.xdms.server.common.resources.KnConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class KnBatchManager implements IStatusMgrNotifyIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBatchManager.class);

    //Batch size
    private int provBatchSize;
    private int updateBanBatchSize;
    private KnXDMBulkOrderInfoDAO boInfoDao;
    private KnXDMProvBatchInfoDAO batchInfoDao;
    private KnPAMBatchUtils pamBatchUtils;
    private KnXDMBulkMediator bulkMediator;
    private KnBulkFwController jobController;
    private KnJobStatusObserver knJobStatusObserver;
    private static KnBatchManager instance;

    private AtomicInteger jobId;

    public static KnBatchManager getInstance() {
        if (instance == null) {
            instance = new KnBatchManager();
            List<IStatusMgrNotifyIntf> list = new ArrayList<>();
            list.add(instance);
            KnStatusManagerClient.registerObjects(list);
        }
        return instance;
    }

    private KnBatchManager() {
        String methodName = "KnBatchManager()";
        knLogger.info(methodName, "ENTRY ");
        KnBulkFwInitializer bulkFwConfig = KnBulkFwInitializer.getInstance();
        String pttServerId = bulkFwConfig.getPttServerId();
        this.provBatchSize = bulkFwConfig.getBulkProvBatchSize();
        this.updateBanBatchSize = bulkFwConfig.getUpdateBanBatchSize();
        knLogger.info(methodName, "provBatchSize ", this.provBatchSize, "updateBanBatchSize", this.updateBanBatchSize);
        boInfoDao = new KnXDMBulkOrderInfoDAO(pttServerId);
        batchInfoDao = new KnXDMProvBatchInfoDAO(pttServerId);
        pamBatchUtils = new KnPAMBatchUtils(pttServerId);
        jobController = (KnBulkFwController) KnSpringContextProvider.getApplicationContext().getBean("knBulkFwController");
        bulkMediator = KnXDMBulkMediator.getInstance();
        knJobStatusObserver = KnJobStatusObserver.getInstance();
        jobId = new AtomicInteger(1);
    }

    /**
     * Method Create the First Batch for the BulkOrder received and invokes the Spring Batch processing
     * @param bulkOrderInfoDTO
     * @return
     */
    public synchronized IXDMResponseDTO prepareBatch(KnBulkDTO bulkOrderInfoDTO) {
        String methodName = "prepareBatch(KnBulkOrderInfoDTO)";
        IXDMResponseDTO errorResponse = null;
        KnPersisterTxn persisterTxn = null;
        int batchSize = provBatchSize;
        int banBatchSize = updateBanBatchSize;
        int bulkOrderType;
        int bulkOrderId = bulkOrderInfoDTO.getBulkOrderId();
        String pamAccId = String.valueOf(bulkOrderInfoDTO.getCorpId());
        bulkOrderType = bulkOrderInfoDTO.getBulkOrderType();
        KnMessage message = bulkOrderInfoDTO.getBulkOrderReqObj();
        knLogger.debug(methodName, "message ", message);
        int jobExecId = 0;
        KnXDMPAMAccInfoDTO bulkOrderReq = (KnXDMPAMAccInfoDTO) message.getPayLoad();

        knLogger.debug(methodName, "bulkOrderReq ", bulkOrderReq);
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            int range = bulkOrderReq.getSubsCount();
            int status = bulkOrderInfoDTO.getStatus();
            int existingSubsCount = bulkOrderReq.getExistingSubsCount();
            int batchExecType;
            if(bulkOrderInfoDTO.getBulkOrderType() == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_ROLLBACK.value()
                    ||bulkOrderInfoDTO.getBulkOrderType() == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_SUSPEND_ROLLBACK.value()
                    ||bulkOrderInfoDTO.getBulkOrderType() == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_REACTIVATE_ROLLBACK.value()){
                batchExecType = KnBulkProvConstants.BATCH_EXEC_TYPE.BATCH_EXEC_ROLLBACK.value();
                Thread.sleep(2000);
            }else{
                batchExecType = KnBulkProvConstants.BATCH_EXEC_TYPE.BATCH_EXEC_NORMAL.value();
            }
            //springBatchDao.cleanupSpringBatchTableForJobId(bulkOrderId,persisterTxn);
            KnBatchDTO batchDto = new KnBatchDTO();
            int clientType;
            knLogger.debug( methodName, "ClientType_BULK ",bulkOrderReq.getProfileDetails().getClient_Type());
            if (bulkOrderReq.getProfileDetails().getClient_Type() == KnConstants.SUBSCRIBERS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()) {
                clientType = KnBulkFwConstants.CLIENT_TYPE.CROSSCARRIERPTTCLIENT.valueOf();
            } else {
                clientType = KnBulkFwConstants.CLIENT_TYPE.WIFIONLY.valueOf();
            }
            knLogger.debug(methodName, "range ", range, "bulkOrderId ", bulkOrderId, "batchExecType ", batchExecType, "bulkOrderType ",
                    bulkOrderType, "Existing subscriber count ", existingSubsCount);
            //Insert batch job to batch table and get jobexecutionid.
            if (bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_CREATE.value()) {
                //Check whether it is an IN-Progress job. If yes, get the remaining number of subscribers to be created
                if (status == KnBulkFwConstants.STATUS.IN_PROGRESS.value()) {
                    List<String> mdnlist = bulkMediator.retrievePAMAccountMDNs(Integer.parseInt(pamAccId), null);
                    int presentCount = mdnlist.size();
                    range = range - presentCount;
                    knLogger.debug(methodName, "PAM Account already contains ", presentCount, " Creating ", range);
                    if (existingSubsCount > 0) {
                        range = range + existingSubsCount;
                        knLogger.debug(methodName, "Accounting the existing mdns in pam account", range);
                    }
                    //Delete the existing record from batch table so that it can start with new one.
                    batchInfoDao.delete(bulkOrderId, persisterTxn);
                    knJobStatusObserver.deleteJob(bulkOrderInfoDTO.getCorpId());
                    knLogger.debug(methodName, "Deleted batch entry");
                }

                if (range <= 0) {
                    knLogger.error(methodName, "Invalid subscriber range for CREATE SUBSCRIBER. Batch will not be created ", range);
                    persisterTxn.rollback();
                    return frameErrorResponse(null);
                }
                // Create pseudo mdn numbers (for create subscriber)
                List<String> mdnList = pamBatchUtils.getPseudoNumbers(range);
                knLogger.debug(methodName, "MDN List fetched for Create batch", KnGDPRTemplate.mdnList(mdnList));
                batchDto.setStartMdn(Long.parseLong(mdnList.get(0).trim()));
                batchDto.setEndMdn(Long.parseLong(mdnList.get(mdnList.size() - 1).trim()));
                batchDto.setBulkOrderId(bulkOrderId);
                KnPseudoMdnHandler pseudoHandler = KnPseudoMdnHandler.getInstance();
                pseudoHandler.insertPseudoList(bulkOrderId, mdnList);
                if (range < batchSize) {
                    batchSize = range;
                }
            } else if(bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.UPDATE_BAN.value()) {
                List<String> mdnlist = bulkMediator.retrieveBanMDNs(Integer.valueOf(pamAccId), bulkOrderReq.getHierarchyType(), persisterTxn);
                knLogger.debug(methodName, "MDN List fetched for Create batch", KnGDPRTemplate.mdnList(mdnlist),"bulkOrderReq :",bulkOrderReq);
                batchDto.setStartMdn(Long.parseLong(mdnlist.get(0).trim()));
                batchDto.setEndMdn(Long.parseLong(mdnlist.get(mdnlist.size() - 1).trim()));
                batchDto.setBulkOrderId(bulkOrderId);
                KnPseudoMdnHandler pseudoHandler = KnPseudoMdnHandler.getInstance();
                pseudoHandler.insertPseudoList(bulkOrderId, mdnlist);
                knLogger.debug(methodName, "Update Ban Batch Size", banBatchSize);
                batchSize = banBatchSize;
            }
              else{
                /*
                 * Get MDN list
            	 * This is required because BulkOrder request will not have the MDN list for DELETE/CHANGE_STATUS operations.
            	 * So it is fetched using a backend API call
            	 * This size of mdnlist will be updated to the request object and updated again to bulkorder table.
            	 *
            	 * DOWNGRADE operation require mdns to be fetched from pseudoMdn table
            	 */
                List<String> mdnlist;
                if (bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DOWNGRADE.value()) {
                    mdnlist = KnKUIDGenerator.getInstance().getKUIDByStatus(range,Integer.valueOf(pamAccId),KnKUIDConstants.KUID_MDN_STATUS.DOWNGRADE.valueOf());

                } else if(bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_ROLLBACK.value()) {
                	boolean isUpgrade = bulkOrderReq.getProfileDetails().isUpgrade();
                	knLogger.info(methodName, "hitting rollback path. need to check whether its a create rollback or upgrade rollback isUpgrade-", isUpgrade);
                	// upgrade flag will be set in knPAMXDMHelper
                	if (isUpgrade) {
                		knLogger.info(methodName, "upgrade rollback getting mdns by insertion time");
                		KnBulkDTO bulkDTO = boInfoDao.getBulkOrderDetails(bulkOrderId, persisterTxn);
                		mdnlist = bulkMediator.retrievePAMAccountMDNsByInsertionTime(Integer.parseInt(pamAccId), bulkDTO.getInsertionTime(), persisterTxn);

                	} else {
                		knLogger.info(methodName, "create rollback");
                		mdnlist = bulkMediator.retrievePAMAccountMDNs(Integer.parseInt(pamAccId), null);
                	}

                } else {
                    mdnlist = bulkMediator.retrievePAMAccountMDNs(Integer.parseInt(pamAccId), null);
                }

                if (mdnlist == null)
                    mdnlist = new ArrayList<>();
                range = mdnlist.size();
                bulkOrderReq.setSubsCount(range);
                message.setPayLoad(bulkOrderReq);
                knLogger.debug(methodName, "MDN List", KnGDPRTemplate.mdnList(mdnlist), "for opearaion ", bulkOrderType, "range ", range, " bylkorderid", bulkOrderId);
                //  Bulkorder request is updated for this purpose.
                knLogger.debug(methodName, "BulkOrder request updated");
                if (!mdnlist.isEmpty()) {
                    //MDN list is not empty. Set Start/End MDNs for initiating batch
                    batchDto.setStartMdn(Long.parseLong(mdnlist.get(0).trim()));
                    batchDto.setEndMdn(Long.parseLong(mdnlist.get((mdnlist.size()) - 1).trim()));
                    batchDto.setBulkOrderId(bulkOrderId);
                } else {
                    //MDN list is empty. There is no data to process. Update the bulkorder table as action COMPLETED.
                    knLogger.error(methodName, "MDN List is empty ");

                    bulkOrderInfoDTO.setStatus(STATUS.COMPLETED.value());
                    IXDMResponseDTO successResponse = frameSuccessResponse();
                    bulkOrderInfoDTO.setBulkOrderObj(successResponse);
                    bulkOrderInfoDTO.setBulkOrderRespObj(successResponse);
                    bulkOrderInfoDTO.setCompletionTime(System.currentTimeMillis());
                    boInfoDao.updateJobStatus(bulkOrderInfoDTO, persisterTxn);
                    knLogger.info(methodName, "Empty request. Updated success status for bulkorder ", bulkOrderId);
                    persisterTxn.save();
                    return frameErrorResponse(null);
                }

                //Delete request will go in a single bacth for delete requests. The same if range is less than batchsize
                if (range < batchSize || bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DELETE.value() ||bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_ROLLBACK.value()
                        || bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DOWNGRADE.value() || bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DOWNGRADE.value()) {
                    batchSize = range;
                }

            }
            batchDto.setOpertaionType(bulkOrderType);
            knLogger.debug(methodName, "batchsize ", batchSize);

            /*
            * New job
            * Insert job into batch table with status as IN_PROGRESS
            */
            if (jobId == null) {
                int maxJobId = batchInfoDao.getMaxJobExecutionId(persisterTxn);
                knLogger.info(methodName, "Max ID reternud for XDM_BATCH_TAble", maxJobId, ", Reinitializing JobID Automic integer ");
                jobId = new AtomicInteger(maxJobId + 1);
            }

            if (jobExecId >= (Integer.MAX_VALUE - 5)) {
                jobId = new AtomicInteger(1);
            }
            jobExecId = jobId.getAndIncrement();
            knLogger.debug(methodName, "Job Execution Id returned by AtomicInteger ", jobExecId);
            batchDto.setBatchExeId(jobExecId);
            batchDto.setBatchExeType(batchExecType);
            batchDto.setStatus(STATUS.IN_PROGRESS.value());
            batchDto.setBatchSize(batchSize);
            batchDto.setClientType(clientType);
            knLogger.info(methodName, "Inserting batch ", batchDto);
            batchInfoDao.insert(batchDto, persisterTxn);
            knJobStatusObserver.addJob(bulkOrderInfoDTO);
            persisterTxn.save();

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DB exception Occurred", e);
            KnDbUtil.rollback(persisterTxn);
            errorResponse = frameErrorResponse(e);
        } catch (KnException e) {
            knLogger.error(methodName, "KnException Occurred", e);
            KnDbUtil.rollback(persisterTxn);
            errorResponse = frameErrorResponse(e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception Occurred", e);
            KnDbUtil.rollback(persisterTxn);
            errorResponse = frameErrorResponse(e);
        }

        //Initiate batch job (only if there is a valid jobExecution Id and job type)
        if (jobExecId != 0 && bulkOrderType != 0) {
            knLogger.info(methodName, "Starting batch process with jobExceID ", jobExecId, ", bulkOrderType ", bulkOrderType, "pamAccId ", pamAccId, "bulkorderid", bulkOrderId);
            int jobExitStatus = initiateBatch(jobExecId, bulkOrderType);
            if (jobExitStatus == 0) {
                knJobStatusObserver.deleteJob(bulkOrderInfoDTO.getCorpId());
            }
        } else {
            //Job couldn't start.
            knLogger.error(methodName, "Batch not started for bulkorder ", bulkOrderId);
        	/*
        	 * Check whether the job didn't start because of some error occured in between.
        	 * If yes create a error response and update the job as FAILED in bulkorder table.
        	 */
            if (errorResponse != null) {
                try {
                    persisterTxn = KnPersisterTxn.getPersisterTxn();
                    persisterTxn.open();
                    knJobStatusObserver.deleteJob(bulkOrderInfoDTO.getCorpId());
                    bulkOrderInfoDTO.setStatus(STATUS.FAILED.value());
                    bulkOrderInfoDTO.setBulkOrderRespObj(errorResponse);
                    bulkOrderInfoDTO.setCompletionTime(System.currentTimeMillis());
                    boInfoDao.updateJobStatus(bulkOrderInfoDTO, persisterTxn);
                    persisterTxn.save();
                } catch (KnDAOException e) {
                    KnDbUtil.rollback(persisterTxn);
                    knLogger.error(methodName, "Exception occurred while updating bulkorder table", e);
                }
                knLogger.error(methodName, "Updated failure status for bulkorder ", bulkOrderId);
            }
        }

        knLogger.info(methodName, "EXIT ");
        return errorResponse;
    }


    /**
     * Initiates the Batch Job With JoBParameters
     * @param jobExecId
     * @param bulkOrderType
     */
    private int initiateBatch(int jobExecId, int bulkOrderType) {
        String methodName = "initiateBatch(int)";
        int jobExitStatus = 0;
        knLogger.info(methodName, "ENTRY ", jobExecId);
        String jobName = KnBulkFwConstants.getJobName(bulkOrderType);
        knLogger.debug(methodName, "jobName ", jobName);
        String[] jobPaths = new String[]{KnBulkFwConstants.SUBS_CONFIG_JOB_XML};
        Map<String, String> jobParameters = new HashMap<>();
        jobParameters.put(KnBulkFwConstants.JOB_EXE_ID, String.valueOf(jobExecId));
        knLogger.debug(methodName, "assigning work order for processing is ", jobExecId);
        jobExitStatus = jobController.startJob(jobPaths, jobName, jobParameters);
        knLogger.info(methodName, "EXIT ");
        return jobExitStatus;
    }

    private IXDMResponseDTO frameErrorResponse(Exception exception) {
        String methodName = "frameErrorResponse(Exception)";
        knLogger.info(methodName, "ENTRY ", exception);
        IXDMResponseDTO errroRespDto = new KnXDMPAMRespDTO();
        errroRespDto.setResponseStatus(KnConstants.RESPONSE_STATUS.FAILURE.value());
        //Check whether it is a KnException
        //Any other exception will be treated as INTERNAL SERVER ERROR
        errroRespDto.setResponseCode(KnBulkFwConstants.ErrorCodes.INTERNAL_SERVER_ERROR);
        if (exception != null) {
            errroRespDto.setResponseMessage(exception.getMessage());
        } else {
            errroRespDto.setResponseMessage("Internal Server Error in Bulk Framework");
        }
        knLogger.info(methodName, "Error Response DTO", errroRespDto);
        return errroRespDto;
    }

    private IXDMResponseDTO frameSuccessResponse() {
        String methodName = "frameSuccessResponse()";
        knLogger.info(methodName, "ENTRY ");
        IXDMResponseDTO responseDTO = new KnXDMPAMRespDTO();
        responseDTO.setResponseCode(KnBulkFwConstants.SUCCESS_CODE);
        responseDTO.setResponseMessage("SUCCESSFULLY executed the operation");
        responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
        knLogger.info(methodName, "Response DTO", responseDTO);
        return responseDTO;
    }

    /**
     * Method Listens for ACTIVE and StandBy events from Status manager
     * Fetches the MAX Id for XDM_PROV_BATCH table and reinitializes the jodId Automic Integer
     * @param previousState
     * @param currentState
     */
    @Override
    public void notify(KnStatusMgrConstants.CARD_STATES previousState, KnStatusMgrConstants.CARD_STATES currentState) {
        final String methodName = "notify(previousState,currentState)";
        knLogger.info(methodName, "Redunancy Status Notify -> Prev Status : ", previousState, ", Curr Status : ", currentState);
        if (KnStatusMgrConstants.CARD_STATES.ACTIVE.equals(currentState)){
                jobId = null;
        }
    }
}