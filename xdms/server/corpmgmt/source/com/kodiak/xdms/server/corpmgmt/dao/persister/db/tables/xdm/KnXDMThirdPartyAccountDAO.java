/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnTPVendorDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

public class KnXDMThirdPartyAccountDAO implements ITableDAO{
	 private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpListMemberDAO.class);
	
	 public String pttServerId = null;

	 public KnXDMThirdPartyAccountDAO(String pttServerId) {
	        this.pttServerId = pttServerId;
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
	
	public Map<String, String> retrieveVendorID(List<String> tpMdnList, KnPersisterTxn persistTxn) throws KnDAOException {
		 String methodName = "retrieveTPVendorByMDN(String)";
	        knLogger.debug(methodName, "mdn", tpMdnList == null ? tpMdnList : KnGDPRTemplate.mdnList(tpMdnList));
	        Connection conn;
	        String query = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        int index=1;
	        Map<String, String> VendorMdnMap = new HashMap<>();
	        try {
	            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
	            query = queryMapper.getQuery(KnPersisterConstants.GET_TP_VENOR_ID_BY_MDN);
	            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
				Collection<String> mdnCollection = new ArrayList<>(tpMdnList);
				query = replaceContactWithValue(query, KnPersisterConstants.MDNLIST,formCommaSeperatedQuesMarks(mdnCollection));
				knLogger.debug(methodName, "query", query);
				pstmt = conn.prepareStatement(query);
				for(String mdn:tpMdnList){
					pstmt.setString(index++, mdn);
				}
				rs = pstmt.executeQuery();
	            knLogger.debug(methodName, "Executing query - ", query);
	            while(rs.next()){
	            	String vendorID = rs.getString(1);
	            	String mdn =  rs.getString(2).trim();
	            	VendorMdnMap.put(mdn, vendorID);
	            }
	            knLogger.debug(methodName, "EXit: Query executed successfully");
	            
	        } catch (SQLException e) {
	            throw KnDbUtil.processException(e, "Failed  to retrieve vendorID",
	                    pttServerId, KnDAOSourceTypes.XDM_TP_ACCOUNT_INFO, query);

			} finally {
				KnDbUtil.closeResultSet(rs);
				KnDbUtil.closePreparedStatement(pstmt);
			}
	        knLogger.exit(methodName, KnGDPRTemplate.mdnMap(VendorMdnMap));
	        return VendorMdnMap;
	}
	
	
	public KnTPVendorDetailsPersistDTO retrieveVendorDetails(String vendorID, KnPersisterTxn persistTxn) throws KnDAOException {
		 String methodName = "retrieveVendorDetails(String)";
	        knLogger.debug(methodName, "mdn", KnGDPRTemplate.mdn(vendorID));
	        Connection conn;
	        String query = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        KnTPVendorDetailsPersistDTO vendorDetails = null;
	        try {
	            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
	            query = queryMapper.getQuery(KnPersisterConstants.GET_TP_VENOR_DETAILS);
	            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
	            pstmt = conn.prepareStatement(query);
	            knLogger.debug(methodName, "Executing query - ", query);
	            pstmt.setString(1, vendorID);
	            rs = pstmt.executeQuery();
	            while(rs.next()){
	            	vendorDetails = new KnTPVendorDetailsPersistDTO();
	            	vendorDetails.setTpAccountId(rs.getInt(1));
	            	vendorDetails.setTpAccount(rs.getString(2).trim());
	            	vendorDetails.setPassword(rs.getString(3));
	            	vendorDetails.setEmailId(rs.getString(4));
	            	vendorDetails.setCreateTs(rs.getLong(5));
	            	vendorDetails.setLastUpdateTs(rs.getLong(6));
	            }
	            knLogger.debug(methodName, "EXit: Query executed successfully");
	            
	        } catch (SQLException e) {
	            throw KnDbUtil.processException(e, "Failed  to retrieve vendorID",
	                    pttServerId, KnDAOSourceTypes.XDM_TP_ACCOUNT_INFO, query);

			} finally {
				KnDbUtil.closeResultSet(rs);
				KnDbUtil.closePreparedStatement(pstmt);
			}
	        knLogger.exit(methodName, vendorDetails);
	        return vendorDetails;
	}
	


}
