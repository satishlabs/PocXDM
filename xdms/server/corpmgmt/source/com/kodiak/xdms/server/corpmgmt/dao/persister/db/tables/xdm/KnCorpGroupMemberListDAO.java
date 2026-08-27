/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpGroupMemberListDAO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 1, 2011      7.0
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
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import com.kodiak.logger.KnLogger;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.SELECT_CORP_LIST_MEMBERS;


public class KnCorpGroupMemberListDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupMemberListDAO.class);

    private static final String CLASS = KnCorpGroupMemberListDAO.class.getName();

    private String xdmsHome;

    KnCorpGroupMemberListDAO(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "insert", "Unimplemented Methods");
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "update", "Unimplemented Methods");
    }

    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "delete", "Unimplemented Methods");
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "select", "Unimplemented Methods");
        return null;
    }

    public Collection<String> selectGroupMemberList(int groupId, KnPersisterTxn persisterTxn)
            throws KnDAOException {

        String methodName = "selectGroupMemberList(int, KnPersisterTxn)";
        knLogger.debug( methodName, "Entry : groupId- " , groupId);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection<String> groupMemberList = new ArrayList<String>();
        String query = null;
        try {
            Connection conn = persisterTxn.getDBConnection(xdmsHome, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(SELECT_CORP_LIST_MEMBERS);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug( methodName, "Executing query - " , "'" , query , "'");
            rs = pstmt.executeQuery();
            knLogger.debug( methodName, "Query executed successfully");
            while (rs.next()) {
                groupMemberList.add(rs.getString(1));
            }
            return groupMemberList;

        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while selecting members of the group - " , e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception  occured while selecting members of the group- " , e);
            throw KnDbUtil.processException(e, "Failed while selecting members of the group " + e,
                    xdmsHome, KnDAOSourceTypes.XDM_CORP_GROUP_MEMBER_LIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }
}
