/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnGroupInfoPersistDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 13, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnExtProfileDetails;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class KnCorpGroupInfoPersistDTO extends KnCorpGroupInfoDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676199L;

    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO performerDetails;
    //    private String groupDisplayName;
    private int corpGroupCount;
    private Map<String, Integer> subsGroupsCount;
    private int groupDistPolicy;
    private Collection<Integer> mappedSublistIds;
    private Collection<KnCorpSubscriberDTO> externalContacts;
    private LinkedList<String> removedMemberList;
    private int maxContactLimitFlag;
    private Collection<Integer> poCSublistList;
    private Collection<String> poCSubscriberList;
    private int existingGroupCount;
    private Collection<String> requestDispatcherSubscribers;
    private LinkedList<String> dbDispatcherSubscribers;
    private Collection<KnCorpSubscriberDTO> addedMdnDTO;
    private boolean multipleDispatcherAllowed;
    private int contactCountInRequest;
    private int maxAllowedContactCountPerRequest;
    private int maxDispatchGroup;
    private int maxSubscriberPerDispatchGroup;
    private int maxDispatchMembersPerDispatchGroup;
    private int dispatchEnabled;
    private boolean groupMember;
    private Collection<String> addedMdnList;
    private Collection<KnCorpGroupMemberDTO> completeSupervisorList;
    private Collection<KnCorpSubscriberDTO> groupPrivateList;
    private Collection<String> reqInterOpSubs;
    private Collection<String> dbInterOpSubs;
    private Map<String, Integer> subscSupVals;
    private Map<String, Integer> interOPSubscGrpCnt;
    private String pocHome;
    private Map<Integer, KnExtProfileDetails> configuredProfileMap;
    private Map<Integer, KnExtProfileDetails> supervisorProfileMap;
    private Map<Integer, KnExtProfileDetails> locWatcherMdnProfileMap;
    private List<Integer> inputProfileIdList;
    private List<String> grpSupervisorMemList;
    //System level BCGroup feature
    private boolean sysBCGFeatue;
    //Broadcaster count in the group
    private int broadcasterCount;
    //external profile Map fro the external contact broadcasters
    private Map<String, KnExtProfileDetails> broadcasterFeatureMap;
    //Broadcaster List in the request
    private List<String> grpBroadcasters;
    private Map<String, Integer> groupMdnGrpCnt;
    private List<String> existingGrpmdns;
    private int maxSGPerGrp;
    private String locWatcherConfigValue;
    private boolean locWatcherExists;
    private Collection<KnCorpGroupMemberDTO> locWatcherList;
    private Map<String, Integer> locWatcherMap;
    private Map<String, Integer> supervisiorMap;
    private Collection<String> requestLocWatcherSubscribers;
    private Collection<String> dbLocWatcherSubscribers;
    private int maxLocWatcherPerGroup;
    private int maxSGMdnPatchPerGroup;
    private List<KnCorpSubscriberDTO> existingGrpMdnDto;
    private Map<String, Integer> grpModifyPermMap;
    private Map<String, Integer> existingGroupModifyPermMap;
    private Map<String, KnCorpSubscriberDTO> tpRequestMDNMap;
    private Map<String, String> vendorMdnMap;
    private List<String> tpDbMDNList;
    private KnTPVendorDetailsPersistDTO tpVendorPersistDTO;
    private int maxAbdgCountSystem;
    private int maxAbdgPerGrpOwner;
    private int maxAbdgPerGrpMem;
    private int corpAbdgGroupCount;
    private int abdgGroupCountPerOwner;
    private Map<String, Integer> abdgGroupCountPerMemberMap;
    private boolean grpOwnerExistsInDB;
    private int maxLargeGrpSystem;
    private int maxLargeGrpCorp;
    private int largeGrpCountInCorp;
    private int largeGrpCountInSystem;
    private int maxMemPerLargeGroup;
    private int largeGroupSupported;
    private int maxLrgAbdgGrpPerCorp;
    private int maxMemsPerLrgAbdgGrp;
    private int numOfLrgAbdgGrp;
    private Collection<KnCorpContactDTO> addedMemberMdnsDTOLst;
    private Map<String, Integer> osmAuthorizeMap;
    private Collection<KnCorpGroupMemberDTO> modifiedMembersList;
    private LinkedList<String> groupMembersList;
    private Collection<Integer> addedSublistIds;
    private Collection<Integer> removedSublistIds;
    private boolean criLocFlag;
    
    private List<String> existingmdns;

    private List<String> criClientList; //To hold the CRI clients from added group members.
    private int maxGroupsPerCRIClient; // to hold the max configured value

    private boolean osmListChanged;
    private KnCorpProfileDTO corpProfile;
    private Map<String, String> paramNameValueMapCommon;
    private List<KnCorpTrustMatrixDTO> trustMatrixDTOList;
    private boolean isGrpSharedModified;
    private List<KnCorpGroupMemberDTO> existingGrpMemDetail;
    private Map<String,Integer> extIntCorpIdMap;
    private Map<Integer, KnCorpTrustMatrixDTO> trustMatrixMap;
    private int groupCorpId;
    private Boolean isUPMCall;
    private boolean reachedMaxDispacherAllowedPerMcxGroup;
    private int maxAllowedDispatcherPerMcxGroup;
    private boolean mcxGroup;
    private int maxAllowedPreConfigGroup;
    private Integer dbIsPreConfiguredGroup;
    private String systemMaxAllowedPreConfigGroup;
    private Integer dbPreConfiguredGroupCount;
    private List<String> validSharedMdnsFromCorp;
    private Collection<Integer> commonContactRejectList;
    private boolean ugwInteropSharedCorpChange;
    private List<String> mdnsPresentInExternalInfo;
    private List<KnCorpSubscriberDTO> addedExternalMembers;
    private boolean pttRecordingFlag;
    private boolean dataRecordingFlag;
    private boolean videoRecordingFlag;
    private Map<Integer, Integer> corpIdAndLargeGroupFlagMap;
    private Integer clusterId;
    private String hierarchyId;
    private String hierarchyRoot;
    private List<String> corpContactExternalMdns;

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getHierarchyRoot() {
        return hierarchyRoot;
    }

    public void setHierarchyRoot(String hierarchyRoot) {
        this.hierarchyRoot = hierarchyRoot;
    }

    public Map<String, KnCorpSubscriberDTO> getTpRequestMDNMap() {
		return tpRequestMDNMap;
	}

	public void setTpRequestMDNMap(Map<String, KnCorpSubscriberDTO> tpRequestMDNMap) {
		this.tpRequestMDNMap = tpRequestMDNMap;
	}

	public Map<String, String> getVendorMdnMap() {
		return vendorMdnMap;
	}

	public void setVendorMdnMap(Map<String, String> vendorMdnMap) {
		this.vendorMdnMap = vendorMdnMap;
	}

	public List<String> getTpDbMDNList() {
		return tpDbMDNList;
	}

	public void setTpDbMDNList(List<String> tpDbMDNList) {
		this.tpDbMDNList = tpDbMDNList;
	}

	public KnTPVendorDetailsPersistDTO getTpVendorPersistDTO() {
		return tpVendorPersistDTO;
	}

	public void setTpVendorPersistDTO(KnTPVendorDetailsPersistDTO tpVendorPersistDTO) {
		this.tpVendorPersistDTO = tpVendorPersistDTO;
	}

	public List<String> getGrpBroadcasters() {
        return grpBroadcasters;
    }

    public void setGrpBroadcasters(List<String> grpBroadcasters) {
        this.grpBroadcasters = grpBroadcasters;
    }

    public Map<String, KnExtProfileDetails> getBroadcasterFeatureMap() {
        return broadcasterFeatureMap;
    }

    public void setBroadcasterFeatureMap(Map<String, KnExtProfileDetails> broadcasterFeatureMap) {
        this.broadcasterFeatureMap = broadcasterFeatureMap;
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

    public List<String> getExistingmdns() {
		return existingmdns;
	}

	public void setExistingmdns(List<String> existingmdns) {
		this.existingmdns = existingmdns;
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

//    public String getGroupDisplayName() {
//        return groupDisplayName;
//    }
//
//    public void setGroupDisplayName(String groupDisplayName) {
//        this.groupDisplayName = groupDisplayName;
//    }

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

    public Collection<Integer> getMappedSublistIds() {
        return mappedSublistIds;
    }

    public void setMappedSublistIds(Collection<Integer> mappedSublistIds) {
        this.mappedSublistIds = mappedSublistIds;
    }

    public Collection<KnCorpSubscriberDTO> getExternalContacts() {
        return externalContacts;
    }

    public void setExternalContacts(Collection<KnCorpSubscriberDTO> externalContacts) {
        this.externalContacts = externalContacts;
    }

    public LinkedList<String> getRemovedMemberList() {
        return removedMemberList;
    }

    public void setRemovedMemberList(LinkedList<String> removedMemberList) {
        this.removedMemberList = removedMemberList;
    }

    public int getMaxContactLimitFlag() {
        return maxContactLimitFlag;
    }

    public void setMaxContactLimitFlag(int maxContactLimitFlag) {
        this.maxContactLimitFlag = maxContactLimitFlag;
    }

    public Collection<Integer> getPoCSublistList() {
        return poCSublistList;
    }

    public void setPoCSublistList(Collection<Integer> poCSublistList) {
        this.poCSublistList = poCSublistList;
    }

    public Collection<String> getPoCSubscriberList() {
        return poCSubscriberList;
    }

    public void setPoCSubscriberList(Collection<String> poCSubscriberList) {
        this.poCSubscriberList = poCSubscriberList;
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

    public Collection<KnCorpSubscriberDTO> getAddedMdnDTO() {
        return addedMdnDTO;
    }

    public void setAddedMdnDTO(Collection<KnCorpSubscriberDTO> addedMdnDTO) {
        this.addedMdnDTO = addedMdnDTO;
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

    public boolean isGroupMember() {
        return groupMember;
    }

    public void setGroupMember(boolean groupMember) {
        this.groupMember = groupMember;
    }

    public Collection<String> getAddedMdnList() {
        return addedMdnList;
    }

    public void setAddedMdnList(Collection<String> addedMdnList) {
        this.addedMdnList = addedMdnList;
    }

    public Collection<KnCorpGroupMemberDTO> getCompleteSupervisorList() {
        return completeSupervisorList;
    }

    public void setCompleteSupervisorList(Collection<KnCorpGroupMemberDTO> completeSupervisorList) {
        this.completeSupervisorList = completeSupervisorList;
    }

    public Collection<KnCorpSubscriberDTO> getGroupPrivateList() {
        return groupPrivateList;
    }

    public void setGroupPrivateList(Collection<KnCorpSubscriberDTO> groupPrivateList) {
        this.groupPrivateList = groupPrivateList;
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

    public Collection<KnCorpGroupMemberDTO> getLocWatcherList() {
        return locWatcherList;
    }

    public void setLocWatcherList(Collection<KnCorpGroupMemberDTO> locWatcherList) {
        this.locWatcherList = locWatcherList;
    }

    public String getLocWatcherConfigValue() {
        return locWatcherConfigValue;
    }

    public void setLocWatcherConfigValue(String locWatcherConfigValue) {
        this.locWatcherConfigValue = locWatcherConfigValue;
    }

    public boolean isLocWatcherExists() {
        return locWatcherExists;
    }

    public void setLocWatcherExists(boolean locWatcherExists) {
        this.locWatcherExists = locWatcherExists;
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

    public Map<Integer, KnExtProfileDetails> getLocWatcherMdnProfileMap() {
        return locWatcherMdnProfileMap;
    }

    public void setLocWatcherMdnProfileMap(Map<Integer, KnExtProfileDetails> locWatcherMdnProfileMap) {
        this.locWatcherMdnProfileMap = locWatcherMdnProfileMap;
    }

    public Collection<String> getRequestLocWatcherSubscribers() {
        return requestLocWatcherSubscribers;
    }

    public void setRequestLocWatcherSubscribers(Collection<String> requestLocWatcherSubscribers) {
        this.requestLocWatcherSubscribers = requestLocWatcherSubscribers;
    }

    public int getMaxLocWatcherPerGroup() {
        return maxLocWatcherPerGroup;
    }

    public void setMaxLocWatcherPerGroup(int maxLocWatcherPerGroup) {
        this.maxLocWatcherPerGroup = maxLocWatcherPerGroup;
    }

    public Collection<String> getDbLocWatcherSubscribers() {
        return dbLocWatcherSubscribers;
    }

    public void setDbLocWatcherSubscribers(Collection<String> dbLocWatcherSubscribers) {
        this.dbLocWatcherSubscribers = dbLocWatcherSubscribers;
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

    public Map<String, Integer> getGrpModifyPermMap() {
        return grpModifyPermMap;
    }

    public void setGrpModifyPermMap(Map<String, Integer> grpModifyPermMap) {
        this.grpModifyPermMap = grpModifyPermMap;
    }

    public Map<String, Integer> getExistingGroupModifyPermMap() {
        return existingGroupModifyPermMap;
    }

    public void setExistingGroupModifyPermMap(Map<String, Integer> existingGroupModifyPermMap) {
        this.existingGroupModifyPermMap = existingGroupModifyPermMap;
    }

    public int getMaxAbdgCountSystem() {
        return maxAbdgCountSystem;
    }

    public void setMaxAbdgCountSystem(int maxAbdgCountSystem) {
        this.maxAbdgCountSystem = maxAbdgCountSystem;
    }

    public int getMaxAbdgPerGrpOwner() {
        return maxAbdgPerGrpOwner;
    }

    public void setMaxAbdgPerGrpOwner(int maxAbdgPerGrpOwner) {
        this.maxAbdgPerGrpOwner = maxAbdgPerGrpOwner;
    }

    public int getMaxAbdgPerGrpMem() {
        return maxAbdgPerGrpMem;
    }

    public void setMaxAbdgPerGrpMem(int maxAbdgPerGrpMem) {
        this.maxAbdgPerGrpMem = maxAbdgPerGrpMem;
    }

    public int getCorpAbdgGroupCount() {
        return corpAbdgGroupCount;
    }

    public void setCorpAbdgGroupCount(int corpAbdgGroupCount) {
        this.corpAbdgGroupCount = corpAbdgGroupCount;
    }

    public int getAbdgGroupCountPerOwner() {
        return abdgGroupCountPerOwner;
    }

    public void setAbdgGroupCountPerOwner(int abdgGroupCountPerOwner) {
        this.abdgGroupCountPerOwner = abdgGroupCountPerOwner;
    }

    public Map<String, Integer> getAbdgGroupCountPerMemberMap() {
        return abdgGroupCountPerMemberMap;
    }

    public void setAbdgGroupCountPerMemberMap(Map<String, Integer> abdgGroupCountPerMemberMap) {
        this.abdgGroupCountPerMemberMap = abdgGroupCountPerMemberMap;
    }

    public boolean isGrpOwnerExistsInDB() {
        return grpOwnerExistsInDB;
    }

    public void setGrpOwnerExistsInDB(boolean grpOwnerExistsInDB) {
        this.grpOwnerExistsInDB = grpOwnerExistsInDB;
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

    public Collection<KnCorpContactDTO> getAddedMemberMdnsDTOLst() {
        return addedMemberMdnsDTOLst;
    }

    public void setAddedMemberMdnsDTOLst(Collection<KnCorpContactDTO> addedMemberMdnsDTOLst) {
        this.addedMemberMdnsDTOLst = addedMemberMdnsDTOLst;
    }

    public Collection<KnCorpGroupMemberDTO> getModifiedMembersList() {
        return modifiedMembersList;
    }

    public void setModifiedMembersList(Collection<KnCorpGroupMemberDTO> modifiedMembersList) {
        this.modifiedMembersList = modifiedMembersList;
    }

    public Map<String, Integer> getOsmAuthorizeMap() {
        return osmAuthorizeMap;
    }

    public void setOsmAuthorizeMap(Map<String, Integer> osmAuthorizeMap) {
        this.osmAuthorizeMap = osmAuthorizeMap;
    }

    public void setNumOfLrgAbdgGrp(int numOfLrgAbdgGrp) {
        this.numOfLrgAbdgGrp = numOfLrgAbdgGrp;
    }

    public LinkedList<String> getGroupMembersList() {
		return groupMembersList;
	}

	public void setGroupMembersList(LinkedList<String> groupMembersList) {
		this.groupMembersList = groupMembersList;
	}

	public Collection<Integer> getAddedSublistIds() {
		return addedSublistIds;
	}

	public void setAddedSublistIds(Collection<Integer> addedSublistIds) {
		this.addedSublistIds = addedSublistIds;
	}

	public Collection<Integer> getRemovedSublistIds() {
		return removedSublistIds;
	}

	public void setRemovedSublistIds(Collection<Integer> removedSublistIds) {
		this.removedSublistIds = removedSublistIds;
	}

    public List<String> getCriClientList() {
        return criClientList;
    }

    public void setCriClientList(List<String> criClientList) {
        this.criClientList = criClientList;
    }

    public int getMaxGroupsPerCRIClient() {
        return maxGroupsPerCRIClient;
    }

    public void setMaxGroupsPerCRIClient(int maxGroupsPerCRIClient) {
        this.maxGroupsPerCRIClient = maxGroupsPerCRIClient;
    }

	public boolean isCriLocFlag() {
		return criLocFlag;
	}

	public void setCriLocFlag(boolean criLocFlag) {
		this.criLocFlag = criLocFlag;
	}

    public boolean isOsmListChanged() { return osmListChanged; }

    public void setOsmListChanged(boolean osmListChanged) { this.osmListChanged = osmListChanged; }

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

    public List<KnCorpTrustMatrixDTO> getTrustMatrixDTOList() {
        return trustMatrixDTOList;
    }

    public void setTrustMatrixDTOList(List<KnCorpTrustMatrixDTO> trustMatrixDTOList) {
        this.trustMatrixDTOList = trustMatrixDTOList;
    }

    public boolean isGrpSharedModified() {
        return isGrpSharedModified;
    }

    public void setGrpSharedModified(boolean grpSharedModified) {
        isGrpSharedModified = grpSharedModified;
    }

    public List<KnCorpGroupMemberDTO> getExistingGrpMemDetail() {
        return existingGrpMemDetail;
    }

    public void setExistingGrpMemDetail(List<KnCorpGroupMemberDTO> existingGrpMemDetail) {
        this.existingGrpMemDetail = existingGrpMemDetail;
    }

    public Map<Integer, KnCorpTrustMatrixDTO> getTrustMatrixMap() {
        return trustMatrixMap;
    }

    public void setTrustMatrixMap(Map<Integer, KnCorpTrustMatrixDTO> trustMatrixMap) {
        this.trustMatrixMap = trustMatrixMap;
    }

    public int getGroupCorpId() {
        return groupCorpId;
    }

    public void setGroupCorpId(int groupCorpId) {
        this.groupCorpId = groupCorpId;
    }

    public Map<String, Integer> getExtIntCorpIdMap() {return extIntCorpIdMap; }

    public void setExtIntCorpIdMap(Map<String, Integer> extIntCorpIdMap) {this.extIntCorpIdMap = extIntCorpIdMap; }

    public Boolean isUPMCall() { return isUPMCall; }

    public void setUPMCall(Boolean UPMCall) { isUPMCall = UPMCall; }

    public boolean isReachedMaxDispacherAllowedPerMcxGroup() {
        return reachedMaxDispacherAllowedPerMcxGroup;
    }

    public void setReachedMaxDispacherAllowedPerMcxGroup(boolean reachedMaxDispacherAllowedPerMcxGroup) {
        this.reachedMaxDispacherAllowedPerMcxGroup = reachedMaxDispacherAllowedPerMcxGroup;
    }

    public int getMaxAllowedDispatcherPerMcxGroup() {
        return maxAllowedDispatcherPerMcxGroup;
    }

    public void setMaxAllowedDispatcherPerMcxGroup(int maxAllowedDispatcherPerMcxGroup) {
        this.maxAllowedDispatcherPerMcxGroup = maxAllowedDispatcherPerMcxGroup;
    }

    public boolean isMcxGroup() {
        return mcxGroup;
    }

    public void setMcxGroup(boolean mcxGroup) {
        this.mcxGroup = mcxGroup;
    }

    public int getMaxAllowedPreConfigGroup() { return maxAllowedPreConfigGroup; }

    public void setMaxAllowedPreConfigGroup(int maxAllowedPreConfigGroup) { this.maxAllowedPreConfigGroup = maxAllowedPreConfigGroup; }

    public Integer getDbIsPreConfiguredGroup() {
        return dbIsPreConfiguredGroup;
    }

    public void setDbIsPreConfiguredGroup(Integer dbIsPreConfiguredGroup) {
        this.dbIsPreConfiguredGroup = dbIsPreConfiguredGroup;
    }

    public String getSystemMaxAllowedPreConfigGroup() {
        return systemMaxAllowedPreConfigGroup;
    }

    public void setSystemMaxAllowedPreConfigGroup(String systemMaxAllowedPreConfigGroup) {
        this.systemMaxAllowedPreConfigGroup = systemMaxAllowedPreConfigGroup;
    }

    public Integer getDbPreConfiguredGroupCount() {
        return dbPreConfiguredGroupCount;
    }

    public void setDbPreConfiguredGroupCount(Integer dbPreConfiguredGroupCount) {
        this.dbPreConfiguredGroupCount = dbPreConfiguredGroupCount;
    }

    public List<String> getValidSharedMdnsFromCorp() { return validSharedMdnsFromCorp; }

    public void setValidSharedMdnsFromCorp(List<String> validSharedMdnsFromCorp) { this.validSharedMdnsFromCorp = validSharedMdnsFromCorp; }

    public Collection<Integer> getCommonContactRejectList() {
        return commonContactRejectList;
    }

    public void setCommonContactRejectList(Collection<Integer> commonContactRejectList) {
        this.commonContactRejectList = commonContactRejectList;
    }

    public boolean isUgwInteropSharedCorpChange() {
        return ugwInteropSharedCorpChange;
    }

    public void setUgwInteropSharedCorpChange(boolean ugwInteropSharedCorpChange) {
        this.ugwInteropSharedCorpChange = ugwInteropSharedCorpChange;
    }

    public List<String> getMdnsPresentInExternalInfo() { return mdnsPresentInExternalInfo; }

    public void setMdnsPresentInExternalInfo(List<String> mdnsPresentInExternalInfo) { this.mdnsPresentInExternalInfo = mdnsPresentInExternalInfo; }

    public List<KnCorpSubscriberDTO> getAddedExternalMembers() { return addedExternalMembers; }

    public void setAddedExternalMembers(List<KnCorpSubscriberDTO> addedExternalMembers) { this.addedExternalMembers = addedExternalMembers;   }

    public boolean isPttRecordingFlag() {
        return pttRecordingFlag;
    }

    public void setPttRecordingFlag(boolean pttRecordingFlag) {
        this.pttRecordingFlag = pttRecordingFlag;
    }

    public boolean isDataRecordingFlag() {
        return dataRecordingFlag;
    }

    public void setDataRecordingFlag(boolean dataRecordingFlag) {
        this.dataRecordingFlag = dataRecordingFlag;
    }

    public boolean isVideoRecordingFlag() {
        return videoRecordingFlag;
    }

    public void setVideoRecordingFlag(boolean videoRecordingFlag) {
        this.videoRecordingFlag = videoRecordingFlag;
    }

    public Map<Integer, Integer> getCorpIdAndLargeGroupFlagMap() {
        return corpIdAndLargeGroupFlagMap;
    }

    public void setCorpIdAndLargeGroupFlagMap(Map<Integer, Integer> corpIdAndLargeGroupFlagMap) {
        this.corpIdAndLargeGroupFlagMap = corpIdAndLargeGroupFlagMap;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public List<String> getCorpContactExternalMdns() {
        return corpContactExternalMdns;
    }

    public void setCorpContactExternalMdns(List<String> corpContactExternalMdns) {
        this.corpContactExternalMdns = corpContactExternalMdns;
    }

    @Override
    public String toString() {
        return "KnCorpGroupInfoPersistDTO{" +
                "groupID = "+super.getGroupId()+
                ", addedMemberMdnsDTOLst=" + addedMemberMdnsDTOLst +
                ", osmAuthorizeMap=" + osmAuthorizeMap +
                ", osmListChanged=" + osmListChanged +
                ", groupShared='" + super.getGrpShared() + '\'' +
                ", corpId='" + super.getCorpId() + '\'' +
                ", eTag='" + super.getETag() + '\'' +
                ", groupType='" + super.getGroupType() + '\'' +
                ", groupCreatedBy='" + super.getGroupCreatedBy() + '\'' +
                ", groupOwner='" + super.getTpGroupOwner() + '\'' +
                ", mcxGrpInd='" + super.getMcxGrpInd() + '\'' +
                ", IsPreConfiguredGroup='" + super.getIsPreConfiguredGroup() + '\'' +
                ", corpSharedCorpInfoList='" + super.getCorpSharedCorpInfoList() + '\'' +
                ", groupOwnerCorpId='" + super.getGrpOwnerCorpId() + '\'' +
                ", groupOwnerExtCorpId='" + super.getGrpOwnerExtCorpId() + '\'' +
                ", getRecordingFs='" + super.getRecordingFs() + '\'' +
                ", ownerIdList='" + super.getOwnerIdList() + '\'' +
                ", completeSupervisorList='" + completeSupervisorList + '\'' +
                ", supervisiorMap='" + supervisiorMap + '\'' +
                ", locWatcherMap='" + locWatcherMap + '\'' +
                ", locWatcherList='" + locWatcherList + '\'' +
                ", grpSupervisorMemList='" + grpSupervisorMemList + '\'' +
                ", reachedMaxDispacherAllowedPerMcxGroup='" + reachedMaxDispacherAllowedPerMcxGroup + '\'' +
                ", maxAllowedDispatcherPerMcxGroup='" + maxAllowedDispatcherPerMcxGroup + '\'' +
                ", pocHome='" + pocHome + '\'' +
                ", mcxGroup='" + mcxGroup + '\'' +
                ", maxAllowedPreConfigGroup='" + maxAllowedPreConfigGroup + '\'' +
                ", dbIsPreConfiguredGroup='" + dbIsPreConfiguredGroup + '\'' +
                ", dbPreConfiguredGroupCount='" + dbPreConfiguredGroupCount + '\'' +
                ", systemMaxAllowedPreConfigGroup='" + systemMaxAllowedPreConfigGroup + '\'' +
                ", commonContactRejectList='" + commonContactRejectList + '\'' +
                ", ugwInteropSharedCorpChange='" + ugwInteropSharedCorpChange + '\'' +
                ", mdnsPresentInExternalInfo='" + mdnsPresentInExternalInfo + '\'' +
                ", addedExternalMembers='" + addedExternalMembers + '\'' +
                ", pttRecordingFlag='" + pttRecordingFlag + '\'' +
                ", dataRecordingFlag='" + dataRecordingFlag + '\'' +
                ", videoRecordingFlag='" + videoRecordingFlag + '\'' +
                ", corpIdAndLargeGroupFlagMap='" + corpIdAndLargeGroupFlagMap + '\'' +
                ", clusterId='" + clusterId + '\'' +
                ", hierarchyRoot='" + hierarchyRoot + '\'' +
                ", videoPermission='" + super.getVideoPermission() + '\'' +
                ", ownerAgencyname='" + getOwnerAgencyName() + '\'' +
                ", ownerOrganizationname='" + getOwnerOrganizationName() + '\'' +
                ", corpContactExternalMdns='" + corpContactExternalMdns + '\'' +
                '}';
    }
}
