/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnRelationalAPI.java
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
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.kodiak.logger.KnLogger;

/**
 * This class implements connection related API for databse. It is used to
 * getconnection and excute Queries on them.
 */
public class KnRelationalAPI {
	private static final KnLogger knLogger = KnLogger.getLogger(KnRelationalAPI.class);
	private KnSqlConnection conn = null;
	private int mHashCode = -1;

	/**
	 * Creats instance of KnRelationalAPI with specific to gievn PTT Server ID.
	 * Instantiates the private variables.
	 * 
	 * @param pttServerID
	 *            - ID of PTT Server.
	 * @throws "No PTT Server available." - when no connection pool is available
	 *         "Server is busy. Please try later." - when all connections are in
	 *         use. "Connection is not available" - connection could not be
	 *         obtained in 15 secs
	 */
	public KnRelationalAPI(String pttServerID) throws SQLException, Exception {
		this(pttServerID, false);
		mHashCode = this.hashCode();
	}

	public KnRelationalAPI(String pttServerID, KnDBConst.DataStores dsnNumber) throws SQLException, Exception {
		this(pttServerID, dsnNumber, false);
		mHashCode = this.hashCode();

	}

	public KnRelationalAPI(String pttServerID, boolean readOnly) throws SQLException, Exception {
		mHashCode = this.hashCode();
		conn = KnDBManager.getDBManagerInstance().getConnection(pttServerID, readOnly);
		if (conn == null) {
			knLogger.info("KnRelationalAPI(String pttServerID,boolean readOnly)", "Connection not available for PTT server ID : " + pttServerID);
			throw new SQLException(pttServerID + ":No PTT Server available.");
		}
		knLogger.info("KnRelationalAPI", "@@@Con Taken@@@ :Hashcode::<" + mHashCode + ">&PTTID::" + pttServerID);
	}

	public KnRelationalAPI(String pttServerID, KnDBConst.DataStores dsnNumber, boolean readOnly) throws SQLException, Exception {
		String dsnName = pttServerID + "_" + dsnNumber.getValue();
		conn = KnDBManager.getDBManagerInstance().getConnection(dsnName, readOnly);
		if (conn == null) {
			knLogger.info("DUAL DSN KnRelationalAPI(String pttServerID,boolean readOnly)", "Connection not available for dual DSN :Hashcode::<"
					+ mHashCode + ">&PTTID::" + dsnName);
			throw new SQLException(pttServerID + ":No PTT Server available.");
		}
		knLogger.info("KnRelationalAPI", "@@@Con Taken for DUAL DSN @@@ : " + dsnName);
	}

	/**
	 * Returns the connection Object of this class.
	 * 
	 * @return Connection - Connection to be used for ececution.
	 */
	public Connection getConnection() {
		return conn;
	}

	/**
	 * Gets DatabaseMetaData of the connection.
	 * 
	 * @return DatabaseMetaData
	 * @throws java.sql.SQLException
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		return conn.getMetaData();
	}

	/**
	 * Gets DatabaseMetaData of the given connection.
	 * 
	 * @param connection
	 * @return DatabaseMetaData
	 * @throws java.sql.SQLException
	 */
	public DatabaseMetaData getMetaData(Connection connection) throws SQLException {
		return connection.getMetaData();
	}

	/**
	 * Closes the connection with te databse for specified Relational class.
	 * 
	 * @param relationAPI
	 *            - KnRelationalAPI to be disconnect from Databse.
	 */
	public static void disconnect(KnRelationalAPI relationAPI) {
		try {
			if (relationAPI != null) {
				boolean lClosed = relationAPI.conn.isClosed();
				knLogger.info("disconnect(KnRelationalAPI relationAPI)", "relationAPI.conn.isClosed() : :Hashcode::<" + relationAPI.mHashCode
						+ ">&PTTID::" + lClosed);
				if (!lClosed) {
					relationAPI.conn.close();
					knLogger.info("disconnect(KnRelationalAPI relationAPI)", "******************CLOSED******************************* : ");
				}
			}
		} catch (Exception e) {
			knLogger.error("disconnect(KnRelationalAPI relationAPI)", "Exception Closing connection.", e);
		}
	}

	/**
	 * Excute the databse Query of the given PreparedStatement and return the
	 * Resultset.
	 * 
	 * @param preparedStatement
	 * @return
	 */
	public ResultSet executeQuery(PreparedStatement preparedStatement) {
		try {
			ResultSet set = preparedStatement.executeQuery();
			return set;
		} catch (Exception e) {
			knLogger.error("executeQuery(PreparedStatement preparedStatement)", "Exception while executing Query.", e);
			return null;
		}
	}

	/**
	 * Excutes the updtae query on given prepare statement.
	 * 
	 * @param preparedStatement
	 * @return
	 */
	public int executeUpdate(PreparedStatement preparedStatement) {
		try {
			int set = preparedStatement.executeUpdate();
			conn.commit();
			return set;
		} catch (Exception e) {
			knLogger.error("executeUpdate(PreparedStatement preparedStatement)", "Exception while executing Update Query.", e);
			return -1;
		}
	}

	/**
	 * Creates PreparedStatement for given query and retrurns it.
	 * 
	 * @param s
	 * @return
	 */
	public PreparedStatement getPreparedStatement(String s) {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(s);
		} catch (Exception e) {
			knLogger.error("getPreparedStatement(String s)", "Exception while creating Prepared Statement.", e);
			return stmt;
		}
		return stmt;
	}

	/**
	 * Excutes the given query on prepared Statement.
	 * 
	 * @param s
	 */
	public void execute(String s) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.execute(s);
		} catch (Exception e) {
			knLogger.error("execute(String s)", "Exception while executing query.", e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					knLogger.error("execute(String s)", "Exception while closing statement query.", e);
				}
			}
		}
	}

	/**
	 * Connection Reaper Class Used to close the connection if it is not being
	 * used for a 5 mins.
	 */
	class ConnectionReaper extends Thread {

		private KnRelationalAPI relapi;
		private final long delay = 300000;

		ConnectionReaper(KnRelationalAPI relapi) {
			this.relapi = relapi;
		}

		@Override
		public void run() {
			while (true) {
				try {
					sleep(delay);
				} catch (InterruptedException e) {
					knLogger.error("run", "InterruptedException", e);
				}
				KnRelationalAPI.disconnect(relapi);
			}
		}
	}
}
