/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;

import java.util.Collection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnDirectoryDAO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 11, 2011           7.0
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
public class KnDirectoryDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDirectoryDAO.class);

    public static final String CLASSNAME = KnDirectoryDAO.class.getName();
    public static final String TABLENAME = "DG.XDM_DIRECTORY";

    public static final String DIR_DOC_ID     = "DIRDOCID";
    public static final String MDN            = "MDN";
    public static final String ETAG           = "ETAG";

    public String pttServerId = null;

    /**
     * @param pttServerId
     */
    public KnDirectoryDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    public static final String QRY_SELECT_ETAG = "SELECT " + ETAG + " FROM " + TABLENAME
            + " WHERE " + MDN + " = ?";
    public static final String QRY_UPDATE_ETAG = "UPDATE " + TABLENAME + " SET " + ETAG + " = ?"
            + " WHERE " + MDN + " = ?";

    /**
     * This method is used for insertion of data in SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "insert", "Not Implemented");
    }

    /**
     * This method is used for updation of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "update", "Not Implemented");
    }

    /**
     * This method is used for deletion of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "delete", "Not Implemented");
    }

    /**
     * This method is used for retrieving of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     * @return Collection
     */
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "select", "Not Implemented");
        return null;
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateEtagForDirDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException{

        String methodName = "updateEtagForDirDoc(String, String, int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_UPDATE_ETAG;

            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated Etag for mdn:  " +  KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Etag for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Etag for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : mdn ->" + KnGDPRTemplate.mdn(mdn));
        }
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getCurrentEtagForDirDoc(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCurrentEtagForDirDoc(String, boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        int etag = 0;

        try {
            query = QRY_SELECT_ETAG;

            //conn = persisterTxn.getDBConnection(pttServerId, true);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " , query , ", persisterTxn : " ,
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName,"QUERY : Completed.");

            if (rs.next()) {
                etag = rs.getInt(1);
            } else {
                // Throw back exception
                knLogger.error( methodName, "No Dir Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Dir Info found. Query ->" + query);
            }


            knLogger.debug( methodName, "Returning Etag - " , etag);
            return etag;
        } catch (SQLException e) {
            knLogger.error( methodName,  "SQL Exception - " , e);

            throw KnDbUtil.processException(e, "Failed to retrieve etag for dirdoc - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " , e);

            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to retrieve etag for dirdoc - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }

    }

}
