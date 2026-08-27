/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.processing.pam;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnPAMProcessor.java
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
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.bulkfw.dao.KnXDMBulkOrderInfoDAO;
import com.kodiak.xdms.bulkfw.dao.KnXDMProvBatchInfoDAO;
import com.kodiak.xdms.bulkfw.dto.KnBatchDTO;
import com.kodiak.xdms.bulkfw.dto.KnBulkDTO;
import com.kodiak.xdms.bulkfw.dto.KnProvBatchProfileDTO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants.STATUS;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class KnPAMProcessor implements ItemProcessor<KnProvBatchProfileDTO, KnProvBatchProfileDTO> {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPAMProcessor.class);

    private KnXDMProvBatchInfoDAO provBatchInfoDao;
    private KnXDMBulkOrderInfoDAO boInfoDao;
    //KnBulkFwInitializer bulkFwConfig;
    private String pttServerId;

    public KnPAMProcessor() {
        try {
            pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            provBatchInfoDao = new KnXDMProvBatchInfoDAO(pttServerId);
            boInfoDao = new KnXDMBulkOrderInfoDAO(pttServerId);
        } catch (Exception e) {
            knLogger.error("KnPAMProcessor()", "Exception Occured ", e);
        }
    }

    public KnProvBatchProfileDTO process(KnProvBatchProfileDTO batchProfileDTO) throws Exception {
        String methodname = "process(KnProvBatchProfileDTO)";
        knLogger.info(methodname, "ENTRY ");
        KnBatchDTO batchInfoDto = batchProfileDTO.getBatchInfoDTO();
        KnPersisterTxn persisterTxn = null;
        KnProvBatchProfileDTO respDto = new KnProvBatchProfileDTO();
        respDto.setBatchInfoDTO(batchInfoDto);
        int pamAccId;
        knLogger.info(methodname, "Operation type ",batchInfoDto.getBatchExeType());
        //Should process only if the job is INPROGRESS or NOT STARTED
        if (STATUS.IN_PROGRESS.value() == batchInfoDto.getStatus() || STATUS.NOT_STARTED.value() == batchInfoDto.getStatus()) {
            long startMdn = batchInfoDto.getStartMdn();
            long endMdn = batchInfoDto.getEndMdn();
            long cycleMdn = batchInfoDto.getCycleMdn();
            int bulkOrderType = batchInfoDto.getOpertaionType();
            int bulkOrderId = batchInfoDto.getBulkOrderId();
            int batchSize = batchInfoDto.getBatchSize();
            int batchExecType = batchInfoDto.getBatchExeType();
            knLogger.debug(methodname, "startMdn ", startMdn, ",endMdn ", endMdn, ",cycleMdn ", cycleMdn);
            //Process happens only if cycleMDN is less than or equal to endMDN. cycleMDN>endMDN means the job is completed.
            if (cycleMdn <= endMdn) {
                List<String> mdnList = new ArrayList<>(batchSize);
                long cycleEndMdn = 0L;

                Map<Long, List<String>> processValue = null;
                if (bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_CREATE.value()) {
                    KnCreatePAMProcessor knCreatePAMProcessor = new KnCreatePAMProcessor();
                    processValue = knCreatePAMProcessor.createProcess(startMdn, endMdn, cycleMdn, bulkOrderId, batchSize);

                } else {
                    try {
                        persisterTxn = KnPersisterTxn.getPersisterTxn();
                        persisterTxn.open();
                        pamAccId = boInfoDao.getPamAccountId(bulkOrderId, persisterTxn);
                        knLogger.info(methodname, "pamAccId from Processor ", pamAccId, " with bulkorder ", bulkOrderId);
                        
                        // these two parameters will be required in case of upgrade rollback
                    	boolean isUpgrade = batchProfileDTO.getDefaultSubsProfile().isUpgrade();
                    	long bulkInsertionTime = 0;
                    	if (isUpgrade) {
                    		KnBulkDTO bulkDTO = boInfoDao.getBulkOrderDetails(bulkOrderId, persisterTxn);
                    		bulkInsertionTime = bulkDTO.getInsertionTime();                        		
                    	}
                        
                        persisterTxn.save();

                        if (bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DELETE.value()
                                || bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DOWNGRADE.value()||bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_ROLLBACK.value()) {
                        	
                        	KnDeletePAMProcessor knDeletePAMProcessor = new KnDeletePAMProcessor();
                            processValue = knDeletePAMProcessor.deleteProcess(pamAccId, cycleMdn, endMdn, batchExecType, batchSize, bulkOrderType, pttServerId, isUpgrade, bulkInsertionTime);
                        } else {
                            KnChangeAuthStatusPAMProcessor knChangeAuthStatusPAMProcessor = new KnChangeAuthStatusPAMProcessor();
                            processValue = knChangeAuthStatusPAMProcessor.changeStatusProcess(pamAccId, cycleMdn, endMdn, batchSize);
                        }
                        knLogger.debug(methodname, "MDN list from processor for pamAccid  ", pamAccId, " are  ", processValue);
                    } catch (Exception e) {
                        knLogger.error(methodname, "Exception", e);
                        if (persisterTxn != null) {
                            persisterTxn.rollback();
                        }
                        throw e;
                    }
                    knLogger.debug(methodname, "MDN list from processor for pamAccid  ", pamAccId, " are  ", processValue);
                }
                for (Entry<Long, List<String>> key : processValue.entrySet()) {
                    mdnList = key.getValue();
                    cycleEndMdn = key.getKey();
                }
                knLogger.debug(methodname, "mdnList ", KnGDPRTemplate.mdnList(mdnList));
                //Set mdnList to DTO so that it will be available for Writer.
                respDto.setMdnList(mdnList);
                respDto.setDefaultSubsProfile(batchProfileDTO.getDefaultSubsProfile());
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                try {
                    persisterTxn.open();
                    //This update of batch table is mainly required for updating cycleMDN.
                    batchInfoDto.setStatus(STATUS.IN_PROGRESS.value());
                    //New cycleMDN will be the cycleMDN for next batch
                    knLogger.debug(methodname, "CycleMdn for next batch", cycleEndMdn);
                    batchInfoDto.setNewCycleMdn(cycleEndMdn);
                    provBatchInfoDao.update(batchInfoDto, persisterTxn);
                    persisterTxn.save();
                } catch (Exception e) {
                    knLogger.error(methodname, "Exception", e);
                    persisterTxn.rollback();
                    throw e;
                }
                knLogger.debug(methodname, "Updated cycle.... ");
            }
        }
        knLogger.info(methodname, "EXIT ");
        return respDto;
    }
}
