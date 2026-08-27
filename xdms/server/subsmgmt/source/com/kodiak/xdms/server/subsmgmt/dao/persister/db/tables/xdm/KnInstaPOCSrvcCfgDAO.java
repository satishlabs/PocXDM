/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnInstaPOCSrvcCfgDAO.java
 * Subsystem:   Provisioning library
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
import com.kodiak.xdms.server.common.dto.common.KnInstaPOCSrvcCfgDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KnInstaPOCSrvcCfgDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnInstaPOCSrvcCfgDAO.class);

    private static final String className = KnInstaPOCSrvcCfgDAO.class.getName();
    private String pttServerId;

    private static final String TABLENAME = "DG.INSTAPOC_SRVC_CFG";

    private static final String PTT_SERVER_ID = "PTTSERVERID";

    private static final String NUM_OCTETS_PER_WAKEUP_MSG = "NUM_OCTETS_PER_WAKEUP_MSG";
    private static final String NUM_WAKEUP_MSG_PER_TRIGGER = "NUM_WAKEUP_MSG_PER_TRIGGER";
    private static final String WAKEUP_TRIGGER_INTERVAL = "WAKEUP_TRIGGER_INTERVAL";
    private static final String NUM_WAKEUP_MSGS_PER_BURST = "NUM_WAKEUP_MSGS_PER_BURST";
    

    private static final String SELECT_ALL_QRY = "SELECT " + PTT_SERVER_ID + ", " + NUM_OCTETS_PER_WAKEUP_MSG +
            ", " + NUM_WAKEUP_MSG_PER_TRIGGER + ", " + WAKEUP_TRIGGER_INTERVAL + "," + NUM_WAKEUP_MSGS_PER_BURST +
            " FROM " + TABLENAME + " WHERE " + PTT_SERVER_ID + "= ?";


    public KnInstaPOCSrvcCfgDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public KnInstaPOCSrvcCfgDTO selectInstaPOCsrvcCfg(String pocPttServerId, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectInstaPOCsrvcCfg(String , KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnInstaPOCSrvcCfgDTO instaPOCSrvcCfgDTO = new KnInstaPOCSrvcCfgDTO();
        knLogger.info( methodName, "ENTRY: Select Presence service Config ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction ");
                persistTxn.open();
                ownedTxn = true;
            }

            query = SELECT_ALL_QRY;

            conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, pocPttServerId);

            knLogger.debug( methodName, "Query: Executing - " , query);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query: Executed ");

            if (rs.next()) {
                instaPOCSrvcCfgDTO.setPttServerId(rs.getString(PTT_SERVER_ID));
                instaPOCSrvcCfgDTO.setNumOctetsPerWakeupMsg(rs.getInt(NUM_OCTETS_PER_WAKEUP_MSG));
                instaPOCSrvcCfgDTO.setNumWakeupMsgPerTrigger(rs.getInt(NUM_WAKEUP_MSG_PER_TRIGGER));
                instaPOCSrvcCfgDTO.setWakeupTriggerInterval(rs.getInt(WAKEUP_TRIGGER_INTERVAL));
                instaPOCSrvcCfgDTO.setNumWakeupMsgsPerBurst(rs.getInt(NUM_WAKEUP_MSGS_PER_BURST));

            } else {
                knLogger.error( methodName, "Insta POC Service config doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Insta POC Service config Doesnt exist", pttServerId,
                        KnProvDAOSourceTypes.INSTAPOCSRVCONFIG, query);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }
            knLogger.debug( methodName, "returning Insta POC Srvc CFG " , instaPOCSrvcCfgDTO);
            return instaPOCSrvcCfgDTO;

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
            throw KnDbUtil.processException(sqlE, "Failed to select Insta POC Service config - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.INSTAPOCSRVCONFIG, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select Insta POC Service config - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.INSTAPOCSRVCONFIG, query);
        } finally {
             KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info( methodName, "EXIT : select Insta POC Service config");
        }
    }
}

