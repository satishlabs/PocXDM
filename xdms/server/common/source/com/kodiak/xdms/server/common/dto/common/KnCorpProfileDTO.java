/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpProfileDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 24, 2011      7.0
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
package com.kodiak.xdms.server.common.dto.common;


public class KnCorpProfileDTO extends KnProfileDTO {

    private static final long serialVersionUID = 7526471155622676171L;

    //Ext-CorpId for the corporate Profile which is referred by the CAT tool
    private String extCorpId;
    //This is the paired contactListId for the corp
    private int pairedContactListId;
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
    private int maxExtContactsPerCorp;
    private int maxGroupsPerSubsc;
    private int maxGroupsPerLargeDispatch;

    //to hold the max contacts per request
    private int maxContactsPerRequest;

    //This is to indicate if Supervisory override is enabled
    private int supervisoryOverrideEnabled;

    //This is to indicate if dispatch feature is enabled
    private int dispatchEnabled;

    //This is to indicate the max Dispatch Group Limit
    private int maxDispatchGroup;

    //This is to indicate max Members Per Dispatch Group
    private int maxMembersPerDispatchGroup;

    //this is to indicate max dispatch member per group
    private  int maxDispatchMembersPerDispatchGroup;

    // This is to hold the current corporate etag values
    private long corpMasterListEtag;

    private int enablePocDonorRadioSupport;

    private String protocolVersion;
    private int maxSubsAllowedGenActvReq;

    //This is to hold max allowed camped group per subscriber
    private int maxCampedGrp;
    //This is to hold system wide TGS feature bit
    private int systemTGSBit;
    //This is to hold the maximum configured external subscribers per corp
    private int maxExtSubsPerCorp;

    private int maxPriority;
    
    private int tgscanningClient;
    
    private int enableBCGrpFeature;

    private int maxMemPerBCGrp;

    private String linkedGwKey;

    private int nxtGenCatEnabled;

    private int maxSGPerGrp;

    //to hold max ptt radio clients scanlist size
    private int maxPttRadioScanSize;

    //to hold max ptt radio clients channel size
    private int maxPttRadioChannelSize;

    private int pttRadioDefScanMode;
    private int pttRadioScanListSize;
    private int pttRadioChannelListSize;

    //XDMData Interface:
    private int maxTextMsgSize;

    private int maxMmmsgSizeCell;

    private int maxMmmsgSizeWifi;

    private int deliveryReceiptFlag;

    private int readReportFlag;

    private int msgTtl;

    private int maxPredefinedMsgCnt;

    private int maxPredefinedTmpltCnt;

    private int maxUserDefinedMsgCnt;

    private int fleetMemberGeoTagFlag;
    private int textMsgFlag;

    private int multiMediaMsgFlag;

    private int locationMsgFlag;

    private int urgentMsgFlag;

    private int webDispatchEnabled;

    private int interopLicenceType;

    private int maxSGPatchPerGrp;

    private int emergFeature;

    private int ambientListening;

    private int discreteListening;

    private int userCheck;

    private int userSvcCtrl;

    private int maxAbdgTalkGrp;

    private int maxLrGabTalkGroup;

    private int maxUsrLrGabGroup;

    private int maxAbdgPerGrpMember;

    private int maxAbdgPerGrpOwner;

    private int maxChannelsPerZone;

    private int maxRadioChannels;

    private int maxZones;

    private int largeGrpSupported;

    private int maxLrgGrpPerCorp;
    private int maxMemPerLrgGrp;
    private int maxLrgBGrpPerCorp;
    private int maxMemPerLrgBGrp;
    private int maxSDDSession;
    private int maxSDYSession;
    private int mcVideoEnabled;
    private int mcVideoUnCfrmPullEnabled;
    private String activeFS2;
    private String corpFS2;
    private String opsCorpFs2;
    private int lmrIntropFlag;
	private Integer privacyAmbDiscListenFlag;
	private Integer mcvideoFloorHoldTimer;
    private Integer userProfileMgmt;
	private Integer maxUserProfiles;
	private Integer maxAssignProfiles;
    private Integer groupProfileMgmt;
    private Integer maxgGroupProfiles;
    private Integer groupSharingFeature;
    private Integer userProfileSharingFeature;
    private Integer commonContactListSupport;
    private int hierarchyType;
    private String catAccessPermSet;
    private String catAccessPermUpdateTS;
    private String xdmCorpFS2Set;

