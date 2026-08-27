/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.common.commdto.response.KnXDMCorpOSMGroupList;

import java.util.List;

public class KnCorpOSMGroupListRespDTO extends KnCorpResponseDTO {

    private List<KnXDMCorpOSMGroupList> OSMIdListMap;

    public List<KnXDMCorpOSMGroupList> getOSMIdListMap() {
        return OSMIdListMap;
    }

    public void setOSMIdListMap(List<KnXDMCorpOSMGroupList> OSMIdListMap) {
        this.OSMIdListMap = OSMIdListMap;
    }

    @Override
    public String toString() {
        return "KnCorpOSMGroupListResponse [OSMIdListMap=" + OSMIdListMap + "]";
    }

}
