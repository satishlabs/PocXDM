/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMLocationServiceConfigDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        26-01-2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

 import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

public class KnXDMLocationServiceConfigDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMLocationServiceConfigDAO.class);

    public String pttServerId = null;

    KnXDMLocationServiceConfigDAO(String pttServerId) {
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

    public Map<String, Boolean> getLocationPubFeaturebit(ArrayList<String> pocHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getLocationPubFeaturebit(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : pocHome,- ", pocHome);
        Connection conn;
        PreparedStatement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<String, Boolean> isLocationPubEnabled = new HashMap<String, Boolean>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.GET_LOCATION_PUBLISH);
            conn = persisterTxn.getDBConnection(pttServerId, false);
            query = replaceContactWithValue(query, KnPersisterConstants.POCHOMES, formCommaSeperatedIdList(pocHome));
            stmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery();
            knLogger.debug(methodName, "Executed query successfully");
            while (rs.next()) {
                isLocationPubEnabled.put(rs.getString(1), Boolean.valueOf(rs.getInt(2)==1 ? true : false));
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getLocationPubFeaturebit -",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_EXT_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
        }
        knLogger.debug(methodName, "isLocationPubEnabled" + isLocationPubEnabled);
        return isLocationPubEnabled;
    }

}