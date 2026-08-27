package com.kodiak.xdms.server.corpmgmt.dto.common;

public class KnAllocatePocSubsUpdateDTO {
    private String mdn;
    private Integer clusterId;
    private String pocHome;
    private String presenceHome;
    private Integer serviceAuthStatus;
    private String activeFS2;
    private String hierarchyId;
    private String hierarchyRoot;
    private String hierarchyPath;
    private Long lastProfileUpdateTime;

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

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(Integer serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public String getActiveFS2() {
        return activeFS2;
    }

    public void setActiveFS2(String activeFS2) {
        this.activeFS2 = activeFS2;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getHierarchyRoot() {
        return hierarchyRoot;
    }

    public void setHierarchyRoot(String hierarchyRoot) {
        this.hierarchyRoot = hierarchyRoot;
    }

    public String getHierarchyPath() {
        return hierarchyPath;
    }

    public void setHierarchyPath(String hierarchyPath) {
        this.hierarchyPath = hierarchyPath;
    }

    public Long getLastProfileUpdateTime() {
        return lastProfileUpdateTime;
    }

    public void setLastProfileUpdateTime(Long lastProfileUpdateTime) {
        this.lastProfileUpdateTime = lastProfileUpdateTime;
    }

    @Override
    public String toString() {
        return "KnAllocatePocSubsUpdateDTO{" +
                "mdn='" + mdn + '\'' +
                ", clusterId=" + clusterId +
                ", pocHome='" + pocHome + '\'' +
                ", presenceHome='" + presenceHome + '\'' +
                ", serviceAuthStatus=" + serviceAuthStatus +
                ", activeFS2='" + activeFS2 + '\'' +
                ", hierarchyId='" + hierarchyId + '\'' +
                ", hierarchyRoot='" + hierarchyRoot + '\'' +
                ", hierarchyPath='" + hierarchyPath + '\'' +
                ", lastProfileUpdateTime=" + lastProfileUpdateTime +
                '}';
    }
}
