/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnResultSet.java
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
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import com.kodiak.logger.KnLogger;

public class KnResultSet implements ResultSet {
	private static final KnLogger knLogger = KnLogger.getLogger(KnResultSet.class);
	protected ResultSet mResultSet;
	public KnSqlConnection mParentSqlConn = null;

	public KnResultSet(ResultSet pResultSet, KnSqlConnection pParentSqlConn) {
		this.mResultSet = pResultSet;
		mParentSqlConn = pParentSqlConn;
	}

	public KnResultSet() {
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		try {
			return mResultSet.getMetaData();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		try {
			return mResultSet.getWarnings();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public void clearWarnings() throws SQLException {
		try {
			mResultSet.clearWarnings();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean wasNull() throws SQLException {
		try {
			return mResultSet.wasNull();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public BigDecimal getBigDecimal(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getBigDecimal(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(String pColumnName, int pValue) throws SQLException {
		try {
			return mResultSet.getBigDecimal(pColumnName, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(int pColumnIndex, int pValue) throws SQLException {
		try {
			return mResultSet.getBigDecimal(pColumnIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public BigDecimal getBigDecimal(String pColumnName) throws SQLException {
		try {
			return mResultSet.getBigDecimal(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Timestamp getTimestamp(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getTimestamp(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Timestamp getTimestamp(String pColumnName) throws SQLException {
		try {
			return mResultSet.getTimestamp(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Timestamp getTimestamp(int pColumnIndex, Calendar pValue) throws SQLException {
		try {
			return mResultSet.getTimestamp(pColumnIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Timestamp getTimestamp(String pColumnName, Calendar pValue) throws SQLException {
		try {
			return mResultSet.getTimestamp(pColumnName, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public InputStream getAsciiStream(String pColumnName) throws SQLException {
		try {
			return mResultSet.getAsciiStream(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public InputStream getAsciiStream(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getAsciiStream(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	@Deprecated
	public InputStream getUnicodeStream(String pColumnName) throws SQLException {
		try {
			return mResultSet.getUnicodeStream(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	@Deprecated
	public InputStream getUnicodeStream(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getUnicodeStream(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public InputStream getBinaryStream(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getBinaryStream(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public InputStream getBinaryStream(String pColumnName) throws SQLException {
		try {
			return mResultSet.getBinaryStream(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public String getCursorName() throws SQLException {
		try {
			return mResultSet.getCursorName();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Reader getCharacterStream(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getCharacterStream(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Reader getCharacterStream(String pColumnName) throws SQLException {
		try {
			return mResultSet.getCharacterStream(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		try {
			return mResultSet.isBeforeFirst();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public boolean isAfterLast() throws SQLException {
		try {
			return mResultSet.isAfterLast();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public boolean isFirst() throws SQLException {
		try {
			return mResultSet.isFirst();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public boolean isLast() throws SQLException {
		try {
			return mResultSet.isLast();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public void beforeFirst() throws SQLException {
		try {
			mResultSet.beforeFirst();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void afterLast() throws SQLException {
		try {
			mResultSet.afterLast();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean absolute(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.absolute(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public void setFetchDirection(int pColumnIndex) throws SQLException {
		try {
			mResultSet.setFetchDirection(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int getFetchDirection() throws SQLException {
		try {
			return mResultSet.getFetchDirection();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public void setFetchSize(int pColumnIndex) throws SQLException {
		try {
			mResultSet.setFetchSize(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int getFetchSize() throws SQLException {
		try {
			return mResultSet.getFetchSize();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public int getConcurrency() throws SQLException {
		try {
			return mResultSet.getConcurrency();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public boolean rowUpdated() throws SQLException {
		try {
			return mResultSet.rowUpdated();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public boolean rowInserted() throws SQLException {
		try {
			return mResultSet.rowInserted();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public boolean rowDeleted() throws SQLException {
		try {
			return mResultSet.rowDeleted();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public void updateNull(int pColumnIndex) throws SQLException {
		try {
			mResultSet.updateNull(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateNull(String pColumnName) throws SQLException {
		try {
			mResultSet.updateNull(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateBoolean(int pColumnIndex, boolean pBool) throws SQLException {
		try {
			mResultSet.updateBoolean(pColumnIndex, pBool);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateBoolean(String pColumnName, boolean pBool) throws SQLException {
		try {
			mResultSet.updateBoolean(pColumnName, pBool);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateByte(int pColumnIndex, byte pValue) throws SQLException {
		try {
			mResultSet.updateByte(pColumnIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateByte(String pColumnName, byte pValue) throws SQLException {
		try {
			mResultSet.updateByte(pColumnName, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateShort(int pColumnIndex, short pValue) throws SQLException {
		try {
			mResultSet.updateShort(pColumnIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateShort(String pColumnName, short pValue) throws SQLException {
		try {
			mResultSet.updateShort(pColumnName, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateInt(String pColumnName, int pValue) throws SQLException {
		try {
			mResultSet.updateInt(pColumnName, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateInt(int pColumnIndex, int pValue) throws SQLException {
		try {
			mResultSet.updateInt(pColumnIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateLong(int pColumnIndex, long pValue) throws SQLException {
		try {
			mResultSet.updateLong(pColumnIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateLong(String pColumnName, long pValue) throws SQLException {
		try {
			mResultSet.updateLong(pColumnName, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateFloat(String pColumnName, float pValue) throws SQLException {
		try {
			mResultSet.updateFloat(pColumnName, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateFloat(int pColumnIndex, float pValue) throws SQLException {
		try {
			mResultSet.updateFloat(pColumnIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateDouble(String pColumnName, double pValue) throws SQLException {
		try {
			mResultSet.updateDouble(pColumnName, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateDouble(int pColumnIndex, double pValue) throws SQLException {
		try {
			mResultSet.updateDouble(pColumnIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateBigDecimal(int pColumnIndex, BigDecimal pValue) throws SQLException {
		try {
			mResultSet.updateBigDecimal(pColumnIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateBigDecimal(String pColumnName, BigDecimal pValue) throws SQLException {
		try {
			mResultSet.updateBigDecimal(pColumnName, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateString(String pColumnName, String pValue) throws SQLException {
		try {
			mResultSet.updateString(pColumnName, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateString(int pColumnIndex, String pValue) throws SQLException {
		try {
			mResultSet.updateString(pColumnIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateBytes(int pColumnIndex, byte[] pValue) throws SQLException {
		try {
			mResultSet.updateBytes(pColumnIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateBytes(String pColumnName, byte[] pValue) throws SQLException {
		try {
			mResultSet.updateBytes(pColumnName, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateDate(String pColumnName, Date pValue) throws SQLException {
		try {
			mResultSet.updateDate(pColumnName, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateDate(int pColumnIndex, Date pValue) throws SQLException {
		try {
			mResultSet.updateDate(pColumnIndex, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateTimestamp(int pColumnIndex, Timestamp pTime) throws SQLException {
		try {
			mResultSet.updateTimestamp(pColumnIndex, pTime);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateTimestamp(String pColumnName, Timestamp pTime) throws SQLException {
		try {
			mResultSet.updateTimestamp(pColumnName, pTime);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateAsciiStream(String pColumnName, InputStream pIstream, int pValue) throws SQLException {
		try {
			mResultSet.updateAsciiStream(pColumnName, pIstream, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateAsciiStream(int pColumnIndex, InputStream pIStream, int pValue) throws SQLException {
		try {
			mResultSet.updateAsciiStream(pColumnIndex, pIStream, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateBinaryStream(int pColumnIndex, InputStream pIStream, int pValue) throws SQLException {
		try {
			mResultSet.updateBinaryStream(pColumnIndex, pIStream, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateBinaryStream(String pColumnName, InputStream pIStream, int pValue) throws SQLException {
		try {
			mResultSet.updateBinaryStream(pColumnName, pIStream, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateCharacterStream(int pColumnIndex, Reader pReader, int pValue) throws SQLException {
		try {
			mResultSet.updateCharacterStream(pColumnIndex, pReader, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateCharacterStream(String pColumnName, Reader pReader, int pValue) throws SQLException {
		try {
			mResultSet.updateCharacterStream(pColumnName, pReader, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateObject(String pColumnName, Object pObject) throws SQLException {
		try {
			mResultSet.updateObject(pColumnName, pObject);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateObject(int pColumnIndex, Object pObject) throws SQLException {
		try {
			mResultSet.updateObject(pColumnIndex, pObject);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateObject(int pColumnIndex, Object pObject, int pValue) throws SQLException {
		try {
			mResultSet.updateObject(pColumnIndex, pObject, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateObject(String pColumnName, Object pObject, int pValue) throws SQLException {
		try {
			mResultSet.updateObject(pColumnName, pObject, pValue);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void insertRow() throws SQLException {
		try {
			mResultSet.insertRow();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateRow() throws SQLException {
		try {
			mResultSet.updateRow();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void deleteRow() throws SQLException {
		try {
			mResultSet.deleteRow();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void refreshRow() throws SQLException {
		try {
			mResultSet.refreshRow();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		try {
			mResultSet.cancelRowUpdates();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		try {
			mResultSet.moveToInsertRow();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		try {
			mResultSet.moveToCurrentRow();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public Statement getStatement() throws SQLException {
		try {
			return mResultSet.getStatement();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Blob getBlob(String pColumnName) throws SQLException {
		try {
			return mResultSet.getBlob(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Blob getBlob(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getBlob(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Clob getClob(String pColumnName) throws SQLException {
		try {
			return mResultSet.getClob(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Clob getClob(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getClob(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public void updateRef(String pColumnName, Ref pRef) throws SQLException {
		try {
			mResultSet.updateRef(pColumnName, pRef);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateRef(int pColumnIndex, Ref pRef) throws SQLException {
		try {
			mResultSet.updateRef(pColumnIndex, pRef);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateBlob(String pColumnName, Blob pBlob) throws SQLException {
		try {
			mResultSet.updateBlob(pColumnName, pBlob);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateBlob(int pColumnIndex, Blob pBlob) throws SQLException {
		try {
			mResultSet.updateBlob(pColumnIndex, pBlob);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateClob(int pColumnIndex, Clob pClob) throws SQLException {
		try {
			mResultSet.updateClob(pColumnIndex, pClob);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateClob(String pColumnName, Clob pClob) throws SQLException {
		try {
			mResultSet.updateClob(pColumnName, pClob);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateArray(String pColumnName, Array pArray) throws SQLException {
		try {
			mResultSet.updateArray(pColumnName, pArray);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public void updateArray(int pColumnIndex, Array pArray) throws SQLException {
		try {
			mResultSet.updateArray(pColumnIndex, pArray);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public Object getObject(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getObject(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Object getObject(String pColumnName, Map<String, Class<?>> pMap) throws SQLException {
		try {
			return mResultSet.getObject(pColumnName, pMap);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Object getObject(String pColumnName) throws SQLException {
		try {
			return mResultSet.getObject(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Object getObject(int pColumnIndex, Map<String, Class<?>> pMap) throws SQLException {
		try {
			return mResultSet.getObject(pColumnIndex, pMap);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public boolean getBoolean(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getBoolean(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public boolean getBoolean(String pColumnName) throws SQLException {
		try {
			return mResultSet.getBoolean(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public byte getByte(String pColumnName) throws SQLException {
		try {
			return mResultSet.getByte(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public byte getByte(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getByte(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public short getShort(String pColumnName) throws SQLException {
		try {
			return mResultSet.getShort(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public short getShort(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getShort(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public int getInt(String pColumnName) throws SQLException {
		try {
			return mResultSet.getInt(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public int getInt(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getInt(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public long getLong(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getLong(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public long getLong(String pColumnName) throws SQLException {
		try {
			return mResultSet.getLong(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public float getFloat(String pColumnName) throws SQLException {
		try {
			return mResultSet.getFloat(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public float getFloat(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getFloat(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public double getDouble(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getDouble(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public double getDouble(String pColumnName) throws SQLException {
		try {
			return mResultSet.getDouble(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public byte[] getBytes(String pColumnName) throws SQLException {
		try {
			return mResultSet.getBytes(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public byte[] getBytes(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getBytes(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public boolean next() throws SQLException {
		try {
			return mResultSet.next();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public URL getURL(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getURL(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public URL getURL(String pColumnName) throws SQLException {
		try {
			return mResultSet.getURL(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public int getType() throws SQLException {
		try {
			return mResultSet.getType();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public boolean previous() throws SQLException {
		try {
			return mResultSet.previous();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public void close() throws SQLException {
		try {
			mResultSet.close();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public String getString(String pColumnName) throws SQLException {
		try {
			return mResultSet.getString(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public String getString(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getString(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Ref getRef(String pColumnName) throws SQLException {
		try {
			return mResultSet.getRef(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Ref getRef(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getRef(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Time getTime(int pColumnIndex, Calendar b) throws SQLException {
		try {
			return mResultSet.getTime(pColumnIndex, b);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Time getTime(String pColumnName) throws SQLException {
		try {
			return mResultSet.getTime(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Time getTime(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getTime(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Time getTime(String pColumnName, Calendar b) throws SQLException {
		try {
			return mResultSet.getTime(pColumnName, b);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Date getDate(String pColumnName) throws SQLException {
		try {
			return mResultSet.getDate(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Date getDate(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getDate(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Date getDate(int pColumnIndex, Calendar b) throws SQLException {
		try {
			return mResultSet.getDate(pColumnIndex, b);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Date getDate(String pColumnName, Calendar b) throws SQLException {
		try {
			return mResultSet.getDate(pColumnName, b);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public boolean first() throws SQLException {
		try {
			return mResultSet.first();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public boolean last() throws SQLException {
		try {
			return mResultSet.last();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Array getArray(String pColumnName) throws SQLException {
		try {
			return mResultSet.getArray(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public Array getArray(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.getArray(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public boolean relative(int pColumnIndex) throws SQLException {
		try {
			return mResultSet.relative(pColumnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}

	}

	@Override
	public void updateTime(String pColumnName, Time pTimer) throws SQLException {
		try {
			mResultSet.updateTime(pColumnName, pTimer);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateTime(int pColumnIndex, Time pTimer) throws SQLException {
		try {
			mResultSet.updateTime(pColumnIndex, pTimer);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int findColumn(String pColumnName) throws SQLException {
		try {
			return mResultSet.findColumn(pColumnName);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;
		}
	}

	@Override
	public int getRow() throws SQLException {
		try {
			return mResultSet.getRow();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}

	}

	// private void handleFailure() {
	// knLogger.debug( "Failure in ResultSET");
	// }

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		try {
			return mResultSet.unwrap(iface);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		try {
			return mResultSet.isWrapperFor(iface);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		try {
			return mResultSet.getRowId(columnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		try {
			return mResultSet.getRowId(columnLabel);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		try {
			mResultSet.updateRowId(columnIndex, x);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		try {
			mResultSet.updateRowId(columnLabel, x);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public int getHoldability() throws SQLException {
		try {
			return mResultSet.getHoldability();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public boolean isClosed() throws SQLException {
		try {
			return mResultSet.isClosed();
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateNString(int columnIndex, String nString) throws SQLException {
		try {
			mResultSet.updateNString(columnIndex, nString);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateNString(String columnLabel, String nString) throws SQLException {
		try {
			mResultSet.updateNString(columnLabel, nString);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		try {
			mResultSet.updateNClob(columnIndex, nClob);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		try {
			mResultSet.updateNClob(columnLabel, nClob);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		try {
			return mResultSet.getNClob(columnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		try {
			return mResultSet.getNClob(columnLabel);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		try {
			return mResultSet.getSQLXML(columnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		try {
			return mResultSet.getSQLXML(columnLabel);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		try {
			mResultSet.updateSQLXML(columnIndex, xmlObject);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		try {
			mResultSet.updateSQLXML(columnLabel, xmlObject);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		try {
			return mResultSet.getNString(columnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		try {
			return mResultSet.getNString(columnLabel);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		try {
			return mResultSet.getNCharacterStream(columnIndex);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		try {
			return mResultSet.getNCharacterStream(columnLabel);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		try {
			mResultSet.updateNCharacterStream(columnIndex, x, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		try {
			mResultSet.updateNCharacterStream(columnLabel, reader, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		try {
			mResultSet.updateAsciiStream(columnIndex, x, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		try {
			mResultSet.updateBinaryStream(columnIndex, x, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		try {
			mResultSet.updateCharacterStream(columnIndex, x, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		try {
			mResultSet.updateAsciiStream(columnLabel, x, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		try {
			mResultSet.updateBinaryStream(columnLabel, x, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		try {
			mResultSet.updateCharacterStream(columnLabel, reader, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		try {
			mResultSet.updateBlob(columnIndex, inputStream, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		try {
			mResultSet.updateBlob(columnLabel, inputStream, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		try {
			mResultSet.updateClob(columnIndex, reader, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		try {
			mResultSet.updateClob(columnLabel, reader, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		try {
			mResultSet.updateNClob(columnIndex, reader, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		try {
			mResultSet.updateNClob(columnLabel, reader, length);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		try {
			mResultSet.updateNCharacterStream(columnIndex, x);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		try {
			mResultSet.updateNCharacterStream(columnLabel, reader);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		try {
			mResultSet.updateAsciiStream(columnIndex, x);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		try {
			mResultSet.updateBinaryStream(columnIndex, x);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		try {
			mResultSet.updateCharacterStream(columnIndex, x);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		try {
			mResultSet.updateAsciiStream(columnLabel, x);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		try {
			mResultSet.updateBinaryStream(columnLabel, x);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		try {
			mResultSet.updateCharacterStream(columnLabel, reader);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		try {
			mResultSet.updateBlob(columnIndex, inputStream);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		try {
			mResultSet.updateBlob(columnLabel, inputStream);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		try {
			mResultSet.updateClob(columnIndex, reader);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		try {
			mResultSet.updateClob(columnLabel, reader);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		try {
			mResultSet.updateNClob(columnIndex, reader);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		try {
			mResultSet.updateNClob(columnLabel, reader);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		try {
			return mResultSet.getObject(columnIndex, type);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		try {
			return mResultSet.getObject(columnLabel, type);
		} catch (SQLException e) {
			knLogger.debug(new Exception().getStackTrace()[0].getMethodName(), e.getMessage());
			mParentSqlConn.handleFailure(e);
			throw e;

		}
	}
}
