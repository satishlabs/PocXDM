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
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSharedCorpInfo;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes.XDM_CORP_GROUPPROFILE_SHAREDLIST;

/**
 * ************************************************************************
 * <p>
 * File name:  KnCorpGroupProfileSharedListDAO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Venkata Sudhakar             July 29, 2020                10.0+
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnCorpGroupProfileSharedListDAO implements ITableDAO {

    private static final String OWNEDCORPID = "OWNEDCORPID";
    private static final String GRPPROFILEID = "GRPPROFILEID";
    private static final String SHAREDCORPID = "SHAREDCORPID";
    private static final String MEM_FEATURES_ALLOWED = "MEM_FEATURES_ALLOWED";

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupProfileSharedListDAO.class);
    private String pttServerId;

    public KnCorpGroupProfileSharedListDAO(String pttServerId) {
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

    /**
     * inserts shared corp information for the given group profile ID
     * @param groupProfilePersistDTO
     * @param persisterTxn
     */
    public void createGroupProfileSharedCorpInfo(KnCorpGroupProfilePersistDTO groupProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "createGroupProfileSharedCorpInfo()";
        knLogger.debug(methodName, "ENTRY : groupProfilePersistDTO - ", groupProfilePersistDTO);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_SHARED_CORPINFO_FOR_GROUPPROFILE);
            knLogger.debug( methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            for(KnCorpSharedCorpInfo sharedCorpInfo:groupProfilePersistDTO.getCorpSharedCorpInfoList()) {
                //group profile id
                pstmt.setInt(1, groupProfilePersistDTO.getGrpProfileId());
                //owned internal corpid
                pstmt.setInt(2, groupProfilePersistDTO.getCorpId());
                //shared internal corpid
                pstmt.setInt(3, sharedCorpInfo.getCorpId());
                //TODO: need to store group memeber propertes as bit set we are not supporting this today
                if(sharedCorpInfo.getMemFeaturesAllowed()!=null)
                    pstmt.setLong(4, sharedCorpInfo.getMemFeaturesAllowed());
                else
                    pstmt.setNull(4, Types.BIGINT);
                pstmt.addBatch();

            }

            int cnt[] = pstmt.executeBatch();
            knLogger.debug( methodName, "Query executed successfully. ",cnt,XDM_CORP_GROUPPROFILE_SHAREDLIST);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to create shared corp Group profile data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    /**
     * Fetches shared corp information for the given group profile ID
     * @param grpProfileId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnCorpSharedCorpInfo> selectGroupProfileSharedCorpInfo(int ownedCorpId, Integer grpProfileId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectGroupProfileSharedCorpInfo()";
        knLogger.debug(methodName, "ENTRY : grpProfileId - ", grpProfileId, "ownedCorpId - ", ownedCorpId);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        List<KnCorpSharedCorpInfo> sharedCorpInfoList = new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SHARED_CORPINFO_BY_PROFILEID);
            knLogger.debug( methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1,ownedCorpId);
            pstmt.setInt(2,grpProfileId);
            rs = pstmt.executeQuery();
            while (rs.next()){
                KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
                sharedCorpInfo.setOwnerCorpId(rs.getInt(OWNEDCORPID));
                sharedCorpInfo.setCorpId(rs.getInt(SHAREDCORPID));
                sharedCorpInfo.setMemFeaturesAllowed((Long)rs.getObject(MEM_FEATURES_ALLOWED));
                sharedCorpInfoList.add(sharedCorpInfo);
            }
            knLogger.debug( methodName, "Query executed successfully. ",sharedCorpInfoList,XDM_CORP_GROUPPROFILE_SHAREDLIST);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch shared corp Group profile data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return sharedCorpInfoList;
    }

    /**
     * Fetches shared corp information for the given group profile IDs
     * @param grpProfileIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer,List<KnCorpSharedCorpInfo>> selectGroupProfileSharedCorpInfo(int ownedCorpId, Collection<Integer> grpProfileIds, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectGroupProfileSharedCorpInfo()";
        knLogger.debug(methodName, "ENTRY : grpProfileIds - ", grpProfileIds,"ownedCorpId - ",ownedCorpId);
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        Map<Integer,List<KnCorpSharedCorpInfo>>sharedCorpInfoMap = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SHARED_CORPINFO_BY_PROFILEIDS);
            query = KnDbUtil.replaceValInQry(query ,ownedCorpId);
            query = KnCorpUtil.replaceContactWithValue(query, GROUPPROFILEIDS, KnCorpUtil.formIntegerCommaSeperatedIdList(grpProfileIds));
            knLogger.debug( methodName, "query -", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()){
                int grpProfileId = rs.getInt(GRPPROFILEID);
                if(sharedCorpInfoMap.get(grpProfileId) == null){
                    List<KnCorpSharedCorpInfo> sharedCorpInfoList = new ArrayList<>();
                    KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
                    sharedCorpInfo.setOwnerCorpId(rs.getInt(OWNEDCORPID));
                    sharedCorpInfo.setCorpId(rs.getInt(SHAREDCORPID));
                    sharedCorpInfo.setMemFeaturesAllowed((Long)rs.getObject(MEM_FEATURES_ALLOWED));
                    sharedCorpInfoList.add(sharedCorpInfo);
                    sharedCorpInfoMap.put(grpProfileId,sharedCorpInfoList);
                }else {
                    KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
                    sharedCorpInfo.setOwnerCorpId(rs.getInt(OWNEDCORPID));
                    sharedCorpInfo.setCorpId(rs.getInt(SHAREDCORPID));
                    sharedCorpInfo.setMemFeaturesAllowed((Long)rs.getObject(MEM_FEATURES_ALLOWED));
                    sharedCorpInfoMap.get(grpProfileId).add(sharedCorpInfo);
                }

            }
            knLogger.debug( methodName, "Query executed successfully. ",sharedCorpInfoMap,XDM_CORP_GROUPPROFILE_SHAREDLIST);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch shared corp Group profile data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return sharedCorpInfoMap;
    }

    /**
     *
     * @param profileId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteGroupProfileSharedInfo(Integer profileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteGroupProfileSharedInfo()";
        knLogger.debug(methodName, "ENTRY : profileId - ", profileId);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_SHARED_CORPINFO_FOR_GROUPPROFILE);
            knLogger.debug( methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1,profileId);
            int cnt = pstmt.executeUpdate();
            knLogger.debug( methodName, "Query executed successfully. ",cnt,XDM_CORP_GROUPPROFILE_SHAREDLIST);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete shard corp info data for group profile" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteGroupProfileSharedInfoByOwnedCorpId(Integer ownedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteGroupProfileSharedInfoByOwnedCorpId()";
        knLogger.debug(methodName, "ENTRY : ownedCorpId - ", ownedCorpId);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORPGRPPROFILE_SHAREDLIST_BY_OWNEDCORPID);
            knLogger.debug( methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1,ownedCorpId);
            pstmt.setInt(2,ownedCorpId);
            int deletedSharedGroupProfile = pstmt.executeUpdate();
            knLogger.debug( methodName, "deletedSharedGroupProfile ",deletedSharedGroupProfile);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete shard corp info data for group profile" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }
}
