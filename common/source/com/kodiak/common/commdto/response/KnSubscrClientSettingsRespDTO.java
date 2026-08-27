/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnSubClientSettingsDTO;

import java.util.Collection;

public class KnSubscrClientSettingsRespDTO extends KnSubClientSettingsDTO implements IXDMResponseDTO{


    private static final long serialVersionUID = 7526471155622676164L;

    public int serviceAuthStatus;
    //stores the response Code
    private String responseCode;
    //stores the responseStatus
    private int responseStatus;
    //stores the response Message
    private String responseMessage;

    private Collection responseDetails;

    private String destPttServerId;

    private String destQueueName;

    private String transactionId;

    private String mdn;

    private String recordingStatus;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String  getRecordingStatus() {
        return recordingStatus;
    }

    public void setRecordingStatus(String clientSettings) { this.recordingStatus = recordingStatus; }

    @Override
    public String getResponseCode() {
        return responseCode;
    }

    @Override
    public void setResponseCode(String responseCode) {
        this.responseCode=responseCode;
    }

    @Override
    public int getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void setResponseStatus(int responseStatus) {
        this.responseCode=responseCode;
    }

    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    @Override
    public void setResponseMessage(String responseMessage) {
        this.responseMessage=responseMessage;
    }

    @Override
    public Collection getResponseDetails() {
        return responseDetails;
    }

    @Override
    public void setResponseDetails(Collection responseDetails) {
        this.responseDetails=responseDetails;
    }

    @Override
    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId=destPttServerId;
    }

    @Override
    public String getDestPttServerId() {
        return destPttServerId;
    }

    @Override
    public void setDestQueueName(String destQueueName) {
        this.destQueueName=destQueueName;
    }

    @Override
    public String getDestQueueName() {
        return destQueueName;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId=transactionId;
    }

    @Override
    public String getObjectId() {
        return getObjectId();
    }
}
