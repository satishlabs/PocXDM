/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/*
 *  ***********************************************************************
 *  File name:  KnMcXGrpMemberPropsDto.java
 *  Subsystem:  PoCXDM
 *
 *    Name                 	    Date         	                  Release
 *    --------------------   -----------------------  -------------------------
 *    Chandrashekar HS          09/07/20, 10:36 AM                    10.0.1
 *
 *  Copyright (c) 2019 Kodiak, A Motorola Solutions Company
 *  9th floor, MFar, Manayata Tech Park,
 *  Greenheart Phase IV,Nagawara
 *  Bangalore - 560 045
 *  www.motorolasolutions.com
 *  All Rights Reserved.
 *
 * This software is the confidential and proprietary information of KodiakMotorola Solutions, Inc.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of the license agreement you entered into with Kodiak Motorola Solutions.
 *   ***********************************************************************
 */

package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kodiak.common.resources.KnGDPRTemplate;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KnMcxGrpMemberPropsDto {

    private String mdn;
    private String name;
    private Integer supervisor;
    private Boolean isLocWatcher;
    private Integer contactType;
    private Boolean broadcaster;
    private Integer clientType;
    private int isOsmAuthorized;
    private int callInitiatePermission;
    private int callReceivePermission;
    private int inCallPermission;
    private Integer videoCallInitiatePermission;
    private Integer videoCallReceivePermission;
    private Integer videoInCallPermission;

    public KnMcxGrpMemberPropsDto(){}

    public KnMcxGrpMemberPropsDto(Integer isSupervisor, Integer isBroadcaster,
                               Integer isLocSupervisor, Integer isOSMAuthorized,
                               Integer callInitiateAllowed, Integer callTerminateAllowed,
                               Integer incallAllowed,Integer videoCallInitiatePermission,
                                  Integer videoCallReceivePermission,
                                  Integer videoInCallPermission) {
        this.supervisor = isSupervisor;
        this.broadcaster = isBroadcaster == 1;
        this.isLocWatcher = isLocSupervisor == 1;
        this.isOsmAuthorized = isOSMAuthorized;
        this.callInitiatePermission = callInitiateAllowed;
        this.callReceivePermission = callTerminateAllowed;
        this.inCallPermission = incallAllowed;
        this.videoCallInitiatePermission = videoCallInitiatePermission;
        this.videoCallReceivePermission = videoCallReceivePermission;
        this.videoInCallPermission = videoInCallPermission;
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

    public int getIsOsmAuthorized() {
        return isOsmAuthorized;
    }

    public void setIsOsmAuthorized(int isOsmAuthorized) {
        this.isOsmAuthorized = isOsmAuthorized;
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
                ", isOSMAuthorize=" + isOsmAuthorized +
                ", clientType=" + clientType +
                ", callInitiatePermission=" + callInitiatePermission +
                ", callReceivePermission=" + callReceivePermission +
                ", inCallPermission=" + inCallPermission +
                ", videoCallInitiatePermission=" + videoCallInitiatePermission +
                ", videoCallReceivePermission=" + videoCallReceivePermission +
                ", videoInCallPermission=" + videoInCallPermission +
                '}';
    }
}
