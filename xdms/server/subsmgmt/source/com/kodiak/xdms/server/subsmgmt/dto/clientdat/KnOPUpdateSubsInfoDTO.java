/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnOPUpdateSubsInfoDTO
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/6/11       7.0
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
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;

public class KnOPUpdateSubsInfoDTO extends KnOPProvDTO {

    private static final long serialVersionUID = 7526471155622676237L;

    private int corpId;
    private boolean activeFSChanged;
    private boolean subsNameChanged;
    private boolean subsTypeChanged;
    private boolean clientTypeChanged;
    private boolean cleanUpTGSData;
    private String pocPttId;
    private String presPttId;
    private Boolean corpAutoPairing;
    private Boolean isOldCorp;
    private Boolean syncDisability;
    private Integer serviceAuthStatus;
    private Integer subsClientType;
    private Integer newSubsClientType;
    private Integer clientPvMajorVersion;
    private Integer clientPvMinorVersion;
    private String clientPassword;
    private String activeFS2;
    private String oldActiveFS2;
    private List<String> userProfileMdns;
    private Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap;
    private Map<String, String> mcsXcapRootUriMap;
    private long lastProfileUpdateTime;
    Map<String, String> profileMdnActivsFsMap;
    private String subsFS2;
    private String mcId;
    private String mcDataId;
    private String mcVideoId;
    private String mcPttId;
    private String networkName;
    private String deviceId;
    private boolean isUpgradePkg;
    private boolean isAutoAssignSkip;

    //Just for getting oldFS of ProfileMDN
    private Map<String, KnOPSubsProfileInfoDTO> mdnUpmFsMap;

    public Map<String, KnOPSubsProfileInfoDTO> getMdnUpmFsMap() {
        return mdnUpmFsMap;
    }

    public void setMdnUpmFsMap(Map<String, KnOPSubsProfileInfoDTO> mdnUpmFsMap) {
        this.mdnUpmFsMap = mdnUpmFsMap;
    }

    public long getLastProfileUpdateTime() {
        return lastProfileUpdateTime;
    }

    public void setLastProfileUpdateTime(long lastProfileUpdateTime) {
        this.lastProfileUpdateTime = lastProfileUpdateTime;
    }


    public Boolean getIsOldCorp() {
        return isOldCorp;
    }

    public void setIsOldCorp(Boolean isOldCorp) {
        this.isOldCorp = isOldCorp;
    }

    public Boolean getCorpAutoPairing() {
        return corpAutoPairing;
    }

    public void setCorpAutoPairing(Boolean corpAutoPairing) {
        this.corpAutoPairing = corpAutoPairing;
    }

    public boolean isSubsTypeChanged() {
        return subsTypeChanged;
    }

    public void setSubsTypeChanged(boolean subsTypeChanged) {
        this.subsTypeChanged = subsTypeChanged;
    }

    public boolean isSubsNameChanged() {
        return subsNameChanged;
    }

    public void setSubsNameChanged(boolean subsNameChanged) {
        this.subsNameChanged = subsNameChanged;
    }

    public boolean isActiveFSChanged() {
        return activeFSChanged;
    }

