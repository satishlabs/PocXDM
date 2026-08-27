/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSNotifyDto;

import java.util.List;

/**
 * Created by abhishek on 25/10/16.
 */


@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class KnSubscrEXDMSNotifyDto extends KnEXDMSNotifyDto {
    private String mdn;
    private Integer corpid;
    private Integer authStatus;
    private String newMdn;
    private Integer clientType;
    private long activeFS;
    private String pv;
    private String activeFS2;
    private Long lastProfileUpdateTime;
    private String baseMdn;
    private String pocPttId;
    private String mcpttId;
    private String oldActiveFS;
    private String subsFS2;
    private String mcId;
    private String mcdataId;
    private String mcvideoId;
    private String networkName;
    private String deviceId;
    private String oldsubsFS2;
    private List<String> mdnList;
    private boolean removeExtContact;

    public boolean getRemoveExtContact() {
        return removeExtContact;
    }

    public void setRemoveExtContact(boolean removeExtContact) {
        this.removeExtContact = removeExtContact;
    }

    public String getPv() {
        return pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public Integer getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(Integer authStatus) {
        this.authStatus = authStatus;
    }

    public String getNewMdn() {
        return newMdn;
    }

    public void setNewMdn(String newMdn) {
        this.newMdn = newMdn;
    }

    public Integer getCorpid() {
        return corpid;
    }

    public void setCorpid(Integer corpid) {
        this.corpid = corpid;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public long getActiveFS() {
        return activeFS;
    }

    public void setActiveFS(long activeFS) {
        this.activeFS = activeFS;
    }

    public String getActiveFS2() {
		return activeFS2;
	}

	public void setActiveFS2(String activeFS2) {
		this.activeFS2 = activeFS2;
	}

    public Long getLastProfileUpdateTime() {
        return lastProfileUpdateTime;
    }

    public void setLastProfileUpdateTime(Long lastProfileUpdateTime) {
        this.lastProfileUpdateTime = lastProfileUpdateTime;
    }

    public String getBaseMdn() {
        return baseMdn;
    }

    public void setBaseMdn(String baseMdn) {
        this.baseMdn = baseMdn;
    }

    public String getPocPttId() {
        return pocPttId;
    }

    public void setPocPttId(String pocPttId) {
        this.pocPttId = pocPttId;
    }

    public String getMcpttId() {
        return mcpttId;
    }

    public void setMcpttId(String mcpttId) {
        this.mcpttId = mcpttId;
    }

    public String getOldActiveFS() {
        return oldActiveFS;
    }

    public void setOldActiveFS(String oldActiveFS) {
        this.oldActiveFS = oldActiveFS;
    }

    public String getSubsFS2() {
        return subsFS2;
    }

    public void setSubsFS2(String subsFS2) {
        this.subsFS2 = subsFS2;
    }

    public String getMcId() {
        return mcId;
    }

    public void setMcId(String mcId) {
        this.mcId = mcId;
    }

    public String getMcdataId() {
        return mcdataId;
    }

    public void setMcdataId(String mcdataId) {
        this.mcdataId = mcdataId;
    }

    public String getMcvideoId() {
        return mcvideoId;
    }

    public void setMcvideoId(String mcvideoId) {
        this.mcvideoId = mcvideoId;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOldsubsFS2() {
        return oldsubsFS2;
    }

    public void setOldsubsFS2(String oldsubsFS2) {
        this.oldsubsFS2 = oldsubsFS2;
    }

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    @Override
    public String toString() {
        return "KnSubscrEXDMSNotifyDto{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", corpid=" + corpid +
                ", authStatus=" + authStatus +
                ", newMdn='" + KnGDPRTemplate.mdn(newMdn) + '\'' +
                ", clientType=" + clientType +
                ", activeFS=" + activeFS +
                ", pv='" + pv + '\'' +
                ", activeFS2='" + activeFS2 + '\'' +
                ", lastProfileUpdateTime=" + lastProfileUpdateTime +
                ", baseMdn='" + KnGDPRTemplate.mdn(baseMdn) + '\'' +
                ", pocPttId='" + pocPttId + '\'' +
                ", oldActiveFS='" + oldActiveFS + '\'' +
                ", mcpttId='" + KnGDPRTemplate.mcpttId(mcpttId) + '\'' +
                ", subsFS2='" + subsFS2 + '\'' +
                ", mcId='" + KnGDPRTemplate.mcId(mcId) + '\'' +
                ", mcdataId='" + KnGDPRTemplate.mcdataId(mcdataId) + '\'' +
                ", mcvideoId='" + KnGDPRTemplate.mcvideoId(mcvideoId) + '\'' +
                ", networkName='" + networkName + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", oldsubsFS2='" + oldsubsFS2 + '\'' +
                ", removeExtContact='" + removeExtContact + '\'' +
                '}';
    }
}
