/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

public class KnXDMMcPttServiceConfigRespDTO extends KnXDMRespDTO {
	private static final long serialVersionUID = 7526471155622676158L;

	private String mdn;
	private String domain;
	private Integer t1EndOfRtpMedia;
	private Integer t3StopTalkingGrace;
	private String immPerilResPriNamespace;
	private String immPerilResPriPriority;
	private String normalResPriNamespace;
	private String 	normalResPriPriority;
	private String protocolVersion;
	//private String eTag;


	private String responseCode;
	private int responseStatus;
	private String responseMessage;
	private Collection responseDetails;
	private String objectId;
	private String destPttServerId;
	private String destQueueName;
	private String transactionId;

	private String emerResPriPriority;
	private String emerResPriNamespace;

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Integer getT1EndOfRtpMedia() {
		return t1EndOfRtpMedia;
	}

	public void setT1EndOfRtpMedia(Integer t1EndOfRtpMedia) {
		this.t1EndOfRtpMedia = t1EndOfRtpMedia;
	}

	public Integer getT3StopTalkingGrace() {
		return t3StopTalkingGrace;
	}

	public void setT3StopTalkingGrace(Integer t3StopTalkingGrace) {
		this.t3StopTalkingGrace = t3StopTalkingGrace;
	}

	public String getImmPerilResPriNamespace() {
		return immPerilResPriNamespace;
	}

	public void setImmPerilResPriNamespace(String immPerilResPriNamespace) {
		this.immPerilResPriNamespace = immPerilResPriNamespace;
	}

	public String getImmPerilResPriPriority() {
		return immPerilResPriPriority;
	}

	public void setImmPerilResPriPriority(String immPerilResPriPriority) {
		this.immPerilResPriPriority = immPerilResPriPriority;
	}

	public String getNormalResPriNamespace() {
		return normalResPriNamespace;
	}

	public void setNormalResPriNamespace(String normalResPriNamespace) {
		this.normalResPriNamespace = normalResPriNamespace;
	}

	public String getNormalResPriPriority() {
		return normalResPriPriority;
	}

	public void setNormalResPriPriority(String normalResPriPriority) {
		this.normalResPriPriority = normalResPriPriority;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}
	/*public String geteTag() {
		return eTag;
	}

	public void seteTag(String eTag) {
		this.eTag = eTag;
	}*/

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
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
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

	public String getEmerResPriPriority() {
		return emerResPriPriority;
	}

	public void setEmerResPriPriority(String emerResPriPriority) {
		this.emerResPriPriority = emerResPriPriority;
	}

	public String getEmerResPriNamespace() {
		return emerResPriNamespace;
	}

	public void setEmerResPriNamespace(String emerResPriNamespace) {
		this.emerResPriNamespace = emerResPriNamespace;
	}

	@Override
	public String toString() {
		return "KnXDMMcPttServiceConfigRespDTO{" +
				"mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
				", domain='" + domain + '\'' +
				", t1EndOfRtpMedia=" + t1EndOfRtpMedia +
				", t3StopTalkingGrace=" + t3StopTalkingGrace +
				", immPerilResPriNamespace='" + immPerilResPriNamespace + '\'' +
				", immPerilResPriPriority='" + immPerilResPriPriority + '\'' +
				", normalResPriNamespace='" + normalResPriNamespace + '\'' +
				", normalResPriPriority='" + normalResPriPriority + '\'' +
				", protocolVersion='" + protocolVersion + '\'' +
				//", eTag='" + eTag + '\'' +
				", responseCode='" + responseCode + '\'' +
				", responseStatus=" + responseStatus +
				", responseMessage='" + responseMessage + '\'' +
				", responseDetails=" + responseDetails +
				", objectId='" + objectId + '\'' +
				", destPttServerId='" + destPttServerId + '\'' +
				", destQueueName='" + destQueueName + '\'' +
				", transactionId='" + transactionId + '\'' +
				'}';
	}
}
