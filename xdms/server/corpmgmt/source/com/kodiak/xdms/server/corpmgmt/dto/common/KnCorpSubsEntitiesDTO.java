/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * ************************************************************************
 * <p>
 * File name:  KnCorpSubsEntitiesDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             March 08, 2018                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnCorpSubsEntitiesDTO {

    private String MDN;

    private String aliasMdn;

    private String userId;

    private int corpId;

    private int profileId;

    private String activeFs2;

    private int clientType;

    private String networkName;

    private Integer cameraType;

    private int mcpttCompliance;

    private String activeFs1;
    private int clientPVmajorVer;

    public String getMDN() {
        return MDN;
    }

    public void setMDN(String MDN) {
        this.MDN = MDN;
    }

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getActiveFs2() {
		return activeFs2;
	}

	public void setActiveFs2(String activeFs2) {
		this.activeFs2 = activeFs2;
	}

	public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public int getMcpttCompliance() { return mcpttCompliance; }

    public void setMcpttCompliance(int mcpttCompliance) { this.mcpttCompliance = mcpttCompliance; }

    public Integer getCameraType() {
        return cameraType;
    }

    public void setCameraType(Integer cameraType) {
        this.cameraType = cameraType;
    }

    public String getActiveFs1() {
        return activeFs1;
    }

    public void setActiveFs1(String activeFs1) {
        this.activeFs1 = activeFs1;
    }

    public int getClientPVmajorVer() {
        return clientPVmajorVer;
    }

    public void setClientPVmajorVer(int clientPVmajorVer) {
        this.clientPVmajorVer = clientPVmajorVer;
    }

    @Override
    public String toString() {
        return "KnCorpSubsEntitiesDTO{" +
                "MDN='" + KnGDPRTemplate.mdn(MDN) + '\'' +
                ", aliasMdn='" + KnGDPRTemplate.name(aliasMdn) + '\'' +
                ", userId='" + userId + '\'' +
                ", corpId=" + corpId +
                ", profileId=" + profileId +
                ", activeFs2=" + activeFs2 +
                ", clientType=" + clientType +
                ", networkName='" + KnGDPRTemplate.name(networkName )+ '\'' +
                ", mcpttCompliance='" + mcpttCompliance + '\''+
                ", cameraType='" + cameraType + '\''+
                ", activeFs1='" + activeFs1 + '\''+
                ", clientPVmajorVer='" + clientPVmajorVer + '\''+
                '}';
    }
}
