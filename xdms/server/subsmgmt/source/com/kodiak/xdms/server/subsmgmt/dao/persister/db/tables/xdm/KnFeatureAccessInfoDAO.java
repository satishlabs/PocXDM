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
import com.kodiak.xdms.server.common.dto.common.KnFeatureAccessInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * *****************************************************************************
 * File name:   KnFeatureAccessInfoDAO.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit KUmar           Aug 07, 2012    7.2
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

public class KnFeatureAccessInfoDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnFeatureAccessInfoDAO.class);

    private static final String className = KnFeatureAccessInfoDAO.class.getName();
    private String pttServerId;

    private static final String TABLENAME = "DG.FEATUREACCESSINFO";


    private static final String FEATURE_ACCESS_INDEX = "FEATUREACCESSINDEX";
    private static final String FEATURE_NAME = "FEATURENAME";
    private static final String ACCESS_SIGNAL_TYPE = "ACCESSSIGNALTYPE";
    private static final String ACCESS_TRANSPORT_SUBSYSTEM = "ACCESSTRANSPORTSUBSYSTEM";
    private static final String ROUTE_TO_SUBSYSTEM = "ROUTETOSUBSYSTEM";
    private static final String SERVICED_BY_SUBSYSTEM = "SERVICEDBYSUBSYSTEM";
    private static final String ACCESS_NUMBER_TYPE = "ACCESSNUMBERTYPE";
    private static final String MULTI_ACCES_SNUMBER_ENABLED = "MULTI_ACCESSNUMBER_ENABLED";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String IS_ACCESS_NUMBER_DEDICATED = "ISACCESSNUMBERDEDICATED";
    private static final String TON = "TON";


    private static final String SELECT_QRY = "SELECT " + FEATURE_ACCESS_INDEX + ", " + FEATURE_NAME + ", "
            + ACCESS_SIGNAL_TYPE + ", " + ACCESS_TRANSPORT_SUBSYSTEM + ", " + ROUTE_TO_SUBSYSTEM +
            ", " + SERVICED_BY_SUBSYSTEM + ", " + ACCESS_NUMBER_TYPE + ", " + MULTI_ACCES_SNUMBER_ENABLED + ", " + DESCRIPTION +
            ", " + IS_ACCESS_NUMBER_DEDICATED + ", " + TON + " FROM " + TABLENAME + " WHERE " + FEATURE_ACCESS_INDEX + "= ?";


    public KnFeatureAccessInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    /**
     * method to select the POC Registrar Service Config
     *
     * @param persistTxn KnPersisterTxn
     * @return KnPOCRegistrarSrvcConfigDTO
     * @throws KnDAOException
     *
     */
    public KnFeatureAccessInfoDTO selectFeatureAccessInfo(int featureAccIndex, KnPersisterTxn persistTxn) throws KnDAOException {

        String methodName = "selectFeatureAccessInfo(int, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnFeatureAccessInfoDTO featureAccessInfoDTO = new KnFeatureAccessInfoDTO();
        knLogger.info( methodName, "ENTRY: Select feature access info ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction ");
                ownedTxn = true;
            }

            query = SELECT_QRY;

            conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, featureAccIndex);

            knLogger.debug( methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query: Executed ");

            if (rs.next()) {
                featureAccessInfoDTO.setFeatureAccessIndex(rs.getInt(FEATURE_ACCESS_INDEX));
                featureAccessInfoDTO.setFeatureName(rs.getString(FEATURE_NAME));
                featureAccessInfoDTO.setAccessSignalType(rs.getString(ACCESS_SIGNAL_TYPE));
                featureAccessInfoDTO.setAccessTransportSubsystem(rs.getInt(ACCESS_TRANSPORT_SUBSYSTEM));
                featureAccessInfoDTO.setRoutetoSubsystem(rs.getInt(ROUTE_TO_SUBSYSTEM));
                featureAccessInfoDTO.setServicedBySubsystem(rs.getInt(SERVICED_BY_SUBSYSTEM));
                featureAccessInfoDTO.setAccessNumberType(rs.getInt(ACCESS_NUMBER_TYPE));
                featureAccessInfoDTO.setMultiAccessNumberEnabled(rs.getInt(MULTI_ACCES_SNUMBER_ENABLED));
                featureAccessInfoDTO.setDescription(rs.getString(DESCRIPTION));
                featureAccessInfoDTO.setAccessNumberDedicated(rs.getInt(IS_ACCESS_NUMBER_DEDICATED));
                featureAccessInfoDTO.setTon(rs.getInt(TON));

            } else {
                knLogger.error( methodName, "feature access info doesn't exist");
                if (ownedTxn) persistTxn.rollback();
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "feature access info Doesnt exist", pttServerId,
                        KnProvDAOSourceTypes.FEATUREACCESSINFO, query);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }
            knLogger.debug( methodName, "returning feature access info ", featureAccessInfoDTO);
            return featureAccessInfoDTO;

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception occurred");
            if (ownedTxn) persistTxn.rollback();
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception occurred");
            if (ownedTxn) persistTxn.rollback();
            throw KnDbUtil.processException(sqlE, "Failed to select feature access info - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.FEATUREACCESSINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            if (ownedTxn) persistTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to selectfeature access info  - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.FEATUREACCESSINFO, query);
        } finally {
             KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info( methodName, "EXIT : select Pfeature access info ");
        }
    }
}
