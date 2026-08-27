/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMSubsProvDTO.java
 * Subsystem:   COMMON DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/22/10       7.0
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

package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.*;

public class KnXDMSubsProvDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676135L;
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
    private String pocHome;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private boolean enablePttRadio;
    private int pamAccId;
    private int dispatchType;
    private String userId;
    private String derivedKey;
    private Map<Integer, Integer> provFSMap;
    private String ufmi;
    private String iDenUserName;
    private String iDenPassword;
    private String iDenBusUnitId;
    private Map<String, Map<String, Integer>> pkgIdMap;
    private int licenseType;
    private String aliasMdn;
    private String firstNetIndicator;
    private String subsFS2;
    private String clientFS2;
    private String activeFS2;
    private String corpAdminFS2;
    private String mcpttCompliance;
    private String mcpttId;
    private String mcVideoId;
    private String mcDataId;
    private String mcId;
	private long lastUpdateProfileTime;
	private int mcsCompliance;
	private Integer onBoardingMailReq;
    private String xcapRootUri;
	private int isDefaultProfile;
	private Integer userProfileIndex;
	private String userProfileName;
    private String userProfileId;
    private Integer recordingStatus;
    private Integer cameraType;
    private String deviceId;
    private String custMcpttId;
    private String custMcVideoId;
    private String custMcDataId;
    private String extGatewayId;

    public String getExtGatewayId() {
        return extGatewayId;
    }

    public void setExtGatewayId(String extGatewayId) {
        this.extGatewayId = extGatewayId;
    }

    public String getCustMcpttId() {
        return custMcpttId;
    }

    public void setCustMcpttId(String custMcpttId) {
        this.custMcpttId = custMcpttId;
    }

    public String getCustMcVideoId() {
        return custMcVideoId;
    }

    public void setCustMcVideoId(String custMcVideoId) {
        this.custMcVideoId = custMcVideoId;
    }

    public String getCustMcDataId() {
        return custMcDataId;
    }

    public void setCustMcDataId(String custMcDataId) {
        this.custMcDataId = custMcDataId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    private KnXDMSubsCameraInfo cameraInfo;
    private List<KnXDMSubsAliasInfoDTO> aliasInfoList;

    private List<KnXDMSubsAliasInfoDTO> addAliasInfoList;
    private List<KnXDMSubsAliasInfoDTO> removeAliasInfoList;


    public List<KnXDMSubsAliasInfoDTO> getAddAliasInfoList() {
        return addAliasInfoList;
    }

    public void setAddAliasInfoList(List<KnXDMSubsAliasInfoDTO> addAliasInfoList) {
        this.addAliasInfoList = addAliasInfoList;
    }

    public List<KnXDMSubsAliasInfoDTO> getRemoveAliasInfoList() {
        return removeAliasInfoList;
    }

    public void setRemoveAliasInfoList(List<KnXDMSubsAliasInfoDTO> removeAliasInfoList) {
        this.removeAliasInfoList = removeAliasInfoList;
    }

    public KnXDMSubsCameraInfo getCameraInfo() {return cameraInfo; }

    public void setCameraInfo(KnXDMSubsCameraInfo cameraInfo) {this.cameraInfo = cameraInfo; }

    public List<KnXDMSubsAliasInfoDTO> getAliasInfoList() {
        return aliasInfoList;
    }

    public void setAliasInfoList(List<KnXDMSubsAliasInfoDTO> aliasInfoList) {
        this.aliasInfoList = aliasInfoList;
    }

    public Integer getRecordingStatus() { return recordingStatus; }

    public void setRecordingStatus(Integer recordingStatus) { this.recordingStatus = recordingStatus; }

    public int getPamAccId() {
        return pamAccId;
    }

    public void setPamAccId(int pamAccId) {
        this.pamAccId = pamAccId;
    }

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
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

    public Boolean isAutoPair() {
        return autoPair;
    }

    public void setAutoPair(Boolean autoPair) {
        this.autoPair = autoPair;
    }

    public long getCorpAdminFS() {
        return corpAdminFS;
    }

    public void setCorpAdminFS(long corpAdminFS) {
        this.corpAdminFS = corpAdminFS;
    }

    /**
     * getter method for MDN
     *
     * @return String
     */
    public String getMdn() {
        return mdn;
    }

    /**
     * setter method for MDN
     *
     * @param mdn String
     */
    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    /**
     * getter method for the IMEI
     *
     * @return String
     */
    public String getIMEI() {
        return IMEI;
    }

    /**
     * setter method for the IMEI
     *
     * @param IMEI String
     */
    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    /**
     * getter method for Subscriber Name
     *
     * @return String
     */
    public String getNetworkName() {
        return networkName;
    }

    /**
     * setter method for Subscriber Name
     *
     * @param networkName String
     */
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    /**
     * getter method for Account Id
     *
     * @return String
     */
    public String getExtCorpId() {
        return extCorpId;
    }

    /**
     * setter method for Account Id
     *
     * @param extCorpId String
     */
    public void setExtCorpId(String extCorpId) {
        if (extCorpId != null) {
            extCorpId = extCorpId.trim();
            if (extCorpId.equals("")) {
                extCorpId = null;
            }
        }
        this.extCorpId = extCorpId;
    }

    /**
     * setter method for the Public Subscription Type
     *
     * @return int value
     */
    public int getPublicSubscriptionType() {
        return publicSubscriptionType;
    }

    /**
     * setter method for Public Subscription Type
     *
     * @param publicSubscriptionType int
     */
    public void setPublicSubscriptionType(int publicSubscriptionType) {
        this.publicSubscriptionType = publicSubscriptionType;
    }

    /**
     * getter method for Corporate Subscription Type
     *
     * @return int
     */
    public int getCorporateSubscriptionType() {
        return corporateSubscriptionType;
    }

    /**
     * setter method for Corporate Subscription Type
     *
     * @param corporateSubscriptionType int
     */
    public void setCorporateSubscriptionType(int corporateSubscriptionType) {
        this.corporateSubscriptionType = corporateSubscriptionType;
    }

    /**
     * getter method for Affiliate Id
     *
     * @return String
     */
    public String getAffiliateId() {
        return affiliateId;
    }

    /**
     * setter method for Affiliate Id
     *
     * @param affiliateId String
     */
    public void setAffiliateId(String affiliateId) {
        this.affiliateId = affiliateId;
    }

    /**
     * getter method for Pay Type
     *
     * @return int
     */
    public int getPayType() {
        return payType;
    }

    /**
     * setter method for PayType
     *
     * @param payType int
     */
    public void setPayType(int payType) {
        this.payType = payType;
    }

    /**
     * Is pairing Indicator Enabled
     *
     * @return boolean
     */
    public Boolean getPairingInd() {
        return pairingInd;
    }

    /**
     * setter for the pairing Indicator
     *
     * @param pairingInd boolean
     */
    public void setPairingInd(Boolean pairingInd) {
        this.pairingInd = pairingInd;
    }

    /**
     * getter method for Roaming types
     *
     * @return Collection of roaming types
     */
    public Collection getRoamingType() {
        return roamingType;
    }

    /**
     * setter method for Roaming Types
     *
     * @param roamingType Collection
     */
    public void setRoamingType(Collection roamingType) {
        this.roamingType = roamingType;
    }

    /**
     * getter method for Subscriber Feature set
     *
     * @return long
     */
    public Long getSubsFeatureSet() {
        return subsFeatureSet;
    }

    /**
     * setter method for Subscriber Feature Set
     *
     * @param subsFeatureSet long
     */
    public void setSubsFeatureSet(Long subsFeatureSet) {
        this.subsFeatureSet = subsFeatureSet;
    }

    /**
     * getter method for Email Address
     *
     * @return String
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * setter method for Email Address
     *
     * @param emailAddress String
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * getter method for Subscriber Client Type
     *
     * @return int
     */
    public Integer getSubscriberClientType() {
        return subscriberClientType;
    }

    /**
     * setter method for Subscriber Client Type
     *
     * @param subscriberClientType int
     */
    public void setSubscriberClientType(Integer subscriberClientType) {
        this.subscriberClientType = subscriberClientType;
    }

    /**
     * getter method for the Custom Param Map
     *
     * @return Map
     */
    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }

    /**
     * setter method for the customParamMap
     *
     * @param customParamMap Map
     */
    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    /**
     * getter method for the dispatch group Member
     *
     * @return int
     */
    public Integer getDispatchGroupMember() {
        return dispatchGroupMember;
    }

    /**
     * setter method for the dispatch group Member
     *
     * @param dispatchGroupMember int
     */
    public void setDispatchGroupMember(Integer dispatchGroupMember) {
        this.dispatchGroupMember = dispatchGroupMember;
    }

    /**
     * getter method for corporate Name
     *
     * @return String
     */
    public String getCorporateName() {
        return corporateName;
    }

    /**
     * setter method for corporate Name
     *
     * @param corporateName String
     */
    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public long getClientFS1() {
        return clientFS1;
    }

    public void setClientFS1(long clientFS1) {
        this.clientFS1 = clientFS1;
    }


    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public long getActiveFS() {
        return activeFS;
    }

    public void setActiveFS(long activeFS) {
        this.activeFS = activeFS;
    }


    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
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

    public String getPocHome() {
        return pocHome;
    }

    public void setPocHome(String pocHome) {
        this.pocHome = pocHome;
    }

    public boolean isEnablePttRadio() {
        return enablePttRadio;
    }

    public void setEnablePttRadio(boolean enablePttRadio) {
        this.enablePttRadio = enablePttRadio;
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

    public String getDerivedKey() {
        return derivedKey;
    }

    public void setDerivedKey(String derivedKey) {
        this.derivedKey = derivedKey;
    }

    public Map<Integer, Integer> getProvFSMap() {
        return provFSMap;
    }

    public void setProvFSMap(Map<Integer, Integer> provFSMap) {
        this.provFSMap = provFSMap;
    }

    public String getUfmi() {
        return ufmi;
    }

    public void setUfmi(String ufmi) {
        this.ufmi = ufmi;
    }

    public String getiDenUserName() {
        return iDenUserName;
    }

    public void setiDenUserName(String iDenUserName) {
        this.iDenUserName = iDenUserName;
    }

    public String getiDenPassword() {
        return iDenPassword;
    }

    public void setiDenPassword(String iDenPassword) {
        this.iDenPassword = iDenPassword;
    }

    public String getiDenBusUnitId() {
        return iDenBusUnitId;
    }

    public void setiDenBusUnitId(String iDenBusUnitId) {
        this.iDenBusUnitId = iDenBusUnitId;
    }

    public String getObjectId() {
        return mdn;
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

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

   

    public String getFirstNetIndicator() {
		return firstNetIndicator;
	}

	public void setFirstNetIndicator(String firstNetIndicator) {
		this.firstNetIndicator = firstNetIndicator;
	}

	public String getSubsFS2() {
		return subsFS2;
	}

	public void setSubsFS2(String subsFS2) {
		this.subsFS2 = subsFS2;
	}

	public String getClientFS2() {
		return clientFS2;
	}

	public void setClientFS2(String clientFS2) {
		this.clientFS2 = clientFS2;
	}

	public String getActiveFS2() {
		return activeFS2;
	}

	public void setActiveFS2(String activeFS2) {
		this.activeFS2 = activeFS2;
	}

	public String getCorpAdminFS2() {
		return corpAdminFS2;
	}

	public void setCorpAdminFS2(String corpAdminFS2) {
		this.corpAdminFS2 = corpAdminFS2;
	}
	
	public String getMcpttCompliance() {
		return mcpttCompliance;
	}

	public void setMcpttCompliance(String mcpttCompliance) {
		this.mcpttCompliance = mcpttCompliance;
	}

	public String getMcpttId() {
		return mcpttId;
	}

	public void setMcpttId(String mcpttId) {
		this.mcpttId = mcpttId;
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

    public long getLastUpdateProfileTime() {
        return lastUpdateProfileTime;
    }

    public void setLastUpdateProfileTime(long lastUpdateProfileTime) {
        this.lastUpdateProfileTime = lastUpdateProfileTime;
    }

    public int getMcsCompliance() {
		return mcsCompliance;
	}

	public void setMcsCompliance(int mcsCompliance) {
		this.mcsCompliance = mcsCompliance;
	}

    public Integer getOnBoardingMailReq() {
        return onBoardingMailReq;
    }

    public void setOnBoardingMailReq(Integer onBoardingMailReq) {
        this.onBoardingMailReq = onBoardingMailReq;
    }

    public String getXcapRootUri() {
        return xcapRootUri;
    }

    public void setXcapRootUri(String xcapRootUri) {
        this.xcapRootUri = xcapRootUri;
    }

    public int getIsDefaultProfile() {
		return isDefaultProfile;
	}

	public void setIsDefaultProfile(int isDefaultProfile) {
		this.isDefaultProfile = isDefaultProfile;
	}

	public Integer getUserProfileIndex() {
		return userProfileIndex;
	}

	public void setUserProfileIndex(Integer userProfileIndex) {
		this.userProfileIndex = userProfileIndex;
	}

	public String getUserProfileName() {
		return userProfileName;
	}

	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
	}

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public Integer getCameraType() {return cameraType; }

    public void setCameraType(Integer cameraType) {this.cameraType = cameraType; }


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
                .append(", clientPvMinorVersion ").append(clientPvMinorVersion)
                .append(", pocHome ").append(pocHome)
                .append(", pamAccId ").append(pamAccId)
                .append(", enablePttRadio ").append(enablePttRadio)
                .append(", dispatchType ").append(dispatchType)
                .append(", userId ").append(KnGDPRTemplate.userId(userId))
                .append(", provFSMap ").append(provFSMap)
                .append(", derivedKey ").append(derivedKey)
                .append(", ufmi ").append(ufmi)
                .append(", iDenUserName ").append(iDenUserName)
                .append(", iDenPassword ").append(iDenPassword)
                .append(", iDenBusUnitId ").append(iDenBusUnitId)
                .append(", pkgIdMap ").append(pkgIdMap)
                .append(", licenseType ").append(licenseType)
                .append(", firstNetIndicator ").append(firstNetIndicator)
                .append(", activeFS2 ").append(activeFS2)
                .append(", corpAdminFS2 ").append(corpAdminFS2)
                .append(", clientFS2 ").append(clientFS2)
                .append(", subsFS2 ").append(subsFS2)
        		.append(", mcpttCompliance ").append(mcpttCompliance)
        		.append(", mcpttId ").append(KnGDPRTemplate.mcpttId(mcpttId))
        		.append(", mcVideoId ").append(KnGDPRTemplate.mcvideoId(mcVideoId))
        		.append(", mcDataId ").append(KnGDPRTemplate.mcdataId(mcDataId))
        		.append(", mcId ").append(KnGDPRTemplate.mcId(mcId))
				.append(", lastUpdateProfileTime ").append(lastUpdateProfileTime)
				.append(", mcsCompliance ").append(mcsCompliance)
                .append(", xcapRootUri ").append(xcapRootUri)
                .append(", isDefaultProfile ").append(isDefaultProfile)
        		.append(", userProfileIndex ").append(userProfileIndex)
        		.append(", userProfileName ").append(userProfileName)
                .append(", onBoardingMailReq ").append(onBoardingMailReq)
                .append(", userProfileId ").append(userProfileId)
                .append(", xcapRootUri ").append(xcapRootUri)
				.append(", cameraType ").append(cameraType)
  				.append(", cameraInfo ").append(cameraInfo)
                .append(", aliasInfoList ").append(aliasInfoList)
                .append(", addAliasInfoList ").append(addAliasInfoList)
                .append(", removeAliasInfoList ").append(removeAliasInfoList)
                .append(", recordingStatus ").append(recordingStatus)
                .append(", deviceId ").append(deviceId)
                .append(", custMcpttId ").append(custMcpttId)
                .append(", custMcVideoId ").append(custMcVideoId)
                .append(", custMcDataId ").append(custMcDataId)
                .append(", extGatewayId ").append(extGatewayId);

        return strBuffer.toString();
    }
}
