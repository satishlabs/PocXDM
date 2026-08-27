/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     24/12/13         7.7.0
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
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.List;

public class KnCorpPAMSubsReqDTO implements IXDMRequestDTO {
    private static final long serialVersionUID = 7526471155622776765L;

    private String operationType;
    private int clientType;
    private IAuthDTO authDTO;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;

    private int pamAccId;
    private int unUsedMdnCount;
    private String xdmsHome;
    private List<String> cleanUpMdnLst;
    private String extCorpId;
    private KnConstants.HIERARCHY_TYPE hierarchyType;

    public int getPamAccId() {
        return pamAccId;
    }

    public void setPamAccId(int pamAccId) {
        this.pamAccId = pamAccId;
    }

    public int getUnUsedMdnCount() {
        return unUsedMdnCount;
    }

    public void setUnUsedMdnCount(int unUsedMdnCount) {
        this.unUsedMdnCount = unUsedMdnCount;
    }

    public String getXdmsHome() {
        return xdmsHome;
    }

    public void setXdmsHome(String xdmsHome) {
        this.xdmsHome = xdmsHome;
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
        return null;
    }

    public List<String> getCleanUpMdnLst() {
        return cleanUpMdnLst;
    }

    public void setCleanUpMdnLst(List<String> cleanUpMdnLst) {
        this.cleanUpMdnLst = cleanUpMdnLst;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    @Override
	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
		return hierarchyType;
	}

	@Override
	public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("pamAccId - ").append(pamAccId);
        sb.append(",clientType - ").append(clientType);
        sb.append(",unUsedMdnCount - ").append(unUsedMdnCount);
        sb.append(",xdmsHome - ").append(xdmsHome);
        sb.append(",cleanUpMdnLst - ").append(KnGDPRTemplate.mdnList(cleanUpMdnLst));
        sb.append(",extCorpId - ").append(extCorpId);
        sb.append(", hierarchyType - ").append(hierarchyType);
        return sb.toString();
    }
}
