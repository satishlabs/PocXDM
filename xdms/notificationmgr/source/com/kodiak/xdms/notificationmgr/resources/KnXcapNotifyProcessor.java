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
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDocDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnNotificationKeyDTO;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil.*;

public class KnXcapNotifyProcessor implements Runnable, IStatusMgrNotifyIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXcapNotifyProcessor.class);
    private static ExecutorService executor = null;

    public static KnXcapNotifyProcessor instance;

    private final KnXcapDiffNotifier xcapDiffNotifier;

    private KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();

    private boolean startUp = true;


    private KnStatusMgrConstants.CARD_STATES currentRedState = KnStatusMgrConstants.CARD_STATES.UNKNOWN;

    static {
        executor = KnThreadExecutors.newFixedThreadExecutor(20, "XCAP_NOTIFY_SENDER");
        knLogger.info("Intiaizing executor :", executor);
    }

    private KnXcapNotifyProcessor() {
        xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
    }

    public static KnXcapNotifyProcessor getInstance() {
        if (instance == null) {
            instance = new KnXcapNotifyProcessor();
            //registering for Redundancy Notify Status
            List<IStatusMgrNotifyIntf> list = new ArrayList<>();
            list.add(instance);
            KnStatusManagerClient.registerObjects(list);
            knLogger.info("getInstance()", "registered for Status..");
        }
        return instance;
    }

    @Override
    public void run() {
        String methodName = "run()";
        Map<KnNotificationKeyDTO, Object> record = null;
        try {
            if (getCurrentRedundancyStatus() != KnStatusMgrConstants.CARD_STATES.ACTIVE) {
                knLogger.debug(methodName, "[XCAP-DEBULK-FLOW] STEP-SCH0 Poll skipped. Card is not active.");
                return;
            }
            if (startUp) {
                xcapDiffNotifier.updateRecord();
                startUp = false;
            }
            record = xcapDiffNotifier.pollRecord();
            if (record == null || record.isEmpty()) {
                return;
            }
            process(record);
        } catch (Throwable e) {
            knLogger.error(methodName, "Exception while processing XCAP notifications. Scheduler will continue with next run.", e);
        }
    }


    public void process(Map<KnNotificationKeyDTO, Object> record) {
        String methodName = "process(Map<KnNotificationKeyDTO, Object> record)";
        knLogger.entry(methodName);
        List<String> mdnListTogetSublistInfo = new ArrayList<>();
        Map<String, KnXDMSubsProvDTO> mdnSubsInfoMap = new HashMap<>();
        Map<String, Set<String>> baseMdnMap = new HashMap<>();

        try {
            xcapDiffNotifier.getmdnListTogetSublistInfo(record, mdnListTogetSublistInfo);
            knLogger.debug(methodName, "mdnListTogetSublistInfo size:", mdnListTogetSublistInfo.size());
            KnXDMSubsProfileRespDTO subsInfo = genInfoUtil.selectSubsProfileInfo(mdnListTogetSublistInfo, null);
            if (subsInfo != null && subsInfo.getSubsRespDTO() != null && !subsInfo.getSubsRespDTO().isEmpty()) {
                List<KnXDMSubsProvDTO> subslist = new ArrayList<>(subsInfo.getSubsRespDTO());
                for (KnXDMSubsProvDTO itr : subslist) {
                    mdnSubsInfoMap.put(itr.getMdn().trim(), itr);
                }
                knLogger.info("mdns set in mdnSubsInfoMap size", mdnSubsInfoMap.size());
            }
            baseMdnMap = genInfoUtil.getBaseMdnsMap(mdnListTogetSublistInfo, null);
            knLogger.debug(methodName, "baseMdnMap size :", baseMdnMap.size(), " mdnSubsInfoMap size: ", mdnSubsInfoMap.size());
        } catch (Exception e) {
            knLogger.error(methodName, "Exception : ", e);
            throw new RuntimeException(e);
        }
        // Optimized mode sends one notification per watcher after the hold.
        // The worker deletes those queue rows after a successful send.
        if (xcapDiffNotifier.isXcapNotificationOptimized()) {
            dispatchBundledNotifications(record, mdnSubsInfoMap, baseMdnMap);
            knLogger.exit(methodName);
            return;
        }
        for (Map.Entry<KnNotificationKeyDTO, Object> itr : record.entrySet()) {
                Object payload = itr.getValue();
                if (payload instanceof KnXcapDiffDirChgNotifyDTO knXcapDiffDirChgNotifyDTO) {
                    KnXcapSendNotification knXcapSendNotification = new KnXcapSendNotification(itr.getKey(), knXcapDiffDirChgNotifyDTO, mdnSubsInfoMap, baseMdnMap);
                    executor.submit(knXcapSendNotification);
                } else if (payload instanceof KnXcapDiffNotifyDTO knXcapDiffNotifyDTO) {
                    KnSendNotification knXcapSendNotification = new KnSendNotification(itr.getKey(), knXcapDiffNotifyDTO, mdnSubsInfoMap, baseMdnMap);
                    executor.submit(knXcapSendNotification);
                } else if (payload instanceof KnMCSNotifyDTO knMCSNotifyDTO) {
                    KnSendMCSNotify knSendMCSNotify = new KnSendMCSNotify(itr.getKey(), knMCSNotifyDTO);
                    executor.submit(knSendMCSNotify);
                }
        }
        xcapDiffNotifier.cleanUpRecord(record.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList()));
        knLogger.exit(methodName);
    }

    /**
     * One task per watcher. Directory changes and document diffs for that watcher
     * stay together. Suppressed and other payloads stay on the existing per-row sender.
     */
    private void dispatchBundledNotifications(Map<KnNotificationKeyDTO, Object> record,
                                              Map<String, KnXDMSubsProvDTO> mdnSubsInfoMap,
                                              Map<String, Set<String>> baseMdnMap) {
        String methodName = "dispatchBundledNotifications";
        Map<String, List<Map.Entry<KnNotificationKeyDTO, Object>>> directoryByWatcher = new HashMap<>();
        Map<String, List<Map.Entry<KnNotificationKeyDTO, Object>>> documentByWatcher = new HashMap<>();
        List<Map.Entry<KnNotificationKeyDTO, Object>> legacyRows = new ArrayList<>();
        for (Map.Entry<KnNotificationKeyDTO, Object> entry : record.entrySet()) {
            KnNotificationKeyDTO key = entry.getKey();
            boolean suppressed = key.getDestType() == KnXcapNotifyConstants.DESTTYPE.SUPPRESSMDN.value();
            if (!suppressed && key.getDestId() != null && entry.getValue() instanceof KnXcapDiffDirChgNotifyDTO) {
                directoryByWatcher.computeIfAbsent(key.getDestId().trim(), ignored -> new ArrayList<>()).add(entry);
            } else if (!suppressed && key.getDestId() != null && entry.getValue() instanceof KnXcapDiffNotifyDTO) {
                documentByWatcher.computeIfAbsent(key.getDestId().trim(), ignored -> new ArrayList<>()).add(entry);
            } else {
                legacyRows.add(entry);
            }
        }
        Set<String> watchers = new LinkedHashSet<>();
        watchers.addAll(directoryByWatcher.keySet());
        watchers.addAll(documentByWatcher.keySet());
        knLogger.info(methodName, "[XCAP-DEBULK-FLOW] STEP-7 Bundling watcher notifications. watchers="
                + watchers.size() + " legacyRows=" + legacyRows.size());
        for (String watcher : watchers) {
            List<KnNotificationKeyDTO> keys = new ArrayList<>();
            List<KnXcapDiffDirChgNotifyDTO> directoryChanges = new ArrayList<>();
            List<KnXcapDiffNotifyDTO> documentChanges = new ArrayList<>();
            for (Map.Entry<KnNotificationKeyDTO, Object> row : directoryByWatcher.getOrDefault(watcher, List.of())) {
                keys.add(row.getKey());
                directoryChanges.add((KnXcapDiffDirChgNotifyDTO) row.getValue());
            }
            for (Map.Entry<KnNotificationKeyDTO, Object> row : documentByWatcher.getOrDefault(watcher, List.of())) {
                keys.add(row.getKey());
                documentChanges.add((KnXcapDiffNotifyDTO) row.getValue());
            }
            knLogger.info(methodName, "[XCAP-DEBULK-FLOW] STEP-7A Submitting one watcher bundle. watcher="
                    + com.kodiak.common.resources.KnGDPRTemplate.mdn(watcher)
                    + " directoryChanges=" + directoryChanges.size()
                    + " documentChanges=" + documentChanges.size());
            executor.submit(new KnWatcherDebulkSendNotification(keys.get(0), keys, directoryChanges, documentChanges,
                    mdnSubsInfoMap, baseMdnMap, xcapDiffNotifier));
        }
        List<KnNotificationKeyDTO> legacyKeys = new ArrayList<>();
        for (Map.Entry<KnNotificationKeyDTO, Object> row : legacyRows) {
            if (submitLegacyNotification(row, mdnSubsInfoMap, baseMdnMap)) {
                legacyKeys.add(row.getKey());
            }
        }
        if (!legacyKeys.isEmpty()) {
            xcapDiffNotifier.cleanUpRecord(legacyKeys);
            knLogger.info(methodName, "[XCAP-DEBULK-FLOW] STEP-7B Legacy rows removed after submit. rows=" + legacyKeys.size());
        }
    }

    private boolean submitLegacyNotification(Map.Entry<KnNotificationKeyDTO, Object> row,
                                             Map<String, KnXDMSubsProvDTO> mdnSubsInfoMap,
                                             Map<String, Set<String>> baseMdnMap) {
        Object payload = row.getValue();
        if (payload instanceof KnXcapDiffDirChgNotifyDTO directoryChange) {
            executor.submit(new KnXcapSendNotification(row.getKey(), directoryChange, mdnSubsInfoMap, baseMdnMap));
            return true;
        }
        if (payload instanceof KnXcapDiffNotifyDTO diffNotify) {
            executor.submit(new KnSendNotification(row.getKey(), diffNotify, mdnSubsInfoMap, baseMdnMap));
            return true;
        }
        if (payload instanceof KnMCSNotifyDTO mcsNotify) {
            executor.submit(new KnSendMCSNotify(row.getKey(), mcsNotify));
            return true;
        }
        knLogger.error("submitLegacyNotification", "[XCAP-DEBULK-FLOW] STEP-ERR Unrecognized payload returned to pending. destType="
                + row.getKey().getDestType() + " watcher="
                + com.kodiak.common.resources.KnGDPRTemplate.mdn(row.getKey().getDestId()));
        xcapDiffNotifier.revertRecordsToPending(java.util.Collections.singletonList(row.getKey()));
        return false;
    }

    @Override
    public void notify(KnStatusMgrConstants.CARD_STATES previousState, KnStatusMgrConstants.CARD_STATES currentState) {
        String methodName = "notify(KnStatusMgrConstants.CARD_STATES, KnStatusMgrConstants.CARD_STATES)";
        knLogger.info(methodName, "notify from Status Mgr [Prev State - ", previousState, "; Current State - ", currentState + "]");

        this.currentRedState = currentState;

        knLogger.debug(methodName, "Current State ", currentState);
    }

    public KnStatusMgrConstants.CARD_STATES getCurrentRedundancyStatus() {
        String methodName = "getCurrentRedundancyStatus";
        knLogger.debug(methodName, "Current Card Redundancy Status ", this.currentRedState);
        return this.currentRedState;
    }
}
