/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpProfileInfoRespDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 24, 2011      7.0
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


import java.util.Collection;

public class KnXDMCorpProfileInfoRespDTO extends KnXDMCorpRespDTO {

    private static final long serialVersionUID = 7526471155622776152L;

    private String corpId;
    //Ext-CorpId for the corporate Profile which is referred by the CAT tool
    private String extCorpId;
    //This is the max CorporateList that a corporate can have
    private int maxCorpLists;
    //This is the max members per CorporateList that a corporate can have
    private int maxMemPerCorpList;
    //This is the max Groups that a Corporate can have
    private int maxCorpGroups;
    //This is the max Members Per Groups that a Corporate can have
    private int maxMemPerCorpGroup;
    //This variable will hold the max contacts that a subscriber can have
    private int maxContactsPerSubsc;
    //This is the max external Contacts allowed per corporate
    private int maxExtContactsPerCorp;
    //This is the flag to identify supervisory override
    private boolean supervisoryOverrideEnabled;
    //This is flag to identify dispatch client feature
    private boolean dispatchEnabled;
    //This is max members per request
    private int maxContactsPerRequest;
    //this is flag to identify hierarchy
    private boolean isHierarchyEnabled;
    //this is flag to identify StandAlone
    private boolean isStandAlone;
    //This is max dispatch group
    private int maxDispatchGroup;
    //This is max members per dispatch group
    private int maxMembersPerDispatchGroup;
    //corpName for the corporate Profile name
    private String corpName;

    private Collection<KnDialPlanDTO> dialPlanList;

    private boolean pocDonorRadioSupport;

     private int maxSubsAllowedGenActvReq;

    private boolean enableTalkGroup;
    
    private int maxPriority;
    
    private int maxScanListSize;

    private int maxNniSubscrPerCorp;
    private boolean isBCGrpEnabled;
    private int maxMemPerBCGrp;
    //Indicator specifying if there are any Alias MDN or Group MDN in the incontext id
    //This used for enabling the gateway launch from the CAT UI
    private boolean isGWEnabled;
    //Its the POC system id
    private String pocSysId;
    private int allowSUContact;
    private int maxDispatchersPerDispatchGrp;
    private long configFeatureSet;
    private int maxSGPerGrp;
    
    //LMR client type changes	
    private int pttRadioScanListSize;
    private int pttRadioChannelListSize;
    private int pttRadioDefScanMode;
    private int maxLocWatcherGrp;

    // XDMDataIntf: REST API:
    private String maxTextMsgSize;
    private String maxMmmsgSizeCell;
    private String maxMmmsgSizeWifi;
    private String deliveryReceiptFlag;
    private String readReportFlag;
    private String msgTtl;
    private String maxPredefinedMsgCnt;
    private String maxPredefinedTmpltCnt;
    private String maxUserDefinedMsgCnt;
    private String fleetMemberGeoTagFlag;
    private int maxSGPatchPerGrp;
    private int bulkExtContAllowed;
    private String maxAbdgTalkGroup;
    private String maxLrGabTalkGroup;
    private String maxUsrLrGabGroup;
    private String maxAbdgGrpPerOwner;
    private String maxAbdgGrpPerMem;
    private String maxChannelsPerZone;
    private String maxRadioChannels;
    private String maxZones;
    private int maxLrgGrpPerCorp;
    private int maxMemPerLrgGrp;
    private int maxLrgBGrpPerCorp;
    private int maxMemPerLrgBGrp;
    private String maxStatusMsgPerOsmList;
    private String maxStatusShortTextLength;
    private String maxStatusMsgLength;
    private String maxBulkCorpAdminFsUpdateAllowed;
	private Integer lmrDataIntropFlag;
	private String lastProfileUpdateTime;
	private int maxUserProfiles;
	private int maxMemNonAPSubList;
	private int maxAssignProfiles;
    private String maxGrpProfiles;
    private Integer maxGrpsPerCriClient;
    private Integer groupSharingFeature;
    private Integer upmSharingFeature;
    private Integer ugwInteropFlag;
    private String maxCommonContactLisSize;
    private String maxCommonContactlistPerSub;
    private String selfDnDPrivilege;
    private String catAccessPermSet;
    private String catAccessPermUpdateTS;
    private String largeAgencyDispatch;
    private String emergConfigTimerFeature;
    private String xdmCorpFs2Set;

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

    public Integer getUgwInteropFlag() {
        return ugwInteropFlag;
    }

