/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnPreparedStatement.java
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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import com.kodiak.logger.KnLogger;

public class KnPreparedStatement implements PreparedStatement {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPreparedStatement.class);
	protected PreparedStatement mSqlPrepStatement;
	public KnSqlConnection mParentSqlConn = null;

	KnPreparedStatement(PreparedStatement pPrepStatement, KnSqlConnection pSqlConn) {
		mSqlPrepStatement = pPrepStatement;
		mParentSqlConn = pSqlConn;
	}

	public KnPreparedStatement() {
	}

	public void setPrepStatement(PreparedStatement pPrepStatement) {
		this.mSqlPrepStatement = pPrepStatement;
	}

	public PreparedStatement getInner() {
		return mSqlPrepStatement;
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		try {
			return mSqlPrepStatement.getMetaData();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		KnResultSet lResultSet = null;
		try {
			lResultSet = new KnResultSet(mSqlPrepStatement.executeQuery(), mParentSqlConn);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
		return lResultSet;
	}

	@Override
	public int executeUpdate() throws SQLException {
		// KnResultSet lResultSet = null;
		try {
			return mSqlPrepStatement.executeUpdate();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void addBatch() throws SQLException {
		// KnResultSet lResultSet = null;
		try {
			mSqlPrepStatement.addBatch();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setNull(int pParamIndex, int pSqlType, String pTypeName) throws SQLException {

		try {
			mSqlPrepStatement.setNull(pParamIndex, pSqlType, pTypeName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setNull(int pParamIndex, int pSqlType) throws SQLException {
		// KnResultSet lResultSet = null;
		try {
			mSqlPrepStatement.setNull(pParamIndex, pSqlType);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setBigDecimal(int pParamIndex, BigDecimal pDecimal) throws SQLException {

		try {
			mSqlPrepStatement.setBigDecimal(pParamIndex, pDecimal);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setBytes(int pParamIndex, byte[] pValue) throws SQLException {
		try {
			mSqlPrepStatement.setBytes(pParamIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void setTimestamp(int pParamIndex, Timestamp pTimestamp, Calendar pDate) throws SQLException {
		try {
			mSqlPrepStatement.setTimestamp(pParamIndex, pTimestamp, pDate);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setTimestamp(int pParamIndex, Timestamp pTimestamp) throws SQLException {
		try {
			mSqlPrepStatement.setTimestamp(pParamIndex, pTimestamp);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setAsciiStream(int pParamIndex, InputStream pIstream, int pLength) throws SQLException {
		try {
			mSqlPrepStatement.setAsciiStream(pParamIndex, pIstream, pLength);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	@Deprecated
	public void setUnicodeStream(int pParamIndex, InputStream pIstream, int pLength) throws SQLException {
		try {
			mSqlPrepStatement.setUnicodeStream(pParamIndex, pIstream, pLength);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setBinaryStream(int pParamIndex, InputStream pIstream, int pLength) throws SQLException {
		try {
			mSqlPrepStatement.setBinaryStream(pParamIndex, pIstream, pLength);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void clearParameters() throws SQLException {
		try {
			mSqlPrepStatement.clearParameters();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setObject(int pParamIndex, Object pParamValue) throws SQLException {
		try {
			mSqlPrepStatement.setObject(pParamIndex, pParamValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setObject(int pParamIndex, Object pParamValue, int pTargetSqlType, int pScale) throws SQLException {
		try {
			mSqlPrepStatement.setObject(pParamIndex, pParamValue, pTargetSqlType, pScale);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setObject(int pParamIndex, Object pParamValue, int pTargetSqlType) throws SQLException {
		try {
			mSqlPrepStatement.setObject(pParamIndex, pParamValue, pTargetSqlType);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setCharacterStream(int pParamIndex, Reader pReader, int pLength) throws SQLException {
		try {
			mSqlPrepStatement.setCharacterStream(pParamIndex, pReader, pLength);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setRef(int pParamIndex, Ref pRef) throws SQLException {
		try {
			mSqlPrepStatement.setRef(pParamIndex, pRef);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setBlob(int pParamIndex, Blob pSqlObj) throws SQLException {
		try {
			mSqlPrepStatement.setBlob(pParamIndex, pSqlObj);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setClob(int pParamIndex, Clob pSqlObj) throws SQLException {
		try {
			mSqlPrepStatement.setClob(pParamIndex, pSqlObj);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setArray(int pParamIndex, Array pValues) throws SQLException {
		try {
			mSqlPrepStatement.setArray(pParamIndex, pValues);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		try {
			return mSqlPrepStatement.getParameterMetaData();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setBoolean(int pParamIndex, boolean pValue) throws SQLException {
		try {
			mSqlPrepStatement.setBoolean(pParamIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setByte(int pParamIndex, byte pValue) throws SQLException {
		try {
			mSqlPrepStatement.setByte(pParamIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setShort(int pParamIndex, short pValue) throws SQLException {
		try {
			mSqlPrepStatement.setShort(pParamIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setInt(int pParamIndex, int pValue) throws SQLException {
		try {
			mSqlPrepStatement.setInt(pParamIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setLong(int pParamIndex, long pValue) throws SQLException {
		try {
			mSqlPrepStatement.setLong(pParamIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setFloat(int pParamIndex, float pValue) throws SQLException {
		try {
			mSqlPrepStatement.setFloat(pParamIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setDouble(int pParamIndex, double pValue) throws SQLException {
		try {
			mSqlPrepStatement.setDouble(pParamIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setURL(int pParamIndex, URL pValue) throws SQLException {
		try {
			mSqlPrepStatement.setURL(pParamIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setTime(int pParamIndex, Time pValue) throws SQLException {
		try {
			mSqlPrepStatement.setTime(pParamIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setTime(int pParamIndex, Time pValue, Calendar pDate) throws SQLException {
		try {
			mSqlPrepStatement.setTime(pParamIndex, pValue, pDate);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean execute() throws SQLException {
		try {
			return mSqlPrepStatement.execute();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setString(int pParamIndex, String pValue) throws SQLException {
		try {
			mSqlPrepStatement.setString(pParamIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setDate(int pParamIndex, Date pValue, Calendar pDate) throws SQLException {
		try {
			mSqlPrepStatement.setDate(pParamIndex, pValue, pDate);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setDate(int pParamIndex, Date pValue) throws SQLException {
		try {
			mSqlPrepStatement.setDate(pParamIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		try {
			return mSqlPrepStatement.getWarnings();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void clearWarnings() throws SQLException {
		try {
			mSqlPrepStatement.clearWarnings();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setFetchDirection(int pParamIndex) throws SQLException {
		try {
			mSqlPrepStatement.setFetchDirection(pParamIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int getFetchDirection() throws SQLException {
		try {
			return mSqlPrepStatement.getFetchDirection();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setFetchSize(int pParamIndex) throws SQLException {
		try {
			mSqlPrepStatement.setFetchSize(pParamIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int getFetchSize() throws SQLException {
		try {
			return mSqlPrepStatement.getFetchSize();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		try {
			return mSqlPrepStatement.getResultSetHoldability();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public ResultSet executeQuery(String pQuery) throws SQLException {
		KnResultSet lResultSet = null;
		try {
			lResultSet = new KnResultSet(mSqlPrepStatement.executeQuery(pQuery), mParentSqlConn);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
		return lResultSet;
	}

	@Override
	public int executeUpdate(String pQuery, int pValue) throws SQLException {
		try {
			return mSqlPrepStatement.executeUpdate(pQuery, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int executeUpdate(String pQuery, String[] pValue) throws SQLException {
		try {
			return mSqlPrepStatement.executeUpdate(pQuery, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int executeUpdate(String pQuery, int[] pValue) throws SQLException {
		try {
			return mSqlPrepStatement.executeUpdate(pQuery, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int executeUpdate(String pQuery) throws SQLException {
		try {
			return mSqlPrepStatement.executeUpdate(pQuery);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		try {
			return mSqlPrepStatement.getMaxFieldSize();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setMaxFieldSize(int pParamIndex) throws SQLException {
		try {
			mSqlPrepStatement.setMaxFieldSize(pParamIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int getMaxRows() throws SQLException {
		try {
			return mSqlPrepStatement.getMaxRows();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setMaxRows(int pParamIndex) throws SQLException {
		try {
			mSqlPrepStatement.setMaxRows(pParamIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setEscapeProcessing(boolean a) throws SQLException {
		try {
			mSqlPrepStatement.setEscapeProcessing(a);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		try {
			return mSqlPrepStatement.getQueryTimeout();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setQueryTimeout(int pTime) throws SQLException {
		try {
			mSqlPrepStatement.setQueryTimeout(pTime);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setCursorName(String pCursor) throws SQLException {
		try {
			mSqlPrepStatement.setCursorName(pCursor);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		KnResultSet lResultSet = null;
		try {
			lResultSet = new KnResultSet(mSqlPrepStatement.getResultSet(), mParentSqlConn);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
		return lResultSet;
	}

	@Override
	public int getUpdateCount() throws SQLException {
		try {
			return mSqlPrepStatement.getUpdateCount();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		try {
			return mSqlPrepStatement.getMoreResults();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean getMoreResults(int a) throws SQLException {
		try {
			return mSqlPrepStatement.getMoreResults(a);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		try {
			return mSqlPrepStatement.getResultSetConcurrency();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int getResultSetType() throws SQLException {
		try {
			return mSqlPrepStatement.getResultSetType();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void addBatch(String pQuery) throws SQLException {
		try {
			mSqlPrepStatement.addBatch(pQuery);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void clearBatch() throws SQLException {
		try {
			mSqlPrepStatement.clearBatch();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int[] executeBatch() throws SQLException {
		try {
			return mSqlPrepStatement.executeBatch();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		KnResultSet lResultSet = null;
		try {
			lResultSet = new KnResultSet(mSqlPrepStatement.getGeneratedKeys(), mParentSqlConn);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
		return lResultSet;
	}

	@Override
	public void close() throws SQLException {
		try {
			mSqlPrepStatement.close();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean execute(String pQuery, int pValue) throws SQLException {
		try {
			return mSqlPrepStatement.execute(pQuery, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean execute(String pQuery) throws SQLException {
		try {
			return mSqlPrepStatement.execute(pQuery);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean execute(String pQuery, int[] pValue) throws SQLException {
		try {
			return mSqlPrepStatement.execute(pQuery, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean execute(String pQuery, String[] pValue) throws SQLException {
		try {
			return mSqlPrepStatement.execute(pQuery, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		try {
			return mSqlPrepStatement.getConnection();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void cancel() throws SQLException {
		try {
			mSqlPrepStatement.cancel();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean isClosed() throws SQLException {
		try {
			return mSqlPrepStatement.isClosed();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		try {
			mSqlPrepStatement.setPoolable(poolable);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean isPoolable() throws SQLException {
		try {
			return mSqlPrepStatement.isPoolable();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		try {
			mSqlPrepStatement.closeOnCompletion();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		try {
			return mSqlPrepStatement.isPoolable();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		try {
			return mSqlPrepStatement.unwrap(iface);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		try {
			return mSqlPrepStatement.isPoolable();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		try {
			mSqlPrepStatement.setRowId(parameterIndex, x);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setNString(int parameterIndex, String value) throws SQLException {
		try {
			mSqlPrepStatement.setNString(parameterIndex, value);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		try {
			mSqlPrepStatement.setNCharacterStream(parameterIndex, value, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		try {
			mSqlPrepStatement.setNClob(parameterIndex, value);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		try {
			mSqlPrepStatement.setClob(parameterIndex, reader, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		try {
			mSqlPrepStatement.setBlob(parameterIndex, inputStream, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		try {
			mSqlPrepStatement.setNClob(parameterIndex, reader, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		try {
			mSqlPrepStatement.setSQLXML(parameterIndex, xmlObject);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		try {
			mSqlPrepStatement.setAsciiStream(parameterIndex, x, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		try {
			mSqlPrepStatement.setBinaryStream(parameterIndex, x, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		try {
			mSqlPrepStatement.setCharacterStream(parameterIndex, reader, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		try {
			mSqlPrepStatement.setAsciiStream(parameterIndex, x);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		try {
			mSqlPrepStatement.setBinaryStream(parameterIndex, x);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		try {
			mSqlPrepStatement.setCharacterStream(parameterIndex, reader);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		try {
			mSqlPrepStatement.setNCharacterStream(parameterIndex, value);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		try {
			mSqlPrepStatement.setClob(parameterIndex, reader);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		try {
			mSqlPrepStatement.setBlob(parameterIndex, inputStream);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		try {
			mSqlPrepStatement.setNClob(parameterIndex, reader);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}
}
