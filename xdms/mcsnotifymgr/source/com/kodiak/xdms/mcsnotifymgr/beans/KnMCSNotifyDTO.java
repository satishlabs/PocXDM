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
import java.util.Set;

/**
 * ************************************************************************
 * <p>
 * File name:  KnMCSNotifyDTO.java
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
public class KnMCSNotifyDTO implements IIdentifier {

    private static final long serialVersionUID = 4285490162035314463L;

    @JsonProperty(value ="xcap-root-uri")
    private String xcapRootUri;
    @JsonProperty(value ="notify-type")
    private int notifyType;
    @JsonProperty(value ="document-change")
    private List<KnDocumentChangeDTO> documentChange;
    @JsonProperty(value = "suppress-mdn")
    private Set<String> suppressMdn;

    public String getXcapRootUri() { return xcapRootUri; }

    public void setXcapRootUri(String xcapRootUri) { this.xcapRootUri = xcapRootUri; }

    public int getNotifyType() { return notifyType; }

    public void setNotifyType(int notifyType) { this.notifyType = notifyType; }

    public List<KnDocumentChangeDTO> getDocumentChange() { return documentChange; }

    public void setDocumentChange(List<KnDocumentChangeDTO> documentChange) { this.documentChange = documentChange; }

    public Set<String> getSuppressMdn() {
        return suppressMdn;
    }

    public void setSuppressMdn(Set<String> suppressMdn) {
        this.suppressMdn = suppressMdn;
    }

    @Override
    public String toString() {
        return "KnMCSNotifyDTO{" +
                "xcapRootUri='" + KnGDPRTemplate.mdnUriTemplate(xcapRootUri) + '\'' +
                ", notifyType=" + notifyType +
                ", documentChange=" + documentChange +
                ",suppressMdn=" + suppressMdn +
                '}';
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
