/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnTransaction.java
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.kodiak.logger.KnLogger;

public class KnTransaction implements UserTransaction {
	private static final KnLogger knLogger = KnLogger.getLogger(KnTransaction.class);
	private Map<String, KnSqlConnection> connectionHash;
	private STATE state = STATE.NEW;
	private Long creationTime;

	private enum STATE {
		//if changing this sequence or adding any more states,
		// please update the same in com.kodiak.common.dao.KnPersisterTxn class STATE enum too
		NEW, STARTED, COMMITED, ROLLBACK, COMMIT_FAIL, ROLLBACK_FAIL
	}

	private final static String INVALID_STATE_MSG = "Transaction is in invalid state, Current State=";

	/**
	 * Begins the current transaction. Intializes the private variables.
	 * 
	 * @throws javax.transaction.NotSupportedException
	 * @throws javax.transaction.SystemException
	 */
	@Override
	public void begin() throws NotSupportedException, SystemException {
		if (!STATE.NEW.equals(state)) {
			knLogger.error(KnTransaction.INVALID_STATE_MSG , state);
			throw new IllegalStateException(KnTransaction.INVALID_STATE_MSG + state);
		}
		connectionHash = new HashMap<String, KnSqlConnection>();
		state = STATE.STARTED;
		creationTime = System.currentTimeMillis();
		knLogger.debug("begin()", "Started Transaction");
	}

	/**
	 * Commits the transaction on the databases. Throws exception if it fails.
	 * 
	 * @throws javax.transaction.RollbackException
	 * @throws javax.transaction.HeuristicMixedException
	 * @throws javax.transaction.HeuristicRollbackException
	 * @throws SecurityException
	 * @throws javax.transaction.SystemException
	 */
	@Override
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, SystemException {
		if (!state.equals(STATE.STARTED)) {
			knLogger.error(KnTransaction.INVALID_STATE_MSG , state);
			throw new IllegalStateException(KnTransaction.INVALID_STATE_MSG + state);
		}
		try {
			for (Map.Entry<String, KnSqlConnection> entry : connectionHash.entrySet()) {
				entry.getValue().commit();
			}
			state = STATE.COMMITED;
		} catch (SQLException e) {
			knLogger.error("commitOrRollBack()", "Exception while commiting. Rolling back as one of the connection is invalid", e);
			rollback();
			state = STATE.COMMIT_FAIL;
			throw new RollbackException("Transaction state=" + state);
		} finally {
			closeAllConnections();
		}
	}

	/**
	 * Rollbacks all the transaction os the connections associated with this
	 * transaction.
	 * 
	 * @throws IllegalStateException
	 * @throws javax.transaction.SystemException
	 */
	@Override
	public void rollback() throws IllegalStateException, SystemException {
		if (!state.equals(STATE.STARTED)) {
			knLogger.error(KnTransaction.INVALID_STATE_MSG , state);
			throw new IllegalStateException(KnTransaction.INVALID_STATE_MSG + state);
		}
		try {
			for (Map.Entry<String, KnSqlConnection> entry : connectionHash.entrySet()) {
				entry.getValue().rollback();
			}
			state = STATE.ROLLBACK;
		} catch (SQLException e) {
			knLogger.error("commitOrRollBack()", " Exception while commiting. Rolling back as one of the connection is invalid", e);
			state = STATE.ROLLBACK_FAIL;
			throw new SystemException("Transaction state=" + state);
		} finally {
			closeAllConnections();
		}
	}

	private void closeAllConnections() {
		knLogger.debug("closeAllConnections() Closing connection after commit");

		for (Map.Entry<String, KnSqlConnection> entry : connectionHash.entrySet()) {
			try {
				entry.getValue().close();
			} catch (SQLException e) {
				knLogger.error("closeAllConnections()", " Exception in Connection Close", e);
			}
		}
		connectionHash.clear();
	}

	/**
	 * Gets the connection from connecion Pool Depending upon the PTT SErver ID.
	 * Throws Exception if the given PTT Server ID does not exist or invalid.
	 * 
	 * @param pttServerID
	 * @return
	 * @throws javax.transaction.SystemException
	 */
	public Connection getConnection(String pttServerID) throws SystemException, Exception {
		return getConnection(pttServerID, false);
	}

	/**
	 * Method to get readonly connection
	 * 
	 * @param pttServerID
	 * @param readOnly
	 * @return
	 * @throws javax.transaction.SystemException
	 * @throws Exception
	 */
	public Connection getConnection(String pttServerID, boolean readOnly) throws SystemException, Exception {
		String methodName = "getConnection(String pttServerID, boolean readOnly)";
		if (!state.equals(STATE.STARTED)) {
			knLogger.error(KnTransaction.INVALID_STATE_MSG , state);
			throw new IllegalStateException(KnTransaction.INVALID_STATE_MSG + state);
		}
		KnSqlConnection conn = null;
		if (connectionHash.containsKey(pttServerID)) {
			conn = connectionHash.get(pttServerID);
		} else {
			conn = KnDBManager.getDBManagerInstance().getConnection(pttServerID, readOnly);
			if (conn == null) {
				knLogger.warn(methodName, "Connection not available for PTT server ID" , pttServerID);
				throw new SystemException("PTT Server not reachable.");
			}
			knLogger.debug(methodName, "###Con Taken###", pttServerID, " connId:", conn.getConnId());
			connectionHash.put(pttServerID, conn);
		}

		if (!readOnly) {
			conn.setWriteConnection();
		}
		return conn;
	}

	/**
	 * @param pttServerID
	 * @param dsnNumber
	 * @return
	 * @throws javax.transaction.SystemException
	 * @throws Exception
	 */
	public Connection getConnection(String pttServerID, KnDBConst.DataStores dsnNumber, boolean readOnly) throws SystemException, Exception {
		pttServerID = pttServerID + "_" + dsnNumber.getValue();
		return getConnection(pttServerID, readOnly);
	}

	/**
	 * default implementation for the interface.
	 * 
	 * @throws IllegalStateException
	 * @throws javax.transaction.SystemException
	 */
	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		throw new UnsupportedOperationException();
	}

	/**
	 * default implementation for the interface.
	 * 
	 * @return 1 if transaction is started and 0 if transaction is in Illegal
	 *         State
	 * @throws javax.transaction.SystemException
	 */
	@Override
	public int getStatus() throws SystemException {
		return state.ordinal();
	}

	/**
	 * default implementation for the interface.
	 * 
	 * @param i
	 * @throws javax.transaction.SystemException
	 */
	@Override
	public void setTransactionTimeout(int timeOut) throws SystemException {
		throw new UnsupportedOperationException();
	}

	public Long getCreationTime() {
		return creationTime;
	}
}
