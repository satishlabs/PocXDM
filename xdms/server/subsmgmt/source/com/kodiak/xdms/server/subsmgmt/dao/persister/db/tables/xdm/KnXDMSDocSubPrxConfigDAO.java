/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMSDocSubPrxConfigDAO.java
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
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KnXDMSDocSubPrxConfigDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMSDocSubPrxConfigDAO.class);

    private static final String className = KnXDMSDocSubPrxConfigDAO.class.getName();
    private String pttServerId;

    private static final String TABLENAME = "DG.XDMS_DOCSUBPRX_CONFIG";

    private static final String PTT_SERVER_ID = "PTTSERVERID";

    private static final String PRIMARY_DOC_SUB_PRX_URI = "PRIMARYDOCSUBPRXURI";
    //private static final String GEO_DOC_SUB_PRX_URI = "GEODOCSUBPRXURI";
    private static final String DOC_SUBSCRIPTION_VALIDITY = "DOCSUBSCRIPTIONVALIDITY";
    //private static final String MAX_DOC_NTFY_MSG_BODY_SIZE = "MAXDOCNTFYMSGBODYSIZE";


    private static final String SELECT_ALL_QRY = "SELECT " + PTT_SERVER_ID + ", " + PRIMARY_DOC_SUB_PRX_URI + ", " + DOC_SUBSCRIPTION_VALIDITY  + " FROM " + TABLENAME + " WHERE " + PTT_SERVER_ID + "= ?";


    public KnXDMSDocSubPrxConfigDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public KnXDMSDocSubPrxConfigDTO selectXDMSDocSubPrxConfig(String presPttServerID,KnPersisterTxn persistTxn) throws KnDAOException {

        String methodName = "selectXDMSDocSubPrxConfig(String,KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnXDMSDocSubPrxConfigDTO xdmsDocSubPrxConfigDTO = new KnXDMSDocSubPrxConfigDTO();
        knLogger.info( methodName, "ENTRY: Select XDMS Doc Sub Prx Config ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction ");
                ownedTxn = true;
            }

            query = SELECT_ALL_QRY;

            conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, presPttServerID);

            knLogger.debug( methodName, "Query: Executing - " , query);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query: Executed ");


            if (rs.next()) {
                xdmsDocSubPrxConfigDTO.setPttServerId(rs.getString(PTT_SERVER_ID));
                xdmsDocSubPrxConfigDTO.setPrimaryDocSubPrxURI(rs.getString(PRIMARY_DOC_SUB_PRX_URI));
                //xdmsDocSubPrxConfigDTO.setGeoDocSubPrxURI(rs.getString(GEO_DOC_SUB_PRX_URI));
                xdmsDocSubPrxConfigDTO.setDocSubscriptionValidity(rs.getInt(DOC_SUBSCRIPTION_VALIDITY));
              //  xdmsDocSubPrxConfigDTO.setMaxDocNtfyMsgBodySize(rs.getInt(MAX_DOC_NTFY_MSG_BODY_SIZE));

            } else {
                knLogger.error( methodName, "XDMS Doc Sub Prx Config doesn't exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "XDMS Doc Sub Prx Config Doesnt exist",
                        pttServerId, KnProvDAOSourceTypes.XDMSDOCSUBPRXCONFIG, query);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }
            knLogger.debug( methodName, "returning dail plan info " , xdmsDocSubPrxConfigDTO);
            return xdmsDocSubPrxConfigDTO;

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
            throw KnDbUtil.processException(sqlE, "Failed to select XDMS Doc Sub Prx Config - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.XDMSDOCSUBPRXCONFIG, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to select XDMS Doc Sub Prx Config - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.XDMSDOCSUBPRXCONFIG, query);
        } finally {
             KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info( methodName, "EXIT : select XDMS Doc Sub Prx Config");
        }
    }
}

