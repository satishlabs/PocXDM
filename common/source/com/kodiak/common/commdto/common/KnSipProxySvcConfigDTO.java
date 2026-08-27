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
 * File name:  KnSipProxySvcConfigDTO.java
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

public class KnSipProxySvcConfigDTO implements Serializable {

    private static final long serialVersionUID = 7526471155622673645L;

    private String pttServerId;
    private String sipProxyURI;
    private String sipProxyUriWifi;
    private String geoSipProxyUri;
    private String geoSipProxyUriWifi;

    public KnSipProxySvcConfigDTO() {}

    public KnSipProxySvcConfigDTO(String pttServerId, String sipProxyURI, String sipProxyUriWifi, String geoSipProxyUri,
                                  String geoSipProxyUriWifi) {
        this.pttServerId = pttServerId;
        this.sipProxyURI = sipProxyURI;
        this.sipProxyUriWifi = sipProxyUriWifi;
        this.geoSipProxyUri = geoSipProxyUri;
        this.geoSipProxyUriWifi = geoSipProxyUriWifi;
    }

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public String getSipProxyURI() {
        return sipProxyURI;
    }

    public void setSipProxyURI(String sipProxyURI) {
        this.sipProxyURI = sipProxyURI;
    }

    public String getGeoSipProxyUri() {
        return geoSipProxyUri;
    }

    public void setGeoSipProxyUri(String geoSipProxyUri) {
        this.geoSipProxyUri = geoSipProxyUri;
    }

    public String getSipProxyUriWifi() {
        return sipProxyUriWifi;
    }

    public void setSipProxyUriWifi(String sipProxyUriWifi) {
        this.sipProxyUriWifi = sipProxyUriWifi;
    }

    public String getGeoSipProxyUriWifi() {
        return geoSipProxyUriWifi;
    }

    public void setGeoSipProxyUriWifi(String geoSipProxyUriWifi) {
        this.geoSipProxyUriWifi = geoSipProxyUriWifi;
    }

    @Override
    public String toString() {
        return "KnSipProxySvcConfigDTO{" +
                "pttServerId='" + pttServerId + '\'' +
                ", sipProxyURI='" + sipProxyURI + '\'' +
                ", geoSipProxyUri='" + geoSipProxyUri + '\'' +
                ", sipProxyUriWifi='" + sipProxyUriWifi + '\'' +
                ", geoSipProxyUriWifi='" + geoSipProxyUriWifi + '\'' +
                '}';
    }
}
