/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnSSChannelGroupInfoDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSSChannelGroupInfoDAO.class);
    private String pttServerId;
    public static final String TABLENAME = "DG.SSCHANNELGROUPINFO";
    public static final String MDN = "MDN";
    public static final String GROUPID = "GROUPID";

    public static final String QURY_SELECT = "SELECT " + MDN + "," + GROUPID + " FROM " + TABLENAME + "  WHERE " + MDN + "=?";
    public static final String QURY_INSERT = "INSERT INTO " + TABLENAME + " VALUES(?,?)";
    public static final String QURY_DELETE = "DELETE FROM " + TABLENAME + " WHERE " + MDN + "=?";

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
     *
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getSSGroupIds(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        //   -get the list of groupIds for the requested mdn.
        final String methodName = "getSSGroupIds(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        List<Integer> groupIds = new ArrayList<Integer>();
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = QURY_SELECT;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            if (rs.next()) {
                do {
                    groupIds.add(rs.getInt(GROUPID));
                } while (rs.next());
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
            KnDbUtil.closePreparedStatement(pStatement);
        }
        knLogger.info(methodName, "Returning SSChannel Group Info List - ", groupIds);
        return groupIds;

    }

    /**
     *
     * @param mdn
     * @param groupIds
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void createMdnGroupIds(String mdn, List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        //   -create the entry in table for request mdn and list of groupid.This method will be used in changeMDN flow.

        final String methodName = "createMdnGroupIds(String,List, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = QURY_INSERT;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            for (Integer grpId : groupIds) {
                pStatement.setString(1, mdn);
                pStatement.setInt(2, grpId);
                pStatement.addBatch();
            }
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            pStatement.executeBatch();
            knLogger.debug(methodName, "QUERY : Insert Completed.");

            knLogger.debug(methodName, "QUERY : Completed.");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to createMdnGroupIds  for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        knLogger.info(methodName, "createMdnGroupIds Successful");

    }

    /**
     *
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        //   -delete all the entry in table for request mdn

        final String methodName = "deleteMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int cnt = 0;
        try {
            query = QURY_DELETE;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            cnt = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed. no.of records deleted:" + cnt);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to deleteMdn  for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        knLogger.info(methodName, "deleteMdn Successful");

    }

    public void deleteMdn(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int cnt = 0;
        int index = 1;
        try {
            query = "DELETE FROM DG.SSCHANNELGROUPINFO WHERE MDN IN (MDNLIST)";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pStatement.setString(index++, mdn);
            }
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            cnt = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed. no.of records deleted:" + cnt);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to deleteMdn  for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        knLogger.info(methodName, "deleteMdn Successful");

    }
}

