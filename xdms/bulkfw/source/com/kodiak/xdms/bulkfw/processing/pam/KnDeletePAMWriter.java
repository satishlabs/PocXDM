/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.processing.pam;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnDeletePAMWriter.java
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
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.kuidgenerator.KnKUIDGenerator;
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

import java.util.ArrayList;
import java.util.List;

public class KnDeletePAMWriter implements ItemWriter<KnProvBatchProfileDTO> {
    private static final KnLogger knLogger = KnLogger.getLogger(KnDeletePAMWriter.class);
    private KnXDMProvBatchInfoDAO provBatchInfoDAO;
    private KnXDMBulkMediator bulkMediator;
    private KnXDMBulkOrderInfoDAO boInfoDao;
    private KnJobStatusObserver knJobStatusObserver;

    public KnDeletePAMWriter() {
        KnBulkFwInitializer bulkFwConfig = KnBulkFwInitializer.getInstance();
        String pttServerId = bulkFwConfig.getPttServerId();
        provBatchInfoDAO = new KnXDMProvBatchInfoDAO(pttServerId);
        boInfoDao = new KnXDMBulkOrderInfoDAO(pttServerId);
        bulkMediator = KnXDMBulkMediator.getInstance();
        knJobStatusObserver = KnJobStatusObserver.getInstance();
    }

    @Override
    public void write(Chunk<? extends KnProvBatchProfileDTO> chunk) throws Exception {
        List<? extends KnProvBatchProfileDTO> batchIpDtos = chunk.getItems();
        String methodname = "write(List)";
        knLogger.info(methodname, "ENTRY ");
        KnPersisterTxn persisterTxn = null;
        KnProvBatchProfileDTO batchDto = batchIpDtos.get(0);
        knLogger.debug(methodname, "batchDto ", batchDto);
        KnBatchDTO batchInfo = batchDto.getBatchInfoDTO();
        KnBulkDTO bulkOrderInfo;
        IXDMResponseDTO responseDTO = null;
        boolean isRollback = (batchInfo.getBatchExeType() == KnBulkProvConstants.BATCH_EXEC_TYPE.BATCH_EXEC_ROLLBACK.value());
        List<String> mdnList = batchDto.getMdnList();
        boolean isLastMDN = false;
        try {
            //Delete the Subscribers for the generated MDNs
            knLogger.debug(methodname, "mdnList ", KnGDPRTemplate.mdnList(mdnList));
            KnXDMPAMSubsProfInfoDTO defaultProfile = batchDto.getDefaultSubsProfile();
            knLogger.debug(methodname, "defaultProfile ", defaultProfile);
            long newCycleMdn = batchInfo.getNewCycleMdn();
            long endMdn = batchInfo.getEndMdn();
            knLogger.info(methodname, "newCycleMdn  ", newCycleMdn, " endMdn  ", endMdn, "rollback ", isRollback);
            KnXDMPAMAccInfoDTO pamAccountInfo;
            List<String> delList = new ArrayList<>();
            bulkOrderInfo = new KnBulkDTO();
            bulkOrderInfo.setBulkOrderId(batchInfo.getBulkOrderId());
            batchInfo.setStatus(STATUS.IN_PROGRESS.value());
            //Call delete for mdns one by one
            for (String mdn : mdnList) {
                //Set cycleMDN for each mdn. This is useful in case of rollback, to start from the failed mdn
                batchInfo.setNewCycleMdn(Long.parseLong(mdn.trim()));
                pamAccountInfo = new KnXDMPAMAccInfoDTO();
                delList.clear();
                delList.add(mdn);
                knLogger.debug(methodname, "Deleting...  ", KnGDPRTemplate.mdn(mdn));
                defaultProfile.setMdns(delList);
                pamAccountInfo.setProfileDetails(defaultProfile);
                pamAccountInfo.setHierarchyType(defaultProfile.getHierarchyType());

                // check for last MDN for delete pam profile purpose.
                int bulkOrderType = batchDto.getBatchInfoDTO().getOpertaionType();
                knLogger.info(methodname, "Recieved operation type--- ", bulkOrderType);
                if (mdnList.lastIndexOf(mdn) == (mdnList.size() - 1)) {
                    if (bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DELETE.value() ||
                            bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_CREATE.value()||bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_ROLLBACK.value()) {
                        knLogger.debug(methodname, "checking weather it is create or upgare ROLLBACK - isUpgare-", pamAccountInfo.getProfileDetails().isUpgrade());
                        if (!pamAccountInfo.getProfileDetails().isUpgrade()) {
                            knLogger.info(methodname, "deleting last MDN", KnGDPRTemplate.mdn(mdn));
                            isLastMDN = true;
                        }
                    }
                }

                //Delete subscriber
                responseDTO = tryDeleteSubscriber(mdn, pamAccountInfo, batchInfo, isRollback, isLastMDN);
                //None of the retry attempts succeeded. Mark the job as CRESHED in bulkorder table
                if (responseDTO == null || responseDTO.getResponseStatus() == MEDIATOR_RESP_STATUS.FAILURE.value()) {
                    knLogger.error(methodname, "Roll-back job failed for bulkorder ", batchInfo.getBulkOrderId(),
                            "for mdns ", KnGDPRTemplate.mdnList(mdnList), "at ", KnGDPRTemplate.mdn(mdn));
                    //Mark job as CRASHED
                    markDeleteAsCrashed(bulkOrderInfo, batchInfo,defaultProfile.getPamAccId());
                    return;
                }

            }
            //final batch executed - Since DELETE is done in a single batch, when all mdns are deleted one by one,
            //it is considered as completion of batch job
                /*
                 * To update bulkorder table
                 * If a normal job is COMPLETED, the bulkorder will be updated as COMPLETED
                 * If a rollback job is COMPLETED, the corresponding bulkorder will be updated as FAILED
                 */
            if (isRollback){
                bulkOrderInfo.setStatus(STATUS.FAILED.value());
            }
            else {
                bulkOrderInfo.setStatus(STATUS.COMPLETED.value());
                if (responseDTO != null) {
                    bulkOrderInfo.setBulkOrderRespObj(responseDTO);
                } else
                    knLogger.error(methodname, "NULL response for a COMPLETED batch job");
            }
            knLogger.debug(methodname, "Delete job completed, Setting status as complete  ", bulkOrderInfo, " Rollback - ", isRollback);

            bulkOrderInfo.setCompletionTime(System.currentTimeMillis());

            batchInfo.setStatus(STATUS.COMPLETED.value());
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            provBatchInfoDAO.delete(batchInfo.getBulkOrderId(), persisterTxn);
            knJobStatusObserver.deleteJob(defaultProfile.getPamAccId());
            boInfoDao.updateJobStatus(bulkOrderInfo, persisterTxn);
            knLogger.debug(methodname, "Updated bulkorder table ", bulkOrderInfo);
            knLogger.info(methodname, "Completed DELETE_SUBSCRIBER job for bulkorder  ", bulkOrderInfo.getBulkOrderId());
            persisterTxn.save();
        } catch (Exception e) {
            assert persisterTxn != null;
            persisterTxn.rollback();
            knLogger.error(methodname, "Exception while updating final status of batch job", batchInfo.getBulkOrderId(), e);
        }
        knLogger.info(methodname, "EXIT ");
    }

