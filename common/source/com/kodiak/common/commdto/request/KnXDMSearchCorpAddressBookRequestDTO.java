/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnXDMSearchCorpAddressBookRequestDTO implements IXDMRequestDTO {
	private static final long serialVersionUID = 7526471155622776161L;
	private String objectId;
	private int clientType;
	private IAuthDTO authDTO;
	private String destPttServerId;
	private String destQueueName;
	private String transactionId;
	private KnConstants.HIERARCHY_TYPE hierarchyType;
	private String txnId;
	private Integer pageId;
	private String searchString;
	private String mdn;
	private String operationType;
	private Integer maxPageSize;
	private String mcpttId;

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

	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
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

	public Integer getMaxPageSize() {
		return maxPageSize;
	}

	public void setMaxPageSize(Integer maxPageSize) {
		this.maxPageSize = maxPageSize;
	}

	public String getMcpttId() {
		return mcpttId;
	}

	public void setMcpttId(String mcpttId) {
		this.mcpttId = mcpttId;
	}

	@Override
	public String toString() {
		return "KnXDMSearchCorpAddressBookRequestDTO [txnId=" + txnId + ", pageId=" + pageId + ", searchString="
				+ searchString + ", mdn=" + KnGDPRTemplate.mdn(mdn) + ", operationType=" + operationType + ", maxPageSize=" + maxPageSize
				 + ", mcpttId=" + KnGDPRTemplate.mcpttId(mcpttId)+ "]";
	}

}
