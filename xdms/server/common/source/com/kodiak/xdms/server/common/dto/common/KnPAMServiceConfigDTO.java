/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;

public class KnPAMServiceConfigDTO implements IIdentifier {
    private String pttServerId;
    private int maxSubscrPerAcc;
    private int maxTxnPerBatch;

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public int getMaxSubscrPerAcc() {
        return maxSubscrPerAcc;
    }

    public void setMaxSubscrPerAcc(int maxSubscrPerAcc) {
        this.maxSubscrPerAcc = maxSubscrPerAcc;
    }

    public int getMaxTxnPerBatch() {
        return maxTxnPerBatch;
    }

    public void setMaxTxnPerBatch(int maxTxnPerBatch) {
        this.maxTxnPerBatch = maxTxnPerBatch;
    }

   public String toString() {
        StringBuffer sb = new StringBuffer(50);
        sb.append(super.toString())
                .append(", pttServerId - ").append(pttServerId)
                .append(", maxSubscrPerAcc - ").append(maxSubscrPerAcc)
                .append(", maxTxnPerBatch - ").append(maxTxnPerBatch);
        return sb.toString();
    }

    public String getObjectId() {
        return pttServerId;
    }
}
