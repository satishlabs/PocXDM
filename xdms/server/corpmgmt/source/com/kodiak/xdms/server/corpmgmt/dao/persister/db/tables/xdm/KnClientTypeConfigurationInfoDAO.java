/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;

/**
 * Created by nnamita on 23-12-2015.
 */
public class KnClientTypeConfigurationInfoDAO  implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnClientTypeConfigurationInfoDAO.class);

    private String pttServerId;

    KnClientTypeConfigurationInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "insert", "Unimplemented Methods");
    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "update", "Unimplemented Methods");
    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "delete", "Unimplemented Methods");
    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "select", "Unimplemented Methods");
        return null;
    }

    public Map<Integer,Boolean> getClientTypeConfigDetails(KnPersisterTxn persisterTxn) throws KnDAOException{

        String methodName = "getClientTypeConfigDetails( KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY :");
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<Integer,Boolean> clientConfigMap = new  HashMap<Integer,Boolean>();
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId,  false);
            query = queryMapper.getQuery(GET_CLIENT_TYPE_CONFIGURATION);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                Boolean clientEnabled = Boolean.TRUE;
                if(rs.getInt(2) == 0){
                    clientEnabled = Boolean.FALSE;
                }
                clientConfigMap.put(rs.getInt(1), clientEnabled);
            }

            knLogger.debug( methodName, "EXIT : clientConfigMap returned - ", clientConfigMap.size());
            return clientConfigMap;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving client configuration- ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occured while retrieving retrieving client configuration - "
                    + e);
            throw KnDbUtil.processException(e, "Failed while retrieving client configuration " + e,
                    pttServerId, KnDAOSourceTypes.CLIENT_TYPE_CONFIGURATION, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
    }
}
