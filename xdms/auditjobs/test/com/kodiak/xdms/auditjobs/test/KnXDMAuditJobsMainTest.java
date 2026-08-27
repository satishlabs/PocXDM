/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnXDMAuditJobsMainTest.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker P       Oct 19, 2012   7.4
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
 * ************************************************************************/
package com.kodiak.xdms.auditjobs.test;

import com.kodiak.common.loader.KnInitializer;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.auditjobs.KnXDMAuditJobs;

/**
 * @author Ravi Shanker .P
 * 
 */
public class KnXDMAuditJobsMainTest {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMAuditJobsMainTest.class);

	private static final String CLASS_NAME = KnXDMAuditJobsMainTest.class.getName();

	public static void main(String[] s) {
		KnInitializer.getInstance();

		knLogger.info( "main", "Starting the Audit Jobs ");
		KnXDMAuditJobs.createInstance();
		knLogger.info( "main", "Started the Audit Jobs");
	}

}
