/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.Map;

/**
 * Created by schandra on 15-11-2016.
 */
public class KnCorpSubscrInfoRespDto extends KnCorpResponseDTO {
    //stores subscriber mdn
    private String mdn;
    //stores Subscriber IMEI;
    private String IMEI;
    //stores the subscriber name
    private String networkName;
    //stores the corp Id
    private String extCorpId;
    //stores the corp Id
    private String corpId;
    //stores the Subscription Type for public
    private int publicSubscriptionType;
    //stores the Subscription Type for Corporate
    private int corporateSubscriptionType;
    //stores the Affiliated Id
    private String affiliateId;
    //stores the pay Type
    private int payType;
    //stores the pairingInd
    private Boolean pairingInd;
    //stores the Roaming Types
    private Collection roamingType;
    //stores the Subscriber Feature Set of Prov Request
    private Long subsFeatureSet;
    //stores the emailAddress
    private String emailAddress;
    //stores the subscriberClientType;
    private Integer subscriberClientType;
    //stores Corporate Name
    private String corporateName;
    //stores ClientFS1
    private long clientFS1;
    //stores the dispatch group member status of the subscriber
    public Integer dispatchGroupMember;
    private String userAgent;
    //stores the Custom Data Map
    private Map<String, Object> customParamMap;
    //stores the ext Corp Id
    private String accountId;
    //stores activeFS
    private long activeFS;

    private long corpAdminFS;

    //stores the last activation time
    private Long lastActivationTime;

    private String billingMDN;

    private int roamingAllowed;

    private Boolean autoPair;

    private Integer dispListRr;
    private String pWsUri;
    private String gWsUri;
    private Integer wsCaeT;

    private String tpUser;
    private String tpAccount;
    private String pocStatus;
    private String apnType;
    private String clientPassword;
    private int clientPvMajorVersion;
    private int clientPvMinorVersion;
    private long subsCreationTime;
    //stores the XDMS Home of the Subscriber
    public String XDMSHome;
    //stores the poC Home of the subscriber
    public String poCHome;
    //stores the Presence Home of the Subscriber
    public String presenceHome;
    //stores the Service Auth Status of the Subscriber
    public int serviceAuthStatus;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public int getPublicSubscriptionType() {
        return publicSubscriptionType;
    }

    public void setPublicSubscriptionType(int publicSubscriptionType) {
        this.publicSubscriptionType = publicSubscriptionType;
    }

    public int getCorporateSubscriptionType() {
        return corporateSubscriptionType;
    }

    public void setCorporateSubscriptionType(int corporateSubscriptionType) {
        this.corporateSubscriptionType = corporateSubscriptionType;
    }

    public String getAffiliateId() {
        return affiliateId;
    }

