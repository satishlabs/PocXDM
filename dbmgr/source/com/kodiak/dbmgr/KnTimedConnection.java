/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnTimedConnection.java
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.kodiak.logger.KnLogger;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * This class offers a timeout feature on dbmgr connections. A maximum length of
 * time allowed for a connection can be specified.
 */
public class KnTimedConnection {
	private static final KnLogger knLogger = KnLogger.getLogger(KnTimedConnection.class);

	/**
	 * Attempts to connect to a service at the specified address and port, for a
	 * specified maximum amount of time.
	 * 
	 * @param source
	 * @param delay
	 * @return
	 * @throws java.sql.SQLException
	 * @throws java.io.InterruptedIOException
	 * @throws Exception
	 */
	public Connection getConnection(ComboPooledDataSource source, int delay) throws SQLException {
		return getConnection(source, delay, false);
	}

	/**
	 * 
	 * @param source
	 * @param delay
	 * @param readOnly
	 * @return
	 * @throws java.sql.SQLException
	 * @throws java.io.InterruptedIOException
	 * @throws Exception
	 */
	public Connection getConnection(ComboPooledDataSource source, int delay, boolean readOnly) throws SQLException {
		return source.getConnection();
	}

	/**
	 * Attempts to connect to a service at the specified address and port, for a
	 * specified maximum amount of time.
	 * 
	 * @param url
	 * @param userName
	 * @param pass
	 * @param delay
	 * @return
	 * @throws java.sql.SQLException
	 */
	public static Connection getConnection(String url, String userName, String pass, int delay) throws SQLException {
		knLogger.debug("getConnection(url)", "delay : " + delay);
		Connection connection = DriverManager.getConnection(url, userName, pass);
		knLogger.debug("getConnection(url)", "CONNECTION CREATED!!!");
		return connection;
	}
}
