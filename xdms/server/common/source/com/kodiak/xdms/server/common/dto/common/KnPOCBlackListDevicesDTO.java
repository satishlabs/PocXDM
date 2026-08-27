/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

/**
 * Created by Deepak on 29/9/14.
 */
public class KnPOCBlackListDevicesDTO {


    private int recId;
    private String deviceVendor;
    private String deviceModel;
    private String deviceOS;
    private String deviceOSVersion;
    private long insertTime;
    private String protocolVersion;
    private String uiVersion;
    private String hsBaseband;

    public int getRecId() {
        return recId;
    }

    public void setRecId(int recId) {
        this.recId = recId;
    }

    public String getDeviceVendor() {
        return deviceVendor;
    }

    public void setDeviceVendor(String deviceVendor) {
        this.deviceVendor = deviceVendor;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceOS() {
        return deviceOS;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public String getDeviceOSVersion() {
        return deviceOSVersion;
    }

    public void setDeviceOSVersion(String deviceOSVersion) {
        this.deviceOSVersion = deviceOSVersion;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getUiVersion() {
        return uiVersion;
    }

    public void setUiVersion(String uiVersion) {
        this.uiVersion = uiVersion;
    }

    public String getHsBaseband() {
        return hsBaseband;
    }

    public void setHsBaseband(String hsBaseband) {
        this.hsBaseband = hsBaseband;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KnPOCBlackListDevicesDTO{");
        sb.append("recId=").append(recId);
        sb.append(", deviceVendor='").append(deviceVendor).append('\'');
        sb.append(", deviceModel='").append(deviceModel).append('\'');
        sb.append(", deviceOS='").append(deviceOS).append('\'');
        sb.append(", deviceOSVersion='").append(deviceOSVersion).append('\'');
        sb.append(", insertTime=").append(insertTime);
        sb.append(", protocolVersion='").append(protocolVersion).append('\'');
        sb.append(", uiVersion='").append(uiVersion).append('\'');
        sb.append(", hsBaseband='").append(hsBaseband).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
