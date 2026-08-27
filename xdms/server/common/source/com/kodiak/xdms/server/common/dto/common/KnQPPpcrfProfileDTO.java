/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

public class KnQPPpcrfProfileDTO {
    private int qppPcrfProfileId;
    private int apnId;
    private String mcpttIdentifier;
    private int reservationPriority;

    public int getQppPcrfProfileId() {
        return qppPcrfProfileId;
    }

    public void setQppPcrfProfileId(int qppPcrfProfileId) {
        this.qppPcrfProfileId = qppPcrfProfileId;
    }

    public int getApnId() {
        return apnId;
    }

    public void setApnId(int apnId) {
        this.apnId = apnId;
    }

    public String getMcpttIdentifier() {
        return mcpttIdentifier;
    }

    public void setMcpttIdentifier(String mcpttIdentifier) {
        this.mcpttIdentifier = mcpttIdentifier;
    }

    public int getReservationPriority() {
        return reservationPriority;
    }

    public void setReservationPriority(int reservationPriority) {
        this.reservationPriority = reservationPriority;
    }

    @Override
    public String toString() {
        return "KnQPPpcrfProfileDTO{" +
                "qppPcrfProfileId=" + qppPcrfProfileId +
                ", apnId=" + apnId +
                ", mcpttIdentifier='" + mcpttIdentifier + '\'' +
                ", reservationPriority=" + reservationPriority +
                '}';
    }
}
