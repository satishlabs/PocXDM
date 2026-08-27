/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnTmpVASSubscriptionKeyInfoDAO.java
 * Subsystem:  Activation Library
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Rashmi Kamat         29-Oct-2010       6.4
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

package com.kodiak.library.activation.dao.persister.db.tables.xdms;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.library.activation.business.helper.KnActClientInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.library.activation.dto.persistdat.KnSubscriptionKeyInfoPersistDTO;
import com.kodiak.library.activation.resources.KnConstants;
import com.kodiak.library.activation.resources.KnDAOSourceTypes;
import com.kodiak.library.activation.resources.KnErrorCodes;

import java.sql.*;
import java.util.Collection;
import java.util.Calendar;
import java.util.TimeZone;

public class KnTmpVASSubscriptionKeyInfoDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnTmpVASSubscriptionKeyInfoDAO.class);

    public static final String CLASSNAME = KnTmpVASSubscriptionKeyInfoDAO.class.getName();
    public String pttServerId = null;

    // Table related constants
    private static final String TABLENAME = "DG.TMPVASSUBSCRIPTIONKEYINFO";
    private static final String MDN = "MDN";
    private static final String SUBSCRIPTIONKEY = "SUBSCRIPTIONKEY";
    private static final String EXPIRYTIME = "ExpiryTime";
    private static final String SERVICENAME = "ServiceName";

    // Queries
    private static final String QRY_SEL_BY_SUBSKEY = "SELECT " + MDN + ", " + SERVICENAME + ", " + EXPIRYTIME + " FROM "
            + TABLENAME + " WHERE " + SUBSCRIPTIONKEY + " = ?";

    private static final String QRY_UPDATE_EXP_TIME = "UPDATE " + TABLENAME + " SET " + EXPIRYTIME + "= ? " +" WHERE " +  MDN + " = ? AND "+ SUBSCRIPTIONKEY +"= ? ";


    public KnTmpVASSubscriptionKeyInfoDAO(String pttserverId) {
        this.pttServerId = pttserverId;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "insert", "Not Implemented");
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "update", "Not Implemented");
    }

    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "delete", "Not Implemented");
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "select", "Not Implemented");
        return null;
    }

    public KnSubscriptionKeyInfoPersistDTO select(String activationKey, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "select(String, KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        ResultSet rs = null;
        String query = null;
        KnSubscriptionKeyInfoPersistDTO persistDTO = new KnSubscriptionKeyInfoPersistDTO();
        knLogger.debug( methodName, "ENTRY : SubscriptionKey -> " + activationKey);
        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
            conn = persisterTxn.getDBConnection(pttServerId, true);
            pStatement = conn.prepareStatement(QRY_SEL_BY_SUBSKEY);
            pStatement.setString(1, activationKey);

            knLogger.debug( methodName, "QUERY : Executing " + QRY_SEL_BY_SUBSKEY + ", activation key : "
                    + activationKey + ", persisterTxn : " + persisterTxn);
            rs = pStatement.executeQuery();
            knLogger.debug( methodName, "QUERY : Completed.");

            if (rs.next()) {
                persistDTO.setMdn(rs.getString(MDN));
                persistDTO.setServiceName(rs.getString(SERVICENAME));
                //As per requirement Timestamp will be always in UTC timeZone in DB.
                persistDTO.setExpiryTime(rs.getTimestamp(EXPIRYTIME,Calendar.getInstance(TimeZone.getTimeZone("UTC"))).getTime());
                knLogger.debug( methodName, "Time Stamp converted to long -" + persistDTO.getExpiryTime());
            } else {
                knLogger.debug( methodName, "No record found for SubscriptionKey");
                if (ownedTxn) persisterTxn.rollback();
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No record found.",
                        pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
            }
            if (ownedTxn) persisterTxn.save();
            return persistDTO;
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to - " + e.getMessage(), pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT : Persist DTO -> " + persistDTO);
        }
    }

  public void updateExpiryTime(KnSubscriptionKeyInfoPersistDTO subsInfoDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "updateExpiryTime(KnSubscriptionKeyInfoPersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        Connection conn;
        PreparedStatement pStatement = null;
        String query = null;
        knLogger.debug( methodName, "ENTRY : subsInfoDTO -> " + subsInfoDTO);
        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }
           			
            conn = persisterTxn.getDBConnection(pttServerId, true);
            String mdn = subsInfoDTO.getMdn();
            String activationKey = subsInfoDTO.getActivationKey();
            Timestamp expiryTime = KnActClientInfoUtil.getInstance().generateExpiryTime(subsInfoDTO.getExpiryTime());
            pStatement = conn.prepareStatement(QRY_UPDATE_EXP_TIME);
            pStatement.setTimestamp(1, expiryTime,Calendar.getInstance(TimeZone.getTimeZone("UTC")));
            pStatement.setString(2,mdn );
            pStatement.setString(3,activationKey);
            knLogger.debug( methodName, "QUERY : Executing " + QRY_UPDATE_EXP_TIME + ", mdn : "
                    + KnGDPRTemplate.mdn(mdn) + ", activationKey : " + activationKey);
            int count = pStatement.executeUpdate();
            knLogger.debug( methodName, "QUERY : Completed." + count);

            if (ownedTxn) persisterTxn.save();
        } catch (SQLException e) {
            knLogger.error( methodName, "SQL Exception - " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to - " + e.getMessage(), pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
        } catch (KnDAOException e) {
            knLogger.error( methodName, "DAO Exception - " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw e;
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception - " + e);
            if (ownedTxn) persisterTxn.rollback();
            throw KnDbUtil.processException(e, "Failed to  - " + e.getMessage(), pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pStatement);
            knLogger.debug( methodName, "EXIT :  -> "  );
        }
    }
}