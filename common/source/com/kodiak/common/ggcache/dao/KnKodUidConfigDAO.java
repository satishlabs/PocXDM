/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * 
 */
package com.kodiak.common.ggcache.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.logger.KnLogger;

/**
 * @author mamatha.rr
 *
 */
public class KnKodUidConfigDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnOidcClientIdSecretDAO.class);

	public String retrieveKUIDPrefix() throws SQLException {
		String methodName = "retrieveKUIDPrefix()";
		String kuidPrefix = null;
		ResultSet rs = null;
		Connection conn = null;
		knLogger.info(methodName, "ENTRY: Get KUID Prefix ");
		try {
			conn = KnGGConnection.getDBConnection();
			String sql = "SELECT KODIAK_POC_PREFIX FROM " + KnGGCacheConstants.DG_SCHEMA
					+ KnGGCacheConstants.GG_CACHE_NAME.KOD_UID_CONFIG.value();
			rs = conn.createStatement().executeQuery(sql);
			if (rs.next()) {
				kuidPrefix = rs.getString("KODIAK_POC_PREFIX").trim();
			}
			knLogger.debug(methodName, "KUID prefix - ", kuidPrefix);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeConnection(conn);
		}

		return kuidPrefix;
	}
}
