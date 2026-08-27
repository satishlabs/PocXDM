/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.library.activation.dao.persister.db.tables.xdms;

import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.library.activation.dto.common.KnSubscrInfoDTO;
import com.kodiak.library.activation.resources.KnDAOSourceTypes;
import com.kodiak.library.activation.resources.KnErrorCodes;
import com.kodiak.logger.KnLogger;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;

public class KnSubscrInfoDAO implements ITableDAO {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSubscrInfoDAO.class);
    public String pttServerId = null;

    public KnSubscrInfoDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }

    private static final String QRY_SEL_BY_MDN = "SELECT CLIENT_TYPE, LICENSE_TYPE,SUBSCRIBERFS2, CORPID FROM DG.POCSUBSCRINFO WHERE MDN = ?";
    private static final String QRY_UPDATE_IMEI="UPDATE DG.POCSUBSCRINFO SET IMEI=? , LASTPROFILEUPDATETIME=? where MDN=?";

    @Override
    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "insert", "Not Implemented");
    }

    @Override
    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "insert", "Not Implemented");
    }

    public void updateSubscriberProfile(KnSubscrInfoDTO subscrInfoDTO, KnPersisterTxn persistTxn) throws KnDAOException {

        String methodName = "updateSubscriberProfile(KnSubscrInfoDTO subscrInfoDTO, KnPersisterTxn persistTxn) ";
        knLogger.debug(methodName, "ENTRY: Update Imei ");
        Connection conn = null;
        PreparedStatement psmt = null;
        String query = null;
        try {
            query = QRY_UPDATE_IMEI;
            conn = persistTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            psmt = conn.prepareStatement(query);
            psmt.setString(1,subscrInfoDTO.getImei());
            psmt.setLong(2,lastProfileUpdateTime);
            psmt.setString(3,subscrInfoDTO.getMdn());
            knLogger.debug(methodName,"Executing query ",query);
            int count=psmt.executeUpdate();
            knLogger.debug( methodName, "Executed Update :", count);
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to - " + e.getMessage(), pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(psmt);
        }
    }

    @Override
    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "insert", "Not Implemented");
    }

    @Override
    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn) throws KnDAOException {
        knLogger.warn( "insert", "Not Implemented");
        return null;
    }

    public KnSubscrInfoDTO getSubscrInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getSubscrInfo()";
        knLogger.debug( methodName, "ENTRY : mdn -> " + KnGDPRTemplate.mdn(mdn));
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        KnSubscrInfoDTO subscrInfoDTO = new KnSubscrInfoDTO();
        try {
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA,true);
            pStatement = conn.prepareStatement(QRY_SEL_BY_MDN);
            pStatement.setString(1, mdn);
            knLogger.debug( methodName, "QUERY : Executing " + QRY_SEL_BY_MDN + ", MDN : "+ KnGDPRTemplate.mdn(mdn) + ", persisterTxn : " + persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");
            if (rs.next()) {
                subscrInfoDTO.setMdn(mdn);
                subscrInfoDTO.setClientType(rs.getInt(1));
                subscrInfoDTO.setLicenseType(rs.getInt(2));
                subscrInfoDTO.setSubsFS2(rs.getString(3));
                subscrInfoDTO.setCorpId(rs.getInt(4));
            } else {
                knLogger.debug( methodName, "No record found for SubscriptionKey");
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No record found.",
                        pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
            }
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), pttServerId, KnDAOSourceTypes.POCSUBSCRINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
        }
        knLogger.debug( methodName, "Returning :", subscrInfoDTO);
        return subscrInfoDTO;
    }
}
