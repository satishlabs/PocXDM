/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupProfileInfo;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupInfoDTO;

import java.util.List;
import java.util.Map;

public class KnCorpBulkGroupPersistDTO extends KnCorpGroupInfoDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676143L;


    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO performerDetails;

    /*private Integer corpId;
    private Integer groupId;*/
    private Integer corpGroupCount;
    private KnCorpProfileDTO corpProfile;
    private KnCorpGroupProfileInfo groupProfile;
    private List<String> exisingGroupNameList;
    Map<String, String> paramNameValueMapCommon;


    /*public String getObjectId() {
        return "" + groupId;
    }*/

    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.performerDetails = persistenceDTO;
    }

    public IPersistenceDTO getPersistenceDTO() {
        return performerDetails;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationType() {
        return operationType;
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

    public IPersistenceDTO getPerformerDetails() {
        return performerDetails;
    }

    public void setPerformerDetails(IPersistenceDTO performerDetails) {
        this.performerDetails = performerDetails;
    }


    /*public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
*/
    public Integer getCorpGroupCount() {
        return corpGroupCount;
    }

    public void setCorpGroupCount(Integer corpGroupCount) {
        this.corpGroupCount = corpGroupCount;
    }

    public KnCorpProfileDTO getCorpProfile() {
        return corpProfile;
    }

    public void setCorpProfile(KnCorpProfileDTO corpProfile) {
        this.corpProfile = corpProfile;
    }

    public KnCorpGroupProfileInfo getGroupProfile() {
        return groupProfile;
    }

    public void setGroupProfile(KnCorpGroupProfileInfo groupProfile) {
        this.groupProfile = groupProfile;
    }

    public List<String> getExisingGroupNameList() {
        return exisingGroupNameList;
    }

    public void setExisingGroupNameList(List<String> exisingGroupNameList) {
        this.exisingGroupNameList = exisingGroupNameList;
    }

    public Map<String, String> getParamNameValueMapCommon() {
        return paramNameValueMapCommon;
    }

    public void setParamNameValueMapCommon(Map<String, String> paramNameValueMapCommon) {
        this.paramNameValueMapCommon = paramNameValueMapCommon;
    }

    @Override
    public String toString() {
        return "KnCorpBulkGroupPersistDTO{" +
                "inputDTO=" + inputDTO +
                ", entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", profile='" + profile + '\'' +
                ", performerDetails=" + performerDetails +
                /*", corpId=" + corpId +
                ", groupId=" + groupId +*/
                ", corpGroupCount=" + corpGroupCount +
                ", corpProfile=" + corpProfile +
                ", groupProfile=" + groupProfile +
                ", exisingGroupNameList=" + exisingGroupNameList +
                //", paramNameValueMapCommon=" + paramNameValueMapCommon +
                '}';
    }
}
