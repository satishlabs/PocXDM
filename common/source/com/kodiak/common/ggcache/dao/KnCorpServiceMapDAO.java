/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.common.ggcache.dto.KnCorpServiceInfoDTO;
import com.kodiak.logger.KnLogger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * *****************************************************************************
 * File name:   KnCorpServiceMapDAO
 * Subsystem:   PoCXDM
 * Description: DTO to store Corp-ID to ServiceType/ServiceVersion mapping
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Saurabh Kumar           16/05/18        9.0
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

public class KnCorpServiceMapDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpServiceMapDAO.class);

    public Map<String, KnCorpServiceInfoDTO> getCorpServiceType(int corpId) throws SQLException {
        String methodName = "getCorpServiceType(int)";
        Connection conn = null;
        ResultSet rs = null;
        knLogger.debug(methodName, "corpId: ", corpId);
        Map<String, KnCorpServiceInfoDTO> corpServiceInfoDTOMap = new HashMap<>();
        KnCorpServiceInfoDTO corpServiceInfoDTO = null;
        try {
            String sql = "SELECT CORPID, SERVICETYPE, SERVICEGRPID FROM " +
                    KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORPSERVICESMAPCACHE.value() +
                    " WHERE CORPID = "+ corpId + ";";
            conn = KnGGConnection.getDBConnection();
            rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                corpServiceInfoDTO = new KnCorpServiceInfoDTO();
                corpServiceInfoDTO.setCorpId(rs.getInt("CORPID"));
                corpServiceInfoDTO.setServiceType(rs.getString("SERVICETYPE"));
                corpServiceInfoDTO.setServiceGrpId(rs.getInt("SERVICEGRPID"));
                corpServiceInfoDTOMap.put(rs.getString("SERVICETYPE"), corpServiceInfoDTO);
            }
            knLogger.info(methodName, "DB values values: ", corpServiceInfoDTOMap);
            return corpServiceInfoDTOMap;
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeConnection(conn);
        }
    }
}
