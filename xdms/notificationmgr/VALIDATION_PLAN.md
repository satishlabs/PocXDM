================================================================================
VALIDATION PLAN — XCAP Notification Temporal Workflow Optimization
Story: Debulk Watcher Notifications (Epoch-Based Batching)
Date : August 10, 2026
================================================================================

PRODUCTION FILE SET (all other files unchanged)
────────────────────────────────────────────────
NEW   dbmgr/source/create_mdn_notify_tracker.sql
NEW   xdms/notificationmgr/.../resources/KnWatcherDebulkSendNotification.java
MOD   common/.../resources/KnConstants.java            (3 constants already present)
MOD   xdms/loader/.../KnXDMSLoader.java                (notificationPoller)
MOD   xdms/notificationmgr/.../impl/KnXcapDiffNotifier.java      (pollRecord + tracker methods)
MOD   xdms/notificationmgr/.../impl/KnXcapDiffNotifierImpl.java  (upsert calls + MDN helpers)
MOD   xdms/notificationmgr/.../resources/KnXcapNotifyProcessor.java (bundle grouping)

================================================================================
STEP 1 — DATABASE SETUP
================================================================================

1.1  Run the tracker table DDL
     sqlplus /nolog @dbmgr/source/create_mdn_notify_tracker.sql

1.2  Verify table was created
     SELECT COUNT(*) FROM DG.MDN_NOTIFY_TRACKER;     -- should return 0 (empty)
     SELECT COUNT(*) FROM DG.XCAP_PENDING_NOTIFYQ;   -- should return existing rows

1.3  Verify the index exists
     SELECT INDEX_NAME FROM USER_INDEXES
     WHERE TABLE_NAME = 'MDN_NOTIFY_TRACKER';
     -- Should return: IDX_MDN_NOTIFY_TRACKER_LAST_TS

================================================================================
STEP 2 — CONFIGURATION (set in microservices common config table)
================================================================================

For INITIAL VALIDATION — start with optimized OFF (legacy path unchanged):
    XCAP_NOTIFICATION_OPTIMIZED  = 0   ← legacy mode
    XCAP_NOTIFICATION_PERIOD     = 120
    XCAP_DIFF_PAYLOAD_SIZE       = 2048

For OPTIMIZATION VALIDATION — enable optimized mode:
    XCAP_NOTIFICATION_OPTIMIZED  = 1   ← enable epoch batching
    XCAP_NOTIFICATION_PERIOD     = 30  ← short window for faster testing
    XCAP_DIFF_PAYLOAD_SIZE       = 2048

Verify configuration was picked up (check startup logs or run):
    grep "XCAP_NOTIFICATION_OPTIMIZED" <application_log>
    grep "optimizedMode =" <application_log>

================================================================================
STEP 3 — BUILD
================================================================================

3.1  Compile the project
     cd C:\mydev\PocXDM_
     gradle build  (or: ant -f build.xml)

3.2  Confirm zero compilation errors

3.3  Confirm new class is in the JAR
     jar tf build/<artifact>.jar | findstr "KnWatcherDebulkSendNotification"
     -- Should print the .class entry

================================================================================
STEP 4 — DEPLOY & START
================================================================================

4.1  Set environment variable
     set CLUSTERID=1      (Windows)
     export CLUSTERID=1   (Linux)

4.2  Deploy WAR/JAR to application server

4.3  Start application and watch startup logs for:
     [INFO] XcapNotifyProcessor scheduled: initialDelay=30s, interval=1s, optimizedMode=true
     [INFO] Initialising executor: ...

================================================================================
STEP 5 — TC-01  Legacy Mode (XCAP_NOTIFICATION_OPTIMIZED=0)
================================================================================

Goal: confirm the original notification path is completely unchanged.

5.1  Set XCAP_NOTIFICATION_OPTIMIZED = 0

5.2  Trigger a single subscriber management operation
     (e.g. add/remove contact, group membership change)

5.3  Verify:
     a) Row inserted in DG.XCAP_PENDING_NOTIFYQ
     b) DG.MDN_NOTIFY_TRACKER remains EMPTY (no upsert when optimized=0)
     c) Within the next NOTIFYJOB_AUDIT_INTRVAL seconds, the watcher receives
        the notification
     d) Row is deleted from DG.XCAP_PENDING_NOTIFYQ after send

