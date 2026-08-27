/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnSubClientSettingsDTO;
import com.kodiak.common.resources.KnConstants;

public class KnSubscrClientSettingsRequestDTO extends KnSubClientSettingsDTO implements  IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622676164L;

    private String mdn;

    private String recordingStatus;

    private String corpId;

    private String operationType;

    private int clientType;

    private IAuthDTO authDTO;

    private String transactionId;

    private String destPttServerId;

    private String destQueueName;

    private KnConstants.HIERARCHY_TYPE hierarchyType;


    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getCorpId() { return corpId;  }

    public void setCorpId(String corpId) { this.corpId = corpId; }

    public void setRecordingStatus(String recordingStatus) {
        this.recordingStatus = recordingStatus;
    }


    public String getRecordingStatus() {
        return recordingStatus;
    }

    @Override
    public String getOperationType() {
        return operationType;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType=operationType;
    }

    @Override
    public int getClientType() {
        return clientType;
    }

    @Override
    public void setClientType(int clientType) {
        this.clientType=clientType;
    }

    @Override
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO=authDTO;
    }

    @Override
    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId=destPttServerId;
    }

    @Override
    public String getDestPttServerId() {
        return destPttServerId;
    }

    @Override
    public void setDestQueueName(String destQueueName) {
        this.destQueueName=destQueueName;
    }

    @Override
    public String getDestQueueName() {
        return destQueueName;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId=transactionId;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) { this.hierarchyType = hierarchyType; }

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() { return hierarchyType; }

    @Override
    public String getObjectId() {
        return getObjectId();
    }

    @Override
    public String toString() {
        return "KnSubscrClientSettingsRequestDTO{" +
                "mdn='" + mdn + '\'' +
                ", recordingStatus='" + recordingStatus + '\'' +
                ", corpId='" + corpId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", clientType=" + clientType +
                ", authDTO=" + authDTO +
                ", transactionId='" + transactionId + '\'' +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                '}';
    }
}

