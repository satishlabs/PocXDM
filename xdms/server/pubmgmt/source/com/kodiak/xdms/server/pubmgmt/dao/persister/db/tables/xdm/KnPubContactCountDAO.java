/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;


//rqHistoryBasedPresence_23 : Populating the DG.publicContactCount table

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class KnPubContactCountDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnPubContactCountDAO.class);

    private static final String TABLENAME = " DG.PUBLICCONTACTCOUNT";


    private static final String MDN = "MDN";
    private static final String PUBLIC_CONTACT_COUNT = "PUBLICCONTACTCOUNT";
    private static final String LAST_UPDATE_TIME = "LASTUPDATETIME";


    private String pttServerId = null;

    public KnPubContactCountDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public static final String QRY_INSERT = "INSERT INTO " + TABLENAME + " VALUES (?,?,?)";

    public static final String QRY_SELECT_COUNT = "SELECT " + PUBLIC_CONTACT_COUNT + " FROM " + TABLENAME
            + " WHERE " + MDN + " = ?";
    public static final String QRY_UPDATE = "UPDATE " + TABLENAME + " SET " + PUBLIC_CONTACT_COUNT + "=?," + LAST_UPDATE_TIME + "=?" + " WHERE " +
            MDN + " = ?";
    public static final String QRY_DELETE = "DELETE FROM " + TABLENAME + " WHERE " + MDN + "= ?";
    public static final String QRY_DELETE_COUNT_4_MDNS = "DELETE FROM " + TABLENAME + " WHERE " + MDN + " IN ";

    /**
     * This method is used for insertion of data in SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn("insert", "Not Implemented");
    }

    /**
     * This method is used for updation of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn("update", "Not Implemented");
    }

    /**
     * This method is used for deletion of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn("delete", "Not Implemented");
    }

    /**
     * This method is used for retrieving of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     * @return Collection
     */
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn("select", "Not Implemented");
        return null;
    }


    /**
     * @param mdn
     * @param publicCount
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void insertContactCount(String mdn, int publicCount, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertContactCount(insertContactCount, publicCount)";
        knLogger.debug(methodName);
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        try {
            query = QRY_INSERT;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            pStatement.setInt(2, publicCount);
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            pStatement.setLong(3, lastProfileUpdateTime);
            knLogger.debug(methodName, "QUERY : Executing " , query , ", mdn, publicCount,last profile time ", KnGDPRTemplate.mdn(mdn), publicCount, lastProfileUpdateTime,
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." , count);
            knLogger.info(methodName, "Inserted Public Contact Count  for MDN:  " , KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to insert record for Public Contact Count  - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to inset record for Public Contact Count  - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_PUBLICCONCTACTCOUNT, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdn(mdn));
        }

    }

    /**
     * @param mdn
     * @param publicCount
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateContactCount(String mdn, int publicCount, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateContactCount(String, Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ", KnGDPRTemplate.mdn(mdn), publicCount);
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = QRY_UPDATE;
            knLogger.debug(methodName, "QUERY : Executing " + query);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, publicCount);
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            pStatement.setLong(2, lastProfileUpdateTime);
            pStatement.setString(3, mdn);
            knLogger.debug(methodName, "QUERY : Executing " , query , ", persisterTxn : ", KnGDPRTemplate.mdn(mdn),
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed.", count);
            knLogger.info(methodName, "incremented public contact count for   " , KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - " , e);
            throw KnDbUtil.processException(e, "Failed increment public contact count - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_PUBLICCONCTACTCOUNT, query);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed increment public contact count - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_PUBLICCONCTACTCOUNT, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT : increment public contact count " , KnGDPRTemplate.mdn(mdn));
        }

    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getContactCount(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getContactCount(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int count = 0;
        try {
            query = QRY_SELECT_COUNT;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ",
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");
            if (rs.next()) {
                count = rs.getInt(1);
            } else {
                knLogger.error(methodName, "No record Info found");
                count = -1;
            }
            knLogger.debug(methodName, "Returning count - ", count);
            return count;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve count for dirdoc - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve count for dirdoc - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }

    }

    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteContactCount(String mdn,
                                   KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteContactCount(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : MDN ", KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;

        try {
            query = QRY_DELETE;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing " + query + ", persisterTxn : ", KnGDPRTemplate.mdn(mdn),
                    persisterTxn);
            int flag = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." , flag);
            knLogger.info(methodName, "Delete public contact count  for MDN  " , KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - " , e);

            throw KnDbUtil.processException(e, "Failed to Delete record for Public contact count  - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_PUBLICCONCTACTCOUNT, query);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to delete record  - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_PUBLICCONCTACTCOUNT, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT : MDN ->" , KnGDPRTemplate.mdn(MDN));
        }
    }


    /**
     * @param newMdn
     * @param oldMdn
     * @param persisterTxn
     * @throws KnDAOException
     * @int contactCount
     */
    public void UpdateContactMDN(String oldMdn, String newMdn, int contactCount,
                                 KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "UpdateContactMDN(String,String,int  KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : oldMdn,newMDN", KnGDPRTemplate.mdn(oldMdn),KnGDPRTemplate.mdn(newMdn));
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = QRY_DELETE;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, oldMdn);
            knLogger.debug(methodName, "QUERY : Executing " ,query , ", persisterTxn : " ,
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed." , count);
            knLogger.info(methodName, "Delete public contact count  for oldMDN  " ,KnGDPRTemplate.mdn(oldMdn));
            insertContactCount(newMdn, contactCount, persisterTxn);
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to Delete record for Public contact count  - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_PUBLICCONCTACTCOUNT, query);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to delete record  - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_PUBLICCONCTACTCOUNT, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT : MDN ->" ,KnGDPRTemplate.mdn(MDN));
        }
    }

    /**
     * @param mdns
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteContactCount(List mdns,
                                   KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deleteContactCount(String, KnPersisterTxn)";
        knLogger.debug(methodName,KnGDPRTemplate.mdnList(mdns));
        Connection conn;
        Statement statement = null;
        String query = null;

        try {

            StringBuffer buffer = new StringBuffer(200);
            buffer.append(QRY_DELETE_COUNT_4_MDNS).append(KnDbUtil.convertListToStringBuffer(mdns));
            query = buffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            statement = conn.createStatement();
            knLogger.debug(methodName, "QUERY : Executing " + query + ", persisterTxn : ", KnGDPRTemplate.mdnList(mdns),
                    persisterTxn);
            int flag = statement.executeUpdate(query);
            knLogger.debug(methodName, "QUERY : Completed." , flag);
            knLogger.info(methodName, "Delete public contact count  for MDN  " , KnGDPRTemplate.mdnList(mdns));
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - " , e);

            throw KnDbUtil.processException(e, "Failed to Delete record for Public contact count  - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_PUBLICCONCTACTCOUNT, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to delete record  - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_PUBLICCONCTACTCOUNT, query);
        } finally {
            KnDbUtil.closeStatement(statement);
        	knLogger.debug(methodName, "EXIT");
        }
    }

}