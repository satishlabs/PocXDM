/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpAddlTGInfoDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.getAddlTGDocumentURI;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GROUPIDS;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.MDNLIST;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formIntegerCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMCorpAddlTalkGrpDAO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             April 26, 2018                9.0
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

public class KnXDMCorpAddlTalkGrpDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpAddlTalkGrpDAO.class);

    private String pttServerId;

    KnXDMCorpAddlTalkGrpDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return null;
    }

    public Collection<KnCorpAddlTGInfoDTO> getSubsAddlTGList(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsAddlTGList(KnIPTalkGroupDTO mdn,boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn));
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        Collection<KnCorpAddlTGInfoDTO> addlTalkGroupInfoDTOS = new ArrayList<>();
        boolean ownedTxn = false;
        ResultSet rs = null;
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
            query = queryMapper.getQuery(KnPersisterConstants.GET_SUBS_ADDL_TALK_GRP);
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing Query- ", query);
            rs = pstmt.executeQuery();
            KnCorpAddlTGInfoDTO addlTalkGroupInfoDTO = null;
            while (rs.next()) {
                addlTalkGroupInfoDTO = new KnCorpAddlTGInfoDTO();
                addlTalkGroupInfoDTO.setMdn(rs.getString(1).trim());
                addlTalkGroupInfoDTO.setGroupId(rs.getInt(2));
                addlTalkGroupInfoDTO.setZoneId(rs.getInt(3));
                addlTalkGroupInfoDTO.setZoneName(rs.getString(4));
                addlTalkGroupInfoDTO.setChannelId(rs.getInt(5));
                addlTalkGroupInfoDTOS.add(addlTalkGroupInfoDTO);
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getSubsAddlTGList", pttServerId, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        }finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        knLogger.debug(methodName,"addlTalkGroupInfoDTOS :",addlTalkGroupInfoDTOS);
        return addlTalkGroupInfoDTOS;
    }

    public Collection<KnCorpAddlTGInfoDTO> getSubsAddlTGList(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsAddlTGList(Collection<String> mdnList, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdnList", mdnList==null ? mdnList:KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        Statement stmt = null;
        String query = null;
        Collection<KnCorpAddlTGInfoDTO> addlTalkGroupInfoDTOS = new ArrayList<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_SUBS_ADDL_TALK_GRP_LIST);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing Query- ", query);
            ResultSet rs = stmt.executeQuery(query);
            KnCorpAddlTGInfoDTO addlTalkGroupInfoDTO = null;
            while (rs.next()) {
                addlTalkGroupInfoDTO = new KnCorpAddlTGInfoDTO();
                addlTalkGroupInfoDTO.setMdn(rs.getString(1).trim());
                addlTalkGroupInfoDTO.setGroupId(rs.getInt(2));
                addlTalkGroupInfoDTO.setZoneId(rs.getInt(3));
                addlTalkGroupInfoDTO.setZoneName(rs.getString(4));
                addlTalkGroupInfoDTO.setChannelId(rs.getInt(5));
                addlTalkGroupInfoDTOS.add(addlTalkGroupInfoDTO);
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.", addlTalkGroupInfoDTOS.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getSubsAddlTGList", pttServerId, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeStatement(stmt);
        }
        return addlTalkGroupInfoDTOS;
    }

    public void deleteSubsAddlTGList(Collection<KnCorpAddlTGInfoDTO> corpAddlTGInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSubsAddlTGList(Collection<KnCorpAddlTGInfoDTO>, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "corpAddlTGInfoDTOS", corpAddlTGInfoDTOS);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_SUBS_ADDL_TALK_GRP);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (KnCorpAddlTGInfoDTO corpAddlTGInfoDTO : corpAddlTGInfoDTOS) {
                pstmt.setString(1, corpAddlTGInfoDTO.getMdn());
                pstmt.setInt(2, corpAddlTGInfoDTO.getGroupId());
                pstmt.setInt(3, corpAddlTGInfoDTO.getZoneId());
                pstmt.setInt(4, corpAddlTGInfoDTO.getChannelId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete ATG from DG.SUBSCRPTTRADIOTGLIST table " + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void insertSubsAddlTGList(Collection<KnCorpAddlTGInfoDTO> corpAddlTGInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertSubsAddlTGList(Collection<KnCorpAddlTGInfoDTO>, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : corpAddlTGInfoDTOS - ", corpAddlTGInfoDTOS);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.INSERT_SUBS_ADDL_TALK_GRP);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (KnCorpAddlTGInfoDTO corpAddlTGInfoDTO : corpAddlTGInfoDTOS) {
                pstmt.setString(1, corpAddlTGInfoDTO.getMdn());
                pstmt.setInt(2, corpAddlTGInfoDTO.getGroupId());
                pstmt.setInt(3, corpAddlTGInfoDTO.getZoneId());
                pstmt.setString(4, corpAddlTGInfoDTO.getZoneName());
                pstmt.setInt(5, corpAddlTGInfoDTO.getChannelId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insert into DG.SUBSCRPTTRADIOTGLIST table " + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteSubsAddlTalkGroup(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSubsAddlTalkGroup(String, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList) , mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_ADDL_TALK_GRP);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete ATG from DG.SUBSCRPTTRADIOTGLIST table " + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteSubsAddlTalkGroupDoc(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSubsAddlTalkGroupDoc(String, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdnList", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_ADDL_TG_USER_DOC);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete ATG from DG.SUBSCRPTTRADIOTGLIST table " + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, KnOPDocChgDTO> insertOrUpdateAddlTGInfo(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertOrUpdateAddlTGInfo(Collection<String>,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY Point : mdnList size - ", mdnList.size());
        var mdnListArray = new ArrayList<>(mdnList);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        Map<String, KnOPDocChgDTO> addlTgMap = new HashMap<>();
        for (var subsList : subsLists) {
            var tempMap = insertOrUpdateAddlTGInfoInBatch(subsList, persisterTxn);
            if (null != tempMap && !tempMap.isEmpty()) {
                addlTgMap.putAll(tempMap);
            }
        }
        knLogger.info(methodName, "Exit insertOrUpdateAddlTGInfo :", addlTgMap.size());
        return addlTgMap;
    }

    public Map<String, KnOPDocChgDTO> insertOrUpdateAddlTGInfoInBatch(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertOrUpdateAddlTGInfoInBatch(Collection<String>,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn;
        boolean ownedTxn = false;
        int index = 1;
        Map<String, KnOPDocChgDTO> addlTgMap = new HashMap<String, KnOPDocChgDTO>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.SELECT_ADDL_TG_USER_DOC);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList, query, "MDNLIST");
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query 1 - ", query);
            for(String mdn : mdnList)
            	pstmt.setString(index++, mdn);
            rs = pstmt.executeQuery();
            Map<String, Long> tgMap = new HashMap<>();
            while(rs.next()){
                tgMap.put(rs.getString(1).trim(), rs.getLong(2));
            }
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            rs = null; pstmt = null;
            if(!tgMap.isEmpty()){
                query = queryMapper.getQuery(KnPersisterConstants.UPDATE_ADDL_TG_USER_DOC);
                pstmt = conn.prepareStatement(query);
                for (String mdn : mdnList) {
                    long etag = System.currentTimeMillis();
                    pstmt.setLong(1, etag);
                    pstmt.setString(2, mdn);
                    pstmt.addBatch();
                    knLogger.debug(methodName, "Executing query 2- ", query);
                    KnOPDocChgDTO docChngDto = new KnOPDocChgDTO();
                    if(tgMap.get(mdn) != null) docChngDto.setPrevEtag(String.valueOf(tgMap.get(mdn)));
                    docChngDto.setDocType(1);
                    docChngDto.setDocUri(getAddlTGDocumentURI(mdn));
                    docChngDto.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                    docChngDto.setNewEtag(String.valueOf(etag));
                    addlTgMap.put(mdn, docChngDto);
                }
            } else {
                query = queryMapper.getQuery(KnPersisterConstants.INSERT_ADDL_TG_USER_DOC);
                pstmt = conn.prepareStatement(query);
                for (String mdn : mdnList) {
                    long etag = System.currentTimeMillis();
                    pstmt.setString(1, mdn);
                    pstmt.setLong(2, etag);
                    pstmt.addBatch();
                    knLogger.debug(methodName, "Executing query 3- ", query);
                    KnOPDocChgDTO docChngDto = new KnOPDocChgDTO();
                    docChngDto.setDocType(1);
                    docChngDto.setDocUri(getAddlTGDocumentURI(mdn));
                    docChngDto.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.ADD.value());
                    docChngDto.setNewEtag(String.valueOf(etag));
                    addlTgMap.put(mdn, docChngDto);
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "EXit: Query executed successfully");
            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (SQLException e) {
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            knLogger.error(methodName, "KnDAOException occured while deleting group - ", e);
            throw KnDbUtil.processException(e, "Failed to insertOrUpdate AddlTG Doc to DG.SUBSCRPTTRADIOGROUPLISTDOC table " + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        }finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);

        }
        return addlTgMap;
    }

    public Map<Integer, Collection<Integer>> getZoneChannelMap(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getZoneChannelMap(KnPersisterTxn persisterTxn)";
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Collection<Integer>> zoneChannelMap = new HashMap<>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_SUBS_ADDL_ZONE_CHANNEL);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing Query- ", query);
            rs = pstmt.executeQuery();
            Collection<Integer> channelList = null;
            while (rs.next()) {
                int zone = rs.getInt(1);
                int channel = rs.getInt(2);
                if(zoneChannelMap.get(zone) != null){
                    channelList = zoneChannelMap.get(zone);
                    channelList.add(channel);
                } else {
                    channelList = new ArrayList<>();
                    channelList.add(channel);
                    zoneChannelMap.put(zone, channelList);
                }
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getZoneChannelMap", pttServerId, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return zoneChannelMap;
    }

    public Collection<KnCorpAddlTGInfoDTO> getSubsAddlDetails(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsAddlDetails(Collection<Integer>, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, groupIds, groupIds);
        Connection conn = null;
        boolean ownedTxn = false;
        Statement stmt = null;
        String query = null;
        Collection<KnCorpAddlTGInfoDTO> addlTalkGroupInfoDTOS = new ArrayList<>();
        ResultSet rs = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_SUBS_ADDL_TALK_GRP_BY_GROUPIDS);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
            if (persisterTxn != null) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing Query- ", query);
            rs = stmt.executeQuery(query);
            KnCorpAddlTGInfoDTO addlTalkGroupInfoDTO = null;
            while (rs.next()) {
                addlTalkGroupInfoDTO = new KnCorpAddlTGInfoDTO();
                addlTalkGroupInfoDTO.setMdn(rs.getString(1).trim());
                addlTalkGroupInfoDTO.setGroupId(rs.getInt(2));
                addlTalkGroupInfoDTO.setZoneId(rs.getInt(3));
                addlTalkGroupInfoDTO.setZoneName(rs.getString(4));
                addlTalkGroupInfoDTO.setChannelId(rs.getInt(5));
                addlTalkGroupInfoDTOS.add(addlTalkGroupInfoDTO);
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getSubsAddlTGList", pttServerId, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return addlTalkGroupInfoDTOS;
    }

    public Map<String, KnOPDocChgDTO> deleteFromSubsAddInfoInfo(Collection<String> mdnList, Map<String, KnOPDocChgDTO> addlTGMap,
                                                                KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteFromSubsAddInfoInfo(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        PreparedStatement pstmt = null;
        String query = null;
        if(addlTGMap == null){
            addlTGMap = new HashMap<String, KnOPDocChgDTO>();
        }
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_ADDL_TG_USER_DOC);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (String mdn : mdnList) {
                pstmt.setString(1, mdn);
                KnOPDocChgDTO docChngDto = new KnOPDocChgDTO();
                docChngDto.setDocType(1);
                docChngDto.setDocUri(getAddlTGDocumentURI(mdn));
                docChngDto.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
                addlTGMap.put(mdn, docChngDto);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete subsAddlitionalDoc Doc from DG.SUBSCRPTTRADIOGROUPLISTDOC table " + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
        return addlTGMap;
    }

    public Map<String, Long> getSubsAddlEtagMap(Collection<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubsAddlEtagMap(Collection<String>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Connection conn;
        Map<String, Long> addlTgMap = new HashMap<String, Long>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.SELECT_ADDL_TG_USER_DOC);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(mdnList));
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query 1 - ", query);
            rs = stmt.executeQuery(query);
            while(rs.next()){
                addlTgMap.put(rs.getString(1).trim(), rs.getLong(2));
            }
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insertOrUpdate AddlTG Doc to DG.SUBSCRPTTRADIOGROUPLISTDOC table " + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        return addlTgMap;
    }

    public void insertSubsAddlTGDoc(Map<String, Long> subsEtagMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertSubsAddlTGDoc(Map<String, Long>,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : subsEtagMap - ", subsEtagMap);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.INSERT_ADDL_TG_USER_DOC);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            if (subsEtagMap != null) {
                for (Map.Entry<String, Long> etagMap : subsEtagMap.entrySet()) {
                    pstmt.setString(1, etagMap.getKey());
                    pstmt.setLong(2, etagMap.getValue());
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insertOrUpdate AddlTG Doc to DG.SUBSCRPTTRADIOGROUPLISTDOC table " + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteAllTGListGrpIds(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteAllTGListGrpIds()";
        knLogger.debug(methodName, "groupIds", groupIds);
        PreparedStatement pstmt = null;
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_ADDLTGLIST_BY_GROUPIDS);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (int id : groupIds) {
                pstmt.setInt(1, id);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete ATG from DG.SUBSCRPTTRADIOTGLIST table " + e,
                    pttServerId, KnDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }


}
