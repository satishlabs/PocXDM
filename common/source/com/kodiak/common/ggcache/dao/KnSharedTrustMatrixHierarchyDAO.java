/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache.dao;

import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.ggcache.KnGGCacheConstants;
import com.kodiak.common.ggcache.KnGGConnection;
import com.kodiak.common.ggcache.dto.KnSharedTrustMatrixHierarchyDTO;
import com.kodiak.logger.KnLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for NEW_SHARED_TRUST_MATRIX_HIERARCHY table.
 *
 * Rules:
 *   - This table is ONLY queried when parent row has REC_ID IS NOT NULL.
 *   - insert() uses an upsert pattern — duplicate (recId, ownerHierarchyId, sharedHierarchyId)
 *     is silently skipped (no error).
 *   - MEM_FEATURES_ALLOWED and SHARING_FEATURE_ALLOWED are stored as NULL (inherit from parent).
 */
public class KnSharedTrustMatrixHierarchyDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSharedTrustMatrixHierarchyDAO.class);
    private static final String TABLE = KnGGCacheConstants.DG_SCHEMA + KnGGCacheConstants.GG_CACHE_NAME.NEW_SHARED_TRUST_MATRIX_HIERARCHY.value();

    /**
     * Inserts a single hierarchy mapping row. Duplicate rows are silently skipped (upsert-ignored).
     */
    public void insert(String recId, String ownerHierarchyId, String sharedHierarchyId) throws SQLException {
        final String methodName = "insert()";
        knLogger.debug(methodName, "ENTRY recId=", recId, " ownerHierarchyId=", ownerHierarchyId, " sharedHierarchyId=", sharedHierarchyId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            if (existsRow(recId, ownerHierarchyId, sharedHierarchyId)) {
                knLogger.debug(methodName, "Duplicate row — skipping insert");
                return;
            }
            String sql = "INSERT INTO " + TABLE +
                    " (REC_ID, OWNER_HIERARCHY_ID, SHARED_HIERARCHY_ID, MEM_FEATURES_ALLOWED, SHARING_FEATURE_ALLOWED)" +
                    " VALUES (?, ?, ?, NULL, NULL)";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, recId);
            pStmt.setString(2, ownerHierarchyId);
            pStmt.setString(3, sharedHierarchyId);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "EXIT — row inserted");
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    /**
     * Returns all hierarchy mapping rows for a given recId (all owner/shared combinations).
     * Used by the read path (getSharedCorpTrustMatrix) to populate hierarchyMappings.
     */
    public List<KnSharedTrustMatrixHierarchyDTO> getAllHierarchiesByRecId(String recId) throws SQLException {
        final String methodName = "getAllHierarchiesByRecId()";
        knLogger.debug(methodName, "ENTRY recId=", recId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<KnSharedTrustMatrixHierarchyDTO> result = new ArrayList<>();
        try {
            String sql = "SELECT REC_ID, OWNER_HIERARCHY_ID, SHARED_HIERARCHY_ID," +
                    " MEM_FEATURES_ALLOWED, SHARING_FEATURE_ALLOWED FROM " + TABLE +
                    " WHERE REC_ID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, recId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnSharedTrustMatrixHierarchyDTO dto = new KnSharedTrustMatrixHierarchyDTO();
                dto.setRecId(rs.getString("REC_ID"));
                dto.setOwnerHierarchyId(rs.getString("OWNER_HIERARCHY_ID"));
                dto.setSharedHierarchyId(rs.getString("SHARED_HIERARCHY_ID"));
                long memFeatures = rs.getLong("MEM_FEATURES_ALLOWED");
                if (!rs.wasNull()) dto.setMemFeaturesAllowed(memFeatures);
                long sharingFeature = rs.getLong("SHARING_FEATURE_ALLOWED");
                if (!rs.wasNull()) dto.setSharingFeatureAllowed(sharingFeature);
                result.add(dto);
            }
            knLogger.debug(methodName, "EXIT result.size=", result.size());
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return result;
    }

    /**
     * Returns list of SHARED_HIERARCHY_IDs for the given (recId, ownerHierarchyId) pair.
     * Empty list means no child rows — treat as "all hierarchies allowed" (flat corp sharing).
     */
    public List<String> getSharedHierarchies(String recId, String ownerHierarchyId) throws SQLException {
        final String methodName = "getSharedHierarchies()";
        knLogger.debug(methodName, "ENTRY recId=", recId, " ownerHierarchyId=", ownerHierarchyId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        List<String> result = new ArrayList<>();
        try {
            String sql = "SELECT SHARED_HIERARCHY_ID FROM " + TABLE +
                    " WHERE REC_ID = ? AND OWNER_HIERARCHY_ID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, recId);
            pStmt.setString(2, ownerHierarchyId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("SHARED_HIERARCHY_ID"));
            }
            knLogger.debug(methodName, "EXIT result=", result);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        return result;
    }

    /**
     * Deletes all child rows for a given recId (CASE 1 — full delete).
     */
    public void deleteByRecId(String recId) throws SQLException {
        final String methodName = "deleteByRecId()";
        knLogger.debug(methodName, "ENTRY recId=", recId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "DELETE FROM " + TABLE + " WHERE REC_ID = ?";
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
     * Deletes all child rows for a given (recId, ownerHierarchyId) — CASE 2, no targetList.
     */
    public void deleteByRecIdAndOwner(String recId, String ownerHierarchyId) throws SQLException {
        final String methodName = "deleteByRecIdAndOwner()";
        knLogger.debug(methodName, "ENTRY recId=", recId, " ownerHierarchyId=", ownerHierarchyId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "DELETE FROM " + TABLE + " WHERE REC_ID = ? AND OWNER_HIERARCHY_ID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, recId);
            pStmt.setString(2, ownerHierarchyId);
            int deleted = pStmt.executeUpdate();
            knLogger.debug(methodName, "EXIT deleted=", deleted);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    /**
     * Deletes specific (recId, ownerHierarchyId, sharedHierarchyId) rows — CASE 2, with targetList.
     * Uses IN (...) clause for the shared hierarchy IDs (TC-DAO-002).
     * Rows not found are silently skipped (zero-row delete is not an error).
     */
    public void deleteByRecIdOwnerAndShared(String recId, String ownerHierarchyId, List<String> sharedIds) throws SQLException {
        final String methodName = "deleteByRecIdOwnerAndShared()";
        knLogger.debug(methodName, "ENTRY recId=", recId, " ownerHierarchyId=", ownerHierarchyId, " sharedIds=", sharedIds);
        if (sharedIds == null || sharedIds.isEmpty()) {
            return;
        }
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < sharedIds.size(); i++) {
                if (i > 0) placeholders.append(",");
                placeholders.append("?");
            }
            String sql = "DELETE FROM " + TABLE +
                    " WHERE REC_ID = ? AND OWNER_HIERARCHY_ID = ? AND SHARED_HIERARCHY_ID IN (" + placeholders + ")";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, recId);
            pStmt.setString(2, ownerHierarchyId);
            for (int i = 0; i < sharedIds.size(); i++) {
                pStmt.setString(3 + i, sharedIds.get(i));
            }
            int deleted = pStmt.executeUpdate();
            knLogger.debug(methodName, "EXIT deleted=", deleted);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    // ── private helpers ────────────────────────────────────────────────────────

    /**
     * Deletes all rows from DG.SHARED_TRUST_MATRIX_HIERARCHY where the given hierarchyId
     * appears as either OWNER_HIERARCHY_ID or SHARED_HIERARCHY_ID.
     * Called during deleteHierarchy to clean up stale trust matrix entries.
     */
    public void deleteByHierarchyId(String hierarchyId) throws SQLException {
        final String methodName = "deleteByHierarchyId()";
        knLogger.debug(methodName, "ENTRY hierarchyId=", hierarchyId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            String sql = "DELETE FROM " + TABLE +
                    " WHERE OWNER_HIERARCHY_ID = ? OR SHARED_HIERARCHY_ID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, hierarchyId);
            pStmt.setString(2, hierarchyId);
            int deleted = pStmt.executeUpdate();
            knLogger.debug(methodName, "EXIT deleted=", deleted);
        } finally {
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }

    private boolean existsRow(String recId, String ownerHierarchyId, String sharedHierarchyId) throws SQLException {
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT 1 FROM " + TABLE +
                    " WHERE REC_ID = ? AND OWNER_HIERARCHY_ID = ? AND SHARED_HIERARCHY_ID = ?";
            conn = KnGGConnection.getDBConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, recId);
            pStmt.setString(2, ownerHierarchyId);
            pStmt.setString(3, sharedHierarchyId);
            rs = pStmt.executeQuery();
            return rs.next();
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
    }
}
