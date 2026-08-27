/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class KnAsyncJobNotifyDAO {

    public static final KnLogger knLogger = KnLogger.getLogger(KnAsyncJobNotifyDAO.class);

    /**
     * @param jobDTO
     * @throws SQLException
     */
    public void insert(KnAsyncJobDTO jobDTO) throws SQLException {
        String methodName = "insert(KnAsyncJobDTO)";
        knLogger.info(methodName, "input -: ", jobDTO);
        Connection conn = null;
        PreparedStatement pStmt = null;

        try {
            String sql = "INSERT INTO " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " (TXNID,CORPID,USERPROFILEID,RESOURCEENTITY,RESOURCETYPE,PAYLOAD,OPERATIONTYPE,STATUS,CREATIONTIME,UPDATIONTIME,CALLBACKURI)"+
                    " VALUES(?,?,?,?,?,?,?,?,?,?,?);";

            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, jobDTO.getTxnId());
            pStmt.setInt(2, jobDTO.getCorpId());
            pStmt.setString(3, jobDTO.getUserProfileId());
            pStmt.setString(4, jobDTO.getResourceEntity());
            pStmt.setInt(5, jobDTO.getResourceType());
            pStmt.setString(6, jobDTO.getPayLoad());
            pStmt.setInt(7, jobDTO.getOpType());
            pStmt.setInt(8, jobDTO.getOpStatus());
            pStmt.setString(9, jobDTO.getCreationTime());
            pStmt.setString(10, jobDTO.getUpdationTime());
            pStmt.setString(11, jobDTO.getCallBackUri());
            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ASYNC_JOB_NOTIFY:", count);

        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }


    /**
     * @param txnId
     * @param opStatus
     * @throws SQLException
     */

    public void updateJobStatus(String txnId, int opStatus) throws SQLException {
        String methodName = "updateJobStatus(String,int)";
        knLogger.info(methodName, "input -: txnId", txnId, "opStatus - ", opStatus);
        Connection conn = null;
        PreparedStatement pStmt = null;
        long updateTs = Instant.now().toEpochMilli();
        try {
            String sql = "UPDATE " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " SET status = ? , UPDATIONTIME = ? WHERE txnId = ?";
            knLogger.debug(methodName,"query - ",sql);
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, opStatus);
            pStmt.setString(2,String.valueOf(updateTs));
            pStmt.setString(3, txnId);

            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: updated job status :", count);

        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }


    }

    public void insertTempStaleRecords(String txnId, int opStatus, List<String> profileMdns, String profileId, int corpId, String payload, int opType) throws SQLException {
        String methodName = "insertTempStaleRecords(String,int)";
        knLogger.info(methodName, "input -: txnId", txnId, "opStatus - ", opStatus, "profileMdns: ", KnGDPRTemplate.profileMdn(profileMdns), "profileId: ", profileId, "payload: ", payload, "opType: ", opType);
        Connection conn = null;
        PreparedStatement pStmt = null;
        long updateTs = Instant.now().toEpochMilli();
        try {
            String sql = "INSERT INTO " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " (STATUS,CREATIONTIME,RESOURCEENTITY,USERPROFILEID,TXNID,CORPID,PAYLOAD,OPERATIONTYPE)" + " VALUES(?,?,?,?,?,?,?,?);";
            knLogger.debug(methodName, "query - ", sql);
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            knLogger.debug(methodName, "profileMdns to be updated in GG: ", KnGDPRTemplate.profileMdn(profileMdns));
            for (String profileMdn : profileMdns) {
                pStmt.setInt(1, opStatus);
                pStmt.setString(2, String.valueOf(updateTs));
                pStmt.setString(3, profileMdn);
                pStmt.setString(4, profileId);
                pStmt.setString(5, randomTransactionId());
                pStmt.setInt(6, corpId);
                pStmt.setString(7, payload);
                pStmt.setInt(8, opType);
                pStmt.addBatch();
            }
            int[] updateCounts = pStmt.executeBatch();
            knLogger.debug(methodName, "Query: updated job status :", Arrays.toString(updateCounts), "done inserting all issue mdns");

        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    public String randomTransactionId() {
        String methodName = "randomTransactionId()";
        String transactionId = UUID.randomUUID().toString();
        knLogger.debug(methodName, "transactionId: ", transactionId);
        return transactionId;
    }

    /**
     * @param txnIds
     * @throws SQLException
     */
    public int delete(Collection<String> txnIds) throws SQLException {
        String methodName = "delete(Collection<String>)";
        knLogger.debug(methodName, "input -: txnId", txnIds);
        Connection conn = null;
        PreparedStatement pStmt = null;
        int count = 0;
        int index=1;
        String txnIdStr = "";
        if (txnIds != null && !txnIds.isEmpty()) {
            for (String txnId : txnIds) {
                txnIdStr = txnIdStr + "?" + ",";
            }
            txnIdStr = txnIdStr.substring(0, txnIdStr.length() - 1);
            knLogger.debug(methodName, "txnIdStr - ", txnIdStr);
            try {
                String sql = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                        " WHERE txnId IN (" + txnIdStr + " );";
                knLogger.debug(methodName, "query -", sql);

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

    /**
     * This method return long running jobs(IN PROGRESS JOBS ) for retry which are more than configured time if none configured then considering 1hr as time
     *
     * @param time
     * @return
     * @throws SQLException
     */

    public List<KnAsyncJobDTO> getAllLongRunningJobsForCorp(String corpId, long time) throws SQLException {
        String methodName = "getAllLongRunningJobs(String,long)";
        knLogger.debug(methodName, "input -: time", time, "corpId -", corpId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<KnAsyncJobDTO> jobNotifyDTOS = new ArrayList<>();
        try {
            String sql = "SELECT txnid,userProfileid,resourceentity,resourcetype,creationtime,corpid FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " WHERE corpid = " + "?" + "creationtime <= (creationtime -" + "?" + ")";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1,corpId);
            pStmt.setLong(2,time);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnAsyncJobDTO jobDTO = new KnAsyncJobDTO();
                jobDTO.setTxnId(rs.getString("txnid"));
                jobDTO.setUserProfileId(rs.getString("userProfileid"));
                jobDTO.setResourceEntity(rs.getString("resourceentity"));
                jobDTO.setResourceType(rs.getInt("resourcetype"));
                jobDTO.setCreationTime(rs.getString("creationtime"));
                jobDTO.setCorpId(rs.getInt("corpid"));
                jobNotifyDTOS.add(jobDTO);
            }
            knLogger.debug(methodName, "result -  :", jobNotifyDTOS);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }

        return jobNotifyDTOS;
    }

    /**
     * @param userProfileId
     * @return
     * @throws SQLException
     */

    public List<Integer> getJobStatusByProfileId(String userProfileId) throws SQLException {
        String methodName = "getJobStatusByProfileId(String)";
        knLogger.debug(methodName, "input -: userProfileId", userProfileId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<Integer> jobSatusList = new ArrayList<>();
        try {
            String sql = "SELECT STATUS FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " WHERE USERPROFILEID= ? AND STATUS IN(0,1);";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, userProfileId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                jobSatusList.add(rs.getInt(1));
            }
            knLogger.info(methodName, "jobSatusList -  :", jobSatusList.size());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return jobSatusList;
    }

    public List<KnAsyncJobDTO> getAllLongRunningJobs(long timeInMs) throws SQLException{
        String methodName = "getAllLongRunningJobs(String,long)";
        knLogger.debug(methodName, "input -: time", timeInMs);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<KnAsyncJobDTO> jobNotifyDTOS = new ArrayList<>();
        try {
            String sql = "SELECT txnid,userProfileid,resourceentity,resourcetype,creationtime,corpid,OPERATIONTYPE,STATUS FROM " +
                    KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " WHERE status = 1 AND UPDATIONTIME <= (" + timeInMs + ")";
            knLogger.debug(methodName,"query -",sql);
            conn = KnGGConnection.getDBConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                KnAsyncJobDTO jobDTO = new KnAsyncJobDTO();
                jobDTO.setTxnId(rs.getString("txnid"));
                jobDTO.setUserProfileId(rs.getString("userProfileid"));
                jobDTO.setResourceEntity(rs.getString("resourceentity"));
                jobDTO.setResourceType(rs.getInt("resourcetype"));
                jobDTO.setCreationTime(rs.getString("creationtime"));
                jobDTO.setCorpId(rs.getInt("corpid"));
                jobDTO.setOpType(rs.getInt("OPERATIONTYPE"));
                jobDTO.setOpStatus(rs.getInt("STATUS"));
                jobNotifyDTOS.add(jobDTO);
            }
            knLogger.debug(methodName, "result -  :", jobNotifyDTOS);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            KnDbUtil.closeConnection(conn);
        }

        return jobNotifyDTOS;

    }

    public List<Integer> getJobStatusByCorpId(int corpId) throws SQLException{
        String methodName = "getJobStatusByProfileId(String)";
        knLogger.debug(methodName, "input -: corpId", corpId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<Integer> jobSatusList = new ArrayList<>();
        try {
            String sql = "SELECT STATUS FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " WHERE CORPID = ? AND STATUS = 1;";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, corpId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                jobSatusList.add(rs.getInt(1));
            }
            knLogger.info(methodName, "jobSatusList -  :", jobSatusList.size());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return jobSatusList;

    }

    public List<Integer> getJobStatusForWatcherByCorpId(int corpId) throws SQLException{
        String methodName = "getJobStatusForWatcherByCorpId(int)";
        knLogger.debug(methodName, "input -: corpId", corpId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<Integer> jobSatusList = new ArrayList<>();
        try {
            String sql = "SELECT STATUS FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " WHERE CORPID = ? AND STATUS = 11;";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, corpId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                jobSatusList.add(rs.getInt(1));
            }
            knLogger.info(methodName, "jobSatusList -  :", jobSatusList.size());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return jobSatusList;

    }

    /**
     * @param txnIds
     * @param opStatus
     * @throws SQLException
     */

    public int updateAllAsyncJobStatus(Set<String> txnIds, int opStatus)  throws SQLException {
        String methodName = "updateJobStatus(String,int)";
        knLogger.debug(methodName, "input -: txnIds", txnIds, "opStatus - ", opStatus);
        int count = 0;
        Connection conn = null;
        PreparedStatement pStmt = null;
        String updateTs = String.valueOf(Instant.now().toEpochMilli());
        String txnIdStr = "";
        int index=3;
        if (txnIds != null && !txnIds.isEmpty()) {
            for (String txnId : txnIds) {
                txnIdStr = txnIdStr + "?" + ",";
            }
            txnIdStr = txnIdStr.substring(0, txnIdStr.length() - 1);
            knLogger.debug(methodName, "txnIdStr - ", txnIdStr);
            try {
                String sql = "UPDATE " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                        " SET status =?, UPDATIONTIME = ? WHERE txnId IN(" + txnIdStr + ")";

                knLogger.debug(methodName, " query - ", sql);

                conn = KnGGConnection.getDBConnection();
                pStmt = conn.prepareStatement(sql);

                pStmt.setInt(1,opStatus);
                pStmt.setString(2,updateTs);
                for(String mdn:txnIds){
                    pStmt.setString(index++, mdn);
                }
                count = pStmt.executeUpdate();

                knLogger.debug(methodName, "Query: updated job status :", count);

            } finally {
                KnDbUtil.closeStatement(pStmt);
                KnDbUtil.closeConnection(conn);
            }
        }
        return count;
    }

    /**
     *
     * @param status
     * @return
     * @throws SQLException
     */
    public List<KnAsyncJobDTO> getJobsByStatus(Collection<Integer> status) throws SQLException {
        String methodName = "getJobsByStatus(Collection<String>)";
        knLogger.debug(methodName, "input -: status", status);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String statusStr = "";
        List<KnAsyncJobDTO> jobNotifyDTOS = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            for (Integer s : status) {
                statusStr = statusStr + s + ",";
            }
            statusStr = statusStr.substring(0, statusStr.length() - 1);


            knLogger.debug(methodName, " statusStr -", statusStr);
            try {
                String sql = "SELECT txnid,userProfileid,resourceentity,resourcetype,creationtime,corpid,OPERATIONTYPE,STATUS,PAYLOAD " +
                        " FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                        " WHERE STATUS IN (" + statusStr + ")";
                knLogger.debug(methodName, "query - ", sql);
                conn = KnGGConnection.getDBConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    KnAsyncJobDTO jobDTO = new KnAsyncJobDTO();
                    jobDTO.setTxnId(rs.getString("txnid"));
                    jobDTO.setUserProfileId(rs.getString("userProfileid"));
                    jobDTO.setResourceEntity(rs.getString("resourceentity"));
                    jobDTO.setResourceType(rs.getInt("resourcetype"));
                    jobDTO.setCreationTime(rs.getString("creationtime"));
                    jobDTO.setCorpId(rs.getInt("corpid"));
                    jobDTO.setOpType(rs.getInt("OPERATIONTYPE"));
                    jobDTO.setOpStatus(rs.getInt("STATUS"));
                    jobDTO.setPayLoad(rs.getString("PAYLOAD"));
                    jobNotifyDTOS.add(jobDTO);
                }
                knLogger.debug(methodName, "result -  :", jobNotifyDTOS);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(stmt);
                KnDbUtil.closeConnection(conn);
            }
        }
        return jobNotifyDTOS;
    }

    /**
     *
     *
     * @return
     * @throws SQLException
     */
    public LinkedHashMap<String, KnAsyncJobDTO> getNewJobs(int jobsToBePulled, Map<Integer, String> corpIdMap) throws SQLException {
        String methodName = "getNewJobs(int, Map<Integer, String>)";
        knLogger.info(methodName, "ENTRY:- ", jobsToBePulled, " corpIdMap :", corpIdMap.keySet());
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        LinkedHashMap<String, KnAsyncJobDTO> jobNotifyDTOS = new LinkedHashMap<>();
        try {
            String corpIdsStr = corpIdMap.entrySet().stream().map(entry -> "(" + entry.getKey() + ",'" + entry.getValue() + "')").collect(Collectors.joining(","));
            String sql = "SELECT txnid,userProfileid,resourceentity,resourcetype,creationtime,corpid,OPERATIONTYPE,STATUS,PAYLOAD,CALLBACKURI FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " WHERE STATUS = 0 AND (CORPID, CREATIONTIME) IN (" + corpIdsStr + ") ORDER BY creationtime ASC LIMIT " + jobsToBePulled;
                conn = KnGGConnection.getDBConnection();
                stmt = conn.createStatement();
                knLogger.debug(methodName, "executing query - ", sql);
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    KnAsyncJobDTO jobDTO = new KnAsyncJobDTO();
                    String txndId = rs.getString("txnid");
                    jobDTO.setTxnId(txndId);
                    jobDTO.setUserProfileId(rs.getString("userProfileid"));
                    jobDTO.setResourceEntity(rs.getString("resourceentity"));
                    jobDTO.setResourceType(rs.getInt("resourcetype"));
                    jobDTO.setCreationTime(rs.getString("creationtime"));
                    jobDTO.setCorpId(rs.getInt("corpid"));
                    jobDTO.setOpType(rs.getInt("OPERATIONTYPE"));
                    jobDTO.setOpStatus(rs.getInt("STATUS"));
                    jobDTO.setPayLoad(rs.getString("PAYLOAD"));
                    jobDTO.setCallBackUri(rs.getString("CALLBACKURI"));
                    jobNotifyDTOS.put(txndId,jobDTO);
                }
                knLogger.info(methodName, "EXIT -  :", jobNotifyDTOS.size());
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(stmt);
                KnDbUtil.closeConnection(conn);
            }

        return jobNotifyDTOS;
    }

    public LinkedHashMap<Integer, Map<Integer, String>> getStatusCorpIdMap(Collection<Integer> statusList) throws SQLException {
        String methodName = "getStatusCorpIdMap()";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        LinkedHashMap<Integer, Map<Integer, String>> jobNotifyDTOS = new LinkedHashMap<>();
        String statusStr = "";
        if (statusList != null && !statusList.isEmpty()) {
            for (Integer s : statusList) {
                statusStr = statusStr + s + ",";
            }
            statusStr = statusStr.substring(0, statusStr.length() - 1);
            knLogger.debug(methodName, " statusStr -", statusStr);
            try {
                String sql = "SELECT CORPID, STATUS, MIN(creationtime) FROM DG.ASYNC_JOB_NOTIFY where STATUS IN (" + statusStr + ") GROUP BY CORPID, STATUS";
                conn = KnGGConnection.getDBConnection();
                stmt = conn.createStatement();
                knLogger.debug(methodName, "executing query - ", sql);
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    Integer status = rs.getInt("STATUS");
                    Integer corpId = rs.getInt("CORPID");
                    String creationTime = rs.getString("MIN(creationtime)");
                    if (jobNotifyDTOS.containsKey(status)) {
                        jobNotifyDTOS.get(status).put(corpId, creationTime);
                    } else {
                        Map<Integer, String> corpIdTimeMap = new HashMap<>();
                        corpIdTimeMap.put(corpId, creationTime);
                        jobNotifyDTOS.put(status, corpIdTimeMap);
                    }
                }
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(stmt);
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.debug(methodName, "statusCorpIdMap -  :", jobNotifyDTOS.size());
        return jobNotifyDTOS;
    }

    /**
     * Method to fetch data job status and updation time
     * @param status
     * @param updatedTimeInMs
     * @return
     * @throws SQLException
     */
    public List<KnAsyncJobDTO> getJobsByStatusAndUpdateTime(Collection<Integer> status,long updatedTimeInMs) throws SQLException {
        String methodName = "getJobsByStatusAndUpdateTime(Collection<String>,long)";
        knLogger.debug(methodName, "input -: status", status," updatedTimeInMs - ",updatedTimeInMs);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String statusStr = "";
        List<KnAsyncJobDTO> jobNotifyDTOS = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            for (Integer s : status) {
                statusStr = statusStr + s + ",";
            }
            statusStr = statusStr.substring(0, statusStr.length() - 1);


            try {
                String sql = "SELECT txnid,userProfileid,resourceentity,resourcetype,creationtime,corpid,OPERATIONTYPE,STATUS,PAYLOAD,UPDATIONTIME " +
                        " FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                        " WHERE STATUS IN (" + statusStr + ") AND UPDATIONTIME <= "+updatedTimeInMs;
                knLogger.debug(methodName, "query - ", sql);
                conn = KnGGConnection.getDBConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    KnAsyncJobDTO jobDTO = new KnAsyncJobDTO();
                    jobDTO.setTxnId(rs.getString("txnid"));
                    jobDTO.setUserProfileId(rs.getString("userProfileid"));
                    jobDTO.setResourceEntity(rs.getString("resourceentity"));
                    jobDTO.setResourceType(rs.getInt("resourcetype"));
                    jobDTO.setCreationTime(rs.getString("creationtime"));
                    jobDTO.setCorpId(rs.getInt("corpid"));
                    jobDTO.setOpType(rs.getInt("OPERATIONTYPE"));
                    jobDTO.setOpStatus(rs.getInt("STATUS"));
                    jobDTO.setPayLoad(rs.getString("PAYLOAD"));
                    jobDTO.setUpdationTime(rs.getString("UPDATIONTIME"));

                    jobNotifyDTOS.add(jobDTO);
                }
                knLogger.debug(methodName, "result -  :", jobNotifyDTOS);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(stmt);
                KnDbUtil.closeConnection(conn);
            }
        }
        return jobNotifyDTOS;
    }

    /**
     * provides Job status information for all the input txnIds
     * @param txnIds
     * @return
     * @throws SQLException
     */
    public List<KnAsyncJobDTO> getJobsByTxnIds(Collection<String> txnIds) throws SQLException {
        String methodName = "getJobStatusByTxnIds(Collection<String>)";
        knLogger.debug(methodName, "input -: txnIds", txnIds);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String txnIdStr = "";
        List<KnAsyncJobDTO> jobNotifyDTOS = new ArrayList<>();
        if (txnIds != null && !txnIds.isEmpty()) {
            for (String s : txnIds) {
                txnIdStr = txnIdStr +"'"+ s + "',";
            }
            txnIdStr = txnIdStr.substring(0, txnIdStr.length() - 1);


            try {
                String sql = "SELECT txnid,userProfileid,resourceentity,resourcetype,creationtime,corpid,OPERATIONTYPE,STATUS,PAYLOAD,UPDATIONTIME " +
                        " FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                        " WHERE txnid IN (" + txnIdStr + ")";
                knLogger.debug(methodName, "query - ", sql);
                conn = KnGGConnection.getDBConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    KnAsyncJobDTO jobDTO = new KnAsyncJobDTO();
                    jobDTO.setTxnId(rs.getString("txnid"));
                    jobDTO.setUserProfileId(rs.getString("userProfileid"));
                    jobDTO.setResourceEntity(rs.getString("resourceentity"));
                    jobDTO.setResourceType(rs.getInt("resourcetype"));
                    jobDTO.setCreationTime(rs.getString("creationtime"));
                    jobDTO.setCorpId(rs.getInt("corpid"));
                    jobDTO.setOpType(rs.getInt("OPERATIONTYPE"));
                    int status = rs.getInt("STATUS");
                    //set status = 3(Failed) if the status of job is CRASHED(5)
                    if(status == KnConstants.UPM_JOB_CRASHED_STATUS)
                        status = KnConstants.UPM_JOB_FAILED_STATUS;
                    jobDTO.setOpStatus(status);
                    jobDTO.setPayLoad(rs.getString("PAYLOAD"));
                    jobDTO.setUpdationTime(rs.getString("UPDATIONTIME"));
                    jobNotifyDTOS.add(jobDTO);
                }
                knLogger.debug(methodName, "result -  :", jobNotifyDTOS);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(stmt);
                KnDbUtil.closeConnection(conn);
            }
        }
        return jobNotifyDTOS;
    }

    public Map<String,Integer> getJobStatusByOperationId(int operationId) throws SQLException{
        String methodName = "getJobStatusByOperationId()";
        knLogger.debug(methodName, "input -: operationId", operationId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<String,Integer> jobSatusMap = new HashMap<>();
        try {
            String sql = "SELECT PAYLOAD,CORPID FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_TASK.value() +
                    " WHERE OPERATIONTYPE = ? ";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, operationId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                jobSatusMap.put(rs.getString("PAYLOAD"),rs.getInt("CORPID"));
            }
            knLogger.debug(methodName, "jobSatusMap -  :", jobSatusMap);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return jobSatusMap;
    }

    public Map<String,KnAsyncJobDTO> getJobStatusByOperationIdAndStatus(int operationId,int status) throws SQLException{
        String methodName = "getJobStatusByOperationIdAndStatus()";
        knLogger.debug(methodName, "input -: operationId", operationId," status :",status);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<String,KnAsyncJobDTO> jobSatusMap = new HashMap<>();
        try {
            String sql = "SELECT CORPID,TXNID,PAYLOAD,RESOURCETYPE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " WHERE OPERATIONTYPE = ? AND STATUS=? ";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, operationId);
            pStmt.setInt(2, status);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnAsyncJobDTO jobDTO = new KnAsyncJobDTO();
                jobDTO.setCorpId(rs.getInt("CORPID"));
                jobDTO.setTxnId(rs.getString("TXNID"));
                jobDTO.setPayLoad(rs.getString("PAYLOAD"));
                jobDTO.setResourceType(rs.getInt("RESOURCETYPE"));
                jobSatusMap.put(rs.getString("PAYLOAD"),jobDTO);
            }
            knLogger.debug(methodName, "jobSatusMap -  :", jobSatusMap);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return jobSatusMap;
    }
    public void deleteStaleAsyncJob(KnAsyncJobDTO asyncJobDTO) throws SQLException {
        String methodName = "delete(Collection<String>)";
        knLogger.info(methodName, "input -: asyncJobDTO", asyncJobDTO);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " WHERE USERPROFILEID =? and OPERATIONTYPE=? and  STATUS=?";
            knLogger.debug(methodName, "query -", sql);
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, asyncJobDTO.getUserProfileId());
            pStmt.setInt(2, asyncJobDTO.getOpType());
            pStmt.setInt(3, asyncJobDTO.getOpStatus());
            int count = pStmt.executeUpdate();
            knLogger.info(methodName, "Query: no.of rows deleted -  :", count);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    public List<String> getCATStaleTransactionIds() throws SQLException{
        String methodName = "getCATStaleTransactionIds()";
        knLogger.debug(methodName, "Entry :");
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<String> catStaleTransactionIds = new ArrayList<>();
        try {
            String sql = "select a.TXNID from "+KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CAT_ASYNC_TXN_INFO.value() +" c join " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " a on a.TXNID=c.TRANSACTIONID where a.STATUS=2 and c.OPERATIONSTATUS=1 ;";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                catStaleTransactionIds.add(rs.getString("TXNID"));
            }
            knLogger.debug(methodName, "catStaleTransactionIds -  :", catStaleTransactionIds);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return catStaleTransactionIds;
    }

    public List<Integer> getAsyncJobstatusList(String userProfileId, String mdn) throws SQLException{
        String methodName = "getAsyncJobstatusList()";
        knLogger.info(methodName, "Entry with params ",userProfileId, KnGDPRTemplate.mdn(mdn));
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<Integer> statusList = new ArrayList<>();
        try {
            String sql = "select STATUS from "+KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +" WHERE USERPROFILEID =? " +
                    "AND RESOURCEENTITY=?;";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, userProfileId);
            pStmt.setString(2, mdn);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                statusList.add(rs.getInt("STATUS"));
            }
            knLogger.info(methodName, "Mdn Exists in GG -  :", statusList);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return statusList;
    }

    public void deleteStaleFromGG(String userProfileId) throws SQLException {
        String methodName = "deleteStaleFromGG";
        knLogger.info(methodName, "userProfileId:", userProfileId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        String query = null;
        try {
            conn = KnGGConnection.getDBConnection();
            query = "DELETE FROM DG.ASYNC_JOB_NOTIFY WHERE USERPROFILEID = ?;";
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, userProfileId);
            int deletedRecord = pStmt.executeUpdate();
            knLogger.info(methodName, "DeletedRecords: ", deletedRecord);
        } catch (SQLException e) {
            knLogger.error(methodName,"SQLException Occured:",e.getMessage());
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    public LinkedHashMap<String, KnAsyncJobDTO> getRetryJobs(int jobsToBePulled) throws SQLException {
        String methodName = "getRetryJobs(int)";
        knLogger.debug(methodName, "ENTRY:- ", jobsToBePulled);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        LinkedHashMap<String, KnAsyncJobDTO> jobNotifyDTOS = new LinkedHashMap<>();
        try {
            String sql = "SELECT txnid,userProfileid,resourceentity,resourcetype,creationtime,corpid,OPERATIONTYPE,STATUS,PAYLOAD,CALLBACKURI FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " WHERE STATUS = 9 order by creationtime asc LIMIT  " + jobsToBePulled;
            conn = KnGGConnection.getDBConnection();
            stmt = conn.createStatement();
            knLogger.debug(methodName, "executing query - ", sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                KnAsyncJobDTO jobDTO = new KnAsyncJobDTO();
                String txndId = rs.getString("txnid");
                jobDTO.setTxnId(txndId);
                jobDTO.setUserProfileId(rs.getString("userProfileid"));
                jobDTO.setResourceEntity(rs.getString("resourceentity"));
                jobDTO.setResourceType(rs.getInt("resourcetype"));
                jobDTO.setCreationTime(rs.getString("creationtime"));
                jobDTO.setCorpId(rs.getInt("corpid"));
                jobDTO.setOpType(rs.getInt("OPERATIONTYPE"));
                jobDTO.setOpStatus(rs.getInt("STATUS"));
                jobDTO.setPayLoad(rs.getString("PAYLOAD"));
                jobDTO.setCallBackUri(rs.getString("CALLBACKURI"));
                jobNotifyDTOS.put(txndId, jobDTO);
            }
            knLogger.info(methodName, "EXIT -  :", jobNotifyDTOS.size());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            KnDbUtil.closeConnection(conn);
        }
        return jobNotifyDTOS;
    }

    public LinkedHashMap<String, KnAsyncJobDTO> getWatchersDetails(int jobsToBePulled, Map<Integer, String> corpIdMap) throws SQLException {
        String methodName = "getWatchersDetails()";
        knLogger.info(methodName, "ENTRY:- ", jobsToBePulled, " corpIdMap :", corpIdMap.keySet());
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        LinkedHashMap<String, KnAsyncJobDTO> watcherDetailsList = new LinkedHashMap<>();
        try {
            String corpIdsStr = corpIdMap.entrySet().stream().map(entry -> "(" + entry.getKey() + ",'" + entry.getValue() + "')").collect(Collectors.joining(","));
            String sql = "SELECT TXNID , CORPID , USERPROFILEID , PAYLOAD , STATUS FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " WHERE STATUS IN (10 , 13 , 14 , 15) AND (CORPID, CREATIONTIME) IN (" + corpIdsStr + ") ORDER BY creationtime ASC LIMIT " + jobsToBePulled;
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnAsyncJobDTO details = new KnAsyncJobDTO();
                String txnId = rs.getString("TXNID");
                details.setTxnId(txnId);
                details.setCorpId(rs.getInt("CORPID"));
                details.setUserProfileId(rs.getString("USERPROFILEID"));
                details.setPayLoad(rs.getString("PAYLOAD"));
                details.setOpStatus(rs.getInt("STATUS"));
                knLogger.info(methodName, "Details Exists in GG details-  :", details.toString());
                watcherDetailsList.put(txnId, details);
            }
            knLogger.info(methodName, "query", sql, "Details Exists in GG -  :", watcherDetailsList.size());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return watcherDetailsList;
    }

    public int updateWatcherState(Set<String> txnIds, int opStatus) throws SQLException {
        String methodName = "updateWatcherState(Set<String>,int)";
        knLogger.debug(methodName, "input -: txnIds", txnIds, "opStatus - ", opStatus);
        int count = 0;
        Connection conn = null;
        PreparedStatement pStmt = null;
        String updateTs = String.valueOf(Instant.now().toEpochMilli());
        try {
            if (txnIds != null && !txnIds.isEmpty()) {
                StringBuilder placeholders = new StringBuilder();
                for (int i = 0; i < txnIds.size(); i++) {
                    placeholders.append("?,");
                }
                placeholders.deleteCharAt(placeholders.length() - 1);
                String sql = "UPDATE " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                        " SET STATUS = ?, UPDATIONTIME = ? WHERE txnId IN (" + placeholders + ")";
                knLogger.debug(methodName, "query - ", sql);

                conn = KnGGConnection.getDBConnection();
                pStmt = conn.prepareStatement(sql);

                pStmt.setInt(1, opStatus);
                pStmt.setString(2, updateTs);

                int index = 3;
                for (String txnId : txnIds) {
                    pStmt.setString(index++, txnId);
                }

                count = pStmt.executeUpdate();
                knLogger.debug(methodName, "Query: updated job status :", count);
            }
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return count;
    }

    public List<KnAsyncJobDTO> getWatchersDetailsRunningjobs(long timeInMs) throws SQLException {
        String methodName = "getWatchersDetailsRunningjobs()";
        knLogger.info(methodName, "Entry with params ", timeInMs);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<KnAsyncJobDTO> watcherDetailsList = new ArrayList<>();
        try {
            String sql = "SELECT TXNID , CORPID , USERPROFILEID FROM DG.ASYNC_JOB_NOTIFY WHERE STATUS = 11 AND UPDATIONTIME <= (" + timeInMs + ")";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setLong(1, timeInMs);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnAsyncJobDTO details = new KnAsyncJobDTO();
                details.setTxnId(rs.getString("TXNID"));
                details.setCorpId(rs.getInt("CORPID"));
                details.setUserProfileId(rs.getString("USERPROFILEID"));
                watcherDetailsList.add(details);
            }
            knLogger.info(methodName, "Details Exists in GG -  :", watcherDetailsList.size());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return watcherDetailsList;
    }

    public Map<String, String> getInProgressJobs(int opStatus) throws SQLException {
        String methodName = "getInProgressJobs(int)";
        knLogger.info(methodName, "input -: opStatus - ", opStatus);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<String, String> txnIds = new HashMap<>();
        try {
            String sql = "SELECT TXNID, OPERATIONTYPE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " WHERE STATUS = ? ";
            knLogger.debug(methodName, "query - ", sql);
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, opStatus);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                txnIds.put(rs.getString("TXNID"), rs.getString("OPERATIONTYPE"));
            }
            knLogger.info(methodName, "Query: InProgress txnIds:", txnIds);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return txnIds;
    }


    public void deleteCompletedCorpIdFromGG(List<Integer> corpIds) throws SQLException {
        String methodName = "deleteCompletedCorpIdFromGG";
        knLogger.info(methodName, "corpIds:", corpIds);
        Connection conn = null;
        PreparedStatement pStmt = null;
        String query = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_TASK.value() + " WHERE CORPID IN (" + corpIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ") AND OPERATIONTYPE = 16";
        try {
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(query);
            int deletedRecords = pStmt.executeUpdate();
            knLogger.info(methodName, "DeletedRecords: ", deletedRecords);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException Occurred: ", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }


    public int getTransactionCountBasedOnStatus() throws SQLException {
        String methodName = "getTransactionCountBasedOnStatus()";
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            String sql = "SELECT COUNT(TXNID) FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.ASYNC_JOB_NOTIFY.value() +
                    " WHERE STATUS IN (10,13,14,15)";
            knLogger.debug(methodName, "query - ", sql);
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            rs = pStmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            knLogger.info(methodName, "Query: TxnIds count--->", count);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return count;
    }

}


