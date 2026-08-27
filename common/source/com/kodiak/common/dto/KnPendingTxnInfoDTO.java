/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */

package com.kodiak.common.dto;

import java.util.List;

public class KnPendingTxnInfoDTO {

    private Integer recId;

    private long insertionTime;

    private Integer Status;

    private String txnId;

    private Integer Priority;

    private Integer entityType;

    private String entityId;

    private Integer corpId;

    private Integer opsId;

    private Long taskBitSet;

    private Long taskStatusBitSet;

    private Integer notifyFlag;

    private String processInfo;

    private String payload;

    private Integer currentTask;

    private Integer retryCount;

    private int param1;

    private List<String> entityIds;

    public Integer getRecId() {
        return recId;
    }

    public void setRecId(Integer recId) {
        this.recId = recId;
    }

    public Long getInsertionTime() {
        return insertionTime;
    }

    public void setInsertionTime(Long insertionTime) {
        this.insertionTime = insertionTime;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public Integer getPriority() {
        return Priority;
    }

    public void setPriority(Integer priority) {
        Priority = priority;
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getOpsId() {
        return opsId;
    }

    public void setOpsId(Integer opsId) {
        this.opsId = opsId;
    }

    public Long getTaskBitSet() {
        return taskBitSet;
    }

    public void setTaskBitSet(Long taskBitSet) {
        this.taskBitSet = taskBitSet;
    }

    public Integer getNotifyFlag() {
        return notifyFlag;
    }

    public void setNotifyFlag(Integer notifyFlag) {
        this.notifyFlag = notifyFlag;
    }

    public String getProcessInfo() {
        return processInfo;
    }

    public void setProcessInfo(String processInfo) {
        this.processInfo = processInfo;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }


    public Integer getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(Integer currentTask) {
        this.currentTask = currentTask;
    }

    public Long getTaskStatusBitSet() {
        return taskStatusBitSet;
    }

    public void setTaskStatusBitSet(Long taskStatusBitSet) {
        this.taskStatusBitSet = taskStatusBitSet;
    }

    public void setInsertionTime(long insertionTime) { this.insertionTime = insertionTime; }

    public Integer getRetryCount() { return retryCount; }

    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public int getParam1() { return param1;}

    public void setParam1(int param1) { this.param1 = param1; }

    public List<String> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(List<String> entityIds) {
        this.entityIds = entityIds;
    }

    @Override
    public String toString() {
        return "KnPendingTxnInfoDTO{" +
                "recId=" + recId +
                ", insertionTime=" + insertionTime +
                ", Status=" + Status +
                ", txnId='" + txnId + '\'' +
                ", Priority=" + Priority +
                ", entityType=" + entityType +
                ", entityId='" + entityId + '\'' +
                ", corpId=" + corpId +
                ", opsId=" + opsId +
                ", taskBitSet=" + taskBitSet +
                ", taskStatusBitSet=" + taskStatusBitSet +
                ", notifyFlag=" + notifyFlag +
                ", processInfo='" + processInfo + '\'' +
                ", payload='" + payload + '\'' +
                ", currentTask=" + currentTask +
                ", retryCount=" + retryCount +
                ", param1=" + param1 +
                ", entityIds=" + entityIds +
                '}';
    }
}


