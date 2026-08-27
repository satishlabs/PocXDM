/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnIPGroupInfoDTO.java
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
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IAuthDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSharedCorpInfo;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class KnIPCorpGroupInfoDTO extends KnCorpGroupInfoDTO implements IInputDTO {

    private static final long serialVersionUID = 7526471155622676190L;

    private Collection<String> addedMemberMdns;
    private Collection<KnCorpContactDTO> addedMemberDTOMdns;
    private Collection<String> addedMdnsList;
    private LinkedList<String> removedMemberMdns;
    private Collection<Integer> addedSublistIds;
    private Collection<Integer> removedSublistIds;
    private String performer;
    private IAuthDTO authDTO;
    private int clientType;
    private String operationType;
    private String entityId;
    private String profile;
    private Collection<KnCorpSubscriberDTO> externalContacts;
    private boolean contactPairing;
    private boolean groupTypeChanged;
    private Map<String, Object> customParamMap;
    private boolean lmrInteropFeatureChanged;
    private Set<KnCorpGroupListInfoDTO> addCorpGroupList;
    private Set<KnCorpGroupListInfoDTO> modifyCorpGroupList;
    private Set<String> removeCorpGroupList;
    private String profileMdn;
    private boolean isUpmCall;
    private boolean isOwnerCorpReq;
    private String useProfileId;
    private Map<Integer, Boolean> grpLocWatcherMap;
    private String extCorpId;
    private String hierarchyId;
    private String requestingHierarchyId;

    // P7-2: explicit add/remove lists for group sharing (modifyGroup path)
    private List<KnCorpSharedCorpInfo> addedCorpSharedCorpInfoList;
    private List<KnCorpSharedCorpInfo> removedCorpSharedCorpInfoList;

    private Set<String> corpContactExternalMdns;

    public List<KnCorpSharedCorpInfo> getAddedCorpSharedCorpInfoList() {
        return addedCorpSharedCorpInfoList;
    }

    public void setAddedCorpSharedCorpInfoList(List<KnCorpSharedCorpInfo> addedCorpSharedCorpInfoList) {
        this.addedCorpSharedCorpInfoList = addedCorpSharedCorpInfoList;
    }

    public List<KnCorpSharedCorpInfo> getRemovedCorpSharedCorpInfoList() {
        return removedCorpSharedCorpInfoList;
    }

    public void setRemovedCorpSharedCorpInfoList(List<KnCorpSharedCorpInfo> removedCorpSharedCorpInfoList) {
        this.removedCorpSharedCorpInfoList = removedCorpSharedCorpInfoList;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getRequestingHierarchyId() {
        return requestingHierarchyId;
    }

    public void setRequestingHierarchyId(String requestingHierarchyId) {
        this.requestingHierarchyId = requestingHierarchyId;
    }

    public KnIPCorpGroupInfoDTO() {

    }
    public KnIPCorpGroupInfoDTO(Set<KnCorpGroupListInfoDTO> addCorpGroupList,
                                String profileMdn, int corpId) {
        super(corpId);
        this.addCorpGroupList = addCorpGroupList;
        this.profileMdn = profileMdn;
    }

    public KnIPCorpGroupInfoDTO(Set<KnCorpGroupListInfoDTO> addCorpGroupList,
                                Set<KnCorpGroupListInfoDTO> modifyCorpGroupList,
                                Set<String> removeCorpGroupList,String profileMdn, int corpId) {
        super(corpId);
        this.addCorpGroupList = addCorpGroupList;
        this.modifyCorpGroupList=modifyCorpGroupList;
        this.removeCorpGroupList=removeCorpGroupList;
        this.profileMdn = profileMdn;
    }



    public Collection<String> getAddedMemberMdns() {
        return addedMemberMdns;
    }

    public void setAddedMemberMdns(Collection<String> addedMemberMdns) {
        this.addedMemberMdns = addedMemberMdns;
    }

    public Collection<KnCorpContactDTO> getAddedMemberDTOMdns() {
        return addedMemberDTOMdns;
    }

    public void setAddedMemberDTOMdns(Collection<KnCorpContactDTO> addedMemberDTOMdns) {
        this.addedMemberDTOMdns = addedMemberDTOMdns;
    }

    public LinkedList<String> getRemovedMemberMdns() {
        return removedMemberMdns;
    }

    public void setRemovedMemberMdns(LinkedList<String> removedMemberMdns) {
        this.removedMemberMdns = removedMemberMdns;
    }

    public Collection<Integer> getRemovedSublistIds() {
        return removedSublistIds;
    }

    public void setRemovedSublistIds(Collection<Integer> removedSublistIds) {
        this.removedSublistIds = removedSublistIds;
    }

    public Collection<Integer> getAddedSublistIds() {
        return addedSublistIds;
    }

    public void setAddedSublistIds(Collection<Integer> addedSublistIds) {
        this.addedSublistIds = addedSublistIds;
    }

    public Collection<KnCorpSubscriberDTO> getExternalContacts() {
        return externalContacts;
    }

    public void setExternalContacts(Collection<KnCorpSubscriberDTO> externalContacts) {
        this.externalContacts = externalContacts;
    }


    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
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

    public Collection<String> getAddedMdnsList() {
        return addedMdnsList;
    }

    public void setAddedMdnsList(Collection<String> addedMdnsList) {
        this.addedMdnsList = addedMdnsList;
    }

    public boolean isContactPairing() {
        return contactPairing;
    }

    public void setContactPairing(boolean contactPairing) {
        this.contactPairing = contactPairing;
    }

    public boolean isGroupTypeChanged() {
        return groupTypeChanged;
    }

    public void setGroupTypeChanged(boolean groupTypeChanged) {
        this.groupTypeChanged = groupTypeChanged;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public boolean isLmrInteropFeatureChanged() {
        return lmrInteropFeatureChanged;
    }

    public void setLmrInteropFeatureChanged(boolean lmrInteropFeatureChanged) {
        this.lmrInteropFeatureChanged = lmrInteropFeatureChanged;
    }

    public Set<KnCorpGroupListInfoDTO> getAddCorpGroupList() {
        return addCorpGroupList;
    }

    public void setAddCorpGroupList(Set<KnCorpGroupListInfoDTO> addCorpGroupList) {
        this.addCorpGroupList = addCorpGroupList;
    }

    public Set<KnCorpGroupListInfoDTO> getModifyCorpGroupList() {
        return modifyCorpGroupList;
    }

    public void setModifyCorpGroupList(Set<KnCorpGroupListInfoDTO> modifyCorpGroupList) {
        this.modifyCorpGroupList = modifyCorpGroupList;
    }

    public Set<String> getRemoveCorpGroupList() {
        return removeCorpGroupList;
    }

    public void setRemoveCorpGroupList(Set<String> removeCorpGroupList) {
        this.removeCorpGroupList = removeCorpGroupList;
    }

    public String getProfileMdn() {
        return profileMdn;
    }

    public void setProfileMdn(String profileMdn) {
        this.profileMdn = profileMdn;
    }

    public boolean isUpmCall() {
        return isUpmCall;
    }

    public void setUpmCall(boolean upmCall) {
        isUpmCall = upmCall;
    }

    public boolean isOwnerCorpReq() {
        return isOwnerCorpReq;
    }

    public void setOwnerCorpReq(boolean ownerCorpReq) {
        isOwnerCorpReq = ownerCorpReq;
    }

    public String getUseProfileId() {
		return useProfileId;
	}
	public void setUseProfileId(String useProfileId) {
		this.useProfileId = useProfileId;
	}
    public Map<Integer, Boolean> getGrpLocWatcherMap() {
        return grpLocWatcherMap;
    }

    public void setGrpLocWatcherMap(Map<Integer, Boolean> grpLocWatcherMap) {
        this.grpLocWatcherMap = grpLocWatcherMap;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public Set<String> getCorpContactExternalMdns() {
        return corpContactExternalMdns;
    }

    public void setCorpContactExternalMdns(Set<String> corpContactExternalMdns) {
        this.corpContactExternalMdns = corpContactExternalMdns;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append(super.toString())
                .append(", AddedMemberMdns - ").append(KnGDPRTemplate.mdnList(addedMemberMdns))
                .append(", AddedMemberDTOMdns - ").append(addedMemberDTOMdns)
                .append(", RemovedMemberMdns - ").append(KnGDPRTemplate.mdnList(removedMemberMdns))
                .append(", AddedSublistIds - ").append(addedSublistIds)
                .append(", RemovedSublistIds - ").append(removedSublistIds)
                .append(", AddedMdnsList - ").append(KnGDPRTemplate.mdnList(addedMdnsList))
                .append(", ContactPairing - ").append(contactPairing)
                .append(", GroupTypeChanged - ").append(groupTypeChanged)
                .append(", CustomParamMap - ").append(customParamMap)
                .append(", profileMdn - ").append(KnGDPRTemplate.mdn(profileMdn))
                .append(", addCorpGroupList - ").append(addCorpGroupList)
                .append(", modifyCorpGroupList - ").append(modifyCorpGroupList)
                .append(", removeCorpGroupList - ").append(removeCorpGroupList)
                .append(", isUpmCall - ").append(isUpmCall)
                .append(", lmrInteropFeatureChanged - ").append(lmrInteropFeatureChanged)
                .append(", isOwnerCorpReq - ").append(isOwnerCorpReq)
                .append(", useProfileId - ").append(useProfileId)
                .append(", grpLocWatcherMap -").append(grpLocWatcherMap);
        return sb.toString();
    }
}