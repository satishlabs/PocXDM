/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dto;

public class KnAsyncJobTaskDTO {

    private String taskId;
    private int corpId;
    private int operationType;
    private String taskType;
    private String userProfileId;
    private int seq;
    private int priority;
    private String startTimeStamp;
    private String endTimeStamp;
    private int status;
    private String txnId;
    private String payLoad;
    private String resourceEntity;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getCorpId() { return corpId; }

    public void setCorpId(int corpId) { this.corpId = corpId; }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(String startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public String getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(String endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }

    public String getResourceEntity() {
        return resourceEntity;
    }

    public void setResourceEntity(String resourceEntity) {
        this.resourceEntity = resourceEntity;
    }

    public String getTaskType() { return taskType; }

    public void setTaskType(String taskType) { this.taskType = taskType; }

    @Override
    public String toString() {
        return "KnAsyncJobTaskDTO{" +
                "taskId='" + taskId + '\'' +
                ", corpId=" + corpId +
                ", operationType=" + operationType +
                ", taskType='" + taskType + '\'' +
                ", userProfileId='" + userProfileId + '\'' +
                ", seq=" + seq +
                ", priority=" + priority +
                ", startTimeStamp='" + startTimeStamp + '\'' +
                ", endTimeStamp='" + endTimeStamp + '\'' +
                ", status=" + status +
                ", txnId='" + txnId + '\'' +
                ", payLoad='" + payLoad + '\'' +
                ", resourceEntity='" + resourceEntity + '\'' +
                '}';
    }
}
