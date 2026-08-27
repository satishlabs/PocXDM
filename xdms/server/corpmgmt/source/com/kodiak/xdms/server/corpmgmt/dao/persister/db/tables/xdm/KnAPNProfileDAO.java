/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dto.common.KnAPNConfigDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnAPNProfileDAO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar      June 04, 2019      9.1.1
 * <p/>
 * <p/>
 * KODIAK, 9th Floor, MFar Greenheart Phase IV
 * Manyata Tech Park, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnAPNProfileDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAPNProfileDAO.class);
    private String pttServerId;

    KnAPNProfileDAO(String pttServerId) {
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

    public Collection<KnAPNConfigDTO> getAPNProfileConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAPNProfileConfig(KnPersisterTxn)";
        knLogger.debug(methodName, "getAPNProfileConfig");
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Collection<KnAPNConfigDTO> apnConfigDTOS = new ArrayList<>();
        KnAPNConfigDTO apnConfigDTO = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.APN_PROFILE_CONFIG);
            conn = persisterTxn.getDBConnection(pttServerId, true);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                apnConfigDTO = new KnAPNConfigDTO();
                apnConfigDTO.setApnId(rs.getInt(1));
                apnConfigDTO.setApnName(rs.getString(2));
                apnConfigDTO.setIsDefault(rs.getInt(3));
                apnConfigDTO.setPttServerId(rs.getString(4));
                apnConfigDTO.setSipProxyUri(rs.getString(5));
                apnConfigDTO.setGeoSipProxyUri(rs.getString(6));
                apnConfigDTO.setGeoRegPrimF5Uri(rs.getString(7));
                apnConfigDTO.setGeoRegGeoF5Uri(rs.getString(8));
                apnConfigDTO.setFdServiceUriCell(rs.getString(9));
                apnConfigDTOS.add(apnConfigDTO);
            }
            knLogger.debug(methodName, "EXit: Query executed successfully", apnConfigDTOS.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getAPNProfileConfig",
                    pttServerId, KnDAOSourceTypes.APNPROFILEINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.exit(methodName);
        return apnConfigDTOS;
    }
}