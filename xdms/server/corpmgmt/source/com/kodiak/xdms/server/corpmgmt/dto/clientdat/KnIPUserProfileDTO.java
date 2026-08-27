/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.commdto.common.KnXDMAddlTalkGroupInfoDTO;
import com.kodiak.common.commdto.request.KnXDMAddlTalkGroupRequestDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;

import java.util.*;

public class KnIPUserProfileDTO implements IInputDTO {

    private String entityId;
    private String operationType;
    private String profile;
    private String performer;

    private Map<String, Object> customParamMap;
    private KnConstants.HIERARCHY_TYPE hierarchyType;

    private String corpId;
    private String profileId;
	private String fetchSize;
    private String startIndex;
    private String userProfileName;
    private String mdn;
    private KnCorpUserProfileDTO userProfileDTO;
    private KnCorpModifyUserProfileDTO modifiedUserProfileDTO;
    private String isDefaultProfile;
    private Set<KnCorpGroupListInfoDTO> addedGroupList;
    private Set<KnCorpGroupListInfoDTO> modifiedGroupList;
    private Set<String> removedGroupList;
    private Set<KnCorpUserProfileMCPTTConfig> addedMdnPerms;
    private Set<KnCorpUserProfileMCPTTConfig> removedMdnPerms;
    private List<String> userProfileMdns;
    private KnUserProfileFSDTO userProfileFSDto;
    private String pocPttServerId;
    private List<String> txnList;
    private List<String> ownerIdList;
    Collection<KnCorpUserProfileDTO> userProfileList;
    private List<String> addedOwnerIDList;
    private List<String> removedOwnerIDList;
    private String sharingEnabled;
    private List<String> userProfileSharedCorpList;
    private String isCaseSensitiveSearch;
    private String hierarchyId;
    private LinkedList<String> removedMDNList;
    private LinkedList<String> addedGroupMemList;
    private Set<String> modifedGroupIds;
    private Set<String> modifyGroupMembers;
    private Collection<KnCorpAddlTGInfoDTO> addedAddlTgList;
    private Collection<KnCorpAddlTGInfoDTO> modifiedAddlTgList;
    private Collection<KnCorpAddlTGInfoDTO> removedAddlTgList;
    private boolean camped_Groups;
    private String addedMember;
    private Set<Integer> addedGroupIds;
    private List<String> mdnList;


    public String getAddedMember() {
        return addedMember;
    }

    public void setAddedMember(String addedMember) {
        this.addedMember = addedMember;
    }

    public boolean isCamped_Groups() {
        return camped_Groups;
    }

    public void setCamped_Groups(boolean camped_Groups) {
        this.camped_Groups = camped_Groups;
    }

    public Collection<KnCorpAddlTGInfoDTO> getAddedAddlTgList() {
        return addedAddlTgList;
    }

    public void setAddedAddlTgList(Collection<KnCorpAddlTGInfoDTO> addedAddlTgList) {
        this.addedAddlTgList = addedAddlTgList;
    }

    public Collection<KnCorpAddlTGInfoDTO> getRemovedAddlTgList() {
        return removedAddlTgList;
    }

    public void setRemovedAddlTgList(Collection<KnCorpAddlTGInfoDTO> removedAddlTgList) {
        this.removedAddlTgList = removedAddlTgList;
    }

    public Collection<KnCorpAddlTGInfoDTO> getModifiedAddlTgList() {
        return modifiedAddlTgList;
    }

    public void setModifiedAddlTgList(Collection<KnCorpAddlTGInfoDTO> modifiedAddlTgList) {
        this.modifiedAddlTgList = modifiedAddlTgList;
    }

    public Set<String> getModifedGroupIds() {
        return modifedGroupIds;
    }

    public void setModifedGroupIds(Set<String> modifedGroupIds) {
        this.modifedGroupIds = modifedGroupIds;
    }

    public Set<String> getModifyGroupMembers() {
        return modifyGroupMembers;
    }

    public void setModifyGroupMembers(Set<String> modifedMembers) {
        this.modifyGroupMembers = modifedMembers;
    }

    public LinkedList<String> getAddedGroupMemList() {
        return addedGroupMemList;
    }

    public void setAddedGroupMemList(LinkedList<String> addedGroupMemList) {
        this.addedGroupMemList = addedGroupMemList;
    }

    public LinkedList<String> getRemovedMDNList() {
        return removedMDNList;
    }

    public void setRemovedMDNList(LinkedList<String> removedMDNList) {
        this.removedMDNList = removedMDNList;
    }

    @Override
    public void setPerformer(String performer) {
        this.performer = performer;
    }

