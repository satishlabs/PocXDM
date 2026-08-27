/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnContactDetailsPersistDTO.java
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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KnContactDetailsPersistDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676197L;

    private IInputDTO inputDTO;
    private String entityId;
    private String operationType;
    private String profile;
    private IPersistenceDTO persistenceDTO;

    private KnCorpSubscriberDTO subscDto;
    private Collection<String> pocSubscMdnList;
    private Collection<String> contactMdnList;
    private Collection<Integer> pocSublistIds;
    private Collection<Integer> subscSublistIds;
    private Collection<KnCorpSubscriberDTO> externalContacts;
    private int contactCount;
    private int currentContactCount;
    private int sublistContactCount;
    private int passedPrivateContactCount;
    private Collection<KnCorpSubscriberDTO> newlyAddedPrivateMembers;
    private Map<String, String> subscContactCnt = new HashMap<String, String>();
    private Map<String, Integer> subscAdditonalContactCnt = new HashMap<String, Integer>();
    private Map<String, String> subscSublistCnt = new HashMap<String, String>();
    private int maxSubscribersContactLimit;
    private Map<String, KnCorpSubscriberDTO> contactCorpDetails = new HashMap<String, KnCorpSubscriberDTO>();
    private long etag;
    private int subscriberCount;
    private String corpEtag;
    private int maxExtCorporateMembers;
    private int contactCountInRequest;
    private int maxAllowedContactCountPerRequest;
    private Map<String, Integer> mdnClientTypeMap;
    private Collection<KnCorpSubscriberDTO> addedMdnDTO;
    private int subsCorpId;
    //To hold the max configured external subscribers per corp
    private int maxExtSubs;
    //To hold the current external subscriber count in the corp
    private int currentExtSubsCount;
    //To hold the input external subscriber list
    private List<KnCorpSubscriberDTO> extSubsList;
    //To hold the all configured profile details for external subs.
    private Map<Integer, KnExtProfileDetails> configuredProfileMap;
    //To hold the all input profileId list
    private List<Integer> inputProfileIdList;
    //To hold the kodiak poc subscr list
    private List<KnCorpSubscriberDTO> extContList;
    private String allowedClientTypes ;
    // WebDispatcher Changes:
    private int dbDispatchType;
    private String dbUserID;
    private int webDispatcherEnabled;
    private boolean userIdExists;
    private boolean webDispatcherFlag;
    private int serviceAuthStatus;
    private int serviceAuthStatusAU;
    private int licenseType;
    private boolean aliasMdnExists;
    private Collection<String> aliasMdnList;
    private Map<String, String> aliasMdnMap;
    private Collection<String> userIdList;
    private Map<String, String> userIdMap;
    private String clientDBPassword;
    private int deviceSharingFlag;
    private String aliasMdn;
    private int mcpttCompliance;
    private int onBoardingEmailReqd;
    private String mcpttId;
    private boolean isUPMSharingEnabled;
    private boolean pttRecordingFlag;
    private boolean dataRecordingFlag;
    private boolean videoRecordingFlag;
    private int commonContactCount;
    private int maxAllowedCommonContactCount;
    private boolean isCommonContactEnabled;
    private int maxAlloedCommonSublistPerSubs;
    private Map<Integer,Integer> subListIdInfo;
    private boolean sysSelfDndPrivilege;
    private boolean corpSelfDndPrivilege;
    private List<Integer> reqSelfDndPrivilegeList;
    private boolean sysLargeAgencyDispatch;
    private boolean corpLargeAgencyDispatch;
    private List<Integer> reqLargeAgencyDispatchList;
    private int maxCorpGroupPerSubs;
    private int subsCorpGrpCount;
    private int clientType;

    public boolean isUPMSharingEnabled() {
        return isUPMSharingEnabled;
    }

    public void setUPMSharingEnabled(boolean UPMSharingEnabled) {
        isUPMSharingEnabled = UPMSharingEnabled;
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
        return subscDto.getMdn();
    }

    public KnCorpSubscriberDTO getSubscDto() {
        return subscDto;
    }

    public void setSubscDto(KnCorpSubscriberDTO subscDto) {
        this.subscDto = subscDto;
    }

    public Collection<String> getPocSubscMdnList() {
        return pocSubscMdnList;
    }

    public void setPocSubscMdnList(Collection<String> pocSubscMdnList) {
        this.pocSubscMdnList = pocSubscMdnList;
    }

    public Collection<String> getContactMdnList() {
        return contactMdnList;
    }

    public void setContactMdnList(Collection<String> contactMdnList) {
        this.contactMdnList = contactMdnList;
    }


    public Collection<Integer> getPocSublistIds() {
        return pocSublistIds;
    }

    public void setPocSublistIds(Collection<Integer> pocSublistIds) {
        this.pocSublistIds = pocSublistIds;
    }

    public Collection<Integer> getSubscSublistIds() {
        return subscSublistIds;
    }

    public void setSubscSublistIds(Collection<Integer> subscSublistIds) {
        this.subscSublistIds = subscSublistIds;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }

    public int getSublistContactCount() {
        return sublistContactCount;
    }

    public void setSublistContactCount(int sublistContactCount) {
        this.sublistContactCount = sublistContactCount;
    }

    public Collection<KnCorpSubscriberDTO> getExternalContacts() {
        return externalContacts;
    }

    public void setExternalContacts(Collection<KnCorpSubscriberDTO> externalContacts) {
        this.externalContacts = externalContacts;
    }

    public int getPassedPrivateContactCount() {
        return passedPrivateContactCount;
    }

    public void setPassedPrivateContactCount(int passedPrivateContactCount) {
        this.passedPrivateContactCount = passedPrivateContactCount;
    }

    public Collection<KnCorpSubscriberDTO> getNewlyAddedPrivateMembers() {
        return newlyAddedPrivateMembers;
    }

    public void setNewlyAddedPrivateMembers(Collection<KnCorpSubscriberDTO> newlyAddedPrivateMembers) {
        this.newlyAddedPrivateMembers = newlyAddedPrivateMembers;
    }

    public Map<String, String> getSubscContactCnt() {
        return subscContactCnt;
    }

    public void setSubscContactCnt(Map<String, String> subscContactCnt) {
        this.subscContactCnt = subscContactCnt;
    }

    public int getCurrentContactCount() {
        return currentContactCount;
    }

    public void setCurrentContactCount(int currentContactCount) {
        this.currentContactCount = currentContactCount;
    }

    public Map<String, Integer> getSubscAdditonalContactCnt() {
        return subscAdditonalContactCnt;
    }

    public void setSubscAdditonalContactCnt(Map<String, Integer> subscAdditonalContactCnt) {
        this.subscAdditonalContactCnt = subscAdditonalContactCnt;
    }

    public int getMaxSubscribersContactLimit() {
        return maxSubscribersContactLimit;
    }

    public void setMaxSubscribersContactLimit(int maxSubscribersContactLimit) {
        this.maxSubscribersContactLimit = maxSubscribersContactLimit;
    }

    public Map<String, String> getSubscSublistCnt() {
        return subscSublistCnt;
    }

    public void setSubscSublistCnt(Map<String, String> subscSublistCnt) {
        this.subscSublistCnt = subscSublistCnt;
    }

    public Map<String, KnCorpSubscriberDTO> getContactCorpDetails() {
        return contactCorpDetails;
    }

    public void setContactCorpDetails(Map<String, KnCorpSubscriberDTO> contactCorpDetails) {
        this.contactCorpDetails = contactCorpDetails;
    }

    public long getEtag() {
        return etag;
    }

    public void setEtag(long etag) {
        this.etag = etag;
    }

    public String getCorpEtag() {
        return corpEtag;
    }

    public void setCorpEtag(String corpEtag) {
        this.corpEtag = corpEtag;
    }

    public int getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(int subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public int getMaxExtCorporateMembers() {
        return maxExtCorporateMembers;
    }

    public void setMaxExtCorporateMembers(int maxExtCorporateMembers) {
        this.maxExtCorporateMembers = maxExtCorporateMembers;
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

    public Map<String, Integer> getMdnClientTypeMap() {
        return mdnClientTypeMap;
    }

    public void setMdnClientTypeMap(Map<String, Integer> mdnClientTypeMap) {
        this.mdnClientTypeMap = mdnClientTypeMap;
    }

    public Collection<KnCorpSubscriberDTO> getAddedMdnDTO() {
        return addedMdnDTO;
    }

    public void setAddedMdnDTO(Collection<KnCorpSubscriberDTO> addedMdnDTO) {
        this.addedMdnDTO = addedMdnDTO;
    }

    public int getSubsCorpId() {
        return subsCorpId;
    }

    public void setSubsCorpId(int subsCorpId) {
        this.subsCorpId = subsCorpId;
    }

    public int getMaxExtSubs() {
        return maxExtSubs;
    }

    public void setMaxExtSubs(int maxExtSubs) {
        this.maxExtSubs = maxExtSubs;
    }

    public int getCurrentExtSubsCount() {
        return currentExtSubsCount;
    }

    public void setCurrentExtSubsCount(int currentExtSubsCount) {
        this.currentExtSubsCount = currentExtSubsCount;
    }

    public List<KnCorpSubscriberDTO> getExtSubsList() {
        return extSubsList;
    }

    public void setExtSubsList(List<KnCorpSubscriberDTO> extSubsList) {
        this.extSubsList = extSubsList;
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

    public List<KnCorpSubscriberDTO> getExtContList() {
        return extContList;
    }

    public void setExtContList(List<KnCorpSubscriberDTO> extContList) {
        this.extContList = extContList;
    }

    public String getAllowedClientTypes() {
        return allowedClientTypes;
    }

    public void setAllowedClientTypes(String allowedClientTypes) {
        this.allowedClientTypes = allowedClientTypes;
    }

    public int getDbDispatchType() {
        return dbDispatchType;
    }

    public void setDbDispatchType(int dbDispatchType) {
        this.dbDispatchType = dbDispatchType;
    }

    public String getDbUserID() {
        return dbUserID;
    }

    public void setDbUserID(String dbUserID) {
        this.dbUserID = dbUserID;
    }

    public int getWebDispatcherEnabled() {
        return webDispatcherEnabled;
    }

    public void setWebDispatcherEnabled(int webDispatcherEnabled) {
        this.webDispatcherEnabled = webDispatcherEnabled;
    }

    public boolean isUserIdExists() {
        return userIdExists;
    }

    public void setUserIdExists(boolean userIdExists) {
        this.userIdExists = userIdExists;
    }

    public boolean isWebDispatcherFlag() {
        return webDispatcherFlag;
    }

    public void setWebDispatcherFlag(boolean webDispatcherFlag) {
        this.webDispatcherFlag = webDispatcherFlag;
    }

    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public int getServiceAuthStatusAU() {
        return serviceAuthStatusAU;
    }

    public void setServiceAuthStatusAU(int serviceAuthStatusAU) {
        this.serviceAuthStatusAU = serviceAuthStatusAU;
    }

    public int getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(int licenseType) {
        this.licenseType = licenseType;
    }

    public boolean isAliasMdnExists() {
        return aliasMdnExists;
    }

    public void setAliasMdnExists(boolean aliasMdnExists) {
        this.aliasMdnExists = aliasMdnExists;
    }

    public Collection<String> getAliasMdnList() {
        return aliasMdnList;
    }

    public void setAliasMdnList(Collection<String> aliasMdnList) {
        this.aliasMdnList = aliasMdnList;
    }

    public Map<String, String> getAliasMdnMap() {
        return aliasMdnMap;
    }

    public void setAliasMdnMap(Map<String, String> aliasMdnMap) {
        this.aliasMdnMap = aliasMdnMap;
    }

    public Collection<String> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(Collection<String> userIdList) {
        this.userIdList = userIdList;
    }

    public Map<String, String> getUserIdMap() {
        return userIdMap;
    }

    public void setUserIdMap(Map<String, String> userIdMap) {
        this.userIdMap = userIdMap;
    }

    public String getClientDBPassword() {
        return clientDBPassword;
    }

    public void setClientDBPassword(String clientDBPassword) {
        this.clientDBPassword = clientDBPassword;
    }

    public int getDeviceSharingFlag() {
        return deviceSharingFlag;
    }

    public void setDeviceSharingFlag(int deviceSharingFlag) {
        this.deviceSharingFlag = deviceSharingFlag;
    }

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

    public int getMcpttCompliance() { return mcpttCompliance; }

    public void setMcpttCompliance(int mcpttCompliance) { this.mcpttCompliance = mcpttCompliance; }

    public int getOnBoardingEmailReqd() {
        return onBoardingEmailReqd;
    }

    public void setOnBoardingEmailReqd(int onBoardingEmailReqd) {
        this.onBoardingEmailReqd = onBoardingEmailReqd;
    }

    public String getMcpttId() {
		return mcpttId;
	}

	public void setMcpttId(String mcpttId) {
		this.mcpttId = mcpttId;
	}
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

    public int getCommonContactCount() { return commonContactCount; }

    public void setCommonContactCount(int commonContactCount) { this.commonContactCount = commonContactCount; }

    public int getMaxAllowedCommonContactCount() { return maxAllowedCommonContactCount; }

    public void setMaxAllowedCommonContactCount(int maxAllowedCommonContactCount) { this.maxAllowedCommonContactCount = maxAllowedCommonContactCount; }

    public boolean isCommonContactEnabled() { return isCommonContactEnabled; }

    public void setCommonContactEnabled(boolean commonContactEnabled) { isCommonContactEnabled = commonContactEnabled; }

    public int getMaxAlloedCommonSublistPerSubs() { return maxAlloedCommonSublistPerSubs; }

    public void setMaxAlloedCommonSublistPerSubs(int maxAlloedCommonSublistPerSubs) { this.maxAlloedCommonSublistPerSubs = maxAlloedCommonSublistPerSubs; }

    public Map<Integer, Integer> getSubListIdInfo() { return subListIdInfo; }

    public void setSubListIdInfo(Map<Integer, Integer> subListIdInfo) { this.subListIdInfo = subListIdInfo; }

    public boolean isSysSelfDndPrivilege() { return sysSelfDndPrivilege; }

    public void setSysSelfDndPrivilege(boolean sysSelfDndPrivilege) { this.sysSelfDndPrivilege = sysSelfDndPrivilege; }

    public boolean isCorpSelfDndPrivilege() { return corpSelfDndPrivilege; }

    public void setCorpSelfDndPrivilege(boolean corpSelfDndPrivilege) { this.corpSelfDndPrivilege = corpSelfDndPrivilege; }

    public List<Integer> getReqSelfDndPrivilegeList() { return reqSelfDndPrivilegeList; }

    public void setReqSelfDndPrivilegeList(List<Integer> reqSelfDndPrivilegeList) { this.reqSelfDndPrivilegeList = reqSelfDndPrivilegeList; }

    public boolean getSysLargeAgencyDispatch() { return sysLargeAgencyDispatch; }

    public void setSysLargeAgencyDispatch(boolean sysLargeAgencyDispatch) { this.sysLargeAgencyDispatch = sysLargeAgencyDispatch; }

    public boolean getCorpLargeAgencyDispatch() { return corpLargeAgencyDispatch; }

    public void setCorpLargeAgencyDispatch(boolean corpLargeAgencyDispatch) { this.corpLargeAgencyDispatch = corpLargeAgencyDispatch; }

    public List<Integer> getReqLargeAgencyDispatchList() { return reqLargeAgencyDispatchList; }

    public void setReqLargeAgencyDispatchList(List<Integer> reqLargeAgencyDispatchList) {
        this.reqLargeAgencyDispatchList = reqLargeAgencyDispatchList;
    }

    public void setMaxCorpGroupPerSubs(int maxCorpGroupPerSubs) { this.maxCorpGroupPerSubs = maxCorpGroupPerSubs; }
    public int getMaxCorpGroupPerSubs() { return maxCorpGroupPerSubs; }

    public void setSubsCorpGrpCount(int subsCorpGrpCount) { this.subsCorpGrpCount = subsCorpGrpCount; }
    public int getSubsCorpGrpCount() { return subsCorpGrpCount; }

    public void setClientType(int clientType) { this.clientType = clientType; }
    public int getClientType() { return clientType; }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(500);
        sb.append("InputDTO - ").append(inputDTO)
                .append(", EntityId - ").append(entityId)
                .append(", operationType - ").append(operationType)
                .append(", subscDto - ").append(subscDto)
                .append(", pocSubscMdnList - ").append(KnGDPRTemplate.mdnList(pocSubscMdnList))
                .append(", contactMdnList - ").append(KnGDPRTemplate.mdnList(contactMdnList))
                .append(", pocSublistIds - ").append(pocSublistIds)
                .append(", subscSublistIds - ").append(subscSublistIds)
                .append(", externalContacts - ").append(externalContacts)
                .append(", contactCount - ").append(contactCount)
                .append(", currentContactCount - ").append(currentContactCount)
                .append(", sublistContactCount - ").append(sublistContactCount)
                .append(", passedPrivateContactCount - ").append(passedPrivateContactCount)
                .append(", newlyAddedPrivateMembers - ").append(newlyAddedPrivateMembers)
                .append(", subscContactCnt - ").append(subscContactCnt)
                .append(", subscAdditonalContactCnt - ").append(subscAdditonalContactCnt)
                .append(", subscSublistCnt - ").append(subscSublistCnt)
                .append(", maxSubscribersContactLimit - ").append(maxSubscribersContactLimit)
                .append(", contactCorpDetails - ").append(contactCorpDetails)
                .append(", etag - ").append(etag)
                .append(", corpEtag - ").append(corpEtag)
                .append(", subscriberCount - ").append(subscriberCount)
                .append(", maxExtCorporateMembers - ").append(maxExtCorporateMembers)
                .append(", contactCountPerRequest - ").append(contactCountInRequest)
                .append(", mdnClientTypeMap - ").append(KnGDPRTemplate.mapKeyMdn(mdnClientTypeMap))
                .append(", maxAllowedContactCountPerRequest - ").append(maxAllowedContactCountPerRequest)
                .append(", addedMdnDTO - ").append(addedMdnDTO)
                .append(", maxExtSubs - ").append(maxExtSubs)
                .append(", currentExtSubsCount - ").append(currentExtSubsCount)
                .append(", extSubsList - ").append(extSubsList)
                .append(", configuredProfileMap - ").append(configuredProfileMap)
                .append(", inputProfileIdList - ").append(inputProfileIdList)
                .append(", subsCorpId - ").append(subsCorpId)
                .append(", allowedClientTypes - ").append(allowedClientTypes)
                .append(", dbDispatchType - ").append(dbDispatchType)
                .append(", dbUserID - ").append(dbUserID)
                .append(", webDispatcherEnabled - ").append(webDispatcherEnabled)
                .append(", userIdExists - ").append(userIdExists)
                .append(", webDispatcherFlag - ").append(webDispatcherFlag)
                .append(", serviceAuthStatus - ").append(serviceAuthStatus)
                .append(", serviceAuthStatusAU - ").append(serviceAuthStatusAU)
                .append(", licenseType - ").append(licenseType)
                .append(", aliasMdnExists - ").append(aliasMdnExists)
                .append(", aliasMdnList - ").append(KnGDPRTemplate.mdnList(aliasMdnList))
                .append(", aliasMdnMap - ").append(KnGDPRTemplate.mdnMap(aliasMdnMap))
                .append(", userIdList - ").append(userIdList)
                .append(", userIdMap - ").append(userIdMap)
                .append(", clientDBPassword - ").append(clientDBPassword)
                .append(", deviceSharingFlag - ").append(deviceSharingFlag)
                .append(", aliasMdn - ").append(KnGDPRTemplate.mdn(aliasMdn))
                .append(", mcpttCompliance - ").append(mcpttCompliance)
                .append(", onBoardingEmailReqd - ").append(onBoardingEmailReqd)
        		.append(", mcpttId - ").append(KnGDPRTemplate.mcpttId(mcpttId))
                .append(", isUPMSharingEnabled - ").append(isUPMSharingEnabled)
                .append(", pttRecordingFlag - ").append(pttRecordingFlag)
                .append(", dataRecordingFlag - ").append(dataRecordingFlag)
                .append(", videoRecordingFlag - ").append(videoRecordingFlag)
                .append(", commonContactCount - ").append(commonContactCount)
                .append(", maxAllowedCommonContactCount - ").append(maxAllowedCommonContactCount)
                .append(", videoRecordingFlag - ").append(videoRecordingFlag)
                .append(", maxAlloedCommonSublistPerSubs - ").append(maxAlloedCommonSublistPerSubs)
                .append(", subListIdInfo - ").append(subListIdInfo)
                .append(", sysSelfDndPrivilege - ").append(sysSelfDndPrivilege)
                .append(", corpSelfDndPrivilege - ").append(corpSelfDndPrivilege)
                .append(", reqSelfDndPrivilegeList - ").append(reqSelfDndPrivilegeList)
                .append(", sysLargeAgencyDispatch - ").append(sysLargeAgencyDispatch)
                .append(", corpLargeAgencyDispatch - ").append(corpLargeAgencyDispatch)
                .append(", reqLargeAgencyDispatchList -").append(reqLargeAgencyDispatchList)
                .append(", clientType -").append(clientType);
        return sb.toString();
    }
}
