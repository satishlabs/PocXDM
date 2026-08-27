/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpLITargetProfile;

import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnCorpLITargetInfoRespDTO.java
 * Subsystem:  WebApps
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Jan 13, 2017               8.3
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

public class KnCorpLITargetInfoRespDTO extends KnCorpResponseDTO {

    private Collection<KnCorpLITargetProfile> liTargetProfileList;

    public Collection<KnCorpLITargetProfile> getLiTargetProfileList() {
        return liTargetProfileList;
    }

    public void setLiTargetProfileList(Collection<KnCorpLITargetProfile> liTargetProfileList) {
        this.liTargetProfileList = liTargetProfileList;
    }

    @Override
    public String toString() {
        return "KnCorpLITargetInfoRespDTO{" +
                "liTargetProfileList=" + liTargetProfileList +
                '}';
    }
}
