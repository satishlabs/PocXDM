/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.util.List;

public class KnXDMDeviceListRespDTO extends KnXDMCorpRespDTO{

    private static final long serialVersionUID = 5945873252128983201L;

    private List<KnDeviceDetailsDTO> deviceInfoList;
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
        return "KnXDMDeviceListRespDTO{" +
                "deviceInfoList=" + deviceInfoList +
                ", count=" + count +
                '}';
    }
}
