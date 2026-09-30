/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.notificationmgr.beans;

import java.io.Serializable;

/**
 * DTO representing a row from the NOTIFICATION_PRIORITY_CONFIG table.
 * Carries the full priority configuration for a given XCAP operation code.
 *
 * Priority tiers (PRIORITY_LEVEL column):
 *   10 = CRITICAL — immediate dispatch, always written to XCAP_PENDING_NOTIFYQ with PRIORITY=0
 *    5 = HIGH     — standard BAU processing, written with PRIORITY=5
 *    0 = LOW      — suppressed; DB write is skipped entirely, deferred to client 30-min refresh
 *
 * PRIORITY_VALUE is the value written into XCAP_PENDING_NOTIFYQ.PRIORITY column:
 *   CRITICAL → 0, HIGH → 5, LOW → 10 (never polled)
 */
public class KnNotifPriorityConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Primary key from NOTIFICATION_PRIORITY_CONFIG */
    private int opsId;

    /** Operation code used as the runtime lookup key */
    private int opsCode;

    /** Priority tier: 10=CRITICAL, 5=HIGH, 0=LOW */
    private String priorityLevel;

    /** Value written to XCAP_PENDING_NOTIFYQ.PRIORITY: 0=CRITICAL, 5=HIGH, 10=LOW */
    private int priorityValue;

    /** Human-readable description of the notification operation */
    private String notifyDesc;

    /** Timestamp of last config modification */
    private String lastModified;

    /** Subsystem label (e.g. "XCAP & MCSXCAP", "MCSXCAP", "XCAP") */
    private String opsName;

    public int getOpsId() {
        return opsId;
    }

    public void setOpsId(int opsId) {
        this.opsId = opsId;
    }

    public int getOpsCode() {
        return opsCode;
    }

    public void setOpsCode(int opsCode) {
        this.opsCode = opsCode;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public int getPriorityValue() {
        return priorityValue;
    }

    public void setPriorityValue(int priorityValue) {
        this.priorityValue = priorityValue;
    }

    public String getNotifyDesc() {
        return notifyDesc;
    }

    public void setNotifyDesc(String notifyDesc) {
        this.notifyDesc = notifyDesc;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getOpsName() {
        return opsName;
    }

    public void setOpsName(String opsName) {
        this.opsName = opsName;
    }

    @Override
    public String toString() {
        return "KnNotifPriorityConfigDTO{" +
                "opsId=" + opsId +
                ", opsCode=" + opsCode +
                ", priorityLevel=" + priorityLevel +
                ", priorityValue=" + priorityValue +
                ", notifyDesc='" + notifyDesc + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", opsName='" + opsName + '\'' +
                '}';
    }
}