    public void setUgwInteropFlag(Integer ugwInteropFlag) {
        this.ugwInteropFlag = ugwInteropFlag;
    }

    public boolean isBCGrpEnabled() {
        return isBCGrpEnabled;
    }

    public void setBCGrpEnabled(boolean isBCGrpEnabled) {
        this.isBCGrpEnabled = isBCGrpEnabled;
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

    public boolean isSupervisoryOverrideEnabled() {
        return supervisoryOverrideEnabled;
    }

    public void setSupervisoryOverrideEnabled(boolean supervisoryOverrideEnabled) {
        this.supervisoryOverrideEnabled = supervisoryOverrideEnabled;
    }

    public boolean isStandAlone() {
        return isStandAlone;
    }

    public void setStandAlone(boolean standAlone) {
        isStandAlone = standAlone;
    }

    public boolean isHierarchyEnabled() {
        return isHierarchyEnabled;
    }

    public void setHierarchyEnabled(boolean hierarchyEnabled) {
        isHierarchyEnabled = hierarchyEnabled;
    }

    public int getMaxContactsPerRequest() {
        return maxContactsPerRequest;
    }

    public void setMaxContactsPerRequest(int maxContactsPerRequest) {
        this.maxContactsPerRequest = maxContactsPerRequest;
    }

    public boolean isDispatchEnabled() {
        return dispatchEnabled;
    }

    public void setDispatchEnabled(boolean dispatchEnabled) {
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

    public Collection<KnDialPlanDTO> getDialPlanList() {
        return dialPlanList;
    }

    public void setDialPlanList(Collection<KnDialPlanDTO> dialPlanList) {
        this.dialPlanList = dialPlanList;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public boolean isPocDonorRadioSupport() {
        return pocDonorRadioSupport;
    }

    public void setPocDonorRadioSupport(boolean pocDonorRadioSupport) {
        this.pocDonorRadioSupport = pocDonorRadioSupport;
    }

    public int getMaxSubsAllowedGenActvReq() {
        return maxSubsAllowedGenActvReq;
    }

    public void setMaxSubsAllowedGenActvReq(int maxSubsAllowedGenActvReq) {
        this.maxSubsAllowedGenActvReq = maxSubsAllowedGenActvReq;
    }

    public boolean isEnableTalkGroup() {
        return enableTalkGroup;
    }

    public void setEnableTalkGroup(boolean enableTalkGroup) {
        this.enableTalkGroup = enableTalkGroup;
    }
    

    public int getMaxPriority() {
		return maxPriority;
	}

	public void setMaxPriority(int maxPriority) {
		this.maxPriority = maxPriority;
	}

    public int getMaxNniSubscrPerCorp() {
        return maxNniSubscrPerCorp;
    }

    public void setMaxNniSubscrPerCorp(int maxNniSubscrPerCorp) {
        this.maxNniSubscrPerCorp = maxNniSubscrPerCorp;
    }

	public int getMaxScanListSize() {
		return maxScanListSize;
	}

	public void setMaxScanListSize(int maxScanListSize) {
		this.maxScanListSize = maxScanListSize;
	}

    public boolean isGWEnabled() { return isGWEnabled;  }

    public void setGWEnabled(boolean isGWEnabled) { this.isGWEnabled = isGWEnabled; }

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

    public int getMaxDispatchersPerDispatchGrp() {
        return maxDispatchersPerDispatchGrp;
    }

    public void setMaxDispatchersPerDispatchGrp(int maxDispatchersPerDispatchGrp) {
        this.maxDispatchersPerDispatchGrp = maxDispatchersPerDispatchGrp;
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

    public int getMaxLocWatcherGrp() {
        return maxLocWatcherGrp;
    }

    public void setMaxLocWatcherGrp(int maxLocWatcherGrp) {
        this.maxLocWatcherGrp = maxLocWatcherGrp;
    }

    public String getMaxTextMsgSize() {
        return maxTextMsgSize;
    }

    public void setMaxTextMsgSize(String maxTextMsgSize) {
        this.maxTextMsgSize = maxTextMsgSize;
    }

    public String getMaxMmmsgSizeCell() {
        return maxMmmsgSizeCell;
    }

    public void setMaxMmmsgSizeCell(String maxMmmsgSizeCell) {
        this.maxMmmsgSizeCell = maxMmmsgSizeCell;
    }

    public String getMaxMmmsgSizeWifi() {
        return maxMmmsgSizeWifi;
    }

    public void setMaxMmmsgSizeWifi(String maxMmmsgSizeWifi) {
        this.maxMmmsgSizeWifi = maxMmmsgSizeWifi;
    }

    public String getDeliveryReceiptFlag() {
        return deliveryReceiptFlag;
    }

    public void setDeliveryReceiptFlag(String deliveryReceiptFlag) {
        this.deliveryReceiptFlag = deliveryReceiptFlag;
    }

    public String getMsgTtl() {
        return msgTtl;
    }

    public void setMsgTtl(String msgTtl) {
        this.msgTtl = msgTtl;
    }

    public String getMaxPredefinedMsgCnt() {
        return maxPredefinedMsgCnt;
    }

    public void setMaxPredefinedMsgCnt(String maxPredefinedMsgCnt) {
        this.maxPredefinedMsgCnt = maxPredefinedMsgCnt;
    }

    public String getMaxPredefinedTmpltCnt() {
        return maxPredefinedTmpltCnt;
    }

    public void setMaxPredefinedTmpltCnt(String maxPredefinedTmpltCnt) {
        this.maxPredefinedTmpltCnt = maxPredefinedTmpltCnt;
    }

    public String getMaxUserDefinedMsgCnt() {
        return maxUserDefinedMsgCnt;
    }

    public void setMaxUserDefinedMsgCnt(String maxUserDefinedMsgCnt) {
        this.maxUserDefinedMsgCnt = maxUserDefinedMsgCnt;
    }

    public String getFleetMemberGeoTagFlag() {
        return fleetMemberGeoTagFlag;
    }

    public void setFleetMemberGeoTagFlag(String fleetMemberGeoTagFlag) {
        this.fleetMemberGeoTagFlag = fleetMemberGeoTagFlag;
    }

    public String getReadReportFlag() {
        return readReportFlag;
    }

    public void setReadReportFlag(String readReportFlag) {
        this.readReportFlag = readReportFlag;
    }

    public int getMaxSGPatchPerGrp() {
        return maxSGPatchPerGrp;
    }

    public void setMaxSGPatchPerGrp(int maxSGPatchPerGrp) {
        this.maxSGPatchPerGrp = maxSGPatchPerGrp;
    }

    public int getBulkExtContAllowed() {
        return bulkExtContAllowed;
    }

    public void setBulkExtContAllowed(int bulkExtContAllowed) {
        this.bulkExtContAllowed = bulkExtContAllowed;
    }

    public String getMaxAbdgTalkGroup() {
        return maxAbdgTalkGroup;
    }

    public void setMaxAbdgTalkGroup(String maxAbdgTalkGroup) {
        this.maxAbdgTalkGroup = maxAbdgTalkGroup;
    }

    public String getMaxLrGabTalkGroup() {
        return maxLrGabTalkGroup;
    }

    public void setMaxLrGabTalkGroup(String maxLrGabTalkGroup) {
        this.maxLrGabTalkGroup = maxLrGabTalkGroup;
    }

    public String getMaxUsrLrGabGroup() {
        return maxUsrLrGabGroup;
    }

    public void setMaxUsrLrGabGroup(String maxUsrLrGabGroup) {
        this.maxUsrLrGabGroup = maxUsrLrGabGroup;
    }

    public String getMaxAbdgGrpPerOwner() {
        return maxAbdgGrpPerOwner;
    }

    public void setMaxAbdgGrpPerOwner(String maxAbdgGrpPerOwner) {
        this.maxAbdgGrpPerOwner = maxAbdgGrpPerOwner;
    }

    public String getMaxAbdgGrpPerMem() {
        return maxAbdgGrpPerMem;
    }

    public void setMaxAbdgGrpPerMem(String maxAbdgGrpPerMem) {
        this.maxAbdgGrpPerMem = maxAbdgGrpPerMem;
    }

    public String getMaxChannelsPerZone() {
        return maxChannelsPerZone;
    }

    public void setMaxChannelsPerZone(String maxChannelsPerZone) {
        this.maxChannelsPerZone = maxChannelsPerZone;
    }

    public String getMaxRadioChannels() {
        return maxRadioChannels;
    }

    public void setMaxRadioChannels(String maxRadioChannels) {
        this.maxRadioChannels = maxRadioChannels;
    }

    public String getMaxZones() {
        return maxZones;
    }

    public void setMaxZones(String maxZones) {
        this.maxZones = maxZones;
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

	public Integer getLmrDataIntropFlag() {
        return lmrDataIntropFlag;
    }

    public void setLmrDataIntropFlag(Integer lmrDataIntropFlag) {
        this.lmrDataIntropFlag = lmrDataIntropFlag;
    }

    public String getLastProfileUpdateTime() {
        return lastProfileUpdateTime;
    }

    public void setLastProfileUpdateTime(String lastProfileUpdateTime) {
        this.lastProfileUpdateTime = lastProfileUpdateTime;
    }
    public String getMaxBulkCorpAdminFsUpdateAllowed() {
        return maxBulkCorpAdminFsUpdateAllowed;
    }

    public void setMaxBulkCorpAdminFsUpdateAllowed(String maxBulkCorpAdminFsUpdateAllowed) {
        this.maxBulkCorpAdminFsUpdateAllowed = maxBulkCorpAdminFsUpdateAllowed;
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
    public Integer getGroupSharingFeature() {  return groupSharingFeature; }

    public void setGroupSharingFeature(Integer groupSharingFeature) {  this.groupSharingFeature = groupSharingFeature; }

    public Integer getUpmSharingFeature() {
        return upmSharingFeature;
    }

    public void setUpmSharingFeature(Integer upmSharingFeature) {
        this.upmSharingFeature = upmSharingFeature;
    }

    public String getSelfDnDPrivilege() { return selfDnDPrivilege; }

    public void setSelfDnDPrivilege(String selfDnDPrivilege) { this.selfDnDPrivilege = selfDnDPrivilege; }

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

    public String getLargeAgencyDispatch() { return largeAgencyDispatch; }

    public void setLargeAgencyDispatch(String largeAgencyDispatch) { this.largeAgencyDispatch = largeAgencyDispatch; }

    public String getEmergConfigTimerFeature() {
        return emergConfigTimerFeature;
    }

    public void setEmergConfigTimerFeature(String emergConfigTimerFeature) {
        this.emergConfigTimerFeature = emergConfigTimerFeature;
    }
    public String getXdmCorpFs2Set() {
        return xdmCorpFs2Set;
    }
    public void setXdmCorpFs2Set(String xdmCorpFs2Set) {
        this.xdmCorpFs2Set = xdmCorpFs2Set;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(super.toString())
                .append(", CorpId - ").append(corpId)
                .append(", ExtCorpId - ").append(extCorpId)
                .append(", maxCorpLists - ").append(maxCorpLists)
                .append(", maxMemPerCorpList - ").append(maxMemPerCorpList)
                .append(", maxCorpGroups - ").append(maxCorpGroups)
                .append(", maxMemPerCorpGroup - ").append(maxMemPerCorpGroup)
                .append(", maxContactsPerSubsc - ").append(maxContactsPerSubsc)
                .append(", maxExtContactsPerCorporate - ").append(maxExtContactsPerCorp)
                .append(", supervisoryOverrideEnabled - ").append(supervisoryOverrideEnabled)
                .append(", dispatchEnabled - ").append(dispatchEnabled)
                .append(", maxContactsPerRequest - ").append(maxContactsPerRequest)
                .append(", isHierarchyEnabled - ").append(isHierarchyEnabled)
                .append(", isStandAlone - ").append(isStandAlone)
                .append(", maxDispatchGroup - ").append(maxDispatchGroup)
                .append(", maxMembersPerDispatchGroup - ").append(maxMembersPerDispatchGroup)
                .append(", corpName - ").append(corpName)
                .append(", dialPlanList - ").append(dialPlanList)
                .append(", pocDonorRadioSupport - ").append(pocDonorRadioSupport)
                .append(", maxSubsAllowedGenActvReq - ").append(maxSubsAllowedGenActvReq)
                .append(", enableTalkGroup - ").append(enableTalkGroup)
                .append(", maxScanListSize - ").append(maxScanListSize)
                .append(", maxPriority - ").append(maxPriority)
                .append(", maxNniSubscrPerCorp - ").append(maxNniSubscrPerCorp)
                .append(", isGWEnabled - ").append(isGWEnabled)
                .append(", allowSUContact - ").append(allowSUContact)
                .append(", pocSysId - ").append(pocSysId)
                .append(", configFeatureSet - ").append(configFeatureSet)
                .append(", maxSGPerGrp - ").append(maxSGPerGrp)
                .append(", maxDispatchersPerDispatchGrp - ").append(maxDispatchersPerDispatchGrp)
                .append(", pttRadioScanListSize - ").append(pttRadioScanListSize)
                .append(", pttRadioChannelListSize - ").append(pttRadioChannelListSize)
                .append(", pttRadioDefScanMode - ").append(pttRadioDefScanMode)
                .append(", maxLocWatcherGrp - ").append(maxLocWatcherGrp)
                .append(", maxTextMsgSize - ").append(maxTextMsgSize)
                .append(", maxMmmsgSizeCell - ").append(maxMmmsgSizeCell)
                .append(", maxMmmsgSizeWifi - ").append(maxMmmsgSizeWifi)
                .append(", deliveryReceiptFlag - ").append(deliveryReceiptFlag)
                .append(", readReportFlag - ").append(readReportFlag)
                .append(", msgTtl - ").append(msgTtl)
                .append(", maxPredefinedMsgCnt - ").append(maxPredefinedMsgCnt)
                .append(", maxPredefinedTmpltCnt - ").append(maxPredefinedTmpltCnt)
                .append(", maxUserDefinedMsgCnt - ").append(maxUserDefinedMsgCnt)
                .append(", fleetMemberGeoTagFlag - ").append(fleetMemberGeoTagFlag)
                .append(", maxSGPatchPerGrp - ").append(maxSGPatchPerGrp)
                .append(", bulkExtContAllowed - ").append(bulkExtContAllowed)
                .append(", maxAbdgTalkGroup - ").append(maxAbdgTalkGroup)
                .append(", maxLrGabTalkGroup - ").append(maxLrGabTalkGroup)
                .append(", maxUsrLrGabGroup - ").append(maxUsrLrGabGroup)
                .append(", maxAbdgGrpPerOwner - ").append(maxAbdgGrpPerOwner)
                .append(", maxAbdgGrpPerMem - ").append(maxAbdgGrpPerMem)
                .append(", maxChannelsPerZone - ").append(maxChannelsPerZone)
                .append(", maxRadioChannels - ").append(maxRadioChannels)
                .append(", maxZones - ").append(maxZones)
                .append(", maxLrgGrpPerCorp - ").append(maxLrgGrpPerCorp)
                .append(", maxMemPerLrgGrp - ").append(maxMemPerLrgGrp)
                .append(", maxLrgBGrpPerCorp - ").append(maxLrgBGrpPerCorp)
                .append(", maxMemPerLrgBGrp - ").append(maxMemPerLrgBGrp)
                .append(", maxStatusMsgPerOsmList - ").append(maxStatusMsgPerOsmList)
                .append(", maxStatusShortTextLength - ").append(maxStatusShortTextLength)
                .append(", maxStatusMsgLength - ").append(maxStatusMsgLength)
                .append(", maxBulkCorpAdminFsUpdateAllowed - ").append(maxBulkCorpAdminFsUpdateAllowed)
				.append(", lmrDataIntropFlag - ").append(lmrDataIntropFlag)
                .append(", lastProfileUpdateTime - ").append(lastProfileUpdateTime)
                .append(", maxUserProfiles - ").append(maxUserProfiles)
                .append(", maxMemNonAPSubList - ").append(maxMemNonAPSubList)
        		.append(", maxAssignProfiles - ").append(maxAssignProfiles)
                .append(", maxGrpProfiles - ").append(maxGrpProfiles)
                .append(", maxGrpsPerCriClient - ").append(maxGrpsPerCriClient)
                .append(", groupSharingFeature - ").append(groupSharingFeature)
                .append(", upmSharingFeature - ").append(upmSharingFeature)
                .append(", ugwInteropFlag - ").append(ugwInteropFlag)
                .append(", maxCommonContactLisSize - ").append(maxCommonContactLisSize)
                .append(", maxCommonContactlistPerSub - ").append(maxCommonContactlistPerSub)
                .append(", upmSharingFeature - ").append(upmSharingFeature)
                .append(", selfDnDPrivilege - ").append(selfDnDPrivilege)
                .append(", catAccessPermSet - ").append(catAccessPermSet)
                .append(", catAccessPermUpdateTS - ").append(catAccessPermUpdateTS)
                .append(", largeAgencyDispatch - ").append(largeAgencyDispatch)
                .append(", emergConfigTimerFeature - ").append(emergConfigTimerFeature)
                .append(", xdmCorpFs2Set - ").append(xdmCorpFs2Set);

        return sb.toString();
    }
}
