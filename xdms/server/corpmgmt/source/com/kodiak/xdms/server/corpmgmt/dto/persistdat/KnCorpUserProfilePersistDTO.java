/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpModifyUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSharedCorpInfo;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;

import java.util.*;

public class KnCorpUserProfilePersistDTO implements IPersistenceDTO {
    private static final long serialVersionUID = -1866685130022287779L;

    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO persistenceDTO;

    private String corpId;
    private String fetchSize;
    private String startIndex;
    private KnCorpUserProfileDTO userProfileDTO;
    private KnCorpModifyUserProfileDTO modifyUserProfileDTO;

    private String maxUserProfile;
    private String dbMaxUserProfile;
    private String dbProfileName;
    private String reqProfileName;
    private Integer autoAssign;
    Collection<KnCorpGroupInfoPersistDTO> corpGroupList;
    private String userProfileId;
    private KnCorpUserProfileDTO dbUserProfileDTO;
    private String maxUserProfilePerSub;
    private List<String> dbProfileMdnList;
    private boolean userProfileMgmtBit;
    private Collection<String> assignedMdnList;
    private List<Integer> jobStatusList;
    private boolean mcpttCompliance;
    private int mcxGroupCount;
    private boolean skipValidation;
    private int maxZone;
    private int maxPttRadioScanSize;
    private int maxChannelsPerZone;
    private int maxPriority;
    private Map<Integer,List<KnCorpSharedCorpInfo>> sharedCorpGrpInfoMap;
    private Map<Integer, KnCorpTrustMatrixDTO> trustMatrixMap;
    private Map<String, BitSet> externalPemMap;
    private Map<String,Integer> subslistMemCorpInfo;
    private boolean sharedHierarchy;
    private int clientType;
    private String dbUPMSharingFlag;
    private String reqUPMSharingFlag;
    private List<String> reqUserProfileSharedCorpList;
    private List<String> dbUserProfileSharedCorpList;
    private List<Integer> isPreConfiguredGroupList;
    private boolean upmShareEnabled;
    private boolean sharedUpmForMdnCorp;
    private boolean onlyCorpSubs;
    private boolean sharedUpm;
    private boolean assignUPMAllowed;
    private boolean unassignUPMAllowed;
    private List<Integer> sharedCorpListId;
    private boolean isOwnerCorpReq;
    private List<String> addedUpmSharedCorpList;
    private List<String> removedUpmSharedCorpList;
    private int commonContactListRejectionId;
    private List<Integer> largeGroupIds;
    private boolean systemLevelLargeGroupLimitReached;
    private int maxGroupsPerMemberCount;
    private int groupListSize;
    private boolean sysSelfDndPrivilege;
    private boolean corpSelfDndPrivilege;
    private int reqSelfDndPrivilege;
    private boolean maxLocWachersPerGroupReached;
    private boolean primaryDestinationIsEmpty;
    private boolean destIsBroadCastGroup;
    private boolean groupDoesNotBelogsToUPM;
    private boolean sublistDoesNotBelogsToUPM;
    private boolean maxMembersAllowedPerGroupIsReached;
    private boolean sysLargeAgencyDispatch;
    private boolean corpLargeAgencyDispatch;
    private int reqLargeAgencyDispatch;
    private int groupType;

    @Override
    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    @Override
    public IInputDTO getInputDTO() {
        return inputDTO;
    }


