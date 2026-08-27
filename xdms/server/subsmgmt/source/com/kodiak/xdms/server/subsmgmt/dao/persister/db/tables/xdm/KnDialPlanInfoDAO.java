/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnDialPlanInfoDAO.java
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
import com.kodiak.xdms.server.common.dto.common.KnDialPlanInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KnDialPlanInfoDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDialPlanInfoDAO.class);

    private static final String className = KnDialPlanInfoDAO.class.getName();
    private String pttServerId;

    private static final String TABLENAME = "DG.DIALPLANINFO";

    private static final String PTT_SERVER_ID = "PTTSERVERID";

    private static final String COUNTRY_CODE = "COUNTRYCODE";
    private static final String NATIONAL_DIAL_PREFIX = "NATIONALDIALPREFIX";
    private static final String INTERNATIONAL_DIAL_PREFIX = "INTERNATIONALDIALPREFIX";
    private static final String NETWORK_NUMBERING_PLAN = "NETWORKNUMBERINGPLAN";

    private static final String SELECT_ALL_QRY = "SELECT " + PTT_SERVER_ID + ", " + COUNTRY_CODE + ", "
            + NATIONAL_DIAL_PREFIX + ", " + INTERNATIONAL_DIAL_PREFIX + ", " + NETWORK_NUMBERING_PLAN
            + " FROM " + TABLENAME + " WHERE " + PTT_SERVER_ID + "= ?";


    public KnDialPlanInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    public KnDialPlanInfoDTO selectDialPlanInfo(KnPersisterTxn persistTxn) throws KnDAOException {

        String methodName = "selectDialPlanInfo(KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnDialPlanInfoDTO dialPlanInfoDTO = new KnDialPlanInfoDTO();
        knLogger.info( methodName, "ENTRY: Select Dial plan info ");
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
            pStmt.setString(1, pttServerId);

            knLogger.debug( methodName, "Query: Executing - " , query);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query: Executed ");

            if (rs.next()) {
                dialPlanInfoDTO.setPttServerId(rs.getString(PTT_SERVER_ID));
                dialPlanInfoDTO.setCountryCode(rs.getString(COUNTRY_CODE));
                dialPlanInfoDTO.setNationalDialPrefix(rs.getString(NATIONAL_DIAL_PREFIX));
                dialPlanInfoDTO.setInternationalDialPrefix(rs.getString(INTERNATIONAL_DIAL_PREFIX));
                dialPlanInfoDTO.setNetworkNumberingPlan(rs.getInt(NETWORK_NUMBERING_PLAN));

            } else {
                knLogger.error( methodName, "Dial Plan info doesn't exist");
                if (ownedTxn) {
                    persistTxn.rollback();
                }
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Dial Plan info  Doesnt exist", pttServerId,
                        KnProvDAOSourceTypes.DIALPLANINFO, query);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }
            knLogger.debug( methodName, "returning dial plan info " , dialPlanInfoDTO);
            return dialPlanInfoDTO;

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
            throw KnDbUtil.processException(sqlE, "Failed to select Dial plan info - " + sqlE.getMessage(), pttServerId,
                    KnProvDAOSourceTypes.DIALPLANINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select Dial plan info - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.DIALPLANINFO, query);
        } finally {
             KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info( methodName, "EXIT : select  Dial plan info");
        }
    }
}

