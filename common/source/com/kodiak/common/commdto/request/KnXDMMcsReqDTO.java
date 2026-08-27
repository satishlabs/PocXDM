/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnXDMMcsReqDTO implements IXDMRequestDTO {
	private static final long serialVersionUID = 7526471155622776161L;
	private String objectId;
	private int clientType;
	private String userAgent;
	private String corpID;
	private String groupID;
	private String gmsFQDN;
	private IAuthDTO authDTO;
	private String destPttServerId;
	private String destQueueName;
	private String transactionId;
	private KnConstants.HIERARCHY_TYPE hierarchyType;
	private String mcId;
	private String mcpttID;
	private String operationType;
	private String auid;
	private String fileName;
	private long ifMatch;
	private long ifNoneMatch;
	private String version;
	private String clientFS2;



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

	public String getUserAgent() {return userAgent; }

	public void setUserAgent(String userAgent) {this.userAgent = userAgent; }

	public String getCorpID() {return corpID; }

	public void setCorpID(String corpID) {
		this.corpID = corpID;
	}

	public String getGroupID() {return groupID; }

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getGmsFQDN() {return gmsFQDN; }

	public void setGmsFQDN(String gmsFQDN) {
		this.gmsFQDN = gmsFQDN;
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

	public String getMcId() {return mcId; }

	public void setMcId(String mcId) {this.mcId = mcId; }

	public String getMcpttID() {return mcpttID; }

	public void setMcpttID(String mcpttID) {
		this.mcpttID = mcpttID;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getAuid() {
		return auid;
	}

	public void setAuid(String auid) {
		this.auid = auid;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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

	public String getClientFS2() {	return clientFS2; }

	public void setClientFS2(String clientFS2) {	this.clientFS2 = clientFS2; }

	@Override
	public String toString() {
		return "KnXDMMcsReqDTO{" +
				"objectId='" + objectId + '\'' +
				", clientType=" + clientType +
				", userAgent=" + userAgent +
				", authDTO=" + authDTO +
				", destPttServerId='" + destPttServerId + '\'' +
				", destQueueName='" + destQueueName + '\'' +
				", transactionId='" + transactionId + '\'' +
				", hierarchyType=" + hierarchyType +
				", mcId='" + KnGDPRTemplate.mcId(mcId) + '\'' +
				", mcpttID='" + KnGDPRTemplate.mcpttId(mcpttID) + '\'' +
				", operationType='" + operationType + '\'' +
				", auid='" + auid + '\'' +
				", fileName='" + fileName + '\'' +
				", version='" + version + '\'' +
				", ifMatch='" + ifMatch + '\'' +
				", ifNoneMatch='" + ifNoneMatch + '\'' +
				", clientFS2='" + clientFS2 + '\'' +
				'}';
	}
}
