/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
//package com.kodiak.xdms.server.corpmgmt.dto.common;
package com.kodiak.common.commdto.response;


import java.util.List;

public class KnXDMGroupStatsRespDTO extends KnXDMCorpRespDTO {
    private static final long serialVersionUID = 5962370194401081816L;

    private String groupType;

    private List<KnCORPGroupStatsRespDTO> groupStats;

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public List<KnCORPGroupStatsRespDTO> getGroupStats() {
        return groupStats;
    }

    public void setGroupStats(List<KnCORPGroupStatsRespDTO> groupStats) {
        this.groupStats = groupStats;
    }

    @Override
    public String toString() {
        return "KnXDMGroupStatsRespDTO{" +
                "groupType='" + groupType + '\'' +
                ", groupStats=" + groupStats +
                '}';
    }
}
