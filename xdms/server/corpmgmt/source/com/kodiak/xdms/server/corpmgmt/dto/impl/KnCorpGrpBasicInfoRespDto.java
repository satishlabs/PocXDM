/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;


public class KnCorpGrpBasicInfoRespDto extends KnCorpResponseDTO{
    private int groupId;
    private String grpDisplayName;
    private int grpType;
    private int grpEtag;
    private int groupListId;
    private int corpId;
    private int lmrInteropCapable;
    private String grpOwner;
    private int groupCreateBy;
    private boolean isLargeGroup;
    private int mcxGrpInd;
    private String groupProfileId;
    private Integer grpShared;
    private boolean isOwnerCorpReq;
    private int groupCorpId;
    private int overrideDnd;
    private Integer isPreConfiguredGroup;
    private Integer ugwInterop;
    private String recordingFs;

    private Integer oldLmrInteropFlag;
    private Integer authorizedLargeTG;
    private Integer videoPermission;
    private String hierarchyId;
    public Integer getOldLmrInteropFlag() {
        return oldLmrInteropFlag;
    }

    public void setOldLmrInteropFlag(Integer oldLmrInteropFlag) {
        this.oldLmrInteropFlag = oldLmrInteropFlag;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGrpDisplayName() {
        return grpDisplayName;
    }

    public void setGrpDisplayName(String grpDisplayName) {
        this.grpDisplayName = grpDisplayName;
    }

    public int getGrpType() {
        return grpType;
    }

    public void setGrpType(int grpType) {
        this.grpType = grpType;
    }

    public int getGrpEtag() {
        return grpEtag;
    }

    public void setGrpEtag(int grpEtag) {
        this.grpEtag = grpEtag;
    }

    public int getGroupListId() {
        return groupListId;
    }

    public void setGroupListId(int groupListId) {
        this.groupListId = groupListId;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public int getLmrInteropCapable() {
        return lmrInteropCapable;
    }

    public void setLmrInteropCapable(int lmrInteropCapable) {
        this.lmrInteropCapable = lmrInteropCapable;
    }

    public String getGrpOwner() {
        return grpOwner;
    }

    public void setGrpOwner(String grpOwner) {
        this.grpOwner = grpOwner;
    }

    public int getGroupCreateBy() {
        return groupCreateBy;
    }

    public void setGroupCreateBy(int groupCreateBy) {
        this.groupCreateBy = groupCreateBy;
    }

    public boolean isLargeGroup() {
        return isLargeGroup;
    }

    public void setLargeGroup(boolean largeGroup) {
        isLargeGroup = largeGroup;
    }

    public int getMcxGrpInd() {
		return mcxGrpInd;
	}

	public void setMcxGrpInd(int mcxGrpInd) {
		this.mcxGrpInd = mcxGrpInd;
	}

    public String getGroupProfileId() { return groupProfileId; }

    public void setGroupProfileId(String groupProfileId) { this.groupProfileId = groupProfileId; }

    public Integer getGrpShared() {
        return grpShared;
    }

    public void setGrpShared(Integer grpShared) {
        this.grpShared = grpShared;
    }

    public boolean isOwnerCorpReq() {
        return isOwnerCorpReq;
    }

    public void setOwnerCorpReq(boolean ownerCorpReq) {
        isOwnerCorpReq = ownerCorpReq;
    }

    public int getGroupCorpId() {
        return groupCorpId;
    }

    public void setGroupCorpId(int groupCorpId) {
        this.groupCorpId = groupCorpId;
    }

    public int getOverrideDnd() { return overrideDnd; }

    public void setOverrideDnd(int overrideDnd) { this.overrideDnd = overrideDnd; }

    public Integer getIsPreConfiguredGroup() {
        return isPreConfiguredGroup;
    }

    public void setIsPreConfiguredGroup(Integer isPreConfiguredGroup) {
        this.isPreConfiguredGroup = isPreConfiguredGroup;
    }

    public Integer getUgwInterop() {
        return ugwInterop;
    }

    public void setUgwInterop(Integer ugwInterop) {
        this.ugwInterop = ugwInterop;
    }

    public String getRecordingFs() {
        return recordingFs;
    }

    public void setRecordingFs(String recordingFs) {
        this.recordingFs = recordingFs;
    }

    public Integer getAuthorizedLargeTG() {
        return authorizedLargeTG;
    }

    public void setAuthorizedLargeTG(Integer authorizedLargeTG) {
        this.authorizedLargeTG = authorizedLargeTG;
    }

    public Integer getVideoPermission() {
        return videoPermission;
    }

    public void setVideoPermission(Integer videoPermission) {
        this.videoPermission = videoPermission;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append("groupId - ").append(groupId);
        sb.append("grpDisplayName - ").append(grpDisplayName);
        sb.append("grpType - ").append(grpType);
        sb.append("grpEtag - ").append(grpEtag);
        sb.append("groupListId - ").append(groupListId);
        sb.append("lmrInteropCapable - ").append(lmrInteropCapable);
        sb.append("grpOwner - ").append(grpOwner);
        sb.append("groupCreateBy - ").append(groupCreateBy);
        sb.append("isLargeGroup - ").append(isLargeGroup);
        sb.append("mcxGrpInd - ").append(mcxGrpInd);
        sb.append("groupProfileId - ").append(groupProfileId);
        sb.append("grpShared - ").append(grpShared);
        sb.append("isOwnerCorpReq - ").append(isOwnerCorpReq);
        sb.append("groupCorpId - ").append(groupCorpId);
        sb.append("overrideDnd - ").append(overrideDnd);
        sb.append("isPreConfiguredGroup - ").append(isPreConfiguredGroup);
        sb.append("ugwInterop - ").append(ugwInterop);
        sb.append("recordingFs - ").append(recordingFs);
        sb.append("authorizedLargeTG - ").append(authorizedLargeTG);
        sb.append("videoPermission - ").append(videoPermission);
        return sb.toString();
    }
}
