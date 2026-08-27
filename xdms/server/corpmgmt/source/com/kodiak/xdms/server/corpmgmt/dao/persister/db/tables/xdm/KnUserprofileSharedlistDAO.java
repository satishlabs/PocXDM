/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;


import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnUserProfileAssignedDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnUserprofileSharedlistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;

import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes.XDM_CORP_GROUPPROFILE_SHAREDLIST;
import static com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes.XDM_POCSUBSCRINFO;

public class KnUserprofileSharedlistDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnUserprofileSharedlistDAO.class);
    public String pttServerId = null;

    private static final String TABLENAME = "DG.USERPROFILEMDNMAP";
    private static final String SHAREDCORPID = "SHAREDCORPID";
    private static final String SHAREDCORPIDS = "SHAREDCORPIDS";
    private static final String USERPROFILEID = "USERPROFILEID";
    private static final String OWNEDCORPID = "OWNEDCORPID";
    private static final String PROFILEIDS = "PROFILEIDS";
    private static final String GROUPID = "GROUPIDS";
    private static final String GETCLIENTTYPE = "SELECT MDN , CLIENT_TYPE FROM DG.POCSUBSCRINFO WHERE MDN IN ";


    public KnUserprofileSharedlistDAO(String pttServerId) {
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

    public void insert(List<KnUserprofileSharedlistDTO> userprofileSharedlist,KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert()";
        knLogger.info(methodName, "Entry UserprofileSharedlist : ", userprofileSharedlist);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(INSERT_USERPROFILE_SHARED);
            pstmt = conn.prepareStatement(query);
            for (KnUserprofileSharedlistDTO upmSharedCorp : userprofileSharedlist) {
                pstmt.setString(1, upmSharedCorp.getUserprofileId());
                pstmt.setInt(2, upmSharedCorp.getSharedCorpId());
                pstmt.setInt(3, upmSharedCorp.getOwnerCorpId());
                pstmt.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing - ", query);
            pstmt.executeBatch();
            knLogger.info(methodName, " Exit");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert " + e,
                    pttServerId, KnDAOSourceTypes.USERPROFILE_SHAREDLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
    public List<Integer> getSharedCorpIdFromUserProfielId(String userProfileId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSharedCorpIdFromUserProfielId(String,boolean,KnPersisterTxn)";
        knLogger.info(methodName, "Entry with input-: ", userProfileId);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        boolean ownedTxn = false;
        List<Integer> sharedCorpIds = new ArrayList<>();
        try {
           // conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_USERPROFILE_SHARED_CORPID);
            knLogger.debug(methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userProfileId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sharedCorpIds.add(rs.getInt(SHAREDCORPID));
            }
            knLogger.info(methodName, "sharedCorpIds are :", sharedCorpIds);
            knLogger.debug(methodName, "Query executed successfully. ", sharedCorpIds, XDM_CORP_GROUPPROFILE_SHAREDLIST);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch shared corp Group profile data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }

        return sharedCorpIds;
    }

    public Map<String,Integer> getUserProfileOwnerinfo(ArrayList<String> profileIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        {
            String methodName = "getUserProfileOwnerinfo()";
            knLogger.info(methodName, " profileIds : ", profileIds, " readOnly :",readOnly);
            Connection conn = null;
            PreparedStatement stmt = null;
            String query = null;
            ResultSet rs = null;
            int index = 1;
            boolean ownedTxn = false;
            Map<String, Integer> profileOwnerInfo = new HashMap<>();
            try {
                if (persisterTxn != null) {
                    knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                    conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
                    knLogger.debug(methodName, "if block");
                } else {
                    conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                    ownedTxn = true;
                    knLogger.debug(methodName, "else block");
                    knLogger.debug(methodName, "conn", conn);
                }
                KnQueryMapper queryMapper = KnQueryMapper.getInstance();
                query = queryMapper.getQuery(GET_USERPROFILE_OWNER_CORPID);
                //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(profileIds,query,"PROFILEIDS");
                stmt = conn.prepareStatement(query);
                knLogger.debug(methodName, "Executing query - ", query);
                for(String id : profileIds){
                    stmt.setString(index++,id);
                }
                rs = stmt.executeQuery();
                while(rs.next()){
                    profileOwnerInfo.put(rs.getString(1).trim(), rs.getInt(2));
                }
            } catch (SQLException e) {
                throw KnDbUtil.processException(e, "Failed ",
                        pttServerId, XDM_POCSUBSCRINFO, query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(stmt);
                if (ownedTxn) {
                    KnDbUtil.closeConnection(conn);
                }
            }
            knLogger.debug(methodName, "EXit: getUserProfileOwnerinfo : ",profileOwnerInfo);
            knLogger.info(methodName, "Exit: getUserProfileOwnerinfo size: ", profileOwnerInfo.size());
            return profileOwnerInfo;
        }
    }

    public List<String> getXdmUserProfileIdsBySharedCorpId(String sharedCorpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getXdmUserProfileIdsBySharedCorpId(String,boolean, KnPersisterTxn)";
        knLogger.info(methodName, "Entry with input-: ", sharedCorpId);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        List<String> userProfileIds = new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_USERPROFILE_ID_FROM_SHARED_CORPID);
            knLogger.debug(methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, sharedCorpId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                userProfileIds.add(rs.getString(USERPROFILEID));
            }
            knLogger.info(methodName, "userProfileIds are :", userProfileIds);
            knLogger.debug(methodName, "Query executed successfully. ", userProfileIds, XDM_CORP_GROUPPROFILE_SHAREDLIST);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch shared corp Group profile data" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_PROFILE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }

        return userProfileIds;
    }

    public List<String> getMdnBySharedUserProfileCorpIds(int ownCorpId,List<Integer> sharedCorpids, KnPersisterTxn persisterTxn) throws KnDAOException {
        {
            String methodName = "getMdnBySharedUserProfileCorpIds()";
            knLogger.debug(methodName, " ownCorpId : ", ownCorpId," sharedCorpids :",sharedCorpids );
            Connection conn;
            PreparedStatement pstmt = null;
            String query = null;
            ResultSet rs = null;
            int index = 1;
            List<String> mdnList=new ArrayList<>();
            try {
                 if(sharedCorpids == null) {
                    knLogger.debug(methodName, "No shared corpid is passed. ",sharedCorpids);
                    return mdnList;
                }
                KnQueryMapper queryMapper = KnQueryMapper.getInstance();
                query = queryMapper.getQuery(GET_SHARED_MEMBERS_BY_SHARED_USERPROFILE);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarksAnyType(sharedCorpids,query,SHAREDCORPIDS);
                pstmt = conn.prepareStatement(query);
                knLogger.debug(methodName, "Executing query - ", query);
                for(Integer corpId : sharedCorpids){
                    pstmt.setInt(index++,corpId);
                }
                pstmt.setInt(index++,ownCorpId);
                rs = pstmt.executeQuery();
                while(rs.next()){
                    mdnList.add(rs.getString(1).trim());
                }
            } catch (SQLException e) {
                throw KnDbUtil.processException(e, "Failed ",
                        pttServerId, XDM_POCSUBSCRINFO, query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closePrepareStmt(pstmt);
            }
            knLogger.debug(methodName, "EXit: mdnList : ", KnGDPRTemplate.mdnList(mdnList));
            return mdnList;
        }
    }

    public List<KnUserprofileSharedlistDTO> getUserProfileSharedListByUpmId(String userProfileId
            , KnPersisterTxn persisterTxn) throws KnDAOException {
        {
            String methodName = "getUserProfileSharedListByUpmId()";
            knLogger.debug(methodName, " userProfileId : ", userProfileId);
            Connection conn = null;
            PreparedStatement pstmt = null;
            String query = null;
            ResultSet rs = null;
            List<KnUserprofileSharedlistDTO> upmSharedList=new ArrayList<>();
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
                //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                KnQueryMapper queryMapper = KnQueryMapper.getInstance();
                query = queryMapper.getQuery(GET_USERPROFILE_SHARED_BY_UPMID);
                knLogger.debug(methodName, "query -", query);
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, userProfileId);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    upmSharedList.add(new KnUserprofileSharedlistDTO(rs.getString(1),
                            rs.getInt(2),
                            rs.getInt(3)));
                }
            } catch (SQLException e) {
                throw KnDbUtil.processException(e, "Failed ",
                        pttServerId, KnDAOSourceTypes.USERPROFILE_SHAREDLIST, query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(pstmt);
                if (ownedTxn) {
                    KnDbUtil.closeConnection(conn);
                }
            }
            knLogger.debug(methodName, "EXit: upmSharedList : ",upmSharedList);
            return upmSharedList;
        }
    }

    public void deleteCorpInfoFromUserProfileSharedList(String userProfileId,List<String> corpIds,KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "deleteCorpInfoFromUserProfileSharedList()";
        knLogger.debug(methodName, "Entry UserprofileId ", userProfileId , "CorpIds to be removed ",corpIds);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(DELETE_CORP_USERPROFILE_SHARED);
            pstmt = conn.prepareStatement(query);
            for (String corpId : corpIds) {
                pstmt.setString(1, userProfileId);
                pstmt.setInt(2, Integer.parseInt(corpId));
                pstmt.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing - ", query);
            pstmt.executeBatch();
            knLogger.debug( methodName, " Exit");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert " + e,
                    pttServerId, KnDAOSourceTypes.USERPROFILE_SHAREDLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public List<KnUserProfileAssignedDTO> getUserProfileSubsCount(Collection<String> userProfileIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserProfileSubsCount(Collection<String>, boolean, KnPersisterTxn)";
        knLogger.info(methodName, "Entry with input-: ", userProfileIds);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        List<KnUserProfileAssignedDTO> result = new ArrayList<>();
        int index = 1;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = KnQueryMapper.getInstance().getQuery(GET_USER_PROFILE_ASSIGNED_TO_SUBS);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarksAnyType(userProfileIds,query,PROFILEIDS);
            pstmt = conn.prepareStatement(query);
            for(String userProfileId : userProfileIds){
                pstmt.setString(index++,userProfileId);
            }
            knLogger.debug(methodName, "Executing query - ", query);

            rs = pstmt.executeQuery();

            String userProfileId = null;
            while (rs.next()) {
                KnUserProfileAssignedDTO userProfileAssignedDTO = new KnUserProfileAssignedDTO();
                userProfileId = rs.getString(1);
                userProfileAssignedDTO.setUserProfileId(userProfileId);
                userProfileAssignedDTO.setCorpId(rs.getInt(2));
                userProfileAssignedDTO.setMdn(rs.getString(3));
                result.add(userProfileAssignedDTO);
            }
            knLogger.info(methodName, "result are :", result);
            knLogger.debug(methodName, "Query executed successfully. ", result, XDM_POCSUBSCRINFO);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch shared corp Group profile data" + e,
                    pttServerId, XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }

        return result;
    }

    public Set<String> getProfileMdnList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileMdnList(int groupId, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "Entry with input-: ", groupId);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        Set<String> profileMdnSet = new HashSet<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = KnQueryMapper.getInstance().getQuery(GET_PROFILEMDNS_BASED_ON_GROUPID);
            knLogger.debug(methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileMdnSet.add(rs.getString(1).trim());
            }
            knLogger.info(methodName, "result are :", rs);
            knLogger.debug(methodName, "Query executed successfully. ", rs, XDM_POCSUBSCRINFO);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch profile Mdns" + e,
                    pttServerId, XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return profileMdnSet;
    }

    public int getGroupMemCountAndList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMemCountAndList(int groupId, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "Entry with input-: ", groupId);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = KnQueryMapper.getInstance().getQuery(GET_MEMBERS_COUNT);
            knLogger.debug(methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
            knLogger.info(methodName, "result are :", rs);
            knLogger.debug(methodName, "Query executed successfully. ", rs, XDM_POCSUBSCRINFO);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch profile Mdns" + e,
                    pttServerId, XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return count;
    }

    public Map<Integer, Integer> getGroupMemCount(Set<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMemCount(Set<Integer>,KnPersisterTxn )";
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Integer> grpIdMemCountMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GRP_MEMBERS_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Exeuting query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                grpIdMemCountMap.put(rs.getInt(1), rs.getInt(2));
            }
            knLogger.info(methodName, "EXIT. groupSize - ", grpIdMemCountMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group info  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_COUNT, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return grpIdMemCountMap;
    }

    public Map<Integer, Integer> getGroupAllMemCount(Set<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupAllMemCount(Set<Integer>,KnPersisterTxn )";
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Integer> grpIdMemCountMap = new HashMap<>();
        try {
            query = "SELECT CORPGROUPID,COUNT(MEMBERMDN) FROM DG.CORPGROUPMEMBERLIST WHERE CORPGROUPID IN (GROUPIDS) GROUP BY CORPGROUPID";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                grpIdMemCountMap.put(rs.getInt(1), rs.getInt(2));
            }
            for (int groupId : groupIds) {
                grpIdMemCountMap.putIfAbsent(groupId, 0);
            }
            knLogger.info(methodName, "EXIT. groupSize - ", grpIdMemCountMap.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group info  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_COUNT, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return grpIdMemCountMap;
    }

    public Map<String, Integer> getSubsDetails(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsDetails(int groupId, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "Entry with input-: ", mdnList);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        Map<String, Integer> subcriberDetailsResponse = new HashMap<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            StringBuilder strBuffer = new StringBuilder(200);
            strBuffer.append(GETCLIENTTYPE).append("(");
            strBuffer.append(KnGeneralUtil.formCommaSeperatedIdList(mdnList)).append(");");
            query = strBuffer.toString();
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                subcriberDetailsResponse.put(rs.getString(1).trim(), rs.getInt(2));
            }
            knLogger.info(methodName, "result are :", subcriberDetailsResponse);
            knLogger.debug(methodName, "Query executed successfully. ", rs, XDM_POCSUBSCRINFO);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch profile Mdns" + e,
                    pttServerId, XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return subcriberDetailsResponse;
    }

    public int getUserPfofileIdCount(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserPfofileIdCount(int groupId, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "Entry with input-: ", groupId);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = KnQueryMapper.getInstance().getQuery(GET_USER_PROFILE_ID_COUNT);
            knLogger.debug(methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
            knLogger.info(methodName, "result are :", rs);
            knLogger.debug(methodName, "Query executed successfully. ", rs, XDM_POCSUBSCRINFO);
            knLogger.debug(methodName, "count ", count);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch profile Mdns" + e,
                    pttServerId, XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return count;
    }

    public Map<Integer, Integer> getProfileIdCount(Set<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileIdCount(List<Integer> groupIds, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "Entry with input-: ", groupIds);
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Integer> profileIdCountMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PROFILE_ID_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            pstmt = conn.prepareStatement(query);
            knLogger.debug( methodName, "Exeuting query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                profileIdCountMap.put(rs.getInt(1), rs.getInt(2));
            }
            knLogger.info(methodName, "EXIT. groupSize - ", profileIdCountMap.size());
        }catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while retrieving the group info  ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_COUNT, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return profileIdCountMap;
    }

    public int getMemCountBasedOnLocWatcher(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMemCountBasedOnLocWatcher(int groupId, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "Entry with input-: ", groupId);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        int count = 0;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = KnQueryMapper.getInstance().getQuery(GET_MEMMDN_COUNT);
            knLogger.debug(methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
            knLogger.info(methodName, "result are :", rs);
            knLogger.debug(methodName, "Query executed successfully. ", rs, XDM_POCSUBSCRINFO);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch profile Mdns" + e,
                    pttServerId, XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return count;
    }

    public Set<Integer> getGroupIdBasedOnMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupIdBasedOnMdn(String mdn, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "Entry with input-: ", mdn);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        Set<Integer> groupIds = new HashSet<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = KnQueryMapper.getInstance().getQuery(GET_GROUPID_BASED_ON_MDN);
            knLogger.debug(methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groupIds.add(rs.getInt(1));
            }
            knLogger.info(methodName, "result are :", rs);
            knLogger.debug(methodName, "Query executed successfully. ", rs, XDM_POCSUBSCRINFO);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch profile Mdns" + e,
                    pttServerId, XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return groupIds;
    }

    public int getMdnCountBasedOnUPMID(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMdnCountBasedOnUPMID(List<Integer> groupIds, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "Entry with input-: ", groupIds);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        int count = 0;
        int index = 1;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_MDN_COUNT_BASED_ON_UPM_ID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarksAnyType(groupIds, query, GROUPID);
            knLogger.debug(methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            for (int groupId : groupIds) {
                pstmt.setInt(index++, groupId);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
            knLogger.info(methodName, "result are :", rs);
            knLogger.debug(methodName, "Query executed successfully. ", rs, XDM_POCSUBSCRINFO);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch profile Mdns" + e,
                    pttServerId, XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return count;
    }

    public Set<String> getProfileMdns(Set<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileMdns(Set<Integer> groupIds, KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "Entry with input-: ", groupIds);
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn = null;
        int index = 1;
        Set<String> profileMdnSet = new HashSet<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PROFILEMDNS_BASED_ON_GROUPIDS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarksAnyType(groupIds, query, GROUPID);
            knLogger.debug(methodName, "query -", query);
            pstmt = conn.prepareStatement(query);
            for (int groupId : groupIds) {
                pstmt.setInt(index++, groupId);
            }
            rs = pstmt.executeQuery();

            while (rs.next()) {
                profileMdnSet.add(rs.getString(1).trim());
            }
            knLogger.info(methodName, "result are :", rs);
            knLogger.debug(methodName, "Query executed successfully. ", rs, XDM_POCSUBSCRINFO);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch profile Mdns" + e,
                    pttServerId, XDM_POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return profileMdnSet;
    }
}
