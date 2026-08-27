/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  IActClientDAOFactory.java
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
package com.kodiak.library.activation.dao;

import com.kodiak.library.activation.dao.persister.*;
import com.kodiak.library.activation.dao.persister.db.tables.xdms.KnDeviceInfoDAO;
import com.kodiak.library.activation.dao.persister.db.tables.xdms.KnSubscrInfoDAO;
import com.kodiak.common.dao.KnDAOException;

public interface IActClientDAOFactory {
    /**
     * This method will create EMS data object
     *
     * @return the EMS data object
     */
    IActClientEmsDAO createEmsDAO()
            throws KnDAOException;

    /**
     * This method will create LS data object
     *
     * @return the LS data object
     */
    IActClientXDMServerDAO createXDMServerDAO()
            throws KnDAOException;

    /**
     * This method will create NS data object
     *
     * @param pttServerId
     * @return the NS data object
     */
    IActClientNameServerDAO createNameServerDAO(String pttServerId)
            throws KnDAOException;

    /**
     * This method will return the RTX DAO
     *
     * @param pttServerId
     * @return the RTX DAO object
     */
    IActClientRtxDAO createRtxDAO(String pttServerId)
            throws KnDAOException;

    /**
     * This method will create Web data object
     *
     * @return the Web data object
     */
    IActClientWebDAO createWebServerDAO()
            throws KnDAOException;

    KnDeviceInfoDAO createDeviceInfoDAO() throws KnDAOException;

    KnSubscrInfoDAO createSubscrInfoDAO() throws KnDAOException;
}
