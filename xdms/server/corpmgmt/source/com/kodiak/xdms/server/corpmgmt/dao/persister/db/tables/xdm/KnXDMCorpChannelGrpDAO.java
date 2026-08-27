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
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpTalkGrpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.MDNLIST;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnXDMCorpChannelGrpDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpChannelGrpDAO.class);

    private String pttServerId;

    KnXDMCorpChannelGrpDAO(String pttServerId) {
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
     * @return
     */


    public void cleanUpSubsChannelGrps(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "cleanUpSubsChannelGrps(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdn ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_SUBSCRIBERS_CHANNELGROUP);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeUpdate();
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleting entries from DG.CHANNELGROUPINFO", pttServerId,
                    KnDAOSourceTypes.CHANNELGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void cleanUpSubsChannelGrps(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "cleanUpSubsChannelGrps(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdn ", KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        int index =1;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "DELETE FROM DG.CHANNELGROUPINFO WHERE MDN IN (MDNLIST)";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            pstmt = conn.prepareStatement(query);
            for(String mdn : mdnList) {
                pstmt.setString(index++, mdn);
            }
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt.executeUpdate();
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleting entries from DG.CHANNELGROUPINFO", pttServerId,
                    KnDAOSourceTypes.CHANNELGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteChannelGrps(String mdn, List<Integer> grpIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteChannelGrps(String, int KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdn ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        Statement st = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_CHANNEL_GROUP_FOR_MDNLIST);
            query = replaceContactWithValue(query, KnPersisterConstants.GROUPIDS, formIntegerCommaSeperatedIdList(grpIdList));
            st = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", query);
            st.executeUpdate(query);
            knLogger.debug(methodName, "Exit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleting entries from DG.CHANNELGROUPINFO", pttServerId,
                    KnDAOSourceTypes.CHANNELGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(st);
        }
    }

    public void createSubsChannelGrps(String mdn, List<KnCorpTalkGrpInfoDTO> grpList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSubsChannelGrps(String mdn, Map<Integer,KnCorpTalkGrpInfoDTO> grpList, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn), "grpList", grpList);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.CREATE_SUBS_CHANNEL_GRP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            for (KnCorpTalkGrpInfoDTO corpTalkGrpInfoDTO : grpList) {
                pstmt.setString(1, mdn);
                pstmt.setInt(2, corpTalkGrpInfoDTO.getGroupId());
                pstmt.setInt(3, corpTalkGrpInfoDTO.getChannel());
                pstmt.addBatch();
            }
            knLogger.debug(methodName, "Executing Query- ", query);
            pstmt.executeBatch();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while create Subscriber channel Groups", pttServerId, KnDAOSourceTypes.CHANNELGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }


    public List<KnCorpTGSPersistDTO> getSubsChannelGrp(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsChannelGrp(String mdn, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ipTalkGroupDTO", ipTalkGroupDTO);
        List<KnCorpTGSPersistDTO> corpTGSPersistDTOs = new ArrayList<KnCorpTGSPersistDTO>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_SUBS_CHANNEL_GRP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, ipTalkGroupDTO.getMdn());
            knLogger.debug(methodName, "Executing Query- ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                // GROUPID, PRIORITY
                KnCorpTGSPersistDTO corpTGSPersistDTO = new KnCorpTGSPersistDTO();
                corpTGSPersistDTO.setGroupId(rs.getInt(1));
                corpTGSPersistDTO.setChannel(rs.getInt(2));
                corpTGSPersistDTOs.add(corpTGSPersistDTO);
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getSubsCampedGrp", pttServerId, KnDAOSourceTypes.CHANNELGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return corpTGSPersistDTOs;
    }


    public void deleteAllChannelGroups(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllCampedGroups(KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_SUBS_CHANNEL_GRP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing Query- ", query);
            pstmt.executeQuery();
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while deleteAllCampedGroups", pttServerId, KnDAOSourceTypes.CHANNELGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }


    public List<String> getMdnsForChannelGrp(Integer grpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMdnsForChannelGrp(Integer deletedGrpId, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "grpId", grpId);
        List<String> mdns = new ArrayList<String>();
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_MDNS_FOR_CHANNEL_GRP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, grpId);
            knLogger.debug(methodName, "Executing Query- ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                mdns.add(rs.getString(1).trim());
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
            return mdns;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CHANNELGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }

    }

    public int getChannelGrpCount(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getChannelGrpCount(Integer deletedGrpId, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn));
        int count = 0;
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_CHANNEL_GRP_COUNT);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, MDNLIST, "'" + mdn + "'");
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing Query- ", query);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(2);
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
            return count;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CHANNELGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Map<String, Integer> getChannelGrpCount(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getChannelGrpCount(Set<String> mdnList, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdnList", mdnList ==null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        Map<String, Integer> subscCampGrpCount = new HashMap<String, Integer>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            Collection<Collection<String>> compList = KnDbUtil.getCollectionList(new ArrayList<String>(mdnList), 1000);
            for (Collection<String> mdns : compList) {
            	int index = 1;
                query = queryMapper.getQuery(KnPersisterConstants.GET_CHANNEL_GRP_COUNT);
                query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdns, query, MDNLIST);
                pstmt = conn.prepareStatement(query);
                knLogger.debug(methodName, "Executing Query- ", query);
                for(String mdn : mdnList)
                	pstmt.setString(index++, mdn);
                
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    subscCampGrpCount.put(rs.getString(1).trim(), rs.getInt(2) );
                }
            }
            for(String mdn : mdnList){
                if(subscCampGrpCount.get(mdn) == null){
                    subscCampGrpCount.put(mdn, 0);
                }
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
            return subscCampGrpCount;
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CHANNELGROUPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteChannelGrps(Map<Integer, List<String>> map, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteChannelGrps(Integer deletedGrpId, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "map", map);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.DELETE_CHANNEL_GRP_MDN_GRPID);
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
            throw KnDbUtil.processException(e, "Failed while createSubsTalkGrpScanMode", pttServerId, KnDAOSourceTypes.CHANNELGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }


}
