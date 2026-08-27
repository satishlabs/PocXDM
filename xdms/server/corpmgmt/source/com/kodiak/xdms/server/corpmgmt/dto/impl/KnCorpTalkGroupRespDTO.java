/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

public class KnCorpTalkGroupRespDTO extends KnCorpResponseDTO {
	boolean modeChange;

	public boolean isModeChange() {
		return modeChange;
	}

	public void setModeChange(boolean modeChange) {
		this.modeChange = modeChange;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnCorpTalkGroupRespDTO [modeChange=").append(modeChange).append("]");
		return builder.toString();
	}

}
