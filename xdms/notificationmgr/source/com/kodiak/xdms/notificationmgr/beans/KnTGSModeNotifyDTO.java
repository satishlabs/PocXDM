/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     27/5/14         7.7.0
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
 * ************************************************************************
 */
package com.kodiak.xdms.notificationmgr.beans;

public class KnTGSModeNotifyDTO extends KnCommonNotifyDTO {

    private static final long serialVersionUID = 7526471155622676762L;
    private Integer tgsMode;
 //   private Integer scanCap;

    public Integer getTgsMode() {
        return tgsMode;
    }

    public void setTgsMode(Integer tgsMode) {
        this.tgsMode = tgsMode;
    }

//    public Integer getScanCap() {
//        return scanCap;
//    }
//
//    public void setScanCap(Integer scanCap) {
//        this.scanCap = scanCap;
//    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("tgsMode - "). append(tgsMode);
      //  strBuffer.append("scanCap - "). append(scanCap);
        return strBuffer.toString();
    }
}
