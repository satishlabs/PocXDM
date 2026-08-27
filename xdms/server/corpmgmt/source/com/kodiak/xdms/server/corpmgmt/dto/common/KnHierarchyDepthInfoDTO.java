package com.kodiak.xdms.server.corpmgmt.dto.common;

public class KnHierarchyDepthInfoDTO {

    private String corpId;
    private String ancestorHierId;
    private String descendantHierId;
    private Integer depth;
    private Long creationTime;
    private Long updateTime;
    private String transactionId;
    private String customField1;
    private String customField2;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getAncestorHierId() {
        return ancestorHierId;
    }

    public void setAncestorHierId(String ancestorHierId) {
        this.ancestorHierId = ancestorHierId;
    }

    public String getDescendantHierId() {
        return descendantHierId;
    }

    public void setDescendantHierId(String descendantHierId) {
        this.descendantHierId = descendantHierId;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "KnHierarchyDepthInfoDTO{" +
                "corpId='" + corpId + '\'' +
                ", ancestorHierId='" + ancestorHierId + '\'' +
                ", descendantHierId='" + descendantHierId + '\'' +
                ", depth=" + depth +
                ", creationTime=" + creationTime +
                ", updateTime=" + updateTime +
                ", customField1='" + customField1 + '\'' +
                ", customField2='" + customField2 + '\'' +
                    ", transactionId='" + transactionId + '\'' +
                '}';
    }

}
