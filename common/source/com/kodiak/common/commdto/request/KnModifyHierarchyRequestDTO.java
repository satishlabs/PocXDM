/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnModifiedIdDetailsListDTO;
import com.kodiak.common.resources.KnConstants;

import java.io.Serial;
import java.util.List;
import java.util.Map;

public class KnModifyHierarchyRequestDTO implements IXDMRequestDTO {

    @Serial
    private static final long serialVersionUID = 7526472295622776148L;

    private String operationType;
    private String entityId;
    private String profile;
    private int clientType;
    private String destPttServerId;
    private String destQueueName;
    private String transactionId;
    private String version;
    private Map<String, Object> customParamMap;
    private String corpId;
    private List<KnModifiedIdDetailsListDTO> modifiedIdDetails;

    @Override
    public IAuthDTO getAuthDTO() {
        return null;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {

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
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {

    }

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return null;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public List<KnModifiedIdDetailsListDTO> getModifiedIdDetails() {
        return modifiedIdDetails;
    }

    public void setModifiedIdDetails(List<KnModifiedIdDetailsListDTO> modifiedIdDetails) {
        this.modifiedIdDetails = modifiedIdDetails;
    }

    @Override
    public String toString() {
        return "KnModifyHierarchyRequestDTO{" +
                "operationType='" + operationType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", profile='" + profile + '\'' +
                ", clientType=" + clientType +
                ", destPttServerId='" + destPttServerId + '\'' +
                ", destQueueName='" + destQueueName + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", version='" + version + '\'' +
                ", customParamMap=" + customParamMap +
                ", corpId='" + corpId + '\'' +
                ", modifiedIdDetails=" + modifiedIdDetails +
                '}';
    }

    @Override
    public String getObjectId() {
        return "";
    }
}
