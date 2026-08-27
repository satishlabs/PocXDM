/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import java.util.List;
import java.util.Set;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonProperty;

public class KnCorpUserProfileDTO {

    @JsonProperty(value ="id")
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
    private Set<KnCorpUserProfileMCPTTConfig> mcpttPermissionsConfigIncb;
    private String type="mcsUserProfile";
    private String ver="1.0";
    private String tgscMode;
    private List<String> ownerIdList;
    private boolean userProfilebelongstomdn;
    private Boolean sharingEnabled;
    private String selfDnDPrivilege;
    private String hierarchyId;
    /**
     * As discuss corporateID  will be owner corpId.
     */
  //  private Integer ownerCorpId;



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

    public String getTgscMode() {   return tgscMode; }

    public void setTgscMode(String tgscMode) {  this.tgscMode = tgscMode;  }

    public List<String> getOwnerIdList() { return ownerIdList; }

    public void setOwnerIdList(List<String> ownerIdList) { this.ownerIdList = ownerIdList; }

    public boolean isUserProfilebelongstomdn() { return userProfilebelongstomdn; }

    public void setUserProfilebelongstomdn(boolean userProfilebelongstomdn) { this.userProfilebelongstomdn = userProfilebelongstomdn; }

    public Boolean getSharingEnabled() { return sharingEnabled; }

    public void setSharingEnabled(Boolean sharingEnabled) { this.sharingEnabled = sharingEnabled; }

    public Set<KnCorpUserProfileMCPTTConfig> getMcpttPermissionsConfigIncb() {
        return mcpttPermissionsConfigIncb;
    }

    public void setMcpttPermissionsConfigIncb(Set<KnCorpUserProfileMCPTTConfig> mcpttPermissionsConfigIncb) {
        this.mcpttPermissionsConfigIncb = mcpttPermissionsConfigIncb;
    }

    public String getSelfDnDPrivilege() {
        return selfDnDPrivilege;
    }

    public void setSelfDnDPrivilege(String selfDnDPrivilege) {
        this.selfDnDPrivilege = selfDnDPrivilege;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    /*public Integer getOwnerCorpId() { return ownerCorpId; }

    public void setOwnerCorpId(Integer ownerCorpId) { this.ownerCorpId = ownerCorpId; }
*/
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
                ", tgscMode=" + tgscMode +
                ", ownerIdList=" + ownerIdList +
                ", userProfilebelongstomdn=" + userProfilebelongstomdn +
                ", sharingEnabled=" + sharingEnabled +
                ", selfDnDPrivilege=" + selfDnDPrivilege +
                ", hierarchyId='" + hierarchyId + '\'' +
                '}';
    }
}
