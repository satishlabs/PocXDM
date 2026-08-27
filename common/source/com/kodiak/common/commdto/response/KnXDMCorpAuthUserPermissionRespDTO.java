/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnTargetMdnPermissionBitInfo;

import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMCorpAuthUserPermissionRespDTO.java
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

public class KnXDMCorpAuthUserPermissionRespDTO extends KnXDMCorpRespDTO {
    private static final long serialVersionUID = 7526471155622776156L;

    private Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfo;

    public Collection<KnTargetMdnPermissionBitInfo> getTargetMdnPermissionBitInfo() {
        return targetMdnPermissionBitInfo;
    }

    public void setTargetMdnPermissionBitInfo(Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfo) {
        this.targetMdnPermissionBitInfo = targetMdnPermissionBitInfo;
    }

    @Override
    public String toString() {
        return "KnXDMCorpAuthUserPermissionRespDTO{" +
                "targetMdnPermissionBitInfo=" + targetMdnPermissionBitInfo +
                '}';
    }
}
