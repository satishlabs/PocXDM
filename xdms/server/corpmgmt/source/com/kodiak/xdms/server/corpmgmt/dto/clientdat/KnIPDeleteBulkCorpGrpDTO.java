/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import java.util.List;

public class KnIPDeleteBulkCorpGrpDTO extends KnIPCorpInfoDTO {

    private String grpProfileName;
    private Integer grpProfileId;
    private List<Integer> groupIdList;

    public String getGrpProfileName() {
        return grpProfileName;
    }

    public void setGrpProfileName(String grpProfileName) {
        this.grpProfileName = grpProfileName;
    }

    public Integer getGrpProfileId() {
        return grpProfileId;
    }

    public void setGrpProfileId(Integer grpProfileId) {
        this.grpProfileId = grpProfileId;
    }

    public List<Integer> getGroupIdList() {
        return groupIdList;
    }

    public void setGroupIdList(List<Integer> groupIdList) {
        this.groupIdList = groupIdList;
    }
}
