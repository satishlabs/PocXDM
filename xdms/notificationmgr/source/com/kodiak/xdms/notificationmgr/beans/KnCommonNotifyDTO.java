/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.notificationmgr.beans;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnCommonNotifyDTO.java
 * Subsystem:  PoC XDMS
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ajit Kumar           21-Sep-2012  7.4
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
public class KnCommonNotifyDTO implements IIdentifier {
    private static final long serialVersionUID = 7526471155622676300L;
    //stores the mdn
    private String mdn;
    //stores the PoC Home
    private String pocHome;
    //stores the presence Home
    private String presenceHome;
    //stores the reason
    private int action;

    /**
     * getter method for the MDN
     *
     * @return String
     */
    public String getMdn() {
        return mdn;
    }

    /**
     * setter method for the MDN
     *
     * @param mdn String
     */
    public void setMdn(String mdn) {
        if (mdn != null) {
            mdn = mdn.trim();
            if (mdn.equals("")) {
                mdn = null;
            }
        }
        this.mdn = mdn;
    }

    /**
     * getter method for the POC Home
     *
     * @return String
     */
    public String getPocHome() {
        return pocHome;
    }

    /**
     * setter method for the POC Home
     *
     * @param pocHome String
     */
    public void setPocHome(String pocHome) {
        if (pocHome != null) {
            pocHome = pocHome.trim();
            if (pocHome.equals("")) {
                pocHome = null;
            }
        }
        this.pocHome = pocHome;
    }

    /**
     * getter method for the Presence Home
     *
     * @return String
     */
    public String getPresenceHome() {
        return presenceHome;
    }

    /**
     * setter method for the Presence Home
     *
     * @param presenceHome String
     */
    public void setPresenceHome(String presenceHome) {
        if (presenceHome != null) {
            presenceHome = presenceHome.trim();
            if (presenceHome.equals("")) {
                presenceHome = null;
            }
        }
        this.presenceHome = presenceHome;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getObjectId() {
        return mdn;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(" KnCommonNotifyDTO --> ");
        strBuffer.append(" MDN - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", POC_HOME - ").append(pocHome)
                .append(", PRESENCE_HOME - ").append(presenceHome)
                .append(", ACTION - ").append(action);
        return strBuffer.toString();
    }
}
