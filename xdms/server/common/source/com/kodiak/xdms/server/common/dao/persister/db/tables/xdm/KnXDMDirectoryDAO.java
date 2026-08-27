/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/******************************************************************************
 * File name:   KnXDMDirectoryDAO.java
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
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnDocPersistDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import static com.kodiak.xdms.server.common.util.KnGeneralUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.common.util.KnGeneralUtil.replaceContactWithValue;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KnXDMDirectoryDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMDirectoryDAO.class);
    private static final String className = KnXDMDirectoryDAO.class.getName();
    private String pttServerId;

    private static final String DIRDOCID = "DIRDOCID";
    private static final String MDN = "MDN";
    private static final String ETAG = "ETAG";

    private static final String TABLENAME = "DG.XDM_DIRECTORY";

    private static final String SELECT_QRY = "SELECT " + MDN + ", " + DIRDOCID + ", " + ETAG +
            "WHERE " + MDN + "= ?";
    private static final String INSERT_QRY = "INSERT INTO " + TABLENAME + "(" + DIRDOCID + ", " + MDN + ", " + ETAG + ") " +
            "VALUES (?, ?, ?)";
    private static final String DELETE_QRY = "DELETE FROM " + TABLENAME + " WHERE " + MDN + "= ?";

    private static final String UPDATE_QRY = "UPDATE " + TABLENAME + " SET " + ETAG + "= ? WHERE " + MDN + "= ?";

    public static final String QRY_UPDATE_ETAG = "UPDATE " + TABLENAME + " SET " + ETAG + " = " + (ETAG + "+ 1")
            + " WHERE " + MDN + " = ?";

    public static final String QRY_SELECT_ETAG = "SELECT " + ETAG + " FROM " + TABLENAME
            + " WHERE " + MDN + " = ?";

    public static final String QRY_SELECT_ETAGS = "SELECT " + ETAG + " FROM " + TABLENAME
            + " WHERE " + MDN + " IN ";

    private static final String UPDATE_MDN_QRY = "UPDATE " + TABLENAME + " SET " + MDN + " = ? WHERE " + MDN + " = ?";
    
    private static final String SELECT_DIRECTORY_ETAG ="SELECT MDN,ETAG FROM DG.XDM_DIRECTORY WHERE MDN IN ";

    public static final String QRY_UPDATE_LIST_OF_MDN_ETAG = "UPDATE " + TABLENAME + " SET " + ETAG + " = " + (ETAG + "+ 1")
            + " WHERE " + MDN + " IN ";

    public KnXDMDirectoryDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.info( methodName, "ENTRY : Add Mdn to XDM Directory ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                persistTxn.open();
                ownedTxn = true;
            }

            KnDocPersistDTO docDto = (KnDocPersistDTO) persistenceDTO;

            int dirDocId = docDto.getDocId();
            String mdn = docDto.getMdn();
            int etag = docDto.getEtag();

            query = INSERT_QRY;

            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, dirDocId);
            pStmt.setString(2, mdn);
            pStmt.setInt(3, etag);

            knLogger.debug( methodName, "Query: Executing - " + query + ", persister DTO - " + persistenceDTO +
                    " Persister Txn - " + persistTxn);
            pStmt.executeUpdate();
            knLogger.debug( methodName, " Query: Executed ");

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }

        } catch (KnDAOException daoE) {
            knLogger.error( methodName, "DAO Exception Occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception Occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to add mdn to Directory - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to add mdn to Directory - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : Add Mdn to XDM Directory ");
        }
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateEtagForDirDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateEtagForDirDoc(String, String, int, KnPersisterTxn)";
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
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated Etag for mdn:  " + KnGDPRTemplate.mdn(mdn));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Etag for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
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
            throw KnDbUtil.processException(e, "Failed to update Etag for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : mdn ->" + KnGDPRTemplate.mdn(mdn));
        }
    }

    /**
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getCurrentEtagForDirDoc(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCurrentEtagForDirDoc(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        knLogger.info( methodName, "ENTRY: retrieve Current Dir Doc Etag for MDN - " + KnGDPRTemplate.mdn(mdn) + " with txn - " + persisterTxn
                );
        int etag = 0;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_SELECT_ETAG;
            if (ownedTxn) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
                //conn = persisterTxn.getDBConnection(pttServerId, true);
            } else {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                //conn = persisterTxn.getDBConnection(pttServerId, false);
            }
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                etag = rs.getInt(1);
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Returning Etag - " + etag);
            return etag;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve etag for dirdoc - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
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
            throw KnDbUtil.processException(e, "Failed to retrieve etag for dirdoc - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : etag ->" + etag);
        }

    }

    public Map<String, Integer> getCurrentEtagForDirDoc(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCurrentEtagForDirDoc(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: retrieve Current Dir Doc Etag for MDN list");

        Map<String, Integer> etagMap = new HashMap<>();
        if (mdnList == null || mdnList.isEmpty()) {
            knLogger.debug(methodName, "EXIT: empty mdnList, nothing to fetch");
            return etagMap;
        }

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

            if (ownedTxn) {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            } else {
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            }

            StringBuilder buffer = new StringBuilder(SELECT_DIRECTORY_ETAG.length() + 2 + mdnList.size() * 3);
            buffer.append(SELECT_DIRECTORY_ETAG)
                    .append("(")
                    .append(com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList))
                    .append(")");
            query = buffer.toString();

            pStatement = conn.prepareStatement(query);
            int index = 1;
            for (String mdn : mdnList) {
                pStatement.setString(index++, mdn);
            }
            knLogger.debug(methodName, "QUERY : Executing " + query + ", persisterTxn : " + persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed.");

            while (rs.next()) {
                etagMap.put(rs.getString(1), rs.getInt(2));
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info(methodName, "Returning etagMap of size - " + etagMap.size());
            return etagMap;
        } catch (SQLException e) {
            knLogger.error(methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve etag for dirdoc - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve etag for dirdoc - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug(methodName, "EXIT : etagMap size ->" + etagMap.size());
        }

    }

    /**
     * this method will not be same as the above getCurrentDirEtagForUpdate method.
     * However, retaining the same as this method is used across multiple APIs
     * and would require re-testing of all those APIs. Fix for PR INT98761
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getCurrentDirEtagForUpdate(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCurrentDirEtagForUpdate(String, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        knLogger.debug( methodName, "ENTRY: retrieve Current Dir Doc Etag for MDN - " + KnGDPRTemplate.mdn(mdn) + " with txn - " + persisterTxn
                );
        int etag = 0;

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            query = QRY_SELECT_ETAG;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persisterTxn.getDBConnection(pttServerId, false);

            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " +
                    persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                etag = rs.getInt(1);
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Returning Etag - " + etag);
            return etag;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to retrieve etag for dirdoc - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
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
            throw KnDbUtil.processException(e, "Failed to retrieve etag for dirdoc - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.info( methodName, "EXIT : ", "mdn", KnGDPRTemplate.mdn(mdn), "etag ->" + etag);
        }

    }


    /**
     * method to the delete MDN From the XDM Directory
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
        knLogger.info( methodName, "Delete the XDM Directory with DTO - " + persistencDTO + ", " +
                "Txn - " + persistTxn);
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "opening the transaction");
                persistTxn.open();
                ownedTxn = true;
            }

            KnDocPersistDTO docPersistDto = (KnDocPersistDTO) persistencDTO;
            String mdn = docPersistDto.getMdn();

            query = DELETE_QRY;
            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", mdn : " + KnGDPRTemplate.mdn(mdn) + ", persistTxn : " + persistTxn);
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed.");
            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug( methodName, "saving the transaction ");
                persistTxn.save();
            }

        } catch (KnDAOException daoE) {
            knLogger.error( methodName, "DAO Exception - " + daoE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to delete mdn from Directory - " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete mdn from Directory - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : delete mdn from Directory - ");
        }
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * method to update the old mdn of directory doc to the new mdn
     *
     * @param oldMdn     String
     * @param newMdn     String
     * @param persistTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateMdn(String oldMdn, String newMdn, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "updateMdn(String, Stirng, KnPersisterTxn)";
        boolean ownedTxn = false;
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.info( methodName, "ENTRY: update old Mdn [" + KnGDPRTemplate.mdn(oldMdn) + "] with new Mdn [" + KnGDPRTemplate.mdn(newMdn) + "]");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the transaction");
                persistTxn.open();
                ownedTxn = true;
            }

            query = UPDATE_MDN_QRY;
            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, newMdn);
            pStmt.setString(2, oldMdn);

            knLogger.debug( methodName, "QUERY : Executing " + query + ", Old mdn : " + KnGDPRTemplate.mdn(oldMdn) +
                    ", and new mdn : " + KnGDPRTemplate.mdn(newMdn) + " persistTxn : " + persistTxn);
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
            knLogger.error( methodName, "SQLException occurred");
            knLogger.error( methodName, sqlE);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update  mdn from Directory - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            knLogger.error( methodName, e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update mdn from Directory - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : update mdn from Directory - ");
        }
    }

    public void insert(List<? extends IPersistenceDTO> persistenceDTOs, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(List<? extends IPersistenceDTO>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.info( methodName, "ENTRY : Add Mdn to XDM Directory ");
        try {

            List<KnDocPersistDTO> docDtos = (List<KnDocPersistDTO>) persistenceDTOs;

            int etag = docDtos.get(0).getEtag();

            query = INSERT_QRY;

            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            for (KnDocPersistDTO docPersistDTO : docDtos) {
                pStmt.setInt(1, docPersistDTO.getDocId());
                pStmt.setString(2, docPersistDTO.getMdn());
                pStmt.setInt(3, etag);
                pStmt.addBatch();
            }
            knLogger.debug( methodName, "Query: Executing - " + query + ", persister DTO - " + persistenceDTOs.size() +
                    " Persister Txn - " + persistTxn);
            pStmt.executeBatch();
            knLogger.debug( methodName, " Query: Executed ");

            knLogger.debug( methodName, "EXIT : Add Mdns to XDM Directory ");

        } catch (KnDAOException daoE) {
            knLogger.error( methodName, "DAO Exception Occured");
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error( methodName,"SQL Exception Occured");
            throw KnDbUtil.processException(sqlE, "Failed to add mdns to Directory - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } catch (Exception e) {
            knLogger.error( methodName,"Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to add mdns to Directory - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    /**
     * method to update the old mdn of directory doc to the new mdn
     *
     * @param oldMdns    String
     * @param newMdns    String
     * @param persistTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updateMdn(List<String> oldMdns, List<String> newMdns, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "updateMdn(List<String>, List<Stirng>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.info( methodName, "ENTRY: update old Mdn [" + oldMdns.size() + "] with new Mdn [" + newMdns.size() + "]");
        try {
             query = UPDATE_MDN_QRY;
            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            for (int i = 0; i < oldMdns.size(); i++) {
                pStmt.setString(1, newMdns.get(i));
                pStmt.setString(2, oldMdns.get(i));
                pStmt.addBatch();
            }

            knLogger.debug( methodName,"QUERY : Executing " + query + ", Old mdn : " + oldMdns.size() +
                    ", and new mdn : " + newMdns.size() + " persistTxn : " + persistTxn);
            pStmt.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed.");
            knLogger.debug( methodName, "EXIT : update mdns from Directory - ");

        } catch (KnDAOException daoE) {
            knLogger.error( methodName, "DAO Exception - " + daoE);
            knLogger.error( methodName, daoE);
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error( methodName,"SQLException occurred");
            knLogger.error( methodName, sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to update mdns from Directory - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e );
            knLogger.error( methodName, e);
            throw KnDbUtil.processException(e, "Failed to update mdns from Directory - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    /**
     * method to the delete MDN From the XDM Directory
     *
     * @param mdns       List<String>
     * @param persistTxn KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void delete(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(List<? extends IPersistenceDTO>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.info( methodName, "Delete the XDM Directory with DTO - " + mdns.size() + ", " +
                "Txn - " + persistTxn);
        try {
             query = DELETE_QRY;
            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            for (String mdn : mdns) {
                pStmt.setString(1, mdn);
                pStmt.addBatch();
            }
            knLogger.debug( methodName,  "QUERY : Executing " , query);
            pStmt.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed.");
            // save the transaction only if its owned by the current method.
            knLogger.debug( methodName, "EXIT : delete mdns from Directory - ");

        } catch (KnDAOException daoE) {
            knLogger.error( methodName, "DAO Exception - ", daoE);
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            throw KnDbUtil.processException(sqlE, "Failed to delete mdns from Directory - " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to delete mdns from Directory - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }


    /**
     * @param mdns
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void updateEtagForDirDoc(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateEtagForDirDoc(String, String, int, KnPersisterTxn)";
        knLogger.debug( methodName,"Entry : ");

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;

        try {

            query = QRY_UPDATE_ETAG;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            for (String mdn : mdns) {
                pStatement.setString(1, mdn);
                pStatement.addBatch();
            }
            knLogger.debug( methodName, "QUERY : Executing " , query , ", persisterTxn : " ,
                    persisterTxn);
            int[] count = pStatement.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed." , count.length);


            knLogger.info( methodName, "Updated Etag for mdns:  " ,mdns.size());
        } catch (SQLException e) {
            knLogger.error( methodName,"SQL Exception - " , e);

            throw KnDbUtil.processException(e, "Failed to update Etag for mdns - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - ", e);

            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to update Etag for mdns- " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : mdn ->" , mdns.size());
        }
    }

    /**
     * @param mdns
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getCurrentEtagsForDirDoc(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCurrentEtagsForDirDoc(List<String>, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : ");

        Connection conn;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        knLogger.info( methodName, "ENTRY: retrieve Current Dir Doc Etags for MDNs - " , mdns.size() , " with txn - " , persisterTxn );
        List<Integer> etags = new ArrayList<>();

        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            StringBuilder buffer = new StringBuilder(200);
            buffer.append(QRY_SELECT_ETAGS).append("(").append(com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdns)).append(")");
            query = buffer.toString();
            statement = conn.prepareStatement(query);
            for(String mdn:mdns){
                statement.setString(index++, mdn);
            }
            rs = statement.executeQuery();
            knLogger.debug( methodName, "QUERY : Executing " , query , ", persisterTxn : " ,
                    persisterTxn);
            //rs = statement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                etags = new ArrayList<Integer>();
                do {
                    etags.add(rs.getInt(1));
                } while (rs.next());
            }


            return etags;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " , e);

            throw KnDbUtil.processException(e, "Failed to retrieve etag for dirdoc - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " ,e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            throw KnDbUtil.processException(e, "Failed to retrieve etag for dirdoc - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);

            knLogger.debug( methodName, "EXIT : etags ->" , etags);
        }

    }
    
    public Map<String, Integer> getAndUpdateDirectoryEtag(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getAndUpdateDirectoryEtag(persisterTxn)";
        knLogger.debug( methodName, "ENTRY Point : mdnList -" , mdnList == null ? 0 : mdnList.size());

        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String selectQuery = null;
        String updateQuery = null;
        Map<String, Integer> directoryEtagMap = new HashMap<>();

        try {
            if (mdnList == null || mdnList.isEmpty()) {
                return directoryEtagMap;
            }
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            List<String> subsList = new ArrayList<String>(mdnList);
            Collections.sort(subsList);
            knLogger.debug( methodName, "MDN list after sorting  - " , KnGDPRTemplate.mdnList(subsList));
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            StringBuilder strBuffer = new StringBuilder(200);
            strBuffer.append(SELECT_DIRECTORY_ETAG).append("(");
            strBuffer.append(formCommaSeperatedIdList(subsList)).append(");");
            selectQuery = strBuffer.toString();
            knLogger.debug( methodName, "Executing query- " , selectQuery );
            pstmt = conn.prepareStatement(selectQuery);
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully ");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                directoryEtagMap.put(mdn.trim(), rs.getInt(2));
            }

            if (!directoryEtagMap.isEmpty()) {
                KnDbUtil.closePreparedStatement(pstmt);
                pstmt = null;
                List<String> updateMdnList = new ArrayList<String>(directoryEtagMap.keySet());
                StringBuilder updateBuffer = new StringBuilder(200);
                updateBuffer.append(QRY_UPDATE_LIST_OF_MDN_ETAG)
                        .append("(")
                        .append(com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(updateMdnList))
                        .append(")");
                updateQuery = updateBuffer.toString();
                pstmt = conn.prepareStatement(updateQuery);
                int index = 1;
                for (String mdn : updateMdnList) {
                    pstmt.setString(index++, mdn);
                }
                knLogger.debug( methodName, "Executing query- " , updateQuery );
                pstmt.executeUpdate();
            }
            knLogger.debug(methodName, "Query executed successfully");

        }  catch (Exception e) {
            throw KnDbUtil.processException(e, "Failed while fetching and updating the Subscriber directory etag " + e,
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, selectQuery + " , " + updateQuery);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return directoryEtagMap;
    }

    public Map<String, Integer> getDirectoryEtag(Set<String> mdnList) /*throws KnDAOException*/ {
        String methodName = "getDirectoryEtag(persisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdnList -", mdnList.size());
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String selectQuery = null;
        Map<String, Integer> directoryEtagMap = new HashMap<>();
        String SELECT_DIRECTORY_ETAG = "SELECT MDN,ETAG FROM DG.XDM_DIRECTORY WHERE MDN IN ";
        try {
            //pttServerId = retrieveLocalXDMPttServerId();
            List<String> subsList = new ArrayList<String>(mdnList);
            Collections.sort(subsList);
            knLogger.debug(methodName, "MDN list after sorting  - ", KnGDPRTemplate.mdnList(subsList));
            //conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
            StringBuilder strBuffer = new StringBuilder(200);
            strBuffer.append(SELECT_DIRECTORY_ETAG).append("(");
            strBuffer.append(formCommaSeperatedIdList(subsList)).append(");");
            selectQuery = strBuffer.toString();
            knLogger.debug(methodName, "Executing query- ", selectQuery);
            pstmt = conn.prepareStatement(selectQuery);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully ");
            while (rs.next()) {
                String mdn = rs.getString(1).trim();
                directoryEtagMap.put(mdn.trim(), rs.getInt(2));
            }
            knLogger.debug(methodName, "Query executed successfully");

        } catch (Exception e) {
            //throw KnDbUtil.processException(e, "Failed while fetching and updating the Subscriber directory etag " + e,
            //pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, selectQuery );
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            KnDbUtil.closeConnection(conn);
        }
        return directoryEtagMap;
    }

    public void updateEtagForDirDocOfMdnList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateEtagForDirDoc(String, String, int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : mdnList: ",KnGDPRTemplate.mdnList(mdnList));

        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int index = 1;
        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            StringBuilder strBuffer = new StringBuilder(200);
            //strBuffer.append(QRY_UPDATE_LIST_OF_MDN_ETAG).append("(");
            //strBuffer.append(formCommaSeperatedIdList(mdnList)).append(");");
            strBuffer.append(QRY_UPDATE_LIST_OF_MDN_ETAG).append("(").append(com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList)).append(")");
            query = strBuffer.toString();
            knLogger.debug( methodName, "Executing query- " , query );
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStatement = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pStatement.setString(index++,mdn);
            }
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);
            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.info( methodName, "Updated Etag for mdn:  " + KnGDPRTemplate.mdnList(mdnList));
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to update Etag for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
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
            throw KnDbUtil.processException(e, "Failed to update Etag for mdn - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_DIRECTORY, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
    }
}