/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnOPChgAuthStatusRespDTO.java
 * Subsystem:   Prov Library DTO
 * <p>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       1/27/11       7.0
 * <p>
 * <p>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * *******************************************************************************
 */

package com.kodiak.xdms.server.bulkops.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.bulkops.dto.clientdat.KnOPProvDTO;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * DTO object used for Change Service Auth Status response
 * the parameters mdn, pocServerHome, Presence Server Home
 * while sending the de-activation notification
 */
public class KnOPChgAuthStatusRespDTO extends KnOPProvDTO {

    private static final long serialVersionUID = 7526471155622676230L;

    private String mdn;
    private String pocServerHome;
    private String presenceServerHome;
    private Integer subsClientType;
    private Integer dispatchType;
    //This is for getting the activeFS
    private String activeFs2;
    //This is notifying if sync is enabled for document modification.
    private boolean isSyncDisabled;

    private int corpid;
    private int authStatus;
    private String subsFS2;
    private String mcId;
    private String mcPttId;
    private String mcVideoId;
    private String mcDataId;
    private String networkName;

    private Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap;
    private Map<String, String> mcsXcapRootUriMap;
    private List<String> mdnList;
    private List<String> profileMdnList;

    /**
     * getter method for the Mdn
     *
     * @return String
     */
    public String getMdn() {
        return mdn;
    }

    /**
     * setter method for the Mdn
     *
     * @param mdn String
     */
    public void setMdn(String mdn) {
        if (mdn != null) {
            mdn = mdn.trim();
            if (mdn.equals("")) {
                mdn = null;
            }
        }
        this.mdn = mdn;
    }

    /**
     * getter method for the Poc Server Home
     *
     * @return String
     */
    public String getPocServerHome() {
        return pocServerHome;
    }

    /**
     * setter method for the Presence Server Home
     *
     * @param pocServerHome String
     */
    public void setPocServerHome(String pocServerHome) {
        if (pocServerHome != null) {
            pocServerHome = pocServerHome.trim();
            if (pocServerHome.equals("")) {
                pocServerHome = null;
            }
        }
        this.pocServerHome = pocServerHome;
    }

    /**
     * getter method for the Presence Server Home
     *
     * @return String
     */
    public String getPresenceServerHome() {
        return presenceServerHome;
    }

    /**
     * setter method for the Presence Server Home
     *
     * @param presenceServerHome String
     */
    public void setPresenceServerHome(String presenceServerHome) {
        if (presenceServerHome != null) {
            presenceServerHome = presenceServerHome.trim();
            if (presenceServerHome.equals("")) {
                presenceServerHome = null;
            }
        }
        this.presenceServerHome = presenceServerHome;
    }



    public Integer getSubsClientType() {
        return subsClientType;
    }

    public void setSubsClientType(Integer subsClientType) {
        this.subsClientType = subsClientType;
    }

    public Integer getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(Integer dispatchType) {
        this.dispatchType = dispatchType;
    }

    public String getActiveFs2() {
		return activeFs2;
	}

	public void setActiveFs2(String activeFs2) {
		this.activeFs2 = activeFs2;
	}

	public boolean isSyncDisabled() {
        return isSyncDisabled;
    }

    public void setSyncDisabled(boolean isSyncDisabled) {
        this.isSyncDisabled = isSyncDisabled;
    }


    public int getCorpid() {
        return corpid;
    }

    public void setCorpid(int corpid) {
        this.corpid = corpid;
    }

    public int getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(int authStatus) {
        this.authStatus = authStatus;
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

    public String getMcDataId() {
        return mcDataId;
    }

    public void setMcDataId(String mcDataId) {
        this.mcDataId = mcDataId;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
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

    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }
    
    public List<String> getProfileMdnList() {
		return profileMdnList;
	}

	public void setProfileMdnList(List<String> profileMdnList) {
		this.profileMdnList = profileMdnList;
	}

    @Override
    public String toString() {
        return "KnOPChgAuthStatusRespDTO{" +
                "mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
                ", pocServerHome='" + pocServerHome + '\'' +
                ", presenceServerHome='" + presenceServerHome + '\'' +
                ", subsClientType=" + subsClientType +
                ", isSyncDisabled=" + isSyncDisabled +
                ", activeFs2='" + activeFs2 + '\'' +
                ", corpId='" + corpid + '\'' +
                ", authStatus='" + authStatus + '\'' +
                ", mdnList='" + KnGDPRTemplate.mdnList(mdnList) + '\'' +
                ", profileMdnList='" + KnGDPRTemplate.mdnList(profileMdnList) + '\'' +
                ", subsFS2='" + subsFS2 + '\'' +
                ", mcId='" + KnGDPRTemplate.mcId(mcId) + '\'' +
                ", mcDataId='" + KnGDPRTemplate.mcdataId(mcDataId) + '\'' +
                ", mcPttId='" + KnGDPRTemplate.mcpttId(mcPttId) + '\'' +
                ", mcVideoId='" + KnGDPRTemplate.mcvideoId(mcVideoId) + '\'' +
                ", networkName='" + networkName + '\'' +
                '}';
    }
}
