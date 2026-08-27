/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dto;

public class KnDefaultMCSClientInfo {
    private Integer pv;
    private String clientFS2;
    private Integer isDefault;

    public Integer getPv() {
        return pv;
    }

    public void setPv(Integer pv) {
        this.pv = pv;
    }

    public String getClientFS2() {
        return clientFS2;
    }

    public void setClientFS2(String clientFS2) {
        this.clientFS2 = clientFS2;
    }

    public Integer getIsDefault() {return isDefault; }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return "KnDefaultMCSClientInfo{" +
                "pv=" + pv +
                ", clientFS2='" + clientFS2 + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
