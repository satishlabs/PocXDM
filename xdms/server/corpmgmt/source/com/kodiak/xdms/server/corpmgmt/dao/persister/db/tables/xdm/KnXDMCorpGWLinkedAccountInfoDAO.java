/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGWLinkedAccountInfoDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;



public class KnXDMCorpGWLinkedAccountInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpGroupMemberListDAO.class);
    private String pttServerId;

    KnXDMCorpGWLinkedAccountInfoDAO(String pttServerId) {
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

    public KnCorpGWLinkedAccountInfoDTO getCorporateLinkedAccountInfo(String refId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorporateLinkedAccountInfo(corpId,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : refId - ", refId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        KnCorpGWLinkedAccountInfoDTO linkedAccInfo = new KnCorpGWLinkedAccountInfoDTO();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_CORP_GW_ACCOUNT_ID);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, refId);
            knLogger.debug( methodName, "Executing query -", "'", query, "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully.");
            if (rs.next()) {
                linkedAccInfo.setAccountId(rs.getInt(1));
                linkedAccInfo.setGwEtag(rs.getLong(2));
                linkedAccInfo.setNniRefId(rs.getString(3).trim());
            }
            knLogger.debug( methodName, "Response  - ", linkedAccInfo);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to fetch group list for the external contact ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_GROUP_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        return linkedAccInfo;
    }
}
