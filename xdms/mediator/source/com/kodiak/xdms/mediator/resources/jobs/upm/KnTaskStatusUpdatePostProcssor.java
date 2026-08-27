/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnLogger;

public class KnTaskStatusUpdatePostProcssor implements ITaskPostProcessor {

    private static final KnLogger knLogger = KnLogger.getLogger(KnTaskStatusUpdatePostProcssor.class);

    private KnGeneralCacheUtil generalCacheUtil;
    private String taskId;
    private int taskStatus;

    public KnTaskStatusUpdatePostProcssor(String taskId, int taskStatus) {
        generalCacheUtil = KnGeneralCacheUtil.getInstance();
        this.taskId = taskId;
        this.taskStatus = taskStatus;
    }

    @Override
    public void postProcess(KnTaskResult result) {
        String methodName = "postProcessor(KnnTaskResult)";
        try {
            knLogger.debug(methodName, "ENTRY - taskId", this.taskId, "taskStatus - ", this.taskStatus);
            generalCacheUtil.updateAsyncJobTaskStatus(this.taskId, this.taskStatus);
            knLogger.debug(methodName, "EXIT -");

        } catch (KnDAOException e) {
            knLogger.debug(methodName, "Exception while updating task status", e.getMessage());
        }
    }
}
