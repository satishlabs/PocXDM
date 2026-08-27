/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * Created by schandra on 22-12-2017.
 */
public class KnMCPTTPermInfoDTO {
    private Long permBitset;
    private String targetMdn;
    private String authorizedMdn;
    private int discreetEnabled;

    public KnMCPTTPermInfoDTO(){}

    public KnMCPTTPermInfoDTO(Long permBitset, String targetMdn, int discreetEnabled) {
        this.permBitset = permBitset;
        this.targetMdn = targetMdn;
        this.discreetEnabled = discreetEnabled;
    }

    public Long getPermBitset() {
        return permBitset;
    }

    public void setPermBitset(Long permBitset) {
        this.permBitset = permBitset;
    }

    public String getTargetMdn() {
        return targetMdn;
    }

    public void setTargetMdn(String targetMdn) {
        this.targetMdn = targetMdn;
    }

    public String getAuthorizedMdn() {return authorizedMdn; }

    public void setAuthorizedMdn(String authorizedMdn) {this.authorizedMdn = authorizedMdn; }

    public int getDiscreetEnabled() {
        return discreetEnabled;
    }

    public void setDiscreetEnabled(int discreetEnabled) {
        this.discreetEnabled = discreetEnabled;
    }

    @Override
    public String toString() {
        return "KnMCPTTPermInfoDTO{" +
                "permBitset=" + permBitset +
                ", targetMdn='" + KnGDPRTemplate.mdn(targetMdn) + '\'' +
                ", authorizedMdn='" + KnGDPRTemplate.mdn(authorizedMdn) + '\'' +
                ", discreetEnabled=" + discreetEnabled +
                '}';
    }
}
