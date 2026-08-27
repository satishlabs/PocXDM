/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;

/**
 * *****************************************************************************
 * <p/>
 * Subsystem:   POC
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit kumar           Jab 15, 2011       7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * *******************************************************************************
 */
public class KnPAMSvcConfigDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622678010L;

    private String pttServerId;
    private int maxSubPerAccount;
    private int maxTxnPerBatch;

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public int getMaxSubPerAccount() {
        return maxSubPerAccount;
    }

    public void setMaxSubPerAccount(int maxSubPerAccount) {
        this.maxSubPerAccount = maxSubPerAccount;
    }

    public int getMaxTxnPerBatch() {
        return maxTxnPerBatch;
    }

    public void setMaxTxnPerBatch(int maxTxnPerBatch) {
        this.maxTxnPerBatch = maxTxnPerBatch;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(super.toString());
        strBuffer.append(", pttServerId - ").append(pttServerId);
        strBuffer.append(", maxSubPerAccount - ").append(maxSubPerAccount);
        strBuffer.append(", maxTxnPerBatch - ").append(maxTxnPerBatch);
        return strBuffer.toString();
    }

    public String getObjectId() {
        return pttServerId;
    }
}
