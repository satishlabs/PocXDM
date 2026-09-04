/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnTPUserAccountDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnTPUserPersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;


public class KnTPUserMDNMapDAO implements ITableDAO{
	private static final KnLogger knLogger = KnLogger.getLogger(KnTPUserMDNMapDAO.class);
	private String pttServerId;

	public KnTPUserMDNMapDAO(String pttServerId) {
		this.pttServerId = pttServerId;
	}

	private static final String TP_USER_ID = "THIRD_PARTY_USER_ID";
    private static final String TP_USER = "THIRD_PARTY_USER";
    private static final String TP_ID = "THIRD_PARTY_ID";
    private static final String MDN = "MDN";
    private static final String CREATE_TS = "CREATE_TS";

	private static final String QRY_EXECUTING_MSG  = "QUERY: Executing - ";
	private static final String QRY_EXE_MSG  = "QUERY: Executed - ";
	private static final String SQL_EXCEPTION_MSG = "SQL Exception occurred";

	private static final String INSERT_QRY = "INSERT INTO DG.THIRD_PARTY_USER_MDN_MAP (THIRD_PARTY_USER_ID, THIRD_PARTY_USER, THIRD_PARTY_ID, MDN, CREATE_TS)"
			+ " VALUES (?, ?, ?, ?, ?)";

	private static final String GET_QUERY = "SELECT THIRD_PARTY_USER, THIRD_PARTY_ID FROM DG.THIRD_PARTY_USER_MDN_MAP WHERE MDN=?";

	private static final String UPDATE_QUERY = "UPDATE DG.THIRD_PARTY_USER_MDN_MAP SET THIRD_PARTY_USER=? WHERE MDN=?";

	private static final String DELETE_QUERY = "DELETE FROM DG.THIRD_PARTY_USER_MDN_MAP WHERE MDN=?";

	private static final String GET_USER_QUERY = "SELECT MDN, THIRD_PARTY_ID FROM DG.THIRD_PARTY_USER_MDN_MAP WHERE THIRD_PARTY_USER = ?";


	@Override
	public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
		final String methodName = "insert(IPersistenceDTO, KnPersisterTxn)";
		knLogger.info(methodName, persistenceDTO);
		Connection conn;
		PreparedStatement pStmt = null;
		int result;

		try {
			KnTPUserPersistDTO tpUserPersistDTO = (KnTPUserPersistDTO) persistenceDTO;
			conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			pStmt = conn.prepareStatement(INSERT_QRY);

			pStmt.setInt(1, tpUserPersistDTO.getTpUserId());
			pStmt.setString(2, tpUserPersistDTO.getTpUser());
			pStmt.setInt(3, tpUserPersistDTO.getTpAccountId());
			pStmt.setString(4, tpUserPersistDTO.getMdn());
			pStmt.setLong(5, tpUserPersistDTO.getCreationTime());

			knLogger.debug( methodName, QRY_EXECUTING_MSG, INSERT_QRY);
	        result = pStmt.executeUpdate();
	        knLogger.debug( methodName, QRY_EXE_MSG, result);

		} catch (SQLException e) {
			knLogger.error(methodName, SQL_EXCEPTION_MSG);
			throw KnDbUtil.processException(e, "Failed to create TP Account - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.THIRD_PARTY_USER_MDN_MAP, INSERT_QRY);
		} finally {
			KnDbUtil.closeStatement(pStmt);
		}

		knLogger.exit(methodName, result);
	}

