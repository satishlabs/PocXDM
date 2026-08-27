/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.dto.IIdentifier;

import java.util.List;

/**
 * *****************************************************************************
 * File name:   KnCorpProfileDTO.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Sudhendu Nayak       26/07/2022       13.1
 * <p/>
 * <p/>
 * Bangalore - 560 001
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * *******************************************************************************
 */

public class KnXDMCorpProfileDTO implements IIdentifier {
    private static final long serialVersionUID = 4423129073570786042L;

    private String accountId;
    private String corporateName;
    private String maxSubscribers;
    private String maxCorpLists;
    private String maxMemPerCorpList;
    private String maxCorpGroups;
    private String maxMemPerCorpGroup;
    private String maxContactsPerSubs;
    private String maxExtContactsPerCorp;
    private String maxExtSubsPerCorp;
    private String maxDispatchGroups;
    private String maxMemPerDispatchGroup;
    private String supervisoryOverrideEnabled;
    private String dispatchEnabled;
    private String maxContactsPerRequest;
    private String requestThresold;
    private String isHierarchyEnabled;
    private String isStandalone;
    private String isInterOpEnabled;
    private List<KnXDMDialPlanInfo> dialPlanList;
    private String isTalkGroupEnabled;
    private String maxScanListSize;
    private String priorityRange;
    private String isBCGrpEnabled;
    private String maxMemPerBCGrp;
    private String isGWEnabled;
    private String pocSysId;
    private String allowSUContact;
    private String maxDispatcherPerDispGrp;
    private String maxSGPerGrp;
    private String configFeatureSet;
    private String pttRadioScanListSize;
    private String pttRadioChannelListSize;
    private String pttRadioDefScanMode;
    private String maxLocWatcherPerGrp;
    private String maxSGPatchPerGrp;
    private String addExtContLimit;
    private String maxChannelAllowed;
    private String maxZoneAllowed;
    private String maxChannelsPerZone;
    private String maxLgrGrp;
    private String maxLgrBCGrp;
    private String maxMemPerLrgGrp;
    private String maxMemPerLrgBCGrp;
    private String maxStatusMsgPerOsmList;
    private String maxStatusShortTextLength;
    private String maxStatusMsgLength;
    private String updateCorpAdminFsLimit;
    private String maxGrpProfiles;
    private String maxUserProfiles;
    private String maxMemNonAPSubList;
    private String maxAssignProfiles;
    private String maxCommonContactLisSize;
    private String maxCommonContactlistPerSub;
    private String maxHierarchyLevels;
    private String largeAgencyDispatchFeature;
    private Boolean locationEnabled;

