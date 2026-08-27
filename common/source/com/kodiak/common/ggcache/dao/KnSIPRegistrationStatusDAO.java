/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
//import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.common.resources.KnGeneralUtil;

import static com.kodiak.common.ggcache.KnGGCacheConstants.ONLINE;

public class KnSIPRegistrationStatusDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSIPRegistrationStatusDAO.class);

	public static final String MDNLIST = "MDNLIST";
	public static final String MDN = "MDN";

    public  String getRegisterPOCHomeByDeviceIMPI(String deviceIMPI) throws SQLException{
		String methodName = "getRegisterPOCHomeByDeviceIMPI(String deviceIMPI)";
		knLogger.info(methodName, "Entry", deviceIMPI);
		String registeredHome = null;
		Connection conn = null;
		 PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			if (deviceIMPI != null) {
				conn = KnGGConnection.getDBConnection();
				String sql = "SELECT REGISTEREDHOME FROM "
						+ KnGGCacheConstants.GG_CACHE_NAME.SIPREGISTRATIONSTATUS.value() + " WHERE DEVICE_IMPI = ?";
				knLogger.info(methodName, "query", sql);
				pStmt = conn.prepareStatement(sql);
				pStmt.setBytes(1, deviceIMPI.getBytes(StandardCharsets.UTF_8));
				rs = pStmt.executeQuery();
				if (rs.next()) {
					registeredHome = rs.getString("REGISTEREDHOME");
				}
			}
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeStatement(pStmt);
			KnDbUtil.closeConnection(conn);
		}
		knLogger.info(methodName, "registeredHome   ", registeredHome);
		return registeredHome;
	}

	public Map<String,String> getOnlineSubcribersListByMdn(List<String> mdnList) throws SQLException{
		String methodName = "Dao.getOnlineSubcribersListByMdn()";
		knLogger.info(methodName, "Entry mdnList:", KnGDPRTemplate.mdnList(mdnList));
		Map<String,String> onlineSubscriber = new HashMap<>();
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		int index = 2;
		try {
			if (mdnList != null&&!mdnList.isEmpty()) {
				conn = KnGGConnection.getDBConnection();
				StringBuilder query=new StringBuilder();
				query.append(" SELECT MDN ,BASEMDN FROM ");
				query.append(KnGGCacheConstants.GG_CACHE_NAME.SIPREGISTRATIONSTATUS.value());
				query.append(" WHERE ONLINESTATUS=? AND ");//ONLINESTATUS 0 - Offline,1 - Online,2 – TU (Temporary Unreachable)
				query.append(" MDN IN( ");
				query.append(MDNLIST);
				query.append(" ) ");
				//final String finalQuery=KnGeneralUtil.replaceContactWithValue(query.toString(), MDNLIST, KnGeneralUtil.formCommaSeperatedIdList(mdnList));
				final String finalQuery = KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query.toString(),MDNLIST);
				knLogger.info(methodName, "query :", finalQuery);
				pStmt = conn.prepareStatement(finalQuery);
				pStmt.setInt(1, ONLINE);
				for(String mdn : mdnList)
					pStmt.setString(index++, mdn);
				rs = pStmt.executeQuery();
				while (rs.next()) {
					onlineSubscriber.put(rs.getString("MDN"),rs.getString("BASEMDN"));
				}
			}
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeStatement(pStmt);
			KnDbUtil.closeConnection(conn);
		}
		knLogger.info(methodName, "onlineSubscriber:   ", onlineSubscriber);
		return onlineSubscriber;
	}

	public Map<String,Integer> getActiveMdn(String baseMdn) throws SQLException {
		String methodName = "getActiveMdn(String)";
		knLogger.debug(methodName, "ENTRY: ", KnGDPRTemplate.mdn(baseMdn));
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		Map<String,Integer> activeMdn=new HashMap<>();
		try {
			String sql = "SELECT MDN FROM "  + KnGGCacheConstants.GG_CACHE_NAME.SIPREGISTRATIONSTATUS.value() +
					" WHERE BASEMDN = ? AND ONLINESTATUS =1 ";
			conn = KnGGConnection.getDBConnection();
			pStmt = conn.prepareStatement(sql);
			pStmt.setString(1,baseMdn.trim());
			rs = pStmt.executeQuery();
			while (rs.next()) {
				if(rs.getString(MDN).equals(baseMdn)){
					activeMdn.put(rs.getString(MDN),0); //basemdn
				}else{
					activeMdn.put(rs.getString(MDN),1); //profilemdn
				}
			}
			knLogger.debug(methodName, "EXIT :", activeMdn.size());
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeStatement(pStmt);
			KnDbUtil.closeConnection(conn);
		}
		return activeMdn;
	}
}