	public KnTPUserAccountDTO retrieveTPUserAccountForMDN(String mdn, KnPersisterTxn persistTxn) throws KnDAOException {
		final String methodName = "retrieveTPUserAccountForMDN(String, KnPersisterTxn)";
		knLogger.info(methodName, KnGDPRTemplate.mdn(mdn));
		Connection conn;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		KnTPUserAccountDTO tpUserPersistDTO = null;
		try {
			conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			pStmt = conn.prepareStatement(GET_QUERY);
			pStmt.setString(1, mdn);

			knLogger.debug( methodName, QRY_EXECUTING_MSG, GET_QUERY);
			rs = pStmt.executeQuery();
			knLogger.debug( methodName, QRY_EXE_MSG);

			if (rs.next()) {
				tpUserPersistDTO = new KnTPUserAccountDTO();
				tpUserPersistDTO.setMdn(mdn);
				tpUserPersistDTO.setTpUser(rs.getString(TP_USER));
				tpUserPersistDTO.setTpAccId(rs.getInt(TP_ID));
			}

		} catch (SQLException e) {
			knLogger.error(methodName, SQL_EXCEPTION_MSG);
			throw KnDbUtil.processException(e, "Failed to create TP Account - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.THIRD_PARTY_USER_MDN_MAP, GET_QUERY);

		} finally {
            KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeStatement(pStmt);
        }

		knLogger.exit(methodName, tpUserPersistDTO);
		return tpUserPersistDTO;
	}

	public void updateTPUserMDNMapByMDN(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
		final String methodName = "updateTPUserMDNMapByMDN(IPersistenceDTO, KnPersisterTxn)";
		knLogger.info(methodName, persistenceDTO);
		Connection conn;
		PreparedStatement pStmt = null;
		int res;

		try {
			KnTPUserPersistDTO tpUserPersistDTO = (KnTPUserPersistDTO) persistenceDTO;
			conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			pStmt = conn.prepareStatement(UPDATE_QUERY);

			pStmt.setString(1, tpUserPersistDTO.getTpUser());
			pStmt.setString(2, tpUserPersistDTO.getMdn());

			knLogger.debug( methodName, QRY_EXECUTING_MSG, UPDATE_QUERY);
	        res = pStmt.executeUpdate();
	        knLogger.debug( methodName, QRY_EXE_MSG, res);

		} catch (SQLException e) {
			knLogger.error(methodName, SQL_EXCEPTION_MSG);
			throw KnDbUtil.processException(e, "Failed to create TP Account - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.THIRD_PARTY_USER_MDN_MAP, UPDATE_QUERY);
		} finally {
			KnDbUtil.closeStatement(pStmt);
		}

		knLogger.exit(methodName, res);
	}

	public void deleteTPUserMDNMap(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
		final String methodName = "deleteTPUserMDNMap(IPersistenceDTO, KnPersisterTxn)";
		knLogger.info(methodName, persistenceDTO);
		Connection conn;
		PreparedStatement pStmt = null;
		int res = 0;
        String query = null;
		try {
			KnTPUserPersistDTO tpUserPersistDTO = (KnTPUserPersistDTO) persistenceDTO;
			conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            if(null != tpUserPersistDTO.getMdnList() && !tpUserPersistDTO.getMdnList().isEmpty()){
                query = "DELETE FROM DG.THIRD_PARTY_USER_MDN_MAP WHERE MDN = ?";
                //query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(tpUserPersistDTO.getMdnList(),query,"MDNLIST");
                pStmt = conn.prepareStatement(query);
                for(String mdn : tpUserPersistDTO.getMdnList()){
                    pStmt.setString(1, mdn);
                    pStmt.addBatch();
                }
                res = pStmt.executeBatch().length;
                knLogger.debug(methodName, QRY_EXE_MSG, res);
            } else {
                pStmt = conn.prepareStatement(DELETE_QUERY);
                pStmt.setString(1, tpUserPersistDTO.getMdn());

                knLogger.debug(methodName, QRY_EXECUTING_MSG, DELETE_QUERY);
                res = pStmt.executeUpdate();
                knLogger.debug(methodName, QRY_EXE_MSG, res);
            }
		} catch (SQLException e) {
			knLogger.error(methodName, SQL_EXCEPTION_MSG);
			throw KnDbUtil.processException(e, "Failed to create TP Account - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.THIRD_PARTY_USER_MDN_MAP, DELETE_QUERY);
		} finally {
			KnDbUtil.closeStatement(pStmt);
		}

		knLogger.exit(methodName, res);
	}

	public KnTPUserAccountDTO getTPUserDetails(String userName, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "getTPUserDetails(userId)";
        knLogger.info(methodName, "ENTRY : userName - ", userName);

		Connection conn;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		KnTPUserAccountDTO tpUserPersistDTO = null;
		try {
			conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			pStmt = conn.prepareStatement(GET_USER_QUERY);
			pStmt.setString(1, userName);

			knLogger.debug( methodName, QRY_EXECUTING_MSG, GET_USER_QUERY);
			rs = pStmt.executeQuery();
			knLogger.debug( methodName, QRY_EXE_MSG);

			if (rs.next()) {
				tpUserPersistDTO = new KnTPUserAccountDTO();
				tpUserPersistDTO.setMdn(rs.getString(MDN));
				tpUserPersistDTO.setTpAccId(rs.getInt(TP_ID));
			}

		} catch (SQLException e) {
			knLogger.error(methodName, SQL_EXCEPTION_MSG);
			throw KnDbUtil.processException(e, "Failed to create TP Account - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.THIRD_PARTY_USER_MDN_MAP, GET_QUERY);

		} finally {
            KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeStatement(pStmt);
        }

		knLogger.exit(methodName, tpUserPersistDTO);
		return tpUserPersistDTO;
    }

	@Override
	public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

	}

	@Override
	public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {

	}

	@Override
	public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
		return null;
	}




}
