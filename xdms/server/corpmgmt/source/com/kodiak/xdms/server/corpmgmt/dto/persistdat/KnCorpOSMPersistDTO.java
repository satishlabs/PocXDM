/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.commdto.request.KnCorpOperationStatusMesssageInfoDTO;
import com.kodiak.common.commdto.request.KnXDMOSMInfoRequestDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class KnCorpOSMPersistDTO extends KnCorpInfoDTO implements IPersistenceDTO {


    private static final long serialVersionUID = -5492933669000867307L;

    private String etag;
    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO persistenceDTO;

    private String internalCorpId;
    private String OSMListName;
    private String OSMListId;
    private Set<KnXDMOSMInfoRequestDTO> addedOSMMsgList;
    private Set<KnXDMOSMInfoRequestDTO> modifiedOSMMsgList;
    private Set<KnXDMOSMInfoRequestDTO> removedOSMMsgList;
    private String isDefault;

    private String maxStatusMsgPerOsmList;
    private String maxStatusShortTextLength;
    private String maxStatusMsgLength;
    private String maxFormMsgAppendLength;
    private List<KnXDMOSMInfoRequestDTO> osmInfoList;
    private Set<KnCorpOperationStatusMesssageInfoDTO> corpOSMList;
    private int uniqueOSMInfoCount;
    private Map<Integer, Integer> osmListIdAndDefultMap;
    private KnCorpOperationStatusMesssageInfoDTO OSMListDetails;
    private List<Integer> groupIds;
    private String hierarchyId;
    private List<KnCorpGroupDTO> corpGroupInfoList;

    @Override
    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    @Override
    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    @Override
    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    @Override
    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
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
        return "" + super.getCorpId();
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public void setProfile(String profile) {
        this.profile = profile;
    }


    public String getOSMListName() {
        return OSMListName;
    }

    public void setOSMListName(String OSMListName) {
        this.OSMListName = OSMListName;
    }

    public String getOSMListId() {
        return OSMListId;
    }

    public void setOSMListId(String OSMListId) {
        this.OSMListId = OSMListId;
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

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getInternalCorpId() {
        return internalCorpId;
    }

    public void setInternalCorpId(String internalCorpId) {
        this.internalCorpId = internalCorpId;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getMaxStatusMsgPerOsmList() {
        return maxStatusMsgPerOsmList;
    }

    public void setMaxStatusMsgPerOsmList(String maxStatusMsgPerOsmList) {
        this.maxStatusMsgPerOsmList = maxStatusMsgPerOsmList;
    }

    public String getMaxStatusShortTextLength() {
        return maxStatusShortTextLength;
    }

    public void setMaxStatusShortTextLength(String maxStatusShortTextLength) {
        this.maxStatusShortTextLength = maxStatusShortTextLength;
    }

    public String getMaxStatusMsgLength() {
        return maxStatusMsgLength;
    }

    public void setMaxStatusMsgLength(String maxStatusMsgLength) {
        this.maxStatusMsgLength = maxStatusMsgLength;
    }

    public String getMaxFormMsgAppendLength() {
        return maxFormMsgAppendLength;
    }

    public void setMaxFormMsgAppendLength(String maxFormMsgAppendLength) {
        this.maxFormMsgAppendLength = maxFormMsgAppendLength;
    }

    public Map<Integer, Integer> getOsmListIdAndDefultMap() {
        return osmListIdAndDefultMap;
    }

    public void setOsmListIdAndDefultMap(Map<Integer, Integer> osmListIdAndDefultMap) {
        this.osmListIdAndDefultMap = osmListIdAndDefultMap;
    }

    public List<KnXDMOSMInfoRequestDTO> getOsmInfoList() {
        return osmInfoList;
    }

    public void setOsmInfoList(List<KnXDMOSMInfoRequestDTO> osmInfoList) {
        this.osmInfoList = osmInfoList;
    }

    public Set<KnCorpOperationStatusMesssageInfoDTO> getCorpOSMList() {
        return corpOSMList;
    }

    public void setCorpOSMList(Set<KnCorpOperationStatusMesssageInfoDTO> corpOSMList) {
        this.corpOSMList = corpOSMList;
    }

    public int getUniqueOSMInfoCount() {
        return uniqueOSMInfoCount;
    }

    public void setUniqueOSMInfoCount(int uniqueOSMInfoCount) {
        this.uniqueOSMInfoCount = uniqueOSMInfoCount;
    }

    public KnCorpOperationStatusMesssageInfoDTO getOSMListDetails() {
        return OSMListDetails;
    }

    public void setOSMListDetails(KnCorpOperationStatusMesssageInfoDTO OSMListDetails) {
        this.OSMListDetails = OSMListDetails;
    }

    public List<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public List<KnCorpGroupDTO> getCorpGroupInfoList() {
        return corpGroupInfoList;
    }

    public void setCorpGroupInfoList(List<KnCorpGroupDTO> corpGroupInfoList) {
        this.corpGroupInfoList = corpGroupInfoList;
    }

    @Override
    public String toString() {
        return "KnCorpOSMPersistDTO{" +
                "etag='" + etag + '\'' +
                ", inputDTO=" + inputDTO +
                ", entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", profile='" + profile + '\'' +
                ", persistenceDTO=" + persistenceDTO +
                ", internalCorpId='" + internalCorpId + '\'' +
                ", OSMListName='" + OSMListName + '\'' +
                ", OSMListId='" + OSMListId + '\'' +
                ", addedOSMMsgList=" + addedOSMMsgList +
                ", modifiedOSMMsgList=" + modifiedOSMMsgList +
                ", removedOSMMsgList=" + removedOSMMsgList +
                ", isDefault='" + isDefault + '\'' +
                ", maxStatusMsgPerOsmList='" + maxStatusMsgPerOsmList + '\'' +
                ", maxStatusShortTextLength='" + maxStatusShortTextLength + '\'' +
                ", maxStatusMsgLength='" + maxStatusMsgLength + '\'' +
                ", maxFormMsgAppendLength='" + maxFormMsgAppendLength + '\'' +
                ", osmInfoList=" + osmInfoList +
                ", corpOSMList=" + corpOSMList +
                ", uniqueOSMInfoCount=" + uniqueOSMInfoCount +
                ", OSMListDetails=" + OSMListDetails +
                ", osmListIdAndDefultMap=" + osmListIdAndDefultMap +
                ", groupIds=" + groupIds +
                ", hierarchyId=" + hierarchyId +
                ", corpGroupInfoList=" + corpGroupInfoList +
                '}';
    }
}
