/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.xdms.server.common.dto.common.KnAPNConfigDTO;
import com.kodiak.xdms.server.common.dto.common.KnSIPProxySvcConfigDTO;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpPoCSvcConfigRespDTO.java
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

public class KnCorpPoCSvcConfigRespDTO extends KnCorpResponseDTO {

    private Collection<KnSIPProxySvcConfigDTO> sipProxySvcConfig;

    private Collection<KnAPNConfigDTO> apnConfig;

    public Collection<KnSIPProxySvcConfigDTO> getSipProxySvcConfig() {
        return sipProxySvcConfig;
    }

    public void setSipProxySvcConfig(Collection<KnSIPProxySvcConfigDTO> sipProxySvcConfig) {
        this.sipProxySvcConfig = sipProxySvcConfig;
    }

    public Collection<KnAPNConfigDTO> getApnConfig() {
        return apnConfig;
    }

    public void setApnConfig(Collection<KnAPNConfigDTO> apnConfig) {
        this.apnConfig = apnConfig;
    }

    @Override
    public String toString() {
        return "KnCorpPoCSvcConfigRespDTO{" +
                "sipProxySvcConfig=" + sipProxySvcConfig +
                ", apnConfig=" + apnConfig +
                '}';
    }
}
