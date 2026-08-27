/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.persistdat;

import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonInclude;
import com.kodiak.xdms.server.common.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpUserProfileMCPTTConfig;
import com.kodiak.xdms.server.common.dto.common.KnSubsEmergencyConfigDTO;

import java.util.Set;
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value= JsonInclude.Include.NON_NULL)
public class KnCorpUserProfileDTO {
    private String _id;
    private Integer userProfileIndex;
    private String userProfileName;
    private Long createTimeStamp;
    private int userProfileStatus;
    private Long updateTimeStamp;
    private Integer corporateID;
    private Integer contactListID;
    private Set<KnCorpGroupListInfoDTO> groupList;
    private String featureBS;
    private KnSubsEmergencyConfigDTO EmergencyConfig;
    private Set<KnCorpUserProfileMCPTTConfig> mcpttPermissionsConfig;
    private String type="mcsUserProfile";
    private String ver="1.0";
    private Boolean sharingEnabled;
    private Integer ownerCorpId;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Integer getUserProfileIndex() {
        return userProfileIndex;
    }

    public void setUserProfileIndex(Integer userProfileIndex) {
        this.userProfileIndex = userProfileIndex;
    }

    public String getUserProfileName() {
        return userProfileName;
    }

    public void setUserProfileName(String userProfileName) {
        this.userProfileName = userProfileName;
    }

    public Long getCreateTimeStamp() {
        return createTimeStamp;
    }

    public void setCreateTimeStamp(Long createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

    public Long getUpdateTimeStamp() {
        return updateTimeStamp;
    }

    public void setUpdateTimeStamp(Long updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public Integer getCorporateID() {
        return corporateID;
    }

    public void setCorporateID(Integer corporateID) {
        this.corporateID = corporateID;
    }

    public Integer getContactListID() {
        return contactListID;
    }

    public void setContactListID(Integer contactListID) {
        this.contactListID = contactListID;
    }

    public Set<KnCorpGroupListInfoDTO> getGroupList() {
        return groupList;
    }

    public void setGroupList(Set<KnCorpGroupListInfoDTO> groupList) {
        this.groupList = groupList;
    }

    public String getFeatureBS() {
        return featureBS;
    }

    public void setFeatureBS(String featureBS) {
        this.featureBS = featureBS;
    }

    public KnSubsEmergencyConfigDTO getEmergencyConfig() {
        return EmergencyConfig;
    }

    public void setEmergencyConfig(KnSubsEmergencyConfigDTO emergencyConfig) {
        EmergencyConfig = emergencyConfig;
    }

    public Set<KnCorpUserProfileMCPTTConfig> getMcpttPermissionsConfig() { return mcpttPermissionsConfig; }

    public void setMcpttPermissionsConfig(Set<KnCorpUserProfileMCPTTConfig> mcpttPermissionsConfig) { this.mcpttPermissionsConfig = mcpttPermissionsConfig; }

    public int getUserProfileStatus() { return userProfileStatus; }

    public void setUserProfileStatus(int userProfileStatus) { this.userProfileStatus = userProfileStatus; }

    public Boolean getSharingEnabled() { return sharingEnabled; }

    public void setSharingEnabled(Boolean sharingEnabled) { this.sharingEnabled = sharingEnabled; }

    public Integer getOwnerCorpId() { return ownerCorpId; }

    public void setOwnerCorpId(Integer ownerCorpId) { this.ownerCorpId = ownerCorpId; }


    @Override
    public String toString() {
        return "KnCorpUserProfileDTO{" +
                "type='" + type + '\'' +
                ", ver='" + ver + '\'' +
                ", _id='" + _id + '\'' +
                ", userProfileIndex=" + userProfileIndex +
                ", userProfileName='" + userProfileName + '\'' +
                ", createTimeStamp=" + createTimeStamp +
                ", userProfileStatus=" + userProfileStatus +
                ", updateTimeStamp=" + updateTimeStamp +
                ", corporateID=" + corporateID +
                ", contactListID=" + contactListID +
                ", groupList=" + groupList +
                ", FeatureBS='" + featureBS + '\'' +
                ", EmergencyConfig=" + EmergencyConfig +
                ", mcpttPermissionsConfig=" + mcpttPermissionsConfig +
                ", sharingEnabled=" + sharingEnabled +
                ", ownerCorpId=" + ownerCorpId +
                '}';
    }
}
