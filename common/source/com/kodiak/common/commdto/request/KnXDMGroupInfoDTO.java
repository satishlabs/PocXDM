/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMGroupDTO;
import com.kodiak.common.commdto.common.KnXDMGroupMdnInfoDTO;
import com.kodiak.common.commdto.common.KnXDMMemberDTO;

import java.util.Collection;
import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMGroupInfoDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 17, 2011           7.0
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
public class KnXDMGroupInfoDTO extends KnXDMGroupDTO implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622676140L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String objectId;
    private String destPttServerId;
    private String destQueueName;

    private Collection<KnXDMMemberDTO> memberDetails;

    private Collection<KnXDMGroupMdnInfoDTO> grpMembers;
    private int memberCount;

    private String transactionId;
    private KnConstants.HIERARCHY_TYPE hierarchyType;

    private String vendorID;

    private List<KnXDMGroupMdnInfoDTO> modifiedMembers;

    private List<String> removenMembers;

    private String newGrpName;
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


    public Collection<KnXDMGroupMdnInfoDTO> getGrpMembers() {
        return grpMembers;
    }

    public void setGrpMembers(Collection<KnXDMGroupMdnInfoDTO> grpMembers) {
        this.grpMembers = grpMembers;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
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

    public String getVendorID() {
        return vendorID;
    }

    public void setVendorID(String vendorID) {
        this.vendorID = vendorID;
    }

    public List<KnXDMGroupMdnInfoDTO> getModifiedMembers() {
        return modifiedMembers;
    }

    public void setModifiedMembers(List<KnXDMGroupMdnInfoDTO> modifiedMembers) {
        this.modifiedMembers = modifiedMembers;
    }

    public List<String> getRemovenMembers() {
        return removenMembers;
    }

    public void setRemovenMembers(List<String> removenMembers) {
        this.removenMembers = removenMembers;
    }

    public String getNewGrpName() {
        return newGrpName;
    }

    public void setNewGrpName(String newGrpName) {
        this.newGrpName = newGrpName;
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
        strBuffer.append(", grpMembers - ").append(grpMembers);
        strBuffer.append(", MemberCount - ").append(memberCount);
        strBuffer.append(", MemberDetails - ").append(memberDetails);
        strBuffer.append(", Dest_PTT_Server_ID - ").append(destPttServerId);
        strBuffer.append(", Dest_Queue_Name - ").append(destQueueName);
        strBuffer.append(", Transaction_ID - ").append(transactionId);
        strBuffer.append(", hierarchyType - ").append(hierarchyType);
        strBuffer.append(", vendorID - ").append(vendorID);
        strBuffer.append(", modifiedMembers - ").append(modifiedMembers);
        strBuffer.append(", removenMembers - ").append(removenMembers);
        strBuffer.append(", version - ").append(version);
        return strBuffer.toString();
    }
}
