/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/*
 *  Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
 *  All Rights Reserved
 *  Motorola Solutions Confidential Restricted
 *
 */

package com.kodiak.xdms.notificationmgr.resources;

import com.kodiak.common.commdto.common.KnXDMSubsProvDTO;
import com.kodiak.common.commdto.response.KnXDMSubsProfileRespDTO;
import com.kodiak.common.resources.KnThreadExecutors;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants;
import com.kodiak.xdms.mcsnotifymgr.beans.KnMCSNotifyDTO;
import com.kodiak.xdms.mcsnotifymgr.resources.KnSendMCSNotify;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnNotificationKeyDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil.*;

/**
 * Scheduled Runnable that drives the XCAP notification dispatch pipeline.
 *
 * <h3>Execution modes</h3>
 * <ol>
 *   <li><b>Legacy mode ({@code XCAP_NOTIFICATION_OPTIMIZED=0})</b><br>
 *       Each polled record produces one independent worker task – the existing behaviour.</li>
 *   <li><b>Optimised / debulk mode ({@code XCAP_NOTIFICATION_OPTIMIZED=1})</b><br>
 *       {@link KnXcapDiffDirChgNotifyDTO} payloads for the <em>same watcher</em> are
 *       grouped by a composite watcher key ({@code destType|destId}) and handed to a
 *       single {@link KnWatcherDebulkSendNotification} worker.  This is the
 *       "Send Together" step described in the epic: one bundled notification per
 *       watcher per epoch window.</li>
 * </ol>
 */
