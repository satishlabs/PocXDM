/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.common.ggcache.dto.KnAsyncJobTaskDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;

import java.sql.*;
import java.time.Instant;
import java.util.*;


public class KnAsyncJobTaskDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnAsyncJobTaskDAO.class);
    private static final String STATUS = "STATUS";
    private static final String TASKID = "TASKID";
    private static final String TXNID = "TXNID";

    /**
     *
     * @param jobTaskDTO
     * @return
     * @throws SQLException
     */
    public boolean createAsyncJobTask(KnAsyncJobTaskDTO jobTaskDTO) throws SQLException {
        String methodName = "createAsyncJobTask(KnAsyncJobTaskDTO)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean result = false;
        knLogger.info(methodName, "ENTRY:", jobTaskDTO);
        try {
            String sql = "INSERT INTO " +
                    KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_TASK.value() +
                    " (TASKID,TXNID,CORPID,OPERATIONTYPE,TASKTYPE,USERPROFILEID,SEQUENCE,PRIORITY,START_TIME,END_TIME,STATUS,PAYLOAD,RESOURCEENTITY)"+
                    " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            knLogger.debug(methodName, "executing sql - ", sql);

            conn = KnGGConnection.getDBConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, jobTaskDTO.getTaskId());
            pstmt.setString(2,jobTaskDTO.getTxnId() );
            pstmt.setInt(3, jobTaskDTO.getCorpId());
            pstmt.setInt(4, jobTaskDTO.getOperationType());
            pstmt.setString(5, jobTaskDTO.getTaskType());
            pstmt.setString(6, jobTaskDTO.getUserProfileId());
            pstmt.setInt(7, jobTaskDTO.getSeq());
            pstmt.setInt(8, jobTaskDTO.getPriority());
            pstmt.setString(9, jobTaskDTO.getStartTimeStamp());
            pstmt.setString(10, jobTaskDTO.getEndTimeStamp());
            pstmt.setInt(11,jobTaskDTO.getStatus() );
            pstmt.setString(12,jobTaskDTO.getPayLoad() );
            pstmt.setString(13,jobTaskDTO.getResourceEntity() );

            int cnt = pstmt.executeUpdate();

            result = cnt > 0 ? true : false;

            knLogger.info(methodName, "EXIT:", result);
            return result;
        } finally {
            KnDbUtil.closeStatement(pstmt);
            KnDbUtil.closeConnection(conn);
        }
    }


    /**
     * For creating batch of tasks
     * @param jobTaskDTOs
     * @throws SQLException
     */
     public void createAsyncJobTASK(Collection<KnAsyncJobTaskDTO> jobTaskDTOs) throws SQLException {
        String methodName = "createAsyncJobTASK(Collection<KnAsyncJobTaskDTO>)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean result = false;
        knLogger.debug(methodName, "ENTRY:", jobTaskDTOs);
        try {

            String sql = "INSERT INTO " +
                    KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_TASK.value() +
                    " (TASKID,TXNID,CORPID,OPERATIONTYPE,TASKTYPE,USERPROFILEID,SEQUENCE,PRIORITY,START_TIME,END_TIME,STATUS,PAYLOAD,RESOURCEENTITY)"+
                    " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            knLogger.debug(methodName, "executing sql - ", sql);

            conn = KnGGConnection.getDBConnection();
            pstmt = conn.prepareStatement(sql);
            if (null != jobTaskDTOs && !jobTaskDTOs.isEmpty()) {
                for (KnAsyncJobTaskDTO jobTaskDTO : jobTaskDTOs) {
                    pstmt.setString(1,  jobTaskDTO.getTaskId());
                    pstmt.setString(2, jobTaskDTO.getTxnId() );
                    pstmt.setInt(3,  jobTaskDTO.getCorpId());
                    pstmt.setInt(4,  jobTaskDTO.getOperationType());
                    pstmt.setString(5,  jobTaskDTO.getTaskType());
                    pstmt.setString(6,  jobTaskDTO.getUserProfileId());
                    pstmt.setInt(7,  jobTaskDTO.getSeq());
                    pstmt.setInt(8,  jobTaskDTO.getPriority());
                    pstmt.setString(9,  jobTaskDTO.getStartTimeStamp());
                    pstmt.setString(10,  jobTaskDTO.getEndTimeStamp());
                    pstmt.setInt(11, jobTaskDTO.getStatus() );
                    pstmt.setString(12, jobTaskDTO.getPayLoad() );
                    pstmt.setString(13, jobTaskDTO.getResourceEntity() );
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            knLogger.debug(methodName, "EXIT:", result);

        } finally {
            KnDbUtil.closeStatement(pstmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    /**
     *
     * @param taskId
     * @param status
     * @throws SQLException
     */

    public void updateJobTaskStatus(String taskId,int status) throws SQLException{
        String methodName = "updateJobTaskStatus(String,int)";
        knLogger.debug(methodName, "input -: jobId", taskId,"status - ",status);
        Connection conn = null;
        PreparedStatement pStmt = null;
        String updatTS = String.valueOf(Instant.now().toEpochMilli());
        try {
            String sql = "UPDATE " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_TASK.value() +
                    " SET status = ? , END_TIME = ? WHERE taskid = ?";
            knLogger.debug(methodName,"query -",sql);
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, status);
            pStmt.setString(2,updatTS);
            pStmt.setString(3, taskId);

            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: updated task status :", count);

        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }

    }

    /**
     *
     * @param tasks
     * @throws SQLException
     */
    public int deleteAsyncJObTasks(Collection<String> tasks) throws SQLException{
        String methodName = "deleteUPMJObTasks(Collection<String>)";
        knLogger.debug(methodName, "input -: taskIds", tasks);
        Connection conn = null;
        Statement stmt = null;
        int count = 0;
        String taskStr = "";
        if (taskStr != null && !taskStr.isEmpty()) {
            for (String taskId : tasks) {
                taskStr = taskStr + "'" + taskId + "'" + ",";
            }
            taskStr = taskStr.substring(0, taskStr.length() - 1);
            knLogger.debug(methodName, "taskStr - ", taskStr);
            try {
                String sql = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_TASK.value() +
                        "WHERE taskid IN (" + taskStr + ")";
                knLogger.debug(methodName, "querey -", sql);
                conn = KnGGConnection.getDBConnection();
                stmt = conn.createStatement();
                count = stmt.executeUpdate(sql);
                knLogger.debug(methodName, "Query: no.of rows deleted -  :", count);

            } finally {
                KnDbUtil.closeStatement(stmt);
                KnDbUtil.closeConnection(conn);
            }
        }
        return count;
    }

    /**
     *
     * @param taskId
     * @return
     * @throws SQLException
     */
    public int getTaskStatus(String taskId) throws SQLException{
        String methodName = "getTaskStatus(int)";
        knLogger.debug(methodName, "input -: taskId", taskId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet resultSet = null;
        int status = 0;
        try {
            String sql = "SELECT STATUS FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_TASK.value() +
                    " WHERE TASKID= ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, taskId);
            resultSet = pStmt.executeQuery();
            if (resultSet.next()) {
                status=resultSet.getInt(STATUS);
            }

            knLogger.debug(methodName, "Query: taskId - ",taskId,"status - ", status);

        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }

        return status;
    }

    /**
     *
     * @param jobId
     * @return
     * @throws SQLException
     */
    public Map<String,Integer> getTaskStatusByJobId(String jobId) throws SQLException{
        String methodName = "getTaskStatusByJobId(String)";
        knLogger.debug(methodName, "input -: jobId", jobId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet resultSet = null;
        Map<String,Integer> taskStatusMap = new HashMap<>();
        try {
            String sql = "SELECT TASKID,STATUS FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_TASK.value() +
                    " WHERE TXNID= ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, jobId);
            resultSet = pStmt.executeQuery();
            if (resultSet.next()) {
                taskStatusMap.put(resultSet.getString(TASKID), resultSet.getInt(STATUS));
            }
            knLogger.debug(methodName, "Query: result ",taskStatusMap);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }

        return taskStatusMap;
    }

    /**
     *
     * @param profileMdn
     * @param taskId
     * @throws SQLException
     */
    public void updateProfileMdn(String profileMdn,String taskId) throws SQLException{
        String methodName = "updateProfileMdn()";
        knLogger.debug(methodName, "input -: profileMdn", profileMdn,"taskId ",taskId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "UPDATE " + KnGGCacheConstants.DG_SCHEMA+ KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_TASK.value() +
                    " SET PAYLOAD = ? WHERE TASKID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, profileMdn);
            pStmt.setString(2, taskId);
            knLogger.debug(methodName,"sql : ",sql);
            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: updated task status :", count);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }

    }

    /**
     *
     * @param taskId
     * @return
     * @throws SQLException
     */
    public String getProfileMdnByTaskId(long taskId) throws SQLException {
        String methodName = "updateJobStatus(String,int)";
        knLogger.debug(methodName, "input -: taskId ",taskId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet resultSet = null;
        String profileMdn=null;
        try {
            String sql = "SELECT PAYLOAD FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_TASK.value() +
                    " WHERE TASKID= ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, String.valueOf(taskId));
            knLogger.debug(methodName,"sql ",sql);
            resultSet = pStmt.executeQuery();
            if (resultSet.next()) {
                profileMdn=resultSet.getString("PAYLOAD");
            }
            knLogger.debug(methodName, "Query result: profileMdn :", KnGDPRTemplate.mdn(profileMdn));

        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return profileMdn;
    }

    /**
     *
     * @param txnIds
     * @throws SQLException
     */
    public int deleteAsyncJObTasksByTxnIdID(Collection<String> txnIds) throws SQLException {
        String methodName = "deleteAsyncJObTasksByTxnIdID(Collection<String>)";
        knLogger.debug(methodName, "input -: taskIds", txnIds);
        Connection conn = null;
        PreparedStatement pStmt = null;
        int count = 0;
        String txnIdStr = "";
        int index=1;
        if (txnIds != null && !txnIds.isEmpty()) {
            for (String txnId : txnIds) {
                txnIdStr = txnIdStr + "?" + ",";
            }
            txnIdStr = txnIdStr.substring(0, txnIdStr.length() - 1);
            knLogger.debug(methodName, "txnIdStr - ", txnIdStr);

            try {
                String sql = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_TASK.value() +
                        " WHERE TXNID IN (" + txnIdStr + ")";
                knLogger.debug(methodName, "executing sql -", sql);
                conn = KnGGConnection.getDBConnection();
                pStmt = conn.prepareStatement(sql);
                for(String txnId:txnIds){
                    pStmt.setString(index++, txnId);
                }
                count = pStmt.executeUpdate();
                knLogger.debug(methodName, "Query: no.of rows deleted -  :", count);

            } finally {
                KnDbUtil.closeStatement(pStmt);
                KnDbUtil.closeConnection(conn);
            }
        }
        return count;
    }

    public Collection<String> getStaleTransactionInAsyncTask() throws SQLException {
        String methodName = "getStaleTransactionInAsyncTask()";
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet resultSet = null;
        Set<String> transactionIds = new HashSet<>();
        try {
            String sql = "SELECT DISTINCT AT.TXNID FROM DG.ASYNC_JOB_TASK AT " +
                    "LEFT JOIN DG.ASYNC_JOB_NOTIFY AN ON AT.TXNID = AN.TXNID where AN.TXNID is null;";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            resultSet = pStmt.executeQuery();
            while (resultSet.next()) {
                transactionIds.add(resultSet.getString(TXNID));
            }
            knLogger.info(methodName, "Query: result ", transactionIds.size());
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }

        return transactionIds;
    }

}
