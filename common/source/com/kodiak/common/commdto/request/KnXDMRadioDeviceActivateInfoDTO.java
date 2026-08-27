/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnXDMActivateInfoDTO.java
 * Subsystem:   Common Communication DTO
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       1/9/11   7.0
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
 * *************************************************************************
 */

package com.kodiak.common.commdto.request;


import com.kodiak.common.commdto.common.KnUserAgentDTO;

public class KnXDMRadioDeviceActivateInfoDTO extends KnXDMSubsInfoDTO {

    private static final long serialVersionUID = 7526471155622676136L;

    private String deviceId;

    private String devicePassword;

    private String deviceClientId;

    private String realm;

    private String userAgent;

    private KnUserAgentDTO userAgentDTO;

    private String serviceName;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDevicePassword() {
        return devicePassword;
    }

    public void setDevicePassword(String devicePassword) {
        this.devicePassword = devicePassword;
    }

    public String getDeviceClientId() {
        return deviceClientId;
    }

    public void setDeviceClientId(String deviceClientId) {
        this.deviceClientId = deviceClientId;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public KnUserAgentDTO getUserAgentDTO() {
        return userAgentDTO;
    }

    public void setUserAgentDTO(KnUserAgentDTO userAgentDTO) {
        this.userAgentDTO = userAgentDTO;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return super.toString()+
                "KnXDMRadioDeviceActivateInfoDTO{" +
                "deviceId='" + deviceId + '\'' +
                ", devicePassword='" + devicePassword + '\'' +
                ", deviceClientId='" + deviceClientId + '\'' +
                ", realm='" + realm + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", userAgentDTO=" + userAgentDTO +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }
}