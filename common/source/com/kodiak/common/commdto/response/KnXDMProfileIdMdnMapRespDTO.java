/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

/**
 * ************************************************************************
 * <p>
 * File name:  KnSubsInfoList.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Ajit Kumar             Nov 15, 2019                8.1.2
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.Map;

public class KnXDMProfileIdMdnMapRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526471155622666666L;

    private Map<String, Map<Integer,String>> profileIdMdnMap;

    private String responseCode;
    private int responseStatus;
    private String responseMessage;
    private Collection responseDetails;
    private String objectId;
    private String transactionId;
    private String destPttServerId;
    private String destQueueName;


    public Map<String, Map<Integer, String>> getProfileIdMdnMap() {
        return profileIdMdnMap;
    }

    public void setProfileIdMdnMap(Map<String, Map<Integer, String>> profileIdMdnMap) {
        this.profileIdMdnMap = profileIdMdnMap;
    }

    @Override
    public String getResponseCode() {
        return responseCode;
    }

    @Override
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public int getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    @Override
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public Collection getResponseDetails() {
        return responseDetails;
    }

    @Override
    public void setResponseDetails(Collection responseDetails) {
        this.responseDetails = responseDetails;
    }

    @Override
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String getDestPttServerId() {
        return destPttServerId;
    }

    @Override
    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId = destPttServerId;
    }

    @Override
    public String getDestQueueName() {
        return destQueueName;
    }

    @Override
    public void setDestQueueName(String destQueueName) {
        this.destQueueName = destQueueName;
    }

    @Override
    public String toString() {
        return "KnXDMProfileIdMdnMapRespDTO{" +
                "profileIdMdnMap=" + KnGDPRTemplate.mcpttIdAndProfileMdnMap(profileIdMdnMap) +
                '}';
    }
}
