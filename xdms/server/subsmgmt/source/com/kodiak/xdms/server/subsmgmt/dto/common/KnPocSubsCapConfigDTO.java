/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ***********************************************************************
 * <p/>
 * File name:  KnPocSubsCapConfigDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker        7/6/13        7.5
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
 * ***********************************************************************
 */

package com.kodiak.xdms.server.subsmgmt.dto.common;


import com.kodiak.common.dto.IIdentifier;

public class KnPocSubsCapConfigDTO implements IIdentifier {

    private String pttServerId;
    private int maxSubsLimit;
    private int allowProv;

    /**
     * getter method for PttServerId (POC)
     *
     * @return String poc PttServerId
     */
    public String getPttServerId() {
        return pttServerId;
    }

    /**
     * setter method for pttServerId
     *
     * @param pttServerId Poc PttServer ID
     */
    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    /**
     * getter method for Max Subscriber Limit
     *
     * @return String
     */
    public int getMaxSubsLimit() {
        return maxSubsLimit;
    }

    /**
     * setter method for the max Subscriber limit per POC
     *
     * @param maxSubsLimit int
     */
    public void setMaxSubsLimit(int maxSubsLimit) {
        this.maxSubsLimit = maxSubsLimit;
    }

    /**
     * getter method for the allow Prov Enum Value
     *
     * @return int (enum value)
     */
    public int getAllowProv() {
        return allowProv;
    }

    /**
     * setter method for the allow Prov Enum Value
     *
     * @param allowProv int (enum value)
     */
    public void setAllowProv(int allowProv) {
        this.allowProv = allowProv;
    }

    /**
     * @return
     */
    public String getObjectId() {
        return pttServerId;
    }


    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("KnPocSubsCapConfigDTO[")
                .append("pttServerId - ").append(pttServerId)
                .append(", maxSubsLimit - ").append(maxSubsLimit)
                .append(", allowProv - ").append(allowProv)
                .append("]");
        return strBuilder.toString();
    }
}
