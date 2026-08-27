/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpProfileInfoDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 16, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.common;


import com.kodiak.xdms.server.common.dto.common.KnDialPlanInfoDTO;

import java.util.Collection;

public class KnCorpProfileInfoDTO {

    //From Corporate Profile table (DG.PoCCorpInfo)//
    //CorpId for the corporate Profile
    private String corpId;
    //Ext-CorpId for the corporate Profile which is referred by the CAT tool
    private String extCorpId;
    //This is the max Suibscriber for the corporate
    private int maxSubscrs;
    //This is the max CorporateList that a corporate can have
    private int maxCorpLists;
    //This is the max members per CorporateList that a corporate can have
    private int maxMemPerCorpList;
    //This is the max Groups that a Corporate can have
    private int maxCorpGroups;
    //This is the max Members Per Groups that a Corporate can have
    private int maxMemPerCorpGroup;

    //From global table DG.XDMS_Svc_Config_<pttserverid>//
    //this variable will hold the max contacts that a subscriber can have
    private int maxContactsPerSubsc;
    //This is the max Contacts allowed per Corporate
    private int maxExtContactsPerCorp;
    //This is to indicate if Supervisory override is enabled
    private int supervisoryOverrideEnabled;

    //This is to indicate if dispatch feature is enabled
    private int dispatchEnabled;

    //This is to indicate the max Dispatch Group Limit
    private int maxDispatchGroup;

    //This is to indicate max Members Per Dispatch Group
    private int maxMembersPerDispatchGroup;

    private Collection<KnDialPlanInfoDTO> dialPlanList;
    //corpName for the corporate name
    private String corpName;

    private int maxContactsPerRequest;

    private int enablePocDonorRadioSupport;

    private int maxSubsAllowedGenActvReq;

    private int enableTalkGroup;

    private int maxPriority;


    private int maxNniSubscrPerCorp;

    private int maxScanListSize;

    private int enableBCGFeature;

    private int maxMemPerBCGrp;

    //Indicator specifying if there are any Alias MDN or Group MDN in the incontext id
    //This used for enabling the gateway launch from the CAT UI
    private boolean isGWEnabled;

    //Its the POC system id
    private String pocSysId;

    private int allowSUContact;

    private int maxDisptcherPerDispatchGrp;

    private long configFeatureSet;

    private int maxSGPerGrp;
    
    //LMR client type changes	
    private int pttRadioScanListSize;
    private int pttRadioChannelListSize;
    private int pttRadioDefScanMode;

    private int isLocWatcher;
    private int maxLocWatcherPerGrp;
    private int convClientEnabled;
    private int osmFeatureFlag;

    private int textMsgFlag;
    private int multiMediaMsgFlag;
    private int locationMsgFlag;
    private int urgentMsgFlag;
    private int webDispatcherEnabled;
    private int interopLicenceType;
    private int maxSGPatchPerGrp;
    private int emergFeature;
    private int ambientListening;
    private int discreteListening;
    private int userCheck;
    private int userSvcCtrl;
    private int bulkExtContAllowed;
    private int maxChannelsPerZone;
    private int maxRadioChannels;
    private int maxZones;
    private int largeGrpSupport;
    private int maxLrgGrpPerCorp;
    private int maxMemPerLrgGrp;
    private int maxLrgBGrpPerCorp;
    private int maxMemPerLrgBGrp;
    private int deviceSharingFlag;
    private int mcVideoEnabled;
    private int mcVideoUnCfrmPullEnabled;
    private String maxStatusMsgPerOsmList;
    private String maxStatusShortTextLength;
    private String maxStatusMsgLength;
    private String maxBulkCorpAdminFsUpdateAllowed;
    private String opsCorpFs;
    private int userProfileMgmt;
	private int maxUserProfiles;
	private int maxMemNonAPSubList;
	private int maxAssignProfiles;
    private int allowGroupAcrossZones;
    private int isVLrgGrpEnabled;
    private int groupProfileMgmt;
    private String maxGrpProfiles;
    private Integer maxGrpsPerCriClient;
    private int groupSharingFeature;
    private int trkMailSupported;
    private int emergDestAll;
    private int upmSharingFlag;
    private String mcxGroupReGroupFlag;
    private int commonContactListSupport;
    private String maxCommonContactLisSize;
    private String maxCommonContactlistPerSub;
    private int pocGwSynEnable;
    private int hierarchyType;
    private int selfDnDPrivilege;
    private String catAccessPermSet;
    private String catAccessPermUpdateTS;
    private int largeAgencyDispatch;
    private int emergConfigTimerFeature;

