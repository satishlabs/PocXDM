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
 * Sanjiv K Acharyya     29/10/13         7.7.0
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
import java.util.Map;

public class KnXDMTalkGrpReqestDTO implements IXDMRequestDTO{

    private static final long serialVersionUID = 7526471155622687641L;

    private int clientType;

    private IAuthDTO authDTO;

    private String transactionId;

    private String destPttServerId;

    private String destQueueName;

    private String operationType;
    private String mdn;
    private List<Integer> assignedGrpList;
    private List<Integer> deAssignedGrpList;
    private int corpId;
    private Map<String, Object> customParamMap;
    private String deleteType;
    private int camped_by;
    private KnConstants.HIERARCHY_TYPE hierarchyType;

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
    	return hierarchyType;
    }

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
    	this.hierarchyType = hierarchyType;
    }

    public String getObjectId() {
        return operationType;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public List<Integer> getAssignedGrpList() {
        return assignedGrpList;
    }

    public void setAssignedGrpList(List<Integer> assignedGrpList) {
        this.assignedGrpList = assignedGrpList;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public List<Integer> getDeAssignedGrpList() {
        return deAssignedGrpList;
    }

    public void setDeAssignedGrpList(List<Integer> deAssignedGrpList) {
        this.deAssignedGrpList = deAssignedGrpList;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String getDeleteType() {
        return deleteType;
    }

    public void setDeleteType(String deleteType) {
        this.deleteType = deleteType;
    }

    public int getCamped_by() {
        return camped_by;
    }

    public void setCamped_by(int camped_by) {
        this.camped_by = camped_by;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(50);
        sb.append("corpId - ").append(corpId);
        sb.append(", mdn - ").append(KnGDPRTemplate.mdn(mdn));
        sb.append(", assignedGrpList - ").append(assignedGrpList);
        sb.append(", deAssignedGrpList - ").append(deAssignedGrpList);
        sb.append(", customParamMap - ").append(customParamMap);
        sb.append(", deleteType - ").append(deleteType);
        sb.append(", camped_by - ").append(camped_by);
        sb.append(", hierarchyType - ").append(hierarchyType);
        return sb.toString();
    }
}
