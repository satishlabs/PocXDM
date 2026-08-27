/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.common.commdto.request.KnCorpOperationStatusMesssageInfoDTO;

public class KnCorpOSMInfoListDetailsRespDTO extends KnCorpResponseDTO {

    private KnCorpOperationStatusMesssageInfoDTO OSMListDetails;

    public KnCorpOperationStatusMesssageInfoDTO getOSMListDetails() {
        return OSMListDetails;
    }

    public void setOSMListDetails(KnCorpOperationStatusMesssageInfoDTO OSMListDetails) {
        this.OSMListDetails = OSMListDetails;
    }

    @Override
    public String toString() {
        return "KnCorpOSMInfoListDetailsRespDTO{" +
                "OSMListDetails=" + OSMListDetails +
                '}';
    }
}
