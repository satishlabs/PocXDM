/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

/**
 * *********************************************************************
 * File name:   KnXDMScanlistInfoDTO.java
 * Subsystem:   PoCXDM
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Hemanta Tahu          24/7/14      7.5
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
 *
 * *************************************************************************
 */

import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.io.Serializable;
import java.util.List;

public class KnXDMScanlistInfoDTO  implements IXDMRequestDTO,Serializable {

    private static final long serialVersionUID = 2765996681123741538L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String objectId;

    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private String mdn;
    private String etag;
    private List<KnXDMTalkGroupInfoDTO> scanList;
    private String scanListId;
    private String name;
    private String enabled;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String version;
    private String mcPttId;

    public String getMcPttId() {
		return mcPttId;
	}

	public void setMcPttId(String mcPttId) {
		this.mcPttId = mcPttId;
	}

	public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public List<KnXDMTalkGroupInfoDTO> getScanList() {
        return scanList;
    }

    public void setScanList(List<KnXDMTalkGroupInfoDTO> scanList) {
        this.scanList = scanList;
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

    public String getScanListId() {
        return scanListId;
    }

    public void setScanListId(String scanListId) {
        this.scanListId = scanListId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
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

    @Override
    public String toString() {
        return "KnXDMScanlistInfoDTO{" +
                "operationType='" + operationType + '\'' +
                ", clientType=" + clientType +
                ", mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", enabled='" + enabled + '\'' +
                ", version='" + version + '\'' +
                ", mcPttId='" + KnGDPRTemplate.mcpttId(mcPttId) + '\'' +
                '}';
    }
}
