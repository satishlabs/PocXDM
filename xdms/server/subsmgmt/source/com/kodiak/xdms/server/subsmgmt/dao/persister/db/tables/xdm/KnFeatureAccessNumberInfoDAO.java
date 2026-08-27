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
import com.kodiak.xdms.server.common.dto.common.KnFeatureAccessNumberInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * *****************************************************************************
 * File name:   KnFeatureAccessNumberInfoDAO.java
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

public class KnFeatureAccessNumberInfoDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnFeatureAccessNumberInfoDAO.class);

    private static final String className = KnFeatureAccessNumberInfoDAO.class.getName();
    private String pttServerId;

    private static final String TABLENAME = "DG.FEATUREACCESSNUMBERINFO";

    private static final String ACCESS_NUMBER = "ACCESSNUMBER";
    private static final String ACCESS_NUMBER_INDEX = "ACCESSNUMBERINDEX";
    private static final String FEATURE_ACCESS_INDEX = "FEATUREACCESSINDEX";
    private static final String PTT_SERVER_ID = "PTTSERVERID";

    private static final String SELECT_QRY = "SELECT " + PTT_SERVER_ID + ", " + ACCESS_NUMBER + ", "
            + ACCESS_NUMBER_INDEX + ", " + FEATURE_ACCESS_INDEX + " FROM " + TABLENAME + " WHERE " + PTT_SERVER_ID + "= ? AND " + FEATURE_ACCESS_INDEX + "= ?";


    public KnFeatureAccessNumberInfoDAO(String pttServerId) {
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
    public KnFeatureAccessNumberInfoDTO selectFeatureAccessNumberInfo(int featureAccIndex, KnPersisterTxn persistTxn) throws KnDAOException {

        String methodName = "selectFeatureAccessNumberInfo(int, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnFeatureAccessNumberInfoDTO featureAccessNumberInfoDTO = new KnFeatureAccessNumberInfoDTO();
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
            pStmt.setString(1, pttServerId);
            pStmt.setInt(2, featureAccIndex);

            knLogger.debug( methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query: Executed ");

            if (rs.next()) {
                featureAccessNumberInfoDTO.setPttServerId(rs.getString(PTT_SERVER_ID));
                featureAccessNumberInfoDTO.setAccessNumber(rs.getString(ACCESS_NUMBER));
                featureAccessNumberInfoDTO.setAccessNumberIndex(rs.getInt(ACCESS_NUMBER_INDEX));
                featureAccessNumberInfoDTO.setFeatureAccessIndex(rs.getInt(FEATURE_ACCESS_INDEX));
            } else {
                knLogger.error( methodName, "feature access info doesn't exist");
                if (ownedTxn) persistTxn.rollback();
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "feature access info Doesnt exist", pttServerId,
                        KnProvDAOSourceTypes.FEATUREACCESSNUMBERINFO, query);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }
            knLogger.debug( methodName, "returning feature access info ", featureAccessNumberInfoDTO);
            return featureAccessNumberInfoDTO;

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception occurred");
            if (ownedTxn) persistTxn.rollback();
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception occurred");
            if (ownedTxn) persistTxn.rollback();
            throw KnDbUtil.processException(sqlE, "Failed to select feature access info - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.FEATUREACCESSNUMBERINFO, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            if (ownedTxn) persistTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to selectfeature access info  - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.FEATUREACCESSNUMBERINFO, query);
        } finally {
             KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info( methodName, "EXIT : select Pfeature access info ");
        }
    }
}
