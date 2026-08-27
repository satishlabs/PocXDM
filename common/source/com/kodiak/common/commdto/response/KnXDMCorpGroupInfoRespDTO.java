/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpGroupInfoRespDTO.java
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
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnHierarchyMappingInfo;
import com.kodiak.common.commdto.common.KnXDMCorpContactDTO;
import com.kodiak.common.commdto.common.KnXDMCorpGrpSharedCorpListDTO;
import com.kodiak.common.commdto.common.KnXDMCorpSublistDTO;

import java.util.Collection;
import java.util.List;

public class KnXDMCorpGroupInfoRespDTO extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = 7526471155622776150L;

    private String corpId;
    private String groupId;
    private String groupName;
    private int groupType;
    private String eTag;
    private Collection<KnXDMCorpContactDTO> groupMembers;
    private Collection<KnXDMCorpSublistDTO> sublistList;
    private int maxGroupMemberLimitFlag;
    private int groupMemberCount;
    private Collection<KnXDMCorpContactDTO> groupSupervisorMembers;
    private int overrideDND;
    private String protocolVersion;
    private String callPermission;
    private Integer avatar;
    private Integer hangTimeOut;
    private Integer emergOverrideDND;
    private Integer emergHangTimeAddOn;
    private Integer emergAutoFloorTimer;
    private boolean isEmergGrp;
    private String groupOwner;
    private Integer groupCreatedBy;
    private boolean isLargeGroup;
    private KnXDMCorpOSMInfoRespDTO OSMListDetails;
    private String groupUri;
	private int lmrInterpCapable;
    private int mcxGrpInd;
    private String pocHome;
    private Integer audioCutIn;
    private String groupProfileId;
    private Integer groupServiceType;
    private String grpShared;
    private List<KnXDMCorpGrpSharedCorpListDTO> grpSharedCopList;
    private String grpOwnerCorpId;
    private String grpOwnerExtCorpId;
    private List<String> ownerIdList;
    private Integer externalCorpGroup;
    private Integer isPreConfiguredGroup;
    private Boolean isMdnAuthorized;
    private List<String> unAuthorizedGroupURIList;
    private Integer ugwInterop;
    private String recordingFs;
    private List<String> sharedIdList;
    private Integer videoPermission;


    private String ugwConfig;
    private String recordingFsVal;
    private Integer authorizedLargeTG;
    private Integer memberListCount;
    /** Shared hierarchy list for getGroupDetails (Phase 6). ownerHierarchyId=targetCorpExtId, sharedHierarchyId=targetHierarchyName */
    private List<KnHierarchyMappingInfo> sharedHierarchyList;
    private String clusterId;
    private String ownerAgencyName;
    private String ownerOrganizationName;

    public Integer getIsPreConfiguredGroup() {
        return isPreConfiguredGroup;
    }

    public void setIsPreConfiguredGroup(Integer isPreConfiguredGroup) {
        this.isPreConfiguredGroup = isPreConfiguredGroup;
    }

    public Integer getVideoPermission() {
        return videoPermission;
    }

    public void setVideoPermission(Integer videoPermission) {
        this.videoPermission = videoPermission;
    }

    public Integer getAvatar() {
        return avatar;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
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

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public Collection<KnXDMCorpContactDTO> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(Collection<KnXDMCorpContactDTO> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public int getMaxGroupMemberLimitFlag() {
        return maxGroupMemberLimitFlag;
    }

    public void setMaxGroupMemberLimitFlag(int maxGroupMemberLimitFlag) {
        this.maxGroupMemberLimitFlag = maxGroupMemberLimitFlag;
    }

    public Collection<KnXDMCorpSublistDTO> getSublistList() {
        return sublistList;
    }

    public void setSublistList(Collection<KnXDMCorpSublistDTO> sublistList) {
        this.sublistList = sublistList;
    }

    public int getGroupMemberCount() {
        return groupMemberCount;
    }

    public void setGroupMemberCount(int groupMemberCount) {
        this.groupMemberCount = groupMemberCount;
    }

    public Collection<KnXDMCorpContactDTO> getGroupSupervisorMembers() {
        return groupSupervisorMembers;
    }

    public void setGroupSupervisorMembers(Collection<KnXDMCorpContactDTO> groupSupervisorMembers) {
        this.groupSupervisorMembers = groupSupervisorMembers;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public int getOverrideDND() {
        return overrideDND;
    }

    public void setOverrideDND(int overrideDND) {
        this.overrideDND = overrideDND;
    }

    public String getCallPermission() {
        return callPermission;
    }

    public void setCallPermission(String callPermission) {
        this.callPermission = callPermission;
    }

    public Integer getHangTimeOut() {
        return hangTimeOut;
    }

    public void setHangTimeOut(Integer hangTimeOut) {
        this.hangTimeOut = hangTimeOut;
    }

    public Integer getEmergOverrideDND() {
        return emergOverrideDND;
    }

    public void setEmergOverrideDND(Integer emergOverrideDND) {
        this.emergOverrideDND = emergOverrideDND;
    }

    public Integer getEmergHangTimeAddOn() {
        return emergHangTimeAddOn;
    }

    public void setEmergHangTimeAddOn(Integer emergHangTimeAddOn) {
        this.emergHangTimeAddOn = emergHangTimeAddOn;
    }

    public Integer getEmergAutoFloorTimer() {
        return emergAutoFloorTimer;
    }

    public void setEmergAutoFloorTimer(Integer emergAutoFloorTimer) {
        this.emergAutoFloorTimer = emergAutoFloorTimer;
    }

    public boolean isEmergGrp() {
        return isEmergGrp;
    }

    public void setEmergGrp(boolean emergGrp) {
        isEmergGrp = emergGrp;
    }

    public String getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(String groupOwner) {
        this.groupOwner = groupOwner;
    }

    public Integer getGroupCreatedBy() {
        return groupCreatedBy;
    }

    public void setGroupCreatedBy(Integer groupCreatedBy) {
        this.groupCreatedBy = groupCreatedBy;
    }

    public boolean isLargeGroup() {
        return isLargeGroup;
    }

    public void setLargeGroup(boolean isLargeGroup) {
        this.isLargeGroup = isLargeGroup;
    }

    public KnXDMCorpOSMInfoRespDTO getOSMListDetails() {
		return OSMListDetails;
	}

    public void setOSMListDetails(KnXDMCorpOSMInfoRespDTO OSMListDetails) {
        this.OSMListDetails = OSMListDetails;
    }

    public String getGroupUri() {
        return groupUri;
    }

    public void setGroupUri(String groupUri) {
        this.groupUri = groupUri;
    }
	
	public int getLmrInterpCapable() {
        return lmrInterpCapable;
    }

    public void setLmrInterpCapable(int lmrInterpCapable) {
        this.lmrInterpCapable = lmrInterpCapable;
    }

    public String getPocHome() {
        return pocHome;
    }

    public void setPocHome(String pocHome) {
        this.pocHome = pocHome;
    }
    public int getMcxGrpInd() {
        return mcxGrpInd;
    }

    public void setMcxGrpInd(int mcxGrpInd) {
        this.mcxGrpInd = mcxGrpInd;
    }

    public Integer getAudioCutIn() {
        return audioCutIn;
    }

    public void setAudioCutIn(Integer audioCutIn) {
        this.audioCutIn = audioCutIn;
    }

    public String getGroupProfileId() {
        return groupProfileId;
    }

    public void setGroupProfileId(String groupProfileId) {
        this.groupProfileId = groupProfileId;
    }

    public Integer getGroupServiceType() {
        return groupServiceType;
    }

    public void setGroupServiceType(Integer groupServiceType) {
        this.groupServiceType = groupServiceType;
    }

    public String getGrpShared() {return grpShared; }

    public void setGrpShared(String grpShared) {this.grpShared = grpShared; }

    public List<KnXDMCorpGrpSharedCorpListDTO> getGrpSharedCopList() {return grpSharedCopList; }

    public void setGrpSharedCopList(List<KnXDMCorpGrpSharedCorpListDTO> grpSharedCopList) {this.grpSharedCopList = grpSharedCopList; }

    public String getGrpOwnerCorpId() {return grpOwnerCorpId; }

    public void setGrpOwnerCorpId(String grpOwnerCorpId) {this.grpOwnerCorpId = grpOwnerCorpId; }

    public String getGrpOwnerExtCorpId() {return grpOwnerExtCorpId; }

    public void setGrpOwnerExtCorpId(String grpOwnerExtCorpId) {this.grpOwnerExtCorpId = grpOwnerExtCorpId; }

    public List<String> getOwnerIdList() {
        return ownerIdList;
    }

    public void setOwnerIdList(List<String> ownerIdList) {
        this.ownerIdList = ownerIdList;
    }

    public Integer getExternalCorpGroup() {return externalCorpGroup;}

    public void setExternalCorpGroup(Integer externalCorpGroup) {this.externalCorpGroup = externalCorpGroup;}

    public Boolean getMdnAuthorized() { return isMdnAuthorized; }

    public void setMdnAuthorized(Boolean mdnAuthorized) { isMdnAuthorized = mdnAuthorized; }



    public String getUgwConfig() {
        return ugwConfig;
    }

    public void setUgwConfig(String ugwConfig) {
        this.ugwConfig = ugwConfig;
    }

    public List<String> getUnAuthorizedGroupURIList() {
        return unAuthorizedGroupURIList;
    }

    public void setUnAuthorizedGroupURIList(List<String> unAuthorizedGroupURIList) {
        this.unAuthorizedGroupURIList = unAuthorizedGroupURIList;
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

    public String getRecordingFsVal() {
        return recordingFsVal;
    }

    public void setRecordingFsVal(String recordingFsVal) {
        this.recordingFsVal = recordingFsVal;
    }

    public List<String> getSharedIdList() {
        return sharedIdList;
    }

    public void setSharedIdList(List<String> sharedIdList) {
        this.sharedIdList = sharedIdList;
    }
    public Integer getAuthorizedLargeTG() { return authorizedLargeTG;}
    public void setAuthorizedLargeTG(Integer authorizedLargeTG) { this.authorizedLargeTG = authorizedLargeTG;}

    public Integer getMemberListCount() { return memberListCount; }

    public void setMemberListCount(Integer memberListCount) { this.memberListCount = memberListCount; }

    public List<KnHierarchyMappingInfo> getSharedHierarchyList() { return sharedHierarchyList; }
    public void setSharedHierarchyList(List<KnHierarchyMappingInfo> sharedHierarchyList) { this.sharedHierarchyList = sharedHierarchyList; }

    public String getClusterId() { return clusterId; }
    public void setClusterId(String clusterId) { this.clusterId = clusterId; }

    public String getOwnerAgencyName() {
        return ownerAgencyName;
    }

    public void setOwnerAgencyName(String ownerAgencyName) {
        this.ownerAgencyName = ownerAgencyName;
    }

    public String getOwnerOrganizationName() {
        return ownerOrganizationName;
    }

    public void setOwnerOrganizationName(String ownerOrganizationName) {
        this.ownerOrganizationName = ownerOrganizationName;
    }

    @Override
    public String toString() {
        return "KnXDMCorpGroupInfoRespDTO{" +
                "corpId='" + corpId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupType=" + groupType +
                ", eTag='" + eTag + '\'' +
                ", groupMembers=" + groupMembers +
                ", sublistList=" + sublistList +
                ", maxGroupMemberLimitFlag=" + maxGroupMemberLimitFlag +
                ", groupMemberCount=" + groupMemberCount +
                ", groupSupervisorMembers=" + groupSupervisorMembers +
                ", overrideDND=" + overrideDND +
                ", protocolVersion='" + protocolVersion + '\'' +
                ", callPermission='" + callPermission + '\'' +
                ", avatar=" + avatar +
                ", hangTimeOut=" + hangTimeOut +
                ", emergOverrideDND=" + emergOverrideDND +
                ", emergHangTimeAddOn=" + emergHangTimeAddOn +
                ", emergAutoFloorTimer=" + emergAutoFloorTimer +
                ", isEmergGrp=" + isEmergGrp +
                ", groupOwner=" + groupOwner +
                ", groupCreatedBy=" + groupCreatedBy +
                ", isLargeGroup=" + isLargeGroup +
                ", memberListCount=" + memberListCount +
                ", OSMListDetails=" + OSMListDetails +
                ", groupUri=" + groupUri +
				", lmrInterpCapable=" + lmrInterpCapable +
                ", mcxGrpInd=" + mcxGrpInd +
                ", pocHome=" + pocHome +
                ", audioCutIn=" + audioCutIn +
                ", groupServiceType=" + groupServiceType +
                ", groupProfileId=" + groupProfileId +
                ", groupShared=" + grpShared +
                ", groupSharedCopList=" + grpSharedCopList +
                ", groupOwnerCorpId=" + grpOwnerCorpId +
                ", groupOwnerExtCorpId=" + grpOwnerExtCorpId +
                ", ownerIdList=" + ownerIdList +
                ", externalCorpGroup= " +externalCorpGroup +
                ", isPreConfiguredGroup= " +isPreConfiguredGroup +
                ", isMdnAuthorized= " +isMdnAuthorized +
                ", ugwConfig= " +ugwConfig +
                ", unAuthorizedGroupURIList= " + unAuthorizedGroupURIList +
                ", ugwInterop= " + ugwInterop +
                ", recordingFs= " + recordingFs +
                ", recordingFsVal= " + recordingFsVal +
                ", sharedIdList= " + sharedIdList +
                ", authorizedLargeTG= " + authorizedLargeTG +
                ", videoPermission= " + videoPermission +
                ", ownerAgencyName= " + ownerAgencyName +
                ", ownerOrganizationName= " + ownerOrganizationName +
                '}';
    }
}
