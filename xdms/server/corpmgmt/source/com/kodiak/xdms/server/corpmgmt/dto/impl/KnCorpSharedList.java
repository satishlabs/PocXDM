/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;

import java.util.List;

public class KnCorpSharedList  extends KnCorpResponseDTO{
    List<KnCorpTrustMatrixDTO> List;

    public java.util.List<KnCorpTrustMatrixDTO> getList() {
        return List;
    }

    public void setList(java.util.List<KnCorpTrustMatrixDTO> list) {
        List = list;
    }
}
