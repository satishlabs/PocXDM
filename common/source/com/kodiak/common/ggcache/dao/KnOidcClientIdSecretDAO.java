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
import java.util.HashMap;
import java.util.Map;

public class KnOidcClientIdSecretDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnOidcClientIdSecretDAO.class);

    public Map<String, String> getOidcClientIDSecret() throws SQLException {
        String methodName = "getOidcClientIDSecret()";
        Connection conn = null;
        ResultSet rs = null;
        Map<String, String> oidcClientIdSecretMap = new HashMap<>();
        try {
            String sql = "SELECT OIDC_CLIENT_ID, OIDC_CLIENT_SECRET FROM " +
                    KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.OIDC_CLIENT_ID_SECRET.value() ;
            conn = KnGGConnection.getDBConnection();
            knLogger.info(methodName, "query : ", sql);
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                oidcClientIdSecretMap.put(rs.getString("OIDC_CLIENT_ID"),
                        rs.getString("OIDC_CLIENT_SECRET"));
            }
            knLogger.info(methodName, "values: ", oidcClientIdSecretMap);
            return oidcClientIdSecretMap;
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeConnection(conn);
        }
    }
}
