# Evidence cheatsheet

Paste outputs into the matching SN proof sheet.

## SQL

```sql
-- Queue for CAT watcher
SELECT DEST_ID, DEST_TYPE, NOTIFY_STATUS, COUNT(*) CNT
  FROM DG.XCAP_PENDING_NOTIFYQ
 WHERE DEST_ID IN ('805061809210','323045306')
 GROUP BY DEST_ID, DEST_TYPE, NOTIFY_STATUS;

-- Tracker
SELECT MDN, LAST_NOTIFIED_TIME
  FROM DG.MDN_NOTIFY_TRACKER
 WHERE MDN IN ('805061809210','323045306');

-- Index (001)
SELECT INDEX_NAME FROM USER_INDEXES WHERE TABLE_NAME = 'MDN_NOTIFY_TRACKER';
```

## Greps

```bash
# Everything
grep -nE "\[XCAP-DEBULK-FLOW\]" XDMDataMgr.log | tail -200

# Config / 1s poll
grep -nE "STEP-SCH2|STEP-CONF1|STEP-HG1 Poll start|STEP-9A" XDMDataMgr.log | tail -20

# Enqueue
grep -nE "STEP-SN2A|STEP-P2|STEP-P3" XDMDataMgr.log | tail -30

# Hold / claim / send
grep -nE "STEP-HG2A|STEP-HG3C|STEP-HG5|STEP-6A|STEP-11A Rule|STEP-12 Worker|STEP-12A|STEP-12B" XDMDataMgr.log | tail -50

# Standby / recovery
grep -nE "STEP-1A Skipping|STEP-2 Startup recovery" XDMDataMgr.log | tail -15
```

## Optimized happy path (CAT after subscriber change)

```text
STEP-SN2A WATCHER destType=2 destId=805061809210
STEP-P3 Upserted MDN tracker
STEP-HG2A eligibleCount=0     (before period)
… wait XCAP_NOTIFICATION_PERIOD …
STEP-HG3C destId=805061809210
STEP-HG5 NOTIFY_INITIATED
STEP-6A bundledCount=N watcher=805061809210
STEP-11A Rule …
STEP-12 destType=2 appliedRule=… isSuccess=true
STEP-12A cleanedRows=N
```
