/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Objects;

/**
 * ************************************************************************
 * <p>
 * File name:  KnSubsEmergencyAttributes.java
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

public class KnSubsEmergencyAttributes {

    private String mdn;

    private Integer emergDestTypeIntf;

    private Integer emergCallType;

    private Integer emergCnclPermission;

    private Integer emergLmrBehaviour;

    private Integer emergInitPermission;

    private Integer emergOriginBitSet;

    private Integer emergTermBitSet;
    private String emergConfigTimer;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public Integer getEmergDestTypeIntf() {
        return emergDestTypeIntf;
    }

    public void setEmergDestTypeIntf(Integer emergDestTypeIntf) {
        this.emergDestTypeIntf = emergDestTypeIntf;
    }

    public Integer getEmergCallType() {
        return emergCallType;
    }

    public void setEmergCallType(Integer emergCallType) {
        this.emergCallType = emergCallType;
    }

    public Integer getEmergCnclPermission() {
        return emergCnclPermission;
    }

    public void setEmergCnclPermission(Integer emergCnclPermission) {
        this.emergCnclPermission = emergCnclPermission;
    }

    public Integer getEmergLmrBehaviour() {
        return emergLmrBehaviour;
    }

    public void setEmergLmrBehaviour(Integer emergLmrBehaviour) {
        this.emergLmrBehaviour = emergLmrBehaviour;
    }

    public Integer getEmergInitPermission() {
        return emergInitPermission;
    }

    public void setEmergInitPermission(Integer emergInitPermission) {
        this.emergInitPermission = emergInitPermission;
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

    public String getEmergConfigTimer() {
        return emergConfigTimer;
    }

    public void setEmergConfigTimer(String emergConfigTimer) {
        this.emergConfigTimer = emergConfigTimer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnSubsEmergencyAttributes that = (KnSubsEmergencyAttributes) o;
        return Objects.equals(emergDestTypeIntf, that.emergDestTypeIntf) && Objects.equals(emergCallType, that.emergCallType) && Objects.equals(emergCnclPermission, that.emergCnclPermission) && Objects.equals(emergInitPermission, that.emergInitPermission) && Objects.equals(emergOriginBitSet, that.emergOriginBitSet) && Objects.equals(emergTermBitSet, that.emergTermBitSet);
    }

    @Override
    public String toString() {
        return "KnSubsEmergencyAttributes{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn )+ '\'' +
                ", emergDestTypeIntf=" + emergDestTypeIntf +
                ", emergCallType=" + emergCallType +
                ", emergCnclPermission=" + emergCnclPermission +
                ", emergLmrBehaviour=" + emergLmrBehaviour +
                ", emergInitPermission=" + emergInitPermission +
                ", emergOriginBitSet=" + emergOriginBitSet +
                ", emergTermBitSet=" + emergTermBitSet +
                ", emergConfigTimer=" + emergConfigTimer +
                '}';
    }
}
