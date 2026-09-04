/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpGroupMemberListDAO.java
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

import com.kodiak.common.dao.KnConnectionException;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
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
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpTalkGrpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGrpMemListDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;
import com.timesten.jdbc.TimesTenTypes;

import java.net.Inet4Address;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.kodiak.logger.KnLogger;

import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GET_GROUP_MEMBER_LIST_SP;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnXDMCorpGroupMemberListDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpGroupMemberListDAO.class);
    private String pttServerId;

    KnXDMCorpGroupMemberListDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("insert", "Unimplemented Methods");
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("update", "Unimplemented Methods");
    }

    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("delete", "Unimplemented Methods");
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("select", "Unimplemented Methods");
        return null;
    }

    public List<Integer> getGroupHavingMember(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupHavingMember(KnIPCorpContactDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : contactDTO - ", contactDTO);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<Integer> groupList = new ArrayList<Integer>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_MAPPED_TO_EXTERNAL_MEMBER);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, contactDTO.getMdn());
            pstmt.setInt(2, contactDTO.getCorpId());
            knLogger.debug(methodName, "Executing query -", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                groupList.add(rs.getInt(1));
            }
            knLogger.debug(methodName, "groupList size - ", groupList.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch group list for the external contact ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return groupList;
    }

    public Collection<KnCorpSubscriberDTO> getGroupsAllMemberList(int groupId, int maxGroupMemberLimit, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getGroupsAllMemberList(int,int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : groupId - ", groupId, ", maxGroupMemberLimit - ", maxGroupMemberLimit);
        int maxErrorLen = 512;
        int returnval = -1;
        String errormsg;
        ResultSet cursor = null;
        Collection<KnCorpSubscriberDTO> groupList = new ArrayList<KnCorpSubscriberDTO>();
        try {

            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            CallableStatement stmt = conn.prepareCall("{call DG.Kdk_POCDataRetrieval.retrieveCorpGrpMemList(?,?,?,?,?)}");
            stmt.setInt(1, groupId);
            stmt.setInt(2, maxGroupMemberLimit);
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.registerOutParameter(4, TimesTenTypes.CURSOR);
            stmt.registerOutParameter(5, Types.VARCHAR, maxErrorLen);
            stmt.executeUpdate();
            knLogger.debug(methodName, "Stored Procedure executed successfully.");
            returnval = stmt.getInt(3);
            if (returnval != 0) {
                cursor = (ResultSet) stmt.getObject(4);
            }
            errormsg = stmt.getString(5);
            knLogger.debug(methodName, "Error Message obtained from Stored procedure - ", errormsg);
            knLogger.debug(methodName, "cursor - ", cursor);
            if (cursor != null) {
                while (cursor.next()) {
                    KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                    subsc.setMdn(cursor.getString(1).trim());
                    groupList.add(subsc);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    knLogger.error(methodName, "Exception occured while retrieving the group member List - " + e);
                }
            }
            return groupList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the subsribers resourcelist details - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "SQLException occured while fetching the subsribers resourcelist details - ", e);
            throw KnDbUtil.processException(e, "Failed while fetching the subsribers resourcelist details " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, "");
        } finally {
            KnDbUtil.closeResultSet(cursor);
            knLogger.debug(methodName, "EXIT : Group list size returned is - ", groupList.size());
        }
    }

    public void insertIntoCorpGroupMemberList(Map<Integer, Collection<KnCorpContactDTO>> groupMemberList,
                                              KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertIntoCorpGroupMemberList(Map<Integer, Collection<KnCorpContactDTO>>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupMemberList - ", groupMemberList);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_INTO_GROUP_MEMBERLIST);
            knLogger.debug(methodName, "Executing query - ", query);
            Set<Integer> groupIdList = groupMemberList.keySet();
            pstmt = conn.prepareStatement(query);
            for (Integer groupId : groupIdList) {
                Collection<KnCorpContactDTO> memberList = groupMemberList.get(groupId);
                for (KnCorpContactDTO member : memberList) {
                    pstmt.setInt(1, groupId);
                    pstmt.setString(2, member.getMdn());
                    pstmt.setInt(3, member.getSupervisory());
                    pstmt.setInt(4, member.getBroadcaster());
                    int memberType = member.getMemberType();
                    if (memberType <= 0) {
                        pstmt.setNull(5, Types.INTEGER);
                    } else {
                        pstmt.setInt(5, memberType);
                    }
                    //Modified for 8.1.1 changes
                    pstmt.setInt(6,member.getCallInitiatePermission());
                    pstmt.setInt(7,member.getCallReceivePermission());
                    pstmt.setInt(8,member.getInCallPermission());
                    pstmt.setInt(9, member.getLocWatcher());
                    if(member.getGroupModifyPerm() != null){
                        pstmt.setInt(10, member.getGroupModifyPerm());
                    } else {
                        pstmt.setInt(10, ENABLED);
                    }
                    pstmt.setInt(11, member.getIsOSMAuthorize());
                    pstmt.setInt(12, member.getVideoCallInitiatePermission());
                    pstmt.setInt(13, member.getVideoCallReceivePermission());
                    pstmt.setInt(14, member.getVideoInCallPermission());
                    pstmt.setInt(15, member.getIsAffiliationEnabled());
                    if (member.getCorpId() == 0) {
                        pstmt.setNull(16, Types.INTEGER);
                    } else {
                        pstmt.setInt(16, member.getCorpId());
                    } pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to addGroupMembers in  groupmemberlist table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void insertBulkIntoCorpGroupMemberList(Map<Integer, KnCorpContactDTO> groupMemberList,
                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertBulkIntoCorpGroupMemberList(Map<Integer, Collection<KnCorpContactDTO>>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupMemberList - ", groupMemberList);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_INTO_GROUP_MEMBERLIST);
            knLogger.debug(methodName, "Executing query - ", query);
            Set<Integer> groupIdList = groupMemberList.keySet();
            pstmt = conn.prepareStatement(query);
            for (Integer groupId : groupIdList) {
                KnCorpContactDTO member = groupMemberList.get(groupId);
                pstmt.setInt(1, groupId);
                pstmt.setString(2, member.getMdn());
                pstmt.setInt(3, member.getSupervisory());
                pstmt.setInt(4, member.getBroadcaster());
                int memberType = member.getMemberType();
                if (memberType <= 0) {
                    pstmt.setNull(5, Types.INTEGER);
                } else {
                    pstmt.setInt(5, memberType);
                }
                //Modified for 8.1.1 changes
                pstmt.setInt(6, member.getCallInitiatePermission());
                pstmt.setInt(7, member.getCallReceivePermission());
                pstmt.setInt(8, member.getInCallPermission());
                pstmt.setInt(9, member.getLocWatcher());
                if (member.getGroupModifyPerm() != null) {
                    pstmt.setInt(10, member.getGroupModifyPerm());
                } else {
                    pstmt.setInt(10, ENABLED);
                }
                pstmt.setInt(11, member.getIsOSMAuthorize());
                pstmt.setInt(12, member.getVideoCallInitiatePermission());
                pstmt.setInt(13, member.getVideoCallReceivePermission());
                pstmt.setInt(14, member.getVideoInCallPermission());
                pstmt.setInt(15, member.getIsAffiliationEnabled());
                if (member.getCorpId() == 0) {
                    pstmt.setNull(16, Types.INTEGER);
                } else {
                    pstmt.setInt(16, member.getCorpId());
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to addGroupMembers in  groupmemberlist table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }


    public LinkedHashMap<Integer, LinkedList<Integer>> deleteCorpGroupMemberList(LinkedHashMap<Integer, LinkedList<String>> groupMemberList,
                                                                                 KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteCorpGroupMemberList(Map<Integer, Collection<String>>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupMemberList - ", KnGDPRTemplate.mapMdnAsLinkedValue(groupMemberList));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_GROUP_MEMBER_LIST);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            Set<Integer> groupIdList = groupMemberList.keySet();
            LinkedHashMap<Integer, LinkedList<Integer>> delGrpMemStatus = new LinkedHashMap<Integer, LinkedList<Integer>>();
            pstmt = conn.prepareStatement(query);
            for (Integer groupId : groupIdList) {
                LinkedList<String> memberList = groupMemberList.get(groupId);
                for (String member : memberList) {
                    pstmt.setInt(1, groupId);
                    pstmt.setString(2, member);
                    pstmt.setInt(3, groupId);
                    pstmt.setString(4, member);
                    pstmt.addBatch();
                }
            }
            int[] status = pstmt.executeBatch();
            knLogger.debug(methodName, "Query executed successfully.");

            int i = 0;
            for (Integer groupId : groupIdList) {
                LinkedList<Integer> delStatusList = new LinkedList<Integer>();
                for (String member : groupMemberList.get(groupId)) {
                    delStatusList.add(status[i++]);
                }
                delGrpMemStatus.put(groupId, delStatusList);
            }
            knLogger.debug(methodName, "EXIT", delGrpMemStatus);
            return delGrpMemStatus;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting from corp group member list table-  ",
                    e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while deleting from corp group member list table- ",
                    e);
            throw KnDbUtil.processException(e, "Failed to addGroupMembers in  groupmemberlist table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }


    public Map<Integer, Collection<String>> selectGroupMemberListForGroupIds(Collection<Integer> groupList,
                                                                             KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "selectGroupMemberListForGroupIds(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupList - ", groupList);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Collection<String>> groupMemberList = new HashMap<Integer, Collection<String>>();
        Connection conn = null;
        boolean ownedTxn = false;
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_MEMBERS_FOR_GROUPIDS);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");

            while (rs.next()) {
                int groupId = rs.getInt(1);
                Collection<String> groupMembers = groupMemberList.get(groupId);
                if (groupMembers == null) {
                    groupMembers = new ArrayList<String>();
                }
                groupMembers.add(rs.getString(2).trim());
                groupMemberList.put(groupId, groupMembers);
            }
            knLogger.debug(methodName, "EXIT: Returning groupMemberList size- ", groupMemberList.size());
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while selecting from corp group member list table-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while selecting from corp group member list table- ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve from  groupmemberlist table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return groupMemberList;
    }

    public int getGroupMemSize(Collection<Integer> groupList,
                               KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "selectGroupMemberListForGroupIds(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupList - ", groupList);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        int count = 0;
        Connection conn = null;
        boolean ownedTxn = false;
        try {
            if (persisterTxn != null) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_MEM_SIZE);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");

            while (rs.next()) {
                count = rs.getInt(1);
            }
            knLogger.debug(methodName, "EXIT: Returning groupMemberList size- ", count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while selecting from corp group member list table-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while selecting from corp group member list table- ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve from  groupmemberlist table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return count;
    }

    public int getMCXGroupMemSize(Collection<Integer> groupList,
                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getMCXGroupMemSize(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupList - ", groupList);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        int count = 0;
        Connection conn = null;
        boolean ownedTxn = false;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            if (persisterTxn != null) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_MCX_GROUP_MEM_SIZE);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");

            while (rs.next()) {
                count = rs.getInt(1);
            }
            knLogger.debug(methodName, "EXIT: Returning groupMemberList size- ", count);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while selecting from corp group member list table-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while selecting from corp group member list table- ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve from  groupmemberlist table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return count;
    }

    /**
     * This method is used to fetch the supervisor of the group with is_supervisory as 1 or 2
     *
     * @param groupId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnCorpGroupMemberDTO> getGroupSupervisorMembers(int groupId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupSupervisorMembers(int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupId - ", groupId);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, KnCorpGroupMemberDTO> supervisoryMembers = new HashMap<String, KnCorpGroupMemberDTO>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUPS_SUPERVISORY_MEMBERS);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                KnCorpGroupMemberDTO groupMem = new KnCorpGroupMemberDTO();
                String mdn = rs.getString(1).trim();
                groupMem.setMdn(mdn);
                groupMem.setSupervisory(rs.getInt(2));
                groupMem.setBroadcaster(rs.getInt(3));
                groupMem.setCallInitiatePermission(rs.getInt(4));
                groupMem.setCallReceivePermission(rs.getInt(5));
                groupMem.setInCallPermission(rs.getInt(6));
                groupMem.setVideoCallInitiatePermission(rs.getInt(7));
                groupMem.setVideoCallReceivePermission(rs.getInt(8));
                groupMem.setVideoInCallPermission(rs.getInt(9));
                groupMem.setLocWatcher(rs.getInt(10));
                groupMem.setIsOSMAuthorize(rs.getInt(11));
                groupMem.setCorpId(rs.getInt(12));
                groupMem.setServiceAuthStatus(rs.getInt(13));
                groupMem.setHierarchyId(rs.getString(14));
                supervisoryMembers.put(mdn, groupMem);
            }
            knLogger.debug(methodName, "EXIT list size - ", supervisoryMembers.size()," supervisoryMembers :",supervisoryMembers);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to  retrieve the group supervisor ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return supervisoryMembers;
    }

    /**
     * This method is used to fetch the supervisor of the group with is_supervisory as 1.
     *
     * @param groupIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Set<Integer> getGroupSupervisorList(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupSupervisorList(Collection<Integer>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupIds -  -", groupIds);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Set<Integer> corpGroupListIds = new HashSet<>();
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(CORPGROUPID_FOR_BROADCASTER);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query -  " , "'" , query , "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                corpGroupListIds.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to  retrieve the group supervisor ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return corpGroupListIds;
    }

    public void updateGroupMemberListSupervisorList(Collection<KnCorpGroupMemberDTO> supervisorMemberList,
                                                    int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateGroupMemberListSupervisorList(Collection<KnCorpGroupMemberDTO>, int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : supervisorMemberList - ", supervisorMemberList, "groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_MEMBERS_SUPERVISOR_ATTRIBUTE);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            for (KnCorpGroupMemberDTO memberDTO : supervisorMemberList) {
                pstmt.setInt(1, memberDTO.getSupervisory());
                //Modified for 8.1.1 changes
                pstmt.setInt(2, memberDTO.getCallInitiatePermission());
                pstmt.setInt(3, memberDTO.getCallReceivePermission());
                pstmt.setInt(4, memberDTO.getInCallPermission());
                pstmt.setInt(5, memberDTO.getLocWatcher());
                if(memberDTO.getGroupModifyPerm() != null){
                    pstmt.setInt(6, memberDTO.getGroupModifyPerm());
                } else {
                    pstmt.setInt(6, ENABLED);
                }
                pstmt.setInt(7, memberDTO.getIsOSMAuthorize());
                pstmt.setInt(8, memberDTO.getVideoCallInitiatePermission());
                pstmt.setInt(9, memberDTO.getVideoCallReceivePermission());
                pstmt.setInt(10, memberDTO.getVideoInCallPermission());
                pstmt.setString(11, memberDTO.getMdn());
                pstmt.setInt(12, groupId);

                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateGroupMemberListSupervisorList - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while updateGroupMemberListSupervisorList - ", e);
            throw KnDbUtil.processException(e, "Failed while updateGroupMemberListSupervisorList - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteAllCorpGroupMemberList(Collection<Integer> groupIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteAllCorpGroupMemberList(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIdList - ", groupIdList);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_GROUP_MEMBER_LIST_FOR_A_GROUP);
            //query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            for(Integer groupId: groupIdList){
                pstmt.setInt(1, groupId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting from corp group member list table-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while deleting from corp group member list table- ", e);
            throw KnDbUtil.processException(e, "Failed to addGroupMembers in  groupmemberlist table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<String, HashMap<Integer, String>> getGroupListStatus(Collection<Integer> groupIdLst,
                                                                    KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupListStatus(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIdLst - ", groupIdLst);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, HashMap<Integer, String>> groupListStatus = new HashMap<String, HashMap<Integer, String>>();
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_LIST_STATUS);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdLst));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            HashMap<Integer, String> deleteGroupIdList = new HashMap<Integer, String>();
            HashMap<Integer, String> modifiedGroupIdList = new HashMap<Integer, String>();
            while (rs.next()) {
                int count = rs.getInt(2);
                int groupId = rs.getInt(1);
                knLogger.debug(methodName, "groupId ",groupId," count :",count);
                String grpName=null;
                if(rs.getString(3) != null)
                    grpName=new String(rs.getString(3).trim().getBytes("8859_1"),"UTF-8");
                if (count == 0) {
                    deleteGroupIdList.put(groupId, (String.valueOf(rs.getInt(4))).concat("_").concat(grpName));
                }else{
                    modifiedGroupIdList.put(groupId, (String.valueOf(rs.getInt(4))).concat("_").concat(grpName));
                }
            }
            if (!deleteGroupIdList.isEmpty()) {
                groupListStatus.put(KnPersisterConstants.DELETED, deleteGroupIdList);
            }
            knLogger.debug(methodName, "deleteGroupIdList",deleteGroupIdList);
            knLogger.debug(methodName, "modifiedGroupIdList",modifiedGroupIdList);
            if (!modifiedGroupIdList.isEmpty()) {
                groupListStatus.put(KnPersisterConstants.MODIFIED, modifiedGroupIdList);
            }
            knLogger.debug(methodName, "EXIT");
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
            return groupListStatus;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the group list status -  ", e);
            throw e;
        } catch (Exception e) {
            if(ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "Unexpected Exception occured while retrieving the group list status- ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group list status  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public LinkedHashMap<Integer, LinkedList<String>> getGroupDispatcherSubscriber(Collection<Integer> groupIdList, int supervisor, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getGroupDispatcherSubscriber(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIdList - ", groupIdList);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        boolean ownedTxn = false;
        LinkedHashMap<Integer, LinkedList<String>> dispatchSubscriberMap = new LinkedHashMap<Integer, LinkedList<String>>();
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
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_DISPTACH_SUBSCRIBERS);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, supervisor);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                int groupId = rs.getInt(2);
                String mdn = rs.getString(1).trim();
                LinkedList<String> mdnList = dispatchSubscriberMap.get(groupId);
                if (mdnList == null || mdnList.isEmpty()) {
                    mdnList = new LinkedList<String>();
                }
                mdnList.add(mdn);
                dispatchSubscriberMap.put(groupId, mdnList);
            }
            knLogger.debug(methodName, "EXIT. DIspatcher SubscriberMap size - ", dispatchSubscriberMap.size());
            return dispatchSubscriberMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the group dispatcher subscribers -  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving the  group dispatcher subscribers- ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group dispatcher subscribers " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
    }

    public LinkedHashMap<Integer, LinkedList<String>> getGroupMdnSubscriber(Collection<Integer> groupIdList, int memberType, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getGroupMdnSubscriber(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIdList - ", groupIdList);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        boolean ownedTxn = false;
        LinkedHashMap<Integer, LinkedList<String>> groupMdnSubscriberMap = new LinkedHashMap<Integer, LinkedList<String>>();
        try {
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
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_MDN_SUBSCRIBER);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, memberType);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                int groupId = rs.getInt(2);
                String mdn = rs.getString(1).trim();
                LinkedList<String> mdnList = groupMdnSubscriberMap.get(groupId);
                if (mdnList == null || mdnList.isEmpty()) {
                    mdnList = new LinkedList<String>();
                }
                mdnList.add(mdn);
                groupMdnSubscriberMap.put(groupId, mdnList);
            }
            knLogger.debug(methodName, "EXIT. DIspatcher SubscriberMap size - ", groupMdnSubscriberMap.size());
            return groupMdnSubscriberMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the group Mdn subscribers -  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving the  group mdn subscribers- ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group mdn subscribers " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
    }

    public Map<Integer, Collection<String>> getGroupLocWatcherSubscriber(Collection<Integer> groupIdList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getGroupLocWatcherSubscriber(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIdList - ", groupIdList);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Collection<String>> locWatcherSubscriberMap = new HashMap<Integer, Collection<String>>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GROUP_CORP_MEMBER_LIST);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                if (IS_LOCWATCHER == rs.getInt(4)) {
                    int groupId = rs.getInt(1);
                    String mdn = rs.getString(2).trim();
                    Collection<String> mdnList = locWatcherSubscriberMap.get(groupId);
                    if (mdnList == null || mdnList.isEmpty()) {
                        mdnList = new ArrayList<>();
                    }
                    mdnList.add(mdn);
                    locWatcherSubscriberMap.put(groupId, mdnList);
                }
            }
            knLogger.debug(methodName, "EXIT. LocWatcher SubscriberMap size - ", locWatcherSubscriberMap.size());
            return locWatcherSubscriberMap;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving the  group LocWatcher subscribers- ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group LocWatcher subscribers " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateCorpGroupMemberList(Collection<Integer> groupList, String oldMdn, String newMdn,
                                          KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateIsOSMAuthorize(Collection<Integer>, String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupList - ", groupList, ", oldMdn - ", KnGDPRTemplate.mdn(oldMdn), ", newMdn - ", KnGDPRTemplate.mdn(newMdn));
        Statement stmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_MEMBERLIST_TABLE);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupList));
            query = KnDbUtil.replaceValInQry(query, newMdn);
            query = KnDbUtil.replaceValInQry(query, oldMdn);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            knLogger.debug(methodName, "EXIT: Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating into the corp group member list table-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while updating into the corp group member list table- ", e);
            throw KnDbUtil.processException(e, "Failed to updating Group Members in  groupmemberlist table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeStatement(stmt);
        }
    }

    public Map<String, KnCorpGroupMemberDTO> getGroupMemberDetailsListInfo(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupMemberDetailsListInfo(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupId - ", groupId);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, KnCorpGroupMemberDTO> grpMemDetailsMap = new HashMap<String, KnCorpGroupMemberDTO>();
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GROUP_MEMBER_DETAILS_LIST);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                KnCorpGroupMemberDTO subsc = new KnCorpGroupMemberDTO();
                String mdn = rs.getString(1).trim();
                subsc.setMdn(mdn);
                subsc.setSupervisory(rs.getInt(2));
                grpMemDetailsMap.put(mdn, subsc);
            }
            knLogger.debug(methodName, "EXIT. Response Map size  - ", grpMemDetailsMap.size());
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getGroupMemberDetailsListInfo-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getGroupMemberDetailsListInfo- ", e);
            throw KnDbUtil.processException(e, "Failed to getGroupMemberDetailsListInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return grpMemDetailsMap;
    }

    public Map<String, Integer> getExternalSubscriberGroupCount(Collection<String> externalMdnList,
                                                                int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExternalSubscriberGroupCount(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : externalMdnList - ", externalMdnList ==null ? externalMdnList : KnGDPRTemplate.mdnList(externalMdnList), ", corpId - ", corpId);
        Connection conn = null;
        PreparedStatement pStmt = null;
        String query = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        Map<String, Integer> subscGrpCount = new HashMap<String, Integer>();
        try {
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
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
            var mdnListArray = new ArrayList<>(externalMdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                KnQueryMapper queryMapper = KnQueryMapper.getInstance();
                query = queryMapper.getQuery(GET_SUBSC_GROUP_COUNT);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subsList, query, MDNLIST);
                int index = 2;
                knLogger.debug(methodName, "Executing query", query);
                pStmt = conn.prepareStatement(query);
                pStmt.setInt(1, corpId);

                for (String externalMdn : subsList)
                    pStmt.setString(index++, externalMdn);

                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    subscGrpCount.put(rs.getString(1).trim(), rs.getInt(2));
                }
            }
            return subscGrpCount;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getExternalSubscriberGroupCount -  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getExternalSubscriberGroupCount - ", e);
            throw KnDbUtil.processException(e, "Failed while getExternalSubscriberGroupCount  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug(methodName, "EXIT : The subscriber group count lsit size - ", subscGrpCount.size());
        }
    }

    public Map<Integer, Integer> getGroupListForSubsWithCorpId(Collection<String> mdnList, boolean readOnly,
                                                               KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupListForSubsWithCorpId(Collection<String>, boolean, KnPersisterTxn)";
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, Integer> groupCorpIdMap = new HashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBERS_GROUPS_LIST_WITH_CORP_ID);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                groupCorpIdMap.put(rs.getInt(1), rs.getInt(3));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while fetching group member corp info - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while fetching group member corp info - ", e);
            throw KnDbUtil.processException(e, "Failed to getGroupListForSubsWithCorpId " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT: groupCorpIdMap - ", groupCorpIdMap);
        }
        return groupCorpIdMap;
    }

    /**
     * This method will retrieve the members from the dg.corpgroupmemberlist table based on the supervisory value.
     *
     * @param groupIds
     * @param clientType
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Collection<String>> getGroupSpecificSubsc(Collection<Integer> groupIds, int clientType, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getGroupSpecificSubsc(Collection<Interger>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : groupIds - ", groupIds, ", clientType - ", clientType);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Collection<String>> grpSubscMap = new HashMap<Integer, Collection<String>>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_SUBSC_OF_TYPE);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            query = KnDbUtil.replaceValInQry(query, clientType);
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                int grpId = rs.getInt(1);
                Collection<String> mdnList = grpSubscMap.get(grpId);
                if (mdnList == null) {
                    mdnList = new ArrayList<String>();
                }
                mdnList.add(rs.getString(2).trim());
                grpSubscMap.put(grpId, mdnList);
            }
            return grpSubscMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getGroupSpecificSubsc -  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getGroupSpecificSubsc - ", e);
            throw KnDbUtil.processException(e, "Failed while getGroupInterOpSubsc  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : The group subsc Map size is  - ", grpSubscMap.size());
        }
    }

    /**
     * This method is used to fetch the specific privilege members of the groups for the corporate
     *
     * @param supervisorTypes
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getCoporateGrpSpecificSubsc(Collection<Integer> supervisorTypes, int
            corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCoporateGrpSpecificSubsc(Collection<Interger>, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : supervisorTypes - ", supervisorTypes, " , corpId - ", corpId);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Collection<KnCorpGroupMemberDTO>> corpGrpSupervisorMap = new ConcurrentHashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PRIVILLEGED_MEMBERS_IF_GRP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, SUPERVISORVALUE, formIntegerCommaSeperatedIdList(supervisorTypes));
            query = KnDbUtil.replaceValInQry(query, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                int grpId = rs.getInt(1);
                Collection<KnCorpGroupMemberDTO> mdnList = corpGrpSupervisorMap.get(grpId);
                if (mdnList == null) {
                    mdnList = new ArrayList<KnCorpGroupMemberDTO>();
                }
                KnCorpGroupMemberDTO contact = new KnCorpGroupMemberDTO();
                contact.setMdn(rs.getString(2).trim());
                contact.setSupervisory(rs.getInt(3));
                contact.setBroadcaster(rs.getInt(4));
                int memberType = rs.getInt(5);
                if(memberType == SG_MDN_MEMBER_TYPE){
                    contact.setClientType(SUBSCR_CLIENT_TYPE.GROUPMDN.value());
                } else if(memberType == SG_MDN_PATCH_MEMBER_TYPE){
                    contact.setClientType(SUBSCR_CLIENT_TYPE.SGMDNPATCH.value());
                }
                contact.setCallInitiatePermission(rs.getInt(6));
                contact.setCallReceivePermission(rs.getInt(7));
                contact.setInCallPermission(rs.getInt(8));
                contact.setLocWatcher(rs.getInt(9));
                contact.setVideoCallInitiatePermission(rs.getInt(10));
                contact.setVideoCallReceivePermission(rs.getInt(11));
                contact.setVideoInCallPermission(rs.getInt(12));
                mdnList.add(contact);
                corpGrpSupervisorMap.put(grpId, mdnList);
            }

        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getGroupInterOpSubsc  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXIT : The group subsc Map size is  - ", corpGrpSupervisorMap.size());
        return corpGrpSupervisorMap;
    }


    /**
     * This method is used to fetch the specific privilege members of the groups for the groupIds. This is read only method.
     *
     * @param supervisorTypes
     * @param groupIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getGrpSpecificSubsc(Collection<Integer> supervisorTypes, Collection<Integer>
            groupIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGrpSpecificSubsc(Collection<Interger>, Collection<Interger>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : supervisorTypes - ", supervisorTypes, " , groupIds - ", groupIds);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Collection<KnCorpGroupMemberDTO>> corpGrpSupervisorMap = new ConcurrentHashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PRIVILLEGED_MEMBERS_FOR_GRPLISTS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, SUPERVISORVALUE, formIntegerCommaSeperatedIdList(supervisorTypes));
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                int grpId = rs.getInt(1);
                Collection<KnCorpGroupMemberDTO> mdnList = corpGrpSupervisorMap.get(grpId);
                if (mdnList == null) {
                    mdnList = new ArrayList<KnCorpGroupMemberDTO>();
                }
                KnCorpGroupMemberDTO contact = new KnCorpGroupMemberDTO();
                contact.setMdn(rs.getString(2).trim());
                contact.setSupervisory(rs.getInt(3));
                contact.setBroadcaster(rs.getInt(4));
                int memberType = rs.getInt(5);
                if(memberType == SG_MDN_MEMBER_TYPE){
                    contact.setClientType(SUBSCR_CLIENT_TYPE.GROUPMDN.value());
                } else if(memberType == SG_MDN_PATCH_MEMBER_TYPE){
                    contact.setClientType(SUBSCR_CLIENT_TYPE.SGMDNPATCH.value());
                }
                contact.setCallInitiatePermission(rs.getInt(6));
                contact.setCallReceivePermission(rs.getInt(7));
                contact.setInCallPermission(rs.getInt(8));
                contact.setLocWatcher(rs.getInt(9));
                contact.setVideoCallInitiatePermission(rs.getInt(10));
                contact.setVideoCallReceivePermission(rs.getInt(11));
                contact.setVideoInCallPermission(rs.getInt(12));
                mdnList.add(contact);
                corpGrpSupervisorMap.put(grpId, mdnList);
            }

        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getGroupInterOpSubsc  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXIT : The group subsc Map size is  - ", corpGrpSupervisorMap.size());
        return corpGrpSupervisorMap;
    }


    /**
     * This method is used to fetch the SG privilege members of the groups for the corporate
     *
     * @param groupMdnList
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getGroupListForGroupMDN(Collection<String> groupMdnList, int
            corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupListForGroupMDN(List<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : groupMdnList - ", groupMdnList == null ? groupMdnList : KnGDPRTemplate.mdnList(groupMdnList), " , corpId - ", corpId);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Collection<KnCorpGroupMemberDTO>> corpGrpSupervisorMap = new ConcurrentHashMap<Integer, Collection<KnCorpGroupMemberDTO>>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            String queryMain = queryMapper.getQuery(GET_SG_PRIVILLEGED_MEMBERS_IF_GRP);
            Collection<Collection<String>> collList = KnDbUtil.getCollectionList((List<String>) groupMdnList, 1000);
            for (Collection<String> gMdns : collList) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                query = KnDbUtil.replaceValInQry(queryMain, corpId);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(gMdns));
                knLogger.debug(methodName, "Executing query - ", query);
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                knLogger.debug(methodName, "query executed successfully");
                while (rs.next()) {
                    int grpId = rs.getInt(1);
                    Collection<KnCorpGroupMemberDTO> mdnList = corpGrpSupervisorMap.get(grpId);
                    if (mdnList == null) {
                        mdnList = new ArrayList<KnCorpGroupMemberDTO>();
                    }
                    KnCorpGroupMemberDTO contact = new KnCorpGroupMemberDTO();
                    contact.setMdn(rs.getString(2).trim());
                    contact.setClientType(8);
                    mdnList.add(contact);
                    corpGrpSupervisorMap.put(grpId, mdnList);
                }
            }

        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getGroupInterOpSubsc  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXIT : The group subsc Map size is  - ", corpGrpSupervisorMap.size());
        return corpGrpSupervisorMap;
    }

    public LinkedList<String> getExternalGrpMembers(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExternalGrpMembers(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        LinkedList<String> extMemberList = new LinkedList<String>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_EXTERNAL_MEMBERS);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, groupId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                extMemberList.add(rs.getString(1).trim());
            }
            return extMemberList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getExternalGrpMembers -  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getExternalGrpMembers - ",
                    e);
            throw KnDbUtil.processException(e, "Failed while getExternalGrpMembers  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT : The group subsc Map size is  - ", extMemberList.size());
        }
    }

    /**
     * This method returns a Map of group member MDN and its is_supervisor value.
     *
     * @param groupId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnCorpGroupMemberDTO> getGroupMembersList(int groupId,boolean readonly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupMembersList(int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY Point : groupId - ", groupId,readonly);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, KnCorpGroupMemberDTO> grpMemberMap = new HashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GROUP_MEMBER_LIST);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query executed successfully.");
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                KnCorpGroupMemberDTO groupMemberDTO = new KnCorpGroupMemberDTO();
                String mdn = rs.getString(1).trim();
                groupMemberDTO.setMdn(mdn);
                groupMemberDTO.setSupervisory(rs.getInt(2));
                groupMemberDTO.setBroadcaster(rs.getInt(3));
                groupMemberDTO.setCallInitiatePermission(rs.getInt(4));
                groupMemberDTO.setCallReceivePermission(rs.getInt(5));
                groupMemberDTO.setInCallPermission(rs.getInt(6));
                groupMemberDTO.setLocWatcher(rs.getInt(7));
                groupMemberDTO.setGroupModifyPerm(rs.getInt(8));
                groupMemberDTO.setIsOSMAuthorize(rs.getInt(9));
                groupMemberDTO.setVideoCallInitiatePermission(rs.getInt(10));
                groupMemberDTO.setVideoCallReceivePermission(rs.getInt(11));
                groupMemberDTO.setVideoInCallPermission(rs.getInt(12));
                groupMemberDTO.setIsAffiliationEnabled(rs.getInt(13));
                groupMemberDTO.setCorpId(rs.getInt(14));
                groupMemberDTO.setHierarchyId(rs.getString(15));
                grpMemberMap.put(mdn, groupMemberDTO);
            }
            knLogger.debug(methodName, "EXIT. Response Map size  - ", grpMemberMap.size(),KnGDPRTemplate.mapKeyMdn(grpMemberMap));
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getGroupMembersList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return grpMemberMap;
    }

    /**
     * This method returns a List of DTO of group member MDN and its is_supervisor and LocWatcher value.
     *
     * @param groupIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnCorpGroupMemberDTO> getCorpGroupMembersList(Collection<Integer> groupIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpGroupMembersList(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupIds - ", groupIds, " readOnly :", readOnly);
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Collection<KnCorpGroupMemberDTO> grpMemberMap = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GROUP_CORP_MEMBER_LIST);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Query executed successfully.");
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                KnCorpGroupMemberDTO groupMemberDTO = new KnCorpGroupMemberDTO();
                String mdn = rs.getString(2).trim();
                groupMemberDTO.setMdn(mdn);
                groupMemberDTO.setGroupId(rs.getInt(1));
                groupMemberDTO.setSupervisory(rs.getInt(3));
                groupMemberDTO.setLocWatcher(rs.getInt(4));
                groupMemberDTO.setIsOSMAuthorize(rs.getInt(5));
                groupMemberDTO.setCallInitiatePermission(rs.getInt(6));
                groupMemberDTO.setCallReceivePermission(rs.getInt(7));
                groupMemberDTO.setInCallPermission(rs.getInt(8));
                groupMemberDTO.setVideoCallInitiatePermission(rs.getInt(9));
                groupMemberDTO.setVideoCallReceivePermission(rs.getInt(10));
                groupMemberDTO.setVideoInCallPermission(rs.getInt(11));
                groupMemberDTO.setBroadcaster(rs.getInt(12));
                groupMemberDTO.setCorpId(rs.getInt(13));
                grpMemberMap.add(groupMemberDTO);
            }
            knLogger.debug(methodName, "EXIT. Response Map size  - ", grpMemberMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getGroupMembersList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return grpMemberMap;
    }

    /**
     * This method returns a all the groupMember where at least one locWatcher present.
     *
     * @param groupIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnCorpGroupMemberDTO> getCorpGroupMemberForLocWatcher(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpGroupMemberForLocWatcher(Collection<Integer> , KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupIds - ", groupIds);
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Collection<KnCorpGroupMemberDTO> grpMemberMap = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GROUP_LOCWATCHER_LIST);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            query = KnDbUtil.replaceValInQry(query, LOCWATCHER);
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Query executed successfully.");
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                KnCorpGroupMemberDTO groupMemberDTO = new KnCorpGroupMemberDTO();
                String mdn = rs.getString(2).trim();
                groupMemberDTO.setMdn(mdn);
                groupMemberDTO.setGroupId(rs.getInt(1));
                groupMemberDTO.setSupervisory(rs.getInt(3));
                groupMemberDTO.setLocWatcher(rs.getInt(4));
                grpMemberMap.add(groupMemberDTO);
            }
            knLogger.debug(methodName, "EXIT. Response Map size  - ", grpMemberMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getGroupMembersList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return grpMemberMap;
    }

    public Collection<String> getCorpGroupMemberIsLocWatcher(Collection<Integer> groupIds, int isLocwatcher, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpGroupMemberIsLocWatcher(Collection<Integer>, isLocwatcher, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupIds - ", groupIds);
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        List<String> grpMemberMap = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GROUP_ISLOCWATCHER_LIST);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            query = KnDbUtil.replaceValInQry(query, isLocwatcher);
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Query executed successfully.");
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                grpMemberMap.add(mdn);
            }
            knLogger.debug(methodName, "EXIT. Response Map size  - ", grpMemberMap.size());
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occured while getCorpGroupMemberIsLocWatcher -  ", e);
            throw KnDbUtil.processException(e, "Failed to getGroupMembersList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return grpMemberMap;
    }

    /**
     * Method to retrieve group member for location supervisor for requested groupIds. This is read only method.
     */
    public Map<Integer, List<String>> getCorpGroupMemberForLocSupervisor(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpGroupMemberForLocSupervisor(Collection<Integer> , KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupIds - ", groupIds);
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, List<String>> grpMemberMap = new HashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GROUP_SUPERVISOR_LOCWATCHER_LIST);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            query = KnDbUtil.replaceValInQry(query, LOCWATCHER);
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Query executed successfully.");
            rs = stmt.executeQuery(query);
            List<String> members = null;
            while (rs.next()) {
                Integer groupId = rs.getInt(1);
                String mdn = rs.getString(2).trim();
                if(grpMemberMap.get(groupId) != null){
                    members = grpMemberMap.get(groupId);
                    members.add(mdn);
                } else {
                    members = new ArrayList<>();
                    members.add(mdn);
                    grpMemberMap.put(groupId, members);
                }
            }
            knLogger.debug(methodName, "EXIT. Response Map size  - ", grpMemberMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getCorpGroupMemberForLocSupervisor ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return grpMemberMap;
    }

    /**
     * This method calls a SP to get the truncated group members
     *
     * @param groupId
     * @param maxGroupMemberLimit
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<String> getTruncatedGroupMems(int groupId, int maxGroupMemberLimit, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getTruncatedGroupMems(int, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : groupId - ", groupId, ", maxGroupMemberLimit - ", maxGroupMemberLimit);
        int maxErrorLen = 512;
        int returnval = -1;
        String errormsg;
        ResultSet cursor = null;
        List<String> groupList = new ArrayList<String>();
        String query;
        CallableStatement stmt = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_MEMBER_LIST_SP);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            stmt = conn.prepareCall(query);
            stmt.setInt(1, groupId);
            stmt.setInt(2, maxGroupMemberLimit);
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.registerOutParameter(4, TimesTenTypes.CURSOR);
            stmt.registerOutParameter(5, Types.VARCHAR, maxErrorLen);
            knLogger.debug(methodName, "Executing SP", query);
            stmt.executeUpdate();
            knLogger.debug(methodName, "Stored Procedure executed successfully.");
            returnval = stmt.getInt(3);
            if (returnval != 0) {
                cursor = (ResultSet) stmt.getObject(4);
            }
            errormsg = stmt.getString(5);
            knLogger.debug(methodName, "Error Message obtained from Stored procedure - ", errormsg);
            if (cursor != null && !cursor.wasNull()) {
                while (cursor.next()) {
                    groupList.add(cursor.getString(1).trim());
                }
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching the subsribers resourcelist details ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, "");
        } finally {
            KnDbUtil.closeResultSet(cursor);
            KnDbUtil.closeStatement(stmt);
        }
        return groupList;
    }

    /**
     * This method returns a Map which contains groupId and the list of deleted members present in the groupId.
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, List<KnCorpGrpMemListDTO>> getSubsGroupIdListMap(List<String> mdnList, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        String methodName = "getSubsGroupIdList(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point: mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        String query = null;
        ResultSet rs = null;
        Statement stmt = null;
        Map<Integer, List<KnCorpGrpMemListDTO>> grpSubsListMap = new HashMap<Integer, List<KnCorpGrpMemListDTO>>(mdnList.size());
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBERS_GROUP_LIST);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                int grpId = rs.getInt(2);
                List<KnCorpGrpMemListDTO> subsList = grpSubsListMap.get(grpId);
                if (subsList == null) {
                    subsList = new ArrayList<KnCorpGrpMemListDTO>();
                }
                KnCorpGrpMemListDTO grpMemListDTO = new KnCorpGrpMemListDTO();
                grpMemListDTO.setGroupMem(rs.getString(1).trim());
                grpMemListDTO.setSuperVisor(rs.getInt(3));
                subsList.add(grpMemListDTO);
                grpSubsListMap.put(grpId, subsList);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetaching the mdn contact list ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_CONTACT_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXIT. GroupSubslist Map ", grpSubsListMap);
        return grpSubsListMap;
    }

    /**
     * This method deleted the group members in mdnList from dg.corpgroupmemberlist table.
     *
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteGrpMemList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteGrpMemList(List<String>,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORPGROUP_MEMBER_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteing group member list",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public List<String> selectGroupMemberList(int groupId,boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectGroupMemberList(int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : groupId- ", groupId, " readOnly :", readOnly);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> groupMemberList = new ArrayList<String>();
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_CORP_LIST_MEMBERS);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                groupMemberList.add(rs.getString(1).trim());
            }
            knLogger.debug(methodName, "EXIT : Group Members list - ", KnGDPRTemplate.mdnList(groupMemberList));
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while selecting members of the group " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return groupMemberList;
    }

    public List<Integer> getBroadcstGroupList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getBroadcstGroupList(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdn- ", KnGDPRTemplate.mdn(mdn));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Integer> grpIdList = new ArrayList<>();
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBSC_BROADCAST_BROUPS);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2, 1);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                grpIdList.add(rs.getInt(1));
            }
            knLogger.debug(methodName, "EXIT : Group list size returned is - ", grpIdList.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while selecting members of the group " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return grpIdList;
    }

    public void updateGrpBroadcasters(Map<Integer, List<String>> groupMemMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateGrpBroadcasters(Map ,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupMemMap - ", groupMemMap);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_BROADCASTERS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (Map.Entry<Integer, List<String>> entry : groupMemMap.entrySet()) {
                int groupId = entry.getKey();
                List<String> memList = entry.getValue();
                for (String mdn : memList) {
                    pstmt.setInt(1, 0);
                    pstmt.setString(2, mdn);
                    pstmt.setInt(3, groupId);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteing group member list",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public List<Integer> getSupervisorGroups(String mdn, int supervisor, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSupervisorGroups(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdn- ", KnGDPRTemplate.mdn(mdn), "  supervisor - ", supervisor);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Integer> grpIdList = new ArrayList<>();
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_LIST_SUPERVISOR);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2, supervisor);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                grpIdList.add(rs.getInt(1));
            }
            knLogger.debug(methodName, "EXIT : Group list size returned is - ", grpIdList.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while selecting members of the group " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return grpIdList;
    }

    public List<Integer> getSupervisorGroups(List<String> mdnList, int supervisor, KnPersisterTxn persisterTxn) throws KnDBPersistenceException, KnDBConnectionException,KnDAOException {
        String methodName = "getSupervisorGroups(List<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : mdn- ", KnGDPRTemplate.mdnList(mdnList), "  supervisor - ", supervisor);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Integer> grpIdList = new ArrayList<>();
        String query = null;
        int index = 1;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "SELECT CORPGROUPID FROM DG.CORPGROUPMEMBERLIST WHERE MEMBERMDN IN (MDNLIST) AND IS_SUPERVISOR = ?";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            pstmt = conn.prepareStatement(query);
            for(String mdn : mdnList) {
                pstmt.setString(index++, mdn);
            }
            pstmt.setInt(index, supervisor);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                grpIdList.add(rs.getInt(1));
            }
            knLogger.debug(methodName, "EXIT : Group list size returned is - ", grpIdList.size());
        } catch (SQLException | KnConnectionException e) {
            throw KnDbUtil.processException(e, "Failed while selecting members of the group " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return grpIdList;
    }

    public Set<Integer> getGrpDispMemList(List<Integer> dispatcherGrpList, String MDN, String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGrpDispMemList(List<Integer>, string, string, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : dispatcherGrpList- ", dispatcherGrpList, "  MDN - ", KnGDPRTemplate.mdn(MDN));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Set<Integer> grpIdList = new HashSet<>();
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORPGROUPID_MEMBERMDN);

            String corpGroupID = formIntegerCommaSeperatedIdList(dispatcherGrpList);
            query = replaceContactWithValue(query, GROUPIDS, corpGroupID);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, MDN);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                List<Integer> groupIdList = new ArrayList<>();
                groupIdList.add(rs.getInt(1));
                grpIdList.addAll(groupIdList);
            }
            knLogger.debug(methodName, "EXIT : Group list size returned is - ", grpIdList.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while selecting members of the group " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return grpIdList;
    }

    public Set<Integer> getGrpIdsHavingAtleastOneMember(List<Integer> dispatcherGrpList, String MDN, String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGrpIdsHavingAtleastOneMember(List<Integer>, string, string, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : dispatcherGrpLists- ", dispatcherGrpList, "  MDN - ", KnGDPRTemplate.mdn(MDN));
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Set<Integer> grpIdList = new HashSet<>();
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_NONDELETE_CORPGROUPID);

            String corpGroupID = formIntegerCommaSeperatedIdList(dispatcherGrpList);
            query = replaceContactWithValue(query, GROUPIDS, corpGroupID);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, MDN);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                int count = rs.getInt(2);
                int groupId = rs.getInt(1);
                knLogger.debug(methodName, "groupId and Count ", groupId, count);
                if (count != 0) {
                    grpIdList.add(groupId);
                }
            }
            knLogger.debug(methodName, "EXIT : GroupId size returned ", grpIdList.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while selecting groupID " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return grpIdList;
    }


    public void updateGrpMemListProperties(Collection<KnCorpGroupMemberDTO> modifiedMembers, int grpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateGrpMemListProperties(Collection<KnCorpGroupMemberDTO>, int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupMemMap - ", modifiedMembers, " , grpId - ", grpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_BROADCASTERS_MODIFYPERMS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for(KnCorpGroupMemberDTO grpMember : modifiedMembers){
                pstmt.setInt(1, grpMember.getBroadcaster());
                if(grpMember.getGroupModifyPerm() != null){
                    pstmt.setInt(2, grpMember.getGroupModifyPerm());
                } else {
                    pstmt.setInt(2, ENABLED);
                }
                pstmt.setInt(3, grpMember.getIsOSMAuthorize());
                pstmt.setString(4, grpMember.getMdn());
                pstmt.setInt(5, grpId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteing group member list",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, Integer> selectSubscrGrpCounts(List<String> finalGroupMembersinDB, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectSubscrGrpCounts(List, Collection KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : finalGroupMembersinDB - ", finalGroupMembersinDB, " , corpId - ", corpId);
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        boolean ownedTxn = false;
        ResultSet rs = null;
        Map<String, Integer> subscGrpCnt = new HashMap<String, Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            Collection<Collection<String>> collList = KnDbUtil.getCollectionList(finalGroupMembersinDB, 1000);
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
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
            if (collList != null) {
                for (Collection<String> mdnList : collList) {
                    int index = 1;
                    rs = null;
                    
                    query = queryMapper.getQuery(GET_SUBSC_GROUP_COUNT_MAP);      
                    query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList, query, "MDNLIST");
                    //query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks("GROUPIDS",groupIdList, query);
                    knLogger.debug(methodName, "Executing query - ", query);
                    pstmt = conn.prepareStatement(query);
                    for(String mdn : mdnList)
                        pstmt.setString(index++, mdn);
                    
                    /*for(Integer groupID : groupIdList)
                        pstmt.setInt(index++,groupID);*/
                    pstmt.setInt(index++,corpId);
                    
                    rs = pstmt.executeQuery();
                   
                    while (rs.next()) {
                        knLogger.debug(methodName, "Result set is ");
                        subscGrpCnt.put(rs.getString(1).trim(), rs.getInt(2));
                    }
                }
            }
            knLogger.debug(methodName, "Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteing group member groupn count",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return subscGrpCnt;
    }

    public Map<Integer, Integer> getSGCorpGroupMembersCount(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSGCorpGroupMembersCount(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupIds - ", groupIds);
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Integer> sgMemberCount = new HashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SG_GROUP_CORP_MEMBER_LIST);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Query executed successfully.");
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                sgMemberCount.put(rs.getInt(1), rs.getInt(2));
            }
            knLogger.debug(methodName, "EXIT. Response Map size  - ", sgMemberCount.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getSGCorpGroupMembersCount ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return sgMemberCount;
    }

    public Map<String, String> selectGroupMemberForGroupIds(Collection<Integer> groupList, String memberMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "selectGroupMemberForGroupIds(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupList - ", groupList);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, String> groupMemberList = new HashMap<String, String>();
        boolean ownedTxn = false;
        Connection conn = null;
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
            //Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_MEMBER_FOR_GROUPIDS);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberMdn);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                groupMemberList.put(String.valueOf(rs.getInt(1)), rs.getString(2).trim());
            }
            knLogger.debug(methodName, "EXIT: Returning groupMemberList size- ", groupMemberList.size());
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while selecting from corp group member list table- ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve from  groupmemberlist table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return groupMemberList;
    }

    public Map<String, Integer> getExternalSubscriberAbdgGroupCount(Collection<String> externalMdnList,
                                                                    int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExternalSubscriberAbdgGroupCount(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : externalMdnList - ", externalMdnList == null ? externalMdnList : KnGDPRTemplate.mdnList(externalMdnList), ", corpId - ", corpId);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Integer> subscGrpCount = new HashMap<String, Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            var mdnListArray = new ArrayList<>(externalMdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(GET_SUBSC_ABDG_GROUP_COUNT);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(subsList));
                query = KnDbUtil.replaceValInQry(query, CREATED_BY.ABDG.value());
                query = KnDbUtil.replaceValInQry(query, corpId);
                knLogger.debug(methodName, "Executing query", query);
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    subscGrpCount.put(rs.getString(1).trim(), rs.getInt(2));
                }
            }
            return subscGrpCount;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getExternalSubscriberAbdgGroupCount - ", e);
            throw KnDbUtil.processException(e, "Failed while getExternalSubscriberAbdgGroupCount  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : The subscriber group count lsit size - ", subscGrpCount.size());
        }
    }

    public void updateGrpMemListLocWatchers(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "updateGrpMemListLocWatchers()";
        knLogger.debug(methodName, "ENTRY : groupMemMap - ", groupIds);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_MEMS_LOCWATCHER);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for(int groupId : groupIds){
                pstmt.setInt(1, 0);
                pstmt.setInt(2, groupId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while updating group member list",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getGrpSubsc(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGrpSubsc( Collection<Interger>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ", " , groupIds - ", groupIds);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Collection<KnCorpGroupMemberDTO>> corpGrpSupervisorMap = new ConcurrentHashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_MEMBERS_FOR_GRPLISTS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                int grpId = rs.getInt(1);
                Collection<KnCorpGroupMemberDTO> mdnList = corpGrpSupervisorMap.get(grpId);
                if (mdnList == null) {
                    mdnList = new ArrayList<KnCorpGroupMemberDTO>();
                }
                KnCorpGroupMemberDTO contact = new KnCorpGroupMemberDTO();
                contact.setMdn(rs.getString(2).trim());
                contact.setSupervisory(rs.getInt(3));
                contact.setBroadcaster(rs.getInt(4));
                int memberType = rs.getInt(5);
                if (memberType == SG_MDN_MEMBER_TYPE) {
                    contact.setClientType(SUBSCR_CLIENT_TYPE.GROUPMDN.value());
                } else if (memberType == SG_MDN_PATCH_MEMBER_TYPE) {
                    contact.setClientType(SUBSCR_CLIENT_TYPE.SGMDNPATCH.value());
                }
                contact.setCallInitiatePermission(rs.getInt(6));
                contact.setCallReceivePermission(rs.getInt(7));
                contact.setInCallPermission(rs.getInt(8));
                contact.setLocWatcher(rs.getInt(9));
                mdnList.add(contact);
                corpGrpSupervisorMap.put(grpId, mdnList);
            }

        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getGroupInterOpSubsc  ", pttServerId,
                    KnDAOSourceTypes.XDM_CORP_LIST_MEMBER, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "EXIT : The group subsc Map size is  - ", corpGrpSupervisorMap.size());
        return corpGrpSupervisorMap;
    }

    /**
     * This method returns a Map of group member MDN and its is_supervisor value.
     *
     * @param groupId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnCorpGroupMemberDTO> getGroupMembersListForWcsrOrCatUi(int groupId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupMembersListForWcsrOrCatUi(int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupId - ", groupId);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, KnCorpGroupMemberDTO> grpMemberMap = new HashMap<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GROUP_MEMBER_LIST_FOR_WCSR_OR_CAT);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Query executed successfully.");
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                KnCorpGroupMemberDTO groupMemberDTO = new KnCorpGroupMemberDTO();
                String mdn = rs.getString(1).trim();
                groupMemberDTO.setMdn(mdn);
                groupMemberDTO.setSupervisory(rs.getInt(2));
                groupMemberDTO.setBroadcaster(rs.getInt(3));
                groupMemberDTO.setCallInitiatePermission(rs.getInt(4));
                groupMemberDTO.setCallReceivePermission(rs.getInt(5));
                groupMemberDTO.setInCallPermission(rs.getInt(6));
                groupMemberDTO.setVideoCallInitiatePermission(rs.getInt(7));
                groupMemberDTO.setVideoCallReceivePermission(rs.getInt(8));
                groupMemberDTO.setVideoInCallPermission(rs.getInt(9));
                groupMemberDTO.setLocWatcher(rs.getInt(10));
                groupMemberDTO.setGroupModifyPerm(rs.getInt(11));
                groupMemberDTO.setIsOSMAuthorize(rs.getInt(12));
                groupMemberDTO.setServiceAuthStatus(rs.getInt(13));
                grpMemberMap.put(mdn, groupMemberDTO);
            }
            knLogger.debug(methodName, "EXIT. Response Map size  - ", grpMemberMap.size(),KnGDPRTemplate.mapKeyMdn(grpMemberMap));
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getGroupMembersList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return grpMemberMap;
    }
    
    /**
     * This method returns a Map of group member MDN and its is_supervisor value.
     *
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<String> getRealMdns(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getRealMdns(List<String>,boolean, KnPersisterTxn)";

        var mdnListArray = new ArrayList<>(mdns);
        var returnRealMdns = new ArrayList<String>();
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var subRealMdns = getSubRealMdns(subsList, readOnly, persisterTxn);
            if (subRealMdns != null && !subRealMdns.isEmpty()) {
                returnRealMdns.addAll(subRealMdns);
            }
        }
        knLogger.exit(methodName, "EXIT : returnList size " , returnRealMdns == null ? 0 : returnRealMdns.size());
        return returnRealMdns;
    }
    private List<String> getSubRealMdns(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubRealMdns(List<String>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdns - ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        PreparedStatement pstmt = null;
        Connection conn = null;
        boolean ownedTxn = false;
        String query = null;
        ResultSet rs = null;
        int index = 1;
        List<String> realMdns = new ArrayList<String>();
        try {

            if (persisterTxn != null) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_REAL_MDNS);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdns, query, MDNS);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            
            for(String mdn : mdns)
                pstmt.setString(index++, mdn);
            
            rs = pstmt.executeQuery();
            
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                realMdns.add(mdn);
            }
            knLogger.debug(methodName, "EXIT. Response list size  - ", KnGDPRTemplate.mdnList(realMdns));
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getGroupMembersList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return realMdns;
    }
    

    public Map<String,Integer> getSubsScrGroupCount(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubsScrGroupCount(List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY Point : mdns - ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        Map<String, Integer> groupCountsMap = new HashMap<>();
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_COUNT_BY_SUBSCRIBER);
            Collection<Collection<String>> collList = KnDbUtil.getCollectionList(mdns, 1000);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            for (Collection<String> splitGroupProfileIdList : collList) {
                String finalQuery = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(splitGroupProfileIdList));
                knLogger.debug(methodName, "Executing query - ", finalQuery);
                stmt = conn.createStatement();
                rs = stmt.executeQuery(finalQuery);
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    groupCountsMap.put(rs.getString(1).trim(), rs.getInt(2));
                }
            }

            return groupCountsMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed while fetching group count ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.info(methodName, "EXIT : group count ", groupCountsMap.size());
        }
    }

    public Map<String,Integer> getSubsScrGroupCount(List<String> mdns, KnPersisterTxn persisterTxn, int corpId) throws KnDAOException {
        final String methodName = "getSubsScrGroupCount(List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY Point : mdns - ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        Map<String, Integer> groupCountsMap = new HashMap<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_COUNT_BY_SUBSCRIBER_OWN_CORP);
            Collection<Collection<String>> collList = KnDbUtil.getCollectionList(mdns, 1000);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            for (Collection<String> splitGroupProfileIdList : collList) {
                String finalQuery = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(splitGroupProfileIdList));
                knLogger.debug(methodName, "Executing query - ", finalQuery);
                pstmt = conn.prepareStatement(finalQuery);
                pstmt.setInt(1, corpId);
                rs = pstmt.executeQuery();

                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    groupCountsMap.put(rs.getString(1).trim(), rs.getInt(2));
                }
            }

            return groupCountsMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed while fetching group count ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.info(methodName, "EXIT : group count ", groupCountsMap.size());
        }
    }

    public Map<String,Integer> getSubsScrGroupCountExceptABDG(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubsScrGroupCountExceptABDG(List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY Point : mdns - ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        Map<String, Integer> groupCountsMap = new HashMap<>();
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBS_GROUP_LIST_COUNT_EXCEPT_ABDG);
            Collection<Collection<String>> collList = KnDbUtil.getCollectionList(mdns, 1000);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            for (Collection<String> splitGroupProfileIdList : collList) {
                String finalQuery = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(splitGroupProfileIdList));
                knLogger.debug(methodName, "Executing query - ", finalQuery);
                stmt = conn.createStatement();
                rs = stmt.executeQuery(finalQuery);
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    groupCountsMap.put(rs.getString(1).trim(), rs.getInt(2));
                }
            }

            return groupCountsMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed while fetching group count ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.info(methodName, "EXIT : group count ", groupCountsMap.size());
        }
    }

    public Map<String,Integer> getSubsScrGroupCountExceptABDG(List<String> mdns, KnPersisterTxn persisterTxn, int corpId) throws KnDAOException {
        final String methodName = "getSubsScrGroupCountExceptABDG(List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY Point : mdns - ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        Map<String, Integer> groupCountsMap = new HashMap<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBS_GROUP_LIST_COUNT_EXCEPT_ABDG_OWN_CORP);
            Collection<Collection<String>> collList = KnDbUtil.getCollectionList(mdns, 1000);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            for (Collection<String> splitGroupProfileIdList : collList) {
                String finalQuery = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(splitGroupProfileIdList));
                knLogger.debug(methodName, "Executing query - ", finalQuery);
                pstmt = conn.prepareStatement(finalQuery);
                pstmt.setInt(1, corpId);
                pstmt.setInt(2, corpId);
                pstmt.setInt(3, corpId);
                pstmt.setInt(4, corpId);
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    groupCountsMap.put(rs.getString(1).trim(), rs.getInt(2));
                }
            }

            return groupCountsMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed while fetching group count ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.info(methodName, "EXIT : group count ", groupCountsMap.size());
        }
    }

    /**
     * This method returns a Map of group member MDN and its is_supervisor value.
     *
     * @param groupIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Map<String, KnCorpGroupMemberDTO>> getBulkGroupMembersList(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getBulkGroupMembersList(List<Integer> , KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupIds - ", groupIds);
        PreparedStatement stmt = null;
        String query = null;
        ResultSet rs = null;
        var returnGrpMemberMap = new HashMap<Integer, Map<String, KnCorpGroupMemberDTO>>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(BULK_GROUP_CORP_MEMBER_LIST);
            query = replaceContactWithValue(query, GROUPIDS, formCommaSeperatedIntegerQuesMarks(groupIds));
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.prepareStatement(query);
            int index = 1;
            for (Integer groupID : groupIds)
                stmt.setInt(index++, groupID);

            knLogger.debug(methodName, "Query executed successfully.");
            rs = stmt.executeQuery();
            while (rs.next()) {
                KnCorpGroupMemberDTO groupMemberDTO = new KnCorpGroupMemberDTO();
                int groupId = rs.getInt("CORPGROUPID");
                groupMemberDTO.setGroupId(groupId);
                String mdn = rs.getString("MEMBERMDN").trim();
                groupMemberDTO.setMdn(mdn);
                groupMemberDTO.setSupervisory(rs.getInt("IS_SUPERVISOR"));
                groupMemberDTO.setLocWatcher(rs.getInt("IS_LOCWATCHER"));
                groupMemberDTO.setIsOSMAuthorize(rs.getInt("IS_OSMAUTHORIZED"));
                groupMemberDTO.setCallInitiatePermission(rs.getInt("CALL_INITIATE_PERMISSION"));
                groupMemberDTO.setCallReceivePermission(rs.getInt("CALL_RECEIVE_PERMISSION"));
                groupMemberDTO.setInCallPermission(rs.getInt("INCALL_PERMISSION"));
                groupMemberDTO.setBroadcaster(rs.getInt("IS_BROADCASTER"));
                groupMemberDTO.setCorpId(rs.getInt("MEMBERMDN_CORPID"));
                groupMemberDTO.setGroupModifyPerm(rs.getInt("GRP_MODIFY_PERM"));
                groupMemberDTO.setVideoCallInitiatePermission(rs.getInt("V_CALL_INITIATE_PERMISSION"));
                groupMemberDTO.setVideoCallReceivePermission(rs.getInt("V_CALL_RECEIVE_PERMISSION"));
                groupMemberDTO.setVideoInCallPermission(rs.getInt("V_INCALL_PERMISSION"));

                if (!returnGrpMemberMap.containsKey(groupId)) {
                    var grpMemberMap = new HashMap<String, KnCorpGroupMemberDTO>();
                    returnGrpMemberMap.put(groupId, grpMemberMap);
                }
                returnGrpMemberMap.get(groupId).put(mdn, groupMemberDTO);
            }
            knLogger.debug(methodName, "EXIT. Response Map size  - ", returnGrpMemberMap.size(), KnGDPRTemplate.mapKeyMdn(returnGrpMemberMap.values().stream().flatMap(map -> map.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1))));
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getGroupMembersList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return returnGrpMemberMap;
    }

    public Map<Integer, KnCorpGroupMemberDTO> getBulkGroupMembersListMap(List<Integer> groupIds, String member, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getBulkGroupMembersList(List<Integer> , KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupIds - ", groupIds);
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        boolean ownedTxn = false;
        var returnGrpMemberMap = new HashMap<Integer, KnCorpGroupMemberDTO>();
        try {
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
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(BULK_GROUP_CORP_MEMBER_LIST_MAP);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            query = KnDbUtil.replaceValInQry(query, member);
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Query executed successfully.");
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                KnCorpGroupMemberDTO groupMemberDTO = new KnCorpGroupMemberDTO();
                int groupId = rs.getInt("CORPGROUPID");
                groupMemberDTO.setGroupId(groupId);
                String mdn = rs.getString("MEMBERMDN").trim();
                groupMemberDTO.setMdn(mdn);
                groupMemberDTO.setSupervisory(rs.getInt("IS_SUPERVISOR"));
                groupMemberDTO.setLocWatcher(rs.getInt("IS_LOCWATCHER"));
                groupMemberDTO.setIsOSMAuthorize(rs.getInt("IS_OSMAUTHORIZED"));
                groupMemberDTO.setCallInitiatePermission(rs.getInt("CALL_INITIATE_PERMISSION"));
                groupMemberDTO.setCallReceivePermission(rs.getInt("CALL_RECEIVE_PERMISSION"));
                groupMemberDTO.setInCallPermission(rs.getInt("INCALL_PERMISSION"));
                groupMemberDTO.setBroadcaster(rs.getInt("IS_BROADCASTER"));
                groupMemberDTO.setCorpId(rs.getInt("MEMBERMDN_CORPID"));
                groupMemberDTO.setGroupModifyPerm(rs.getInt("GRP_MODIFY_PERM"));
                groupMemberDTO.setVideoCallInitiatePermission(rs.getInt("V_CALL_INITIATE_PERMISSION"));
                groupMemberDTO.setVideoCallReceivePermission(rs.getInt("V_CALL_RECEIVE_PERMISSION"));
                groupMemberDTO.setVideoInCallPermission(rs.getInt("V_INCALL_PERMISSION"));

                returnGrpMemberMap.put(groupId, groupMemberDTO);
            }
            knLogger.debug(methodName, "EXIT. Response Map size  - ", returnGrpMemberMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getGroupMembersList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            if(ownedTxn){
                KnDbUtil.closeConnection(conn);
            }
        }
        return returnGrpMemberMap;
    }
    public void updateBulkGroupMemberListSupervisorList(Map<Integer, Collection<KnCorpGroupMemberDTO>> supervisorMemberListMap,
                                                     KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateBulkGroupMemberListSupervisorList(Map<Integer, List<KnCorpGroupMemberDTO>>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : supervisorMemberListMap - ", supervisorMemberListMap);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_MEMBERS_SUPERVISOR_ATTRIBUTE);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            for (var entry : supervisorMemberListMap.entrySet()) {
                var groupId = entry.getKey();
                var supervisorMemberList = entry.getValue();
                for (KnCorpGroupMemberDTO memberDTO : supervisorMemberList) {
                    pstmt.setInt(1, memberDTO.getSupervisory());
                    //Modified for 8.1.1 changes
                    pstmt.setInt(2, memberDTO.getCallInitiatePermission());
                    pstmt.setInt(3, memberDTO.getCallReceivePermission());
                    pstmt.setInt(4, memberDTO.getInCallPermission());
                    pstmt.setInt(5, memberDTO.getLocWatcher());
                    if (memberDTO.getGroupModifyPerm() != null) {
                        pstmt.setInt(6, memberDTO.getGroupModifyPerm());
                    } else {
                        pstmt.setInt(6, ENABLED);
                    }
                    pstmt.setInt(7, memberDTO.getIsOSMAuthorize());
                    pstmt.setInt(8, memberDTO.getVideoCallInitiatePermission());
                    pstmt.setInt(9, memberDTO.getVideoCallReceivePermission());
                    pstmt.setInt(10, memberDTO.getVideoInCallPermission());
                    pstmt.setString(11, memberDTO.getMdn());
                    pstmt.setInt(12, groupId);

                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateGroupMemberListSupervisorList - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while updateGroupMemberListSupervisorList - ", e);
            throw KnDbUtil.processException(e, "Failed while updateGroupMemberListSupervisorList - " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Integer getTotalMemberCountFromGroupList(List<Integer> groupIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getTotalMemberCountFromGroupList(List<Integer> ,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : groupIds - ", groupIds);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Collection<String> totalMembers = new ArrayList<>();
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_TOTAL_SUBSCRIBER_COUNT_BASED_ON_GROUPIDS);
            knLogger.debug(methodName, "Executing query - ", query);
            query = KnCorpUtil.replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                totalMembers.add(rs.getString(1));
            }
            knLogger.debug(methodName, "EXIT. Response count  - ", totalMembers);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getTotalMemberCountFromGroupList-  ", e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "Unexpected Exception occured while getTotalMemberCountFromGroupList- ", e);
            throw KnDbUtil.processException(e, "Failed to getTotalMemberCountFromGroupList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return totalMembers.size();
    }

    public Map<String, Integer> getPaginatedSubscriberData(Integer groupId , Integer firstIndex, Integer lastIndex, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getPaginatedSubscriberData(Integer, Integer, Integer, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : firstIndex ", firstIndex, " lastIndex ", lastIndex);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        int totalMemberCount = 0;
        Map<String, Integer> rowNumMemDetailsMap = new TreeMap<>();
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_PAGINATED_SUBSCRIBER_DETAILS);

            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, firstIndex);
            pstmt.setInt(3, lastIndex);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully..");
            while (rs.next()) {
                rowNumMemDetailsMap.put(rs.getString(1).trim(), rs.getInt(2));
            }
            knLogger.debug(methodName, "EXIT. Response count  - ", rowNumMemDetailsMap);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "KnDAOException occured while getPaginatedSubscriberData-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getPaginatedSubscriberData- ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to getTotalMemberCountFromGroupList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return rowNumMemDetailsMap;
    }

    public Integer getGroupIdBasedonRowNumber(List<String> mdnList,int rowNumber, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupIdBasedonRowNumber(List<String>, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdnList ", KnGDPRTemplate.mdnList(mdnList), " rowNumber ", rowNumber);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        int totalMemberCount = 0;
        boolean ownedTxn = false;
        Integer corpGroupId = null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_ID_BASED_ON_ROWNUM);
            query = KnCorpUtil.replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, rowNumber);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                corpGroupId = rs.getInt(1);
            }
            knLogger.debug(methodName, "EXIT. corpGroupId  - ", corpGroupId);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "KnDAOException occured while getGroupIdBasedonRowNumber-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getGroupIdBasedonRowNumber- ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to getTotalMemberCountFromGroupList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return corpGroupId;
    }

    public Map<String,KnCorpGroupMemberDTO> getGroupSubsBasicInfo(List<Integer> groupIdList, List<String> mdnList,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupSubsBasicInfo(List<String>, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdnList ", KnGDPRTemplate.mdnList(mdnList), " groupIdList ", groupIdList);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        Integer corpGroupId = null;
        Map<String,KnCorpGroupMemberDTO> result = new HashMap<>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GRP_MEMBERS_BASIC_INFO);
            query = KnCorpUtil.replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            query = KnCorpUtil.replaceContactWithValue(query, GROUPIDLIST, formIntegerCommaSeperatedIdList(groupIdList));
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                KnCorpGroupMemberDTO groupMemberDTO = new KnCorpGroupMemberDTO();
                groupMemberDTO.setGroupId(rs.getInt(1));
                groupMemberDTO.setSupervisory(rs.getInt(2));
                groupMemberDTO.setLocWatcher(rs.getInt(3));
                groupMemberDTO.setCorpId(rs.getInt(4));
                result.put(rs.getString(5).trim(), groupMemberDTO);
            }
            knLogger.debug(methodName, "EXIT. corpGroupId  - ", corpGroupId);
            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "KnDAOException occured while getGroupIdBasedonRowNumber-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getGroupIdBasedonRowNumber- ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to getTotalMemberCountFromGroupList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return result;
    }


    public Map<String,Integer> getSubscriberBroadcastGroupCount(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscriberBroadcastGroupCount(List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY Point : mdns - ", mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns));
        Map<String, Integer> groupCountsMap = new HashMap<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;

        if (mdns == null || mdns.isEmpty()) {
            return groupCountsMap;
        }

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBER_BROADCAST_GROUP_COUNT);
            Collection<Collection<String>> collList = KnDbUtil.getCollectionList(mdns, 1000);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            for (Collection<String> splitGroupProfileIdList : collList) {
                String finalQuery = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(splitGroupProfileIdList));
                knLogger.debug(methodName, "Executing query - ", finalQuery);
                stmt = conn.createStatement();
                rs = stmt.executeQuery(finalQuery);
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    groupCountsMap.put(rs.getString(1).trim(), rs.getInt(2));
                }
            }

            return groupCountsMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed while fetching group count ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.info(methodName, "EXIT : group count ", groupCountsMap.size());
        }
    }

    public Set<String> getGroupMemsList(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupMemsList(Collection<Integer>, KnPersisterTxn)";
        Set<String> memberlist = new HashSet<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_DISTINCT_MEMBERLIST_BY_GROUPIDS);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Exeuting query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                memberlist.add(rs.getString(1).trim());
            }
            knLogger.debug(methodName, "EXIT. groupSize - ", memberlist.size());
            return memberlist;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed while fetching member list ." + e, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.info(methodName, "EXIT : group mem list count ", memberlist.size());
        }
    }

    public List<String> getAllGroupMdns(Collection<Integer> groupIdList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getAllGroupMdns(Collection<Integer>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIdList - ", groupIdList);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        List<String> groupMdnList = new ArrayList<>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = "SELECT MEMBERMDN FROM DG.CORPGROUPMEMBERLIST WHERE CORPGROUPID IN (GROUPIDS) AND MEMBERMDN_CORPID = ?";
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groupMdnList.add(rs.getString(1).trim());
            }
            knLogger.debug(methodName, "EXIT: Query executed successfully, groupMdnList size - ", groupMdnList.size());
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while fetching group MDNs - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while fetching group MDNs - ", e);
            throw KnDbUtil.processException(e, "Failed to getAllGroupMdns from groupmemberlist table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return groupMdnList;
    }

}