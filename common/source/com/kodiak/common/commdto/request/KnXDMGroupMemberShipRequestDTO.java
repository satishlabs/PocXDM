/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.List;

public class KnXDMGroupMemberShipRequestDTO implements IXDMRequestDTO {

    private static final long serialVersionUID = -3146823258608115563L;

    private KnConstants.HIERARCHY_TYPE hierarchyType;

    private String operationType;
    private int clientType;

    private IAuthDTO authDTO;

    private String transactionId;
    private String version;



    private String destPttServerId;

    private String destQueueName;

    private List<Integer> groupIdList;

    private String mdn;


    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    /**
     * getter method for Operation Type
     *
     * @return String
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * setter method for Operation Type
     *
     * @param operationType String
     */
    public void setOperationType(String operationType) {
        if (operationType != null) {
            operationType = operationType.trim();
            if (operationType.equals("")) {
                operationType = null;
            }
        }
        this.operationType = operationType;
    }

    /**
     * getter method for client Type
     *
     * @return int
     */
    public int getClientType() {
        return clientType;
    }

    /**
     * setter method for client Type
     *
     * @param clientType int
     */
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    /**
     * getter method for the Auth DTO
     *
     * @return IAuthDTO
     */
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    /**
     * setter method for the Auth DTO
     *
     * @param authDTO IAuthDTO
     */
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    /**
     * getter method for transaction ID
     *
     * @return String
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * setter method for Transaction Id
     *
     * @param transactionId String
     */
    public void setTransactionId(String transactionId) {
        if (transactionId != null) {
            transactionId = transactionId.trim();
            if (transactionId.equals("")) {
                transactionId = null;
            }
        }
        this.transactionId = transactionId;
    }

    public String getDestPttServerId() {
        return destPttServerId;
    }

    public void setDestPttServerId(String destPttServerId) {
        if (destPttServerId != null) {
            destPttServerId = destPttServerId.trim();
            if (destPttServerId.equals("")) {
                destPttServerId = null;
            }
        }
        this.destPttServerId = destPttServerId;
    }

    public String getDestQueueName() {
        return destQueueName;
    }

    public void setDestQueueName(String destQueueName) {
        if (destQueueName != null) {
            destQueueName = destQueueName.trim();
            if (destQueueName.equals("")) {
                destQueueName = null;
            }
        }
        this.destQueueName = destQueueName;
    }

    public String getObjectId() {
        return transactionId;
    }

    public List<Integer> getGroupIdList() { return groupIdList; }

    public void setGroupIdList(List<Integer> groupIdList) { this.groupIdList = groupIdList; }

    public String getMdn() { return mdn; }

    public void setMdn(String mdn) { this.mdn = mdn; }

    public String getVersion() { return version; }

    public void setVersion(String version) { this.version = version; }


    @Override
    public String toString() {
        return "KnGroupMemberShipRequestDTO{" +
                "hierarchyType=" + hierarchyType +
                ", operationType='" + operationType + '\'' +
                ", clientType=" + clientType +
                ", authDTO=" + authDTO +
                ", transactionId='" + transactionId + '\'' +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                ", groupIdList=" + groupIdList +
                ", mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                '}';
    }

}
