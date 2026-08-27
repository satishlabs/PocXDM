/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnPOCSubscrRoamingProfileDAO.java
 * Subsystem:   Provisioning library
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
 * *******************************************************************************
 */
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil.convertListToIntBuffer;
import static com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil.replaceContactWithValue;

public class KnPOCSubscrRoamingProfileDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPOCSubscrRoamingProfileDAO.class);

    private static final String className = KnPOCSubscrRoamingProfileDAO.class.getName();
    private String pttServerId;

    private static final String MDN = "MDN";
    private static final String ROAMINGCLUSTERID = "ROAMINGCLUSTERID";

    private static final String TABLENAME = "DG.POCSUBSCRROAMINGPROFILE";

    private static final String SELECT_QRY = "SELECT " + ROAMINGCLUSTERID + " FROM " + TABLENAME +
            " WHERE " + MDN + "= ?";
    private static final String INSERT_QRY = "INSERT INTO " + TABLENAME + "(" + MDN + ", " + ROAMINGCLUSTERID + ") " +
            "VALUES(?, ?)";
    private static final String UPDATE_QRY = "UPDATE " + TABLENAME + " SET " + ROAMINGCLUSTERID + "= ? " +
            "WHERE " + MDN + "= ?";
    private static final String DELETE_QRY = "DELETE FROM " + TABLENAME + " WHERE " + MDN + "= ?";

    private static final String DELETE_ROAMINGTYPE_QRY = "DELETE FROM " + TABLENAME + " WHERE " + MDN + "= ? AND " + ROAMINGCLUSTERID + " IN ROAMINGCLUSTERIDS ";

    private static final String UPDATE_MDN_QRY = "UPDATE " + TABLENAME + " SET " + MDN + " = ? WHERE " + MDN + " = ?";

    public KnPOCSubscrRoamingProfileDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug( methodName, "ENTRY: Create Subsc Roaming Profile for MDN ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persistTxn.open();
                ownedTxn = true;
            }

            KnSubsProfilePersistDTO subsInfoDto = (KnSubsProfilePersistDTO) persistenceDTO;
            String mdn = subsInfoDto.getMdn();
            ArrayList<Integer> roamingType = subsInfoDto.getRoamingTypes();

            query = INSERT_QRY;
            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            for (Integer roamingClusterId : roamingType) {
                pStmt.setString(1, mdn);
                pStmt.setInt(2, roamingClusterId);
                pStmt.addBatch();
            }
            knLogger.debug( methodName, "Query: Executing query - ", query, ", DTO - ", persistenceDTO,
                    ", Txn - ", persistTxn);
            pStmt.executeBatch();
            knLogger.debug( methodName, "Query: Executed");

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction");
                persistTxn.save();
            }
        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception Occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {

            knLogger.error( methodName, "SQL Exception Occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to create Subs Roaming  Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to create Subs Roaming Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : create Subs Roaming Profile");
        }
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "update(IPersistenceDTO, KnPersisterTxn)";
        knLogger.error( methodName, "Not Implemented");
    }

    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delte(IpersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug( methodName, "ENTRY: DELETE Subs Roaming Profile");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persistTxn.open();
                ownedTxn = true;
            }
            KnSubsProfilePersistDTO subsInfoDTO = (KnSubsProfilePersistDTO) persistenceDTO;
            String mdn = subsInfoDTO.getMdn();

            query = DELETE_QRY;

            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);

            knLogger.debug( methodName, "Query: Executing query - ", query, ", DTO - ", persistenceDTO,
                    ", Txn - ", persistTxn);
            pStmt.executeUpdate();
            knLogger.debug( methodName, "Query: Executed ");

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to delete Subs Roaming profile - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete Subs Roaming profile - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : delete Subs Roaming profile - ");
        }
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "select(IPersistenceDTO, KnPersisterTxn)";
        knLogger.debug( methodName, "Not Implemented");
        return null;
    }

    /**
     * method to retrieve the subscriber roaming profile
     *
     * @param mdn        String
     * @param persistTxn KnPersisterTxn
     * @return ArrayList<Integer>
     * @throws KnDAOException DAO Layer Exception
     */
    public ArrayList<Integer> selectRoamingTypes(String mdn, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "select(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean ownedTxn = false;
        knLogger.debug( methodName, "ENTRY: Select Subs Roaming Profile");

        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening Transaction");
                persistTxn.open();
                ownedTxn = true;
            }

            query = SELECT_QRY;
            if (ownedTxn) {
                //conn = persistTxn.getDBConnection(pttServerId, true);
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            } else {
                //conn = persistTxn.getDBConnection(pttServerId, false);
                conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            }

            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);
            knLogger.debug( methodName, "Query: Executing query - ", query, ", MDN - ", KnGDPRTemplate.mdn(mdn),
                    ", Txn - ", persistTxn);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, "Query: Executed");

            ArrayList<Integer> roamingType = new ArrayList<Integer>();
            if (rs.next()) {
                int roamingClusterId = 0;
                do {
                    roamingClusterId = rs.getInt(1);
                    roamingType.add(roamingClusterId);
                } while (rs.next());
            } else {
                knLogger.warn( methodName, "No Roaming Profile found for Subscriber - ", KnGDPRTemplate.mdn(mdn));
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber Roaming Profile Doesnt exist", pttServerId,
                        KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
            }

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving Transaction");
                persistTxn.save();
            }
            knLogger.debug( methodName, "returning subs roaming profile ", roamingType);
            return roamingType;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to Select Subs Roaming profile - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to Select Subs Roaming profile - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : Select Subs Roaming profile - ");
        }
    }

    /**
     * method to update the mdn of the Subscriber Roaming profile
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
        knLogger.debug( methodName, "ENTRY: update old Mdn [", KnGDPRTemplate.mdn(oldMdn), "] with new Mdn [", KnGDPRTemplate.mdn(newMdn), "]"
        );
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

            knLogger.debug( methodName, "QUERY : Executing ", query, ", Old mdn : ", KnGDPRTemplate.mdn(oldMdn),
                    ", and new mdn : ", KnGDPRTemplate.mdn(newMdn), " persistTxn : ", persisterTxn);
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed.");
            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug( methodName, "saving the transaction ");
                persisterTxn.save();
            }

        } catch (KnDAOException daoE) {
            knLogger.error( methodName, "DAO Exception - ", daoE);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update poc subscr roaming profile mdn - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to poc subscr roaming profile mdn - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : update poc subscr roaming profile mdn - ");
        }
    }

    public void insert(List<String> mdns, List<Integer> roamingClusterIds, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(List <String>,List<Integer>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug( methodName, "ENTRY: Create Subsc Roaming Profile for MDN ");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persistTxn.open();
                ownedTxn = true;
}


            query = INSERT_QRY;
            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            for (String mdn : mdns) {
                for (Integer roamingClusterId : roamingClusterIds) {
                    pStmt.setString(1, mdn);
                    pStmt.setInt(2, roamingClusterId);
                    pStmt.addBatch();
                }
            }
            knLogger.debug( methodName, "Query: Executing query - ", query, ", DTO - ", ", Txn - ", persistTxn);
            int[] count =pStmt.executeBatch();
            knLogger.debug( methodName, "Query: Executed  mdn count:",count);

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction");
                persistTxn.save();
            }
        } catch (KnDAOException dbConne) {
            knLogger.error( methodName, "DAO Exception Occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQL Exception Occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to create Subs Roaming  Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to create Subs Roaming Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : create Subs Roaming Profile");
        }
    }

    public void delete(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(List<String>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        boolean ownedTxn = false;
        knLogger.debug( methodName, "ENTRY: DELETE Subs Roaming Profiles");
        try {
            if (persistTxn == null) {
                persistTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "Opening the Transaction");
                persistTxn.open();
                ownedTxn = true;
            }

            query = DELETE_QRY;

            //conn = persistTxn.getDBConnection(pttServerId, false);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            for (String mdn : mdns) {
                pStmt.setString(1, mdn);
                pStmt.addBatch();
            }

            knLogger.debug( methodName, "Query: Executing query - ", query, ", DTO - ",  ", Txn - ", persistTxn);
           int[] count = pStmt.executeBatch();
            knLogger.debug( methodName, "Query: Executed mdns count:",count);

            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the Transaction");
                persistTxn.save();
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw e;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to delete Subs Roaming profile - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persistTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to delete Subs Roaming profile - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : delete Subs Roaming profile - ");
        }
    }

    public void deleteRoamingType(String mdn,List<Integer> roamingClusterIds, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "deleteRoamingType(List<String>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug( methodName, "ENTRY:Delete Roaming Type for mdn",mdn," with roamingClusterIds",roamingClusterIds);
        try {
            query = DELETE_ROAMINGTYPE_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, "ROAMINGCLUSTERIDS", convertListToIntBuffer (roamingClusterIds).toString());
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);
            knLogger.debug(methodName, "Query: Executing query - ", query, ", DTO - ", ", Txn - ", persistTxn);
            int result=pStmt.executeUpdate();
            knLogger.debug( methodName, "Query: Executed mdns count:",result);
        }  catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete Subs Roaming profile - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug( methodName, "EXIT:Deleted Roaming Type");
    }

    public void updateMdn(List<String> oldMdns, List<String> newMdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateMdn(List <String>, List<String<, KnPersisterTxn)";
        boolean ownedTxn = false;
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug( methodName, "ENTRY: update old Mdn [", oldMdns.size(), "] with new Mdn [", newMdns.size(), "]"
        );
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
            for (int i = 0; i < oldMdns.size(); i++) {
                pStmt.setString(1, newMdns.get(i));
                pStmt.setString(2, oldMdns.get(i));
                pStmt.addBatch();
            }

            knLogger.debug( methodName, "QUERY : Executing ", query, ", Old mdn : ", oldMdns.size(),
                    ", and new mdn : ", newMdns.size(), " persistTxn : ", persisterTxn);
            int[] count =  pStmt.executeBatch();
            knLogger.debug( methodName, "QUERY : Completed. mdn count",count);
            // save the transaction only if its owned by the current method.
            if (ownedTxn) {
                knLogger.debug( methodName, "saving the transaction ");
                persisterTxn.save();
            }

        } catch (KnDAOException daoE) {
            knLogger.error( methodName, "DAO Exception - ", daoE);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw daoE;
        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occurred");
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(sqlE, "Failed to update poc subscr roaming profile mdn - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - ", e);
            if (ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed to poc subscr roaming profile mdn - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRROAMINGPROFILE, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug( methodName, "EXIT : update poc subscr roaming profile mdn - ");
        }
    }

}
