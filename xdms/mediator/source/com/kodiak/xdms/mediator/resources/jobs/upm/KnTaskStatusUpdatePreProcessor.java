/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnLogger;

public class KnTaskStatusUpdatePreProcessor implements ITaskPreprocessor {

    private static final KnLogger knLogger = KnLogger.getLogger(KnTaskStatusUpdatePreProcessor.class);
    private KnGeneralCacheUtil generalCacheUtil;
    private String taskId;
    private int taskStatus;

    public KnTaskStatusUpdatePreProcessor(String taskId, int taskStatus) {
        generalCacheUtil = KnGeneralCacheUtil.getInstance();
        this.taskId = taskId;
        this.taskStatus = taskStatus;
    }


    @Override
    public void preprocess(KnTaskResult result) {
        String methodName = "preprocess(KnnTaskResult)";

        try {
            knLogger.debug(methodName, "ENTRY - taskId", this.taskId, "taskStatus - ", this.taskStatus);
            generalCacheUtil.updateAsyncJobTaskStatus(this.taskId, this.taskStatus);
            knLogger.debug(methodName, "EXIT -");
        } catch (KnDAOException e) {
            knLogger.debug(methodName, "Exception while updating task status", e.getMessage());
        }

    }
}
