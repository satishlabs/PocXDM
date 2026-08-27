/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpResponseDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 18, 2011      7.0
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

import com.kodiak.common.commdto.common.KnCorpGroupContactDTO;
import com.kodiak.common.commdto.common.KnXDMCorpUserDetailsRespDTO;
import com.kodiak.common.commdto.request.KnXDMSubsAliasDetailsReqDTO;
import com.kodiak.common.commdto.response.KnXDMFailureRespDTO;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.utilities.lieventhandler.dto.KnLIEventDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDispatchDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnTGSModeChgDTO;
import com.kodiak.xdms.server.corpmgmt.dto.ICorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;

import java.util.*;


public class KnCorpCloningResponseDTO implements ICorpResponseDTO {

    //0 - success, 1 - failure
    private int status = 0;
    private String statusCode;
    private String message;
    private Collection<KnCorpFailedData> failedDataList;
    private Map<String, KnOPDirChgDTO> changeLogMap;
    private String etag;
    private Collection<String> disabledDispatchMemList;
    private Map<Integer, List<String>> disabledDispatchMemListMap;
    private Collection<String> enabledDispatchMemList;
    private int pairedContactListId;
    private boolean isSublistExists;
    // to hold the TGS Mode change documents
    private Map<String, KnTGSModeChgDTO> tgsModeChgMap;
    private LinkedList<KnLIEventDTO> liEventList;
    private int peg;
    private Collection<KnCorpContactDTO> addedGroupMember;
    private Collection<String> addedGroupDistributionMember;
    private boolean isSyncDisabled;
    private long activeFS;
    private String mdn;
    private int mdnCorpId;
	private Collection<KnXDMFailureRespDTO> failureDetails;
    private boolean isProfileChanged;
    private Map<String,Object> responseMap;
    private KnXDMCorpUserDetailsRespDTO idmSubscriberDTO;
    private boolean isUserIdChanged;
    private boolean isUserOverriden;
    private List<String> mdnList;
    private int deletedGroupId;
    private boolean isAliasMdnChanged;
    private boolean isAliasOverriden;
    private int groupCreatedBy;
    private KnXDMSubsAliasDetailsReqDTO oidcSubscriberDTO;
    private Map<String, KnOPDispatchDirChgDTO> msDtoMap;
    private boolean subsDeactivated;
    private int dispatchType;
    private Collection<Integer> groupIds;
    //stores the notifications Object
    private List<KnOPDirChgDTO> dirChgDTOs;
    private List<KnOPDirChgDTO> profileMdnDirChgDTOs;
    private Map<String, String> updateTimeMap;
    private int publicSubscriptionType;
	private String activeFS2;
	private Integer lmrIntropCapable;
    private Collection<String> addedLocWatcherList;
    private Collection<String> removedLocWatcherList;
    private int mcpttCompliance;
    private String mcId;
    private String mcpttId;
    private String mcDataId;
    private String mcVideoId;
    private String aliasMdn;
    private String networkName;
    private Collection<KnCorpUserProfileDTO> userProfileList;
    private KnCorpUserProfileDTO userProfile;
    private int onBoardingMailReqd;
    private Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap;
    private Map<String, String> mcsXcapRootUriMap;
    private Map<Integer,Integer> grpMemberShipMap;
    private Map<Integer, KnCorpGroupContactDTO > mcxGrpMemberShipMap;
    private int mcxGrpInd;
    private String userProfileId;
    // Added by DTXJ47 MCSJAVALIB-1577
    private String pocHome;
    private Map<Integer, KnCorpGroupInfoDTO> groupSharedMap;
    private Map<Integer, List<Integer>> groupIdSharedCorpListMap;
    private Map<String, String> profileMdnActiveFsMap;
    private String subscriberEmailId;
    List<KnAsyncJobDTO> jobStatus;
    private List<String> ownerIDList;
    private Map<Integer,String> delGrpPocHomeMap;
    private String tgscMode;
    private String sharingEnabled;
    private List<String> userProfileSharedCorpList;
    private String ownerCorpId;
    private String ownerExtCorpId;
    private Boolean isMdnAuthorized;
    private Collection<String> onlineMdnList;
    private List<String> unAuthorizedGroupURIList;
    private String grpSIPUri;
    private String grpUri;
    private String userId;
    private String deviceImpi;
    private String deviceImpu;
    private String deviceDigestPwd;
    private String mcsLoginUrl;
    private String recordingFs;
    //Just for getting oldFS of ProfileMDN
    private Map<String, KnCorpSubscriberDTO> mdnUpmFsMap;
    private Integer oldLmrInteropFlag;
    private Integer lmrInteropFlag;
    private String newGroupDisplayName;
    private String billingMDN;
    private List<String> actualDeletedBroadcaster;
    private boolean corpNotification;
    private int intCorpId;
    private String extCorpId;
    private boolean isDispacherPresent;
    private Set<String> profileMDNs;
    private boolean isLocationDisabled;
    private int locwatcherCount;
    private int userProfileCount;
    private boolean isEmergencyAttributesChanged;
    private List<Integer> upmCount;

