/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;

public class KnXDMTalkGroupRespDTO extends KnXDMCorpRespDTO {

	private static final long serialVersionUID = 1L;
	private List<KnXDMTalkGroupInfoDTO> campGrpList;
	private Collection<KnXDMAddlTalkGroupInfoDTO> addlTalkGroupInfo;
	private int mode;
    private int scanningEnabled;
	private int responseStatus = 1;
	private String responseCode;
	private String responseMessage;
	private String destPttServerId;
	private String destQueueName;
	private String transactionId;
	private String docEtag;
	private int corpId;
	private int pvVersion;
	private Map<Integer, Integer> groupsIDcorpIDMap;

	public List<KnXDMTalkGroupInfoDTO> getCampGrpList() {
		return campGrpList;
	}

	public void setCampGrpList(List<KnXDMTalkGroupInfoDTO> campGrpList) {
		this.campGrpList = campGrpList;
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
	public String getObjectId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection getResponseDetails() {
		return null;
	}

	@Override
	public void setResponseDetails(Collection responseDetails) {

	}

	public int getMode() {
		return mode;
	}

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getScanningEnabled() {
        return scanningEnabled;
    }

	public void setScanningEnabled(int scanningEnabled) {
		this.scanningEnabled = scanningEnabled;
	}

	public Collection<KnXDMAddlTalkGroupInfoDTO> getAddlTalkGroupInfo() {
		return addlTalkGroupInfo;
	}

	public void setAddlTalkGroupInfo(Collection<KnXDMAddlTalkGroupInfoDTO> addlTalkGroupInfo) {
		this.addlTalkGroupInfo = addlTalkGroupInfo;
	}

	public String getDocEtag() {
		return docEtag;
	}

	public void setDocEtag(String docEtag) {
		this.docEtag = docEtag;
	}

	public int getCorpId() {
		return corpId;
	}

	public void setCorpId(int corpId) {
		this.corpId = corpId;
	}

	public int getPvVersion() {
		return pvVersion;
	}

	public void setPvVersion(int pvVersion) {
		this.pvVersion = pvVersion;
	}

	public Map<Integer, Integer> getGroupsIDcorpIDMap() {
		return groupsIDcorpIDMap;
	}

	public void setGroupsIDcorpIDMap(Map<Integer, Integer> groupsIDcorpIDMap) {
		this.groupsIDcorpIDMap = groupsIDcorpIDMap;
	}

	@Override
	public String toString() {
		return "KnXDMTalkGroupRespDTO{" +
				"campGrpList=" + campGrpList +
				", addlTalkGroupInfo=" + addlTalkGroupInfo +
				", mode=" + mode +
				", scanningEnabled=" + scanningEnabled +
				", responseStatus=" + responseStatus +
				", responseCode='" + responseCode + '\'' +
				", responseMessage='" + responseMessage + '\'' +
				", destPttServerId='" + destPttServerId + '\'' +
				", destQueueName='" + destQueueName + '\'' +
				", transactionId='" + transactionId + '\'' +
				", docEtag='" + docEtag + '\'' +
				", corpId=" + corpId +
				", pvVersion=" + pvVersion +
				", groupsIDcorpIDMap=" + groupsIDcorpIDMap +
				'}';
	}
}
