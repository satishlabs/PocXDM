/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.commdto.common.KnIdDetailsDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIntegerQuesMarks;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.replaceContactWithValue;

public class KnXDMBanDetailsDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXdmFanDetailsDao.class);
    private String pttServerId;
    private String XDM_BAN_DETAILS = "BAN_DETAILS";

    public KnXDMBanDetailsDAO(String pttServerId) {
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

    public List<KnIdDetailsDTO> getBanDetailsByBanFanId(List<Integer> idList, int idType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getBanDetailsByBanFanId(idList, persisterTxn)";
        knLogger.debug(methodName, "Entry : idList - ", idList);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT BAN_ID,EXTERNAL_BAN_ID,BAN_NAME,FAN_ID, BAN_TYPE FROM DG.BAN_DETAILS WHERE ";
        if (idType == 1) {
            query = query.concat(" BAN_ID IN (IDLIST)");
        } else {
            query = query.concat(" FAN_ID IN (IDLIST)");
        }
        List<KnIdDetailsDTO> banDetailList = new ArrayList<>();
        int index = 1;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = replaceContactWithValue(query, "IDLIST", formCommaSeperatedIntegerQuesMarks(idList));
            knLogger.debug(methodName, "query", query);
            pstmt = conn.prepareStatement(query);
            for (Integer mdn : idList) {
                pstmt.setInt(index++, mdn);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                KnIdDetailsDTO idDetailsDTo = new KnIdDetailsDTO();
                idDetailsDTo.setIdKey(rs.getInt("BAN_ID"));
                idDetailsDTo.setExtId(rs.getString("EXTERNAL_BAN_ID"));
                idDetailsDTo.setIdName(rs.getString("BAN_NAME"));
                idDetailsDTo.setParentId(rs.getInt("FAN_ID"));
                banDetailList.add(idDetailsDTo);
            }
        } catch (SQLException e) {
            knLogger.error(methodName, "SQLException occured ", e);
            throw KnDbUtil.processException(e, "Failed while BAN_DETAILS -",pttServerId, XDM_BAN_DETAILS, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return banDetailList;
    }
}
