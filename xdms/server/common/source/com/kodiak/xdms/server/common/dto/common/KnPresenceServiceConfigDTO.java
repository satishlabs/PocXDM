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

public class KnPresenceServiceConfigDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676177L;

    private String pttServerId;
    private String primaryPresenceServerURI;
//    private String geoPresenceServerURI;
    private int rLS_SubscriptionValidity;
    private int enablePRInPoC;

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public int getPresencePublishThrottleTimer() {
        return presencePublishThrottleTimer;
    }

    public void setPresencePublishThrottleTimer(int presencePublishThrottleTimer) {
        this.presencePublishThrottleTimer = presencePublishThrottleTimer;
    }

    public String getPrimaryPresenceServerURI() {
        return primaryPresenceServerURI;
    }

    public void setPrimaryPresenceServerURI(String primaryPresenceServerURI) {
        this.primaryPresenceServerURI = primaryPresenceServerURI;
    }

//    public String getGeoPresenceServerURI() {
//        return geoPresenceServerURI;
//    }
//
//    public void setGeoPresenceServerURI(String geoPresenceServerURI) {
//        this.geoPresenceServerURI = geoPresenceServerURI;
//    }

    public int getRLS_SubscriptionValidity() {
        return rLS_SubscriptionValidity;
    }

    public void setRLS_SubscriptionValidity(int rLS_SubscriptionValidity) {
        this.rLS_SubscriptionValidity = rLS_SubscriptionValidity;
    }

    public int getPresencePublishValidity() {
        return presencePublishValidity;
    }

    public void setPresencePublishValidity(int presencePublishValidity) {
        this.presencePublishValidity = presencePublishValidity;
    }

    public int getMax_SIPNotifyMTUSize() {
        return max_SIPNotifyMTUSize;
    }

    public void setMax_SIPNotifyMTUSize(int max_SIPNotifyMTUSize) {
        this.max_SIPNotifyMTUSize = max_SIPNotifyMTUSize;
    }

    public int getRLS_NotifyThrottleTimer() {
        return rLS_NotifyThrottleTimer;
    }

    public void setRLS_NotifyThrottleTimer(int rLS_NotifyThrottleTimer) {
        this.rLS_NotifyThrottleTimer = rLS_NotifyThrottleTimer;
    }

    private int presencePublishValidity;
    private int max_SIPNotifyMTUSize;
    private int rLS_NotifyThrottleTimer;
    private int presencePublishThrottleTimer;

    /**
     * getter method for enable pr in poc value
     *
     * @return
     */
    public int getEnablePRInPoC() {
        return enablePRInPoC;
    }

    /**
     * setter method for enable pr in poc value
     *
     * @param enablePRInPoC
     */
    public void setEnablePRInPoC(int enablePRInPoC) {
        this.enablePRInPoC = enablePRInPoC;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", pttServerId - ").append(pttServerId);
        strBuffer.append(", primaryPresenceServerURI - ").append(primaryPresenceServerURI);
//        strBuffer.append(", geoPresenceServerURI - ").append(geoPresenceServerURI);
        strBuffer.append(", rLS_SubscriptionValidity - ").append(rLS_SubscriptionValidity);
        strBuffer.append(", presencePublishValidity - ").append(presencePublishValidity);
        strBuffer.append(", max_SIPNotifyMTUSize - ").append(max_SIPNotifyMTUSize);
        strBuffer.append(", rLS_NotifyThrottleTimer - ").append(rLS_NotifyThrottleTimer);
        strBuffer.append(", presencePublishThrottleTimer - ").append(presencePublishThrottleTimer);
        strBuffer.append(", Enabled PR IN POC - ").append(enablePRInPoC);
        return strBuffer.toString();
    }


    public String getObjectId() {
        return pttServerId;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
