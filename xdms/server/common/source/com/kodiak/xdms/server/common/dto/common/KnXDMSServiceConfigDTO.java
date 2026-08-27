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
 * Ravi Shanker .P       1/4/11       7.0
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
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;

public class KnXDMSServiceConfigDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676182L;

    private String pttServerId;
    private String primaryXDMSUri;
    private String geoXDMSUri;
    private String XcapRootUri;
    private String XcapRootUri_Wifi;
    private String authRealm;
    private int maxPublicContactsPerSubs;
    private int maxPublicPOCGrpsPerSubs;
    private int maxMembersPerPublicPOCGrp;
    private String publicPocGrpConfURITemplate;
    private int maxCorpContactsPerSubs;
    private int maxCorpGrpsPerSubs;
    private String corpPocGrpConfURITemplate;
    private int maxSubscrPerCorp;
    private int maxSublistsPerCorp;
    private int maxMembersPerCorpSublist;
    private int maxExtContactsPerCorp;
    private int maxPOCGrpsPerCorp;
    private int maxMembersPerCorpPOCGrp;
    private int maxContactPerRequest;
    private int supervisoryEnabled;
    private int dispatchEnabled;
    private int maxDispatchGroup;
    private int maxMembersPerDispatchGroup;
    private int maxDispatchMembersPerDispatchGroup;
    private int blockUnsupportedDevices;
    private int enablePocDonorRadioSupport;
    private int enableTGS;
    private int maxCampedGroups;
    private int maxPriority;
    private int tgscanningClient;
    private int maxExtSubsPerCorp;
    private int enableBCGrpFeature;
    private int maxMemPerBCGrp;
    private int blockBlackListDevices;
    private int enable3rdPartyPocClientSupport;
    private int maxSGMdnsPerGroup;
    //LMR client type changes
    private int pttRadioScanListSize;
    private int pttRadioChannelListSize;
    private int pttRadioDefScanMode;
    // WebDispatcherChanges:
    private int webDispatchEnabled;
    private int subsDefPttRadio;
    private int interopLicenceType;
    private int maxSGPatchPerGrp;
    private int iDenInterOp;
    private int emergFeature;
    private int ambientListening;
    private int discreteListening;
    private int userCheck;
    private int userSvcCtrl;
    private int maxChannelsPerZone;
    private int maxRadioChannels;
    private int maxZones;
    private int largeGrpSupport;
    private int maxLrgGrpPerCorp;
    private int maxMemPerLrgGrp;
    private int maxLrgBGrpPerCorp;
    private int maxMemPerLrgBGrp;
    private int multiSimSession;
    private int mcVideoEnabled;
    private int mcVideoUnCfrmPullEnabled;
    private Integer privacyAmbDiscListen;
    private String mcsXcapRootUriWifi;
    private String kmsUriWifi;
    private int maxCorpGrpsPerLargeDispatch;

    /**
     * getter method for xdm server pttServer Id
     *
     * @return String
     */
    public String getPttServerId() {
        return pttServerId;
    }

    /**
     * setter method for xdm server pttServer Id
     *
     * @param pttServerId String
     */
    public void setPttServerId(String pttServerId) {
        if (pttServerId != null) {
            pttServerId = pttServerId.trim();
            if (pttServerId.equals("")) {
                pttServerId = null;
            }
        }
        this.pttServerId = pttServerId;
    }

    /**
     * getter method for the Auth realm
     *
     * @return String
     */
    public String getAuthRealm() {
        return authRealm;
    }

    /**
     * setter method for the Auth Realm
     *
     * @param authRealm String
     */
    public void setAuthRealm(String authRealm) {
        if (authRealm != null) {
            authRealm = authRealm.trim();
            if (authRealm.equals("")) {
                authRealm = null;
            }
        }
        this.authRealm = authRealm;
    }

    /**
     * getter method for Primary XDMS Uri
     *
     * @return String
     */
    public String getPrimaryXDMSUri() {
        return primaryXDMSUri;
    }

    /**
     * setter method for Primary XDMS Uri
     *
     * @param primaryXDMSUri String
     */
    public void setPrimaryXDMSUri(String primaryXDMSUri) {
        if (primaryXDMSUri != null) {
            primaryXDMSUri = primaryXDMSUri.trim();
            if (primaryXDMSUri.equals("")) {
                primaryXDMSUri = null;
            }
        }
        this.primaryXDMSUri = primaryXDMSUri;
    }

    /**
     * getter method for the Geo XDMS Uri
     *
     * @return String
     */
    public String getGeoXDMSUri() {
        return geoXDMSUri;
    }

    /**
     * setter method for the XDMS Uri
     *
     * @param geoXDMSUri String
     */
    public void setGeoXDMSUri(String geoXDMSUri) {
        if (geoXDMSUri != null) {
            geoXDMSUri = geoXDMSUri.trim();
            if (geoXDMSUri.equals("")) {
                geoXDMSUri = null;
            }
        }
        this.geoXDMSUri = geoXDMSUri;
    }

    /**
     * getter method for Xcap Root Uri
     *
     * @return Strign
     */
    public String getXcapRootUri() {
        return XcapRootUri;
    }

    /**
     * setter method for the Xcap Root Uri
     *
     * @param xcapRootUri String
     */
    public void setXcapRootUri(String xcapRootUri) {
        XcapRootUri = xcapRootUri;
    }


    public String getXcapRootUri_Wifi() {
        return XcapRootUri_Wifi;
    }

    public void setXcapRootUri_Wifi(String xcapRootUri_Wifi) {
        XcapRootUri_Wifi = xcapRootUri_Wifi;
    }

    /**
     * getter method for the Max Public Contacts Per Subscriber
     *
     * @return int
     */
    public int getMaxPublicContactsPerSubs() {
        return maxPublicContactsPerSubs;
    }

    /**
     * Setter method for the Max Public Contacts Per Subscriber
     *
     * @param maxPublicContactsPerSubs int
     */
    public void setMaxPublicContactsPerSubs(int maxPublicContactsPerSubs) {
        this.maxPublicContactsPerSubs = maxPublicContactsPerSubs;
    }

    /**
     * getter method for the Max Public POC Grps Per Subscriber
     *
     * @return int
     */
    public int getMaxPublicPOCGrpsPerSubs() {
        return maxPublicPOCGrpsPerSubs;
    }

    /**
     * setter method for the Max Public POC Grps Per Subscriber
     *
     * @param maxPublicPOCGrpsPerSubs int
     */
    public void setMaxPublicPOCGrpsPerSubs(int maxPublicPOCGrpsPerSubs) {
        this.maxPublicPOCGrpsPerSubs = maxPublicPOCGrpsPerSubs;
    }

    /**
     * getter method for the Max Members per Public POC Grp Per Subscriber
     *
     * @return int
     */
    public int getMaxMembersPerPublicPOCGrp() {
        return maxMembersPerPublicPOCGrp;
    }

    /**
     * setter method for the Max Members per Public POC Grp Per Subscriber
     *
     * @param maxMembersPerPublicPOCGrp int
     */
    public void setMaxMembersPerPublicPOCGrp(int maxMembersPerPublicPOCGrp) {
        this.maxMembersPerPublicPOCGrp = maxMembersPerPublicPOCGrp;
    }

    /**
     * getter method for the Public Poc Grp Conf URI Template
     *
     * @return String
     */
    public String getPublicPocGrpConfURITemplate() {
        return publicPocGrpConfURITemplate;
    }

    /**
     * setter method for the public POC Grp Conf URI Template
     *
     * @param publicPocGrpConfURITemplate String
     */
    public void setPublicPocGrpConfURITemplate(String publicPocGrpConfURITemplate) {
        if (publicPocGrpConfURITemplate != null) {
            publicPocGrpConfURITemplate = publicPocGrpConfURITemplate.trim();
            if (publicPocGrpConfURITemplate.equals("")) {
                publicPocGrpConfURITemplate = null;
            }
        }
        this.publicPocGrpConfURITemplate = publicPocGrpConfURITemplate;
    }

    /**
     * getter method for the Max Corp Contacts Per Subscriber
     *
     * @return int
     */
    public int getMaxCorpContactsPerSubs() {
        return maxCorpContactsPerSubs;
    }

    /**
     * setter method for the Max Corp Contacts Per subscriber
     *
     * @param maxCorpContactsPerSubs int
     */
    public void setMaxCorpContactsPerSubs(int maxCorpContactsPerSubs) {
        this.maxCorpContactsPerSubs = maxCorpContactsPerSubs;
    }

    /**
     * getter method for the max Corp Grps per Subscriber
     *
     * @return int
     */
    public int getMaxCorpGrpsPerSubs() {
        return maxCorpGrpsPerSubs;
    }

    /**
     * setter method for the max Corp grps per Subscriber
     *
     * @param maxCorpGrpsPerSubs int
     */
    public void setMaxCorpGrpsPerSubs(int maxCorpGrpsPerSubs) {
        this.maxCorpGrpsPerSubs = maxCorpGrpsPerSubs;
    }

    /**
     * getter method for Corp POC Grp Conf URI Template
     *
     * @return String
     */
    public String getCorpPocGrpConfURITemplate() {
        return corpPocGrpConfURITemplate;
    }

    /**
     * setter method for Corp POC Grp Conf URI Template
     *
     * @param corpPocGrpConfURITemplate String
     */
    public void setCorpPocGrpConfURITemplate(String corpPocGrpConfURITemplate) {
        if (corpPocGrpConfURITemplate != null) {
            corpPocGrpConfURITemplate = corpPocGrpConfURITemplate.trim();
            if (corpPocGrpConfURITemplate.equals("")) {
                corpPocGrpConfURITemplate = null;
            }
        }
        this.corpPocGrpConfURITemplate = corpPocGrpConfURITemplate;
    }

    /**
     * getter method for the Max Subscribers per Corporation
     *
     * @return int
     */
    public int getMaxSubscrPerCorp() {
        return maxSubscrPerCorp;
    }

    /**
     * setter method for the Max Subscribers per Corporation
     *
     * @param maxSubscrPerCorp int
     */
    public void setMaxSubscrPerCorp(int maxSubscrPerCorp) {
        this.maxSubscrPerCorp = maxSubscrPerCorp;
    }

    /**
     * getter method for the Max Sub Lists per Corporation
     *
     * @return int
     */
    public int getMaxSublistsPerCorp() {
        return maxSublistsPerCorp;
    }

    /**
     * setter method for the Max Sub Lists per Corporation
     *
     * @param maxSublistsPerCorp int
     */
    public void setMaxSublistsPerCorp(int maxSublistsPerCorp) {
        this.maxSublistsPerCorp = maxSublistsPerCorp;
    }

    /**
     * getter method for the Max Members per Corporation sub list
     *
     * @return int
     */
    public int getMaxMembersPerCorpSublist() {
        return maxMembersPerCorpSublist;
    }

    /**
     * setter method for the Max Members per Corporation sub list
     *
     * @param maxMembersPerCorpSublist int
     */
    public void setMaxMembersPerCorpSublist(int maxMembersPerCorpSublist) {
        this.maxMembersPerCorpSublist = maxMembersPerCorpSublist;
    }

    /**
     * getter method for the Max Ext Contacts per Corporation
     *
     * @return int
     */
    public int getMaxExtContactsPerCorp() {
        return maxExtContactsPerCorp;
    }

    /**
     * setter method for the Max Ext Contacts per Corporation
     *
     * @param maxExtContactsPerCorp int
     */
    public void setMaxExtContactsPerCorp(int maxExtContactsPerCorp) {
        this.maxExtContactsPerCorp = maxExtContactsPerCorp;
    }

    /**
     * getter method for the Max PoC Grps per Corporation
     *
     * @return int
     */
    public int getMaxPOCGrpsPerCorp() {
        return maxPOCGrpsPerCorp;
    }

    /**
     * setter method for the Max PoC Grps per Corporation
     *
     * @param maxPOCGrpsPerCorp int
     */
    public void setMaxPOCGrpsPerCorp(int maxPOCGrpsPerCorp) {
        this.maxPOCGrpsPerCorp = maxPOCGrpsPerCorp;
    }

    /**
     * getter method for the Max Members per Corporation per Poc Grp
     *
     * @return int
     */
    public int getMaxMembersPerCorpPOCGrp() {
        return maxMembersPerCorpPOCGrp;
    }

    /**
     * setter method for the Max Members per Corporation per Poc Grp
     *
     * @param maxMembersPerCorpPOCGrp int
     */
    public void setMaxMembersPerCorpPOCGrp(int maxMembersPerCorpPOCGrp) {
        this.maxMembersPerCorpPOCGrp = maxMembersPerCorpPOCGrp;
    }

    public int getMaxContactPerRequest() {
        return maxContactPerRequest;
    }

    public void setMaxContactPerRequest(int maxContactPerRequest) {
        this.maxContactPerRequest = maxContactPerRequest;
    }

    public int getSupervisoryEnabled() {
        return supervisoryEnabled;
    }

    public void setSupervisoryEnabled(int supervisoryEnabled) {
        this.supervisoryEnabled = supervisoryEnabled;
    }

    public int getDispatchEnabled() {
        return dispatchEnabled;
    }

    public void setDispatchEnabled(int dispatchEnabled) {
        this.dispatchEnabled = dispatchEnabled;
    }

    public int getMaxDispatchGroup() {
        return maxDispatchGroup;
    }

    public void setMaxDispatchGroup(int maxDispatchGroup) {
        this.maxDispatchGroup = maxDispatchGroup;
    }

    public int getMaxMembersPerDispatchGroup() {
        return maxMembersPerDispatchGroup;
    }

    public void setMaxMembersPerDispatchGroup(int maxMembersPerDispatchGroup) {
        this.maxMembersPerDispatchGroup = maxMembersPerDispatchGroup;
    }

    public int getMaxDispatchMembersPerDispatchGroup() {
        return maxDispatchMembersPerDispatchGroup;
    }

    public void setMaxDispatchMembersPerDispatchGroup(int maxDispatchMembersPerDispatchGroup) {
        this.maxDispatchMembersPerDispatchGroup = maxDispatchMembersPerDispatchGroup;
    }

    public int getBlockUnsupportedDevices() {
        return blockUnsupportedDevices;
    }

    public void setBlockUnsupportedDevices(int blockUnsupportedDevices) {
        this.blockUnsupportedDevices = blockUnsupportedDevices;
    }

    public int getEnablePocDonorRadioSupport() {
        return enablePocDonorRadioSupport;
    }

    public void setEnablePocDonorRadioSupport(int enablePocDonorRadioSupport) {
        this.enablePocDonorRadioSupport = enablePocDonorRadioSupport;
    }

    public int getEnableTGS() {
        return enableTGS;
    }

    public void setEnableTGS(int enableTGS) {
        this.enableTGS = enableTGS;
    }

    public int getMaxCampedGroups() {
        return maxCampedGroups;
    }

    public void setMaxCampedGroups(int maxCampedGroups) {
        this.maxCampedGroups = maxCampedGroups;
    }

    public String getObjectId() {
        return pttServerId;  //To change body of implemented methods use File | Settings | File Templates.
    }

	public int getMaxPriority() {
		return maxPriority;
	}



	public void setMaxPriority(int maxPriority) {
		this.maxPriority = maxPriority;
	}

	public int getTgscanningClient() {
		return tgscanningClient;
	}

	public void setTgscanningClient(int tgscanningClient) {
		this.tgscanningClient = tgscanningClient;
	}
    public int getMaxExtSubsPerCorp() {
        return maxExtSubsPerCorp;
    }

    public void setMaxExtSubsPerCorp(int maxExtSubsPerCorp) {
        this.maxExtSubsPerCorp = maxExtSubsPerCorp;
    }

    public int getEnableBCGrpFeature() {
        return enableBCGrpFeature;
    }

    public void setEnableBCGrpFeature(int enableBCGrpFeature) {
        this.enableBCGrpFeature = enableBCGrpFeature;
    }

    public int getMaxMemPerBCGrp() {
        return maxMemPerBCGrp;
    }

    public void setMaxMemPerBCGrp(int maxMemPerBCGrp) {
        this.maxMemPerBCGrp = maxMemPerBCGrp;
    }

    public int getBlockBlackListDevices() {
        return blockBlackListDevices;
    }

    public void setBlockBlackListDevices(int blockBlackListDevices) {
        this.blockBlackListDevices = blockBlackListDevices;
    }

    public int getEnable3rdPartyPocClientSupport() {
        return enable3rdPartyPocClientSupport;
    }

    public void setEnable3rdPartyPocClientSupport(int enable3rdPartyPocClientSupport) {
        this.enable3rdPartyPocClientSupport = enable3rdPartyPocClientSupport;
    }

    public int getMaxSGMdnsPerGroup() {
        return maxSGMdnsPerGroup;
    }

    public void setMaxSGMdnsPerGroup(int maxSGMdnsPerGroup) {
        this.maxSGMdnsPerGroup = maxSGMdnsPerGroup;
    }

    public int getPttRadioScanListSize() {
		return pttRadioScanListSize;
	}

	public void setPttRadioScanListSize(int pttRadioScanListSize) {
		this.pttRadioScanListSize = pttRadioScanListSize;
	}

	public int getPttRadioChannelListSize() {
		return pttRadioChannelListSize;
	}

	public void setPttRadioChannelListSize(int pttRadioChannelListSize) {
		this.pttRadioChannelListSize = pttRadioChannelListSize;
	}
    
	public int getPttRadioDefScanMode() {
		return pttRadioDefScanMode;
	}

	public void setPttRadioDefScanMode(int pttRadioDefScanMode) {
		this.pttRadioDefScanMode = pttRadioDefScanMode;
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

    public int getInteropLicenceType() {
        return interopLicenceType;
    }

    public void setInteropLicenceType(int interopLicenceType) {
        this.interopLicenceType = interopLicenceType;
    }

    public int getMaxSGPatchPerGrp() {
        return maxSGPatchPerGrp;
    }

    public void setMaxSGPatchPerGrp(int maxSGPatchPerGrp) {
        this.maxSGPatchPerGrp = maxSGPatchPerGrp;
    }

    public int getiDenInterOp() {
		return iDenInterOp;
	}

	public void setiDenInterOp(int iDenInterOp) {
		this.iDenInterOp = iDenInterOp;
	}

    public int getEmergFeature() {
        return emergFeature;
    }

    public void setEmergFeature(int emergFeature) {
        this.emergFeature = emergFeature;
    }

    public int getAmbientListening() {
        return ambientListening;
    }

    public void setAmbientListening(int ambientListening) {
        this.ambientListening = ambientListening;
    }

    public int getDiscreteListening() {
        return discreteListening;
    }

    public void setDiscreteListening(int discreteListening) {
        this.discreteListening = discreteListening;
    }

    public int getUserCheck() {
        return userCheck;
    }

    public void setUserCheck(int userCheck) {
        this.userCheck = userCheck;
    }

    public int getUserSvcCtrl() {
        return userSvcCtrl;
    }

    public void setUserSvcCtrl(int userSvcCtrl) {
        this.userSvcCtrl = userSvcCtrl;
    }

    public int getMaxChannelsPerZone() {
        return maxChannelsPerZone;
    }

    public void setMaxChannelsPerZone(int maxChannelsPerZone) {
        this.maxChannelsPerZone = maxChannelsPerZone;
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

    public int getLargeGrpSupport() {
        return largeGrpSupport;
    }

    public void setLargeGrpSupport(int largeGrpSupport) {
        this.largeGrpSupport = largeGrpSupport;
    }

    public int getMaxLrgGrpPerCorp() {
        return maxLrgGrpPerCorp;
    }

    public void setMaxLrgGrpPerCorp(int maxLrgGrpPerCorp) {
        this.maxLrgGrpPerCorp = maxLrgGrpPerCorp;
    }

    public int getMaxMemPerLrgGrp() {
        return maxMemPerLrgGrp;
    }

    public void setMaxMemPerLrgGrp(int maxMemPerLrgGrp) {
        this.maxMemPerLrgGrp = maxMemPerLrgGrp;
    }

    public int getMaxLrgBGrpPerCorp() {
        return maxLrgBGrpPerCorp;
    }

    public void setMaxLrgBGrpPerCorp(int maxLrgBGrpPerCorp) {
        this.maxLrgBGrpPerCorp = maxLrgBGrpPerCorp;
    }

    public int getMaxMemPerLrgBGrp() {
        return maxMemPerLrgBGrp;
    }

    public void setMaxMemPerLrgBGrp(int maxMemPerLrgBGrp) {
        this.maxMemPerLrgBGrp = maxMemPerLrgBGrp;
    }

    public int getMultiSimSession() { return multiSimSession; }

    public void setMultiSimSession(int multiSimSession) { this.multiSimSession = multiSimSession; }

    public int getMcVideoEnabled() {
        return mcVideoEnabled;
    }

    public void setMcVideoEnabled(int mcVideoEnabled) {
        this.mcVideoEnabled = mcVideoEnabled;
    }

    public int getMcVideoUnCfrmPullEnabled() {
        return mcVideoUnCfrmPullEnabled;
    }

    public void setMcVideoUnCfrmPullEnabled(int mcVideoUnCfrmPullEnabled) {
        this.mcVideoUnCfrmPullEnabled = mcVideoUnCfrmPullEnabled;
    }

    public Integer getPrivacyAmbDiscListen() {
        return privacyAmbDiscListen;
    }

    public void setPrivacyAmbDiscListen(Integer privacyAmbDiscListen) {
        this.privacyAmbDiscListen = privacyAmbDiscListen;
    }

    public String getMcsXcapRootUriWifi() {
        return mcsXcapRootUriWifi;
    }

    public void setMcsXcapRootUriWifi(String mcsXcapRootUriWifi) {
        this.mcsXcapRootUriWifi = mcsXcapRootUriWifi;
    }

    public String getKmsUriWifi() {
        return kmsUriWifi;
    }

    public void setKmsUriWifi(String kmsUriWifi) {
        this.kmsUriWifi = kmsUriWifi;
    }

    /**
     * getter method for the max Corp Grps per Large Dispatch
     *
     * @return int
     */
    public int getMaxCorpGrpsPerLargeDispatch() {
        return maxCorpGrpsPerLargeDispatch;
    }

    /**
     * setter method for the max Corp grps per Large Dispatch
     *
     * @param maxCorpGrpsPerLargeDispatch int
     */
    public void setMaxCorpGrpsPerLargeDispatch(int maxCorpGrpsPerLargeDispatch) {
        this.maxCorpGrpsPerLargeDispatch = maxCorpGrpsPerLargeDispatch;
    }

    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer(500);
        strBuffer.append("PTTSERVER ID - ").append(pttServerId)
                .append(", Primary_XDMS_URI - ").append(primaryXDMSUri)
                .append(", Geo_XDMS URI - ").append(geoXDMSUri)
                .append(", XCAP_Root_URI - ").append(XcapRootUri)
                .append(", XcapRootUri_Wifi - ").append(XcapRootUri_Wifi)
                .append(", Max_PublicContacts_Per_Subscr - ").append(maxPublicContactsPerSubs)
                .append(", Max_Public_POC_Grps_Per_Subscr - ").append(maxPublicPOCGrpsPerSubs)
                .append(", Public_POC_Grp_Conf_URI_Template - ").append(publicPocGrpConfURITemplate)
                .append(", Max_Corp_Contacts_Per_Subs - ").append(maxCorpContactsPerSubs)
                .append(", Max_Corp_Grps_Per_Subs - ").append(maxCorpGrpsPerSubs)
                .append(", Corp_POC_Grp_Conf_URI_Template - ").append(corpPocGrpConfURITemplate)
                .append(", Max_Subs_Per_Corporation - ").append(maxSubscrPerCorp)
                .append(", Max_Subs_Lists_per_Corporation - ").append(maxSublistsPerCorp)
                .append(", Max_Members_per_Corp_Subs_List - ").append(maxMembersPerCorpSublist)
                .append(", Max_Ext_Contacts_Per_Corporation - ").append(maxExtContactsPerCorp)
                .append(", Max_POC_Grps_Per_Corporation - ").append(maxPOCGrpsPerCorp)
                .append(", Max_Members_Per_Corp_POC_Grp - ").append(maxMembersPerCorpPOCGrp)
                .append(", MAX_CONTACT_PER_REQUEST - ").append(maxContactPerRequest)
                .append(", SUPERVISOR_ENABLED - ").append(supervisoryEnabled)
                .append(", DISPATCH_ENABLED - ").append(dispatchEnabled)
                .append(", maxDispatchGroup - ").append(maxDispatchGroup)
                .append(", maxMembersPerDispatchGroup - ").append(maxMembersPerDispatchGroup)
                .append(", maxDispatchMembersPerDispatchGroup - ").append(maxDispatchMembersPerDispatchGroup)
                .append(", blockUnsupportedDevices - ").append(blockUnsupportedDevices)
                .append(", enablePocDonorRadioSupport - ").append(enablePocDonorRadioSupport)
                .append(", enableTGS - ").append(enableTGS)
                .append(", maxExtSubsPerCorp - ").append(maxExtSubsPerCorp)
                .append(", maxCampedGroups").append(maxCampedGroups)
                .append(", enableBCGrpFeature").append(enableBCGrpFeature)
                .append(", maxMemPerBCGrp").append(maxMemPerBCGrp)
                .append(", maxPriority").append(maxPriority)
                .append(", blockBlackListDevices").append(blockBlackListDevices)
                .append(", enable3rdPartyPocClientSupport").append(enable3rdPartyPocClientSupport)
                .append(", maxSGMdnsPerGroup").append(maxSGMdnsPerGroup)
                .append(", pttRadioScanListSize").append(pttRadioScanListSize)
                .append(", pttRadioChannelListSize").append(pttRadioChannelListSize)
                .append(", pttRadioDefScanMode").append(pttRadioDefScanMode)
                .append(", webDispatchEnabled").append(webDispatchEnabled)
                .append(", subsDefPttRadio ").append(subsDefPttRadio)
                .append(", interopLicenceType ").append(interopLicenceType)
                .append(", maxSGPatchPerGrp ").append(maxSGPatchPerGrp)
                .append(", iDenInterOp ").append(iDenInterOp)
                .append(", maxSGPatchPerGrp ").append(maxSGPatchPerGrp)
                .append(", emergFeature ").append(emergFeature)
                .append(", ambientListening ").append(ambientListening)
                .append(", discreteListening ").append(discreteListening)
                .append(", userSvcCtrl ").append(userSvcCtrl)
                .append(", maxChannelsPerZone ").append(maxChannelsPerZone)
                .append(", maxRadioChannels ").append(maxRadioChannels)
                .append(", maxZones ").append(maxZones)
                .append(", largeGrpSupport ").append(largeGrpSupport)
                .append(", maxLrgGrpPerCorp ").append(maxLrgGrpPerCorp)
                .append(", maxMemPerLrgGrp ").append(maxMemPerLrgGrp)
                .append(", maxLrgBGrpPerCorp ").append(maxLrgBGrpPerCorp)
                .append(", maxMemPerLrgBGrp ").append(maxMemPerLrgBGrp)
                .append(", multiSimSession ").append(multiSimSession)
                .append(", mcVideoEnabled ").append(mcVideoEnabled)
                .append(", mcVideoUnCfrmPullEnabled ").append(mcVideoUnCfrmPullEnabled)
                .append(", mcsXcapRootUriWifi ").append(mcsXcapRootUriWifi)
                .append(", kmsUriWifi ").append(kmsUriWifi)
                .append(", privacyAmbDiscListen ").append(privacyAmbDiscListen)
                .append(", maxCorpGrpsPerLargeDispatch ").append(maxCorpGrpsPerLargeDispatch);
        return strBuffer.toString();
    }
}
