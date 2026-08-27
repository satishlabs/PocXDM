/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnActClientTablesRegistry.java
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
package com.kodiak.library.activation.dao.persister.db.tables;

import com.kodiak.library.activation.dao.persister.db.tables.ems.KnActClientEmsTablesRegistry;
import com.kodiak.library.activation.dao.persister.db.tables.rtx.KnActClientRtxTablesRegistry;
import com.kodiak.library.activation.dao.persister.db.tables.web.KnActClientWebTablesRegistry;
import com.kodiak.library.activation.dao.persister.db.tables.xdms.KnActClientXDMSTableRegistry;

public final class KnActClientTablesRegistry {

    private static KnActClientRtxTablesRegistry rtxTableRegistry = new KnActClientRtxTablesRegistry();
    private static KnActClientXDMSTableRegistry xdmsTableRegistry = new KnActClientXDMSTableRegistry();
    private static KnActClientEmsTablesRegistry emsTablesRegistry = new KnActClientEmsTablesRegistry();
    private static KnActClientWebTablesRegistry webTablesRegistry = new KnActClientWebTablesRegistry();

    /**
     * Creates RTX tables factory.
     *
     * @return
     */
    public static KnActClientRtxTablesRegistry getRtxTableRegistry() {
        return rtxTableRegistry;
    }

    /**
     * Creates LNS tables factory.
     *
     * @return
     */
    public static KnActClientXDMSTableRegistry getXdmsTableRegistry() {
        return xdmsTableRegistry;
    }

    /**
     * Creates EMS tables factory.
     *
     * @return
     */
    public static KnActClientEmsTablesRegistry getEmsTablesRegistry() {
        return emsTablesRegistry;
    }

    /**
     * return WEB tables factory
     * @return
     */
    public static KnActClientWebTablesRegistry getWebTablesRegistry() {
        return webTablesRegistry;
    }
}
