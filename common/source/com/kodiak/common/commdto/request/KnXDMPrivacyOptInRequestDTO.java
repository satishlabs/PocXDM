/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnXDMPrivacyOptInRequestDTO implements IXDMRequestDTO {
	private static final long serialVersionUID = 7526471155622776161L;
	private String objectId;
	private int clientType;
	private IAuthDTO authDTO;
	private String destPttServerId;
	private String destQueueName;
	private String transactionId;
	private KnConstants.HIERARCHY_TYPE hierarchyType;
	private String txnId;
	private String mdn;
	private String operationType;
	private String optStatusValue;
	private String mcptt_id;

	public String getMcptt_id() {
		return mcptt_id;
	}

	public void setMcptt_id(String mcptt_id) {
		this.mcptt_id = mcptt_id;
	}

	public String getOptStatusValue() {
		return optStatusValue;
	}

	public void setOptStatusValue(String optStatusValue) {
		this.optStatusValue = optStatusValue;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public int getClientType() {
		return clientType;
	}

	public void setClientType(int clientType) {
		this.clientType = clientType;
	}

	public IAuthDTO getAuthDTO() {
		return authDTO;
	}

	public void setAuthDTO(IAuthDTO authDTO) {
		this.authDTO = authDTO;
	}

	public String getDestPttServerId() {
		return destPttServerId;
	}

	public void setDestPttServerId(String destPttServerId) {
		this.destPttServerId = destPttServerId;
	}

	public String getDestQueueName() {
		return destQueueName;
	}

	public void setDestQueueName(String destQueueName) {
		this.destQueueName = destQueueName;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}


	@Override
	public String toString() {
		return "KnXDMPrivacyOptInRequestDTO{" +
				"objectId='" + objectId + '\'' +
				", clientType=" + clientType +
				", authDTO=" + authDTO +
				", destPttServerId='" + destPttServerId + '\'' +
				", destQueueName='" + destQueueName + '\'' +
				", transactionId='" + transactionId + '\'' +
				", hierarchyType=" + hierarchyType +
				", txnId='" + txnId + '\'' +
				", mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
				", operationType='" + operationType + '\'' +
				", optStatusValue='" + optStatusValue + '\'' +
				'}';
	}
}
