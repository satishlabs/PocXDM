/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.commdto.request.KnXDMOSMInfoRequestDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class KnIPOsmDTO implements IInputDTO {

    private String entityId;
    private String operationType;
    private String profile;
    private String performer;

    private String corpId;
    private String OSMListId;
    private String OSMListName;
    private String isDefault;
    private Set<KnXDMOSMInfoRequestDTO> addedOSMMsgList;
    private Set<KnXDMOSMInfoRequestDTO> modifiedOSMMsgList;
    private Set<KnXDMOSMInfoRequestDTO> removedOSMMsgList;
    private Map<String, Object> customParamMap;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private Collection<String> assignedOSMIdToGroupIds;
    private Collection<String> removedOSMIdFromGroupIds;
    private Collection<String> OSMListIds;
    private String hierarchyId;


    @Override
    public void setPerformer(String performer) {
        this.performer=performer;
    }

    @Override
    public String getPerformer() {
        return performer;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {

    }

    @Override
    public IAuthDTO getAuthDTO() {
        return null;
    }

    @Override
    public void setClientType(int clientType) {

    }

    @Override
    public int getClientType() {
        return 0;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Override
    public String getOperationType() {
        return operationType;
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
    public String getObjectId() {
        return "" + getCorpId();
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public void setProfile(String profile) {
        this.profile=profile;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getOSMListId() {
        return OSMListId;
    }

    public void setOSMListId(String OSMListId) {
        this.OSMListId = OSMListId;
    }

    public String getOSMListName() {
        return OSMListName;
    }

    public void setOSMListName(String OSMListName) {
        this.OSMListName = OSMListName;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public Set<KnXDMOSMInfoRequestDTO> getAddedOSMMsgList() {
        return addedOSMMsgList;
    }

    public void setAddedOSMMsgList(Set<KnXDMOSMInfoRequestDTO> addedOSMMsgList) {
        this.addedOSMMsgList = addedOSMMsgList;
    }

    public Set<KnXDMOSMInfoRequestDTO> getModifiedOSMMsgList() {
        return modifiedOSMMsgList;
    }

    public void setModifiedOSMMsgList(Set<KnXDMOSMInfoRequestDTO> modifiedOSMMsgList) {
        this.modifiedOSMMsgList = modifiedOSMMsgList;
    }

    public Set<KnXDMOSMInfoRequestDTO> getRemovedOSMMsgList() {
        return removedOSMMsgList;
    }

    public void setRemovedOSMMsgList(Set<KnXDMOSMInfoRequestDTO> removedOSMMsgList) {
        this.removedOSMMsgList = removedOSMMsgList;
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

    public Collection<String> getAssignedOSMIdToGroupIds() {
        return assignedOSMIdToGroupIds;
    }

    public void setAssignedOSMIdToGroupIds(Collection<String> assignedOSMIdToGroupIds) {
        this.assignedOSMIdToGroupIds = assignedOSMIdToGroupIds;
    }

    public Collection<String> getRemovedOSMIdFromGroupIds() {
        return removedOSMIdFromGroupIds;
    }

    public void setRemovedOSMIdFromGroupIds(Collection<String> removedOSMIdFromGroupIds) {
        this.removedOSMIdFromGroupIds = removedOSMIdFromGroupIds;
    }

    public Collection<String> getOSMListIds() {
        return OSMListIds;
    }

    public void setOSMListIds(Collection<String> OSMListIds) {
        this.OSMListIds = OSMListIds;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    @Override
    public String toString() {
        return "KnIPOsmDTO{" +
                "entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", profile='" + profile + '\'' +
                ", performer='" + performer + '\'' +
                ", corpId='" + corpId + '\'' +
                ", OSMListId='" + OSMListId + '\'' +
                ", OSMListName='" + OSMListName + '\'' +
                ", isDefault='" + isDefault + '\'' +
                ", addedOSMMsgList=" + addedOSMMsgList +
                ", modifiedOSMMsgList=" + modifiedOSMMsgList +
                ", removedOSMMsgList=" + removedOSMMsgList +
                ", customParamMap=" + customParamMap +
                ", hierarchyType=" + hierarchyType +
                ", assignedOSMIdToGroupIds=" + assignedOSMIdToGroupIds +
                ", removedOSMIdFromGroupIds=" + removedOSMIdFromGroupIds +
                ", OSMListIds=" + OSMListIds +
                ", hierarchyId=" + hierarchyId +
                '}';
    }
}
