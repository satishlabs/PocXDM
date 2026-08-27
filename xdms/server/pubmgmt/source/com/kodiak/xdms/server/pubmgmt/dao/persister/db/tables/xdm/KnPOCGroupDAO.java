/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupPersistDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPOCGroupDAO.java
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
public class KnPOCGroupDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPOCGroupDAO.class);

    public static final String CLASSNAME = KnPOCGroupDAO.class.getName();
    public static final String TABLENAME = "DG.XDM_POCGROUP";

    public static final String POC_GROUP_ID         = "POCGROUPID";
    public static final String OWNER_MDN            = "OWNERMDN";
    public static final String LIST_SERVICE_URI     = "LISTSERVICEURI";
    public static final String GROUP_TYPE           = "GROUPTYPE";

    public String pttServerId = null;

    public KnPOCGroupDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    public static final String QRY_INSERT = "INSERT INTO " + TABLENAME + " VALUES (?,?,?,?)";
    public static final String QRY_DELETE = "DELETE FROM " + TABLENAME + " WHERE " + POC_GROUP_ID + " = ?";
    public static final String QRY_DELETE_MDN = "DELETE FROM " + TABLENAME + " WHERE " + OWNER_MDN
            + " = ? AND " + LIST_SERVICE_URI + " = ?";
    public static final String QRY_SELECT_POC_GROUP_IDS = "SELECT " + POC_GROUP_ID + " FROM " + TABLENAME
            + " WHERE " + OWNER_MDN + " = ?";
    public static final String QRY_SELECT_ALL_POC_GROUPS = "SELECT " + POC_GROUP_ID + ", " + LIST_SERVICE_URI
            + " FROM " + TABLENAME + " WHERE " + OWNER_MDN + " = ?";
    public static final String QRY_DELETE_ALL_GROUPS = "DELETE FROM " + TABLENAME + " WHERE " + POC_GROUP_ID + " IN ";
    public static final String QRY_UPDATE_All_GROUPS_FOR_MDN = "UPDATE " + TABLENAME + " SET " + OWNER_MDN + " = ? "
            + ", " + LIST_SERVICE_URI + " = ? WHERE " + POC_GROUP_ID + " = ?";
    public static final String QRY_SELECT_POC_GROUP_IDS_4_MDNS = "SELECT " + POC_GROUP_ID + " FROM " + TABLENAME
            + " WHERE " + OWNER_MDN + " IN ";

    public static final String QRY_SELECT_LIST_URI_POC_GRP = " SELECT " + LIST_SERVICE_URI + " FROM " + TABLENAME
            + " WHERE " + OWNER_MDN + " = ?";

    /**
     * This method is used for insertion of data in SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "insert", "Not Implemented");
    }

    /**
     * This method is used for updation of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
     */
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "update", "Not Implemented");
    }

    /**
     * This method is used for deletion of data from SQL tables.
     *
     * @param persistenceDTO
     * @param persisterTxn
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
     * @param groupInfoPersistDTO
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void insertPocGroup(KnPubGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "insertPocGroup(KnPubGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        String mdn = groupInfoPersistDTO.getOwner();

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_INSERT;

            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);

            pStatement.setInt(1, groupInfoPersistDTO.getGroupId());
            pStatement.setString(2, mdn);
            pStatement.setString(3, groupInfoPersistDTO.getListServiceURI());
            pStatement.setInt(4, groupInfoPersistDTO.getGroupType());

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Inserted Group for MDN:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to insert record for Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
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
            throw KnDbUtil.processException(e, "Failed to inset record for Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdn(mdn));
        }

    }


    /**
     * @param pocGroupId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deletePocGroup(int pocGroupId, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deletePocGroup(int, KnPersisterTxn)";
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
            //conn = persisterTxn.getDBConnection(pttServerId, false);
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
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Delete record for Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
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
            throw KnDbUtil.processException(e, "Failed to delete record for Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : PocGroupId ->" + pocGroupId);
        }
    }


    /**
     * @param mdn
     * @param listServiceURI
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deletePocGroupForMdn(String mdn, String listServiceURI,
                                     KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "deletePocGroupForMdn(String, String, KnPersisterTxn)";
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

            query = QRY_DELETE_MDN;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            pStatement.setString(2, listServiceURI);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Delete Group for MDN:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Delete record for Group - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
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
            throw KnDbUtil.processException(e, "Failed to delete record for List - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : MDN ->" + KnGDPRTemplate.mdn(mdn));
        }
    }


    /**
     * @param pocGroupIds
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteAllPocGroups(Collection<Integer> pocGroupIds, KnPersisterTxn persisterTxn) throws KnDAOException {

        final String methodName = "deleteAllPocGroups(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug( methodName, pocGroupIds);
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        String query = null;

        try {
            StringBuffer buffer = new StringBuffer(200);
            buffer.append(QRY_DELETE_ALL_GROUPS).append(KnDbUtil.convertListToIntBuffer(pocGroupIds));
            query = buffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            statement = conn.createStatement();

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            statement.execute(query);
            knLogger.debug( methodName, "QUERY : Completed : ");
            knLogger.info( methodName, "Delete all groups :groupsIds:  " + pocGroupIds);
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to Delete all Groups :groupsIds - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to Delete all Groups :groupsIds - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            knLogger.debug(methodName, "EXIT");
        }
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<Integer> getPocGroupIdsForMdn(String mdn, boolean readonly, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "getPocGroupIdsForMdn(String, boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : " + KnGDPRTemplate.mdn(mdn));

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        Collection<Integer> pocGroupIds = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_SELECT_POC_GROUP_IDS;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //    conn = persisterTxn.getDBConnection(pttServerId, readonly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                pocGroupIds = new ArrayList<Integer>();
                do {
                    pocGroupIds.add(rs.getInt(1));
                } while(rs.next());
            } else {
                // Throw back Exception
                knLogger.debug( methodName, "No Group Info found");
//                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Returning pocGroupIds - " + pocGroupIds);
            return pocGroupIds;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve pocGroupIds for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
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
            throw KnDbUtil.processException(e, "Failed to retrieve pocGroupIds for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : pocGroupIds ->" + pocGroupIds);
        }
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnPubGroupPersistDTO> getAllPocGroupsForMdn(String mdn, boolean readonly, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        String methodName = "getAllPocGroupsForMdn(String, boolean, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : " + KnGDPRTemplate.mdn(mdn));

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        Collection<KnPubGroupPersistDTO> pocGroups = null;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_SELECT_ALL_POC_GROUPS;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            //    conn = persisterTxn.getDBConnection(pttServerId, readonly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                pocGroups = new ArrayList<KnPubGroupPersistDTO>();
                do {
                    KnPubGroupPersistDTO pocGroup = new KnPubGroupPersistDTO();
                    pocGroup.setGroupDocId(rs.getInt(1));
                    pocGroup.setListServiceURI(rs.getString(2));
                    pocGroup.setOwner(mdn);

                    pocGroups.add(pocGroup);
                } while(rs.next());
            } else {
                // Throw back Exception
                knLogger.debug( methodName, "No Group Info found");
//                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Returning pocGroups - " + pocGroups);
            return pocGroups;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve pocGroups for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
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
            throw KnDbUtil.processException(e, "Failed to retrieve pocGroups for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : pocGroups ->" + pocGroups);
        }
    }


    /**
     * @param pocGroups
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateAllPocGroupsForMdn(Collection<KnPubGroupPersistDTO> pocGroups, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateAllPocGroupsForMdn(Collection<KnPubGroupPersistDTO>, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : PocGroups :" + pocGroups);

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

            query = QRY_UPDATE_All_GROUPS_FOR_MDN;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);

            for (KnPubGroupPersistDTO pocGroup : pocGroups) {
                pStatement.setString(1, pocGroup.getOwner());
                pStatement.setString(2, pocGroup.getListServiceURI());
                pStatement.setInt(3, pocGroup.getGroupDocId());

                pStatement.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            pStatement.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed." );

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated PocGrous.");
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update PocGrous for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
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
            throw KnDbUtil.processException(e, "Failed to update PocGrous for MDN - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : ");
        }
    }

    /**
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<Integer> getPocGroupIdsForMdn(List mdns, boolean readonly, KnPersisterTxn persisterTxn) throws KnDAOException {

        final String methodName = "getPocGroupIdsForMdn(String, boolean, KnPersisterTxn)";
        knLogger.debug( methodName, mdns);
        Connection conn;
        Statement statement = null;
        ResultSet rs = null;
        String query = null;

        Collection<Integer> pocGroupIds = null;

        try {

            StringBuffer buffer = new StringBuffer(200);
            buffer.append(QRY_SELECT_POC_GROUP_IDS_4_MDNS).append(KnDbUtil.convertListToStringBuffer(mdns));
            query = buffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            statement = conn.createStatement();
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            rs = statement.executeQuery(query);
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                pocGroupIds = new ArrayList<Integer>();
                do {
                    pocGroupIds.add(rs.getInt(1));
                } while(rs.next());
            } else {
                // Throw back Exception
                knLogger.debug( methodName, "No Group Info found");
//                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }
            knLogger.info( methodName, "Returning pocGroupIds - " + pocGroupIds);
            return pocGroupIds;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve pocGroupIds for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve pocGroupIds for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
            knLogger.debug(methodName, "EXIT");
        }
    }

    /**
     * @param mdn
     * @param readonly
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<String> getAllListServiceUrisForMdn(String mdn, boolean readonly, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        String methodName = "getAllListServiceUrisForMdn(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : " + KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        Collection<String> serviceUri = null;
        try {
            query = QRY_SELECT_LIST_URI_POC_GRP;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readonly);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug(methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            if (rs.next()) {
                serviceUri = new ArrayList<String>();
                do {
                    serviceUri.add(rs.getString(1));
                } while (rs.next());
            } else {
                // Throw back Exception
                knLogger.debug(methodName, "No list service uris  Info found");
//                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found. Query ->" + query);
            }
            knLogger.info(methodName, "Returning listServiceURI's - " ,serviceUri);
            return serviceUri;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - " + e);

            throw KnDbUtil.processException(e, "Failed to retrieve list service uri for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);

            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to retrieve list service uri for Mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_POCGROUP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT : list service uri ->" + serviceUri);
        }
    }


}
