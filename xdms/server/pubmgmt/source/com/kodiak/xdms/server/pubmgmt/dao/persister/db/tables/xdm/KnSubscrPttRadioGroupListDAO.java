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
import com.kodiak.xdms.server.pubmgmt.dto.common.KnGroupUsageListDocDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

public class KnSubscrPttRadioGroupListDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSubscrPttRadioGroupListDAO.class);

    public String pttServerId = null;

    public KnSubscrPttRadioGroupListDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public static final String TABLENAME = "DG.SUBSCRPTTRADIOGROUPLISTDOC";
    public static final String MDN = "MDN";
    public static final String ETAG = "ETAG";

    public static final String GET_SUBSCRPTTRADIOGROUPLISTDOC = "SELECT " + MDN + "," + ETAG + " FROM " + TABLENAME + "  WHERE " + MDN + "=?";

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


    public KnGroupUsageListDocDTO getSubscrPTTRadioGroupListDoc(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscrPTTRadioGroupListDoc(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "AuthEntry : ");
        KnGroupUsageListDocDTO usageListDocDTO = new KnGroupUsageListDocDTO();
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = GET_SUBSCRPTTRADIOGROUPLISTDOC;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, " persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            if (rs.next()) {
                usageListDocDTO.setMdn(rs.getString(MDN).trim());
                usageListDocDTO.setEtag(rs.getLong(ETAG));
            } else {
                // throw back exception
                knLogger.error(methodName, "No SubscrPTTRadioGroupList doc Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No SubscrPTTRadioGroupList doc Info found. Query ->" + query);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve SubscrPTTRadioGroupList doc for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_SUBSCRPTTRADIOGROUPLISTDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        knLogger.info(methodName, "Returning SubscrPTTRadioGroupList details - ", usageListDocDTO);
        return usageListDocDTO;
    }
}
