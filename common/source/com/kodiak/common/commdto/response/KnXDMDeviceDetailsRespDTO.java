/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMDeviceAddlInfoDTO;

public class KnXDMDeviceDetailsRespDTO extends KnXDMCorpRespDTO{

    private static final long serialVersionUID = 8688002782072875371L;
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
}
