/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/******************************************************************************
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
 * *******************************************************************************/

package com.kodiak.xdms.server.common.dao.persister.db.tables;

import com.kodiak.xdms.server.common.dao.persister.db.tables.ems.KnEMSTablesRegistry;
import com.kodiak.xdms.server.common.dao.persister.db.tables.poc.KnPoCTablesRegistry;
import com.kodiak.xdms.server.common.dao.persister.db.tables.presence.KnPresenceTablesRegistry;
import com.kodiak.xdms.server.common.dao.persister.db.tables.xdm.KnXDMTablesRegistry;

public final class KnTablesRegistry {
    private static final KnXDMTablesRegistry xdmTablesRegistry = new KnXDMTablesRegistry();
    private static final KnPresenceTablesRegistry presenceTablesRegistry = new KnPresenceTablesRegistry();
    private static final KnPoCTablesRegistry pocTablesRegistry = new KnPoCTablesRegistry();
    private static final KnEMSTablesRegistry emsTablesRegistry = new KnEMSTablesRegistry();

    /**
     *
     * @return
     */
    public static KnXDMTablesRegistry getXDMTablesRegistry(){
        return xdmTablesRegistry;
    }

    /**
     *
     * @return
     */
    public static KnPresenceTablesRegistry getPresenceTablesRegistry(){
        return presenceTablesRegistry;
    }

    /**
     *
     * @return
     */
    public static KnPoCTablesRegistry getPoCTablesRegistry(){
        return pocTablesRegistry;
    }

    /**
     * 
     * @return
     */
    public static KnEMSTablesRegistry getEMSTablesRegistry(){
        return emsTablesRegistry;
    }

}
