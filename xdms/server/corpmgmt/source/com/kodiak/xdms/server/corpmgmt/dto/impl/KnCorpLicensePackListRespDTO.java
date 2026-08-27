/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnLicensePackDTO;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpLicensePackListRespDTO.java
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

public class KnCorpLicensePackListRespDTO extends KnCorpResponseDTO {

    private Collection<KnLicensePackDTO> licensePackList;

    public Collection<KnLicensePackDTO> getLicensePackList() {
        return licensePackList;
    }

    public void setLicensePackList(Collection<KnLicensePackDTO> licensePackList) {
        this.licensePackList = licensePackList;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append(super.toString());
        sb.append(", licensePackList - ").append(licensePackList);
        return sb.toString();
    }
}
