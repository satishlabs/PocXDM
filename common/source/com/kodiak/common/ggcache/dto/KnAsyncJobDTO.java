/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dto;

public class KnAsyncJobDTO {
/**
  * ************************************************************************
  * <p>
  * File name:  KnAsyncJobDTO.java
  * Subsystem:  PoC
  * <p>
  * Name                         Date                     Release
  * --------------------     ----------------        ------------------
  * Venkata Sudhakar Talluri             Nov 8, 2019                10.0
  * <p>
  * <p>
  * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
  * Greenheart Phase IV, Nagawara
  * Bangalore - 560 045
  * www.kodiakptt.com
  * All Rights Reserved.
  * <p>
  * This software is the confidential and proprietary information of Kodiak
  * Networks, Inc. You shall not disclose such confidential information and
  * shall use it only in accordance with the terms of the license agreement
  * you entered into with Kodiak Networks.
  * ************************************************************************
  *
  **/

    private String txnId;
    private int corpId;
    private String userProfileId;
    private String resourceEntity;
    private int resourceType;
    private String payLoad;
    private int opType;
    private int opStatus;
    private String creationTime;
    private String updationTime;
    private String callBackUri;

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public int getCorpId() { return corpId; }

    public void setCorpId(int corpId) { this.corpId = corpId; }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) { this.userProfileId = userProfileId; }


    public String getResourceEntity() { return resourceEntity; }

    public void setResourceEntity(String resourceEntity) {  this.resourceEntity = resourceEntity; }

    public int getResourceType() { return resourceType; }

    public void setResourceType(int resourceType) { this.resourceType = resourceType; }

    public String getUpdationTime() { return updationTime; }

    public void setUpdationTime(String updationTime) { this.updationTime = updationTime; }

    public String getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public int getOpStatus() {
        return opStatus;
    }

    public void setOpStatus(int opStatus) {
        this.opStatus = opStatus;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getCallBackUri() {
        return callBackUri;
    }

    public void setCallBackUri(String callBackUri) {
        this.callBackUri = callBackUri;
    }

    @Override
    public String toString() {
        return "KnAsyncJobDTO{" +
                "txnId='" + txnId + '\'' +
                ", corpId=" + corpId +
                ", userProfileId='" + userProfileId + '\'' +
                ", resourceEntity='" + resourceEntity + '\'' +
                ", resourceType=" + resourceType +
                ", payLoad='" + payLoad + '\'' +
                ", opType=" + opType +
                ", opStatus=" + opStatus +
                ", creationTime='" + creationTime + '\'' +
                ", updationTime='" + updationTime + '\'' +
                ", callBackUri='" + callBackUri + '\'' +
                '}';
    }
}
