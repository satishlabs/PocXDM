/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnUserEmergencyAttributes;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMCorpEmergencyRespDTO.java
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

public class KnXDMCorpEmergencyRespDTO extends KnXDMCorpRespDTO {
    private static final long serialVersionUID = 7526781155622666156L;

    private KnUserEmergencyAttributes userEmergencyAttributes;

    public KnUserEmergencyAttributes getCorpUserEmergencyAttributes() {
        return userEmergencyAttributes;
    }

    public void setCorpUserEmergencyAttributes(KnUserEmergencyAttributes userEmergencyAttributes) {
        this.userEmergencyAttributes = userEmergencyAttributes;
    }

    @Override
    public String toString() {
        return "KnXDMCorpEmergencyRespDTO{" +
                "userEmergencyAttributes=" + userEmergencyAttributes +
                '}';
    }
}
