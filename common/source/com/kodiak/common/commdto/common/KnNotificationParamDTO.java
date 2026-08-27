/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/*
 *  Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
 *  All Rights Reserved
 *  Motorola Solutions Confidential Restricted
 *
 */

package com.kodiak.common.commdto.common;

public class KnNotificationParamDTO {

    private Long insertionTime;
    private String destId;
    private int destType;
    private int notifyStatus;
    private int payloadVersion;
    private byte[] payload;


    private String cid;
    private int opsCode;
    private int priority = 5;
    private int retryCount;
    private int notifyType;
    private int protocol;

    private int msgType;

    public Long getInsertionTime() {
        return insertionTime;
    }

    public void setInsertionTime(Long insertionTime) {
        this.insertionTime = insertionTime;
    }

    public String getDestId() {
        return destId;
    }

    public void setDestId(String destId) {
        this.destId = destId;
    }

    public int getDestType() {
        return destType;
    }

    public void setDestType(int destType) {
        this.destType = destType;
    }

    public int getNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(int notifyStatus) {
        this.notifyStatus = notifyStatus;
    }

    public int getPayloadVersion() {
        return payloadVersion;
    }

    public void setPayloadVersion(int payloadVersion) {
        this.payloadVersion = payloadVersion;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getOpsCode() {
        return opsCode;
    }

    public void setOpsCode(int opsCode) {
        this.opsCode = opsCode;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(int notifyType) {
        this.notifyType = notifyType;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("KnNotificationParamDTO{");
        sb.append("insertionTime=").append(insertionTime);
        sb.append(", destId='").append(destId).append('\'');
        sb.append(", destType=").append(destType);
        sb.append(", notifyStatus=").append(notifyStatus);
        sb.append(", payloadVersion=").append(payloadVersion);
        sb.append(", cid='").append(cid).append('\'');
        sb.append(", opsCode=").append(opsCode);
        sb.append(", priority=").append(priority);
        sb.append(", retryCount=").append(retryCount);
        sb.append(", notifyType=").append(notifyType);
        sb.append(", protocol=").append(protocol);
        sb.append(", msgType=").append(msgType);
        sb.append('}');
        return sb.toString();
    }
}