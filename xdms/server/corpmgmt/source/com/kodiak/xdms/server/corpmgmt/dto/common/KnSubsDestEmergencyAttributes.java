/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

/**
 * ************************************************************************
 * <p>
 * File name:  KnSubsDestEmergencyAttributes.java
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

public class KnSubsDestEmergencyAttributes extends KnSubsEmergencyAttributes {

    private Integer emergDestTypeMgmt;

    private Integer emergDestPriority;

    private String emergDest;

    public Integer getEmergDestTypeMgmt() {
        return emergDestTypeMgmt;
    }

    public void setEmergDestTypeMgmt(Integer emergDestTypeMgmt) {
        this.emergDestTypeMgmt = emergDestTypeMgmt;
    }

    public Integer getEmergDestPriority() {
        return emergDestPriority;
    }

    public void setEmergDestPriority(Integer emergDestPriority) {
        this.emergDestPriority = emergDestPriority;
    }

    public String getEmergDest() {
        return emergDest;
    }

    public void setEmergDest(String emergDest) {
        this.emergDest = emergDest;
    }

    @Override
    public String toString() {
        return "KnSubsDestEmergencyAttributes{" +
                "emergDestTypeMgmt=" + emergDestTypeMgmt +
                ", emergDestPriority=" + emergDestPriority +
                ", emergDest='" + emergDest + '\'' +
                super.toString()+
                '}';
    }
}
