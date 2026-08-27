/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/******************************************************************************
 * File name:   KnProvTablesRegistry.java
 * Subsystem:   Subscriber Management Lib
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
 * *******************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables;

import com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.ems.KnProvEMSTablesRegistry;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.poc.KnProvPOCTablesRegistry;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.presence.KnProvPresenceTablesRegistry;
import com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm.KnProvXDMTablesRegistry;

public final class KnProvTablesRegistry {

    private static final KnProvXDMTablesRegistry xdmTablesregistry = new KnProvXDMTablesRegistry();
    private static final KnProvPresenceTablesRegistry presenceTablesRegistry = new KnProvPresenceTablesRegistry();
    private static final KnProvPOCTablesRegistry pocTablesRegistry = new KnProvPOCTablesRegistry();
    private static final KnProvEMSTablesRegistry emsTablesRegistry = new KnProvEMSTablesRegistry();

    /**
     * returns instance of Prov XDM Tables Registry
     *
     * @return KnProvXDMTablesRegistry
     */
    public static KnProvXDMTablesRegistry getProvXDMTablesRegistry() {
        return xdmTablesregistry;
    }

    /**
     * returns instance of Prov Presence Tables Registry
     *
     * @return KnProvPresenceTablesRegistry
     */
    public static KnProvPresenceTablesRegistry getPresenceTablesRegistry() {
        return presenceTablesRegistry;
    }

    /**
     * returns instance of Prov POC Tables Registry
     *
     * @return KnProvPOCTablesRegistry
     */
    public static KnProvPOCTablesRegistry getProvPOCTablesRegistry() {
        return pocTablesRegistry;
    }

    /**
     * returns instance of Prov EMS Tables Registry
     *
     * @return KnProvEMSTablesRegistry
     */
    public static KnProvEMSTablesRegistry getProvEMSTablesRegistry() {
        return emsTablesRegistry;
    }
}
