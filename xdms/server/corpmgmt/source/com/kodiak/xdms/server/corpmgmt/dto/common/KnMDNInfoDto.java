/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * Created by asanjiv on 10/3/15.
 */
package com.kodiak.xdms.server.corpmgmt.dto.common;


import com.kodiak.common.resources.KnGDPRTemplate;

public class KnMDNInfoDto {
    private String mdn;
    private String name;
    private Integer corpID;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCorpID() {
        return corpID;
    }

    public void setCorpID(Integer corpID) {
        this.corpID = corpID;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(", MDN - ").append(KnGDPRTemplate.mdn(mdn));
        sb.append(", Name - ").append(KnGDPRTemplate.name(name));
        sb.append(", CorpID" ).append(corpID);
        return sb.toString();
    }
}
