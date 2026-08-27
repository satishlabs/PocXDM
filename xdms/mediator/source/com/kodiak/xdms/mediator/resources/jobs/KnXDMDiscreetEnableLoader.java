/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.frameworks.jobscheduler.IJobSchedulerIntf;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;

import java.util.ArrayList;

public class KnXDMDiscreetEnableLoader {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCacheCleanUpLoader.class);
    private final String className = KnXDMDiscreetEnableLoader.class.getName();
    private static KnXDMDiscreetEnableLoader discreetEnableLoader = null;
    private static IJobSchedulerIntf schedulerIntf;

    private KnXDMDiscreetEnableLoader() {
        schedulerIntf = KnJobSchedulerImpl.getInstance();
        assignJobs();
    }

    public static KnXDMDiscreetEnableLoader getInstance() {
        if (discreetEnableLoader == null) {
            discreetEnableLoader = new KnXDMDiscreetEnableLoader();
        }
        return discreetEnableLoader;
    }

    private void assignJobs() {
        String methodName = "assignJobs()";
        knLogger.info(methodName, "ENTRY: XDMS audit assignJobs - ");
        try {
            ArrayList<KnXDMDiscreetEnableAudit> jobsList = new ArrayList<>();
            KnXDMDiscreetEnableAudit discreetEnableAudit = new KnXDMDiscreetEnableAudit();
            jobsList.add(discreetEnableAudit);
            String cronExpression = discreetEnableAudit.getCronExpression();
            schedulerIntf.addRamCronJob(jobsList, cronExpression);
        } catch (KnJobSchedulerException e) {
            knLogger.error(methodName, "Failed to assign XDMS audit discreet enable Job - ");
            knLogger.error(methodName, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception: Failed to assign XDMS audit discreet enable Job - ",  e);
            knLogger.error(methodName, e);

        }
    }
}
