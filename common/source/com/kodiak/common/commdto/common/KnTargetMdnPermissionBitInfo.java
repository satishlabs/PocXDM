/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.io.Serializable;

/**
 * ************************************************************************
 * <p>
 * File name:  KnTargetMdnPermissionBitInfo.java
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

public class KnTargetMdnPermissionBitInfo implements Serializable {

    private static final long serialVersionUID = 7526471155622676234L;

    private String mdn;
    private String subsName;
    private int ambientListening;
    private int discreteListening;
    private int userCheck;
    private int userEnable;
    private int emergPermission;
    private int mcVideoUnConfirmedPull;
    private long permBitSet;
    //discreteEnabled can be null
    private Integer discreteEnabled;
    private Integer commonContact;

    public KnTargetMdnPermissionBitInfo(){}

    public KnTargetMdnPermissionBitInfo(String mdn, String subsName){
        this.mdn = mdn;
        this.subsName = subsName;
    }

    public KnTargetMdnPermissionBitInfo(String mdn, String subsName, int ambientListening
            , int discreteListening, int userCheck, int userEnable
            , int emergPermission, int mcVideoUnConfirmedPull
            ) {
        this.mdn = mdn;
        this.subsName = subsName;
        this.ambientListening = ambientListening;
        this.discreteListening = discreteListening;
        this.userCheck = userCheck;
        this.userEnable = userEnable;
        this.emergPermission = emergPermission;
        this.mcVideoUnConfirmedPull = mcVideoUnConfirmedPull;
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

    public int getAmbientListening() {
        return ambientListening;
    }

    public void setAmbientListening(int ambientListening) {
        this.ambientListening = ambientListening;
    }

    public int getDiscreteListening() {
        return discreteListening;
    }

    public void setDiscreteListening(int discreteListening) {
        this.discreteListening = discreteListening;
    }

    public int getUserCheck() {
        return userCheck;
    }

    public void setUserCheck(int userCheck) {
        this.userCheck = userCheck;
    }

    public int getUserEnable() {
        return userEnable;
    }

    public void setUserEnable(int userEnable) {
        this.userEnable = userEnable;
    }

    public int getEmergPermission() {
        return emergPermission;
    }

    public void setEmergPermission(int emergPermission) {
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

    public int getMcVideoUnConfirmedPull() {
        return mcVideoUnConfirmedPull;
    }

    public void setMcVideoUnConfirmedPull(int mcVideoUnConfirmedPull) {
        this.mcVideoUnConfirmedPull = mcVideoUnConfirmedPull;
    }

    public Integer getCommonContact() { return commonContact; }

    public void setCommonContact(Integer commonContact) { this.commonContact = commonContact; }

    @Override
    public String toString() {
        return "KnTargetMdnPermissionBitInfo{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", subsName=" + subsName +
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
