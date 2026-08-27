/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import java.util.List;

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;

public class KnXDMTalkGroupRespServerDTO extends KnCorpResponseDTO {

	List<KnXDMTalkGroupInfoDTO> campGrpList;

	public List<KnXDMTalkGroupInfoDTO> getCampGrpList() {
		return campGrpList;
	}

	public void setCampGrpList(List<KnXDMTalkGroupInfoDTO> campGrpList) {
		this.campGrpList = campGrpList;
	}

}
