/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnActClientDBEmsDAO.java
 * Subsystem:  Activation Library
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Rashmi Kamat         29-Oct-2010       6.4
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
package com.kodiak.library.activation.dao.persister.db;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.library.activation.dao.persister.IActClientEmsDAO;
import com.kodiak.common.dao.KnDAOException;

public class KnActClientDBEmsDAO implements IActClientEmsDAO {
    private static final String className = KnActClientDBWebDAO.class.getName();
    //stores the emsDSN
    private String emsDsn;

    /*private static final String QRY_SEL_XDMSERVER_ID = "SELECT A.PTTSERVERID FROM DG.SIGNALINGCARDINFO A," +
            " DG.SIGNALINGCARDADDLINFO B WHERE (B.SIGNALINGCARDID = A.SIGNALINGCARDID) AND B.SIGNALINGCARDTYPE = ?";*/

    public KnActClientDBEmsDAO() {
        this.emsDsn = KnDBConst.getEmsDBKey();
    }

       /**
     * this method returns POC XDM Server ID
     *
     * @param persisterTxn transaction object
     * @throws KnDAOException
     *          throws exception if client is not registered
     */
   /* public KnXdmServerInfoPersistDTO retrieveXdmServerId(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "retrieveXdmServerId(KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStatement;
        ResultSet rs;
        boolean ownedTxn = false;
        String geoSipGwId = null;
        KnXdmServerInfoPersistDTO xdmServerInfoDTO = new KnXdmServerInfoPersistDTO();
        knLogger.debug( methodName, "ENTRY -> retrieveXdmServerId()");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                ownedTxn = true;
                persisterTxn.open();
            }
            conn = persisterTxn.getDBConnection(emsDsn, true);
            pStatement = conn.prepareStatement(QRY_SEL_XDMSERVER_ID);
            pStatement.setShort(1, KnConstants.POC_XDM_CARD_TYPE);

            knLogger.debug( methodName, "QUERY : Executing " + QRY_SEL_XDMSERVER_ID + ", Txn : "
                    + persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                geoSipGwId = rs.getString(1);
                if(geoSipGwId != null)
                    xdmServerInfoDTO.setXdmServerId(geoSipGwId.trim());
                else{ //Exception
                }
            } else {
                knLogger.debug( methodName, "No record found for XDM Card Type: "+KnConstants.POC_XDM_CARD_TYPE);
            }
            //save the transaction if it is owned
            if (ownedTxn) persisterTxn.save();
            return xdmServerInfoDTO;
        } catch (KnDAOException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error( methodName, "exception - " + e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error( methodName, "exception occured - " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve XDM Server Id. Excep: " + e.getMessage(),
                    emsDsn, KnDAOSourceTypes.FETCH_XDMSERVER_ID, null);
        } finally {
            if (ownedTxn) persisterTxn = null;
            knLogger.debug( methodName, "EXIT : XDM Server ID");
        }
    }*/
}
