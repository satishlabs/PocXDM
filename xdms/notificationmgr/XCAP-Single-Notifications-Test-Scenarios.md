# XCAP Single Notification (Watcher Debulk) — Scenario Pack

**Document type:** Test / evidence pack for team review  
**Epic:** Optimization of XCAP Watcher Notifications via Temporal Workflow Management  
**Opportunity-1:** Each watcher is sent **one** notification (with multiple deltas) in a given epoch. If the compiled diff is too large, fall back to a lightweight directory / full-document style notify.  
**Scope:** XCAP watcher notifications only (CAT, Dispatcher, Handset **as watchers**). MCS is out of scope.  
**Implementation note:** The epic text mentions Temporal workflow and load sharding. Production code uses an in-process 1-second poller + worker pool with the same save / hold / send pipeline. That is sufficient to prove Opportunity-1.  
**Housekeeping formula:** Jira text says `LAST_NOTIFIED_TIME <= Current_Time + XCAP_NOTIFICATION_PERIOD`. That is inverted (future time). Implementation and this pack use **`LAST_NOTIFIED_TIME <= Current_Time - XCAP_NOTIFICATION_PERIOD`**.

**Reference MDNs (example lab pair)**  
- Subscriber / document owner: `323045306`  
- CAT watcher: `805061809210`  

**Log marker:** `[XCAP-DEBULK-FLOW]`  
**Log file:** `XDMDataMgr.log` (or site equivalent)

---

## 0. Jira requirement coverage matrix

| Jira ID | Requirement | Scenario(s) | Status |
|---|---|---|---|
| Opp-1 | One watcher notify per epoch for many short-interval changes | SN-003, SN-005, SN-015, SN-016 | Covered |
| Opp-1 | Too many diffs → directory / full-doc style | SN-009 | Covered |
| 1.1 | CAT / Dispatcher / Handset as monitoring clients | SN-005, SN-006, SN-017 | Covered |
| 2.1 Save First | Persist to DB; do not send on the save thread | SN-002 (legacy contrast), SN-003 | Covered |
| 2.1 Hold and Gather | Hold for the window; group by unique watcher | SN-004, SN-005, SN-006 | Covered |
| 2.1 Send Together | One bundled send when the window closes | SN-005 | Covered |
| 2.2 | `XCAP_NOTIFICATION_PERIOD` default 120s | SN-CFG, SN-004, SN-005 | Covered |
| 2.2 | `XCAP_DIFF_PAYLOAD_SIZE` default 2048 | SN-008, SN-009 | Covered |
| 2.2 | `XCAP_NOTIFICATION_OPTIMIZED` 0/1 default false | SN-002, SN-014, SN-CFG | Covered |
| 2.3 | Table `MDN_NOTIFY_TRACKER` (MDN, LAST_NOTIFIED_TIME) | SN-001 | Covered |
| 2.3 | Insert-if-absent; if exists do nothing | SN-003, SN-013 | Covered |
| 2.4.1.1 | Poll every 1s when flag on | SN-007 | Covered |
| 2.4.1.1 | Flag on + XDM ACTIVE | SN-011, SN-CFG | Covered |
| 2.4.1.1 | Max 100 MDNs, pending, epoch expired | SN-005, SN-018 | Covered |
| 2.4.1.1 | Hand to worker pool | SN-005 | Covered |
| 2.4.1.1 | Mark in-progress | SN-005 | Covered |
| 2.4.1.1 | Advance `LAST_NOTIFIED_TIME` to now | SN-005 | Covered |
| 2.4.1.1 | Tracker housekeeping | SN-019 | Covered |
| 2.4.1.1 Else | Not active / flag off → do not run epoch path | SN-002, SN-011, SN-014 | Covered |
| 2.4.1.2 Rule I in-limit | Consolidated document diff | SN-008 | Covered |
| 2.4.1.2 Rule I oversize | Directory etag fallback | SN-009 | Covered |
| 2.4.1.2 Rule II | Multi-document → directory etag | SN-010 | Covered |
| 2.4.1.2 Rule III | Core directory → directory etag | SN-020 | Covered |
| 2.4.1.2 | Cleanup `XCAP_PENDING_NOTIFYQ` after send | SN-005 | Covered |
| Product split | Document **owner** own-change is immediate (destType=1) | SN-017 | Covered (required so CAT wait is not confused with handset self-notify) |
| Resiliency | Startup recovery of `NOTIFY_INITIATED` | SN-012 | Covered |
| Rollback | Flag 0 restores legacy | SN-014 | Covered |
| Performance | Burst reduction | SN-015 | Covered |

