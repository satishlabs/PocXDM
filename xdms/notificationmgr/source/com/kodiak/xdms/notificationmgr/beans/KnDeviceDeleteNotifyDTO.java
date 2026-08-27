/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.notificationmgr.beans;

public class KnDeviceDeleteNotifyDTO extends KnCommonNotifyDTO {
	private static final long serialVersionUID = 2420766007434579381L;
	private String deviceIMPI;
	public String getDeviceIMPI() {
		return deviceIMPI;
	}
	public void setDeviceIMPI(String deviceIMPI) {
		this.deviceIMPI = deviceIMPI;
	}
	@Override
	public String toString() {
		return "KnDeviceDeleteNotifyDTO [deviceIMPI=" + deviceIMPI + "]";
	}
	

}
