/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by venkata sudhakar talluri on 28-12-2018
 */

public class KnXDMTGSSListRequestDTO implements IXDMRequestDTO{

    private static final long serialVersionUID = 3591326255267270996L;

    public String mdn;
    public int corpId;
    public int groupId;
    private int clientType;
    private String version;

    private IAuthDTO authDTO;

    private String transactionId;

    private String destPttServerId;

    private String destQueueName;

    private String operationType;

    private KnConstants.HIERARCHY_TYPE hierarchyType;
    
    public String mcpttId;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
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
    public String getOperationType() {
        return operationType;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    @Override
    public int getClientType() {
        return clientType;
    }

    @Override
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    @Override
    public String getObjectId() {
        return mdn;
    }

    
    public String getMcpttId() {
		return mcpttId;
	}

	public void setMcpttId(String mcpttId) {
		this.mcpttId = mcpttId;
	}

	public String getVersion() { return version; }

    public void setVersion(String version) { this.version = version; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(50);
        sb.append("corpId - ").append(corpId);
        sb.append(", mdn - ").append(KnGDPRTemplate.mdn(mdn));
        sb.append(", groupId - ").append(groupId);
        sb.append(", mcpttId - ").append(KnGDPRTemplate.mcpttId(mcpttId));
        return sb.toString();
    }

}

