/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.persistdat;

import java.util.List;

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
    private int deviceType;
    private String deviceName;
    private String reqDeviceId;
    private Integer corpId;
	private String deviceSubscriberMdn;
    private List<String> deviceIdList;

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

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getReqDeviceId() {
		return reqDeviceId;
	}

	public void setReqDeviceId(String reqDeviceId) {
		this.reqDeviceId = reqDeviceId;
	}

	public Integer getCorpId() {
		return corpId;
	}

	public void setCorpId(Integer corpId) {
		this.corpId = corpId;
	}

	public String getDeviceSubscriberMdn() {
		return deviceSubscriberMdn;
	}

	public void setDeviceSubscriberMdn(String deviceSubscriberMdn) {
		this.deviceSubscriberMdn = deviceSubscriberMdn;
	}

    public List<String> getDeviceIdList() {
        return deviceIdList;
    }

    public void setDeviceIdList(List<String> deviceIdList) {
        this.deviceIdList = deviceIdList;
    }

    @Override
	public String toString() {
		return "KnDeviceInfoPersistDTO [deviceId=" + deviceId + ", deviceStatus=" + deviceStatus
				+ ", deviceActTimeStamp=" + deviceActTimeStamp + ", deviceLastUsed=" + deviceLastUsed
				+ ", deviceDigestPassword=" + deviceDigestPassword + ", deviceCreatedAs=" + deviceCreatedAs
				+ ", deviceClientId=" + deviceClientId + ", deviceshared=" + deviceshared + ", deviceIMPI=" + deviceIMPI
				+ ", deviceType=" + deviceType+ ", deviceName=" + deviceName+ ", reqDeviceId=" + reqDeviceId+ ", corpId=" + corpId
				+ ", deviceSubscriberMdn=" + deviceSubscriberMdn + "]";
	}

	
}
