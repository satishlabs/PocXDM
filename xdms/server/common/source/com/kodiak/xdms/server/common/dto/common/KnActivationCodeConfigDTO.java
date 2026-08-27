/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

/**
 * Created by Deepak on 28/10/14.
 */
public class KnActivationCodeConfigDTO {

    private String pttServerId;
    private int clientType;
    private int actCodeLength;
    private int actCodeType;
    private int actCodeValidity;
    private int actCodeExpiry;
    private int intf;

    public int getIntf() {
		return intf;
	}

	public void setIntf(int intf) {
		this.intf = intf;
	}

	public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getActCodeLength() {
        return actCodeLength;
    }

    public void setActCodeLength(int actCodeLength) {
        this.actCodeLength = actCodeLength;
    }

    public int getActCodeType() {
        return actCodeType;
    }

    public void setActCodeType(int actCodeType) {
        this.actCodeType = actCodeType;
    }

    public int getActCodeValidity() {
        return actCodeValidity;
    }

    public void setActCodeValidity(int actCodeValidity) {
        this.actCodeValidity = actCodeValidity;
    }

    public int getActCodeExpiry() {
        return actCodeExpiry;
    }

    public void setActCodeExpiry(int actCodeExpiry) {
        this.actCodeExpiry = actCodeExpiry;
    }
}