---

## 1. Preconditions (all scenarios)

| Item | Expected |
|---|---|
| Both tables exist | `DG.XCAP_PENDING_NOTIFYQ`, `DG.MDN_NOTIFY_TRACKER` |
| Config loaded by running process | See SN-CFG |
| Node ACTIVE (except SN-011) | Poller `STEP-1` not `STEP-1A` |
| This JAR deployed | Class `KnWatcherDebulkSendNotification` present; logs contain `[XCAP-DEBULK-FLOW]` |

**NOTIFY_STATUS used by this code**

| Value | Meaning |
|---|---|
| `1` | PENDING (not in-progress) |
| `NOTIFY_INITIATED` | In-progress (claimed) |

**DEST_TYPE used by this code**

| Value | Meaning |
|---|---|
| `1` | Document-owner self notify (immediate when optimized) |
| `2` | Related XCAP watcher (wait-and-bundle) |
| `3` | MCS suppress — out of scope |

---

## SN-CFG — Runtime configuration loaded

**Jira:** 2.2  

**Steps**

1. Confirm C&P / microservices config:  
   - `XCAP_NOTIFICATION_OPTIMIZED=1` (or `0` for legacy cases)  
   - `XCAP_NOTIFICATION_PERIOD=120` (or shorter for lab)  
   - `XCAP_DIFF_PAYLOAD_SIZE=2048`  
2. Restart XDM if poll interval is only read at scheduler start.  
3. Grep startup / first poll.

**Expected**

```text
STEP-SCH2 Optimized mode detected ... effectiveIntervalSeconds=1
STEP-HG1 Poll start ... optimizedMode=true
STEP-CONF1 ... rawFlag=1 enabled=true
STEP-9A Payload threshold ... effectiveValue=2048   (on first bundled send)
```

**Evidence**

```bash
grep -nE "STEP-SCH2|STEP-HG1 Poll start|STEP-CONF1|STEP-9A Payload threshold" XDMDataMgr.log | tail -20
```

**Pass:** Flag, period, 1s poll, and payload size match config.  
**Fail:** `optimizedMode=false` while C&P shows 1.

---

## SN-001 — Create / verify MDN_NOTIFY_TRACKER

**Jira:** 2.3  
**TestRail:** XCAP-DEBULK-001  

**Steps**

1. If table is missing, run `dbmgr/source/create_mdn_notify_tracker.sql`.  
2. Query table and index metadata.

**Expected**

- Table `DG.MDN_NOTIFY_TRACKER` exists.  
- Columns: `MDN`, `LAST_NOTIFIED_TIME`.  
- PK on `MDN`.  
- Index `IDX_MDN_NOTIFY_TRACKER_LAST_TS`.

**Evidence**

```sql
SELECT COUNT(*) FROM DG.MDN_NOTIFY_TRACKER;
-- Index (Oracle/TimesTen USER_INDEXES or equivalent):
SELECT INDEX_NAME FROM USER_INDEXES WHERE TABLE_NAME = 'MDN_NOTIFY_TRACKER';
```

---

## SN-002 — Legacy mode keeps old behaviour (negative for wait-and-bundle)

**Jira:** 2.2 default false; 2.4.1.1 Else path  
**TestRail:** XCAP-DEBULK-002  

**Preconditions:** `XCAP_NOTIFICATION_OPTIMIZED=0`

**Steps**

1. Trigger one XCAP notification-producing change.  
2. Observe queue and tracker.  
3. Observe delivery timing.

**Expected**

- Row in `XCAP_PENDING_NOTIFYQ` destType=`1`.  
- **No** new `MDN_NOTIFY_TRACKER` row required for send.  
- Notification sent on **legacy** cadence (`NOTIFYJOB_AUDIT_INTRVAL`), not held for 120s.  
- Queue row cleaned after send.  
- Logs: `STEP-SN2A LEGACY`, `STEP-P3 Legacy mode`, `STEP-HG2 Legacy claim`, **no** `STEP-6A`.

**Evidence**

