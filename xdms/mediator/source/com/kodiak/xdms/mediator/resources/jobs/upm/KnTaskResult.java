/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpEXDMSNotifyDto;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Work as VO object between tasks holds the data needs for each task
 */
public class KnTaskResult {

    private String taskId;

    private int taskStatus;

    private String errorMsg;

    private String profileMdn;

    private boolean contactEtagToBeUpdated;
    private boolean permissionEtagToBeUpdated;
    private boolean emergencyEtagToBeUpdated;
    private boolean isGroupEtagToBeUpdated;
    private int upmCount;
    public int getUpmCount() {
        return upmCount;
    }

    public void setUpmCount(int upmCount) {
        this.upmCount = upmCount;
    }

    private Collection<String> disabledDispatchMemList;
    
    private Collection<String> enabledDispatchMemList;
    
    private Collection<String> addedLocWatcherList;
    
    private Collection<String> removedLocWatcherList;

    private String ttTempError;
    private KnCorpResponseDTO addGroupResp;
    private KnCorpResponseDTO removeGroupResp;
    private KnCorpResponseDTO modifyGroupMemPropResp;
    private KnCorpResponseDTO removeGroupMemPropResp;
    private List<KnCorpEXDMSNotifyDto> microserviceNotify;
    private Set<KnCorpGroupListInfoDTO> addUpmGroup;
    private Set<KnCorpGroupListInfoDTO> modifyUpmGroup;
    private Set<String> allMcsXcapUris;
    private Collection<Integer> groupIds;
    private String tgscMode;
    private String activeFS2;
    private Long lastUpdateprofileTime;
    Set<KnCorpGroupListInfoDTO>upmGroups;
    private int mcxGrpInd;
    private KnCorpResponseDTO assignGroupResp;
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getErrorMsg() { return errorMsg; }

    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }

    public String getProfileMdn() {
        return profileMdn;
    }

    public void setProfileMdn(String profileMdn) {
        this.profileMdn = profileMdn;
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

    public boolean isContactEtagToBeUpdated() {
        return contactEtagToBeUpdated;
    }

    public void setContactEtagToBeUpdated(boolean contactEtagToBeUpdated) {
        this.contactEtagToBeUpdated = contactEtagToBeUpdated;
    }

    public boolean isPermissionEtagToBeUpdated() {
        return permissionEtagToBeUpdated;
    }

    public void setPermissionEtagToBeUpdated(boolean permissionEtagToBeUpdated) {
        this.permissionEtagToBeUpdated = permissionEtagToBeUpdated;
    }

    public boolean isEmergencyEtagToBeUpdated() {
        return emergencyEtagToBeUpdated;
    }

    public void setEmergencyEtagToBeUpdated(boolean emergencyEtagToBeUpdated) {
        this.emergencyEtagToBeUpdated = emergencyEtagToBeUpdated;
    }

    public boolean isGroupEtagToBeUpdated() {
        return isGroupEtagToBeUpdated;
    }

    public void setGroupEtagToBeUpdated(boolean groupEtagToBeUpdated) {
        isGroupEtagToBeUpdated = groupEtagToBeUpdated;
    }

    public String getTtTempError() {
        return ttTempError;
    }

    public void setTtTempError(String ttTempError) {
        this.ttTempError = ttTempError;
    }

    public KnCorpResponseDTO getAddGroupResp() {
        return addGroupResp;
    }

    public void setAddGroupResp(KnCorpResponseDTO addGroupResp) {
        this.addGroupResp = addGroupResp;
    }

    public KnCorpResponseDTO getRemoveGroupResp() {
        return removeGroupResp;
    }

    public void setRemoveGroupResp(KnCorpResponseDTO removeGroupResp) {
        this.removeGroupResp = removeGroupResp;
    }

    public KnCorpResponseDTO getModifyGroupMemPropResp() {
        return modifyGroupMemPropResp;
    }

    public void setModifyGroupMemPropResp(KnCorpResponseDTO modifyGroupMemPropResp) {
        this.modifyGroupMemPropResp = modifyGroupMemPropResp;
    }

    public KnCorpResponseDTO getRemoveGroupMemPropResp() {
        return removeGroupMemPropResp;
    }

    public void setRemoveGroupMemPropResp(KnCorpResponseDTO removeGroupMemPropResp) {
        this.removeGroupMemPropResp = removeGroupMemPropResp;
    }

    public List<KnCorpEXDMSNotifyDto> getMicroserviceNotify() {
        return microserviceNotify;
    }

    public void setMicroserviceNotify(List<KnCorpEXDMSNotifyDto> microserviceNotify) {
        this.microserviceNotify = microserviceNotify;
    }

    public Set<KnCorpGroupListInfoDTO> getAddUpmGroup() {
        return addUpmGroup;
    }

    public void setAddUpmGroup(Set<KnCorpGroupListInfoDTO> addUpmGroup) {
        this.addUpmGroup = addUpmGroup;
    }

    public Set<KnCorpGroupListInfoDTO> getModifyUpmGroup() {
        return modifyUpmGroup;
    }

    public void setModifyUpmGroup(Set<KnCorpGroupListInfoDTO> modifyUpmGroup) {
        this.modifyUpmGroup = modifyUpmGroup;
    }

    public Set<String> getAllMcsXcapUris() {
        return allMcsXcapUris;
    }

    public void setAllMcsXcapUris(Set<String> allMcsXcapUris) {
        this.allMcsXcapUris = allMcsXcapUris;
    }

    public Collection<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Collection<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public String getTgscMode() {
        return tgscMode;
    }

    public void setTgscMode(String tgscMode) {
        this.tgscMode = tgscMode;
    }


    public String getActiveFS2() {
        return activeFS2;
    }

    public void setActiveFS2(String activeFS2) {
        this.activeFS2 = activeFS2;
    }

    public Long getLastUpdateprofileTime() {
        return lastUpdateprofileTime;
    }

    public void setLastUpdateprofileTime(Long lastUpdateprofileTime) {
        this.lastUpdateprofileTime = lastUpdateprofileTime;
    }

    public Set<KnCorpGroupListInfoDTO> getUpmGroups() {
        return upmGroups;
    }

    public void setUpmGroups(Set<KnCorpGroupListInfoDTO> upmGroups) {
        this.upmGroups = upmGroups;
    }

    public int getMcxGrpInd() {
        return mcxGrpInd;
    }

    public void setMcxGrpInd(int mcxGrpInd) {
        this.mcxGrpInd = mcxGrpInd;
    }

    public KnCorpResponseDTO getAssignGroupResp() {
        return assignGroupResp;
    }

    public void setAssignGroupResp(KnCorpResponseDTO assignGroupResp) {
        this.assignGroupResp = assignGroupResp;
    }

    @Override
    public String toString() {
        return "KnTaskResult{" +
                "taskId='" + taskId + '\'' +
                ", taskStatus=" + taskStatus +
                ", errorMsg='" + errorMsg + '\'' +
                ", profileMdn='" + profileMdn + '\'' +
                ", contactEtagToBeUpdated=" + contactEtagToBeUpdated +
                ", permissionEtagToBeUpdated=" + permissionEtagToBeUpdated +
                ", emergencyEtagToBeUpdated=" + emergencyEtagToBeUpdated +
                ", isGroupEtagToBeUpdated=" + isGroupEtagToBeUpdated +
                ", upmCount=" + upmCount +
                ", disabledDispatchMemList=" + disabledDispatchMemList +
                ", enabledDispatchMemList=" + enabledDispatchMemList +
                ", addedLocWatcherList=" + addedLocWatcherList +
                ", removedLocWatcherList=" + removedLocWatcherList +
                ", ttTempError='" + ttTempError + '\'' +
                ", addGroupResp=" + addGroupResp +
                ", removeGroupResp=" + removeGroupResp +
                ", modifyGroupMemPropResp=" + modifyGroupMemPropResp +
                ", removeGroupMemPropResp=" + removeGroupMemPropResp +
                ", microserviceNotify=" + microserviceNotify +
                ", groupIds=" + groupIds +
                ", tgscMode=" + tgscMode +
                ", activeFS2=" + activeFS2 +
                ", lastUpdateprofileTime=" + lastUpdateprofileTime +
                ", upmGroups=" + upmGroups +
                ", mcxGrpInd=" + mcxGrpInd +
                ", assignGroupResp=" + assignGroupResp +
                '}';
    }
}

