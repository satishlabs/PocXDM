/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p>
 * File name:  KnXdmFeatureBitDowngradeAudit.java
 * Subsystem:  PoC
 * <p>
 * Name                        		 Date                    	 Release
 * --------------------    		 ----------------       	 ------------------
 * Sravan kumar Kuppala          20/10/19, 12:15 PM                  13.1
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

import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.jobscheduler.IJobSchedulerIntf;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;

import java.util.ArrayList;


public class KnXdmFeatureBitDowngradeAudit {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXdmFeatureBitDowngradeAudit.class);
    private static KnXdmFeatureBitDowngradeAudit instance = new KnXdmFeatureBitDowngradeAudit();

    private IJobSchedulerIntf jobScheduler;


    private KnXdmFeatureBitDowngradeAudit() {
        init();
    }

    /**
     * singleton initialization of the Class
     *
     * @return
     */
    public static synchronized KnXdmFeatureBitDowngradeAudit getInstance() {
        return instance;
    }

    private void init() {
        String methodName = "initializeI()";
        knLogger.info(methodName, "Initilization of KnXDMFeatureBitUpdateAuditJob");
        try {

            jobScheduler = KnJobSchedulerImpl.getInstance();
            jobScheduler.addRamCronJob(new ArrayList<KnAbstractJob>() {{
                add(new KnDowngradeFeatureBitJob());
            }}, getCronExpression());
            knLogger.info(methodName, "Scheduled UpdateFeatureBitJob");
        } catch (KnJobSchedulerException e) {
            knLogger.error(methodName, "Exception", e);
        }
    }

    private String getCronExpression() {
        String methodName = "getCronExpression()";
        knLogger.info(methodName, "ENTRY: Get Cron Expression - ");
        // timer is set to every 5 seconds
        String cron = KnGeneralUtil.getUpgradeProps().getProperty("CRON");
        if (cron == null) {
            cron = "5";
        }
        String cronExpression = "0/" + cron + " * * * * ? *";
        knLogger.info(methodName, "EXIT: Cron Expression - ", cronExpression);
        return cronExpression;

    }

}
