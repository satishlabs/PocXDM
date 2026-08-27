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
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnSubsAddEmgrConfigDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

public class KnPocSubscrAddInfoDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnPocSubscrAddInfoDAO.class);

    public String pttServerId = null;

    public KnPocSubscrAddInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    public static final String TABLENAME = "DG.POCSUBSCR_ADDLINFO";
    public static final String MDN = "MDN";
    public static final String EMERGDESTTYPE = "EMERGDESTTYPE";
    public static final String EMERGORIGINDICATORBITSET = "EMERGORIGINDICATORBITSET";
    public static final String EMERGRECVINDICATORBITSET = "EMERGRECVINDICATORBITSET";
    public static final String GET_EMERGENCY_CONFIG = "SELECT " + EMERGDESTTYPE + ", " + EMERGORIGINDICATORBITSET + ", " + EMERGRECVINDICATORBITSET
            + " FROM " + TABLENAME+ " WHERE " + MDN + "=?";

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

    public KnSubsAddEmgrConfigDTO getEmgrConfig(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getEmgrConfig(String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : ");
        KnSubsAddEmgrConfigDTO emgrConfig = new KnSubsAddEmgrConfigDTO();
            Connection conn;
            PreparedStatement pStatement = null;
            ResultSet rs = null;
            String query = null;

            try {
                query = GET_EMERGENCY_CONFIG;

                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);            pStatement = conn.prepareStatement(query);
                pStatement.setString(1, mdn);
                knLogger.debug( methodName, "QUERY : Executing " , query , " persisterTxn : " , persisterTxn);
                rs = pStatement.executeQuery();
                knLogger.debug( methodName, "QUERY : Completed.");

                if (rs.next()) {
                    emgrConfig.setEmergencyDestType(rs.getInt(EMERGDESTTYPE));
                    emgrConfig.setEmergOriginBitset(rs.getInt(EMERGORIGINDICATORBITSET));
                    emgrConfig.setEmergReceiveBitset(rs.getInt(EMERGRECVINDICATORBITSET));
                } else {
                    // throw back exception
                    knLogger.error( methodName, "No EmgrConfig Info found");
                    throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No EmgrConfig Info found. Query ->" + query);
                }

            } catch (Exception e) {
                knLogger.error( methodName, "Unexpected Exception - " , e);
                throw KnDbUtil.processException(e, "Failed to retrieve EmgrConfig for mdn - " + e.getMessage(), pttServerId, KnDAOSourceTypes.XDM_POCSUBSCR_ADDLINFO, query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closeStatement(pStatement);
            }
            knLogger.info( methodName, "Returning Authorization doc details - " ,  emgrConfig);
            return emgrConfig;
        }


    }
