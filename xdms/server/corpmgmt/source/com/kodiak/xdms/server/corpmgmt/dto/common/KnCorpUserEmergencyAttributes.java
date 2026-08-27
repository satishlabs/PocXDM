/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * ************************************************************************
 * <p>
 * File name:  KnCorpUserEmergencyAttributes.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Jan 19, 2017                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnCorpUserEmergencyAttributes {
    private String mdn;
    private Integer emergInitPermission;
    private Integer emergCallType;
    private Integer emergCancelPermission;
    private Integer emergOriginBitSet;
    private Integer emergTermBitSet;
    private Integer emergLMRBehavior;
    private Integer emergDestType;
    private String priDestination;
    private String secDestination;
    private String emergConfigTimer;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public Integer getEmergInitPermission() {
        return emergInitPermission;
    }

    public void setEmergInitPermission(Integer emergInitPermission) {
        this.emergInitPermission = emergInitPermission;
    }

    public Integer getEmergCallType() {
        return emergCallType;
    }

    public void setEmergCallType(Integer emergCallType) {
        this.emergCallType = emergCallType;
    }

    public Integer getEmergCancelPermission() {
        return emergCancelPermission;
    }

    public void setEmergCancelPermission(Integer emergCancelPermission) {
        this.emergCancelPermission = emergCancelPermission;
    }

    public Integer getEmergOriginBitSet() {
        return emergOriginBitSet;
    }

    public void setEmergOriginBitSet(Integer emergOriginBitSet) {
        this.emergOriginBitSet = emergOriginBitSet;
    }

    public Integer getEmergTermBitSet() {
        return emergTermBitSet;
    }

    public void setEmergTermBitSet(Integer emergTermBitSet) {
        this.emergTermBitSet = emergTermBitSet;
    }

    public Integer getEmergLMRBehavior() {
        return emergLMRBehavior;
    }

    public void setEmergLMRBehavior(Integer emergLMRBehavior) {
        this.emergLMRBehavior = emergLMRBehavior;
    }

    public Integer getEmergDestType() {
        return emergDestType;
    }

    public void setEmergDestType(Integer emergDestType) {
        this.emergDestType = emergDestType;
    }

    public String getPriDestination() {
        return priDestination;
    }

    public void setPriDestination(String priDestination) {
        this.priDestination = priDestination;
    }

    public String getSecDestination() {
        return secDestination;
    }

    public void setSecDestination(String secDestination) {
        this.secDestination = secDestination;
    }

    public String getEmergConfigTimer() {
        return emergConfigTimer;
    }

    public void setEmergConfigTimer(String emergConfigTimer) {
        this.emergConfigTimer = emergConfigTimer;
    }

	@Override
    public String toString() {
        return "KnCorpUserEmergencyAttributes{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", emergInitPermission=" + emergInitPermission +
                ", emergCallType=" + emergCallType +
                ", emergCancelPermission=" + emergCancelPermission +
                ", emergOriginBitSet=" + emergOriginBitSet +
                ", emergTermBitSet=" + emergTermBitSet +
                ", emergLMRBehavior=" + emergLMRBehavior +
                ", emergDestType=" + emergDestType +
                ", priDestination='" + priDestination + '\'' +
                ", secDestination='" + secDestination + '\'' +
                ", emergConfigTimer='" + emergConfigTimer + '\'' +
                '}';
    }
}
