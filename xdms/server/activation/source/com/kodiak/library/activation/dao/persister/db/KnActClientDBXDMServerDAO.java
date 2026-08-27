/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnActClientDBXDMServerDAO.java
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
package com.kodiak.library.activation.dao.persister.db;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.library.activation.dao.persister.IActClientXDMServerDAO;
import com.kodiak.library.activation.dao.persister.db.tables.KnActClientTablesRegistry;
import com.kodiak.library.activation.dao.persister.db.tables.xdms.KnTmpVASSubscriptionKeyInfoDAO;
import com.kodiak.library.activation.dto.persistdat.KnSubscriptionKeyInfoPersistDTO;
import com.kodiak.library.activation.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;

public class KnActClientDBXDMServerDAO implements IActClientXDMServerDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnActClientDBXDMServerDAO.class);
    private static final String CLASS = KnActClientDBXDMServerDAO.class.getName();
    public static String pttServerId = null;

    public KnActClientDBXDMServerDAO() {
    }

    public KnSubscriptionKeyInfoPersistDTO retreieveSubscriptionKeyInfo(String activationKey, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "retreieveSubscriptionKeyInfo(String, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug( methodName,
                "ENTRY : ActivationKey -> " + activationKey + ", txn -> " + persisterTxn);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                ownedTxn = true;
                persisterTxn.open();
            }
            knLogger.debug( methodName, "Ptt Server Id -> " + pttServerId);
            KnTmpVASSubscriptionKeyInfoDAO subsKeyInfoDAO = KnActClientTablesRegistry.getXdmsTableRegistry().
                    createTmpVASSuscrptionKeyInfoDAO(pttServerId);
            KnSubscriptionKeyInfoPersistDTO persistDTO = subsKeyInfoDAO.select(activationKey, persisterTxn);

            if (ownedTxn) persisterTxn.save();
            return persistDTO;
        } catch (KnDAOException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error( methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to fetch subscriptionkeyinfo for " + activationKey +
                    ". Ex: " + e.getMessage(), pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, null);
        } finally {
            persisterTxn = null;
            knLogger.debug( methodName, "EXIT :");
        }
    }


      public void updateExpiryTime(KnSubscriptionKeyInfoPersistDTO subsInfoDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "updateExpiryTime(KnSubscriptionKeyInfoPersistDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        knLogger.debug( methodName,
                "ENTRY : subsInfoDTO -> " + subsInfoDTO + ", txn -> " + persisterTxn);
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                ownedTxn = true;
                persisterTxn.open();
            }
            knLogger.debug( methodName, "Ptt Server Id -> " + pttServerId);
            KnTmpVASSubscriptionKeyInfoDAO subsKeyInfoDAO = KnActClientTablesRegistry.getXdmsTableRegistry().
                    createTmpVASSuscrptionKeyInfoDAO(pttServerId);
             subsKeyInfoDAO.updateExpiryTime(subsInfoDTO, persisterTxn);

            if (ownedTxn) persisterTxn.save();
        } catch (KnDAOException e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error( methodName, "DAO Exception - " + e);
            throw e;
        } catch (Exception e) {
            if (ownedTxn) persisterTxn.rollback();
            knLogger.error( methodName, "Unexpected Exception - " + e);
            throw KnDbUtil.processException(e, "Failed to fetch update Expiry time for " + subsInfoDTO +
                    ". Ex: " + e.getMessage(), pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, null);
        } finally {
            persisterTxn = null;
            knLogger.debug( methodName, "EXIT :");
        }
    }
}
