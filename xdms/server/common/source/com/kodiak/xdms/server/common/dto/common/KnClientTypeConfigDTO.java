/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

/**
 * Created by hanwar on 08-02-2016.
 */
public class KnClientTypeConfigDTO {

    private int clientType;
    private int isEnable;
    private int enableSuppDevChk;

    public int getEnableSuppDevChk() {
        return enableSuppDevChk;
    }

    public void setEnableSuppDevChk(int enableSuppDevChk) {
        this.enableSuppDevChk = enableSuppDevChk;
    }

    public int getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(int isEnable) {
        this.isEnable = isEnable;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    @Override
    public String toString() {
        return "KnClientTypeConfigDTO{" +
                "clientType=" + clientType +
                ", isEnable=" + isEnable +
                ", enableSuppDevChk=" + enableSuppDevChk +
                '}';
    }
}
