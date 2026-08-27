/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * Created by abhishek on 25/10/16.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class KnActiEXDMSNotifyDto extends KnEXDMSNotifyDto {
    private String mdn;
    private Integer corpid;
    private Long activeFS;
    private Integer authStatus;
    private String pv;
    private String oldPV;
    private Integer clientType;
    private Integer dispatchType;
    private String activeFS2;
    private Long lastProfileUpdateTime;
    private Integer syncGwProfileCreated;
    private String oldActiveFS;
    private String subsFS2;
    private String mcId;
    private String mcdataId;
    private String mcpttId;
    private String mcvideoId;
    private String networkName;
    private String deviceId;

    public String getPv() {
        return pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }

    public String getOldPV() {
        return oldPV;
    }

    public void setOldPV(String oldPV) {
        this.oldPV = oldPV;
    }

    public Integer getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(Integer authStatus) {
        this.authStatus = authStatus;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public Long getActiveFS() {
        return activeFS;
    }

    public void setActiveFS(Long activeFS) {
        this.activeFS = activeFS;
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

    public Integer getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(Integer dispatchType) {
        this.dispatchType = dispatchType;
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

    public Integer getSyncGwProfileCreated() {
        return syncGwProfileCreated;
    }

    public void setSyncGwProfileCreated(Integer syncGwProfileCreated) {
        this.syncGwProfileCreated = syncGwProfileCreated;
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

    public String getMcpttId() {
        return mcpttId;
    }

    public void setMcpttId(String mcpttId) {
        this.mcpttId = mcpttId;
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

    @Override
    public String toString() {
        return "KnActiEXDMSNotifyDto{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", corpid=" + corpid +
                ", activeFS=" + activeFS +
                ", pv=" + pv +
                ", oldPV=" + oldPV +
                ", authStatus=" + authStatus +
                ", clientType=" + clientType +
                ", dispatchType=" + dispatchType +
                ", activeFS2=" + activeFS2 +
                ", lastProfileUpdateTime=" + lastProfileUpdateTime +
                ", oldActiveFS=" + oldActiveFS +
                ", subsFS2=" + subsFS2 +
                ", mcId=" + KnGDPRTemplate.mcId(mcId) +
                ", mcdataId=" + KnGDPRTemplate.mcdataId(mcdataId) +
                ", mcpttId=" + KnGDPRTemplate.mcpttId(mcpttId) +
                ", mcvideoId=" + KnGDPRTemplate.mcvideoId(mcvideoId) +
                ", networkName=" + networkName +
                ", deviceId=" + deviceId +
                '}';
    }
}
