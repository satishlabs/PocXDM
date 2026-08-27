/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnBulkConfigDocDiffUtil.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker P       Oct 15, 2012   7.4
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
 * ************************************************************************/
package com.kodiak.xdms.auditjobs.bulkfw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;

/**
 * @author Ravi Shanker .P
 * 
 */
public class KnBulkConfigDocDiffDbUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnBulkConfigDocDiffDbUtil.class);
	private final String className = KnBulkConfigDocDiffDbUtil.class.getName();
	private String pttServerId;

	public KnBulkConfigDocDiffDbUtil() {
		this.pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
	}

	/**
	 * 
	 * @param persisterTxn
	 * @return
	 * @throws KnDAOException
	 */
	public List<Integer> getSubsConfCompletedJobList(KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "getSubsConfCompletedJobList(KnPersisterTxn)";
		List<Integer> completedJobList = new LinkedList<Integer>();
		Connection conn;
		PreparedStatement pStmt = null;
		ResultSet rs = null;

		knLogger.info( methodName, "ENTRY : get Clean up BO details");

		final String tableName = "DG.XDM_BULK_ORDER_INFO";
		final String selectQry = "SELECT BULKORDER_ID FROM " + tableName + " WHERE STATUS = 3 AND BULKORDER_TYPE = 1";


		try {
			conn = persisterTxn.getDBConnection(pttServerId, true);
			pStmt = conn.prepareStatement(selectQry);
			knLogger.debug( methodName, "QUERY: Executing", selectQry);
			rs = pStmt.executeQuery();
			knLogger.debug( methodName, "QUERY: Executed");

			while (rs.next()) {
				completedJobList.add(rs.getInt("BULKORDER_ID"));
			}

		} catch (KnDAOException dbConne) {
			knLogger.error( methodName, "DAO Exception occurred");
			throw dbConne;
		} catch (SQLException e) {
			knLogger.error( methodName, "SQL Exception occurred");
			throw KnDbUtil.processException(e, "Failed to get Clean up BO details - " + e.getMessage(), pttServerId, tableName, selectQry);
		} catch (Exception e) {
			knLogger.error( methodName, "Unexpected Exception - ", e);
			throw KnDbUtil.processException(e, "Failed to get Clean up BO details- " + e.getMessage(), pttServerId, tableName, selectQry);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeStatement(pStmt);
		}
        knLogger.info( methodName, "EXIT : get Clean up BO details");

		return completedJobList;
	}

	/**
	 * 
	 * @param boList
	 * @param persisterTxn
	 * @throws KnDAOException
	 */
	public void cleanUpSubsConfigBO(List<Integer> boList, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "cleanUpSubsConfigBO(List<Integer>, KnPersisterTxn)";
		knLogger.info( methodName, "ENTRY: Clean up of BO list ", boList);

		Connection conn;
		PreparedStatement pStmt = null;
		PreparedStatement pStmt1 = null;

		String configDoc_Qry = null;
		String boInfo_Qry = null;

		try {
			conn = persisterTxn.getDBConnection(pttServerId, false);
			StringBuilder configDocQuery = new StringBuilder("DELETE FROM DG.XDM_BO_CONFIG_DOC WHERE BULKORDER_ID IN (");
			StringBuilder boInfoQuery = new StringBuilder("DELETE FROM DG.XDM_BULK_ORDER_INFO WHERE BULKORDER_ID IN (");

			int i = 0;
			for (Integer boId : boList) {
				configDocQuery.append(boId);
				boInfoQuery.append(boId);
				if (i++ < (boList.size() - 1)) {
					configDocQuery.append(", ");
					boInfoQuery.append(", ");
				}
			}
			configDocQuery.append(")");
			boInfoQuery.append(")");

			configDoc_Qry = configDocQuery.toString();
			boInfo_Qry = boInfoQuery.toString();

			pStmt = conn.prepareStatement(configDoc_Qry);
			pStmt1 = conn.prepareStatement(boInfo_Qry);

			knLogger.debug( methodName, "QUERY: Executing ", configDoc_Qry);
			int result = pStmt.executeUpdate();
			knLogger.debug( methodName, "QUERY: Executed", result);

			knLogger.debug( methodName, "QUERY: Executing ", boInfo_Qry);
			int result1 = pStmt1.executeUpdate();
			knLogger.debug( methodName, "QUERY: Executed", result1);

		} catch (KnDAOException dbConne) {
			knLogger.error( methodName, "DAO Exception occurred");
			throw dbConne;
		} catch (SQLException e) {
			knLogger.error( methodName, "SQL Exception occurred");
			throw KnDbUtil.processException(e, "Failed to get Clean up BO details - " + e.getMessage(), pttServerId, null, null);
		} catch (Exception e) {
			knLogger.error( methodName, "Unexpected Exception - ", e);
			throw KnDbUtil.processException(e, "Failed to get Clean up BO details- " + e.getMessage(), pttServerId, null, null);
		} finally {
			KnDbUtil.closeStatement(pStmt);
			KnDbUtil.closeStatement(pStmt1);
		}
        knLogger.info( methodName, "EXIT : get Clean up BO details");

	}
}
