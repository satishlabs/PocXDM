/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class KnSubscrPTTRadioGrpListDocDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnDeviceInfoDAO.class);
    private String pttServerId;

    public KnSubscrPTTRadioGrpListDocDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    private static final int DEFAULT_ZONE = 1;
    private static final int DEFAULT_CHANNEL = 8;
    private static final String INSERT_PTTRADIO_GROUP_LIST_DOC = "INSERT INTO DG.SUBSCRPTTRADIOGROUPLISTDOC (MDN, ETAG) VALUES(?, ?) ";
    private static final String GET_PTTRADIO_GROUP_LIST_DOC = "SELECT COUNT(MDN) FROM DG.SUBSCRPTTRADIOGROUPLISTDOC WHERE MDN = ? ";
    private static final String DELETE_PTTRADIO_GROUP_LIST_ZONE = "DELETE FROM DG.SUBSCRPTTRADIOTGLIST WHERE MDN = ? AND ZONEID > ?";
    private static final String DELETE_PTTRADIO_GROUP_LIST_CHANNEL = "DELETE FROM DG.SUBSCRPTTRADIOTGLIST WHERE MDN = ? AND ZONEID = ? AND CHANNELID > ?";
    private static final String GET_PTTRADIO_GROUP_LIST = "SELECT GROUPID FROM DG.SUBSCRPTTRADIOTGLIST WHERE MDN = ? AND (ZONEID > ? OR CHANNELID > ?)";
    private static final String DELETE_SCAN_LIST = "DELETE FROM DG.CAMPEDGROUPINFO WHERE MDN = ? AND GROUPID = ?";

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

    public boolean isDocExist(KnPersisterTxn persistTxn, String mdn) throws KnDAOException {
        String methodName = "isDocExist()";
        String query = GET_PTTRADIO_GROUP_LIST_DOC;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean isExist = false;
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug(methodName, "Query: Executed ");
            if (rs.next()) {
                int count = rs.getInt(1);
                if(count > 0){
                    isExist = true;
                }
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to fetch the count- " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.SUBSCRPTTRADIOGROUPLISTDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
        }
        knLogger.debug(methodName, "isExist - ", isExist);
        return isExist;
    }

    public void insertTGLDoc(KnPersisterTxn persistTxn, String mdn, long etag) throws KnDAOException {
        String methodName = "insertTGLDoc()";
        String query = INSERT_PTTRADIO_GROUP_LIST_DOC;
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);
            pStmt.setLong(2, etag);
            knLogger.debug( methodName, "QUERY: Executing the Query " , query);
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed the Query");

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil.processException(e, "Failed to insert - "
                            + e.getMessage(), pttServerId, KnProvDAOSourceTypes.SUBSCRPTTRADIOGROUPLISTDOC, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    public void deleteSubsAddlTalkGroupZone(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSubsAddlTalkGroupZone(String, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, mdn);
        PreparedStatement pstmt = null;
        String query = DELETE_PTTRADIO_GROUP_LIST_ZONE;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2, DEFAULT_ZONE);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete ATG from DG.SUBSCRPTTRADIOTGLIST table " + e,
                    pttServerId, KnProvDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public void deleteSubsAddlTalkGroupChannel(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSubsAddlTalkGroupChannel(String, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdn(mdn));
        PreparedStatement pstmt = null;
        String query = DELETE_PTTRADIO_GROUP_LIST_CHANNEL;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2, DEFAULT_ZONE);
            pstmt.setInt(3, DEFAULT_CHANNEL);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete ATG from DG.SUBSCRPTTRADIOTGLIST table " + e,
                    pttServerId, KnProvDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }

    public Collection<Integer> getSubsAddlTGList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsAddlTGList(KnIPTalkGroupDTO mdn, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = GET_PTTRADIO_GROUP_LIST;
        Set<Integer> groupIds = new HashSet<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, mdn);
            pstmt.setInt(2, DEFAULT_ZONE);
            pstmt.setInt(3, DEFAULT_CHANNEL);
            knLogger.debug(methodName, "Executing Query- ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groupIds.add(rs.getInt(1));
            }
            knLogger.debug(methodName, "Exit: Query executed successfully.");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getSubsAddlTGList", pttServerId, KnProvDAOSourceTypes.SUBSCRPTTRADIOTGLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return groupIds;
    }

    public void deleteScanList(String mdn, Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteScanList(String, Collection<Integer>, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdn(mdn));
        PreparedStatement pstmt = null;
        String query = DELETE_SCAN_LIST;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pstmt = conn.prepareStatement(query);
            for (Integer grpId : groupIds) {
                pstmt.setString(1, mdn);
                pstmt.setInt(2, grpId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to delete ATG from DG.CAMPEDGROUPINFO table " + e,
                    pttServerId, KnProvDAOSourceTypes.CAMPEDGROUPINFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
    }
}
