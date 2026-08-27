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
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnEmergencyDocDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

public class KnEmergencyDocDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnEmergencyDocDAO.class);

    public String pttServerId = null;

    public KnEmergencyDocDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public static final String TABLENAME = "DG.EMERGENCY_DOC";
    public static final String MDN ="MDN";
    public static final String ETAG = "ETAG";

    public static final String GET_EMERGENCY_DOC = "SELECT " + MDN +","+ETAG+ " FROM " +TABLENAME + "  WHERE "+MDN+"=?";
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


    public KnEmergencyDocDTO getEmergencyDocDetails(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getEmergencyDocDetails(String, boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "AuthEntry : ");
        KnEmergencyDocDTO emergencyDocDTO = new KnEmergencyDocDTO();
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = GET_EMERGENCY_DOC;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " , query , " persisterTxn : " , persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                emergencyDocDTO.setMdn(rs.getString(MDN).trim());
                emergencyDocDTO.setEtag(rs.getLong(ETAG));
            } else {
                // throw back exception
                knLogger.info( methodName, "No Emergency doc Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Emergency doc Info found. Query ->" + query);
            }

        } catch (KnDAOException e) {
            knLogger.info( methodName, "DAO Exception - ");
            throw e;
        }catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to retrieve Emergency Emergency config doc for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_EMERGENCYDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        knLogger.info( methodName, "Returning Emergency config doc details - " ,  emergencyDocDTO);
        return emergencyDocDTO;
    }
}
