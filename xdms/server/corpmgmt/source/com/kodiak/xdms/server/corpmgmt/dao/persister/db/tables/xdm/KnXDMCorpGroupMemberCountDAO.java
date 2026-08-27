/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpGroupMemberCountDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        28-03-2011      7.0
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
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.kodiak.logger.KnLogger;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnXDMCorpGroupMemberCountDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpGroupMemberCountDAO.class);
    private String pttServerId;
    private String CLASS = KnXDMCorpGroupMemberCountDAO.class.getName();

    KnXDMCorpGroupMemberCountDAO(String pttServerId) {
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

    public void deleteCorpGroupMemberCountEntry(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpGroupMemberCountEntry(int, KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : groupId - " , groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_GROUP_MEMBER_COUNT_ENTRY);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeQuery();
            knLogger.debug( methodName, "groupId - " , groupId, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleteing the corp group member count entry - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleteing the corp list reference for all group - " , e);
            throw KnDbUtil.processException(e, "Failed while deleteing the corp group member count entry - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_COUNT, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteAllGroupsCorpGroupMemberCountEntry(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "deleteAllGroupsCorpGroupMemberCountEntry(Collection<Integer>, KnPersisterTxn )";
        knLogger.debug( methodName, "ENTRY : groupIdsList - " , groupIdsList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_ALL_GROUPS_MEMBER_COUNT_ENTRY);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdsList));
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            pstmt.executeQuery();
            knLogger.debug( methodName, "groupIdsList - " , groupIdsList, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleteing the corp group member count entry - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleteing the corp list reference for all group - " , e);
            throw KnDbUtil.processException(e, "Failed while deleteing the corp group member count entry - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_COUNT, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<Integer, Integer> getGroupMemCount(Collection<Integer> grpIdList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getGroupMemCount(List<Integer>,boolean,KnPersisterTxn )";
        Connection conn = null;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        Map<Integer, Integer> grpIdMemCountMap = new HashMap<>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_MEMBER_COUNT);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(grpIdList));
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Exeuting query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                grpIdMemCountMap.put(rs.getInt(1), rs.getInt(2));
            }
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
            knLogger.debug(methodName, "EXIT. groupSize - ", grpIdMemCountMap.size());
        }catch (SQLException e) {
            if (ownedTxn) {
                knLogger.debug(methodName, "rolling back the transaction ");
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while retrieving the group info  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_COUNT, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return grpIdMemCountMap;
    }

    public void updateAllGroupsCorpGroupMemberCountEntry(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "updateAllGroupsCorpGroupMemberCountEntry(Collection<Integer>, KnPersisterTxn )";
        knLogger.info(methodName, "ENTRY : groupIdsList - ", groupIdsList != null ? groupIdsList.size() : groupIdsList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int affectedRows = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_ALL_GROUPS_MEMBER_COUNT_ENTRY);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdsList));
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            affectedRows = pstmt.executeUpdate();
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while deleting the corp group member count entry - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while updating the corp list reference for all group - ", e);
            throw KnDbUtil.processException(e, "Failed while updating the corp group member count entry - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_COUNT, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "EXIT: No. of updated rows are : ", affectedRows);
        }
    }

    public Map<Integer, Integer> getGroupMemsCounts(Collection<Integer> grpIdList , KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMemsCounts(List<Integer>)";
        Connection conn = null;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        Map<Integer, Integer> grpIdMemCountMap = new HashMap<>();
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_MEM_COUNT_ON_GROUPIDS);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(grpIdList));
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Exeuting query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                grpIdMemCountMap.put(rs.getInt(1), rs.getInt(2));
            }
            knLogger.debug(methodName, "EXIT. groupSize - ", grpIdMemCountMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group info  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_COUNT, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return grpIdMemCountMap;
    }
}
