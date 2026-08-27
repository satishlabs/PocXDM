/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.logger.KnLogger;
import com.kodiak.frameworks.jobscheduler.IJobSchedulerIntf;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;

import java.util.ArrayList;

/**
 * *****************************************************************************
 * File name:   KnCacheCleanUpLoader
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar           11/09/12        7.2.4
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
 * *******************************************************************************
 */

public class KnCacheCleanUpLoader {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCacheCleanUpLoader.class);
    private final String className = KnCacheCleanUpLoader.class.getName();
    private static KnCacheCleanUpLoader cacheCleanUpLoader = null;
    private static IJobSchedulerIntf schedulerIntf;

    private KnCacheCleanUpLoader() {
        schedulerIntf = KnJobSchedulerImpl.getInstance();
        assignJobs();
    }

    public static synchronized KnCacheCleanUpLoader getInstance() {

        if (cacheCleanUpLoader == null) {
            cacheCleanUpLoader = new KnCacheCleanUpLoader();
        }
        return cacheCleanUpLoader;
    }


    private void assignJobs() {
        String methodName = "assignJobs()";
        knLogger.info( methodName, "ENTRY: assignJobs - ");
        try {
            ArrayList<KnCacheCleanUp> jobsList = new ArrayList<KnCacheCleanUp>();
            KnCacheCleanUp cacheCleanUpJob = new KnCacheCleanUp();
            jobsList.add(cacheCleanUpJob);
            String cronExpression = cacheCleanUpJob.getCronExpression();
            schedulerIntf.addRamCronJob(jobsList, cronExpression);
        } catch (KnJobSchedulerException e) {
            knLogger.error( methodName, "Failed to assign clean up cache Job - ");
            knLogger.error( methodName, e);
        } catch (Exception e) {
            knLogger.error( methodName, "Exception: Failed to assign clean up cache Job - " + e);
            knLogger.error( methodName, e);
        }
    }
}
