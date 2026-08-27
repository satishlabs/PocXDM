/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.KnXDMContactListDTO;
import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.commdto.common.KnXDMMemberDTO;
import com.kodiak.common.resources.KnConstants;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMContactListInfoDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 14, 2011           7.0
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
public class KnXDMContactListInfoDTO extends KnXDMContactListDTO implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622676138L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String objectId;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String version;

    /**
     * Member Details: Xcap body info which needs to be updated in xml doc
     */
    private Collection<KnXDMMemberDTO> memberDetails;
    /**
     * Member Info: Normalized member details which needs to be updated in Relational DB
     */
    private Collection<KnXDMMdnInfoDTO> members;
    private int memberCount;


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

    public Collection<KnXDMMdnInfoDTO> getMembers() {
        return members;
    }

    public void setMembers(Collection<KnXDMMdnInfoDTO> members) {
        this.members = members;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public Collection<KnXDMMemberDTO> getMemberDetails() {
        return memberDetails;
    }

    public void setMemberDetails(Collection<KnXDMMemberDTO> memberDetails) {
        this.memberDetails = memberDetails;
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

    @Override
	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	@Override
	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(super.toString());

        strBuffer.append(", OperationType : ").append(operationType);
        strBuffer.append(", ClientType : ").append(clientType);
        strBuffer.append(", AuthDTO : ").append(authDTO);
        strBuffer.append(", ObjectId : ").append(objectId);
        strBuffer.append(", Members : ").append(members);
        strBuffer.append(", MemberCount : ").append(memberCount);
        strBuffer.append(", MemberDetails : ").append(memberDetails);
        strBuffer.append(", Dest_PTT_Server_ID - ").append(destPttServerId);
        strBuffer.append(", Dest_Queue_Name - ").append(destQueueName);
        strBuffer.append(", Transaction_ID - ").append(transactionId);
        strBuffer.append(", hierarchyType - ").append(hierarchyType);
        strBuffer.append(", version - ").append(version);

        return strBuffer.toString();

    }
}
