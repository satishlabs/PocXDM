/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.List;

public class KnXDMMcPttUEConfigRespDTO extends KnXDMRespDTO {
	private static final long serialVersionUID = 7526471155622676158L;

	private String mdn;
	private String mcsDomainName;
	private String mcPttUEConfigname;
	private Integer maxSimDynSession;
	private Integer maxSimDedSession;
	private Integer ipPrefOnCellIntf;
	//private String eTag;
	private String sipProxyURI;
	private String geoSipProxyURI;

	private String responseCode;
	private int responseStatus;
	private String responseMessage;
	private Collection responseDetails;
	private String objectId;
	private String destPttServerId;
	private String destQueueName;
	private String transactionId;

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public String getMcsDomainName() {
		return mcsDomainName;
	}

	public void setMcsDomainName(String mcsDomainName) {
		this.mcsDomainName = mcsDomainName;
	}

	public String getMcPttUEConfigname() {
		return mcPttUEConfigname;
	}

	public void setMcPttUEConfigname(String mcPttUEConfigname) {
		this.mcPttUEConfigname = mcPttUEConfigname;
	}

	public Integer getMaxSimDynSession() {
		return maxSimDynSession;
	}

	public void setMaxSimDynSession(Integer maxSimDynSession) {
		this.maxSimDynSession = maxSimDynSession;
	}

	public Integer getMaxSimDedSession() {
		return maxSimDedSession;
	}

	public void setMaxSimDedSession(Integer maxSimDedSession) {
		this.maxSimDedSession = maxSimDedSession;
	}

	public Integer getIpPrefOnCellIntf() {
		return ipPrefOnCellIntf;
	}

	public void setIpPrefOnCellIntf(Integer ipPrefOnCellIntf) {
		this.ipPrefOnCellIntf = ipPrefOnCellIntf;
	}

	public String getSipProxyURI() { return sipProxyURI; }

	public void setSipProxyURI(String sipProxyURI) { this.sipProxyURI = sipProxyURI; }

	public String getGeoSipProxyURI() {	return geoSipProxyURI; }

	public void setGeoSipProxyURI(String geoSipProxyURI) { this.geoSipProxyURI = geoSipProxyURI; }

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

	@Override
	public String toString() {
		return "KnXDMMcPttUEConfigRespDTO{" +
				"mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
				", mcsDomainName='" + mcsDomainName + '\'' +
				", mcPttUEConfigname='" + mcPttUEConfigname + '\'' +
				", maxSimDynSession=" + maxSimDynSession +
				", maxSimDedSession=" + maxSimDedSession +
				", ipPrefOnCellIntf=" + ipPrefOnCellIntf +
				", sipProxyURI=" + sipProxyURI +
				", geoSipProxyURI=" + geoSipProxyURI +
			//	", eTag='" + eTag + '\'' +
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
