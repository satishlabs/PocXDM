/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpDirInfoRespDTO.java
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
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpFolderInfoDTO;

import java.util.Collection;


public class KnCorpDirInfoRespDTO extends KnCorpResponseDTO {

    private String xcapRoot;
    private Collection<KnCorpFolderInfoDTO> folders;

    public String getXcapRoot() {
        return xcapRoot;
    }

    public void setXcapRoot(String xcapRoot) {
        this.xcapRoot = xcapRoot;
    }

    public Collection<KnCorpFolderInfoDTO> getFolders() {
        return folders;
    }

    public void setFolders(Collection<KnCorpFolderInfoDTO> folders) {
        this.folders = folders;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append(super.toString())
                .append(", xcapRoot - ").append(xcapRoot)
                .append(", folders - ").append(folders);
        return sb.toString();
    }
}
