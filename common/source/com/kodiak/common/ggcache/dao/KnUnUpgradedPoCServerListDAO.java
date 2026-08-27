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
import java.sql.ResultSet;
import java.sql.SQLException;

public class KnUnUpgradedPoCServerListDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUnUpgradedPoCServerListDAO.class);

    public Integer isUnUpgradedPOCSERVER(String pttServerId) throws SQLException {
        String methodName = "isUnUpgradedPOCSERVER(String)";
        Connection conn = null;
        ResultSet rs = null;
        knLogger.debug(methodName, "pttServerId", pttServerId);
        int count=0;
        try {
            String sql = "SELECT COUNT(*) as pocCount FROM " +
                    KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.UNUPGRADEDPOCSERVERLISTCACHE.value() +
                    " WHERE POCPTTSERVERID = '" + pttServerId + "';";
            conn = KnGGConnection.getDBConnection();
            rs = conn.createStatement().executeQuery(sql);
            if (rs.next()) {
                count = Integer.parseInt(rs.getString("pocCount"));
            }
            knLogger.info(methodName, "count of values: ", count);
            return count;
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeConnection(conn);
        }
    }
}
