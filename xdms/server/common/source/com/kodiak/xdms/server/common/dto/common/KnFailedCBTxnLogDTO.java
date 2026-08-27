/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * Created by schandra on 18-01-2017.
 */
public class KnFailedCBTxnLogDTO implements IIdentifier {

    private String mdn;
    private long lastUpdateTime;
    private int impactedClusterId;
    private int txnType;
    private int loggedBy;

    @Override
    public String getObjectId() {
        return null;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getImpactedClusterId() {
        return impactedClusterId;
    }

    public void setImpactedClusterId(int impactedClusterId) {
        this.impactedClusterId = impactedClusterId;
    }

    public int getTxnType() {
        return txnType;
    }

    public void setTxnType(int txnType) {
        this.txnType = txnType;
    }

    public int getLoggedBy() {
        return loggedBy;
    }

    public void setLoggedBy(int loggedBy) {
        this.loggedBy = loggedBy;
    }

    @Override
    public String toString() {
        return "KnFailedCBTxnLogDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", lastUpdateTime=" + lastUpdateTime +
                ", impactedClusterId=" + impactedClusterId +
                ", txnType=" + txnType +
                ", loggedBy=" + loggedBy +
                '}';
    }
}
