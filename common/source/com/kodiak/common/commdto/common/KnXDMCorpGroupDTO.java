/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpGroupInfoDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 17, 2011      7.0
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
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KnXDMCorpGroupDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676125L;

    private String corpId;
    private String groupId;
    private String groupName;
    private String newGroupDisplayName;
    private String ownerMdn;
    private String eTag;
    private int maxGroupMemberLimitFlag;
    private int memberCount;
    private Map<String, Object> customParamMap;
    private int groupType;
    private Collection<KnXDMCorpContactDTO> supervisorList;
    private boolean isCamped;
    private Integer avatar;
    private int overrideDND;
    private boolean isLargeGrp;
    private int createdBy;
    private String groupUri;
    private Integer mcxGrpInd;
    private boolean upmCall;
    private String userProfileId;
    private Integer grpProfileId;
    private String grpProfileName;
    private Integer grpServiceType;
    private Integer audioCutIn;
    private Integer featureAllowed;
    private Integer grpShared;
    private List<KnXDMCorpGrpSharedCorpListDTO> grpSharedCopList;
    private String grpOwnerCorpId;
    private String grpOwnerExtCorpId;
    private List<String> addedOwnerIdList;
    private List<String> removedOwnerIdList;
    private List<String> ownerIdList;
    private Integer isPreConfiguredGroup;
    private String ugwConfig;
    private Map<Integer, Boolean> grpLocWatcherMap;
    private Integer ugwInterop;
    private String recordingFS;
    private List<String> sharedIdList;
    private Integer authorizedLargeTG;
    private Integer  videoPermission;

    public Integer getVideoPermission() {
        return videoPermission;
    }

    public void setVideoPermission(Integer videoPermission) {
        this.videoPermission = videoPermission;
    }

    public Integer getIsPreConfiguredGroup() {
        return isPreConfiguredGroup;
    }

    public void setIsPreConfiguredGroup(Integer isPreConfiguredGroup) {
        this.isPreConfiguredGroup = isPreConfiguredGroup;
    }

    public Integer getMcxGrpInd() {
		return mcxGrpInd;
	}

	public void setMcxGrpInd(Integer mcxGrpInd) {
		this.mcxGrpInd = mcxGrpInd;
	}

    public Integer getAvatar() {
		return avatar;
	}

	public String getNewGroupDisplayName() {
	return newGroupDisplayName;
}

    public void setNewGroupDisplayName(String newGroupDisplayName) {
        this.newGroupDisplayName = newGroupDisplayName;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


	public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOwnerMdn() {
        return ownerMdn;
    }

    public void setOwnerMdn(String ownerMdn) {
        this.ownerMdn = ownerMdn;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public String getObjectId() {
        return groupId;
    }

    public int getMaxGroupMemberLimitFlag() {
        return maxGroupMemberLimitFlag;
    }

    public void setMaxGroupMemberLimitFlag(int maxGroupMemberLimitFlag) {
        this.maxGroupMemberLimitFlag = maxGroupMemberLimitFlag;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public Collection<KnXDMCorpContactDTO> getSupervisorList() {
        return supervisorList;
    }

    public void setSupervisorList(Collection<KnXDMCorpContactDTO> supervisorList) {
        this.supervisorList = supervisorList;
    }

    public boolean isCamped() {
        return isCamped;
    }

    public void setCamped(boolean camped) {
        isCamped = camped;
    }

    public int getOverrideDND() {
        return overrideDND;
    }

    public void setOverrideDND(int overrideDND) {
        this.overrideDND = overrideDND;
    }

    public boolean isLargeGrp() {
        return isLargeGrp;
    }

    public void setLargeGrp(boolean largeGrp) {
        isLargeGrp = largeGrp;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getGroupUri() { return groupUri; }

    public void setGroupUri(String groupUri) { this.groupUri = groupUri; }

    public boolean isUpmCall() {
        return upmCall;
    }

    public void setUpmCall(boolean upmCall) {
        this.upmCall = upmCall;
    }

    public Integer getGrpProfileId() {
        return grpProfileId;
    }

    public void setGrpProfileId(Integer grpProfileId) {
        this.grpProfileId = grpProfileId;
    }

    public String getGrpProfileName() {
        return grpProfileName;
    }

    public void setGrpProfileName(String grpProfileName) {
        this.grpProfileName = grpProfileName;
    }

    public Integer getGrpServiceType() {
        return grpServiceType;
    }

    public void setGrpServiceType(Integer grpServiceType) {
        this.grpServiceType = grpServiceType;
    }

    public Integer getAudioCutIn() {
        return audioCutIn;
    }

    public void setAudioCutIn(Integer audioCutIn) {
        this.audioCutIn = audioCutIn;
    }

    public Integer getFeatureAllowed() {
        return featureAllowed;
    }

    public void setFeatureAllowed(Integer featureAllowed) {
        this.featureAllowed = featureAllowed;
    }

    public Integer getGrpShared() { return grpShared; }

    public void setGrpShared(Integer grpShared) { this.grpShared = grpShared; }

    public List<KnXDMCorpGrpSharedCorpListDTO> getGrpSharedCopList() {return grpSharedCopList; }

    public void setGrpSharedCopList(List<KnXDMCorpGrpSharedCorpListDTO> grpSharedCopList) {this.grpSharedCopList = grpSharedCopList; }

    public String getGrpOwnerCorpId() {return grpOwnerCorpId; }

    public void setGrpOwnerCorpId(String grpOwnerCorpId) {this.grpOwnerCorpId = grpOwnerCorpId; }

    public String getGrpOwnerExtCorpId() {return grpOwnerExtCorpId; }

    public void setGrpOwnerExtCorpId(String grpOwnerExtCorpId) {this.grpOwnerExtCorpId = grpOwnerExtCorpId; }
    
    public String getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(String userProfileId) {
		this.userProfileId = userProfileId;
	}

    public List<String> getAddedOwnerIdList() {
        return addedOwnerIdList;
    }

    public void setAddedOwnerIdList(List<String> addedOwnerIdList) {
        this.addedOwnerIdList = addedOwnerIdList;
    }

    public List<String> getRemovedOwnerIdList() {
        return removedOwnerIdList;
    }

    public void setRemovedOwnerIdList(List<String> removedOwnerIdList) {
        this.removedOwnerIdList = removedOwnerIdList;
    }

    public List<String> getOwnerIdList() { return ownerIdList; }

    public void setOwnerIdList(List<String> ownerIdList) { this.ownerIdList = ownerIdList; }

    public String getUgwConfig() {
        return ugwConfig;
    }

    public void setUgwConfig(String ugwConfig) {
        this.ugwConfig = ugwConfig;
    }

    public Map<Integer, Boolean> getGrpLocWatcherMap() {
        return grpLocWatcherMap;
    }

    public void setGrpLocWatcherMap(Map<Integer, Boolean> grpLocWatcherMap) {
        this.grpLocWatcherMap = grpLocWatcherMap;
    }

    public Integer getUgwInterop() {
        return ugwInterop;
    }

    public void setUgwInterop(Integer ugwInterop) {
        this.ugwInterop = ugwInterop;
    }

    public String getRecordingFS() {
        return recordingFS;
    }

    public void setRecordingFS(String recordingFS) {
        this.recordingFS = recordingFS;
    }

    public List<String> getSharedIdList() {
        return sharedIdList;
    }

    public void setSharedIdList(List<String> sharedIdList) {
        this.sharedIdList = sharedIdList;
    }

    public Integer getAuthorizedLargeTG() {
        return authorizedLargeTG;
    }

    public void setAuthorizedLargeTG(Integer authorizedLargeTG) {
        this.authorizedLargeTG = authorizedLargeTG;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("CorpId - ").append(corpId)
                .append(", GroupId - ").append(groupId)
                .append(", GroupName - ").append(groupName)
                .append(", OwnerMdn - ").append(KnGDPRTemplate.mdn(ownerMdn))
                .append(", ETag - ").append(eTag)
                .append(", MaxGrouMemLimitFlag - ").append(maxGroupMemberLimitFlag)
                .append(", memberCount - ").append(memberCount)
                .append(", customParamMap - ").append(customParamMap)
                .append(", groupType - ").append(groupType)
                .append(", avatar - ").append(avatar)
                .append(", supervisorList - ").append(supervisorList)
                .append(", isCamped - ").append(isCamped)
                .append(", isLargeGrp - ").append(isLargeGrp)
                .append(", overrideDND - ").append(overrideDND)
                .append(", createdBy - ").append(createdBy)
                .append(", groupUri - ").append(groupUri)
                .append(", upmCall - ").append(upmCall)
        		.append(", mcxGrpInd - ").append(mcxGrpInd)
                .append(", grpProfileId - ").append(grpProfileId)
                .append(", grpProfileName - ").append(grpProfileName)
                .append(", featureAllowed - ").append(featureAllowed)
                .append(", grpServiceType - ").append(grpServiceType)
                .append(", audioCutIn - ").append(audioCutIn)
                .append(", groupShared=").append(grpShared)
                .append(", groupSharedCopList=").append(grpSharedCopList)
                .append(", groupOwnerCorpId=").append(grpOwnerCorpId)
                .append(", groupOwnerExtCorpId=").append(grpOwnerExtCorpId)
                .append(", userProfileId=").append(userProfileId)
                .append(", addedOwnerIdList=").append(addedOwnerIdList)
                .append(", removedOwnerIdList=").append(removedOwnerIdList)
                .append(", ownerIdList -").append(ownerIdList)
                .append(", isPreConfiguredGroup -").append(isPreConfiguredGroup)
                .append(", ugwConfig -").append(ugwConfig)
                .append(", grpLocWatcherMap -").append(grpLocWatcherMap)
                .append(", ugwInterop - ").append(ugwInterop)
                .append(", recordingFS - ").append(recordingFS)
                .append(", sharedIdList -").append(sharedIdList)
                .append(", authorizedLargeTG -").append(authorizedLargeTG)
                .append(", videoPermission -").append(videoPermission);

        return sb.toString();
    }
}
