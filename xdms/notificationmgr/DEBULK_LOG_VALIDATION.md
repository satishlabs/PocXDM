# XCAP Debulk Log Validation Guide

This guide validates the end-to-end temporal/debulk flow using logs only (VM no-debug scenario).

## 1) Common log marker

All new tracing logs include this tag:

`[XCAP-DEBULK-FLOW]`

Use it to filter quickly.

## 2) Scheduler validation (`KnXDMSLoader`)

Expected sequence:

- `STEP-SCH1 Initializing XCAP notify poller scheduler`
- `STEP-SCH2 ...` (optimized OR legacy branch)
- `STEP-SCH3 Scheduler active ...`

Meaning:

- `STEP-SCH2 Optimized mode ... 1 second` => epoch mode ON
- `STEP-SCH2 Legacy mode ...` => old cadence path

Capture command:

```bash
grep -RsnE "STEP-SCH1|STEP-SCH2|STEP-SCH3|XCAP notify poller scheduler" XDMDataMgr.log | tail -100
```

## 3) Producer/save-first validation (`KnXcapDiffNotifierImpl`)

For each producer call path you should see:

- `STEP-P1 Save-first entry ...`
- `STEP-P2 Saved notifications to DG.XCAP_PENDING_NOTIFYQ ...`
- `STEP-P3A MDN extraction complete ...`
- `STEP-P3 Upserted MDN tracker entries ...`
- `STEP-P4 Triggered microservice etag notify ...`

Meaning:

- Queue write happened before send.
- Tracker row registration happened for epoch gating.

Capture command:

```bash
grep -RsnE "STEP-P0|STEP-P1|STEP-P2|STEP-P3A|STEP-P3|STEP-P4|extractWatcherMdnsFromDiff|extractWatcherMdnsFromDirChg" XDMDataMgr.log | tail -300
```

## 4) Hold-and-gather poll validation (`KnXcapDiffNotifier.pollRecord`)

Expected sequence:

- `STEP-HG1 Poll start ...`
- `STEP-HG2 ...` or `STEP-HG2 Legacy ...`
- `STEP-HG3 ...`
- `STEP-HG4 Final record assembly complete ...`
- `STEP-HG5 Marked rows as NOTIFY_INITIATED ...`
- `STEP-HG6 ...` (optimized only)
- `STEP-HG7 Poll cycle completed ...`

Meaning:

- Optimized mode checks tracker eligibility before claim.
- Claimed rows transition to `NOTIFY_INITIATED`.
- Tracker timestamp and cleanup are executed in optimized path.

Capture command:

```bash
grep -RsnE "STEP-HG1|STEP-HG2|STEP-HG2A|STEP-HG3|STEP-HG4|STEP-HG5|STEP-HG6|STEP-HG6A|STEP-HG6B|STEP-HG7|fetchEligibleMdns" XDMDataMgr.log | tail -300
```

## 5) Grouping and dispatch validation (`KnXcapNotifyProcessor`)

Expected sequence:

- `STEP-1 Poll cycle started`
- `STEP-3 Poll complete ...`
- `STEP-4 Processing claimed records ...`
- `STEP-5 ... subscriber context ...`
- `STEP-6 Dispatch summary ...`
- `STEP-7 Cleanup complete ...`

Key evidence from `STEP-6`:

- `groupedWatchers` > 0 (optimized grouping active)
- `groupedPayloads` indicates how many rows were bundled

Capture command:

```bash
grep -RsnE "STEP-1 Poll cycle started|STEP-3 Poll complete|STEP-4 Processing claimed records|STEP-5|STEP-6 Dispatch summary|STEP-7 Cleanup complete" XDMDataMgr.log | tail -300
```

## 6) Per-watcher worker and rule validation (`KnWatcherDebulkSendNotification`)

Expected sequence per watcher:

- `STEP-8 Worker started ... bundledCount=...`
- `STEP-9 Resolved payload threshold ...`
- `STEP-10 Computed bundle size ...`
- `STEP-11 Distinct document analysis ...`
- `STEP-12 Worker completed ... appliedRule=... isSuccess=...`

Rule values in logs:

- `RULE-I-CONSOLIDATED`
- `RULE-I-OVERSIZE-FALLBACK`
- `RULE-II-MULTI-DOC`

Capture command:

```bash
grep -RsnE "STEP-8 Worker started|STEP-9 Resolved payload threshold|STEP-10 Computed bundle size|STEP-11 Distinct document analysis|STEP-12 Worker completed|RULE-I-CONSOLIDATED|RULE-I-OVERSIZE-FALLBACK|RULE-II-MULTI-DOC" XDMDataMgr.log | tail -300
```

