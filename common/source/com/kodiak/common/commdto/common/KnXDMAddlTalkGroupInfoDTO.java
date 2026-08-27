/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.io.Serializable;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMAddlTalkGroupInfoDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             April 26, 2018                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnXDMAddlTalkGroupInfoDTO implements Serializable {

    private static final long serialVersionUID = 8965996681123741538L;

    private String mdn;
    private Integer groupId;
    private Integer zoneId;
    private String zoneName;
    private Integer channelId;
    private String groupUri;
    private Integer priority;
    private Integer groupType;
    private Integer avatar;
    private Integer memberCount;
    private Integer createdBy;
    private String OSMListId;
    private Integer videoPermission;

    private Integer mcxGroupInd;
    private String zones;
    private String channels;

    public Integer getMcxGroupInd() {
        return mcxGroupInd;
    }

    public void setMcxGroupInd(Integer mcxGroupInd) {
        this.mcxGroupInd = mcxGroupInd;
    }
    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getGroupUri() {
        return groupUri;
    }

    public void setGroupUri(String groupUri) {
        this.groupUri = groupUri;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    public Integer getAvatar() {
        return avatar;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getOSMListId() {
        return OSMListId;
    }

    public void setOSMListId(String OSMListId) {
        this.OSMListId = OSMListId;
    }

    public String getZones() {
        return zones;
    }

    public void setZones(String zones) {
        this.zones = zones;
    }

    public String getChannels() {
        return channels;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public Integer getVideoPermission() {
        return videoPermission;
    }

    public void setVideoPermission(Integer videoPermission) {
        this.videoPermission = videoPermission;
    }

    @Override
    public String toString() {
        return "KnXDMAddlTalkGroupInfoDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", groupId=" + groupId +
                ", zoneId=" + zoneId +
                ", zoneName='" + zoneName + '\'' +
                ", channelId=" + channelId +
                ", groupUri='" + groupUri + '\'' +
                ", priority=" + priority +
                ", groupType=" + groupType +
                ", avatar=" + avatar +
                ", memberCount=" + memberCount +
                ", createdBy=" + createdBy +
                ", OSMListId='" + OSMListId + '\'' +
                ", mcxGroupInd='" + mcxGroupInd + '\'' +
                ", zones='" + zones + '\'' +
                ", channels='" + channels + '\'' +
                ", videoPermission=" + videoPermission +
                '}';
    }
}
