package com.kodiak.common.commdto.response;

import java.io.Serializable;

public class KnBulkOpsErrorDetail implements Serializable {
    private static final long serialVersionUID = 2368152933639235293L;
    private String mdn;
    private String errorCode;
    private String errorMessage;

    // Getters and Setters
    public String getMdn() { return mdn; }
    public void setMdn(String mdn) { this.mdn = mdn; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    @Override
    public String toString() {
        return "KnBulkErrorDetail{" +
                "mdn='" + mdn + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
