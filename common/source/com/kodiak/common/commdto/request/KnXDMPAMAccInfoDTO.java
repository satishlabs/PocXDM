/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Map;

/**
 * *****************************************************************************
 * File name:   KnXDMPAMAccInfoDTO.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar          08/02/2013       7.4
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
 * *******************************************************************************
 */

public class KnXDMPAMAccInfoDTO implements IXDMRequestDTO {
    private static final long serialVersionUID = 7526471155622678003L;

    private int pamAccId;
    private String oldBillingNumber;
    private String billingName;
    private int pamAccState;
    private String billingNumber;
    private int totalNoOfLines;
    private int subsCount;
    private int existingSubsCount;
    private long creationTime;
    private long lastUpdateTime;
    private boolean isprofileChange;
    private KnConstants.HIERARCHY_TYPE hierarchyType;

    private Map<String, Object> OpMap;

    private KnXDMPAMSubsProfInfoDTO profileDetails;

    private KnXDMPAMSubsProfInfoDTO oldProfileDetails;

    //stores the Client Type
    private int clientType;

    //stores the operation Type
    private String operationType;

    private String transactionId;

    private IAuthDTO authDTO;

    private String destPttServerId;

    private String destQueueName;
    private Map<Integer,Integer> featureBitInfoMap;
    private String  version;

    public int getPamAccId() {
        return pamAccId;
    }

    public void setPamAccId(int pamAccId) {
        this.pamAccId = pamAccId;
    }

    public String getOldBillingNumber() {
        return oldBillingNumber;
    }

    public void setOldBillingNumber(String oldBillingMDN) {
        this.oldBillingNumber = oldBillingMDN;
    }

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    public int getPamAccState() {
        return pamAccState;
    }

    public void setPamAccState(int pamAccState) {
        this.pamAccState = pamAccState;
    }

    public String getBillingNumber() {
        return billingNumber;
    }

    public void setBillingNumber(String billingNumber) {
        this.billingNumber = billingNumber;
    }

    public int getTotalNoOfLines() {
        return totalNoOfLines;
    }

    public void setTotalNoOfLines(int totalNoOfLines) {
        this.totalNoOfLines = totalNoOfLines;
    }

    public int getSubsCount() {
        return subsCount;
    }

    public void setSubsCount(int subsCount) {
        this.subsCount = subsCount;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public KnXDMPAMSubsProfInfoDTO getProfileDetails() {
        return profileDetails;
    }

    public void setProfileDetails(KnXDMPAMSubsProfInfoDTO profileDetails) {
        this.profileDetails = profileDetails;
    }

    public void setOldProfileDetails(KnXDMPAMSubsProfInfoDTO oldProfileDetails) {
		this.oldProfileDetails = oldProfileDetails;
	}

	public KnXDMPAMSubsProfInfoDTO getOldProfileDetails() {
		return oldProfileDetails;
	}

	public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
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

    public String getObjectId() {
        return oldBillingNumber;
    }

    public Map<String, Object> getOpMap() {
        return OpMap;
    }

    public void setOpMap(Map<String, Object> opMap) {
        OpMap = opMap;
    }

    public int getExistingSubsCount() {
		return existingSubsCount;
	}

	public void setExistingSubsCount(int existingSubsCount) {
		this.existingSubsCount = existingSubsCount;
	}


    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public boolean isIsprofileChange() {
        return isprofileChange;
    }

    public void setIsprofileChange(boolean isprofileChange) {
        this.isprofileChange = isprofileChange;
    }

    public Map<Integer, Integer> getFeatureBitInfoMap() {
        return featureBitInfoMap;
    }

    public void setFeatureBitInfoMap(Map<Integer, Integer> featureBitInfoMap) {
        this.featureBitInfoMap = featureBitInfoMap;
    }

    public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String toString() {
        StringBuilder strBuffer = new StringBuilder(100);
        strBuffer.append(" pamAccId - ").append(pamAccId)
                .append(", transactionId - ").append(transactionId)
                .append(", oldBillingNumber - ").append(KnGDPRTemplate.mdn(oldBillingNumber))
                .append(", billingName - ").append(KnGDPRTemplate.name(billingName))
                .append(", billingNumber - ").append(KnGDPRTemplate.mdn(billingNumber))
                .append(", totalNoOfLines - ").append(totalNoOfLines)
                .append(", subsCount - ").append(subsCount)
                .append(", profileDetails - ").append(profileDetails)
                .append(", operationType - ").append(operationType)
                .append(", existingSubsCount - ").append(existingSubsCount)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", featureBitInfoList - ").append(featureBitInfoMap)
                .append(", OpMap - ").append(OpMap)
                .append(", version - ").append(version);

        return strBuffer.toString();
    }
}
