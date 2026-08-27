/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * Created by schandra on 22-12-2017.
 */
public class KnAuthDocDTO {

    private String mdn;
    private long etag;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public long getEtag() {
        return etag;
    }

    public void setEtag(long etag) {
        this.etag = etag;
    }

    @Override
    public String toString() {
        return "KnAuthDocDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", etag='" + etag + '\'' +
                '}';
    }
}
