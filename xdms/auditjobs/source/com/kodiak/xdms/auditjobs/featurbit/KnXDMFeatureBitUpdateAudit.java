/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/*
 *  ***********************************************************************
 *  File name:  KnXDMFeatureBitUpdateAudit.java
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

import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.jobscheduler.IJobSchedulerIntf;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.auditjobs.KnXDMAuditJobs;

import java.util.ArrayList;


public class KnXDMFeatureBitUpdateAudit {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMFeatureBitUpdateAudit.class);
    private static KnXDMFeatureBitUpdateAudit instance = new KnXDMFeatureBitUpdateAudit();;
    private IJobSchedulerIntf jobScheduler;


    private KnXDMFeatureBitUpdateAudit() {
        init();
    }

    /**
     * singleton initialization of the Class
     *
     * @return
     */
    public static synchronized KnXDMFeatureBitUpdateAudit getInstance() {
        return instance;
    }

    private void init() {
        String methodName = "initializeI()";
        knLogger.info(methodName, "Initilization of KnXDMFeatureBitUpdateAuditJob");
        try {
            jobScheduler = KnJobSchedulerImpl.getInstance();
            jobScheduler.addRamCronJob(new ArrayList<KnAbstractJob>() {{
                    add(new KnUpdateFeatureBitJob());
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
        if(cron==null){
            cron= "5";
        }
        String cronExpression = "0/"+cron+" * * * * ? *";
        knLogger.info(methodName, "EXIT: Cron Expression - ", cronExpression);
        return cronExpression;

    }

}
