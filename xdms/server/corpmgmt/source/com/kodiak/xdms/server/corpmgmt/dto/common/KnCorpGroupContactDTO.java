/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

public class KnCorpGroupContactDTO {

    private Integer isSupervisor;
    private Integer isBroadcaster;
    private Integer isLocSupervisor;
    private Integer isOSMAuthorized;
    private Integer callInitiateAllowed;
    private Integer callTerminateAllowed;
    private Integer IncallAllowed;
    private Integer videoCallInitiateAllowed;
    private Integer videoCallReceiveAllowed;
    private Integer videoInCallAllowed;

    public KnCorpGroupContactDTO(){}
    public KnCorpGroupContactDTO(Integer isSupervisor, Integer isBroadcaster,
                                 Integer isLocSupervisor, Integer isOSMAuthorized,
                                 Integer callInitiateAllowed, Integer callTerminateAllowed,
                                 Integer incallAllowed,Integer videoCallInitiateAllowed, Integer videoCallReceiveAllowed,
                                 Integer videoInCallAllowed) {
        this.isSupervisor = isSupervisor;
        this.isBroadcaster = isBroadcaster;
        this.isLocSupervisor = isLocSupervisor;
        this.isOSMAuthorized = isOSMAuthorized;
        this.callInitiateAllowed = callInitiateAllowed;
        this.callTerminateAllowed = callTerminateAllowed;
        this.videoCallInitiateAllowed = videoCallInitiateAllowed;
        this.videoCallReceiveAllowed = videoCallReceiveAllowed;
        this.videoInCallAllowed = videoInCallAllowed;
        IncallAllowed = incallAllowed;
    }

    public Integer getVideoCallInitiateAllowed() {
        return videoCallInitiateAllowed;
    }

    public void setVideoCallInitiateAllowed(Integer videoCallInitiateAllowed) {
        this.videoCallInitiateAllowed = videoCallInitiateAllowed;
    }

    public Integer getVideoCallReceiveAllowed() {
        return videoCallReceiveAllowed;
    }

    public void setVideoCallReceiveAllowed(Integer videoCallReceiveAllowed) {
        this.videoCallReceiveAllowed = videoCallReceiveAllowed;
    }

    public Integer getVideoInCallAllowed() {
        return videoInCallAllowed;
    }

    public void setVideoInCallAllowed(Integer videoInCallAllowed) {
        this.videoInCallAllowed = videoInCallAllowed;
    }

    public Integer getIsSupervisor() {
        return isSupervisor;
    }

    public void setIsSupervisor(Integer isSupervisor) {
        this.isSupervisor = isSupervisor;
    }

    public Integer getIsBroadcaster() {
        return isBroadcaster;
    }

    public void setIsBroadcaster(Integer isBroadcaster) {
        this.isBroadcaster = isBroadcaster;
    }

    public Integer getIsLocSupervisor() {
        return isLocSupervisor;
    }

    public void setIsLocSupervisor(Integer isLocSupervisor) {
        this.isLocSupervisor = isLocSupervisor;
    }

    public Integer getIsOSMAuthorized() {
        return isOSMAuthorized;
    }

    public void setIsOSMAuthorized(Integer isOSMAuthorized) {
        this.isOSMAuthorized = isOSMAuthorized;
    }

    public Integer getCallInitiateAllowed() {
        return callInitiateAllowed;
    }

    public void setCallInitiateAllowed(Integer callInitiateAllowed) {
        this.callInitiateAllowed = callInitiateAllowed;
    }

    public Integer getCallTerminateAllowed() {
        return callTerminateAllowed;
    }

    public void setCallTerminateAllowed(Integer callTerminateAllowed) {
        this.callTerminateAllowed = callTerminateAllowed;
    }

    public Integer getIncallAllowed() {
        return IncallAllowed;
    }

    public void setIncallAllowed(Integer incallAllowed) {
        IncallAllowed = incallAllowed;
    }

    @Override
    public String toString() {
        return "KnCorpGroupContactDTO{" +
                "isSupervisor=" + isSupervisor +
                ", isBroadcaster=" + isBroadcaster +
                ", isLocSupervisor=" + isLocSupervisor +
                ", isOSMAuthorized=" + isOSMAuthorized +
                ", callInitiateAllowed=" + callInitiateAllowed +
                ", callTerminateAllowed=" + callTerminateAllowed +
                ", IncallAllowed=" + IncallAllowed +
                ", videoCallInitiateAllowed=" + videoCallInitiateAllowed +
                ", videoCallReceiveAllowed=" + videoCallReceiveAllowed +
                ", videoInCallAllowed=" + videoInCallAllowed +
                '}';
    }
}
