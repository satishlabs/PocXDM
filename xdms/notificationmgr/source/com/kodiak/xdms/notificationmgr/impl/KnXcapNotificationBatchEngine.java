/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * Wait-and-bundle pipeline for XCAP notifications to dispatcher and handset watchers.
 *
 * A subscriber profile, contact, or group change is saved first. The account that
 * changed receives its notification immediately. A different account watching that
 * subscriber is held for XCAP_NOTIFICATION_PERIOD seconds. Changes for that watcher
 * share one LAST_NOTIFIED_TIME. When the window closes, one worker sends one
 * notification to that watcher.
 *
 * DG.XCAP_PENDING_NOTIFYQ is on the primary TimesTen DSN (for example DG_010011).
 * DG.MDN_NOTIFY_TRACKER is on the secondary DSN, local PTT id plus "_6"
 * (XDM_SHARED_DATA, for example DG_010011_6).
 * LAST_NOTIFIED_TIME is BIGINT epoch milliseconds. MDN is CHAR(15).
 * The tables are never joined in one SQL statement.
 *
 * The active-card check stays in KnXcapNotifyProcessor, the only caller of pollAndClaim().
 * When XCAP_NOTIFICATION_OPTIMIZED is off, this class does not write the tracker and
 * pollRecord() keeps the existing one-row sender.
 */
package com.kodiak.xdms.notificationmgr.impl;

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.notificationmgr.resources.KnXcapNotifyConstants;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnNotificationKeyDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.common.resources.KnConstants.XCAP_NOTIFY_SUPPRESS_MDN_BATCH;

