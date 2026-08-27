/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.impl;

import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * Created by asanjiv on 11/2/2016.
 */
public class KnCorpSubscrInfoDTO {

    private String mdn;
    private String clientFS2;
    private String corpAdminFS2;

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

	public String getClientFS2() {
		return clientFS2;
	}

	public void setClientFS2(String clientFS2) {
		this.clientFS2 = clientFS2;
	}

	public String getCorpAdminFS2() {
		return corpAdminFS2;
	}

	public void setCorpAdminFS2(String corpAdminFS2) {
		this.corpAdminFS2 = corpAdminFS2;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(100);
		sb.append(super.toString())
				.append(", mdn - ").append(KnGDPRTemplate.mdn(mdn))
				.append(", clientFS2 - ").append(clientFS2)
				.append(", corpAdminFS2 - ").append(corpAdminFS2);
		return sb.toString();

	}
}
