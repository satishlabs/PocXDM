/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMRLSRespDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Mar 11, 2011           7.0
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
 * ************************************************************************
 */
public class KnXDMRLSRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526471155622676161L;
    //stores the response Code
    private String responseCode;
    //stores the responseStatus
    private int responseStatus;
    //stores the response message;
    private String responseMessage;
    //Stores the response details;
    private Collection responseDetails;

    private Collection<KnXDMRLSServiceDTO> services;

    private long rlsEtag;

    private String destPttServerId;

    private String destQueueName;
    private String transactionId;

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
        return responseCode;
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
    public Collection<KnXDMRLSServiceDTO> getServices() {
        return this.services;
    }

    /**
     * @param services
     */
    public void setServices(Collection<KnXDMRLSServiceDTO> services) {
        this.services = services;
    }

    /**
     * @return
     */
    public long getRlsEtag() {
        return rlsEtag;
    }

    /**
     * @param rlsEtag
     */
    public void setRlsEtag(long rlsEtag) {
        this.rlsEtag = rlsEtag;
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

    /**
     * getter method for transaction ID
     *
     * @return String
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * setter method for Transaction Id
     *
     * @param transactionId String
     */
    public void setTransactionId(String transactionId) {
        if (transactionId != null) {
            transactionId = transactionId.trim();
            if (transactionId.equals("")) {
                transactionId = null;
            }
        }
        this.transactionId = transactionId;
    }


    /**
     * @return
     */
    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(super.toString());
        strBuffer.append(" Response_Code - ").append(responseCode)
                .append(", Response_Status - ").append(responseStatus)
                .append(", Response_Message - ").append(responseMessage)
                .append(", Response_Details - ").append(responseDetails)
                .append(", Services - ").append(services)
                .append(", Dest_Queue_Name - ").append(destQueueName)
                .append(", Dest_PTT_Server_ID - ").append(destPttServerId)
                .append(", Rls Etag - ").append(rlsEtag);
        strBuffer.append(", Transaction_ID - ").append(transactionId);

        return strBuffer.toString();
    }

}
