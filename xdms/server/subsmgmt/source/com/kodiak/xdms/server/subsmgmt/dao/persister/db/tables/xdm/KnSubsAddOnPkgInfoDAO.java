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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

public class KnSubsAddOnPkgInfoDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSubsAddOnPkgInfoDAO.class);
	private String pttServerId;
	private static final String MDN="MDN";
	private static final String ADDON_PKGCODE="ADDON_PKGCODE";
    private static final String QRY_SELECT_ADDONPACKAGES="SELECT "+ ADDON_PKGCODE +" FROM DG.SUBSCR_ADDON_PKGINFO WHERE "+ MDN+" = ? ";
    private static final String QRY_INSERT_ADDONPACKAGES="INSERT INTO DG.SUBSCR_ADDON_PKGINFO ( "+MDN+" , "+ADDON_PKGCODE+" ) VALUES(?,?)";
    private static final String QRY_DELETE_ADDONPACKAGES="DELETE FROM DG.SUBSCR_ADDON_PKGINFO WHERE "+MDN+" = ?";
    private static final String QRY_BULK_SELECT_ADDONPACKAGES="SELECT "+MDN+" , "+ADDON_PKGCODE+" FROM DG.SUBSCR_ADDON_PKGINFO WHERE "+MDN+" IN (";
   
	public KnSubsAddOnPkgInfoDAO(String pttServerId) {
		this.pttServerId = pttServerId;
	}
	
	
	
	public List<String> selectSubAddOnPkgs(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
		final String methodName="selectSubAddOnPkgs(String mdn, KnPersisterTxn persistTxn)";
        knLogger.debug(methodName,"ENTRY: mdn ",KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStmt = null;
        List<String> addonPkgCodes=new ArrayList<>();
        try {
        	 
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_SELECT_ADDONPACKAGES);
            pStmt.setString(1,mdn);
            knLogger.debug(methodName, "QUERY: Executing the query - ", QRY_SELECT_ADDONPACKAGES);
            ResultSet rs=pStmt.executeQuery();
            while(rs.next())
            {
            	addonPkgCodes.add(rs.getString(ADDON_PKGCODE));
            }
            knLogger.debug(methodName, "QUERY: Executed ");
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select addon package - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.ADDONPKGINFO, QRY_SELECT_ADDONPACKAGES);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName, "selectSubAddOnPkgs(String mdn, KnPersisterTxn persistTxn) ,pkgCodes",addonPkgCodes);
		return addonPkgCodes;
	}
	
	
	public void createSubAddOnPkgs(String mdn, List<String> addOnPkgs, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		final String methodName="createSubAddOnPkgs(String mdn, List<String> addOnPkgs, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName,"ENTRY: mdn ",KnGDPRTemplate.mdn(mdn)," addOnPkgs ",addOnPkgs);
        Connection conn;
        PreparedStatement pStmt = null;
        try {
        	 
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_INSERT_ADDONPACKAGES);
            for (String pkgCode : addOnPkgs) {
            	 pStmt.setString(1,mdn);
            	 pStmt.setString(2,pkgCode );
            	 pStmt.addBatch();
			}
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_INSERT_ADDONPACKAGES);

            int[] count = pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed addon packages count:", count);

        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to create addon package - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.ADDONPKGINFO, QRY_INSERT_ADDONPACKAGES);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
       
	}
		
	public void deleteSubAddlOnPkgs(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
		final String methodName="deleteSubAddlOnPkgs(String mdn, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName,"ENTRY: mdn ",KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStmt = null;
        try {
        	 
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_DELETE_ADDONPACKAGES);
            pStmt.setString(1,mdn);
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_DELETE_ADDONPACKAGES);

            pStmt.execute();
            knLogger.debug(methodName, "Query: Executed ");

        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete Addon package - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.ADDONPKGINFO, QRY_DELETE_ADDONPACKAGES);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
		
	}

    public void deleteSubAddlOnPkgs(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName="deleteSubAddlOnPkgs(String mdn, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName,"ENTRY: mdn ",KnGDPRTemplate.mdnList(mdnList));
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        int index = 1;
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = "DELETE FROM DG.SUBSCR_ADDON_PKGINFO WHERE MDN IN (MDNLIST)";
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(mdnList,query,"MDNLIST");
            pStmt = conn.prepareStatement(query);
            for(String mdn : mdnList){
                pStmt.setString(index++,mdn);
            }
            knLogger.debug(methodName, "QUERY: Executing the Query - ", query);

            pStmt.execute();
            knLogger.debug(methodName, "Query: Executed ");

        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete Addon package - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.ADDONPKGINFO, QRY_DELETE_ADDONPACKAGES);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }

    }
	
	public Map<String,List<String>> selectSubAddOnPkgs(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
		final String methodName="selectSubAddOnPkgs(List<String> mdns, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName,"ENTRY:mdns",KnGDPRTemplate.mdnList(mdns));
        Connection conn;
        PreparedStatement pStmt = null;
        Map<String,List<String>> mdnAddonPkgMap=new HashMap<>();
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(QRY_BULK_SELECT_ADDONPACKAGES);
        for(int i=0;i<mdns.size();i++){
        strBuffer.append("?,");
         }
        strBuffer.deleteCharAt(strBuffer.length()-1);
        strBuffer.append(")");
        String query = strBuffer.toString();
        knLogger.debug(methodName, "QUERY: Executing the query - ", query);
        try {
        	 
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStmt = conn.prepareStatement(query);
            int i=0;
            for (String mdn : mdns) {
            	  pStmt.setString(++i,mdn);
			}
          
          
            ResultSet rs=pStmt.executeQuery();
            while(rs.next())
            {
            	if(mdnAddonPkgMap.get(rs.getString(MDN))!=null)
            	{
            		mdnAddonPkgMap.get(rs.getString(MDN).trim()).add(rs.getString(ADDON_PKGCODE));
            	}
            	else
            	{
            		 List<String> addonPkgCodes=new ArrayList<>();
            		 addonPkgCodes.add(rs.getString(ADDON_PKGCODE));
            		 mdnAddonPkgMap.put(rs.getString(MDN).trim(), addonPkgCodes);
            	}
            	
            }
            knLogger.debug(methodName, "QUERY: Executed ");
        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to select addon package - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.ADDONPKGINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName, "selectSubAddOnPkgs(List<String> mdns, KnPersisterTxn persisterTxn) ,mdnAddonPkgMap",KnGDPRTemplate.mapKeyMdn(mdnAddonPkgMap));
		return mdnAddonPkgMap;
		
	}

	public void createBulkSubAddOnPkgs(List<String> mdns, List<String> addOnPkgs, KnPersisterTxn persisterTxn)
			throws KnDAOException {
		final String methodName="createBulkSubAddOnPkgs(List<String> mdn, List<String> addOnPkgs, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName,"ENTRY: mdns ",KnGDPRTemplate.mdnList(mdns)," addOnPkgs ",addOnPkgs);
        Connection conn;
        PreparedStatement pStmt = null;
        try {
        	 
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_INSERT_ADDONPACKAGES);
            
			for (String mdn : mdns) {
				for (String pkgCode : addOnPkgs) {
					pStmt.setString(1, mdn);
					pStmt.setString(2, pkgCode);
					pStmt.addBatch();
				}
			}
            
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_INSERT_ADDONPACKAGES);

            int[] count = pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed addon packages count:", count);

        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to create addon package - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.ADDONPKGINFO, QRY_INSERT_ADDONPACKAGES);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
	}
	
	public void deleteBulkSubAddlOnPkgs(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
		final String methodName="deleteBulkSubAddlOnPkgs(List<String> mdns, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName,"ENTRY: mdns ",KnGDPRTemplate.mdnList(mdns));
        Connection conn;
        PreparedStatement pStmt = null;
        try {
        	 
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(QRY_DELETE_ADDONPACKAGES);
            for (String mdn : mdns) {
                pStmt.setString(1, mdn);
                pStmt.addBatch();
            }
            knLogger.debug(methodName, "QUERY: Executing the Query - ", QRY_DELETE_ADDONPACKAGES);

            pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed ");

        }catch (Exception e) {
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
