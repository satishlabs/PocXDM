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
import com.kodiak.xdms.server.common.dto.common.KnSWPkgConfigDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * *****************************************************************************
 * File name:   KnSWPkgConfigDAO.java
 * Subsystem:  XDMS
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar           Mar 30, 2012       7.2
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

public class KnSWPkgConfigDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSWPkgConfigDAO.class);
    private static final String className = KnSWPkgConfigDAO.class.getName();
    private String pttServerId;

    private static final String TABLENAME = "DG.SWPKGCONFIGPARAMVALUE";

    private static final String SW_PKG_ID = "SWPKGID";
    private static final String INSTANCE_NUM = "INSTANCENUM";
    private static final String PARAM_NAME = "PARAMNAME";
    private static final String PTT_SERVER_ID = "PTTSERVERID";
    private static final String PARAM_VALUE = "PARAMVALUE";

    private static final String SELECT_ALL_QRY = "SELECT " + PTT_SERVER_ID + ", " + SW_PKG_ID + ", "
            + INSTANCE_NUM + ", " + PARAM_NAME + ", " + PARAM_VALUE + " "
            + " FROM " + TABLENAME + " WHERE " + PTT_SERVER_ID + "= ?";


    public KnSWPkgConfigDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    /**
     * method to select the SW Pkg Config
     *
     * @param persistTxn KnPersisterTxn
     * @return KnSWPkgConfigDTO
     * @throws KnDAOException
     */
    public ArrayList<KnSWPkgConfigDTO> selectSWPkgConfig(KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "selectSWPkgConfig( KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        KnSWPkgConfigDTO swPkgConfigDTO;
        ArrayList<KnSWPkgConfigDTO> swPkgConfigDTOs = new ArrayList<KnSWPkgConfigDTO>();

        knLogger.info( methodName, "ENTRY: Select SW Pkg Config ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction ");
                ownedTxn = true;
            }

            query = SELECT_ALL_QRY;

            conn = persistTxn.getDBConnection(pttServerId, true);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, pttServerId);

            knLogger.debug( methodName, "Query: Executing - " , query);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query: Executed ");

            while (rs.next()) {
                swPkgConfigDTO = new KnSWPkgConfigDTO();
                swPkgConfigDTO.setPttServerId(rs.getString(PTT_SERVER_ID));
                swPkgConfigDTO.setSwPkgId(rs.getInt(SW_PKG_ID));
                swPkgConfigDTO.setInstanceNum(rs.getInt(INSTANCE_NUM));
                swPkgConfigDTO.setParamName(rs.getString(PARAM_NAME));
                swPkgConfigDTO.setParamValue(rs.getString(PARAM_VALUE));
                swPkgConfigDTOs.add(swPkgConfigDTO);
            }

            if (swPkgConfigDTOs == null || swPkgConfigDTOs.isEmpty()) {
                knLogger.error( methodName, "SW Pkg Config doesn't exist");
                if (ownedTxn) {
                    persistTxn.rollback();
                }
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "SW Pkg Config Doesnt exist", pttServerId,
                        KnProvDAOSourceTypes.SWPKGCONFIG, query);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }
            knLogger.debug( methodName, "returning SW Pkg Config " , swPkgConfigDTOs);
            return swPkgConfigDTOs;

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
            throw KnDbUtil.processException(sqlE, "Failed to select SW Pkg Config  - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SWPKGCONFIG, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to selectSW Pkg Config  - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.SWPKGCONFIG, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.info( methodName, "EXIT : select SW Pkg Config ");
        }
    }


}
