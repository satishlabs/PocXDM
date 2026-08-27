/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnXDMSubscFeatureRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO {

	private static final long serialVersionUID = 1L;
	private String operationType;
	private int clientType;
	private IAuthDTO authDTO;
	private String destPttServerId;
	private String destQueueName;
	private String transactionId;
	private String mdn;

	private KnSubsriberFeatureInfo subsFeatureInfo;
	private KnConstants.HIERARCHY_TYPE hierarchyType;

	@Override
	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	@Override
	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
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
	public String getDestPttServerId() {
		return destPttServerId;
	}

	@Override
	public void setDestPttServerId(String destPttServerId) {
		this.destPttServerId = destPttServerId;
	}

	@Override
	public String getDestQueueName() {
		return destQueueName;
	}

	@Override
	public void setDestQueueName(String destQueueName) {
		this.destQueueName = destQueueName;
	}

	@Override
	public String getTransactionId() {
		return transactionId;
	}

	@Override
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public KnSubsriberFeatureInfo getSubsFeatureInfo() {
		return subsFeatureInfo;
	}

	public void setSubsFeatureInfo(KnSubsriberFeatureInfo subsFeatureInfo) {
		this.subsFeatureInfo = subsFeatureInfo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnXDMSubscFeatureRequestDTO [operationType=").append(operationType).append(", clientType=").append(clientType)
				.append(", authDTO=").append(authDTO).append(", destPttServerId=").append(destPttServerId).append(", destQueueName=")
				.append(destQueueName).append(", transactionId=").append(transactionId).append(", mdn=").append(KnGDPRTemplate.mdn(mdn)).append(", subsFeatureInfo=")
				.append(subsFeatureInfo).append(", hierarchyType=").append(hierarchyType).append("]");
		return builder.toString();
	}

}
