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
public class KnInstaPOCSrvcCfgDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676173L;

    private String pttServerId;
    private int wakeupTriggerInterval;
    private int numWakeupMsgPerTrigger;
    private int numOctetsPerWakeupMsg;
    private int numWakeupMsgsPerBurst;

    public int getNumWakeupMsgsPerBurst() {
        return numWakeupMsgsPerBurst;
    }

    public void setNumWakeupMsgsPerBurst(int numWakeupMsgsPerBurst) {
        this.numWakeupMsgsPerBurst = numWakeupMsgsPerBurst;
    }

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public int getWakeupTriggerInterval() {
        return wakeupTriggerInterval;
    }

    public void setWakeupTriggerInterval(int wakeupTriggerInterval) {
        this.wakeupTriggerInterval = wakeupTriggerInterval;
    }

    public int getNumWakeupMsgPerTrigger() {
        return numWakeupMsgPerTrigger;
    }

    public void setNumWakeupMsgPerTrigger(int numWakeupMsgPerTrigger) {
        this.numWakeupMsgPerTrigger = numWakeupMsgPerTrigger;
    }

    public int getNumOctetsPerWakeupMsg() {
        return numOctetsPerWakeupMsg;
    }

    public void setNumOctetsPerWakeupMsg(int numOctetsPerWakeupMsg) {
        this.numOctetsPerWakeupMsg = numOctetsPerWakeupMsg;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(super.toString());
        strBuffer.append(", pttServerId - ").append(pttServerId);
        strBuffer.append(", wakeupTriggerInterval - ").append(wakeupTriggerInterval);
        strBuffer.append(", numWakeupMsgPerTrigger - ").append(numWakeupMsgPerTrigger);
        strBuffer.append(", numOctetsPerWakeupMsg - ").append(numOctetsPerWakeupMsg);

        return strBuffer.toString();
    }

    public String getObjectId() {
        return pttServerId;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
