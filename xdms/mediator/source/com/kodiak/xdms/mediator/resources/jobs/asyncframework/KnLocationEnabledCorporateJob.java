/**
 * ************************************************************************
 * <p>
 * File name:  KnLocationEnabledCorporateJob.java
 * Subsystem:  PoC
 * <p>
 * Name                        		 Date                    	 Release
 * --------------------    		 ----------------       	 ------------------
 * Harssh Malik                     23/10/2024                  13.1
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 *
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2024 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 */
package com.kodiak.xdms.mediator.resources.jobs.asyncframework;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.mediator.impl.KnXDMMediator;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;


import java.util.*;


public class KnLocationEnabledCorporateJob implements IStatusMgrNotifyIntf, Runnable {
    private static final KnLogger knLogger = KnLogger.getLogger(KnLocationEnabledCorporateJob.class);
    private KnGeneralCacheUtil cacheUtil = KnGeneralCacheUtil.getInstance();
    private KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
    private KnStatusMgrConstants.CARD_STATES currentRedState = KnStatusMgrConstants.CARD_STATES.ACTIVE;
    private KnXDMMediator mediator = KnXDMMediator.getInstance();

    public static KnLocationEnabledCorporateJob getInstance() {
        return new KnLocationEnabledCorporateJob();
    }

    private List<Integer> getCorpIdFromGG() throws KnDAOException {
        Map<String, Integer> jobStatusByOperationId = cacheUtil.getJobStatusByOperationId(16);
        List<Integer> corpIds = new ArrayList<>();
        if (jobStatusByOperationId != null && !jobStatusByOperationId.isEmpty()) {
            corpIds = new ArrayList<>(jobStatusByOperationId.values());
        }
        return corpIds;
    }

    public boolean locationEnableJob() throws KnJobSchedulerException {
        boolean isExecuted = false;
        try {
            List<Integer> corpIds = getCorpIdFromGG();
            knLogger.debug("corpIds::", corpIds.size());
            for (int corpId : corpIds) {
                int startIndex = 0;
                int batchSize = 100;
                int counter = 0;
                List<String> baseAndProfileMdnsInBatch;
                do {
                    int endIndex = startIndex + batchSize;
                    baseAndProfileMdnsInBatch = genInfoUtil.getBaseAndProfileMdnsInBatch(corpId, startIndex, endIndex);
                    mediator.processLocationEnabledCorporateJob(baseAndProfileMdnsInBatch);
                    startIndex = endIndex;
                    knLogger.info("Fetched batch of size:", baseAndProfileMdnsInBatch.size());
                    counter++;
                } while (!baseAndProfileMdnsInBatch.isEmpty());
                isExecuted = true;
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            knLogger.error("Exception Occured while running the Location Enable Job - ", e);
        }
        return isExecuted;
    }

    @Override
    public void notify(KnStatusMgrConstants.CARD_STATES previousState, KnStatusMgrConstants.CARD_STATES currentState) {
        String methodName = "notify(KnStatusMgrConstants.CARD_STATES, KnStatusMgrConstants.CARD_STATES) InConsistentUPMRecovery";
        knLogger.info(methodName, "notify from Status Mgr [Prev State - ", previousState,
                "; Current State - ", currentState + "]");

        this.currentRedState = currentState;

        knLogger.debug(methodName, "Current State ", currentState);
    }

    public KnStatusMgrConstants.CARD_STATES getCurrentRedundancyStatus() {
        String methodName = "getCurrentRedundancyStatusInConsistentUPMRecovery";
        knLogger.debug(methodName, "Current Card Redundancy Status ", this.currentRedState);
        return this.currentRedState;
    }

    @Override
    public void run() {
        String methodName = "LocationEnabledJob";
        knLogger.info(methodName, "xdm current state - ", getCurrentRedundancyStatus());
        if (getCurrentRedundancyStatus() == KnStatusMgrConstants.CARD_STATES.ACTIVE) {
            try {
                boolean executed = locationEnableJob();
                if (executed) {
                    cacheUtil.deleteCompletedCorpIdFromGG(getCorpIdFromGG());
                    knLogger.debug("Job Deleted from GG");
                }
            } catch (Throwable e) {
                knLogger.error("Exception Happened:", e);
            }
            knLogger.info(methodName, "Done execution");
        }
    }
}
