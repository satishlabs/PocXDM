/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.List;
import java.util.Map;

/**
 * *****************************************************************************
 * File name:   KnPAMSubsProfInfoDTO.java
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
public class KnPAMSubsProfInfoDTO implements IIdentifier {
    private static final long serialVersionUID = 7526471155622678001L;

    //stores the PAM Account Id
    private int pamAccId;
    //stores the profile Id
    private int profileId;
    //stores the Profile name
    private String profileName;
    // stores the subscriber FS
    private String subscriberFS2;
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
    //stores the Service Auth Status
    private int serviceAuthStatus;
    private List<String> mdns;
    //stores the internal corporate id
    private int corpID;
    //stores the imei
    private String imei;
    //stores the email Address
    private String email;

    private Boolean autoPair;
    private String extPamAccId;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    // It holds all the feature set that comes in reques i.e featureBitInfoMap
    private Map<Integer,Integer> provFSMap;
    private Map<String, Map<String,Integer>> pkgIdMap;
    private String tierPkgCode;
    private Integer dataPkgId;
    private List<String> addOnPkgId;
    private int licenseType;
    private String firstNetIndicator;
    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public String getExtPamAccId() {
		return extPamAccId;
	}

	public void setExtPamAccId(String extPamAccId) {
		this.extPamAccId = extPamAccId;
	}

	public Boolean isAutoPair() {
		return autoPair;
	}

	public void setAutoPair(Boolean autoPair) {
		this.autoPair = autoPair;
	}

    public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

    public int getCorpID() {
		return corpID;
	}

	public void setCorpID(int corpID) {
		this.corpID = corpID;
	}

	//stores the Custom Data Map
    private Map<String, Object> customParamMap;

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

    public String getSubscriberFS2() {
		return subscriberFS2;
	}

	public void setSubscriberFS2(String subscriberFS2) {
		this.subscriberFS2 = subscriberFS2;
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

    public String getObjectId() {
        return String.valueOf(this.pamAccId + this.profileId);
    }

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
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

	public String getTierPkgCode() {
		return tierPkgCode;
	}

	public void setTierPkgCode(String tierPkgCode) {
		this.tierPkgCode = tierPkgCode;
	}

	public Integer getDataPkgId() {
		return dataPkgId;
	}

	public void setDataPkgId(Integer dataPkgId) {
		this.dataPkgId = dataPkgId;
	}

	public List<String> getAddOnPkgId() {
		return addOnPkgId;
	}

	public void setAddOnPkgId(List<String> addOnPkgId) {
		this.addOnPkgId = addOnPkgId;
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

	public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append("[KnPAMAccInfoDTO --> ");
        strBuffer.append(", pamAccId - ").append(pamAccId)
                .append(", profileId - ").append(profileId)
                .append(", profileName - ").append(profileName)
                .append(", subscriberFS2 - ").append(subscriberFS2)
                .append(", pubSubsType - ").append(pubSubsType)
                .append(", corpSubsType - ").append(corpSubsType)
                .append(", client_Type - ").append(client_Type)
                .append(", extCorpId - ").append(extCorpId)
                .append(", corpName - ").append(corpName)
                 .append(", corpID - ").append(corpID)
                .append(", email - ").append(KnGDPRTemplate.email(email))
                .append(", imei - ").append(imei)
                .append(", serviceAuthStatus - ").append(serviceAuthStatus)
                .append(", creationTime - ").append(creationTime)
                .append(", lastUpdateTime - ").append(lastUpdateTime)
                .append(", autoPair - ").append(autoPair)
                .append(", extPamAccId - ").append(extPamAccId)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", provFSMap - ").append(provFSMap)
                .append(", pkgIdMap - ").append(pkgIdMap)
                .append(", tierPkgCode - ").append(tierPkgCode)
                .append(", dataPkgId - ").append(dataPkgId)
                .append(", addOnPkgId - ").append(addOnPkgId)
                .append(", licenseType - ").append(licenseType)
                .append(", firstNetIndicator - ").append(firstNetIndicator)
                .append("]");

        return strBuffer.toString();
    }


}
