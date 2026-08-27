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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpLITargetProfile;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GET_LI_TARGET_INFO;

/**
 * ************************************************************************
 * <p>
 * File name:  KnXDMLISubscriberInfoDAO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Feb 06, 2017                8.3
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnXDMLISubscriberInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMLISubscriberInfoDAO.class);

    public String pttServerId = null;

    KnXDMLISubscriberInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("insert", "Unimplemented Methods");
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("update", "Unimplemented Methods");
    }

    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("delete", "Unimplemented Methods");
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn("select", "Unimplemented Methods");
        return null;
    }

    /**
     * This method is used to Subscriber which is available in LI_TARGET_INFO table. This is read only method.
     *
     * @param persisterTxn
     * @return
     * @throws Exception
     */
    public Collection<KnCorpLITargetProfile> getLITargetInfo(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getLITargetInfo(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : Input DTO passed persisterTxn - ", persisterTxn);
        Connection conn;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpLITargetProfile> liSubscribersList = new ArrayList<KnCorpLITargetProfile>();
        KnCorpLITargetProfile liTargetProfile = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_LI_TARGET_INFO);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                liTargetProfile = new KnCorpLITargetProfile();
                liTargetProfile.setMdn(rs.getString(1).trim());
                liTargetProfile.setInsertionTime(rs.getLong(2));
                liTargetProfile.setLIID(rs.getString(3));
                liTargetProfile.setLIPttServerId(rs.getString(4));
                liTargetProfile.setLICCCPttServerId(rs.getString(5));
                liTargetProfile.setDFCCIPAddress(rs.getString(6));
                liTargetProfile.setDFCCPort(rs.getInt(7));
                liTargetProfile.setIPAddressType(rs.getInt(8));
                liSubscribersList.add(liTargetProfile);
            }
            return liSubscribersList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the Li target Subcribers List - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching the Li target Subcribers List - ",e);
            throw KnDbUtil.processException(e, "Failed while fetching the Li subcribers List ." + e,
                    pttServerId, KnDAOSourceTypes.XDM_LI_TARGET_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT : Li target Subcribers List returned with no of members - ", liSubscribersList.size());
        }
    }
}
