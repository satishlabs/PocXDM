/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p>
 * File name:  KnFeatureBitRollBackInfoDao.java
 * Subsystem:  PoC
 * <p>
 * Name                        		 Date                    	 Release
 * --------------------    		 ----------------       	 ------------------
 * Sravan kumar Kuppala          20/10/23, 10:15 AM                  13.1
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 *
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2022 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 */

package com.kodiak.common.ggcache.dao;

import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.logger.KnLogger;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class KnFeatureBitRollBackInfoDao {

    public static final KnLogger knLogger = KnLogger.getLogger(KnFeatureBitRollBackInfoDao.class);

    public void insert(String oldFeatureBit, String crntFeatureBit, int status) throws SQLException {
        String methodName = "insert(String,String, int)";
        knLogger.info(methodName, " input -: oldFeatureBit ", oldFeatureBit, " Status ", status);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String updatTS = String.valueOf(Instant.now().toEpochMilli());
        String sql = "INSERT INTO " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.FEATUREBIT_ROLLBACK_INFO.value() +
                " (SRC_RELEASE, OLD_RELEASE,REQ_TIME, REQ_STATUS)" +
                " VALUES(?,?,?,?)";
        knLogger.debug(methodName, "query - ", sql);
        conn = KnGGConnection.getDBConnection();
        pStmt = conn.prepareStatement(sql);
        pStmt.setString(1, crntFeatureBit);
        pStmt.setString(2, oldFeatureBit);
        pStmt.setString(3, updatTS);
        pStmt.setInt(4, status);
        int count = pStmt.executeUpdate();
        knLogger.debug(methodName, "Query executed successfully " + sql, count);
    }

    public void update(int status) throws SQLException {
        String methodName = "insert(String,String, int)";
        knLogger.info(methodName, "ENTRY");
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String updatTS = String.valueOf(Instant.now().toEpochMilli());
        String sql = "UPDATE " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.FEATUREBIT_ROLLBACK_INFO.value() +
                " SET REQ_STATUS =? WHERE REQ_STATUS=1";
        knLogger.debug(methodName, "query - ", sql);
        conn = KnGGConnection.getDBConnection();
        pStmt = conn.prepareStatement(sql);
        pStmt.setInt(1, status);
        int count = pStmt.executeUpdate();
        knLogger.debug(methodName, "Query executed successfully " + sql, count);
    }

    public void delete() throws SQLException {
        String methodName = "delete()";
        knLogger.info(methodName, "ENTRY");
        Connection conn = null;
        PreparedStatement pStmt = null;
        String sql = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.FEATUREBIT_ROLLBACK_INFO.value();
        knLogger.debug(methodName, "query - ", sql);
        conn = KnGGConnection.getDBConnection();
        pStmt = conn.prepareStatement(sql);
        int count = pStmt.executeUpdate();
        knLogger.debug(methodName, "Query executed successfully ", count);
    }
}
