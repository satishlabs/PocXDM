/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMPAMRespDTO.java
 * Subsystem:   COMMON DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar          feb 27 2013       7.4
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

package com.kodiak.common.commdto.response;


import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.kodiak.common.commdto.request.KnXDMPAMAccInfoDTO;
import com.kodiak.common.resources.KnConstants;

public class KnXDMPAMRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526471155622907567L;
    private String billingNumber;

    private int pamStatus;
    private KnConstants.HIERARCHY_TYPE hierarchyType;

    private List<KnXDMSubsProfileRespDTO> subProfileList;
    //stores the response Code
    private String responseCode;
    //stores the responseStatus
    private int responseStatus;
    //stores the response message;
    private String responseMessage;
    //Stores the response details;
    private Collection responseDetails;

    private String objectId;

    private String transactionId;

    private Map<String, Object> customParamMap;

    private String destPttServerId;

    private String destQueueName;

    private KnXDMPAMAccInfoDTO knXDMPAMAccInfoDTO;

    public KnXDMPAMAccInfoDTO getKnXDMPAMAccInfoDTO() {
        return knXDMPAMAccInfoDTO;
    }

    public void setKnXDMPAMAccInfoDTO(KnXDMPAMAccInfoDTO knXDMPAMAccInfoDTO) {
        this.knXDMPAMAccInfoDTO = knXDMPAMAccInfoDTO;
    }



    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Collection getResponseDetails() {
        return responseDetails;
    }

    public void setResponseDetails(Collection responseDetails) {
        this.responseDetails = responseDetails;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String getDestPttServerId() {
        return destPttServerId;
    }

    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId = destPttServerId;
    }

    public String getDestQueueName() {
        return destQueueName;
    }

    public void setDestQueueName(String destQueueName) {
        this.destQueueName = destQueueName;
    }

    public String getBillingNumber() {
        return billingNumber;
    }

    public void setBillingNumber(String billingNumber) {
        this.billingNumber = billingNumber;
    }

    public int getPamStatus() {
        return pamStatus;
    }

    public void setPamStatus(int pamStatus) {
        this.pamStatus = pamStatus;
    }

    public List<KnXDMSubsProfileRespDTO> getSubProfileList() {
        return subProfileList;
    }

    public void setSubProfileList(List<KnXDMSubsProfileRespDTO> subProfileList) {
        this.subProfileList = subProfileList;
    }

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(" responseCode - ").append(responseCode)
                .append(", knXDMPAMAccInfoDTO - ").append(knXDMPAMAccInfoDTO)
                .append(", responseStatus - ").append(responseStatus);

        return strBuffer.toString();
    }

 /*
    public String toString() {
        return "KnXDMPAMRespDTO{" +
                "extpamAccId='" + extpamAccId + '\'' +
                ", pamStatus=" + pamStatus +
                ", subProfileList=" + subProfileList +
                ", responseCode='" + responseCode + '\'' +
                ", responseStatus=" + responseStatus +
                ", responseMessage='" + responseMessage + '\'' +
                ", responseDetails=" + responseDetails +
                ", objectId='" + objectId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", customParamMap=" + customParamMap +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                '}';
    }*/

}
