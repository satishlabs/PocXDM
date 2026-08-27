/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.clientdat;

import com.kodiak.common.dto.IIdentifier;

import java.util.List;

public class KnDeleteDeviceDTO implements IIdentifier{
	private static final long serialVersionUID = 8975899881246414494L;
	private String pocHome;
    private String  deviceIMPI;
    private List<String> deviceIMPIList;
    
	public String getPocHome() {
		return pocHome;
	}

	public void setPocHome(String pocHome) {
		this.pocHome = pocHome;
	}

	public String getDeviceIMPI() {
		return deviceIMPI;
	}

	public void setDeviceIMPI(String deviceIMPI) {
		this.deviceIMPI = deviceIMPI;
	}

    public List<String> getDeviceIMPIList() {
        return deviceIMPIList;
    }

    public void setDeviceIMPIList(List<String> deviceIMPIList) {
        this.deviceIMPIList = deviceIMPIList;
    }

    @Override
	public String toString() {
		return "KnDeleteDeviceDTO [pocHome=" + pocHome + ", deviceIMPI=" + deviceIMPI + "]";
	}

	@Override
	public String getObjectId() {
		// TODO Auto-generated method stub
		return null;
	}

}
