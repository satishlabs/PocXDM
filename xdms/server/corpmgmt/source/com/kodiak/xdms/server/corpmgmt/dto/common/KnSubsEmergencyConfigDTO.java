/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import java.util.Set;

public class KnSubsEmergencyConfigDTO {

    private Integer permission;
    private Integer destType;
    private Integer callType;
    private Integer cancelPermission;
    private Integer origBitset;
    private Integer termBitset;
    private Integer lmrBehavior;
    private Set<KnDestinationAttributeDTO> destAttributes;
    private String emergConfigTimer;

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public Integer getDestType() {
        return destType;
    }

    public void setDestType(Integer destType) {
        this.destType = destType;
    }

    public Integer getCallType() {
        return callType;
    }

    public void setCallType(Integer callType) {
        this.callType = callType;
    }

    public Integer getCancelPermission() {
        return cancelPermission;
    }

    public void setCancelPermission(Integer cancelPermission) {
        this.cancelPermission = cancelPermission;
    }

    public Integer getOrigBitset() {
        return origBitset;
    }

    public void setOrigBitset(Integer origBitset) {
        this.origBitset = origBitset;
    }

    public Integer getTermBitset() {
        return termBitset;
    }

    public void setTermBitset(Integer termBitset) {
        this.termBitset = termBitset;
    }

    public Integer getLmrBehavior() {
        return lmrBehavior;
    }

    public void setLmrBehavior(Integer lmrBehavior) {
        this.lmrBehavior = lmrBehavior;
    }

    public Set<KnDestinationAttributeDTO> getDestAttributes() {
        return destAttributes;
    }

    public void setDestAttributes(Set<KnDestinationAttributeDTO> destAttributes) {
        this.destAttributes = destAttributes;
    }

    public String getEmergConfigTimer() { return emergConfigTimer; }

    public void setEmergConfigTimer(String emergConfigTimer) { this.emergConfigTimer = emergConfigTimer; }

    @Override
    public String toString() {
        return "KnSubsEmergencyConfigDTO{" +
                "permission=" + permission +
                ", destType=" + destType +
                ", callType=" + callType +
                ", cancelPermission=" + cancelPermission +
                ", origBitset=" + origBitset +
                ", termBitset=" + termBitset +
                ", lmrBehavior=" + lmrBehavior +
                ", destAttributes=" + destAttributes +
                ", emergConfigTimer=" + emergConfigTimer +
                '}';
    }
}
