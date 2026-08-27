/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnUserAgentDTO.java
 * Subsystem:  POC Activation WebServices
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Ajit Kumar           04-May-2012      7.2
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

import com.kodiak.common.dto.IIdentifier;

/**
 * Created by IntelliJ IDEA.
 * User: kodiak
 * Date: 4/5/12
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnUserAgentDTO implements IIdentifier {
    private static final long serialVersionUID = 7526471155622676200L;

    private String deviceUA;
    private String manufactName;
    private String deviceName;
    private String osName;
    private String osVersion;
    private String appName;
    private String appVersion;
    private String protocolVersion;
    private String uiVersion;
    private String userAgent;
    private String hsBaseband;

    public String getDeviceUA() {
        return deviceUA;
    }

    public void setDeviceUA(String deviceUA) {
        this.deviceUA = deviceUA;
    }

    public String getManufactName() {
        return manufactName;
    }

    public void setManufactName(String manufactName) {
        this.manufactName = manufactName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
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

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getObjectId() {
        return manufactName;
    }

    public String getHsBaseband() {
        return hsBaseband;
    }

    public void setHsBaseband(String hsBaseband) {
        this.hsBaseband = hsBaseband;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append("Device_UA - ").append(deviceUA)
                .append("Manufacturer_Name - ").append(manufactName)
                .append(", Device_Name - ").append(deviceName)
                .append(", OS_Name - ").append(osName)
                .append(", OS_Version - ").append(osVersion)
                .append(", Application_Name - ").append(appName)
                .append(", Application_Version - ").append(appVersion)
                .append(", Protocol_Version - ").append(protocolVersion)
                .append(", UI_Version - ").append(uiVersion)
                .append(", hsBaseband - ").append(hsBaseband);
        return sb.toString();
    }
}
