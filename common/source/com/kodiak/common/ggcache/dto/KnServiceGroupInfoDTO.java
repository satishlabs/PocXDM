/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dto;

/**
 * *****************************************************************************
 * File name:   KnServiceGroupInfoDTO
 * Subsystem:   Utility
 * Description: DTO to store available traffic group's info in system
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Saurabh Kumar           16/05/18        9.0
 * <p/>
 * <p/>
 * Copyright (c) 2018  Kodiak , A Motorola Solutions Company
 * 9th floor, MFar, Manayata Tech Park,
 * Greenheart Phase IV, Nagawara,
 * Bengaluru, Karnataka 560045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak,A Motorola Solutions Company
 * You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak,A Motorola Solutions Company.
 * *******************************************************************************
 */

public class KnServiceGroupInfoDTO  {

    private Integer serviceGrpId;
    private String serviceType;
    private String serviceVer;
    private Integer isDefault;
    private long lastUpdateTime;

    public Integer getServiceGrpId() {
        return serviceGrpId;
    }

    public void setServiceGrpId(Integer serviceGrpId) {
        this.serviceGrpId = serviceGrpId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceVer() {
        return serviceVer;
    }

    public void setServiceVer(String serviceVer) {
        this.serviceVer = serviceVer;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "KnServiceGroupInfoDTO{" +
                "serviceGrpId=" + serviceGrpId +
                ", serviceType='" + serviceType + '\'' +
                ", serviceVer='" + serviceVer + '\'' +
                ", isDefault=" + isDefault +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}
