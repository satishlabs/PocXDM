/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.notificationmgr.impl;

import com.kodiak.common.resources.KnThreadExecutors;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.beans.KnMcsxcapMdnDTO;
import com.kodiak.xdms.notificationmgr.resources.KnEtagMcsSendNotification;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class KnEtagNotificationConsumer implements Runnable{
    private static final KnLogger knLogger = KnLogger.getLogger(KnEtagNotificationConsumer.class);
    private KnGenInfoUtil genInfoUtil;
    private KnXcapDiffNotifier xcapDiffNotifier;
    private BlockingQueue<String> etagBlockingQueue = null;
    private static ExecutorService executor = null;

    static {
        executor = KnThreadExecutors.newFixedThreadExecutor(20, "ETAG_MCS_NOTIFY_SENDER");
        knLogger.info("Intiaizing executor :", executor);
    }

    public KnEtagNotificationConsumer() {
        this.genInfoUtil = KnGenInfoUtil.getInstance();
        xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
        this.etagBlockingQueue = KnMCSXCAPNotifier.getEtagBlockingQueue();
    }
    @Override
    public void run() {
        try {
        Set<String> etagMdnDTOs = new HashSet<>();
        etagBlockingQueue.drainTo(etagMdnDTOs, 1000);
        knLogger.info("Inside run Method of KnEtagNotificationConsumer", etagBlockingQueue);
        Map<String, Integer> mapOfEtag = genInfoUtil.getDirectoryEtag(etagMdnDTOs);
        etagMdnDTOs.forEach(mdn -> {
            KnEtagMcsSendNotification knEtagMcsSendNotification = new KnEtagMcsSendNotification(mdn, mapOfEtag);
            executor.submit(knEtagMcsSendNotification);
        });
        } catch (Throwable t) {
            knLogger.error("Exception in KnEtagNotificationConsumer", t.getMessage(), t);
        }
    }

}
