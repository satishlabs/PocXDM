/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.common.ggcache.dto.KnOidcTmpPwdDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * *****************************************************************************
 * File name:   KnSubsAddlInfoDAO
 * Subsystem:   PoCXDM
 * Description:
 * <p/>
 * Name                  Date                   Release
 * -----------------    -----------             -------
 * Saurabh Kumar           Nov 20, 2018          9.0
 * <p/>
 * <p/>
 * Copyright (c) 2018  Kodiak , A Motorola Solutions Company
 * 9th floor, MFar, Manayata Tech Park,
 * Greenheart Phase IV, Nagawara,
 * Bengaluru, Karnataka 560045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak,A Motorola Solutions Company
 * You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak,A Motorola Solutions Company.
 * *******************************************************************************
 */

public class KnSubsAddlInfoDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsAddlInfoDAO.class);

    public void deleteOidcTmpPwd(String mdn) throws SQLException {
        String methodName = "deleteOidcTmpPwd(String)";
        knLogger.debug(methodName, "input MDN -: ", KnGDPRTemplate.mdn(mdn));
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
        	String sql = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.SUBSCRIBERADDLINFOCACHE.value() +
                    " WHERE MDN = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, mdn);
            int x = pStmt.executeUpdate();
        } finally {
            KnDbUtil.closeConnection(conn);
        }
    }

    public void insertOidcTmpPwd(KnOidcTmpPwdDTO oidcTmpPwdDTO) throws SQLException {
        String methodName = "insertOidcTmpPwd(String)";
        knLogger.debug(methodName, "input oidcTmpPwdDTO -: ", oidcTmpPwdDTO);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
        	String sql = "INSERT INTO " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.SUBSCRIBERADDLINFOCACHE.value() +
                  " VALUES(?,?,?,?);";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, oidcTmpPwdDTO.getMdn());
            pStmt.setString(2, oidcTmpPwdDTO.getTmpPwd());
            pStmt.setString(3, oidcTmpPwdDTO.getTmpPwdExpiry());
            pStmt.setString(4, oidcTmpPwdDTO.getTmpPwdCreationTS());
            pStmt.executeUpdate();
        } finally {
            KnDbUtil.closeConnection(conn);
            KnDbUtil.closeStatement(pStmt);
        }
    }

    public KnOidcTmpPwdDTO selectOidcTmpPwd(String mdn) throws SQLException {
        String methodName = "selectOidcTmpPwd()";
        Connection conn = null;
        ResultSet rs = null;
        KnOidcTmpPwdDTO oidcTmpPwdDTO = null;
        try {
            String sql = "SELECT TEMPPASSWORD, TEMPPASSWORDEXPIRY FROM " +
                    KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.SUBSCRIBERADDLINFOCACHE.value() +
                    " WHERE MDN = '" + mdn + "';";
            conn = KnGGConnection.getDBConnection();
            knLogger.info(methodName, "query : ", sql);
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                oidcTmpPwdDTO = new KnOidcTmpPwdDTO();
                oidcTmpPwdDTO.setTmpPwd(rs.getString("TEMPPASSWORD"));
                oidcTmpPwdDTO.setTmpPwdExpiry(rs.getString("TEMPPASSWORDEXPIRY"));
            }
            knLogger.info(methodName, "values: ", oidcTmpPwdDTO);
            return oidcTmpPwdDTO;
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeConnection(conn);
        }
    }

    public List<String> retriveExpPasswordMDNs(Long time) throws SQLException {
        String methodName = "retriveExpPasswordMDNs(Long)";
        Connection conn = null;
        ResultSet rs = null;
        List<String> mdns = new ArrayList<>();
        try {
            String sql = "SELECT MDN FROM " +
                    KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.SUBSCRIBERADDLINFOCACHE.value() +
                    " WHERE CONVERT (TEMPPASSWORDEXPIRY, LONG) < " + time + ";";

            conn = KnGGConnection.getDBConnection();
            knLogger.info(methodName, "query : ", sql);
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                mdns.add(rs.getString("MDN"));

            }
            knLogger.info(methodName, "mdns: ", KnGDPRTemplate.mdnList(mdns));
            return mdns;
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeConnection(conn);
        }
    }

    public int deleteExpPasswordMDNs(List<String> mdns) throws SQLException {
        String methodName = "deleteExpPasswordMDNs(List<String>)";
        Connection conn = null;
        ResultSet rs = null;
        String creteria = "";
        PreparedStatement pStmt = null;
        int rowDeleted = 0;
        int index = 1;
        if (mdns != null && !mdns.isEmpty()) {
            for (String mdn : mdns) {
            	creteria = creteria + "'" + mdn + "'" + ",";
            }
            creteria = creteria.substring(0, creteria.length() - 1);
            try {
                String sql = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA +
                        KnGGCacheConstants.GG_CACHE_NAME.SUBSCRIBERADDLINFOCACHE.value() + " WHERE MDN IN (QUESMARK)";
                sql = KnDbUtil.formCommaSeperatedQuesMarks(mdns, sql,"QUESMARK");
                knLogger.info(methodName, "query : ", sql);
                conn = KnGGConnection.getDBConnection();
                pStmt = conn.prepareStatement(sql);
                for(String mdn : mdns) {
					pStmt.setString(index++, mdn);
                }
                rowDeleted = pStmt.executeUpdate();
                knLogger.info(methodName, "rowDeleted : ", rowDeleted);

            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeConnection(conn);
            }
        }
        return rowDeleted;
    }

    /**
     * Check if emergency alert is active for the given MDN in DG.SUBSCREMERGSTATEINFO cache.
     * Returns true if EMERGALERTSTATE == 1, false otherwise (including no row found).
     */
    public boolean isClientInEmergency(String mdn) throws SQLException {
        String methodName = "isClientInEmergency(String)";
        Connection conn = null;
        ResultSet rs = null;
        boolean isClientInEmerg = false;
        knLogger.debug(methodName, "mdn: ", KnGDPRTemplate.mdn(mdn));
        try {
            String sql = "SELECT EMERGALERTSTATE FROM "
                    + KnGGCacheConstants.DG_SCHEMA
                    + KnGGCacheConstants.GG_CACHE_NAME.SUBSCREMERGSTATEINFO.value()
                    + " WHERE MDN = '" + mdn.trim() + "';";
            conn = KnGGConnection.getDBConnection();
            rs = conn.createStatement().executeQuery(sql);
            knLogger.info(methodName, "query : ", sql);
            if (rs.next()) {
                int state = rs.getInt("EMERGALERTSTATE");
                if (state == 1)
                    isClientInEmerg = true;

            }
            knLogger.info(methodName, "isClientInEmerg: ", isClientInEmerg);
            return isClientInEmerg;
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeConnection(conn);
        }
    }
}
