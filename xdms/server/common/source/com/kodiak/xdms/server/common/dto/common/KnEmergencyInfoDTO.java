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
 * File name:  KnEmergencyInfoDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Dec 07, 2017                9.0
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

public class KnEmergencyInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155645676170L;

    private String destination;

    private Integer destType;

    private Integer destPriority;

    @Override
    public String getObjectId() {
        return destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getDestType() {
        return destType;
    }

    public void setDestType(Integer destType) {
        this.destType = destType;
    }

    public Integer getDestPriority() {
        return destPriority;
    }

    public void setDestPriority(Integer destPriority) {
        this.destPriority = destPriority;
    }

    @Override
    public String toString() {
        return "KnEmergencyInfoDTO{" +
                "destination='" + destination + '\'' +
                ", destType=" + destType +
                ", destPriority=" + destPriority +
                '}';
    }
}
