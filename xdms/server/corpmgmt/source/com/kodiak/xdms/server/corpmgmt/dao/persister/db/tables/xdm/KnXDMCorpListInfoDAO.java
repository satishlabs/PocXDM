/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpListInfoDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        26-01-2011      7.0
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
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSublistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import com.kodiak.logger.KnLogger;

import static com.kodiak.common.resources.KnConstants.IDTYPE;
import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.IDLIST;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnXDMCorpListInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpListInfoDAO.class);

    public String pttServerId = null;

    KnXDMCorpListInfoDAO(String pttServerId) {
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


    public Collection<Integer> getPoCSublistIdInfo(Collection<Integer> sublistid, int corpId,
                                                   KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPoCSublistIdInfo(Collection<Integer>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : SublistId - ", sublistid, ", CorpId - ", corpId);

        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<Integer> pocSublists = new ArrayList<Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(CORP_LIST_ID_FOR_CORP_FROM_CORP_LIST);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String sublistList = formIntegerCommaSeperatedIdList(sublistid);
            query = KnCorpUtil.replaceContactWithValue(query, SUBLISTID, sublistList);
            query = KnDbUtil.replaceValInQry(query, corpId);
            query = KnDbUtil.replaceValInQry(query, SHARED_DISTRBUTION_POLICY);
            query = KnDbUtil.replaceValInQry(query, SHARED_LIST_TYPE);
            knLogger.debug(methodName, "QUERY : Executing - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                pocSublists.add(rs.getInt(1));
            }
            return pocSublists;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving SublistIds - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving SublistIds - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to getPoCSublistIdList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT Fetched the details from DB no of sublists  :  - ", pocSublists.size());
        }
    }

    public Collection<Integer> getCommonContactListRejectForGrp(Collection<Integer> sublistid, int corpId,
                                                                KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCommonContactListRejectForGrp(Collection<Integer>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : SublistId - ", sublistid, ", CorpId - ", corpId);

        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<Integer> pocSublists = new ArrayList<Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(CORP_LIST_ID_FOR_CORP_FROM_CORP_LIST);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String sublistList = formIntegerCommaSeperatedIdList(sublistid);
            query = KnCorpUtil.replaceContactWithValue(query, SUBLISTID, sublistList);
            query = KnDbUtil.replaceValInQry(query, corpId);
            query = KnDbUtil.replaceValInQry(query, 6);
            query = KnDbUtil.replaceValInQry(query, 1);
            knLogger.debug(methodName, "QUERY : Executing - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                pocSublists.add(rs.getInt(1));
            }
            return pocSublists;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving SublistIds - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving SublistIds - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to get SubListIds " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT Fetched the details from DB no of sublists  :  - ", pocSublists.size());
        }
    }

    public int getCommonContactListReject(int sublistid, int corpId,
                                          KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCommonContactListReject(Collection<Integer>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : SublistId - ", sublistid, ", CorpId - ", corpId);
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        int pocSublist = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            //query = queryMapper.getQuery(CORP_LIST_ID_FOR_CORP_FROM_CORP_LIST);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            query = "SELECT CORPLISTID FROM DG.CORPLISTINFO WHERE CORPLISTID = ? AND CORPID = ? AND LISTDISTRIBUTIONPOLICY = ? AND LISTTYPE = ?";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = KnDbUtil.replaceValInQry(query, sublistid);
            query = KnDbUtil.replaceValInQry(query, corpId);
            query = KnDbUtil.replaceValInQry(query, 6);
            query = KnDbUtil.replaceValInQry(query, 1);
            knLogger.debug(methodName, "QUERY : Executing - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                pocSublist = rs.getInt(1);
            }
            return pocSublist;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving SublistIds - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving SublistIds - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to getPoCSublistIdList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT Fetched the sublistId from DB  - ", pocSublist);
        }
    }

    public void insertCorpListInfo(KnCorpSublistDTO corpSublist, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "insertCorpListInfo(KnCorpSublistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : CorpSubList - ", corpSublist);

        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            // 1. Get the base query
            query = queryMapper.getQuery(INSERT_INTO_CORP_LIST_INFO);

            boolean hasHierarchyId= (corpSublist.getHierarchyId() != null && !corpSublist.getHierarchyId().isEmpty());

            if (hasHierarchyId) {
                query = query.replace(") VALUES", ", HIERARCHY_ID) VALUES");
                query = query.substring(0, query.lastIndexOf(")")) + ", ?)";
            }
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpSublist.getSublistId());
            pstmt.setInt(2, corpSublist.getCorpId());
            String sublistName = corpSublist.getSublistName();
            if (null != sublistName) {
                sublistName = new String(sublistName.getBytes("UTF-8"), "8859_1");
            }
            pstmt.setString(3, sublistName);
            pstmt.setInt(4, corpSublist.getDistributionPolicy());
            pstmt.setInt(5, corpSublist.getSublistType());
            pstmt.setLong(6, corpSublist.getETag());
            pstmt.setLong(7, System.currentTimeMillis());

            if (hasHierarchyId) {
                pstmt.setString(8, corpSublist.getHierarchyId());
            }

           knLogger.debug(methodName, "Executing query - '", query, "'");
            pstmt.executeUpdate(); // Use executeUpdate() for INSERT statements
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while inserting Sublist details - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while inserting Sublist ", "details - " + e);
            throw KnDbUtil.processException(e, "Failed to insertCorpListInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Collection<KnCorpSublistDTO> getSubsMappedSublistList(KnIPCorpContactDTO
                                                                         contactDTO, int privateListId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsMappedSublistList(KnIPCorpContactDTO, int, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : privateListId - ", privateListId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpSublistDTO> subscSublitsList = new ArrayList<KnCorpSublistDTO>();
        int index = 1;
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBSC_SUBLIST_DETAILS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            if(null != contactDTO.getMdnList() && !contactDTO.getMdnList().isEmpty()){
                query = "SELECT CORPLISTID, LISTTYPE, LISTDISPLAYNAME, LISTDISTRIBUTIONPOLICY FROM DG.CORPLISTINFO WHERE CORPLISTID IN( SELECT CORPLISTID FROM DG.CORPLISTDISTINFO WHERE RECIPIENTMDN IN (MDNLIST)) AND CORPLISTID != ? AND CORPID=?";
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(contactDTO.getMdnList(),query,"MDNLIST");
                for(String mdn : contactDTO.getMdnList()) {
                    pstmt.setString(index++, mdn);
                }
                pstmt.setInt(index, privateListId);
                pstmt.setInt(index, contactDTO.getCorpId());
            }else {
                String mdn = contactDTO.getMdn();
                pstmt.setString(1, mdn);
                pstmt.setInt(2, privateListId);
                pstmt.setInt(3, contactDTO.getCorpId());
            }
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpSublistDTO sublistDetails = new KnCorpSublistDTO();
                sublistDetails.setSublistId(rs.getInt(1));
                sublistDetails.setSublistType(rs.getInt(2));
                //multilingual revert changes
                if (null != rs.getString(3)) {
                    sublistDetails.setSublistName(new String(rs.getString(3).getBytes("8859_1"), "UTF-8"));
                }
                sublistDetails.setListDistribution(rs.getInt(4));
                subscSublitsList.add(sublistDetails);
            }
            knLogger.debug(methodName, "Returning sublistList of size- ", subscSublitsList.size());
            return subscSublitsList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving Subscriber SublistList - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving Subscriber ", "SublistList - " + e);
            throw KnDbUtil.processException(e, "Failed to getSubsMappedSublistDetails " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.info(methodName, "EXIT: Returning sublistList of size- ", subscSublitsList.size());
        }
    }


    public void modifySublistName(String sublistName, int sublistId, KnPersisterTxn persisterTxn)
            throws KnDAOException {

        String methodName = "modifySublistName(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : sublistName - ", sublistName, " ,sublistId - ", sublistId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_SUBLIST_NAME);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            //multilingual revert change
            if (null != sublistName) {
                sublistName = new String(sublistName.getBytes("UTF-8"), "8859_1");
            }
            pstmt.setString(1, sublistName);
            pstmt.setInt(2, sublistId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeQuery();
            knLogger.debug(methodName, "EXIT : Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating sublist name - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while updating sublist", " name - " + e);
            throw KnDbUtil.processException(e, "Failed to modifySublistName " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public KnCorpSublistDTO getSublistInfo(int sublistId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSublistInfo(int, int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : sublistId= ", sublistId, "corpid= ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpSublistDTO sublistInfo = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBLIST_DETAILS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sublistId);
            pstmt.setInt(2, corpId);

            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                sublistInfo = new KnCorpSublistDTO();
                sublistInfo.setSublistId(sublistId);
                //multilingual revert change
                if (null != rs.getString(1)) {
                    try {
                        sublistInfo.setSublistName(new String(rs.getString(1).getBytes("8859_1"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                sublistInfo.setSublistType(rs.getInt(2));
                sublistInfo.setDistributionPolicy(rs.getInt(3));
                sublistInfo.setETag(rs.getInt(4));
            } else {
                knLogger.error(methodName, "Sublist details not found for SublistId - ", sublistId);
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND,
                        "Sublist not found.", pttServerId, KnDAOSourceTypes.SUBLISTINFO, query);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getSublistInfo ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT - ", sublistInfo);
        return sublistInfo;
    }

    public KnCorpSublistDTO getSublistInfoWithHierarchyId(int sublistId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException {
        String methodName = "getSublistInfoWithHierarchyId(int, int,boolean, KnPersisterTxn,String)";
        knLogger.debug(methodName, "ENTRY : sublistId= ", sublistId, "corpid= ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpSublistDTO sublistInfo = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBLIST_DETAILS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sublistId);
            pstmt.setInt(2, corpId);

            query = query.trim();

            if (hierarchyId != null && !hierarchyId.isEmpty()) {
                knLogger.info(methodName, "Hierarchy id is passed. hierarchyId = ", hierarchyId);
                // Split: Remove semicolon if it exists
                if (query.endsWith(";")) {
                    query = query.substring(0, query.length() - 1);
                }

                // Write: Append the new condition with a leading space
                query += " AND HIERARCHY_ID = ?;";
                pstmt.setString(3, hierarchyId);
            } else {
                knLogger.info(methodName, "No hierarchy filter applied for sublistId: ", sublistId);
            }

            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                sublistInfo = new KnCorpSublistDTO();
                sublistInfo.setSublistId(sublistId);
                //multilingual revert change
                if (null != rs.getString(1)) {
                    try {
                        sublistInfo.setSublistName(new String(rs.getString(1).getBytes("8859_1"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                sublistInfo.setSublistType(rs.getInt(2));
                sublistInfo.setDistributionPolicy(rs.getInt(3));
                sublistInfo.setETag(rs.getInt(4));
            } else {
                knLogger.error(methodName, "Sublist details not found for SublistId - ", sublistId);
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND,
                        "Sublist not found.", pttServerId, KnDAOSourceTypes.SUBLISTINFO, query);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to getSublistInfo ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "EXIT - ", sublistInfo);
        return sublistInfo;
    }

    public boolean isSublistExistInCorporate(int corpId, String sublistName, KnPersisterTxn persisterTxn)
            throws KnDAOException {

        String methodName = "isSublistExistInCorporate(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, ", sublistName - ", sublistName);
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBLIST_COUNT_BY_NAME);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            //multilingual revert change
            if (null != sublistName) {
                sublistName = new String(sublistName.getBytes("UTF-8"), "8859_1");
            }
            pstmt.setString(1, sublistName);
            pstmt.setInt(2, corpId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully");
            return rs.next();
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving sublist count - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving sublist ", "count - " + e);
            throw KnDbUtil.processException(e, "Failed to getSublistInfoByName " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public int getCorpSublistCount(int corpId, int listType, boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException {
        String methodName = "getCorpSublistCount(int, int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, ", listType - ", listType);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int count = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORP_SUBLIST_COUNT);
            boolean hasHierarchy = (hierarchyId != null && !hierarchyId.isEmpty());

            //TODO : Write a new query
            if (hasHierarchy) {
                query = query.trim();
                if (query.endsWith(";")) {
                    query = query.substring(0, query.length() - 1);
                }

                query += " AND HIERARCHY_ID = ?;";
            }
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setInt(2, listType);
            if (hasHierarchy) {
                pstmt.setString(3, hierarchyId);
            }

            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving Sublist count - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving Sublist ", "count - " + e);
            throw KnDbUtil.processException(e, "Failed to getCorpSublistCount " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT : Sublist count - ", count);
        }
    }

    public Collection<KnCorpSublistDTO> selectAllSublist(int corpId, int listType, int nextToken, int fetchSize, boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId)
            throws KnDAOException {

        String methodName = "selectAllSublist(int, int, int, int,boolean,KnPersisterTxn,String)";
        knLogger.info(methodName, "ENTRY : corpId - ", corpId, "listType - ", listType, "nextToken -", nextToken, "fetchSize: ", fetchSize);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpSublistDTO> sublistList = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            if (0 <= nextToken && 0 < fetchSize) {
                if(hierarchyId==null){
                    query = queryMapper.getQuery(GET_CORP_ALL_SUBLIST_PAGINATED);

                    int startIndex = KnCorpUtil.getStartIndex(fetchSize, nextToken);
                    int endIndex = KnCorpUtil.getEndIndex(fetchSize, nextToken);

                    pstmt = conn.prepareStatement(query);

                    pstmt.setInt(1, startIndex);
                    pstmt.setInt(2, endIndex);
                    pstmt.setInt(3, corpId);
                    pstmt.setInt(4, listType);

                    knLogger.debug(methodName, "Executing query with startIndex,endIndex:  ", query, startIndex, endIndex);
                }else{
                    //TODO: Write a new query in sql.xml file
                    query = "SELECT ROWS ? to ? " +
                            "CLI.CORPLISTID, CLI.LISTDISPLAYNAME, CLI.LISTDISTRIBUTIONPOLICY, CLI.ETAG, COUNT(CLM.MEMBERMDN) " +
                            "FROM DG.CORPLISTINFO CLI " +
                            "LEFT JOIN DG.CORPLISTMEMBER CLM ON CLI.CORPLISTID = CLM.CORPLISTID " +
                            "WHERE CLI.CORPID = ? AND CLI.LISTTYPE = ? AND CLI.HIERARCHY_ID = ? " +
                            "GROUP BY CLI.CORPLISTID, CLI.LISTDISPLAYNAME, CLI.LISTDISTRIBUTIONPOLICY, CLI.ETAG " +
                            "ORDER BY CLI.LISTDISPLAYNAME";

                    int startIndex = KnCorpUtil.getStartIndex(fetchSize, nextToken);
                    int endIndex = KnCorpUtil.getEndIndex(fetchSize, nextToken);

                    pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, startIndex);
                    pstmt.setInt(2, endIndex);
                    pstmt.setInt(3, corpId);
                    pstmt.setInt(4, listType);
                    pstmt.setString(5, hierarchyId);
                    knLogger.debug(methodName, "Executing query with startIndex,endIndex,hierarchyId :  ", query, startIndex, endIndex,hierarchyId);
                }
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query Executed successfully");
            } else {
                if(hierarchyId==null){
                    query = queryMapper.getQuery(GET_CORP_ALL_SUBLIST);
                    pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, corpId);
                    pstmt.setInt(2, listType);
                    knLogger.debug(methodName, "Executing query:- ", query);
                }else {
                    //TODO: Write a new query in sql.xml file
                    // 1. Updated query string with HIERARCHY_ID in the WHERE clause
                    query = "SELECT CLI.CORPLISTID, CLI.LISTDISPLAYNAME, CLI.LISTDISTRIBUTIONPOLICY, CLI.ETAG, COUNT(CLM.MEMBERMDN) " +
                            "FROM DG.CORPLISTINFO CLI " +
                            "LEFT JOIN DG.CORPLISTMEMBER CLM ON CLI.CORPLISTID = CLM.CORPLISTID " +
                            "WHERE CLI.CORPID = ? AND CLI.LISTTYPE = ? AND CLI.HIERARCHY_ID = ? " +
                            "GROUP BY CLI.CORPLISTID, CLI.LISTDISPLAYNAME, CLI.LISTDISTRIBUTIONPOLICY, CLI.ETAG";

                    conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                    pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, corpId);
                    pstmt.setInt(2, listType);
                    pstmt.setString(3, hierarchyId);
                    knLogger.debug(methodName, "Executing query for corpId: ", corpId, " and hierarchyId: ", hierarchyId);
                }
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query Executed successfully");
            }
            knLogger.debug(methodName, "Query Executed successfully");
            while (rs.next()) {
                KnCorpSublistDTO sublistDTO = new KnCorpSublistDTO();
                sublistDTO.setSublistType(listType);
                sublistDTO.setSublistId(rs.getInt(1));
                //multilingual revert change
                if (null != rs.getString(2)) {
                    sublistDTO.setSublistName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                }
                sublistDTO.setDistributionPolicy(rs.getInt(3));
                sublistDTO.setETag(rs.getInt(4));
                int count = rs.getInt(5);
                sublistDTO.setMemberCount(count);
                sublistList.add(sublistDTO);
            }
            knLogger.debug(methodName, "SublistList Details:  ", sublistList, "Sublist Size: ", sublistList.size());
            return sublistList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving sublistList - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while retrieving ", "sublistList - ", e);
            throw KnDbUtil.processException(e, "Failed to selectAllSublist " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT.SublistList size - ", sublistList.size());
        }
    }

    public void deleteSublistInfo(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteSublistInfo(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : SublistId - ", sublistId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_SUBLIST_INFO);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, sublistId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeUpdate();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting Sublist - ",
                    sublistId + ", " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while deleting Sublist - ",
                    sublistId + ", " + e);
            throw KnDbUtil.processException(e, "Failed to deleteSublistInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteSublistInfo(List<Integer> sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteSublistInfo(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : SublistId - ", sublistId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int index = 1;
        try {
            query = "DELETE FROM DG.CORPLISTINFO WHERE CORPLISTID IN (SUBLISTID)";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks("SUBLISTID",sublistId,query);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (Integer val : sublistId) {
                pstmt.setInt(index++, val);
            }
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeUpdate();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting Sublist - ",
                    sublistId + ", " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while deleting Sublist - ",
                    sublistId + ", " + e);
            throw KnDbUtil.processException(e, "Failed to deleteSublistInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<Integer, Integer> fetchAndUpdateSublistEtag(Collection<Integer> sublistIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "fetchAndUpdateSublistEtag(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : sublistIdList - ", sublistIdList);
        Connection connection;
        Connection readOnlyConn;
        Statement stmt = null;
        PreparedStatement updatePstmt = null;
        String query = null;
        String updateQuery = null;
        ResultSet rs = null;
        List<Integer> sublistIds = new ArrayList<Integer>(sublistIdList);
        Collections.sort(sublistIds);
        knLogger.debug(methodName, "sublist Ids after sorted", sublistIds);
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBLIST_ETAG);
            query = KnCorpUtil.replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublistIdList));
            readOnlyConn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            stmt = readOnlyConn.createStatement();
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully");
            Map<Integer, Integer> eTagMap = new HashMap<Integer, Integer>();
            while (rs.next()) {
                eTagMap.put(rs.getInt(1), rs.getInt(2));
            }
            connection = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            updateQuery = queryMapper.getQuery(UPDATE_SUBLIST_ETAG);
            knLogger.debug(methodName, "Executing query - ", query);
            updatePstmt = connection.prepareStatement(updateQuery);
            if (!eTagMap.isEmpty()) {
                for (Integer sublistId : sublistIds) {
//                    Integer sublistId = entry.getKey();
                    if (eTagMap.get(sublistId) != null) {
                        int etag = eTagMap.get(sublistId);
                        updatePstmt.setInt(1, ++etag);
                        updatePstmt.setLong(2, System.currentTimeMillis());
                        updatePstmt.setInt(3, sublistId);
                        updatePstmt.addBatch();
                        eTagMap.put(sublistId, etag);
                    }
                }
            }
            knLogger.debug(methodName, "Executing query - ", updateQuery);
            updatePstmt.executeBatch();
            knLogger.debug(methodName, "EXIT: Query executed successfully, Etag", eTagMap);
            return eTagMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetchAndUpdateSublistEtag - ", sublistIdList + ", " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetchAndUpdateSublistEtag - ", sublistIdList + ", " + e);
            throw KnDbUtil.processException(e, "Failed to fetchAndUpdateSublistEtag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            KnDbUtil.closeStatement(updatePstmt);
        }
    }

    public int getSublistCountByNameExcludingCurrentSublist(int corpId, String sublistName,
                                                            int sublistId, KnPersisterTxn persisterTxn)
            throws KnDAOException {

        String methodName = "getSublistCountByNameExcludingCurrentSublist(int, String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId - ", corpId, ", sublistName - ", sublistName, " , sublistid - ", sublistId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int count = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_SUBLIST_COUNT_BY_NAME_EXCLUDING_CURRENT_SUBLIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            //multilingual revert change
            if (null != sublistName) {
                sublistName = new String(sublistName.getBytes("UTF-8"), "8859_1");
            }
            pstmt.setString(1, sublistName);
            pstmt.setInt(2, corpId);
            pstmt.setInt(3, sublistId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving sublist count by name ", "excluding current sublist - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving sublist count by name ", "excluding current sublist " + e);
            throw KnDbUtil.processException(e, "Failed to getSublistInfoByName " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "EXIT : Sublist count - ", count);
        }
    }

    public Collection<Integer> getCorpSublistIdList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpSublistIdList(KnIPCorpInfoDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpInfoDTO - ", corpInfoDTO);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<Integer> sublistLists = new ArrayList<Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_CORPORATE_SUBLIST_LIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpInfoDTO.getCorpId());
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                sublistLists.add(rs.getInt(1));
            }
            return sublistLists;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving sublist list for the corporate - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving sublist list for the corporate"
                    + e);
            throw KnDbUtil.processException(e, "Failed to getCorpSublistIdList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT : Sublist List size is - ", sublistLists.size());
        }
    }

    public void deleteAllSublistInfo(Collection<Integer> sublistIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllSublistInfo(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : SublistId - ", sublistIdsList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_ALL_SUBLISTS_INFO);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublistIdsList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeUpdate();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting Sublist list - ", sublistIdsList + ", " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while deleting Sublist list - ", sublistIdsList + ", " + e);
            throw KnDbUtil.processException(e, "Failed to deleteAllSublitInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public ArrayList<Integer> getSharedSublistFromList(Collection<Integer> sublistLists, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSharedSublistFromList(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : sublistLists - ", sublistLists);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SHARED_SUBLIST_LIST);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublistLists));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = stmt.executeQuery(query);
            ArrayList<Integer> sharedSublists = new ArrayList<Integer>();
            while (rs.next()) {
                sharedSublists.add(rs.getInt(1));
            }
            knLogger.debug(methodName, "Exit: Query executed successfully, Resturning response shared sublist list - ", sharedSublists);
            return sharedSublists;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving shared Sublist list - ", sublistLists + ", " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while  retrieving shared Sublist list - ", sublistLists + ", " + e);
            throw KnDbUtil.processException(e, "Failed to getSharedSublistFromList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
    }

    public ArrayList<Integer> getNonSharedSublistFromList(Collection<Integer> sublistLists, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getNonSharedSublistFromList(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : sublistLists - ", sublistLists);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_NON_SHARED_SUBLIST_LIST);
            query = replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublistLists));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = stmt.executeQuery(query);
            ArrayList<Integer> sharedSublists = new ArrayList<Integer>();
            while (rs.next()) {
                sharedSublists.add(rs.getInt(1));
            }
            knLogger.debug(methodName, "Exit: Query executed successfully, Resturning response shared sublist list - ", sharedSublists);
            return sharedSublists;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving non shared Sublist list - ", sublistLists + ", " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while  retrieving non shared Sublist list - ", sublistLists + ", " + e);
            throw KnDbUtil.processException(e, "Failed to getSharedSublistFromList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
    }

    public void updateSublistEtag(int sublistId, long etag, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSublistEtag(int, long, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : SublistId - ", sublistId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_SUBLIST_ETAG);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(3, sublistId);
            pstmt.setInt(1, Integer.valueOf(String.valueOf(etag)));
            pstmt.setLong(2, System.currentTimeMillis());
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.execute();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating etag for  Sublist - ", sublistId + ", " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while updating etag for  Sublist - ", sublistId + ", " + e);
            throw KnDbUtil.processException(e, "Failed to deleteAllSublitInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * This method deleted sublist entries from corplistinfo table
     *
     * @param sublistIds
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteSublistInfoList(List<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteSublistInfoList(List<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : sublistIds - ", sublistIds);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_SUBLIST_INFO);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (int id : sublistIds) {
                pstmt.setInt(1, id);
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to deleteSublistInfo in batch", pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    /**
     * Method to retrieve the sublist details from list of input sublists based on list type and corpId.
     *
     * @param sublistIds
     * @param corpId
     * @param sublistType
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, KnCorpSublistDTO> filterSublists(List<Integer> sublistIds, int corpId, int sublistType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "filterSublists(List<Integer>, int, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : sublistIds - ", sublistIds, " corpId", corpId, "sublistType", sublistType);
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, KnCorpSublistDTO> sublistDTOMap;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_FILTERED_SUBLIST_LIST);
            query = KnDbUtil.replaceValInQry(query, sublistType);
            query = KnDbUtil.replaceValInQry(query, corpId);
            List<List<Integer>> sublistIdsList = KnDbUtil.getLists(sublistIds, 1000);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            sublistDTOMap = new HashMap<>();
            for (List<Integer> listIds : sublistIdsList) {
                getSublistDetails(listIds, conn, sublistType, sublistDTOMap, query);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed " + e, pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "Exit: sublistDTOMap - ", sublistDTOMap);
        return sublistDTOMap;
    }

    /**
     * Method to retrieve the sublist details from list of input sublists based on list type and corpId and setting the
     * result in sublistDTOMap request argument.
     *
     * @param sublists
     * @param conn
     * @param listType
     * @param sublistDTOMap
     * @param query
     * @throws SQLException
     */
    private void getSublistDetails(List<Integer> sublists, Connection conn, int
            listType, Map<Integer, KnCorpSublistDTO> sublistDTOMap, String query) throws SQLException {
        String methodName = "getSublistDetails()";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String newQuery = KnCorpUtil.replaceContactWithValue(query, SUBLISTID, formIntegerCommaSeperatedIdList(sublists));
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", newQuery);
            rs = stmt.executeQuery(newQuery);
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpSublistDTO sublistDTO = new KnCorpSublistDTO();
                int listId = rs.getInt(1);
                sublistDTO.setSublistId(listId);
                //multilingual revert change
                if (null != rs.getString(2)) {
                    try {
                        sublistDTO.setSublistName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, "sublistDTOMap", "error in encoding to UTF-8", e);
                    }
                }
                sublistDTO.setSublistType(listType);
                sublistDTOMap.put(listId, sublistDTO);
            }
            knLogger.debug(methodName, "sublistDTOMap", sublistDTOMap);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
    }


    /**
     * This method is used to get the sublist details with the said name passed as a parameter
     *
     * @param corpId
     * @param subPrefix
     * @param persisterTxn
     * @return
     */
    public KnCorpSublistDTO getSublistDetailsByName(int corpId, String subPrefix, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSublistDetailsByName(corpId,subPrefix,persisterTxn)";
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpSublistDTO sublistDTO = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_SUBLIST_DETAILS_BY_NAME);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.prepareStatement(query);
            //multilingual revert changes
            try {
                if (subPrefix != null)
                    subPrefix = new String(subPrefix.getBytes("UTF-8"), "8859_1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            stmt.setString(1, subPrefix);
            stmt.setInt(2, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            if (rs.next()) {
                sublistDTO = new KnCorpSublistDTO();
                int listId = rs.getInt(1);
                sublistDTO.setSublistId(listId);
                sublistDTO.setETag(rs.getLong(2));
                knLogger.debug(methodName, "sublistDTO details", sublistDTO);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed " + e, pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return sublistDTO;
    }

    /**
     * @param sublistid
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer,Integer> getAllTypePoCSublistIdInfo(Collection<Integer> sublistid, int corpId,
                                                          KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAllTypePoCSublistIdInfo(Collection<Integer>, int, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : SublistId - ", sublistid, ", CorpId - ", corpId);

        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        //Collection<Integer> pocSublists = new ArrayList<Integer>();
        Map<Integer,Integer> subListIdInfo = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORP_LIST_ID_FOR_SHARED_AND_UPM_CORPLIST);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            String sublistList = formIntegerCommaSeperatedIdList(sublistid);
            query = KnCorpUtil.replaceContactWithValue(query, SUBLISTID, sublistList);
            query = KnDbUtil.replaceValInQry(query, corpId);
            query = KnDbUtil.replaceValInQry(query, SHARED_LIST_TYPE);
            knLogger.debug(methodName, "QUERY : Executing - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                subListIdInfo.put(rs.getInt(1),rs.getInt(2));
            }
            return subListIdInfo;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving SublistIds - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving SublistIds - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to getPoCSublistIdList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.info(methodName, "EXIT Fetched the details from DB no of sublists  :  - ", subListIdInfo.size());
        }
    }


    public void insertCorpListInfo(List<KnCorpSublistDTO> corpSublist, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertCorpListInfo()";
        knLogger.debug(methodName, "ENTRY : CorpSubList - ", corpSublist);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_INTO_CORP_LIST_INFO);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (KnCorpSublistDTO list : corpSublist) {
                pstmt.setInt(1, list.getSublistId());
                pstmt.setInt(2, list.getCorpId());
                String sublistName = list.getSublistName();
                if (null != sublistName) {
                    try {
                        sublistName = new String(list.getSublistName().getBytes("UTF-8"), "8859_1");
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error(methodName, "UnsupportedEncodingException - ", e);
                    }
                }
                pstmt.setString(3, sublistName);
                pstmt.setInt(4, list.getDistributionPolicy());
                pstmt.setInt(5, list.getSublistType());
                pstmt.setLong(6, list.getETag());
                pstmt.setLong(7, System.currentTimeMillis());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Batch Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed  while inserting into the group info table " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public int getCorpIdFromCorpListInfo(int sublistid, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpIdFromCorpListInfo(Collection<Integer>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : SublistId - ", sublistid);

        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        int corpId = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CORP_ID_FROM_CORPLISTINFO);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = KnDbUtil.replaceValInQry(query, sublistid);
            knLogger.debug(methodName, "QUERY : Executing - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                corpId = rs.getInt(1);
            }
            return corpId;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving SublistIds - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving SublistIds - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to getCorpIdFromCorpListInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT Fetched the corpId of sublist  :  - ", corpId);
        }
    }

    public List<String> getCommonContactListForMdns(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCommonContactListForMdns(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn ", KnGDPRTemplate.mdn(mdn));
        List<String> listOfContactMdns = new ArrayList<>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int corpId = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_ALL_COMMON_CONTACTLIST_MDNS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "QUERY : Executing - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2, KnConstants.DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                listOfContactMdns.add((rs.getString(1)).trim());
            }
            return listOfContactMdns;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving contact mdns - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving contact mdns - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to getCorpIdFromCorpListInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.debug(methodName, "EXIT Fetched the contact mdn details  - ", KnGDPRTemplate.mdnList(listOfContactMdns));
        }
    }

    public List<String> getAllSublistContactMdns(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAllSublistContactMdns(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn ", KnGDPRTemplate.mdn(mdn));
        List<String> listOfContactMdns = new ArrayList<>();
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        int corpId = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_LIST_OF_ALL_SUBLIST_MEMBERS);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = KnDbUtil.replaceValInQry(query, mdn);
            knLogger.debug(methodName, "QUERY : Executing - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                listOfContactMdns.add(rs.getString(1).trim());
            }
            return listOfContactMdns;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving SublistIds - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving SublistIds - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to getCorpIdFromCorpListInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT Fetched the corpId of sublist  :  - ", KnGDPRTemplate.mdnList(listOfContactMdns));
        }
    }

    /**
     * This method deletes sublists entries from corplistinfo table using corpId
     * @param corpId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllSubListsOfCorporate(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllSubListsOfCorporate(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :  corpId- ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_ALL_SUBLISTS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeUpdate();
            knLogger.debug(methodName, "EXIT: Query executed successfully");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting Sublists with corpId- ",
                    corpId + ", " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while deleting Sublists with corpId- ",
                    corpId + ", " + e);
            throw KnDbUtil.processException(e, "Failed to deleteSublistInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public boolean isCommonContactList(Collection<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isCommonContactList(Collection<Integer>, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : SublistId - ", sublistIds);
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<Integer> pocSublists = new ArrayList<Integer>();
        boolean isCommonContactList = Boolean.FALSE;
        try {
            query = "SELECT CORPLISTID FROM DG.CORPLISTINFO WHERE CORPLISTID IN (SUBLISTIDS) AND LISTDISTRIBUTIONPOLICY = 6 AND LISTTYPE=1";
            String subListIds = formIntegerCommaSeperatedIdList(sublistIds);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = KnCorpUtil.replaceContactWithValue(query, "SUBLISTIDS", subListIds);
            knLogger.debug(methodName, "QUERY : Executing - ", query);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                pocSublists.add(rs.getInt("CORPLISTID"));
            }
            if (!pocSublists.isEmpty()) {
                isCommonContactList = Boolean.TRUE;
            }
            return isCommonContactList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving isCommonContactList - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving isCommonContactList - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to get isCommonContactList " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.info(methodName, "EXIT isCommonContactList:- ", isCommonContactList);
        }
    }

    public boolean isSublistValidRequest(List<Integer> sublistIdList, Map<String, Object> customParams, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "isSublistValidRequest(Collection<String>,customParams KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : sublistIdList in isSublistValidRequest  ", sublistIdList);
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        boolean isValid = false;
        int count = 0;
        int idType = 0;
        Collection<String> idList = null;
        try {
            if (null == customParams) {
                return isValid;
            }
            List<String> stringList = sublistIdList.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            idType = Integer.parseInt((String) customParams.get(IDTYPE));
            idList = (Collection<String>) customParams.get(IDLIST);
            var subsLists = KnGeneralUtil.splitList(stringList, BULK_UPDATE_SIZE);
            if (idType == 1) {
                query = "SELECT count(1) FROM DG.SUBSCRIBER_ADDLINFO WHERE ban_id IN (IDLIST) AND mdn IN (SELECT MEMBERMDN FROM DG.CORPLISTMEMBER WHERE CORPLISTID IN (SUBLISTID));";
            } else {
                query = "SELECT count(1) FROM DG.SUBSCRIBER_ADDLINFO WHERE fan_id IN (IDLIST) AND mdn IN (SELECT MEMBERMDN FROM DG.CORPLISTMEMBER WHERE CORPLISTID IN (SUBLISTID));";
            }
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            for(Collection<String> sublistId : subsLists){
                query = replaceContactWithValue(query, IDLIST, formCommaSeperatedQuesMarks(idList));
                query = replaceContactWithValue(query, "SUBLISTID", formCommaSeperatedQuesMarks(sublistId));
                knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for (String id : idList) {
                    pStmt.setString(index++, id);
                }
                for (Integer subList : sublistIdList) {
                    pStmt.setInt(index++, subList);
                }
                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    count = rs.getInt(1);
                }
                if (count>0) {
                    isValid = true;
                }
                knLogger.debug(methodName, "checking count ", count," idList size ",idList.size()," isValid ",isValid);
            }
        } catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed to retrieve Sublist info ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        return isValid;
    }

    public Map<String,Integer> getPaginatedContatInfo(List<String> contactMdns, int startIndex, int endIndex, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPaginatedContatInfo(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn ", KnGDPRTemplate.mdnList(contactMdns));
        Map<String, Integer> contactMdnMap = new TreeMap<>();
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PAGINATED_OWNER_MDN_INFO);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = KnCorpUtil.replaceContactWithValue(query, "CONTACTMDNLIST", formCommaSeperatedIdList(contactMdns));
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, startIndex);
            stmt.setInt(2, endIndex);
            knLogger.debug(methodName, "QUERY : Executing - ", query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                contactMdnMap.put(rs.getString(1).trim(), rs.getInt(2));
            }
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
            knLogger.info(methodName, "EXIT : contactMdnMap ", contactMdnMap.size());
            return contactMdnMap;

        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "KnDAOException occured while retrieving getPaginatedContatInfo - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving getPaginatedContatInfo - ",
                    e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to getCorpIdFromCorpListInfo " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(stmt);
        }
    }

    public Integer getUniqueContactListCount(String contactMdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUniqueContactListCount(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : mdn ", KnGDPRTemplate.mdn(contactMdns));
        Set<String> ownerMdns = new HashSet<>();
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_CONTACT_MDN_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            stmt = conn.prepareStatement(query);
            stmt.setString(1, contactMdns);
            knLogger.debug(methodName, "QUERY : Executing - ", query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                ownerMdns.add(rs.getString(1).trim());
            }
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
            return ownerMdns.size();

        } catch (KnDAOException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "KnDAOException occured while retrieving getUniqueContactListCount - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving getUniqueContactListCount - ",
                    e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to getUniqueContactListCount " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(stmt);
        }
    }


    public Collection<KnCorpSublistDTO> getSubsMappedSublistList(Map<String, KnIPCorpContactDTO> contactDTOMap, int corpListId, KnPersisterTxn persisterTxn,int corpId) throws KnConnectionException, KnDBPersistenceException,KnDAOException {
        String methodName = "getSubsMappedSublistList(KnIPCorpContactDTO, int, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : privateListId - ", corpListId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpSublistDTO> subscSublitsList = new ArrayList<KnCorpSublistDTO>();
        List<String> mdnList = new ArrayList<>(contactDTOMap.keySet());
        int index = 1;
        try {
            query = "SELECT CORPLISTID, LISTTYPE, LISTDISPLAYNAME, LISTDISTRIBUTIONPOLICY FROM DG.CORPLISTINFO WHERE CORPLISTID IN(SELECT CORPLISTID FROM DG.CORPLISTDISTINFO WHERE RECIPIENTMDN IN (MDNLIST)) AND CORPLISTID != ? AND CORPID=?";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pstmt.setString(index++, mdn);
            }
            pstmt.setInt(2, corpListId);
            pstmt.setInt(3,corpId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                KnCorpSublistDTO sublistDetails = new KnCorpSublistDTO();
                sublistDetails.setSublistId(rs.getInt(1));
                sublistDetails.setSublistType(rs.getInt(2));
                //multilingual revert changes
                if (null != rs.getString(3)) {
                    sublistDetails.setSublistName(new String(rs.getString(3).getBytes("8859_1"), "UTF-8"));
                }
                sublistDetails.setListDistribution(rs.getInt(4));
                subscSublitsList.add(sublistDetails);
            }
            knLogger.debug(methodName, "Returning sublistList of size- ", subscSublitsList.size());
            return subscSublitsList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving Subscriber SublistList - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving Subscriber ", "SublistList - " + e);
            throw KnDbUtil.processException(e, "Failed to getSubsMappedSublistDetails " + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_LIST_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            knLogger.info(methodName, "EXIT: Returning sublistList of size- ", subscSublitsList.size());
        }
    }
}