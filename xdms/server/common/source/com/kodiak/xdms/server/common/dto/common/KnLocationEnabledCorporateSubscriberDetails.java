package com.kodiak.xdms.server.common.dto.common;

public class KnLocationEnabledCorporateSubscriberDetails {

    private String mdn;
    private String xdmFS2;
    private String clientFS2;
    private String corpFS2;
    private String opsFS2;
    private String corpAdminFS2;
    private String userProfileFS2;
    private String activeFS2;
    private String subscriberFS2;
    private int protocolVersion;
    private int corpId;
    private Long lastUpdateTime;
    private String pocHome;
    private String presenceHome;
    private String xdmsHome;

    public String getXdmFS2() {
        return xdmFS2;
    }

    public void setXdmFS2(String xdmFS2) {
        this.xdmFS2 = xdmFS2;
    }

    public String getClientFS2() {
        return clientFS2;
    }

    public void setClientFS2(String clientFS2) {
        this.clientFS2 = clientFS2;
    }

    public String getCorpFS2() {
        return corpFS2;
    }

    public void setCorpFS2(String corpFS2) {
        this.corpFS2 = corpFS2;
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

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getActiveFS2() {
        return activeFS2;
    }

    public void setActiveFS2(String activeFS2) {
        this.activeFS2 = activeFS2;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getSubscriberFS2() {
        return subscriberFS2;
    }

    public void setSubscriberFS2(String subscriberFS2) {
        this.subscriberFS2 = subscriberFS2;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
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
        this.presenceHome = presenceHome;
    }

    public String getXdmsHome() {
        return xdmsHome;
    }

    public void setXdmsHome(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    @Override
    public String toString() {
        return "KnLocationEnabledSubsDetails{" +
                "xdmFS2='" + xdmFS2 + '\'' +
                ", clientFS2='" + clientFS2 + '\'' +
                ", corpFS2='" + corpFS2 + '\'' +
                ", opsFS2='" + opsFS2 + '\'' +
                ", corpAdminFS2='" + corpAdminFS2 + '\'' +
                ", userProfileFS2='" + userProfileFS2 + '\'' +
                ", protocolVersion=" + protocolVersion + '\'' +
                ", activeFS2=" + activeFS2 +
                ", corpId=" + corpId +
                ", lastUpdateTime=" + lastUpdateTime +
                ", subscriberFS2='" + subscriberFS2 + '\'' +
                ", mdn='" + mdn + '\'' +
                ", pocHome='" + pocHome + '\'' +
                ", presenceHome='" + presenceHome + '\'' +
                ", xdmsHome='" + xdmsHome + '\'' +
                '}';
    }
}
