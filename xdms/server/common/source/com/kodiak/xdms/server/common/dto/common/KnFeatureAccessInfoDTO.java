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
 * Ajit kumar           Aug 06, 2012       7.2
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
public class KnFeatureAccessInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676191L;

    private int featureAccessIndex;
    private String featureName;
    private String accessSignalType;
    private int accessTransportSubsystem;
    private int routetoSubsystem;
    private int servicedBySubsystem;
    private int accessNumberType;
    private int multiAccessNumberEnabled;
    private String description;
    private int isAccessNumberDedicated;
    private int ton;

    public int getFeatureAccessIndex() {
        return featureAccessIndex;
    }

    public void setFeatureAccessIndex(int featureAccessIndex) {
        this.featureAccessIndex = featureAccessIndex;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getAccessSignalType() {
        return accessSignalType;
    }

    public void setAccessSignalType(String accessSignalType) {
        this.accessSignalType = accessSignalType;
    }

    public int getAccessTransportSubsystem() {
        return accessTransportSubsystem;
    }

    public void setAccessTransportSubsystem(int accessTransportSubsystem) {
        this.accessTransportSubsystem = accessTransportSubsystem;
    }

    public int getRoutetoSubsystem() {
        return routetoSubsystem;
    }

    public void setRoutetoSubsystem(int routetoSubsystem) {
        this.routetoSubsystem = routetoSubsystem;
    }

    public int getServicedBySubsystem() {
        return servicedBySubsystem;
    }

    public void setServicedBySubsystem(int servicedBySubsystem) {
        this.servicedBySubsystem = servicedBySubsystem;
    }

    public int getAccessNumberType() {
        return accessNumberType;
    }

    public void setAccessNumberType(int accessNumberType) {
        this.accessNumberType = accessNumberType;
    }

    public int getMultiAccessNumberEnabled() {
        return multiAccessNumberEnabled;
    }

    public void setMultiAccessNumberEnabled(int multiAccessNumberEnabled) {
        this.multiAccessNumberEnabled = multiAccessNumberEnabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAccessNumberDedicated() {
        return isAccessNumberDedicated;
    }

    public void setAccessNumberDedicated(int accessNumberDedicated) {
        isAccessNumberDedicated = accessNumberDedicated;
    }

    public int getTon() {
        return ton;
    }

    public void setTon(int ton) {
        this.ton = ton;
    }


    public String toString() {
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", featureAccessIndex - ").append(featureAccessIndex);
        strBuffer.append(", featureName - ").append(featureName);
        strBuffer.append(", accessSignalType - ").append(accessSignalType);
        strBuffer.append(", accessTransportSubsystem - ").append(accessTransportSubsystem);
        strBuffer.append(", routetoSubsystem - ").append(routetoSubsystem);
        strBuffer.append(", servicedBySubsystem - ").append(servicedBySubsystem);
        strBuffer.append(", accessNumberType - ").append(accessNumberType);
        strBuffer.append(", multiAccessNumberEnabled - ").append(multiAccessNumberEnabled);
        strBuffer.append(", description - ").append(description);
        strBuffer.append(", isAccessNumberDedicated - ").append(isAccessNumberDedicated);
        strBuffer.append(", ton - ").append(ton);
        return strBuffer.toString();
    }

    public String getObjectId() {
        return String.valueOf(featureAccessIndex);  //To change body of implemented methods use File | Settings | File Templates.
    }
}

