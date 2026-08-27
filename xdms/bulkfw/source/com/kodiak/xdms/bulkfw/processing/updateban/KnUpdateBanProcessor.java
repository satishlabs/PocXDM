/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.processing.updateban;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnUpdateBanProcessor.java
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
import com.kodiak.xdms.bulkfw.dao.KnXDMProvBatchInfoDAO;
import com.kodiak.xdms.bulkfw.dto.KnBatchDTO;
import com.kodiak.xdms.bulkfw.dto.KnProvBatchProfileDTO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants.STATUS;
import com.kodiak.xdms.bulkfw.util.KnPseudoMdnHandler;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;


public class KnUpdateBanProcessor implements ItemProcessor<KnProvBatchProfileDTO, KnProvBatchProfileDTO> {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUpdateBanProcessor.class);

    private KnXDMProvBatchInfoDAO provBatchInfoDao;

    public KnUpdateBanProcessor() {
        try {
            String pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            provBatchInfoDao = new KnXDMProvBatchInfoDAO(pttServerId);
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
        int extBanId = 0;
        //Should process only if the job is INPROGRESS or NOT STARTED
        if (STATUS.IN_PROGRESS.value() == batchInfoDto.getStatus() || STATUS.NOT_STARTED.value() == batchInfoDto.getStatus()) {
            long startMdn = batchInfoDto.getStartMdn();
            long endMdn = batchInfoDto.getEndMdn();
            long cycleMdn = batchInfoDto.getCycleMdn();
            int bulkOrderId = batchInfoDto.getBulkOrderId();
            int batchSize = batchInfoDto.getBatchSize();
            knLogger.debug(methodname, "batchSize", batchSize);
            long cycleEndMdn;
            knLogger.info(methodname, "startMdn ", startMdn, ",endMdn ", endMdn, ",cycleMdn ", cycleMdn);
            //Process happens only if cycleMDN is less than or equal to endMDN. cycleMDN>endMDN means the job is completed.
            if (cycleMdn <= endMdn) {
                List<String> mdnList = new ArrayList<>(batchSize);
                cycleEndMdn = 0L;
                try {
                    persisterTxn = KnPersisterTxn.getPersisterTxn();
                    persisterTxn.open();
                    if (cycleMdn == 0)
                        cycleMdn = startMdn;
                    knLogger.debug(methodname, "Cyclemdn", cycleMdn);
                    //Get mdnlist from Pseudo Mdn handler
                    KnPseudoMdnHandler handler = KnPseudoMdnHandler.getInstance();
                    List<String> mdns = handler.getMdnList(bulkOrderId);
                    knLogger.debug(methodname, "Fetched  mdn list", KnGDPRTemplate.mdnList(mdns));
                    int pseudoMdnSize = mdns.size();
                    int startIndex = mdns.indexOf(String.valueOf(cycleMdn));
                    int endIndex = startIndex + batchSize;
                    knLogger.debug(methodname, "startIndex", startIndex, " ,endIndex", endIndex);
                    knLogger.debug(methodname, "mdns.size()", mdns.size(), " ,batchSize", batchSize);
                    if (endIndex >= mdns.size()) {    //Last batch
                        knLogger.debug(methodname, "XXX");
                        endIndex = pseudoMdnSize;
                        cycleEndMdn = endMdn;
                    } else {
                        knLogger.debug(methodname, "cycleEndMdn", cycleEndMdn);
                        knLogger.debug(methodname, "Long.parseLong(mdns.get(endIndex).trim())", Long.parseLong(mdns.get(endIndex).trim()));
                        cycleEndMdn = Long.parseLong(mdns.get(endIndex).trim());
                    }
                    knLogger.debug(methodname, "startIndex", startIndex, "endIndex", endIndex);
                    for (int i = startIndex; i < endIndex; i++) {
                        mdnList.add(mdns.get(i));
                    }

                    persisterTxn.save();
                    knLogger.debug(methodname, "MDN list from processor for extBanId  ", extBanId, " are  ", mdnList);
                } catch (Exception e) {
                    knLogger.error(methodname, "Exception", e);
                    assert persisterTxn != null;
                    persisterTxn.rollback();
                    throw e;
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
