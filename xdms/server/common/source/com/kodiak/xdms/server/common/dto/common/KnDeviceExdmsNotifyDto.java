/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class KnDeviceExdmsNotifyDto extends KnEXDMSNotifyDto {
    private String deviceId;
    private String deviceSubscrMdn;
    private String deviceType;
    private Long lastUpdateTime;
    private Integer corpid;
    private Integer lmrInteropFlag;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceSubscrMdn() {
        return deviceSubscrMdn;
    }

    public void setDeviceSubscrMdn(String deviceSubscrMdn) {
        this.deviceSubscrMdn = deviceSubscrMdn;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    public Integer getCorpid() {
        return corpid;
    }
    public void setCorpid(Integer corpid) {
        this.corpid = corpid;
    }

    public Integer getLmrInteropFlag() {
        return lmrInteropFlag;
    }

    public void setLmrInteropFlag(Integer lmrInteropFlag) {
        this.lmrInteropFlag = lmrInteropFlag;
    }

    @Override
    public String toString() {
        return "KnDeviceExdmsNotifyDto{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceSubscrMdn='" + deviceSubscrMdn + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", lastUpdateTime=" + lastUpdateTime +
                ", corpid=" + corpid +
                ", lmrInteropFlag=" + lmrInteropFlag +
                '}';
    }
}
