/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnPocSubsAddlInfoDTO implements IIdentifier {
    private static final long serialVersionUID = -5768175723337300627L;

    private String mdn;
    private int emergOrigIndicatorBitSet;
    private int onBoardingEmailReqd;
    private Integer emergTermAlertIndExtM;

    private String tierPackage;
    private String pttSettingDocId;

    public String getTierPackage() {
        return tierPackage;
    }

    public void setTierPackage(String tierPackage) {
        this.tierPackage = tierPackage;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public int getEmergOrigIndicatorBitSet() {
        return emergOrigIndicatorBitSet;
    }

    public void setEmergOrigIndicatorBitSet(int emergOrigIndicatorBitSet) {
        this.emergOrigIndicatorBitSet = emergOrigIndicatorBitSet;
    }

    public int getOnBoardingEmailReqd() {
        return onBoardingEmailReqd;
    }

    public void setOnBoardingEmailReqd(int onBoardingEmailReqd) {
        this.onBoardingEmailReqd = onBoardingEmailReqd;
    }

    public Integer getEmergTermAlertIndExtM() { return emergTermAlertIndExtM; }

    public void setEmergTermAlertIndExtM(Integer emergTermAlertIndExtM) { this.emergTermAlertIndExtM = emergTermAlertIndExtM; }

    public String getPttSettingDocId() {
        return pttSettingDocId;
    }

    public void setPttSettingDocId(String pttSettingDocId) {
        this.pttSettingDocId = pttSettingDocId;
    }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public String toString() {
        return "KnPocSubsAddlInfoDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", emergOrigIndicatorBitSet=" + emergOrigIndicatorBitSet +
                ", onBoardingEmailReqd=" + onBoardingEmailReqd +
                ", emergTermAlertIndExtM=" + emergTermAlertIndExtM +
                ", tierPackage=" + tierPackage +
                ", pttSettingDocId='" + pttSettingDocId +
                '}';
    }
}
