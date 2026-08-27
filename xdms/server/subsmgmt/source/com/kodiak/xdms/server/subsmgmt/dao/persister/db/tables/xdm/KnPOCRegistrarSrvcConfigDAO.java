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
import com.kodiak.xdms.server.common.dto.common.KnPOCRegistrarSrvcConfigDTO;
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

public class KnPOCRegistrarSrvcConfigDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPOCRegistrarSrvcConfigDAO.class);

    private static final String className = KnPOCRegistrarSrvcConfigDAO.class.getName();
    private String pttServerId;

    private static final String TABLENAME = "DG.POCREGISTRARSRVCCONFIG";

    private static final String PRIMARY_REGISTRAR_URI = "PRIMARYREGISTRARURI";
    private static final String GEO_REGISTRAR_URI = "GEOREGISTRARURI";
    private static final String LOCATION_DEBOUNCE_TIMER = "LOCATIONDEBOUNCETIMER";
    private static final String PTT_SERVER_ID = "PTTSERVERID";
    private static final String ENABLE_ROAMING_STATUS_CHECK = "ENABLEROAMINGSTATUSCHECK";
    private static final String MAX_REGISTER_EXPIRY_TIME_DURATION = "MAXREGISTEREXPIRYTIMEDURATION";
    private static final String ENABLE_TU_FEATURE = "ENABLETUFEATURE";
    private static final String ENABLE_TU_SMS_FLAG = "ENABLETUSMSFLAG";
    private static final String TU_DOWN_TIMER = "TUDOWNTIMER";
    private static final String TU_UP_TIMER_START_VAL = "TUUPTIMERSTARTVAL";
    private static final String TU_UP_TIMER_MAX_VAL = "TUUPTIMERMAXVAL";
    private static final String TU_UP_TIMER_RAMP_DOWN_PERIOD = "TUUPTIMERRAMPDOWNPERIOD";
    private static final String TU_FORCE_ONLINE_MAX_WAIT_TIMER = "TUFORCEONLINEMAXWAITTIMER";

    private static final String SELECT_ALL_QRY = "SELECT " + PTT_SERVER_ID + ", " + PRIMARY_REGISTRAR_URI + ", "
            + GEO_REGISTRAR_URI + ", " + ENABLE_ROAMING_STATUS_CHECK + ", " + MAX_REGISTER_EXPIRY_TIME_DURATION + ", "
            + LOCATION_DEBOUNCE_TIMER + "," + ENABLE_TU_FEATURE + "," + ENABLE_TU_SMS_FLAG + "," + TU_DOWN_TIMER + "," + TU_UP_TIMER_START_VAL + "," + TU_UP_TIMER_MAX_VAL + "," + TU_UP_TIMER_RAMP_DOWN_PERIOD + "," + TU_FORCE_ONLINE_MAX_WAIT_TIMER + " FROM " + TABLENAME + " WHERE " + PTT_SERVER_ID + "= ?";


    public KnPOCRegistrarSrvcConfigDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    /**
     * method to select the POC Registrar Service Config
     *
     * @param pocPttServerId String
     * @param persistTxn     KnPersisterTxn
     * @return KnPOCRegistrarSrvcConfigDTO
     * @throws KnDAOException
     */
    public KnPOCRegistrarSrvcConfigDTO selectPOCRegistrarSrvcConfig(String pocPttServerId, KnPersisterTxn persistTxn) throws KnDAOException {

        String methodName = "selectPOCRegistrarSrvcConfig(String, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnPOCRegistrarSrvcConfigDTO srvcConfigDTO = new KnPOCRegistrarSrvcConfigDTO();
        knLogger.info( methodName, "ENTRY: Select POC Registrar Srvc Config ");
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
                srvcConfigDTO.setPttServerId(rs.getString(PTT_SERVER_ID));
                srvcConfigDTO.setPrimaryRegistrarURI(rs.getString(PRIMARY_REGISTRAR_URI));
                srvcConfigDTO.setEnableRoamingStatusCheck(rs.getInt(ENABLE_ROAMING_STATUS_CHECK));
                srvcConfigDTO.setGeoRegistrarURI(rs.getString(GEO_REGISTRAR_URI));
                srvcConfigDTO.setLocationDebounceTimer(rs.getInt(LOCATION_DEBOUNCE_TIMER));
                srvcConfigDTO.setMaxRegisterExpiryTimeDuration(rs.getInt(MAX_REGISTER_EXPIRY_TIME_DURATION));

                srvcConfigDTO.setEnableTUFeature(rs.getInt(ENABLE_TU_FEATURE));
                srvcConfigDTO.setEnableTUSMSFlag(rs.getInt(ENABLE_TU_SMS_FLAG));
                srvcConfigDTO.setTuDownTimer(rs.getInt(TU_DOWN_TIMER));
                srvcConfigDTO.setTuUpTimerStartVal(rs.getInt(TU_UP_TIMER_START_VAL));
                srvcConfigDTO.setTuUpTimerMaxVal(rs.getInt(TU_UP_TIMER_MAX_VAL));
                srvcConfigDTO.setTuUpTimerRampDownPeriod(rs.getInt(TU_UP_TIMER_RAMP_DOWN_PERIOD));
                srvcConfigDTO.setTuForceOnlineMaxWaitTimer(rs.getInt(TU_FORCE_ONLINE_MAX_WAIT_TIMER));


            } else {
                knLogger.error( methodName, "POC Registrar service Config doesn't exist");
                if (ownedTxn) {
                    persistTxn.rollback();
                }
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "POC Registrar service Config  Doesnt exist", pttServerId,
                        KnProvDAOSourceTypes.POCREGISTRARSRVCCONFIG, query);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }
            knLogger.debug( methodName, "returning poc registrar Srvc Config ", srvcConfigDTO);
            return srvcConfigDTO;

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
            throw KnDbUtil.processException(sqlE, "Failed to select POC Registrar service Config  - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCREGISTRARSRVCCONFIG, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to selectPOC Registrar service Config  - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCREGISTRARSRVCCONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info(methodName, "EXIT : select POC Registrar service Config ");
        }
    }
}