public class KnXcapNotificationBatchEngine {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXcapNotificationBatchEngine.class);
    static final String FLOW_TAG = "[XCAP-DEBULK-FLOW]";

    static final int DEFAULT_PERIOD_SECONDS = 120;
    static final int DEFAULT_PAYLOAD_BYTES = 2048;
    /** Config keys read from retrieveMSSvcsCommonConfig. Kept here so this class does not depend on newer KnConstants fields. */
    private static final String XCAP_NOTIFICATION_OPTIMIZED = "XCAP_NOTIFICATION_OPTIMIZED";
    private static final String XCAP_NOTIFICATION_PERIOD = "XCAP_NOTIFICATION_PERIOD";
    private static final String XCAP_DIFF_PAYLOAD_SIZE = "XCAP_DIFF_PAYLOAD_SIZE";
    static final int MAX_MDNS_PER_POLL = 100;
    /** Live secondary column DG.MDN_NOTIFY_TRACKER.MDN is CHAR(15). */
    static final int TRACKER_MDN_LENGTH = 15;

    private static final String PROBE_TRACKER =
            "SELECT FIRST 1 MDN FROM DG.MDN_NOTIFY_TRACKER";
    /** Insert the watcher only when absent. An existing row keeps LAST_NOTIFIED_TIME. */
    private static final String INSERT_IF_ABSENT =
            "INSERT INTO DG.MDN_NOTIFY_TRACKER (MDN, LAST_NOTIFIED_TIME) "
                    + "SELECT ?, ? FROM DUAL WHERE NOT EXISTS ("
                    + "SELECT 1 FROM DG.MDN_NOTIFY_TRACKER WHERE MDN = ?)";
    private static final String SELECT_AGED_TRACKER =
            "SELECT FIRST 500 MDN FROM DG.MDN_NOTIFY_TRACKER "
                    + "WHERE LAST_NOTIFIED_TIME <= ? ORDER BY LAST_NOTIFIED_TIME";
    private static final String SELECT_PENDING_DEST_IDS =
            "SELECT FIRST 200 DEST_ID FROM DG.XCAP_PENDING_NOTIFYQ "
                    + "WHERE NOTIFY_STATUS = ? AND DEST_TYPE <> ? AND DEST_ID IS NOT NULL "
                    + "ORDER BY INSERTION_TIME";
    /** Oldest pending rows, including OPS_CODE so own-account changes can be released immediately. */
    private static final String SELECT_CLASSIFIABLE_PENDING =
            "SELECT FIRST 500 INSERTION_TIME, DEST_ID, DEST_TYPE, PAYLOAD, MSG_TYPE, CID, OPS_CODE "
                    + "FROM DG.XCAP_PENDING_NOTIFYQ WHERE NOTIFY_STATUS = ? AND DEST_TYPE <> ? "
                    + "ORDER BY INSERTION_TIME";
    private static final String SELECT_EXPIRED_TRACKER =
            "SELECT FIRST 500 MDN FROM DG.MDN_NOTIFY_TRACKER WHERE LAST_NOTIFIED_TIME <= ?";
    private static final String TOUCH_TRACKER =
            "UPDATE DG.MDN_NOTIFY_TRACKER SET LAST_NOTIFIED_TIME = ? WHERE MDN = ?";
    private static final String RESET_TRACKER =
            "UPDATE DG.MDN_NOTIFY_TRACKER SET LAST_NOTIFIED_TIME = ? WHERE MDN = ?";
    private static final String DELETE_TRACKER =
            "DELETE FROM DG.MDN_NOTIFY_TRACKER WHERE MDN = ?";
    private static final String MARK_IN_PROGRESS =
            "UPDATE DG.XCAP_PENDING_NOTIFYQ SET NOTIFY_STATUS = ? "
                    + "WHERE DEST_ID = ? AND INSERTION_TIME = ? AND DEST_TYPE = ? AND NOTIFY_STATUS = ?";
    private static final String REVERT_TO_PENDING =
            "UPDATE DG.XCAP_PENDING_NOTIFYQ SET NOTIFY_STATUS = ? "
                    + "WHERE DEST_ID = ? AND INSERTION_TIME = ? AND DEST_TYPE = ? AND NOTIFY_STATUS = ?";
    private static final String SELECT_SUPPRESSED =
            "SELECT FIRST 100 INSERTION_TIME, DEST_ID, DEST_TYPE, PAYLOAD, MSG_TYPE, PARAM1, CID "
                    + "FROM DG.XCAP_PENDING_NOTIFYQ WHERE NOTIFY_STATUS = ? AND DEST_TYPE = ? "
                    + "ORDER BY INSERTION_TIME";

    private final Object tableLock = new Object();
    private volatile boolean tableReady;
    private volatile String lastLoggedFlag;

    /**
     * Missing, 0, or any value other than 1 or true leaves the legacy sender in place.
     */
    public boolean isOptimized() {
        String raw = readConfig(XCAP_NOTIFICATION_OPTIMIZED);
        boolean enabled = raw != null && ("1".equals(raw.trim()) || "true".equalsIgnoreCase(raw.trim()));
        if (raw == null ? lastLoggedFlag != null : !raw.equals(lastLoggedFlag)) {
            lastLoggedFlag = raw;
            knLogger.info("isOptimized",
                    FLOW_TAG + " STEP-CONF1 XCAP_NOTIFICATION_OPTIMIZED rawValue=" + raw
                            + " enabled=" + enabled
                            + " periodSeconds=" + notificationPeriodSeconds()
                            + " payloadBytes=" + diffPayloadLimitBytes());
        }
        return enabled;
    }

    public int notificationPeriodSeconds() {
        return positiveInt(readConfig(XCAP_NOTIFICATION_PERIOD), DEFAULT_PERIOD_SECONDS, "notificationPeriodSeconds");
    }

    public int diffPayloadLimitBytes() {
        return positiveInt(readConfig(XCAP_DIFF_PAYLOAD_SIZE), DEFAULT_PAYLOAD_BYTES, "diffPayloadLimitBytes");
    }

    /**
     * Section 2.3. First change for a watcher inserts LAST_NOTIFIED_TIME = now on the
     * secondary database. A later change for the same watcher does nothing, so the
     * window does not slide forward. Own transaction: a tracker failure leaves the
     * primary queue row pending.
     */
    public void recordWatchers(Collection<String> mdns) {
        String methodName = "recordWatchers";
        if (!isOptimized() || mdns == null || mdns.isEmpty()) {
            return;
        }
        LinkedHashSet<String> distinct = new LinkedHashSet<>();
        for (String mdn : mdns) {
            if (mdn != null && !mdn.trim().isEmpty()) {
                distinct.add(mdn.trim());
            }
        }
        if (distinct.isEmpty()) {
            return;
        }
        if (!ensureTrackerTable()) {
            knLogger.error(methodName, FLOW_TAG + " STEP-ERR Tracker table is not available. "
                    + "Queued notifications stay pending and will be recovered on the next poll.");
            return;
        }
        KnPersisterTxn txn = null;
        PreparedStatement statement = null;
        try {
            txn = KnPersisterTxn.getPersisterTxn();
            txn.open();
            Connection connection = secondaryConnection(txn);
            statement = connection.prepareStatement(INSERT_IF_ABSENT);
            long nowMillis = System.currentTimeMillis();
            for (String mdn : distinct) {
                if (mdn.length() > TRACKER_MDN_LENGTH) {
                    knLogger.error(methodName, FLOW_TAG + " STEP-ERR Watcher MDN is longer than CHAR("
                            + TRACKER_MDN_LENGTH + ") and was not tracked. watcher=" + KnGDPRTemplate.mdn(mdn));
                    continue;
                }
                statement.setString(1, mdn);
                statement.setLong(2, nowMillis);
                statement.setString(3, mdn);
                statement.addBatch();
            }
            int[] counts = statement.executeBatch();
            KnDbUtil.closeStatement(statement);
            statement = null;
            int periodSeconds = notificationPeriodSeconds();
            logWatcherHold(connection, new ArrayList<>(distinct), counts, periodSeconds);
            txn.save();
            int inserted = 0;
            for (int count : counts) {
                if (count > 0) {
                    inserted++;
                }
            }
            knLogger.info(methodName,
                    FLOW_TAG + " STEP-TRK1 Tracker insert complete. requested=" + distinct.size()
                            + " inserted=" + inserted
                            + " alreadyTracked=" + (distinct.size() - inserted)
                            + " holdSeconds=" + periodSeconds
                            + " watchers=" + maskMdns(distinct));
        } catch (Exception e) {
            rollback(txn);
            knLogger.error(methodName,
                    FLOW_TAG + " STEP-ERR Tracker insert failed. Queue rows stay pending and "
                            + "will be claimed as orphans. watchers=" + maskMdns(distinct), e);
        } finally {
            KnDbUtil.closeStatement(statement);
        }
    }

    /**
     * Section 2.4.1.1. Own-account rows are claimed on this poll. Watcher rows are claimed
     * only after LAST_NOTIFIED_TIME is at or before now minus XCAP_NOTIFICATION_PERIOD.
     * At most 100 watcher recipients are claimed. Their tracker time moves to now.
     */
    public Map<KnNotificationKeyDTO, Object> pollAndClaim() {
        String methodName = "pollAndClaim";
        Map<KnNotificationKeyDTO, Object> claimed = new LinkedHashMap<>();
        if (!ensureTrackerTable()) {
            knLogger.error(methodName, FLOW_TAG + " STEP-ERR Poll skipped. Tracker table is not available.");
            return claimed;
        }
        int periodSeconds = notificationPeriodSeconds();
        long cutoff = System.currentTimeMillis() - (periodSeconds * 1000L);
        KnPersisterTxn txn = null;
        try {
            txn = KnPersisterTxn.getPersisterTxn();
            txn.open();
            Connection primary = primaryConnection(txn);
            Connection secondary = secondaryConnection(txn);
            LinkedHashSet<String> delayedWatchers = new LinkedHashSet<>();
            claimed.putAll(claimByRecipient(primary, secondary, cutoff, delayedWatchers));
            int suppressed = appendSuppressed(primary, claimed);
            if (!claimed.isEmpty()) {
                if (!delayedWatchers.isEmpty()) {
                    logWatcherRelease(secondary, delayedWatchers, claimed, periodSeconds);
                }
                markInProgress(primary, claimed.keySet());
                touchTracker(secondary, delayedWatchers);
            }
            int removed = housekeepTracker(primary, secondary, cutoff);
            txn.save();
            if (!claimed.isEmpty() || removed > 0) {
                knLogger.info(methodName, FLOW_TAG + " STEP-SCH5 Poll cycle committed. claimedRows="
                        + claimed.size() + " delayedWatcherCount=" + delayedWatchers.size()
                        + " suppressedRows=" + suppressed + " trackerRowsRemoved=" + removed);
            }
        } catch (Exception e) {
            rollback(txn);
            claimed.clear();
            knLogger.error(methodName, FLOW_TAG + " STEP-ERR Poll cycle rolled back. No rows were claimed.", e);
        }
        return claimed;
    }

    public void removeTrackers(Collection<String> mdns) {
        List<String> idle = mdnsWithoutQueueRows(mdns);
        updateByMdn("removeTrackers", DELETE_TRACKER, idle, null,
                FLOW_TAG + " STEP-TRK2 Tracker rows removed after successful send.");
    }

    /**
     * A successful own-account send must not close a watcher window that still has
     * pending or in-progress queue rows for the same recipient.
     */
    private List<String> mdnsWithoutQueueRows(Collection<String> mdns) {
        List<String> values = trimmed(mdns);
        if (values.isEmpty()) {
            return values;
        }
        KnPersisterTxn txn = null;
        try {
            txn = KnPersisterTxn.getPersisterTxn();
            txn.open();
            Set<String> stillQueued = queueDestIds(primaryConnection(txn), values, false);
            rollback(txn);
            List<String> idle = new ArrayList<>();
            for (String mdn : values) {
                if (!stillQueued.contains(mdn)) {
                    idle.add(mdn);
                }
            }
            if (idle.size() != values.size()) {
                knLogger.info("removeTrackers", FLOW_TAG + " STEP-TRK2 Tracker kept. Other queue rows are still open for this recipient. kept="
                        + (values.size() - idle.size()));
            }
            return idle;
        } catch (Exception e) {
            rollback(txn);
            knLogger.error("removeTrackers", FLOW_TAG + " STEP-ERR Tracker delete skipped. Queue state could not be read.", e);
            return Collections.emptyList();
        }
    }

    public void reopenWindowForRetry(Collection<String> mdns, long periodMillis) {
        long retryFrom = System.currentTimeMillis() - Math.max(periodMillis, 0L);
        updateByMdn("reopenWindowForRetry", RESET_TRACKER, mdns, retryFrom,
                FLOW_TAG + " STEP-TRK3 Tracker window reopened for retry. retryFromEpochMillis=" + retryFrom);
    }

    public void revertToPending(List<KnNotificationKeyDTO> keys) {
        String methodName = "revertToPending";
        if (keys == null || keys.isEmpty()) {
            return;
        }
        KnPersisterTxn txn = null;
        PreparedStatement statement = null;
        try {
            txn = KnPersisterTxn.getPersisterTxn();
            txn.open();
            statement = primaryConnection(txn).prepareStatement(REVERT_TO_PENDING);
            for (KnNotificationKeyDTO key : keys) {
                statement.setInt(1, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
                statement.setString(2, key.getDestId());
                statement.setLong(3, key.getInsertionTime());
                statement.setInt(4, key.getDestType());
                statement.setInt(5, KnXcapNotifyConstants.NOTIFYSTATUS.NOTIFY_INITIATED.value());
                statement.addBatch();
            }
            statement.executeBatch();
            txn.save();
            knLogger.info(methodName, FLOW_TAG + " STEP-SCH6 Queue rows returned to pending. rowCount=" + keys.size());
        } catch (Exception e) {
            rollback(txn);
            knLogger.error(methodName, FLOW_TAG + " STEP-ERR Failed to return queue rows to pending. rowCount="
                    + keys.size(), e);
        } finally {
            KnDbUtil.closeStatement(statement);
        }
    }

    /**
     * Due watchers come from the tracker first: LAST_NOTIFIED_TIME is old enough and
     * the primary queue still has a pending row. Their queue rows are loaded by MDN,
     * up to 100 MDNs. Own-account rows are released on this poll and do not move the
     * tracker. A watcher row with no tracker row is claimed so a failed insert is not stuck.
     */
    private Map<KnNotificationKeyDTO, Object> claimByRecipient(Connection primary, Connection secondary, long cutoff,
                                                               LinkedHashSet<String> delayedWatchers) throws Exception {
        Map<KnNotificationKeyDTO, Object> claimed = new LinkedHashMap<>();
        Set<String> claimedKeys = new HashSet<>();
        List<String> aged = querySingleColumn(secondary, SELECT_AGED_TRACKER, statement -> statement.setLong(1, cutoff));
        Set<String> agedWithQueue = queueDestIds(primary, aged, true);
        for (String mdn : aged) {
            if (delayedWatchers.size() >= MAX_MDNS_PER_POLL) {
                break;
            }
            if (agedWithQueue.contains(mdn)) {
                delayedWatchers.add(mdn);
            }
        }
        if (!delayedWatchers.isEmpty()) {
            Map<KnNotificationKeyDTO, Object> dueRows = loadPendingRows(primary, delayedWatchers);
            claimed.putAll(dueRows);
            Set<String> loadedDestinations = new HashSet<>();
            for (KnNotificationKeyDTO key : dueRows.keySet()) {
                claimedKeys.add(rowKey(key));
                if (key.getDestId() != null) {
                    loadedDestinations.add(key.getDestId().trim());
                }
            }
            delayedWatchers.retainAll(loadedDestinations);
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        List<Object[]> rows = new ArrayList<>();
        Set<String> holdDestinations = new LinkedHashSet<>();
        try {
            statement = primary.prepareStatement(SELECT_CLASSIFIABLE_PENDING);
            statement.setInt(1, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
            statement.setInt(2, KnXcapNotifyConstants.DESTTYPE.SUPPRESSMDN.value());
            result = statement.executeQuery();
            while (result.next()) {
                String destId = result.getString(2);
                if (destId == null || destId.trim().isEmpty()) {
                    continue;
                }
                Object payload;
                try {
                    payload = KnGeneralUtil.byteArrayToObject(result.getBytes(4));
                } catch (Exception decodeFailure) {
                    knLogger.error("claimByRecipient",
                            FLOW_TAG + " STEP-ERR Skipping a queue row whose payload could not be read. recipient="
                                    + KnGDPRTemplate.mdn(destId), decodeFailure);
                    continue;
                }
                if (payload == null) {
                    continue;
                }
                boolean hold = KnXcapDiffNotifier.holdsForConfiguredDelay(payload, result.getInt(7));
                rows.add(new Object[]{result.getLong(1), destId, result.getInt(3), result.getInt(5),
                        result.getString(6), payload, hold});
                if (hold) {
                    holdDestinations.add(destId.trim());
                }
            }
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closeStatement(statement);
        }
        Set<String> tracked = trackerMdns(secondary, holdDestinations);
        LinkedHashSet<String> ownAccounts = new LinkedHashSet<>();
        int orphanWatchers = 0;
        for (Object[] row : rows) {
            String destId = ((String) row[1]).trim();
            boolean hold = (Boolean) row[6];
            KnNotificationKeyDTO key = new KnNotificationKeyDTO((Long) row[0], (String) row[1],
                    (Integer) row[2], (Integer) row[3], (String) row[4]);
            if (claimedKeys.contains(rowKey(key))) {
                continue;
            }
            if (!hold) {
                if (ownAccounts.size() >= MAX_MDNS_PER_POLL && !ownAccounts.contains(destId)) {
                    continue;
                }
                claimed.put(key, row[5]);
                claimedKeys.add(rowKey(key));
                ownAccounts.add(destId);
            } else if (!tracked.contains(destId)) {
                if (delayedWatchers.size() >= MAX_MDNS_PER_POLL && !delayedWatchers.contains(destId)) {
                    continue;
                }
                orphanWatchers++;
                claimed.put(key, row[5]);
                claimedKeys.add(rowKey(key));
                delayedWatchers.add(destId);
            }
        }
        if (!ownAccounts.isEmpty()) {
            knLogger.info("claimByRecipient", FLOW_TAG + " STEP-DELAY Own-account queue rows released without the hold. recipients="
                    + maskMdns(ownAccounts) + " rowCount=" + ownAccounts.size());
        }
        if (!delayedWatchers.isEmpty()) {
            knLogger.info("claimByRecipient", FLOW_TAG + " STEP-SCH2 Watchers due after the configured period. count="
                    + delayedWatchers.size() + " watchers=" + maskMdns(delayedWatchers));
        } else if (ownAccounts.isEmpty()) {
            knLogger.debug("claimByRecipient", FLOW_TAG + " STEP-SCH2 No own-account rows and no watchers are due. cutoff=" + cutoff);
        }
        if (orphanWatchers > 0) {
            knLogger.warn("claimByRecipient", FLOW_TAG + " STEP-SCH2A Watcher rows had no tracker entry and were claimed. count="
                    + orphanWatchers);
        }
        return claimed;
    }

    private static String rowKey(KnNotificationKeyDTO key) {
        return key.getInsertionTime() + "|" + key.getDestId() + "|" + key.getDestType();
    }

    /**
     * Due watchers are the overlap of an aged secondary tracker row and a pending
     * primary queue row. Orphans are pending primary rows with no tracker row, so a
     * failed tracker insert is not stuck.
     */
    private LinkedHashSet<String> selectEligibleWatchers(Connection primary, Connection secondary, long cutoff)
            throws SQLException {
        LinkedHashSet<String> watchers = new LinkedHashSet<>();
        List<String> aged = querySingleColumn(secondary, SELECT_AGED_TRACKER, statement -> statement.setLong(1, cutoff));
        Set<String> agedWithQueue = queueDestIds(primary, aged, true);
        for (String mdn : aged) {
            if (watchers.size() >= MAX_MDNS_PER_POLL) {
                break;
            }
            if (agedWithQueue.contains(mdn)) {
                watchers.add(mdn);
            }
        }
        if (watchers.size() < MAX_MDNS_PER_POLL) {
            List<String> pendingDests = querySingleColumn(primary, SELECT_PENDING_DEST_IDS, statement -> {
                statement.setInt(1, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
                statement.setInt(2, KnXcapNotifyConstants.DESTTYPE.SUPPRESSMDN.value());
            });
            Set<String> tracked = trackerMdns(secondary, pendingDests);
            int orphanCount = 0;
            for (String destId : pendingDests) {
                if (watchers.size() >= MAX_MDNS_PER_POLL) {
                    break;
                }
                if (!tracked.contains(destId) && watchers.add(destId)) {
                    orphanCount++;
                }
            }
            if (orphanCount > 0) {
                knLogger.warn("selectEligibleWatchers",
                        FLOW_TAG + " STEP-SCH2A Pending primary rows had no secondary tracker entry and were claimed. count="
                                + orphanCount);
            }
        }
        return watchers;
    }

    private Map<KnNotificationKeyDTO, Object> loadPendingRows(Connection connection, Set<String> watchers) throws Exception {
        Map<KnNotificationKeyDTO, Object> rows = new LinkedHashMap<>();
        if (watchers == null || watchers.isEmpty()) {
            return rows;
        }
        List<String> mdns = new ArrayList<>(watchers);
        String query = "SELECT INSERTION_TIME, DEST_ID, DEST_TYPE, PAYLOAD, MSG_TYPE, CID "
                + "FROM DG.XCAP_PENDING_NOTIFYQ WHERE NOTIFY_STATUS = ? AND DEST_TYPE <> ? AND DEST_ID IN ("
                + placeholders(mdns.size()) + ") ORDER BY DEST_ID, INSERTION_TIME";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
            statement.setInt(2, KnXcapNotifyConstants.DESTTYPE.SUPPRESSMDN.value());
            for (int i = 0; i < mdns.size(); i++) {
                statement.setString(i + 3, mdns.get(i));
            }
            result = statement.executeQuery();
            while (result.next()) {
                KnNotificationKeyDTO key = new KnNotificationKeyDTO(result.getLong(1), result.getString(2),
                        result.getInt(3), result.getInt(5), result.getString(6));
                try {
                    Object payload = KnGeneralUtil.byteArrayToObject(result.getBytes(4));
                    if (payload != null) {
                        rows.put(key, payload);
                    }
                } catch (Exception decodeFailure) {
                    knLogger.error("loadPendingRows",
                            FLOW_TAG + " STEP-ERR Skipping a queue row whose payload could not be read. watcher="
                                    + KnGDPRTemplate.mdn(key.getDestId()), decodeFailure);
                }
            }
            knLogger.info("loadPendingRows", FLOW_TAG + " STEP-SCH3 Pending rows loaded for the watcher bundle. watchers="
                    + watchers.size() + " loadedRows=" + rows.size());
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closeStatement(statement);
        }
        return rows;
    }

    /** Suppressed rows stay on the existing per-row path so that queue is not stranded. */
    private int appendSuppressed(Connection connection, Map<KnNotificationKeyDTO, Object> claimed) throws Exception {
        int suppressBatch = positiveInt(readConfig(XCAP_NOTIFY_SUPPRESS_MDN_BATCH), 100, "appendSuppressed");
        PreparedStatement statement = null;
        ResultSet result = null;
        int added = 0;
        int watcherCount = 0;
        try {
            statement = connection.prepareStatement(SELECT_SUPPRESSED);
            statement.setInt(1, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
            statement.setInt(2, KnXcapNotifyConstants.DESTTYPE.SUPPRESSMDN.value());
            result = statement.executeQuery();
            while (result.next()) {
                int paramCount = result.getInt(6);
                if (watcherCount + paramCount > suppressBatch) {
                    break;
                }
                Object payload = KnGeneralUtil.byteArrayToObject(result.getBytes(4));
                if (payload == null) {
                    continue;
                }
                watcherCount += paramCount;
                claimed.put(new KnNotificationKeyDTO(result.getLong(1), result.getString(2),
                        result.getInt(3), result.getInt(5), result.getString(7)), payload);
                added++;
            }
            if (added > 0) {
                knLogger.info("appendSuppressed", FLOW_TAG + " STEP-SCH3A Suppressed rows included. rows=" + added);
            }
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closeStatement(statement);
        }
        return added;
    }

    private void markInProgress(Connection connection, Set<KnNotificationKeyDTO> keys) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(MARK_IN_PROGRESS);
            for (KnNotificationKeyDTO key : keys) {
                statement.setInt(1, KnXcapNotifyConstants.NOTIFYSTATUS.NOTIFY_INITIATED.value());
                statement.setString(2, key.getDestId());
                statement.setLong(3, key.getInsertionTime());
                statement.setInt(4, key.getDestType());
                statement.setInt(5, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
                statement.addBatch();
            }
            statement.executeBatch();
            knLogger.info("markInProgress", FLOW_TAG + " STEP-SCH4 Queue rows marked in progress. rows=" + keys.size());
        } finally {
            KnDbUtil.closeStatement(statement);
        }
    }

    /** Opens the next window. An orphan watcher with no tracker row is inserted. */
    private void touchTracker(Connection connection, Set<String> watchers) throws SQLException {
        if (watchers == null || watchers.isEmpty()) {
            return;
        }
        PreparedStatement update = null;
        PreparedStatement insert = null;
        try {
            update = connection.prepareStatement(TOUCH_TRACKER);
            insert = connection.prepareStatement(INSERT_IF_ABSENT);
            long nowMillis = System.currentTimeMillis();
            for (String mdn : watchers) {
                if (mdn == null || mdn.trim().isEmpty() || mdn.trim().length() > TRACKER_MDN_LENGTH) {
                    continue;
                }
                String watcher = mdn.trim();
                update.setLong(1, nowMillis);
                update.setString(2, watcher);
                update.addBatch();
                insert.setString(1, watcher);
                insert.setLong(2, nowMillis);
                insert.setString(3, watcher);
                insert.addBatch();
            }
            update.executeBatch();
            insert.executeBatch();
            knLogger.info("touchTracker",
                    FLOW_TAG + " STEP-SCH4A LAST_NOTIFIED_TIME set to current time. watchers=" + watchers.size());
        } finally {
            KnDbUtil.closeStatement(update);
            KnDbUtil.closeStatement(insert);
        }
    }

    /**
     * Removes a secondary tracker row only after its hold has elapsed and the primary
     * queue has nothing pending or in progress for that MDN.
     * The written check LAST_NOTIFIED_TIME &gt;= now + period points at a future time
     * and would not remove a row that was just set to now. This method uses
     * LAST_NOTIFIED_TIME &lt;= now - period.
     */
    private int housekeepTracker(Connection primary, Connection secondary, long cutoff) throws SQLException {
        List<String> expired = querySingleColumn(secondary, SELECT_EXPIRED_TRACKER,
                statement -> statement.setLong(1, cutoff));
        if (expired.isEmpty()) {
            return 0;
        }
        Set<String> stillQueued = queueDestIds(primary, expired, false);
        PreparedStatement delete = null;
        try {
            delete = secondary.prepareStatement(DELETE_TRACKER);
            int batched = 0;
            for (String mdn : expired) {
                if (stillQueued.contains(mdn)) {
                    continue;
                }
                delete.setString(1, mdn);
                delete.addBatch();
                batched++;
            }
            if (batched == 0) {
                return 0;
            }
            delete.executeBatch();
            knLogger.info("housekeepTracker",
                    FLOW_TAG + " STEP-SCH5A Closed tracker windows removed. cutoff=" + cutoff + " removed=" + batched);
            return batched;
        } finally {
            KnDbUtil.closeStatement(delete);
        }
    }

    private void updateByMdn(String methodName, String sql, Collection<String> mdns, Long when, String successMessage) {
        if (mdns == null || mdns.isEmpty() || !ensureTrackerTable()) {
            return;
        }
        KnPersisterTxn txn = null;
        PreparedStatement statement = null;
        try {
            txn = KnPersisterTxn.getPersisterTxn();
            txn.open();
            statement = secondaryConnection(txn).prepareStatement(sql);
            int batched = 0;
            for (String mdn : mdns) {
                if (mdn == null || mdn.trim().isEmpty()) {
                    continue;
                }
                int index = 1;
                if (when != null) {
                    statement.setLong(index++, when);
                }
                statement.setString(index, mdn.trim());
                statement.addBatch();
                batched++;
            }
            if (batched > 0) {
                statement.executeBatch();
                txn.save();
                knLogger.info(methodName, successMessage + " rowCount=" + batched);
            }
        } catch (Exception e) {
            rollback(txn);
            knLogger.error(methodName, FLOW_TAG + " STEP-ERR Tracker update failed. rowCount=" + mdns.size(), e);
        } finally {
            KnDbUtil.closeStatement(statement);
        }
    }

    private boolean ensureTrackerTable() {
        if (tableReady) {
            return true;
        }
        synchronized (tableLock) {
            if (tableReady) {
                return true;
            }
            tableReady = probeTrackerTable();
            return tableReady;
        }
    }

    /** Confirms DG.MDN_NOTIFY_TRACKER is reachable on the secondary DSN. The live table is not created here. */
    private boolean probeTrackerTable() {
        KnPersisterTxn txn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            txn = KnPersisterTxn.getPersisterTxn();
            txn.open();
            Connection connection = secondaryConnection(txn);
            statement = connection.prepareStatement(PROBE_TRACKER);
            result = statement.executeQuery();
            knLogger.info("probeTrackerTable", FLOW_TAG + " STEP-TRK0 Secondary tracker DG.MDN_NOTIFY_TRACKER is available. "
                    + "LAST_NOTIFIED_TIME is BIGINT epoch milliseconds. dataStore=" + KnDBConst.DataStores.XDM_SHARED_DATA);
            rollback(txn);
            return true;
        } catch (Exception e) {
            rollback(txn);
            knLogger.error("probeTrackerTable", FLOW_TAG + " STEP-ERR DG.MDN_NOTIFY_TRACKER is not reachable on the secondary database "
                    + "(local PTT id plus _" + KnDBConst.DataStores.XDM_SHARED_DATA.getValue() + ").", e);
            return false;
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closeStatement(statement);
        }
    }

    private Connection primaryConnection(KnPersisterTxn txn) throws Exception {
        return txn.getDBConnection(KnDbUtil.getDBConfigInfo().getLocalPttId(), false);
    }

    private Connection secondaryConnection(KnPersisterTxn txn) throws Exception {
        String localPttId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        knLogger.debug("secondaryConnection", FLOW_TAG + " Secondary tracker DSN suffix _"
                + KnDBConst.DataStores.XDM_SHARED_DATA.getValue() + " basePttId=" + localPttId);
        return txn.getDBConnection(localPttId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
    }

    private List<String> querySingleColumn(Connection connection, String sql, StatementBinder binder) throws SQLException {
        List<String> values = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = connection.prepareStatement(sql);
            binder.bind(statement);
            result = statement.executeQuery();
            while (result.next()) {
                String value = result.getString(1);
                if (value != null && !value.trim().isEmpty()) {
                    values.add(value.trim());
                }
            }
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closeStatement(statement);
        }
        return values;
    }

    private Set<String> queueDestIds(Connection primary, Collection<String> mdns, boolean pendingOnly) throws SQLException {
        Set<String> found = new HashSet<>();
        List<String> values = trimmed(mdns);
        if (values.isEmpty()) {
            return found;
        }
        String statusClause = pendingOnly
                ? "NOTIFY_STATUS = ? AND DEST_TYPE <> ?"
                : "NOTIFY_STATUS IN (?, ?) AND DEST_TYPE <> ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = primary.prepareStatement("SELECT DISTINCT DEST_ID FROM DG.XCAP_PENDING_NOTIFYQ WHERE "
                    + statusClause + " AND DEST_ID IN (" + placeholders(values.size()) + ")");
            int index = 1;
            statement.setInt(index++, KnXcapNotifyConstants.NOTIFYSTATUS.PENDING.value());
            if (!pendingOnly) {
                statement.setInt(index++, KnXcapNotifyConstants.NOTIFYSTATUS.NOTIFY_INITIATED.value());
            }
            statement.setInt(index++, KnXcapNotifyConstants.DESTTYPE.SUPPRESSMDN.value());
            for (String mdn : values) {
                statement.setString(index++, mdn);
            }
            result = statement.executeQuery();
            while (result.next()) {
                String destId = result.getString(1);
                if (destId != null && !destId.trim().isEmpty()) {
                    found.add(destId.trim());
                }
            }
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closeStatement(statement);
        }
        return found;
    }

    private Set<String> trackerMdns(Connection secondary, Collection<String> mdns) throws SQLException {
        Set<String> found = new HashSet<>();
        List<String> values = trimmed(mdns);
        if (values.isEmpty()) {
            return found;
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = secondary.prepareStatement(
                    "SELECT MDN FROM DG.MDN_NOTIFY_TRACKER WHERE MDN IN (" + placeholders(values.size()) + ")");
            for (int i = 0; i < values.size(); i++) {
                statement.setString(i + 1, values.get(i));
            }
            result = statement.executeQuery();
            while (result.next()) {
                String mdn = result.getString(1);
                if (mdn != null && !mdn.trim().isEmpty()) {
                    found.add(mdn.trim());
                }
            }
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closeStatement(statement);
        }
        return found;
    }

    private void logWatcherHold(Connection connection, List<String> watchers, int[] counts, int periodSeconds) {
        try {
            Map<String, Long> windowStart = readTrackerTimes(connection, watchers);
            long now = System.currentTimeMillis();
            for (int i = 0; i < watchers.size(); i++) {
                String watcher = watchers.get(i);
                int count = (counts != null && i < counts.length) ? counts[i] : 0;
                String windowState = count == Statement.SUCCESS_NO_INFO ? "unknown" : (count > 0 ? "started" : "joined");
                Long started = windowStart.get(watcher);
                long heldMillis = started == null ? 0L : Math.max(0L, now - started);
                long remaining = Math.max(0L, (periodSeconds * 1000L) - heldMillis);
                knLogger.info("logWatcherHold",
                        FLOW_TAG + " STEP-DELAY Watcher notification held. watcher=" + KnGDPRTemplate.mdn(watcher)
                                + " window=" + windowState
                                + " holdSeconds=" + periodSeconds
                                + " windowStart=" + started
                                + " remainingSeconds=" + (remaining / 1000L));
            }
        } catch (Exception e) {
            knLogger.warn("logWatcherHold", FLOW_TAG + " STEP-DELAY Hold log skipped. " + e.getMessage());
        }
    }

    private void logWatcherRelease(Connection connection, Set<String> watchers,
                                   Map<KnNotificationKeyDTO, Object> claimed, int periodSeconds) {
        try {
            Map<String, Long> windowStart = readTrackerTimes(connection, watchers);
            long now = System.currentTimeMillis();
            for (String watcher : watchers) {
                Long started = windowStart.get(watcher);
                long heldSeconds = started == null ? periodSeconds : Math.max(0L, now - started) / 1000L;
                int queuedChanges = 0;
                for (KnNotificationKeyDTO key : claimed.keySet()) {
                    if (key.getDestId() != null && watcher.equals(key.getDestId().trim())
                            && key.getDestType() != KnXcapNotifyConstants.DESTTYPE.SUPPRESSMDN.value()) {
                        queuedChanges++;
                    }
                }
                knLogger.info("logWatcherRelease",
                        FLOW_TAG + " STEP-DELAY Watcher hold elapsed. watcher=" + KnGDPRTemplate.mdn(watcher)
                                + " heldSeconds=" + heldSeconds
                                + " configuredHoldSeconds=" + periodSeconds
                                + " windowStart=" + started
                                + " queuedChanges=" + queuedChanges);
            }
        } catch (Exception e) {
            knLogger.warn("logWatcherRelease", FLOW_TAG + " STEP-DELAY Release log skipped. " + e.getMessage());
        }
    }

    private Map<String, Long> readTrackerTimes(Connection connection, Collection<String> watchers) {
        Map<String, Long> times = new LinkedHashMap<>();
        List<String> values = trimmed(watchers);
        if (values.isEmpty()) {
            return times;
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = connection.prepareStatement(
                    "SELECT MDN, LAST_NOTIFIED_TIME FROM DG.MDN_NOTIFY_TRACKER WHERE MDN IN ("
                            + placeholders(values.size()) + ")");
            for (int i = 0; i < values.size(); i++) {
                statement.setString(i + 1, values.get(i));
            }
            result = statement.executeQuery();
            while (result.next()) {
                if (result.getString(1) != null) {
                    times.put(result.getString(1).trim(), result.getLong(2));
                }
            }
        } catch (Exception e) {
            knLogger.warn("readTrackerTimes", FLOW_TAG + " STEP-DELAY Tracker time read skipped. " + e.getMessage());
        } finally {
            KnDbUtil.closeResultSet(result);
            KnDbUtil.closeStatement(statement);
        }
        return times;
    }

    private List<String> trimmed(Collection<String> mdns) {
        List<String> values = new ArrayList<>();
        if (mdns == null) {
            return values;
        }
        for (String mdn : mdns) {
            if (mdn != null && !mdn.trim().isEmpty()) {
                values.add(mdn.trim());
            }
        }
        return values;
    }

    private String placeholders(int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append('?');
        }
        return builder.toString();
    }

    private String maskMdns(Collection<String> mdns) {
        List<String> masked = new ArrayList<>();
        int shown = 0;
        for (String mdn : mdns) {
            if (shown++ == 5) {
                break;
            }
            masked.add(KnGDPRTemplate.mdn(mdn));
        }
        if (mdns.size() > 5) {
            masked.add("+" + (mdns.size() - 5) + " more");
        }
        return masked.toString();
    }

    private int positiveInt(String raw, int fallback, String methodName) {
        if (raw == null || raw.trim().isEmpty()) {
            return fallback;
        }
        try {
            int parsed = Integer.parseInt(raw.trim());
            if (parsed <= 0) {
                knLogger.warn(methodName, FLOW_TAG + " Non-positive config value '" + raw + "'. Using " + fallback + ".");
                return fallback;
            }
            return parsed;
        } catch (NumberFormatException e) {
            knLogger.warn(methodName, FLOW_TAG + " Unreadable config value '" + raw + "'. Using " + fallback + ".");
            return fallback;
        }
    }

    private String readConfig(String key) {
        try {
            String cluster = System.getenv(CLUSTERID_ENV_NAME);
            if (cluster == null || cluster.trim().isEmpty()) {
                return null;
            }
            Map<String, String> config = KnGenInfoUtil.getInstance().retrieveMSSvcsCommonConfig(Integer.parseInt(cluster));
            return config == null ? null : config.get(key);
        } catch (Exception e) {
            knLogger.warn("readConfig", FLOW_TAG + " Config read failed for " + key + ". " + e.getMessage());
            return null;
        }
    }

    private void rollback(KnPersisterTxn txn) {
        try {
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback", FLOW_TAG + " STEP-ERR Rollback failed.", e);
        }
    }

    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
