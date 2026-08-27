/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.xdms.server.common.dto.intf.ISubscriberDTO;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.KnXDMError;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnSubscriberDTO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 12, 2011           7.0
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
public class KnSubscriberDTO implements ISubscriberDTO {

	private static final long serialVersionUID = 7526471155622676179L;

    private int dtoStatus;
    private KnXDMError errorObject;
    private String objectId;
    private IPopulate dtoObject;

    private String mdn;
    private String mcpttID;
    private String networkName;
    private int pubSubscriptionType;
    private int corpSubscriptionType;
    private int serviceAuthStatus;
    //added to specify the supervisory attribute for the group members and the group id
    private int supervisory;
    private int groupId;
    private int clientType;
    private int contact_type;
    private String callPermission;
    private int locWatcher;
    private String userAgent;
    private int clientPVMajorVersion;
    private String ua;
    private String isAuthUser;
    private String activeFS2;
    private String aliasMdn;
    private String userId;
    private int isOSMAuthorize;
    private int mcpttCompliance;
    private int callInitiatePermission;
    private int callReceivePermission;
    private int inCallPermission;
    private String videoCallPermission;
    private Integer videoCallInitiatePermission;
    private Integer videoCallReceivePermission;
    private Integer videoInCallPermission;
    private int licenseType;
    private int memberCorpId;
    private int broadcaster;
    private int isAffiliationEnabled;


	public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getMcpttID() {return mcpttID; }

    public void setMcpttID(String mcpttID) {this.mcpttID = mcpttID; }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public int getPubSubscriptionType() {
        return pubSubscriptionType;
    }

    public void setPubSubscriptionType(int pubSubscriptionType) {
        this.pubSubscriptionType = pubSubscriptionType;
    }

    public int getCorpSubscriptionType() {
        return corpSubscriptionType;
    }

    public void setCorpSubscriptionType(int corpSubscriptionType) {
        this.corpSubscriptionType = corpSubscriptionType;
    }

    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public int getDTOStatus() {
        return dtoStatus;
    }

    public void setDTOStatus(int dtoStatus) {
        this.dtoStatus = dtoStatus;
    }

    public KnXDMError getErrorObject() {
        return errorObject;
    }

    public void setErrorObject(KnXDMError errorObject) {
        this.errorObject = errorObject;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void populate(IPopulate dtoObject) {

    }

    public int getSupervisory() {
        return supervisory;
    }

    public void setSupervisory(int supervisory) {
        this.supervisory = supervisory;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getContact_type() {
        return contact_type;
    }

    public void setContact_type(int contact_type) {
        this.contact_type = contact_type;
    }

    public String getCallPermission() {
        return callPermission;
    }

    public void setCallPermission(String callPermission) {
        this.callPermission = callPermission;
    }

    public int getLocWatcher() {
        return locWatcher;
    }

    public void setLocWatcher(int locWatcher) {
        this.locWatcher = locWatcher;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getClientPVMajorVersion() {
        return clientPVMajorVersion;
    }

    public void setClientPVMajorVersion(int clientPVMajorVersion) {
        this.clientPVMajorVersion = clientPVMajorVersion;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getIsAuthUser() {
        return isAuthUser;
    }

    public void setIsAuthUser(String isAuthUser) {
        this.isAuthUser = isAuthUser;
    }

    
    public String getActiveFS2() {
		return activeFS2;
	}

	public void setActiveFS2(String activeFS2) {
		this.activeFS2 = activeFS2;
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

    public int getIsOSMAuthorize() {
        return isOSMAuthorize;
    }

    public void setIsOSMAuthorize(int isOSMAuthorize) {
        this.isOSMAuthorize = isOSMAuthorize;
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

	public String getVideoCallPermission() {
		return videoCallPermission;
	}

	public void setVideoCallPermission(String videoCallPermission) {
		this.videoCallPermission = videoCallPermission;
	}

	public Integer getVideoCallInitiatePermission() {
		return videoCallInitiatePermission;
	}

	public void setVideoCallInitiatePermission(Integer videoCallInitiatePermission) {
		this.videoCallInitiatePermission = videoCallInitiatePermission;
	}

	public Integer getVideoCallReceivePermission() {
		return videoCallReceivePermission;
	}

	public void setVideoCallReceivePermission(Integer videoCallReceivePermission) {
		this.videoCallReceivePermission = videoCallReceivePermission;
	}

	public Integer getVideoInCallPermission() {
		return videoInCallPermission;
	}

	public void setVideoInCallPermission(Integer videoInCallPermission) {
		this.videoInCallPermission = videoInCallPermission;
	}


    public int getMcpttCompliance() {return mcpttCompliance; }

    public void setMcpttCompliance(int mcpttCompliance) {this.mcpttCompliance = mcpttCompliance; }

    public int getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(int licenseType) {
        this.licenseType = licenseType;
    }

    public int getMemberCorpId() {
        return memberCorpId;
    }

    public void setMemberCorpId(int memberCorpId) {
        this.memberCorpId = memberCorpId;
    }

    public int getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(int broadcaster) {
        this.broadcaster = broadcaster;
    }

    public int getIsAffiliationEnabled() { return isAffiliationEnabled; }

    public void setIsAffiliationEnabled(int isAffiliationEnabled) { this.isAffiliationEnabled = isAffiliationEnabled; }

    @Override
    public String toString() {
        return "KnSubscriberDTO{" +
                "dtoStatus=" + dtoStatus +
                ", errorObject=" + errorObject +
                ", objectId='" + objectId + '\'' +
                ", dtoObject=" + dtoObject +
                ", mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", mcpttID='" + KnGDPRTemplate.mcpttId(mcpttID) + '\'' +
                ", networkName='" + KnGDPRTemplate.name(networkName) + '\'' +
                ", pubSubscriptionType=" + pubSubscriptionType +
                ", corpSubscriptionType=" + corpSubscriptionType +
                ", serviceAuthStatus=" + serviceAuthStatus +
                ", supervisory=" + supervisory +
                ", groupId=" + groupId +
                ", clientType=" + clientType +
                ", contact_type=" + contact_type +
                ", callPermission='" + callPermission + '\'' +
                ", locWatcher=" + locWatcher +
                ", userAgent=" + userAgent +
                ", clientPVMajorVersion=" + clientPVMajorVersion +
                ", ua=" + ua +
                ", isAuthUser=" + isAuthUser +
                ", activeFS2=" + activeFS2 +
                ", aliasMdn=" + KnGDPRTemplate.mdn(aliasMdn) +
                ", isOSMAuthorize=" + isOSMAuthorize +
                ", userId=" + KnGDPRTemplate.userId(userId) +
                ", mcpttCompliance=" + mcpttCompliance +
                ", callInitiatePermission=" + callInitiatePermission +
                ", callReceivePermission=" + callReceivePermission +
                ", inCallPermission=" + inCallPermission +
                ", videoCallPermission='" + videoCallPermission + '\'' +
                ", videoCallInitiatePermission=" + videoCallInitiatePermission +
                ", videoCallReceivePermission=" + videoCallReceivePermission +
                ", videoInCallPermission=" + videoInCallPermission +
                ", licenseType=" + licenseType +
                ", memberCorpId=" + memberCorpId +
                ", broadcaster=" + broadcaster +
                ", isAffiliationEnabled=" + isAffiliationEnabled +
                '}';
    }
}
