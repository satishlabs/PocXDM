/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.persistdat;

public class KnDeviceAddlInfoPersistDTO {
    private String deviceId;
    private String deviceSerialNo;
    private String deviceIMEI1;
    private String deviceIMEI2;
    private String deviceInfo;
    private String deviceVersion;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceSerialNo() {
        return deviceSerialNo;
    }

    public void setDeviceSerialNo(String deviceSerialNo) {
        this.deviceSerialNo = deviceSerialNo;
    }

    public String getDeviceIMEI1() {
        return deviceIMEI1;
    }

    public void setDeviceIMEI1(String deviceIMEI1) {
        this.deviceIMEI1 = deviceIMEI1;
    }

    public String getDeviceIMEI2() {
        return deviceIMEI2;
    }

    public void setDeviceIMEI2(String deviceIMEI2) {
        this.deviceIMEI2 = deviceIMEI2;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    @Override
    public String toString() {
        return "KnDeviceAddlInfoPersistDTO{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceSerialNo='" + deviceSerialNo + '\'' +
                ", deviceIMEI1='" + deviceIMEI1 + '\'' +
                ", deviceIMEI2='" + deviceIMEI2 + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", deviceVersion='" + deviceVersion + '\'' +
                '}';
    }
}
