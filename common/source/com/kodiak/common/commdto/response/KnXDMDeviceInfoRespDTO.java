/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * 
 * ***********************************************************************
 *  File name: KnXDMDeviceInfoRespDTO.java
 *  
 *  Name                 	Date         	   Release
 *  -------------------- -------------------- -------------------------------
 *  Kumar Abhinav        02-Jan-2020, 2:04:51 am      10.0
 *
 *  Copyright (c) 2019 Kodiak, A Motorola Solutions Company
 *  9th floor, MFar, Manayata Tech Park,
 *  Greenheart Phase IV,Nagawara
 *  Bangalore - 560 045
 *  www.kodiakptt.com
 *  All Rights Reserved.
 *
 *  This software is the confidential and proprietary information of Kodiak
 *  Networks, Inc. You shall not disclose such confidential information and
 *  shall use it only in accordance with the terms of the license agreement
 *  you entered into with Kodiak Networks.
 * ***********************************************************************
 */
package com.kodiak.common.commdto.response;

import java.util.Collection;

import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;

public class KnXDMDeviceInfoRespDTO extends KnXDMDeviceProvDTO implements IXDMResponseDTO {
	private static final long serialVersionUID = 7526471155622678952L;
	private String responseCode;
    private int responseStatus;
    private String responseMessage;
    private Collection responseDetails;
    private String destPttServerId;

    private String destQueueName;

    private String transactionId;

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
     * getter method for the Response Message
     *
     * @return String
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * setter method for the Response Message
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

    public String getObjectId() {
        return transactionId;
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

	@Override
	public String toString() {
		return "KnXDMDeviceInfoRespDTO [responseCode=" + responseCode + ", responseStatus=" + responseStatus
				+ ", responseMessage=" + responseMessage + ", responseDetails=" + responseDetails + ", destPttServerId="
				+ destPttServerId + ", destQueueName=" + destQueueName + ", transactionId=" + transactionId + "]";
	}    
    
}
