/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mcsnotifymgr.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.List;

/**
 * ************************************************************************
 * <p>
 * File name:  KnDocumentChangeDTO.java
 * Subsystem:  XDMS
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Venkata Sudhakar            Dec 23, 2019                10.0+
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KnDocumentChangeDTO implements IIdentifier {

    private static final long serialVersionUID = 114551026600849279L;

    @JsonProperty(value = "docType")
    private int docType;
    @JsonProperty(value ="mdn")
    private String mdn;
    @JsonProperty(value ="xcap-root-uri")
    private String xcapRootUri;
    @JsonProperty(value ="docChangeList")
    private List<KnDocChangeListDto> docChangeList;

    public KnDocumentChangeDTO() {}

    public KnDocumentChangeDTO(int docType, String mdn, String xcapRootUri, List<KnDocChangeListDto> docChangeList) {
        this.docType = docType;
        this.mdn = mdn;
        this.xcapRootUri = xcapRootUri;
        this.docChangeList = docChangeList;
    }

    public String getMdn() { return mdn; }

    public void setMdn(String mdn) { this.mdn = mdn; }

    public List<KnDocChangeListDto> getDocChangeList() { return docChangeList; }

    public void setDocChangeList(List<KnDocChangeListDto> docChangeList) { this.docChangeList = docChangeList; }

    public int getDocType() { return docType; }

    public void setDocType(int docType) { this.docType = docType; }

    public String getXcapRootUri() {
        return xcapRootUri;
    }

    public void setXcapRootUri(String xcapRootUri) {
        this.xcapRootUri = xcapRootUri;
    }

    @Override
    public String toString() {
        return "KnDocumentChangeDTO{" +
                "docType=" + docType +
                ", mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", xcapRootUri='" + KnGDPRTemplate.mdnUriTemplate(xcapRootUri) + '\'' +
                ", docChangeList=" + docChangeList +
                '}';
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
