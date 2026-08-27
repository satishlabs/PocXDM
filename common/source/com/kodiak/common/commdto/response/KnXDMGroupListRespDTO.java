/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMGroupDTO;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMGroupListRespDTO.java
 * Subsystem:  Common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Feb 3, 2011           7.0
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
public class KnXDMGroupListRespDTO implements IXDMResponseDTO {

    private static final long serialVersionUID = 7526471155622676158L;

    private String responseCode;
    private int responseStatus;
    private String responseMessage;
    private Collection responseDetails;
    private String objectId;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;

    private Collection<KnXDMGroupDTO> groupList;


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

    public Collection<KnXDMGroupDTO> getGroupList() {
        return groupList;
    }

    public void setGroupList(Collection<KnXDMGroupDTO> groupList) {
        this.groupList = groupList;
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


    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(super.toString());

        strBuffer.append(", ResponseCode : ").append(responseCode);
        strBuffer.append(", ResponseStatus : ").append(responseStatus);
        strBuffer.append(", ResponseMessage : ").append(responseMessage);
        strBuffer.append(", ResponseDetails : ").append(responseDetails);
        strBuffer.append(", ObjectId : ").append(objectId);
        strBuffer.append(", Group List : ").append(groupList);
        strBuffer.append(", Dest_PTT_Server_ID - ").append(destPttServerId);
        strBuffer.append(", Dst_Queue_Name - ").append(destQueueName);
        strBuffer.append(", Transaction_ID - ").append(transactionId);

        return strBuffer.toString();

    }


}
