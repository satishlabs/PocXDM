/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnLITargetProfileInfo;

import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnLITargetProfileInfoRespDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Feb 06, 2017                8.3
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

public class KnLITargetProfileInfoRespDTO extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = 7526471155622776152L;

    private Collection<KnLITargetProfileInfo> liTargetProfileInfoDTO;

    public Collection<KnLITargetProfileInfo> getLiTargetProfileInfoDTO() {
        return liTargetProfileInfoDTO;
    }

    public void setLiTargetProfileInfoDTO(Collection<KnLITargetProfileInfo> liTargetProfileInfoDTO) {
        this.liTargetProfileInfoDTO = liTargetProfileInfoDTO;
    }

    @Override
    public String toString() {
        return "KnLITargetProfileInfoRespDTO{" +
                "liTargetProfileInfoDTO=" + liTargetProfileInfoDTO +
                '}';
    }
}
