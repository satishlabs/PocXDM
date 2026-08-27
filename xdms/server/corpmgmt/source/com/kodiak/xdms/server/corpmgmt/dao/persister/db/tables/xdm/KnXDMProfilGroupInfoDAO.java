/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GET_PROFILE_MDN_BY_UP_ID;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GET_SUBSCRIBERS_DETAILS;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.MDNLIST;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.SELECT_PROFILE_GROUP_INFO_BY_PROFILEID;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.SELECT_PROFILE_GROUP_INFO_BY_GROUPID;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class KnXDMProfilGroupInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMProfilGroupInfoDAO.class);

    public String pttServerId = null;
    private static final String UPMID = "UPMID";

    KnXDMProfilGroupInfoDAO(String pttServerId) {
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

    public void insertProfileGroupInfo(String userProfileId, Set<KnCorpGroupListInfoDTO> groupList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertProfileGroupInfo(String, Set<KnCorpGroupListInfoDTO>, KnPersisterTxn)";
        PreparedStatement pstmt = null;
        String query = null;
        knLogger.info(methodName, "userProfileId", userProfileId);
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            //INSERT INTO DG.PROFILE_GROUP_INFO (USER_PROFILEID, GROUPID, GROUP_ZONE, GROUP_CHANNEL, GROUP_PRIORITY, IS_SUPERVISOR, IS_BROADCASTER,
            // CALL_INITIATE_PERMISSION, CALL_RECEIVE_PERMISSION, INCALL_PERMISSION, IS_LOCWATCHER, IS_OSMAUTHORIZED) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
            query = queryMapper.getQuery(KnPersisterConstants.INSERT_PROFILE_GROUP_INFO);
            pstmt = conn.prepareStatement(query);
            for(KnCorpGroupListInfoDTO infoDTO : groupList) {
                pstmt.setString(1, userProfileId);
                pstmt.setInt(2, infoDTO.getGroupID());
                pstmt.setNull(3, java.sql.Types.INTEGER);
                if( infoDTO.getGroupZone() != null){
                    pstmt.setInt(3, infoDTO.getGroupZone());
                }else{
                    pstmt.setNull(3, java.sql.Types.INTEGER);
                }
                if( infoDTO.getGroupChannel() != null){
                    pstmt.setInt(4, infoDTO.getGroupChannel());
                }else{
                    pstmt.setNull(4, java.sql.Types.INTEGER);
                }
                if( infoDTO.getGroupPriority() != null) {
                    pstmt.setInt(5, infoDTO.getGroupPriority());
                }else{
                    pstmt.setNull(5, java.sql.Types.INTEGER);
                }
                if( infoDTO.getGrpMemProps() != null) {
                    KnCorpGroupContactDTO memberProps =infoDTO.getGrpMemProps();
                    if( memberProps.getIsSupervisor() != null) {
                        pstmt.setInt(6, memberProps.getIsSupervisor());
                    }else{
                        pstmt.setNull(6, java.sql.Types.INTEGER);
                    }
                    if( memberProps.getIsBroadcaster() != null) {
                        pstmt.setInt(7, memberProps.getIsBroadcaster());
                    }else{
                        pstmt.setNull(7, java.sql.Types.INTEGER);
                    }
                    if( memberProps.getCallInitiateAllowed() != null) {
                        pstmt.setInt(8, memberProps.getCallInitiateAllowed());
                    }else{
                        pstmt.setNull(8, java.sql.Types.INTEGER);
                    }
                    if( memberProps.getCallTerminateAllowed() != null) {
                        pstmt.setInt(9, memberProps.getCallTerminateAllowed());
                    }else{
                        pstmt.setNull(9, java.sql.Types.INTEGER);
                    }
                    if( memberProps.getIncallAllowed() != null) {
                        pstmt.setInt(10, memberProps.getIncallAllowed());
                    }else{
                        pstmt.setNull(10, java.sql.Types.INTEGER);
                    }
                    if( memberProps.getVideoCallInitiateAllowed() != null) {
                        pstmt.setInt(11, memberProps.getVideoCallInitiateAllowed());
                    }else{
                        pstmt.setNull(11, java.sql.Types.INTEGER);
                    }
                    if( memberProps.getVideoCallReceiveAllowed() != null) {
                        pstmt.setInt(12, memberProps.getVideoCallReceiveAllowed());
                    }else{
                        pstmt.setNull(12, java.sql.Types.INTEGER);
                    }
                    if( memberProps.getVideoInCallAllowed() != null) {
                        pstmt.setInt(13, memberProps.getVideoInCallAllowed());
                    }else{
                        pstmt.setNull(13, java.sql.Types.INTEGER);
                    }
                    if( memberProps.getIsLocSupervisor() != null) {
                        pstmt.setInt(14, memberProps.getIsLocSupervisor());
                    }else{
                        pstmt.setNull(14, java.sql.Types.INTEGER);
                    }
                    if( memberProps.getIsOSMAuthorized() != null) {
                        pstmt.setInt(15, memberProps.getIsOSMAuthorized());
                    }else{
                        pstmt.setNull(15, java.sql.Types.INTEGER);
                    }
                }else{
                    pstmt.setNull(6, java.sql.Types.INTEGER);
                    pstmt.setNull(7, java.sql.Types.INTEGER);
                    pstmt.setNull(8, java.sql.Types.INTEGER);
                    pstmt.setNull(9, java.sql.Types.INTEGER);
                    pstmt.setNull(10, java.sql.Types.INTEGER);
                    pstmt.setNull(11, java.sql.Types.INTEGER);
                    pstmt.setNull(12, java.sql.Types.INTEGER);
                    pstmt.setNull(13, java.sql.Types.INTEGER);
                    pstmt.setNull(14, java.sql.Types.INTEGER);
                    pstmt.setNull(15, java.sql.Types.INTEGER);
                }
                pstmt.addBatch();
            }
            knLogger.debug( methodName, "Executing query - ", query);
            int[] count = pstmt.executeBatch();
            knLogger.debug( methodName, "EXIT: Query executed successfully.", count);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while inserting into the group info table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_PROFILE_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }


    public void deleteProfileGroupInfo(String userProfileId, List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteProfileGroupInfo(String, List<Integer>, KnPersisterTxn)";
        PreparedStatement pstmt = null;
        String query = null;
        knLogger.debug( methodName, "userProfileId - ", userProfileId, "groupIds", groupIds);

        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            //DELETE FROM DG.PROFILE_GROUP_INFO WHERE USER_PROFILEID = ? AND GROUPID = ?
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_PROFILE_GROUP_INFO);
            pstmt = conn.prepareStatement(query);
            for(Integer groupId : groupIds) {
                pstmt.setString(1, userProfileId);
                pstmt.setInt(2, groupId);
                pstmt.addBatch();
            }
            knLogger.debug( methodName, "Executing query - ", query);
            int[] count = pstmt.executeBatch();
            knLogger.debug( methodName, "EXIT: Query executed successfully.", count);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while deleting " + e,
                    pttServerId, KnDAOSourceTypes.XDM_PROFILE_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteProfileGroupInfoByGroupId(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteProfileGroupInfoByGroupId(int, KnPersisterTxn)";
        PreparedStatement pstmt = null;
        String query = null;
        knLogger.debug( methodName, "groupId", groupId);
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            //DELETE FROM DG.PROFILE_GROUP_INFO WHERE GROUPID = ?
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_PROFILE_GROUP_INFO_BY_GROUPID);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query - ", query);
            int count = pstmt.executeUpdate();
            knLogger.debug( methodName, "EXIT: Query executed successfully.", count);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while deleting " + e,
                    pttServerId, KnDAOSourceTypes.XDM_PROFILE_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }


    public void deleteProfileGroupInfoByProfileId(String profileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteProfileGroupInfoByProfileId(String, KnPersisterTxn)";
        PreparedStatement pstmt = null;
        String query = null;
        knLogger.debug( methodName, "profileId", profileId);
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            //DELETE FROM DG.PROFILE_GROUP_INFO WHERE GROUPID = ?
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_PROFILE_GROUP_INFO_BY_PROFILEID);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, profileId);
            knLogger.debug( methodName, "Executing query - ", query);
            int count = pstmt.executeUpdate();
            knLogger.debug( methodName, "EXIT: Query executed successfully.", count);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while deleting " + e,
                    pttServerId, KnDAOSourceTypes.XDM_PROFILE_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteProfileSharedList(String profileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteProfileSharedList(String, KnPersisterTxn)";
        PreparedStatement pstmt = null;
        String query = null;
        knLogger.debug( methodName, "profileId", profileId);
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            //DELETE FROM DG.USERPROFILE_SHAREDLIST WHERE USERPROFILEID = ?
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_USERPROFILE_SHAREDLIST_BY_PROFILEID);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, profileId);
            knLogger.debug( methodName, "Executing query - ", query);
            int count = pstmt.executeUpdate();
            knLogger.debug( methodName, "EXIT: Query executed successfully.", count);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while deleting " + e,
                    pttServerId, KnDAOSourceTypes.USERPROFILE_SHAREDLIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Set<KnCorpGroupListInfoDTO> retriveGroupProfileInfoByProfileId( String userProfileId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDBPersistenceException, KnDBConnectionException, KnDAOException {
        String methodName="getUserProfileForMcx( String userProfileId,boolean readOnly, KnPersisterTxn persisterTxn)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpUserProfileDTO knCorpUserProfileDTO=new KnCorpUserProfileDTO();
        Set<KnCorpGroupListInfoDTO> groupList=new HashSet<KnCorpGroupListInfoDTO>();
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
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = queryMapper.getQuery(SELECT_PROFILE_GROUP_INFO_BY_PROFILEID);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userProfileId);
            knLogger.debug( methodName, "Executing query - ", query);

            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "EXIT: Query executed successfully.", rs);


            while (rs.next()) {
                KnCorpGroupListInfoDTO knCorpGroupListInfoDTO=new KnCorpGroupListInfoDTO();
                knCorpGroupListInfoDTO.setGroupID(rs.getInt(1));
                knCorpGroupListInfoDTO.setGroupZone(rs.getInt(2));
                knCorpGroupListInfoDTO.setGroupChannel(rs.getInt(3));
                knCorpGroupListInfoDTO.setGroupPriority(rs.getInt(4));
                KnCorpGroupContactDTO knCorpGroupContactDTO= new KnCorpGroupContactDTO();
                knCorpGroupContactDTO.setIsSupervisor(rs.getInt(5));
                knCorpGroupContactDTO.setIsBroadcaster(rs.getInt(6));
                knCorpGroupContactDTO.setCallInitiateAllowed(rs.getInt(7));
                knCorpGroupContactDTO.setCallTerminateAllowed(rs.getInt(8));
                knCorpGroupContactDTO.setIncallAllowed(rs.getInt(9));
                knCorpGroupContactDTO.setVideoCallInitiateAllowed(rs.getInt(10));
                knCorpGroupContactDTO.setVideoCallReceiveAllowed(rs.getInt(11));
                knCorpGroupContactDTO.setVideoInCallAllowed(rs.getInt(12));
                knCorpGroupContactDTO.setIsLocSupervisor(rs.getInt(13));
                knCorpGroupContactDTO.setIsOSMAuthorized(rs.getInt(14));
                knCorpGroupListInfoDTO.setGrpMemProps(knCorpGroupContactDTO);
                groupList.add(knCorpGroupListInfoDTO);
            }
            knCorpUserProfileDTO.setGroupList(groupList);
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while reading " + e,
                    pttServerId, KnDAOSourceTypes.XDM_PROFILE_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.debug( methodName, "MCX Profile query  result DTO- ", knCorpUserProfileDTO);
        return groupList;
    }

    public Map<String,KnCorpGroupListInfoDTO> retriveGroupProfileInfoByGroupId( Integer groupId, KnPersisterTxn persisterTxn) throws KnDBPersistenceException, KnDBConnectionException, KnDAOException {
        String methodName="retriveGroupProfileInfoByGroupId( Integer groupId, KnPersisterTxn persisterTxn)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String,KnCorpGroupListInfoDTO> groupList=new  HashMap<String,KnCorpGroupListInfoDTO>();

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(SELECT_PROFILE_GROUP_INFO_BY_GROUPID);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query - ", query);

            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "EXIT: Query executed successfully.", rs);


            while (rs.next()) {
                KnCorpGroupListInfoDTO knCorpGroupListInfoDTO=new KnCorpGroupListInfoDTO();
                knCorpGroupListInfoDTO.setGroupID(rs.getInt(1));
                knCorpGroupListInfoDTO.setGroupZone(rs.getInt(2));
                knCorpGroupListInfoDTO.setGroupChannel(rs.getInt(3));
                knCorpGroupListInfoDTO.setGroupPriority(rs.getInt(4));
                KnCorpGroupContactDTO knCorpGroupContactDTO= new KnCorpGroupContactDTO();
                knCorpGroupContactDTO.setIsSupervisor(rs.getInt(5));
                knCorpGroupContactDTO.setIsBroadcaster(rs.getInt(6));
                knCorpGroupContactDTO.setCallInitiateAllowed(rs.getInt(7));
                knCorpGroupContactDTO.setCallTerminateAllowed(rs.getInt(8));
                knCorpGroupContactDTO.setIncallAllowed(rs.getInt(9));
                knCorpGroupContactDTO.setVideoCallInitiateAllowed(rs.getInt(10));
                knCorpGroupContactDTO.setVideoCallReceiveAllowed(rs.getInt(11));
                knCorpGroupContactDTO.setVideoInCallAllowed(rs.getInt(12));
                knCorpGroupContactDTO.setIsLocSupervisor(rs.getInt(13));
                knCorpGroupContactDTO.setIsOSMAuthorized(rs.getInt(14));
                knCorpGroupListInfoDTO.setGrpMemProps(knCorpGroupContactDTO);
                groupList.put(rs.getString(15).trim(), knCorpGroupListInfoDTO);
            }
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while reading " + e,
                    pttServerId, KnDAOSourceTypes.XDM_PROFILE_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug( methodName, "MCX Profile query  result DTO- ", groupList);
        return groupList;
    }

    public Collection<KnCorpGroupMemberDTO> groupProfileInfoByGroupId(Integer groupId, KnPersisterTxn persisterTxn) throws KnDBPersistenceException, KnDBConnectionException, KnDAOException {
        String methodName = "groupProfileInfoByGroupId( Integer groupId, KnPersisterTxn persisterTxn)";
        Connection conn = null;
        boolean ownedTxn = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpGroupMemberDTO> groupList = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            if (persisterTxn != null) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            query = queryMapper.getQuery(SELECT_PROFILE_GROUP_INFO_BY_GROUPID);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Query executed successfully.", rs);
            while (rs.next()) {
                KnCorpGroupMemberDTO knCorpGroupListInfoDTO = new KnCorpGroupMemberDTO();
                knCorpGroupListInfoDTO.setGroupId(rs.getInt(1));
                KnCorpGroupContactDTO knCorpGroupContactDTO = new KnCorpGroupContactDTO();
                knCorpGroupContactDTO.setIsSupervisor(rs.getInt(5));
                knCorpGroupContactDTO.setIsBroadcaster(rs.getInt(6));
                knCorpGroupContactDTO.setCallInitiateAllowed(rs.getInt(7));
                knCorpGroupContactDTO.setCallTerminateAllowed(rs.getInt(8));
                knCorpGroupContactDTO.setIncallAllowed(rs.getInt(9));
                knCorpGroupContactDTO.setVideoCallInitiateAllowed(rs.getInt(10));
                knCorpGroupContactDTO.setVideoCallReceiveAllowed(rs.getInt(11));
                knCorpGroupContactDTO.setVideoInCallAllowed(rs.getInt(12));
                knCorpGroupContactDTO.setIsLocSupervisor(rs.getInt(13));
                knCorpGroupListInfoDTO.setGroupContactDTO(knCorpGroupContactDTO);
                groupList.add(knCorpGroupListInfoDTO);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while reading " + e,
                    pttServerId, KnDAOSourceTypes.XDM_PROFILE_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.debug(methodName, "MCX Profile query  result DTO- ", groupList.size());
        return groupList;
    }

    public void deleteBulkProfileGroupInfo(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteBulkProfileGroupInfo()";
        PreparedStatement pstmt = null;
        String query = null;
        knLogger.debug( methodName, "groupIds", groupIds);
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            //>DELETE FROM DG.PROFILE_GROUP_INFO WHERE GROUPID = ?
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_PROFILE_GROUP_INFO_BY_GROUPID);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query - ", query);
            for(Integer groupId : groupIds) {
                pstmt.setInt(1, groupId);
                pstmt.addBatch();
            }
            int[] count = pstmt.executeBatch();
            knLogger.debug( methodName, "EXIT: Query executed successfully.", count);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while deleting " + e,
                    pttServerId, KnDAOSourceTypes.XDM_PROFILE_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteProfileGroupInfoByProfileIds(List<String> profileIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteProfileGroupInfoByProfileIds(List<String>, KnPersisterTxn)";
        PreparedStatement pstmt = null;
        String query = null;
        knLogger.debug( methodName, "profileIds :", profileIds);
        try {
            final AtomicInteger counter = new AtomicInteger(0);
            final int batchSize = 999;
            final Collection<List<String>> profileIdChunk = profileIds.stream()
                    .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / batchSize))
                    .values();
            for(List<String> profileIdChunks:profileIdChunk){
                Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                KnQueryMapper queryMapper = KnQueryMapper.getInstance();
                query = queryMapper.getQuery(KnPersisterConstants.DELETE_PROFILE_GROUP_INFO_BY_UPMID);
                final String finalQuery= KnGeneralUtil.replaceContactWithValue(query, UPMID, KnGeneralUtil.formCommaSeperatedIdList(profileIdChunks));
                knLogger.debug( methodName, "Executing query - ", finalQuery);
                pstmt = conn.prepareStatement(finalQuery);
                int count = pstmt.executeUpdate();
                knLogger.debug( methodName, "EXIT: deleted upmIds", count);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while deleting " + e,
                    pttServerId, KnDAOSourceTypes.XDM_PROFILE_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }
}
