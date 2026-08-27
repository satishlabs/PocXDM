/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */

package com.kodiak.common.dto;
public class KnAsyncNotifyDTO {

    private Integer recId;

    private long insertionTime;

    private Integer Status;

    private String txnId;

    private Integer Priority;

    private Integer entityType;

    private String entityId;

    private Integer corpId;

    private Integer opsId;

    private long taskBitSet;

    private long taskStatusBitSet;

    private Integer notifyFlag;

    private String processInfo;

    private String payload;

    private Integer currentTask;

    public Integer getRecId() {
        return recId;
    }

    public void setRecId(Integer recId) {
        this.recId = recId;
    }

    public long getInsertionTime() {
        return insertionTime;
    }

    public void setInsertionTime(long insertionTime) {
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

    public long getTaskBitSet() {
        return taskBitSet;
    }

    public void setTaskBitSet(long taskBitSet) {
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

    public long getTaskStatusBitSet() {
        return taskStatusBitSet;
    }

    public void setTaskStatusBitSet(long taskStatusBitSet) {
        this.taskStatusBitSet = taskStatusBitSet;
    }


    @Override
    public String toString() {
        return "KnAsyncNotifyDTO{" +
                "recId=" + recId +
                ", insertionTime=" + insertionTime +
                ", Status=" + Status +
                ", transactionId='" + txnId + '\'' +
                ", Priority=" + Priority +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", corpId=" + corpId +
                ", opsId=" + opsId +
                ", taskBitSet=" + taskBitSet +
                ", taskStatusBitSet=" + taskStatusBitSet +
                ", notifyFlag=" + notifyFlag +
                ", processInfo='" + processInfo + '\'' +
                ", payload='" + payload + '\'' +
                ", currentTask=" + currentTask +
                '}';
    }
}


