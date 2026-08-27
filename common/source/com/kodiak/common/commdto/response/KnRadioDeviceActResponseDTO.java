/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;


public class KnRadioDeviceActResponseDTO extends KnXDMRespDTO {

    private static final long serialVersionUID = 7526471155622676149L;

    private String deviceId;
    private String deviceIMPI;
    private String deviceDigestName;
    private String deviceShared;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceIMPI() {
        return deviceIMPI;
    }

    public void setDeviceIMPI(String deviceIMPI) {
        this.deviceIMPI = deviceIMPI;
    }

    public String getDeviceDigestName() {
        return deviceDigestName;
    }

    public void setDeviceDigestName(String deviceDigestName) {
        this.deviceDigestName = deviceDigestName;
    }

    public String getDeviceShared() {
        return deviceShared;
    }

    public void setDeviceShared(String deviceShared) {
        this.deviceShared = deviceShared;
    }

    @Override
    public String toString() {
        return super.toString() +
                "KnRadioDeviceActResponseDTO{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceIMPI='" + deviceIMPI + '\'' +
                ", deviceDigestName='" + deviceDigestName + '\'' +
                ", deviceShared='" + deviceShared + '\'' +
                '}';
    }
}