5.4  Log check:
     grep "optimizedMode = false" <log>
     grep "Optimized mode OFF – skipping tracker upsert" <log>

PASS criteria: watcher notified, tracker table empty, legacy behaviour preserved.

================================================================================
STEP 6 — TC-02  Rapid Changes → Single Tracker Row
================================================================================

Goal: 10 rapid changes for same watcher → 1 tracker row, multiple queue rows.

6.1  Set XCAP_NOTIFICATION_OPTIMIZED = 1, XCAP_NOTIFICATION_PERIOD = 120

6.2  Trigger 10 rapid management operations on the SAME subscriber MDN
     (e.g. loop script that adds/modifies contacts in quick succession)

6.3  Immediately after (within 1-2 seconds), query:
     SELECT COUNT(*) FROM DG.XCAP_PENDING_NOTIFYQ WHERE DEST_ID = '<watcher_mdn>';
     -- Expected: 10 rows (all PENDING, not yet claimed)

     SELECT COUNT(*) FROM DG.MDN_NOTIFY_TRACKER WHERE MDN = '<watcher_mdn>';
     -- Expected: exactly 1 row (epoch preserved, not reset by repeats)

     SELECT LAST_NOTIFIED_TIME FROM DG.MDN_NOTIFY_TRACKER WHERE MDN = '<watcher_mdn>';
     -- Note this timestamp; it should NOT change for the next 120 seconds

6.4  Log check:
     grep "Tracker upsert executed for" <log>
     grep "INSERT-IF-ABSENT" <log>   -- should appear only once

PASS criteria: 10 queue rows, exactly 1 tracker row, epoch clock preserved.

================================================================================
STEP 7 — TC-03  Poll Before Epoch Expiry
================================================================================

Goal: poller runs but does NOT send while epoch window is still open.

7.1  Immediately after TC-02 (tracker row exists, epoch fresh):

7.2  Watch logs for 30 seconds; confirm:
     grep "Eligible watcher MDN count for this cycle: 0" <log>
     -- Should appear on every 1-second poll during the epoch window

7.3  Verify queue rows remain PENDING:
     SELECT NOTIFY_STATUS FROM DG.XCAP_PENDING_NOTIFYQ WHERE DEST_ID = '<watcher_mdn>';
     -- All rows should have NOTIFY_STATUS = 0 (PENDING)

7.4  Verify no notification was sent to the watcher (check client side / wireshark)

PASS criteria: no send occurs during the epoch window.

================================================================================
STEP 8 — TC-04  Poll After Epoch Expiry → One Bundled Send
================================================================================

Goal: after XCAP_NOTIFICATION_PERIOD seconds, all 10 queue rows are claimed and
      ONE bundled notification is sent.

8.1  Wait for XCAP_NOTIFICATION_PERIOD seconds (30s in test config)

8.2  Watch logs for:
     [INFO] Eligible watcher MDN count for this cycle: 1
     [INFO] Submitting debulk workers for 1 unique watcher(s)
     [INFO] CID:<cid> watcher:<mdn> bundledCount:10 isSuccess:true

8.3  Verify queue rows are deleted:
     SELECT COUNT(*) FROM DG.XCAP_PENDING_NOTIFYQ WHERE DEST_ID = '<watcher_mdn>';
     -- Expected: 0

8.4  Verify the watcher received EXACTLY 1 notification (not 10) on the client

8.5  Verify tracker timestamp was advanced:
     SELECT LAST_NOTIFIED_TIME FROM DG.MDN_NOTIFY_TRACKER WHERE MDN = '<watcher_mdn>';
     -- Should be a newer timestamp than the one noted in TC-02

PASS criteria: 1 bundled send, queue cleared, tracker timestamp advanced.

================================================================================
STEP 9 — TC-05  Multiple Watchers in Same Epoch
================================================================================

Goal: multiple watchers each receive ONE bundled notification.

9.1  Trigger changes for 3 different subscriber MDNs (watcher_A, watcher_B, watcher_C)

