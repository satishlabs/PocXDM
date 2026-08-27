/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KnXDMLoginNotifyEventReqDTO implements IXDMRequestDTO {

	private String deviceMdn;
	private String mcServiceBaseMDN;
	private String onlineStatus;

	//stores the Client Type
	private int clientType;
	//stores the operation Type
	private String operationType;
	private String transactionId;
	private IAuthDTO authDTO;
	private String destPttServerId;
	private String destQueueName;
	private KnConstants.HIERARCHY_TYPE hierarchyType;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceMdn() {
		return deviceMdn;
	}

	public void setDeviceMdn(String deviceMdn) {
		this.deviceMdn = deviceMdn;
	}

	public String getMcServiceBaseMDN() {
		return mcServiceBaseMDN;
	}

	public void setMcServiceBaseMDN(String mcServiceBaseMDN) {
		this.mcServiceBaseMDN = mcServiceBaseMDN;
	}

	public String getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
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
	public String getObjectId() {
		return mcServiceBaseMDN;
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
    public String getOperationType() {
        return operationType;
    }

	@Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

	@Override
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" KnXDMTPUserInfoReqDTO[ ")
                .append(", profileMdn - ").append(KnGDPRTemplate.mdn(deviceMdn))
                .append(", realMdn - ").append(KnGDPRTemplate.mdn(mcServiceBaseMDN))
				.append(", id - ").append(id)
				.append("]");

        return builder.toString();
    }
}