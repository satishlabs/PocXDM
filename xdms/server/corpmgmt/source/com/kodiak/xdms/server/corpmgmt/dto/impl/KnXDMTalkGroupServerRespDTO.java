/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpAddlTGInfoDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KnXDMTalkGroupServerRespDTO extends KnCorpResponseDTO {
	private List<KnXDMTalkGroupInfoDTO> campGrpList;
	private Collection<KnCorpAddlTGInfoDTO> addlTGList;
	private int mode;
	private int scanningEnabled;
	private Integer campModeCap;
	private int subscrProtocolversion;
	private Map<Integer, Integer> groupIDcorpIDMap;

	public List<KnXDMTalkGroupInfoDTO> getCampGrpList() {
		return campGrpList;
	}

	public void setCampGrpList(List<KnXDMTalkGroupInfoDTO> campGrpList) {
		this.campGrpList = campGrpList;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

    public int getScanningEnabled() {
        return scanningEnabled;
    }

    public void setScanningEnabled(int scanningEnabled) {
        this.scanningEnabled = scanningEnabled;
    }

	public Collection<KnCorpAddlTGInfoDTO> getAddlTGList() {
		return addlTGList;
	}

	public void setAddlTGList(Collection<KnCorpAddlTGInfoDTO> addlTGList) {
		this.addlTGList = addlTGList;
	}

	public int getSubscrProtocolversion() {
		return subscrProtocolversion;
	}

	public void setSubscrProtocolversion(int subscrProtocolversion) {
		this.subscrProtocolversion = subscrProtocolversion;
	}

	public Integer getCampModeCap() {
		return campModeCap;
	}

	public void setCampModeCap(Integer campModeCap) {
		this.campModeCap = campModeCap;
	}

	public Map<Integer, Integer> getGroupIDcorpIDMap() {
		return groupIDcorpIDMap;
	}

	public void setGroupIDcorpIDMap(Map<Integer, Integer> groupIDcorpIDMap) {
		this.groupIDcorpIDMap = groupIDcorpIDMap;
	}

	@Override
	public String toString() {
		return "KnXDMTalkGroupServerRespDTO{" +
				"campGrpList=" + campGrpList +
				", addlTGList=" + addlTGList +
				", mode=" + mode +
				", scanningEnabled=" + scanningEnabled +
				", campModeCap=" + campModeCap +
				", subscrProtocolversion=" + subscrProtocolversion +
				", groupIDcorpIDMap=" + groupIDcorpIDMap +
				'}';
	}
}
