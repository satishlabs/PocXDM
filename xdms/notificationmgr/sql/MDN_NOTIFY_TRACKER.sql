-- Run this script on the secondary database (XDM_SHARED_DATA).
-- DG.XCAP_PENDING_NOTIFYQ stays on the primary database. The application
-- never joins the two tables in one statement.
--
-- Wait-and-bundle tracker for XCAP watcher notifications.
-- One row per subscriber. LAST_NOTIFIED_TIME is the start of the current
-- collection window. The poller sends a subscriber only after
-- LAST_NOTIFIED_TIME is older than XCAP_NOTIFICATION_PERIOD.
--
-- Housekeeping deletes a row only when that window has elapsed AND the
-- subscriber has no PENDING or NOTIFY_INITIATED rows in DG.XCAP_PENDING_NOTIFYQ.
-- The epic text "LAST_NOTIFIED_TIME >= Current_Time + period" is inverted;
-- a last-notified time cannot be in the future. The running code uses
-- LAST_NOTIFIED_TIME <= Current_Time - XCAP_NOTIFICATION_PERIOD.

CREATE TABLE DG.MDN_NOTIFY_TRACKER (
    MDN                VARCHAR(64) NOT NULL,
    LAST_NOTIFIED_TIME DATETIME YEAR TO FRACTION(3) NOT NULL,
    PRIMARY KEY (MDN)
) LOCK MODE ROW;

CREATE INDEX IDX_MDN_NOTIFY_TRACKER_LAST_TS
    ON DG.MDN_NOTIFY_TRACKER (LAST_NOTIFIED_TIME);
