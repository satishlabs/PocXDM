/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMSublistDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 15, 2011      7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KnXDMCorpUserProfileDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676128L;

    private String corpId;
    private String profileId;
    private Set<KnXDMGroupListInfoDTO> groupListInfo;
    private KnXDMEmergencyConfig emergencyAttributes;
    private String userProfileFS;
    private String FeatureBS;
    private String eTag;
    private Map<String, Object> customParamMap;
    private String profileName;
    private String userProfileIndex;
    private String contactListId;
    private Integer maxCountPerProfileIndex;
    private String isDefaultProfile;
    private Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfoList;
    private int userProfileStatus;
    private String tgscMode;
    private List<String> ownerIdList;
    private Boolean sharingEnabled;
    private String ownerCorpId;

    public KnXDMCorpUserProfileDTO() {

    }

    public KnXDMCorpUserProfileDTO(String corpId, String profileId, String profileName, String userProfileIndex) {
        this.corpId = corpId;
        this.profileId = profileId;
        this.profileName = profileName;
        this.userProfileIndex = userProfileIndex;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

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

    @Override
    public String getObjectId() {
        return getProfileName();
    }

    public String getUserProfileIndex() {
        return userProfileIndex;
    }

    public void setUserProfileIndex(String userProfileIndex) {
        this.userProfileIndex = userProfileIndex;
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

    public String getUserProfileFS() {
        return userProfileFS;
    }

    public void setUserProfileFS(String userProfileFS) {
        this.userProfileFS = userProfileFS;
    }

    public Integer getMaxCountPerProfileIndex() {
        return maxCountPerProfileIndex;
    }

    public void setMaxCountPerProfileIndex(Integer maxCountPerProfileIndex) {
        this.maxCountPerProfileIndex = maxCountPerProfileIndex;
    }

	public String getIsDefaultProfile() {
		return isDefaultProfile;
	}

	public void setIsDefaultProfile(String isDefaultProfile) {
		this.isDefaultProfile = isDefaultProfile;
	}

    public Collection<KnTargetMdnPermissionBitInfo> getTargetMdnPermissionBitInfoList() {  return targetMdnPermissionBitInfoList; }

    public void setTargetMdnPermissionBitInfoList(Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfoList) {
        this.targetMdnPermissionBitInfoList = targetMdnPermissionBitInfoList;
    }

    public int getUserProfileStatus() { return userProfileStatus; }

    public void setUserProfileStatus(int userProfileStatus) { this.userProfileStatus = userProfileStatus; }

    public String getTgscMode() { return tgscMode; }

    public void setTgscMode(String tgscMode) {  this.tgscMode = tgscMode; }

    public List<String> getOwnerIdList() { return ownerIdList; }

    public void setOwnerIdList(List<String> ownerIdList) { this.ownerIdList = ownerIdList; }

    public Boolean getSharingEnabled() {
        return sharingEnabled;
    }

    public void setSharingEnabled(Boolean sharingEnabled) {
        this.sharingEnabled = sharingEnabled;
    }

    public String getOwnerCorpId() {
        return ownerCorpId;
    }

    public void setOwnerCorpId(String ownerCorpId) {
        this.ownerCorpId = ownerCorpId;
    }

    @Override
    public String toString() {
        return "KnXDMCorpUserProfileDTO{" +
                "corpId='" + corpId + '\'' +
                ", profileId='" + profileId + '\'' +
                ", groupListInfo=" + groupListInfo +
                ", emergencyAttributes=" + emergencyAttributes +
                ", userProfileFS='" + userProfileFS + '\'' +
                ", FeatureBS='" + FeatureBS + '\'' +
                ", profileName='" + profileName + '\'' +
                ", userProfileIndex='" + userProfileIndex + '\'' +
                ", contactListId='" + contactListId + '\'' +
                ", maxCountPerProfileIndex=" + maxCountPerProfileIndex +
                ", isDefaultProfile='" + isDefaultProfile + '\'' +
                ", targetMdnPermissionBitInfoList=" + targetMdnPermissionBitInfoList +
                ", userProfileStatus=" + userProfileStatus +
                ", tgscMode=" + tgscMode +
                ", ownerIdList=" + ownerIdList +
                ", sharingEnabled=" + sharingEnabled +
                ", ownerCorpId=" + ownerCorpId +
                '}';
    }
}

