/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;

public class KnDeviceImplInfoDTO implements IIdentifier {

    private String pttServerId;

    private String deviceId;

    private String deviceImpu;

    private String deviceImpl;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceImpu() {
        return deviceImpu;
    }

    public void setDeviceImpu(String deviceImpu) {
        this.deviceImpu = deviceImpu;
    }

    public String getDeviceImpl() {
        return deviceImpl;
    }

    public void setDeviceImpl(String deviceImpl) {
        this.deviceImpl = deviceImpl;
    }

    @Override
    public String getObjectId() {
        return pttServerId;
    }

    @Override
    public String toString() {
        return "KnDeviceImplInfo{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceImpu='" + deviceImpu + '\'' +
                ", deviceImpl='" + deviceImpl + '\'' +
                '}';
    }
}
