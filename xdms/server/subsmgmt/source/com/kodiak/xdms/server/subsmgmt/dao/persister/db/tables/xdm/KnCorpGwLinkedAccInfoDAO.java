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
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnCorpGwLinkedAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by kdeepak on 27-04-2015.
 */
public class KnCorpGwLinkedAccInfoDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGwLinkedAccInfoDAO.class);
    private String pttServerId;
    private static final String GWLINKEDID="GWLINKEDID";
    private static final String LastUpdateTime="LastUpdateTime";
    private static final String NNI_GW_ACCOUNTID="NNI_GW_ACCOUNTID";
    private static final String POC_CORP_NNI_REFERENCE_ID="POC_CORP_NNI_REFERENCE_ID";
    private static final String QRY_UPDATE_LASTUPDATETIME="UPDATE DG.CORP_GW_LINKED_ACCOUNTINFO SET LastUpdateTime = ? WHERE POC_CORP_NNI_REFERENCE_ID = ? ";

    public KnCorpGwLinkedAccInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public void updateEtagForCorpNNIRefId(KnCorpGwLinkedAccInfoDTO corpGwLinkedAccInfoDTO,KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName="updateEtagForCorpNNIRefId(KnCorpGwLinkedAccInfoDTO)";
        knLogger.debug(methodName,"ENTRY:corpGwLinkedAccInfoDTO",corpGwLinkedAccInfoDTO);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        int rowCount=0;
        knLogger.debug(methodName, "ENTRY: Update LastProfileUpdateTime with DTO -", corpGwLinkedAccInfoDTO);
        try {
            long lastProfileUpdateTime = corpGwLinkedAccInfoDTO.getLastUpdateTime();
            String corpNNIRefId=corpGwLinkedAccInfoDTO.getCorpNNIRefId();
            query = QRY_UPDATE_LASTUPDATETIME;
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setLong(1, lastProfileUpdateTime);
            pStmt.setString(2,corpNNIRefId);
            knLogger.debug(methodName, "QUERY: Executing the query - ", query);
            rowCount=pStmt.executeUpdate();
            knLogger.debug(methodName, "QUERY: Executed ");
        } catch (KnDAOException dbConne) {
            knLogger.error(methodName, "DAO Exception occurred");
            throw dbConne;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to update Last profile Update Time - " + e.getMessage(),
                    pttServerId, KnProvDAOSourceTypes.CORPGWLINKEDACCOUNTINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.debug(methodName, "EXIT: update Last profile Update Time ,rowCount",rowCount);
    }
}