9.2  Query tracker:
     SELECT MDN, LAST_NOTIFIED_TIME FROM DG.MDN_NOTIFY_TRACKER
     WHERE MDN IN ('<watcher_A>', '<watcher_B>', '<watcher_C>');
     -- Expected: 3 rows, one per watcher

9.3  Wait for epoch to expire, then verify logs:
     [INFO] Submitting debulk workers for 3 unique watcher(s)
     [INFO] CID:... watcher:<watcher_A> bundledCount:N isSuccess:true
     [INFO] CID:... watcher:<watcher_B> bundledCount:N isSuccess:true
     [INFO] CID:... watcher:<watcher_C> bundledCount:N isSuccess:true

9.4  Verify each watcher received exactly 1 notification on the client

PASS criteria: 3 watchers, 3 bundled sends, no cross-watcher mixing.

================================================================================
STEP 10 — TC-06  Duplicate Tracker Row Prevention
================================================================================

Goal: if tracker row already exists, no duplicate insert; epoch preserved.

10.1  With a tracker row already present from TC-02, trigger another change for the same MDN

10.2  Query:
      SELECT COUNT(*) FROM DG.MDN_NOTIFY_TRACKER WHERE MDN = '<watcher_mdn>';
      -- Must still be exactly 1

      SELECT LAST_NOTIFIED_TIME FROM DG.MDN_NOTIFY_TRACKER WHERE MDN = '<watcher_mdn>';
      -- Must be UNCHANGED (original epoch start time preserved)

10.3  Log check:
      grep "INSERT-IF-ABSENT" <log>  -- should NOT show a new insert for existing MDN

PASS criteria: 1 tracker row, original timestamp unchanged.

================================================================================
STEP 11 — TC-07  Standby Card
================================================================================

Goal: on standby/redundancy switchover, poller does nothing.

11.1  Force card to STANDBY state (via status manager or test harness)

11.2  Trigger management operations and wait for epoch expiry

11.3  Verify:
      a) Queue rows remain PENDING (not claimed)
      b) Tracker timestamps not updated
      c) No notifications sent

11.4  Log check:
      grep "getCurrentRedundancyStatus" <log>
      -- Should show UNKNOWN or STANDBY, not ACTIVE

PASS criteria: no processing on standby card.

================================================================================
STEP 12 — TC-08  Recovery After Restart (NOTIFY_INITIATED rows)
================================================================================

Goal: rows stuck in NOTIFY_INITIATED after a crash are recovered.

12.1  Manually set some rows to NOTIFY_INITIATED:
      UPDATE DG.XCAP_PENDING_NOTIFYQ SET NOTIFY_STATUS = 2 WHERE DEST_ID = '<test_mdn>';

12.2  Restart the application

12.3  Verify on first poll cycle (startup=true path):
      [INFO] updateRecord() called  -- this resets NOTIFY_INITIATED → PENDING

12.4  Verify the rows are reset to PENDING:
      SELECT NOTIFY_STATUS FROM DG.XCAP_PENDING_NOTIFYQ WHERE DEST_ID = '<test_mdn>';
      -- All should be 0 (PENDING)

PASS criteria: stuck rows recovered automatically on restart.

================================================================================
STEP 13 — TC-09  Rule I (Single Doc, Multiple Diffs, Within Payload Limit)
================================================================================

Goal: bundled payload within XCAP_DIFF_PAYLOAD_SIZE → consolidated Doc Diff Notification.

13.1  Set XCAP_DIFF_PAYLOAD_SIZE = 10000 (large limit to stay within it)

13.2  Trigger 3 changes to a SINGLE document for one watcher

13.3  After epoch expiry, verify log:
      [INFO] Rule I (within limit) – payload N bytes OK → sending consolidated Document Diff

13.4  Verify watcher received 1 notification with multiple diffs inside (not a directory etag)

PASS criteria: consolidated diff notification, 1 send, correct content.

================================================================================
STEP 14 — TC-10  Rule I (Single Doc, Oversized Payload → Fallback)
================================================================================

Goal: bundled payload exceeds XCAP_DIFF_PAYLOAD_SIZE → Directory Etag fallback.

