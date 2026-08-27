package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class KnEmergencyDocDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnEmergencyDocDAO.class);
    public String pttServerId = null;
    private static final String UPDATE_EMERG_USER_DOC = "UPDATE DG.EMERGENCY_DOC SET ETAG = ? WHERE MDN = ?";

    KnEmergencyDocDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void updateEmergencyDocEtag(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateEmergencyDocEtag(String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdn - ", mdn);
        PreparedStatement pstmt = null;
        String query = null;
        Connection conn;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = UPDATE_EMERG_USER_DOC;
            pstmt = conn.prepareStatement(query);
            long etag = System.currentTimeMillis();
            pstmt.setLong(1, etag);
            pstmt.setString(2, mdn);
            knLogger.debug(methodName, "Executing query 2- ", query);
            pstmt.executeUpdate();
            knLogger.debug(methodName, "EXit: Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to insertOrUpdate Emergency Doc from DG.EMERGENCY_DOC table " + e,
                    pttServerId, KnProvDAOSourceTypes.SUBS_EMERGENCY_DOC, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
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
