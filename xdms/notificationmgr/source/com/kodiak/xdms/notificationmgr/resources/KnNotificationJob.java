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
 * Upananda Singha      11/30/11      7.2
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
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;

import java.util.Collection;

/**
 * This is the Job class implementation defining the job for XCap Notification processing
 */
public class KnNotificationJob extends KnAbstractJob {
	private static final KnLogger knLogger = KnLogger.getLogger(KnNotificationJob.class);

    private static final long serialVersionUID = 7526471155622676266L;

    private final String CLASS = KnNotificationJob.class.getName();

    private Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffNotifyDTOs;
    private KnXcapDiffNotifier xcapDiffNotifier;
    private String defaultJobName = "XCapNotificationJob";

    public KnNotificationJob() {
        setJobName(defaultJobName);
        xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
    }

    /**
     * This method sets the notifications to be processed by the Job
     *
     * @param xcapDiffNotifyDTOs the notification objects
     */
    public void setNotifications(Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffNotifyDTOs) {
        this.xcapDiffNotifyDTOs = xcapDiffNotifyDTOs;
    }


    /**
     * The executeTask method that gets executed by the Scheduler when scheduler schedules the job
     *
     * @return boolean flag indicating job execution is successful or not
     * @throws KnJobSchedulerException exception
     */
    public boolean executeTask() throws KnJobSchedulerException {
        String methodName = "executeTask()";
        //MaxNotifications per job is not configured. Will Process the notifications in single Job
        knLogger.info( methodName, "Processing notifications inside Job - " + getJobName());
        boolean isSuccess = xcapDiffNotifier.sendXcapDiffNotifications(xcapDiffNotifyDTOs, null,null, null);
        knLogger.info( methodName, "Job executed. Job status - " + isSuccess);
        return isSuccess;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(500);
        sb.append("Notification List - ").append(xcapDiffNotifyDTOs);
        return sb.toString();
    }
    }
