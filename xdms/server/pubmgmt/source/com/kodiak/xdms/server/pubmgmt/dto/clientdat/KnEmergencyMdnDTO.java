/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnEmergencyMdnDTO {

    private String entryMdn;
    private int priority;
    private int type;
    private String emergencyCallDisplayName;
    private String emergencyCallUri;
    private String emergencyAlertUri;
    private String emergencyAlertDisplayName;
    private String privateEmergencyAlertUri;
    private String privateEmergencyAlertDispName;

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

    public String getEmergencyCallDisplayName() {
        return emergencyCallDisplayName;
    }

    public void setEmergencyCallDisplayName(String emergencyCallDisplayName) {
        this.emergencyCallDisplayName = emergencyCallDisplayName;
    }

    public String getEmergencyCallUri() {
        return emergencyCallUri;
    }

    public void setEmergencyCallUri(String emergencyCallUri) {
        this.emergencyCallUri = emergencyCallUri;
    }

    public String getEmergencyAlertUri() {
        return emergencyAlertUri;
    }

    public void setEmergencyAlertUri(String emergencyAlertUri) {
        this.emergencyAlertUri = emergencyAlertUri;
    }

    public String getEmergencyAlertDisplayName() {
        return emergencyAlertDisplayName;
    }

    public void setEmergencyAlertDisplayName(String emergencyAlertDisplayName) {
        this.emergencyAlertDisplayName = emergencyAlertDisplayName;
    }

    public String getPrivateEmergencyAlertUri() {
        return privateEmergencyAlertUri;
    }

    public void setPrivateEmergencyAlertUri(String privateEmergencyAlertUri) {
        this.privateEmergencyAlertUri = privateEmergencyAlertUri;
    }

    public String getPrivateEmergencyAlertDispName() {
        return privateEmergencyAlertDispName;
    }

    public void setPrivateEmergencyAlertDispName(String privateEmergencyAlertDispName) {
        this.privateEmergencyAlertDispName = privateEmergencyAlertDispName;
    }

    @Override
    public String toString() {
        return "KnEmergencyMdnDTO{" +
                "entryMdn='" + KnGDPRTemplate.mdn(entryMdn) + '\'' +
                ", priority=" + priority +
                ", type=" + type +
                '}';
    }
}
