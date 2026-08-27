/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkFwInitializer.java
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
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.springcontainer.KnSpringContextProvider;
import com.kodiak.xdms.bulkfw.controller.KnBulkFwController;
import com.kodiak.xdms.bulkfw.controller.KnPseudoMDNAudit;
import com.kodiak.xdms.bulkfw.dao.KnSpringBatchDAO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;
import org.springframework.context.ApplicationContext;

import java.util.*;

public final class KnBulkFwInitializer {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkFwInitializer.class);
    private static KnBulkFwInitializer instance;
    private String pttServerId;
    private int bulkProvBatchSize;
    private int updateBanBatchSize;
    private int updateBanSimulReqSize;
    public static int upperWaterMark;
    private KnSpringBatchDAO springBatchDAO;

    private KnBulkFwInitializer() {
        KnPseudoMDNAudit pseudoAudit;
        Thread pseudoAuditThread;
        try {
            this.pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            initialize();
            ApplicationContext context = KnSpringContextProvider.getApplicationContext();
            knLogger.debug("KnBulkFwInitializer", "Spring context - ", context);
            KnBulkFwController knBulkFwController = (KnBulkFwController) context.getBean("knBulkFwController");
            knLogger.debug("KnBulkFwInitializer", "knBulkFwController", knBulkFwController, "registering table to pocxla");
            knBulkFwController.registerDynamicClient();
            pseudoAudit = new KnPseudoMDNAudit();
            pseudoAuditThread = new Thread(pseudoAudit);
            pseudoAuditThread.setName("SpringAuditThread");
            pseudoAuditThread.start();
            knLogger.debug("initialize", "Initialized SpringAuditThread");
            springBatchDAO = new KnSpringBatchDAO(pttServerId);
            springBatchDAO.cleanupSpringBatchInfo();
        } catch (Exception e) {
            knLogger.error("KnBulkFwInitializer", "Failed to get register observers ", e);
        }

    }

    public static synchronized KnBulkFwInitializer getInstance() {
        if (instance == null) {
            instance = new KnBulkFwInitializer();
        }
        return instance;
    }

    /**
     * Initialize bulkframework and pseudo mdn audit
     */
    private void initialize() {
        knLogger.debug("initialize", "Starting the Bulk FW");
        try {
            this.getConfigParams();

            knLogger.debug("initialize", "Initialized Bulk Framework");
        } catch (KnException e) {
            knLogger.error("initialize", "Failed initialize bulk framework Extended", e);
        }
        knLogger.debug("initialize", "Started the BULK FW Extended Initializer");
    }

    private void getConfigParams() throws KnException {
        String methodName = "getConfigParams()";
        knLogger.info(methodName, "Loading config Info");
        KnGeneralUtil generalUtil = new KnGeneralUtil();
        Collection<String> keyList = new ArrayList<>();
        keyList.add(KnBulkFwConstants.BULK_PROV_BATCH_SIZE);
        keyList.add(KnBulkFwConstants.UPDATE_BAN_BATCH_SIZE);
        keyList.add(KnBulkFwConstants.UPDATE_BAN_SIMUL_REQ_SIZE);
        keyList.add(KnBulkFwConstants.PSEUDOMDN_HIGH_WATERMARK);
        Map<String, String> configMap = generalUtil.retrieveConfig(keyList);
        knLogger.debug(methodName, "Batch Params ", configMap);
        if (configMap != null) {
            this.bulkProvBatchSize = Integer.parseInt(configMap.get(KnBulkFwConstants.BULK_PROV_BATCH_SIZE));
            this.updateBanBatchSize = Integer.parseInt(configMap.get(KnBulkFwConstants.UPDATE_BAN_BATCH_SIZE));
            this.updateBanSimulReqSize = Integer.parseInt(configMap.get(KnBulkFwConstants.UPDATE_BAN_SIMUL_REQ_SIZE));
            upperWaterMark = Integer.parseInt(configMap.get(KnBulkFwConstants.PSEUDOMDN_HIGH_WATERMARK));
        } else {
            throw new KnException(KnBulkFwConstants.ErrorCodes.SUBSYSTEM_NOT_INITIALIZED,
                    "Subsystem not initialzed properly. Missing BULK_PROV_BATCH_SIZE configuration ");
        }
    }

    public String getPttServerId() {
        return this.pttServerId;
    }

    public int getBulkProvBatchSize() {
        return this.bulkProvBatchSize;
    }

    public int getUpdateBanBatchSize() {
        return this.updateBanBatchSize;
    }

    public int getUpdateBanSimulReqSize() {
        return this.updateBanSimulReqSize;
    }

    public int getUpperWaterMark() {
        return this.upperWaterMark;
    }
}
