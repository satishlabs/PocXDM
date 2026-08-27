/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

public class KnPreActivatedClientInfo implements IIdentifier {

    private Integer clientType;
    private String clientFS2;
    private Integer pv;
    private Integer defaultt;
    private Integer cameraType;


    public KnPreActivatedClientInfo() {
    }


    @Override
    public String getObjectId() {
        return null;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public String getClientFS2() {
        return clientFS2;
    }

    public void setClientFS2(String clientFS2) {
        this.clientFS2 = clientFS2;
    }

    public Integer getPv() {
        return pv;
    }

    public void setPv(Integer pv) {
        this.pv = pv;
    }

    public Integer getDefaultt() {
        return defaultt;
    }

    public void setDefaultt(Integer defaultt) {
        this.defaultt = defaultt;
    }

    public Integer getCameraType() {
        return cameraType;
    }

    public void setCameraType(Integer cameraType) {
        this.cameraType = cameraType;
    }

    @Override
    public String toString() {
        return "KnPreActivatedClientInfo{" +
                "clientType=" + clientType +
                ", clientFS2='" + clientFS2 + '\'' +
                ", pv=" + pv +
                ", defaultt=" + defaultt +
                ", cameraType=" + cameraType +
                '}';
    }
}