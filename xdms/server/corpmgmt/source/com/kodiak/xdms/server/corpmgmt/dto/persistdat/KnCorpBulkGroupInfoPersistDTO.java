/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpBulkGroupInfoPersistDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar      Jan 23, 2019      9.03
 * <p/>
 * <p/>
 * 9th Floor, MFar Manyata Tech Park
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.common.KnExtProfileDetails;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;

import java.util.*;

public class KnCorpBulkGroupInfoPersistDTO extends KnCorpGroupInfoDTO implements IPersistenceDTO {
    private static final long serialVersionUID = 7526471155622676193L;

    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO performerDetails;
    private int corpGroupCount;
    private Map<String, Integer> subsGroupsCount;
    private int groupDistPolicy;
    private int maxContactLimitFlag;
    private int existingGroupCount;
    private Collection<String> requestDispatcherSubscribers;
    private LinkedList<String> dbDispatcherSubscribers;
    private boolean multipleDispatcherAllowed;
    private int contactCountInRequest;
    private int maxAllowedContactCountPerRequest;
    private int maxDispatchGroup;
    private int maxSubscriberPerDispatchGroup;
    private int maxDispatchMembersPerDispatchGroup;
    private int dispatchEnabled;
    private Collection<String> reqInterOpSubs;
    private Collection<String> dbInterOpSubs;
    private Map<String, Integer> subscSupVals;
    private Map<String, Integer> interOPSubscGrpCnt;
    private String pocHome;
    private Map<Integer, KnExtProfileDetails> configuredProfileMap;
    private Map<Integer, KnExtProfileDetails> supervisorProfileMap;
    private List<Integer> inputProfileIdList;
    private List<String> grpSupervisorMemList;
    private boolean sysBCGFeatue;
    private int broadcasterCount;
    private List<String> grpBroadcasters;
    private Map<String, Integer> groupMdnGrpCnt;
    private List<String> existingGrpmdns;
    private int maxSGPerGrp;
    private Map<String, Integer> supervisiorMap;
    private int maxLocWatcherPerGroup;
    private int maxSGMdnPatchPerGroup;
    private List<KnCorpSubscriberDTO> existingGrpMdnDto;
    private int maxLargeGrpSystem;
    private int maxLargeGrpCorp;
    private int largeGrpCountInCorp;
    private int largeGrpCountInSystem;
    private int maxMemPerLargeGroup;
    private int largeGroupSupported;
    private int maxLrgAbdgGrpPerCorp;
    private int maxMemsPerLrgAbdgGrp;
    private int numOfLrgAbdgGrp;
    private Map<String, KnCorpSubscriberDTO> inputMdnsProfile;
    private Collection<Integer> groupIdNotExists;
    private boolean grpNotExistsForToMdn;
    private Map<Integer, KnCorpGroupDTO> groupDetailsMap;
    private int maxLargeBCGrpCorp;
    private int largeBCGrpCountInCorp;
    private int maxMemPerLargeBCGroup;
    private int maxAllowedMemPerBCG;
    private int actualMemberForLargeGroup;
    private int actualMemberForLargeGroupBC;
    private Map<Integer, Integer> standardGroupsCount;
    private Map<Integer, Integer> dispatchGroupsCount;
    private Map<Integer, Integer> broadcastGroupsCount;
    private LinkedHashMap<Integer, LinkedList<String>> dispatcherListCount;
    private LinkedHashMap<Integer, LinkedList<String>> groupMdnListCount;
    private LinkedHashMap<Integer, LinkedList<String>> groupMdnPatchListCount;
    private LinkedHashMap<Integer, LinkedList<String>> interopMdnListCount;
    private Map<Integer, Integer> grpIdNormalToLrgTran;
    private Collection<Integer> grpIds;
    private int toMdnMcpttCompliance;
    private int maxGrpsPerCriClient;
    private Map<Integer, LinkedList<String>> dispatcherSubscriberMap;
    private Map<Integer, Collection<String>> dbLocWatcherSubscriberMap;

