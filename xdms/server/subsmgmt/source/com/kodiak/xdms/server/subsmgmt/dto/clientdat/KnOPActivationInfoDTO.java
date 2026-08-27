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
 * Ravi Shanker .P       12/29/10       7.0
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
import java.util.Map;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;

public class KnOPActivationInfoDTO extends KnOPProvDTO {

    private static final long serialVersionUID = 7526471155622676229L;

    //stores the XCAP Root URI
    private String xcapRootUri;


    //stores the XCAP Root URI WIFI
    private String xcapRootUriWifi;
    //stores the XUI
    private String xui;

    //stores the mdn or Subscriber Number
    private String mdn;

    //stores the Active Feature Set
    private long activeFS1;
    private boolean cleanUpTGSData;

    private String apnName;
    private long oldActiveFS1;

    private int expiryTime;
    private String email;
    private String password;
    private String subscriberName;

    private String sgwRootUriCellular;
    private String sgwRootUriWifi;
    private String authUriCellular;
    private String authUriWifi;
    private String cbBukInfo;
    private String sgwAuthMec;
    private String ipvc;
    private String ipvcPrefC;
    private String ipvcPrefW;
    private String ipvcMulti;
    private String ipvcPrefMulti;
    private String sgwLocUriCellular;
    private String token;
    private String sgwLocUriWifi;
    private int corpId;
    private String pvVersion;
    private Integer clientType;
    private String activeFS2;
    private String oldActiveFS2;
    private String subscriberFS2;
    private Integer isOptInNeeded;
    private boolean performPrivacyOperation;
    private Integer privacyValueToExecute;
    //pv 18
    private String mcsXcapRootUri;
    private String mcsXcapRootUriWifi;
    private String kmsUri;
    private String kmsUriWifi;
    private String mcsXui;
    private Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap;
    private Map<String, String> mcsXcapRootUriMap;
    private String MCXDOMAINID;
    private Map<String, String> profileMdnActivsFsMap;
    private String subsFS2;
    private String mcId;
    private String mcDataId;
    private String mcPttId;
    private String mcVideoId;
    private String networkName;

    //Just for getting oldFS of ProfileMDN
    private Map<String, KnOPSubsProfileInfoDTO> mdnUpmFsMap;

    public void setMdnUpmFsMap(Map<String, KnOPSubsProfileInfoDTO> mdnUpmFsMap) {
        this.mdnUpmFsMap = mdnUpmFsMap;
    }

    public Map<String, KnOPSubsProfileInfoDTO> getMdnUpmFsMap() {
        return mdnUpmFsMap;
    }
	public String getMCXDOMAINID() {
		return MCXDOMAINID;
	}

	public void setMCXDOMAINID(String mCXDOMAINID) {
		MCXDOMAINID = mCXDOMAINID;
	}

	public String getGMSDOMAINID() {
		return GMSDOMAINID;
	}

	public void setGMSDOMAINID(String gMSDOMAINID) {
		GMSDOMAINID = gMSDOMAINID;
	}

	private String GMSDOMAINID;
    


	public boolean isPerformPrivacyOperation() {
        return performPrivacyOperation;
    }

    public void setPerformPrivacyOperation(boolean performPrivacyOperation) {
        this.performPrivacyOperation = performPrivacyOperation;
    }

    public Integer getPrivacyValueToExecute() {
        return privacyValueToExecute;
    }

    public void setPrivacyValueToExecute(Integer privacyValueToExecute) {
        this.privacyValueToExecute = privacyValueToExecute;
    }

    public Integer getIsOptInNeeded() {
        return isOptInNeeded;
    }

    public void setIsOptInNeeded(Integer isOptInNeeded) {
        this.isOptInNeeded = isOptInNeeded;
    }

    public String getPvVersion() {
        return pvVersion;
    }

