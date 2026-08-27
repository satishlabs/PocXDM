/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.resources.jobs.KnNotificationForAddgroup;
import com.kodiak.xdms.mediator.resources.jobs.KnNotificationForModiFyGroup;
import com.kodiak.xdms.mediator.resources.jobs.KnNotificationForRemoveGroup;
import com.kodiak.xdms.mediator.resources.jobs.asyncframework.KnAbstractJob;
import com.kodiak.xdms.server.common.resources.KnConstants;

import java.util.*;


public class KnWatcherNotify extends KnAbstractJob implements Runnable {

    private static final KnLogger knLogger = KnLogger.getLogger(com.kodiak.xdms.mediator.resources.jobs.asyncframework.KnUPMJob.class);
    private String txnId;
    private KnAsyncJobDTO asyncJobDTO;
    private LinkedHashMap<String, KnAsyncJobDTO> newJobs;
    private static final int ASSIGN_UPM = 10;
    private static final int ADD_GROUP = 13;
    private static final int MODIFY_GROUP = 14;
    private static final int REMOVE_GROUP = 15;

    public KnWatcherNotify(String txnId, KnAsyncJobDTO asyncJobDTO, LinkedHashMap<String, KnAsyncJobDTO> newJobs) {
        this.txnId = txnId;
        this.asyncJobDTO = asyncJobDTO;
        this.newJobs = newJobs;
    }

    @Override
    public boolean executeJob() {
        String methodName = "run()";
        knLogger.debug(methodName, "txnId", txnId, "asyncJobDTO", asyncJobDTO, "newJobsSize: ", newJobs.size(), "newJobs: ", newJobs);
        Iterator<Map.Entry<String, KnAsyncJobDTO>> itr = newJobs.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, KnAsyncJobDTO> entry = itr.next();
            knLogger.debug(methodName, "entry:", entry);
            KnAsyncJobDTO watcherJobNotifyDTO = entry.getValue();
            knLogger.debug(methodName, "watcherJobNotifyDTO: ", watcherJobNotifyDTO);
            if (watcherJobNotifyDTO.getOpStatus() == ASSIGN_UPM || watcherJobNotifyDTO.getOpStatus() == ADD_GROUP) {
                KnNotificationForAddgroup knNotificationForAddgroup = new KnNotificationForAddgroup(watcherJobNotifyDTO);
                knNotificationForAddgroup.run();
            } else if (watcherJobNotifyDTO.getOpStatus() == MODIFY_GROUP) {
                KnNotificationForModiFyGroup knNotificationForModiFyGroup = new KnNotificationForModiFyGroup(watcherJobNotifyDTO);
                knNotificationForModiFyGroup.run();
            } else if (watcherJobNotifyDTO.getOpStatus() == REMOVE_GROUP) {
                KnNotificationForRemoveGroup knNotificationForRemoveGroup = new KnNotificationForRemoveGroup(watcherJobNotifyDTO);
                knNotificationForRemoveGroup.run();
            }
        }
        return false;
    }

    @Override
    public void run() {
        executeJob();
    }
}