    private Map<Integer, List<KnCorpGroupMemberDTO>> addedMdnSupervisorMap;
    private Map<Integer, Collection<String>> addedMdnListMap;
    private Map<Integer, Collection<String>> requestDispatcherSubscribersMap;
    private Map<Integer, Collection<String>> reqInterOpSubsMap;

    public List<String> getGrpBroadcasters() {
        return grpBroadcasters;
    }

    public void setGrpBroadcasters(List<String> grpBroadcasters) {
        this.grpBroadcasters = grpBroadcasters;
    }

    public boolean isSysBCGFeatue() {
        return sysBCGFeatue;
    }

    public void setSysBCGFeatue(boolean sysBCGFeatue) {
        this.sysBCGFeatue = sysBCGFeatue;
    }

    public int getBroadcasterCount() {
        return broadcasterCount;
    }

    public void setBroadcasterCount(int broadcasterCount) {
        this.broadcasterCount = broadcasterCount;
    }

    private String allowedClientTypes;

    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.performerDetails = persistenceDTO;
    }

    public IPersistenceDTO getPersistenceDTO() {
        return performerDetails;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public IPersistenceDTO getPerformerDetails() {
        return performerDetails;
    }

    public void setPerformerDetails(IPersistenceDTO performerDetails) {
        this.performerDetails = performerDetails;
    }

    public int getCorpGroupCount() {
        return corpGroupCount;
    }

    public void setCorpGroupCount(int corpGroupCount) {
        this.corpGroupCount = corpGroupCount;
    }

    public Map<String, Integer> getSubsGroupsCount() {
        return subsGroupsCount;
    }

    public void setSubsGroupsCount(Map<String, Integer> subsGroupsCount) {
        this.subsGroupsCount = subsGroupsCount;
    }

    public int getGroupDistPolicy() {
        return groupDistPolicy;
    }

    public void setGroupDistPolicy(int groupDistPolicy) {
        this.groupDistPolicy = groupDistPolicy;
    }

    public int getMaxContactLimitFlag() {
        return maxContactLimitFlag;
    }

    public void setMaxContactLimitFlag(int maxContactLimitFlag) {
        this.maxContactLimitFlag = maxContactLimitFlag;
    }

    public int getExistingGroupCount() {
        return existingGroupCount;
    }

    public void setExistingGroupCount(int existingGroupCount) {
        this.existingGroupCount = existingGroupCount;
    }

    public Collection<String> getRequestDispatcherSubscribers() {
        return requestDispatcherSubscribers;
    }

    public void setRequestDispatcherSubscribers(Collection<String> requestDispatcherSubscribers) {
        this.requestDispatcherSubscribers = requestDispatcherSubscribers;
    }

    public LinkedList<String> getDbDispatcherSubscribers() {
        return dbDispatcherSubscribers;
    }

    public void setDbDispatcherSubscribers(LinkedList<String> dbDispatcherSubscribers) {
        this.dbDispatcherSubscribers = dbDispatcherSubscribers;
    }

    public boolean isMultipleDispatcherAllowed() {
        return multipleDispatcherAllowed;
    }

    public void setMultipleDispatcherAllowed(boolean multipleDispatcherAllowed) {
        this.multipleDispatcherAllowed = multipleDispatcherAllowed;
    }

    public int getContactCountInRequest() {
        return contactCountInRequest;
    }

    public void setContactCountInRequest(int contactCountInRequest) {
        this.contactCountInRequest = contactCountInRequest;
    }

    public int getMaxAllowedContactCountPerRequest() {
        return maxAllowedContactCountPerRequest;
    }

    public void setMaxAllowedContactCountPerRequest(int maxAllowedContactCountPerRequest) {
        this.maxAllowedContactCountPerRequest = maxAllowedContactCountPerRequest;
    }

    public int getMaxDispatchGroup() {
        return maxDispatchGroup;
    }

    public void setMaxDispatchGroup(int maxDispatchGroup) {
        this.maxDispatchGroup = maxDispatchGroup;
    }

    public int getMaxSubscriberPerDispatchGroup() {
        return maxSubscriberPerDispatchGroup;
    }

    public void setMaxSubscriberPerDispatchGroup(int maxSubscriberPerDispatchGroup) {
        this.maxSubscriberPerDispatchGroup = maxSubscriberPerDispatchGroup;
    }

    public int getMaxDispatchMembersPerDispatchGroup() {
        return maxDispatchMembersPerDispatchGroup;
    }

    public void setMaxDispatchMembersPerDispatchGroup(int maxDispatchMembersPerDispatchGroup) {
        this.maxDispatchMembersPerDispatchGroup = maxDispatchMembersPerDispatchGroup;
    }

    public int getDispatchEnabled() {
        return dispatchEnabled;
    }

    public void setDispatchEnabled(int dispatchEnabled) {
        this.dispatchEnabled = dispatchEnabled;
    }

    public Collection<String> getReqInterOpSubs() {
        return reqInterOpSubs;
    }

    public void setReqInterOpSubs(Collection<String> reqInterOpSubs) {
        this.reqInterOpSubs = reqInterOpSubs;
    }

    public Collection<String> getDbInterOpSubs() {
        return dbInterOpSubs;
    }

    public void setDbInterOpSubs(Collection<String> dbInterOpSubs) {
        this.dbInterOpSubs = dbInterOpSubs;
    }

    public Map<String, Integer> getSubscSupVals() {
        return subscSupVals;
    }

    public void setSubscSupVals(Map<String, Integer> subscSupVals) {
        this.subscSupVals = subscSupVals;
    }

    public Map<String, Integer> getInterOPSubscGrpCnt() {
        return interOPSubscGrpCnt;
    }

    public void setInterOPSubscGrpCnt(Map<String, Integer> interOPSubscGrpCnt) {
        this.interOPSubscGrpCnt = interOPSubscGrpCnt;
    }

    public String getPocHome() {
        return pocHome;
    }

    public Map<Integer, KnExtProfileDetails> getConfiguredProfileMap() {
        return configuredProfileMap;
    }

    public void setConfiguredProfileMap(Map<Integer, KnExtProfileDetails> configuredProfileMap) {
        this.configuredProfileMap = configuredProfileMap;
    }

    public List<Integer> getInputProfileIdList() {
        return inputProfileIdList;
    }

    public void setInputProfileIdList(List<Integer> inputProfileIdList) {
        this.inputProfileIdList = inputProfileIdList;
    }

    public void setPocHome(String pocHome) {
        this.pocHome = pocHome;
    }

    public Map<Integer, KnExtProfileDetails> getSupervisorProfileMap() {
        return supervisorProfileMap;
    }

    public void setSupervisorProfileMap(Map<Integer, KnExtProfileDetails> supervisorProfileMap) {
        this.supervisorProfileMap = supervisorProfileMap;
    }

    public List<String> getGrpSupervisorMemList() {
        return grpSupervisorMemList;
    }

    public void setGrpSupervisorMemList(List<String> grpSupervisorMemList) {
        this.grpSupervisorMemList = grpSupervisorMemList;
    }

    public String getAllowedClientTypes() {
        return allowedClientTypes;
    }

    public void setAllowedClientTypes(String allowedClientTypes) {
        this.allowedClientTypes = allowedClientTypes;
    }

    public Map<String, Integer> getGroupMdnGrpCnt() {
        return groupMdnGrpCnt;
    }

    public void setGroupMdnGrpCnt(Map<String, Integer> groupMdnGrpCnt) {
        this.groupMdnGrpCnt = groupMdnGrpCnt;
    }

    public List<String> getExistingGrpmdns() {
        return existingGrpmdns;
    }

    public void setExistingGrpmdns(List<String> existingGrpmdns) {
        this.existingGrpmdns = existingGrpmdns;
    }

    public int getMaxSGPerGrp() {
        return maxSGPerGrp;
    }

    public void setMaxSGPerGrp(int maxSGPerGrp) {
        this.maxSGPerGrp = maxSGPerGrp;
    }

    public Map<String, Integer> getSupervisiorMap() {
        return supervisiorMap;
    }

    public void setSupervisiorMap(Map<String, Integer> supervisiorMap) {
        this.supervisiorMap = supervisiorMap;
    }

    public int getMaxLocWatcherPerGroup() {
        return maxLocWatcherPerGroup;
    }

    public void setMaxLocWatcherPerGroup(int maxLocWatcherPerGroup) {
        this.maxLocWatcherPerGroup = maxLocWatcherPerGroup;
    }

    public int getMaxSGMdnPatchPerGroup() {
        return maxSGMdnPatchPerGroup;
    }

    public void setMaxSGMdnPatchPerGroup(int maxSGMdnPatchPerGroup) {
        this.maxSGMdnPatchPerGroup = maxSGMdnPatchPerGroup;
    }

    public List<KnCorpSubscriberDTO> getExistingGrpMdnDto() {
        return existingGrpMdnDto;
    }

    public void setExistingGrpMdnDto(List<KnCorpSubscriberDTO> existingGrpMdnDto) {
        this.existingGrpMdnDto = existingGrpMdnDto;
    }

    public int getMaxLargeGrpSystem() {
        return maxLargeGrpSystem;
    }

    public void setMaxLargeGrpSystem(int maxLargeGrpSystem) {
        this.maxLargeGrpSystem = maxLargeGrpSystem;
    }

    public int getMaxLargeGrpCorp() {
        return maxLargeGrpCorp;
    }

    public void setMaxLargeGrpCorp(int maxLargeGrpCorp) {
        this.maxLargeGrpCorp = maxLargeGrpCorp;
    }

    public int getLargeGrpCountInCorp() {
        return largeGrpCountInCorp;
    }

    public void setLargeGrpCountInCorp(int largeGrpCountInCorp) {
        this.largeGrpCountInCorp = largeGrpCountInCorp;
    }

    public int getLargeGrpCountInSystem() {
        return largeGrpCountInSystem;
    }

    public void setLargeGrpCountInSystem(int largeGrpCountInSystem) {
        this.largeGrpCountInSystem = largeGrpCountInSystem;
    }

    public int getMaxMemPerLargeGroup() {
        return maxMemPerLargeGroup;
    }

    public void setMaxMemPerLargeGroup(int maxMemPerLargeGroup) {
        this.maxMemPerLargeGroup = maxMemPerLargeGroup;
    }

    public int getLargeGroupSupported() {
        return largeGroupSupported;
    }

    public void setLargeGroupSupported(int largeGroupSupported) {
        this.largeGroupSupported = largeGroupSupported;
    }

    public int getMaxLrgAbdgGrpPerCorp() {
        return maxLrgAbdgGrpPerCorp;
    }

    public void setMaxLrgAbdgGrpPerCorp(int maxLrgAbdgGrpPerCorp) {
        this.maxLrgAbdgGrpPerCorp = maxLrgAbdgGrpPerCorp;
    }

    public int getMaxMemsPerLrgAbdgGrp() {
        return maxMemsPerLrgAbdgGrp;
    }

    public void setMaxMemsPerLrgAbdgGrp(int maxMemsPerLrgAbdgGrp) {
        this.maxMemsPerLrgAbdgGrp = maxMemsPerLrgAbdgGrp;
    }

    public int getNumOfLrgAbdgGrp() {
        return numOfLrgAbdgGrp;
    }

    public void setNumOfLrgAbdgGrp(int numOfLrgAbdgGrp) {
        this.numOfLrgAbdgGrp = numOfLrgAbdgGrp;
    }

    public Map<String, KnCorpSubscriberDTO> getInputMdnsProfile() {
        return inputMdnsProfile;
    }

    public void setInputMdnsProfile(Map<String, KnCorpSubscriberDTO> inputMdnsProfile) {
        this.inputMdnsProfile = inputMdnsProfile;
    }

    public Collection<Integer> getGroupIdNotExists() {
        return groupIdNotExists;
    }

    public void setGroupIdNotExists(Collection<Integer> groupIdNotExists) {
        this.groupIdNotExists = groupIdNotExists;
    }

    public boolean isGrpNotExistsForToMdn() {
        return grpNotExistsForToMdn;
    }

    public void setGrpNotExistsForToMdn(boolean grpNotExistsForToMdn) {
        this.grpNotExistsForToMdn = grpNotExistsForToMdn;
    }

    public Map<Integer, KnCorpGroupDTO> getGroupDetailsMap() {
        return groupDetailsMap;
    }

    public void setGroupDetailsMap(Map<Integer, KnCorpGroupDTO> groupDetailsMap) {
        this.groupDetailsMap = groupDetailsMap;
    }

    public int getMaxLargeBCGrpCorp() {
        return maxLargeBCGrpCorp;
    }

    public void setMaxLargeBCGrpCorp(int maxLargeBCGrpCorp) {
        this.maxLargeBCGrpCorp = maxLargeBCGrpCorp;
    }

    public int getLargeBCGrpCountInCorp() {
        return largeBCGrpCountInCorp;
    }

    public void setLargeBCGrpCountInCorp(int largeBCGrpCountInCorp) {
        this.largeBCGrpCountInCorp = largeBCGrpCountInCorp;
    }

    public int getMaxMemPerLargeBCGroup() {
        return maxMemPerLargeBCGroup;
    }

    public void setMaxMemPerLargeBCGroup(int maxMemPerLargeBCGroup) {
        this.maxMemPerLargeBCGroup = maxMemPerLargeBCGroup;
    }

    public int getMaxAllowedMemPerBCG() {
        return maxAllowedMemPerBCG;
    }

    public void setMaxAllowedMemPerBCG(int maxAllowedMemPerBCG) {
        this.maxAllowedMemPerBCG = maxAllowedMemPerBCG;
    }

    public int getActualMemberForLargeGroup() {
        return actualMemberForLargeGroup;
    }

    public void setActualMemberForLargeGroup(int actualMemberForLargeGroup) {
        this.actualMemberForLargeGroup = actualMemberForLargeGroup;
    }

    public int getActualMemberForLargeGroupBC() {
        return actualMemberForLargeGroupBC;
    }

    public void setActualMemberForLargeGroupBC(int actualMemberForLargeGroupBC) {
        this.actualMemberForLargeGroupBC = actualMemberForLargeGroupBC;
    }

    public Map<Integer, Integer> getStandardGroupsCount() {
        return standardGroupsCount;
    }

    public void setStandardGroupsCount(Map<Integer, Integer> standardGroupsCount) {
        this.standardGroupsCount = standardGroupsCount;
    }

    public Map<Integer, Integer> getDispatchGroupsCount() {
        return dispatchGroupsCount;
    }

    public void setDispatchGroupsCount(Map<Integer, Integer> dispatchGroupsCount) {
        this.dispatchGroupsCount = dispatchGroupsCount;
    }

    public Map<Integer, Integer> getBroadcastGroupsCount() {
        return broadcastGroupsCount;
    }

    public void setBroadcastGroupsCount(Map<Integer, Integer> broadcastGroupsCount) {
        this.broadcastGroupsCount = broadcastGroupsCount;
    }

    public LinkedHashMap<Integer, LinkedList<String>> getDispatcherListCount() {
        return dispatcherListCount;
    }

    public void setDispatcherListCount(LinkedHashMap<Integer, LinkedList<String>> dispatcherListCount) {
        this.dispatcherListCount = dispatcherListCount;
    }

    public LinkedHashMap<Integer, LinkedList<String>> getGroupMdnListCount() {
        return groupMdnListCount;
    }

    public void setGroupMdnListCount(LinkedHashMap<Integer, LinkedList<String>> groupMdnListCount) {
        this.groupMdnListCount = groupMdnListCount;
    }

    public LinkedHashMap<Integer, LinkedList<String>> getGroupMdnPatchListCount() {
        return groupMdnPatchListCount;
    }

    public void setGroupMdnPatchListCount(LinkedHashMap<Integer, LinkedList<String>> groupMdnPatchListCount) {
        this.groupMdnPatchListCount = groupMdnPatchListCount;
    }

    public LinkedHashMap<Integer, LinkedList<String>> getInteropMdnListCount() {
        return interopMdnListCount;
    }

    public void setInteropMdnListCount(LinkedHashMap<Integer, LinkedList<String>> interopMdnListCount) {
        this.interopMdnListCount = interopMdnListCount;
    }

    public Map<Integer, Integer> getGrpIdNormalToLrgTran() {
        return grpIdNormalToLrgTran;
    }

    public void setGrpIdNormalToLrgTran(Map<Integer, Integer> grpIdNormalToLrgTran) {
        this.grpIdNormalToLrgTran = grpIdNormalToLrgTran;
    }

    public Collection<Integer> getGrpIds() {
        return grpIds;
    }

    public void setGrpIds(Collection<Integer> grpIds) {
        this.grpIds = grpIds;
    }

    public int getToMdnMcpttCompliance() {
        return toMdnMcpttCompliance;
    }

    public void setToMdnMcpttCompliance(int toMdnMcpttCompliance) {
        this.toMdnMcpttCompliance = toMdnMcpttCompliance;
    }

    public int getMaxGrpsPerCriClient() {
        return maxGrpsPerCriClient;
    }

    public void setMaxGrpsPerCriClient(int maxGrpsPerCriClient) {
        this.maxGrpsPerCriClient = maxGrpsPerCriClient;
    }

    public Map<Integer, LinkedList<String>> getDispatcherSubscriberMap() {
        return dispatcherSubscriberMap;
    }

    public void setDispatcherSubscriberMap(Map<Integer, LinkedList<String>> dispatcherSubscriberMap) {
        this.dispatcherSubscriberMap = dispatcherSubscriberMap;
    }

    public Map<Integer, Collection<String>> getDbLocWatcherSubscriberMap() {
        return dbLocWatcherSubscriberMap;
    }

    public void setDbLocWatcherSubscriberMap(Map<Integer, Collection<String>> dbLocWatcherSubscriberMap) {
        this.dbLocWatcherSubscriberMap = dbLocWatcherSubscriberMap;
    }

    public Map<Integer, List<KnCorpGroupMemberDTO>> getAddedMdnSupervisorMap() {
        return addedMdnSupervisorMap;
    }

    public void setAddedMdnSupervisorMap(Map<Integer, List<KnCorpGroupMemberDTO>> addedMdnSupervisorMap) {
        this.addedMdnSupervisorMap = addedMdnSupervisorMap;
    }

    public Map<Integer, Collection<String>> getAddedMdnListMap() {
        return addedMdnListMap;
    }

    public void setAddedMdnListMap(Map<Integer, Collection<String>> addedMdnListMap) {
        this.addedMdnListMap = addedMdnListMap;
    }

    public Map<Integer, Collection<String>> getRequestDispatcherSubscribersMap() {
        return requestDispatcherSubscribersMap;
    }

    public void setRequestDispatcherSubscribersMap(Map<Integer, Collection<String>> requestDispatcherSubscribersMap) {
        this.requestDispatcherSubscribersMap = requestDispatcherSubscribersMap;
    }

    public Map<Integer, Collection<String>> getReqInterOpSubsMap() {
        return reqInterOpSubsMap;
    }

    public void setReqInterOpSubsMap(Map<Integer, Collection<String>> reqInterOpSubsMap) {
        this.reqInterOpSubsMap = reqInterOpSubsMap;
    }

    @Override
    public String toString() {
        return "KnCorpBulkGroupInfoPersistDTO{" +
                "inputDTO=" + inputDTO +
                ", entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", profile='" + profile + '\'' +
                ", performerDetails=" + performerDetails +
                ", corpGroupCount=" + corpGroupCount +
                ", subsGroupsCount=" + subsGroupsCount +
                ", groupDistPolicy=" + groupDistPolicy +
                ", maxContactLimitFlag=" + maxContactLimitFlag +
                ", existingGroupCount=" + existingGroupCount +
                ", requestDispatcherSubscribers=" + KnGDPRTemplate.mdnList(requestDispatcherSubscribers) +
                ", dbDispatcherSubscribers=" + KnGDPRTemplate.mdnList(dbDispatcherSubscribers) +
                ", multipleDispatcherAllowed=" + multipleDispatcherAllowed +
                ", contactCountInRequest=" + contactCountInRequest +
                ", maxAllowedContactCountPerRequest=" + maxAllowedContactCountPerRequest +
                ", maxDispatchGroup=" + maxDispatchGroup +
                ", maxSubscriberPerDispatchGroup=" + maxSubscriberPerDispatchGroup +
                ", maxDispatchMembersPerDispatchGroup=" + maxDispatchMembersPerDispatchGroup +
                ", dispatchEnabled=" + dispatchEnabled +
                ", reqInterOpSubs=" + reqInterOpSubs +
                ", dbInterOpSubs=" + dbInterOpSubs +
                ", subscSupVals=" + subscSupVals +
                ", interOPSubscGrpCnt=" + interOPSubscGrpCnt +
                ", pocHome='" + pocHome + '\'' +
                ", configuredProfileMap=" + configuredProfileMap +
                ", supervisorProfileMap=" + supervisorProfileMap +
                ", inputProfileIdList=" + inputProfileIdList +
                ", grpSupervisorMemList=" + KnGDPRTemplate.mdnList(grpSupervisorMemList) +
                ", sysBCGFeatue=" + sysBCGFeatue +
                ", broadcasterCount=" + broadcasterCount +
                ", grpBroadcasters=" + KnGDPRTemplate.mdnList(grpBroadcasters) +
                ", groupMdnGrpCnt=" + groupMdnGrpCnt +
                ", existingGrpmdns=" + existingGrpmdns +
                ", maxSGPerGrp=" + maxSGPerGrp +
                ", supervisiorMap=" + supervisiorMap +
                ", maxLocWatcherPerGroup=" + maxLocWatcherPerGroup +
                ", maxSGMdnPatchPerGroup=" + maxSGMdnPatchPerGroup +
                ", existingGrpMdnDto=" + existingGrpMdnDto +
                ", maxLargeGrpSystem=" + maxLargeGrpSystem +
                ", maxLargeGrpCorp=" + maxLargeGrpCorp +
                ", largeGrpCountInCorp=" + largeGrpCountInCorp +
                ", largeGrpCountInSystem=" + largeGrpCountInSystem +
                ", maxMemPerLargeGroup=" + maxMemPerLargeGroup +
                ", largeGroupSupported=" + largeGroupSupported +
                ", maxLrgAbdgGrpPerCorp=" + maxLrgAbdgGrpPerCorp +
                ", maxMemsPerLrgAbdgGrp=" + maxMemsPerLrgAbdgGrp +
                ", numOfLrgAbdgGrp=" + numOfLrgAbdgGrp +
                ", inputMdnsProfile=" + inputMdnsProfile +
                ", groupIdNotExists=" + groupIdNotExists +
                ", grpNotExistsForToMdn=" + grpNotExistsForToMdn +
                ", groupDetailsMap=" + groupDetailsMap +
                ", maxLargeBCGrpCorp=" + maxLargeBCGrpCorp +
                ", largeBCGrpCountInCorp=" + largeBCGrpCountInCorp +
                ", maxMemPerLargeBCGroup=" + maxMemPerLargeBCGroup +
                ", maxAllowedMemPerBCG=" + maxAllowedMemPerBCG +
                ", actualMemberForLargeGroup=" + actualMemberForLargeGroup +
                ", actualMemberForLargeGroupBC=" + actualMemberForLargeGroupBC +
                ", standardGroupsCount=" + standardGroupsCount +
                ", dispatchGroupsCount=" + dispatchGroupsCount +
                ", broadcastGroupsCount=" + broadcastGroupsCount +
                ", dispatcherListCount=" + dispatcherListCount +
                ", groupMdnListCount=" + groupMdnListCount +
                ", groupMdnPatchListCount=" + groupMdnPatchListCount +
                ", interopMdnListCount=" + interopMdnListCount +
                ", grpIdNormalToLrgTran=" + grpIdNormalToLrgTran +
                ", grpIds=" + grpIds +
                ", allowedClientTypes='" + allowedClientTypes +
                ", dispatcherSubscriberMap='" + dispatcherSubscriberMap +
                ", dbLocWatcherSubscriberMap='" + dbLocWatcherSubscriberMap +
                ", addedMdnSupervisorMap='" + addedMdnSupervisorMap +
                ", addedMdnListMap='" + addedMdnListMap +
                ", requestDispatcherSubscribersMap='" + requestDispatcherSubscribersMap +
                ", reqInterOpSubsMap='" + reqInterOpSubsMap +
                '}';
    }
}
