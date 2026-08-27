/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnMemberDTO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 7, 2011        7.0
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

public class KnMemberDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676216L;

    private String memberMdn;
    private String memberName;
    private String clientType;
    private Integer subscriptionType;
    private Integer corpId;
    private String ufmi;
    private String aliasMdn;
    private String userId;

    public String getMemberMdn() {
        return memberMdn;
    }

    public void setMemberMdn(String memberMdn) {
        this.memberMdn = memberMdn;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getUfmi() {
        return ufmi;
    }

    public void setUfmi(String ufmi) {
        this.ufmi = ufmi;
    }

    public Integer getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(Integer subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(50);
        strBuffer.append(super.toString());
        strBuffer.append(", MemberMdn - ").append(KnGDPRTemplate.mdn(memberMdn));
        strBuffer.append(", MemberName - ").append(KnGDPRTemplate.name(memberName));
        strBuffer.append(", ClientType - ").append(clientType);
        strBuffer.append(", SubscriptionType - ").append(subscriptionType);
        strBuffer.append(", CorpId - ").append(corpId);
        strBuffer.append(", ufmi - ").append(ufmi);
        strBuffer.append(", aliasMdn - ").append(KnGDPRTemplate.mdn(aliasMdn));
        strBuffer.append(", userId - ").append(KnGDPRTemplate.userId(userId));
        return strBuffer.toString();
    }

    public String getObjectId() {
        return memberMdn;
    }
}
