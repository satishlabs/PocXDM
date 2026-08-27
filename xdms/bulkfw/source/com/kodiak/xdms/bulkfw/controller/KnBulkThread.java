/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.controller;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkThread.java
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
import com.kodiak.utilities.springcontainer.KnSpringContextProvider;

public class KnBulkThread implements Runnable {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkThread.class);
    private static KnBulkFwController bulkFwController;

    public void run() {
        final String methodName = "run()";
        try {

            bulkFwController = (KnBulkFwController) KnSpringContextProvider.getApplicationContext().getBean("knBulkFwController");
        } catch (Exception e) {
            knLogger.info(methodName, "Exception occurred while getting Controller instance", e);
        }
        knLogger.info(methodName, "Thread started Running..");
        bulkFwController.processNewJobs(KnBulkFwController.jobStore);
        knLogger.info(methodName, "Job Execution completed..");
        knLogger.exit(methodName);
    }
}