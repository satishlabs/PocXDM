/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnLicenseSubDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     09/10/2014    7.10
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


package com.kodiak.xdms.server.corpmgmt.dto.common;


import com.kodiak.common.resources.KnGDPRTemplate;

public class KnLicenseSubDTO implements Comparable<KnLicenseSubDTO> {

    private String mdn;
    private String name;
    private int serviceAuthStatus;
    private int markStatus;
    private int subsClientType;
    private String tpUser;
    private String tpAccount;
    private int publicSubsType;
    private int corpSubsType;


    public String getTpUser() {
		return tpUser;
	}

	public void setTpUser(String tpUser) {
		this.tpUser = tpUser;
	}

	public String getTpAccount() {
		return tpAccount;
	}

	public void setTpAccount(String tpAccount) {
		this.tpAccount = tpAccount;
	}

	public int getSubsClientType() {
		return subsClientType;
	}

	public void setSubsClientType(int subsClientType) {
		this.subsClientType = subsClientType;
	}

	public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public int getMarkStatus() {
        return markStatus;
    }

    public void setMarkStatus(int markStatus) {
        this.markStatus = markStatus;
    }

    public int getPublicSubsType() {
        return publicSubsType;
    }

    public void setPublicSubsType(int publicSubsType) {
        this.publicSubsType = publicSubsType;
    }

    public int getCorpSubsType() {
        return corpSubsType;
    }

    public void setCorpSubsType(int corpSubsType) {
        this.corpSubsType = corpSubsType;
    }

    @Override
    public String toString() {
       StringBuilder builder = new StringBuilder();
       builder.append("KnLicenseSubDTO[")
       .append("mdn-").append(KnGDPRTemplate.mdn(mdn))
       .append(", name-").append(KnGDPRTemplate.name(name))
       .append(", serviceAuthStatus-").append(serviceAuthStatus)
       .append(", markStatus-").append(markStatus)
       .append(", subsClientType-").append(subsClientType)
       .append(", tpUser-").append(tpUser)
       .append(", tpAccount-").append(tpAccount)
       .append(", publicSubsType-").append(publicSubsType)
       .append(", corpSubsType-").append(corpSubsType)
       .append("]");
       
       return builder.toString();
    }

    @Override
    public int compareTo(KnLicenseSubDTO o) {
        return 0;
    }
}
