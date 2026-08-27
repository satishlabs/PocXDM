/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import java.util.List;
import java.util.Map;

public class KnIPCorpBulkGroupDTO extends KnIPCorpGroupProfileDTO {

    private static final long serialVersionUID = -5638847421386209067L;
    private List<String> grpNameList;
    private List<Integer> grpIdList;
    private Map<Integer, KnIPCorpGroupInfoDTO> ipCorpGroupInfoDTOMap;


    public List<String> getGrpNameList() {
        return grpNameList;
    }

    public void setGrpNameList(List<String> grpNameList) {
        this.grpNameList = grpNameList;
    }

    public List<Integer> getGrpIdList() {
        return grpIdList;
    }

    public void setGrpIdList(List<Integer> grpIdList) {
        this.grpIdList = grpIdList;
    }

    public Map<Integer, KnIPCorpGroupInfoDTO> getIpCorpGroupInfoDTOMap() {
        return ipCorpGroupInfoDTOMap;
    }

    public void setIpCorpGroupInfoDTOMap(Map<Integer, KnIPCorpGroupInfoDTO> ipCorpGroupInfoDTOMap) {
        this.ipCorpGroupInfoDTOMap = ipCorpGroupInfoDTOMap;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        //sb.append(super.toString());
        sb.append(super.toString())
                .append(", grpNameList - ").append(grpNameList)
                .append(", grpIdList - ").append(grpIdList)
                .append(", ipCorpGroupInfoDTOMap - ").append(ipCorpGroupInfoDTOMap == null ? null : ipCorpGroupInfoDTOMap.entrySet());
        return sb.toString();
    }
}
