/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSGrpMemberDto;
import com.kodiak.xdms.server.common.dto.common.KnEXDMSNotifyDto;

import java.util.List;

/**
 * Created by asanjiv on 10/7/16.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class KnCorpEXDMSNotifyDto extends KnEXDMSNotifyDto {

    private String grpName;
    private Integer grpId;
    private Integer grpType;
    private String grpUri;
    private Integer etag;
    private Integer corpid;
    private Integer previousEtag;
    private List<KnEXDMSGrpMemberDto> addedMemebrs;
    private List<KnEXDMSGrpMemberDto> modifiedMembers;
    private KnMcxGrpMemberPropsDto mcxGrpMemProperties;
    private List<String> grpDistMems;
    private List<String> removedMembers;
    private List<String> addedGrpDistMems;
    private List<String> removedGrpDistMems;
    private int createdBy;
    private boolean osmIdChanged;
    private boolean isLargeGroup;
    private Integer grpCategoryInd;
    private Integer grpShare;
    private String grpSIPUri;

    // for ContactNotification:
    private String mdn;
    private Integer clientType;
    private Integer lmrInteropFlag;
    private String profileId;
    
    //MCSJAVALIB-1507
    private String pocHome;

    private Integer isPreConfigGroup;

    private Integer oldLmrInteropFlag;
    private String recordingFs;

    private String clusterId;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getGrpName() {
        return grpName;
    }

    public void setGrpName(String grpName) {
        this.grpName = grpName;
    }

    public Integer getGrpId() {
        return grpId;
    }

    public void setGrpId(Integer grpId) {
        this.grpId = grpId;
    }

    public Integer getGrpType() {
        return grpType;
    }

    public void setGrpType(Integer grpType) {
        this.grpType = grpType;
    }

    public String getGrpUri() {
        return grpUri;
    }

    public void setGrpUri(String grpUri) {
        this.grpUri = grpUri;
    }

    public Integer getEtag() {
        return etag;
    }

    public void setEtag(Integer etag) {
        this.etag = etag;
    }

    public Integer getCorpid() {
        return corpid;
    }

    public void setCorpid(Integer corpid) {
        this.corpid = corpid;
    }

    public Integer getPreviousEtag() {
        return previousEtag;
    }

    public void setPreviousEtag(Integer previousEtag) {
        this.previousEtag = previousEtag;
    }

    public List<KnEXDMSGrpMemberDto> getAddedMemebrs() {
        return addedMemebrs;
    }

    public void setAddedMemebrs(List<KnEXDMSGrpMemberDto> addedMemebrs) {
        this.addedMemebrs = addedMemebrs;
    }

    public List<KnEXDMSGrpMemberDto> getModifiedMembers() {
        return modifiedMembers;
    }

    public void setModifiedMembers(List<KnEXDMSGrpMemberDto> modifiedMembers) {
        this.modifiedMembers = modifiedMembers;
    }

    public List<String> getGrpDistMems() {
        return grpDistMems;
    }

    public void setGrpDistMems(List<String> grpDistMems) {
        this.grpDistMems = grpDistMems;
    }

    public List<String> getRemovedMembers() {
        return removedMembers;
    }

    public void setRemovedMembers(List<String> removedMembers) {
        this.removedMembers = removedMembers;
    }

    public List<String> getAddedGrpDistMems() {
        return addedGrpDistMems;
    }

    public void setAddedGrpDistMems(List<String> addedGrpDistMems) {
        this.addedGrpDistMems = addedGrpDistMems;
    }

    public List<String> getRemovedGrpDistMems() {
        return removedGrpDistMems;
    }

    public void setRemovedGrpDistMems(List<String> removedGrpDistMems) {
        this.removedGrpDistMems = removedGrpDistMems;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isOsmIdChanged() {
        return osmIdChanged;
    }

    public void setOsmIdChanged(boolean osmIdChanged) {
        this.osmIdChanged = osmIdChanged;
    }
	
	 public Integer getLmrInteropFlag() {
        return lmrInteropFlag;
    }

    public void setLmrInteropFlag(Integer lmrInteropFlag) {
        this.lmrInteropFlag = lmrInteropFlag;
    }

    public boolean isLargeGroup() {
		return isLargeGroup;
	}

	public void setLargeGroup(boolean isLargeGroup) {
		this.isLargeGroup = isLargeGroup;
	}

    public Integer getGrpCategoryInd() {
        return grpCategoryInd;
    }

    public void setGrpCategoryInd(Integer grpCategoryInd) {
        this.grpCategoryInd = grpCategoryInd;
    }

    public KnMcxGrpMemberPropsDto getMcxGrpMemProperties() {
        return mcxGrpMemProperties;
    }

    public void setMcxGrpMemProperties(KnMcxGrpMemberPropsDto mcxGrpMemProperties) {
        this.mcxGrpMemProperties = mcxGrpMemProperties;
    }
    
    public String getPocHome() {
		return pocHome;
	}

	public void setPocHome(String pocHome) {
		this.pocHome = pocHome;
	}

    public Integer getGrpShare() {
        return grpShare;
    }

    public void setGrpShare(Integer grpShare) {
        this.grpShare = grpShare;
    }

    public Integer getIsPreConfigGroup() { return isPreConfigGroup; }

    public void setIsPreConfigGroup(Integer isPreConfigGroup) { this.isPreConfigGroup = isPreConfigGroup; }

    public String getGrpSIPUri() {
        return grpSIPUri;
    }

    public void setGrpSIPUri(String grpSIPUri) {
        this.grpSIPUri = grpSIPUri;
    }

    public Integer getOldLmrInteropFlag() {
        return oldLmrInteropFlag;
    }

    public void setOldLmrInteropFlag(Integer oldLmrInteropFlag) {
        this.oldLmrInteropFlag = oldLmrInteropFlag;
    }

    public String getRecordingFs() {
        return recordingFs;
    }

    public void setRecordingFs(String recordingFs) {
        this.recordingFs = recordingFs;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }
    @Override
	public String toString() {
		return "KnCorpEXDMSNotifyDto [grpName=" + grpName + ", grpId=" + grpId + ", grpType=" + grpType + ", grpUri="
				+ grpUri + ", etag=" + etag + ", corpid=" + corpid + ", previousEtag=" + previousEtag
				+ ", addedMemebrs=" + addedMemebrs + ", modifiedMembers=" + modifiedMembers + ", grpDistMems="
				+ KnGDPRTemplate.mdnList(grpDistMems) + ", removedMembers=" + KnGDPRTemplate.mdnList(removedMembers) + ", addedGrpDistMems=" + KnGDPRTemplate.mdnList(addedGrpDistMems)
				+ ", removedGrpDistMems=" + removedGrpDistMems + ", createdBy=" + createdBy + ", osmIdChanged="
				+ osmIdChanged + ", isLargeGroup=" + isLargeGroup + ", grpCategoryInd=" + grpCategoryInd + ", mdn="
				+ KnGDPRTemplate.mdn(mdn) + ", clientType=" + clientType + ", pocHome=" + pocHome +", lmrInteropFlag=" + lmrInteropFlag+", oldLmrInteropFlag=" + oldLmrInteropFlag +", grpSIPUri=" + grpSIPUri
                + ", grpShare=" + grpShare + ", recordingFs=" + recordingFs + ", clusterId=" + clusterId + "]";
	}
}
