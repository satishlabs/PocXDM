/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnSubscriberFeatureBitInfo;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.resources.KnConstants;

import java.util.List;


public class KnCorpSubbscrFeatureInfoRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO  {

    private static final long serialVersionUID = 7526471155622776153L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;

    private List<KnSubscriberFeatureBitInfo> subsFeatureInfoList;
    private KnConstants.HIERARCHY_TYPE hierarchyType;


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

    public List<KnSubscriberFeatureBitInfo> getSubsFeatureInfoList() {
        return subsFeatureInfoList;
    }

    public void setSubsFeatureInfoList(List<KnSubscriberFeatureBitInfo> subsFeatureInfoList) {
        this.subsFeatureInfoList = subsFeatureInfoList;
    }

    @Override
	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	@Override
	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append("subsFeatureInfoList - ").append(subsFeatureInfoList);
        sb.append(", hierarchyType - ").append(hierarchyType);
        return sb.toString();
    }
}
