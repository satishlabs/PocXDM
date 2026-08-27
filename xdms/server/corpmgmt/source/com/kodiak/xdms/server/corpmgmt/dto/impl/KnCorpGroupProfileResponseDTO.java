/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupProfileInfo;

import java.util.List;
import java.util.Map;

public class KnCorpGroupProfileResponseDTO extends KnCorpResponseDTO {
private List<KnCorpGroupProfileInfo> groupProfileList;
Map<String,Integer> groupProfileCountMap;
private int totalGroupProfilesCount;


private int totalGroupProfilesUnfilteredCount;

public List<KnCorpGroupProfileInfo> getGroupProfileList() {
	return groupProfileList;
}

public void setGroupProfileList(List<KnCorpGroupProfileInfo> groupProfileList) {
	this.groupProfileList = groupProfileList;
}

public Map<String, Integer> getGroupProfileCountMap() {
	return groupProfileCountMap;
}

public void setGroupProfileCountMap(Map<String, Integer> groupProfileCountMap) {
	this.groupProfileCountMap = groupProfileCountMap;
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
	return "KnCorpGroupProfileResponseDTO [groupProfileList=" + groupProfileList + ", groupProfileCountMap="
			+ groupProfileCountMap + ", totalGroupProfilesCount="
				+ totalGroupProfilesCount + ", totalUnfilteredGroupProfilesCount="
			      + totalGroupProfilesUnfilteredCount + "]";
}


}