public class KnXcapNotifyProcessor implements Runnable, IStatusMgrNotifyIntf {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXcapNotifyProcessor.class);
    private static final String FLOW_TAG = "[XCAP-DEBULK-FLOW]";

    /** Thread pool used to dispatch individual (legacy) or bundled (optimised) send tasks. */
    private static ExecutorService executor = null;

    public static KnXcapNotifyProcessor instance;

    private final KnXcapDiffNotifier xcapDiffNotifier;
    private final KnGenInfoUtil       genInfoUtil = KnGenInfoUtil.getInstance();

    /** {@code true} during the very first run cycle; triggers NOTIFY_INITIATED → PENDING reset. */
    private boolean startUp = true;

    /** Tracks the current redundancy state; only ACTIVE card performs notification dispatch. */
    private KnStatusMgrConstants.CARD_STATES currentRedState =
            KnStatusMgrConstants.CARD_STATES.UNKNOWN;

    static {
        // Fixed thread pool for XCAP notification send workers.
        // Size 20 allows up to 20 concurrent watcher sends without blocking the poller.
        executor = KnThreadExecutors.newFixedThreadExecutor(20, "XCAP_NOTIFY_SENDER");
        knLogger.info("Initialising executor: " + executor);
    }

    private KnXcapNotifyProcessor() {
        xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
    }

    public static KnXcapNotifyProcessor getInstance() {
        if (instance == null) {
            instance = new KnXcapNotifyProcessor();
            // Register for redundancy state notifications so that only the active card sends
            List<IStatusMgrNotifyIntf> list = new ArrayList<>();
            list.add(instance);
            KnStatusManagerClient.registerObjects(list);
            knLogger.info("getInstance() Registered for Status Manager notifications");
        }
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    // Runnable entry point
    // ─────────────────────────────────────────────────────────────────────────────────

    @Override
    public void run() {
        String methodName = "run()";
        Map<KnNotificationKeyDTO, Object> record = null;
        try {
            int queueDepthAtCycleStart = xcapDiffNotifier.totalRecordCount();
            knLogger.info(methodName + " " + FLOW_TAG + " STEP-1 Poll cycle started. queueDepth=" + queueDepthAtCycleStart);

            // Only the ACTIVE card should deliver notifications
            if (getCurrentRedundancyStatus() != KnStatusMgrConstants.CARD_STATES.ACTIVE) {
                knLogger.info(methodName + " " + FLOW_TAG
                        + " STEP-1A Skipping cycle because node is not ACTIVE. currentState="
                        + getCurrentRedundancyStatus() + " queueDepthUnchangedSnapshot=" + queueDepthAtCycleStart);
                return;
            }

            // On first run, recover any rows that were left in NOTIFY_INITIATED state
            // (e.g. from a previous crash or restart) by resetting them to PENDING.
            if (startUp) {
                knLogger.info(methodName + " " + FLOW_TAG
                        + " STEP-2 Startup recovery: resetting NOTIFY_INITIATED rows to PENDING");
                xcapDiffNotifier.updateRecord();
                knLogger.info(methodName + " " + FLOW_TAG
                        + " STEP-2B Startup recovery complete. queueDepthAfterRecovery=" + xcapDiffNotifier.totalRecordCount());
                startUp = false;
            }

            // Poll the pending notification queue (legacy or epoch-gated depending on flag)
            record = xcapDiffNotifier.pollRecord();
            if (record == null || record.isEmpty()) {
                knLogger.info(methodName + " " + FLOW_TAG
                        + " STEP-3 Poll complete: no records claimed in this cycle");
                return;
            }

            knLogger.info(methodName + " " + FLOW_TAG
                    + " STEP-3 Poll complete: claimedRecords=" + record.size());

            // Dispatch the polled records to workers
            process(record);
            knLogger.info(methodName + " " + FLOW_TAG
                    + " STEP-7A Post-dispatch queue snapshot. queueDepthAfterSubmit=" + xcapDiffNotifier.totalRecordCount()
                    + " claimedRecords=" + record.size());

        } catch (Throwable e) {
            knLogger.error(methodName
                    + " Exception while processing XCAP notifications. Scheduler continues. error=" + e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    // Process a polled record batch
    // ─────────────────────────────────────────────────────────────────────────────────

    /**
     * Dispatches a batch of polled notification records to worker threads.
     *
     * <h3>Optimised path (XCAP_NOTIFICATION_OPTIMIZED=1)</h3>
     * <ol>
     *   <li>All {@link KnXcapDiffDirChgNotifyDTO} payloads are collected into a
     *       {@code Map<watcherKey, List<DTO>>} where {@code watcherKey = destType|destId}.</li>
     *   <li>Once the loop completes, one {@link KnWatcherDebulkSendNotification} worker is
     *       submitted <em>per unique watcher key</em> – giving exactly one bundled notification
     *       per subscriber per epoch window.</li>
     *   <li>Non-dir-change payloads ({@link KnXcapDiffNotifyDTO}, {@link KnMCSNotifyDTO})
     *       continue to be dispatched individually as before.</li>
     * </ol>
     *
     * <h3>Legacy path</h3>
     * Every record produces its own independent worker task.
     *
     * @param record polled snapshot from {@link KnXcapDiffNotifier#pollRecord()}
     */
    public void process(Map<KnNotificationKeyDTO, Object> record) {
        String methodName = "process(Map<KnNotificationKeyDTO, Object>)";
        knLogger.entry(methodName);
        knLogger.info(methodName + " " + FLOW_TAG
                + " STEP-4 Processing claimed records. recordSize=" + record.size());

        // ── Subscriber-info prefetch (needed by all send paths) ───────────────────────
        List<String>               mdnListTogetSublistInfo = new ArrayList<>();
        Map<String, KnXDMSubsProvDTO> mdnSubsInfoMap       = new HashMap<>();
        Map<String, Set<String>>      baseMdnMap            = new HashMap<>();

        try {
            knLogger.info(methodName + " " + FLOW_TAG
                    + " STEP-5 Fetching subscriber context for claimed records");
            xcapDiffNotifier.getmdnListTogetSublistInfo(record, mdnListTogetSublistInfo);
            knLogger.debug(methodName + " mdnListTogetSublistInfo size=" + mdnListTogetSublistInfo.size());

            KnXDMSubsProfileRespDTO subsInfo =
                    genInfoUtil.selectSubsProfileInfo(mdnListTogetSublistInfo, null);
            if (subsInfo != null && subsInfo.getSubsRespDTO() != null
                    && !subsInfo.getSubsRespDTO().isEmpty()) {
                for (KnXDMSubsProvDTO itr : subsInfo.getSubsRespDTO()) {
                    mdnSubsInfoMap.put(itr.getMdn().trim(), itr);
                }
                knLogger.info(methodName + " mdnSubsInfoMap size=" + mdnSubsInfoMap.size());
            }
            baseMdnMap = genInfoUtil.getBaseMdnsMap(mdnListTogetSublistInfo, null);
            knLogger.debug(methodName + " baseMdnMap=" + baseMdnMap.size()
                    + " mdnSubsInfoMap=" + mdnSubsInfoMap.size());
            knLogger.info(methodName + " " + FLOW_TAG
                    + " STEP-5 Completed subscriber context fetch. mdnSubsInfoMap=" + mdnSubsInfoMap.size()
                    + " baseMdnMap=" + baseMdnMap.size());

        } catch (Exception e) {
            knLogger.error(methodName + " Exception during subscriber info prefetch: " + e);
            throw new RuntimeException(e);
        }

        // ── Check optimised mode once per batch ───────────────────────────────────────
        boolean optimizedMode = xcapDiffNotifier.isOptimizedNotificationEnabled();
        knLogger.debug(methodName + " optimizedMode=" + optimizedMode);

        // ── Grouping structures for the debulk (optimised) path ───────────────────────
        // watcherKey = destType + "|" + destId  (unique per subscriber and destination type)
        Map<String, List<KnXcapDiffDirChgNotifyDTO>> groupedDirChanges = new LinkedHashMap<>();
        Map<String, KnNotificationKeyDTO>              groupedKeys       = new LinkedHashMap<>();
        Map<String, List<KnNotificationKeyDTO>>        groupedSeqKeys    = new LinkedHashMap<>();
        int legacyDirTasks = 0;
        int diffTasks = 0;
        int mcsTasks = 0;
        int groupedPayloads = 0;

        // ── Main dispatch loop ────────────────────────────────────────────────────────
        for (Map.Entry<KnNotificationKeyDTO, Object> itr : record.entrySet()) {
            Object              payload = itr.getValue();
            KnNotificationKeyDTO key    = itr.getKey();

            if (optimizedMode && payload instanceof KnXcapDiffDirChgNotifyDTO dto) {
                // ── OPTIMISED: accumulate per watcher instead of submitting immediately ──
                String watcherKey = key.getDestType() + "|" + key.getDestId();
                groupedDirChanges
                        .computeIfAbsent(watcherKey, k -> new ArrayList<>())
                        .add(dto);
                groupedSeqKeys
                        .computeIfAbsent(watcherKey, k -> new ArrayList<>())
                        .add(key);
                groupedPayloads++;
                // Keep the first key seen for this watcher (preserves CID / dest metadata)
                groupedKeys.putIfAbsent(watcherKey, key);
                knLogger.debug(methodName + " Accumulated DirChg for watcher=" + watcherKey
                        + " totalSoFar=" + groupedDirChanges.get(watcherKey).size());

            } else if (payload instanceof KnXcapDiffDirChgNotifyDTO dto) {
                // ── LEGACY: one worker per notification ───────────────────────────────
                executor.submit(new KnXcapSendNotification(key, dto, mdnSubsInfoMap, baseMdnMap));
                legacyDirTasks++;

            } else if (payload instanceof KnXcapDiffNotifyDTO dto) {
                // ── Diff notifications: always dispatched individually ────────────────
                executor.submit(new KnSendNotification(key, dto, mdnSubsInfoMap, baseMdnMap));
                diffTasks++;

            } else if (payload instanceof KnMCSNotifyDTO dto) {
                // ── MCS notifications: always dispatched individually ─────────────────
                executor.submit(new KnSendMCSNotify(key, dto));
                mcsTasks++;

            } else {
                knLogger.warn(methodName + " Unknown payload type=" + payload);
            }
        }

        // ── OPTIMISED: submit ONE bundled send per unique watcher ─────────────────────
        if (optimizedMode && !groupedDirChanges.isEmpty()) {
            knLogger.info(methodName + " Submitting debulk workers for "
                    + groupedDirChanges.size() + " unique watcher(s)");

            for (Map.Entry<String, List<KnXcapDiffDirChgNotifyDTO>> entry
                    : groupedDirChanges.entrySet()) {

                String watcherKey = entry.getKey();
                List<KnXcapDiffDirChgNotifyDTO> notifications = entry.getValue();
                KnNotificationKeyDTO            notifKey      = groupedKeys.get(watcherKey);

                knLogger.info(methodName
                        + " CID=" + notifKey.getCid()
                        + " watcher=" + notifKey.getDestId()
                        + " bundledCount=" + notifications.size());
                knLogger.info(methodName + " " + FLOW_TAG
                        + " STEP-6A Bundled watcher ready for dispatch. cid=" + notifKey.getCid()
                        + " watcher=" + notifKey.getDestId()
                        + " watcherKey=" + watcherKey
                        + " bundledCount=" + notifications.size()
                        + " seqKeyCount=" + groupedSeqKeys.get(watcherKey).size());

                // Submit one bundled Runnable for this watcher
                executor.submit(new KnWatcherDebulkSendNotification(
                        notifKey, groupedSeqKeys.get(watcherKey), notifications, mdnSubsInfoMap, baseMdnMap,
                        xcapDiffNotifier));
            }
        }

        knLogger.info(methodName + " " + FLOW_TAG
                + " STEP-6 Dispatch summary: optimizedMode=" + optimizedMode
                + " groupedWatchers=" + groupedDirChanges.size()
                + " groupedPayloads=" + groupedPayloads
                + " legacyDirTasks=" + legacyDirTasks
                + " diffTasks=" + diffTasks
                + " mcsTasks=" + mcsTasks);
        int effectiveSendTasks = optimizedMode
                ? groupedDirChanges.size() + diffTasks + mcsTasks + legacyDirTasks
                : legacyDirTasks + diffTasks + mcsTasks;
        int fanoutReduction = optimizedMode ? Math.max(groupedPayloads - groupedDirChanges.size(), 0) : 0;
        knLogger.info(methodName + " " + FLOW_TAG
                + " STEP-6B Fanout benchmark snapshot. optimizedMode=" + optimizedMode
                + " effectiveSendTasks=" + effectiveSendTasks
                + " groupedPayloads=" + groupedPayloads
                + " groupedWatchers=" + groupedDirChanges.size()
                + " fanoutReduction=" + fanoutReduction
                + " claimedRecords=" + record.size());

        knLogger.info(methodName + " " + FLOW_TAG
                + " STEP-7 Dispatch submitted; cleanup delegated to worker completion. claimedCount=" + record.size());

        knLogger.exit(methodName);
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    // Redundancy status management
    // ─────────────────────────────────────────────────────────────────────────────────

    @Override
    public void notify(KnStatusMgrConstants.CARD_STATES previousState,
                       KnStatusMgrConstants.CARD_STATES currentState) {
        String methodName = "notify";
        knLogger.info(methodName + " Status Mgr update [Prev:" + previousState
                + "; Current:" + currentState + "]");
        this.currentRedState = currentState;
        knLogger.debug(methodName + " Current State=" + currentState);
    }

    public KnStatusMgrConstants.CARD_STATES getCurrentRedundancyStatus() {
        String methodName = "getCurrentRedundancyStatus";
        knLogger.debug(methodName + " Current Card Redundancy Status=" + this.currentRedState);
        return this.currentRedState;
    }
}

