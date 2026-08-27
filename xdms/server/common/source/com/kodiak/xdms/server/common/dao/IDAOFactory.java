/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/


/**
 * *****************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 16, 2010       7.0
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
 * *******************************************************************************
 */

package com.kodiak.xdms.server.common.dao;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.xdms.server.common.dao.persister.IEmsDAO;
import com.kodiak.xdms.server.common.dao.persister.IPoCServerDAO;
import com.kodiak.xdms.server.common.dao.persister.IPresenceServerDAO;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;

public interface IDAOFactory {

    /**
     * This method will create EMS data object
     * @return the EMS data object
     * @throws KnDAOException DAO LayerException
     */
    IEmsDAO createEmsDAO()
            throws KnDAOException;

    /**
     * This method will create XDM Server data object
     * @param pttServerId String
     * @return the XDM Server data object
     * @throws KnDAOException DAO Layer Exception
     */
    IXDMServerDAO createXDMServerDAO(String pttServerId)
            throws KnDAOException;

    /**
     * This method will create PoC Server data object
     * @param pttServerId String
     * @return the PoC Server data object
     * @throws KnDAOException DAP Layer Exception
     */
    IPoCServerDAO createPoCServerDAO(String pttServerId)
            throws KnDAOException;

    /**
     * This method will create Presence data object
     * @return the Presence data object
     * @param pttServerId String
     * @throws KnDAOException DAO Layer Exception
     */
    IPresenceServerDAO createPresenceServerDAO(String pttServerId)
            throws  KnDAOException;

}
