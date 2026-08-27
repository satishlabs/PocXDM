/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao.persister.db;

import com.kodiak.xdms.server.pubmgmt.dao.IPubDAOFactory;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubPocDAO;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.common.dao.KnDAOException;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubDBDAOFactory.java
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
public class KnPubDBDAOFactory implements IPubDAOFactory {

    private static boolean isInitialized = false;

    public KnPubDBDAOFactory() {
        if (!isInitialized) {
            isInitialized = true;
        }
    }

    public IPubPocDAO createPocServerDAO(String pttServerId) throws KnDAOException {
        return new KnPubPocDAO(pttServerId);
    }

    public IPubXdmDAO createXdmServerDAO(String pttServerId) throws KnDAOException {
        return new KnPubXdmDAO(pttServerId);
    }


}
