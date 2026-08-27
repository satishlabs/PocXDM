/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnExternalSubsDetailsDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Gunjan Kumar      April 10, 2014      7.9
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
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnExternalSubsDetailsDTO implements IIdentifier {

	private static final long serialVersionUID = -4203866914241214847L;
	private String mdn;
	private String name;
	private Integer type;
	private String objectId;
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	@Override
	public String getObjectId() {
		// TODO Auto-generated method stub
		return this.objectId;
	}
	@Override
	public String toString() {
		return "KnExternalSubsDetailsDTO [mdn=" + KnGDPRTemplate.mdn(mdn) + ", name=" + KnGDPRTemplate.name(name)
				+ ", type=" + type + ", objectId=" + objectId + "]";
	}
}
