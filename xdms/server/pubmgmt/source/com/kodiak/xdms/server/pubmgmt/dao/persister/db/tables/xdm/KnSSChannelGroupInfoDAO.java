/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.IntToDoubleFunction;


/**
 * Created by venkata sudhakar talluri on 28-12-2018
 * <p>
 * Table :DG.SSCHANNELGROUPINFO
 */


public class KnSSChannelGroupInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSSChannelGroupInfoDAO.class);
    private String pttServerId;
    public static final String SSCHANNELGROUPINFO = "DG.SSCHANNELGROUPINFO";
    public static final String GROUPINFO = "DG.CORPGROUPINFO";
    public static final String MDN = "MDN";
    public static final String GROUPID = "GROUPID";
    public static final String CORPID = "CORPID";

    public static final String QURY_SELECT = "SELECT " + MDN + "," + GROUPID + " FROM " + SSCHANNELGROUPINFO + "  WHERE " + MDN + "=?";
    public static final String QURY_INSERT = "INSERT INTO " + SSCHANNELGROUPINFO + " VALUES(?,?)";
    public static final String QURY_DELETE = "DELETE FROM " + SSCHANNELGROUPINFO + " WHERE " + MDN + "=? AND " + GROUPID + "=?";
    public static final String QUERY_GET_GROUPID_CORP_INFO = "SELECT S.GROUPID,C.CORPID FROM "+GROUPINFO+" C, "+SSCHANNELGROUPINFO+" S WHERE\n" +
            "            C.CORPGROUPID=S.GROUPID AND S.MDN= ?;";

    public KnSSChannelGroupInfoDAO() {
    }

    public KnSSChannelGroupInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("insert", "Not Implemented");
    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("update", "Not Implemented");
    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("delete", "Not Implemented");
    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("select", "Not Implemented");
        return null;
    }

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getSSGroupIds(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSSGroupIds(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        List<Integer> groupIds = new ArrayList<Integer>();
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = QURY_SELECT;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            if (rs.next()) {
                do {
                    groupIds.add(rs.getInt(GROUPID));
                }while (rs.next());
            }/* else {
                // throw back exception
                knLogger.error(methodName, "No SSChannel Group Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No SSChannel Group Info found. Query ->" + query);
            }
            */
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve SSChannel Group Info found for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStatement);
        }
        knLogger.info(methodName, "Returning SSChannel Group Info List - ", groupIds);
        return groupIds;
    }

    /**
     * @param mdn
     * @param groupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void createSSGroupId(String mdn, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createSSGroupId(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        try {
            query = QURY_INSERT;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn.trim());
            pStatement.setInt(2, groupId);
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.info(methodName, "Inserted record for mdn:  " + KnGDPRTemplate.mdn(mdn) + " groupId:" + groupId);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to Insert record for mdn and groupId - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        }finally {
            KnDbUtil.closeStatement(pStatement);
        }
        knLogger.debug(methodName, "EXIT : mdn ->" + KnGDPRTemplate.mdn(mdn) + ", gropId ->" + groupId);
    }

    /**
     * @param mdn
     * @param groupId
     * @param persisterTxn
     * @throws KnDAOException
     */

    public void deleteSSGroupId(String mdn, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSSGroupId(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        try {
            query = QURY_DELETE;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName,"query::::"+query);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn.trim());
            pStatement.setInt(2, groupId);
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.info(methodName, "Deleted record for mdn:  " + KnGDPRTemplate.mdn(mdn) + " groupId:" + groupId);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to Delete record for mdn and groupId - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        }finally {
            KnDbUtil.closeStatement(pStatement);
        }
        knLogger.debug(methodName, "EXIT : mdn ->" + KnGDPRTemplate.mdn(mdn) + ", gropId ->" + groupId);

    }

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Integer> getSsGroupIdsCorpInfo(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSsGroupIdsCorpInfo(String, boolean, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : ", KnGDPRTemplate.mdn(mdn));
        Map<Integer, Integer> GroupIdCorpInfo = new HashMap<>();
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = QUERY_GET_GROUPID_CORP_INFO;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            while (rs.next()) {
                GroupIdCorpInfo.put(rs.getInt(GROUPID), rs.getInt(CORPID));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve SSChannel Group Info found for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStatement);
            knLogger.info(methodName, "Returning SSChannel Group Info List - ", GroupIdCorpInfo.size());
        }
        return GroupIdCorpInfo;
    }

}
