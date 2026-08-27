/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
//package com.kodiak.xdms.server.corpmgmt.dto.impl;
package com.kodiak.common.commdto.response;
import com.kodiak.common.dto.IIdentifier;

public class KnCORPGroupStatsRespDTO implements IIdentifier
{
    private static final long serialVersionUID = -7079513242016063752L;

    private String groupType;

    private KnCorpGroupStatsDTO groupStats;

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public KnCorpGroupStatsDTO getGroupStats() {
        return groupStats;
    }

    public void setGroupStats(KnCorpGroupStatsDTO groupStats) {
        this.groupStats = groupStats;
    }

    @Override
    public String toString() {
        return "KnCORPGroupStatsRespDTO{" +
                "groupType='" + groupType + '\'' +
                ", groupStats=" + groupStats +
                '}';
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
