/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p>
 * File name:  KnCorpGroupMemberCountDAO.java
 * Subsystem:  pubmgmt
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Indrajeet Kadolakar        12-June-2023               12.3.1.2
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

package com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KnCorpGroupMemberCountDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupMemberCountDAO.class);
    private String pttServerId;
    public static final String GROUP_MEMBER_COUNT = "SELECT CORPGROUPID, MEMBERCOUNT FROM DG.CORPGROUPMEMBERCOUNT WHERE CORPGROUPID IN ";

    public KnCorpGroupMemberCountDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    /**
     * Method to return members count in requested groups.
     *
     * @param grpIdList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, Integer> getGroupMemCount(Collection<Integer> grpIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMemCount(List<Integer>, KnPersisterTxn )";
        knLogger.info(methodName, "Entry- GroupIdList size :", grpIdList.size());
        Connection conn;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Map<Integer, Integer> grpIdMemCountMap = new HashMap<>();
        try {
            StringBuffer buffer = new StringBuffer(200);
            buffer.append(GROUP_MEMBER_COUNT).append(KnDbUtil.convertListToIntBuffer(grpIdList));
            query = buffer.toString();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                grpIdMemCountMap.put(rs.getInt("CORPGROUPID"), rs.getInt("MEMBERCOUNT"));
            }
        } catch (SQLException e) {
            knLogger.error(methodName, " SQLException occurred- ", e);
            throw KnDbUtil.processException(e, "Failed while retrieving the group info  ", pttServerId, KnDAOSourceTypes.CORP_GROUP_MEMBER_COUNT, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.info(methodName, "EXIT groupSize - ", grpIdMemCountMap.size());
        }
        return grpIdMemCountMap;
    }
}