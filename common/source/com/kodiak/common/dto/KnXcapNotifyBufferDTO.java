/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */

package com.kodiak.common.dto;
public class KnXcapNotifyBufferDTO {

    Integer serviceType;
    String txnId;
    Integer seqNum;
    Integer entityType;
    String entityID;
    Integer directoryEtag;
    Integer docType;
    Integer docEtag;
    Long insertionTime;
    Integer status;
    String payload;

    public Integer getServiceType() {
        return serviceType;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public Integer getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(Integer seqNum) {
        this.seqNum = seqNum;
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public Integer getDirectoryEtag() {
        return directoryEtag;
    }

    public void setDirectoryEtag(Integer directoryEtag) {
        this.directoryEtag = directoryEtag;
    }

    public Integer getDocType() {
        return docType;
    }

    public void setDocType(Integer docType) {
        this.docType = docType;
    }

    public Integer getDocEtag() {
        return docEtag;
    }

    public void setDocEtag(Integer docEtag) {
        this.docEtag = docEtag;
    }

    public Long getInsertionTime() {
        return insertionTime;
    }

    public void setInsertionTime(Long insertionTime) {
        this.insertionTime = insertionTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "KnXcapNotifyBufferDTO{" +
                "serviceType=" + serviceType +
                ", txnId='" + txnId + '\'' +
                ", seqNum=" + seqNum +
                ", entityType=" + entityType +
                ", entityID='" + entityID + '\'' +
                ", directoryEtag=" + directoryEtag +
                ", docType=" + docType +
                ", docEtag=" + docEtag +
                ", insertionTime=" + insertionTime +
                ", status=" + status +
                ", payload='" + payload + '\'' +
                '}';
    }
}
