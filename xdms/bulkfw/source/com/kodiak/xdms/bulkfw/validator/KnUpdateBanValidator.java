/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.validator;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnUpdateBanValidator.java
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
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.bulkfw.KnBulkFwInitializer;
import com.kodiak.xdms.bulkfw.KnJobStatusObserver;
import com.kodiak.xdms.bulkfw.dto.KnBulkDTO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;

import java.util.*;

public class KnUpdateBanValidator {

    private static final KnLogger knLogger = KnLogger.getLogger(KnUpdateBanValidator.class);

    private static KnUpdateBanValidator instance;

    public static KnUpdateBanValidator getInstance() {
        if (instance == null)
            instance = new KnUpdateBanValidator();
        return instance;
    }

    public List<KnBulkDTO> validate(List<KnBulkDTO> jobList) {
        String methodName = "validate(List<KnBulkOrderInfoDTO>,KnPersisterTxn)";
        KnBulkFwInitializer bulkFwConfig = KnBulkFwInitializer.getInstance();
        Map<Integer, KnBulkDTO> runningJobs = KnJobStatusObserver.getRunningJobs();
        knLogger.debug(methodName, "joblist ", jobList);
        List<Integer> runningUpdateJobs = filterUpdateJobsFromRunningJobs(runningJobs);
        Iterator<KnBulkDTO> iterator = jobList.iterator();
        int updateBanJobsCount = 0;
        while (iterator.hasNext()) {
            KnBulkDTO bulkOrderInfo = iterator.next();
            Integer banId = bulkOrderInfo.getCorpId();
            int bulkOrderId = bulkOrderInfo.getBulkOrderId();
            int bulkOrderType = bulkOrderInfo.getBulkOrderType();
            if (runningJobs.containsKey(banId)) {
                knLogger.debug(methodName, "Removing bulkOrderId", bulkOrderId, "from list, for existing Internal BanId", banId);
                iterator.remove();
            } 
            if (bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.UPDATE_BAN.value()) {
                if (runningUpdateJobs.size() + updateBanJobsCount >= bulkFwConfig.getUpdateBanSimulReqSize()) {
                    knLogger.debug(methodName, "Removing UPDATE Job with bulkorderId", bulkOrderId, " as already ", runningUpdateJobs, " are running ");
                    iterator.remove();
                }else{
                    updateBanJobsCount++;
                }
            }
        }
        return jobList;
    }

    private List<Integer> filterUpdateJobsFromRunningJobs(Map<Integer, KnBulkDTO> runningJobs) {
        String methodName = "filterUpdateJobsFromRunningJobs()";
        List<Integer> updateJobs = new ArrayList<>();
        if (!runningJobs.isEmpty())
            for (Map.Entry<Integer, KnBulkDTO> entry : runningJobs.entrySet()) {
                KnBulkDTO bulkOrderInfo = entry.getValue();
                if (bulkOrderInfo.getBulkOrderType() == KnBulkFwConstants.BULK_ORDER_TYPE.UPDATE_BAN.value()) {
                    updateJobs.add(entry.getKey());
                }

            }
        knLogger.debug(methodName, "Filtered UPDATE jobs from joblist ", updateJobs);
        return updateJobs;
    }
}
