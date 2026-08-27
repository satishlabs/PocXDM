/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMCorpGroupProfileDTO;

import java.util.List;

public class KnXDMCorpGroupProfileResponseDTO extends KnXDMCorpRespDTO {

	private static final long serialVersionUID = 737283804477524386L;
	private String corpId;
	List<KnXDMCorpGroupProfileDTO> groupProfile;
	private int totalGroupProfilesCount;
	private int totalGroupProfilesUnfilteredCount;

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public List<KnXDMCorpGroupProfileDTO> getGroupProfile() {
		return groupProfile;
	}

	public void setGroupProfile(List<KnXDMCorpGroupProfileDTO> groupProfile) {
		this.groupProfile = groupProfile;
	}

	public int getTotalGroupProfilesCount() { return totalGroupProfilesCount; }

	public void setTotalGroupProfilesCount(int totalGroupProfilesCount) { this.totalGroupProfilesCount = totalGroupProfilesCount; }

	public int getTotalGroupProfilesUnfilteredCount() {
		return totalGroupProfilesUnfilteredCount;
	}

	public void setTotalGroupProfilesUnfilteredCount(int totalGroupProfilesUnfilteredCount) {
		this.totalGroupProfilesUnfilteredCount = totalGroupProfilesUnfilteredCount;
	}

	@Override
	public String toString() {
		return "KnXDMCorpGroupProfileResponseDTO [corpId=" + corpId + ", groupProfile=" + groupProfile +  ", totalGroupProfilesCount=" + totalGroupProfilesCount + ", totalUnfilteredGroupProfilesCount=" + totalGroupProfilesUnfilteredCount + "]";
	}

	

}
