/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

import java.util.List;

public class KnXDMCorpGroupProfileDTO implements IIdentifier {

	private static final long serialVersionUID = -1978384555405330598L;
	private String grpProfileName;
	private Integer grpType;
	private Integer grpAvatar;
	private Integer grpServiceType;
	private String grpOSMListId;
	private Integer audioCutIn;
	private Integer mcxGroup;
	private Integer grpProfileId;
	private Integer grpCount;
	private Long createTimeStamp;
	private Long updateTimeStamp;
	private Integer grpProfileStatus;
	private Integer featureAllowed;
	private Integer overrideDND;
	private Integer grpShared;
	private List<KnXDMCorpGrpSharedCorpListDTO> sharedCorpList;
	private Integer grpOwnerCorpId;
	private String grpOwnerExtCorpId;
	private Integer ugwInterop;

	@Override
	public String getObjectId() {
		// TODO Auto-generated method stub
		return grpProfileName;
	}

	public String getGrpProfileName() {
		return grpProfileName;
	}

	public void setGrpProfileName(String grpProfileName) {
		this.grpProfileName = grpProfileName;
	}

	public Integer getGrpType() {
		return grpType;
	}

	public void setGrpType(Integer grpType) {
		this.grpType = grpType;
	}

	public Integer getGrpAvatar() {
		return grpAvatar;
	}

	public void setGrpAvatar(Integer grpAvatar) {
		this.grpAvatar = grpAvatar;
	}

	public Integer getGrpServiceType() {
		return grpServiceType;
	}

	public void setGrpServiceType(Integer grpServiceType) {
		this.grpServiceType = grpServiceType;
	}

	public String getGrpOSMListId() {
		return grpOSMListId;
	}

	public void setGrpOSMListId(String grpOSMListId) {
		this.grpOSMListId = grpOSMListId;
	}

	public Integer getAudioCutIn() {
		return audioCutIn;
	}

	public void setAudioCutIn(Integer audioCutIn) {
		this.audioCutIn = audioCutIn;
	}

	public Integer getMcxGroup() {
		return mcxGroup;
	}

	public void setMcxGroup(Integer mcxGroup) {
		this.mcxGroup = mcxGroup;
	}

	public Integer getGrpProfileId() {
		return grpProfileId;
	}

	public void setGrpProfileId(Integer grpProfileId) {
		this.grpProfileId = grpProfileId;
	}

	public Integer getGrpCount() {
		return grpCount;
	}

	public void setGrpCount(Integer grpCount) {
		this.grpCount = grpCount;
	}

	
	public Long getCreateTimeStamp() {
		return createTimeStamp;
	}

	public void setCreateTimeStamp(Long createTimeStamp) {
		this.createTimeStamp = createTimeStamp;
	}

	public Long getUpdateTimeStamp() {
		return updateTimeStamp;
	}

	public void setUpdateTimeStamp(Long updateTimeStamp) {
		this.updateTimeStamp = updateTimeStamp;
	}

	public Integer getGrpProfileStatus() {
		return grpProfileStatus;
	}

	public void setGrpProfileStatus(Integer grpProfileStatus) {
		this.grpProfileStatus = grpProfileStatus;
	}

	public Integer getFeatureAllowed() {
		return featureAllowed;
	}

	public void setFeatureAllowed(Integer featureAllowed) {
		this.featureAllowed = featureAllowed;
	}

	public Integer getOverrideDND() { return overrideDND; }

	public void setOverrideDND(Integer overrideDND) { this.overrideDND = overrideDND; }

	public List<KnXDMCorpGrpSharedCorpListDTO> getSharedCorpList() { return sharedCorpList; }

	public void setSharedCorpList(List<KnXDMCorpGrpSharedCorpListDTO> sharedCorpList) { this.sharedCorpList = sharedCorpList; }

	public Integer getGrpShared() { return grpShared; }

	public void setGrpShared(Integer grpShared) { this.grpShared = grpShared; }

	public Integer getGrpOwnerCorpId() { return grpOwnerCorpId; }

	public void setGrpOwnerCorpId(Integer grpOwnerCorpId) { this.grpOwnerCorpId = grpOwnerCorpId; }

	public String getGrpOwnerExtCorpId() { return grpOwnerExtCorpId; }

	public void setGrpOwnerExtCorpId(String grpOwnerExtCorpId) { this.grpOwnerExtCorpId = grpOwnerExtCorpId; }

	public Integer getUgwInterop() {
		return ugwInterop;
	}

	public void setUgwInterop(Integer ugwInterop) {
		this.ugwInterop = ugwInterop;
	}

	@Override
	public String toString() {
		return "KnXDMCorpGroupProfileDTO{" +
				"grpProfileName='" + grpProfileName + '\'' +
				", grpType=" + grpType +
				", grpAvatar=" + grpAvatar +
				", grpServiceType=" + grpServiceType +
				", grpOSMListId='" + grpOSMListId + '\'' +
				", audioCutIn=" + audioCutIn +
				", mcxGroup=" + mcxGroup +
				", grpProfileId=" + grpProfileId +
				", grpCount=" + grpCount +
				", createTimeStamp=" + createTimeStamp +
				", updateTimeStamp=" + updateTimeStamp +
				", grpProfileStatus=" + grpProfileStatus +
				", featureAllowed=" + featureAllowed +
				", overrideDND=" + overrideDND +
				", grpShared=" + grpShared +
				", ugwInterop=" + ugwInterop +
				", sharedCorpList=" + sharedCorpList +
				", grpOwnerCorpId='" + grpOwnerCorpId + '\'' +
				", grpOwnerExtCorpId='" + grpOwnerExtCorpId + '\'' +
				'}';
	}
}