    public void setActiveFSChanged(boolean activeFSChanged) {
        this.activeFSChanged = activeFSChanged;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public boolean isCleanUpTGSData() {
        return cleanUpTGSData;
    }

    public void setCleanUpTGSData(boolean cleanUpTGSData) {
        this.cleanUpTGSData = cleanUpTGSData;
    }

    public String getPocPttId() {
        return pocPttId;
    }

    public void setPocPttId(String pocPttId) {
        this.pocPttId = pocPttId;
    }

    public String getPresPttId() {
        return presPttId;
    }

    public void setPresPttId(String presPttId) {
        this.presPttId = presPttId;
    }

    public Boolean getSyncDisability() {
        return syncDisability;
    }

    public void setSyncDisability(Boolean syncDisability) {
        this.syncDisability = syncDisability;
    }

    public Integer getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(Integer serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public Integer getSubsClientType() {
        return subsClientType;
    }

    public void setSubsClientType(Integer subsClientType) {
        this.subsClientType = subsClientType;
    }

    public Integer getNewSubsClientType() {
        return newSubsClientType;
    }

    public void setNewSubsClientType(Integer newSubsClientType) {
        this.newSubsClientType = newSubsClientType;
    }

    public Integer getClientPvMajorVersion() {
        return clientPvMajorVersion;
    }

    public void setClientPvMajorVersion(Integer clientPvMajorVersion) {
        this.clientPvMajorVersion = clientPvMajorVersion;
    }

    public boolean isClientTypeChanged() {
        return clientTypeChanged;
    }

    public void setClientTypeChanged(boolean clientTypeChanged) {
        this.clientTypeChanged = clientTypeChanged;
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

    public String getOldActiveFS2() {
        return oldActiveFS2;
    }

    public void setOldActiveFS2(String oldActiveFS2) {
        this.oldActiveFS2 = oldActiveFS2;
    }

    public Integer getClientPvMinorVersion() {
        return clientPvMinorVersion;
    }

    public void setClientPvMinorVersion(Integer clientPvMinorVersion) {
        this.clientPvMinorVersion = clientPvMinorVersion;
    }

    public List<String> getUserProfileMdns() {
        return userProfileMdns;
    }

    public void setUserProfileMdns(List<String> userProfileMdns) {
        this.userProfileMdns = userProfileMdns;
    }

    public Map<String, Collection<KnDocChangeListDTO>> getProfileMdnEtagMap() {
        return profileMdnEtagMap;
    }

    public void setProfileMdnEtagMap(Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap) {
        this.profileMdnEtagMap = profileMdnEtagMap;
    }

    public Map<String, String> getMcsXcapRootUriMap() {
        return mcsXcapRootUriMap;
    }

    public void setMcsXcapRootUriMap(Map<String, String> mcsXcapRootUriMap) {
        this.mcsXcapRootUriMap = mcsXcapRootUriMap;
    }

    public Map<String, String> getProfileMdnActivsFsMap() {
        return profileMdnActivsFsMap;
    }

    public void setProfileMdnActivsFsMap(Map<String, String> profileMdnActivsFsMap) {
        this.profileMdnActivsFsMap = profileMdnActivsFsMap;
    }

    public String getSubsFS2() {
        return subsFS2;
    }

    public void setSubsFS2(String subsFS2) {
        this.subsFS2 = subsFS2;
    }

    public String getMcId() {
        return mcId;
    }

    public void setMcId(String mcId) {
        this.mcId = mcId;
    }

    public String getMcDataId() {
        return mcDataId;
    }

    public void setMcDataId(String mcDataId) {
        this.mcDataId = mcDataId;
    }

    public String getMcVideoId() {
        return mcVideoId;
    }

    public void setMcVideoId(String mcVideoId) {
        this.mcVideoId = mcVideoId;
    }

    public String getMcPttId() {
        return mcPttId;
    }

    public void setMcPttId(String mcPttId) {
        this.mcPttId = mcPttId;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isAutoAssignSkip() {
        return isAutoAssignSkip;
    }

    public void setAutoAssignSkip(boolean autoAssignSkip) {
        isAutoAssignSkip = autoAssignSkip;
    }

    public boolean isUpgradePkg() {
        return isUpgradePkg;
    }

    public void setUpgradePkg(boolean upgradePkg) {
        isUpgradePkg = upgradePkg;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(super.toString());
        strBuffer.append(" [KnOPUpdateSubsInfoDTO --> ");
        strBuffer.append(", CORP_ID - ").append(corpId)
                .append(", activeFSChanged - ").append(activeFSChanged)
                .append(", subsNameChanged - ").append(subsNameChanged)
                .append(", subsTypeChanged - ").append(subsTypeChanged)
                .append(", cleanUpTGSData - ").append(cleanUpTGSData)
                .append(", corpAutoPairing - ").append(corpAutoPairing)
                .append(", isOldCorp - ").append(isOldCorp)
                .append(", activeFS2 - ").append(activeFS2)
                .append(", serviceAuthStatus - ").append(serviceAuthStatus)
                .append(", syncDisability - ").append(syncDisability)
                .append(", newSubsClientType - ").append(newSubsClientType)
                .append(", subsClientType - ").append(subsClientType)
                .append(", oldActiveFS2 - ").append(oldActiveFS2)
                .append(", clientPvMajorVersion - ").append(clientPvMajorVersion)
                .append(", clientPvMinorVersion - ").append(clientPvMinorVersion)
                .append(", clientTypeChanged - ").append(clientTypeChanged)
                .append(", clientPassword - ").append(clientPassword)
                .append(", userProfileMdns - ").append(userProfileMdns)
                .append(", profileMdnEtagMap - ").append(profileMdnEtagMap)
                .append(", mcsXcapRootUriMap - ").append(mcsXcapRootUriMap)
                .append(", lastProfileUpdateTime - ").append(lastProfileUpdateTime)
                .append(", profileMdnActivsFsMap - ").append(profileMdnActivsFsMap)
                .append(", subsFS2 - ").append(subsFS2)
                .append(", mcId - ").append(mcId)
                .append(", mcDataId - ").append(mcDataId)
                .append(", mcPttId - ").append(mcPttId)
                .append(", mcVideoId - ").append(mcVideoId)
                .append(", networkName - ").append(networkName)
                .append(", deviceId - ").append(deviceId)
                .append(", isUpgradePkg - ").append(isUpgradePkg)
                .append(", isAutoAssignSkip - ").append(isAutoAssignSkip)
                .append("]");

        return strBuffer.toString();
    }
}
