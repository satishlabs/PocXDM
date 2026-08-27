/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMMdnInfoDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 14, 2011           7.0
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
public class KnXDMMdnInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676133L;

    private String mdn;
    private String aliasMdn;
    private String userId;
    private String name;
    private int subsType;
    private long activeFs1;
    private String contactType;
    private String ufmi;
    private int clientMajorVersion;
    private String activeFs2;
    private String mcpttId;
    private List<String> addOnAliasIds;

    //SharedCallURIExtM
    private String sharedCalluri;
    private String sharedcalldispName;
    private String sharedcallentryTypeExtM;
    private String sharedcallContactTypeExtM;
    private String sharedcallClientTypeExM;
    private String sharedcallUaExM;
    private Integer sharedcallIsAuthUserExM;
    private String sharedcallActiveFSExM;
    private Integer sharedcallUserCheckPermExtM;
    private Integer sharedcallUserSvcPermExtM;
    private Integer sharedcallAmbientListPermExtM;
    private Integer sharedcallDiscreetListPermExtM;
    private Integer sharedcallEmergInitCancelPermExtM;
    private Integer sharedcallUserSvcStatusExtM;
    private Integer sharedcallDiscreetListStatusExtM;
    //onNetwork
    private String onNetworkMCPTTGroupInfoUri;
    private String onNetworkMcPttGroupInfoDispName;
    private Integer onNetworkMcPttGroupInfoGroupTypeExtM;
    private Integer onNetworkMcPttGroupInfoGroupmemberCountExtM;
    private Integer onNetworkMcPttGroupInfoAvatarId;
    private String onNetworkMcPttGroupInfoGroupZoneExtM;
    private String onNetworkMcPttGroupInfoGoupChannelExtM;
    private Integer onNetworkMcPttGroupInfoGrouppriorityExtM;
    private Integer onNetworkMcPttGroupInfoIsAbdgGroupExtM;
    private String onNetworkMcPttGroupInfoAbdgGroupOwnerExtMUri;
    private String onNetworkMcPttGroupInfoAbdgGroupOwnerExtMDispName;
    private String privateCallUri;
    private String privateCallDispName;
    private Integer isBroadcasterExtM;
    private Integer cameraTypeExtM;
    private Integer isExtCorpGroupExtM;
    private Integer corpID;
    private String subscriberFs2;
    private Integer isTgssGroupExtM;
    private Object packageInfo;
    private Integer unConfirmedPullExtM;

    private Integer isLargeGroupExtM;
    private Integer isMcxGroupExtM;
    private Integer groupmemberListCountExtM;
    public Integer getIsExtCorpGroupExtM() {
        return isExtCorpGroupExtM;
    }

    public void setIsExtCorpGroupExtM(Integer isExtCorpGroupExtM) {
        this.isExtCorpGroupExtM = isExtCorpGroupExtM;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSubsType() {
        return subsType;
    }

    public void setSubsType(int subsType) {
        this.subsType = subsType;
    }

    public long getActiveFs1() {
        return activeFs1;
    }

    public void setActiveFs1(long activeFs1) {
        this.activeFs1 = activeFs1;
    }

    public String getUfmi() {
        return ufmi;
    }

    public void setUfmi(String ufmi) {
        this.ufmi = ufmi;
    }

    public String getContactType() {return contactType; }

    public void setContactType(String contactType) {this.contactType = contactType; }

    public int getClientMajorVersion() {
        return clientMajorVersion;
    }

    public void setClientMajorVersion(int clientMajorVersion) {
        this.clientMajorVersion = clientMajorVersion;
    }

    public String getActiveFs2() {
        return activeFs2;
    }

    public void setActiveFs2(String activeFs2) {
        this.activeFs2 = activeFs2;
    }

    public String getMcpttId() {
        return mcpttId;
    }

    public void setMcpttId(String mcpttId) {
        this.mcpttId = mcpttId;
    }

    public List<String> getAddOnAliasIds() {
        if (addOnAliasIds == null) {
            addOnAliasIds = new ArrayList<>();
        }
        return addOnAliasIds;
    }

    public String getSharedCalluri() {
        return sharedCalluri;
    }

    public void setSharedCalluri(String sharedCalluri) {
        this.sharedCalluri = sharedCalluri;
    }

    public String getSharedcalldispName() {
        return sharedcalldispName;
    }

    public void setSharedcalldispName(String sharedcalldispName) {
        this.sharedcalldispName = sharedcalldispName;
    }

    public String getSharedcallentryTypeExtM() {
        return sharedcallentryTypeExtM;
    }

    public void setSharedcallentryTypeExtM(String sharedcallentryTypeExtM) {
        this.sharedcallentryTypeExtM = sharedcallentryTypeExtM;
    }

    public String getSharedcallContactTypeExtM() {
        return sharedcallContactTypeExtM;
    }

    public void setSharedcallContactTypeExtM(String sharedcallContactTypeExtM) {
        this.sharedcallContactTypeExtM = sharedcallContactTypeExtM;
    }

    public String getSharedcallClientTypeExM() {
        return sharedcallClientTypeExM;
    }

    public void setSharedcallClientTypeExM(String sharedcallClientTypeExM) {
        this.sharedcallClientTypeExM = sharedcallClientTypeExM;
    }

    public String getSharedcallUaExM() {
        return sharedcallUaExM;
    }

    public void setSharedcallUaExM(String sharedcallUaExM) {
        this.sharedcallUaExM = sharedcallUaExM;
    }

    public Integer getSharedcallIsAuthUserExM() {
        return sharedcallIsAuthUserExM;
    }

    public void setSharedcallIsAuthUserExM(Integer sharedcallIsAuthUserExM) {
        this.sharedcallIsAuthUserExM = sharedcallIsAuthUserExM;
    }

    public String getSharedcallActiveFSExM() {
        return sharedcallActiveFSExM;
    }

    public void setSharedcallActiveFSExM(String sharedcallActiveFSExM) {
        this.sharedcallActiveFSExM = sharedcallActiveFSExM;
    }

    public Integer getSharedcallUserCheckPermExtM() {
        return sharedcallUserCheckPermExtM;
    }

    public void setSharedcallUserCheckPermExtM(Integer sharedcallUserCheckPermExtM) {
        this.sharedcallUserCheckPermExtM = sharedcallUserCheckPermExtM;
    }

    public Integer getSharedcallUserSvcPermExtM() {
        return sharedcallUserSvcPermExtM;
    }

    public void setSharedcallUserSvcPermExtM(Integer sharedcallUserSvcPermExtM) {
        this.sharedcallUserSvcPermExtM = sharedcallUserSvcPermExtM;
    }

    public Integer getSharedcallAmbientListPermExtM() {
        return sharedcallAmbientListPermExtM;
    }

    public void setSharedcallAmbientListPermExtM(Integer sharedcallAmbientListPermExtM) {
        this.sharedcallAmbientListPermExtM = sharedcallAmbientListPermExtM;
    }

    public Integer getSharedcallDiscreetListPermExtM() {
        return sharedcallDiscreetListPermExtM;
    }

    public void setSharedcallDiscreetListPermExtM(Integer sharedcallDiscreetListPermExtM) {
        this.sharedcallDiscreetListPermExtM = sharedcallDiscreetListPermExtM;
    }

    public Integer getSharedcallEmergInitCancelPermExtM() {
        return sharedcallEmergInitCancelPermExtM;
    }

    public void setSharedcallEmergInitCancelPermExtM(Integer sharedcallEmergInitCancelPermExtM) {
        this.sharedcallEmergInitCancelPermExtM = sharedcallEmergInitCancelPermExtM;
    }

    public Integer getSharedcallUserSvcStatusExtM() {
        return sharedcallUserSvcStatusExtM;
    }

    public void setSharedcallUserSvcStatusExtM(Integer sharedcallUserSvcStatusExtM) {
        this.sharedcallUserSvcStatusExtM = sharedcallUserSvcStatusExtM;
    }

    public Integer getSharedcallDiscreetListStatusExtM() {
        return sharedcallDiscreetListStatusExtM;
    }

    public void setSharedcallDiscreetListStatusExtM(Integer sharedcallDiscreetListStatusExtM) {
        this.sharedcallDiscreetListStatusExtM = sharedcallDiscreetListStatusExtM;
    }

    public String getOnNetworkMCPTTGroupInfoUri() {
        return onNetworkMCPTTGroupInfoUri;
    }

    public void setOnNetworkMCPTTGroupInfoUri(String onNetworkMCPTTGroupInfoUri) {
        this.onNetworkMCPTTGroupInfoUri = onNetworkMCPTTGroupInfoUri;
    }

    public String getOnNetworkMcPttGroupInfoDispName() {
        return onNetworkMcPttGroupInfoDispName;
    }

    public void setOnNetworkMcPttGroupInfoDispName(String onNetworkMcPttGroupInfoDispName) {
        this.onNetworkMcPttGroupInfoDispName = onNetworkMcPttGroupInfoDispName;
    }

    public String getPrivateCallUri() {
        return privateCallUri;
    }

    public void setPrivateCallUri(String privateCallUri) {
        this.privateCallUri = privateCallUri;
    }

    public String getPrivateCallDispName() {
        return privateCallDispName;
    }

    public void setPrivateCallDispName(String privateCallDispName) {
        this.privateCallDispName = privateCallDispName;
    }

    public Integer getOnNetworkMcPttGroupInfoGroupTypeExtM() {
        return onNetworkMcPttGroupInfoGroupTypeExtM;
    }

    public void setOnNetworkMcPttGroupInfoGroupTypeExtM(Integer onNetworkMcPttGroupInfoGroupTypeExtM) {
        this.onNetworkMcPttGroupInfoGroupTypeExtM = onNetworkMcPttGroupInfoGroupTypeExtM;
    }

    public Integer getOnNetworkMcPttGroupInfoGroupmemberCountExtM() {
        return onNetworkMcPttGroupInfoGroupmemberCountExtM;
    }

    public void setOnNetworkMcPttGroupInfoGroupmemberCountExtM(Integer onNetworkMcPttGroupInfoGroupmemberCountExtM) {
        this.onNetworkMcPttGroupInfoGroupmemberCountExtM = onNetworkMcPttGroupInfoGroupmemberCountExtM;
    }

    public Integer getOnNetworkMcPttGroupInfoAvatarId() {
        return onNetworkMcPttGroupInfoAvatarId;
    }

    public void setOnNetworkMcPttGroupInfoAvatarId(Integer onNetworkMcPttGroupInfoAvatarId) {
        this.onNetworkMcPttGroupInfoAvatarId = onNetworkMcPttGroupInfoAvatarId;
    }

    public String getOnNetworkMcPttGroupInfoGroupZoneExtM() {
        return onNetworkMcPttGroupInfoGroupZoneExtM;
    }

    public void setOnNetworkMcPttGroupInfoGroupZoneExtM(String onNetworkMcPttGroupInfoGroupZoneExtM) {
        this.onNetworkMcPttGroupInfoGroupZoneExtM = onNetworkMcPttGroupInfoGroupZoneExtM;
    }

    public String getOnNetworkMcPttGroupInfoGoupChannelExtM() {
        return onNetworkMcPttGroupInfoGoupChannelExtM;
    }

    public void setOnNetworkMcPttGroupInfoGoupChannelExtM(String onNetworkMcPttGroupInfoGoupChannelExtM) {
        this.onNetworkMcPttGroupInfoGoupChannelExtM = onNetworkMcPttGroupInfoGoupChannelExtM;
    }

    public Integer getOnNetworkMcPttGroupInfoGrouppriorityExtM() {
        return onNetworkMcPttGroupInfoGrouppriorityExtM;
    }

    public void setOnNetworkMcPttGroupInfoGrouppriorityExtM(Integer onNetworkMcPttGroupInfoGrouppriorityExtM) {
        this.onNetworkMcPttGroupInfoGrouppriorityExtM = onNetworkMcPttGroupInfoGrouppriorityExtM;
    }

    public Integer getIsBroadcasterExtM() {
        return isBroadcasterExtM;
    }

    public void setIsBroadcasterExtM(Integer isBroadcasterExtM) {
        this.isBroadcasterExtM = isBroadcasterExtM;
    }

    public Integer getCameraTypeExtM() {return cameraTypeExtM; }

    public void setCameraTypeExtM(Integer cameraTypeExtM) {this.cameraTypeExtM = cameraTypeExtM; }

    public Integer getCorpID() {
        return corpID;
    }

    public void setCorpID(Integer corpID) {
        this.corpID = corpID;
    }

    public String getSubscriberFs2() {
        return subscriberFs2;
    }

    public void setSubscriberFs2(String subscriberFs2) {
        this.subscriberFs2 = subscriberFs2;
    }

    public Integer getIsTgssGroupExtM() {
        return isTgssGroupExtM;
    }

    public void setIsTgssGroupExtM(Integer isTgssGroupExtM) {
        this.isTgssGroupExtM = isTgssGroupExtM;
    }

    public Integer getOnNetworkMcPttGroupInfoIsAbdgGroupExtM() {
        return onNetworkMcPttGroupInfoIsAbdgGroupExtM;
    }

    public void setOnNetworkMcPttGroupInfoIsAbdgGroupExtM(Integer onNetworkMcPttGroupInfoIsAbdgGroupExtM) {
        this.onNetworkMcPttGroupInfoIsAbdgGroupExtM = onNetworkMcPttGroupInfoIsAbdgGroupExtM;
    }

    public String getOnNetworkMcPttGroupInfoAbdgGroupOwnerExtMUri() {
        return onNetworkMcPttGroupInfoAbdgGroupOwnerExtMUri;
    }

    public void setOnNetworkMcPttGroupInfoAbdgGroupOwnerExtMUri(String onNetworkMcPttGroupInfoAbdgGroupOwnerExtMUri) {
        this.onNetworkMcPttGroupInfoAbdgGroupOwnerExtMUri = onNetworkMcPttGroupInfoAbdgGroupOwnerExtMUri;
    }

    public String getOnNetworkMcPttGroupInfoAbdgGroupOwnerExtMDispName() {
        return onNetworkMcPttGroupInfoAbdgGroupOwnerExtMDispName;
    }

    public void setOnNetworkMcPttGroupInfoAbdgGroupOwnerExtMDispName(String onNetworkMcPttGroupInfoAbdgGroupOwnerExtMDispName) {
        this.onNetworkMcPttGroupInfoAbdgGroupOwnerExtMDispName = onNetworkMcPttGroupInfoAbdgGroupOwnerExtMDispName;
    }

    public Object getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(Object packageInfo) {
        this.packageInfo = packageInfo;
    }

    public Integer getUnConfirmedPullExtM() {
        return unConfirmedPullExtM;
    }

    public void setUnConfirmedPullExtM(Integer unConfirmedPullExtM) {
        this.unConfirmedPullExtM = unConfirmedPullExtM;
    }

    public Integer getIsLargeGroupExtM() { return isLargeGroupExtM; }

    public void setIsLargeGroupExtM(Integer isLargeGroupExtM) { this.isLargeGroupExtM = isLargeGroupExtM; }

    public Integer getIsMcxGroupExtM() { return isMcxGroupExtM; }

    public void setIsMcxGroupExtM(Integer isMcxGroupExtM) { this.isMcxGroupExtM = isMcxGroupExtM; }

    public Integer getGroupmemberListCountExtM() { return groupmemberListCountExtM; }

    public void setGroupmemberListCountExtM(Integer groupmemberListCountExtM) { this.groupmemberListCountExtM = groupmemberListCountExtM; }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(" Mdn - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", Name - ").append(KnGDPRTemplate.name(name))
                .append(", corpID - ").append(corpID)
                .append(", subsType - ").append(subsType)
                .append(", activeFs1 - ").append(activeFs1)
                .append(", ufmi - ").append(ufmi)
                .append(", contactType - ").append(contactType)
                .append(", activeFs1 - ").append(activeFs1)
                .append(", aliasMdn - ").append(KnGDPRTemplate.mdn(aliasMdn))
                .append(", userId - ").append(KnGDPRTemplate.userId(userId))
                .append(", clientMajorVersion - ").append(clientMajorVersion)
                .append(", mcpttId - ").append(KnGDPRTemplate.mcpttId(mcpttId))
                .append(", addOnAliasIds - ").append(addOnAliasIds)
                .append(", sharedCalluri - ").append(sharedCalluri)
                .append(", sharedcalldispName - ").append(sharedcalldispName)
                .append(", sharedcallentryTypeExtM - ").append(sharedcallentryTypeExtM)
                .append(", sharedcallContactTypeExtM - ").append(sharedcallContactTypeExtM)
                .append(", sharedsharedcallClientTypeExMcalldispName - ").append(sharedcallClientTypeExM)
                .append(", sharedcallUaExM - ").append(sharedcallUaExM)
                .append(", sharedcallIsAuthUserExM - ").append(sharedcallIsAuthUserExM)
                .append(", sharedcallActiveFSExM - ").append(sharedcallActiveFSExM)
                .append(", sharedcallUserCheckPermExtM - ").append(sharedcallUserCheckPermExtM)
                .append(", sharedcallUserSvcPermExtM - ").append(sharedcallUserSvcPermExtM)
                .append(", sharedcallAmbientListPermExtM - ").append(sharedcallAmbientListPermExtM)
                .append(", sharedcallDiscreetListPermExtM - ").append(sharedcallDiscreetListPermExtM)
                .append(", sharedcallEmergInitCancelPermExtM - ").append(sharedcallEmergInitCancelPermExtM)
                .append(", sharedcallUserSvcStatusExtM - ").append(sharedcallUserSvcStatusExtM)
                .append(", sharedcallDiscreetListStatusExtM - ").append(sharedcallDiscreetListStatusExtM)
                .append(", onNetworkMCPTTGroupInfoUri - ").append(onNetworkMCPTTGroupInfoUri)
                .append(", onNetworkMcPttGroupInfoDispName - ").append(onNetworkMcPttGroupInfoDispName)
                .append(", privateCallUri - ").append(privateCallUri)
                .append(", privateCallDispName - ").append(privateCallDispName)
                .append(", onNetworkMcPttGroupInfoGroupTypeExtM - ").append(onNetworkMcPttGroupInfoGroupTypeExtM)
                .append(", onNetworkMcPttGroupInfoGroupmemberCountExtM - ").append(onNetworkMcPttGroupInfoGroupmemberCountExtM)
                .append(", onNetworkMcPttGroupInfoAvatarId - ").append(onNetworkMcPttGroupInfoAvatarId)
                .append(", onNetworkMcPttGroupInfoGroupZoneExtM - ").append(onNetworkMcPttGroupInfoGroupZoneExtM)
                .append(", onNetworkMcPttGroupInfoGoupChannelExtM - ").append(onNetworkMcPttGroupInfoGoupChannelExtM)
                .append(", onNetworkMcPttGroupInfoGrouppriorityExtM - ").append(onNetworkMcPttGroupInfoGrouppriorityExtM)
                .append(", onNetworkMcPttGroupInfoIsAbdgGroupExtM - ").append(onNetworkMcPttGroupInfoIsAbdgGroupExtM)
                .append(", onNetworkMcPttGroupInfoAbdgGroupOwnerExtMUri - ").append(onNetworkMcPttGroupInfoAbdgGroupOwnerExtMUri)
                .append(", onNetworkMcPttGroupInfoAbdgGroupOwnerExtMDispName - ").append(onNetworkMcPttGroupInfoAbdgGroupOwnerExtMDispName)
                .append(", isBroadcasterExtM - ").append(isBroadcasterExtM)
                .append(", activeFs2 - ").append(activeFs2)
                .append(", cameraTypeExtM - ").append(cameraTypeExtM)
                .append(", isExtCorpGroupExtM - ").append(isExtCorpGroupExtM)
                .append(", isTgssGroupExtM - ").append(isTgssGroupExtM)
                .append(", isLargeGroupExtM - ").append(isLargeGroupExtM)
                .append(", isMcxGroupExtM - ").append(isMcxGroupExtM)
                .append(", groupmemberListCountExtM - ").append(groupmemberListCountExtM)
                .append(", subscriberFs2 - ").append(subscriberFs2)
                .append(", packageInfo - ").append(packageInfo)
                .append(", unConfirmedPullExtM - ").append(unConfirmedPullExtM);
        return strBuffer.toString();

    }

    public String getObjectId() {
        return mdn;
    }
}
