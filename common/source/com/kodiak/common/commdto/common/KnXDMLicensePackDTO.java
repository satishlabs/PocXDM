/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Map;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMLicensePackDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Shaik Mahaaboob Basha 9/8/14        7.10
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

public class KnXDMLicensePackDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676133L;

    private String billingNumber;
    private String billingName;
    private int totalNoOfLines;
    private int corpId;
    private String extCorpID;
	private String corpName;
    private int subscriptionType;
    private int subsClientType;
    private String eTag;
    private Map<String, Object> customParamMap;
    private String email;
    private String imei;
    private String pamAccState;
    private Map<Integer,Integer> provFSMap;
    private long subscriberFs;
    private Map<String, Map<String,Integer>> pkgIdMap;
    private int licenseType;
    private String firstNetIndicator;
    private String subscriberFs2;
    
    public String getPamAccState() {
		return pamAccState;
	}

	public void setPamAccState(String pamAccState) {
		this.pamAccState = pamAccState;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getExtCorpID() {
		return extCorpID;
	}

	public void setExtCorpID(String extCorpID) {
		this.extCorpID = extCorpID;
	}

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

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public long getSubscriberFs() {
        return subscriberFs;
    }

    public void setSubscriberFs(long subscriberFs) {
        this.subscriberFs = subscriberFs;
    }

    @Override
    public String getObjectId() {
        return billingNumber;
    }


    public Map<Integer, Integer> getProvFSMap() {
        return provFSMap;
    }

    public void setProvFSMap(Map<Integer, Integer> provFSMap) {
        this.provFSMap = provFSMap;
    }
    

    public Map<String, Map<String, Integer>> getPkgIdMap() {
		return pkgIdMap;
	}

	public void setPkgIdMap(Map<String, Map<String, Integer>> pkgIdMap) {
		this.pkgIdMap = pkgIdMap;
	}

	public int getLicenseType() {
		return licenseType;
	}

	public void setLicenseType(int licenseType) {
		this.licenseType = licenseType;
	}

	public String getFirstNetIndicator() {
		return firstNetIndicator;
	}

	public void setFirstNetIndicator(String firstNetIndicator) {
		this.firstNetIndicator = firstNetIndicator;
	}

	public String getSubscriberFs2() {
		return subscriberFs2;
	}

	public void setSubscriberFs2(String subscriberFs2) {
		this.subscriberFs2 = subscriberFs2;
	}

	@Override
    public String toString() {
        StringBuilder strBuffer = new StringBuilder(100);
        strBuffer.append(" billingNumber - ").append(KnGDPRTemplate.mdn(billingNumber))
                .append(", billingName - ").append(KnGDPRTemplate.name(billingName))
                .append(", totalNoOfLines - ").append(totalNoOfLines)
                .append(", corpId - ").append(corpId)
                .append(", extCorpID - ").append(extCorpID)
                .append(", corpName - ").append(corpName)
                .append(", subscriptionType - ").append(subscriptionType)
                .append(", subsClientType - ").append(subsClientType)
                .append(", eTag - ").append(eTag)
                .append(", customParamMap - ").append(customParamMap)
                .append(", subscriberFs - ").append(subscriberFs)
                .append(", provFSMap - ").append(provFSMap)
                .append(", pkgIdMap - ").append(pkgIdMap)
                .append(", customParamMap - ").append(customParamMap)
                .append(", licenseType - ").append(licenseType)
                .append(", firstNetIndicator - ").append(firstNetIndicator)
                .append(", subscriberFs2 - ").append(subscriberFs2);

        return strBuffer.toString();
    }
}
