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
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;

public class KnDeviceInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnDeviceInfoDAO.class);
    private String pttServerId;

    public KnDeviceInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    private static final String UPDATE_LAST_USED_BY_MDN="UPDATE DG.DEVICE_INFO SET DEVICELASTUSED = ? WHERE DEVICEID = ? ";

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

    public void updateLastUsed(String mdn, Long lastProfileUpdateTime, KnPersisterTxn persistTxn) throws KnDAOException{
        String methodName = "updateLastUsed()";
        String query = UPDATE_LAST_USED_BY_MDN;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug( methodName, "ENTRY : Device last used ");
        try {
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pStmt.setLong(++columnIndex, lastProfileUpdateTime);
            pStmt.setString(++columnIndex, mdn);
            knLogger.debug( methodName, "QUERY: Executing the Query " , query);
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed the Query");

        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to update Corp Profile - " + e.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.CORPINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }
}
