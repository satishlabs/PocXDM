/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * ************************************************************************
 * <p>
 * File name:  KnTargetPermsInfoDTO.java
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

public class KnTargetPermsInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676170L;

    private String mdn;
    private String aliasMdn;
    private String userId;
    private String fper;
    private String fsts;
    private long activeFs;

    @Override
    public String getObjectId() {
        return mdn;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFper() {
        return fper;
    }

    public void setFper(String fper) {
        this.fper = fper;
    }

    public String getFsts() {
        return fsts;
    }

    public void setFsts(String fsts) {
        this.fsts = fsts;
    }

    public long getActiveFs() {
        return activeFs;
    }

    public void setActiveFs(long activeFs) {
        this.activeFs = activeFs;
    }

    @Override
    public String toString() {
        return "KnTargetPermsInfoDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", aliasMdn='" + KnGDPRTemplate.mdn(aliasMdn) + '\'' +
                ", userId='" + KnGDPRTemplate.userId(userId) + '\'' +
                ", fper='" + fper + '\'' +
                ", fsts='" + fsts + '\'' +
                ", activeFs='" + activeFs + '\'' +
                '}';
    }
}
