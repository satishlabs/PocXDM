/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jan 27, 2012
 * Time: 11:27:16 AM
 * To change this template use File | Settings | File Templates.
 */
import com.kodiak.common.dto.IIdentifier;

public class KnPOCSuppDevicesDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676175L;

    private String pttServerId;
    private int recId;
    private String deviceVendor;
    private String deviceModel;
    private String deviceOS;
    private String deviceOSVersion;
    private long insertTime;


    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

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

    public String getObjectId() {
        return pttServerId;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String toString() {
           StringBuffer strBuffer = new StringBuffer(500);
           strBuffer.append("pttServerID - ").append(pttServerId)
                   .append(", Rec ID - ").append(recId)
                   .append(", Device Vendor - ").append(deviceVendor)
                   .append(", Device Model - ").append(deviceModel)
                   .append(", Device OS - ").append(deviceOS)
                   .append(", Device OS Version - ").append(deviceOSVersion)
                   .append(", Insert Time - ").append(insertTime);

        return strBuffer.toString();

    }
}
