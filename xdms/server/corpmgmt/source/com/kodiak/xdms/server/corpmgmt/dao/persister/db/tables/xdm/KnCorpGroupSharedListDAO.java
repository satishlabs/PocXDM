/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
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
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSharedCorpInfo;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;

public class KnCorpGroupSharedListDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupSharedListDAO.class);
    private String pttServerId;
    private static final String OWNEDCORPID = "OWNEDCORPID";
    private static final String CORPGROUPID = "CORPGROUPID";
    private static final String SHAREDCORPID = "SHAREDCORPID";
    private static final String SHAREDCORPIDS = "SHAREDCORPIDS";
    private static final String MEM_FEATURES_ALLOWED = "MEM_FEATURES_ALLOWED";


    public KnCorpGroupSharedListDAO(String pttServerId) { this.pttServerId = pttServerId; }

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

    /**
     * inserts shared corp information for the given group profile ID
     * @param groupInfoPersistDTOs
     * @param persisterTxn
     */
    public void createGroupSharedCorpInfo(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOs, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "createGroupSharedCorpInfo(List<KnCorpGroupInfoPersistDTO>)";
        knLogger.debug(methodName, "ENTRY : groupInfoPersistDTO - ", groupInfoPersistDTOs);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.INSERT_SHARED_CORPINFO_FOR_GROUP);
            knLogger.debug( methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            for(KnCorpGroupInfoPersistDTO persistDTO:groupInfoPersistDTOs) {
                for (KnCorpSharedCorpInfo sharedCorpInfo : persistDTO.getCorpSharedCorpInfoList()) {
                    // group id
                    pstmt.setInt(1,persistDTO.getGroupId());
                    //shared internal corpid
                    pstmt.setInt(2, sharedCorpInfo.getCorpId());
                    //owned internal corpid
                    pstmt.setInt(3, persistDTO.getCorpId());
                    //TODO: need to store group memeber propertes as bit set we are not supporting this today
                    if(sharedCorpInfo.getMemFeaturesAllowed()!=null)
                        pstmt.setLong(4, sharedCorpInfo.getMemFeaturesAllowed());
                    else
                        pstmt.setNull(4, Types.INTEGER);
                    pstmt.addBatch();

                }
            }
            int cnt[] = pstmt.executeBatch();
            knLogger.debug( methodName, "Query executed successfully. ",cnt, KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to create shared corp Group data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    /**
     *
     * @param ownedCorpId
     * @param groupId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnCorpSharedCorpInfo> selectGroupSharedCorpInfo(int ownedCorpId, int groupId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectGroupSharedCorpInfo()";
        knLogger.debug(methodName, "ENTRY : groupId - ", groupId, "ownedCorpId - ", ownedCorpId, " readOnly :", readOnly);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        List<KnCorpSharedCorpInfo> sharedCorpInfoList = new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.SELECT_SHARED_CORPINFO_BY_GROUPID_OWNERCORPID);
            knLogger.debug( methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1,ownedCorpId);
            pstmt.setInt(2,groupId);
            rs = pstmt.executeQuery();
            while (rs.next()){
                KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
                sharedCorpInfo.setOwnerCorpId(rs.getInt(OWNEDCORPID));
                sharedCorpInfo.setCorpId(rs.getInt(SHAREDCORPID));
                sharedCorpInfo.setMemFeaturesAllowed((Long)rs.getObject(MEM_FEATURES_ALLOWED));
                sharedCorpInfoList.add(sharedCorpInfo);
            }
            knLogger.debug( methodName, "Query executed successfully. ",sharedCorpInfoList,KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to create shared corp  data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return sharedCorpInfoList;
    }

    /**
     *
     * @param ownedCorpId
     * @param groupIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfo(int ownedCorpId, Collection<Integer> groupIds, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectGroupSharedCorpInfo()";
        knLogger.debug(methodName, "ENTRY : groupIds - ", groupIds, "ownedCorpId - ", ownedCorpId);
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        Map<Integer, List<KnCorpSharedCorpInfo>> sharedCorpInfoMap = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.SELECT_SHARED_CORPINFO_BY_OWNERCORPID_GROUPIDS);
            query = KnDbUtil.replaceValInQry(query ,ownedCorpId);
            query = KnCorpUtil.replaceContactWithValue(query, GROUPIDS, KnCorpUtil.formIntegerCommaSeperatedIdList(groupIds));
            knLogger.debug( methodName, "query -", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()){
                int grpId = rs.getInt(CORPGROUPID);
                if(sharedCorpInfoMap.get(grpId) == null) {
                    List<KnCorpSharedCorpInfo> sharedCorpInfoList = new ArrayList<>();
                    KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
                    sharedCorpInfo.setOwnerCorpId(rs.getInt(OWNEDCORPID));
                    sharedCorpInfo.setCorpId(rs.getInt(SHAREDCORPID));
                    sharedCorpInfo.setMemFeaturesAllowed((Long) rs.getObject(MEM_FEATURES_ALLOWED));
                    sharedCorpInfoList.add(sharedCorpInfo);
                    sharedCorpInfoMap.put(grpId,sharedCorpInfoList);
                }else {
                    KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
                    sharedCorpInfo.setOwnerCorpId(rs.getInt(OWNEDCORPID));
                    sharedCorpInfo.setCorpId(rs.getInt(SHAREDCORPID));
                    sharedCorpInfo.setMemFeaturesAllowed((Long) rs.getObject(MEM_FEATURES_ALLOWED));
                    sharedCorpInfoMap.get(grpId).add(sharedCorpInfo);
                }
            }
            knLogger.debug( methodName, "Query executed successfully. ",sharedCorpInfoMap,KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch shared corp  data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return sharedCorpInfoMap;
    }

    /**
     *
     * @param sharedCorpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer,List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfoBySharedCorpId(int sharedCorpId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectGroupSharedCorpInfoBySharedCorpId(int, boolean, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : ,sharedCorpId - ", sharedCorpId);
        PreparedStatement pStmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        Map<Integer,List<KnCorpSharedCorpInfo>> sharedCorpInfoMap = new HashMap<>();
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
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.SELECT_SHARED_CORPINFO_BY_SHRD_CORPID);
            knLogger.debug( methodName, "query -", query);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1,sharedCorpId);
            rs = pStmt.executeQuery();
            while (rs.next()){
                int grpId = rs.getInt(CORPGROUPID);
                if(sharedCorpInfoMap.get(grpId) == null) {
                    List<KnCorpSharedCorpInfo> sharedCorpInfoList = new ArrayList<>();
                    KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
                    sharedCorpInfo.setOwnerCorpId(rs.getInt(OWNEDCORPID));
                    sharedCorpInfo.setCorpId(rs.getInt(SHAREDCORPID));
                    sharedCorpInfo.setMemFeaturesAllowed((Long) rs.getObject(MEM_FEATURES_ALLOWED));
                    sharedCorpInfoList.add(sharedCorpInfo);
                    sharedCorpInfoMap.put(grpId,sharedCorpInfoList);
                }else {
                    KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
                    sharedCorpInfo.setOwnerCorpId(rs.getInt(OWNEDCORPID));
                    sharedCorpInfo.setCorpId(rs.getInt(SHAREDCORPID));
                    sharedCorpInfo.setMemFeaturesAllowed((Long) rs.getObject(MEM_FEATURES_ALLOWED));
                    sharedCorpInfoMap.get(grpId).add(sharedCorpInfo);
                }
            }
            knLogger.debug( methodName, "Query executed successfully. ",sharedCorpInfoMap,KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch shared corp group  data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return sharedCorpInfoMap;
    }


    /**
     *
     * @param groupIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfoByGroupId(Collection<Integer> groupIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectGroupSharedCorpInfoByGroupId()";
        knLogger.debug(methodName, "ENTRY : groupId - ", groupIds, " readOnly :", readOnly);
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        Map<Integer, List<KnCorpSharedCorpInfo>> sharedCorpInfoMap = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.SELECT_SHARED_CORPINFO_BY_GROUPID);
            query = KnCorpUtil.replaceContactWithValue(query, GROUPIDS, KnCorpUtil.formIntegerCommaSeperatedIdList(groupIds));
            knLogger.debug( methodName, "query -", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()){
                int grpId = rs.getInt(CORPGROUPID);
                if(sharedCorpInfoMap.get(grpId) == null) {
                    List<KnCorpSharedCorpInfo> sharedCorpInfoList = new ArrayList<>();
                    KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
                    sharedCorpInfo.setOwnerCorpId(rs.getInt(OWNEDCORPID));
                    sharedCorpInfo.setCorpId(rs.getInt(SHAREDCORPID));
                    sharedCorpInfo.setMemFeaturesAllowed((Long) rs.getObject(MEM_FEATURES_ALLOWED));
                    sharedCorpInfoList.add(sharedCorpInfo);
                    sharedCorpInfoMap.put(grpId,sharedCorpInfoList);
                }else {
                    KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
                    sharedCorpInfo.setOwnerCorpId(rs.getInt(OWNEDCORPID));
                    sharedCorpInfo.setCorpId(rs.getInt(SHAREDCORPID));
                    sharedCorpInfo.setMemFeaturesAllowed((Long) rs.getObject(MEM_FEATURES_ALLOWED));
                    sharedCorpInfoMap.get(grpId).add(sharedCorpInfo);
                }
            }
            knLogger.debug( methodName, "Query executed successfully. ",sharedCorpInfoMap,KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to create shared corp  data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return sharedCorpInfoMap;
    }

    /**
     * Method to delete list of group ids from shared group info.
     * @param groupIds
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteSharedGroups(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSharedGroups()";
        knLogger.debug(methodName, "ENTRY : groupIds - ", groupIds);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_SHARED_GROUPS);
            knLogger.debug( methodName, "Executing query -", query);
            pstmt = conn.prepareStatement(query);
            for (Integer id : groupIds) {
                pstmt.setInt(1, id);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug( methodName, "EXIT: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete shared corp  data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteSharedGroupsByOwnedCorp(Integer ownedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSharedGroupsByOwnedCorp(String)";
        knLogger.debug(methodName, "ENTRY : ownedCorpId - ", ownedCorpId);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_SHARED_GROUPS_BY_OWNEDCORP);
            knLogger.debug( methodName, "Executing query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, ownedCorpId);
            pstmt.setInt(2, ownedCorpId);
            int deletedSharedGroups = pstmt.executeUpdate();
            knLogger.debug( methodName, "EXIT: deletedSharedGroups:",deletedSharedGroups);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete shared corp  data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public List<KnCorpSharedCorpInfo> selectGroupSharedCorpInfoByOwnedCorpId(int ownedCorpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectGroupSharedCorpInfo()";
        knLogger.debug(methodName, "ENTRY : ownedCorpId - ",ownedCorpId);
        PreparedStatement pStmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        List<KnCorpSharedCorpInfo> ownedCorpGroupInfo = new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.SELECT_SHARED_CORPINFO_BY_OWN_CORPID);
            knLogger.debug( methodName, "query -", query);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1,ownedCorpId);
            rs = pStmt.executeQuery();
            while (rs.next()){
                KnCorpSharedCorpInfo ownCorpGroupInfo=new KnCorpSharedCorpInfo();
                ownCorpGroupInfo.setOwnerCorpId(rs.getInt(OWNEDCORPID));
                ownCorpGroupInfo.setCorpId(rs.getInt(SHAREDCORPID));
                ownCorpGroupInfo.setMemFeaturesAllowed((Long) rs.getObject(MEM_FEATURES_ALLOWED));
                ownCorpGroupInfo.setCorpGroupId(rs.getInt(CORPGROUPID));
                ownedCorpGroupInfo.add(ownCorpGroupInfo);

            }
            knLogger.debug( methodName, "Query executed successfully. ",ownedCorpGroupInfo,KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch owned corp group  data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return ownedCorpGroupInfo;
    }

    public List<String> getSharedGroupMemberBySharedAndOwnCorpids(int ownedCorpId,List<Integer> sharedCorpids, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSharedGroupMemberBySharedAndOwnCorpids()";
        knLogger.debug(methodName, "ENTRY : ownedCorpId - ", ownedCorpId,"sharedCorpids - ",sharedCorpids);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        List<String> groupMemberList = new ArrayList<>();
        try {
            if(sharedCorpids == null) {
                knLogger.debug(methodName, "No shared corpid is passed. ",sharedCorpids);
                return groupMemberList;
            }
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            var mdnListArray = new ArrayList<>(sharedCorpids);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(KnPersisterConstants.GET_SHARED_GROUP_MEMBERS_BY_SHARED_AND_OWN_CORPIDS);
                //query = KnCorpUtil.replaceContactWithValue(query, SHAREDCORPID, KnCorpUtil.formIntegerCommaSeperatedIdList(sharedCorpids));
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarksAnyType(subsList, query, SHAREDCORPIDS);
                int index = 1;
                knLogger.debug(methodName, "query", query);
                pstmt = conn.prepareStatement(query);
                for (Integer corpId : subsList) {
                    pstmt.setInt(index++, corpId);
                }
                pstmt.setInt(index++, ownedCorpId);
                for (Integer corpId : subsList) {
                    pstmt.setInt(index++, corpId);
                }
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    groupMemberList.add(rs.getString(1));
                }
            }
            knLogger.debug( methodName, "Query executed successfully. ", KnGDPRTemplate.mdnList(groupMemberList));
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to create shared corp  data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GRP_SHAREDLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return groupMemberList;
    }

}
