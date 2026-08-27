/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.logger.KnLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

/**
 * This table maintains information related to asynch calls that triggerd from CATUI and this table is owned by CAT SUBSYESTEM. We should update this table when job related to the
 * txn in progress /success or failed along with the time stamp
 */
public class KnCatAsyncTxnInfoDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCatAsyncTxnInfoDAO.class);


    public int updateTxnStatus(String txnId, int opStatus, String opMsg, int msgStatus) throws SQLException {
        String methodName = "updateTxnStatus(String,int,String,int)";
        knLogger.debug(methodName, "input -: txnId", txnId, "opStatus ", opStatus, "opMsg", opMsg, " msgStatus - ", msgStatus);
        Connection conn = null;
        PreparedStatement pStmt = null;
        String updatTS = String.valueOf(System.currentTimeMillis());
        int count = 0;
        try {
            String sql = "UPDATE " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CAT_ASYNC_TXN_INFO.value() +
                    " SET OPERATIONSTATUS = ? ,UPDATEDTS = ?, OPERATIONMSG = ? , MSGREADSTATUS  = ? WHERE TRANSACTIONID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, opStatus);
            pStmt.setString(2, updatTS);
            pStmt.setString(3, opMsg);
            pStmt.setInt(4, msgStatus);
            pStmt.setString(5, txnId);
            knLogger.debug(methodName, "sql : ", sql);
            count = pStmt.executeUpdate();
            knLogger.info(methodName, "Query: updated task status :", count, txnId);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return count;
    }

    /**
     * Bulk update for all txnIds with same status,opMsg & msgStatus
     * @param txnIds
     * @param opStatus
     * @param opMsg
     * @param msgStatus
     * @throws SQLException
     */
    public void updateTxnStatus(Collection<String> txnIds, int opStatus, String opMsg, int msgStatus) throws SQLException {
        String methodName = "updateTxnStatus()";
        knLogger.debug(methodName, "input -: txnIds", txnIds,"opStatus ",opStatus,"opMsg",opMsg," msgStatus - ",msgStatus);
        Connection conn = null;
        PreparedStatement pStmt = null;
        String updatTS = String.valueOf(System.currentTimeMillis());
        try {
            String sql = "UPDATE " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CAT_ASYNC_TXN_INFO.value() +
                    " SET OPERATIONSTATUS = ? ,UPDATEDTS = ?, OPERATIONMSG = ? , MSGREADSTATUS  = ? WHERE TRANSACTIONID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            for(String txnId:txnIds) {
                pStmt.setInt(1, opStatus);
                pStmt.setString(2, updatTS);
                pStmt.setString(3, opMsg);
                pStmt.setInt(4, msgStatus);
                pStmt.setString(5, txnId);
                knLogger.debug(methodName, "sql : ", sql);
                pStmt.addBatch();
            }
            int count[] = pStmt.executeBatch();
            knLogger.debug(methodName, "Query: updated task status :", count);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }


    }

}
