/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 *  Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
 *  All Rights Reserved
 *  Motorola Solutions Confidential Restricted
 * <p>
 * File name:  KnTransactionStorage.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Sanjiv A                    16-Oct-2023                13.0
 * <p>
 * ************************************************************************
 */
package com.kodiak.common.dao;

import com.kodiak.dbmgr.KnTransaction;
import com.kodiak.logger.KnLogger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class KnTransactionStorage {

    private static final KnLogger knLogger = KnLogger.getLogger(KnTransactionStorage.class);
    private ScheduledExecutorService scheduler;
    private static KnTransactionStorage instance;
    private ConcurrentLinkedQueue<KnTransaction> txnQueue;
    private static final int maxCapacity = 20000;
    private ScheduledFuture<?> scheduledFuture;

    private KnTransactionStorage() {
        txnQueue = new ConcurrentLinkedQueue<>();
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public static synchronized KnTransactionStorage getInstance() {
        if (instance == null) {
            synchronized (KnTransactionStorage.class) {
                if (instance == null) {
                    knLogger.debug("getInstance()", "creating instance");
                    instance = new KnTransactionStorage();
                }
            }
        }
        return instance;
    }

    /**
     * Method to add transaction object in cache
     * @param txn
     */
    public void add(KnTransaction txn) {
        String methodName = "add()";
        if (txnQueue.size() >= maxCapacity) {
            knLogger.info(methodName, "Storage capacity reached Skipping adding txn for - ", txn);
        } else {
            knLogger.debug(methodName, "adding txn - ", txn);
            txnQueue.add(txn);
        }
    }

    /**
     * Method to remoce transaction object from cache
     * @param txn
     */
    public void remove(KnTransaction txn) {
        String methodName = "remove()";
        txnQueue.remove(txn);
        knLogger.debug(methodName, "Queue size - ", txnQueue.size());
    }

    private ConcurrentLinkedQueue<KnTransaction> getTxnQueue() {
        return txnQueue;
    }

    /**
     * Method to schedule the audit thread and its implementatiov
     * @param txnExpiryTimer
     * @param auditDelayTimer
     */
    public void scheduleThread(long txnExpiryTimer, int auditDelayTimer) {
        String methodName = "scheduleThread()";
        try {
            if (null != scheduledFuture && !scheduledFuture.isCancelled()) {
                knLogger.info(methodName, "Already scheduled, cancelling existing task");
                scheduledFuture.cancel(false);
            }
            knLogger.info(methodName, "Scheduling a new task");
            scheduledFuture = scheduler.scheduleWithFixedDelay(() -> {
                        ConcurrentLinkedQueue<KnTransaction> queue = getTxnQueue();
                        knLogger.info(methodName, "Audit started Queue size - ", queue.size());
                        while (!queue.isEmpty()) {
                            try {
                                KnTransaction txnObj = queue.peek();
                                int state = txnObj.getStatus();
                                long creatTime = txnObj.getCreationTime();
                                long expTime = creatTime + txnExpiryTimer;
                                knLogger.info(methodName, "Checking for ", txnObj);
                                long currentTime = System.currentTimeMillis();
                                knLogger.debug(methodName, "currentTime ", currentTime, " Exp Time -", expTime);
                                if (expTime < currentTime) {
                                    if (txnObj.getStatus() == 1) {
                                        knLogger.info(methodName, "RollBack for ", txnObj, " Status - ", state);
                                        txnObj.rollback();
                                    }
                                    queue.poll();
                                } else {
                                    knLogger.debug(methodName, "First txn object is not expired, skipping to check others ", txnObj, " Status - ", state);
                                    break;
                                }
                            } catch (Exception e) {
                                knLogger.error(methodName, "Exception - ", e);
                            } catch (Throwable t) {
                                knLogger.error(methodName, "Throwable - ", t);
                            }
                        }
                    }, 180, auditDelayTimer, TimeUnit.SECONDS
            );
            knLogger.debug(methodName, "-- Scheduled LRT Monitor thread --");
        } catch (Exception e) {
            knLogger.error(methodName, "Exception - ", e);
        }
    }
}
