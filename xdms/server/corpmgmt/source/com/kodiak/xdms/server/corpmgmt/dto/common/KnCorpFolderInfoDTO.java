/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnFolderInfoDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Mar 11, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.common;

import java.util.Collection;


public class KnCorpFolderInfoDTO {

    private String auid;
    private Collection<KnCorpDocInfoDTO> entries;

    public String getAuid() {
        return auid;
    }

    public void setAuid(String auid) {
        this.auid = auid;
    }

    public Collection<KnCorpDocInfoDTO> getEntries() {
        return entries;
    }

    public void setEntries(Collection<KnCorpDocInfoDTO> entries) {
        this.entries = entries;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append(super.toString())
                .append(", auid - ").append(auid)
                .append(", entries - ").append(entries);
        return sb.toString();
    }
}
