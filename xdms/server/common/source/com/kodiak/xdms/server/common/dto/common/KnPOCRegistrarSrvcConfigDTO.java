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
public class KnPOCRegistrarSrvcConfigDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676174L;

    private String pttServerId;
    private String primaryRegistrarURI;
    private String geoRegistrarURI;
    private int enableRoamingStatusCheck;
    private int maxRegisterExpiryTimeDuration;
    private int locationDebounceTimer;
    private int enableTUFeature;
    private int enableTUSMSFlag;
    private int tuDownTimer;
    private int tuUpTimerStartVal;
    private int tuUpTimerMaxVal;
    private int tuUpTimerRampDownPeriod;
    private int tuForceOnlineMaxWaitTimer;

    public int getMaxRegisterExpiryTimeDuration() {
        return maxRegisterExpiryTimeDuration;
    }

    public void setMaxRegisterExpiryTimeDuration(int maxRegisterExpiryTimeDuration) {
        this.maxRegisterExpiryTimeDuration = maxRegisterExpiryTimeDuration;
    }

    public int getLocationDebounceTimer() {
        return locationDebounceTimer;
    }

    public void setLocationDebounceTimer(int locationDebounceTimer) {
        this.locationDebounceTimer = locationDebounceTimer;
    }

    public String getPttServerId() {
        return pttServerId;
    }

    public void setPttServerId(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public String getPrimaryRegistrarURI() {
        return primaryRegistrarURI;
    }

    public void setPrimaryRegistrarURI(String primaryRegistrarURI) {
        this.primaryRegistrarURI = primaryRegistrarURI;
    }

    public String getGeoRegistrarURI() {
        return geoRegistrarURI;
    }

    public void setGeoRegistrarURI(String geoRegistrarURI) {
        this.geoRegistrarURI = geoRegistrarURI;
    }

    public int getEnableRoamingStatusCheck() {
        return enableRoamingStatusCheck;
    }

    public void setEnableRoamingStatusCheck(int enableRoamingStatusCheck) {
        this.enableRoamingStatusCheck = enableRoamingStatusCheck;
    }

    public int getEnableTUFeature() {
        return enableTUFeature;
    }

    public void setEnableTUFeature(int enableTUFeature) {
        this.enableTUFeature = enableTUFeature;
    }

    public int getEnableTUSMSFlag() {
        return enableTUSMSFlag;
    }

    public void setEnableTUSMSFlag(int enableTUSMSFlag) {
        this.enableTUSMSFlag = enableTUSMSFlag;
    }

    public int getTuDownTimer() {
        return tuDownTimer;
    }

    public void setTuDownTimer(int tuDownTimer) {
        this.tuDownTimer = tuDownTimer;
    }

    public int getTuUpTimerStartVal() {
        return tuUpTimerStartVal;
    }

    public void setTuUpTimerStartVal(int tuUpTimerStartVal) {
        this.tuUpTimerStartVal = tuUpTimerStartVal;
    }

    public int getTuUpTimerMaxVal() {
        return tuUpTimerMaxVal;
    }

    public void setTuUpTimerMaxVal(int tuUpTimerMaxVal) {
        this.tuUpTimerMaxVal = tuUpTimerMaxVal;
    }

    public int getTuUpTimerRampDownPeriod() {
        return tuUpTimerRampDownPeriod;
    }

    public void setTuUpTimerRampDownPeriod(int tuUpTimerRampDownPeriod) {
        this.tuUpTimerRampDownPeriod = tuUpTimerRampDownPeriod;
    }

    public int getTuForceOnlineMaxWaitTimer() {
        return tuForceOnlineMaxWaitTimer;
    }

    public void setTuForceOnlineMaxWaitTimer(int tuForceOnlineMaxWaitTimer) {
        this.tuForceOnlineMaxWaitTimer = tuForceOnlineMaxWaitTimer;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", pttServerId - ").append(pttServerId);
        strBuffer.append(", primaryRegistrarURI - ").append(primaryRegistrarURI);
        strBuffer.append(", geoRegistrarURI - ").append(geoRegistrarURI);
        strBuffer.append(", enableRoamingStatusCheck - ").append(enableRoamingStatusCheck);
        strBuffer.append(", maxRegisterExpiryTimeDuration - ").append(maxRegisterExpiryTimeDuration);
        strBuffer.append(", locationDebounceTimer - ").append(locationDebounceTimer);

        strBuffer.append(", enableTUFeature - ").append(enableTUFeature);
        strBuffer.append(", enableTUSMSFlag - ").append(enableTUSMSFlag);
        strBuffer.append(", tuDownTimer - ").append(tuDownTimer);
        strBuffer.append(", tuUpTimerStartVal - ").append(tuUpTimerStartVal);
        strBuffer.append(", tuUpTimerMaxVal - ").append(tuUpTimerMaxVal);
        strBuffer.append(", tuUpTimerRampDownPeriod - ").append(tuUpTimerRampDownPeriod);
        strBuffer.append(", tuForceOnlineMaxWaitTimer - ").append(tuForceOnlineMaxWaitTimer);


        return strBuffer.toString();
    }

    public String getObjectId() {
        return pttServerId;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

