/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.common;

public class KnSubsAddEmgrConfigDTO {
    private int emergencyDestType;
    private int emergOriginBitset;
    private int emergReceiveBitset;


    public int getEmergencyDestType() {
        return emergencyDestType;
    }

    public void setEmergencyDestType(int emergencyDestType) {
        this.emergencyDestType = emergencyDestType;
    }

    public int getEmergOriginBitset() {
        return emergOriginBitset;
    }

    public void setEmergOriginBitset(int emergOriginBitset) {
        this.emergOriginBitset = emergOriginBitset;
    }

    public int getEmergReceiveBitset() {
        return emergReceiveBitset;
    }

    public void setEmergReceiveBitset(int emergReceiveBitset) {
        this.emergReceiveBitset = emergReceiveBitset;
    }


    @Override
    public String toString() {
        return "KnSubsAddEmgrConfigDTO{" +
                "emergencyDestType=" + emergencyDestType +
                ", emergOriginBitset=" + emergOriginBitset +
                ", emergReceiveBitset=" + emergReceiveBitset +
                '}';
    }
}
