package com.kodiak.xdms.server.corpmgmt.dto.common;

public class KnDeploySiteInfoDTO {
    private Integer clusterId;
    private String deploymentType;
    private String clusterName;
    private String clusterFqdn;
    private String desc;
    private String countryCode;
    private String isRedundant;
    private Integer redundantClsId;
    private Byte clusterType;
    private String geoCode;

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public String getDeploymentType() {
        return deploymentType;
    }

    public void setDeploymentType(String deploymentType) {
        this.deploymentType = deploymentType;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterFqdn() {
        return clusterFqdn;
    }

    public void setClusterFqdn(String clusterFqdn) {
        this.clusterFqdn = clusterFqdn;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIsRedundant() {
        return isRedundant;
    }

    public void setIsRedundant(String isRedundant) {
        this.isRedundant = isRedundant;
    }

    public Integer getRedundantClsId() {
        return redundantClsId;
    }

    public void setRedundantClsId(Integer redundantClsId) {
        this.redundantClsId = redundantClsId;
    }

    public Byte getClusterType() {
        return clusterType;
    }

    public void setClusterType(Byte clusterType) {
        this.clusterType = clusterType;
    }

    public String getGeoCode() {
        return geoCode;
    }

    public void setGeoCode(String geoCode) {
        this.geoCode = geoCode;
    }

    @Override
    public String toString() {
        return "KnDeploySiteInfoDTO{" +
                "clusterId=" + clusterId +
                ", deploymentType='" + deploymentType + '\'' +
                ", clusterName='" + clusterName + '\'' +
                ", clusterFqdn='" + clusterFqdn + '\'' +
                ", desc='" + desc + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", isRedundant='" + isRedundant + '\'' +
                ", redundantClsId=" + redundantClsId +
                ", clusterType=" + clusterType +
                ", geoCode='" + geoCode + '\'' +
                '}';
    }
}
