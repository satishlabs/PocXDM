/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnEmergencyMdnDTOForMCSXCAP implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676134L;

    private String entryMdn;
    private int priority;
    private int type;
    private String emergencyCallDisplayName;
    private String emergencyCallUri;
    private String emergencyCallEntryInfo;
    private String emergencyAlertUri;
    private String emergencyAlertDisplayName;
    private String privateEmergencyAlertUri;
    private String privateEmergencyAlertDispName;
    private Integer emergCallorigModeExtM;
    private Integer emergOrigAlertIndExtM;
    private String emergLocPollTimerExtM;
    private Integer emergPriorityExtM;
    private String emergConfigTimerExtM;

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

    public Integer getEmergCallorigModeExtM() {
        return emergCallorigModeExtM;
    }

    public void setEmergCallorigModeExtM(Integer emergCallorigModeExtM) {
        this.emergCallorigModeExtM = emergCallorigModeExtM;
    }

    public Integer getEmergOrigAlertIndExtM() {
        return emergOrigAlertIndExtM;
    }

    public void setEmergOrigAlertIndExtM(Integer emergOrigAlertIndExtM) {
        this.emergOrigAlertIndExtM = emergOrigAlertIndExtM;
    }

    public String getEmergLocPollTimerExtM() {
        return emergLocPollTimerExtM;
    }

    public void setEmergLocPollTimerExtM(String emergLocPollTimerExtM) {
        this.emergLocPollTimerExtM = emergLocPollTimerExtM;
    }

    public Integer getEmergPriorityExtM() {
        return emergPriorityExtM;
    }

    public void setEmergPriorityExtM(Integer emergPriorityExtM) {
        this.emergPriorityExtM = emergPriorityExtM;
    }

    public String getEmergencyCallEntryInfo() {
        return emergencyCallEntryInfo;
    }

    public void setEmergencyCallEntryInfo(String emergencyCallEntryInfo) {
        this.emergencyCallEntryInfo = emergencyCallEntryInfo;
    }

    public String getEmergConfigTimerExtM() {return emergConfigTimerExtM;}

    public void setEmergConfigTimerExtM(String emergConfigTimerExtM) {
        this.emergConfigTimerExtM = emergConfigTimerExtM;
    }

    @Override
    public String toString() {
        return "KnEmergencyMdnDTOForMCSXCAP{" +
                "entryMdn='" + KnGDPRTemplate.mdn(entryMdn) + '\'' +
                ", priority=" + priority +
                ", emergCallorigModeExtM=" + emergCallorigModeExtM +
                ", emergOrigAlertIndExtM=" + emergOrigAlertIndExtM +
                ", emergLocPollTimerExtM=" + emergLocPollTimerExtM +
                ", emergPriorityExtM=" + emergPriorityExtM +
                ", emergencyCallEntryInfo=" + emergencyCallEntryInfo +
                ", type=" + type +
                ", emergConfigTimerExtM=" + emergConfigTimerExtM +
                '}';
    }

    @Override
    public String getObjectId() {
        return entryMdn;
    }
}
