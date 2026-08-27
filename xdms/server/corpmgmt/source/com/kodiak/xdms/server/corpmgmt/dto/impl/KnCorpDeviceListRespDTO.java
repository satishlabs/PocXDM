/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.common.commdto.response.KnDeviceDetailsDTO;

import java.util.List;

public class KnCorpDeviceListRespDTO extends KnCorpResponseDTO{

    List<KnDeviceDetailsDTO> deviceInfoList;
    private int count;

    public List<KnDeviceDetailsDTO> getDeviceInfoList() {
        return deviceInfoList;
    }

    public void setDeviceInfoList(List<KnDeviceDetailsDTO> deviceInfoList) {
        this.deviceInfoList = deviceInfoList;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "KnCorpDeviceListRespDTO{" +
                "deviceInfoList=" + deviceInfoList +
                ", count=" + count +
                '}';
    }
}
