/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.util.List;

public class KnXDMCorpOSMGroupListRespDTO extends KnXDMCorpRespDTO implements IXDMResponseDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2208598349284562213L;

	private List<KnXDMCorpOSMGroupList> OSMIdListMap;

	public List<KnXDMCorpOSMGroupList> getOSMIdListMap() {
		return OSMIdListMap;
	}

	public void setOSMIdListMap(List<KnXDMCorpOSMGroupList> OSMIdListMap) {
		this.OSMIdListMap = OSMIdListMap;
	}

	@Override
	public String toString() {
		return "KnXDMCorpOSMGroupListRespDTO [OSMIdListMap=" + OSMIdListMap + "]";
	}

}
