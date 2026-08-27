/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnAPNProfileConfigDTO;
import com.kodiak.common.commdto.common.KnSipProxySvcConfigDTO;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPoCConfigInfoRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar      June 04, 2019      9.1.1
 * <p/>
 * <p/>
 * KODIAK, 9th Floor, MFar Greenheart Phase IV
 * Manyata Tech Park, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnPoCConfigInfoRespDTO extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = 7526471155622776150L;

    private Collection<KnSipProxySvcConfigDTO> sipProxySvcConfig;

    private Collection<KnAPNProfileConfigDTO> apnProfileConfig;

    public Collection<KnSipProxySvcConfigDTO> getSipProxySvcConfig() {
        return sipProxySvcConfig;
    }

    public void setSipProxySvcConfig(Collection<KnSipProxySvcConfigDTO> sipProxySvcConfig) {
        this.sipProxySvcConfig = sipProxySvcConfig;
    }

    public Collection<KnAPNProfileConfigDTO> getApnProfileConfig() {
        return apnProfileConfig;
    }

    public void setApnProfileConfig(Collection<KnAPNProfileConfigDTO> apnProfileConfig) {
        this.apnProfileConfig = apnProfileConfig;
    }

    @Override
    public String toString() {
        return "KnPoCConfigInfoRespDTO{" +
                "sipProxySvcConfig=" + sipProxySvcConfig +
                ", apnProfileConfig=" + apnProfileConfig +
                '}';
    }
}
