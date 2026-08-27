/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.library.activation.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnDeviceInfoDTO extends KnResponseDTO{

    private Integer loginType;
    private String userId;
    private String password;
    private String clientId;
    private String clientSecret;
    private int licenseType;

    private String xui;
    private Long activeFS1;
    private String apn;
    private String xcapRootUriWifi;
    private String sgwRUriC;
    private String sgwRUriW;
    private String cbBI;
    private String sgwAM;
    private String authUriC;
    private String authUriW;
    private String sgwLUriC;
    private String sgwLUriW;
    private String token;
    private Integer ipVC;
    private Integer ipVPrefC;
    private Integer ipVPrefW;
    private Integer clientType;

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getXui() {
        return xui;
    }

    @Override
    public void setXui(String xui) {
        this.xui = xui;
    }

    public Long getActiveFS1() {
        return activeFS1;
    }

    public void setActiveFS1(Long activeFS1) {
        this.activeFS1 = activeFS1;
    }

    public String getApn() {
        return apn;
    }

    public void setApn(String apn) {
        this.apn = apn;
    }

    public String getXcapRootUriWifi() {
        return xcapRootUriWifi;
    }

    public void setXcapRootUriWifi(String xcapRootUriWifi) {
        this.xcapRootUriWifi = xcapRootUriWifi;
    }

    public String getSgwRUriC() {
        return sgwRUriC;
    }

    public void setSgwRUriC(String sgwRUriC) {
        this.sgwRUriC = sgwRUriC;
    }

    public String getSgwRUriW() {
        return sgwRUriW;
    }

    public void setSgwRUriW(String sgwRUriW) {
        this.sgwRUriW = sgwRUriW;
    }

    public String getCbBI() {
        return cbBI;
    }

    public void setCbBI(String cbBI) {
        this.cbBI = cbBI;
    }

    public String getSgwAM() {
        return sgwAM;
    }

    public void setSgwAM(String sgwAM) {
        this.sgwAM = sgwAM;
    }

    public String getAuthUriC() {
        return authUriC;
    }

    public void setAuthUriC(String authUriC) {
        this.authUriC = authUriC;
    }

    public String getAuthUriW() {
        return authUriW;
    }

    public void setAuthUriW(String authUriW) {
        this.authUriW = authUriW;
    }

    public String getSgwLUriC() {
        return sgwLUriC;
    }

    public void setSgwLUriC(String sgwLUriC) {
        this.sgwLUriC = sgwLUriC;
    }

    public String getSgwLUriW() {
        return sgwLUriW;
    }

    public void setSgwLUriW(String sgwLUriW) {
        this.sgwLUriW = sgwLUriW;
    }

    public Integer getIpVC() {
        return ipVC;
    }

    public void setIpVC(Integer ipVC) {
        this.ipVC = ipVC;
    }

    public Integer getIpVPrefC() {
        return ipVPrefC;
    }

    public void setIpVPrefC(Integer ipVPrefC) {
        this.ipVPrefC = ipVPrefC;
    }

    public Integer getIpVPrefW() {
        return ipVPrefW;
    }

    public void setIpVPrefW(Integer ipVPrefW) {
        this.ipVPrefW = ipVPrefW;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public int getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(int licenseType) {
        this.licenseType = licenseType;
    }

    @Override
    public String toString() {
        return "KnDeviceInfoDTO{" +
                "loginType=" + loginType +
                ", userId='" + KnGDPRTemplate.userId(userId) + '\'' +
                ", password='" + password + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", xui='" + xui + '\'' +
                ", activeFS1=" + activeFS1 +
                ", apn='" + apn + '\'' +
                ", xcapRootUriWifi='" + xcapRootUriWifi + '\'' +
                ", sgwRUriC='" + sgwRUriC + '\'' +
                ", sgwRUriW='" + sgwRUriW + '\'' +
                ", cbBI='" + cbBI + '\'' +
                ", sgwAM='" + sgwAM + '\'' +
                ", authUriC='" + authUriC + '\'' +
                ", authUriW='" + authUriW + '\'' +
                ", sgwLUriC='" + sgwLUriC + '\'' +
                ", sgwLUriW='" + sgwLUriW + '\'' +
                ", token='" + token + '\'' +
                ", ipVC=" + ipVC +
                ", ipVPrefC=" + ipVPrefC +
                ", ipVPrefW=" + ipVPrefW +
                ", clientType=" + clientType +
                '}';
    }
}
