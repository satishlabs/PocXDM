/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

public class KnPAMSubsAddOnPkgInfoDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSubsAddOnPkgInfoDAO.class);
	private String pttServerId;
    private static final String QRY_SELECT_ADDONPACKAGES="SELECT ADDON_PKGCODE FROM DG.PAMSUBSCR_ADDON_PKGINFO WHERE PAMACCID = ? ";
    private static final String QRY_INSERT_ADDONPACKAGES="INSERT INTO DG.PAMSUBSCR_ADDON_PKGINFO (PAMACCID,ADDON_PKGCODE) VALUES(?,?)";
    private static final String QRY_DELETE_ADDONPACKAGES="DELETE FROM DG.PAMSUBSCR_ADDON_PKGINFO WHERE PAMACCID = ?";
    
	public KnPAMSubsAddOnPkgInfoDAO(String pttServerId) {
		this.pttServerId = pttServerId;
	}
	
	public List<String> selectPAMSubAddOnPkgs(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
		final String methodName="selectPAMSubAddOnPkgs(int pamAccId, KnPersisterTxn persistTxn)";
        knLogger.debug(methodName,"ENTRY: pamAccId",pamAccId);
        Connection conn;
        PreparedStatement pStmt = null;
        List<String> addonPkgCodes=new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_SELECT_ADDONPACKAGES);
            pStmt.setInt(1,pamAccId);
            knLogger.debug(methodName, "QUERY: Executing the query - ", QRY_SELECT_ADDONPACKAGES);
            ResultSet rs=pStmt.executeQuery();
            while(rs.next())
            {
            	addonPkgCodes.add(rs.getString("ADDON_PKGCODE"));
            }
            knLogger.debug(methodName, "QUERY: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select addon package - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.ADDONPKGINFO, QRY_SELECT_ADDONPACKAGES);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        
        knLogger.debug(methodName, "selectPAMSubAddOnPkgs(int pamAccId, KnPersisterTxn persistTxn) ,pkgCodes",addonPkgCodes);
		return addonPkgCodes;
	}

	
	public void createPAMSubAddOnPkgs(int pamAccId, List<String> addOnPkgs, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		final String methodName="createPAMSubAddOnPkgs(int pamAccId, List<String> addOnPkgs, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName,"ENTRY: pamAccId ",pamAccId," addOnPkgs ",addOnPkgs);
        Connection conn;
        PreparedStatement pStmt = null;
        try {
        	 
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_INSERT_ADDONPACKAGES);
            for (String pkgCode : addOnPkgs) {
            	 pStmt.setInt(1,pamAccId);
            	 pStmt.setString(2,pkgCode );
            	 pStmt.addBatch();
			}
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_INSERT_ADDONPACKAGES);

            int[] count = pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed addon packages count:", count);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to create addon package - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.ADDONPKGINFO, QRY_INSERT_ADDONPACKAGES);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
		
	}

	public void deletePAMSubAddlOnPkgs(int pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException {
		final String methodName="deletePAMSubAddlOnPkgs(int pamAccId, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName,"ENTRY: pamAccId ",pamAccId);
        Connection conn;
        PreparedStatement pStmt = null;
        try {
        	 
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_DELETE_ADDONPACKAGES);
            pStmt.setInt(1,pamAccId);
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_DELETE_ADDONPACKAGES);

            pStmt.execute();
            knLogger.debug(methodName, "Query: Executed ");

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete Addon package - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.ADDONPKGINFO, QRY_DELETE_ADDONPACKAGES);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
		
	}
	
	

	@Override
	public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
