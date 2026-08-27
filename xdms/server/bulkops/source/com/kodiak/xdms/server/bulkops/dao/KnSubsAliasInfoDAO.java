package com.kodiak.xdms.server.bulkops.dao;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.dto.common.KnSubsAliasInfoDTO;
import com.kodiak.xdms.server.bulkops.resources.KnBulkOpsDAOSourceTypes;


import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnSubsAliasInfoDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsAliasInfoDAO.class);
    private String pttServerId;

    public static final String ALIASIDINFOLIST = "ALIASIDINFOLIST";
    public static final String ALIASISSUERLIST = "ALIASISSUERLIST";
    public static final String QRY_GET_SUBS_ALIASID_INFO = "SELECT MDN, ALIASID, ALIAS_ISSUER, ALIASID_TYPE" +
            " FROM DG.POCSUBSCR_ALIASIDLIST" +
            " WHERE ALIASID IN ( ALIASIDINFOLIST ) " +
            " AND ALIAS_ISSUER IN ( ALIASISSUERLIST )";

    public KnSubsAliasInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public Map<String, List<KnSubsAliasInfoDTO>> selectSubsAliasIdInfo(Map<String, String> aliasIdIssuerMap, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {

        final String methodName = "selectSubsAliasIdInfo(Map<String, String>,boolean, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: aliasIdIssuerMap - ", aliasIdIssuerMap, " readOnly :", readOnly);
        Map<String, List<KnSubsAliasInfoDTO>> subsAliasInfoMap = new HashMap<>();
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String query = QRY_GET_SUBS_ALIASID_INFO;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            String aliasIdStr = KnGeneralUtil.formCommaSeperatedIdList(aliasIdIssuerMap.keySet());
            String aliasIdIsStr = KnGeneralUtil.formCommaSeperatedIdList(aliasIdIssuerMap.values());

            query = KnGeneralUtil.replaceContactWithValue(query, ALIASIDINFOLIST, aliasIdStr);
            query = KnGeneralUtil.replaceContactWithValue(query, ALIASISSUERLIST, aliasIdIsStr);

            stmt = conn.createStatement();
            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);

            rs = stmt.executeQuery(query);
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                KnSubsAliasInfoDTO aliasIdInfo = new KnSubsAliasInfoDTO();
                aliasIdInfo.setMdn(mdn);
                if (rs.getBytes(2) != null) {
                    aliasIdInfo.setAliasId(new String(rs.getBytes(2), StandardCharsets.UTF_8));
                }
                aliasIdInfo.setAliasIdIssuer(rs.getString(3));
                aliasIdInfo.setAliasIdType((Integer) rs.getObject(4));

                if (subsAliasInfoMap.get(mdn) != null) {
                    subsAliasInfoMap.get(mdn).add(aliasIdInfo);
                } else {
                    List<KnSubsAliasInfoDTO> aliasInfoDTOList = new ArrayList<KnSubsAliasInfoDTO>();
                    aliasInfoDTOList.add(aliasIdInfo);
                    subsAliasInfoMap.put(mdn, aliasInfoDTOList);
                }

            }
            knLogger.debug(methodName, "Query: Executed :", subsAliasInfoMap.size());

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get subs camera info - " + e.getMessage(),
                    pttServerId, KnBulkOpsDAOSourceTypes.POCSUBSCR_ALIASIDLIST
                    , QRY_GET_SUBS_ALIASID_INFO);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }

        return subsAliasInfoMap;
    }

    /**
     * Bulk variant: accepts a list of aliasId→issuer maps, merges them into batched IN lists, and
     * executes a single query to retrieve alias info for all provided pairs.
     * Returns a map keyed by MDN with the list of alias info DTOs.
     */
    public Map<String, List<KnSubsAliasInfoDTO>> selectSubsAliasIdInfo(List<Map<String, String>> aliasIdIssuerMaps,
                                                                       boolean readOnly,
                                                                       KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "selectSubsAliasIdInfo(List<Map<String,String>>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: aliasIdIssuerMaps count - ", (aliasIdIssuerMaps == null ? 0 :
                aliasIdIssuerMaps.size()), " readOnly :", readOnly);
        Map<String, List<KnSubsAliasInfoDTO>> subsAliasInfoMap = new HashMap<>();
        if (aliasIdIssuerMaps == null || aliasIdIssuerMaps.isEmpty()) {
            knLogger.debug(methodName, "No aliasIdIssuer maps provided");
            return subsAliasInfoMap;
        }

        // Merge all aliasIds and issuers into sets to avoid duplicates
        List<String> allAliasIds = new ArrayList<>();
        List<String> allIssuers = new ArrayList<>();
        for (Map<String, String> m : aliasIdIssuerMaps) {
            if (m == null || m.isEmpty()) continue;
            allAliasIds.addAll(m.keySet());
            allIssuers.addAll(m.values());
        }
        if (allAliasIds.isEmpty() || allIssuers.isEmpty()) {
            knLogger.debug(methodName, "No aliasIds or issuers to query");
            return subsAliasInfoMap;
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String query = QRY_GET_SUBS_ALIASID_INFO;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            String aliasIdStr = KnGeneralUtil.formCommaSeperatedIdList(allAliasIds);
            String aliasIssuerStr = KnGeneralUtil.formCommaSeperatedIdList(allIssuers);

            query = KnGeneralUtil.replaceContactWithValue(query, ALIASIDINFOLIST, aliasIdStr);
            query = KnGeneralUtil.replaceContactWithValue(query, ALIASISSUERLIST, aliasIssuerStr);

            stmt = conn.createStatement();
            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);

            rs = stmt.executeQuery(query);
            // Column indices based on SELECT: MDN(1), ALIASID(2), ALIAS_ISSUER(3), ALIASID_TYPE(4)
            while (rs != null && rs.next()) {
                String mdn = rs.getString(1);
                if (mdn == null) continue;
                mdn = mdn.trim();

                KnSubsAliasInfoDTO aliasIdInfo = new KnSubsAliasInfoDTO();
                aliasIdInfo.setMdn(mdn);
                byte[] aliasIdBytes = rs.getBytes(2);
                if (aliasIdBytes != null) {
                    aliasIdInfo.setAliasId(new String(aliasIdBytes, StandardCharsets.UTF_8));
                }
                aliasIdInfo.setAliasIdIssuer(rs.getString(3));
                int aliasType = rs.getInt(4);
                if (!rs.wasNull()) {
                    aliasIdInfo.setAliasIdType(aliasType);
                }

                subsAliasInfoMap.computeIfAbsent(mdn, k -> new ArrayList<>()).add(aliasIdInfo);
            }
            knLogger.debug(methodName, "Query: Executed :", subsAliasInfoMap.size());
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to get subs alias info - " + e.getMessage(), pttServerId,
                    KnBulkOpsDAOSourceTypes.POCSUBSCR_ALIASIDLIST, QRY_GET_SUBS_ALIASID_INFO);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return subsAliasInfoMap;
    }
}
