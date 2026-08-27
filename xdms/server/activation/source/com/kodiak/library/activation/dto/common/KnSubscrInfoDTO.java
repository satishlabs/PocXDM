/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.library.activation.dto.common;

public class KnSubscrInfoDTO {

    private String mdn;
    private Integer clientType;
    private Integer licenseType;
    private String imei;
    private String subsFS2;
    private Integer corpId;


    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public Integer getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(Integer licenseType) {
        this.licenseType = licenseType;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

	public String getSubsFS2() {
		return subsFS2;
	}

	public void setSubsFS2(String subsFS2) {
		this.subsFS2 = subsFS2;
	}

    public Integer getCorpId() { return corpId; }

    public void setCorpId(Integer corpId) { this.corpId = corpId; }
}
