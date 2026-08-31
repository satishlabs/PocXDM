/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ******************************************************************************
 * File name  : KnWatcherDebulkSendNotification.java
 * Subsystem  : XCAP Notification Manager – Temporal Workflow Debulk
 *
 * Description:
 *   Dedicated {@link Runnable} that sends a single bundled notification to one
 *   watcher/subscriber for the current epoch window.
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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sends one bundled XCAP watcher notification for all directory-change diffs
 * accumulated during the current epoch window for a single subscriber.
 */
public class KnWatcherDebulkSendNotification implements Runnable {

    private static final KnLogger knLogger =
            KnLogger.getLogger(KnWatcherDebulkSendNotification.class);
    private static final String FLOW_TAG = "[XCAP-DEBULK-FLOW]";

    private final KnNotificationKeyDTO seqId;
    private final List<KnNotificationKeyDTO> seqIds;
    private final List<KnXcapDiffDirChgNotifyDTO> notifications;
    private final Map<String, KnXDMSubsProvDTO> mdnSubsInfoMap;
    private final Map<String, Set<String>> baseMdnsMap;
    private final KnXcapDiffNotifier xcapDiffNotifier;

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

    @Override
    public void run() {
        String methodName = "run()";

        if (notifications == null || notifications.isEmpty()) {
            knLogger.warn(methodName,
                    FLOW_TAG + " STEP-8 Empty notification bundle; nothing to send. watcher="
                            + safeValue(seqId != null ? seqId.getDestId() : null));
            return;
        }

        knLogger.info(methodName,
                FLOW_TAG + " STEP-8 Worker started. cid=" + safeValue(seqId != null ? seqId.getCid() : null)
                        + " watcher=" + safeValue(seqId != null ? seqId.getDestId() : null)
                        + " bundledCount=" + notifications.size());

        try {
            int maxPayloadBytes = resolveMaxPayloadBytes();
            knLogger.info(methodName, FLOW_TAG + " STEP-9 Resolved payload threshold. maxPayloadBytes=" + maxPayloadBytes);

            long serialisedPayloadBytes = computeSerializedSize(notifications);
            knLogger.info(methodName,
                    FLOW_TAG + " STEP-10 Bundle size measured. serialisedPayloadBytes=" + serialisedPayloadBytes
                            + " maxPayloadBytes=" + maxPayloadBytes
                            + " bundledCount=" + notifications.size());

            long distinctDocCount = countDistinctDocuments(notifications);
            boolean coreDirectoryOnly = isCoreDirectoryOnlyBundle(notifications);
            int inlineDocPayloadCount = countInlineDocPayloads(notifications);
            LinkedHashSet<String> docSelectors = collectDocumentSelectors(notifications);
            knLogger.info(methodName,
                    FLOW_TAG + " STEP-11 Bundle composition resolved. distinctDocCount=" + distinctDocCount
                            + " inlineDocPayloadCount=" + inlineDocPayloadCount
                            + " coreDirectoryOnly=" + coreDirectoryOnly
                            + " sampleDocSelectors=" + docSelectors.stream().limit(5).collect(Collectors.toList()));

            String appliedRule;
            if (coreDirectoryOnly) {
                appliedRule = "RULE-III-CORE-DIRECTORY";
                knLogger.info(methodName,
                        FLOW_TAG + " STEP-11A Rule III selected. inlineDocPayloadCount=" + inlineDocPayloadCount
                                + " distinctDocCount=" + distinctDocCount
                                + " -> using Directory Etag notification");
            } else if (distinctDocCount > 1) {
                appliedRule = "RULE-II-MULTI-DOC";
                knLogger.info(methodName,
                        FLOW_TAG + " STEP-11A Rule II selected. multipleDocuments=" + distinctDocCount
                                + " -> using Directory Etag notification");
            } else if (serialisedPayloadBytes > maxPayloadBytes) {
                appliedRule = "RULE-I-OVERSIZE-FALLBACK";
                knLogger.info(methodName,
                        FLOW_TAG + " STEP-11A Rule I fallback selected. payloadBytes=" + serialisedPayloadBytes
                                + " threshold=" + maxPayloadBytes
                                + " -> using Directory Etag notification");
            } else {
                appliedRule = "RULE-I-CONSOLIDATED";
                knLogger.info(methodName,
                        FLOW_TAG + " STEP-11A Rule I selected. payloadBytes=" + serialisedPayloadBytes
                                + " threshold=" + maxPayloadBytes
                                + " -> using consolidated diff notification");
            }

            boolean isSuccess;
            if ("RULE-I-CONSOLIDATED".equals(appliedRule)) {
                isSuccess = xcapDiffNotifier.sendXcapDiffNotifications(
                        notifications, mdnSubsInfoMap, null, baseMdnsMap);
            } else {
                xcapDiffNotifier.sendXcapDiffDirMicroserviceNotificationforEtagNotify(notifications);
                isSuccess = true;
            }

            knLogger.info(methodName,
                    FLOW_TAG + " STEP-12 Worker completed. cid=" + safeValue(seqId != null ? seqId.getCid() : null)
                            + " watcher=" + safeValue(seqId != null ? seqId.getDestId() : null)
                            + " bundledCount=" + notifications.size()
                            + " appliedRule=" + appliedRule
                            + " isSuccess=" + isSuccess);

            if (isSuccess && seqIds != null && !seqIds.isEmpty()) {
                xcapDiffNotifier.cleanUpRecord(seqIds);
                knLogger.info(methodName, FLOW_TAG + " STEP-12A Worker queue cleanup complete. cleanedRows=" + seqIds.size()
                        + " watcher=" + safeValue(seqId != null ? seqId.getDestId() : null));
            } else if (!isSuccess) {
                handleRetryOnSendFailure(methodName);
            }

        } catch (Exception e) {
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
        revertSeqIdsToPending(seqIds);
        if (seqId != null && seqId.getDestId() != null) {
            resetWatcherTrackerEpoch(Collections.singletonList(seqId.getDestId().trim()),
                    resolveNotificationPeriodMillis());
        }
    }

    private void revertSeqIdsToPending(List<KnNotificationKeyDTO> keys) {
        KnPersisterTxn persisterTxn = null;
        PreparedStatement pStmt = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            Connection connection = persisterTxn.getDBConnection(
                    KnDbUtil.getDBConfigInfo().getLocalPttId(), false);
            String updateQry =
                    "UPDATE DG.XCAP_PENDING_NOTIFYQ SET NOTIFY_STATUS = ? "
                    + "WHERE NOTIFY_STATUS = ? AND DEST_ID = ? AND INSERTION_TIME = ? AND DEST_TYPE = ?";
            pStmt = connection.prepareStatement(updateQry);
            for (KnNotificationKeyDTO key : keys) {
                pStmt.setInt(1, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
                pStmt.setInt(2, KnXcapNotifyConstants.NOTIFYSTATUS.NOTIFY_INITIATED.value());
                pStmt.setString(3, key.getDestId());
                pStmt.setLong(4, key.getInsertionTime());
                pStmt.setInt(5, key.getDestType());
                pStmt.addBatch();
            }
            pStmt.executeBatch();
            persisterTxn.save();
        } catch (Exception e) {
            knLogger.warn("revertSeqIdsToPending", FLOW_TAG + " STEP-12B Queue revert failed", e);
            if (persisterTxn != null) {
                try {
                    persisterTxn.rollback();
                } catch (Exception rollbackEx) {
                    knLogger.warn("revertSeqIdsToPending", "Rollback failed after queue revert error", rollbackEx);
                }
            }
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    private void resetWatcherTrackerEpoch(List<String> mdns, long periodMillis) {
        if (mdns == null || mdns.isEmpty()) {
            return;
        }
        long retryEligibleTs = System.currentTimeMillis() - periodMillis;
        KnPersisterTxn persisterTxn = null;
        PreparedStatement pStmt = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
            Connection connection;
            try {
                connection = persisterTxn.getDBConnection(localPttId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            } catch (Exception e) {
                connection = persisterTxn.getDBConnection(localPttId, false);
            }
            String updateSql = "UPDATE DG.MDN_NOTIFY_TRACKER SET LAST_NOTIFIED_TIME = ? WHERE MDN = ?";
            pStmt = connection.prepareStatement(updateSql);
            for (String mdn : mdns) {
                if (mdn == null || mdn.trim().isEmpty()) {
                    continue;
                }
                pStmt.setLong(1, retryEligibleTs);
                pStmt.setString(2, mdn.trim());
                pStmt.addBatch();
            }
            pStmt.executeBatch();
            persisterTxn.save();
            knLogger.info("resetWatcherTrackerEpoch", FLOW_TAG + " STEP-12C Tracker epoch reset for retry. mdnCount="
                    + mdns.size() + " retryEligibleTs=" + retryEligibleTs);
        } catch (Exception e) {
            knLogger.warn("resetWatcherTrackerEpoch", FLOW_TAG + " STEP-12C Tracker epoch reset failed", e);
            if (persisterTxn != null) {
                try {
                    persisterTxn.rollback();
                } catch (Exception rollbackEx) {
                    knLogger.warn("resetWatcherTrackerEpoch", "Rollback failed after tracker reset error", rollbackEx);
                }
            }
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

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

    private String safeValue(String value) {
        return value != null ? value : "null";
    }
}