14.1  Set XCAP_DIFF_PAYLOAD_SIZE = 10 (tiny limit to force fallback)

14.2  Trigger 3 changes to a SINGLE document

14.3  After epoch expiry, verify log:
      [INFO] Rule I (oversized) – payload N bytes exceeds limit 10 → falling back to Directory Etag

14.4  Verify watcher received a Directory Etag notification (not inline diffs)

PASS criteria: directory etag notification sent as fallback.

================================================================================
STEP 15 — TC-11  Rule II (Multiple Documents → Directory Etag)
================================================================================

Goal: changes spanning multiple documents → unified Directory Etag notification.

15.1  Trigger changes to 2 DIFFERENT documents for the same watcher

15.2  After epoch expiry, verify log:
      [INFO] Rule II – multiple documents (2) → sending Directory Etag notification

15.3  Verify watcher received 1 Directory Etag notification (not per-document diffs)

PASS criteria: 1 directory etag notification for multi-doc changes.

================================================================================
STEP 16 — NTC-01  Burst Management — Notification Reduction
================================================================================

Goal: bulk operation generates fewer notifications than without debulk.

16.1  WITHOUT optimization (XCAP_NOTIFICATION_OPTIMIZED=0):
      Trigger 50 rapid changes on 5 watchers
      Record: number of notifications sent to each watcher

16.2  WITH optimization (XCAP_NOTIFICATION_OPTIMIZED=1, period=30s):
      Trigger same 50 changes on 5 watchers
      Record: number of notifications sent to each watcher after epoch

16.3  Compare:
      Without optimization: ~10 notifications per watcher
      With optimization   : 1 notification per watcher

PASS criteria: significant reduction (ideally 90%+) in per-watcher notification count.

================================================================================
STEP 17 — NTC-02  Poller Cadence Verification
================================================================================

Goal: confirm 1-second poll interval when optimized mode is ON.

17.1  Set XCAP_NOTIFICATION_OPTIMIZED = 1

17.2  Count "Eligible watcher MDN count for this cycle" log lines over 10 seconds
      grep -c "Eligible watcher MDN count for this cycle" <log_10s_window>
      -- Expected: ~10 lines (1 per second)

17.3  Set XCAP_NOTIFICATION_OPTIMIZED = 0, repeat
      -- Expected: lines appear at NOTIFYJOB_AUDIT_INTRVAL interval

PASS criteria: 1-second cadence confirmed in optimized mode.

================================================================================
STEP 18 — NTC-05  No Duplicate Notifications Within Same Epoch
================================================================================

Goal: a watcher receives exactly 1 notification within a single epoch window.

18.1  Trigger 20 changes for one watcher within 5 seconds

18.2  After epoch expiry, use network capture or client-side log to confirm:
      - Exactly 1 XCAP notification received by the watcher client

18.3  Repeat for 3 epochs in a row:
      - Each epoch: some changes → epoch expires → 1 notification
      - Total: 3 notifications after 3 epochs (not 60 for 20 changes × 3 epochs)

PASS criteria: 1 notification per watcher per epoch, no duplicates.

================================================================================
STEP 19 — ROLLBACK VERIFICATION
================================================================================

19.1  Set XCAP_NOTIFICATION_OPTIMIZED = 0

19.2  Trigger management operations

19.3  Verify:
      a) Notifications are sent immediately (within NOTIFYJOB_AUDIT_INTRVAL)
      b) MDN_NOTIFY_TRACKER has NO new rows inserted
      c) Existing tracker rows are harmlessly ignored

19.4  Log check:
      grep "Optimized mode OFF" <log>
      grep "optimizedMode = false" <log>

PASS criteria: legacy behaviour fully restored without restart.

================================================================================
QUICK SANITY CHECKLIST (run before every test cycle)
================================================================================

  [ ] CLUSTERID env variable set         echo %CLUSTERID%
  [ ] MDN_NOTIFY_TRACKER exists          SELECT COUNT(*) FROM DG.MDN_NOTIFY_TRACKER
  [ ] XCAP_PENDING_NOTIFYQ accessible    SELECT COUNT(*) FROM DG.XCAP_PENDING_NOTIFYQ
  [ ] Config loaded                      grep "XCAP_NOTIFICATION" <startup_log>
  [ ] Card is ACTIVE                     grep "ACTIVE" <status_log>
  [ ] Poller started                     grep "XcapNotifyProcessor scheduled" <startup_log>

