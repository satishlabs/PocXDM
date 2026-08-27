/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMIntfDAO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker .P      Feb 23, 2012  7.2
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
 * ************************************************************************
 */
package com.kodiak.xdms.xdmintf.resources;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class KnXDMIntfDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMIntfDAO.class);
    private final String CLASS_NAME = KnXDMIntfDAO.class.getName();

    private final String APP_INTF_TABLE_NAME = "DG.APPLICATIONINTERFACE";

    private String xdmPttServerId = null;

    public KnXDMIntfDAO() {
        this.xdmPttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
    }

    /**
     * method to update or ACK the Application Interface table.
     * @param msgObj        KnMessage
     * @throws KnDAOException DB Layer Exception
     */
    public void updateAppIntfMsg(KnMessage msgObj) throws KnDAOException {
        String methodName = "updateAppIntfMsg(KnMessage)";
        knLogger.info( methodName, "ENTRY: Update APP Intf Msg with msg Obj - " , msgObj);
        PreparedStatement pStmt = null;
        Connection conn = null;
        KnPersisterTxn persisterTxn = null;
        String query = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug( methodName, "opening the transaction");
            persisterTxn.open();
            StringBuffer queryBuffer = new StringBuffer(700);
            queryBuffer.append("UPDATE ").append(APP_INTF_TABLE_NAME).append(" SET TARGETSUBSYSTEMID=?");
            queryBuffer.append(" WHERE ").append(" SRCID=? AND TXNID=?");

            query = queryBuffer.toString();

            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, KnConstants.IPC_SUBSYS_XDMS);
            pStmt.setString(2, msgObj.getSrcIPAddress());
            pStmt.setString(3, msgObj.getCorrelationId());
            knLogger.debug( methodName, "QUERY: Executing - " , query , " with DTO - " , msgObj);
            int result = pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed - " , result);

            knLogger.debug( methodName, "Saving the transaction");
            persisterTxn.save();
            knLogger.info( methodName, "EXIT : updated entry for message - " , msgObj);
        } catch (Exception e) {
            if (persisterTxn != null)
                persisterTxn.rollback();
            knLogger.error( methodName, "Exception ... " , e);
            throw new KnDAOException("", e.getMessage(), e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }


    /**
     * This method is used for deletion of data from Application Interface Table tables.
     *
     * @param msgDTO KnMessage
     * @throws KnDAOException DB Layer Exception
     */
    public void deleteAppIntfMsg(KnMessage msgDTO) throws KnDAOException {
        String methodName = "delete(KnMessage)";
        knLogger.debug( methodName, "Entry: delete message - " , msgDTO);
        PreparedStatement pStmt = null;
        Connection conn = null;
        KnPersisterTxn persisterTxn = null;
        String query = null;
        try {
            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug( methodName, "opening the transaction");
            persisterTxn.open();
            StringBuffer queryBuffer = new StringBuffer(700);
            queryBuffer.append("DELETE FROM ").append(APP_INTF_TABLE_NAME).append(" WHERE ");
            queryBuffer.append(" SRCID=? AND TXNID=?");

            query = queryBuffer.toString();

            conn = persisterTxn.getDBConnection(xdmPttServerId, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, msgDTO.getDestIPAdress());
            pStmt.setString(2, msgDTO.getCorrelationId());
            knLogger.debug( methodName, "QUERY: Executing - " , query , " with DTO - " , msgDTO);
            int result = pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed - " , result);
            
            if (result <= 0) {
            	knLogger.warn(methodName, "No Record found for Deletion");
            }

            knLogger.debug( methodName, "Saving the transaction");
            persisterTxn.save();
            knLogger.info( methodName, "EXIT : Deleted entry for message - " , msgDTO);
        } catch (Exception e) {
            if (persisterTxn != null)
                persisterTxn.rollback();
            knLogger.error( methodName, "Exception ... " , e);
            throw new KnDAOException("", e.getMessage(), e);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

}