    @Override
    public String getObjectId() {
        return null;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public String getMaxSubscribers() {
        return maxSubscribers;
    }

    public void setMaxSubscribers(String maxSubscribers) {
        this.maxSubscribers = maxSubscribers;
    }

    public String getMaxCorpLists() {
        return maxCorpLists;
    }

    public void setMaxCorpLists(String maxCorpLists) {
        this.maxCorpLists = maxCorpLists;
    }

    public String getMaxMemPerCorpList() {
        return maxMemPerCorpList;
    }

    public void setMaxMemPerCorpList(String maxMemPerCorpList) {
        this.maxMemPerCorpList = maxMemPerCorpList;
    }

    public String getMaxCorpGroups() {
        return maxCorpGroups;
    }

    public void setMaxCorpGroups(String maxCorpGroups) {
        this.maxCorpGroups = maxCorpGroups;
    }

    public String getMaxMemPerCorpGroup() {
        return maxMemPerCorpGroup;
    }

    public void setMaxMemPerCorpGroup(String maxMemPerCorpGroup) {
        this.maxMemPerCorpGroup = maxMemPerCorpGroup;
    }

    public String getMaxContactsPerSubs() {
        return maxContactsPerSubs;
    }

    public void setMaxContactsPerSubs(String maxContactsPerSubs) {
        this.maxContactsPerSubs = maxContactsPerSubs;
    }

    public String getMaxExtContactsPerCorp() {
        return maxExtContactsPerCorp;
    }

    public void setMaxExtContactsPerCorp(String maxExtContactsPerCorp) {
        this.maxExtContactsPerCorp = maxExtContactsPerCorp;
    }

    public String getMaxExtSubsPerCorp() {
        return maxExtSubsPerCorp;
    }

    public void setMaxExtSubsPerCorp(String maxExtSubsPerCorp) {
        this.maxExtSubsPerCorp = maxExtSubsPerCorp;
    }

    public String getMaxDispatchGroups() {
        return maxDispatchGroups;
    }

    public void setMaxDispatchGroups(String maxDispatchGroups) {
        this.maxDispatchGroups = maxDispatchGroups;
    }

    public String getMaxMemPerDispatchGroup() {
        return maxMemPerDispatchGroup;
    }

    public void setMaxMemPerDispatchGroup(String maxMemPerDispatchGroup) {
        this.maxMemPerDispatchGroup = maxMemPerDispatchGroup;
    }

    public String getSupervisoryOverrideEnabled() {
        return supervisoryOverrideEnabled;
    }

    public void setSupervisoryOverrideEnabled(String supervisoryOverrideEnabled) {
        this.supervisoryOverrideEnabled = supervisoryOverrideEnabled;
    }

    public String getDispatchEnabled() {
        return dispatchEnabled;
    }

    public void setDispatchEnabled(String dispatchEnabled) {
        this.dispatchEnabled = dispatchEnabled;
    }

    public String getMaxContactsPerRequest() {
        return maxContactsPerRequest;
    }

    public void setMaxContactsPerRequest(String maxContactsPerRequest) {
        this.maxContactsPerRequest = maxContactsPerRequest;
    }

    public String getRequestThresold() {
        return requestThresold;
    }

    public void setRequestThresold(String requestThresold) {
        this.requestThresold = requestThresold;
    }

    public String getIsHierarchyEnabled() {
        return isHierarchyEnabled;
    }

    public void setIsHierarchyEnabled(String isHierarchyEnabled) {
        this.isHierarchyEnabled = isHierarchyEnabled;
    }

    public String getIsStandalone() {
        return isStandalone;
    }

    public void setIsStandalone(String isStandalone) {
        this.isStandalone = isStandalone;
    }

    public String getIsInterOpEnabled() {
        return isInterOpEnabled;
    }

    public void setIsInterOpEnabled(String isInterOpEnabled) {
        this.isInterOpEnabled = isInterOpEnabled;
    }

    public List<KnXDMDialPlanInfo> getDialPlanList() {
        return dialPlanList;
    }

    public void setDialPlanList(List<KnXDMDialPlanInfo> dialPlanList) {
        this.dialPlanList = dialPlanList;
    }

    public String getIsTalkGroupEnabled() {
        return isTalkGroupEnabled;
    }

    public void setIsTalkGroupEnabled(String isTalkGroupEnabled) {
        this.isTalkGroupEnabled = isTalkGroupEnabled;
    }

    public String getMaxScanListSize() {
        return maxScanListSize;
    }

    public void setMaxScanListSize(String maxScanListSize) {
        this.maxScanListSize = maxScanListSize;
    }

    public String getPriorityRange() {
        return priorityRange;
    }

    public void setPriorityRange(String priorityRange) {
        this.priorityRange = priorityRange;
    }

    public String getIsBCGrpEnabled() {
        return isBCGrpEnabled;
    }

    public void setIsBCGrpEnabled(String isBCGrpEnabled) {
        this.isBCGrpEnabled = isBCGrpEnabled;
    }

    public String getMaxMemPerBCGrp() {
        return maxMemPerBCGrp;
    }

    public void setMaxMemPerBCGrp(String maxMemPerBCGrp) {
        this.maxMemPerBCGrp = maxMemPerBCGrp;
    }

    public String getIsGWEnabled() {
        return isGWEnabled;
    }

    public void setIsGWEnabled(String isGWEnabled) {
        this.isGWEnabled = isGWEnabled;
    }

    public String getPocSysId() {
        return pocSysId;
    }

    public void setPocSysId(String pocSysId) {
        this.pocSysId = pocSysId;
    }

    public String getAllowSUContact() {
        return allowSUContact;
    }

    public void setAllowSUContact(String allowSUContact) {
        this.allowSUContact = allowSUContact;
    }

    public String getMaxDispatcherPerDispGrp() {
        return maxDispatcherPerDispGrp;
    }

    public void setMaxDispatcherPerDispGrp(String maxDispatcherPerDispGrp) {
        this.maxDispatcherPerDispGrp = maxDispatcherPerDispGrp;
    }

    public String getMaxSGPerGrp() {
        return maxSGPerGrp;
    }

    public void setMaxSGPerGrp(String maxSGPerGrp) {
        this.maxSGPerGrp = maxSGPerGrp;
    }

    public String getConfigFeatureSet() {
        return configFeatureSet;
    }

    public void setConfigFeatureSet(String configFeatureSet) {
        this.configFeatureSet = configFeatureSet;
    }

    public String getPttRadioScanListSize() {
        return pttRadioScanListSize;
    }

    public void setPttRadioScanListSize(String pttRadioScanListSize) {
        this.pttRadioScanListSize = pttRadioScanListSize;
    }

    public String getPttRadioChannelListSize() {
        return pttRadioChannelListSize;
    }

    public void setPttRadioChannelListSize(String pttRadioChannelListSize) {
        this.pttRadioChannelListSize = pttRadioChannelListSize;
    }

    public String getPttRadioDefScanMode() {
        return pttRadioDefScanMode;
    }

    public void setPttRadioDefScanMode(String pttRadioDefScanMode) {
        this.pttRadioDefScanMode = pttRadioDefScanMode;
    }

    public String getMaxLocWatcherPerGrp() {
        return maxLocWatcherPerGrp;
    }

    public void setMaxLocWatcherPerGrp(String maxLocWatcherPerGrp) {
        this.maxLocWatcherPerGrp = maxLocWatcherPerGrp;
    }

    public String getMaxSGPatchPerGrp() {
        return maxSGPatchPerGrp;
    }

    public void setMaxSGPatchPerGrp(String maxSGPatchPerGrp) {
        this.maxSGPatchPerGrp = maxSGPatchPerGrp;
    }

    public String getAddExtContLimit() {
        return addExtContLimit;
    }

    public void setAddExtContLimit(String addExtContLimit) {
        this.addExtContLimit = addExtContLimit;
    }

    public String getMaxChannelAllowed() {
        return maxChannelAllowed;
    }

    public void setMaxChannelAllowed(String maxChannelAllowed) {
        this.maxChannelAllowed = maxChannelAllowed;
    }

    public String getMaxZoneAllowed() {
        return maxZoneAllowed;
    }

    public void setMaxZoneAllowed(String maxZoneAllowed) {
        this.maxZoneAllowed = maxZoneAllowed;
    }

    public String getMaxChannelsPerZone() {
        return maxChannelsPerZone;
    }

    public void setMaxChannelsPerZone(String maxChannelsPerZone) {
        this.maxChannelsPerZone = maxChannelsPerZone;
    }

    public String getMaxLgrGrp() {
        return maxLgrGrp;
    }

    public void setMaxLgrGrp(String maxLgrGrp) {
        this.maxLgrGrp = maxLgrGrp;
    }

    public String getMaxLgrBCGrp() {
        return maxLgrBCGrp;
    }

    public void setMaxLgrBCGrp(String maxLgrBCGrp) {
        this.maxLgrBCGrp = maxLgrBCGrp;
    }

    public String getMaxMemPerLrgGrp() {
        return maxMemPerLrgGrp;
    }

    public void setMaxMemPerLrgGrp(String maxMemPerLrgGrp) {
        this.maxMemPerLrgGrp = maxMemPerLrgGrp;
    }

    public String getMaxMemPerLrgBCGrp() {
        return maxMemPerLrgBCGrp;
    }

    public void setMaxMemPerLrgBCGrp(String maxMemPerLrgBCGrp) {
        this.maxMemPerLrgBCGrp = maxMemPerLrgBCGrp;
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

    public String getUpdateCorpAdminFsLimit() {
        return updateCorpAdminFsLimit;
    }

    public void setUpdateCorpAdminFsLimit(String updateCorpAdminFsLimit) {
        this.updateCorpAdminFsLimit = updateCorpAdminFsLimit;
    }

    public String getMaxGrpProfiles() {
        return maxGrpProfiles;
    }

    public void setMaxGrpProfiles(String maxGrpProfiles) {
        this.maxGrpProfiles = maxGrpProfiles;
    }

    public String getMaxUserProfiles() {
        return maxUserProfiles;
    }

    public void setMaxUserProfiles(String maxUserProfiles) {
        this.maxUserProfiles = maxUserProfiles;
    }

    public String getMaxMemNonAPSubList() {
        return maxMemNonAPSubList;
    }

    public void setMaxMemNonAPSubList(String maxMemNonAPSubList) {
        this.maxMemNonAPSubList = maxMemNonAPSubList;
    }

    public String getMaxAssignProfiles() {
        return maxAssignProfiles;
    }

    public void setMaxAssignProfiles(String maxAssignProfiles) {
        this.maxAssignProfiles = maxAssignProfiles;
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
    public String getMaxHierarchyLevels() {
        return maxHierarchyLevels;
    }
    public void setMaxHierarchyLevels(String maxHierarchyLevels) {
        this.maxHierarchyLevels = maxHierarchyLevels;
    }

    public String getLargeAgencyDispatchFeature() {
        return largeAgencyDispatchFeature;
    }

    public void setLargeAgencyDispatchFeature(String largeAgencyDispatchFeature) {
        this.largeAgencyDispatchFeature = largeAgencyDispatchFeature;
    }
    public Boolean isLocationEnabled() {
        return locationEnabled;
    }

    public void setLocationEnabled(Boolean locationEnabled) {
        this.locationEnabled = locationEnabled;
    }

    @Override
    public String toString() {
        return "KnCorpProfileDTO{" +
                "maxSubscribers='" + maxSubscribers + '\'' +
                ", accountId='" + accountId + '\'' +
                ", corporateName='" + corporateName + '\'' +
                ", maxCorpLists='" + maxCorpLists + '\'' +
                ", maxMemPerCorpList='" + maxMemPerCorpList + '\'' +
                ", maxCorpGroups='" + maxCorpGroups + '\'' +
                ", maxMemPerCorpGroup='" + maxMemPerCorpGroup + '\'' +
                ", maxContactsPerSubs='" + maxContactsPerSubs + '\'' +
                ", maxExtContactsPerCorp='" + maxExtContactsPerCorp + '\'' +
                ", maxExtSubsPerCorp='" + maxExtSubsPerCorp + '\'' +
                ", maxDispatchGroups='" + maxDispatchGroups + '\'' +
                ", maxMemPerDispatchGroup='" + maxMemPerDispatchGroup + '\'' +
                ", supervisoryOverrideEnabled='" + supervisoryOverrideEnabled + '\'' +
                ", dispatchEnabled='" + dispatchEnabled + '\'' +
                ", maxContactsPerRequest='" + maxContactsPerRequest + '\'' +
                ", requestThresold='" + requestThresold + '\'' +
                ", isHierarchyEnabled='" + isHierarchyEnabled + '\'' +
                ", isStandalone='" + isStandalone + '\'' +
                ", isInterOpEnabled='" + isInterOpEnabled + '\'' +
                ", dialPlanList=" + dialPlanList +
                ", isTalkGroupEnabled='" + isTalkGroupEnabled + '\'' +
                ", maxScanListSize='" + maxScanListSize + '\'' +
                ", priorityRange='" + priorityRange + '\'' +
                ", isBCGrpEnabled='" + isBCGrpEnabled + '\'' +
                ", maxMemPerBCGrp='" + maxMemPerBCGrp + '\'' +
                ", isGWEnabled='" + isGWEnabled + '\'' +
                ", pocSysId='" + pocSysId + '\'' +
                ", allowSUContact='" + allowSUContact + '\'' +
                ", maxDispatcherPerDispGrp='" + maxDispatcherPerDispGrp + '\'' +
                ", maxSGPerGrp='" + maxSGPerGrp + '\'' +
                ", configFeatureSet='" + configFeatureSet + '\'' +
                ", pttRadioScanListSize='" + pttRadioScanListSize + '\'' +
                ", pttRadioChannelListSize='" + pttRadioChannelListSize + '\'' +
                ", pttRadioDefScanMode='" + pttRadioDefScanMode + '\'' +
                ", maxLocWatcherPerGrp='" + maxLocWatcherPerGrp + '\'' +
                ", maxSGPatchPerGrp='" + maxSGPatchPerGrp + '\'' +
                ", addExtContLimit='" + addExtContLimit + '\'' +
                ", maxChannelAllowed='" + maxChannelAllowed + '\'' +
                ", maxZoneAllowed='" + maxZoneAllowed + '\'' +
                ", maxChannelsPerZone='" + maxChannelsPerZone + '\'' +
                ", maxLgrGrp='" + maxLgrGrp + '\'' +
                ", maxLgrBCGrp='" + maxLgrBCGrp + '\'' +
                ", maxMemPerLrgGrp='" + maxMemPerLrgGrp + '\'' +
                ", maxMemPerLrgBCGrp='" + maxMemPerLrgBCGrp + '\'' +
                ", maxStatusMsgPerOsmList='" + maxStatusMsgPerOsmList + '\'' +
                ", maxStatusShortTextLength='" + maxStatusShortTextLength + '\'' +
                ", maxStatusMsgLength='" + maxStatusMsgLength + '\'' +
                ", updateCorpAdminFsLimit='" + updateCorpAdminFsLimit + '\'' +
                ", maxGrpProfiles='" + maxGrpProfiles + '\'' +
                ", maxUserProfiles='" + maxUserProfiles + '\'' +
                ", maxMemNonAPSubList='" + maxMemNonAPSubList + '\'' +
                ", maxAssignProfiles='" + maxAssignProfiles + '\'' +
                ", maxCommonContactLisSize='" + maxCommonContactLisSize + '\'' +
                ", maxCommonContactlistPerSub='" + maxCommonContactlistPerSub + '\'' +
                ", maxHierarchyLevels='" + maxHierarchyLevels + '\'' +
                ", largeAgencyDispatchFeature='" + largeAgencyDispatchFeature + '\'' +
                ", locationEnabled='" + locationEnabled + '\'' +
                '}';
    }
}
