/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnOPDeleteSubsRespDTO.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       11/4/11       7.0
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

import java.util.List;
import java.util.Map;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnOPDeleteSubsRespDTO extends KnOPProvDTO {

    private static final long serialVersionUID = 7526471155622676233L;

    private String mdn;
    private String pocServerHome;
    private String presenceServerHome;
    private long activeFS;
    private int corpId;
    private String password;
    private String activeFS2;
    private List<String> userProfileMdns;
    private Map<String, List<String>> userProfileMdnMap;
    private boolean isCorporateDeleted;

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getActiveFS() {
        return activeFS;
    }

    public void setActiveFS(long activeFS) {
        this.activeFS = activeFS;
    }

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


    public String getActiveFS2() {
		return activeFS2;
	}

	public void setActiveFS2(String activeFS2) {
		this.activeFS2 = activeFS2;
	}

	public List<String> getUserProfileMdns() {
		return userProfileMdns;
	}

	public void setUserProfileMdns(List<String> userProfileMdns) {
		this.userProfileMdns = userProfileMdns;
	}

    public Map<String, List<String>> getUserProfileMdnMap() {
        return userProfileMdnMap;
    }

    public void setUserProfileMdnMap(Map<String, List<String>> userProfileMdnMap) {
        this.userProfileMdnMap = userProfileMdnMap;
    }

    public boolean isCorporateDeleted() {
        return isCorporateDeleted;
    }

    public void setCorporateDeleted(boolean corporateDeleted) {
        isCorporateDeleted = corporateDeleted;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(super.toString());
        strBuffer.append(" [KnOPDeleteSubsRespDTO --> ");
        strBuffer.append(", MDN - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", Poc_Server_Home - ").append(pocServerHome)
                .append(", Presence_Server_Home - ").append(presenceServerHome)
                .append(", ActiveFS- ").append(activeFS)
                .append(", corpId- ").append(corpId)
                .append(", password- ").append(password)
                .append(", activeFS2- ").append(activeFS2)
                .append(", userProfileMdns- ").append(userProfileMdns)
                .append(", userProfileMdnMap- ").append(userProfileMdnMap)
                .append(", isCorporateDeleted- ").append(isCorporateDeleted)

                .append("]");

        return strBuffer.toString();

    }
}
