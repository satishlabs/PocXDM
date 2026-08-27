/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpSubscriberInfoRequestDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 17, 2011      7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.List;
import java.util.Map;

public class KnXDMCorpSubscInfoRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622776161L;

    private String operationType;
    private int clientType;
    private int newClientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private String subscriberMdn;
    private String mcpttId;
    private String newMdn;
    private String name;
    private String oldName;
    private int publicSubscriptionType;
    private int newPublicSubscriptionType;
    private int corpSubscriptionType;
    private int newCorpSubscriptionType;
    private int newCorpId;
    private Map<String, Object> customParamMap;
    private Boolean autoPairingFlag;
    private String xdmshome;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String activeFs2;
    private List<String> pttServerIds;
    private String userProfileId;
    private int pvMajorVersion;
    private int mcpttCompliance;
    private boolean upmCall;
    private String mcDataId;
    private String mcVideoId;
    private String groupId;
    private List<String> groupURIList;
    private String subscriberFS2;
    private Integer nextToken;
    private Integer fetchSize;
    private String fanId;

    public int getPvMajorVersion() {
        return pvMajorVersion;
    }

    public void setPvMajorVersion(int pvMajorVersion) {
        this.pvMajorVersion = pvMajorVersion;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
    	return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
    	this.hierarchyType = hierarchyType;
    }


    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public void setDestPttServerId(String destPttServerId) {
        this.destPttServerId = destPttServerId;
    }

    public String getDestPttServerId() {
        return destPttServerId;
    }

    public void setDestQueueName(String destQueueName) {
        this.destQueueName = destQueueName;
    }

    public String getDestQueueName() {
        return destQueueName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSubscriberMdn() {
        return subscriberMdn;
    }

    public void setSubscriberMdn(String subscriberMdn) {
        this.subscriberMdn = subscriberMdn;
    }

    public String getNewMdn() {
        return newMdn;
    }

    public void setNewMdn(String newMdn) {
        this.newMdn = newMdn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNewClientType() {
        return newClientType;
    }

    public void setNewClientType(int newClientType) {
        this.newClientType = newClientType;
    }

    public int getPublicSubscriptionType() {
        return publicSubscriptionType;
    }

    public void setPublicSubscriptionType(int publicSubscriptionType) {
        this.publicSubscriptionType = publicSubscriptionType;
    }

    public int getNewPublicSubscriptionType() {
        return newPublicSubscriptionType;
    }

    public void setNewPublicSubscriptionType(int newPublicSubscriptionType) {
        this.newPublicSubscriptionType = newPublicSubscriptionType;
    }

    public int getCorpSubscriptionType() {
        return corpSubscriptionType;
    }

    public void setCorpSubscriptionType(int corpSubscriptionType) {
        this.corpSubscriptionType = corpSubscriptionType;
    }

    public int getNewCorpSubscriptionType() {
        return newCorpSubscriptionType;
    }

    public void setNewCorpSubscriptionType(int newCorpSubscriptionType) {
        this.newCorpSubscriptionType = newCorpSubscriptionType;
    }

    public int getNewCorpId() {
        return newCorpId;
    }

    public void setNewCorpId(int newCorpId) {
        this.newCorpId = newCorpId;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }
     public Boolean isAutoPairingFlag() {
        return autoPairingFlag;
    }

    public void setAutoPairingFlag(Boolean autoPairingFlag) {
        this.autoPairingFlag = autoPairingFlag;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getXdmshome() {
        return xdmshome;
    }

    public void setXdmshome(String xdmshome) {
        this.xdmshome = xdmshome;
    }

    public String getActiveFs2() {
		return activeFs2;
	}

	public void setActiveFs2(String activeFs2) {
		this.activeFs2 = activeFs2;
	}

    public List<String> getPttServerIds() {
        return pttServerIds;
    }

    public void setPttServerIds(List<String> pttServerIds) {
        this.pttServerIds = pttServerIds;
    }

    public int getMcpttCompliance() {
		return mcpttCompliance;
	}

	public void setMcpttCompliance(int mcpttCompliance) {
		this.mcpttCompliance = mcpttCompliance;
	}

    public boolean isUpmCall() {
        return upmCall;
    }

    public void setUpmCall(boolean upmCall) {
        this.upmCall = upmCall;
    }
    

    public String getMcpttId() {
		return mcpttId;
	}

	public void setMcpttId(String mcpttId) {
		this.mcpttId = mcpttId;
	}

    public String getMcDataId() { return mcDataId; }

    public void setMcDataId(String mcDataId) { this.mcDataId = mcDataId; }

    public String getMcVideoId() { return mcVideoId; }

    public void setMcVideoId(String mcVideoId) { this.mcVideoId = mcVideoId; }

    public String getGroupId() { return groupId; }

    public void setGroupId(String groupId) { this.groupId = groupId; }

    public List<String> getGroupURIList() {
        return groupURIList;
    }

    public void setGroupURIList(List<String> groupURIList) {
        this.groupURIList = groupURIList;
    }

    public String getSubscriberFS2() {
        return subscriberFS2;
    }

    public void setSubscriberFS2(String subscriberFS2) {
        this.subscriberFS2 = subscriberFS2;
    }

    public Integer getNextToken() {
        return nextToken;
    }

    public void setNextToken(Integer nextToken) {
        this.nextToken = nextToken;
    }

    public Integer getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String getFanId() {
        return fanId;
    }

    public void setFanId(String fanId) {
        this.fanId = fanId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(super.toString())
                .append(", OperationType - ").append(operationType)
                .append(", ClientType - ").append(clientType)
                .append(", AuthDto - ").append(authDTO)
                .append(", destPttServerId - ").append(destPttServerId)
                .append(", destQueueName - ").append(destQueueName)
                .append(", subscriberMdn - ").append(KnGDPRTemplate.mdn(subscriberMdn))
                .append(", transactionId - ").append(transactionId)
                .append(", newMdn - ").append(KnGDPRTemplate.mdn(newMdn))
                .append(", newClientType - ").append(newClientType)
                .append(", publicSubscriptionType - ").append(publicSubscriptionType)
                .append(", newPublicSubscriptionType - ").append(newPublicSubscriptionType)
                .append(", corpSubscriptionType - ").append(corpSubscriptionType)
                .append(", newCorpSubscriptionType - ").append(newCorpSubscriptionType)
                .append(", newCorpId - ").append(newCorpId)
                .append(", subsName - ").append(KnGDPRTemplate.name(name))
                .append(", customParamMap - ").append(customParamMap)
                .append(", autoPairingFlag - ").append(autoPairingFlag)
                .append(", xdmshome - ").append(xdmshome)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", subsOldName - ").append(KnGDPRTemplate.name(oldName))
                .append(", activeFs2 - ").append(activeFs2)
                .append(", pttServerIds - ").append(pttServerIds)
                .append(", userProfileId - ").append(userProfileId)
                .append(", pvMajorVersion - ").append(pvMajorVersion)
                .append(", upmCall - ").append(upmCall)
                .append(", mcpttCompliance - ").append(mcpttCompliance)
                .append(", mcpttId - ").append(KnGDPRTemplate.mcpttId(mcpttId))
                .append(", mcVideoId - ").append(KnGDPRTemplate.mcpttId(mcVideoId))
                .append(", groupId - ").append(groupId)
                .append(", groupURIList - ").append(groupURIList)
                .append(", nextToken - ").append(nextToken)
                .append(", fetchSize - ").append(fetchSize)
                .append(", fanId - ").append(fanId);

        return sb.toString();
    }
}
