/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnSubClientSettingsDTO {

    private String mdn;
    private String recordingStaus;
    private String corpId;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getRecordingStaus() {
        return recordingStaus;
    }

    public void setRecordingStaus(String recordingStaus) {
        this.recordingStaus = recordingStaus;
    }

    @Override
    public String toString(){
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("KnSubClientSettings - ")
                .append("MDN "+ KnGDPRTemplate.mdn(mdn))
                .append("CorpID " + corpId)
                .append("Recording Status "+recordingStaus);
        return strBuilder.toString();
    }
}
