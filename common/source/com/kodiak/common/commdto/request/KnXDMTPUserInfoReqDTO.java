/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnXDMTPUserInfoReqDTO implements IXDMRequestDTO {

	private int tpAccountId;
	private int tpUserId;
	private String tpUser;
	private String billingMdn;
	private String mdn;
	private String activationCode;
	private String vendorId;

	//stores the Client Type
	private int clientType;
	//stores the operation Type
	private String operationType;
	private String transactionId;
	private IAuthDTO authDTO;
	private String destPttServerId;
	private String destQueueName;
	private KnConstants.HIERARCHY_TYPE hierarchyType;
	private String version;

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	@Override
	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	@Override
	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String getObjectId() {
		return mdn;
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
                .append(", tpAccountId - ").append(tpAccountId)
                .append(", tpUserId - ").append(tpUserId)
                .append(", tpUser - ").append(tpUser)
                .append(", billingMdn - ").append(KnGDPRTemplate.mdn(billingMdn))
                .append(", mdn - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", activationCode - ").append(activationCode)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", vendorId - ").append(vendorId)
				.append(", version - ").append(version)
				.append("]");

        return builder.toString();
    }
}