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
 * File name:  KnCorpLITargetProfile.java
 * Subsystem:  WebApps
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Jan 13, 2017               8.3
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

public class KnCorpLITargetProfile {

    private String mdn;

    private long insertionTime;

    private String LIID;

    private String LIPttServerId;

    private String LICCCPttServerId;

    private String DFCCIPAddress;

    private int DFCCPort;

    private int IPAddressType;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public long getInsertionTime() {
        return insertionTime;
    }

    public void setInsertionTime(long insertionTime) {
        this.insertionTime = insertionTime;
    }

    public String getLIID() {
        return LIID;
    }

    public void setLIID(String LIID) {
        this.LIID = LIID;
    }

    public String getLIPttServerId() {
        return LIPttServerId;
    }

    public void setLIPttServerId(String LIPttServerId) {
        this.LIPttServerId = LIPttServerId;
    }

    public String getLICCCPttServerId() {
        return LICCCPttServerId;
    }

    public void setLICCCPttServerId(String LICCCPttServerId) {
        this.LICCCPttServerId = LICCCPttServerId;
    }

    public String getDFCCIPAddress() {
        return DFCCIPAddress;
    }

    public void setDFCCIPAddress(String DFCCIPAddress) {
        this.DFCCIPAddress = DFCCIPAddress;
    }

    public int getDFCCPort() {
        return DFCCPort;
    }

    public void setDFCCPort(int DFCCPort) {
        this.DFCCPort = DFCCPort;
    }

    public int getIPAddressType() {
        return IPAddressType;
    }

    public void setIPAddressType(int IPAddressType) {
        this.IPAddressType = IPAddressType;
    }

    @Override
    public String toString() {
        return "KnLITargetProfileInfoRespDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", insertionTime='" + insertionTime + '\'' +
                ", LIID='" + LIID + '\'' +
                ", LIPttServerId='" + LIPttServerId + '\'' +
                ", LICCCPttServerId='" + LICCCPttServerId + '\'' +
                ", DFCCIPAddress='" + DFCCIPAddress + '\'' +
                ", DFCCPort='" + DFCCPort + '\'' +
                ", IPAddressType='" + IPAddressType + '\'' +
                '}';
    }
}
