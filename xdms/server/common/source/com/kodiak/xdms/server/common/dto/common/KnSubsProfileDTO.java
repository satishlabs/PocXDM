/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnSubsProfileDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 12, 2011           7.0
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
public class KnSubsProfileDTO extends KnProfileDTO {

    private static final long serialVersionUID = 7526471155622676180L;


    private int publicSubscriptionType;
    private int corpSubscriptionType; //need to update query to retrieve these as well
    private int contactListId;
    private String subscriberEmail;
    private int clientType;
    private String protocolVersion;
    private int pamAccId;
    private int dispatchType;
    private String userId;
    private String subscriberName;
    private String clientPassowrd;
    private String userAgent;
    private int clientMinorVersion;
    private int clientMajorVersion;
    private int emergCallType;
    private int emergCancelPermission;
    private int emergInitiatePermission;
    private int emergLmrBehaviour;
    private int licenseType;
    private String aliasMdn;
    private int mcpttCompliance;
    private String mcpttId;
    private String mcVideoId;
    private String mcDataId;
    private String mcId;
    private int maxSDDSession;
    private int maxSDYSession;
    private String ufmi;
    private String clientFS2;
    private String activeFS2;
    private String subscriberFS2;
    private Integer qpppackId;
    private Integer userProfileIndex;
    private Boolean defaultProfile;
    private long lastProfileUpdateTime;
    private int discreetEnabled;
    private String userProfileId;
    private String derivedKey;
    private String opsFS2;
    private String corpAdminFS2;
    private String xdmsFS2;
    private String userProfileFS2;
    private String oldActiveFS2;
    private boolean isActiveFSUpdated;
    private boolean isUserAgentUpdated;
    private Integer cameraType;
    private String accountId;
    private String emergConfigTimer;
    private String pocHome;
    private String xdmsHome;
    private int corporateSubscriptionType;
    private Boolean pairingIndicator;
    private String affiliateId;
    private String IMEI;
    private int payType;
    private long subsCreationTime;
    private int corpContactListId;
    private String emailAddress;
    private String clientPassword;
    private Integer subsClientType;
    private Integer dispatchGroupMember;
    private int clientPVmajorVer;
    private int clientPVminorVer;
    private int vocoderId;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private Long lastActivationTime;
    private String iDenUserName;
    private String iDenPassword;
    private String iDenBusUnitId;
    private int poCStatusAU;
    private int poCStatusOP;
    private Integer serviceStatusOp;
    private Integer serviceStatusAuthUser;
    private Integer previousServiceAuthStatus;
    private int qppPkgId;
    private String firstNetIndicator;
    private String subsFS2;
    private String extGatewayId;
    private int isDefaultProfile;
    private String featureRelVersion;
    private int swType;
    private int platformType;
    private int dynamicQosFlag;
    private int privacyOptStatus;
    private String hierarchyId;


    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public String getSubscriberEmail() {
        return subscriberEmail;
    }

    public void setSubscriberEmail(String subscriberEmail) {
        this.subscriberEmail = subscriberEmail;
    }

    public int getPublicSubscriptionType() {
        return publicSubscriptionType;
    }

    public void setPublicSubscriptionType(int publicSubscriptionType) {
        this.publicSubscriptionType = publicSubscriptionType;
    }

    public int getCorpSubscriptionType() {
        return corpSubscriptionType;
    }

    public void setCorpSubscriptionType(int corpSubscriptionType) {
        this.corpSubscriptionType = corpSubscriptionType;
    }

    public int getContactListId() {
        return contactListId;
    }

