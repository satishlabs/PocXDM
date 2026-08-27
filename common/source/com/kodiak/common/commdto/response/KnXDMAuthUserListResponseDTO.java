/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.List;

public class KnXDMAuthUserListResponseDTO extends KnXDMRespDTO {

    private static final long serialVersionUID = -8306364183553963039L;

    private List<String> autorizationMdnList;

    public List<String> getAutorizationMdnList() {
        return autorizationMdnList;
    }

    public void setAutorizationMdnList(List<String> autorizationMdnList) {
        this.autorizationMdnList = autorizationMdnList;
    }

    @Override
    public String toString() {
        return "KnXDMAuthUserListResponseDTO{" +
                "autorizationMdnList=" + KnGDPRTemplate.mdnList(autorizationMdnList)  +
                '}';
    }
}


