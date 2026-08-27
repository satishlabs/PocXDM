/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnSubscriberProvDTO.java
 * Subsystem:   Subscriber Management Lib
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       12/30/10   7.0
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
 * *************************************************************************
 */
package com.kodiak.xdms.server.bulkops.dto.common;

import com.kodiak.common.commdto.common.KnSubsCameraInfo;
import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.common.commdto.common.KnXDMSubsAliasInfoDTO;
import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KnSubscriberProvDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676241L;

    private String mdn;
    private String oldMdn;
    //stores IMEI of the mdn
    private String IMEI;
    //stores network Name
    private String networkName;
    //stores Pay Type
    private int payType;
    //stores affiliate Id
    private String affiliateId;
    //stores the ext Corp Id
    private String extCorpId;
    //stores the pairing Indicator
    private Boolean pairingInd;
    //stores the public Subscription Type
    private int publicSubscriptionType;
    //stores the corporate Subscription Type
    private int corporateSubscriptionType;
    //stores the Service Auth Status
    private Integer serviceAuthStatus;

    //stores the dispatch Group Member Value
    private Integer dispatchGroupMember;

    //stores the name of the corporate Name
    private String corporateName;

    //stores the pairing Indicator
    private ArrayList<Integer> roamingTypes;

    private long ifMatch;
    private long ifNoneMatch;
    private String subsFS2;
    private String clientFS2;
    private String activeFS2;
    private String opsFS2;
    private String corpAdminFS2;
    private String xdmsFS2;
    //stores the emailAddress
    private String emailAddress;
    //stores the subsClientType;
    private Integer subsClientType;
    //stores the ext Corp Id
    private String accountId;
    //UserAgent
    private String userAgent;
    //UserAgent DTO
    private KnUserAgentDTO userAgentDTO;

    //stores the pamaccId;
    private Integer pamAccId;
    //stores the last activation time
    private Long lastActivationTime;

    private String billingMDN;

    private int roamingAllowed;
    private int swType;
    private int platformType;

    private Boolean autoPair;


    private String tpUser;
    private String tpAccount;
    private KnConstants.HIERARCHY_TYPE hierarchyType;

    //EnablePttRadio : For Converged Client
    private boolean enablePttRadio;

    private int subsDefPttRadio;

	private Map<Integer,Integer> vocoderIdMap;

    private int corpId;

    private Integer serviceStatusOp;
    private Integer serviceStatusAuthUser;

    private Map<Integer,Integer> provFSMap;
    private String ufmi;
    private String iDenUserName;
    private String iDenPassword;
    private String iDenBusUnitId;
    private Map<String, Map<String,Integer>> pkgIdMap;
    private String aliasMdn;
    private int licenseType;
    private int qppPkgId;
    private String firstNetIndicator;
    private int mcpttCompliance;
    private String mcpttId;
    private String mcVideoId;
    private String mcDataId;
    private String mcId;
    private String userId;
    private int mcsCompliance;
    //upm
    private Integer userProfileIndex;
    private int isDefaultProfile;
    private String userProfileName;
    private String userProfileId;
    private String userProfileFS2;
    private List<String> mdnList;
    private Integer onBoardingEmailReqd;
    private int clientPVmajorVer;
    private int clientPVminorVer;
    private int dynamicQosFlag;
    private String derivedKey;
    private String clientPassword;
    private KnUserProfileFSProvDTO userProfileFSProvDTO;
    private String featureRelVersion;
    private String pocPttServerId;
    private String presPttServerId;
    private String tgscMode;
    private Integer recordingStatus;
    private Integer cameraType;
    KnSubsCameraInfo cameraInfo;
	private List<KnXDMSubsAliasInfoDTO> aliasInfoDTOList;
    private List<KnSubsAliasInfoDTO> aliasInfoList;

    private List<KnXDMSubsAliasInfoDTO> addAliasInfoDTOList;
    private List<KnSubsAliasInfoDTO> addAliasInfoList;
    private List<KnXDMSubsAliasInfoDTO> removeAliasInfoDTOList;
    private List<KnSubsAliasInfoDTO> removeAliasInfoList;
    private String deviceId;
    private String custMcpttId;
    private String custMcVideoId;
    private String custMcDataId;
    private String selfDnDPrivilege;
    private int privacyOptStatus;
    private String extGatewayId;
    private String emergConfigTimer;

    public String getExtGatewayId() { return extGatewayId;}
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

    public Integer getRecordingStatus() {
        return recordingStatus;
    }

    public void setRecordingStatus(Integer recordingStatus) {
        this.recordingStatus = recordingStatus;
    }


    public String getFeatureRelVersion() {
		return featureRelVersion;
	}

	public void setFeatureRelVersion(String featureRelVersion) {
		this.featureRelVersion = featureRelVersion;
	}

	public Integer getServiceStatusOp() {
        return serviceStatusOp;
    }

    public void setServiceStatusOp(Integer serviceStatusOp) {
        this.serviceStatusOp = serviceStatusOp;
    }

    public Integer getServiceStatusAuthUser() {
        return serviceStatusAuthUser;
    }

    public void setServiceStatusAuthUser(Integer serviceStatusAuthUser) {
        this.serviceStatusAuthUser = serviceStatusAuthUser;
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

    /**
     * getter method for Mdn
     *
     * @return String
     */
    public String getMdn() {
        return mdn;
    }

    /**
     * setter method for Mdn
     *
     * @param mdn String
     */
    public void setMdn(String mdn) {
        if (mdn != null) {
            mdn = mdn.trim();
            if (mdn.equals("")) {
                mdn = null;
            }
        }
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

    public List<KnXDMSubsAliasInfoDTO> getAliasInfoDTOList() {
		return aliasInfoDTOList;
	}

	public void setAliasInfoDTOList(List<KnXDMSubsAliasInfoDTO> aliasInfoDTOList) {
		this.aliasInfoDTOList = aliasInfoDTOList;
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
     * getter method for network Name
     *
     * @return String
     */
    public String getNetworkName() {
        return networkName;
    }

    /**
     * setter method for the network Name
     *
     * @param networkName String
     */
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    /**
     * getter method for the Pay Type
     *
     * @return int
     */
    public int getPayType() {
        return payType;
    }

    /**
     * setter method for the Pay Type
     *
     * @param payType int
     */
    public void setPayType(int payType) {
        this.payType = payType;
    }

    /**
     * getter method for the Affiliate Id
     *
     * @return String
     */
    public String getAffiliateId() {
        return affiliateId;
    }

    /**
     * setter method for the affiliate Id
     *
     * @param affiliateId String
     */
    public void setAffiliateId(String affiliateId) {
        this.affiliateId = affiliateId;
    }

    /**
     * getter method for External Corp Id
     *
     * @return String
     */
    public String getExtCorpId() {
        return extCorpId;
    }

    /**
     * setter method for the External Corp Id
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
     * getter method for the pairing Indicator
     *
     * @return boolean
     */
    public Boolean getPairingInd() {
        return pairingInd;
    }

    /**
     * setter method for the pairing indicator
     *
     * @param pairingInd boolean
     */
    public void setPairingInd(Boolean pairingInd) {
        this.pairingInd = pairingInd;
    }

    /**
     * getter method for the Public Subscription Type
     *
     * @return int
     */
    public int getPublicSubscriptionType() {
        return publicSubscriptionType;
    }

    /**
     * setter method for the Public Subscription Type
     *
     * @param publicSubscriptionType int
     */
    public void setPublicSubscriptionType(int publicSubscriptionType) {
        this.publicSubscriptionType = publicSubscriptionType;
    }

    /**
     * getter method for the Corporate Subscription Type
     *
     * @return int
     */
    public int getCorporateSubscriptionType() {
        return corporateSubscriptionType;
    }

    /**
     * setter method for the corporate Subscription Type
     *
     * @param corporateSubscriptionType int
     */
    public void setCorporateSubscriptionType(int corporateSubscriptionType) {
        this.corporateSubscriptionType = corporateSubscriptionType;
    }

    /**
     * getter method for the service Auth Status
     *
     * @return serviceAuthStatus int
     */
    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    /**
     * setter method for the Service Auth Status
     *
     * @param serviceAuthStatus int
     */
    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    /**
     * getter method for the Roaming Types
     *
     * @return ArrayList
     */
    public ArrayList<Integer> getRoamingTypes() {
        return roamingTypes;
    }

    /**
     * setter method for the roaming Types
     *
     * @param roamingTypes ArrayList
     */
    public void setRoamingTypes(ArrayList<Integer> roamingTypes) {
        this.roamingTypes = roamingTypes;
    }


    public long getIfMatch() {
        return ifMatch;
    }

    public void setIfMatch(long ifMatch) {
        this.ifMatch = ifMatch;
    }

    public long getIfNoneMatch() {
        return ifNoneMatch;
    }

    public void setIfNoneMatch(long ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
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
     * setter method for the email Address
     *
     * @param emailAddress String
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Getter method for the Subscriber Client Type
     *
     * @return int
     */
    public Integer getSubsClientType() {
        return subsClientType;
    }

    /**
     * setter method for Subscriber Client Type
     *
     * @param subsClientType int
     */
    public void setSubsClientType(Integer subsClientType) {
        this.subsClientType = subsClientType;
    }

    /**
     * getter method for the Dispatch Group Member
     *
     * @return int
     */
    public Integer getDispatchGroupMember() {
        return dispatchGroupMember;
    }

    /**
     * setter method for the Dispatch Group Member
     *
     * @param dispatchGroupMember int
     */
    public void setDispatchGroupMember(Integer dispatchGroupMember) {
        this.dispatchGroupMember = dispatchGroupMember;
    }

    /**
     * getter method for the Corporate Name
     *
     * @return String
     */
    public String getCorporateName() {
        return corporateName;
    }

    /**
     * setter method for the Corporate Name
     *
     * @param corporateName String
     */
    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }


    public String getAccountId() {
        return accountId;
    }

    public KnUserAgentDTO getUserAgentDTO() {
        return userAgentDTO;
    }

    public void setUserAgentDTO(KnUserAgentDTO userAgentDTO) {
        this.userAgentDTO = userAgentDTO;
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

    /* public Map<String, Object> getCustomParamMap() {
  return customParamMap;
}

public void setCustomParamMap(Map<String, Object> customParamMap) {
  this.customParamMap = customParamMap;
}      */

    public Integer getPamAccId() {
        return pamAccId;
    }

    public void setPamAccId(Integer pamAccId) {
        this.pamAccId = pamAccId;
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

    public int getSwType() {
        return swType;
    }

    public void setSwType(int swType) {
        this.swType = swType;
    }

    public int getPlatformType() {
        return platformType;
    }

    public void setPlatformType(int platformType) {
        this.platformType = platformType;
    }

    public Map<Integer, Integer> getVocoderIdMap() {
        return vocoderIdMap;
    }

    public void setVocoderIdMap(Map<Integer, Integer> vocoderIdMap) {
        this.vocoderIdMap = vocoderIdMap;
    }

    public boolean isEnablePttRadio() {
        return enablePttRadio;
    }

    public void setEnablePttRadio(boolean enablePttRadio) {
        this.enablePttRadio = enablePttRadio;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public String getOldMdn() {
		return oldMdn;
	}

	public void setOldMdn(String oldMdn) {
		this.oldMdn = oldMdn;
	}
	public int getSubsDefPttRadio() {
			return subsDefPttRadio;
	}

	public void setSubsDefPttRadio(int subsDefPttRadio) {
			this.subsDefPttRadio = subsDefPttRadio;
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


	public Map<String, Map<String, Integer>> getPkgIdMap() {
		return pkgIdMap;
	}

	public void setPkgIdMap(Map<String, Map<String, Integer>> pkgIdMap) {
		this.pkgIdMap = pkgIdMap;
	}

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

    public int getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(int licenseType) {
        this.licenseType = licenseType;
    }

    public int getQppPkgId() {
		return qppPkgId;
	}

	public void setQppPkgId(int qppPkgId) {
		this.qppPkgId = qppPkgId;
	}



    public String getFirstNetIndicator() {
		return firstNetIndicator;
	}

	public void setFirstNetIndicator(String firstNetIndicator) {
		this.firstNetIndicator = firstNetIndicator;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getMcsCompliance() {
		return mcsCompliance;
	}

	public void setMcsCompliance(int mcsCompliance) {
		this.mcsCompliance = mcsCompliance;
	}

    public Integer getUserProfileIndex() {
        return userProfileIndex;
    }

    public void setUserProfileIndex(Integer userProfileIndex) {
        this.userProfileIndex = userProfileIndex;
    }

    public int getIsDefaultProfile() {
        return isDefaultProfile;
    }

    public void setIsDefaultProfile(int isDefaultProfile) {
        this.isDefaultProfile = isDefaultProfile;
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

    public String getUserProfileFS2() {
        return userProfileFS2;
    }

    public void setUserProfileFS2(String userProfileFS2) {
        this.userProfileFS2 = userProfileFS2;
    }

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    public Integer getOnBoardingEmailReqd() {
        return onBoardingEmailReqd;
    }

    public void setOnBoardingEmailReqd(Integer onBoardingEmailReqd) {
        this.onBoardingEmailReqd = onBoardingEmailReqd;
    }

    public int getClientPVmajorVer() {
		return clientPVmajorVer;
	}

	public void setClientPVmajorVer(int clientPVmajorVer) {
		this.clientPVmajorVer = clientPVmajorVer;
	}

	public int getClientPVminorVer() {
		return clientPVminorVer;
	}

	public void setClientPVminorVer(int clientPVminorVer) {
		this.clientPVminorVer = clientPVminorVer;
	}

	public int getDynamicQosFlag() {
		return dynamicQosFlag;
	}

	public void setDynamicQosFlag(int dynamicQosFlag) {
		this.dynamicQosFlag = dynamicQosFlag;
	}

	public String getDerivedKey() {
		return derivedKey;
	}

	public void setDerivedKey(String derivedKey) {
		this.derivedKey = derivedKey;
	}

	public String getClientPassword() {
		return clientPassword;
	}

	public void setClientPassword(String clientPassword) {
		this.clientPassword = clientPassword;
	}

	public String getActiveFS2() {
		return activeFS2;
	}

	public void setActiveFS2(String activeFS2) {
		this.activeFS2 = activeFS2;
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

    public KnUserProfileFSProvDTO getUserProfileFSProvDTO() {
        return userProfileFSProvDTO;
    }

    public void setUserProfileFSProvDTO(KnUserProfileFSProvDTO userProfileFSProvDTO) {
        this.userProfileFSProvDTO = userProfileFSProvDTO;
    }

    public String getPocPttServerId() {
        return pocPttServerId;
    }

    public void setPocPttServerId(String pocPttServerId) {
        this.pocPttServerId = pocPttServerId;
    }

    public String getPresPttServerId() {
        return presPttServerId;
    }

    public void setPresPttServerId(String presPttServerId) {
        this.presPttServerId = presPttServerId;
    }

    public String getTgscMode() { return tgscMode; }

    public void setTgscMode(String tgscMode) {  this.tgscMode = tgscMode; }


    public Integer getCameraType() {return cameraType; }

    public void setCameraType(Integer cameraType) {this.cameraType = cameraType; }

    public KnSubsCameraInfo getCameraInfo() {return cameraInfo; }

    public void setCameraInfo(KnSubsCameraInfo cameraInfo) {this.cameraInfo = cameraInfo; }

    public List<KnSubsAliasInfoDTO> getAliasInfoList() {
        return aliasInfoList;
    }

    public List<KnXDMSubsAliasInfoDTO> getAddAliasInfoDTOList() {
        return addAliasInfoDTOList;
    }

    public void setAddAliasInfoDTOList(List<KnXDMSubsAliasInfoDTO> addAliasInfoDTOList) {
        this.addAliasInfoDTOList = addAliasInfoDTOList;
    }

    public List<KnSubsAliasInfoDTO> getAddAliasInfoList() {
        return addAliasInfoList;
    }

    public void setAddAliasInfoList(List<KnSubsAliasInfoDTO> addAliasInfoList) {
        this.addAliasInfoList = addAliasInfoList;
    }

    public List<KnXDMSubsAliasInfoDTO> getRemoveAliasInfoDTOList() {
        return removeAliasInfoDTOList;
    }

    public void setRemoveAliasInfoDTOList(List<KnXDMSubsAliasInfoDTO> removeAliasInfoDTOList) {
        this.removeAliasInfoDTOList = removeAliasInfoDTOList;
    }

    public List<KnSubsAliasInfoDTO> getRemoveAliasInfoList() {
        return removeAliasInfoList;
    }

    public void setRemoveAliasInfoList(List<KnSubsAliasInfoDTO> removeAliasInfoList) {
        this.removeAliasInfoList = removeAliasInfoList;
    }

    public String getSelfDnDPrivilege() {
        return selfDnDPrivilege;
    }

    public void setSelfDnDPrivilege(String selfDnDPrivilege) {
        this.selfDnDPrivilege = selfDnDPrivilege;
    }

    public int getPrivacyOptStatus() {
        return privacyOptStatus;
    }

    public void setPrivacyOptStatus(int privacyOptStatus) {
        this.privacyOptStatus = privacyOptStatus;
    }
    public String getEmergConfigTimer() { return emergConfigTimer; }
    public void setEmergConfigTimer(String emergConfigTimer) { this.emergConfigTimer = emergConfigTimer; }
    public void setAliasInfoList(List<KnSubsAliasInfoDTO> aliasInfoList) {
        this.aliasInfoList = aliasInfoList;
    }    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(" [KnSubscriberProvDTO").append(" --> ");
        strBuffer.append("MDN -").append(KnGDPRTemplate.mdn(mdn))
                .append(", IMEI - ").append(IMEI)
                .append(", Network_Name - ").append(KnGDPRTemplate.name(networkName))
                .append(", Pay_Type - ").append(payType)
                .append(", Affiliate_Id - ").append(affiliateId)
                .append(", Ext_Corp_Id - ").append(extCorpId)
                .append(", Pairing_Indicator - ").append(pairingInd)
                .append(", Public_Subscription_Type - ").append(publicSubscriptionType)
                .append(", Corporate_Subscription_Type - ").append(corporateSubscriptionType)
                .append(", Service_Auth_Status - ").append(serviceAuthStatus)
                .append(", Roaming_Types - ").append(roamingTypes)
                .append(", If-Match - ").append(ifMatch)
                .append(", If-None-Match - ").append(ifNoneMatch)
                .append(", subsFS2 - ").append(subsFS2)
                .append(", clientFS2 - ").append(clientFS2)
                .append(", eMail_Address - ").append(KnGDPRTemplate.email(emailAddress))
                .append(", Subs_Client_Type - ").append(subsClientType)
                .append(", Dispatch_Group_Member - ").append(dispatchGroupMember)
                .append(", Corporate_Name - ").append(corporateName)
                .append(", Account_Id - ").append(accountId)
                .append(", userAgentDTO - ").append(userAgentDTO)
                .append(", userAgent - ").append(userAgent)
                .append(", pamAccId - ").append(pamAccId)
                .append(", lastActivationTime - ").append(lastActivationTime)
                .append(", billingMDN - ").append(billingMDN)
                .append(", roamingAllowed - ").append(roamingAllowed)
                .append(", swType - ").append(swType)
                .append(", platformType - ").append(platformType)
                .append(", autoPair - ").append(autoPair)
                .append(", tpUser - ").append(tpUser)
                .append(", tpAccount - ").append(tpAccount)
                .append(", vocoderIdMap - ").append(vocoderIdMap)
                .append(", enablePttRadio - ").append(enablePttRadio)
                .append(", corpId - ").append(corpId)
                .append(", serviceStatusOp - ").append(serviceStatusOp)
                .append(", serviceStatusAuthUser - ").append(serviceStatusAuthUser)
                .append(", oldMdn - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", provFSMap - ").append(provFSMap)
                .append(", subsDefPttRadio - ").append(subsDefPttRadio)
                .append(", ufmi - ").append(ufmi)
                .append(", iDenUserName - ").append(iDenUserName)
                .append(", iDenPassword - ").append(iDenPassword)
                .append(", iDenBusUnitId - ").append(iDenBusUnitId)
                .append(", pkgIdMap - ").append(pkgIdMap)
                .append(", aliasMdn - ").append(aliasMdn)
                .append(", licenseType - ").append(licenseType)
                .append(", qppPkgId - ").append(qppPkgId)
                .append(", firstNetIndicator - ").append(firstNetIndicator)
                .append(", mcpttCompliance - ").append(mcpttCompliance)
                .append(", mcpttId - ").append(KnGDPRTemplate.mcpttId(mcpttId))
                .append(", mcVideoId - ").append(KnGDPRTemplate.mcvideoId(mcVideoId))
                .append(", mcDataId - ").append(KnGDPRTemplate.mcdataId(mcDataId))
                .append(", mcId - ").append(KnGDPRTemplate.mcId(mcId))
                .append(", userId - ").append(KnGDPRTemplate.userId(userId))
                .append(", mcsCompliance - ").append(mcsCompliance)
                .append(", userProfileIndex - ").append(userProfileIndex)
                .append(", isDefaultProfile - ").append(isDefaultProfile)
                .append(", userProfileName - ").append(userProfileName)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", userProfileFS2 - ").append(userProfileFS2)
                .append(", mdnList - ").append(KnGDPRTemplate.mdnList(mdnList))
                .append(", onBoardingEmailReqd - ").append(onBoardingEmailReqd)
                .append(", clientPVmajorVer - ").append(clientPVmajorVer)
                .append(", clientPVminorVer - ").append(clientPVminorVer)
                .append(", dynamicQosFlag - ").append(dynamicQosFlag)
                .append(", clientPassword - ").append(clientPassword)
                .append(", activeFS2 - ").append(activeFS2)
                .append(", opsFS2 - ").append(opsFS2)
                .append(", corpAdminFS2 - ").append(corpAdminFS2)
                .append(", xdmsFS2 - ").append(xdmsFS2)
                .append(", pocPttServerId - ").append(pocPttServerId)
                .append(", presPttServerId - ").append(presPttServerId)
                .append(", tgscMode - ").append(tgscMode)
                .append(", recordingStatus - ").append(recordingStatus)
                .append(", userProfileFSProvDTO - ").append(userProfileFSProvDTO)
 				.append(", cameraType - ").append(cameraType)
                .append(", cameraInfo - ").append(cameraInfo)
                .append(", aliasInfoList - ").append(aliasInfoList)
                .append(", aliasInfoDTOList - ").append(aliasInfoDTOList)
                .append(", addAliasInfoList - ").append(addAliasInfoList)
                .append(", addAliasInfoDTOList -").append(addAliasInfoDTOList)
                .append(", removeAliasInfoList -").append(removeAliasInfoList)
                .append(",removeAliasInfoDTOList -").append(removeAliasInfoDTOList)
                .append(", custMcpttId -").append(custMcpttId)
                .append(", custMcVideoId -").append(custMcVideoId)
                .append(",custMcDataId -").append(custMcDataId)
                .append(",selfDnDPrivilege -").append(selfDnDPrivilege)
                .append(",privacyOptStatus -").append(privacyOptStatus)
                .append(",extGatewayId -").append(extGatewayId)
                .append(",emergConfigTimer -").append(emergConfigTimer)
                .append("]");
        return strBuffer.toString();
    }

    public String getObjectId() {
        return this.mdn;
    }
}
