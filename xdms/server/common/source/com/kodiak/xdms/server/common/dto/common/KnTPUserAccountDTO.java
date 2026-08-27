/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnTPUserAccountDTO implements IIdentifier{

	private String mdn;
	private String tpUser;
	private String tpAccount;
	private int tpAccId;

	public int getTpAccId() {
		return tpAccId;
	}

	public void setTpAccId(int tpAccId) {
		this.tpAccId = tpAccId;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		if (mdn != null) {
			mdn = mdn.trim();
			if (mdn.equals("")) {
				mdn = null;
			}
		}
		this.mdn = mdn;
	}

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

	@Override
	public String getObjectId() {
		return mdn;
	}

	@Override
    public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnTPUserAccountDTO [ ")
		.append("mdn-").append(KnGDPRTemplate.mdn(mdn))
		.append(", tpUser-").append(tpUser)
		.append(", tpAccount-").append(tpAccount)
		.append(" ]");

		return builder.toString();
	}
}
