/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/

package com.kodiak.tool.memorymonitor;

import java.util.TimerTask;
import java.util.logging.Level;

public class KnAliveTask extends TimerTask {

    public KnAliveTask() {
    }
    @Override
    public void run() {
        KnMemoryMonitor.logger.log(Level.INFO, "Keeping it alive");
    }

}
