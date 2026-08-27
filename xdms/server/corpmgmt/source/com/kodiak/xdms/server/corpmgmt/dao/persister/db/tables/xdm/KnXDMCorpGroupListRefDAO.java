/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpGroupListRefDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        02-02-2011      7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;

import com.kodiak.logger.KnLogger;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnXDMCorpGroupListRefDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpGroupListRefDAO.class);

    private String pttServerId;
    private String CLASS = KnXDMCorpGroupListRefDAO.class.getName();

    KnXDMCorpGroupListRefDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "insert", "Unimplemented Methods");
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "update", "Unimplemented Methods");
    }

    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "delete", "Unimplemented Methods");
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "select", "Unimplemented Methods");
        return null;
    }

    public Collection<KnCorpGroupInfoPersistDTO> selectGroupMappedToSublist(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectGroupMappedToSublist(int,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : sublistId - ", sublistId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection<KnCorpGroupInfoPersistDTO> groupList = new ArrayList<KnCorpGroupInfoPersistDTO>();
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBLIST_DIST_TOGROUP);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sublistId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully ");
            while (rs.next()) {
                KnCorpGroupInfoPersistDTO grp = new KnCorpGroupInfoPersistDTO();
                grp.setGroupId(rs.getInt(1));
                grp.setGroupType(mappGroupTypeToApp(rs.getInt(2)));
                //multilingual revert change
                if(rs.getString(3) != null){
                    grp.setGroupDisplayName(new String(rs.getString(3).trim().getBytes("8859_1"),"UTF-8"));
                }
                grp.setGroupMemberListId(rs.getInt(4));
                if(rs.getInt(6) == 1){
                    grp.setLargeGroup(true);
                }
                groupList.add(grp);
            }
            return groupList;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving group mapped to sublist - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving group mapped to sublist  - " , e);
            throw KnDbUtil.processException(e, "Failed while retrieving group mapped to sublist  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_REF, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName,  "EXIT : Groups list size returned - ", groupList.size());
        }
    }

    public void deleteCorpListDistGroupReference(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpListDistReference(int,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : sublistId - ", sublistId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORP_LIST_REF_FOR_ALL_GROUPS);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sublistId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeQuery();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleteing the corp list reference for all group - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleteing the corp list reference for all group - " , e);
            throw KnDbUtil.processException(e, "Failed while deleteing the corp list reference for all group " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_REF, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Collection<Integer> selectGroupsSublistIdInRemovedList(int groupId, Collection<Integer> removedSublistIds,
                                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectGroupsSublistIdInRemovedList(int, Collection<Integer>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupId - ", groupId, " ,removedSublistIds - ", removedSublistIds);
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        Collection<Integer> sublistList = new ArrayList<Integer>();
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBLIST_REF_FOR_GROUP_FROM_SUBLIST);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String removeSublistIdStr = formIntegerCommaSeperatedIdList(removedSublistIds);
            //pstmt.setString(1, removeSublistIdStr);
            query = replaceContactWithValue(query, SUBLISTID, removeSublistIdStr);
            query = KnDbUtil.replaceValInQry(query, groupId);
            knLogger.debug( methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                sublistList.add(rs.getInt(1));
            }
            return sublistList;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while checking if the request sublist " ,
                    "refrence for group exist in db- " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while checking if the request sublist - " , "refrence for group exist in db- " , e);
            throw KnDbUtil.processException(e, "Failed to selectGroupsSublistIdInRemovedList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_REF, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug( methodName, "EXIT : sublist list size returned - " , sublistList.size());
        }
    }

    public void deleteGroupSublistRef(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteGroupSublistRef(int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_ALL_SUBLIST_FOR_GROUP);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeQuery();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting the groups sublist refrences - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while deleting the groups sublist refrences  - " + e);
            throw KnDbUtil.processException(e, "Failed while deleting the groups sublist refrences " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_REF, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void removeSublistListsMappingFromGroup(Collection<Integer> removedSublistIds,
                                                   int groupId,
                                                   KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "removeSublistListsMappingFromGroup(Collection<Integer>, int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : removedSublistIds - ", removedSublistIds, " , groupId - ", groupId);
        Connection conn;
        Statement stmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_GROUPS_SUBLIST_REFRENCE);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(removedSublistIds));
            query = KnDbUtil.replaceValInQry(query, groupId);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleteing the corp list reference for all group - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleteing the corp list reference for all group - " , e);
            throw KnDbUtil.processException(e, "Failed while deleteing the corp list reference for all group " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_REF, query);
        } finally {
            KnDbUtil.closeStatement(stmt);
        }
    }

    public void deleteAllGroupsSublistRef(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllGroupsSublistRef(Collection<Integer>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupIdsList - ", groupIdsList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_ALL_GROUPS_LIST_REF);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdsList));
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeQuery();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting the groups sublist refrences - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while deleting the groups sublist refrences  - " + e);
            throw KnDbUtil.processException(e, "Failed while deleting the groups sublist refrences " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_REF, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Collection<Integer> getGroupsSublistListFromDB(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupsSublistListFromDB(int,KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : groupId - " , groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection<Integer> sublistList = new ArrayList<Integer>();
        String query = null;
        try {
          KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBLIST_OF_GROUP);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sublistList.add(rs.getInt(1));
            }
            knLogger.debug( methodName, "EXIT: Query executed successfully, Response list size is - " , sublistList.size());
            return sublistList;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving the group sublist List - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while retrieving the group sublist List  - " + e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group sublist List " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_REF, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    /**
     * This method deleted the Group_Sublist entries for sublistId.
     * @param sublistIds
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteCorpListDistGroupReference(List<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpListDistGroupReference(List<Integer>,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : sublistId - ", sublistIds);
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORP_LIST_REF_FOR_ALL_GROUPS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for(int id : sublistIds){
                pstmt.setInt(1, id);
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteing the corp list reference in batch" ,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_REF, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Collection<Integer> getAllSublistForGroupIds(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAllSublistForGroupIds()";
        knLogger.debug( methodName, "ENTRY : groupIdsList - " , groupIdsList);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Collection<Integer> sublistIds=new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_ALL_SUBLIST_OF_GROUPIDS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String sublistListStr = formIntegerCommaSeperatedIdList(groupIdsList);
            query = replaceContactWithValue(query, GROUPIDS, sublistListStr);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Exeuting query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                sublistIds.add(rs.getInt(1));
            }
            knLogger.debug( methodName, "EXIT. sublistIds - ", sublistIds);
            return sublistIds;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured - " + e);
            throw KnDbUtil.processException(e, "Failed while retrieving sublist of group " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_REF, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
    }
    public Map<Integer,Collection<Integer>> getBulkGroupsSublistListFromDB(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupsSublistListFromDB(int,KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : groupIds - " , groupIds);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        var sublistListMap = new HashMap<Integer,Collection<Integer>>();
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBLIST_OF_GROUP_IDS);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String sublistListStr = formIntegerCommaSeperatedIdList(groupIds);
            query = replaceContactWithValue(query, GROUPIDS, sublistListStr);
            pstmt = conn.prepareStatement(query);

            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int groupId = rs.getInt("CORPGROUPID");
                int sublistId = rs.getInt("CORPLISTID");
                if(!sublistListMap.containsKey(groupId))
                    sublistListMap.put(groupId, new ArrayList<>());

                sublistListMap.get(groupId).add(sublistId);
            }
            knLogger.debug( methodName, "EXIT: Query executed successfully, Response list size is - " , sublistListMap.size());
            return sublistListMap;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving the group sublist List - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while retrieving the group sublist List  - " + e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group sublist List " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_LIST_REF, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

}
