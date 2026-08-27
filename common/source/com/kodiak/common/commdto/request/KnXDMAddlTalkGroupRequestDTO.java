/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMAddlTalkGroupRequestDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             April 26, 2018                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnXDMAddlTalkGroupRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO {

    private static final long serialVersionUID = 7862068107642427274L;
    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private Collection<KnXDMAddlTalkGroupInfoDTO> addedAddlTgList;
    private Collection<KnXDMAddlTalkGroupInfoDTO> modifiedAddlTgList;
    private Collection<KnXDMAddlTalkGroupInfoDTO> removedAddlTgList;
    private String mdn;
    private Integer mode;
    private int ifMatch;
    private int ifNoneMatch;
    private boolean calledFromModifyUPM;
    private boolean upmCall;
    private String mcpttId;

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public int getClientType() {
        return clientType;
    }

    public boolean isCalledFromModifyUPM() {
		return calledFromModifyUPM;
	}

	public void setCalledFromModifyUPM(boolean calledFromModifyUPM) {
		this.calledFromModifyUPM = calledFromModifyUPM;
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

    public Collection<KnXDMAddlTalkGroupInfoDTO> getAddedAddlTgList() {
        return addedAddlTgList;
    }

    public void setAddedAddlTgList(Collection<KnXDMAddlTalkGroupInfoDTO> addedAddlTgList) {
        this.addedAddlTgList = addedAddlTgList;
    }

    public Collection<KnXDMAddlTalkGroupInfoDTO> getModifiedAddlTgList() {
        return modifiedAddlTgList;
    }

    public void setModifiedAddlTgList(Collection<KnXDMAddlTalkGroupInfoDTO> modifiedAddlTgList) {
        this.modifiedAddlTgList = modifiedAddlTgList;
    }

    public Collection<KnXDMAddlTalkGroupInfoDTO> getRemovedAddlTgList() {
        return removedAddlTgList;
    }

    public void setRemovedAddlTgList(Collection<KnXDMAddlTalkGroupInfoDTO> removedAddlTgList) {
        this.removedAddlTgList = removedAddlTgList;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public int getIfMatch() {
        return ifMatch;
    }

    public void setIfMatch(int ifMatch) {
        this.ifMatch = ifMatch;
    }

    public int getIfNoneMatch() {
        return ifNoneMatch;
    }

    public void setIfNoneMatch(int ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
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

	@Override
    public String toString() {
        return "KnXDMAddlTalkGroupRequestDTO{" +
                "operationType='" + operationType + '\'' +
                ", clientType=" + clientType +
                ", authDTO=" + authDTO +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", addedAddlTgList=" + addedAddlTgList +
                ", modifiedAddlTgList=" + modifiedAddlTgList +
                ", removedAddlTgList=" + removedAddlTgList +
                ", mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", mode='" + mode + '\'' +
                ", calledFromModifyUPM='" + calledFromModifyUPM + '\'' +
                ", mcpttId='" + KnGDPRTemplate.mcpttId(mcpttId) + '\'' +
                '}';
    }
}
