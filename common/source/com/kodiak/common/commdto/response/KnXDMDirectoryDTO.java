/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMDirectoryDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 15, 2011           7.0
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
public class KnXDMDirectoryDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676153L;

    private String mdn;
    private int dirEtag;
    private Collection<KnAppInfoDTO> appInfoList;
    private int corpId;
    private String xdmsHome;
    private String protocolVersion;
    private String userAgent;
    private String baseMdn;
    private String userProfileId;
    private String activeFs2;
    private String subsriberFS2;
    private int clientType;

    public String getActiveFs2() {
        return activeFs2;
    }

    public void setActiveFs2(String activeFs2) {
        this.activeFs2 = activeFs2;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
     public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public int getDirEtag() {
        return dirEtag;
    }

    public void setDirEtag(int dirEtag) {
        this.dirEtag = dirEtag;
    }

    public Collection<KnAppInfoDTO> getAppInfoList() {
        return appInfoList;
    }

    public void setAppInfoList(Collection<KnAppInfoDTO> appInfoList) {
        this.appInfoList = appInfoList;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public String getXdmsHome() {
        return xdmsHome;
    }

    public void setXdmsHome(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getBaseMdn() {
        return baseMdn;
    }

    public void setBaseMdn(String baseMdn) {
        this.baseMdn = baseMdn;
    }

    public String getSubsriberFS2() {
        return subsriberFS2;
    }

    public void setSubsriberFS2(String subsriberFS2) {
        this.subsriberFS2 = subsriberFS2;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(100);
        strBuffer.append(super.toString());
        strBuffer.append(" Mdn - ").append(KnGDPRTemplate.mdn(mdn));
        strBuffer.append(", DirEtag - ").append(dirEtag);
        strBuffer.append(", corpId - ").append(corpId);
        strBuffer.append(", protocolVersion - ").append(protocolVersion);
        strBuffer.append(", AppInfoList - ").append(appInfoList);
        strBuffer.append(", userAgent - ").append(userAgent);
        strBuffer.append(", baseMdn - ").append(KnGDPRTemplate.mdn(baseMdn));
        strBuffer.append(", userProfileId - ").append(userProfileId);
        strBuffer.append(", activeFs2 - ").append(activeFs2);

        return strBuffer.toString();
    }

    public String getObjectId() {
        return this.mdn;
    }
}
