/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMRespDTO.java
 * Subsystem:   COMMON DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/22/10       7.0
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

import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KnXDMRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526471155622676160L;
    //stores the response Code
    private String responseCode;
    //stores the responseStatus
    private int responseStatus;
    //stores the response message;
    private String responseMessage;
    //Stores the response details;
    private Collection responseDetails;
    //Stores the response etag;
    private long etag;
    private String objectId;

    private String transactionId;

    private String mdn;

    private Map<String, Object> customParamMap;

    private String destPttServerId;

    private String destQueueName;

    private Collection<KnXDMFailureRespDTO> failureDetails;

    private Map<String, Boolean> mdnListMap;
    private String internalDeviceId;

    private List<String> notifiedMdnList;

    /**
     * getter method for response code
     *
     * @return String
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * setter method for Response code
     *
     * @param responseCode String
     */
    public void setResponseCode(String responseCode) {
        if (responseCode != null) {
            responseCode = responseCode.trim();
            if (responseCode.equals("")) {
                responseCode = null;
            }
        }
        this.responseCode = responseCode;
    }

    /**
     * getter method for Response Status
     *
     * @return String
     */
    public int getResponseStatus() {
        return responseStatus;
    }

    /**
     * setter method for Response Status
     *
     * @param responseStatus String
     */
    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    /**
     * getter method for the response Message
     *
     * @return String
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * setter method for the response Message
     *
     * @param responseMessage String
     */
    public void setResponseMessage(String responseMessage) {
        if (responseMessage != null) {
            responseMessage = responseMessage.trim();
            if (responseMessage.equals("")) {
                responseMessage = null;
            }
        }
        this.responseMessage = responseMessage;
    }

    public String getObjectId() {
        return objectId;
    }

    /**
     * getter method for the Response Details
     *
     * @return Collection
     */
    public Collection getResponseDetails() {
        return responseDetails;
    }

    /**
     * setter method for the Response Details
     *
     * @param responseDetails Collection
     */
    public void setResponseDetails(Collection responseDetails) {
        this.responseDetails = responseDetails;
    }

    /**
     * @return
     */
    public long getEtag() {
        return etag;
    }

    /**
     * @param etag
     */
    public void setEtag(long etag) {
        this.etag = etag;
    }

    /**
     * getter method for Custom Param Map
     *
     * @return Map
     */
    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    /**
     * setter method for Custom Param Map
     *
     * @param customParamMap Map
     */
    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        if (transactionId != null) {
            transactionId = transactionId.trim();
            if (transactionId.equals("")) {
                transactionId = null;
            }
        }
        this.transactionId = transactionId;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        if (mdn != null) {
            mdn = mdn.trim();
            if (mdn.equals("")) {
                mdn = null;
            }
        }
        this.mdn = mdn;
    }

    public String getDestPttServerId() {
        return destPttServerId;
    }

    public void setDestPttServerId(String destPttServerId) {
        if (destPttServerId != null) {
            destPttServerId = destPttServerId.trim();
            if (destPttServerId.equals("")) {
                destPttServerId = null;
            }
        }
        this.destPttServerId = destPttServerId;
    }

    public String getDestQueueName() {
        return destQueueName;
    }

    public void setDestQueueName(String destQueueName) {
        if (destQueueName != null) {
            destQueueName = destQueueName.trim();
            if (destQueueName.equals("")) {
                destQueueName = null;
            }
        }
        this.destQueueName = destQueueName;
    }

    public Collection<KnXDMFailureRespDTO> getFailureDetails() {
        return failureDetails;
    }

    public void setFailureDetails(Collection<KnXDMFailureRespDTO> failureDetails) {
        this.failureDetails = failureDetails;
    }

    public Map<String, Boolean> getMdnListMap() {
        return mdnListMap;
    }

    public void setMdnListMap(Map<String, Boolean> mdnListMap) {
        this.mdnListMap = mdnListMap;
    }

    public String getInternalDeviceId() {
        return internalDeviceId;
    }

    public void setInternalDeviceId(String internalDeviceId) {
        this.internalDeviceId = internalDeviceId;
    }

    public List<String> getNotifiedMdnList() { return notifiedMdnList; }

    public void setNotifiedMdnList(List<String> notifiedMdnList) { this.notifiedMdnList = notifiedMdnList; }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(" Response_Code - ").append(responseCode)
                .append(", Response_Status - ").append(responseStatus)
                .append(", Response_Message - ").append(responseMessage)
                .append(", Response_Details - ").append(responseDetails)
                .append(", Etag - ").append(etag)
                .append(", Object_Id - ").append(objectId)
                .append(", Transaction_Id - ").append(transactionId)
                .append(", mdn - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", Dest_PTT_Server_ID - ").append(destPttServerId)
                .append(", Dest_Queue_Name - ").append(destQueueName)
                .append(", failureDetails - ").append(failureDetails)
                .append(", InternalDeviceId - ").append(internalDeviceId)
                .append(", mdnListMap - ").append(KnGDPRTemplate.mapKeyMdn(mdnListMap))
                .append(", notifiedMdnList - ").append(KnGDPRTemplate.mdnList(notifiedMdnList));

        return strBuffer.toString();
    }

}
