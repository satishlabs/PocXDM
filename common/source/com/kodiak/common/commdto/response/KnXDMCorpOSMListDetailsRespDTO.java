/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

public class KnXDMCorpOSMListDetailsRespDTO extends KnXDMCorpRespDTO implements IXDMResponseDTO {

    /**
     *
     */
    private static final long serialVersionUID = 6821839968697726798L;

    private KnXDMCorpOSMInfoRespDTO OSMListDetails;

    public KnXDMCorpOSMInfoRespDTO getOSMListDetails() {
        return OSMListDetails;
    }

    public void setOSMListDetails(KnXDMCorpOSMInfoRespDTO oSMListDetails) {
        OSMListDetails = oSMListDetails;
    }

    @Override
    public String toString() {
        return "KnXDMCorpOSMListDetailsRespDTO [OSMListDetails=" + OSMListDetails + "]";
    }

}
