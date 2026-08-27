/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.common.commdto.common.KnXDMDeviceAddlInfoDTO;
import com.kodiak.common.commdto.response.KnDeviceDetailsDTO;

public class KnCorpDeviceInfoRespDTO extends KnCorpResponseDTO{

    private KnDeviceDetailsDTO deviceInfo;
    private KnXDMDeviceAddlInfoDTO deviceAddlInfo;

    public KnDeviceDetailsDTO getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(KnDeviceDetailsDTO deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public KnXDMDeviceAddlInfoDTO getDeviceAddlInfo() {
        return deviceAddlInfo;
    }

    public void setDeviceAddlInfo(KnXDMDeviceAddlInfoDTO deviceAddlInfo) {
        this.deviceAddlInfo = deviceAddlInfo;
    }

    @Override
    public String toString() {
        return "KnCorpDeviceInfoRespDTO{" +
                "deviceInfo=" + deviceInfo +
                ", deviceAddlInfo=" + deviceAddlInfo +
                '}';
    }
}
