/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpDispMemReqDTO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Acharyya        30-11-2011      7.2
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

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpContactDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

public class KnCorpDispMemReqDTO implements IXDMRequestDTO {
    private static final long serialVersionUID = 7526471155622776130L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private int corpId;
    private Collection<KnXDMCorpContactDTO> addedgroupMemberList;
    private Collection<KnXDMCorpContactDTO> deletedgroupMemberList;
    private String updatedMdn;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private Collection<Integer> grpIds;
    private String toMdn;

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public Collection<KnXDMCorpContactDTO> getAddedgroupMemberList() {
        return addedgroupMemberList;
    }

    public void setAddedgroupMemberList(Collection<KnXDMCorpContactDTO> addedgroupMemberList) {
        this.addedgroupMemberList = addedgroupMemberList;
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

    public String getObjectId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<KnXDMCorpContactDTO> getDeletedgroupMemberList() {
        return deletedgroupMemberList;
    }

    public void setDeletedgroupMemberList(Collection<KnXDMCorpContactDTO> deletedgroupMemberList) {
        this.deletedgroupMemberList = deletedgroupMemberList;
    }

    public String getUpdatedMdn() {
        return updatedMdn;
    }

    public void setUpdatedMdn(String updatedMdn) {
        this.updatedMdn = updatedMdn;
    }

    @Override
	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	@Override
	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

    public Collection<Integer> getGrpIds() {
        return grpIds;
    }

    public void setGrpIds(Collection<Integer> grpIds) {
        this.grpIds = grpIds;
    }

    public String getToMdn() {
        return toMdn;
    }

    public void setToMdn(String toMdn) {
        this.toMdn = toMdn;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(", addedgroupMemberList - ").append(addedgroupMemberList)
                .append(", deletedgroupMemberList - ").append(deletedgroupMemberList)
                .append(", corpId - ").append(corpId)
                .append(", toMdn - ").append(KnGDPRTemplate.mdn(toMdn))
                .append(", corpId - ").append(corpId)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", updatedMdn - ").append(KnGDPRTemplate.mdn(updatedMdn))
                .append(", grpIds - ").append(grpIds);
        return sb.toString();
    }
}
