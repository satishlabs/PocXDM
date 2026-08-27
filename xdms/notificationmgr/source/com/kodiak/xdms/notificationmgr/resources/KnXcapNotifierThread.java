/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXcapNotifierThread.java
 * Subsystem:   Xcap Notificaton Mgr
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       2/28/11        7.0.2
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
 * *******************************************************************************
 */
package com.kodiak.xdms.notificationmgr.resources;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;

import java.util.Collection;

public class KnXcapNotifierThread implements Runnable {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXcapNotifierThread.class);
    private static final String className = KnXcapNotifierThread.class.getName();
    private Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffNotifyDTOs;
    private KnXcapDiffNotifier xcapDiffNotifier;
    private int maxAllowedSize = -1;

    public KnXcapNotifierThread(KnXcapDiffNotifier notifier, Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffNotifyDTOs,
                                int maxAllowedSize) {
        this.xcapDiffNotifier = notifier;
        this.xcapDiffNotifyDTOs = xcapDiffNotifyDTOs;
        this.maxAllowedSize = maxAllowedSize;
    }

    public void run() {
        String methodName = "run()";
        knLogger.info( methodName, "Executing in a new Thread Context");
        xcapDiffNotifier.sendXcapDiffNotifications(xcapDiffNotifyDTOs, null,null, null);
    }
}
