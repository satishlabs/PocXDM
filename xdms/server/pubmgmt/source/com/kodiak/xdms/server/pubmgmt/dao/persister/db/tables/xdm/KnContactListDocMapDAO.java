/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.sql.*;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnContactListDocMapDAO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 11, 2011           7.0
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
public class KnContactListDocMapDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnContactListDocMapDAO.class);


    public static final String CLASSNAME = KnContactListDocMapDAO.class.getName();
    public static final String TABLENAME = "DG.XDM_CONTACTLIST_DOCMAP";

    public static final String RESOURCE_LIST_DOC_ID = "RESOURCELISTDOCID";
    public static final String CONTACT_LIST_ID = "CONTACTLISTID";
    public static final String OWNER_MDN = "OWNERMDN";
    public static final String RESOURCELIST_ETAG = "RESOURCELIST_ETAG";

    public String pttServerId = null;

    public KnContactListDocMapDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    public static final String QRY_SELECT_CONTACT_LIST_ID = "SELECT " + CONTACT_LIST_ID + " FROM " + TABLENAME
            + " WHERE " + OWNER_MDN + " = ?";
    public static final String QRY_SELECT_CONTACT_LIST_IDS = "SELECT " + RESOURCE_LIST_DOC_ID + ", " +
            CONTACT_LIST_ID + " FROM " + TABLENAME + " WHERE " + RESOURCE_LIST_DOC_ID + " IN ";
    public static final String QRY_UPDATE_ETAG = "UPDATE " + TABLENAME + " SET " + RESOURCELIST_ETAG + " = ? "
            + " WHERE " + OWNER_MDN + " = ?";
    public static final String QRY_UPDATE_ETAGS_FOR_MDN = "UPDATE " + TABLENAME + " SET " + RESOURCELIST_ETAG + " = "
            + "(" + RESOURCELIST_ETAG + "+1)" + " WHERE " + OWNER_MDN + " = ?";
    public static final String QRY_UPDATE_MDN = "UPDATE " + TABLENAME + " SET " + OWNER_MDN + " = ? , "
            + RESOURCELIST_ETAG + " = (" + RESOURCELIST_ETAG + "+1)" + " WHERE " + OWNER_MDN + " = ?";
    public static final String QRY_SELECT_ETAG_FOR_MDN = "SELECT " + RESOURCELIST_ETAG + " FROM " + TABLENAME
            + " WHERE " + OWNER_MDN + " = ? ";

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
     *
     * @param mdn
     * @param readonly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getContactListIdForMDN(String mdn, boolean readonly,
                                      KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getContactListIdForMDN(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int contactListId = 0;
        try {
            query = QRY_SELECT_CONTACT_LIST_ID;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            if (rs.next()) {
                contactListId = rs.getInt(1);
            } else {
                // Throw back Exception
                knLogger.error(methodName, "No Contact Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Contact Info found. Query ->" + query);
            }


            knLogger.info(methodName, "Returning contactListId - ", contactListId);
            return contactListId;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve contactListId for resourceListId - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }

    }

    /**
     * @param resourceListIds
     * @param readonly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Integer> getContactListIdsForResourceListIds(Collection<Integer>
                                                                             resourceListIds, boolean readonly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getContactListIdsForResourceListIds(Collection<Integer>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        String query = null;

        Map<Integer, Integer> resourceVsContactListIdMap = null;

        try {

            StringBuffer buffer = new StringBuffer(200);
            buffer.append(QRY_SELECT_CONTACT_LIST_IDS).append(KnDbUtil.convertListToIntBuffer(resourceListIds));
            query = buffer.toString();

            //conn = persisterTxn.getDBConnection(pttServerId, readonly);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            statement = conn.createStatement();
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            rs = statement.executeQuery(query);
            knLogger.debug(methodName, "QUERY : Completed.");

            if (rs.next()) {
                resourceVsContactListIdMap = new HashMap<Integer, Integer>();
                do {
                    resourceVsContactListIdMap.put(rs.getInt(1), rs.getInt(2));
                } while (rs.next());
            } else {
                // Throw back exception
                knLogger.error(methodName, "No Contact Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Contact Info found. Query ->" + query);
            }


            knLogger.info(methodName, "Returning resourceListVscontactListIdMap - ", resourceVsContactListIdMap);
            return resourceVsContactListIdMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);

            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);

            throw KnDbUtil.processException(e, "Failed to retrieve contactListIds for resourceListIds - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
        }

    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateContactListDocMapEtagsForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateContactListDocMapEtagsForMdn(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ", KnGDPRTemplate.mdn(mdn));

        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;

        try {
            query = QRY_UPDATE_ETAGS_FOR_MDN;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ",
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed.", count);
            knLogger.info(methodName, "Updated Etags for Mdn:  ", KnGDPRTemplate.mdn(mdn));

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Etags for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }


    /**
     * @param mdn
     * @param newMdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateMdn(String mdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateMdn(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : Mdn :", KnGDPRTemplate.mdn(mdn), ", New Mdn :", KnGDPRTemplate.mdn(newMdn));

        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;

        try {
            query = QRY_UPDATE_MDN;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, newMdn);
            pStatement.setString(2, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed.", count);
            knLogger.info(methodName, "Updated Mdn and Etags for MDN:  ", KnGDPRTemplate.mdn(mdn));
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Mdn and etags for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }

    public int selectResourceListEtagForMdn(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "selectResourceListEtagForMdn(String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int etag = 0;

        try {

            query = QRY_SELECT_ETAG_FOR_MDN;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            if (rs.next()) {
                etag = rs.getInt(1);
            } else {
                // throw back exception
                knLogger.error(methodName, "No Contact Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Contact Info found. Query ->" + query);
            }
            knLogger.info(methodName, "Returning etag - ", etag);
            return etag;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve etag for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }
}
