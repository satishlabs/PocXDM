/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.asyncframework;

import com.kodiak.common.resources.KnThreadExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class KnAsyncService {

    //TODO: make it to dynamic thread pool with diffrent poolname and make as ENUM
    private static ExecutorService jobExecutor = KnThreadExecutors.newDynamicThreadPool(5, 5, 0L, "ASYNC_FW_JOBTHREAD");
    private static ExecutorService jobRetryExecutor = KnThreadExecutors.newDynamicThreadPool(2, 2, 0L, "RETRY_JOBTHREAD");
    private static ExecutorService taskExecutor = Executors.newFixedThreadPool(5);
    private static ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private static ExecutorService jobwatcherExecutor = KnThreadExecutors.newDynamicThreadPool(5, 5, 0L, "GROUP_WATCHER_NOTIFY_THREAD");

    public static ExecutorService getJobExecutor() {
        return jobExecutor;
    }

    public static ExecutorService getRetryJobExecutor() {
        return jobRetryExecutor;
    }

    public static ExecutorService getTaskExecutor() {
        return taskExecutor;
    }

    public static ScheduledExecutorService getScheduledExecutor() { return scheduledExecutor; }

    public static ExecutorService getJobwatcherExecutor() {
        return jobwatcherExecutor;
    }
}
