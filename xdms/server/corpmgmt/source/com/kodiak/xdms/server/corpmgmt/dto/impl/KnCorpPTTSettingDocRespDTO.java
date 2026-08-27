package com.kodiak.xdms.server.corpmgmt.dto.impl;

public class KnCorpPTTSettingDocRespDTO extends KnCorpResponseDTO{

    private int corpId;
    private String pttSettingId;
    private String hierarchyId;
    private int isDefault;
    private long createdTime;
    private long updatedTime;


    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public String getPttSettingId() {
        return pttSettingId;
    }

    public void setPttSettingId(String pttSettingId) {
        this.pttSettingId = pttSettingId;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return "KnCorpPTTSettingDocRespDTO{" +
                "corpId=" + corpId +
                ", pttSettingId='" + pttSettingId + '\'' +
                ", hierarchyId='" + hierarchyId + '\'' +
                ", isDefault=" + isDefault +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
