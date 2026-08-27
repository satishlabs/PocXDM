/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p>
 * File name:  KnGroupHierrarchyDAO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Jusbin Mathew           5-April-2023                  11.3
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 * <p>
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2023 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.GET_FAN_ID_INFO;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnGroupHierarchyDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpGroupInfoDAO.class);
    private final String ID_VALUE = "ID_VALUE";
    private final String CORPGROUPID = "CORPGROUPID";
    private final String XDM_TABLE_NAME = "DG.GROUP_HIERARCHY_MAP";
    private final String GROUP_IDS = "GROUP_IDS";
    private final String ID_TYPE = "ID_TYPE";

    public String pttServerId = null;

    KnGroupHierarchyDAO(String pttServerId) {
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

    public Map<Integer, Integer> getIdValueGroupIdMap(List<Integer> groupIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        Map<Integer, Integer> response = new HashMap<>();
        String methodName = "getIdValueGroupIdMap()";
        knLogger.info(methodName, "Entry : ids - ", groupIds.size());
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Connection conn = null;
        int index = 1;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_FAN_ID_INFO);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, GROUP_IDS, formCommaSeperatedIntegerQuesMarks(groupIds));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (Integer mdn : groupIds) {
                pstmt.setInt(index++, mdn);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                response.put(rs.getInt(ID_VALUE), rs.getInt(ID_TYPE));
            }
            knLogger.info(methodName, " Exit: idsInfo ", response.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while GROUP_HIERARCHY_MAP -",
                    pttServerId, XDM_TABLE_NAME, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return response;
    }


    /**
     * Method to return the Group Hierarchy Map info for a given groupId.
     * @param groupIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Integer> getGroupHierarchyMap(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        Map<Integer, Integer> response = new HashMap<>();
        String methodName = "getIdValueGroupIdMap()";
        knLogger.info(methodName, "Entry : ids - ", groupIds.size());
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Connection conn;
        int index = 1;
        try {
            query = "SELECT ID_VALUE, ID_TYPE FROM DG.GROUP_HIERARCHY_MAP WHERE CORPGROUPID IN (GROUP_IDS)";
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, GROUP_IDS, formCommaSeperatedIntegerQuesMarks(groupIds));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (Integer mdn : groupIds) {
                pstmt.setInt(index++, mdn);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                response.put(rs.getInt(ID_VALUE), rs.getInt(ID_TYPE));
            }
            knLogger.info(methodName, " Exit: idsInfo ", response.size());
        } catch (SQLException e) {
            knLogger.error(methodName, "Exception - ", e);
            throw KnDbUtil.processException(e, "Failed while GROUP_HIERARCHY_MAP -",
                    pttServerId, XDM_TABLE_NAME, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return response;
    }


}
