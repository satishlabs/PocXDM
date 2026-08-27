/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMBulkCorpGroupInfoRequestDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 17, 2011      7.0
 * <p/>
 * <p/>
 * Copyright (c) 2006  Kodiak Networks (India) Pvt. Ltd.
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
import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.resources.KnConstants;

import java.util.Map;


public class KnXDMBulkCorpGroupInfoRequestDTO implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622776156L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private String corpId;
    private String userProfileId;
    private String eTag;
    private Map<Integer, KnXDMCorpGroupInfoRequestDTO> groupInfoMap;
    private Map<Integer, KnXDMAddlTalkGroupInfoDTO> addlTalkGroupInfoMap;
    private Map<Integer, Boolean> grpLocWatcherMap;

    private KnConstants.HIERARCHY_TYPE hierarchyType;

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
    	return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
    	this.hierarchyType = hierarchyType;
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

    public Map<Integer, KnXDMCorpGroupInfoRequestDTO> getGroupInfoMap() {
        return groupInfoMap;
    }

    public void setGroupInfoMap(Map<Integer, KnXDMCorpGroupInfoRequestDTO> groupInfoMap) {
        this.groupInfoMap = groupInfoMap;
    }

    public Map<Integer, KnXDMAddlTalkGroupInfoDTO> getAddlTalkGroupInfoMap() {
        return addlTalkGroupInfoMap;
    }

    public void setAddlTalkGroupInfoMap(Map<Integer, KnXDMAddlTalkGroupInfoDTO> addlTalkGroupInfoMap) {
        this.addlTalkGroupInfoMap = addlTalkGroupInfoMap;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public Map<Integer, Boolean> getGrpLocWatcherMap() {
        return grpLocWatcherMap;
    }

    public void setGrpLocWatcherMap(Map<Integer, Boolean> grpLocWatcherMap) {
        this.grpLocWatcherMap = grpLocWatcherMap;
    }

    @Override
    public String toString() {
        return "KnXDMBulkCorpGroupInfoRequestDTO{" +
                "operationType='" + operationType + '\'' +
                ", clientType=" + clientType +
                ", authDTO=" + authDTO +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", corpId='" + corpId + '\'' +
                ", userProfileId='" + userProfileId + '\'' +
                ", eTag='" + eTag + '\'' +
                ", groupInfoMap=" + groupInfoMap +
                ", addlTalkGroupInfoMap=" + addlTalkGroupInfoMap +
                ", hierarchyType=" + hierarchyType +
                ", grpLocWatcherMap=" + grpLocWatcherMap +
                '}';
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