    public List<Integer> getUpmCount() {
        return upmCount;
    }

    public void setUpmCount(List<Integer> upmCount) {
        this.upmCount = upmCount;
    }

    public int getUserProfileCount() {
        return userProfileCount;
    }

    public void setUserProfileCount(int userProfileCount) {
        this.userProfileCount = userProfileCount;
    }

    public int getLocwatcherCount() {
        return locwatcherCount;
    }

    public void setLocwatcherCount(int locwatcherCount) {
        this.locwatcherCount = locwatcherCount;
    }

    public boolean getIsLocationDisabled() {
        return isLocationDisabled;
    }

    public void setIsLocationDisabled(boolean isLocationDisabled) {
        this.isLocationDisabled = isLocationDisabled;
    }

    public Set<String> getProfileMDNs() {
        return profileMDNs;
    }

    public void setProfileMDNs(Set<String> profileMDNs) {
        this.profileMDNs = profileMDNs;
    }

    public boolean getIsDispacherPresent() {
        return isDispacherPresent;
    }

    public void setIsDispacherPresent(boolean isDispacherPresent) {
        this.isDispacherPresent = isDispacherPresent;
    }

    public String getBillingMDN() {
        return billingMDN;
    }

    public void setBillingMDN(String billingMDN) {
        this.billingMDN = billingMDN;
    }

    public String getNewGroupDisplayName() {
        return newGroupDisplayName;
    }

    public void setNewGroupDisplayName(String newGroupDisplayName) {
        this.newGroupDisplayName = newGroupDisplayName;
    }

    public String getMcsLoginUrl() {
        return mcsLoginUrl;
    }

