/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnDBEvent.java
 * Subsystem:
 * <p/>
 * Name                 Date          Release
 * -----------------    -----------   -------
 * WebApps              29/04/14      7.9
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
 * *************************************************************************
 */
package com.kodiak.dbmgr;

import com.mchange.v2.c3p0.ComboPooledDataSource;

class KnDBEvent {
	public int mEventName = 0;
	public KnDBConnectionDetails mDBConnDetailObj = null;
	public String mIPAddress = null;
	public String mDBID = null;
	public ComboPooledDataSource mComboPoolDataSrc = null;

	@Override
	public String toString() {
		return "KnDBEvent [mEventName=" + mEventName + ", mDBConnDetailObj=" + mDBConnDetailObj + ", mIPAddress=" + mIPAddress + ", mDBID=" + mDBID
				+ ", mComboPoolDataSrc=" + mComboPoolDataSrc + "]";
	}
}