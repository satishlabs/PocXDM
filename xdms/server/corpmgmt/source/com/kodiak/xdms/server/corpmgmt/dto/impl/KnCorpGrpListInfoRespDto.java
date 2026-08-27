/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;


import java.util.List;

public class KnCorpGrpListInfoRespDto extends KnCorpResponseDTO {

    List<KnCorpGrpBasicInfoRespDto> groupListInfo;


    public List<KnCorpGrpBasicInfoRespDto> getGroupListInfo() {
        return groupListInfo;
    }

    public void setGroupListInfo(List<KnCorpGrpBasicInfoRespDto> groupListInfo) {
        this.groupListInfo = groupListInfo;
    }

    @Override
    public String toString() {
        return "KnCorpGrpListInfoRespDto{" +
                "groupListInfo=" + groupListInfo +
                '}';
    }
}
