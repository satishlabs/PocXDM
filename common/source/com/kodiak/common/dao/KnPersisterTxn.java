/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnPersisterTxn.java
 * Subsystem:   Common DAO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 16, 2010       7.0
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
package com.kodiak.common.dao;

import com.kodiak.common.exception.KnSystemError;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.resources.KnErrorCodes;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnTransaction;
import com.kodiak.dbmgr.KnTransactionManager;
import java.sql.Connection;

public class KnPersisterTxn {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPersisterTxn.class);

    private static final String CLASS = KnPersisterTxn.class.getName();

    /**
     * Instance of DB transaction
     */
    KnTransaction dbTxn = null;

    /**
     * constructor
     */
    private KnPersisterTxn() {
    }

    /**
     * boolean flag for indicating an open transaction
     */
    private boolean txnOpened = false;

    /**
     * Return the instance of KnPersisterTxn.
     *
     * @return KnPersisterTxn object
     */
    public static synchronized KnPersisterTxn getPersisterTxn()
            throws KnPersistenceException {
        KnPersisterTxn persisterTxn = new KnPersisterTxn();
        knLogger.debug( "getPersisterTxn()", "Returning KnPersisterTxn - " , persisterTxn);
        return persisterTxn;
    }

    /**
     * Begins all kind of transctions corresponding to different datasources, which needs to be provided for the dao layer
     */
    public void open() throws KnPersistenceException {
        try {
            dbTxn = KnTransactionManager.getTransaction();
            dbTxn.begin();
            txnOpened = true;
//            KnNotifyAgentImpl.getAgent().registerObject(this, INotify.EXIT, INotifyAgent.NOTIFY_FOR_CURR_INTF);
            knLogger.info( "open", " " , this);
            KnTransactionStorage.getInstance().add(dbTxn);
        } catch (Exception e) {
            knLogger.error( "open", "Failed to open Transaction - " + this + ", exception - " + e);
            throw new KnPersistenceException(KnErrorCodes.DAO.TXN_START_FAILED, "Failed to Open txn " + this, e);
        }
    }

    /**
     * Saves and commits all kind of transactions opened for different datasources of dao layer.
     */
    public void save() throws KnPersistenceException {
        if (dbTxn != null) {
            try {
                dbTxn.commit();
                txnOpened = false;
                knLogger.info( "save", "DB Transaction Saved - " , this);
                KnTransactionStorage.getInstance().remove(dbTxn);
            } catch (Exception e) {
                knLogger.error( "save", "Failed to save Transaction - " + this + ", exception - " + e);
                throw new KnPersistenceException(KnErrorCodes.DAO.TXN_COMMIT_FAILED, "Failed to save txn " + this, e);
            }
        }
    }

    /**
     * Roll backs all all kind of transactions opened for different datasources of dao layer.
     */
    public void rollback() throws KnPersistenceException {
        if (dbTxn != null) {
            try {
                dbTxn.rollback();
                txnOpened = false;
                knLogger.info( "rollback", "Transaction rolled back - " , this);
                KnTransactionStorage.getInstance().remove(dbTxn);
            } catch (Exception e) {
                knLogger.error( "rollback", "Failed to rollback Transaction - " + this + ", exception - " + e);
                throw new KnPersistenceException(KnErrorCodes.DAO.TXN_ROLLBACK_FAILED, "Failed to rollback txn " + this, e);
            }
        }
    }

    /**
     * Return the DB connection object with in the transaction and for a given pttserver Id.
     *
     * @param pttServerId the pttServerId
     * @return Database connection object
     * @throws KnConnectionException exception
     */
    public Connection getDBConnection(String pttServerId, boolean readOnly) throws KnConnectionException {
        return getConnection(pttServerId, null, false);
    }

    /**
     * Return Dual DSN connection object with
     *
     * @param pttServerId the pttServerId
     * @param dualDSNIndx the dualDSNIndx indentifier
     * @return Database connection object
     * @throws KnConnectionException exception
     */
    public Connection getDBConnection(String pttServerId, KnDBConst.DataStores dualDSNIndx, boolean readOnly)
            throws KnConnectionException {
        knLogger.debug("getDBConnection () 1 pttServerId",pttServerId," dualDSNIndx ",dualDSNIndx,"readOnly ",readOnly);
        if (dualDSNIndx == null) {
            throw new KnConnectionException(KnErrorCodes.DAO.TXN_CONNECTION_NOT_AVAILABLE,
                    "Could not retrieve DB connection : - DUAL DataStore value is null.", null, pttServerId);
        } else {
            return getConnection(pttServerId, dualDSNIndx, readOnly);
        }
    }

    /**
     * Private utility method for getting DB connections
     *
     * @param pttServerId the pttServerId
     * @param dualDSNIndx the dualDSNIndx indentifier
     * @return Database connection object
     * @throws KnConnectionException
     *          exception
     */
    private Connection getConnection(String pttServerId, KnDBConst.DataStores dualDSNIndx, boolean readOnly)
            throws KnConnectionException {
        if (dbTxn != null) {
            try {
                if (dualDSNIndx == null)
                    return dbTxn.getConnection(pttServerId, readOnly);
                else
                    return dbTxn.getConnection(pttServerId, dualDSNIndx, readOnly);
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                knLogger.error( "getDBConnection", "Failed to get DB connection - " + this + ", exception - " + e);
                if (("PTT Server not reachable.").equalsIgnoreCase(errorMessage)) {
                    throw new KnConnectionException(KnErrorCodes.DAO.PTT_SERVER_NOT_REACHABLE,
                            "Connection not available for PTT server ID", e, pttServerId);
                } else if (("Server is busy. Please try later.").equalsIgnoreCase(errorMessage)) {
                    throw new KnConnectionException(KnErrorCodes.DAO.SERVER_BUSY,
                            "Server is busy. Please try again after sometime", e, pttServerId);
                }
                throw new KnConnectionException(KnErrorCodes.DAO.TXN_CONNECTION_NOT_AVAILABLE,
                        "Could not retrieve DB connection : ", e, pttServerId);
            }
        } else
            throw new KnSystemError(KnErrorCodes.DAO.TXN_NOT_STARTED,
                    "DB Transaction is not started : " + dbTxn);
    }

    public int getTransactionStatus(){
        final String methodName="getTransactionStatus()";
        int status =0;
        try{
            status = dbTxn.getStatus();
        }catch (Exception ex){
            knLogger.error(methodName,"Error occured  ",ex);
        }
        return status;
    }

    /**
     * Returns the state of the transaction.
     *
     * @return the state of the transaction
     */
    public enum STATE {
        //if changing this sequence or adding any more states,
        // please update the same in com.kodiak.dbmgr.KnTransaction class STATE enum too
        NEW, STARTED, COMMITED, ROLLBACK, COMMIT_FAIL, ROLLBACK_FAIL
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p/>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "KnPersisterTxn[" + hashCode() + "] -> dbTxn : " + dbTxn;
    }

    public boolean isTxnOpen() {
        return txnOpened;
    }

}
