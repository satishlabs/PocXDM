/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpMemberDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 15, 2011      7.0
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
package com.kodiak.common.commdto.common;


import java.util.Map;

public class KnXDMCorpContactDTO extends KnXDMMdnInfoDTO {

	private static final long serialVersionUID = 7526471155622676123L;

    private String contactType;
    private String serviceAuthStatus;
    private String maxContactsLimitFlag;
    private int distributionType;
    private int contactCount;
    private int supervisor;
    private int clientType;
    private int locWatcher;

    private String activationCode;
    private String activationTimestamp;
    private String parentId;
    private String expiryTime;
    private long activeFs1;
    private int broadcaster;
    private Map<String,Object> customMap;
    private String tpUser;
    private String tpAccount;
    private int corpId;

    //Modified for 8.1.1 Changes
    private int callInitiatePermission;
    private int callReceivePermission;;
    private int inCallPermission;
    private int videoCallInitiatePermission;
    private int videoCallReceivePermission;
    private int videoInCallPermission;
    private int dispatchType;
    private String ua;
    // TP dynamic cgmt changes
    private int groupModifyPerm;
    private int licenseType;
    private int isAuthUser;
    private int isOSMAuthorize;
    private int mcpttCompliance;
    private int  isAffiliationEnabled;
    private int memberType;
    private Integer commonContact;
    private String mcpttId;
    private String deviceId;
    private String billingMDN;
    private String extGatewayId;

    public int getVideoCallInitiatePermission() {
        return videoCallInitiatePermission;
    }

    public void setVideoCallInitiatePermission(int videoCallInitiatePermission) {
        this.videoCallInitiatePermission = videoCallInitiatePermission;
    }

    public int getVideoCallReceivePermission() {
        return videoCallReceivePermission;
    }

    public void setVideoCallReceivePermission(int videoCallReceivePermission) {
        this.videoCallReceivePermission = videoCallReceivePermission;
    }

    public int getVideoInCallPermission() {
        return videoInCallPermission;
    }

    public void setVideoInCallPermission(int videoInCallPermission) {
        this.videoInCallPermission = videoInCallPermission;
    }

    public int getGroupModifyPerm() {
		return groupModifyPerm;
	}

	public void setGroupModifyPerm(int groupModifyPerm) {
		this.groupModifyPerm = groupModifyPerm;
	}

