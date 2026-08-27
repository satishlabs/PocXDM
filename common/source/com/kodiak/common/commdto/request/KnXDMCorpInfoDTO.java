/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;
import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.resources.KnConstants;
public class KnXDMCorpInfoDTO implements IXDMRequestDTO {
    private static final long serialVersionUID = 7526471155622676145L;
    private String operationType;
    private String transactionId;
    private String accountId;
    @Override
    public String getOperationType() {
        return operationType;
    }
    @Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    @Override
    public int getClientType() {
        return 0;
    }
    @Override
    public void setClientType(int clientType) {
    }
    @Override
    public IAuthDTO getAuthDTO() {
        return null;
    }
    @Override
    public void setAuthDTO(IAuthDTO authDTO) {
    }
    @Override
    public void setDestPttServerId(String destPttServerId) {
    }
    @Override
    public String getDestPttServerId() {
        return null;
    }
    @Override
    public void setDestQueueName(String destQueueName) {
    }
    @Override
    public String getDestQueueName() {
        return null;
    }
    @Override
    public String getTransactionId() {
        return transactionId;
    }
    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
    }
    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return null;
    }
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    @Override
    public String toString() {
        return "KnXDMCorpInfoDTO{" +
                "operationType='" + operationType + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", accountId='" + accountId + '\'' +
                '}';
    }
    @Override
    public String getObjectId() {
        return null;
    }
}