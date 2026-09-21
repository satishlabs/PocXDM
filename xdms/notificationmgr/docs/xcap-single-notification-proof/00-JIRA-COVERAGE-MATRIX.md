# Jira coverage → proof files

| Jira | Requirement | Proof file | TestRail |
|---|---|---|---|
| 2.2 | Config loaded: PERIOD, PAYLOAD, OPTIMIZED | SN-CFG.md | — |
| 2.3 | Tracker table + PK + index | SN-001.md | XCAP-DEBULK-001 |
| 2.2 / Else | Flag=0 legacy, no tracker required | SN-002.md | XCAP-DEBULK-002 |
| Opp-1 / 2.1 / 2.3 | Rapid changes: many queue rows, one tracker | SN-003.md | XCAP-DEBULK-003 |
| 2.1 Hold / 2.4.1.1 | No send before epoch | SN-004.md | XCAP-DEBULK-004 |
| Opp-1 / 2.1 Send / 2.4.1.1–2 | One bundled send, in-progress, tracker now, queue cleanup | SN-005.md | XCAP-DEBULK-005 |
| 2.1 unique user / 1.1 | Multi-watcher isolation | SN-006.md | XCAP-DEBULK-006 |
| 2.4.1.1 1s poll | Optimized 1s vs legacy interval | SN-007.md | XCAP-DEBULK-007 |
| 2.4.1.2 Rule I | Consolidated diff within size | SN-008.md | XCAP-DEBULK-008 |
| Opp-1 / Rule I oversize | Directory etag fallback | SN-009.md | XCAP-DEBULK-009 |
| 2.4.1.2 Rule II | Multi-document directory etag | SN-010.md | XCAP-DEBULK-010 |
| 2.4.1.1 ACTIVE | Standby does not process | SN-011.md | XCAP-DEBULK-011 |
| Engine | Startup recovery NOTIFY_INITIATED | SN-012.md | XCAP-DEBULK-012 |
| 2.3 insert-if-absent | Duplicate tracker protection | SN-013.md | XCAP-DEBULK-013 |
| 2.2 rollback | Flag 0 restores legacy | SN-014.md | XCAP-DEBULK-014 |
| 1.1 / Opp-1 | Burst reduction | SN-015.md | XCAP-DEBULK-015 |
| Opp-1 | Exactly one notify per watcher per epoch | SN-016.md | — |
| Opp-1 scope | Owner immediate vs watcher delayed | SN-017.md | — |
| 2.4.1.1 | Max 100 MDNs per cycle | SN-018.md | — |
| 2.4.1.1 housekeeping | LAST_NOTIFIED_TIME <= now - period | SN-019.md | — |
| 2.4.1.2 Rule III | Core directory etag | SN-020.md | — |
| 2.4.1.2 post-send cleanup | Failed send keeps queue | SN-021.md | — |
