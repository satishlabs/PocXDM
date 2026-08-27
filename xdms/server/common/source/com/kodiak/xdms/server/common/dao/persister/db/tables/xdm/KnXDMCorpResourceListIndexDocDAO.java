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
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnDocPersistDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnResourceListPersistDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class KnXDMCorpResourceListIndexDocDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpResourceListIndexDocDAO.class);
    private static final String className = KnXDMCorpResourceListIndexDocDAO.class.getName();
    private String pttServerId;

    private static final String RESOURCELIST_DOC_ID = "RESOURCELISTDOCID";
    private static final String MDN = "MDN";
    private static final String ETAG = "ETAG";

    private static final String TABLENAME = "DG.XDM_CORPRESOURCELISTINDEXDOC";

    private static final String SELECT_QRY = "SELECT ";
    private static final String INSERT_QRY = "INSERT INTO " + TABLENAME + "(" + RESOURCELIST_DOC_ID + ", " +
            MDN + ", " + ETAG + ") VALUES(?, ?, ?)";
    private static final String UPDATE_QRY = "UPDATE " + TABLENAME;
    private static final String DELETE_QRY = "DELETE FROM " + TABLENAME + " WHERE " + MDN + "= ?";
    private static final String UPDATE_MDN_QRY = "UPDATE " + TABLENAME + " SET " + MDN + " = ? WHERE " + MDN + " = ?";
    private static final String SELECT_MDN_COUNT = "SELECT COUNT(MDN) AS MDN_COUNT FROM " + TABLENAME + " WHERE " + MDN + " = ?";

    public KnXDMCorpResourceListIndexDocDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.info( methodName, "ENTRY: Add XDM directory with Txn - " + persistTxn);
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                persistTxn.open();
                ownedTxn = true;
            }

            KnDocPersistDTO resourceListDto = (KnDocPersistDTO) persistenceDTO;
            int resourceListDocId = resourceListDto.getDocId();
            String mdn = resourceListDto.getMdn();
            int etag = resourceListDto.getEtag();

            query = INSERT_QRY;

            conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, resourceListDocId);
            pStmt.setString(2, mdn);
            pStmt.setInt(3, etag);

            knLogger.debug( methodName, "Executing Query - " + query + ", DTO - " + persistenceDTO);
            pStmt.executeUpdate();
            knLogger.debug( methodName, "Executed Query ");

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }

        } catch (KnDAOException daoE) {
            knLogger.error( methodName, "DAO Exception Occured");
            knLogger.error( methodName, daoE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception Occured");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to add mdn to Corp Resource List Index doc - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            knLogger.error( methodName, e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to add mdn to Corp Resource List Index doc - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : Add Mdn mdn to Corp Resource List Index doc");
        }
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * method to delete the Corp Resource List Index Doc
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
        knLogger.info( methodName, "ENTRY: Delete XDM directory with Txn - " + persistTxn);
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                persistTxn.open();
                ownedTxn = true;
            }

            KnResourceListPersistDTO resourceListDto = (KnResourceListPersistDTO) persistencDTO;
            String mdn = resourceListDto.getMdn();

            query = DELETE_QRY;
            conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);

            knLogger.debug( methodName, "QUERY : Executing - " + query + ", DTO  - " + persistencDTO);
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed.");
            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug( methodName, "saving the transaction ");
                persistTxn.save();
            }

        } catch (KnDAOException daoE) {
            knLogger.error( methodName, "DAO Exception - " + daoE);
            knLogger.error( methodName, daoE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to delete mdn from Corp Resource List Index doc - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            knLogger.error( methodName, e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete mdn from Corp Resource List Index doc - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : delete mdn from Corp Resource List Index doc - ");
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
        knLogger.info( methodName, "ENTRY: update old Mdn [" + KnGDPRTemplate.mdn(oldMdn) + "] with new Mdn [" + KnGDPRTemplate.mdn(newMdn) + "]");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = UPDATE_MDN_QRY;
            conn = persisterTxn.getDBConnection(pttServerId, false);
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
            throw KnDbUtil.processException(sqlE, "Failed to corp resource list index doc mdn - " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to corp resource list index doc mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : update corp resource list index doc mdn - ");
        }
    }

    public void insert(List<? extends IPersistenceDTO> persistenceDTOs, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(List<? extends IPersistenceDTO>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.info( methodName, "ENTRY: Add XDM directory with Txn - " + persistTxn);
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                persistTxn.open();
                ownedTxn = true;
}

            List<KnDocPersistDTO> resourceListDtos = (List<KnDocPersistDTO>) persistenceDTOs;
            int etag = resourceListDtos.get(0).getEtag();

            query = INSERT_QRY;

            conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            for (KnDocPersistDTO docPersistDTO : resourceListDtos) {
                pStmt.setInt(1, docPersistDTO.getDocId());
                pStmt.setString(2, docPersistDTO.getMdn());
                pStmt.setInt(3, etag);
                pStmt.addBatch();
            }

            knLogger.debug( methodName, "Executing Query - " + query + ", DTO - " + persistenceDTOs.size());
            pStmt.executeBatch();
            knLogger.debug( methodName, "Executed Query ");

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }

        } catch (KnDAOException daoE) {
            knLogger.error( methodName, "DAO Exception Occured");
            knLogger.error( methodName, daoE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception Occured");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to add mdns to Corp Resource List Index doc - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            knLogger.error( methodName, e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to add mdns to Corp Resource List Index doc - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : Add mdns to Corp Resource List Index doc");
        }
    }

    /**
     * method to update the mdn of the Corp resource List index doc
     *
     * @param oldMdns      List<String>
     * @param newMdns      List<String>
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateMdn(List<String> oldMdns, List<String> newMdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateMdn(List<String>, List<String>, KnPersisterTxn)";
        boolean ownedTxn = false;
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.info( methodName, "ENTRY: update old Mdn [" + oldMdns.size() + "] with new Mdn [" + newMdns.size() + "]");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            query = UPDATE_MDN_QRY;
            conn = persisterTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            for (int i = 0; i < oldMdns.size(); i++) {
                pStmt.setString(1, newMdns.get(i));
                pStmt.setString(2, oldMdns.get(i));
                pStmt.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing " + query + ", Old mdn : " + oldMdns.size() +
                    ", and new mdn : " + newMdns.size() + " persistTxn : " + persisterTxn);
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
            throw KnDbUtil.processException(sqlE, "Failed to corp resource list index doc mdns - " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to corp resource list index doc mdns - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : update corp resource list index doc mdns - ");
        }
    }

    /**
     * method to delete the Corp Resource List Index Doc
     *
     * @param mdns       List<String>
     * @param persistTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void delete(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(List<String>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.info( methodName, "ENTRY: Delete XDM directory with Txn - " + persistTxn);
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                persistTxn.open();
                ownedTxn = true;
            }


            query = DELETE_QRY;
            conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            for (String mdn : mdns) {
                pStmt.setString(1, mdn);
                pStmt.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing - " + query + ", DTO  - " + mdns.size());
            pStmt.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed.");
            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug( methodName, "saving the transaction ");
                persistTxn.save();
            }

        } catch (KnDAOException daoE) {
            knLogger.error( methodName, "DAO Exception - " + daoE);
            knLogger.error( methodName, daoE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to delete mdn from Corp Resource List Index doc - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            knLogger.error( methodName, e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete mdn from Corp Resource List Index doc - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : delete mdn from Corp Resource List Index doc - ");
        }
    }

    /**
     * method to get the count of MDN in the Corp Resource List Index Doc
     *
     * @param mdn          String
     * @param persisterTxn KnPersisterTxn
     * @return int - count of MDN
     * @throws KnDAOException DB Layer Exception
     */
    public int getMdnCount(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMdnCount(String, KnPersisterTxn)";
        String query = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int mdnCount = 0;

        try {
            query = SELECT_MDN_COUNT;
            conn = persisterTxn.getDBConnection(pttServerId, false);
            stmt = conn.prepareStatement(query);
            stmt.setString(1, mdn);
            knLogger.debug(methodName, "Executing query: " + query + " with MDN: " + mdn);
            rs = stmt.executeQuery();

            if (rs.next()) {
                mdnCount = rs.getInt("MDN_COUNT");
            }
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception occurred", e);
            throw KnDbUtil.processException(e, "Failed to get MDN count for MDN: " + mdn + " - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_CORPRESOURCELISTINDEXDOC, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(stmt);
        }

        knLogger.debug(methodName, "MDN count for MDN " + mdn + ": " + mdnCount);
        return mdnCount;
    }

}
