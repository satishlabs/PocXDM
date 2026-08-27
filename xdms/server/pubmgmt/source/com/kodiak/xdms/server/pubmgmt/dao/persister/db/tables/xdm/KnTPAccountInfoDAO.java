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
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

public class KnTPAccountInfoDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorporateInfoDAO.class);

    public static final String TABLENAME = "DG.THIRD_PARTY_ACCOUNT_INFO";

    public static final String QRY_SELECT_TP_ACCOUNT_ID = "SELECT THIRD_PARTY_ID FROM " + TABLENAME + " WHERE THIRD_PARTY_ACCT_ID = ?";

    public String pttServerId = null;

    public KnTPAccountInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
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

    /**
     * Method to get the third party ID.
     * @param tpAccID
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getTPId(String tpAccID, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getTPId(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : -> ", tpAccID);

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int result = 0;
        try {
            query = QRY_SELECT_TP_ACCOUNT_ID;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            //conn = persisterTxn.getDBConnection(pttServerId, true);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, tpAccID);
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
            knLogger.debug(methodName, "QUERY : Completed : result", result);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.THIRD_PARTY_ACCOUNT_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStatement);
        }
        return result;
    }
}
