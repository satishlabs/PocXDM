/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/*
 *  ***********************************************************************
 *  File name:  KnUpdateFeatureBitJob.java
 *  Subsystem:  PoCXDM
 *
 *    Name                 	    Date         	                  Release
 *    --------------------   -----------------------  -------------------------
 *    Chandrashekar HS          23/01/20, 1:23 PM                    10.0
 *
 *  Copyright (c) 2019 Kodiak, A Motorola Solutions Company
 *  9th floor, MFar, Manayata Tech Park,
 *  Greenheart Phase IV,Nagawara
 *  Bangalore - 560 045
 *  www.motorolasolutions.com
 *  All Rights Reserved.
 *
 * This software is the confidential and proprietary information of KodiakMotorola Solutions, Inc.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of the license agreement you entered into with Kodiak Motorola Solutions.
 *   ***********************************************************************
 */

package com.kodiak.xdms.auditjobs.featurbit;

import com.kodiak.common.commdto.common.KnCorpFeatureBitInfoDTO;
import com.kodiak.common.commdto.common.KnSubsFeatureBitInfoDTO;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featuresetupgrade.util.KnUpgardeFS;
import com.kodiak.utilities.featuresetupgrade.util.KnUpgardeFSConfig;

import java.util.List;

public class KnUpdateFeatureBitJob extends KnAbstractJob {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUpdateFeatureBitJob.class);

    private static KnFeatureBitJobDbUtil jobDbUtil = new KnFeatureBitJobDbUtil();

    @Override
    public boolean executeTask() throws KnJobSchedulerException {
        String methodName = "executeFeatureBitUpdateTask()";
        try {
            knLogger.info(methodName, "Fetching XDM xdm license version");
            String featureBitVersion = String.valueOf(KnUpgardeFSConfig.getFsCurrentVersion());
            //Fetch the un-upgraded corporates
            List<KnCorpFeatureBitInfoDTO> corpFeatureBitInfoDTOList = jobDbUtil.getUnUpgradedCorps(featureBitVersion);
            knLogger.info(methodName, "Calling feature bit update for corps size :", corpFeatureBitInfoDTOList.size());
            if (corpFeatureBitInfoDTOList.size() > 0) {
                knLogger.debug(methodName, "Calling feature bit update for corps ", corpFeatureBitInfoDTOList);
                KnUpgardeFS.getInstance().updateCorpFeaturebits(corpFeatureBitInfoDTOList);
                knLogger.debug(methodName, "feature bit updated for corps ", corpFeatureBitInfoDTOList);
                return false;
            }
            //Fetch the un-upgraded billing numbers
            List<KnSubsFeatureBitInfoDTO> pamIdsFeatureBitInfoDTOList = jobDbUtil.getUnUpgradedPamIds(featureBitVersion);
            //List<KnSubsFeatureBitInfoDTO> pamIdsFeatureBitInfoDTOList = new ArrayList<>();
            knLogger.info(methodName, "Calling feature bit update for pamids size :", pamIdsFeatureBitInfoDTOList.size());
            if (pamIdsFeatureBitInfoDTOList.size() > 0) {
                knLogger.debug(methodName, "Calling feature bit update for pamids ", pamIdsFeatureBitInfoDTOList);
                KnUpgardeFS.getInstance().updatePamFeaturebits(pamIdsFeatureBitInfoDTOList);
                knLogger.debug(methodName, "feature bit updated for pamids ", pamIdsFeatureBitInfoDTOList);
                return false;
            }

            //Fetch the un-upgraded mdns
            List<KnSubsFeatureBitInfoDTO> subsFeatureBitInfoDTOList = jobDbUtil.getUnUpgradedMdns(featureBitVersion);
            knLogger.info(methodName, "Calling feature bit update for mdns size :", subsFeatureBitInfoDTOList.size());
            if (subsFeatureBitInfoDTOList.size() > 0) {
                knLogger.debug(methodName, "Calling feature bit update for mdns ", subsFeatureBitInfoDTOList);
                KnUpgardeFS.getInstance().updateSubscriberFeaturebits(subsFeatureBitInfoDTOList);
                knLogger.debug(methodName, "feature bit updated for mdns ", subsFeatureBitInfoDTOList);
            }
            if (corpFeatureBitInfoDTOList.size() == 0 && pamIdsFeatureBitInfoDTOList.size() == 0 && subsFeatureBitInfoDTOList.size() == 0) {
                knLogger.info(methodName, "All the MDN Feature bits are updated to current release");
                knLogger.info(methodName, "deleting the current job");

                // XDM-10556: Update feature bit version in CMS
                //jobDbUtil.updateFeatureBitVersionInCms(featureBitVersion);

                KnGeneralUtil.setIsFeatureBitUpdateComplete(true);
                return true;
            }
        } catch (Exception e) {
            knLogger.error(methodName, "failed to execute job in current trigger", e);
        }
        return false;
    }
}
