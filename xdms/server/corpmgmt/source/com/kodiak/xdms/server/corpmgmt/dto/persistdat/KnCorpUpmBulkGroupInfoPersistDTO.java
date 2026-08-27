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
 * Copyright (c) 2006  Kodiak Networks (India) Pvt. Ltd.
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

import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;

import java.util.*;

public class KnCorpUpmBulkGroupInfoPersistDTO implements IPersistenceDTO {
    private static final long serialVersionUID = 7526471155622676193L;

    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO performerDetails;

    private Map<Integer, KnCorpGroupInfoPersistDTO> persistDTOMap;
    private Map<Integer, LinkedList<String>> dispatcherSubscriberMap;
    private Map<Integer, Collection<String>> dbLocWatcherSubscriberMap;
    private Map<Integer, List<KnCorpGroupMemberDTO>> addedMdnSupervisorMap;
    private Map<Integer, Collection<String>> addedMdnListMap;
    private Map<Integer, Collection<String>> requestDispatcherSubscribersMap;
    private Map<Integer, Collection<String>> reqInterOpSubsMap;
    private Map<String, Integer> locWatcherMap;
    private Map<String, Integer> supervisiorMap;
    private Map<String, Integer> osmAuthorizeMap;
    private Map<Integer, Collection<KnCorpGroupMemberDTO>> completeSupervisorListMap;
    private Map<Integer, Collection<KnCorpGroupMemberDTO>> locWatcherListMap;
    private List<String> grpSupervisorMemList;
    private Map<String, Integer> grpModifyPermMap;
    private  Map<Integer, Collection<String>> dbInterOpSubsMap;
    private int maxAllowedDispatcherPerMcxGroup;
    private boolean multipleDispatcherAllowed;
    private  Map<Integer, Boolean> reachedMaxDispacherAllowedPerMcxGroupMap;
    private List<Integer> groupIds;
    private int maxLargeGrpSystem;
    private int maxLargeGrpCorp;
    private int largeGrpCountInCorp;
    private int largeGrpCountInSystem;
    private int maxMemPerLargeGroup;
    private int largeGroupSupported;
    private int maxLrgAbdgGrpPerCorp;
    private int maxMemsPerLrgAbdgGrp;
    private int numOfLrgAbdgGrp;
    private int maxNumberOfMembers;
    private int maxGroupsPerMemberCount;
    private KnCorpProfileDTO corpProfile;
    private Map<String, String> paramNameValueMapCommon;
    private String allowedClientTypes;
    private Integer ugwInterop;
    private String ugwInteropSystemConfig;

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


    public String getAllowedClientTypes() {
        return allowedClientTypes;
    }

    public void setAllowedClientTypes(String allowedClientTypes) {
        this.allowedClientTypes = allowedClientTypes;
    }

    public Map<Integer, KnCorpGroupInfoPersistDTO> getPersistDTOMap() {
        return persistDTOMap;
    }

