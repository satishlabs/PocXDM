/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.processing.pam;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCreatePAMWriter.java
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
import com.kodiak.utilities.generatealarmutil.KnAlarmConstants;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;
import com.kodiak.utilities.kuidgenerator.KnKUIDConstants;
import com.kodiak.xdms.bulkfw.KnBulkFwInitializer;
import com.kodiak.xdms.bulkfw.KnJobStatusObserver;
import com.kodiak.xdms.bulkfw.KnXDMBulkMediator;
import com.kodiak.xdms.bulkfw.dao.KnSpringBatchDAO;
import com.kodiak.xdms.bulkfw.dao.KnXDMBulkOrderInfoDAO;
import com.kodiak.xdms.bulkfw.dao.KnXDMProvBatchInfoDAO;
import com.kodiak.xdms.bulkfw.dto.KnBatchDTO;
import com.kodiak.xdms.bulkfw.dto.KnBulkDTO;
import com.kodiak.xdms.bulkfw.dto.KnProvBatchProfileDTO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants.MEDIATOR_RESP_STATUS;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants.STATUS;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwUtil;
import com.kodiak.xdms.bulkfw.util.KnPAMBatchUtils;
import com.kodiak.xdms.bulkfw.util.KnPseudoMdnHandler;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

import java.util.List;

