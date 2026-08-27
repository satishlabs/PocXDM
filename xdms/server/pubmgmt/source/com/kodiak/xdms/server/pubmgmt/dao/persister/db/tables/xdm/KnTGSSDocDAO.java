/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnTGSSDocDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

/**
 * Created by venkata sudhakar talluri on 28-12-2018
 */


/*Table:DG.SIMULSESSION_DOC*/

public class KnTGSSDocDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnTGSSDocDAO.class);
    private String pttServerId;

    public static final String TABLENAME = "DG.SIMULSESSION_DOC";
    public static final String MDN = "MDN";
    public static final String ETAG = "ETAG";

    public static final String GET_TGSS_DOC = "SELECT " + MDN + "," + ETAG + " FROM " + TABLENAME + "  WHERE " + MDN + "=?";
    public static final String QRY_UPDATE_ETAG = "UPDATE " + TABLENAME + " SET " + ETAG + "=? WHERE " + MDN + "=?";


    public KnTGSSDocDAO(String pttServerId) {
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
    public KnTGSSDocDTO getTGSSSDoc(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getTGSSSDoc(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        KnTGSSDocDTO tgssDocDTO = new KnTGSSDocDTO();
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = GET_TGSS_DOC;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            if (rs.next()) {
                tgssDocDTO.setMdn(rs.getString(MDN).trim());
                tgssDocDTO.setEtag(rs.getLong(ETAG));
            } else {
                // throw back exception
                knLogger.info(methodName, "No Talk Group SS doc Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Talk Group SS doc Info found. Query ->" + query);
            }

        } catch (KnDAOException e) {
            knLogger.info(methodName, "DAO Exception - ");
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve Talk Group SS doc for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_TGSSDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStatement);
        }
        knLogger.info(methodName, "Returning Talk Group SS doc details - ", tgssDocDTO);
        return tgssDocDTO;
    }

    /**
     *
     * @param mdn
     * @param etag
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateEtagTGSSDoc(String mdn, long etag, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateEtagTGSSDoc(String, long, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        try {

            query = QRY_UPDATE_ETAG;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setLong(1, etag);
            pStatement.setString(2, mdn.trim());
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.info(methodName, "Updated Etag for mdn:  " + KnGDPRTemplate.mdn(mdn));
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to update Etag for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_TGSSDOC, query);
        } finally {
            KnDbUtil.closeStatement(pStatement);
        }
        knLogger.debug(methodName, "EXIT : mdn ->" + KnGDPRTemplate.mdn(mdn));

    }
}