    /**
     * Method performing delete and retry in case of failure
     *
     * @param pamAccountInfo
     * @param batchInfo
     * @return
     * @throws KnException
     */
    private IXDMResponseDTO tryDeleteSubscriber(String mdn, KnXDMPAMAccInfoDTO pamAccountInfo, KnBatchDTO batchInfo, boolean rollbackJob, boolean isLastMDN) throws KnException {
        String methodname = "tryDeleteSubscriber(KnXDMPAMAccInfoDTO, KnProvBatchJobDTO, boolean, boolean)";
        knLogger.info(methodname, "Entry ", KnGDPRTemplate.mdn(mdn));
        KnPersisterTxn persisterTxn;
        IXDMResponseDTO responseDTO;
        int retryCount = 0;
        do {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            if (rollbackJob) {
                knLogger.debug(methodname, "Rollback Delete Job");
                responseDTO = bulkMediator.deleteSubscribersForRollback(pamAccountInfo, isLastMDN, persisterTxn);
            } else {
                knLogger.debug(methodname, "Normal Delete Job");
                responseDTO = bulkMediator.deleteSubscriber(pamAccountInfo, isLastMDN, persisterTxn);
            }

            knLogger.debug(methodname, "Response ", responseDTO, retryCount);
            if (responseDTO == null || responseDTO.getResponseStatus() == MEDIATOR_RESP_STATUS.FAILURE.value()) {
                //Increment retry count
                retryCount++;
                persisterTxn.rollback();
                //Wait for RETRY_INTERVAL before next retry
                try {
                    Thread.sleep(KnBulkProvConstants.RETRY_INTERVAL);
                } catch (InterruptedException e) {
                    knLogger.error(methodname, "InterruptedException from thread");
                    throw new KnException(KnBulkFwConstants.ErrorCodes.INTERNAL_SERVER_ERROR, "Internal Server Error");
                }
            } else {
                provBatchInfoDAO.update(batchInfo, persisterTxn);
                knLogger.debug(methodname, "Marking Pseudo Mdn as unused", KnGDPRTemplate.mdn(mdn));
                List<String> pseudomdn = new ArrayList<>();
                pseudomdn.add(mdn);
                KnKUIDGenerator.getInstance().deleteKUIDPool(pseudomdn,persisterTxn);
                persisterTxn.save();
                break;
            }
        } while (retryCount <= KnBulkProvConstants.RETRY_COUNT);
        return responseDTO;
    }

    /**
     * Mark delete operation status as crashed.
     * Batch Info table gets updated as FAILED and Bulk order info table is updated as CRASHED
     */
    private void markDeleteAsCrashed(KnBulkDTO bulkOrderInfo, KnBatchDTO batchInfo, int corpid) {
        String methodname = "markDeleteAsCrashed(KnBulkOrderInfoDTO, KnProvBatchJobDTO)";
        knLogger.info(methodname, "Entry ");
        KnPersisterTxn persisterTxn = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            batchInfo.setStatus(STATUS.FAILED.value());
            provBatchInfoDAO.update(batchInfo, persisterTxn);
            knJobStatusObserver.deleteJob(corpid);
            bulkOrderInfo.setStatus(STATUS.CRASHED.value());
            bulkOrderInfo.setCompletionTime(System.currentTimeMillis());
            boInfoDao.updateJobStatus(bulkOrderInfo, persisterTxn);
            persisterTxn.save();
            knLogger.debug(methodname, "Marked delete job as CRASHED");
        } catch (KnException e) {
            try {
                knLogger.error(methodname, "Exception while marking job as crashed.. Rolling back the transaction", e);
                assert persisterTxn != null;
                persisterTxn.rollback();
            } catch (KnPersistenceException ex) {
                knLogger.error(methodname, "Exception while transaction rollback", e);
            }
        }
    }
}
