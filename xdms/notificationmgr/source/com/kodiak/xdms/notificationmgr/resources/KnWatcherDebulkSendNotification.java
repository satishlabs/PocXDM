/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ******************************************************************************
 * File name  : KnWatcherDebulkSendNotification.java
 * Subsystem  : XCAP Notification Manager – Watcher Debulk
 *
 * Description:
 *   Dedicated {@link Runnable} that sends a single bundled notification to one
 *   watcher/subscriber for the current notification collection window.
 *
 *   The worker is the final "Send Together" stage of the debulk pipeline:
 *   1. measure the bundled payload
 *   2. determine whether the bundle is single-doc or multi-doc
 *   3. choose the appropriate send strategy
 *   4. dispatch one watcher notification
 *
 *   Rule summary:
 *   - Rule I: single document with multiple diffs → consolidated diff notification
 *   - Rule I fallback: if the payload is too large → directory-etag notification
 *   - Rule II: multiple documents → directory-etag notification
 *   - Rule III: core directory changes → directory-etag notification
 *
 * Execution:
 *   Submitted to the shared {@code XCAP_NOTIFY_SENDER} executor from
 *   {@link KnXcapNotifyProcessor#process}. One instance is created per watcher
 *   per epoch cycle.
 * ******************************************************************************
 */
package com.kodiak.xdms.notificationmgr.resources;

import com.kodiak.common.commdto.common.KnXDMSubsProvDTO;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDocDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnNotificationKeyDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sends one bundled XCAP watcher notification for all directory-change diffs
 * accumulated during the current epoch window for a single subscriber.
 *
 * <h3>Send-time rules</h3>
 * <ul>
 *   <li><b>Rule I – Single document, multiple diffs</b>: send the consolidated
 *       diff unless the payload exceeds {@code XCAP_DIFF_PAYLOAD_SIZE}; if it does,
 *       fall back to a directory-etag notification.</li>
 *   <li><b>Rule II – Multiple documents, multiple diffs</b>: always use the
 *       directory-etag notification path.</li>
 *   <li><b>Rule III – Core directory changes</b>: no inline diffs are emitted;
 *       always use the directory-etag notification path.</li>
 * </ul>
 */
public class KnWatcherDebulkSendNotification implements Runnable {

    private static final KnLogger knLogger =
            KnLogger.getLogger(KnWatcherDebulkSendNotification.class);
    private static final String FLOW_TAG = "[XCAP-DEBULK-FLOW]";

    // ─────────────────────────────────────────────────────────────────────────────────
    // Constructor parameters  (immutable once assigned)
    // ────────────────────────────────────────────────────────────────────────────────

    /** Composite key that identifies the source queue row (watcher, dest-type, CID …). */
    private final KnNotificationKeyDTO seqId;

    /** All queue keys consumed for this watcher bundle in the current poll cycle. */
    private final List<KnNotificationKeyDTO> seqIds;

    /**
     * All directory-change DTOs accumulated for this watcher during the current epoch.
     * The list is expected to contain at least one element.
     */
    private final List<KnXcapDiffDirChgNotifyDTO> notifications;

    /** Subscriber profile map keyed by MDN; used by the notifier during send. */
    private final Map<String, KnXDMSubsProvDTO> mdnSubsInfoMap;

    /** Base-MDN map required by the underlying notifier logic. */
    private final Map<String, Set<String>> baseMdnsMap;

    /**
     * Reference to the notifier that performs the actual DB/network send.
     * Injected so callers can supply a mock in tests.
     */
    private final KnXcapDiffNotifier xcapDiffNotifier;

    // ─────────────────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────────────────

    /**
     * Constructs a new debulk send task for a single watcher.
     *
     * @param seqId           composite key for this watcher's queue rows
     * @param notifications   bundled list of directory-change DTOs for the epoch
     * @param mdnSubsInfoMap  subscriber profile lookup map
     * @param baseMdnsMap     base-MDN lookup map
     * @param xcapDiffNotifier notifier instance that performs the actual send
     */
    public KnWatcherDebulkSendNotification(KnNotificationKeyDTO seqId,
                                           List<KnNotificationKeyDTO> seqIds,
                                           List<KnXcapDiffDirChgNotifyDTO> notifications,
                                           Map<String, KnXDMSubsProvDTO> mdnSubsInfoMap,
                                           Map<String, Set<String>> baseMdnsMap,
                                           KnXcapDiffNotifier xcapDiffNotifier) {
        this.seqId = seqId;
        this.seqIds = seqIds;
        this.notifications = notifications;
        this.mdnSubsInfoMap = mdnSubsInfoMap;
        this.baseMdnsMap = baseMdnsMap;
        this.xcapDiffNotifier = xcapDiffNotifier;
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    // Runnable implementation
    // ─────────────────────────────────────────────────────────────────────────────────

    /**
     * Executes the bundled send for this watcher.
     *
     * <ol>
     *   <li>Validate the bundle is not empty.</li>
     *   <li>Resolve the configured payload threshold.</li>
     *   <li>Measure the serialized bundle size.</li>
     *   <li>Count distinct documents to select the rule path.</li>
     *   <li>Dispatch one bundled watcher notification.</li>
     *   <li>Log the final outcome with the watcher key and bundled row count.</li>
     * </ol>
     */
    @Override
    public void run() {
        String methodName = "run()";

        // ── Guard: nothing to send ────────────────────────────────────────────────────
        if (notifications == null || notifications.isEmpty()) {
            knLogger.warn(methodName,
                    FLOW_TAG + " STEP-8 Empty notification bundle; nothing to send. watcher="
                            + safeValue(seqId != null ? seqId.getDestId() : null));
            return;
        }

        knLogger.info(methodName,
                FLOW_TAG + " STEP-8 Worker started. cid=" + safeValue(seqId != null ? seqId.getCid() : null)
                        + " watcher=" + safeValue(seqId != null ? seqId.getDestId() : null)
                        + " destType=" + (seqId != null ? seqId.getDestType() : -1)
                        + " bundledCount=" + notifications.size());

        try {
            if (seqId != null && seqId.getDestType() == KnXcapNotifyConstants.DESTTYPE.GROUP.value()) {
                int ntfy = 0;
                for (KnXcapDiffDirChgNotifyDTO dto : notifications) {
                    if (dto != null) {
                        dto.setNtfyOnAnyMDN(ntfy);
                    }
                }
            }
            int maxPayloadBytes = resolveMaxPayloadBytes();
            knLogger.info(methodName, FLOW_TAG + " STEP-9 Resolved payload threshold. maxPayloadBytes=" + maxPayloadBytes);

            // ── Step 2: Measure the serialized bundle size ────────────────────────────
            long serialisedPayloadBytes = computeSerializedSize(notifications);
            knLogger.info(methodName,
                    FLOW_TAG + " STEP-10 Bundle size measured. serialisedPayloadBytes=" + serialisedPayloadBytes
                            + " maxPayloadBytes=" + maxPayloadBytes
                            + " bundledCount=" + notifications.size());

            // ── Step 3: Count distinct documents in the bundle ────────────────────────
            long distinctDocCount = countDistinctDocuments(notifications);
            boolean coreDirectoryOnly = isCoreDirectoryOnlyBundle(notifications);
            int inlineDocPayloadCount = countInlineDocPayloads(notifications);
            LinkedHashSet<String> docSelectors = collectDocumentSelectors(notifications);
            knLogger.info(methodName,
                    FLOW_TAG + " STEP-11 Bundle composition resolved. distinctDocCount=" + distinctDocCount
                            + " inlineDocPayloadCount=" + inlineDocPayloadCount
                            + " coreDirectoryOnly=" + coreDirectoryOnly
                            + " sampleDocSelectors=" + docSelectors.stream().limit(5).collect(Collectors.toList()));

            // ── Step 4: Decide the send strategy ──────────────────────────────────────
            String appliedRule = determineRule(notifications, serialisedPayloadBytes, maxPayloadBytes);
            if (coreDirectoryOnly) {
                knLogger.info(methodName,
                        FLOW_TAG + " STEP-11A Rule III selected. inlineDocPayloadCount=" + inlineDocPayloadCount
                                + " distinctDocCount=" + distinctDocCount
                                + " -> using Directory Etag notification");
            } else if (distinctDocCount > 1) {
                knLogger.info(methodName,
                        FLOW_TAG + " STEP-11A Rule II selected. multipleDocuments=" + distinctDocCount
                                + " -> using Directory Etag notification");
            } else if (serialisedPayloadBytes > maxPayloadBytes) {
                knLogger.info(methodName,
                        FLOW_TAG + " STEP-11A Rule I fallback selected. payloadBytes=" + serialisedPayloadBytes
                                + " threshold=" + maxPayloadBytes
                                + " -> using Directory Etag notification");
            } else {
                knLogger.info(methodName,
                        FLOW_TAG + " STEP-11A Rule I selected. payloadBytes=" + serialisedPayloadBytes
                                + " threshold=" + maxPayloadBytes
                                + " -> using consolidated diff notification");
            }

            // ── Step 5: Dispatch one bundled watcher notification ─────────────────────
            // Route to the correct send path based on the chosen rule:
            //   RULE-I-CONSOLIDATED   → inline consolidated diff notification
            //   All other rules       → directory-etag notification (no inline payload)
            boolean isSuccess;
            if ("RULE-I-CONSOLIDATED".equals(appliedRule)) {
                isSuccess = xcapDiffNotifier.sendXcapDiffNotifications(
                        notifications, mdnSubsInfoMap, null, baseMdnsMap);
            } else {
                List<KnXcapDiffDirChgNotifyDTO> directoryEtagBundle = toDirectoryEtagBundle(notifications);
                isSuccess = xcapDiffNotifier.sendXcapDiffNotifications(
                        directoryEtagBundle, mdnSubsInfoMap, null, baseMdnsMap);
            }

            // ── Step 6: Log outcome ─────────────���────────────────────────────────────
            String allDirUris = notifications.stream()
                    .filter(n -> n != null && n.getDirURI() != null)
                    .map(KnXcapDiffDirChgNotifyDTO::getDirURI)
                    .distinct()
                    .collect(Collectors.joining(","));
            knLogger.info(methodName,
                    FLOW_TAG + " STEP-12 Worker completed. cid=" + safeValue(seqId != null ? seqId.getCid() : null)
                            + " watcher=" + safeValue(seqId != null ? seqId.getDestId() : null)
                            + " destType=" + (seqId != null ? seqId.getDestType() : -1)
                            + " ntfy=0"
                            + " dirURI=" + allDirUris
                            + " bundledCount=" + notifications.size()
                            + " appliedRule=" + appliedRule
                            + " isSuccess=" + isSuccess);

            // Cleanup only after a successful send so failures stay eligible for retry.
            if (isSuccess && seqIds != null && !seqIds.isEmpty()) {
                xcapDiffNotifier.cleanUpRecord(seqIds);
                knLogger.info(methodName, FLOW_TAG + " STEP-12A Worker queue cleanup complete. cleanedRows=" + seqIds.size()
                        + " watcher=" + safeValue(seqId != null ? seqId.getDestId() : null));

                // Eagerly remove the MDN_NOTIFY_TRACKER row so the MDN does not
                // re-appear as eligible until a new operation inserts it again.
                // This prevents spurious empty-batch cycles and keeps the tracker bounded.
                if (seqId != null && seqId.getDestId() != null
                        && seqId.getDestType() == KnXcapNotifyConstants.DESTTYPE.GROUP.value()) {
                    xcapDiffNotifier.cleanupTrackerForMdn(
                            Collections.singletonList(seqId.getDestId().trim()));
                    knLogger.info(methodName, FLOW_TAG + " STEP-12B Tracker row removed for watcher="
                            + seqId.getDestId());
                }
            } else if (!isSuccess) {
                handleRetryOnSendFailure(methodName);
            }

        } catch (Exception e) {
            // Catch-all: one watcher failure should not terminate the worker thread.
            knLogger.error(methodName,
                    FLOW_TAG + " STEP-ERR Failed debulk send for watcher. cid="
                            + safeValue(seqId != null ? seqId.getCid() : null)
                            + " watcher=" + safeValue(seqId != null ? seqId.getDestId() : null), e);
            handleRetryOnSendFailure(methodName);
        }
    }

    private void handleRetryOnSendFailure(String callerMethod) {
        if (seqIds == null || seqIds.isEmpty()) {
            knLogger.warn(callerMethod, FLOW_TAG + " STEP-12B Retry skipped — no seqIds to revert");
            return;
        }
        knLogger.warn(callerMethod, FLOW_TAG + " STEP-12B Send failed; reverting queue rows to PENDING. watcher="
                + safeValue(seqId != null ? seqId.getDestId() : null)
                + " rowCount=" + seqIds.size());
        xcapDiffNotifier.revertRecordsToPending(seqIds);
        if (seqId != null && seqId.getDestId() != null) {
            long periodMillis = resolveNotificationPeriodMillis();
            xcapDiffNotifier.resetMdnNotifyTrackerEpochForRetry(
                    Collections.singletonList(seqId.getDestId().trim()), periodMillis);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────────────

    /**
     * Determines the notification send rule for the given bundle.
     *
     * <p>Package-private to allow direct testing without mocking the full send path.</p>
     *
     * @param notifications        accumulated DTOs for this watcher
     * @param serialisedBytes      pre-computed serialized size
     * @param maxPayloadBytes      configured threshold from XCAP_DIFF_PAYLOAD_SIZE
     * @return one of: "RULE-III-CORE-DIRECTORY", "RULE-II-MULTI-DOC",
     *                 "RULE-I-OVERSIZE-FALLBACK", "RULE-I-CONSOLIDATED"
     */
    private List<KnXcapDiffDirChgNotifyDTO> toDirectoryEtagBundle(List<KnXcapDiffDirChgNotifyDTO> notifications) {
        List<KnXcapDiffDirChgNotifyDTO> etagBundle = new ArrayList<>();
        for (KnXcapDiffDirChgNotifyDTO source : notifications) {
            if (source == null) {
                continue;
            }
            KnXcapDiffDirChgNotifyDTO copy = new KnXcapDiffDirChgNotifyDTO();
            copy.setMdn(source.getMdn());
            copy.setPocHome(source.getPocHome());
            copy.setPresenceHome(source.getPresenceHome());
            copy.setXcapRootUri(source.getXcapRootUri());
            copy.setDirURI(source.getDirURI());
            copy.setDirPrevEtag(source.getDirPrevEtag());
            copy.setDirNewEtag(source.getDirNewEtag());
            copy.setProtocolVersion(source.getProtocolVersion());
            copy.setNtfyOnAnyMDN(source.getNtfyOnAnyMDN());
            copy.setNotfnCapability(source.isNotfnCapability());
            copy.setPushNotifyEnabled(source.isPushNotifyEnabled());
            copy.setDocDiffObj(Collections.emptyList());
            etagBundle.add(copy);
        }
        return etagBundle;
    }

    static String determineRule(List<KnXcapDiffDirChgNotifyDTO> notifications,
                                long serialisedBytes,
                                int maxPayloadBytes) {
        boolean coreDirectoryOnly = notifications.stream()
                .allMatch(dto -> dto.getDocDiffObj() == null || dto.getDocDiffObj().isEmpty());
        if (coreDirectoryOnly) {
            return "RULE-III-CORE-DIRECTORY";
        }
        long distinctDocCount = notifications.stream()
                .filter(dto -> dto.getDocDiffObj() != null)
                .flatMap(dto -> dto.getDocDiffObj().stream())
                .map(KnXcapDiffDocDTO::getDocumentSelector)
                .filter(sel -> sel != null && !sel.trim().isEmpty())
                .distinct()
                .count();
        if (distinctDocCount > 1) {
            return "RULE-II-MULTI-DOC";
        }
        if (serialisedBytes > maxPayloadBytes) {
            return "RULE-I-OVERSIZE-FALLBACK";
        }
        return "RULE-I-CONSOLIDATED";
    }

    /**
     * Reads {@code XCAP_DIFF_PAYLOAD_SIZE} from microservices config.
     * Falls back to 2048 bytes on any error.
     *
     * @return maximum payload size in bytes
     */
    private int resolveMaxPayloadBytes() {
        String methodName = "resolveMaxPayloadBytes";
        try {
            String clusterIdEnv = System.getenv(KnConstants.CLUSTERID_ENV_NAME);
            int clusterId = Integer.parseInt(clusterIdEnv);
            Map<String, String> configMap =
                    KnGenInfoUtil.getInstance().retrieveMSSvcsCommonConfig(clusterId);
            String raw = configMap.get(KnConstants.XCAP_DIFF_PAYLOAD_SIZE);
            int effectiveValue = (raw != null) ? Integer.parseInt(raw) : 2048;
            knLogger.info(methodName,
                    FLOW_TAG + " STEP-9A Payload threshold config resolved. clusterId=" + clusterIdEnv
                            + " rawValue=" + raw + " effectiveValue=" + effectiveValue);
            return effectiveValue;
        } catch (Exception e) {
            knLogger.warn(methodName,
                    FLOW_TAG + " STEP-9A Payload threshold config fallback engaged. defaultValue=2048 cause="
                            + e.getMessage(), e);
            return 2048;
        }
    }

    private long resolveNotificationPeriodMillis() {
        try {
            String clusterIdEnv = System.getenv(KnConstants.CLUSTERID_ENV_NAME);
            int clusterId = Integer.parseInt(clusterIdEnv);
            Map<String, String> configMap =
                    KnGenInfoUtil.getInstance().retrieveMSSvcsCommonConfig(clusterId);
            String raw = configMap.get(KnConstants.XCAP_NOTIFICATION_PERIOD);
            int periodSeconds = (raw != null) ? Integer.parseInt(raw) : 120;
            return (long) periodSeconds * 1000L;
        } catch (Exception e) {
            return 120_000L;
        }
    }


    /**
     * Computes the approximate serialized byte size of the notification list.
     *
     * <p>Uses Java object serialization so the result is an upper bound for network
     * payload size purposes. The actual XML payload will be smaller; using the
     * serialized size is a conservative (safe) choice.</p>
     *
     * @param notifications list to measure
     * @return byte count, or {@code Long.MAX_VALUE} on serialization failure
     *         (which safely triggers the fallback path)
     */
    private long computeSerializedSize(List<KnXcapDiffDirChgNotifyDTO> notifications) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(notifications);
            oos.flush();
            return baos.size();
        } catch (IOException e) {
            knLogger.warn("computeSerializedSize",
                    FLOW_TAG + " Serialization failed; returning MAX_VALUE to trigger fallback", e);
            return Long.MAX_VALUE;
        }
    }

    /**
     * Counts the number of distinct document selectors present across all notifications.
     *
     * <p>A count greater than one means the bundle spans multiple documents, so
     * Rule II applies and the worker should fall back to the directory-etag path.</p>
     *
     * @param notifications notification list to inspect
     * @return distinct document count (0 if none carry inline diffs)
     */
    private long countDistinctDocuments(List<KnXcapDiffDirChgNotifyDTO> notifications) {
        return notifications.stream()
                .filter(dto -> dto.getDocDiffObj() != null)
                .flatMap(dto -> dto.getDocDiffObj().stream())
                .map(KnXcapDiffDocDTO::getDocumentSelector)
                .filter(sel -> sel != null && !sel.trim().isEmpty())
                .distinct()
                .count();
    }

    private boolean isCoreDirectoryOnlyBundle(List<KnXcapDiffDirChgNotifyDTO> notifications) {
        return notifications.stream()
                .allMatch(dto -> dto.getDocDiffObj() == null || dto.getDocDiffObj().isEmpty());
    }

    private int countInlineDocPayloads(List<KnXcapDiffDirChgNotifyDTO> notifications) {
        return (int) notifications.stream()
                .filter(dto -> dto.getDocDiffObj() != null && !dto.getDocDiffObj().isEmpty())
                .count();
    }

    private LinkedHashSet<String> collectDocumentSelectors(List<KnXcapDiffDirChgNotifyDTO> notifications) {
        return notifications.stream()
                .filter(dto -> dto.getDocDiffObj() != null)
                .flatMap(dto -> dto.getDocDiffObj().stream())
                .map(KnXcapDiffDocDTO::getDocumentSelector)
                .filter(sel -> sel != null && !sel.trim().isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Normalizes a possibly null value for log output.
     *
     * @param value raw value
     * @return the original value or the literal string "null"
     */
    private String safeValue(String value) {
        return value != null ? value : "null";
    }
}
