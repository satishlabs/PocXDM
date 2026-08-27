/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.common.ggcache.dto.KnCorpGrpLmrExtnDTO;
import com.kodiak.common.ggcache.dto.KnUserProfileInfoDTO;
import com.kodiak.logger.KnLogger;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class KnCorpGrpLmrExtnDAO {

    public static final KnLogger knLogger = KnLogger.getLogger(KnCorpGrpLmrExtnDAO.class);

    public KnCorpGrpLmrExtnDTO getCorpGrpLmrExtn(int corpId, int corpGroupId) throws SQLException {
        String methodName = "getCorpGrpLmrExtn(int, int)";
        knLogger.debug(methodName, " input -: corpId ", corpId, " corpGroupId ", corpGroupId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnCorpGrpLmrExtnDTO corpGrpLmrExtnDTO=null;
        try {
            String sql = "SELECT CORPID, CORPGROUPID, LMREXTN FROM "
                    + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORPGRP_LMREXTN.value() +
                    " WHERE CORPID= ?  AND CORPGROUPID =?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, corpId);
            pStmt.setInt(2, corpGroupId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                corpGrpLmrExtnDTO=new KnCorpGrpLmrExtnDTO();
                corpGrpLmrExtnDTO.setCorpId(rs.getInt(1));
                corpGrpLmrExtnDTO.setCorpGroupId(rs.getInt(2));
                corpGrpLmrExtnDTO.setLmrExtn(rs.getBytes(3));
            }
            knLogger.debug(methodName, " exit -  :", corpGrpLmrExtnDTO);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return corpGrpLmrExtnDTO;
    }

    public void insertGrpLmrExtn(int corpId, int groupId,String ugwConfig) throws SQLException {
        String methodName = "insert(corpId,groupId,ugwConfig)";
        knLogger.debug(methodName, "corpId: ", corpId," groupId:",groupId," ugwConfig:",ugwConfig);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "INSERT INTO " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORPGRP_LMREXTN.value() +
                    " (CORPID, CORPGROUPID, LMREXTN)"+
                    " VALUES(?,?,?);";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, corpId);
            pStmt.setInt(2, groupId);
            pStmt.setBytes(3, ugwConfig.getBytes(StandardCharsets.UTF_8));
            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, " Inserted:", count);

        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    public void updateGrpLmrExtn(int corpId, int groupId,String ugwConfig) throws SQLException {
        String methodName = "updateGrpLmrExtn()";
        knLogger.debug(methodName, "corpId: ", corpId," groupId:",groupId," ugwConfig:",ugwConfig);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "UPDATE " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORPGRP_LMREXTN.value() +
                    " SET LMREXTN = ? WHERE CORPID = ? and CORPGROUPID=? ";
            knLogger.debug(methodName,"query - ",sql);
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setBytes(1, ugwConfig.getBytes(StandardCharsets.UTF_8));
            pStmt.setInt(2, corpId);
            pStmt.setInt(3, groupId);

            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, " updated :", count);

        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }


    }

    public void deleteGrpLmrExtn(int corpId, int groupId) throws SQLException {
        String methodName = "deleteGrpLmrExtn()";
        knLogger.debug(methodName, "corpId :", corpId," groupId:",groupId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        int count = 0;
        int index=1;
        String txnIdStr = "";
        try {
            String sql = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORPGRP_LMREXTN.value() +
                    " WHERE CORPID=? and CORPGROUPID=?";
            knLogger.debug(methodName, "query -", sql);

            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, corpId);
            pStmt.setInt(2, groupId);
            count = pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: no.of rows deleted -  :", count);

        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    public void deleteGrpLmrExtn(int groupId) throws SQLException {
        String methodName = "deleteGrpLmrExtn(int groupId)";
        knLogger.debug(methodName ,"groupId:",groupId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        int count = 0;
        try {
            String sql = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORPGRP_LMREXTN.value() +
                    " WHERE CORPGROUPID=?";
            knLogger.debug(methodName, "query -", sql);

            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, groupId);
            count = pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: no.of rows deleted -  :", count);

        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }
}
