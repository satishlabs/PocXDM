/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMGroupMdnInfoDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 14, 2011           7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
public class KnXDMGroupMdnInfoDTO extends KnXDMMdnInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676132L;

    private int supervisor;
    private int broadcaster;
    private int locWatcher;
    //Modified for 8.1.1 changes
    private int callInitiatePermission;
    private int callReceivePermission;
    private int inCallPermission;
    private int videoCallInitiatePermission;
    private int videoCallReceivePermission;
    private int videoInCallPermission;
    private Integer grpModifyPerm;
    private int isOSMAuthorize;


    public int getVideoCallInitiatePermission() {
        return videoCallInitiatePermission;
    }

    public void setVideoCallInitiatePermission(int videoCallInitiatePermission) {
        this.videoCallInitiatePermission = videoCallInitiatePermission;
    }

    public int getVideoCallReceivePermission() {
        return videoCallReceivePermission;
    }

    public void setVideoCallReceivePermission(int videoCallReceivePermission) {
        this.videoCallReceivePermission = videoCallReceivePermission;
    }

    public int getVideoInCallPermission() {
        return videoInCallPermission;
    }

    public void setVideoInCallPermission(int videoInCallPermission) {
        this.videoInCallPermission = videoInCallPermission;
    }

    public Integer getGrpModifyPerm() {
        return grpModifyPerm;
    }

    public void setGrpModifyPerm(Integer grpModifyPerm) {
        this.grpModifyPerm = grpModifyPerm;
    }

    public int getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(int broadcaster) {
        this.broadcaster = broadcaster;
    }

    public int getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(int supervisor) {
        this.supervisor = supervisor;
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

    public int getLocWatcher() {
        return locWatcher;
    }

    public void setLocWatcher(int locWatcher) {
        this.locWatcher = locWatcher;
    }

    public int getIsOSMAuthorize() {
        return isOSMAuthorize;
    }

    public void setIsOSMAuthorize(int isOSMAuthorize) {
        this.isOSMAuthorize = isOSMAuthorize;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(super.toString());
        strBuffer.append("supervisor - ").append(supervisor);
        strBuffer.append("broadcaster - ").append(broadcaster);
        strBuffer.append("callInitiatePermission - ").append(callInitiatePermission);
        strBuffer.append("callReceivePermission - ").append(callReceivePermission);
        strBuffer.append("inCallPermission - ").append(inCallPermission);
        strBuffer.append("videoCallInitiatePermission - ").append(videoCallInitiatePermission);
        strBuffer.append("videoCallReceivePermission - ").append(videoCallReceivePermission);
        strBuffer.append("videoInCallPermission - ").append(videoInCallPermission);
        strBuffer.append("locWatcher - ").append(locWatcher);
        strBuffer.append("grpModifyPerm - ").append(grpModifyPerm);
        strBuffer.append("isOsmAuthorized - ").append(isOSMAuthorize);
        return strBuffer.toString();
    }
}