public class KnCreatePAMWriter implements ItemWriter<KnProvBatchProfileDTO> {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCreatePAMWriter.class);

    private KnXDMProvBatchInfoDAO provBatchInfoDAO;
    private KnXDMBulkOrderInfoDAO boInfoDao;
    private KnXDMBulkMediator bulkMediator;
    private KnJobStatusObserver jobStatusObserver;
    private KnPAMBatchUtils batchProcessUtils;
    private KnSpringBatchDAO springBatchDao;
    private KnBulkFwUtil bulkFwUtil = null;

    public KnCreatePAMWriter() {
        KnBulkFwInitializer bulkFwConfig = KnBulkFwInitializer.getInstance();
        String pttServerId = bulkFwConfig.getPttServerId();
        provBatchInfoDAO = new KnXDMProvBatchInfoDAO(pttServerId);
        boInfoDao = new KnXDMBulkOrderInfoDAO(pttServerId);
        bulkMediator = KnXDMBulkMediator.getInstance();
        batchProcessUtils = new KnPAMBatchUtils(pttServerId);
        jobStatusObserver = KnJobStatusObserver.getInstance();
        springBatchDao = new KnSpringBatchDAO(pttServerId);
        bulkFwUtil = KnBulkFwUtil.getInstance();
    }


    @Override
    public void write(Chunk<? extends KnProvBatchProfileDTO> chunk) throws Exception {
        List<? extends KnProvBatchProfileDTO> batchIpDtos = chunk.getItems();
        final String methodname = "write(List)";
        knLogger.info(methodname, "ENTRY ");
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();
        KnProvBatchProfileDTO batchDto = batchIpDtos.get(0);
        IXDMResponseDTO responseDTO = null;
        knLogger.debug(methodname, "batchDto ", batchDto);
        KnBatchDTO batchInfo = batchDto.getBatchInfoDTO();
        int bulkOrderId = batchInfo.getBulkOrderId();
        List<String> mdnList = batchDto.getMdnList();
        boolean isFirstBatch = false;
        int corpid = batchDto.getDefaultSubsProfile().getPamAccId();
        KnPseudoMdnHandler handler = KnPseudoMdnHandler.getInstance();
        try {
            persisterTxn.open();

            //Create the Subscribers for the generated MDNs
            knLogger.debug(methodname, "mdnList ", KnGDPRTemplate.mdnList(mdnList), "batch exec type",batchInfo.getBatchExeType());

            KnXDMPAMSubsProfInfoDTO defaultProfile = batchDto.getDefaultSubsProfile();
            knLogger.debug(methodname, "defaultProfile ", defaultProfile);
            defaultProfile.setMdns(mdnList);
            KnXDMPAMAccInfoDTO pamAccountInfo = new KnXDMPAMAccInfoDTO();
            pamAccountInfo.setProfileDetails(defaultProfile);
            pamAccountInfo.setHierarchyType(defaultProfile.getHierarchyType());
            knLogger.info(methodname, "Calling create subscriber  for mdns from ", batchInfo.getCycleMdn()," bulkorder ", bulkOrderId);
            // after first batch creation we need to create a PAM profile.
            if (batchInfo.getCycleMdn() == 0) {
                knLogger.debug(methodname, "First batch is being executed.");
                isFirstBatch = true;
            }

            //Mediator call to create subscribers
            responseDTO = bulkMediator.createSubscribers(pamAccountInfo, isFirstBatch, persisterTxn);
            knLogger.debug(methodname, "CreateSubscriber response ", responseDTO);
            long newCycleMdn = batchInfo.getNewCycleMdn();
            long endMdn = batchInfo.getEndMdn();
            knLogger.debug(methodname, "newCycleMdn  ", newCycleMdn);
            knLogger.debug(methodname, "endMdn  ", endMdn);
            if (responseDTO != null && responseDTO.getResponseStatus() == MEDIATOR_RESP_STATUS.SUCCESS.value()) {
                if (newCycleMdn >= endMdn) {
                    //final batch executed -
                    //Update batch & bulkorder tables
                    knLogger.info(methodname, "Final batch for this job");
                    KnBulkDTO bulkOrderInfo = new KnBulkDTO();
                    bulkOrderInfo.setBulkOrderId(batchInfo.getBulkOrderId());
                    bulkOrderInfo.setStatus(STATUS.COMPLETED.value());
                    bulkOrderInfo.setCompletionTime(System.currentTimeMillis());
                    bulkOrderInfo.setBulkOrderObj(responseDTO);
                    bulkOrderInfo.setBulkOrderRespObj(responseDTO);
                    batchInfo.setStatus(STATUS.COMPLETED.value());
                    provBatchInfoDAO.delete(batchInfo.getBulkOrderId(), persisterTxn);
                    knLogger.info(methodname, "Job completed for JobExecID", batchInfo.getBatchExeId());
                    jobStatusObserver.deleteJob(corpid);
                    boInfoDao.updateJobStatus(bulkOrderInfo, persisterTxn);
                    knLogger.debug(methodname, "Updated bulkorder table ", bulkOrderInfo);
                    knLogger.info(methodname, "Completed CREATE_SUBSCRIBER Job for bulkorder ", bulkOrderInfo.getBulkOrderId());
                    handler.deletePseudoList(bulkOrderId);
                }
                persisterTxn.save();
            } else {
                //Received failed response. Start rollback
                persisterTxn.rollback();
                knLogger.info(methodname, "Failed create subscriber, Initiating rollback  ");
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                jobStatusObserver.deleteJob(corpid);
                //reverting back remaining  allocated(3) MDNs to Deallocated state(0) against this PamAccID
                knLogger.debug(methodname, "Marked pseudomdns as unused  ", KnGDPRTemplate.mdnList(mdnList));
                provBatchInfoDAO.delete(batchInfo.getBulkOrderId(), persisterTxn);
                jobStatusObserver.addRollbackJob(batchInfo, responseDTO, pamAccountInfo, defaultProfile.getPamAccId(), persisterTxn);
                persisterTxn.save();
                handler.deletePseudoList(bulkOrderId);
                //Check whether unique constraint error response received. If yes, raise an alarm
                if (responseDTO != null && responseDTO.getResponseCode().contains("SP12051")) {
                    knLogger.info(methodname, "Mismatch observed in Pseudo Pool and PocSubsInfo for mdns  ", KnGDPRTemplate.mdnList(mdnList));
                    KnAlarmGeneratorUtil.generateAlarm(KnAlarmConstants.ALARM_PSEUDO_NUM_POOL_DB_INCONSISTENCY_OBSERVED, KnAlarmConstants.SEVERITY_MAJOR,
                    		KnKUIDConstants.ALARM_MOCLASSTYPE, "PseudoMDNGenerator");
                }
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
            jobStatusObserver.addRollbackJob(batchInfo, responseDTO, pamAccountInfo ,corpid, persisterTxn);
            persisterTxn.save();
            handler.deletePseudoList(bulkOrderId);
            knLogger.debug(methodname, "Subscriber create failed for  ", KnGDPRTemplate.mdnList(mdnList), " Rollback triggered...");
            throw e;
        }
        knLogger.info(methodname, "EXIT ");
    }
}



