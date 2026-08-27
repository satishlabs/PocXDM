/**
 *  Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
 *  All Rights Reserved
 *  Motorola Solutions Confidential Restricted
 *
 */
package com.kodiak.xdms.notificationmgr.beans;
import com.kodiak.common.resources.KnGDPRTemplate;
import java.util.Objects;
public class KnMcsxcapMdnDTO {
    private String mdn;
    private boolean emergencyFlag;
    public String getMdn() {
        return mdn;
    }
    public void setMdn(String mdn) {
        this.mdn = mdn;
    }
    public boolean isEmergencyFlag() {
        return emergencyFlag;
    }
    public void setEmergencyFlag(boolean emergencyFlag) {
        this.emergencyFlag = emergencyFlag;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnMcsxcapMdnDTO that = (KnMcsxcapMdnDTO) o;
        return emergencyFlag == that.emergencyFlag && Objects.equals(mdn, that.mdn);
    }
    @Override
    public int hashCode() {
        return Objects.hash(mdn, emergencyFlag);
    }
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("KnMcsxcapMdnDTO{");
        sb.append("mdn='").append(KnGDPRTemplate.mdn(mdn)).append('\'');
        sb.append(", emergencyFlag=").append(emergencyFlag);
        sb.append('}');
        return sb.toString();
    }
}
