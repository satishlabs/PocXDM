/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.dao;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSpringBatchDAO.java
 * Subsystem:  XDMS-BulkFrameWork
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     08/07/2015    8.0
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
 * ************************************************************************
 */
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDBConfigInfo;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;

import java.sql.*;
import java.util.Date;


public class KnSpringBatchDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSpringBatchDAO.class);
    private String pttServerId;

    public KnSpringBatchDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    private static final String  DELETE_BATCH_STEP_EXECUTION_CONTEXT = "DELETE FROM DG.BTCH_STEP_EXECUTION_CONTEXT WHERE STEP_EXECUTION_ID IN (SELECT STEP_EXECUTION_ID FROM DG.BTCH_STEP_EXECUTION WHERE JOB_EXECUTION_ID IN (SELECT JOB_EXECUTION_ID FROM  DG.BTCH_JOB_EXECUTION where END_TIME < ? AND STATUS='COMPLETED'))";
    private static final String  DELETE_BATCH_STEP_EXECUTION         = "DELETE FROM DG.BTCH_STEP_EXECUTION WHERE JOB_EXECUTION_ID IN (SELECT JOB_EXECUTION_ID FROM  DG.BTCH_JOB_EXECUTION where END_TIME < ? AND STATUS='COMPLETED')";
    private static final String  DELETE_BATCH_JOB_EXECUTION_CONTEXT  = "DELETE FROM DG.BTCH_JOB_EXECUTION_CONTEXT WHERE JOB_EXECUTION_ID IN (SELECT JOB_EXECUTION_ID FROM  DG.BTCH_JOB_EXECUTION where END_TIME < ? AND STATUS='COMPLETED')";
    private static final String  DELETE_BATCH_JOB_EXECUTION_PARAMS   = "DELETE FROM DG.BTCH_JOB_EXECUTION_PARAMS WHERE JOB_EXECUTION_ID IN (SELECT JOB_INSTANCE_ID FROM  DG.BTCH_JOB_EXECUTION where END_TIME < ? AND STATUS='COMPLETED')";
    private static final String  DELETE_BATCH_JOB_EXECUTION          = "DELETE FROM DG.BTCH_JOB_EXECUTION where END_TIME < ? AND STATUS='COMPLETED'";
    private static final String  DELETE_BATCH_JOB_INSTANCE           = "DELETE FROM DG.BTCH_JOB_INSTANCE WHERE JOB_INSTANCE_ID NOT IN (SELECT JOB_INSTANCE_ID FROM DG.BTCH_JOB_EXECUTION)";

    /**
     * method to clean up all the spring batch info data
     *
     *
     * @throws KnDAOException exception
     */
    public void cleanupSpringBatchInfo() throws KnDAOException {
        String methodName = "cleanupSpringBatchInfo(KnPeristerTxn)";
        knLogger.info(methodName, "ENTRY: Cleanup of Spring batch Data");

        String stepExeCntxtQry = "DELETE FROM DG.BTCH_STEP_EXECUTION_CONTEXT";
        String stepExeQry = "DELETE FROM DG.BTCH_STEP_EXECUTION";
        String jobExeCntxtQry = "DELETE FROM DG.BTCH_JOB_EXECUTION_CONTEXT";
        String jobExeQry = "DELETE FROM DG.BTCH_JOB_EXECUTION";
        String jobParamsQry = "DELETE FROM DG.BTCH_JOB_EXECUTION_PARAMS";
        String jobInstQry = "DELETE FROM DG.BTCH_JOB_INSTANCE";

        KnPersisterTxn persisterTxn = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            deleteQuery(stepExeCntxtQry, persisterTxn);
            deleteQuery(stepExeQry, persisterTxn);
            deleteQuery(jobExeCntxtQry, persisterTxn);
            deleteQuery(jobExeQry, persisterTxn);
            deleteQuery(jobParamsQry, persisterTxn);
            deleteQuery(jobInstQry, persisterTxn);
            persisterTxn.save();
        }catch (KnDAOException e) {
            knLogger.error(methodName, "DB exception Occurred", e);
            KnDbUtil.rollback(persisterTxn);
        }
        knLogger.info(methodName, "EXIT : Cleanup of Spring batch Data");

    }

    /**
     * Method Cleans UP the BATCH Metadata table
     * @param query
     * @param persisterTxn
     * @throws KnDAOException
     */
    private void deleteQuery(final String query, final KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteQuery(String, KnPersisterTxn)";
        knLogger.info(methodName, "executing query ", query);

        Connection conn;
        Statement pStmt = null;
        try {

            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.createStatement();

            knLogger.debug(methodName, "QUERY: Executing ", query);
            boolean result = pStmt.execute(query);
            knLogger.debug(methodName, "QUERY: Executed ", result);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(e, "Failed to Delete Query - " + e.getMessage(), pttServerId, "", query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to Delete Query - " + e.getMessage(), pttServerId, "", query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.info(methodName, "EXIT : Delete Query");
        }

    }

    /**
     * Method to cleanup Batch Metadata Table for single batch execution ID
     * @param jobExecId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void cleanupSpringBatchTableForJobId(int jobExecId, KnPersisterTxn persisterTxn) throws KnDAOException{

        final String methodName = "cleanupSpringBatchTableForJobId";
        knLogger.info(methodName, "Cleaning batch tables for JOBEXECUTIONID", jobExecId );

        String jobInstanceIdQuery = "select JOB_EXECUTION_ID from DG.BTCH_JOB_EXECUTION_PARAMS where STRING_VAL = "+jobExecId;
        long jobInstanceId= selectQuery(jobInstanceIdQuery,persisterTxn);

        String jobExecutionIdQuery = "select JOB_EXECUTION_ID from DG.BTCH_JOB_EXECUTION where JOB_INSTANCE_ID="+jobInstanceId;
        long jobExecutionId = selectQuery(jobExecutionIdQuery,persisterTxn);

        String stepExceIdQuery = "SELECT STEP_EXECUTION_ID from DG.BTCH_STEP_EXECUTION where JOB_EXECUTION_ID = "+jobExecutionId;

        long stepExceId= selectQuery(stepExceIdQuery,persisterTxn);

        String stepExeCntxtQry = "delete from DG.BTCH_STEP_EXECUTION_CONTEXT where STEP_EXECUTION_ID = "+stepExceId;
        String stepExeQry = "delete from DG.BTCH_STEP_EXECUTION where JOB_EXECUTION_ID = "+jobExecutionId;
        String jobExeCntxtQry = "delete from DG.BTCH_JOB_EXECUTION_CONTEXT where JOB_EXECUTION_ID ="+jobExecutionId;
        String jobExeQry = "DELETE FROM DG.BTCH_JOB_EXECUTION where JOB_EXECUTION_ID ="+jobExecutionId;
        String jobParamsQry = "DELETE FROM DG.BTCH_JOB_EXECUTION_PARAMS where JOB_EXECUTION_ID="+jobInstanceId;
        String jobInstQry = "DELETE FROM DG.BTCH_JOB_INSTANCE where JOB_INSTANCE_ID="+jobInstanceId;

        deleteQuery(stepExeCntxtQry, persisterTxn);
        deleteQuery(stepExeQry, persisterTxn);
        deleteQuery(jobExeCntxtQry, persisterTxn);
        deleteQuery(jobExeQry, persisterTxn);
        deleteQuery(jobParamsQry, persisterTxn);
        deleteQuery(jobInstQry, persisterTxn);


        knLogger.info(methodName, "Cleaning batch tables completed for JOBEXECUTIONID", jobExecId );
    }

    /**
     * Method retrives the JoBExecution ID from Batch Metadata Tables
     * @param query
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    private long selectQuery(final String query, final KnPersisterTxn persisterTxn) throws KnDAOException{
        long valve=0;
        String methodName = "selectQuery(query, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: get value for query ", query);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {

            conn = persisterTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);

            knLogger.debug(methodName, "QUERY: Executing ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed ", rs);

            if (rs.next()) {
                valve=rs.getLong(1);
            }
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed to get value - " + e.getMessage(), pttServerId,"", query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get value  - " + e.getMessage(), pttServerId,"", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "EXIT : JOBID ", valve);

        return valve;
    }


    /**
     * Method to Removes Spring Batch MetaData tables Data Whose JOB are completed before 30 mins
     * @throws KnDAOException
     */
    public void removeSpringBatchHistoryTableData() throws KnDAOException{
        String methodName = "removeSpringBatchHistoryTableData()";
        knLogger.info(methodName, "Entry");
        Date date = new Date();
        long ONE_MINUTE_IN_MILLIS=60000;//millisecs
        long time=date.getTime();
        Date substarctThirtyMinutes = new Date(time - (30 * ONE_MINUTE_IN_MILLIS));
        Timestamp sqlTime = new Timestamp(substarctThirtyMinutes.getTime());
        KnPersisterTxn persisterTxn = null;
        if (pttServerId == null) {
            KnDBConfigInfo dbConfigInfo = KnDbUtil.getDBConfigInfo();
            pttServerId = dbConfigInfo.getLocalPttId();
        }
        knLogger.debug(methodName, "pttServerId- ", pttServerId);
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            persisterTxn.open();
            deleteQuery(DELETE_BATCH_STEP_EXECUTION_CONTEXT, sqlTime, persisterTxn);
            deleteQuery(DELETE_BATCH_STEP_EXECUTION, sqlTime, persisterTxn);
            deleteQuery(DELETE_BATCH_JOB_EXECUTION_CONTEXT, sqlTime, persisterTxn);
            deleteQuery(DELETE_BATCH_JOB_EXECUTION_PARAMS, sqlTime, persisterTxn);
            deleteQuery(DELETE_BATCH_JOB_EXECUTION, sqlTime, persisterTxn);
            deleteQuery(DELETE_BATCH_JOB_INSTANCE, persisterTxn);
            persisterTxn.save();
        }catch (KnDAOException e) {
            knLogger.error(methodName, "DB exception Occurred", e);
            KnDbUtil.rollback(persisterTxn);
        }
        knLogger.info(methodName, "EXIT : Spring Batch Meta Data Tables");
    }

    private void deleteQuery(final String query, Timestamp sqlTime, final KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteQuery(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "executing query ", query);

        Connection conn;
        PreparedStatement pStmt = null;
        try {

            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setTimestamp(1, sqlTime);


            knLogger.info(methodName, "QUERY: Executing ", query);
            boolean result = pStmt.execute();
            knLogger.debug(methodName, "QUERY: Executed ", result);

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(e, "Failed to Delete Query - " + e.getMessage(), pttServerId, "", query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to Delete Query - " + e.getMessage(), pttServerId, "", query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            knLogger.debug(methodName, "EXIT : Delete Query");
        }

    }
}
