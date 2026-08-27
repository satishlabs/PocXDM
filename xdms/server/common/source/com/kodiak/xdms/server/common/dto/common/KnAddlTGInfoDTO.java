/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;
/**
 * Created by Vinutha on 11/02/25.
 */

public class KnAddlTGInfoDTO {
    private int groupId;
    private String mdn;
    private Integer zoneId;
    private String zoneName;
    private Integer channelId;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
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

    @Override
    public String toString() {
        return "KnCorpAddlTGInfoDTO{" +
                "groupId=" + groupId +
                ", mdn='" + mdn + '\'' +
                ", zoneId=" + zoneId +
                ", zoneName='" + zoneName + '\'' +
                ", channelId=" + channelId +
                '}';
    }
}
