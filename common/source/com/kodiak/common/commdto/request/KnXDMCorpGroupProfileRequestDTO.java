/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpGrpSharedCorpListDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;

import java.util.List;

public class KnXDMCorpGroupProfileRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO {
	
	private static final long serialVersionUID = -5915280086436366885L;
	private String operationType;
	private int clientType;
	private IAuthDTO authDTO;
	private String destPttServerId;
	private String destQueueName;
	private String transactionId;
	private String grpProfileName;
	private Integer grpType;
	private Integer grpAvatar;
	private Integer grpServiceType;
	private String grpOSMListId;
	private Integer audioCutIn;
	private Integer mcxGroup;
	private Integer grpProfileId;
	private Integer startIndex;
	private Integer fetchSize;
	private Integer overrideDND;
	private String newGrpProfileName;
	private Integer grpShared;
	private List<KnXDMCorpGrpSharedCorpListDTO> sharedCorpList;
	private String ugwInterop;
    private String hierarchyId;

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
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

	public void setGrpProfileName(String grpProfileName) {
		this.grpProfileName = grpProfileName;
	}

	public void setGrpType(Integer grpType) {
		this.grpType = grpType;
	}

	public void setGrpAvatar(Integer grpAvatar) {
		this.grpAvatar = grpAvatar;
	}

	public void setGrpServiceType(Integer grpServiceType) {
		this.grpServiceType = grpServiceType;
	}

	public void setGrpOSMListId(String grpOSMListId) {
		this.grpOSMListId = grpOSMListId;
	}

	public void setAudioCutIn(Integer audioCutIn) {
		this.audioCutIn = audioCutIn;
	}

	public void setMcxGroup(Integer mcxGroup) {
		this.mcxGroup = mcxGroup;
	}

	public String getGrpProfileName() {
		return grpProfileName;
	}

	public Integer getGrpType() {
		return grpType;
	}

	public Integer getGrpAvatar() {
		return grpAvatar;
	}

	public Integer getGrpServiceType() {
		return grpServiceType;
	}

	public String getGrpOSMListId() {
		return grpOSMListId;
	}

	public Integer getAudioCutIn() {
		return audioCutIn;
	}

	public Integer getMcxGroup() {
		return mcxGroup;
	}

	
	public Integer getGrpProfileId() {
		return grpProfileId;
	}

	public void setGrpProfileId(Integer grpProfileId) {
		this.grpProfileId = grpProfileId;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(Integer fetchSize) {
		this.fetchSize = fetchSize;
	}

	public Integer getOverrideDND() { return overrideDND; }

	public void setOverrideDND(Integer overrideDND) { this.overrideDND = overrideDND; }

	public String getNewGrpProfileName() {
		return newGrpProfileName;
	}

	public void setNewGrpProfileName(String newGrpProfileName) {
		this.newGrpProfileName = newGrpProfileName;
	}

	public Integer getGrpShared() { return grpShared; }

	public void setGrpShared(Integer grpShared) { this.grpShared = grpShared; }

	public List<KnXDMCorpGrpSharedCorpListDTO> getSharedCorpList() { return sharedCorpList; }

	public void setSharedCorpList(List<KnXDMCorpGrpSharedCorpListDTO> sharedCorpList) { this.sharedCorpList = sharedCorpList; }

	public String getUgwInterop() {
		return ugwInterop;
	}

	public void setUgwInterop(String ugwInterop) {
		this.ugwInterop = ugwInterop;
	}

	@Override
	public String toString() {
		return "KnXDMCorpGroupProfileRequestDTO [operationType=" + operationType + ", clientType=" + clientType
				+ ", authDTO=" + authDTO + ", destPttServerId=" + destPttServerId + ", destQueueName=" + destQueueName
				+ ", transactionId=" + transactionId + ", grpProfileName=" + grpProfileName + ", grpType=" + grpType
				+ ", grpAvatar=" + grpAvatar + ", grpServiceType=" + grpServiceType + ", grpOSMListId=" + grpOSMListId
				+ ", audioCutIn=" + audioCutIn + ", mcxGroup=" + mcxGroup + ", grpProfileId=" + grpProfileId
				+ ", startIndex=" + startIndex + ", fetchSize=" + fetchSize + ", overrideDND=" + overrideDND
				+ ", newGrpprofileName = " + newGrpProfileName +"+ , grpShared = " + grpShared + ", ugwInterop= " + ugwInterop +", hierarchyId= "+ hierarchyId + "]";
	}

}
