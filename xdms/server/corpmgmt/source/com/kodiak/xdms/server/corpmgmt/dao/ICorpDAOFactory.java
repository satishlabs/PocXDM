/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpDAOFactory.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        20-01-2011      7.0
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

package com.kodiak.xdms.server.corpmgmt.dao;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpPocDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;

public interface ICorpDAOFactory {

    /**
     * This method will create POC data object
     *
     * @param pttServerId the Ptt Server Id of PoC Server
     * @return the ICorpPocDao reference to PoC Dao object
     * @throws KnDAOException exception
     */
    public ICorpPocDAO createPocServerDAO(String pttServerId)
            throws KnDAOException;

    /**
     * This method will create XDM data object
     *
     * @param pttServerId pttServerId the Ptt Server Id of XDM Server
     * @return the ICorpXdmDAO reference to XDM Dao object
     * @throws KnDAOException exception
     */
    public ICorpXdmDAO createXdmServerDAO(String pttServerId)
            throws KnDAOException;

}
