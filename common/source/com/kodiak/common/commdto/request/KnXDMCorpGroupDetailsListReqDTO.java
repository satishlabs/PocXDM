/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.List;
/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMCorpGroupDetailsListReqDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Jaikee Gupta             11-May-2023                  12.3.0
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 * <p>
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2023 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value= JsonInclude.Include.NON_NULL)
public class KnXDMCorpGroupDetailsListReqDTO implements IXDMRequestDTO{


    private static final long serialVersionUID = 1447026479247381251L;

    private List<KnXDMGroupDetailsReqDTO> groupDetailsReqList;
    private String version;

    private IAuthDTO authDTO;

    private String transactionId;

    private String destPttServerId;

    private String destQueueName;

    private String operationType;

    private KnConstants.HIERARCHY_TYPE hierarchyType;

    public List<KnXDMGroupDetailsReqDTO> getGroupDetailsReqList() {
        return groupDetailsReqList;
    }

    public void setGroupDetailsReqList(List<KnXDMGroupDetailsReqDTO> groupDetailsReqList) {
        this.groupDetailsReqList = groupDetailsReqList;
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
    public int getClientType() {
        return 0;
    }

    @Override
    public void setClientType(int clientType) {

    }

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }


	public String getVersion() { return version; }

    public void setVersion(String version) { this.version = version; }


    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public String toString() {
        return "KnXDMCorpGroupDetailsListReqDTO{" +
                "groupDetailsReqList=" + groupDetailsReqList +
                ", version='" + version + '\'' +
                ", authDTO=" + authDTO +
                ", transactionId='" + transactionId + '\'' +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                ", operationType='" + operationType + '\'' +
                ", hierarchyType=" + hierarchyType +
                '}';
    }
}

