/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.List;
import java.util.Map;

/**
 * *****************************************************************************
 * File name:   KnXDMPAMSubsProfInfoDTO.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar          08/02/2013       7.4
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
 * *******************************************************************************
 */
public class KnXDMPAMSubsProfInfoDTO implements IIdentifier {
    private static final long serialVersionUID = 7526471155622678009L;

    //stores the PAM Account Id
    private int pamAccId;
    //stores the profile Id
    private int profileId;
    //stores the Profile name
    private String profileName;
    // stores the subscriber FS
    private long subscriberFS;
    //stores the public subscription type
    private int pubSubsType;
    //stores the corporate subscription type
    private int corpSubsType;
    //stores the Client type
    private int client_Type;
    //stores the external corporate id
    private String extCorpId;
    //stores the Corpoarate name
    private String corpName;
    //stores the creation time
    private long creationTime;
    //stores the Last update time
    private long lastUpdateTime;

    private int serviceAuthStatus;

    // this flag is to differentiate between createLicensepack and upgardeLicensepack in bulk layer.
    private boolean isUpgrade;

	private List<String> mdns;

    //stores the Custom Data Map
    private Map<String, Object> customParamMap;

    private String IMEI;

    private String emailAddress;

    private Boolean autoPair;

    private KnConstants.HIERARCHY_TYPE hierarchyType;
    
    private int subsDefPttRadio;

    private Map<Integer,Integer> featureBitInfoMap;
    private Map<String, Map<String,Integer>> pkgIdMap;
    private int licenseType;
    private String firstNetIndicator;
    private String subscriberFS2;
    private int corpID;

    public int getCorpID() {
        return corpID;
    }

    public void setCorpID(int corpID) {
        this.corpID = corpID;
    }

    public int getSubsDefPttRadio() {
		return subsDefPttRadio;
	}

	public void setSubsDefPttRadio(int subsDefPttRadio) {
		this.subsDefPttRadio = subsDefPttRadio;
	}

	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public Boolean isAutoPair() {
		return autoPair;
	}

	public void setAutoPair(Boolean autoPair) {
		this.autoPair = autoPair;
	}

    public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

    public boolean isUpgrade() {
    	return isUpgrade;
    }

    public void setUpgrade(boolean isUpgrade) {
    	this.isUpgrade = isUpgrade;
    }

    public int getPamAccId() {
        return pamAccId;
    }

    public void setPamAccId(int pamAccId) {
        this.pamAccId = pamAccId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public long getSubscriberFS() {
        return subscriberFS;
    }

    public void setSubscriberFS(long subscriberFS) {
        this.subscriberFS = subscriberFS;
    }

    public int getPubSubsType() {
        return pubSubsType;
    }

    public void setPubSubsType(int pubSubsType) {
        this.pubSubsType = pubSubsType;
    }

    public int getCorpSubsType() {
        return corpSubsType;
    }

    public void setCorpSubsType(int corpSubsType) {
        this.corpSubsType = corpSubsType;
    }

    public int getClient_Type() {
        return client_Type;
    }

    public void setClient_Type(int client_Type) {
        this.client_Type = client_Type;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public List<String> getMdns() {
        return mdns;
    }

    public void setMdns(List<String> mdns) {
        this.mdns = mdns;
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public String getObjectId() {
        return String.valueOf(pamAccId + profileId);
    }

    public Map<Integer, Integer> getFeatureBitInfoMap() {
        return featureBitInfoMap;
    }

    public void setFeatureBitInfoMap(Map<Integer, Integer> featureBitInfoMap) {
        this.featureBitInfoMap = featureBitInfoMap;
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

	public String getSubscriberFS2() {
		return subscriberFS2;
	}

	public void setSubscriberFS2(String subscriberFS2) {
		this.subscriberFS2 = subscriberFS2;
	}

	public String toString() {
        StringBuilder strBuffer = new StringBuilder(100);
    strBuffer.append("[KnXDMPAMSubsProfInfoDTO --> ");
        strBuffer.append(", pamAccId - ").append(pamAccId)
                .append(", profileId - ").append(profileId)
                .append(", profileName - ").append(profileName)
                .append(", subscriberFS - ").append(subscriberFS)
                .append(", pubSubsType - ").append(pubSubsType)
                .append(", corpSubsType - ").append(corpSubsType)
                .append(", client_Type - ").append(client_Type)
                .append(", extCorpId - ").append(extCorpId)
                .append(", corpName - ").append(corpName)
                .append(", mdns - ").append(KnGDPRTemplate.mdnList(mdns))
                .append(", creationTime - ").append(creationTime)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", lastUpdateTime - ").append(lastUpdateTime)
                .append(", featureBitInfoList - ").append(featureBitInfoMap)
                .append(", subsDefPttRadio - ").append(subsDefPttRadio)
                .append(", pkgIdMap - ").append(pkgIdMap)
                .append(", licenseType - ").append(licenseType)
                .append(", firstNetIndicator - ").append(firstNetIndicator)
                .append(", subscriberFS2 - ").append(subscriberFS2)
                .append(", corpID - ").append(corpID)
                .append("]");

        return strBuffer.toString();
    }


}
