/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.resources;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnThreadExecutors.java
 * Subsystem:  Clientless Service
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ajit Kumar         06-Jun-2013    7.6
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

/**
 * This class provides the thread executor service handlers for
 * different types of fixed as well dynamic thread pool implementations
 * in an efficient manner.
 */
public class KnThreadExecutors {

    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                threadFactory);
    }

    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    public static ExecutorService newDynamicThreadPool(int corePoolSize, int maximumPoolSize) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    public static ExecutorService newDynamicThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    public static ExecutorService newDynamicThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                                       String threadName) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new KnThreadFactory(threadName));
    }

    public static ExecutorService newDynamicThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                                       ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);
    }

    public static ScheduledExecutorService newScheduledThreadPool(int threads, String threadName) {
        return Executors.newScheduledThreadPool(threads, new KnThreadFactory(threadName));
    }

    public static ScheduledExecutorService newScheduledThreadPool(int threads, KnThreadFactory threadFactory) {
        return Executors.newScheduledThreadPool(threads, threadFactory);
    }

    public static ScheduledExecutorService newScheduledThreadPool(int threads) {
        return Executors.newScheduledThreadPool(threads);
    }

    public static ExecutorService newSingleThreadExecutor(String threadName) {
        return Executors.newSingleThreadExecutor(new KnThreadFactory(threadName));
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String threadName) {
        return Executors.newSingleThreadScheduledExecutor(new KnThreadFactory(threadName));
    }

    public static ExecutorService newFixedThreadExecutor(int threads,String threadName) {
        return Executors.newFixedThreadPool(threads,new KnThreadFactory(threadName));
    }
}

class KnThreadFactory implements ThreadFactory {

   // private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public KnThreadFactory(String threadPrefix) {
        //SecurityManager securityManager = System.getSecurityManager();
       /* group = (securityManager != null) ? securityManager.getThreadGroup() :
                Thread.currentThread().getThreadGroup();*/
        namePrefix = threadPrefix;
    }

    public Thread newThread(Runnable r) {
        Thread thread = new Thread(null, r, namePrefix + "-" + threadNumber.getAndIncrement(), 0);
        if (thread.isDaemon())
            thread.setDaemon(false);
        if (thread.getPriority() != Thread.NORM_PRIORITY)
            thread.setPriority(Thread.NORM_PRIORITY);
        return thread;

    }

}

