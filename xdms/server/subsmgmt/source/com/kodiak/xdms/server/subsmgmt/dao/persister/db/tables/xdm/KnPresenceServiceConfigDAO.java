/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnPresenceServiceConfigDAO.java
 * Subsystem:   Provisioning Library
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit KUmar           Jan 15, 2011       7.0
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

package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnPresenceServiceConfigDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KnPresenceServiceConfigDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPresenceServiceConfigDAO.class);

    private static final String className = KnPresenceServiceConfigDAO.class.getName();
    private String xdmPttServerId;

    private static final String TABLENAME = "DG.PRESENCESERVICECONFIG";

    private static final String PTT_SERVER_ID = "PTTSERVERID";

    private static final String PRIMARY_PRESENCE_SERVER_URI = "PRIMARYPRESENCESERVERURI";
    //private static final String GEO_PRESENCE_SERVER_URI = "GEOPRESENCESERVERURI";
    private static final String PRESENCE_PUBLISH_THROTTLE_TIMER = "PRESENCEPUBLISHTHROTTLETIMER";
    private static final String RLS_SUBSCRIPTION_VALIDITY = "RLS_SUBSCRIPTIONVALIDITY";
    private static final String PRESENCE_PUBLISH_VALIDITY = "PRESENCEPUBLISHVALIDITY";
  //  private static final String MAX_SIP_NOTIFY_MTU_SIZE = "MAX_SIPNOTIFYMTUSIZE";
    private static final String RLS_NOTIFY_THROTTLE_TIMER = "RLS_NOTIFYTHROTTLETIMER";
    private static final String ENABLE_PR_IN_POC = "ENABLE_PR_IN_POC";


    private static final String SELECT_ALL_QRY = "SELECT " + PTT_SERVER_ID + ", " + PRIMARY_PRESENCE_SERVER_URI +
            ", " + PRESENCE_PUBLISH_THROTTLE_TIMER + ", " + RLS_SUBSCRIPTION_VALIDITY +
            ", " + PRESENCE_PUBLISH_VALIDITY + ", " +  RLS_NOTIFY_THROTTLE_TIMER +  ", " + ENABLE_PR_IN_POC +
            " FROM " + TABLENAME + " WHERE " + PTT_SERVER_ID + "= ?";


    public KnPresenceServiceConfigDAO(String pttServerId) {
        this.xdmPttServerId = pttServerId;
    }

    /**
     *  method to select the Presence Service Config
     *
     * @param presencePttServerId String
     * @param persistTxn  KnPersisterTxn
     * @return  KnPresenceServiceConfigDTO
     * @throws KnDAOException
     */
    public KnPresenceServiceConfigDTO selectPresenceServiceConfig(String presencePttServerId, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectPresenceServiceConfig(String,KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnPresenceServiceConfigDTO preServConfigDTO = new KnPresenceServiceConfigDTO();
        knLogger.info( methodName, "ENTRY: Select Presence service Config ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction ");
                persistTxn.open();
                ownedTxn = true;
            }

            query = SELECT_ALL_QRY;

            conn = persistTxn.getDBConnection(xdmPttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, presencePttServerId);

            knLogger.debug( methodName, "Query: Executing - " , query, " PTTID - ", presencePttServerId);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query: Executed ");

            if (rs.next()) {
                preServConfigDTO.setPttServerId(rs.getString(PTT_SERVER_ID));
                preServConfigDTO.setPrimaryPresenceServerURI(rs.getString(PRIMARY_PRESENCE_SERVER_URI));
                //preServConfigDTO.setGeoPresenceServerURI(rs.getString(GEO_PRESENCE_SERVER_URI));
                preServConfigDTO.setPresencePublishThrottleTimer(rs.getInt(PRESENCE_PUBLISH_THROTTLE_TIMER));
                preServConfigDTO.setRLS_SubscriptionValidity(rs.getInt(RLS_SUBSCRIPTION_VALIDITY));
                preServConfigDTO.setPresencePublishValidity(rs.getInt(PRESENCE_PUBLISH_VALIDITY));
                //preServConfigDTO.setMax_SIPNotifyMTUSize(rs.getInt(MAX_SIP_NOTIFY_MTU_SIZE));
                preServConfigDTO.setRLS_NotifyThrottleTimer(rs.getInt(RLS_NOTIFY_THROTTLE_TIMER));
                preServConfigDTO.setEnablePRInPoC(rs.getInt(ENABLE_PR_IN_POC));

            } else {
                knLogger.error( methodName, "Presence Service Config doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Presence Service Config  Doesnt exist",
                        xdmPttServerId, KnProvDAOSourceTypes.PRESENCESRVCONFIG, query);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }
            knLogger.debug( methodName, "returning presence service config " , preServConfigDTO);
            return preServConfigDTO;

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
            throw KnDbUtil.processException(sqlE, "Failed to select Presence Service Config - " + sqlE.getMessage(),
                    xdmPttServerId, KnProvDAOSourceTypes.PRESENCESRVCONFIG, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select Presence Service Config- " + e.getMessage(),
                    xdmPttServerId, KnProvDAOSourceTypes.PRESENCESRVCONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info( methodName, "EXIT : select Presence Service Config");
        }
    }
}
