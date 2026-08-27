/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMExtSubscriberDTO;

import java.util.Collection;
import java.util.List;

/**
 * Created by Deepak on 28/3/14.
 */
public class KnXDMExtSubsResponseDTO implements IXDMResponseDTO {
    private static final long serialVersionUID = -8816239020735491626L;
    private String responseCode;
    private int responseStatus;
    private String responseMessage;
    private Collection responseDetails;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private List<KnXDMExtSubscriberDTO> extSubs;

    public KnXDMExtSubsResponseDTO() {
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public List<KnXDMExtSubscriberDTO> getExtSubs() {
        return extSubs;
    }

    public void setExtSubs(List<KnXDMExtSubscriberDTO> extSubs) {
        this.extSubs = extSubs;
    }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public String toString() {
        return "KnXDMExtSubsResponseDTO{" +
                "responseCode='" + responseCode + '\'' +
                ", responseStatus=" + responseStatus +
                ", responseMessage='" + responseMessage + '\'' +
                ", responseDetails=" + responseDetails +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", extSubs=" + extSubs +
                '}';
    }
}
