/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnDBConnectionDetails.java
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

import java.io.Serializable;

import com.mchange.v2.c3p0.ComboPooledDataSource;

class KnDBConnectionDetails implements Serializable {

	private static final long serialVersionUID = 2151718539005941237L;

	private String dbConnectiontype = null;

	private String ipAddress = null;

	private int dbPort;

	private String userId = null;

	private String password = null;

	private String dbConnectionString = null;

	private String pttServerID = null;

	private ComboPooledDataSource comboPooledDataSource = null;

	private int comboPoolHashCode = -1;

	public String getDbConnectiontype() {
		return dbConnectiontype;
	}

	public void setDbConnectiontype(String dbConnectiontype) {
		this.dbConnectiontype = dbConnectiontype;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getDbPort() {
		return dbPort;
	}

	public void setDbPort(int dbPort) {
		this.dbPort = dbPort;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDbConnectionString() {
		return dbConnectionString;
	}

	public void setDbConnectionString(String dbConnectionString) {
		this.dbConnectionString = dbConnectionString;
	}

	public String getPttServerID() {
		return pttServerID;
	}

	public void setPttServerID(String pttServerID) {
		this.pttServerID = pttServerID;
	}

	public ComboPooledDataSource getComboPooledDataSource() {
		return comboPooledDataSource;
	}

	public synchronized void setComboPooledDataSource(ComboPooledDataSource pComboPoolDatasrc) {
		comboPoolHashCode = -1;
		if (pComboPoolDatasrc != null) {
			comboPoolHashCode = pComboPoolDatasrc.hashCode();
		}
		this.comboPooledDataSource = pComboPoolDatasrc;
	}

	public int getComboPoolHashCode() {
		return comboPoolHashCode;
	}

	@Override
	public String toString() {
		return "KnDBConnectionDetails{" +
				"dbConnectiontype='" + dbConnectiontype + '\'' +
				", ipAddress='" + ipAddress + '\'' +
				", dbPort=" + dbPort +
				", userId='" + userId + '\'' +
				", password='" + password + '\'' +
				", dbConnectionString='" + dbConnectionString + '\'' +
				", pttServerID='" + pttServerID + '\'' +
				", comboPooledDataSource=" + comboPooledDataSource +
				", comboPoolHashCode=" + comboPoolHashCode +
				'}';
	}
}
