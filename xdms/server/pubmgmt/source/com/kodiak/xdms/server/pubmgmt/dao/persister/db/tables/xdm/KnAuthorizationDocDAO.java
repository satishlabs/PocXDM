/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnAuthDocDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

/**
 * Created by schandra on 22-12-2017.
 */
public class KnAuthorizationDocDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAuthorizationDocDAO.class);

    public String pttServerId = null;

    public KnAuthorizationDocDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public static final String TABLENAME = "DG.AUTHORIZATION_DOC";
    public static final String MDN ="MDN";
    public static final String ETAG = "ETAG";

    public static final String GET_AUTHORIZATION_DOC = "SELECT " + MDN +","+ETAG+ " FROM " +TABLENAME + "  WHERE "+MDN+"=?";
    public static final String QRY_UPDATE_ETAG = "UPDATE "+ TABLENAME + " SET " +ETAG+"=? WHERE "+ MDN +"=?";
    /**
     * This method is used for insertion of data in SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn("insert", "Not Implemented");
    }

    /**
     * This method is used for updation of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn("update", "Not Implemented");
    }

    /**
     * This method is used for deletion of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn("delete", "Not Implemented");
    }

    /**
     * This method is used for retrieving of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     * @return Collection
     */
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn("select", "Not Implemented");
        return null;
    }


    public KnAuthDocDTO getAuthorizationDocDetails(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getAuthorizationDocDetails(String,boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "AuthEntry : ");
        KnAuthDocDTO authDocDTO = new KnAuthDocDTO();
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = GET_AUTHORIZATION_DOC;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " , query , " persisterTxn : " , persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                authDocDTO.setMdn(rs.getString(MDN).trim());
                authDocDTO.setEtag(rs.getLong(ETAG));
            } else {
                // throw back exception
                knLogger.info( methodName, "No Authorization doc Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Authorization doc Info found. Query ->" + query);
            }

        } catch (KnDAOException e) {
            knLogger.info( methodName, "DAO Exception - ");
            throw e;
        }  catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ",e);
            throw KnDbUtil.processException(e, "Failed to retrieve Authorization doc for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_AUTHORIZATIONDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        knLogger.info( methodName, "Returning Authorization doc details - " ,  authDocDTO);
        return authDocDTO;
    }


    public void updateAuthorizationDocDetails(String mdn, long etag, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "updateAuthorizationDocDetails(String, int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        try {

            query = QRY_UPDATE_ETAG;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setLong(1, etag);
            pStatement.setString(2, mdn.trim());
            knLogger.debug( methodName, "QUERY : Executing " , query , ", persisterTxn : "  ,persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.info( methodName, "Updated Etag for mdn:  " + mdn);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to update Etag for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_AUTHORIZATIONDOC, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
             knLogger.debug( methodName, "EXIT : mdn ->" + KnGDPRTemplate.mdn(mdn));

    }

    public void updateMdnsEtagForAuthDoc(List<String> authMdnsList, long etag, KnPersisterTxn persisterTxn)throws KnDAOException{
        String methodName = "updateMdnsEtagForAuthDoc(String, String, int, KnPersisterTxn)";
        knLogger.debug( methodName,"Entry : ");

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {

            query = QRY_UPDATE_ETAG;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            for (String mdn : authMdnsList) {
                pStatement.setLong(1, etag);
                pStatement.setString(2, mdn.trim());
                pStatement.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing " , query , ", persisterTxn : " ,
                    persisterTxn);
            int[] count = pStatement.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed." , count.length);


            knLogger.info( methodName, "Updated Etag for mdns:  " ,authMdnsList.size());
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to update Etag for mdns- " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_AUTHORIZATIONDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : mdn ->" , authMdnsList.size());
        }
    }
}
