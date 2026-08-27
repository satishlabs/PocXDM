/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p>
 * File name:  KnFeatureBitJob.java
 * Subsystem:  PoC
 * <p>
 * Name                        		 Date                    	 Release
 * --------------------    		 ----------------       	 ------------------
 * Sravan kumar Kuppala          20/10/20, 02:15 PM                  13.1
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 *
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2022 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 */

package com.kodiak.xdms.auditjobs.asyncfw;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featuresetupgrade.util.KnDowngradeFs;
import com.kodiak.utilities.featuresetupgrade.util.KnDowngradeFsConfig;
import com.kodiak.utilities.featuresetupgrade.util.KnUpgardeFS;
import com.kodiak.utilities.featuresetupgrade.util.KnUpgardeFSConfig;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.auditjobs.featurbit.KnXdmFeatureBitDowngradeAudit;
import com.kodiak.xdms.auditjobs.featurbit.KnXDMFeatureBitUpdateAudit;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.resources.KnConstants;

import java.util.*;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;


public class KnFeatureBitJob implements Runnable, IStatusMgrNotifyIntf {


    private static final KnLogger knLogger = KnLogger.getLogger(KnFeatureBitJob.class);

    private static KnFeatureBitJob instance;
    private static KnGeneralCacheUtil generalCacheUtil;
    public static Map<String, KnAsyncJobDTO> downgradeBit;
    public static Boolean downgradeBitFlag = false;
    private KnStatusMgrConstants.CARD_STATES currentRedState = KnStatusMgrConstants.CARD_STATES.UNKNOWN;
    static final String SRC_FEATURE_REL_VERSION = "SRC_FEATURE_REL_VERSION";

    @Override
    public void run() {
        knLogger.info("KnFeatureBitJob()", "xdm current state - ", getCurrentRedundancyStatus());
        if (getCurrentRedundancyStatus() == KnStatusMgrConstants.CARD_STATES.ACTIVE) {
            triggerJob();
        }
    }

    private boolean triggerJob() {
        String methodName = "triggerJob";
        String version = "17";
        boolean result = false;
        try {
            downgradeBit = generalCacheUtil.getJobStatusByOperationIdAndStatus
                    (KnConstants.UPM_OPERATION_TYPE.FEATURE_BIT_DOWNGRADE.Value(), KnConstants.UPM_JOB_STATUS.NEW.Value());
            knLogger.info(methodName, "GG Job for rollback flag", downgradeBitFlag);

            KnUpgardeFSConfig.initialise();
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = KnGenInfoUtil.getInstance().retrieveMSSvcsCommonConfig(clusterId);
            String xdmSourceVersion = microServicesParamNameValueMap.get(SRC_FEATURE_REL_VERSION);
            knLogger.info(methodName, "ENTRY KnFeatureBitJob xdmSourceVersion ", xdmSourceVersion);
            if (xdmSourceVersion != null && !xdmSourceVersion.isEmpty()) {
                version = xdmSourceVersion;
            }
            Thread.sleep(30000);
            knLogger.info(methodName, " KnFeatureBitJob version ", version);
            if (Float.parseFloat(KnUpgardeFSConfig.masterFeatureSetDetails.getFsCurrentVersion()) > Float.parseFloat(version)) {
                knLogger.info(methodName, " ENTRY Upgrade triggered ");
                KnUpgardeFS.getInstance();
                KnXDMFeatureBitUpdateAudit.getInstance();
                knLogger.info(methodName, "Upgrade triggered ");
            } else if (!downgradeBitFlag && downgradeBit.entrySet().iterator().next().getValue().getCorpId() != 0 && getCurrentRedundancyStatus() == KnStatusMgrConstants.CARD_STATES.ACTIVE && Float.parseFloat(KnUpgardeFSConfig.masterFeatureSetDetails.getFsCurrentVersion()) < Float.parseFloat(version)) {
                downgradeBitFlag = true;
                knLogger.info(methodName, "ENTRY KnFeatureBitJob rollback ", downgradeBitFlag);
                try {
                    KnDowngradeFs.getInstance();
                    KnXdmFeatureBitDowngradeAudit.getInstance();
                    generalCacheUtil.delete();
                    generalCacheUtil.insert(version, KnDowngradeFsConfig.getFsCurrentVersion(), 1);
                    Collection<String> txnIds = new ArrayList<>();
                    String txnId = downgradeBit.entrySet().iterator().next().getValue().getTxnId();
                    txnIds.add(txnId);
                    generalCacheUtil.deleteAsyncJob(txnIds);
                    //generalCacheUtil.delete();
                    generalCacheUtil.update(2);
                    //generalCacheUtil.insert(version, KnDowngardeFSConfig.getFsCurrentVersion(), 2);
                    knLogger.info(methodName, "Rollback done successfully ");
                    return true;
                } catch (KnDAOException ex) {
                    generalCacheUtil.update(3);
                    knLogger.info(methodName, "Exception occurred while downgrading ", ex.getMessage());
                }

            }
        } catch (Throwable e) {
            knLogger.error(methodName, "exception while fetching jobs", e);
            try {
                generalCacheUtil.insert(version, KnDowngradeFsConfig.getFsCurrentVersion(), 3);
            } catch (KnDAOException ex) {
                knLogger.info(methodName, "Exception occurred  ", ex);
            }
        }

        return result;
    }


    public static KnFeatureBitJob getInstance() {
        if (instance == null) {
            instance = new KnFeatureBitJob();
            //registering for Redundancy Notify Status
            List<IStatusMgrNotifyIntf> list = new ArrayList<>();
            list.add(instance);
            KnStatusManagerClient.registerObjects(list);
            knLogger.debug("getInstance()", "registered for Status..");
            generalCacheUtil = KnGeneralCacheUtil.getInstance();
        }

        return instance;
    }

    @Override
    public void notify(KnStatusMgrConstants.CARD_STATES previousState, KnStatusMgrConstants.CARD_STATES currentState) {
        String methodName = "notify(KnStatusMgrConstants.CARD_STATES, KnStatusMgrConstants.CARD_STATES)";
        knLogger.info(methodName, "notify from Status Mgr [Prev State - ", previousState,
                "; Current State - ", currentState + "]");

        this.currentRedState = currentState;

        knLogger.debug(methodName, "Current State ", currentState);
    }

    /**
     * method which gives redundancy state as TRUE if ACTIVE else false
     *
     * @return boolean
     */
    public KnStatusMgrConstants.CARD_STATES getCurrentRedundancyStatus() {
        String methodName = "getCurrentRedundancyStatus";
        knLogger.debug(methodName, "Current Card Redundancy Status ", this.currentRedState);
        return this.currentRedState;
    }

}
