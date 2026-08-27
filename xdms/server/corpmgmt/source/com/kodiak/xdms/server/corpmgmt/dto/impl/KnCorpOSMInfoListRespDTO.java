/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.common.commdto.request.KnCorpOperationStatusMesssageInfoDTO;

import java.util.Set;

public class KnCorpOSMInfoListRespDTO extends KnCorpResponseDTO {

    private Set<KnCorpOperationStatusMesssageInfoDTO> OSMListInfo;

    public Set<KnCorpOperationStatusMesssageInfoDTO> getOSMListInfo() {
        return OSMListInfo;
    }

    public void setOSMListInfo(Set<KnCorpOperationStatusMesssageInfoDTO> OSMListInfo) {
        this.OSMListInfo = OSMListInfo;
    }

    @Override
    public String toString() {
        return "KnCorpOSMInfoListRespDTO{" +
                "OSMListInfo=" + OSMListInfo +
                '}';
    }
}