    public int getPocGwSynEnable() {
        return pocGwSynEnable;
    }

    public void setPocGwSynEnable(int pocGwSynEnable) {
        this.pocGwSynEnable = pocGwSynEnable;
    }

    public String getMaxCommonContactLisSize() {
        return maxCommonContactLisSize;
    }

    public void setMaxCommonContactLisSize(String maxCommonContactLisSize) {
        this.maxCommonContactLisSize = maxCommonContactLisSize;
    }

    public String getMaxCommonContactlistPerSub() {
        return maxCommonContactlistPerSub;
    }

    public void setMaxCommonContactlistPerSub(String maxCommonContactlistPerSub) {
        this.maxCommonContactlistPerSub = maxCommonContactlistPerSub;
    }

    public int getCommonContactListSupport() {
        return commonContactListSupport;
    }

    public void setCommonContactListSupport(int commonContactListSupport) {
        this.commonContactListSupport = commonContactListSupport;
    }

    public int getIsVLrgGrpEnabled() {
		return isVLrgGrpEnabled;
	}

	public void setIsVLrgGrpEnabled(int isVLrgGrpEnabled) {
		this.isVLrgGrpEnabled = isVLrgGrpEnabled;
	}

    public int getEnableBCGFeature() {
        return enableBCGFeature;
    }

    public void setEnableBCGFeature(int enableBCGFeature) {
        this.enableBCGFeature = enableBCGFeature;
    }

    public int getMaxMemPerBCGrp() {
        return maxMemPerBCGrp;
    }

