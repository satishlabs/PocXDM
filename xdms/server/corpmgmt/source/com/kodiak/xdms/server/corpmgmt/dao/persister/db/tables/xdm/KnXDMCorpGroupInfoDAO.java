/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpGroupInfoDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        05-02-2011      7.0
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


import com.kodiak.common.commdto.request.KnXDMGroupPropertyInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGrpBasicInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;

import static com.kodiak.common.resources.KnConstants.AREA_BASED_DYNAMIC_GROUP;
import static com.kodiak.common.resources.KnConstants.DISABLED;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.CREATED_BY.ABDG;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.CREATED_BY.DYNAMIC_CGMT_INTF;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnXDMCorpGroupInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpGroupInfoDAO.class);

    public String pttServerId = null;

    KnXDMCorpGroupInfoDAO(String pttServerId) {
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


    public Collection<KnCorpGroupInfoDTO> getSublistGroupDistributionList(int sublistId, int maxGroupMemberLimit, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSublistGroupDistributionList(int, int,boolean, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : sublistId - ", sublistId, ", maxGroupMemberLimit - ", maxGroupMemberLimit);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection<KnCorpGroupInfoDTO> groupList = new ArrayList<KnCorpGroupInfoDTO>();
        Map<Integer,Integer> grorpMemberCount = new  HashMap<Integer,Integer>();
        KnCorpGroupInfoDTO group;
        String query = null;
        int memCount;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = queryMapper.getQuery(GRP_MEMBER_COUNT);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sublistId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                grorpMemberCount.put(rs.getInt(1), rs.getInt(2));
            }
            query = queryMapper.getQuery(GET_SUBLIST_GRP_DIST_LIST);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sublistId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                group = new KnCorpGroupInfoDTO();
                group.setGroupId(rs.getInt(1));
                //multilingual revert changes
                if(null != rs.getString(2))
                {
                    try {
                        group.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"),"UTF-8"));
                    } catch (Exception e) {
                        knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(2), e);
                    }
                }else
                {
                    group.setGroupDisplayName(rs.getString(2));
                }
                group.setGroupType(mappGroupTypeToApp(rs.getInt(3)));
                group.setAvatar((Integer) rs.getObject(4));
                memCount = grorpMemberCount.get(rs.getInt(1));
                group.setGroupMemberCount(memCount);
                if (memCount > maxGroupMemberLimit) {
                    group.setMaxGroupMemLimitFlag(GREATER_THAN_LIMIT);
                } else if (memCount == maxGroupMemberLimit) {
                    group.setMaxGroupMemLimitFlag(EQUAL_TO_LIMIT);
                } else if (memCount < MIN_MEMBER_LIMIT) {
                    group.setMaxGroupMemLimitFlag(LESS_THAN_MIN_LIMIT);
                } else if (memCount < maxGroupMemberLimit) {
                    group.setMaxGroupMemLimitFlag(LESS_THAN_LIMIT);
                }
                if(rs.getInt(5) == 1){
                    group.setLargeGroup(true);
                }
                groupList.add(group);
            }
            knLogger.debug( methodName, "EXIT : group List size returned - ", groupList.size());
            return groupList;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving sublist distribution to group list- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving sublist distribution to group list- - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while retrieving sublist distribution to group list " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        }
        finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }


    public void deleteGroup(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteGroup(int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_GROUP_INFO);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Exeuting query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while deleting group - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while deleting group - ", e);
            throw KnDbUtil.processException(e, "Failed while deleting group  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Set<Integer> getCorpGroupIdByOsmListId(int OsmListId, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getCorpGroupIdByOsmListId(int, List, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : OsmListId - ", OsmListId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Set<Integer> groupIds=new HashSet<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORPGROUPID_BY_OSMLISTID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Exeuting query - ", query);
            pstmt.setInt(1, OsmListId);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                groupIds.add(rs.getInt(1));
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving the group count by type- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving the group count by type- ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group count by type  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            knLogger.debug( methodName, "EXIT groupIds:",groupIds);
        }
        return groupIds;
    }

    public int updateIsOSMAuthorize(Set<Integer> groupIds, String mdn,String isOSMAuthorize, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateIsOSMAuthorize()";
        knLogger.debug(methodName, "ENTRY : groupIds - ", groupIds);
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int count=0;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_MDN_OSM_AUTHORIZE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Exeuting query - ",query);
            pstmt.setInt(1, Integer.parseInt(isOSMAuthorize));
            pstmt.setString(2, mdn);
            count=pstmt.executeUpdate();
            knLogger.debug( methodName, "Query executed successfully");
            if (ownedTxn) {
                persisterTxn.save();
            }

        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error( methodName, "KnDAOException occured while updateIsOSMAuthorize - ", e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error( methodName, "Unexpected Exception occured while updateIsOSMAuthorize - ", e);
            throw KnDbUtil.processException(e, "Failed while updateIsOSMAuthorize " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT : Returning updateIsOSMAuthorize - ", count);
        }

        return count;
    }

    public Map<Integer, Integer> updateGroupListEtag(Collection<Integer> groupIdLst, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateGroupListEtag(Collection<Integer>, KnPersisterTxn )";
        knLogger.entry(methodName, "ENTRY : groupIdLst - ", groupIdLst != null ? groupIdLst.size() : 0);
        Connection conn;
        boolean ownedTxn = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, Integer> groupEtagMap = new HashMap<Integer, Integer>();
        if(null !=groupIdLst && !groupIdLst.isEmpty()) {
            List<Integer> grpIdList = new ArrayList<Integer>(groupIdLst);
            Collections.sort(grpIdList);
            knLogger.debug(methodName, "Group Id list after sorted", grpIdList);
            try {
                if (persisterTxn == null) {
                    persisterTxn = KnPersisterTxn.getPersisterTxn();
                    knLogger.debug(methodName, "Opening the Transaction");
                    persisterTxn.open();
                    ownedTxn = true;
                }
                KnQueryMapper queryMapper = KnQueryMapper.getInstance();
                query = queryMapper.getQuery(SELECT_GROUP_LIST_ETAG);
                //conn = persisterTxn.getDBConnection(pttServerId, false);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdLst));
                pstmt = conn.prepareStatement(query);
                knLogger.debug(methodName, "Exeuting query - ", "'", query, "'");
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                while (rs.next()) {
                    groupEtagMap.put(rs.getInt(1), rs.getInt(2));
                }
                knLogger.debug(methodName, "GRoup etag obtained - ", groupEtagMap);
                String updateEtagQuery = queryMapper.getQuery(UPDATE_GROUP_ETAG);
                pstmt = conn.prepareStatement(updateEtagQuery);
                if (null != groupEtagMap && !groupEtagMap.isEmpty()) {
                    for (Integer groupId : grpIdList) {
                        int etag = groupEtagMap.get(groupId);
                        pstmt.setInt(1, ++etag);
                        pstmt.setInt(3, groupId);
                        pstmt.setLong(2, System.currentTimeMillis());
                        pstmt.addBatch();
                        groupEtagMap.put(groupId, etag);
                    }
                    pstmt.executeBatch();
                }
                if (ownedTxn) {
                    persisterTxn.save();
                }
            } catch (KnDAOException e) {
                if (ownedTxn) {
                    persisterTxn.rollback();
                }
                knLogger.error(methodName, "KnDAOException occured while deleting group - ", e);
                throw e;
            } catch (Exception e) {
                if (ownedTxn) {
                    persisterTxn.rollback();
                }
                knLogger.error(methodName, "Unexpected Exception occured while deleting group - ", e);
                throw KnDbUtil.processException(e, "Failed while deleting group  " + e,
                        pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closePreparedStatement(pstmt);
                knLogger.debug(methodName, "EXIT : Returning groupEtag Map size - ", groupEtagMap.size());
            }
        }
        return groupEtagMap;
    }

    public Map<Integer, Integer> updateGroupListEtag(Map<Integer, Integer> groupIdLst, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateGroupListEtag(Map<Integer, Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIdLst - ", groupIdLst);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, Integer> groupEtagMap = new HashMap<Integer, Integer>();
        List<Integer> grpIdList = new ArrayList<Integer>(groupIdLst.keySet());
        Collections.sort(grpIdList);
        knLogger.debug( methodName, "Group Id list after sorted", grpIdList);
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_LIST_ETAG);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(grpIdList));
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Exeuting query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                groupEtagMap.put(rs.getInt(1), rs.getInt(2));
            }
            knLogger.debug( methodName, "GRoup etag obtained - ", groupEtagMap);
            String updateEtagQuery = queryMapper.getQuery(UPDATE_GROUP_ETAG_LARGE_FLAG);
            pstmt = conn.prepareStatement(updateEtagQuery);
            for (Integer groupId : grpIdList) {
                int etag = groupEtagMap.get(groupId);
                pstmt.setInt(1, ++etag);
                pstmt.setLong(2, System.currentTimeMillis());
                pstmt.setInt(3, groupIdLst.get(groupId));
                pstmt.setInt(4, groupId);
                pstmt.addBatch();
                if(groupIdLst.get(groupId) == DISABLED) groupEtagMap.put(groupId, etag);
            }
            pstmt.executeBatch();
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while update group - ", e);
            throw KnDbUtil.processException(e, "Failed while update group  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT : Returning groupEtag Map size - ", groupEtagMap.size());
        }
        return groupEtagMap;
    }

    public KnCorpGroupDTO getGroupBasicInfo(int groupId,boolean readonly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupBasicInfo(int,KnPersisterTxn )";
        knLogger.info(methodName, "ENTRY : groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpGroupDTO groupInfoDto = new KnCorpGroupDTO();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_INFO_BY_GROUP_ID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            if (rs.next()) {
                groupInfoDto.setGroupId(rs.getInt(1));
                groupInfoDto.setCorpId(rs.getInt(2));
                //multilingual revert changes
                if(null != rs.getString(3))
                {
                    try {
                        groupInfoDto.setGroupDisplayName(new String(rs.getString(3).trim().getBytes("8859_1"),"UTF-8"));
                    } catch (Exception e) {
                        knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(3), e);
                    }
                }
                groupInfoDto.setETag(rs.getInt(4));
                groupInfoDto.setGroupType(mappGroupTypeToApp(rs.getInt(5)));
                groupInfoDto.setOverrideDnd(rs.getInt(6));
                groupInfoDto.setAvatar((Integer) rs.getObject(7));
                groupInfoDto.setEmergAutoFloorTimer((Integer) rs.getObject(8));
                groupInfoDto.setEmergHangTimeAddOn((Integer) rs.getObject(9));
                groupInfoDto.setEmergOverrideDND((Integer) rs.getObject(10));
                groupInfoDto.setHangTimeOut((Integer) rs.getObject(11));
                groupInfoDto.setGroupCreatedBy(rs.getInt(12));
                if(rs.getString(13) != null) groupInfoDto.setTpGroupOwner(rs.getString(13).trim());
                if(rs.getInt(14) == 1){
                    groupInfoDto.setLargeGroup(Boolean.TRUE);
                }
                if(rs.getInt(14) == 2){
                    groupInfoDto.setMcxGrpInd(1);
                }
                groupInfoDto.setOSMListId(String.valueOf(rs.getInt(15)));
                groupInfoDto.setLmrInteropCapable(rs.getInt(16));
                groupInfoDto.setPocHome(rs.getString(17));
                groupInfoDto.setGroupProfileId(rs.getString(18));
                groupInfoDto.setGrpShared((Integer) rs.getObject(19));
                groupInfoDto.setIsPreConfiguredGroup((Integer)rs.getObject (20));
                groupInfoDto.setUgwInterop(rs.getInt(21));
                groupInfoDto.setRecordingFs(String.valueOf(rs.getInt("RECORDING_FS")));
                if (null != rs.getObject("AUTHORIZED_LARGE_TG")) {
                    groupInfoDto.setAuthorizedLargeTG(rs.getInt("AUTHORIZED_LARGE_TG"));
                } else {
                    groupInfoDto.setAuthorizedLargeTG(KnConstants.DEFAULT_AUTHORIZED_LARGE_TG_VALUE);
                }
                // Handle VIDEO_PERMISSION with null safety
                if (null != rs.getObject("VIDEO_PERMISSION")) {
                    int videoPermValue = rs.getInt("VIDEO_PERMISSION");
                    groupInfoDto.setVideoPermission(videoPermValue);
                    knLogger.debug(methodName, "Read VIDEO_PERMISSION from DB - groupId:", groupId, ", videoPermission:", videoPermValue);
                } else {
                    groupInfoDto.setVideoPermission(KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                    knLogger.debug(methodName, "VIDEO_PERMISSION is NULL in DB, setting default - groupId:", groupId, ", default:", KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                }
            }
            knLogger.info( methodName, "EXIT : group Info returned - ", groupInfoDto);
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while retrieving group basic Information -  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        }
        finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return groupInfoDto;
    }
    public Integer getMemberCountFromMemberList(int groupId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMemberCountFromMemberList(groupId, persisterTxn)";
        knLogger.debug(methodName, "Entry : groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Integer memberCount = 0;
        String query = "SELECT MEMBERCOUNT FROM DG.CORPGROUPMEMBERCOUNT WHERE  CORPGROUPID=?;";
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            //  Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (rs.getObject(1) != null) {
                    memberCount = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while retrieving memberCount -  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_COUNT, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return memberCount;
    }

    public void modifyGroupName(String groupName, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupName(String, int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupName - ", groupName, " , groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_NAME);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            //multilingual revert changes
            if(null != groupName)
            {
                try {
                    pstmt.setString(1, new String(groupName.getBytes("UTF-8"),"8859_1"));
                } catch (Exception e) {
                    knLogger.error(methodName, "UTF-8 encoding exception - ", groupName, e);
                }
            }else
            {
                pstmt.setString(1, groupName);
            }
            pstmt.setInt(2, groupId);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating group name- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while  updating group name - " + e);
            throw KnDbUtil.processException(e, "Failed while  updating group name " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    public void modifyGroupOSMListId(int corpId,int groupId,String OSMListId, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "modifyGroupOSMListId()";
        knLogger.debug(methodName, "ENTRY :",corpId,groupId,OSMListId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_CORP_GROUP_OSM);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            //seting null if osmlistid is empty or none is selected.
            if(OSMListId!=null&&!OSMListId.isEmpty()){
                pstmt.setInt(1, Integer.parseInt(OSMListId));
            }else{
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setInt(2, corpId);
            pstmt.setInt(3, groupId);
            knLogger.debug( methodName, "Executing query - ",query);
            pstmt.executeUpdate();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while  updating group osmList - " + e);
            throw KnDbUtil.processException(e, "Failed while  updating group osmList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    public void modifyGroupVideoPermission(Integer videoPermission, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupVideoPermission()";
        knLogger.info(methodName, "ENTRY : videoPermission - ", videoPermission, " , groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_VIDEO_PERMISSION);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            if (videoPermission != null) {
                pstmt.setInt(1, videoPermission);
            } else {
                pstmt.setNull(1, java.sql.Types.TINYINT);
            }
            pstmt.setInt(2, groupId);
            pstmt.executeUpdate();
            knLogger.info(methodName, "Successfully updated VIDEO_PERMISSION - groupId:", groupId, ", videoPermission:", videoPermission);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while updating videoPermission - " + e);
            throw KnDbUtil.processException(e, "Failed while updating videoPermission " + e, pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    public void modifyGroupAvatar(Integer avatar, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupAvatar(int, int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : avatar - ", avatar, " , groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_AVATAR);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);

            if(avatar!=null)
                pstmt.setInt(1, avatar);
            else
                pstmt.setNull(1, Types.INTEGER);

            pstmt.setInt(2, groupId);
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            pstmt.executeUpdate();
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while  updating group avatar - " + e);
            throw KnDbUtil.processException(e, "Failed while  updating group avatar " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteAllGroup(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllGroup(Collection<Integer>, KnPersisterTxn )";
        knLogger.info(methodName, "ENTRY : groupIdsList - ", groupIdsList != null ? groupIdsList.size() : null);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_ALL_GROUPS_INFO);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdsList));
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.info(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while deleting all the groups - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while deleting all the groups - ", e);
            throw KnDbUtil.processException(e, "Failed while deleting all the group  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteAllGrpHierarchy(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllGrpHierarchy(Collection<Integer>, KnPersisterTxn )";
        knLogger.info(methodName, "ENTRY : groupIdsList - ", groupIdsList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int count = 0;
        try {
            query = "DELETE FROM DG.GROUP_HIERARCHY_MAP WHERE CORPGROUPID IN (GROUPIDS)";
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdsList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            count = pstmt.executeUpdate();
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while deleting all the groups Hierarchy- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occurred while deleting all the groups Hierarchy - ", e);
            throw KnDbUtil.processException(e, "Failed while deleting all the group Hierarchy  " + e,
                    pttServerId, KnDAOSourceTypes.GROUP_HIERARCHY_MAP, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
            knLogger.info(methodName, "EXIT : Records Deleted: ", count);
        }
    }

    public ArrayList<Integer> getAllGroupsPrivateList(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAllGroupsPrivateList(Collection<Integer>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupIdsList - ", groupIdsList);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        ArrayList<Integer> sublistLists = new ArrayList<Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUPS_PRIVATE_LIST_IDS);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdsList));
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Exeuting query - ", "'", query, "'");
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                sublistLists.add(rs.getInt(1));
            }
            return sublistLists;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while fetching all the groups private List- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while fetching all the groups private List - ", e);
            throw KnDbUtil.processException(e, "Failed while fetching all the groups private List  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug( methodName, "EXIT. Groups private List size returned - ", sublistLists.size());
        }
    }

    public void updateGroupType(int groupType, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateGroupType(int, int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupType - ", groupType, ", groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_TYPE);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Exeuting query - ", query);
            pstmt.setInt(1, mappGroupTypeToDB(groupType));
            pstmt.setInt(2, groupId);
            pstmt.executeQuery();     
       knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating the group type- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while updating the group type - ", e);
            throw KnDbUtil.processException(e, "Failed while updating the group type  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public int getCorpGroupCount(int corpId, List<Integer> groupTypeList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpGroupCount(int, List, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, ", groupType - ", groupTypeList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        int groupCount = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GROUP_COUNT_BY_TYPE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            List<Integer> grpTypDBList = new ArrayList<>(groupTypeList.size());
            for(int id : groupTypeList){
               int dbId = mappGroupTypeToDB(id);
                grpTypDBList.add(dbId);
            }
            query = replaceContactWithValue(query, GROUPIDLIST, formIntegerCommaSeperatedIdList(grpTypDBList));
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Exeuting query - ", query);
            pstmt.setInt(1, corpId);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            if (rs.next()) {
                groupCount = rs.getInt(1);
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving the group count by type- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving the group count by type- ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group count by type  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT");
        }
         return groupCount;
    }

    /**
     * This method returns the group ids where the subscriber exist
     * @param subsGroupIdList
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, KnCorpGrpBasicInfoDTO> getGrpDetForGetDir(List<Integer> subsGroupIdList, int corpId, KnPersisterTxn
             persisterTxn) throws KnDAOException {
        String methodName = "getGrpDetForGetDir(List, int, KnPersisterTxn )";
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, KnCorpGrpBasicInfoDTO> grpInfoMap = new HashMap<Integer, KnCorpGrpBasicInfoDTO>(subsGroupIdList.size());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_ID_ETAG_WITHOUT_CORP);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(subsGroupIdList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Exeuting query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpGrpBasicInfoDTO knCorpGrpBasicInfoDTO = new KnCorpGrpBasicInfoDTO();
                knCorpGrpBasicInfoDTO.setGroupId(rs.getInt(1));
                knCorpGrpBasicInfoDTO.setGrpEtag(rs.getInt(2));
                knCorpGrpBasicInfoDTO.setGrpType(rs.getInt(3));
                knCorpGrpBasicInfoDTO.setGroupCreatedBy(rs.getInt(4));
                if(rs.getInt(6)==2){
                    knCorpGrpBasicInfoDTO.setMcxGroupInd(1);
                }
                knCorpGrpBasicInfoDTO.setCorpId(rs.getInt(7));
                knCorpGrpBasicInfoDTO.setIsPreConfiguredGroup(rs.getInt(8));
                knCorpGrpBasicInfoDTO.setGrpDisplayName(rs.getString(9).trim());
                grpInfoMap.put(rs.getInt(1),knCorpGrpBasicInfoDTO);
            }
            knLogger.debug( methodName, "EXIT. group ", grpInfoMap);
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group info  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return grpInfoMap;
    }

    public Map<Integer, Integer> getValidGrpInCorp(List<Integer> grpIdList, int corpId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException{
        String methodName = "getValidGrpInCorp(List<Integer>, int, boolean, KnPersisterTxn )";
        Connection conn = null;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        Map<Integer, Integer> grpIdTypeMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_ID_ETAG);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(grpIdList));
            query = KnDbUtil.replaceValInQry(query, corpId);
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
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
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Exeuting query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                grpIdTypeMap.put(rs.getInt(1), mappGroupTypeToApp(rs.getInt(3)));
            }
            knLogger.info(methodName, "EXIT. groupSize - ", grpIdTypeMap.size());
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group info  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return grpIdTypeMap;
    }
    public Map<Integer, Integer> getValidGrp(List<Integer> grpIdList, KnPersisterTxn persisterTxn)
            throws KnDAOException{
        String methodName = "getValidGrp(List<Integer>, int, KnPersisterTxn )";
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Integer> grpIdTypeMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_ID_ETAG_WITHOUT_CORP);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(grpIdList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Exeuting query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                grpIdTypeMap.put(rs.getInt(1), mappGroupTypeToApp(rs.getInt(3)));
            }
            knLogger.debug(methodName, "EXIT. groupSize - ", grpIdTypeMap.size());
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group info  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return grpIdTypeMap;
    }

    public ArrayList<KnCorpGroupDTO> getGrpsNameEtagInfo(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGrpsNameEtagInfo(Collection<Integer>, KnPersisterTxn )";
        knLogger.debug(methodName);
        Connection conn;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        ArrayList<KnCorpGroupDTO> groupBasicInfoList = new ArrayList<KnCorpGroupDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUPS_NAME_ETAG);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Exeuting query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpGroupDTO  groupDTO = new KnCorpGroupDTO();
                groupDTO.setGroupId(rs.getInt(1));
                //multilingual revert change
                if(null != rs.getString(2))
                {
                    try {
                        groupDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"),"UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(2), e);
                    }
                }else
                {
                    groupDTO.setGroupDisplayName(rs.getString(2));
                }
                groupDTO.setETag(rs.getInt(3));
                groupDTO.setCorpId(rs.getInt(4));
                groupDTO.setGroupType(mappGroupTypeToApp(rs.getInt(5)));
                groupBasicInfoList.add(groupDTO);
            }
            String updateEtagQuery = queryMapper.getQuery(UPDATE_GROUP_ETAG);
            pstmt = conn.prepareStatement(updateEtagQuery);
            for (KnCorpGroupDTO groupDTO : groupBasicInfoList) {
                int etag = groupDTO.getETag();
                pstmt.setInt(1, ++etag);
                groupDTO.setETag(etag);
                pstmt.setInt(3, groupDTO.getGroupId());
                pstmt.setLong(2, System.currentTimeMillis());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug( methodName, "EXIT. groupBasicInfoList size- ", groupBasicInfoList.size());
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group info  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            KnDbUtil.closeStatement(stmt);
        }
        return groupBasicInfoList;
    }

    public void modifyGroupOverrdeDND(int overrideDnd, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupOverrdeDND(String, int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : overrideDnd - ", overrideDnd, " , groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_OVERRIDEDND);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, overrideDnd);
            pstmt.setInt(2, groupId);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while  updating group name " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public ArrayList<KnCorpGroupDTO> getGroupBasicInfoList(Collection<Integer> groupIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupBasicInfoList(Collection<Integer>,boolean, KnPersisterTxn )";
        knLogger.info(methodName, "ENTRY: ");
        boolean ownedTxn = false;
        Connection conn = null;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        ArrayList<KnCorpGroupDTO> groupBasicInfoList = new ArrayList<KnCorpGroupDTO>();
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUPS_NAME_ETAG);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Exeuting query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpGroupDTO  groupDTO = new KnCorpGroupDTO();
                groupDTO.setGroupId(rs.getInt(1));
                //multilingual revert change
                if(null != rs.getString(2))
                {
                    try {
                        groupDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"),"UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, "UTF-8 encoding exception - ", rs.getString(2), e);
                    }
                }else
                {
                    groupDTO.setGroupDisplayName(rs.getString(2));
                }
                groupDTO.setETag(rs.getInt(3));
                groupDTO.setCorpId(rs.getInt(4));
                groupDTO.setGroupType(mappGroupTypeToApp(rs.getInt(5)));
                groupDTO.setGroupCreatedBy(rs.getInt(7));
                if(rs.getInt(8) == 1){
                    groupDTO.setLargeGroup(true);
                }
                if(rs.getInt(8) == 2) {
                    groupDTO.setMcxGrpInd(1);
                }else {
                    groupDTO.setMcxGrpInd(0);
                }
                groupDTO.setGrpMemListId(rs.getInt(9));
                groupDTO.setGrpShared((Integer) rs.getObject(10));
                groupDTO.setPocHome(rs.getString(11));
                groupDTO.setUgwInterop((Integer) rs.getObject("UGWINTEROP"));
                groupDTO.setVideoPermission(rs.getInt(13));
                groupDTO.setHierarchyId(rs.getString("HIERARCHY_ID"));
                groupDTO.setGroupProfileId(rs.getString("GRP_PROFILE_ID"));
                groupBasicInfoList.add(groupDTO);
            }
            knLogger.info(methodName, "EXIT. groupBasicInfoList size- ", groupBasicInfoList.size());
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group info  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return groupBasicInfoList;
    }

    public Collection<Integer> getGroupIdList(int corpId,  KnPersisterTxn persisterTxn)  throws KnDAOException{

        String methodName = "getGroupIdList(corpId, KnPersisterTxn )";
        knLogger.debug(methodName);
        Connection conn = null;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        ArrayList<Integer> groupIdList = new ArrayList<Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_ID_LIST);
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
            stmt = conn.createStatement();
            query = KnDbUtil.replaceValInQry(query, corpId);
            knLogger.debug(methodName, "Exeuting query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                groupIdList.add(rs.getInt(1));
            }
            knLogger.debug( methodName, "EXIT. groupIdList size- ", groupIdList.size());
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group list for the corporate  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return groupIdList;
    }

    public Set<Integer> groupsPushedToSublists(Collection<Integer> removeSublistIds, KnPersisterTxn persisterTxn)  throws KnDAOException{

        String methodName = "groupsPushedToSublists(removeSublistIds, pttServerId, KnPersisterTxn )";
        knLogger.debug(methodName);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Set<Integer> groupIdList = new HashSet<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GROUP_PUSHED_TO_SUBLISTS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String sublistListStr = formIntegerCommaSeperatedIdList(removeSublistIds);
            query = replaceContactWithValue(query, SUBLISTID, sublistListStr);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Exeuting query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                groupIdList.add(rs.getInt(1));
            }
            knLogger.debug( methodName, "EXIT. groupIdList size- ", groupIdList.size());
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group list for the corporate  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return groupIdList;
    }


    /**
     * This method will be used to get the group details by name
     * @param corpId
     * @param grpPrefix
     * @param persisterTxn
     * @return
     */
    public KnCorpGroupDTO getGroupDetailsByName(int corpId, String grpPrefix, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getGroupDetailsByName(corpId, grpPrefix, KnPersisterTxn )";
        knLogger.debug(methodName);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        KnCorpGroupDTO groupDTO = new KnCorpGroupDTO();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_BASIC_INFO_DETAILS_BY_NAME);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            //multilingual revert changes
            pstmt.setInt(1,corpId);
            try {
                grpPrefix=new String(grpPrefix.getBytes("UTF-8"),"8859_1");
            } catch (UnsupportedEncodingException e) {
                knLogger.error("UnsupportedEncodingException while parsing group name ",e);
            }
            pstmt.setString(2,grpPrefix);
            knLogger.debug(methodName, "Exeuting query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                groupDTO.setGroupId(rs.getInt(1));
                groupDTO.setETag(rs.getInt(2));
            }
            knLogger.debug( methodName, "EXIT. groupDTO details is- ", groupDTO);
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group details by the name ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return groupDTO;
    }

    public void modifyGroupLmrInteropCapable(int lmrInteropCapable, Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupLmrInteropCapable(int, Collection<Integer>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : lmrInteropCapable - ", lmrInteropCapable, " , groupIds - ", groupIds);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_LMR_INTEROP_CAPABLE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (Integer groupId : groupIds) {
                pstmt.setInt(1, lmrInteropCapable);
                pstmt.setInt(2, groupId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug( methodName, "Executing query - ", "'", query, "'");
            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while  modifyGroupLmrInteropCapable - " + e);
            throw KnDbUtil.processException(e, "Failed while  modifyGroupLmrInteropCapable " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void modifyGroupUGWParameter(int ugwInterop, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupLmrInteropCapable(int, Collection<Integer>, KnPersisterTxn )";
        knLogger.info(methodName, "ENTRY : ugwInterop - ", ugwInterop, " , groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int affectedRows = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_UGWINTEROP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, ugwInterop);
            pstmt.setInt(2, groupId);
            affectedRows = pstmt.executeUpdate();
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while  ugwInterop - " + e);
            throw KnDbUtil.processException(e, "Failed while  ugwInterop " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "EXIT: No. of updated rows are : ", affectedRows);
        }
    }

    public void modifyGroupRecordingFsParameter(int recordingFs, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupRecordingFsParameter()";
        knLogger.info(methodName, "ENTRY : recordingFs - ", recordingFs, " , groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int affectedRows = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_RECORDING_FS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, recordingFs);
            pstmt.setInt(2, groupId);
            affectedRows = pstmt.executeUpdate();
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while updating recordingFs - " + e);
            throw KnDbUtil.processException(e, "Failed while updating recordingFs " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "EXIT: No. of updated rows are : ", affectedRows);
        }
    }
    public void modifyAuthorizedLargeTGParameter(int authorizedLargeTG, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyAuthorizedLargeTGParameter(int, Collection<Integer>, KnPersisterTxn )";
        knLogger.info(methodName, "ENTRY : authorizedLargeTG - ", authorizedLargeTG, " , groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int affectedRows = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_AUTHORIZED_LARGE_TG);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, authorizedLargeTG);
            pstmt.setInt(2, groupId);
            affectedRows = pstmt.executeUpdate();
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while  authorizedLargeTG - " + e);
            throw KnDbUtil.processException(e, "Failed while  authorizedLargeTG " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "EXIT: No. of updated rows are : ", affectedRows);
        }
    }

    public Map<String, Integer> getValidGrpTypeInCorp(Collection<String> grpIdList, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException{
        String methodName = "getValidGrpTypeInCorp(List<String>, int, KnPersisterTxn )";
        knLogger.debug( methodName, "grpIdList - ", grpIdList);
        Connection conn = null;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Integer> grpIdTypeMap = new HashMap<>();
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
            query = queryMapper.getQuery(GET_GROUP_ID_ETAG);
            Collection<String> grpIds = new ArrayList<>();
            for(String grp : grpIdList){
                if(grp != null){
                    grpIds.add(grp);
                }
            }
            knLogger.debug( methodName, "grpIds - ", grpIds);
            query = replaceContactWithValue(query, GROUPIDS, formCommaSeperatedIdList(grpIds));
            query = KnDbUtil.replaceValInQry(query, corpId);
           // conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            knLogger.debug( methodName, "Exeuting query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                grpIdTypeMap.put(String.valueOf(rs.getInt(1)), mappGroupTypeToApp(rs.getInt(3)));
            }
            knLogger.debug(methodName, "EXIT. groupSize - ", grpIdTypeMap.size());
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group info  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return grpIdTypeMap;
    }

    public void modifyGroupEmergAttributes(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupEmergAttributes(KnIPCorpGroupInfoDTO, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupInfoDTO - ", groupInfoDTO);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            if(groupInfoDTO.getEmergAutoFloorTimer() != null && groupInfoDTO.getHangTimeOut() != LESS_THAN_LIMIT){
                query = queryMapper.getQuery(UPDATE_GROUP_EMERG_AUTO_FLOOR_TIMER);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, groupInfoDTO.getEmergAutoFloorTimer());
                pstmt.setInt(2, groupInfoDTO.getGroupId());
                knLogger.debug( methodName, "Executing query - ", query);
                pstmt.executeQuery();
            } else if(groupInfoDTO.getEmergAutoFloorTimer() != null && groupInfoDTO.getEmergAutoFloorTimer() == LESS_THAN_LIMIT){
                query = queryMapper.getQuery(UPDATE_GROUP_EMERG_AUTO_FLOOR_TIMER);
                pstmt = conn.prepareStatement(query);
                pstmt.setNull(1, Types.INTEGER);
                pstmt.setInt(2, groupInfoDTO.getGroupId());
                knLogger.debug( methodName, "Executing query - ", query);
                pstmt.executeQuery();
            }
            KnDbUtil.closePreparedStatement(pstmt); pstmt = null;

            if(groupInfoDTO.getEmergHangTimeAddOn() != null && groupInfoDTO.getHangTimeOut() != LESS_THAN_LIMIT){
                query = queryMapper.getQuery(UPDATE_GROUP_EMERG_HANG_TIME_ADDON);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, groupInfoDTO.getEmergHangTimeAddOn());
                pstmt.setInt(2, groupInfoDTO.getGroupId());
                knLogger.debug( methodName, "Executing query - ", query);
                pstmt.executeQuery();
            } else if(groupInfoDTO.getEmergHangTimeAddOn() != null && groupInfoDTO.getEmergHangTimeAddOn() == LESS_THAN_LIMIT){
                query = queryMapper.getQuery(UPDATE_GROUP_EMERG_HANG_TIME_ADDON);
                pstmt = conn.prepareStatement(query);
                pstmt.setNull(1, Types.INTEGER);
                pstmt.setInt(2, groupInfoDTO.getGroupId());
                knLogger.debug( methodName, "Executing query - ", query);
                pstmt.executeQuery();
            }
            KnDbUtil.closePreparedStatement(pstmt); pstmt = null;

            if(groupInfoDTO.getEmergOverrideDND() != null && groupInfoDTO.getHangTimeOut() != LESS_THAN_LIMIT){
                query = queryMapper.getQuery(UPDATE_GROUP_EMERG_OVERRIDE_DND);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, groupInfoDTO.getEmergOverrideDND());
                pstmt.setInt(2, groupInfoDTO.getGroupId());
                knLogger.debug( methodName, "Executing query - ", query);
                pstmt.executeQuery();
            } else if(groupInfoDTO.getEmergOverrideDND() != null && groupInfoDTO.getEmergOverrideDND() == LESS_THAN_LIMIT){
                query = queryMapper.getQuery(UPDATE_GROUP_EMERG_OVERRIDE_DND);
                pstmt = conn.prepareStatement(query);
                pstmt.setNull(1, Types.INTEGER);
                pstmt.setInt(2, groupInfoDTO.getGroupId());
                knLogger.debug( methodName, "Executing query - ", query);
                pstmt.executeQuery();
            }
            KnDbUtil.closePreparedStatement(pstmt); pstmt = null;

            if(groupInfoDTO.getHangTimeOut() != null && groupInfoDTO.getHangTimeOut() != LESS_THAN_LIMIT){
                query = queryMapper.getQuery(UPDATE_GROUP_EMERG_HANG_TIME_OUT);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, groupInfoDTO.getHangTimeOut());
                pstmt.setInt(2, groupInfoDTO.getGroupId());
                knLogger.debug( methodName, "Executing query - ", query);
                pstmt.executeQuery();
            } else if(groupInfoDTO.getHangTimeOut() != null && groupInfoDTO.getHangTimeOut() == LESS_THAN_LIMIT){
                query = queryMapper.getQuery(UPDATE_GROUP_EMERG_HANG_TIME_OUT);
                pstmt = conn.prepareStatement(query);
                pstmt.setNull(1, Types.INTEGER);
                pstmt.setInt(2, groupInfoDTO.getGroupId());
                knLogger.debug( methodName, "Executing query - ", query);
                pstmt.executeQuery();
            }

            knLogger.debug( methodName, "EXIT: Query executed successfully");
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while  modifyGroupEmergAttributes - " + e);
            throw KnDbUtil.processException(e, "Failed while  modifyGroupEmergAttributes " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public KnCorpGroupDTO getGroupIdByName(String grpPrefix, int clientIntf, String ownerMdn, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getGroupIdByName(grpPrefix, int, KnPersisterTxn )";
        knLogger.debug(methodName);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        KnCorpGroupDTO groupDTO = new KnCorpGroupDTO();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_ID_BY_NAME);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            try {
                grpPrefix=new String(grpPrefix.getBytes("UTF-8"),"8859_1");
            } catch (UnsupportedEncodingException e) {
                knLogger.error("UnsupportedEncodingException while parsing group name ",e);
            }
            pstmt.setString(1,grpPrefix);
            if(clientIntf == AREA_BASED_DYNAMIC_GROUP) {
                pstmt.setInt(2, ABDG.value());
            } else {
                pstmt.setInt(2, DYNAMIC_CGMT_INTF.value());
            }
            pstmt.setString(3, ownerMdn);
            knLogger.debug(methodName, "Exeuting query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            if (rs.next()) {
                groupDTO.setGroupId(rs.getInt(1));
                groupDTO.setCorpId(rs.getInt(2));
            } else {
                knLogger.error( methodName, "Group does not exists. grpPrefix - ", grpPrefix);
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "GroupInfo not found.", pttServerId, KnDAOSourceTypes.GRPINFO, query);
            }
            knLogger.debug( methodName, "EXIT. groupDTO details is- ", groupDTO);
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group details by the name ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return groupDTO;
    }

    public Collection<KnCorpGroupInfoPersistDTO> selectTpGroupList(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectTpGroupList(int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : corpId - ", corpId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpGroupInfoPersistDTO> groupList = new ArrayList<KnCorpGroupInfoPersistDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_TP_SUBSC_GROUP_LIST);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setInt(2, DYNAMIC_CGMT_INTF.value());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                if(rs.getString(6) != null){
                    groupPersistDTO.setTpGroupOwner(rs.getString(6).trim());
                }
                groupPersistDTO.setGroupId(rs.getInt(1));
                if (rs.getString(2) != null) {
                    groupPersistDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                }
                groupPersistDTO.setGroupType(mappGroupTypeToApp(rs.getInt(3)));
                groupPersistDTO.setAvatar(rs.getInt(4));
                groupPersistDTO.setOverrideDnd(rs.getInt(5));
                groupList.add(groupPersistDTO);
            }
            knLogger.debug(methodName, "Group List fetched sucecssfully.Size of List  - ", groupList.size());
            return groupList;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving Subs GroupList - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve Subs GroupList " + e, pttServerId, KnDAOSourceTypes.SUBSGRPLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    
    public boolean updateGroupOwner(String oldGroupOwner, String newGroupOwner, int corpID, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "updateGroupOwner(oldMdn, newMdn, KnPersisterTxn )";
        knLogger.debug(methodName, "oldGroupOwner-", oldGroupOwner, "newGroupOwner-", newGroupOwner, "corpID-", corpID);
        
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        boolean status  = false;
        
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_OWNER);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            
            pstmt.setString(1,newGroupOwner);
            pstmt.setString(2,oldGroupOwner);
            pstmt.setInt(3,corpID);
            
            knLogger.debug(methodName, "Exeuting query - ", query);
            status = pstmt.execute();
            knLogger.debug( methodName, "Query executed successfully - ", status);
            
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while updating the group owners",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
            
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT. groupDTO details is- ", status);
        return status;
    }

    public boolean updateGroupOwner(List<String> oldGroupOwner, String newGroupOwner, int corpID, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "updateGroupOwner(oldMdn, newMdn, KnPersisterTxn )";
        knLogger.debug(methodName, "oldGroupOwner-", oldGroupOwner, "newGroupOwner-", newGroupOwner, "corpID-", corpID);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        boolean status  = false;
        int index = 2;
        try {
            query = "UPDATE DG.CORPGROUPINFO SET GROUP_OWNER = ? WHERE GROUP_OWNER IN (OLDGROUPOWNERS) and CORPID = ?";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(oldGroupOwner,query,"OLDGROUPOWNERS");
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1,newGroupOwner);
            for(String itr : oldGroupOwner){
                pstmt.setString(index++,itr);
            }
            pstmt.setInt(index,corpID);

            knLogger.debug(methodName, "Exeuting query - ", query);
            status = pstmt.execute();
            knLogger.debug( methodName, "Query executed successfully - ", status);

        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while updating the group owners",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);

        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT. groupDTO details is- ", status);
        return status;
    }

    public Collection<Integer> getCorpGroupCountIntf(int corpId, int intf, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpGroupCountIntf(int, int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, ", groupType - ", intf);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Collection<Integer> groupIds = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GROUP_COUNT_BY_INTF);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Exeuting query - ", query);
            pstmt.setInt(1, corpId);
            pstmt.setInt(2, intf);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                groupIds.add(rs.getInt(1));
            }
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving the group ids by intf- ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group count by type  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT");
        }
        return groupIds;
    }

    public int getCorpGroupCountIntfPerOwner(int corpId, int intf, String groupOwner, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpGroupCountIntfPerOwner(int, int, String, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, ", groupType - ", intf, "groupOwner - ", groupOwner);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        int groupCount = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GROUP_COUNT_BY_INTF_PER_OWNER);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Exeuting query - ", query);
            pstmt.setInt(1, corpId);
            pstmt.setInt(2, intf);
            pstmt.setString(3, groupOwner);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            if (rs.next()) {
                groupCount = rs.getInt(1);
            }
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving the group count by intf- ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group count by type  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug( methodName, "EXIT");
        }
        return groupCount;
    }

    /**
     * Method to return list of large group.
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
   public List<KnCorpGroupDTO> getAllLargeGroups(KnPersisterTxn persisterTxn) throws KnDAOException {
       String methodName = "getAllLargeGroups()";
       Connection conn = null;
       PreparedStatement pstmt = null;
       String query = null;
       ResultSet rs = null;
       boolean ownedTxn = false;
       List<KnCorpGroupDTO> lrgGrpList = new ArrayList<>();
       try {
           KnQueryMapper queryMapper = KnQueryMapper.getInstance();
           query = queryMapper.getQuery(SELECT_ALL_LARGE_GROUPS);
           if (persisterTxn != null) {
               knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
               conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
           } else {
               conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
               ownedTxn = true;
           }
           //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
           pstmt = conn.prepareStatement(query);
           knLogger.debug( methodName, "Exeuting query - ", query);
           pstmt.setInt(1, 1);
           rs = pstmt.executeQuery();
           knLogger.debug( methodName, "Query executed successfully");
           while (rs.next()) {
               KnCorpGroupDTO groupDTO = new KnCorpGroupDTO();
               groupDTO.setGroupId(rs.getInt(1));
               groupDTO.setCorpId(rs.getInt(2));
               groupDTO.setGroupType(mappGroupTypeToApp(rs.getInt(3)));

               lrgGrpList.add(groupDTO);
           }
       } catch (Exception e) {
           throw KnDbUtil.processException(e, "Failed while retrieving the group count by type  " + e,
                   pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
       } finally {
           KnDbUtil.closeResultSet(rs);
           KnDbUtil.closeStatement(pstmt);
           if (ownedTxn) {
               KnDbUtil.closeConnection(conn);
           }
       }
       knLogger.debug( methodName, "Returning lrgGrpList - ", lrgGrpList);
       return lrgGrpList;
   }


    public void updateIsLargeGrpFlag(Map<Integer, Integer> groupLrgGrpFlagMap, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "updateIsLargeGrpFlag()";
        knLogger.debug(methodName, "groupLrgGrpFlagMap -", groupLrgGrpFlagMap);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_IS_LARGEGROUP_FLAG);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for(Map.Entry<Integer, Integer> entry: groupLrgGrpFlagMap.entrySet()){
                pstmt.setInt(1, entry.getValue());
                pstmt.setInt(2, entry.getKey());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Query executed successfully");
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while updating the group info",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);

        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }


    public int getLrgAbdgGroupCount(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getLrgAbdgGroupCount()";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        int counter = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_LARGE_ABDG_GROUP_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Exeuting query - ", query);
            pstmt.setInt(1, corpId);
            pstmt.setInt(2, com.kodiak.common.resources.KnConstants.GROUP_CREATED_BY_ABDG_INTF);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            if (rs.next()) {
                counter = rs.getInt(1);
            }
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving the group ids by intf- ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group count by type  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT", counter);
        return counter;
    }

    public Collection<String> getLocWatcherAndDispatcher(Collection<Integer> grpIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getLocWatcherAndDispatcher()";
        knLogger.debug(methodName, "ENTRY : grpIds - ", grpIds);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Set<String> mdnList = new HashSet<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_LOC_WATCHER_AND_DISPATCHER);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(grpIds));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                mdnList.add(rs.getString(1).trim());
            }
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving the group member properties- ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group member properties  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "EXIT", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        return mdnList;
    }

    public void updateGroupInfoProperties(Map<Integer, KnCorpGroupDTO> groupEtagList, KnCorpGroupProfilePersistDTO
            groupProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateGroupInfoProperties()";
        PreparedStatement pstmt = null;
        String query = null;
        boolean skipGrpShared = groupProfilePersistDTO.isSkipGrpSharedUpdate();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            if (skipGrpShared) {
                query = queryMapper.getQuery(UPDATE_GROUP_PROPERTIES_BY_PROFILE_ID_NO_SHARED);
            } else {
                query = queryMapper.getQuery(UPDATE_GROUP_PROPERTIES_BY_PROFILE_ID);
            }
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            Long curentTime = System.currentTimeMillis();
            for (Integer groupId : groupEtagList.keySet()) {
                int etag = groupEtagList.get(groupId).getETag() + 1;
                pstmt.setInt(1, etag);
                pstmt.setLong(2, curentTime);
                //Setting for overrideDND
                if (groupProfilePersistDTO.getOverrideDND() != null) {
                    pstmt.setInt(3, groupProfilePersistDTO.getOverrideDND());
                } else {
                    pstmt.setNull(3, Types.INTEGER);
                }
                //Setting for Avatar
                if (groupProfilePersistDTO.getGrpAvatar() != null) {
                    pstmt.setInt(4, groupProfilePersistDTO.getGrpAvatar());
                } else {
                    pstmt.setNull(4, Types.INTEGER);
                }

                //Setting for OSMLISID
                if (groupProfilePersistDTO.getGrpOSMListId() != null) {
                    pstmt.setInt(5, Integer.parseInt(groupProfilePersistDTO.getGrpOSMListId()));
                } else {
                    pstmt.setNull(5, Types.INTEGER);
                }

                //Setting for Group Service Type
                if (groupProfilePersistDTO.getGrpServiceType() != null) {
                    pstmt.setInt(6, groupProfilePersistDTO.getGrpServiceType());
                } else {
                    pstmt.setNull(6, Types.INTEGER);
                }

                //Setting for Allowed Feature
                if (groupProfilePersistDTO.getFeatureAllowed() != null) {
                    pstmt.setInt(7, groupProfilePersistDTO.getFeatureAllowed());
                } else {
                    pstmt.setNull(7, Types.INTEGER);
                }

                //Setting for AudioCutin
                if (groupProfilePersistDTO.getAudioCutIn() != null) {
                    pstmt.setInt(8, groupProfilePersistDTO.getAudioCutIn());
                } else {
                    pstmt.setNull(8, Types.INTEGER);
                }

                if (skipGrpShared) {
                    // Query 744: no GROUP_SHARED column — params are pos 9=UGWINTEROP, 10=CORPGROUPID
                    if (null != groupProfilePersistDTO.getUgwInterop()) {
                        pstmt.setInt(9, Integer.parseInt(groupProfilePersistDTO.getUgwInterop()));
                    } else {
                        pstmt.setNull(9, Types.INTEGER);
                    }
                    pstmt.setInt(10, groupId);
                } else {
                    // Query 529: includes GROUP_SHARED — params are pos 9=GROUP_SHARED, 10=UGWINTEROP, 11=CORPGROUPID
                    if (groupProfilePersistDTO.getGrpShared() != null) {
                        pstmt.setInt(9, groupProfilePersistDTO.getGrpShared());
                    } else {
                        pstmt.setNull(9, Types.INTEGER);
                    }
                    if (null != groupProfilePersistDTO.getUgwInterop()) {
                        pstmt.setInt(10, Integer.parseInt(groupProfilePersistDTO.getUgwInterop()));
                    } else {
                        pstmt.setNull(10, Types.INTEGER);
                    }
                    pstmt.setInt(11, groupId);
                }
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while inserting into the group info table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void modifyGroup_GrpSharedFlag(Map<Integer, Integer> groupIdSharedFlagMap, KnPersisterTxn persisterTxn)  throws KnDAOException{
        String methodName = "modifyGroup_GrpSharedFlag()";
        knLogger.debug(methodName, "groupIdSharedFlagMap -", groupIdSharedFlagMap);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUPS_GROUP_SHARED_FLAG);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for(Map.Entry<Integer, Integer> entry: groupIdSharedFlagMap.entrySet()){
                pstmt.setInt(1, entry.getValue());
                pstmt.setInt(2, entry.getKey());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Query executed successfully");
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while updating the group info",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);

        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public List<Integer> getGroupIdsByMemberAndGroupType(String mdn,Integer groupType,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupIdsByMemberAndGroupType()";
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        List<Integer> groupIds = new ArrayList<>();
        try {
            knLogger.debug(methodName," mdn :",KnGDPRTemplate.mdn(mdn)," groupType:",groupType);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUPIDS_BY_MEMBER_AND_GROUP_TYPE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Exeuting query - ", query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2,groupType);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groupIds.add(rs.getInt(1));
            }
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group by type  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug( methodName, "Returning groupIds - ", groupIds);
        return groupIds;
    }

    public List<Integer> getSharedCorpIds(int groupId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSharedCorpIds(groupId, boolean, persisterTxn)";
        knLogger.info(methodName, "Entry : groupId - ", groupId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Integer> sharedCorpids = new ArrayList<>();
        String query = "SELECT SHAREDCORPID FROM DG.CORPGRP_SHAREDLIST WHERE CORPGROUPID=?;";
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sharedCorpids.add(rs.getInt(1));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getSharedCorpIds  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while getSharedCorpIds - ", e);
            throw KnDbUtil.processException(e, "Failed to get SharedCorpIds -" + e,
                    xdmsHome, com.kodiak.xdms.server.common.resources.KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.info(methodName, "Exit :", sharedCorpids);
        return sharedCorpids;
    }

    public List<Integer> getUpmSharedCorpIds(String userProfileId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUpmSharedCorpIds(groupId, boolean, persisterTxn)";
        knLogger.info(methodName, "Entry : userProfileId - ", userProfileId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Integer> sharedCorpids = new ArrayList<>();
        String query = "SELECT SHAREDCORPID FROM DG.USERPROFILE_SHAREDLIST WHERE USERPROFILEID=?";
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userProfileId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sharedCorpids.add(rs.getInt(1));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getSharedCorpIds  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while getSharedCorpIds - ", e);
            throw KnDbUtil.processException(e, "Failed to get SharedCorpIds -" + e,
                    xdmsHome, com.kodiak.xdms.server.common.resources.KnDAOSourceTypes.CORPGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.info(methodName, "Exit :", sharedCorpids);
        return sharedCorpids;
    }
    public void modifyBulkGroupProperties(List<KnXDMGroupPropertyInfoDTO> bulkGroupProperties, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupProperties(String, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : bulkGroupProperties - ", bulkGroupProperties);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        try {

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            for (KnXDMGroupPropertyInfoDTO propertyInfoDTO : bulkGroupProperties) {
                int columnIndex = 0;
                ArrayList<String> queryFields = new ArrayList<>();
                if (null != propertyInfoDTO.getRecordingFS()) {
                    queryFields.add("RECORDING_FS");
                }
                if (null != propertyInfoDTO.getUgwInterop()) {
                    queryFields.add("UGWINTEROP");
                }
                String insertQry = com.kodiak.common.dao.KnDbUtil.getUpdateCorpGroupPropsQuery("DG.CORPGROUPINFO", queryFields);

                pstmt = conn.prepareStatement(insertQry);

                if (null != propertyInfoDTO.getRecordingFS()) {
                    pstmt.setString(++columnIndex, propertyInfoDTO.getRecordingFS());
                }
                if (null != propertyInfoDTO.getUgwInterop()) {
                    pstmt.setString(++columnIndex, propertyInfoDTO.getUgwInterop());
                }

                pstmt.setString(++columnIndex, propertyInfoDTO.getGroupId());
                pstmt.executeUpdate();
                pstmt.close();

            }

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving the corpFS- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception  occurred while  retrieving the corpFS  - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while  retrieving the corporate etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteGroupHierarchy(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteGroupHierarchy(int, KnPersisterTxn )";
        knLogger.info(methodName, "ENTRY : groupId - ", groupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_GROUP_HIERARCHY);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug(methodName, "Exeuting query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.info(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting group Hierarchy - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while deleting group Hierarchy- ", e);
            throw KnDbUtil.processException(e, "Failed while deleting group  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
            knLogger.info(methodName, "EXIT");
        }
    }

    public List<KnCorpGroupInfoDTO> getGroupsDetailsWithoutMembers(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupsDetailsWithoutMembers(Collection<Integer>, KnPersisterTxn )";
        knLogger.entry(methodName, "ENTRY : groupIds - ", groupIds == null ? 0 : groupIds.size());
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        var corpGroupInfoDTOS = new ArrayList<KnCorpGroupInfoDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_LIST_ETAG);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                var corpGroupInfoDTO = new KnCorpGroupInfoDTO();
                corpGroupInfoDTO.setGroupId(rs.getInt(1));
                corpGroupInfoDTO.setETag(rs.getInt(2));
                corpGroupInfoDTOS.add(corpGroupInfoDTO);
            }
            knLogger.exit(methodName, "EXIT : group Info returned - ", corpGroupInfoDTOS.size());
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while retrieving group basic Information -  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return corpGroupInfoDTOS;
    }
    public Set<Integer> getCorpGroupByOSMListIdMap(Map<Integer, Integer> groupOSMListIdMap,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpGroupByOSMListIdMap(Map<Integer, Integer> , KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : OsmListId - ", groupOSMListIdMap);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Set<Integer> groupIds = new HashSet<>();
        try {
            if (isObjectNullOrEmpty(groupOSMListIdMap)) {
                knLogger.info(methodName, "Returning empty data as input is not valid ", groupOSMListIdMap);
                return groupIds;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORPGROUPID_BY_GROUP_AND_OSMLISTID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            String groupCorpQuestionMark = formCommaSeperatedBulkIntegerQuesMarks(groupOSMListIdMap);
            query = replaceContactWithValue(query, GROUPOSMMAP, groupCorpQuestionMark);

            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Exeuting query - ", query);
            int index = 1;
            for (var entry : groupOSMListIdMap.entrySet()) {
                pstmt.setInt(index++, entry.getKey());
                pstmt.setInt(index++, entry.getValue());
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                groupIds.add(rs.getInt("CORPGROUPID"));
            }

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the group count by type- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving the group count by type- ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group count by type  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT groupIds:", groupIds);
        }
        return groupIds;
    }
    public void modifyBulkGroupName(Map<Integer, String> grpIdvsDisplayName, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupName(Map<Integer, String>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : grpIdvsDisplayName - ", grpIdvsDisplayName);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        var failedJobIdList = new ArrayList<Integer>();
        List<Integer> processJobList = new LinkedList<Integer>(grpIdvsDisplayName.keySet());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_NAME);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            while (processJobList.size() > 0) {
                for (Integer groupId : processJobList) {
                    var groupName = grpIdvsDisplayName.get(groupId);
                    //multilingual revert changes
                    if (null != groupName) {
                        try {
                            pstmt.setString(1, new String(groupName.getBytes("UTF-8"), "8859_1"));
                        } catch (Exception e) {
                            knLogger.error(methodName, "UTF-8 encoding exception - ", groupName, e);
                        }
                    } else {
                        pstmt.setString(1, groupName);
                    }
                    pstmt.setInt(2, groupId);
                    pstmt.addBatch();
                }

                try {
                    knLogger.debug(methodName, "QUERY: Executing", query, " with ", processJobList);
                    int[] updateCount = pstmt.executeBatch();
                    if (updateCount.length == processJobList.size()) {
                        processJobList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();

                    for (int failedRow : updateCount) {
                        failedJobIdList.add(processJobList.get(failedRow));
                        processJobList = processJobList.subList(0, failedRow + 1);
                    }
                }
            }
            knLogger.info(methodName, "QUERY: batch completed failedJobIdList - ", failedJobIdList);

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating group name- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while  updating group name - " + e);
            throw KnDbUtil.processException(e, "Failed while  updating group name " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    public void modifyBulkGroupOSMListId(Map<Integer, KnIPCorpGroupInfoDTO> grpIdvsOSMListIdMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupOSMListId(Map<Integer, KnIPCorpGroupInfoDTO>)";
        knLogger.debug(methodName, "ENTRY : ", grpIdvsOSMListIdMap.keySet());
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        var failedJobIdList = new ArrayList<Integer>();
        List<Integer> processJobList = new LinkedList<Integer>(grpIdvsOSMListIdMap.keySet());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_CORP_GROUP_OSM);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            while (processJobList.size() > 0) {
                for (Integer groupId : processJobList) {
                    //seting null if osmlistid is empty or none is selected.
                    var groupInfoDto = grpIdvsOSMListIdMap.get(groupId);
                    int corpId = groupInfoDto.getCorpId();
                    var OSMListId = groupInfoDto.getOSMListId();
                    if (OSMListId != null && !OSMListId.isEmpty()) {
                        pstmt.setInt(1, Integer.parseInt(OSMListId));
                    } else {
                        pstmt.setNull(1, Types.INTEGER);
                    }

                    pstmt.setInt(2, corpId);
                    pstmt.setInt(3, groupId);
                    pstmt.addBatch();
                }

                try {
                    knLogger.debug(methodName, "QUERY: Executing", query);
                    int[] updateCount = pstmt.executeBatch();
                    if (updateCount.length == processJobList.size()) {
                        processJobList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();

                    for (int failedRow : updateCount) {
                        failedJobIdList.add(processJobList.get(failedRow));
                        processJobList = processJobList.subList(0, failedRow + 1);
                    }
                }
            }
            knLogger.debug(methodName, "QUERY: batch completed");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while  updating group osmList - " + e);
            throw KnDbUtil.processException(e, "Failed while  updating group osmList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    public void modifyBulkGroupAvatar(Map<Integer, Integer> tempGrpIdvsAvatar, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupAvatar(Map<Integer, Integer> tempGrpIdvsAvatar, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY :  tempGrpIdvsAvatar - ", tempGrpIdvsAvatar);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_AVATAR);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            var failedJobIdList = new ArrayList<Integer>();
            List<Integer> processJobList = new LinkedList<>(tempGrpIdvsAvatar.keySet());

            while (processJobList.size() > 0) {
                for (Integer groupId : processJobList) {
                    Integer avatar = tempGrpIdvsAvatar.get(groupId);
                    if (avatar != null)
                        pstmt.setInt(1, avatar);
                    else
                        pstmt.setNull(1, Types.INTEGER);

                    pstmt.setInt(2, groupId);
                    pstmt.addBatch();
                }

                try {
                    knLogger.debug(methodName, "QUERY: Executing", query, " with ", tempGrpIdvsAvatar);
                    int[] updateCount = pstmt.executeBatch();
                    if (updateCount.length == processJobList.size()) {
                        processJobList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();

                    for (int failedRow : updateCount) {
                        failedJobIdList.add(processJobList.get(failedRow));
                        processJobList = processJobList.subList(0, failedRow + 1);
                    }

                }
            }
            knLogger.debug(methodName, "QUERY: batch completed");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while  updating group avatar - " + e);
            throw KnDbUtil.processException(e, "Failed while  updating group avatar " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    public void updateBulkGroupType(Map<Integer, Integer> grpIdvsIsGroupTypeChanged, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateBulkGroupType(Map<Integer, Integer>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : grpIdvsIsGroupTypeChanged - ", grpIdvsIsGroupTypeChanged);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        List<Integer> failedJobIdList = new ArrayList<Integer>();
        List<Integer> processJobList = new LinkedList<Integer>(grpIdvsIsGroupTypeChanged.keySet());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_TYPE);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);

            while (processJobList.size() > 0) {
                for (Integer groupId : processJobList) {
                    pstmt.setInt(1, mappGroupTypeToDB(grpIdvsIsGroupTypeChanged.get(groupId)));
                    pstmt.setInt(2, groupId);
                    pstmt.addBatch();
                }

                try {
                    knLogger.debug(methodName, "QUERY: Executing", query);
                    int[] updateCount = pstmt.executeBatch();
                    if (updateCount.length == processJobList.size()) {
                        processJobList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();

                    for (int failedRow : updateCount) {
                        failedJobIdList.add(processJobList.get(failedRow));
                        processJobList = processJobList.subList(0, failedRow + 1);
                    }

                }
            }
            knLogger.debug(methodName, "QUERY: batch completed. failedJobIdList - " , failedJobIdList);

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while updating the group type- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while updating the group type - ", e);
            throw KnDbUtil.processException(e, "Failed while updating the group type  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    public void modifyBulkGroupOverrdeDND(Map<Integer, Integer> groupIdVsOverrideDnd, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupOverrdeDND(Map<Integer, Integer>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupIdVsOverrideDnd - ", groupIdVsOverrideDnd);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        List<Integer> failedJobIdList = new ArrayList<Integer>();
        List<Integer> processJobList = new LinkedList<Integer>(groupIdVsOverrideDnd.keySet());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_OVERRIDEDND);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            while (processJobList.size() > 0) {
                for (Integer groupId : processJobList) {
                    pstmt.setInt(1, groupIdVsOverrideDnd.get(groupId));
                    pstmt.setInt(2, groupId);
                    pstmt.addBatch();
                }

                try {
                    knLogger.debug(methodName, "QUERY: Executing", query);
                    int[] updateCount = pstmt.executeBatch();
                    if (updateCount.length == processJobList.size()) {
                        processJobList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();

                    for (int failedRow : updateCount) {
                        failedJobIdList.add(processJobList.get(failedRow));
                        processJobList = processJobList.subList(0, failedRow + 1);
                    }
                }
            }
            knLogger.debug(methodName, "QUERY: batch completed failedJobIdList- ", failedJobIdList);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while  updating group name " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    public void modifyBulkGroupLmrInteropCapable(Map<Integer, Integer> grpIdvsIsLmrInteropFeatureChangedMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = " public void modifyBulkGroupLmrInteropCapable(Map<Integer, Integer>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : grpIdvsIsLmrInteropFeatureChangedMap - ", grpIdvsIsLmrInteropFeatureChangedMap);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        var failedJobIdList = new ArrayList<Integer>();
        List<Integer> processJobList = new LinkedList<Integer>(grpIdvsIsLmrInteropFeatureChangedMap.keySet());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_LMR_INTEROP_CAPABLE);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            while (processJobList.size() > 0) {
                for (Integer groupId : processJobList) {
                    pstmt.setInt(1, grpIdvsIsLmrInteropFeatureChangedMap.get(groupId));
                    pstmt.setInt(2, groupId);
                    pstmt.addBatch();
                }

                try {
                    knLogger.debug(methodName, "QUERY: Executing", query);
                    int[] updateCount = pstmt.executeBatch();
                    if (updateCount.length == processJobList.size()) {
                        processJobList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();

                    for (int failedRow : updateCount) {
                        failedJobIdList.add(processJobList.get(failedRow));
                        processJobList = processJobList.subList(0, failedRow + 1);
                    }

                }
            }
            knLogger.info(methodName, "QUERY: batch completed failedJobIdList- ", failedJobIdList);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while  modifyGroupLmrInteropCapable - " + e);
            throw KnDbUtil.processException(e, "Failed while  modifyGroupLmrInteropCapable " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    public void modifyBulkGroupUGWParameter(Map<Integer, Integer> groupIdVsUgwParameterMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupUGWParameter(Map<Integer, Integer>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupIdVsUgwParameterMap - ", groupIdVsUgwParameterMap);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        var failedJobIdList = new ArrayList<Integer>();
        List<Integer> processJobList = new LinkedList<Integer>(groupIdVsUgwParameterMap.keySet());

        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_UGWINTEROP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            while (processJobList.size() > 0) {
                for (Integer groupId : processJobList) {
                    pstmt.setInt(1, groupIdVsUgwParameterMap.get(groupId));
                    pstmt.setInt(2, groupId);
                    pstmt.addBatch();
                }
                try {
                    knLogger.debug(methodName, "QUERY: Executing", query);
                    int[] updateCount = pstmt.executeBatch();
                    if (updateCount.length == processJobList.size()) {
                        processJobList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();

                    for (int failedRow : updateCount) {
                        failedJobIdList.add(processJobList.get(failedRow));
                        processJobList = processJobList.subList(0, failedRow + 1);
                    }
                }
            }
            knLogger.info(methodName, "QUERY: batch completed. failedJobIdList- ", failedJobIdList);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while  ugwInterop - " + e);
            throw KnDbUtil.processException(e, "Failed while  ugwInterop " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    public void modifyBulkGroupRecordingFsParameter(Map<Integer, Integer> groupIdVsRecordingFsParameterMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupRecordingFsParameter(Map<Integer, Integer>)";
        knLogger.debug(methodName, "ENTRY : groupIdVsRecordingFsParameterMap - ",groupIdVsRecordingFsParameterMap);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        var failedJobIdList = new ArrayList<Integer>();
        List<Integer> processJobList = new LinkedList<Integer>(groupIdVsRecordingFsParameterMap.keySet());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_RECORDING_FS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            while (processJobList.size() > 0) {
                for (Integer groupId : processJobList) {
                    pstmt.setInt(1, groupIdVsRecordingFsParameterMap.get(groupId));
                    pstmt.setInt(2, groupId);
                    pstmt.addBatch();
                }

                try {
                    knLogger.debug(methodName, "QUERY: Executing", query);
                    int[] updateCount = pstmt.executeBatch();
                    if (updateCount.length == processJobList.size()) {
                        processJobList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();

                    for (int failedRow : updateCount) {
                        failedJobIdList.add(processJobList.get(failedRow));
                        processJobList = processJobList.subList(0, failedRow + 1);
                    }

                }
            }
            knLogger.info(methodName, "QUERY: batch completed, failedJobIdList - " , failedJobIdList);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while updating recordingFs - " + e);
            throw KnDbUtil.processException(e, "Failed while updating recordingFs " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    public void modifyBulkGroupEmergAttributes(Map<Integer, KnIPCorpGroupInfoDTO> groupInfoDTOMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupEmergAttributes(Map<Integer, KnIPCorpGroupInfoDTO> , KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : groupInfoDTOMap - ", groupInfoDTOMap);
        try {
            var tempGrpIdVsEmergAutoFloorTimer = new HashMap<Integer, Integer>();
            var tempGrpIdVsEmergHangTimeAddOn = new HashMap<Integer, Integer>();
            var tempGrpIdVsEmergOverrideDND = new HashMap<Integer, Integer>();
            var tempGrpIdVsHangTimeOut = new HashMap<Integer, Integer>();
            for (var entry : groupInfoDTOMap.entrySet()) {
                var groupId = entry.getKey();
                var groupInfoDTO = entry.getValue();
                if (groupInfoDTO.getEmergAutoFloorTimer() != null && groupInfoDTO.getHangTimeOut() != LESS_THAN_LIMIT) {
                    tempGrpIdVsEmergAutoFloorTimer.put(groupId, groupInfoDTO.getEmergAutoFloorTimer());
                } else if (groupInfoDTO.getEmergAutoFloorTimer() != null && groupInfoDTO.getEmergAutoFloorTimer() == LESS_THAN_LIMIT) {
                    tempGrpIdVsEmergAutoFloorTimer.put(groupId, null);
                }
                if (groupInfoDTO.getEmergHangTimeAddOn() != null && groupInfoDTO.getHangTimeOut() != LESS_THAN_LIMIT) {
                    tempGrpIdVsEmergHangTimeAddOn.put(groupId, groupInfoDTO.getEmergHangTimeAddOn());
                } else if (groupInfoDTO.getEmergHangTimeAddOn() != null && groupInfoDTO.getEmergHangTimeAddOn() == LESS_THAN_LIMIT) {
                    tempGrpIdVsEmergHangTimeAddOn.put(groupId, null);
                }
                if (groupInfoDTO.getEmergOverrideDND() != null && groupInfoDTO.getHangTimeOut() != LESS_THAN_LIMIT) {
                    tempGrpIdVsEmergOverrideDND.put(groupId, groupInfoDTO.getEmergOverrideDND());
                } else if (groupInfoDTO.getEmergOverrideDND() != null && groupInfoDTO.getEmergOverrideDND() == LESS_THAN_LIMIT) {
                    tempGrpIdVsEmergOverrideDND.put(groupId, null);
                }
                if (groupInfoDTO.getHangTimeOut() != null && groupInfoDTO.getHangTimeOut() != LESS_THAN_LIMIT) {
                    tempGrpIdVsHangTimeOut.put(groupId, groupInfoDTO.getHangTimeOut());
                } else if (groupInfoDTO.getHangTimeOut() != null && groupInfoDTO.getHangTimeOut() == LESS_THAN_LIMIT) {
                    tempGrpIdVsHangTimeOut.put(groupId, null);
                }
            }
            if (!tempGrpIdVsEmergAutoFloorTimer.isEmpty())
                updateBulkEmergAutoFloorTimer(tempGrpIdVsEmergAutoFloorTimer, persisterTxn);
            if (!tempGrpIdVsEmergHangTimeAddOn.isEmpty())
                updateBulkEmergHangTimeAddOn(tempGrpIdVsEmergHangTimeAddOn, persisterTxn);
            if (!tempGrpIdVsEmergOverrideDND.isEmpty())
                updateBulkEmergOverrideDND(tempGrpIdVsEmergOverrideDND, persisterTxn);
            if (!tempGrpIdVsHangTimeOut.isEmpty())
                updateBulkHangTimeOut(tempGrpIdVsHangTimeOut, persisterTxn);
            knLogger.info(methodName, "EXIT: Query executed successfully");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while  modifyGroupEmergAttributes - " + e);
            throw KnDbUtil.processException(e, "Failed while  modifyGroupEmergAttributes " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, "");
        }
    }

    private void updateBulkHangTimeOut(Map<Integer, Integer> tempGrpIdVsHangTimeOut, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateBulkHangTimeOut(Map<Integer, Integer> , KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: tempGrpIdVsHangTimeOut- ", tempGrpIdVsHangTimeOut);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        var failedJobIdList = new ArrayList<Integer>();
        List<Integer> processJobList = new LinkedList<Integer>(tempGrpIdVsHangTimeOut.keySet());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_EMERG_HANG_TIME_OUT);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            while (processJobList.size() > 0) {
                for (Integer groupId : processJobList) {
                    var hangTimeOut = tempGrpIdVsHangTimeOut.get(groupId);
                    if (hangTimeOut == null)
                        pStmt.setNull(1, Types.INTEGER);
                    else
                        pStmt.setInt(1, hangTimeOut);
                    pStmt.setInt(2, groupId);
                    pStmt.addBatch();
                }
                try {
                    knLogger.debug(methodName, "QUERY: Executing", query);
                    int[] updateCount = pStmt.executeBatch();
                    if (updateCount.length == processJobList.size()) {
                        processJobList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();

                    for (int failedRow : updateCount) {
                        failedJobIdList.add(processJobList.get(failedRow));
                        processJobList = processJobList.subList(0, failedRow + 1);
                    }
                }
            }
            knLogger.info(methodName, "QUERY: batch completed, failedJobIdList- ", failedJobIdList);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception", e);
            throw e;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception", e);
            throw KnDbUtil.processException(e, "Failed to job ID - " + e.getMessage(), pttServerId, "", query);
        } catch (Exception e) {
            knLogger.error(methodName, "Un Expected Exception", e);
            throw KnDbUtil.processException(e, "Failed to get jobId - " + e.getMessage(), pttServerId, "", query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT : failedJobIdList ->", failedJobIdList);
    }

    private void updateBulkEmergOverrideDND(HashMap<Integer, Integer> tempGrpIdVsEmergOverrideDND, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateBulkEmergOverrideDND(Map<Integer, Integer> , KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: tempGrpIdVsHangTimeOut- ", tempGrpIdVsEmergOverrideDND);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        var failedJobIdList = new ArrayList<Integer>();
        List<Integer> processJobList = new LinkedList<Integer>(tempGrpIdVsEmergOverrideDND.keySet());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_EMERG_OVERRIDE_DND);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            while (processJobList.size() > 0) {
                for (Integer groupId : processJobList) {
                    var emergOverrideDND = tempGrpIdVsEmergOverrideDND.get(groupId);
                    if (emergOverrideDND == null)
                        pStmt.setNull(1, Types.INTEGER);
                    else
                        pStmt.setInt(1, emergOverrideDND);
                    pStmt.setInt(2, groupId);
                    pStmt.addBatch();
                }
                try {
                    knLogger.debug(methodName, "QUERY: Executing", query);
                    int[] updateCount = pStmt.executeBatch();
                    if (updateCount.length == processJobList.size()) {
                        processJobList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();
                    for (int failedRow : updateCount) {
                        failedJobIdList.add(processJobList.get(failedRow));
                        processJobList = processJobList.subList(0, failedRow + 1);
                    }
                }
            }
            knLogger.info(methodName, "QUERY: batch completed, failedJobIdList- ", failedJobIdList);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception", e);
            throw e;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception", e);
            throw KnDbUtil.processException(e, "Failed to job ID - " + e.getMessage(), pttServerId, "", query);
        } catch (Exception e) {
            knLogger.error(methodName, "Un Expected Exception", e);
            throw KnDbUtil.processException(e, "Failed to get jobId - " + e.getMessage(), pttServerId, "", query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT : failedJobIdList ->", failedJobIdList);
    }

    private void updateBulkEmergHangTimeAddOn(HashMap<Integer, Integer> tempGrpIdVsEmergHangTimeAddOn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateBulkEmergOverrideDND(Map<Integer, Integer> , KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: tempGrpIdVsHangTimeOut- ", tempGrpIdVsEmergHangTimeAddOn);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        var failedJobIdList = new ArrayList<Integer>();
        List<Integer> processJobList = new LinkedList<Integer>(tempGrpIdVsEmergHangTimeAddOn.keySet());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_EMERG_HANG_TIME_ADDON);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            while (processJobList.size() > 0) {
                for (Integer groupId : processJobList) {
                    var emergHangTimeAddOn = tempGrpIdVsEmergHangTimeAddOn.get(groupId);
                    if (emergHangTimeAddOn == null)
                        pStmt.setNull(1, Types.INTEGER);
                    else
                        pStmt.setInt(1, emergHangTimeAddOn);
                    pStmt.setInt(2, groupId);
                    pStmt.addBatch();
                }
                try {
                    knLogger.debug(methodName, "QUERY: Executing", query);
                    int[] updateCount = pStmt.executeBatch();
                    if (updateCount.length == processJobList.size()) {
                        processJobList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();
                    for (int failedRow : updateCount) {
                        failedJobIdList.add(processJobList.get(failedRow));
                        processJobList = processJobList.subList(0, failedRow + 1);
                    }
                }
            }
            knLogger.info(methodName, "QUERY: batch completed, failedJobIdList- ", failedJobIdList);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception", e);
            throw e;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception", e);
            throw KnDbUtil.processException(e, "Failed to job ID - " + e.getMessage(), pttServerId, "", query);
        } catch (Exception e) {
            knLogger.error(methodName, "Un Expected Exception", e);
            throw KnDbUtil.processException(e, "Failed to get jobId - " + e.getMessage(), pttServerId, "", query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT : failedJobIdList ->", failedJobIdList);
    }

    private void updateBulkEmergAutoFloorTimer(HashMap<Integer, Integer> tempGrpIdVsEmergAutoFloorTimer, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateBulkEmergOverrideDND(Map<Integer, Integer> , KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: tempGrpIdVsHangTimeOut- ", tempGrpIdVsEmergAutoFloorTimer);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        var failedJobIdList = new ArrayList<Integer>();
        List<Integer> processJobList = new LinkedList<Integer>(tempGrpIdVsEmergAutoFloorTimer.keySet());
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_GROUP_EMERG_AUTO_FLOOR_TIMER);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            while (processJobList.size() > 0) {
                for (Integer groupId : processJobList) {
                    var emergAutoFloorTimer = tempGrpIdVsEmergAutoFloorTimer.get(groupId);
                    if (emergAutoFloorTimer == null)
                        pStmt.setNull(1, Types.INTEGER);
                    else
                        pStmt.setInt(1, emergAutoFloorTimer);
                    pStmt.setInt(2, groupId);
                    pStmt.addBatch();
                }
                try {
                    knLogger.debug(methodName, "QUERY: Executing", query);
                    int[] updateCount = pStmt.executeBatch();
                    if (updateCount.length == processJobList.size()) {
                        processJobList.clear();
                    }
                } catch (BatchUpdateException e) {
                    knLogger.error(methodName, "DAO Exception", e);
                    int[] updateCount = e.getUpdateCounts();
                    for (int failedRow : updateCount) {
                        failedJobIdList.add(processJobList.get(failedRow));
                        processJobList = processJobList.subList(0, failedRow + 1);
                    }
                }
            }
            knLogger.info(methodName, "QUERY: batch completed, failedJobIdList- ", failedJobIdList);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception", e);
            throw e;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception", e);
            throw KnDbUtil.processException(e, "Failed to job ID - " + e.getMessage(), pttServerId, "", query);
        } catch (Exception e) {
            knLogger.error(methodName, "Un Expected Exception", e);
            throw KnDbUtil.processException(e, "Failed to get jobId - " + e.getMessage(), pttServerId, "", query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT : failedJobIdList ->", failedJobIdList);
    }

    public Map<Integer, Integer> getMaxLocWatchersCount(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMaxLocWatchersCount()";
        knLogger.entry(methodName, "ENTRY : groupIds - ", groupIds == null ? 0 : groupIds.size());
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, Integer> corpGroupInfoDTOS = new HashMap<>();
        boolean ownedTxn = false;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_MAX_LOC_WATCHER_COUNT_ON_GROUPIDS);
           // conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                corpGroupInfoDTOS.put(rs.getInt(1), rs.getInt(2));
            }
            knLogger.exit(methodName, "EXIT : Max lock water count - ", corpGroupInfoDTOS.size());
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while retrieving group basic Information -  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return corpGroupInfoDTOS;
    }

    public void updateSubsOsmAuthorizeInAllGroups(String mdn,String isOSMAuthorize, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsOsmAuthorizeInAllGroups(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn - ", KnGDPRTemplate.mdn(mdn));
        boolean ownedTxn = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_SUBSCRIBER_OSM_BIT_IN_ALL_GROUPS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Executing query - ",query);
            pstmt.setInt(1, Integer.parseInt(isOSMAuthorize));
            pstmt.setString(2, mdn);
            pstmt.execute();
            knLogger.debug( methodName, "Query executed successfully");
            if (ownedTxn) {
                persisterTxn.save();
            }

        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.warn( methodName, "KnDAOException occured while updateIsOSMAuthorize - ", e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.warn( methodName, "Unexpected Exception occured while updateIsOSMAuthorize - ", e);
            throw KnDbUtil.processException(e, "Failed while updateIsOSMAuthorize " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info( methodName, " updated subs OSM authorize in all groups ");
        }
    }

    public String fetchGroupPocHome(int corpGroupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "fetchGroupPocHome(int,KnPersisterTxn)";
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        String pocHome = null;
        try {
            knLogger.debug(methodName, "corpGroupId: ", corpGroupId);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_POCHOME);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Exeuting query - ", query);
            pstmt.setInt(1, corpGroupId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                pocHome = (rs.getString(1));
            }
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the pocHome  " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "pocHome - ", pocHome);
        return pocHome;
    }

    public void updatePocHome(String pocHome, int clusterId, int corpGroupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updatePocHome(String, int, int, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : pocHome - ", pocHome, " , clusterid - ", clusterId, "corpGroupId: ", corpGroupId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_POCHOME);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pocHome);
            pstmt.setInt(2, clusterId);
            pstmt.setInt(3, corpGroupId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating group name- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while  updating group name - " + e);
            throw KnDbUtil.processException(e, "Failed while  updating group name " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void updateGroupMemberCountByGroupId(Map<Integer, Integer> nonZeroMemberGroupIds, KnPersisterTxn persisterTxn) {
        String methodName = "updateGroupMemberCountByGroupId(Map<Integer, Integer>, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : nonZeroMemberGroupIds - ", nonZeroMemberGroupIds);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            query ="UPDATE DG.CORPGROUPMEMBERCOUNT SET MEMBERCOUNT = ?, LASTUPDATETIME = ? WHERE CORPGROUPID = ?";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (Map.Entry<Integer, Integer> entry : nonZeroMemberGroupIds.entrySet()) {
                pstmt.setInt(1, entry.getValue());
                pstmt.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
                pstmt.setInt(3, entry.getKey());
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while updating group member count - " + e);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public List<Integer> getGroupsWithNullPocHome(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupsWithNullPocHome(List<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : groupIds - ", groupIds);
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        List<Integer> nullPocHomeGroupIds = new ArrayList<>();
        try {
            query = "SELECT CORPGROUPID, POCHOME FROM DG.CORPGROUPINFO WHERE CORPGROUPID IN (GROUPIDS)";
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int grpId = rs.getInt(1);
                String pocHome = rs.getString(2);
                if (pocHome == null || pocHome.trim().isEmpty() || pocHome.trim().equals("0")) {
                    nullPocHomeGroupIds.add(grpId);
                }
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while fetching groups with null pocHome - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occurred while fetching groups with null pocHome - ", e);
            throw KnDbUtil.processException(e, "Failed while fetching groups with null pocHome " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : nullPocHomeGroupIds size - ", nullPocHomeGroupIds.size());
        }
        return nullPocHomeGroupIds;
    }
}