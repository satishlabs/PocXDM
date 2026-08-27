/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;

/**
 * *****************************************************************************
 * <p/>
 * Subsystem:   POC
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit kumar           Jab 15, 2011       7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * *******************************************************************************
 */
public class KnDialPlanInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676172L;

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
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", pttServerId - ").append(pttServerId);
        strBuffer.append(", networkNumberingPlan - ").append(networkNumberingPlan);
        strBuffer.append(", countryCode - ").append(countryCode);
        strBuffer.append(", nationalDialPrefix - ").append(nationalDialPrefix);
        strBuffer.append(", internationalDialPrefix - ").append(internationalDialPrefix);

        return strBuffer.toString();
    }

    public String getObjectId() {
        return pttServerId;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
