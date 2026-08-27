/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMLicenseSubsDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Shaik Mahaaboob Basha 11/8/14        7.10
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

public class KnXDMLicenseSubsDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676133L;

    private String mdn;
    private String name;
    private int serviceAuthStatus;
    private int markStatus;
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
    public String getObjectId() {
        return null;
    }
}
