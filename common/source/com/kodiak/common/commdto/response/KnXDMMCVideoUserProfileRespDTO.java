/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;


import com.kodiak.common.commdto.common.KnXDMMCSGroupInfoDTO;
import com.kodiak.common.commdto.common.KnXDMMemberDTO;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMMCVideoUserProfileRespDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Shashank Tewari            July 29, 2019                9.1.1
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

public class KnXDMMCVideoUserProfileRespDTO extends KnXDMRespDTO {
    private static final long serialVersionUID = -1540924884058819709L;
    private String xuiURI;
    private String name;
    private String status;
    private String profileName;
    private String preSelectedIndication;
    private KnXDMMemberDTO cmUserAlias;
    private KnXDMMemberDTO cmVideoUserID;
    private String mcoName;
    private Collection<KnXDMMemberDTO> cmNotList;
    private String cmRecPriority;
    private Collection<KnXDMMemberDTO> cmSharedNotList;
    private String onMaxaffiliationsN2;
    private Collection<KnXDMMCSGroupInfoDTO> mcsgrpInfo;
    private String maxSimultaneousVideoStreams;
    private Integer maxTimeSingleTransmit;
    private String activeFS;
    private String oldActiveFS;
    private int corpId;
    private Integer userProfileIndex;

    public String getXuiURI() {
        return xuiURI;
    }

    public void setXuiURI(String xuiURI) {
        this.xuiURI = xuiURI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getPreSelectedIndication() {
        return preSelectedIndication;
    }

    public void setPreSelectedIndication(String preSelectedIndication) {
        this.preSelectedIndication = preSelectedIndication;
    }

    public KnXDMMemberDTO getCmUserAlias() {
        return cmUserAlias;
    }

    public void setCmUserAlias(KnXDMMemberDTO cmUserAlias) {
        this.cmUserAlias = cmUserAlias;
    }

    public KnXDMMemberDTO getCmVideoUserID() {
        return cmVideoUserID;
    }

    public void setCmVideoUserID(KnXDMMemberDTO cmVideoUserID) {
        this.cmVideoUserID = cmVideoUserID;
    }

    public String getMcoName() {
        return mcoName;
    }

    public void setMcoName(String mcoName) {
        this.mcoName = mcoName;
    }

    public Collection<KnXDMMemberDTO> getCmNotList() {
        return cmNotList;
    }

    public void setCmNotList(Collection<KnXDMMemberDTO> cmNotList) {
        this.cmNotList = cmNotList;
    }

    public String getCmRecPriority() {
        return cmRecPriority;
    }

    public void setCmRecPriority(String cmRecPriority) {
        this.cmRecPriority = cmRecPriority;
    }

    public Collection<KnXDMMemberDTO> getCmSharedNotList() {
        return cmSharedNotList;
    }

    public void setCmSharedNotList(Collection<KnXDMMemberDTO> cmSharedNotList) {
        this.cmSharedNotList = cmSharedNotList;
    }

    public String getOnMaxaffiliationsN2() {
        return onMaxaffiliationsN2;
    }

    public void setOnMaxaffiliationsN2(String onMaxaffiliationsN2) {
        this.onMaxaffiliationsN2 = onMaxaffiliationsN2;
    }

    public Collection<KnXDMMCSGroupInfoDTO> getMcsgrpInfo() {return mcsgrpInfo; }

    public void setMcsgrpInfo(Collection<KnXDMMCSGroupInfoDTO> mcsgrpInfo) {
        this.mcsgrpInfo = mcsgrpInfo;
    }

    public String getMaxSimultaneousVideoStreams() {
        return maxSimultaneousVideoStreams;
    }

    public void setMaxSimultaneousVideoStreams(String maxSimultaneousVideoStreams) {
        this.maxSimultaneousVideoStreams = maxSimultaneousVideoStreams;
    }

    public Integer getMaxTimeSingleTransmit() {return maxTimeSingleTransmit; }

    public void setMaxTimeSingleTransmit(Integer maxTimeSingleTransmit) {
        this.maxTimeSingleTransmit = maxTimeSingleTransmit;
    }

    public String getActiveFS() {
        return activeFS;
    }

    public void setActiveFS(String activeFS) {
        this.activeFS = activeFS;
    }

    public String getOldActiveFS() {
        return oldActiveFS;
    }

    public void setOldActiveFS(String oldActiveFS) {
        this.oldActiveFS = oldActiveFS;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public Integer getUserProfileIndex() { return userProfileIndex; }

    public void setUserProfileIndex(Integer userProfileIndex) { this.userProfileIndex = userProfileIndex; }

    @Override
    public String toString() {
        return "KnXDMMCVideoUserProfileRespDTO{" +
                "xuiURI='" + xuiURI + '\'' +
                ", name='" + KnGDPRTemplate.name(name) + '\'' +
                ", status='" + status + '\'' +
                ", profileName='" + profileName + '\'' +
                ", activeFS='" + activeFS + '\'' +
                ", preSelectedIndication='" + preSelectedIndication + '\'' +
                ", cmUserAlias=" + cmUserAlias +
                ", cmVideoUserID=" + cmVideoUserID +
                ", mcoName='" + mcoName + '\'' +
                ", cmNotList=" + cmNotList +
                ", cmRecPriority='" + cmRecPriority + '\'' +
                ", cmSharedNotList=" + cmSharedNotList +
                ", onMaxaffiliationsN2='" + onMaxaffiliationsN2 + '\'' +
                ", mcsgrpInfo=" + mcsgrpInfo +
                ", maxSimultaneousVideoStreams='" + maxSimultaneousVideoStreams + '\'' +
                ", maxTimeSingleTransmit='" + maxTimeSingleTransmit + '\'' +
                ", oldActiveFS='" + oldActiveFS + '\'' +
                ", corpId='" + corpId + '\'' +
                ", userProfileIndex='" + userProfileIndex + '\'' +
                '}';
    }
}
