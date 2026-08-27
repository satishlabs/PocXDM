/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnExtSubscriberDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnExtSubsPersistDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.kodiak.xdms.server.subsmgmt.resources.KnProvUtil.getComSepList;

/**
 * Created by Deepak on 28/3/14.
 */
public class KnExtSubscriberInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnExtSubscriberInfoDAO.class);
    private String pttServerId;
    private static final String TABLENAME = "DG.EXTSUBSCRINFO";
    private static final String MDN = "MDN";
    private static final String PROFILE_ID = "PROFILE_ID";
    private static final String SET_BY = "SET_BY";
    private static final String MDN_LIST = "MDNLIST";
    private static final String INSERT_QRY = "INSERT INTO " + TABLENAME + "(" + MDN + ", " + PROFILE_ID + "," + SET_BY + ") " +
            " VALUES (?, ?, ?)";
    private final String UPDATE_QRY = "UPDATE " + TABLENAME + " SET ";
    private static final String DELETE_QRY = "DELETE FROM " + TABLENAME + " WHERE " + MDN + "= ?";
    public static final String GET_QUERY = "SELECT " + MDN + "," + PROFILE_ID + "  FROM " + TABLENAME + " WHERE " + MDN + " IN (" + MDN_LIST + ")";

    public KnExtSubscriberInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    /**
     * Method to create External Subscriber Profile in the DB
     *
     * @param persistenceDTO
     * @param persistTxn
     * @throws KnDAOException
     */
    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(IPersistenceDTO,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Create External Subscriber ");
        knLogger.info(methodName, "ENTRY: Create External Subscriber DTO ", persistenceDTO);
        String query = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        try {
            query = INSERT_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);

            KnExtSubsPersistDTO extSubsPersistDTO = (KnExtSubsPersistDTO) persistenceDTO;
            if (extSubsPersistDTO != null && extSubsPersistDTO.getExtSubs() != null && !extSubsPersistDTO.getExtSubs().isEmpty()) {
                List<KnExtSubscriberDTO> listOfExtSubs = extSubsPersistDTO.getExtSubs();
                for (KnExtSubscriberDTO extSubscriberDTO : listOfExtSubs) {
                    pStmt.setString(1, extSubscriberDTO.getMdn());
                    pStmt.setInt(2, extSubscriberDTO.getProfileId());
                    pStmt.setInt(3, extSubscriberDTO.getSetBy());
                    pStmt.addBatch();
                }

                knLogger.debug(methodName, "QUERY : Executing " + query);
                pStmt.executeBatch();
                knLogger.debug(methodName, "QUERY : Completed.");
                knLogger.debug(methodName, "EXIT: Create External Subscriber");
            }
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQLException occurred");
            throw KnDbUtil.processException(sqlE, "Failed to Create External Subscriber - " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_EXTSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to Create External Subscriber - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_EXTSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    /**
     * Method to update External Subscriber value in the DB
     *
     * @param persistenceDTO
     * @param persistTxn
     * @throws KnDAOException
     */
    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "update(IPersistenceDTO,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Update External Subscriber Profile");
        knLogger.debug(methodName, "EXIT: Update External Subscriber Profile Sucessful");
      /*  String query = null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        knLogger.info(methodName, "ENTRY: Update External Subscriber Profile");
        try {
            KnExtSubsPersistDTO extSubsPersistDTO = (KnExtSubsPersistDTO) persistenceDTO;
            List<KnExtSubscriberDTO> listOfExtSubs = extSubsPersistDTO.getExtSubs();
            int profileId;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            for (KnExtSubscriberDTO extSubscriberDTO : listOfExtSubs) {
                if(!((extSubscriberDTO.getProfileId() == 0 )&&(extSubscriberDTO.getSetBy()==0))){
                    StringBuilder queryBuffer = new StringBuilder();
                    queryBuffer.append(UPDATE_QRY);
                    if (extSubscriberDTO.getProfileId() != 0) {

                        if (extSubscriberDTO.getSetBy() != 0) {
                            queryBuffer.append(PROFILE_ID).append("=?, ");
                        } else {
                            queryBuffer.append(PROFILE_ID).append("=? ");
                        }
                    }
                    if (extSubscriberDTO.getSetBy() != 0) {
                        queryBuffer.append(SET_BY).append("=? ");
                    }
                    queryBuffer.append(" WHERE ").append(MDN).append("=?");
                    query = queryBuffer.toString();
                    pStmt = conn.prepareStatement(query);
                    int columnIndex = 0;
                    if (extSubscriberDTO.getProfileId() != 0) {
                        pStmt.setInt(++columnIndex, extSubscriberDTO.getProfileId());
                    }
                    if (extSubscriberDTO.getSetBy() != 0) {
                        pStmt.setInt(++columnIndex, extSubscriberDTO.getSetBy());
                    }
                    pStmt.setString(++columnIndex, extSubscriberDTO.getMdn());
                    pStmt.addBatch();
                  }//end of if

            }//end of forEach
            knLogger.debug(methodName, "QUERY : Executing " + query);
            pStmt.executeBatch();
            knLogger.debug(methodName, "QUERY : Completed.");
            knLogger.debug(methodName, "Query: Executed ");


        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred",dbConne);
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQLException occurred",sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to update External Subscriber - " + sqlE.getMessage(),
                    pttServerId, KnDAOSourceTypes.XDM_EXTSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update External Subscriber Profile - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_EXTSUBSCRINFO, query);
        } finally {
            knLogger.info(methodName, "EXIT: update External Subscriber Profile");
        }*/
    }

    /**
     * Method to delete the External Subscriber Profile from the DB
     *
     * @param persistenceDTO
     * @param persistTxn
     * @throws KnDAOException
     */
    @Override
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(IPersistenceDTO,KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: Delete External Subscriber ");
        knLogger.debug(methodName, "ENTRY: Delete External Subscriber DTO -", persistenceDTO);
        String query = null;
        Connection conn = null;
        PreparedStatement pStmt = null;

        try {
            query = DELETE_QRY;
            KnExtSubsPersistDTO extSubsPersistDTO = (KnExtSubsPersistDTO) persistenceDTO;
            List<KnExtSubscriberDTO> listOfExtSubs = extSubsPersistDTO.getExtSubs();
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(DELETE_QRY);
            for (KnExtSubscriberDTO extSubscriberDTO : listOfExtSubs) {
                pStmt.setString(1, extSubscriberDTO.getMdn());
                pStmt.addBatch();
            }
            pStmt.executeBatch();
            knLogger.debug(methodName, "QUERY : Completed.");

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to Delete External Subscriber - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_EXTSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to Delete External Subscriber - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_EXTSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.info(methodName, "EXIT : Delete External Subscriber");
        }
    }

