/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;


import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.logger.KnLogger;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPPAMAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnPAMAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * *****************************************************************************
 * File name:   KnPAMAccInfoDAO
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar            08/02/13       7.4
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
public class KnPAMAccInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPAMAccInfoDAO.class);
    private String pttServerId;

    private static final String TABLE_NAME = "DG.PAMACCOUNTINFO";

    private static final String PAMACCID = "PAMACCID";
    private static final String PAMACCNAME = "PAMACCNAME";
    private static final String PAMACCSTATE = "PAMACCSTATE";
    private static final String EXTERNALPAMACCID = "EXTERNALPAMACCID";
    private static final String BILLINGMDN = "BILLINGMDN";
    private static final String MAXSUBSCRIBER = "MAXSUBSCRIBER";
    private static final String CREATIONTIME = "CREATIONTIME";
    private static final String LASTUPDATETIME = "LASTUPDATETIME";
    private static final String CORP_HIERARCHY  = "CORP_HIERARCHY";
   
    private static final String UPDATE_TIMESTAMP_QRY = "UPDATE " + TABLE_NAME +" SET " + LASTUPDATETIME + "=? WHERE " + PAMACCID + "=?" ;
    private static final String GET_PAM_INFO_BY_EXTID="SELECT PAMACCID,PAMACCNAME, PAMACCSTATE, EXTERNALPAMACCID, " +
            "BILLINGMDN, MAXSUBSCRIBER, CREATIONTIME, LASTUPDATETIME, CORP_HIERARCHY FROM DG.PAMACCOUNTINFO WHERE EXTERNALPAMACCID =?";

    private static final String UPDATE_MAXSUB_QRY = "UPDATE DG.PAMACCOUNTINFO SET MAXSUBSCRIBER=?, " +
            "LASTUPDATETIME=? WHERE EXTERNALPAMACCID=?";

    private static final String UPDATE_PAMACC_NAME = "UPDATE DG.PAMACCOUNTINFO SET PAMACCNAME=?," +
            "LASTUPDATETIME=? WHERE PAMACCID=?";


    private static final String UPDATE_BILLINGMDN_QRY = "UPDATE DG.PAMACCOUNTINFO SET " +
            "EXTERNALPAMACCID=?, BILLINGMDN=?, LASTUPDATETIME=? WHERE PAMACCID=?";

    private static final String DELETE_BILLINGMDN_QRY = "DELETE FROM DG.PAMACCOUNTINFO  WHERE EXTERNALPAMACCID=?";

    private static final String GET_PAM_INFO_BY_PAMACCID="SELECT PAMACCID, PAMACCNAME, PAMACCSTATE, EXTERNALPAMACCID," +
            "BILLINGMDN, MAXSUBSCRIBER, CREATIONTIME, LASTUPDATETIME, CORP_HIERARCHY FROM DG.PAMACCOUNTINFO WHERE PAMACCID =?";

    private static final String GET_PAMACCID_BY_EXTPAMACCID="SELECT PAMACCID FROM DG.PAMACCOUNTINFO WHERE EXTERNALPAMACCID =?";

    private static final String UPDATE_PAMSTATE_QRY = "UPDATE DG.PAMACCOUNTINFO SET PAMACCSTATE=?, " +
            "LASTUPDATETIME=? WHERE EXTERNALPAMACCID=?";


    private static final String QRY_EXECUTING_MSG  = "QUERY: Executing - ";
    private static final String QRY_EXE_MSG  = "QUERY: Executed - ";
    private static final String SQL_EXCEPTION_MSG = "SQL Exception occurred";
    public KnPAMAccInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    /**
     * method to insert records for the table DG.PAMACCOUNTINFO
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insert(IPersistenceDTO, KnPersiterTxn)";
        knLogger.entry(methodName, persistenceDTO, persisterTxn);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;

        try {
            KnPAMAccInfoDTO pamAccInfoDTO = (KnPAMAccInfoDTO) persistenceDTO;
            int pamAccountId = pamAccInfoDTO.getPamAccId();
            String pamAccName = pamAccInfoDTO.getBillingName();
            int pamAccState = pamAccInfoDTO.getPamAccState();
            String extPamAccId = pamAccInfoDTO.getExtPamAccId();
            String billingMdn = pamAccInfoDTO.getBillingMdn();
            int maxSubs = pamAccInfoDTO.getTotalNoOfLines();
            long creationTime = pamAccInfoDTO.getCreationTime();
            long lastUpdateTime = pamAccInfoDTO.getLastUpdateTime();
            
            //multilingual revert changes
            ArrayList<String> queryFields = new ArrayList<String>();
            queryFields.add(PAMACCID);
            queryFields.add(PAMACCNAME);
            queryFields.add(PAMACCSTATE);
            queryFields.add(EXTERNALPAMACCID);
            queryFields.add(BILLINGMDN);
            queryFields.add(MAXSUBSCRIBER);
            queryFields.add(CREATIONTIME);
            queryFields.add(LASTUPDATETIME);
            queryFields.add(CORP_HIERARCHY);

            query = KnDbUtil.getInsertQuery(TABLE_NAME, queryFields);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            pStmt.setInt(++columnIndex, pamAccountId);
            //multilingual revert changes
            if(pamAccName !=null){
            	try {
					pStmt.setString(++columnIndex, new String(pamAccName.getBytes("UTF-8"),"8859_1"));
				} catch (UnsupportedEncodingException e) {
					knLogger.error("UnsupportedEncodingException while billing group name ",e);	
				}
            }
            else{
            	 pStmt.setNull(++columnIndex, java.sql.Types.VARCHAR);
            }
            pStmt.setInt(++columnIndex, pamAccState);
            pStmt.setString(++columnIndex, extPamAccId);
            pStmt.setString(++columnIndex, billingMdn);
            pStmt.setInt(++columnIndex, maxSubs);
            pStmt.setLong(++columnIndex, creationTime);
            pStmt.setLong(++columnIndex, lastUpdateTime);
            pStmt.setInt(++columnIndex, pamAccInfoDTO.getHierarchyType().value());

            knLogger.debug( methodName, QRY_EXECUTING_MSG, query);
            int result = pStmt.executeUpdate();
            knLogger.debug( methodName, QRY_EXE_MSG, result);

        } catch (SQLException sqlE) {
            knLogger.error( methodName, SQL_EXCEPTION_MSG, sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to create PAM Account Info - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMACCOUNTINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }

        knLogger.exit(methodName);
    }

    /**
     * method to update records for the table DG.PAMACCOUNTINFO
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "update(IPersistenceDTO, KnPersisterTxn)";
        knLogger.entry(methodName, persistenceDTO, persisterTxn);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;

        try {

            KnPAMAccInfoDTO pamAccInfoDTO = (KnPAMAccInfoDTO) persistenceDTO;
            int pamAccountId = pamAccInfoDTO.getPamAccId();
            String pamAccName = pamAccInfoDTO.getBillingName();
            int pamAccState = pamAccInfoDTO.getPamAccState();
            String extPamAccId = pamAccInfoDTO.getExtPamAccId();
            String billingMdn = pamAccInfoDTO.getBillingMdn();
            int maxSubs = pamAccInfoDTO.getTotalNoOfLines();
            long lastUpdateTime = pamAccInfoDTO.getLastUpdateTime();

            //multilingual revert changes
            StringBuilder queryBuffer = new StringBuilder();
            queryBuffer.append("UPDATE ").append(TABLE_NAME).append(" SET ");
            queryBuffer.append(PAMACCNAME).append("=?, ");
            queryBuffer.append(PAMACCSTATE).append("=?, ");
            queryBuffer.append(EXTERNALPAMACCID).append("=?, ");
            queryBuffer.append(BILLINGMDN).append("=?, ");
            queryBuffer.append(MAXSUBSCRIBER).append("=?, ");
            queryBuffer.append(LASTUPDATETIME).append("=?");
            queryBuffer.append(" WHERE ").append(PAMACCID).append("=?");

            query = queryBuffer.toString();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;
            //multilingual revert changes
            if (pamAccName != null) {
            	try {
					pStmt.setString(++columnIndex, new String(pamAccName.getBytes("UTF-8"),"8859_1"));
				} catch (UnsupportedEncodingException e) {
					knLogger.error("UnsupportedEncodingException while parsing billing name ",e);	
				}
            } else {
                pStmt.setNull(++columnIndex, java.sql.Types.VARCHAR);
            }
            pStmt.setInt(++columnIndex, pamAccState);

            if (extPamAccId != null) {
                pStmt.setString(++columnIndex, extPamAccId);
            } else {
                pStmt.setNull(++columnIndex, java.sql.Types.VARCHAR);
            }
            if (billingMdn != null) {
                pStmt.setString(++columnIndex, billingMdn);
            } else {
                pStmt.setNull(++columnIndex, java.sql.Types.VARCHAR);
            }
            pStmt.setInt(++columnIndex, maxSubs);

            pStmt.setLong(++columnIndex, lastUpdateTime);

            pStmt.setInt(++columnIndex, pamAccountId);

            knLogger.debug( methodName, QRY_EXECUTING_MSG, query);
            int result = pStmt.executeUpdate();
            knLogger.debug( methodName, QRY_EXE_MSG, result);

        }catch (SQLException sqlE) {
            knLogger.error( methodName, SQL_EXCEPTION_MSG, sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to update PAM Acc Info - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMACCOUNTINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.exit(methodName);
    }


    public void updateBillingMDN(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateBillingMDN(IPersistenceDTO, KnPersisterTxn)";
        knLogger.entry(methodName, persistenceDTO, persisterTxn);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = UPDATE_BILLINGMDN_QRY;

        try {

            KnPAMAccInfoDTO pamAccInfoDTO = (KnPAMAccInfoDTO) persistenceDTO;

            int pamAccountId = pamAccInfoDTO.getPamAccId();
            String newBillingMdn = pamAccInfoDTO.getBillingMdn();
            long lastUpdateTime = pamAccInfoDTO.getLastUpdateTime();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);

            pStmt.setString(1, newBillingMdn);
            pStmt.setString(2, newBillingMdn);
            pStmt.setLong(3, lastUpdateTime);
            pStmt.setInt(4, pamAccountId);

            knLogger.debug( methodName, QRY_EXECUTING_MSG, query);
            int result = pStmt.executeUpdate();
            knLogger.debug( methodName, QRY_EXE_MSG, result);

        }catch (SQLException sqlE) {
            knLogger.error( methodName, SQL_EXCEPTION_MSG, sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to update PAM Acc Info - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMACCOUNTINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.exit(methodName);
    }

    /**
     * method to delete records for the table DG.Subscriber_AddlInfo
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "delete(IPersistenceDTO, KnPersisterTxn)";
        knLogger.entry(methodName, persistenceDTO, persisterTxn);
        String query = DELETE_BILLINGMDN_QRY;
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            KnPAMAccInfoDTO pamAccInfoDTO = (KnPAMAccInfoDTO) persistenceDTO;
            String extPamAccId = pamAccInfoDTO.getBillingMdn();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, extPamAccId);

            knLogger.debug( methodName, QRY_EXECUTING_MSG, query, extPamAccId);
            int result = pStmt.executeUpdate();
            knLogger.debug( methodName, QRY_EXE_MSG, result);

        } catch (SQLException sqlE) {
            knLogger.error( methodName, SQL_EXCEPTION_MSG, sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to delete PAM account Info - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMACCOUNTINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.exit(methodName);
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "select(IPersistenceDTO, KnPersisterTxn)";
        knLogger.info( methodName, "NOT IMPLEMENTED");
        return null;
    }

    /**
     * method to retrieve the Subscriber Addl Info
     *
     * @param extPamAccId  String
     * @param persisterTxn KnPersisterTxn
     * @return KnIdsSubsProfileInfoDTO
     * @throws KnDAOException DB Exception
     */
    public KnOPPAMAccInfoDTO getPAMAccountInfo(String extPamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getPAMAccountInfo(String , KnPersisterTxn)";
        knLogger.entry( methodName, extPamAccId, persisterTxn);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = GET_PAM_INFO_BY_EXTID;
        ResultSet rs = null;
        KnOPPAMAccInfoDTO pamAccInfoDTO = null;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, extPamAccId);
            knLogger.debug( methodName, QRY_EXECUTING_MSG, query, extPamAccId);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, QRY_EXE_MSG, rs);
            if (rs.next()) {
                pamAccInfoDTO = new KnOPPAMAccInfoDTO();
                pamAccInfoDTO.setPamAccId(rs.getInt(PAMACCID));
                //multilingual revert changes
                if(rs.getString(PAMACCNAME) != null){
					try {
						pamAccInfoDTO.setBillingName(new String(rs.getString(PAMACCNAME).getBytes("8859_1"),"UTF-8"));
					} catch (UnsupportedEncodingException e) {
						knLogger.error("UnsupportedEncodingException while parsing billing name ",e);	
					}
                }
                pamAccInfoDTO.setPamAccState(rs.getInt(PAMACCSTATE));
                pamAccInfoDTO.setExtPamAccId(trim(rs.getString(EXTERNALPAMACCID)));
                pamAccInfoDTO.setBillingMdn(trim(rs.getString(BILLINGMDN)));
                pamAccInfoDTO.setTotalNoOfLines(rs.getInt(MAXSUBSCRIBER));
                pamAccInfoDTO.setCreationTime(rs.getLong(CREATIONTIME));
                pamAccInfoDTO.setLastUpdateTime(rs.getLong(LASTUPDATETIME));
                pamAccInfoDTO.setHierarchyType(HIERARCHY_TYPE.validate(rs.getInt(CORP_HIERARCHY)));
            } else {
                knLogger.debug( methodName, "PAM Account Profile does not Exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "PAM Account Doesnt exist",
                        pttServerId, KnProvDAOSourceTypes.PAMACCOUNTINFO, query);
            }
        } catch (SQLException sqlE) {
            knLogger.error( methodName, SQL_EXCEPTION_MSG, sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to retrieve PAM Account Info - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMACCOUNTINFO, query);

        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }

        knLogger.exit( methodName, pamAccInfoDTO);
        return pamAccInfoDTO;
    }


    public KnOPPAMAccInfoDTO retrieveLicensePackInfo(String extPamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
    	 final String methodName = "retrieveLicensePackInfo(String , KnPersisterTxn)";
         knLogger.entry( methodName, extPamAccId, persisterTxn);
         KnOPPAMAccInfoDTO pamAccInfoDTO = null;
         try {
        	 pamAccInfoDTO = this.getPAMAccountInfo(extPamAccId, persisterTxn);

		} catch (KnDAOException e) {
			if (!com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
				throw e;
	           }
		}
         return pamAccInfoDTO;
    }




    /**
     * method to retrieve the Pam account Info
     *
     * @param pamAccId  String
     * @param persisterTxn KnPersisterTxn
     * @return KnIdsSubsProfileInfoDTO
     * @throws KnDAOException DB Exception
     */
    public KnOPPAMAccInfoDTO getPAMAccInfoFromId(int pamAccId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getPAMAccInfoFromId(int, boolean, KnPersisterTxn)";
        knLogger.entry(methodName,pamAccId, persisterTxn);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = GET_PAM_INFO_BY_PAMACCID;
        ResultSet rs = null;
        KnOPPAMAccInfoDTO pamAccInfoDTO = null;
        try {

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, pamAccId);

            knLogger.debug( methodName, QRY_EXECUTING_MSG, query, pamAccId);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, QRY_EXE_MSG, rs);
            if (rs.next()) {
                pamAccInfoDTO = new KnOPPAMAccInfoDTO();
                pamAccInfoDTO.setPamAccId(rs.getInt(PAMACCID));
                if(rs.getString(PAMACCNAME) != null){
					try {
						pamAccInfoDTO.setBillingName(new String(rs.getString(PAMACCNAME).getBytes("8859_1"),"UTF-8"));
					} catch (UnsupportedEncodingException e) {
						knLogger.error("UnsupportedEncodingException while parsing billing name ",e);	
					}
                }
                pamAccInfoDTO.setPamAccState(rs.getInt(PAMACCSTATE));
                pamAccInfoDTO.setExtPamAccId(trim(rs.getString(EXTERNALPAMACCID)));
                pamAccInfoDTO.setBillingMdn(trim(rs.getString(BILLINGMDN)));
                pamAccInfoDTO.setTotalNoOfLines(rs.getInt(MAXSUBSCRIBER));
                pamAccInfoDTO.setCreationTime(rs.getLong(CREATIONTIME));
                pamAccInfoDTO.setLastUpdateTime(rs.getLong(LASTUPDATETIME));
                pamAccInfoDTO.setHierarchyType(HIERARCHY_TYPE.validate(rs.getInt(CORP_HIERARCHY)));
            } else {
                knLogger.debug( methodName, "PAM Account Profile does not Exist");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "PAM Account Doesnt exist",
                        pttServerId, KnProvDAOSourceTypes.PAMACCOUNTINFO, query);
            }

        } catch (SQLException sqlE) {
            knLogger.error( methodName, SQL_EXCEPTION_MSG, sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to retrieve PAM Account Info - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMACCOUNTINFO, query);
        }finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }

        knLogger.exit(methodName, pamAccInfoDTO);
        return pamAccInfoDTO;
    }
    /**
     * method to retrieve the PAM Account ID
     *
     * @param extPamAccId  String
     * @param persisterTxn KnPersisterTxn
     * @return int pam acc id for the external pam acc Id
     * @throws KnDAOException DAO Layer exception
     */
    public int getPAMAccId(String extPamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getPAMAccId(String, KnPersisterTxn)";
        String query = GET_PAMACCID_BY_EXTPAMACCID;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        int pamAccId = -1;

        knLogger.entry(methodName, extPamAccId, persisterTxn);
        try {

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, extPamAccId);

            knLogger.debug( methodName, QRY_EXECUTING_MSG, query, " with extPamAccId - ", extPamAccId);
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, QRY_EXE_MSG);

            if (rs.next()) {
                pamAccId = rs.getInt(PAMACCID);

            }

        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            throw KnDbUtil.processException(sqlE, "Failed to get PAM Acc ID - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.PAMACCOUNTINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }

        return pamAccId;
    }

    /**
     * method to update pam account state for the table DG.PAMACCOUNTINFO
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updatePAMAccState(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updatePAMAccState(IPersistenceDTO, KnPersisterTxn)";
        knLogger.entry(methodName, persistenceDTO, persisterTxn);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = UPDATE_PAMSTATE_QRY;

        try {
            KnPAMAccInfoDTO pamAccInfoDTO = (KnPAMAccInfoDTO) persistenceDTO;
            int pamAccState = pamAccInfoDTO.getPamAccState();
            String extPamAccId = pamAccInfoDTO.getBillingMdn();

            long lastUpdateTime = pamAccInfoDTO.getLastUpdateTime();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;

            pStmt.setInt(++columnIndex, pamAccState);
            pStmt.setLong(++columnIndex, lastUpdateTime);
            pStmt.setString(++columnIndex, extPamAccId);


            knLogger.debug( methodName, QRY_EXECUTING_MSG, query, pamAccInfoDTO);
            int result = pStmt.executeUpdate();
            knLogger.debug( methodName, QRY_EXE_MSG, result);

        } catch (SQLException sqlE) {
            knLogger.error( methodName, SQL_EXCEPTION_MSG, sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to update PAM Acc State - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMACCOUNTINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }

        knLogger.exit(methodName);
    }


    /**
     * method to update pam account subscount for the table DG.PAMACCOUNTINFO
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updatePAMAccMaxSub(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updatePAMAccMaxSub(IPersistenceDTO, KnPersisterTxn)";
        knLogger.entry( methodName, persistenceDTO, persisterTxn);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = UPDATE_MAXSUB_QRY;

        try {
            KnPAMAccInfoDTO pamAccInfoDTO = (KnPAMAccInfoDTO) persistenceDTO;
            int maxSub = pamAccInfoDTO.getTotalNoOfLines();
            String extPamAccId = pamAccInfoDTO.getBillingMdn();
            long lastUpdateTime = pamAccInfoDTO.getLastUpdateTime();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            int columnIndex = 0;

            pStmt.setInt(++columnIndex, maxSub);
            pStmt.setLong(++columnIndex, lastUpdateTime);
            pStmt.setString(++columnIndex, extPamAccId);


            knLogger.debug( methodName, QRY_EXECUTING_MSG, query,  pamAccInfoDTO);
            int result = pStmt.executeUpdate();
            knLogger.exit( methodName,result);

        } catch (SQLException sqlE) {
            knLogger.error( methodName, SQL_EXCEPTION_MSG, sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to update PAM Acc State - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMACCOUNTINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    /**
     * method to update pam account name for the table DG.PAMACCOUNTINFO
     *
     * @param persistenceDTO IPersistenceDTO
     * @param persisterTxn   KnPersisterTxn
     * @throws KnDAOException DB Layer Exception
     */
    public void updatePAMAccName(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updatePAMAccName(IPersistenceDTO, KnPersisterTxn)";
        knLogger.entry( methodName, persistenceDTO, persisterTxn);
        Connection conn;
        PreparedStatement pStmt = null;

        try {
            KnPAMAccInfoDTO pamAccInfoDTO = (KnPAMAccInfoDTO) persistenceDTO;
            String billingName  = pamAccInfoDTO.getBillingName();
            int pamAccId = pamAccInfoDTO.getPamAccId();
            long lastUpdateTime = pamAccInfoDTO.getLastUpdateTime();

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(UPDATE_PAMACC_NAME);
            int columnIndex = 0;
            //multilingual revert changes
            if(billingName != null){
            	try {
					billingName = new String(billingName.getBytes("UTF-8"),"8859_1");
				} catch (UnsupportedEncodingException e) {
					knLogger.error("UnsupportedEncodingException while parsing billing name ",e);	
				}
            }
            pStmt.setString(++columnIndex, billingName);
            pStmt.setLong(++columnIndex, lastUpdateTime);
            pStmt.setInt(++columnIndex, pamAccId);


            knLogger.debug( methodName, QRY_EXECUTING_MSG, UPDATE_PAMACC_NAME,  pamAccInfoDTO);
            int result = pStmt.executeUpdate();
            knLogger.exit( methodName,result);

        } catch (SQLException sqlE) {
            knLogger.error( methodName, SQL_EXCEPTION_MSG, sqlE);
            throw KnDbUtil.processException(sqlE, "Failed to update PAM Acc State - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.PAMACCOUNTINFO, UPDATE_PAMACC_NAME);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    /**
     * Update the lastupdatetime
     *
     * @param int          pamAccId
     * @param persisterTxn KnPersisterTxn
     * @throws KnDAOException DB Layer exception
     */
    public void updatePAMAccLastProfileUpdatedTime(int pamAccId, long lastProfileUpdatetime, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updatePAMAccLastProfileUpdatedTime(int,long,KnPersisterTxn)";
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        int res = 0;
        knLogger.entry(methodName, pamAccId);
        try {

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(UPDATE_TIMESTAMP_QRY);
            pStmt.setLong(1, lastProfileUpdatetime);
            pStmt.setInt(2, pamAccId);

            knLogger.debug(methodName, QRY_EXECUTING_MSG, query);
            res = pStmt.executeUpdate();
            knLogger.debug(methodName, QRY_EXE_MSG);

        }  catch (SQLException sqlE) {
            knLogger.error(methodName, SQL_EXCEPTION_MSG);
            throw KnDbUtil.processException(sqlE, "Failed to update Last profile Update Time - " + sqlE.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.exit(methodName,  res);
    }

    private static String trim(String input) {
        return input != null ? input.trim() : input;
    }


}
