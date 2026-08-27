/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.io.Serializable;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnAPNProfileConfigDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar      June 04, 2019      9.1.1
 * <p/>
 * <p/>
 * KODIAK, 9th Floor, MFar Greenheart Phase IV
 * Manyata Tech Park, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnAPNProfileConfigDTO implements Serializable {

    private static final long serialVersionUID = 7576471155622673645L;

    private int apnId;
    private String apnName;
    private int isDefault;
    private String pttServerId;
    private String sipProxyUri;
    private String geoSipProxyUri;
    private String geoRegPrimF5Uri;
    private String geoRegGeoF5Uri;
    private String fdServiceUriCell;

    public KnAPNProfileConfigDTO() {}

    public KnAPNProfileConfigDTO(int apnId, String apnName, int isDefault, String pttServerId, String sipProxyUri,
                                 String geoSipProxyUri, String geoRegPrimF5Uri, String geoRegGeoF5Uri, String fdServiceUriCell) {
        this.apnId = apnId;
        this.apnName = apnName;
        this.isDefault = isDefault;
        this.pttServerId = pttServerId;
        this.sipProxyUri = sipProxyUri;
        this.geoSipProxyUri = geoSipProxyUri;
        this.geoRegPrimF5Uri = geoRegPrimF5Uri;
        this.geoRegGeoF5Uri = geoRegGeoF5Uri;
        this.fdServiceUriCell = fdServiceUriCell;
    }

    public int getApnId() {
        return apnId;
    }

    public void setApnId(int apnId) {
        this.apnId = apnId;
    }

    public String getApnName() {
        return apnName;
    }

    public void setApnName(String apnName) {
        this.apnName = apnName;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public String getSipProxyUri() {
        return sipProxyUri;
    }

    public void setSipProxyUri(String sipProxyUri) {
        this.sipProxyUri = sipProxyUri;
    }

    public String getGeoSipProxyUri() {
        return geoSipProxyUri;
    }

    public void setGeoSipProxyUri(String geoSipProxyUri) {
        this.geoSipProxyUri = geoSipProxyUri;
    }

    public String getGeoRegPrimF5Uri() {
        return geoRegPrimF5Uri;
    }

    public void setGeoRegPrimF5Uri(String geoRegPrimF5Uri) {
        this.geoRegPrimF5Uri = geoRegPrimF5Uri;
    }

    public String getGeoRegGeoF5Uri() {
        return geoRegGeoF5Uri;
    }

    public void setGeoRegGeoF5Uri(String geoRegGeoF5Uri) {
        this.geoRegGeoF5Uri = geoRegGeoF5Uri;
    }

    public String getFdServiceUriCell() {
        return fdServiceUriCell;
    }

    public void setFdServiceUriCell(String fdServiceUriCell) {
        this.fdServiceUriCell = fdServiceUriCell;
    }

    @Override
    public String toString() {
        return "KnAPNProfileConfigDTO{" +
                "apnId='" + apnId + '\'' +
                ", apnName='" + apnName + '\'' +
                ", isDefault='" + isDefault + '\'' +
                ", pttServerId='" + pttServerId + '\'' +
                ", sipProxyUri='" + sipProxyUri + '\'' +
                ", geoSipProxyUri='" + geoSipProxyUri + '\'' +
                ", geoRegPrimF5Uri='" + geoRegPrimF5Uri + '\'' +
                ", geoRegGeoF5Uri='" + geoRegGeoF5Uri + '\'' +
                ", fdServiceUriCell='" + fdServiceUriCell + '\'' +
                '}';
    }
}
