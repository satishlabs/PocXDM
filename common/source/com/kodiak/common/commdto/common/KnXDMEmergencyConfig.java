/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.io.Serializable;
import java.util.Set;

public class KnXDMEmergencyConfig implements Serializable {
    private static final long serialVersionUID = -1155693988597970L;

    private String emergInitPermission;
    private String emergDestType;
    private String emergCallType;
    private String emergCancelPermission;
    private String emergOriginBitSet;
    private String emergTermBitSet;
    private String emergLMRBehavior;
    private Set<KnXDMEmergencyDestAttributes> emergDestAttributes;
    private String emergConfigTimer;

    public String getEmergInitPermission() {
        return emergInitPermission;
    }

    public void setEmergInitPermission(String emergInitPermission) {
        this.emergInitPermission = emergInitPermission;
    }

    public String getEmergDestType() {
        return emergDestType;
    }

    public void setEmergDestType(String emergDestType) {
        this.emergDestType = emergDestType;
    }

    public String getEmergCallType() {
        return emergCallType;
    }

    public void setEmergCallType(String emergCallType) {
        this.emergCallType = emergCallType;
    }

    public String getEmergCancelPermission() {
        return emergCancelPermission;
    }

    public void setEmergCancelPermission(String emergCancelPermission) {
        this.emergCancelPermission = emergCancelPermission;
    }

    public String getEmergOriginBitSet() {
        return emergOriginBitSet;
    }

    public void setEmergOriginBitSet(String emergOriginBitSet) {
        this.emergOriginBitSet = emergOriginBitSet;
    }

    public String getEmergTermBitSet() {
        return emergTermBitSet;
    }

    public void setEmergTermBitSet(String emergTermBitSet) {
        this.emergTermBitSet = emergTermBitSet;
    }

    public String getEmergLMRBehavior() {
        return emergLMRBehavior;
    }

    public void setEmergLMRBehavior(String emergLMRBehavior) {
        this.emergLMRBehavior = emergLMRBehavior;
    }

    public Set<KnXDMEmergencyDestAttributes> getEmergDestAttributes() {
        return emergDestAttributes;
    }

    public void setEmergDestAttributes(Set<KnXDMEmergencyDestAttributes> emergDestAttributes) {
        this.emergDestAttributes = emergDestAttributes;
    }

    public String getEmergConfigTimer() {
        return emergConfigTimer;
    }

    public void setEmergConfigTimer(String emergConfigTimer) {
        this.emergConfigTimer = emergConfigTimer;
    }

    @Override
    public String toString() {
        return "KnXDMEmergencyConfig{" +
                "emergInitPermission='" + emergInitPermission + '\'' +
                ", emergDestType='" + emergDestType + '\'' +
                ", emergCallType='" + emergCallType + '\'' +
                ", emergCancelPermission='" + emergCancelPermission + '\'' +
                ", emergOriginBitSet='" + emergOriginBitSet + '\'' +
                ", emergTermBitSet='" + emergTermBitSet + '\'' +
                ", emergLMRBehavior='" + emergLMRBehavior + '\'' +
                ", emergDestAttributes=" + emergDestAttributes + '\'' +
                ", emergConfigTimer='" + emergConfigTimer +
                '}';
    }
}
