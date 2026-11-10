-- Secondary TimesTen DSN only: <localPttId>_6
-- Example from the primary card: ttdir DG_010011_6
-- XDM_SHARED_DATA = 6. Do not create this table on the primary DSN.
-- DG.XCAP_PENDING_NOTIFYQ stays on the primary DSN (example DG_010011).
-- LAST_NOTIFIED_TIME is epoch milliseconds. MDN is CHAR(15), matching the live table.
-- The application does not join this table with DG.XCAP_PENDING_NOTIFYQ.

CREATE TABLE DG.MDN_NOTIFY_TRACKER (
    MDN CHAR(15) NOT NULL,
    LAST_NOTIFIED_TIME BIGINT,
    PRIMARY KEY (MDN)
);
