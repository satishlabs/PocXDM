/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.common.ggcache.dto.KnUserProfileInfoDTO;
import com.kodiak.logger.KnLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KnUserProfileInfoDAO {

    public static final KnLogger knLogger = KnLogger.getLogger(KnUserProfileInfoDAO.class);

    public void insert(KnUserProfileInfoDTO userProfileInfo) throws SQLException {
        String methodName = "insert()";
        knLogger.debug(methodName, " Entry - ", userProfileInfo);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "INSERT INTO " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.USER_PROFILE_INFO.value() +
                    " (USERPROFILEID,USERPROFILEINDEX,TGSC_MODE)" +
                    " VALUES(?,?,?);";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, userProfileInfo.getUserProfileId());
            pStmt.setInt(2, userProfileInfo.getUserprofileIndex());
            pStmt.setInt(3, userProfileInfo.getTgscMode());
            int count = pStmt.executeUpdate();
            knLogger.info(methodName, "Query: Executed :", count);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    public void update(KnUserProfileInfoDTO userProfileInfo) throws SQLException {
        String methodName = "update()";
        knLogger.debug(methodName, "Entry -", userProfileInfo);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "UPDATE " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.USER_PROFILE_INFO.value() +
                    " SET TGSC_MODE = ? WHERE USERPROFILEID = ?";
            knLogger.debug(methodName, "query - ", sql);
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, userProfileInfo.getTgscMode());
            pStmt.setString(2, userProfileInfo.getUserProfileId());
            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed :", count);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    public void delete(String userProfileId) throws SQLException {
        String methodName = "delete()";
        knLogger.debug(methodName, " Entry -", userProfileId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        int count;
        try {
            String sql = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.USER_PROFILE_INFO.value() +
                    " WHERE USERPROFILEID =? ";
            knLogger.debug(methodName, "query -", sql);
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, userProfileId);
            count = pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed :", count);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    public KnUserProfileInfoDTO getUserProfileInfoByProfileId(String userProfileId) throws SQLException {
        String methodName = "getUserProfileInfoByProfileId()";
        knLogger.debug(methodName, " input -: userProfileId", userProfileId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnUserProfileInfoDTO userProfileInfo=null;
        try {
            String sql = "SELECT USERPROFILEID,USERPROFILEINDEX,TGSC_MODE FROM "
                    + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.USER_PROFILE_INFO.value() +
                    " WHERE USERPROFILEID= ? ";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, userProfileId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                userProfileInfo=new KnUserProfileInfoDTO();
                userProfileInfo.setUserProfileId(rs.getString(1));
                userProfileInfo.setUserprofileIndex(rs.getInt(2));
                userProfileInfo.setTgscMode(rs.getInt(3));
            }
            knLogger.debug(methodName, " exit -  :", userProfileInfo);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return userProfileInfo;
    }
}
