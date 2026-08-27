package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpPTTSettingDocRespDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;

public class KnCorpPTTSettingDocDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpPTTSettingDocDAO.class);

    private static final String TABLENAME = "DG.CORP_PTTSETTING_MAP";
    private static final String SUBSCRIBER_ADDL_INFO_TABLE = "DG.POCSUBSCR_ADDLINFO";

    private static final String UPDATE_QRY = "UPDATE " + TABLENAME + " SET ";

    private static final String CORPID  = "CORPID";
    private static final String PTT_SETTING_DOCID  = "PTT_SETTING_DOCID";
    private static final String HIERARCHY_ID  = "HIERARCHY_ID";
    private static final String IS_DEFAULTDOC  = "IS_DEFAULTDOC";
    private static final String CREATION_TIME  = "CREATION_TIME";
    private static final String UPDATE_TIME  = "UPDATE_TIME";
    private static final String MDNLIST  = "MDNLIST";

    public String pttServerId = null;
    public KnCorpPTTSettingDocDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }



    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return List.of();
    }

    public Set<KnCorpPTTSettingDocRespDTO> getPTTSettingDocIdForCorp(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPTTSettingDocIdForCorp(int,  KnPersisterTxn)";
        knLogger.debug(methodName, corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Set<KnCorpPTTSettingDocRespDTO> pttDocList = new HashSet<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PTT_SETTING_DOC_FOR_CORP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                KnCorpPTTSettingDocRespDTO respDTO = new KnCorpPTTSettingDocRespDTO();
                respDTO.setPttSettingId(rs.getString(PTT_SETTING_DOCID));
                respDTO.setIsDefault(rs.getInt(IS_DEFAULTDOC));
                pttDocList.add(respDTO);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while retrieving the getPTTSettingIds - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the getPTTSettingIds -  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "pttDocList: ",pttDocList);
        return pttDocList;

    }
    public Set<KnCorpPTTSettingDocRespDTO> getPTTSettingDocIdsForHierarchy(int corpId,String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPTTSettingDocIdsForHierarchy(int,  KnPersisterTxn)";
        knLogger.debug(methodName, corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Set<KnCorpPTTSettingDocRespDTO> pttDocList = new HashSet<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PTT_SETTING_DOC_FOR_HIERARCHY);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setString(2, hierarchyId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                KnCorpPTTSettingDocRespDTO respDTO = new KnCorpPTTSettingDocRespDTO();
                respDTO.setPttSettingId(rs.getString(PTT_SETTING_DOCID));
                respDTO.setIsDefault(rs.getInt(IS_DEFAULTDOC));
                pttDocList.add(respDTO);
            }
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while retrieving the getPTTSettingDocIdsForHierarchy - ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the getPTTSettingDocIdsForHierarchy -  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "pttDocList: ",pttDocList);
        return pttDocList;

    }

    public boolean isPttSettingDocAlreadyDefault(String docId, int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isPttSettingDocAlreadyDefault(String, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: docId - ", docId, ", corpId - ", corpId, ", hierarchyId - ", hierarchyId);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        boolean isDefault = false;

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);

            // Check if hierarchyId is null and use appropriate query
            if (hierarchyId == null || hierarchyId.trim().isEmpty()) {
                // Query without hierarchy_id condition
                query = queryMapper.getQuery(CHECK_PTT_SETTING_DOC_IS_DEFAULT_WITHOUT_HIERARCHY);
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, docId);
                pstmt.setInt(2, corpId);
                knLogger.debug(methodName, "Using query without hierarchy_id");
            } else {
                // Query with hierarchy_id condition
                query = queryMapper.getQuery(CHECK_PTT_SETTING_DOC_IS_DEFAULT);
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, docId);
                pstmt.setString(2, hierarchyId);
                pstmt.setInt(3, corpId);
                knLogger.debug(methodName, "Using query with hierarchy_id - ", hierarchyId);
            }

            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int isDefaultDoc = rs.getInt("IS_DEFAULTDOC");
                isDefault = (isDefaultDoc == 1);
                knLogger.debug(methodName, "IS_DEFAULTDOC value - ", isDefaultDoc);
            }

            knLogger.debug(methodName, "EXIT: isDefault - ", isDefault);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while checking if PTT Setting Doc is default - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while checking if PTT Setting Doc is default - ", e);
            throw KnDbUtil.processException(e, "Failed while checking if PTT Setting Doc is default - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }

        return isDefault;
    }

    public void resetAllDefaultPttSettingDocs(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "resetAllDefaultPttSettingDocs(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: corpId - ", corpId, ", hierarchyId - ", hierarchyId);

        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            // Check if hierarchyId is null and use appropriate query
            if (hierarchyId == null || hierarchyId.trim().isEmpty()) {
                // Query without hierarchy_id condition - resets all for corp
                query = queryMapper.getQuery(RESET_ALL_DEFAULT_PTT_SETTING_DOCS_WITHOUT_HIERARCHY);
                pstmt = conn.prepareStatement(query);
                pstmt.setLong(1, System.currentTimeMillis());
                pstmt.setInt(2, corpId);
                knLogger.debug(methodName, "Resetting all defaults for corp without hierarchy_id");
            } else {
                // Query with hierarchy_id condition - resets all for specific hierarchy
                query = queryMapper.getQuery(RESET_ALL_DEFAULT_PTT_SETTING_DOCS);
                pstmt = conn.prepareStatement(query);
                pstmt.setLong(1, System.currentTimeMillis());
                pstmt.setString(2, hierarchyId);
                pstmt.setInt(3, corpId);
                knLogger.debug(methodName, "Resetting all defaults for hierarchy - ", hierarchyId);
            }

            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            int rowsUpdated = pstmt.executeUpdate();
            knLogger.info(methodName, "EXIT: Reset ", rowsUpdated, " PTT Setting docs to non-default");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while resetting all default PTT Setting docs - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while resetting all default PTT Setting docs - ", e);
            throw KnDbUtil.processException(e, "Failed while resetting all default PTT Setting docs - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    public void resetDefaultPTTSettingDoc( int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "resetDefaultPTTSettingDoc(String, String,  String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: corpId - ", corpId, ", hierarchyId - ", hierarchyId);
        Connection conn = null;
        PreparedStatement pstmtReset = null;
        PreparedStatement pstmtSet = null;
        String resetQuery = null;
        String setQuery = null;
        long updateTime = System.currentTimeMillis();

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            // Determine if hierarchyId is null/empty
            boolean hasHierarchy = (hierarchyId != null && !hierarchyId.trim().isEmpty());
            resetQuery = queryMapper.getQuery(RESET_ALL_DEFAULT_PTT_SETTING_DOCS);
            pstmtReset = conn.prepareStatement(resetQuery);
            pstmtReset.setLong(1, updateTime);
            // STEP 1: Reset all other PTT Setting docs to IS_DEFAULTDOC = 0
            if (hasHierarchy) {
                // Reset query with hierarchy_id condition
                pstmtReset.setString(2, hierarchyId);
                pstmtReset.setInt(3, corpId);
                knLogger.debug(methodName, "Resetting all defaults for hierarchy - ", hierarchyId);
            } else {
                // Reset query without hierarchy_id condition - resets all for corp
                pstmtReset.setString(2, String.valueOf(corpId));
                pstmtReset.setInt(3, corpId);
                knLogger.debug(methodName, "Resetting all defaults for corp without hierarchy_id");
            }
            knLogger.debug(methodName, "Executing reset query - ", "'", resetQuery, "'");
            int rowsReset = pstmtReset.executeUpdate();
            knLogger.info(methodName, "Reset ", rowsReset, " PTT Setting docs to non-default");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while updatePttSettingDocToDefault - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while updatePttSettingDocToDefault - ", e);
            throw KnDbUtil.processException(e, "Failed while updatePttSettingDocToDefault - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, resetQuery != null ? resetQuery : setQuery);
        } finally {
            KnDbUtil.closePreparedStatement(pstmtReset);
            KnDbUtil.closePreparedStatement(pstmtSet);
        }
    }

    public void updatePttSettingDocToDefault(String docId, int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updatePttSettingDocToDefault(String, String,  String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: docId - ", docId, ", corpId - ", corpId, ", hierarchyId - ", hierarchyId);

        Connection conn = null;
        PreparedStatement pstmtReset = null;
        PreparedStatement pstmtSet = null;
        String resetQuery = null;
        String setQuery = null;
        long updateTime = System.currentTimeMillis();

        try {
            // Get single connection and query mapper instance for both operations
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            // Determine if hierarchyId is null/empty
            boolean hasHierarchy = (hierarchyId != null && !hierarchyId.trim().isEmpty());
            setQuery = queryMapper.getQuery(UPDATE_PTT_SETTING_DOC_TO_DEFAULT);
            pstmtSet = conn.prepareStatement(setQuery);
            pstmtSet.setLong(1, updateTime);
            pstmtSet.setString(2, docId);
            // STEP 2: Set the requested document as default
            if (hasHierarchy) {
                // Set query with hierarchy_id condition
                pstmtSet.setString(3, hierarchyId);
                pstmtSet.setInt(4, corpId);
                knLogger.debug(methodName, "Setting default with hierarchy_id - ", hierarchyId);
            } else {
                // Set query without hierarchy_id condition
                pstmtSet.setString(3, String.valueOf(corpId));
                pstmtSet.setInt(4, corpId);
                knLogger.debug(methodName, "Setting default without hierarchy_id");
            }
            knLogger.debug(methodName, "Executing set query - ", "'", setQuery, "'");
            int rowsSet = pstmtSet.executeUpdate();
            knLogger.info(methodName, "EXIT: Successfully set ", rowsSet, " PTT Setting doc as default. docId - ", docId);

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while updatePttSettingDocToDefault - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while updatePttSettingDocToDefault - ", e);
            throw KnDbUtil.processException(e, "Failed while updatePttSettingDocToDefault - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, resetQuery != null ? resetQuery : setQuery);
        } finally {
            KnDbUtil.closePreparedStatement(pstmtReset);
            KnDbUtil.closePreparedStatement(pstmtSet);
        }
    }

    public void addPttSettingToHierarchy(String docId, int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "addPttSettingToHierarchy(KnXDMPTTSettingHierarchyListDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, ", hierarchyId - ", hierarchyId, ", docId - ",docId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_CORP_PTT_SETTING_DOC);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setString(2, docId);
            pstmt.setString(3, hierarchyId);
            pstmt.setInt(4, 0);
            pstmt.setLong(5, System.currentTimeMillis());
            pstmt.setLong(6, System.currentTimeMillis());

            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while addPttSettingToHierarchy- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while addPttSettingToHierarchy - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while addPttSettingToHierarchy  - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deletePttSettingHierarchyMapping(String docId, int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deletePttSettingHierarchyMapping(KnXDMPTTSettingHierarchyListDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, ", hierarchyId - ", hierarchyId, ", docId - ",docId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORP_PTT_SETTING_DOC);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setString(2, docId);
            pstmt.setString(3, hierarchyId);

            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occurred while deletePttSettingHierarchyMapping- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred while deletePttSettingHierarchyMapping - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while deletePttSettingHierarchyMapping  - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * Clears the PTT_SETTING_DOCID column in DG.POCSUBSCR_ADDLINFO for all subscribers
     * within the given corporation and hierarchy that currently have the supplied PTT
     * Setting doc id assigned. The fallback resolution (hierarchy default -> system default)
     * is performed at read time by the existing getSubscriberDetails flow.
     *
     * @param pttSettingId the PTT setting doc id being unassigned from the hierarchy
     * @param corpId       corp id
     * @param hierarchyId  hierarchy id from which the PTT doc is being unassigned
     * @param persisterTxn persister transaction
     * @throws KnDAOException on DB failures
     */
    public void clearPttSettingDocFromHierarchySubscribers(String pttSettingId, int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "clearPttSettingDocFromHierarchySubscribers(String, int, String, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId, ", pttSettingId - ", pttSettingId, ", hierarchyId - ", hierarchyId);

        if (pttSettingId == null || pttSettingId.trim().isEmpty() || hierarchyId == null || hierarchyId.trim().isEmpty()) {
            knLogger.debug(methodName, "pttSettingId/hierarchyId is null or empty, skipping clearPttSettingDocFromHierarchySubscribers");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UNASSIGN_PTT_SETTING_DOC_FROM_HIERARCHY_SUBSCRIBERS);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pttSettingId);
            pstmt.setInt(2, corpId);
            pstmt.setString(3, hierarchyId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            int removedSubs = pstmt.executeUpdate();
            knLogger.debug(methodName, "EXIT: Subscribers unassigned from PTT Setting doc count- ", removedSubs);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while clearPttSettingDocFromHierarchySubscribers - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while clearPttSettingDocFromHierarchySubscribers - ", e);
            throw KnDbUtil.processException(e, "Failed while clearPttSettingDocFromHierarchySubscribers  - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void assignPttSettingDocToMdns(List<String> mdnList, String pttSettingId, int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "assignPttSettingDocToMdns()";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId, ", pttSettingId - ", pttSettingId, ", mdnList - ", mdnList, ", hierarchyId - ", hierarchyId);

        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(ASSIGN_PTT_SETTING_DOC_TO_MDNS);

            String mdnlist = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList);
            query = KnDbUtil.replaceContactWithValue(query, MDNLIST, mdnlist);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pttSettingId);
            int paramIndex = 2;
            for (String mdn : mdnList) {
                pstmt.setString(paramIndex++, mdn);
            }

            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt.executeUpdate();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while assignPttSettingDocToMdns- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while assignPttSettingDocToMdns - ", e);
            throw KnDbUtil.processException(e, "Failed while assignPttSettingDocToMdns  - " + e,
                pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void updateLastProfileUpdateTimeForMdns(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateLastProfileUpdateTimeForMdns()";
        knLogger.info(methodName, "ENTRY : mdnList - ", mdnList);

        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String placeholders = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList);
            query = "UPDATE DG.POCSUBSCRINFO SET LASTPROFILEUPDATETIME = ? WHERE MDN IN (MDNLIST)";
            query = KnDbUtil.replaceContactWithValue(query, MDNLIST, placeholders);
            pstmt = conn.prepareStatement(query);
            pstmt.setLong(1, System.currentTimeMillis());
            int paramIndex = 2;
            for (String mdn : mdnList) {
                pstmt.setString(paramIndex++, mdn);
            }
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeUpdate();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while updateLastProfileUpdateTimeForMdns- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while updateLastProfileUpdateTimeForMdns - ", e);
            throw KnDbUtil.processException(e, "Failed while updateLastProfileUpdateTimeForMdns  - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void unassignPttSettingDocToMdns(List<String> mdnList, int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "unassignPttSettingDocToMdns()";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId, "mdnList - ", mdnList, ", hierarchyId - ", hierarchyId);

        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UNASSIGN_PTT_SETTING_DOC_FROM_MDNS);
            String mdnlist = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList);
            query = KnDbUtil.replaceContactWithValue(query, MDNLIST, mdnlist);
            pstmt = conn.prepareStatement(query);
            int paramIndex = 1;
            for (String mdn : mdnList) {
                pstmt.setString(paramIndex++, mdn);
            }
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt.executeUpdate();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while unassignPttSettingDocToMdns- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while unassignPttSettingDocToMdns - ", e);
            throw KnDbUtil.processException(e, "Failed while unassignPttSettingDocToMdns  - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public List<KnXDMMdnInfoDTO> getPttSettingDocMdnList(String pttSettingId, String hierarchyId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPttSettingDocMdnList(String, String, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: corpId:", corpId, ", pttSettingId:", pttSettingId, ", hierarchyId:", hierarchyId);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<KnXDMMdnInfoDTO> mdnList = new ArrayList<>();

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PTT_SETTING_DOC_MDNLIST);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pttSettingId);
            pstmt.setInt(2, corpId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String mdn = rs.getString(1);
                String subsName = rs.getString(2);

                KnXDMMdnInfoDTO mdnInfoDTO = new KnXDMMdnInfoDTO();
                mdnInfoDTO.setMdn(mdn);
                mdnInfoDTO.setName(subsName);
                mdnList.add(mdnInfoDTO);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getPttSettingDocMdnList - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while getPttSettingDocMdnList - ", e);
            throw KnDbUtil.processException(e, "Failed while getPttSettingDocMdnList -" + e, pttServerId, "", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT: assignedMdn list size=", mdnList.size());
        return mdnList;
    }

    public int getMDNCountForPttSettingDocID(String pttSettingID, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMDNCountForPttSettingDocID()";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId, "pttSettingID - ", pttSettingID);

        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        int subsCount = 0;

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_MDN_COUNT_FOR_PTT_SETTING_DOCID);

            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pttSettingID);
            pstmt.setInt(2, corpId);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                subsCount = rs.getInt(1);
            }
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getMDNCountForPttSettingDocID - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while getMDNCountForPttSettingDocID - ", e);
            throw KnDbUtil.processException(e, "Failed while getMDNCountForPttSettingDocID - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT: subsCount", subsCount);
        return subsCount;
    }

    public void updateSubscriberPttSettingDocId(String newPttSettingDocId, String mdn, KnPersisterTxn persisterTxn)  throws KnDAOException {
        String methodName = "updateSubscriberPttSettingDocId()";
        knLogger.info(methodName, "ENTRY : MDN - ", mdn, "newPttSettingDocId - ", newPttSettingDocId);

        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            query = "UPDATE DG.POCSUBSCR_ADDLINFO SET PTT_SETTING_DOCID = ? WHERE MDN = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, newPttSettingDocId);
            pstmt.setString(2, mdn);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while updateSubscriberPttSettingDocId- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while updateSubscriberPttSettingDocId - ", e);
            throw KnDbUtil.processException(e, "Failed while updateSubscriberPttSettingDocId  - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deletePttSettingCorpMapping(String docId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deletePttSettingCorpMapping(String, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId,  ", docId - ", docId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORP_PTT_SETTING_DOC);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setString(2, docId);
            pstmt.setString(3, String.valueOf(corpId));

            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeUpdate(); // Use executeUpdate() for DELETE statements
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while deletePttSettingCorpMapping- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while deletePttSettingCorpMapping - ", e);
            throw KnDbUtil.processException(e, "Failed while deleting PTT setting Corp mapping - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, ""); // Query is dynamic, so pass empty string
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public List<KnCorpPTTSettingDocRespDTO> getCorpPTTSettingDocInfoForTemplateId(String pttSettingId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpPTTSettingDocInfoForTemplateId";
        knLogger.debug(methodName, pttSettingId);
        List<KnCorpPTTSettingDocRespDTO> respDTOList = new ArrayList<>();
        String query = KnQueryMapper.getInstance().getQuery(GET_CORP_PTT_SETTING_DOC_FOR_TEMPLATE_ID);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pttSettingId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs= pstmt.executeQuery();
            while (rs.next()) {
                KnCorpPTTSettingDocRespDTO respDTO = new KnCorpPTTSettingDocRespDTO();
                respDTO.setCorpId(rs.getInt(CORPID));
                respDTO.setPttSettingId(rs.getString(PTT_SETTING_DOCID));
                respDTO.setHierarchyId(rs.getString(HIERARCHY_ID));
                respDTO.setIsDefault(rs.getInt(IS_DEFAULTDOC));
                respDTO.setCreatedTime(rs.getLong(CREATION_TIME));
                respDTO.setUpdatedTime(rs.getLong(UPDATE_TIME));
                respDTOList.add(respDTO);
            }
            knLogger.debug(methodName, "Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getCorpPTTSettingDocInfoForTemplateId- ", e);
            throw e;
        }catch (Exception e) {
            knLogger.error(methodName, "Error occurred while retrieving data - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve data - " + e, pttServerId, KnDAOSourceTypes.XDM_CORP_PTTSETTING, query);
        }
        finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "Response DTO List: ", respDTOList);
        return respDTOList;
    }


}
