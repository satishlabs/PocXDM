/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import java.util.List;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnXDMTalkGroupRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO {

	private static final long serialVersionUID = -1462068107642427274L;
	private String operationType;
	private int clientType;
	private IAuthDTO authDTO;
	private String destPttServerId;
	private String destQueueName;
	private String transactionId;
	private List<KnXDMTalkGroupInfoDTO> addedCampGrpList;
	private List<KnXDMTalkGroupInfoDTO> modifiedCampGrpList;
	private List<KnXDMTalkGroupInfoDTO> removedCampGrpList;
	private Integer mode;
	private String mdn;
	private KnConstants.HIERARCHY_TYPE hierarchyType;
	private boolean upmCall;
	private String mcPttId;

	@Override
	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	@Override
	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
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

	public List<KnXDMTalkGroupInfoDTO> getAddedCampGrpList() {
		return addedCampGrpList;
	}

	public void setAddedCampGrpList(List<KnXDMTalkGroupInfoDTO> addedCampGrpList) {
		this.addedCampGrpList = addedCampGrpList;
	}

	public List<KnXDMTalkGroupInfoDTO> getModifiedCampGrpList() {
		return modifiedCampGrpList;
	}

	public void setModifiedCampGrpList(List<KnXDMTalkGroupInfoDTO> modifiedCampGrpList) {
		this.modifiedCampGrpList = modifiedCampGrpList;
	}

	public List<KnXDMTalkGroupInfoDTO> getRemovedCampGrpList() {
		return removedCampGrpList;
	}

	public void setRemovedCampGrpList(List<KnXDMTalkGroupInfoDTO> removedCampGrpList) {
		this.removedCampGrpList = removedCampGrpList;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public boolean isUpmCall() {
		return upmCall;
	}

	public void setUpmCall(boolean upmCall) {
		this.upmCall = upmCall;
	}


	public String getMcPttId() {
		return mcPttId;
	}

	public void setMcPttId(String mcPttId) {
		this.mcPttId = mcPttId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnXDMTalkGroupRequestDTO [operationType=").append(operationType).append(", clientType=").append(clientType)
				.append(", authDTO=").append(authDTO).append(", destPttServerId=").append(destPttServerId).append(", destQueueName=")
				.append(destQueueName).append(", transactionId=").append(transactionId).append(", addedCampGrpList=").append(addedCampGrpList)
				.append(", modifiedCampGrpList=").append(modifiedCampGrpList).append(", removedCampGrpList=").append(removedCampGrpList)
				.append(", mode=").append(mode).append(", mdn=").append(KnGDPRTemplate.mdn(mdn)).append(", hierarchyType=").append(hierarchyType)
				.append(", upmCall=").append(upmCall).append(", mcpttId=").append(KnGDPRTemplate.mcpttId(mcPttId)).append("]");
		return builder.toString();
	}

}
