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
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;

import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.logger.KnLogger;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.sql.*;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPOCGroupDocMapDAO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 13, 2011           7.0
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
public class KnPOCGroupDocMapDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPOCGroupDocMapDAO.class);

    public static final String CLASSNAME = KnPOCGroupDocMapDAO.class.getName();
    public static final String TABLENAME = "DG.XDM_POCGROUP_DOCMAP";
    public static final String GROUP_DOC_ID     = "GROUPDOCID";
    public static final String POC_GROUP_ID     = "POCGROUPID";
    public static final String OWNER_MDN        = "OWNERMDN";
    public static final String GROUP_DOC_ETAG   = "GROUPDOC_ETAG";


    public String pttServerId = null;
    KnGenInfoUtil genInfoUtil = null;

    public KnPOCGroupDocMapDAO(String pttServerId) {
        this.pttServerId = pttServerId;
        genInfoUtil = KnGenInfoUtil.getInstance();
    }

    public static final String QRY_INSERT = "INSERT INTO " + TABLENAME + " VALUES (?,?,?,?)";
    public static final String QRY_DELETE = "DELETE FROM " + TABLENAME + " WHERE " + POC_GROUP_ID + " = ?";

    public static final String QRY_SELECT_GROUP_ID = "SELECT " + POC_GROUP_ID + " FROM " + TABLENAME
            + " WHERE " + GROUP_DOC_ID + " = ?";

    public static final String QRY_SELECT_GROUP_IDS = "SELECT " + GROUP_DOC_ID + ", " + POC_GROUP_ID +
            " FROM " + TABLENAME + " WHERE " + GROUP_DOC_ID + " IN ";
    public static final String QRY_UPDATE_ETAG = "UPDATE " + TABLENAME + " SET " + GROUP_DOC_ETAG + " = ? "
            + " WHERE " + GROUP_DOC_ID + " = ?";
    public static final String QRY_SELECT_GROUP_DOC_ID = "SELECT " + GROUP_DOC_ID + " FROM " + TABLENAME
            + " WHERE " + POC_GROUP_ID + " = ?" + " AND " + OWNER_MDN + " = ?";
    public static final String QRY_UPDATE_ALL_ETAGS = "UPDATE " + TABLENAME + " SET " + GROUP_DOC_ETAG + " = "
            + "(" + GROUP_DOC_ETAG + "+1)" + " WHERE " + OWNER_MDN + " = ?";
    public static final String QRY_DELTE_ALL_GROUP_DOCS = "DELETE FROM " + TABLENAME + " WHERE " + POC_GROUP_ID + " IN ";
    public static final String QRY_UPDATE_MDN = "UPDATE " + TABLENAME + " SET " + OWNER_MDN + " = ? , " + GROUP_DOC_ETAG
            + " = (" + GROUP_DOC_ETAG + "+1)" + " WHERE " + OWNER_MDN + " = ?";
    public static final String QRY_SELECT_POC_GROUP_ID = "SELECT " + POC_GROUP_ID + " FROM " + TABLENAME
            + " WHERE " + GROUP_DOC_ID + " = ?" + " AND " + OWNER_MDN + " = ?";


    /**
     * This method is used for insertion of data in SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     *
     */
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "insert", "Not Implemented");
    }

    /**
     * This method is used for updation of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     *
     */
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "update", "Not Implemented");
    }

    /**
     * This method is used for deletion of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     *
     */
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "delete", "Not Implemented");
    }

    /**
     * This method is used for retrieving of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     * @return Collection
     */
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "select", "Not Implemented");
        return null;
    }


    /**
     *
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void insertGroupDocMap(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "insertGroupDocMap(KnPubGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        String mdn = groupInfoPersistDTO.getOwner();
        //   int etag = 0;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_INSERT;

            int pocGroupId = genInfoUtil.retrieveIdForTable(TABLENAME, pttServerId, POC_GROUP_ID, false, KnConstants.DUAL_DATA_STORE);

            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, groupInfoPersistDTO.getGroupDocId());
            pStatement.setInt(2, pocGroupId);
            pStatement.setString(3, mdn);
            pStatement.setInt(4, 1);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Inserted GroupDocMap for MDN:  " + KnGDPRTemplate.mdn(mdn));
            groupInfoPersistDTO.setGroupId(pocGroupId);
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to insert record for GroupDocMap - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to inset record for GroupDocMap - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdn(mdn));
        }

    }

    /**
     *
     * @param pocGroupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deletePocGroupDocMap(int pocGroupId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deletePocGroupDocMap(int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_DELETE;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, pocGroupId);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Delete Group for PocGroupId:  " + pocGroupId);
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Delete record for GroupDocMap - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete record for GroupDocMap - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStatement);
            knLogger.debug( methodName, "EXIT : PocGroupId ->" + pocGroupId);
        }
    }


    /**
     *
     * @param groupDocId
     * @param etag
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateEtagForPOCGroupDocMap(int groupDocId, int etag,
                                            KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateEtagForPOCGroupDocMap(int, int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_UPDATE_ETAG;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, etag);
            pStatement.setInt(2, groupDocId);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated Etag for groupDocId:  " + groupDocId);
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Etag for groupDocId - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Etag for groupDocId - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : groupDocId ->" + groupDocId);
        }

    }


    /**
     *
     * @param groupDocId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getPocGroupIdForGroupDocId(int groupDocId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPocGroupIdForGroupDocId(int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        int pocGroupId = 0;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_SELECT_GROUP_ID;

            //conn = persisterTxn.getDBConnection(pttServerId, true);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, groupDocId);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                pocGroupId = rs.getInt(1);
            } else {
                // Throw back exception
                knLogger.error( methodName, "No Group Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }

            if (ownedTxn){
                persisterTxn.save();
            }
            knLogger.info( methodName, "Returning pocGroupId - " + pocGroupId);
            return pocGroupId;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve pocGroupId for groupDocId - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve pocGroupId for groupDocId - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : pocGroupId ->" + pocGroupId);
        }

    }


    /**
     *
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getGroupDocIdForPocGroupId(int pocgroupId, String ownerMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupDocIdForPocGroupId(int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : PocGroupId:" + pocgroupId + ", OwnerMdn:" + KnGDPRTemplate.mdn(ownerMdn));

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        int groupDocId = 0;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_SELECT_GROUP_DOC_ID;

            //conn = persisterTxn.getDBConnection(pttServerId, true);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, pocgroupId);
            pStatement.setString(2, ownerMdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", pocGroupId: " + pocgroupId +
                    ", persisterTxn : " + persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                groupDocId = rs.getInt(1);
            } else {
                // Throw back exception
                knLogger.error( methodName, "No Group Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Returning groupDocId - " + groupDocId);
            return groupDocId;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve groupDocId for pocGroupId - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve groupDocId for pocGroupId - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : groupDocId ->" + groupDocId);
        }

    }

    /**
     *
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getGroupIdForGroupDocId(int groupDocId, String ownerMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupDocIdForPocGroupId(int, String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : PocGroupId:" + groupDocId + ", OwnerMdn:" + KnGDPRTemplate.mdn(ownerMdn));
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int pocGroupId = 0;
        try {
            query = QRY_SELECT_POC_GROUP_ID;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, groupDocId);
            pStatement.setString(2, ownerMdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", groupDocId: " + groupDocId +
                    ", persisterTxn : " + persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");
            if (rs.next()) {
                pocGroupId = rs.getInt(1);
            }
            knLogger.info( methodName, "Returning pocGroupId - " + pocGroupId);
            return pocGroupId;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve pocGroupId for groupDocId - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : pocGroupId ->" + pocGroupId);
        }
    }


    /**
     *
     * @param groupDocIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Integer> getPocGroupIdsForGroupDocIds(Collection<Integer> groupDocIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPocGroupIdForGroupDocId(Collection<Integer>, boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        String query = null;

        Map<Integer, Integer> grpDocVsGrpIds = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            StringBuffer buffer = new StringBuffer(200);
            buffer.append(QRY_SELECT_GROUP_IDS).append(KnDbUtil.convertListToIntBuffer(groupDocIds));
            query = buffer.toString();

            //conn = persisterTxn.getDBConnection(pttServerId, readOnly);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            statement = conn.createStatement();
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " + persisterTxn);
            rs = statement.executeQuery(query);
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                grpDocVsGrpIds = new HashMap<Integer, Integer>();
                do {
                    grpDocVsGrpIds.put(rs.getInt(1), rs.getInt(2));
                } while(rs.next());
            } else {
                // Throw back exception
                knLogger.error( methodName, "No Group Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }

            if (ownedTxn){
                persisterTxn.save();
            }
            knLogger.info( methodName, "Returning Map - " + grpDocVsGrpIds);
            return grpDocVsGrpIds;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve pocGroupIds for groupDocIds - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to retrieve pocGroupIds for groupDocIds - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            knLogger.debug( methodName, "EXIT : grpDocId Vs grpId Map ->" + grpDocVsGrpIds);
        }

    }


    /**
     *
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updatePOCGroupDocMapEtagsForMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updatePOCGroupDocMapEtagsForMdn(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : " + KnGDPRTemplate.mdn(mdn));

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_UPDATE_ALL_ETAGS;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn){
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated groupDocs Etags for Mdn:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update groupDocs Etags for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn){
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update groupDocs Etags for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : Mdn ->" + KnGDPRTemplate.mdn(mdn));
        }

    }


    /**
     *
     * @param pocGroupIds
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllPocGroupDocMaps(Collection<Integer> pocGroupIds, KnPersisterTxn
            persisterTxn) throws KnDAOException {

        final String methodName = "deleteAllPocGroupDocMaps(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug( methodName, pocGroupIds);
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        String query = null;

        try {
            StringBuffer buffer = new StringBuffer(200);
            buffer.append(QRY_DELTE_ALL_GROUP_DOCS).append(KnDbUtil.convertListToIntBuffer(pocGroupIds));
            query = buffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            statement = conn.createStatement();

            knLogger.debug( methodName, "QUERY : Executing " , query , ", persisterTxn : " ,
                    persisterTxn);
            statement.execute(query);
            knLogger.debug( methodName, "QUERY : Completed : ");
            knLogger.info( methodName, "Delete members from all groups :groupsIds:  " , pocGroupIds);
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to Delete all GroupDocs :groupsIds - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " ,e);
            throw KnDbUtil.processException(e, "Failed to Delete all GroupDocs :groupsIds - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            knLogger.debug(methodName, "EXIT");
        }
    }


    /**
     *
     * @param mdn
     * @param newMdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateMdn(String mdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateMdn(String, String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : Mdn :" + KnGDPRTemplate.mdn(mdn) + ", New Mdn :" + KnGDPRTemplate.mdn(newMdn));

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_UPDATE_MDN;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, newMdn);
            pStatement.setString(2, mdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed."+count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated Mdn and Etags for MDN:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Mdn and etags for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Mdn and etags for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUPDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdn(mdn));
        }
    }
}
