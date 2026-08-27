/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.util.Collection;

import com.kodiak.common.commdto.common.KnXDMLicensePackDTO;

public class KnXDMLicensePackResponseDTO extends KnXDMLicensePackDTO implements IXDMResponseDTO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3926565610172334129L;

	//stores the response Code
    private String responseCode;
    
    //stores the responseStatus
    private int responseStatus;
    
    //stores the response message;
    private String responseMessage;
    
    //Stores the response details;
    private Collection responseDetails;
    
    private String destPttServerId;
    
    private String destQueueName;
    
    private String transactionId;
    
    private long creationTime;
    
    private long updationTime;
    
    // holds service auth status of License pack
    private int serviceAuthStatus;
    
    public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getUpdationTime() {
		return updationTime;
	}

	public void setUpdationTime(long updationTime) {
		this.updationTime = updationTime;
	}
	
	public int getServiceAuthStatus() {
		return serviceAuthStatus;
	}

	public void setServiceAuthStatus(int serviceAuthStatus) {
		this.serviceAuthStatus = serviceAuthStatus;
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

}
