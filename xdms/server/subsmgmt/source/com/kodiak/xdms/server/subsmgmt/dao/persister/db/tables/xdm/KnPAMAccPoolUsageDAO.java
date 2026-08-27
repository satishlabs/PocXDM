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
import java.util.List;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnPAMAccPoolUsageDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnPAMAccPoolPersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

public class KnPAMAccPoolUsageDAO implements ITableDAO{
	
	private static final KnLogger knLogger = KnLogger.getLogger(KnPAMAccPoolUsageDAO.class);
	private String pttServerId;

	public KnPAMAccPoolUsageDAO(String pttServerId) {
		this.pttServerId = pttServerId;
	}
	
	private static final String PAMACCID = "PAMACCID";
    private static final String BILLINGMDN = "BILLINGMDN";
    private static final String MDN = "MDN";
    private static final String USAGE = "USAGE";
	
	private static final int NOT_IN_USE = 1;
	private static final String QRY_EXECUTING_MSG  = "QUERY: Executing - ";
	private static final String QRY_EXE_MSG  = "QUERY: Executed - ";
	private static final String SQL_EXCEPTION_MSG = "SQL Exception occurred";
	
	private static final String GET_BY_BILLINGMDN = "SELECT FIRST 1 MDN, PAMACCID FROM DG.PAMACCOUNT_POOL_USAGE WHERE BILLINGMDN=? and USAGE=? ORDER BY MDN";
	
	private static final String RETRIEVE_PAM_POOL = "SELECT PAMACCID, BILLINGMDN, USAGE FROM DG.PAMACCOUNT_POOL_USAGE WHERE MDN=?";
	
	private static final String INSERT_QRY = "INSERT INTO DG.PAMACCOUNT_POOL_USAGE (PAMACCID, BILLINGMDN, MDN, USAGE) VALUES (?, ?, ?, ?)";
	
	private static  String DELETE_MDN_QRY = "DELETE FROM DG.PAMACCOUNT_POOL_USAGE WHERE MDN=?";
	
