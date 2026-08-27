/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;


import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.common.resources.util.KnSnowflakeIdGenerator;
import com.kodiak.logger.KnLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the DAO class which fetches TrustMatrixInfo for the given extCorpId
 */
public class KnCorpSharedTrustMatrixDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpSharedTrustMatrixDAO.class);

    private static final String CORPIDS = "CORPIDS";

    public List<KnCorpTrustMatrixDTO> getSharedCorpMatrix(String extCorpId) throws SQLException {
        String methodName = "getSharedCorpMatrix(int)";
        knLogger.debug(methodName, "ENTRY:- ", extCorpId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<KnCorpTrustMatrixDTO> trustMatrixDTOS = new ArrayList<>();
        try {
            String sql = "SELECT EXTCORPORATEID,SHARED_EXTCORPID,MEM_FEATURES_ALLOWED,SHARING_FEATURE_ALLOWED,REC_ID FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORP_SHARING_TRUST_MATRIX.value() +
                    " WHERE EXTCORPORATEID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1,extCorpId.trim());
            knLogger.debug(methodName, "executing query - ", sql,"extCorpId",extCorpId.trim());
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnCorpTrustMatrixDTO dto = new KnCorpTrustMatrixDTO();
                dto.setExtCorpId(rs.getString("EXTCORPORATEID"));
                dto.setSharedExtCorpId(rs.getString("SHARED_EXTCORPID"));
                dto.setMemFeaturesAllowed(rs.getLong("MEM_FEATURES_ALLOWED"));
                dto.setRecId(rs.getString("REC_ID"));
                Long sharingFeatureAllowed = (Long) rs.getObject("SHARING_FEATURE_ALLOWED");
                if(sharingFeatureAllowed == null){
                    dto.setSharingFeatureAllowed(1);
                } else {
                    dto.setSharingFeatureAllowed(rs.getLong("SHARING_FEATURE_ALLOWED"));
                }

                if(sharingFeatureAllowed == null || sharingFeatureAllowed == 0 ||sharingFeatureAllowed == 1){
                    dto.setTypeOfSharingFeatureAllowed(OFSHARINGTYPE.GROUPSHARING.value());
                } else if (sharingFeatureAllowed == 2){
                    dto.setTypeOfSharingFeatureAllowed(OFSHARINGTYPE.USERPROFILESHARING.value());
                }else if (sharingFeatureAllowed == 3){
                    dto.setTypeOfSharingFeatureAllowed(OFSHARINGTYPE.GROUPANDUSERPROFILESHARING.value());
                }

                trustMatrixDTOS.add(dto);
            }
            knLogger.debug(methodName, "EXIT -  :", trustMatrixDTOS);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }

        return trustMatrixDTOS;
    }

    /**
     * Fetch TrustMatrix data by sharedExtCorpid
     * @param sharedExtCorpId
     * @return
     * @throws SQLException
     */
    public List<KnCorpTrustMatrixDTO> getSharedCorpMatrixBySharedCorpId(String sharedExtCorpId) throws SQLException {
        String methodName = "getSharedCorpMatrixBySharedCorpId(String)";
        knLogger.debug(methodName, "ENTRY:- ", sharedExtCorpId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<KnCorpTrustMatrixDTO> trustMatrixDTOS = new ArrayList<>();
        try {
            String sql = "SELECT EXTCORPORATEID,SHARED_EXTCORPID,MEM_FEATURES_ALLOWED,REC_ID FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORP_SHARING_TRUST_MATRIX.value() +
                    " WHERE SHARED_EXTCORPID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1,sharedExtCorpId.trim());
            knLogger.debug(methodName, "executing query - ", sql,"sharedExtCorpId",sharedExtCorpId.trim());
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnCorpTrustMatrixDTO dto = new KnCorpTrustMatrixDTO();
                dto.setExtCorpId(rs.getString("EXTCORPORATEID"));
                dto.setSharedExtCorpId(rs.getString("SHARED_EXTCORPID"));
                dto.setMemFeaturesAllowed(rs.getLong("MEM_FEATURES_ALLOWED"));
                dto.setRecId(rs.getString("REC_ID"));
                trustMatrixDTOS.add(dto);
            }
            knLogger.debug(methodName, "EXIT -  :", trustMatrixDTOS);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }

        return trustMatrixDTOS;
    }

    public void deleteCorpMatrixByOwnedCorpId(String ownedExtCorpIds) throws SQLException {
        String methodName = "deleteCorpMatrixByOwnedCorpId()";
        knLogger.debug(methodName, "ENTRY:- ", ownedExtCorpIds);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
                conn = KnGGConnection.getDBConnection();
                StringBuilder query=new StringBuilder();
                query.append(" DELETE FROM  ");
                query.append(KnGGCacheConstants.DG_SCHEMA );
                query.append(KnGGCacheConstants.GG_CACHE_NAME.CORP_SHARING_TRUST_MATRIX.value());
                query.append(" WHERE EXTCORPORATEID=? ");
                query.append(" OR SHARED_EXTCORPID=? ");
                knLogger.debug(methodName, "executing query - ", query);
                pStmt = conn.prepareStatement(query.toString());
                pStmt.setString(1,ownedExtCorpIds);
                pStmt.setString(2,ownedExtCorpIds);
                int deletedRecord = pStmt.executeUpdate();
                knLogger.debug(methodName, "EXIT -  deletedRecord:", deletedRecord);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    public List<KnCorpTrustMatrixDTO> getSharedCorpMatrixByExtAndSharingFeature(String extCorpId) throws SQLException {
        String methodName = "getSharedCorpMatrixByExtAndSharingFeature(String,int)";
        knLogger.debug(methodName, "ENTRY: ", extCorpId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<KnCorpTrustMatrixDTO> trustMatrixDTOS = new ArrayList<>();
        try {
            String sql = "SELECT EXTCORPORATEID,SHARED_EXTCORPID,MEM_FEATURES_ALLOWED,SHARING_FEATURE_ALLOWED,REC_ID FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORP_SHARING_TRUST_MATRIX.value() +
                    " WHERE EXTCORPORATEID = ? AND (SHARING_FEATURE_ALLOWED=2 OR SHARING_FEATURE_ALLOWED=3) ";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1,extCorpId.trim());

            knLogger.debug(methodName, "executing query - ", sql,"extCorpId",extCorpId.trim());
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnCorpTrustMatrixDTO dto = new KnCorpTrustMatrixDTO();
                dto.setExtCorpId(rs.getString("EXTCORPORATEID"));
                dto.setSharedExtCorpId(rs.getString("SHARED_EXTCORPID"));
                dto.setMemFeaturesAllowed(rs.getLong("MEM_FEATURES_ALLOWED"));
                dto.setRecId(rs.getString("REC_ID"));
                Long sharingFeatureAllowed = (Long) rs.getObject("SHARING_FEATURE_ALLOWED");
                if(sharingFeatureAllowed == null){
                    dto.setSharingFeatureAllowed(1);
                } else {
                    dto.setSharingFeatureAllowed(rs.getLong("SHARING_FEATURE_ALLOWED"));
                }
                trustMatrixDTOS.add(dto);
            }
            knLogger.debug(methodName, "EXIT :", trustMatrixDTOS);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }

        return trustMatrixDTOS;
    }

    // ── Write methods ──────────────────────────────────────────────────────────

    /**
     * Inserts a new parent trust matrix row with a freshly generated REC_ID.
     * Returns the generated recId so callers can insert child hierarchy rows.
     */
    public String insertWithRecId(String extCorpId, String sharedExtCorpId, long memFeaturesAllowed, long sharingFeatureAllowed) throws SQLException {
        final String methodName = "insertWithRecId()";
        knLogger.debug(methodName, "ENTRY extCorpId=", extCorpId, " sharedExtCorpId=", sharedExtCorpId);
        String recId = String.valueOf(KnSnowflakeIdGenerator.nextId());
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "INSERT INTO " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORP_SHARING_TRUST_MATRIX.value() +
                    " (EXTCORPORATEID, SHARED_EXTCORPID, MEM_FEATURES_ALLOWED, SHARING_FEATURE_ALLOWED, REC_ID)" +
                    " VALUES (?, ?, ?, ?, ?)";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, extCorpId);
            pStmt.setString(2, sharedExtCorpId);
            pStmt.setLong(3, memFeaturesAllowed);
            pStmt.setLong(4, sharingFeatureAllowed);
            pStmt.setString(5, recId);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "EXIT recId=", recId);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return recId;
    }

    /**
     * Assigns a new REC_ID to an existing legacy row (where REC_ID IS NULL).
     * Returns the newly assigned recId.
     */
    public String assignRecId(String extCorpId, String sharedExtCorpId) throws SQLException {
        final String methodName = "assignRecId()";
        knLogger.debug(methodName, "ENTRY extCorpId=", extCorpId, " sharedExtCorpId=", sharedExtCorpId);
        String recId = String.valueOf(KnSnowflakeIdGenerator.nextId());
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "UPDATE " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORP_SHARING_TRUST_MATRIX.value() +
                    " SET REC_ID = ? WHERE EXTCORPORATEID = ? AND SHARED_EXTCORPID = ? AND REC_ID IS NULL";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, recId);
            pStmt.setString(2, extCorpId);
            pStmt.setString(3, sharedExtCorpId);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "EXIT recId=", recId);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return recId;
    }

    /**
     * Updates MEM_FEATURES_ALLOWED and SHARING_FEATURE_ALLOWED for a row identified by REC_ID.
     */
    public void updateParentRow(String recId, long memFeaturesAllowed, long sharingFeatureAllowed) throws SQLException {
        final String methodName = "updateParentRow()";
        knLogger.debug(methodName, "ENTRY recId=", recId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "UPDATE " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORP_SHARING_TRUST_MATRIX.value() +
                    " SET MEM_FEATURES_ALLOWED = ?, SHARING_FEATURE_ALLOWED = ? WHERE REC_ID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setLong(1, memFeaturesAllowed);
            pStmt.setLong(2, sharingFeatureAllowed);
            pStmt.setString(3, recId);
            int updated = pStmt.executeUpdate();
            knLogger.debug(methodName, "EXIT updated=", updated);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    /**
     * Deletes the parent row identified by REC_ID.
     * Caller must delete child rows in NEW_SHARED_TRUST_MATRIX_HIERARCHY first.
     */
    public void deleteByRecId(String recId) throws SQLException {
        final String methodName = "deleteByRecId()";
        knLogger.debug(methodName, "ENTRY recId=", recId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORP_SHARING_TRUST_MATRIX.value() +
                    " WHERE REC_ID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, recId);
            int deleted = pStmt.executeUpdate();
            knLogger.debug(methodName, "EXIT deleted=", deleted);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    /**
     * Returns true if a parent row exists for the given corp pair.
     */
    public boolean exists(String extCorpId, String sharedExtCorpId) throws SQLException {
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT 1 FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORP_SHARING_TRUST_MATRIX.value() +
                    " WHERE EXTCORPORATEID = ? AND SHARED_EXTCORPID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, extCorpId);
            pStmt.setString(2, sharedExtCorpId);
            rs = pStmt.executeQuery();
            return rs.next();
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    /**
     * Returns true if a parent row exists for the given REC_ID.
     */
    public boolean existsByRecId(String recId) throws SQLException {
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT 1 FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORP_SHARING_TRUST_MATRIX.value() +
                    " WHERE REC_ID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, recId);
            rs = pStmt.executeQuery();
            return rs.next();
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    /**
     * Updates MEM_FEATURES_ALLOWED and SHARING_FEATURE_ALLOWED by corp-pair.
     * Used for legacy rows (REC_ID IS NULL) when no hierarchyMap is present (TC-XDM-UTM-030).
     * Does NOT touch REC_ID.
     */
    public void updateByCorpPair(String extCorpId, String sharedExtCorpId, long memFeaturesAllowed, long sharingFeatureAllowed) throws SQLException {
        final String methodName = "updateByCorpPair()";
        knLogger.debug(methodName, "ENTRY extCorpId=", extCorpId, " sharedExtCorpId=", sharedExtCorpId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "UPDATE " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORP_SHARING_TRUST_MATRIX.value() +
                    " SET MEM_FEATURES_ALLOWED = ?, SHARING_FEATURE_ALLOWED = ?" +
                    " WHERE EXTCORPORATEID = ? AND SHARED_EXTCORPID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setLong(1, memFeaturesAllowed);
            pStmt.setLong(2, sharingFeatureAllowed);
            pStmt.setString(3, extCorpId);
            pStmt.setString(4, sharedExtCorpId);
            int updated = pStmt.executeUpdate();
            knLogger.debug(methodName, "EXIT updated=", updated);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    public void deleteByCorpPair(String extCorpId, String sharedExtCorpId) throws SQLException {
        String methodName = "deleteByCorpPair(extCorpId, sharedExtCorpId)";
        knLogger.debug(methodName, "ENTRY extCorpId=", extCorpId, " sharedExtCorpId=", sharedExtCorpId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "DELETE FROM " + KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.CORP_SHARING_TRUST_MATRIX.value()
                    + " WHERE EXTCORPORATEID = ? AND SHARED_EXTCORPID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, extCorpId.trim());
            pStmt.setString(2, sharedExtCorpId.trim());
            pStmt.executeUpdate();
            knLogger.debug(methodName, "EXIT");
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    public static enum OFSHARINGTYPE {
        GROUPSHARING(1),
        USERPROFILESHARING(2),
        GROUPANDUSERPROFILESHARING(3);

        int sharingType;

        OFSHARINGTYPE(int eventType) {
            this.sharingType = eventType;
        }

        public int value() {
            return sharingType;
        }

    }
}
