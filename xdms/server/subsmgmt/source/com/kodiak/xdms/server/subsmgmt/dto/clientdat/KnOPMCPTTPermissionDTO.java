/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import java.util.List;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnOPMCPTTPermissionDTO extends KnOPProvDTO {

    private static final long serialVersionUID = 5446853267699673673L;

    List<String> autorizedMdnList;

    public List<String> getAutorizedMdnList() {
        return autorizedMdnList;
    }

    public void setAutorizedMdnList(List<String> autorizedMdnList) {
        this.autorizedMdnList = autorizedMdnList;
    }

    @Override
    public String toString() {
        return "KnOPMCPTTPermissionDTO{" +
                "autorizedMdnList=" + KnGDPRTemplate.mdnList(autorizedMdnList) +
                '}';
    }
}
