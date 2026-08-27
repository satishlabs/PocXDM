package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnIdDetailsListDTO;
import com.kodiak.common.resources.KnConstants;

import java.util.List;
import java.util.Map;

public class KnCreateHirarchyRequestDTO implements IXDMRequestDTO {
    private static final long serialVersionUID = 7526472295622776147L;
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
    private KnIdDetailsListDTO idDetailsListDTO;
    private List<String> removedHierarchy;

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
        return null;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {

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

    @Override
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {

    }

    @Override
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return null;
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

    public KnIdDetailsListDTO getIdDetailsListDTO() {
        return idDetailsListDTO;
    }

    public void setIdDetailsListDTO(KnIdDetailsListDTO idDetailsListDTO) {
        this.idDetailsListDTO = idDetailsListDTO;
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

    public List<String> getRemovedHierarchy() {
        return removedHierarchy;
    }

    public void setRemovedHierarchy(List<String> removedHierarchy) {
        this.removedHierarchy = removedHierarchy;
    }

    @Override
    public String toString() {
        return "KnCreateHirarchyRequestDTO{" +
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
                ", idDetailsListDTO=" + idDetailsListDTO +
                ", removedHierarchy=" + removedHierarchy +
                '}';
    }

    @Override
    public String getObjectId() {
        return "";
    }
}

