/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

/**
 * Created by schandra on 27-10-2016.
 */
public class KnMicroSvcsCommonConfig {
    private String countryCode;
    private int clusterId;
    private String paramName;
    private int paramScope;
    private String path;
    private String paramValue;
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

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
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
        strBuffer.append(", paramName - ").append(paramName);
        strBuffer.append(", paramScope - ").append(paramScope);
        strBuffer.append(", path - ").append(path);
        strBuffer.append(", paramValue - ").append(paramValue);
        strBuffer.append(", lastUpdateTime - ").append(lastUpdateTime);
        return strBuffer.toString();
    }
}
