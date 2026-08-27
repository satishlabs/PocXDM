/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;

import java.util.List;

/**
 * ************************************************************************
 * <p>
 * File name:  KnUserEmergDestResponseDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             April 05, 2018                9.0
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

public class KnUserEmergDestResponseDTO extends KnXDMCorpRespDTO {
    private static final long serialVersionUID = 7526471155656676150L;

    private List<KnXDMMdnInfoDTO> emergencyUsers;

    public List<KnXDMMdnInfoDTO> getEmergencyUsers() {
        return emergencyUsers;
    }

    public void setEmergencyUsers(List<KnXDMMdnInfoDTO> emergencyUsers) {
        this.emergencyUsers = emergencyUsers;
    }

    @Override
    public String toString() {
        return "KnUserEmergDestResponseDTO{" +
                "emergencyUsers=" + emergencyUsers +
                '}';
    }
}
