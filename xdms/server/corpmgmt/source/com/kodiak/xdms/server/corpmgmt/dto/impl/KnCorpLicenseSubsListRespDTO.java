/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnLicenseSubDTO;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpLicenseSubsListRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     09/10/2014    7.10
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnCorpLicenseSubsListRespDTO extends KnCorpResponseDTO {

    private Collection<KnLicenseSubDTO> licenseSubsList;

    public Collection<KnLicenseSubDTO> getLicenseSubsList() {
        return licenseSubsList;
    }

    public void setLicenseSubsList(Collection<KnLicenseSubDTO> licenseSubsList) {
        this.licenseSubsList = licenseSubsList;
    }
}
