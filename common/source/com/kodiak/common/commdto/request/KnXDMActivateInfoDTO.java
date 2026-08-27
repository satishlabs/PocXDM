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

import java.util.Map;

public class KnXDMActivateInfoDTO extends KnXDMSubsInfoDTO {

    private static final long serialVersionUID = 7526471155622676136L;

    private String realm;

    private String userAgent;

    private Long clientFeatureSet;

    private Integer subsClientType;

    private KnUserAgentDTO userAgentDTO;

    private Integer swType;

    private Integer platformType;

    private Map<Integer,Integer> vocoderIdMap;

    private String activationKey;

    private String serviceName;

    private String authKey;

    private String deviceId;
    private String clientFeatureSet2;
    private String mcptt_id;


    /**
     * getter method for the Realm
     *
     * @return String
     */
    public String getRealm() {
        return realm;
    }

    /**
     * setter method for the Realm
     *
     * @param realm String
     */
    public void setRealm(String realm) {
        this.realm = realm;
    }

    /**
     * getter method for the User Agent
     *
     * @return String
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * setter method for the User Agent
     *
     * @param userAgent String
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * getter method for Client Feature Set
     *
     * @return long
     */
    public Long getClientFeatureSet() {
        return clientFeatureSet;
    }

    /**
     * setter method for Client Feature Set
     *
     * @param clientFeatureSet long
     */
    public void setClientFeatureSet(Long clientFeatureSet) {
        this.clientFeatureSet = clientFeatureSet;
    }

    /**
     * getter method for Subs Client Type
     *
     * @return int
     */
    public Integer getSubsClientType() {
        return subsClientType;
    }

    /**
     * setter method for the Subs Client Type
     *
     * @param subsClientType int
     */
    public void setSubsClientType(Integer subsClientType) {
        this.subsClientType = subsClientType;
    }

    public KnUserAgentDTO getUserAgentDTO() {
        return userAgentDTO;
    }

    public void setUserAgentDTO(KnUserAgentDTO userAgentDTO) {
        this.userAgentDTO = userAgentDTO;
    }

    public Integer getSwType() {
        return swType;
    }

    public void setSwType(Integer swType) {
        this.swType = swType;
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public Map<Integer, Integer> getVocoderIdMap() {
        return vocoderIdMap;
    }

    public void setVocoderIdMap(Map<Integer, Integer> vocoderIdMap) {
        this.vocoderIdMap = vocoderIdMap;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getClientFeatureSet2() {
		return clientFeatureSet2;
	}

	public void setClientFeatureSet2(String clientFeatureSet2) {
		this.clientFeatureSet2 = clientFeatureSet2;
	}

	public String getMcptt_id() {
		return mcptt_id;
	}

	public void setMcptt_id(String mcptt_id) {
		this.mcptt_id = mcptt_id;
	}

	public String toString() {
        StringBuilder strBuffer = new StringBuilder(50);
        strBuffer.append(super.toString());
        strBuffer.append(", [KnXDMActivateInfo - ")
                .append(", User Agent - ").append(userAgent)
                .append(", Client_Feature_Set - ").append(clientFeatureSet)
                .append(", Subscriber_Client_Type - ").append(subsClientType)
                .append(", UserAgent_DTO - ").append(userAgentDTO)
                .append(", swType - ").append(swType)
                .append(", platformType- ").append(platformType)
                .append(", vocoderIdMap - ").append(vocoderIdMap)
                .append(", activationKey - ").append(activationKey)
                .append(", serviceName - ").append(serviceName)
                .append(", authKey - ").append(authKey)
                .append(", deviceId - ").append(deviceId)
                .append(", clientFeatureSet2 - ").append(clientFeatureSet2)
                .append("]");

        return strBuffer.toString();
    }
}