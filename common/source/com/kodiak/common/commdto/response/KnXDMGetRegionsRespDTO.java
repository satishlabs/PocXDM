/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
 * All Rights Reserved
 * Motorola Solutions Confidential Restricted
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.util.List;

/**
 * Response DTO for getRegions operation in XDM layer.
 * Extends KnXDMCorpRespDTO following the standard pattern used by all XDM corp response DTOs.
 * All responseCode/responseStatus/responseMessage fields are inherited from KnXDMCorpRespDTO.
 * Must be kept in sync with the same class in MCS Platform SDK.
 */
public class KnXDMGetRegionsRespDTO extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = -8816239020735491627L;

    private List<String> geoCodeList;

    public List<String> getGeoCodeList() {
        return geoCodeList;
    }

    public void setGeoCodeList(List<String> geoCodeList) {
        this.geoCodeList = geoCodeList;
    }

    @Override
    public String toString() {
        return "KnXDMGetRegionsRespDTO{geoCodeList=" + geoCodeList + ", " + super.toString() + "}";
    }
}
