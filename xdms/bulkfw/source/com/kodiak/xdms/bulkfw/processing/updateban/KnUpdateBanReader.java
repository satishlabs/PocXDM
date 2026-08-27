/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.processing.updateban;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnUpdateBanReader.java
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
import com.kodiak.common.commdto.request.KnXDMPAMSubsProfInfoDTO;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.bulkfw.KnJobStatusObserver;
import com.kodiak.xdms.bulkfw.dao.KnXDMProvBatchInfoDAO;
import com.kodiak.xdms.bulkfw.dto.KnBatchDTO;
import com.kodiak.xdms.bulkfw.dto.KnProvBatchProfileDTO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;
import com.kodiak.xdms.bulkfw.util.KnPAMBatchUtils;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.NonTransientResourceException;
import org.springframework.batch.infrastructure.item.ParseException;
import org.springframework.batch.infrastructure.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;

public class KnUpdateBanReader extends ItemListenerSupport implements ItemReader {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUpdateBanReader.class);

    private KnXDMProvBatchInfoDAO provBatchInfoDao;
    private int jobExecutionId;
    private KnXDMPAMSubsProfInfoDTO defaultSubsProfile = null;
    private KnPAMBatchUtils bulkProvUtil;
    private KnJobStatusObserver knJobStatusObserver;

    public KnUpdateBanReader() {
        knLogger.debug("KnUpdateBanReader constructor", "Constructor ");
        knJobStatusObserver = new KnJobStatusObserver();
        String pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        bulkProvUtil = new KnPAMBatchUtils(pttServerId);
        provBatchInfoDao = new KnXDMProvBatchInfoDAO(pttServerId);

    }

    @Value("#{jobParameters['jobExecutionId']}")
    public void setJobExecutionId(int jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        String methodname = "read()";
        knLogger.info(methodname, "ENTRY ");
        KnProvBatchProfileDTO batchProfileDTO = new KnProvBatchProfileDTO();
        KnPersisterTxn persisterTxn = KnPersisterTxn.getPersisterTxn();

        try {
            persisterTxn.open();
            KnBatchDTO batchExeInfo = provBatchInfoDao.getCurrentBatchExecutionInfo(jobExecutionId, persisterTxn);
            knLogger.debug(methodname, "batchExeInfo ", batchExeInfo);
            if (checkJoComplete(batchExeInfo, jobExecutionId)) {
                persisterTxn.save();
                return null;
            }
            knLogger.debug(methodname, "Current batch ", batchExeInfo);
                if (defaultSubsProfile == null) {
                    defaultSubsProfile = bulkProvUtil.getDefaultSubsProfile(batchExeInfo.getBulkOrderId(), false, persisterTxn);
                    knLogger.debug(methodname, "New defaultSubsProfile ", defaultSubsProfile);
                }
                batchProfileDTO.setDefaultSubsProfile(defaultSubsProfile);
            batchProfileDTO.setBatchInfoDTO(batchExeInfo);
            persisterTxn.save();

        } catch (Exception e) {
            knLogger.error(methodname, "Exception occured ", e);
            persisterTxn.rollback();
            throw e;
        }
        knLogger.info(methodname, "EXIT ");
        return batchProfileDTO;
    }

    private boolean checkJoComplete(KnBatchDTO batchExeInfo, int jobExecutionid) {
        String methodname = "checkJoComplete()";
        if (batchExeInfo == null) {
            knLogger.info(methodname, "Job completed. Return null for job exceId ", jobExecutionid);
            knJobStatusObserver.updateJobStatus(KnBulkFwConstants.STATUS.COMPLETED.value(), jobExecutionid);
            return true;
        }
        return false;
    }
}