    public void setMcsLoginUrl(String mcsLoginUrl) {
        this.mcsLoginUrl = mcsLoginUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceImpi() {
        return deviceImpi;
    }

    public void setDeviceImpi(String deviceImpi) {
        this.deviceImpi = deviceImpi;
    }

    public String getDeviceImpu() {
        return deviceImpu;
    }

    public void setDeviceImpu(String deviceImpu) {
        this.deviceImpu = deviceImpu;
    }

    public String getDeviceDigestPwd() {
        return deviceDigestPwd;
    }

    public void setDeviceDigestPwd(String deviceDigestPwd) {
        this.deviceDigestPwd = deviceDigestPwd;
    }

    public Map<String, KnCorpSubscriberDTO> getMdnUpmFsMap() {
        return mdnUpmFsMap;
    }

    public void setMdnUpmFsMap(Map<String, KnCorpSubscriberDTO> mdnUpmFsMap) {
        this.mdnUpmFsMap = mdnUpmFsMap;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }


    public int getMcxGrpInd() {
        return mcxGrpInd;
    }

    public void setMcxGrpInd(int mcxGrpInd) {
        this.mcxGrpInd = mcxGrpInd;
    }

    public int getPeg() {
        return peg;
    }

    public void setPeg(int peg) {
        this.peg = peg;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Collection<KnCorpFailedData> getFailedDataList() {
        return failedDataList;
    }

    public void setFailedDataList(Collection<KnCorpFailedData> failedDataList) {
        this.failedDataList = failedDataList;
    }

    public Map<String, KnOPDirChgDTO> getChangeLogMap() {
        return changeLogMap;
    }

    public void setChangeLogMap(Map<String, KnOPDirChgDTO> changeLogMap) {
        this.changeLogMap = changeLogMap;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Collection<String> getDisabledDispatchMemList() {
        return disabledDispatchMemList;
    }

    public void setDisabledDispatchMemList(Collection<String> disabledDispatchMemList) {
        this.disabledDispatchMemList = disabledDispatchMemList;
    }

    public Collection<String> getEnabledDispatchMemList() {
        return enabledDispatchMemList;
    }

    public void setEnabledDispatchMemList(Collection<String> enabledDispatchMemList) {
        this.enabledDispatchMemList = enabledDispatchMemList;
    }

    public int getPairedContactListId() {
        return pairedContactListId;
    }

    public void setPairedContactListId(int pairedContactListId) {
        this.pairedContactListId = pairedContactListId;
    }

    public Map<String, KnTGSModeChgDTO> getTgsModeChgMap() {
        return tgsModeChgMap;
    }

    public void setTgsModeChgMap(Map<String, KnTGSModeChgDTO> tgsModeChgMap) {
        this.tgsModeChgMap = tgsModeChgMap;
    }

    public LinkedList<KnLIEventDTO> getLiEventList() {
        return liEventList;
    }

    public void setLiEventList(LinkedList<KnLIEventDTO> liEventList) {
        this.liEventList = liEventList;
    }

    public boolean getIsSublistExists() {
        return isSublistExists;
    }

    public void setIsSublistExists(boolean isSublistExists) {
        this.isSublistExists = isSublistExists;
    }

    public Collection<KnCorpContactDTO> getAddedGroupMember() {
        return addedGroupMember;
    }

    public void setAddedGroupMember(Collection<KnCorpContactDTO> addedGroupMember) {
        this.addedGroupMember = addedGroupMember;
    }

    public Collection<String> getAddedGroupDistributionMember() {
        return addedGroupDistributionMember;
    }

    public void setAddedGroupDistributionMember(Collection<String> addedGroupDistributionMember) {
        this.addedGroupDistributionMember = addedGroupDistributionMember;
    }

    public boolean isSyncDisabled() {
        return isSyncDisabled;
    }

    public void setSyncDisabled(boolean isSyncDisabled) {
        this.isSyncDisabled = isSyncDisabled;
    }

    public long getActiveFS() {
        return activeFS;
    }

    public void setActiveFS(long activeFS) {
        this.activeFS = activeFS;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public int getMdnCorpId() {
        return mdnCorpId;
    }

    public void setMdnCorpId(int mdnCorpId) {
        this.mdnCorpId = mdnCorpId;
    }
    public List<KnOPDirChgDTO> getDirChgDTOs() {
        return dirChgDTOs;
    }

    public void setDirChgDTOs(List<KnOPDirChgDTO> dirChgDTOs) {
        this.dirChgDTOs = dirChgDTOs;
    }

    public boolean isProfileChanged() {
        return isProfileChanged;
    }

    public void setProfileChanged(boolean profileChanged) {
        isProfileChanged = profileChanged;
    }

    public Map<String, Object> getResponseMap() {
        return responseMap;
    }

    public void setResponseMap(Map<String, Object> responseMap) {
        this.responseMap = responseMap;
    }

    public KnXDMCorpUserDetailsRespDTO getIdmSubscriberDTO() {
        return idmSubscriberDTO;
    }

    public void setIdmSubscriberDTO(KnXDMCorpUserDetailsRespDTO idmSubscriberDTO) {
        this.idmSubscriberDTO = idmSubscriberDTO;
    }

    public boolean isUserIdChanged() {
        return isUserIdChanged;
    }

    public void setUserIdChanged(boolean userIdChanged) {
        isUserIdChanged = userIdChanged;
    }

    public boolean isUserOverriden() {
        return isUserOverriden;
    }

    public void setUserOverriden(boolean userOverriden) {
        isUserOverriden = userOverriden;
    }

    @Override
	public Collection<KnXDMFailureRespDTO> getFailureDetails() {
		return failureDetails;
	}

	@Override
	public void setFailureDetails(Collection<KnXDMFailureRespDTO> failureDetails) {
		this.failureDetails = failureDetails;
	}

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    public int getDeletedGroupId() {
        return deletedGroupId;
    }

    public void setDeletedGroupId(int deletedGroupId) {
        this.deletedGroupId = deletedGroupId;
    }

    public boolean isAliasMdnChanged() {
        return isAliasMdnChanged;
    }

    public void setAliasMdnChanged(boolean aliasMdnChanged) {
        isAliasMdnChanged = aliasMdnChanged;
    }

    public boolean isAliasOverriden() {
        return isAliasOverriden;
    }

    public void setAliasOverriden(boolean aliasOverriden) {
        isAliasOverriden = aliasOverriden;
    }

    public int getGroupCreatedBy() {
        return groupCreatedBy;
    }

    public void setGroupCreatedBy(int groupCreatedBy) {
        this.groupCreatedBy = groupCreatedBy;
    }

    public KnXDMSubsAliasDetailsReqDTO getOidcSubscriberDTO() {
        return oidcSubscriberDTO;
    }

    public void setOidcSubscriberDTO(KnXDMSubsAliasDetailsReqDTO oidcSubscriberDTO) {
        this.oidcSubscriberDTO = oidcSubscriberDTO;
    }

    public Map<String, KnOPDispatchDirChgDTO> getMsDtoMap() {
        return msDtoMap;
    }

    public void setMsDtoMap(Map<String, KnOPDispatchDirChgDTO> msDtoMap) {
        this.msDtoMap = msDtoMap;
    }

    public boolean isSubsDeactivated() {
        return subsDeactivated;
    }

    public void setSubsDeactivated(boolean subsDeactivated) {
        this.subsDeactivated = subsDeactivated;
    }

    public int getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(int dispatchType) {
        this.dispatchType = dispatchType;
    }

    public Collection<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Collection<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public Map<String, String> getUpdateTimeMap() {
        return updateTimeMap;
    }

    public void setUpdateTimeMap(Map<String, String> updateTimeMap) {
        this.updateTimeMap = updateTimeMap;
    }

    public int getPublicSubscriptionType() {
        return publicSubscriptionType;
    }

    public void setPublicSubscriptionType(int publicSubscriptionType) {
        this.publicSubscriptionType = publicSubscriptionType;
    }

	public String getActiveFS2() {
		return activeFS2;
	}

	public void setActiveFS2(String activeFS2) {
		this.activeFS2 = activeFS2;
	}

    public Integer getLmrIntropCapable() {
        return lmrIntropCapable;
    }

    public void setLmrIntropCapable(Integer lmrIntropCapable) {
        this.lmrIntropCapable = lmrIntropCapable;
    }

    public Collection<String> getAddedLocWatcherList() {
        return addedLocWatcherList;
    }

    public void setAddedLocWatcherList(Collection<String> addedLocWatcherList) {
        this.addedLocWatcherList = addedLocWatcherList;
    }

    public Collection<String> getRemovedLocWatcherList() {
        return removedLocWatcherList;
    }

    public void setRemovedLocWatcherList(Collection<String> removedLocWatcherList) {
        this.removedLocWatcherList = removedLocWatcherList;
    }

    public int getMcpttCompliance() { return mcpttCompliance; }

    public void setMcpttCompliance(int mcpttCompliance) { this.mcpttCompliance = mcpttCompliance; }

    public String getMcId() { return mcId; }

    public void setMcId(String mcId) { this.mcId = mcId; }

    public String getMcpttId() { return mcpttId; }

    public void setMcpttId(String mcpttId) { this.mcpttId = mcpttId; }

    public String getMcDataId() { return mcDataId; }

    public void setMcDataId(String mcDataId) { this.mcDataId = mcDataId; }

    public String getMcVideoId() { return mcVideoId; }

    public void setMcVideoId(String mcVideoId) { this.mcVideoId = mcVideoId; }

    public String getAliasMdn() { return aliasMdn; }

    public void setAliasMdn(String aliasMdn) { this.aliasMdn = aliasMdn; }

    public String getNetworkName() { return networkName; }

    public void setNetworkName(String networkName) { this.networkName = networkName; }

    public Collection<KnCorpUserProfileDTO> getUserProfileList() { return userProfileList; }

    public void setUserProfileList(Collection<KnCorpUserProfileDTO> userProfileList) { this.userProfileList = userProfileList; }

    public KnCorpUserProfileDTO getUserProfile() { return userProfile; }

    public void setUserProfile(KnCorpUserProfileDTO userProfile) { this.userProfile = userProfile; }

    public int getOnBoardingMailReqd() {
        return onBoardingMailReqd;
    }

    public void setOnBoardingMailReqd(int onBoardingMailReqd) {
        this.onBoardingMailReqd = onBoardingMailReqd;
    }

    public Map<String, Collection<KnDocChangeListDTO>> getProfileMdnEtagMap() {
        return profileMdnEtagMap;
    }

    public void setProfileMdnEtagMap(Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap) {
        this.profileMdnEtagMap = profileMdnEtagMap;
    }

    public Map<String, String> getMcsXcapRootUriMap() {
        return mcsXcapRootUriMap;
    }

    public void setMcsXcapRootUriMap(Map<String, String> mcsXcapRootUriMap) {
        this.mcsXcapRootUriMap = mcsXcapRootUriMap;
    }

    public Map<Integer, Integer> getGrpMemberShipMap() { return grpMemberShipMap; }

    public void setGrpMemberShipMap(Map<Integer, Integer> grpMemberShipMap) { this.grpMemberShipMap = grpMemberShipMap; }

    public Map<Integer, KnCorpGroupContactDTO > getMcxGrpMemberShipMap() {
		return mcxGrpMemberShipMap;
	}

	public void setMcxGrpMemberShipMap(Map<Integer, KnCorpGroupContactDTO > mcxGrpMemberShipMap) {
		this.mcxGrpMemberShipMap = mcxGrpMemberShipMap;
	}
	public String getPocHome() {
		return pocHome;
	}

	public void setPocHome(String pocHome) {
		this.pocHome = pocHome;
	}

    public Map<Integer, KnCorpGroupInfoDTO> getGroupSharedMap() {
        return groupSharedMap;
    }

    public void setGroupSharedMap(Map<Integer, KnCorpGroupInfoDTO> groupSharedMap) {
        this.groupSharedMap = groupSharedMap;
    }

    public Map<Integer, List<Integer>> getGroupIdSharedCorpListMap() {
        return groupIdSharedCorpListMap;
    }

    public void setGroupIdSharedCorpListMap(Map<Integer, List<Integer>> groupIdSharedCorpListMap) {
        this.groupIdSharedCorpListMap = groupIdSharedCorpListMap;
    }
    
    public Map<String, String> getProfileMdnActiveFsMap() {
		return profileMdnActiveFsMap;
	}

	public void setProfileMdnActiveFsMap(Map<String, String> profileMdnActiveFsMap) {
		this.profileMdnActiveFsMap = profileMdnActiveFsMap;
	}

    public String getSubscriberEmailId() { return subscriberEmailId; }

    public void setSubscriberEmailId(String subscriberEmailId) { this.subscriberEmailId = subscriberEmailId; }

    public List<KnAsyncJobDTO> getJobStatus() {   return jobStatus; }

    public void setJobStatus(List<KnAsyncJobDTO> jobStatus) {   this.jobStatus = jobStatus;  }
    
    

    public List<KnOPDirChgDTO> getProfileMdnDirChgDTOs() {
		return profileMdnDirChgDTOs;
	}

	public void setProfileMdnDirChgDTOs(List<KnOPDirChgDTO> profileMdnDirChgDTOs) {
		this.profileMdnDirChgDTOs = profileMdnDirChgDTOs;
	}

    public List<String> getOwnerIDList() { return ownerIDList; }

    public void setOwnerIDList(List<String> ownerIDList) { this.ownerIDList = ownerIDList; }

    public Map<Integer, String> getDelGrpPocHomeMap() { return delGrpPocHomeMap; }

    public void setDelGrpPocHomeMap(Map<Integer, String> delGrpPocHomeMap) { this.delGrpPocHomeMap = delGrpPocHomeMap; }

    public String getTgscMode() {
        return tgscMode;
    }

    public void setTgscMode(String tgscMode) {
        this.tgscMode = tgscMode;
    }
    public String getSharingEnabled() {
        return sharingEnabled;
    }

    public void setSharingEnabled(String sharingEnabled) {
        this.sharingEnabled = sharingEnabled;
    }

    public List<String> getUserProfileSharedCorpList() {
        return userProfileSharedCorpList;
    }

    public void setUserProfileSharedCorpList(List<String> userProfileSharedCorpList) {
        this.userProfileSharedCorpList = userProfileSharedCorpList;
    }

    public String getOwnerCorpId() {
        return ownerCorpId;
    }

    public void setOwnerCorpId(String ownerCorpId) {
        this.ownerCorpId = ownerCorpId;
    }

    public String getOwnerExtCorpId() {
        return ownerExtCorpId;
    }

    public void setOwnerExtCorpId(String ownerExtCorpId) {
        this.ownerExtCorpId = ownerExtCorpId;
    }

    public Boolean getMdnAuthorized() { return isMdnAuthorized; }

    public void setMdnAuthorized(Boolean mdnAuthorized) { isMdnAuthorized = mdnAuthorized; }

    public Collection<String> getOnlineMdnList() {
        return onlineMdnList;
    }

    public void setOnlineMdnList(Collection<String> onlineMdnList) {
        this.onlineMdnList = onlineMdnList;
    }

    public List<String> getUnAuthorizedGroupURIList() {
        return unAuthorizedGroupURIList;
    }

    public void setUnAuthorizedGroupURIList(List<String> unAuthorizedGroupURIList) {
        this.unAuthorizedGroupURIList = unAuthorizedGroupURIList;
    }

    public String getGrpSIPUri() {
        return grpSIPUri;
    }

    public void setGrpSIPUri(String grpSIPUri) {
        this.grpSIPUri = grpSIPUri;
    }

    public String getGrpUri() {
        return grpUri;
    }

    public void setGrpUri(String grpUri) {
        this.grpUri = grpUri;
    }

    public String getRecordingFs() {
        return recordingFs;
    }

    public void setRecordingFs(String recordingFs) {
        this.recordingFs = recordingFs;
    }

    public Integer getOldLmrInteropFlag() {
        return oldLmrInteropFlag;
    }

    public void setOldLmrInteropFlag(Integer oldLmrInteropFlag) {
        this.oldLmrInteropFlag = oldLmrInteropFlag;
    }

    public Integer getLmrInteropFlag() {
        return lmrInteropFlag;
    }

    public void setLmrInteropFlag(Integer lmrInteropFlag) {
        this.lmrInteropFlag = lmrInteropFlag;
    }

    public List<String> getActualDeletedBroadcaster() {
        return actualDeletedBroadcaster;
    }

    public void setActualDeletedBroadcaster(List<String> actualDeletedBroadcaster) {
        this.actualDeletedBroadcaster = actualDeletedBroadcaster;
    }

    public boolean isCorpNotification() {
        return corpNotification;
    }

    public void setCorpNotification(boolean corpNotification) {
        this.corpNotification = corpNotification;
    }

    public int getIntCorpId() {
        return intCorpId;
    }

    public void setIntCorpId(int intCorpId) {
        this.intCorpId = intCorpId;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public Map<Integer, List<String>> getDisabledDispatchMemListMap() {
        return disabledDispatchMemListMap;
    }

    public void setDisabledDispatchMemListMap(Map<Integer, List<String>> disabledDispatchMemListMap) {
        this.disabledDispatchMemListMap = disabledDispatchMemListMap;
    }

    public boolean isEmergencyAttributesChanged() {
        return isEmergencyAttributesChanged;
    }

    public void setEmergencyAttributesChanged(boolean emergencyAttributesChanged) {
        isEmergencyAttributesChanged = emergencyAttributesChanged;
    }

    @Override
    public String toString() {
        return "KnCorpResponseDTO{" +
                "status=" + status +
                ", statusCode='" + statusCode + '\'' +
                ", message='" + message + '\'' +
                ", failedDataList=" + failedDataList +
                ", changeLogMap=" + KnGDPRTemplate.mapKeyMdn(changeLogMap) +
                ", etag='" + etag + '\'' +
                ", disabledDispatchMemList=" + disabledDispatchMemList +
                ", enabledDispatchMemList=" + enabledDispatchMemList +
                ", pairedContactListId=" + pairedContactListId +
                ", isSublistExists=" + isSublistExists +
                ", tgsModeChgMap=" +KnGDPRTemplate.mapKeyMdn(tgsModeChgMap) +
                ", liEventList=" + liEventList +
                ", isSyncDisabled=" + isSyncDisabled +
                ", activeFS=" + activeFS +
                ", mdn=" + KnGDPRTemplate.mdn(mdn) +
                ", peg=" + peg +
                ", addedGroupMember=" + addedGroupMember +
                ", addedGroupDistributionMember=" + addedGroupDistributionMember +
                ", mdnCorpId=" + mdnCorpId +
                ", dirChgDTOs=" + dirChgDTOs +
                ", failureDetails=" + failureDetails +
                ", isProfileChanged=" + isProfileChanged +
                ", responseMap=" + responseMap +
                ", idmSubscriberDTO=" + idmSubscriberDTO +
                ", isUserIdChanged=" + isUserIdChanged +
                ", isUserOverriden=" + isUserOverriden +
                ", mdnList=" + KnGDPRTemplate.mdnList(mdnList) +
                ", deletedGroupId=" + deletedGroupId +
                ", isAliasMdnChanged=" + isAliasMdnChanged +
                ", isAliasOverriden=" + isAliasOverriden +
                ", oidcSubscriberDTO=" + oidcSubscriberDTO +
                ", msDtoMap=" + msDtoMap +
                ", subsDeactivated=" + subsDeactivated +
                ", dispatchType=" + dispatchType +
                ", groupIds=" + groupIds +
                ", updateTimeMap=" + updateTimeMap +
                ", publicSubscriptionType=" + publicSubscriptionType +
				", activeFS2=" + activeFS2 +
                ", lmrIntropCapable=" + lmrIntropCapable +
                ", addedLocWatcherList=" + addedLocWatcherList +
                ", removedLocWatcherList=" + removedLocWatcherList +
                ", mcpttCompliance="+mcpttCompliance +
                ", mcId="+mcId +
                ", mcpttId="+KnGDPRTemplate.mcpttId(mcpttId) +
                ", mcDataId="+KnGDPRTemplate.mcdataId(mcDataId) +
                ", mcVideoId="+KnGDPRTemplate.mcvideoId(mcVideoId) +
                ", aliasMdn="+KnGDPRTemplate.mdn(aliasMdn) +
                ", onBoardingMailReqd="+onBoardingMailReqd +
                ", profileMdnEtagMap="+profileMdnEtagMap +
                ", mcsXcapRootUriMap="+mcsXcapRootUriMap +
                ", grpMemberShipMap="+grpMemberShipMap +
                ", mcxGrpMemberShipMap="+mcxGrpMemberShipMap +
                 ", pocHome="+pocHome +
                ", groupSharedMap="+groupSharedMap +
                ", groupIdSharedCorpListMap="+groupIdSharedCorpListMap +
				", profileMdnActiveFsMap="+profileMdnActiveFsMap +
                ", userProfile="+userProfile +
                ", subscriberEmailId="+subscriberEmailId +
                ", jobStatus="+jobStatus +
                ", profileMdnDirChgDTOs="+profileMdnDirChgDTOs +
                ", ownerIDList="+ownerIDList +
                ", delGrpPocHomeMap="+delGrpPocHomeMap +
                ", tgscMode="+tgscMode +
                ", sharingEnabled="+sharingEnabled +
                ", userProfileSharedCorpList="+userProfileSharedCorpList +
                ", ownerCorpId="+ownerCorpId +
                ", ownerExtCorpId="+ownerExtCorpId +
                ", isMdnAuthorized="+isMdnAuthorized +
                ", addedLocWatcherList="+addedLocWatcherList +
                ", removedLocWatcherList="+removedLocWatcherList +
                ", onlineMdnList="+KnGDPRTemplate.mdnList(onlineMdnList) +
                ", addedLocWatcherList="+addedLocWatcherList +
                ", removedLocWatcherList="+removedLocWatcherList +
                ", unAuthorizedGroupURIList=" + unAuthorizedGroupURIList +
                ", grpUri=" + grpUri +
                ", oldLmrInteropFlag=" + oldLmrInteropFlag +
                ", grpSIPUri=" + grpSIPUri +
                ", recordingFs=" + recordingFs +
                ", lmrInteropFlag=" + lmrInteropFlag +
                ", billingMDN=" + billingMDN +
                ",actualDeletedBroadcaster=" + actualDeletedBroadcaster +
                ",corpNotification=" + corpNotification +
                ",intCorpId=" + intCorpId +
                ",extCorpId=" + extCorpId +
                ",networkName=" + networkName +
                ",isDispacherPresent=" + isDispacherPresent +
                ",profileMDNs=" + profileMDNs +
                ",isLocationDisabled=" + isLocationDisabled +
                ",locwatcherCount=" + locwatcherCount +
                ",userProfileCount=" + userProfileCount +
                ",isEmergencyAttributesChanged=" + isEmergencyAttributesChanged +
                ",upmCount=" + upmCount +
                ",disabledDispatchMemListMap=" + disabledDispatchMemListMap +
                '}';
    }
}