	public void createPAMAccPoolUsage(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
		final String methodName = "createPAMAccPoolUsage(String)";
		Connection conn;
		PreparedStatement pStmt = null;
		int[] count;
		try {
			KnPAMAccPoolPersistDTO pamAccPoolPersistDTO = (KnPAMAccPoolPersistDTO) persistenceDTO;
			knLogger.entry( methodName, pamAccPoolPersistDTO, persisterTxn);
			KnPAMAccPoolUsageDTO pamAccPoolUsageDTO = pamAccPoolPersistDTO.getPamAccPoolUsage();
			conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			pStmt = conn.prepareStatement(INSERT_QRY);
			
			List<String> mdnList = pamAccPoolPersistDTO.getMdns();
			int pamAccID = pamAccPoolUsageDTO.getPamAccId();
			String billingMDN =  pamAccPoolUsageDTO.getBillingMdn();
			int usage = pamAccPoolUsageDTO.getUsage();
			
			for (String mdn : mdnList) {
				pStmt.setInt(1, pamAccID);
				pStmt.setString(2, billingMDN);
				pStmt.setString(3, mdn);
				pStmt.setInt(4, usage);
				
				pStmt.addBatch();
			}
			
			knLogger.debug(methodName, QRY_EXECUTING_MSG, INSERT_QRY);
			count = pStmt.executeBatch();
			knLogger.debug(methodName, QRY_EXE_MSG);
			
			
		} catch (SQLException e) {
			knLogger.error(methodName, SQL_EXCEPTION_MSG);
			throw KnDbUtil.processException(e, "Failed to get mdn - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.PAMACCOUNT_POOL_USAGE, INSERT_QRY);
			
		} finally {
			KnDbUtil.closePreparedStatement(pStmt);
		}
		knLogger.exit(methodName, "query executed size - ", count.length);
	}
	
	public void deletePAMAccPoolUsage(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
		final String methodName = "deletePAMAccPoolUsage(String)";
		knLogger.entry( methodName, "MDN size -", mdns.size(), persisterTxn);
		Connection conn;
		PreparedStatement pStmt = null;
		int[] count;
		try {
			conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			pStmt = conn.prepareStatement(DELETE_MDN_QRY);
			
			
			for (String mdn : mdns) {
				pStmt.setString(1, mdn);
				pStmt.addBatch();
			}
			
			knLogger.debug(methodName, QRY_EXECUTING_MSG, DELETE_MDN_QRY);
			count = pStmt.executeBatch();
			knLogger.debug(methodName, QRY_EXE_MSG);
			
			
		} catch (SQLException e) {
			knLogger.error(methodName, SQL_EXCEPTION_MSG);
			throw KnDbUtil.processException(e, "Failed to get mdn - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.PAMACCOUNT_POOL_USAGE, DELETE_MDN_QRY);
			
		} finally {
			KnDbUtil.closePreparedStatement(pStmt);
		}
		knLogger.exit(methodName, "query executed size - ", count.length);
	}
	
	public int retrievePAMAccPoolUsageForMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
		final String methodName = "retrievePAMAccPoolUsageForMDN(String)";
		knLogger.entry( methodName, mdn, persisterTxn);
		Connection conn;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		int result = 0;
		try {
			conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			pStmt = conn.prepareStatement(RETRIEVE_PAM_POOL);
			pStmt.setString(1, mdn);
			
			knLogger.debug(methodName, QRY_EXECUTING_MSG, RETRIEVE_PAM_POOL);
			rs = pStmt.executeQuery();
			knLogger.debug(methodName, QRY_EXE_MSG);
			
			if (rs.next()) {
				knLogger.info(methodName, "TP PAMAcc pool exist");
				result = rs.getInt(USAGE);
			}
			
		} catch (SQLException e) {
			knLogger.error(methodName, SQL_EXCEPTION_MSG);
			throw KnDbUtil.processException(e, "Failed to get mdn - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.PAMACCOUNT_POOL_USAGE, RETRIEVE_PAM_POOL);
			
		} finally {
			 KnDbUtil.closeResultSet(rs);
			 KnDbUtil.closePreparedStatement(pStmt);
		}
		
		knLogger.exit(methodName, "Mdn usage - ", result);
		return result;
	}
	
	public KnPAMAccPoolUsageDTO getFirstUnusedMdnFromPAMAccPoolUsage(String billingMDN, KnPersisterTxn persisterTxn) throws KnDAOException{
		final String methodName = "getFirstUnusedMdnFromPAMAccPoolUsage(String)";
		knLogger.entry( methodName, billingMDN, persisterTxn);
		KnPAMAccPoolUsageDTO pamAccPoolUsageDTO = null;
		Connection conn;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			pStmt = conn.prepareStatement(GET_BY_BILLINGMDN);
			pStmt.setString(1, billingMDN);
			pStmt.setInt(2, NOT_IN_USE);
			
			knLogger.debug(methodName, QRY_EXECUTING_MSG, GET_BY_BILLINGMDN);
			rs = pStmt.executeQuery();
			knLogger.debug(methodName, QRY_EXE_MSG);
			
			if (rs.next()) {
				pamAccPoolUsageDTO = new KnPAMAccPoolUsageDTO();
				pamAccPoolUsageDTO.setMdn(rs.getString(MDN));
				pamAccPoolUsageDTO.setPamAccId(rs.getInt(PAMACCID));
			}
			
		} catch (SQLException e) {
			knLogger.error(methodName, SQL_EXCEPTION_MSG);
			throw KnDbUtil.processException(e, "Failed to get mdn - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.PAMACCOUNT_POOL_USAGE, GET_BY_BILLINGMDN);
			
		} finally {
			 KnDbUtil.closeResultSet(rs);
			 KnDbUtil.closePreparedStatement(pStmt);
		}
		
		knLogger.exit(methodName, pamAccPoolUsageDTO);
		return pamAccPoolUsageDTO;
	}

	@Override
	public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
		
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
