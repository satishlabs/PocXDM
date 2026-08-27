/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * Created by Deepak on 2/6/14.
 */
public class KnOPSubsDispatcherDTO extends KnOPProvDTO {
    private String mdn;
    //stores the XDMS Home
    private String XDMSHome;
    //stores the PoCHome of Subscriber
    private String poCHome;
    //stores the Presence Home of Subscriber
    private String presenceHome;
    // stores the last profile update time
    private long lastProfileUpdateTime;
    //stores the dispatch Group Member Value
    private Integer dispatchGroupMember;
    //stores the corpid
    private int corpId;
    //stores the public Subscription Type
    private int publicSubscriptionType;
    //stores the corporate Subscription Type
    private int corporateSubscriptionType;
    private int clientPVmajorVer;
    private int clientPVminorVer;
    private int subsClientType;
    private int  qppPkgId;
    private String  firstNetIndicator;
    private String subsFS2;
    private String clientFS2;
    private String opsFS2;
    private String corpAdminFS2;
    private String activeFS2;
    private String xdmsFS2;
    private String userProfileFS2;

    //Only to get the oldactiveFs in notifies
    private String oldActiveFS;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
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

    public Integer getDispatchGroupMember() {
        return dispatchGroupMember;
    }

    public void setDispatchGroupMember(Integer dispatchGroupMember) {
        this.dispatchGroupMember = dispatchGroupMember;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public int getPublicSubscriptionType() {
        return publicSubscriptionType;
    }

    public void setPublicSubscriptionType(int publicSubscriptionType) {
        this.publicSubscriptionType = publicSubscriptionType;
    }

    public int getCorporateSubscriptionType() {
        return corporateSubscriptionType;
    }

    public void setCorporateSubscriptionType(int corporateSubscriptionType) {
        this.corporateSubscriptionType = corporateSubscriptionType;
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

    public int getSubsClientType() {
		return subsClientType;
	}

	public void setSubsClientType(int subsClientType) {
		this.subsClientType = subsClientType;
	}

	public int getQppPkgId() {
		return qppPkgId;
	}

	public void setQppPkgId(int qppPkgId) {
		this.qppPkgId = qppPkgId;
	}

	public String getFirstNetIndicator() {
		return firstNetIndicator;
	}

	public void setFirstNetIndicator(String firstNetIndicator) {
		this.firstNetIndicator = firstNetIndicator;
	}

	public String getSubsFS2() {
		return subsFS2;
	}

	public void setSubsFS2(String subsFS2) {
		this.subsFS2 = subsFS2;
	}

	public String getClientFS2() {
		return clientFS2;
	}

	public void setClientFS2(String clientFS2) {
		this.clientFS2 = clientFS2;
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

	public String getActiveFS2() {
		return activeFS2;
	}

	public void setActiveFS2(String activeFS2) {
		this.activeFS2 = activeFS2;
	}

	public String getXdmsFS2() {
		return xdmsFS2;
	}

	public void setXdmsFS2(String xdmsFS2) {
		this.xdmsFS2 = xdmsFS2;
	}

	public String getUserProfileFS2() {
		return userProfileFS2;
	}

	public void setUserProfileFS2(String userProfileFS2) {
		this.userProfileFS2 = userProfileFS2;
	}

    public String getOldActiveFS() {
        return oldActiveFS;
    }

    public void setOldActiveFS(String oldActiveFS) {
        this.oldActiveFS = oldActiveFS;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KnOPSubsDispatcherDTO{");
        sb.append("mdn='").append(KnGDPRTemplate.mdn(mdn)).append('\'');
        sb.append(", XDMSHome='").append(XDMSHome).append('\'');
        sb.append(", poCHome='").append(poCHome).append('\'');
        sb.append(", presenceHome='").append(presenceHome).append('\'');
        sb.append(", lastProfileUpdateTime=").append(lastProfileUpdateTime);
        sb.append(", dispatchGroupMember=").append(dispatchGroupMember);
        sb.append(", corpId=").append(corpId);
        sb.append(", publicSubscriptionType=").append(publicSubscriptionType);
        sb.append(", corporateSubscriptionType=").append(corporateSubscriptionType);
        sb.append(", subsFS2=").append(subsFS2);
        sb.append(", clientFS2=").append(clientFS2);
        sb.append(", opsFS2=").append(opsFS2);
        sb.append(", corpAdminFS2=").append(corpAdminFS2);
        sb.append(", activeFS2=").append(activeFS2);
        sb.append(", clientPVmajorVer=").append(clientPVmajorVer);
        sb.append(", clientPVminorVer=").append(clientPVminorVer);
        sb.append(", subsClientType=").append(subsClientType);
        sb.append(", xdmsFS2=").append(xdmsFS2);
        sb.append(", qppPkgId=").append(qppPkgId);
        sb.append(", firstNetIndicator=").append(firstNetIndicator);
        sb.append(", userProfileFS2=").append(userProfileFS2);
        sb.append(", oldActiveFS=").append(oldActiveFS);
        sb.append('}');
        return sb.toString();
    }
}