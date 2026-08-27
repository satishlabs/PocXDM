/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnTargetMdnPermBitInfo;

import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnCorpUserPermissionRespDTO.java
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

public class KnCorpUserPermissionRespDTO extends KnCorpResponseDTO {

    private Collection<KnTargetMdnPermBitInfo> targetMdnPermissionBitInfoList;

    private Collection<String> authMdnList;

    public Collection<KnTargetMdnPermBitInfo> getTargetMdnPermissionBitInfoList() {
        return targetMdnPermissionBitInfoList;
    }

    public void setTargetMdnPermissionBitInfoList(Collection<KnTargetMdnPermBitInfo> targetMdnPermissionBitInfoList) {
        this.targetMdnPermissionBitInfoList = targetMdnPermissionBitInfoList;
    }

    public Collection<String> getAuthMdnList() {
        return authMdnList;
    }

    public void setAuthMdnList(Collection<String> authMdnList) {
        this.authMdnList = authMdnList;
    }

    @Override
    public String toString() {
        return "KnCorpUserPermissionRespDTO{" +
                "targetMdnPermissionBitInfoList=" + targetMdnPermissionBitInfoList +
                ", authMdnList=" + KnGDPRTemplate.mdnList(authMdnList )+
                '}';
    }
}
