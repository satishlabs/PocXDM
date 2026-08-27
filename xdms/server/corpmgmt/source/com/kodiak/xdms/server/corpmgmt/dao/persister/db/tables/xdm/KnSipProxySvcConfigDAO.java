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
import com.kodiak.xdms.server.common.dto.common.KnSIPProxySvcConfigDTO;
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
 * File name:  KnSipProxySvcConfigDAO.java
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

public class KnSipProxySvcConfigDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSipProxySvcConfigDAO.class);
    private String pttServerId;

    KnSipProxySvcConfigDAO(String pttServerId) {
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

    public Collection<KnSIPProxySvcConfigDTO> getSipProxySvcConfig(boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSipProxySvcConfig(boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "getSipProxySvcConfig - readOnly :", readOnly);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        ResultSet rs = null;
        Collection<KnSIPProxySvcConfigDTO> sipProxySvcConfigInfoDTO = new ArrayList<>();
        KnSIPProxySvcConfigDTO sipProxySvcConfigDTO = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(KnPersisterConstants.SIP_PROXY_SVC_CONFIG);
            conn = persisterTxn.getDBConnection(pttServerId, readOnly);
            pstmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sipProxySvcConfigDTO = new KnSIPProxySvcConfigDTO();
                sipProxySvcConfigDTO.setPttServerId(rs.getString(1));
                sipProxySvcConfigDTO.setSipProxyURI(rs.getString(2));
                sipProxySvcConfigDTO.setGeoSipProxyUri(rs.getString(3));
                sipProxySvcConfigDTO.setSipProxyUriWifi(rs.getString(4));
                sipProxySvcConfigDTO.setGeoSipProxyUriWifi(rs.getString(5));
                sipProxySvcConfigInfoDTO.add(sipProxySvcConfigDTO);
            }
            knLogger.debug(methodName, "EXit: Query executed successfully", sipProxySvcConfigInfoDTO.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while getSipProxySvcConfig",
                    pttServerId, KnDAOSourceTypes.SIPPROXYSVCCONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.exit(methodName);
        return sipProxySvcConfigInfoDTO;
    }
}