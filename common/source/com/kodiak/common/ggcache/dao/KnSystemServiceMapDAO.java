/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;

/**
 * *****************************************************************************
 * File name:   KnSystemServiceMapDAO
 * Subsystem:   PoCXDM
 * Description: DTO to store Corp-ID to ServiceType/ServiceVersion mapping
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Saurabh Kumar           18/06/18        9.0
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

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.common.ggcache.dto.KnServiceGroupInfoDTO;
import com.kodiak.logger.KnLogger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class KnSystemServiceMapDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSystemServiceMapDAO.class);

    public Map<String, KnServiceGroupInfoDTO> getSystemServiceMapByGrpId(int serviceGrpId) throws SQLException {
        String methodName = "getSystemServiceMapByGrpId(int)";
        Connection conn = null;
        ResultSet rs = null;
        knLogger.debug(methodName, "serviceGrpId", serviceGrpId);
        Map<String, KnServiceGroupInfoDTO> serviceGroupInfoDTOMap = new HashMap<>();
        KnServiceGroupInfoDTO serviceGroupInfoDTO = null;
        try {
            String sql = "SELECT SERVICEGRPID, SERVICETYPE, SERVICEVER, IS_DEFAULT FROM " +
                    KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.SERVICEGROUPINFOCACHE.value() +
                    " WHERE SERVICEGRPID = "+ serviceGrpId + ";";
            conn = KnGGConnection.getDBConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                serviceGroupInfoDTO = new KnServiceGroupInfoDTO();
                serviceGroupInfoDTO.setServiceGrpId(rs.getInt("SERVICEGRPID"));
                serviceGroupInfoDTO.setServiceType(rs.getString("SERVICETYPE"));
                serviceGroupInfoDTO.setServiceVer(rs.getString("SERVICEVER"));
                serviceGroupInfoDTO.setIsDefault(rs.getInt("IS_DEFAULT"));
                serviceGroupInfoDTOMap.put(rs.getString("SERVICETYPE"), serviceGroupInfoDTO);
            }
            knLogger.info(methodName, "values: ", serviceGroupInfoDTOMap);
            return serviceGroupInfoDTOMap;
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeConnection(conn);
        }
    }

    public Map<String, KnServiceGroupInfoDTO> getSystemServiceMapByType(String serviceType) throws SQLException {
        String methodName = "getSystemServiceMapByType(String)";
        Connection conn = null;
        ResultSet rs = null;
        knLogger.debug(methodName, "serviceGrpId", serviceType);
        Map<String, KnServiceGroupInfoDTO> serviceGroupInfoDTOMap = new HashMap<>();
        KnServiceGroupInfoDTO serviceGroupInfoDTO = null;
        try {
            String sql = "SELECT SERVICEGRPID, SERVICETYPE, SERVICEVER, IS_DEFAULT FROM " +
                    KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.SERVICEGROUPINFOCACHE.value() +
                    " WHERE SERVICETYPE = '"+ serviceType + "' AND IS_DEFAULT = 1;" ;
            conn = KnGGConnection.getDBConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                serviceGroupInfoDTO = new KnServiceGroupInfoDTO();
                serviceGroupInfoDTO.setServiceGrpId(rs.getInt("SERVICEGRPID"));
                serviceGroupInfoDTO.setServiceType(rs.getString("SERVICETYPE"));
                serviceGroupInfoDTO.setServiceVer(rs.getString("SERVICEVER"));
                serviceGroupInfoDTO.setIsDefault(rs.getInt("IS_DEFAULT"));
                serviceGroupInfoDTOMap.put(rs.getString("SERVICETYPE"), serviceGroupInfoDTO);
            }
            knLogger.info(methodName, "values: ", serviceGroupInfoDTOMap);
            return serviceGroupInfoDTOMap;
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeConnection(conn);
        }
    }
}
