/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.processing.updateban;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnUpdateBanWriter.java
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
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants.MEDIATOR_RESP_STATUS;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants.STATUS;
import com.kodiak.xdms.bulkfw.util.KnPseudoMdnHandler;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

import java.util.List;

public class KnUpdateBanWriter implements ItemWriter<KnProvBatchProfileDTO> {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUpdateBanWriter.class);
    private KnXDMProvBatchInfoDAO provBatchInfoDAO;
    private KnXDMBulkMediator bulkMediator;
    private KnXDMBulkOrderInfoDAO boInfoDao;
    private KnJobStatusObserver jobStatusObserver;

    public KnUpdateBanWriter() {
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
        knLogger.debug(methodname, "batchDto ", batchDto);
        KnBatchDTO batchInfo = batchDto.getBatchInfoDTO();
        int bulkOrderId = batchInfo.getBulkOrderId();
        List<String> mdnList = batchDto.getMdnList();
        boolean isFirstBatch = false;
        boolean isLastBatch = false;
        int corpid = batchDto.getDefaultSubsProfile().getPamAccId();
        KnPseudoMdnHandler handler = KnPseudoMdnHandler.getInstance();
        try {
            persisterTxn.open();

            //UpdateBan for the generated MDNs
            knLogger.debug(methodname, "mdnList ", KnGDPRTemplate.mdnList(mdnList));
            KnXDMPAMSubsProfInfoDTO subsProvInfoDTO = batchDto.getDefaultSubsProfile();
            KnXDMPAMAccInfoDTO accInfoDTO = new KnXDMPAMAccInfoDTO();
            accInfoDTO.setProfileDetails(subsProvInfoDTO);
            accInfoDTO.setHierarchyType(subsProvInfoDTO.getHierarchyType());
            knLogger.debug(methodname, "defaultProfile ", subsProvInfoDTO);
            subsProvInfoDTO.setMdns(mdnList);
            knLogger.info(methodname, "Calling update Ban  for mdns from ", batchInfo.getCycleMdn()," bulkorder ", bulkOrderId);
            // after first batch creation we need to create a PAM profile.
            if (batchInfo.getCycleMdn() == 0) {
                knLogger.debug(methodname, "First batch is being executed.");
                isFirstBatch = true;
            }

            long newCycleMdn = batchInfo.getNewCycleMdn();
            long endMdn = batchInfo.getEndMdn();

            knLogger.debug(methodname, "batch size-", batchInfo.getBatchSize(), "current MDN-", KnGDPRTemplate.mdn(mdnList.get(batchInfo.getBatchSize()-1)), "endMDN-", endMdn);
            if (mdnList.get(batchInfo.getBatchSize()-1).equals(String.valueOf(endMdn))) {
                knLogger.debug(methodname, "Last batch is being executed.");
                isLastBatch = true;
            }


            //Mediator call to update Ban subscribers
            responseDTO = bulkMediator.updateHierarchy(accInfoDTO, isFirstBatch, isLastBatch, persisterTxn);
            knLogger.debug(methodname, "Update Hierarchy response ", responseDTO);
            knLogger.debug(methodname, "newCycleMdn  ", newCycleMdn, "endMdn  ", endMdn);
            if (responseDTO != null && responseDTO.getResponseStatus() == MEDIATOR_RESP_STATUS.SUCCESS.value()) {
                if (isLastBatch) {
                    //final batch executed -
                    //Update batch & bulkorder tables
                    knLogger.info(methodname, "Final batch for this job", batchInfo);
                    KnBulkDTO bulkOrderInfo = new KnBulkDTO();
                    bulkOrderInfo.setBulkOrderId(batchInfo.getBulkOrderId());
                    bulkOrderInfo.setStatus(STATUS.COMPLETED.value());
                    bulkOrderInfo.setCompletionTime(System.currentTimeMillis());
                    bulkOrderInfo.setBulkOrderObj(responseDTO);
                    bulkOrderInfo.setBulkOrderRespObj(responseDTO);
                    batchInfo.setStatus(STATUS.COMPLETED.value());
                    provBatchInfoDAO.delete(batchInfo.getBulkOrderId(), persisterTxn);
                    jobStatusObserver.deleteJob(corpid);
                    boInfoDao.updateJobStatus(bulkOrderInfo, persisterTxn);
                    handler.deletePseudoList(bulkOrderId);
                    knLogger.debug(methodname, "Updated bulkorder table ", bulkOrderInfo);
                    knLogger.info(methodname, "Completed UPDATE BAN  Job for bulkorder ", bulkOrderInfo.getBulkOrderId());
                }
                persisterTxn.save();
            } else {
                //Received failed response. Start rollback
                persisterTxn.rollback();
                knLogger.debug(methodname, "Failed To update Ban, Initiating rollback  ");
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                jobStatusObserver.addRollbackJob(batchInfo, responseDTO,accInfoDTO,subsProvInfoDTO.getPamAccId(), persisterTxn);
                persisterTxn.save();
                handler.deletePseudoList(bulkOrderId);
            }

        } catch (Exception e) {
            //initiate rollback Job
            persisterTxn.rollback();
            knLogger.error(methodname, "Exception, Initiating rollback  ", e);
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            KnXDMPAMSubsProfInfoDTO defaultProfile = batchDto.getDefaultSubsProfile();
            KnXDMPAMAccInfoDTO pamAccountInfo = new KnXDMPAMAccInfoDTO();
            pamAccountInfo.setProfileDetails(defaultProfile);
            jobStatusObserver.addRollbackJob(batchInfo, responseDTO, pamAccountInfo, corpid, persisterTxn);
            persisterTxn.save();
            handler.deletePseudoList(bulkOrderId);
            knLogger.debug(methodname, "Update Ban failed for  ", KnGDPRTemplate.mdnList(mdnList), " Rollback triggered...");
            throw e;
        }
        knLogger.info(methodname, "EXIT ");
    }

}


