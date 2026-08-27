/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

/**
 * Created by schandra on 27-10-2016.
 */
public class KnMicroSvcsServiceConfig {

    private String countryCode;
    private int clusterId;
    private String serviceType;
    private String serviceVer;
    private String paramName;
    private int paramScope;
    private String path;
    private String paramvalue;
    private long lastUpdateTime;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceVer() {
        return serviceVer;
    }

    public void setServiceVer(String serviceVer) {
        this.serviceVer = serviceVer;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public int getParamScope() {
        return paramScope;
    }

    public void setParamScope(int paramScope) {
        this.paramScope = paramScope;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParamvalue() {
        return paramvalue;
    }

    public void setParamvalue(String paramvalue) {
        this.paramvalue = paramvalue;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(super.toString());
        strBuffer.append(", countryCode - ").append(countryCode);
        strBuffer.append(", clusterId - ").append(clusterId);
        strBuffer.append(", serviceType - ").append(serviceType);
        strBuffer.append(", serviceVer - ").append(serviceVer);
        strBuffer.append(", paramName - ").append(paramName);
        strBuffer.append(", paramScope - ").append(paramScope);
        strBuffer.append(", path - ").append(path);
        strBuffer.append(", paramvalue - ").append(paramvalue);
        strBuffer.append(", lastUpdateTime - ").append(lastUpdateTime);
        return strBuffer.toString();
    }
}
