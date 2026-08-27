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
import com.kodiak.xdms.server.common.KnXDMSystemError;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBDAOFactory;
import com.kodiak.xdms.server.common.dao.persister.xml.KnXmlDAOFactory;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;


public class KnFactorySelector {

    public static final int DB = 0;

    public static final int XML = 1;

    /**
     * This method will return a DAO factory implementation based
     * on the value of type.
     * @param type
     * @return the implementation
     * @throws KnDAOException
     */
    public static IDAOFactory getDAOFactory(int type)
        throws KnDAOException {
        switch (type) {
            case DB:
                return new KnDBDAOFactory();
            case XML:
                return new KnXmlDAOFactory();
            default:
                throw new KnXDMSystemError(KnErrorCodes.DAO.INVALID_FACTORY_TYPE, "Invalid database type : " + type);
        }
    }

}
