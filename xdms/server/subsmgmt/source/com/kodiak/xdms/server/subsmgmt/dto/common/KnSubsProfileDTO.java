/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/30/10       7.0
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

package com.kodiak.xdms.server.subsmgmt.dto.common;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnSubsProfileDTO extends KnSubscriberProvDTO {

    private static final long serialVersionUID = 7526471155622676242L;

    //stores the XDMS Home
    private String XDMSHome;
    //stores the PoCHome of Subscriber
    private String poCHome;
    //stores the Presence Home of Subscriber
    private String presenceHome;
    // stores the last profile update time
    private long lastProfileUpdateTime;
    //stores the userAgent
    private String userAgent;
    //stores the corpContactListId
    private int corpContactListId;

    private String pocPttServerId;
    private String presPttServerId;

    //stores the corporation Id;
    private int corpId;

    //client password - should be used only with-in BO layer must not be passed outside
    private String clientPassword;
    private int clientPVmajorVer;
    private int clientPVminorVer;

    // stores the profile creation  time
    private long subsCreationTime;

    private int dynamicQosFlag;

    private int vocoderId;
    private String apnName;

    private String countryCode;

    private boolean isSyncDisabled;

    private KnConstants.HIERARCHY_TYPE hierarchyType;

    private String derivedKey;

    private int dispatchType;

    private int subsDefPttRadio;
    // Service Auth status set by AuthUser
    private int poCStatusAU;
    // Service Auth status set by Operator
    private int poCStatusOP;
    private String activeFS2;
    private String opsFS2;
    private String corpAdminFS2;
    private String xdmsFS2;

    private String ufmi;
    private String aliasMdn;
    private String xcapRootUri;
    private String featureRelVersion;
    private String oldUserId;
    private String deviceId;
    private Integer previousServiceAuthStatus;
    private String pttSettingDocId;
    private String clusterId;
    private String hierarchyId;
    private String hierarchyRoot;

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getOldUserId() {
        return oldUserId;
    }

    public void setOldUserId(String oldUserId) {
        this.oldUserId = oldUserId;
    }




    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUfmi() {
        return ufmi;
    }

    public void setUfmi(String ufmi) {
        this.ufmi = ufmi;
    }

	public String getDerivedKey() {
        return derivedKey;
    }

    public void setDerivedKey(String derivedKey) {
        this.derivedKey = derivedKey;
    }

    public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    public boolean isSyncDisabled() {
        return isSyncDisabled;
    }

    public void setSyncDisabled(boolean isSyncDisabled) {
        this.isSyncDisabled = isSyncDisabled;
    }

    /**
     * getter method for the Corporation Id
     *
     * @return int
     */
    public int getCorpId() {
        return corpId;
    }

    /**
     * setter method for the Corporation Id
     *
     * @param corpId int
     */
    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    /**
     * getter method for the XDMS Home
     *
     * @return String
     */
    public String getXDMSHome() {
        return XDMSHome;
    }

    /**
     * setter method for the XDMS Home
     *
     * @param XDMSHome String
     */
    public void setXDMSHome(String XDMSHome) {
        if (XDMSHome != null) {
            XDMSHome = XDMSHome.trim();
            if (XDMSHome.equals("")) {
                XDMSHome = null;
            }
        }
        this.XDMSHome = XDMSHome;
    }

    /**
     * getter method for the PoC Home
     *
     * @return String
     */
    public String getPoCHome() {
        return poCHome;
    }

    /**
     * setter method for the PoC Home
     *
     * @param poCHome String
     */
    public void setPoCHome(String poCHome) {
        if (poCHome != null) {
            poCHome = poCHome.trim();
            if (poCHome.equals("")) {
                poCHome = null;
            }
        }
        this.poCHome = poCHome;
    }

    /**
     * getter method for the Presence Home
     *
     * @return String
     */
    public String getPresenceHome() {
        return presenceHome;
    }

    /**
     * setter method for the Presence Home
     *
     * @param presenceHome String
     */
    public void setPresenceHome(String presenceHome) {
        if (presenceHome != null) {
            presenceHome = presenceHome.trim();
            if (presenceHome.equals("")) {
                presenceHome = null;
            }
        }
        this.presenceHome = presenceHome;
    }


    public long getLastProfileUpdateTime() {
        return lastProfileUpdateTime;
    }

    public void setLastProfileUpdateTime(long lastProfileUpdateTime) {
        this.lastProfileUpdateTime = lastProfileUpdateTime;
    }

    /**
     * getter method for the UserAgent
     *
     * @return String
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * setter method for the userAgent
     *
     * @param userAgent String
     */
    public void setUserAgent(String userAgent) {
        if (userAgent != null) {
            userAgent = userAgent.trim();
            if (userAgent.equals("")) {
                userAgent = null;
            }
        }
        this.userAgent = userAgent;
    }

    /**
     * getter method for the Corp Contact List Id
     *
     * @return int
     */
    public int getCorpContactListId() {
        return corpContactListId;
    }

    /**
     * setter method for the corp Contact List Id
     *
     * @param corpContactListId int
     */
    public void setCorpContactListId(int corpContactListId) {
        this.corpContactListId = corpContactListId;
    }

    /**
     * getter method for the client password (HA1 password)
     *
     * @return String
     */
    public String getClientPassword() {
        return clientPassword;
    }

    /**
     * setter method for the client Password (HA1 password)
     *
     * @param clientPassword String
     */
    public void setClientPassword(String clientPassword) {
        if (clientPassword != null) {
            clientPassword = clientPassword.trim();
            if (clientPassword.equals("")) {
                clientPassword = null;
            }
        }
        this.clientPassword = clientPassword;
    }

    public int getVocoderId() {
        return vocoderId;
    }

    public void setVocoderId(int vocoderId) {
        this.vocoderId = vocoderId;
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

    public long getSubsCreationTime() {
        return subsCreationTime;
    }

    public void setSubsCreationTime(long subsCreationTime) {
        this.subsCreationTime = subsCreationTime;
    }

    public int getDynamicQosFlag() {
        return dynamicQosFlag;
    }

    public void setDynamicQosFlag(int dynamicQosFlag) {
        this.dynamicQosFlag = dynamicQosFlag;
    }

    public String getApnName() {
        return apnName;
    }

    public void setApnName(String apnName) {
        this.apnName = apnName;
    }

    public int getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(int dispatchType) {
        this.dispatchType = dispatchType;
    }

    public int getSubsDefPttRadio() {
		return subsDefPttRadio;
	}

	public void setSubsDefPttRadio(int subsDefPttRadio) {
		this.subsDefPttRadio = subsDefPttRadio;
	}

    public int getPoCStatusAU() {
        return poCStatusAU;
    }

    public void setPoCStatusAU(int poCStatusAU) {
        this.poCStatusAU = poCStatusAU;
    }

    public int getPoCStatusOP() {
        return poCStatusOP;
    }

    public void setPoCStatusOP(int poCStatusOP) {
        this.poCStatusOP = poCStatusOP;
    }

	public String getAliasMdn() {
		return aliasMdn;
	}

	public void setAliasMdn(String aliasMdn) {
		this.aliasMdn = aliasMdn;
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

    public String getXcapRootUri() {
        return xcapRootUri;
    }

    public void setXcapRootUri(String xcapRootUri) {
        this.xcapRootUri = xcapRootUri;
    }

    public String getFeatureRelVersion() { return featureRelVersion; }

    public void setFeatureRelVersion(String featureRelVersion) { this.featureRelVersion = featureRelVersion; }

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

    public Integer getPreviousServiceAuthStatus() {
        return previousServiceAuthStatus;
    }

    public void setPreviousServiceAuthStatus(Integer previousServiceAuthStatus) {
        this.previousServiceAuthStatus = previousServiceAuthStatus;
    }

    public String getHierarchyRoot() {
        return hierarchyRoot;
    }

    public void setHierarchyRoot(String hierarchyRoot) {
        this.hierarchyRoot = hierarchyRoot;
    }

    @Override
    public String getPttSettingDocId() {
        return pttSettingDocId;
    }

    @Override
    public void setPttSettingDocId(String pttSettingDocId) {
        this.pttSettingDocId = pttSettingDocId;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(super.toString());
        strBuffer.append("[KnSubsProfileDTO --> ");
        strBuffer.append(", XDMS_Home - ").append(XDMSHome)
                .append(", PoC_Home - ").append(poCHome)
                .append(", Presence_Home - ").append(presenceHome)
                .append(", Corp_Id - ").append(corpId)
                .append(", Subscriber Creation Time - ").append(subsCreationTime)
                .append(", Last Profile Update Time - ").append(lastProfileUpdateTime)
                .append(", User_Agent - ").append(userAgent)
                .append(", Corp_Contact_List_Id - ").append(corpContactListId)
                .append(", Client_Password - ").append(clientPassword)
                .append(", CLIENT_PV_MAJOR_VERSION - ").append(clientPVmajorVer)
                .append(", CLIENT_PV_MINOR_VERSION - ").append(clientPVminorVer)
                .append(", DYNAMIC_QOS_FLAG - ").append(dynamicQosFlag)
                .append(", IS_SYNC_DIDABLED - ").append(isSyncDisabled)
                .append(", vocoderId - ").append(vocoderId)
                .append(", apnName - ").append(apnName)
                .append(", hierarchyType - ").append(hierarchyType)
                .append(", derivedKey - ").append(derivedKey)
                .append(", dispatchType - ").append(dispatchType)
                .append(", subsDefPttRadio - ").append(subsDefPttRadio)
                .append(", poCStatusAU - ").append(poCStatusAU)
                .append(", poCStatusOP - ").append(poCStatusOP)
                .append(", ufmi - ").append(ufmi)
                .append(", aliasMdn - ").append(KnGDPRTemplate.mdn(aliasMdn))
                .append(", ACTIVE_FS2 - ").append(activeFS2)
                .append(", OPS_FS2 - ").append(opsFS2)
                .append(", CORP_ADMIN_FS2 - ").append(corpAdminFS2)
                .append(", xdmsFS2 - ").append(xdmsFS2)
                .append(", xcapRootUri - ").append(xcapRootUri)
                .append(", featureRelVersion - ").append(featureRelVersion)
                .append(", presPttServerId - ").append(presPttServerId)
                .append(", pocPttServerId - ").append(pocPttServerId)
                .append(", deviceId - ").append(deviceId)
                .append(", previousServiceAuthStatus - ").append(previousServiceAuthStatus)
                .append(", pttSettingDocId - ").append(pttSettingDocId)
                .append(", clusterId - ").append(clusterId)
                .append(", hierarchyId - ").append(hierarchyId)
                .append(", hierarchyRoot - ").append(hierarchyRoot)
                .append("]");

        return strBuffer.toString();
    }
}
