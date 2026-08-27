/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import java.io.Serializable;

public class KnXDMDialPlanInfo implements Serializable {

    private static final long serialVersionUID = 5314218160543848102L;

    private String countryCode;
    private String nationalPrefix;
    private String intlPrefix;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getNationalPrefix() {
        return nationalPrefix;
    }

    public void setNationalPrefix(String nationalPrefix) {
        this.nationalPrefix = nationalPrefix;
    }

    public String getIntlPrefix() {
        return intlPrefix;
    }

    public void setIntlPrefix(String intlPrefix) {
        this.intlPrefix = intlPrefix;
    }

    @Override
    public String toString() {
        return "KnXDMDialPlanInfo{" +
                "countryCode='" + countryCode + '\'' +
                ", nationalPrefix='" + nationalPrefix + '\'' +
                ", intlPrefix='" + intlPrefix + '\'' +
                '}';
    }
}
