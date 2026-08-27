/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;

/**
 * ************************************************************************
 * <p>
 * File name:  KnEmergencyAttributesDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Jul 12, 2018                9.0
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

public class KnEmergencyAttributesDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155645676179L;

    private Integer emergInitPerm;

    private Integer emergCancelPerm;

    private Integer emergCallType;

    private Integer emergOrigBitSet;

    private Integer emergDestType;

    private String emergConfigTimer;


    public Integer getEmergInitPerm() {
        return emergInitPerm;
    }

    public void setEmergInitPerm(Integer emergInitPerm) {
        this.emergInitPerm = emergInitPerm;
    }

    public Integer getEmergCancelPerm() {
        return emergCancelPerm;
    }

    public void setEmergCancelPerm(Integer emergCancelPerm) {
        this.emergCancelPerm = emergCancelPerm;
    }

    public Integer getEmergCallType() {
        return emergCallType;
    }

    public void setEmergCallType(Integer emergCallType) {
        this.emergCallType = emergCallType;
    }

    public Integer getEmergOrigBitSet() {
        return emergOrigBitSet;
    }

    public void setEmergOrigBitSet(Integer emergOrigBitSet) {
        this.emergOrigBitSet = emergOrigBitSet;
    }

    public Integer getEmergDestType() {
        return emergDestType;
    }

    public void setEmergDestType(Integer emergDestType) {
        this.emergDestType = emergDestType;
    }

    public String getEmergConfigTimer() { return emergConfigTimer;}

    public void setEmergConfigTimer(String emergConfigTimer) { this.emergConfigTimer = emergConfigTimer; }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public String toString() {
        return "KnEmergencyAttributesDTO{" +
                "emergInitPerm=" + emergInitPerm +
                ", emergCancelPerm=" + emergCancelPerm +
                ", emergCallType=" + emergCallType +
                ", emergOrigBitSet=" + emergOrigBitSet +
                ", emergDestType=" + emergDestType +
                ", emergConfigTimer='" + emergConfigTimer +
                '}';
    }
}
