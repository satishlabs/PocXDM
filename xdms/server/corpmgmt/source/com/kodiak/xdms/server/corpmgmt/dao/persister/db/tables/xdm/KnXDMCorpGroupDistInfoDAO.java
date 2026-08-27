/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpGroupDistInfoDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        03-03-2011      7.0
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
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnXDMCorpGroupDistInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpGroupDistInfoDAO.class);

    public String pttServerId = null;

    KnXDMCorpGroupDistInfoDAO(String pttServerId) {
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

    private int getSize(Collection dataList) {
        if (dataList != null) {
            return dataList.size();
        } else {
            return 0;
        }
    }

    public Map<Integer, HashMap<String, Collection<String>>> getAllSubscribersGroupList(Collection<String> mdnList,
                                                                                        int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubcriberSublistMemberShipList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Enrty :Input list size passed is - ", getSize(mdnList));
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, HashMap<String, Collection<String>>> groupMemberMap = new HashMap<Integer, HashMap<String, Collection<String>>>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            var mdnListArray = new ArrayList<>(mdnList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                query = queryMapper.getQuery(GET_GROUP_ID_LIST_FOR_SUBSCRIBERS);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(subsList));
                query = KnDbUtil.replaceValInQry(query, corpId);
                knLogger.debug(methodName, "Executing query - ", "'", query, "'");
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    int groupId = rs.getInt(1);
                    String mdn = rs.getString(2).trim();
                    String groupName = null;
                    //multilingual revert changes
                    if (rs.getString(3) != null)
                        groupName = new String(rs.getString(3).getBytes("8859_1"), "UTF-8");
                    HashMap<String, Collection<String>> groupNameMemberListMap = groupMemberMap.get(groupId);
                    Collection<String> memberList = null;
                    if (groupNameMemberListMap != null) {
                        memberList = groupNameMemberListMap.get(groupName);
                    } else {
                        groupNameMemberListMap = new HashMap<>();
                    }
                    if (memberList == null) {
                        memberList = new ArrayList<String>();
                    }
                    memberList.add(mdn);
                    groupNameMemberListMap.put(groupName, memberList);
                    groupMemberMap.put(groupId, groupNameMemberListMap);
                }
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching subscribers group ids lists-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching subscribers group ids lists- ", e);
            throw KnDbUtil.processException(e, "Failed to getAllSubscribersGroupList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT Group Member Map size returned - ", groupMemberMap.size());
        }
        return groupMemberMap;
    }

    public LinkedList<String> getGroupMemberList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMemberList(int KnPersisterTxn)";
        knLogger.debug(methodName, "Enrty :Input DTO passed groupId - ", groupId, ", Txn - ", persisterTxn);
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        LinkedList<String> mdnList = new LinkedList<String>();
        try {

            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_DISTINCT_MEMBERS);
            query = replaceContactWithValue(query, GROUPIDS, String.valueOf(groupId));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                if (!mdnList.contains(mdn)) {
                    mdnList.add(mdn);
                }
            }
            return mdnList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching group members-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching group members- ", e);
            throw KnDbUtil.processException(e, "Failed to getGroupMemberList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT: No of Members found for the group - ", mdnList.size());
        }
    }

    public Map<Integer, Collection<String>> getGroupSubscriberDistList(Collection<Integer> grpIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupSubscriberDistList(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Enrty :Input DTO passed grpIdList - ", grpIdList);

        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Connection conn = null;
        boolean ownedTxn = false;
        Map<Integer, Collection<String>> groupMemberMap = new HashMap<Integer, Collection<String>>();
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
            query = queryMapper.getQuery(SELECT_GROUP_MEMBERS);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(grpIdList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                int grpId = rs.getInt(1);
                Collection<String> mdnList = groupMemberMap.get(grpId);
                if (mdnList == null) {
                    mdnList = new ArrayList<String>();
                }
                String mdn = rs.getString(2).trim();
                mdnList.add(mdn);
                groupMemberMap.put(grpId, mdnList);
            }
            if (groupMemberMap.size() != grpIdList.size()) {
                for (int grpId : grpIdList) {
                    if (!groupMemberMap.containsKey(grpId)) {
                        groupMemberMap.put(grpId, new ArrayList<String>());
                    }
                }
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching group members-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching group members- ", e);
            throw KnDbUtil.processException(e, "Failed to getGroupMemberList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug(methodName, "EXIT");
        }
        return groupMemberMap;
    }

    public void insertIntoCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>> adddedMembersMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertIntoCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>>, KnPersisterTxn)";
        knLogger.debug(methodName, "Enrty :Input DTO passed adddedMembersMap - ", adddedMembersMap);

        PreparedStatement pstmt = null;
        String query = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_GROUP_DIST_INFO);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            boolean dataPresent = KnPersisterConstants.FALSE;
            for (int grpId : adddedMembersMap.keySet()) {
                Collection<String> addedMembers = (adddedMembersMap.get(grpId)).get(KnPersisterConstants.ADDED_MEMBERS);
                if (addedMembers != null && !addedMembers.isEmpty()) {
                    dataPresent = KnPersisterConstants.TRUE;
                    for (String mdn : addedMembers) {
                        pstmt.setInt(1, grpId);
                        pstmt.setString(2, mdn);
                        pstmt.addBatch();
                    }
                }
            }
            if (dataPresent) {
                pstmt.executeBatch();
            }
            knLogger.debug(methodName, "Input DTO passed adddedMembersMap - ", adddedMembersMap, "Exit: Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while inserting group members-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while inserting group members- ", e);
            throw KnDbUtil.processException(e, "Failed to insertIntoCorpGroupDistInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void insertIntoCorpGrpDistInfo(Map<Integer, String> adddedMembersMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertIntoCorpGrpDistInfo(Map<Integer, String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Enrty :Input DTO passed adddedMembersMap - ", adddedMembersMap);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_GROUP_DIST_INFO);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            for (int grpId : adddedMembersMap.keySet()) {
                pstmt.setInt(1, grpId);
                pstmt.setString(2, adddedMembersMap.get(grpId));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Input DTO passed adddedMembersMap - ", adddedMembersMap, "Exit: Query executed successfully.");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while inserting group members- ", e);
            throw KnDbUtil.processException(e, "Failed to insertIntoCorpGroupDistInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteFrmCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>> deletedMembersMap,
                                           KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteFrmCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>>, KnPersisterTxn)";
        knLogger.debug(methodName, "Enrty :Input DTO passed deletedMembersMap - ", KnGDPRTemplate.mapMdnOfMap(deletedMembersMap));

        PreparedStatement pstmt = null;
        String query = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_SUBSCRIBER_DIST_INFO);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            boolean dataPresent = KnPersisterConstants.FALSE;
            for (int grpId : deletedMembersMap.keySet()) {
                Collection<String> deletedMembers = (deletedMembersMap.get(grpId)).get(KnPersisterConstants.DELTED_MEMBERS);
                if (deletedMembers != null && !deletedMembers.isEmpty()) {
                    dataPresent = KnPersisterConstants.TRUE;
                    for (String mdn : deletedMembers) {
                        pstmt.setInt(1, grpId);
                        pstmt.setString(2, mdn);
                        pstmt.setInt(3, grpId);
                        pstmt.setString(4, mdn);
                        pstmt.addBatch();
                    }
                }
            }
            if (dataPresent) {
                int[] resultStatus = pstmt.executeBatch();
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while inserting group members-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while inserting group members- ", e);
            throw KnDbUtil.processException(e, "Failed to insertIntoCorpGroupDistInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getAllSubscribersGroupListForAllCorporate(Collection<String> mdnList,
                                                                                                    KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getAllSubscribersGroupListForAllCorporate(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Enrty :Input passed id - ", mdnList ==null ? mdnList : KnGDPRTemplate.mdnList(mdnList));

        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, Collection<KnCorpGroupMemberDTO>> groupMemberMap = new HashMap<Integer, Collection<KnCorpGroupMemberDTO>>();
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_ID_LIST_FOR_SUBSCRIBERS_FOR_ALL_CORPORATE);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                int groupId = rs.getInt(1);
                String mdn = rs.getString(2).trim();
                Collection<KnCorpGroupMemberDTO> memberList = groupMemberMap.get(groupId);
                if (memberList == null) {
                    memberList = new ArrayList<KnCorpGroupMemberDTO>();
                }
                KnCorpGroupMemberDTO subsc = new KnCorpGroupMemberDTO();
                subsc.setMdn(mdn);
                subsc.setSupervisory(rs.getInt(3));
                subsc.setLocWatcher(rs.getInt(4));
                subsc.setCorpId(rs.getInt(5));
                memberList.add(subsc);
                groupMemberMap.put(groupId, memberList);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching subscribers group ids lists-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching subscribers group ids lists- ", e);
            throw KnDbUtil.processException(e, "Failed to getAllSubscribersGroupList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT Group Member Map size returned - ", groupMemberMap.size());
        }
        return groupMemberMap;
    }

    public Map<Integer, Collection<String>> getAllSubscribersGroupListAsExtContact(Collection<String> mdnList, int
            corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAllSubscribersGroupListAsExtContact(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :Input passed id - ", mdnList ==null ? mdnList : KnGDPRTemplate.mdnList(mdnList), ", corpId - ", corpId);
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, Collection<String>> groupMemberMap = new HashMap<Integer, Collection<String>>();
        try {

            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            if (mdnList.size() == 1) {
                query = queryMapper.getQuery(GET_GROUP_ID_LIST_FOR_EXT_CONTACT_SINGLE);
                for (String mdn : mdnList) {
                    query = KnDbUtil.replaceValInQry(query, mdn);
                }
                query = KnDbUtil.replaceValInQry(query, corpId);
            } else {
                query = queryMapper.getQuery(GET_GROUP_ID_LIST_FOR_EXT_CONTACT);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
                query = KnDbUtil.replaceValInQry(query, corpId);
            }
            knLogger.debug(methodName, "Executing query - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                int groupId = rs.getInt(1);
                String mdn = rs.getString(2);
                Collection<String> memberList = groupMemberMap.get(groupId);
                if (memberList == null) {
                    memberList = new ArrayList<String>();
                }
                memberList.add(mdn);
                groupMemberMap.put(groupId, memberList);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching subscribers group ids lists-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching subscribers group ids lists- ", e);
            throw KnDbUtil.processException(e, "Failed to getAllSubscribersGroupListAsExtContact " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT Group Member Map size returned - ", groupMemberMap.size());
        }
        return groupMemberMap;
    }

    public Collection<String> getSubsNotInDispatchGroupFromMDNList(Collection<String> mdnList, int
            corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsNotInDispatchGroupFromMDNList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :Input passed id - ", mdnList ==null ? mdnList : KnGDPRTemplate.mdnList(mdnList), ", corpId - ", corpId);
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Set<String> nonGroupMembers = new HashSet<String>();
        nonGroupMembers.addAll(mdnList);
        Set<String> groupMembers = new HashSet<String>();
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            ArrayList<String> mdnLists = new ArrayList<String>(mdnList);
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(mdnLists, BULK_UPDATE_SIZE);
            for (Collection<String> subMdnList : compList) {
                query = queryMapper.getQuery(GET_MEMBERS_IN_DISPATCH_GROUPS);
                query = replaceContactWithValue(query, KnPersisterConstants.MDNLIST, formCommaSeperatedIdList(subMdnList));
                query = KnDbUtil.replaceValInQry(query, corpId);
                query = KnDbUtil.replaceValInQry(query, DISPATCH_GROUP_TYPE);
                knLogger.debug(methodName, "Executing query - ", query);
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    groupMembers.add(rs.getString(2).trim());
                }
            }
            for (String mdn : groupMembers) {
                if (nonGroupMembers.contains(mdn)) {
                    nonGroupMembers.remove(mdn);
                }
            }
            knLogger.debug(methodName, "Non Dispatch group members list size - ", nonGroupMembers.size(), "Dispatch group members list size - ", groupMembers.size());
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching members of dispatch group -  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching members of dispatch group - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to fetch members of dispatch group " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT.");
        }
        return nonGroupMembers;
    }

    public Collection<String> getSubsNotInNormalDispatchGroupFromMDNList(Collection<String> mdnList, int
            corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsNotInNormalDispatchGroupFromMDNList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :Input passed id - ", mdnList ==null ? mdnList : KnGDPRTemplate.mdnList(mdnList), ", corpId - ", corpId);
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Set<String> nonGroupMembers = new HashSet<String>();
        nonGroupMembers.addAll(mdnList);
        Set<String> groupMembers = new HashSet<String>();
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            ArrayList<String> mdnLists = new ArrayList<String>(mdnList);
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(mdnLists, 1000);
            for (Collection<String> subMdnList : compList) {
                query = queryMapper.getQuery(GET_MEMBERS_IN_DISPATCH_GROUPS);
                query = replaceContactWithValue(query, KnPersisterConstants.MDNLIST, formCommaSeperatedIdList(subMdnList));
                query = KnDbUtil.replaceValInQry(query, corpId);
                query = KnDbUtil.replaceValInQry(query, DISPATCH_GROUP_TYPE);
                //replace the last ; in the query
                query = query.replace(";", "");
                query = query + " AND IS_LARGEGROUP = 0;";
                knLogger.debug(methodName, "Executing query - ", query);
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    groupMembers.add(rs.getString(2).trim());
                }
            }
            for (String mdn : groupMembers) {
                if (nonGroupMembers.contains(mdn)) {
                    nonGroupMembers.remove(mdn);
                }
            }
            knLogger.debug(methodName, "Non Dispatch group members list size - ", nonGroupMembers.size(), "Dispatch group members list size - ", groupMembers.size());
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching members of dispatch group -  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching members of dispatch group - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to fetch members of dispatch group " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT.");
        }
        return nonGroupMembers;
    }


    public Collection<String> getLocWatcherMdn(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getLocWatcherMdn(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :Input passed id - ", mdnList ==null ? mdnList : KnGDPRTemplate.mdnList(mdnList) , ", corpId - ", corpId);
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Set<String> nonGroupMembers = new HashSet<String>();
        nonGroupMembers.addAll(mdnList);
        Set<String> groupMembers = new HashSet<String>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            ArrayList<String> mdnLists = new ArrayList<String>(mdnList);
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(mdnLists, 100);
            for (Collection<String> subMdnList : compList) {
                query = queryMapper.getQuery(GET_GROUPMEMBER_LOCWATCHER_PRESENT);
                query = replaceContactWithValue(query, KnPersisterConstants.MDNLIST, formCommaSeperatedIdList(subMdnList));
                query = KnDbUtil.replaceValInQry(query, corpId);
                query = KnDbUtil.replaceValInQry(query, IS_LOCWATCHER);
                knLogger.debug(methodName, "Executing query - ", query);
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    groupMembers.add(rs.getString(1).trim());
                }
            }
            knLogger.debug(methodName, "member associated with locWatcher members list size - ", nonGroupMembers.size(),
                    "member associated without locWatcher members list size - ", groupMembers.size());
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching members associated with locWatcher - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to fetch members associated with locWatcher " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT.");
        }
        return groupMembers;
    }


    public Map<Integer, Collection<String>> getAllSubscribersGroupDistForAllCorporate(Collection<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getAllSubscribersGroupDistForAllCorporate(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Enrty :Input passed id - ", mdnList ==null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        boolean ownedTxn = false;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, Collection<String>> groupMemberMap = new HashMap<Integer, Collection<String>>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_DISTRIBUTION_FOR_SUBSCRIBERS_FOR_ALL_CORPORATE);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                int groupId = rs.getInt(1);
                String mdn = rs.getString(2).trim();
                Collection<String> memberList = groupMemberMap.get(groupId);
                if (memberList == null) {
                    memberList = new ArrayList<String>();
                }
                memberList.add(mdn);
                groupMemberMap.put(groupId, memberList);
            }
            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "KnDAOException occured while fetching subscribers group ids lists-  ", e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "Unexpected Exception occured while fetching subscribers group ids lists- ",
                    e);
            throw KnDbUtil.processException(e, "Failed to getAllSubscribersGroupList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT Group Member Map size returned - ", groupMemberMap.size());
        }
        return groupMemberMap;
    }

    public boolean isSubscriberPartOfGroup(int groupId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isSubscriberPartOfGroup(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :Input passed groupId - ", groupId, ", mdn - ", KnGDPRTemplate.mdn(mdn));

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        boolean isSubscPartOfGrp = KnPersisterConstants.FALSE;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(IS_SUBSC_PART_OF_GRP);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            pstmt.setString(2, mdn);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            if (rs.next()) {
                isSubscPartOfGrp = KnPersisterConstants.TRUE;
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching members of dispatch group -  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching members of dispatch group - ", e);
            throw KnDbUtil.processException(e, "Failed to fetch members of dispatch group " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            knLogger.debug(methodName, "EXIT: Subscriber is part of group -isSubscPartOfGrp - ", isSubscPartOfGrp);
        }
        return isSubscPartOfGrp;
    }

    public Map<String, Collection<Integer>> getGroupListForSubs(Collection<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupListForSubs(Collection<String>,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Enrty :Input DTO passed mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList), " readOnly :", readOnly);

        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String, Collection<Integer>> groupMemberMap = new HashMap<String, Collection<Integer>>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSCRIBERS_GROUPS_LIST);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                String mdn = rs.getString(2).trim();
                Collection<Integer> grpList = groupMemberMap.get(mdn);
                if (grpList == null) {
                    grpList = new ArrayList<Integer>();
                }
                int grpid = rs.getInt(1);
                grpList.add(grpid);
                groupMemberMap.put(mdn, grpList);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching group members-  ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching group members- ",
                    e);
            throw KnDbUtil.processException(e, "Failed to getGroupMemberList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT");
        }
        return groupMemberMap;
    }

    /**
     * This method returns the list of group ids where subscriber exist as member.
     *
     * @param subsMdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getSubsGroupIdList(String subsMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsGroupIdList(String, KnPersisterTxn)";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<Integer> grpIdList = new ArrayList<Integer>();
        try {
            knLogger.debug(methodName,"Enter subsMdn:",KnGDPRTemplate.mdn(subsMdn));
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBSCRIBER_GRP_IDS);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, subsMdn);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                grpIdList.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getGroupMemberList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "Query executed successfully grpIdList:",grpIdList);
        return grpIdList;
    }

    /**
     * This method filters out the groupId from a list of groupids where mdn doest not exist as member and return the subset
     * of groupIds from input list where mdn exist as member.
     *
     * @param grpList
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getSubsGroupIdList(List<Integer> grpList, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsGroupIdList(List<Integer>, String, KnPersisterTxn)";
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        List<Integer> grpIdList = new ArrayList<Integer>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBSCRIBERS_GROUPIDS_FROM_GROUPLIST);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(grpList));
            query = KnDbUtil.replaceValInQry(query, mdn);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                grpIdList.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getGroupMemberList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return grpIdList;
    }

    /**
     * This method deleted the corp group distribution entries for mdnList.
     *
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteGrpDistList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteGrpDistList(List<String>,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : mdnList - ", mdnList ==null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_CORP_GROUP_DISTIBUTION_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit:Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteing from corpgroupdistinfo ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * This method is used to get the group mdns group ids its associated to each other
     * This is always one to one mapping
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, List<String>> getSubscriberGroupIds(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubscriberGroupIds(List<String>,boolean,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : mdnList - ", mdnList ==null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        Map<Integer, List<String>> subsGroupMap = new HashMap<Integer, List<String>>();
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            Collection<Collection<String>> collList = KnDbUtil.getCollectionList(mdnList, BULK_UPDATE_SIZE);
            for (Collection<String> gMdns : collList) {
                query = queryMapper.getQuery(GET_SUBSCRIBERS_GROUPID_IDS);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList, query, MDNLIST);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
                pstmt = conn.prepareStatement(query);
                knLogger.debug(methodName, "Executing query - ", query);
                 
                    for(String mdn : mdnList)
                    	pstmt.setString(index++, mdn);
                
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    int grpId = rs.getInt(2);
                    List<String> grpMdnList = null;
                    if(subsGroupMap.get(grpId) != null){
                        grpMdnList = subsGroupMap.get(grpId);
                    } else {
                        grpMdnList = new ArrayList<>();
                    }
                    grpMdnList.add(rs.getString(1).trim());
                    subsGroupMap.put(grpId, grpMdnList);
                }
            }
            knLogger.debug(methodName, "Exit:Query executed successfully");
            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while fetching the subscribers group ids from corpgroupdistinfo ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return subsGroupMap;
    }

    public Collection<String> getSubscribersGroupList(Collection<Integer> groupIds, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubscribersGroupList(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Enrty :Input passed id - ", groupIds);
        boolean ownedTxn = false;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<String> groupMemberList = new ArrayList<>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_GROUP_DISTINCT_MEMBERS);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                groupMemberList.add(rs.getString(1).trim());
            }
            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (Exception e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "Unexpected Exception occured while fetching subscribers member lists- ",
                    e);
            throw KnDbUtil.processException(e, "Failed to getSubscribersGroupList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT Group Member Map size returned - ", groupMemberList.size());
        }
        return groupMemberList;
    }

    /**
     * This method is used to get the corp group id, corp id excluding ABDG group
     * This is always one to one mapping
     *
     * @param corpGroupIdList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, List<String>> getCorpGroupIds(Collection<Integer> corpGroupIdList, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getCorpGroupIds(Collection<Integer>,boolean,KnPersisterTxn )";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<Integer, List<String>> subsGroupMap = new HashMap<Integer, List<String>>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();

            query = queryMapper.getQuery(GET_CORPID_FOR_ABDG_GROUP);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(corpGroupIdList));
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");

            while (rs.next()) {
                int grpId = rs.getInt(2);
                List<String> corpGrpList = null;
                if (subsGroupMap.get(grpId) != null) {
                    corpGrpList = subsGroupMap.get(grpId);
                } else {
                    corpGrpList = new ArrayList<>();
                }
                corpGrpList.add(rs.getString(1).trim());
                subsGroupMap.put(grpId, corpGrpList);
            }
            knLogger.debug(methodName, "Exit:Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching the subscribers group ids from corpgroupdistinfo ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_DIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return subsGroupMap;
    }
}