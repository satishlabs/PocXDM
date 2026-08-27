/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

/**
 * Created by schandra on 27-10-2016.
 */
public class KnMicroSvcsClusterInfo {

    private String countryCode;
    private int clusterId;
    private int isConfigured;
    private String ptxBucketUriwifi;
    private String desc;
    private String kodiakMapsWifiUri;
    private String kodiakMapsGeoWifiuri;
    private String googleMapsApiKey;
    private String locDataUriWifi;

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

    public int getIsConfigured() {
        return isConfigured;
    }

    public void setIsConfigured(int isConfigured) {
        this.isConfigured = isConfigured;
    }

    public String getPtxBucketUriwifi() {
        return ptxBucketUriwifi;
    }

    public void setPtxBucketUriwifi(String ptxBucketUriwifi) {
        this.ptxBucketUriwifi = ptxBucketUriwifi;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getKodiakMapsWifiUri() {
        return kodiakMapsWifiUri;
    }

    public void setKodiakMapsWifiUri(String kodiakMapsWifiUri) {
        this.kodiakMapsWifiUri = kodiakMapsWifiUri;
    }

    public String getKodiakMapsGeoWifiuri() {
        return kodiakMapsGeoWifiuri;
    }

    public void setKodiakMapsGeoWifiuri(String kodiakMapsGeoWifiuri) {
        this.kodiakMapsGeoWifiuri = kodiakMapsGeoWifiuri;
    }

    public String getGoogleMapsApiKey() {
        return googleMapsApiKey;
    }

    public void setGoogleMapsApiKey(String googleMapsApiKey) {
        this.googleMapsApiKey = googleMapsApiKey;
    }

    public String getLocDataUriWifi() {
        return locDataUriWifi;
    }

    public void setLocDataUriWifi(String locDataUriWifi) {
        this.locDataUriWifi = locDataUriWifi;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(super.toString());
        strBuffer.append(", countryCode - ").append(countryCode);
        strBuffer.append(", clusterId - ").append(clusterId);
        strBuffer.append(", isConfigured - ").append(isConfigured);
        strBuffer.append(", ptxBucketUriwifi - ").append(ptxBucketUriwifi);
        strBuffer.append(", desc - ").append(desc);
        strBuffer.append(", kodiakMapsWifiUri - ").append(kodiakMapsWifiUri);
        strBuffer.append(", kodiakMapsGeoWifiuri - ").append(kodiakMapsGeoWifiuri);
        strBuffer.append(", googleMapsApiKey - ").append(googleMapsApiKey);
        strBuffer.append(", locDataUriWifi -").append(locDataUriWifi);
        return strBuffer.toString();
    }
}
