/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kodiak.common.resources.KnGDPRTemplate;
/**
 * Created by asanjiv on 10/7/16.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class KnEXDMSGrpMemberDto {

	private String mdn;
    private String name;
    private Integer supervisor;
    private Boolean isLocWatcher;
    private Integer contactType;
    private Boolean broadcaster;
    private Integer clientType;
    private int isOSMAuthorize;
    private int callInitiatePermission;
    private int callReceivePermission;
    private int inCallPermission;
    private Integer videoCallInitiatePermission;
    private Integer videoCallReceivePermission;
    private Integer videoInCallPermission;
    private Integer memberCorpId;
    private int isAffiliationEnabled;

    public KnEXDMSGrpMemberDto(){}

    public KnEXDMSGrpMemberDto(Integer isSupervisor, Integer isBroadcaster,
                                 Integer isLocSupervisor, Integer isOSMAuthorized,
                                 Integer callInitiateAllowed, Integer callTerminateAllowed,
                                 Integer incallAllowed) {
        this.supervisor = isSupervisor;
        this.broadcaster = isBroadcaster == 1;
        this.isLocWatcher = isLocSupervisor == 1;
        this.isOSMAuthorize = isOSMAuthorized;
        this.callInitiatePermission = callInitiateAllowed;
        this.callReceivePermission = callTerminateAllowed;
        this.inCallPermission = incallAllowed;
    }


    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Integer supervisor) {
        this.supervisor = supervisor;
    }

    public Boolean getIsLocWatcher() {
        return isLocWatcher;
    }

    public void setIsLocWatcher(Boolean isLocWatcher) {
        this.isLocWatcher = isLocWatcher;
    }

    public Integer getContactType() {
        return contactType;
    }

    public void setContactType(Integer contactType) {
        this.contactType = contactType;
    }

    public Boolean getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(Boolean broadcaster) {
        this.broadcaster = broadcaster;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public int getIsOSMAuthorize() {
        return isOSMAuthorize;
    }

    public void setIsOSMAuthorize(int isOSMAuthorize) {
        this.isOSMAuthorize = isOSMAuthorize;
    }
    

    public int getCallInitiatePermission() {
		return callInitiatePermission;
	}

	public void setCallInitiatePermission(int callInitiatePermission) {
		this.callInitiatePermission = callInitiatePermission;
	}

	public int getCallReceivePermission() {
		return callReceivePermission;
	}

	public void setCallReceivePermission(int callReceivePermission) {
		this.callReceivePermission = callReceivePermission;
	}

	public int getInCallPermission() {
		return inCallPermission;
	}

	public void setInCallPermission(int inCallPermission) {
		this.inCallPermission = inCallPermission;
	}

    public Integer getMemberCorpId() {
        return memberCorpId;
    }

    public void setMemberCorpId(Integer memberCorpId) {
        this.memberCorpId = memberCorpId;
    }

    public int getIsAffiliationEnabled() { return isAffiliationEnabled; }

    public void setIsAffiliationEnabled(int isAffiliationEnabled) { this.isAffiliationEnabled = isAffiliationEnabled; }

    public Integer getVideoCallInitiatePermission() {
        return videoCallInitiatePermission;
    }

    public void setVideoCallInitiatePermission(Integer videoCallInitiatePermission) {
        this.videoCallInitiatePermission = videoCallInitiatePermission;
    }

    public Integer getVideoCallReceivePermission() {
        return videoCallReceivePermission;
    }

    public void setVideoCallReceivePermission(Integer videoCallReceivePermission) {
        this.videoCallReceivePermission = videoCallReceivePermission;
    }

    public Integer getVideoInCallPermission() {
        return videoInCallPermission;
    }

    public void setVideoInCallPermission(Integer videoInCallPermission) {
        this.videoInCallPermission = videoInCallPermission;
    }

    @Override
    public String toString() {
        return "KnEXDMSGrpMemberDto{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", name='" + KnGDPRTemplate.name(name) + '\'' +
                ", supervisor=" + supervisor +
                ", isLocWatcher=" + isLocWatcher +
                ", contactType=" + contactType +
                ", broadcaster=" + broadcaster +
                ", isOSMAuthorize=" + isOSMAuthorize +
                ", clientType=" + clientType +
                ", callInitiatePermission=" + callInitiatePermission +
                ", callReceivePermission=" + callReceivePermission +
                ", inCallPermission=" + inCallPermission +
                ", videoCallInitiatePermission=" + videoCallInitiatePermission +
                ", videoCallReceivePermission=" + videoCallReceivePermission +
                ", videoInCallPermission=" + videoInCallPermission +
                ", memberCorpId=" + memberCorpId +
                ", isAffiliationEnabled=" + isAffiliationEnabled +
                '}';
    }
}
