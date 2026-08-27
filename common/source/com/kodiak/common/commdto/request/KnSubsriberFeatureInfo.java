/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;


public class KnSubsriberFeatureInfo {
	private String tgScanningClientFeaturebit;

	public String getTgScanningClientFeaturebit() {
		return tgScanningClientFeaturebit;
	}

	public void setTgScanningClientFeaturebit(String tgScanningClientFeaturebit) {
		this.tgScanningClientFeaturebit = tgScanningClientFeaturebit;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnSubsriberFeatureInfo [tgScanningClientFeaturebit=").append(tgScanningClientFeaturebit).append("]");
		return builder.toString();
	}
	
}
