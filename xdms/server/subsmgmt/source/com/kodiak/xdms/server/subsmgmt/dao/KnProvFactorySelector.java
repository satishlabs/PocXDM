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
 * Ravi Shanker .P       Dec 15, 2010       7.0
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
package com.kodiak.xdms.server.subsmgmt.dao;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.xdms.server.common.KnXDMSystemError;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.KnProvDBDAOFactory;
import com.kodiak.xdms.server.subsmgmt.dao.persister.xml.KnProvXMLDAOFactory;

public class KnProvFactorySelector {

    //Factory type that denotes the Relational Data Base
    public static final int DB = 0;

    //Factory type that denotes the XML Data Base
    public static final int XML = 1;

    public static IProvDAOFactory getProvDAOFactory(int type)
            throws KnDAOException, KnProvBOException {
        switch (type) {
            case DB:
                return new KnProvDBDAOFactory();
            case XML:
                return new KnProvXMLDAOFactory();
            default:
                throw new KnXDMSystemError(KnErrorCodes.DAO.INVALID_FACTORY_TYPE, "Invalid database type : " + type);
        }
    }

}
