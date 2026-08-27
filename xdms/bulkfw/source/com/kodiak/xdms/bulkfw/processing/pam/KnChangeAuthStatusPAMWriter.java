/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.processing.pam;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnChangeAuthStatusPAMWriter.java
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
import com.kodiak.common.commdto.request.KnXDMPAMSubsProfInfoDTO;
import com.kodiak.common.commdto.response.IXDMResponseDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.bulkfw.KnBulkFwInitializer;
import com.kodiak.xdms.bulkfw.KnJobStatusObserver;
import com.kodiak.xdms.bulkfw.KnXDMBulkMediator;
import com.kodiak.xdms.bulkfw.dao.KnXDMBulkOrderInfoDAO;
import com.kodiak.xdms.bulkfw.dao.KnXDMProvBatchInfoDAO;
import com.kodiak.xdms.bulkfw.dto.KnBatchDTO;
import com.kodiak.xdms.bulkfw.dto.KnBulkDTO;
import com.kodiak.xdms.bulkfw.dto.KnProvBatchProfileDTO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants.MEDIATOR_RESP_STATUS;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants.STATUS;
import com.kodiak.xdms.bulkfw.resources.KnBulkProvConstants;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

import java.util.List;

public class KnChangeAuthStatusPAMWriter implements ItemWriter<KnProvBatchProfileDTO> {
    private static final KnLogger knLogger = KnLogger.getLogger(KnChangeAuthStatusPAMWriter.class);
    private KnXDMProvBatchInfoDAO provBatchInfoDAO;
    private KnXDMBulkOrderInfoDAO boInfoDao;
    private KnXDMBulkMediator bulkMediator;
    private int serviceJob;
    private KnJobStatusObserver jobStatusObserver;

    public KnChangeAuthStatusPAMWriter() {
        KnBulkFwInitializer bulkFwConfig = KnBulkFwInitializer.getInstance();
        String pttServerId = bulkFwConfig.getPttServerId();
        provBatchInfoDAO = new KnXDMProvBatchInfoDAO(pttServerId);
        boInfoDao = new KnXDMBulkOrderInfoDAO(pttServerId);
        bulkMediator = KnXDMBulkMediator.getInstance();
        jobStatusObserver = KnJobStatusObserver.getInstance();
    }

