package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnBulkSubscriberEntry;
import com.kodiak.common.commdto.common.KnBulkSubscriberRespDTO;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnXDMBulkOpsRespDTO extends KnBulkSubscriberRespDTO implements IXDMResponseDTO {
    @Serial
    private static final long serialVersionUID = 7526471155622676303L;
    private String objectId;
    private int responseStatus = 1;
    private String responseCode;
    private String responseMessage;
    private Collection responseDetails;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;

    private String batchId;
    private Integer totalCount;
    private Integer successMdnCount = 0;
    private Integer failureMdnCount = 0;
    private Long processingTimeMs;
    private List<KnBulkSubscriberEntry> successMdns = new ArrayList<>();
    private List<KnBulkOpsErrorDetail> failureMdns = new ArrayList<>();
    private boolean isCorporateDeleted;

    public void addFailure(String mdn, String errorCode, String errorMessage) {
        KnBulkOpsErrorDetail error = new KnBulkOpsErrorDetail();
        error.setMdn(mdn);
        error.setErrorCode(errorCode);
        error.setErrorMessage(errorMessage);
        failureMdns.add(error);
        failureMdnCount++;
    }

    @Override
    public String getResponseCode() {
        return this.responseCode;
    }

    @Override
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public int getResponseStatus() {
        return this.responseStatus;
    }

    @Override
    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    @Override
    public String getResponseMessage() {
        return this.responseMessage;
    }

    @Override
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public Collection getResponseDetails() {
        return null; //todo not be used for corporate
    }

    @Override
    public void setResponseDetails(Collection responseDetails) {
        //todo not be used for corporate
    }

    @Override
    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId = destPttServerId;
    }

    @Override
    public String getDestPttServerId() {
        return destPttServerId;
    }

    @Override
    public void setDestQueueName(String destQueueName) {
        this.destQueueName = destQueueName;
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
        this.transactionId = transactionId;
    }

    @Override
    public String getObjectId() {
        return this.transactionId;
    }

    public String getBatchId() {
        return batchId;
    }
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Integer getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSuccessMdnCount() {
        return successMdnCount;
    }
    public void setSuccessMdnCount(Integer successMdnCount) {
        this.successMdnCount = successMdnCount;
    }

    public Integer getFailureMdnCount() {
        return failureMdnCount;
    }
    public void setFailureMdnCount(Integer failureMdnCount) {
        this.failureMdnCount = failureMdnCount;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }
    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public List<KnBulkSubscriberEntry> getSuccessMdns() {
        return successMdns;
    }
    public void setSuccessMdns(List<KnBulkSubscriberEntry> successMdns) {
        this.successMdns = successMdns;
    }

    public List<KnBulkOpsErrorDetail> getFailureMdns() {
        return failureMdns;
    }
    public void setFailureMdns(List<KnBulkOpsErrorDetail> failureMdns) {
        this.failureMdns = failureMdns;
    }

    public boolean isCorporateDeleted() {
        return isCorporateDeleted;
    }

    public void setCorporateDeleted(boolean corporateDeleted) {
        isCorporateDeleted = corporateDeleted;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(", ResponseStatus - ").append(responseStatus)
                .append(", ResponseCode - ").append(responseCode)
                .append(", ResponseMsg - ").append(responseMessage)
                .append(", destPttServerId - ").append(destPttServerId)
                .append(", destQueueName - ").append(destQueueName)
                .append(", transactionId - ").append(transactionId)
                .append(", batchId - ").append(batchId)
                .append(", totalCount - ").append(totalCount)
                .append(", successMdnCount - ").append(successMdnCount)
                .append(", failureMdnCount - ").append(failureMdnCount)
                .append(", processingTimeMs - ").append(processingTimeMs)
                .append(", successMdns - ").append(successMdns)
                .append(", failureMdns - ").append(failureMdns)
                .append(", isCorporateDeleted - ").append(isCorporateDeleted)
                .append(super.toString());
        return sb.toString();
    }
}
