package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.commdto.common.KnModifiedIdDetailsListDTO;
import com.kodiak.common.dao.KnConnectionException;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpHierarchyGeocodeMapDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDeploySiteInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnHierarchyDepthInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnHierarchyInfoDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedQuesMarks;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

public class KnXDMHierarchyDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMHierarchyDAO.class);
    private String pttServerId;
    ;

    public KnXDMHierarchyDAO(String pttServerId) {
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

    public void insertIntoHierarchyDetails(List<KnHierarchyInfoDTO> hierarchyInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertIntoHierarchyDetails(List<KnHierarchyInfoDTO> , KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : hierarchyInfoDTOS - ", hierarchyInfoDTOS);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_INTO_HIERARCHY_DETAILS);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (KnHierarchyInfoDTO hierarchyInfoDTO : hierarchyInfoDTOS) {
                int index = 1;
                // Required fields (NOT NULL)
                pstmt.setString(index++, hierarchyInfoDTO.getCorpId());
                pstmt.setString(index++, hierarchyInfoDTO.getHierarchyId());
                pstmt.setString(index++, hierarchyInfoDTO.getExternalHierarchyId());
                // Optional fields
                if (hierarchyInfoDTO.getHierarchyName() != null) {
                    pstmt.setString(index++, hierarchyInfoDTO.getHierarchyName());
                } else {
                    pstmt.setNull(index++, Types.NVARCHAR);
                }
                if (hierarchyInfoDTO.getHierarchyAlias() != null) {
                    pstmt.setString(index++, hierarchyInfoDTO.getHierarchyAlias());
                } else {
                    pstmt.setNull(index++, Types.NVARCHAR);
                }
                // Required field (NOT NULL)
                pstmt.setByte(index++, (byte)1);
                // Optional fields
                if (hierarchyInfoDTO.getSegmentIndicator() != null) {
                    pstmt.setString(index++, hierarchyInfoDTO.getSegmentIndicator());
                } else {
                    pstmt.setNull(index++, Types.VARCHAR);
                }
                if (hierarchyInfoDTO.getSubScrDefPttRadio() != null) {
                    pstmt.setByte(index++, hierarchyInfoDTO.getSubScrDefPttRadio());
                } else {
                    pstmt.setNull(index++, Types.TINYINT);
                }
                if (hierarchyInfoDTO.getFeatureVersion() != null) {
                    pstmt.setInt(index++, hierarchyInfoDTO.getFeatureVersion());
                } else {
                    pstmt.setNull(index++, Types.INTEGER);
                }
                if (hierarchyInfoDTO.getCreationTime() != null) {
                    pstmt.setLong(index++, hierarchyInfoDTO.getCreationTime());
                } else {
                    pstmt.setNull(index++, Types.BIGINT);
                }
                if (hierarchyInfoDTO.getUpdateTime() != null) {
                    pstmt.setLong(index++, hierarchyInfoDTO.getUpdateTime());
                } else {
                    pstmt.setNull(index++, Types.BIGINT);
                }
                if (hierarchyInfoDTO.getStatus() != null) {
                    pstmt.setByte(index++, hierarchyInfoDTO.getStatus());
                } else {
                    pstmt.setNull(index++, Types.TINYINT);
                }
                if (hierarchyInfoDTO.getCustomField1() != null) {
                    pstmt.setString(index++, hierarchyInfoDTO.getCustomField1());
                } else {
                    pstmt.setNull(index++, Types.VARCHAR);
                }
                if (hierarchyInfoDTO.getCustomField2() != null) {
                    pstmt.setString(index++, hierarchyInfoDTO.getCustomField2());
                } else {
                    pstmt.setNull(index++, Types.VARCHAR);
                }
                pstmt.addBatch();

            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert into hierarchy info table " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void insertOrUpdateHierarchyDetails(List<KnHierarchyInfoDTO> hierarchyInfoDTOS, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertOrUpdateHierarchyDetails(List<KnHierarchyInfoDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point: hierarchyInfoDTOS - ", hierarchyInfoDTOS);
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        String insertQuery = null;
        String updateQuery = null;
        Connection conn;

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            insertQuery = queryMapper.getQuery(INSERT_INTO_HIERARCHY_DETAILS);
            updateQuery = queryMapper.getQuery(UPDATE_INTO_HIERARCHY_DETAILS);

            knLogger.debug(methodName, "Insert Query: ", insertQuery);
            knLogger.debug(methodName, "Update Query: ", updateQuery);

            insertStmt = conn.prepareStatement(insertQuery);
            updateStmt = conn.prepareStatement(updateQuery);

            // Fetch all existing hierarchy IDs in one query
            Set<String> existingHierarchyIds = fetchExistingHierarchyIds(corpId, conn);

            for (KnHierarchyInfoDTO hierarchyInfoDTO : hierarchyInfoDTOS) {
                if (existingHierarchyIds.contains(hierarchyInfoDTO.getHierarchyId())) {
                    knLogger.info(methodName, " Hierarchy ID exists, preparing to update: ", hierarchyInfoDTO.getHierarchyId());
                    // Prepare update statement
                    int index = 1;
                    updateStmt.setString(index++, hierarchyInfoDTO.getHierarchyName() == null ? null : hierarchyInfoDTO.getHierarchyName());
                    updateStmt.setString(index++, hierarchyInfoDTO.getHierarchyAlias() == null ? null : hierarchyInfoDTO.getHierarchyAlias());
                    updateStmt.setString(index++, hierarchyInfoDTO.getSegmentIndicator());
                    if (hierarchyInfoDTO.getSubScrDefPttRadio() != null) {
                        updateStmt.setByte(index++, hierarchyInfoDTO.getSubScrDefPttRadio());
                    } else {
                        updateStmt.setNull(index++, Types.TINYINT);
                    }
                    if (hierarchyInfoDTO.getFeatureVersion() != null) {
                        updateStmt.setInt(index++, hierarchyInfoDTO.getFeatureVersion());
                    } else {
                        updateStmt.setNull(index++, Types.INTEGER);
                    }
                    if (hierarchyInfoDTO.getUpdateTime() != null) {
                        updateStmt.setLong(index++, hierarchyInfoDTO.getUpdateTime());
                    } else {
                        updateStmt.setNull(index++, Types.BIGINT);
                    }
                    if (hierarchyInfoDTO.getStatus() != null) {
                        updateStmt.setByte(index++, hierarchyInfoDTO.getStatus());
                    } else {
                        updateStmt.setNull(index++, Types.TINYINT);
                    }
                    updateStmt.setString(index++, hierarchyInfoDTO.getCustomField1());
                    updateStmt.setString(index++, hierarchyInfoDTO.getCustomField2());
                    updateStmt.setString(index++, hierarchyInfoDTO.getCorpId());
                    updateStmt.setString(index, hierarchyInfoDTO.getHierarchyId());
                    updateStmt.addBatch();
                } else {
                    knLogger.info(methodName," Hierarchy ID does not exist, preparing to insert: ", hierarchyInfoDTO.getHierarchyId());
                    // Prepare insert statement
                    int index = 1;
                    insertStmt.setString(index++, hierarchyInfoDTO.getCorpId());
                    insertStmt.setString(index++, hierarchyInfoDTO.getHierarchyId());
                    insertStmt.setString(index++, hierarchyInfoDTO.getExternalHierarchyId());
                    insertStmt.setString(index++, hierarchyInfoDTO.getHierarchyName());
                    insertStmt.setString(index++, hierarchyInfoDTO.getHierarchyAlias());
                    insertStmt.setByte(index++, (byte) 1);
                    insertStmt.setString(index++, hierarchyInfoDTO.getSegmentIndicator());
                    if (hierarchyInfoDTO.getSubScrDefPttRadio() != null) {
                        insertStmt.setByte(index++, hierarchyInfoDTO.getSubScrDefPttRadio());
                    } else {
                        insertStmt.setObject(index++, null);
                    }
                    if (hierarchyInfoDTO.getFeatureVersion() != null) {
                        insertStmt.setInt(index++, hierarchyInfoDTO.getFeatureVersion());
                    } else {
                        insertStmt.setObject(index++, null);
                    }
                    if (hierarchyInfoDTO.getCreationTime() != null) {
                        insertStmt.setLong(index++, hierarchyInfoDTO.getCreationTime());
                    } else {
                        insertStmt.setObject(index++, null);
                    }
                    if (hierarchyInfoDTO.getUpdateTime() != null) {
                        insertStmt.setLong(index++, hierarchyInfoDTO.getUpdateTime());
                    } else {
                        insertStmt.setObject(index++, null);
                    }
                    if (hierarchyInfoDTO.getStatus() != null) {
                        insertStmt.setByte(index++, hierarchyInfoDTO.getStatus());
                    } else {
                        insertStmt.setObject(index++, null);
                    }
                    insertStmt.setString(index++, hierarchyInfoDTO.getCustomField1());
                    insertStmt.setString(index, hierarchyInfoDTO.getCustomField2());
                    insertStmt.addBatch();
                }
            }

            // Execute batch operations
            int[] ints = updateStmt.executeBatch();
            knLogger.info(methodName," Number of records updated: ", ints.length);
            int[] ints1 = insertStmt.executeBatch();
            knLogger.info(methodName," Number of records updated: ", ints1.length);
            knLogger.debug(methodName, "Exit: Insert/Update operations completed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert or update hierarchy info table " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, insertQuery + " / " + updateQuery);
        } finally {
            KnDbUtil.closePreparedStatement(insertStmt);
            KnDbUtil.closePreparedStatement(updateStmt);
        }
    }

    private Set<String> fetchExistingHierarchyIds(int corpId, Connection conn) throws SQLException, KnDAOException {
        final String methodName = "fetchExistingHierarchyIds(Connection)";
        knLogger.info(methodName," Fetching existing hierarchy IDs from database.");
        final String query = "SELECT HIERARCHY_ID FROM DG.CORP_HIERARCHY_DETAILS WHERE CORPID = ?";
        Set<String> hierarchyIds = new HashSet<>();
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        try {
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                hierarchyIds.add(rs.getString("HIERARCHY_ID"));
            }
        }catch (Exception e){
            knLogger.error(methodName," Error while fetching existing hierarchy IDs: ", e);
            throw KnDbUtil.processException(e, "Failed to insert or update hierarchy info table " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.info(methodName," Retrieved ", hierarchyIds.size(), " existing hierarchy IDs.:",hierarchyIds.toString());
        return hierarchyIds;
    }

    public void updateIntoHierarchyDetails(List<KnHierarchyInfoDTO> hierarchyInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateIntoHierarchyDetails(List<KnHierarchyInfoDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : hierarchyInfoDTOS - ", hierarchyInfoDTOS);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_INTO_HIERARCHY_DETAILS); // Assuming the query ID for update is defined
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (KnHierarchyInfoDTO hierarchyInfoDTO : hierarchyInfoDTOS) {
                int index = 1;
                // Required fields (NOT NULL)
                pstmt.setString(index++, hierarchyInfoDTO.getHierarchyName());
                pstmt.setString(index++, hierarchyInfoDTO.getHierarchyAlias());
                pstmt.setString(index++, hierarchyInfoDTO.getSegmentIndicator());
                pstmt.setByte(index++, hierarchyInfoDTO.getSubScrDefPttRadio() != null ? hierarchyInfoDTO.getSubScrDefPttRadio() : (byte) 0);
                pstmt.setInt(index++, hierarchyInfoDTO.getFeatureVersion() != null ? hierarchyInfoDTO.getFeatureVersion() : 0);
                pstmt.setLong(index++, hierarchyInfoDTO.getUpdateTime() != null ? hierarchyInfoDTO.getUpdateTime() : System.currentTimeMillis());
                pstmt.setByte(index++, hierarchyInfoDTO.getStatus() != null ? hierarchyInfoDTO.getStatus() : (byte) 1);
                pstmt.setString(index++, hierarchyInfoDTO.getCustomField1());
                pstmt.setString(index++, hierarchyInfoDTO.getCustomField2());
                pstmt.setString(index++, hierarchyInfoDTO.getCorpId()); // WHERE clause field
                pstmt.setString(index++, hierarchyInfoDTO.getHierarchyId()); // WHERE clause field
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to update hierarchy info table " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void insertIntoHierarchyDepth(List<KnHierarchyDepthInfoDTO> hierarchyDepthInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertIntoHierarchyDetails(List<KnHierarchyInfoDTO> , KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : hierarchyInfoDTOS - ", hierarchyDepthInfoDTOS);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_INTO_HIERARCHY_DEPTH_TABLE);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (KnHierarchyDepthInfoDTO hierarchyDepthInfoDTO : hierarchyDepthInfoDTOS) {
                int index = 1;
                pstmt.setString(index++, hierarchyDepthInfoDTO.getCorpId());
                pstmt.setString(index++, hierarchyDepthInfoDTO.getAncestorHierId());
                pstmt.setString(index++, hierarchyDepthInfoDTO.getDescendantHierId());
                pstmt.setInt(index++, hierarchyDepthInfoDTO.getDepth());
                if (hierarchyDepthInfoDTO.getCreationTime() != null) {
                    pstmt.setLong(index++, hierarchyDepthInfoDTO.getCreationTime());
                } else {
                    pstmt.setNull(index++, Types.BIGINT);
                }
                if (hierarchyDepthInfoDTO.getUpdateTime() != null) {
                    pstmt.setLong(index++, hierarchyDepthInfoDTO.getUpdateTime());
                } else {
                    pstmt.setNull(index++, Types.BIGINT);
                }
                if (hierarchyDepthInfoDTO.getCustomField1() != null) {
                    pstmt.setString(index++, hierarchyDepthInfoDTO.getCustomField1());
                } else {
                    pstmt.setNull(index++, Types.VARCHAR);
                }
                if (hierarchyDepthInfoDTO.getCustomField2() != null) {
                    pstmt.setString(index++, hierarchyDepthInfoDTO.getCustomField2());
                } else {
                    pstmt.setNull(index++, Types.VARCHAR);
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert into hierarchy depth table " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DEPTH, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void insertOrUpdateHierarchyDepth(List<KnHierarchyDepthInfoDTO> hierarchyDepthInfoDTOS, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertOrUpdateHierarchyDepth(List<KnHierarchyDepthInfoDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point: hierarchyDepthInfoDTOS - ", hierarchyDepthInfoDTOS);
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        String insertQuery = null;
        String updateQuery = null;
        Connection conn;

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            insertQuery = queryMapper.getQuery(INSERT_INTO_HIERARCHY_DEPTH_TABLE);
            updateQuery = queryMapper.getQuery(UPDATE_INTO_HIERARCHY_DEPTH_TABLE);

            knLogger.debug(methodName, "Insert Query: ", insertQuery);
            knLogger.debug(methodName, "Update Query: ", updateQuery);

            insertStmt = conn.prepareStatement(insertQuery);
            updateStmt = conn.prepareStatement(updateQuery);

            // Fetch all existing ancestor-descendant pairs
            Set<String> existingPairs = fetchExistingHierarchyDepthPairs(corpId, conn);
            knLogger.debug(methodName,"HierarchyDepthInfoDTO Size:",hierarchyDepthInfoDTOS.size());
            for (KnHierarchyDepthInfoDTO hierarchyDepthInfoDTO : hierarchyDepthInfoDTOS) {
                knLogger.debug(methodName,"parent ID:",hierarchyDepthInfoDTO.getAncestorHierId(),"ChildId:",hierarchyDepthInfoDTO.getDescendantHierId());
                String pairKey = hierarchyDepthInfoDTO.getAncestorHierId() + "|" + hierarchyDepthInfoDTO.getDescendantHierId();
                int index = 1;

                if (existingPairs.contains(pairKey)) {
                    knLogger.info(methodName,"Key already available");
                    // Prepare update statement
                    updateStmt.setInt(index++, hierarchyDepthInfoDTO.getDepth());
                    if (hierarchyDepthInfoDTO.getUpdateTime() != null) {
                        updateStmt.setLong(index++, hierarchyDepthInfoDTO.getUpdateTime());
                    } else {
                        updateStmt.setNull(index++, Types.BIGINT);
                    }
                    updateStmt.setString(index++, hierarchyDepthInfoDTO.getCustomField1());
                    updateStmt.setString(index++, hierarchyDepthInfoDTO.getCustomField2());
                    updateStmt.setInt(index++, Integer.parseInt(hierarchyDepthInfoDTO.getCorpId()));
                    updateStmt.setString(index++, hierarchyDepthInfoDTO.getAncestorHierId());
                    updateStmt.setString(index, hierarchyDepthInfoDTO.getDescendantHierId());
                    updateStmt.addBatch();
                } else {
                    knLogger.info(methodName,"New Child creation:");
                    // Prepare insert statement
                    insertStmt.setString(index++, hierarchyDepthInfoDTO.getCorpId());
                    insertStmt.setString(index++, hierarchyDepthInfoDTO.getAncestorHierId());
                    insertStmt.setString(index++, hierarchyDepthInfoDTO.getDescendantHierId());
                    insertStmt.setInt(index++, hierarchyDepthInfoDTO.getDepth());
                    if (hierarchyDepthInfoDTO.getCreationTime() != null) {
                        insertStmt.setLong(index++, hierarchyDepthInfoDTO.getCreationTime());
                    } else {
                        insertStmt.setNull(index++, Types.BIGINT);
                    }
                    if (hierarchyDepthInfoDTO.getUpdateTime() != null) {
                        insertStmt.setLong(index++, hierarchyDepthInfoDTO.getUpdateTime());
                    } else {
                        insertStmt.setNull(index++, Types.BIGINT);
                    }
                    if (hierarchyDepthInfoDTO.getCustomField1() != null) {
                        insertStmt.setString(index++, hierarchyDepthInfoDTO.getCustomField1());
                    } else {
                        insertStmt.setNull(index++, Types.VARCHAR);
                    }
                    if (hierarchyDepthInfoDTO.getCustomField2() != null) {
                        insertStmt.setString(index++, hierarchyDepthInfoDTO.getCustomField2());
                    } else {
                        insertStmt.setNull(index++, Types.VARCHAR);
                    }
                    insertStmt.addBatch();
                }
            }

            // Execute batch operations
            int[] updateCount = updateStmt.executeBatch();
            int[] insertCount = insertStmt.executeBatch();
            knLogger.debug(methodName, "Exit: Insert/Update operations completed successfully. Update count:", Arrays.stream(updateCount).sum(),"Insert count:",Arrays.stream(insertCount).sum());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert or update hierarchy depth table " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DEPTH, insertQuery + " / " + updateQuery);
        } catch (Exception e) {
            knLogger.info(methodName, "Error while Insert OR Update into Depth table: ", e);
                throw KnDbUtil.processException(e, "Failed to insert or update hierarchy depth table " + e,
                        pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DEPTH, insertQuery + " / " + updateQuery);
        } finally {
            KnDbUtil.closePreparedStatement(insertStmt);
            KnDbUtil.closePreparedStatement(updateStmt);
        }
    }

    private Set<String> fetchExistingHierarchyDepthPairs(int corpId, Connection conn) throws SQLException, KnDAOException {
        final String query = "SELECT ANCESTOR_HIER_ID, DESCENDANT_HIER_ID FROM DG.CORP_HIERARCHY_DEPTH WHERE CORPID = ?";
        Set<String> pairs = new HashSet<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, corpId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String pairKey = rs.getString("ANCESTOR_HIER_ID") + "|" + rs.getString("DESCENDANT_HIER_ID");
                    pairs.add(pairKey);
                }
            }
        } catch (Exception e) {
            knLogger.error("fetchExistingHierarchyDepthPairs", "Error while fetching existing hierarchy depth pairs: ", e);
            throw KnDbUtil.processException(e, "Failed to update hierarchy depth table " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DEPTH, query);
        }
        return pairs;
    }

    public void updateIntoHierarchyDepth(List<KnHierarchyDepthInfoDTO> hierarchyInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateIntoHierarchyDepth(List<KnHierarchyDepthInfoDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : hierarchyInfoDTOS - ", hierarchyInfoDTOS);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_INTO_HIERARCHY_DEPTH_TABLE);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (KnHierarchyDepthInfoDTO hierarchyDepthInfoDTO : hierarchyInfoDTOS) {
                int index = 1;
                pstmt.setString(index++, hierarchyDepthInfoDTO.getCorpId());
                pstmt.setString(index++, hierarchyDepthInfoDTO.getAncestorHierId());
                pstmt.setString(index++, hierarchyDepthInfoDTO.getDescendantHierId());
                pstmt.setInt(index++, hierarchyDepthInfoDTO.getDepth());
                if (hierarchyDepthInfoDTO.getCreationTime() != null) {
                    pstmt.setLong(index++, hierarchyDepthInfoDTO.getCreationTime());
                } else {
                    pstmt.setNull(index++, Types.BIGINT);
                }
                if (hierarchyDepthInfoDTO.getUpdateTime() != null) {
                    pstmt.setLong(index++, hierarchyDepthInfoDTO.getUpdateTime());
                } else {
                    pstmt.setNull(index++, Types.BIGINT);
                }
                if (hierarchyDepthInfoDTO.getCustomField1() != null) {
                    pstmt.setString(index++, hierarchyDepthInfoDTO.getCustomField1());
                } else {
                    pstmt.setNull(index++, Types.VARCHAR);
                }
                if (hierarchyDepthInfoDTO.getCustomField2() != null) {
                    pstmt.setString(index++, hierarchyDepthInfoDTO.getCustomField2());
                } else {
                    pstmt.setNull(index++, Types.VARCHAR);
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to update hierarchy depth table " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DEPTH, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public List<KnDeploySiteInfoDTO> getDeploySiteInfo(KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDeploySiteInfo(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : getDeploySiteInfo - ");
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        int index = 1;
        ResultSet resultSet = null;
        List<KnDeploySiteInfoDTO> deploySiteInfoDTOS = new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_DEPLOY_SITE_INFO);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            resultSet = pstmt.executeQuery();
            while(resultSet.next()){
                KnDeploySiteInfoDTO deploySiteInfoDTO = new KnDeploySiteInfoDTO();
                deploySiteInfoDTO.setClusterId(resultSet.getInt("CLUSTERID"));
                deploySiteInfoDTO.setDeploymentType(resultSet.getString("DEPLOYMENT_TYPE"));
                deploySiteInfoDTO.setClusterName(resultSet.getString("CLUSTER_NAME"));
                deploySiteInfoDTO.setClusterFqdn(resultSet.getString("CLUSTER_FQDN"));
                deploySiteInfoDTO.setDesc(resultSet.getString("DESC"));
                deploySiteInfoDTO.setCountryCode(resultSet.getString("COUNTRYCODE"));
                deploySiteInfoDTO.setIsRedundant(resultSet.getString("ISREDUNDANT"));
                int redundantClsId = resultSet.getInt("REDUNDANTCLSID");
                deploySiteInfoDTO.setRedundantClsId(resultSet.wasNull() ? null : redundantClsId);
                byte clusterType = resultSet.getByte("CLUSTER_TYPE");
                deploySiteInfoDTO.setClusterType(resultSet.wasNull() ? null : clusterType);
                deploySiteInfoDTO.setGeoCode(resultSet.getString("GEOCODE"));
                deploySiteInfoDTOS.add(deploySiteInfoDTO);
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getDeploySiteInfo " + e,
                    pttServerId, KnDAOSourceTypes.DEPLOY_SITE_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return deploySiteInfoDTOS;
    }

    public List<String> getExtIdNameInfo(List<String> extIdNameList, Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getExtIdNameInfo(List<String>, Integer, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : getExtIdNameInfo - ");
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        int index = 1;
        List<String> result = new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_HIERARCHY_NAME_LIST);
            query = replaceContactWithValue(query, HIERARCHY_NAME_LIST, formCommaSeperatedQuesMarks(extIdNameList));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);

            for(String mdn:extIdNameList){
                pstmt.setString(index++, mdn);
            }

            pstmt.setInt(index++, corpId);
            resultSet = pstmt.executeQuery();
            while(resultSet.next()){
                result.add(resultSet.getString(1));
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.", result);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to CORP_HIERARCHY_DETAILS " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return result;
    }


    public List<String> getDescendantExtIds(String extId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDescendantExtIds";
        List<String> descendantExtIds = new ArrayList<>();
        String query = null;
        try {
            query = "SELECT DESCENDANT_HIER_ID FROM DG.CORP_HIERARCHY_DEPTH WHERE ANCESTOR_HIER_ID = ?";
            try (PreparedStatement stmt = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false).prepareStatement(query)) {
                stmt.setString(1, extId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        descendantExtIds.add(rs.getString("DESCENDANT_HIER_ID"));
                    }
                }
            } catch (KnConnectionException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to CORP_HIERARCHY_DETAILS " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
        }
        return descendantExtIds;
    }

    public void deleteFromHierarchyDetails(List<String> descendantExtIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteFromHierarchyDetails";
        String query = null;
        try {
            query = "DELETE FROM DG.CORP_HIERARCHY_DETAILS WHERE HIERARCHY_ID = ?";
            try (PreparedStatement stmt = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false).prepareStatement(query)) {
                for (String extId : descendantExtIds) {
                    stmt.setString(1, extId);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to CORP_HIERARCHY_DETAILS " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        }
    }

    public void deleteFromHierarchyDepth(List<String> descendantExtIds, String parentHierarchyId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteFromHierarchyDepth";
        knLogger.info(methodName,"ENTRY Point: descendantExtIds - ", descendantExtIds, " parentHierarchyId - ", parentHierarchyId);
        String deleteQuery1 = null;
        String deleteQuery2 = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            deleteQuery1 = queryMapper.getQuery(DELETE_HIERARCHY_FOR_DESCENDANT);
            deleteQuery2 = queryMapper.getQuery(DELETE_HIERARCHY_FOR_ANCESTOR);

            try (PreparedStatement stmt1 = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, Boolean.TRUE).prepareStatement(deleteQuery1);
                 PreparedStatement stmt2 = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, Boolean.TRUE).prepareStatement(deleteQuery2)) {
                for (String hierarchyId : descendantExtIds) {
                    // For stmt1, we set the parameters for the direct deletion of the descendant hierarchy ID
                    stmt1.setString(1, hierarchyId);
                    stmt1.setInt(2, corpId);
                    stmt1.addBatch();
                    // For stmt2, we need to set the parameters for the subquery as well
                    stmt2.setInt(1, corpId);
                    stmt2.setString(2, hierarchyId);
                    stmt2.setString(3, hierarchyId);
                    stmt2.addBatch();
                }
                int[] result1 = stmt2.executeBatch();
                int[] result2 = stmt1.executeBatch();
                knLogger.info(methodName,"Deleted from hierarchy depth table. deleteQuery1:: Result1: ", Arrays.stream(result1).sum(), " deleteQuery2:: Result2: ", Arrays.stream(result2).sum());
            } catch (KnConnectionException e) {
                knLogger.error(methodName,"Error while deleting from hierarchy depth table: ", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            knLogger.error(methodName,"Error while deleting from hierarchy depth table: ", e);
            throw KnDbUtil.processException(e, "Failed to CORP_HIERARCHY_DETAILS " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, deleteQuery1 + " / " + deleteQuery2);
        } catch (Exception e) {
            knLogger.error(methodName,"Error while deleting from hierarchy depth table: ", e);
            throw e;
        }
    }
    public void insertIntoHierarchyGeocodeMap(List<KnCorpHierarchyGeocodeMapDTO> geocodeMapDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertIntoHierarchyGeocodeMap(List<CorpHierarchyGeocodeMapDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : geocodeMapDTOS - ", geocodeMapDTOS);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_INTO_HIERARCHY_GEOCODE_MAP);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (KnCorpHierarchyGeocodeMapDTO dto : geocodeMapDTOS) {
                int index = 1;
                pstmt.setInt(index++, dto.getCorpId());
                pstmt.setString(index++, dto.getHierarchyId());
                pstmt.setString(index++, dto.getGeocode());
                if (dto.getCreationTime() != null) {
                    pstmt.setLong(index++, dto.getCreationTime());
                } else {
                    pstmt.setNull(index++, Types.BIGINT);
                }
                if (dto.getUpdateTime() != null) {
                    pstmt.setLong(index++, dto.getUpdateTime());
                } else {
                    pstmt.setNull(index++, Types.BIGINT);
                }
                if (dto.getCustomField1() != null) {
                    pstmt.setString(index++, dto.getCustomField1());
                } else {
                    pstmt.setNull(index++, Types.VARCHAR);
                }
                if (dto.getCustomField2() != null) {
                    pstmt.setString(index++, dto.getCustomField2());
                } else {
                    pstmt.setNull(index++, Types.VARCHAR);
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert into hierarchy geocode map table " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_GEOCODE_MAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * <h6>Updates hierarchy attributes in the database.</h6>
     * <p>
     * This method updates specific attributes of a hierarchy record in the database
     * using the provided `KnModifiedIdDetailsListDTO` object. It prepares and executes
     * an SQL update statement to modify the hierarchy attributes.
     * </p>
     * @param modifiedDetail An object containing the details of the hierarchy attributes to be updated.
     *                       This includes the ID name, alias, and ID key.
     * @param persisterTxn   The transaction object used to manage the database connection and operations.
     * @throws KnDAOException If an error occurs during the database operation, a `KnDAOException` is thrown.
     */
    public void updateHierarchyAttributes(KnModifiedIdDetailsListDTO  modifiedDetail, boolean isAliasUpdateRequest, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateHierarchyAttributes(KnModifiedIdDetailsListDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : modifiedDetail - ", modifiedDetail);
        PreparedStatement pstmt = null;
        String query = "UPDATE DG.CORP_HIERARCHY_DETAILS SET HIERARCHY_NAME = NVL(?,HIERARCHY_NAME) WHERE HIERARCHY_ID = ? ";
        String aliasQuery = " UPDATE DG.CORP_HIERARCHY_DETAILS SET HIERARCHY_NAME = NVL(?,HIERARCHY_NAME) , HIERARCHY_ALIAS = ? WHERE HIERARCHY_ID = ? ";
        Connection conn;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            if (isAliasUpdateRequest) {
                pstmt = conn.prepareStatement(aliasQuery);
                pstmt.setString(1, modifiedDetail.getIdName());
                pstmt.setString(2, modifiedDetail.getAlias());
                pstmt.setString(3, modifiedDetail.getIdKey());
            } else {
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, modifiedDetail.getIdName());
                pstmt.setString(2, modifiedDetail.getIdKey());
            }
            int rowCount = pstmt.executeUpdate();
            knLogger.debug(methodName, "Row affected: ", rowCount);
        } catch (Exception e) {
            knLogger.error(methodName, "Error updating hierarchy attributes: ", e);
            throw KnDbUtil.processException(e, "Failed to insert into hierarchy geocode map table " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * <h6>Fetches all descendant hierarchy IDs from the database.</h6>
     *
     * @param persisterTxn The transaction object used to manage database operations.
     * @return A set of strings containing all descendant hierarchy IDs.
     * @throws SQLException If a database access error occurs.
     * @throws KnConnectionException If there is an issue with the database connection.
     */
    public Set<String> fetchAllDescendantHierarchyIds(int corpId, KnPersisterTxn persisterTxn) throws SQLException, KnDAOException {
        final String methodName = "fetchAllDescendantHierarchyIds(Connection)";
        knLogger.info(methodName, " Fetching all descendant hierarchy IDs from database.");
        Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
        return fetchExistingHierarchyIds(corpId, conn);
    }

    public void insertOrUpdateModifyHierarchyGeocode(Map<String,List<String>> addOrUpdateGeoCodes,int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertOrUpdateHierarchyGeocode(List<String>, String, Integer, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point: addOrUpdateGeoCodes - ", addOrUpdateGeoCodes);

        try {
            Set<String> listOfHierarchyIds = addOrUpdateGeoCodes.keySet();
            Map<String, List<String>> stringListMap = fetchAllGeocode(listOfHierarchyIds, corpId, persisterTxn);
            List<KnCorpHierarchyGeocodeMapDTO> geoDtoList = new ArrayList<>();
            // Filter the existing data
            for (Map.Entry<String, List<String>> entry : addOrUpdateGeoCodes.entrySet()) {
                String hierarchyId = entry.getKey();
                List<String> value = entry.getValue();
                List<String> listOfGeocode = stringListMap.get(hierarchyId);
                for (String geoCode : value) {
                    if (listOfGeocode == null || listOfGeocode.isEmpty() || !listOfGeocode.contains(geoCode)) {
                        KnCorpHierarchyGeocodeMapDTO dto = new KnCorpHierarchyGeocodeMapDTO();
                        dto.setCorpId(corpId);
                        dto.setHierarchyId(hierarchyId);
                        dto.setGeocode(geoCode);
                        geoDtoList.add(dto);
                    } else {
                        knLogger.warn(methodName, "Geocode ", geoCode, " already exists for hierarchyId ", hierarchyId, ". Skipping insertion.");
                    }
                }
            }
            insertIntoHierarchyGeocodeMap(geoDtoList, persisterTxn);
            knLogger.debug(methodName, "Exit: Insert operations completed successfully.");
        } catch (Exception e) {
            knLogger.error(methodName, "Error inserting or updating hierarchy geocode map: ", e);
            throw KnDbUtil.processException(e, "Failed to insert or update hierarchy geocode map table " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_GEOCODE_MAP, "InsertOrUpdateHierarchyGeocode");
        }
    }

    public void removeGeocodeMapping(int corpId, Map<String, List<String>> removeGeocode, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "removeGeocodeMapping(String, int, List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point: corpId - ", corpId, ", removeGeocode - ", removeGeocode);

        PreparedStatement pstmt = null;
        String query = "";
        Connection conn;

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();

            query = queryMapper.getQuery(DELETE_HIERARCHY_GEOCODE_MAP);
            knLogger.debug(methodName, "Executing query - ", query);
            knLogger.info(methodName, "Removing geocode mappings for corpId: ", corpId, ", geocodes: ", removeGeocode);
            pstmt = conn.prepareStatement(query);
            for (Map.Entry<String, List<String>> entry : removeGeocode.entrySet()) {
                String hierarchyId = entry.getKey();
                List<String> geoCodesToRemove = entry.getValue();
                if (geoCodesToRemove == null || geoCodesToRemove.isEmpty()) {
                    knLogger.warn(methodName, "No geocodes provided for hierarchyId ", hierarchyId, ". Skipping deletion for this hierarchy.");
                    continue;
                }
                for (String geoCode : geoCodesToRemove) {
                    pstmt.setString(1, hierarchyId);
                    pstmt.setString(2, String.valueOf(corpId));
                    pstmt.setString(3, geoCode);
                    pstmt.addBatch();
                }

                knLogger.info(methodName, "Hierarchy ID: ", hierarchyId, ", Geocodes to remove: ", geoCodesToRemove);
            }
            int[] result = pstmt.executeBatch();
            int sum = Arrays.stream(result).sum();
            knLogger.debug(methodName, "Number of geocode mappings removed: ", sum);
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException | KnConnectionException e) {
            knLogger.error(methodName, "Error removing geocode mapping: ", e);
            throw KnDbUtil.processException(e, "Failed while removing Geocode  " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_GEOCODE_MAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<String,List<String>> fetchAllGeocode(Set<String> listOfHierarchyId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "fetchAllGeocode(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point: hierarchyId - ", listOfHierarchyId, ", corpId - ", corpId);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        final String HIERARCHY_ID = "HIERARCHY_ID", GEOCODE = "GEOCODE";
        Map<String, List<String>> geoCodeMap = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_HIERARCHY_GEOCODE_MAP);
            query = replaceContactWithValue(query, HIERARCHY_ID_LIST, formCommaSeperatedQuesMarks(listOfHierarchyId));
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            int index = 2;
            for (String hierarchyId : listOfHierarchyId) {
                pstmt.setString(index++, hierarchyId);
            }
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String hierarchyIdKey = resultSet.getString(HIERARCHY_ID);
                geoCodeMap.computeIfAbsent(hierarchyIdKey, k -> new ArrayList<>())
                        .add(resultSet.getString(GEOCODE));
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.", geoCodeMap);
            return geoCodeMap;
        } catch (SQLException e) {
            knLogger.error(methodName, "Error fetching geocode map: ", e);
            throw KnDbUtil.processException(e, "Failed to fetch hierarchy geocode map table " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_GEOCODE_MAP, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * Retrieves geocode list for a specific hierarchy within a corporation.
     * Used by getRegions API in XDM layer when hierarchyId IS provided.
     * 
     * @param corpId The corporate ID
     * @param hierarchyId The hierarchy ID
     * @param persisterTxn The transaction context
     * @return List of geocode strings for the specific hierarchy
     * @throws KnDAOException if database operation fails
     */
    public List<String> getRegionsByHierarchyId(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getRegionsByHierarchyId(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point: corpId - ", corpId, ", hierarchyId - ", hierarchyId);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        List<String> geoCodeList = new ArrayList<>();

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_REGIONS_BY_HIERARCHY_ID);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setString(2, hierarchyId);

            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                geoCodeList.add(resultSet.getString("GEOCODE"));
            }

            knLogger.debug(methodName, "Exit: GeoCodeList size - ", geoCodeList.size());
            return geoCodeList;

        } catch (SQLException e) {
            knLogger.error(methodName, "Error fetching geocodes for hierarchyId: ", hierarchyId, ", corpId: ", corpId, " - ", e);
            throw KnDbUtil.processException(e, "Failed to fetch regions from CORP_HIERARCHY_GEOCODE_MAPPING: " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_GEOCODE_MAP, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * Retrieves all regions from DEPLOY_SITE_INFO table.
     * Used by getRegions API in XDM layer when hierarchyId is NOT provided.
     * 
     * @param corpId The corporate ID (used for logging, not in query)
     * @param persisterTxn The transaction context
     * @return List of all geocode strings from DEPLOY_SITE_INFO
     * @throws KnDAOException if database operation fails
     */
    public List<String> getAllRegions(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getAllRegions(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point: corpId - ", corpId);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        List<String> geoCodeList = new ArrayList<>();

        try {
            conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_ALL_REGIONS);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);

            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                geoCodeList.add(resultSet.getString("GEOCODE"));
            }

            knLogger.debug(methodName, "Exit: Total regions fetched - ", geoCodeList.size());
            return geoCodeList;

        } catch (SQLException e) {
            knLogger.error(methodName, "Error fetching all regions from DEPLOY_SITE_INFO for corpId: ", corpId, " - ", e);
            throw KnDbUtil.processException(e, "Failed to fetch all regions from DEPLOY_SITE_INFO: " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.DEPLOY_SITE_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<String,Integer> fetchAllHierarchyDepths(String corpId,String descendantId,KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "fetchAllHierarchyDepths(corpId,descendantId)";
        knLogger.debug(methodName, "ENTRY Point: corpId: - ", corpId, ", descendantId - ", descendantId);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        final String Column1 = "ANCESTOR_HIER_ID";
        final String Column2 = "DEPTH";
        Map<String, Integer> hierarchyDepthMap = new ConcurrentHashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_HIERARCHY_DEPTH);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, corpId);
            pstmt.setString(2, descendantId);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String ancestorHerId = resultSet.getString(Column1);
                int depth = resultSet.getInt(Column2);
                hierarchyDepthMap.put(ancestorHerId, depth);
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.", hierarchyDepthMap);
            return hierarchyDepthMap;
        } catch (SQLException e) {
            knLogger.error(methodName, "Error fetching all hierarchy depths: ", e);
            throw KnDbUtil.processException(e, "Failed to fetch all hierarchy depths " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DEPTH, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public KnHierarchyDepthInfoDTO getRootNodeBasedOnCorpId(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getRootNodeBasedOnCorpId(Integer, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : getExtIdNameInfo - ");
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        int index = 1;
        String idKey = null;
        KnHierarchyDepthInfoDTO hierarchyDepthInfoDTO = new KnHierarchyDepthInfoDTO();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "SELECT CORPID, ANCESTOR_HIER_ID, DESCENDANT_HIER_ID, DEPTH, CUSTOM_FIELD_1 " +
                    "FROM DG.CORP_HIERARCHY_DEPTH WHERE CORPID = ? AND DEPTH = 0 ";

            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            resultSet = pstmt.executeQuery();
            while(resultSet.next()){
                hierarchyDepthInfoDTO.setCorpId(resultSet.getString("CORPID"));
                hierarchyDepthInfoDTO.setAncestorHierId(resultSet.getString("ANCESTOR_HIER_ID"));
                hierarchyDepthInfoDTO.setDescendantHierId(resultSet.getString("DESCENDANT_HIER_ID"));
                hierarchyDepthInfoDTO.setTransactionId(resultSet.getString("CUSTOM_FIELD_1"));
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.", hierarchyDepthInfoDTO);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to CORP_HIERARCHY_DETAILS " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return hierarchyDepthInfoDTO;
    }

    public Map<String, KnHierarchyDepthInfoDTO> getChileNodesBasedOnHierarchyIds(List<String> hierarchyIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getChileNodesBasedOnHierarchyIds(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : hierarchyIds - ", hierarchyIds);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        Map<String, KnHierarchyDepthInfoDTO> childNodesMap = new HashMap<>();
        try {
            if (hierarchyIds == null || hierarchyIds.isEmpty()) {
                knLogger.debug(methodName, "hierarchyIds is null or empty, returning empty map");
                return childNodesMap;
            }

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            // Build query with placeholder for IN clause
            query = "SELECT CORPID, ANCESTOR_HIER_ID, DESCENDANT_HIER_ID, DEPTH " +
                    "FROM DG.CORP_HIERARCHY_DEPTH WHERE ANCESTOR_HIER_ID IN(" + HIERARCHY_ID_LIST + ")";

            // Replace placeholder with question marks
            query = replaceContactWithValue(query, HIERARCHY_ID_LIST, formCommaSeperatedQuesMarks(hierarchyIds));

            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);

            // Set the hierarchy IDs in the prepared statement
            int index = 1;
            for (String hierarchyId : hierarchyIds) {
                pstmt.setString(index++, hierarchyId);
            }

            resultSet = pstmt.executeQuery();
            while(resultSet.next()){
                KnHierarchyDepthInfoDTO hierarchyDepthInfoDTO = new KnHierarchyDepthInfoDTO();
                hierarchyDepthInfoDTO.setCorpId(resultSet.getString("CORPID"));
                hierarchyDepthInfoDTO.setAncestorHierId(resultSet.getString("ANCESTOR_HIER_ID"));
                hierarchyDepthInfoDTO.setDescendantHierId(resultSet.getString("DESCENDANT_HIER_ID"));
                hierarchyDepthInfoDTO.setDepth(resultSet.getInt("DEPTH"));

                // Use descendant ID as the key
                childNodesMap.put(hierarchyDepthInfoDTO.getDescendantHierId(), hierarchyDepthInfoDTO);
            }
            knLogger.debug(methodName, "Exit: Query executed successfully. Found ", childNodesMap.size(), " child nodes");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to query CORP_HIERARCHY_DEPTH " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return childNodesMap;
    }

    /**
     * <h5>Retrieves a mapping of hierarchy IDs to their corresponding names for a given corporation ID.</h5>
     *
     * <p>This method fetches the hierarchy ID-name pairs from the database for the specified corporation ID.
     * It uses a prepared statement to execute the query and stores the results in a map. If an error occurs
     * during the database operation, it throws a `KnDAOException`.</p>
     *
     * @param corpId       The ID of the corporation for which the hierarchy ID-name mapping is to be fetched.
     * @param persisterTxn The transaction object used to manage the database connection and operations.
     * @return A map where the keys are hierarchy IDs and the values are their corresponding names.
     * @throws KnDAOException If an error occurs during the database operation.
     */
    public  Map<String,String> getHierarchyIdNameAndIdList(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getHierarchyIdNameList(Integer, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : getHierarchyIdNameList - ");
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        Map<String,String> hierarchyIdNameMap = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(FETCH_EXISTING_ID_NAME);
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                hierarchyIdNameMap.putIfAbsent( resultSet.getString(2),resultSet.getString(1));
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.", hierarchyIdNameMap);
        } catch (SQLException e) {
            knLogger.error(methodName, "Error fetching hierarchy ID name list: ", e);
            throw KnDbUtil.processException(e, "Failed to fetch HierarchyIdName " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unknown exception occurred ", e);
            throw KnDbUtil.processException(e, "Unknown exception while fetch HierarchyIdName " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "Exit Point : getHierarchyIdNameList - ");
        return hierarchyIdNameMap;
    }
    /**
     * <h5>Retrieves a mapping of hierarchy IDs to their corresponding names for a given corporation ID.</h5>
     *
     * <p>This method fetches the hierarchy ID-name pairs from the database for the specified corporation ID.
     * It uses a prepared statement to execute the query and stores the results in a map. If an error occurs
     * during the database operation, it throws a `KnDAOException`.</p>
     *
     * @param corpId       The ID of the corporation for which the hierarchy ID-name mapping is to be fetched.
     * @param persisterTxn The transaction object used to manage the database connection and operations.
     * @return A map where the keys are hierarchy IDs and the values are their corresponding names.
     * @throws KnDAOException If an error occurs during the database operation.
     */
    public  Map<String,String> getHierarchyIdNameList(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getHierarchyIdNameList(Integer, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : getHierarchyIdNameList - ");
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        Map<String,String> hierarchyIdNameMap = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(FETCH_EXISTING_ID_NAME);
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                hierarchyIdNameMap.putIfAbsent( resultSet.getString(1),resultSet.getString(2));
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.", hierarchyIdNameMap);
        } catch (SQLException e) {
            knLogger.error(methodName, "Error fetching hierarchy ID name list: ", e);
            throw KnDbUtil.processException(e, "Failed to fetch HierarchyIdName " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unknown exception occurred ", e);
            throw KnDbUtil.processException(e, "Unknown exception while fetch HierarchyIdName " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "Exit Point : getHierarchyIdNameList - ");
        return hierarchyIdNameMap;
    }

    /**
     * Batch-fetches hierarchy names for a given set of hierarchy IDs.
     * Returns a map of hierarchyId → hierarchyName.
     * IDs not found in the table are simply absent from the map.
     */
    public Map<String, String> getHierarchyNamesByIds(List<String> hierarchyIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getHierarchyNamesByIds(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY hierarchyIds=", hierarchyIds);
        Map<String, String> result = new HashMap<>();
        if (hierarchyIds == null || hierarchyIds.isEmpty()) {
            return result;
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            String placeholders = formCommaSeperatedQuesMarks(hierarchyIds);
            query = "SELECT HIERARCHY_ID, HIERARCHY_NAME FROM DG.CORP_HIERARCHY_DETAILS WHERE HIERARCHY_ID IN (" + placeholders + ")";
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (int i = 0; i < hierarchyIds.size(); i++) {
                pstmt.setString(i + 1, hierarchyIds.get(i));
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("HIERARCHY_ID");
                String name = rs.getString("HIERARCHY_NAME");
                if (id != null) {
                    result.put(id, name != null ? name : "");
                }
            }
            knLogger.debug(methodName, "EXIT result.size=", result.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch hierarchy names by IDs: " + e,
                    pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return result;
    }

    /**
     * Fetches the maximum hierarchy ID from the hierarchy_details table.
     * Returns the largest numeric hierarchy ID stored in the database.
     * Used for initializing the sequential ID counter on application startup.
     * 
     * @param persisterTxn Database transaction context
     * @return Maximum hierarchy ID as Long, or null if table is empty
     * @throws KnDAOException if database query fails
     */
    public Long getMaxHierarchyId(KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getMaxHierarchyId(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Fetching maximum hierarchy ID from database");
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        Long maxId = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            // TimesTen does not support regex operator '~'.
            // IDs are stored as zero-padded 8-digit strings, so string MAX is equivalent to numeric MAX.
            query = "SELECT MAX(HIERARCHY_ID) AS MAX_ID FROM DG.CORP_HIERARCHY_DETAILS "
                    + "WHERE HIERARCHY_ID BETWEEN '00000000' AND '99999999' AND LENGTH(HIERARCHY_ID) = 8";
            knLogger.debug(methodName, "Executing query: " + query);
            pstmt = conn.prepareStatement(query);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                String maxIdStr = resultSet.getString("MAX_ID");
                if (maxIdStr != null && !maxIdStr.trim().isEmpty()) {
                    maxId = Long.parseLong(maxIdStr);
                } else {
                    maxId = null;
                }
            }
            knLogger.info(methodName, "Maximum hierarchy ID from database: " + maxId);
        } catch (SQLException e) {
            knLogger.error(methodName, "Error fetching max hierarchy ID: ", e);
            throw KnDbUtil.processException(e, "Failed to fetch max hierarchy ID: " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception: ", e);
            throw KnDbUtil.processException(e, "Unexpected exception while fetching max hierarchy ID: " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT: Returning maxId = " + maxId);
        return maxId;
    }

    /**
     * Fetches all hierarchy IDs with their corresponding depths for a given corporate and descendant ID.
     * Returns a map of hierarchy IDs to their depths from the hierarchy_depth table.
     * 
     * @param corpId Corporate ID to filter by
     * @param descendantId Specific descendant hierarchy ID to query
     * @param persisterTxn Database transaction context
     * @return Map of hierarchy IDs to their depths
     * @throws KnDAOException if database query fails
     */
    public Map<String, Integer> fetchAllHierarchyWithDepth(String corpId, String descendantId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "fetchAllHierarchyWithDepth(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: corpId=" + corpId + ", descendantId=" + descendantId);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        Map<String, Integer> hierarchyDepthMap = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            // Query to fetch all hierarchy entries with their depths for a given descendant
            query = "SELECT ANCESTOR_HIER_ID, DEPTH FROM DG.CORP_HIERARCHY_DEPTH WHERE CORPID = ? AND DESCENDANT_HIER_ID = ?";
            knLogger.debug(methodName, "Executing query: " + query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, corpId);
            pstmt.setString(2, descendantId);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String ancestorId = resultSet.getString("ANCESTOR_HIER_ID");
                int depth = resultSet.getInt("DEPTH");
                hierarchyDepthMap.put(ancestorId, depth);
            }
            knLogger.info(methodName, "Fetched " + hierarchyDepthMap.size() + " hierarchy depth entries");
        } catch (SQLException e) {
            knLogger.error(methodName, "Error fetching hierarchy depth information: ", e);
            throw KnDbUtil.processException(e, "Failed to fetch hierarchy depth info: " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception: ", e);
            throw KnDbUtil.processException(e, "Unexpected exception while fetching hierarchy depth info: " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT: Returning " + hierarchyDepthMap.size() + " entries");
        return hierarchyDepthMap;
    }

    public  Map<String,Map<String,Integer>> fetchMaxChildLengthAndDepth(List<String> hierarchyId, String corpId, KnPersisterTxn persistenceTxn) throws KnDAOException {
        final String methodName = "fetchMaxChildLengthAndDepth(hierarchyId, persistenceTxn)";
        knLogger.debug(methodName, "ENTRY Point : fetchMaxChildLengthAndDepth - ");
        PreparedStatement horizontalPStmt = null;
        PreparedStatement verticalPStmt = null;
        String horizontalLengthQuery;
        String verticalLengthQuery;
        Connection conn;
        ResultSet horizonalResultSet = null;
        ResultSet verticalResultSet = null;
        Map<String, Integer> horizontalLength = new HashMap<>();
        Map<String, Integer> verticalLength = new HashMap<>();
        Map<String, Map<String, Integer>> hierarchyIdLengthMap = new HashMap<>();
        try {
            conn = persistenceTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();

            horizontalLengthQuery = queryMapper.getQuery(FETCH_HIERARCHY_HORIZONTAL_LENGTH);
            verticalLengthQuery = queryMapper.getQuery(FETCH_HIERARCHY_VERTICAL_LENGTH);
            knLogger.debug(methodName, "horizontalLengthQuery", horizontalLengthQuery);
            knLogger.debug(methodName, "verticalLengthQuery", verticalLengthQuery);

            horizontalLengthQuery = replaceContactWithValue(horizontalLengthQuery, HIERARCHY_ID_LIST, formCommaSeperatedQuesMarks(hierarchyId));
            verticalLengthQuery = replaceContactWithValue(verticalLengthQuery, HIERARCHY_ID_LIST, formCommaSeperatedQuesMarks(hierarchyId));

            horizontalPStmt = conn.prepareStatement(horizontalLengthQuery);
            verticalPStmt = conn.prepareStatement(verticalLengthQuery);

            horizontalPStmt.setString(1, corpId);
            verticalPStmt.setString(1, corpId);
            int index = 2;
            for (String herId : hierarchyId) {
                horizontalPStmt.setString(index, herId);
                verticalPStmt.setString(index, herId);
                index++;
            }
            horizonalResultSet = horizontalPStmt.executeQuery();
            verticalResultSet = verticalPStmt.executeQuery();

            while (horizonalResultSet.next()) {
                horizontalLength.putIfAbsent(horizonalResultSet.getString(ANCESTOR_HIER_ID), horizonalResultSet.getInt(MAX_NODE_COUNT));
            }
            while (verticalResultSet.next()) {
                verticalLength.putIfAbsent(verticalResultSet.getString(DESCENDANT_HIER_ID), verticalResultSet.getInt(MAX_DEPTH));
            }
            hierarchyIdLengthMap.put(MAX_HORIZONTAL_LENGTH, horizontalLength);
            hierarchyIdLengthMap.put(MAX_VERTICAL_DEPTH, verticalLength);
            knLogger.debug(methodName, "Exit: Query executed successfully.", hierarchyIdLengthMap);
        } catch (SQLException e) {
            knLogger.error(methodName, "Error fetching hierarchy horizontal and vertical lengths: ", e);
            throw KnDbUtil.processException(e, "Failed to fetch Hierarchy depth" + e, pttServerId, KnDAOSourceTypes.MAX_HIERARCHY_DEPTH_AND_CHILD_LENGTH, null);
        } catch (Exception e) {
            knLogger.error(methodName, "Unknown exception occurred ", e);
            throw KnDbUtil.processException(e, "Failed to fetch Hierarchy depth " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DETAILS, null);
        } finally {
            KnDbUtil.closeResultSet(horizonalResultSet);
            KnDbUtil.closeResultSet(verticalResultSet);
            KnDbUtil.closePreparedStatement(horizontalPStmt);
            KnDbUtil.closePreparedStatement(verticalPStmt);
        }
        knLogger.debug(methodName, "Exit Point : fetchMaxChildLengthAndDepth - ");

        return hierarchyIdLengthMap;
    }

    /**
     * Fetches all child depth information for a given ancestor hierarchy ID.
     * <p>
     * This method retrieves the depth information of all descendant hierarchy nodes
     * for a specified ancestor hierarchy ID and corporate ID. The data is fetched
     * from the database using a prepared SQL query.
     * </p>
     *
     * @param ancestorId  The ancestor hierarchy ID for which child depth information is to be fetched.
     * @param corpId      The corporate ID used to filter the query results.
     * @param persisterTxn The transaction object used to manage the database connection and operations.
     * @return A list of `KnHierarchyDepthInfoDTO` objects containing the descendant hierarchy IDs and their depths.
     *         If no data is found, an empty list is returned.
     * @throws KnDAOException If an error occurs during the database operation, such as a SQL exception.
     */
    public List<KnHierarchyDepthInfoDTO> fetchAllChildDepthInfoForAncestor(String ancestorId, String corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "fetchAllChildDepthInfoForAncestor(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point: ancestorId - ", ancestorId, ", corpId - ", corpId);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        List<KnHierarchyDepthInfoDTO> childDepthInfoList = new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, Boolean.TRUE);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(FETCH_CHILD_DEPTH_OF_ANCESTOR);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, corpId);
            pstmt.setString(2, ancestorId);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                KnHierarchyDepthInfoDTO hierarchyDepthInfoDTO = new KnHierarchyDepthInfoDTO();
                hierarchyDepthInfoDTO.setDescendantHierId(resultSet.getString("DESCENDANT_HIER_ID"));
                hierarchyDepthInfoDTO.setDepth(resultSet.getInt("DEPTH"));
                childDepthInfoList.add(hierarchyDepthInfoDTO);
            }
            knLogger.debug(methodName, "Exit: Query executed successfully. Child depth info count: ", childDepthInfoList.size());
            return childDepthInfoList;
        } catch (SQLException e) {
            knLogger.error(methodName, "Error fetching child depth info for ancestor: ", e);
            throw KnDbUtil.processException(e, "Failed to fetch child depth info for ancestor " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DEPTH, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception: ", e);
            throw KnDbUtil.processException(e, "Unexpected error while fetching child depth info for ancestor " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DEPTH, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * Fetches the maximum child depth for a list of ancestor hierarchy IDs.
     * <p>
     * This method retrieves the maximum depth of child nodes for each ancestor ID
     * provided in the input list. It queries the database to fetch the depth information
     * and returns a map where the keys are ancestor IDs and the values are their respective
     * maximum child depths.
     * </p>
     *
     * @param ancestorIds A list of ancestor hierarchy IDs for which the maximum child depth is to be fetched.
     *                    If the list is null or empty, an empty map is returned.
     * @param corpId      The corporate ID used to filter the query results.
     * @param persisterTxn The transaction object used to manage the database connection and operations.
     * @return A map where the keys are ancestor hierarchy IDs and the values are their maximum child depths.
     *         If no data is found, an empty map is returned.
     * @throws KnDAOException If an error occurs during the database operation, such as a SQL exception.
     */
    public Map<String, Integer> fetchMaxChildDepthForAncestors(List<String> ancestorIds, String corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "fetchMaxChildDepthForAncestors(List<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point: ancestorIds - ", ancestorIds, ", corpId - ", corpId);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        Map<String, Integer> maxChildDepthMap = new HashMap<>();
        try {
            if (ancestorIds == null || ancestorIds.isEmpty()) {
                knLogger.debug(methodName, "ancestorIds is null or empty, returning empty map");
                return maxChildDepthMap;
            }

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, Boolean.TRUE);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(FETCH_MAX_CHILD_DEPTH_OF_ANCESTOR);
            query = replaceContactWithValue(query, HIERARCHY_ID_LIST, formCommaSeperatedQuesMarks(ancestorIds));
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, corpId);
            int index = 2;
            for (String ancestorId : ancestorIds) {
                pstmt.setString(index++, ancestorId);
            }
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String ancestorId = resultSet.getString("ANCESTOR_HIER_ID");
                int maxChildDepth = resultSet.getInt("MAX_CHILD_DEPTH");
                maxChildDepthMap.put(ancestorId, maxChildDepth);
            }
            knLogger.debug(methodName, "Exit: Query executed successfully. Max child depth map size: ", maxChildDepthMap.size());
            return maxChildDepthMap;
        } catch (SQLException e) {
            knLogger.error(methodName, "Error fetching max child depth for ancestors: ", e);
            throw KnDbUtil.processException(e, "Failed to fetch max child depth for ancestors " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DEPTH, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception: ", e);
            throw KnDbUtil.processException(e, "Unexpected error while fetching max child depth for ancestors " + e, pttServerId, KnDAOSourceTypes.CORP_HIERARCHY_DEPTH, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * Retrieves the root hierarchy name for a given corporation ID.
     * <p>
     * This method fetches the root hierarchy name from the database for the specified corporation ID.
     * It uses a prepared statement to execute the query and returns the root hierarchy name if found.
     * </p>
     *
     * @param corpId       The ID of the corporation for which the root hierarchy name is to be fetched.
     * @param persisterTxn The transaction object used to manage the database connection and operations.
     * @return The root hierarchy name as a `String`, or `null` if no root hierarchy name is found.
     * @throws KnDAOException If an error occurs during the database operation, such as a SQL exception.
     */
    public Map<String,String> getRootHierarchyName(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getRootHierarchyName(Integer, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point: corpId - ", corpId);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        Map<String,String> rootHierarchyDetails = new HashMap<>();
        try {
            // Obtain a database connection for the specified data store
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, Boolean.TRUE);
            // Retrieve the query for fetching the root hierarchy name
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(FETCH_ROOT_HIERARCHY_NAME);
            knLogger.debug(methodName, "Executing query - ", query);
            // Prepare the SQL statement and set the corporation ID parameter
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);

            // Execute the query and fetch the result
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                rootHierarchyDetails.put(KnConstants.ROOT_HIERARCHY_NAME, resultSet.getString("HIERARCHY_NAME"));
                rootHierarchyDetails.put(KnConstants.ROOT_HIERARCHY_ID, resultSet.getString("HIERARCHY_ID"));
            }
            return rootHierarchyDetails;
        } catch (SQLException e) {
            // Log and throw an exception if a SQL error occurs
            knLogger.error(methodName, "Error fetching max child depth for ancestors: ", e);
            throw KnDbUtil.processException(e, "Failed to fetch Root Hierarchy Name " + e, pttServerId, KnDAOSourceTypes.ROOT_HIERARCHY_NAME, query);
        } catch (Exception e) {
            // Log and throw an exception for any unexpected errors
            knLogger.error(methodName, "Unexpected exception: ", e);
            throw KnDbUtil.processException(e, "Unexpected error while fetching root hierarchy name " + e, pttServerId, KnDAOSourceTypes.ROOT_HIERARCHY_NAME, query);
        } finally {
            // Ensure the prepared statement and result set are closed
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * Retrieves the subscriber count based on hierarchy cluster IDs.
     * <p>
     * This method fetches the subscriber count for each hierarchy ID and cluster ID combination
     * from the database. It uses a prepared SQL query to retrieve the data and returns a map
     * where the keys are hierarchy IDs, and the values are maps of cluster IDs to subscriber counts.
     * </p>
     *
     * @param hierarchyIdList A set of hierarchy IDs for which the subscriber count is to be fetched.
     *                        If the set is empty, no data will be fetched.
     * @param corpId          The corporate ID used to filter the query results.
     * @param persistence     The transaction object used to manage the database connection and operations.
     * @return A map where the keys are hierarchy IDs, and the values are maps of cluster IDs to subscriber counts.
     *         If no data is found, an empty map is returned.
     * @throws KnDAOException If an error occurs during the database operation, such as a SQL exception.
     */
    public Map<String,Map<Integer,Integer>> getSubscriberCountBasedOnHierarchyClusterId(Set<String> hierarchyIdList, int corpId,
                                                                                       KnPersisterTxn persistence) throws KnDAOException {
        final String methodName = "getSubscriberCountBasedOnHierarchyClusterId(hierarchyIdList, persisterTxn)";
        knLogger.debug(methodName, "ENTRY Point: corpId - ", hierarchyIdList);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        ResultSet resultSet = null;
        Map<String, Map<Integer, Integer>> rootHierarchyDetails = new HashMap<>();
        try {
            List<String> input = new ArrayList<>(hierarchyIdList);
            // Obtain a database connection for the specified data store
            conn = persistence.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, Boolean.TRUE);
            // Retrieve the query for fetching the root hierarchy name
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(FETCH_HIERARCHY_GEO_SUB_COUNT);
            query = replaceContactWithValue(query, HIERARCHY_ID_LIST, formCommaSeperatedQuesMarks(hierarchyIdList));
            knLogger.debug(methodName, "Executing query - ", query);
            // Prepare the SQL statement and set the corporation ID parameter
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            int index = 2;
            for (String param : input) {
                pstmt.setString(index++, param);
            }

            // Execute the query and fetch the result
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                rootHierarchyDetails.computeIfAbsent(resultSet.getString("HIERARCHY_ID"), k -> new HashMap<>())
                        .put(resultSet.getInt("CLUSTERID"), resultSet.getInt("SUBSCRIBER_COUNT"));
            }
            return rootHierarchyDetails;
        } catch (SQLException e) {
            // Log and throw an exception if a SQL error occurs
            knLogger.error(methodName, "SQLException occurred during subscriber count fetching ", e);
            throw KnDbUtil.processException(e, "Failed to fetch Subscriber count for the hierarchy-cluster " + e, pttServerId, KnDAOSourceTypes.FETCH_SUBSCRIBER_COUNT_PER_HIERARCHY_CLUSTER, query);
        } catch (Exception e) {
            // Log and throw an exception for any unexpected errors
            knLogger.error(methodName, "Unexpected exception: ", e);
            throw KnDbUtil.processException(e, "Unexpected error while fetching subscriber " + e, pttServerId, KnDAOSourceTypes.FETCH_SUBSCRIBER_COUNT_PER_HIERARCHY_CLUSTER, query);
        } finally {
            // Ensure the prepared statement and result set are closed
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
}