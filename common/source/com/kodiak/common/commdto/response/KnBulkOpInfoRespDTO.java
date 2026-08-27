/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.util.List;

import com.kodiak.common.commdto.common.KnBulkOpInfoDTO;

public class KnBulkOpInfoRespDTO extends KnXDMRespDTO{
	
	private static final long serialVersionUID = -4957236357989032336L;
	
	private String responseCode;
	private int responseStatus;
	private String responseMessage;
	private String destPttServerId;
	private String destQueueName;
	private String transactionId;
	private List<KnBulkOpInfoDTO> bulkOpInfoDTOList;
	
	public List<KnBulkOpInfoDTO> getBulkOpInfoDTOList() {
		return this.bulkOpInfoDTOList;
	}

	public void setBulkOpInfoDTOList(List<KnBulkOpInfoDTO> bulkOpInfoDTOList) {
		this.bulkOpInfoDTOList = bulkOpInfoDTOList;
	}

	@Override
	public String getObjectId() {
		return null;
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
	public void setDestPttServerId(String destPttServerId) {
		this.destPttServerId = destPttServerId;
	}

	@Override
	public String getDestPttServerId() {
		return this.destPttServerId;
	}

	@Override
	public void setDestQueueName(String destQueueName) {
		this.destQueueName =destQueueName;
	}

	@Override
	public String getDestQueueName() {
		return this.destQueueName;
	}

	@Override
	public String getTransactionId() {
		return this.transactionId;
	}

	@Override
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" bulkOpInfoDTOList - ").append(this.bulkOpInfoDTOList)
        .append("responseCode - ").append(this.responseCode)
        .append("responseMessage - ").append(this.responseMessage)
        .append("responseStatus - ").append(this.responseStatus);
        return builder.toString();
    }

}
