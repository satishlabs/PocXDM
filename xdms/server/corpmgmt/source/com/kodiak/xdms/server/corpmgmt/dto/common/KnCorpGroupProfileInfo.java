/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import java.util.List;

public class KnCorpGroupProfileInfo {

    private Integer corpId;
    private Integer profileId;
    private String profileName;
    private Integer grpType;
    private Integer avatar;
    private Integer videoCallPermission;
    private Integer serviceType;
    private Integer osmListId;
    private Integer audioCutIn;
    private Integer mcxGrp;
    private Integer overrideDnd;
    private Integer featureAllowed;

    private Long createTimeStamp;
    private Long updateTimeStamp;
    private Integer grpProfileStatus;
    private Integer grpShared;
    private List<KnCorpSharedCorpInfo> sharedCorpList;
    private Integer grpOwnerCorpId;
    private String grpOwnerExtCorpId;
    private Integer ugwInterop;
    private String hierarchyId;

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public Integer getVideoCallPermission() {
        return videoCallPermission;
    }

    public void setVideoCallPermission(Integer videoCallPermission) {
        this.videoCallPermission = videoCallPermission;
    }

    public Integer getUgwInterop() {
        return ugwInterop;
    }

    public void setUgwInterop(Integer ugwInterop) {
        this.ugwInterop = ugwInterop;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Integer getGrpType() {
        return grpType;
    }

    public void setGrpType(Integer grpType) {
        this.grpType = grpType;
    }

    public Integer getAvatar() {
        return avatar;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public Integer getServiceType() {
        return serviceType;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    public Integer getOsmListId() {
        return osmListId;
    }

    public void setOsmListId(Integer osmListId) {
        this.osmListId = osmListId;
    }

    public Integer getAudioCutIn() {
        return audioCutIn;
    }

    public void setAudioCutIn(Integer audioCutIn) {
        this.audioCutIn = audioCutIn;
    }

    public Integer getMcxGrp() {
        return mcxGrp;
    }

    public void setMcxGrp(Integer mcxGrp) {
        this.mcxGrp = mcxGrp;
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

    public Integer getGrpProfileStatus() {
        return grpProfileStatus;
    }

    public void setGrpProfileStatus(Integer grpProfileStatus) {
        this.grpProfileStatus = grpProfileStatus;
    }

    public Integer getOverrideDnd() {
        return overrideDnd;
    }

    public void setOverrideDnd(Integer overrideDnd) {
        this.overrideDnd = overrideDnd;
    }

    public Integer getFeatureAllowed() {
        return featureAllowed;
    }

    public void setFeatureAllowed(Integer featureAllowed) {
        this.featureAllowed = featureAllowed;
    }

    public Integer getGrpShared() { return grpShared; }

    public void setGrpShared(Integer grpShared) { this.grpShared = grpShared; }


    public List<KnCorpSharedCorpInfo> getSharedCorpList() { return sharedCorpList; }

    public void setSharedCorpList(List<KnCorpSharedCorpInfo> sharedCorpList) { this.sharedCorpList = sharedCorpList; }

    public Integer getGrpOwnerCorpId() { return grpOwnerCorpId; }

    public void setGrpOwnerCorpId(Integer grpOwnerCorpId) { this.grpOwnerCorpId = grpOwnerCorpId; }

    public String getGrpOwnerExtCorpId() { return grpOwnerExtCorpId; }

    public void setGrpOwnerExtCorpId(String grpOwnerExtCorpId) { this.grpOwnerExtCorpId = grpOwnerExtCorpId; }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append("corpId - ").append(corpId);
        sb.append("profileId - ").append(profileId);
        sb.append("profileName - ").append(profileName);
        sb.append("grpType - ").append(grpType);
        sb.append("avatar - ").append(avatar);
        sb.append("serviceType - ").append(serviceType);
        sb.append("osmListId - ").append(osmListId);
        sb.append("audioCutIn - ").append(audioCutIn);
        sb.append("mcxGrp - ").append(mcxGrp);
        sb.append("overrideDnd - ").append(overrideDnd);
        sb.append("grpShared - ").append(grpShared);
        sb.append("sharedCorpList - ").append(sharedCorpList);
        sb.append("grpOwnerCorpId - ").append(grpOwnerCorpId);
        sb.append("grpOwnerExtCorpId - ").append(grpOwnerExtCorpId);
        sb.append("ugwInterop - ").append(ugwInterop);
        sb.append("hierarchyId - ").append(hierarchyId);
        return sb.toString();
    }
}