//    public KnExtSubsPersistDTO getExtSubscrInfo(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn)throws KnDAOException {
////        String methodName = "getExtSubscrInfo(IPersistenceDTO, KnPersisterTxn)";
////        String query = null;
////        Connection conn=null;
////        PreparedStatement pStmt=null;
////        Statement statement=null;
////        ResultSet rs = null;
////        knLogger.info( methodName, "ENTRY: Get External Subscriber Profile");
////        KnExtSubscriberInfoDTO extSubscriberInfoDTO=new KnExtSubscriberInfoDTO();
////
////        List<KnExtSubscriberDTO> listExtSubsDTO=new ArrayList<>();
////        List<String> listOfMdns=new ArrayList<>();
////        try {
////            KnExtSubsPersistDTO extSubsPersistDTO = (KnExtSubsPersistDTO) persistenceDTO;
////            List<KnExtSubscriberDTO> listOfExtSubs = extSubsPersistDTO.getExtSubs();
////            for (KnExtSubscriberDTO extSubscriberDTO : listOfExtSubs) {
////                listOfMdns.add(extSubscriberDTO.getMdn());
////            }
////            query = GET_QUERY.replaceAll(MDN_LIST, getComSepList(listOfMdns));
////            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA , false);
////            statement  = conn.createStatement();
////            knLogger.debug( methodName, "QUERY : Executing " + query + ", persisterTxn : " + persistTxn);
////            rs = statement.executeQuery(query);
////            knLogger.debug( methodName, "QUERY : Completed.");
////
////            while (rs.next()) {
////                KnExtSubscriberDTO extSubscriberDTO=new KnExtSubscriberDTO();
////                extSubscriberDTO.setMdn(rs.getString(1).trim());
////                extSubscriberDTO.setProfileId(rs.getInt(2));
//////                extSubscriberDTO.setSetBy(rs.getInt(3));
////                listExtSubsDTO.add(extSubscriberDTO);
////
////            }
////            extSubscriberInfoDTO.setExtSubs(listExtSubsDTO);
////            knLogger.debug( methodName, "External Subscriber Profile Successful With ResponseDTO",extSubscriberInfoDTO);
////        }catch (SQLException sqlE) {
////            knLogger.error( methodName, "SQL Exception occurred");
////            throw KnDbUtil.processException(sqlE, "Failed to Get External Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_EXTSUBSCRINFO, query);
////        } catch (Exception e) {
////            knLogger.error( methodName, "Unexpected Exception - ", e);
////            throw KnDbUtil.processException(e, "Failed to Get External Subscriber Profile - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_EXTSUBSCRINFO, query);
////        } finally {
////            KnDbUtil.closeResultSet(rs);
////            knLogger.info( methodName, "EXIT : Get External Subscriber Profile");
////        }
////        return extSubscriberInfoDTO;
//        return null;
//    }

    /**
     * -->Method to get External subscriber info for the list of mdns from the DB
     *
     * @param extMdnList
     * @param persistTxn
     * @return
     * @throws KnDAOException
     */
    public KnExtSubsPersistDTO getExtSubscrInfo(ArrayList<String> extMdnList, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getExtSubscrInfo(ArrayList, KnPersisterTxn)";
        String query = null;
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        knLogger.info(methodName, "ENTRY: Get External Subscriber Profile");
        KnExtSubsPersistDTO extSubsPersistDTO = new KnExtSubsPersistDTO();
        List<KnExtSubscriberDTO> listExtSubsDTO = new ArrayList<>();
        try {
            query = GET_QUERY.replaceAll(MDN_LIST, getComSepList(extMdnList));
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            statement = conn.createStatement();
            knLogger.debug(methodName, "QUERY : Executing " + query + ", persisterTxn : " + persistTxn);
            rs = statement.executeQuery(query);
            knLogger.debug(methodName, "QUERY : Completed.");

            while (rs.next()) {
                KnExtSubscriberDTO extSubscriberDTO = new KnExtSubscriberDTO();
                extSubscriberDTO.setMdn(rs.getString(1).trim());
                extSubscriberDTO.setProfileId(rs.getInt(2));
//              extSubscriberDTO.setSetBy(rs.getInt(3));
                listExtSubsDTO.add(extSubscriberDTO);

            }
            extSubsPersistDTO.setExtSubs(listExtSubsDTO);
            knLogger.debug(methodName, "External Subscriber Profile Successful With ResponseDTO", extSubsPersistDTO);
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to Get External Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnDAOSourceTypes.XDM_EXTSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to Get External Subscriber Profile - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_EXTSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(statement);
        }
        knLogger.info(methodName, "EXIT : Get External Subscriber Profile");
        return extSubsPersistDTO;
    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return null;
    }


}
