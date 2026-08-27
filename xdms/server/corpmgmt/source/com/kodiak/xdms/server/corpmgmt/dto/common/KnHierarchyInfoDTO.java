package com.kodiak.xdms.server.corpmgmt.dto.common;

public class KnHierarchyInfoDTO {

    private String corpId;
    private String hierarchyId;
    private String externalHierarchyId;
    private String hierarchyName;
    private String hierarchyAlias;
    private Byte hierarchyType;
    private String segmentIndicator;
    private Byte subScrDefPttRadio;
    private Integer featureVersion;
    private Long creationTime;
    private Long updateTime;
    private Byte status;
    private String customField1;
    private String customField2;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getExternalHierarchyId() {
        return externalHierarchyId;
    }

    public void setExternalHierarchyId(String externalHierarchyId) {
        this.externalHierarchyId = externalHierarchyId;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public String getHierarchyAlias() {
        return hierarchyAlias;
    }

    public void setHierarchyAlias(String hierarchyAlias) {
        this.hierarchyAlias = hierarchyAlias;
    }

    public Byte getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(Byte hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public String getSegmentIndicator() {
        return segmentIndicator;
    }

    public void setSegmentIndicator(String segmentIndicator) {
        this.segmentIndicator = segmentIndicator;
    }

    public Byte getSubScrDefPttRadio() {
        return subScrDefPttRadio;
    }

    public void setSubScrDefPttRadio(Byte subScrDefPttRadio) {
        this.subScrDefPttRadio = subScrDefPttRadio;
    }

    public Integer getFeatureVersion() {
        return featureVersion;
    }

    public void setFeatureVersion(Integer featureVersion) {
        this.featureVersion = featureVersion;
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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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
        return "KnHierarchyInfoDTO{" +
                "corpId='" + corpId + '\'' +
                ", hierarchyId='" + hierarchyId + '\'' +
                ", externalHierarchyId='" + externalHierarchyId + '\'' +
                ", hierarchyName='" + hierarchyName + '\'' +
                ", hierarchyAlias='" + hierarchyAlias + '\'' +
                ", hierarchyType=" + hierarchyType +
                ", segmentIndicator='" + segmentIndicator + '\'' +
                ", subScrDefPttRadio=" + subScrDefPttRadio +
                ", featureVersion=" + featureVersion +
                ", creationTime=" + creationTime +
                ", updateTime=" + updateTime +
                ", status=" + status +
                ", customField1='" + customField1 + '\'' +
                ", customField2='" + customField2 + '\'' +
                '}';
    }

}
