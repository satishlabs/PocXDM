package com.kodiak.xdms.server.bulkops.dao;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.bulkops.KnBulkOpsException;
import com.kodiak.xdms.server.bulkops.utils.KnBulkOpsDBUtil;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;

import java.util.List;
import java.util.Map;

public class KnSimulSessionDocDAO {
    private final KnBulkOpsDBUtil bulkOpsDBUtil;

    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMDirectoryDAO.class);
    public static final String INSERT = "INSERT INTO DG.SIMULSESSION_DOC  VALUES (?, ?)";
    public static final String DELETE_MDN = "DELETE FROM DG.SIMULSESSION_DOC WHERE MDN = ?";
    public static final String ETAG_SELECT_IN = "SELECT MDN, ETAG FROM DG.SIMULSESSION_DOC WHERE MDN IN ";

    public KnSimulSessionDocDAO() throws KnBulkOpsException {
        bulkOpsDBUtil = KnBulkOpsDBUtil.getInstance();
    }

    public void deleteMdn(String mdn, KnPersisterTxn persistTxn) throws KnDAOException {
        // delete the entry in table for requested mdn
        final String methodName = "deleteMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        String query = DELETE_MDN;
        try {
            java.util.List<Object[]> batchParams = new java.util.ArrayList<>();
            batchParams.add(new Object[]{ mdn.trim() });
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persistTxn);
            bulkOpsDBUtil.executeBatchUpdate(query, batchParams, persistTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
            knLogger.debug(methodName, "QUERY : Completed.");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to deleteMdn for mdn - " + e.getMessage(), null, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        }
    }

    /**
     * Retrieve SIMULSESSION_DOC etags for a list of MDNs in bulk.
     * <p>
     * This method performs a single IN-clause query against the SIMULSESSION_DOC table
     * and returns a mapping of MDN to its current etag. For MDNs that do not have a row
     * in the table, the returned etag value will be {@code -1}. This matches the
     * single-MDN logic, where an absent TGSS document is represented by etag {@code -1},
     * allowing callers to compute {@code newEtag = etag + 1} and detect creation cases
     * when {@code newEtag == 0}.
     * </p>
     *
     * @param mdns          list of MDNs for which etags should be retrieved; if {@code null}
     *                      or empty, an empty map is returned
     * @param persisterTxn  the transaction context; if provided and not owned by this method,
     *                      its lifecycle (open/save/rollback) is respected by the underlying
     *                      DB utility; connection is obtained via the XDM shared datastore
     * @return a map of MDN to etag, containing an entry for each input MDN; MDNs not present
     *         in SIMULSESSION_DOC will have an etag value of {@code -1}
     * @throws KnDAOException if a database error occurs while executing the query
     *
     * @implNote Uses {@link KnBulkOpsDBUtil#executeInClauseQuery(String, java.util.List, KnBulkOpsDBUtil.ResultSetProcessor, KnPersisterTxn, KnDBConst.DataStores)}
     *           with {@link KnDBConst.DataStores#XDM_SHARED_DATA}. The returned map is first
     *           initialized with {@code -1} for all input MDNs and then overwritten only for
     *           MDNs found by the query.
     */
    public Map<String, Long> getSSDocEtags(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSSDocEtags(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : size=", (mdns != null ? mdns.size() : 0));
        if (mdns == null || mdns.isEmpty()) {
            return new java.util.HashMap<>();
        }
        // Initialize with -1 for all MDNs to indicate 'not found'
        Map<String, Long> etagMap = new java.util.HashMap<>();
        for (String mdn : mdns) {
            etagMap.put(mdn, -1L);
        }
        Map<String, Long> found = bulkOpsDBUtil.executeInClauseQuery(ETAG_SELECT_IN, mdns,
                rs -> {
                    Map<String, Long> map = new java.util.HashMap<>();
                    while (rs.next()) {
                        String mdn = rs.getString("MDN");
                        long etag = Long.parseLong(rs.getString("ETAG"));
                        map.put(mdn, etag);
                    }
                    return map;
                },
                persisterTxn,
                KnDBConst.DataStores.XDM_SHARED_DATA);
        // Overwrite defaults with found values
        for (Map.Entry<String, Long> entry : found.entrySet()) {
            etagMap.put(entry.getKey(), entry.getValue());
        }
        knLogger.debug(methodName, "Exit : found ", found.size(), " rows; total returned ", etagMap.size());
        return etagMap;
    }

    /**
     * Bulk insert SIMULSESSION_DOC rows.
     */
    public void insertMdns(List<Object[]> mdnEtagParams, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertMdns(List<Object[]>, KnPersisterTxn)";
        if (mdnEtagParams == null || mdnEtagParams.isEmpty()) {
            knLogger.debug(methodName, "No rows to insert");
            return;
        }
        bulkOpsDBUtil.executeBatchUpdate(INSERT, mdnEtagParams, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
        knLogger.debug(methodName, "Inserted ", mdnEtagParams.size(), " rows");
    }

    /**
     * Bulk delete SIMULSESSION_DOC rows by MDN.
     */
    public void deleteMdns(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteMdns(List<String>, KnPersisterTxn)";
        if (mdns == null || mdns.isEmpty()) {
            knLogger.debug(methodName, "No rows to delete");
            return;
        }
        List<Object[]> batchParams = new java.util.ArrayList<>(mdns.size());
        for (String mdn : mdns) {
            batchParams.add(new Object[]{ mdn.trim() });
        }
        bulkOpsDBUtil.executeBatchUpdate(DELETE_MDN, batchParams, persisterTxn, KnDBConst.DataStores.XDM_SHARED_DATA);
        knLogger.debug(methodName, "Deleted ", mdns.size(), " rows");
    }


}
