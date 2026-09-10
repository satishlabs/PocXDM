/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.notificationmgr.impl;

import java.util.LinkedHashSet;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.beans.KnMcsxcapMdnDTO;

public class KnMCSXCAPNotifier {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXcapDiffNotifier.class);

    public static final int MAX_BLOCKING_QUEUE_SIZE = 50000;
    public static final int MAX_NOTIFICATION_SIZE = 1000;

    private static KnMCSXCAPNotifier knMCSXCAPNotifier = null;

    private static BlockingQueue<KnMcsxcapMdnDTO> blockingQueue = null;

    private static BlockingQueue<String> suppressMdnBlockingQueue = null;

    private static BlockingQueue<String> etagBlockingQueue = null;

    private KnMCSXCAPNotifier() {
    }

    public static synchronized KnMCSXCAPNotifier getInstance() {
        final String methodName = "getInstance()";
        if (knMCSXCAPNotifier == null) {
            knMCSXCAPNotifier = new KnMCSXCAPNotifier();
        }
        knLogger.debug(methodName, "Instance obtained");
        return knMCSXCAPNotifier;
    }

    public static BlockingQueue<KnMcsxcapMdnDTO> getBlockingQueue() {
        final String methodName = "getBlockingQueueInstance()";
        if (blockingQueue == null) {
            blockingQueue = new LinkedBlockingQueue<KnMcsxcapMdnDTO>();
        }
        knLogger.debug(methodName, "BlockingQueue Instance obtained", blockingQueue.hashCode());
        return blockingQueue;
    }

    public static BlockingQueue<String> getSuppressMdnBlockingQueue() {
        final String methodName = "getSuppressMdnBlockingQueue()";
        if (suppressMdnBlockingQueue == null) {
            suppressMdnBlockingQueue = new LinkedBlockingQueue<String>();
        }
        knLogger.debug(methodName, "BlockingQueue Instance obtained", suppressMdnBlockingQueue.hashCode());
        return suppressMdnBlockingQueue;
    }

    public void sendMCSXCAPNotification(LinkedHashSet<KnMcsxcapMdnDTO> mcsxcapMdnDTOS) {
        if (mcsxcapMdnDTOS != null && !mcsxcapMdnDTOS.isEmpty()) {
            if (mcsxcapMdnDTOS.size() > MAX_NOTIFICATION_SIZE) {
                LinkedHashSet<KnMcsxcapMdnDTO> emergencyMdnDTOS = mcsxcapMdnDTOS.stream()
                        .filter(KnMcsxcapMdnDTO::isEmergencyFlag)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                sendNotification(emergencyMdnDTOS);
                Set<String> nonEmergencyMdns = mcsxcapMdnDTOS.stream()
                        .filter(dto -> !dto.isEmergencyFlag())
                        .map(KnMcsxcapMdnDTO::getMdn)
                        .collect(Collectors.toSet());
                nonEmergencyMdns.forEach(nonEmergencyMdn -> {
                    try {
                        if (suppressMdnBlockingQueue.size() < MAX_BLOCKING_QUEUE_SIZE) {
                            suppressMdnBlockingQueue.put(nonEmergencyMdn);
                        }
                    } catch (InterruptedException e) {
                        knLogger.error("sendMCSXCAPNotification", "Error while adding non emergency mdn to suppressMdnBlockingQueue", e);
                    }
                });
            } else {
                sendNotification(mcsxcapMdnDTOS);
            }
        }
    }

    private static void sendNotification(LinkedHashSet<KnMcsxcapMdnDTO> mcsxcapMdnDTOS) {
        mcsxcapMdnDTOS.stream().forEach(mcsxcapMdnDTO -> {
            try {
                if (blockingQueue != null) {
                    if (blockingQueue.size() < MAX_BLOCKING_QUEUE_SIZE) {
                        if (mcsxcapMdnDTO.isEmergencyFlag() && blockingQueue.stream().anyMatch(existingDto -> existingDto.getMdn().equals(mcsxcapMdnDTO.getMdn()) && !existingDto.isEmergencyFlag())) {
                            blockingQueue.removeIf(existingDto -> existingDto.getMdn().equals(mcsxcapMdnDTO.getMdn()) && !existingDto.isEmergencyFlag());
                            blockingQueue.put(mcsxcapMdnDTO);
                        } else if (blockingQueue.stream().noneMatch(existingDto -> existingDto.getMdn().equals(mcsxcapMdnDTO.getMdn()))) {
                            blockingQueue.put(mcsxcapMdnDTO);
                        }
                    } else {
                        suppressMdnBlockingQueue.put(mcsxcapMdnDTO.getMdn());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static BlockingQueue<String> getEtagBlockingQueue() {
        final String methodName = "getBlockingQueueInstance()";
        if (etagBlockingQueue == null) {
            etagBlockingQueue = new LinkedBlockingQueue<String>();
        }
        knLogger.debug(methodName, "BlockingQueue Instance obtained", blockingQueue.hashCode());
        return etagBlockingQueue;
    }

    public static void sendEtagNotification(LinkedHashSet<String> etagMdnDTOS) {
        if (etagMdnDTOS == null || etagMdnDTOS.isEmpty()) {
            knLogger.warn("sendEtagNotification", "No etag notifications to process.");
            return;
        }

        etagMdnDTOS.stream()
                .filter(etagMdnDTO -> etagMdnDTO != null && !etagMdnDTO.isEmpty()) // Filter out null or empty strings
                .forEach(etagMdnDTO -> {
                    try {
                        if (etagBlockingQueue != null
                                && etagBlockingQueue.size() < MAX_BLOCKING_QUEUE_SIZE
                                && !etagBlockingQueue.contains(etagMdnDTO)) {
                            etagBlockingQueue.put(etagMdnDTO);
                        }
                    } catch (Exception e) {
                        knLogger.error("sendEtagNotification", "Exception occurred while adding to etagBlockingQueue", e);
                    }
                });

        knLogger.info("sendEtagNotification", "Etag notification sent to blocking queue", etagBlockingQueue);
    }

}

