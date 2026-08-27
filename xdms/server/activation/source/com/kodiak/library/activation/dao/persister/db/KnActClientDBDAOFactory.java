/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnActClientDBDAOFactory.java
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

import com.kodiak.library.activation.dao.persister.db.tables.xdms.KnDeviceInfoDAO;
import com.kodiak.library.activation.dao.persister.db.tables.xdms.KnSubscrInfoDAO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.library.activation.business.KnActBOException;
import com.kodiak.library.activation.business.helper.KnActClientInfoUtil;
import com.kodiak.library.activation.dao.IActClientDAOFactory;
import com.kodiak.library.activation.dao.persister.*;

public class KnActClientDBDAOFactory implements IActClientDAOFactory {
    private static boolean isInitialized = false;
    private static final String className = KnActClientDBDAOFactory.class.getName();
    private static String pttServerId = null;

    public KnActClientDBDAOFactory() throws KnActBOException, KnDAOException {
        if (!isInitialized) {
            isInitialized = true;
            //KnActClientDBWebDAO.pttServerId = KnConfigInfoUtil.getInstance().getHomePttServerId().substring(3);
            // Xdm server pttserverid
            this.pttServerId = KnActClientInfoUtil.getInstance().getXdmServerId();
            KnActClientDBXDMServerDAO.pttServerId = this.pttServerId;
        }
    }


    /**
     * This method will create EMS data object
     *
     * @return
     * @throws KnDAOException
     */
    public IActClientEmsDAO createEmsDAO() throws KnDAOException {
        return new KnActClientDBEmsDAO();
    }

    /**
     * This method will create LS data object
     *
     * @return
     * @throws KnDAOException
     */
    public IActClientXDMServerDAO createXDMServerDAO() throws KnDAOException {
        return new KnActClientDBXDMServerDAO();
    }

    /**
     * This method will create NS data object
     *
     * @param pttServerId
     * @return
     * @throws KnDAOException
     */
    public IActClientNameServerDAO createNameServerDAO(String pttServerId) throws KnDAOException {
        return new KnActClientDBNameServerDAO(pttServerId);
    }

    /**
     * This method will create RTX data object
     *
     * @param pttServerId
     * @return
     * @throws KnDAOException
     */
    public IActClientRtxDAO createRtxDAO(String pttServerId) throws KnDAOException {
        return new KnActClientDBRtxDAO(pttServerId);
    }

    /**
     * This method will create Web data object
     *
     * @return the Web data object
     */
    public IActClientWebDAO createWebServerDAO() throws KnDAOException {
        return new KnActClientDBWebDAO();
    }

    @Override
    public KnDeviceInfoDAO createDeviceInfoDAO() throws KnDAOException {
        return new KnDeviceInfoDAO(pttServerId);
    }

    @Override
    public KnSubscrInfoDAO createSubscrInfoDAO() throws KnDAOException {
        return new KnSubscrInfoDAO(pttServerId);
    }
}
