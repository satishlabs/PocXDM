/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.util.Map;

public class KnXDMDevStatsRespDTO extends KnXDMCorpRespDTO {
    private static final long serialVersionUID = -3388047771821246609L;
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
