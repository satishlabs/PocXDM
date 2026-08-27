/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;

public class KnAuthorizationDocDAO implements ITableDAO {


    private static final KnLogger knLogger = KnLogger.getLogger(KnAuthorizationDocDAO.class);

    private static final String className = KnAuthorizationDocDAO.class.getName();
    private String pttServerId;
    private static final String TABLENAME = "DG.AUTHORIZATION_DOC";
    private static final String DELETE_QUERY = "DELETE FROM "+TABLENAME+" WHERE MDN =?";
    private static final String UPDATE_ETAG = "UPDATE "+TABLENAME+" SET ETAG = ?"+" WHERE MDN =?";


    public KnAuthorizationDocDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void insert(String mdn, Long etag, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "insert(mdn, etag, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;

        knLogger.debug(methodName, "ENTRY: insert new target mdn ", KnGDPRTemplate.mdn(mdn), " Persist ", persisterTxn);

        try {
            ArrayList<String> queryFields = new ArrayList<String>();
            queryFields.add("MDN");
            queryFields.add("ETAG");
            query = KnDbUtil.getInsertQuery(TABLENAME, queryFields);
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, KnGDPRTemplate.mdn(mdn));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, " Connection .. ", conn);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pStmt.setString(++columnIndex,mdn);
            pStmt.setLong(++columnIndex,etag);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to update etag - "+  e.getMessage(), pttServerId, KnProvDAOSourceTypes.AUTHORIZATION_DOC, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void updateEtag(String mdn, long etag, KnPersisterTxn persistTxn) throws KnDAOException{

        String methodName = "updateEtag(Mdn,etag,KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;

        knLogger.debug(methodName, "ENTRY: update etag for MDN ", KnGDPRTemplate.mdn(mdn), "ETAG :",etag," Persist ", persistTxn);

        try {

            query = UPDATE_ETAG;
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, KnGDPRTemplate.mdn(mdn));
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, " Connection .. ", conn);
            pStmt = conn.prepareStatement(UPDATE_ETAG);
            pStmt.setLong(1,etag);
            pStmt.setString(2,mdn);
            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to update etag - "+  e.getMessage(), pttServerId, KnProvDAOSourceTypes.AUTHORIZATION_DOC, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public void deleteMdn(String mdn,KnPersisterTxn persistTxn)throws KnDAOException{

        String methodName = "deleteMdn(Mdn,KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;

        knLogger.debug(methodName, "ENTRY: delete MDN ", KnGDPRTemplate.mdn(mdn), " Persist ", persistTxn);

        try {

            query = DELETE_QUERY;
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, KnGDPRTemplate.mdn(mdn));
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            knLogger.debug(methodName, " Connection .. ", conn);
            pStmt = conn.prepareStatement(DELETE_QUERY);
            pStmt.setString(1,mdn);
            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to delete - "+  e.getMessage(), pttServerId, KnProvDAOSourceTypes.AUTHORIZATION_DOC, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
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
}