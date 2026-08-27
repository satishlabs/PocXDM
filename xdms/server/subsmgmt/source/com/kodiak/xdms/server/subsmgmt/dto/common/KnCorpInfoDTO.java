/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   Kn.java
 * Subsystem:
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
package com.kodiak.xdms.server.subsmgmt.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnConstants;

public class KnCorpInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676238L;

    private String extCorpId;
    private int corpId;
    private String XDMSHome;
    private String corporateName;
    private int maxSubscribers;
    private int maxCorpLists;
    private int maxMembersPerCorpList;
    private int maxCorpGroups;
    private int maxMembersPerCorpGroup;
    private int pairedContactListId;
    private String pocHome;
    private String linkedGwKey;
    private int dynamicQosFlag;
    private KnConstants.HIERARCHY_TYPE hierarchyType;
    private int webDispatchEnabled;
    private int subsDefPttRadio;
    private String corpFS2;
	private String opsCorpFS2;
    private String featureRelVersion;
    private int maxDispatchGroups;
    private int maxMemPerDispatchGroup;
    private Boolean dispatchEnabled;
    private Boolean isInterOpEnabled;
    private int maxExtSubsPerCorp;
    private int maxMemPerBCGrp;
    private int maxRadioChannels;
    private int maxZones;
    private int maxChannelsPerZone;
    private int maxLargeTalkGroup;
    private int maxGroupProfile;
    private int maxUserProfile;
    private int maxAssignProfiles;
    private int maxCorpHiearchyLevel;
    private String xdmCorpFS2Set;

	public KnConstants.HIERARCHY_TYPE getHierarchyType() {
        return hierarchyType;
    }

    public void setHierarchyType(KnConstants.HIERARCHY_TYPE hierarchyType) {
        this.hierarchyType = hierarchyType;
    }
    /**
     * getter method for Ext Corp Id
     *
     * @return String
     */
    public String getExtCorpId() {
        return extCorpId;
    }

    /**
     * setter method for the ext Corp Id
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
     * getter method for the Corp Id
     *
     * @return int
     */
    public int getCorpId() {
        return corpId;
    }

    /**
     * setter method for the Corp Id
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

    /**
     * getter method for the Max Subscribers
     *
     * @return int
     */
    public int getMaxSubscribers() {
        return maxSubscribers;
    }

    /**
     * setter method for the Max Subscribers
     *
     * @param maxSubscribers int
     */
    public void setMaxSubscribers(int maxSubscribers) {
        this.maxSubscribers = maxSubscribers;
    }

    /**
     * getter method for the Max Corp Lists
     *
     * @return int
     */
    public int getMaxCorpLists() {
        return maxCorpLists;
    }

    /**
     * setter method for the Max Corp Lists
     *
     * @param maxCorpLists int
     */
    public void setMaxCorpLists(int maxCorpLists) {
        this.maxCorpLists = maxCorpLists;
    }

    /**
     * getter method for the Max Members per Corp List
     *
     * @return int
     */
    public int getMaxMembersPerCorpList() {
        return maxMembersPerCorpList;
    }

    /**
     * setter method for the Max Members per Corp List
     *
     * @param maxMembersPerCorpList int
     */
    public void setMaxMembersPerCorpList(int maxMembersPerCorpList) {
        this.maxMembersPerCorpList = maxMembersPerCorpList;
    }

    /**
     * getter method for the Max Corp Groups
     *
     * @return int
     */
    public int getMaxCorpGroups() {
        return maxCorpGroups;
    }

    /**
     * setter method for the Max Corp Groups
     *
     * @param maxCorpGroups int
     */
    public void setMaxCorpGroups(int maxCorpGroups) {
        this.maxCorpGroups = maxCorpGroups;
    }

    /**
     * getter method for the Max Members per Corp Group
     *
     * @return int
     */
    public int getMaxMembersPerCorpGroup() {
        return maxMembersPerCorpGroup;
    }

    /**
     * setter method for the Max Members per Corp Group
     *
     * @param maxMembersPerCorpGroup int
     */
    public void setMaxMembersPerCorpGroup(int maxMembersPerCorpGroup) {
        this.maxMembersPerCorpGroup = maxMembersPerCorpGroup;
    }

    /**
     *   getter method for the paired contact list ID
     * @return    int
     */
    public int getPairedContactListId() {
           return pairedContactListId;
    }

    /**
     * setter method for the Max Members per Corp Group
     * @param pairedContactListId int
     */
    public void setPairedContactListId(int pairedContactListId) {
           this.pairedContactListId = pairedContactListId;
    }

    /**
     * getter method for the POC Home
     * @return String
     */
    public String getPocHome() {
        return pocHome;
    }

    /**
     * setter method for the POC Home
     * @param pocHome String
     */
    public void setPocHome(String pocHome) {
        if (pocHome != null) {
            pocHome = pocHome.trim();
            if (pocHome.equals("")){
                pocHome = null;
            }
        }
        this.pocHome = pocHome;
    }

    public String getLinkedGwKey() {
        return linkedGwKey;
    }

    public void setLinkedGwKey(String linkedGwKey) {
        this.linkedGwKey = linkedGwKey;
    }

    public int getDynamicQosFlag() {
        return dynamicQosFlag;
    }

    public void setDynamicQosFlag(int dynamicQosFlag) {
        this.dynamicQosFlag = dynamicQosFlag;
    }

    public int getWebDispatchEnabled() {
        return webDispatchEnabled;
    }

    public void setWebDispatchEnabled(int webDispatchEnabled) {
        this.webDispatchEnabled = webDispatchEnabled;
    }
    public int getSubsDefPttRadio() {
		return subsDefPttRadio;
	}

	public void setSubsDefPttRadio(int subsDefPttRadio) {
		this.subsDefPttRadio = subsDefPttRadio;
	}
	
    public String getCorpFS2() {
		return corpFS2;
	}

	public void setCorpFS2(String corpFS2) {
		this.corpFS2 = corpFS2;
	}

	public String getOpsCorpFS2() {
		return opsCorpFS2;
	}

	public void setOpsCorpFS2(String opsCorpFS2) {
		this.opsCorpFS2 = opsCorpFS2;
	}

    public String getFeatureRelVersion() { return featureRelVersion; }

    public void setFeatureRelVersion(String featureRelVersion) { this.featureRelVersion = featureRelVersion; }

    public int getMaxDispatchGroups() {
        return maxDispatchGroups;
    }

    public void setMaxDispatchGroups(int maxDispatchGroups) {
        this.maxDispatchGroups = maxDispatchGroups;
    }

    public int getMaxMemPerDispatchGroup() {
        return maxMemPerDispatchGroup;
    }

    public void setMaxMemPerDispatchGroup(int maxMemPerDispatchGroup) {
        this.maxMemPerDispatchGroup = maxMemPerDispatchGroup;
    }

    public Boolean getDispatchEnabled() {
        return dispatchEnabled;
    }

    public void setDispatchEnabled(Boolean dispatchEnabled) {
        this.dispatchEnabled = dispatchEnabled;
    }

    public Boolean getIsInterOpEnabled() {
        return isInterOpEnabled;
    }

    public void setIsInterOpEnabled(Boolean isInterOpEnabled) {
        this.isInterOpEnabled = isInterOpEnabled;
    }

    public int getMaxExtSubsPerCorp() {
        return maxExtSubsPerCorp;
    }

    public void setMaxExtSubsPerCorp(int maxExtSubsPerCorp) {
        this.maxExtSubsPerCorp = maxExtSubsPerCorp;
    }

    public int getMaxMemPerBCGrp() {
        return maxMemPerBCGrp;
    }

    public void setMaxMemPerBCGrp(int maxMemPerBCGrp) {
        this.maxMemPerBCGrp = maxMemPerBCGrp;
    }

    public int getMaxRadioChannels() {
        return maxRadioChannels;
    }

    public void setMaxRadioChannels(int maxRadioChannels) {
        this.maxRadioChannels = maxRadioChannels;
    }

    public int getMaxZones() {
        return maxZones;
    }

    public void setMaxZones(int maxZones) {
        this.maxZones = maxZones;
    }

    public int getMaxChannelsPerZone() {
        return maxChannelsPerZone;
    }

    public void setMaxChannelsPerZone(int maxChannelsPerZone) {
        this.maxChannelsPerZone = maxChannelsPerZone;
    }

    public int getMaxLargeTalkGroup() {
        return maxLargeTalkGroup;
    }

    public void setMaxLargeTalkGroup(int maxLargeTalkGroup) {
        this.maxLargeTalkGroup = maxLargeTalkGroup;
    }

    public int getMaxGroupProfile() {
        return maxGroupProfile;
    }

    public void setMaxGroupProfile(int maxGroupProfile) {
        this.maxGroupProfile = maxGroupProfile;
    }

    public int getMaxUserProfile() {
        return maxUserProfile;
    }

    public void setMaxUserProfile(int maxUserProfile) {
        this.maxUserProfile = maxUserProfile;
    }

    public int getMaxAssignProfiles() {
        return maxAssignProfiles;
    }

    public void setMaxAssignProfiles(int maxAssignProfiles) {
        this.maxAssignProfiles = maxAssignProfiles;
    }

    public int getMaxCorpHiearchyLevel() {
        return maxCorpHiearchyLevel;
    }

    public void setMaxCorpHiearchyLevel(int maxCorpHiearchyLevel) {
        this.maxCorpHiearchyLevel = maxCorpHiearchyLevel;
    }

    public String getXdmCorpFS2Set() {
        return xdmCorpFS2Set;
    }

    public void setXdmCorpFS2Set(String xdmCorpFS2Set) {
        this.xdmCorpFS2Set = xdmCorpFS2Set;
    }

    @Override
    public String toString() {
        return "KnCorpInfoDTO{" +
                "extCorpId='" + extCorpId + '\'' +
                ", corpId=" + corpId +
                ", XDMSHome='" + XDMSHome + '\'' +
                ", corporateName='" + corporateName + '\'' +
                ", maxSubscribers=" + maxSubscribers +
                ", maxCorpLists=" + maxCorpLists +
                ", maxMembersPerCorpList=" + maxMembersPerCorpList +
                ", maxCorpGroups=" + maxCorpGroups +
                ", maxMembersPerCorpGroup=" + maxMembersPerCorpGroup +
                ", pairedContactListId=" + pairedContactListId +
                ", pocHome='" + pocHome + '\'' +
                ", linkedGwKey='" + linkedGwKey + '\'' +
                ", dynamicQosFlag=" + dynamicQosFlag +
                ", hierarchyType=" + hierarchyType +
                ", webDispatchEnabled=" + webDispatchEnabled +
                ", subsDefPttRadio=" + subsDefPttRadio +
                ", corpFS2='" + corpFS2 + '\'' +
                ", opsCorpFS2='" + opsCorpFS2 + '\'' +
                ", featureRelVersion='" + featureRelVersion + '\'' +
                ", maxDispatchGroups=" + maxDispatchGroups +
                ", maxMemPerDispatchGroup=" + maxMemPerDispatchGroup +
                ", maxExtSubsPerCorp=" + maxExtSubsPerCorp +
                ", dispatchEnabled=" + dispatchEnabled +
                ", isInterOpEnabled=" + isInterOpEnabled +
                ", maxMemPerBCGrp=" + maxMemPerBCGrp +
                ", maxRadioChannels=" + maxRadioChannels +
                ", maxZones=" + maxZones +
                ", maxChannelsPerZone=" + maxChannelsPerZone +
                ", maxLargeTalkGroup=" + maxLargeTalkGroup +
                ", maxGroupProfile=" + maxGroupProfile +
                ", maxUserProfile=" + maxUserProfile +
                ", maxAssignProfiles=" + maxAssignProfiles +
                ", maxCorpHiearchyLevel=" + maxCorpHiearchyLevel +
                ", xdmCorpFS2Set='" + xdmCorpFS2Set + '\'' +
                '}';
    }

    public String getObjectId() {
        return this.extCorpId;
    }
}
