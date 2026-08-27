/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnMDNInfoDto;

import java.util.List;

/**
 * ************************************************************************
 * <p>
 * File name:  KnEmergUserDestRespDTO.java
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

public class KnEmergUserDestRespDTO extends KnCorpResponseDTO {

    public List<KnMDNInfoDto> mdnInfoDtoList;

    public List<KnMDNInfoDto> getMdnInfoDtoList() {
        return mdnInfoDtoList;
    }

    public void setMdnInfoDtoList(List<KnMDNInfoDto> mdnInfoDtoList) {
        this.mdnInfoDtoList = mdnInfoDtoList;
    }

    @Override
    public String toString() {
        return "KnEmergUserDestRespDTO{" +
                "mdnInfoDtoList=" + mdnInfoDtoList +
                '}';
    }
}
