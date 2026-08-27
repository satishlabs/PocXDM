/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnPAMAccPoolUsageDTO implements IIdentifier{

	private static final long serialVersionUID = -8530119106424124096L;
	
	private int pamAccId;
	private String billingMdn;
	private String mdn;
	private int usage;
	
	public int getPamAccId() {
		return pamAccId;
	}

	public void setPamAccId(int pamAccId) {
		this.pamAccId = pamAccId;
	}

	public String getBillingMdn() {
		return billingMdn;
	}

	public void setBillingMdn(String billingMdn) {
		this.billingMdn = billingMdn;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public int getUsage() {
		return usage;
	}

	public void setUsage(int usage) {
		this.usage = usage;
	}

	@Override
	public String getObjectId() {
		return String.valueOf(this.pamAccId);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnPAMAccPoolUsageDTO[ ")
		.append("pamAccId-").append(pamAccId)
		.append(", billingMdn").append(billingMdn)
		.append(", mdn").append(KnGDPRTemplate.mdn(mdn))
		.append(", usage").append(usage)
		.append(" ]");
		
		return builder.toString();
	}

}





