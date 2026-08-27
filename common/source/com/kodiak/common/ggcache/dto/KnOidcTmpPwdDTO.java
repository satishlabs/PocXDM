/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dto;

import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * *****************************************************************************
 * File name:   KnOidcTmpPwdDTO
 * Subsystem:   PoCXDM
 * Description:
 * <p/>
 * Name                  Date                   Release
 * -----------------    -----------             -------
 * Saurabh Kumar           Nov 20, 2018          9.0
 * <p/>
 * <p/>
 * Copyright (c) 2018  Kodiak , A Motorola Solutions Company
 * 9th floor, MFar, Manayata Tech Park,
 * Greenheart Phase IV, Nagawara,
 * Bengaluru, Karnataka 560045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak,A Motorola Solutions Company
 * You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak,A Motorola Solutions Company.
 * *******************************************************************************
 */

public class KnOidcTmpPwdDTO {

    private String mdn;

    private String tmpPwd;

    private String tmpPwdExpiry;

    private String tmpPwdCreationTS;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getTmpPwd() {
        return tmpPwd;
    }

    public void setTmpPwd(String tmpPwd) {
        this.tmpPwd = tmpPwd;
    }

    public String getTmpPwdExpiry() {
        return tmpPwdExpiry;
    }

    public void setTmpPwdExpiry(String tmpPwdExpiry) {
        this.tmpPwdExpiry = tmpPwdExpiry;
    }

    public String getTmpPwdCreationTS() {
        return tmpPwdCreationTS;
    }

    public void setTmpPwdCreationTS(String tmpPwdCreationTS) {
        this.tmpPwdCreationTS = tmpPwdCreationTS;
    }

    @Override
    public String toString() {
        return "KnOidcTmpPwdDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", tmpPwd='" + tmpPwd + '\'' +
                ", tmpPwdExpiry='" + tmpPwdExpiry + '\'' +
                ", tmpPwdCreationTS='" + tmpPwdCreationTS + '\'' +
                '}';
    }
}
