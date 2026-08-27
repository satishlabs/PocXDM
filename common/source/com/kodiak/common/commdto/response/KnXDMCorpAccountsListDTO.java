/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.resources.KnConstants;

import java.util.Collection;
import java.util.List;

public class KnXDMCorpAccountsListDTO implements IXDMResponseDTO, IXDMRequestDTO
{
    private static final long serialVersionUID = 5883431567908772077L;

    private List<KnXDMCorpAccountsList> xdmCorpAccList;

    private String responseCode;
    private int responseStatus;
    private String responseMessage;
    private Collection responseDetails;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private String txnId;
    private String fetchSize;
    private String nextToken;

    public List<KnXDMCorpAccountsList> getXdmCorpAccList() {
        return xdmCorpAccList;
    }

    public void setXdmCorpAccList(List<KnXDMCorpAccountsList> xdmCorpAccList) {
        this.xdmCorpAccList = xdmCorpAccList;
    }

    public String getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(String responseCode) {
        if (responseCode != null) {
            responseCode = responseCode.trim();
            if (responseCode.equals("")) {
                responseCode = null;
            }
        }

        this.responseCode = responseCode;
    }

    public int getResponseStatus() {
        return this.responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        if (responseMessage != null) {
            responseMessage = responseMessage.trim();
            if (responseMessage.equals("")) {
                responseMessage = null;
            }
        }

        this.responseMessage = responseMessage;
    }

    public Collection getResponseDetails() {
        return this.responseDetails;
    }

    public void setResponseDetails(Collection responseDetails) {
        this.responseDetails = responseDetails;
    }

    public String getObjectId() {
        return this.transactionId;
    }

    public String getDestPttServerId() {
        return this.destPttServerId;
    }

    @Override
    public String getOperationType() {
        return "";
    }

    @Override
    public void setOperationType(String operationType) {

    }

    @Override
    public int getClientType() {
        return 0;
    }

    @Override
    public void setClientType(int clientType) {

    }

    @Override
    public IAuthDTO getAuthDTO() {
        return null;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {

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
        return this.destQueueName;
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
        return this.transactionId;
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
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {

    }

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return null;
    }

    public String getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(String fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String getNextToken() {
        return nextToken;
    }

    public void setNextToken(String nextToken) {
        this.nextToken = nextToken;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    @Override
    public String toString() {
        return "KnXDMCorpAccountsListDTO{" +
                "responseCode='" + responseCode + '\'' +
                ", responseStatus=" + responseStatus +
                ", responseMessage='" + responseMessage + '\'' +
                ", responseDetails=" + responseDetails +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", xdmCorpAccList=" + xdmCorpAccList +
                '}';
    }
}
