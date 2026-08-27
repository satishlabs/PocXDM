/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.common;

import com.kodiak.common.dto.IIdentifier;

public class KnRoamingClusterInfoDTO implements IIdentifier {

	private int clusterID;
	private String description;
	private String clusterName;
	private String countryCode;
	private int defaultCluster;
	
	public int getClusterID() {
		return clusterID;
	}

	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public int getDefaultCluster() {
		return defaultCluster;
	}

	public void setDefaultCluster(int defaultCluster) {
		this.defaultCluster = defaultCluster;
	}
	
	@Override
	public String getObjectId() {
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnRoamingClusterInfoDTO[")
		.append("clusterID-").append(clusterID)
		.append(", description-").append(description)
		.append(", clusterName-").append(clusterName)
		.append(", countryCode-").append(countryCode)
		.append(", defaultCluster-").append(defaultCluster);
		
		return builder.toString();
	}
}