	public int getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(int broadcaster) {
        this.broadcaster = broadcaster;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(String serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public String getMaxContactsLimitFlag() {
        return maxContactsLimitFlag;
    }

    public void setMaxContactsLimitFlag(String maxContactsLimitFlag) {
        this.maxContactsLimitFlag = maxContactsLimitFlag;
    }

    public int getDistributionType() {
        return distributionType;
    }

    public void setDistributionType(int distributionType) {
        this.distributionType = distributionType;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }

    public int getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(int supervisor) {
        this.supervisor = supervisor;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getActivationTimestamp() {
        return activationTimestamp;
    }

    public void setActivationTimestamp(String activationTimestamp) {
        this.activationTimestamp = activationTimestamp;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    public long getActiveFs1() {
        return activeFs1;
    }

    public void setActiveFs1(long activeFs1) {
        this.activeFs1 = activeFs1;
    }

    public Map<String, Object> getCustomMap() {
        return customMap;
    }

    public void setCustomMap(Map<String, Object> customMap) {
        this.customMap = customMap;
    }

    public String getTpUser() {
        return tpUser;
    }

    public void setTpUser(String tpUserId) {
        this.tpUser = tpUserId;
    }

    public String getTpAccount() {
        return tpAccount;
    }

    public void setTpAccount(String tpAccount) {
        this.tpAccount = tpAccount;
    }

    public int getCallInitiatePermission() {
        return callInitiatePermission;
    }

    public void setCallInitiatePermission(int callInitiatePermission) {
        this.callInitiatePermission = callInitiatePermission;
    }

    public int getCallReceivePermission() {
        return callReceivePermission;
    }

    public void setCallReceivePermission(int callReceivePermission) {
        this.callReceivePermission = callReceivePermission;
    }

    public int getInCallPermission() {
        return inCallPermission;
    }

    public void setInCallPermission(int inCallPermission) {
        this.inCallPermission = inCallPermission;
    }

    public int getLocWatcher() {
        return locWatcher;
    }

    public void setLocWatcher(int locWatcher) {
        this.locWatcher = locWatcher;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public int getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(int dispatchType) {
        this.dispatchType = dispatchType;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public int getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(int licenseType) {
        this.licenseType = licenseType;
    }

    public int getIsAuthUser() {
        return isAuthUser;
    }

    public void setIsAuthUser(int isAuthUser) {
        this.isAuthUser = isAuthUser;
    }

    public int getIsOSMAuthorize() {
        return isOSMAuthorize;
    }

    public void setIsOSMAuthorize(int isOSMAuthorize) {
        this.isOSMAuthorize = isOSMAuthorize;
    }
    
    public int getIsAffiliationEnabled() {
		return isAffiliationEnabled;
	}

	public void setIsAffiliationEnabled(int isAffiliationEnabled) {
		this.isAffiliationEnabled = isAffiliationEnabled;
	}

	public int getMemberType() {
		return memberType;
	}

	public void setMemberType(int memberType) {
		this.memberType = memberType;
	}

    public int getMcpttCompliance() { return mcpttCompliance; }

    public void setMcpttCompliance(int mcpttCompliance) { this.mcpttCompliance = mcpttCompliance; }

    public Integer getCommonContact() {
        return commonContact;
    }

    public void setCommonContact(Integer commonContact) {
        this.commonContact = commonContact;
    }

    @Override
    public String getMcpttId() {
        return mcpttId;
    }

    @Override
    public void setMcpttId(String mcpttId) {
        this.mcpttId = mcpttId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getBillingMDN() {
        return billingMDN;
    }

    public void setBillingMDN(String billingMDN) {
        this.billingMDN = billingMDN;
    }

    public String getExtGatewayId() {
        return extGatewayId;
    }

    public void setExtGatewayId(String extGatewayId) {
        this.extGatewayId = extGatewayId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(super.toString())
                .append(", ContactType - ").append(contactType)
                .append(", ServiceAuthStatus - ").append(serviceAuthStatus)
                .append(", MaxContactsLimitFlag - ").append(maxContactsLimitFlag)
                .append(", MemberDistType - ").append(distributionType)
                .append(", ContactCount - ").append(contactCount)
                .append(", supervisor - ").append(supervisor)
                .append(", activationCode - ").append(activationCode)
                .append(", activationTimestamp - ").append(activationTimestamp)
                .append(", parentId - ").append(parentId)
                .append(", clientType - ").append(clientType)
                .append(", expiryTime - ").append(expiryTime)
                .append(", activeFs1 - ").append(activeFs1)
                .append(", broadcaster - ").append(broadcaster)
                .append(", tpUser - ").append(tpUser)
                .append(", tpAccount - ").append(tpAccount)
                .append(", callInitiatePermission - ").append(callInitiatePermission)
                .append(", callReceivePermission - ").append(callReceivePermission)
                .append(", inCallPermission - ").append(inCallPermission)
                .append(", locWatcher - ").append(locWatcher)
                .append(", corpId - ").append(corpId)
                .append(", dispatchType - ").append(dispatchType)
                .append(", ua - ").append(ua)
                .append(", groupModifyPerm - ").append(groupModifyPerm)
                .append(", licenseType - ").append(licenseType)
                .append(", isOSMAuthorize - ").append(isOSMAuthorize)
                .append(", isAuthUser - ").append(isAuthUser)
                .append(", mcpttCompliance - ").append(mcpttCompliance)
                .append(", isAffiliationEnabled - ").append(isAffiliationEnabled)
        		.append(", memberType - ").append(memberType)
        		.append(", commonContact - ").append(commonContact)
                .append(", mcpttId - ").append(mcpttId)
                .append(", billingMDN - ").append(billingMDN)
                .append(", deviceId - ").append(deviceId)
                .append(", extGatewayId - ").append(extGatewayId)
                .append(", VideoCallInitiatePermission - ").append(videoCallInitiatePermission)
                .append(", VideoCallReceivePermission - ").append(videoCallReceivePermission)
                .append(", VideoInCallPermission - ").append(videoInCallPermission);

        return sb.toString();
    }
}
