/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KnCorpModifyUserProfileDTO extends KnCorpUserProfileDTO {

    private Set<KnCorpGroupListInfoDTO> addedGroupList;
    private Set<KnCorpGroupListInfoDTO> modifiedGroupList;
    private Set<String> removedGroupIdsList;
    private Set<KnCorpUserProfileMCPTTConfig> addedMcpttPermissionsConfig;
    private Set<KnCorpUserProfileMCPTTConfig> modifiedPermissionsConfig;
    private Set<KnCorpUserProfileMCPTTConfig> removedMcpttPermissionsConfig;
    private KnUserProfileFSDTO userProfileFSDto;
    private String userProfileSharingEnabled;
    private List<String> addUserProfileSharedCorpList;
    private List<String> removeUserProfileSharedCorpList;

    public Set<KnCorpGroupListInfoDTO> getAddedGroupList() {
        return addedGroupList;
    }

    public void setAddedGroupList(Set<KnCorpGroupListInfoDTO> addedGroupList) {
        this.addedGroupList = addedGroupList;
    }

    public Set<KnCorpGroupListInfoDTO> getModifiedGroupList() {
        return modifiedGroupList;
    }

    public void setModifiedGroupList(Set<KnCorpGroupListInfoDTO> modifiedGroupList) {
        this.modifiedGroupList = modifiedGroupList;
    }

    public Set<String> getRemovedGroupIdsList() {
        return removedGroupIdsList;
    }

    public void setRemovedGroupIdsList(Set<String> removedGroupIdsList) {
        this.removedGroupIdsList = removedGroupIdsList;
    }

    public Set<KnCorpUserProfileMCPTTConfig> getAddedMcpttPermissionsConfig() {
        return addedMcpttPermissionsConfig;
    }

    public void setAddedMcpttPermissionsConfig(Set<KnCorpUserProfileMCPTTConfig> addedMcpttPermissionsConfig) {
        this.addedMcpttPermissionsConfig = addedMcpttPermissionsConfig;
    }

    public Set<KnCorpUserProfileMCPTTConfig> getModifiedPermissionsConfig() {
        return modifiedPermissionsConfig;
    }

    public void setModifiedPermissionsConfig(Set<KnCorpUserProfileMCPTTConfig> modifiedPermissionsConfig) {
        this.modifiedPermissionsConfig = modifiedPermissionsConfig;
    }

    public Set<KnCorpUserProfileMCPTTConfig> getRemovedMcpttPermissionsConfig() {
        return removedMcpttPermissionsConfig;
    }

    public void setRemovedMcpttPermissionsConfig(Set<KnCorpUserProfileMCPTTConfig> removedMcpttPermissionsConfig) {
        this.removedMcpttPermissionsConfig = removedMcpttPermissionsConfig;
    }

    public KnUserProfileFSDTO getUserProfileFSDto() {
        return userProfileFSDto;
    }

    public void setUserProfileFSDto(KnUserProfileFSDTO userProfileFSDto) {
        this.userProfileFSDto = userProfileFSDto;
    }


    public List<String> getAddUserProfileSharedCorpList() { return addUserProfileSharedCorpList; }

    public void setAddUserProfileSharedCorpList(List<String> addUserProfileSharedCorpList) { this.addUserProfileSharedCorpList = addUserProfileSharedCorpList; }

    public List<String> getRemoveUserProfileSharedCorpList() { return removeUserProfileSharedCorpList; }

    public void setRemoveUserProfileSharedCorpList(List<String> removeUserProfileSharedCorpList) { this.removeUserProfileSharedCorpList = removeUserProfileSharedCorpList; }

    public String getUserProfileSharingEnabled() { return userProfileSharingEnabled; }

    public void setUserProfileSharingEnabled(String userProfileSharingEnabled) { this.userProfileSharingEnabled = userProfileSharingEnabled; }

    @Override
    public String toString() {
        return "KnCorpModifyUserProfileDTO{" +
                "KnCorpUserProfileDTO= "+super.toString()+
                "addedGroupList=" + addedGroupList +
                ", modifiedGroupList=" + modifiedGroupList +
                ", removedGroupIdsList=" + removedGroupIdsList +
                ", addedMcpttPermissionsConfig=" + addedMcpttPermissionsConfig +
                ", modifiedPermissionsConfig=" + modifiedPermissionsConfig +
                ", removedMcpttPermissionsConfig=" + removedMcpttPermissionsConfig +
                ", userProfileFSDto=" + userProfileFSDto +
                ", addUserProfileSharedCorpList=" + addUserProfileSharedCorpList +
                ", removeUserProfileSharedCorpList=" + removeUserProfileSharedCorpList +
                ", userProfileSharingEnabled=" + userProfileSharingEnabled +
                '}';
    }
}
