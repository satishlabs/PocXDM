/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSublistDetailsPersistDTO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        24-01-2011      7.0
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

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.common.KnExtProfileDetails;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSublistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KnSublistDetailsPersistDTO extends KnCorpSublistDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676206L;

    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO persistenceDTO;
    private Collection<Integer> sublistIds;
    private Collection<String> removedMemberList;
    private Collection<String> pocMdnList;
    private Collection<KnCorpSubscriberDTO> corpExtContacts;
    private Collection<KnCorpSubscriberDTO> dbSublistMembers;
    private int totalSublistsMembersCount;
    private int corpSublistCount;
    private long currentEtag;
    private int maxCorpList;
    private int maxCorpListMemberCount;
    private int sublistCountByName;
    private Collection<KnCorpSubscriberDTO> addedMdnDTOList;
    private Map<String, Integer> subscAdditonalContactCnt;
    private Map<String, String> subscContactCnt;
    private int maxSubscribersContactLimit;
    private int contactCountInRequest;
    private int maxAllowedContactCountPerRequest;
    private Map<String, Integer> subsGroupCountMap;
    private int maxGroupsPerMemberCount;
    private boolean isSublistExistInCorporate;
    private Map<Integer,KnExtProfileDetails> configuredProfileMap;
    private List<Integer> inputProfileIdList;
    private boolean isSublstInDispGrp;
    private boolean isSublistInGroup;
    private boolean isSublistDistributed;

    private List<String> criClientList; //To hold the CRI clients from added group members.
    private int maxGroupsPerCRIClient; // to hold the max configured value
    private Map<String, String> lastMemberSublistUPMMap;
    private boolean isCommContactListSupp;
    private int cmnContactListSize;
    private int totalCommonContactListMemCount;
    private boolean isCommonContactList;
    private boolean isNonAutoPair;
    private boolean listDistribution;
    private Set<Integer> subscrsCorpIds;
    private int subListCorpId;
    private Map<String, Integer> commCnctListCountPerSub;
    private int sysMaxAllowedCommCnctListPerSub;
    private Collection<String> unAssignedSublistMdns;


    public Collection<Integer> getSublistIds() {
        return sublistIds;
    }

    public void setSublistIds(Collection<Integer> sublistIds) {
        this.sublistIds = sublistIds;
    }

    public int getTotalSublistsMembersCount() {
        return totalSublistsMembersCount;
    }

    public void setTotalSublistsMembersCount(int totalSublistsMembersCount) {
        this.totalSublistsMembersCount = totalSublistsMembersCount;
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
        this.persistenceDTO = persistenceDTO;
    }

    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
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

    public String getObjectId() {
        return null;
    }

    public Collection<String> getPocMdnList() {
        return pocMdnList;
    }

    public void setPocMdnList(Collection<String> pocMdnList) {
        this.pocMdnList = pocMdnList;
    }

    public Collection<String> getRemovedMemberList() {
        return removedMemberList;
    }

    public void setRemovedMemberList(Collection<String> removedMemberList) {
        this.removedMemberList = removedMemberList;
    }

    public int getCorpSublistCount() {
        return corpSublistCount;
    }

    public void setCorpSublistCount(int corpSublistCount) {
        this.corpSublistCount = corpSublistCount;
    }

    public long getCurrentEtag() {
        return currentEtag;
    }

    public void setCurrentEtag(long currentEtag) {
        this.currentEtag = currentEtag;
    }

    public int getMaxCorpList() {
        return maxCorpList;
    }

    public void setMaxCorpList(int maxCorpList) {
        this.maxCorpList = maxCorpList;
    }

    public int getMaxCorpListMemberCount() {
        return maxCorpListMemberCount;
    }

    public void setMaxCorpListMemberCount(int maxCorpListMemberCount) {
        this.maxCorpListMemberCount = maxCorpListMemberCount;
    }

    public int getSublistCountByName() {
        return sublistCountByName;
    }

    public void setSublistCountByName(int sublistCountByName) {
        this.sublistCountByName = sublistCountByName;
    }

    public Collection<KnCorpSubscriberDTO> getCorpExtContacts() {
        return corpExtContacts;
    }

    public void setCorpExtContacts(Collection<KnCorpSubscriberDTO> corpExtContacts) {
        this.corpExtContacts = corpExtContacts;
    }

    public Collection<KnCorpSubscriberDTO> getDbSublistMembers() {
        return dbSublistMembers;
    }

    public void setDbSublistMembers(Collection<KnCorpSubscriberDTO> dbSublistMembers) {
        this.dbSublistMembers = dbSublistMembers;
    }

    public Collection<KnCorpSubscriberDTO> getAddedMdnDTOList() {
        return addedMdnDTOList;
    }

    public void setAddedMdnDTOList(Collection<KnCorpSubscriberDTO> addedMdnDTOList) {
        this.addedMdnDTOList = addedMdnDTOList;
    }

    public Map<String, Integer> getSubscAdditonalContactCnt() {
        return subscAdditonalContactCnt;
    }

    public void setSubscAdditonalContactCnt(Map<String, Integer> subscAdditonalContactCnt) {
        this.subscAdditonalContactCnt = subscAdditonalContactCnt;
    }

    public Map<String, String> getSubscContactCnt() {
        return subscContactCnt;
    }

    public void setSubscContactCnt(Map<String, String> subscContactCnt) {
        this.subscContactCnt = subscContactCnt;
    }

    public int getMaxSubscribersContactLimit() {
        return maxSubscribersContactLimit;
    }

    public void setMaxSubscribersContactLimit(int maxSubscribersContactLimit) {
        this.maxSubscribersContactLimit = maxSubscribersContactLimit;
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

    public Map<String, Integer> getSubsGroupCountMap() {
        return subsGroupCountMap;
    }

    public void setSubsGroupCountMap(Map<String, Integer> subsGroupCountMap) {
        this.subsGroupCountMap = subsGroupCountMap;
    }

    public int getMaxGroupsPerMemberCount() {
        return maxGroupsPerMemberCount;
    }

    public void setMaxGroupsPerMemberCount(int maxGroupsPerMemberCount) {
        this.maxGroupsPerMemberCount = maxGroupsPerMemberCount;
    }
    
    public boolean isSublistExistInCorporate() {
		return isSublistExistInCorporate;
	}

	public void setSublistExistInCorporate(boolean isSublistExistInCorporate) {
		this.isSublistExistInCorporate = isSublistExistInCorporate;
	}


    public Map<Integer, KnExtProfileDetails> getConfiguredProfileMap() {
		return configuredProfileMap;
	}

	public void setConfiguredProfileMap(
			Map<Integer, KnExtProfileDetails> configuredProfileMap) {
		this.configuredProfileMap = configuredProfileMap;
	}

	public List<Integer> getInputProfileIdList() {
		return inputProfileIdList;
	}

	public void setInputProfileIdList(List<Integer> inputProfileIdList) {
		this.inputProfileIdList = inputProfileIdList;
	}

    public boolean isSublstInDispGrp() {
        return isSublstInDispGrp;
    }

    public void setSublstInDispGrp(boolean sublstInDispGrp) {
        isSublstInDispGrp = sublstInDispGrp;
    }

    public boolean isSublistInGroup() {
        return isSublistInGroup;
    }

    public void setSublistInGroup(boolean sublistInGroup) {
        isSublistInGroup = sublistInGroup;
    }

    public boolean isSublistDistributed() {
        return isSublistDistributed;
    }

    public void setSublistDistributed(boolean sublistDistributed) {
        isSublistDistributed = sublistDistributed;
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

    public Map<String, String> getLastMemberSublistUPMMap() {
        return lastMemberSublistUPMMap;
    }

    public void setLastMemberSublistUPMMap(Map<String, String> lastMemberSublistUPMMap) {
        this.lastMemberSublistUPMMap = lastMemberSublistUPMMap;
    }

    public boolean isCommContactListSupp() {
        return isCommContactListSupp;
    }

    public void setCommContactListSupp(boolean commContactListSupp) {
        isCommContactListSupp = commContactListSupp;
    }

    public int getCmnContactListSize() {
        return cmnContactListSize;
    }

    public void setCmnContactListSize(int cmnContactListSize) {
        this.cmnContactListSize = cmnContactListSize;
    }

    public int getTotalCommonContactListMemCount() {
        return totalCommonContactListMemCount;
    }

    public void setTotalCommonContactListMemCount(int totalCommonContactListMemCount) {
        this.totalCommonContactListMemCount = totalCommonContactListMemCount;
    }

    public boolean isCommonContactList() {
        return isCommonContactList;
    }

    public void setCommonContactList(boolean commonContactList) {
        isCommonContactList = commonContactList;
    }

    public boolean isNonAutoPair() {
        return isNonAutoPair;
    }

    public void setNonAutoPair(boolean nonAutoPair) {
        isNonAutoPair = nonAutoPair;
    }

    public boolean isListDistribution() {
        return listDistribution;
    }

    public void setListDistribution(boolean listDistribution) {
        this.listDistribution = listDistribution;
    }

    public Set<Integer> getSubscrsCorpIds() {
        return subscrsCorpIds;
    }

    public void setSubscrsCorpIds(Set<Integer> subscrsCorpIds) {
        this.subscrsCorpIds = subscrsCorpIds;
    }

    public int getSubListCorpId() {
        return subListCorpId;
    }

    public void setSubListCorpId(int subListCorpId) {
        this.subListCorpId = subListCorpId;
    }

    public Map<String, Integer> getCommCnctListCountPerSub() {
        return commCnctListCountPerSub;
    }

    public void setCommCnctListCountPerSub(Map<String, Integer> commCnctListCountPerSub) {
        this.commCnctListCountPerSub = commCnctListCountPerSub;
    }

    public int getSysMaxAllowedCommCnctListPerSub() {
        return sysMaxAllowedCommCnctListPerSub;
    }

    public void setSysMaxAllowedCommCnctListPerSub(int sysMaxAllowedCommCnctListPerSub) {
        this.sysMaxAllowedCommCnctListPerSub = sysMaxAllowedCommCnctListPerSub;
    }

    public Collection<String> getUnAssignedSublistMdns() {
        return unAssignedSublistMdns;
    }

    public void setUnAssignedSublistMdns(Collection<String> unAssignedSublistMdns) {
        this.unAssignedSublistMdns = unAssignedSublistMdns;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(400);
        sb.append(super.toString())
                .append(", InputDTO - ").append(inputDTO)
                .append(", EntityhId - ").append(entityId)
                .append(", OperationType - ").append(operationType)
                .append(", Profile - ").append(profile)
                .append(", PersistDTO - ").append(persistenceDTO)
                .append(", SublistList - ").append(sublistIds)
                .append(", AddedMdnList - ").append(KnGDPRTemplate.mdnList(pocMdnList))
                .append(", RemovedMdnList - ").append(KnGDPRTemplate.mdnList(removedMemberList))
                .append(", TotalMemberCount - ").append(totalSublistsMembersCount)
                .append(", CorpSublistCount - ").append(corpSublistCount)
                .append(", CurrentEtag - ").append(currentEtag)
                .append(", MaxCorpList - ").append(maxCorpList)
                .append(", MaxCorpListMemberCount - ").append(maxCorpListMemberCount)
                .append(", SublistCountByName - ").append(sublistCountByName)
                .append(", CorpExternalContactList - ").append(corpExtContacts)
                .append(", DbSublistMembers - ").append(dbSublistMembers)
                .append(", addedMdnDTOList - ").append(addedMdnDTOList)
                .append(", subscAdditonalContactCnt - ").append(KnGDPRTemplate.mapKeyMdn(subscAdditonalContactCnt))
                .append(", subscContactCnt - ").append(subscContactCnt)
                .append(", maxSubscribersContactLimit - ").append(maxSubscribersContactLimit)
                .append(", contactCountInRequest - ").append(contactCountInRequest)
                .append(", maxAllowedContactCountPerRequest - ").append(maxAllowedContactCountPerRequest)
                 .append(", contactCountInRequest - ").append(contactCountInRequest)
                .append(", subsGroupCountMap - ").append(KnGDPRTemplate.mapKeyMdn(subsGroupCountMap))
        		.append(", isSublistExistInCorporate - ").append(isSublistExistInCorporate)
        		.append(", configuredProfileMap - ").append(configuredProfileMap)
        		.append(", isSublstInDispGrp - ").append(isSublstInDispGrp)
                .append(", inputProfileIdList - ").append(inputProfileIdList)
                .append(", isSublistInGroup - ").append(isSublistInGroup)
                .append(", isSublistDistributed - ").append(isSublistDistributed)
                .append(", criClientList - ").append(criClientList)
                .append(", lastMemberSublistUPMMap - ").append(lastMemberSublistUPMMap)
                .append(", maxGroupsPerCRIClient - ").append(maxGroupsPerCRIClient)
                .append(", isCommContactListSupp - ").append(isCommContactListSupp)
                .append(", cmnContactListSize - ").append(cmnContactListSize)
                .append(", totalCommonContactListMemCount - ").append(totalCommonContactListMemCount)
                .append(", isCommonContactList - ").append(isCommonContactList)
                .append(", isNonAutoPair - ").append(isNonAutoPair)
                .append(", listDistribution - ").append(listDistribution)
                .append(", subscrsCorpIds - ").append(subscrsCorpIds)
                .append(", subListCorpId - ").append(subListCorpId)
                .append(", commCnctListCountPerSub - ").append(commCnctListCountPerSub)
                .append(", sysMaxAllowedCommCnctListPerSub - ").append(sysMaxAllowedCommCnctListPerSub)
                .append(", unAssignedSublistMdns - ").append(KnGDPRTemplate.mdnList(unAssignedSublistMdns));

        return sb.toString();

    }
}