/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

public class KnXDMGetCorpFSResponse extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = -3388047771821246619L;

    private KnCorpGetCorpFSResponse corporateFS;

    public KnCorpGetCorpFSResponse getCorporateFS() {
        return corporateFS;
    }

    public void setCorporateFS(KnCorpGetCorpFSResponse corporateFS) {
        this.corporateFS = corporateFS;
    }

    @Override
    public String toString() {
        return "KnXDMCorpGetCorpFSResponse{" +
                "corporateFS=" + corporateFS +
                '}';
    }
}
