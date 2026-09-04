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

/*
DG.SIMULSESSION_DOC
* */
public class KnSimulSessionDocDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSimulSessionDocDAO.class);
    private String pttServerId;
    public static final String TABLENAME = "DG.SIMULSESSION_DOC";
    public static final String MDN = "MDN";
    public static final String ETAG = "ETAG";

    public static final String QURY_ETAG_SELECT = "SELECT " + ETAG + " FROM " + TABLENAME + "  WHERE " + MDN + "=?";
    public static final String QURY_INSERT =   "INSERT INTO "+TABLENAME+" VALUES(?,?)";
    public static final String QRY_UPDATE_ETAG = "UPDATE " + TABLENAME + " SET " + ETAG + "=? WHERE " + MDN + "=?";
    public static final String QRY_DELETE_MDN = "DELETE FROM "+ TABLENAME + "  WHERE " + MDN + "=?";

    public KnSimulSessionDocDAO() {
    }

    public KnSimulSessionDocDAO(String pttServerId) {
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

    public long getSSDocEtag(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
     ///   get the mdn and etag from requested mdn.
        final String methodName = "getSSGroupIds(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        long eTag = -1;
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = QURY_ETAG_SELECT;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn.trim());
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            if (rs.next()) {
                eTag = rs.getLong(ETAG);
            }/* else {
                // throw back exception
                knLogger.error(methodName, "No SSDoc found. Query ->" + query);
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No SSDoc found. Query ->" + query);
            }*/

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to getSSDocEtag found for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        knLogger.info(methodName, "Returning ETAG - ", eTag +" for MDN:"+mdn);
        return eTag;

    }
    public void insertMdn(String mdn, Long eTag, KnPersisterTxn persisterTxn) throws KnDAOException{
        //create the entry in table for request mdn and etag.
        final String methodName = "insertMdn(String,Long, KnPersisterTxn)";
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
            pStatement.setString(1, mdn.trim());
            pStatement.setLong(2, eTag);
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            int cnt = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed.");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to insertMdn found for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }

    }

    public void updateEtag(String mdn, long etag, KnPersisterTxn persistTxn) throws KnDAOException {
        //update the Etag for request mdn.
        final String methodName = "updateEtag(String,Long, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        long eTag = 0;
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = QRY_UPDATE_ETAG;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setLong(1, eTag);
            pStatement.setString(2, mdn.trim());
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persistTxn);
            int cnt = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed.");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to updateEtag found for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_SSCHNLGRP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }


    }

    public void deleteMdn(String mdn, KnPersisterTxn persistTxn)throws KnDAOException {
        // delete the entry in table for request mdn andd groupid.

        final String methodName = "deleteMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = QRY_DELETE_MDN;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn.trim());
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persistTxn);
            int cnt = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed.");

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

    }

    public void deleteMdn(List<String> mdnList, KnPersisterTxn persistTxn)throws KnDAOException {
        final String methodName = "deleteMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = "DELETE FROM DG.SIMULSESSION_DOC WHERE MDN = ?";
            //query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pStatement.setString(1, mdn);
                pStatement.addBatch();
            }
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persistTxn);
            int cnt = pStatement.executeBatch().length;
            knLogger.debug(methodName, "QUERY : Completed.");

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

    }

}
