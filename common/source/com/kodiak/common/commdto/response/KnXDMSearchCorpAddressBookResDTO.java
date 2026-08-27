/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.kodiak.common.commdto.common.KnXDMSubsProvDTO;
public class KnXDMSearchCorpAddressBookResDTO implements IXDMResponseDTO {
	private static final long serialVersionUID = 7526471155622676158L;
	private String objectId;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private String responseCode;
    private int responseStatus;
    private String responseMessage;
    private Collection responseDetails;
    private String txnId;
	private Integer pageId;
    private Integer totalPages;
    private Integer pageSize;
    private List<KnXDMSubsProvDTO> subsRespDTO;
    
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
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
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public Integer getPageId() {
		return pageId;
	}
	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}
	public Integer getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	public List<KnXDMSubsProvDTO> getSubsRespDTO() {
		return subsRespDTO;
	}
	public void setSubsRespDTO(List<KnXDMSubsProvDTO> subsRespDTO) {
		this.subsRespDTO = subsRespDTO;
	}
	@Override
	public String toString() {
		return "KnXDMSearchCorpAddressBookResDTO [objectId=" + objectId + ", destPttServerId=" + destPttServerId
				+ ", destQueueName=" + destQueueName + ", transactionId=" + transactionId + ", responseCode="
				+ responseCode + ", responseStatus=" + responseStatus + ", responseMessage=" + responseMessage
				+ ", responseDetails=" + responseDetails + ", txnId=" + txnId + ", pageId=" + pageId + ", totalPages="
				+ totalPages + ", pageSize=" + pageSize + ", subsRespDTO=" + subsRespDTO + "]";
	}
	
	
}