    public void setAffiliateId(String affiliateId) {
        this.affiliateId = affiliateId;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public Boolean getPairingInd() {
        return pairingInd;
    }

    public void setPairingInd(Boolean pairingInd) {
        this.pairingInd = pairingInd;
    }

    public Collection getRoamingType() {
        return roamingType;
    }

    public void setRoamingType(Collection roamingType) {
        this.roamingType = roamingType;
    }

    public Long getSubsFeatureSet() {
        return subsFeatureSet;
    }

    public void setSubsFeatureSet(Long subsFeatureSet) {
        this.subsFeatureSet = subsFeatureSet;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Integer getSubscriberClientType() {
        return subscriberClientType;
    }

    public void setSubscriberClientType(Integer subscriberClientType) {
        this.subscriberClientType = subscriberClientType;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public long getClientFS1() {
        return clientFS1;
    }

    public void setClientFS1(long clientFS1) {
        this.clientFS1 = clientFS1;
    }

    public Integer getDispatchGroupMember() {
        return dispatchGroupMember;
    }

    public void setDispatchGroupMember(Integer dispatchGroupMember) {
        this.dispatchGroupMember = dispatchGroupMember;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public long getActiveFS() {
        return activeFS;
    }

    public void setActiveFS(long activeFS) {
        this.activeFS = activeFS;
    }

    public long getCorpAdminFS() {
        return corpAdminFS;
    }

    public void setCorpAdminFS(long corpAdminFS) {
        this.corpAdminFS = corpAdminFS;
    }

    public Long getLastActivationTime() {
        return lastActivationTime;
    }

    public void setLastActivationTime(Long lastActivationTime) {
        this.lastActivationTime = lastActivationTime;
    }

    public String getBillingMDN() {
        return billingMDN;
    }

    public void setBillingMDN(String billingMDN) {
        this.billingMDN = billingMDN;
    }

    public int getRoamingAllowed() {
        return roamingAllowed;
    }

    public void setRoamingAllowed(int roamingAllowed) {
        this.roamingAllowed = roamingAllowed;
    }

    public Boolean getAutoPair() {
        return autoPair;
    }

    public void setAutoPair(Boolean autoPair) {
        this.autoPair = autoPair;
    }

    public Integer getDispListRr() {
        return dispListRr;
    }

    public void setDispListRr(Integer dispListRr) {
        this.dispListRr = dispListRr;
    }

    public String getpWsUri() {
        return pWsUri;
    }

    public void setpWsUri(String pWsUri) {
        this.pWsUri = pWsUri;
    }

    public String getgWsUri() {
        return gWsUri;
    }

    public void setgWsUri(String gWsUri) {
        this.gWsUri = gWsUri;
    }

    public Integer getWsCaeT() {
        return wsCaeT;
    }

    public void setWsCaeT(Integer wsCaeT) {
        this.wsCaeT = wsCaeT;
    }

    public String getTpUser() {
        return tpUser;
    }

    public void setTpUser(String tpUser) {
        this.tpUser = tpUser;
    }

    public String getTpAccount() {
        return tpAccount;
    }

    public void setTpAccount(String tpAccount) {
        this.tpAccount = tpAccount;
    }

    public String getPocStatus() {
        return pocStatus;
    }

    public void setPocStatus(String pocStatus) {
        this.pocStatus = pocStatus;
    }

    public String getApnType() {
        return apnType;
    }

    public void setApnType(String apnType) {
        this.apnType = apnType;
    }

    public String getClientPassword() {
        return clientPassword;
    }

    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    public int getClientPvMajorVersion() {
        return clientPvMajorVersion;
    }

    public void setClientPvMajorVersion(int clientPvMajorVersion) {
        this.clientPvMajorVersion = clientPvMajorVersion;
    }

    public int getClientPvMinorVersion() {
        return clientPvMinorVersion;
    }

    public void setClientPvMinorVersion(int clientPvMinorVersion) {
        this.clientPvMinorVersion = clientPvMinorVersion;
    }

    public long getSubsCreationTime() {
        return subsCreationTime;
    }

    public void setSubsCreationTime(long subsCreationTime) {
        this.subsCreationTime = subsCreationTime;
    }

    public String getXDMSHome() {
        return XDMSHome;
    }

    public void setXDMSHome(String XDMSHome) {
        this.XDMSHome = XDMSHome;
    }

    public String getPoCHome() {
        return poCHome;
    }

    public void setPoCHome(String poCHome) {
        this.poCHome = poCHome;
    }

    public String getPresenceHome() {
        return presenceHome;
    }

    public void setPresenceHome(String presenceHome) {
        this.presenceHome = presenceHome;
    }

    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(" MDN - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", IMEI - ").append(IMEI)
                .append(", Network_Name - ").append(KnGDPRTemplate.name(networkName))
                .append(", ExtCorpId - ").append(extCorpId)
                .append(", Public_Subscription_Type - ").append(publicSubscriptionType)
                .append(", Corporate_Subscription_Type - ").append(corporateSubscriptionType)
                .append(", Affiliate_Id - ").append(affiliateId)
                .append(", Pay_Type - ").append(payType)
                .append(", Pairing_Indicator - ").append(pairingInd)
                .append(", Roaming_Types - ").append(roamingType)
                .append(", Subscriber_Feature_Set - ").append(subsFeatureSet)
                .append(", eMail_Address - ").append(KnGDPRTemplate.email(emailAddress))
                .append(", Subscriber_Client_Type - ").append(subscriberClientType)
                .append(", Dispatch_Group_Member - ").append(dispatchGroupMember)
                .append(", Corporate_Name - ").append(corporateName)
                .append(", client_FS1 - ").append(clientFS1)
                .append(", active_FS - ").append(activeFS)
                .append(", corp_Admin_FS - ").append(corpAdminFS)
                .append(", Account_Id - ").append(accountId)
                .append(", userAgent - ").append(userAgent)
                .append(", Custom_Param_Map - ").append(customParamMap)
                .append(", Corp Id -").append(corpId)
                .append(", lastActivationTime -").append(lastActivationTime)
                .append(", billingMDN -").append(KnGDPRTemplate.mdn(billingMDN))
                .append(", roamingAllowed -").append(roamingAllowed)
                .append(", autoPair -").append(autoPair)
                .append(", tpUser -").append(tpUser)
                .append(", tpAccount -").append(tpAccount)
                .append(", disp-list-rr ").append(dispListRr)
                .append(", p-ws-uri ").append(pWsUri)
                .append(", g-ws-uri ").append(gWsUri)
                .append(", ws-cae-t ").append(wsCaeT)
                .append(", pocStatus ").append(pocStatus)
                .append(", apnType ").append(apnType)
                .append(", clientPassword ").append(clientPassword)
                .append(", clientPvMajorVersion ").append(clientPvMajorVersion)
                .append(", clientPvMinorVersion ").append(clientPvMinorVersion);
        return strBuffer.toString();
    }
}
