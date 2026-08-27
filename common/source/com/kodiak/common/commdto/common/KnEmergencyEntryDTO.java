/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.io.Serializable;

public class KnEmergencyEntryDTO implements Serializable {
    private String entryMdn;
    private int priority;
    private int type;

    public String getEntryMdn() {
        return entryMdn;
    }

    public void setEntryMdn(String entryMdn) {
        this.entryMdn = entryMdn;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "KnEmergencyEntryDTO{" +
                "entryMdn='" + KnGDPRTemplate.mdn(entryMdn) + '\'' +
                ", priority=" + priority +
                ", type=" + type +
                '}';
    }
}
