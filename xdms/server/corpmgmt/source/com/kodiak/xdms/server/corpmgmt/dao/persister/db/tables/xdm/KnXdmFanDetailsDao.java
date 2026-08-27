/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p>
 * File name:  KnXdmFanDetailsDao.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Sunil Biradar              10-Mar-2023                  12.3
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 * <p>
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2022 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 */

package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.sql.*;
import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnXdmFanDetailsDao implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXdmFanDetailsDao.class);
    private String pttServerId;
    private String XDM_FAN_DETAILS = "FAN_DETAILS";

    public KnXdmFanDetailsDao(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    public void insert(IPersistenceDTO iPersistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void update(IPersistenceDTO iPersistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void delete(IPersistenceDTO iPersistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection select(IPersistenceDTO iPersistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<String> getFirstNetFanIdsByFanIds(List<String> idList, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException {
        String methodName = "getFirstNetFanIdsByFanIds(List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : idList - ", idList);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        String query = "SELECT FAN_ID FROM DG.FAN_DETAILS WHERE FAN_ID IN (FANLIST) AND SEGMENT_INDICATOR IS NOT NULL";
        List<String> fanList = new ArrayList<>();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = replaceContactWithValue(query, "FANLIST", formCommaSeperatedIdList(idList));
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                fanList.add(String.valueOf(rs.getInt(1)));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while getFirstNetFanIdsByFanIds  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occurred while getFirstNetFanIdsByFanIds - ",
                    new KnException(KnErrorCodes.DAO.INTERNAL_ERROR, e.getMessage(), e));
            throw KnDbUtil.processException(e, "Failed while getFirstNetFanIdsByFanIds -" + e,
                    pttServerId, XDM_FAN_DETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "EXIT. fanList size returned is - ", fanList.size());
        }
        return fanList;
    }

    /**
     * Retrieves the list of FAN IDs for the specified corporation ID and existing ID list.
     *
     * @param corpId the ID of the corporation
     * @param idListExistInReq the collection of existing FAN IDs to filter by
     * @param persisterTxn the transaction object for database operations
     * @return a collection of FAN IDs that match the given corporation ID and existing ID list
     * @throws KnDAOException if a data access error occurs
     */
    public Collection<Integer> getFanList(int corpId, Collection<Integer> idListExistInReq, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "custom : getFanList(corpId, idListExistInReq, persisterTxn)";
        knLogger.info(methodName, "Entry : corpId - ", corpId, " idListExistInReq - ", idListExistInReq);
        Statement stmt = null;
        ResultSet rs = null;
        String query = "SELECT FAN_ID FROM DG.FAN_DETAILS WHERE CORPID = ? AND FAN_ID IN (FANLIST);";
        Collection<Integer> fanList = new ArrayList<Integer>();
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, "FANLIST", formIntegerCommaSeperatedIdList(idListExistInReq));
            query = KnDbUtil.replaceValInQry(query, corpId);
            stmt = conn.createStatement();
            knLogger.debug(methodName, "Executing query - ", query);
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                fanList.add(rs.getInt(1));
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getFanList  - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while getFanList - ",
                    new KnException(KnErrorCodes.DAO.INTERNAL_ERROR, e.getMessage(), e));
            throw KnDbUtil.processException(e, "Failed while getFanDetails -" + e,
                    pttServerId, XDM_FAN_DETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            knLogger.debug(methodName, "EXIT. fanList size  - ", fanList.size());
        }
        return fanList;
    }

}
