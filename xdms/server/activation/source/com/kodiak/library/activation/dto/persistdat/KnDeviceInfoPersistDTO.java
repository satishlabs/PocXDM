/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.library.activation.dto.persistdat;

public class KnDeviceInfoPersistDTO {

    private String deviceId;
    private Integer deviceStatus;
    private Long deviceActTimeStamp;
    private Long deviceLastUsed;
    private String deviceDigestPassword;
    private Integer deviceCreatedAs;
    private String deviceClientId;
    private Integer deviceshared;
    private String deviceIMPI;
    private Integer deviceType;
    private Integer corpId;
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(Integer deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public Long getDeviceActTimeStamp() {
        return deviceActTimeStamp;
    }

    public void setDeviceActTimeStamp(Long deviceActTimeStamp) {
        this.deviceActTimeStamp = deviceActTimeStamp;
    }

    public Long getDeviceLastUsed() {
        return deviceLastUsed;
    }

    public void setDeviceLastUsed(Long deviceLastUsed) {
        this.deviceLastUsed = deviceLastUsed;
    }

    public String getDeviceDigestPassword() {
        return deviceDigestPassword;
    }

    public void setDeviceDigestPassword(String deviceDigestPassword) {
        this.deviceDigestPassword = deviceDigestPassword;
    }

    public Integer getDeviceCreatedAs() {
		return deviceCreatedAs;
	}

	public void setDeviceCreatedAs(Integer deviceCreatedAs) {
		this.deviceCreatedAs = deviceCreatedAs;
	}

	public String getDeviceClientId() {
		return deviceClientId;
	}

	public void setDeviceClientId(String deviceClientId) {
		this.deviceClientId = deviceClientId;
	}

	public Integer getDeviceshared() {
		return deviceshared;
	}

	public void setDeviceshared(Integer deviceshared) {
		this.deviceshared = deviceshared;
	}

	public String getDeviceIMPI() {
		return deviceIMPI;
	}

	public void setDeviceIMPI(String deviceIMPI) {
		this.deviceIMPI = deviceIMPI;
	}

    public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

    public Integer getCorpId() { return corpId; }

    public void setCorpId(Integer corpId) { this.corpId = corpId; }

    @Override
    public String toString() {
		return "KnDeviceInfoPersistDTO [deviceId=" + deviceId + ", deviceStatus=" + deviceStatus
				+ ", deviceActTimeStamp=" + deviceActTimeStamp + ", deviceLastUsed=" + deviceLastUsed
				+ ", deviceDigestPassword=" + deviceDigestPassword + ", deviceCreatedAs=" + deviceCreatedAs
				+ ", deviceClientId=" + deviceClientId + ", deviceshared=" + deviceshared + ", deviceIMPI=" + deviceIMPI+ ", deviceType=" + deviceType
                + ", corpId=" + corpId
				+ "]";
    }
}
