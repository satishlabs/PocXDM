/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

public class KnXDMDeviceAddlInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 3161758901935635592L;
    private String deviceSerialNo;
    private String deviceIMEI1;
    private String deviceIMEI2;
    private String deviceInfo;
    private String deviceVersion;

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
    public String getObjectId() {
        return deviceSerialNo;
    }

    @Override
    public String toString() {
        return "KnXDMDeviceAddlInfoDTO{" +
                "deviceSerialNo='" + deviceSerialNo + '\'' +
                ", deviceIMEI1='" + deviceIMEI1 + '\'' +
                ", deviceIMEI2='" + deviceIMEI2 + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", deviceVersion='" + deviceVersion + '\'' +
                '}';
    }
}
