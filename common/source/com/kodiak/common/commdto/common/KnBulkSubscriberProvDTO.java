package com.kodiak.common.commdto.common;

import com.kodiak.common.resources.KnConstants;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KnBulkSubscriberProvDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7526471155622676302L;
    protected String transactionId;
    private List<KnBulkSubscriberEntry> subscriberList;
    private Boolean nameChangeAllowed = Boolean.FALSE;
    private int clientType;
    private OperationType bulkOperationType;
    private Map<String, Map<String, Integer>> pkgIdMap;
    private int licenseType;
    private String corporateName;
    private String accountId;
    private String extCorpId;
    private String corpId;
    private int publicSubscriptionType;
    private int corporateSubscriptionType;
    private Integer subscriberClientType;
    private long ifMatch;
    private long ifNoneMatch;
    private int clientPvMajorVersion;
    private int clientPvMinorVersion;
    protected KnConstants.HIERARCHY_TYPE hierarchyType;
    private long lastUpdateProfileTime;
    private int subsDefPttRadio;
    private Integer serviceStatusOp;
    private Integer serviceStatusAuthUser;
    private KnConstants.SERVICE_AUTH_STATUS serviceAuthStatus;
    private Integer previousServiceAuthStatusToStore;
    private String batchId;
    private Integer batchSize;
    private Long requestTimestamp;
    public Integer dispatchGroupMember;
    private Boolean pairingInd = false;
    private Boolean autoPair;
    private String subsFS2;
    private String clientFS2;
    private String activeFS2;
    private String corpAdminFS2;
    private String opsFS2;
    private String userProfileFS2;
    private String xdmsFS2;
    private int isDefaultProfile;
    private Integer userProfileIndex;
    private long profileCreationTime;
    private long lastProfileUpdateTime;
    private int qppPkgId;
    private String featureRelVersion;
    private int mcsCompliance;
    private Integer onBoardingMailReq;

    private String IMEI;
    private String affiliateId;
    private int payType;
    private Collection roamingType;
    private Long subsFeatureSet;
    private long clientFS1;
    private String userAgent;
    private Map<String, Object> customParamMap;
    private long activeFS;
    private long corpAdminFS;
    private Long lastActivationTime;
    private String billingMDN;
    private int roamingAllowed;
    private Integer dispListRr;
    private String pWsUri;
    private String gWsUri;
    private Integer wsCaeT;
    private String tpUser;
    private String tpAccount;
    private String pocStatus;
    private String apnType;
    private String clientPassword;
    private boolean enablePttRadio;
    private int pamAccId;
    private int dispatchType;
    private String derivedKey;
    private Map<Integer, Integer> provFSMap;
    private String ufmi;
    private String iDenUserName;
    private String iDenPassword;
    private String iDenBusUnitId;
    private String aliasMdn;
    private String firstNetIndicator;
    private String mcpttCompliance;
    private String xcapRootUri;
    private String userProfileName;
    private String userProfileId;
    private Integer recordingStatus;
    private Integer cameraType;
    private String deviceId;
    private String custMcpttId;
    private String custMcVideoId;
    private String custMcDataId;
    private String extGatewayId;
    private KnXDMSubsCameraInfo cameraInfo;
    private List<KnXDMSubsAliasInfoDTO> aliasInfoList;
    private List<KnXDMSubsAliasInfoDTO> addAliasInfoList;
    private List<KnXDMSubsAliasInfoDTO> removeAliasInfoList;

    // Getters and Setters
    public String getObjectId() {
        return transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        if (transactionId != null) {
            transactionId = transactionId.trim();
            if (transactionId.isEmpty()) {
                transactionId = null;
            }
        }
        this.transactionId = transactionId;
    }

    public List<KnBulkSubscriberEntry> getSubscriberList() {
        return subscriberList;
    }
    public void setSubscriberList(List<KnBulkSubscriberEntry> subscriberList) {
        this.subscriberList = subscriberList;
    }

    public Boolean getNameChangeAllowed() {
        return nameChangeAllowed;
    }

    public void setNameChangeAllowed(Boolean nameChangeAllowed) {
        this.nameChangeAllowed = nameChangeAllowed;
    }

    public OperationType getBulkOperationType() {
        return bulkOperationType;
    }
    public void setBulkOperationType(OperationType bulkOperationType) {
        this.bulkOperationType = bulkOperationType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }
    public int getClientType() {
        return clientType;
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

    public String getCorporateName() {
        return corporateName;
    }
    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getExtCorpId() {
        return extCorpId;
    }
    public void setExtCorpId(String extCorpId) {
        if (extCorpId != null) {
            extCorpId = extCorpId.trim();
            if (extCorpId.isEmpty()) {
                extCorpId = null;
            }
        }
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

    public Integer getSubscriberClientType() {
        return subscriberClientType;
    }
    public void setSubscriberClientType(Integer subscriberClientType) {
        this.subscriberClientType = subscriberClientType;
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

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }
    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public long getLastUpdateProfileTime() {
        return lastUpdateProfileTime;
    }
    public void setLastUpdateProfileTime(long lastUpdateProfileTime) {
        this.lastUpdateProfileTime = lastUpdateProfileTime;
    }

    public int getSubsDefPttRadio() {
        return subsDefPttRadio;
    }
    public void setSubsDefPttRadio(int subsDefPttRadio) {
        this.subsDefPttRadio = subsDefPttRadio;
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

    public KnConstants.SERVICE_AUTH_STATUS getServiceAuthStatus() {
        return serviceAuthStatus;
    }
    public void setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public Integer getPreviousServiceAuthStatusToStore() {
        return previousServiceAuthStatusToStore;
    }
    public void setPreviousServiceAuthStatusToStore(Integer previousServiceAuthStatusToStore) {
        this.previousServiceAuthStatusToStore = previousServiceAuthStatusToStore;
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public Integer getBatchSize() {
        return subscriberList != null ? subscriberList.size() : 0;
    }

    public Long getRequestTimestamp() { return requestTimestamp; }
    public void setRequestTimestamp(Long requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public Integer getDispatchGroupMember() {
        return dispatchGroupMember;
    }
    public void setDispatchGroupMember(Integer dispatchGroupMember) {
        this.dispatchGroupMember = dispatchGroupMember;
    }

    public Boolean isAutoPair() {
        return autoPair;
    }
    public void setAutoPair(Boolean autoPair) {
        this.autoPair = autoPair;
    }

    public Boolean getPairingInd() {
        return pairingInd;
    }
    public void setPairingInd(Boolean pairingInd) {
        this.pairingInd = pairingInd;
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

    public String getOpsFS2() {
        return opsFS2;
    }
    public void setOpsFS2(String opsFS2) {
        this.opsFS2 = opsFS2;
    }

    public String getUserProfileFS2() {
        return userProfileFS2;
    }
    public void setUserProfileFS2(String userProfileFS2) {
        this.userProfileFS2 = userProfileFS2;
    }

    public String getXdmsFS2() {
        return xdmsFS2;
    }
    public void setXdmsFS2(String xdmsFS2) {
        this.xdmsFS2 = xdmsFS2;
    }

    public long getProfileCreationTime() {
        return profileCreationTime;
    }
    public void setProfileCreationTime(long profileCreationTime) {
        this.profileCreationTime = profileCreationTime;
    }

    public long getLastProfileUpdateTime() {
        return lastProfileUpdateTime;
    }
    public void setLastProfileUpdateTime(long lastProfileUpdateTime) {
        this.lastProfileUpdateTime = lastProfileUpdateTime;
    }

    public int getQppPkgId() {
        return qppPkgId;
    }
    public void setQppPkgId(int qppPkgId) {
        this.qppPkgId = qppPkgId;
    }

    public String getFeatureRelVersion() {
        return featureRelVersion;
    }
    public void setFeatureRelVersion(String featureRelVersion) {
        this.featureRelVersion = featureRelVersion;
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

    public long getCorpAdminFS() {
        return corpAdminFS;
    }
    public void setCorpAdminFS(long corpAdminFS) {
        this.corpAdminFS = corpAdminFS;
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

    public int getPayType() {
        return payType;
    }
    public void setPayType(int payType) {
        this.payType = payType;
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

    public Map<String, Object> getCustomParamMap() {
        return customParamMap;
    }
    public void setCustomParamMap(Map<String, Object> customParamMap) {
        this.customParamMap = customParamMap;
    }

    public long getClientFS1() {
        return clientFS1;
    }
    public void setClientFS1(long clientFS1) {
        this.clientFS1 = clientFS1;
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

    public String getMcpttCompliance() {
        return mcpttCompliance;
    }
    public void setMcpttCompliance(String mcpttCompliance) {
        this.mcpttCompliance = mcpttCompliance;
    }

    public String getXcapRootUri() {
        return xcapRootUri;
    }
    public void setXcapRootUri(String xcapRootUri) {
        this.xcapRootUri = xcapRootUri;
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
        strBuffer.append(", Transaction_Id - ").append(this.transactionId)
                .append(" Subscriber List - ").append(this.subscriberList)
                .append(" Name Change Allowed - ").append(this.nameChangeAllowed)
                .append(", ClientType - ").append(this.clientType)
                .append(", pkgIdMap ").append(this.pkgIdMap)
                .append(", licenseType ").append(this.licenseType)
                .append(", Corporate_Name - ").append(this.corporateName)
                .append(", Account_Id - ").append(this.accountId)
                .append(", ExtCorpId - ").append(this.extCorpId)
                .append(", Corp Id -").append(this.corpId)
                .append(", Public_Subscription_Type - ").append(this.publicSubscriptionType)
                .append(", Corporate_Subscription_Type - ").append(this.corporateSubscriptionType)
                .append(", Subscriber_Client_Type - ").append(this.subscriberClientType)
                .append(", If_Match - ").append(this.ifMatch)
                .append(", If-None-Match - ").append(this.ifNoneMatch)
                .append(", clientPvMajorVersion ").append(this.clientPvMajorVersion)
                .append(", clientPvMinorVersion ").append(this.clientPvMinorVersion)
                .append(", lastUpdateProfileTime ").append(this.lastUpdateProfileTime)
                .append(", subsDefPttRadio ").append(this.subsDefPttRadio)
                .append(", serviceStatusOp ").append(this.serviceStatusOp)
                .append(", serviceStatusAuthUser ").append(this.serviceStatusAuthUser)
                .append(", serviceAuthStatus ").append(this.serviceAuthStatus)
                .append(", batchId - ").append(this.batchId)
                .append(", batchSize - ").append(this.getBatchSize())
                .append(", requestTimestamp - ").append(this.requestTimestamp)
                .append(", dispatchGroupMember - ").append(this.dispatchGroupMember)
                .append(", hierarchyType - ").append(this.hierarchyType)
                .append(", pairingInd - ").append(this.pairingInd)
                .append(", autoPair - ").append(this.autoPair)
                .append(", subsFS2 - ").append(this.subsFS2)
                .append(", clientFS2 - ").append(this.clientFS2)
                .append(", activeFS2 - ").append(this.activeFS2)
                .append(", corpAdminFS2 - ").append(this.corpAdminFS2)
                .append(", opsFS2 - ").append(this.opsFS2)
                .append(", userProfileFS2 - ").append(this.userProfileFS2)
                .append(", xdmsFS2 - ").append(this.xdmsFS2)
                .append(", isDefaultProfile - ").append(this.isDefaultProfile)
                .append(", userProfileIndex - ").append(this.userProfileIndex)
                .append(", profileCreationTime - ").append(this.profileCreationTime)
                .append(", lastProfileUpdateTime - ").append(this.lastProfileUpdateTime)
                .append(", qppPkgId - ").append(this.qppPkgId)
                .append(", featureRelVersion - ").append(this.featureRelVersion)
                .append(", mcsCompliance - ").append(this.mcsCompliance)
                .append(", onBoardingMailReq - ").append(this.onBoardingMailReq);
        return strBuffer.toString();
    }
}