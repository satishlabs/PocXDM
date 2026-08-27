/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.dao;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMProvBatchInfoDAO.java
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
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.bulkfw.dto.KnBatchDTO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class KnXDMProvBatchInfoDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMProvBatchInfoDAO.class);

    private final String batchExe_Type = "BATCH_EXE_TYPE";
    private final String type = "TYPE";
    private final String bulkOrder_id = "BULKORDER_ID";
    private final String jobExecID = "JOB_EXECUTION_ID";
    private final String startMdn = "START_MDN";
    private final String STATUS = "STATUS";
    private final String cycleMdn = "CYCLE_MDN";
    private final String endMdn = "END_MDN";
    private final String operation_Type = "OPERATION_TYPE";
    private final String batchSize = "BATCH_SIZE";
    private final String tableName = "DG.XDM_PROV_BATCH_INFO";
    private String pttServerId;



    public KnXDMProvBatchInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    /**
     * Insert the Batch Job
     * @param batchDto
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int insert(KnBatchDTO batchDto, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "insert(KnProvBatchJobDTO, KnPersisterTxn)";

        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;

        try {

            knLogger.info(methodName, "ENTRY: Inserting to bacth table...Job Execution Id ", batchDto.getBatchExeId());
            conn = persisterTxn.getDBConnection(pttServerId, true);
            StringBuilder insertQry = new StringBuilder();
            insertQry.append("INSERT INTO ").append(tableName);
            insertQry.append(" (").append(jobExecID).append(",").append(operation_Type).append(",").append(bulkOrder_id).append(",");
            insertQry.append(startMdn).append(",").append(endMdn).append(",").append(STATUS).append(",");
            insertQry.append(batchExe_Type).append(",").append(batchSize).append(",").append(type);
            insertQry.append(")").append(" VALUES (?,?,?,?,?,?,?,?,?)");

            knLogger.debug(methodName, "INSERT QUERY:  ", insertQry.toString());
            pStmt = conn.prepareStatement(insertQry.toString());
            pStmt.setInt(1, batchDto.getBatchExeId());
            pStmt.setInt(2, batchDto.getOpertaionType());
            pStmt.setInt(3, batchDto.getBulkOrderId());
            pStmt.setLong(4, batchDto.getStartMdn());
            pStmt.setLong(5, batchDto.getEndMdn());
            pStmt.setInt(6, batchDto.getStatus());
            pStmt.setInt(7, batchDto.getBatchExeType());
            pStmt.setInt(8, batchDto.getBatchSize());
            pStmt.setInt(9, batchDto.getClientType());
            int update = pStmt.executeUpdate();
            knLogger.debug(methodName, "INSERT QUERY Completed:  ", update);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, "");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, "");
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "EXIT ");
        return batchDto.getBatchExeId();
    }

    /**
     * Updates the Batch Job
     * @param batchDto
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void update(KnBatchDTO batchDto, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "update(KnBatchJobDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY ");
        Connection conn;
        PreparedStatement pStmt = null;

        StringBuilder updateQuery = new StringBuilder();
        updateQuery.append("UPDATE ").append(tableName);
        updateQuery.append(" SET ").append(cycleMdn).append("=?").append(",").append(STATUS).append("=?")
                .append(" WHERE ").append(jobExecID).append("=?");
        knLogger.debug(methodName, "Updatequery ", updateQuery.toString());
        try {
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(updateQuery.toString());
            pStmt.setLong(1, batchDto.getNewCycleMdn());
            pStmt.setInt(2, batchDto.getStatus());
            pStmt.setInt(3, batchDto.getBatchExeId());
            pStmt.executeUpdate();

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, "");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, "");
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "EXIT ");

    }

    /**
     * Method Deletes the Batch Job
     * @param bulkOrderId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void delete(int bulkOrderId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "delete(jobExecId, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY ");
        Connection conn;
        PreparedStatement pStmt = null;
        int update;

        StringBuilder deleteQuery = new StringBuilder();
        deleteQuery.append("DELETE FROM ").append(tableName).append(" WHERE ").append(bulkOrder_id).append("=?");

        knLogger.debug(methodName, "Deletequery ", deleteQuery.toString());

        try {
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(deleteQuery.toString());
            pStmt.setInt(1, bulkOrderId);
            update = pStmt.executeUpdate();
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed to Delete - " + e.getMessage(), pttServerId, tableName, "");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed Delete - " + e.getMessage(), pttServerId, tableName, "");
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "EXIT ", update);
    }

    /**
     * Method Fetches JobExcecution ID for the BulkOrder
     * @param bulkOrderId
     * @param batchExecType
     * @param persisterTxn
     * @return
     * @throws KnException
     */
    public int getBatchExecutionId(int bulkOrderId, int batchExecType, KnPersisterTxn persisterTxn) throws KnException {
        String methodName = "getBatchExecutionId(int,int,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: get JobExecution ID... ", bulkOrderId, batchExecType);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int jobExeId = 0;
        String getDetailsQry = "SELECT " + jobExecID + " FROM " + tableName + " WHERE " + bulkOrder_id + "=? AND " + batchExe_Type + "=?";

        try {

            conn = persisterTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(getDetailsQry);
            pStmt.setInt(1, bulkOrderId);
            pStmt.setInt(2, batchExecType);
            knLogger.debug(methodName, "QUERY: Executing ", getDetailsQry);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "QUERY: Executed ", rs);

            if (rs.next()) {
                knLogger.info(methodName, "EXIT ", jobExecID);
                jobExeId = rs.getInt(jobExecID);
            }

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, getDetailsQry);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get Bulk Order Info - " + e.getMessage(), pttServerId, tableName, getDetailsQry);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info(methodName, "EXIT ");
        }
        return jobExeId;
    }

    /**
     * Fetches the Batch Excecution Info
     * @param jobExecutionId
     * @param persisterTxn
     * @return
     * @throws KnException
     */

    public KnBatchDTO getCurrentBatchExecutionInfo(Integer jobExecutionId, KnPersisterTxn persisterTxn) throws KnException {

        String methodName = "getCurrentBatchExecutionInfo(Integer, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY:... ", jobExecutionId);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnBatchDTO jobExeDto = null;
        final String getDetailsQry = "SELECT * FROM " + tableName + " WHERE " + jobExecID + "=?" + " AND " + STATUS + "=?";
        knLogger.debug(methodName, "QUERY: Executing ", getDetailsQry);
        try {
            conn = persisterTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(getDetailsQry);
            pStmt.setInt(1, jobExecutionId);
            pStmt.setInt(2, KnBulkFwConstants.STATUS.IN_PROGRESS.value());
            rs = pStmt.executeQuery();

            if (rs.next()) {
                jobExeDto = new KnBatchDTO();
                jobExeDto.setBatchExeId(rs.getInt(jobExecID));
                jobExeDto.setOpertaionType(rs.getInt(operation_Type));
                jobExeDto.setBulkOrderId(rs.getInt(bulkOrder_id));
                jobExeDto.setStartMdn(rs.getLong(startMdn));
                jobExeDto.setEndMdn(rs.getLong(endMdn));
                jobExeDto.setCycleMdn(rs.getLong(cycleMdn));
                jobExeDto.setStatus(rs.getInt(STATUS));
                jobExeDto.setBatchExeType(rs.getInt(batchExe_Type));
                jobExeDto.setBatchSize(rs.getInt(batchSize));
                jobExeDto.setClientType(rs.getInt(type));
                knLogger.info(methodName, "EXIT: jobExeDto ", jobExeDto);
            }

        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred - ", e);
            throw KnDbUtil.processException(e, "ERROR - " + e.getMessage(), pttServerId, tableName, "");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "ERROR - " + e.getMessage(), pttServerId, tableName, "");
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "EXIT ");
        return jobExeDto;
    }

    /**
     * Cleans the batches from Prov_Batch table whose status is not INPROGRESS and NOT_STARTED
     * @param persisterTxn
     * @throws KnException
     */
    public void clearBatchTable(KnPersisterTxn persisterTxn) throws KnException {
        String methodName = "clearBatchTable(KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY ");
        Connection conn;
        PreparedStatement pStmt = null;
        int update;

        StringBuilder deleteQuery = new StringBuilder();
        deleteQuery.append("DELETE FROM ").append(tableName).append(" WHERE ").append(STATUS).append(" NOT IN (?,?)");
        knLogger.debug(methodName, "Deletequery ", deleteQuery.toString());

        try {
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(deleteQuery.toString());
            pStmt.setInt(1, KnBulkFwConstants.STATUS.NOT_STARTED.value());
            pStmt.setInt(2, KnBulkFwConstants.STATUS.IN_PROGRESS.value());
            update = pStmt.executeUpdate();
            knLogger.debug(methodName, "Batch Table cleared ", update);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed to Delete - " + e.getMessage(), pttServerId, tableName, "");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed Delete - " + e.getMessage(), pttServerId, tableName, "");
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.info(methodName, "EXIT ", update);
    }


    /**
     * Method Fetches the MAX jobexec from the table
     * This method is used to reinitialize Automic integer on XDM status change
     * @param persisterTxn
     * @return
     * @throws KnException
     */
    public int getMaxJobExecutionId(KnPersisterTxn persisterTxn) throws KnException{

        String methodName = "getMaxJobExecutionId(KnProvBatchJobDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Fetching the MAX ID. ");
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int jobexecId = 1;
        StringBuilder getQry = new StringBuilder();
        getQry.append("SELECT MAX(").append(jobExecID).append(") FROM ").append(tableName);
        knLogger.debug(methodName, "QUERY: Executing ", getQry);
        try {
            conn = persisterTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(getQry.toString());
            rs = pStmt.executeQuery();

            if (rs.next()) {
                jobexecId = rs.getInt(1);
            }
            knLogger.debug(methodName, "QUERY: Executed ", rs);
        }catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed to get Max Id  - " + e.getMessage(), pttServerId, tableName, "");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get Max Id  - " + e.getMessage(), pttServerId, tableName, "");
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return jobexecId;
    }
}
