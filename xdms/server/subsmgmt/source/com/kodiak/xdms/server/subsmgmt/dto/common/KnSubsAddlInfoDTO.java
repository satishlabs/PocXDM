/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.common;

import java.util.List;

import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * Created by hanwar on 24-02-2017.
 */
public class KnSubsAddlInfoDTO {

    private static final long serialVersionUID = 7526471155622676272L;

    private String mdn;
    private List<String> mdns; 
    private Integer timeSlotType;
    private Integer drxValue;
    private Integer drxThreshCount;
    private Integer drxMO;
    private Integer drxMT;
    private Integer callHistoryDuration;
    private Integer dataPurgeDuration;
    private Long fixedtsStartTime;
    private Long fixedtsEndTime;
    private String tierPkgCode;
    private Integer dataPkgId;
    private Integer onBoardingMailReqd;
    private String userProfileName;

    private String recordingStatus;
    private List<String> profilemdn;
    private String pttSettingDocId;

    public String getRecordingStatus() { return recordingStatus; }

    public void setRecordingStatus(String recordingStatus) { this.recordingStatus = recordingStatus; }

    private Integer recodingStatus;

    public Integer getRecodingStatus() {
        return recodingStatus;
    }

    public void setRecodingStatus(Integer recodingStatus) {
        this.recodingStatus = recodingStatus;
    }



    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public Integer getTimeSlotType() {
        return timeSlotType;
    }

    public void setTimeSlotType(Integer timeSlotType) {
        this.timeSlotType = timeSlotType;
    }

    public Integer getDrxValue() {
        return drxValue;
    }

    public void setDrxValue(Integer drxValue) {
        this.drxValue = drxValue;
    }

    public Integer getDrxThreshCount() {
        return drxThreshCount;
    }

    public void setDrxThreshCount(Integer drxThreshCount) {
        this.drxThreshCount = drxThreshCount;
    }

    public Integer getDrxMO() {
        return drxMO;
    }

    public void setDrxMO(Integer drxMO) {
        this.drxMO = drxMO;
    }

    public Integer getDrxMT() {
        return drxMT;
    }

    public void setDrxMT(Integer drxMT) {
        this.drxMT = drxMT;
    }

    public Integer getCallHistoryDuration() {
        return callHistoryDuration;
    }

    public void setCallHistoryDuration(Integer callHistoryDuration) {
        this.callHistoryDuration = callHistoryDuration;
    }

    public Integer getDataPurgeDuration() {
        return dataPurgeDuration;
    }

    public void setDataPurgeDuration(Integer dataPurgeDuration) {
        this.dataPurgeDuration = dataPurgeDuration;
    }

    public Long getfixedtsStartTime() {
        return fixedtsStartTime;
    }

    public void setfixedtsStartTime(Long fixedtsStartTime) {
        this.fixedtsStartTime = fixedtsStartTime;
    }

    public Long getFixedtsEndTime() {
        return fixedtsEndTime;
    }

    public void setFixedtsEndTime(Long fixedtsEndTime) {
        this.fixedtsEndTime = fixedtsEndTime;
    }

    public String getTierPkgCode() {
		return tierPkgCode;
	}

	public void setTierPkgCode(String tierPkgCode) {
		this.tierPkgCode = tierPkgCode;
	}

	public Integer getDataPkgId() {
		return dataPkgId;
	}

	public void setDataPkgId(Integer dataPkgId) {
		this.dataPkgId = dataPkgId;
	}

	public List<String> getMdns() {
		return mdns;
	}

	public void setMdns(List<String> mdns) {
		this.mdns = mdns;
	}

    public Integer getOnBoardingMailReqd() {
        return onBoardingMailReqd;
    }

    public void setOnBoardingMailReqd(Integer onBoardingMailReqd) {
        this.onBoardingMailReqd = onBoardingMailReqd;
    }

    public String getUserProfileName() { return userProfileName; }

    public void setUserProfileName(String userProfileName) { this.userProfileName = userProfileName; }

    public List<String> getProfilemdn() {
        return profilemdn;
    }

    public void setProfilemdn(List<String> profilemdn) {
        this.profilemdn = profilemdn;
    }

    public String getPttSettingDocId() {return pttSettingDocId;}

    public void setPttSettingDocId(String pttSettingDocId) {
        this.pttSettingDocId = pttSettingDocId;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append("[KnSubsAddlInfoDTO --> ");
        strBuffer.append("MDN - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", timeSlotType - ").append(timeSlotType)
                .append(", drxThreshCount - ").append(drxThreshCount)
                .append(", drxValue - ").append(drxValue)
                .append(", drxMO - ").append(drxMO)
                .append(", drxMT - ").append(drxMT)
                .append(", callHistoryDuration - ").append(callHistoryDuration)
                .append(", dataPurgeDuration - ").append(dataPurgeDuration)
                .append(", fixedtsStartTime - ").append(fixedtsStartTime)
                .append(", fixedtsEndTime - ").append(fixedtsEndTime)
                .append(", tierPkgCode - ").append(tierPkgCode)
                .append(", dataPkgId - ").append(dataPkgId)
                .append(", onBoardingMailReqd - ").append(onBoardingMailReqd)
                .append(", recordingStatus - ").append(recodingStatus)
                .append(", mdns - ").append(KnGDPRTemplate.mdnList(mdns))
                .append(", mdns - ").append(KnGDPRTemplate.profileMdn(profilemdn))
                .append(", pttSettingDocId - ").append(pttSettingDocId)
                .append("]");
        return strBuffer.toString();
    }
}
