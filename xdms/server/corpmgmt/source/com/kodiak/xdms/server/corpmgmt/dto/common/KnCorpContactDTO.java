/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpContactDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 19, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.kodiak.common.resources.KnConstants;

public class KnCorpContactDTO extends KnCorpSubscriberDTO {

    private int subscContactsCount;
    private int distributionType;
    private int supervisory;
    private int broadcaster;
    private int memberType;
    private int isLocWatcher;
    //Modified for 8.1.1 changes
    private int callInitiatePermission;
    private int callReceivePermission;
    private int inCallPermission;
    private int videoCallInitiatePermission;
    private int videoCallReceivePermission;
    private int videoInCallPermission;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private int isOSMAuthorize;
    private int isAffiliationEnabled;

    public int getVideoInCallPermission() {
        return videoInCallPermission;
    }

    public void setVideoInCallPermission(int videoInCallPermission) {
        this.videoInCallPermission = videoInCallPermission;
    }

    public int getVideoCallReceivePermission() {
        return videoCallReceivePermission;
    }

    public void setVideoCallReceivePermission(int videoCallReceivePermission) {
        this.videoCallReceivePermission = videoCallReceivePermission;
    }

    public int getVideoCallInitiatePermission() {
        return videoCallInitiatePermission;
    }

    public void setVideoCallInitiatePermission(int videoCallInitiatePermission) {
        this.videoCallInitiatePermission = videoCallInitiatePermission;
    }

    public int getIsAffiliationEnabled() {
		return isAffiliationEnabled;
	}

	public void setIsAffiliationEnabled(int isAffiliationEnabled) {
		this.isAffiliationEnabled = isAffiliationEnabled;
	}

	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }


    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }

    public int getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(int broadcaster) {
        this.broadcaster = broadcaster;
    }

    public KnCorpContactDTO() {

    }
    public KnCorpContactDTO(String mdn) {
        super(mdn);
    }

    public int getSubscContactsCount() {
        return subscContactsCount;
    }

    public void setSubscContactsCount(int subscContactsCount) {
        this.subscContactsCount = subscContactsCount;
    }

    public int getDistributionType() {
        return distributionType;
    }

    public void setDistributionType(int distributionType) {
        this.distributionType = distributionType;
    }


    public int getSupervisory() {
        return supervisory;
    }

    public void setSupervisory(int supervisory) {
        this.supervisory = supervisory;
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
        return isLocWatcher;
    }

    public void setLocWatcher(int isLocWatcher) {
        this.isLocWatcher = isLocWatcher;
    }

    public int getIsOSMAuthorize() {
        return isOSMAuthorize;
    }

    public void setIsOSMAuthorize(int isOSMAuthorize) {
        this.isOSMAuthorize = isOSMAuthorize;
    }

    @Override
    public String toString() {
        return "KnCorpContactDTO{" +
                "KnCorpSubscriberDTO ="+super.toString()+
                "subscContactsCount=" + subscContactsCount +
                ", distributionType=" + distributionType +
                ", supervisory=" + supervisory +
                ", broadcaster=" + broadcaster +
                ", memberType=" + memberType +
                ", isLocWatcher=" + isLocWatcher +
                ", callInitiatePermission=" + callInitiatePermission +
                ", callReceivePermission=" + callReceivePermission +
                ", inCallPermission=" + inCallPermission +
                 ", hierarchyType=" + hierarchyType +
                ", isOsmAuthorized=" + isOSMAuthorize +
                ", isAffiliationEnabled=" + isAffiliationEnabled +
                ", videoInCallPermission=" +videoInCallPermission+
                ", videoCallReceivePermission=" +videoCallReceivePermission+
                ", videoCallInitiatePermission=" +videoCallInitiatePermission+
                '}';
    }
}