    public void setPvVersion(String pvVersion) {
        this.pvVersion = pvVersion;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    /**
     * getter method for the MDN
     *
     * @return String
     */
    public String getMdn() {
        return mdn;
    }

    /**
     * setter method for the MDN
     *
     * @param mdn String
     */
    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    /**
     * getter method for the XCAP Root URI
     *
     * @return String
     */
    public String getXcapRootUri() {
        return xcapRootUri;
    }

    /**
     * setter method for the XCAP Root URI
     *
     * @param xcapRootUri String
     */
    public void setXcapRootUri(String xcapRootUri) {
        this.xcapRootUri = xcapRootUri;
    }

    public String getXcapRootUri_Wifi() {
        return xcapRootUriWifi;
    }

    public void setXcapRootUri_Wifi(String xcapRootUriWifi) {
        this.xcapRootUriWifi = xcapRootUriWifi;
    }

    /**
     * getter method for the XUI
     *
     * @return String
     */
    public String getXui() {
        return xui;
    }

    /**
     * setter method for the XUI
     *
     * @param xui String
     */
    public void setXui(String xui) {
        this.xui = xui;
    }

    /**
     * getter method for the Active Feature Set
     *
     * @return long
     */
    public long getActiveFS1() {
        return activeFS1;
    }

    /**
     * setter method for the Active Feature Set
     * @param activeFS1 long
     */
    public void setActiveFS1(long activeFS1) {
        this.activeFS1 = activeFS1;
    }

    public String getApnName() {
        return apnName;
    }

    public void setApnName(String apnName) {
        this.apnName = apnName;
    }

    public long getOldActiveFS1() {
        return oldActiveFS1;
    }

    public void setOldActiveFS1(long oldActiveFS1) {
        this.oldActiveFS1 = oldActiveFS1;
    }

    public boolean isCleanUpTGSData() {
        return cleanUpTGSData;
    }

    public void setCleanUpTGSData(boolean cleanUpTGSData) {
        this.cleanUpTGSData = cleanUpTGSData;
    }

    public int getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(int expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubscriberName() {
        return subscriberName;
    }

    public void setSubscriberName(String subscriberName) {
        this.subscriberName = subscriberName;
    }

    public String getXcapRootUriWifi() {
        return xcapRootUriWifi;
    }

    public void setXcapRootUriWifi(String xcapRootUriWifi) {
        this.xcapRootUriWifi = xcapRootUriWifi;
    }

    public String getSgwRootUriCellular() {
        return sgwRootUriCellular;
    }

    public void setSgwRootUriCellular(String sgwRootUriCellular) {
        this.sgwRootUriCellular = sgwRootUriCellular;
    }

    public String getSgwRootUriWifi() {
        return sgwRootUriWifi;
    }

    public void setSgwRootUriWifi(String sgwRootUriWifi) {
        this.sgwRootUriWifi = sgwRootUriWifi;
    }

    public String getAuthUriCellular() {
        return authUriCellular;
    }

    public void setAuthUriCellular(String authUriCellular) {
        this.authUriCellular = authUriCellular;
    }

    public String getAuthUriWifi() {
        return authUriWifi;
    }

    public void setAuthUriWifi(String authUriWifi) {
        this.authUriWifi = authUriWifi;
    }

    public String getCbBukInfo() {
        return cbBukInfo;
    }

    public void setCbBukInfo(String cbBukInfo) {
        this.cbBukInfo = cbBukInfo;
    }

    public String getSgwAuthMec() {
        return sgwAuthMec;
    }

    public void setSgwAuthMec(String sgwAuthMec) {
        this.sgwAuthMec = sgwAuthMec;
    }

    public String getIpvc() {
        return ipvc;
    }

    public void setIpvc(String ipvc) {
        this.ipvc = ipvc;
    }

    public String getIpvcPrefC() {
        return ipvcPrefC;
    }

    public void setIpvcPrefC(String ipvcPrefC) {
        this.ipvcPrefC = ipvcPrefC;
    }

    public String getIpvcPrefW() {
        return ipvcPrefW;
    }

    public void setIpvcPrefW(String ipvcPrefW) {
        this.ipvcPrefW = ipvcPrefW;
    }

    public String getIpvcMulti() {
        return ipvcMulti;
    }

    public void setIpvcMulti(String ipvcMulti) {
        this.ipvcMulti = ipvcMulti;
    }

    public String getIpvcPrefMulti() {
        return ipvcPrefMulti;
    }

    public void setIpvcPrefMulti(String ipvcPrefMulti) {
        this.ipvcPrefMulti = ipvcPrefMulti;
    }

    public String getSgwLocUriCellular() {
        return sgwLocUriCellular;
    }

    public void setSgwLocUriCellular(String sgwLocUriCellular) {
        this.sgwLocUriCellular = sgwLocUriCellular;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSgwLocUriWifi() {
        return sgwLocUriWifi;
    }

    public void setSgwLocUriWifi(String sgwLocUriWifi) {
        this.sgwLocUriWifi = sgwLocUriWifi;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
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

    public String getMcsXcapRootUri() {
        return mcsXcapRootUri;
    }

    public void setMcsXcapRootUri(String mcsXcapRootUri) {
        this.mcsXcapRootUri = mcsXcapRootUri;
    }

    public String getMcsXcapRootUriWifi() {
        return mcsXcapRootUriWifi;
    }

    public void setMcsXcapRootUriWifi(String mcsXcapRootUriWifi) {
        this.mcsXcapRootUriWifi = mcsXcapRootUriWifi;
    }

    public String getKmsUri() {
        return kmsUri;
    }

    public void setKmsUri(String kmsUri) {
        this.kmsUri = kmsUri;
    }

    public String getKmsUriWifi() {
        return kmsUriWifi;
    }

    public void setKmsUriWifi(String kmsUriWifi) {
        this.kmsUriWifi = kmsUriWifi;
    }

    public String getMcsXui() {
        return mcsXui;
    }

    public void setMcsXui(String mcsXui) {
        this.mcsXui = mcsXui;
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

    public String getSubscriberFS2() {
        return subscriberFS2;
    }

    public void setSubscriberFS2(String subscriberFS2) {
        this.subscriberFS2 = subscriberFS2;
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

    public String getMcPttId() {
        return mcPttId;
    }

    public void setMcPttId(String mcPttId) {
        this.mcPttId = mcPttId;
    }

    public String getMcVideoId() {
        return mcVideoId;
    }

    public void setMcVideoId(String mcVideoId) {
        this.mcVideoId = mcVideoId;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(super.toString());
        strBuffer.append("[KnOPActivationInfoDTO --> ");
        strBuffer.append("MDN - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", XCAP_ROOT_URI - ").append(xcapRootUri)
                .append(", XCAP_ROOT_URI_WIFI - ").append(xcapRootUriWifi)
                .append(", XUI - ").append(xui)
                .append(", ACTIVE_FS - ").append(activeFS1)
                .append(",CLEANUP_TGS").append(cleanUpTGSData)
                .append(",expiryTime").append(expiryTime)
                .append(", clientPassword - ").append(password)
                .append(", email - ").append(KnGDPRTemplate.email(email))
                .append(",subscriberName").append(KnGDPRTemplate.name(subscriberName))
                .append(" sgwRootUriCellular - ").append(sgwRootUriCellular)
                .append(" sgwRootUriWifi - ").append(sgwRootUriWifi)
                .append(" authUriCellular - ").append(authUriCellular)
                .append(" authUriWifi - ").append(authUriWifi)
                .append(" cbBukInfo - ").append(cbBukInfo)
                .append(" sgwAuthMec - ").append(sgwAuthMec)
                .append(" ipvc - ").append(ipvc)
                .append(" ipvcPrefC - ").append(ipvcPrefC)
                .append(" ipvcPrefW - ").append(ipvcPrefW)
                .append(" corpId - ").append(corpId)
                .append(" pvVersion - ").append(pvVersion)
                .append(" sgwLocUriCellular - ").append(sgwLocUriCellular)
                .append(" sgwLocUriWifi - ").append(sgwLocUriWifi)
                .append(" token - ").append(token)
                .append(" clientType - ").append(clientType)
                .append(" activeFS2 - ").append(activeFS2)
                .append(" oldActiveFS2 - ").append(oldActiveFS2)
                .append(" isOptInNeeded - ").append(isOptInNeeded)
                .append(" performPrivacyOperation - ").append(performPrivacyOperation)
                .append(" privacyValueToExecute - ").append(privacyValueToExecute)
                .append(" mcsXcapRootUri - ").append(mcsXcapRootUri)
                .append(" mcsXcapRootUriWifi - ").append(mcsXcapRootUriWifi)
                .append(" kmsUri - ").append(kmsUri)
                .append(" kmsUriWifi - ").append(kmsUriWifi)
                .append(" mcsXui - ").append(mcsXui)
                .append(" profileMdnEtagMap - ").append(profileMdnEtagMap)
                .append(" mcsXcapRootUriMap - ").append(mcsXcapRootUriMap)
                .append(" profileMdnActivsFsMap - ").append(profileMdnActivsFsMap)
                .append(" subscriberFS2 - ").append(subscriberFS2)
                .append(" mdnUpmFsMap - ").append(mdnUpmFsMap)
                .append(" subsFS2 - ").append(mdnUpmFsMap)
                .append(" mcId - ").append(KnGDPRTemplate.mdn(mcId))
                .append(" mcDataId - ").append(KnGDPRTemplate.mdn(mcDataId))
                .append(" mcPttId - ").append(KnGDPRTemplate.mdn(mcPttId))
                .append(" mcVideoId - ").append(KnGDPRTemplate.mdn(mcVideoId))
                .append(" networkName - ").append(networkName)
                .append("]");
        return strBuffer.toString();

    }
}
