/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   KnProvXMLDAOFactory.java
 * Subsystem:   Provisioning Library
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       1/2/11   7.0
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
 * *************************************************************************
 */
package com.kodiak.xdms.server.subsmgmt.dao.persister.xml;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.xdms.server.subsmgmt.dao.IProvDAOFactory;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.KnProvXDMServerDAO;

public class KnProvXMLDAOFactory implements IProvDAOFactory {



    public IProvXDMServerDAO createProvXDMServerDAO() throws KnDAOException {
        return new KnProvXDMServerDAO(null);
    }
}
