/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnPOCSubscrInfoDAO.java
 * Subsystem:   Provisioning Library
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

public class KnUserProfileMdnMapDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUserProfileMdnMapDAO.class);

    private static final String className = KnUserProfileMdnMapDAO.class.getName();
    private String pttServerId;
    private static final String TABLENAME = "DG.USERPROFILEMDNMAP";
    private static final String BASEMDN = "BASEMDN";
    private static final String PROFILEMDN = "PROFILEMDN";
    private static final String DELETE_QRY = "DELETE FROM " + TABLENAME + " WHERE " + BASEMDN + "= ?";
    private static final String GET_USER_PROFILE_MDN_MAP_BY_MDN="SELECT BASEMDN,PROFILEMDN FROM DG.USERPROFILEMDNMAP WHERE BASEMDN =?";
    private static final String QRY_EXECUTING_MSG  = "QUERY: Executing - ";
    private static final String QRY_EXE_MSG  = "QUERY: Executed - ";



    public KnUserProfileMdnMapDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
    	String methodName = "insert(IPersistenceDTO, KnPersisterTxn)";
    	String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
    	try {

        knLogger.debug(methodName, "ENTRY: Create User Profile Mdn Map with DTO - ", persistenceDTO, " Persist ", persistTxn);


            KnSubsProfilePersistDTO subsInfoPersistDto = (KnSubsProfilePersistDTO) persistenceDTO;
            String mdn = subsInfoPersistDto.getMdn();
            String profileMdn = subsInfoPersistDto.getProfileMdn();
            ArrayList<String> queryFields = new ArrayList<String>();
            queryFields.add(BASEMDN);
            queryFields.add(PROFILEMDN);
           
            query = KnDbUtil.getInsertQuery(TABLENAME, queryFields);
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, persistenceDTO);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, " Connection .. ", conn);
            /*  KnDBConst.DataStores.
        conn = persistTxn.getDBConnection(pttServerId, , false);*/
            pStmt = conn.prepareStatement(query);

            int columnIndex = 0; // will dynamically update the column index value as per the received values.
            pStmt.setString(++columnIndex, mdn);
            pStmt.setString(++columnIndex, profileMdn);
           
            knLogger.debug(methodName, "Query: Executing -", query, ", persist DTO - ", persistenceDTO);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");


        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to Create User Profile Mdn Map - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to Create User Profile Mdn Map - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug(methodName, "EXIT : Create User Profile Mdn Map");
        }

    }

    public Map<String,String> insertUserProfileMdn(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "insert(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Map<String, String> userProfileMdnMap = new HashMap<>();
        Connection conn;
        PreparedStatement pStmt = null;
        try {

            knLogger.debug(methodName, "ENTRY: Create User Profile Mdn Map with DTO - ", persistenceDTO, " Persist ", persistTxn);

            KnSubsProfilePersistDTO subsInfoPersistDto = (KnSubsProfilePersistDTO) persistenceDTO;
            String mdn = subsInfoPersistDto.getMdn();
            String profileMdn = subsInfoPersistDto.getProfileMdn();
            ArrayList<String> queryFields = new ArrayList<String>();
            queryFields.add(BASEMDN);
            queryFields.add(PROFILEMDN);

            query = KnDbUtil.getInsertQuery(TABLENAME, queryFields);
            knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA, persistenceDTO);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, " Connection .. ", conn);
            /*  KnDBConst.DataStores.
        conn = persistTxn.getDBConnection(pttServerId, , false);*/
            pStmt = conn.prepareStatement(query);

            int columnIndex = 0; // will dynamically update the column index value as per the received values.
            pStmt.setString(++columnIndex, mdn);
            pStmt.setString(++columnIndex, profileMdn);

            knLogger.debug(methodName, "Query: Executing -", query, ", persist DTO - ", persistenceDTO);
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");
            userProfileMdnMap.put(mdn,profileMdn);

        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to Create User Profile Mdn Map - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            knLogger.error(methodName, e);
            throw KnDbUtil.processException(e, "Failed to Create User Profile Mdn Map - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug(methodName, "EXIT : Create User Profile Mdn Map");
        }
        return  userProfileMdnMap;
    }


	@Override
	public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {

        String methodName = "delete(IPersistenceDTO, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;

        knLogger.debug(methodName, "ENTRY: Delete User Profile Mdn Map");
        try {

            KnSubsProfilePersistDTO subsInfoPersistDTO = (KnSubsProfilePersistDTO) persistencDTO;
            String mdn = subsInfoPersistDTO.getMdn();

            query = DELETE_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            //conn = persistTxn.getDBConnection(pttServerId, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, mdn);

            knLogger.debug(methodName, "Query: Executing - ", query, ", mdn - ", KnGDPRTemplate.mdn(mdn));
            pStmt.executeUpdate();
            knLogger.debug(methodName, "Query: Executed ");


        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (SQLException sqlE) {
            knLogger.error(methodName, "SQL Exception occurred");
            throw KnDbUtil.processException(sqlE, "Failed to delete Subscriber Profile - " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
            knLogger.debug(methodName, "EXIT : delete User Profile Mdn Map");
        }
    
	}
	
	 /**
     * method to retrieve User Profile Mdn Map
     *
     * @param baseMdn  String
     * @param persisterTxn KnPersisterTxn
     * @return Map<String, String> baseMdn,profileMdn
     * @throws KnDAOException DAO Layer exception
     */
    public Map<String, String> getUserProfileMdnMap(String baseMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getUserProfileMdnMap(String, KnPersisterTxn)";
        String query = GET_USER_PROFILE_MDN_MAP_BY_MDN;
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<String, String> userProfileMdnMap= new HashMap<String, String>();

        knLogger.entry(methodName, KnGDPRTemplate.mdn(baseMdn), persisterTxn);
        try {

            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, baseMdn);

            knLogger.debug( methodName, QRY_EXECUTING_MSG, query, " with baseMdn - ", KnGDPRTemplate.mdn(baseMdn));
            rs = pStmt.executeQuery();
            knLogger.debug( methodName, QRY_EXE_MSG);

            if (rs.next()) {
            	userProfileMdnMap.put(rs.getString(BASEMDN), rs.getString(PROFILEMDN));
            }

        } catch (SQLException sqlE) {
            knLogger.error( methodName, "SQLException occured");
            throw KnDbUtil.processException(sqlE, "Failed to user Profile Mdn Map- " + sqlE.getMessage(), pttServerId, KnProvDAOSourceTypes.USERPROFILEMDNMAP, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }

        return userProfileMdnMap;
    }
	
    @Override
	public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
		final String methodName = "update(IPersistenceDTO, KnPersisterTxn)";
        knLogger.entry(methodName, "Entry-->", persistTxn);
		Connection conn;
		PreparedStatement pStmt = null;
		String query = null;

		try {

			KnSubsProfilePersistDTO subsInfoPersistDTO = (KnSubsProfilePersistDTO) persistenceDTO;
			String mdn = subsInfoPersistDTO.getMdn().trim();
			String profileMdn = subsInfoPersistDTO.getProfileMdn().trim();

			// multilingual revert changes
			StringBuilder queryBuffer = new StringBuilder();
			queryBuffer.append("UPDATE ").append(TABLENAME).append(" SET ");
			queryBuffer.append(PROFILEMDN).append("=?");
			queryBuffer.append(" WHERE ").append(BASEMDN).append("=?");

			query = queryBuffer.toString();

			conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			pStmt = conn.prepareStatement(query);
			int columnIndex = 0;
			// multilingual revert changes
			pStmt.setString(++columnIndex, profileMdn);
			pStmt.setString(++columnIndex, mdn);

			knLogger.debug(methodName, QRY_EXECUTING_MSG, query);
			int result = pStmt.executeUpdate();
			knLogger.debug(methodName, QRY_EXE_MSG, result);

		} catch (KnDAOException dbConne) {
			knLogger.error(methodName, "DAO Exception occurred");
			throw dbConne;
		} catch (SQLException sqlE) {
			knLogger.error(methodName, "SQL Exception occurred");
			throw KnDbUtil.processException(sqlE, "Failed to update User Profile Mdn Map - " + sqlE.getMessage(),
					pttServerId, KnProvDAOSourceTypes.POCSUBSCRINFO, query);
		} catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception - ", e);
			throw KnDbUtil.processException(e, "Failed to update User Profile Mdn Map - " + e.getMessage(), pttServerId,
					KnProvDAOSourceTypes.POCSUBSCRINFO, query);
		} finally {
            KnDbUtil.closePreparedStatement(pStmt);
			knLogger.debug(methodName, "EXIT : Failed to update User Profile Mdn Map");
		}
	}

	@Override
	public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
		// TODO Auto-generated method stub
		return null;
	}


}
