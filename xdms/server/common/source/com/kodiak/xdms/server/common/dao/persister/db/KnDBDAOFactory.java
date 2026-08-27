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
package com.kodiak.xdms.server.common.dao.persister.db;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.xdms.server.common.dao.IDAOFactory;
import com.kodiak.xdms.server.common.dao.persister.IEmsDAO;
import com.kodiak.xdms.server.common.dao.persister.IPoCServerDAO;
import com.kodiak.xdms.server.common.dao.persister.IPresenceServerDAO;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;

public class KnDBDAOFactory implements IDAOFactory {


    public IEmsDAO createEmsDAO() throws KnDAOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IXDMServerDAO createXDMServerDAO(String pttServerId) throws KnDAOException {
        return new KnDBXDMServerDAO(pttServerId);
    }

    public IPoCServerDAO createPoCServerDAO(String pttServerId) throws KnDAOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IPresenceServerDAO createPresenceServerDAO(String pttServerId) throws KnDAOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
