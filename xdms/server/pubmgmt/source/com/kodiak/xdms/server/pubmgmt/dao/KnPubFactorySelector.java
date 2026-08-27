/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dao;

import com.kodiak.xdms.server.pubmgmt.dao.persister.db.KnPubDBDAOFactory;
import com.kodiak.xdms.server.pubmgmt.dao.persister.xml.KnPubXmlDAOFactory;
import com.kodiak.xdms.server.common.KnXDMSystemError;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubFactorySelector.java
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
public class KnPubFactorySelector {

    /* the factory type constants */
    /**
     * constant for denoting the factory type - Database
     */
    public static final int DB = 0;
    /**
     * constanst for denoting the factory type - XML
     */
    public static final int XML = 1;

    /**
     * This method will return a DAO factory implementation based
     * on the value of type.
     *
     * @param type
     * @return the implementation
     *
     */
    public static IPubDAOFactory getDAOFactory(int type) throws KnDAOException {
        switch (type) {
            case DB:
                return new KnPubDBDAOFactory();
            case XML:
                return new KnPubXmlDAOFactory();
            default:
                throw new KnXDMSystemError(KnErrorCodes.DAO.INVALID_FACTORY_TYPE, "Invalid database type : " + type);
        }
    }
}
