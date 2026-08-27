/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMRLSInfoDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Mar 11, 2011           7.0
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
public class KnXDMRLSInfoDTO implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622676142L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String objectId;

    private String mdn;
    private int ifMatch;
    private int ifNoneMatch;

    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String version;
    private String mcPttId;


	public String getMcPttId() {
		return mcPttId;
	}

	public void setMcPttId(String mcPttId) {
		this.mcPttId = mcPttId;
	}

	@Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
    	return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
    	this.hierarchyType = hierarchyType;
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

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getDestPttServerId() {
        return destPttServerId;
    }

    public void setDestPttServerId(String destPttServerId) {
        if (destPttServerId != null) {
            destPttServerId = destPttServerId.trim();
            if (destPttServerId.equals("")) {
                destPttServerId = null;
            }
        }
        this.destPttServerId = destPttServerId;
    }

    public String getDestQueueName() {
        return destQueueName;
    }

    public void setDestQueueName(String destQueueName) {
        if (destQueueName != null) {
            destQueueName = destQueueName.trim();
            if (destQueueName.equals("")) {
                destQueueName = null;
            }
        }
        this.destQueueName = destQueueName;
    }

    /**
     * getter method for transaction ID
     *
     * @return String
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * setter method for Transaction Id
     *
     * @param transactionId String
     */
    public void setTransactionId(String transactionId) {
        if (transactionId != null) {
            transactionId = transactionId.trim();
            if (transactionId.equals("")) {
                transactionId = null;
            }
        }
        this.transactionId = transactionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);

        strBuffer.append(", OperationType : ").append(operationType);
        strBuffer.append(", ClientType : ").append(clientType);
        strBuffer.append(", AuthDTO : ").append(authDTO);
        strBuffer.append(", ObjectId : ").append(objectId);
        strBuffer.append(", MDN - ").append(KnGDPRTemplate.mdn(mdn));
        strBuffer.append(", IfMatch - ").append(ifMatch);
        strBuffer.append(", IfNoneMatch - ").append(ifNoneMatch);
        strBuffer.append(", Dest_PTT_Server_ID - ").append(destPttServerId);
        strBuffer.append(", Dest_Queue_Name - ").append(destQueueName);
        strBuffer.append(", Transaction_ID - ").append(transactionId);
        strBuffer.append(", hierarchyType - ").append(hierarchyType);
        strBuffer.append(", version - ").append(version);

        return strBuffer.toString();
    }

}
