/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class KnEXDMSNotifyDto{

    private String id;
    private String type;
    private String ver;
    private Integer notifyEventType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public Integer getNotifyEventType() {
        return notifyEventType;
    }

    public void setNotifyEventType(Integer notifyEventType) {
        this.notifyEventType = notifyEventType;
    }

    @Override
    public String toString() {
        return "KnEXDMSNotifyDto{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", ver='" + ver + '\'' +
                '}';
    }
}
