/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * 
 * ***********************************************************************
 *  File name: KnXDMDeviceProvInfoDTO.java
 *  
 *  Name                 	Date         	   Release
 *  -------------------- -------------------- -------------------------------
 *  Kumar Abhinav        02-Jan-2020, 1:01:34 am      10.0
 *
 *  Copyright (c) 2019 Kodiak, A Motorola Solutions Company
 *  9th floor, MFar, Manayata Tech Park,
 *  Greenheart Phase IV,Nagawara
 *  Bangalore - 560 045
 *  www.kodiakptt.com
 *  All Rights Reserved.
 *
 *  This software is the confidential and proprietary information of Kodiak
 *  Networks, Inc. You shall not disclose such confidential information and
 *  shall use it only in accordance with the terms of the license agreement
 *  you entered into with Kodiak Networks.
 * ***********************************************************************
 */
package com.kodiak.common.commdto.request;

import java.util.Map;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.resources.KnConstants;

public class KnXDMDeviceProvInfoDTO extends KnXDMDeviceProvDTO implements IXDMRequestDTO {
	private static final long serialVersionUID = 7526471155622678950L;
	private int clientType;
	private String operationType;
	private String transactionId;
	private IAuthDTO authDTO;
	private long ifMatch;
	private long ifNoneMatch;
	private String destPttServerId;
	private String destQueueName;
	private KnUserAgentDTO userAgentDTO;
	private String srcRoutingKey;
	private String srcQueueName;
	private String correlationId;
	private String srcIPAddress;
	private Map<Integer, Integer> vocoderIdMap;
	private KnConstants.HIERARCHY_TYPE hierarchyType;
	private String version;
	private String entityId;
	private String txnId;
	private String fetchSize;
	private String nextToken;

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public String getSrcIPAddress() {
		return srcIPAddress;
	}

	public void setSrcIPAddress(String srcIPAddress) {
		this.srcIPAddress = srcIPAddress;
	}

	public void setCorrelationId(String correlationId) {
		if (correlationId != null) {
			correlationId = correlationId.trim();
			if (correlationId.equals("")) {
				correlationId = null;
			}
		}
		this.correlationId = correlationId;
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

	public int getClientType() {
		return clientType;
	}

	public void setOperationType(String operationType) {
		if (operationType != null) {
			operationType = operationType.trim();
			if (operationType.equals("")) {
				operationType = null;
			}
		}
		this.operationType = operationType;
	}

	public String getOperationType() {
		return operationType;
	}

	public String getObjectId() {
		return transactionId;
	}

	public long getIfMatch() {
		return ifMatch;
	}

	public void setIfMatch(long ifMatch) {
		this.ifMatch = ifMatch;
	}

	public long getIfNoneMatch() {
		return ifNoneMatch;
	}

	public void setIfNoneMatch(long ifNoneMatch) {
		this.ifNoneMatch = ifNoneMatch;
	}

	public String getTransactionId() {
		return transactionId;
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

	public KnUserAgentDTO getUserAgentDTO() {
		return userAgentDTO;
	}

	public void setUserAgentDTO(KnUserAgentDTO userAgentDTO) {
		this.userAgentDTO = userAgentDTO;
	}

	@Override
	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	@Override
	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

	public String getSrcRoutingKey() {
		return srcRoutingKey;
	}

	public void setSrcRoutingKey(String srcRoutingKey) {
		this.srcRoutingKey = srcRoutingKey;
	}

	public String getSrcQueueName() {
		return srcQueueName;
	}

	public void setSrcQueueName(String srcQueueName) {
		this.srcQueueName = srcQueueName;
	}

	public void setVocoderIdMap(Map<Integer, Integer> vocoderIdMap) {
		this.vocoderIdMap = vocoderIdMap;
	}

	public Map<Integer, Integer> getVocoderIdMap() {
		return vocoderIdMap;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(String fetchSize) {
		this.fetchSize = fetchSize;
	}

	public String getNextToken() {
		return nextToken;
	}

	public void setNextToken(String nextToken) {
		this.nextToken = nextToken;
	}

	@Override
	public String toString() {
		return "KnXDMDeviceProvInfoDTO [clientType=" + clientType + ", operationType=" + operationType
				+ ", transactionId=" + transactionId + ", authDTO=" + authDTO + ", ifMatch=" + ifMatch
				+ ", ifNoneMatch=" + ifNoneMatch + ", destPttServerId=" + destPttServerId + ", destQueueName="
				+ destQueueName + ", userAgentDTO=" + userAgentDTO + ", srcRoutingKey=" + srcRoutingKey
				+ ", srcQueueName=" + srcQueueName + ", correlationId=" + correlationId + ", srcIPAddress="
				+ srcIPAddress + ", vocoderIdMap=" + vocoderIdMap + ", hierarchyType=" + hierarchyType + ", version="
				+ version + "]";
	}

}
