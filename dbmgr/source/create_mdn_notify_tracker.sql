-- =============================================================================
-- XCAP Temporal Notification Optimization
-- Creates DG.MDN_NOTIFY_TRACKER for epoch-gated watcher notification debulking.
-- Target: TimesTen / Oracle-compatible dialect used by XDM (DG schema).
-- TestRail: XCAP-DEBULK-001
-- =============================================================================

-- Drop is intentionally omitted for production safety.
-- Re-run only after confirming the table does not already exist.

CREATE TABLE DG.MDN_NOTIFY_TRACKER (
    MDN                 VARCHAR(32)   NOT NULL,
    LAST_NOTIFIED_TIME  BIGINT        NOT NULL,
    PRIMARY KEY (MDN)
);

-- Speeds epoch eligibility scans ordered by LAST_NOTIFIED_TIME.
CREATE INDEX IDX_MDN_NOTIFY_TRACKER_LAST_TS
    ON DG.MDN_NOTIFY_TRACKER (LAST_NOTIFIED_TIME);

-- Verification queries (manual):
-- SELECT COUNT(*) FROM DG.MDN_NOTIFY_TRACKER;
-- SELECT INDEX_NAME FROM USER_INDEXES WHERE TABLE_NAME = 'MDN_NOTIFY_TRACKER';