```bash
grep -nE "STEP-SN2A LEGACY|STEP-P3 Legacy mode|STEP-SF0 Optimized mode OFF|STEP-HG2 Legacy|STEP-6A Bundled" XDMDataMgr.log | tail -20
```

```sql
SELECT COUNT(*) FROM DG.MDN_NOTIFY_TRACKER WHERE MDN='805061809210';
```

**Pass:** Tracker not used; watcher/client still notified.  
**Fail:** destType=`2` hold or tracker upsert while flag=0.

---

## SN-003 — Rapid changes: many queue rows, one tracker row

**Jira:** Opportunity-1; 2.1 Save First; 2.3 insert-if-absent  
**TestRail:** XCAP-DEBULK-003  

**Preconditions:** `OPTIMIZED=1`, `PERIOD=120`. CAT watches subscriber `323045306`.

**Steps**

1. Trigger **10 rapid subscriber** updates (not CAT self-edit).  
2. Immediately query queue and tracker for CAT `805061809210`.  
3. Confirm `LAST_NOTIFIED_TIME` is **not** reset by repeats.

**Expected**

- Multiple `XCAP_PENDING_NOTIFYQ` rows: `DEST_ID='805061809210' AND DEST_TYPE=2 AND NOTIFY_STATUS=1`.  
- Exactly **one** tracker row for CAT.  
- Logs: `STEP-SN2A WATCHER destType=2 destId=805061809210`, `STEP-P2`, `STEP-P3`, `STEP-SF2` with `unchangedRows` on repeats.

**Evidence**

```sql
SELECT DEST_ID, DEST_TYPE, NOTIFY_STATUS, COUNT(*)
  FROM DG.XCAP_PENDING_NOTIFYQ
 WHERE DEST_ID='805061809210'
 GROUP BY DEST_ID, DEST_TYPE, NOTIFY_STATUS;

SELECT MDN, LAST_NOTIFIED_TIME FROM DG.MDN_NOTIFY_TRACKER
 WHERE MDN='805061809210';
```

```bash
grep -nE "STEP-SN2A WATCHER destId=805061809210|STEP-P3 Upserted|STEP-SF2 Tracker upsert" XDMDataMgr.log | tail -30
```

**Pass:** Many destType=`2` queue rows, one tracker row, timestamp stable.  
**Fail:** Empty destType=`2` after subscriber update (watcher path not hit).

---

## SN-004 — No watcher send before epoch expiry (negative)

**Jira:** 2.1 Hold; 2.4.1.1 eligibility  
**TestRail:** XCAP-DEBULK-004  

**Steps**

1. Complete SN-003.  
2. Poll during the pre-expiry window (`< XCAP_NOTIFICATION_PERIOD`).  
3. Confirm CAT client does not receive the bundled watcher notify.

**Expected**

- destType=`2` rows remain PENDING (`NOTIFY_STATUS=1`).  
- `STEP-HG2A ... eligibleCount=0` **or** no `STEP-HG3C destId=805061809210`.  
- **No** `STEP-12 Worker completed ... destType=2 watcher=805061809210`.

**Evidence**

```bash
grep -nE "STEP-HG2A Eligible MDNs fetched|STEP-HG3C Claimed queue row. destId=805061809210|STEP-12 Worker completed.*watcher=805061809210" XDMDataMgr.log | tail -30
```

**Pass:** No CAT destType=`2` send before period.  
**Fail:** CAT `STEP-12 destType=2` immediately.

---

## SN-005 — One bundled send after epoch expiry

**Jira:** Opportunity-1; 2.1 Send Together; 2.4.1.1 execution path; 2.4.1.2 cleanup  
**TestRail:** XCAP-DEBULK-005  

**Steps**

1. Wait until `XCAP_NOTIFICATION_PERIOD` elapses.  
2. Observe poller claim, worker dispatch, queue cleanup, tracker update.  
3. Confirm CAT received **exactly one** notify for that epoch (not one per change).

**Expected**

- `STEP-HG2A eligibleCount>=1` (capped at 100 MDNs).  
- `STEP-HG3C` for CAT; `STEP-HG5` in-progress.  
- Tracker `LAST_NOTIFIED_TIME` advanced to ~now.  
- **One** `STEP-6A Bundled watcher ... watcher=805061809210 bundledCount=N`.  
- One `STEP-8` / `STEP-12 destType=2` / `STEP-12A` cleanup.  
- Queue destType=`2` for claimed batch is empty after success.

