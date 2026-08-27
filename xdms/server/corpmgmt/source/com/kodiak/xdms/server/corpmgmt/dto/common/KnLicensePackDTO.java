/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

import java.util.Map;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnLicensePackDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     09/10/2014    7.10
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

public class KnLicensePackDTO implements Comparable<KnLicensePackDTO> {

    private String billingNumber;
    private String billingName;
    private int totalNoOfLines;
    private int corpId;
    private String corpName;
    private int subscriptionType;
    private int subsClientType;
    private int accountStatus;
    private String eTag;
    private int pamAccId;
    private Map<String, Object> customParamMap;
    private String subscriberFs2;

    public String getBillingNumber() {
        return billingNumber;
    }

    public void setBillingNumber(String billingNumber) {
        this.billingNumber = billingNumber;
    }

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    public int getTotalNoOfLines() {
        return totalNoOfLines;
    }

    public void setTotalNoOfLines(int totalNoOfLines) {
        this.totalNoOfLines = totalNoOfLines;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public int getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(int subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public int getSubsClientType() {
        return subsClientType;
    }

    public void setSubsClientType(int subsClientType) {
        this.subsClientType = subsClientType;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public int getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
    }

    public int getPamAccId() {
        return pamAccId;
    }

    public void setPamAccId(int pamAccId) {
        this.pamAccId = pamAccId;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String getSubscriberFs2() {
		return subscriberFs2;
	}

	public void setSubscriberFs2(String subscriberFs2) {
		this.subscriberFs2 = subscriberFs2;
	}

	@Override
    public int compareTo(KnLicensePackDTO o) {
        return 0;
    }
}