    @Override
    public void write(Chunk<? extends KnProvBatchProfileDTO> chunk) throws Exception {
        List<? extends KnProvBatchProfileDTO> batchIpDtos = chunk.getItems();
        String methodname = "write(List)";
        knLogger.info(methodname, "ENTRY ");
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        KnProvBatchProfileDTO batchDto = batchIpDtos.get(0);
        IXDMResponseDTO responseDTO = null;
        KnBulkDTO bulkOrderInfo = null;
        knLogger.debug(methodname, "batchDto ", batchDto);
        KnBatchDTO batchInfo = batchDto.getBatchInfoDTO();
        serviceJob = batchInfo.getOpertaionType();
        //Check whether this is a rollback job
        boolean isRollback = (batchInfo.getBatchExeType() == KnBulkProvConstants.BATCH_EXEC_TYPE.BATCH_EXEC_ROLLBACK.value());
        knLogger.info(methodname, "OperationType ", serviceJob, "Rollback", isRollback);
        //Get mdn list
        int corpid = batchDto.getDefaultSubsProfile().getPamAccId();
        List<String> mdnList = batchDto.getMdnList();
        try {
            persisterTxn.open();

            //Create the Subscribers for the generated MDNs
            knLogger.debug(methodname, "mdnList ", KnGDPRTemplate.mdnList(mdnList));
            KnXDMPAMSubsProfInfoDTO defaultProfile = batchDto.getDefaultSubsProfile();
            knLogger.debug(methodname, "defaultProfile ", defaultProfile);
            KnXDMPAMAccInfoDTO pamAccountInfo = new KnXDMPAMAccInfoDTO();
            defaultProfile.setMdns(mdnList);
            long newCycleMdn = batchInfo.getNewCycleMdn();
            long endMdn = batchInfo.getEndMdn();
            knLogger.debug(methodname, "newCycleMdn  ", newCycleMdn);
            knLogger.debug(methodname, "endMdn  ", endMdn);
            /*
             * Profile will be updated based on whether the status is REACTIVATE or SUSPEND
             * If the batch is to REACTIVATE, set profile auth status as REACTIVATE
             * Same for SUSPEND
             */
            if (serviceJob == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_REACTIVATE.value()||serviceJob == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_REACTIVATE_ROLLBACK.value())
                defaultProfile.setServiceAuthStatus(KnBulkFwConstants.SERVICEAUTH_STATUS.REACTIVATE.value());
            else if (serviceJob == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_SUSPEND.value()||serviceJob == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_SUSPEND_ROLLBACK.value())
                defaultProfile.setServiceAuthStatus(KnBulkFwConstants.SERVICEAUTH_STATUS.SUSPEND.value());
            pamAccountInfo.setProfileDetails(defaultProfile);

            // Mediator call to change status
            knLogger.info(methodname, "Calling change ServiceAuth status  from ",
                    batchInfo.getCycleMdn(), " bulkorder ", batchInfo.getBulkOrderId());
            responseDTO = bulkMediator.changeServiceAuthStatus(pamAccountInfo, persisterTxn);
            /*
             * Check whether the response is null or failure
    		 * If the job is rollback, the previous action will be retried based on the configured retry count and retry interval .
    		 * If the job is not a rollback one, a new rollback job will be initiated.
    		 */
            if (responseDTO == null || responseDTO.getResponseStatus() == MEDIATOR_RESP_STATUS.FAILURE.value()) {

                if (isRollback) {
                    boolean retry = false;
                    int retryCount = 0;
                    //Loop for retry
                    do {
                        responseDTO = bulkMediator.changeServiceAuthStatus(pamAccountInfo, persisterTxn);
                        knLogger.debug(methodname, "Rollback job retry,  Change subs status response  ", responseDTO);
                        //Check whether retry is success.
                        if (responseDTO == null || responseDTO.getResponseStatus() == MEDIATOR_RESP_STATUS.FAILURE.value()) {
                            //Retry failed. Increment the retry counter, wait and retry.
                            retry = true;
                            retryCount++;
                            Thread.sleep(KnBulkProvConstants.RETRY_INTERVAL);
                        } else    //Retry succeeded. Set retry as false to exit the loop
                            retry = false;
                    } while (retry && retryCount < KnBulkProvConstants.RETRY_COUNT);

                    //Retry for rollback job failed for all attempts. Throw exception to bulk status to CRASHED
                    if (retry)
                        throw new Exception("Rollback job failed for " + mdnList);
                } else { //Normal job
                    //Received failed response. Start rollback
                    persisterTxn.rollback();
                    knLogger.debug(methodname, "Failed changeServiceAuthStatus, Initiating rollback  ");
                    persisterTxn = KnPersisterTxn.getPersisterTxn();
                    persisterTxn.open();
                    jobStatusObserver.addRollbackJob(batchInfo, responseDTO, pamAccountInfo, corpid, persisterTxn);
                    persisterTxn.save();
                }
                knLogger.debug(methodname, "changeServiceAuthStatus response ", responseDTO);
            } else {
                if (newCycleMdn >= endMdn) {
                    //final batch executed -
                    //Update batch & bulkorder tables status as COMPLETED. Also update response time and response DTO
                    bulkOrderInfo = new KnBulkDTO();
                    bulkOrderInfo.setBulkOrderId(batchInfo.getBulkOrderId());
                    bulkOrderInfo.setStatus(STATUS.COMPLETED.value());
                    bulkOrderInfo.setCompletionTime(System.currentTimeMillis());
                    if (!isRollback) {
                        bulkOrderInfo.setBulkOrderObj(responseDTO);
                        bulkOrderInfo.setBulkOrderRespObj(responseDTO);
                    }
                    jobStatusObserver.deleteJob(corpid);
                    batchInfo.setStatus(STATUS.COMPLETED.value());
                    provBatchInfoDAO.delete(batchInfo.getBulkOrderId(), persisterTxn);
                    knLogger.debug(methodname, "Updated bulkorder table ", bulkOrderInfo);
                    boInfoDao.updateJobStatus(bulkOrderInfo, persisterTxn);
                    knLogger.info(methodname, "Completed CHANGE_SUB_STATUS Job for bulkorder ", bulkOrderInfo.getBulkOrderId());
                }
            }

            persisterTxn.save();
        } catch (Exception e) {
            //initiate rollback Job
            persisterTxn.rollback();
            knLogger.error(methodname, "Exception", e);
            //Rollback job failed
            //set the bulkorder status as CRASHED
            if (isRollback) {
                knLogger.error(methodname, "Exception in rollback job for bulkorder ", batchInfo.getBulkOrderId(), "for mdns ", KnGDPRTemplate.mdnList(mdnList));
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                bulkOrderInfo = new KnBulkDTO();
                jobStatusObserver.deleteJob(corpid);
                batchInfo.setStatus(STATUS.FAILED.value());
                provBatchInfoDAO.update(batchInfo, persisterTxn);
                bulkOrderInfo.setStatus(STATUS.CRASHED.value());
                bulkOrderInfo.setCompletionTime(System.currentTimeMillis());
                boInfoDao.updateJobStatus(bulkOrderInfo, persisterTxn);
                persisterTxn.save();
            } else {    //Initiate rollback
                knLogger.error(methodname, "Exception, Initiating rollback  ", e);
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                KnXDMPAMSubsProfInfoDTO defaultProfile = batchDto.getDefaultSubsProfile();
                KnXDMPAMAccInfoDTO pamAccountInfo = new KnXDMPAMAccInfoDTO();
                pamAccountInfo.setProfileDetails(defaultProfile);
                jobStatusObserver.addRollbackJob(batchInfo, responseDTO, pamAccountInfo, defaultProfile.getPamAccId(), persisterTxn);
                persisterTxn.save();
                knLogger.error(methodname, "changeServiceAuthStatus failed for  ", KnGDPRTemplate.mdnList(mdnList), " Rollback triggered...");
                throw e;
            }
        }
        knLogger.info(methodname, "EXIT ");
    }
}