**Evidence**

```bash
grep -nE "STEP-HG2A|STEP-HG3C Claimed queue row. destId=805061809210|STEP-HG5|Tracker updated for|STEP-6A Bundled watcher ready.*watcher=805061809210|STEP-12 Worker completed.*watcher=805061809210|STEP-12A" XDMDataMgr.log | tail -40
```

```sql
SELECT COUNT(*) FROM DG.XCAP_PENDING_NOTIFYQ
 WHERE DEST_ID='805061809210' AND DEST_TYPE=2;
SELECT MDN, LAST_NOTIFIED_TIME FROM DG.MDN_NOTIFY_TRACKER
 WHERE MDN='805061809210';
```

**Pass:** One bundled CAT send; queue cleaned; tracker time advanced.  
**Fail:** Multiple `STEP-6A` for same CAT in one epoch, or no send after period.

---

## SN-006 — Multi-watcher isolation

**Jira:** 2.1 group by unique user; 1.1 CAT / Dispatcher / Handset  
**TestRail:** XCAP-DEBULK-006  

**Steps**

1. Trigger changes for **3 different watchers** in the same window.  
2. Wait for epoch expiry.  
3. Verify one send per watcher and no payload mixing.

**Expected**

- Three tracker rows (one per watcher).  
- Three `STEP-6A` lines with three distinct `watcher=` values.  
- Each `STEP-12 dirURI` belongs to that watcher’s related documents, not another watcher’s.

**Evidence**

```bash
grep -nE "STEP-6A Bundled watcher ready|STEP-12 Worker completed" XDMDataMgr.log | tail -30
```

**Pass:** One bundle per watcher; no cross-MDN mix.  
**Fail:** One worker listing two watcher destIds, or shared dirURI from the wrong watcher.

---

## SN-007 — Poll cadence 1 second vs legacy

**Jira:** 2.4.1.1 “executes explicitly every 1 seconds”  
**TestRail:** XCAP-DEBULK-007  

**Steps**

1. Start with `OPTIMIZED=1`; confirm scheduler interval.  
2. Set `OPTIMIZED=0`; restart/reload per deployment; confirm legacy interval.

**Expected**

- ON → `overriding poll interval to 1 second`, `effectiveIntervalSeconds=1`.  
- OFF → `using configured interval` = `NOTIFYJOB_AUDIT_INTRVAL`.

**Evidence**

```bash
grep -nE "STEP-SCH2|overriding poll interval to 1 second|using configured interval" XDMDataMgr.log | tail -10
```

**Note:** Interval is applied when the poller is scheduled. Flip flag then restart XDM if SCH2 does not change.

---

## SN-008 — Rule I: single document, within payload limit

**Jira:** 2.4.1.2 Rule I in-limit  
**TestRail:** XCAP-DEBULK-008  

**Preconditions:** `XCAP_DIFF_PAYLOAD_SIZE` high enough (default 2048 or raise).

**Steps**

1. Generate multiple diffs for **one** document on the subscriber.  
2. Wait for bundled send.  
3. Inspect `appliedRule` and client content.

**Expected**

- `STEP-11A Rule I selected ... consolidated diff`  
- `appliedRule=RULE-I-CONSOLIDATED`  
- One send; inline diffs not discarded.

**Evidence**

```bash
grep -nE "STEP-11A Rule I selected|appliedRule=RULE-I-CONSOLIDATED|STEP-12 Worker completed.*watcher=805061809210" XDMDataMgr.log | tail -15
```

---

## SN-009 — Rule I fallback: payload exceeds threshold (“full document” / directory etag)

**Jira:** Opportunity-1 too many diffs; 2.4.1.2 Rule I oversize  
**TestRail:** XCAP-DEBULK-009  

**Preconditions:** `XCAP_DIFF_PAYLOAD_SIZE=1` (or very low).

**Steps**

1. Same single-document burst as SN-008.  
2. Wait for send.  
3. Confirm directory-etag path (`docDiffObj` stripped).

**Expected**

- `STEP-10 serialisedPayloadBytes > maxPayloadBytes`  
- `STEP-11A Rule I fallback ... Directory Etag`  
- `appliedRule=RULE-I-OVERSIZE-FALLBACK`

**Evidence**

