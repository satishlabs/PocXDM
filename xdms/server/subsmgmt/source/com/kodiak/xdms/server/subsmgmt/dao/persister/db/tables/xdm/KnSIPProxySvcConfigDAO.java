/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnSIPProxySvcConfigDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * *****************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar           Aug 31, 2012       7.4
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
 * *******************************************************************************
 */

public class KnSIPProxySvcConfigDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSIPProxySvcConfigDAO.class);

    private static final String className = KnSIPProxySvcConfigDAO.class.getName();
    private String pttServerId;

    private static final String TABLENAME = "DG.SIPPROXYSVCCONFIG";

    private static final String SIP_PROXY_URI = "SIPPROXYURI";
    private static final String PTT_SERVER_ID = "PTTSERVERID";
    private static final String CLIENT_CONNECTION_RETRY_INTERVAL = "CLIENTCONNECTIONRETRYINTERVAL";
    private static final String MAX_CLIENT_CONN_RETRY_ATTEMPTS = "MAXCLIENTCONNRETRYATTEMPTS";
    private static final String CLIENT_CONNECTION_SECURITY_LEVEL = "CLIENTCONNECTIONSECURITYLEVEL";
    private static final String CLIENT_SIP_TXN_TIMEOUT = "CLIENTSIPTXNTIMEOUT";
    private static final String CLIENT_SIP_REFER_TXN_TIMEOUT = "CLIENTSIPREFERTXNTIMEOUT";
    private static final String MIN_TCP_KA_TIMER_ON_WIFI = "MIN_TCP_KA_TIMER_ON_WIFI";
    private static final String WIFI_TCP_KA_TIMER_INCR_VAL = "WIFI_TCP_KA_TIMER_INCR_VAL";
    private static final String MAX_TCP_KA_TIMER_ON_WIFI = "MAX_TCP_KA_TIMER_ON_WIFI";
    private static final String WIFI_SSID_TIMEOUT_MAP_SIZE = "WIFI_SSID_TIMEOUT_MAP_SIZE";
    private static final String TCP_KA_TIMER_ON_MACRO_CELLULAR = "TCP_KA_TIMER_ON_MACRO_CELLULAR";
    private static final String DETECT_WIFI_NAT_TCP_TIMEOUT = "DETECT_WIFI_NAT_TCP_TIMEOUT";
    private static final String SIP_PROXY_URI_FOR_BCS = "SIPPROXYURIFORBCS";



    private static final String SELECT_ALL_QRY = "SELECT " + PTT_SERVER_ID + ", " + SIP_PROXY_URI + ", "
            + CLIENT_CONNECTION_RETRY_INTERVAL + ", " + MAX_CLIENT_CONN_RETRY_ATTEMPTS + ", " + CLIENT_CONNECTION_SECURITY_LEVEL + ", " + CLIENT_SIP_TXN_TIMEOUT + ", "
            + CLIENT_SIP_REFER_TXN_TIMEOUT + ", " + MIN_TCP_KA_TIMER_ON_WIFI + ", " + WIFI_TCP_KA_TIMER_INCR_VAL + ", "
            + MAX_TCP_KA_TIMER_ON_WIFI + ", " + WIFI_SSID_TIMEOUT_MAP_SIZE + ", " + TCP_KA_TIMER_ON_MACRO_CELLULAR + ", " + DETECT_WIFI_NAT_TCP_TIMEOUT + " ," + SIP_PROXY_URI_FOR_BCS
            + " FROM  " + TABLENAME + " WHERE " + PTT_SERVER_ID + " =?";


    public KnSIPProxySvcConfigDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    /**
     * method to select the SIP Proxy Service Config
     *
     * @param persistTxn KnPersisterTxn
     * @return KnSipProxySvcConfigDTO
     * @throws KnDAOException
     */
    public KnSIPProxySvcConfigDTO selectSIPProxySvcConfig(String pocPttServerId, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSIPProxySvcConfig(String, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnSIPProxySvcConfigDTO sipProxySvcConfigDTO = new KnSIPProxySvcConfigDTO();
        knLogger.info( methodName, "ENTRY: Select SIP Proxy Srvc Config ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction ");
                ownedTxn = true;
            }

            query = SELECT_ALL_QRY;

            conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, pocPttServerId);

            knLogger.debug( methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query: Executed ");

            if (rs.next()) {
                sipProxySvcConfigDTO.setPttServerId(rs.getString(PTT_SERVER_ID));
                sipProxySvcConfigDTO.setSipProxyURI(rs.getString(SIP_PROXY_URI));
                sipProxySvcConfigDTO.setClientConnRetryInterval(rs.getInt(CLIENT_CONNECTION_RETRY_INTERVAL));
                sipProxySvcConfigDTO.setClientConnSecurityLevel(rs.getInt(CLIENT_CONNECTION_SECURITY_LEVEL));
                sipProxySvcConfigDTO.setClientSipReferTxnTimeout(rs.getInt(CLIENT_SIP_REFER_TXN_TIMEOUT));
                sipProxySvcConfigDTO.setClientSipTxnTimeout(rs.getInt(CLIENT_SIP_TXN_TIMEOUT));
                sipProxySvcConfigDTO.setDetectWifiNatTcpTimeout(rs.getInt(DETECT_WIFI_NAT_TCP_TIMEOUT));
                sipProxySvcConfigDTO.setMaxClientConnRtyAttempts(rs.getInt(MAX_CLIENT_CONN_RETRY_ATTEMPTS));
                sipProxySvcConfigDTO.setMaxTcpKaTimerOnWifi(rs.getInt(MAX_TCP_KA_TIMER_ON_WIFI));
                sipProxySvcConfigDTO.setTcpKaTimerOnMacroCellular(rs.getInt(TCP_KA_TIMER_ON_MACRO_CELLULAR));
                sipProxySvcConfigDTO.setMinTcpKaTimerOnWifi(rs.getInt(MIN_TCP_KA_TIMER_ON_WIFI));
                sipProxySvcConfigDTO.setWifiSsidTimeoutMapSize(rs.getInt(WIFI_SSID_TIMEOUT_MAP_SIZE));
                sipProxySvcConfigDTO.setWifiTcpKaTimerIncrVal(rs.getInt(WIFI_TCP_KA_TIMER_INCR_VAL));
                sipProxySvcConfigDTO.setSipProxyURIForBCS(rs.getString(SIP_PROXY_URI_FOR_BCS));

            } else {
                knLogger.error( methodName, "SIP Proxy  service Config doesn't exist");
                if (ownedTxn) {
                    persistTxn.rollback();
                }
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "SIP Proxy  service Config  Doesnt exist", pttServerId,
                        KnProvDAOSourceTypes.SIPPROXYSVCCONFIG, query);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }
            knLogger.debug( methodName, "returning SIP Proxy  Srvc Config ", sipProxySvcConfigDTO);
            return sipProxySvcConfigDTO;

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception occurred");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to select SIP Proxy  service Config  - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SIPPROXYSVCCONFIG, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select SIP Proxy  service Config  - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SIPPROXYSVCCONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info( methodName, "EXIT : select SIP Proxy  service Config ");
        }
    }
}
