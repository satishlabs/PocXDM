/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 Created by venkata sudhakar talluri on 02-01-2019
 */

public class KnTGSSDocDTO implements IIdentifier{

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
    public String getObjectId() { return mdn; }

    @Override
    public String toString() {
        return "KnTGSSDocDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", etag=" + etag +
                '}';
    }
}
