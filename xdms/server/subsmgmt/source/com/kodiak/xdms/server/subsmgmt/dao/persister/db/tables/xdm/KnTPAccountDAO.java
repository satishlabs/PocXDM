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

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnTPVendorDetailsDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

public class KnTPAccountDAO implements ITableDAO{
	 private static final KnLogger knLogger = KnLogger.getLogger(KnTPAccountDAO.class);

	 public String pttServerId = null;

	 public KnTPAccountDAO(String pttServerId) {
	        this.pttServerId = pttServerId;
	    }

	 private static final String GET_QUERY = "SELECT THIRD_PARTY_ID, THIRD_PARTY_ACCT_ID, PASSWORD, EMAIL_ID, CREATE_TS,"
	 		+ " LASTUPDATE_TS FROM DG.THIRD_PARTY_ACCOUNT_INFO WHERE THIRD_PARTY_ACCT_ID=?";

	 private static final String QRY_EXECUTING_MSG  = "QUERY: Executing - ";

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

	public KnTPVendorDetailsDTO retrieveVendorDetails(String vendorID, KnPersisterTxn persistTxn) throws KnDAOException {
		 String methodName = "retrieveVendorDetails(String)";
	        knLogger.debug(methodName, "mdn", vendorID);
	        Connection conn;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        KnTPVendorDetailsDTO vendorDetails = null;
	        try {
	            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
	            pstmt = conn.prepareStatement(GET_QUERY);
	            pstmt.setString(1, vendorID);
	            knLogger.debug( methodName, QRY_EXECUTING_MSG, GET_QUERY);
	            rs = pstmt.executeQuery();
	            while(rs.next()){
	            	vendorDetails = new KnTPVendorDetailsDTO();
	            	vendorDetails.setTpAccountId(rs.getInt(1));
	            	vendorDetails.setTpAccount(rs.getString(2).trim());
	            	vendorDetails.setPassword(rs.getString(3));
	            	vendorDetails.setEmailId(rs.getString(4));
	            	vendorDetails.setCreateTs(rs.getLong(5));
	            	vendorDetails.setLastUpdateTs(rs.getLong(6));
	            }
	            knLogger.debug(methodName, "EXit: Query executed successfully");

	        } catch (SQLException e) {
	            throw KnDbUtil.processException(e, "Failed  to retrieve vendorID - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.XDM_TP_ACCOUNT_INFO, GET_QUERY);

	        } finally {
	            KnDbUtil.closeResultSet(rs);
				KnDbUtil.closeStatement(pstmt);
	        }
	        knLogger.exit(methodName, vendorDetails);
	        return vendorDetails;
	}

}
