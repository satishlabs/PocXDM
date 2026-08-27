/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.util.Collection;

public class KnXDMGetCorporateAccountDetailsRespDTO implements IXDMResponseDTO {
    private static final long serialVersionUID = -4406225978197622289L;
    private String responseCode;
    private int responseStatus;
    private String responseMessage;
    private Collection responseDetails;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private KnXDMCorpProfile corpProfile;

    public KnXDMCorpProfile getCorpProfile() {
        return corpProfile;
    }

    public void setCorpProfile(KnXDMCorpProfile corpProfile) {
        this.corpProfile = corpProfile;
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
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public String toString() {
        return "KnXDMGetCorporateAccountDetailsRespDTO{" +
                "responseCode='" + responseCode + '\'' +
                ", responseStatus=" + responseStatus +
                ", responseMessage='" + responseMessage + '\'' +
                ", responseDetails=" + responseDetails +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", corpProfile=" + corpProfile +
                '}';
    }


}
