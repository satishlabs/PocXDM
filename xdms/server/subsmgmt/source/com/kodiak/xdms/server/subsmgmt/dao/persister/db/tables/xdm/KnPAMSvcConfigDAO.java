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
import com.kodiak.xdms.server.common.dto.common.KnPAMSvcConfigDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * *****************************************************************************
 * File name:   KnPAMSvcConfigDAO.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar           Feb 12, 2013       7.4
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


public class KnPAMSvcConfigDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPAMSvcConfigDAO.class);



    private static final String className = KnPAMSvcConfigDAO.class.getName();
    private String pttServerId;

    private static final String TABLENAME = "DG.PAM_SVC_CONFIG";

    private static final String PTT_SERVER_ID = "PTTSERVERID";

    private static final String MAX_SUBSCR_PER_ACCOUNT = "MAXSUBSCRPERACCOUNT";
    private static final String MAX_TXN_PER_BATCH = "MAXTXNPERBATCH";

    private static final String SELECT_ALL_QRY = "SELECT " + PTT_SERVER_ID + ", " + MAX_SUBSCR_PER_ACCOUNT +   ", "+MAX_TXN_PER_BATCH +
                        " FROM " + TABLENAME + " WHERE " + PTT_SERVER_ID + "= ?";

    public KnPAMSvcConfigDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public KnPAMSvcConfigDTO selectPAMSvcConfig( KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectPAMSvcConfig( KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnPAMSvcConfigDTO srvcConfigDTO = new KnPAMSvcConfigDTO();
        knLogger.info( methodName, "ENTRY: Select PAM SVC Config ");
        try {

            query = SELECT_ALL_QRY;

            conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, pttServerId);

            knLogger.debug( methodName, "Query: Executing - ", query);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query: Executed ");


            if (rs.next()) {
                srvcConfigDTO.setPttServerId(rs.getString(PTT_SERVER_ID));
                srvcConfigDTO.setMaxSubPerAccount(rs.getInt(MAX_SUBSCR_PER_ACCOUNT));
                srvcConfigDTO.setMaxTxnPerBatch(rs.getInt(MAX_TXN_PER_BATCH));

            } else {
                knLogger.error( methodName, "PAM Service config doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "PAM Service config Doesnt exist",
                        pttServerId, KnProvDAOSourceTypes.PAMSRVCONFIG, query);
            }

            knLogger.debug( methodName, "returning pam service Config ", srvcConfigDTO);
            return srvcConfigDTO;

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to select PAM Service config - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMSRVCONFIG, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select PAM Service config - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMSRVCONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info( methodName, "EXIT : select PAM Service config");
        }
    }
}
