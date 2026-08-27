/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

public class KnLicenseInfoDAO implements ITableDAO {

	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupDistInfoDAO.class);

	private String pttServerId;

	public KnLicenseInfoDAO(String pttServerId) {
		this.pttServerId = pttServerId;
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

	public String getCorpAdminFS2BitMask(KnPersisterTxn persisterTxn) throws KnDAOException {
		String methodName = "getCorpAdminFS2BitMask(KnPersisterTxn persisterTxn)";
		knLogger.debug(methodName);
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		String bitMask = null;
		try {
			Connection conn = persisterTxn.getDBConnection(pttServerId, false);
			KnQueryMapper queryMapper = KnQueryMapper.getInstance();
			query = queryMapper.getQuery(KnPersisterConstants.SELECT_CORPFS1_BITMASK);
			stmt = conn.createStatement();
			knLogger.debug(methodName, "Executing query - ", "'", query, "'");
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				bitMask = rs.getString(1);
			} else {
				knLogger.fatal( methodName, "CORPFS2BITMASK NOT EXIST");
                throw new KnDBPersistenceException(DAO.ROW_NOT_FOUND,
                        "CORPFS2BITMASK NOT EXIST", pttServerId, KnDAOSourceTypes.LICENSEINFO, query);
			}
			knLogger.debug(methodName, "Query executed successfully.");
			knLogger.debug(methodName);
			return bitMask;

		} catch (KnDAOException e) {
			knLogger.error(methodName, "KnDAOException occured while getCorpAdminFS2BitMask  ", e);
			throw e;
		} catch (Exception e) {
			knLogger.error(methodName, "Unexpected Exception while getCorpAdminFS2BitMask ", e);
			throw KnDbUtil.processException(e, "Failed to getCorpAdminFS2BitMask " + e, pttServerId, KnDAOSourceTypes.LICENSEINFO, query);
		} finally {
			KnDbUtil.closeResultSet(rs);
			KnDbUtil.closeStatement(stmt);
		}
	}
}
