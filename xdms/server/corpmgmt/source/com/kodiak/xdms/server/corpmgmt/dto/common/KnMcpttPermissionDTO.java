/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * ************************************************************************
 * <p>
 * File name:  KnMcpttPermissionDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Dec 07, 2017                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnMcpttPermissionDTO {
    // DB Params
    private int corpid;
    private String authMdn;
    private String targetMdn;
    private long mcpttPerms;
    private Integer discreteEnabled;
    // Notification Params
    private int serviceAuthUserAU;
    private Integer commonAu;

    public KnMcpttPermissionDTO(){}

    public KnMcpttPermissionDTO(int corpid, String authMdn, String targetMdn, long mcpttPerms, Integer discreteEnabled) {
        this.corpid = corpid;
        this.authMdn = authMdn;
        this.targetMdn = targetMdn;
        this.mcpttPerms = mcpttPerms;
        this.discreteEnabled = discreteEnabled;
    }

    public int getCorpid() {
        return corpid;
    }

    public void setCorpid(int corpid) {
        this.corpid = corpid;
    }

    public String getAuthMdn() {
        return authMdn;
    }

    public void setAuthMdn(String authMdn) {
        this.authMdn = authMdn;
    }

    public String getTargetMdn() {
        return targetMdn;
    }

    public void setTargetMdn(String targetMdn) {
        this.targetMdn = targetMdn;
    }

    public long getMcpttPerms() {
        return mcpttPerms;
    }

    public void setMcpttPerms(long mcpttPerms) {
        this.mcpttPerms = mcpttPerms;
    }

    public Integer getDiscreteEnabled() {
        return discreteEnabled;
    }

    public void setDiscreteEnabled(Integer discreteEnabled) {
        this.discreteEnabled = discreteEnabled;
    }

    public int getServiceAuthUserAU() {
        return serviceAuthUserAU;
    }

    public void setServiceAuthUserAU(int serviceAuthUserAU) {
        this.serviceAuthUserAU = serviceAuthUserAU;
    }

    public Integer getCommonAu() { return commonAu; }

    public void setCommonAu(Integer commonAu) { this.commonAu = commonAu; }

    @Override
    public String toString() {
        return "KnMcpttPermissionDTO{" +
                "corpid=" + corpid +
                ", authMdn='" + KnGDPRTemplate.mdn(authMdn) + '\'' +
                ", targetMdn='" + KnGDPRTemplate.mdn(targetMdn) + '\'' +
                ", mcpttPerms=" + mcpttPerms +
                ", discreteEnabled=" + discreteEnabled +
                ", serviceAuthUserAU=" + serviceAuthUserAU +
                ", commonAu=" + commonAu +
                '}';
    }
}
