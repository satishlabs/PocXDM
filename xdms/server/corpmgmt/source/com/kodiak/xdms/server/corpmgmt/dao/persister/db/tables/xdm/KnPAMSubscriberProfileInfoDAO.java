/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GET_PAM_CLIENT_TYPES_FOR_CORP;

/**
 * Created by nnamita on 30-12-2015.
 */
public class KnPAMSubscriberProfileInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnClientTypeConfigurationInfoDAO.class);

    private String pttServerId;

    KnPAMSubscriberProfileInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("insert", "Unimplemented Methods");
    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("update", "Unimplemented Methods");
    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("delete", "Unimplemented Methods");
    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("select", "Unimplemented Methods");
        return null;
    }


    public Set<Integer> getCorporatePamClientTypes(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorporatePamClientTypes( corpId, KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY :");
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Set<Integer>  pamClientTypes = new HashSet<Integer>();
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(GET_PAM_CLIENT_TYPES_FOR_CORP);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1,corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                pamClientTypes.add(rs.getInt(1));
            }
            knLogger.debug(methodName, "EXIT : the pamClientTypes for the corporate returned is  ", pamClientTypes);
            return pamClientTypes;
        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving pam account client List from DB - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while retrieving pam account client list from DB" + e,
                    pttServerId, KnDAOSourceTypes.PAMSUBSCRPROFILEINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }
}
