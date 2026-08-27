/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */

package com.kodiak.common.dto;

public class KnPaylaodInfoDTO {

    private boolean idMdnDeletedfromGroup;

    private boolean isMdnDeleted;

    public boolean isIdMdnDeletedfromGroup() {
        return idMdnDeletedfromGroup;
    }

    public void setIdMdnDeletedfromGroup(boolean idMdnDeletedfromGroup) {
        this.idMdnDeletedfromGroup = idMdnDeletedfromGroup;
    }

    public boolean isMdnDeleted() {
        return isMdnDeleted;
    }

    public void setMdnDeleted(boolean mdnDeleted) {
        isMdnDeleted = mdnDeleted;
    }

    @Override
    public String toString() {
        return "KnPaylaodInfoDTO{" +
                "idMdnDeletedfromGroup=" + idMdnDeletedfromGroup +
                ", isMdnDeleted=" + isMdnDeleted +
                '}';
    }
}
