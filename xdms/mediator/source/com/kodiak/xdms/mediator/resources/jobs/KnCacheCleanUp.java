/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * *****************************************************************************
 * File name:   KnCacheCleanUp
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
public class KnCacheCleanUp extends KnAbstractJob {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCacheCleanUp.class);
    private static final String ACTIVE_RELEASE_DIR = System.getProperty("activeRelDir");
    private static String watcherDir = "watch";
    private static String triggerFile = "triggerfile.properties";
    private File file;

    public KnCacheCleanUp() {
        knLogger.debug("constructor", "getting instance of Config manager");
        file = new File(ACTIVE_RELEASE_DIR + File.separator + watcherDir + File.separator + triggerFile);

    }

    @Override
    public boolean executeTask() throws KnJobSchedulerException {
        String methodName = "executeTask()";
        knLogger.info(methodName, "Task to clean up cache ");
        try {
            // creates a FileWriter Object
            FileWriter writer = new FileWriter(file);
            // Writes the content to the file
            for (KnConstants.CACHE_TABLE_LIST table : KnConstants.CACHE_TABLE_LIST.values()) {
                writer.write(table.value() + "\n");
            }
            writer.flush();
            writer.close();
            knLogger.info(methodName, "table list overridden");

        } catch (IOException e) {
            knLogger.error(methodName, "IO exception occurred", e);
        }
        return false;
    }


    /**
     * method to generate the Cron Expression for this Job
     *
     * @return String cron Expression
     */
    public String getCronExpression() {
        String methodName = "getCronExpression()";
        knLogger.info(methodName, "ENTRY: Get Cron Expression - ");
        int cacheClearTime = 30; //if not config in DB take default 30 mins
        try {
            //  try {
            /*    Collection<String> keyList = new ArrayList<String>();
           keyList.add(KnConstants.CACHE_CLEANUP_TIMER);
           generalUtil = new KnGeneralUtil();
           Map<String, String> configMap = generalUtil.retrieveConfig(keyList);
           if (configMap != null) {
               cacheClearTime = Integer.parseInt(configMap.get(KnConstants.CACHE_CLEANUP_TIMER));
           } */
            cacheClearTime = Integer.parseInt(System.getenv("XDM_DM_CONFIGCACHEREFRESHTIMER"));
        } catch (NumberFormatException ne) {
            knLogger.warn(methodName, "Exception occured while getting refersh timer ", ne);
            knLogger.info(methodName, "Taking default refersh timer as ", cacheClearTime);
        }
        knLogger.debug(methodName, " time for CACHE_CLEANUP_TIMER :", cacheClearTime);
        String cronExpression = "0 */" + cacheClearTime + " * ? * *";
        knLogger.info(methodName, "EXIT: Cron Expression - ", cronExpression);
        return cronExpression;

        /* } catch (KnDAOException e) {
          knLogger.error( methodName, "Exception occurred- " + e);
      }  */
        //return null;
    }
}


