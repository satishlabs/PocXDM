/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p>
 * File name:  KnDowngradeFeatureBitJob.java
 * Subsystem:  PoC
 * <p>
 * Name                        		 Date                    	 Release
 * --------------------    		 ----------------       	 ------------------
 * Sravan kumar Kuppala          20/10/22, 03:15 PM                  13.1
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

package com.kodiak.xdms.auditjobs.featurbit;

import com.kodiak.common.commdto.common.KnCorpFeatureBitInfoDTO;
import com.kodiak.common.commdto.common.KnSubsFeatureBitInfoDTO;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featuresetupgrade.util.KnDowngradeFs;
import com.kodiak.utilities.featuresetupgrade.util.KnDowngradeFsConfig;

import java.util.List;

public class KnDowngradeFeatureBitJob extends KnAbstractJob {

    private static final KnLogger knLogger = KnLogger.getLogger(KnDowngradeFeatureBitJob.class);

    private static KnFeatureBitJobDbUtil jobDbUtil = new KnFeatureBitJobDbUtil();
    public static String oldfeatureBitversion = null;

    @Override
    public boolean executeTask() throws KnJobSchedulerException {
        String methodName = "executeFeatureBitUpdateTask()";
        try {
            knLogger.info(methodName, "Fetching XDM xdm license version");
            String featureBitVersion = String.valueOf(KnDowngradeFsConfig.getFsCurrentVersion());
            //Fetch the un-upgraded corporates
            List<KnCorpFeatureBitInfoDTO> corpFeatureBitInfoDTOList = jobDbUtil.getUnUpgradedCorps(featureBitVersion);
            if (corpFeatureBitInfoDTOList.size() > 0) {
                knLogger.debug(methodName, "Calling feature bit update for corps ", corpFeatureBitInfoDTOList);
                oldfeatureBitversion = corpFeatureBitInfoDTOList.get(0).getFeatureReleaseVersion();
                KnDowngradeFs.getInstance().updateCorpFeaturebits(corpFeatureBitInfoDTOList);
                knLogger.debug(methodName, "feature bit updated for corps ", corpFeatureBitInfoDTOList);
                return false;

            }
            //Fetch the un-upgraded billing numbers
            List<KnSubsFeatureBitInfoDTO> pamIdsFeatureBitInfoDTOList = jobDbUtil.getUnUpgradedPamIds(featureBitVersion);
            //List<KnSubsFeatureBitInfoDTO> pamIdsFeatureBitInfoDTOList = new ArrayList<>();
            if (pamIdsFeatureBitInfoDTOList.size() > 0) {
                knLogger.debug(methodName, "Calling feature bit update for pamids ", pamIdsFeatureBitInfoDTOList);
                KnDowngradeFs.getInstance().updatePamFeaturebits(pamIdsFeatureBitInfoDTOList);
                knLogger.debug(methodName, "feature bit updated for pamids ", pamIdsFeatureBitInfoDTOList);
                return false;
            }

            //Fetch the un-upgraded mdns
            List<KnSubsFeatureBitInfoDTO> subsFeatureBitInfoDTOList = jobDbUtil.getUnUpgradedMdns(featureBitVersion);
            knLogger.info(methodName, "Calling feature bit update for mdns ", subsFeatureBitInfoDTOList.size());
            if (subsFeatureBitInfoDTOList.size() > 0) {
                knLogger.debug(methodName, "Calling feature bit update for mdns ", subsFeatureBitInfoDTOList);
                KnDowngradeFs.getInstance().updateSubscriberFeaturebits(subsFeatureBitInfoDTOList);
                knLogger.debug(methodName, "feature bit updated for mdns ", subsFeatureBitInfoDTOList);
            }
            if (corpFeatureBitInfoDTOList.size() == 0 && pamIdsFeatureBitInfoDTOList.size() == 0 && subsFeatureBitInfoDTOList.size() == 0) {
                knLogger.info(methodName, "All the MDN Feature bits are updated to current release and deleting the current job");
                //jobDbUtil.updateFeatureBitVersionInCms(featureBitVersion);
                KnGeneralUtil.setIsFeatureBitUpdateComplete(true);
                return true;
            }
        } catch (Exception e) {
            knLogger.error(methodName, "failed to execute jpb in current trigger", e);
        }
        return false;
    }
}
