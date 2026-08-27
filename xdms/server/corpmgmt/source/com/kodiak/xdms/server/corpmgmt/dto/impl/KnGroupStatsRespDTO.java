/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.common.commdto.response.KnCORPGroupStatsRespDTO;

import java.util.List;

public class KnGroupStatsRespDTO extends KnCorpResponseDTO{
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
}
