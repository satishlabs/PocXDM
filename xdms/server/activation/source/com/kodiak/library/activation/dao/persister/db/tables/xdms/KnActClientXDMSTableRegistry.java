/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnActClientLNSTablesRegistry.java
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
package com.kodiak.library.activation.dao.persister.db.tables.xdms;

public final class KnActClientXDMSTableRegistry {

    /**
     * Creates VAS Subscription Key DAO
     *
     * @param pttServerId pttServerId
     * @return return VAS subscription key dao
     */
    public KnTmpVASSubscriptionKeyInfoDAO createTmpVASSuscrptionKeyInfoDAO(String pttServerId) {
        return new KnTmpVASSubscriptionKeyInfoDAO(pttServerId);
    }
}
