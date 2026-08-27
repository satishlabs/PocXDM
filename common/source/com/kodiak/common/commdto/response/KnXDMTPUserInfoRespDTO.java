/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

public class KnXDMTPUserInfoRespDTO implements IXDMResponseDTO {
	
	private int tpAccountId;
	private int tpUserId;
	private String tpUser;
	private String billingMdn;
	private String mdn;
	private String activationCode;
	private String expiryTime;
	
    private String responseCode;
    private int responseStatus;
    private String responseMessage;
    private Collection responseDetails;
    private String objectId;
    private String transactionId;
    private String destPttServerId;
    private String destQueueName;

	public int getTpAccountId() {
		return tpAccountId;
	}

	public void setTpAccountId(int tpAccountId) {
		this.tpAccountId = tpAccountId;
	}

	public int getTpUserId() {
		return tpUserId;
	}

	public void setTpUserId(int tpUserId) {
		this.tpUserId = tpUserId;
	}

	public String getTpUser() {
		return tpUser;
	}

	public void setTpUser(String tpUser) {
		this.tpUser = tpUser;
	}

	public String getBillingMdn() {
		return billingMdn;
	}

	public void setBillingMdn(String billingMdn) {
		this.billingMdn = billingMdn;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public String getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(String expiryTime) {
		this.expiryTime = expiryTime;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	@Override
	public String getObjectId() {
		 return objectId;
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
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnXDMTPUserInfoRespDTO[")
		.append("tpAccountId-").append(tpAccountId)
		.append(", tpUserId-").append(tpUserId)
		.append(", tpUser-").append(tpUser)
		.append(", billingMdn-").append(KnGDPRTemplate.mdn(billingMdn))
		.append(", mdn-").append(KnGDPRTemplate.mdn(mdn))
		.append(", activationCode-").append(activationCode)
		.append(", expiryTime-").append(expiryTime)
		.append(", responseCode-").append(responseCode)
		.append(", responseStatus-").append(responseStatus)
		.append(", responseMessage-").append(responseMessage)
		.append("]");
		
		return builder.toString();
	}

}
