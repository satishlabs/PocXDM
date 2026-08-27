/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

public class KnCorporateInfoDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorporateInfoDAO.class);

    public static final String TABLENAME = "DG.POCCORPINFO";

    public static final String CORP_ID = "CORPID";
    public static final String EXTERNAL_CORP_ID = "EXTCORPID";
    public static final String MAXSIMULDEDICATESESSION = "MAXSIMULDEDICATEDSESSION";
    public static final String MAXSIMULDYNAMICSESSION = "MAXSIMULDYNAMICSESSION";
    public String pttServerId = null;

    public KnCorporateInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public static final String QRY_SELECT_CORP = "SELECT " + CORP_ID + " FROM " + TABLENAME + " WHERE " + EXTERNAL_CORP_ID + " = ?";
    public static final String QRY_SELECT_MAXSSDS = "SELECT " + MAXSIMULDEDICATESESSION + " FROM " + TABLENAME + " WHERE " + CORP_ID + " = ?";

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

    public KnCorpProfileDTO getCorpProfileDetails(String extCorpId, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getCorpProfileDetails(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : -> ", extCorpId);

        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        KnCorpProfileDTO corpProfileDTO = null;
        try {

            query = QRY_SELECT_CORP;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(query);
            pStatement.setString(1, extCorpId);

            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            if (rs.next()) {
                corpProfileDTO = new KnCorpProfileDTO();
                corpProfileDTO.setCorpId(rs.getInt(1));
                corpProfileDTO.setExtCorpId(extCorpId);
            }
            knLogger.debug(methodName, "QUERY : Completed : result", corpProfileDTO);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to query corporate table - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCCORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        return corpProfileDTO;
    }

    /**
     *
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getMaxSSDDSessionCnt(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMaxSSDDSessionCnt(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : -> ", corpId);
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        int maxSSDDCnt = 0;
        try {

            query = QRY_SELECT_MAXSSDS;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            pStatement = conn.prepareStatement(query);
            pStatement.setInt(1, corpId);

            knLogger.debug(methodName, "QUERY : Executing ", query, ", persisterTxn : ", persisterTxn);
            rs = pStatement.executeQuery();
            if (rs.next()) {
                maxSSDDCnt = rs.getInt(1);
            }
            knLogger.debug(methodName, "QUERY : Completed : result", maxSSDDCnt);

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to query corporate table for max ssdd grp cnt - " + e.getMessage(),
                    pttServerId, KnDAOSourceTypes.POCCORPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        return maxSSDDCnt;
    }

}
