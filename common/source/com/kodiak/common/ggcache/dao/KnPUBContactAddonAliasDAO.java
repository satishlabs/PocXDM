/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;

import com.kodiak.common.commdto.request.KnPUBContactAddonAliasDTO;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.logger.KnLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class KnPUBContactAddonAliasDAO {

    /**
     * ************************************************************************
     * <p>
     * File name:  KnPUBContactAddonAliasDAO.java
     * Subsystem:  XCAP-XDM
     * <p>
     * Name                         Date                     Release
     * --------------------     ----------------        ------------------
     * Shashank Tewari             March 13, 2019                10.0
     * <p>
     * <p>
     * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
     * Greenheart Phase IV, Nagawara
     * Bangalore - 560 045
     * www.kodiakptt.com
     * All Rights Reserved.
     * <p>
     * This software is the confidential and proprietary information of Kodiak
     * Networks, Inc. You shall not disclose such confidential information and
     * shall use it only in accordance with the terms of the license agreement
     * you entered into with Kodiak Networks.
     * ************************************************************************
     */

    private static final KnLogger knLogger = KnLogger.getLogger(KnPUBContactAddonAliasDAO.class);

    /**
     * @param contactAddonAliasMapDTO
     * @throws SQLException
     */
    public void insertPUBContactAddonAliasInfo(HashMap<String, ArrayList<KnPUBContactAddonAliasDTO>> contactAddonAliasMapDTO) throws SQLException {
        String methodName = "insertPUBContactAddonAliasInfo(HashMap<String, ArrayList<KnPUBContactAddonAliasDTO>>)";
        knLogger.info(methodName, "input -: ", contactAddonAliasMapDTO.size());
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            StringBuilder insertQry = new StringBuilder();
            insertQry.append("INSERT INTO ").append(KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.PUB_CONTACT_ADDONALIASIDS.value());
            insertQry.append(" (").append("OWNERMDN").append(",").append("CONTACTMDN").append(",").append("CONTACTTYPE").append(",");
            insertQry.append("CONTACTKEY").append(",").append("CONTACTVALUE");
            insertQry.append(")").append(" VALUES (?,?,?,?,?)");

            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(insertQry.toString());
            ArrayList<KnPUBContactAddonAliasDTO> addonAliasDTOS = (ArrayList<KnPUBContactAddonAliasDTO>) contactAddonAliasMapDTO.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
            knLogger.debug(methodName, "Printing addonAliasDTOS = ", addonAliasDTOS);
            for (KnPUBContactAddonAliasDTO addonAliasDTO : addonAliasDTOS) {
                pStmt.setString(1, addonAliasDTO.getOwnerMDN());
                pStmt.setString(2, addonAliasDTO.getContactMDN());
                pStmt.setInt(3, addonAliasDTO.getContactType());
                pStmt.setString(4, addonAliasDTO.getKey());
                pStmt.setString(5, addonAliasDTO.getValue());
                pStmt.addBatch();
            }
            int[] count = pStmt.executeBatch();
            knLogger.info(methodName, "Query: Executed Insertion count:", count);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }


    public ArrayList<KnPUBContactAddonAliasDTO> getPUBContactAddonAliasInfo(KnPUBContactAddonAliasDTO contactAddonAliasReqDTO) throws SQLException {
        String methodName = "getPUBContactAddonAliasInfo(KnPUBContactAddonAliasDTO)";
        knLogger.info(methodName, "Entry with input-: ", contactAddonAliasReqDTO);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        ArrayList<KnPUBContactAddonAliasDTO> contactAddonAliasDTOS = new ArrayList<>();
        try {
            String sql = "SELECT OWNERMDN, CONTACTMDN, CONTACTTYPE, CONTACTKEY, CONTACTVALUE FROM " + KnGGCacheConstants.DG_SCHEMA +
                    KnGGCacheConstants.GG_CACHE_NAME.PUB_CONTACT_ADDONALIASIDS.value() +
                    " WHERE OWNERMDN= ? " +
                    ";";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, contactAddonAliasReqDTO.getOwnerMDN());
            knLogger.debug(methodName, "Query: Executing - ", sql);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnPUBContactAddonAliasDTO contactAddonAliasDTO = new KnPUBContactAddonAliasDTO();
                contactAddonAliasDTO.setOwnerMDN(rs.getString("OWNERMDN"));
                contactAddonAliasDTO.setContactMDN(rs.getString("CONTACTMDN"));
                contactAddonAliasDTO.setContactType(rs.getInt("CONTACTTYPE"));
                contactAddonAliasDTO.setKey(rs.getString("CONTACTKEY"));
                contactAddonAliasDTO.setValue(rs.getString("CONTACTVALUE"));
                contactAddonAliasDTOS.add(contactAddonAliasDTO);
            }
            knLogger.info(methodName, "contactAddonAliasDTOS are :", contactAddonAliasDTOS);
            return contactAddonAliasDTOS;
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeConnection(conn);
        }
    }


    public void deletePUBContactAddonAliasInfo(ArrayList<KnPUBContactAddonAliasDTO> contactAddonAliasReqDTOs) throws SQLException {
        String methodName = "deletePUBContactAddonAliasInfo(ArrayList<KnPUBContactAddonAliasDTO>)";
        knLogger.info(methodName, "Entry with input-: ", contactAddonAliasReqDTOs);
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pStmt = null;
        int rowDeleted = 0;
        try {
            String sql = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA +
                    KnGGCacheConstants.GG_CACHE_NAME.PUB_CONTACT_ADDONALIASIDS.value() + " WHERE OWNERMDN = ? " +
                    " AND CONTACTMDN = ? " +
                    ";";
            knLogger.info(methodName, "query : ", sql);
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            for (KnPUBContactAddonAliasDTO contactAddonAliasReqDTO : contactAddonAliasReqDTOs) {
                pStmt.setString(1, contactAddonAliasReqDTO.getOwnerMDN());
                pStmt.setString(2, contactAddonAliasReqDTO.getContactMDN());
                pStmt.addBatch();
            }
            int[] count = pStmt.executeBatch();
            knLogger.info(methodName, "Query: Executed Insertion count:", count);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeConnection(conn);
        }
        knLogger.info(methodName, "Exit -: ");
    }

}
