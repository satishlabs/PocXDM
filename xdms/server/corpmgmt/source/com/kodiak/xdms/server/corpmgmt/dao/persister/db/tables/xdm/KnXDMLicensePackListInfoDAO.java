/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMLicensePackListInfoDAO.java
 * Subsystem:  PoC
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     09/10/2014    7.10
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
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnLicensePackDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;


public class KnXDMLicensePackListInfoDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger
			.getLogger(KnXDMLicensePackListInfoDAO.class);

	private String pttServerId = null;

	KnXDMLicensePackListInfoDAO(String pttServerId) {
		this.pttServerId = pttServerId;
	}

	@Override
	public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn)
			throws KnDAOException {

	}

	@Override
	public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn)
			throws KnDAOException {

	}

	@Override
	public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn)
			throws KnDAOException {

	}

	@Override
	public Collection select(IPersistenceDTO persistenceDTO,
			KnPersisterTxn persistTxn) throws KnDAOException {
		return null;
	}

	public List<KnLicensePackDTO> getBillingProfilesForCorpId(int corpId,
															  String corpName, boolean readOnly, KnPersisterTxn persistTxn) throws KnDAOException {
		final String methodName = "getBillingProfilesForCorpId(int, String,boolean, KnPersisterTxn)";
		knLogger.debug(methodName, corpId, corpName, persistTxn);

		Connection conn;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = null;
		List<KnLicensePackDTO> licensePackList = new ArrayList<KnLicensePackDTO>();
		try {
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(GET_BILLING_MDNS);
			conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
			pStmt = conn.prepareStatement(query);
			pStmt.setInt(1, corpId);
			knLogger.debug(methodName, "Executing query - ", query);
			rs = pStmt.executeQuery();
			knLogger.debug(methodName, "Query executed succesfully");
			while (rs.next()) {
				KnLicensePackDTO licensePackDTO = new KnLicensePackDTO();
				licensePackDTO.setBillingNumber(rs.getString(1).trim());
				licensePackDTO.setPamAccId(rs.getInt(2));
				licensePackDTO.setCorpId(corpId);
				licensePackDTO.setCorpName(corpName);
				//multilingual revert changes
				if(rs.getString(3) != null)
				try {
						licensePackDTO.setBillingName(new String(rs.getString(3).getBytes("8859_1"),"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					knLogger.error("UnsupportedEncodingException while parsing billing name ",e);
				}
				licensePackDTO.setTotalNoOfLines(rs.getInt(4));
				licensePackDTO.setAccountStatus(rs.getInt(5));
				licensePackDTO.seteTag(String.valueOf(rs.getLong(6)).trim());
				licensePackDTO.setSubscriptionType(getMappedSubscriptionType(
						rs.getInt(7), rs.getInt(8)));
				licensePackDTO.setSubsClientType(rs.getInt(9));
				String subsFS=rs.getString(11)!=null?rs.getString(11):KnGeneralUtil.convertLongToHexString(rs.getLong(10));
				licensePackDTO.setSubscriberFs2(subsFS);
				licensePackList.add(licensePackDTO);
			}

		} catch (SQLException sqlE) {

			throw KnDbUtil.processException(sqlE, "Failed while fetching the Billing Profiles For CorpId ." + sqlE, pttServerId,
                    KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeStatement(pStmt);
		}
		knLogger.debug(methodName, licensePackList.size());
		return licensePackList;
	}

	public KnCorpSubscriberDTO getPamAccountId(String billingNumber, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "getPamAccountId(int,  KnPersisterTxn)";
		knLogger.debug(methodName, billingNumber);
		Connection conn;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = null;
		KnCorpSubscriberDTO corpSubscriberDTO = null;
		try {
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(GET_PAMACC_ID);
			conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, billingNumber);
			knLogger.debug(methodName, "Executing query - ", query);
			rs = pstmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully.");
			if (rs.next()) {
				corpSubscriberDTO = new KnCorpSubscriberDTO();
				corpSubscriberDTO.setPamAccId(rs.getInt(1));
				corpSubscriberDTO.setEtag(rs.getLong(2));
			}
		} catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception occured while retrieving the PamAccId - ", e);
			throw KnDbUtil.processException(e, "Failed while retrieving the PamAccId Id -  " + e,
					pttServerId, KnDAOSourceTypes.LICENSEINFO, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeStatement(pstmt);
		}
		return corpSubscriberDTO;

	}

	private static String trim(String input) {
		return input != null ? input.trim() : input;
	}

	public Map<Integer, String> getBillingNumber(Set<Integer> pamAccIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "getBillingNumber(Set<Integer>,  KnPersisterTxn)";
		knLogger.debug( methodName, "Entry : ", pamAccIdList);
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String query = null;
		int index = 1;
		Map<Integer, String> pamBillingMdnMap =new HashMap<>();
		try {
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(GET_BILLING_MDN);
			Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
			query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(PAMACCIDLIST, pamAccIdList, query);
			pStmt = conn.prepareStatement(query);
			knLogger.debug(methodName, "Executing query - ", query);
			for(int pamAccId : pamAccIdList)
				pStmt.setInt(index++, pamAccId);
			rs = pStmt.executeQuery();
			knLogger.debug(methodName, "Query executed successfully");
			knLogger.debug(methodName, pamAccIdList);
			while (rs.next()) {
				pamBillingMdnMap.put(rs.getInt(2),trim(rs.getString(1)));
			}
			knLogger.debug(methodName, "List size from DB :  " , pamBillingMdnMap.size());
		}catch (SQLException e) {
			throw KnDbUtil.processException(e, "Failed while retrieving the Billing Mdn - - ",
					pttServerId, KnDAOSourceTypes.XDM_PAMACCOUNT_INFO, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeStatement(pStmt);
		}
		return pamBillingMdnMap;

	}
}