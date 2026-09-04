/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     5/11/13         7.7.0
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
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpTalkGrpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnTalkGrpScanMode;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GROUPIDS;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.MDNLIST;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnXDMCorpCampGrpDOA implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpCampGrpDOA.class);

    private String pttServerId;

    KnXDMCorpCampGrpDOA(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return null; // To change body of implemented methods use File |
        // Settings | File Templates.
    }

    /**
     * This method is to prepare the corporate group doc uri
     *
     * @param mdn
     * @param groupId
     * @return
     */
    private String getGroupDocUri(String mdn, int groupId) {
        String methodName = "getGroupDocUri(String ,int, String)";
        knLogger.debug(methodName, "generating group uri for mdn - ", KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(70);
        strBuffer.append(KnConstants.CORP_GROUP_URI);
        strBuffer.append(mdn);
        strBuffer.append("/").append(groupId).append(".xml");
        knLogger.debug(methodName, "generated ENTRY uri of Notification for mdn - ", KnGDPRTemplate.mdn(mdn) + " is - " + strBuffer.toString());
        return strBuffer.toString();
    }

    public void cleanUpSubsCampedGrps(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteTGSEntries(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdn ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_SUBSCRIBERS_CAMPEDGROUP);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleting entries from DG.CAMPEDGROUPINFO", pttServerId,
                    KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void cleanUpSubsCampedGrps(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteTGSEntries(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdn ", KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int index = 1;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "DELETE FROM DG.CAMPEDGROUPINFO WHERE MDN = ?";
            //query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            pstmt = conn.prepareStatement(query);
            for(String mdn : mdnList) {
            	pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleting entries from DG.CAMPEDGROUPINFO", pttServerId,
                    KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void deleteCampedGrps(String mdn, List<Integer> grpIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteTGSEntries(String, int KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdn ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_CAMPED_GROUP_FOR_MDNLIST);
            //query = replaceContactWithValue(query, KnPersisterConstants.GROUPIDS, formIntegerCommaSeperatedIdList(grpIdList));
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            for(Integer grpId : grpIdList) {
                pstmt.setInt(1, grpId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleting entries from DG.CAMPEDGROUPINFO", pttServerId,
                    KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public void createSubsCampedGrps(String mdn, List<KnCorpTalkGrpInfoDTO> grpList, int campedBy, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSubsCampedGrps(String mdn, Map<Integer,KnCorpTalkGrpInfoDTO> grpList, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn), "grpList", grpList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.CREATE_SUBS_CAMPED_GRP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (KnCorpTalkGrpInfoDTO corpTalkGrpInfoDTO : grpList) {
                // MDN, GROUPURI, GROUPID, CAMPED_BY, PRIORITY
                pstmt.setString(1, mdn);
                pstmt.setString(2, getGroupDocUri(mdn, corpTalkGrpInfoDTO.getGroupId()));
                pstmt.setInt(3, corpTalkGrpInfoDTO.getGroupId());
                pstmt.setInt(4, campedBy);
                pstmt.setInt(5, corpTalkGrpInfoDTO.getPriority());
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing Query- ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while create Subscriber Camped Groups", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void createSubsTalkGrpScanMode(String mdn, Integer mode, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSubsTalkGrpScanMode(String mdn, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn), "mode", mode);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.CREATE_SUBS_TALK_GRP_SCAN_MODE);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            // MDN, TGSC_MODE,ETAG
            pstmt.setString(1, mdn);
            pstmt.setInt(2, mode);
            pstmt.setInt(3, 1);
            knLogger.debug(methodName, "Executing Query- ", query);
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public int deleteSubsTalkGrpScanMode(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSubsTalkGrpScanMode(String mdn, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int deletedRowCount = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_SUBS_TALK_GRP_SCAN_MODE);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing Query- ", query);
            deletedRowCount = pstmt.executeUpdate();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
        return deletedRowCount;
    }

    public int deleteSubsTalkGrpScanMode(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSubsTalkGrpScanMode(List<String> mdnList, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int deletedRowCount = 0;
        try {
            query = "DELETE FROM DG.XDMS_TGSC WHERE MDN = ?";
            //query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            for(String mdn : mdnList) {
            	pstmt.setString(1, mdn);
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing Query- ", query);
            deletedRowCount = pstmt.executeBatch().length;
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return deletedRowCount;
    }

    public List<KnCorpTGSPersistDTO> getSubsCampedGrp(KnIPTalkGroupDTO ipTalkGroupDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsCampedGrp(String mdn, boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ipTalkGroupDTO", ipTalkGroupDTO);
        List<KnCorpTGSPersistDTO> corpTGSPersistDTOs = new ArrayList<KnCorpTGSPersistDTO>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean ownedTxn = false;
        String query = null;
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
            query = queryMapper.getQuery(KnPersisterConstants.GET_SUBS_CAMPED_GRP);
           // conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, ipTalkGroupDTO.getMdn());
            knLogger.debug(methodName, "Executing Query- ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                // GROUPID, PRIORITY
                KnCorpTGSPersistDTO corpTGSPersistDTO = new KnCorpTGSPersistDTO();
                corpTGSPersistDTO.setGroupId(rs.getInt(1));
                corpTGSPersistDTO.setPriority(rs.getInt(2));
                corpTGSPersistDTOs.add(corpTGSPersistDTO);
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getSubsCampedGrp", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        }finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return corpTGSPersistDTOs;
    }

    public Map<Integer, KnCorpGroupInfoPersistDTO> getGroupDisplayNameCorpIdMapInfo(List<Integer> groupIdLst, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupDisplayNameCorpIdMapInfo(List<Integer>,boolean, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : groupIdsList - ", groupIdLst.size());
        Connection conn = null;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        Map<Integer, KnCorpGroupInfoPersistDTO> groupNameMap = new HashMap<>(groupIdLst.size());
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_GROUPDISPLAY_NAME);
            query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIdLst));
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Exeuting query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                //multilingual revert change
                KnCorpGroupInfoPersistDTO persistDTO = new KnCorpGroupInfoPersistDTO();
                if (rs.getString(2) != null)
                    try {
                        persistDTO.setGroupDisplayName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error("UnsupportedEncodingException while parsing group name ", e);
                    }
                persistDTO.setCorpId(rs.getInt(3));
                groupNameMap.put(rs.getInt(1), persistDTO);
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching groupName CorpId info ", pttServerId, KnDAOSourceTypes.GRPINFO, query);
        }finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
        return groupNameMap;
    }

    public void deleteAllCampedGroups(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllCampedGroups(KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_ALL_CAMPED_GROUPS);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing Query- ", query);
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteAllCampedGroups", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateSubsTalkGrpScanMode(String mdn, Integer mode, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsTalkGrpScanMode(String mdn, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn), "mode", mode);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.UPDATE_SUBS_TALK_GRP_SCAN_MODE);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, mode);
            pstmt.setString(2, mdn);
            knLogger.debug(methodName, "Executing Query- ", query);
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void updateSubsTalkGrpScanEtag(String mdn, Integer etag, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsTalkGrpScanEtag(String mdn, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn), "etag", etag);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.UPDATE_SUBS_TALK_GRP_SCAN_ETAG);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, etag);
            pstmt.setString(2, mdn);
            knLogger.debug(methodName, "Executing Query- ", query);
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public KnTalkGrpScanMode getSubsTalkGrpScanMode(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsTalkGrpScanMode(String mdn, boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn));
        KnTalkGrpScanMode grpScanMode = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId,  false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(pttServerId, true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_SUBS_TALK_GRP_SCAN_MODE);
            //conn = persisterTxn.getDBConnection(pttServerId, readOnly);
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing Query- ", query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                grpScanMode = new KnTalkGrpScanMode();
                grpScanMode.setMode(rs.getInt(1));
                grpScanMode.setEtag(rs.getInt(2));
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
            return grpScanMode;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        }finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {

                KnDbUtil.closeConnection(conn);
            }
        }
    }

    public List<String> getMdnsForCampedGrp(Integer grpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMdnsForCampedGrp(Integer deletedGrpId, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "grpId", grpId);
        List<String> mdns = new ArrayList<String>();
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_MDNS_FOR_CAMPED_GRP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, grpId);
            knLogger.debug(methodName, "Executing Query- ", query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                mdns.add(rs.getString(1).trim());
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
            return mdns;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }

    }

    public int getCampedGrpCount(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMdnsForCampedGrp(Integer deletedGrpId, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn));
        int count = 0;
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_CAMPED_GRP_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, MDNLIST, "'" + mdn + "'");
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing Query- ", query);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(2);
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
            return count;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, Integer> getCampedGrpCount(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMdnsForCampedGrp(Set<String> mdnList, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdnList", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        Map<String, Integer> subscCampGrpCount = new HashMap<String, Integer>();
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(new ArrayList<String>(mdnList), 1000);
            for (Collection<String> mdns : compList) {
            	int index = 1;
                query = queryMapper.getQuery(KnPersisterConstants.GET_CAMPED_GRP_COUNT);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdns, query, MDNLIST);
                pstmt = conn.prepareStatement(query);
                knLogger.debug(methodName, "Executing Query- ", query);
                
                for(String mdn : mdns) {
                	pstmt.setString(index++, mdn);
                }                	         
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    subscCampGrpCount.put(rs.getString(1).trim(), rs.getInt(2) );
                }
            }
            for(String mdn : mdnList){
                if(subscCampGrpCount.get(mdn) == null){
                    subscCampGrpCount.put(mdn, Integer.valueOf(0));
                }
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
            return subscCampGrpCount;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteCampedGrps(Map<Integer, List<String>> map, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMdnsForCampedGrp(Integer deletedGrpId, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "map", map);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_CAMPED_GRP_MDN_GRPID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
                Integer grpId = entry.getKey();
                List<String> mdns = entry.getValue();
                for (String mdn : mdns) {
                    pstmt.setString(1, mdn);
                    pstmt.setInt(2, grpId);
                    pstmt.addBatch();
                }
            }
            knLogger.debug(methodName, "Executing Query- ", query);
            int[] status = pstmt.executeBatch();
            knLogger.debug(methodName, "status- ", status);
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, Integer> getTGSCDocumentEtag(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTGSCDocumentEtag(List<String> mdns, KnPersisterTxn persisterTxn)";
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Integer> mdnTGSCEtag = new HashMap<String, Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            ArrayList<String> mdnArrList = new ArrayList<String>();
            if (mdns != null) {
                mdnArrList.addAll(mdns);
            }
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(mdnArrList, 1000);
            for (Collection<String> subMdnList : compList) {
            	int index = 1;
                query = queryMapper.getQuery(KnPersisterConstants.SELECT_TGSC_DOCUMENT_ETAG);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(subMdnList, query, "MDNLIST");
                conn = persisterTxn.getDBConnection(pttServerId, false);
                knLogger.debug(methodName, "Executing query -", query);
                pstmt = conn.prepareStatement(query);
                for(String mdn : mdns)
                	pstmt.setString(index++, mdn);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    mdnTGSCEtag.put(rs.getString(1).trim(), rs.getInt(2));
                }
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.",mdnTGSCEtag);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getTGSCDocumentEtag", pttServerId, KnDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return mdnTGSCEtag;
    }
}
