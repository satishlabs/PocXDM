/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mcsnotifymgr.beans;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * ************************************************************************
 * <p>
 * File name: KnDocChangeListDto.java
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
public class KnDocChangeListDto implements IIdentifier {

    private static final long serialVersionUID = 7774391869703977027L;


    @JsonProperty(value ="doc-uri")
    private String docUri;
    @JsonProperty(value ="new-etag")
    private String newEtag;
    @JsonProperty(value ="previous-etag")
    private String previousEtag;
    @JsonProperty(value ="exists")
    private String exists;
    @JsonProperty(value ="doc-diff")
    private KnDocumentDiffDTO documentDiffDTO;
    private List<String> docUriList;

    public KnDocChangeListDto() {}

    public KnDocChangeListDto(String docUri, String newEtag, String previousEtag, String exists) {
        this.docUri = docUri;
        this.newEtag = newEtag;
        this.previousEtag = previousEtag;
        this.exists = exists;
    }

    public String getDocUri() { return docUri; }

    public void setDocUri(String docUri) { this.docUri = docUri; }

    public String getNewEtag() { return newEtag; }

    public void setNewEtag(String newEtag) { this.newEtag = newEtag; }

    public String getPreviousEtag() { return previousEtag; }

    public void setPreviousEtag(String previousEtag) { this.previousEtag = previousEtag; }

    public KnDocumentDiffDTO getDocumentDiffDTO() { return documentDiffDTO; }

    public void setDocumentDiffDTO(KnDocumentDiffDTO documentDiffDTO) { this.documentDiffDTO = documentDiffDTO; }

    public String getExists() {
        return exists;
    }

    public void setExists(String exists) {
        this.exists = exists;
    }

    public List<String> getDocUriList() {
        return docUriList;
    }

    public void setDocUriList(List<String> docUriList) {
        this.docUriList = docUriList;
    }

    @Override
    public String toString() {
        return "KnDocChangeListDto{" +
                "docUri='" + KnGDPRTemplate.mdnUriTemplate(docUri) + '\'' +
                ", newEtag='" + newEtag + '\'' +
                ", previousEtag='" + previousEtag + '\'' +
                ", exists='" + exists + '\'' +
                ", documentDiffDTO=" + documentDiffDTO +
                ", docUriList= " + docUriList +
                '}';
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
