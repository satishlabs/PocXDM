/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnTransactionManager.java
 * Subsystem:
 * <p/>
 * Name                 Date          Release
 * -----------------    -----------   -------
 * WebApps              29/04/14      7.9
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
package com.kodiak.dbmgr;

import com.kodiak.logger.KnLogger;

public class KnTransactionManager {
	private static final KnLogger knLogger = KnLogger.getLogger(KnTransaction.class);

	private KnTransactionManager() {

	}

	public static KnTransaction getTransaction() {
		KnTransaction trans = null;
		synchronized (KnTransactionManager.class) {
			trans = new KnTransaction();
		}
		knLogger.debug("getTransaction()", "Returning KnTransaction : ");
		return trans;
	}
}
