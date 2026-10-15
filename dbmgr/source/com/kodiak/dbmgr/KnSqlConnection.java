/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnSqlConnection.java
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

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import com.kodiak.logger.KnLogger;

class KnSqlConnection implements Connection {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSqlConnection.class);
	protected Connection mC3P0Connection;
	private KnDBConnectionDetails mDbConnectionDetailsObj = null;
	private int mParentPoolID = -1;
	private boolean mIsReadOnly = false;

	private static long seqNum = 0;
	private long myId = 0;

	public KnSqlConnection(Connection pSqlConnection, KnDBConnectionDetails pDbConnectionDetailsObj, boolean pIsReadOnly) {
		this.mC3P0Connection = pSqlConnection;
		this.mDbConnectionDetailsObj = pDbConnectionDetailsObj;
		mParentPoolID = pDbConnectionDetailsObj.getComboPoolHashCode();
		mIsReadOnly = pIsReadOnly;
		myId = getId();
	}

	private static synchronized long getId() {
		seqNum++;
		return seqNum;
	}

	public long getConnId() {
		return myId;
	}

	public void setWriteConnection() {
		mIsReadOnly = false;
		try {
			knLogger.debug("setWriteConnection()", "mC3P0Connection mode:" , String.valueOf(mC3P0Connection.getAutoCommit()));
			if (mC3P0Connection.getAutoCommit())
				mC3P0Connection.setAutoCommit(false);
		} catch (Exception ex) {
		}
	}

	public KnSqlConnection() {
	}

	void removeReferences() {
		mC3P0Connection = null;
		mDbConnectionDetailsObj = null;
		mParentPoolID = -1;
	}

	public Connection getC3P0Connection() {
		return mC3P0Connection;
	}

	@Override
	public Statement createStatement(int pResultSetType, int pResultSetConcurrency, int pResultSetHoldability) throws SQLException {
		KnSqlStatement lSqlStmt = null;
		try {
			lSqlStmt = new KnSqlStatement(mC3P0Connection.createStatement(pResultSetType, pResultSetConcurrency, pResultSetHoldability), this);
		} catch (SQLException ex) {
			knLogger.debug("createStatment", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
		return lSqlStmt;
	}

	@Override
	public Statement createStatement(int pResultSetType, int pResultSetConcurrency) throws SQLException {
		KnSqlStatement lSqlStmt = null;
		try {
			lSqlStmt = new KnSqlStatement(mC3P0Connection.createStatement(pResultSetType, pResultSetConcurrency), this);
		} catch (SQLException ex) {
			knLogger.debug("createStatment", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
		return lSqlStmt;
	}

	@Override
	public Statement createStatement() throws SQLException {
		KnSqlStatement lSqlStmt = null;
		try {
			lSqlStmt = new KnSqlStatement(mC3P0Connection.createStatement(), this);
		} catch (SQLException ex) {
			knLogger.debug("createStatement:", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
		return lSqlStmt;
	}

	@Override
	public PreparedStatement prepareStatement(String pSqlString, String pColumnNames[]) throws SQLException {
		KnPreparedStatement lPrepStatement = null;
		try {
			lPrepStatement = new KnPreparedStatement(mC3P0Connection.prepareStatement(pSqlString, pColumnNames), this);
		} catch (SQLException ex) {
			knLogger.debug("prepareStatement:", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
		return lPrepStatement;
	}

	@Override
	public PreparedStatement prepareStatement(String pSqlString) throws SQLException {
		KnPreparedStatement lPrepStatement = null;
		try {
			lPrepStatement = new KnPreparedStatement(mC3P0Connection.prepareStatement(pSqlString), this);
			knLogger.debug("prepareStatement(String)", lPrepStatement.toString());
		} catch (SQLException ex) {
			knLogger.debug("prepareStatement::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
		return lPrepStatement;
	}

	@Override
	public PreparedStatement prepareStatement(String pSqlString, int pResultSetType, int pResultSetConcurrency) throws SQLException {
		KnPreparedStatement lPrepStatement = null;
		try {
			lPrepStatement = new KnPreparedStatement(mC3P0Connection.prepareStatement(pSqlString, pResultSetType, pResultSetConcurrency), this);
			knLogger.debug("prepareStatement(String),int,int", lPrepStatement.toString());
		} catch (SQLException ex) {
			knLogger.debug("prepareStatement:: ", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
		return lPrepStatement;
	}

	@Override
	public PreparedStatement prepareStatement(String pSqlString, int pResultSetType, int pResultSetConcurrency, int pResultSetHoldability)
			throws SQLException {
		KnPreparedStatement lPrepStatement = null;
		try {
			lPrepStatement = new KnPreparedStatement(mC3P0Connection.prepareStatement(pSqlString, pResultSetType, pResultSetConcurrency,
					pResultSetHoldability), this);
			knLogger.debug("prepareStatement(String,int,int,int)", lPrepStatement.toString());
		} catch (SQLException ex) {
			knLogger.debug("prepareStatement::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
		return lPrepStatement;
	}

	@Override
	public PreparedStatement prepareStatement(String pSqlString, int pAutoGeneratedKeys) throws SQLException {
		KnPreparedStatement lPrepStatement = null;
		try {
			lPrepStatement = new KnPreparedStatement(mC3P0Connection.prepareStatement(pSqlString, pAutoGeneratedKeys), this);
			knLogger.debug("prepareStatement(String,int)", lPrepStatement.toString());
		} catch (SQLException ex) {
			knLogger.debug("prepareStatement::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
		return lPrepStatement;
	}

	@Override
	public PreparedStatement prepareStatement(String pSqlString, int pColumnIndexes[]) throws SQLException {
		KnPreparedStatement lPrepStatement = null;
		try {
			lPrepStatement = new KnPreparedStatement(mC3P0Connection.prepareStatement(pSqlString, pColumnIndexes), this);
			knLogger.debug("prepareStatement(String,int[])", lPrepStatement.toString());
		} catch (SQLException ex) {
			knLogger.debug("prepareStatement::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
		return lPrepStatement;
	}

	@Override
	public CallableStatement prepareCall(String pSqlString, int pResultSetType, int pResultSetConcurrency, int pResultSetHoldability)
			throws SQLException {
		try {
			return mC3P0Connection.prepareCall(pSqlString, pResultSetType, pResultSetConcurrency, pResultSetHoldability);
		} catch (SQLException ex) {
			knLogger.debug("prepareCall::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public CallableStatement prepareCall(String pSqlString, int pResultSetType, int pResultSetConcurrency) throws SQLException {
		try {
			return mC3P0Connection.prepareCall(pSqlString, pResultSetType, pResultSetConcurrency);
		} catch (SQLException ex) {
			knLogger.debug("prepareCall::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public CallableStatement prepareCall(String pSqlString) throws SQLException {
		try {
			return mC3P0Connection.prepareCall(pSqlString);
		} catch (SQLException ex) {
			knLogger.debug("prepareCall::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public String nativeSQL(String pSqlString) throws SQLException {
		try {
			return mC3P0Connection.nativeSQL(pSqlString);
		} catch (SQLException ex) {
			knLogger.debug("nativeSQL::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}

	}

	@Override
	public void setAutoCommit(boolean pEnable) throws SQLException {
		try {
			mC3P0Connection.setAutoCommit(pEnable);
		} catch (SQLException ex) {
			knLogger.debug("setAutoCommit::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}

	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		try {
			return mC3P0Connection.getAutoCommit();
		} catch (SQLException ex) {
			knLogger.debug("getAutoCommit::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void commit() throws SQLException {
		try {
			if (!mIsReadOnly) {
				knLogger.debug("commit()", "mC3P0Connection:" , mC3P0Connection.hashCode() , " local hashcode:" , this.hashCode() , " myId:" , myId);
				mC3P0Connection.commit();
			}
		} catch (SQLException ex) {
			knLogger.debug("commit::", ex.getMessage() , " myId:" , myId);
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void rollback(Savepoint pSavepoint) throws SQLException {
		try {
			mC3P0Connection.rollback(pSavepoint);
		} catch (SQLException ex) {
			knLogger.debug("rollback::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void rollback() throws SQLException {
		try {
			knLogger.debug("rollback()", "mC3P0Connection:" , mC3P0Connection.hashCode() , " local hashcode:" , this.hashCode() , " myId:" , myId);
			mC3P0Connection.rollback();
		} catch (SQLException ex) {
			knLogger.debug("rollback::", ex.getMessage() + " myId:" ,myId);
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		try {
			return mC3P0Connection.getMetaData();
		} catch (SQLException ex) {
			knLogger.debug("getMetaData::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void setCatalog(String pCatalog) throws SQLException {
		try {
			mC3P0Connection.setCatalog(pCatalog);
		} catch (SQLException ex) {
			knLogger.debug("setCatalog::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public String getCatalog() throws SQLException {
		try {
			return mC3P0Connection.getCatalog();
		} catch (SQLException ex) {
			knLogger.debug("getCatalog:", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void setTransactionIsolation(int pLevel) throws SQLException {
		try {
			mC3P0Connection.setTransactionIsolation(pLevel);
		} catch (SQLException ex) {
			knLogger.debug("setTransactionIsolation:", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		try {
			return mC3P0Connection.getTransactionIsolation();
		} catch (SQLException ex) {
			knLogger.debug("getTransactionIsolation::", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		try {
			return mC3P0Connection.getWarnings();
		} catch (SQLException ex) {
			knLogger.debug("getWarnings:", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void clearWarnings() throws SQLException {
		try {
			mC3P0Connection.clearWarnings();
		} catch (SQLException ex) {
			knLogger.debug("clearWarnings", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {

		try {
			return mC3P0Connection.getTypeMap();
		} catch (SQLException ex) {
			knLogger.debug("getTypeMap", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}

	}

	@Override
	public void setTypeMap(Map<String, Class<?>> pMap) throws SQLException {
		try {
			mC3P0Connection.setTypeMap(pMap);
		} catch (SQLException ex) {
			knLogger.debug("setTypeMap", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void setHoldability(int pHoldability) throws SQLException {
		try {
			mC3P0Connection.setHoldability(pHoldability);
		} catch (SQLException ex) {
			knLogger.debug("setHoldability", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public int getHoldability() throws SQLException {
		try {
			return mC3P0Connection.getHoldability();
		} catch (SQLException ex) {
			knLogger.debug("getHoldability", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {

		try {
			return mC3P0Connection.setSavepoint();
		} catch (SQLException ex) {
			knLogger.debug("setSavepoint", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public Savepoint setSavepoint(String pSavePoint) throws SQLException {
		try {
			return mC3P0Connection.setSavepoint(pSavePoint);
		} catch (SQLException ex) {
			knLogger.debug("setSavepoint", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void releaseSavepoint(Savepoint pSavePoint) throws SQLException {
		try {
			mC3P0Connection.releaseSavepoint(pSavePoint);
		} catch (SQLException ex) {
			knLogger.debug("releaseSavepoint", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void setReadOnly(boolean pEnable) throws SQLException {

		try {
			mC3P0Connection.setReadOnly(pEnable);
		} catch (SQLException ex) {
			knLogger.debug("setReadOnly", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public boolean isReadOnly() throws SQLException {

		try {
			return mC3P0Connection.isReadOnly();
		} catch (SQLException ex) {
			knLogger.debug("isReadOnly", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}

	}

	@Override
	public void close() throws SQLException {
		if (mC3P0Connection != null) {
			try {
				if (!mIsReadOnly) {
					knLogger.debug("closeConnection()", "Connection rollback calling forcefully!! myId:" , myId);
					mC3P0Connection.rollback();
					knLogger.debug("closeConnection()", "Connection rollback calling forcefully success!! myId:" , myId);
				}
			} catch (Exception ex) {

			}
			mC3P0Connection.close();
			mC3P0Connection = null;
		} else {
			knLogger.debug("closeConnection()", "Connection Already closed!! myId:" , myId);
		}
	}

	void handleFailure(SQLException pEx) {

		String lErrorMsg = pEx.getMessage();
		knLogger.warn("handleFailure()",
				"Entered::" , mDbConnectionDetailsObj.getPttServerID() , "" , "and IPaddress::" , mDbConnectionDetailsObj.getIpAddress() , " myId:", myId);

		knLogger.warn("handleFailure()", "Error Code <" , pEx.getErrorCode() , "> SQL State <" , pEx.getSQLState() , ">");

		knLogger.warn("handleFailure()", "Error Message <" , lErrorMsg , "> ");

		knLogger.debug("handleFailure()", "Current HashCode::" , mDbConnectionDetailsObj.getComboPoolHashCode());
		knLogger.debug("handleFailure()", "Parent HashCode ::" , mParentPoolID);
		if (pEx.getSQLState() == null)
			return;
		if ((pEx.getSQLState().compareToIgnoreCase("08S01") == 0 || lErrorMsg.indexOf("TT0994") != -1 || (lErrorMsg.indexOf("TT8025") != -1)
				|| (lErrorMsg.indexOf("TT0846") != -1) || (lErrorMsg.indexOf("TT0946") != -1))) {
			synchronized (mDbConnectionDetailsObj) {
				if ((mDbConnectionDetailsObj != null) && (mParentPoolID == mDbConnectionDetailsObj.getComboPoolHashCode())) {
					KnDBManager.getDBManagerInstance().postPoolDestroyPoolEvent(mDbConnectionDetailsObj);
					mDbConnectionDetailsObj.setComboPooledDataSource(null);
				}
			}
		}

	}

	@Override
	public boolean isClosed() throws SQLException {
		if (mC3P0Connection != null) {
			return mC3P0Connection.isClosed();
		} else
			return true;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		try {
			return mC3P0Connection.unwrap(iface);
		} catch (SQLException ex) {
			knLogger.debug("unwrap", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		try {
			return mC3P0Connection.isWrapperFor(iface);
		} catch (SQLException ex) {
			knLogger.debug("isWrapperFor", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public Clob createClob() throws SQLException {
		try {
			return mC3P0Connection.createClob();
		} catch (SQLException ex) {
			knLogger.debug("createClob", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public Blob createBlob() throws SQLException {
		try {
			return mC3P0Connection.createBlob();
		} catch (SQLException ex) {
			knLogger.debug("createBlob", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public NClob createNClob() throws SQLException {
		try {
			return mC3P0Connection.createNClob();
		} catch (SQLException ex) {
			knLogger.debug("createNClob", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		try {
			return mC3P0Connection.createSQLXML();
		} catch (SQLException ex) {
			knLogger.debug("createSQLXML", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		try {
			return mC3P0Connection.isValid(timeout);
		} catch (SQLException ex) {
			knLogger.debug("isValid", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		try {
			mC3P0Connection.setClientInfo(name, value);
		} catch (SQLException ex) {
			knLogger.debug("setClientInfo", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		try {
			mC3P0Connection.setClientInfo(properties);
		} catch (SQLException ex) {
			knLogger.debug("setClientInfo", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		try {
			return mC3P0Connection.getClientInfo(name);
		} catch (SQLException ex) {
			knLogger.debug("getClientInfo", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		try {
			return mC3P0Connection.getClientInfo();
		} catch (SQLException ex) {
			knLogger.debug("getClientInfo", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		try {
			return mC3P0Connection.createArrayOf(typeName, elements);
		} catch (SQLException ex) {
			knLogger.debug("createArrayOf", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		try {
			return mC3P0Connection.createStruct(typeName, attributes);
		} catch (SQLException ex) {
			knLogger.debug("createStruct", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		try {
			mC3P0Connection.setSchema(schema);
		} catch (SQLException ex) {
			knLogger.debug("setSchema", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}

	}

	@Override
	public String getSchema() throws SQLException {
		try {
			return mC3P0Connection.getSchema();
		} catch (SQLException ex) {
			knLogger.debug("getSchema", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		try {
			mC3P0Connection.abort(executor);
		} catch (SQLException ex) {
			knLogger.debug("abort", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		try {
			mC3P0Connection.setNetworkTimeout(executor, milliseconds);
		} catch (SQLException ex) {
			knLogger.debug("setNetworkTimeout", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		try {
			return mC3P0Connection.getNetworkTimeout();
		} catch (SQLException ex) {
			knLogger.debug("getNetworkTimeout", ex.getMessage());
			handleFailure(ex);
			throw ex;
		}
	}

}
