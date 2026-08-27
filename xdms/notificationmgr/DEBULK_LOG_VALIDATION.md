# XCAP Debulk – Complete Log Capture & TestRail Evidence Guide

All flow tracing uses the common marker:

```text
[XCAP-DEBULK-FLOW]
```

Replace `XDMDataMgr.log` with your VM log path. On Windows PowerShell, use the `Select-String` variants at the bottom.

---

## 0) One-shot: capture FULL debulk flow (use this first)

### Linux / VM bash

```bash
# Complete flow – all steps, all rules, all errors
grep -nE "\[XCAP-DEBULK-FLOW\]" XDMDataMgr.log | tee /tmp/xcap-debulk-full.log

# Same, but only last ~2000 hits (fresh run)
grep -nE "\[XCAP-DEBULK-FLOW\]" XDMDataMgr.log | tail -2000 | tee /tmp/xcap-debulk-tail.log

# Full pipeline step sequence (producer → hold/gather → dispatch → worker)
grep -nE "STEP-(SCH|P|SF|HG|CONF|GN|SN|OUT)|STEP-[0-9]|STEP-ERR|RULE-I|RULE-II|RULE-III" XDMDataMgr.log | tee /tmp/xcap-debulk-steps.log
```

### CID / MDN scoped (replace placeholders)

```bash
WATCHER_MDN="<mdn>"
CID="<cid>"

grep -nE "\[XCAP-DEBULK-FLOW\].*(${WATCHER_MDN}|${CID})" XDMDataMgr.log | tee /tmp/xcap-debulk-${WATCHER_MDN}.log
```

---

## 1) Sanity gate (build / single class)

```bash
# Confirm single class definition (no duplicated block)
grep -n "class KnXcapNotifyProcessor" \
  xdms/notificationmgr/source/com/kodiak/xdms/notificationmgr/resources/KnXcapNotifyProcessor.java
# Expected: exactly 1 match

# Confirm new worker class present in deployed jar (after build/deploy)
jar tf <path-to-xdms-jar> | grep KnWatcherDebulkSendNotification
```

---

## 2) TestRail case → expected logs + grep

### XCAP-DEBULK-001 — Create MDN_NOTIFY_TRACKER

DB only (no log). Run:

```sql
-- @dbmgr/source/create_mdn_notify_tracker.sql
SELECT COUNT(*) FROM DG.MDN_NOTIFY_TRACKER;
-- Index check (TimesTen/Oracle):
SELECT INDEX_NAME FROM USER_INDEXES WHERE TABLE_NAME = 'MDN_NOTIFY_TRACKER';
-- Expect: IDX_MDN_NOTIFY_TRACKER_LAST_TS
```

---

### XCAP-DEBULK-002 — Legacy mode unchanged (`OPTIMIZED=0`)

```bash
grep -nE "STEP-CONF1|STEP-P0|STEP-P1|STEP-P2|STEP-P3 Legacy mode|STEP-SF0 Optimized mode OFF|STEP-HG2 Legacy|STEP-SCH2 Legacy" XDMDataMgr.log | tail -200
```

**Expect:**
- `rawFlag=0` / `enabled=false` / `optimizedMode=false`
- `STEP-P2` queue save
- `STEP-P3 Legacy mode – tracker upsert skipped` **OR** `STEP-SF0 Optimized mode OFF – skipping tracker upsert`
- **No** `STEP-SF1` / `STEP-SF2` tracker upsert
- Legacy poll: `STEP-HG2 Legacy claim complete`
- No `STEP-6A Bundled watcher` / no `STEP-8 Worker started`

```sql
SELECT COUNT(*) FROM DG.MDN_NOTIFY_TRACKER;           -- no new rows for this run
SELECT * FROM DG.XCAP_PENDING_NOTIFYQ WHERE DEST_ID='<mdn>';
```

---

### XCAP-DEBULK-003 — Optimized upserts single tracker row

```bash
grep -nE "STEP-P1|STEP-P2|STEP-P3 Upserted|STEP-P3A|STEP-SF1|STEP-SF1B|STEP-SF2|STEP-SF4" XDMDataMgr.log | tail -300
```

**Expect:** multiple `STEP-P2` for rapid changes; `STEP-SF2` with `unchangedRows` rising on repeats; one tracker row in DB.

```sql
SELECT MDN, LAST_NOTIFIED_TIME FROM DG.MDN_NOTIFY_TRACKER WHERE MDN='<mdn>';
SELECT COUNT(*) FROM DG.XCAP_PENDING_NOTIFYQ WHERE DEST_ID='<mdn>';
```

---

### XCAP-DEBULK-004 — No send before epoch expiry

