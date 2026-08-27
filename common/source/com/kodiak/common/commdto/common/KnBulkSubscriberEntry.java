package com.kodiak.common.commdto.common;

import com.kodiak.common.resources.KnConstants;

import java.io.Serializable;

public class KnBulkSubscriberEntry implements Serializable {
    private static final long serialVersionUID = 7526471155622676302L;

    private String mdn;              // Mobile Directory Number (UNIQUE)
    private String networkName;      // Subscriber name/display name
    private String pocHome;         // POC Home Server
    private String presenceHome;    // Presence Home Server
    private String activeFS2;       // Active Feature Set 2 (specific to this subscriber's POC/Presence home)
    private String mcpttId;
    private String mcVideoId;
    private String mcDataId;
    private String mcId;
    private String aliasMdn;
    private String userId;
    private String email;
    private int serviceAuthStatus;
    private int prevServiceAuthStatus;
    private int dispatchType;
    private int subsClientType;


    public String getMdn() {
        return mdn;
    }
    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getNetworkName() {
        return networkName;
    }
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getPocHome() {
        return pocHome;
    }
    public void setPocHome(String pocHome) {
        this.pocHome = pocHome;
    }

    public String getPresenceHome() {
        return presenceHome;
    }
    public void setPresenceHome(String presenceHome) {
        if (presenceHome != null) {
            presenceHome = presenceHome.trim();
            if (presenceHome.isEmpty()) {
                presenceHome = null;
            }
        }
        this.presenceHome = presenceHome;
    }

    public String getActiveFS2() {
        return activeFS2;
    }
    public void setActiveFS2(String activeFS2) {
        this.activeFS2 = activeFS2;
    }

    public String getMcpttId() {
        return mcpttId;
    }
    public void setMcpttId(String mcpttId) {
        this.mcpttId = mcpttId;
    }

    public String getMcVideoId() {
        return mcVideoId;
    }
    public void setMcVideoId(String mcVideoId) {
        this.mcVideoId = mcVideoId;
    }

    public String getMcDataId() {
        return mcDataId;
    }
    public void setMcDataId(String mcDataId) {
        this.mcDataId = mcDataId;
    }

    public String getMcId() {
        return mcId;
    }
    public void setMcId(String mcId) {
        this.mcId = mcId;
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

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }


    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public int getPrevServiceAuthStatus() {
        return prevServiceAuthStatus;
    }

    public void setPrevServiceAuthStatus(int prevServiceAuthStatus) {
        this.prevServiceAuthStatus = prevServiceAuthStatus;
    }

    public int getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(int dispatchType) {
        this.dispatchType = dispatchType;
    }

    public int getSubsClientType() {
        return subsClientType;
    }

    public void setSubsClientType(int subsClientType) {
        this.subsClientType = subsClientType;
    }

    @Override
    public String toString() {
        return "KnBulkSubscriberEntry{" +
                "mcpttId='" + mcpttId + '\'' +
                ", mdn='" + mdn + '\'' +
                ", networkName='" + networkName + '\'' +
                ", pocHome='" + pocHome + '\'' +
                ", presenceHome='" + presenceHome + '\'' +
                ", activeFS2='" + activeFS2 + '\'' +
                ", mcVideoId='" + mcVideoId + '\'' +
                ", mcDataId='" + mcDataId + '\'' +
                ", mcId='" + mcId + '\'' +
                ", aliasMdn='" + aliasMdn + '\'' +
                ", userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", serviceAuthStatus=" + serviceAuthStatus +
                ", prevServiceAuthStatus=" + prevServiceAuthStatus +
                ", dispatchType=" + dispatchType +
                ", subsClientType=" + subsClientType +
                '}';
    }
}
