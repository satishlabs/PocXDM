/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpDBDAOFactory.java
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

package com.kodiak.xdms.server.corpmgmt.dao.persister.db;

import com.kodiak.xdms.server.corpmgmt.dao.ICorpDAOFactory;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpPocDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.common.dao.KnDAOException;

public class KnCorpDBDAOFactory implements ICorpDAOFactory {


    public KnCorpDBDAOFactory() {

    }

    public ICorpPocDAO createPocServerDAO(String pttServerId) throws KnDAOException {
        return new KnCorpPocDAO(pttServerId);
    }

    public ICorpXdmDAO createXdmServerDAO(String pttServerId) throws KnDAOException {
        return new KnCorpXdmDAO(pttServerId);
    }
}
