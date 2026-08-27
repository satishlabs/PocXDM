/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSharedCorpInfo;

import java.util.List;

public class KnIPCorpGorupProfileDTO extends KnIPCorpInfoDTO {
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
	private List<KnCorpSharedCorpInfo> sharedCorpList;
	private String ugwInterop;
	private Integer videoCallPermission;

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

	public List<KnCorpSharedCorpInfo> getSharedCorpList() { return sharedCorpList; }

	public void setSharedCorpList(List<KnCorpSharedCorpInfo> sharedCorpList) { this.sharedCorpList = sharedCorpList; }

	public String getUgwInterop() {
		return ugwInterop;
	}

	public void setUgwInterop(String ugwInterop) {
		this.ugwInterop = ugwInterop;
	}

	public Integer getVideoCallPermission() {
		return videoCallPermission;
	}

	public void setVideoCallPermission(Integer videoCallPermission) {
		this.videoCallPermission = videoCallPermission;
	}

	@Override
	public String toString() {
		return "KnIPCorpGorupProfileDTO [grpProfileName=" + grpProfileName + ", grpType=" + grpType + ", grpAvatar="
				+ grpAvatar + ", grpServiceType=" + grpServiceType + ", grpOSMListId=" + grpOSMListId + ", audioCutIn="
				+ audioCutIn + ", mcxGroup=" + mcxGroup + ", grpProfileId=" + grpProfileId + ", startIndex="
				+ startIndex + ", fetchSize=" + fetchSize + ", overrideDND=" + overrideDND + ", newGrpProfileName="
				+ newGrpProfileName+ ",grpShared= "+grpShared+",sharedCorpList= "+sharedCorpList + ", ugwInterop= " + ugwInterop
				+ ", videoCallPermission=" + videoCallPermission + "]";
	}
	
}
