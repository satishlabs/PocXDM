package com.kodiak.xdms.server.corpmgmt.dto.clientdat;


import com.kodiak.common.resources.KnConstants;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KnIPUnAllocateSubscriberDTO implements IInputDTO {
    private IAuthDTO authDTO;
    private String entityId;
    private String operationType;
    private int clientType;
    private String profile;
    private String performer;
    private String corpId;
    private Map<String, Object> customParamMap;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private String hierarchyId;
    private List<String> mdnList;

    @Override
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(String entityId) {
        this.entityId = entityId;
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
        return clientType;
    }

    @Override
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String getPerformer() {
        return performer;
    }

    @Override
    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    @Override
    public String getObjectId() {
        return getObjectId();
    }

    @Override
    public String toString() {
        return "KnIPAllocateSubscriberDTO{" +
                "authDTO=" + authDTO +
                ", entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", clientType=" + clientType +
                ", profile='" + profile + '\'' +
                ", performer='" + performer + '\'' +
                ", corpId='" + corpId + '\'' +
                ", customParamMap=" + customParamMap +
                ", hierarchyType=" + hierarchyType +
                ", hierarchyId='" + hierarchyId + '\'' +
                ", mdnList=" + mdnList +
                '}';
    }
}