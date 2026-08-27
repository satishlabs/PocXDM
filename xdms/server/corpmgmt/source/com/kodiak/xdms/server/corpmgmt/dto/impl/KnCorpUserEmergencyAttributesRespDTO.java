/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserEmergencyAttributes;

import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnCorpUserEmergencyAttributesRespDTO.java
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


public class KnCorpUserEmergencyAttributesRespDTO extends KnCorpResponseDTO {

    private KnCorpUserEmergencyAttributes corpUserEmergencyAttributes;

    private Collection<KnCorpUserEmergencyAttributes> corpUserEmergencyDetails;

    public KnCorpUserEmergencyAttributes getCorpUserEmergencyAttributes() {
        return corpUserEmergencyAttributes;
    }

    public void setCorpUserEmergencyAttributes(KnCorpUserEmergencyAttributes corpUserEmergencyAttributes) {
        this.corpUserEmergencyAttributes = corpUserEmergencyAttributes;
    }

    public Collection<KnCorpUserEmergencyAttributes> getCorpUserEmergencyDetails() {
        return corpUserEmergencyDetails;
    }

    public void setCorpUserEmergencyDetails(Collection<KnCorpUserEmergencyAttributes> corpUserEmergencyDetails) {
        this.corpUserEmergencyDetails = corpUserEmergencyDetails;
    }

    @Override
    public String toString() {
        return "KnCorpUserEmergencyAttributesRespDTO{" +
                "corpUserEmergencyAttributes=" + corpUserEmergencyAttributes +
                ", corpUserEmergencyDetails=" + corpUserEmergencyDetails +
                '}';
    }
}
