/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.persistence.logger;

import com.kodiak.logger.KnLogger;

import com.mchange.v2.c3p0.ConnectionCustomizer;
import java.sql.Connection;


public abstract class KnC3p0DSLogger implements ConnectionCustomizer {

    private KnLogger logger;
    private int activeConnections = 0;
    private int acquiredConnections = 0;

    public KnC3p0DSLogger(Class dsClass) {
        logger = KnLogger.getLogger(dsClass);
    }

    public void onAcquire(Connection c, String pdsIdt) {
        acquiredConnections++;
        logger.info("onAcquire: Conn from db : ", c, " [", pdsIdt, "]", "Open Conns in Pool : ", acquiredConnections);
    }

    public void onDestroy(Connection c, String pdsIdt) {
        acquiredConnections--;
        logger.info("onDestroy: Conn closed with db : ", c, " [", pdsIdt, "]", "Open Conns in Pool : ", acquiredConnections);
    }

    public void onCheckOut(Connection c, String pdsIdt) {
        activeConnections++;
        logger.info("onCheckOut: Conn from pool : ", c, " [", pdsIdt, "]", "Active Conns in Pool : ", activeConnections);
    }

    public void onCheckIn(Connection c, String pdsIdt) {
        activeConnections--;
        logger.info("onCheckIn: Conn returned to pool : ", c, " [", pdsIdt, "]", "Active Conns in Pool : ", activeConnections);
    }
}