/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;


import com.kodiak.common.commdto.common.KnPreActivatedClientInfo;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.logger.KnLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;


public class KnPreActivatedClientInfoDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnPreActivatedClientInfoDAO.class);

    public String getActivatedClientFS2(Integer cameraType,Integer clientType) throws SQLException {
        String methodName = "getActivatedClientFS2(int,int)";
        knLogger.info(methodName, "ENTRY:- ");
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String finalClientFS2 = null;
        ArrayList<KnPreActivatedClientInfo> clientInfoList = new ArrayList<>();
        try {
            String sql = "SELECT CONVERT (CLIENTFS2 , varchar) as CLIENTFS2,DEFAULT FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.PRE_ACTIVATED_CLIENTINFO.value() +
                    " WHERE CAMERATYPE = ? AND CLIENTTYPE = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1,cameraType);
            pStmt.setInt(2,clientType);
            knLogger.debug(methodName, "executing query - ", sql,"cameraType",cameraType,"CLIENTTYPE",clientType);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnPreActivatedClientInfo preActivatedClientInfo = new KnPreActivatedClientInfo();
                String clientFS2 = rs.getString("CLIENTFS2");
                preActivatedClientInfo.setClientFS2(clientFS2);
                preActivatedClientInfo.setDefaultt(rs.getInt("DEFAULT"));
                clientInfoList.add(preActivatedClientInfo);
            }
            knLogger.debug(methodName, " clientInfoList-  :", clientInfoList);
            if(clientInfoList.size() == 1){
                if(clientInfoList.get(0).getDefaultt() == 1){
                    finalClientFS2 = clientInfoList.get(0).getClientFS2();
                }
            }
            else if(clientInfoList.size() > 1){
                Optional<KnPreActivatedClientInfo> clientInfoOptional = Optional.empty();
                for (KnPreActivatedClientInfo info : clientInfoList) {
                    if (info.getDefaultt() == 1) {
                        clientInfoOptional = Optional.of(info);
                        break;
                    }
                }
                if(clientInfoOptional.isPresent()){
                    finalClientFS2 = clientInfoOptional.get().getClientFS2();
                }
            }
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        knLogger.info(methodName, "EXIT with clientFS2-  :", finalClientFS2);
        return finalClientFS2;
    }


}