    public void setPersistDTOMap(Map<Integer, KnCorpGroupInfoPersistDTO> persistDTOMap) {
        this.persistDTOMap = persistDTOMap;
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

    public IPersistenceDTO getPerformerDetails() {
        return performerDetails;
    }

    public void setPerformerDetails(IPersistenceDTO performerDetails) {
        this.performerDetails = performerDetails;
    }

    public Map<String, Integer> getLocWatcherMap() {
        return locWatcherMap;
    }

    public void setLocWatcherMap(Map<String, Integer> locWatcherMap) {
        this.locWatcherMap = locWatcherMap;
    }

    public Map<String, Integer> getSupervisiorMap() {
        return supervisiorMap;
    }

    public void setSupervisiorMap(Map<String, Integer> supervisiorMap) {
        this.supervisiorMap = supervisiorMap;
    }

    public Map<String, Integer> getOsmAuthorizeMap() {
        return osmAuthorizeMap;
    }

    public void setOsmAuthorizeMap(Map<String, Integer> osmAuthorizeMap) {
        this.osmAuthorizeMap = osmAuthorizeMap;
    }

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getCompleteSupervisorListMap() {
        return completeSupervisorListMap;
    }

    public void setCompleteSupervisorListMap(Map<Integer, Collection<KnCorpGroupMemberDTO>> completeSupervisorListMap) {
        this.completeSupervisorListMap = completeSupervisorListMap;
    }

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getLocWatcherListMap() {
        return locWatcherListMap;
    }

    public void setLocWatcherListMap(Map<Integer, Collection<KnCorpGroupMemberDTO>> locWatcherListMap) {
        this.locWatcherListMap = locWatcherListMap;
    }

    public List<String> getGrpSupervisorMemList() {
        return grpSupervisorMemList;
    }

    public void setGrpSupervisorMemList(List<String> grpSupervisorMemList) {
        this.grpSupervisorMemList = grpSupervisorMemList;
    }

    public Map<String, Integer> getGrpModifyPermMap() {
        return grpModifyPermMap;
    }

    public void setGrpModifyPermMap(Map<String, Integer> grpModifyPermMap) {
        this.grpModifyPermMap = grpModifyPermMap;
    }

    public Map<Integer, Collection<String>> getDbInterOpSubsMap() {
        return dbInterOpSubsMap;
    }

    public void setDbInterOpSubsMap(Map<Integer, Collection<String>> dbInterOpSubsMap) {
        this.dbInterOpSubsMap = dbInterOpSubsMap;
    }

    public int getMaxAllowedDispatcherPerMcxGroup() {
        return maxAllowedDispatcherPerMcxGroup;
    }

    public void setMaxAllowedDispatcherPerMcxGroup(int maxAllowedDispatcherPerMcxGroup) {
        this.maxAllowedDispatcherPerMcxGroup = maxAllowedDispatcherPerMcxGroup;
    }

    public Map<Integer, Boolean> getReachedMaxDispacherAllowedPerMcxGroupMap() {
        return reachedMaxDispacherAllowedPerMcxGroupMap;
    }

    public void setReachedMaxDispacherAllowedPerMcxGroupMap(Map<Integer, Boolean> reachedMaxDispacherAllowedPerMcxGroupMap) {
        this.reachedMaxDispacherAllowedPerMcxGroupMap = reachedMaxDispacherAllowedPerMcxGroupMap;
    }
    public boolean isMultipleDispatcherAllowed() {
        return multipleDispatcherAllowed;
    }

    public void setMultipleDispatcherAllowed(boolean multipleDispatcherAllowed) {
        this.multipleDispatcherAllowed = multipleDispatcherAllowed;
    }

    public List<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Integer> groupIds) {
        this.groupIds = groupIds;
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

    public int getMaxNumberOfMembers() {
        return maxNumberOfMembers;
    }

    public void setMaxNumberOfMembers(int maxNumberOfMembers) {
        this.maxNumberOfMembers = maxNumberOfMembers;
    }

    public int getMaxGroupsPerMemberCount() {
        return maxGroupsPerMemberCount;
    }

    public void setMaxGroupsPerMemberCount(int maxGroupsPerMemberCount) {
        this.maxGroupsPerMemberCount = maxGroupsPerMemberCount;
    }

    public KnCorpProfileDTO getCorpProfile() {
        return corpProfile;
    }

    public void setCorpProfile(KnCorpProfileDTO corpProfile) {
        this.corpProfile = corpProfile;
    }

    public Map<String, String> getParamNameValueMapCommon() {
        return paramNameValueMapCommon;
    }

    public void setParamNameValueMapCommon(Map<String, String> paramNameValueMapCommon) {
        this.paramNameValueMapCommon = paramNameValueMapCommon;
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

    @Override
    public String toString() {
        return "KnCorpUpmBulkGroupInfoPersistDTO{" +
                "inputDTO=" + inputDTO +
                ", entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", profile='" + profile + '\'' +
                ", performerDetails=" + performerDetails +
                ", persistDTOMap=" + persistDTOMap +
                ", dispatcherSubscriberMap=" + dispatcherSubscriberMap +
                ", dbLocWatcherSubscriberMap=" + dbLocWatcherSubscriberMap +
                ", addedMdnSupervisorMap=" + addedMdnSupervisorMap +
                ", addedMdnListMap=" + addedMdnListMap +
                ", requestDispatcherSubscribersMap=" + requestDispatcherSubscribersMap +
                ", reqInterOpSubsMap=" + reqInterOpSubsMap +
                ", locWatcherMap=" + locWatcherMap +
                ", supervisiorMap=" + supervisiorMap +
                ", osmAuthorizeMap=" + osmAuthorizeMap +
                ", completeSupervisorListMap=" + completeSupervisorListMap +
                ", locWatcherListMap=" + locWatcherListMap +
                ", grpSupervisorMemList=" + grpSupervisorMemList +
                ", grpModifyPermMap=" + grpModifyPermMap +
                ", dbInterOpSubsMap=" + dbInterOpSubsMap +
                ", maxAllowedDispatcherPerMcxGroup=" + maxAllowedDispatcherPerMcxGroup +
                ", reachedMaxDispacherAllowedPerMcxGroupMap=" + reachedMaxDispacherAllowedPerMcxGroupMap +
                ", multipleDispatcherAllowed=" + multipleDispatcherAllowed +
                ", groupIds=" + groupIds +
                ", maxLargeGrpSystem=" + maxLargeGrpSystem +
                ", maxLargeGrpCorp=" + maxLargeGrpCorp +
                ", largeGrpCountInCorp=" + largeGrpCountInCorp +
                ", largeGrpCountInSystem=" + largeGrpCountInSystem +
                ", maxMemPerLargeGroup=" + maxMemPerLargeGroup +
                ", largeGroupSupported=" + largeGroupSupported +
                ", maxLrgAbdgGrpPerCorp=" + maxLrgAbdgGrpPerCorp +
                ", maxMemsPerLrgAbdgGrp=" + maxMemsPerLrgAbdgGrp +
                ", numOfLrgAbdgGrp=" + numOfLrgAbdgGrp +
                ", allowedClientTypes='" + allowedClientTypes + '\'' +
                ", maxGroupsPerMemberCount='" + maxGroupsPerMemberCount + '\'' +
                ", maxNumberOfMembers=" + maxNumberOfMembers +
                ", ugwInterop=" + ugwInterop +
                ", ugwInteropSystemConfig=" + ugwInteropSystemConfig +
                '}';
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
