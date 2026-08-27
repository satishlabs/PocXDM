/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class KnMDNDetailsDTO {
	private String mdn;
	private String activeFS;
	private Long lastProfileUpdateTime;

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public String getActiveFS() {
		return activeFS;
	}

	public void setActiveFS(String activeFS) {
		this.activeFS = activeFS;
	}

	public Long getLastProfileUpdateTime() {
		return lastProfileUpdateTime;
	}

	public void setLastProfileUpdateTime(Long lastProfileUpdateTime) {
		this.lastProfileUpdateTime = lastProfileUpdateTime;
	}

}
