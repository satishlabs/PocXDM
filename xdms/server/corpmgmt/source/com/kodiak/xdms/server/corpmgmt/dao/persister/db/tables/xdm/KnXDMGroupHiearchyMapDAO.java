/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.kodiak.xdms.server.common.dao.persister.db.tables.xdm.KnXDMCorpInfoDAO.formIntegerCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;


public class KnXDMGroupHiearchyMapDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMGroupHiearchyMapDAO.class);
    public String pttServerId = null;

    private final String GROUP_IDS="GROUP_IDS";
    private final String CORPGROUPID="CORPGROUPID";
    private final String ID_VALUE="ID_VALUE";
    private final String ID_VALUELIST="ID_VALUELIST";
    private final String ID_TYPE = "ID_TYPE";
    private final String ID_TYPE_STRING = "ID_TYPE_STRING";

    public KnXDMGroupHiearchyMapDAO(String pttServerId) {
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
        return null;
    }

    public void insertGroupHiearchyMap(Integer groupId, List<String> ownerFanIds, String idType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertGroupHiearchyMap()";
        knLogger.debug(methodName, "Entry groupId:",groupId," ownerFanIds :",ownerFanIds," idType :",idType);
        PreparedStatement pstmt = null;
        String query = null;
        try{
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_GROUP_HIERARCHY_MAP);
            knLogger.debug( methodName, "Executing query -" , "'" , query , "'");
            pstmt = conn.prepareStatement(query);
            for(String ownerFanId:ownerFanIds){
                pstmt.setInt(1, groupId);
                pstmt.setInt(2, Integer.parseInt(ownerFanId));
                pstmt.setInt(3, Integer.parseInt(idType));
                pstmt.addBatch();
            }
            pstmt.executeBatch();

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while inserting  - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured while inserting - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    pttServerId, INSERT_GROUP_HIERARCHY_MAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void insertGroupHiearchyMap(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, List<String> ownerFanIds, String idType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertGroupHiearchyMap()";
        knLogger.debug(methodName, "Entry groupInfoPersistDTOList:", groupInfoPersistDTOList, " ownerFanIds :", ownerFanIds, " idType :", idType);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_GROUP_HIERARCHY_MAP);
            knLogger.debug(methodName, "Executing query -", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            for (KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO : groupInfoPersistDTOList) {
                for (String ownerFanId : ownerFanIds) {
                    pstmt.setInt(1, corpGroupInfoPersistDTO.getGroupId());
                    pstmt.setInt(2, Integer.parseInt(ownerFanId));
                    pstmt.setInt(3, Integer.parseInt(idType));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while inserting  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "SQLException occured while inserting - ", e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    pttServerId, INSERT_GROUP_HIERARCHY_MAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void removeGroupHiearchyMapByOwnerIds(Integer groupId, List<String> ownerFanIds,String idType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "removeGroupHiearchyMapByOwnerIds()";
        knLogger.debug(methodName, "Entry groupId:",groupId," ownerFanIds :",ownerFanIds," idType :",idType);
        PreparedStatement pstmt = null;
        String query = null;
        try{
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(REMOVE_GROUP_HIERARCHY_MAP);
            knLogger.debug( methodName, "Executing query -" , "'" , query , "'");
            pstmt = conn.prepareStatement(query);
            for(String ownerFanId:ownerFanIds){
                pstmt.setInt(1, groupId);
                pstmt.setInt(2, Integer.parseInt(ownerFanId));
                pstmt.setInt(3, Integer.parseInt(idType));
                pstmt.addBatch();
            }
            pstmt.executeBatch();

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured - " , e);
            throw KnDbUtil.processException(e, "Failed while inserting  " + e,
                    pttServerId, REMOVE_GROUP_HIERARCHY_MAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     *
     * @param groupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void removeGroupHiearchyMapByGroupId(Integer groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "removeGroupHiearchyMapByGroupId()";
        knLogger.debug(methodName, "Entry groupId:",groupId);
        PreparedStatement pstmt = null;
        String query = null;
        int count = 0;
        try{
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(REMOVE_GROUP_HIERARCHY_MAP_BY_GROUPID);
            knLogger.debug( methodName, "Executing query -" , "'" , query , "'");
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1,groupId);
            count = pstmt.executeUpdate();
            knLogger.info(methodName,"no of records deleted -",count);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "SQLException occured - " , e);
            throw KnDbUtil.processException(e, "Failed while removeGroupHiearchyMapByGroupId  " + e,
                    pttServerId, REMOVE_GROUP_HIERARCHY_MAP_BY_GROUPID, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<Integer, List<String>> getGroupOwnerList(List<Integer> groupIds,String idType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        Map<Integer, List<String>> response= new HashMap<>();
        String methodName = "getGroupOwnerList()";
        knLogger.debug(methodName, "Entry : groupIds - ", groupIds," idType :",idType);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query=null;
        Connection conn = null;
        boolean ownedTxn = false;
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
           // Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_HIERARCHY_MAP_BY_GROUPID);
            query = replaceContactWithValue(query, GROUP_IDS, formIntegerCommaSeperatedIdList(groupIds));
            knLogger.debug(methodName," query :",query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(idType));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if(response.containsKey(rs.getInt(CORPGROUPID))){
                    response.get(rs.getInt(CORPGROUPID)).add(String.valueOf(rs.getInt(ID_VALUE)));
                }else{
                    response.put(rs.getInt(CORPGROUPID),new ArrayList<>());
                    response.get(rs.getInt(CORPGROUPID)).add(String.valueOf(rs.getInt(ID_VALUE)));
                }
            }
            knLogger.debug(methodName, " Exit: ownerList ", response);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while GROUP_HIERARCHY_MAP -",
                    pttServerId, GET_GROUP_HIERARCHY_MAP_BY_GROUPID, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return response;
    }

    public Map<Integer, List<String>> getGroupSharedIdList(List<Integer> groupIds, String idType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        Map<Integer, List<String>> response = new HashMap<>();
        String methodName = "getGroupSharedIdList()";
        knLogger.info(methodName, "Entry : groupIds - ", groupIds.size(), "idType :", idType );
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_HIERARCHY_MAP_FOR_SHAREDLIST_BY_GROUPID);
            query = replaceContactWithValue(query, GROUP_IDS, formIntegerCommaSeperatedIdList(groupIds));
            knLogger.debug(methodName, " query :", query);
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (response.containsKey(rs.getInt(CORPGROUPID))) {
                    response.get(rs.getInt(CORPGROUPID)).add(String.valueOf(rs.getInt(ID_VALUE)));
                } else {
                    response.put(rs.getInt(CORPGROUPID), new ArrayList<>());
                    response.get(rs.getInt(CORPGROUPID)).add(String.valueOf(rs.getInt(ID_VALUE)));
                }
            }
            knLogger.info(methodName, " Exit: sharedIdList ", response.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while GROUP_HIERARCHY_MAP -",
                    pttServerId, GET_GROUP_HIERARCHY_MAP_FOR_SHAREDLIST_BY_GROUPID, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return response;
    }


    /**
     *
     * @param groupIds
     * @param idList
     * @param idType
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Integer> isValidGroupForIdList(List<Integer> groupIds, List<Integer> idList, String idType, boolean readOnly, KnPersisterTxn persisterTxn)  throws KnDAOException {
        Map<Integer, Integer> response= new HashMap<>();
        String methodName = "isValidGroupForIdList(List<Integer>, List<Integer>, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : groupIds - ", groupIds," idType :",idType,"idList -",idList);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query=null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(IS_VALID_GROUPS_FOR_IDLIST);
            query = replaceContactWithValue(query, GROUP_IDS, formIntegerCommaSeperatedIdList(groupIds));
            query = replaceContactWithValue(query, ID_VALUELIST, formIntegerCommaSeperatedIdList(idList));
            knLogger.debug(methodName," query :",query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(idType));
            rs = pstmt.executeQuery();
            while (rs.next()) {
               response.put(rs.getInt(CORPGROUPID),rs.getInt(ID_VALUE));
            }
            knLogger.debug(methodName, " Exit: response - ", response);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching from GROUP_HIERARCHY_MAP -",
                    pttServerId, IS_VALID_GROUPS_FOR_IDLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return response;
    }

    public Map<String,Integer> getOwnerAndSharedContextIdByGroupIds(List<Integer> groupIds,String idType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getOwnerAndSharedContextIdByGroupIds()";
        knLogger.info(methodName, "Entry : groupId - ", groupIds.size()," idType ",idType);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String idTypeString = "";
        if (KnConstants.BAN_TYPE.equals(idType)) {
            idTypeString = "AND ID_TYPE= 1";
        } else {
            idTypeString = "AND (ID_TYPE= 2 OR ID_TYPE= 3)";
        }
        String query = " SELECT ID_VALUE, ID_TYPE FROM DG.GROUP_HIERARCHY_MAP WHERE CORPGROUPID IN (GROUP_IDS) ID_TYPE_STRING ";
        Map<String, Integer> fanIdDetailsMap = new HashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, GROUP_IDS, formIntegerCommaSeperatedIdList(groupIds));
            query = replaceContactWithValue(query, "ID_TYPE_STRING", idTypeString);
            knLogger.debug(methodName, " query :", query);
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                fanIdDetailsMap.put(String.valueOf(rs.getInt(ID_VALUE)), rs.getInt(ID_TYPE));
            }
            knLogger.info(methodName, " Exit fanidsMap: ", fanIdDetailsMap);
        } catch (SQLException e) {
            knLogger.error(methodName, "KnDAOException occured in getOwnerAndSharedContextIdByGroupIds - ", e);
            throw KnDbUtil.processException(e, "Failed while GROUP_HIERARCHY_MAP -",
                    pttServerId, "XDM_TABLE_NAME", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return fanIdDetailsMap;
    }

    /**
     * Inserts a shared-hierarchy mapping row (ID_TYPE=4) with CORPID and UPDATETIME columns.
     * Used for hierarchy-scoped group sharing (Phase 6).
     */
    public void insertSharedGroupHierarchyMap(Integer groupId, List<String> hierarchyIds, List<Integer> corpIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertSharedGroupHierarchyMap()";
        knLogger.debug(methodName, "Entry groupId:", groupId, " hierarchyIds:", hierarchyIds);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_SHARED_GROUP_HIERARCHY_MAP);
            knLogger.debug(methodName, "Executing query -", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            for (int i = 0; i < hierarchyIds.size(); i++) {
                pstmt.setInt(1, groupId);
                pstmt.setInt(2, Integer.parseInt(hierarchyIds.get(i)));
                pstmt.setInt(3, 4); // ID_TYPE = 4 for shared hierarchy
                pstmt.setInt(4, corpIds.get(i));
                pstmt.setLong(5, System.currentTimeMillis());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred - ", e);
            throw KnDbUtil.processException(e, "Failed while insertSharedGroupHierarchyMap " + e,
                    pttServerId, INSERT_SHARED_GROUP_HIERARCHY_MAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * Returns all ID_TYPE=4 rows for a group as {ID_VALUE, CORPID} pairs.
     * Used for delta computation in modifyGroup (Phase 6).
     */
    public List<int[]> getSharedGroupHierarchyMappings(Integer groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSharedGroupHierarchyMappings()";
        knLogger.debug(methodName, "Entry groupId:", groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<int[]> result = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SHARED_GROUP_HIERARCHY_MAPPINGS);
            knLogger.debug(methodName, "Executing query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(new int[]{rs.getInt(ID_VALUE), rs.getInt("CORPID")});
            }
            knLogger.debug(methodName, "Exit result.size=", result.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getSharedGroupHierarchyMappings - ",
                    pttServerId, SELECT_SHARED_GROUP_HIERARCHY_MAPPINGS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return result;
    }

    /**
     * Counts active CORPGRP_SHAREDLIST rows for the given corp pair.
     * Used for TC-XDM-DTM-010 delete blocking (Phase 6).
     */
    public int countActiveGroupsByCorpPair(Integer ownedCorpId, Integer sharedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "countActiveGroupsByCorpPair()";
        knLogger.debug(methodName, "Entry ownedCorpId:", ownedCorpId, " sharedCorpId:", sharedCorpId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(COUNT_ACTIVE_GROUPS_BY_CORP_PAIR);
            knLogger.debug(methodName, "Executing query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, ownedCorpId);
            pstmt.setInt(2, sharedCorpId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while countActiveGroupsByCorpPair - ",
                    pttServerId, COUNT_ACTIVE_GROUPS_BY_CORP_PAIR, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * Returns true if the given hierarchyId exists in DG.GROUP_HIERARCHY_MAP with ID_TYPE=4 (shared node).
     * Used to block deleteHierarchy when a shared group is scoped to this hierarchy node.
     */
    public boolean isSharedHierarchyNode(String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isSharedHierarchyNode()";
        knLogger.debug(methodName, "Entry hierarchyId:", hierarchyId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(CHECK_SHARED_HIERARCHY_NODE);
            knLogger.debug(methodName, "Executing query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(hierarchyId));
            rs = pstmt.executeQuery();
            boolean exists = rs.next() && rs.getInt(1) > 0;
            knLogger.debug(methodName, "Exit hierarchyId:", hierarchyId, " isSharedNode:", exists);
            return exists;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while isSharedHierarchyNode - ",
                    pttServerId, CHECK_SHARED_HIERARCHY_NODE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }



}

