/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/

package com.kodiak.common.commdto.response;

import com.kodiak.common.resources.KnConstants;

import java.util.Collection;
import java.util.List;

public class KnXDMExtGWProfileListDTO implements com.kodiak.common.commdto.response.IXDMResponseDTO {

    private static final long serialVersionUID = -34234234234234L;
    private List<KnExtGWProfileInfoDTO> gwProfileList;
    private String responseCode;
    private int responseStatus;
    private String responseMessage;
    private Collection responseDetails;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String operationType;


    public KnXDMExtGWProfileListDTO() {
    }

    public List<KnExtGWProfileInfoDTO> getExtGatewayInfoList() {
        return this.gwProfileList;
    }

    public void setExtGatewayInfoList(List<KnExtGWProfileInfoDTO> gwProfileList) {
        this.gwProfileList = gwProfileList;
    }

    public String getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(String responseCode) {
        if (responseCode != null) {
            responseCode = responseCode.trim();
            if (responseCode.equals("")) {
                responseCode = null;
            }
        }

        this.responseCode = responseCode;
    }

    public int getResponseStatus() {
        return this.responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        if (responseMessage != null) {
            responseMessage = responseMessage.trim();
            if (responseMessage.equals("")) {
                responseMessage = null;
            }
        }

        this.responseMessage = responseMessage;
    }

    public Collection getResponseDetails() {
        return this.responseDetails;
    }

    public void setResponseDetails(Collection responseDetails) {
        this.responseDetails = responseDetails;
    }

    public String getObjectId() {
        return this.transactionId;
    }

    public String getDestPttServerId() {
        return this.destPttServerId;
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
        return this.destQueueName;
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

    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        if (transactionId != null) {
            transactionId = transactionId.trim();
            if (transactionId.equals("")) {
                transactionId = null;
            }
        }

        this.transactionId = transactionId;
    }

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }


    public String toString() {
        return "KnXDMExtGWProfileListDTO{responseCode='" + this.responseCode + "', responseStatus="
                + this.responseStatus + ", responseMessage='" + this.responseMessage + "', responseDetails="
                + this.responseDetails + ", destPttServerId='" + this.destPttServerId + "', destQueueName='"
                + this.destQueueName + "', transactionId='" + this.transactionId + "', gwProfileList=" + this.gwProfileList + "}";
    }
}
