/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

public class KnTalkGrpScanMode {
	
	Integer mode;
	Integer etag;
//	Integer scanCapability;

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public Integer getEtag() {
		return etag;
	}

	public void setEtag(Integer etag) {
		this.etag = etag;
	}

//	public Integer getScanCapability() {
//		return scanCapability;
//	}
//
//	public void setScanCapability(Integer scanCapability) {
//		this.scanCapability = scanCapability;
//	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnTalkGrpScanMode [mode=").append(mode).append(", etag=").append(etag).append("]");
		return builder.toString();
	}

}
