/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnCorpTrustMatrixInfo;

import java.util.List;

public class KnXDMCorpTrustMatrixRespDTO extends KnXDMCorpRespDTO{
    private static final long serialVersionUID = 7656512154565654L;

    List<KnCorpTrustMatrixInfo> sharedList;
    /** UCSPROVCONFIG-3193: Echo the sourceHierarchy filter used in the request */
    private String sourceHierarchy;

    public List<KnCorpTrustMatrixInfo> getSharedList() {
        return sharedList;
    }

    public void setSharedList(List<KnCorpTrustMatrixInfo> sharedList) {
        this.sharedList = sharedList;
    }

    public String getSourceHierarchy() { return sourceHierarchy; }
    public void setSourceHierarchy(String sourceHierarchy) { this.sourceHierarchy = sourceHierarchy; }
}
