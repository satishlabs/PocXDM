/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.notificationmgr.impl;

import java.util.Collection;

import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mcsnotifymgr.KnMCSDocChangeNotifier;
import com.kodiak.xdms.mcsnotifymgr.KnMCSDocChangeNotifier.IMdnTrackerRegistrar;

/**
 * Bridges MCS queue producers to {@link KnXcapDiffNotifier#upsertMdnNotifyTracker}
 * for all MCS notification entry points (UI, mediator, bulk, etc.).
 */
public class KnXcapMcsTrackerRegistrar implements IMdnTrackerRegistrar {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXcapMcsTrackerRegistrar.class);
    private static final String FLOW_TAG = "[XCAP-DEBULK-FLOW]";

    private static final KnXcapMcsTrackerRegistrar INSTANCE = new KnXcapMcsTrackerRegistrar();

    public static KnXcapMcsTrackerRegistrar getInstance() {
        return INSTANCE;
    }

    @Override
    public void registerWatcherMdnsIfOptimized(Collection<String> watcherMdns) {
        String methodName = "registerWatcherMdnsIfOptimized";
        if (watcherMdns == null || watcherMdns.isEmpty()) {
            return;
        }
        KnXcapDiffNotifier xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
        if (!xcapDiffNotifier.isOptimizedNotificationEnabled()) {
            knLogger.info(methodName, FLOW_TAG + " STEP-MCS0 Optimized mode OFF – MCS tracker upsert skipped");
            return;
        }
        try {
            xcapDiffNotifier.upsertMdnNotifyTracker(watcherMdns, null);
            knLogger.info(methodName, FLOW_TAG + " STEP-MCS1 MCS tracker upsert completed. uniqueMdns="
                    + watcherMdns.size());
            xcapDiffNotifier.logTrackerSnapshotForMdns("STEP-MCS1B", watcherMdns, null);
        } catch (KnPersistenceException e) {
            knLogger.warn(methodName, FLOW_TAG + " STEP-MCS1 MCS tracker upsert failed; queue rows remain saved. uniqueMdns="
                    + watcherMdns.size(), e);
        }
    }
}
