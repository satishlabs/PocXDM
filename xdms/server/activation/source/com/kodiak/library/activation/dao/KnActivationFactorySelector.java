/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnActivationFactorySelector.java
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

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.library.activation.business.KnActBOException;
import com.kodiak.library.activation.dao.persister.db.KnActClientDBDAOFactory;
import com.kodiak.library.activation.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.KnXDMSystemError;

public class KnActivationFactorySelector {
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
     * @throws KnDAOException
     *
     */
    public static IActClientDAOFactory getDAOFactory(int type)
            throws KnDAOException, KnActBOException {
        switch (type) {
            case DB:
                return new KnActClientDBDAOFactory();
            default:
                throw new KnXDMSystemError(KnErrorCodes.DAO.INVALID_FACTORY_TYPE, "Invalid database type : " + type);
        }
    }
}
