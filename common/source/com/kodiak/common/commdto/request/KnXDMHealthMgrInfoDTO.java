/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.resources.KnConstants;


public class KnXDMHealthMgrInfoDTO implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622676141L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String objectId;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private KnConstants.HIERARCHY_TYPE hierarchyType;

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

    public String getObjectId() {
        return objectId;
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


    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(", AuthDTO - ").append(authDTO)
                .append(", Operation_Type - ").append(operationType)
                .append(", Client_Type - ").append(clientType)
                .append(", Object_Id - ").append(objectId);
        strBuffer.append(", Dest_PTT_Server_ID - ").append(destPttServerId);
        strBuffer.append(", Dest_Queue_Name - ").append(destQueueName);
        strBuffer.append(", Transaction_ID - ").append(transactionId);
        strBuffer.append(", hierarchyType - ").append(hierarchyType);

        return strBuffer.toString();
    }
}
