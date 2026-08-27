/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.persistdat;

public class KnDeviceImpiInfoPersistDTO {
	private String deviceImpi ;
	private String deviceImpu ;
	public String getDeviceImpi() {
		return deviceImpi;
	}
	public void setDeviceImpi(String deviceImpi) {
		this.deviceImpi = deviceImpi;
	}
	public String getDeviceImpu() {
		return deviceImpu;
	}
	public void setDeviceImpu(String deviceImpu) {
		this.deviceImpu = deviceImpu;
	}
	
	@Override
	public String toString() {
		return "KnDeviceImpiInfoPersistDTO [deviceImpi=" + deviceImpi + ", deviceImpu=" + deviceImpu + "]";
	}
	
}
