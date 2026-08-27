/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import java.util.Map;

public class KnCorpDeviceStatsRespDTO extends KnCorpResponseDTO {
    //This is the map of deviceType as key and deviceCount as value
    private Map<Integer, Integer> deviceCountByDeviceType;

    public Map<Integer, Integer> getDeviceCountByDeviceType() {
        return deviceCountByDeviceType;
    }

    public void setDeviceCountByDeviceType(Map<Integer, Integer> deviceCountByDeviceType) {
        this.deviceCountByDeviceType = deviceCountByDeviceType;
    }

    @Override
    public String toString() {
        return "KnXDMDeviceStatsRespDTO{" +
                ", deviceTypeCount=" + deviceCountByDeviceType +
                '}';
    }

}
