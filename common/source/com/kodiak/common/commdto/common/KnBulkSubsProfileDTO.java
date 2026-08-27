package com.kodiak.common.commdto.common;

import com.kodiak.common.resources.KnConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KnBulkSubsProfileDTO implements Serializable {
    private static final long serialVersionUID = 7526471155622676304L;
    private String mdn;
    private String networkName;
    private int corpId;
    private String extCorpId;
    private String accountId;
    private int subsClientType;
    private int publicSubscriptionType;
    private int corporateSubscriptionType;
    private int serviceAuthStatus;
    private String activeFS2;
    private String subsFS2;
    private int clientPVmajorVer;
    private int clientPVminorVer;
    private String userId;
    private int onBoardingEmailReqd;
    private int dispatchType;
    private String mcpttId;
    private String mcId;
    private String mcsCompliance;
    private String aliasMdn;
    private String clientFS2;
    private String opsFS2;
    private String corpAdminFS2;
    private String xdmsFS2;
    private String userProfileFS2;
    private String corpFS2;  // Corporate feature set (from corp profile)

    // Package related fields
    private Integer qppPkgId;
    private Integer dataPkgId;
    private String tierPkgCode;           // Existing tier package from POCSUBSCR_ADDLINFO
    private List<String> addonPkgCodes;
    private Integer mcpttCompliance;
    private String pocHome;          // POC PTT Server ID
    private String presenceHome;     // Presence PTT Server ID
    private String xdmsHome;
    private int dispatchGroupMember;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private Integer userProfileIndex;  // User Profile Management index (0 = base MDN, >0 = profile MDN)

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public String getExtCorpId() {
        return extCorpId;
    }

    public void setExtCorpId(String extCorpId) {
        this.extCorpId = extCorpId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getSubsClientType() {
        return subsClientType;
    }

    public void setSubsClientType(int subsClientType) {
        this.subsClientType = subsClientType;
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

    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public String getActiveFS2() {
        return activeFS2;
    }

    public void setActiveFS2(String activeFS2) {
        this.activeFS2 = activeFS2;
    }

    public String getSubsFS2() {
        return subsFS2;
    }

    public void setSubsFS2(String subsFS2) {
        this.subsFS2 = subsFS2;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getOnBoardingEmailReqd() {
        return onBoardingEmailReqd;
    }

    public void setOnBoardingEmailReqd(int onBoardingEmailReqd) {
        this.onBoardingEmailReqd = onBoardingEmailReqd;
    }

    public int getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(int dispatchType) {
        this.dispatchType = dispatchType;
    }

    public String getMcpttId() {
        return mcpttId;
    }

    public void setMcpttId(String mcpttId) {
        this.mcpttId = mcpttId;
    }

    public String getMcId() {
        return mcId;
    }

    public void setMcId(String mcId) {
        this.mcId = mcId;
    }

    public String getMcsCompliance() {
        return mcsCompliance;
    }

    public void setMcsCompliance(String mcsCompliance) {
        this.mcsCompliance = mcsCompliance;
    }

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

    public String getClientFS2() {
        return clientFS2;
    }

    public void setClientFS2(String clientFS2) {
        this.clientFS2 = clientFS2;
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

    public String getUserProfileFS2() {
        return userProfileFS2;
    }

    public void setUserProfileFS2(String userProfileFS2) {
        this.userProfileFS2 = userProfileFS2;
    }

    public String getCorpFS2() {
        return corpFS2;
    }

    public void setCorpFS2(String corpFS2) {
        this.corpFS2 = corpFS2;
    }

    public Integer getQppPkgId() {
        return qppPkgId;
    }

    public void setQppPkgId(Integer qppPkgId) {
        this.qppPkgId = qppPkgId;
    }

    public Integer getDataPkgId() {
        return dataPkgId;
    }

    public void setDataPkgId(Integer dataPkgId) {
        this.dataPkgId = dataPkgId;
    }

    public String getTierPkgCode() {
        return tierPkgCode;
    }

    public void setTierPkgCode(String tierPkgCode) {
        this.tierPkgCode = tierPkgCode;
    }

    public List<String> getAddonPkgCodes() {
        return addonPkgCodes;
    }

    public void setAddonPkgCodes(List<String> addonPkgCodes) {
        this.addonPkgCodes = addonPkgCodes;
    }

    public void addAddonPkgCode(String addonPkgCode) {
        if (addonPkgCodes == null) {
            addonPkgCodes = new ArrayList<>();
        }
        if (addonPkgCode != null) {
            addonPkgCodes.add(addonPkgCode);
        }
    }

    public Integer getMcpttCompliance() {
        return mcpttCompliance;
    }

    public void setMcpttCompliance(Integer mcpttCompliance) {
        this.mcpttCompliance = mcpttCompliance;
    }

    public String getPocHome() {
        return pocHome;
    }

    public void setPocHome(String pocHome) {
        this.pocHome = pocHome;
    }

    public String getPresenceHome() {
        return presenceHome;
    }

    public void setPresenceHome(String presenceHome) {
        this.presenceHome = presenceHome;
    }

    public String getXdmsHome() {
        return xdmsHome;
    }

    public void setXdmsHome(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    public int getDispatchGroupMember() {
        return dispatchGroupMember;
    }

    public void setDispatchGroupMember(int dispatchGroupMember) {
        this.dispatchGroupMember = dispatchGroupMember;
    }

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public Integer getUserProfileIndex() {
        return userProfileIndex;
    }

    public void setUserProfileIndex(Integer userProfileIndex) {
        this.userProfileIndex = userProfileIndex;
    }

    @Override
    public String toString() {
        return "KnBulkSubsProfileDTO{mdn=" + mdn + ", networkName=" + networkName + ", corpId=" + corpId + "}";
    }
}
