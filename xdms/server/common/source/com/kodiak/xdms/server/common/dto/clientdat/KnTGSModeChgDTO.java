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
package com.kodiak.xdms.server.common.dto.clientdat;

import com.kodiak.common.dto.IIdentifier;

public class KnTGSModeChgDTO implements IIdentifier {
    private static final long serialVersionUID = 7526471155622676765L;
   //stores the PoC Home
    private String pocHome;
    //stores the presence Home
    private String presenceHome;
    //store the TSGMode value
    private Integer tgsMode;

   // private Integer scanCap;

    public String getPocHome() {
        return pocHome;
    }

    public void setPocHome(String pocHome) {
        this.pocHome = pocHome;
    }

    public String getPresenceHome() {
        return presenceHome;
    }

    public void setPresenceHome(String presenceHome) {
        this.presenceHome = presenceHome;
    }

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
        StringBuilder sb = new StringBuilder(100);
        sb.append("[KnTGSModeChgDTO -> ");
        sb.append("pocHome - ").append(pocHome)
                .append(", presenceHome - ").append(presenceHome)
                .append(", tgsMode - ").append(tgsMode)
             //   .append(", scanCap - ").append(scanCap)
                .append(super.toString()).append("]");

        return sb.toString();
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
