/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.io.Serializable;

public class KnXDMGroupListInfoDTO implements Serializable {

    private static final long serialVersionUID = 4503797433322327262L;

    private String groupId;
    private String zoneId;
    private String channelId;
    private String priority;
    private KnXDMGroupMdnInfoDTO groupMemProp;
    private String groupName;
    private String groupType;

    public String getGroupId() {
        return groupId;
    }

    public KnXDMGroupListInfoDTO setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getZoneId() {
        return zoneId;
    }

    public KnXDMGroupListInfoDTO setZoneId(String zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    public String getChannelId() {
        return channelId;
    }

    public KnXDMGroupListInfoDTO setChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public String getPriority() {
        return priority;
    }

    public KnXDMGroupListInfoDTO setPriority(String priority) {
        this.priority = priority;
        return this;
    }

    public KnXDMGroupMdnInfoDTO getGroupMemProp() {
        return groupMemProp;
    }

    public KnXDMGroupListInfoDTO setGroupMemProp(KnXDMGroupMdnInfoDTO groupMemProp) {
        this.groupMemProp = groupMemProp;
        return this;
    }

    public String getGroupName() { return groupName; }

    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getGroupType() { return groupType; }

    public void setGroupType(String groupType) { this.groupType = groupType; }

    @Override
    public String toString() {
        return "KnXDMGroupListInfoDTO{" +
                "groupId='" + groupId + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", channelId='" + channelId + '\'' +
                ", priority='" + priority + '\'' +
                ", groupMemProp=" + groupMemProp +
                ", groupName=" + groupName +
                ", groupType=" + groupType +
                '}';
    }
}
