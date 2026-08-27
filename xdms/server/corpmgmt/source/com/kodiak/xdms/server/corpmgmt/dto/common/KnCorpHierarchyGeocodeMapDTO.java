package com.kodiak.xdms.server.corpmgmt.dto.common;

public class KnCorpHierarchyGeocodeMapDTO {

    private Integer corpId;
    private String hierarchyId;
    private String geocode;
    private Long creationTime;
    private Long updateTime;
    private String customField1;
    private String customField2;

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getGeocode() {
        return geocode;
    }

    public void setGeocode(String geocode) {
        this.geocode = geocode;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getCustomField1() {
        return customField1;
    }

    public void setCustomField1(String customField1) {
        this.customField1 = customField1;
    }

    public String getCustomField2() {
        return customField2;
    }

    public void setCustomField2(String customField2) {
        this.customField2 = customField2;
    }

    @Override
    public String toString() {
        return "CorpHierarchyGeocodeMapDTO{" +
                "corpId=" + corpId +
                ", hierarchyId='" + hierarchyId + '\'' +
                ", geocode='" + geocode + '\'' +
                ", creationTime=" + creationTime +
                ", updateTime=" + updateTime +
                ", customField1='" + customField1 + '\'' +
                ", customField2='" + customField2 + '\'' +
                '}';
    }
}
