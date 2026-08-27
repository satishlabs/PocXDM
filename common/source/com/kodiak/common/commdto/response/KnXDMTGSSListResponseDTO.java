/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by venkata sudhakar talluri on 28-12-2018
 */
public class KnXDMTGSSListResponseDTO implements IXDMResponseDTO{

    private static final long serialVersionUID = -237157840999515932L;
    public String mdn;
    public int corpId;
    public List<Integer> groupIds;
    private Map<Integer,Integer> groupIdCorpInfo;
    private String docEtag;
    private int responseStatus = 1;
    private String responseCode;
    private String responseMessage;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;


    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public List<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Integer> groupIds) { this.groupIds = groupIds; }

    public String getDocEtag() {
        return docEtag;
    }

    public void setDocEtag(String docEtag) {
        this.docEtag = docEtag;
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
    public String getResponseCode() {
        return responseCode;
    }

    @Override
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
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
    public Collection getResponseDetails() {
        return null;
    }

    @Override
    public void setResponseDetails(Collection responseDetails) {

    }

    @Override
    public String getObjectId() {
        return mdn;
    }

    public Map<Integer, Integer> getGroupIdCorpInfo() { return groupIdCorpInfo; }

    public void setGroupIdCorpInfo(Map<Integer, Integer> groupIdCorpInfo) { this.groupIdCorpInfo = groupIdCorpInfo; }

    @Override
    public String toString() {
        return "KnXDMTGSSListResponseDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", corpId=" + corpId +
                ", groupIds=" + groupIds +
                ", groupIdCorpInfo=" + groupIdCorpInfo +
                ", docEtag='" + docEtag + '\'' +
                '}';
    }
}
