/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.persistdat;

/**
 * Created by hanwar on 10-09-2016.
 */
public class KnClientVocoderProfilePersistDTO {

    private int priority;
    private int vocoderId;
    private String mdn;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getVocoderId() {
        return vocoderId;
    }

    public void setVocoderId(int vocoderId) {
        this.vocoderId = vocoderId;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }
}