```bash
grep -nE "STEP-HG1|STEP-HG2A|STEP-HG3 No eligible|STEP-HG7|STEP-8 Worker started|STEP-6A Bundled" XDMDataMgr.log | tail -300
```

**Expect during pre-expiry window:**
- `STEP-HG2A ... eligibleCount=0` and/or `STEP-HG3 No eligible watchers`
- **No** `STEP-8 Worker started` for that watcher

---

### XCAP-DEBULK-005 — One bundled send after epoch expiry

```bash
grep -nE "STEP-HG2A|STEP-HG3 Claimed|STEP-HG5|STEP-HG6|STEP-6A Bundled|STEP-6 Dispatch|STEP-8 Worker started|STEP-12 Worker completed|STEP-12A" XDMDataMgr.log | tail -400
```

**Expect:**
- `eligibleCount>=1`, `claimedRows>0`
- Exactly one `STEP-6A Bundled watcher ... bundledCount=N` per watcher
- One `STEP-8` / `STEP-12` pair; `STEP-12A ... cleanedRows=N`
- `STEP-HG6A Tracker timestamp update complete`

---

### XCAP-DEBULK-006 — Multi-watcher isolation

```bash
grep -nE "STEP-6A Bundled watcher|STEP-8 Worker started|STEP-12 Worker completed" XDMDataMgr.log | tail -200
```

**Expect:** one `STEP-6A` / `STEP-8` / `STEP-12` **per distinct watcher**; no mixed watcher IDs in the same worker line.

---

### XCAP-DEBULK-007 — Poll cadence 1s vs legacy

```bash
grep -nE "STEP-SCH2|STEP-SCH3|XCAP_NOTIFICATION_OPTIMIZED|overriding poll interval|using configured interval" XDMDataMgr.log | tail -100
```

**Expect:**
- ON → `effectiveIntervalSeconds=1` / `overriding poll interval to 1 second`
- OFF → `Legacy mode` / configured `NOTIFYJOB_AUDIT_INTRVAL`

---

### XCAP-DEBULK-008 — Rule I consolidated (within threshold)

```bash
grep -nE "STEP-9|STEP-10|STEP-11|STEP-11A Rule I selected|RULE-I-CONSOLIDATED|STEP-12 Worker completed" XDMDataMgr.log | tail -200
```

**Expect:** `appliedRule=RULE-I-CONSOLIDATED`

---

### XCAP-DEBULK-009 — Rule I oversize fallback

```bash
grep -nE "STEP-9A|STEP-10|STEP-11A Rule I fallback|RULE-I-OVERSIZE-FALLBACK|STEP-12 Worker completed" XDMDataMgr.log | tail -200
```

**Expect:** `appliedRule=RULE-I-OVERSIZE-FALLBACK` (set `XCAP_DIFF_PAYLOAD_SIZE` very low)

---

### XCAP-DEBULK-010 — Rule II multi-document

```bash
grep -nE "STEP-11|STEP-11A Rule II|RULE-II-MULTI-DOC|STEP-12 Worker completed" XDMDataMgr.log | tail -200
```

**Expect:** `appliedRule=RULE-II-MULTI-DOC`

---

### XCAP-DEBULK-011 — Standby does not process

```bash
grep -nE "STEP-1 Poll cycle started|STEP-1A Skipping cycle because node is not ACTIVE|STEP-6 Dispatch" XDMDataMgr.log | tail -150
```

**Expect:** `STEP-1A Skipping cycle ... not ACTIVE`; no `STEP-6` / `STEP-8` on standby.

---

### XCAP-DEBULK-012 — Startup recovery resets NOTIFY_INITIATED

```bash
grep -nE "STEP-2 Startup recovery|STEP-2A Recovery reset|STEP-2B Startup recovery complete" XDMDataMgr.log | tail -100
```

**Expect:** recovery on first cycle after restart; rows return to pending and process normally.

---

### XCAP-DEBULK-013 — Duplicate tracker insert protection

```bash
grep -nE "STEP-SF1B|STEP-SF2|unchangedRows|affectedRows|STEP-P3 Upserted" XDMDataMgr.log | tail -200
```

**Expect:** repeated producers → `unchangedRows` increases; DB still has **one** row per MDN.

---

### XCAP-DEBULK-014 — Rollback by config flag

```bash
grep -nE "STEP-CONF1|STEP-SCH2|STEP-P3 Legacy mode|STEP-SF0 Optimized mode OFF|STEP-HG2 Legacy" XDMDataMgr.log | tail -200
```

**Expect after `XCAP_NOTIFICATION_OPTIMIZED=0`:** legacy path only; no new tracker activity required for sends.

---

### XCAP-DEBULK-015 — Burst reduction / fanout

