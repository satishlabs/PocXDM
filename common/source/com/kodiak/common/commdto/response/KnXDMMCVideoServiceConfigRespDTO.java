/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMMCVideoServiceConfigRespDTO.java
 * Subsystem:  XDMS
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Shashank Tewari      29/07/2019    9.1
 * <p/>
 * <p/>
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnXDMMCVideoServiceConfigRespDTO extends KnXDMRespDTO {
    private static final long serialVersionUID = 6613773386312744671L;
    private String domain;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "KnXDMMCVideoServiceConfigRespDTO{" +
                "domain='" + domain + '\'' +
                '}';
    }
}