```bash
grep -nE "STEP-10 Bundle size|STEP-11A Rule I fallback|appliedRule=RULE-I-OVERSIZE-FALLBACK" XDMDataMgr.log | tail -15
```

---

## SN-010 — Rule II: multiple documents

**Jira:** 2.4.1.2 Rule II  
**TestRail:** XCAP-DEBULK-010  

**Steps**

1. Change **two or more documents** for the same subscriber while CAT watches.  
2. Wait for send.

**Expected**

- `STEP-11 distinctDocCount>=2`  
- `STEP-11A Rule II selected ... Directory Etag`  
- `appliedRule=RULE-II-MULTI-DOC`  
- One unified directory-etag notify, not per-file consolidated Rule I.

**Evidence**

```bash
grep -nE "STEP-11 Bundle composition|STEP-11A Rule II|appliedRule=RULE-II-MULTI-DOC" XDMDataMgr.log | tail -15
```

---

## SN-011 — Standby / non-active does not process (negative)

**Jira:** 2.4.1.1 Validation “XDM is in active mode”; Else path  
**TestRail:** XCAP-DEBULK-011  

**Steps**

1. Force node to standby / non-ACTIVE.  
2. Ensure pending destType=`2` rows exist.  
3. Observe poller.

**Expected**

- `STEP-1A Skipping cycle because node is not ACTIVE`  
- No `STEP-HG3C` / `STEP-6A` / `STEP-12 destType=2` on this node.  
- Queue remains unprocessed by standby.

**Evidence**

```bash
grep -nE "STEP-1 Poll cycle started|STEP-1A Skipping cycle because node is not ACTIVE|STEP-6A Bundled" XDMDataMgr.log | tail -20
```

---

## SN-012 — Startup recovery of NOTIFY_INITIATED

**Jira:** Processing engine resiliency (required for in-progress rows after crash)  
**TestRail:** XCAP-DEBULK-012  

**Steps**

1. Seed queue rows with in-progress / `NOTIFY_INITIATED` status.  
2. Restart XDM.  
3. Observe first `KnXcapNotifyProcessor.run()` cycle.

**Expected**

- `STEP-2 Startup recovery: resetting NOTIFY_INITIATED rows to PENDING`  
- `STEP-2A` / `STEP-2B`  
- Rows return to PENDING then follow SN-004/SN-005.

**Evidence**

```bash
grep -nE "STEP-2 Startup recovery|STEP-2A Recovery reset|STEP-2B Startup recovery complete" XDMDataMgr.log | tail -10
```

---

## SN-013 — Duplicate tracker insert protection

**Jira:** 2.3 If entry exists, do nothing  
**TestRail:** XCAP-DEBULK-013  

**Steps**

1. With tracker row present, repeat producer calls for the same watcher.  
2. Query tracker count and `LAST_NOTIFIED_TIME`.

**Expected**

- Still **one** tracker row.  
- Timestamp unchanged until a successful epoch claim (SN-005).  
- `STEP-SF2` `unchangedRows` increases; PK duplicate treated as success.

**Evidence**

```sql
SELECT COUNT(*), MIN(LAST_NOTIFIED_TIME), MAX(LAST_NOTIFIED_TIME)
  FROM DG.MDN_NOTIFY_TRACKER WHERE MDN='805061809210';
```

```bash
grep -nE "STEP-SF2A MDN already in tracker|unchangedRows=" XDMDataMgr.log | tail -15
```

---

## SN-014 — Rollback by flag (legacy restored)

**Jira:** 2.2 flag; 2.4.1.1 Else  
**TestRail:** XCAP-DEBULK-014  

**Steps**

1. After running optimized, set `XCAP_NOTIFICATION_OPTIMIZED=0`.  
2. Restart/reload per deployment.  
3. Trigger notifications.

**Expected**

- Legacy path active (`STEP-HG2 Legacy`, `STEP-SN2A LEGACY`).  
- No new tracker upserts required for sends.  
- Notifications continue (not “do nothing” in the sense of outage). Jira Else “do nothing” applies to the **epoch engine**, not to all notifies.

**Evidence:** same greps as SN-002 after the flag change.

---

## SN-015 — Burst reduction

**Jira:** Opportunity-1 / 1.1 overhead reduction  
**TestRail:** XCAP-DEBULK-015  

**Steps**