    public void setMaxMemPerBCGrp(int maxMemPerBCGrp) {
        this.maxMemPerBCGrp = maxMemPerBCGrp;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public int getMaxSubscrs() {
        return maxSubscrs;
    }

    public void setMaxSubscrs(int maxSubscrs) {
        this.maxSubscrs = maxSubscrs;
    }

    public int getMaxCorpLists() {
        return maxCorpLists;
    }

    public void setMaxCorpLists(int maxCorpLists) {
        this.maxCorpLists = maxCorpLists;
    }

    public int getMaxMemPerCorpList() {
        return maxMemPerCorpList;
    }

    public void setMaxMemPerCorpList(int maxMemPerCorpList) {
        this.maxMemPerCorpList = maxMemPerCorpList;
    }

    public int getMaxCorpGroups() {
        return maxCorpGroups;
    }

    public void setMaxCorpGroups(int maxCorpGroups) {
        this.maxCorpGroups = maxCorpGroups;
    }

    public int getMaxMemPerCorpGroup() {
        return maxMemPerCorpGroup;
    }

    public void setMaxMemPerCorpGroup(int maxMemPerCorpGroup) {
        this.maxMemPerCorpGroup = maxMemPerCorpGroup;
    }

    public int getMaxContactsPerSubsc() {
        return maxContactsPerSubsc;
    }

    public void setMaxContactsPerSubsc(int maxContactsPerSubsc) {
        this.maxContactsPerSubsc = maxContactsPerSubsc;
    }

    public int getMaxExtContactsPerCorp() {
        return maxExtContactsPerCorp;
    }

    public void setMaxExtContactsPerCorp(int maxExtContactsPerCorp) {
        this.maxExtContactsPerCorp = maxExtContactsPerCorp;
    }

    public int getSupervisoryOverrideEnabled() {
        return supervisoryOverrideEnabled;
    }

    public void setSupervisoryOverrideEnabled(int supervisoryOverrideEnabled) {
        this.supervisoryOverrideEnabled = supervisoryOverrideEnabled;
    }

    public int getDispatchEnabled() {
        return dispatchEnabled;
    }

    public void setDispatchEnabled(int dispatchEnabled) {
        this.dispatchEnabled = dispatchEnabled;
    }

    public int getMaxDispatchGroup() {
        return maxDispatchGroup;
    }

    public void setMaxDispatchGroup(int maxDispatchGroup) {
        this.maxDispatchGroup = maxDispatchGroup;
    }

    public int getMaxMembersPerDispatchGroup() {
        return maxMembersPerDispatchGroup;
    }

    public void setMaxMembersPerDispatchGroup(int maxMembersPerDispatchGroup) {
        this.maxMembersPerDispatchGroup = maxMembersPerDispatchGroup;
    }

    public Collection<KnDialPlanInfoDTO> getDialPlanList() {
        return dialPlanList;
    }

    public void setDialPlanList(Collection<KnDialPlanInfoDTO> dialPlanList) {
        this.dialPlanList = dialPlanList;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public int getMaxContactsPerRequest() {
        return maxContactsPerRequest;
    }

    public void setMaxContactsPerRequest(int maxContactsPerRequest) {
        this.maxContactsPerRequest = maxContactsPerRequest;
    }

    public int getEnablePocDonorRadioSupport() {
        return enablePocDonorRadioSupport;
    }

    public void setEnablePocDonorRadioSupport(int enablePocDonorRadioSupport) {
        this.enablePocDonorRadioSupport = enablePocDonorRadioSupport;
    }

    public int getMaxSubsAllowedGenActvReq() {
        return maxSubsAllowedGenActvReq;
    }

    public void setMaxSubsAllowedGenActvReq(int maxSubsAllowedGenActvReq) {
        this.maxSubsAllowedGenActvReq = maxSubsAllowedGenActvReq;
    }

    public int getEnableTalkGroup() {
        return enableTalkGroup;
    }

    public void setEnableTalkGroup(int enableTalkGroup) {
        this.enableTalkGroup = enableTalkGroup;
    }


    public int getMaxPriority() {
        return maxPriority;
    }

    public void setMaxPriority(int maxPriority) {
        this.maxPriority = maxPriority;
    }

    public int getMaxScanListSize() {
        return maxScanListSize;
    }

    public void setMaxScanListSize(int maxScanListSize) {
        this.maxScanListSize = maxScanListSize;
    }

    public int getMaxNniSubscrPerCorp() {
        return maxNniSubscrPerCorp;
    }

    public void setMaxNniSubscrPerCorp(int maxNniSubscrPerCorp) {
        this.maxNniSubscrPerCorp = maxNniSubscrPerCorp;
    }

    public boolean isGWEnabled() {
        return isGWEnabled;
    }

    public void setGWEnabled(boolean isGWEnabled) {
        this.isGWEnabled = isGWEnabled;
    }

    public String getPocSysId() {
        return pocSysId;
    }

    public void setPocSysId(String pocSysId) {
        this.pocSysId = pocSysId;
    }

    public int getAllowSUContact() {
        return allowSUContact;
    }

    public void setAllowSUContact(int allowSUContact) {
        this.allowSUContact = allowSUContact;
    }

    public int getMaxDisptcherPerDispatchGrp() {
        return maxDisptcherPerDispatchGrp;
    }

    public void setMaxDisptcherPerDispatchGrp(int maxDisptcherperDispatchGrp) {
        this.maxDisptcherPerDispatchGrp = maxDisptcherperDispatchGrp;
    }

    public long getConfigFeatureSet() {
        return configFeatureSet;
    }

    public void setConfigFeatureSet(long configFeatureSet) {
        this.configFeatureSet = configFeatureSet;
    }

    public int getMaxSGPerGrp() {
        return maxSGPerGrp;
    }

    public void setMaxSGPerGrp(int maxSGPerGrp) {
        this.maxSGPerGrp = maxSGPerGrp;
    }

	public int getPttRadioScanListSize() {
		return pttRadioScanListSize;
	}

	public void setPttRadioScanListSize(int pttRadioScanListSize) {
		this.pttRadioScanListSize = pttRadioScanListSize;
	}

	public int getPttRadioChannelListSize() {
		return pttRadioChannelListSize;
	}

	public void setPttRadioChannelListSize(int pttRadioChannelListSize) {
		this.pttRadioChannelListSize = pttRadioChannelListSize;
	}
	
	public int getPttRadioDefScanMode() {
		return pttRadioDefScanMode;
	}

	public void setPttRadioDefScanMode(int pttRadioDefScanMode) {
		this.pttRadioDefScanMode = pttRadioDefScanMode;
	}

    public int getIsLocWatcher() {
        return isLocWatcher;
    }

    public void setIsLocWatcher(int isLocWatcher) {
        this.isLocWatcher = isLocWatcher;
    }

    public int getMaxLocWatcherPerGrp() {
        return maxLocWatcherPerGrp;
    }

    public void setMaxLocWatcherPerGrp(int maxLocWatcherPerGrp) {
        this.maxLocWatcherPerGrp = maxLocWatcherPerGrp;
    }

    public int getConvClientEnabled() {
        return convClientEnabled;
    }

    public void setConvClientEnabled(int convClientEnabled) {
        this.convClientEnabled = convClientEnabled;
    }

    public int getOsmFeatureFlag() {
        return osmFeatureFlag;
    }

    public void setOsmFeatureFlag(int osmFeatureFlag) {
        this.osmFeatureFlag = osmFeatureFlag;
    }

    public int getTextMsgFlag() {
        return textMsgFlag;
    }

    public void setTextMsgFlag(int textMsgFlag) {
        this.textMsgFlag = textMsgFlag;
    }

    public int getMultiMediaMsgFlag() {
        return multiMediaMsgFlag;
    }

    public void setMultiMediaMsgFlag(int multiMediaMsgFlag) {
        this.multiMediaMsgFlag = multiMediaMsgFlag;
    }

    public int getLocationMsgFlag() {
        return locationMsgFlag;
    }

    public void setLocationMsgFlag(int locationMsgFlag) {
        this.locationMsgFlag = locationMsgFlag;
    }

    public int getUrgentMsgFlag() {
        return urgentMsgFlag;
    }

    public void setUrgentMsgFlag(int urgentMsgFlag) {
        this.urgentMsgFlag = urgentMsgFlag;
    }

    public int getWebDispatcherEnabled() {
        return webDispatcherEnabled;
    }

    public void setWebDispatcherEnabled(int webDispatcherEnabled) {
        this.webDispatcherEnabled = webDispatcherEnabled;
    }

    public int getInteropLicenceType() {
        return interopLicenceType;
    }

    public void setInteropLicenceType(int interopLicenceType) {
        this.interopLicenceType = interopLicenceType;
    }

    public int getMaxSGPatchPerGrp() {
        return maxSGPatchPerGrp;
    }

    public void setMaxSGPatchPerGrp(int maxSGPatchPerGrp) {
        this.maxSGPatchPerGrp = maxSGPatchPerGrp;
    }

    public int getEmergFeature() {
        return emergFeature;
    }

    public void setEmergFeature(int emergFeature) {
        this.emergFeature = emergFeature;
    }

    public int getAmbientListening() {
        return ambientListening;
    }

    public void setAmbientListening(int ambientListening) {
        this.ambientListening = ambientListening;
    }

    public int getDiscreteListening() {
        return discreteListening;
    }

    public void setDiscreteListening(int discreteListening) {
        this.discreteListening = discreteListening;
    }

    public int getUserCheck() {
        return userCheck;
    }

    public void setUserCheck(int userCheck) {
        this.userCheck = userCheck;
    }

    public int getUserSvcCtrl() {
        return userSvcCtrl;
    }

    public void setUserSvcCtrl(int userSvcCtrl) {
        this.userSvcCtrl = userSvcCtrl;
    }

    public int getBulkExtContAllowed() {
        return bulkExtContAllowed;
    }

    public void setBulkExtContAllowed(int bulkExtContAllowed) {
        this.bulkExtContAllowed = bulkExtContAllowed;
    }

    public int getMaxChannelsPerZone() {
        return maxChannelsPerZone;
    }

    public void setMaxChannelsPerZone(int maxChannelsPerZone) {
        this.maxChannelsPerZone = maxChannelsPerZone;
    }

    public int getMaxRadioChannels() {
        return maxRadioChannels;
    }

    public void setMaxRadioChannels(int maxRadioChannels) {
        this.maxRadioChannels = maxRadioChannels;
    }

    public int getMaxZones() {
        return maxZones;
    }

    public void setMaxZones(int maxZones) {
        this.maxZones = maxZones;
    }

    public int getLargeGrpSupport() {
        return largeGrpSupport;
    }

    public void setLargeGrpSupport(int largeGrpSupport) {
        this.largeGrpSupport = largeGrpSupport;
    }

    public int getMaxLrgGrpPerCorp() {
        return maxLrgGrpPerCorp;
    }

    public void setMaxLrgGrpPerCorp(int maxLrgGrpPerCorp) {
        this.maxLrgGrpPerCorp = maxLrgGrpPerCorp;
    }

    public int getMaxMemPerLrgGrp() {
        return maxMemPerLrgGrp;
    }

    public void setMaxMemPerLrgGrp(int maxMemPerLrgGrp) {
        this.maxMemPerLrgGrp = maxMemPerLrgGrp;
    }

    public int getMaxLrgBGrpPerCorp() {
        return maxLrgBGrpPerCorp;
    }

    public void setMaxLrgBGrpPerCorp(int maxLrgBGrpPerCorp) {
        this.maxLrgBGrpPerCorp = maxLrgBGrpPerCorp;
    }

    public int getMaxMemPerLrgBGrp() {
        return maxMemPerLrgBGrp;
    }

    public void setMaxMemPerLrgBGrp(int maxMemPerLrgBGrp) {
        this.maxMemPerLrgBGrp = maxMemPerLrgBGrp;
    }

    public int getDeviceSharingFlag() {
        return deviceSharingFlag;
    }

    public void setDeviceSharingFlag(int deviceSharingFlag) {
        this.deviceSharingFlag = deviceSharingFlag;
    }

    public int getMcVideoEnabled() {
        return mcVideoEnabled;
    }

    public void setMcVideoEnabled(int mcVideoEnabled) {
        this.mcVideoEnabled = mcVideoEnabled;
    }

    public int getMcVideoUnCfrmPullEnabled() {
        return mcVideoUnCfrmPullEnabled;
    }

    public void setMcVideoUnCfrmPullEnabled(int mcVideoUnCfrmPullEnabled) {
        this.mcVideoUnCfrmPullEnabled = mcVideoUnCfrmPullEnabled;
    }

    public String getMaxStatusMsgPerOsmList() {
        return maxStatusMsgPerOsmList;
    }

    public void setMaxStatusMsgPerOsmList(String maxStatusMsgPerOsmList) {
        this.maxStatusMsgPerOsmList = maxStatusMsgPerOsmList;
    }

    public String getMaxStatusShortTextLength() {
        return maxStatusShortTextLength;
    }

    public void setMaxStatusShortTextLength(String maxStatusShortTextLength) {
        this.maxStatusShortTextLength = maxStatusShortTextLength;
    }

    public String getMaxStatusMsgLength() {
        return maxStatusMsgLength;
    }

    public void setMaxStatusMsgLength(String maxStatusMsgLength) {
        this.maxStatusMsgLength = maxStatusMsgLength;
    }

    public String getMaxBulkCorpAdminFsUpdateAllowed() {
        return maxBulkCorpAdminFsUpdateAllowed;
    }

    public void setMaxBulkCorpAdminFsUpdateAllowed(String maxBulkCorpAdminFsUpdateAllowed) {
        this.maxBulkCorpAdminFsUpdateAllowed = maxBulkCorpAdminFsUpdateAllowed;
    }

    public String getOpsCorpFs() {
        return opsCorpFs;
    }

    public void setOpsCorpFs(String opsCorpFs) {
        this.opsCorpFs = opsCorpFs;
    }

    public int getUserProfileMgmt() {
		return userProfileMgmt;
	}

	public void setUserProfileMgmt(int userProfileMgmt) {
		this.userProfileMgmt = userProfileMgmt;
	}

	public int getMaxUserProfiles() {
		return maxUserProfiles;
	}

	public void setMaxUserProfiles(int maxUserProfiles) {
		this.maxUserProfiles = maxUserProfiles;
	}

	public int getMaxMemNonAPSubList() {
		return maxMemNonAPSubList;
	}

	public void setMaxMemNonAPSubList(int maxMemNonAPSubList) {
		this.maxMemNonAPSubList = maxMemNonAPSubList;
	}

	public int getMaxAssignProfiles() {
		return maxAssignProfiles;
	}

	public void setMaxAssignProfiles(int maxAssignProfiles) {
		this.maxAssignProfiles = maxAssignProfiles;
	}

    public int getAllowGroupAcrossZones() {
        return allowGroupAcrossZones;
    }

    public void setAllowGroupAcrossZones(int allowGroupAcrossZones) {
        this.allowGroupAcrossZones = allowGroupAcrossZones;
    }

    public int getGroupProfileMgmt() {
        return groupProfileMgmt;
    }

    public void setGroupProfileMgmt(int groupProfileMgmt) {
        this.groupProfileMgmt = groupProfileMgmt;
    }

    public String getMaxGrpProfiles() {
        return maxGrpProfiles;
    }

    public void setMaxGrpProfiles(String maxGrpProfiles) {
        this.maxGrpProfiles = maxGrpProfiles;
    }

    public Integer getMaxGrpsPerCriClient() {
        return maxGrpsPerCriClient;
    }

    public void setMaxGrpsPerCriClient(Integer maxGrpsPerCriClient) {
        this.maxGrpsPerCriClient = maxGrpsPerCriClient;
    }

    public int getGroupSharingFeature() {  return groupSharingFeature;  }

    public void setGroupSharingFeature(int groupSharingFeature) {  this.groupSharingFeature = groupSharingFeature; }

    public int getTrkMailSupported() { return trkMailSupported; }

    public void setTrkMailSupported(int trkMailSupported) { this.trkMailSupported = trkMailSupported; }

    public int getEmergDestAll() {
        return emergDestAll;
    }

    public void setEmergDestAll(int emergDestAll) {
        this.emergDestAll = emergDestAll;
    }

    public int getUpmSharingFlag() {
        return upmSharingFlag;
    }

    public void setUpmSharingFlag(int upmSharingFlag) {
        this.upmSharingFlag = upmSharingFlag;
    }
    public String getMcxGroupReGroupFlag() {
        return mcxGroupReGroupFlag;
    }

    public void setMcxGroupReGroupFlag(String mcxGroupReGroupFlag) {
        this.mcxGroupReGroupFlag = mcxGroupReGroupFlag;
    }

    public int getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(int hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public int getSelfDnDPrivilege() { return selfDnDPrivilege; }

    public void setSelfDnDPrivilege(int selfDnDPrivilege) { this.selfDnDPrivilege = selfDnDPrivilege; }

    public String getCatAccessPermSet() {
        return catAccessPermSet;
    }

    public void setCatAccessPermSet(String catAccessPermSet) {
        this.catAccessPermSet = catAccessPermSet;
    }

    public String getCatAccessPermUpdateTS() {
        return catAccessPermUpdateTS;
    }

    public void setCatAccessPermUpdateTS(String catAccessPermUpdateTS) {
        this.catAccessPermUpdateTS = catAccessPermUpdateTS;
    }

    public int getLargeAgencyDispatch() { return largeAgencyDispatch; }

    public void setLargeAgencyDispatch(int largeAgencyDispatch) { this.largeAgencyDispatch = largeAgencyDispatch; }

    public int getEmergConfigTimerFeature() {
        return emergConfigTimerFeature;
    }

    public void setEmergConfigTimerFeature(int emergConfigTimerFeature) {
        this.emergConfigTimerFeature = emergConfigTimerFeature;
    }



    @Override
    public String toString() {
        return "KnCorpProfileInfoDTO{" +
                "corpId='" + corpId + '\'' +
                ", extCorpId='" + extCorpId + '\'' +
                ", maxSubscrs=" + maxSubscrs +
                ", maxCorpLists=" + maxCorpLists +
                ", maxMemPerCorpList=" + maxMemPerCorpList +
                ", maxCorpGroups=" + maxCorpGroups +
                ", maxMemPerCorpGroup=" + maxMemPerCorpGroup +
                ", maxContactsPerSubsc=" + maxContactsPerSubsc +
                ", maxExtContactsPerCorp=" + maxExtContactsPerCorp +
                ", supervisoryOverrideEnabled=" + supervisoryOverrideEnabled +
                ", dispatchEnabled=" + dispatchEnabled +
                ", maxDispatchGroup=" + maxDispatchGroup +
                ", maxMembersPerDispatchGroup=" + maxMembersPerDispatchGroup +
                ", dialPlanList=" + dialPlanList +
                ", corpName='" + corpName + '\'' +
                ", maxContactsPerRequest=" + maxContactsPerRequest +
                ", enablePocDonorRadioSupport=" + enablePocDonorRadioSupport +
                ", maxSubsAllowedGenActvReq=" + maxSubsAllowedGenActvReq +
                ", enableTalkGroup=" + enableTalkGroup +
                ", maxPriority=" + maxPriority +
                ", maxNniSubscrPerCorp=" + maxNniSubscrPerCorp +
                ", maxScanListSize=" + maxScanListSize +
                ", enableBCGFeature=" + enableBCGFeature +
                ", maxMemPerBCGrp=" + maxMemPerBCGrp +
                ", isGWEnabled=" + isGWEnabled +
                ", pocSysId='" + pocSysId + '\'' +
                ", allowSUContact=" + allowSUContact +
                ", maxDisptcherPerDispatchGrp=" + maxDisptcherPerDispatchGrp +
                ", configFeatureSet=" + configFeatureSet +
                ", maxSGPerGrp=" + maxSGPerGrp +
                ", pttRadioScanListSize=" + pttRadioScanListSize +
                ", pttRadioChannelListSize=" + pttRadioChannelListSize +
                ", pttRadioDefScanMode=" + pttRadioDefScanMode +
                ", isLocWatcher=" + isLocWatcher +
                ", maxLocWatcherPerGrp=" + maxLocWatcherPerGrp +
                ", convClientEnabled=" + convClientEnabled +
                ", osmFeatureFlag=" + osmFeatureFlag +
                ", textMsgFlag=" + textMsgFlag +
                ", multiMediaMsgFlag=" + multiMediaMsgFlag +
                ", locationMsgFlag=" + locationMsgFlag +
                ", urgentMsgFlag=" + urgentMsgFlag +
                ", webDispatcherEnabled=" + webDispatcherEnabled +
                ", interopLicenceType=" + interopLicenceType +
                ", maxSGPatchPerGrp=" + maxSGPatchPerGrp +
                ", emergFeature=" + emergFeature +
                ", ambientListening=" + ambientListening +
                ", discreteListening=" + discreteListening +
                ", userCheck=" + userCheck +
                ", userSvcCtrl=" + userSvcCtrl +
                ", bulkExtContAllowed=" + bulkExtContAllowed +
                ", maxChannelsPerZone=" + maxChannelsPerZone +
                ", maxRadioChannels=" + maxRadioChannels +
                ", maxZones=" + maxZones +
                ", largeGrpSupport=" + largeGrpSupport +
                ", maxLrgGrpPerCorp=" + maxLrgGrpPerCorp +
                ", maxMemPerLrgGrp=" + maxMemPerLrgGrp +
                ", maxLrgBGrpPerCorp=" + maxLrgBGrpPerCorp +
                ", maxMemPerLrgBGrp=" + maxMemPerLrgBGrp +
                ", deviceSharingFlag=" + deviceSharingFlag +
                ", mcVideoEnabled=" + mcVideoEnabled +
                ", mcVideoUnCfrmPullEnabled=" + mcVideoUnCfrmPullEnabled +
                ", maxBulkCorpAdminFsUpdateAllowed=" + maxBulkCorpAdminFsUpdateAllowed +
                ", opsCorpFs=" + opsCorpFs +
                ", userProfileMgmt=" + userProfileMgmt +
                ", maxUserProfiles=" + maxUserProfiles +
                ", maxMemNonAPSubList=" + maxMemNonAPSubList +
                ", maxAssignProfiles=" + maxAssignProfiles +
                ", allowGroupAcrossZones=" + allowGroupAcrossZones +
                ", groupProfileMgmt=" + groupProfileMgmt +
                ", maxGrpProfiles=" + maxGrpProfiles +
                ", maxGrpsPerCriClient=" + maxGrpsPerCriClient +
                ", groupSharingFeature=" + groupSharingFeature +
                ", trkMailSupported=" + trkMailSupported +
                ", emergDestAll=" + emergDestAll +
                ", upmSharingFlag=" + upmSharingFlag +
                ", mcxGroupReGroupFlag=" + mcxGroupReGroupFlag +
                ", commonContactListSupport=" + commonContactListSupport +
                ", maxCommonContactLisSize=" + maxCommonContactLisSize +
                ", maxCommonContactlistPerSub=" + maxCommonContactlistPerSub +
                ", pocGwSynEnable =" + pocGwSynEnable +
                ", hierarchyType =" + hierarchyType +
                ", selfDnDPrivilege =" + selfDnDPrivilege +
                ", catAccessPermSet =" + catAccessPermSet +
                ", catAccessPermUpdateTS =" + catAccessPermUpdateTS +
                ", largeAgencyDispatch =" + largeAgencyDispatch +
                ", emergConfigTimerFeature =" + emergConfigTimerFeature +
                '}';
    }
}