## 7) DB cross-check SQL (optional but recommended)

```sql
SELECT COUNT(*) AS PENDING_CNT FROM DG.XCAP_PENDING_NOTIFYQ;

SELECT FIRST 50 INSERTION_TIME, DEST_ID, DEST_TYPE, NOTIFY_STATUS, CID, MSG_TYPE
FROM DG.XCAP_PENDING_NOTIFYQ
WHERE NOTIFY_STATUS IN (1,2)
ORDER BY INSERTION_TIME DESC;

SELECT MDN, LAST_NOTIFIED_TIME
FROM DG.MDN_NOTIFY_TRACKER
ORDER BY LAST_NOTIFIED_TIME DESC;
```

Tracker-upsert error capture command:

```bash
grep -RsnE "STEP-SF0|STEP-SF1|STEP-SF2|tracker upsert|SQLException during tracker upsert|KnPersistenceException during tracker upsert|Unexpected exception during tracker upsert|MDN_NOTIFY_TRACKER" XDMDataMgr.log | tail -300
```

## 7A) Fresh-run automation validation (truncate + re-trigger)

Use this flow when you want to prove that tracker rows are auto-created by code (no manual insert).

1) Truncate queue in primary DB (`ttdir`):

```sql
TRUNCATE TABLE DG.XCAP_PENDING_NOTIFYQ;
SELECT COUNT(*) AS PENDING_CNT FROM DG.XCAP_PENDING_NOTIFYQ;
```

2) Truncate tracker in secondary DB (`ttdir DG_017001_6`):

```sql
TRUNCATE TABLE DG.MDN_NOTIFY_TRACKER;
SELECT COUNT(*) AS TRACKER_CNT FROM DG.MDN_NOTIFY_TRACKER;
```

3) Trigger fresh notifications from management operation (create/update flows).

4) Validate logs for automatic tracker insert and poller DB selection:

```bash
grep -RsnE "STEP-P2|STEP-P3A|STEP-P3|STEP-SF1A|STEP-SF2|STEP-HG1A|STEP-HG2A|STEP-HG3|STEP-HG5|STEP-HG7" XDMDataMgr.log | tail -400
```

Expected evidence:

- `STEP-P2` queue save is done.
- `STEP-SF1A Using secondary DB for MDN tracker upsert` appears.
- `STEP-SF2 Tracker upsert completed` appears.
- `STEP-HG1A Using secondary DB for MDN tracker operations` appears.
- `STEP-HG2A eligibleCount > 0` and then `STEP-HG3 claimedRows > 0`.

5) Re-check both DB tables:

Primary DB (`ttdir`):

```sql
SELECT COUNT(*) AS PENDING_CNT FROM DG.XCAP_PENDING_NOTIFYQ WHERE NOTIFY_STATUS = 1;
SELECT FIRST 50 INSERTION_TIME, DEST_ID, DEST_TYPE, NOTIFY_STATUS, CID, MSG_TYPE
FROM DG.XCAP_PENDING_NOTIFYQ
WHERE NOTIFY_STATUS IN (1,2)
ORDER BY INSERTION_TIME DESC;
```

Secondary DB (`ttdir DG_017001_6`):

```sql
SELECT COUNT(*) AS TRACKER_CNT FROM DG.MDN_NOTIFY_TRACKER;
SELECT FIRST 50 MDN, LAST_NOTIFIED_TIME
FROM DG.MDN_NOTIFY_TRACKER
ORDER BY LAST_NOTIFIED_TIME DESC;
```

## 8) Operational checkpoints

1. Trigger rapid multiple changes for same watcher.
2. Confirm producer logs `STEP-P1..STEP-P4`.
3. Confirm poll logs `STEP-HG1..STEP-HG7`.
4. Confirm processor logs show grouped watcher dispatch.
5. Confirm worker logs show one `STEP-8..STEP-12` per watcher with `bundledCount > 1`.

## 9) Quick grep command (Linux VM)

```bash
grep -n "XCAP-DEBULK-FLOW" <xdm_log_file>
```

CID-scoped capture (replace `<CID>`):

```bash
grep -Rsn "<CID>" XDMDataMgr.log | tail -300
```

## 10) Quick grep command (Windows PowerShell)

```powershell
Select-String -Path "<xdm_log_file>" -Pattern "XCAP-DEBULK-FLOW"
```

