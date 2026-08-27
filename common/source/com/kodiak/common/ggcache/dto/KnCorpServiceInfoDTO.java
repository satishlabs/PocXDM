/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dto;

/**
 * *****************************************************************************
 * File name:   KnCorpServiceInfoDTO
 * Subsystem:   Utility
 * Description: DTO to store Corp-ID to ServiceType/ServiceVersion mapping
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

public class KnCorpServiceInfoDTO  {

    private Integer corpId;
    private String serviceType;
    private Integer serviceGrpId;
    private long lastUpdateTime;

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getServiceGrpId() {
        return serviceGrpId;
    }

    public void setServiceGrpId(Integer serviceGrpId) {
        this.serviceGrpId = serviceGrpId;
    }

    @Override
    public String toString() {
        return "KnCorpServiceInfoDTO{" +
                "corpId='" + corpId + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", serviceGrpId='" + serviceGrpId + '\'' +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}