1. Baseline: 50 rapid subscriber changes / 5 watchers with `OPTIMIZED=0`. Count per-watcher sends.  
2. Repeat with `OPTIMIZED=1`. Count `STEP-6A` / `STEP-12 destType=2` after one epoch.

**Expected**

- Optimized: about **1 send per watcher per epoch**, not 1 per change.  
- No sustained destType=`2` backlog after send.

**Evidence**

```bash
grep -c "STEP-6A Bundled watcher ready" XDMDataMgr.log
grep -c "STEP-12 Worker completed" XDMDataMgr.log
grep -nE "STEP-6 Dispatch summary|groupedWatchers|groupedPayloads" XDMDataMgr.log | tail -10
```

---

## SN-016 — Exactly one notify per watcher per epoch (Opportunity-1 primary)

**Jira:** Opportunity-1  

**Steps**

1. 20 subscriber changes within a few seconds for one CAT watcher.  
2. After one period, count CAT destType=`2` sends.  
3. Repeat for 3 epochs.

**Expected**

- Epoch 1: 1 CAT notify.  
- 3 epochs: 3 CAT notifies (not 60).  
- `bundledCount` reflects queued deltas.

**Evidence:** SN-005 greps scoped to CAT; client/CAT UI shows one refresh per epoch.

---

## SN-017 — Document owner immediate vs watcher delayed (product split)

**Jira:** Opportunity-1 is **watcher** notify. Own-document clients must not wait 120s.

**Steps**

1. Update subscriber `323045306`.  
2. Confirm subscriber destType=`1` immediate.  
3. Confirm CAT destType=`2` held until period.  
4. Update **on CAT** (own directory). Confirm CAT destType=`1` immediate; queue may be empty after send.

**Expected**

| Action | Queue | Tracker | Send |
|---|---|---|---|
| Subscriber own update | destType=`1` for subscriber | No | Immediate `STEP-12 destType=1` |
| Same save, CAT as related watcher | destType=`2` for CAT | Yes, insert-if-absent | After period `STEP-12 destType=2` |
| CAT own update | destType=`1` for CAT | No | Immediate; table often empty after send |

**Evidence**

```bash
grep -nE "STEP-SN2A SELF destId=323045306|STEP-SN2A WATCHER destId=805061809210|STEP-SN2A SELF destId=805061809210|STEP-12 Worker completed" XDMDataMgr.log | tail -30
```

**Pass:** Empty queue after **CAT self-update** is correct. Empty destType=`2` after **subscriber** update is a **fail**.

---

## SN-018 — Max 100 MDNs per poll cycle

**Jira:** 2.4.1.1 Fetch 100 MDN max  

**Steps**

1. Create pending destType=`2` for more than 100 distinct watchers with expired epochs (lab).  
2. Capture one poll cycle `STEP-HG2A`.

**Expected**

- `STEP-HG2A ... maxMdns=100 ... eligibleCount<=100`

**Evidence**

```bash
grep -nE "STEP-HG2A Eligible MDNs fetched. maxMdns=100" XDMDataMgr.log | tail -10
```

---

## SN-019 — Tracker housekeeping (corrected formula)

**Jira:** 2.4.1.1 Housekeeping (implemented as **minus** period)  

**Steps**

1. After send, tracker `LAST_NOTIFIED_TIME` is now.  
2. Wait until `LAST_NOTIFIED_TIME <= now - period` **and** no PENDING/INITIATED queue for that MDN.  
3. Confirm poller deletes stale tracker rows.

**Expected**

- `STEP-HG6B Tracker housekeeping. LAST_NOTIFIED_TIME <= now - period.`  
- Do **not** expect delete when timestamp is still within the current epoch.

**Evidence**

```bash
grep -nE "STEP-HG6B Tracker housekeeping" XDMDataMgr.log | tail -10
```

---

## SN-020 — Rule III: core directory changes

**Jira:** 2.4.1.2 Rule III  

**Steps**

1. Produce structural directory changes with **no** per-file `docDiffObj` (contact add/remove on directory tree).  
2. Wait for bundled watcher send.

**Expected**

- `STEP-11 coreDirectoryOnly=true`  
- `STEP-11A Rule III selected ... Directory Etag`  
- `appliedRule=RULE-III-CORE-DIRECTORY`

**Evidence**