```bash
grep -nE "STEP-6B Fanout benchmark|STEP-6 Dispatch summary|groupedWatchers|groupedPayloads|fanoutReduction|STEP-8 Worker started|STEP-12 Worker completed" XDMDataMgr.log | tee /tmp/xcap-debulk-perf.log
```

**Expect (optimized ON):** `fanoutReduction > 0`, `groupedPayloads >> groupedWatchers`, fewer `STEP-8` than queue inserts.

Compare OFF vs ON:

```bash
# Count watcher sends in a time window (adjust log file / time filter as needed)
grep -c "STEP-8 Worker started" XDMDataMgr.log
grep -c "STEP-P2 Saved notifications" XDMDataMgr.log
```

---

## 3) Master grep cheatsheet (copy/paste)

```bash
########################
# A) EVERYTHING
########################
grep -nE "\[XCAP-DEBULK-FLOW\]" XDMDataMgr.log | tee /tmp/xcap-debulk-full.log

########################
# B) BY STAGE
########################
# Scheduler
grep -nE "STEP-SCH[123]" XDMDataMgr.log

# Producer save-first
grep -nE "STEP-P[0-4]|STEP-PX|STEP-P3A" XDMDataMgr.log

# Tracker upsert / secondary DB
grep -nE "STEP-SF[0-4]|STEP-SF1A|STEP-SF1B" XDMDataMgr.log

# Hold-and-gather poller
grep -nE "STEP-HG[1-7]|STEP-HG2A|STEP-HG5A|STEP-HG6[ABC]" XDMDataMgr.log

# Processor grouping / dispatch
grep -nE "STEP-[1-7][AB]? |STEP-6A|STEP-6B|STEP-7A" XDMDataMgr.log

# Worker + rules
grep -nE "STEP-(8|9|9A|10|11|11A|12|12A|ERR)|RULE-I-|RULE-II-|RULE-III-" XDMDataMgr.log

# Config resolve
grep -nE "STEP-CONF1|XCAP_NOTIFICATION_OPTIMIZED|XCAP_NOTIFICATION_PERIOD|XCAP_DIFF_PAYLOAD_SIZE" XDMDataMgr.log

# Errors only
grep -nE "STEP-ERR|tracker upsert failed|Unexpected Exception|Persister Txn occurred" XDMDataMgr.log

########################
# C) RULE OUTCOMES ONLY
########################
grep -nE "appliedRule=|RULE-I-CONSOLIDATED|RULE-I-OVERSIZE-FALLBACK|RULE-II-MULTI-DOC|RULE-III-CORE-DIRECTORY" XDMDataMgr.log
```

---

## 4) PowerShell (Windows VM / local)

```powershell
# Full flow
Select-String -Path "XDMDataMgr.log" -Pattern "\[XCAP-DEBULK-FLOW\]" |
  ForEach-Object { "$($_.LineNumber):$($_.Line)" } |
  Tee-Object -FilePath "xcap-debulk-full.log"

# Steps + rules
Select-String -Path "XDMDataMgr.log" -Pattern "STEP-(SCH|P|SF|HG|CONF)|STEP-[0-9]|RULE-I|RULE-II|RULE-III|STEP-ERR" |
  Tee-Object -FilePath "xcap-debulk-steps.log"

# Watcher scoped
$mdn = "<mdn>"
Select-String -Path "XDMDataMgr.log" -Pattern "\[XCAP-DEBULK-FLOW\].*$mdn" |
  Tee-Object -FilePath "xcap-debulk-$mdn.log"
```

---

## 5) End-to-end expected step order (optimized ON)

```text
STEP-SCH1 → STEP-SCH2 (1s) → STEP-SCH3
STEP-P0 → STEP-P1 → STEP-P2 → STEP-P2A → STEP-P3A → STEP-P3 → STEP-SF* → STEP-P4
STEP-1 → STEP-HG1 → STEP-HG2A → STEP-HG3 → STEP-HG4 → STEP-HG5 → STEP-HG6* → STEP-HG7
STEP-3 → STEP-4 → STEP-5 → STEP-6A → STEP-6 / STEP-6B → STEP-7
STEP-8 → STEP-9/9A → STEP-10 → STEP-11/11A → STEP-12 → STEP-12A
```

Legacy OFF skips: `STEP-SF*` insert path, `STEP-HG2A` eligibility, `STEP-6A`, `STEP-8..12` debulk worker.

---

## 6) Recommended TestRail evidence fields

| Field | Example |
|--------|---------|
| Automation Candidate | Yes/No |
| Build/Branch | release branch |
| Config Set | `OPTIMIZED=1, PERIOD=30, DIFF_PAYLOAD_SIZE=2048` |
| DB Node | primary / secondary (`XDM_SHARED_DATA` for tracker) |
| Evidence | SQL output + `/tmp/xcap-debulk-*.log` snippet + client capture |
