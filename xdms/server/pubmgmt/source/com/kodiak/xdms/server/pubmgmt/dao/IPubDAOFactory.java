/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao;

import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubPocDAO;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.common.dao.KnDAOException;

/**
 * ************************************************************************
 * <p/>
 * File name:  IPubDAOFactory.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 10, 2011           7.0
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
public interface IPubDAOFactory {

    /**
     * This method will create POC data object
     *
     * @return the POC data object
     */
    IPubPocDAO createPocServerDAO(String pttServerId)
            throws KnDAOException;

    /**
     * This method will create XDM data object
     *
     * @return the XDM data object
     */
    IPubXdmDAO createXdmServerDAO(String pttServerId)
            throws KnDAOException;

}
