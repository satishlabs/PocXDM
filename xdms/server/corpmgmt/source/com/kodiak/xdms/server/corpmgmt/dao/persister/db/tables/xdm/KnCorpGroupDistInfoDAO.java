/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpGroupDistInfoDAO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 29, 2011      7.0
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
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;


public class KnCorpGroupDistInfoDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupDistInfoDAO.class);

    private static final String CLASS = KnCorpGroupDistInfoDAO.class.getName();

    private String pttServerId;

    KnCorpGroupDistInfoDAO(String pttServerId) {
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

    private int getSize(Collection dataList) {
        if (dataList != null) {
            return dataList.size();
        } else {
            return 0;
        }
    }

    //The map will contact mapping for subscribers only whose group count is atleast 1 or more
    public Map<String, Integer> selectSubscriberGroupCounts(Collection<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException {

        String methodName = "selectSubscriberGroupCounts(Collection<String>, KnPersisterTxn)";
        knLogger.debug( methodName, "Enrty :Input DTO passed mdnList size - ", getSize(mdnList));

        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        boolean ownedTxn = false;
        Connection conn = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            //Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
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
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();//todo group by
            var mdnListArray = new ArrayList<>(mdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            Map<String, Integer> countMap = new HashMap<String, Integer>();
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(SUBSCRIBER_GROUP_MEMBER_COUNTS);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(subsList));
                int index=1;
                knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for (String mdn : subsList) {
                    pStmt.setString(index++, mdn);
                }
                rs = pStmt.executeQuery();
                while (rs.next()) {
                    countMap.put(rs.getString(1).trim(), rs.getInt(2));
                }
            }
            knLogger.debug( methodName, "MemberGroupCount Map size - ", countMap.size());
            return countMap;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while fetching subscribers group count-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while fetching subscribers group count- ",
                    e);
            throw KnDbUtil.processException(e, "Failed to selectSubscriberGroupCounts " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug(methodName, "EXIT:");
        }
    }

    public void deleteGroupDistribution(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteGroupDistribution (int, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY groupId - ", groupId);
        PreparedStatement pstmt = null;
        String query = null;
        try {

            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //query = "DELETE FROM DG.CorpGroupDistInfo WHERE CorpGroupId = ?";
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_COMP_GROUP_DIST_INFO);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt.executeUpdate();
            knLogger.debug( methodName, "EXIT: Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting from group distribution table-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception while deleting from group distribution table- ", e);
            throw KnDbUtil.processException(e, "Failed to deleteGroupDistribution " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }


    public void insertToCorpGroupDistInfo(Collection<KnCorpSubscriberDTO> actualMdnToBeAddedToGroup,
                                          int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertToCorpGroupDistInfo(Collection<KnCorpSubscriberDTO>, int, KnPersisterTxn )";
        knLogger.debug( methodName, "Entry : actualMdnToBeAddedToGroup size - ", getSize(actualMdnToBeAddedToGroup), ", groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            query = queryMapper.getQuery(INSERT_INTO_GROUP_DIST_INFO);
            pstmt = conn.prepareStatement(query);
            for (KnCorpSubscriberDTO subsc : actualMdnToBeAddedToGroup) {
                pstmt.setInt(1, groupId);
                String mdn = subsc.getMdn();
                pstmt.setString(2, mdn);
                pstmt.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing - ", "'", query, "'");
            pstmt.executeBatch();
            knLogger.debug( methodName, "QUERY : Executed Query.");
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to insertToCorpGroupDistInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while -  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to getSublistGroupDistributionList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug( methodName, "EXIT: QUERY : Executed Query.");
        }
    }

    public void deleteAllGroupsDistribution(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllGroupsDistribution (Collection<Integer>, KnPersisterTxn)";
        knLogger.debug( methodName, "ENTRY groupIdsList size - ", getSize(groupIdsList));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_ALL_GROUPS_DIST_INFO);
            //query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdsList));
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            for(Integer groupId : groupIdsList) {
                pstmt.setInt(1, groupId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug( methodName, "groupIdsList size - ", getSize(groupIdsList), "EXIT: Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting the all group distribution data-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception while deleting the all group distribution data- ", e);
            throw KnDbUtil.processException(e, "Failed to deleteAllGroupsDistribution " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void insertToCorpGroupDistInfo(List<String> distMdnList, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertToCorpGroupDistInfo(Collection<KnCorpSubscriberDTO>, int, KnPersisterTxn )";
        knLogger.debug( methodName, "Entry - ", KnGDPRTemplate.mdnList(distMdnList), " groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(INSERT_INTO_GROUP_DIST_INFO);
            pstmt = conn.prepareStatement(query);
            for (String mdn : distMdnList) {
                pstmt.setInt(1, groupId);
                pstmt.setString(2, mdn);
                pstmt.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing - ", query);
            pstmt.executeBatch();
            knLogger.debug( methodName, "QUERY : Executed Query.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insertToCorpGroupDistInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public List<String> deleteGroupDistInfo(List<String> deletedMembers, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteGroupDistInfo(List<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Enrty :Input DTO passed deletedMembers - ", deletedMembers.size());
        PreparedStatement pstmt = null;
        String query = null;
        List<String> deletedList = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_SUBSCRIBER_DIST_INFO);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            for (String mdn : deletedMembers) {
                pstmt.setInt(1, groupId);
                pstmt.setString(2, mdn);
                pstmt.setInt(3, groupId);
                pstmt.setString(4, mdn);
                pstmt.addBatch();
            }
            int[] status = pstmt.executeBatch();
            knLogger.debug(methodName, "Query executed successfully.");

            int i = 0;
            for(String mdn : deletedMembers){
               if(status[i++] == 1){
                 deletedList.add(mdn);
               }
            }
            knLogger.debug( methodName, "EXIT deletedList ", KnGDPRTemplate.mdnList(deletedList));
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insertIntoCorpGroupDistInfo ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return deletedList;
    }

    public void deleteGroupDistInfo(Map<Integer, List<String>> grpMemMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteGroupDistInfo(Map, KnPersisterTxn)";
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_GROUP_DIST_INFO);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            for(Map.Entry<Integer, List<String>> entry : grpMemMap.entrySet()){
                int groupId = entry.getKey();
                List<String> memList = entry.getValue();
                for(String mdn : memList){
                    pstmt.setString(1, mdn);
                    pstmt.setInt(2, groupId);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insertIntoCorpGroupDistInfo ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<String, Integer> selectSubscriberAbdgGroupCounts(Collection<Integer> groupIds, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectSubscriberAbdgGroupCounts(Collection<String>, KnPersisterTxn)";
        knLogger.debug( methodName, "Enrty :Input DTO passed mdnList size - ", getSize(groupIds));
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();//todo group by
            query = queryMapper.getQuery(SUBSCRIBER_ABDG_GROUP_MEMBER_COUNTS);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Query executed successfully.");
            rs = stmt.executeQuery(query);
            Map<String, Integer> countMap = new HashMap<String, Integer>();
            while (rs.next()) {
                countMap.put(rs.getString(1).trim(), rs.getInt(2));
            }
            knLogger.debug( methodName, "MemberAbdgGroupCount Map size - ", countMap.size());
            return countMap;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while fetching subscribers abdg group count- ", e);
            throw KnDbUtil.processException(e, "Failed to selectSubscriberAbdgGroupCounts " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug( methodName, "EXIT:");
        }
    }

    public Map<String, List<Integer>> getSubscriberDistGroupList(Set<Integer> groupList, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getSubscriberDistGroupList()";
        knLogger.debug( methodName, "Enrty :- ", getSize(groupList));

        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            Map<String, List<Integer>> subscrDistGroupMap = new HashMap<>();
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_DELETED_GROUPMEMBERS);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupList));
            knLogger.debug( methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Query executed successfully.");
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                String mdn = rs.getString(2).trim();
                int groupId = rs.getInt(1);
                List<Integer> groups = subscrDistGroupMap.get(mdn);
                if(groups == null){
                    groups = new ArrayList<>();
                }
                groups.add(groupId);
                subscrDistGroupMap.put(mdn, groups);
            }
            knLogger.debug( methodName, "Map size - ", subscrDistGroupMap.size());
            return subscrDistGroupMap;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured",
                    e);
            throw KnDbUtil.processException(e, "Failed " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
    }
}