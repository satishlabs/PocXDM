/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonInclude;
import com.kodiak.common.resources.KnGDPRTemplate;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value= JsonInclude.Include.NON_NULL)
public class KnCorpUserProfileMCPTTConfig {

    private String mdn;

    private long permBitSet;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public long getPermBitSet() {
        return permBitSet;
    }

    public void setPermBitSet(long permBitSet) {
        this.permBitSet = permBitSet;
    }

    @Override
    public String toString() {
        return "KnCorpUserProfileMCPTTConfig{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", permBitSet=" + permBitSet +
                '}';
    }
}
