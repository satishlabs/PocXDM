/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMailHookRespDTO.java
 * Subsystem:
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Kr. Acharyya    30/5/12       7.2
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
 * ************************************************************************
 */
package com.kodiak.common.commdto.response;

import java.util.Collection;

public class KnMailHookRespDTO {
    private int status;
    private int httpStatusCode;
    private String errorCode;
    private Collection<String> errorParamList;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Collection<String> getErrorParamList() {
        return errorParamList;
    }

    public void setErrorParamList(Collection<String> errorParamList) {
        this.errorParamList = errorParamList;
    }

     public String toString() {
        StringBuilder strBuffer = new StringBuilder(100);
        strBuffer.append(" status - ").append(status)
                .append(", httpStatusCode - ").append(httpStatusCode)
                .append(", errorCode - ").append(errorCode)
                 .append(", errorParamList - ").append(errorParamList);
        return strBuffer.toString();
    }
}
