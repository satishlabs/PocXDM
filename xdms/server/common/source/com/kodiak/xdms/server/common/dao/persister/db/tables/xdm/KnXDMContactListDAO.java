/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/******************************************************************************
 * File name:   KnXDMContactListDAO.java
 * Subsystem:   Server Common
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
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class KnXDMContactListDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMContactListDAO.class);
    private static final String className = KnXDMContactListDAO.class.getName();
    private String pttServerId;

    private static final String OWNER_MDN = "OWNERMDN";
    private static final String CONTACT_LIST_ID = "CONTACTLISTID";

    private static final String TABLENAME = "DG.XDM_CONTACTLIST";

    private static final String INSERT_QRY = "INSERT INTO " + TABLENAME + "(" + OWNER_MDN + ", " + CONTACT_LIST_ID + ") " +
            " VALUES (?, ?)";
    private static final String DELETE_QRY = "DELETE FROM " + TABLENAME + " WHERE " + OWNER_MDN + "= ?";

    private static final String SELECT_QRY = "SELECT " + CONTACT_LIST_ID + " FROM " + TABLENAME + " WHERE " +
            OWNER_MDN + " = ?";

    private static final String UPDATE_MDN_QRY = "UPDATE " + TABLENAME + " SET " + OWNER_MDN + " = ? WHERE " + OWNER_MDN + " = ?";

    public KnXDMContactListDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(IPersistenceDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn = null;
        PreparedStatement prepStmt = null;
        String query = null;


        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "opening the transaction");
                persistTxn.open();
                ownedTxn = true;
            }
            KnContactListPersistDTO contactListDTO = (KnContactListPersistDTO) persistenceDTO;
            knLogger.debug( methodName, "Contact List DTO - " + contactListDTO);
            String ownerMdn = contactListDTO.getMdn();
            int contactListId = contactListDTO.getContactListId();

            query = INSERT_QRY;
            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            prepStmt = conn.prepareStatement(query);
            prepStmt.setString(1, ownerMdn);
            prepStmt.setInt(2, contactListId);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", mdn : " + KnGDPRTemplate.mdn(ownerMdn) + ", persistTxn : " + persistTxn);
            prepStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed.");
            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug( methodName, "saving the transaction ");
                persistTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception Occured");
            knLogger.error( methodName, dbConne);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(dbConne, "Failed to add mdn to contact List - " + dbConne.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception Occured");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to add mdn to contact List - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to add mdn to contact list- " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(prepStmt);
            knLogger.debug( methodName, "EXIT : Add Mdn to Contact List");
        }

    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "update(IPesistenceDTO, KnPesisterTxn)";
        knLogger.debug( methodName, "Method not Implemented");

    }

    /**
     * method to delete the contact list from the DB
     *
     * @param persistencDTO IPersistenceDTO
     * @param persistTxn    KnPersisterTxn
     * @throws KnDAOException DB Layer exception
     */
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(IPersistenceDTO, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStmt = null;
        String query = null;
        boolean ownedTxn = false;

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

            knLogger.debug( methodName, "QUERY : Executing " + query + ", ownerMdn : " + KnGDPRTemplate.mdn(ownerMdn) + ", persistTxn : " + persistTxn);
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed.");

            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug( methodName, "saving the transaction ");
                persistTxn.save();
            }

        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to delete mdn from contactList - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            knLogger.error( methodName, e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete mdn from contactList - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : delete mdn from ContactList - ");
        }
    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return null;
    }

    public Integer selectAll(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
       // return null;
        String methodName = "select(IPersistenceDTO, KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStmt = null;
        String query = null;
        ResultSet rs = null;
        Integer contactListId;

        try {

            KnContactListPersistDTO contactListDTO = (KnContactListPersistDTO) persistenceDTO;
            String ownerMdn = contactListDTO.getMdn();

            query = SELECT_QRY;
            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, ownerMdn);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", ownerMdn : " + KnGDPRTemplate.mdn(ownerMdn) + ", persistTxn : " + persistTxn);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                contactListId = rs.getInt(1);
            } else {
                knLogger.error( methodName, "contactListId not found for mdn - " + KnGDPRTemplate.mdn(ownerMdn));
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "contactListId not found");
            }
            knLogger.debug( methodName, "contactListId found - " + contactListId);

            // save the transaction only if its owned by the current method.
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            knLogger.error( methodName, sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to delete mdn from contactList - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            knLogger.error( methodName, e);
            throw KnDbUtil.processException(e, "Failed to delete mdn from contactList - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : delete mdn from ContactList - ");
        }
        return contactListId;
    }

    /**
     * method to update the mdn for the resource list index doc
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
        knLogger.info( methodName, "ENTRY: update old Mdn [" + KnGDPRTemplate.mdn(oldMdn) + "] with new Mdn [" + KnGDPRTemplate.mdn(newMdn) + "]");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = UPDATE_MDN_QRY;

            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, newMdn);
            pStmt.setString(2, oldMdn);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", Old mdn : " + KnGDPRTemplate.mdn(oldMdn) +
                    ", and new mdn : " + KnGDPRTemplate.mdn(newMdn) + " persistTxn : " + persisterTxn);
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed.");
            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug( methodName, "saving the transaction ");
                persisterTxn.save();
            }

        } catch (KnDAOException daoE) {
            knLogger.error( methodName, "DAO Exception - " + daoE);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update update contact list mdn - " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update contact list mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : update update contact list mdn - ");
        }
    }

    public void insert(List<? extends IPersistenceDTO> persistenceDTOs, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(List<? extends IPersistenceDTO> , KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn = null;
        PreparedStatement prepStmt = null;
        String query = null;


        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "opening the transaction");
                persistTxn.open();
                ownedTxn = true;
}
            List<KnContactListPersistDTO> contactListDTOs = (List<KnContactListPersistDTO>) persistenceDTOs;
            knLogger.debug( methodName, "Contact List DTO - " + contactListDTOs.size());

            query = INSERT_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            prepStmt = conn.prepareStatement(query);
            for (KnContactListPersistDTO contactListPersistDTO : contactListDTOs) {
                prepStmt.setString(1, contactListPersistDTO.getMdn());
                prepStmt.setInt(2, contactListPersistDTO.getContactListId());
                prepStmt.addBatch();
            }

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persistTxn : " + persistTxn);
            prepStmt.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed.");
            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug( methodName, "saving the transaction ");
                persistTxn.save();
            }

        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception Occured");
            knLogger.error( methodName, dbConne);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(dbConne, "Failed to add mdn to contact List - " + dbConne.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception Occured");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to add mdns to contact List - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to add mdns to contact list- " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(prepStmt);
            knLogger.debug( methodName, "EXIT : Add Mdns to Contact List");
        }

    }

    public void updateMdn(List<String> oldMdns, List<String> newMdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateMdn(List<String>, List<String>, KnPersisterTxn)";
        boolean ownedTxn = false;
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.info( methodName, "ENTRY: update old Mdns [" + oldMdns.size() + "] with new Mdns [" + newMdns.size() + "]");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = UPDATE_MDN_QRY;

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            for (int i = 0; i < oldMdns.size(); i++) {
                pStmt.setString(1, newMdns.get(i));
                pStmt.setString(2, oldMdns.get(i));
                pStmt.addBatch();
            }

            knLogger.debug( methodName, "QUERY : Executing " + query + " persistTxn : " + persisterTxn);
            pStmt.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed.");
            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug( methodName, "saving the transaction ");
                persisterTxn.save();
            }

        } catch (KnDAOException daoE) {
            knLogger.error( methodName, "DAO Exception - " + daoE);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update contact list mdns - " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update contact list mdns - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : update contact list mdns - ");
        }
    }

    public void delete(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(List<String> , KnPersisterTxn)";
        Connection conn = null;
        PreparedStatement pStmt = null;
        String query = null;
        boolean ownedTxn = false;

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

            knLogger.debug( methodName, "QUERY : Executing " + query + ", persistTxn : " + persistTxn);
            pStmt.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed.");

            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug( methodName, "saving the transaction ");
                persistTxn.save();
            }

        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to delete mdns from contactList - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            knLogger.error( methodName, e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete mdns from contactList - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CONTACTLIST, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : delete mdns from ContactList - ");
        }
    }

}
