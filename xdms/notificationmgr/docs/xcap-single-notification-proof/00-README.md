# XCAP Single Notification — Full Proof Pack

**Epic:** Optimization of XCAP Watcher Notifications via Temporal Workflow Management  
**Opportunity-1:** One watcher notification per epoch (multiple deltas). Oversized diffs fall back to directory / full-document style notify.  
**Audience:** QA, TestRail, development, reviewers  
**How to use:** Fill **Evidence captured** on each SN sheet, attach SQL + `[XCAP-DEBULK-FLOW]` log snippets, tick Pass/Fail on `99-SIGNOFF.md`.

## Lab pair

| Role | MDN |
|---|---|
| Subscriber / document owner | `323045306` |
| CAT watcher | `805061809210` |

Log file: `XDMDataMgr.log`  
Marker: `[XCAP-DEBULK-FLOW]`

## Files in this pack

| File | Purpose |
|---|---|
| `00-JIRA-COVERAGE-MATRIX.md` | Every Jira clause → scenario + proof file |
| `01-EVIDENCE-CHEATSHEET.md` | Master SQL + greps |
| `SN-CFG.md` … `SN-021.md` | One proof sheet per scenario |
| `99-SIGNOFF.md` | Team sign-off checklist |

Open any `.md` in Word: File → Open → save as `.docx` if needed.

## Constants used by this code

| Field | Value | Meaning |
|---|---|---|
| NOTIFY_STATUS | `1` | PENDING |
| NOTIFY_STATUS | `2` | NOTIFY_INITIATED (in-progress) |
| DEST_TYPE | `1` | Owner self (immediate when optimized) |
| DEST_TYPE | `2` | Related XCAP watcher (wait-and-bundle) |

## Intentional vs Jira wording

| Jira text | Proof pack |
|---|---|
| Temporal + sharding | Not claimed. Poller + worker pool is the pipeline. |
| Housekeeping `<= now + period` | Proof uses **`now - period`** (correct). |
| Flag off “do nothing” | Epoch engine off; **legacy send still works**. |
| Hold all notifies | **Watchers** wait. **Owner own-change** is immediate (SN-017). |