    @Override
    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    @Override
    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    @Override
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    @Override
    public String getOperationType() {
        return operationType;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public String getObjectId() {
        return corpId;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public KnCorpUserProfileDTO getUserProfileDTO() {
        return userProfileDTO;
    }

    public void setUserProfileDTO(KnCorpUserProfileDTO userProfileDTO) {
        this.userProfileDTO = userProfileDTO;
    }

    public String getMaxUserProfile() {
        return maxUserProfile;
    }

    public void setMaxUserProfile(String maxUserProfile) {
        this.maxUserProfile = maxUserProfile;
    }

    public String getDbMaxUserProfile() {
        return dbMaxUserProfile;
    }

    public void setDbMaxUserProfile(String dbMaxUserProfile) {
        this.dbMaxUserProfile = dbMaxUserProfile;
    }

    public String getDbProfileName() {
        return dbProfileName;
    }

    public void setDbProfileName(String dbProfileName) {
        this.dbProfileName = dbProfileName;
    }

    public String getReqProfileName() {
        return reqProfileName;
    }

    public void setReqProfileName(String reqProfileName) {
        this.reqProfileName = reqProfileName;
    }

    public String getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(String fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(String startIndex) {
        this.startIndex = startIndex;
    }

    public Collection<KnCorpGroupInfoPersistDTO> getCorpGroupList() {
        return corpGroupList;
    }

    public void setCorpGroupList(Collection<KnCorpGroupInfoPersistDTO> corpGroupList) {
        this.corpGroupList = corpGroupList;
    }

    public Integer getAutoAssign() {
        return autoAssign;
    }

    public void setAutoAssign(Integer autoAssign) {
        this.autoAssign = autoAssign;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public KnCorpUserProfileDTO getDbUserProfileDTO() {
        return dbUserProfileDTO;
    }

    public void setDbUserProfileDTO(KnCorpUserProfileDTO dbUserProfileDTO) {
        this.dbUserProfileDTO = dbUserProfileDTO;
    }

    public String getMaxUserProfilePerSub() {
        return maxUserProfilePerSub;
    }

    public void setMaxUserProfilePerSub(String maxUserProfilePerSub) {
        this.maxUserProfilePerSub = maxUserProfilePerSub;
    }

    public boolean isUserProfileMgmtBit() {
        return userProfileMgmtBit;
    }

    public void setUserProfileMgmtBit(boolean userProfileMgmtBit) {
        this.userProfileMgmtBit = userProfileMgmtBit;
    }

    public List<String> getDbProfileMdnList() {
        return dbProfileMdnList;
    }

    public void setDbProfileMdnList(List<String> dbProfileMdnList) {
        this.dbProfileMdnList = dbProfileMdnList;
    }

    public Collection<String> getAssignedMdnList() {
        return assignedMdnList;
    }

    public void setAssignedMdnList(Collection<String> assignedMdnList) {
        this.assignedMdnList = assignedMdnList;
    }

    public List<Integer> getJobStatusList() {
        return jobStatusList;
    }

    public void setJobStatusList(List<Integer> jobStatusList) {
        this.jobStatusList = jobStatusList;
    }

    public KnCorpModifyUserProfileDTO getModifyUserProfileDTO() {
        return modifyUserProfileDTO;
    }

    public void setModifyUserProfileDTO(KnCorpModifyUserProfileDTO modifyUserProfileDTO) {
        this.modifyUserProfileDTO = modifyUserProfileDTO;
    }

    public boolean isMcpttCompliance() {
        return mcpttCompliance;
    }

    public void setMcpttCompliance(boolean mcpttCompliance) {
        this.mcpttCompliance = mcpttCompliance;
    }

    public int getMcxGroupCount() {
        return mcxGroupCount;
    }

    public void setMcxGroupCount(int mcxGroupCount) {
        this.mcxGroupCount = mcxGroupCount;
    }

    public boolean isSkipValidation() { return skipValidation; }

    public void setSkipValidation(boolean skipValidation) { this.skipValidation = skipValidation; }

    public int getMaxZone() {
        return maxZone;
    }

    public void setMaxZone(int maxZone) {
        this.maxZone = maxZone;
    }

    public int getMaxPttRadioScanSize() {
        return maxPttRadioScanSize;
    }

    public void setMaxPttRadioScanSize(int maxPttRadioScanSize) {
        this.maxPttRadioScanSize = maxPttRadioScanSize;
    }

    public int getMaxChannelsPerZone() {
        return maxChannelsPerZone;
    }

    public void setMaxChannelsPerZone(int maxChannelsPerZone) {
        this.maxChannelsPerZone = maxChannelsPerZone;
    }

    public int getMaxPriority() {
        return maxPriority;
    }

    public void setMaxPriority(int maxPriority) {
        this.maxPriority = maxPriority;
    }

    public Map<Integer, List<KnCorpSharedCorpInfo>> getSharedCorpGrpInfoMap() {
        return sharedCorpGrpInfoMap;
    }

    public void setSharedCorpGrpInfoMap(Map<Integer, List<KnCorpSharedCorpInfo>> sharedCorpGrpInfoMap) {
        this.sharedCorpGrpInfoMap = sharedCorpGrpInfoMap;
    }

    public Map<Integer, KnCorpTrustMatrixDTO> getTrustMatrixMap() {
        return trustMatrixMap;
    }

    public void setTrustMatrixMap(Map<Integer, KnCorpTrustMatrixDTO> trustMatrixMap) {
        this.trustMatrixMap = trustMatrixMap;
    }

    public Map<String, BitSet> getExternalPemMap() { return externalPemMap; }

    public void setExternalPemMap(Map<String, BitSet> externalPemMap) { this.externalPemMap = externalPemMap; }

    public Map<String, Integer> getSubslistMemCorpInfo() { return subslistMemCorpInfo; }

    public void setSubslistMemCorpInfo(Map<String, Integer> subslistMemCorpInfo) { this.subslistMemCorpInfo = subslistMemCorpInfo; }

    public boolean isSharedHierarchy() { return sharedHierarchy; }

    public void setSharedHierarchy(boolean sharedHierarchy) { this.sharedHierarchy = sharedHierarchy; }

    public int getClientType() { return clientType; }

    public void setClientType(int clientType) { this.clientType = clientType; }

    public String getDbUPMSharingFlag() {
        return dbUPMSharingFlag;
    }

    public void setDbUPMSharingFlag(String dbUPMSharingFlag) {
        this.dbUPMSharingFlag = dbUPMSharingFlag;
    }

    public String getReqUPMSharingFlag() {
        return reqUPMSharingFlag;
    }

    public void setReqUPMSharingFlag(String reqUPMSharingFlag) {
        this.reqUPMSharingFlag = reqUPMSharingFlag;
    }

    public List<String> getReqUserProfileSharedCorpList() {
        return reqUserProfileSharedCorpList;
    }

    public void setReqUserProfileSharedCorpList(List<String> reqUserProfileSharedCorpList) {
        this.reqUserProfileSharedCorpList = reqUserProfileSharedCorpList;
    }

    public List<String> getDbUserProfileSharedCorpList() {
        return dbUserProfileSharedCorpList;
    }

    public void setDbUserProfileSharedCorpList(List<String> dbUserProfileSharedCorpList) {
        this.dbUserProfileSharedCorpList = dbUserProfileSharedCorpList;
    }

    public List<Integer> getIsPreConfiguredGroupList() {
        return isPreConfiguredGroupList;
    }

    public void setIsPreConfiguredGroupList(List<Integer> isPreConfiguredGroupList) {
        this.isPreConfiguredGroupList = isPreConfiguredGroupList;
    }

    public boolean isUpmShareEnabled() {
        return upmShareEnabled;
    }

    public void setUpmShareEnabled(boolean upmShareEnabled) {
        this.upmShareEnabled = upmShareEnabled;
    }

    public boolean isSharedUpmForMdnCorp() {
        return sharedUpmForMdnCorp;
    }

    public void setSharedUpmForMdnCorp(boolean sharedUpmForMdnCorp) {
        this.sharedUpmForMdnCorp = sharedUpmForMdnCorp;
    }

    public boolean isOnlyCorpSubs() {
        return onlyCorpSubs;
    }

    public void setOnlyCorpSubs(boolean onlyCorpSubs) {
        this.onlyCorpSubs = onlyCorpSubs;
    }

    public boolean isSharedUpm() {
        return sharedUpm;
    }

    public void setSharedUpm(boolean sharedUpm) {
        this.sharedUpm = sharedUpm;
    }

    public boolean isAssignUPMAllowed() {
        return assignUPMAllowed;
    }

    public void setAssignUPMAllowed(boolean assignUPMAllowed) {
        this.assignUPMAllowed = assignUPMAllowed;
    }

    public boolean isUnassignUPMAllowed() {
        return unassignUPMAllowed;
    }

    public void setUnassignUPMAllowed(boolean unassignUPMAllowed) {
        this.unassignUPMAllowed = unassignUPMAllowed;
    }

    public List<Integer> getSharedCorpListId() {
        return sharedCorpListId;
    }

    public void setSharedCorpListId(List<Integer> sharedCorpListId) {
        this.sharedCorpListId = sharedCorpListId;
    }

    public boolean isOwnerCorpReq() { return isOwnerCorpReq; }

    public void setOwnerCorpReq(boolean ownerCorpReq) { isOwnerCorpReq = ownerCorpReq; }

    public List<String> getAddedUpmSharedCorpList() { return addedUpmSharedCorpList; }

    public void setAddedUpmSharedCorpList(List<String> addedUpmSharedCorpList) { this.addedUpmSharedCorpList = addedUpmSharedCorpList; }

    public List<String> getRemovedUpmSharedCorpList() { return removedUpmSharedCorpList; }

    public void setRemovedUpmSharedCorpList(List<String> removedUpmSharedCorpList) { this.removedUpmSharedCorpList = removedUpmSharedCorpList; }

    public int getCommonContactListRejectionId() {
        return commonContactListRejectionId;
    }

    public void setCommonContactListRejectionId(int commonContactListRejectionId) {
        this.commonContactListRejectionId = commonContactListRejectionId;
    }

    public List<Integer> getLargeGroupIds() {
        return largeGroupIds;
    }

    public void setLargeGroupIds(List<Integer> largeGroupIds) {
        this.largeGroupIds = largeGroupIds;
    }

    public boolean isSystemLevelLargeGroupLimitReached() {
        return systemLevelLargeGroupLimitReached;
    }

    public void setSystemLevelLargeGroupLimitReached(boolean systemLevelLargeGroupLimitReached) {
        this.systemLevelLargeGroupLimitReached = systemLevelLargeGroupLimitReached;
    }

    public int getMaxGroupsPerMemberCount() {
        return maxGroupsPerMemberCount;
    }

    public void setMaxGroupsPerMemberCount(int maxGroupsPerMemberCount) {
        this.maxGroupsPerMemberCount = maxGroupsPerMemberCount;
    }

    public int getGroupListSize() {
        return groupListSize;
    }

    public void setGroupListSize(int groupListSize) {
        this.groupListSize = groupListSize;
    }

    public boolean isSysSelfDndPrivilege() {
        return sysSelfDndPrivilege;
    }

    public void setSysSelfDndPrivilege(boolean sysSelfDndPrivilege) {
        this.sysSelfDndPrivilege = sysSelfDndPrivilege;
    }

    public boolean isCorpSelfDndPrivilege() {
        return corpSelfDndPrivilege;
    }

    public void setCorpSelfDndPrivilege(boolean corpSelfDndPrivilege) {
        this.corpSelfDndPrivilege = corpSelfDndPrivilege;
    }

    public int getReqSelfDndPrivilege() {
        return reqSelfDndPrivilege;
    }

    public void setReqSelfDndPrivilege(int reqSelfDndPrivilege) {
        this.reqSelfDndPrivilege = reqSelfDndPrivilege;
    }

    public boolean getMaxLocWachersPerGroupReached() {
        return maxLocWachersPerGroupReached;
    }

    public void setMaxLocWachersPerGroupReached(boolean maxLocWachersPerGroupReached) {
        this.maxLocWachersPerGroupReached = maxLocWachersPerGroupReached;
    }

    public boolean isPrimaryDestinationIsEmpty() {
        return primaryDestinationIsEmpty;
    }

    public void setPrimaryDestinationIsEmpty(boolean primaryDestinationIsEmpty) {
        this.primaryDestinationIsEmpty = primaryDestinationIsEmpty;
    }

    public boolean isDestIsBroadCastGroup() {
        return destIsBroadCastGroup;
    }

    public void setDestIsBroadCastGroup(boolean destIsBroadCastGroup) {
        this.destIsBroadCastGroup = destIsBroadCastGroup;
    }

    public boolean isGroupDoesNotBelogsToUPM() {
        return groupDoesNotBelogsToUPM;
    }

    public void setGroupDoesNotBelogsToUPM(boolean groupDoesNotBelogsToUPM) {
        this.groupDoesNotBelogsToUPM = groupDoesNotBelogsToUPM;
    }

    public boolean isSublistDoesNotBelogsToUPM() {
        return sublistDoesNotBelogsToUPM;
    }

    public void setSublistDoesNotBelogsToUPM(boolean sublistDoesNotBelogsToUPM) {
        this.sublistDoesNotBelogsToUPM = sublistDoesNotBelogsToUPM;
    }

    public boolean isMaxMembersAllowedPerGroupIsReached() {
        return maxMembersAllowedPerGroupIsReached;
    }

    public void setMaxMembersAllowedPerGroupIsReached(boolean maxMembersAllowedPerGroupIsReached) {
        this.maxMembersAllowedPerGroupIsReached = maxMembersAllowedPerGroupIsReached;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public boolean getSysLargeAgencyDispatch() {
        return sysLargeAgencyDispatch;
    }

    public void setSysLargeAgencyDispatch(boolean sysLargeAgencyDispatch) {
        this.sysLargeAgencyDispatch = sysLargeAgencyDispatch;
    }

    public boolean getCorpLargeAgencyDispatch() {
        return corpLargeAgencyDispatch;
    }

    public void setCorpLargeAgencyDispatch(boolean corpLargeAgencyDispatch) {
        this.corpLargeAgencyDispatch = corpLargeAgencyDispatch;
    }

    public int getReqLargeAgencyDispatch() {
        return reqLargeAgencyDispatch;
    }

    public void setReqLargeAgencyDispatch(int reqLargeAgencyDispatch) {
        this.reqLargeAgencyDispatch = reqLargeAgencyDispatch;
    }

    @Override
    public String toString() {
        return "KnCorpUserProfilePersistDTO{" +
                "entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", profile='" + profile + '\'' +
                ", persistenceDTO=" + persistenceDTO +
                ", corpId='" + corpId + '\'' +
                ", userProfileDTO=" + userProfileDTO +
                ", modifyUserProfileDTO=" + modifyUserProfileDTO +
                ", maxUserProfile='" + maxUserProfile + '\'' +
                ", dbMaxUserProfile='" + dbMaxUserProfile + '\'' +
                ", dbProfileName=" + dbProfileName +
                ", reqProfileName=" + reqProfileName +
                ", corpGroupList=" + corpGroupList +
                ", autoAssign=" + autoAssign +
                ", userProfileId=" + userProfileId +
                ", dbUserProfileDTO=" + dbUserProfileDTO +
                ", maxUserProfilePerSub=" + maxUserProfilePerSub +
                ", dbProfileMdnList=" + dbProfileMdnList +
                ", userProfileMgmtBit=" + userProfileMgmtBit +
                ", assignedMdnList=" + assignedMdnList +
                ", jobStatusList=" + jobStatusList +
                ", mcpttComplience = " + mcpttCompliance +
                ", mcxGroupCount = " + mcxGroupCount +
                ", maxZone = " + maxZone +
                ", maxPttRadioScanSize = " + maxPttRadioScanSize +
                ", maxChannelsPerZone = " + maxChannelsPerZone +
                ", maxPriority = " + maxPriority +
                ", sharedCorpGrpInfoMap = " + sharedCorpGrpInfoMap +
                ", trustMatrixMap = " + trustMatrixMap +
                ", externalPemMap = " + externalPemMap +
                ", subslistMemCorpInfo = " + subslistMemCorpInfo +
                ", clientType = " + clientType +
                ", dbUPMSharingFlag = " + dbUPMSharingFlag +
                ", reqUPMSharingFlag = " + reqUPMSharingFlag +
                ", dbUserProfileSharedCorpList = " + dbUserProfileSharedCorpList +
                ", reqUserProfileSharedCorpList = " + reqUserProfileSharedCorpList +
                ", isPreConfiguredGroupList = " + isPreConfiguredGroupList +
                ", upmShareEnabled = " + upmShareEnabled +
                ", sharedUpmForMdnCorp = " + sharedUpmForMdnCorp +
                ", onlyCorpSubs = " + onlyCorpSubs +
                ", sharedUpm = " + sharedUpm +
                ", assignUPMAllowed = " + assignUPMAllowed +
                ", sharedCorpListId = " + sharedCorpListId +
                ", unassignUPMAllowed = " + unassignUPMAllowed +
                ", isOwnerCorpReq = " + isOwnerCorpReq +
                ", addedUpmSharedCorpList = " + addedUpmSharedCorpList +
                ", removedUpmSharedCorpList = " + removedUpmSharedCorpList +
                ", commonContactListRejectionId = " + commonContactListRejectionId +
                ", largeGroupIds = " + largeGroupIds +
                ", systemLevelLargeGroupLimitReached = " + systemLevelLargeGroupLimitReached +
                ", maxGroupsPerMemberCount = " + maxGroupsPerMemberCount +
                ", groupListSize=" + groupListSize +
                ", sysSelfDndPrivilege=" + sysSelfDndPrivilege +
                ", corpSelfDndPrivilege=" + corpSelfDndPrivilege +
                ", reqSelfDndPrivilege=" + reqSelfDndPrivilege +
                ", maxLocWachersPerGroupReached=" + maxLocWachersPerGroupReached +
                ", primaryDestinationIsEmpty=" + primaryDestinationIsEmpty +
                ", destIsBroadCastGroup=" + destIsBroadCastGroup +
                ", sublistDoesNotBelogsToUPM=" + sublistDoesNotBelogsToUPM +
                ", sysLargeAgencyDispatch=" + sysLargeAgencyDispatch +
                ", corpLargeAgencyDispatch=" + corpLargeAgencyDispatch +
                ", reqLargeAgencyDispatch=" + reqLargeAgencyDispatch +
                '}';
    }
}