    public Integer getCommonContactListSupport() {
        return commonContactListSupport;
    }

    public void setCommonContactListSupport(Integer commonContactListSupport) {
        this.commonContactListSupport = commonContactListSupport;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public int getPairedContactListId() {
        return pairedContactListId;
    }

    public void setPairedContactListId(int pairedContactListId) {
        this.pairedContactListId = pairedContactListId;
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

    public int getMaxGroupsPerSubsc() {
        return maxGroupsPerSubsc;
    }

    public void setMaxGroupsPerSubsc(int maxGroupsPerSubsc) {
        this.maxGroupsPerSubsc = maxGroupsPerSubsc;
    }

    public int getMaxGroupsPerLargeDispatch() {
        return maxGroupsPerLargeDispatch;
    }

    public void setMaxGroupsPerLargeDispatch(int maxGroupsPerLargeDispatch) {
        this.maxGroupsPerLargeDispatch = maxGroupsPerLargeDispatch;
    }
    
    public int getMaxContactsPerRequest() {
        return maxContactsPerRequest;
    }

    public void setMaxContactsPerRequest(int maxContactsPerRequest) {
        this.maxContactsPerRequest = maxContactsPerRequest;
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

    public int getMaxDispatchMembersPerDispatchGroup() {
        return maxDispatchMembersPerDispatchGroup;
    }

    public void setMaxDispatchMembersPerDispatchGroup(int maxDispatchMembersPerDispatchGroup) {
        this.maxDispatchMembersPerDispatchGroup = maxDispatchMembersPerDispatchGroup;
    }

    public long getCorpMasterListEtag() {
        return corpMasterListEtag;
    }

    public void setCorpMasterListEtag(long corpMasterListEtag) {
        this.corpMasterListEtag = corpMasterListEtag;
    }

    public int getEnablePocDonorRadioSupport() {
        return enablePocDonorRadioSupport;
    }

    public void setEnablePocDonorRadioSupport(int enablePocDonorRadioSupport) {
        this.enablePocDonorRadioSupport = enablePocDonorRadioSupport;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getMaxSubsAllowedGenActvReq() {
        return maxSubsAllowedGenActvReq;
    }

    public void setMaxSubsAllowedGenActvReq(int maxSubsAllowedGenActvReq) {
        this.maxSubsAllowedGenActvReq = maxSubsAllowedGenActvReq;
    }

    public int getMaxCampedGrp() {
        return maxCampedGrp;
    }

    public void setMaxCampedGrp(int maxCampedGrp) {
        this.maxCampedGrp = maxCampedGrp;
    }

    public int getSystemTGSBit() {
        return systemTGSBit;
    }

    public void setSystemTGSBit(int systemTGSBit) {
        this.systemTGSBit = systemTGSBit;
    }

    public int getMaxExtSubsPerCorp() {
        return maxExtSubsPerCorp;
    }

    public void setMaxExtSubsPerCorp(int maxExtSubsPerCorp) {
        this.maxExtSubsPerCorp = maxExtSubsPerCorp;
    }

    public int getEnableBCGrpFeature() {
        return enableBCGrpFeature;
    }

    public void setEnableBCGrpFeature(int enableBCGrpFeature) {
        this.enableBCGrpFeature = enableBCGrpFeature;
    }

    public int getMaxMemPerBCGrp() {
        return maxMemPerBCGrp;
    }

    public void setMaxMemPerBCGrp(int maxMemPerBCGrp) {
        this.maxMemPerBCGrp = maxMemPerBCGrp;
    }

    public int getMaxPriority() { return maxPriority; }

    public void setMaxPriority(int maxPriority) { this.maxPriority = maxPriority;}

    public int getTgscanningClient() { return tgscanningClient; }

    public void setTgscanningClient(int tgscanningClient) { this.tgscanningClient = tgscanningClient; }

    public String getLinkedGwKey() {
        return linkedGwKey;
    }

    public void setLinkedGwKey(String linkedGwKey) {
        this.linkedGwKey = linkedGwKey;
    }

    public int getNxtGenCatEnabled() {
        return nxtGenCatEnabled;
    }

    public void setNxtGenCatEnabled(int nxtGenCatEnabled) {
        this.nxtGenCatEnabled = nxtGenCatEnabled;
    }

    public int getMaxSGPerGrp() {
        return maxSGPerGrp;
    }

    public void setMaxSGPerGrp(int maxSGPerGrp) {
        this.maxSGPerGrp = maxSGPerGrp;
    }

    public int getMaxPttRadioScanSize() {
        return maxPttRadioScanSize;
    }

    public void setMaxPttRadioScanSize(int maxPttRadioScanSize) {
        this.maxPttRadioScanSize = maxPttRadioScanSize;
    }

    public int getMaxPttRadioChannelSize() {
        return maxPttRadioChannelSize;
    }

    public void setMaxPttRadioChannelSize(int maxPttRadioChannelSize) {
        this.maxPttRadioChannelSize = maxPttRadioChannelSize;
    }

    public int getPttRadioDefScanMode() {
        return pttRadioDefScanMode;
    }

    public void setPttRadioDefScanMode(int pttRadioDefScanMode) {
        this.pttRadioDefScanMode = pttRadioDefScanMode;
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

    public int getMaxTextMsgSize() {
        return maxTextMsgSize;
    }

    public void setMaxTextMsgSize(int maxTextMsgSize) {
        this.maxTextMsgSize = maxTextMsgSize;
    }

    public int getMaxMmmsgSizeCell() {
        return maxMmmsgSizeCell;
    }

    public void setMaxMmmsgSizeCell(int maxMmmsgSizeCell) {
        this.maxMmmsgSizeCell = maxMmmsgSizeCell;
    }

    public int getMaxMmmsgSizeWifi() {
        return maxMmmsgSizeWifi;
    }

    public void setMaxMmmsgSizeWifi(int maxMmmsgSizeWifi) {
        this.maxMmmsgSizeWifi = maxMmmsgSizeWifi;
    }

    public int getDeliveryReceiptFlag() {
        return deliveryReceiptFlag;
    }

    public void setDeliveryReceiptFlag(int deliveryReceiptFlag) {
        this.deliveryReceiptFlag = deliveryReceiptFlag;
    }

    public int getMsgTtl() {
        return msgTtl;
    }

    public void setMsgTtl(int msgTtl) {
        this.msgTtl = msgTtl;
    }

    public int getMaxPredefinedMsgCnt() {
        return maxPredefinedMsgCnt;
    }

    public void setMaxPredefinedMsgCnt(int maxPredefinedMsgCnt) {
        this.maxPredefinedMsgCnt = maxPredefinedMsgCnt;
    }

    public int getMaxPredefinedTmpltCnt() {
        return maxPredefinedTmpltCnt;
    }

    public void setMaxPredefinedTmpltCnt(int maxPredefinedTmpltCnt) {
        this.maxPredefinedTmpltCnt = maxPredefinedTmpltCnt;
    }

    public int getMaxUserDefinedMsgCnt() {
        return maxUserDefinedMsgCnt;
    }

    public void setMaxUserDefinedMsgCnt(int maxUserDefinedMsgCnt) {
        this.maxUserDefinedMsgCnt = maxUserDefinedMsgCnt;
    }

    public int getFleetMemberGeoTagFlag() {
        return fleetMemberGeoTagFlag;
    }

    public void setFleetMemberGeoTagFlag(int fleetMemberGeoTagFlag) {
        this.fleetMemberGeoTagFlag = fleetMemberGeoTagFlag;
    }

    public int getReadReportFlag() {
        return readReportFlag;
    }

    public void setReadReportFlag(int readReportFlag) {
        this.readReportFlag = readReportFlag;
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

    public int getWebDispatchEnabled() {
        return webDispatchEnabled;
    }

    public void setWebDispatchEnabled(int webDispatchEnabled) {
        this.webDispatchEnabled = webDispatchEnabled;
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

    public int getMaxAbdgTalkGrp() {
        return maxAbdgTalkGrp;
    }

    public void setMaxAbdgTalkGrp(int maxAbdgTalkGrp) {
        this.maxAbdgTalkGrp = maxAbdgTalkGrp;
    }

    public int getMaxLrGabTalkGroup() {
        return maxLrGabTalkGroup;
    }

    public void setMaxLrGabTalkGroup(int maxLrGabTalkGroup) {
        this.maxLrGabTalkGroup = maxLrGabTalkGroup;
    }

    public int getMaxUsrLrGabGroup() {
        return maxUsrLrGabGroup;
    }

    public void setMaxUsrLrGabGroup(int maxUsrLrGabGroup) {
        this.maxUsrLrGabGroup = maxUsrLrGabGroup;
    }

    public int getMaxAbdgPerGrpMember() {
        return maxAbdgPerGrpMember;
    }

    public void setMaxAbdgPerGrpMember(int maxAbdgPerGrpMember) {
        this.maxAbdgPerGrpMember = maxAbdgPerGrpMember;
    }

    public int getMaxAbdgPerGrpOwner() {
        return maxAbdgPerGrpOwner;
    }

    public void setMaxAbdgPerGrpOwner(int maxAbdgPerGrpOwner) {
        this.maxAbdgPerGrpOwner = maxAbdgPerGrpOwner;
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

    public int getLargeGrpSupported() {
        return largeGrpSupported;
    }

    public void setLargeGrpSupported(int largeGrpSupported) {
        this.largeGrpSupported = largeGrpSupported;
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

    public int getMaxSDDSession() { return maxSDDSession; }

    public void setMaxSDDSession(int maxSDDSession) { this.maxSDDSession = maxSDDSession; }

    public int getMaxSDYSession() { return maxSDYSession; }

    public void setMaxSDYSession(int maxSDYSession) { this.maxSDYSession = maxSDYSession; }

    public String getActiveFS2() {
		return activeFS2;
	}

	public void setActiveFS2(String activeFS2) {
		this.activeFS2 = activeFS2;
	}

	public String getCorpFS2() {
		return corpFS2;
	}

	public void setCorpFS2(String corpFS2) {
		this.corpFS2 = corpFS2;
	}

	public String getOpsCorpFs2() {
		return opsCorpFs2;
	}

	public void setOpsCorpFs2(String opsCorpFs2) {
		this.opsCorpFs2 = opsCorpFs2;
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

    public int getLmrIntropFlag() {
        return lmrIntropFlag;
    }

    public void setLmrIntropFlag(int lmrIntropFlag) {
        this.lmrIntropFlag = lmrIntropFlag;
    }
	
	public Integer getPrivacyAmbDiscListenFlag() {
        return privacyAmbDiscListenFlag;
    }

    public void setPrivacyAmbDiscListenFlag(Integer privacyAmbDiscListenFlag) {
        this.privacyAmbDiscListenFlag = privacyAmbDiscListenFlag;
    }

    public Integer getMcvideoFloorHoldTimer() {return mcvideoFloorHoldTimer; }

    public void setMcvideoFloorHoldTimer(Integer mcvideoFloorHoldTimer) {
        this.mcvideoFloorHoldTimer = mcvideoFloorHoldTimer;
    }

    public Integer getUserProfileMgmt() {
        return userProfileMgmt;
    }

    public void setUserProfileMgmt(Integer userProfileMgmt) {
        this.userProfileMgmt = userProfileMgmt;
    }

    public Integer getMaxUserProfiles() {
        return maxUserProfiles;
    }

    public void setMaxUserProfiles(Integer maxUserProfiles) {
        this.maxUserProfiles = maxUserProfiles;
    }

    public Integer getMaxAssignProfiles() {
        return maxAssignProfiles;
    }

    public void setMaxAssignProfiles(Integer maxAssignProfiles) {
        this.maxAssignProfiles = maxAssignProfiles;
    }

    public Integer getGroupProfileMgmt() {
        return groupProfileMgmt;
    }

    public void setGroupProfileMgmt(Integer groupProfileMgmt) {
        this.groupProfileMgmt = groupProfileMgmt;
    }

    public Integer getMaxgGroupProfiles() {
        return maxgGroupProfiles;
    }

    public void setMaxgGroupProfiles(Integer maxgGroupProfiles) { this.maxgGroupProfiles = maxgGroupProfiles; }

    public Integer getGroupSharingFeature() {  return groupSharingFeature; }

    public void setGroupSharingFeature(Integer groupSharingFeature) {  this.groupSharingFeature = groupSharingFeature; }

    public Integer getUserProfileSharingFeature() {
        return userProfileSharingFeature;
    }

    public void setUserProfileSharingFeature(Integer userProfileSharingFeature) {
        this.userProfileSharingFeature = userProfileSharingFeature;
    }

    public int getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(int hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

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

    public String getXdmCorpFS2Set() {
        return xdmCorpFS2Set;
    }

    public void setXdmCorpFS2Set(String xdmCorpFS2Set) {
        this.xdmCorpFS2Set = xdmCorpFS2Set;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(super.toString())
                .append(", ExtCorpId - ").append(extCorpId)
                .append(", PairingContactListId - ").append(pairedContactListId)
                .append(", maxCorpLists - ").append(maxCorpLists)
                .append(", maxMemPerCorpList - ").append(maxMemPerCorpList)
                .append(", maxCorpGroups - ").append(maxCorpGroups)
                .append(", maxMemPerCorpGroup - ").append(maxMemPerCorpGroup)
                .append(", maxContactsPerSubsc - ").append(maxContactsPerSubsc)
                .append(", maxExtContactsPerCorp - ").append(maxExtContactsPerCorp)
                .append(", maxGroupsPerSubsc - ").append(maxGroupsPerSubsc)
                .append(", maxContactsPerRequest - ").append(maxContactsPerRequest)
                .append(", supervisoryOverrideEnabled - ").append(supervisoryOverrideEnabled)
                .append(", dispatchEnabled - ").append(dispatchEnabled)
                .append(", maxDispatchGroup - ").append(maxDispatchGroup)
                .append(", maxMembersPerDispatchGroup - ").append(maxMembersPerDispatchGroup)
                .append(", maxDispatchMembersPerDispatchGroup - ").append(maxDispatchMembersPerDispatchGroup)
                .append(", corpMasterListEtag - ").append(corpMasterListEtag)
                .append(", enablePocDonorRadioSupport - ").append(enablePocDonorRadioSupport)
                .append(", protocolVersion - ").append(protocolVersion)
                .append(", maxSubsAllowedGenActvReq - ").append(maxSubsAllowedGenActvReq)
                .append(", maxCampedGrp - ").append(maxCampedGrp)
                .append(", systemTGSBit - ").append(systemTGSBit)
                .append(", maxExtSubsPerCorp - ").append(maxExtSubsPerCorp)
                .append(", enableBCGrpFeature - ").append(enableBCGrpFeature)
                .append(", maxMemPerBCGrp - ").append(maxMemPerBCGrp)
                .append(", maxExtSubsPerCorp - ").append(maxExtSubsPerCorp)
                .append(", linkedGwKey - ").append(linkedGwKey)
                .append(", tgscanningClient - ").append(tgscanningClient)
                .append(", nxtGenCatEnabled - ").append(nxtGenCatEnabled)
                .append(", maxSGPerGrp - ").append(maxSGPerGrp)
                .append(", maxPttRadioChannelSize - ").append(maxPttRadioChannelSize)
                .append(", maxPttRadioScanSize - ").append(maxPttRadioScanSize)
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
                .append(", textMsgFlag - ").append(textMsgFlag)
                .append(", multiMediaMsgFlag - ").append(multiMediaMsgFlag)
                .append(", locationMsgFlag - ").append(locationMsgFlag)
                .append(", urgentMsgFlag - ").append(urgentMsgFlag)
                .append(", pttRadioDefScanMode - ").append(pttRadioDefScanMode)
                .append(", pttRadioScanListSize - ").append(pttRadioScanListSize)
                .append(", pttRadioChannelListSize - ").append(pttRadioChannelListSize)
                .append(", webDispatchEnabled - ").append(webDispatchEnabled)
                .append(", interopLicenceType - ").append(interopLicenceType)
                .append(", maxSGPatchPerGrp - ").append(maxSGPatchPerGrp)
                .append(", emergFeature - ").append(emergFeature)
                .append(", maxAbdgTalkGrp - ").append(maxAbdgTalkGrp)
                .append(", maxLrGabTalkGroup - ").append(maxLrGabTalkGroup)
                .append(", maxUsrLrGabGroup - ").append(maxUsrLrGabGroup)
                .append(", maxAbdgPerGrpMember - ").append(maxAbdgPerGrpMember)
                .append(", maxAbdgPerGrpOwner - ").append(maxAbdgPerGrpOwner)
                .append(", maxChannelsPerZone - ").append(maxChannelsPerZone)
                .append(", maxRadioChannels - ").append(maxRadioChannels)
                .append(", maxZones - ").append(maxZones)
                .append(", largeGrpSupported - ").append(largeGrpSupported)
                .append(", maxLrgGrpPerCorp - ").append(maxLrgGrpPerCorp)
                .append(", maxMemPerLrgGrp - ").append(maxMemPerLrgGrp)
                .append(", maxLrgBGrpPerCorp - ").append(maxLrgBGrpPerCorp)
                .append(", maxMemPerLrgBGrp - ").append(maxMemPerLrgBGrp)
                .append(", maxsimuldedeicatedsession - ").append(maxSDDSession)
                .append(", maxsimuldynamicsession - ").append(maxSDYSession)
                .append("activeFS2 - ").append(activeFS2)
                .append("corpFS2 - ").append(corpFS2)
                .append("opsCorpFs2 - ").append(opsCorpFs2)
                .append("mcVideoEnabled - ").append(mcVideoEnabled)
                .append("mcVideoUnCfrmPullEnabled - ").append(mcVideoUnCfrmPullEnabled)
				.append("privacyAmbDiscListenFlag - ").append(privacyAmbDiscListenFlag)
                .append("mcvideoFloorHoldTimer - ").append(mcvideoFloorHoldTimer)
				.append(", lmrIntropFlag - ").append(lmrIntropFlag)
                .append(", maxAssignProfiles - ").append(maxAssignProfiles)
                .append(", groupProfileMgmt - ").append(groupProfileMgmt)
                .append(", maxgGroupProfiles - ").append(maxgGroupProfiles)
                .append(", groupSharingFeature - ").append(groupSharingFeature)
                .append(", commonContactListSupport - ").append(commonContactListSupport)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", catAccessPermSet - ").append(catAccessPermSet)
                .append(", catAccessPermUpdateTS - ").append(catAccessPermUpdateTS)
                .append(", userProfileSharingFeature - ").append(userProfileSharingFeature)
                .append(", maxGroupsPerLargeDispatch - ").append(maxGroupsPerLargeDispatch)
                .append(", xdmCorpFS2Set - ").append(xdmCorpFS2Set);
        return sb.toString();
    }
}
