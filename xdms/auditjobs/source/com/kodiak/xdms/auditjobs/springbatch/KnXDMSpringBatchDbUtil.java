/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnXDMSpringBatchUtil.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker P       Oct 13, 2012   7.4
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
package com.kodiak.xdms.auditjobs.springbatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;

/**
 * @author Ravi Shanker .P
 * 
 */
public class KnXDMSpringBatchDbUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMSpringBatchDbUtil.class);
	private final String className = KnXDMSpringBatchDbUtil.class.getName();
	private String pttServerId;

	public KnXDMSpringBatchDbUtil(String pttServerId) {
		this.pttServerId = pttServerId;
	}

	/**
	 * Method which gets the list of all possible jobs that can be cleanup.
	 * 
	 * @param persisterTxn
	 * @return Map<Integer, List<Integer>>
	 * @throws KnDAOException
	 */
	public Map<Integer, List<Integer>> getCleanupJobDetails(KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "getCleanupJobList()";
		knLogger.info( methodName, "ENTRY: get Clean up Job details");

		Connection conn;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String selectJobListQry = null;
		boolean ownedTxn = false;
		String tableName = "DG.BTCH_JOB_EXECUTION";

		// Map of job Instance Id and Job Execution ID
		Map<Integer, List<Integer>> cleanUpJobMap = new HashMap<Integer, List<Integer>>();

		try {
			if (persisterTxn == null) {
				persisterTxn = KnPersisterTxn.getPersisterTxn();
				knLogger.debug( methodName, "Opening the transaction");
				persisterTxn.open();
				ownedTxn = true;
			}

			// SELECT JOB_INSTANCE_ID, JOB_EXECUTION_ID FROM
			// DG.BTCH_JOB_EXECUTION WHERE STATUS IN ('COMPLETED', 'FAILED')
			// AND STATUS NOT IN ('STARTED', 'STOPPED');
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("SELECT JOB_INSTANCE_ID, JOB_EXECUTION_ID FROM ").append(tableName);
			strBuilder.append(" WHERE STATUS IN (").append("'COMPLETED', 'FAILED', 'ABANDON'").append(")");
			strBuilder.append(" AND STATUS NOT IN (");
			strBuilder.append("'STARTING', 'STARTED', 'STOPPED', 'STOPPING'").append(")");

			selectJobListQry = strBuilder.toString();

			conn = persisterTxn.getDBConnection(pttServerId, true);
			pStmt = conn.prepareStatement(selectJobListQry);

			knLogger.debug( methodName, "QUERY: Executing", selectJobListQry);
			rs = pStmt.executeQuery();
			knLogger.debug( methodName, "QUERY: Executed");

			while (rs.next()) {
				int jobExecutionId = rs.getInt("JOB_EXECUTION_ID");
				Integer jobInstanceId = rs.getInt("JOB_INSTANCE_ID");

				if (cleanUpJobMap.containsKey(jobInstanceId)) {
					List<Integer> jobExecutionIdList = cleanUpJobMap.get(jobInstanceId);
					jobExecutionIdList.add(jobExecutionId);
				} else {
					List<Integer> jobExecutionIdList = new ArrayList<Integer>();
					jobExecutionIdList.add(jobExecutionId);
					cleanUpJobMap.put(jobInstanceId, jobExecutionIdList);
				}
			}

			if (ownedTxn) {
				knLogger.debug( methodName, "Saving the transaction");
				persisterTxn.save();
			}

		} catch (KnDAOException dbConne) {
			knLogger.error( methodName, "DAO Exception occurred");
			if (ownedTxn) {
				KnDbUtil.rollback(persisterTxn);
			}
			throw dbConne;
		} catch (SQLException e) {
			knLogger.error( methodName, "SQL Exception occurred");
			if (ownedTxn) {
				KnDbUtil.rollback(persisterTxn);
			}
			throw KnDbUtil.processException(e, "Failed to get Spring batch info - " + e.getMessage(), pttServerId, tableName, selectJobListQry);
		} catch (Exception e) {
			knLogger.error( methodName, "Unexpected Exception - ", e);
			if (ownedTxn) {
				KnDbUtil.rollback(persisterTxn);
			}
			throw KnDbUtil.processException(e, "Failed to get Spring batch info - " + e.getMessage(), pttServerId, tableName, selectJobListQry);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeStatement(pStmt);
			knLogger.info( methodName, "EXIT : get Clean up Job details");
		}

		return cleanUpJobMap;

	}

	/**
	 * 
	 * @param deleteJobDetails
	 * @param persisterTxn
	 * @throws KnDAOException
	 */
	public void deleteJobDetails(Map<Integer, List<Integer>> deleteJobDetails, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "deleteJobDetails()";
		knLogger.info( methodName, "ENTRY: Delete job details for ", deleteJobDetails);

		boolean ownedTxn = false;

		List<Integer> jobInstanceIds = new LinkedList<Integer>();
		List<Integer> jobExecutionIds = new LinkedList<Integer>();
		List<Integer> jobStepExeIds = null;
		try {
			if (persisterTxn == null) {
				persisterTxn = KnPersisterTxn.getPersisterTxn();
				knLogger.debug( methodName, "Opening the transaction");
				persisterTxn.open();
				ownedTxn = true;
			}

			for (Integer jobInstanceId : deleteJobDetails.keySet()) {
				jobInstanceIds.add(jobInstanceId);
				jobExecutionIds.addAll(deleteJobDetails.get(jobInstanceId));
			}

			// get the list of Step Execution Ids;
			jobStepExeIds = getStepExecutionIdList(jobExecutionIds, persisterTxn);

			// Delete entries from BATCH_STEP_EXECUTION_CONTEXT
			String StepExeCntxt_table = "DG.BTCH_STEP_EXECUTION_CONTEXT";
			StringBuilder delStepExeCntxt = new StringBuilder();
			delStepExeCntxt.append("DELETE FROM ").append(StepExeCntxt_table).append(" WHERE STEP_EXECUTION_ID IN (");

			int i = 0;
			for (Integer stepExeId : jobStepExeIds) {
				delStepExeCntxt.append(stepExeId);
				if (i < (jobStepExeIds.size() - 1)) {
					delStepExeCntxt.append(", ");
				}
			}
			delStepExeCntxt.append(")");
			deleteQuery(StepExeCntxt_table, delStepExeCntxt.toString(), persisterTxn);

			String stepExeTable = "DG.BTCH_STEP_EXECUTION";
			String jobExeCntxtTable = "DG.BTCH_JOB_EXECUTION_CONTEXT";
			String jobExeTable = "DG.BTCH_JOB_EXECUTION";

			StringBuilder delStepExe = new StringBuilder();
			delStepExe.append("DELETE FROM ").append(stepExeTable).append(" WHERE JOB_EXECUTION_ID IN (");

			StringBuilder delJobExeCntxt = new StringBuilder();
			delJobExeCntxt.append("DELETE FROM ").append(jobExeCntxtTable).append(" WHERE JOB_EXECUTION_ID IN (");

			StringBuilder delJobExe = new StringBuilder();
			delJobExe.append("DELETE FROM ").append(jobExeTable).append(" WHERE JOB_EXECUTION_ID IN (");

			int j = 0;
			for (Integer jobExeId : jobExecutionIds) {
				delStepExe.append(jobExeId);
				delJobExeCntxt.append(jobExeId);
				delJobExe.append(jobExeId);

				if (j++ < (jobExecutionIds.size() - 1)) {
					delStepExe.append(", ");
					delJobExeCntxt.append(", ");
					delJobExe.append(", ");
				}
			}
			delStepExe.append(")");
			delJobExeCntxt.append(")");
			delJobExe.append(")");

			// Delete entries from DG.BTCH_STEP_EXECUTION
			deleteQuery(stepExeTable, delStepExe.toString(), persisterTxn);

			// Delete entries from DG.BTCH_JOB_EXECUTION_CONTEXT
			deleteQuery(jobExeCntxtTable, delJobExeCntxt.toString(), persisterTxn);

			// Delete entries from DG.BTCH_JOB_EXECUTION
			deleteQuery(jobExeTable, delJobExe.toString(), persisterTxn);

			String jobParamsTable = "DG.BTCH_JOB_EXECUTION_PARAMS";
			String jobInstTable = "DG.BTCH_JOB_INSTANCE";

			StringBuilder delJobParams = new StringBuilder();
			delJobParams.append("DELETE FROM ").append(jobParamsTable).append(" WHERE JOB_INSTANCE_ID IN (");

			StringBuilder delJobInst = new StringBuilder();
			delJobInst.append("DELETE FROM ").append(jobInstTable).append(" WHERE JOB_INSTANCE_ID IN (");

			int k = 0;
			for (Integer jobInstId : jobInstanceIds) {
				delJobParams.append(jobInstId);
				delJobInst.append(jobInstId);
				if (k++ < (jobInstanceIds.size() - 1)) {
					delJobParams.append(", ");
					delJobInst.append(", ");
				}
			}
			delJobParams.append(")");
			delJobInst.append(")");

			// Delete entries from DG.BTCH_JOB_EXECUTION_PARAMS
			deleteQuery(jobParamsTable, delJobParams.toString(), persisterTxn);

			// Delete entries from DG.BTCH_JOB_INSTANCE
			deleteQuery(jobInstTable, delJobInst.toString(), persisterTxn);

			if (ownedTxn) {
				knLogger.debug( methodName, "Saving the transaction");
				persisterTxn.save();
			}

		} catch (KnDAOException dbConne) {
			knLogger.error( methodName, "DAO Exception occurred");
			if (ownedTxn) {
				KnDbUtil.rollback(persisterTxn);
			}
			throw dbConne;
		} catch (Exception e) {
			knLogger.error( methodName, "Unexpected Exception - ", e);
			if (ownedTxn) {
				KnDbUtil.rollback(persisterTxn);
			}
			throw KnDbUtil.processException(e, "Failed to Deleted job details - " + e.getMessage(), pttServerId, "BATCH_TABLES", null);
		} finally {
			knLogger.info( methodName, "EXIT : Deleted job details");
		}

	}

	/**
	 * 
	 * @param jobExecutionIdList
	 * @param persisterTxn
	 * @return
	 * @throws KnDAOException
	 */
	private List<Integer> getStepExecutionIdList(List<Integer> jobExecutionIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "getStepExecutionIdList(List<Integer>, KnPersisterTxn)";
		List<Integer> stepExecutionIdList = new ArrayList<Integer>();
		knLogger.info( methodName, "ENTRY: Get Step Execution Id List");

		String selectQry = null;
		String TABLE_NAME = "DG.BTCH_STEP_EXECUTION";
		Connection conn;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {

			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("SELECT STEP_EXECUTION_ID FROM ").append(TABLE_NAME).append(" WHERE JOB_EXECUTION_ID IN (");
			int i = 0;
			for (Integer jobExecutionId : jobExecutionIdList) {
				strBuilder.append(jobExecutionId);
				if (i++ < (jobExecutionIdList.size() - 1)) {
					strBuilder.append(",");
				}
			}
			strBuilder.append(")");

			selectQry = strBuilder.toString();
			conn = persisterTxn.getDBConnection(pttServerId, true);
			pStmt = conn.prepareStatement(selectQry);

			knLogger.debug( methodName, "QUERY: Executing ", selectQry);
			rs = pStmt.executeQuery();
			knLogger.debug( methodName, "QUERY : Executed");

			while (rs.next()) {
				stepExecutionIdList.add(rs.getInt("STEP_EXECUTION_ID"));
			}

		} catch (KnDAOException dbConne) {
			knLogger.error( methodName, "DAO Exception occurred");
			throw dbConne;
		} catch (SQLException e) {
			knLogger.error( methodName, "SQL Exception occurred");
			throw KnDbUtil.processException(e, "Failed to create Bulk Order Info - " + e.getMessage(), pttServerId, TABLE_NAME, selectQry);
		} catch (Exception e) {
			knLogger.error( methodName, "Unexpected Exception - ", e);
			throw KnDbUtil.processException(e, "Failed to create Bulk Order Info - " + e.getMessage(), pttServerId, TABLE_NAME, selectQry);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeStatement(pStmt);
			knLogger.info( methodName, "EXIT : Get Step Execution Id List");
		}

		return stepExecutionIdList;
	}

	/**
	 * 
	 * @param tableName
	 * @param query
	 * @param persisterTxn
	 * @throws KnDAOException
	 */
	private void deleteQuery(final String tableName, final String query, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "deleteQuery(String, String, KnPersisterTxn)";
		knLogger.info( methodName, "executing query ", query, "  on ", tableName);

		Connection conn;
		PreparedStatement pStmt = null;
		try {

			conn = persisterTxn.getDBConnection(pttServerId, false);
			pStmt = conn.prepareStatement(query);

			knLogger.debug( methodName, "QUERY: Executing ", query);
			int result = pStmt.executeUpdate();
			knLogger.debug( methodName, "QUERY: Executed ", result);

		} catch (KnDAOException dbConne) {
			knLogger.error( methodName, "DAO Exception occurred");
			throw dbConne;
		} catch (SQLException e) {
			knLogger.error( methodName, "SQL Exception occurred");
			throw KnDbUtil.processException(e, "Failed to Delete Query - " + e.getMessage(), pttServerId, tableName, query);
		} catch (Exception e) {
			knLogger.error( methodName, "Unexpected Exception - ", e);
			throw KnDbUtil.processException(e, "Failed to Delete Query - " + e.getMessage(), pttServerId, tableName, query);
		} finally {
			KnDbUtil.closeStatement(pStmt);
			knLogger.info( methodName, "EXIT : Delete Query");
		}

	}
}
