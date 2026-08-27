/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

/**
 * Created by nnamita on 4/14/15.
 */
public class KnCorpGWLinkedAccountInfoDTO {
    private int accountId;
    private long gwEtag;
    private String nniRefId;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public long getGwEtag() {
        return gwEtag;
    }

    public void setGwEtag(long gwEtag) {
        this.gwEtag = gwEtag;
    }

    public String getNniRefId() {
        return nniRefId;
    }

    public void setNniRefId(String nniRefId) {
        this.nniRefId = nniRefId;
    }
}
