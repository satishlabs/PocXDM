/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpGroupInfoDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 11, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.impl;


import com.kodiak.common.commdto.request.KnXDMGroupPropertyInfoDTO;
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.dto.IGroupIdentifier;
import com.kodiak.xdms.server.corpmgmt.dto.IOwnerInfo;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSharedCorpInfo;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KnCorpGroupDTO implements IIdentifier, IGroupIdentifier, IOwnerInfo {

    private static final long serialVersionUID = 7526471155622676265L;

    private String owner;
    private int corpId;
    private int groupId;
    private int idValue;
    private int idType;
    private String groupDisplayName;
    private String newGroupDisplayName;
    private int eTag;
    private int groupType;
    private boolean contactPairing;
    private Integer groupMemCount;
    private int overrideDnd;
    private Boolean groupPairing;
    private Integer avatar;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private int lmrInteropCapable;
    private Integer hangTimeOut;
    private Integer emergOverrideDND;
    private Integer emergHangTimeAddOn;
    private Integer emergAutoFloorTimer;
    private String tpVendorID;
	private String tpRequestMdn;
	private String tpGroupOwner;
	private int groupCreatedBy;
	private boolean isEmergGrp;
    private boolean isLargeGroup;
    private int grpMemListId;
    private String OSMListId;
    private Map<Integer, Integer> osmListIdAndDefultMap;
    private Collection<KnCorpSubscriberDTO> pocSubsInfo;
    private String pocHome;
    private Integer mcxGrpInd;
    private Integer audioCutIn;
    private String groupProfileId;
    private Integer groupServiceType;
    private Integer featureAllowed;
    private Integer grpShared;
    private List<KnCorpSharedCorpInfo> corpSharedCorpInfoList;
    private String grpOwnerCorpId;
    private String grpOwnerExtCorpId;
    private String mcPttId;
    private List<String> addedOwnerIdList;
    private List<String> removedOwnerIdList;
    private List<String> ownerIdList;
    private List<String> sharedIdList;
    private Integer externalCorpGroup;
    private Collection<String> externalMdnList;
    private Integer isPreConfiguredGroup;
    private String ugwConfig;
    private String grpSIPUri;
    private List<Integer> groupIdFromURI;
    private Integer ugwInterop;
    private String ugwInteropSystemConfig;
    private String recordingFs;
    private Integer authorizedLargeTG;
    private Integer videoPermission;
    private List<KnXDMGroupPropertyInfoDTO> groupPropertyList;

    private boolean isEmptyGroup;
    private Integer clusterId;
    private String hierarchyId;

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public int getIdType() {
        return idType;
    }
    public void setIdType(int idType) {
        this.idType = idType;
    }
    public int getIdValue() {
        return idValue;
    }
    public void setIdValue(int idValue) {
        this.idValue = idValue;
    }
    public boolean getIsEmptyGroup() {
        return isEmptyGroup;
    }
    public void setEmptyGroup(boolean emptyGroup) {
        isEmptyGroup = emptyGroup;
    }
    public Integer getIsPreConfiguredGroup() {
        return isPreConfiguredGroup;
    }



    public void setIsPreConfiguredGroup(Integer isPreConfiguredGroup) {
        this.isPreConfiguredGroup = isPreConfiguredGroup;
    }

    public String getMcPttId() {
		return mcPttId;
	}

	public void setMcPttId(String mcPttId) {
		this.mcPttId = mcPttId;
	}

	public KnCorpGroupDTO() {}

    public KnCorpGroupDTO(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupCreatedBy() {
		return groupCreatedBy;
	}

	public void setGroupCreatedBy(int groupCreatedBy) {
		this.groupCreatedBy = groupCreatedBy;
	}

	public String getTpVendorID() {
		return tpVendorID;
	}

	public void setTpVendorID(String tpVendorID) {
		this.tpVendorID = tpVendorID;
	}

	public String getTpRequestMdn() {
		return tpRequestMdn;
	}

	public void setTpRequestMdn(String tpRequestMdn) {
		this.tpRequestMdn = tpRequestMdn;
	}

	public String getTpGroupOwner() {
		return tpGroupOwner;
	}

	public void setTpGroupOwner(String tpGroupOwner) {
		this.tpGroupOwner = tpGroupOwner;
	}

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public Boolean isGroupPairing() {
        return groupPairing;
    }

    public void setGroupPairing(Boolean groupPairing) {
        this.groupPairing = groupPairing;
    }

    public int getOverrideDnd() {
        return overrideDnd;
    }

    public void setOverrideDnd(int overrideDnd) {
        this.overrideDnd = overrideDnd;
    }

    public Integer getGroupMemCount() {
        return groupMemCount;
    }

    public void setGroupMemCount(Integer groupMemCount) {
        this.groupMemCount = groupMemCount;
    }

    public String getObjectId() {
        return "" + groupId;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupDisplayName() {
        return this.groupDisplayName;
    }

    public void setGroupDisplayName(String groupDisplayName) {
        this.groupDisplayName = groupDisplayName;
    }

    public int getETag() {
        return eTag;
    }

    public void setETag(int eTag) {
        this.eTag = eTag;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public boolean isContactPairing() {
        return contactPairing;
    }

    public void setContactPairing(boolean contactPairing) {
        this.contactPairing = contactPairing;
    }

    public Integer getAvatar() {
        return avatar;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public int getLmrInteropCapable() {
        return lmrInteropCapable;
    }

    public void setLmrInteropCapable(int lmrInteropCapable) {
        this.lmrInteropCapable = lmrInteropCapable;
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

    public String getNewGroupDisplayName() {
        return newGroupDisplayName;
    }

    public void setNewGroupDisplayName(String newGroupDisplayName) {
        this.newGroupDisplayName = newGroupDisplayName;
    }

    public boolean isEmergGrp() {
        return isEmergGrp;
    }

    public void setEmergGrp(boolean emergGrp) {
        isEmergGrp = emergGrp;
    }

    public boolean isLargeGroup() {
        return isLargeGroup;
    }

    public void setLargeGroup(boolean largeGroup) {
        isLargeGroup = largeGroup;
    }

    public int getGrpMemListId() {
        return grpMemListId;
    }

    public void setGrpMemListId(int grpMemListId) {
        this.grpMemListId = grpMemListId;
    }

    public String getOSMListId() {
        return OSMListId;
    }

    public void setOSMListId(String OSMListId) {
        this.OSMListId = OSMListId;
    }

    public Map<Integer, Integer> getOsmListIdAndDefultMap() {
        return osmListIdAndDefultMap;
    }

    public void setOsmListIdAndDefultMap(Map<Integer, Integer> osmListIdAndDefultMap) {
        this.osmListIdAndDefultMap = osmListIdAndDefultMap;
    }

    public Collection<KnCorpSubscriberDTO> getPocSubsInfo() {
        return pocSubsInfo;
    }

    public void setPocSubsInfo(Collection<KnCorpSubscriberDTO> pocSubsInfo) {
        this.pocSubsInfo = pocSubsInfo;
    }

    public String getPocHome() {
        return pocHome;
    }

    public void setPocHome(String pocHome) {
        this.pocHome = pocHome;
    }
    
     public Integer getMcxGrpInd() {
		return mcxGrpInd;
	}

	public void setMcxGrpInd(Integer mcxGrpInd) {
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

    public Integer getFeatureAllowed() {
        return featureAllowed;
    }

    public void setFeatureAllowed(Integer featureAllowed) {
        this.featureAllowed = featureAllowed;
    }

    public Integer getGrpShared() { return grpShared; }

    public void setGrpShared(Integer grpShared) { this.grpShared = grpShared; }

    public List<KnCorpSharedCorpInfo> getCorpSharedCorpInfoList() {  return corpSharedCorpInfoList;  }

    public void setCorpSharedCorpInfoList(List<KnCorpSharedCorpInfo> corpSharedCorpInfoList) {  this.corpSharedCorpInfoList = corpSharedCorpInfoList;  }

    public String getGrpOwnerCorpId() {return grpOwnerCorpId; }

    public void setGrpOwnerCorpId(String grpOwnerCorpId) {this.grpOwnerCorpId = grpOwnerCorpId; }

    public String getGrpOwnerExtCorpId() {return grpOwnerExtCorpId; }

    public void setGrpOwnerExtCorpId(String grpOwnerExtCorpId) {this.grpOwnerExtCorpId = grpOwnerExtCorpId; }

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

    public Integer getExternalCorpGroup() {
        return externalCorpGroup;
    }

    public void setExternalCorpGroup(Integer externalCorpGroup) {
        this.externalCorpGroup = externalCorpGroup;
    }

    public Collection<String> getExternalMdnList() {
        return externalMdnList;
    }

    public void setExternalMdnList(Collection<String> externalMdnList) {
        this.externalMdnList = externalMdnList;
    }

    public String getUgwConfig() {
        return ugwConfig;
    }

    public void setUgwConfig(String ugwConfig) {
        this.ugwConfig = ugwConfig;
    }

    public String getGrpSIPUri() {
        return grpSIPUri;
    }

    public void setGrpSIPUri(String grpSIPUri) {
        this.grpSIPUri = grpSIPUri;
    }

    public List<Integer> getGroupIdFromURI() {
        return groupIdFromURI;
    }

    public void setGroupIdFromURI(List<Integer> groupIdFromURI) {
        this.groupIdFromURI = groupIdFromURI;
    }

    public Integer getUgwInterop() {
        return ugwInterop;
    }

    public void setUgwInterop(Integer ugwInterop) {
        this.ugwInterop = ugwInterop;
    }

    public String getUgwInteropSystemConfig() {
        return ugwInteropSystemConfig;
    }

    public void setUgwInteropSystemConfig(String ugwInteropSystemConfig) {
        this.ugwInteropSystemConfig = ugwInteropSystemConfig;
    }
    public String getRecordingFs() {
        return recordingFs;
    }

    public void setRecordingFs(String recordingFs) {
        this.recordingFs = recordingFs;
    }

    public List<KnXDMGroupPropertyInfoDTO> getGroupPropertyList() {
        return groupPropertyList;
    }

    public void setGroupPropertyList(List<KnXDMGroupPropertyInfoDTO> groupPropertyList) {
        this.groupPropertyList = groupPropertyList;
    }

    public List<String> getSharedIdList() {
        return sharedIdList;
    }

    public void setSharedIdList(List<String> sharedIdList) {
        this.sharedIdList = sharedIdList;
    }
    public Integer getAuthorizedLargeTG() { return authorizedLargeTG;}
    public void setAuthorizedLargeTG(Integer authorizedLargeTG) { this.authorizedLargeTG = authorizedLargeTG;}

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
        StringBuffer sb = new StringBuffer(400);
        sb.append(super.toString())
               // .append(", owner - ").append(owner)
                .append(", corpId - ").append(corpId)
                .append(", groupId - ").append(groupId)
                .append(", groupDisplayName - ").append(groupDisplayName)
                .append(", newGroupDisplayName - ").append(newGroupDisplayName)
                .append(", eTag; - ").append(eTag)
                .append(", groupType; - ").append(groupType)
                .append(", groupMemCount; - ").append(groupMemCount)
                .append(", overrideDnd; - ").append(overrideDnd)
                .append(", avatar - ").append(avatar)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", contactPairing - ").append(contactPairing)
                .append(", lmrInteropCapable; - ").append(lmrInteropCapable)
                .append(", tpGroupOwner - ").append(tpGroupOwner)
                .append(", tpRequestMdn - ").append(tpRequestMdn)
                .append(", tpVendorID - ").append(tpVendorID)
                .append(", groupCreatedBy - ").append(groupCreatedBy)
                .append(", hangTimeOut; - ").append(hangTimeOut)
                .append(", emergOverrideDND; - ").append(emergOverrideDND)
                .append(", emergHangTimeAddOn; - ").append(emergHangTimeAddOn)
                .append(", emergAutoFloorTimer; - ").append(emergAutoFloorTimer)
                .append(", isEmergGrp; - ").append(isEmergGrp)
                .append(", isLargeGroup - ").append(isLargeGroup)
                .append(", OSMListId - ").append(OSMListId)
                .append(", osmListIdAndDefultMap - ").append(osmListIdAndDefultMap)
                .append(", pocSubsInfo - ").append(pocSubsInfo)
                .append(", pocHome - ").append(pocHome)
                .append(", grpMemListId - ").append(grpMemListId)
                .append(", grpMemListId - ").append(grpMemListId)
        		.append(",mcxGrpInd -").append(mcxGrpInd)
                .append(",mcxGrpInd -").append(mcxGrpInd)
                .append(",audioCutIn -").append(audioCutIn)
                .append(",groupProfileId -").append(groupProfileId)
                .append(",groupServiceType -").append(groupServiceType)
                .append(",grpShared -").append(grpShared)
                .append(",corpSharedCorpInfoList -").append(corpSharedCorpInfoList)
                .append(",grpOwnerCorpId -").append(grpOwnerCorpId)
                .append(",addedOwnerIdList -").append(addedOwnerIdList)
                .append(",removedOwnerIdList -").append(removedOwnerIdList)
                .append(",grpOwnerExtCorpId -").append(grpOwnerExtCorpId)
                .append(",ownerIdList -").append(ownerIdList)
                .append(",externalCorpGroup -").append(externalCorpGroup)
                .append(",externalMdnList -").append(externalMdnList)
                .append(",isPreConfiguredGroup -").append(isPreConfiguredGroup)
                .append(",grpSIPUri -").append(grpSIPUri)
                .append(",ugwConfig -").append(ugwConfig)
                .append(",groupIdFromURI - ").append(groupIdFromURI)
                .append(",ugwInterop - ").append(ugwInterop)
                .append(",ugwInteropSystemConfig -").append(ugwInteropSystemConfig)
                .append(",groupPropertyList -").append(groupPropertyList)
                .append(",recordingFs -").append(recordingFs)
                .append(",sharedIdList -").append(sharedIdList)
                .append(",sharedIdList -").append(sharedIdList)
                .append(",idType -").append(idType)
                .append(",idValue -").append(idValue)
                .append(",isEmptyGroup -").append(isEmptyGroup)
                .append(",authorizedLargeTG -").append(authorizedLargeTG)
                .append(", clusterId -").append(clusterId)
                .append(",videoPermission -").append(videoPermission)
                .append(", hierarchyId -").append(hierarchyId);


        return sb.toString();
    }
}
