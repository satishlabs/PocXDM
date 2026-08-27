/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnCorpAddlInfoPersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by hanwar on 24-02-2017.
 */
public class KnCorpAddlInfoDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpAddlInfoDAO.class);

    public static final String className = KnCorpAddlInfoDAO.class.getName();
    private String pttServerId = null;

    private static final String CORPID = "CORPID";
    private static final String DRX_VALUE_LIST = "DRX_VALUE_LIST";
    private static final String TIME_SLOT_TYPE = "TIME_SLOT_TYPE";
    private static final String DRX_VALUE = "DRX_VALUE";
    private static final String DRX_THRESHOLD_COUNT = "DRX_THRESHOLD_COUNT";
    private static final String DRX_MO = "DRX_MO";
    private static final String DRX_MT = "DRX_MT";
    private static final String CALL_HISTORY_DURATION = "CALL_HISTORY_DURATION";
    private static final String DATA_PURGE_DURATION = "DATA_PURGE_DURATION";
    private static final String FIXEDTS_START_TIME = "FIXEDTS_START_TIME";
    private static final String FIXEDTS_END_TIME  = "FIXEDTS_END_TIME";
    private static final String EXTCORPID = "EXTCORPID";
    private static final String XDMSHOME = "XDMSHOME";
    private static final String PROFILE_CREATION_TIME = "PROFILECREATIONTIME";
    private static final String LAST_PROFILE_UPDATE_TIME = "LASTPROFILEUPDATETIME";

    private static final String TABLENAME = "DG.POCCORP_ADDLINFO";
    private static final String DELETE_QRY = "DELETE FROM " + TABLENAME + " WHERE " + CORPID + " = ?";


    public KnCorpAddlInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    public void deleteCorpAddlInfo(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpAddlInfo(IPersistenceDTO, persisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug( methodName, "ENTRY: Delete Corp Profile from CorpAddlInfo");
        try {

            KnCorpAddlInfoPersistDTO corpInfoDto = (KnCorpAddlInfoPersistDTO) persistenceDTO;
            int corpId = corpInfoDto.getCorpId();

            query = DELETE_QRY;

            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);

            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, corpId);

            knLogger.debug( methodName, "QUERY: Executing the Query - " , query , ", DTO - " , persistenceDTO);
            pStmt.executeUpdate();
            knLogger.debug( methodName, "QUERY: Executed the Query");
           } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " , e);
            knLogger.error( methodName, e);
            throw KnDbUtil.processException(e, "Failed to delete corporate addlinfo profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.CORPINFOADDLINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }
}
