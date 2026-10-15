/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnDBProps.java
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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.kodiak.logger.KnLogger;
import com.kodiak.vault.KnCommonVaultUtil;

import static com.kodiak.dbmgr.KnDBConst.*;

final class KnDBProps {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDBProps.class);
	private static KnDBProps dBProps;
	private String ipAddress;
	private int port;
	private String userID;
	private String password;
	private String dbDsn;
	private int pttDbPort;
	private String activeIPAuditInterval;
	private int tcpPort;
	private String connectionType;
	private int ttcTimeout;
	private int sqlQueryTimeout;
	private int busyIdleAuditInterval;

	public void init() throws Exception {
		String methodName = "init()";
		Properties lDbProps = new Properties();
		String lActiveRelPath = System.getProperty(KnDBConst.ACTIVE_REL_DIR_ENV_KEY);
		try(FileInputStream lFStream = new FileInputStream(lActiveRelPath + File.separator + KnDBConst.DB_PROPS_FILE_NAME);) {
			knLogger.entry(methodName);
			lDbProps.load(lFStream);
			ipAddress = lDbProps.getProperty("IPADDRESS");
			port = Integer.parseInt(lDbProps.getProperty("PORT"));
			userID = KnCommonVaultUtil.getKeyFromVault(T10_USER_NAME_PATH, DBMGR_DBUSERID);
			password = KnCommonVaultUtil.getKeyFromVault(T10_PASSWORD_NAME_PATH, DBMGR_DBPASSWORD);
			dbDsn = lDbProps.getProperty("DBDSN");
			activeIPAuditInterval = lDbProps.getProperty("ACTIVE_IP_AUDIT_INTERVAL");
			pttDbPort = Integer.parseInt(lDbProps.getProperty("PTTDBPORT"));
			tcpPort = Integer.parseInt((lDbProps.getProperty("TCP_PORT")));
			connectionType = lDbProps.getProperty("CONNECTIONTYPE");
			ttcTimeout = Integer.parseInt(lDbProps.getProperty("TTC_TIMEOUT"));
			sqlQueryTimeout = Integer.parseInt(lDbProps.getProperty("SQL_QUERY_TIMEOUT"));
			busyIdleAuditInterval = Integer.parseInt(lDbProps.getProperty("BUSY_IDLE_AUDIT_INTERVAL"));
		} 
		knLogger.exit(methodName);
	}

	private KnDBProps() {

	}

	public synchronized static KnDBProps getInstance() {
		if (dBProps == null) {
			String lMethName = "getInstance()";
			knLogger.debug(lMethName, "Entered");
			dBProps = new KnDBProps();
			knLogger.debug("getInstance()", "Configured Local DB Props::" + dBProps.toString());
		}
		return dBProps;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getPort() {
		return port;
	}

	public String getUserID() {
		return userID;
	}

	public String getPassword() {
		return password;
	}

	public String getDbDsn() {
		return dbDsn;
	}

	public int getPttDbPort() {
		return pttDbPort;
	}

	public String getActiveIPAuditInterval() {
		return activeIPAuditInterval;
	}

	public int getTcpPort() {
		return tcpPort;
	}

	public String getConnectionType() {
		return connectionType;
	}

	public int getTtcTimeout() {
		return ttcTimeout;
	}

	public int getSqlQueryTimeout() {
		return sqlQueryTimeout;
	}

	public int getBusyIdleAuditInterval() {
		return busyIdleAuditInterval;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnDBProps [ipAddress=").append(ipAddress).append(", port=").append(port).append(", userID=").append(userID)
				.append(", dbDsn=").append(dbDsn).append(", pttDbPort=").append(pttDbPort)
				.append(", activeIPAuditInterval=").append(activeIPAuditInterval).append(", tcpPort=").append(tcpPort).append(", connectionType=")
				.append(connectionType).append(", ttcTimeout=").append(ttcTimeout).append(", sqlQueryTimeout=").append(sqlQueryTimeout)
				.append(", busyIdleAuditInterval=").append(busyIdleAuditInterval).append("]");
		return builder.toString();
	}
}
