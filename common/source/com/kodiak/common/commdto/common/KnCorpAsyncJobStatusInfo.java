/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.io.Serializable;

public class KnCorpAsyncJobStatusInfo implements Serializable {
    private static final long serialVersionUID = 76533232656456L;


    private String txnId;
    private String txnStatus;
    private String createTS;
    private String operationType;
    private String updateTS;

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getCreateTS() {
        return createTS;
    }

    public void setCreateTS(String createTS) {
        this.createTS = createTS;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getUpdateTS() {
        return updateTS;
    }

    public void setUpdateTS(String updateTS) {
        this.updateTS = updateTS;
    }

    @Override
    public String toString() {
        return "KnCorpAsyncJobStatusInfo{" +
                "txnId='" + txnId + '\'' +
                ", txnStatus='" + txnStatus + '\'' +
                ", createTS='" + createTS + '\'' +
                ", operationType='" + operationType + '\'' +
                ", updateTS='" + updateTS + '\'' +
                '}';
    }
}
