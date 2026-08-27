/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpSublistInfoRequestDTO.java
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
import com.kodiak.common.commdto.common.KnXDMCorpSublistInfoDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.LinkedList;


public class KnXDMCorpSublistInfoRequestDTO extends KnXDMCorpSublistInfoDTO implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622776160L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private String filterType;
    private Collection<String> mdnList;
    private Collection<String> sublistIds;
    //Added linked List to make sure the mdnList order is maintained
    private LinkedList<String> removedMdnList;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String version;
    private Integer userProfileListType;
    private Integer listDistribution;
    private String hierarchyId;

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId= hierarchyId;
    }


    public String getHierarchyId() {
        return hierarchyId;
    }

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
    	return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
    	this.hierarchyType = hierarchyType;
    }

    private boolean distribution;

    public String getOperationType() {
        return this.operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public int getClientType() {
        return this.clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public IAuthDTO getAuthDTO() {
        return this.authDTO;
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

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public Collection<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(Collection<String> mdnList) {
        this.mdnList = mdnList;
    }

    public Collection<String> getSublistIds() {
        return sublistIds;
    }

    public void setSublistIds(Collection<String> sublistIds) {
        this.sublistIds = sublistIds;
    }

    public LinkedList<String> getRemovedMdnList() {
        return removedMdnList;
    }

    public void setRemovedMdnList(LinkedList<String> removedMdnList) {
        this.removedMdnList = removedMdnList;
    }

    public boolean isDistribution() {
        return distribution;
    }

    public void setDistribution(boolean distribution) {
        this.distribution = distribution;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getUserProfileListType() {
        return userProfileListType;
    }

    public void setUserProfileListType(Integer userProfileListType) {
        this.userProfileListType = userProfileListType;
    }

    public Integer getListDistribution() {
        return listDistribution;
    }

    public void setListDistribution(Integer listDistribution) {
        this.listDistribution = listDistribution;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(super.toString())
                .append(", OperationTYpe - ").append(operationType)
                .append(", ClientType - ").append(clientType)
                .append(", AuthDTO - ").append(authDTO)
                .append(", destPttServerId - ").append(destPttServerId)
                .append(", destQueueName - ").append(destQueueName)
                .append(", transactionId - ").append(transactionId)
                .append(", FilterType - ").append(filterType)
                .append(", AddedMdnList - ").append(KnGDPRTemplate.mdnList(mdnList))
                .append(", SublistIds - ").append(sublistIds)
                .append(", RemovedMdnList - ").append(KnGDPRTemplate.mdnList(removedMdnList))
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", distribution - ").append(distribution)
                .append(", version - ").append(version)
                .append(", userProfileListType - ").append(userProfileListType)
                .append(", listDistribution - ").append(listDistribution);
        return sb.toString();
    }
}
