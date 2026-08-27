/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * 
 * ***********************************************************************
 *  File name: KnXDMDeviceProvDTO.java
 * 
 *  Name                 	Date         	   Release
 *  -------------------- -------------------- -------------------------------
 *  Kumar Abhinav        02-Jan-2020, 1:10:44 am      10.0
 *
 *  Copyright (c) 2019 Kodiak, A Motorola Solutions Company
 *  9th floor, MFar, Manayata Tech Park,
 *  Greenheart Phase IV,Nagawara
 *  Bangalore - 560 045
 *  www.kodiakptt.com
 *  All Rights Reserved.
 *
 *  This software is the confidential and proprietary information of Kodiak
 *  Networks, Inc. You shall not disclose such confidential information and
 *  shall use it only in accordance with the terms of the license agreement
 *  you entered into with Kodiak Networks.
 * ***********************************************************************
 */
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

public class KnXDMDeviceProvDTO implements IIdentifier{
	private static final long serialVersionUID = 7526471155622678951L;
	private String deviceId;
	private String accountId;
	private int deviceType;
	private String devicePassword;
	private String deviceIMPI;
	private String deviceIMPU;
	private Integer deviceShared;
	private String deviceName;
	private String deviceClientId;
    private Integer deviceStatus;
    private Long deviceActTimeStamp;
    private Long deviceLastUsed;
    private Integer deviceCreatedAs;
    private String reqDeviceId;
    private Integer corpId;
    private String deviceSubscrMdn;
    private KnXDMDeviceAddlInfoDTO deviceAddlInfo;

	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public String getDevicePassword() {
		return devicePassword;
	}
	public void setDevicePassword(String devicePassword) {
		this.devicePassword = devicePassword;
	}
	public String getDeviceIMPI() {
		return deviceIMPI;
	}
	public void setDeviceIMPI(String deviceIMPI) {
		this.deviceIMPI = deviceIMPI;
	}
	public String getDeviceIMPU() {
		return deviceIMPU;
	}
	public void setDeviceIMPU(String deviceIMPU) {
		this.deviceIMPU = deviceIMPU;
	}
	
	public Integer getDeviceShared() {
		return deviceShared;
	}
	public void setDeviceShared(Integer deviceShared) {
		this.deviceShared = deviceShared;
	}
	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceClientId() {
		return deviceClientId;
	}

	public void setDeviceClientId(String deviceClientId) {
		this.deviceClientId = deviceClientId;
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
	
	public Integer getDeviceCreatedAs() {
		return deviceCreatedAs;
	}
	public void setDeviceCreatedAs(Integer deviceCreatedAs) {
		this.deviceCreatedAs = deviceCreatedAs;
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

	public String getDeviceSubscrMdn() {
		return deviceSubscrMdn;
	}

	public void setDeviceSubscrMdn(String deviceSubscrMdn) {
		this.deviceSubscrMdn = deviceSubscrMdn;
	}

	public KnXDMDeviceAddlInfoDTO getDeviceAddlInfo() {
		return deviceAddlInfo;
	}

	public void setDeviceAddlInfo(KnXDMDeviceAddlInfoDTO deviceAddlInfo) {
		this.deviceAddlInfo = deviceAddlInfo;
	}

	@Override
	public String getObjectId() {
		return deviceId;
	}


	@Override
	public String toString() {
		return "KnXDMDeviceProvDTO [deviceId=" + deviceId + ", accountId=" + accountId + ", deviceType=" + deviceType
				+ ", devicePassword=" + devicePassword + ", deviceIMPI=" + deviceIMPI + ", deviceIMPU=" + deviceIMPU
				+ ", deviceShared=" + deviceShared + ", deviceName=" + deviceName + ", deviceClientId=" + deviceClientId
				+ ", deviceStatus=" + deviceStatus + ", deviceActTimeStamp=" + deviceActTimeStamp + ", deviceLastUsed="
				+ deviceLastUsed + ", deviceCreatedAs=" + deviceCreatedAs + ", reqDeviceId=" + reqDeviceId + ", corpId="
				+ corpId + ",deviceSubscriberMdn=" + deviceSubscrMdn +", deviceAdditionalInfo=" + deviceAddlInfo + " ]";
	}
	
	
}
