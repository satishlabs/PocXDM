/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;
import static com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes.XDM_USERPROFILE_HIERARCHY_MAP;

public class KnXDMUserProfilHiearchyMapDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMUserProfilHiearchyMapDAO.class);

    public String pttServerId = null;
    private static String ID_VALUE = "ID_VALUE";
    private static String GET_PROFILE_ID_OWNER="select USERPROFILEID,ID_VALUE from DG.USERPROFILE_HIERARCHY_MAP where USERPROFILEID IN(USERPROFILEIDLIST) AND ID_TYPE=2;";
    private static String USERPROFILEIDLIST ="USERPROFILEIDLIST";
    KnXDMUserProfilHiearchyMapDAO(String pttServerId) {
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

    public void insertUserProfileHiearchyMap(String userProfileId, List<String> ownerFanIds,String idType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertUserProfileHiearchyMap()";
        knLogger.info(methodName, "Entry userProfileId:", userProfileId, " ownerFanIds :", ownerFanIds, " idType :", idType);
        PreparedStatement pstmt = null;
        String query = null;
        try{
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_USERPROFILE_HIERARCHY_MAP);
            knLogger.debug( methodName, "Executing query -" , "'" , query , "'");
            pstmt = conn.prepareStatement(query);
            for(String ownerFanId:ownerFanIds){
                pstmt.setString(1, userProfileId);
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
                    pttServerId, XDM_USERPROFILE_HIERARCHY_MAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteUserProfileHiearchyMap(String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteUserProfileHiearchyMap(String)";
        knLogger.debug(methodName, "Entry : userProfileId -", userProfileId);
        PreparedStatement pStmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_USERPROFILE_HIERARCHY_MAP);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, userProfileId);
            knLogger.debug(methodName, "Executing query - ", query);
            int count = pStmt.executeUpdate();
            knLogger.debug(methodName, "count - ", count);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteUserProfileHiearchyMap -",
                    pttServerId, XDM_USERPROFILE_HIERARCHY_MAP, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

	public List<String> userProfileIdsHiearchyMap(KnPersisterTxn persisterTxn,List<String> iD_VALUE, boolean readOnly) throws KnDBPersistenceException, KnDBConnectionException, KnDAOException {
		// TODO Auto-generated method stub
		String methodName = "userProfileIdsHiearchyMap(KnPersisterTxn, List<String>, boolean)";

        knLogger.debug(methodName, "Entry : ");
        PreparedStatement pStmt = null;
        String query = GET_USERPROFILEID_HIERARCHY_MAP;
        List<String> userProfileID = new ArrayList<String>();
        ResultSet rs = null;
        try {
        	Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
        	KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_USERPROFILEID_HIERARCHY_MAP);
            query = replaceContactWithValue(query, "ID_VALUES", formCommaSeperatedIdList(iD_VALUE));
            knLogger.debug(methodName, "Executing query - ", query);
            pStmt = conn.prepareStatement(query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed Successfully", query);
            while(rs.next()) {
            	userProfileID.add(rs.getString(1));
            }

        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while userProfileIdsHiearchyMap -",
                    pttServerId, XDM_USERPROFILE_HIERARCHY_MAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
		return userProfileID;
		
	}

    public List<String> userProfileIdsHiearchyMapFetchSize(KnPersisterTxn persisterTxn, String startIndex, String fetchSize, List<String> iD_VALUE) throws KnDBPersistenceException, KnDBConnectionException, KnDAOException {

        String methodName = "userProfileIdsHiearchyMapFetchSize(persisterTxn,String,String,List<String>)";
        int lastIndex = Integer.parseInt(startIndex) + Integer.parseInt(fetchSize);
        knLogger.info(methodName, "Entry : ", "startIndex: ", startIndex, "fetchSize:", fetchSize, "lastIndex:", lastIndex);
        PreparedStatement pStmt = null;
        String query = GET_USERPROFILEID_HIERARCHY_MAP_FETCH_SIZE;
        List<String> userProfileID = new ArrayList<String>();
        ResultSet rs = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_USERPROFILEID_HIERARCHY_MAP_FETCH_SIZE);
            query = replaceContactWithValue(query, "ID_VALUES", formCommaSeperatedIdList(iD_VALUE));
            knLogger.debug(methodName, "Executing query - ", query);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, Integer.parseInt(startIndex) + 1);
            pStmt.setInt(2, lastIndex);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query executed Successfully", query);
            while (rs.next()) {
                userProfileID.add(rs.getString(1));
            }
            knLogger.info(methodName, "userProfileID: ", userProfileID, "startIndex: ", startIndex, "fetchSize: ", fetchSize, "lastIndex: ", lastIndex);

        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while userProfileIdsHiearchyMap -",
                    pttServerId, XDM_USERPROFILE_HIERARCHY_MAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
        return userProfileID;

    }

    public List<String> getOwnerIDByUserProfileId(String userProfileId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getOwnerIDByUserProfileId(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : userProfileId - ",userProfileId);
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        List<String> ownerIdList = new ArrayList<String>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_OWNERIDLIST_BY_USERPROFILEID);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, userProfileId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pStmt.executeQuery();
            while(rs.next()){
                ownerIdList.add(rs.getString(ID_VALUE));
            }
            knLogger.debug(methodName, "ownerIdList - ", ownerIdList);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getOwnerIDByUserProfileId -",
                    pttServerId, XDM_USERPROFILE_HIERARCHY_MAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return ownerIdList;
    }

    public  Map<String,List<String>> getUserProfileOwnerList(Collection<String> userProfileIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getOwnerIDByUserProfileId(Collection<String>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : userProfileId - ",userProfileIds);
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = GET_PROFILE_ID_OWNER;
        HashMap<String,List<String>> userProfileInfo = new HashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, USERPROFILEIDLIST, formCommaSeperatedIdList(userProfileIds));
            knLogger.debug(methodName, "Going to Execute query - ", query);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pStmt.executeQuery();
            while(rs.next()){
                if(userProfileInfo.containsKey(rs.getString(1))){
                    userProfileInfo.get(rs.getString(1)).add(String.valueOf(rs.getInt(2)));
                }else{
                    userProfileInfo.put(rs.getString(1), new ArrayList<>());
                    userProfileInfo.get(rs.getString(1)).add(String.valueOf(rs.getInt(2)));
                }
            }
            knLogger.debug(methodName, "userProfileInfo - ", userProfileInfo);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getOwnerIDByUserProfileId -",
                    pttServerId, XDM_USERPROFILE_HIERARCHY_MAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return userProfileInfo;
    }

    public void removeUserProfileHiearchyMapByOwnerIds(String userProfileId, List<String> ownerFanIds,String idType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "removeUserProfileHiearchyMapByOwnerIds()";
        knLogger.debug(methodName, "Entry userProfileId:",userProfileId," ownerFanIds :",ownerFanIds," idType :",idType);
        PreparedStatement pstmt = null;
        String query = null;
        try{
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(REMOVE_USERPROFILE_FAN_HIERARCHY_MAP);
            knLogger.debug( methodName, "Executing query -" , "'" , query , "'");
            pstmt = conn.prepareStatement(query);
            for(String ownerFanId:ownerFanIds){
                pstmt.setString(1, userProfileId);
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
                    pttServerId, XDM_USERPROFILE_HIERARCHY_MAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

  }



