/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpDirRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Mar 12, 2011      7.0
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
package com.kodiak.common.commdto.response;

import java.util.Collection;


public class KnXDMCorpDirRespDTO extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = 7526471155622776164L;

    private Collection<KnAppInfoDTO> appInfoList;

    public Collection<KnAppInfoDTO> getAppInfoList() {
        return appInfoList;
    }

    public void setAppInfoList(Collection<KnAppInfoDTO> appInfoList) {
        this.appInfoList = appInfoList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(super.toString())
                .append(", appInfoList - ").append(appInfoList);
        return sb.toString();
    }
}
