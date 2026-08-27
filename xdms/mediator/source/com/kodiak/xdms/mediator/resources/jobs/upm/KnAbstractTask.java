/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

//Before running each task check if any preporcessors are present and execute them
//Then run task
//Any post processor run post processor


import com.kodiak.logger.KnLogger;

import java.util.ArrayList;
import java.util.List;

public abstract class KnAbstractTask implements ITask {

    KnLogger knLogger = KnLogger.getLogger(KnAbstractTask.class);

    public List<ITaskPreprocessor> taskPreprocessors;
    public List<ITaskPostProcessor> taskPostProcessors;
    KnTaskResult taskResult = new KnTaskResult();

    public abstract KnTaskResult executeTask();


    public KnTaskResult execute() {
        String method = "execute()";
        if (this.taskPreprocessors != null) {
            taskPreprocessors.forEach(taskPreprocessor -> {
                taskPreprocessor.preprocess(taskResult);
            });
        }

        KnTaskResult taskResults=executeTask();

        if (this.taskPostProcessors != null) {
            taskPostProcessors.forEach(taskPostProcessor -> {
                taskPostProcessor.postProcess(taskResult);
            });
        }
        return taskResults;
    }


    public void addPreprocessor(ITaskPreprocessor preprocessor) {
        if (this.taskPreprocessors == null) {
            this.taskPreprocessors = new ArrayList<>();
        }

        this.taskPreprocessors.add(preprocessor);
    }

    public void addPostprocessor(ITaskPostProcessor postProcessor) {
        if (this.taskPostProcessors == null) {
            this.taskPostProcessors = new ArrayList<>();
        }

        this.taskPostProcessors.add(postProcessor);
    }

    @Override
    public void onSuccess(KnTaskResult result) {

    }

    @Override
    public void onFailure(KnTaskResult result) {

    }
}
