/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpFactorySelector.java
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
import com.kodiak.xdms.server.common.KnXDMSystemError;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpDBDAOFactory;
import com.kodiak.xdms.server.corpmgmt.dao.persister.xml.KnCorpXmlDAOFactory;

public class KnCorpFactorySelector {

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
     * @param type the DAO Factory Type
     * @return the implementation
     * @throws KnDAOException exception
     */
    public static ICorpDAOFactory getDAOFactory(int type) throws KnDAOException {
        switch (type) {
            case DB:
                return new KnCorpDBDAOFactory();
            case XML:
                return new KnCorpXmlDAOFactory();
            default:
                throw new KnXDMSystemError(KnErrorCodes.DAO.INVALID_FACTORY_TYPE, "Invalid database type : " + type);
        }
    }
}