    @Override
    public String getPerformer() {
        return performer;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {

    }

    @Override
    public IAuthDTO getAuthDTO() {
        return null;
    }

    @Override
    public void setClientType(int clientType) {

    }

    @Override
    public int getClientType() {
        return 0;
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
        return "" + getCorpId();
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

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
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

    public String getUserProfileName() {
		return userProfileName;
	}

	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) { this.mdn = mdn; }

    public String getIsDefaultProfile() { return isDefaultProfile; }

    public void setIsDefaultProfile(String isDefaultProfile) { this.isDefaultProfile = isDefaultProfile; }

    public Set<KnCorpGroupListInfoDTO> getAddedGroupList() {
        return addedGroupList;
    }

    public void setAddedGroupList(Set<KnCorpGroupListInfoDTO> addedGroupList) {
        this.addedGroupList = addedGroupList;
    }

    public Set<KnCorpGroupListInfoDTO> getModifiedGroupList() {
        return modifiedGroupList;
    }

    public void setModifiedGroupList(Set<KnCorpGroupListInfoDTO> modifiedGroupList) {
        this.modifiedGroupList = modifiedGroupList;
    }

    public Set<String> getRemovedGroupList() {
        return removedGroupList;
    }

    public void setRemovedGroupList(Set<String> removedGroupList) {
        this.removedGroupList = removedGroupList;
    }

    public Set<KnCorpUserProfileMCPTTConfig> getAddedMdnPerms() {
        return addedMdnPerms;
    }

    public void setAddedMdnPerms(Set<KnCorpUserProfileMCPTTConfig> addedMdnPerms) {
        this.addedMdnPerms = addedMdnPerms;
    }

    public Set<KnCorpUserProfileMCPTTConfig> getRemovedMdnPerms() {
        return removedMdnPerms;
    }

    public void setRemovedMdnPerms(Set<KnCorpUserProfileMCPTTConfig> removedMdnPerms) {
        this.removedMdnPerms = removedMdnPerms;
    }

    public KnCorpModifyUserProfileDTO getModifiedUserProfileDTO() {
        return modifiedUserProfileDTO;
    }

    public void setModifiedUserProfileDTO(KnCorpModifyUserProfileDTO modifiedUserProfileDTO) {
        this.modifiedUserProfileDTO = modifiedUserProfileDTO;
    }
    public List<String> getUserProfileMdns() {
		return userProfileMdns;
	}

	public void setUserProfileMdns(List<String> userProfileMdns) {
		this.userProfileMdns = userProfileMdns;
	}

    public KnUserProfileFSDTO getUserProfileFSDto() {
        return userProfileFSDto;
    }

    public void setUserProfileFSDto(KnUserProfileFSDTO userProfileFSDto) {
        this.userProfileFSDto = userProfileFSDto;
    }

    public String getPocPttServerId() {
        return pocPttServerId;
    }

    public void setPocPttServerId(String pocPttServerId) {
        this.pocPttServerId = pocPttServerId;
    }


    public List<String> getTxnList() {    return txnList;  }

    public void setTxnList(List<String> txnList) {    this.txnList = txnList;  }

    public List<String> getOwnerIdList() {
        return ownerIdList;
    }

    public void setOwnerIdList(List<String> ownerIdList) {
        this.ownerIdList = ownerIdList;
    }

    public Collection<KnCorpUserProfileDTO> getUserProfileList() { return userProfileList; }

    public void setUserProfileList(Collection<KnCorpUserProfileDTO> userProfileList) { this.userProfileList = userProfileList; }

    public List<String> getAddedOwnerIDList() {
        return addedOwnerIDList;
    }

    public void setAddedOwnerIDList(List<String> addedOwnerIDList) {
        this.addedOwnerIDList = addedOwnerIDList;
    }

    public List<String> getRemovedOwnerIDList() {
        return removedOwnerIDList;
    }

    public void setRemovedOwnerIDList(List<String> removedOwnerIDList) {
        this.removedOwnerIDList = removedOwnerIDList;
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

    public String getIsCaseSensitiveSearch() {
        return isCaseSensitiveSearch;
    }

    public void setIsCaseSensitiveSearch(String isCaseSensitiveSearch) {
        this.isCaseSensitiveSearch = isCaseSensitiveSearch;
    }

    public Set<Integer> getAddedGroupIds() {
        return addedGroupIds;
    }

    public void setAddedGroupIds(Set<Integer> addedGroupIds) {
        this.addedGroupIds = addedGroupIds;
    }

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    @Override
    public String toString() {
        return "KnIPUserProfileDTO{" +
                "entityId='" + entityId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", profile='" + profile + '\'' +
                ", performer='" + performer + '\'' +
                ", customParamMap=" + customParamMap +
                ", hierarchyType=" + hierarchyType +
                ", corpId='" + corpId + '\'' +
                ", profileId='" + profileId + '\'' +
                ", fetchSize='" + fetchSize + '\'' +
                ", startIndex='" + startIndex + '\'' +
                ", userProfileName='" + userProfileName + '\'' +
                ", mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", userProfileDTO=" + userProfileDTO +
                ", modifiedUserProfileDTO=" + modifiedUserProfileDTO +
                ", isDefaultProfile= '" + isDefaultProfile +'\''+
                ", addedGroupList= '" + addedGroupList +'\''+
                ", modifiedGroupList= '" + modifiedGroupList +'\''+
                ", removedGroupList= '" + removedGroupList +'\''+
                ", addedMdnPerms= '" + addedMdnPerms +'\''+
                ", removedMdnPerms= '" + removedMdnPerms +'\''+
                ", userProfileMdns='" + KnGDPRTemplate.mdnList(userProfileMdns) + '\'' +
                ", userProfileFSDto='" + userProfileFSDto + '\'' +
                ", pocPttServerId='" + pocPttServerId + '\'' +
                ", txnList='" + txnList + '\'' +
                ", ownerIdList='" + ownerIdList + '\'' +
                ", addedOwnerIDList='" + addedOwnerIDList + '\'' +
                ", removedOwnerIDList='" + removedOwnerIDList + '\'' +
                ", sharingEnabled='" + sharingEnabled + '\'' +
                ", isCaseSensitiveSearch='" + isCaseSensitiveSearch + '\'' +
                ", userProfileSharedCorpList='" + userProfileSharedCorpList + '\'' +
                ", removedMDNList ='" + removedMDNList + '\'' +
                ", addedGroupMemList ='" + addedGroupMemList + '\'' +
                ", addedAddlTgList='" + addedAddlTgList + '\'' +
                ", modifiedAddlTgList ='" + modifiedAddlTgList + '\'' +
                ", removedAddlTgList ='" + removedAddlTgList + '\'' +
                ", camped_Groups ='" + camped_Groups + '\'' +
                ", addedMember ='" + addedMember + '\'' +
                ", hierarchyId='" + hierarchyId + '\'' +
                '}';
    }
}
