/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
 * All Rights Reserved
 * Motorola Solutions Confidential Restricted
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import java.util.List;

/**
 * Internal response DTO for getRegions operation, used within the XDM server layer.
 * This DTO is an internal representation; it is mapped to KnXDMGetRegionsRespDTO
 * by KnXDMCorpMediator before returning to the caller. Follows the same pattern as
 * KnCorpGroupProfileResponseDTO, KnCorpGroupListRespDTO, etc.
 */
public class KnRegionsCorpRespDTO extends KnCorpResponseDTO {

    private List<String> geoCodeList;

    public List<String> getGeoCodeList() {
        return geoCodeList;
    }

    public void setGeoCodeList(List<String> geoCodeList) {
        this.geoCodeList = geoCodeList;
    }

    @Override
    public String toString() {
        return "KnRegionsCorpRespDTO{geoCodeList=" + geoCodeList + "}";
    }
}
