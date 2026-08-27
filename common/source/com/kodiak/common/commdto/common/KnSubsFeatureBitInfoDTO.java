/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/*
 *  ***********************************************************************
 *  File name:  KnSubsFeatureBitInfoDTO.java
 *  Subsystem:  PoCXDM
 *
 *    Name                 	    Date         	                  Release
 *    --------------------   -----------------------  -------------------------
 *    Chandrashekar HS          06/02/20, 12:39 PM                    10.0
 *
 *  Copyright (c) 2019 Kodiak, A Motorola Solutions Company
 *  9th floor, MFar, Manayata Tech Park,
 *  Greenheart Phase IV,Nagawara
 *  Bangalore - 560 045
 *  www.motorolasolutions.com
 *  All Rights Reserved.
 *
 * This software is the confidential and proprietary information of KodiakMotorola Solutions, Inc.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of the license agreement you entered into with Kodiak Motorola Solutions.
 *   ***********************************************************************
 */

package com.kodiak.common.commdto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnSubsFeatureBitInfoDTO {
    private String mdn;
    private int corpId;
    private int publicSubscriptionType;
    private int corporateSubscriptionType;
    private int clientType;
    private int qppPackId;
    private long subsFS1;
    private long clientFS1;
    private long activeFS1;
    private long opsFS1;
    private long xdmFS1;
    private long corpAdminFS1;
    private String subsFS2;
    private String clientFS2;
    private String activeFS2;
    private String opsFS2;
    private String corpAdminFS2;
    private String xdmFS2;
    private String userProfileFS2;

    int pamAccId;
    private String tierPkgCode;
    String featureReleaseVersion;

    String oldFeatureReleaseVersion;
    private int userProfileIndex;


    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public int getPublicSubscriptionType() {
        return publicSubscriptionType;
    }

    public void setPublicSubscriptionType(int publicSubscriptionType) {
        this.publicSubscriptionType = publicSubscriptionType;
    }

    public int getCorporateSubscriptionType() {
        return corporateSubscriptionType;
    }

    public void setCorporateSubscriptionType(int corporateSubscriptionType) {
        this.corporateSubscriptionType = corporateSubscriptionType;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getQppPackId() {
        return qppPackId;
    }

    public void setQppPackId(int qppPackId) {
        this.qppPackId = qppPackId;
    }

    public long getSubsFS1() {
        return subsFS1;
    }

    public void setSubsFS1(long subsFS1) {
        this.subsFS1 = subsFS1;
    }

    public long getClientFS1() {
        return clientFS1;
    }

    public void setClientFS1(long clientFS1) {
        this.clientFS1 = clientFS1;
    }

    public long getActiveFS1() {
        return activeFS1;
    }

    public void setActiveFS1(long activeFS1) {
        this.activeFS1 = activeFS1;
    }

    public long getOpsFS1() {
        return opsFS1;
    }

    public void setOpsFS1(long opsFS1) {
        this.opsFS1 = opsFS1;
    }

    public long getCorpAdminFS1() {
        return corpAdminFS1;
    }

    public void setCorpAdminFS1(long corpAdminFS1) {
        this.corpAdminFS1 = corpAdminFS1;
    }

    public String getSubsFS2() {
        return subsFS2;
    }

    public void setSubsFS2(String subsFS2) {
        this.subsFS2 = subsFS2;
    }

    public String getClientFS2() {
        return clientFS2;
    }

    public void setClientFS2(String clientFS2) {
        this.clientFS2 = clientFS2;
    }

    public String getActiveFS2() {
        return activeFS2;
    }

    public void setActiveFS2(String activeFS2) {
        this.activeFS2 = activeFS2;
    }

    public String getOpsFS2() {
        return opsFS2;
    }

    public void setOpsFS2(String opsFS2) {
        this.opsFS2 = opsFS2;
    }

    public String getCorpAdminFS2() {
        return corpAdminFS2;
    }

    public void setCorpAdminFS2(String corpAdminFS2) {
        this.corpAdminFS2 = corpAdminFS2;
    }

    public String getUserProfileFS2() {
        return userProfileFS2;
    }

    public void setUserProfileFS2(String userProfileFS2) {
        this.userProfileFS2 = userProfileFS2;
    }


    public long getXdmFS1() {
        return xdmFS1;
    }

    public void setXdmFS1(long xdmFS1) {
        this.xdmFS1 = xdmFS1;
    }

    public String getXdmFS2() {
        return xdmFS2;
    }

    public void setXdmFS2(String xdmFS2) {
        this.xdmFS2 = xdmFS2;
    }

    public int getPamAccId() {
        return pamAccId;
    }

    public void setPamAccId(int pamAccId) {
        this.pamAccId = pamAccId;
    }

    public String getTierPkgCode() {
        return tierPkgCode;
    }

    public void setTierPkgCode(String tierPkgCode) {
        this.tierPkgCode = tierPkgCode;
    }

    public String getFeatureReleaseVersion() {
        return featureReleaseVersion;
    }

    public void setFeatureReleaseVersion(String featureReleaseVersion) {
        this.featureReleaseVersion = featureReleaseVersion;
    }

    public String getOldFeatureReleaseVersion() {
        return oldFeatureReleaseVersion;
    }

    public void setOldFeatureReleaseVersion(String oldFeatureReleaseVersion) {
        this.oldFeatureReleaseVersion = oldFeatureReleaseVersion;
    }

    public int getUserProfileIndex() { return userProfileIndex; }

    public void setUserProfileIndex(int userProfileIndex) { this.userProfileIndex = userProfileIndex; }

    @Override
    public String toString() {
        return "KnSubsFeatureBitInfoDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", coprId=" + corpId +
                ", publicSubscriptionType=" + publicSubscriptionType +
                ", corporateSubscriptionType=" + corporateSubscriptionType +
                ", clientType=" + clientType +
                ", qppPackId=" + qppPackId +
                ", subsFS1=" + subsFS1 +
                ", clientFS1=" + clientFS1 +
                ", activeFS1=" + activeFS1 +
                ", opsFS1=" + opsFS1 +
                ", xdmFS1='" + xdmFS1 + '\'' +
                ", corpAdminFS1=" + corpAdminFS1 +
                ", subsFS2='" + subsFS2 + '\'' +
                ", clientFS2='" + clientFS2 + '\'' +
                ", activeFS2='" + activeFS2 + '\'' +
                ", opsFS2='" + opsFS2 + '\'' +
                ", corpAdminFS2='" + corpAdminFS2 + '\'' +
                ", xdmFS2='" + xdmFS2 + '\'' +
                ", userProfileFS2='" + userProfileFS2 + '\'' +
                ", pamAccId=" + pamAccId +
                ", tierPkgCode='" + tierPkgCode + '\'' +
                ", featureReleaseVersion=" + featureReleaseVersion +
                ", oldFeatureReleaseVersion=" + oldFeatureReleaseVersion +
                ", userProfileIndex=" + userProfileIndex +
                '}';
    }
}