    public void setContactListId(int contactListId) {
        this.contactListId = contactListId;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getPamAccId() {
        return pamAccId;
    }

    public void setPamAccId(int pamAccId) {
        this.pamAccId = pamAccId;
    }

    public int getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(int dispatchType) {
        this.dispatchType = dispatchType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubscriberName() {
        return subscriberName;
    }

    public void setSubscriberName(String subscriberName) {
        this.subscriberName = subscriberName;
    }

    public String getClientPassowrd() {
        return clientPassowrd;
    }

    public void setClientPassowrd(String clientPassowrd) {
        this.clientPassowrd = clientPassowrd;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getClientMajorVersion() {
        return clientMajorVersion;
    }

    public void setClientMajorVersion(int clientMajorVersion) {
        this.clientMajorVersion = clientMajorVersion;
    }

    public int getEmergCallType() {
        return emergCallType;
    }

    public void setEmergCallType(int emergCallType) {
        this.emergCallType = emergCallType;
    }

    public int getEmergCancelPermission() {
        return emergCancelPermission;
    }

    public void setEmergCancelPermission(int emergCancelPermission) {
        this.emergCancelPermission = emergCancelPermission;
    }

    public int getEmergInitiatePermission() {
        return emergInitiatePermission;
    }

    public void setEmergInitiatePermission(int emergInitiatePermission) {
        this.emergInitiatePermission = emergInitiatePermission;
    }

    public int getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(int licenseType) {
        this.licenseType = licenseType;
    }

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

    public int getEmergLmrBehaviour() {
        return emergLmrBehaviour;
    }

    public void setEmergLmrBehaviour(int emergLmrBehaviour) {
        this.emergLmrBehaviour = emergLmrBehaviour;
    }

    public int getMcpttCompliance() {
        return mcpttCompliance;
    }

    public void setMcpttCompliance(int mcpttCompliance) {
        this.mcpttCompliance = mcpttCompliance;
    }

    public String getMcpttId() {
        return mcpttId;
    }

    public void setMcpttId(String mcpttId) {
        this.mcpttId = mcpttId;
    }

    public int getMaxSDDSession() {
        return maxSDDSession;
    }

    public void setMaxSDDSession(int maxSDDSession) {
        this.maxSDDSession = maxSDDSession;
    }

    public int getMaxSDYSession() {
        return maxSDYSession;
    }

    public void setMaxSDYSession(int maxSDYSession) {
        this.maxSDYSession = maxSDYSession;
    }

    public String getUfmi() {
        return ufmi;
    }

    public void setUfmi(String ufmi) {
        this.ufmi = ufmi;
    }

    public String getActiveFS2() {
        return activeFS2;
    }

    public void setActiveFS2(String activeFS2) {
        this.activeFS2 = activeFS2;
    }

    public String getSubscriberFS2() {
        return subscriberFS2;
    }

    public void setSubscriberFS2(String subscriberFS2) {
        this.subscriberFS2 = subscriberFS2;
    }

    public String getMcVideoId() {
        return mcVideoId;
    }

    public void setMcVideoId(String mcVideoId) {
        this.mcVideoId = mcVideoId;
    }

    public String getMcDataId() {
        return mcDataId;
    }

    public void setMcDataId(String mcDataId) {
        this.mcDataId = mcDataId;
    }

    public String getMcId() {
        return mcId;
    }

    public void setMcId(String mcId) {
        this.mcId = mcId;
    }

    public Integer getQpppackId() {
        return qpppackId;
    }

    public void setQpppackId(Integer qpppackId) {
        this.qpppackId = qpppackId;
    }


    public int getClientMinorVersion() {
        return clientMinorVersion;
    }

    public void setClientMinorVersion(int clientMinorVersion) {
        this.clientMinorVersion = clientMinorVersion;
    }

    public String getClientFS2() {
        return clientFS2;
    }

    public void setClientFS2(String clientFS2) {
        this.clientFS2 = clientFS2;
    }

    public Integer getUserProfileIndex() {
        return userProfileIndex;
    }

    public void setUserProfileIndex(Integer userProfileIndex) {
        this.userProfileIndex = userProfileIndex;
    }

    public Boolean getDefaultProfile() {
        return defaultProfile;
    }

    public void setDefaultProfile(Boolean defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    public long getLastProfileUpdateTime() {
        return lastProfileUpdateTime;
    }

    public void setLastProfileUpdateTime(long lastProfileUpdateTime) {
        this.lastProfileUpdateTime = lastProfileUpdateTime;
    }

    public int getDiscreetEnabled() {
        return discreetEnabled;
    }

    public void setDiscreetEnabled(int discreetEnabled) {
        this.discreetEnabled = discreetEnabled;
    }

    public String getOpsFS2() {
        return opsFS2;
    }

    public void setOpsFS2(String opsFS2) {
        this.opsFS2 = opsFS2;
    }

    public String getCorpAdminFS2() {
        return corpAdminFS2;
    }

    public void setCorpAdminFS2(String corpAdminFS2) {
        this.corpAdminFS2 = corpAdminFS2;
    }

    public String getXdmsFS2() {
        return xdmsFS2;
    }

    public void setXdmsFS2(String xdmsFS2) {
        this.xdmsFS2 = xdmsFS2;
    }

    public String getUserProfileFS2() {
        return userProfileFS2;
    }

    public void setUserProfileFS2(String userProfileFS2) {
        this.userProfileFS2 = userProfileFS2;
    }

    public String getDerivedKey() {
        return derivedKey;
    }

    public void setDerivedKey(String derivedKey) {
        this.derivedKey = derivedKey;
    }

    public String getOldActiveFS2() {
        return oldActiveFS2;
    }

    public void setOldActiveFS2(String oldActiveFS2) {
        this.oldActiveFS2 = oldActiveFS2;
    }

    public boolean isActiveFSUpdated() {
        return isActiveFSUpdated;
    }

    public void setActiveFSUpdated(boolean activeFSUpdated) {
        isActiveFSUpdated = activeFSUpdated;
    }

    public boolean isUserAgentUpdated() {
        return isUserAgentUpdated;
    }

    public void setUserAgentUpdated(boolean userAgentUpdated) {
        isUserAgentUpdated = userAgentUpdated;
    }

    public Integer getCameraType() {
        return cameraType;
    }

    public void setCameraType(Integer cameraType) {
        this.cameraType = cameraType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getEmergConfigTimer() {
        return emergConfigTimer;
    }

    public void setEmergConfigTimer(String emergConfigTimer) {
        this.emergConfigTimer = emergConfigTimer;
    }

    public int getPrivacyOptStatus() {
        return privacyOptStatus;
    }

    public void setPrivacyOptStatus(int privacyOptStatus) {
        this.privacyOptStatus = privacyOptStatus;
    }

    public int getDynamicQosFlag() {
        return dynamicQosFlag;
    }

    public void setDynamicQosFlag(int dynamicQosFlag) {
        this.dynamicQosFlag = dynamicQosFlag;
    }

    public int getPlatformType() {
        return platformType;
    }

    public void setPlatformType(int platformType) {
        this.platformType = platformType;
    }

    public int getSwType() {
        return swType;
    }

    public void setSwType(int swType) {
        this.swType = swType;
    }

    public String getFeatureRelVersion() {
        return featureRelVersion;
    }

    public void setFeatureRelVersion(String featureRelVersion) {
        this.featureRelVersion = featureRelVersion;
    }

    public int getIsDefaultProfile() {
        return isDefaultProfile;
    }

    public void setIsDefaultProfile(int isDefaultProfile) {
        this.isDefaultProfile = isDefaultProfile;
    }

    public String getExtGatewayId() {
        return extGatewayId;
    }

    public void setExtGatewayId(String extGatewayId) {
        this.extGatewayId = extGatewayId;
    }

    public String getSubsFS2() {
        return subsFS2;
    }

    public void setSubsFS2(String subsFS2) {
        this.subsFS2 = subsFS2;
    }

    public String getFirstNetIndicator() {
        return firstNetIndicator;
    }

    public void setFirstNetIndicator(String firstNetIndicator) {
        this.firstNetIndicator = firstNetIndicator;
    }

    public int getQppPkgId() {
        return qppPkgId;
    }

    public void setQppPkgId(int qppPkgId) {
        this.qppPkgId = qppPkgId;
    }

    public Integer getPreviousServiceAuthStatus() {
        return previousServiceAuthStatus;
    }

    public void setPreviousServiceAuthStatus(Integer previousServiceAuthStatus) {
        this.previousServiceAuthStatus = previousServiceAuthStatus;
    }

    public Integer getServiceStatusAuthUser() {
        return serviceStatusAuthUser;
    }

    public void setServiceStatusAuthUser(Integer serviceStatusAuthUser) {
        this.serviceStatusAuthUser = serviceStatusAuthUser;
    }

    public Integer getServiceStatusOp() {
        return serviceStatusOp;
    }

    public void setServiceStatusOp(Integer serviceStatusOp) {
        this.serviceStatusOp = serviceStatusOp;
    }

    public int getPoCStatusOP() {
        return poCStatusOP;
    }

    public void setPoCStatusOP(int poCStatusOP) {
        this.poCStatusOP = poCStatusOP;
    }

    public int getPoCStatusAU() {
        return poCStatusAU;
    }

    public void setPoCStatusAU(int poCStatusAU) {
        this.poCStatusAU = poCStatusAU;
    }

    public String getiDenBusUnitId() {
        return iDenBusUnitId;
    }

    public void setiDenBusUnitId(String iDenBusUnitId) {
        this.iDenBusUnitId = iDenBusUnitId;
    }

    public String getiDenPassword() {
        return iDenPassword;
    }

    public void setiDenPassword(String iDenPassword) {
        this.iDenPassword = iDenPassword;
    }

    public String getiDenUserName() {
        return iDenUserName;
    }

    public void setiDenUserName(String iDenUserName) {
        this.iDenUserName = iDenUserName;
    }

    public Long getLastActivationTime() {
        return lastActivationTime;
    }

    public void setLastActivationTime(Long lastActivationTime) {
        this.lastActivationTime = lastActivationTime;
    }

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public int getVocoderId() {
        return vocoderId;
    }

    public void setVocoderId(int vocoderId) {
        this.vocoderId = vocoderId;
    }

    public int getClientPVminorVer() {
        return clientPVminorVer;
    }

    public void setClientPVminorVer(int clientPVminorVer) {
        this.clientPVminorVer = clientPVminorVer;
    }

    public int getClientPVmajorVer() {
        return clientPVmajorVer;
    }

    public void setClientPVmajorVer(int clientPVmajorVer) {
        this.clientPVmajorVer = clientPVmajorVer;
    }

    public Integer getDispatchGroupMember() {
        return dispatchGroupMember;
    }

    public void setDispatchGroupMember(Integer dispatchGroupMember) {
        this.dispatchGroupMember = dispatchGroupMember;
    }

    public Integer getSubsClientType() {
        return subsClientType;
    }

    public void setSubsClientType(Integer subsClientType) {
        this.subsClientType = subsClientType;
    }

    public String getClientPassword() {
        return clientPassword;
    }

    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public int getCorpContactListId() {
        return corpContactListId;
    }

    public void setCorpContactListId(int corpContactListId) {
        this.corpContactListId = corpContactListId;
    }

    public long getSubsCreationTime() {
        return subsCreationTime;
    }

    public void setSubsCreationTime(long subsCreationTime) {
        this.subsCreationTime = subsCreationTime;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getAffiliateId() {
        return affiliateId;
    }

    public void setAffiliateId(String affiliateId) {
        this.affiliateId = affiliateId;
    }

    public Boolean getPairingIndicator() {
        return pairingIndicator;
    }

    public void setPairingIndicator(Boolean pairingIndicator) {
        this.pairingIndicator = pairingIndicator;
    }

    public int getCorporateSubscriptionType() {
        return corporateSubscriptionType;
    }

    public void setCorporateSubscriptionType(int corporateSubscriptionType) {
        this.corporateSubscriptionType = corporateSubscriptionType;
    }

    @Override
    public String getXdmsHome() {
        return xdmsHome;
    }

    @Override
    public void setXdmsHome(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    @Override
    public String getPocHome() {
        return pocHome;
    }

    @Override
    public void setPocHome(String pocHome) {
        this.pocHome = pocHome;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(super.toString());
        strBuffer.append(", PublicSubscriptionType - ").append(publicSubscriptionType);
        strBuffer.append(", CorpSubscriptionType - ").append(corpSubscriptionType);
        strBuffer.append(", ContactListId - ").append(contactListId);
        strBuffer.append(", subscriberEmail - ").append(KnGDPRTemplate.email(subscriberEmail));
        strBuffer.append(", clientType - ").append(clientType);
        strBuffer.append(", protocolVersion - ").append(protocolVersion);
        strBuffer.append(", pamAccId - ").append(pamAccId);
        strBuffer.append(", dispatchType - ").append(dispatchType);
        strBuffer.append(", userId - ").append(KnGDPRTemplate.userId(userId));
        strBuffer.append(", clientPassowrd - ").append(clientPassowrd);
        strBuffer.append(", userAgent - ").append(userAgent);
        strBuffer.append(", clientMajorVersion - ").append(clientMajorVersion);
        strBuffer.append(", subscriberFS2 - ").append(subscriberFS2);
        strBuffer.append(", clientFS2 - ").append(clientFS2);
        strBuffer.append(", opsFS2 - ").append(opsFS2);
        strBuffer.append(", corpAdminFS2 - ").append(corpAdminFS2);
        strBuffer.append(", xdmsFS2 - ").append(xdmsFS2);
        strBuffer.append(", userProfileFS2 - ").append(userProfileFS2);
        strBuffer.append(", activeFS2 - ").append(activeFS2);
        strBuffer.append(", emergCallType - ").append(emergCallType);
        strBuffer.append(", emergCancelPermission - ").append(emergCancelPermission);
        strBuffer.append(", emergInitiatePermission - ").append(emergInitiatePermission);
        strBuffer.append(", licenseType - ").append(licenseType);
        strBuffer.append(", aliasMdn - ").append(KnGDPRTemplate.mdn(aliasMdn));
        strBuffer.append(", emergLmrBehaviour - ").append(emergLmrBehaviour);
        strBuffer.append(", mcpttCompliance - ").append(mcpttCompliance);
        strBuffer.append(", mcpttId - ").append(KnGDPRTemplate.mcpttId(mcpttId));
        strBuffer.append(", mcId - ").append(KnGDPRTemplate.mcId(mcId));
        strBuffer.append(", mcDataId - ").append(KnGDPRTemplate.mcdataId(mcDataId));
        strBuffer.append(", mcVideoId - ").append(KnGDPRTemplate.mcvideoId(mcVideoId));
        strBuffer.append(", maxsimuldedeicatedsession - ").append(maxSDDSession);
        strBuffer.append(", maxsimuldynamicsession - ").append(maxSDYSession);
        strBuffer.append(", ufmi - ").append(ufmi);
        strBuffer.append(", qpppackId - ").append(qpppackId);
        strBuffer.append(", userProfileIndex - ").append(userProfileIndex);
        strBuffer.append(", lastProfileUpdateTime - ").append(lastProfileUpdateTime);
        strBuffer.append(", discreetEnabled - ").append(discreetEnabled);
        strBuffer.append(", userProfileId - ").append(userProfileId);
        strBuffer.append(", oldActiveFS2 - ").append(oldActiveFS2);
        strBuffer.append(", isActiveFSUpdated - ").append(isActiveFSUpdated);
        strBuffer.append(", isUserAgentUpdated - ").append(isUserAgentUpdated);
        strBuffer.append(", cameraType - ").append(cameraType);
        strBuffer.append(", accountId - ").append(accountId);
        strBuffer.append(", emergConfigTimer - ").append(emergConfigTimer);
        strBuffer.append(", corpContactListId - ").append(corpContactListId);
        strBuffer.append(", hierarchyId - ").append(hierarchyId);
        return strBuffer.toString();
    }

}
