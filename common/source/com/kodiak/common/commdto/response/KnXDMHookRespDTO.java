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

import java.util.Collection;
import java.util.List;

public class KnXDMHookRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526471155622676190L;
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

    private List<String> mdns;

    private String destPttServerId;

    private String destQueueName;


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

    public List<String> getMdns() {
        return mdns;
    }

    public void setMdns(List<String> mdns) {
        this.mdns = mdns;
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

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(" Response_Code - ").append(responseCode)
                .append(", Response_Status - ").append(responseStatus)
                .append(", Response_Message - ").append(responseMessage)
                .append(", Response_Details - ").append(responseDetails)
                .append(", Etag - ").append(etag)
                .append(", Object_Id - ").append(objectId)
                .append(", Transaction_Id - ").append(transactionId)

                .append(", Dest_PTT_Server_ID - ").append(destPttServerId)
                .append(", Dest_Queue_Name - ").append(destQueueName) ;

        return strBuffer.toString();
    }

}
