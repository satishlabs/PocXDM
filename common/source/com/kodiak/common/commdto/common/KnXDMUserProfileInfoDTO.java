/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import java.io.Serializable;
import java.util.*;

public class KnXDMUserProfileInfoDTO implements Serializable {

    private static final long serialVersionUID = 9138015816084882265L;

    private String profileName;
    private String contactListId;
    private Set<KnXDMGroupListInfoDTO> groupListInfo;
    private KnXDMEmergencyConfig emergencyAttributes;
    private String profileId;
    private KnXDMUserProfileFSDTO userProfileFS;
    private Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfoList;
    private Set<KnXDMGroupListInfoDTO> addedGroupListInfo;
    private Set<KnXDMGroupListInfoDTO> modifiedGroupListInfo;
    private Set<String> removedGroupIds;
    private Collection<KnTargetMdnPermissionBitInfo> addedMdnPerms;
    private Collection<KnTargetMdnPermissionBitInfo> modifiedMdnPerms;
    private Collection<KnTargetMdnPermissionBitInfo> removedMdnPerms;
    private String tgscMode;
    private String sharingEnabled;
    private List<String> addUserProfileSharedCorpList;
    private List<String> removeUserProfileSharedCorpList;


    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getContactListId() {
        return contactListId;
    }

    public void setContactListId(String contactListId) {
        this.contactListId = contactListId;
    }

    public Set<KnXDMGroupListInfoDTO> getGroupListInfo() {
        return groupListInfo;
    }

    public void setGroupListInfo(Set<KnXDMGroupListInfoDTO> groupListInfo) {
        this.groupListInfo = groupListInfo;
    }

    public KnXDMEmergencyConfig getEmergencyAttributes() {
        return emergencyAttributes;
    }

    public void setEmergencyAttributes(KnXDMEmergencyConfig emergencyAttributes) {
        this.emergencyAttributes = emergencyAttributes;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public KnXDMUserProfileFSDTO getUserProfileFS() {
        return userProfileFS;
    }

    public void setUserProfileFS(KnXDMUserProfileFSDTO userProfileFS) {
        this.userProfileFS = userProfileFS;
    }

    public Collection<KnTargetMdnPermissionBitInfo> getTargetMdnPermissionBitInfoList() {
        return targetMdnPermissionBitInfoList;
    }

    public void setTargetMdnPermissionBitInfoList(Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfoList) {
        this.targetMdnPermissionBitInfoList = targetMdnPermissionBitInfoList;
    }

    public Set<KnXDMGroupListInfoDTO> getAddedGroupListInfo() {
        return addedGroupListInfo;
    }

    public void setAddedGroupListInfo(Set<KnXDMGroupListInfoDTO> addedGroupListInfo) {
        this.addedGroupListInfo = addedGroupListInfo;
    }

    public Set<KnXDMGroupListInfoDTO> getModifiedGroupListInfo() {
        return modifiedGroupListInfo;
    }

    public void setModifiedGroupListInfo(Set<KnXDMGroupListInfoDTO> modifiedGroupListInfo) {
        this.modifiedGroupListInfo = modifiedGroupListInfo;
    }

    public Collection<KnTargetMdnPermissionBitInfo> getAddedMdnPerms() {
        return addedMdnPerms;
    }

    public void setAddedMdnPerms(Collection<KnTargetMdnPermissionBitInfo> addedMdnPerms) {
        this.addedMdnPerms = addedMdnPerms;
    }

    public Collection<KnTargetMdnPermissionBitInfo> getRemovedMdnPerms() {
        return removedMdnPerms;
    }

    public void setRemovedMdnPerms(Collection<KnTargetMdnPermissionBitInfo> removedMdnPerms) {
        this.removedMdnPerms = removedMdnPerms;
    }

    public Set<String> getRemovedGroupIds() {
        return removedGroupIds;
    }

    public void setRemovedGroupIds(Set<String> removedGroupIds) {
        this.removedGroupIds = removedGroupIds;
    }

    public Collection<KnTargetMdnPermissionBitInfo> getModifiedMdnPerms() {
        return modifiedMdnPerms;
    }

    public void setModifiedMdnPerms(Collection<KnTargetMdnPermissionBitInfo> modifiedMdnPerms) {
        this.modifiedMdnPerms = modifiedMdnPerms;
    }

    public String getTgscMode() {  return tgscMode; }

    public void setTgscMode(String tgscMode) {this.tgscMode = tgscMode; }

    public String getSharingEnabled() { return sharingEnabled; }

    public void setSharingEnabled(String sharingEnabled) { this.sharingEnabled = sharingEnabled; }

    public List<String> getAddUserProfileSharedCorpList() { return addUserProfileSharedCorpList; }

    public void setAddUserProfileSharedCorpList(List<String> addUserProfileSharedCorpList) { this.addUserProfileSharedCorpList = addUserProfileSharedCorpList; }

    public List<String> getRemoveUserProfileSharedCorpList() { return removeUserProfileSharedCorpList; }

    public void setRemoveUserProfileSharedCorpList(List<String> removeUserProfileSharedCorpList) { this.removeUserProfileSharedCorpList = removeUserProfileSharedCorpList; }

    @Override
    public String toString() {
        return "KnXDMUserProfileInfoDTO{" +
                "profileName='" + profileName + '\'' +
                ", contactListId='" + contactListId + '\'' +
                ", groupListInfo=" + groupListInfo +
                ", emergencyAttributes=" + emergencyAttributes +
                ", profileId='" + profileId + '\'' +
                ", userProfileFS='" + userProfileFS + '\'' +
                ", targetMdnPermissionBitInfoList=" + targetMdnPermissionBitInfoList +
                ", addedGroupListInfo=" + addedGroupListInfo +
                ", modifiedGroupListInfo=" + modifiedGroupListInfo +
                ", removedGroupIds=" + removedGroupIds +
                ", addedMdnPerms=" + addedMdnPerms +
                ", modifiedMdnPerms=" + modifiedMdnPerms +
                ", removedMdnPerms=" + removedMdnPerms +
                ", tgscMode=" + tgscMode +
                ", sharingEnabled=" + sharingEnabled +
                ", addUserProfileSharedCorpList=" + addUserProfileSharedCorpList +
                ", removeUserProfileSharedCorpList=" + removeUserProfileSharedCorpList +
                '}';
    }
}
