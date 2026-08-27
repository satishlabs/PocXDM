/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs;

import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.impl.KnXDMMediator;
import com.kodiak.xdms.server.common.KnXDMServerException;

public class KnNotificationForRemoveGroup implements Runnable {
    KnLogger knLogger = KnLogger.getLogger(KnNotificationForRemoveGroup.class);
    private static KnXDMMediator knXDMMediator = KnXDMMediator.getInstance();
    KnAsyncJobDTO watcher;

    public KnNotificationForRemoveGroup(KnAsyncJobDTO watcher) {
        this.watcher = watcher;
    }

    @Override
    public void run() {
        String methodName = "KnNotificationForRemoveGroupGroup , run()";
        try {
            boolean status = knXDMMediator.SendGroupWatcherNotificationForRemoveGroup(watcher);
            if (status) {
                knLogger.info(methodName, "Watcher Notification sent successfully");
            } else {
                knLogger.error(methodName, "Watcher Notification failed");
            }
        } catch (KnXDMServerException | KnPersistenceException e) {
            throw new RuntimeException(e);
        }
    }
}
