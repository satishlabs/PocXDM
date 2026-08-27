/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/******************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 15, 2010       7.0
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
 * *******************************************************************************/
package com.kodiak.xdms.server.common.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnContactListPersistDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class KnXDMContactListDocMapDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMContactListDocMapDAO.class);

    private static final String className = KnXDMContactListDocMapDAO.class.getName();
    private String pttServerId;

    private static final String RESOURCE_LIST_DOCID = "RESOURCELISTDOCID";
    private static final String CONTACT_LIST_ID = "CONTACTLISTID";
    private static final String OWNERMDN = "OWNERMDN";
    private static final String RESOURCELIST_ETAG = "RESOURCELIST_ETAG";

    private static final String TABLENAME = "DG.XDM_CONTACTLIST_DOCMAP";

    private static final String INSERT_QRY = "INSERT INTO " + TABLENAME + "(" + RESOURCE_LIST_DOCID + ", " +
            CONTACT_LIST_ID + ", " + OWNERMDN + ", " + RESOURCELIST_ETAG + ") VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QRY = "UPDATE " + TABLENAME + " SET " + RESOURCELIST_ETAG + "= ? " +
            " WHERE " + RESOURCE_LIST_DOCID + "= ? AND " + OWNERMDN + "= ?";
    private static final String DELETE_QRY = "DELETE FROM " + TABLENAME + " WHERE " + OWNERMDN + "= ?";

    private static final String SELECT_QRY = "SELECT " + RESOURCELIST_ETAG + "= ? FROM " + TABLENAME + " WHERE " +
            OWNERMDN + "= ? AND " + RESOURCE_LIST_DOCID + "= ?";

    private static final String SELECT_RESLISTID_QRY = "SELECT MAX(" + RESOURCE_LIST_DOCID + ")+1 FROM " + TABLENAME;

    private static final String UPDATE_MDN_QRY = "UPDATE " + TABLENAME + " SET " + OWNERMDN + " = ? WHERE " + OWNERMDN + " = ?";

    public KnXDMContactListDocMapDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;

        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                persistTxn.open();
                ownedTxn = true;
            }
            KnContactListPersistDTO contactListDTO = (KnContactListPersistDTO) persistenceDTO;
            knLogger.debug(methodName, "Contact List Doc Map DTO - " + contactListDTO);
            int resourceListDocId = contactListDTO.getResourceListId();
            int contactListId = contactListDTO.getContactListId();
            String ownerMdn = contactListDTO.getMdn();
            int resourceListEtag = contactListDTO.getEtag();

            query = INSERT_QRY;

            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, resourceListDocId);
            pStmt.setInt(2, contactListId);
            pStmt.setString(3, ownerMdn);
            pStmt.setInt(4, resourceListEtag);

            knLogger.debug(methodName, "QUERY : Executing " + query + ", DTO - " + persistenceDTO +
                    ", persistTxn - " + persistTxn);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed.");

            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persistTxn.save();
            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception Occured");
            knLogger.error(methodName, dbConne);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(dbConne, "Failed to add mdn to contact List doc map - " + dbConne.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception Occured");
            knLogger.error(methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to add mdn to contact List doc map - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to add mdn to contact list doc map - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug(methodName, "EXIT : Add Mdn to Contact List doc map");
        }
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "update(IPersistenceDTO, persistenceDTO)";
        knLogger.debug(methodName, "Not Implemented");
    }

    /**
     * method which deletes the contact list doc map from the DB
     *
     * @param persistencDTO IPersistenceDTO
     * @param persistTxn    KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.info(methodName, "ENTRY: delete mdn from ContactList Doc Map ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                persistTxn.open();
                ownedTxn = true;
            }

            KnContactListPersistDTO contactListDTO = (KnContactListPersistDTO) persistencDTO;
            String ownerMdn = contactListDTO.getMdn();

            query = DELETE_QRY;
            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, ownerMdn);

            knLogger.debug(methodName, "QUERY : Executing " + query + ", mdn : " + KnGDPRTemplate.mdn(ownerMdn) + ", persistTxn : " + persistTxn);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed.");
            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persistTxn.save();
            }

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQLException occured");
            knLogger.error(methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to delete mdn from contactList Doc Map- " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete mdn from contactList Doc Map - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug(methodName, "EXIT : delete mdn from ContactList Doc Map - ");
        }
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * method to update the mdn of the Corp resource List index doc
     *
     * @param oldMdn       String
     * @param newMdn       String
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateMdn(String oldMdn, String newMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateMdn(String, String, KnPersisterTxn)";
        boolean ownedTxn = false;
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.info(methodName, "ENTRY: update old Mdn [" + oldMdn + "] with new Mdn [" + newMdn + "]");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = UPDATE_MDN_QRY;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, newMdn);
            pStmt.setString(2, oldMdn);

            knLogger.debug(methodName, "QUERY : Executing " + query + ", Old mdn : " + KnGDPRTemplate.mdn(oldMdn) +
                    ", and new mdn : " + KnGDPRTemplate.mdn(newMdn) + " persistTxn : " + persisterTxn);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY : Completed.");
            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }

        } catch (KnDAOException daoE) {
            knLogger.error(methodName, "DAO Exception - " + daoE);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQLException occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update contact list mdn - " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to update contact list mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug(methodName, "EXIT : update contact list mdn - ");
        }
    }

    public void insert(List<? extends IPersistenceDTO> persistenceDTOs, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(List<? extends IPersistenceDTO>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;

        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                persistTxn.open();
                ownedTxn = true;
            }
            List<KnContactListPersistDTO> contactListDTOs = (List<KnContactListPersistDTO>) persistenceDTOs;
            knLogger.debug(methodName, "Contact List Doc Map DTO - " + contactListDTOs.size());

            int resourceListEtag = contactListDTOs.get(0).getEtag();

            query = INSERT_QRY;

            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            for (KnContactListPersistDTO contactListPersistDTO : contactListDTOs) {
                pStmt.setInt(1, contactListPersistDTO.getResourceListId());
                pStmt.setInt(2, contactListPersistDTO.getContactListId());
                pStmt.setString(3, contactListPersistDTO.getMdn());
                pStmt.setInt(4, resourceListEtag);
                pStmt.addBatch();
            }
            knLogger.debug(methodName, "QUERY : Executing " + query + ", DTO - " + persistenceDTOs.size() +
                    ", persistTxn - " + persistTxn);
            pStmt.executeBatch();
            knLogger.debug(methodName, "QUERY : Completed.");

            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persistTxn.save();
            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception Occured");
            knLogger.error(methodName, dbConne);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(dbConne, "Failed to add mdns to contact List doc map - " + dbConne.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception Occured");
            knLogger.error(methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to add mdns to contact List doc map - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to add mdns to contact list doc map - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug(methodName, "EXIT : Add Mdns to Contact List doc map");
        }
    }

    /**
     * method to update the mdn of the Corp resource List index doc
     *
     * @param oldMdns      String []
     * @param newMdns      String []
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateMdn(List<String> oldMdns, List<String> newMdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateMdn(List<String>, List<String>, KnPersisterTxn)";
        boolean ownedTxn = false;
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.info(methodName, "ENTRY: update old Mdns [" + oldMdns.size() + "] with new Mdns [" + newMdns.size() + "]");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = UPDATE_MDN_QRY;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            for (int i = 0; i < oldMdns.size(); i++) {
                pStmt.setString(1, newMdns.get(i));
                pStmt.setString(2, oldMdns.get(i));
                pStmt.addBatch();
            }

            knLogger.debug(methodName, "QUERY : Executing " + query + ", Old mdn : " + oldMdns.size() +
                    ", and new mdn : " + newMdns.size() + " persistTxn : " + persisterTxn);
            pStmt.executeBatch();
            knLogger.debug(methodName, "QUERY : Completed.");
            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }

        } catch (KnDAOException daoE) {
            knLogger.error(methodName, "DAO Exception - " + daoE);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQLException occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update contact list doc map mdns - " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to update contact list doc map mdns - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug(methodName, "EXIT : update contact list doc map mdns - ");
        }
    }

    /**
     * method which deletes the contact list doc map from the DB
     *
     * @param mdns       List<String>
     * @param persistTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void delete(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(List<? extends IPersistenceDTO>], KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.info(methodName, "ENTRY: delete mdn from ContactList Doc Map ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                persistTxn.open();
                ownedTxn = true;
            }

            query = DELETE_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            for (String mdn : mdns) {
                pStmt.setString(1, mdn);
                pStmt.addBatch();
            }

            knLogger.debug(methodName, "QUERY : Executing " + query + ", persistTxn : " + persistTxn);
            pStmt.executeBatch();
            knLogger.debug(methodName, "QUERY : Completed.");
            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug(methodName, "saving the transaction ");
                persistTxn.save();
            }

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQLException occured");
            knLogger.error(methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to delete mdn from contactList Doc Map- " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete mdn from contactList Doc Map - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug(methodName, "EXIT : delete mdn from ContactList Doc Map - ");
        }
    }

    public int getMaxResourceListId(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMaxResourceListId(KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        int maxId = 0;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {

            knLogger.debug(methodName, "Before calling the query.");
            query = SELECT_RESLISTID_QRY;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ",
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            if (rs.next()) {
                maxId = rs.getInt(1);
            }


            knLogger.info(methodName, "Returning Max Resource list id- ", maxId);
            return maxId;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve Max Resource List Id " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLISTDOCMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT : max resourcelist id->", maxId);
        }

    }


}
