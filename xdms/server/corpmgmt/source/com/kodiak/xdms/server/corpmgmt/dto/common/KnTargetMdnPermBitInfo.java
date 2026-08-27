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
 * File name:  KnTargetMdnPermBitInfo.java
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

public class KnTargetMdnPermBitInfo {
    private String mdn;
    private String subsName;
    private Integer ambientListening;
    private Integer discreteListening;
    private Integer userCheck;
    private Integer userEnable;
    private Integer emergPermission;
    private Integer mcVideoUnConfirmedPull;
    private long permBitSet;
    private Integer discreteEnabled;
    private Integer commonContact;

    public KnTargetMdnPermBitInfo(){}

    public KnTargetMdnPermBitInfo(String mdn, String subsName,
                                  Integer ambientListening, Integer discreteListening,
                                  Integer userCheck, Integer userEnable,
                                  Integer emergPermission, Integer mcVideoUnConfirmedPull,
                                  long permBitSet, Integer discreteEnabled) {
        this.mdn = mdn;
        this.subsName = subsName;
        this.ambientListening = ambientListening;
        this.discreteListening = discreteListening;
        this.userCheck = userCheck;
        this.userEnable = userEnable;
        this.emergPermission = emergPermission;
        this.mcVideoUnConfirmedPull = mcVideoUnConfirmedPull;
        this.permBitSet = permBitSet;
        this.discreteEnabled = discreteEnabled;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getSubsName() {
        return subsName;
    }

    public void setSubsName(String subsName) {
        this.subsName = subsName;
    }

    public Integer getAmbientListening() {
        return ambientListening;
    }

    public void setAmbientListening(Integer ambientListening) {
        this.ambientListening = ambientListening;
    }

    public Integer getDiscreteListening() {
        return discreteListening;
    }

    public void setDiscreteListening(Integer discreteListening) {
        this.discreteListening = discreteListening;
    }

    public Integer getUserCheck() {
        return userCheck;
    }

    public void setUserCheck(Integer userCheck) {
        this.userCheck = userCheck;
    }

    public Integer getUserEnable() {
        return userEnable;
    }

    public void setUserEnable(Integer userEnable) {
        this.userEnable = userEnable;
    }

    public Integer getEmergPermission() {
        return emergPermission;
    }

    public void setEmergPermission(Integer emergPermission) {
        this.emergPermission = emergPermission;
    }

    public long getPermBitSet() {
        return permBitSet;
    }

    public void setPermBitSet(long permBitSet) {
        this.permBitSet = permBitSet;
    }

    public Integer getDiscreteEnabled() {
        return discreteEnabled;
    }

    public void setDiscreteEnabled(Integer discreteEnabled) {
        this.discreteEnabled = discreteEnabled;
    }

    public Integer getMcVideoUnConfirmedPull() {
        return mcVideoUnConfirmedPull;
    }

    public void setMcVideoUnConfirmedPull(Integer mcVideoUnConfirmedPull) {
        this.mcVideoUnConfirmedPull = mcVideoUnConfirmedPull;
    }

    public Integer getCommonContact() { return commonContact; }

    public void setCommonContact(Integer commonContact) { this.commonContact = commonContact; }

    @Override
    public String toString() {
        return "KnTargetMdnPermBitInfo{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", subsName=" + KnGDPRTemplate.name(subsName) +
                ", ambientListening=" + ambientListening +
                ", discreteListening=" + discreteListening +
                ", userCheck=" + userCheck +
                ", userEnable=" + userEnable +
                ", emergPermission=" + emergPermission +
                ", permBitSet=" + permBitSet +
                ", discreteEnabled=" + discreteEnabled +
                ", mcVideoUnConfirmedPull=" + mcVideoUnConfirmedPull +
                ", commonContact=" + commonContact +
                '}';
    }
}
