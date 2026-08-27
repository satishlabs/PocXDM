/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;

import java.util.Objects;

/**
 * ************************************************************************
 * <p>
 * File name:  KnCorpAddlTGInfoDTO.java
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


public class KnCorpAddlTGInfoDTO extends KnCorpGroupDTO implements Comparable<KnCorpAddlTGInfoDTO> {

    private String mdn;
    private Integer zoneId;
    private String zoneName;
    private Integer channelId;
    private Integer priority;

    public KnCorpAddlTGInfoDTO() {}

    public KnCorpAddlTGInfoDTO(int groupId) {
        super(groupId);
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(KnCorpAddlTGInfoDTO that) {
        if(this.getZoneId() != null && that.getZoneId() != null){
            return Integer.compare(this.getZoneId().compareTo(that.getZoneId()), 0);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KnCorpAddlTGInfoDTO)) return false;
        KnCorpAddlTGInfoDTO that = (KnCorpAddlTGInfoDTO) o;
        return Objects.equals(zoneId, that.zoneId) &&
                Objects.equals(channelId, that.channelId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(zoneId, channelId);
    }

    @Override
    public String toString() {
        return "KnCorpAddlTGInfoDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", zoneId=" + zoneId +
                ", zoneName='" + zoneName + '\'' +
                ", channelId=" + channelId +
                ", priority=" + priority +
                ", groupId =" + super.getGroupId() +
                '}';
    }
}
