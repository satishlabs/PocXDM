/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnEMSInfo.java
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



final class KnEMSInfo {

	private String dsn;
	private String userId;
	private String password;
	private String ipAddress;
	private String dbKey;
	private int port;
	private static KnEMSInfo emsInfo;

	public void init() throws Exception {
		KnDBManagerDAO dbManagerDAO = KnDBManagerDAO.getInstance();
		KnConnectionDetailDTO connectionDetailDTO = dbManagerDAO.getEMSConnDetail();
		this.dsn = connectionDetailDTO.getDsn();
		this.userId = connectionDetailDTO.getUserName();
		this.password = connectionDetailDTO.getPassword();
		this.ipAddress = connectionDetailDTO.getIpAddress();
		this.port = connectionDetailDTO.getPort();
		this.dbKey = this.dsn + this.ipAddress;
	}

	private KnEMSInfo() {

	}

	public synchronized static KnEMSInfo getInstance() {
		if (emsInfo == null) {
			emsInfo = new KnEMSInfo();
		}
		return emsInfo;
	}

	public String getDsn() {
		return this.dsn;
	}

	public String getUserId() {
		return userId;
	}

	public String getPassword() {
		return password;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getDbKey() {
		return dbKey;
	}

	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnEMSInfo [dsn=").append(dsn).append(", userId=").append(userId)
				.append(", ipAddress=").append(ipAddress).append(", dbKey=").append(dbKey).append(", port=").append(port).append("]");
		return builder.toString();
	}

}