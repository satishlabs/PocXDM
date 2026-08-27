/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnCorpUserProfileMCPTTConfig {

    private String mdn;

    private long permBitSet;

    public KnCorpUserProfileMCPTTConfig(){

    }

    public KnCorpUserProfileMCPTTConfig(String mdn, long permBitSet) {
        this.mdn = mdn;
        this.permBitSet = permBitSet;
    }

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
