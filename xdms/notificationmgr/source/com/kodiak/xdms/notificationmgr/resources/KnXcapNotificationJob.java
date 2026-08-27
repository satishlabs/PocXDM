/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnNotificationJob.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ajit Kumar          24/Apr/13      7.6
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


import com.kodiak.logger.KnLogger;
import com.kodiak.frameworks.jobscheduler.KnAbstractJob;
import com.kodiak.frameworks.jobscheduler.resources.KnJobSchedulerException;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;

import java.util.Collection;
import java.util.List;

/**
 * This is the Job class implementation defining the job for XCap Notification processing
 */
public class KnXcapNotificationJob extends KnAbstractJob {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXcapNotificationJob.class);

    private static final long serialVersionUID = 7526471155622676269L;

    private final String CLASS = KnXcapNotificationJob.class.getName();

    private List<KnXcapDiffNotifyDTO> xcapDiffNotifyDTOs;
    private KnXcapDiffNotifier xcapDiffNotifier;
    private String defaultJobName = "XcapDiffNotificationJob";

    public KnXcapNotificationJob() {
        setJobName(defaultJobName);
        xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
    }

    /**
     * This method sets the notifications to be processed by the Job
     *
     * @param xcapDiffNotifyDTOs the notification objects
     */
    public void setNotifications(List<KnXcapDiffNotifyDTO> xcapDiffNotifyDTOs) {
        this.xcapDiffNotifyDTOs = xcapDiffNotifyDTOs;
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
        boolean isSuccess = xcapDiffNotifier.generateDirNotification(xcapDiffNotifyDTOs, null, null);
        knLogger.info( methodName, "Job executed. Job status : " + isSuccess);
        return isSuccess;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(500);
        sb.append("Notification List - ").append(xcapDiffNotifyDTOs);
        return sb.toString();
    }
    }