================================================================================
KEY LOG MESSAGES TO MONITOR
================================================================================

  Startup
    "XcapNotifyProcessor scheduled: initialDelay=30s, interval=1s, optimizedMode=true"
    "XCAP_NOTIFICATION_OPTIMIZED=1 → overriding poll interval to 1 second"

  Per poll cycle (optimized ON, eligible MDNs exist)
    "Eligible watcher MDN count for this cycle: N"
    "Submitting debulk workers for N unique watcher(s)"

  Per watcher bundle send
    "CID:<x> watcher:<mdn> bundledCount:<n> isSuccess:true"

  Tracker operations
    "Tracker upsert executed for N MDN(s)"
    "Updated LAST_NOTIFIED_TIME for N MDN(s)"
    "Stale tracker rows deleted: N"

  Payload rules
    "Rule I (within limit) – payload N bytes OK"
    "Rule I (oversized) – payload N bytes exceeds limit"
    "Rule II – multiple documents"

  Rollback / feature off
    "Optimized mode OFF – skipping tracker upsert"
    "XCAP_NOTIFICATION_OPTIMIZED=0 → using configured interval"

================================================================================
DATABASE MONITORING QUERIES (copy-paste during testing)
================================================================================

-- Queue health
SELECT DEST_ID, NOTIFY_STATUS, COUNT(*) AS CNT
FROM DG.XCAP_PENDING_NOTIFYQ
GROUP BY DEST_ID, NOTIFY_STATUS
ORDER BY CNT DESC;

-- Tracker state
SELECT MDN,
       LAST_NOTIFIED_TIME,
       (CURRENT_TIMESTAMP - LAST_NOTIFIED_TIME) AS AGE_MS
FROM DG.MDN_NOTIFY_TRACKER
ORDER BY LAST_NOTIFIED_TIME ASC;

-- Check a specific watcher
SELECT Q.DEST_ID, Q.NOTIFY_STATUS, Q.INSERTION_TIME,
       T.LAST_NOTIFIED_TIME
FROM DG.XCAP_PENDING_NOTIFYQ Q
LEFT JOIN DG.MDN_NOTIFY_TRACKER T ON T.MDN = Q.DEST_ID
WHERE Q.DEST_ID = '<watcher_mdn>';

-- Full cleanup after test cycle
-- DELETE FROM DG.MDN_NOTIFY_TRACKER;   -- only in test environments!

================================================================================
TEST CASE SUMMARY TABLE
================================================================================

TC-ID    Scenario                                   Expected                      Status
────────────────────────────────────────────────────────────────────────────────────────
TC-01    XCAP_NOTIFICATION_OPTIMIZED=0              Legacy flow unchanged          [ ]
TC-02    10 rapid changes, same watcher             1 tracker row, 10 queue rows   [ ]
TC-03    Poll before epoch expiry                   No send                        [ ]
TC-04    Poll after epoch expiry                    1 bundled send, queue cleared  [ ]
TC-05    3 watchers active                          3 bundled sends, no mixing     [ ]
TC-06    Tracker row already exists                 No duplicate; epoch preserved  [ ]
TC-07    Standby card                               No processing                  [ ]
TC-08    Restart with NOTIFY_INITIATED rows         Recovery via updateRecord()    [ ]
TC-09    Single doc, diffs within limit             Consolidated diff notification [ ]
TC-10    Single doc, payload oversized              Directory etag fallback        [ ]
TC-11    Multiple documents                         Unified directory etag         [ ]
NTC-01   Burst: 50 changes, 5 watchers              90%+ reduction in sends        [ ]
NTC-02   Poller cadence, optimized ON               1-second interval confirmed    [ ]
NTC-05   No duplicate notifications per epoch       Exactly 1 per watcher          [ ]
TC-RB    Rollback (set flag=0)                      Legacy restored immediately    [ ]

================================================================================
END OF VALIDATION PLAN
================================================================================

