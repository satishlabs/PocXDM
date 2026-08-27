/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnExtProfileDetails;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KnCorpBCGrpPersistDTO extends KnCorpGroupInfoDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676191L;

    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO performerDetails;

    //System level BCGroup feature
    private boolean sysBCGFeatue;
    //Broadcaster count in the group
    private int broadcasterCount;
    //external profile Map fro the external contact broadcasters
    private Map<Integer, KnExtProfileDetails> extBroadcasterFeatureMap;

    //Broadcaster List in the request
    private List<String> grpBroadcasters;
    //External profile Map for external contacts in the request.
    private Map<Integer,KnExtProfileDetails> configuredProfileMap;
    //Valid External contact for the corporation
    private List<KnCorpSubscriberDTO> validExtContacts;
    //Valid internal poc subscribers for the corporation
    private List<KnCorpSubscriberDTO> validInternalCont;
    //Valid sublist Ids for the corporation
    private List<Integer> validSublistIds;
    //Distinct sublist members from the sublist Ids.
    private List<String> allSublistMembers;
    //Total existing group count
    private int corpGroupCount;
    //Existing group name count
    private int groupNameCount;
    //Max allowed group members per Broadcast group
    private int maxAllowedMemPerBCG;
    //Min allowed group members
    private int minAllowedMemPerBCG;
    //All subscribers and existing group count.
    private Map<String, Integer> subsGroupCountMap;
    //Maximum allowed contact per request.
    private int maxContactsPerRequest;
    //Internal broadcaster broadcast group service feature bit value.
    private Map<String, Boolean> interBroadcasterBitMap;
    //All distinct added members + sublistmembers
    private Set<String> allDistinctMemebrs;
    //Existing sublist Ids mapped to group.
    private List<Integer> existingSublistIds;
    //Group Private memberss
    private List<KnCorpSubscriberDTO> privateMemList;
    //Existing Broadcaster List
    private List<String> existingBroadcasters;
    //Final Broadcaster list in the group
    private List<String> finalBrdstrList;
    //Existing Group Member List
    private List<String> existingGrpMembers;

    private List<KnCorpSubscriberDTO> finalGroupMemList;

    private String allowedClientTypes;

    private boolean grpMdnPresent;

    private List<String> groupMdnList;

    private Map<String,Integer> groupMdnGrpCnt;

    private List<String> interOpMembers;

    private Map<String,Integer> interOpGrpCount;

    private int maxSGPerGrp;

    private int maxSGMdnPatchPerGroup;

    private boolean onlyAddedGroupMdn;

    private List<KnCorpSubscriberDTO> existingGrpMdnDto;

    private Map<String, KnCorpSubscriberDTO> tpRequestMDNMap;

    private List<String> tpMdnList;

    private KnTPVendorDetailsPersistDTO tpVendorPersistDTO;

	private Map<String, String> vendorMdnMap;

	private List<String> tpDbMDNList;

    private Map<String, Integer> existingGroupModifyPermMap;

    private int maxLargeBCGrpCorp;
    private int maxLargeGrpSystem;
    private int largeBCGrpCountInCorp;
    private int largeGrpCountInSystem;
    private int maxMemPerLargeBCGroup;
    private int largeGroupSupported;
    private Collection<KnCorpContactDTO> addedMemberMdnsDTOLst;
    private Collection<KnCorpGroupMemberDTO> modifiedMembersList;
    private boolean criLocFlag;

    private List<String> criClientList; //To hold the CRI clients from added group members.
    private int maxGroupsPerCRIClient; // to hold the max configured value

    private KnCorpProfileDTO corpProfile;
    private Map<String, String> paramNameValueMapCommon;
    private List<KnCorpTrustMatrixDTO> trustMatrixDTOList;
    private boolean isGrpSharedModified;
    private List<KnCorpGroupMemberDTO> existingGrpMemDetail;
    private Map<String,Integer> extIntCorpIdMap;
    private Map<Integer, KnCorpTrustMatrixDTO> trustMatrixMap;
    private int groupCorpId;
    private String systemMaxAllowedPreConfigGroup;
    private Integer dbPreConfiguredGroupCount;
    private Integer dbIsPreConfiguredGroup;
    private Collection<Integer> commonContactRejectList;
    private List<String> mdnsPresentInExternalInfo;
    private List<KnCorpSubscriberDTO> addedExternalMembers;
    private boolean pttRecordingFlag;
    private boolean dataRecordingFlag;
    private boolean videoRecordingFlag;
    private Map<Integer, Integer> corpIdAndLargeGroupFlagMap;
    private int maxBroadcastGroupCountPerSubscriber;
    private Map<String, Integer> subscriberBroadcastGroupCount;
    private List<String> corpContactExternalMdns;

    public Map<String, Integer> getExistingGroupModifyPermMap() {
		return existingGroupModifyPermMap;
	}

	public void setExistingGroupModifyPermMap(Map<String, Integer> existingGroupModifyPermMap) {
		this.existingGroupModifyPermMap = existingGroupModifyPermMap;
	}

	public KnTPVendorDetailsPersistDTO getTpVendorPersistDTO() {
		return tpVendorPersistDTO;
	}

	public void setTpVendorPersistDTO(KnTPVendorDetailsPersistDTO tpVendorPersistDTO) {
		this.tpVendorPersistDTO = tpVendorPersistDTO;
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

	public List<String> getTpMdnList() {
		return tpMdnList;
	}

	public void setTpMdnList(List<String> tpMdnList) {
		this.tpMdnList = tpMdnList;
	}

	public List<KnCorpSubscriberDTO> getFinalGroupMemList() {
        return finalGroupMemList;
    }

    public void setFinalGroupMemList(List<KnCorpSubscriberDTO> finalGroupMemList) {
        this.finalGroupMemList = finalGroupMemList;
    }

    public List<String> getFinalBrdstrList() {
        return finalBrdstrList;
    }

    public void setFinalBrdstrList(List<String> finalBrdstrList) {
        this.finalBrdstrList = finalBrdstrList;
    }

    public List<String> getExistingGrpMembers() {
        return existingGrpMembers;
    }

    public void setExistingGrpMembers(List<String> existingGrpMembers) {
        this.existingGrpMembers = existingGrpMembers;
    }

    public List<Integer> getExistingSublistIds() {
        return existingSublistIds;
    }

    public void setExistingSublistIds(List<Integer> existingSublistIds) {
        this.existingSublistIds = existingSublistIds;
    }

    public List<KnCorpSubscriberDTO> getPrivateMemList() {
        return privateMemList;
    }

    public void setPrivateMemList(List<KnCorpSubscriberDTO> privateMemList) {
        this.privateMemList = privateMemList;
    }

    public List<String> getExistingBroadcasters() {
        return existingBroadcasters;
    }

    public void setExistingBroadcasters(List<String> existingBroadcasters) {
        this.existingBroadcasters = existingBroadcasters;
    }

    public Map<String, Integer> getInterOpGrpCount() {
        return interOpGrpCount;
    }

    public void setInterOpGrpCount(Map<String, Integer> interOpGrpCount) {
        this.interOpGrpCount = interOpGrpCount;
    }

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

    public boolean isCriLocFlag() {
		return criLocFlag;
	}

	public void setCriLocFlag(boolean criLocFlag) {
		this.criLocFlag = criLocFlag;
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

    public Map<Integer, KnExtProfileDetails> getExtBroadcasterFeatureMap() {
        return extBroadcasterFeatureMap;
    }

    public void setExtBroadcasterFeatureMap(Map<Integer, KnExtProfileDetails> extBroadcasterFeatureMap) {
        this.extBroadcasterFeatureMap = extBroadcasterFeatureMap;
    }

    public List<String> getGrpBroadcasters() {
        return grpBroadcasters;
    }

    public void setGrpBroadcasters(List<String> grpBroadcasters) {
        this.grpBroadcasters = grpBroadcasters;
    }

    public Map<Integer, KnExtProfileDetails> getConfiguredProfileMap() {
        return configuredProfileMap;
    }

    public void setConfiguredProfileMap(Map<Integer, KnExtProfileDetails> configuredProfileMap) {
        this.configuredProfileMap = configuredProfileMap;
    }

    public List<KnCorpSubscriberDTO> getValidExtContacts() {
        return validExtContacts;
    }

    public void setValidExtContacts(List<KnCorpSubscriberDTO> validExtContacts) {
        this.validExtContacts = validExtContacts;
    }

    public List<KnCorpSubscriberDTO> getValidInternalCont() {
        return validInternalCont;
    }

    public void setValidInternalCont(List<KnCorpSubscriberDTO> validInternalCont) {
        this.validInternalCont = validInternalCont;
    }

    public List<Integer> getValidSublistIds() {
        return validSublistIds;
    }

    public void setValidSublistIds(List<Integer> validSublistIds) {
        this.validSublistIds = validSublistIds;
    }

    public List<String> getAllSublistMembers() {
        return allSublistMembers;
    }

    public void setAllSublistMembers(List<String> allSublistMembers) {
        this.allSublistMembers = allSublistMembers;
    }

    public int getCorpGroupCount() {
        return corpGroupCount;
    }

    public void setCorpGroupCount(int corpGroupCount) {
        this.corpGroupCount = corpGroupCount;
    }

    public int getGroupNameCount() {
        return groupNameCount;
    }

    public void setGroupNameCount(int groupNameCount) {
        this.groupNameCount = groupNameCount;
    }

    public int getMaxAllowedMemPerBCG() {
        return maxAllowedMemPerBCG;
    }

    public void setMaxAllowedMemPerBCG(int maxAllowedMemPerBCG) {
        this.maxAllowedMemPerBCG = maxAllowedMemPerBCG;
    }

    public int getMinAllowedMemPerBCG() {
        return minAllowedMemPerBCG;
    }

    public void setMinAllowedMemPerBCG(int minAllowedMemPerBCG) {
        this.minAllowedMemPerBCG = minAllowedMemPerBCG;
    }

    public Map<String, Integer> getSubsGroupCountMap() {
        return subsGroupCountMap;
    }

    public void setSubsGroupCountMap(Map<String, Integer> subsGroupCountMap) {
        this.subsGroupCountMap = subsGroupCountMap;
    }

    public int getMaxContactsPerRequest() {
        return maxContactsPerRequest;
    }

    public void setMaxContactsPerRequest(int maxContactsPerRequest) {
        this.maxContactsPerRequest = maxContactsPerRequest;
    }

    public Map<String, Boolean> getInterBroadcasterBitMap() {
        return interBroadcasterBitMap;
    }

    public void setInterBroadcasterBitMap(Map<String, Boolean> interBroadcasterBitMap) {
        this.interBroadcasterBitMap = interBroadcasterBitMap;
    }

    public Set<String> getAllDistinctMemebrs() {
        return allDistinctMemebrs;
    }

    public void setAllDistinctMemebrs(Set<String> allDistinctMemebrs) {
        this.allDistinctMemebrs = allDistinctMemebrs;
    }

    public String getAllowedClientTypes() {
        return allowedClientTypes;
    }

    public void setAllowedClientTypes(String allowedClientTypes) {
        this.allowedClientTypes = allowedClientTypes;
    }

    public boolean isGrpMdnPresent() {
        return grpMdnPresent;
    }

    public void setGrpMdnPresent(boolean grpMdnPresent) {
        this.grpMdnPresent = grpMdnPresent;
    }

    public List<String> getGroupMdnList() {
        return groupMdnList;
    }

    public void setGroupMdnList(List<String> groupMdnList) {
        this.groupMdnList = groupMdnList;
    }

    public Map<String, Integer> getGroupMdnGrpCnt() {
        return groupMdnGrpCnt;
    }

    public void setGroupMdnGrpCnt(Map<String, Integer> groupMdnGrpCnt) {
        this.groupMdnGrpCnt = groupMdnGrpCnt;
    }

    public List<String> getInterOpMembers() {
        return interOpMembers;
    }

    public void setInterOpMembers(List<String> interOpMembers) {
        this.interOpMembers = interOpMembers;
    }

    public int getMaxSGPerGrp() {
        return maxSGPerGrp;
    }

    public void setMaxSGPerGrp(int maxSGPerGrp) {
        this.maxSGPerGrp = maxSGPerGrp;
    }

    public int getMaxSGMdnPatchPerGroup() {
        return maxSGMdnPatchPerGroup;
    }

    public void setMaxSGMdnPatchPerGroup(int maxSGMdnPatchPerGroup) {
        this.maxSGMdnPatchPerGroup = maxSGMdnPatchPerGroup;
    }

    public boolean isOnlyAddedGroupMdn() {
        return onlyAddedGroupMdn;
    }

    public void setOnlyAddedGroupMdn(boolean onlyAddedGroupMdn) {
        this.onlyAddedGroupMdn = onlyAddedGroupMdn;
    }

    public List<KnCorpSubscriberDTO> getExistingGrpMdnDto() {
        return existingGrpMdnDto;
    }

    public void setExistingGrpMdnDto(List<KnCorpSubscriberDTO> existingGrpMdnDto) {
        this.existingGrpMdnDto = existingGrpMdnDto;
    }

    public int getMaxLargeBCGrpCorp() {
        return maxLargeBCGrpCorp;
    }

    public void setMaxLargeBCGrpCorp(int maxLargeBCGrpCorp) {
        this.maxLargeBCGrpCorp = maxLargeBCGrpCorp;
    }

    public int getMaxLargeGrpSystem() {
        return maxLargeGrpSystem;
    }

    public void setMaxLargeGrpSystem(int maxLargeGrpSystem) {
        this.maxLargeGrpSystem = maxLargeGrpSystem;
    }

    public int getLargeBCGrpCountInCorp() {
        return largeBCGrpCountInCorp;
    }

    public void setLargeBCGrpCountInCorp(int largeBCGrpCountInCorp) {
        this.largeBCGrpCountInCorp = largeBCGrpCountInCorp;
    }

    public int getLargeGrpCountInSystem() {
        return largeGrpCountInSystem;
    }

    public void setLargeGrpCountInSystem(int largeGrpCountInSystem) {
        this.largeGrpCountInSystem = largeGrpCountInSystem;
    }

    public int getMaxMemPerLargeBCGroup() {
        return maxMemPerLargeBCGroup;
    }

    public void setMaxMemPerLargeBCGroup(int maxMemPerLargeBCGroup) {
        this.maxMemPerLargeBCGroup = maxMemPerLargeBCGroup;
    }

    public int getLargeGroupSupported() {
        return largeGroupSupported;
    }

    public void setLargeGroupSupported(int largeGroupSupported) {
        this.largeGroupSupported = largeGroupSupported;
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

    public Map<String, Integer> getExtIntCorpIdMap() {
        return extIntCorpIdMap;
    }

    public void setExtIntCorpIdMap(Map<String, Integer> extIntCorpIdMap) {
        this.extIntCorpIdMap = extIntCorpIdMap;
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

    public Integer getDbIsPreConfiguredGroup() {
        return dbIsPreConfiguredGroup;
    }

    public void setDbIsPreConfiguredGroup(Integer dbIsPreConfiguredGroup) {
        this.dbIsPreConfiguredGroup = dbIsPreConfiguredGroup;
    }

    public Collection<Integer> getCommonContactRejectList() {
        return commonContactRejectList;
    }

    public void setCommonContactRejectList(Collection<Integer> commonContactRejectList) {
        this.commonContactRejectList = commonContactRejectList;
    }

    public List<String> getMdnsPresentInExternalInfo() { return mdnsPresentInExternalInfo; }

    public void setMdnsPresentInExternalInfo(List<String> mdnsPresentInExternalInfo) { this.mdnsPresentInExternalInfo = mdnsPresentInExternalInfo; }

    public List<KnCorpSubscriberDTO> getAddedExternalMembers() { return addedExternalMembers; }

    public void setAddedExternalMembers(List<KnCorpSubscriberDTO> addedExternalMembers) { this.addedExternalMembers = addedExternalMembers; }

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

    public Map<String, Integer> getSubscriberBroadcastGroupCount() {
        return subscriberBroadcastGroupCount;
    }

    public void setSubscriberBroadcastGroupCount(Map<String, Integer> subscriberBroadcastGroupCount) {
        this.subscriberBroadcastGroupCount = subscriberBroadcastGroupCount;
    }

    public int getMaxBroadcastGroupCountPerSubscriber() {
        return maxBroadcastGroupCountPerSubscriber;
    }

    public void setMaxBroadcastGroupCountPerSubscriber(int maxBroadcastGroupCountPerSubscriber) {
        this.maxBroadcastGroupCountPerSubscriber = maxBroadcastGroupCountPerSubscriber;
    }

    public List<String> getCorpContactExternalMdns() {
        return corpContactExternalMdns;
    }

    public void setCorpContactExternalMdns(List<String> corpContactExternalMdns) {
        this.corpContactExternalMdns = corpContactExternalMdns;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(250);
        sb.append(super.toString())
                .append("corpGroupCount - ").append(corpGroupCount)
                .append("sysBCGFeatue - ").append(sysBCGFeatue)
                .append("broadcasterCount - ").append(broadcasterCount)
                .append("extBroadcasterFeatureMap - ").append(extBroadcasterFeatureMap)
                .append("grpBroadcasters - ").append(grpBroadcasters)
                .append("configuredProfileMap - ").append(configuredProfileMap)
                .append("validExtContacts - ").append(validExtContacts)
                .append("validInternalCont - ").append(validInternalCont)
                .append("validSublistIds - ").append(validSublistIds)
                .append("allSublistMembers - ").append(allSublistMembers)
                .append("corpGroupCount - ").append(corpGroupCount)
                .append("groupNameCount - ").append(groupNameCount)
                .append("maxAllowedMemPerBCG - ").append(maxAllowedMemPerBCG)
                .append("minAllowedMemPerBCG - ").append(minAllowedMemPerBCG)
                .append("maxContactsPerRequest - ").append(maxContactsPerRequest)
                .append("interBroadcasterBitMap - ").append(interBroadcasterBitMap)
                .append("allDistinctMemebrs - ").append(allDistinctMemebrs)
                .append("subsGroupCountMap - ").append(subsGroupCountMap)
                .append("interOpMembers - ").append(interOpMembers)
                .append("groupMdnList - ").append(KnGDPRTemplate.mdnList(groupMdnList))
                .append("interOpGrpCount - ").append(interOpGrpCount)
                .append("maxSGPerGrp - ").append(maxSGPerGrp)
                .append("maxSGMdnPatchPerGroup - ").append(maxSGMdnPatchPerGroup)
                .append("onlyAddedGroupMdn - ").append(onlyAddedGroupMdn)
                .append("existingGrpMdnDto - ").append(existingGrpMdnDto)
                .append("maxLargeGrpSystem - ").append(maxLargeGrpSystem)
                .append("maxLargeBCGrpCorp - ").append(maxLargeBCGrpCorp)
                .append("largeBCGrpCountInCorp - ").append(largeBCGrpCountInCorp)
                .append("largeGrpCountInSystem - ").append(largeGrpCountInSystem)
                .append("addedMemberMdnsDTOLst - ").append(addedMemberMdnsDTOLst)
                .append("modifiedMembersList - ").append(modifiedMembersList)
                .append("existingGroupModifyPermMap - ").append(existingGroupModifyPermMap)
                .append("isGrpSharedModified - ").append(isGrpSharedModified)
                .append("systemMaxAllowedPreConfigGroup - ").append(systemMaxAllowedPreConfigGroup)
                .append("dbIsPreConfiguredGroup - ").append(dbIsPreConfiguredGroup)
                .append("dbPreConfiguredGroupCount - ").append(dbPreConfiguredGroupCount)
                .append("commonContactRejectList - ").append(commonContactRejectList)
                .append("mdnsPresentInExternalInfo - ").append(mdnsPresentInExternalInfo)
                .append("addedExternalMembers - ").append(addedExternalMembers)
                .append("pttRecordingFlag - ").append(pttRecordingFlag)
                .append("dataRecordingFlag - ").append(dataRecordingFlag)
                .append("videoRecordingFlag - ").append(videoRecordingFlag)
                .append("corpIdAndLargeGroupFlagMap - ").append(corpIdAndLargeGroupFlagMap)
                .append("maxBroadcastGroupCountPerSubscriber - ").append(maxBroadcastGroupCountPerSubscriber)
                .append("subscriberBroadcastGroupCount - ").append(subscriberBroadcastGroupCount)
                .append("corpContactExternalMdns - ").append(corpContactExternalMdns);
        return sb.toString();
    }
}
