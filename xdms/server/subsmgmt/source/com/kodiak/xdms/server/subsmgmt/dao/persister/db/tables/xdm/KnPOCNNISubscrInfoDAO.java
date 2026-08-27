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
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnNNISubsProfileDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnBulkNNISubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvDAOSourceTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Deepak  on 21-04-2015.
 */
public class KnPOCNNISubscrInfoDAO implements ITableDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPOCNNISubscrInfoDAO.class);
    private String pttServerId;
    private static final String MDN = "MDN";
    private static final String EXTERNAL_ID="EXTERNAL_ID";
    private static final String PROFILEID="PROFILEID";
    private static final String NNIACTIVEFS="NNIACTIVEFS";
    private static final String TABLENAME = "DG.POC_NNISUBSCR_INFO";
    private static final String DELETE_QRY = "DELETE FROM " + TABLENAME + " WHERE " + MDN + " = ?";

    public KnPOCNNISubscrInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        final String methodName = "insert(IPersistenceDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : ", persistenceDTO);
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        try {
            KnBulkNNISubsProfilePersistDTO bulkNNISubsProfilePersistDTO = (KnBulkNNISubsProfilePersistDTO) persistenceDTO;
            KnNNISubsProfileDTO nniSubsProfileDTO=bulkNNISubsProfilePersistDTO.getNniSubsProfile();
            Integer profileId = nniSubsProfileDTO.getProfileId();
            Long nniActiveFs = nniSubsProfileDTO.getNniActiveFs();
            knLogger.info(methodName,  " ,profileId=", profileId, " ,nniActiveFs=", nniActiveFs);
            ArrayList<String> queryFields = new ArrayList<String>();
            queryFields.add(MDN);
            queryFields.add(PROFILEID);
            queryFields.add(NNIACTIVEFS);
            query = KnDbUtil.getInsertQuery(TABLENAME, queryFields);
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);

            for (String mdn : bulkNNISubsProfilePersistDTO.getMdns()) {
                int columnIndex = 0;
                pStmt.setString(++columnIndex, mdn);
                pStmt.setInt(++columnIndex, profileId);
                pStmt.setLong(++columnIndex, nniActiveFs);
                pStmt.addBatch();
            }
            knLogger.debug(methodName, "Query: Executing -", query, ", persist DTO - ", nniSubsProfileDTO);
            int[] count = pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed  mdns count:", count);


        }  catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to create NNI Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCNNISUBSCRPROFILEINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
        knLogger.info(methodName, "EXIT : ");
    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {

    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
    }

    public void delete(List<String> mdns, KnPersisterTxn persistTxn) throws KnDAOException {
        String methodName = "delete(List<String>, KnPersisterTxn)";
        String query = null;
        Connection conn;
        PreparedStatement pStmt = null;
        knLogger.debug(methodName, "ENTRY: Delete NNI Subscriber Profile");
        try {
            query = DELETE_QRY;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            for (String mdn : mdns) {
                pStmt.setString(1, mdn);
                pStmt.addBatch();
            }
            knLogger.debug(methodName, "Query: Executing - ", query);
            int[] count = pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed mdns count:", count);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception - ", e);
            throw KnDbUtil.processException(e, "Failed to delete NNI Subscriber Profile - " + e.getMessage(), pttServerId, KnProvDAOSourceTypes.POCNNISUBSCRPROFILEINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        return null;
    }
}
