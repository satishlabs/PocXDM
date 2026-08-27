/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnTargetMdnInfoDTO;
import com.kodiak.common.resources.KnConstants;

import java.io.Serializable;
import java.util.List;

/**
 * Created by schandra on 18-12-2017.
 */
public class KnXDMAuthListRequestDTO implements IXDMRequestDTO,Serializable {

    private static final long serialVersionUID = -2160319161941557979L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String objectId;

    private String transactionId;
    private String destPttServerId;
    private String destQueueName;
    private String authMdn;
    private int etag;
    private int ifMatch;
    private int ifNoneMatch;
    private List<KnTargetMdnInfoDTO> targetInfo;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String version;
    private String mcpttId;

    public String getAuthMdn() {
        return authMdn;
    }

    public void setAuthMdn(String authMdn) {
        this.authMdn = authMdn;
    }

    public List<KnTargetMdnInfoDTO> getTargetInfo() {
        return targetInfo;
    }

    public void setTargetInfo(List<KnTargetMdnInfoDTO> targetInfo) {
        this.targetInfo = targetInfo;
    }

    public int getEtag() {
        return etag;
    }

    public void setEtag(int etag) {
        this.etag = etag;
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
        this.destPttServerId = destPttServerId;
    }

    public String getDestQueueName() {
        return destQueueName;
    }

    public void setDestQueueName(String destQueueName) {
        this.destQueueName = destQueueName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
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

	public String getMcpttId() {
		return mcpttId;
	}

	public void setMcpttId(String mcpttId) {
		this.mcpttId = mcpttId;
	}
}
