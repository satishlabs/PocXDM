/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     28/5/14         7.7.0
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
package com.kodiak.xdms.notificationmgr.resources;

import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.beans.KnSEHNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnSEHNotifier;

import java.util.List;

public class KnSEHNotificationJob extends KnAbstractJob {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSEHNotificationJob.class);

    private static final long serialVersionUID = 7526471155622676269L;
    private List<KnSEHNotifyDTO> sehNotifyDTOList;
    private KnSEHNotifier sehNotifier;
    private String defaultJobName = "SEHNotificationJob";

    public KnSEHNotificationJob() {
        setJobName(defaultJobName);
        sehNotifier = KnSEHNotifier.getInstance();
    }

    /**
     * This method sets the notifications to be processed by the Job
     *
     * @param sehNotifyDTOList the notification objects
     */
    public void setNotifications(List<KnSEHNotifyDTO> sehNotifyDTOList) {
        this.sehNotifyDTOList = sehNotifyDTOList;
    }


    /**
     * The executeTask method that gets executed by the Scheduler when scheduler schedules the job
     *
     * @return boolean flag indicating job execution is successful or not
     * @throws com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException exception
     */
    public boolean executeTask() throws KnJobSchedulerException {
        String methodName = "executeTask()";
        //MaxNotifications per job is not configured. Will Process the notifications in single Job
        knLogger.info( methodName, "Processing notifications inside Job :" + getJobName());
        boolean isSuccess = sehNotifier.generateSEHNotification(sehNotifyDTOList);
        knLogger.info( methodName, "Job executed. Job status : " + isSuccess);
        return isSuccess;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(500);
        sb.append("Notification List - ").append(sehNotifyDTOList);
        return sb.toString();
    }
}