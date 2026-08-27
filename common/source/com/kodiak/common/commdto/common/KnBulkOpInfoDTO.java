/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.util.Map;

import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnConstants;

public class KnBulkOpInfoDTO implements IIdentifier, IXDMRequestDTO{

	private static final long serialVersionUID = -5141708044248119590L;

	private String transID;
	private String operationID;
	private String respCode;
	private int priority;
	private Map<String, Object> customParamMap;
	private KnConstants.HIERARCHY_TYPE hierarchyType;

    //stores the Client Type
    private int clientType;

    //stores the operation Type
    private String operationType;

    private String transactionId;

    private IAuthDTO authDTO;

    private String destPttServerId;

    private String destQueueName;


	public String getTransID() {
		return transID;
	}

	public void setTransID(String transID) {
		this.transID = transID;
	}

	public String getOperationID() {
		return operationID;
	}


	public void setOperationID(String operationID) {
		this.operationID = operationID;
	}


	public String getRespCode() {
		return respCode;
	}


	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}


	public int getPriority() {
		return priority;
	}


	public void setPriority(int priority) {
		this.priority = priority;
	}


	public Map<String, Object> getCustomParamMap() {
		return customParamMap;
	}


	public void setCustomParamMap(Map<String, Object> customParamMap) {
		this.customParamMap = customParamMap;
	}

	@Override
	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	@Override
	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
	}


	@Override
	public String getObjectId() {
		return transID;
	}

	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" transactionID - ").append(transID)
                .append(", operationID - ").append(operationID)
                .append(", respCode - ").append(respCode)
                .append(", priority - ").append(priority)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", customParamMap - ").append(customParamMap);

        return builder.toString();
    }


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
		return clientType;
	}


	@Override
	public void setClientType(int clientType) {
		this.clientType = clientType;
	}


	@Override
	public IAuthDTO getAuthDTO() {
		return authDTO;
	}


	@Override
	public void setAuthDTO(IAuthDTO authDTO) {
		this.authDTO = authDTO;
	}


	@Override
	public void setDestPttServerId(String destPttServerId) {
		this.destPttServerId = destPttServerId;

	}


	@Override
	public String getDestPttServerId() {
		return destPttServerId;
	}


	@Override
	public void setDestQueueName(String destQueueName) {
		this.destQueueName = destQueueName;
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
		this.transactionId = transactionId;
	}

}