```bash
grep -nE "coreDirectoryOnly=true|STEP-11A Rule III|appliedRule=RULE-III-CORE-DIRECTORY" XDMDataMgr.log | tail -15
```

---

## SN-021 — Send failure does not drop queue (negative)

**Jira:** 2.4.1.2 cleanup is **post** successful notification  

**Steps**

1. Force or observe a failed bundled send (`isSuccess=false`).  

**Expected**

- `STEP-12B` revert to PENDING.  
- **No** `STEP-12A` for that batch.  
- destType=`2` rows still present.

**Evidence**

```bash
grep -nE "STEP-12 Worker completed.*isSuccess=false|STEP-12B Send failed|STEP-12A Worker queue cleanup" XDMDataMgr.log | tail -15
```

---

## 2. End-to-end lab sequence (share with team)

Use CAT `805061809210` watching subscriber `323045306`, `OPTIMIZED=1`, `PERIOD=120`.

1. SN-CFG + SN-001  
2. Subscriber burst → SN-003 + SN-004 (before 120s)  
3. Wait period → SN-005 + SN-016 + rule SN-008/009/010/020 as data allows  
4. CAT self-edit → SN-017 (immediate, no destType=`2` hold)  
5. Flag 0 → SN-002 / SN-014  

**Master grep**

```bash
grep -nE "\[XCAP-DEBULK-FLOW\]" XDMDataMgr.log | tail -200
```

**Optimized happy-path order**

```text
STEP-SCH2 (1s)
STEP-P1 → STEP-P2 → STEP-SN2A WATCHER destType=2 → STEP-P3
STEP-1 → STEP-HG1 → STEP-HG2A (eligibleCount=0 until period)
… after period …
STEP-HG3C → STEP-HG5 → tracker LAST_NOTIFIED_TIME=now
STEP-6A bundledCount=N
STEP-8 → STEP-9/10/11/11A → STEP-12 destType=2 appliedRule=… → STEP-12A
```

---

## 3. Out of scope (do not fail the pack for these)

| Item | Reason |
|---|---|
| Temporal.io / mathematical load sharding | Not in this XDM implementation; poller+pool is the pipeline |
| MCS destType=`3` | Epic is XCAP watcher notifications |
| Holding **owner** self-notify for 120s | Contradicts Opportunity-1 (watchers) and lab handset UX |
| Jira housekeeping `+ period` | Incorrect formula; use minus period |

---

## 4. Sign-off checklist

| # | Scenario | Jira | Pass |
|---|---|---|---|
| SN-CFG | Config loaded | 2.2 | [ ] |
| SN-001 | Tracker table | 2.3 | [ ] |
| SN-002 | Legacy / flag off | 2.2 / Else | [ ] |
| SN-003 | Save-first + one tracker | 2.1 / 2.3 | [ ] |
| SN-004 | Hold before expiry | 2.1 / 2.4.1.1 | [ ] |
| SN-005 | One bundled send + cleanup | Opp-1 / 2.4.1.1–2 | [ ] |
| SN-006 | Multi-watcher isolation | 2.1 | [ ] |
| SN-007 | 1s poll | 2.4.1.1 | [ ] |
| SN-008 | Rule I | 2.4.1.2 | [ ] |
| SN-009 | Rule I fallback | Opp-1 / 2.4.1.2 | [ ] |
| SN-010 | Rule II | 2.4.1.2 | [ ] |
| SN-011 | Standby | 2.4.1.1 | [ ] |
| SN-012 | Startup recovery | Engine | [ ] |
| SN-013 | Insert-if-absent | 2.3 | [ ] |
| SN-014 | Rollback flag | 2.2 | [ ] |
| SN-015 | Burst reduction | 1.1 | [ ] |
| SN-016 | 1 notify per epoch | Opp-1 | [ ] |
| SN-017 | Owner immediate / watcher delay | Opp-1 scope | [ ] |
| SN-018 | Max 100 MDNs | 2.4.1.1 | [ ] |
| SN-019 | Housekeeping minus period | 2.4.1.1 | [ ] |
| SN-020 | Rule III | 2.4.1.2 | [ ] |
| SN-021 | Failed send keeps queue | 2.4.1.2 | [ ] |

**Document status:** Complete for team sharing. Attach SQL output + `[XCAP-DEBULK-FLOW]` snippets per scenario as evidence.
