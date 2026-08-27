/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnProvDBDAOFactory.java
 * Subsystem:   Provisioning Library
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
package com.kodiak.xdms.server.subsmgmt.dao.persister.db;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.dao.IProvDAOFactory;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;

public class KnProvDBDAOFactory implements IProvDAOFactory {
	private static final KnLogger knLogger = KnLogger.getLogger(KnProvDBDAOFactory.class);
    private static final String className = KnProvDBDAOFactory.class.getName();

    private static boolean isInitialized = false;
    private String xdmPttServerId;

    public KnProvDBDAOFactory() throws KnProvBOException {
//        if (!isInitialized) {
//            isInitialized = true;
            KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
            try {
                this.xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error( "constructor", "failed to retrieve xdm Ptt Sever Id");
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }
//        }
    }

    public IProvXDMServerDAO createProvXDMServerDAO() throws KnDAOException {
        knLogger.debug( "createProvXDMServerDAO", "xdm Ptt Server Id - " ,xdmPttServerId);
        return new KnProvXDMServerDAO(xdmPttServerId);
    }

}
