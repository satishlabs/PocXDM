/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.util.Set;

public class KnXDMCorpOSMListRespDTO extends KnXDMCorpRespDTO implements IXDMResponseDTO {

    /**
     *
     */
    private static final long serialVersionUID = 7497177756245499513L;

    private Set<KnXDMCorpOSMInfoRespDTO> OSMListInfo;

    public Set<KnXDMCorpOSMInfoRespDTO> getOSMListInfo() {
        return OSMListInfo;
    }

    public void setOSMListInfo(Set<KnXDMCorpOSMInfoRespDTO> oSMListInfo) {
        OSMListInfo = oSMListInfo;
    }

    @Override
    public String toString() {
        return "KnXDMCorpOSMListRespDTO [OSMListInfo=" + OSMListInfo + "]";
    }

}
