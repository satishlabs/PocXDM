/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

public class KnSubsCameraInfo implements IIdentifier {

	private static final long serialVersionUID = 7527891155244676144L;

	private String ipIdentifier;
	private String cameraSerialId;

	public KnSubsCameraInfo() {
	}

	public KnSubsCameraInfo(String ipIdentifier, String cameraSerialId) {
		this.ipIdentifier = ipIdentifier;
		this.cameraSerialId = cameraSerialId;
	}

	@Override
	public String getObjectId() {
		return null;
	}

	public String getIpIdentifier() {
		return ipIdentifier;
	}

	public void setIpIdentifier(String ipIdentifier) {
		this.ipIdentifier = ipIdentifier;
	}

	public String getCameraSerialId() {
		return cameraSerialId;
	}

	public void setCameraSerialId(String cameraSerialId) {
		this.cameraSerialId = cameraSerialId;
	}

	@Override
	public String toString() {
		return "KnSubsCameraInfo{" +
				"ipIdentifier='" + ipIdentifier + '\'' +
				", cameraSerialId='" + cameraSerialId + '\'' +
				'}';
	}

}