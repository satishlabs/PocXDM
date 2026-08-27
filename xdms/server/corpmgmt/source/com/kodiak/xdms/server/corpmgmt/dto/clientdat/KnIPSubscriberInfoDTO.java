/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnIPSubscriberInfoDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar      Oct 15, 2015      8.1
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
package com.kodiak.xdms.server.corpmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KnIPSubscriberInfoDTO extends KnIPSubsDTO {

    private int eTag;
    private String corpId;
    private Map<String, Object> customParamMap;
    private String subscrName;
    private int subscriptionType;
    private String subscriberEmail;
    private List<String> mdnList;
    private long lastProfileUpdateTime;
    private String dispatchType;
    private String userId;
    private String clientPassword;
    private int serviceAuthStatus;
    private int appId;
    private int serviceAuthStatusAU;
    private String aliasMdn;
    private int tmpPwdMode;
    private String toMdn;
    private String fromMdn;
    private Collection<Integer> groupIds;
    private String userProfileId;
    private int sendAccountMail;

    private String cloningBitset;
    private String pttSettingDocId;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public int getEtag() {
        return eTag;
    }

    public void setEtag(int eTag) {
        this.eTag = eTag;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String getSubscrName() {
        return subscrName;
    }

    public void setSubscrName(String subscrName) {
        this.subscrName = subscrName;
    }

    public int getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(int subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    public String getSubscriberEmail() {
        return subscriberEmail;
    }

    public void setSubscriberEmail(String subscriberEmail) {
        this.subscriberEmail = subscriberEmail;
    }

    public long getLastProfileUpdateTime() {
        return lastProfileUpdateTime;
    }

    public void setLastProfileUpdateTime(long lastProfileUpdateTime) {
        this.lastProfileUpdateTime = lastProfileUpdateTime;
    }

    public String getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(String dispatchType) {
        this.dispatchType = dispatchType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientPassword() {
        return clientPassword;
    }

    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getServiceAuthStatusAU() {
        return serviceAuthStatusAU;
    }

    public void setServiceAuthStatusAU(int serviceAuthStatusAU) {
        this.serviceAuthStatusAU = serviceAuthStatusAU;
    }

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

    public int getTmpPwdMode() {
        return tmpPwdMode;
    }

    public void setTmpPwdMode(int tmpPwdMode) {
        this.tmpPwdMode = tmpPwdMode;
    }

    public String getToMdn() {
        return toMdn;
    }

    public void setToMdn(String toMdn) {
        this.toMdn = toMdn;
    }

    public String getFromMdn() {
        return fromMdn;
    }

    public void setFromMdn(String fromMdn) {
        this.fromMdn = fromMdn;
    }

    public Collection<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Collection<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public String getUserProfileId() { return userProfileId; }

    public void setUserProfileId(String userProfileId) { this.userProfileId = userProfileId; }

    public int getSendAccountMail() {
        return sendAccountMail;
    }

    public void setSendAccountMail(int sendAccountMail) {
        this.sendAccountMail = sendAccountMail;
    }

    public String getCloningBitset() {
        return cloningBitset;
    }

    public void setCloningBitset(String cloningBitset) {
        this.cloningBitset = cloningBitset;
    }

    public String getPttSettingDocId() {
        return pttSettingDocId;
    }

    public void setPttSettingDocId(String pttSettingDocId) {
        this.pttSettingDocId = pttSettingDocId;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(super.toString())
                .append(", eTag - ").append(eTag)
                .append(", corpId - ").append(corpId)
                .append(", mdn - ").append(KnGDPRTemplate.mdn(getMdn()))
                .append(", customParamMap - ").append(customParamMap)
                .append(", mdnList - ").append(KnGDPRTemplate.mdnList(mdnList))
                .append(", subscriberEmail - ").append(KnGDPRTemplate.email(subscriberEmail))
                .append(", dispatchType - ").append(dispatchType)
                .append(", userId - ").append(userId)
                .append(", clientPassword - ").append(clientPassword)
                .append(", serviceAuthStatus - ").append(serviceAuthStatus)
                .append(", serviceAuthStatusAU - ").append(serviceAuthStatusAU)
                .append(", appId - ").append(appId)
                .append(", aliasMdn - ").append(KnGDPRTemplate.mdn(aliasMdn))
                .append(", tmpPwdMode - ").append(tmpPwdMode)
                .append(", toMdn - ").append(KnGDPRTemplate.mdn(toMdn))
                .append(", fromMdn - ").append(KnGDPRTemplate.mdn(fromMdn))
                .append(", groupIds - ").append(groupIds)
                .append(", userProfileId - ").append(userProfileId)
                .append(", sendAccountMail - ").append(sendAccountMail)
                .append(", pttSettingDocId - ").append(pttSettingDocId);
        return sb.toString();
    }
}
