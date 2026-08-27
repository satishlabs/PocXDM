/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.validator;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnPAMBulkValidator.java
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
import com.kodiak.xdms.bulkfw.KnJobStatusObserver;
import com.kodiak.xdms.bulkfw.dto.KnBulkDTO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;

import java.util.*;

public class KnPAMBulkValidator {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPAMBulkValidator.class);

    private static KnPAMBulkValidator instance;

    public static KnPAMBulkValidator getInstance() {
        if (instance == null)
            instance = new KnPAMBulkValidator();
        return instance;
    }

    /**
     * Method validates the parallel execution of PAM jobs and filters the number of delete jobs executin
     * @param jobList
     * @return
     */
    public List<KnBulkDTO> validatePam(List<KnBulkDTO> jobList) {
        String methodName = "validate(List<KnBulkOrderInfoDTO>,KnPersisterTxn)";
        knLogger.debug(methodName, "joblist ", jobList);
        Map<Integer, KnBulkDTO> runningJobs = KnJobStatusObserver.getRunningJobs();
        List<Integer> runningDeleteJobs = filterDeleteJobsFromRunningJobs(runningJobs);
        Iterator<KnBulkDTO> iterator = jobList.iterator();
        int deleteJobsCount = 0;
        while (iterator.hasNext()) {
            KnBulkDTO bulkOrderInfo = iterator.next();
            Integer pamAccId = bulkOrderInfo.getCorpId();
            int bulkOrderId = bulkOrderInfo.getBulkOrderId();
            int bulkOrderType = bulkOrderInfo.getBulkOrderType();
            boolean isRemoved = false;
            if (runningJobs.containsKey(pamAccId)) {
                knLogger.info(methodName, "Removing bulkOrderId", bulkOrderId, "from list, for existing pamAccId", pamAccId);
                iterator.remove();
                isRemoved = true;
            }
            if (bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DELETE.value() || bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DOWNGRADE.value()) {
                if ((runningDeleteJobs.size() + deleteJobsCount >= KnBulkFwConstants.MAX_ALLOWED_DELETE_BATCHES) && !isRemoved) {
                    knLogger.info(methodName, "Removing DELETE Job with bulkorderId", bulkOrderId, " as already ", runningDeleteJobs, " are running ");
                    iterator.remove();
                } else {
                    deleteJobsCount++;
                }
            }
        }
        knLogger.info(methodName, "Filtered PAm jobs from joblist ", jobList);
        return jobList;
    }

    private List<Integer> filterDeleteJobsFromRunningJobs(Map<Integer, KnBulkDTO> runningJobs) {
        String methodName = "filterDeleteJobsFromRunningJobs";
        knLogger.info(methodName, "Running Jobs ", runningJobs);
        List<Integer> deleteJobs = new ArrayList<>();
        if (!runningJobs.isEmpty())
            for (Map.Entry<Integer, KnBulkDTO> entry : runningJobs.entrySet()) {
                KnBulkDTO bulkOrderInfo = entry.getValue();
                if (bulkOrderInfo.getBulkOrderType() == KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DELETE.value()) {
                    deleteJobs.add(entry.getKey());
                }
            }
        knLogger.info(methodName, "Filtered Delete jobs from joblist ", deleteJobs);
        return deleteJobs;
    }
}