/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.common.ggcache.dto.KnDefaultMCSClientInfo;
import com.kodiak.logger.KnLogger;

public class KnMCSClientInfoDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMCSClientInfoDAO.class);

	public KnDefaultMCSClientInfo retrieveDefaultMCSClientInfo() throws SQLException {
		String methodName = "retrieveDefaultMCSClientInfo()";
		KnDefaultMCSClientInfo defaultMCSClientInfo = null;
		ResultSet rs = null;
		Connection conn = null;
		knLogger.info(methodName, "ENTRY: Get defaultMCSClientInfo ");
		try {
			conn = KnGGConnection.getDBConnection();
			String sql = "SELECT PV, CONVERT (CLIENTFS2 , varchar) as CLIENTFS2 FROM " + KnGGCacheConstants.DG_SCHEMA
					+ KnGGCacheConstants.GG_CACHE_NAME.MCS_CLIENT_INFO.value()+" WHERE DEFAULT=1";
			rs = conn.createStatement().executeQuery(sql);
			if (rs.next()) {
				defaultMCSClientInfo= new KnDefaultMCSClientInfo();
				defaultMCSClientInfo.setPv(rs.getInt("PV"));
				defaultMCSClientInfo.setClientFS2(rs.getString("CLIENTFS2"));				
			}
			knLogger.debug(methodName, "defaultMCSClientInfo - ", defaultMCSClientInfo);
			if(defaultMCSClientInfo == null) {
				
			}
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeConnection(conn);
		}

		return defaultMCSClientInfo;
	}

	public Map<Integer,KnDefaultMCSClientInfo> retrieveMCSClientInfo() throws SQLException {
		String methodName = "retrieveMCSClientInfo()";
		KnDefaultMCSClientInfo mcsClientInfo = null;
		ResultSet rs = null;
		Connection conn = null;
		Map<Integer,KnDefaultMCSClientInfo> mcsClientInfoMap = new HashMap<>();;
		knLogger.info(methodName, "ENTRY: Get retrieveMCSClientInfo ");
		try {
			conn = KnGGConnection.getDBConnection();
			String sql = "SELECT PV, CONVERT (CLIENTFS2 , varchar) as CLIENTFS2, DEFAULT FROM " + KnGGCacheConstants.DG_SCHEMA
					+ KnGGCacheConstants.GG_CACHE_NAME.MCS_CLIENT_INFO.value();
			rs = conn.createStatement().executeQuery(sql);
			while (rs.next()) {
				mcsClientInfo = new KnDefaultMCSClientInfo();
				mcsClientInfo.setPv(rs.getInt("PV"));
				mcsClientInfo.setClientFS2(rs.getString("CLIENTFS2"));
				mcsClientInfo.setIsDefault(rs.getInt("DEFAULT"));
				mcsClientInfoMap.put(rs.getInt("PV"),mcsClientInfo);
			}
			knLogger.debug(methodName, "mcsClientInfoMap - ", mcsClientInfoMap.values());
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeConnection(conn);
		}

		return mcsClientInfoMap;
	}

	
}
