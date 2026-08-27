/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMDeviceAddlInfoDTO;

import java.io.Serializable;

public class KnDeviceDetailsDTO implements Serializable {

    private static final long serialVersionUID = 5734923679056664466L;

    private String deviceId;
    private int deviceType;
    private String deviceName;
    private String ugwInterop;
    private String deviceSubscrMdn;
    private String subscriberFs2;
    private String reqDeviceId;

    private KnXDMDeviceAddlInfoDTO deviceAddlInfo;

    public String getReqDeviceId() {
        return reqDeviceId;
    }

    public void setReqDeviceId(String reqDeviceId) {
        this.reqDeviceId = reqDeviceId;
    }

    public String getSubscriberFs2() {
        return subscriberFs2;
    }

    public void setSubscriberFs2(String subscriberFs2) {
        this.subscriberFs2 = subscriberFs2;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getUgwInterop() {
        return ugwInterop;
    }

    public void setUgwInterop(String ugwInterop) {
        this.ugwInterop = ugwInterop;
    }

    public String getDeviceSubscrMdn() {
        return deviceSubscrMdn;
    }

    public void setDeviceSubscrMdn(String deviceSubscrMdn) {
        this.deviceSubscrMdn = deviceSubscrMdn;
    }

    public KnXDMDeviceAddlInfoDTO getDeviceAddlInfo() {
        return deviceAddlInfo;
    }

    public void setDeviceAddlInfo(KnXDMDeviceAddlInfoDTO deviceAddlInfo) {
        this.deviceAddlInfo = deviceAddlInfo;
    }

    @Override
    public String toString() {
        return "KnDeviceDetailsDTO{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceType=" + deviceType +
                ", deviceName='" + deviceName + '\'' +
                ", ugwInterop='" + ugwInterop + '\'' +
                ", deviceSubscrMdn='" + deviceSubscrMdn + '\'' +
                ", subscriberFs2='" + subscriberFs2 + '\'' +
                ", reqDeviceId='" + reqDeviceId + '\'' +
                ", deviceAddlInfo='" + deviceAddlInfo + '\'' +
                '}';
    }
}
