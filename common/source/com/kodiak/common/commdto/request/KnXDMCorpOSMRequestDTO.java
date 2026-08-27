/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;

import java.util.Collection;
import java.util.Set;



public class KnXDMCorpOSMRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO {


	/**
	 *
	 */
	private static final long serialVersionUID = 65080740387536829L;

	private String operationType;
	private int clientType;
	private IAuthDTO authDTO;
	private String destPttServerId;
	private String destQueueName;
	private String transactionId;

	private String corpId;
	private String OSMListId;
	private String OSMListName;
	private String isDefault;
	private Set<KnXDMOSMInfoRequestDTO> addedOSMMsgList;
	private Set<KnXDMOSMInfoRequestDTO> modifiedOSMMsgList;
	private Set<KnXDMOSMInfoRequestDTO> removedOSMMsgList;
	private Collection<String> assignedOSMIdToGroupIds;
	private Collection<String> removedOSMIdFromGroupIds;
	private Collection<String> OSMListIds;
    private String hierarchyId;

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

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getOSMListName() {
		return OSMListName;
	}

	public void setOSMListName(String oSMListName) {
		OSMListName = oSMListName;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public Set<KnXDMOSMInfoRequestDTO> getAddedOSMMsgList() {
		return addedOSMMsgList;
	}

	public void setAddedOSMMsgList(Set<KnXDMOSMInfoRequestDTO> addedOSMMsgList) {
		this.addedOSMMsgList = addedOSMMsgList;
	}

	public Set<KnXDMOSMInfoRequestDTO> getModifiedOSMMsgList() {
		return modifiedOSMMsgList;
	}

	public void setModifiedOSMMsgList(Set<KnXDMOSMInfoRequestDTO> modifiedOSMMsgList) {
		this.modifiedOSMMsgList = modifiedOSMMsgList;
	}

	public Set<KnXDMOSMInfoRequestDTO> getRemovedOSMMsgList() {
		return removedOSMMsgList;
	}

	public void setRemovedOSMMsgList(Set<KnXDMOSMInfoRequestDTO> removedOSMMsgList) {
		this.removedOSMMsgList = removedOSMMsgList;
	}

	public String getOSMListId() {
		return OSMListId;
	}

	public void setOSMListId(String oSMListId) {
		OSMListId = oSMListId;
	}

	public Collection<String> getAssignedOSMIdToGroupIds() {
		return assignedOSMIdToGroupIds;
	}

	public void setAssignedOSMIdToGroupIds(Collection<String> assignedOSMIdToGroupIds) {
		this.assignedOSMIdToGroupIds = assignedOSMIdToGroupIds;
	}

	public Collection<String> getRemovedOSMIdFromGroupIds() {
		return removedOSMIdFromGroupIds;
	}

	public void setRemovedOSMIdFromGroupIds(Collection<String> removedOSMIdFromGroupIds) {
		this.removedOSMIdFromGroupIds = removedOSMIdFromGroupIds;
	}

    public Collection<String> getOSMListIds() {
        return OSMListIds;
    }

    public void setOSMListIds(Collection<String> OSMListIds) {
        this.OSMListIds = OSMListIds;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    @Override
    public String toString() {
        return "KnXDMCorpOSMRequestDTO [operationType=" + operationType + ", clientType=" + clientType + ", authDTO="
                + authDTO + ", destPttServerId=" + destPttServerId + ", destQueueName=" + destQueueName
                + ", transactionId=" + transactionId + ", corpId=" + corpId + ", OSMListId=" + OSMListId
                + ", OSMListName=" + OSMListName + ", isDefault=" + isDefault + ", addedOSMMsgList=" + addedOSMMsgList
                + ", modifiedOSMMsgList=" + modifiedOSMMsgList + ", removedOSMMsgList=" + removedOSMMsgList
                + ", assignedOSMIdToGroupIds=" + assignedOSMIdToGroupIds + ", removedOSMIdFromGroupIds="
                + removedOSMIdFromGroupIds + ", OSMListIds=" + OSMListIds + ", hierarchyId=" + hierarchyId + "]";
    }

}
