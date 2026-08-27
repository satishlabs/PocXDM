/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnTargetMdnPermissionBitInfo;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnCorpSubMCPTTInfoRequestDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Dec 07, 2017                9.0
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

public class KnCorpSubMCPTTInfoRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO  {

    private static final long serialVersionUID = 7516471155622776456L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfoList;
    private String authorizedMdn;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private boolean upmCall;
    private Collection<KnTargetMdnPermissionBitInfo> targetProfileMdnPermissionBitInfoList;
    private boolean allAuTask;

    @Override
    public String getOperationType() {
        return operationType;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
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
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }


    public Collection<KnTargetMdnPermissionBitInfo> getTargetMdnPermissionBitInfoList() {
        return targetMdnPermissionBitInfoList;
    }

    public void setTargetMdnPermissionBitInfoList(Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfoList) {
        this.targetMdnPermissionBitInfoList = targetMdnPermissionBitInfoList;
    }

    public String getAuthorizedMdn() {
        return authorizedMdn;
    }

    public void setAuthorizedMdn(String authorizedMdn) {
        this.authorizedMdn = authorizedMdn;
    }

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public boolean isUpmCall() {
        return upmCall;
    }

    public void setUpmCall(boolean upmCall) {
        this.upmCall = upmCall;
    }

    public Collection<KnTargetMdnPermissionBitInfo> getTargetProfileMdnPermissionBitInfoList() {
        return targetProfileMdnPermissionBitInfoList;
    }

    public void setTargetProfileMdnPermissionBitInfoList(Collection<KnTargetMdnPermissionBitInfo> targetProfileMdnPermissionBitInfoList) {
        this.targetProfileMdnPermissionBitInfoList = targetProfileMdnPermissionBitInfoList;
    }

    public boolean isAllAuTask() {
        return allAuTask;
    }

    public void setAllAuTask(boolean allAuTask) {
        this.allAuTask = allAuTask;
    }

    @Override
    public String toString() {
        return "KnCorpSubMCPTTInfoRequestDTO{" +
                "targetMdnPermissionBitInfoList=" + targetMdnPermissionBitInfoList +
                ", authorizedMdn='" + KnGDPRTemplate.mdn(authorizedMdn) + '\'' +
                ", hierarchyType=" + hierarchyType +
                ", upmCall=" + upmCall +
                ", targetProfileMdnPermissionBitInfoList=" + targetProfileMdnPermissionBitInfoList +
                ", allAuTask=" + allAuTask +
                '}';
    }
}
