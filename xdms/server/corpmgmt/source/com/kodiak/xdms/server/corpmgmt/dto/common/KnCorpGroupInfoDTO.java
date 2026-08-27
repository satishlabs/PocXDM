/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnGroupInfoDTO.java
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
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class KnCorpGroupInfoDTO extends KnCorpGroupDTO {

    private int corpId;
    private int groupMemberCount;
    private int maxNumberOfMembers;
    private int maxGroupMemLimitFlag;
    private int maxGroups;
    private int maxGroupsPerMemberCount;
    private int maxGroupsPerLargeDispatchMemberCount;
    private int minGroupMemberCount;
    private int groupMemberListId;
    private Collection<KnCorpSublistDTO> sublistList;
    private Collection<KnCorpGroupMemberDTO> groupMembers;
    private Map<String, KnCorpSubscriberDTO> groupMemberMap;
    private Collection<KnCorpGroupMemberDTO> groupSupervisor;
    private Collection<KnCorpSubscriberDTO> sublistMembers;
    private Collection<KnCorpGroupMemberDTO> modifiedMembers;
    private Collection<KnCorpGroupMemberDTO> addedMdnSupervisor;
    private Collection<KnCorpSubscriberDTO> addedMDNs;
    private Map<String, Integer> subsGroupCount;
    private int memberCount;
    private boolean isCamped;
    private String subscrProtocolversion;
    private String groupUri;
    private int maxVLGMdnPerGroup;
    private List<Integer> groupIds;
    private boolean isSysLargeAgencyDispatchEnabled;
    private boolean isCorpLargeAgencyDispatchEnabled;

    private int memberListCount;
    private String ownerAgencyName;
    private String ownerOrganizationName;
    private String ownerHierarchyId;
    private List<int[]> groupSharingMap;

    public KnCorpGroupInfoDTO(){}

    public KnCorpGroupInfoDTO(int corpId) {
        this.corpId = corpId;
    }

    public String getSubscrProtocolversion() {
        return subscrProtocolversion;
    }

    public void setSubscrProtocolversion(String subscrProtocolversion) {
        this.subscrProtocolversion = subscrProtocolversion;
    }
    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public Collection<KnCorpSublistDTO> getSublistList() {
        return sublistList;
    }

    public void setSublistList(Collection<KnCorpSublistDTO> sublistList) {
        this.sublistList = sublistList;
    }

    public Collection<KnCorpGroupMemberDTO> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(Collection<KnCorpGroupMemberDTO> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public int getGroupMemberCount() {
        return groupMemberCount;
    }

    public void setGroupMemberCount(int groupMemberCount) {
        this.groupMemberCount = groupMemberCount;
    }

    public int getMaxNumberOfMembers() {
        return maxNumberOfMembers;
    }

    public void setMaxNumberOfMembers(int maxNumberOfMembers) {
        this.maxNumberOfMembers = maxNumberOfMembers;
    }

    public int getGroupMemberListId() {
        return groupMemberListId;
    }

    public void setGroupMemberListId(int groupMemberListId) {
        this.groupMemberListId = groupMemberListId;
    }

    public int getMaxGroups() {
        return maxGroups;
    }

    public void setMaxGroups(int maxGroups) {
        this.maxGroups = maxGroups;
    }

    public int getMaxGroupMemLimitFlag() {
        return maxGroupMemLimitFlag;
    }

    public void setMaxGroupMemLimitFlag(int maxGroupMemLimitFlag) {
        this.maxGroupMemLimitFlag = maxGroupMemLimitFlag;
    }

    public Map<String, KnCorpSubscriberDTO> getGroupMemberMap() {
        return groupMemberMap;
    }

    public void setGroupMemberMap(Map<String, KnCorpSubscriberDTO> groupMemberMap) {
        this.groupMemberMap = groupMemberMap;
    }

    public int getMaxGroupsPerMemberCount() {
        return maxGroupsPerMemberCount;
    }

    public void setMaxGroupsPerMemberCount(int maxGroupsPerMemberCount) {
        this.maxGroupsPerMemberCount = maxGroupsPerMemberCount;
    }

    public int getMaxGroupsPerLargeDispatchMemberCount() {
        return maxGroupsPerLargeDispatchMemberCount;
    }

    public void setMaxGroupsPerLargeDispatchMemberCount(int maxGroupsPerLargeDispatchMemberCount) {
        this.maxGroupsPerLargeDispatchMemberCount = maxGroupsPerLargeDispatchMemberCount;
    }

    public boolean getIsSysLargeAgencyDispatchEnabled() {
        return isSysLargeAgencyDispatchEnabled;
    }

    public void setIsSysLargeAgencyDispatchEnabled(boolean isSysLargeAgencyDispatchEnabled) {
        this.isSysLargeAgencyDispatchEnabled = isSysLargeAgencyDispatchEnabled;
    }

    public boolean getIsCorpLargeAgencyDispatchEnabled() {
        return isCorpLargeAgencyDispatchEnabled;
    }

    public void setIsCorpLargeAgencyDispatchEnabled(boolean isCorpLargeAgencyDispatchEnabled) {
        this.isCorpLargeAgencyDispatchEnabled = isCorpLargeAgencyDispatchEnabled;
    }

    public int getMinGroupMemberCount() {
        return minGroupMemberCount;
    }

    public void setMinGroupMemberCount(int minGroupMemberCount) {
        this.minGroupMemberCount = minGroupMemberCount;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public Collection<KnCorpGroupMemberDTO> getGroupSupervisor() {
        return groupSupervisor;
    }

    public void setGroupSupervisor(Collection<KnCorpGroupMemberDTO> groupSupervisor) {
        this.groupSupervisor = groupSupervisor;
    }

    public Collection<KnCorpSubscriberDTO> getSublistMembers() {
        return sublistMembers;
    }

    public void setSublistMembers(Collection<KnCorpSubscriberDTO> sublistMembers) {
        this.sublistMembers = sublistMembers;
    }

    public Collection<KnCorpSubscriberDTO> getAddedMDNs() {
        return addedMDNs;
    }

    public void setAddedMDNs(Collection<KnCorpSubscriberDTO> addedMDNs) {
        this.addedMDNs = addedMDNs;
    }

    public Map<String, Integer> getSubsGroupCount() {
        return subsGroupCount;
    }

    public void setSubsGroupCount(Map<String, Integer> subsGroupCount) {
        this.subsGroupCount = subsGroupCount;
    }

    public Collection<KnCorpGroupMemberDTO> getModifiedMembers() {
        return modifiedMembers;
    }

    public void setModifiedMembers(Collection<KnCorpGroupMemberDTO> modifiedMembers) {
        this.modifiedMembers = modifiedMembers;
    }

    public Collection<KnCorpGroupMemberDTO> getAddedMdnSupervisor() {
        return addedMdnSupervisor;
    }

    public void setAddedMdnSupervisor(Collection<KnCorpGroupMemberDTO> addedMdnSupervisor) {
        this.addedMdnSupervisor = addedMdnSupervisor;
    }

    public boolean isCamped() {
        return isCamped;
    }

    public void setCamped(boolean camped) {
        isCamped = camped;
    }

    public String getGroupUri() {
        return groupUri;
    }

    public void setGroupUri(String groupUri) {
        this.groupUri = groupUri;
    }

    public int getMaxVLGMdnPerGroup() {
        return maxVLGMdnPerGroup;
    }

    public void setMaxVLGMdnPerGroup(int maxVLGMdnPerGroup) {
        this.maxVLGMdnPerGroup = maxVLGMdnPerGroup;
    }

    public List<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public int getMemberListCount() { return memberListCount; }

    public void setMemberListCount(int memberListCount) { this.memberListCount = memberListCount; }

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

    public String getOwnerHierarchyId() { return ownerHierarchyId; }

    public void setOwnerHierarchyId(String ownerHierarchyId) { this.ownerHierarchyId = ownerHierarchyId; }

    public List<int[]> getGroupSharingMap() {
        return groupSharingMap;
    }

    public void setGroupSharingMap(List<int[]> groupSharingMap) {
        this.groupSharingMap = groupSharingMap;
    }

    @Override
    public String toString() {
        return "KnCorpGroupInfoDTO{" +
                "KnCorpGroupDTO ="+super.toString()+
                " , corpId=" + corpId +
                ", groupMemberCount=" + groupMemberCount +
                ", maxNumberOfMembers=" + maxNumberOfMembers +
                ", maxGroupMemLimitFlag=" + maxGroupMemLimitFlag +
                ", maxGroups=" + maxGroups +
                ", maxGroupsPerMemberCount=" + maxGroupsPerMemberCount +
                ", minGroupMemberCount=" + minGroupMemberCount +
                ", groupMemberListId=" + groupMemberListId +
                ", sublistList=" + sublistList +
                ", groupMembers=" + groupMembers +
                ", groupMemberMap=" + groupMemberMap +
                ", groupSupervisor=" + groupSupervisor +
                ", sublistMembers=" + sublistMembers +
                ", modifiedMembers=" + modifiedMembers +
                ", addedMdnSupervisor=" + addedMdnSupervisor +
                ", memberCount=" + memberCount +
                ", isCamped=" + isCamped +
                ", subscrProtocolversion='" + subscrProtocolversion + '\'' +
                ", groupShared='" + super.getGrpShared() + '\'' +
                ", corpSharedCorpInfoList='" + super.getCorpSharedCorpInfoList() + '\'' +
                ", groupOwnerCorpId='" + super.getGrpOwnerCorpId() + '\'' +
                ", groupOwnerExtCorpId='" + super.getGrpOwnerExtCorpId() + '\'' +
                ", maxGroupsPerLargeDispatchMemberCount='" + maxGroupsPerLargeDispatchMemberCount + '\'' +
                ", sysLargeAgencyDispatchEnabled= '" + isSysLargeAgencyDispatchEnabled + '\'' +
                ", corpLargeAgencyDispatchEnabled= '" + isCorpLargeAgencyDispatchEnabled + '\'' +
                ", memberListCount= '" + memberListCount + '\'' +
                ", ownerAgencyName= '" + ownerAgencyName + '\'' +
                ", ownerOrganizationName= '" + ownerOrganizationName + '\'' +
                ", ownerHierarchyId= '" + ownerHierarchyId + '\'' +
                ", groupSharingMap= '" + groupSharingMap + '\'' +
                '}';
    }



}
