/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.resources.util;

import com.kodiak.logger.KnLogger;

/**
 * Thread-safe 64-bit Snowflake ID generator for use across the XDM layer.
 * Matches the algorithm used in AuthZ DistributedIdGenerator:
 *   id = (epochOffsetMs << 22) | (machineId << 12) | sequence
 *
 * Machine ID is derived from env vars PTTSERVERID and LOCAL_IP_ADDRESS,
 * matching the same globalIdentity formula used in the AuthZ service.
 */
public class KnSnowflakeIdGenerator {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSnowflakeIdGenerator.class);

    private static final long EPOCH = 1704067200000L;

    private static final long MACHINE_ID_BITS  = 10L;
    private static final long SEQUENCE_BITS    = 12L;

    private static final long MAX_MACHINE_ID   = -1L ^ (-1L << MACHINE_ID_BITS);
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT  = SEQUENCE_BITS + MACHINE_ID_BITS;
    private static final long SEQUENCE_MASK    = -1L ^ (-1L << SEQUENCE_BITS);

    private static final long machineId;

    private static long lastTimestamp = -1L;
    private static long sequence = 0L;

    static {
        String pttServerId    = System.getenv("PTTSERVERID");
        String localIpAddress = System.getenv("LOCAL_IP_ADDRESS");
        String globalIdentity = pttServerId + "@" + localIpAddress;
        machineId = Math.abs(globalIdentity.hashCode()) % (MAX_MACHINE_ID + 1);
        knLogger.info("KnSnowflakeIdGenerator", "Initialized — machineId=", machineId,
                " identity=", globalIdentity);
    }

    private KnSnowflakeIdGenerator() {}

    /**
     * Returns the next unique 64-bit Snowflake ID.
     */
    public static synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("KnSnowflakeIdGenerator: clock moved backwards. "
                    + "Refusing to generate id for " + (lastTimestamp - timestamp) + " ms.");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | sequence;
    }

    private static long waitNextMillis(long lastTs) {
        long ts = System.currentTimeMillis();
        while (ts <= lastTs) {
            ts = System.currentTimeMillis();
        }
        return ts;
    }
}
