/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.io.Serializable;

/**
 * ************************************************************************
 * <p>
 * File name:  KnPUBContactAddonAliasDTO.java
 * Subsystem:  XCAP
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Shashank Tewari             March 13, 2020                10.1
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

public class KnPUBContactAddonAliasDTO implements Serializable {

    private String ownerMDN;
    private String contactMDN;
    private Integer contactType;
    private String key;
    private String value;


    public String getOwnerMDN() {
        return ownerMDN;
    }

    public void setOwnerMDN(String ownerMDN) {
        this.ownerMDN = ownerMDN;
    }

    public String getContactMDN() {
        return contactMDN;
    }

    public void setContactMDN(String contactMDN) {
        this.contactMDN = contactMDN;
    }

    public Integer getContactType() {
        return contactType;
    }

    public void setContactType(Integer contactType) {
        this.contactType = contactType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "KnPUBContactAddonAliasDTO{" +
                "ownerMDN='" + KnGDPRTemplate.mdn(ownerMDN) + '\'' +
                ", contactMDN='" + KnGDPRTemplate.mdn(contactMDN) + '\'' +
                ", contactType=" + contactType +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}