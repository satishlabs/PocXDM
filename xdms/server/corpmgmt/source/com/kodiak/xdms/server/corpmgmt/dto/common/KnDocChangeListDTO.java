/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * ************************************************************************
 * <p>
 * File name:  KnDocChangeListDto.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Feb 27, 2020                10.0.+
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnDocChangeListDTO {

    private String mdn;
    private String docUri;
    private String newEtag;
    private String previousEtag;
    private String exists;

    public KnDocChangeListDTO(){

    }

    public KnDocChangeListDTO(String mdn, String docUri, String newEtag, String previousEtag, String exists) {
        this.mdn = mdn;
        this.docUri = docUri;
        this.newEtag = newEtag;
        this.previousEtag = previousEtag;
        this.exists = exists;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getDocUri() {
        return docUri;
    }

    public void setDocUri(String docUri) {
        this.docUri = docUri;
    }

    public String getNewEtag() {
        return newEtag;
    }

    public void setNewEtag(String newEtag) {
        this.newEtag = newEtag;
    }

    public String getPreviousEtag() {
        return previousEtag;
    }

    public void setPreviousEtag(String previousEtag) {
        this.previousEtag = previousEtag;
    }

    public String getExists() {
        return exists;
    }

    public void setExists(String exists) {
        this.exists = exists;
    }

    @Override
    public String toString() {
        return "KnDocChangeList{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", docUri='" + docUri + '\'' +
                ", newEtag='" + newEtag + '\'' +
                ", previousEtag='" + previousEtag + '\'' +
                ", exists='" + exists + '\'' +
                '}';
    }
}
