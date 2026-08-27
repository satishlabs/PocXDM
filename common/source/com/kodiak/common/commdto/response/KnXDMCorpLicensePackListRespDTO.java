/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMLicensePackDTO;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpLicensePackListRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Shaik Mahaaboob Basha 9/8/14        7.10
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

public class KnXDMCorpLicensePackListRespDTO extends KnXDMCorpRespDTO implements IXDMResponseDTO {
    private static final long serialVersionUID = 7526786155622776147L;

    private Collection<KnXDMLicensePackDTO> licensePackProfileList;

    public Collection<KnXDMLicensePackDTO> getLicensePackProfileList() {
        return licensePackProfileList;
    }

    public void setLicensePackProfileList(Collection<KnXDMLicensePackDTO> licensePackProfileList) {
        this.licensePackProfileList = licensePackProfileList;
    }
}
