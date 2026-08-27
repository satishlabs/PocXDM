/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.dto.IIdentifier;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/8/11
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnDialPlanDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676148L;

    private String pttServerId;
    private int networkNumberingPlan;
    private String countryCode;
    private String nationalDialPrefix;
    private String internationalDialPrefix;

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public int getNetworkNumberingPlan() {
        return networkNumberingPlan;
    }

    public void setNetworkNumberingPlan(int networkNumberingPlan) {
        this.networkNumberingPlan = networkNumberingPlan;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getNationalDialPrefix() {
        return nationalDialPrefix;
    }

    public void setNationalDialPrefix(String nationalDialPrefix) {
        this.nationalDialPrefix = nationalDialPrefix;
    }

    public String getInternationalDialPrefix() {
        return internationalDialPrefix;
    }

    public void setInternationalDialPrefix(String internationalDialPrefix) {
        this.internationalDialPrefix = internationalDialPrefix;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(super.toString());
        strBuffer.append(", pttServerId - ").append(pttServerId);
        strBuffer.append(", networkNumberingPlan - ").append(networkNumberingPlan);
        strBuffer.append(", countryCode - ").append(countryCode);
        strBuffer.append(", nationalDialPrefix - ").append(nationalDialPrefix);
        strBuffer.append(", internationalDialPrefix - ").append(internationalDialPrefix);

        return strBuffer.toString();
    }

    public String getObjectId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
