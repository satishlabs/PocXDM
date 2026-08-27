/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnChangeMDNDTO.java
 * Subsystem:  common lib
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Feb 16, 2011           7.0
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

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnChangeMDNDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676170L;

    private String oldMDN;
    private String oldIMEI;
    private String newMDN;
    private String newIMEI;
    private KnConstants.HIERARCHY_TYPE hierarchyType;

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    /**
     * getter method for the old MDN
     *
     * @return String
     */
    public String getOldMDN() {
        return oldMDN;
    }

    /**
     * setter method for the old MDN
     *
     * @param oldMDN String
     */
    public void setOldMDN(String oldMDN) {
        if (oldMDN != null) {
            oldMDN = oldMDN.trim();
            if (oldMDN.equals("")) {
                oldMDN = null;
            }
        }
        this.oldMDN = oldMDN;
    }

    /**
     * getter method for the Old IMEI
     *
     * @return String
     */
    public String getOldIMEI() {
        return oldIMEI;
    }

    /**
     * setter method for the old IMEI
     *
     * @param oldIMEI String
     */
    public void setOldIMEI(String oldIMEI) {
        if (oldIMEI != null) {
            oldIMEI = oldIMEI.trim();
            if (oldIMEI.equals("")) {
                oldIMEI = null;
            }
        }
        this.oldIMEI = oldIMEI;
    }

    /**
     * getter method for the new MDN
     *
     * @return String
     */
    public String getNewMDN() {
        return newMDN;
    }

    /**
     * setter method for the new MDN
     *
     * @param newMDN String
     */
    public void setNewMDN(String newMDN) {
        if (newMDN != null) {
            newMDN = newMDN.trim();
            if (newMDN.equals("")) {
                newMDN = null;
            }
        }
        this.newMDN = newMDN;
    }

    /**
     * getter method for the new IMEI
     *
     * @return String
     */
    public String getNewIMEI() {
        return newIMEI;
    }

    /**
     * setter method for the new IMEI
     *
     * @param newIMEI String
     */
    public void setNewIMEI(String newIMEI) {
        if (newIMEI != null) {
            newIMEI = newIMEI.trim();
            if (newIMEI.equals("")) {
                newIMEI = null;
            }
        }
        this.newIMEI = newIMEI;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append("[KnChangeMDNDTO --> ");
        strBuffer.append(" OLD_MDN - ").append(KnGDPRTemplate.mdn(oldMDN))
                .append(", OLD_IMEI - ").append(oldIMEI)
                .append(", NEW_MDN - ").append(KnGDPRTemplate.mdn(newMDN))
                .append(", OLD_IMEI - ").append(newIMEI)
                .append("]");
        return strBuffer.toString();
    }

    public String getObjectId() {
        return this.newMDN + this.oldMDN;
    }
}

